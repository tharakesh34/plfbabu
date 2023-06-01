package com.pennant.pff.receipt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.loanclosure.LoanClosureUpload;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.dao.LoanClosureUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.CommonHeader;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class LoanClosureUploadProcessRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(LoanClosureUploadProcessRecord.class);

	private LoanClosureUploadDAO loanClosureUploadDAO;
	@Autowired
	private UploadService loanClosureUploadService;

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Sheet sheet = attributes.getSheet();

		Row headerRow = sheet.getRow(0);

		Row row = attributes.getRow();

		LoanClosureUpload lcu = new LoanClosureUpload();

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		Long recordSeq = (Long) record.getValue("RecordSeq");

		lcu.setHeaderId(headerID);
		lcu.setRecordSeq(recordSeq);

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		int readColumn = 0;

		Cell rowCell = null;

		try {
			for (Cell cell : headerRow) {
				rowCell = row.getCell(readColumn);

				if (cell.getColumnIndex() > 3) {
					break;
				}

				readColumn = cell.getColumnIndex() + 1;
				if (rowCell == null) {
					continue;
				}

				switch (cell.getColumnIndex()) {
				case 0:
					lcu.setReference(rowCell.toString());
					break;
				case 1:
					lcu.setClosureType(rowCell.toString());
					break;
				case 2:
					lcu.setRemarks(rowCell.toString());
					break;
				case 3:
					lcu.setReasonCode(rowCell.toString());
					break;
				default:
					break;
				}
			}

			long uploadID = loanClosureUploadDAO.save(lcu);
			lcu.setId(uploadID);

			List<LoanClosureUpload> allocations = new ArrayList<>();

			int index = 0;
			for (Cell cell : headerRow) {

				if (index < readColumn) {
					index++;
					continue;
				}

				LoanClosureUpload lc = new LoanClosureUpload();

				String allocationType = cell.toString();

				if (allocationType == null) {
					break;
				}

				if (CommonHeader.isValid(allocationType)) {
					continue;
				}

				lc.setId(uploadID);
				lc.setHeaderId(headerID);
				lc.setCode(allocationType.toUpperCase());

				rowCell = row.getCell(index);

				if (rowCell != null) {

					String strAmount = rowCell.toString();

					if (StringUtils.isNotEmpty(strAmount)) {
						BigDecimal str = BigDecimal.ZERO;

						try {
							str = new BigDecimal(strAmount);
						} catch (NumberFormatException e) {
							throw new AppException("Invalid amount");
						}

						if (str.compareTo(BigDecimal.ZERO) > 0) {
							lc.setAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
							allocations.add(lc);
						}
					}
				}
				index++;

				if (index == 23) {
					record.addValue("ERRORCODE", "9999");
					record.addValue("ERRORDESC", "Fee Types are exceeded the limit");

					record.addValue("STATUS", "F");
					record.addValue("PROGRESS", EodConstants.PROGRESS_FAILED);

					continue;
				}
			}

			loanClosureUploadDAO.saveAllocations(allocations);
			lcu.setAllocations(allocations);

			loanClosureUploadService.doValidate(header, lcu);

			if (lcu.getProgress() == EodConstants.PROGRESS_FAILED) {
				record.addValue("ERRORCODE", lcu.getErrorCode());
				record.addValue("ERRORDESC", lcu.getErrorDesc());
			}
		} catch (AppException e) {
			lcu.setStatus("F");
			lcu.setProgress(EodConstants.PROGRESS_FAILED);

			record.addValue("ERRORCODE", "9999");
			record.addValue("ERRORDESC", e.getMessage());

			record.addValue("STATUS", lcu.getStatus());
			record.addValue("PROGRESS", lcu.getProgress());
		}

		List<LoanClosureUpload> details = new ArrayList<>();
		details.add(lcu);

		loanClosureUploadDAO.update(details);

		loanClosureUploadService.updateProcess(header, lcu, record);

		header.getUploadDetails().add(lcu);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setLoanClosureUploadDAO(LoanClosureUploadDAO loanClosureUploadDAO) {
		this.loanClosureUploadDAO = loanClosureUploadDAO;
	}

}
