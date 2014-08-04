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
 * FileName    		:  CommidityLoanDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters.impl;



import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinContributorHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.FinContributorDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CommidityLoanDetail</b>.<br>
 * 
 */
public class FinContributorDetailServiceImpl extends GenericService<CommidityLoanDetail> implements FinContributorDetailService {
	private final static Logger logger = Logger.getLogger(FinContributorDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinContributorHeaderDAO finContributorHeaderDAO;
	private FinContributorDetailDAO finContributorDetailDAO;
	
	@Override
	public FinContributorDetail getContributorDetailById(String finReference, long contributorBaseNo) {
		return getFinContributorDetailDAO().getFinContributorDetailByID(finReference, contributorBaseNo,"_View");
	}
	
	@Override
	public FinContributorDetail getApprovedContributorDetailById(String finReference, long contributorBaseNo) {
		return getFinContributorDetailDAO().getFinContributorDetailByID(finReference, contributorBaseNo, "_AView");
	}
	
	@Override
    public FinContributorDetail refresh(FinContributorDetail finContributorDetail) {
		logger.debug("Entering");
		getFinContributorDetailDAO().refresh(finContributorDetail);
		getFinContributorDetailDAO().initialize(finContributorDetail);
		logger.debug("Leaving");
		return finContributorDetail;
	}

	@Override
    public List<AuditDetail> saveOrUpdate(FinContributorHeader contributorHeader, String tableType,  String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(contributorHeader, contributorHeader.getExcludeFields());

		contributorHeader.setWorkflowId(0);
		if (contributorHeader.isNewRecord()) {
			getFinContributorHeaderDAO().save(contributorHeader, tableType);
		} else {
			getFinContributorHeaderDAO().update(contributorHeader, tableType);
		}

		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], contributorHeader.getBefImage(), contributorHeader));

		List<FinContributorDetail> contributorDetails = contributorHeader.getContributorDetailList();

		if (contributorDetails != null && !contributorDetails.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (FinContributorDetail contributorDetail : contributorDetails) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";

				contributorDetail.setWorkflowId(0);		
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (contributorDetail.isNewRecord()) {
					saveRecord = true;
					if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (contributorDetail.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = contributorDetail.getRecordType();
					recordStatus = contributorDetail.getRecordStatus();
					contributorDetail.setRecordType("");
					contributorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					getFinContributorDetailDAO().save(contributorDetail, tableType);
				}

				if (updateRecord) {
					getFinContributorDetailDAO().update(contributorDetail, tableType);
				}

				if (deleteRecord) {
					getFinContributorDetailDAO().delete(contributorDetail, tableType);
				}

				if (approveRec) {
					contributorDetail.setRecordType(rcdType);
					contributorDetail.setRecordStatus(recordStatus);
				}

				fields = PennantJavaUtil.getFieldDetails(contributorDetail, contributorDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], contributorDetail.getBefImage(), contributorDetail));
				i++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	@Override
    public List<AuditDetail> doApprove(FinContributorHeader contributorHeader, String tableType,  String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(contributorHeader, contributorHeader.getExcludeFields());
		CommidityLoanHeader header = new CommidityLoanHeader();
		BeanUtils.copyProperties(contributorHeader, header);
		
	
		contributorHeader.setRoleCode("");
		contributorHeader.setNextRoleCode("");
		contributorHeader.setTaskId("");
		contributorHeader.setNextTaskId("");
		contributorHeader.setWorkflowId(0);

		getFinContributorHeaderDAO().save(contributorHeader, tableType);

		auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], header.getBefImage(), header));
		auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], contributorHeader.getBefImage(), contributorHeader));

		List<FinContributorDetail> contributorDetails = contributorHeader.getContributorDetailList();

		if(contributorDetails !=null && !contributorDetails.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;
			
			for (FinContributorDetail contributorDetail : contributorDetails) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";

				CommidityLoanDetail detail = new CommidityLoanDetail();

				BeanUtils.copyProperties(contributorDetail, detail);
				contributorDetail.setWorkflowId(0);		
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (contributorDetail.isNewRecord()) {
					saveRecord = true;
					if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (contributorDetail.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = contributorDetail.getRecordType();
					recordStatus = contributorDetail.getRecordStatus();
					contributorDetail.setRecordType("");
					contributorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					getFinContributorDetailDAO().save(contributorDetail, tableType);
				}

				if (updateRecord) {
					getFinContributorDetailDAO().update(contributorDetail, tableType);
				}

				if (deleteRecord) {
					getFinContributorDetailDAO().delete(contributorDetail, tableType);
				}

				if (approveRec) {
					contributorDetail.setRecordType(rcdType);
					contributorDetail.setRecordStatus(recordStatus);
				}

				fields = PennantJavaUtil.getFieldDetails(contributorDetail, contributorDetail.getExcludeFields());
				auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], contributorDetail.getBefImage(), contributorDetail));
				i++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	@Override
    public List<AuditDetail> delete(FinContributorHeader contributorHeader, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(contributorHeader, contributorHeader.getExcludeFields());	
		String[] childFields = null;	

		List<FinContributorDetail> commidityLoanDetails = contributorHeader.getContributorDetailList(); 

		if(commidityLoanDetails != null && !commidityLoanDetails.isEmpty()) {
			for (FinContributorDetail commidityLoanDetail : commidityLoanDetails) {
				getFinContributorDetailDAO().delete(commidityLoanDetail, tableType);
				childFields = PennantJavaUtil.getFieldDetails(commidityLoanDetail, commidityLoanDetail.getExcludeFields());	
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, childFields[0], childFields[1], commidityLoanDetail.getBefImage(), commidityLoanDetail));
			}
		}

		getFinContributorHeaderDAO().delete(contributorHeader.getFinReference(), tableType);
		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], contributorHeader.getBefImage(), contributorHeader));

		logger.debug("Leaving");
		return auditDetails;
	}
	@Override
    public List<AuditDetail> validate(List<FinContributorDetail> finContributorDetails,  long workflowId, String method, String auditTranType, String usrLanguage) {
	    // TODO Auto-generated method stub
	    return null;
    }


	
	
	
	public AuditHeaderDAO getAuditHeaderDAO() {
    	return auditHeaderDAO;
    }
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
    	this.auditHeaderDAO = auditHeaderDAO;
    }
	public FinContributorHeaderDAO getFinContributorHeaderDAO() {
    	return finContributorHeaderDAO;
    }
	public void setFinContributorHeaderDAO(FinContributorHeaderDAO finContributorHeaderDAO) {
    	this.finContributorHeaderDAO = finContributorHeaderDAO;
    }
	public FinContributorDetailDAO getFinContributorDetailDAO() {
    	return finContributorDetailDAO;
    }
	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
    	this.finContributorDetailDAO = finContributorDetailDAO;
    }

	@Override
    public FinContributorHeader getContributorHeaderById(String id) {
	    // TODO Auto-generated method stub
	    return null;
    }



}