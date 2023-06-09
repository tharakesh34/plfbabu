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

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffUploadDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.CommonHeader;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.receipt.constants.AllocationType;

public class CrossLoanKnockOffUploadProcessRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffUploadProcessRecord.class);

	@Autowired
	private CrossLoanKnockOffUploadDAO crossLoanKnockOffUploadDAO;
	@Autowired
	private UploadService crossLoanKnockOffUploadService;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private FinExcessAmountDAO finExcessAmountDAO;
	@Autowired
	private ManualAdviseDAO manualAdviseDAO;

	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource paramSource, Table table)
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
		Long recordSeq = (Long) paramSource.getValue("RecordSeq");

		clku.setHeaderId(headerID);
		clku.setRecordSeq(recordSeq);

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

			clku.setId(uploadID);

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

				if (CommonHeader.isValid(allocationType)) {
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
					paramSource.addValue("ERRORCODE", "9999");
					paramSource.addValue("ERRORDESC", "Fee Types are exceeded the limit");

					paramSource.addValue("STATUS", "F");
					paramSource.addValue("PROGRESS", EodConstants.PROGRESS_FAILED);

					continue;
				}
			}

			crossLoanKnockOffUploadDAO.saveAllocations(allocations);

			FinanceMain fromFm = financeMainDAO.getFinanceMainByRef(clku.getFromFinReference(), "", false);
			FinanceMain toFm = financeMainDAO.getFinanceMainByRef(clku.getToFinReference(), "", false);
			clku.setFromFm(fromFm);
			clku.setFromFinID(fromFm.getFinID());
			clku.setToFm(toFm);
			clku.setToFinID(toFm.getFinID());

			if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(clku.getExcessType())) {
				clku.setExcessList(
						finExcessAmountDAO.getExcessAmountsByRefAndType(fromFm.getFinID(), clku.getExcessType()));
			} else {
				if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(clku.getExcessType())) {
					List<ManualAdvise> mbList = manualAdviseDAO.getManualAdviseByRefAndFeeCode(fromFm.getFinID(),
							AdviseType.PAYABLE.id(), clku.getFeeTypeCode());

					clku.setAdvises(mbList);
				}
			}

			clku.setAllocations(crossLoanKnockOffUploadDAO.getAllocations(clku.getId(), clku.getHeaderId()));

			crossLoanKnockOffUploadService.doValidate(header, clku);

			if (clku.getProgress() == EodConstants.PROGRESS_FAILED) {
				paramSource.addValue("ERRORCODE", clku.getErrorCode());
				paramSource.addValue("ERRORDESC", clku.getErrorDesc());
			}

		} catch (AppException e) {
			clku.setStatus("F");
			clku.setProgress(EodConstants.PROGRESS_FAILED);

			paramSource.addValue("ERRORCODE", "9999");
			paramSource.addValue("ERRORDESC", e.getMessage());

			paramSource.addValue("STATUS", clku.getStatus());
			paramSource.addValue("PROGRESS", clku.getProgress());
		}

		List<CrossLoanKnockoffUpload> details = new ArrayList<>();
		details.add(clku);

		crossLoanKnockOffUploadDAO.update(details);

		crossLoanKnockOffUploadService.updateProcess(header, clku, paramSource);

		header.getUploadDetails().add(clku);

		logger.debug(Literal.LEAVING);
	}
}
