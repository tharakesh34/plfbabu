package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.Module;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.Status;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

@Component(value = "lVerificationCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LVerificationCtrl extends GFCBaseListCtrl<Verification> {
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

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private Verification verification;

	private transient boolean validationOn;
	private transient boolean initType;
	private FinanceDetail financeDetail;

	private List<LVDocument> customerDocuments = new ArrayList<>();
	private List<LVDocument> loanDocuments = new ArrayList<>();
	private List<LVDocument> collateralDocuments = new ArrayList<>();

	@Autowired
	private VerificationService verificationService;
	@Autowired
	private LegalVerificationService legalVerificationService;
	@Autowired
	private transient DocumentDetailsDAO documentDetailsDAO;
	@Autowired
	private transient CollateralSetupDAO collateralSetupDAO;

	protected Radiogroup lv;

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

	public void onCreate$window_LVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_LVerificationDialog);

		appendFinBasicDetails(arguments.get("finHeaderList"));

		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		this.verification = financeDetail.getLvVerification();

		//verification.setReference(verification.getCif());

		financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		financeMainDialogCtrl.setLVerificationCtrl(this);

		//render Initiation and Waiver Lists
		renderLVInitiationList();
		renderLVWaiverList();
		setScreenDocuments();

		int divHeight = this.borderLayoutHeight - 80;
		int borderlayoutHeights = divHeight / 3;
		this.listBoxInitiation.setHeight(borderlayoutHeights - 30 + "px");
		this.listBoxWaiver.setHeight(borderlayoutHeights - 30 + "px");
		this.window_LVerificationDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		if (!initType) {
			this.toolbar_Initiation.setVisible(false);
			this.toolbar_Waiver.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_Initiation(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.verification.getKeyReference() == null) {
			MessageUtil.showError("Loan Refererence is not available. Schedule must be generated");
			return;
		}
		// Create a new entity.
		Verification verification = new Verification();
		BeanUtils.copyProperties(this.verification, verification);
		verification.getLvDocuments().clear();
		verification.setNewRecord(true);
		verification.setVerificationType(VerificationType.LV.getKey());
		verification.setRequestType(RequestType.INITIATE.getKey());
		doShowDialogPage(verification, true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
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
			final HashMap<String, Object> map = new HashMap<String, Object>();
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
				reason.setValue(reasonCode.getDescription());
				reason.setAttribute("reasonId", reasonCode.getId());
			}
		}
	}

	public void onCheck$lv(Event event) {
		final HashMap<String, Object> map = new HashMap<>();
		LegalVerification legalVerification = lv.getSelectedItem().getValue();

		if (legalVerification != null) {
			legalVerification = legalVerificationService.getLegalVerification(legalVerification, "_AView");
		}

		if (legalVerification != null) {
			map.put("LOAN_ORG", true);
			map.put("legalVerification", legalVerification);
			if (lvInquiry.getChildren() != null) {
				lvInquiry.getChildren().clear();
			}
			Executions.createComponents("/WEB-INF/pages/Verification/LegalVerification/LegalVerificationDialog.zul",
					lvInquiry, map);
		} else {
			MessageUtil.showMessage("Initiation request not available in Lagal Verification Module.");
		}

	}

	/**
	 * Method to fill LV Initiation List.
	 * 
	 * @param customer
	 */
	public void renderLVInitiationList() {
		logger.debug(Literal.ENTERING);
		List<Verification> verifications;
		if (initType) {
			verifications = verificationService.getVerifications(this.verification.getKeyReference(),
					VerificationType.LV.getKey());
		} else {
			if (lvInquiry.getChildren() != null) {
				lvInquiry.getChildren().clear();
			}
			verifications = getVerifications();
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
			if (!initType) {
				//Select
				listCell = new Listcell();
				listCell.setId("select".concat(String.valueOf(i)));
				Radio select = new Radio();

				select.setRadiogroup(lv);
				select.setValue(vrf.getLegalVerification());
				listCell.appendChild(select);
				listCell.setParent(item);
			}

			//Collateral Type
			listCell = new Listcell();
			listCell.setId("CollateralType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceFor().concat("-").concat(vrf.getReferenceType())));
			listCell.setParent(item);

			//Agency
			listCell = new Listcell();
			listCell.setId("Agency".concat(String.valueOf(i)));
			Label agency = new Label();
			if (vrf.getAgencyName() != null) {
				agency.setValue(String.valueOf(vrf.getAgencyName()));
				agency.setAttribute("agencyId", vrf.getAgency());
			}
			listCell.appendChild(agency);
			listCell.setParent(item);

			//Remarks
			listCell = new Listcell();
			listCell.setId("IRemarks".concat(String.valueOf(i)));
			Label remarks = new Label(vrf.getRemarks());
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
				fillComboBox(decision, vrf.getDecision(), Decision.getList());
				decision.setParent(listCell);
				listCell.setParent(item);

				//Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("IReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				reInitRemarks.setValue(vrf.getDecisionRemarks());
				reInitRemarks.setMaxlength(500);
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				//ReInitiate
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
			addCollateralDocuments(collaterls);
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
			lvDocument.setDocumentId(document.getId());
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

	public void addCollateralDocuments(List<CollateralAssignment> collaterals) {
		collateralDocuments.clear();
		List<DocumentDetails> documents = getCollateralDocuments(collaterals);

		for (DocumentDetails document : documents) {
			LVDocument lvDocument = new LVDocument();
			lvDocument.setDocumentId(document.getId());
			lvDocument.setDocumentSubId(document.getDocCategory());
			lvDocument.setDocumentType(DocumentType.COLLATRL.getKey());
			lvDocument.setDescription(document.getLovDescDocCategoryName());
			lvDocument.setCollateralRef(document.getReferenceId());

			collateralDocuments.add(lvDocument);
		}
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
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
		Verification vrf = verificationService.getVerificationById(id);
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
		Map<String, Object> arg = getDefaultArguments();
		arg.put("initiation", initiation);
		arg.put("verification", vrf);
		arg.put("legalVerificationListCtrl", this);
		arg.put("financeDetail", financeDetail);
		arg.put("lvDocuments", getDocuments());
		arg.put("financeMainBaseCtrl", this.financeMainDialogCtrl);

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
		} else {
			verifications = getVerifications();
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

			//Collateral Reference
			listCell = new Listcell();
			listCell.setId("CollateralRef".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceFor()));
			listCell.setParent(item);

			//Document Type
			listCell = new Listcell();
			listCell.setId("DocumentType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceType()));
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
			listCell.setId("WRemarks".concat(String.valueOf(i)));
			Textbox remarks = new Textbox(vrf.getRemarks());
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
				listCell.setId("WDecision".concat(String.valueOf(i)));
				Combobox decision = new Combobox();
				decision.setReadonly(true);
				if (Decision.getType(vrf.getDecision()) != null) {
					decision.setValue(String.valueOf(Decision.getType(vrf.getDecision()).getValue()));
				}
				fillComboBox(decision, vrf.getDecision(), Decision.getList());
				decision.setParent(listCell);
				listCell.setParent(item);

				//Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("WReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				reInitRemarks.setValue(vrf.getDecisionRemarks());
				reInitRemarks.setMaxlength(500);
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				//ReInitiate
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
			}

			item.setAttribute("vrf", vrf);
			reason.addForward("onFulfill", self, "onChangeReason", item);

			if (!initType) {
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}

			this.listBoxWaiver.appendChild(item);

		}
		logger.debug(Literal.LEAVING);
	}

	public void onClickReInitiate(ForwardEvent event) throws Exception {
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

	public void onClickInitiate(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();

		// Get the selected entity.
		Verification vrf = (Verification) listitem.getAttribute("verification");

		List<LVDocument> list = getDocuments();

		for (LVDocument LVDocument : list) {
			if (vrf.getReferenceType().equals(LVDocument.getDocumentSubId())) {
				vrf.getLvDocuments().add(LVDocument);
				break;
			}
		}
		if (vrf == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		vrf.setCollateralSetup(collateralSetupDAO.getCollateralSetupByRef(vrf.getReferenceFor(), "_Aview"));
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
	private void doClearMessage() {
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

	public void onChangeInitDecision(ForwardEvent event) throws Exception {
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

	public void onChangeWaiveDecision(ForwardEvent event) throws Exception {
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
		verification.setAgency(Long.parseLong(agency.getAttribute("agencyId").toString()));

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
				if (!combobox.isDisabled() && decision == 0) {
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
					throw new WrongValueException(textbox, "Remarks are mandatory when Decision is Override");
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
				if (!combobox.isDisabled() && decision == 0) {
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
					throw new WrongValueException(textbox, "Remarks are mandatory when Decision is Override");
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

	public void doSave_LVVerification(FinanceDetail financeDetail, Tab tab) throws InterruptedException {
		logger.debug("Entering");

		doClearMessage();
		doSetValidation();

		ArrayList<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);

		this.verification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		financeDetail.setLvVerification(this.verification);

		logger.debug("Leaving");
	}

	private List<Verification> getVerifications() {
		//get verifications
		List<Verification> verifications = verificationService.getVerifications(verification.getKeyReference(),
				VerificationType.LV.getKey());
		//get Legal Verifications
		List<LegalVerification> lvList = legalVerificationService.getList(verification.getKeyReference());
		for (Verification vrf : verifications) {
			for (LegalVerification lv : lvList) {
				if (vrf.getId() == lv.getVerificationId()) {
					vrf.setLegalVerification(lv);
					vrf.setLvDocuments(legalVerificationService.getLVDocuments(vrf.getId()));
					break;
				}
			}
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
		verification.setCreatedOn(DateUtility.getAppDate());
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

}
