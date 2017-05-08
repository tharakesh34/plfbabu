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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.service.financemanagement.PresentmentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.interfacebajaj.fileextract.service.FileImport;
import com.pennanttech.pff.core.Literal;

public class PresentmentDetailExtract extends FileImport implements Runnable {
	private final static Logger logger = Logger.getLogger(PresentmentDetailExtract.class);

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

		int recordCount = 0;
		int successCount = 0;
		int failedCount = 0;
		StringBuilder remarks = new StringBuilder("");
		String record = null;
		BufferedReader br = null;
		int rcdLegth = 0;
		MapSqlParameterSource map = null;
		boolean isError = false;

		try {
			rcdLegth = 78;
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setActualCount(getTotalRecords());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStartTime(DateUtility.getSysDate());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_EXECUTING);
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFileName(getFile().getName());

			// Clearing the data from staging tables
			clearTables();

			br = new BufferedReader(new FileReader(getFile()));
			while ((record = br.readLine()) != null) {

				if (StringUtils.trimToNull(record) == null) {
					break;
				}

				recordCount++;
				if (record.length() != rcdLegth) {
					int endLength = rcdLegth + 1;
					throw new Exception("Record Length less than " + endLength + " at line Number " + recordCount);
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

				// Inserting the data into staging table
				insertData(map);

				successCount++;
				map = null;

				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setProcessedCount(recordCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setSuccessCount(successCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFailedCount(failedCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_EXECUTING);
			}
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setEndTime(DateUtility.getSysDate());

			if (recordCount > 0) {
				remarks.append("Completed successfully, total Records: ");
				remarks.append(recordCount);
				remarks.append(", Sucess: ");
				remarks.append(successCount + ".");
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(remarks.toString());
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_SUCCESS);
			} else {
				remarks.append(" Uploaded File is empty please verify once");
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(remarks.toString());
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_FAILED);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			isError = true;
			String errorMasg = e.toString().concat(" at line number: " + recordCount);
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(errorMasg);
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_FAILED);
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setEndTime(DateUtility.getSysDate());
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
				clearTables();
			} else {
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
			throw new Exception("Batchid should be mandatory. ");
		} else if (batchReference.length() != 29) {
			throw new Exception("Batchid length should be 29. ");
		}

		// status
		String status = map.getValue("Status").toString();
		if (StringUtils.trimToNull(status) == null) {
			throw new Exception("Status should be mandatory. ");
		} else if (status.length() != 1) {
			throw new Exception("Status length should be 1. ");
		}

		// ReasonCode
		String reasonCode = map.getValue("ReasonCode").toString();
		if (StringUtils.trimToNull(reasonCode) == null) {
			throw new Exception("ReasonCode should be mandatory. ");
		}
	}

	int totalCount = 0;
	int successCount = 0;
	int failedCount = 0;
	// After file import processing the data from staging table
	private void processingPrsentments() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BRANCHCODE, AGREEMENTNO, INSTALMENTNO, BFLREFERENCENO, BATCHID, AMOUNTCLEARED, ");
		sql.append(" CLEARINGDATE, STATUS, REASONCODE  FROM PRESENTMENT_FILEIMPORT ");

		jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					totalCount++;
					try {

						String presentmentRef = rs.getString("BATCHID");
						String status = rs.getString("STATUS");
						String reasonCode = rs.getString("REASONCODE");

						if (RepayConstants.PEXC_SUCCESS.equals(status)) {
							updatePresentMentdetails(presentmentRef, status);
							updatePresentHeader(presentmentRef, status);
						} else {
							try {
								PresentmentDetail presentmentDetail = presentmentCancellation(presentmentRef, reasonCode);
								if (StringUtils.trimToNull(presentmentDetail.getErrorDesc()) == null) {
									updatePresentMentdetails(presentmentRef, status, presentmentDetail.getBounceID());
									updatePresentHeader(presentmentRef, status);
									successCount++;
								} else {
									String errorDesc = presentmentDetail.getErrorDesc();
									errorDesc = (errorDesc.length() >= 1000) ? errorDesc.substring(0, 988) : errorDesc;
									String errorCode = "PR0001";
									updatePresentMentdetails(presentmentRef, RepayConstants.PEXC_ERROR, errorCode, errorDesc);
									updatePresentHeader(presentmentRef, RepayConstants.PEXC_ERROR);
									failedCount++;
								}
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
								String errorDesc = e.getMessage();
								if (StringUtils.trimToNull(errorDesc) != null) {
									errorDesc = (e.getMessage().length() >= 1000) ? e.getMessage().substring(0, 988) : e.getMessage();
								}
								String errorCode = "PR0002";
								updatePresentMentdetails(presentmentRef, RepayConstants.PEXC_ERROR, errorCode, errorDesc);
								updatePresentHeader(presentmentRef, RepayConstants.PEXC_ERROR);
								failedCount++;
							}
						}
					} catch (Exception e) {
						failedCount++;
					} 
				}
				return failedCount;
			}
 
		});

		logger.debug(Literal.LEAVING);
	}

	// Presentment cancellation process
	private PresentmentDetail presentmentCancellation(String presentmentRef, String reasonCode) throws Exception{
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
	private void updatePresentMentdetails(String presentmentRef, String status) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append("Update Presentmentdetails set Status = :Status Where PresentmentRef = :PresentmentRef ");

		source.addValue("Status", status);
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
	private void updatePresentMentdetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append("Update Presentmentdetails set Status = :Status, ErrorCode = :ErrorCode, ErrorDesc = :ErrorDesc Where PresentmentRef = :PresentmentRef ");

		source.addValue("Status", status);
		source.addValue("PresentmentRef", presentmentRef);
		source.addValue("ErrorCode", errorCode);
		source.addValue("ErrorDesc", errorDesc);
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
	
	// Update the presentment status and bounceid
	private void updatePresentMentdetails(String presentmentRef, String status, long bounceId) {
		logger.debug(Literal.ENTERING);
		
		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();
		
		sql.append("Update Presentmentdetails set Status = :Status , BounceID = :BounceID Where PresentmentRef = :PresentmentRef ");
		
		source.addValue("Status", status);
		source.addValue("PresentmentRef", presentmentRef);
		source.addValue("BounceID", bounceId);
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
	
	
	// Update the PresentHeader Success and failure records
	private void updatePresentHeader(String presentmentRef, String status) {
		logger.debug(Literal.ENTERING);

		long presentmentId = getPresentmentId(presentmentRef);
		updatePresentHeader(presentmentId, status);

		logger.debug(Literal.LEAVING);
	}

	
	// Update the PresentHeader Success and failure records
	private void updatePresentHeader(long presentmentId, String status) {
		logger.debug(Literal.ENTERING);

		StringBuffer sql = new StringBuffer();
		MapSqlParameterSource source = new MapSqlParameterSource();

		if (RepayConstants.PEXC_SUCCESS.equals(status)) {
			sql.append("Update PRESENTMENTHEADER set SUCCESSRECORDS = SUCCESSRECORDS+1 Where ID = :ID ");
		} else {
			sql.append("Update PRESENTMENTHEADER set FAILEDRECORDS = FAILEDRECORDS+1 Where ID = :ID ");

		}
		source.addValue("ID", presentmentId);
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
}
