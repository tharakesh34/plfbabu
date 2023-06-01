package com.pennant.pff.lpp.upload.validate;

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
import org.zkoss.util.resource.Labels;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lpp.upload.LPPUpload;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.lpp.PenaltyTypes;
import com.pennant.pff.lpp.dao.LPPUploadDAO;
import com.pennant.pff.lpp.service.impl.LPPUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.model.UploadDetails;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class LPPUploadProcessRecord implements ProcessRecord {
	private static final Logger logger = LogManager.getLogger(LPPUploadProcessRecord.class);

	private LPPUploadDAO lppUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;

	@Autowired
	private UploadService lPPUploadService;

	public LPPUploadProcessRecord() {
		super();
	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Sheet sheet = attributes.getSheet();

		Row headerRow = sheet.getRow(0);

		Row row = attributes.getRow();

		LPPUpload lpp = new LPPUpload();

		Long headerID = (Long) attributes.getParameterMap().get("HEADER_ID");

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");
		Long recordSeq = (Long) record.getValue("RecordSeq");

		lpp.setHeaderId(headerID);
		lpp.setRecordSeq(recordSeq);

		int readColumn = 0;

		Cell rowCell = null;
		try {

			for (Cell cell : headerRow) {
				rowCell = row.getCell(readColumn);

				if (cell.getColumnIndex() > 10) {
					break;
				}

				readColumn = cell.getColumnIndex() + 1;
				if (rowCell == null) {
					continue;
				}

				switch (cell.getColumnIndex()) {
				case 0:
					lpp.setLoanType(rowCell.toString());
					break;
				case 1:
					lpp.setApplyToExistingLoans(rowCell.toString());
					break;
				case 2:
					lpp.setApplyOverDue(rowCell.toString());
					break;
				case 3:
					lpp.setPenaltyType(rowCell.toString());
					break;
				case 4:
					lpp.setIncludeGraceDays(rowCell.toString());
					break;
				case 5:
					String gracedays = rowCell.toString();
					if (StringUtils.isNotEmpty(gracedays)) {
						lpp.setGraceDays(Integer.parseInt(gracedays));
					}
					break;
				case 6:
					lpp.setCalculatedOn(rowCell.toString());
					break;
				case 7:
					String amountorpercent = rowCell.toString();
					if (amountorpercent != null) {
						lpp.setAmountOrPercent(PennantApplicationUtil.unFormateAmount(amountorpercent, 2));
					}
					break;
				case 8:
					lpp.setAllowWaiver(rowCell.toString());
					break;
				case 9:
					String maxwaiver = rowCell.toString();
					if (maxwaiver != null) {
						lpp.setMaxWaiver(PennantApplicationUtil.unFormateAmount(maxwaiver, 0));
					}
					break;
				case 10:
					String odminamount = rowCell.toString();
					if (odminamount != null) {
						lpp.setODMinAmount(PennantApplicationUtil.unFormateAmount(odminamount, 2));
					}
					break;

				default:
					break;
				}
			}

			validate(header, lpp);

			lppUploadDAO.save(lpp);

			if (PennantConstants.YES.equals(lpp.getApplyToExistingLoans())) {
				int totalRecords = lppUploadDAO.saveByFinType(lpp);
				header.setTotalRecords(header.getTotalRecords() + totalRecords);
				lppUploadDAO.updateTotals(header.getId(), header.getTotalRecords());
			}

			if (lpp.getProgress() == EodConstants.PROGRESS_FAILED) {
				lpp.setStatus("F");
				record.addValue("STATUS", lpp.getStatus());
				record.addValue("ERRORCODE", lpp.getErrorCode());
				record.addValue("ERRORDESC", lpp.getErrorDesc());

				List<LPPUpload> details = new ArrayList<>();
				details.add(lpp);

				lppUploadDAO.update(details);
			}
		} catch (AppException e) {
			header.setFailureRecords(header.getFailureRecords() + 1);
			lpp.setStatus("F");
			lpp.setProgress(EodConstants.PROGRESS_FAILED);

			record.addValue("ERRORCODE", "9999");
			record.addValue("ERRORDESC", e.getMessage());

			record.addValue("STATUS", lpp.getStatus());
			record.addValue("PROGRESS", lpp.getProgress());
		}

		lPPUploadService.updateProcess(header, lpp, record);

		header.getUploadDetails().add(lpp);

		logger.debug(Literal.LEAVING);
	}

	public void validate(FileUploadHeader header, LPPUpload detail) {

		String reference = detail.getReference();
		detail.setHeaderId(header.getId());
		String loanType = detail.getLoanType();

		doBasicValidations(detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			return;
		}

		if (StringUtils.isNotBlank(reference)) {
			validateLoan(header, detail);
		}

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			return;
		}

		validateApplyOverDue(detail);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			return;
		}

		if (StringUtils.isNotBlank(loanType) && !lppUploadDAO.isValidFinType(loanType)) {
			setError(detail, LPPUploadError.LPP_13);
			return;
		}
		setSuccesStatus(detail);
	}

	@Autowired
	public void setLPPUploadDAO(LPPUploadDAO lppUploadDAO) {
		this.lppUploadDAO = lppUploadDAO;
	}

	private void setSuccesStatus(UploadDetails lpp) {
		lpp.setProgress(EodConstants.PROGRESS_SUCCESS);
		lpp.setStatus("S");
		lpp.setErrorCode(null);
		lpp.setErrorDesc(null);
	}

	private void doBasicValidations(LPPUpload detail) {

		BigDecimal maxWaiver = detail.getMaxWaiver();
		BigDecimal minAmount = detail.getODMinAmount();
		BigDecimal amountOrPercent = detail.getAmountOrPercent();

		if (maxWaiver == null) {
			maxWaiver = BigDecimal.ZERO;
		}

		if (amountOrPercent == null) {
			amountOrPercent = BigDecimal.ZERO;
		}

		if (PennantConstants.NO.equals(detail.getApplyOverDue())
				&& (StringUtils.isNotBlank(detail.getReference()) || StringUtils.isNotBlank(detail.getLoanType()))) {
			if (StringUtils.isNotBlank(detail.getCalculatedOn())
					|| (StringUtils.isNotBlank(detail.getIncludeGraceDays()))
					|| (StringUtils.isNotBlank(detail.getPenaltyType()))
					|| StringUtils.isNotBlank(detail.getAllowWaiver()) || (maxWaiver.compareTo(BigDecimal.ZERO)) > 0
					|| detail.getGraceDays() > 0 || amountOrPercent.compareTo(BigDecimal.ZERO) > 0
					|| minAmount.compareTo(BigDecimal.ZERO) > 0) {
				setError(detail, LPPUploadError.LPP_09);
				return;
			}
		}

		boolean applyToExistingLoans = PennantConstants.YES.equals(detail.getApplyToExistingLoans());
		if (StringUtils.isNotBlank(detail.getLoanType())
				&& !(PennantConstants.NO.equals(detail.getApplyToExistingLoans()) || applyToExistingLoans)) {
			setError(detail, LPPUploadError.LPP_14);
			return;
		}

		if (financeTypeDAO.getFinTypeCount(detail.getLoanType(), "_Temp") > 0) {
			setError(detail, LPPUploadError.LPP_28);
			return;
		}
	}

	private void validateApplyOverDue(LPPUpload detail) {

		if (StringUtils.isBlank(detail.getApplyOverDue())) {
			setError(detail, LPPUploadError.LPP_03);
			return;
		}

		boolean applyOverDue = PennantConstants.YES.equals(detail.getApplyOverDue());
		if (!(PennantConstants.NO.equals(detail.getApplyOverDue()) || applyOverDue)) {
			setError(detail, LPPUploadError.LPP_04);
			return;
		}

		if (PennantConstants.YES.equals(detail.getApplyOverDue())) {

			validateIncludeGraceDays(detail);

			if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
				return;
			}

			validatePenaltyType(detail);

			if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
				return;
			}

		}
	}

	private void validateIncludeGraceDays(LPPUpload detail) {
		boolean allowWaiver = PennantConstants.YES.equals(detail.getAllowWaiver());
		boolean includeGraceDays = PennantConstants.YES.equals(detail.getIncludeGraceDays());
		BigDecimal maxWaiver = detail.getMaxWaiver();

		if (!(PennantConstants.NO.equals(detail.getAllowWaiver()) || allowWaiver)) {
			setError(detail, LPPUploadError.LPP_20);
			return;
		}

		if (!(PennantConstants.NO.equals(detail.getIncludeGraceDays()) || includeGraceDays)) {
			setError(detail, LPPUploadError.LPP_19);
			return;
		}

		if (detail.getGraceDays() < 0 || detail.getGraceDays() > 999) {
			setError(detail, LPPUploadError.LPP_15);
			return;
		}

		if (allowWaiver && StringUtils.isBlank(String.valueOf(maxWaiver))) {
			setError(detail, LPPUploadError.LPP_18);
			return;
		} else if (allowWaiver
				&& (maxWaiver.compareTo(BigDecimal.ZERO) <= 0 || maxWaiver.compareTo(new BigDecimal(100)) > 0)) {
			setError(detail, LPPUploadError.LPP_10);
			return;
		}

		if (StringUtils.isNotBlank(String.valueOf(maxWaiver)) && !allowWaiver
				&& maxWaiver.compareTo(BigDecimal.ZERO) > 0) {
			setError(detail, LPPUploadError.LPP_11);
			return;
		}
	}

	private void validatePenaltyType(LPPUpload detail) {

		String calculatedOn = detail.getCalculatedOn();
		String penaltyType = detail.getPenaltyType();
		BigDecimal amountOrPercent = detail.getAmountOrPercent();
		amountOrPercent = amountOrPercent.divide(new BigDecimal(100));

		PenaltyTypes lppType = PenaltyTypes.getTypes(penaltyType);

		if (lppType == null) {
			setError(detail, LPPUploadError.LPP_05);
			return;
		}

		switch (lppType) {
		case FLAT, FLAT_ON_PD_MTH:
			if ((amountOrPercent.compareTo(BigDecimal.ZERO)) < 0
					|| (amountOrPercent.compareTo(new BigDecimal(9999999)) > 0)) {
				setError(detail, LPPUploadError.LPP_07);
				return;
			}

			if (StringUtils.isNotBlank(calculatedOn)) {
				setError(detail, LPPUploadError.LPP_22);
				return;
			}

			if (!PennantConstants.NO.equals(detail.getIncludeGraceDays())) {
				setError(detail, LPPUploadError.LPP_27);
				return;
			}

			break;
		case PERC_ONE_TIME, PERC_ON_PD_MTH, PERC_ON_DUE_DAYS, PERC_ON_EFF_DUE_DAYS:
			if ((amountOrPercent.compareTo(BigDecimal.ZERO)) <= 0
					|| (amountOrPercent.compareTo(new BigDecimal(100)) > 0)) {
				setError(detail, LPPUploadError.LPP_08);
				return;
			}

			if (StringUtils.isBlank(calculatedOn)) {
				setError(detail, LPPUploadError.LPP_23);
				return;
			}

			if (!(FinanceConstants.ODCALON_STOT.equals(calculatedOn)
					|| FinanceConstants.ODCALON_SPRI.equals(calculatedOn)
					|| FinanceConstants.ODCALON_SPFT.equals(calculatedOn)
					|| FinanceConstants.ODCALON_INST.equals(calculatedOn))) {
				setError(detail, LPPUploadError.LPP_06);
				return;
			}

			break;
		default:
			break;
		}

		boolean isLppType = PenaltyTypes.PERC_ONE_TIME.equals(lppType) || PenaltyTypes.PERC_ON_PD_MTH.equals(lppType);

		if (!isLppType && FinanceConstants.ODCALON_INST.equals(calculatedOn)) {
			setError(detail, LPPUploadError.LPP_25);
			return;
		}

		if (isLppType && StringUtils.isBlank(String.valueOf(detail.getODMinAmount()))) {
			setError(detail, LPPUploadError.LPP_26);
			return;
		}

		if (isLppType && !PennantConstants.NO.equals(detail.getIncludeGraceDays())) {
			setError(detail, LPPUploadError.LPP_27);
			return;
		}

	}

	private void validateLoan(FileUploadHeader header, LPPUpload detail) {
		FinanceMain fm = financeMainDAO.getFinanceMain(detail.getReference(), header.getEntityCode());
		Long finID = fm.getFinID();

		if (fm == null || finID == null) {
			setError(detail, LPPUploadError.LPP_02);
			return;
		}

		detail.setReferenceID(finID);

		if (!fm.isFinIsActive()) {
			setError(detail, LPPUploadError.LPP_12);
			return;
		}

		String rcdMntnSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID);
		if (StringUtils.isNotEmpty(rcdMntnSts)) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode("LPP_24");
			detail.setErrorDesc(Labels.getLabel("Finance_Inprogresss_" + rcdMntnSts));
			return;
		}
	}

	private void setError(LPPUpload detail, LPPUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

}