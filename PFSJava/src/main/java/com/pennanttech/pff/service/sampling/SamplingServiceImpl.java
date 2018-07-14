package com.pennanttech.pff.service.sampling;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.validation.CustomerExtLiabilityValidation;
import com.pennant.backend.service.customermasters.validation.CustomerIncomeValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.sampling.model.SamplingCollateral;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAO;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;

public class SamplingServiceImpl extends GenericService<Sampling> implements SamplingService {
	private static final Logger logger = Logger.getLogger(SamplingServiceImpl.class);

	@Autowired
	protected SamplingDAO samplingDAO;

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	protected CustomerIncomeDAO customerIncomeDAO;
	@Autowired
	protected IncomeDetailDAO incomeDetailDAO;
	@Autowired
	protected ExternalLiabilityDAO externalLiabilityDAO;

	@Autowired
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustomerIncomeValidation customerIncomeValidation;
	private CustomerExtLiabilityValidation extLiabilityValidation;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private RuleExecutionUtil ruleExecutionUtil;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	@Autowired
	private DocumentManagerDAO documentManagerDAO;
	@Autowired
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentDetailValidation documentValidation;
	@Autowired
	private CustomerDetailsService customerDetailsService;
	@Autowired
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;

	@Override
	public void save(Sampling sampling) {
		setWorkflowDetails(sampling);

		samplingDAO.save(sampling, TableType.TEMP_TAB);
	}

	private void setWorkflowDetails(Sampling sampling) {
		String workflowType = ModuleUtil.getWorkflowType("Sampling");

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workflowType);

		WorkflowEngine engine = new WorkflowEngine(workFlowDetails.getWorkFlowXml());

		sampling.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		sampling.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		sampling.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		sampling.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		sampling.setWorkflowId(workFlowDetails.getWorkflowId());
		sampling.setRoleCode(workFlowDetails.getFirstTaskOwner());
		sampling.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
		sampling.setTaskId(engine.getUserTaskId(sampling.getRoleCode()));
		sampling.setNextTaskId(engine.getUserTaskId(sampling.getNextRoleCode()) + ";");
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Sampling sampling = (Sampling) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = new ArrayList<>();
		TableType tableType = TableType.MAIN_TAB;
		if (sampling.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (sampling.isNew()) {
			sampling.setId(samplingDAO.save(sampling, tableType));
			auditHeader.getAuditDetail().setModelData(sampling);
			auditHeader.setAuditReference(String.valueOf(sampling.getId()));
		} else {
			samplingDAO.update(sampling, tableType);
		}

		// Customer Income Details
		if (CollectionUtils.isNotEmpty(sampling.getCustomerIncomeList())) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("CustomerIncome");
			details = processingCustIncomeDetails(details, sampling, tableType.getSuffix(), sampling.getCustId());
			auditDetails.addAll(details);
		}

		// Obligation Details
		if (CollectionUtils.isNotEmpty(sampling.getCustomerExtLiabilityList())) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("ObligationDetails");
			details = processingObligations(details, sampling, tableType.getSuffix(), sampling.getCustId());
			auditDetails.addAll(details);
		}

		// Collateral Extended fields
		if (sampling.getExtFieldRenderList() != null && !sampling.getExtFieldRenderList().isEmpty()) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingSamplingExtendedFieldDetailList(details, sampling,
					CollateralConstants.VERIFICATION_MODULE, tableType);
			auditDetails.addAll(details);
		}

		// Sampling documents
		if (CollectionUtils.isNotEmpty(sampling.getDocuments())) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, sampling, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Sampling sampling = (Sampling) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(deleteChilds(sampling, "", auditHeader.getAuditTranType()));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	public List<AuditDetail> deleteChilds(Sampling sampling, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();

		// Customer Income Details.
		List<AuditDetail> custIncomeDetails = sampling.getAuditDetailMap().get("CustomerIncome");
		if (custIncomeDetails != null && !custIncomeDetails.isEmpty()) {
			CustomerIncome custIncome = new CustomerIncome();
			String[] fields = PennantJavaUtil.getFieldDetails(custIncome, custIncome.getExcludeFields());
			for (int i = 0; i < custIncomeDetails.size(); i++) {
				custIncome = (CustomerIncome) custIncomeDetails.get(i).getModelData();
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], custIncome.getBefImage(),
						custIncome));
				incomeDetailDAO.deletebyLinkId(custIncome.getLinkId(), tableType);
			}
		}

		// Obligation Details.
		List<AuditDetail> obligationDetails = sampling.getAuditDetailMap().get("ObligationDetails");
		if (obligationDetails != null && !obligationDetails.isEmpty()) {
			CustomerExtLiability custExtLiability = new CustomerExtLiability();
			String[] fields = PennantJavaUtil.getFieldDetails(custExtLiability, custExtLiability.getExcludeFields());
			for (int i = 0; i < obligationDetails.size(); i++) {
				custExtLiability = (CustomerExtLiability) obligationDetails.get(i).getModelData();
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						custExtLiability.getBefImage(), custExtLiability));
				externalLiabilityDAO.deleteByLinkId(custExtLiability.getLinkId(), tableType);
			}
		}

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = sampling.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && !extendedDetails.isEmpty()) {
			for (int i = 0; i < extendedDetails.size(); i++) {
				ExtendedFieldRender extRender = (ExtendedFieldRender) extendedDetails.get(i).getModelData();
				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(extRender.getTypeCode());
				tableName.append("_tv");
				auditList.addAll(
						extendedFieldDetailsService.delete(sampling.getExtendedFieldHeader(), extRender.getReference(),
								extRender.getTableName(), tableType, auditTranType, extendedDetails.get(i)));
			}
		}

		// Sampling Document Details.
		List<AuditDetail> documentDetails = sampling.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && !documentDetails.isEmpty()) {
			DocumentDetails document = new DocumentDetails();
			List<DocumentDetails> documents = new ArrayList<>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				document = (DocumentDetails) documentDetails.get(i).getModelData();
				document.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				documents.add(document);
				auditList.add(
						new AuditDetail(auditTranType, i + 1, fields[0], fields[1], document.getBefImage(), document));
			}
			documentDetailsDAO.deleteList(documents, tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		Sampling sampling = new Sampling();
		BeanUtils.copyProperties((Sampling) auditHeader.getAuditDetail().getModelData(), sampling);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(sampling.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(samplingDAO.getSampling(sampling.getId(), ""));
		}
		if (sampling.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(sampling, "", tranType));
		} else {
			sampling.setRoleCode("");
			sampling.setNextRoleCode("");
			sampling.setTaskId("");
			sampling.setNextTaskId("");
			sampling.setWorkflowId(0);

			if (sampling.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				sampling.setRecordType("");
				samplingDAO.save(sampling, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				sampling.setRecordType("");
				samplingDAO.update(sampling, TableType.MAIN_TAB);
			}

		}
		// Customer Income Details
		if (CollectionUtils.isNotEmpty(sampling.getCustomerIncomeList())) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("CustomerIncome");
			details = processingCustIncomeDetails(details, sampling, "", sampling.getCustId());
			auditDetails.addAll(details);
		}

		// Obligation Details
		if (CollectionUtils.isNotEmpty(sampling.getCustomerExtLiabilityList())) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("ObligationDetails");
			details = processingObligations(details, sampling, "", sampling.getCustId());
			auditDetails.addAll(details);
		}

		// Extended field Details
		if (sampling.getExtFieldRenderList() != null && !sampling.getExtFieldRenderList().isEmpty()) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingSamplingExtendedFieldDetailList(details, sampling,
					CollateralConstants.VERIFICATION_MODULE, TableType.MAIN_TAB);
			auditDetails.addAll(details);
		}

		// Sampling Document Details
		List<DocumentDetails> documentsList = sampling.getDocuments();
		if (documentsList != null && !documentsList.isEmpty()) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, sampling, "");
			auditDetails.addAll(details);
		}

		List<AuditDetail> auditDetailList = new ArrayList<>();
		auditDetailList.addAll(deleteChilds(sampling, "_Temp", auditHeader.getAuditTranType()));
		samplingDAO.delete(sampling, TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new Sampling(), sampling.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				sampling.getBefImage(), sampling));

		int auditSeq = 1;
		for (AuditDetail auditDetail : auditDetailList) {
			auditDetail.setAuditSeq(auditSeq++);
		}

		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(sampling);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		List<AuditDetail> auditDetails = new ArrayList<>();
		Sampling sampling = (Sampling) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new Sampling(), sampling.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				sampling.getBefImage(), sampling));

		auditDetails.addAll(deleteChilds(sampling, "_Temp", auditHeader.getAuditTranType()));
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param sampling
	 * @param type
	 * @return
	 */
	private List<AuditDetail> saveOrUpdateDocuments(List<AuditDetail> auditDetails, Sampling sampling, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}

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
			documentDetails.setLastMntBy(sampling.getLastMntBy());
			documentDetails.setWorkflowId(0);

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
					documentDetails.setReferenceId(String.valueOf(sampling.getId()));
				}
				if (documentDetails.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(documentDetails.getDocImage());
					documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
				}
				// Pass the docRefId here to save this in place of docImage
				// column. Or add another column for now to
				// save this.
				documentDetailsDAO.save(documentDetails, type);
			}

			if (updateRecord) {
				// When a document is updated, insert another file into the
				// DocumentManager table's.
				// Get the new DocumentManager.id & set to
				// documentDetails.getDocRefId()
				if (documentDetails.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(documentDetails.getDocImage());
					documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
				}
				documentDetailsDAO.update(documentDetails, type);
			}

			if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
				if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					documentDetailsDAO.delete(documentDetails, type);
				}
			}

			if (approveRec) {
				documentDetails.setFinEvent("");
				documentDetails.setRecordType(rcdType);
				documentDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(documentDetails);

		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public void calculateEligilibity(Sampling sampling) {
		HashMap<String, Object> fieldsandvalues = new HashMap<>();
		BigDecimal amount = BigDecimal.ZERO;
		String ruleCode;
		Object object = null;

		BigDecimal custTotalIncome = sampling.getTotalIncome();
		BigDecimal custTotalExpense = sampling.getTotalLiability();

		fieldsandvalues.put("custCtgCode", sampling.getCustCategory());
		fieldsandvalues.put("custTotalIncome", custTotalIncome);
		fieldsandvalues.put("custTotalExpense", custTotalExpense);
		fieldsandvalues.put("finProfitRate", sampling.getInterestRate());
		fieldsandvalues.put("noOfTerms", sampling.getTenure());

		fieldsandvalues.put("Total_Co_Applicants_Income", BigDecimal.ZERO);
		fieldsandvalues.put("Co_Applicants_Obligation_External", BigDecimal.ZERO);
		fieldsandvalues.put("Co_Applicants_Obligation_Internal", BigDecimal.ZERO);
		fieldsandvalues.put("Customer_Obligation_External", BigDecimal.ZERO);
		fieldsandvalues.put("Customer_Obligation_Internal", BigDecimal.ZERO);

		ruleCode = sampling.getEligibilityRules().get(Sampling.RULE_CODE_FOIRAMT);
		if (ruleCode != null) {
			object = excuteRule(ruleCode, sampling.getFinccy(), fieldsandvalues);
		}
		if (object != null) {
			amount = (BigDecimal) object;
		}
		sampling.setFoirEligibility(amount);

		amount = BigDecimal.ZERO;
		ruleCode = sampling.getEligibilityRules().get(Sampling.RULE_CODE_IIRMAX);
		if (ruleCode != null) {
			object = excuteRule(ruleCode, sampling.getFinccy(), fieldsandvalues);
		}
		if (object != null) {
			amount = (BigDecimal) object;
		}
		sampling.setIrrEligibility(amount);

		BigDecimal loanEligibilityAmount = BigDecimal.ZERO;
		BigDecimal requestedAmount = sampling.getLoanAmountRequested();

		if (sampling.getFoirEligibility().compareTo(sampling.getIrrEligibility()) == -1) {
			loanEligibilityAmount = sampling.getFoirEligibility();
		} else {
			loanEligibilityAmount = sampling.getIrrEligibility();
		}

		if (requestedAmount.compareTo(loanEligibilityAmount) == -1) {
			loanEligibilityAmount = requestedAmount;
		}

		if (loanEligibilityAmount == BigDecimal.ZERO) {
			loanEligibilityAmount = requestedAmount;
		}

		sampling.setLoanEligibility(loanEligibilityAmount);

		BigDecimal rate = sampling.getInterestRate();
		int frqequency = 12;
		int noOfTerms = sampling.getTenure();
		BigDecimal principle = new BigDecimal(100000);

		BigDecimal r = rate.divide(new BigDecimal(100).multiply(new BigDecimal(frqequency)), 10,
				BigDecimal.ROUND_HALF_DOWN);
		BigDecimal nTimesOfr = (r.add(BigDecimal.ONE)).pow(noOfTerms);
		BigDecimal numerator = principle.multiply(nTimesOfr).multiply(r);
		BigDecimal denominator = nTimesOfr.subtract(BigDecimal.ONE);
		sampling.setEmi(numerator.divide(denominator, 10, BigDecimal.ROUND_HALF_DOWN));
	}

	private Object excuteRule(String foirRule, String finCcy, HashMap<String, Object> fieldsandvalues) {
		logger.info(String.format("Rule>> %s", foirRule));
		return ruleExecutionUtil.executeRule(foirRule, fieldsandvalues, finCcy, RuleReturnType.DECIMAL);
	}

	@Override
	public Sampling getSampling(Sampling sampling, String type) {
		logger.info(Literal.ENTERING);
		Sampling temp = samplingDAO.getSampling(sampling.getId(), type);
		
		if (temp != null) {
			String finReference = sampling.getKeyReference();
			long custId = 0;
			temp.setCustomers(samplingDAO.getCustomers(finReference, "_view"));
			for (Customer customer : temp.getCustomers()) {
				if ("1".equals(customer.getCustTypeCode())) {
					custId = customer.getCustID();
					break;
				}
			}
			temp.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(custId, true, "_View"));

			List<String> collateralTypes = samplingDAO.getCollateralTypes(finReference);

			for (String collateralType : collateralTypes) {
				temp.getCollaterals().addAll(samplingDAO.getCollaterals(finReference, collateralType));
			}

			temp.setCustomerIncomeList(samplingDAO.getIncomes(sampling.getId()));
			temp.setCustomerExtLiabilityList(samplingDAO.getObligations(sampling.getId()));

			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
					String.valueOf(sampling.getId()), CollateralConstants.SAMPLING_MODULE, "", "_View");
			if (temp.getDocuments() != null && !temp.getDocuments().isEmpty()) {
				temp.getDocuments().addAll(documentList);
			} else {
				temp.setDocuments(documentList);
			}

			if (CollectionUtils.isNotEmpty(temp.getCollaterals())) {
				List<String> collReference = new ArrayList<>();
				List<SamplingCollateral> collList = temp.getCollaterals();
				Map<String, ExtendedFieldRender> extFieldRender = new LinkedHashMap<>();
				
				for (SamplingCollateral collateral : collList) {
					collReference.add(collateral.getCollateralRef());
					
					StringBuilder table = new StringBuilder();
					table.append(CollateralConstants.VERIFICATION_MODULE);
					table.append("_");
					table.append(collateral.getCollateralType());
					table.append("_tv");
										
					String reference = collateral.getCollateralRef();
					Map<String, Object> renderMap=null;

					long linkId = samplingDAO.getCollateralLinkId(reference, sampling.getId(), "");
					String sLinkId = "S".concat(String.valueOf(linkId));
					
					renderMap = samplingDAO.getExtendedField(sLinkId, collateral.getSeqNo(), table.toString().toLowerCase(), "_view");

					if (MapUtils.isNotEmpty(renderMap)) {
						List<ExtendedFieldRender> renderList = new ArrayList<>();
						Map<String, Object> extFieldMap = renderMap;
						ExtendedFieldRender field = new ExtendedFieldRender();
						field.setReference(String.valueOf(extFieldMap.get("Reference")));
						extFieldMap.remove("Reference");
						field.setSeqNo(Integer.valueOf(extFieldMap.get("SeqNo").toString()));
						extFieldMap.remove("SeqNo");
						field.setVersion(Integer.valueOf(extFieldMap.get("Version").toString()));
						extFieldMap.remove("Version");
						field.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
						extFieldMap.remove("LastMntOn");
						field.setLastMntBy(Long.valueOf(extFieldMap.get("LastMntBy").toString()));
						extFieldMap.remove("LastMntBy");
						field.setRecordStatus(
								StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
										: String.valueOf(extFieldMap.get("RecordStatus")));
						extFieldMap.remove("RecordStatus");
						field.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
								: String.valueOf(extFieldMap.get("RoleCode")));
						extFieldMap.remove("RoleCode");
						field.setNextRoleCode(
								StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
										: String.valueOf(extFieldMap.get("NextRoleCode")));
						extFieldMap.remove("NextRoleCode");
						field.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
								: String.valueOf(extFieldMap.get("TaskId")));
						extFieldMap.remove("TaskId");
						field.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null")
								? "" : String.valueOf(extFieldMap.get("NextTaskId")));
						extFieldMap.remove("NextTaskId");
						field.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null")
								? "" : String.valueOf(extFieldMap.get("RecordType")));
						extFieldMap.remove("RecordType");
						field.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
						extFieldMap.remove("WorkflowId");
						field.setMapValues(extFieldMap);
						renderList.add(field);

						extFieldRender.put(field.getReference().concat("-").concat(String.valueOf(field.getSeqNo())), field);
					}
				}
				temp.setExtFieldRenderList(extFieldRender);
			}

			Object object = extendedFieldDetailsService.getLoanOrgExtendedValue(finReference,
					Sampling.REQ_LOAN_AMOUNT_EXTEND_FIELD);

			if (object != null && StringUtils.isNumeric(object.toString())) {
				temp.setLoanAmountRequested((BigDecimal) object);
			}

			if (temp.getLoanAmountRequested() == null) {
				temp.setLoanAmountRequested(BigDecimal.ZERO);
			}
			temp.setEligibilityRules(samplingDAO.getEligibilityRules());
			return temp;
		}
		logger.info(Literal.LEAVING);
		return null;
	}
	
	
	private BigDecimal getTotal(List<CustomerIncome> incomes) {
		BigDecimal total = BigDecimal.ZERO;

		for (CustomerIncome income : incomes) {
			if (PennantConstants.INCOME.equals(income.getIncomeExpense())) {
				total = total.add(income.getCalculatedAmount());
			}
		}

		return total;
	}

	@Override
	public Sampling getSampling(String keyReference, String type) {
		Sampling sampling = samplingDAO.getSampling(keyReference, type);

		if (sampling == null) {
			return null;
		}

		Long incomeLinkId = samplingDAO.getLinkId(sampling.getId(), "link_sampling_incomes_snap");
		if (incomeLinkId != null && incomeLinkId != 0) {
			sampling.setOriginalTotalIncome(getTotal(incomeDetailDAO.getTotalIncomeByLinkId(incomeLinkId)));
		} else {
			sampling.setOriginalTotalIncome(getTotal(incomeDetailDAO.getTotalIncomeByFinReference(keyReference)));
		}

		sampling.setTotalIncome(getTotal(incomeDetailDAO.getTotalIncomeBySamplingId(sampling.getId())));

		Long liabilityLinkId = samplingDAO.getLinkId(sampling.getId(), "link_sampling_liabilities_snap");
		if (liabilityLinkId != null && liabilityLinkId != 0) {
			sampling.setOriginalTotalLiability(externalLiabilityDAO.getTotalLiabilityByLinkId(liabilityLinkId));
		} else {
			sampling.setOriginalTotalLiability(externalLiabilityDAO.getTotalLiabilityByFinReference(keyReference));
		}

		sampling.setTotalLiability(externalLiabilityDAO.getTotalLiabilityBySamplingId(sampling.getId()));
		
		List<SamplingCollateral> collaters = samplingDAO.getCollateralTypesBySamplingId(sampling.getId());
		
		List<String> linkIds = samplingDAO.getCollateralLinkIds(sampling.getId());
		Set<String> collateralType = new HashSet<>();
		List<SamplingCollateral> list = new ArrayList<>();
		for (SamplingCollateral collateral : collaters) {
			String ctype = collateral.getCollateralType();
			if (!collateralType.contains(ctype)) {
				list = samplingDAO.getCollateralsBySamplingId(linkIds, ctype);
				collateralType.add(ctype);
				for (SamplingCollateral sc : list) {
					for (SamplingCollateral cl : collaters) {
						String linkId = sc.getCollateralRef().replaceAll("S", "");
						if(StringUtils.equals(cl.getLinkId(), linkId)) {
							sc.setCollateralRef(cl.getCollateralRef());
						}
					}
				}
				
				sampling.getCollaterals().addAll(list);
			}
		}
		
		sampling.setReamrksMap(samplingDAO.getRemarks(sampling.getId()));

		String eligibilityRule = null;

		String finCCy = sampling.getFinType();

		switch (finCCy) {
		case "205":
			eligibilityRule = "ASLRECOM";
			break;
		case "207":
			eligibilityRule = "LIVESTOC";
			break;
		case "101":
		case "102":
			eligibilityRule = "HFRECOM";
			break;
		case "203":
		case "204":
		case "208":
			eligibilityRule = "LAPRECOM";
			break;
		case "206":
			eligibilityRule = "ADFLREC";
			break;
		default:
			break;
		}

		sampling.setOriginalLoanEligibility(samplingDAO.getLoanEligibility(keyReference, eligibilityRule));

		return sampling;
	}

	@Override
	public List<AuditDetail> processingCustIncomeDetails(List<AuditDetail> auditDetails, Sampling sampling, String type,
			long custId) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			CustomerIncome custIncome = (CustomerIncome) auditDetails.get(i).getModelData();
			custIncome.setLinkId(samplingDAO.getIncomeLinkId(sampling.getId(), custIncome.getCustId()));

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type.toString())) {
				approveRec = true;
				custIncome.setRoleCode("");
				custIncome.setNextRoleCode("");
				custIncome.setTaskId("");
				custIncome.setNextTaskId("");
			}

			if (custIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (custIncome.isNewRecord()) {
				saveRecord = true;
				if (custIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					custIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (custIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					custIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (custIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					custIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (custIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (custIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (custIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (custIncome.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = custIncome.getRecordType();
				recordStatus = custIncome.getRecordStatus();
				custIncome.setRecordType("");
				custIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				incomeDetailDAO.save(custIncome, type);
			}

			if (updateRecord) {
				incomeDetailDAO.update(custIncome, type);
			}

			if (deleteRecord) {
				incomeDetailDAO.delete(custIncome.getId(), type);
			}

			if (approveRec) {
				custIncome.setRecordType(rcdType);
				custIncome.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(custIncome);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	@Override
	public List<AuditDetail> processingObligations(List<AuditDetail> auditDetails, Sampling sampling, String type,
			long custId) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			CustomerExtLiability liability = (CustomerExtLiability) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				liability.setRoleCode("");
				liability.setNextRoleCode("");
				liability.setTaskId("");
				liability.setNextTaskId("");
			}

			liability.setWorkflowId(0);
			liability.setCustId(custId);

			if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (liability.isNewRecord()) {
				saveRecord = true;
				if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					liability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					liability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					liability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (liability.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = liability.getRecordType();
				recordStatus = liability.getRecordStatus();
				liability.setRecordType("");
				liability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				liability.setLinkId(samplingDAO.getLiabilityLinkId(sampling.getId(), liability.getCustId()));
				externalLiabilityDAO.save(liability, type);
			}

			if (updateRecord) {
				externalLiabilityDAO.update(liability, type);
			}

			if (deleteRecord) {
				externalLiabilityDAO.delete(liability.getId(), type);
			}

			if (approveRec) {
				liability.setRecordType(rcdType);
				liability.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(liability);
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;

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
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		getAuditDetails(auditHeader, method);
		Sampling sampling = (Sampling) auditDetail.getModelData();
		String usrLanguage = sampling.getUserDetails().getLanguage();

		// Income Validation
		if (sampling.getCustomerIncomeList() != null && sampling.getCustomerIncomeList().size() > 0) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("CustomerIncome");
			auditDetails.addAll(
					getCustomerIncomeValidation().incomeListValidation(details, sampling.getId(), method, usrLanguage));
		}

		// Obligation Validation
		if (sampling.getCustomerExtLiabilityList() != null && sampling.getCustomerExtLiabilityList().size() > 0) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("ObligationDetails");
			details = getCustomerExtLiabilityValidation().extLiabilityListValidation(details, sampling.getId(), method,
					usrLanguage);
			auditDetails.addAll(details);
		}

		// Collateral Extended field details Validation
		if (sampling.getExtFieldRenderList() != null) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.validateSamplingDetails(details, sampling, method, usrLanguage,
					CollateralConstants.VERIFICATION_MODULE);
			auditDetails.addAll(details);
		}

		// Sampling Document details Validation
		List<DocumentDetails> docuemnts = sampling.getDocuments();
		if (docuemnts != null && !docuemnts.isEmpty()) {
			List<AuditDetail> details = sampling.getAuditDetailMap().get("DocumentDetails");
			details = getDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from samplingDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign the
	 * to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		Sampling sampling = (Sampling) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (sampling.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Customer Income Details
		if (sampling.getCustomerIncomeList() != null && sampling.getCustomerIncomeList().size() > 0) {
			auditDetailMap.put("CustomerIncome", setCustomerIncomeAuditData(sampling, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerIncome"));
		}

		// Obligation Details
		if (sampling.getCustomerExtLiabilityList() != null && sampling.getCustomerExtLiabilityList().size() > 0) {
			auditDetailMap.put("ObligationDetails", setObligationAuditData(sampling, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ObligationDetails"));
		}

		// Collateral Extended Field Details
		if (sampling.getExtFieldRenderList() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService.setExtendedFieldsAuditData(
					new ArrayList<>(sampling.getExtFieldRenderList().values()), auditTranType, method));

			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// Sampling Document Details
		if (sampling.getDocuments() != null && sampling.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(sampling, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		sampling.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(sampling);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> setObligationAuditData(Sampling sampling, String auditTranType, String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerExtLiability custExtLiability = new CustomerExtLiability();
		String[] fields = PennantJavaUtil.getFieldDetails(custExtLiability, custExtLiability.getExcludeFields());

		for (int i = 0; i < sampling.getCustomerExtLiabilityList().size(); i++) {
			CustomerExtLiability liability = sampling.getCustomerExtLiabilityList().get(i);
			if (liability.getInputSource() == null) {
				liability.setInputSource("sampling");
			}
			if (StringUtils.isEmpty(liability.getRecordType())) {
				continue;
			}

			liability.setWorkflowId(sampling.getWorkflowId());
			if (liability.getCustId() <= 0) {
				liability.setCustId(sampling.getCustId());
			}

			boolean isRcdType = false;

			if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				liability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				liability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (sampling.isWorkflow()) {
					isRcdType = true;
				}
			} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				liability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				liability.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			liability.setRecordStatus(sampling.getRecordStatus());
			liability.setLoginDetails(sampling.getUserDetails());
			liability.setLastMntOn(sampling.getLastMntOn());

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], liability.getBefImage(), liability));
		}

		return auditDetails;
	}

	private List<AuditDetail> setCustomerIncomeAuditData(Sampling sampling, String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerIncome custIncome = new CustomerIncome();

		String[] fields = PennantJavaUtil.getFieldDetails(custIncome, custIncome.getExcludeFields());

		for (int i = 0; i < sampling.getCustomerIncomeList().size(); i++) {
			CustomerIncome customerIncome = sampling.getCustomerIncomeList().get(i);
			if (customerIncome.getInputSource() == null) {
				customerIncome.setInputSource("sampling");
			}
			customerIncome.setWorkflowId(sampling.getWorkflowId());
			if (customerIncome.getCustId() <= 0) {
				customerIncome.setCustId(sampling.getCustId());
			}

			if (StringUtils.isEmpty(customerIncome.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;

			if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (sampling.isWorkflow()) {
					isRcdType = true;
				}
			} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				customerIncome.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerIncome.setRecordStatus(sampling.getRecordStatus());
			customerIncome.setLoginDetails(sampling.getUserDetails());
			customerIncome.setLastMntOn(sampling.getLastMntOn());

			if (StringUtils.isNotEmpty(customerIncome.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerIncome.getBefImage(), customerIncome));
			}
		}

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
	public List<AuditDetail> setDocumentDetailsAuditData(Sampling sampling, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < sampling.getDocuments().size(); i++) {
			DocumentDetails documentDetails = sampling.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(sampling.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (sampling.isWorkflow()) {
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

			documentDetails.setRecordStatus(sampling.getRecordStatus());
			documentDetails.setUserDetails(sampling.getUserDetails());
			documentDetails.setLastMntOn(sampling.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * 
	 * @param type
	 *            Collateral Type
	 * @param reference
	 *            Collateral reference
	 * @param samplingId
	 *            Collateral sequence
	 * @return Return Extended collateral fields.
	 */
	@Override
	public Map<String, List<ExtendedFieldData>> getCollateralFields(String type, String linkId, String snapLinkId) {
		Map<String, List<ExtendedFieldData>> collateralFileds = new HashMap<>();

		Map<String, ExtendedFieldData> current = null;
		Map<String, ExtendedFieldData> original = null;

		String table = "verification_".concat(type.toLowerCase()).concat("_tv");
		current = getCollateralMap(table, linkId);
		
		table = "collateral_".concat(type.toLowerCase()).concat("_ed");
		original = getCollateralMap(table, snapLinkId);
		
		List<ExtendedFieldData> data = null;
		for (Entry<String, ExtendedFieldData> currentData : current.entrySet()) {
			for (Entry<String, ExtendedFieldData> originalData : original.entrySet()) {
				if (StringUtils.equals(currentData.getKey(), originalData.getKey())) {
					data = new ArrayList<>(2);
					data.add(originalData.getValue());
					data.add(currentData.getValue());
					collateralFileds.put(currentData.getKey(), data);
				}
			}
		}

		return collateralFileds;
	}

	private Map<String, ExtendedFieldData> getCollateralMap(String table, String reference) {
		return extendedFieldDetailsService.getCollateralFields(table, reference);
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerIncomeValidation getCustomerIncomeValidation() {
		if (customerIncomeValidation == null) {
			this.customerIncomeValidation = new CustomerIncomeValidation(customerIncomeDAO);
		}
		return this.customerIncomeValidation;
	}

	public CustomerExtLiabilityValidation getCustomerExtLiabilityValidation() {
		if (extLiabilityValidation == null) {
			this.extLiabilityValidation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		}
		return this.extLiabilityValidation;
	}

	public DocumentDetailValidation getDocumentValidation() {
		if (documentValidation == null) {
			this.documentValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO,
					customerDocumentDAO);
		}
		return documentValidation;
	}

	public long getCollateralLinkId(long id, String CollateralReference) {
		return samplingDAO.getCollateralLinkId(id, CollateralReference);
	}

	@Override
	public boolean isExist(String finReference, String type) {
		return samplingDAO.isExist(finReference, type);
	}

	@Override
	public void saveSnap(Sampling sampling) {
		saveIncomeSnap(sampling);
		saveLiabilitiesSnap(sampling);
		saveCollateralsSnap(sampling);
	}

	private void saveIncomeSnap(Sampling sampling) {
		List<CustomerIncome> originalList = customerIncomeDAO.getIncomesByFinReference(sampling.getKeyReference());
		List<CustomerIncome> currentList = customerIncomeDAO.getIncomesBySamplingId(sampling.getId());
						
		/**
		 * Preparing income linkid's
		 */
		Set<Long> linkIds = new HashSet<>();
		for (CustomerIncome income : originalList) {
			linkIds.add(income.getLinkId());
		}
		
		/**
		 * Deleting the incomes captured at customer/loan including co-applicant.
		 */
		for (Long linkid : linkIds) {
			try {
				incomeDetailDAO.deletebyLinkId(linkid, "");
				incomeDetailDAO.deletebyLinkId(linkid, "_temp");
			} catch (Exception e) {
				// Ignore
			}
		}
						
		/**
		 * Saving the incomes captured at sampling
		 */
		for (CustomerIncome income : currentList) {
			income.setId(0);
			income.setLinkId(0);
			customerIncomeDAO.setLinkId(income);
			income.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			income.setLastMntBy(sampling.getLastMntBy());
			incomeDetailDAO.save(income, "");
		}
		
		/**
		 * Saving the incomes captured at customer/loan as snap
		 */
		for (CustomerIncome income : originalList) {
			income.setId(0);
			income.setLinkId(0);
			income.setLinkId(samplingDAO.getIncomeSnapLinkId(sampling.getId(), income.getCustId()));
			income.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			income.setLastMntBy(sampling.getLastMntBy());
			incomeDetailDAO.save(income, "");
		}
		
		System.out.println("");
	}

	private void saveLiabilitiesSnap(Sampling sampling) {
		List<CustomerExtLiability> originalList = customerExtLiabilityDAO.getLiabilityByFinReference(sampling.getKeyReference());
		List<CustomerExtLiability> currentList = customerExtLiabilityDAO.getLiabilityBySamplingId(sampling.getId());
		
		/**
		 * Preparing Liabilities linkid's
		 */
		Set<Long> linkIds = new HashSet<>();
		for (CustomerExtLiability liability : originalList) {
			linkIds.add(liability.getLinkId());
		}
		
		/**
		 * Deleting the Liabilities captured at customer/loan including co-applicant.
		 */
		for (Long linkid : linkIds) {
			try {
				externalLiabilityDAO.deleteByLinkId(linkid, "");
				externalLiabilityDAO.deleteByLinkId(linkid, "_temp");
			} catch (Exception e) {
				// Ignore
			}
		}
						
		/**
		 * Saving the Liabilities captured at sampling
		 */
		for (CustomerExtLiability liability : currentList) {
			liability.setId(0);
			liability.setLinkId(0);
			customerExtLiabilityDAO.setLinkId(liability);
			liability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			liability.setLastMntBy(sampling.getLastMntBy());
			externalLiabilityDAO.save(liability, "");
		}
		
		/**
		 * Saving the Liabilities captured at customer/loan as snap
		 */
		for (CustomerExtLiability liability : originalList) {
			liability.setId(0);
			liability.setLinkId(0);
			liability.setLinkId(samplingDAO.getLiabilitySnapLinkId(sampling.getId(), liability.getCustId()));
			liability.setSeqNo(samplingDAO.getNextLiabilitSeq(liability.getLinkId()));
			liability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			liability.setLastMntBy(sampling.getLastMntBy());
			externalLiabilityDAO.save(liability, "");
		}
	}

	private void saveCollateralsSnap(Sampling sampling) {
		Map<String, Object> newOriginal;
		Map<String, Object> newOriginalTemp;

		Map<String, Object> original = null;
		Map<String, ExtendedFieldData> current = null;

		for (SamplingCollateral collSetup : sampling.getCollaterals()) {
			long linkId = samplingDAO.getCollateralLinkId(sampling.getId(), collSetup.getCollateralRef());
			String slinkId = "S".concat(String.valueOf(linkId));
			
			String table = "verification_".concat(collSetup.getCollateralType().toLowerCase()).concat("_tv");
			current = getCollateralMap(table, slinkId);
			
			table = "collateral_".concat(collSetup.getCollateralType().toLowerCase()).concat("_ed");
			original = extendedFieldDetailsService.getCollateralMap(table, collSetup.getCollateralRef());

			newOriginalTemp = new HashMap<>(original);
			newOriginal = new HashMap<>(original);
			for (Entry<String, Object> currField : getMap(current).entrySet()) {
				for (Entry<String, Object> orgField : original.entrySet()) {
					if (orgField.getKey().equals(currField.getKey().toLowerCase())) {
						newOriginalTemp.put(orgField.getKey(), currField.getValue());
						break;
					}
				}
			}
			String referece = collSetup.getCollateralRef();
			int seqNo = (Integer) newOriginalTemp.get("seqno");
			try {
				newOriginalTemp.remove("reference");	
				newOriginalTemp.put("lastmnton", new Timestamp(System.currentTimeMillis()));
				newOriginalTemp.put("lastmntby", sampling.getLastMntBy());
				extendedFieldRenderDAO.update(referece, (Integer) seqNo, newOriginalTemp, "", table);
				extendedFieldRenderDAO.update(referece, (Integer) seqNo, newOriginalTemp, "_temp", table);
			} catch (Exception e) {
				logger.warn(e);
			} finally {
				long sanpLinkId = samplingDAO.getCollateralSnapLinkId(sampling.getId(), referece);
				newOriginal.put("lastmnton", new Timestamp(System.currentTimeMillis()));
				newOriginal.put("lastmntby", sampling.getLastMntBy());
				newOriginal.put("reference", sanpLinkId);
				newOriginal.put("seqno", seqNo);
				extendedFieldRenderDAO.delete(String.valueOf(sanpLinkId), seqNo, "", table);
				extendedFieldRenderDAO.save(newOriginal, "", table);
			}
		}
	}

	private Map<String, Object> getMap(Map<String, ExtendedFieldData> current) {
		Map<String, Object> map = new HashMap<>();

		for (Entry<String, ExtendedFieldData> item : current.entrySet()) {
			map.put(item.getKey(), item.getValue().getFieldValue());
		}

		return map;

	}

	@Override
	public long getCollateralLinkId(String collateralRef, long id, String type) {
		return samplingDAO.getCollateralLinkId(collateralRef, id, type);
	}

	@Override
	public void saveOnReSubmit(Sampling sampling) {
		setWorkflowDetails(sampling);
		sampling.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
		sampling.setRecordType(PennantConstants.RECORD_TYPE_UPD);

		samplingDAO.save(sampling, TableType.TEMP_TAB);

		samplingDAO.saveIncomes(sampling.getId());
		samplingDAO.saveLiabilities(sampling.getId());

		samplingDAO.updateIncomes(sampling);
		samplingDAO.updateLiabilities(sampling);

		Set<String> collateral = new HashSet<>();
		String collateralType;
		for (SamplingCollateral collateralSetup : sampling.getCollaterals()) {
			collateralType = collateralSetup.getCollateralType();
			if (!collateral.contains(collateralType)) {
				samplingDAO.saveCollateral(sampling.getId(), collateralType);
				samplingDAO.updateCollaterals(sampling, collateralType);
				collateral.add(collateralType);
			}
		}
	}
}
