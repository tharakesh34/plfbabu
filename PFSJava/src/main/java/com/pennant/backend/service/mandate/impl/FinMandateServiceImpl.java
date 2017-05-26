/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinMandateServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-10-2016    														*
 *                                                                  						*
 * Modified Date    :  26-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-10-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.service.mandate.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatus;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.util.ModuleUtil;

/**
 * Service implementation for methods that depends on <b>FinMandate</b>.<br>
 * 
 */
public class FinMandateServiceImpl implements FinMandateService {
	private final static Logger	logger	= Logger.getLogger(FinMandateServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private MandateDAO			mandateDAO;
	private MandateStatusDAO	mandateStatusDAO;
	private FinanceMainDAO		financeMainDAO;
	private BankBranchService	bankBranchService;

	public FinMandateServiceImpl() {
		super();
	}

	@Override
	public Mandate getMnadateByID(long mandateID) {
		return mandateDAO.getMandateById(mandateID, "_View");
	}

	@Override
	public List<Mandate> getMnadateByCustID(long custID, long mandateID) {
		return mandateDAO.getMnadateByCustID(custID, mandateID);
	}

	@Override
	public void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(" Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		Mandate mandate = financeDetail.getMandate();
		boolean isMandateReq = checkRepayMethod(finmain);
		if (isMandateReq) {
			if (mandate != null) {
				mandate.setCustID(finmain.getCustID());
				Mandate useExisting = checkExistingMandate(mandate.getMandateID());

				if (useExisting != null) {
					deleteMandate(finmain.getFinReference(), auditDetails);
					//set mandate id to finance
					finmain.setMandateID(mandate.getMandateID());
				} else {
					//check in flow table for new or old record 
					Mandate oldmandate = mandateDAO.getMandateByOrgReference(finmain.getFinReference(),
							MandateConstants.STATUS_FIN, tableType);

					if (oldmandate != null) {
						mandate.setMandateID(oldmandate.getMandateID());
						mandate.setOrgReference(finmain.getFinReference());
						mandate.setStatus(MandateConstants.STATUS_FIN);
						mandateDAO.updateFinMandate(mandate, tableType);
						auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_UPD));
					} else {
						mandate.setStatus(MandateConstants.STATUS_FIN);
						mandate.setOrgReference(finmain.getFinReference());
						long mandateID = mandateDAO.save(mandate, tableType);
						finmain.setMandateID(mandateID);
						auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));
					}
				}
			}
		} else {
			deleteMandate(finmain.getFinReference(), auditDetails);
			finmain.setMandateID(0);
		}
		addAudit(auditHeader, auditDetails);
		logger.debug(" Leaving ");
	}

	@Override
	public void doApprove(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType) {
		logger.debug(" Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		Mandate mandate = financeDetail.getMandate();

		boolean isMandateReq = checkRepayMethod(finmain);
		if (isMandateReq) {
			if (mandate != null) {
				mandate.setCustID(finmain.getCustID());
				Mandate useExisting = checkExistingMandate(mandate.getMandateID());
				if (useExisting != null) {
					//set mandate id to finance
					finmain.setMandateID(mandate.getMandateID());
					if (StringUtils.isEmpty(useExisting.getOrgReference())) {
						mandateDAO.updateOrgReferecne(mandate.getMandateID(), finmain.getFinReference(), "");
					}
				} else {
					mandate.setOrgReference(finmain.getFinReference());
					mandate.setStatus(MandateConstants.STATUS_NEW);
					mandate.setRecordType("");
					mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					long mandateID = mandateDAO.save(mandate, tableType);
					finmain.setMandateID(mandateID);
					auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));
					MandateStatus mandateStatus = new MandateStatus();
					mandateStatus.setMandateID(mandate.getMandateID());
					mandateStatus.setStatus(mandate.getStatus());
					mandateStatus.setReason(mandate.getReason());
					mandateStatus.setChangeDate(DateUtility.getAppDate());
					mandateStatusDAO.save(mandateStatus, "");
				}
			}
		} else {
			finmain.setMandateID(0);
		}

		deleteMandate(finmain.getFinReference(), auditDetails);
		addAudit(auditHeader, auditDetails);
		logger.debug(" Leaving ");
	}

	@Override
	public void doRejct(FinanceDetail financeDetail, AuditHeader auditHeader) {
		logger.debug(" Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		Mandate mandate = financeDetail.getMandate();
		if (mandate != null) {
			Mandate useExisting = checkExistingMandate(mandate.getMandateID());
			if (useExisting == null) {
				if (mandate.getBankBranchID() != 0 && mandate.getBankBranchID() != Long.MIN_VALUE) {
					mandate.setRecordType("");
					mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					mandate.setStatus(MandateConstants.STATUS_REJECTED);
					mandate.setReason(Labels.getLabel("Mandate_Rejected_In_Loan"));
					long mandateID = mandateDAO.save(mandate, "");
					finmain.setMandateID(mandateID);
					auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));

					MandateStatus mandateStatus = new MandateStatus();
					mandateStatus.setMandateID(mandate.getMandateID());
					mandateStatus.setStatus(mandate.getStatus());
					mandateStatus.setReason(Labels.getLabel("Mandate_Rejected_In_Loan"));
					mandateStatus.setChangeDate(DateUtility.getAppDate());
					mandateStatusDAO.save(mandateStatus, "");
				}

			}
		}
		deleteMandate(finmain.getFinReference(), auditDetails);
		addAudit(auditHeader, auditDetails);
		logger.debug(" Leaving ");
	}

	private void deleteMandate(String finreferece, List<AuditDetail> auditDetails) {
		//Check in temporary queue to get to know that the previous mandate is initiated from loan 
		Mandate oldmandate = mandateDAO.getMandateByOrgReference(finreferece, MandateConstants.STATUS_FIN, "_Temp");
		if (oldmandate != null) {
			//if found delete from temporary
			mandateDAO.delete(oldmandate, "_Temp");
			auditDetails.add(getAuditDetails(oldmandate, 2, PennantConstants.TRAN_DEL));
		}

	}

	private boolean checkRepayMethod(FinanceMain finmain) {
		String rpymentod = StringUtils.trimToEmpty(finmain.getFinRepayMethod());
		if (rpymentod.equals(MandateConstants.TYPE_ECS) || rpymentod.equals(MandateConstants.TYPE_DDM)
				|| rpymentod.equals(MandateConstants.TYPE_NACH)) {
			return true;
		} else {
			return false;
		}
	}

	private Mandate checkExistingMandate(long mandateID) {
		return mandateDAO.getMandateById(mandateID, "");
	}

	private void addAudit(AuditHeader auditHeader, List<AuditDetail> auditDetails) {
		//Add audit if any changes
		if (auditDetails.isEmpty()) {
			return;
		}
		AuditHeader header = getAuditHeader(auditHeader);
		header.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(header);
	}

	/**
	 * Validate the mandate assigned to the finance.
	 * 
	 * @param auditDetail
	 * @param financeDetail
	 * @param financeMain
	 */
	public void validateMandate(AuditDetail auditDetail, FinanceDetail financeDetail) {
		Mandate mandate = financeDetail.getMandate();

		if (!financeDetail.isActionSave() && mandate != null) {

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			if (mandate.getMaxLimit() != null && mandate.getMaxLimit().compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal exposure = BigDecimal.ZERO;

				for (FinanceScheduleDetail schedule : financeDetail.getFinScheduleData().getFinanceScheduleDetails()) {
					if (exposure.compareTo(schedule.getRepayAmount()) < 0) {
						exposure = schedule.getRepayAmount();
					}
				}

				if (mandate.isUseExisting()) {
					exposure = exposure.add(getFinanceMainDAO().getTotalMaxRepayAmount(mandate.getMandateID(),
							financeMain.getFinReference()));
				}

				if (mandate.getMaxLimit().compareTo(exposure) < 0) {
					auditDetail.setErrorDetail(90320);
				}

			}

			if (StringUtils.isNotBlank(mandate.getPeriodicity())) {

				if (!validatePayFrequency(financeMain.getRepayFrq().charAt(0), mandate.getPeriodicity().charAt(0))) {

					String[] errParmFrq = new String[2];
					errParmFrq[0] = PennantJavaUtil.getLabel("label_MandateDialog_Periodicity.value");
					errParmFrq[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepayFrq.value");

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "90220", errParmFrq, null), ""));
				}

				if (financeMain.isFinRepayPftOnFrq()) {
					if (!validatePayFrequency(financeMain.getRepayPftFrq().charAt(0),
							mandate.getPeriodicity().charAt(0))) {

						String[] errParmFrq = new String[2];
						errParmFrq[0] = PennantJavaUtil.getLabel("label_MandateDialog_Periodicity.value");
						errParmFrq[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepayPftFrq.value");

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "90220", errParmFrq, null), ""));

					}
				}
			}

		}
	}

	public void promptMandate(AuditDetail auditDetail, FinanceDetail financeDetail) {
		Mandate mandate = financeDetail.getMandate();
		if (!financeDetail.isActionSave() && mandate != null) {
			if (!mandate.isUseExisting()) {
				// prompt for Open Mandate
				int count = getMnadateByCustID(mandate.getCustID(), mandate.getMandateID()).size();
				if (count != 0) {
					String[] errParmMan = new String[2];
					String[] valueParmMan = new String[2];
					valueParmMan[0] = String.valueOf(mandate.getCustCIF());
					valueParmMan[1] = String.valueOf(count);

					errParmMan[0] = " CustCIF : " + valueParmMan[0];
					errParmMan[1] = valueParmMan[1];

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "65013", errParmMan, valueParmMan), ""));
				}
				
				if (mandate.getBankBranchID() != 0) {
					BankBranch bankBranch = bankBranchService.getBankBranchById(mandate.getBankBranchID());

					String mandateType = StringUtils.trimToEmpty(mandate.getMandateType());
					// prompt for Auto Debit
					if ((MandateConstants.TYPE_ECS.equals(mandateType)
							|| MandateConstants.TYPE_NACH.equals(mandateType)) && bankBranch.isDda()) {

						String[] errParmBranch = new String[1];
						String[] valueParmBranch = new String[1];
						valueParmBranch[0] = bankBranch.getBankName();
						errParmBranch[0] = valueParmBranch[0];

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "65016", errParmBranch, valueParmBranch),
								""));

					} else if (StringUtils.equals(mandateType, MandateConstants.TYPE_ECS) && bankBranch.isNach()) {

						String[] errParmBranch = new String[1];
						String[] valueParmBranch = new String[1];
						valueParmBranch[0] = bankBranch.getBankName();
						errParmBranch[0] = valueParmBranch[0];

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "65017", errParmBranch, valueParmBranch),
								""));
					}
				}
			}
		}
	}

	private Boolean validatePayFrequency(char repayFrq, char mandateFrq) {
		boolean valFrq = true;
		if (repayFrq == mandateFrq) {
			valFrq = true;
		} else {
			switch (repayFrq) {
			case 'D':
				if (mandateFrq != 'D') {
					valFrq = false;
				}
				break;
			case 'W':
				if (mandateFrq != 'D') {
					valFrq = false;
				}
				break;
			case 'X':
				if (mandateFrq != 'D' || mandateFrq != 'W') {
					valFrq = false;
				}
				break;
			case 'F':
				if (mandateFrq != 'D' || mandateFrq != 'W' || mandateFrq != 'X') {
					valFrq = false;
				}
				break;
			case 'M':
				if (mandateFrq == 'B' || mandateFrq == 'Q' || mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			case 'B':
				if (mandateFrq == 'Q' || mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;

			case 'Q':
				if (mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			case 'H':
				if (mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			}
		}
		return valFrq;
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(Mandate.class.getSimpleName()));
		newauditHeader.setAuditReference(auditHeader.getAuditReference());
		newauditHeader.setAuditUsrId(auditHeader.getAuditUsrId());
		newauditHeader.setAuditBranchCode(auditHeader.getAuditBranchCode());
		newauditHeader.setAuditDeptCode(auditHeader.getAuditDeptCode());
		newauditHeader.setAuditSystemIP(auditHeader.getAuditSystemIP());
		newauditHeader.setAuditSessionID(auditHeader.getAuditSessionID());
		newauditHeader.setUsrLanguage(auditHeader.getUsrLanguage());
		return newauditHeader;
	}

	public AuditDetail getAuditDetails(Mandate mandate, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new Mandate(), new Mandate().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], mandate.getBefImage(), mandate);
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public BankBranchService getBankBranchService() {
		return bankBranchService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

}