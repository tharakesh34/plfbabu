package com.pennanttech.interfacebajaj.fileextract;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.interfacebajaj.fileextract.service.FileImport;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class PresentmentDetailExtract extends FileImport implements Runnable {
	private static final Logger logger = Logger.getLogger(PresentmentDetailExtract.class);
	
	// Constant values used in the interface
	private static final long			CON_RESPONSE_BOUNCE		= 99;
	private static final long			CON_RESPONSE_SUCCESS	= 0;
	private static final String			CON_LATERESPONSE_FLAG	= "Y";
	private static final String			CON_BOUNCE_STATUS		= "B";
	private static final String			CON_SUCCESS_STATUS		= "S";
	private static final long			CON_USER_ID				= 1000;
	private static final String			IS_LOANISACTIVE			= "1";
	private static final String			DATE_FORMAT				= "dd/MM/yyyy HH:mm:ss";
	private static final String			REGIX					= "[/:\\s]";

	private PresentmentDetailService presentmentDetailService;
	
	

	public PresentmentDetailExtract(DataSource datsSource, PresentmentDetailService presentmentDetailService) {
		super(datsSource);
		this.presentmentDetailService = presentmentDetailService;
	}

	@Override
	public void run() {
		importData();
	}

	// Importing the data from file
		@SuppressWarnings("resource")
		private void importData() {
			logger.debug(Literal.ENTERING);

			int lineNumber = 0;
			MapSqlParameterSource map = null;
			boolean isError = false;
			Workbook workbook = null;
			Sheet sheet = null;
			FileInputStream fis = null;

		try {
			// rcdLegth = 79;
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setTotalRecords(getTotalRecords());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStartTime(DateUtility.getSysDate());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.E.name());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFileName(getFile().getName());

			// Clearing the data from staging tables
			clearTables();

			fis = new FileInputStream(getFile());

			if (getFile().toString().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
			} else {
				workbook = new XSSFWorkbook(fis);
			}

			if (workbook != null) {
				sheet = workbook.getSheetAt(0);
			}

			Row row = null;
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				try {
					if (i == 0 && (row != null && row.getPhysicalNumberOfCells() < row_NumberOfCells)) {
						throw new Exception("Record is invalid at line :" + lineNumber);
					}
					if (i == 0) {
						continue;
					}
					
					lineNumber++;

					StringUtils.trimToNull(String.valueOf(row.getCell(0)));
					if (row.getCell(0) == null) {
						break;
					}

					map = new MapSqlParameterSource();
					map.addValue("BranchCode", getCellValue(row, pos_BranchCode));
					map.addValue("AgreementNo", getCellValue(row, pos_AgreementNo));
					map.addValue("InstalmentNo", "0");
					map.addValue("BFLReferenceNo", getCellValue(row, pos_BFLReferenceNo));
					map.addValue("Batchid", getCellValue(row, pos_Batchid));
					map.addValue("AmountCleared", getCellValue(row, pos_AmountCleared));
					map.addValue("ClearingDate",getDateValue(row, pos_ClearingDate),Types.DATE);
					map.addValue("Status", getCellValue(row, pos_Status));

					map.addValue("Name", getCellValue(row, pos_Name));
					map.addValue("UMRNNo", getCellValue(row, pos_UMRNNo));
					map.addValue("AccountType", getCellValue(row, pos_AccountType));
					map.addValue("PaymentDue", getDateValue(row, pos_PaymentDue),Types.DATE);
					map.addValue("ReasonCode", getStringCellValue(row, pos_ReasonCode));
					
					//TODO:check set the value
					if (row.getPhysicalNumberOfCells() > pos_FailureReasons) {
						map.addValue("Failure reason", StringUtils.trimToNull(row.getCell(pos_FailureReasons).getStringCellValue()));
					}
				} catch (Exception e) {
					logger.error("Exception {}", e);
				}
				// Validate Mandatory fields
				validateFields(map);

				String presentmentRef = map.getValue("Batchid").toString();
				String status = isPresentmentReferenceExists(presentmentRef);
				if (status == null) {
					throw new Exception(Labels.getLabel("label_Presentmentdetails_Notavailable") + presentmentRef);
				} else if (RepayConstants.PEXC_SUCCESS.equals(status) || RepayConstants.PEXC_BOUNCE.equals(status)) {
					throw new Exception(" The presentment with the presentment reference :" + presentmentRef + " already processed.");
				}

				// Inserting the data into staging table
				insertData(map);
				map = null;
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.E.name());
			}

			if (lineNumber <= 0) {
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(" Uploaded File is empty please verify once");
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setEndTime(DateUtility.getSysDate());
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.F.name());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			isError = true;
			String errorMasg = e.toString().concat(". At line number: " + lineNumber);
			if (StringUtils.containsIgnoreCase(errorMasg, "java.lang.Exception:")) {
				errorMasg = StringUtils.replace(errorMasg, "java.lang.Exception:", "");
			}
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(errorMasg);
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setEndTime(DateUtility.getSysDate());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.F.name());
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (!isError) {
					backUpFile();	
				}
			} catch (IOException e) {
				logger.error("Exception {}", e);
			}
			if (isError) {
				// If error, Clear the staging tables.
				clearTables();
			} else {
				// After completion of file import, processing the data from staging tables.
				processingPrsentments();
			}
		}
		logger.debug(Literal.LEAVING);
	}
	
	// Validating the mandatory fields
	private void validateFields(MapSqlParameterSource map) throws Exception {
		
		// Aggement Number
		Object aggrementNum = map.getValue("AgreementNo");
		if (aggrementNum != null && aggrementNum.toString().length() > 14) {
			throw new Exception("Client Code  length should be less than 15.");
		}
	
		//bflreferenceno
		Object bflreferenceno = map.getValue("BFLReferenceNo");
		if (bflreferenceno != null && bflreferenceno.toString().length() > 3) {
			throw new Exception("Dealer Code  length should be less than 4.");
		}

		// batchReference
		Object batchid = map.getValue("Batchid");
		if (batchid == null) {
			throw new Exception("Debit Ref should be mandatory.");
		} else if (batchid.toString().length() != 29) {
			throw new Exception("Debit Ref length should be 29.");
		}
		
		// status
		Object status = map.getValue("Status");
		if (status == null) {
			throw new Exception("Status should be mandatory.");
		} else if (status.toString().length() != 1) {
			throw new Exception("Status length should be 1.");
		}

		// ReasonCode
		Object reasonCode = map.getValue("ReasonCode");
		if (status != null && !StringUtils.equals(RepayConstants.PAYMENT_SUCCESS, status.toString())) {
			if (reasonCode == null) {
				throw new Exception("Failure Code should be mandatory.");
			} else if (StringUtils.isBlank(reasonCode.toString())) {
				throw new Exception("Failure Code should not be empty.");
			}
		}
	}

	StringBuilder remarks = null;
	PresentmentDetail detail = null;
	int recordCount = 0;
	int successCount = 0;
	int failedCount = 0;
	long batchId = 0;
	
	// After file import, processing the data from staging table
	private void processingPrsentments() {
		logger.debug(Literal.ENTERING);

		recordCount = 0;
		successCount = 0;
		failedCount = 0;
		batchId = 0;
		remarks = null;
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BRANCHCODE, AGREEMENTNO, INSTALMENTNO, BFLREFERENCENO, BATCHID, AMOUNTCLEARED, ");
		sql.append(" CLEARINGDATE, STATUS, REASONCODE  FROM PRESENTMENT_FILEIMPORT ");

		jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				batchId = saveFileHeader(getFile().getName());
				
				while (rs.next()) {
					recordCount++;
					try {
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks("Processing the response file.....");
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setProcessedRecords(recordCount);
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setSuccessRecords(successCount);
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFailedRecords(failedCount);
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.E.name());

						// Fetching the mandatory data from resultset
						String presentmentRef = rs.getString("BATCHID");
						String status = rs.getString("STATUS");
						String reasonCode = rs.getString("REASONCODE");
						reasonCode = StringUtils.trimToEmpty(reasonCode);
						if (RepayConstants.PEXC_SUCCESS.equals(status)) {
							successCount++;
							updatePresentmentDetails(presentmentRef, status);
							updatePresentmentHeader(presentmentRef, status, status);
							presentmentDetailService.updateFinanceDetails(presentmentRef);
							updateChequeStatus(presentmentRef);
							saveBatchLog(batchId, status, presentmentRef, null);
						} else {
							try {
								detail = presentmentCancellation(presentmentRef, reasonCode);
								if (StringUtils.trimToNull(detail.getErrorDesc()) == null) {
									successCount++;
									detail.setErrorDesc(reasonCode + " - " + detail.getBounceReason());
									updatePresentmentDetails(presentmentRef, RepayConstants.PEXC_BOUNCE, detail.getBounceID(), detail.getManualAdviseId(), detail.getErrorDesc());
									updatePresentmentHeader(presentmentRef, RepayConstants.PEXC_BOUNCE, detail.getStatus());
									saveBatchLog(batchId, RepayConstants.PEXC_BOUNCE, presentmentRef, detail.getErrorDesc());
								} else {
									failedCount++;
									updatePresentmentDetails(presentmentRef, RepayConstants.PEXC_FAILURE, "PR0001", detail.getErrorDesc());
									updatePresentmentHeader(presentmentRef, RepayConstants.PEXC_FAILURE, detail.getStatus());
									saveBatchLog(batchId, RepayConstants.PEXC_FAILURE, presentmentRef, detail.getErrorDesc());
								}
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
								failedCount++;
								updatePresentmentDetails(presentmentRef, RepayConstants.PEXC_FAILURE, "PR0002", e.getMessage());
								updatePresentmentHeader(presentmentRef, RepayConstants.PEXC_FAILURE, detail.getStatus());
								saveBatchLog(batchId, RepayConstants.PEXC_FAILURE, presentmentRef, e.getMessage());
							}
						}
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}
				}
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setProcessedRecords(recordCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setSuccessRecords(successCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFailedRecords(failedCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setEndTime(DateUtility.getSysDate());
				
				// Update the Status of the file as Reading Successful
				remarks = new StringBuilder();
				if (failedCount > 0) {
					remarks.append(" Completed with exceptions, total Records: ");
					remarks.append(recordCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount + ".");
					remarks.append(", Failure: ");
					remarks.append(failedCount+".");
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(remarks.toString());
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.F.name());
				} else {
					remarks.append(" Completed successfully, total Records: ");
					remarks.append(recordCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount + ".");
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(remarks.toString());
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.S.name());
				}
				updateFileHeader(batchId, recordCount, successCount, failedCount, remarks.toString());
				return 0;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	// Presentment cancellation process
	private PresentmentDetail presentmentCancellation(String presentmentRef, String reasonCode) throws Exception {
		return this.presentmentDetailService.presentmentCancellation(presentmentRef, reasonCode);
	}

	// Truncating the data from staging tables
	private void clearTables() {
		logger.debug(Literal.ENTERING);

		jdbcTemplate.update("TRUNCATE TABLE PRESENTMENT_FILEIMPORT", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	// Inserting data into staging table
	private void insertData(MapSqlParameterSource map) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PRESENTMENT_FILEIMPORT (BranchCode,AgreementNo,InstalmentNo,BFLReferenceNo,Batchid,AmountCleared,ClearingDate,Status,ReasonCode)");
		sql.append("Values ( :BranchCode, :AgreementNo, :InstalmentNo, :BFLReferenceNo, :Batchid, :AmountCleared, :ClearingDate, :Status, :ReasonCode)");
		jdbcTemplate.update(sql.toString(), map);

		logger.debug(Literal.LEAVING);
	}

	// Update the presentment status
	private void updatePresentmentDetails(String presentmentRef, String status) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append("Update Presentmentdetails set Status = :Status, ErrorDesc = :ErrorDesc Where PresentmentRef = :PresentmentRef ");

		source.addValue("Status", status);
		source.addValue("ErrorDesc", null);
		source.addValue("PresentmentRef", presentmentRef);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}

		logger.debug(Literal.LEAVING);
	}

	// Update the presentment status
	private void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		logger.debug(Literal.ENTERING);
		presentmentDetailService.updatePresentmentDetails(presentmentRef, status, errorCode, errorDesc);
		logger.debug(Literal.LEAVING);
	}

	// Update the presentment status and bounceid
	private void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {
		logger.debug(Literal.ENTERING);
		presentmentDetailService.updatePresentmentDetails(presentmentRef, status, bounceId, manualAdviseId, errorDesc);
		logger.debug(Literal.LEAVING);
	}

	// Update the PresentHeader Success and failure records
	private void updatePresentmentHeader(String presentmentRef, String status, String preStatus) {
		logger.debug(Literal.ENTERING);

		long presentmentId = getPresentmentId(presentmentRef);
		updatePresentmentHeader(presentmentId, status, preStatus);
		
		PresentmentHeader header = getPresentmentHeader(presentmentId);
		if (header.getTotalRecords() == header.getSuccessRecords() + header.getFailedRecords()) {
			updatePresentmentHeaderStatus(header);
		}

		logger.debug(Literal.LEAVING);
	}

	// Update the PresentHeader Success and failure records
	private void updatePresentmentHeader(long presentmentId, String status, String preStatus) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		if (RepayConstants.PEXC_SUCCESS.equals(status) || RepayConstants.PEXC_BOUNCE.equals(status)) {
			if (RepayConstants.PEXC_FAILURE.equals(preStatus)) {
				sql.append("Update PRESENTMENTHEADER set SUCCESSRECORDS = SUCCESSRECORDS+1, FAILEDRECORDS = FAILEDRECORDS-1 Where ID = :ID ");
			} else {
				sql.append("Update PRESENTMENTHEADER set SUCCESSRECORDS = SUCCESSRECORDS+1 Where ID = :ID ");
			}
		} else if (!RepayConstants.PEXC_FAILURE.equals(preStatus)) {
			sql.append("Update PRESENTMENTHEADER set FAILEDRECORDS = FAILEDRECORDS+1 Where ID = :ID ");
		}
		
		try {
			if (sql.length() > 0) {
				source.addValue("ID", presentmentId);
				this.jdbcTemplate.update(sql.toString(), source);
			}
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
	}

	// Getting presentmentid using presentment reference
	private long getPresentmentId(String presentmentRef) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" SELECT PRESENTMENTID FROM PRESENTMENTDETAILS  WHERE PRESENTMENTREF = :PRESENTMENTREF ");
		source.addValue("PRESENTMENTREF", presentmentRef);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
	}

	// Getting presentmentid using presentment reference
	private PresentmentHeader getPresentmentHeader(long id) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" SELECT ID, TOTALRECORDS, PROCESSEDRECORDS, SUCCESSRECORDS, FAILEDRECORDS FROM PRESENTMENTHEADER WHERE ID = :ID");
		source.addValue("ID", id);
		
		RowMapper<PresentmentHeader> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PresentmentHeader.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
	}

	// Updating presentment header status
	private void updatePresentmentHeaderStatus(PresentmentHeader header) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append("UPDATE PRESENTMENTHEADER SET STATUS = :STATUS WHERE ID = :ID");
		source.addValue("ID", header.getId());
		source.addValue("STATUS", RepayConstants.PEXC_RECEIVED);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
	}
	
	// Getting the status of the presentment
	private String isPresentmentReferenceExists(String presentmentRef) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" SELECT STATUS FROM PRESENTMENTDETAILS  WHERE PRESENTMENTREF = :PRESENTMENTREF ");
		sql.append(" AND (STATUS = :APPSTATUS OR STATUS = :FAISTATUS )");
		source.addValue("PRESENTMENTREF", presentmentRef);
		source.addValue("APPSTATUS", RepayConstants.PAYMENT_APPROVE	);
		source.addValue("FAISTATUS", RepayConstants.PAYMENT_FAILURE);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception {}", e);
			return null;
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
	}
	
	private long saveFileHeader(String fileName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" INSERT INTO BatchFileHeader");
		sql.append(" (ID, FileName, StartTime)");
		sql.append(" VALUES( :ID, :FileName, :StartTime)");

		long batchId = presentmentDetailService.getSeqNumber("SeqBatchFileHeader");

		source.addValue("ID", batchId);
		source.addValue("FileName", fileName);
		source.addValue("StartTime", DateUtility.getSysDate());

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

		return batchId;
	}
	
	
	private void saveBatchLog(long batchId, String status, String reference, String errDesc) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" INSERT INTO BatchFileDetails");
		sql.append(" (FileId, Reference, Status, ErrorDesc)");
		sql.append(" VALUES( :FileId, :Reference, :Status, :ErrorDesc)");

		source.addValue("FileId", batchId);
		source.addValue("Reference", reference);
		source.addValue("Status", status);
		if (StringUtils.trimToNull(errDesc) != null) {
			errDesc = (errDesc.length() >= 1000) ? errDesc.substring(0, 988) : errDesc;
		}
		source.addValue("ErrorDesc", errDesc);

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void updateFileHeader(long batchId, int recordCount, int successCount, int failedCount, String remarks) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuffer query = new StringBuffer();
		query.append(" UPDATE BatchFileHeader SET EndTime = :EndTime, TotalRecords = :TotalRecords,");
		query.append(" SucessRecords = :SucessRecords, FailedRecords = :FailedRecords, Remarks = :Remarks Where ID = :ID");

		source.addValue("EndTime", DateUtility.getSysDate());
		source.addValue("TotalRecords", recordCount);
		source.addValue("SucessRecords", successCount);
		source.addValue("FailedRecords", failedCount);
		if (StringUtils.trimToNull(remarks) != null) {
			remarks = (remarks.length() >= 2000) ? remarks.substring(0, 1988) : remarks;
		}
		source.addValue("Remarks", remarks);
		source.addValue("ID", batchId);

		try {
			this.jdbcTemplate.update(query.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	
	public void responseProcess() {
 		logger.debug(Literal.ENTERING);

		recordCount = 0;
		successCount = 0;
		failedCount = 0;
		batchId = 0;
		remarks = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RESPONSEID, AGREEMENTNO,INSTRUMENT_MODE,PRESENTATIONDATE,EMI_NO,CHEQUEAMOUNT,RETURN_CODE, LATE_RESPONSE_FLAG, BATCHID, PICKUP_BATCHID, ");
		sql.append("  STATUS_CODE,RETURN_REASON ,LATE_RESPONSE_FLAG ,PROCESSED_DATE FROM PRESENTMENT_RESPONSE WHERE PICKUP_BATCHID IS NULL ");
		sql.append("  Order By AgreementNo, ResponseID ");

		jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				String procName = "PRESENTMENT_RESPONSE_SCHEDULE";
				procName = procName.concat("_").concat(DateUtil.getSysDate(DateFormat.LONG_DATE_TIME));
				batchId = saveFileHeader(procName);
				String date = DateUtil.getSysDate(DATE_FORMAT);
				date = date.replaceAll(REGIX, "");
				long jobid = Long.valueOf(date);

				while (rs.next()) {
					recordCount++;
					Presentment presement = new Presentment();
					String presentmentRef = null;
					try {
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks("Processing the response file.....");
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setProcessedRecords(recordCount);
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setSuccessRecords(successCount);
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFailedRecords(failedCount);
						PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.E.name());

						// Fetching the mandatory data from resultset
						presement.setBatchId(rs.getString("BATCHID"));
						presement.setReturnCode(rs.getLong("RETURN_CODE"));
						presement.setReturnReason(rs.getString("RETURN_REASON"));
						presement.setLateResponseFlag(rs.getString("LATE_RESPONSE_FLAG"));
						presement.setAgreementNo(rs.getString("AGREEMENTNO"));
						presement.setInstrumentMode(rs.getString("INSTRUMENT_MODE"));
						presement.setPresentationDate(rs.getDate("PRESENTATIONDATE"));
						presement.setEmiNo(rs.getLong("EMI_NO"));
						presement.setChequeAmount(rs.getBigDecimal("CHEQUEAMOUNT"));
						presement.setProcessedDate(rs.getDate("PROCESSED_DATE"));
						presement.setResponseID(rs.getLong("RESPONSEID"));
						presement.setPickupBatchId(jobid);

						validateResponseFields(presement);

						PresentmentDetail presentmentDetail = isPresentmentResponseIsExits(presement.getBatchId());

						if (presentmentDetail == null) {
							throw new Exception(
									Labels.getLabel("label_Presentmentdetails_Notavailable") + presentmentRef);
						}

						if (presentmentDetail.getStatus().equals(RepayConstants.PEXC_APPROV)) {
							firstResponseUpdate(presentmentDetail, presement);
						} else {
							lateResponseUpdate(presentmentDetail, presement);
						}

					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						failedCount++;
						updatePresentmentResponse(presement,RepayConstants.PRES_ERROR, e.getMessage());

					}
				}
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setProcessedRecords(recordCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setSuccessRecords(successCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFailedRecords(failedCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setEndTime(DateUtility.getSysDate());

				// Update the Status of the file as Reading Successful
				remarks = new StringBuilder();
				if (failedCount > 0) {
					remarks.append(" Completed with exceptions, total Records: ");
					remarks.append(recordCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount + ".");
					remarks.append(", Failure: ");
					remarks.append(failedCount + ".");
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(remarks.toString());
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.F.name());
				} else {
					remarks.append(" Completed successfully, total Records: ");
					remarks.append(recordCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount + ".");
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(remarks.toString());
					PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.S.name());
				}
				updateFileHeader(batchId, recordCount, successCount, failedCount, remarks.toString());

				return 0;
			}
		});
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Update the first response
	 * 
	 * @param presentmentDetail
	 * @param presement_Response
	 */
	private void firstResponseUpdate(PresentmentDetail presentmentDetail, Presentment presement_Response) {
		if (DateUtility.compare(DateUtility.getAppDate(), presentmentDetail.getSchDate()) < 0) {
			updatePresentmentResponse(presement_Response,RepayConstants.PRES_PENDING, null);
			return;
		}

		if (CON_RESPONSE_BOUNCE == presement_Response.getReturnCode()) {
			createBounce(presement_Response,presement_Response.getReturnReason(), RepayConstants.PRES_SUCCESS);
		} else {
			updateSuccessResponse(presement_Response);
		}
	}

	private void lateResponseUpdate(PresentmentDetail presentmentDetail, Presentment presement_Response)
			throws Exception {

		if (!StringUtils.equals(CON_LATERESPONSE_FLAG, presement_Response.getLateResponseFlag())) {
			updatePresentmentResponse(presement_Response,RepayConstants.PRES_DUPLICATE, Labels.getLabel("label_StatusCode_Duplicate"));
			return;
		}

		String status = CON_SUCCESS_STATUS;
		if (CON_RESPONSE_BOUNCE == presement_Response.getReturnCode()) {
			status = CON_BOUNCE_STATUS;
		}

		if (status.equals(presentmentDetail.getStatus())) {
			updatePresentmentResponse(presement_Response,RepayConstants.PRES_DUPLICATE, Labels.getLabel("label_StatusCode_Duplicate"));
			return;
		}

		if (status.equals(CON_RESPONSE_BOUNCE)) {

			Boolean isLoanActive = isLoanActive(presement_Response.getBatchId());

			if (isLoanActive) {
				createBounce(presement_Response,presement_Response.getReturnReason(), RepayConstants.PRES_SUCCESS);
			} else {
				updatePresentmentResponse(presement_Response,RepayConstants.PRES_LOANCLOSED, Labels.getLabel("label_StatusCode_LoanClosed"));
			}
		} else {
			if (presentmentDetail.getBounceID() > 0) {
				ManualAdvise manualAdvise = getManualAdviceDetails(presentmentDetail.getPresentmentRef());
				if (manualAdvise != null) {
					boolean isExits = isManualAdviceExitsInManualMovements(manualAdvise.getAdviseID());

					if (manualAdvise.getWaivedAmount().compareTo(new BigDecimal(0)) == 0
							&& manualAdvise.getPaidAmount().compareTo(new BigDecimal(0)) == 0 && !isExits) {
						cancelBounce(manualAdvise);
					} else {
						createPayableAdvice(manualAdvise);
					}
				}
			}

			LoggedInUser userDetails = new LoggedInUser();
			userDetails.setLoginUsrID(CON_USER_ID);
			presentmentDetailService.processReceipts(presentmentDetail, userDetails);
			updateSuccessResponse(presement_Response);

		}
	}

	private void cancelBounce(ManualAdvise manualAdvise) {
		try{
			saveToManualAdviseCancel(manualAdvise.getAdviseID());
			deleteManualAdvise(manualAdvise.getAdviseID());
		}catch(Exception e){
			logger.error(Literal.EXCEPTION,e);
		}
		
	}

	private void deleteManualAdvise(long adviseID) {
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("ADVISEID", adviseID);
		this.jdbcTemplate.update("DELETE FROM MANUALADVISE WHERE ADVISEID=:ADVISEID", param);
	}

	private void saveToManualAdviseCancel(long adviseID) {
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("ADVISEID", adviseID);
		this.jdbcTemplate.update("INSERT INTO MANUALADVISE_CANCEL SELECT * FROM MANUALADVISE WHERE ADVISEID=:ADVISEID", param);
		
	}

	private boolean isManualAdviceExitsInManualMovements(long manualAdviseId) {
		
		StringBuilder selectSql = new StringBuilder();
		boolean adviceCondition = false;
		long adviceId=0;

		selectSql.append("select adviseid from MANUALADVISEMOVEMENTS_Temp ");
		selectSql.append(" WHERE adviseid= :adviseid");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("adviseid", manualAdviseId);

		try{
			adviceId = jdbcTemplate.queryForObject(selectSql.toString(), paramMap, Long.class);
		}catch(EmptyResultDataAccessException er){
			adviceId=0;
		}catch(Exception e){
			logger.error(Literal.EXCEPTION,e);
		}
		if (adviceId>0) {
			adviceCondition = true;
		} else {
			adviceCondition = false;
		}
		return adviceCondition;
	}

	private void createPayableAdvice(ManualAdvise manualAdvise) {
		String feeTypeCode = SysParamUtil.getValueAsString("PROCESS_FEE_TYPE");
		int feetypeId=getFeeTypeIdBasedOnFeeType(feeTypeCode);
		long SeqId=getSequenceId();
		updateSequence(SeqId);
		manualAdvise.setAdviseID(SeqId);
		manualAdvise.setAdviseType(2);//change if neede
		manualAdvise.setFeeTypeID(feetypeId);
		manualAdvise.setValueDate(DateUtil.getSysDate());
		manualAdvise.setPostDate(DateUtility.getAppDate());
		manualAdvise.setPaidAmount(new BigDecimal(0));
		manualAdvise.setWaivedAmount(new BigDecimal(0));
		manualAdvise.setReservedAmt(new BigDecimal(0));
		manualAdvise.setBalanceAmt(new BigDecimal(0));
		manualAdvise.setRoleCode(null);
		manualAdvise.setTaskId(null);
		manualAdvise.setBounceID(0);
		manualAdvise.setReceiptID(0);
		manualAdvise.setWorkflowId(0);

		saveManualAdvice(manualAdvise);
	}

	private void saveManualAdvice(ManualAdvise manualAdvise) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ManualAdvise");
		sql.append("(adviseID, adviseType, finReference, feeTypeID, sequence, adviseAmount, BounceID, ReceiptID, ");
		sql.append(" paidAmount, waivedAmount, remarks, ValueDate, PostDate,ReservedAmt, BalanceAmt,  ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :adviseID, :adviseType, :finReference, :feeTypeID, :sequence, :adviseAmount, :BounceID, :ReceiptID,");
		sql.append(" :paidAmount, :waivedAmount, :remarks, :ValueDate, :PostDate, :ReservedAmt, :BalanceAmt, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);

		try {
			this.jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

	}

	private long getSequenceId() {
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("select seqNo+1 from seqManualAdvise ");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		return jdbcTemplate.queryForObject(selectSql.toString(), paramMap, Long.class);
	}
	
	public void updateSequence(long seqNo){
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("update seqManualAdvise set seqNo = :seqNo");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("seqNo", seqNo);
		
		this.jdbcTemplate.update(updateSql.toString(), paramMap);
	}

	private int getFeeTypeIdBasedOnFeeType(String feeTypeCode) {
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT Feetypeid FROM FeeTypes ");
		selectSql.append(" WHERE Feetypecode= :Feetypecode");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("Feetypecode", feeTypeCode);

		return jdbcTemplate.queryForObject(selectSql.toString(), paramMap, Integer.class);
	}

	private ManualAdvise getManualAdviceDetails(String presentmentRef) {

		logger.debug(Literal.ENTERING);

		ManualAdvise manualAdvice = new ManualAdvise();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT T2.ADVISEID,T2.finReference, T2.adviseAmount ");
		selectSql.append(" FROM PRESENTMENTDETAILS T1 inner join "); 
		selectSql.append(" MANUALADVISE T2 on T2.ADVISEID=T1.MANUALADVISEID");
		selectSql.append(" WHERE T1.PRESENTMENTREF= :PRESENTMENTREF");

		paramMap.addValue("PRESENTMENTREF", presentmentRef);

		RowMapper<ManualAdvise> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);
		try {
			manualAdvice = this.jdbcTemplate.queryForObject(selectSql.toString(), paramMap, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			manualAdvice = null;
		}
		return manualAdvice;
	}

	private void updateSuccessResponse(Presentment presement_Response) {
		successCount++;
		updatePresentmentDetails(presement_Response.getBatchId(), RepayConstants.PEXC_SUCCESS);
		updatePresentmentHeader(presement_Response.getBatchId(), RepayConstants.PEXC_SUCCESS, RepayConstants.PEXC_SUCCESS);
		presentmentDetailService.updateFinanceDetails(presement_Response.getBatchId());
		saveBatchLog(batchId, RepayConstants.PEXC_SUCCESS, presement_Response.getBatchId(), null);
		updatePresentmentResponse(presement_Response, RepayConstants.PRES_SUCCESS, null);
	}

	/**
	 * @param presentmentRef
	 * @param reasonCode
	 * @param statusCode
	 */
	private void createBounce(Presentment presement_Response, String reasonCode, String statusCode) {

		try {
			detail = presentmentCancellation(presement_Response.getBatchId(), reasonCode);

			if (StringUtils.trimToNull(detail.getErrorDesc()) == null) {

				successCount++;
				detail.setErrorDesc(reasonCode + " - " + detail.getBounceReason());
				updatePresentmentDetails(presement_Response.getBatchId(), RepayConstants.PEXC_BOUNCE, detail.getBounceID(),
						detail.getManualAdviseId(), detail.getErrorDesc());
				updatePresentmentHeader(presement_Response.getBatchId(), RepayConstants.PEXC_BOUNCE, detail.getStatus());
				saveBatchLog(batchId, RepayConstants.PEXC_BOUNCE, presement_Response.getBatchId(), detail.getErrorDesc());
				updatePresentmentResponse(presement_Response, statusCode, null);

			} else {

				failedCount++;
				updatePresentmentDetails(presement_Response.getBatchId(), RepayConstants.PEXC_FAILURE, "PR0001", detail.getErrorDesc());
				updatePresentmentHeader(presement_Response.getBatchId(), RepayConstants.PEXC_FAILURE, detail.getStatus());
				saveBatchLog(batchId, RepayConstants.PEXC_FAILURE, presement_Response.getBatchId(), detail.getErrorDesc());
				updatePresentmentResponse(presement_Response, RepayConstants.PRES_FAILED,detail.getErrorDesc());

			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			failedCount++;
			updatePresentmentDetails(presement_Response.getBatchId(), RepayConstants.PEXC_FAILURE, "PR0002", e.getMessage());
			updatePresentmentHeader(presement_Response.getBatchId(), RepayConstants.PEXC_FAILURE, detail.getStatus());
			saveBatchLog(batchId, RepayConstants.PEXC_FAILURE, presement_Response.getBatchId(), e.getMessage());
			updatePresentmentResponse(presement_Response, RepayConstants.PRES_ERROR, e.getMessage());
		}
	}

	private Boolean isLoanActive(String batchId) {

		StringBuilder selectSql = new StringBuilder();
		Boolean loanStatus = false;

		selectSql.append("SELECT T2.FINISACTIVE FROM PRESENTMENTDETAILS T1 ");
		selectSql.append(" INNER JOIN FINANCEMAIN T2 ON T2.FINREFERENCE=T1.FINREFERENCE ");
		selectSql.append(" WHERE PRESENTMENTREF= :PRESENTMENTREF");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("PRESENTMENTREF", batchId);

		String status = jdbcTemplate.queryForObject(selectSql.toString(), paramMap, String.class);
		if (IS_LOANISACTIVE.equals(status)) {
			loanStatus = true;
		} else {
			loanStatus = false;
		}
		return loanStatus;
	}

	private void updatePresentmentResponse(Presentment presement_Response, String statusCode, String errorMsg) {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = null;

		parmMap = new MapSqlParameterSource();

		parmMap.addValue("RESPONSEID", presement_Response.getResponseID());
		parmMap.addValue("PROCESSED_FLAG", "Y");
		parmMap.addValue("PROCESSED_DATE", new Timestamp(System.currentTimeMillis()));
		parmMap.addValue("STATUS_CODE", statusCode);
		parmMap.addValue("ERROR_MSG", errorMsg);

		if (StringUtils.equals(RepayConstants.PRES_PENDING, statusCode)) {
			parmMap.addValue("PICKUP_BATCHID", null);
		} else {
			parmMap.addValue("PICKUP_BATCHID", presement_Response.getPickupBatchId());
		}

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("UPDATE PRESENTMENT_RESPONSE");
		sql.append(" SET PICKUP_BATCHID = :PICKUP_BATCHID ,STATUS_CODE = :STATUS_CODE ,");
		sql.append(" PROCESSED_FLAG = :PROCESSED_FLAG,PROCESSED_DATE = :PROCESSED_DATE,");
		sql.append(" ERROR_MSG = :ERROR_MSG");
		sql.append(" WHERE RESPONSEID = :RESPONSEID");

		// Execute the SQL, binding the arguments.
		int recordCount = jdbcTemplate.update(sql.toString(), parmMap);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	private void validateResponseFields(Presentment presement) throws Exception {

		// batchReference
		String batchReference = presement.getBatchId();
		if (StringUtils.trimToNull(batchReference) == null) {
			throw new Exception("Batchid should be mandatory.");
		} else if (batchReference.length() != 29) {
			throw new Exception("Batchid length should be 29.");
		}

		// status
		Long status = presement.getReturnCode();
		if (status == null) {
			throw new Exception("Return Code should be mandatory.");
		} else if (!(status == CON_RESPONSE_SUCCESS || status == CON_RESPONSE_BOUNCE)) {
			throw new Exception("Return Code should be either 0 or 99.");
		}
		// ReasonCode
		if (CON_RESPONSE_BOUNCE == presement.getReturnCode()
				&& StringUtils.trimToNull(presement.getReturnReason()) == null) {
			throw new Exception("Return Reason should be mandatory.");
		}
	}
	

	private PresentmentDetail isPresentmentResponseIsExits(String batchId) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail presentmentDetail = new PresentmentDetail();
		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" SELECT SCHDATE,STATUS,PRESENTMENTAMT,presentmentref,FINREFERENCE,EMINO, PRESENTMENTID, ID,MANDATEID,bounceid ");
		sql.append(" FROM PRESENTMENTDETAILS  WHERE PRESENTMENTREF = :PRESENTMENTREF ");

		source.addValue("PRESENTMENTREF", batchId);

		RowMapper<PresentmentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PresentmentDetail.class);
		try {
			presentmentDetail = this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			presentmentDetail = null;
		}
		return presentmentDetail;
	}
	/*
	 * Updating the cheque status if the mode is PDC
	 */
	private void updateChequeStatus(String presentmentRef) {

		String paymentMode = presentmentDetailService.getPaymenyMode(presentmentRef);
		PresentmentDetail detail = presentmentDetailService.getPresentmentDetailsByMode(presentmentRef, paymentMode);
		//Updating the cheque status as releases if the payment mode is PDC
		if (MandateConstants.TYPE_PDC.equals(paymentMode)) {
			updateChequeStatus(detail.getMandateId(), PennantConstants.CHEQUESTATUS_REALISED);
		}
		logger.debug(Literal.LEAVING);
		
	}

	private void updateChequeStatus(long chequeDetailsId, String chequestatus) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		try {
			sql = new StringBuilder();
			sql.append("update CHEQUEDETAIL Set Chequestatus = :Chequestatus  where ChequeDetailsId = :ChequeDetailsId ");
			logger.trace(Literal.SQL + sql.toString());

			source = new MapSqlParameterSource();
			source.addValue("Chequestatus", chequestatus);
			source.addValue("ChequeDetailsId", chequeDetailsId);
			this.jdbcTemplate.update(sql.toString(), source);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
	}

}