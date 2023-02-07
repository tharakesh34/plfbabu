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

		Row row = attributes.getRow();

		ManualKnockOffUpload mku = new ManualKnockOffUpload();

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		mku.setHeaderId(headerID);

		for (Cell cell : row) {
			switch (cell.getColumnIndex()) {
			case 0:
				mku.setReference(cell.toString());
				break;
			case 1:
				mku.setExcessType(cell.toString());
				break;
			case 2:
				mku.setAllocationType(cell.toString());
				break;
			case 3:
				String strAmount = cell.toString();
				if (strAmount != null) {
					mku.setReceiptAmount(PennantApplicationUtil.unFormateAmount(strAmount, 2));
				}
				break;
			case 4:
				String strAdviseID = cell.toString();
				if (StringUtils.isNotEmpty(strAdviseID)) {
					mku.setAdviseId(Long.valueOf(strAdviseID));
				}
				break;
			}
		}

		long uploadID = manualKnockOffUploadDAO.save(mku);

		Sheet sheet = attributes.getSheet();

		Row headerRow = sheet.getRow(0);

		List<ManualKnockOffUpload> allocations = new ArrayList<>();

		int index = 0;
		for (Cell cell : row) {
			if (index < 5) {
				index++;
				continue;
			}

			ManualKnockOffUpload alloc = new ManualKnockOffUpload();
			Cell headerCell = headerRow.getCell(index);

			String allocationType = headerCell.toString();

			if (allocationType == null) {
				break;
			}

			alloc.setId(uploadID);
			alloc.setCode(allocationType.toUpperCase());

			String strAmount = cell.toString();

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