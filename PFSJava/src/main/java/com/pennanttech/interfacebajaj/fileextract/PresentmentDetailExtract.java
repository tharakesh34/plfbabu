package com.pennanttech.interfacebajaj.fileextract;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.PresentmentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.interfacebajaj.fileextract.service.FileImport;
import com.pennanttech.pff.core.Literal;

public class PresentmentDetailExtract extends FileImport implements Runnable {
	private static final Logger logger = Logger.getLogger(PresentmentDetailExtract.class);

	private PresentmentHeaderService presentmentHeaderService;

	public PresentmentDetailExtract(DataSource datsSource, PresentmentHeaderService presentmentHeaderService) {
		super(datsSource);
		this.presentmentHeaderService = presentmentHeaderService;
	}

	@Override
	public void run() {
		importData();
	}

	// Importing the data from file
	private void importData() {
		logger.debug(Literal.ENTERING);

		String record = null;
		BufferedReader br = null;
		int rcdLegth = 0;
		int lineNumber = 0;
		MapSqlParameterSource map = null;
		boolean isError = false;

		try {
			rcdLegth = 78;
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setTotalRecords(getTotalRecords());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStartTime(DateUtility.getSysDate());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(ExecutionStatus.E.name());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFileName(getFile().getName());

			// Clearing the data from staging tables
			clearTables();

			br = new BufferedReader(new FileReader(getFile()));
			while ((record = br.readLine()) != null) {
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks("Uploading and validating the presentment response.....");
				if (StringUtils.trimToNull(record) == null) {
					break;
				}

				lineNumber++;
				if (record.length() != rcdLegth) {
					int endLength = rcdLegth + 1;
					throw new Exception("Record Length less than " + endLength);
				}

				map = new MapSqlParameterSource();
				map.addValue("BranchCode", getFieldValue(record, 0, 3));
				map.addValue("AgreementNo", getFieldValue(record, 3, 14));
				map.addValue("InstalmentNo", getFieldValue(record, 17, 3));
				map.addValue("BFLReferenceNo", getFieldValue(record, 20, 3));
				map.addValue("Batchid", getFieldValue(record, 23, 29));
				map.addValue("AmountCleared", getFieldValue(record, 52, 14));
				map.addValue("ClearingDate", getDateValue(record, 66, 8));
				map.addValue("Status", getFieldValue(record, 74, 1));
				map.addValue("ReasonCode", getFieldValue(record, 75, 3));

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
				if (br != null) {
					br.close();
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

		// batchReference
		String batchReference = map.getValue("Batchid").toString();
		if (StringUtils.trimToNull(batchReference) == null) {
			throw new Exception("Batchid should be mandatory.");
		} else if (batchReference.length() != 29) {
			throw new Exception("Batchid length should be 29.");
		}

		// status
		String status = map.getValue("Status").toString();
		if (StringUtils.trimToNull(status) == null) {
			throw new Exception("Status should be mandatory.");
		} else if (status.length() != 1) {
			throw new Exception("Status length should be 1.");
		}

		// ReasonCode
		String reasonCode = map.getValue("ReasonCode").toString();
		if (StringUtils.trimToNull(reasonCode) == null) {
			throw new Exception("ReasonCode should be mandatory.");
		}
	}

	StringBuilder remarks = null;
	PresentmentDetail detail = null;
	int recordCount = 0;
	int successCount = 0;
	int failedCount = 0;
	
	// After file import, processing the data from staging table
	private void processingPrsentments() {
		logger.debug(Literal.ENTERING);

		recordCount = 0;
		successCount = 0;
		failedCount = 0;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BRANCHCODE, AGREEMENTNO, INSTALMENTNO, BFLREFERENCENO, BATCHID, AMOUNTCLEARED, ");
		sql.append(" CLEARINGDATE, STATUS, REASONCODE  FROM PRESENTMENT_FILEIMPORT ");

		jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
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

						if (RepayConstants.PEXC_SUCCESS.equals(status)) {
							successCount++;
							updatePresentmentDetails(presentmentRef, status);
							updatePresentmentHeader(presentmentRef, status, status);
						} else {
							try {
								detail = presentmentCancellation(presentmentRef, reasonCode);
								if (StringUtils.trimToNull(detail.getErrorDesc()) == null) {
									successCount++;
									detail.setErrorDesc(reasonCode + " - " + detail.getBounceReason());
									updatePresentmentDetails(presentmentRef, RepayConstants.PEXC_BOUNCE, detail.getBounceID(), detail.getManualAdviseId(), detail.getErrorDesc());
									updatePresentmentHeader(presentmentRef, RepayConstants.PEXC_BOUNCE, detail.getStatus());
								} else {
									failedCount++;
									updatePresentmentDetails(presentmentRef, RepayConstants.PEXC_FAILURE, "PR0001", detail.getErrorDesc());
									updatePresentmentHeader(presentmentRef, RepayConstants.PEXC_FAILURE, detail.getStatus());
								}
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
								failedCount++;
								updatePresentmentDetails(presentmentRef, RepayConstants.PEXC_FAILURE, "PR0002", e.getMessage());
								updatePresentmentHeader(presentmentRef, RepayConstants.PEXC_FAILURE, detail.getStatus());
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
					remarks = null;
				}
				return 0;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	// Presentment cancellation process
	private PresentmentDetail presentmentCancellation(String presentmentRef, String reasonCode) throws Exception {
		return this.presentmentHeaderService.presentmentCancellation(presentmentRef, reasonCode);
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
		presentmentHeaderService.updatePresentmentDetails(presentmentRef, status, errorCode, errorDesc);
		logger.debug(Literal.LEAVING);
	}

	// Update the presentment status and bounceid
	private void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {
		logger.debug(Literal.ENTERING);
		presentmentHeaderService.updatePresentmentDetails(presentmentRef, status, bounceId, manualAdviseId, errorDesc);
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
		source.addValue("PRESENTMENTREF", presentmentRef);
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
}
