package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.receipts.CrossLoanKnockOffUploadDAO;
import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.receipt.constants.AllocationType;

public class CrossLoanKnockOffUploadProcessRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffUploadProcessRecord.class);

	private CrossLoanKnockOffUploadDAO crossLoanKnockOffUploadDAO;

	@Autowired
	private UploadService crossLoanKnockOffUploadService;

	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Sheet sheet = attributes.getSheet();

		Row headerRow = sheet.getRow(0);

		Row row = attributes.getRow();

		CrossLoanKnockoffUpload clku = new CrossLoanKnockoffUpload();

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		clku.setHeaderId(headerID);

		int readColumn = 0;

		Cell rowCell = null;
		try {
			for (Cell cell : headerRow) {
				rowCell = row.getCell(readColumn);

				if (cell.getColumnIndex() > 5) {
					break;
				}

				readColumn = cell.getColumnIndex() + 1;
				if (rowCell == null) {
					continue;
				}

				switch (cell.getColumnIndex()) {
				case 0:
					clku.setFromFinReference(rowCell.toString());
					break;
				case 1:
					clku.setToFinReference(rowCell.toString());
					break;
				case 2:
					clku.setExcessType(rowCell.toString());
					break;
				case 3:
					String strAmount = rowCell.toString();
					if (strAmount != null) {
						clku.setExcessAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
					}
					break;
				case 4:
					clku.setAllocationType(rowCell.toString());
					break;

				case 5:
					clku.setFeeTypeCode(rowCell.toString());
					break;
				default:
					break;
				}
			}

			if (StringUtils.isEmpty(clku.getAllocationType())) {
				clku.setAllocationType(AllocationType.AUTO);
			}

			long uploadID = crossLoanKnockOffUploadDAO.save(clku);

			List<CrossLoanKnockoffUpload> allocations = new ArrayList<>();

			int index = 0;
			for (Cell cell : headerRow) {

				if (index < readColumn) {
					index++;
					continue;
				}

				CrossLoanKnockoffUpload alloc = new CrossLoanKnockoffUpload();

				String allocationType = cell.toString();

				if (allocationType == null) {
					break;
				}

				if ("CreatedBy".equals(allocationType) || "CreatedOn".equals(allocationType)
						|| "ApprovedBy".equals(allocationType) || "ApprovedOn".equals(allocationType)
						|| "Status".equals(allocationType) || "ErrorCode".equals(allocationType)
						|| "ErrorDesc".equals(allocationType)) {
					continue;
				}

				alloc.setId(uploadID);
				alloc.setHeaderId(headerID);
				alloc.setCode(allocationType.toUpperCase());

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
							alloc.setAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
							allocations.add(alloc);
						}
					}
				}
				index++;

				if (index == 23) {
					throw new AppException("Fee Types are exceeded the limit");
				}
			}

			crossLoanKnockOffUploadDAO.saveAllocations(allocations);

			crossLoanKnockOffUploadService.doValidate(header, clku);

			if (clku.getProgress() == EodConstants.PROGRESS_FAILED) {
				record.addValue("ERRORCODE", clku.getErrorCode());
				record.addValue("ERRORDESC", clku.getErrorDesc());

				List<CrossLoanKnockoffUpload> details = new ArrayList<>();
				details.add(clku);

				crossLoanKnockOffUploadDAO.update(details);
			}

		} catch (AppException e) {
			header.setFailureRecords(header.getFailureRecords() + 1);
			clku.setStatus("F");
			clku.setProgress(EodConstants.PROGRESS_FAILED);

			record.addValue("ERRORCODE", "9999");
			record.addValue("ERRORDESC", e.getMessage());

			record.addValue("STATUS", clku.getStatus());
			record.addValue("PROGRESS", clku.getProgress());
		}

		crossLoanKnockOffUploadService.updateProcess(header, clku, record);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setCrossLoanKnockOffUploadDAO(CrossLoanKnockOffUploadDAO crossLoanKnockOffUploadDAO) {
		this.crossLoanKnockOffUploadDAO = crossLoanKnockOffUploadDAO;
	}

}
