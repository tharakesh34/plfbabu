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
 * FileName    		:  DedupParmServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.dedup.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DedupParm</b>.<br>
 * 
 */
public class DedupParmServiceImpl extends GenericService<DedupParm> implements DedupParmService {
	private final static Logger logger = Logger.getLogger(DedupParmServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private DedupParmDAO dedupParmDAO;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public DedupParmDAO getDedupParmDAO() {
		return dedupParmDAO;
	}
	public void setDedupParmDAO(DedupParmDAO dedupParmDAO) {
		this.dedupParmDAO = dedupParmDAO;
	}

	public DedupParm getDedupParm() {
		return getDedupParmDAO().getDedupParm();
	}
	
	public DedupParm getNewDedupParm() {
		return getDedupParmDAO().getNewDedupParm();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List validate(String resultQuery,CustomerDedup customerDedup ) {
		return getDedupParmDAO().validate(resultQuery,customerDedup);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * DedupParams/DedupParams_Temp by using DedupParmDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using DedupParmDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtDedupParams by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();

		if (dedupParm.isWorkflow()) {
			tableType="_TEMP";
		}

		if (dedupParm.isNew()) {
			getDedupParmDAO().save(dedupParm,tableType);
		}else{
			getDedupParmDAO().update(dedupParm,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table DedupParams by using DedupParmDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtDedupParams by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();
		getDedupParmDAO().delete(dedupParm,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDedupParmById fetch the details by using DedupParmDAO's
	 * getDedupParmById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DedupParm
	 */
	@Override
	public DedupParm getDedupParmById(String id,String queryModule,String querySubCode) {
		return getDedupParmDAO().getDedupParmByID(id,queryModule,querySubCode,"_View");
	}

	/**
	 * getApprovedDedupParmById fetch the details by using DedupParmDAO's
	 * getDedupParmById method . with parameter id and type as blank. it fetches
	 * the approved records from the DedupParams.
	 * 
	 * @param id
	 *            (String)
	 * @return DedupParm
	 */
	@Override
	public DedupParm getApprovedDedupParmById(String id,String queryModule,String querySubCode) {
		return getDedupParmDAO().getDedupParmByID(id,queryModule,querySubCode,"");
	}

	@Override	
	public List<CustomerDedup> fetchCustomerDedupDetails(String userRole, CustomerDetails aCustomerDetails){
		DedupParm dedupParm = getApprovedDedupParmById(userRole, "Customer", aCustomerDetails.getCustomer().getLovDescCustCtgType());
		if (dedupParm!=null ) {
			String replaceString="";
			if (StringUtils.trimToEmpty(dedupParm.getSQLQuery()).contains(PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL)) {
				replaceString=PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL;
			}else if ( StringUtils.trimToEmpty(dedupParm.getSQLQuery()).contains(PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE)) {
				replaceString=PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE;
			}
				if (!"".equals(replaceString)) {
	                StringBuilder rule = new StringBuilder("");
	                //CustDocType = :CustDocType AND CustDocTitle = :CustDocTitle
	                if (aCustomerDetails.getCustomerDocumentsList() != null
	                        && aCustomerDetails.getCustomerDocumentsList().size() > 0) {
		                for (CustomerDocument customerDocument : aCustomerDetails
		                        .getCustomerDocumentsList()) {
			                if (!rule.toString().equals("")) {
				                rule.append("or");
			                }
			                rule.append("(" + PennantConstants.CUST_DEDUP_LISTFILED2 + " = '");
			                rule.append(customerDocument.getCustDocType());
			                rule.append("' AND " + PennantConstants.CUST_DEDUP_LISTFILED3 + " = '");
			                rule.append(customerDocument.getCustDocTitle());
			                rule.append("')");
		                }
	                }else{
	                	 rule.append(PennantConstants.CUST_DEDUP_LISTFILED2 + " IN (");
	                }
	                dedupParm.setSQLQuery(dedupParm.getSQLQuery().replace(replaceString,rule.toString()));
                }
				logger.debug(dedupParm.getSQLQuery());
            
        }
		
		
	
		if(dedupParm!=null){
			return getDedupParmDAO().fetchCustomerDedupDetails(aCustomerDetails.getCustDedup() ,dedupParm.getSQLQuery());	
		}
		return new ArrayList<CustomerDedup>();
	}	
	
	/**
	 * Prepare Finance Dedup object using Customer ID
	 */
	@Override
    public FinanceDedup getCustomerById(long custID) {
	    return getDedupParmDAO().getFinDedupByCustId(custID);
    }

	/**
	 * Method for Fetching Dedup Finance List using Customer Dedup Details
	 */
	@Override	
	public List<FinanceDedup> fetchFinDedupDetails(String queryCode, FinanceDedup aFinanceDedup){
		DedupParm dedupParm = getApprovedDedupParmById(queryCode, PennantConstants.DedupFinance, "L");
		
		if(dedupParm!=null){
			return getDedupParmDAO().fetchFinDedupDetails(aFinanceDedup ,dedupParm.getSQLQuery());	
		}
		return new ArrayList<FinanceDedup>();
	}	
	
	@Override	
	public List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail referenceDetail){
		return getDedupParmDAO().getQueryCodeList(referenceDetail, "_AFDView");
	}	
	
	/**
	 * This method refresh the Record.
	 * 
	 * @param DedupParm
	 *            (dedupParm)
	 * @return dedupParm
	 */
	@Override
	public DedupParm refresh(DedupParm dedupParm) {
		logger.debug("Entering");
		getDedupParmDAO().refresh(dedupParm);
		getDedupParmDAO().initialize(dedupParm);
		logger.debug("Leaving");
		return dedupParm;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDedupParmDAO().delete with parameters dedupParm,"" b) NEW Add
	 * new record in to main table by using getDedupParmDAO().save with
	 * parameters dedupParm,"" c) EDIT Update record in the main table by using
	 * getDedupParmDAO().update with parameters dedupParm,"" 3) Delete the
	 * record from the workFlow table by using getDedupParmDAO().delete with
	 * parameters dedupParm,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtDedupParams by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtDedupParams by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupParm dedupParm = new DedupParm();
		BeanUtils.copyProperties((DedupParm) auditHeader.getAuditDetail()
				.getModelData(), dedupParm);

		if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getDedupParmDAO().delete(dedupParm,"");

		} else {
			dedupParm.setRoleCode("");
			dedupParm.setNextRoleCode("");
			dedupParm.setTaskId("");
			dedupParm.setNextTaskId("");
			dedupParm.setWorkflowId(0);

			if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				dedupParm.setRecordType("");
				getDedupParmDAO().save(dedupParm,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				dedupParm.setRecordType("");
				getDedupParmDAO().update(dedupParm,"");
			}
		}

		getDedupParmDAO().delete(dedupParm,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dedupParm);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDedupParmDAO().delete with parameters
	 * dedupParm,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtDedupParams by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDedupParmDAO().delete(dedupParm,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getDedupParmDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		DedupParm dedupParm = (DedupParm) auditDetail.getModelData();

		DedupParm tempDedupParm = null;
		if (dedupParm.isWorkflow()) {
			tempDedupParm = getDedupParmDAO().getDedupParmByID(
					dedupParm.getQueryCode(),dedupParm.getQueryModule(),dedupParm.getQuerySubCode(), "_Temp");
		}
		DedupParm befDedupParm = getDedupParmDAO().getDedupParmByID(
				dedupParm.getQueryCode(),dedupParm.getQueryModule(),dedupParm.getQuerySubCode(), "");

		DedupParm oldDedupParm = dedupParm.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = dedupParm.getQueryCode();
		errParm[0] = PennantJavaUtil.getLabel("label_QueryCode") + ":" + valueParm[0];

		if (dedupParm.isNew()) { // for New record or new record into work flow

			if (!dedupParm.isWorkflow()) {// With out Work flow only new records
				if (befDedupParm != null) { // Record Already Exists in the
											// table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (dedupParm.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befDedupParm != null || tempDedupParm != null) { // if records already exists in
												// the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befDedupParm == null || tempDedupParm != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!dedupParm.isWorkflow()) { // With out Work flow for update and
											// delete

				if (befDedupParm == null) { // if records not exists in the main
											// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (oldDedupParm != null
							&& !oldDedupParm.getLastMntOn().equals(
									befDedupParm.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {

				if (tempDedupParm == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (oldDedupParm != null
						&& !oldDedupParm.getLastMntOn().equals(
								tempDedupParm.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove") || !dedupParm.isWorkflow()) {
			dedupParm.setBefImage(befDedupParm);
		}

		return auditDetail;
	}
	
}