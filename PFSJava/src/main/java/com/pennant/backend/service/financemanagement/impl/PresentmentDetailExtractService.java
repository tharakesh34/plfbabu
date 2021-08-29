package com.pennant.backend.service.financemanagement.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;

public class PresentmentDetailExtractService {
	private static final Logger logger = LogManager.getLogger(PresentmentDetailExtractService.class);

	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ChequeDetailDAO chequeDetailDAO;

	public PresentmentDetailExtractService(PresentmentDetailDAO presentmentDetailDAO,
			FinExcessAmountDAO finExcessAmountDAO, ChequeDetailDAO chequeDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
		this.finExcessAmountDAO = finExcessAmountDAO;
		this.chequeDetailDAO = chequeDetailDAO;
	}

	private void save(PresentmentHeader ph) {
		List<PresentmentDetail> presentments = ph.getPresentments();
		List<FinExcessAmount> excess = new ArrayList<>();
		List<FinExcessAmount> excessRevarsal = new ArrayList<>();
		List<FinExcessMovement> excessMovement = new ArrayList<>();
		List<PresentmentDetail> includeList = new ArrayList<>();
		List<FinExcessAmount> emiInAdvance = new ArrayList<>();

		for (PresentmentDetail pd : presentments) {
			Long headerId = null;
			String key = null;
			if (ph.isGroupByBank() && isGroupByPartnerBank(ph)) {
				key = getKeyByPBAndBank(pd);
			} else if (ph.isGroupByBank()) {
				key = getKeyByBank(pd);
			} else if (isGroupByPartnerBank(ph)) {
				key = getKeyByPB(pd);
			} else {
				key = getDefaultGroupKey(pd);
			}

			headerId = getHeaderId(key, ph);

			if (headerId == null) {
				ph.setId(Long.MIN_VALUE);
				ph.setSchdate(pd.getDefSchdDate());
				ph.setEntityCode(pd.getEntityCode());
				ph.setBankCode(pd.getBankCode());
				ph.setPartnerBankId(pd.getPartnerBankId());
				headerId = savePresentmentHeaderDetails(ph);
				ph.getGroups().put(key, headerId);
			}

			pd.setHeaderId(headerId);

			if (pd.getExcessAmountReversal() != null) {
				excessRevarsal.add(pd.getExcessAmountReversal());
				excessMovement.add(pd.getExcessAmountReversal().getExcessMovement());
			}

			if (pd.getExcessAmount() != null) {
				excess.add(pd.getExcessAmount());
			}
			if (pd.getEmiInAdvance() != null) {
				emiInAdvance.add(pd.getEmiInAdvance());
			}
		}

		if (!excess.isEmpty()) {
			finExcessAmountDAO.updateExcessAmtList(excess);
		}

		if (!excessRevarsal.isEmpty()) {
			finExcessAmountDAO.updateExcessReserveList(excessRevarsal);
		}

		if (!excessMovement.isEmpty()) {
			finExcessAmountDAO.saveExcessMovementList(excessMovement);
		}

		if (!emiInAdvance.isEmpty()) {
			finExcessAmountDAO.updateExcessEMIAmount(emiInAdvance, "R");
		}
		presentmentDetailDAO.saveList(presentments);

		presentments.stream().filter(pd -> RepayConstants.PEXC_EMIINCLUDE == pd.getExcludeReason())
				.forEach(includeList::add);

		if (includeList.isEmpty()) {
			return;
		}

		presentmentDetailDAO.updateSchdWithPresentmentId(includeList);
		if (MandateConstants.TYPE_PDC.equals(ph.getMandateType())) {
			chequeDetailDAO.updateChequeStatus(includeList);
		}
	}

	private boolean isGroupByPartnerBank(PresentmentHeader ph) {
		if (ph.isGroupByPartnerBank() && !MandateConstants.TYPE_PDC.equals(ph.getMandateType())) {
			return true;
		}
		return false;
	}

	public void processPresentment(PresentmentHeader ph, ResultSet rs) throws SQLException {
		List<PresentmentDetail> presentments = ph.getPresentments();

		ph.setSchdate(rs.getDate("DEFSCHDDATE"));
		ph.setBankCode(rs.getString("BANKCODE"));
		ph.setEntityCode(rs.getString("ENTITYCODE"));
		ph.setPartnerBankId(rs.getLong("PARTNERBANKID"));
		PresentmentDetail pd = new PresentmentDetail();
		pd.setBankCode(rs.getString("BANKCODE"));
		pd.setPartnerBankId(rs.getLong("PARTNERBANKID"));
		pd.setEntityCode(rs.getString("ENTITYCODE"));
		pd.setPresentmentAmt(BigDecimal.ZERO);
		pd.setStatus(RepayConstants.PEXC_IMPORT);
		pd.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);
		pd.setPresentmentRef(getPresentmentRef(rs));

		// Schedule Setup
		pd.setFinReference(rs.getString("FINREFERENCE"));
		pd.setSchDate(rs.getDate("SCHDATE"));
		pd.setEmiNo(rs.getInt("EMINO"));
		pd.setSchSeq(rs.getInt("SCHSEQ"));
		pd.setDefSchdDate(rs.getDate("DEFSCHDDATE"));

		BigDecimal schAmtDue = rs.getBigDecimal("PROFITSCHD").add(rs.getBigDecimal("PRINCIPALSCHD"))
				.add(rs.getBigDecimal("FEESCHD")).subtract(rs.getBigDecimal("SCHDPRIPAID"))
				.subtract(rs.getBigDecimal("SCHDPFTPAID")).subtract(rs.getBigDecimal("SCHDFEEPAID"))
				.subtract(rs.getBigDecimal("TDSAMOUNT"));

		if (BigDecimal.ZERO.compareTo(schAmtDue) >= 0) {
			presentments.add(pd);
			return;
		}

		pd.setSchAmtDue(schAmtDue);
		pd.setSchPriDue(rs.getBigDecimal("PRINCIPALSCHD").subtract(rs.getBigDecimal("SCHDPRIPAID")));
		pd.setSchPftDue(rs.getBigDecimal("PROFITSCHD").subtract(rs.getBigDecimal("SCHDPFTPAID")));
		pd.setSchFeeDue(rs.getBigDecimal("FEESCHD").subtract(rs.getBigDecimal("SCHDFEEPAID")));
		pd.settDSAmount(rs.getBigDecimal("TDSAMOUNT"));
		pd.setSchInsDue(BigDecimal.ZERO);
		pd.setSchPenaltyDue(BigDecimal.ZERO);
		pd.setAdvanceAmt(schAmtDue);
		pd.setAdviseAmt(BigDecimal.ZERO);
		pd.setExcessID(0);
		pd.setReceiptID(0);

		// Mandate Details
		pd.setMandateId(rs.getLong("MANDATEID"));
		pd.setMandateExpiryDate(rs.getDate("EXPIRYDATE"));
		pd.setMandateStatus(rs.getString("STATUS"));
		pd.setMandateType(rs.getString("MANDATETYPE"));

		pd.setVersion(0);
		pd.setLastMntBy(ph.getLastMntBy());
		pd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		pd.setWorkflowId(0);

		if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
			pd.setGrcAdvType(rs.getString("GRCADVTYPE"));
			pd.setAdvType(rs.getString("ADVTYPE"));
			pd.setAdvStage(rs.getString("ADVSTAGE"));
			pd.setGrcPeriodEndDate(rs.getDate("GRCPERIODENDDATE"));
			pd.setBpiOrHoliday(rs.getString("BPIORHOLIDAY"));
			pd.setBpiTreatment(rs.getString("BPITREATMENT"));
		}

		doCalculations(ph, pd);

		presentments.add(pd);

		if (presentments.size() >= 100) {
			save(ph);
			presentments.clear();
		}
	}

	public void processPDCPresentment(PresentmentHeader ph, ResultSet rs) throws SQLException {
		List<PresentmentDetail> presentments = ph.getPresentments();

		ph.setSchdate(rs.getDate("DEFSCHDDATE"));
		ph.setBankCode(rs.getString("BANKCODE"));
		ph.setEntityCode(rs.getString("ENTITYCODE"));
		PresentmentDetail pd = new PresentmentDetail();
		pd.setEntityCode(rs.getString("ENTITYCODE"));
		pd.setBankCode(rs.getString("BANKCODE"));
		pd.setHeaderId(ph.getId());
		pd.setPresentmentAmt(BigDecimal.ZERO);
		pd.setStatus(RepayConstants.PEXC_IMPORT);
		pd.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);
		pd.setPresentmentRef(getPresentmentRef(rs));

		// Schedule Setup
		pd.setFinReference(rs.getString("FINREFERENCE"));
		pd.setSchDate(rs.getDate("SCHDATE"));
		pd.setEmiNo(rs.getInt("EMINO"));
		pd.setSchSeq(rs.getInt("SCHSEQ"));
		pd.setDefSchdDate(rs.getDate("DEFSCHDDATE"));

		BigDecimal schAmtDue = rs.getBigDecimal("PROFITSCHD").add(rs.getBigDecimal("PRINCIPALSCHD"))
				.add(rs.getBigDecimal("FEESCHD")).subtract(rs.getBigDecimal("SCHDPRIPAID"))
				.subtract(rs.getBigDecimal("SCHDPFTPAID")).subtract(rs.getBigDecimal("SCHDFEEPAID"))
				.subtract(rs.getBigDecimal("TDSAMOUNT"));
		if (BigDecimal.ZERO.compareTo(schAmtDue) >= 0) {
			presentments.add(pd);
			return;
		}

		pd.setSchAmtDue(schAmtDue);
		pd.setSchPriDue(rs.getBigDecimal("PRINCIPALSCHD").subtract(rs.getBigDecimal("SCHDPRIPAID")));
		pd.setSchPftDue(rs.getBigDecimal("PROFITSCHD").subtract(rs.getBigDecimal("SCHDPFTPAID")));
		pd.setSchFeeDue(rs.getBigDecimal("FEESCHD").subtract(rs.getBigDecimal("SCHDFEEPAID")));
		pd.settDSAmount(rs.getBigDecimal("TDSAMOUNT"));
		pd.setSchInsDue(BigDecimal.ZERO);
		pd.setSchPenaltyDue(BigDecimal.ZERO);
		// pDetail.setAdvanceAmt(schAmtDue);
		pd.setAdviseAmt(BigDecimal.ZERO);
		pd.setExcessID(0);
		pd.setReceiptID(0);

		// PDC Details
		pd.setMandateId(rs.getLong("CHEQUEDETAILSID"));
		pd.setMandateExpiryDate(rs.getDate("CHEQUEDATE"));
		pd.setMandateStatus(rs.getString("STATUS"));
		pd.setMandateType(rs.getString("MANDATETYPE"));

		pd.setVersion(0);
		pd.setLastMntBy(ph.getLastMntBy());
		pd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		pd.setWorkflowId(0);

		if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
			pd.setGrcAdvType(rs.getString("GRCADVTYPE"));
			pd.setAdvType(rs.getString("ADVTYPE"));
			pd.setGrcPeriodEndDate(rs.getDate("GRCPERIODENDDATE"));
			pd.setBpiOrHoliday(rs.getString("BPIORHOLIDAY"));
			pd.setBpiTreatment(rs.getString("BPITREATMENT"));
		}

		doPDCCalculations(ph, pd);

		presentments.add(pd);

		if (presentments.size() >= 100) {
			save(ph);
			presentments.clear();
		}
	}

	private Long getHeaderId(String key, PresentmentHeader ph) {
		return ph.getGroups().get(key);

	}

	private String getKeyByBank(PresentmentDetail ph) {
		Date defSchDate = ph.getDefSchdDate();
		String bankCode = ph.getBankCode();
		String entity = ph.getEntityCode();

		StringBuilder key = new StringBuilder();

		key.append(DateUtil.formatToShortDate(defSchDate));
		key.append(bankCode);
		key.append(entity);
		return key.toString();
	}

	private String getKeyByPB(PresentmentDetail ph) {
		long partnerBankId = ph.getPartnerBankId();
		Date defSchDate = ph.getDefSchdDate();
		String entity = ph.getEntityCode();

		StringBuilder key = new StringBuilder();

		key.append(DateUtil.formatToShortDate(defSchDate));
		key.append(entity);
		key.append(partnerBankId);
		return key.toString();
	}

	private String getPartnerBankWithNonPDCGroupKey(PresentmentDetail ph) {
		long partnerBankId = ph.getPartnerBankId();
		Date defSchDate = ph.getDefSchdDate();
		String entity = ph.getEntityCode();

		StringBuilder key = new StringBuilder();

		key.append(DateUtil.formatToShortDate(defSchDate));
		key.append(entity);
		key.append(partnerBankId);
		return key.toString();
	}

	private String getDefaultGroupKey(PresentmentDetail ph) {
		Date defSchDate = ph.getDefSchdDate();
		String entity = ph.getEntityCode();

		StringBuilder key = new StringBuilder();

		key.append(DateUtil.formatToShortDate(defSchDate));
		key.append(entity);
		return key.toString();
	}

	private String getKeyByPBAndBank(PresentmentDetail ph) {
		long partnerBankId = ph.getPartnerBankId();
		Date defSchDate = ph.getDefSchdDate();
		String bankCode = ph.getBankCode();
		String entity = ph.getEntityCode();

		StringBuilder key = new StringBuilder();

		key.append(DateUtil.formatToShortDate(defSchDate));
		key.append(bankCode);
		key.append(entity);
		key.append(partnerBankId);
		return key.toString();
	}

	public String extarctPresentments(PresentmentHeader ph) {
		ph.setBpiPaidOnInstDate(SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE));
		ph.setGroupByBank(SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK));
		ph.setGroupByPartnerBank(ImplementationConstants.GROUP_BATCH_BY_PARTNERBANK);
		try {
			if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
				presentmentDetailDAO.extactPresentments(ph, this);
			} else if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_REPRESENTMENT, ph.getPresentmentType())) {
				presentmentDetailDAO.extactRePresentments(ph, this);
			}
			if (!ph.getPresentments().isEmpty()) {
				save(ph);
			}
			if (ph.getGroups().isEmpty()) {
				return PennantJavaUtil.getLabel("label_PresentmentSearchMessage");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return PennantJavaUtil.getLabel("label_PresentmentExtractedMessage");
	}

	public String extarctPDCPresentments(PresentmentHeader ph) {
		ph.setBpiPaidOnInstDate(SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE));
		ph.setGroupByBank(SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK));

		if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
			presentmentDetailDAO.extactPDCPresentments(ph, this);
		} else if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_REPRESENTMENT, ph.getPresentmentType())) {
			presentmentDetailDAO.extactPDCRePresentments(ph, this);
		}

		if (!ph.getPresentments().isEmpty()) {
			save(ph);
		}

		if (ph.getGroups().isEmpty()) {
			return PennantJavaUtil.getLabel("label_PresentmentSearchMessage");
		}

		return PennantJavaUtil.getLabel("label_PresentmentExtractedMessage");
	}

	private void doPDCCalculations(PresentmentHeader ph, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		BigDecimal emiInAdvanceAmt;
		String finReference = pd.getFinReference();

		// Cheque Status
		if (!PennantConstants.CHEQUESTATUS_NEW.equals(pd.getMandateStatus())) {
			if (PennantConstants.CHEQUESTATUS_PRESENT.equals(pd.getMandateStatus())) {
				pd.setExcludeReason(RepayConstants.CHEQUESTATUS_PRESENT);
				return;
			}
			if (PennantConstants.CHEQUESTATUS_REALISE.equals(pd.getMandateStatus())) {
				pd.setExcludeReason(RepayConstants.CHEQUESTATUS_REALISE);
				return;
			}
			if (PennantConstants.CHEQUESTATUS_REALISED.equals(pd.getMandateStatus())) {
				pd.setExcludeReason(RepayConstants.CHEQUESTATUS_REALISED);
				return;
			}
			// COMMENTED THIS CODE FOR REPRESENTMENT PROCESS i.e, if Check got bounced also it shouldbe allowed for
			// Representment
			/*
			 * if (PennantConstants.CHEQUESTATUS_BOUNCE.equals(presentmentDetail.getMandateStatus())) {
			 * presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_BOUNCE); }
			 */
		}

		// EMI HOLD
		if (pd.getDefSchdDate().compareTo(pd.getDefSchdDate()) > 0) {
			pd.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		// at first advance interest and EMI then EMI advance
		if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
			processAdvAmounts(ph, pd);
		}

		// EMI IN ADVANCE
		// if there is no due no need to proceed further.
		if (pd.getSchAmtDue().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		// EMI IN ADVANCE
		FinExcessAmount excessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);
		if (excessAmount != null) {
			emiInAdvanceAmt = excessAmount.getBalanceAmt();

			if ((emiInAdvanceAmt.compareTo(BigDecimal.ZERO) > 0) && emiInAdvanceAmt.compareTo(pd.getSchAmtDue()) >= 0) {
				pd.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
				pd.setPresentmentAmt(BigDecimal.ZERO);
				pd.setAdvanceAmt(pd.getSchAmtDue());
				pd.setExcessID(excessAmount.getExcessID());
				pd.setExcessAmount(excessAmount);
			} else {
				pd.setPresentmentAmt(pd.getSchAmtDue());
				pd.setAdvanceAmt(BigDecimal.ZERO);

			}
		} else {
			pd.setPresentmentAmt(pd.getSchAmtDue());
			pd.setAdvanceAmt(BigDecimal.ZERO);
		}

		BigDecimal advAmount = pd.getAdvAdjusted();
		pd.setPresentmentAmt(pd.getPresentmentAmt().subtract(advAmount));
		pd.setAdvanceAmt(pd.getAdvanceAmt().add(advAmount));

		logger.debug(Literal.LEAVING);
	}

	private void doCalculations(PresentmentHeader ph, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		BigDecimal emiInAdvanceAmt;

		String finReference = pd.getFinReference();
		// Mandate Rejected
		String mandateStatus = pd.getMandateStatus();
		if (MandateConstants.STATUS_REJECTED.equals(mandateStatus)) {
			pd.setExcludeReason(RepayConstants.PEXC_MANDATE_REJECTED);
			return;
		}

		// EMI HOLD
		if (pd.getDefSchdDate().compareTo(ph.getToDate()) > 0) {
			pd.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		// Mandate Hold
		if (MandateConstants.STATUS_HOLD.equals(mandateStatus)) {
			pd.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
			return;
		}

		boolean isECSMandate = MandateConstants.TYPE_ECS.equals(pd.getMandateType());
		if (!isECSMandate) {
			if (!MandateConstants.STATUS_APPROVED.equals(mandateStatus)) {
				pd.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
				return;
			}

			// Mandate Expired
			if (pd.getMandateExpiryDate() != null
					&& DateUtility.compare(pd.getDefSchdDate(), pd.getMandateExpiryDate()) > 0) {
				pd.setExcludeReason(RepayConstants.PEXC_MANDATE_EXPIRY);
				return;
			}
		}

		// at first advance interest and EMI then EMI advance
		if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
			processAdvAmounts(ph, pd);
		}

		// EMI IN ADVANCE
		// if there is no due no need to proceed further.
		if (pd.getSchAmtDue().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		// EMI IN ADVANCE
		FinExcessAmount excessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);
		if (excessAmount != null) {
			emiInAdvanceAmt = excessAmount.getBalanceAmt();
			excessAmount.setAmount(emiInAdvanceAmt);
			pd.setEmiInAdvance(excessAmount);
		} else {
			emiInAdvanceAmt = BigDecimal.ZERO;
		}

		if (emiInAdvanceAmt.compareTo(BigDecimal.ZERO) > 0) {
			pd.setExcessID(excessAmount.getExcessID());
		}

		if (emiInAdvanceAmt.compareTo(pd.getSchAmtDue()) >= 0) {
			pd.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
			pd.setPresentmentAmt(BigDecimal.ZERO);
			pd.setAdvanceAmt(pd.getSchAmtDue());
		} else {
			pd.setPresentmentAmt(pd.getSchAmtDue().subtract(emiInAdvanceAmt));
			pd.setAdvanceAmt(emiInAdvanceAmt);
		}

		BigDecimal advAmount = pd.getAdvAdjusted();
		pd.setPresentmentAmt(pd.getPresentmentAmt().subtract(advAmount));
		pd.setAdvanceAmt(pd.getAdvanceAmt().add(advAmount));

		logger.debug(Literal.LEAVING);
	}

	private long savePresentmentHeaderDetails(PresentmentHeader header) {
		generateID(header);

		String reference = StringUtils.leftPad(String.valueOf(header.getId()), 15, "0");
		header.setStatus(RepayConstants.PEXC_EXTRACT);
		header.setPresentmentDate(DateUtility.getSysDate());
		header.setReference(header.getMandateType().concat(reference));
		header.setdBStatusId(0);
		header.setImportStatusId(0);
		header.setTotalRecords(0);
		header.setProcessedRecords(0);
		header.setSuccessRecords(0);
		header.setFailedRecords(0);
		presentmentDetailDAO.savePresentmentHeader(header);

		return header.getId();

	}

	public void generateID(PresentmentHeader header) {
		if (header.getId() == Long.MIN_VALUE) {
			long id = presentmentDetailDAO.getSeqNumber("SeqPresentmentHeader");
			header.setId(id);
		}
	}

	private String getPresentmentRef(ResultSet rs) throws SQLException {
		logger.debug(Literal.ENTERING);

		StringBuilder sb = new StringBuilder();
		sb.append(rs.getString("BRANCHCODE"));
		sb.append(rs.getString("LOANTYPE"));
		sb.append(rs.getString("MANDATETYPE"));

		logger.debug(Literal.LEAVING);
		return sb.toString();
	}

	private void processAdvAmounts(PresentmentHeader ph, PresentmentDetail pd) {
		AdvanceType advanceType = null;
		String amountType = "";

		if (pd.getGrcPeriodEndDate() != null && pd.getSchDate().compareTo(pd.getGrcPeriodEndDate()) <= 0) {
			advanceType = AdvanceType.getType(pd.getGrcAdvType());
		} else {
			advanceType = AdvanceType.getType(pd.getAdvType());
		}

		if (FinanceConstants.FLAG_BPI.equals(pd.getBpiOrHoliday())) {
			if (FinanceConstants.BPI_DISBURSMENT.equals(pd.getBpiTreatment()) && ph.isBpiPaidOnInstDate()) {
				advanceType = AdvanceType.AF;
			}
		}

		if (advanceType == null) {
			return;
		}

		// get excess
		String finRef = pd.getFinReference();
		int exculdeReason = 0;
		BigDecimal dueAmt = BigDecimal.ZERO;

		if (advanceType == AdvanceType.AE) {
			if (AdvanceStage.getStage(pd.getAdvStage()) == AdvanceStage.FE) {
				return;
			}
			amountType = RepayConstants.EXAMOUNTTYPE_ADVEMI;
			dueAmt = pd.getSchAmtDue();
			exculdeReason = RepayConstants.PEXC_ADVEMI;
		} else {
			amountType = RepayConstants.EXAMOUNTTYPE_ADVINT;
			dueAmt = pd.getSchPftDue();
			if (pd.getSchPriDue().compareTo(BigDecimal.ZERO) == 0) {
				exculdeReason = RepayConstants.PEXC_ADVINT;
			}
		}

		FinExcessAmount finExAmt = finExcessAmountDAO.getExcessAmountsByRefAndType(finRef, amountType);

		if (finExAmt == null) {
			return;
		}

		BigDecimal excessBal = finExAmt.getBalanceAmt();
		BigDecimal adjAmount = BigDecimal.ZERO;

		if (dueAmt.compareTo(BigDecimal.ZERO) > 0) {
			if (excessBal != null && excessBal.compareTo(BigDecimal.ZERO) > 0) {
				if (dueAmt.compareTo(excessBal) >= 0) {
					adjAmount = excessBal;
				} else {
					adjAmount = dueAmt;
				}
			}
		}

		pd.setAdvAdjusted(adjAmount);
		if (adjAmount.compareTo(dueAmt) == 0) {
			pd.setExcludeReason(exculdeReason);
		}

		if (FinanceConstants.FLAG_BPI.equals(pd.getBpiOrHoliday())) {
			if (FinanceConstants.BPI_DISBURSMENT.equals(pd.getBpiTreatment()) && ph.isBpiPaidOnInstDate()) {
				return;
			}
		}

		// process advance moment
		finExAmt.setReservedAmt(adjAmount);
		BigDecimal amount = finExAmt.getAmount();
		BigDecimal reservedAmt = finExAmt.getReservedAmt();
		BigDecimal utilisedAmt = finExAmt.getUtilisedAmt();
		finExAmt.setBalanceAmt(amount.subtract(reservedAmt).subtract(utilisedAmt));
		pd.setExcessAmountReversal(finExAmt);
		// finExcessAmountDAO.updateExcessReserve(finExAmt);

		// movement
		FinExcessMovement exMovement = new FinExcessMovement();
		exMovement.setExcessID(finExAmt.getExcessID());
		exMovement.setReceiptID(ph.getId());
		exMovement.setMovementFrom(RepayConstants.PAYTYPE_PRESENTMENT);
		exMovement.setAmount(adjAmount);
		exMovement.setSchDate(pd.getSchDate());
		exMovement.setMovementType("I");
		exMovement.setTranType("I");
		finExAmt.setExcessMovement(exMovement);
		// finExcessAmountDAO.saveExcessMovement(exMovement);
	}
}
