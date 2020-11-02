package com.pennant.backend.service.financemanagement.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.core.TableType;

public class PresentmentDetailExtractService {
	private static final Logger logger = Logger.getLogger(PresentmentDetailExtractService.class);

	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ChequeDetailDAO chequeDetailDAO;

	public PresentmentDetailExtractService(PresentmentDetailDAO presentmentDetailDAO,
			FinExcessAmountDAO finExcessAmountDAO, ChequeDetailDAO chequeDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
		this.finExcessAmountDAO = finExcessAmountDAO;
		this.chequeDetailDAO = chequeDetailDAO;
	}

	/*
	 * Processing the Presentments If the payment type isPDC
	 */
	public String savePDCPresentments(PresentmentHeader presentmentHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean isEmptyRecords = false;
		Map<Object, Long> map = new HashMap<Object, Long>();
		long presentmentId = 0;
		ResultSet rs = null;
		List<Object> resultList = null;
		try {

			if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT,
					presentmentHeader.getPresentmentType())) {
				resultList = presentmentDetailDAO.getPDCPresentmentDetails(presentmentHeader);
			} else if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_REPRESENTMENT,
					presentmentHeader.getPresentmentType())) {
				resultList = presentmentDetailDAO.getPDCRePresentmentDetails(presentmentHeader);
			}
			rs = (ResultSet) resultList.get(0);
			while (rs.next()) {

				Date defSchDate = rs.getDate("DEFSCHDDATE");
				String bankCode = rs.getString("BANKCODE");
				String entity = rs.getString("ENTITYCODE");
				if (defSchDate != null) {
					if (!map.containsKey(defSchDate)
							|| (!map.containsKey(bankCode)
									&& SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK))
							|| !map.containsKey(entity)) {
						presentmentHeader.setSchdate(defSchDate);
						presentmentHeader.setBankCode(bankCode);
						presentmentHeader.setEntityCode(entity);
						if (presentmentHeader.getId() != Long.MIN_VALUE) {
							presentmentHeader.setId(Long.MIN_VALUE);
						}
						presentmentId = savePresentmentHeaderDetails(presentmentHeader);
						map.put(defSchDate, presentmentId);
						map.put(bankCode, presentmentId);
						map.put(entity, presentmentId);
					}
				}

				PresentmentDetail pDetail = new PresentmentDetail();
				pDetail.setPresentmentAmt(BigDecimal.ZERO);
				pDetail.setStatus(RepayConstants.PEXC_IMPORT);
				pDetail.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);
				pDetail.setPresentmentRef(getPresentmentRef(rs));

				// Schedule Setup
				pDetail.setFinReference(rs.getString("FINREFERENCE"));
				pDetail.setSchDate(rs.getDate("SCHDATE"));
				pDetail.setEmiNo(rs.getInt("EMINO"));
				pDetail.setSchSeq(rs.getInt("SCHSEQ"));
				pDetail.setDefSchdDate(rs.getDate("DEFSCHDDATE"));
				pDetail.setBpiOrHoliday(rs.getString("BPIORHOLIDAY"));
				pDetail.setBpiTreatment(rs.getString("BPITREATMENT"));

				BigDecimal schAmtDue = rs.getBigDecimal("PROFITSCHD").add(rs.getBigDecimal("PRINCIPALSCHD"))
						.add(rs.getBigDecimal("FEESCHD")).subtract(rs.getBigDecimal("SCHDPRIPAID"))
						.subtract(rs.getBigDecimal("SCHDPFTPAID")).subtract(rs.getBigDecimal("SCHDFEEPAID"))
						.subtract(rs.getBigDecimal("TDSAMOUNT"));
				if (BigDecimal.ZERO.compareTo(schAmtDue) >= 0) {
					continue;
				}

				pDetail.setSchAmtDue(schAmtDue);
				pDetail.setSchPriDue(rs.getBigDecimal("PRINCIPALSCHD").subtract(rs.getBigDecimal("SCHDPRIPAID")));
				pDetail.setSchPftDue(rs.getBigDecimal("PROFITSCHD").subtract(rs.getBigDecimal("SCHDPFTPAID")));
				pDetail.setSchFeeDue(rs.getBigDecimal("FEESCHD").subtract(rs.getBigDecimal("SCHDFEEPAID")));
				pDetail.settDSAmount(rs.getBigDecimal("TDSAMOUNT"));
				pDetail.setSchInsDue(BigDecimal.ZERO);
				pDetail.setSchPenaltyDue(BigDecimal.ZERO);
				//pDetail.setAdvanceAmt(schAmtDue);
				pDetail.setAdviseAmt(BigDecimal.ZERO);
				pDetail.setExcessID(0);
				pDetail.setReceiptID(0);

				// PDC Details
				pDetail.setMandateId(rs.getLong("CHEQUEDETAILSID"));
				pDetail.setMandateExpiryDate(rs.getDate("CHEQUEDATE"));
				pDetail.setMandateStatus(rs.getString("STATUS"));
				pDetail.setMandateType(rs.getString("MANDATETYPE"));

				pDetail.setVersion(0);
				pDetail.setLastMntBy(presentmentHeader.getLastMntBy());
				pDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				pDetail.setWorkflowId(0);
				pDetail.setEntityCode(rs.getString("ENTITYCODE"));

				if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT,
						presentmentHeader.getPresentmentType())) {
					pDetail.setGrcAdvType(rs.getString("GRCADVTYPE"));
					pDetail.setAdvType(rs.getString("ADVTYPE"));
					pDetail.setGrcPeriodEndDate(rs.getDate("GRCPERIODENDDATE"));
				}

				doPDCCalculations(pDetail, presentmentHeader);

				if (pDetail.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(pDetail.getExcessID(), "R", pDetail.getAdvanceAmt());
				}

				isEmptyRecords = true;

				// PresentmentDetail saving
				pDetail.setPresentmentId(presentmentId);
				long id = presentmentDetailDAO.save(pDetail, TableType.MAIN_TAB);

				// FinScheduleDetails update
				if (RepayConstants.PEXC_EMIINCLUDE == pDetail.getExcludeReason()) {
					presentmentDetailDAO.updateFinScheduleDetails(id, pDetail.getFinReference(), pDetail.getSchDate(),
							pDetail.getSchSeq());
					updateChequeStatus(pDetail.getMandateId(), PennantConstants.CHEQUESTATUS_PRESENT);
				}
			}
			if (!isEmptyRecords) {
				return PennantJavaUtil.getLabel("label_PresentmentSearchMessage");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (resultList != null) {
				PreparedStatement stmt = (PreparedStatement) resultList.get(1);
				if (stmt != null) {
					stmt.close();
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return PennantJavaUtil.getLabel("label_PresentmentExtractedMessage");
	}

	/*
	 * Processing the Presentments If the payment type is ECS ,NACH,DD
	 */
	public String savePresentments(PresentmentHeader ph) throws Exception {
		logger.debug(Literal.ENTERING);
		boolean isEmptyRecords = false;
		Map<Object, Long> map = new HashMap<Object, Long>();
		long presentmentId = 0;
		ResultSet rs = null;
		List<Object> resultList = null;
		try {

			if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
				resultList = presentmentDetailDAO.getPresentmentDetails(ph);
			} else if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_REPRESENTMENT, ph.getPresentmentType())) {
				resultList = presentmentDetailDAO.getRePresentmentDetails(ph);
			}

			rs = (ResultSet) resultList.get(0);
			while (rs.next()) {
				Date defSchDate = rs.getDate("DEFSCHDDATE");
				String bankCode = rs.getString("BANKCODE");
				String entity = rs.getString("ENTITYCODE");
				long partnerBankId = rs.getLong("PARTNERBANKID");
				if (defSchDate != null) {
					if (!map.containsKey(defSchDate)
							|| (!map.containsKey(bankCode)
									&& SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK)
									&& ImplementationConstants.GROUP_BATCH_BY_PARTNERBANK)
							|| !map.containsKey(entity)) {
						ph.setSchdate(defSchDate);
						ph.setBankCode(bankCode);
						ph.setEntityCode(entity);
						ph.setPartnerBankId(partnerBankId);
						if (ph.getId() != Long.MIN_VALUE) {
							ph.setId(Long.MIN_VALUE);
						}
						presentmentId = savePresentmentHeaderDetails(ph);
						map.put(defSchDate, presentmentId);
						map.put(bankCode, presentmentId);
						map.put(entity, presentmentId);
					}
				}

				PresentmentDetail pDetail = new PresentmentDetail();
				pDetail.setPresentmentAmt(BigDecimal.ZERO);
				pDetail.setStatus(RepayConstants.PEXC_IMPORT);
				pDetail.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);
				pDetail.setPresentmentRef(getPresentmentRef(rs));

				// Schedule Setup
				pDetail.setFinReference(rs.getString("FINREFERENCE"));
				pDetail.setSchDate(rs.getDate("SCHDATE"));
				pDetail.setEmiNo(rs.getInt("EMINO"));
				pDetail.setSchSeq(rs.getInt("SCHSEQ"));
				pDetail.setDefSchdDate(rs.getDate("DEFSCHDDATE"));
				pDetail.setBpiOrHoliday(rs.getString("BPIORHOLIDAY"));
				pDetail.setBpiTreatment(rs.getString("BPITREATMENT"));

				BigDecimal schAmtDue = rs.getBigDecimal("PROFITSCHD").add(rs.getBigDecimal("PRINCIPALSCHD"))
						.add(rs.getBigDecimal("FEESCHD")).subtract(rs.getBigDecimal("SCHDPRIPAID"))
						.subtract(rs.getBigDecimal("SCHDPFTPAID")).subtract(rs.getBigDecimal("SCHDFEEPAID"))
						.subtract(rs.getBigDecimal("TDSAMOUNT"));
				if (BigDecimal.ZERO.compareTo(schAmtDue) >= 0) {
					continue;
				}

				pDetail.setSchAmtDue(schAmtDue);
				pDetail.setSchPriDue(rs.getBigDecimal("PRINCIPALSCHD").subtract(rs.getBigDecimal("SCHDPRIPAID")));
				pDetail.setSchPftDue(rs.getBigDecimal("PROFITSCHD").subtract(rs.getBigDecimal("SCHDPFTPAID")));
				pDetail.setSchFeeDue(rs.getBigDecimal("FEESCHD").subtract(rs.getBigDecimal("SCHDFEEPAID")));
				pDetail.settDSAmount(rs.getBigDecimal("TDSAMOUNT"));
				pDetail.setSchInsDue(BigDecimal.ZERO);
				pDetail.setSchPenaltyDue(BigDecimal.ZERO);
				pDetail.setAdvanceAmt(schAmtDue);
				pDetail.setAdviseAmt(BigDecimal.ZERO);
				pDetail.setExcessID(0);
				pDetail.setReceiptID(0);

				// Mandate Details
				pDetail.setMandateId(rs.getLong("MANDATEID"));
				pDetail.setMandateExpiryDate(rs.getDate("EXPIRYDATE"));
				pDetail.setMandateStatus(rs.getString("STATUS"));
				pDetail.setMandateType(rs.getString("MANDATETYPE"));

				pDetail.setVersion(0);
				pDetail.setLastMntBy(ph.getLastMntBy());
				pDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				pDetail.setWorkflowId(0);
				pDetail.setEntityCode(rs.getString("ENTITYCODE"));

				if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, ph.getPresentmentType())) {
					pDetail.setGrcAdvType(rs.getString("GRCADVTYPE"));
					pDetail.setAdvType(rs.getString("ADVTYPE"));
					pDetail.setAdvStage(rs.getString("ADVSTAGE"));
					pDetail.setGrcPeriodEndDate(rs.getDate("GRCPERIODENDDATE"));
				}

				doCalculations(pDetail, ph);

				if (pDetail.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(pDetail.getExcessID(), "R", pDetail.getAdvanceAmt());
				}

				isEmptyRecords = true;

				// PresentmentDetail saving
				pDetail.setPresentmentId(presentmentId);
				long id = presentmentDetailDAO.save(pDetail, TableType.MAIN_TAB);

				// FinScheduleDetails update
				if (RepayConstants.PEXC_EMIINCLUDE == pDetail.getExcludeReason()) {
					presentmentDetailDAO.updateFinScheduleDetails(id, pDetail.getFinReference(), pDetail.getSchDate(),
							pDetail.getSchSeq());
				}
			}

			if (!isEmptyRecords) {
				return PennantJavaUtil.getLabel("label_PresentmentSearchMessage");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (resultList != null) {
				PreparedStatement stmt = (PreparedStatement) resultList.get(1);
				if (stmt != null) {
					stmt.close();
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return PennantJavaUtil.getLabel("label_PresentmentExtractedMessage");
	}

	private void doPDCCalculations(PresentmentDetail presentmentDetail, PresentmentHeader detailHeader) {
		logger.debug(Literal.ENTERING);

		BigDecimal emiInAdvanceAmt;
		String finReference = presentmentDetail.getFinReference();

		// Cheque Status
		if (!PennantConstants.CHEQUESTATUS_NEW.equals(presentmentDetail.getMandateStatus())) {
			if (PennantConstants.CHEQUESTATUS_PRESENT.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_PRESENT);
				return;
			}
			if (PennantConstants.CHEQUESTATUS_REALISE.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_REALISE);
				return;
			}
			if (PennantConstants.CHEQUESTATUS_REALISED.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_REALISED);
				return;
			}
			//COMMENTED THIS CODE FOR REPRESENTMENT PROCESS i.e, if Check got bounced also it shouldbe allowed for Representment
			/*
			 * if (PennantConstants.CHEQUESTATUS_BOUNCE.equals(presentmentDetail.getMandateStatus())) {
			 * presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_BOUNCE); }
			 */
		}

		// EMI HOLD
		if (DateUtility.compare(presentmentDetail.getDefSchdDate(), detailHeader.getToDate()) > 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		//at first advance interest and EMI then EMI advance 
		if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, detailHeader.getPresentmentType())) {
			processAdvAmounts(detailHeader, presentmentDetail);
		}

		// EMI IN ADVANCE
		//if there is no due no need to proceed further.
		if (presentmentDetail.getSchAmtDue().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		// EMI IN ADVANCE
		FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);
		if (finExcessAmount != null) {
			emiInAdvanceAmt = finExcessAmount.getBalanceAmt();

			if ((emiInAdvanceAmt.compareTo(BigDecimal.ZERO) > 0)
					&& emiInAdvanceAmt.compareTo(presentmentDetail.getSchAmtDue()) >= 0) {
				presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
				presentmentDetail.setPresentmentAmt(BigDecimal.ZERO);
				presentmentDetail.setAdvanceAmt(presentmentDetail.getSchAmtDue());
				presentmentDetail.setExcessID(finExcessAmount.getExcessID());
			} else {
				presentmentDetail.setPresentmentAmt(presentmentDetail.getSchAmtDue());
				presentmentDetail.setAdvanceAmt(BigDecimal.ZERO);

			}
		} else {
			presentmentDetail.setPresentmentAmt(presentmentDetail.getSchAmtDue());
			presentmentDetail.setAdvanceAmt(BigDecimal.ZERO);
		}

		BigDecimal advAmount = presentmentDetail.getAdvAdjusted();
		presentmentDetail.setPresentmentAmt(presentmentDetail.getPresentmentAmt().subtract(advAmount));
		presentmentDetail.setAdvanceAmt(presentmentDetail.getAdvanceAmt().add(advAmount));

		logger.debug(Literal.LEAVING);
	}

	private void doCalculations(PresentmentDetail presentmentDetail, PresentmentHeader detailHeader) {
		logger.debug(Literal.ENTERING);

		BigDecimal emiInAdvanceAmt;
		String finReference = presentmentDetail.getFinReference();

		// Mandate Rejected
		if (MandateConstants.STATUS_REJECTED.equals(presentmentDetail.getMandateStatus())) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_REJECTED);
			return;
		}

		// EMI HOLD
		if (DateUtility.compare(presentmentDetail.getDefSchdDate(), detailHeader.getToDate()) > 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		// Mandate Hold
		if (MandateConstants.STATUS_HOLD.equals(presentmentDetail.getMandateStatus())) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
			return;
		}

		boolean isECSMandate = MandateConstants.TYPE_ECS.equals(presentmentDetail.getMandateType());
		if (!isECSMandate) {
			if (!MandateConstants.STATUS_APPROVED.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
				return;
			}

			// Mandate Expired
			if (presentmentDetail.getMandateExpiryDate() != null && DateUtility
					.compare(presentmentDetail.getDefSchdDate(), presentmentDetail.getMandateExpiryDate()) > 0) {
				presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_EXPIRY);
				return;
			}
		}

		//at first advance interest and EMI then EMI advance 
		if (StringUtils.equalsIgnoreCase(PennantConstants.PROCESS_PRESENTMENT, detailHeader.getPresentmentType())) {
			processAdvAmounts(detailHeader, presentmentDetail);
		}

		// EMI IN ADVANCE
		//if there is no due no need to proceed further.
		if (presentmentDetail.getSchAmtDue().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		// EMI IN ADVANCE
		FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);
		if (finExcessAmount != null) {
			emiInAdvanceAmt = finExcessAmount.getBalanceAmt();
			presentmentDetail.setExcessID(finExcessAmount.getExcessID());
		} else {
			emiInAdvanceAmt = BigDecimal.ZERO;
		}

		if (emiInAdvanceAmt.compareTo(presentmentDetail.getSchAmtDue()) >= 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
			presentmentDetail.setPresentmentAmt(BigDecimal.ZERO);
			presentmentDetail.setAdvanceAmt(presentmentDetail.getSchAmtDue());
		} else {
			presentmentDetail.setPresentmentAmt(presentmentDetail.getSchAmtDue().subtract(emiInAdvanceAmt));
			presentmentDetail.setAdvanceAmt(emiInAdvanceAmt);
		}

		BigDecimal advAmount = presentmentDetail.getAdvAdjusted();
		presentmentDetail.setPresentmentAmt(presentmentDetail.getPresentmentAmt().subtract(advAmount));
		presentmentDetail.setAdvanceAmt(presentmentDetail.getAdvanceAmt().add(advAmount));

		logger.debug(Literal.LEAVING);
	}

	private long savePresentmentHeaderDetails(PresentmentHeader header) {
		logger.debug(Literal.ENTERING);
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

		logger.debug(Literal.LEAVING);
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

	protected void updateChequeStatus(long chequeDetailsId, String status) {
		chequeDetailDAO.updateChequeStatus(chequeDetailsId, status);
	}

	private void processAdvAmounts(PresentmentHeader detailHeader, PresentmentDetail prd) {

		AdvanceType advanceType = null;
		String amountType = "";

		if (prd.getGrcPeriodEndDate() != null && prd.getSchDate().compareTo(prd.getGrcPeriodEndDate()) <= 0) {
			advanceType = AdvanceType.getType(prd.getGrcAdvType());
		} else {
			advanceType = AdvanceType.getType(prd.getAdvType());
		}

		if (StringUtils.equals(prd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
			if (StringUtils.equals(prd.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
					&& SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE)) {
				advanceType = AdvanceType.AF;
			}
		}

		if (advanceType == null) {
			return;
		}

		//get excess
		String finRef = prd.getFinReference();
		int exculdeReason = 0;
		BigDecimal dueAmt = BigDecimal.ZERO;

		if (advanceType == AdvanceType.AE) {
			if (AdvanceStage.getStage(prd.getAdvStage()) == AdvanceStage.FE) {
				return;
			}
			amountType = RepayConstants.EXAMOUNTTYPE_ADVEMI;
			dueAmt = prd.getSchAmtDue();
			exculdeReason = RepayConstants.PEXC_ADVEMI;
		} else {
			amountType = RepayConstants.EXAMOUNTTYPE_ADVINT;
			dueAmt = prd.getSchPftDue();
			if (prd.getSchPriDue().compareTo(BigDecimal.ZERO) == 0) {
				exculdeReason = RepayConstants.PEXC_ADVINT;
			}
		}

		FinExcessAmount finExAmt = finExcessAmountDAO.getExcessAmountsByRefAndType(finRef, amountType);

		if (finExAmt != null) {
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

			if (adjAmount.compareTo(dueAmt) == 0) {
				prd.setAdvAdjusted(adjAmount);
				prd.setExcludeReason(exculdeReason);
			}

			//process advance moment
			finExAmt.setReservedAmt(adjAmount);
			BigDecimal amount = finExAmt.getAmount();
			BigDecimal reservedAmt = finExAmt.getReservedAmt();
			BigDecimal utilisedAmt = finExAmt.getUtilisedAmt();
			finExAmt.setBalanceAmt(amount.subtract(reservedAmt).subtract(utilisedAmt));
			finExcessAmountDAO.updateExcessReserve(finExAmt);
			//movement
			FinExcessMovement exMovement = new FinExcessMovement();
			exMovement.setExcessID(finExAmt.getExcessID());
			exMovement.setReceiptID(detailHeader.getId());//Setting presentment id as unique reference
			exMovement.setMovementFrom(RepayConstants.PAYTYPE_PRESENTMENT);
			exMovement.setAmount(adjAmount);
			exMovement.setSchDate(prd.getSchDate());
			exMovement.setMovementType("I");//in process
			exMovement.setTranType("I");//in process
			finExcessAmountDAO.saveExcessMovement(exMovement);
		}
	}
}
