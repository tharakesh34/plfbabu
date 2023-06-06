package com.pennanttech.external.gst.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.gst.dao.ExtGSTDao;
import com.pennanttech.external.gst.model.GSTCompDetail;
import com.pennanttech.external.gst.model.GSTCompHeader;
import com.pennanttech.external.gst.model.GSTInvoiceDetail;
import com.pennanttech.external.gst.model.GSTRespDetail;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileProcessGSTRespJob extends AbstractJob
		implements ExtIntfConfigConstants, InterfaceConstants, ErrorCodesConstants {
	private static final Logger logger = LogManager.getLogger(FileProcessGSTRespJob.class);
	private static final String FETCH_GSTCOMPHEADER_QUERY = "Select * from GSTCOMPHEADER  Where STATUS = ?  AND EXTRACTION= ?";
	private static final String FETCH_GSTCOMPDETAILS_QUERY = "Select * from GSTCOMPDETAILS  Where STATUS = ? AND HEADER_ID = ?";
	private DataSource dataSource;
	private ExtGSTDao extGSTDao;
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		applicationContext = ApplicationContextProvider.getApplicationContext();
		extGSTDao = applicationContext.getBean(ExtGSTDao.class);
		dataSource = applicationContext.getBean("extDataSource", DataSource.class);

		// Read 10 files at a time using file status = 0
		JdbcCursorItemReader<GSTCompHeader> cursorItemReader = new JdbcCursorItemReader<GSTCompHeader>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(1);
		cursorItemReader.setSql(FETCH_GSTCOMPHEADER_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<GSTCompHeader>() {
			@Override
			public GSTCompHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
				GSTCompHeader headerFile = new GSTCompHeader();
				headerFile.setId(rs.getLong("ID"));
				headerFile.setStatus(rs.getInt("STATUS"));
				headerFile.setExtraction(rs.getInt("EXTRACTION"));
				headerFile.setFileName(rs.getString("FILE_NAME"));
				headerFile.setFileLocation(rs.getString("FILE_LOCATION"));
				headerFile.setCreatedDate(rs.getDate("CREATED_DATE"));
				return headerFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED);// STATUS = ?
				ps.setLong(2, COMPLETED);// Extraction=?
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		GSTCompHeader header;
		try {
			while ((header = cursorItemReader.read()) != null) {
				try {
					// update the file status as processing
					extGSTDao.updateFileStatus(header.getId(), INPROCESS);

					// Process records in the file
					processFileRecords(header);

					// Update file status as processed
					extGSTDao.updateFileStatus(header.getId(), COMPLETED);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					extGSTDao.updateFileStatus(header.getId(), EXCEPTION);
				}
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void processFileRecords(GSTCompHeader header) {

		logger.debug(Literal.ENTERING);

		// Fetch 100 records at a time
		JdbcCursorItemReader<GSTCompDetail> dataCursorReader = new JdbcCursorItemReader<GSTCompDetail>();
		dataCursorReader.setDataSource(dataSource);
		dataCursorReader.setFetchSize(100);
		dataCursorReader.setSql(FETCH_GSTCOMPDETAILS_QUERY);
		dataCursorReader.setRowMapper(new RowMapper<GSTCompDetail>() {
			@Override
			public GSTCompDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
				GSTCompDetail detail = new GSTCompDetail();
				detail.setId(rs.getLong("ID"));
				detail.setHeaderId(rs.getLong("HEADER_ID"));
				detail.setStatus(rs.getInt("STATUS"));
				detail.setRecord(rs.getString("RECORD_DATA"));
				return detail;
			}
		});

		dataCursorReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED); // STATUS = ?
				ps.setLong(2, header.getId()); // HEADER_ID = ?
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		dataCursorReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		GSTCompDetail detail;

		try {

			while ((detail = dataCursorReader.read()) != null) {
				try {

					GSTRespDetail responseBean = null;

					// Get extPresentment object from record data
					responseBean = convertRecordToBean(detail);

					if (responseBean != null) {
						// FIXME process in PLF

						// Fetch GST VOUCHER from GST_VOUCHER_DETAILS based on ID, If not found error.
						boolean isGstVoucherFound = extGSTDao.isVoucherFound(responseBean.getTransactionUID());
						if (!isGstVoucherFound) {
							// save record with error mentioning as GST voucher not found in PLF
							detail.setStatus(FAILED);
							detail.setErrorCode(F405);
							detail.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(F405));
							extGSTDao.updateGSTRecordDetailStatus(detail);
							continue;
						}

						// Save the Invoice details into the table
						GSTInvoiceDetail invoiceDetail = getInvoiceDetail(responseBean);
						extGSTDao.saveGSTInvoiceDetails(invoiceDetail);

						// Update GST VOUCHER ID in Response detail record for successful transaction
						detail.setGstVoucherId(responseBean.getTransactionUID());
					}

					detail.setStatus(COMPLETED);
					extGSTDao.updateGSTRecordDetailStatus(detail);

					// if (gstRespBeanList.size() == BULK_RECORD_COUNT) {
					//
					// }
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					detail.setStatus(EXCEPTION);
					detail.setErrorCode(F702);
					detail.setErrorMessage(e.getMessage());
				}

			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (dataCursorReader != null) {
				dataCursorReader.close();
			}
		}
		logger.debug(Literal.LEAVING);

	}

	private GSTInvoiceDetail getInvoiceDetail(GSTRespDetail respBean) {
		GSTInvoiceDetail detail = new GSTInvoiceDetail();
		detail.setCustomerName(respBean.getCustomerName());
		detail.setCustomerAddress("");// FIXME Call db for customer address
		detail.setCurrentGstin(respBean.getGstin());
		detail.setLoanBranchAddress("");// FIXME Get from DB
		detail.setGstin(respBean.getGstinOfBank());
		detail.setTransactionDate(respBean.getTransactionDate());
		detail.setInvoiceNumber(respBean.getGstInvoiceNumber());
		detail.setChargeDescription("");// FIXME Charge desc for fee. Get from DB
		detail.setChargeAmount(respBean.getTotalInvoiceValue());
		detail.setCgstRate(respBean.getCgstRate());
		detail.setCgstAmount(respBean.getCgstAmount());
		detail.setSgstRate(respBean.getSgstRate());
		detail.setSgstAmount(respBean.getSgstAmount());
		detail.setIgstRate(respBean.getIgstRate());
		detail.setIgstAmount(respBean.getIgstAmount());
		detail.setUgstRate(respBean.getUtgstRate());
		detail.setUgstAmount(respBean.getUtgstAmount());
		detail.setPop("");// FIXME Source State Place of Purchase
		detail.setPos("");// FIXME Destination State Place of Service
		detail.setCin("L65920MH1994PC08618");// HardCoded
		detail.setPan("AAACH27020H");// HardCoded
		detail.setSac(respBean.getSac());
		detail.setWebsiteAddress("www.hdfcbank.com");// HardCoded
		detail.setEmailId("loansupport@hdfcbank.com");// HardCoded
		detail.setRegBankAddress("");// FIXME Registered address of FI/Bank
		detail.setDisclaimer("");// HardCoded
		return detail;
	}

	private GSTRespDetail convertRecordToBean(GSTCompDetail detail) {
		GSTRespDetail responseBean = new GSTRespDetail();
		String[] lineDataStrings = detail.getRecord().split("\\|");

		if (lineDataStrings == null || lineDataStrings.length == 0) {
			detail.setErrorCode(F703);
			detail.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(F703));
			detail.setStatus(FAILED);
			return null;
		}

		responseBean.setRequestType(TextFileUtil.getItem(lineDataStrings, 1));
		responseBean.setCustomerId(TextFileUtil.getItem(lineDataStrings, 2));
		responseBean.setAccountId(TextFileUtil.getItem(lineDataStrings, 3));
		responseBean.setGstin(TextFileUtil.getItem(lineDataStrings, 4));
		responseBean.setServiceCode(TextFileUtil.getItem(lineDataStrings, 5));
		responseBean.setSac(TextFileUtil.getItem(lineDataStrings, 6));
		responseBean.setTransactionCode(TextFileUtil.getItem(lineDataStrings, 7));
		responseBean.setTransactionVolume(TextFileUtil.getLongItem(lineDataStrings, 8));
		responseBean.setTransactionValue(TextFileUtil.getBigDecimalItem(lineDataStrings, 9));
		responseBean.setTransactionPricedCharge(TextFileUtil.getBigDecimalItem(lineDataStrings, 10));
		responseBean.setChargeInclusiveOfTax(TextFileUtil.getItem(lineDataStrings, 11));
		responseBean.setTaxAmount(TextFileUtil.getBigDecimalItem(lineDataStrings, 12));
		responseBean.setTransactionDate(TextFileUtil.getDateItem(lineDataStrings, 13));
		responseBean.setTransactionUID(TextFileUtil.getLongItem(lineDataStrings, 14));
		responseBean.setSourceBranch(TextFileUtil.getItem(lineDataStrings, 15));
		responseBean.setSourceState(TextFileUtil.getItem(lineDataStrings, 16));
		responseBean.setDestinationBranch(TextFileUtil.getItem(lineDataStrings, 17));
		responseBean.setDestinationState(TextFileUtil.getItem(lineDataStrings, 18));
		responseBean.setCurrencyCode(TextFileUtil.getItem(lineDataStrings, 19));
		responseBean.setChannel(TextFileUtil.getItem(lineDataStrings, 20));
		responseBean.setSourceSystem(TextFileUtil.getItem(lineDataStrings, 21));
		responseBean.setCustomerExempt(TextFileUtil.getItem(lineDataStrings, 22));
		responseBean.setAccountExempt(TextFileUtil.getItem(lineDataStrings, 23));
		responseBean.setBranchExempt(TextFileUtil.getItem(lineDataStrings, 24));
		responseBean.setServiceChargeExempt(TextFileUtil.getItem(lineDataStrings, 25));
		responseBean.setTransactionExempt(TextFileUtil.getItem(lineDataStrings, 26));
		responseBean.setRelatedEntity(TextFileUtil.getItem(lineDataStrings, 27));
		responseBean.setStandardFee(TextFileUtil.getBigDecimalItem(lineDataStrings, 28));
		responseBean.setReversalTransactionFlag(TextFileUtil.getItem(lineDataStrings, 29));
		responseBean.setOriginalTransactionId(TextFileUtil.getItem(lineDataStrings, 30));
		responseBean.setUserCField1(TextFileUtil.getItem(lineDataStrings, 31));
		responseBean.setUserCField2(TextFileUtil.getItem(lineDataStrings, 32));
		responseBean.setUserCField3(TextFileUtil.getItem(lineDataStrings, 33));
		responseBean.setUserCField4(TextFileUtil.getItem(lineDataStrings, 34));
		responseBean.setUserCField5(TextFileUtil.getItem(lineDataStrings, 35));
		responseBean.setUserNField1(TextFileUtil.getLongItem(lineDataStrings, 36));
		responseBean.setUserNField2(TextFileUtil.getLongItem(lineDataStrings, 37));
		responseBean.setUserDField1(TextFileUtil.getItem(lineDataStrings, 38));
		responseBean.setUserDField2(TextFileUtil.getItem(lineDataStrings, 39));
		responseBean.setActualUserId(TextFileUtil.getItem(lineDataStrings, 40));
		responseBean.setUserDepartment(TextFileUtil.getItem(lineDataStrings, 41));
		responseBean.setRequestDate(TextFileUtil.getItem(lineDataStrings, 42));
		responseBean.setSuccessStatus(TextFileUtil.getItem(lineDataStrings, 43));
		responseBean.setFailureReason(TextFileUtil.getItem(lineDataStrings, 44));
		responseBean.setGstExempted(TextFileUtil.getItem(lineDataStrings, 45));
		responseBean.setGstExemptReason(TextFileUtil.getItem(lineDataStrings, 46));
		responseBean.setGstTaxAmount(TextFileUtil.getBigDecimalItem(lineDataStrings, 47));
		responseBean.setIgstAccountNumber(TextFileUtil.getItem(lineDataStrings, 48));
		responseBean.setCgstAccountNumber(TextFileUtil.getItem(lineDataStrings, 49));
		responseBean.setSgstAccountNumber(TextFileUtil.getItem(lineDataStrings, 50));
		responseBean.setUtgstAccountNumber(TextFileUtil.getItem(lineDataStrings, 51));
		responseBean.setIgstAmount(TextFileUtil.getBigDecimalItem(lineDataStrings, 52));
		responseBean.setCgstAmount(TextFileUtil.getBigDecimalItem(lineDataStrings, 53));
		responseBean.setSgstAmount(TextFileUtil.getBigDecimalItem(lineDataStrings, 54));
		responseBean.setUtgstAmount(TextFileUtil.getBigDecimalItem(lineDataStrings, 55));
		responseBean.setGstInvoiceNumber(TextFileUtil.getItem(lineDataStrings, 56));
		responseBean.setGstInvoiceDate(TextFileUtil.getItem(lineDataStrings, 57));
		responseBean.setCgstSgstUtgstState(TextFileUtil.getItem(lineDataStrings, 58));
		responseBean.setGlAccountId(TextFileUtil.getItem(lineDataStrings, 59));
		responseBean.setCgstCess1(TextFileUtil.getBigDecimalItem(lineDataStrings, 60));
		responseBean.setCgstCess2(TextFileUtil.getBigDecimalItem(lineDataStrings, 61));
		responseBean.setCgstCess3(TextFileUtil.getBigDecimalItem(lineDataStrings, 62));
		responseBean.setSgstCess1(TextFileUtil.getBigDecimalItem(lineDataStrings, 63));
		responseBean.setSgstCess2(TextFileUtil.getBigDecimalItem(lineDataStrings, 64));
		responseBean.setSgstCess3(TextFileUtil.getBigDecimalItem(lineDataStrings, 65));
		responseBean.setIgstCess1(TextFileUtil.getBigDecimalItem(lineDataStrings, 66));
		responseBean.setIgstCess2(TextFileUtil.getBigDecimalItem(lineDataStrings, 67));
		responseBean.setIgstCess3(TextFileUtil.getBigDecimalItem(lineDataStrings, 68));
		responseBean.setUtgstCess1(TextFileUtil.getBigDecimalItem(lineDataStrings, 69));
		responseBean.setUtgstCess2(TextFileUtil.getBigDecimalItem(lineDataStrings, 70));
		responseBean.setUtgstCess3(TextFileUtil.getBigDecimalItem(lineDataStrings, 71));
		responseBean.setGstRate(TextFileUtil.getBigDecimalItem(lineDataStrings, 72));
		responseBean.setCgstRate(TextFileUtil.getBigDecimalItem(lineDataStrings, 73));
		responseBean.setSgstRate(TextFileUtil.getBigDecimalItem(lineDataStrings, 74));
		responseBean.setIgstRate(TextFileUtil.getBigDecimalItem(lineDataStrings, 75));
		responseBean.setUtgstRate(TextFileUtil.getBigDecimalItem(lineDataStrings, 76));
		responseBean.setSystemCField1(TextFileUtil.getItem(lineDataStrings, 77));
		responseBean.setSystemCField2(TextFileUtil.getItem(lineDataStrings, 78));
		responseBean.setSystemCField3(TextFileUtil.getItem(lineDataStrings, 79));
		responseBean.setSystemCField4(TextFileUtil.getItem(lineDataStrings, 80));
		responseBean.setSystemCField5(TextFileUtil.getItem(lineDataStrings, 81));
		responseBean.setSystemNField1(TextFileUtil.getLongItem(lineDataStrings, 82));
		responseBean.setSystemNField2(TextFileUtil.getLongItem(lineDataStrings, 83));
		responseBean.setSystemDField1(TextFileUtil.getItem(lineDataStrings, 84));
		responseBean.setSystemDField2(TextFileUtil.getItem(lineDataStrings, 85));
		responseBean.setTransactionType(TextFileUtil.getItem(lineDataStrings, 86));
		responseBean.setInvoiceType(TextFileUtil.getItem(lineDataStrings, 87));
		responseBean.setOrginalInvoiceNumber(TextFileUtil.getItem(lineDataStrings, 88));
		responseBean.setOriginalInvoiceDate(TextFileUtil.getItem(lineDataStrings, 89));
		responseBean.setGstinOfBank(TextFileUtil.getItem(lineDataStrings, 90));
		responseBean.setCustomerName(TextFileUtil.getItem(lineDataStrings, 91));
		responseBean.setBankName(TextFileUtil.getItem(lineDataStrings, 92));
		responseBean.setSacDescription(TextFileUtil.getItem(lineDataStrings, 93));
		responseBean.setTransactionIndicator(TextFileUtil.getItem(lineDataStrings, 94));
		responseBean.setGlBranch(TextFileUtil.getItem(lineDataStrings, 95));
		responseBean.setTotalInvoiceValue(TextFileUtil.getBigDecimalItem(lineDataStrings, 96));
		responseBean.setBusinessUnit(TextFileUtil.getItem(lineDataStrings, 97));
		responseBean.setGstRField1(TextFileUtil.getItem(lineDataStrings, 98));
		responseBean.setGstRField2(TextFileUtil.getItem(lineDataStrings, 99));
		responseBean.setGstRField3(TextFileUtil.getItem(lineDataStrings, 100));
		responseBean.setGstRField4(TextFileUtil.getItem(lineDataStrings, 101));
		responseBean.setGstRField5(TextFileUtil.getItem(lineDataStrings, 102));
		responseBean.setGstRField6(TextFileUtil.getItem(lineDataStrings, 103));
		responseBean.setGstRField7(TextFileUtil.getItem(lineDataStrings, 104));
		responseBean.setGstRField8(TextFileUtil.getItem(lineDataStrings, 105));
		responseBean.setGstRField9(TextFileUtil.getItem(lineDataStrings, 106));
		responseBean.setGstRField10(TextFileUtil.getItem(lineDataStrings, 107));
		responseBean.setGstRField11(TextFileUtil.getItem(lineDataStrings, 108));
		responseBean.setGstRField12(TextFileUtil.getItem(lineDataStrings, 109));
		responseBean.setGstRField13(TextFileUtil.getDateItem(lineDataStrings, 110));
		responseBean.setGstRField14(TextFileUtil.getItem(lineDataStrings, 111));
		responseBean.setGstRField15(TextFileUtil.getItem(lineDataStrings, 112));
		responseBean.setUserCField6(TextFileUtil.getItem(lineDataStrings, 113));
		responseBean.setUserCField7(TextFileUtil.getItem(lineDataStrings, 114));
		responseBean.setUserCField8(TextFileUtil.getItem(lineDataStrings, 115));
		responseBean.setUserCField9(TextFileUtil.getItem(lineDataStrings, 116));
		responseBean.setUserCField10(TextFileUtil.getItem(lineDataStrings, 117));
		responseBean.setUserCField11(TextFileUtil.getItem(lineDataStrings, 118));
		responseBean.setUserCField12(TextFileUtil.getItem(lineDataStrings, 119));
		responseBean.setUserCField13(TextFileUtil.getItem(lineDataStrings, 120));
		responseBean.setUserCField14(TextFileUtil.getItem(lineDataStrings, 121));
		responseBean.setUserCField15(TextFileUtil.getItem(lineDataStrings, 122));
		responseBean.setUserNField3(TextFileUtil.getLongItem(lineDataStrings, 123));
		responseBean.setUserNField4(TextFileUtil.getLongItem(lineDataStrings, 124));
		responseBean.setUserDField3(TextFileUtil.getDateItem(lineDataStrings, 125));
		responseBean.setUserDField4(TextFileUtil.getDateItem(lineDataStrings, 126));
		responseBean.setTaxRoundingDifference(TextFileUtil.getBigDecimalItem(lineDataStrings, 127));
		return responseBean;
	}
}
