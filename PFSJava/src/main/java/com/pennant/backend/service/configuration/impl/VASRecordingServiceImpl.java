/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VASRecordingServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016 * *
 * Modified Date : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.applicationmaster.RelationshipOfficerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennapps.core.util.ObjectUtil;

/**
 * Service implementation for methods that depends on <b>VASRecording</b>.<br>
 * 
 */
public class VASRecordingServiceImpl extends GenericService<VASRecording> implements VASRecordingService {
	private static final Logger logger = LogManager.getLogger(VASRecordingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private VASRecordingDAO vASRecordingDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private CheckListDetailService checkListDetailService;
	private VASConfigurationService vASConfigurationService;
	private FinanceCheckListReferenceDAO financeCheckListReferenceDAO;
	private DocumentTypeDAO documentTypeDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private DocumentDetailValidation vasDocumentValidation;
	private CustomerDAO customerDAO;
	private CollateralSetupDAO collateralSetupDAO;
	private VasRecordingValidation vasRecordingValidation;
	private PostingsDAO postingsDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private RelationshipOfficerDAO relationshipOfficerDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceWorkFlowService financeWorkFlowService;
	private VehicleDealerDAO vehicleDealerDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private AccountingSetDAO accountingSetDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceTypeDAO financeTypeDAO;

	/**
	 * @return the vASRecording
	 */
	@Override
	public VASRecording getVASRecording() {
		return vASRecordingDAO.getVASRecording();
	}

	/**
	 * @return the vASRecording for New Record
	 */
	@Override
	public VASRecording getNewVASRecording() {
		return vASRecordingDAO.getNewVASRecording();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table VASRecording/VASRecording_Temp
	 * by using VASRecordingDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using VASRecordingDAO's update method 3) Audit the record in to AuditHeader and AdtVASRecording
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	@Override
	public void updateVasStatus(String status, String vasReference) {
		vASRecordingDAO.updateVasStatus(status, vasReference);
	}

	@Override
	public void updateVasPaymentId(String reference, long paymentInsId) {
		vASRecordingDAO.updateVasStatus(reference, paymentInsId);
	}

	@Override
	public List<VASPremiumCalcDetails> getPremiumCalcDeatils(VASPremiumCalcDetails premiumCalcDetails) {
		return vASConfigurationService.getPremiumCalcDeatils(premiumCalcDetails);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table VASRecording/VASRecording_Temp
	 * by using VASRecordingDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using VASRecordingDAO's update method 3) Audit the record in to AuditHeader and AdtVASRecording
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
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
		if (StringUtils.isNotBlank(errorCode)) {
			ErrorDetail errorDetail = new ErrorDetail(errorCode, null);
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(errorDetail, auditHeader.getUsrLanguage()));
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tableType = "";

		if (vASRecording.isWorkflow()) {
			tableType = "_Temp";
		}

		if (vASRecording.isNewRecord()) {
			vASRecordingDAO.save(vASRecording, tableType);
		} else {
			vASRecordingDAO.update(vASRecording, tableType);
		}

		// VAS documents
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
		if (vASRecording.getExtendedFieldRender() != null
				&& vASRecording.getVasConfiguration().getExtendedFieldHeader() != null) {
			List<AuditDetail> details = vASRecording.getAuditDetailMap().get("ExtendedFieldDetail");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					vASRecording.getVasConfiguration().getExtendedFieldHeader(), tableType, 0);

			auditDetails.addAll(details);
		}
		if (StringUtils.equals(vASRecording.getSourceId(), PennantConstants.FINSOURCE_ID_API)
				&& !CollectionUtils.isEmpty(vASRecording.getFinFeeDetailsList())) {
			for (FinFeeDetail finFeeDetail : vASRecording.getFinFeeDetailsList()) {
				long id = finFeeDetailDAO.save(finFeeDetail, false, "_temp");
				finFeeDetail.setFeeID(id);
				if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PART_OF_SALE_PRICE)
						&& StringUtils.equals(vASRecording.getPostingAgainst(), VASConsatnts.VASAGAINST_FINANCE)) {
					String errorCodes = procAddVasFees(vASRecording, finFeeDetail);
					if (StringUtils.isNotBlank(errorCodes)) {
						ErrorDetail errorDetail = new ErrorDetail(errorCodes, null);
						auditHeader
								.setErrorDetails(ErrorUtil.getErrorDetail(errorDetail, auditHeader.getUsrLanguage()));
						logger.debug("Leaving");
						return auditHeader;
					}
				}
			}
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * VASRecording by using VASRecordingDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtVASRecording by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		vASRecordingDAO.delete(vASRecording, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new VASRecording(), vASRecording.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				vASRecording.getBefImage(), vASRecording));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVASRecordingByRef fetch the details by using VASRecordingDAO's getVASRecordingByRef method.
	 * 
	 * @param vasReference (String)
	 * @param type         (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public VASRecording getVASRecordingByRef(String vasReference, String nextRoleCode, boolean isEnquiry) {
		logger.debug("Entering");

		VASRecording vr = vASRecordingDAO.getVASRecordingByReference(vasReference, "_View");

		if (vr == null) {
			return null;
		}

		// VasconfigurationDetails
		vr.setVasConfiguration(vASConfigurationService.getApprovedVASConfigurationByCode(vr.getProductCode(), true));

		// Set CustomerDetails
		vr.setVasCustomer(vASRecordingDAO.getVasCustomerCif(vr.getPrimaryLinkRef(), vr.getPostingAgainst()));

		// Extended Field Details
		StringBuilder tableName = new StringBuilder();
		tableName.append(VASConsatnts.MODULE_NAME);
		tableName.append("_");
		tableName.append(vr.getProductCode());
		tableName.append("_ED");

		Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(vasReference, tableName.toString(),
				"_View");
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
			extendedFieldRender
					.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
							: String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			extendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			extendedFieldRender
					.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
							: String.valueOf(extFieldMap.get("NextRoleCode")));
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
			vr.setExtendedFieldRender(extendedFieldRender);
		}

		// Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(vasReference,
				VASConsatnts.MODULE_NAME, FinServiceEvent.ORG, "_View");
		if (vr.getDocuments() != null && !vr.getDocuments().isEmpty()) {
			vr.getDocuments().addAll(documentList);
		} else {
			vr.setDocuments(documentList);
		}
		// Not Required Other Process details for the Enquiry
		if (!isEnquiry) {

			// Agreement Details & Check List Details
			if (StringUtils.isNotEmpty(vr.getRecordType())
					&& !StringUtils.equals(vr.getRecordType(), PennantConstants.RECORD_TYPE_UPD)
					&& !StringUtils.equals(vr.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				vr = getProcessEditorDetails(vr, nextRoleCode, FinServiceEvent.ORG);
			} else if (StringUtils.equals(VASConsatnts.STATUS_CANCEL, vr.getVasStatus())
					|| StringUtils.isEmpty(vr.getRecordType())) {

				// Get details from Postings
				// Reversals of Tran Code and & DEBIT -CREDIT
				// set to Vas recording bean as Return dataset

				List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
				String[] finEvent = { AccountingEvent.VAS_FEE, AccountingEvent.INSPAY };
				list = postingsDAO.getPostingsByVasref(vr.getVasReference(), finEvent);

				for (ReturnDataSet returnDataSet : list) {
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
				vr.setReturnDataSetList(list);
			}
		} else {
			String[] finEvent = { AccountingEvent.VAS_FEE, AccountingEvent.INSPAY };
			vr.setReturnDataSetList(postingsDAO.getPostingsByVasref(vr.getVasReference(), finEvent));
		}
		logger.debug("Leaving");
		return vr;

	}

	/**
	 * getVASRecordingForRebook fetch the details by using VASRecordingDAO's getVASRecordingByRef method.
	 * 
	 * @param vasReference (String)
	 * @param type         (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public VASRecording getVASRecordingForInsurance(String vasReference, String nextRoleCode, String event,
			boolean isEnquiry) {
		logger.debug("Entering");

		VASRecording vasRecording = vASRecordingDAO.getVASRecordingByReference(vasReference, "_View");
		if (vasRecording != null) {

			// VasconfigurationDetails
			vasRecording.setVasConfiguration(
					vASConfigurationService.getApprovedVASConfigurationByCode(vasRecording.getProductCode(), true));

			// Set CustomerDetails
			vasRecording.setVasCustomer(vASRecordingDAO.getVasCustomerCif(vasRecording.getPrimaryLinkRef(),
					vasRecording.getPostingAgainst()));

			if (!VASConsatnts.VAS_EVENT_CANCELLATION.equals(event)) {
				// Extended Field Details
				StringBuilder tableName = new StringBuilder();
				tableName.append(VASConsatnts.MODULE_NAME);
				tableName.append("_");
				tableName.append(vasRecording.getProductCode());
				tableName.append("_ED");

				Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(vasReference,
						tableName.toString(), "_View");
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
					extendedFieldRender.setRecordStatus(
							StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
									: String.valueOf(extFieldMap.get("RecordStatus")));
					extFieldMap.remove("RecordStatus");
					extendedFieldRender
							.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
									: String.valueOf(extFieldMap.get("RoleCode")));
					extFieldMap.remove("RoleCode");
					extendedFieldRender.setNextRoleCode(
							StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
									: String.valueOf(extFieldMap.get("NextRoleCode")));
					extFieldMap.remove("NextRoleCode");
					extendedFieldRender
							.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
									: String.valueOf(extFieldMap.get("TaskId")));
					extFieldMap.remove("TaskId");
					extendedFieldRender.setNextTaskId(
							StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null") ? ""
									: String.valueOf(extFieldMap.get("NextTaskId")));
					extFieldMap.remove("NextTaskId");
					extendedFieldRender.setRecordType(
							StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null") ? ""
									: String.valueOf(extFieldMap.get("RecordType")));
					extFieldMap.remove("RecordType");
					extendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
					extFieldMap.remove("WorkflowId");
					extendedFieldRender.setMapValues(extFieldMap);
					vasRecording.setExtendedFieldRender(extendedFieldRender);
				}

				// Document Details
				List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(vasReference,
						VASConsatnts.MODULE_NAME, event, "_View");
				if (vasRecording.getDocuments() != null && !vasRecording.getDocuments().isEmpty()) {
					vasRecording.getDocuments().addAll(documentList);
				} else {
					vasRecording.setDocuments(documentList);
				}
				// Not Required Other Process details for the Enquiry
				if (!isEnquiry) {
					// Agreement Details & Check List Details
					if (StringUtils.isNotEmpty(vasRecording.getRecordType())
							&& !StringUtils.equals(vasRecording.getRecordType(), PennantConstants.RECORD_TYPE_UPD)
							&& !StringUtils.equals(vasRecording.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						vasRecording = getProcessEditorDetails(vasRecording, nextRoleCode, event);
					}
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

		VasCustomer vasCustomer = vASRecordingDAO.getVasCustomerCif(primaryLinkRef, postingAgainst);
		logger.debug("Leaving");
		return vasCustomer;
	}

	/**
	 * getApprovedVASRecordingByCode fetch the details by using VASRecordingDAO's getApprovedVASRecordingByCode method .
	 * with parameter productCode . it fetches the approved records from the VASRecording.
	 * 
	 * @param productCode (String)
	 * @return VASRecording
	 */
	@Override
	public VASRecording getVASRecordingByReference(String vasRefrence) {
		return vASRecordingDAO.getVASRecordingByReference(vasRefrence, "_AView");
	}

	@Override
	public VASRecording getVASRecording(String vasRefrence, String vasStatus) {
		return vASRecordingDAO.getVASRecording(vasRefrence, vasStatus, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using vASRecordingDAO.delete with parameters
	 * vASRecording,"" b) NEW Add new record in to main table by using vASRecordingDAO.save with parameters
	 * vASRecording,"" c) EDIT Update record in the main table by using vASRecordingDAO.update with parameters
	 * vASRecording,"" 3) Delete the record from the workFlow table by using vASRecordingDAO.delete with parameters
	 * vASRecording,"_Temp" 4) Audit the record in to AuditHeader and AdtVASRecording by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtVASRecording by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		aAuditHeader = businessValidation(aAuditHeader, "doApprove", false);
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);
		VASRecording vASRecording = new VASRecording("");
		BeanUtils.copyProperties((VASRecording) auditHeader.getAuditDetail().getModelData(), vASRecording);

		// VAS Cancellation process verification
		String errorCode = procVasCancellation(vASRecording, true);
		if (StringUtils.isNotBlank(errorCode)) {
			ErrorDetail errorDetail = new ErrorDetail(errorCode, null);
			aAuditHeader.setErrorDetails(ErrorUtil.getErrorDetail(errorDetail, auditHeader.getUsrLanguage()));
			logger.debug("Leaving");
			return aAuditHeader;
		}

		// Processing Accounting Details
		if ((StringUtils.equals(vASRecording.getRecordType(), PennantConstants.RECORD_TYPE_NEW)
				|| StringUtils.equals(vASRecording.getVasStatus(), VASConsatnts.STATUS_CANCEL)
				|| StringUtils.equals(vASRecording.getVasStatus(), VASConsatnts.STATUS_SURRENDER))
				&& !(VASConsatnts.STATUS_MAINTAINCE.equals(vASRecording.getVasStatus()))) {
			if (vASRecording.isInsuranceCancel()) {
				VASRecording recording = executeInsuranceAccountingProcess(auditHeader);
				vASRecording.setManualAdviseId(recording.getManualAdviseId());
				vASRecording.setReceivableAdviseId(recording.getReceivableAdviseId());
			} else {
				executeAccountingProcess(auditHeader, SysParamUtil.getAppDate());
			}
		}

		if (vASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(vASRecording, "", tranType));
			vASRecordingDAO.delete(vASRecording, "");
		} else {
			vASRecording.setRoleCode("");
			vASRecording.setNextRoleCode("");
			vASRecording.setTaskId("");
			vASRecording.setNextTaskId("");
			vASRecording.setWorkflowId(0);

			if (!StringUtils.equals(vASRecording.getVasStatus(), VASConsatnts.STATUS_CANCEL)
					&& !StringUtils.equals(vASRecording.getVasStatus(), VASConsatnts.STATUS_SURRENDER)) {
				vASRecording.setVasStatus(VASConsatnts.STATUS_NORMAL);
			}
			if (vASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				vASRecording.setRecordType("");
				if (!StringUtils.equals(vASRecording.getVasStatus(), VASConsatnts.STATUS_CANCEL)
						&& !StringUtils.equals(vASRecording.getVasStatus(), VASConsatnts.STATUS_MAINTAINCE)
						&& !vASRecording.isInsuranceCancel()) {
					vASRecording.setStatus(InsuranceConstants.PENDING);
				}
				vASRecordingDAO.save(vASRecording, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				vASRecording.setRecordType("");
				vASRecordingDAO.update(vASRecording, "");
			}

			// Checklist details
			if (vASRecording.getVasCheckLists() != null && !vASRecording.getVasCheckLists().isEmpty()) {
				auditDetails.addAll(processingCheckListDetailsList(vASRecording, ""));
			}

			// VAS Document Details
			List<DocumentDetails> documentsList = vASRecording.getDocuments();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = vASRecording.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, vASRecording, "");
				auditDetails.addAll(details);
			}

			// Vas Extended field Details
			if (vASRecording.getExtendedFieldRender() != null
					&& vASRecording.getVasConfiguration().getExtendedFieldHeader() != null) {
				List<AuditDetail> details = vASRecording.getAuditDetailMap().get("ExtendedFieldDetail");
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						vASRecording.getVasConfiguration().getExtendedFieldHeader(), "", 0);

				auditDetails.addAll(details);
			}
		}
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new VASRecording(), vASRecording.getExcludeFields());
		if (!StringUtils.equals(vASRecording.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditDetailList.addAll(listDeletion(vASRecording, "_Temp", auditHeader.getAuditTranType()));
			vASRecordingDAO.delete(vASRecording, "_Temp");

			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					vASRecording.getBefImage(), vASRecording));
			auditHeader.setAuditDetails(auditDetailList);
			auditHeaderDAO.addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				vASRecording.getBefImage(), vASRecording));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using vASRecordingDAO.delete with parameters vASRecording,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtVASRecording by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
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
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				vASRecording.getBefImage(), vASRecording));

		auditDetails.addAll(listDeletion(vASRecording, "_Temp", auditHeader.getAuditTranType()));
		vASRecordingDAO.delete(vASRecording, "_Temp");

		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method for Checking Cancellation of VAS Record
	 * 
	 * @param recording
	 */
	private String procVasCancellation(VASRecording recording, boolean approvalProcess) {
		logger.debug("Entering");

		// If it is Coming from Insurance cancellation no need to check.
		if (recording.isInsuranceCancel()) {
			return null;
		}

		// VAS Cancellation validations
		if (!StringUtils.equals(recording.getVasStatus(), VASConsatnts.STATUS_CANCEL)
				|| !recording.isFinanceProcess()) {
			logger.debug("Leaving");
			return null;
		}

		// Get Fee detail record
		FinFeeDetail vasFeeDetail = finFeeDetailDAO.getVasFeeDetailById(recording.getVasReference(), false, "");
		if (vasFeeDetail == null) {
			logger.debug("Leaving");
			return "90323";
		}

		// If No Balance amount to schedule effected
		if (vasFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) == 0) {
			finFeeDetailDAO.statusUpdate(vasFeeDetail.getFeeID(), FinanceConstants.FEE_STATUS_CANCEL, false, "");
			logger.debug("Leaving");
			return null;
		}

		// If No Schedule Maintenance , no need to check Finance Details
		if (!StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)
				&& !StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
				&& !StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
				&& !StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)) {

			finFeeDetailDAO.statusUpdate(vasFeeDetail.getFeeID(), FinanceConstants.FEE_STATUS_CANCEL, false, "");
			logger.debug("Leaving");
			return null;
		}

		Long finID = financeMainDAO.getFinID(recording.getPrimaryLinkRef(), TableType.TEMP_TAB);
		if (finID == null) {
			logger.debug("Leaving");
			return "90324";
		}

		// Get Schedule Details of Primary Link Reference and check paid Status

		finID = financeMainDAO.getFinID(recording.getPrimaryLinkRef(), TableType.MAIN_TAB);

		FinScheduleData scheduleData = getFinSchDataByFinRef(finID, false);
		List<FinanceScheduleDetail> schdList = scheduleData.getFinanceScheduleDetails();
		boolean isSchdPaid = false;
		Date recalFromDate = null;
		for (int i = 1; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if (recalFromDate == null) {
				recalFromDate = curSchd.getSchDate();
			}
			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0) {
				if (!StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
					isSchdPaid = true;
					break;
				}
			}
		}

		// If Schedules Paid for any dates then VAS cancellation not allowed
		if (isSchdPaid) {
			logger.debug("Leaving");
			return "90325";
		}

		// If not approval Process, No need of actual updations Only validation is enough
		if (!approvalProcess) {
			logger.debug("Leaving");
			return null;
		}

		// Based on Fee Schedule Method schedules need to Reset
		// 1. Deduct From Disbursement : No Change
		// 2. Add to Disbursement on Profit Calculation : Do partial Settlement on Start Date and Recalculate total EMI
		// 3. Schedule Terms(First Term / N Terms / Entire Tenor) : Schedule Reversal of Sum Amount in Fees
		if (StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {

			FinanceMain fm = scheduleData.getFinanceMain();
			fm.setEventFromDate(fm.getFinStartDate());
			fm.setEventToDate(fm.getMaturityDate());
			fm.setRecalFromDate(recalFromDate);
			fm.setRecalToDate(fm.getMaturityDate());
			fm.setFeeChargeAmt(fm.getFeeChargeAmt().subtract(vasFeeDetail.getRemainingFee()));

			// Disbursement Details updation
			for (FinanceDisbursement cusrDisb : scheduleData.getDisbursementDetails()) {
				if (DateUtil.compare(cusrDisb.getDisbDate(), fm.getFinStartDate()) == 0) {
					cusrDisb.setFeeChargeAmt(cusrDisb.getFeeChargeAmt().subtract(vasFeeDetail.getRemainingFee()));
					break;
				}
			}

			// Schedule Details Updation
			for (FinanceScheduleDetail curSchd : schdList) {
				if (DateUtil.compare(curSchd.getSchDate(), fm.getFinStartDate()) == 0) {
					curSchd.setFeeChargeAmt(curSchd.getFeeChargeAmt().subtract(vasFeeDetail.getRemainingFee()));
					break;
				}
			}

			// Schedule Recalculation
			scheduleData = ScheduleCalculator.reCalSchd(scheduleData, fm.getScheduleMethod());

			// Finance Data Deletion
			listDeletion(fm.getFinID(), false);

			// Schedule Updations
			scheduleData.getFinanceMain().setVersion(fm.getVersion() + 1);
			financeMainDAO.update(scheduleData.getFinanceMain(), TableType.MAIN_TAB, false);
			listSave(scheduleData, false);

		} else if (StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(),
				CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
				|| StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
				|| StringUtils.equals(vasFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)) {

			List<FinFeeScheduleDetail> feeSchdList = finFeeScheduleDetailDAO
					.getFeeScheduleByFeeID(vasFeeDetail.getFeeID(), false, "");
			for (FinFeeScheduleDetail feeSchd : feeSchdList) {

				// Schedule Details Updation
				for (FinanceScheduleDetail curSchd : schdList) {
					if (DateUtil.compare(curSchd.getSchDate(), feeSchd.getSchDate()) == 0) {
						curSchd.setFeeSchd(curSchd.getFeeSchd().subtract(feeSchd.getSchAmount()));
						curSchd.setSchdFeeOS(curSchd.getSchdFeeOS().subtract(feeSchd.getSchAmount()));
						break;
					}
				}
			}

			financeScheduleDetailDAO.deleteByFinReference(scheduleData.getFinanceMain().getFinID(), "", false, 0);
			financeScheduleDetailDAO.saveList(schdList, "", false);

			// Schedule Version Updating
			financeMainDAO.updateSchdVersion(scheduleData.getFinanceMain(), false);
		}

		// Update Fee Details record with Cancel status
		finFeeDetailDAO.statusUpdate(vasFeeDetail.getFeeID(), FinanceConstants.FEE_STATUS_CANCEL, false, "");

		logger.debug("Leaving");
		return null;
	}

	private FinScheduleData getFinSchDataByFinRef(long finID, boolean isPennding) {
		logger.debug("Entering");

		FinScheduleData schdData = new FinScheduleData();

		if (!isPennding) {
			schdData.setFinanceMain(financeMainDAO.getFinanceMainById(finID, "", false));
			schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
			schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false));
			schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, "", false));
		} else {
			schdData.setFinanceMain(financeMainDAO.getFinanceMainById(finID, "_View", false));
			schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "_View", false));
			schdData.setDisbursementDetails(
					financeDisbursementDAO.getFinanceDisbursementDetails(finID, "_View", false));
			schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, "_View", false));
			schdData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "_View"));
			schdData.setFinanceType(financeTypeDAO.getFinanceTypeByFinType(schdData.getFinanceMain().getFinType()));
		}
		logger.debug("Leaving");
		return schdData;
	}

	private void listDeletion(long finID, boolean isPennding) {
		logger.debug("Entering");
		if (!isPennding) {
			financeScheduleDetailDAO.deleteByFinReference(finID, "", false, 0);
			financeDisbursementDAO.deleteByFinReference(finID, "", false, 0);
			repayInstructionDAO.deleteByFinReference(finID, "", false, 0);
		} else {
			financeScheduleDetailDAO.deleteByFinReference(finID, "_temp", false, 0);
			financeDisbursementDAO.deleteByFinReference(finID, "_temp", false, 0);
			repayInstructionDAO.deleteByFinReference(finID, "_temp", false, 0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to save Finance Details
	 */
	private void listSave(FinScheduleData schdData, boolean isPendding) {
		logger.debug(Literal.ENTERING);

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<FinanceDisbursement> disbursements = schdData.getDisbursementDetails();
		List<RepayInstruction> repayInstructions = schdData.getRepayInstructions();

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (FinanceScheduleDetail schd : schedules) {
			schd.setFinID(finID);
			schd.setFinReference(finReference);
		}

		for (FinanceDisbursement disbursement : disbursements) {
			disbursement.setFinID(finID);
			disbursement.setFinReference(finReference);
		}

		for (RepayInstruction rpayIns : repayInstructions) {
			rpayIns.setFinID(finID);
			rpayIns.setFinReference(finReference);
		}

		String tableType = "";

		if (isPendding) {
			tableType = "_Temp";
		}

		financeScheduleDetailDAO.saveList(schedules, tableType, false);

		financeDisbursementDAO.saveList(disbursements, tableType, false);

		repayInstructionDAO.saveList(repayInstructions, tableType, false);

		if ("".equals(tableType)) {
			financeMainDAO.updateSchdVersion(fm, false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the next process
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = getVasRecordingValidation().validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		VASRecording vasRecording = (VASRecording) auditDetail.getModelData();
		String usrLanguage = vasRecording.getUserDetails().getLanguage();

		// VAS Check List Details
		List<FinanceCheckListReference> vasCheckList = vasRecording.getVasCheckLists();
		if (vasCheckList != null && !vasCheckList.isEmpty()) {
			List<AuditDetail> auditDetailList = vasRecording.getAuditDetailMap().get("CheckListDetails");
			auditDetailList = checkListDetailService.validate(auditDetailList, method, usrLanguage);
			auditDetails.addAll(auditDetailList);
		}

		// VAS Document details Validation
		List<DocumentDetails> documentDetailsList = vasRecording.getDocuments();
		if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
			List<AuditDetail> details = vasRecording.getAuditDetailMap().get("DocumentDetails");
			details = getVASDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Extended field details Validation
		if (vasRecording.getExtendedFieldRender() != null
				&& vasRecording.getVasConfiguration().getExtendedFieldHeader() != null) {
			List<AuditDetail> details = vasRecording.getAuditDetailMap().get("ExtendedFieldDetail");
			ExtendedFieldHeader extendedFieldHeader = vasRecording.getVasConfiguration().getExtendedFieldHeader();
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
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public VASRecording getProcessEditorDetails(VASRecording vasRecording, String nextRoleCode, String procEdtEvent) {
		logger.debug("Entering");

		String productCode = vasRecording.getProductCode();
		List<FinanceReferenceDetail> aggrementList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> checkListdetails = new ArrayList<FinanceReferenceDetail>(1);

		// Fetch Total Process editor Details
		List<FinanceReferenceDetail> finRefDetails = financeReferenceDetailDAO.getFinanceProcessEditorDetails(
				productCode, StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent, "_VASVIEW");

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
		// Finance Agreement Details
		vasRecording.setAggrements(aggrementList);

		// Check list Details
		checkListDetailService.fetchVASCheckLists(vasRecording, checkListdetails);

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
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails, VASRecording vasRecording,
			String tableType) {
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
			if (!(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode()))) {
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
				documentDetails.setFinReference(vasRecording.getFinReference());
				if (DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())) {
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
					} else if (documentDetails.isNewRecord()) {
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
					documentDetails.setFinEvent(FinServiceEvent.ORG);

					saveDocument(DMSModule.FINANCE, DMSModule.VAS, documentDetails);

					documentDetailsDAO.save(documentDetails, tableType);
				}

				if (updateRecord) {
					saveDocument(DMSModule.FINANCE, DMSModule.VAS, documentDetails);
					documentDetailsDAO.update(documentDetails, tableType);
				}

				if (deleteRecord
						&& ((StringUtils.isEmpty(tableType) && !isTempRecord) || (StringUtils.isNotEmpty(tableType)))) {
					if (!tableType.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						documentDetailsDAO.delete(documentDetails, tableType);
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
					customerDocumentDAO.save(custdoc, "");
				} else {
					customerDocumentDAO.update(custdoc, "");
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
				financeCheckListReferenceDAO.save(vasChecklistRef, tableType);
			} else if (vasChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				financeCheckListReferenceDAO.delete(vasChecklistRef, tableType);
			} else if (vasChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				financeCheckListReferenceDAO.update(vasChecklistRef, tableType);
			}
			auditDetails.get(i).setModelData(vasChecklistRef);
		}

		logger.debug("Leaving ");
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
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		VASRecording vasRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (vasRecording.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// VAS Document Details
		if (vasRecording.getDocuments() != null && vasRecording.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(vasRecording, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// Collateral Checklist Details
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
			vasCheckLists = checkListDetailService.getCheckListByFinRef(vasRecording.getVasReference(), tableType);
			vasRecording.setVasCheckLists(vasCheckLists);

			if (vasCheckLists != null && !vasCheckLists.isEmpty()) {
				auditDetailMap.put("CheckListDetails", setCheckListsAuditData(vasRecording, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CheckListDetails"));
			}
		}

		// VAS Extended Field Details
		if (vasRecording.getExtendedFieldRender() != null
				&& vasRecording.getVasConfiguration().getExtendedFieldHeader() != null) {
			auditDetailMap.put("ExtendedFieldDetail", extendedFieldDetailsService
					.setExtendedFieldsAuditData(vasRecording.getExtendedFieldRender(), auditTranType, method, null));
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
	public List<AuditDetail> setDocumentDetailsAuditData(VASRecording vasRecording, String auditTranType,
			String method) {
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
			documentDetailsDAO.deleteList(docList, tableType);
		}

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = vasRecording.getAuditDetailMap().get("ExtendedFieldDetail");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			ExtendedFieldHeader extendedFieldHeader = vasRecording.getVasConfiguration().getExtendedFieldHeader();
			auditList.addAll(extendedFieldDetailsService.delete(extendedFieldHeader, vasRecording.getVasReference(),
					tableType, auditTranType, extendedDetails));

		}

		logger.debug("Leaving");
		return auditList;
	}

	/**
	 * Method for Deleting CheckLists related records in _Temp/Main tables depend on method type
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
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], vasCheckListRef.getBefImage(),
						vasCheckListRef));
			}
			financeCheckListReferenceDAO.delete(vasCheckListRef, tableType);
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

		CustomerDocument customerDocument = customerDocumentDAO.getCustomerDocumentById(
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

		saveDocument(DMSModule.FINANCE, DMSModule.VAS, documentDetails);

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
	 * 
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private void executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws InterfaceException {
		logger.debug("Entering");

		VASRecording vASRecording = new VASRecording("");
		BeanUtils.copyProperties((VASRecording) auditHeader.getAuditDetail().getModelData(), vASRecording);

		if (vASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

			AEEvent aeEvent = new AEEvent();
			aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
			aeEvent.setAccountingEvent(AccountingEvent.VAS_FEE);
			aeEvent.setFinReference(vASRecording.getVasReference());
			aeEvent.setValueDate(curBDay);
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			if (amountCodes == null) {
				amountCodes = new AEAmountCodes();
			}

			// Based on VAS Created Against, details will be captured
			if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vASRecording.getPostingAgainst())) {
				FinanceMain financeMain = financeMainDAO.getFMForVAS(vASRecording.getPrimaryLinkRef());
				if (financeMain == null) {
					throw new AppException("Selected LAN is either rejected or not active.");
				}
				amountCodes.setFinType(financeMain.getFinType());
				aeEvent.setBranch(financeMain.getFinBranch());
				aeEvent.setCcy(financeMain.getFinCcy());
				aeEvent.setCustID(financeMain.getCustID());
			} else if (StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vASRecording.getPostingAgainst())) {
				Customer customer = customerDAO.getCustomerByCIF(vASRecording.getPrimaryLinkRef(), "");
				aeEvent.setBranch(customer.getCustDftBranch());
				aeEvent.setCcy(customer.getCustBaseCcy());
				aeEvent.setCustID(customer.getCustID());
			} else if (StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vASRecording.getPostingAgainst())) {
				CollateralSetup collateralSetup = collateralSetupDAO
						.getCollateralSetupByRef(vASRecording.getPrimaryLinkRef(), "");
				// TODO:Need to modify for getting branch as per performance
				Customer customer = customerDAO.getCustomerByID(collateralSetup.getDepositorId(), "");
				aeEvent.setCcy(collateralSetup.getCollateralCcy());
				aeEvent.setCustID(collateralSetup.getDepositorId());
				aeEvent.setBranch(customer.getCustDftBranch());
			}

			aeEvent.setCcy(SysParamUtil.getAppCurrency());
			// For GL Code
			VehicleDealer vehicleDealer = vehicleDealerDAO.getDealerShortCodes(vASRecording.getProductCode());
			amountCodes.setProductCode(vehicleDealer.getProductShortCode());
			amountCodes.setDealerCode(vehicleDealer.getDealerShortCode());

			aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
			vASRecording.getDeclaredFieldValues(aeEvent.getDataMap());
			aeEvent.getAcSetIDList().add(vASRecording.getVasConfiguration().getFeeAccounting());
			postingsPreparationUtil.postAccounting(aeEvent);

		} else if (StringUtils.equals(VASConsatnts.STATUS_CANCEL, vASRecording.getVasStatus())) {
			postingsPreparationUtil.postReveralsByFinreference(vASRecording.getVasReference());
			Long paymentId = vASRecordingDAO.getPaymentInsId(vASRecording.getVasReference(), "");
			if (paymentId != null && paymentId > 0) {
				vASRecordingDAO.updateVasInsStatus(paymentId);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Execute Insurance Posting Details
	 * 
	 * @param auditHeader
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private VASRecording executeInsuranceAccountingProcess(AuditHeader auditHeader) throws InterfaceException {
		logger.debug("Entering");

		VASRecording vASRecording = new VASRecording("");
		BeanUtils.copyProperties((VASRecording) auditHeader.getAuditDetail().getModelData(), vASRecording);

		// Based on VAS Created Against, details will be captured
		if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vASRecording.getPostingAgainst())) {
			if (vASRecording.getPaymentInsId() == 0) {
				vASRecording.setPaymentInsId(Long.MIN_VALUE);
			}
			// Creating the payable Advise
			createPayableAdvise(vASRecording);

			if (vASRecording.getPaymentInsId() != Long.MIN_VALUE) {
				// Creating the Receivable Advise against the insurance partner.
				createReceivableAdvise(vASRecording);
			}
		}

		if (vASRecording.getPaymentInsId() != Long.MIN_VALUE) {
			// Prepare the accounting when payment done to the insurance partner
			createInsurancePaymentAccounting(auditHeader, vASRecording);
		}

		logger.debug(Literal.LEAVING);
		return vASRecording;
	}

	private void createInsurancePaymentAccounting(AuditHeader auditHeader, VASRecording vASRecording) {
		AEEvent aeEvent = new AEEvent();
		aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
		aeEvent.setAccountingEvent(AccountingEvent.CANINS);
		aeEvent.setFinReference(vASRecording.getVasReference());
		aeEvent.setEntityCode(vASRecording.getEntityCode());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent.setCcy(SysParamUtil.getAppCurrency());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Based on VAS Created Against, details will be captured
		if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vASRecording.getPostingAgainst())) {
			FinanceMain financeMain = financeMainDAO.getFMForVAS(vASRecording.getPrimaryLinkRef());
			amountCodes.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setCustID(financeMain.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vASRecording.getPostingAgainst())) {
			Customer customer = customerDAO.getCustomerByCIF(vASRecording.getPrimaryLinkRef(), "");
			aeEvent.setBranch(customer.getCustDftBranch());
			aeEvent.setCcy(customer.getCustBaseCcy());
			aeEvent.setCustID(customer.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vASRecording.getPostingAgainst())) {
			CollateralSetup collateralSetup = collateralSetupDAO
					.getCollateralSetupByRef(vASRecording.getPrimaryLinkRef(), "");
			// TODO:Need to modify for getting branch as per performance
			Customer customer = customerDAO.getCustomerByID(collateralSetup.getDepositorId(), "");
			aeEvent.setCcy(collateralSetup.getCollateralCcy());
			aeEvent.setCustID(collateralSetup.getDepositorId());
			aeEvent.setBranch(customer.getCustDftBranch());
		}
		// For GL Code
		VehicleDealer vehicleDealer = vehicleDealerDAO.getDealerShortCodes(vASRecording.getProductCode());
		amountCodes.setProductCode(vehicleDealer.getProductShortCode());
		amountCodes.setDealerCode(vehicleDealer.getDealerShortCode());

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		vASRecording.getDeclaredFieldValues(aeEvent.getDataMap());

		long accountsetId = accountingSetDAO.getAccountingSetId(AccountingEvent.CANINS, AccountingEvent.CANINS);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
	}

	/**
	 * Creating a manual advise for insurance Cancel or surrender amount.
	 * 
	 * @param vASRecording
	 */
	private void createPayableAdvise(VASRecording vASRecording) {
		logger.debug("Entering");

		Date appDate = SysParamUtil.getAppDate();

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		manualAdvise.setAdviseType(AdviseType.PAYABLE.id());
		manualAdvise.setFinReference(vASRecording.getPrimaryLinkRef());
		manualAdvise.setFeeTypeID(vASRecording.getVasConfiguration().getFeeType());
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(vASRecording.getCancelAmt());
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks("Insurance cancel or surrender payble amount.");
		manualAdvise.setBounceID(0);
		manualAdvise.setReceiptID(0);
		manualAdvise.setValueDate(appDate);
		manualAdvise.setPostDate(appDate);
		manualAdvise.setReservedAmt(BigDecimal.ZERO);
		manualAdvise.setBalanceAmt(vASRecording.getCancelAmt());

		manualAdvise.setVersion(0);
		manualAdvise.setLastMntBy(vASRecording.getLastMntBy());
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRoleCode("");
		manualAdvise.setNextRoleCode("");
		manualAdvise.setTaskId("");
		manualAdvise.setNextTaskId("");
		manualAdvise.setRecordType("");
		manualAdvise.setWorkflowId(0);

		String manualAdviseId = manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);
		vASRecording.setManualAdviseId(Long.valueOf(manualAdviseId));

		logger.debug("Leaving");
	}

	/**
	 * Creating a Receivable advise for insurance Cancel or surrender amount.
	 * 
	 * @param vASRecording
	 */
	private void createReceivableAdvise(VASRecording vASRecording) {
		logger.debug("Entering");

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		manualAdvise.setAdviseType(AdviseType.RECEIVABLE.id());
		manualAdvise.setFinReference(vASRecording.getVasReference());
		manualAdvise.setFeeTypeID(vASRecording.getVasConfiguration().getFeeType());
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(vASRecording.getCancelAmt());
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks("Insurance cancel or surrender receivble amount.");
		manualAdvise.setBounceID(0);
		manualAdvise.setReceiptID(0);
		manualAdvise.setValueDate(SysParamUtil.getAppDate());
		manualAdvise.setPostDate(SysParamUtil.getAppDate());
		manualAdvise.setReservedAmt(BigDecimal.ZERO);
		manualAdvise.setBalanceAmt(vASRecording.getCancelAmt());

		manualAdvise.setVersion(0);
		manualAdvise.setLastMntBy(vASRecording.getLastMntBy());
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRoleCode("");
		manualAdvise.setNextRoleCode("");
		manualAdvise.setTaskId("");
		manualAdvise.setNextTaskId("");
		manualAdvise.setRecordType("");
		manualAdvise.setWorkflowId(0);

		String receibleAdviseId = manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);
		vASRecording.setReceivableAdviseId(Long.valueOf(receibleAdviseId));

		logger.debug("Leaving");
	}

	@Override
	public List<VASRecording> getVasRecordingsByPrimaryLinkRef(String primaryLinkRef) {
		return vASRecordingDAO.getVASRecordingsByLinkRef(primaryLinkRef, "");
	}

	// validations For API Specific
	@Override
	public AuditDetail doValidations(VASRecording vasRecording, boolean isPending) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();
		if (vasRecording != null) {
			if (StringUtils.isBlank(vasRecording.getProductCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "product";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (StringUtils.isBlank(vasRecording.getPostingAgainst())) {
				String[] valueParm = new String[1];
				valueParm[0] = "postingAgainst";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			} else {
				if (StringUtils.equals("Loan", vasRecording.getPostingAgainst())) {
					vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
				}
			}

			if (!(StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vasRecording.getPostingAgainst())
					|| StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vasRecording.getPostingAgainst())
					|| StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vasRecording.getPostingAgainst()))) {
				String[] valueParm = new String[2];
				valueParm[0] = "postingAgainst";
				valueParm[1] = vasRecording.getPostingAgainst();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;

			}
			if (StringUtils.isBlank(vasRecording.getPrimaryLinkRef())) {
				String[] valueParm = new String[1];
				valueParm[0] = "primaryLinkRef";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (vasRecording.getFee() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fee";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (vasRecording.getWaivedAmt() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "WaivedAmt";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90242", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			VASConfiguration vASConfiguration = vASConfigurationService
					.getVASConfigurationByCode(vasRecording.getProductCode());
			if (vASConfiguration == null || !vASConfiguration.isActive()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Product";
				valueParm[1] = vasRecording.getProductCode();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
					vasRecording.getProductCode(), FinServiceEvent.ORG, VASConsatnts.MODULE_NAME);
			if (financeWorkFlow == null) {
				String[] valueParm = new String[2];
				valueParm[0] = vasRecording.getProductCode();
				valueParm[1] = "workflow";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90339", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (!StringUtils.equals(vASConfiguration.getRecAgainst(), vasRecording.getPostingAgainst())) {
				String[] valueParm = new String[2];
				valueParm[0] = "PostingAgainst";
				valueParm[1] = vasRecording.getPostingAgainst();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (StringUtils.equalsIgnoreCase(VASConsatnts.VASAGAINST_CUSTOMER, vasRecording.getPostingAgainst())) {
				Customer customer = customerDAO.getCustomerByCIF(vasRecording.getPrimaryLinkRef(), "_View");
				if (customer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getPrimaryLinkRef();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90101", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else if (StringUtils.equalsIgnoreCase(VASConsatnts.VASAGAINST_FINANCE,
					vasRecording.getPostingAgainst())) {
				Long finID = null;
				if (isPending) {
					finID = financeMainDAO.getActiveFinID(vasRecording.getPrimaryLinkRef(), TableType.TEMP_TAB);
					if (finID != null) {
						FinanceMain financeMain = financeMainDAO.getFinanceMainById(finID, "_Temp", false);
						if (financeMain != null) {
							vasRecording.setWorkflowId(financeMain.getWorkflowId());
						}
					}
				} else {
					finID = financeMainDAO.getActiveFinID(vasRecording.getPrimaryLinkRef(), TableType.MAIN_TAB);
				}

				if (finID == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getPrimaryLinkRef();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90201", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else if (StringUtils.equalsIgnoreCase(VASConsatnts.VASAGAINST_COLLATERAL,
					vasRecording.getPostingAgainst())) {
				int count = collateralSetupDAO.getCountByCollateralRef(vasRecording.getPrimaryLinkRef());
				if (count <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getPrimaryLinkRef();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90906", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (!vASConfiguration.isAllowFeeToModify()) {
				if (vasRecording.getFee().compareTo(vASConfiguration.getVasFee()) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Fee:" + vasRecording.getFee();
					valueParm[1] = "VasConfig Fee:" + vASConfiguration.getVasFee();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30570", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else if (vasRecording.getFee().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fee";
				valueParm[1] = "Zero";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90205", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}

			if (vasRecording.getWaivedAmt().compareTo(vasRecording.getFee()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Waived Amount:" + vasRecording.getWaivedAmt();
				valueParm[1] = "Fee:" + vasRecording.getFee();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30565", "", valueParm));
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
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90264", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "feePaymentMode";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if (vasRecording.getValueDate() == null) {
				vasRecording.setValueDate(SysParamUtil.getAppDate());
			} else {
				if (vasRecording.getValueDate().before(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE))
						|| vasRecording.getValueDate().after(SysParamUtil.getAppDate())) {
					String[] valueParm = new String[3];
					valueParm[0] = "Value Date";
					valueParm[1] = DateUtil
							.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE));
					valueParm[2] = DateUtil.formatToLongDate(SysParamUtil.getAppDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
					return auditDetail;
				}
			}
			if (vASConfiguration.isFeeAccrued()) {
				if (vasRecording.getAccrualTillDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "accrualTillDate";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					if (vasRecording.getAccrualTillDate().before(SysParamUtil.getAppDate()) || vasRecording
							.getAccrualTillDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
						String[] valueParm = new String[3];
						valueParm[0] = "AccrualTillDate";
						valueParm[1] = DateUtil.formatToLongDate(SysParamUtil.getAppDate());
						valueParm[2] = DateUtil.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
						return auditDetail;
					}

				}
			} else {
				if (vasRecording.getAccrualTillDate() != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "accrualTillDate";
					valueParm[1] = "FeeAccrued";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90298", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				vasRecording.setAccrualTillDate(SysParamUtil.getAppDate());
			}
			if (vASConfiguration.isRecurringType()) {
				if (vasRecording.getRecurringDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "recurringDate";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					if (vasRecording.getRecurringDate().before(SysParamUtil.getAppDate())
							|| vasRecording.getRecurringDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
						String[] valueParm = new String[3];
						valueParm[0] = "RecurringDate";
						valueParm[2] = DateUtil.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
						valueParm[1] = DateUtil.formatToLongDate(SysParamUtil.getAppDate());
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
						return auditDetail;
					}
				}
			} else {
				if (vasRecording.getRecurringDate() != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "RecurringDate";
					valueParm[1] = "RecurringType is Active";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90298", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				vasRecording.setRecurringDate(SysParamUtil.getAppDate());
				vasRecording.setRenewalFee(BigDecimal.ZERO);
			}
			if (StringUtils.isNotBlank(vasRecording.getDsaId())) {
				RelationshipOfficer relationshipOfficer = relationshipOfficerDAO
						.getRelationshipOfficerById(vasRecording.getDsaId(), "");
				if (relationshipOfficer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getDsaId();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (StringUtils.isNotBlank(vasRecording.getDmaId())) {
				RelationshipOfficer dmaCode = relationshipOfficerDAO.getRelationshipOfficerById(vasRecording.getDmaId(),
						"");
				if (dmaCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getDmaId();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm));
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
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			if (StringUtils.isNotBlank(vasRecording.getReferralId())) {
				RelationshipOfficer referralId = relationshipOfficerDAO
						.getRelationshipOfficerById(vasRecording.getReferralId(), "");
				if (referralId == null) {
					String[] valueParm = new String[1];
					valueParm[0] = vasRecording.getReferralId();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
			vasRecording.setFeeAccounting(vASConfiguration.getFeeAccounting());
			if (vasRecording.getDocuments() != null && !vasRecording.getDocuments().isEmpty()) {
				for (DocumentDetails detail : vasRecording.getDocuments()) {
					// validate Dates
					if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
						if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
							String[] valueParm = new String[2];
							valueParm[0] = DateUtil.format(detail.getCustDocIssuedOn(), PennantConstants.XMLDateFormat);
							valueParm[1] = DateUtil.format(detail.getCustDocExpDate(), PennantConstants.XMLDateFormat);
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65030", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					// validate custDocIssuedCountry
					if (StringUtils.isBlank(detail.getCustDocIssuedCountry())) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustDocIssuedCountry";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
					int count = customerDocumentDAO.getCustCountryCount(detail.getCustDocIssuedCountry());
					if (count <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "custDocIssuedCountry";
						valueParm[1] = detail.getCustDocIssuedCountry();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
					}

					if (StringUtils.equals(detail.getDocCategory(), "03")) {
						Pattern pattern = Pattern.compile("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
						if (detail.getCustDocTitle() != null) {
							detail.setCustDocTitle(detail.getCustDocTitle().toUpperCase());

							Matcher matcher = pattern.matcher(detail.getCustDocTitle());
							if (matcher.find() == false) {
								String[] valueParm = new String[0];
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90251", "", valueParm));
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
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90123", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					DocumentType docType = documentTypeDAO.getDocumentTypeById(detail.getDocCategory(), "");
					if (docType == null || (DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode()))) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDocCategory();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90401", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
					if (!(StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_PDF)
							|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_DOC)
							|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_DOCX)
							|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_IMAGE)
							|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_ZIP)
							|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_7Z)
							|| StringUtils.equals(detail.getDoctype(), PennantConstants.DOC_TYPE_RAR))) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDoctype();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90122", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
					}
					String docFormate = detail.getDocName().substring(detail.getDocName().lastIndexOf(".") + 1);
					if (StringUtils.equals(detail.getDocName(), docFormate)) {
						String[] valueParm = new String[1];
						valueParm[0] = "docName: " + docFormate;
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90291", "", valueParm));
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
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
						}
					}
				}

			}
			int extendedDetailsCount = 0;
			List<ExtendedFieldDetail> exdFldConfig = vASConfiguration.getExtendedFieldHeader()
					.getExtendedFieldDetails();
			if (vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails() != null) {
				for (ExtendedFieldDetail detail : exdFldConfig) {
					if (detail.isFieldMandatory()) {
						extendedDetailsCount++;
					}
				}
			}
			if (extendedDetailsCount > 0
					&& (vasRecording.getExtendedDetails() == null || vasRecording.getExtendedDetails().isEmpty())) {
				String[] valueParm = new String[1];
				valueParm[0] = "ExtendedDetails";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
				return auditDetail;
			}
			if (vasRecording.getExtendedDetails() != null && !vasRecording.getExtendedDetails().isEmpty()) {
				for (ExtendedField details : vasRecording.getExtendedDetails()) {
					int exdMandConfigCount = 0;
					for (ExtendedFieldData extendedFieldData : details.getExtendedFieldDataList()) {
						if (StringUtils.isBlank(extendedFieldData.getFieldName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldName";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
						if (StringUtils.isBlank(Objects.toString(extendedFieldData.getFieldValue(), ""))) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldValue";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
						boolean isFeild = false;
						if (vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails() != null) {
							for (ExtendedFieldDetail detail : vASConfiguration.getExtendedFieldHeader()
									.getExtendedFieldDetails()) {
								if (StringUtils.equals(detail.getFieldName(), extendedFieldData.getFieldName())) {
									if (detail.isFieldMandatory()) {
										exdMandConfigCount++;
									}
									List<ErrorDetail> errList = extendedFieldDetailsService
											.validateExtendedFieldData(detail, extendedFieldData);
									auditDetail.getErrorDetails().addAll(errList);
									isFeild = true;
								}
							}
							if (!isFeild) {
								String[] valueParm = new String[1];
								valueParm[0] = "vas setup";
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90265", "", valueParm));
								auditDetail.setErrorDetail(errorDetail);
								return auditDetail;
							}
						}
					}
					if (extendedDetailsCount != exdMandConfigCount) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90297", "", null)));
						return auditDetail;
					}
				}

			}
			Map<String, Object> mapValues = new HashMap<String, Object>();
			if (vasRecording.getExtendedDetails() != null) {
				for (ExtendedField details : vasRecording.getExtendedDetails()) {
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
			if (vASConfiguration.isPostValidationReq()) {
				errors = extendedFieldDetailsService.getPostValidationErrors(vASConfiguration.getPostValidation(),
						mapValues);
			}
			if (errors != null) {
				List<ScriptError> errorsList = errors.getAll();
				for (ScriptError error : errorsList) {
					auditDetail.setErrorDetail(new ErrorDetail("", "90909", "", error.getValue(), null, null));
				}
			}
		}
		if (isPending) {
			if (CollectionUtils.isEmpty(vasRecording.getFinFeeDetailsList())) {
				String[] valueParm = new String[1];
				valueParm[0] = "fees";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			for (FinFeeDetail finFeeDetail : vasRecording.getFinFeeDetailsList()) {
				if (finFeeDetail.getActualAmount().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "feeAmount";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					if (finFeeDetail.getActualAmount().compareTo(vasRecording.getFee()) != 0) {
						String[] valueParm = new String[3];
						valueParm[0] = "fee ";
						valueParm[1] = "feeAmount";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90277", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
				if (StringUtils.isBlank(finFeeDetail.getFeeScheduleMethod())) {
					String[] valueParm = new String[1];
					valueParm[0] = "feeMethod";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					if (!StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), FinanceConstants.BPI_NO)
							&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_PART_OF_DISBURSE)
							&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_PART_OF_SALE_PRICE)
							&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
							&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
							&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
							&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_PAID_BY_CUSTOMER)
							&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
						String[] valueParm = new String[2];
						valueParm[0] = finFeeDetail.getFeeScheduleMethod();
						valueParm[1] = CalculationConstants.REMFEE_PART_OF_DISBURSE + ","
								+ CalculationConstants.REMFEE_PART_OF_SALE_PRICE + ","
								+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + ","
								+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + ","
								+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + ","
								+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ","
								+ CalculationConstants.REMFEE_WAIVED_BY_BANK;
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90243", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
			}
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				boolean extended = list.get(i).isExtended();
				String fields = list.get(i).getAuditField();
				String fieldValues = list.get(i).getAuditValue();
				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {

						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());

						AuditDetail auditDetail = new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
								befImg, object);
						if (extended) {
							auditDetail.setExtended(extended);
						}
						auditDetail.setAuditField(fields);
						auditDetail.setAuditValue(fieldValues);
						auditDetailsList.add(auditDetail);
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}

		logger.debug("Leaving");
		return auditDetailsList;
	}

	private String procAddVasFees(VASRecording vASRecording, FinFeeDetail finFeeDetail) {

		Long finID = financeMainDAO.getFinID(vASRecording.getPrimaryLinkRef());

		FinScheduleData finScheduleData = getFinSchDataByFinRef(finID, true);
		FinanceMain finMian = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		boolean isSchdPaid = false;
		Date recalFromDate = null;
		for (int i = 1; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if (recalFromDate == null) {
				recalFromDate = curSchd.getSchDate();
			}
			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0) {
				if (!StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
					isSchdPaid = true;
					break;
				}
			}
		}

		// If Schedules Paid for any dates then VAS not allowed
		if (isSchdPaid) {
			logger.debug("Leaving");
			return "90325";
		}
		if (!vASRecording.getFinFeeDetailsList().isEmpty() && finScheduleData != null) {
			finScheduleData.getFinFeeDetailList().addAll(vASRecording.getFinFeeDetailsList());
			finMian.setEventFromDate(finMian.getFinStartDate());
			finMian.setEventToDate(finMian.getMaturityDate());
			finMian.setRecalFromDate(recalFromDate);
			finMian.setRecalToDate(finMian.getMaturityDate());
			finMian.setFeeChargeAmt(finMian.getFeeChargeAmt().add(finFeeDetail.getActualAmount()));
			finMian.setBefImage(finMian);
			FinanceScheduleDetail fsd = finScheduleData.getFinanceScheduleDetails().get(0);
			if (fsd != null) {
				fsd.setFeeChargeAmt(finMian.getFeeChargeAmt() == null ? BigDecimal.ZERO : finMian.getFeeChargeAmt());
			}
			// Schedule Recalculation
			finScheduleData = ScheduleCalculator.reCalSchd(finScheduleData, finMian.getScheduleMethod());
			// Schedule Recalculation
			if (!finScheduleData.getFinFeeDetailList().isEmpty()) {
				finScheduleData = FeeScheduleCalculator.feeSchdBuild(finScheduleData);
			}
			// Finance Data Deletion
			listDeletion(finID, true);

			// Schedule Updations
			finScheduleData.getFinanceMain().setVersion(finMian.getVersion() + 1);
			financeMainDAO.update(finScheduleData.getFinanceMain(), TableType.TEMP_TAB, false);
			listSave(finScheduleData, true);
		}

		return null;
	}

	@Override
	public VASRecording getVASRecordingDetails(VASRecording vasRecording) {
		logger.debug("Entering");

		// VASRecording vasRecording = vASRecordingDAO.getVASRecordingByReference(vasReference, tableType);
		if (vasRecording != null) {

			// VasconfigurationDetails
			/*
			 * vasRecording.setVasConfiguration(vASConfigurationService
			 * .getApprovedVASConfigurationByCode(vasRecording.getProductCode(), false));
			 */
			// Extended Field Details
			StringBuilder tableName = new StringBuilder();
			tableName.append(VASConsatnts.MODULE_NAME);
			tableName.append("_");
			tableName.append(vasRecording.getProductCode());
			tableName.append("_ED");

			Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField("VAS2005000037",
					tableName.toString(), "_View");
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
				extendedFieldRender.setRecordStatus(
						StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
								: String.valueOf(extFieldMap.get("RecordStatus")));
				extFieldMap.remove("RecordStatus");
				extendedFieldRender
						.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
								: String.valueOf(extFieldMap.get("RoleCode")));
				extFieldMap.remove("RoleCode");
				extendedFieldRender.setNextRoleCode(
						StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
								: String.valueOf(extFieldMap.get("NextRoleCode")));
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
				vasRecording.setExtendedFieldRender(extendedFieldRender);
			}
		}
		logger.debug("Leaving");
		return vasRecording;
	}

	@Override
	public List<VASRecording> getLoanReportVasRecordingByRef(String reference) {
		logger.debug(Literal.ENTERING);
		List<VASRecording> loanReportVasRecordingByRef = vASRecordingDAO.getLoanReportVasRecordingByRef(reference);
		for (VASRecording vasRecording : loanReportVasRecordingByRef) {

			if (loanReportVasRecordingByRef != null) {
				// VasconfigurationDetails
				vasRecording.setVasConfiguration(
						vASConfigurationService.getApprovedVASConfigurationByCode(vasRecording.getProductCode(), true));

			}
		}
		logger.debug(Literal.LEAVING);
		return loanReportVasRecordingByRef;
	}

	@Override
	public String getVasInsStatus(long paymentInsId) {
		return vASRecordingDAO.getVasInsStatus(paymentInsId);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	@Autowired
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	@Autowired
	public void setvASConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vASConfigurationService = vASConfigurationService;
	}

	@Autowired
	public void setFinanceCheckListReferenceDAO(FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}

	@Autowired
	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

	@Autowired
	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	@Autowired
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	@Autowired
	public void setRelationshipOfficerDAO(RelationshipOfficerDAO relationshipOfficerDAO) {
		this.relationshipOfficerDAO = relationshipOfficerDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	@Autowired
	public void setVehicleDealerDAO(VehicleDealerDAO vehicleDealerDAO) {
		this.vehicleDealerDAO = vehicleDealerDAO;
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	@Autowired
	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	@Autowired
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public DocumentDetailValidation getVASDocumentValidation() {
		if (vasDocumentValidation == null) {
			this.vasDocumentValidation = new DocumentDetailValidation(documentDetailsDAO);
		}
		return vasDocumentValidation;
	}

	public VasRecordingValidation getVasRecordingValidation() {
		if (vasRecordingValidation == null) {
			this.vasRecordingValidation = new VasRecordingValidation(vASRecordingDAO);
		}
		return this.vasRecordingValidation;
	}

}