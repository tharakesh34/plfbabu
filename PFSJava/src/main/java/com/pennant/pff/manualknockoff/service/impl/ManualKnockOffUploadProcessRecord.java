package com.pennant.pff.manualknockoff.service.impl;

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

import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.manualknockoff.dao.ManualKnockOffUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.model.knockoff.ManualKnockOffUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.receipt.constants.AllocationType;

public class ManualKnockOffUploadProcessRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(ManualKnockOffUploadProcessRecord.class);

	private ManualKnockOffUploadDAO manualKnockOffUploadDAO;
	@Autowired
	private UploadService manualKnockOffUploadServiceImpl;

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Sheet sheet = attributes.getSheet();

		Row headerRow = sheet.getRow(0);

		Row row = attributes.getRow();

		ManualKnockOffUpload mku = new ManualKnockOffUpload();

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		mku.setHeaderId(headerID);

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		int readColumn = 0;

		Cell rowCell = null;

		for (Cell cell : headerRow) {
			rowCell = row.getCell(readColumn);

			if (cell.getColumnIndex() > 4) {
				break;
			}

			readColumn = cell.getColumnIndex() + 1;
			if (rowCell == null) {
				continue;
			}

			switch (cell.getColumnIndex()) {
			case 0:
				mku.setReference(rowCell.toString());
				break;
			case 1:
				mku.setExcessType(rowCell.toString());
				break;
			case 2:
				mku.setAllocationType(rowCell.toString());
				break;
			case 3:
				String strAmount = rowCell.toString();
				if (strAmount != null) {
					mku.setReceiptAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
				}
				break;
			case 4:
				String strAdviseID = rowCell.toString();
				if (strAdviseID != null && StringUtils.isNotEmpty(strAdviseID)) {
					mku.setAdviseId(Long.valueOf(strAdviseID));
				}
				break;
			default:
				break;
			}
		}

		if (StringUtils.isEmpty(mku.getAllocationType())) {
			mku.setAllocationType(AllocationType.AUTO);
		}

		long uploadID = manualKnockOffUploadDAO.save(mku);
		mku.setId(uploadID);

		List<ManualKnockOffUpload> allocations = new ArrayList<>();

		int index = 0;
		for (Cell cell : headerRow) {

			if (index < readColumn) {
				index++;
				continue;
			}

			ManualKnockOffUpload alloc = new ManualKnockOffUpload();

			String allocationType = cell.toString();

			if (allocationType == null) {
				break;
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

		manualKnockOffUploadDAO.saveAllocations(allocations);

		manualKnockOffUploadServiceImpl.doValidate(header, mku);

		if (mku.getProgress() == EodConstants.PROGRESS_FAILED) {
			record.addValue("ERRORCODE", mku.getErrorCode());
			record.addValue("ERRORDESC", mku.getErrorDesc());

			List<ManualKnockOffUpload> details = new ArrayList<>();
			details.add(mku);

			manualKnockOffUploadDAO.update(details);
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setManualKnockOffUploadDAO(ManualKnockOffUploadDAO manualKnockOffUploadDAO) {
		this.manualKnockOffUploadDAO = manualKnockOffUploadDAO;
	}

}