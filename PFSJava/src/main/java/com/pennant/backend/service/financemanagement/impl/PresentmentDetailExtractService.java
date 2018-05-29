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
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class PresentmentDetailExtractService {
	private static final Logger logger = Logger.getLogger(PresentmentDetailExtractService.class);

	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ChequeDetailDAO chequeDetailDAO;

	public PresentmentDetailExtractService(PresentmentDetailDAO presentmentDetailDAO, FinExcessAmountDAO finExcessAmountDAO, ChequeDetailDAO chequeDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
		this.finExcessAmountDAO = finExcessAmountDAO;
		this.chequeDetailDAO = chequeDetailDAO;
	}

	/*
	 * Processing the Presentments If the payment type isPDC
	 */
	public String savePDCPresentments(PresentmentHeader presentmentHeader) throws Exception{
		logger.debug(Literal.ENTERING);

		boolean isEmptyRecords = false;
		Map<Object, Long> map = new HashMap<Object, Long>();
		long presentmentId = 0;
		ResultSet rs = null;
		List<Object> resultList = null;
		try {
			resultList = presentmentDetailDAO.getPDCPresentmentDetails(presentmentHeader);
			rs = (ResultSet) resultList.get(0);
			while (rs.next()) {
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

				doPDCCalculations(pDetail, presentmentHeader);

				if (pDetail.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(pDetail.getExcessID(), "R", pDetail.getAdvanceAmt());
				}

				Date defSchDate = rs.getDate("DEFSCHDDATE");
				String bankCode = rs.getString("BANKCODE");
				String entity = rs.getString("ENTITYCODE");
				if (defSchDate != null) {
					if (!map.containsKey(defSchDate)
							|| (!map.containsKey(bankCode) && ImplementationConstants.GROUP_BATCH_BY_BANK)
							|| !map.containsKey(entity)) {
						presentmentHeader.setSchdate(defSchDate);
						presentmentHeader.setBankCode(bankCode);
						presentmentHeader.setEntityCode(entity);
						presentmentId = savePresentmentHeaderDetails(presentmentHeader);
						map.put(defSchDate, presentmentId);
						map.put(bankCode, presentmentId);
						map.put(entity, presentmentId);
					}
				}
				isEmptyRecords = true;

				// PresentmentDetail saving
				pDetail.setPresentmentId(presentmentId);
				long id = presentmentDetailDAO.save(pDetail, TableType.MAIN_TAB);

				// FinScheduleDetails update
				if (RepayConstants.PEXC_EMIINCLUDE == pDetail.getExcludeReason()) {
					presentmentDetailDAO.updateFinScheduleDetails(id, pDetail.getFinReference(), pDetail.getSchDate(), pDetail.getSchSeq());
					updateChequeStatus(pDetail.getMandateId(),PennantConstants.CHEQUESTATUS_PRESENT);
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
	public String savePresentments(PresentmentHeader presentmentHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean isEmptyRecords = false;
		Map<Object, Long> map = new HashMap<Object, Long>();
		long presentmentId = 0;
		ResultSet rs = null;
		List<Object> resultList = null;
		try {
			resultList = presentmentDetailDAO.getPresentmentDetails(presentmentHeader);
			rs = (ResultSet) resultList.get(0);
			while (rs.next()) {

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
				pDetail.setLastMntBy(presentmentHeader.getLastMntBy());
				pDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				pDetail.setWorkflowId(0);
				pDetail.setEntityCode(rs.getString("ENTITYCODE"));

				doCalculations(pDetail, presentmentHeader);

				if (pDetail.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(pDetail.getExcessID(), "R", pDetail.getAdvanceAmt());
				}

				Date defSchDate = rs.getDate("DEFSCHDDATE");
				String bankCode = rs.getString("BANKCODE");
				String entity = rs.getString("ENTITYCODE");
				if (defSchDate != null) {
					if (!map.containsKey(defSchDate)
							|| (!map.containsKey(bankCode) && ImplementationConstants.GROUP_BATCH_BY_BANK)
							|| !map.containsKey(entity)) {
						presentmentHeader.setSchdate(defSchDate);
						presentmentHeader.setBankCode(bankCode);
						presentmentHeader.setEntityCode(entity);
						presentmentId = savePresentmentHeaderDetails(presentmentHeader);
						map.put(defSchDate, presentmentId);
						map.put(bankCode, presentmentId);
						map.put(entity, presentmentId);
					}
				}
				isEmptyRecords = true;

				// PresentmentDetail saving
				pDetail.setPresentmentId(presentmentId);
				long id = presentmentDetailDAO.save(pDetail, TableType.MAIN_TAB);

				// FinScheduleDetails update
				if (RepayConstants.PEXC_EMIINCLUDE == pDetail.getExcludeReason()) {
					presentmentDetailDAO.updateFinScheduleDetails(id, pDetail.getFinReference(), pDetail.getSchDate(), pDetail.getSchSeq());
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
		if (!PennantConstants.CHEQUESTATUS_NEW.equals(presentmentDetail.getMandateStatus()) && !PennantConstants.CHEQUESTATUS_BOUNCE.equals(presentmentDetail.getMandateStatus())) {
			if (PennantConstants.CHEQUESTATUS_PRESENT.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_PRESENT);
			}
			if (PennantConstants.CHEQUESTATUS_REALISE.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_REALISE);
			}
			if (PennantConstants.CHEQUESTATUS_REALISED.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.CHEQUESTATUS_REALISED);
			}
			return;
		}
		
		// EMI HOLD
		if (DateUtility.compare(presentmentDetail.getDefSchdDate(), detailHeader.getToDate()) > 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}
		
		// EMI IN ADVANCE
		FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference, RepayConstants.EXAMOUNTTYPE_EMIINADV);
		if (finExcessAmount != null) {
			emiInAdvanceAmt = finExcessAmount.getBalanceAmt();
			if ((BigDecimal.ZERO.compareTo(emiInAdvanceAmt) > 0) && emiInAdvanceAmt.compareTo(presentmentDetail.getSchAmtDue()) >= 0) {
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
		
		logger.debug(Literal.LEAVING);
	}

	private long savePresentmentHeaderDetails(PresentmentHeader header) {
		logger.debug(Literal.ENTERING);
		
		long id = presentmentDetailDAO.getSeqNumber("SeqPresentmentHeader");
		String reference = StringUtils.leftPad(String.valueOf(id), 15, "0");
		header.setId(id);
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
		return id;

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
}
