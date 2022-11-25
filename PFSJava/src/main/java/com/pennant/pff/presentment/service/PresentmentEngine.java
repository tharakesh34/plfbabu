package com.pennant.pff.presentment.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.pff.presentment.dao.DueExtractionConfigDAO;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.dao.PresentmentExcludeCodeDAO;
import com.pennanttech.external.ExternalPresentmentHook;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.external.PresentmentRequest;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class PresentmentEngine {
	private final Logger logger = LogManager.getLogger(PresentmentEngine.class);

	public static final String STATUS_SUBMIT = "Submit";
	public static final String STATUS_APPROVE = "Approve";

	private FinExcessAmountDAO finExcessAmountDAO;
	private OverdrafLoanService overdrafLoanService;
	private ChequeDetailDAO chequeDetailDAO;
	private PresentmentDAO presentmentDAO;
	private DueExtractionConfigDAO dueExtractionConfigDAO;
	private PresentmentDetailService presentmentDetailService;
	private PresentmentExcludeCodeDAO presentmentExcludeCodeDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CustomerDAO customerDAO;
	private ReceiptPaymentService receiptPaymentService;
	private ExternalPresentmentHook externalPresentmentHook;
	private PresentmentRequest defaultPresentmentRequest;
	private PresentmentRequest presentmentRequest;

	public PresentmentEngine() {
		super();
	}

	/**
	 * This method will prepare the presentment data for the provided parameters. This method will be called from EOD
	 * for auto extraction (only presentment) and from screen for manual extraction (both presentment and
	 * re-presentment.
	 * 
	 * @param header
	 * @return return the total number of records extracted.
	 */
	public int preparation(PresentmentHeader header) {
		logger.debug(Literal.ENTERING);

		Date appDate = header.getAppDate();
		String presentmentType = header.getPresentmentType();
		boolean autoExtract = header.isAutoExtract();
		String finType = header.getLoanType();
		String finBranch = header.getFinBranch();
		String entityCode = header.getEntityCode();
		String emandateSource = header.getEmandateSource();
		String instrumentType = StringUtils.trimToNull(header.getMandateType());
		Date fromDate = header.getFromDate();
		Date toDate = header.getToDate();
		Date dueDate = header.getDueDate();

		StringBuilder info = new StringBuilder();
		info.append("\nBatch-ID: ").append(presentmentType);
		info.append("\nPresentment Type: ").append(presentmentType);
		info.append("\nEMandate Source: ").append(emandateSource);
		info.append("\nInstrument Type: ").append(presentmentType);
		info.append("\nLoan Types: ").append(finType);
		info.append("\nLoan Branch: ").append(entityCode);
		info.append("\nEntity: ").append(finBranch);
		info.append("\nFrom Date: ").append(DateUtil.formatToShortDate(fromDate));
		info.append("\nTo Date: ").append(DateUtil.formatToShortDate(toDate));
		info.append("\nDue Date: ").append(DateUtil.formatToShortDate(dueDate));
		info.append("\nApp Date: ").append(DateUtil.formatToShortDate(appDate));
		info.append("\nAuto Extaction: ").append(autoExtract);

		logger.info(info.toString());

		int count = 0;

		if (autoExtract) {
			Map<String, Date> dueDates = dueExtractionConfigDAO.getDueDates(appDate);

			for (String code : dueDates.keySet()) {
				PresentmentHeader ph = new PresentmentHeader();
				ph.setBatchID(header.getBatchID());
				ph.setAutoExtract(true);

				ph.setPresentmentType(presentmentType);
				ph.setMandateType(code);
				ph.setAppDate(appDate);
				ph.setDueDate(dueDates.get(code));

				count = count + prepareDues(ph);
			}
		} else {
			if (instrumentType != null && !"#".equals(instrumentType)) {
				count = count + prepareDues(header);
			} else {
				for (ValueLabel code : MandateUtil.getInstrumentTypesForBE()) {
					PresentmentHeader ph = new PresentmentHeader();
					ph.setMandateType(header.getMandateType());
					ph.setEmandateSource(header.getEmandateSource());
					ph.setLoanType(header.getLoanType());
					ph.setEntityCode(header.getEntityCode());
					ph.setFinBranch(header.getFinBranch());
					ph.setFromDate(header.getFromDate());
					ph.setToDate(header.getToDate());
					ph.setDueDate(header.getDueDate());
					ph.setBpiPaidOnInstDate(header.isBpiPaidOnInstDate());
					ph.setGroupByBank(header.isGroupByPartnerBank());
					ph.setGroupByPartnerBank(header.isGroupByPartnerBank());

					ph.setBatchID(header.getBatchID());
					ph.setAutoExtract(true);

					ph.setPresentmentType(presentmentType);
					ph.setMandateType(code.getValue());
					ph.setAppDate(appDate);

					count = count + prepareDues(ph);
				}
			}
		}

		if (count == 0) {
			return 0;
		}

		long batchID = header.getBatchID();
		count = count - presentmentDAO.clearSecurityCheque(batchID);

		presentmentDAO.updateToSecurityMandate(batchID);

		presentmentDAO.updatePartnerBankID(batchID);

		logger.debug(Literal.LEAVING);

		return count;
	}

	private int prepareDues(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		long batchID = ph.getBatchID();
		String instrumentType = StringUtils.trimToNull(ph.getMandateType());
		Date fromDate = ph.getFromDate();
		Date toDate = ph.getToDate();
		Date dueDate = ph.getDueDate();
		String presentmentType = ph.getPresentmentType();

		int count = 0;

		if (fromDate != null && toDate != null && "#".equals(instrumentType)) {
			/* From Screen when fromDate, toDate selected */
			count = presentmentDAO.extarct(batchID, fromDate, toDate);
		} else if (fromDate != null && toDate != null && instrumentType != null && !"#".equals(instrumentType)) {
			/* From Screen when fromDate, toDate, instrumentType is selected */
			count = presentmentDAO.extarct(batchID, instrumentType, fromDate, toDate);
		} else if (dueDate != null) {
			/* From EOD for Auto Extraction */
			count = presentmentDAO.extarct(batchID, instrumentType, dueDate, dueDate);
			logger.info("\nInstrument Type {}\nDue Date {}", instrumentType, DateUtil.formatToShortDate(dueDate));
		}

		if (fromDate != null && toDate != null) {
			logger.info("\nInstrument Type {}\nFrom Date {}\nTo Date {}", instrumentType,
					DateUtil.formatToShortDate(fromDate), DateUtil.formatToShortDate(toDate));
		}

		if (count == 0) {
			logger.debug(Literal.LEAVING);
			return 0;
		}

		count = count - presentmentDAO.clearByNoDues(batchID);

		if (count == 0) {
			logger.debug(Literal.LEAVING);
			return 0;
		}

		if (InstrumentType.isIPDC(instrumentType)) {
			presentmentDAO.updateIPDC(batchID);
		}

		/* The below code will be executed when manual extraction only */
		count = count - exludeByParameter(ph);

		if (PennantConstants.PROCESS_PRESENTMENT.equalsIgnoreCase(presentmentType)) {
			count = count - presentmentDAO.clearByExistingRecord(batchID);
		} else {
			count = count - presentmentDAO.clearByRepresentment(batchID);
		}

		logger.debug(Literal.LEAVING);

		return count;
	}

	private int exludeByParameter(PresentmentHeader ph) {
		int count = 0;

		long batchID = ph.getBatchID();
		String finType = ph.getLoanType();
		String finBranch = ph.getFinBranch();
		String entityCode = ph.getEntityCode();
		String emandateSource = ph.getEmandateSource();
		String instrumentType = StringUtils.trimToNull(ph.getMandateType());

		if ((instrumentType != null && !"#".equals(instrumentType)) && !ph.isAutoExtract()) {
			count = count + presentmentDAO.clearByInstrumentType(batchID, instrumentType);
		}

		if (InstrumentType.isEMandate(instrumentType) && StringUtils.isNotEmpty(emandateSource)
				&& !ph.isAutoExtract()) {
			count = count + presentmentDAO.clearByInstrumentType(batchID, instrumentType, emandateSource);
		}

		if (StringUtils.trimToNull(finType) != null) {
			count = count + presentmentDAO.clearByLoanType(batchID, finType);
		}

		if (StringUtils.trimToNull(finBranch) != null) {
			count = count + presentmentDAO.clearByLoanBranch(batchID, finBranch);
		}

		if (StringUtils.trimToNull(entityCode) != null) {
			count = count + presentmentDAO.clearByEntityCode(batchID, entityCode);
		}

		return count;
	}

	public void grouping(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);
		long batchID = ph.getBatchID();
		List<PresentmentDetail> list = null;

		if (ph.isGroupByBank() && ph.isGroupByPartnerBank()) {
			list = presentmentDAO.getGroupByPartnerBankAndBank(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByPartnerBank(batchID, list);
		} else if (ph.isGroupByBank()) {
			list = presentmentDAO.getGroupByBank(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByBank(batchID, list);
		} else if (ph.isGroupByPartnerBank()) {
			list = presentmentDAO.getGroupByPartnerBank(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByPartnerBank(batchID, list);
		} else {
			list = presentmentDAO.getGroupByDefault(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByDefault(batchID, list);
		}

		logger.debug(Literal.LEAVING);
	}

	private void setHeader(PresentmentHeader ph, List<PresentmentDetail> list) {
		logger.debug(Literal.ENTERING);

		Date appDate = ph.getAppDate();
		long batchID = ph.getBatchID();

		Map<String, Date> dueDates = null;

		if (PennantConstants.PROCESS_PRESENTMENT.equals(ph.getPresentmentType())) {
			dueDates = dueExtractionConfigDAO.getDueDates(appDate);
		}

		for (PresentmentDetail pd : list) {

			String instrumentType = pd.getInstrumentType();
			String presentmentType = ph.getPresentmentType();

			Date fromDate = ph.getFromDate();
			Date toDate = ph.getToDate();
			Date dueDate = null;

			if (fromDate != null && toDate != null) {
				dueDate = toDate;
			} else if (dueDates != null) {
				dueDate = dueDates.get(instrumentType);
			} else {
				dueDate = pd.getDefSchdDate();
			}

			if (fromDate == null) {
				fromDate = dueDate;
			}

			if (toDate == null) {
				toDate = dueDate;
			}

			PresentmentHeader header = new PresentmentHeader();
			header.setBatchID(batchID);
			header.setFromDate(fromDate);
			header.setToDate(toDate);
			header.setMandateType(instrumentType);
			header.setPresentmentType(presentmentType);

			long headerId = saveHeader(header, pd);

			pd.setDueDate(dueDate);
			pd.setHeaderId(headerId);
		}

		logger.debug(Literal.LEAVING);
	}

	private long saveHeader(PresentmentHeader ph, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		long headerId = presentmentDAO.getSeqNumber("SeqPresentmentHeader");

		String reference = StringUtils.leftPad(String.valueOf(headerId), 15, "0");

		ph.setStatus(RepayConstants.PEXC_EXTRACT);
		ph.setPresentmentDate(DateUtil.getSysDate());
		ph.setId(headerId);
		ph.setSchdate(pd.getDefSchdDate());
		ph.setEntityCode(pd.getEntityCode());
		ph.setBankCode(pd.getBankCode());
		ph.setPartnerBankId(pd.getPartnerBankId());

		if (ph.getPartnerBankId() == null || ph.getPartnerBankId() <= 0) {
			Presentment pb = presentmentDAO.getPartnerBankId(ph.getLoanType(), ph.getMandateType());

			if (pb == null) {
				pb = new Presentment();
				pb.setPartnerBankId(621L);
			}

			ph.setPartnerBankId(pb.getPartnerBankId());
		}

		String ref = ph.getMandateType().concat(reference);

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(ph.getPresentmentType())) {
			ref = "RE" + ref;
		}

		ph.setReference(ref);
		ph.setdBStatusId(0);
		ph.setImportStatusId(0);
		ph.setTotalRecords(0);
		ph.setProcessedRecords(0);
		ph.setSuccessRecords(0);
		ph.setFailedRecords(0);

		presentmentDAO.savePresentmentHeader(ph);

		logger.debug(Literal.LEAVING);
		return headerId;
	}

	public PresentmentDetail extract(long extractionID, PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail pd = presentmentDAO.getPresentmentDetail(extractionID);

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(ph.getPresentmentType())) {
			Long mandateId = presentmentDAO.getPreviousMandateID(pd.getFinID(), pd.getSchDate());

			if (mandateId != null) {
				pd.setMandateId(mandateId);
			}
		}

		BigDecimal schAmtDue = BigDecimal.ZERO;

		BigDecimal schPriDue = BigDecimal.ZERO;
		schPriDue = schPriDue.add(pd.getPrincipalSchd());
		schPriDue = schPriDue.subtract(pd.getSchdPriPaid());

		BigDecimal schPftDue = BigDecimal.ZERO;
		schPftDue = schPftDue.add(pd.getProfitSchd());
		schPftDue = schPftDue.subtract(pd.getSchdPftPaid());

		BigDecimal schFeeDue = BigDecimal.ZERO;
		schFeeDue = schFeeDue.add(pd.getFeeSchd());
		schFeeDue = schFeeDue.subtract(pd.getSchdFeePaid());

		BigDecimal tDSAmount = BigDecimal.ZERO;
		tDSAmount = tDSAmount.add(pd.gettDSAmount());
		tDSAmount = tDSAmount.subtract(pd.getTdsPaid());

		schAmtDue = schAmtDue.add(schPriDue);
		schAmtDue = schAmtDue.add(schPftDue);
		schAmtDue = schAmtDue.add(schFeeDue);
		schAmtDue = schAmtDue.subtract(tDSAmount);

		String productCategory = pd.getProductCategory();
		if (BigDecimal.ZERO.compareTo(schAmtDue) >= 0) {
			boolean dueExists = true;
			if (ProductUtil.isOverDraft(productCategory) && ph.getAppDate().compareTo(pd.getSchDate()) < 0) {
				dueExists = false;
			}

			if (!dueExists) {
				return pd;
			}
		}

		pd.setStatus(RepayConstants.PEXC_IMPORT);

		pd.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);

		pd.setPresentmentAmt(BigDecimal.ZERO);
		pd.setSchAmtDue(schAmtDue);
		pd.setSchPriDue(schPriDue);
		pd.setSchPftDue(schPftDue);
		pd.setSchFeeDue(schFeeDue);
		pd.settDSAmount(tDSAmount);

		pd.setSchInsDue(BigDecimal.ZERO);
		pd.setSchPenaltyDue(BigDecimal.ZERO);
		pd.setAdvanceAmt(BigDecimal.ZERO);
		pd.setAdviseAmt(BigDecimal.ZERO);
		pd.setExcessID(0);
		pd.setReceiptID(0);

		pd.setVersion(0);
		pd.setLastMntBy(ph.getLastMntBy());
		pd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		pd.setWorkflowId(0);

		if (FinanceConstants.FLAG_HOLDEMI.equals(pd.getBpiOrHoliday())) {
			pd.setOriginalSchDate(pd.getSchDate());
			pd.setSchDate(pd.getDefSchdDate());
		}

		if (pd.getSchAmtDue().compareTo(BigDecimal.ZERO) > 0) {
			doCalculations(ph, pd);
		}

		logger.debug(Literal.LEAVING);
		return pd;
	}

	private void doCalculations(PresentmentHeader ph, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		String mandateStatus = pd.getMandateStatus();

		if (InstrumentType.isPDC(pd.getInstrumentType()) || InstrumentType.isIPDC(pd.getInstrumentType())) {
			pd.setMandateId(pd.getChequeId());

			String chequeStatus = pd.getChequeStatus();

			if (!ChequeSatus.NEW.equals(chequeStatus)) {
				int excludeReason = 0;
				switch (chequeStatus) {
				case ChequeSatus.PRESENT:
					excludeReason = RepayConstants.CHEQUESTATUS_PRESENT;
					break;
				case ChequeSatus.REALISE:
					excludeReason = RepayConstants.CHEQUESTATUS_REALISE;
					break;
				case ChequeSatus.REALISED:
					excludeReason = RepayConstants.CHEQUESTATUS_REALISED;
					break;
				default:
					break;
				}
				pd.setExcludeReason(excludeReason);
			}
		} else {
			if (InstrumentType.isECS(pd.getMandateType())) {
				if (MandateStatus.isRejected(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_REJECTED);
				} else if (MandateStatus.isHold(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
				}
			} else {
				if (MandateStatus.isRejected(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_REJECTED);
				} else if (MandateStatus.isHold(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
				} else if (!MandateStatus.isApproved(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
				}

				if (pd.getMandateExpiryDate() != null
						&& DateUtil.compare(pd.getDefSchdDate(), pd.getMandateExpiryDate()) > 0) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_EXPIRY);
				}
			}
		}

		if (pd.getDefSchdDate().compareTo(pd.getDueDate()) > 0) {
			pd.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		if (PennantConstants.PROCESS_PRESENTMENT.equalsIgnoreCase(ph.getPresentmentType())) {
			processAdvAmounts(ph, pd);
		}

		processEMIInAdvance(pd);

		BigDecimal advAmount = pd.getAdvAdjusted();
		pd.setPresentmentAmt(pd.getPresentmentAmt().subtract(advAmount));

		if (InstrumentType.isPDC(pd.getInstrumentType()) || InstrumentType.isIPDC(pd.getInstrumentType())) {
			pd.setPresentmentAmt(pd.getChequeAmount());
		}

		logger.debug(Literal.LEAVING);
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

		FinExcessAmount finExAmt = finExcessAmountDAO.getExcessAmountsByRefAndType(pd.getFinID(), amountType);

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

		finExAmt.setReservedAmt(adjAmount);
		BigDecimal amount = finExAmt.getAmount();
		BigDecimal reservedAmt = finExAmt.getReservedAmt();
		BigDecimal utilisedAmt = finExAmt.getUtilisedAmt();
		finExAmt.setBalanceAmt(amount.subtract(reservedAmt).subtract(utilisedAmt));
		pd.setExcessAmountReversal(finExAmt);

		FinExcessMovement exMovement = new FinExcessMovement();
		exMovement.setExcessID(finExAmt.getExcessID());
		exMovement.setReceiptID(Long.MIN_VALUE);
		exMovement.setMovementFrom(RepayConstants.PAYTYPE_PRESENTMENT);
		exMovement.setAmount(adjAmount);
		exMovement.setSchDate(pd.getSchDate());
		exMovement.setMovementType("I");
		exMovement.setTranType("I");
		finExAmt.setExcessMovement(exMovement);
	}

	private void processEMIInAdvance(PresentmentDetail pd) {
		long finID = pd.getFinID();

		BigDecimal emiInAdvanceAmt = BigDecimal.ZERO;
		FinExcessAmount excessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);

		if (excessAmount != null) {
			emiInAdvanceAmt = excessAmount.getBalanceAmt();
			pd.setEmiInAdvance(excessAmount);
		}

		if (emiInAdvanceAmt.compareTo(BigDecimal.ZERO) > 0) {
			pd.setExcessID(excessAmount.getExcessID());
		}

		BigDecimal advanceAmt = BigDecimal.ZERO;
		if (emiInAdvanceAmt.compareTo(pd.getSchAmtDue()) >= 0) {
			pd.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
			pd.setPresentmentAmt(BigDecimal.ZERO);
			pd.setAdvanceAmt(pd.getSchAmtDue());
			pd.setStatus(RepayConstants.PEXC_APPROV);
			advanceAmt = pd.getAdvanceAmt();
		} else {
			advanceAmt = emiInAdvanceAmt;
			pd.setPresentmentAmt(pd.getSchAmtDue().subtract(advanceAmt));
		}

		if (excessAmount != null) {
			excessAmount.setAmount(advanceAmt);
		}

		BigDecimal advAmount = pd.getAdvAdjusted();
		pd.setAdvanceAmt(advanceAmt.add(advAmount));
	}

	public void save(PresentmentDetail pd) {
		List<FinExcessAmount> excess = new ArrayList<>();
		List<FinExcessAmount> excessRevarsal = new ArrayList<>();
		List<FinExcessMovement> excessMovement = new ArrayList<>();
		List<PresentmentDetail> includeList = new ArrayList<>();
		List<FinExcessAmount> emiInAdvance = new ArrayList<>();

		Map<String, PresentmentDetail> odPresentments = new HashMap<>();

		setPresentmentRef(pd);

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

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(pd.getProductCategory())) {
			odPresentments.put(pd.getFinReference(), pd);
		}

		presentmentDAO.save(pd);

		if (RepayConstants.PEXC_EMIINCLUDE == pd.getExcludeReason()) {
			includeList.add(pd);
		}

		if (ImplementationConstants.OVERDRAFT_REPRESENTMENT_CHARGES_INCLUDE && !odPresentments.isEmpty()) {
			overdrafLoanService.createCharges(odPresentments.values().stream().collect(Collectors.toList()));
		}

		if (includeList.isEmpty()) {
			return;
		}

		if (FinanceConstants.FLAG_HOLDEMI.equals(pd.getBpiOrHoliday())) {
			pd.setSchDate(pd.getOriginalSchDate());
		}

		try {
			FinanceMain fm = new FinanceMain();
			fm.setFinID(pd.getFinID());
			fm.setSchdVersion(pd.getSchdVersion());

			financeMainDAO.updateSchdVersion(fm, false);

			presentmentDAO.updateSchdWithPresentmentId(includeList);

			if (pd.getRePresentUploadID() != null) {
				presentmentDAO.updateRepresentWithPresentmentId(includeList);
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

			List<PresentmentDetail> cheques = new ArrayList<>();
			if (InstrumentType.isPDC(pd.getInstrumentType()) || InstrumentType.isIPDC(pd.getInstrumentType())) {
				cheques.add(pd);
			}

			if (!cheques.isEmpty()) {
				chequeDetailDAO.updateChequeStatus(cheques);
			}

		} catch (ConcurrencyException e) {
			pd.setExcludeReason(RepayConstants.PEXC_SCHDVERSION);
			presentmentDAO.updateExcludeReason(pd.getId(), RepayConstants.PEXC_SCHDVERSION);
		}

	}

	public void clearQueue(long batchId) {
		this.presentmentDAO.clearQueue(batchId);
	}

	private int getPresentmentIndex(Date businessDate, List<FinanceScheduleDetail> schedules) {
		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail schd = schedules.get(i);

			if (schd.getDefSchdDate().compareTo(businessDate) == 0) {
				return i;
			}
		}

		return 0;
	}

	private void createReceipt(PresentmentDetail pd) {
		Date businessDate = pd.getAppDate();

		ReceiptDTO receiptDTO = new ReceiptDTO();

		long finID = pd.getFinID();
		FinanceMain fm = financeMainDAO.getFinMainsForEODByFinRef(finID, true);
		FinanceProfitDetail pftDetails = financeProfitDetailDAO.getFinProfitDetailsById(finID);
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		Customer customer = customerDAO.getCustomerEOD(fm.getCustID());

		FinExcessAmount emiInAdvance = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);

		int presentmentIndex = getPresentmentIndex(businessDate, schedules);

		fm.setEventProperties(pd.getEventProperties());

		receiptDTO.setFinType(FinanceConfigCache.getCacheFinanceType(fm.getFinType()));
		receiptDTO.setPresentmentDetail(pd);
		receiptDTO.setBussinessDate(businessDate);
		receiptDTO.setCustomer(customer);
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setEmiInAdvance(emiInAdvance);
		receiptDTO.setProfitDetail(pftDetails);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setValuedate(businessDate);
		receiptDTO.setPostDate(businessDate);

		receiptPaymentService.processReceipts(receiptDTO, presentmentIndex);
	}

	public void approve(PresentmentDetail pd) {
		Map<Integer, String> bounceForPD = presentmentExcludeCodeDAO.getUpfrontBounceCode();

		boolean upfronBounceRequired = MapUtils.isNotEmpty(bounceForPD);

		int excludeReason = pd.getExcludeReason();

		if (upfronBounceRequired && (excludeReason != RepayConstants.PEXC_EMIINCLUDE
				|| excludeReason != RepayConstants.PEXC_EMIINADVANCE)) {
			presentmentDAO.approveExludes(pd.getId());
		} else if (excludeReason != RepayConstants.PEXC_EMIINCLUDE
				|| excludeReason != RepayConstants.PEXC_EMIINADVANCE) {
			presentmentDAO.updatePresentmentIdAsZero(pd.getId());
			return;
		}

		if (DateUtil.compare(pd.getAppDate(), pd.getSchDate()) >= 0) {
			createReceipt(pd);
		}
	}

	public void sendToPresentment(PresentmentHeader ph) {
		long headerId = ph.getId();

		List<PresentmentDetail> presements = presentmentDAO.getSendToPresentmentDetails(headerId);

		List<Long> idList = new ArrayList<>();

		for (PresentmentDetail pd : presements) {
			long detailID = pd.getId();
			idList.add(detailID);
		}

		try {
			String presentmentRef = ph.getReference();
			String bankAccNo = ph.getPartnerAcctNumber();

			if (externalPresentmentHook != null) {
				externalPresentmentHook.processPresentmentRequest(ph);
			} else {
				getPresentmentRequest().sendReqest(idList, ph.getId(), false, ph.getMandateType(), presentmentRef,
						bankAccNo);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		presentmentDAO.updateHeader(headerId);
	}

	public void approve(long batchId) {
		Map<Integer, String> bounceForPD = presentmentExcludeCodeDAO.getUpfrontBounceCode();

		boolean upfronBounceRequired = MapUtils.isNotEmpty(bounceForPD);

		LoggedInUser loggedInUser = new LoggedInUser();

		List<PresentmentHeader> headerList = presentmentDAO.getPresentmentHeaders(batchId);

		for (PresentmentHeader ph : headerList) {
			int totalRecords = 0;

			long id = ph.getId();

			List<Long> includeList = presentmentDAO.getIncludeList(id);
			ph.setIncludeList(includeList);

			totalRecords = includeList.size();

			List<Long> excludeList = presentmentDAO.getExcludeList(id);
			ph.setExcludeList(excludeList);
			totalRecords = totalRecords + excludeList.size();

			if (upfronBounceRequired) {
				presentmentDAO.approveExludes(id);
			}

			if (StringUtils.isEmpty(ph.getPartnerAcctNumber())
					&& (ph.getPartnerBankId() == null || ph.getPartnerBankId() <= 0)) {
				Presentment pb = presentmentDAO.getPartnerBankId(ph.getLoanType(), ph.getMandateType());

				if (pb == null) {
					pb = new Presentment();
					pb.setPartnerBankId(621L);

					presentmentDAO.updatePartnerBankID(id, pb.getPartnerBankId());
				}

				ph.setPartnerAcctNumber(pb.getAccountNo());
				ph.setPartnerBankId(pb.getPartnerBankId());
			} else {
				ph.setPartnerAcctNumber(ph.getPartnerAcctNumber());
				ph.setPartnerBankId(ph.getPartnerBankId());
			}

			ph.setUserDetails(loggedInUser);

			try {
				ph.setUserAction(STATUS_APPROVE);
				presentmentDetailService.updatePresentmentDetails(ph);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

			presentmentDAO.updateHeader(id);
		}
	}

	public int preparationForRepresentment(long batchID, List<Long> headerId) {
		int count = 0;

		List<PresentmentHeader> list = new ArrayList<>();

		list = presentmentDAO.getpresentmentHeaderList(headerId);

		for (PresentmentHeader ph : list) {
			count = count + presentmentDAO.extract(batchID, ph);

			if (InstrumentType.isIPDC(ph.getMandateType())) {
				presentmentDAO.updateIPDC(batchID);
			}
		}

		count = count - presentmentDAO.clearByNoDues(batchID);

		if (count == 0) {
			return 0;
		}

		count = count - presentmentDAO.clearSecurityCheque(batchID);

		presentmentDAO.updateToSecurityMandate(batchID);

		presentmentDAO.updatePartnerBankID(batchID);

		return count;
	}

	public PresentmentDetail getPresentmenToPost(Long presentmentID) {
		return presentmentDAO.getPresentmenToPost(presentmentID);
	}

	public List<PresentmentHeader> getPresentmenHeaders(Long batchId) {
		return presentmentDAO.getPresentmentHeaders(batchId);
	}

	private void setPresentmentRef(PresentmentDetail pd) {
		StringBuilder sb = new StringBuilder();
		sb.append(pd.getBranchCode());
		sb.append(pd.getFinType());
		sb.append(pd.getInstrumentType());

		long id = presentmentDAO.getNextValue();
		pd.setId(id);

		String reference = sb.toString();
		String presentmentRef = StringUtils.leftPad(String.valueOf(id), 29 - reference.length(), "0");
		pd.setPresentmentRef(reference.concat(presentmentRef));
	}

	public PresentmentDetail getPresentmentDetail(Long presentmentID) {
		return presentmentDAO.getPresentmentDetail(presentmentID);
	}

	@Autowired
	public void setPresentmentDAO(PresentmentDAO presentmentDAO) {
		this.presentmentDAO = presentmentDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
	public void setDueExtractionConfigDAO(DueExtractionConfigDAO dueExtractionConfigDAO) {
		this.dueExtractionConfigDAO = dueExtractionConfigDAO;
	}

	@Autowired
	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	@Autowired
	public void setPresentmentExcludeCodeDAO(PresentmentExcludeCodeDAO presentmentExcludeCodeDAO) {
		this.presentmentExcludeCodeDAO = presentmentExcludeCodeDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	@Autowired(required = false)
	public void setExternalPresentmentHook(ExternalPresentmentHook externalPresentmentHook) {
		this.externalPresentmentHook = externalPresentmentHook;
	}

	@Autowired
	private PresentmentRequest getPresentmentRequest() {
		return presentmentRequest == null ? defaultPresentmentRequest : presentmentRequest;
	}

	@Autowired
	public void setDefaultPresentmentRequest(PresentmentRequest defaultPresentmentRequest) {
		this.defaultPresentmentRequest = defaultPresentmentRequest;
	}

	@Autowired(required = false)
	@Qualifier(value = "presentmentRequest")
	public void setPresentmentRequest(PresentmentRequest presentmentRequest) {
		this.presentmentRequest = presentmentRequest;
	}
}
