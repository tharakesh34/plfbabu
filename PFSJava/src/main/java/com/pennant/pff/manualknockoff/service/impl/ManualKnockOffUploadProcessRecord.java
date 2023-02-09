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
import com.pennant.pff.manualknockoff.dao.ManualKnockOffUploadDAO;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.model.knockoff.ManualKnockOffUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class ManualKnockOffUploadProcessRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(ManualKnockOffUploadProcessRecord.class);

	private ManualKnockOffUploadDAO manualKnockOffUploadDAO;

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

		int readColumn = 0;

		Cell rowCell = null;

		for (Cell cell : headerRow) {
			switch (cell.getColumnIndex()) {
			case 0:
				rowCell = row.getCell(readColumn);
				mku.setReference(rowCell.toString());
				readColumn = cell.getColumnIndex() + 1;
				break;
			case 1:
				rowCell = row.getCell(readColumn);
				mku.setExcessType(rowCell.toString());
				readColumn = cell.getColumnIndex() + 1;
				break;
			case 2:
				rowCell = row.getCell(readColumn);
				mku.setAllocationType(rowCell.toString());
				readColumn = cell.getColumnIndex() + 1;
				break;
			case 3:
				rowCell = row.getCell(readColumn);
				String strAmount = rowCell.toString();
				if (strAmount != null) {
					mku.setReceiptAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
				}
				readColumn = cell.getColumnIndex() + 1;
				break;
			case 4:
				rowCell = row.getCell(readColumn);
				if (rowCell != null) {
					String strAdviseID = rowCell.toString();
					if (strAdviseID != null && StringUtils.isNotEmpty(strAdviseID)) {
						mku.setAdviseId(Long.valueOf(strAdviseID));
					}
				}
				break;
			}
		}

		long uploadID = manualKnockOffUploadDAO.save(mku);

		List<ManualKnockOffUpload> allocations = new ArrayList<>();

		int index = 0;
		for (Cell cell : headerRow) {

			if (index < 5) {
				index++;
				continue;
			}

			ManualKnockOffUpload alloc = new ManualKnockOffUpload();

			String allocationType = cell.toString();

			if (allocationType == null) {
				break;
			}

			alloc.setId(uploadID);
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

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setManualKnockOffUploadDAO(ManualKnockOffUploadDAO manualKnockOffUploadDAO) {
		this.manualKnockOffUploadDAO = manualKnockOffUploadDAO;
	}

}