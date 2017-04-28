package com.pennanttech.interfacebajaj.fileextract;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.Assert;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.interfacebajaj.fileextract.service.FileImport;

public class PresentmentDetailExtract extends FileImport implements Runnable {

	private final static Logger logger = Logger.getLogger(PresentmentDetailExtract.class);

	public PresentmentDetailExtract(DataSource datsSource) {
		super(datsSource);
	}

	@Override
	public void run() {
		importData();
	}

	private void importData() {

		int recordCount = 0;
		int successCount = 0;
		int failedCount = 0;
		StringBuilder remarks = new StringBuilder("");
		String key = null;
		String record = null;
		BufferedReader br = null;
		int rcdLegth = 0;
		MapSqlParameterSource map = null;

		try {
			Assert.notNull(batchType, "Batch type cannot be blank");
			Assert.notNull(userId, "User Id cannot be blank");

			batchStatus.setProcName(batchType);
			batchStatus.setBatchStatus("I");
			batchStatus.setUserId(userId);
			batchStatus.setStartTm(new Timestamp(System.currentTimeMillis()));
			batchStatus.setBatchFileName(getFile().getName());

			/* logBatchStatus(batchStatus); */

			Assert.notNull(endLineLength, "Presentment import file record length is not available in System paramers.");
			rcdLegth = Integer.valueOf(StringUtils.trimToEmpty(endLineLength.toString()));

			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStartTime(DateUtility.getSysDate());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_EXECUTING);
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFileName(getFile().getName());

			br = new BufferedReader(new FileReader(getFile()));
			while ((record = br.readLine()) != null) {
				try {
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
					map.addValue("AgreementNo", getFieldValue(record, 4, 13));
					map.addValue("InstalmentNo", getFieldValue(record, 17, 3));
					map.addValue("BFLReferenceNo", getFieldValue(record, 20, 3));
					map.addValue("Batchid", getFieldValue(record, 23, 29));
					map.addValue("AmountCleared", getFieldValue(record, 29, 14));
					map.addValue("ClearingDate", getDateValue(record, 30, 8));
					map.addValue("Status", getFieldValue(record, 38, 1));
					map.addValue("ReasonCode", getFieldValue(record, 40, 3));

					key = map.getValue("Batchid").toString();

					UpdatePresentMentdetails(map);
					//Check with chaitanya for others....
					successCount++;
					if (isLogStatus()) {
						saveBatchLog(key, "S", "Success.");
					}
					map = null;
				} catch (Exception e) {
					logger.error("Exception {}", e);
					failedCount++;
					if (StringUtils.trimToEmpty(key).isEmpty()) {
						key = String.valueOf(recordCount);
					}
					/* saveBatchLog(key, "F", e.getMessage()); */
					key = null;
				}
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setProcessedCount(recordCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setSuccessCount(successCount);
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFailedCount(failedCount);
			}
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setEndTime(DateUtility.getSysDate());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_EXECUTING);

			if (recordCount > 0) {
				// Update the Status of the file as Reading Successful
				if (failedCount > 0) {
					remarks.append("Completed with exceptions, total Records: ");
					remarks.append(recordCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount);
					remarks.append(", Failure: ");
					remarks.append(failedCount + ".");
				} else {
					remarks.append("Completed successfully, total Records: ");
					remarks.append(recordCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount + ".");
				}
				updateBatchStatus("S", remarks.toString());

				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_FAILED);
			} else {
				remarks.append(" Uploaded File is empty please verify once");
				updateBatchStatus("F", remarks.toString());
				PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_FAILED);
			}
		} catch (Exception e) {
			updateBatchStatus("F", e.getMessage());
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setStatus(PennantConstants.FILESTATUS_FAILED);

		} finally {
			PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setRemarks(remarks.toString());
			try {
				if (br != null) {
					br.close();
				}
				backUpFile();
			} catch (IOException e) {
				logger.error("Exception {}", e);
			}
			map = null;
		}
	}

	private void UpdatePresentMentdetails(MapSqlParameterSource map) {

		logger.debug("Entering");

		StringBuffer query = new StringBuffer();
		query.append("Update Presentmentdetails set Status = :Status Where PresentmentId = :Batchid ");

		try {
			this.jdbcTemplate.update(query.toString(), map);
		} catch (Exception e) {
			logger.error("Exception {}", e);
		}

		logger.debug("Leaving");

	}
}
