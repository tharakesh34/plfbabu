package com.pennant.webui.configuration.vasconfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.interfacebajaj.fileextract.service.FileImport;
import com.pennanttech.pennapps.core.resource.Literal;

public class VASPremiumCalcDetailExtract extends FileImport implements Runnable {
	private static final Logger logger = Logger.getLogger(VASPremiumCalcDetailExtract.class);

	private PresentmentDetailService presentmentDetailService;

	public VASPremiumCalcDetailExtract(DataSource datsSource, PresentmentDetailService presentmentDetailService) {
		super(datsSource);
		this.presentmentDetailService = presentmentDetailService;
	}

	@Override
	public void run() {
		importData();
	}

	private void importData() {
		logger.debug(Literal.ENTERING);

		int lineNumber = 0;
		MapSqlParameterSource map = null;
		Workbook workbook = null;
		Sheet sheet = null;
		FileInputStream fis = null;
		int recordCount = 0;
		int  successCount = 0;
		int  failedCount = 0;
		long batchId = 0;
		ArrayList<VASPremiumCalcDetails> premiumCalcDetails = new ArrayList<>();
		VASPremiumCalcDetails calcDetails = null;

		try {
			PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setTotalRecords(getTotalRecords());
			PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setStartTime(DateUtility.getSysDate());
			PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setStatus(ExecutionStatus.E.name());
			PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setFileName(getFile().getName());

			fis = new FileInputStream(getFile());
			if (getFile().toString().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
			} else {
				workbook = new XSSFWorkbook(fis);
			}
			if (workbook != null) {
				sheet = workbook.getSheetAt(0);
			}
			
			batchId = saveFileHeader(getFile().getName());
			
			Row row = null;
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				try {
					if (i == 0) {
						continue;
					}
					lineNumber++;

					calcDetails = new VASPremiumCalcDetails();
					calcDetails.setProductType(productType);
					
					map = new MapSqlParameterSource();
					map.addValue("BranchCode", getCellValue(row, pos_BranchCode));
					map.addValue("AgreementNo", getCellValue(row, pos_AgreementNo));
					map.addValue("InstalmentNo", "0");
					map.addValue("BFLReferenceNo", getCellValue(row, pos_BFLReferenceNo));
					map.addValue("Batchid", getCellValue(row, pos_Batchid));
					map.addValue("AmountCleared", getCellValue(row, pos_AmountCleared));
					map.addValue("ClearingDate", getDateValue(row, pos_ClearingDate), Types.DATE);
					map.addValue("Status", getCellValue(row, pos_Status));

					map.addValue("Name", getCellValue(row, pos_Name));
					map.addValue("UMRNNo", getCellValue(row, pos_UMRNNo));
					map.addValue("AccountType", getCellValue(row, pos_AccountType));
					map.addValue("PaymentDue", getDateValue(row, pos_PaymentDue), Types.DATE);
					map.addValue("ReasonCode", getStringCellValue(row, pos_ReasonCode));

					if (row.getPhysicalNumberOfCells() > pos_FailureReasons) {
						map.addValue("Failure reason", StringUtils.trimToNull(row.getCell(pos_FailureReasons).getStringCellValue()));
					}
				} catch (Exception e) {
					saveBatchLog(batchId, RepayConstants.PEXC_FAILURE, String.valueOf(lineNumber), e.getMessage());
				}
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setStatus(ExecutionStatus.E.name());
			}

			if (lineNumber <= 0) {
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setRemarks(" Uploaded File is empty please verify once");
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setEndTime(DateUtility.getSysDate());
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setStatus(ExecutionStatus.F.name());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setRemarks(e.getMessage());
			PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setEndTime(DateUtility.getSysDate());
		} finally {
			StringBuilder remarks = new StringBuilder();
			if (failedCount > 0) {
				remarks.append(" Completed with exceptions, total Records: ");
				remarks.append(recordCount);
				remarks.append(", Sucess: ");
				remarks.append(successCount + ".");
				remarks.append(", Failure: ");
				remarks.append(failedCount + ".");
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setRemarks(remarks.toString());
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setStatus(ExecutionStatus.F.name());
			} else {
				remarks.append(" Completed successfully, total Records: ");
				remarks.append(recordCount);
				remarks.append(", Sucess: ");
				remarks.append(successCount + ".");
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setRemarks(remarks.toString());
				PennantConstants.BATCH_TYPE_VASPREMIUM_CALC_IMPORT.setStatus(ExecutionStatus.S.name());
			}
			updateFileHeader(batchId, recordCount, successCount, failedCount, remarks.toString());
			try {
				if (fis != null) {
					fis.close();
				}
				backUpFile();
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		logger.debug(Literal.LEAVING);
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
		source.addValue("ProcessName", "VASPREMIUM_CALC_IMPORT");
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
		query.append(
				" SucessRecords = :SucessRecords, FailedRecords = :FailedRecords, Remarks = :Remarks Where ID = :ID");

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

}
