package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.fi.RCUStatus;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.RiskContainmentUnitService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

@Component(value = "rcuVerificationDialogCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RCUVerificationDialogCtrl extends GFCBaseCtrl<Verification> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(RCUVerificationDialogCtrl.class);

	protected Window window_RCUVerificationDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxRCUVerification;
	protected Groupbox rcuInquiry;
	protected Radiogroup rcuRadioGroup;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private Verification verification;
	private FinanceDetail financeDetail;

	private transient boolean validationOn;
	private transient boolean initType;
	private boolean recSave;

	private Map<String, Verification> customerDocuments = new LinkedHashMap<>();
	private Map<String, Verification> loanDocuments = new LinkedHashMap<>();
	private Map<String, Verification> collateralDocuments = new LinkedHashMap<>();
	private Set<String> rcuRequiredDocs = new HashSet<>();
	private Map<String, String> documentMap = new HashMap<>();
	private Map<String, String> collateralMap = new HashMap<>();
	private List<Verification> deleteVerifications = new ArrayList<>();

	@Autowired
	private SearchProcessor searchProcessor;
	@Autowired
	private transient RiskContainmentUnitService riskContainmentUnitService;
	@Autowired
	private transient VerificationService verificationService;
	@Autowired
	private transient DocumentDetailsDAO documentDetailsDAO;

	/**
	 * default constructor.<br>
	 */
	public RCUVerificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_RCUVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_RCUVerificationDialog);

		appendFinBasicDetails(arguments.get("finHeaderList"));

		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		this.verification = financeDetail.getRcuVerification();
		if (this.verification == null) {
			this.verification = new Verification();
			this.financeDetail.setRcuVerification(this.verification);
		}
		this.verification.setKeyReference(financeDetail.getFinScheduleData().getFinReference());

		financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");
		financeMainDialogCtrl.setRcuVerificationDialogCtrl(this);

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		if (arguments.get("enqiryModule") != null) {
			enqiryModule = (Boolean) arguments.get("enqiryModule");
		}

		setDocumentDetails();

		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		setScreenVerifications();

		logger.debug(Literal.LEAVING);
	}

	private void setScreenVerifications() {
		List<CustomerDocument> customerDocumentList = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<DocumentDetails> loanDocumentList = financeDetail.getDocumentDetailsList();
		List<CollateralAssignment> collaterls = financeDetail.getCollateralAssignmentList();

		if (customerDocumentList != null) {
			setCustomerDocuments(customerDocumentList);
		}

		if (loanDocumentList != null) {
			setDocumentDetails(getLoanDocuments(loanDocumentList), DocumentType.LOAN);
		}

		if (collaterls != null) {
			setDocumentDetails(getCollateralDocuments(collaterls), DocumentType.COLLATRL);
		}

		renderVerifications();
	}

	private void setCustomerDocuments(List<CustomerDocument> documents) {
		Set<String> deleteSet = new HashSet<>();

		// Prepare the Customer Documents
		for (CustomerDocument document : documents) {
			Verification object = getVerification(document, DocumentType.CUSTOMER);
			if (isNotDeleted(document.getRecordType())) {
				customerDocuments.put(document.getCustDocCategory(), object);
			} else {
				deleteSet.add(document.getCustDocCategory());
			}
		}

		// Set the old verification fields back.
		Map<String, Verification> map;
		if (initType) {
			map = getOldVerifications(DocumentType.CUSTOMER, TableType.STAGE_TAB);
		} else {
			map = getOldVerifications(DocumentType.CUSTOMER, TableType.BOTH_TAB);
		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			Verification item;
			if (deleteSet.contains(entrySet.getKey())) {
				item = entrySet.getValue();
				if (item.getRcuDocument() == null
						|| !verificationService.isVerificationInRecording(item, VerificationType.RCU)) {
					item.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					deleteVerifications.add(item);
				} else {
					item.setDocName(getDocumentName(item.getRcuDocument().getDocumentSubId()));
					customerDocuments.put(entrySet.getKey(), item);
				}
			}
		}

		for (Entry<String, Verification> entrySet : customerDocuments.entrySet()) {
			Verification previous = map.get(entrySet.getKey());
			Verification current = entrySet.getValue();
			if (previous != null) {
				setOldVerificationFields(current, previous);
			}
		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			RCUDocument rcuDocument;
			if (customerDocuments.get(entrySet.getKey()) == null) {
				rcuDocument = entrySet.getValue().getRcuDocument();

				if (rcuDocument != null
						&& verificationService.isVerificationInRecording(entrySet.getValue(), VerificationType.RCU)) {
					entrySet.getValue().setDocName(getDocumentName(rcuDocument.getDocumentSubId()));
					customerDocuments.put(entrySet.getKey(), entrySet.getValue());
				}
			}

		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			if (entrySet.getKey().startsWith(String.valueOf(DocumentType.CUSTOMER).concat("dummy$#"))) {
				RCUDocument document = entrySet.getValue().getRcuDocument();
				if (document != null) {
					entrySet.getValue().setDocName(getDocumentName(document.getDocumentSubId()));
					entrySet.getValue().setReinitid(document.getReinitid());
					if (initType) {
						verificationService.setLastStatus(entrySet.getValue());
					}
				}
				customerDocuments.put(entrySet.getKey(), entrySet.getValue());
			}
		}

	}

	private void setDocumentDetails(List<DocumentDetails> documents, DocumentType documentType) {
		Map<String, Verification> docMap;
		Set<String> deleteSet = new HashSet<>();

		if (documentType == DocumentType.LOAN) {
			docMap = loanDocuments;
		} else {
			docMap = collateralDocuments;
		}

		// Prepare the Loan/Collateral Documents
		for (DocumentDetails document : documents) {
			String reference = document.getDocCategory();
			if (isNotDeleted(document.getRecordType())) {
				Verification object = getVerification(document, documentType);
				if (documentType == DocumentType.COLLATRL) {
					reference = String.valueOf(document.getDocId()).concat(reference);
					collateralMap.put(reference, document.getReferenceId());
				}
				docMap.put(reference, object);
			} else {
				if (documentType == DocumentType.COLLATRL) {
					reference = String.valueOf(document.getDocId()).concat(reference);
				}
				deleteSet.add(reference);
			}
		}

		// Set the old verification fields back.
		Map<String, Verification> map;
		if (initType) {
			map = getOldVerifications(documentType, TableType.STAGE_TAB);
		} else {
			map = getOldVerifications(documentType, TableType.BOTH_TAB);
		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			Verification item;
			if (deleteSet.contains(entrySet.getKey())) {
				item = entrySet.getValue();
				if (item.getRcuDocument() == null
						|| !verificationService.isVerificationInRecording(item, VerificationType.RCU)) {
					item.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					deleteVerifications.add(item);
				} else {
					item.setDocName(getDocumentName(item.getRcuDocument().getDocumentSubId()));
					docMap.put(entrySet.getKey(), item);
				}
			}
		}

		for (Entry<String, Verification> entrySet : docMap.entrySet()) {
			Verification previous = map.get(entrySet.getKey());
			Verification current = entrySet.getValue();
			if (previous != null) {
				setOldVerificationFields(current, previous);
			}
		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			RCUDocument rcuDocument;
			if (docMap.get(entrySet.getKey()) == null) {
				rcuDocument = entrySet.getValue().getRcuDocument();

				if (rcuDocument != null
						&& verificationService.isVerificationInRecording(entrySet.getValue(), VerificationType.RCU)) {
					entrySet.getValue().setDocName(getDocumentName(rcuDocument.getDocumentSubId()));
					docMap.put(entrySet.getKey(), entrySet.getValue());
				}
			}

		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			if (entrySet.getKey().startsWith(String.valueOf(documentType).concat("dummy$#"))) {
				RCUDocument document = entrySet.getValue().getRcuDocument();
				if (document != null) {
					String key = String.valueOf(document.getDocumentId()).concat(document.getDocumentSubId());

					String docName = getDocumentName(document.getDocumentSubId());

					if (collateralMap.containsKey(key)) {
						docName = collateralMap.get(key).concat(" - ").concat(docName);
					}

					entrySet.getValue().setDocName(docName);

					if (initType) {
						verificationService.setLastStatus(entrySet.getValue());
					}
				}
				docMap.put(entrySet.getKey(), entrySet.getValue());
			}
		}

	}

	private boolean isRCUExists(long verificationId) {
		Search search = new Search(RiskContainmentUnit.class);
		search.addField("verificationId");
		search.addTabelName("verification_rcu_view");
		search.addFilter(new Filter("verificationId", verificationId));
		List<RiskContainmentUnit> list = searchProcessor.getResults(search);

		return !list.isEmpty();
	}

	private boolean isAgencyChanged(Verification verification) {
		Search search = new Search(Verification.class);
		search.addTabelName("verifications");
		search.addFilter(new Filter("id", verification.getId()));
		if (verification.getAgency() != null) {
			search.addFilter(new Filter("agency", verification.getAgency()));
		} else {
			search.addWhereClause("agency is null");
		}
		int count = searchProcessor.getCount(search);

		return count > 0 ? false : true;
	}

	private void setOldVerificationFields(Verification current, Verification previous) {
		current.setId(previous.getId());
		current.setRequestType(previous.getRequestType());
		current.setAgency(previous.getAgency());
		current.setAgencyName(previous.getAgencyName());
		current.setReason(previous.getReason());
		current.setReasonName(previous.getReasonName());
		current.setRemarks(previous.getRemarks());
		current.setDecision(previous.getDecision());
		current.setDecisionRemarks(previous.getDecisionRemarks());
		current.setReference(previous.getReference());
		current.setStatus(previous.getStatus());
		current.setVerificationDate(previous.getVerificationDate());
		current.setKeyReference(previous.getKeyReference());
		current.setCustId(previous.getCustId());
		current.setNewRecord(false);

		RCUDocument previousDoc = previous.getRcuDocument();
		RCUDocument currentDoc = current.getRcuDocument();

		if (previousDoc != null) {
			currentDoc.setVerificationId(previousDoc.getVerificationId());
			currentDoc.setSeqNo(previousDoc.getSeqNo());
			currentDoc.setDocumentId(previousDoc.getDocumentId());
			currentDoc.setDocumentSubId(previousDoc.getDocumentSubId());
			currentDoc.setReinitid(previousDoc.getReinitid());
			currentDoc.setDocType(previousDoc.getDocType());
			currentDoc.setDocumentType(previousDoc.getDocumentType());
		}

		if (initType) {
			verificationService.setLastStatus(current);
		}

	}

	private boolean isNotDeleted(String recordType) {
		return !(PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| PennantConstants.RECORD_TYPE_CAN.equals(recordType));
	}

	private Verification getVerification(CustomerDocument document, DocumentType documentType) {
		RCUDocument rcuDocument = new RCUDocument();
		Verification item = new Verification();

		item.setDocName(getDocumentName(document.getCustDocCategory()));
		item.setReferenceFor(document.getCustDocCategory());

		item.setNewRecord(true);
		item.setReferenceType(documentType.getValue());
		item.setDocType(documentType.getKey());
		item.setVerificationType(VerificationType.RCU.getKey());
		item.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		item.setKeyReference(this.verification.getKeyReference());
		item.setCustId(document.getCustID());

		//set RCU Required
		if (rcuRequiredDocs.contains(document.getCustDocCategory())) {
			item.setRequestType(RequestType.INITIATE.getKey());
		} else {
			item.setRequestType(RequestType.NOT_REQUIRED.getKey());
		}

		rcuDocument.setDocCategory(document.getCustDocCategory());
		rcuDocument.setDocumentId(document.getCustID());
		rcuDocument.setDocumentSubId(document.getCustDocCategory());
		rcuDocument.setDocumentType(documentType.getKey());
		item.setRcuDocument(rcuDocument);

		if (initType) {
			verificationService.setLastStatus(item);
		}

		return item;
	}

	private Verification getVerification(DocumentDetails document, DocumentType documentType) {
		RCUDocument rcuDocument = new RCUDocument();
		Verification item = new Verification();

		item.setNewRecord(true);
		item.setDocName(getDocumentName(document.getDocCategory()));
		item.setReferenceType(documentType.getValue());
		item.setReference(String.valueOf(document.getDocId()));
		item.setDocType(documentType.getKey());
		item.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		item.setReferenceFor(document.getDocCategory());
		item.setKeyReference(this.verification.getKeyReference());
		item.setVerificationType(VerificationType.RCU.getKey());

		// set RCU Required
		if (rcuRequiredDocs.contains(document.getDocCategory())) {
			item.setRequestType(RequestType.INITIATE.getKey());
		} else {
			item.setRequestType(RequestType.NOT_REQUIRED.getKey());
		}

		rcuDocument.setDocCategory(document.getDocCategory());
		rcuDocument.setDocumentId(document.getDocId());
		rcuDocument.setDocumentSubId(document.getDocCategory());
		rcuDocument.setDocumentType(documentType.getKey());
		rcuDocument.setCollateralRef(document.getReferenceId());
		item.setRcuDocument(rcuDocument);

		if (initType) {
			verificationService.setLastStatus(item);
		}

		return item;
	}

	private String getDocumentName(String code) {
		StringBuilder builder = new StringBuilder();

		builder.append(code);
		if (documentMap.containsKey(code)) {
			builder.append(" - ");
			builder.append(documentMap.get(code));
		}

		return builder.toString();
	}

	private Map<String, Verification> getOldVerifications(DocumentType documentType, TableType tableType) {
		Map<String, Verification> verificationMap = new HashMap<>();
		List<Verification> list = new ArrayList<>();
		List<Verification> verifications;
		List<RCUDocument> oldDocuments;

		verifications = verificationService.getVerifications(this.verification.getKeyReference(),
				VerificationType.RCU.getKey());
		oldDocuments = riskContainmentUnitService.getDocuments(verification.getKeyReference(), tableType, documentType);

		for (Verification item : verifications) {
			if (item.getRequestType() != RequestType.INITIATE.getKey()) {
				list.add(item);
			}
		}

		for (RCUDocument document : oldDocuments) {
			for (Verification ver : verifications) {
				if (ver.getRequestType() == RequestType.INITIATE.getKey()
						&& ver.getId() == document.getVerificationId()) {
					Verification item = new Verification();
					BeanUtils.copyProperties(ver, item);
					item.setRemarks(document.getInitRemarks());
					item.setDecision((document.getDecision()));
					item.setDecisionRemarks((document.getDecisionRemarks()));
					item.setRcuDocument(document);
					item.setReinitid(document.getReinitid());
					list.add(item);
				}
			}
		}

		int i = 0;
		for (Verification ver : list) {
			if (ver.getReinitid() != null && ver.getReinitid() != 0L) {
				verificationMap.put(String.valueOf(documentType).concat("dummy$#").concat(String.valueOf(++i)), ver);
				continue;
			}

			String reference = ver.getReferenceFor();
			if (ver.getRcuDocument() != null) {
				reference = ver.getRcuDocument().getDocCategory();
				if (documentType == DocumentType.COLLATRL) {
					reference = String.valueOf(ver.getRcuDocument().getDocumentId()).concat(reference);
				}
			} else {
				if (documentType == DocumentType.COLLATRL) {
					reference = ver.getReferenceFor();
					reference = ver.getReference().concat(ver.getReferenceFor());
				}
			}

			verificationMap.put(reference, ver);
		}

		return verificationMap;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final HashMap<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private org.zkoss.zk.ui.Component getComponent(Listitem listitem, String listcellId) {
		List<Listcell> listcels = listitem.getChildren();

		for (Listcell listcell : listcels) {
			String id = StringUtils.trimToNull(listcell.getId());

			if (id == null) {
				continue;
			}
			id = id.replaceAll("\\d", "");
			if (StringUtils.equals(id, listcellId)) {
				return listcell.getFirstChild();
			}
		}
		return null;
	}

	public void onChnageTv(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();

		Combobox cfiv = (Combobox) getComponent(listitem, "RequestType");
		ExtendedCombobox cAgency = (ExtendedCombobox) getComponent(listitem, "Agency");
		ExtendedCombobox cReason = (ExtendedCombobox) getComponent(listitem, "Reason");

		onchangeVerificationType(cfiv, cAgency, cReason);
	}

	private void onchangeVerificationType(Combobox cfiv, ExtendedCombobox cAgency, ExtendedCombobox cReason) {
		RequestType type;
		type = RequestType.getType(Integer.parseInt(cfiv.getSelectedItem().getValue()));

		switch (type) {
		case INITIATE:
			cAgency.setReadonly(false);
			cReason.setValue("");
			cReason.setReadonly(true);
			break;
		case WAIVE:
			cAgency.setValue("");
			cAgency.setReadonly(true);
			cReason.setReadonly(false);
			break;
		default:
			cAgency.setValue("");
			cReason.setValue("");
			cAgency.setReadonly(true);
			cReason.setReadonly(true);
			break;
		}
	}

	public void onChangeAgency(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox agency = (ExtendedCombobox) getComponent(listitem, "Agency");
		Object dataObject = agency.getObject();

		if (dataObject instanceof String) {
			agency.setValue(dataObject.toString());
			agency.setDescription("");
		} else {
			VehicleDealer vehicleDealer = (VehicleDealer) dataObject;
			if (vehicleDealer != null) {
				agency.setAttribute("agencyId", vehicleDealer.getId());
			}
		}
	}

	public void onChangeReason(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox reason = (ExtendedCombobox) getComponent(listitem, "Reason");
		Object dataObject = reason.getObject();

		if (dataObject instanceof String) {
			reason.setValue(dataObject.toString());
			reason.setDescription("");
		} else {
			ReasonCode reasonCode = (ReasonCode) dataObject;
			if (reasonCode != null) {
				reason.setAttribute("reasonId", reasonCode.getId());
			}
		}
	}

	public void onChangeReInitAgency(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox agency = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
		Object dataObject = agency.getObject();

		if (dataObject instanceof String) {
			agency.setValue(dataObject.toString());
			agency.setDescription("");
		} else {
			VehicleDealer vehicleDealer = (VehicleDealer) dataObject;
			if (vehicleDealer != null) {
				agency.setAttribute("reInitAgencyId", vehicleDealer.getId());
			}
		}
	}

	public void onChangeDecision(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox reInitAgency = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
		Combobox decisionBox = (Combobox) getComponent(listitem, "Decision");
		Decision decision;
		try {
			decision = Decision.getType(Integer.parseInt(decisionBox.getSelectedItem().getValue()));
		} catch (Exception e) {
			String value = decisionBox.getValue();
			decisionBox.setValue("");
			throw new WrongValueException(decisionBox,
					Labels.getLabel("STATIC_INVALID", new String[] { value + " is not valid," }));
		}

		if (decision.getKey() == Decision.RE_INITIATE.getKey()) {
			reInitAgency.setReadonly(false);
		} else {
			reInitAgency.setValue("");
			reInitAgency.setReadonly(true);
		}
	}

	public void onCheck$rcuRadioGroup(Event event) {
		final HashMap<String, Object> map = new HashMap<>();
		RiskContainmentUnit riskContainmentUnit = riskContainmentUnitService
				.getRiskContainmentUnit(Long.parseLong(rcuRadioGroup.getSelectedItem().getValue().toString()));
		if (rcuInquiry.getChildren().size() >= 2) {
			rcuInquiry.getChildren().remove(1);
		}
		if (riskContainmentUnit != null) {
			riskContainmentUnit = riskContainmentUnitService.getRiskContainmentUnit(riskContainmentUnit, "_View");
		}
		if (riskContainmentUnit != null && StringUtils.isEmpty(riskContainmentUnit.getNextRoleCode())) {
			map.put("LOAN_ORG", true);
			map.put("riskContainmentUnit", riskContainmentUnit);
			Executions.createComponents("/WEB-INF/pages/Verification/RiskContainmentUnit/RiskContainmentUnitDialog.zul",
					rcuInquiry, map);
		} else if (riskContainmentUnit != null) {
			MessageUtil.showMessage("Verification is not yet completed.");
		} else {
			MessageUtil.showMessage("Initiation request not available.");
		}
	}

	public void addCustomerDocuments(List<CustomerDocument> documents) {
		customerDocuments.clear();
		if (documents != null) {
			setCustomerDocuments(documents);
		}
		renderVerifications();
	}

	public void addLoanDocuments(List<DocumentDetails> documents) {
		loanDocuments.clear();
		if (documents != null) {
			setDocumentDetails(getLoanDocuments(documents), DocumentType.LOAN);
		}
		renderVerifications();
	}

	public void addCollateralDocuments(List<CollateralAssignment> collaterals) {
		collateralDocuments.clear();
		if (collaterals != null) {
			setDocumentDetails(getCollateralDocuments(collaterals), DocumentType.COLLATRL);
		}

		renderVerifications();
	}

	private List<DocumentDetails> getLoanDocuments(List<DocumentDetails> documents) {
		List<DocumentDetails> loanDocs = new ArrayList<>();
		for (DocumentDetails document : documents) {
			if (!DocumentCategories.CUSTOMER.getKey().equals(document.getCategoryCode())) {
				loanDocs.add(document);
			}
		}
		return loanDocs;
	}

	private List<DocumentDetails> getCollateralDocuments(List<CollateralAssignment> collaterals) {
		List<DocumentDetails> documents = new ArrayList<>();

		for (CollateralAssignment collateral : collaterals) {
			List<DocumentDetails> list = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
					CollateralConstants.MODULE_NAME, "", "_View");

			if (list != null) {
				for (DocumentDetails documentDetails : list) {
					if (!isNotDeleted(collateral.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					}
				}
				documents.addAll(list);
			}
		}

		return documents;
	}

	/**
	 * Method to fill TV Initiation/Approval tab.
	 * 
	 * @param customer
	 */
	public void renderVerifications() {
		logger.debug(Literal.ENTERING);
		Listgroup group;
		Listcell cell;
		List<Integer> docTypes = DocumentType.getKeys();

		List<Verification> verifications = new ArrayList<>();

		verifications.addAll(customerDocuments.values());
		verifications.addAll(loanDocuments.values());
		verifications.addAll(collateralDocuments.values());

		this.verification.getVerifications().clear();
		this.verification.getVerifications().addAll(verifications);

		if (listBoxRCUVerification.getItems() != null) {
			listBoxRCUVerification.getItems().clear();
		}
		//set Initiated flag to initiated Records
		setInitiated(verification.getVerifications());

		//Render Verifications
		int i = 0;
		DocumentType documentType;
		for (Verification vrf : verifications) {
			if (!initType && vrf.getId() == 0) {
				continue;
			}

			if (docTypes.contains(vrf.getDocType())) {
				documentType = DocumentType.getType(vrf.getDocType());
				// Creating list group for DocType.
				group = new Listgroup();
				cell = new Listcell(documentType.getValue());
				cell.setStyle("font-weight:bold;");
				group.appendChild(cell);
				group.setAttribute("empty", "empty");
				listBoxRCUVerification.appendChild(group);
				docTypes.remove((Object) vrf.getDocType());
			}

			i++;
			Listitem item = new Listitem();
			Listcell listCell;
			if (!initType) {
				//Select
				listCell = new Listcell();
				listCell.setId("select".concat(String.valueOf(i)));
				Radio select = new Radio();
				select.setRadiogroup(rcuRadioGroup);
				select.setValue(vrf.getId());
				listCell.appendChild(select);
				listCell.setParent(item);
			}

			//Document Name
			listCell = new Listcell();
			listCell.setId("ReferenceFor".concat(String.valueOf(i)));
			String docName = vrf.getDocName();

			if (vrf.getDocType() == DocumentType.COLLATRL.getKey()) {
				String key = StringUtils.trimToEmpty(vrf.getReference()).concat(vrf.getReferenceFor());

				RCUDocument rcuDocument = vrf.getRcuDocument();
				if (rcuDocument != null
						&& (rcuDocument.getDocumentId() != null && rcuDocument.getDocumentSubId() != null)) {
					key = StringUtils.trimToEmpty(String.valueOf(rcuDocument.getDocumentId()))
							.concat(rcuDocument.getDocumentSubId());
				}

				if (collateralMap.containsKey(key)) {
					docName = collateralMap.get(key).concat(" - ").concat(docName);
				}
			}

			listCell.appendChild(new Label(docName));
			listCell.setParent(item);

			//RCU
			listCell = new Listcell();
			listCell.setId("RequestType".concat(String.valueOf(i)));
			Combobox requestType = new Combobox();
			requestType.setReadonly(true);
			requestType.setValue(String.valueOf(vrf.getRequestType()));

			List<ValueLabel> list = new ArrayList<>();
			int reqType = vrf.getRequestType();
			if (reqType == RequestType.NOT_REQUIRED.getKey() && rcuRequiredDocs.contains(vrf.getReferenceFor())) {
				list = RequestType.getList();
			} else if (reqType == RequestType.WAIVE.getKey() && !rcuRequiredDocs.contains(vrf.getReferenceFor())) {
				list = RequestType.getList();
			} else if (rcuRequiredDocs.contains(vrf.getReferenceFor())) {
				for (ValueLabel valueLabel : RequestType.getList()) {
					if (Integer.parseInt(valueLabel.getValue()) != RequestType.NOT_REQUIRED.getKey()) {
						list.add(valueLabel);
					}
				}
			} else {
				for (ValueLabel valueLabel : RequestType.getList()) {
					if (Integer.parseInt(valueLabel.getValue()) != RequestType.WAIVE.getKey()) {
						list.add(valueLabel);
					}
				}
			}

			fillComboBox(requestType, reqType, list);

			requestType.setParent(listCell);
			listCell.setParent(item);

			//Agency
			listCell = new Listcell();
			listCell.setId("Agency".concat(String.valueOf(i)));
			ExtendedCombobox agency = new ExtendedCombobox();
			if (vrf.getAgencyName() != null) {
				agency.setValue(String.valueOf(vrf.getAgencyName()));
				agency.setAttribute("agencyId", vrf.getAgency());
				agency.setAttribute("oldAgencyId", vrf.getAgency());
				agency.setAttribute("agencyName", vrf.getAgencyName());
			}
			fillAgencies(agency);
			listCell.appendChild(agency);
			listCell.setParent(item);

			//Reason
			listCell = new Listcell();
			listCell.setId("Reason".concat(String.valueOf(i)));
			ExtendedCombobox reason = new ExtendedCombobox();
			if (vrf.getReasonName() != null) {
				reason.setValue(vrf.getReasonName());
				reason.setAttribute("reasonId", vrf.getReason());
			}
			fillReasons(reason);
			listCell.appendChild(reason);
			listCell.setParent(item);

			//Remarks
			listCell = new Listcell();
			listCell.setId("Remarks".concat(String.valueOf(i)));
			Textbox remarks = new Textbox(vrf.getRemarks());
			remarks.setMaxlength(500);
			listCell.appendChild(remarks);
			listCell.setParent(item);

			if (initType) {
				// Last Verification Agency
				listCell = new Listcell();
				listCell.appendChild(new Label(vrf.getLastAgency()));
				listCell.setParent(item);
			}

			//Status
			listCell = new Listcell();
			Label status = new Label();

			if (initType && vrf.getLastStatus() != 0) {
				status.setValue(RCUStatus.getType(vrf.getLastStatus()).getValue());

			} else if (!initType && vrf.getStatus() != 0) {
				status.setValue(RCUStatus.getType(vrf.getStatus()).getValue());
			}

			listCell.appendChild(status);
			listCell.setParent(item);

			// Verification Date
			listCell = new Listcell();
			if (initType) {
				listCell.appendChild(new Label(DateUtil.formatToShortDate(vrf.getLastVerificationDate())));
			} else {
				listCell.appendChild(new Label(DateUtil.formatToShortDate(vrf.getVerificationDate())));
			}
			listCell.setParent(item);

			if (!initType) {
				//Decision
				listCell = new Listcell();
				listCell.setId("Decision".concat(String.valueOf(i)));
				Combobox decision = new Combobox();
				decision.setReadonly(true);
				if (Decision.getType(vrf.getDecision()) != null) {
					decision.setValue(String.valueOf(Decision.getType(vrf.getDecision()).getValue()));
				}

				fillDecision(vrf, decision);

				decision.setParent(listCell);
				listCell.setParent(item);

				//Re-Initiation Agency
				listCell = new Listcell();
				listCell.setId("ReInitAgency".concat(String.valueOf(i)));
				ExtendedCombobox reInitAgency = new ExtendedCombobox();
				reInitAgency.setReadonly(true);
				fillAgencies(reInitAgency);
				listCell.appendChild(reInitAgency);
				listCell.setParent(item);

				//Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("ReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				reInitRemarks.setMaxlength(500);
				reInitRemarks.setValue(vrf.getDecisionRemarks());
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				decision.addForward("onChange", self, "onChangeDecision", item);
				reInitAgency.addForward("onFulfill", self, "onChangeReInitAgency", item);

				if (vrf.getReinitid() == null || vrf.getReinitid() == 0L) {

				} else {
					decision.setValue(Decision.RE_INITIATE.getValue());
					decision.setDisabled(true);
					reInitRemarks.setReadonly(true);
				}

				if (enqiryModule) {
					decision.setDisabled(true);
					reInitAgency.setReadonly(true);
					reInitRemarks.setReadonly(true);
				}
			}

			item.setAttribute("requestType", vrf.getRequestType());

			requestType.addForward("onChange", self, "onChnageTv", item);
			agency.addForward("onFulfill", self, "onChangeAgency", item);
			reason.addForward("onFulfill", self, "onChangeReason", item);

			onchangeVerificationType(requestType, agency, reason);

			item.setAttribute("vrf", vrf);
			this.listBoxRCUVerification.appendChild(item);

			if (!initType || vrf.isInitiated()) {
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}

			if (enqiryModule) {
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}

		}

		// Creating empty list groups.
		for (

		int docType : docTypes) {
			group = new Listgroup();
			cell = new Listcell(DocumentType.getType(docType).getValue());
			cell.setStyle("font-weight:bold;");
			group.appendChild(cell);
			group.setAttribute("empty", "empty");
			listBoxRCUVerification.appendChild(group);
			Listitem item = new Listitem();
			cell = new Listcell("No Records Available");
			cell.setSpan(20);
			cell.setStyle("text-align:center;");
			item.appendChild(cell);
			item.setAttribute("empty", "empty");
			listBoxRCUVerification.appendChild(item);
		}

		logger.debug(Literal.LEAVING);
	}

	private void setInitiated(List<Verification> verifications) {
		for (Verification item : verifications) {
			RCUDocument rcuDocument = item.getRcuDocument();
			if (item.getRequestType() == RequestType.INITIATE.getKey() && (rcuDocument.getDocumentId() != null)
					&& verificationService.isVerificationInRecording(item, VerificationType.RCU)) {
				item.setInitiated(true);
			}
		}
	}

	private void fillDecision(Verification vrf, Combobox decision) {
		List<ValueLabel> decisionList = new ArrayList<>();
		if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()
				|| vrf.getStatus() == RCUStatus.POSITIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			decisionList.add(new ValueLabel(String.valueOf(Decision.SELECT.getKey()), Decision.SELECT.getValue()));
			if (vrf.getDecision() == Decision.SELECT.getKey()) {
				vrf.setDecision(Decision.APPROVE.getKey());
			}
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else if (vrf.getStatus() == RCUStatus.NEGATIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.APPROVE.getKey()), Decision.APPROVE.getValue()));
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else if (vrf.getRequestType() == RequestType.WAIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else {
			fillComboBox(decision, vrf.getDecision(), Decision.getList());
		}
	}

	private void setDocumentDetails() {
		this.rcuRequiredDocs.clear();
		this.documentMap.clear();

		Search search = new Search(com.pennant.backend.model.systemmasters.DocumentType.class);
		search.addField("doctypecode");
		search.addField("docTypeDesc");
		search.addField("rcureq");
		search.addTabelName("BMTDocumentTypes");
		List<com.pennant.backend.model.systemmasters.DocumentType> list = searchProcessor.getResults(search);

		for (com.pennant.backend.model.systemmasters.DocumentType documentType : list) {
			if (documentType.isRcuReq()) {
				this.rcuRequiredDocs.add(documentType.getDocTypeCode());
			}

			this.documentMap.put(documentType.getDocTypeCode(), documentType.getDocTypeDesc());
		}
	}

	private List<ValueLabel> filterDecisions(List<ValueLabel> list) {
		boolean flag = true;
		List<ValueLabel> decisionList = new ArrayList<>();
		for (ValueLabel decValueLabel : Decision.getList()) {
			for (ValueLabel valueLabel : list) {
				if (Integer.parseInt(valueLabel.getValue()) == Integer.parseInt(decValueLabel.getValue())) {
					flag = false;
				}
			}
			if (flag) {
				decisionList.add(decValueLabel);
			}
			flag = true;
		}
		return decisionList;
	}

	private void fillAgencies(ExtendedCombobox agency) {
		logger.debug(Literal.ENTERING);

		agency.setModuleName("VerificationAgencies");
		agency.setValueColumn("DealerName");
		agency.setValidateColumns(new String[] { "DealerName" });
		Filter[] agencyFilter = new Filter[1];
		agencyFilter[0] = new Filter("DealerType", Agencies.RCUVAGENCY.getKey(), Filter.OP_EQUAL);
		agency.setFilters(agencyFilter);

		logger.debug(Literal.LEAVING);
	}

	private void fillReasons(ExtendedCombobox reason) {
		logger.debug(Literal.ENTERING);

		reason.setModuleName("VerificationWaiverReason");
		reason.setValueColumn("Code");
		reason.setValidateColumns(new String[] { "Code" });
		Filter[] reasonFilter = new Filter[1];
		reasonFilter[0] = new Filter("ReasonTypecode", WaiverReasons.RCUWRES.getKey(), Filter.OP_EQUAL);
		reason.setFilters(reasonFilter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		for (Listitem listitem : listBoxRCUVerification.getItems()) {
			if (listitem.getAttribute("empty") != null && listitem.getAttribute("empty").equals("empty")) {
				continue;
			}
			Combobox rcuComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "Decision");
			ExtendedCombobox reInitagencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
			Textbox reInitRemarks = (Textbox) getComponent(listitem, "ReInitRemarks");

			rcuComboBox.clearErrorMessage();
			agencyComboBox.clearErrorMessage();
			reasonComboBox.clearErrorMessage();

			if (decision != null) {
				decision.clearErrorMessage();
			}
			if (reInitagencyComboBox != null) {
				reInitagencyComboBox.clearErrorMessage();
			}
			if (reInitRemarks != null) {
				reInitRemarks.clearErrorMessage();
			}
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);

		for (Listitem listitem : listBoxRCUVerification.getItems()) {
			if (listitem.getAttribute("empty") != null && listitem.getAttribute("empty").equals("empty")) {
				continue;
			}
			Combobox rcuComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			ExtendedCombobox reInitAgencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");

			if (!rcuComboBox.isDisabled()) {
				rcuComboBox.setConstraint(
						new PTStringValidator(Labels.getLabel("label_RCUVerificationDialog_RCU.value"), null, true));
			}

			if (!agencyComboBox.isReadonly()) {
				agencyComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_RCUVerificationDialog_Agency.value"), null, true, true));
			}

			if (!reasonComboBox.isReadonly()) {
				reasonComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_RCUVerificationDialog_Reason.value"), null, true, true));
			}

			if (!initType && !reInitAgencyComboBox.isReadonly()) {
				reInitAgencyComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_RCUVerificationDialog_Agency.value"), null, true, true));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void setValue(Listitem listitem, String comonentId) {

		Verification verification = (Verification) listitem.getAttribute("vrf");
		verification.setOldRequestType(Integer.parseInt(listitem.getAttribute("requestType").toString()));

		switch (comonentId) {
		case "RequestType":
			String requestType = ((Combobox) getComponent(listitem, "RequestType")).getSelectedItem().getValue();
			verification.setRequestType(Integer.parseInt(requestType));
			break;
		case "Agency":
			ExtendedCombobox agency = ((ExtendedCombobox) getComponent(listitem, "Agency"));
			if (StringUtils.isNotEmpty(agency.getValue())) {
				verification.setAgency(Long.parseLong(agency.getAttribute("agencyId").toString()));
			} else {
				verification.setAgency(null);
			}
			break;
		case "Reason":
			ExtendedCombobox reason = ((ExtendedCombobox) getComponent(listitem, "Reason"));
			if (StringUtils.isNotEmpty(reason.getValue())) {
				verification.setReason(Long.parseLong(reason.getAttribute("reasonId").toString()));
			} else {
				verification.setReason(null);
			}
			break;
		case "Remarks":
			verification.setRemarks(((Textbox) getComponent(listitem, "Remarks")).getValue());
			break;
		case "Decision":
			Combobox combobox = (Combobox) getComponent(listitem, "Decision");
			int decision = Integer.parseInt(getComboboxValue(combobox).toString());
			if (combobox.isDisabled()) {
				verification.setIgnoreFlag(true);
			}
			verification.setDecision(decision);
			if (!combobox.isDisabled() && decision == 0 && !this.recSave) {
				throw new WrongValueException(combobox,
						Labels.getLabel("STATIC_INVALID", new String[] { "Decision should be mandatory" }));
			} else if (verification.getOldRequestType() == RequestType.INITIATE.getKey()
					&& decision == Decision.RE_INITIATE.getKey()) {
				verification.getRcuDocument().setReInitiated(true);
			}
			break;
		case "ReInitAgency":
			ExtendedCombobox reInitAgency = ((ExtendedCombobox) getComponent(listitem, "ReInitAgency"));
			if (StringUtils.isNotEmpty(reInitAgency.getValue())) {
				verification.setReInitAgency(Long.parseLong(reInitAgency.getAttribute("reInitAgencyId").toString()));
			} else {
				verification.setReInitAgency(null);
			}
			break;
		case "ReInitRemarks":
			Textbox textbox = (Textbox) getComponent(listitem, "ReInitRemarks");
			verification.setDecisionRemarks(textbox.getValue());
			if (verification.getDecision() == Decision.OVERRIDE.getKey()
					&& StringUtils.isEmpty(verification.getDecisionRemarks())) {
				throw new WrongValueException(textbox, "Remarks are mandatory when Decision is Override");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @return
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		for (Listitem listitem : listBoxRCUVerification.getItems()) {
			if (listitem.getAttribute("empty") != null && listitem.getAttribute("empty").equals("empty")) {
				continue;
			}
			try {
				setValue(listitem, "RequestType");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				setValue(listitem, "Agency");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "Reason");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			setValue(listitem, "Remarks");

			if (!initType) {
				try {
					setValue(listitem, "Decision");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					setValue(listitem, "ReInitAgency");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					setValue(listitem, "ReInitRemarks");
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		}

		doRemoveValidation();

		logger.debug(Literal.LEAVING);
		return wve;
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		for (Listitem listitem : listBoxRCUVerification.getItems()) {
			if (listitem.getAttribute("empty") != null && listitem.getAttribute("empty").equals("empty")) {
				continue;
			}
			Combobox fivComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "Decision");
			ExtendedCombobox reInitagencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
			Textbox reInitRemarks = (Textbox) getComponent(listitem, "ReInitRemarks");

			fivComboBox.setConstraint("");
			agencyComboBox.setConstraint("");
			reasonComboBox.setConstraint("");

			if (decision != null) {
				decision.setConstraint("");
			}
			if (reInitagencyComboBox != null) {
				reInitagencyComboBox.setConstraint("");
			}
			if (reInitRemarks != null) {
				reInitRemarks.setConstraint("");
			}

		}

		logger.debug(Literal.LEAVING);

	}

	private void fillComboBox(Combobox combobox, int value, List<ValueLabel> list) {
		combobox.getChildren().clear();
		for (ValueLabel valueLabel : list) {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (Integer.parseInt(valueLabel.getValue()) == value) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (!wve.isEmpty()) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (tab != null) {
				tab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	public boolean doSave(FinanceDetail financeDetail, Tab tab, boolean recSave, Radiogroup userAction) {
		logger.debug(Literal.ENTERING);
		this.userAction = userAction;
		this.recSave = recSave;
		doClearMessage();
		doSetValidation();

		ArrayList<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);
		List<Verification> vrfs = getVerifications();
		verification.getVerifications().clear();
		verification.setVerifications(vrfs);
		this.verification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		this.verification.getVerifications().addAll(deleteVerifications);
		financeDetail.setRcuVerification(this.verification);

		logger.debug(Literal.LEAVING);
		if (tab.getId().equals("TAB".concat(AssetConstants.UNIQUE_ID_RCUAPPROVAL))) {
			return validateReinitiation(financeDetail.getRcuVerification().getVerifications());
		}
		return true;
	}

	private boolean validateReinitiation(List<Verification> verifications) {
		for (Verification verification : verifications) {
			if (verification.getDecision() == Decision.RE_INITIATE.getKey()
					&& !userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_SAVED)
					&& verification.getReinitid() == null) {
				MessageUtil.showError("RCU Verification Re-Initiation is allowed only when user action is save");
				return false;
			}
		}
		return true;
	}

	private List<Verification> getVerifications() {
		Map<Long, Verification> reInitMap = new HashMap<>();
		Map<Long, Verification> other = new HashMap<>();
		List<Verification> verifications = new ArrayList<>();
		List<Verification> newverifications = new ArrayList<>();
		Verification aVerification = null;

		for (Verification vrf : this.verification.getVerifications()) {
			if (vrf.getRequestType() != RequestType.INITIATE.getKey()) {
				verifications.add(vrf);
			}
			if ((vrf.getAgency() != null && !reInitMap.containsKey(vrf.getAgency()))
					|| (vrf.getReInitAgency() != null && !reInitMap.containsKey(vrf.getReInitAgency()))
							&& !vrf.isIgnoreFlag()) {
				if (vrf.getDecision() == Decision.RE_INITIATE.getKey() && !initType) {
					reInitMap.put(vrf.getReInitAgency(), vrf);
				} else if (!vrf.isInitiated() || !initType) {
					other.put(vrf.getAgency(), vrf);
				}
			}
		}

		if (initType) {
			for (Verification verification : this.verification.getVerifications()) {
				if (!verification.isNewRecord() && isAgencyChanged(verification)) {
					verification.setNewRecord(true);
					if (verification.getRcuDocument() == null
							|| !verificationService.isVerificationInRecording(verification, VerificationType.RCU)) {
						Verification vrf = new Verification();
						BeanUtils.copyProperties(verification, vrf);
						if (verification.getRcuDocument() != null) {
							RCUDocument rcuDoc = new RCUDocument();
							BeanUtils.copyProperties(verification.getRcuDocument(), rcuDoc);
							vrf.setRcuDocument(rcuDoc);
						}
						vrf.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						deleteVerifications.add(vrf);
					}
				}
			}

			for (Verification verification : this.verification.getVerifications()) {
				if (verification.isNewRecord()) {
					newverifications.add(verification);
				}
			}

			for (Verification newVrf : newverifications) {
				for (Verification vrf : this.verification.getVerifications()) {
					if (!vrf.isInitiated() && !vrf.isNewRecord() && newVrf.getAgency() != null
							&& newVrf.getAgency() == vrf.getAgency()) {
						if (!isRCUExists(vrf.getId())) {
							newVrf.setNewRecord(false);
							newVrf.setId(vrf.getId());
							break;
						}
					}
				}
			}

		}

		for (Verification vrf : this.verification.getVerifications()) {
			RCUDocument document = vrf.getRcuDocument();
			document.setInitRemarks(vrf.getRemarks());
			document.setDecisionRemarks(vrf.getDecisionRemarks());
			document.setDecision(vrf.getDecision());
			if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
				if (!initType && vrf.getDecision() == Decision.RE_INITIATE.getKey() && !vrf.isIgnoreFlag()) {
					aVerification = reInitMap.get(vrf.getReInitAgency());
					document.setInitRemarks(vrf.getDecisionRemarks());
					aVerification.getRcuDocuments().add(document);
				} else if (!vrf.isInitiated() || !initType) {
					aVerification = other.get(vrf.getAgency());
					if (aVerification != null) {
						aVerification.getRcuDocuments().add(document);
					}
				}
			} else if (!initType && vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
				aVerification = reInitMap.get(vrf.getReInitAgency());
				document.setInitRemarks(vrf.getDecisionRemarks());
				aVerification.getRcuDocuments().add(document);
			}
		}

		verifications.addAll(reInitMap.values());
		verifications.addAll(other.values());

		return verifications;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Verification getVerification() {
		return verification;
	}

	public void setVerification(Verification verification) {
		this.verification = verification;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
}
