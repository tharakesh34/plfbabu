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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatus;
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
					Mandate oldmandate = mandateDAO.getMandateByOrgReference(finmain.getFinReference(), MandateConstants.STATUS_FIN, tableType);

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
		Mandate oldmandate = mandateDAO.getMandateByOrgReference(finreferece,MandateConstants.STATUS_FIN, "_Temp");
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

}