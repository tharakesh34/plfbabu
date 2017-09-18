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
 * FileName    		:  BranchServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.impl.BranchDAOImpl;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Branch</b>.<br>
 * 
 */
public class BranchServiceImpl extends GenericService<Branch> implements BranchService {
	private static Logger logger = Logger.getLogger(BranchDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private BranchDAO branchDAO;
	private PostingsDAO postingsDAO;

	public BranchServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}	
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}
	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTBranches/RMTBranches_Temp by using BranchDAO's save method b) Update
	 * the Record in the table. based on the module workFlow Configuration. by
	 * using BranchDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtRMTBranches by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		Branch branch = (Branch) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		
		if (branch.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (branch.isNew()) {
			branch.setBranchCode(getBranchDAO().save(branch,tableType));
			auditHeader.getAuditDetail().setModelData(branch);
			auditHeader.setAuditReference(branch.getBranchCode());
		}else{
			getBranchDAO().update(branch,tableType);
		}
		
		if(branch.getBefImage() != null && branch.getBefImage().isBranchIsActive() && !branch.isBranchIsActive()){
			getBranchDAO().updateApplicationAccess(PennantConstants.ALLOW_ACCESS_TO_APP, "N");
			SysParamUtil.setParmDetails(PennantConstants.ALLOW_ACCESS_TO_APP,  "N");

			List<ReturnDataSet> existingPostings = getPostingsDAO().getPostingsbyFinanceBranch(branch.getBranchCode());

			if(existingPostings != null && !existingPostings.isEmpty()){
				long linkedTranId = getPostingsDAO().getLinkedTransId();
				List<ReturnDataSet> executePostings = new ArrayList<ReturnDataSet>();
				List<ReturnDataSet> revPostings = preparePostingsForBranchChange(existingPostings,branch,linkedTranId,0,true);
				if(revPostings != null){
					executePostings.addAll(revPostings);
				}
				List<ReturnDataSet> newPostings = preparePostingsForBranchChange(existingPostings,branch,linkedTranId,revPostings==null?0:revPostings.size(),false);
				if(newPostings != null){
					executePostings.addAll(newPostings);
				}
				if(!executePostings.isEmpty()){
					getPostingsDAO().saveBatch(executePostings);
				}
			}

			getBranchDAO().updateFinanceBranch(branch, "_Temp");
			getBranchDAO().updateFinanceBranch(branch, "");

			getBranchDAO().updateApplicationAccess(PennantConstants.ALLOW_ACCESS_TO_APP, "Y");
			SysParamUtil.setParmDetails(PennantConstants.ALLOW_ACCESS_TO_APP,  "Y");
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	
	/**This method will prepare reversal postings for Finances under old finBranch
	 * @param existingPostings
	 * @return
	 */
	private List<ReturnDataSet> preparePostingsForBranchChange(List<ReturnDataSet> existingPostings,Branch branch,long linkedTranId,int seqNo,boolean isReversal){
		logger.debug("Entering");
		List<ReturnDataSet> finalPostings = null;
		if(existingPostings != null && !existingPostings.isEmpty()){
			String currAccount = "";
			String finReference = "";
			String tranCode = "";
			ReturnDataSet revDataSet = null;
			for (int i=0 ; i < existingPostings.size() ; i++) {
				ReturnDataSet returnDataSet =  existingPostings.get(i);
				if(StringUtils.equals(currAccount, returnDataSet.getAccount()) && 
						StringUtils.equals(finReference, returnDataSet.getFinReference()) && 
						StringUtils.equals(tranCode, returnDataSet.getTranCode())){
					if(revDataSet != null){
						revDataSet.setPostAmount(revDataSet.getPostAmount().add(returnDataSet.getPostAmount()));
					}
				}else{
					revDataSet = new ReturnDataSet();
					revDataSet.setAccount(returnDataSet.getAccount());
					revDataSet.setFinReference(returnDataSet.getFinReference());
					revDataSet.setFinEvent(AccountEventConstants.ACCEVENT_BRANCH_CLOSE);
					revDataSet.setPostAmount(returnDataSet.getPostAmount());
					revDataSet.setAcCcy(returnDataSet.getAcCcy());
					if(isReversal){
						revDataSet.setPostBranch(branch.getBranchCode());
						if(StringUtils.equals(AccountConstants.TRANCODE_CREDIT, returnDataSet.getTranCode())){
							revDataSet.setTranCode(AccountConstants.TRANCODE_DEBIT);
							revDataSet.setRevTranCode(AccountConstants.TRANCODE_CREDIT);
							revDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
						}else{
							revDataSet.setTranCode(AccountConstants.TRANCODE_CREDIT);
							revDataSet.setRevTranCode(AccountConstants.TRANCODE_DEBIT);
							revDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
						}
					}else{
						revDataSet.setPostBranch(branch.getNewBranchCode());
						revDataSet.setTranCode(returnDataSet.getTranCode());
						revDataSet.setRevTranCode(returnDataSet.getRevTranCode());
						revDataSet.setDrOrCr(returnDataSet.getDrOrCr());
					}
				}
				returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(returnDataSet.getAcCcy(), SysParamUtil.getAppCurrency(), revDataSet.getPostAmount()));
				currAccount = returnDataSet.getAccount();
				finReference = returnDataSet.getFinReference();
				tranCode = returnDataSet.getTranCode();
				if(revDataSet.getPostAmount().compareTo(BigDecimal.ZERO) != 0 && (i == existingPostings.size()-1 || 
					 !StringUtils.equals(currAccount, existingPostings.get(i+1).getAccount()) ||
								!StringUtils.equals(finReference, existingPostings.get(i+1).getFinReference()) ||
								!StringUtils.equals(tranCode, existingPostings.get(i+1).getTranCode()))){
					if(finalPostings == null){
						finalPostings = new ArrayList<ReturnDataSet>();
					}
					finalPostings.add(revDataSet);
				}
			}
		}
		if(finalPostings != null && !finalPostings.isEmpty()){
			Date dateAppDate = DateUtility.getAppDate();
			Date dateValueDate = DateUtility.getAppValueDate();
			for (ReturnDataSet retDataSet : finalPostings) {
				seqNo = seqNo+1;
				if (retDataSet.getLinkedTranId() == Long.MIN_VALUE) {
					retDataSet.setLinkedTranId(linkedTranId);
				}
				retDataSet.setPostref(String.valueOf(retDataSet.getLinkedTranId()+"-"+seqNo));
				retDataSet.setPostingId(retDataSet.getFinReference()+DateUtility.formatDate(new Date(), "yyyyMMddHHmmss")+
						StringUtils.leftPad(String.valueOf((long)((new Random()).nextDouble()*10000L)).trim(), 4,"0"));
				retDataSet.setShadowPosting(false);
				retDataSet.setPostDate(dateAppDate);
				retDataSet.setValueDate(dateValueDate);
				retDataSet.setAmountType(AccountConstants.TRANSENTRY_AMOUNTTYPE);
				retDataSet.setTranOrderId("1-1");
				if(isReversal){
					retDataSet.setTranDesc("Finance Branch Change Reversal Transactions");
				}else{
					retDataSet.setTranDesc("Finance Branch Change New Transactions");
				}
			}
		}
		logger.debug("Leaving");
		return finalPostings;
	}
	
	
	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTBranches by using BranchDAO's delete method with type as Blank
	 * 3) Audit the record in to AuditHeader and AdtRMTBranches by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Branch branch = (Branch) auditHeader.getAuditDetail().getModelData();
		getBranchDAO().delete(branch, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBranchById fetch the details by using BranchDAO's getBranchById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Branch
	 */
	@Override
	public Branch getBranchById(String id) {
		return getBranchDAO().getBranchById(id,"_View");
	}

	/**
	 * getApprovedBranchById fetch the details by using BranchDAO's getBranchById method .
	 * with parameter id and type as blank. it fetches the approved records from the RMTBranches.
	 * @param id (String)
	 * @return Branch
	 */
	public Branch getApprovedBranchById(String id) {
		return getBranchDAO().getBranchById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBranchDAO().delete with parameters branch,"" b) NEW Add new
	 * record in to main table by using getBranchDAO().save with parameters
	 * branch,"" c) EDIT Update record in the main table by using
	 * getBranchDAO().update with parameters branch,"" 3) Delete the record from
	 * the workFlow table by using getBranchDAO().delete with parameters
	 * branch,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTBranches
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtRMTBranches by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Branch branch = new Branch();
		BeanUtils.copyProperties((Branch) auditHeader.getAuditDetail().getModelData(), branch);
		getBranchDAO().delete(branch, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(branch.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(branchDAO.getBranchById(branch.getBranchCode(), ""));
		}

		if (branch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getBranchDAO().delete(branch, TableType.MAIN_TAB);

		} else {
			branch.setRoleCode("");
			branch.setNextRoleCode("");
			branch.setTaskId("");
			branch.setNextTaskId("");
			branch.setWorkflowId(0);

			if (branch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				branch.setRecordType("");
				getBranchDAO().save(branch, TableType.MAIN_TAB);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				branch.setRecordType("");
				getBranchDAO().update(branch, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(branch);
		
		if(branch.getBefImage() != null && branch.getBefImage().isBranchIsActive() && !branch.isBranchIsActive()){
			getBranchDAO().updateApplicationAccess(PennantConstants.ALLOW_ACCESS_TO_APP, "N");
			SysParamUtil.setParmDetails(PennantConstants.ALLOW_ACCESS_TO_APP,  "N");

			List<ReturnDataSet> existingPostings = getPostingsDAO().getPostingsbyFinanceBranch(branch.getBranchCode());

			if(existingPostings != null && !existingPostings.isEmpty()){
				long linkedTranId = getPostingsDAO().getLinkedTransId();
				List<ReturnDataSet> executePostings = new ArrayList<ReturnDataSet>();
				List<ReturnDataSet> revPostings = preparePostingsForBranchChange(existingPostings,branch,linkedTranId,0,true);
				if(revPostings != null){
					executePostings.addAll(revPostings);
				}
				List<ReturnDataSet> newPostings = preparePostingsForBranchChange(existingPostings,branch,linkedTranId,revPostings==null?0:revPostings.size(),false);
				if(newPostings != null){
					executePostings.addAll(newPostings);
				}
				if(!executePostings.isEmpty()){
					getPostingsDAO().saveBatch(executePostings);
				}
			}

			getBranchDAO().updateFinanceBranch(branch, "_Temp");
			getBranchDAO().updateFinanceBranch(branch, "");

			getBranchDAO().updateApplicationAccess(PennantConstants.ALLOW_ACCESS_TO_APP, "Y");
			SysParamUtil.setParmDetails(PennantConstants.ALLOW_ACCESS_TO_APP,  "Y");
		}
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBranchDAO().delete with parameters
	 * branch,"_Temp" 3) Audit the record in to AuditHeader and AdtRMTBranches
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Branch branch= (Branch) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBranchDAO().delete(branch, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getBranchDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage){
		logger.debug(Literal.ENTERING);

		// Get the model object.
		Branch branch = (Branch) auditDetail.getModelData();
		String code = branch.getBranchCode();

		// Check the unique keys.
		if (branch.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(branch.getRecordType())
				&& branchDAO
						.isDuplicateKey(code, branch.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_BranchCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
	
}