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
 * FileName    		:  CollateralSetupServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-12-2016    														*
 *                                                                  						*
 * Modified Date    :  13-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.collateral.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CoOwnerDetailDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.collateral.CollateralThirdPartyDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedField;
import com.pennant.backend.model.staticparms.ExtendedFieldData;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>CollateralSetup</b>.<br>
 * 
 */
public class CollateralSetupServiceImpl extends GenericService<CollateralSetup> implements CollateralSetupService {
	private static final Logger				logger	= Logger.getLogger(CollateralSetupServiceImpl.class);

	private AuditHeaderDAO					auditHeaderDAO;
	private CollateralSetupDAO				collateralSetupDAO;
	private FinanceReferenceDetailDAO		financeReferenceDetailDAO;

	private CollateralThirdPartyDAO			collateralThirdPartyDAO;
	private CoOwnerDetailDAO				coOwnerDetailDAO;
	private DocumentDetailsDAO				documentDetailsDAO;
	private DocumentManagerDAO				documentManagerDAO;
	private CustomerDocumentDAO				customerDocumentDAO;
	private FinFlagDetailsDAO				finFlagDetailsDAO;
	private FinanceCheckListReferenceDAO	financeCheckListReferenceDAO;
	private CityDAO							cityDAO;
	private CustomerDetailsService			customerDetailsService;
	private CheckListDetailService			checkListDetailService;
	private CollateralStructureService		collateralStructureService;
	private CurrencyDAO						currencyDAO;
	private DocumentTypeService				documentTypeService;
	private CollateralAssignmentDAO			collateralAssignmentDAO;

	// Validation Service Classes
	private CollateralThirdPartyValidation	collateralThirdPartyValidation;
	private FlagDetailValidation			collateralFlagValidation;
	private DocumentDetailValidation		collateralDocumentValidation;
	private CoOwnerDetailsValidation		coOwnerDetailsValidation;

	private ExtendedFieldDetailsService		extendedFieldDetailsService;
	private ExtendedFieldRenderDAO			extendedFieldRenderDAO;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public CollateralThirdPartyDAO getCollateralThirdPartyDAO() {
		return collateralThirdPartyDAO;
	}

	public void setCollateralThirdPartyDAO(CollateralThirdPartyDAO collateralThirdPartyDAO) {
		this.collateralThirdPartyDAO = collateralThirdPartyDAO;
	}

	public CoOwnerDetailDAO getCoOwnerDetailDAO() {
		return coOwnerDetailDAO;
	}

	public void setCoOwnerDetailDAO(CoOwnerDetailDAO coOwnerDetailDAO) {
		this.coOwnerDetailDAO = coOwnerDetailDAO;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public FinanceCheckListReferenceDAO getFinanceCheckListReferenceDAO() {
		return financeCheckListReferenceDAO;
	}

	public void setFinanceCheckListReferenceDAO(FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}

	public CityDAO getCityDAO() {
		return cityDAO;
	}

	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public CheckListDetailService getCheckListDetailService() {
		return checkListDetailService;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public CollateralStructureService getCollateralStructureService() {
		return collateralStructureService;
	}

	public void setCollateralStructureService(CollateralStructureService collateralStructureService) {
		this.collateralStructureService = collateralStructureService;
	}

	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public CollateralThirdPartyValidation getCollateralThirdPartyValidation() {
		if (collateralThirdPartyValidation == null) {
			this.collateralThirdPartyValidation = new CollateralThirdPartyValidation(collateralThirdPartyDAO);
		}
		return collateralThirdPartyValidation;
	}

	public DocumentDetailValidation getCollateralDocumentValidation() {
		if (collateralDocumentValidation == null) {
			this.collateralDocumentValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO,
					customerDocumentDAO);
		}
		return collateralDocumentValidation;
	}

	public CoOwnerDetailsValidation getCoOwnerDetailsValidation() {
		if (coOwnerDetailsValidation == null) {
			this.coOwnerDetailsValidation = new CoOwnerDetailsValidation(coOwnerDetailDAO);
		}
		return coOwnerDetailsValidation;
	}

	public FlagDetailValidation getCollateralFlagValidation() {
		if (collateralFlagValidation == null) {
			this.collateralFlagValidation = new FlagDetailValidation(finFlagDetailsDAO);
		}
		return this.collateralFlagValidation;
	}

	/**
	 * @param extendedFieldDetailsService
	 *            the extendedFieldDetailsService to set
	 */
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	/**
	 * @param extendedFieldRenderDAO
	 *            the extendedFieldRenderDAO to set
	 */
	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CollateralDetail/CollateralDetail_Temp by using CollateralDetailDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using CollateralDetailDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtCollateralDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tableType = "";
		CollateralSetup collateralSetup = (CollateralSetup) auditHeader.getAuditDetail().getModelData();

		if (collateralSetup.isWorkflow()) {
			tableType = "_Temp";
		}
		if (collateralSetup.isNew()) {
			getCollateralSetupDAO().save(collateralSetup, tableType);
		} else {
			getCollateralSetupDAO().update(collateralSetup, tableType);
		}

		// CoOwner Details Processing
		if (collateralSetup.getCoOwnerDetailList() != null && !collateralSetup.getCoOwnerDetailList().isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("CollateralCoOwnerDetails");
			details = processingCoOwnersList(details, tableType);
			auditDetails.addAll(details);
		}

		// Collateral Third Party Details Processing
		if (collateralSetup.getCollateralThirdPartyList() != null
				&& !collateralSetup.getCollateralThirdPartyList().isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("CollateralThirdParty");
			details = processingThirdPartyDetailsList(details, tableType);
			auditDetails.addAll(details);
		}

		// Flag Details
		if (collateralSetup.getFinFlagsDetailsList() != null && !collateralSetup.getFinFlagsDetailsList().isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("FinFlagsDetail");
			details = processingFinFlagDetailList(details, collateralSetup, tableType);
			auditDetails.addAll(details);
		}

		//Collateral documents
		if (collateralSetup.getDocuments() != null && !collateralSetup.getDocuments().isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, collateralSetup, tableType);
			auditDetails.addAll(details);
		}

		// Collateral collateralCheckLists
		if (collateralSetup.getCollateralCheckLists() != null && !collateralSetup.getCollateralCheckLists().isEmpty()) {
			auditDetails.addAll(processingCheckListDetailsList(collateralSetup, tableType));
		}

		// Collateral Extended field Details
		if (collateralSetup.getExtendedFieldRenderList() != null
				&& !collateralSetup.getExtendedFieldRenderList().isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					collateralSetup.getCollateralStructure().getExtendedFieldHeader(), tableType);
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
	 * CollateralDetail by using CollateralDetailDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCollateralDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CollateralSetup collateralSetup = (CollateralSetup) auditHeader.getAuditDetail().getModelData();

		auditDetails.addAll(listDeletion(collateralSetup, "", auditHeader.getAuditTranType()));
		getCollateralSetupDAO().delete(collateralSetup, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralSetup(), collateralSetup.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				collateralSetup.getBefImage(), collateralSetup));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCollateralDetailById fetch the details by using CollateralDetailDAO's getCollateralDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CollateralDetail
	 */
	@Override
	public CollateralSetup getCollateralSetupByRef(String collateralRef, String nextRoleCode, boolean isEnquiry) {
		logger.debug("Entering");

		CollateralSetup collateralSetup = getCollateralSetupDAO().getCollateralSetupByRef(collateralRef, "_View");
		if (collateralSetup != null) {

			// Collateral Type/Structure Details
			collateralSetup.setCollateralStructure(getCollateralStructureService()
					.getApprovedCollateralStructureByType(collateralSetup.getCollateralType()));

			// Co-Owner Details
			collateralSetup.setCoOwnerDetailList(getCoOwnerDetailDAO().getCoOwnerDetailByRef(collateralRef, "_View"));

			// Third Party Details
			collateralSetup.setCollateralThirdPartyList(
					getCollateralThirdPartyDAO().getCollThirdPartyDetails(collateralRef, "_View"));

			//Flag Details
			collateralSetup.setFinFlagsDetailsList(getFinFlagDetailsDAO().getFinFlagsByFinRef(collateralRef,
					CollateralConstants.MODULE_NAME, "_View"));

			// Assignment Details
			collateralSetup.setAssignmentDetails(getCollateralAssignmentDAO()
					.getCollateralAssignmentByColRef(collateralRef, collateralSetup.getCollateralType()));

			// Extended Field Details
			ExtendedFieldHeader extendedFieldHeader = collateralSetup.getCollateralStructure().getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			List<Map<String, Object>> renderMapList = extendedFieldRenderDAO.getExtendedFieldMap(collateralRef,
					tableName.toString(), "_View");

			List<ExtendedFieldRender> renderList = new ArrayList<>();
			for (int i = 0; i < renderMapList.size(); i++) {

				Map<String, Object> extFieldMap = renderMapList.get(i);
				ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();

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
				extendedFieldRender
						.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null")
								? "" : String.valueOf(extFieldMap.get("RecordStatus")));
				extFieldMap.remove("RecordStatus");
				extendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null")
						? "" : String.valueOf(extFieldMap.get("RoleCode")));
				extFieldMap.remove("RoleCode");
				extendedFieldRender
						.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null")
								? "" : String.valueOf(extFieldMap.get("NextRoleCode")));
				extFieldMap.remove("NextRoleCode");
				extendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
						: String.valueOf(extFieldMap.get("TaskId")));
				extFieldMap.remove("TaskId");
				extendedFieldRender
						.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null") ? ""
								: String.valueOf(extFieldMap.get("NextTaskId")));
				extFieldMap.remove("NextTaskId");
				extendedFieldRender
						.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null") ? ""
								: String.valueOf(extFieldMap.get("RecordType")));
				extFieldMap.remove("RecordType");
				extendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
				extFieldMap.remove("WorkflowId");

				extendedFieldRender.setMapValues(extFieldMap);
				renderList.add(extendedFieldRender);
			}
			collateralSetup.setExtendedFieldRenderList(renderList);

			// Not Required Other Process details for the Enquiry
			if (!isEnquiry) {

				// Customer Details
				collateralSetup.setCustomerDetails(getCustomerDetailsService()
						.getCustomerDetailsById(collateralSetup.getDepositorId(), true, "_View"));

				// Document Details
				List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(collateralRef,
						CollateralConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG, "_View");
				if (collateralSetup.getDocuments() != null && !collateralSetup.getDocuments().isEmpty()) {
					collateralSetup.getDocuments().addAll(documentList);
				} else {
					collateralSetup.setDocuments(documentList);
				}

				// Agreement Details & Check List Details
				if (StringUtils.isNotEmpty(collateralSetup.getRecordType())
						&& !StringUtils.equals(collateralSetup.getRecordType(), PennantConstants.RECORD_TYPE_UPD)
						&& !StringUtils.equals(collateralSetup.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
					collateralSetup = getProcessEditorDetails(collateralSetup, nextRoleCode,
							FinanceConstants.FINSER_EVENT_ORG);
				}
			}
		}

		logger.debug("Leaving");
		return collateralSetup;
	}

	/**
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public CollateralSetup getProcessEditorDetails(CollateralSetup collateralSetup, String nextRoleCode,
			String procEdtEvent) {
		logger.debug("Entering");

		boolean isCustExist = true;
		String collateralType = collateralSetup.getCollateralType();
		String ctgType = StringUtils.trimToEmpty(collateralSetup.getCustomerDetails().getCustomer().getCustCtgCode());
		if (StringUtils.isEmpty(ctgType)) {
			isCustExist = false;
		}

		List<FinanceReferenceDetail> aggrementList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> checkListdetails = new ArrayList<FinanceReferenceDetail>(1);

		// Fetch Total Process editor Details 
		List<FinanceReferenceDetail> finRefDetails = getFinanceReferenceDetailDAO().getFinanceProcessEditorDetails(
				collateralType, StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
				"_CLTVIEW");

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
		collateralSetup.setAggrements(aggrementList);

		if (isCustExist) {
			//Check list Details
			getCheckListDetailService().fetchCollateralCheckLists(collateralSetup, checkListdetails);
		}
		logger.debug("Leaving");
		return collateralSetup;
	}

	/**
	 * Method for Calculating Total Assigned percentage value
	 */
	@Override
	public BigDecimal getAssignedPerc(String collateralRef, String reference) {
		return getCollateralAssignmentDAO().getAssignedPerc(collateralRef, reference, "_View");
	}

	/**
	 * getApprovedCollateralDetailById fetch the details by using CollateralDetailDAO's getCollateralDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the CollateralDetail.
	 * 
	 * @param id
	 *            (String)
	 * @return CollateralDetail
	 */
	public CollateralSetup getApprovedCollateralSetupById(String collateralRef) {
		logger.debug("Entering");
		CollateralSetup collateralSetup = getCollateralSetupDAO().getCollateralSetupByRef(collateralRef, "_AView");
		if (collateralSetup != null) {

			// set co-Owner details
			collateralSetup.setCoOwnerDetailList(getCoOwnerDetailDAO().getCoOwnerDetailByRef(collateralRef, "_AView"));

			// set Collateral thirdparty details
			collateralSetup.setCollateralThirdPartyList(
					getCollateralThirdPartyDAO().getCollThirdPartyDetails(collateralRef, "_AView"));

			// set document details
			collateralSetup.setDocuments(getDocumentDetailsDAO().getDocumentDetailsByRef(collateralRef,
					CollateralConstants.MODULE_NAME, "", ""));

		}
		logger.debug("Leaving");
		return collateralSetup;
	}

	private CollateralStructure getCollateralStructure(String collateralType) {
		return collateralStructureService.getApprovedCollateralStructureByType(collateralType);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCollateralSetupDAO().delete with
	 * parameters collateralSetup,"" b) NEW Add new record in to main table by using getCollateralSetupDAO().save with
	 * parameters collateralSetup,"" c) EDIT Update record in the main table by using getCollateralSetupDAO().update
	 * with parameters collateralSetup,"" 3) Delete the record from the workFlow table by using
	 * getCollateralSetupDAO().delete with parameters collateralSetup,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtCollateralDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtCollateralDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		aAuditHeader = businessValidation(aAuditHeader, "doApprove");

		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		CollateralSetup collateralSetup = (CollateralSetup) auditHeader.getAuditDetail().getModelData();

		if (collateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(collateralSetup, "", tranType));
			getCollateralSetupDAO().delete(collateralSetup, "");
		} else {
			collateralSetup.setRoleCode("");
			collateralSetup.setNextRoleCode("");
			collateralSetup.setTaskId("");
			collateralSetup.setNextTaskId("");
			collateralSetup.setWorkflowId(0);

			if (collateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				collateralSetup.setRecordType("");
				getCollateralSetupDAO().save(collateralSetup, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				collateralSetup.setRecordType("");
				getCollateralSetupDAO().update(collateralSetup, "");
			}

			// CoOwner Details
			if (collateralSetup.getCoOwnerDetailList() != null && !collateralSetup.getCoOwnerDetailList().isEmpty()) {
				List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("CollateralCoOwnerDetails");
				details = processingCoOwnersList(details, "");
				auditDetails.addAll(details);
			}

			// Third Party Details
			if (collateralSetup.getCollateralThirdPartyList() != null
					&& !collateralSetup.getCollateralThirdPartyList().isEmpty()) {
				List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("CollateralThirdParty");
				details = processingThirdPartyDetailsList(details, "");
				auditDetails.addAll(details);
			}

			// Fin Flag Details
			if (collateralSetup.getFinFlagsDetailsList() != null
					&& !collateralSetup.getFinFlagsDetailsList().isEmpty()) {
				List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("FinFlagsDetail");
				details = processingFinFlagDetailList(details, collateralSetup, "");
				auditDetails.addAll(details);
			}

			// Checklist details
			if (collateralSetup.getCollateralCheckLists() != null
					&& !collateralSetup.getCollateralCheckLists().isEmpty()) {
				auditDetails.addAll(processingCheckListDetailsList(collateralSetup, ""));
			}

			//Collateral Document Details
			List<DocumentDetails> documentsList = collateralSetup.getDocuments();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, collateralSetup, "");
				auditDetails.addAll(details);
			}

			// Collateral Extended field Details
			if (collateralSetup.getExtendedFieldRenderList() != null
					&& !collateralSetup.getExtendedFieldRenderList().isEmpty()) {
				List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("ExtendedFieldDetails");
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						collateralSetup.getCollateralStructure().getExtendedFieldHeader(), "");
				auditDetails.addAll(details);
			}

		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralSetup(), collateralSetup.getExcludeFields());
		if (!StringUtils.equals(collateralSetup.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditDetailList.addAll(listDeletion(collateralSetup, "_Temp", auditHeader.getAuditTranType()));
			getCollateralSetupDAO().delete(collateralSetup, "_Temp");

			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					collateralSetup.getBefImage(), collateralSetup));
			auditHeader.setAuditDetails(auditDetailList);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				collateralSetup.getBefImage(), collateralSetup));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCollateralSetupDAO().delete with parameters collateralSetup,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtCollateralDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CollateralSetup collateralSetup = (CollateralSetup) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralSetup(), collateralSetup.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				collateralSetup.getBefImage(), collateralSetup));

		auditDetails.addAll(listDeletion(collateralSetup, "_Temp", auditHeader.getAuditTranType()));
		getCollateralSetupDAO().delete(collateralSetup, "_Temp");

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		CollateralSetup collateralSetup = (CollateralSetup) auditDetail.getModelData();
		String usrLanguage = collateralSetup.getUserDetails().getUsrLanguage();

		//CoOwnerDetails Validation
		List<CoOwnerDetail> coOwnerDetailList = collateralSetup.getCoOwnerDetailList();
		if (coOwnerDetailList != null && !coOwnerDetailList.isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("CollateralCoOwnerDetails");
			details = getCoOwnerDetailsValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//CollateralThirdpaty details Validation
		List<CollateralThirdParty> thirdPatyDetailList = collateralSetup.getCollateralThirdPartyList();
		if (thirdPatyDetailList != null && !thirdPatyDetailList.isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("CollateralThirdParty");
			details = getCollateralThirdPartyValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Collateral Flag details Validation
		List<FinFlagsDetail> finFlagsDetailList = collateralSetup.getFinFlagsDetailsList();
		if (finFlagsDetailList != null && !finFlagsDetailList.isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("FinFlagsDetail");
			details = getCollateralFlagValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Collateral Check List Details
		List<FinanceCheckListReference> collateralCheckList = collateralSetup.getCollateralCheckLists();
		if (collateralCheckList != null && !collateralCheckList.isEmpty()) {
			List<AuditDetail> auditDetailList = collateralSetup.getAuditDetailMap().get("CheckListDetails");
			auditDetailList = getCheckListDetailService().validate(auditDetailList, method, usrLanguage);
			auditDetails.addAll(auditDetailList);
		}

		//Collateral Document details Validation
		List<DocumentDetails> documentDetailsList = collateralSetup.getDocuments();
		if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("DocumentDetails");
			details = getCollateralDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Extended field details Validation
		if (collateralSetup.getExtendedFieldRenderList() != null
				&& !collateralSetup.getExtendedFieldRenderList().isEmpty()) {
			List<AuditDetail> details = collateralSetup.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extendedFieldHeader = collateralSetup.getCollateralStructure().getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extendedFieldHeader, details, method,
					usrLanguage);
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
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getCollateralSetupDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		CollateralSetup collateralSetup = (CollateralSetup) auditDetail.getModelData();

		CollateralSetup tempCollateralSetup = null;
		if (collateralSetup.isWorkflow()) {
			tempCollateralSetup = getCollateralSetupDAO().getCollateralSetupByRef(collateralSetup.getId(), "_Temp");
		}
		CollateralSetup befCollateralSetup = getCollateralSetupDAO().getCollateralSetupByRef(collateralSetup.getId(),
				"");
		CollateralSetup oldCollateralSetup = collateralSetup.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = collateralSetup.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_CollateralRef") + ":" + valueParm[0];

		if (collateralSetup.isNew()) { // for New record or new record into work flow

			if (!collateralSetup.isWorkflow()) {// With out Work flow only new records  
				if (befCollateralSetup != null) { // Record Already Exists in the table then error  
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (collateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCollateralSetup != null || tempCollateralSetup != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befCollateralSetup == null || tempCollateralSetup != null) {
						auditDetail.setErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!collateralSetup.isWorkflow()) { // With out Work flow for update and delete

				if (befCollateralSetup == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldCollateralSetup != null
							&& !oldCollateralSetup.getLastMntOn().equals(befCollateralSetup.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {

				if (tempCollateralSetup == null) { // if records not exists in the Work flow table 
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempCollateralSetup != null && oldCollateralSetup != null
						&& !oldCollateralSetup.getLastMntOn().equals(tempCollateralSetup.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		// Multi Loan Assignment Flag details Checking on Removal
		if (befCollateralSetup != null && tempCollateralSetup == null) {
			if (befCollateralSetup.isMultiLoanAssignment() && !collateralSetup.isMultiLoanAssignment()) {

				// Checking Existing Loan Count assigned to this Collateral Reference
				int assignedCount = getCollateralAssignmentDAO()
						.getAssignedCollateralCount(collateralSetup.getCollateralRef(), "_View");
				if (assignedCount > 1) {
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "CL001", errParm, valueParm));
				}
			}
		}

		// Bank valuation checking against the total utilized amount
		if (!StringUtils.trimToEmpty(method).equals("doReject")) {
			if (collateralSetup.getBankValuation() != null && befCollateralSetup != null) {

				BigDecimal assignedPerc = getCollateralAssignmentDAO()
						.getAssignedPerc(collateralSetup.getCollateralRef(), null, "_View");
				if (assignedPerc.compareTo(BigDecimal.ZERO) > 0
						&& befCollateralSetup.getBankValuation().compareTo(collateralSetup.getBankValuation()) > 0) {
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "CL002", errParm, valueParm));
				}
			}
		}

		// Checking wile record deletion if collateral used in loan or not.
		// If it used we cannot allow to delete that record.
		if (PennantConstants.RECORD_TYPE_DEL.equals(collateralSetup.getRecordType())) {
			int assignedCount = getCollateralAssignmentDAO()
					.getAssignedCollateralCount(collateralSetup.getCollateralRef(), "_View");
			if (assignedCount > 0) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove") || !collateralSetup.isWorkflow()) {
			auditDetail.setBefImage(befCollateralSetup);
		}
		logger.debug("Leaving");
		return auditDetail;
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

		CollateralSetup collateralSetup = (CollateralSetup) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (collateralSetup.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		////Collateral CoOwner details
		if (collateralSetup.getCoOwnerDetailList() != null && collateralSetup.getCoOwnerDetailList().size() > 0) {
			auditDetailMap.put("CollateralCoOwnerDetails",
					setCoOwnersAuditData(collateralSetup, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CollateralCoOwnerDetails"));
		}

		//CollateralThirdParty details
		if (collateralSetup.getCollateralThirdPartyList() != null
				&& collateralSetup.getCollateralThirdPartyList().size() > 0) {
			auditDetailMap.put("CollateralThirdParty", setThirdPartyAuditData(collateralSetup, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CollateralThirdParty"));
		}

		// Collateral Flag details
		if (collateralSetup.getFinFlagsDetailsList() != null && collateralSetup.getFinFlagsDetailsList().size() > 0) {
			auditDetailMap.put("FinFlagsDetail", setFinFlagAuditData(collateralSetup, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinFlagsDetail"));
		}

		//Collateral Document Details
		if (collateralSetup.getDocuments() != null && collateralSetup.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(collateralSetup, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		//Collateral Checklist Details
		List<FinanceCheckListReference> collateralCheckLists = collateralSetup.getCollateralCheckLists();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (collateralCheckLists != null && !collateralCheckLists.isEmpty()) {
				auditDetailMap.put("CheckListDetails", setCheckListsAuditData(collateralSetup, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CheckListDetails"));
			}
		} else {
			String tableType = "_Temp";
			if (collateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			collateralCheckLists = getCheckListDetailService().getCheckListByFinRef(collateralSetup.getCollateralRef(),
					tableType);
			collateralSetup.setCollateralCheckLists(collateralCheckLists);

			if (collateralCheckLists != null && !collateralCheckLists.isEmpty()) {
				auditDetailMap.put("CheckListDetails", setCheckListsAuditData(collateralSetup, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CheckListDetails"));
			}
		}

		// Collateral Extended Field Details
		if (collateralSetup.getExtendedFieldRenderList() != null
				&& collateralSetup.getExtendedFieldRenderList().size() > 0) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(collateralSetup.getExtendedFieldRenderList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		collateralSetup.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(collateralSetup);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of collateral CoOwners Audit Details with detailed fields
	 * 
	 * @param collateralSetup
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCoOwnersAuditData(CollateralSetup collateralSetup, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		CoOwnerDetail ownerDetail = new CoOwnerDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(ownerDetail, ownerDetail.getExcludeFields());

		for (int i = 0; i < collateralSetup.getCoOwnerDetailList().size(); i++) {

			CoOwnerDetail coOwnerDetail = collateralSetup.getCoOwnerDetailList().get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(coOwnerDetail.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (collateralSetup.isWorkflow()) {
					isRcdType = true;
				}
			} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				coOwnerDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], coOwnerDetail.getBefImage(),
					coOwnerDetail));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List Collateral Third Party of Audit Details with detailed fields
	 * 
	 * @param collateralSetup
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setThirdPartyAuditData(CollateralSetup collateralSetup, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		CollateralThirdParty thirdParty = new CollateralThirdParty();
		String[] fields = PennantJavaUtil.getFieldDetails(thirdParty, thirdParty.getExcludeFields());

		for (int i = 0; i < collateralSetup.getCollateralThirdPartyList().size(); i++) {

			CollateralThirdParty collateralThirdParty = collateralSetup.getCollateralThirdPartyList().get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(collateralThirdParty.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (collateralSetup.isWorkflow()) {
					isRcdType = true;
				}
			} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				collateralThirdParty.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					collateralThirdParty.getBefImage(), collateralThirdParty));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List Collateral Flag of Audit Details with detailed fields
	 * 
	 * @param collateralSetup
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinFlagAuditData(CollateralSetup collateralSetup, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinFlagsDetail flagsDetail = new FinFlagsDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(flagsDetail, flagsDetail.getExcludeFields());

		for (int i = 0; i < collateralSetup.getFinFlagsDetailsList().size(); i++) {

			FinFlagsDetail finFlagsDetail = collateralSetup.getFinFlagsDetailsList().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(finFlagsDetail.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (collateralSetup.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFlagsDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finFlagsDetail.getBefImage(),
					finFlagsDetail));

		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(CollateralSetup collateralSetup, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < collateralSetup.getDocuments().size(); i++) {
			DocumentDetails documentDetails = collateralSetup.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(collateralSetup.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (collateralSetup.isWorkflow()) {
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

			documentDetails.setRecordStatus(collateralSetup.getRecordStatus());
			documentDetails.setUserDetails(collateralSetup.getUserDetails());
			documentDetails.setLastMntOn(collateralSetup.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for checkList Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> setCheckListsAuditData(CollateralSetup collateralSetup, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceCheckListReference object = new FinanceCheckListReference();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < collateralSetup.getCollateralCheckLists().size(); i++) {
			FinanceCheckListReference collChekListRef = collateralSetup.getCollateralCheckLists().get(i);

			collChekListRef.setFinReference(collateralSetup.getCollateralRef());

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(collChekListRef.getRecordType()))) {
				continue;
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (collChekListRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
					auditTranType = PennantConstants.TRAN_ADD;
					collChekListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (collChekListRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_UPD;
				} else {
					auditTranType = PennantConstants.RCD_DEL;
				}
			}
			if (StringUtils.trimToEmpty(method).equals("doApprove")) {
				collChekListRef.setRecordType(PennantConstants.RCD_ADD);
			}

			collChekListRef.setRecordStatus("");
			collChekListRef.setUserDetails(collateralSetup.getUserDetails());
			collChekListRef.setLastMntOn(collateralSetup.getLastMntOn());
			collChekListRef.setLastMntBy(collateralSetup.getLastMntBy());
			collChekListRef.setWorkflowId(collateralSetup.getWorkflowId());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], collChekListRef.getBefImage(),
					collChekListRef));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for CoOwner Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingCoOwnersList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			CoOwnerDetail coOwnerDetail = (CoOwnerDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				coOwnerDetail.setRoleCode("");
				coOwnerDetail.setNextRoleCode("");
				coOwnerDetail.setTaskId("");
				coOwnerDetail.setNextTaskId("");
			}

			coOwnerDetail.setWorkflowId(0);

			if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (coOwnerDetail.isNewRecord()) {
				saveRecord = true;
				if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (coOwnerDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (coOwnerDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = coOwnerDetail.getRecordType();
				recordStatus = coOwnerDetail.getRecordStatus();
				coOwnerDetail.setRecordType("");
				coOwnerDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getCoOwnerDetailDAO().save(coOwnerDetail, type);
			}

			if (updateRecord) {
				getCoOwnerDetailDAO().update(coOwnerDetail, type);
			}

			if (deleteRecord) {
				getCoOwnerDetailDAO().delete(coOwnerDetail, type);
			}

			if (approveRec) {
				coOwnerDetail.setRecordType(rcdType);
				coOwnerDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(coOwnerDetail);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Third Party Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingThirdPartyDetailsList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			CollateralThirdParty collateralThirdParty = (CollateralThirdParty) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				collateralThirdParty.setRoleCode("");
				collateralThirdParty.setNextRoleCode("");
				collateralThirdParty.setTaskId("");
				collateralThirdParty.setNextTaskId("");
			}

			collateralThirdParty.setWorkflowId(0);

			if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (collateralThirdParty.isNewRecord()) {
				saveRecord = true;
				if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (collateralThirdParty.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (collateralThirdParty.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = collateralThirdParty.getRecordType();
				recordStatus = collateralThirdParty.getRecordStatus();
				collateralThirdParty.setRecordType("");
				collateralThirdParty.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getCollateralThirdPartyDAO().save(collateralThirdParty, type);
			}

			if (updateRecord) {
				getCollateralThirdPartyDAO().update(collateralThirdParty, type);
			}

			if (deleteRecord) {
				getCollateralThirdPartyDAO().delete(collateralThirdParty, type);
			}

			if (approveRec) {
				collateralThirdParty.setRecordType(rcdType);
				collateralThirdParty.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(collateralThirdParty);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param collateralSetup
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingFinFlagDetailList(List<AuditDetail> auditDetails,
			CollateralSetup collateralSetup, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finFlagsDetail.setRoleCode("");
				finFlagsDetail.setNextRoleCode("");
				finFlagsDetail.setTaskId("");
				finFlagsDetail.setNextTaskId("");
			}

			finFlagsDetail.setWorkflowId(0);

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finFlagsDetail.isNewRecord()) {
				saveRecord = true;
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finFlagsDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finFlagsDetail.getRecordType();
				recordStatus = finFlagsDetail.getRecordStatus();
				finFlagsDetail.setRecordType("");
				finFlagsDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinFlagDetailsDAO().save(finFlagsDetail, type);
			}

			if (updateRecord) {
				getFinFlagDetailsDAO().update(finFlagsDetail, type);
			}

			if (deleteRecord) {
				getFinFlagDetailsDAO().delete(finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),
						finFlagsDetail.getModuleName(), type);
			}

			if (approveRec) {
				finFlagsDetail.setRecordType(rcdType);
				finFlagsDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finFlagsDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param collateralSetup
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
			CollateralSetup collateralSetup, String type) {
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
				if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setLastMntBy(collateralSetup.getLastMntBy());
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
						documentDetails.setReferenceId(collateralSetup.getCollateralRef());
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
					getDocumentDetailsDAO().save(documentDetails, type);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
					if (documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					getDocumentDetailsDAO().update(documentDetails, type);
				}

				if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
					if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						getDocumentDetailsDAO().delete(documentDetails, type);
					}
				}

				if (approveRec) {
					documentDetails.setFinEvent("");
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			} else {
				CustomerDocument custdoc = getCustomerDocument(documentDetails, collateralSetup);
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
	 * @param collateralSetup
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingCheckListDetailsList(CollateralSetup collateralSetup, String tableType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = collateralSetup.getAuditDetailMap().get("CheckListDetails");

		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference collateralChecklistRef = (FinanceCheckListReference) auditDetails.get(i)
					.getModelData();

			collateralChecklistRef.setWorkflowId(0);

			if (StringUtils.isEmpty(tableType)) {
				collateralChecklistRef.setVersion(collateralChecklistRef.getVersion() + 1);
				collateralChecklistRef.setRoleCode("");
				collateralChecklistRef.setNextRoleCode("");
				collateralChecklistRef.setTaskId("");
				collateralChecklistRef.setNextTaskId("");
			}

			if (collateralChecklistRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
				collateralChecklistRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				getFinanceCheckListReferenceDAO().save(collateralChecklistRef, tableType);
			} else if (collateralChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				getFinanceCheckListReferenceDAO().delete(collateralChecklistRef, tableType);
			} else if (collateralChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				getFinanceCheckListReferenceDAO().update(collateralChecklistRef, tableType);
			}
			auditDetails.get(i).setModelData(collateralChecklistRef);
		}

		logger.debug("Leaving ");
		return auditDetails;
	}

	// Method for Deleting all records related to collateral setup in _Temp/Main tables depend on method type

	public List<AuditDetail> listDeletion(CollateralSetup collateralSetup, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// CoOwner Details.
		List<AuditDetail> coOwnerDetails = collateralSetup.getAuditDetailMap().get("CollateralCoOwnerDetails");
		if (coOwnerDetails != null && coOwnerDetails.size() > 0) {
			CoOwnerDetail ownerDetail = new CoOwnerDetail();
			CoOwnerDetail coOwnerDetail = null;
			String[] fields = PennantJavaUtil.getFieldDetails(ownerDetail, ownerDetail.getExcludeFields());
			for (int i = 0; i < coOwnerDetails.size(); i++) {
				coOwnerDetail = (CoOwnerDetail) coOwnerDetails.get(i).getModelData();
				coOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], coOwnerDetail.getBefImage(),
						coOwnerDetail));
			}
			getCoOwnerDetailDAO().deleteList(coOwnerDetail, tableType);
		}

		// Thirdparty Details
		List<AuditDetail> thirdPartyDetails = collateralSetup.getAuditDetailMap().get("CollateralThirdParty");
		CollateralThirdParty thirdParty = new CollateralThirdParty();
		CollateralThirdParty collateralThirdParty = null;
		if (thirdPartyDetails != null && thirdPartyDetails.size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(thirdParty, thirdParty.getExcludeFields());
			for (int i = 0; i < thirdPartyDetails.size(); i++) {
				collateralThirdParty = (CollateralThirdParty) thirdPartyDetails.get(i).getModelData();
				collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						collateralThirdParty.getBefImage(), collateralThirdParty));
			}
			getCollateralThirdPartyDAO().deleteList(collateralThirdParty, tableType);
		}

		// Flag Details.
		List<AuditDetail> flagDetails = collateralSetup.getAuditDetailMap().get("FinFlagsDetail");
		FinFlagsDetail flagsDetail = new FinFlagsDetail();
		FinFlagsDetail finFlagsDetail = null;
		if (flagDetails != null && flagDetails.size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(flagsDetail, flagsDetail.getExcludeFields());
			for (int i = 0; i < flagDetails.size(); i++) {
				finFlagsDetail = (FinFlagsDetail) flagDetails.get(i).getModelData();
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finFlagsDetail.getBefImage(),
						finFlagsDetail));
			}
			getFinFlagDetailsDAO().deleteList(finFlagsDetail.getReference(), CollateralConstants.MODULE_NAME,
					tableType);
		}

		// Checklist Details delete
		auditList.addAll(deleteCheckLists(collateralSetup, tableType, auditTranType));

		// Document Details. 
		List<AuditDetail> documentDetails = collateralSetup.getAuditDetailMap().get("DocumentDetails");
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
		List<AuditDetail> extendedDetails = collateralSetup.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			ExtendedFieldHeader extendedFieldHeader = collateralSetup.getCollateralStructure().getExtendedFieldHeader();
			auditList.addAll(extendedFieldDetailsService.delete(extendedFieldHeader, collateralSetup.getCollateralRef(),
					tableType, auditTranType, extendedDetails));
		}

		logger.debug("Leaving");
		return auditList;
	}

	// Method for Deleting  CheckLists related records in _Temp/Main tables depend on method type
	private List<AuditDetail> deleteCheckLists(CollateralSetup collateralSetup, String tableType,
			String auditTranType) {
		logger.debug("Entering ");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		List<FinanceCheckListReference> checkList = collateralSetup.getCollateralCheckLists();
		FinanceCheckListReference checklist = new FinanceCheckListReference();
		FinanceCheckListReference finCheckListRef = null;
		if (checkList != null && !checkList.isEmpty()) {
			for (int i = 0; i < checkList.size(); i++) {
				finCheckListRef = checkList.get(i);
				String[] fields = PennantJavaUtil.getFieldDetails(checklist, checklist.getExcludeFields());
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finCheckListRef.getBefImage(),
						finCheckListRef));
			}
			getFinanceCheckListReferenceDAO().delete(finCheckListRef.getFinReference(), tableType);
		}
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Get latest version of collateral setup
	 * 
	 * @param collateralRef
	 * @param Integer
	 */
	@Override
	public int getVersion(String collateralRef) {
		return getCollateralSetupDAO().getVersion(collateralRef, "");
	}

	/**
	 * Fetch list of customer collateral by custId
	 * 
	 * @param depositorId
	 * @return List<CollateralSetup>
	 */
	@Override
	public List<CollateralSetup> getApprovedCollateralByCustId(long depositorId) {
		logger.debug("Entering");

		List<CollateralSetup> collaterals = getCollateralSetupDAO().getApprovedCollateralByCustId(depositorId,
				"_AView");
		for (CollateralSetup setup : collaterals) {
			setup.setCoOwnerDetailList(getCoOwnerDetailDAO().getCoOwnerDetailByRef(setup.getCollateralRef(), "_AView"));
			setup.setCollateralThirdPartyList(
					getCollateralThirdPartyDAO().getCollThirdPartyDetails(setup.getCollateralRef(), "_AView"));

			// set document details
			setup.setDocuments(getDocumentDetailsDAO().getDocumentDetailsByRef(setup.getCollateralRef(),
					CollateralConstants.MODULE_NAME, "", ""));

			CollateralStructure collateralStructure = collateralStructureService
					.getApprovedCollateralStructureByType(setup.getCollateralType());
			setup.setCollateralStructure(collateralStructure);

			// set Extended details
			String reference = setup.getCollateralRef();
			ExtendedFieldHeader extendedFieldHeader = setup.getCollateralStructure().getExtendedFieldHeader();
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			List<Map<String, Object>> extendedMapValues = extendedFieldRenderDAO.getExtendedFieldMap(reference,
					tableName.toString(), "");
			if (extendedMapValues != null) {
				List<ExtendedField> extendedDetails = new ArrayList<ExtendedField>();
				for (Map<String, Object> mapValues : extendedMapValues) {
					List<ExtendedFieldData> extendedFieldDataList = new ArrayList<ExtendedFieldData>();
					for (Entry<String, Object> entry : mapValues.entrySet()) {
						ExtendedFieldData exdFieldData = new ExtendedFieldData();
						if (StringUtils.isNotBlank(String.valueOf(entry.getValue()))
								|| !StringUtils.equals(String.valueOf(entry.getValue()), "null")) {
							exdFieldData.setFieldName(entry.getKey());
							exdFieldData.setFieldValue(String.valueOf(entry.getValue()));
							extendedFieldDataList.add(exdFieldData);
						}
					}
					ExtendedField extendedField = new ExtendedField();
					extendedField.setExtendedFieldDataList(extendedFieldDataList);
					extendedDetails.add(extendedField);
				}
				setup.setExtendedDetails(extendedDetails);
			}
		}

		logger.debug("Leaving");
		return collaterals;
	}

	/**
	 * Method for validate collateral setup details
	 * 
	 * @param collateralSetup
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(CollateralSetup collateralSetup, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();

		if (StringUtils.equals(method, "create") || StringUtils.equals(method, "update")
				|| StringUtils.equals(method, "delete")) {
			// customer cif
			if (StringUtils.isNotBlank(collateralSetup.getDepositorCif())) {
				Customer customer = customerDetailsService.getCustomerByCIF(collateralSetup.getDepositorCif());
				if (customer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = collateralSetup.getDepositorCif();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90101", "", valueParm)));
				} else {
					collateralSetup.setDepositorId(customer.getCustID());
				}
			}
		}

		if (StringUtils.equals(method, "delete")) {
			// validate collateral reference
			if (StringUtils.isNotBlank(collateralSetup.getCollateralRef())) {
				int recordCount = collateralSetupDAO.getCollateralCountByref(collateralSetup.getCollateralRef(), "");
				if (recordCount <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = collateralSetup.getCollateralRef();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90906", "", valueParm)));
				}
			}

			// validate collateral reference and customer
			if (StringUtils.isNotBlank(collateralSetup.getDepositorCif())
					&& StringUtils.isNotBlank(collateralSetup.getCollateralRef())) {

				long depositorId = collateralSetup.getDepositorId();
				String collateralRef = collateralSetup.getCollateralRef();

				CollateralSetup setup = collateralSetupDAO.getCollateralSetup(collateralRef, depositorId, "");
				if (setup == null) {
					String[] valueParm = new String[2];
					valueParm[0] = collateralSetup.getDepositorCif();
					valueParm[1] = collateralRef;
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90907", "", valueParm)));
				}
			}
		}

		if (StringUtils.equals(method, "update")) {
			// validate collateral reference
			if (StringUtils.isNotBlank(collateralSetup.getCollateralRef())) {
				int recordCount = collateralSetupDAO.getCollateralCountByref(collateralSetup.getCollateralRef(), "");
				if (recordCount <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = collateralSetup.getCollateralRef();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90906", "", valueParm)));
				}
			}
		}
		if (!StringUtils.equals(method, "delete")) {
			// fetch collateral structure
			CollateralStructure collateralStructure = null;

			// collateral type
			String collateralType = collateralSetup.getCollateralType();
			if (StringUtils.isNotBlank(collateralType)) {
				collateralStructure = getCollateralStructure(collateralType);
			} else if (StringUtils.isNotBlank(collateralSetup.getCollateralRef())) {
				CollateralSetup setup = getCollateralSetupDAO()
						.getCollateralSetupByRef(collateralSetup.getCollateralRef(), "");
				if (setup != null) {
					collateralStructure = getCollateralStructure(setup.getCollateralType());
				}
			}

			if (collateralStructure == null) {
				String[] valueParm = new String[1];
				valueParm[0] = collateralType;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90901", "", valueParm)));
				return auditDetail;
			}

			// collateralLoc
			if (collateralStructure.isCollateralLocReq()) {
				if (StringUtils.isBlank(collateralSetup.getCollateralLoc())) {
					String[] valueParm = new String[1];
					valueParm[0] = "collateralLoc";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90902", "", valueParm)));
				} else {
					Pattern pattern = Pattern
							.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_NAME));
					Matcher matcher = pattern.matcher(collateralSetup.getCollateralLoc());
					if (matcher.matches() == false) {
						String[] valueParm = new String[1];
						valueParm[0] = "collateralLoc";
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90348", "", valueParm)));
					}
				}
			}

			// valuator
			if (collateralStructure.isCollateralValuatorReq()) {
				if (StringUtils.isBlank(collateralSetup.getValuator())) {
					String[] valueParm = new String[1];
					valueParm[0] = "valuator";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90902", "", valueParm)));
				} else {
					Pattern pattern = Pattern
							.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_NAME));
					Matcher matcher = pattern.matcher(collateralSetup.getValuator());
					if (matcher.matches() == false) {
						String[] valueParm = new String[1];
						valueParm[0] = "valuator";
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90348", "", valueParm)));
					}
				}
			}

			// validate currency
			if (StringUtils.isNotBlank(collateralSetup.getCollateralCcy())) {
				Currency currency = currencyDAO.getCurrencyByCode(collateralSetup.getCollateralCcy());
				if (currency == null) {
					String[] valueParm = new String[1];
					valueParm[0] = collateralSetup.getCollateralCcy();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90501", "", valueParm)));
				}
			}

			// expiry date
			Date currAppDate = DateUtility.getAppDate();
			Date expiryDate = collateralSetup.getExpiryDate();
			if (expiryDate != null) {
				if (expiryDate.compareTo(currAppDate) <= 0
						|| expiryDate.compareTo(SysParamUtil.getValueAsDate("APP_DFT_END_DATE")) >= 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "ExpiryDate";
					valueParm[1] = DateUtility.formatToLongDate(currAppDate);
					valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm)));
				}
			}
			// expiry date
			Date nextRvwDate = collateralSetup.getNextReviewDate();
			if (nextRvwDate != null) {
				if (nextRvwDate.compareTo(currAppDate) <= 0
						|| nextRvwDate.compareTo(SysParamUtil.getValueAsDate("APP_DFT_END_DATE")) >= 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "nextReviewDate";
					valueParm[1] = DateUtility.formatToLongDate(currAppDate);
					valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm)));
				}
				if (StringUtils.isNotBlank(collateralSetup.getReviewFrequency())) {
					if (!FrequencyUtil.isFrqDate(collateralSetup.getReviewFrequency(), nextRvwDate)) {
						String[] valueParm = new String[1];
						valueParm[0] = DateUtility.formatToLongDate(nextRvwDate);
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91123", "", valueParm)));
					}
				}
			}
			if (collateralSetup.getReviewFrequency() != null) {
				ErrorDetails errorDetail = FrequencyUtil.validateFrequency(collateralSetup.getReviewFrequency());
				if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
					String[] valueParm = new String[1];
					valueParm[0] = collateralSetup.getReviewFrequency();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90207", "", valueParm)));
				}
			}
			// validate third party customers
			List<CollateralThirdParty> thirdPartyCollateral = collateralSetup.getCollateralThirdPartyList();
			if (thirdPartyCollateral != null) {
				for (CollateralThirdParty thirdPartyCust : thirdPartyCollateral) {
					int rcdCount = customerDetailsService.getCustomerCountByCIF(thirdPartyCust.getCustCIF());
					if (rcdCount <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = thirdPartyCust.getCustCIF();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90904", "", valueParm)));
					}
				}
			}

			// validate collateral coOwner details
			List<CoOwnerDetail> coOwnerDetails = collateralSetup.getCoOwnerDetailList();
			for (CoOwnerDetail coOwnerDetail : coOwnerDetails) {

				// validate co-owner cif
				if (coOwnerDetail.isBankCustomer()) {
					int rcdCount = customerDetailsService.getCustomerCountByCIF(coOwnerDetail.getCoOwnerCIF());
					if (rcdCount <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = coOwnerDetail.getCoOwnerCIF();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90905", "", valueParm)));
					}
				} else {
					String mobileNumber = coOwnerDetail.getMobileNo();
					if (StringUtils.isBlank(mobileNumber)) {
						String[] valueParm = new String[1];
						valueParm[0] = "phoneNumber";
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm)));
					} else {
						if (!(mobileNumber.matches("\\d{10}"))) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90278", "", null)));
							return auditDetail;
						}
					}

				}

				// validate address
				City city = cityDAO.getCityById(coOwnerDetail.getAddrCountry(), coOwnerDetail.getAddrProvince(),
						coOwnerDetail.getAddrCity(), "");
				if (city == null) {
					String[] valueParm = new String[2];
					valueParm[0] = coOwnerDetail.getAddrProvince();
					valueParm[1] = coOwnerDetail.getAddrCountry();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm)));
				}
			}

			// validate Extended details
			int extendedDetailsCount = 0;
			List<ExtendedFieldDetail> exdFldConfig = collateralStructure.getExtendedFieldHeader()
					.getExtendedFieldDetails();
			for (ExtendedFieldDetail detail : exdFldConfig) {
				if (detail.isFieldMandatory()) {
					extendedDetailsCount++;
				}
			}
			if (extendedDetailsCount > 0 && (collateralSetup.getExtendedDetails() == null
					|| collateralSetup.getExtendedDetails().isEmpty())) {
				String[] valueParm = new String[1];
				valueParm[0] = "ExtendedDetails";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm)));
				return auditDetail;
			}
			if (collateralSetup.getExtendedDetails() != null && !collateralSetup.getExtendedDetails().isEmpty()) {
				for (ExtendedField details : collateralSetup.getExtendedDetails()) {
					int exdMandConfigCount = 0;
					for (ExtendedFieldData extendedFieldData : details.getExtendedFieldDataList()) {
						if (StringUtils.isBlank(extendedFieldData.getFieldName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldName";
							auditDetail
									.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm)));
							return auditDetail;
						}
						if (StringUtils.isBlank(extendedFieldData.getFieldValue())) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldValue";
							auditDetail
									.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm)));
							return auditDetail;
						}
						boolean isFeild = false;
						for (ExtendedFieldDetail detail : exdFldConfig) {
							if (StringUtils.equals(detail.getFieldName(), extendedFieldData.getFieldName())) {
								if (detail.isFieldMandatory()) {
									exdMandConfigCount++;
								}
								List<ErrorDetails> errList = extendedFieldDetailsService
										.validateExtendedFieldData(detail, extendedFieldData);
								auditDetail.getErrorDetails().addAll(errList);
								isFeild = true;
							}
						}
						if (!isFeild) {
							String[] valueParm = new String[1];
							valueParm[0] = "collateral structure";
							auditDetail
									.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90265", "", valueParm)));
							return auditDetail;
						}
					}
					if (extendedDetailsCount != exdMandConfigCount) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90297", "", null)));
						return auditDetail;
					}
				}
			}

			Map<String, Object> mapValues = new HashMap<String, Object>();
			if (collateralSetup.getExtendedDetails() != null) {
				for (ExtendedField details : collateralSetup.getExtendedDetails()) {
					for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {
						for (ExtendedFieldDetail detail : exdFldConfig) {
							if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BASERATE, detail.getFieldType())
									&& StringUtils.equals(extFieldData.getFieldName(), detail.getFieldName())) {
								extFieldData.setFieldName(extFieldData.getFieldName().concat("_BR"));
							}
							if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PHONE, detail.getFieldType())
									&& StringUtils.equals(extFieldData.getFieldName(), detail.getFieldName())) {
								extFieldData.setFieldName(extFieldData.getFieldName().concat("_SC"));
							}
							mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						}
					}
				}
			}
			// do script pre validation and post validation
			ScriptErrors errors = null;
			if (collateralStructure.isPostValidationReq()) {
				errors = extendedFieldDetailsService.getPostValidationErrors(collateralStructure.getPostValidation(),
						mapValues);
			}
			if (errors != null) {
				List<ScriptError> errorsList = errors.getAll();
				for (ScriptError error : errorsList) {
					auditDetail.setErrorDetail(new ErrorDetails("", "90909", "", error.getValue(), null, null));
				}
			}

			// validate document details
			List<DocumentDetails> documentDetails = collateralSetup.getDocuments();
			if (documentDetails != null) {
				for (DocumentDetails detail : documentDetails) {
					DocumentType docType = documentTypeService.getDocumentTypeById(detail.getDocCategory());

					// validate doc category
					if (docType == null) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDocCategory();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90401", "", valueParm)));
					}
				}
			}
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Method for getting the CustomerDocuments
	 * 
	 * @param documentDetails
	 * @return collateralSetup
	 */
	private CustomerDocument getCustomerDocument(DocumentDetails documentDetails, CollateralSetup collateralSetup) {
		logger.debug("Entering");

		CustomerDocument customerDocument = getCustomerDocumentDAO()
				.getCustomerDocumentById(collateralSetup.getDepositorId(), documentDetails.getDocCategory(), "");

		if (customerDocument == null) {
			customerDocument = new CustomerDocument();
			customerDocument.setCustDocIsAcrive(documentDetails.isCustDocIsAcrive());
			customerDocument.setCustDocIsVerified(documentDetails.isCustDocIsVerified());
			customerDocument.setCustDocRcvdOn(documentDetails.getCustDocRcvdOn());
			customerDocument.setCustDocVerifiedBy(documentDetails.getCustDocVerifiedBy());
			customerDocument.setNewRecord(true);
		}
		customerDocument.setCustID(collateralSetup.getDepositorId());
		customerDocument.setLovDescCustCIF(collateralSetup.getDepositorCif());
		customerDocument.setLovDescCustShrtName(collateralSetup.getDepositorName());

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
	 * Method for get collateral count by reference.
	 * 
	 * @param collateralRef
	 * @return Integer
	 */
	@Override
	public int getCountByCollateralRef(String collateralRef) {
		return getCollateralSetupDAO().getCountByCollateralRef(collateralRef);
	}

	/**
	 * Method for Fetching Movement Details against Collateral Reference
	 */
	@Override
	public List<CollateralMovement> getCollateralMovements(String collateralRef) {
		return getCollateralAssignmentDAO().getCollateralMovements(collateralRef);
	}

	/**
	 * Fetch list of customer collateral by custId
	 * 
	 * @param custId
	 * @return List<CollateralSetup>
	 */
	@Override
	public List<CollateralSetup> getCollateralSetupByCustId(long custId) {
		logger.debug("Entering");

		List<CollateralSetup> collaterals = getCollateralSetupDAO().getApprovedCollateralByCustId(custId, "_AView");
		for (CollateralSetup setup : collaterals) {
			// set Extended details
			CollateralStructure collateralStructure = collateralStructureService
					.getApprovedCollateralStructureByType(setup.getCollateralType());
			setup.setCollateralStructure(collateralStructure);
			String reference = setup.getCollateralRef();
			ExtendedFieldHeader extendedFieldHeader = setup.getCollateralStructure().getExtendedFieldHeader();
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			List<Map<String, Object>> extendedMapValues = extendedFieldRenderDAO.getExtendedFieldMap(reference,
					tableName.toString(), "");
			if (extendedMapValues != null) {
				List<ExtendedField> extendedDetails = new ArrayList<ExtendedField>();
				for (Map<String, Object> mapValues : extendedMapValues) {
					List<ExtendedFieldData> extendedFieldDataList = new ArrayList<ExtendedFieldData>();
					for (Entry<String, Object> entry : mapValues.entrySet()) {
						ExtendedFieldData exdFieldData = new ExtendedFieldData();
						if (StringUtils.isNotBlank(String.valueOf(entry.getValue()))
								|| !StringUtils.equals(String.valueOf(entry.getValue()), "null")) {
							exdFieldData.setFieldName(entry.getKey());
							exdFieldData.setFieldValue(String.valueOf(entry.getValue()));
							extendedFieldDataList.add(exdFieldData);
						}
					}
					ExtendedField extendedField = new ExtendedField();
					extendedField.setExtendedFieldDataList(extendedFieldDataList);
					extendedDetails.add(extendedField);
				}
				setup.setExtendedDetails(extendedDetails);
			}
		}

		logger.debug("Leaving");
		return collaterals;
	}

	@Override
	public boolean isThirdPartyUsed(String collateralRef, long custId) {
		logger.debug("Entering");
		boolean isThirdPartyUsed = getCollateralThirdPartyDAO().isThirdPartyUsed(collateralRef, custId);
		logger.debug("Leaving");
		return isThirdPartyUsed;
	}

}
