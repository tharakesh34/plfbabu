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
 * FileName    		:  VASRecordingServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2016    														*
 *                                                                  						*
 * Modified Date    :  02-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.configuration.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.RelationshipOfficerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedField;
import com.pennant.backend.model.staticparms.ExtendedFieldData;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.collateral.impl.ExtendedFieldDetailsValidation;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pff.core.InterfaceException;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>VASRecording</b>.<br>
 * 
 */
public class VASRecordingServiceImpl extends GenericService<VASRecording> implements VASRecordingService {
	private static final Logger				logger	= Logger.getLogger(VASRecordingServiceImpl.class);

	private AuditHeaderDAO					auditHeaderDAO;
	private VASRecordingDAO					vASRecordingDAO;
	private FinanceReferenceDetailDAO		financeReferenceDetailDAO;
	private ExtendedFieldRenderDAO			extendedFieldRenderDAO;
	private DocumentDetailsDAO				documentDetailsDAO;

	private CheckListDetailService			checkListDetailService;
	private VASConfigurationService			vASConfigurationService;
	private CustomerDetailsService			customerDetailsService;

	private ExtendedFieldHeaderDAO			extendedFieldHeaderDAO;
	private ExtendedFieldDetailDAO			extendedFieldDetailDAO;
	private DocumentManagerDAO				documentManagerDAO;
	private FinanceCheckListReferenceDAO	financeCheckListReferenceDAO;
	private DocumentTypeDAO					documentTypeDAO;
	private CustomerDocumentDAO				customerDocumentDAO;
	private TransactionEntryDAO				transactionEntryDAO;
	private FinFeeDetailDAO					finFeeDetailDAO;
	private FinFeeScheduleDetailDAO			finFeeScheduleDetailDAO;

	// Validation Service Classes
	private DocumentDetailValidation		vasDocumentValidation;
	private ExtendedFieldDetailsValidation	extendedFieldDetailsValidation;
	private CustomerDAO						customerDAO;
	private CollateralSetupDAO				collateralSetupDAO;
	private VasRecordingValidation			vasRecordingValidation;
	private PostingsDAO 					postingsDAO;
	private AccountProcessUtil 				accountProcessUtil;
	private FinanceMainDAO 					financeMainDAO;
	private FinanceScheduleDetailDAO 		financeScheduleDetailDAO;
	private FinanceDisbursementDAO			financeDisbursementDAO;
	private RepayInstructionDAO				repayInstructionDAO;
	private RelationshipOfficerDAO			relationshipOfficerDAO;
	private ScriptValidationService 		scriptValidationService;
	private PostingsPreparationUtil			postingsPreparationUtil;
	private FinanceWorkFlowService			financeWorkFlowService;
	
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public VASRecordingDAO getVASRecordingDAO() {
		return vASRecordingDAO;
	}
	public void setVASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public VasRecordingValidation getVasRecordingValidation() {
		if (vasRecordingValidation == null) {
			this.vasRecordingValidation = new VasRecordingValidation(vASRecordingDAO);
		}
		return this.vasRecordingValidation;
	}


	/**
	 * @return the vASRecording
	 */
	@Override
	public VASRecording getVASRecording() {
		return getVASRecordingDAO().getVASRecording();
	}

	/**
	 * @return the vASRecording for New Record
	 */
	@Override
	public VASRecording getNewVASRecording() {
		return getVASRecordingDAO().getNewVASRecording();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table VASRecording/VASRecording_Temp
	 * by using VASRecordingDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using VASRecordingDAO's update method 3) Audit the record in to AuditHeader and AdtVASRecording
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table VASRecording/VASRecording_Temp
	 * by using VASRecordingDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using VASRecordingDAO's update method 3) Audit the record in to AuditHeader and AdtVASRecording
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		VASRecording vASRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();

		// VAS Cancellation process verification
		String errorCode = procVasCancellation(vASRecording, false);
		if(StringUtils.isNotBlank(errorCode)){
			ErrorDetails errorDetail = new ErrorDetails(errorCode, null);
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(errorDetail, auditHeader.getUsrLanguage()));
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tableType = "";

		if (vASRecording.isWorkflow()) {
			tableType = "_Temp";
		}

		if (vASRecording.isNew()) {
			getVASRecordingDAO().save(vASRecording, tableType);
		} else {
			getVASRecordingDAO().update(vASRecording, tableType);
		}

		//VAS documents
		if (vASRecording.getDocuments() != null && !vASRecording.getDocuments().isEmpty()) {
			List<AuditDetail> details = vASRecording.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, vASRecording, tableType);
			auditDetails.addAll(details);
		}

		// VAS CheckLists
		if (vASRecording.getVasCheckLists() != null && !vASRecording.getVasCheckLists().isEmpty()) {
			auditDetails.addAll(processingCheckListDetailsList(vASRecording, tableType));
		}

		// Vas Extended field Details
		if (vASRecording.getExtendedFieldRender() != null) {
			List<AuditDetail> details = vASRecording.getAuditDetailMap().get("ExtendedFieldDetail");
			details = processingExtendedFieldDetailList(details, vASRecording, tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * VASRecording by using VASRecordingDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtVASRecording by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		VASRecording vASRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();

		auditDetails.addAll(listDeletion(vASRecording, "", auditHeader.getAuditTranType()));
		getVASRecordingDAO().delete(vASRecording, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new VASRecording(), vASRecording.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], vASRecording.getBefImage(), vASRecording));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVASRecordingByRef fetch the details by using VASRecordingDAO's getVASRecordingByRef method.
	 * 
	 * @param vasReference
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public VASRecording getVASRecordingByRef(String vasReference, String nextRoleCode, boolean isEnquiry) {
		logger.debug("Entering");

		VASRecording vasRecording = getVASRecordingDAO().getVASRecordingByReference(vasReference, "_View");
		if (vasRecording != null) {

			// VasconfigurationDetails
			vasRecording.setVasConfiguration(getvASConfigurationService().getApprovedVASConfigurationByCode(vasRecording.getProductCode()));

			//Set CustomerDetails
			vasRecording.setVasCustomer(getvASRecordingDAO().getVasCustomerCif(vasRecording.getPrimaryLinkRef(), vasRecording.getPostingAgainst()));

			// Extended Field Details
			StringBuilder tableName = new StringBuilder();
			tableName.append(VASConsatnts.MODULE_NAME);
			tableName.append("_");
			tableName.append(vasRecording.getProductCode());
			tableName.append("_ED");

			Map<String, Object> extFieldMap = getExtendedFieldRenderDAO().getExtendedField(vasReference, tableName.toString(), "_View");
			ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
			if (extFieldMap != null) {
				extendedFieldRender.setReference(String.valueOf(extFieldMap.get("Reference")));
				extFieldMap.remove("Reference");
				extendedFieldRender.setSeqNo(Integer.valueOf(extFieldMap.get("SeqNo").toString()));
				extFieldMap.remove("SeqNo");
				extendedFieldRender.setVersion(Integer.valueOf(extFieldMap.get("Version").toString()));
				extFieldMap.remove("Version");
				extendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
				extFieldMap.remove("LastMntOn");
				extendedFieldRender.setLastMntBy(Long.valueOf(extFieldMap.get("LastMntBy").toString()));
				extFieldMap.remove("LastMntBy");
				extendedFieldRender.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? "" : String.valueOf(extFieldMap.get("RecordStatus")));
				extFieldMap.remove("RecordStatus");
				extendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? "" : String.valueOf(extFieldMap.get("RoleCode")));
				extFieldMap.remove("RoleCode");
				extendedFieldRender.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? "" : String.valueOf(extFieldMap.get("NextRoleCode")));
				extFieldMap.remove("NextRoleCode");
				extendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? "" : String.valueOf(extFieldMap.get("TaskId")));
				extFieldMap.remove("TaskId");
				extendedFieldRender.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null") ? "" : String.valueOf(extFieldMap.get("NextTaskId")));
				extFieldMap.remove("NextTaskId");
				extendedFieldRender.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null") ? "" : String.valueOf(extFieldMap.get("RecordType")));
				extFieldMap.remove("RecordType");
				extendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
				extFieldMap.remove("WorkflowId");
				extendedFieldRender.setMapValues(extFieldMap);
				vasRecording.setExtendedFieldRender(extendedFieldRender);
			}

			// Not Required Other Process details for the Enquiry
			if (!isEnquiry) {
				// Document Details
				List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(vasReference, VASConsatnts.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG, "_View");
				if (vasRecording.getDocuments() != null && !vasRecording.getDocuments().isEmpty()) {
					vasRecording.getDocuments().addAll(documentList);
				} else {
					vasRecording.setDocuments(documentList);
				}

				// Agreement Details & Check List Details
				if (StringUtils.isNotEmpty(vasRecording.getRecordType())
						&& !StringUtils.equals(vasRecording.getRecordType(), PennantConstants.RECORD_TYPE_UPD)
						&& !StringUtils.equals(vasRecording.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
					vasRecording = getProcessEditorDetails(vasRecording, nextRoleCode, FinanceConstants.FINSER_EVENT_ORG);
				}else if(StringUtils.equals("C", vasRecording.getVasStatus())|| StringUtils.isEmpty(vasRecording.getRecordType())){

					// Get details from Postings
					// Reversals of Tran Code and & DEBIT -CREDIT
					//set to Vas recording bean as Return dataset

					List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

					list = getPostingsDAO().getPostingsByPostref(vasRecording.getVasReference(), AccountEventConstants.ACCEVENT_VAS_FEE);

					for(ReturnDataSet returnDataSet:list){
						String tranCode = returnDataSet.getTranCode();
						String revTranCode = returnDataSet.getRevTranCode();
						String debitOrCredit = returnDataSet.getDrOrCr();

						returnDataSet.setTranCode(revTranCode);
						returnDataSet.setRevTranCode(tranCode);

						if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
							returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
						} else {
							returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
						}
					}

					vasRecording.setReturnDataSetList(list);
				}
			}
		}
		logger.debug("Leaving");
		return vasRecording;

	}

	/*
	 * Getting the customer details usnig teh 
	 */
	@Override
	public VasCustomer getVasCustomerDetails(String primaryLinkRef, String postingAgainst) {
		logger.debug("Entering");

		VasCustomer vasCustomer = getvASRecordingDAO().getVasCustomerCif(primaryLinkRef, postingAgainst);
		logger.debug("Leaving");
		return vasCustomer;
	}

	/**
	 * getApprovedVASRecordingByCode fetch the details by using VASRecordingDAO's getApprovedVASRecordingByCode method . with
	 * parameter productCode . it fetches the approved records from the VASRecording.
	 * 
	 * @param productCode
	 *            (String)
	 * @return VASRecording
	 */

	public VASRecording getVASRecordingByReference(String vasRefrence) {
		return getVASRecordingDAO().getVASRecordingByReference(vasRefrence, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getVASRecordingDAO().delete with
	 * parameters vASRecording,"" b) NEW Add new record in to main table by using getVASRecordingDAO().save with
	 * parameters vASRecording,"" c) EDIT Update record in the main table by using getVASRecordingDAO().update with
	 * parameters vASRecording,"" 3) Delete the record from the workFlow table by using getVASRecordingDAO().delete with
	 * parameters vASRecording,"_Temp" 4) Audit the record in to AuditHeader and AdtVASRecording by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtVASRecording by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */

	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		aAuditHeader = businessValidation(aAuditHeader, "doApprove", false);
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		VASRecording vASRecording = new VASRecording("");
		BeanUtils.copyProperties((VASRecording) auditHeader.getAuditDetail().getModelData(), vASRecording);

		// VAS Cancellation process verification
		String errorCode = procVasCancellation(vASRecording, true);
		if(StringUtils.isNotBlank(errorCode)){
			ErrorDetails errorDetail = new ErrorDetails(errorCode, null);
			aAuditHeader.setErrorDetails(ErrorUtil.getErrorDetail(errorDetail, auditHeader.getUsrLanguage()));
			logger.debug("Leaving");
			return aAuditHeader;
		}

		// Processing Accounting Details
		if(StringUtils.equals(vASRecording.getRecordType(), PennantConstants.RECORD_TYPE_NEW) 
				|| StringUtils.equals(vASRecording.getVasStatus(),VASConsatnts.STATUS_CANCEL)){
			executeAccountingProcess(auditHeader, DateUtility.getAppDate());
		}

		if (vASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(vASRecording, "", tranType));
			getVASRecordingDAO().delete(vASRecording, "");
		} else {
			vASRecording.setRoleCode("");
			vASRecording.setNextRoleCode("");
			vASRecording.setTaskId("");
			vASRecording.setNextTaskId("");
			vASRecording.setWorkflowId(0);

			if (vASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				vASRecording.setRecordType("");
				getVASRecordingDAO().save(vASRecording, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				vASRecording.setRecordType("");
				getVASRecordingDAO().update(vASRecording, "");
			}

			// Checklist details
			if (vASRecording.getVasCheckLists() != null && !vASRecording.getVasCheckLists().isEmpty()) {
				auditDetails.addAll(processingCheckListDetailsList(vASRecording, ""));
			}

			//VAS Document Details
			List<DocumentDetails> documentsList = vASRecording.getDocuments();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = vASRecording.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, vASRecording, "");
				auditDetails.addAll(details);
			}

			// VAS Extended field Details
			if (vASRecording.getExtendedFieldRender() != null) {
				List<AuditDetail> details = vASRecording.getAuditDetailMap().get("ExtendedFieldDetail");
				details = processingExtendedFieldDetailList(details, vASRecording, "");
				auditDetails.addAll(details);
			}
		}
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new VASRecording(), vASRecording.getExcludeFields());
		if (!StringUtils.equals(vASRecording.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditDetailList.addAll(listDeletion(vASRecording, "_Temp", auditHeader.getAuditTranType()));
			getVASRecordingDAO().delete(vASRecording, "_Temp");

			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], vASRecording.getBefImage(), vASRecording));
			auditHeader.setAuditDetails(auditDetailList);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], vASRecording.getBefImage(), vASRecording));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getVASRecordingDAO().delete with parameters vASRecording,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtVASRecording by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		VASRecording vASRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new VASRecording(), vASRecording.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], vASRecording.getBefImage(), vASRecording));

		auditDetails.addAll(listDeletion(vASRecording, "_Temp", auditHeader.getAuditTranType()));
		getVASRecordingDAO().delete(vASRecording, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method for Checking Cancellation of VAS Record
	 * @param recording
	 */
	private String procVasCancellation(VASRecording recording, boolean approvalProcess){
		logger.debug("Entering");

		// VAS Cancellation validations
		if(!StringUtils.equals(recording.getVasStatus(), VASConsatnts.STATUS_CANCEL) || !recording.isFinanceProcess()){
			logger.debug("Leaving");
			return null;
		}

		// Get Fee detail record
		FinFeeDetail vasFeeDetail = finFeeDetailDAO.getVasFeeDetailById(recording.getVasReference(), false, "");
		if(vasFeeDetail == null){
			logger.debug("Leaving");
			return "90323";
		}

		// If No Balance amount to schedule effected
		if(vasFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) == 0){
			finFeeDetailDAO.statusUpdate(vasFeeDetail.getFeeID(), FinanceConstants.FEE_STATUS_CANCEL, false, "");
			logger.debug("Leaving");
			return null;
		}

		// If No Schedule Maintenance , no need to check Finance Details
		if(!StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE) &&
				!StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR) &&
				!StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS) &&
				!StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)){

			finFeeDetailDAO.statusUpdate(vasFeeDetail.getFeeID(), FinanceConstants.FEE_STATUS_CANCEL, false, "");
			logger.debug("Leaving");
			return null;
		}

		// Check Finance Maintenance Status if any and stop except "Deduct From Disbursement"
		boolean finOnMaintenance = financeMainDAO.isFinReferenceExists(recording.getPrimaryLinkRef(), "_Temp", false);
		if(finOnMaintenance){
			logger.debug("Leaving");
			return "90324";
		}

		// Get Schedule Details of Primary Link Reference and check paid Status
		FinScheduleData scheduleData = getFinSchDataByFinRef(recording.getPrimaryLinkRef());
		List<FinanceScheduleDetail> schdList = scheduleData.getFinanceScheduleDetails();
		boolean isSchdPaid = false;
		Date recalFromDate = null;
		for (int i = 1; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if(recalFromDate == null){
				recalFromDate = curSchd.getSchDate();
			}
			if(curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || 
					curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 ||
					curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0 ||
					curSchd.getSchdInsPaid().compareTo(BigDecimal.ZERO) > 0){
				if(!StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())){
					isSchdPaid = true;
					break;
				}
			}
		}

		// If Schedules Paid for any dates then VAS cancellation not allowed
		if(isSchdPaid){
			logger.debug("Leaving");
			return "90325";
		}

		// If not approval Process, No need of actual updations Only validation is enough
		if(!approvalProcess){
			logger.debug("Leaving");
			return null;
		}

		// Based on Fee Schedule Method schedules need to Reset
		// 1. Deduct From Disbursement : No Change
		// 2. Add to Disbursement on Profit Calculation : Do partial Settlement on Start Date and Recalculate total EMI
		// 3. Schedule Terms(First Term / N Terms / Entire Tenor) : Schedule Reversal of Sum Amount in Fees
		if(StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){

			FinanceMain financeMain = scheduleData.getFinanceMain();
			financeMain.setEventFromDate(financeMain.getFinStartDate());
			financeMain.setEventToDate(financeMain.getMaturityDate());
			financeMain.setRecalFromDate(recalFromDate);
			financeMain.setRecalToDate(financeMain.getMaturityDate());
			financeMain.setFeeChargeAmt(financeMain.getFeeChargeAmt().subtract(vasFeeDetail.getRemainingFee()));

			// Disbursement Details updation
			for (FinanceDisbursement cusrDisb : scheduleData.getDisbursementDetails()) {
				if(DateUtility.compare(cusrDisb.getDisbDate(), financeMain.getFinStartDate()) == 0){
					cusrDisb.setFeeChargeAmt(cusrDisb.getFeeChargeAmt().subtract(vasFeeDetail.getRemainingFee()));
					break;
				}
			}

			// Schedule Details Updation
			for (FinanceScheduleDetail curSchd : schdList) {
				if(DateUtility.compare(curSchd.getSchDate(), financeMain.getFinStartDate()) == 0){
					curSchd.setFeeChargeAmt(curSchd.getFeeChargeAmt().subtract(vasFeeDetail.getRemainingFee()));
					break;
				}
			}

			// Schedule Recalculation
			scheduleData = ScheduleCalculator.reCalSchd(scheduleData, financeMain.getScheduleMethod());

			// Finance Data Deletion
			listDeletion(financeMain.getFinReference());

			// Schedule Updations
			scheduleData.getFinanceMain().setVersion(financeMain.getVersion() + 1);
			financeMainDAO.update(scheduleData.getFinanceMain(), TableType.MAIN_TAB, false);
			listSave(scheduleData);

		}else if(StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR) ||
				StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS) ||
				StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)){

			List<FinFeeScheduleDetail> feeSchdList = finFeeScheduleDetailDAO.getFeeScheduleByFeeID(vasFeeDetail.getFeeID(), false, "");
			for (FinFeeScheduleDetail feeSchd : feeSchdList) {

				// Schedule Details Updation
				for (FinanceScheduleDetail curSchd : schdList) {
					if(DateUtility.compare(curSchd.getSchDate(), feeSchd.getSchDate()) == 0){
						curSchd.setFeeSchd(curSchd.getFeeSchd().subtract(feeSchd.getSchAmount()));
						curSchd.setSchdFeeOS(curSchd.getSchdFeeOS().subtract(feeSchd.getSchAmount()));
						break;
					}
				}
			}

			financeScheduleDetailDAO.deleteByFinReference(scheduleData.getFinanceMain().getFinReference(), "", false, 0);
			financeScheduleDetailDAO.saveList(schdList, "", false);

		}

		// Update Fee Details record with Cancel status
		finFeeDetailDAO.statusUpdate(vasFeeDetail.getFeeID(), FinanceConstants.FEE_STATUS_CANCEL, false, "");

		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	private FinScheduleData getFinSchDataByFinRef(String finReference) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceMain(financeMainDAO.getFinanceMainById(finReference, "", false));
		finSchData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false));
		finSchData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finReference,"", false));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finReference, "", false));
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to delete schedule data
	 * 
	 * @param scheduleData
	 * @param tableType
	 * @param isWIF
	 */
	private void listDeletion(String finReference) {
		logger.debug("Entering");
		financeScheduleDetailDAO.deleteByFinReference(finReference, "", false, 0);
		financeDisbursementDAO.deleteByFinReference(finReference, "", false, 0);
		repayInstructionDAO.deleteByFinReference(finReference, "", false, 0);
		logger.debug("Leaving");
	}

	/**
	 * Method to save Finance Details
	 */
	private void listSave(FinScheduleData schData) {
		logger.debug("Entering");

		for (int i = 0; i < schData.getFinanceScheduleDetails().size(); i++) {
			schData.getFinanceScheduleDetails().get(i).setFinReference(schData.getFinanceMain().getFinReference());
		}
		financeScheduleDetailDAO.saveList(schData.getFinanceScheduleDetails(), "", false);
		
		for (int i = 0; i < schData.getDisbursementDetails().size(); i++) {
			schData.getDisbursementDetails().get(i).setFinReference(schData.getFinanceMain().getFinReference());
		}
		financeDisbursementDAO.saveList(schData.getDisbursementDetails(), "", false);
		
		for (int i = 0; i < schData.getRepayInstructions().size(); i++) {
			schData.getRepayInstructions().get(i).setFinReference(schData.getFinanceMain().getFinReference());
		}
		repayInstructionDAO.saveList(schData.getRepayInstructions(), "", false);

		logger.debug("Leaving");
	}

	/**
	 * businessValidation method do the following steps. 
	 * 1) validate the audit detail 
	 * 2) if any error/Warnings then assign the to auditHeader 
	 * 3) identify the next process
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = getVasRecordingValidation().validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		VASRecording vasRecording = (VASRecording) auditDetail.getModelData();
		String usrLanguage = vasRecording.getUserDetails().getUsrLanguage();

		//VAS Check List Details
		List<FinanceCheckListReference> vasCheckList = vasRecording.getVasCheckLists();
		if (vasCheckList != null && !vasCheckList.isEmpty()) {
			List<AuditDetail> auditDetailList = vasRecording.getAuditDetailMap().get("CheckListDetails");
			auditDetailList = getCheckListDetailService().validate(auditDetailList, method, usrLanguage);
			auditDetails.addAll(auditDetailList);
		}

		//VAS Document details Validation
		List<DocumentDetails> documentDetailsList = vasRecording.getDocuments();
		if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
			List<AuditDetail> details = vasRecording.getAuditDetailMap().get("DocumentDetails");
			details = getVASDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Extended field details Validation
		if (vasRecording.getExtendedFieldRender() != null) {
			List<AuditDetail> details = vasRecording.getAuditDetailMap().get("ExtendedFieldDetail");
			ExtendedFieldHeader extendedFieldHeader = vasRecording.getVasConfiguration().getExtendedFieldHeader();
			StringBuilder sb = new StringBuilder();
			sb.append(extendedFieldHeader.getModuleName());
			sb.append("_");
			sb.append(extendedFieldHeader.getSubModuleName());
			sb.append("_ED");
			details = getExtendedFieldDetailsValidation().vaildateDetails(details, method, usrLanguage, sb.toString());
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}


	/**
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public VASRecording getProcessEditorDetails(VASRecording vasRecording, String nextRoleCode, String procEdtEvent) {
		logger.debug("Entering");

		String productCode = vasRecording.getProductCode();
		List<FinanceReferenceDetail> aggrementList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> checkListdetails = new ArrayList<FinanceReferenceDetail>(1);

		// Fetch Total Process editor Details 
		List<FinanceReferenceDetail> finRefDetails = getFinanceReferenceDetailDAO().getFinanceProcessEditorDetails(
				productCode, StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent, "_VASVIEW");

		if (finRefDetails != null && !finRefDetails.isEmpty()) {
			for (FinanceReferenceDetail finrefDetail : finRefDetails) {
				if ((!finrefDetail.isIsActive()) || StringUtils.isEmpty(finrefDetail.getLovDescRefDesc())) {
					continue;
				}
				if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_CHECKLIST) {
					if (StringUtils.trimToEmpty(finrefDetail.getShowInStage()).contains((nextRoleCode + ","))) {
						checkListdetails.add(finrefDetail);
					}
				} else if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_AGREEMENT) {
					if (StringUtils.trimToEmpty(finrefDetail.getMandInputInStage()).contains((nextRoleCode + ","))) {
						aggrementList.add(finrefDetail);
					}
				}
			}
		}
		//Finance Agreement Details	
		vasRecording.setAggrements(aggrementList);

		//Check list Details
		getCheckListDetailService().fetchVASCheckLists(vasRecording, checkListdetails);

		logger.debug("Leaving");
		return vasRecording;
	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param vasRecording
	 * @param tableType
	 * @return
	 */
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails, VASRecording vasRecording, String tableType) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}
			if (!documentDetails.isDocIsCustDoc()) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				boolean isTempRecord = false;
				if (StringUtils.isEmpty(tableType) || tableType.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setLastMntBy(vasRecording.getLastMntBy());
				documentDetails.setWorkflowId(0);

				if (documentDetails.isDocIsCustDoc()) {
					approveRec = true;
				}
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
					isTempRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
					if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (documentDetails.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = documentDetails.getRecordType();
					recordStatus = documentDetails.getRecordStatus();
					documentDetails.setRecordType("");
					documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
						documentDetails.setReferenceId(vasRecording.getVasReference());
					}
					documentDetails.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
					// Save the document (documentDetails object) into DocumentManagerTable using documentManagerDAO.save(?) get the long Id.
					// This will be used in the getDocumentDetailsDAO().save, Update & delete methods
					if (documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					// Pass the docRefId here to save this in place of docImage column. Or add another column for now to save this.
					getDocumentDetailsDAO().save(documentDetails, tableType);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
					if (documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					getDocumentDetailsDAO().update(documentDetails, tableType);
				}

				if (deleteRecord && ((StringUtils.isEmpty(tableType) && !isTempRecord) || (StringUtils.isNotEmpty(tableType)))) {
					if (!tableType.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						getDocumentDetailsDAO().delete(documentDetails, tableType);
					}
				}
				if (approveRec) {
					documentDetails.setFinEvent("");
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			} else {
				CustomerDocument custdoc = getCustomerDocument(documentDetails, vasRecording);
				if (custdoc.isNewRecord()) {
					getCustomerDocumentDAO().save(custdoc, "");
				} else {
					getCustomerDocumentDAO().update(custdoc, "");
				}
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List Details
	 * 
	 * @param auditDetails
	 * @param vasRecording
	 * @param tableType
	 * @return
	 */
	private List<AuditDetail> processingCheckListDetailsList(VASRecording vasRecording, String tableType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = vasRecording.getAuditDetailMap().get("CheckListDetails");

		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference vasChecklistRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();

			vasChecklistRef.setWorkflowId(0);

			if (StringUtils.isEmpty(tableType)) {
				vasChecklistRef.setVersion(vasChecklistRef.getVersion() + 1);
				vasChecklistRef.setRoleCode("");
				vasChecklistRef.setNextRoleCode("");
				vasChecklistRef.setTaskId("");
				vasChecklistRef.setNextTaskId("");
			}

			if (vasChecklistRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
				vasChecklistRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				getFinanceCheckListReferenceDAO().save(vasChecklistRef, tableType);
			} else if (vasChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				getFinanceCheckListReferenceDAO().delete(vasChecklistRef, tableType);
			} else if (vasChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				getFinanceCheckListReferenceDAO().update(vasChecklistRef, tableType);
			}
			auditDetails.get(i).setModelData(vasChecklistRef);
		}

		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Extended FieldDetails
	 * 
	 * @param auditDetails
	 * @param vasRecording
	 * @param tableType
	 * @return
	 */
	private List<AuditDetail> processingExtendedFieldDetailList(List<AuditDetail> auditDetails, VASRecording vasRecording, String tableType) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		StringBuilder tableName = new StringBuilder();
		tableName.append(VASConsatnts.MODULE_NAME);
		tableName.append("_");
		tableName.append(vasRecording.getProductCode());
		tableName.append("_ED");

		for (int i = 0; i < auditDetails.size(); i++) {
			ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetails.get(i).getModelData();

			if (StringUtils.isEmpty(extendedFieldRender.getRecordType())) {
				continue;
			}
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType)) {
				approveRec = true;
				extendedFieldRender.setRoleCode("");
				extendedFieldRender.setNextRoleCode("");
				extendedFieldRender.setTaskId("");
				extendedFieldRender.setNextTaskId("");
			}

			//Table Name addition for Audit
			extendedFieldRender.setTableName(tableName.toString());
			extendedFieldRender.setWorkflowId(0);

			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (extendedFieldRender.isNewRecord()) {
				saveRecord = true;
				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (extendedFieldRender.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = extendedFieldRender.getRecordType();
				recordStatus = extendedFieldRender.getRecordStatus();
				extendedFieldRender.setRecordType("");
				extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			// Add Common Fields
			HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			if (saveRecord || updateRecord) {
				if (saveRecord) {
					mapValues.put("Reference", extendedFieldRender.getReference());
					mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
				}
				mapValues.put("Version", extendedFieldRender.getVersion());
				mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
				mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
				mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
				mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
				mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
				mapValues.put("TaskId", extendedFieldRender.getTaskId());
				mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
				mapValues.put("RecordType", extendedFieldRender.getRecordType());
				mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			}

			if (saveRecord) {
				getExtendedFieldRenderDAO().save(extendedFieldRender.getMapValues(), tableType, tableName.toString());
			}

			if (updateRecord) {
				getExtendedFieldRenderDAO().update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						extendedFieldRender.getMapValues(), tableType, tableName.toString());
			}

			if (deleteRecord) {
				getExtendedFieldRenderDAO().delete(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						tableType, tableName.toString());
			}
			if (approveRec) {
				extendedFieldRender.setRecordType(rcdType);
				extendedFieldRender.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended fields
			extendedFieldRender.setBefImage(extendedFieldRender);
			auditDetails.get(i).setExtended(true);
			auditDetails.get(i).setModelData(extendedFieldRender);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		VASRecording vasRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (vasRecording.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		//VAS Document Details
		if (vasRecording.getDocuments() != null && vasRecording.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(vasRecording, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		//Collateral Checklist Details
		List<FinanceCheckListReference> vasCheckLists = vasRecording.getVasCheckLists();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (vasCheckLists != null && !vasCheckLists.isEmpty()) {
				auditDetailMap.put("CheckListDetails", setCheckListsAuditData(vasRecording, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CheckListDetails"));
			}
		} else {
			String tableType = "_Temp";
			if (vasRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}
			vasCheckLists = getCheckListDetailService().getCheckListByFinRef(vasRecording.getVasReference(), tableType);
			vasRecording.setVasCheckLists(vasCheckLists);

			if (vasCheckLists != null && !vasCheckLists.isEmpty()) {
				auditDetailMap.put("CheckListDetails", setCheckListsAuditData(vasRecording, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CheckListDetails"));
			}
		}

		// VAS Extended Field Details
		if (vasRecording.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetail", setExtendedFieldsAuditData(vasRecording, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetail"));
		}

		vasRecording.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(vasRecording);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details for DocumentDetails
	 * 
	 * @param vasRecording
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(VASRecording vasRecording, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < vasRecording.getDocuments().size(); i++) {
			DocumentDetails documentDetails = vasRecording.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(vasRecording.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (vasRecording.isWorkflow()) {
					isRcdType = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(vasRecording.getRecordStatus());
			documentDetails.setUserDetails(vasRecording.getUserDetails());
			documentDetails.setLastMntOn(vasRecording.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for checkLists 
	 * 
	 * @param vasRecording
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCheckListsAuditData(VASRecording vasRecording, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceCheckListReference object = new FinanceCheckListReference();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < vasRecording.getVasCheckLists().size(); i++) {
			FinanceCheckListReference vasChekListRef = vasRecording.getVasCheckLists().get(i);

			vasChekListRef.setFinReference(vasRecording.getVasReference());

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(vasChekListRef.getRecordType()))) {
				continue;
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (vasChekListRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
					auditTranType = PennantConstants.TRAN_ADD;
					vasChekListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (vasChekListRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_UPD;
				} else {
					auditTranType = PennantConstants.RCD_DEL;
				}
			}
			if (StringUtils.trimToEmpty(method).equals("doApprove")) {
				vasChekListRef.setRecordType(PennantConstants.RCD_ADD);
			}

			vasChekListRef.setRecordStatus("");
			vasChekListRef.setUserDetails(vasRecording.getUserDetails());
			vasChekListRef.setLastMntOn(vasRecording.getLastMntOn());
			vasChekListRef.setLastMntBy(vasRecording.getLastMntBy());
			vasChekListRef.setWorkflowId(vasRecording.getWorkflowId());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], vasChekListRef.getBefImage(),
					vasChekListRef));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Extended Field Details
	 * 
	 * @param vasRecording
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setExtendedFieldsAuditData(VASRecording vasRecording, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		ExtendedFieldRender extendedFieldRender = vasRecording.getExtendedFieldRender();
		if (extendedFieldRender == null) {
			return null;
		}
		if (StringUtils.isEmpty(StringUtils.trimToEmpty(extendedFieldRender.getRecordType()))) {
			return null;
		}

		boolean isRcdType = false;
		if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			if (extendedFieldRender.isWorkflow()) {
				isRcdType = true;
			}
		} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}

		if ("saveOrUpdate".equals(method) && (isRcdType)) {
			extendedFieldRender.setNewRecord(true);
		}

		if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				auditTranType = PennantConstants.TRAN_ADD;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				auditTranType = PennantConstants.TRAN_DEL;
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
			}
		}

		// Audit Details Preparation
		HashMap<String, Object> auditMapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
		auditMapValues.put("Reference", extendedFieldRender.getReference());
		auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
		auditMapValues.put("Version", extendedFieldRender.getVersion());
		auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
		auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
		auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
		auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
		auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
		auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
		auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
		auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
		auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
		extendedFieldRender.setAuditMapValues(auditMapValues);

		String[] fields = PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender);
		AuditDetail auditDetail = new AuditDetail(auditTranType,  1, fields[0], fields[1], extendedFieldRender.getBefImage(), extendedFieldRender);
		auditDetail.setExtended(true);
		auditDetails.add(auditDetail);

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method for Deleting all records related to VASRecording child tables in _Temp/Main tables depend on method type.
	 * 
	 * @param vasRecording
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	public List<AuditDetail> listDeletion(VASRecording vasRecording, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// Checklist Details delete
		auditList.addAll(deleteCheckLists(vasRecording, tableType, auditTranType));

		// Document Details. 
		List<AuditDetail> documentDetails = vasRecording.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			DocumentDetails document = new DocumentDetails();
			DocumentDetails documentDetail = null;
			List<DocumentDetails> docList = new ArrayList<DocumentDetails>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				documentDetail = (DocumentDetails) documentDetails.get(i).getModelData();
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				docList.add(documentDetail);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetail.getBefImage(),
						documentDetail));
			}
			getDocumentDetailsDAO().deleteList(docList, tableType);
		}

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = vasRecording.getAuditDetailMap().get("ExtendedFieldDetail");

		ExtendedFieldRender fieldRender = null;
		if (extendedDetails != null && extendedDetails.size() > 0) {

			ExtendedFieldHeader extendedFieldHeader = vasRecording.getVasConfiguration().getExtendedFieldHeader();
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			for (int i = 0; i < extendedDetails.size(); i++) {
				fieldRender = (ExtendedFieldRender) extendedDetails.get(i).getModelData();
				fieldRender.setTableName(tableName.toString());

				if (StringUtils.isEmpty(tableType)) {
					fieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else {
					fieldRender.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
				// Audit Details Preparation
				HashMap<String, Object> auditMapValues = (HashMap<String, Object>) fieldRender.getMapValues();
				auditMapValues.put("Reference", vasRecording.getVasReference());
				auditMapValues.put("SeqNo", fieldRender.getSeqNo());
				auditMapValues.put("Version", fieldRender.getVersion());
				auditMapValues.put("LastMntOn", fieldRender.getLastMntOn());
				auditMapValues.put("LastMntBy", fieldRender.getLastMntBy());
				auditMapValues.put("RecordStatus", fieldRender.getRecordStatus());
				auditMapValues.put("RoleCode", fieldRender.getRoleCode());
				auditMapValues.put("NextRoleCode", fieldRender.getNextRoleCode());
				auditMapValues.put("TaskId", fieldRender.getTaskId());
				auditMapValues.put("NextTaskId", fieldRender.getNextTaskId());
				auditMapValues.put("RecordType", fieldRender.getRecordType());
				auditMapValues.put("WorkflowId", fieldRender.getWorkflowId());

				// Audit Saving Purpose
				fieldRender.setAuditMapValues(auditMapValues);
				fieldRender.setBefImage(fieldRender);

				String[] fields = PennantJavaUtil.getExtendedFieldDetails(fieldRender);
				AuditDetail auditDetail = new AuditDetail(auditTranType, i + 1, fields[0], fields[1], fieldRender.getBefImage(), fieldRender);
				auditDetail.setExtended(true);
				auditList.add(auditDetail);
			}
			getExtendedFieldRenderDAO().deleteList(vasRecording.getVasReference(), tableName.toString(), tableType);
		}
		logger.debug("Leaving");
		return auditList;
	}

	/**
	 * Method for Deleting  CheckLists related records in _Temp/Main tables depend on method type
	 * 
	 * @param vasRecording
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	private List<AuditDetail> deleteCheckLists(VASRecording vasRecording, String tableType, String auditTranType) {
		logger.debug("Entering ");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		List<FinanceCheckListReference> checkList = vasRecording.getVasCheckLists();
		FinanceCheckListReference checklist = new FinanceCheckListReference();
		FinanceCheckListReference vasCheckListRef = null;
		if (checkList != null && !checkList.isEmpty()) {
			for (int i = 0; i < checkList.size(); i++) {
				vasCheckListRef = checkList.get(i);
				String[] fields = PennantJavaUtil.getFieldDetails(checklist, checklist.getExcludeFields());
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						vasCheckListRef.getBefImage(), vasCheckListRef));
			}
			getFinanceCheckListReferenceDAO().delete(vasCheckListRef.getFinReference(), tableType);
		}
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Method for getting the CustomerDocuments
	 * 
	 * @param documentDetails
	 * @return vasRecording
	 */
	private CustomerDocument getCustomerDocument(DocumentDetails documentDetails, VASRecording vasRecording) {
		logger.debug("Entering");

		CustomerDocument customerDocument = getCustomerDocumentDAO().getCustomerDocumentById(
				vasRecording.getVasCustomer().getCustomerId(), documentDetails.getDocCategory(), "");

		if (customerDocument == null) {
			customerDocument = new CustomerDocument();
			customerDocument.setCustDocIsAcrive(documentDetails.isCustDocIsAcrive());
			customerDocument.setCustDocIsVerified(documentDetails.isCustDocIsVerified());
			customerDocument.setCustDocRcvdOn(documentDetails.getCustDocRcvdOn());
			customerDocument.setCustDocVerifiedBy(documentDetails.getCustDocVerifiedBy());
			customerDocument.setNewRecord(true);
			customerDocument.setCustID(vasRecording.getVasCustomer().getCustomerId());
		}

		customerDocument.setCustID(vasRecording.getVasCustomer().getCustomerId());
		customerDocument.setLovDescCustCIF(vasRecording.getVasCustomer().getCustCIF());
		customerDocument.setLovDescCustShrtName(vasRecording.getVasCustomer().getCustShrtName());

		customerDocument.setCustDocTitle(documentDetails.getCustDocTitle());
		customerDocument.setCustDocIssuedCountry(documentDetails.getCustDocIssuedCountry());
		customerDocument.setLovDescCustDocIssuedCountry(documentDetails.getLovDescCustDocIssuedCountry());
		customerDocument.setCustDocIssuedOn(documentDetails.getCustDocIssuedOn());
		customerDocument.setCustDocExpDate(documentDetails.getCustDocExpDate());
		customerDocument.setCustDocSysName(documentDetails.getCustDocSysName());
		customerDocument.setCustDocImage(documentDetails.getDocImage());
		customerDocument.setCustDocType(documentDetails.getDoctype());
		customerDocument.setCustDocCategory(documentDetails.getDocCategory());
		customerDocument.setCustDocName(documentDetails.getDocName());
		customerDocument.setLovDescCustDocCategory(documentDetails.getLovDescDocCategoryName());

		if (customerDocument.getDocRefId() <= 0) {
			DocumentManager documentManager = new DocumentManager();
			documentManager.setDocImage(documentDetails.getDocImage());
			customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
		}

		customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerDocument.setRecordType("");
		customerDocument.setUserDetails(documentDetails.getUserDetails());
		customerDocument.setVersion(documentDetails.getVersion());
		customerDocument.setLastMntBy(documentDetails.getLastMntBy());
		customerDocument.setLastMntOn(documentDetails.getLastMntOn());

		logger.debug("Leaving");
		return customerDocument;
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException
	 */
	public void executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws InterfaceException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		VASRecording vASRecording = new VASRecording("");
		BeanUtils.copyProperties((VASRecording) auditHeader.getAuditDetail().getModelData(), vASRecording);

		if(vASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){

			AEEvent aeEvent = new AEEvent();
			aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
			aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_VAS_FEE);
			aeEvent.setFinReference(vASRecording.getVasReference());
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			if(amountCodes == null){
				amountCodes = new AEAmountCodes();
			}

			// Based on VAS Created Against, details will be captured  
			if(StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vASRecording.getPostingAgainst())){
				FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(vASRecording.getPrimaryLinkRef());
				amountCodes.setFinType(financeMain.getFinType());
				aeEvent.setBranch(financeMain.getFinBranch());
				aeEvent.setCcy(financeMain.getFinCcy());
				aeEvent.setCustID(financeMain.getCustID());
			} else if(StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vASRecording.getPostingAgainst())){
				Customer customer = getCustomerDAO().getCustomerByCIF(vASRecording.getPrimaryLinkRef(),"");
				aeEvent.setBranch(customer.getCustDftBranch());
				aeEvent.setCcy(customer.getCustBaseCcy());
				aeEvent.setCustID(customer.getCustID());
			} else if(StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vASRecording.getPostingAgainst())){
				CollateralSetup collateralSetup = collateralSetupDAO.getCollateralSetupByRef(
						vASRecording.getPrimaryLinkRef(),"");
				//TODO:Need to modify for getting branch as per performance
				Customer customer = getCustomerDAO().getCustomerByID(collateralSetup.getDepositorId(),"");
				aeEvent.setCcy(collateralSetup.getCollateralCcy());
				aeEvent.setCustID(collateralSetup.getDepositorId());
				aeEvent.setBranch(customer.getCustDftBranch());
			}

			aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
			vASRecording.getDeclaredFieldValues(aeEvent.getDataMap());
			aeEvent.getAcSetIDList().add(vASRecording.getVasConfiguration().getFeeAccounting());
			postingsPreparationUtil.postAccounting(aeEvent);

		}else if(StringUtils.equals("C", vASRecording.getVasStatus())){
			postingsPreparationUtil.postReveralsByFinreference(vASRecording.getVasReference());
		}

		logger.debug("Leaving");
	}
	@Override
	public List<VASRecording> getVasRecordingsByPrimaryLinkRef(String primaryLinkRef) {
		return vASRecordingDAO.getVASRecordingsByLinkRef(primaryLinkRef,"");
	}
	//validations For API Specific
	@Override
	public AuditDetail doValidations(VASRecording vasRecording) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		ErrorDetails errorDetail = new ErrorDetails();
		if (vasRecording != null) {
			if (StringUtils.isBlank(vasRecording.getProductCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "product";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (StringUtils.isBlank(vasRecording.getPostingAgainst())) {
				String[] valueParm = new String[1];
				valueParm[0] = "postingAgainst";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			} else {
				if(StringUtils.equals("Loan", vasRecording.getPostingAgainst())){
					vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
				}
			}

			if (!(StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vasRecording.getPostingAgainst())
					|| StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vasRecording.getPostingAgainst()) || StringUtils
					.equals(VASConsatnts.VASAGAINST_FINANCE, vasRecording.getPostingAgainst()))) {
				String[] valueParm = new String[2];
				valueParm[0] = "postingAgainst";
				valueParm[1] = vasRecording.getPostingAgainst();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90224", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;

			}
			if (StringUtils.isBlank(vasRecording.getPrimaryLinkRef())) {
				String[] valueParm = new String[1];
				valueParm[0] = "primaryLinkRef";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (vasRecording.getFee() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fee";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			
			VASConfiguration vASConfiguration = vASConfigurationService.getVASConfigurationByCode(vasRecording
					.getProductCode());
			if (vASConfiguration == null || !vASConfiguration.isActive()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Product";
				valueParm[1] = vasRecording.getProductCode();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90224", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
					vasRecording.getProductCode(), FinanceConstants.FINSER_EVENT_ORG, VASConsatnts.MODULE_NAME);
			if(financeWorkFlow == null){
				String[] valueParm = new String[2];
				valueParm[0] = vasRecording.getProductCode();
				valueParm[1] = "workflow";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90339", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (!StringUtils.equals(vASConfiguration.getRecAgainst(), vasRecording.getPostingAgainst())) {
				String[] valueParm = new String[2];
				valueParm[0] = "PostingAgainst";
				valueParm[1] = vasRecording.getPostingAgainst();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90224", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (StringUtils.equalsIgnoreCase(VASConsatnts.VASAGAINST_CUSTOMER, vasRecording.getPostingAgainst())) {
				Customer customer = customerDetailsService.getCheckCustomerByCIF(vasRecording.getPrimaryLinkRef());
				if (customer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getPrimaryLinkRef();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90101", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else if (StringUtils.equalsIgnoreCase(VASConsatnts.VASAGAINST_FINANCE, vasRecording.getPostingAgainst())) {
				int count = financeMainDAO.getFinanceCountById(vasRecording.getPrimaryLinkRef(), "", false);
				if (count <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getPrimaryLinkRef();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90201", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else if (StringUtils.equalsIgnoreCase(VASConsatnts.VASAGAINST_COLLATERAL,
					vasRecording.getPostingAgainst())) {
				int count = collateralSetupDAO.getCountByCollateralRef(vasRecording.getPrimaryLinkRef());
				if (count <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getPrimaryLinkRef();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90906", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (!vASConfiguration.isAllowFeeToModify()) {
				if (vasRecording.getFee().compareTo(vASConfiguration.getVasFee()) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Fee:" + vasRecording.getFee();
					valueParm[1] = "VasConfig Fee:" + vASConfiguration.getVasFee();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30570", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else if (vasRecording.getFee().compareTo(BigDecimal.ZERO) < 1) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fee";
				valueParm[1] = "Zero";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("91125", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}

			// validate FeePaymentMode
			if (StringUtils.isNotBlank(vasRecording.getFeePaymentMode())) {
				List<ValueLabel> paymentModes = PennantStaticListUtil.getFeeTypes();
				boolean paymentSts = false;
				for (ValueLabel value : paymentModes) {
					if (StringUtils.equals(value.getValue(), vasRecording.getFeePaymentMode())) {
						paymentSts = true;
						break;
					}
				}
				if (!paymentSts) {
					String[] valueParm = new String[3];
					valueParm[0] = "paymentMode";
					valueParm[1] = "paymentModes";
					valueParm[2] = FinanceConstants.RECFEETYPE_CASH + "," + FinanceConstants.RECFEETYPE_CHEQUE;
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90264", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "feePaymentMode";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (vasRecording.getValueDate() == null) {
				vasRecording.setValueDate(DateUtility.getAppDate());
			} else {
				if (vasRecording.getValueDate().before(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE))
						|| vasRecording.getValueDate().after(DateUtility.getAppDate())) {
					String[] valueParm = new String[3];
					valueParm[0] = "Value Date";
					valueParm[1] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE));
					valueParm[2] = DateUtility.formatToLongDate(DateUtility.getAppDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm)));
					return auditDetail;
				}
			}
			if (vASConfiguration.isFeeAccrued()) {
				if (vasRecording.getAccrualTillDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "accrualTillDate";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					if(vasRecording.getAccrualTillDate().before(DateUtility.getAppDate())
							|| vasRecording.getAccrualTillDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))){
						String[] valueParm = new String[3];
						valueParm[0] = "AccrualTillDate";
						valueParm[1] = DateUtility.formatToLongDate(DateUtility.getAppDate());
						valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm)));
						return auditDetail;
					}

				}
			} else {
				if(vasRecording.getAccrualTillDate() != null){
					String[] valueParm = new String[2];
					valueParm[0] = "accrualTillDate";
					valueParm[1] = "FeeAccrued";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90298", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				vasRecording.setAccrualTillDate(DateUtility.getAppDate());
			}
			if (vASConfiguration.isRecurringType()) {
				if (vasRecording.getRecurringDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "recurringDate";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					if(vasRecording.getRecurringDate().before(DateUtility.getAppDate())
							|| vasRecording.getRecurringDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))){
						String[] valueParm = new String[3];
						valueParm[0] = "RecurringDate";
						valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
						valueParm[1] = DateUtility.formatToLongDate(DateUtility.getAppDate());
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm)));
						return auditDetail;
					}
				}
			} else {
				if(vasRecording.getRecurringDate() != null){
					String[] valueParm = new String[2];
					valueParm[0] = "RecurringDate";
					valueParm[1] = "RecurringType is Active";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90298", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				vasRecording.setRecurringDate(DateUtility.getAppDate());
				vasRecording.setRenewalFee(BigDecimal.ZERO);
			}
			if (StringUtils.isNotBlank(vasRecording.getDsaId())){
				RelationshipOfficer relationshipOfficer = relationshipOfficerDAO
						.getRelationshipOfficerById(vasRecording.getDsaId(), "");
				if (relationshipOfficer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getDsaId();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90501", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (StringUtils.isNotBlank(vasRecording.getDmaId())){
				RelationshipOfficer dmaCode = relationshipOfficerDAO
						.getRelationshipOfficerById(vasRecording.getDmaId(), "");
				if (dmaCode == null ) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getDmaId();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90501", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (StringUtils.isNotBlank(vasRecording.getFulfilOfficerId())) {
				RelationshipOfficer dmaCode = relationshipOfficerDAO
						.getRelationshipOfficerById(vasRecording.getFulfilOfficerId(), "");
				if (dmaCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getFulfilOfficerId();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90501", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (StringUtils.isNotBlank(vasRecording.getReferralId())){
				RelationshipOfficer referralId = relationshipOfficerDAO
						.getRelationshipOfficerById(vasRecording.getReferralId(), "");
				if (referralId == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getReferralId();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90501", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (vasRecording.getDocuments() != null && !vasRecording.getDocuments().isEmpty()) {
				for (DocumentDetails detail : vasRecording.getDocuments()) {
					//validate Dates
					if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
						if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
							String[] valueParm = new String[2];
							valueParm[0] = DateUtility.formatDate(detail.getCustDocIssuedOn(),
									PennantConstants.XMLDateFormat);
							valueParm[1] = DateUtility.formatDate(detail.getCustDocExpDate(),
									PennantConstants.XMLDateFormat);
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90205", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					// validate custDocIssuedCountry
					if (StringUtils.isBlank(detail.getCustDocIssuedCountry())) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustDocIssuedCountry";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
					int count = getCustomerDocumentDAO()
							.getCustCountryCount(detail.getCustDocIssuedCountry());
					if (count <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "custDocIssuedCountry";
						valueParm[1] = detail.getCustDocIssuedCountry();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}

					if (StringUtils.equals(detail.getDocCategory(), "03")) {
						Pattern pattern = Pattern.compile("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
						if(detail.getCustDocTitle() !=null){
							detail.setCustDocTitle(detail.getCustDocTitle().toUpperCase());;
							Matcher matcher = pattern.matcher(detail.getCustDocTitle());
							if(matcher.find() == false ){
								String[] valueParm = new String[0];
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90251", "", valueParm), "EN");
								auditDetail.setErrorDetail(errorDetail);
								return auditDetail;
							}
						}
					}
					if (StringUtils.isBlank(detail.getDocUri())) {
						if (detail.getDocImage() == null || detail.getDocImage().length <= 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "docContent";
							valueParm[1] = "docRefId";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90123", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					DocumentType docType = documentTypeDAO.getDocumentTypeById(detail.getDocCategory(),"");
					if (docType == null || docType.isDocIsCustDoc()) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDocCategory();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90401", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
					if(!(StringUtils.equals(detail.getDoctype(),PennantConstants.DOC_TYPE_PDF) 
							|| StringUtils.equals(detail.getDoctype(),PennantConstants.DOC_TYPE_DOC)
							|| StringUtils.equals(detail.getDoctype(),PennantConstants.DOC_TYPE_DOCX)
							|| StringUtils.equals(detail.getDoctype(),PennantConstants.DOC_TYPE_IMAGE))){
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDoctype();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90122", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
					String docFormate = detail.getDocName()
							.substring(detail.getDocName().lastIndexOf(".") + 1);
					if (StringUtils.equals(detail.getDocName(), docFormate)) {
						String[] valueParm = new String[1];
						valueParm[0] = "docName: " + docFormate;
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90291", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
					boolean isImage = false;
					if (StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_IMAGE)) {
						if (StringUtils.equals(docFormate, "jpg") || StringUtils.equals(docFormate, "jpeg")
								|| StringUtils.equals(docFormate, "png")) {
							isImage = true;
						}
					}
					if (!isImage) {
						if (!StringUtils.equals(detail.getDoctype(), docFormate)) {
							String[] valueParm = new String[2];
							valueParm[0] = "document type: " + detail.getDocName();
							valueParm[1] = detail.getDoctype();
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90289", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					}
				}

			}
			int extendedDetailsCount = 0;
			if (vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails() != null) {
				for (ExtendedFieldDetail detail : vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails()) {
					if (detail.isFieldMandatory()) {
						extendedDetailsCount++;
					}
				}
			}
			if (extendedDetailsCount > 0 && vasRecording.getExtendedDetails() == null
					|| vasRecording.getExtendedDetails().isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "ExtendedDetails";
				 auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm)));
				 return auditDetail;
			}
			if (vasRecording.getExtendedDetails() != null && !vasRecording.getExtendedDetails().isEmpty()) {
				for (ExtendedField details : vasRecording.getExtendedDetails()) {
					if (vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails().size() != details
							.getExtendedFieldDataList().size()) {
						if (extendedDetailsCount != details.getExtendedFieldDataList().size()) {
							String[] valueParm = new String[1];
							valueParm[0] = "vas setup";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90265", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					int exdMandConfigCount = 0;
					for (ExtendedFieldData extendedFieldData : details.getExtendedFieldDataList()) {
						if (StringUtils.isBlank(extendedFieldData.getFieldName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldName";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
						if (StringUtils.isBlank(extendedFieldData.getFieldValue())) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldValue";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
						boolean isFeild = false;
						if (vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails() != null) {
							for (ExtendedFieldDetail detail : vASConfiguration.getExtendedFieldHeader()
									.getExtendedFieldDetails()) {
								if (StringUtils.equals(detail.getFieldName(), extendedFieldData.getFieldName())) {
									if(detail.isFieldMandatory()) {
										exdMandConfigCount++;
									}
									List<ErrorDetails> errList = getExtendedFieldDetailsValidation().validateExtendedFieldData(detail, 
											extendedFieldData);
									auditDetail.getErrorDetails().addAll(errList);
									isFeild = true;
								}
							}
							if (!isFeild) {
								String[] valueParm = new String[1];
								valueParm[0] = "vas setup";
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90265", "", valueParm), "EN");
								auditDetail.setErrorDetail(errorDetail);
								return auditDetail;
							}
						}
					}
					if (extendedDetailsCount != exdMandConfigCount) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90297", "", null)));
						return auditDetail;
					}
				}

			}
			Map<String, Object>	mapValues = new HashMap<String, Object>();
			if(vasRecording.getExtendedDetails() != null){
				for (ExtendedField details : vasRecording.getExtendedDetails()) {
					for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
					}
				}
			}

			// do script pre validation and post validation
			ScriptErrors errors = null;
			if (vASConfiguration.isPostValidationReq()) {
				errors = scriptValidationService.getPostValidationErrors(vASConfiguration.getPostValidation(), mapValues);
			}
			if (errors != null) {
				List<ScriptError> errorsList = errors.getAll();
				for (ScriptError error : errorsList) {
					auditDetail.setErrorDetail(new ErrorDetails("","90909","",error.getValue(),null,null));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public CheckListDetailService getCheckListDetailService() {
		return checkListDetailService;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public VASConfigurationService getvASConfigurationService() {
		return vASConfigurationService;
	}

	public void setvASConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vASConfigurationService = vASConfigurationService;
	}

	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public FinanceCheckListReferenceDAO getFinanceCheckListReferenceDAO() {
		return financeCheckListReferenceDAO;
	}

	public void setFinanceCheckListReferenceDAO(FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}

	public DocumentDetailValidation getVASDocumentValidation() {
		if (vasDocumentValidation == null) {
			this.vasDocumentValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO,
					customerDocumentDAO);
		}
		return vasDocumentValidation;
	}

	public ExtendedFieldDetailsValidation getExtendedFieldDetailsValidation() {
		if (extendedFieldDetailsValidation == null) {
			this.extendedFieldDetailsValidation = new ExtendedFieldDetailsValidation(extendedFieldRenderDAO);
		}
		return extendedFieldDetailsValidation;
	}

	public VASRecordingDAO getvASRecordingDAO() {
		return vASRecordingDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public DocumentDetailValidation getVasDocumentValidation() {
		return vasDocumentValidation;
	}

	public void setVasDocumentValidation(DocumentDetailValidation vasDocumentValidation) {
		this.vasDocumentValidation = vasDocumentValidation;
	}

	public void setExtendedFieldDetailsValidation(ExtendedFieldDetailsValidation extendedFieldDetailsValidation) {
		this.extendedFieldDetailsValidation = extendedFieldDetailsValidation;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}
	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}
	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public ScriptValidationService getScriptValidationService() {
		return scriptValidationService;
	}
	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public void setRelationshipOfficerDAO(RelationshipOfficerDAO relationshipOfficerDAO) {
		this.relationshipOfficerDAO = relationshipOfficerDAO;
	}

	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}
	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

}