package com.pennanttech.external.collectionreceipt.job;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennanttech.external.ExtReceiptServiceHook;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.model.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtGenericDao;
import com.pennanttech.external.util.InterfaceErrorCodeUtil;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class ExtCollectionFileProcessorJob extends AbstractJob implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtCollectionFileProcessorJob.class);

	private static final String FETCH_QUERY = "Select * from COLL_RECEIPT_HEADER  Where STATUS=? AND EXTRACTION = ?";

	private DataSource dataSource;
	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ExtGenericDao extInterfaceDao;
	private ApplicationContext applicationContext;
	private PlatformTransactionManager transactionManager;
	private ExtReceiptServiceHook extReceiptServiceHook;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("extDataSource", DataSource.class);
		extInterfaceDao = applicationContext.getBean(ExtGenericDao.class);
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);
		transactionManager = applicationContext.getBean("transactionManager", PlatformTransactionManager.class);
		extReceiptServiceHook = applicationContext.getBean(ExtReceiptServiceHook.class);

		if (extReceiptServiceHook != null) {
			return;
		}

		// get error codes handy
		if (InterfaceErrorCodeUtil.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			InterfaceErrorCodeUtil.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Fetch 10 files using extraction status = 0
		JdbcCursorItemReader<CollReceiptHeader> cursorItemReader = new JdbcCursorItemReader<CollReceiptHeader>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(1);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<CollReceiptHeader>() {
			@Override
			public CollReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
				CollReceiptHeader collectionFile = new CollReceiptHeader();
				collectionFile.setId(rs.getLong("ID"));
				collectionFile.setRequestFileName(rs.getString("REQUEST_FILE_NAME"));
				collectionFile.setRequestFileLocation(rs.getString("REQUEST_FILE_LOCATION"));
				collectionFile.setErrorCode(rs.getString("ERROR_CODE"));
				collectionFile.setErrorMessage(rs.getString("ERROR_MESSAGE"));
				return collectionFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, COMPLETED);
				ps.setLong(2, COMPLETED);
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		CollReceiptHeader extReceiptHeader;

		try {
			while ((extReceiptHeader = cursorItemReader.read()) != null) {
				try {

					// header update to in progress
					extReceiptHeader.setStatus(INPROCESS);
					// receiptHeader.setErrorCode("");
					// receiptHeader.setErrorMessage("");
					extCollectionReceiptDao.updateExtCollectionReceiptProcessStatus(extReceiptHeader);

					List<CollReceiptDetail> fileRecordsList = extCollectionReceiptDao
							.fetchCollectionRecordsById(extReceiptHeader.getId());

					if (fileRecordsList.size() > 0) {
						TransactionStatus txStatus = null;
						// Now for each record, generate receipt and update receipt status.
						for (CollReceiptDetail extRcd : fileRecordsList) {
							// begin the transaction
							txStatus = transactionManager.getTransaction(txDef);
							try {
								ExtCollectionReceiptData collectionData = splitAndSetData(extRcd.getRecordData());
								CreateReceiptUpload createReceiptUpload = getCreateReceiptUploadBean(collectionData);

								extReceiptServiceHook.createExtReceipt(createReceiptUpload, FinServiceEvent.SCHDRPY);
								// Verify if receipt has generated by using progress status
								if (createReceiptUpload.getProgress() == 2) {
									extRcd.setReceiptId(createReceiptUpload.getReceiptID());
								} else {
									extRcd.setErrorCode(createReceiptUpload.getErrorCode());
									extRcd.setErrorMessage(createReceiptUpload.getErrorDesc());
								}
								transactionManager.commit(txStatus);
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
								if (txStatus != null) {
									transactionManager.rollback(txStatus);
								}
								extRcd.setErrorCode(F400);
								extRcd.setErrorMessage(e.getMessage());
							}

							extCollectionReceiptDao.updateExtCollectionReceiptDetailStatus(extRcd);

						}
					}

					extReceiptHeader.setStatus(COMPLETED);
					extCollectionReceiptDao.updateExtCollectionReceiptProcessStatus(extReceiptHeader);

				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					extReceiptHeader.setStatus(FAILED);
					extReceiptHeader.setErrorCode(String.valueOf(F400));
					extReceiptHeader.setErrorMessage(e.getMessage());
					extCollectionReceiptDao.updateExtCollectionReceiptProcessStatus(extReceiptHeader);
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);

		} finally {
			if (cursorItemReader != null) {
				cursorItemReader.close();
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private CreateReceiptUpload getCreateReceiptUploadBean(ExtCollectionReceiptData collectionData) {
		boolean isCheque = false;
		CreateReceiptUpload cru = new CreateReceiptUpload();
		cru.setReference(String.valueOf(collectionData.getAgreementNumber()));
		cru.setReferenceID(collectionData.getReceiptNumber());
		cru.setAllocationType("M");
		cru.setAppDate(SysParamUtil.getAppDate());
		cru.setValueDate(SysParamUtil.getAppDate());
		cru.setRealizationDate(collectionData.getReceiptDate());
		cru.setReceiptAmount(collectionData.getGrandTotal());
		cru.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		// collectionData
		if ("Q".equals(StringUtils.stripToEmpty(collectionData.getReceiptType()))) {
			isCheque = true;
		}
		cru.setReceiptPurpose("SP");
		cru.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		cru.setReceiptChannel(PennantConstants.List_Select);

		if (isCheque) {
			cru.setReceiptMode(ReceiptMode.CHEQUE);
			cru.setDepositDate(collectionData.getChequeDate());
		} else {
			cru.setReceiptMode(ReceiptMode.CASH);
			cru.setDepositDate(collectionData.getReceiptDate());
		}
		cru.setBankCode(String.valueOf(collectionData.getDealingBankId()));
		cru.setEffectSchdMethod("");

		if (isCheque) {
			cru.setChequeNumber(String.valueOf(collectionData.getChequeNumber()));
		}

		List<CreateReceiptUpload> alloc = new ArrayList<CreateReceiptUpload>();

		if (collectionData.getBccAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc1 = new CreateReceiptUpload();
			alloc1.setCode("BOUNCE");
			alloc1.setAmount(getRoundAmount(String.valueOf(collectionData.getBccAmount())));
			alloc1.setFeeId(Long.parseLong("45"));
			alloc.add(alloc1);
		}
		if (collectionData.getLppAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc2 = new CreateReceiptUpload();
			alloc2.setCode("ODC");
			alloc2.setAmount(getRoundAmount(String.valueOf(collectionData.getLppAmount())));
			alloc2.setFeeId(Long.parseLong("46"));
			alloc.add(alloc2);
		}
		if (collectionData.getEmiAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc3 = new CreateReceiptUpload();
			alloc3.setCode("EM");
			alloc3.setAmount(getRoundAmount(String.valueOf(collectionData.getEmiAmount())));
			// alloc3.setFeeId(Long.parseLong("46"));
			alloc.add(alloc3);
		}
		if (collectionData.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc4 = new CreateReceiptUpload();
			alloc4.setCode("ADVEMI");
			alloc4.setAmount(getRoundAmount(String.valueOf(collectionData.getExcessAmount() + ".00")));
			alloc4.setFeeId(Long.parseLong("75"));
			alloc.add(alloc4);
		}
		// FOR TESTING
		// 38-ACCCHAR
		// 35-CHRGTWO
		// 36-CHRONE
		// 209-HCGS
		if (collectionData.getOtherAmt1().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc5 = new CreateReceiptUpload();
			alloc5.setCode(String.valueOf(collectionData.getOthercharge1()));
			alloc5.setAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt1())));
			alloc.add(alloc5);
		}

		if (collectionData.getOtherAmt2().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc6 = new CreateReceiptUpload();
			alloc6.setCode(String.valueOf(collectionData.getOtherCharge2()));
			alloc6.setAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt2())));
			alloc.add(alloc6);
		}

		if (collectionData.getOtherAmt3().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc7 = new CreateReceiptUpload();
			alloc7.setCode(String.valueOf(collectionData.getOtherCharge3()));
			alloc7.setAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt3())));
			alloc.add(alloc7);
		}

		if (collectionData.getOtherAmt4().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc8 = new CreateReceiptUpload();
			alloc8.setCode(String.valueOf(collectionData.getOtherCharge4()));
			alloc8.setAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt4())));
			alloc.add(alloc8);
		}

		cru.setAllocations(alloc);
		return cru;
	}

	private BigDecimal getRoundAmount(String strAmount) {
		BigDecimal convertedAmt = new BigDecimal("15000");
		return convertedAmt.setScale(2);
	}

}
