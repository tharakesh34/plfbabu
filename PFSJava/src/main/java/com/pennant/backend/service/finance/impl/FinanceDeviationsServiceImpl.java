package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceDeviationsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;

public class FinanceDeviationsServiceImpl implements FinanceDeviationsService {
	private static final Logger logger = Logger.getLogger(FinanceDeviationsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceDeviationsDAO deviationDetailsDAO;
	private FinanceMainDAO financeMainDAO;
	private TaskOwnersDAO taskOwnersDAO;

	private FinanceTypeDAO financeTypeDAO;
	private CustomerDetailsService customerDetailsService;
	private EligibilityDetailService eligibilityDetailService;
	private CheckListDetailService checkListDetailService;
	private FinanceScoreHeaderDAO financeScoreHeaderDAO;

	public FinanceDeviationsServiceImpl() {
		super();
	}

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceDeviationsDAO getDeviationDetailsDAO() {
		return deviationDetailsDAO;
	}

	public void setDeviationDetailsDAO(FinanceDeviationsDAO deviationDetailsDAO) {
		this.deviationDetailsDAO = deviationDetailsDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public List<FinanceDeviations> getApprovedFinanceDeviations(String finReference) {
		return getDeviationDetailsDAO().getFinanceDeviations(finReference, "");
	}

	@Override
	public List<FinanceDeviations> getFinanceDeviations(String finReference) {
		return getDeviationDetailsDAO().getFinanceDeviations(finReference, "_Temp");
	}
	
	@Override
	public FinanceMain getFinanceMain(String finReference) {
		return  getFinanceMainDAO().getFinanceMainById(finReference, "_View",false);
	}

	@Override
	public FinanceDetail getFinanceDetailById(String finReference) {
		logger.debug(" Entering ");
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "_View", false);
		FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(financeMain.getFinType(), "_AView");

		scheduleData.setFinanceMain(financeMain);
		scheduleData.setFinanceType(financeType);

		financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
		        financeMain.getCustID(), true, "_View"));

		financeDetail.setFinanceDeviations(getFinanceDeviations(finReference));
		financeDetail.setApprovedFinanceDeviations(getApprovedFinanceDeviations(finReference));

		//Finance Eligibility Rule Details List
		//=======================================
		String screenEvent="";
		if (StringUtils.trimToEmpty(financeMain.getFinPreApprovedRef()).equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL)) {
			screenEvent =	FinanceConstants.FINSER_EVENT_PREAPPROVAL;
        }else{
        	screenEvent = FinanceConstants.FINSER_EVENT_ORG;
        }
		
		financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(
		        finReference, financeMain.getFinCcy(), financeMain.getFinAmount(),
		        financeMain.isNewRecord(), financeMain.getFinType(), "",screenEvent));

		// set Check List Details 
		//=======================================
		getCheckListDetailService().setFinanceCheckListDetails(financeDetail,
		        financeMain.getFinType(),FinanceConstants.FINSER_EVENT_ORG, "");
		
		//Set Scoring details
		
		List<FinanceScoreHeader> list = getFinanceScoreHeaderDAO().getFinScoreHeaderList(finReference, "_View");
		
		if (list!=null && !list.isEmpty()) {
			
			financeDetail.setFinScoreHeaderList(list);
			List<Long> headerIds=new ArrayList<Long>(list.size());
			for (FinanceScoreHeader financeScoreHeader : list) {
				headerIds.add(financeScoreHeader.getHeaderId());
	        }
			List<FinanceScoreDetail> dellist = getFinanceScoreHeaderDAO().getFinScoreDetailList(headerIds, "_View");
			
			if (dellist!=null) {
				HashMap<Long, List<FinanceScoreDetail>> map = financeDetail.getScoreDetailListMap();
				for (FinanceScoreDetail financeScoreDetail : dellist) {
					if (map.containsKey(financeScoreDetail.getHeaderId())) {
						map.get(financeScoreDetail.getHeaderId()).add(financeScoreDetail);
	                }else{
	                	List<FinanceScoreDetail> slist=new ArrayList<FinanceScoreDetail>();
	                	slist.add(financeScoreDetail);
	                	map.put(financeScoreDetail.getHeaderId(), slist);
	                }
					
				}
		        
	        }
        }

		logger.debug(" Leaving ");
		return financeDetail;

	}

	@Override
	public void processDevaitions(String finreference, List<FinanceDeviations> newlist,AuditHeader auditHeader) {
		logger.debug(" Entering ");

		if (newlist == null) {
			return;
		}

		String tableType = "_Temp";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinanceDeviations> oldList = getFinanceDeviations(finreference);
		int count = 0; 

		//Checking records to save and Update
		for (FinanceDeviations newfindev : newlist) {

			FinanceDeviations oldFindev = getFinanceDeviationByID(oldList, newfindev);

			if (oldFindev == null) {
				getDeviationDetailsDAO().save(newfindev, tableType);
				auditDetails.add(getFinanceDeviationsAudit(newfindev, ++count,
				        PennantConstants.TRAN_ADD));

			} else {

				FinanceDeviations befImage = new FinanceDeviations();
				BeanUtils.copyProperties(oldFindev, befImage);
				newfindev.setBefImage(befImage);

				newfindev.setDeviationId(oldFindev.getId());
				getDeviationDetailsDAO().update(newfindev, tableType);
				auditDetails.add(getFinanceDeviationsAudit(newfindev, ++count,
				        PennantConstants.TRAN_UPD));

			}

		}

		//Delete the records which are not there in new List
		for (FinanceDeviations financeDeviations : oldList) {
			FinanceDeviations delRecod = getFinanceDeviationByID(newlist, financeDeviations);
			if (delRecod == null) {
				getDeviationDetailsDAO().delete(financeDeviations, tableType);
				financeDeviations.setBefImage(financeDeviations);
				auditDetails.add(getFinanceDeviationsAudit(financeDeviations, ++count,
				        PennantConstants.TRAN_DEL));
			}

		}

		//Add audit if any changes
		if (auditDetails.isEmpty()) {
			return;
		}

		AuditHeader devAuditHeader = getAuditHeader(auditHeader);
		devAuditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(devAuditHeader);

		logger.debug(" Leaving ");

	}

	private FinanceDeviations getFinanceDeviationByID(List<FinanceDeviations> list,
	        FinanceDeviations devNew) {
		for (FinanceDeviations financeDeviations : list) {
			if (StringUtils.equals(devNew.getModule(), financeDeviations.getModule())
			        && StringUtils.equals(devNew.getDeviationCode(),
			                financeDeviations.getDeviationCode())) {
				return financeDeviations;
			}
		}
		return null;
	}

	/** 
	 * 
	 */
	@Override
	public void processApproval(List<FinanceDeviations> list, AuditHeader auditHeader,String finreference) {
		logger.debug(" Entering ");
		if (list != null && !list.isEmpty()) {

			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			int count = 0;
			for (FinanceDeviations financeDeviations : list) {
				String status = StringUtils.trimToEmpty(financeDeviations.getApprovalStatus());
				if (StringUtils.isEmpty(status) || status.equals(PennantConstants.List_Select)) {
					continue;
				}

				getDeviationDetailsDAO().save(financeDeviations, "");
				auditDetails.add(getFinanceDeviationsAudit(financeDeviations, ++count,
				        PennantConstants.TRAN_ADD));

				getDeviationDetailsDAO().delete(financeDeviations, "_Temp");
				financeDeviations.setBefImage(financeDeviations);
				auditDetails.add(getFinanceDeviationsAudit(financeDeviations, ++count,
				        PennantConstants.TRAN_DEL));

			}

			//Add audit if any changes
			if (auditDetails.isEmpty()) {
				logger.debug(" Leaving ");
				return;
			}

			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		

		//if approval for all records came then remove for the deviation approval
		//update finance reference to remove for approval
		checkFinalApproval(finreference);
		logger.debug(" Leaving ");
	}

	/**
	 * @param finref
	 */
	public void checkFinalApproval(String finref) {
		logger.debug(" Entering ");
		List<FinanceDeviations> list = getDeviationDetailsDAO().getFinanceDeviations(finref, "_View");

		if (list != null && !list.isEmpty()) {

			boolean deviationApproved = true;
			boolean rejected = false;
			for (FinanceDeviations financeDeviations : list) {
				finref = financeDeviations.getFinReference();
				String status = StringUtils.trimToEmpty(financeDeviations.getApprovalStatus());
				if (StringUtils.isEmpty(status) || status.equals(PennantConstants.List_Select)) {
					deviationApproved = false;
					break;
				}
				//Check for at least one reject and then proceed
				if (!rejected && status.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					//Check if the rejected record previously approved now
					if (!foundApprovedRecord(financeDeviations,list)) {
						rejected = true;
                    }
				}

			}

			//if approval for all records came then remove for the deviation approval
			//update finance reference to remove for approval
			if (deviationApproved) {

				FinanceMain finmain = getFinanceMainDAO()
				        .getFinanceMainById(finref, "_Temp", false);

				//Remove deviation flag
				finmain.setDeviationApproval(false);

				if (rejected) {

					String roleCode = finmain.getRoleCode();
					String nextRoleCode = finmain.getNextRoleCode();
					String taskId = finmain.getTaskId();
					String nextUserid = StringUtils.trimToEmpty(finmain.getNextUserId());

					//if queue assignment process then
					if (StringUtils.isNotEmpty(nextUserid) && Long.parseLong(nextUserid) != 0) {
						/*	1. get the record with role code and finance reference 
							2. Update finance with the current owner and Update the update the record process flag to zero. 
							3. delete the record which assigned for next role Code.
						*/

						TaskOwners taskowner = getTaskOwnersDAO().getTaskOwner(finref, roleCode);
						if (taskowner != null) {
							finmain.setNextUserId(String.valueOf(taskowner.getCurrentOwner()));
							getTaskOwnersDAO().deviationReject(finref, roleCode, nextRoleCode);
						} else {
							finmain.setNextUserId(String.valueOf(0));
						}
					}

					finmain.setNextTaskId(taskId + ";");
					finmain.setNextRoleCode(roleCode);

				}
				getFinanceMainDAO().updateDeviationApproval(finmain, rejected, "_Temp");
			}
		}
		logger.debug(" Leaving ");
	}

	/**
	 * @param financeDeviations
	 * @param list
	 * @return
	 */
	private boolean foundApprovedRecord(FinanceDeviations financeDeviations,List<FinanceDeviations> list) {
		
		for (FinanceDeviations financeDeviation : list) {
			if (financeDeviations.getModule().equals(financeDeviation.getModule()) && 
					financeDeviations.getDeviationCode().equals(financeDeviation.getDeviationCode()) && 
					StringUtils.trimToEmpty(financeDeviation.getApprovalStatus()).equals(PennantConstants.RCD_STATUS_APPROVED)) {
				return true;
			}
		}
		return false;
	}

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(FinanceDeviations.class.getSimpleName()));
		newauditHeader.setAuditReference(auditHeader.getAuditReference());
		newauditHeader.setAuditUsrId(auditHeader.getAuditUsrId());
		newauditHeader.setAuditBranchCode(auditHeader.getAuditBranchCode());
		newauditHeader.setAuditDeptCode(auditHeader.getAuditDeptCode());
		newauditHeader.setAuditSystemIP(auditHeader.getAuditSystemIP());
		newauditHeader.setAuditSessionID(auditHeader.getAuditSessionID());
		newauditHeader.setUsrLanguage(auditHeader.getUsrLanguage());
		return newauditHeader;
	}

	public AuditDetail getFinanceDeviationsAudit(FinanceDeviations financeDeviations, int auditSeq,
	        String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceDeviations(),
		        new FinanceDeviations().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1],
		        financeDeviations.getBefImage(), financeDeviations);

	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public EligibilityDetailService getEligibilityDetailService() {
		return eligibilityDetailService;
	}

	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public CheckListDetailService getCheckListDetailService() {
		return checkListDetailService;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public TaskOwnersDAO getTaskOwnersDAO() {
		return taskOwnersDAO;
	}

	public void setTaskOwnersDAO(TaskOwnersDAO taskOwnersDAO) {
		this.taskOwnersDAO = taskOwnersDAO;
	}

	public FinanceScoreHeaderDAO getFinanceScoreHeaderDAO() {
	    return financeScoreHeaderDAO;
    }

	public void setFinanceScoreHeaderDAO(FinanceScoreHeaderDAO financeScoreHeaderDAO) {
	    this.financeScoreHeaderDAO = financeScoreHeaderDAO;
    }
}
