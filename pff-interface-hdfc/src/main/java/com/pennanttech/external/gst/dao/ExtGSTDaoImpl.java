package com.pennanttech.external.gst.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.finance.Taxes;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.gst.model.GSTCompDetail;
import com.pennanttech.external.gst.model.GSTCompHeader;
import com.pennanttech.external.gst.model.GSTInvoiceDetail;
import com.pennanttech.external.gst.model.GSTReqFile;
import com.pennanttech.external.gst.model.GSTRequestDetail;
import com.pennanttech.external.gst.model.GSTVoucherDetails;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ExtGSTDaoImpl extends SequenceDao<Object> implements ExtGSTDao, InterfaceConstants {

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;
	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;

	public ExtGSTDaoImpl() {
		super();
	}

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(extNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	private List<GSTVoucherDetails> getVoucherDetailsFromFinFee() {
		String sql = " select  frh.reference FINREFERENCE,'FEE' AMOUNT_TYPE,ffr.feeid REFERENCE_FIELD1, "
				+ "   ffr.receiptid  REFERENCE_FIELD2,ffr.paidamount REFERENCE_AMOUNT,frh.receiptamount ACTUAL_AMOUNT,ffd.TAXHEADERID  from finfeereceipts ffr  "
				+ "   inner join finfeedetail ffd on ffr.FEEID=ffd.FEEID  "
				+ "   inner join finreceiptheader frh on ffr.receiptid=frh.receiptid "
				+ "   where ffr.paidamount >0 and ffd.TAXAPPLICABLE=1 ";

		logger.debug(Literal.SQL, sql);

		List<GSTVoucherDetails> vouchersList = new ArrayList<>();

		mainNamedJdbcTemplate.getJdbcOperations().query(sql, rs -> {
			GSTVoucherDetails gvd = new GSTVoucherDetails();
			gvd.setFinreference(rs.getString("FINREFERENCE"));
			gvd.setAmountType(rs.getString("AMOUNT_TYPE"));
			gvd.setActualAmount(rs.getBigDecimal("ACTUAL_AMOUNT"));
			gvd.setReferenceAmount(rs.getBigDecimal("REFERENCE_AMOUNT"));
			gvd.setReferenceField1(rs.getLong("REFERENCE_FIELD1"));
			gvd.setReferenceField2(rs.getLong("REFERENCE_FIELD2"));
			gvd.setTaxHeaderId(rs.getLong("TAXHEADERID"));
			vouchersList.add(gvd);
		});

		return vouchersList;
	}

	private List<GSTVoucherDetails> getVoucherDetailsFromManualAdvice() {
		String sql = "select mad.FINREFERENCE FINREFERENCE,'ADVISE' AMOUNT_TYPE, MOVEMENTID REFERENCE_FIELD1, "
				+ "   madm.RECEIPTID REFERENCE_FIELD2,madm.PAIDAMOUNT REFERENCE_AMOUNT,frh.receiptamount ACTUAL_AMOUNT , madm.TAXHEADERID "
				+ "   from manualadvisemovements madm "
				+ "   inner join manualadvise mad on madm.ADVISEID=mad.ADVISEID  "
				+ "   inner join finreceiptheader frh on madm.RECEIPTID=frh.receiptid";

		logger.debug(Literal.SQL, sql);

		List<GSTVoucherDetails> vouchersList = new ArrayList<>();

		mainNamedJdbcTemplate.getJdbcOperations().query(sql, rs -> {
			GSTVoucherDetails gvd = new GSTVoucherDetails();
			gvd.setFinreference(rs.getString("FINREFERENCE"));
			gvd.setAmountType(rs.getString("AMOUNT_TYPE"));
			gvd.setActualAmount(rs.getBigDecimal("ACTUAL_AMOUNT"));
			gvd.setReferenceAmount(rs.getBigDecimal("REFERENCE_AMOUNT"));
			gvd.setReferenceField1(rs.getLong("REFERENCE_FIELD1"));
			gvd.setReferenceField2(rs.getLong("REFERENCE_FIELD2"));
			gvd.setTaxHeaderId(rs.getLong("TAXHEADERID"));
			vouchersList.add(gvd);
		});

		return vouchersList;
	}

	private void saveGSTVouchers(List<GSTVoucherDetails> vouchersList) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());

		String sql = " INSERT INTO GST_VOUCHER_DETAILS (FINREFERENCE,AMOUNT_TYPE,REFERENCE_FIELD1, REFERENCE_FIELD2,"
				+ "     REFERENCE_AMOUNT,ACTUAL_AMOUNT,TAXHEADERID,CREATED_DATE)" + "  SELECT ?,?,?,?,?,?,?,?"
				+ "  FROM dual"
				+ "  WHERE NOT EXISTS (SELECT * FROM GST_VOUCHER_DETAILS gvd WHERE gvd.REFERENCE_FIELD1 = ? AND gvd.REFERENCE_FIELD2 = ?)";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				GSTVoucherDetails details = vouchersList.get(index);
				int indx = 1;
				ps.setString(indx++, details.getFinreference());
				ps.setString(indx++, details.getAmountType());
				ps.setLong(indx++, details.getReferenceField1());
				ps.setLong(indx++, details.getReferenceField2());
				ps.setBigDecimal(indx++, details.getReferenceAmount());
				ps.setBigDecimal(indx++, details.getActualAmount());
				ps.setLong(indx++, details.getTaxHeaderId());
				ps.setTimestamp(indx++, curTimeStamp);
				ps.setLong(indx++, details.getReferenceField1());
				ps.setLong(indx, details.getReferenceField2());
			}

			@Override
			public int getBatchSize() {
				return vouchersList.size();
			}
		});
	}

	@Override
	public void extractDetailsFromForGstCalculation() {
		logger.debug(Literal.ENTERING);
		saveGSTVouchers(getVoucherDetailsFromFinFee());
		saveGSTVouchers(getVoucherDetailsFromManualAdvice());
		logger.debug(Literal.LEAVING);
	}

	private List<GSTRequestDetail> fetchGstVouchersDetailsForRequest(long headerId) {
		List<GSTRequestDetail> requestDetails = new ArrayList<GSTRequestDetail>();
		try {
			String sql = " SELECT 'P' AS REQUESTTYPE,GVD.FINREFERENCE ACCOUNTID,'' GSTIN,'997113' HSN, "
					+ " GVD.REFERENCE_AMOUNT TRANSACTIONPRICEDCHARGE,'Y' CHARGEINCLUSIVEOFTAX,GVD.GST_VOUCHER_ID TRANSACTIONUID,"
					+ " 'PLF' SOURCESYSTEM,'N' ACCOUNTEXEMPT,'N' BRANCHEXEMPT,'N' SERVICECHARGEEXEMPT,'N' TRANSACTIONEXEMPT,"
					+ " 'N' RELATEDENTITY,'N' TAXSPLITINSOURCESYSTEM,'LEA' USERCFIELD2,'N' CESSAPPLICABLE FROM  GST_VOUCHER_DETAILS GVD "
					+ " WHERE GVD.PROCESSED =0 AND GVD.REQ_FILE_ID = ?";

			extNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
				ps.setLong(1, headerId);
			}, rs -> {
				GSTRequestDetail grd = new GSTRequestDetail();
				grd.setRequestType(rs.getString("REQUESTTYPE"));
				grd.setAccountId(rs.getString("ACCOUNTID"));
				grd.setGstin(rs.getString("GSTIN"));
				grd.setHsn(rs.getString("HSN"));
				grd.setTransactionPricedCharge(rs.getBigDecimal("TRANSACTIONPRICEDCHARGE"));
				grd.setChargeInclusiveOfTax(rs.getString("CHARGEINCLUSIVEOFTAX"));
				grd.setTransactionUid(rs.getLong("TRANSACTIONUID"));
				grd.setSourceSystem(rs.getString("SOURCESYSTEM"));
				grd.setAccountExempt(rs.getString("ACCOUNTEXEMPT"));
				grd.setBranchExempt(rs.getString("BRANCHEXEMPT"));
				grd.setServiceChargeExempt(rs.getString("SERVICECHARGEEXEMPT"));
				grd.setTransactionExempt(rs.getString("TRANSACTIONEXEMPT"));
				grd.setRelatedEntity(rs.getString("RELATEDENTITY"));
				grd.setTaxSplitInsourceSystem(rs.getString("TAXSPLITINSOURCESYSTEM"));
				grd.setUsercField2(rs.getString("USERCFIELD2"));
				grd.setCessApplicable(rs.getString("CESSAPPLICABLE"));
				grd = fetchLoanAndCustDetails(grd);
				requestDetails.add(grd);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return requestDetails;
	}

	private GSTRequestDetail fetchLoanAndCustDetails(GSTRequestDetail requestDetail) {

		String sql = "SELECT FM.CUSTID CUSTOMERID,CU.CUSTSHRTNAME CUSTOMERNAME FROM FINANCEMAIN FM  "
				+ " INNER JOIN CUSTOMERS CU ON FM.CUSTID = CU.CUSTID  WHERE FM.FINREFERENCE =?";

		logger.debug(Literal.SQL, sql);

		return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
			requestDetail.setCustomerId(rs.getString("CUSTOMERID"));
			requestDetail.setCustomerName(rs.getString("CUSTOMERNAME"));
			return requestDetail;
		}, requestDetail.getAccountId());
	}

	@Override
	public long saveExtractedDetailsToRequestTable(long headerId) {

		List<GSTRequestDetail> requestDetails = fetchGstVouchersDetailsForRequest(headerId);

		String sql = "";
		sql = "INSERT INTO GST_REQUEST_DETAIL (REQUESTTYPE,CUSTOMERID,ACCOUNTID,GSTIN,HSN,"
				+ " TRANSACTIONPRICEDCHARGE,CHARGEINCLUSIVEOFTAX,TRANSACTIONDATE,TRANSACTIONUID,"
				+ " SOURCESYSTEM,ACCOUNTEXEMPT,BRANCHEXEMPT,SERVICECHARGEEXEMPT,TRANSACTIONEXEMPT,"
				+ " RELATEDENTITY,TAXSPLITINSOURCESYSTEM,USERCFIELD2,REQUESTDATETIME,CUSTOMERNAME,CESSAPPLICABLE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL, sql);

		return extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				GSTRequestDetail item = requestDetails.get(index);
				int indx = 1;
				ps.setString(indx++, item.getRequestType());
				ps.setString(indx++, item.getCustomerId());
				ps.setString(indx++, item.getAccountId());
				ps.setString(indx++, item.getGstin());
				ps.setString(indx++, item.getHsn());
				ps.setBigDecimal(indx++, item.getTransactionPricedCharge());
				ps.setString(indx++, item.getChargeInclusiveOfTax());
				ps.setDate(indx++, JdbcUtil.getDate(item.getTransactionDate()));
				ps.setLong(indx++, item.getTransactionUid());
				ps.setString(indx++, item.getSourceSystem());
				ps.setString(indx++, item.getAccountExempt());
				ps.setString(indx++, item.getBranchExempt());
				ps.setString(indx++, item.getServiceChargeExempt());
				ps.setString(indx++, item.getTransactionExempt());
				ps.setString(indx++, item.getRelatedEntity());
				ps.setString(indx++, item.getTaxSplitInsourceSystem());
				ps.setString(indx++, item.getUsercField2());
				ps.setDate(indx++, JdbcUtil.getDate(item.getRequestDate()));
				ps.setString(indx++, item.getCustomerName());
				ps.setString(indx, item.getCessApplicable());
			}

			@Override
			public int getBatchSize() {
				return requestDetails.size();
			}
		}).length;
	}

	@Override
	public List<GSTRequestDetail> fetchRecords() {
		logger.debug(Literal.ENTERING);

		List<GSTRequestDetail> gstRequestList = new ArrayList<>();

		String query = " SELECT * FROM GST_REQUEST_DETAIL WHERE STATUS = ? ";

		logger.debug(Literal.SQL, query);

		extNamedJdbcTemplate.getJdbcOperations().query(query, ps -> {
			ps.setInt(1, UNPROCESSED);
		}, rs -> {
			GSTRequestDetail detail = new GSTRequestDetail();
			detail.setRequestType(rs.getString("REQUESTTYPE"));
			detail.setCustomerId(rs.getString("CUSTOMERID"));
			detail.setAccountId(rs.getString("ACCOUNTID"));
			detail.setGstin(rs.getString("GSTIN"));
			detail.setHsn(rs.getString("HSN"));
			detail.setTransactionPricedCharge(rs.getBigDecimal("TRANSACTIONPRICEDCHARGE"));
			detail.setChargeInclusiveOfTax(rs.getString("CHARGEINCLUSIVEOFTAX"));
			detail.setTransactionDate(rs.getDate("TRANSACTIONDATE"));
			detail.setTransactionUid(rs.getLong("TRANSACTIONUID"));
			detail.setSourceSystem(rs.getString("SOURCESYSTEM"));
			detail.setAccountExempt(rs.getString("ACCOUNTEXEMPT"));
			detail.setBranchExempt(rs.getString("BRANCHEXEMPT"));
			detail.setServiceChargeExempt(rs.getString("SERVICECHARGEEXEMPT"));
			detail.setTransactionExempt(rs.getString("TRANSACTIONEXEMPT"));
			detail.setRelatedEntity(rs.getString("RELATEDENTITY"));
			detail.setTaxSplitInsourceSystem(rs.getString("TAXSPLITINSOURCESYSTEM"));
			detail.setUsercField2(rs.getString("USERCFIELD2"));
			detail.setRequestDate(rs.getDate("REQUESTDATETIME"));
			detail.setCustomerName(rs.getString("CUSTOMERNAME"));
			detail.setCessApplicable(rs.getString("CESSAPPLICABLE"));
			gstRequestList.add(detail);
		});
		logger.debug(Literal.LEAVING);

		return gstRequestList;
	}

	@Override
	public boolean isFileProcessed(String respFileName) {
		logger.debug(Literal.ENTERING);
		String sql = "Select count(1) from GSTRESPHEADER Where FILE_NAME= ?";
		logger.debug(Literal.SQL, sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, respFileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void saveResponseFile(GSTCompHeader compHeader) {
		logger.info(Literal.ENTERING);
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		String sql = "INSERT INTO GSTRESPHEADER (FILE_NAME,FILE_LOCATION,STATUS,EXTRACTION,CREATED_DATE,ERROR_CODE,ERROR_MESSAGE) VALUES (?, ?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, compHeader.getFileName());
			ps.setString(index++, compHeader.getFileLocation());
			ps.setLong(index++, compHeader.getStatus());
			ps.setLong(index++, compHeader.getExtraction());
			ps.setTimestamp(index++, curTimeStamp);
			ps.setString(index++, compHeader.getErrorCode());
			ps.setString(index, compHeader.getErrorMessage());

		});

	}

	@Override
	public void updateFileStatus(GSTCompHeader header) {
		String sql = "UPDATE GSTRESPHEADER SET STATUS = ?,EXTRACTION=?,ERROR_CODE=?,ERROR_MESSAGE=? WHERE ID= ?";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, header.getStatus());
			ps.setLong(index++, header.getExtraction());
			ps.setString(index++, header.getErrorCode());
			ps.setString(index++, header.getErrorMessage());
			ps.setLong(index, header.getId());
		});

	}

	@Override
	public int saveExtGSTCompRecordsData(List<GSTCompDetail> compDetails) {

		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		String sql = "INSERT INTO GSTRESPDETAILS ( HEADER_ID, RECORD_DATA, STATUS, CREATED_DATE) values(?,?,?,?)";

		logger.debug(Literal.SQL, sql);

		return extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				GSTCompDetail item = compDetails.get(index);
				ps.setLong(1, item.getHeaderId());
				ps.setString(2, item.getRecord());
				ps.setLong(3, item.getStatus());
				ps.setTimestamp(4, curTimeStamp);
			}

			@Override
			public int getBatchSize() {
				return compDetails.size();
			}
		}).length;

	}

	@Override
	public void updateGSTRecordDetailStatus(GSTCompDetail detail) {
		String sql = "UPDATE GSTRESPDETAILS  SET STATUS = ?,GST_VOUCHER_ID=?, ERROR_CODE = ?, ERROR_MESSAGE = ? WHERE ID= ? ";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, detail.getStatus());
			ps.setLong(index++, detail.getGstVoucherId());
			ps.setString(index++, detail.getErrorCode());
			ps.setString(index++, detail.getErrorMessage());
			ps.setLong(index, detail.getId());
		});

	}

	@Override
	public void updateGSTRequestFileToHeaderId(GSTReqFile gstReqFile) {
		String sql = "UPDATE GST_REQUEST_FILE SET FILE_NAME = ? WHERE ID = ?";

		logger.debug(Literal.SQL, sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, gstReqFile.getFileName());
			ps.setLong(index, gstReqFile.getId());
		});
	}

	@Override
	public GSTVoucherDetails fetchVoucherDetails(long transactionUID) {
		logger.debug(Literal.ENTERING);
		String sql = "Select GST_VOUCHER_ID,FINREFERENCE,AMOUNT_TYPE,ACTUAL_AMOUNT,"
				+ "REFERENCE_AMOUNT,REFERENCE_FIELD1,REFERENCE_FIELD2,TAXHEADERID "
				+ " from GST_VOUCHER_DETAILS Where GST_VOUCHER_ID= ?";

		logger.debug(Literal.SQL, sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				GSTVoucherDetails gvd = new GSTVoucherDetails();

				gvd.setGstVoucherId(rs.getLong("GST_VOUCHER_ID"));
				gvd.setFinreference(rs.getString("FINREFERENCE"));
				gvd.setAmountType(rs.getString("AMOUNT_TYPE"));
				gvd.setActualAmount(rs.getBigDecimal("ACTUAL_AMOUNT"));
				gvd.setReferenceAmount(rs.getBigDecimal("REFERENCE_AMOUNT"));
				gvd.setReferenceField1(rs.getLong("REFERENCE_FIELD1"));
				gvd.setReferenceField2(rs.getLong("REFERENCE_FIELD2"));
				gvd.setTaxHeaderId(rs.getLong("TAXHEADERID"));

				return gvd;
			}, transactionUID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long saveGSTInvoiceDetails(GSTInvoiceDetail gstInvoiceDetail) {
		String sql = "INSERT INTO GST_INVOICE_DETAILS (CUSTOMER_NAME,CUSTOMER_ADDRESS,CURRENT_GSTIN,"
				+ "LOAN_BRANCH_ADDRESS,GSTIN,TRANSACTION_DATE, INVOICE_NUMBER,CHARGE_DESCRIPTION,CHARGE_AMOUNT,"
				+ "CGST_RATE,CGST_AMOUNT,SGST_RATE, SGST_AMOUNT,IGST_RATE,IGST_AMOUNT,"
				+ "UGST_RATE,UGST_AMOUNT,CESS_AMOUNT, POP,POS,CIN,PAN,SAC,WEBSITE_ADDRESS,"
				+ "EMAILID,REG_BANK_ADDRESS,DISCLAIMER)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL, sql);
		KeyHolder keyHolder = new GeneratedKeyHolder();

		extNamedJdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });
				int index = 1;
				ps.setString(index++, gstInvoiceDetail.getCustomerName());
				ps.setString(index++, gstInvoiceDetail.getCustomerAddress());
				ps.setString(index++, gstInvoiceDetail.getCurrentGstin());
				ps.setString(index++, gstInvoiceDetail.getLoanBranchAddress());
				ps.setString(index++, gstInvoiceDetail.getGstin());
				ps.setDate(index++, (Date) gstInvoiceDetail.getTransactionDate());
				ps.setString(index++, gstInvoiceDetail.getInvoiceNumber());
				ps.setString(index++, gstInvoiceDetail.getChargeDescription());
				ps.setBigDecimal(index++, gstInvoiceDetail.getChargeAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getCgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getCgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getSgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getSgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getIgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getIgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getUgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getUgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getCessAmount());
				ps.setString(index++, gstInvoiceDetail.getPop());
				ps.setString(index++, gstInvoiceDetail.getPos());
				ps.setString(index++, gstInvoiceDetail.getCin());
				ps.setString(index++, gstInvoiceDetail.getPan());
				ps.setString(index++, gstInvoiceDetail.getSac());
				ps.setString(index++, gstInvoiceDetail.getWebsiteAddress());
				ps.setString(index++, gstInvoiceDetail.getEmailId());
				ps.setString(index++, gstInvoiceDetail.getRegBankAddress());
				ps.setString(index, gstInvoiceDetail.getDisclaimer());

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}

	@Override
	public long fetchHeaderIdForProcessing(GSTReqFile gstReqFile) {
		String sql = " Select COUNT(*) FROM GST_VOUCHER_DETAILS WHERE PROCESSED = ? AND REQ_FILE_ID = ? ";
		logger.debug(Literal.SQL, sql);
		long count = 0;
		try {
			count = extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, 0, 0);
			if (count > 0) {
				return getHeaderIdForFile(gstReqFile);
			}
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}
		return count;
	}

	@Override
	public long getHeaderIdForFile(GSTReqFile gstReqFile) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		String sql = "INSERT INTO GST_REQUEST_FILE (FILE_LOCATION, CREATED_DATE) values(?,?)";

		logger.debug(Literal.SQL, sql);
		KeyHolder keyHolder = new GeneratedKeyHolder();

		extNamedJdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });
				int index = 1;
				ps.setString(index++, gstReqFile.getFileLocation());
				ps.setTimestamp(index, curTimeStamp);

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}

	@Override
	public void updateHeaderIdIntoGSTVoucherDetails(long headerId) {

		String sql = "UPDATE GST_VOUCHER_DETAILS  SET REQ_FILE_ID=? WHERE  PROCESSED = ? AND REQ_FILE_ID = ?";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, headerId);
			ps.setLong(index++, 0);
			ps.setLong(index, 0);
		});
	}

	@Override
	public List<Taxes> getTaxDetailsForHeaderId(long taxHeaderId) {
		List<Taxes> taxesList = new ArrayList<>();
		try {
			String sql = " SELECT ID,TAXTYPE,TAXPERC,ACTUALTAX,PAIDTAX,NETTAX FROM  TAX_DETAILS  WHERE REFERENCEID =?";

			mainNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
				ps.setLong(1, taxHeaderId);
			}, rs -> {
				Taxes tax = new Taxes();
				tax.setId(rs.getLong("ID"));
				tax.setTaxType(rs.getString("TAXTYPE"));
				tax.setTaxPerc(rs.getBigDecimal("TAXPERC"));
				tax.setActualTax(rs.getBigDecimal("ACTUALTAX"));
				tax.setPaidTax(rs.getBigDecimal("PAIDTAX"));
				tax.setNetTax(rs.getBigDecimal("NETTAX"));
				taxesList.add(tax);
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return taxesList;
	}

	@Override
	public void updateTaxDetails(Taxes taxes) {
		String sql = "UPDATE TAX_DETAILS  SET TAXPERC=?, ACTUALTAX=?, PAIDTAX=?, NETTAX=? WHERE  ID = ?";
		logger.debug(Literal.SQL, sql);

		mainNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setBigDecimal(index++, taxes.getTaxPerc());
			ps.setBigDecimal(index++, taxes.getActualTax());
			ps.setBigDecimal(index++, taxes.getPaidTax());
			ps.setBigDecimal(index++, taxes.getNetTax());
			ps.setLong(index, taxes.getId());
		});
	}

	@Override
	public void updateFileWriteStatus(int status) {
		String sql = "UPDATE GST_REQUEST_DETAIL SET STATUS = ?";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setInt(index, status);
		});
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

}
