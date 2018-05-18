package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.Status;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
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

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private Verification verification;
	private FinanceDetail financeDetail;

	private transient boolean validationOn;
	private transient boolean initType;
	protected Radiogroup rcuRadioGroup;

	private Map<String, Verification> customerDocuments = new LinkedHashMap<>();
	private Map<String, Verification> loanDocuments = new LinkedHashMap<>();
	private Map<String, Verification> collateralDocuments = new LinkedHashMap<>();
	private List<String> rcurequiredDocs;

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

		financeDetail = (FinanceDetail) arguments.get("financeDetail");

		verification = financeDetail.getRcuVerification();

		financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");
		financeMainDialogCtrl.setRcuVerificationDialogCtrl(this);

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		rcurequiredDocs = getRCURequiredDocs();

		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		setScreenVerifications();

		this.listBoxRCUVerification.setHeight(borderLayoutHeight - 600 - 90 + "px");
		this.window_RCUVerificationDialog.setHeight(borderLayoutHeight - 75 + "px");

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
			setDocumentDetails(loanDocumentList, DocumentType.LOAN);
		}

		if (collaterls != null) {
			setDocumentDetails(getCollateralDocuments(collaterls), DocumentType.COLLATRL);
		}

		renderVerifications();
	}

	private void setCustomerDocuments(List<CustomerDocument> documents) {
		// Prepare the Customer Documents
		for (CustomerDocument document : documents) {
			if (isNotDeleted(document.getRecordType())) {
				Verification object = getVerification(document, DocumentType.CUSTOMER);
				customerDocuments.put(document.getCustDocCategory(), object);
			}
		}

		// Set the old verification fields back.
		Map<String, Verification> map;
		if (initType) {
			map = getOldVerifications(DocumentType.CUSTOMER, TableType.STAGE_TAB);
		} else {
			map = getOldVerifications(DocumentType.CUSTOMER, TableType.BOTH_TAB);
		}

		for (Entry<String, Verification> entrySet : customerDocuments.entrySet()) {
			Verification previous = map.get(entrySet.getKey());
			Verification current = entrySet.getValue();
			if (previous != null) {
				setOldVerificationFields(previous, current);
			}
		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			if (entrySet.getKey().startsWith("dummy$#")) {
				customerDocuments.put(entrySet.getKey(), entrySet.getValue());
			}
		}

		for (Entry<String, Verification> screen : customerDocuments.entrySet()) {
			for (Entry<String, Verification> old : map.entrySet()) {
				if (old.getValue().getReinitid() == null) {
					continue;
				}
				if (screen.getValue().getReinitid() == old.getValue().getReinitid()) {
					old.getValue().setDocName(screen.getValue().getDocName());
				}
			}
		}
	}

	private void setDocumentDetails(List<DocumentDetails> documents, DocumentType documentType) {

		Map<String, Verification> documentMap;

		if (documentType == DocumentType.LOAN) {
			documentMap = loanDocuments;
		} else {
			documentMap = collateralDocuments;
		}

		// Prepare the Customer Documents
		for (DocumentDetails document : documents) {
			if (isNotDeleted(document.getRecordType())) {
				Verification object = getVerification(rcurequiredDocs, document, documentType);
				documentMap.put(document.getDocCategory(), object);
			}
		}

		// Set the old verification fields back.
		Map<String, Verification> map;
		if (initType) {
			map = getOldVerifications(documentType, TableType.STAGE_TAB);
		} else {
			map = getOldVerifications(documentType, TableType.BOTH_TAB);
		}

		for (Entry<String, Verification> entrySet : documentMap.entrySet()) {
			Verification current = map.get(entrySet.getKey());
			Verification previous = entrySet.getValue();
			if (current != null) {
				setOldVerificationFields(current, previous);
			}
		}

		for (Entry<String, Verification> entrySet : map.entrySet()) {
			if (entrySet.getKey().startsWith("dummy$#")) {
				documentMap.put(entrySet.getKey(), entrySet.getValue());
			}
		}

		for (Entry<String, Verification> screen : documentMap.entrySet()) {
			for (Entry<String, Verification> old : map.entrySet()) {
				if (old.getValue().getReinitid() == null) {
					continue;
				}
				if (screen.getValue().getReinitid() == old.getValue().getReinitid()) {
					old.getValue().setDocName(screen.getValue().getDocName());
				}
			}
		}
	}

	private void setOldVerificationFields(Verification previous, Verification current) {
		current.setId(previous.getId());
		current.setRequestType(previous.getRequestType());
		current.setAgency(previous.getAgency());
		current.setAgencyName(previous.getAgencyName());
		current.setReason(previous.getReason());
		current.setRemarks(previous.getRemarks());
		current.setDecision(previous.getDecision());
		current.setDecisionRemarks(previous.getDecisionRemarks());
		current.setReference(previous.getReference());
		current.setNewRecord(false);

		if (previous.getRcuDocument() != null) {
			current.getRcuDocument().setVerificationId(previous.getRcuDocument().getVerificationId());
			current.getRcuDocument().setSeqNo(previous.getRcuDocument().getSeqNo());
		}

	}

	private boolean isNotDeleted(String recordType) {
		return !(PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| PennantConstants.RECORD_TYPE_CAN.equals(recordType));
	}

	private Verification getVerification(CustomerDocument document, DocumentType documentType) {
		RCUDocument rcuDocument = new RCUDocument();
		Verification item = new Verification();

		item.setDocName(getDocumentName(document.getCustDocCategory(), document.getLovDescCustDocCategory()));
		item.setReferenceFor(document.getCustDocCategory());

		item.setNewRecord(true);
		item.setReferenceType(documentType.getValue());
		item.setDocType(documentType.getKey());
		item.setVerificationType(VerificationType.RCU.getKey());
		item.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());

		//set RCU Required
		if (rcurequiredDocs.contains(document.getCustDocCategory())) {
			item.setRequestType(RequestType.INITIATE.getKey());
		} else {
			item.setRequestType(RequestType.NOT_REQUIRED.getKey());
		}

		rcuDocument.setDocCategory(document.getCustDocCategory());
		rcuDocument.setDocumentType(documentType.getKey());
		item.setRcuDocument(rcuDocument);
		return item;
	}

	private Verification getVerification(List<String> requiredDocs, DocumentDetails document,
			DocumentType documentType) {
		RCUDocument rcuDocument = new RCUDocument();
		Verification item = new Verification();

		item.setNewRecord(true);
		item.setDocName(getDocumentName(document.getDocCategory(), document.getLovDescDocCategoryName()));
		item.setReferenceType(documentType.getValue());
		item.setDocType(documentType.getKey());
		item.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		item.setReferenceFor(document.getDocCategory());
		item.setVerificationType(VerificationType.RCU.getKey());

		// set RCU Required
		if (requiredDocs.contains(document.getDocCategory())) {
			item.setRequestType(RequestType.INITIATE.getKey());
		} else {
			item.setRequestType(RequestType.NOT_REQUIRED.getKey());
		}

		rcuDocument.setDocCategory(document.getDocCategory());
		rcuDocument.setDocumentType(documentType.getKey());
		item.setRcuDocument(rcuDocument);
		return item;
	}

	private String getDocumentName(String code, String description) {
		StringBuilder builder = new StringBuilder();

		builder.append(code);
		if (description != null) {
			builder.append(" - ");
			builder.append(description);
		}

		return builder.toString();
	}

	private Map<String, Verification> getOldVerifications(DocumentType documentType, TableType tableType) {
		Map<String, Verification> verificationMap = new HashMap<>();
		List<Verification> list = new ArrayList<>();

		List<Verification> verifications = verificationService.getVerifications(this.verification.getKeyReference(),
				VerificationType.RCU.getKey());
		List<RCUDocument> oldDocuments = riskContainmentUnitService.getDocuments(verification.getKeyReference(),
				tableType, documentType);

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
					item.setDecisionRemarks((document.getDecisionRemarks()));
					item.setRcuDocument(document);
					list.add(item);
				}
			}
		}

		int i = 0;
		for (Verification ver : list) {
			if (ver.getReinitid() != null) {
				verificationMap.put("dummy$#".concat(String.valueOf(++i)), ver);
				continue;
			}

			String reference = ver.getReferenceFor();
			if (ver.getRcuDocument() != null) {
				reference = ver.getRcuDocument().getDocCategory();
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
		try {
			type = RequestType.getType(Integer.parseInt(cfiv.getSelectedItem().getValue()));
		} catch (Exception e) {
			String value = cfiv.getValue();
			cfiv.setValue("");
			throw new WrongValueException(cfiv,
					Labels.getLabel("STATIC_INVALID", new String[] { value + " is not valid," }));
		}
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
		if (riskContainmentUnit != null) {
			riskContainmentUnit = riskContainmentUnitService.getRiskContainmentUnit(riskContainmentUnit);
		}
		if (riskContainmentUnit != null) {
			map.put("LOAN_ORG", true);
			map.put("riskContainmentUnit", riskContainmentUnit);
			if (rcuInquiry.getChildren() != null) {
				rcuInquiry.getChildren().clear();
			}
			Executions.createComponents("/WEB-INF/pages/Verification/RiskContainmentUnit/RiskContainmentUnitDialog.zul",
					rcuInquiry, map);
		} else {
			MessageUtil.showMessage("Initiation request not available in RCU Verification Module.");
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
			setDocumentDetails(documents, DocumentType.LOAN);
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

	private List<DocumentDetails> getCollateralDocuments(List<CollateralAssignment> collaterals) {
		List<DocumentDetails> documents = new ArrayList<>();

		for (CollateralAssignment collateral : collaterals) {
			List<DocumentDetails> list = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
					CollateralConstants.MODULE_NAME, "", "_View");

			if (list != null) {
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

			if (vrf.getReinitid() != null && vrf.getRequestType() == RequestType.WAIVE.getKey()) {
				continue;
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
			listCell.appendChild(new Label(vrf.getDocName()));
			listCell.setParent(item);

			//RCU
			listCell = new Listcell();
			listCell.setId("RequestType".concat(String.valueOf(i)));
			Combobox requestType = new Combobox();
			requestType.setReadonly(true);
			requestType.setValue(String.valueOf(vrf.getRequestType()));
			List<ValueLabel> list = new ArrayList<>();
			if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
				for (ValueLabel valueLabel : RequestType.getList()) {
					if (Integer.parseInt(valueLabel.getValue()) != RequestType.WAIVE.getKey()) {
						list.add(valueLabel);
					}
				}
				fillComboBox(requestType, vrf.getRequestType(), list);
			} else {
				fillComboBox(requestType, vrf.getRequestType(), RequestType.getList());
			}
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

			//Status
			listCell = new Listcell();
			Label status = new Label();
			if (Status.getType(vrf.getStatus()) != null) {
				status.setValue(Status.getType(vrf.getStatus()).getValue());
			}
			listCell.appendChild(status);
			listCell.setParent(item);

			//Verification Date
			listCell = new Listcell();
			listCell.appendChild(new Label(DateUtil.formatToShortDate(vrf.getVerificationDate())));
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

				List<ValueLabel> decisionList = new ArrayList<>();
				if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()
						|| vrf.getStatus() == Status.POSITIVE.getKey()) {
					decisionList.add(
							new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
					decisionList
							.add(new ValueLabel(String.valueOf(Decision.SELECT.getKey()), Decision.SELECT.getValue()));
					if (vrf.getDecision() == Decision.SELECT.getKey()) {
						vrf.setDecision(Decision.APPROVE.getKey());
					}
					fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
				} else if (vrf.getStatus() == Status.NOTCMPLTD.getKey()
						|| vrf.getStatus() == Status.NEGATIVE.getKey()) {
					decisionList.add(
							new ValueLabel(String.valueOf(Decision.APPROVE.getKey()), Decision.APPROVE.getValue()));
					fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
				} else if (vrf.getRequestType() == RequestType.WAIVE.getKey()) {
					decisionList.add(
							new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
					fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
				} else {
					fillComboBox(decision, vrf.getDecision(), Decision.getList());
				}

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

				if (vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
					decision.setDisabled(true);
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

			if (!initType) {
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

	private List<String> getRCURequiredDocs() {
		Search search = new Search(String.class);
		search.addField("doctypecode");
		search.addTabelName("BMTDocumentTypes");
		search.addFilter(new Filter("rcureq", true));

		return searchProcessor.getResults(search);
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

			rcuComboBox.clearErrorMessage();
			agencyComboBox.clearErrorMessage();
			reasonComboBox.clearErrorMessage();

			if (decision != null) {
				decision.clearErrorMessage();
			}
			if (reInitagencyComboBox != null) {
				reInitagencyComboBox.clearErrorMessage();
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
			verification.setDecision(decision);
			if (!combobox.isDisabled() && decision == 0) {
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

		verification.setRecordStatus(this.recordStatus.getValue());
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

			fivComboBox.setConstraint("");
			agencyComboBox.setConstraint("");
			reasonComboBox.setConstraint("");

			if (decision != null) {
				decision.setConstraint("");
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

	public void doSave(FinanceDetail financeDetail, Tab tab) {
		logger.debug(Literal.ENTERING);

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
		financeDetail.setRcuVerification(this.verification);

		logger.debug(Literal.LEAVING);
	}

	private List<Verification> getVerifications() {
		Map<Long, Verification> map = new HashMap<>();
		Map<Long, Verification> reInitMap = new HashMap<>();
		Map<Long, Verification> other = new HashMap<>();
		List<Verification> verifications = new ArrayList<>();
		Verification aVerification = null;

		for (Verification vrf : this.verification.getVerifications()) {

			if (vrf.getRequestType() != RequestType.INITIATE.getKey()) {
				verifications.add(vrf);
			}

			if (vrf.getAgency() != null
					&& (!map.containsKey(vrf.getAgency()) || !reInitMap.containsKey(vrf.getAgency()))) {
				if (vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
					reInitMap.put(vrf.getAgency(), vrf);
				} else if (vrf.getRequestType() == vrf.getOldRequestType()) {
					map.put(vrf.getAgency(), vrf);
				} else {
					other.put(vrf.getAgency(), vrf);
				}
			}
		}

		for (Verification vrf : this.verification.getVerifications()) {
			RCUDocument document = vrf.getRcuDocument();
			document.setInitRemarks(vrf.getRemarks());
			document.setDecisionRemarks(vrf.getDecisionRemarks());
			if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
				if (!initType && vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
					aVerification = reInitMap.get(vrf.getAgency());
					aVerification.getRcuDocuments().add(document);
				} else if (vrf.getRequestType() == vrf.getOldRequestType()) {
					aVerification = map.get(vrf.getAgency());
					if (aVerification != null) {
						aVerification.getRcuDocuments().add(document);
					}
				} else {
					aVerification = other.get(vrf.getAgency());
					if (aVerification != null) {
						aVerification.getRcuDocuments().add(document);
					}
				}
			}
		}

		/*
		 * if (!initType) { for (Verification vrf : this.verification.getVerifications()) { RCUDocument document =
		 * vrf.getRcuDocument(); document.setInitRemarks(vrf.getRemarks());
		 * document.setDecisionRemarks(vrf.getDecisionRemarks()); if (vrf.getDecision() ==
		 * Decision.RE_INITIATE.getKey()) { aVerification = reInitMap.get(vrf.getAgency());
		 * aVerification.getRcuDocuments().add(document); } } }
		 */

		verifications.addAll(map.values());
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
