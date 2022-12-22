package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.collateral.impl.CollateralSetupFetchingService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.CollateralHeaderDialogCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.verification.legalverification.LVInitiationListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.Module;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.fi.LVStatus;
import com.pennanttech.pennapps.pff.verification.fi.TVStatus;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

@Component(value = "lVerificationCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LVerificationCtrl extends GFCBaseCtrl<Verification> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(LVerificationCtrl.class);

	protected Window window_LVerificationDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxInitiation;
	protected Listbox listBoxWaiver;
	protected Groupbox lvInquiry;
	protected Button btnNew_Initiation;
	protected Button btnNew_Waiver;
	protected Toolbar toolbar_Waiver;
	protected Toolbar toolbar_Initiation;
	protected Listheader listheader_LegalVerification_IReInitRemarks;
	protected Listheader listheader_LegalVerification_Initiation_ReInitiate;
	protected Listheader listheader_LegalVerification_WReInitRemarks;
	protected Listheader listheader_LegalVerification_Initiation_WReInitiate;

	private Button btnLVInitiateSave;
	private Button btnLVInitiateClose;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private Verification verification;
	private FinanceDetail financeDetail;

	private transient boolean validationOn;
	private transient boolean initType;
	private boolean recSave;

	private List<LVDocument> customerDocuments = new ArrayList<>();
	private List<LVDocument> loanDocuments = new ArrayList<>();
	private List<LVDocument> collateralDocuments = new ArrayList<>();
	private LVInitiationDialogCtrl lvInitiationDialogCtrl = null;
	private CollateralHeaderDialogCtrl collateralHeaderDialogCtrl = null;

	@Autowired
	private VerificationService verificationService;
	@Autowired
	private LegalVerificationService legalVerificationService;
	@Autowired
	private transient CollateralSetupFetchingService collateralSetupFetchingService;
	@Autowired
	private transient CollateralSetupDAO collateralSetupDAO;
	@Autowired
	private SearchProcessor searchProcessor;

	protected Radiogroup lv;

	private boolean fromVerification;
	private LVInitiationListCtrl lvInitiationListCtrl;

	/**
	 * default constructor.<br>
	 */
	public LVerificationCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_LVerificationDialog(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_LVerificationDialog);

		if (arguments.get("finHeaderList") != null) {
			appendFinBasicDetails(arguments.get("finHeaderList"));
		}
		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		this.verification = financeDetail.getLvVerification();
		if (verification == null) {
			this.verification = new Verification();
			financeDetail.setLvVerification(this.verification);
		}
		this.verification.setKeyReference(financeDetail.getFinScheduleData().getFinReference());

		if (arguments.containsKey("financeMainBaseCtrl")) {
			financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");
		} else {
			finBasicdetails.setVisible(false);
		}

		if (arguments.containsKey("lvInitiationListCtrl")) {
			this.lvInitiationListCtrl = (LVInitiationListCtrl) arguments.get("lvInitiationListCtrl");
			this.fromVerification = true;
			finBasicdetails.setVisible(true);
		}

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		if (arguments.get("enqiryModule") != null) {
			enqiryModule = (Boolean) arguments.get("enqiryModule");
		}
		doCheckRights();
		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		if (fromVerification) {
			this.btnLVInitiateSave.setVisible(fromVerification);
			this.btnLVInitiateClose.setVisible(fromVerification);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		if (financeMainDialogCtrl != null) {
			financeMainDialogCtrl.setLVerificationCtrl(this);
		}

		if (initType) {
			this.btnLVInitiateSave.setVisible(fromVerification);
			this.btnLVInitiateClose.setVisible(fromVerification);
		}
		if (enqiryModule) {
			this.btnNew_Initiation.setVisible(!enqiryModule);
			if (btnLVInitiateSave != null) {
				this.btnLVInitiateSave.setVisible(!enqiryModule);
			}
			if (btnNew_Waiver != null) {
				this.btnNew_Waiver.setVisible(!enqiryModule);
			}
		}
		if (fromVerification) {
			setDialog(DialogType.EMBEDDED);
		}

		// render Initiation and Waiver Lists
		renderLVInitiationList();
		renderLVWaiverList();
		setScreenDocuments();

		if (!initType) {
			this.toolbar_Initiation.setVisible(false);
			this.toolbar_Waiver.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_Initiation(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.verification.getKeyReference() == null) {
			MessageUtil.showError("Loan Refererence is not available. Schedule must be generated");
			return;
		}
		// Create a new entity.
		Verification averification = new Verification();
		BeanUtils.copyProperties(this.verification, averification);
		averification.getLvDocuments().clear();
		averification.setNewRecord(true);
		averification.setVerificationType(VerificationType.LV.getKey());
		averification.setRequestType(RequestType.INITIATE.getKey());
		averification.setKeyReference(this.verification.getKeyReference());
		averification.setCustId(financeDetail.getCustomerDetails().getCustomer().getCustID());
		averification.setReference(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
		doShowDialogPage(averification, true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_Waiver(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.verification.getKeyReference() == null) {
			MessageUtil.showError("Loan Refererence is not available. Schedule must be generated");
			return;
		}
		Verification item = new Verification();
		BeanUtils.copyProperties(this.verification, item);
		item.setVerificationType(VerificationType.LV.getKey());
		item.setRequestType(RequestType.WAIVE.getKey());
		item.setKeyReference(this.verification.getKeyReference());
		item.setCustId(financeDetail.getCustomerDetails().getCustomer().getCustID());
		item.setReference(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
		item.setNewRecord(true);

		// Display the dialog page.
		doShowDialogPage(item, false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
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

	public void onChangeReason(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox reason = (ExtendedCombobox) getComponent(listitem, "Reason");
		Object dataObject = reason.getObject();

		if (dataObject instanceof String) {
			reason.setValue(dataObject.toString());
			reason.setDescription("");
		} else {
			ReasonCode reasonCode = (ReasonCode) dataObject;
			if (reasonCode != null) {
				reason.setValue(reasonCode.getCode());
				reason.setAttribute("reasonId", reasonCode.getId());
			}
		}
	}

	public void onCheck$lv(Event event) {
		final Map<String, Object> map = new HashMap<>();
		LegalVerification legalVerification = lv.getSelectedItem().getValue();
		if (lvInquiry.getChildren().size() >= 2) {
			lvInquiry.getChildren().remove(1);
		}

		if (legalVerification != null) {
			legalVerification = legalVerificationService.getLegalVerification(legalVerification, "_View");
		}

		if (legalVerification != null && StringUtils.isEmpty(legalVerification.getNextRoleCode())) {
			map.put("LOAN_ORG", true);
			map.put("legalVerification", legalVerification);
			Executions.createComponents("/WEB-INF/pages/Verification/LegalVerification/LegalVerificationDialog.zul",
					lvInquiry, map);
		} else if (legalVerification != null) {
			MessageUtil.showMessage("Verification is not yet completed.");
		} else {
			MessageUtil.showMessage("Initiation request not available.");
		}

	}

	/**
	 * Method to fill LV Initiation List.
	 * 
	 * @param customer
	 */
	public void renderLVInitiationList() {
		logger.debug(Literal.ENTERING);
		List<Verification> verifications = null;
		if (initType) {
			verifications = getVerifications();
		} else if (!fromVerification) {
			if (lvInquiry.getChildren().size() >= 2) {
				lvInquiry.getChildren().remove(1);
			}
			verifications = getInitVerifications();
		}
		if (this.listBoxInitiation.getItems() != null) {
			this.listBoxInitiation.getItems().clear();
		}

		int i = 0;
		for (Verification vrf : verifications) {
			if (vrf.getRequestType() == RequestType.WAIVE.getKey()) {
				continue;
			}

			i++;
			Listitem item = new Listitem();
			Listcell listCell;
			if (!initType && !fromVerification) {
				// Select
				listCell = new Listcell();
				listCell.setId("select".concat(String.valueOf(i)));
				Radio select = new Radio();

				select.setRadiogroup(lv);
				select.setValue(vrf.getLegalVerification());
				listCell.appendChild(select);
				listCell.setParent(item);
			}

			// Collateral Type
			listCell = new Listcell();
			listCell.setId("CollateralType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceFor().concat("-").concat(vrf.getReferenceType())));
			listCell.setParent(item);

			// Agency
			listCell = new Listcell();
			listCell.setId("Agency".concat(String.valueOf(i)));
			Label agency = new Label();
			if (vrf.getAgencyName() != null) {
				agency.setValue(String.valueOf(vrf.getAgencyName()));
				agency.setAttribute("agencyId", vrf.getAgency());
			} else if (vrf.getAgency() != null) {
				agency.setAttribute("agencyId", vrf.getAgency());
			}
			listCell.appendChild(agency);
			listCell.setParent(item);

			// Remarks
			listCell = new Listcell();
			listCell.setId("IRemarks".concat(String.valueOf(i)));
			Label remarks = new Label(vrf.getRemarks());
			listCell.appendChild(remarks);
			listCell.setParent(item);

			if (initType) {
				// Last Verification Agency
				listCell = new Listcell();
				listCell.appendChild(new Label(vrf.getLastAgency()));
				listCell.setParent(item);
			}

			// Status
			listCell = new Listcell();
			Label status = new Label();
			if (initType && vrf.getLastStatus() != 0) {
				status.setValue(LVStatus.getType(vrf.getLastStatus()).getValue());

			} else if (!initType && vrf.getStatus() != 0) {
				status.setValue(LVStatus.getType(vrf.getStatus()).getValue());
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
				// Decision
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

				// Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("IReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				reInitRemarks.setValue(vrf.getDecisionRemarks());
				reInitRemarks.setMaxlength(500);
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				// ReInitiate
				listCell = new Listcell();
				listCell.setId("IReInitiate".concat(String.valueOf(i)));
				Button btnReInit = new Button("ReInitiate");
				btnReInit.setDisabled(true);
				listCell.appendChild(btnReInit);
				listCell.setStyle("text-align:right;");
				listCell.setParent(item);

				item.setAttribute("verification", vrf);
				btnReInit.addForward("onClick", self, "onClickReInitiate", item);
				decision.addForward("onChange", self, "onChangeInitDecision", item);

				if (vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
					decision.setDisabled(true);
					btnReInit.setDisabled(true);
					reInitRemarks.setReadonly(true);
				}

				if (enqiryModule) {
					listheader_LegalVerification_IReInitRemarks.setVisible(false);
					listheader_LegalVerification_Initiation_ReInitiate.setVisible(false);
					listheader_LegalVerification_WReInitRemarks.setVisible(false);
					listheader_LegalVerification_Initiation_WReInitiate.setVisible(false);
					decision.setDisabled(true);
					reInitRemarks.setReadonly(true);
					btnReInit.setDisabled(true);
				}
			}

			item.setAttribute("vrf", vrf);
			if (initType) {
				item.setAttribute("id", vrf.getId());
				ComponentsCtrl.applyForward(item, "onDoubleClick=onVerificationItemDoubleClicked");
			}
			this.listBoxInitiation.appendChild(item);

		}
		logger.debug(Literal.LEAVING);
	}

	private void fillDecision(Verification vrf, Combobox combobox) {
		List<ValueLabel> decisionList = new ArrayList<>();

		int requestType = vrf.getRequestType();
		int status = vrf.getStatus();
		int decision = vrf.getDecision();

		if (requestType == RequestType.NOT_REQUIRED.getKey() || status == TVStatus.POSITIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			decisionList.add(new ValueLabel(String.valueOf(Decision.SELECT.getKey()), Decision.SELECT.getValue()));
			if (decision == Decision.SELECT.getKey()) {
				vrf.setDecision(Decision.APPROVE.getKey());
			}
			fillComboBox(combobox, vrf.getDecision(), filterDecisions(decisionList));
		} else if (status == TVStatus.NEGATIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.APPROVE.getKey()), Decision.APPROVE.getValue()));
			if (decision == Decision.APPROVE.getKey()) {
				vrf.setDecision(Decision.SELECT.getKey());
			}
			fillComboBox(combobox, vrf.getDecision(), filterDecisions(decisionList));
		} else if (requestType == RequestType.WAIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			fillComboBox(combobox, decision, filterDecisions(decisionList));
		} else {
			fillComboBox(combobox, decision, Decision.getList());
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

	private void setScreenDocuments() {
		List<CustomerDocument> customerDocumentList = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<DocumentDetails> loanDocumentList = financeDetail.getDocumentDetailsList();
		List<CollateralAssignment> collaterls = financeDetail.getCollateralAssignmentList();

		if (customerDocumentList != null) {
			addCustomerDocuments(customerDocumentList);
		}

		if (loanDocumentList != null) {
			addLoanDocuments(loanDocumentList);
		}

		if (collaterls != null) {
			addCollateralDocuments(collaterls, financeDetail);
		}
	}

	private List<LVDocument> getDocuments() {
		if (!initType) {
			setScreenDocuments();
		}
		List<LVDocument> documents = new ArrayList<>();
		documents.addAll(customerDocuments);
		documents.addAll(loanDocuments);
		documents.addAll(collateralDocuments);

		return documents;
	}

	public void addCustomerDocuments(List<CustomerDocument> documents) {
		customerDocuments.clear();
		for (CustomerDocument document : documents) {
			LVDocument lvDocument = new LVDocument();
			lvDocument.setDocumentId(document.getCustID());
			lvDocument.setDocumentSubId(document.getCustDocCategory());
			lvDocument.setDocumentType(DocumentType.CUSTOMER.getKey());
			lvDocument.setDescription(document.getLovDescCustDocCategory());

			customerDocuments.add(lvDocument);
		}
	}

	public void addLoanDocuments(List<DocumentDetails> documents) {
		loanDocuments.clear();
		for (DocumentDetails document : documents) {
			if ((DocumentCategories.CUSTOMER.getKey().equals(document.getCategoryCode()))) {
				continue;
			}
			LVDocument lvDocument = new LVDocument();
			lvDocument.setDocumentId(document.getId());
			lvDocument.setDocumentSubId(document.getDocCategory());
			lvDocument.setDocumentType(DocumentType.LOAN.getKey());
			lvDocument.setDescription(document.getLovDescDocCategoryName());
			loanDocuments.add(lvDocument);
		}
	}

	public void addCollateralDocuments(List<CollateralAssignment> collateralAsssignments,
			FinanceDetail financeDetails) {
		collateralDocuments.clear();
		List<DocumentDetails> documents = getCollateralSetupFetchingService()
				.getCollateralDocuments(collateralAsssignments, financeDetails.getCollaterals(), false);

		for (DocumentDetails document : documents) {
			LVDocument lvDocument = new LVDocument();
			lvDocument.setDocumentId(document.getId());
			lvDocument.setDocumentSubId(document.getDocCategory());
			lvDocument.setDocumentType(DocumentType.COLLATRL.getKey());
			lvDocument.setDescription(document.getLovDescDocCategoryName());
			lvDocument.setCollateralRef(document.getReferenceId());
			lvDocument.setDocumentRefId(document.getDocRefId());

			collateralDocuments.add(lvDocument);
		}
		if (getLvInitiationDialogCtrl() != null) {
			getLvInitiationDialogCtrl().setCollateralTypeList(collateralAsssignments, financeDetails.getCollaterals());
			setupdatedFinanceData(collateralAsssignments, financeDetails);
		}
		if (getCollateralHeaderDialogCtrl() != null) {
			setupdatedFinanceData(getCollateralHeaderDialogCtrl().getCollateralAssignments(),
					getCollateralHeaderDialogCtrl().getFinanceDetail());
		}
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onVerificationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxInitiation.getSelectedItem();
		if (selectedItem == null) {
			return;
		}
		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		Verification vrf = verificationService.getVerificationById(id, VerificationType.LV);
		if (vrf == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		doShowDialogPage(vrf, true);

		logger.debug("Leaving");
	}

	private void doShowDialogPage(Verification vrf, boolean initiation) {
		logger.debug(Literal.ENTERING);

		// Display the dialog page.
		Map<String, Object> arg = new HashMap<>();
		arg.put("initiation", initiation);
		arg.put("verification", vrf);
		arg.put("legalVerificationListCtrl", this);
		arg.put("financeDetail", financeDetail);
		arg.put("lvDocuments", getDocuments());
		arg.put("enqiryModule", enqiryModule);
		if (!fromVerification) {
			arg.put("financeMainBaseCtrl", this.financeMainDialogCtrl);
		}

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVInitiationDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill LV Waiver List.
	 * 
	 * @param customer
	 */
	public void renderLVWaiverList() {
		logger.debug(Literal.ENTERING);
		List<Verification> verifications;
		if (initType) {
			verifications = verificationService.getVerifications(this.verification.getKeyReference(),
					VerificationType.LV.getKey());
			for (Verification verification : verifications) {
				verificationService.setLastStatus(verification);
			}
		} else {
			verifications = getWaiveVerifications();
		}

		if (this.listBoxWaiver.getItems() != null) {
			this.listBoxWaiver.getItems().clear();
		}

		int i = 0;
		for (Verification vrf : verifications) {
			if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
				continue;
			}

			i++;
			Listitem item = new Listitem();
			Listcell listCell;

			// Collateral Reference
			listCell = new Listcell();
			listCell.setId("CollateralRef".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceFor()));
			listCell.setParent(item);

			// Document Type
			listCell = new Listcell();
			listCell.setId("DocumentType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceType()));
			listCell.setParent(item);

			// Reason
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

			// Remarks
			listCell = new Listcell();
			listCell.setId("WRemarks".concat(String.valueOf(i)));
			Textbox remarks = new Textbox(vrf.getRemarks());
			listCell.appendChild(remarks);
			listCell.setParent(item);

			if (initType) {
				// Last Verification Agency
				listCell = new Listcell();
				listCell.appendChild(new Label(vrf.getLastAgency()));
				listCell.setParent(item);
			}

			// Status
			listCell = new Listcell();
			Label status = new Label();
			if (initType && vrf.getLastStatus() != 0) {
				status.setValue(LVStatus.getType(vrf.getLastStatus()).getValue());

			} else if (!initType && vrf.getStatus() != 0) {
				status.setValue(LVStatus.getType(vrf.getStatus()).getValue());
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
				// Decision
				listCell = new Listcell();
				listCell.setId("WDecision".concat(String.valueOf(i)));
				Combobox decision = new Combobox();
				decision.setReadonly(true);
				if (Decision.getType(vrf.getDecision()) != null) {
					decision.setValue(String.valueOf(Decision.getType(vrf.getDecision()).getValue()));
				}
				fillDecision(vrf, decision);
				decision.setParent(listCell);
				listCell.setParent(item);

				// Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("WReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				reInitRemarks.setValue(vrf.getDecisionRemarks());
				reInitRemarks.setMaxlength(500);
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				// ReInitiate
				listCell = new Listcell();
				listCell.setId("WReInitiate".concat(String.valueOf(i)));
				Button btnReInit = new Button("ReInitiate");
				btnReInit.setDisabled(true);
				listCell.appendChild(btnReInit);
				listCell.setStyle("text-align:right;");
				listCell.setParent(item);

				item.setAttribute("verification", vrf);
				btnReInit.addForward("onClick", self, "onClickInitiate", item);
				decision.addForward("onChange", self, "onChangeWaiveDecision", item);

				if (vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
					decision.setDisabled(true);
					btnReInit.setDisabled(true);
					reInitRemarks.setReadonly(true);
				}

				if (enqiryModule) {
					decision.setDisabled(true);
					reInitRemarks.setReadonly(true);
					btnReInit.setDisabled(true);
				}
			}

			item.setAttribute("vrf", vrf);
			reason.addForward("onFulfill", self, "onChangeReason", item);

			if (!initType) {
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}

			this.listBoxWaiver.appendChild(item);

			if (enqiryModule) {
				if (listheader_LegalVerification_IReInitRemarks != null) {
					listheader_LegalVerification_IReInitRemarks.setVisible(false);
				}
				if (listheader_LegalVerification_Initiation_ReInitiate != null) {
					listheader_LegalVerification_Initiation_ReInitiate.setVisible(false);
				}
				if (listheader_LegalVerification_WReInitRemarks != null) {
					listheader_LegalVerification_WReInitRemarks.setVisible(false);
				}
				if (listheader_LegalVerification_Initiation_WReInitiate != null) {
					listheader_LegalVerification_Initiation_WReInitiate.setVisible(false);
				}
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	public void onClickReInitiate(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();

		// Get the selected entity.
		Verification vrf = (Verification) listitem.getAttribute("verification");

		if (vrf == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		vrf.setNewRecord(true);
		vrf.setApproveTab(true);

		doShowDialogPage(vrf, true);
	}

	public void onClickInitiate(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();

		// Get the selected entity.
		Verification vrf = (Verification) listitem.getAttribute("verification");

		if (vrf == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		vrf.setCollateralSetup(collateralSetupDAO.getCollateralSetupByRef(vrf.getReferenceFor(), "_view"));
		vrf.setRequestType(RequestType.INITIATE.getKey());
		vrf.setNewRecord(true);
		vrf.setApproveTab(true);
		vrf.setWaiveTab(true);

		doShowDialogPage(vrf, true);
	}

	private void fillReasons(ExtendedCombobox reason) {
		logger.debug(Literal.ENTERING);

		reason.setModuleName("VerificationWaiverReason");
		reason.setValueColumn("Code");
		reason.setValidateColumns(new String[] { "Code" });
		Filter reasonFilter[] = new Filter[1];
		reasonFilter[0] = new Filter("ReasonTypecode", WaiverReasons.LVWRES.getKey(), Filter.OP_EQUAL);
		reason.setFilters(reasonFilter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		for (Listitem listitem : listBoxWaiver.getItems()) {
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "WDecision");
			Textbox textBox = (Textbox) getComponent(listitem, "WReInitRemarks");

			reasonComboBox.clearErrorMessage();
			if (decision != null) {
				decision.clearErrorMessage();
			}
			if (textBox != null) {
				textBox.clearErrorMessage();
			}
		}

		for (Listitem listitem : listBoxInitiation.getItems()) {
			Combobox decision = (Combobox) getComponent(listitem, "Decision");
			Textbox textBox = (Textbox) getComponent(listitem, "IReInitRemarks");
			if (decision != null) {
				decision.clearErrorMessage();
			}
			if (textBox != null) {
				textBox.clearErrorMessage();
			}
		}
	}

	public void onChangeInitDecision(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		Combobox decisionBox = (Combobox) getComponent(listitem, "Decision");
		Textbox textbox = (Textbox) getComponent(listitem, "IReInitRemarks");
		Button button = (Button) getComponent(listitem, "IReInitiate");

		int decision = Integer.parseInt(decisionBox.getSelectedItem().getValue());
		if (decision == 2) {
			button.setDisabled(false);
			textbox.setValue("");
			textbox.setReadonly(true);
		} else {
			button.setDisabled(true);
			textbox.setReadonly(false);
		}
	}

	public void onChangeWaiveDecision(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		Combobox decisionBox = (Combobox) getComponent(listitem, "WDecision");
		Textbox textbox = (Textbox) getComponent(listitem, "WReInitRemarks");
		Button button = (Button) getComponent(listitem, "WReInitiate");

		int decision = Integer.parseInt(decisionBox.getSelectedItem().getValue());
		if (decision == 2) {
			button.setDisabled(false);
			textbox.setReadonly(true);
		} else {
			button.setDisabled(true);
			textbox.setReadonly(false);
		}

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		for (Listitem listitem : listBoxWaiver.getItems()) {
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");

			if (!reasonComboBox.isReadonly()) {
				reasonComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_LegalVerificationDialog_Reason.value"), null, true, true));
			}

		}

		logger.debug("Leaving");
	}

	private Verification updateInitVerification(Listitem listitem, ArrayList<WrongValueException> wve) {
		Verification verification = (Verification) listitem.getAttribute("vrf");

		Label agency = ((Label) getComponent(listitem, "Agency"));
		if (agency != null && agency.getAttribute("agencyId") != null) {
			verification.setAgency(Long.parseLong(agency.getAttribute("agencyId").toString()));
		}

		if (!initType) {
			try {
				Combobox combobox = (Combobox) getComponent(listitem, "Decision");
				int decision = Integer.parseInt(getComboboxValue(combobox).toString());
				verification.setDecision(decision);
				if (combobox.isDisabled()) {
					verification.setIgnoreFlag(true);
				} else if (decision == Decision.RE_INITIATE.getKey()) {
					verification.setApproveTab(true);
				}
				if (!combobox.isDisabled() && decision == 0 && !this.recSave) {
					throw new WrongValueException(combobox,
							Labels.getLabel("STATIC_INVALID", new String[] { "Decision should be mandatory" }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Textbox textbox = (Textbox) getComponent(listitem, "IReInitRemarks");
				verification.setDecisionRemarks(textbox.getValue());
				if (verification.getDecision() == Decision.OVERRIDE.getKey()
						&& StringUtils.isEmpty(verification.getDecisionRemarks())) {
					throw new WrongValueException(textbox, Labels.getLabel("label_OVERRIDE_Validation"));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		return verification;
	}

	private Verification updateWaiverVerification(Listitem listitem, ArrayList<WrongValueException> wve) {
		Verification verification = null;
		verification = (Verification) listitem.getAttribute("vrf");
		ExtendedCombobox reason = ((ExtendedCombobox) getComponent(listitem, "Reason"));
		Label collateral = ((Label) getComponent(listitem, "CollateralRef"));

		verification.setReferenceFor(collateral.getValue());

		try {

			if (StringUtils.isNotEmpty(reason.getValue())) {
				verification.setReason(Long.parseLong(reason.getAttribute("reasonId").toString()));
			} else {
				verification.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		verification.setRemarks(((Textbox) getComponent(listitem, "WRemarks")).getValue());

		if (!initType) {
			try {
				Combobox combobox = (Combobox) getComponent(listitem, "WDecision");
				int decision = Integer.parseInt(getComboboxValue(combobox).toString());
				verification.setDecision(decision);
				if (combobox.isDisabled()) {
					verification.setIgnoreFlag(true);
				} else if (decision == Decision.RE_INITIATE.getKey()) {
					throw new WrongValueException(combobox, "Change the decision or Reinitiate the record");
				}
				if (!combobox.isDisabled() && decision == 0 && !this.recSave) {
					throw new WrongValueException(combobox,
							Labels.getLabel("STATIC_INVALID", new String[] { "Decision should be mandatory" }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Textbox textbox = (Textbox) getComponent(listitem, "WReInitRemarks");
				verification.setDecisionRemarks(textbox.getValue());
				if (verification.getDecision() == Decision.OVERRIDE.getKey()
						&& StringUtils.isEmpty(verification.getDecisionRemarks())) {
					throw new WrongValueException(textbox, Labels.getLabel("label_OVERRIDE_Validation"));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		return verification;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @return
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		this.verification.getVerifications().clear();
		for (Listitem listitem : listBoxWaiver.getItems()) {
			this.verification.getVerifications().add(updateWaiverVerification(listitem, wve));
		}

		for (Listitem listitem : listBoxInitiation.getItems()) {
			this.verification.getVerifications().add(updateInitVerification(listitem, wve));
		}

		doRemoveValidation();

		logger.debug("Leaving");
		return wve;
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		for (Listitem listitem : listBoxWaiver.getItems()) {
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "WDecision");

			reasonComboBox.setConstraint("");
			if (decision != null) {
				decision.setConstraint("");
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
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

		logger.debug("Leaving");
	}

	public boolean doSave(FinanceDetail financeDetail, Tab tab, boolean recSave, Radiogroup userAction) {
		logger.debug(Literal.ENTERING);
		this.recSave = recSave;
		this.userAction = userAction;
		doClearMessage();
		if (!recSave) {
			doSetValidation();
		}

		ArrayList<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);

		this.verification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		financeDetail.setLvVerification(this.verification);

		if (fromVerification
				&& (this.listBoxInitiation.getItems().isEmpty() && (this.listBoxWaiver.getItems().isEmpty()))) {
			throw new WrongValueException(Labels.getLabel("label_LVerification_Initiation_Validation.value"));
		}

		if (tab != null && tab.getId().equals("TAB".concat(AssetConstants.UNIQUE_ID_LVAPPROVAL))) {
			return validateReinitiation(financeDetail.getLvVerification().getVerifications());
		} else {
			prepareVerifications();
			if (!recSave) {
				return validateCollateralDocuments(tab);
			}
		}
		return true;

	}

	private void prepareVerifications() {
		List<Verification> verificationsList = verificationService.getVerifications(this.verification.getKeyReference(),
				VerificationType.LV.getKey());

		List<Verification> finalList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(verificationsList)) {
			for (Verification verification : verificationsList) {
				if (CollectionUtils.isNotEmpty(financeDetail.getCollateralAssignmentList())) {
					for (CollateralAssignment assign : financeDetail.getCollateralAssignmentList()) {
						if (StringUtils.equals(verification.getReferenceFor(), assign.getCollateralRef())) {
							finalList.add(verification);
						}
					}
				}
			}
		}

		for (Verification oldVrf : finalList) {
			for (Verification newVrf : verification.getVerifications()) {
				if (newVrf.getId() == oldVrf.getId() && newVrf.getRequestType() == RequestType.WAIVE.getKey()) {
					BeanUtils.copyProperties(newVrf, oldVrf);
				}
			}
		}
		this.verification.setVerifications(finalList);
		verificationService.setLVDetails(this.verification.getVerifications());
	}

	private List<String> getRequiredDocuments() {
		List<String> lvRequiredDocs = new ArrayList<>();
		Search search = new Search(com.pennant.backend.model.systemmasters.DocumentType.class);
		search.addField("doctypecode");
		search.addTabelName("BMTDocumentTypes");
		search.addFilter(new Filter("lvreq", 1));
		search.addFilter(new Filter("categoryId", 3));
		List<com.pennant.backend.model.systemmasters.DocumentType> list = searchProcessor.getResults(search);

		for (com.pennant.backend.model.systemmasters.DocumentType documentType : list) {
			lvRequiredDocs.add(documentType.getDocTypeCode());
		}
		return lvRequiredDocs;
	}

	private List<Long> getRequiredCollateralDocuments() {
		List<String> lvRequiredDocs = getRequiredDocuments();
		List<Long> result = new ArrayList<>();

		for (LVDocument document : collateralDocuments) {
			if (lvRequiredDocs.contains(document.getDocumentSubId())) {
				result.add(document.getDocumentRefId());
			}
		}
		return result;
	}

	private boolean validateCollateralDocuments(Tab tab) {
		List<Long> requiredCollateralDocs = getRequiredCollateralDocuments();
		List<Long> collateralDocuments = new ArrayList<>();
		for (Verification verification : this.verification.getVerifications()) {
			for (LVDocument document : verification.getLvDocuments()) {
				if (document.getDocumentType() == DocumentType.COLLATRL.getKey()) {
					collateralDocuments.add(document.getDocumentRefId());
				}
			}
		}
		for (Long documentId : requiredCollateralDocs) {
			if (!collateralDocuments.contains(documentId)) {
				MessageUtil.showError("Required collateral documents should be initiate/Waive");
				if (tab != null) {
					tab.setSelected(true);
				}
				return false;
			}
		}
		return true;
	}

	private boolean validateReinitiation(List<Verification> verifications) {
		for (Verification verification : verifications) {
			if (verification.getDecision() == Decision.RE_INITIATE.getKey()
					&& !userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_SAVED)
					&& verification.getReinitid() == null) {
				MessageUtil.showError("Legal Verification Re-Initiation is allowed only when user action is save");
				return false;
			}

			if (verification.getDecision() == Decision.APPROVE.getKey() && !recSave
					&& ImplementationConstants.ALW_VERIFICATION_SYNC) {
				LegalVerification legalVerification = new LegalVerification();
				legalVerification.setVerificationId(verification.getId());
				legalVerification = legalVerificationService.getLegalVerification(legalVerification, "_View");
				verification.setLegalVerification(legalVerification);
			}
		}
		return true;
	}

	private List<Verification> getVerifications() {
		List<Verification> list;
		String keyReference = this.verification.getKeyReference();
		list = verificationService.getVerifications(keyReference, VerificationType.LV.getKey());
		if (initType) {
			for (Verification verification : list) {
				verificationService.setLastStatus(verification);
			}
		}

		List<Verification> finalList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(list)) {
			for (Verification verification : list) {
				if (CollectionUtils.isNotEmpty(financeDetail.getCollateralAssignmentList())) {
					for (CollateralAssignment assign : financeDetail.getCollateralAssignmentList()) {
						if (StringUtils.equals(verification.getReferenceFor(), assign.getCollateralRef())) {
							finalList.add(verification);
						}
					}
				}
			}
		}
		return finalList;
	}

	private List<Verification> getInitVerifications() {
		List<Verification> verifications = getVerifications();
		List<LegalVerification> legalVerifications = legalVerificationService.getList(verification.getKeyReference());
		for (Verification verfcation : verifications) {
			for (LegalVerification item : legalVerifications) {
				if (verfcation.getId() == item.getVerificationId()) {
					verfcation.setLegalVerification(item);
					verfcation.setLvDocuments(legalVerificationService.getLVDocuments(verfcation.getId()));
					break;
				}
			}
		}
		return verifications;
	}

	private List<Verification> getWaiveVerifications() {
		// get verifications
		List<Verification> verifications = verificationService.getVerifications(verification.getKeyReference(),
				VerificationType.LV.getKey());

		for (Verification vrf : verifications) {
			vrf.setLvDocuments(legalVerificationService.getLVDocumentsFromStage(vrf.getId()));
		}
		return verifications;
	}

	private void setVerificationData(FinanceDetail financeDetail) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		verification.setCif(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
		verification.setModule(Module.LOAN.getKey());
		verification.setKeyReference(financeMain.getFinReference());
		verification.setCustId(customer.getCustID());
		verification.setCustomerName(customer.getCustShrtName());
		verification.setReference(customer.getCustCIF());
		verification.setCreatedOn(SysParamUtil.getAppDate());
	}

	private void setupdatedFinanceData(List<CollateralAssignment> collateralAsssignments,
			FinanceDetail financeDetails) {
		this.financeDetail.setCollateralAssignmentList(collateralAsssignments);
		this.financeDetail.setCollaterals(financeDetails.getCollaterals());
	}

	public void onClick$btnLVInitiateSave(Event event) {
		logger.debug(Literal.ENTERING);
		try {
			doSave(financeDetail, null, recSave, lv);
		} catch (WrongValueException e) {
			MessageUtil.showError(e.getMessage());
			return;
		}
		try {
			verificationService.saveOrUpdate(financeDetail, VerificationType.LV, PennantConstants.TRAN_WF, initType);
			refreshList();
			String msg = Labels.getLabel("LV_INITIATION",
					new String[] { financeDetail.getFinScheduleData().getFinReference() });
			Clients.showNotification(msg, "info", null, null, -1);
			closeDialog();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void refreshList() {
		lvInitiationListCtrl.search();
	}

	public void onClick$btnLVInitiateClose(Event event) {
		doClose(this.btnLVInitiateSave.isVisible());
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

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setVerificationData(financeDetail);
	}

	public LVInitiationDialogCtrl getLvInitiationDialogCtrl() {
		return lvInitiationDialogCtrl;
	}

	public void setLvInitiationDialogCtrl(LVInitiationDialogCtrl lvInitiationDialogCtrl) {
		this.lvInitiationDialogCtrl = lvInitiationDialogCtrl;
	}

	public CollateralSetupFetchingService getCollateralSetupFetchingService() {
		return collateralSetupFetchingService;
	}

	public void setCollateralSetupFetchingService(CollateralSetupFetchingService collateralSetupFetchingService) {
		this.collateralSetupFetchingService = collateralSetupFetchingService;
	}

	public CollateralHeaderDialogCtrl getCollateralHeaderDialogCtrl() {
		return collateralHeaderDialogCtrl;
	}

	public void setCollateralHeaderDialogCtrl(CollateralHeaderDialogCtrl collateralHeaderDialogCtrl) {
		this.collateralHeaderDialogCtrl = collateralHeaderDialogCtrl;
	}

}
