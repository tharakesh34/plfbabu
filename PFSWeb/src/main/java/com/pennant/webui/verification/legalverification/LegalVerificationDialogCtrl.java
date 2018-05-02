package com.pennant.webui.verification.legalverification;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.verification.fieldinvestigation.FieldInvestigationListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Verification/LegalVerification/LegalVerificationDialog.zul file.
 * <br>
 */
public class LegalVerificationDialogCtrl extends GFCBaseCtrl<LegalVerification> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LegalVerificationDialogCtrl.class);

	protected Window window_LegalVerificationDialog;
	protected Tab verificationDetails;
	protected Groupbox gb_basicDetails;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox collateralType;
	protected Textbox collateralReference;

	protected Tabpanel observationsFieldTabPanel;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected String selectMethodName = "onSelectTab";

	protected Datebox verificationDate;
	protected Textbox agentCode;
	protected Textbox agentName;
	protected Combobox recommendations;
	protected ExtendedCombobox reason;
	protected Textbox remarks;

	private LegalVerification legalVerification;
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient FieldInvestigationListCtrl fieldInvestigationListCtrl;

	private ExtendedFieldCtrl extendedFieldCtrl = null;

	@Autowired
	private transient LegalVerificationService legalVerificationService;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;

	/**
	 * default constructor.<br>
	 */
	public LegalVerificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalVerificationDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_LegalVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalVerificationDialog);

		try {
			// Get the required arguments.
			this.legalVerification = (LegalVerification) arguments.get("legalVerification");

			if (arguments.get("legalVerificationListCtrl") != null) {
				this.fieldInvestigationListCtrl = (FieldInvestigationListCtrl) arguments
						.get("legalVerificationListCtrl");
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (this.legalVerification == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			LegalVerification legalVerification = new LegalVerification();
			BeanUtils.copyProperties(this.legalVerification, legalVerification);
			this.legalVerification.setBefImage(legalVerification);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalVerification.isWorkflow(), this.legalVerification.getWorkflowId(),
					this.legalVerification.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			if (!enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalVerification);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.reason.setMaxlength(8);
		this.reason.setMandatoryStyle(false);
		this.reason.setModuleName("FIStatusReason");
		this.reason.setValueColumn("Code");
		this.reason.setDescColumn("Description");
		this.reason.setValidateColumns(new String[] { "Code" });

		this.agentCode.setMaxlength(8);
		this.agentName.setMaxlength(20);
		this.remarks.setMaxlength(500);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalVerificationList_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalVerificationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalVerificationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalVerificationDialog_btnSave"));

		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws ParseException
	 */
	public void onClick$btnSave(Event event) throws ParseException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.legalVerification);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		fieldInvestigationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.legalVerification.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$reason(Event event) {
		logger.debug("Entering");
		Object dataObject = reason.getObject();
		if (dataObject instanceof String) {
			this.reason.setValue(dataObject.toString());
			this.reason.setDescription("");
			this.reason.setAttribute("ReasonId", null);
		} else {
			ReasonCode details = (ReasonCode) dataObject;
			if (details != null) {
				this.reason.setAttribute("ReasonId", details.getId());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param lv
	 * 
	 */
	public void doWriteBeanToComponents(LegalVerification lv) {
		logger.debug(Literal.ENTERING);

		this.custCIF.setValue(lv.getCif());
		this.finReference.setValue(lv.getKeyReference());
		this.collateralType.setValue(lv.getCollateralType());
		this.collateralReference.setValue(lv.getCollateralReference());

		this.verificationDate.setValue(lv.getDate());
		this.agentCode.setValue(lv.getAgentCode());
		this.agentName.setValue(lv.getAgentName());
		this.recommendations.setValue(String.valueOf(lv.getStatus()));
		if (!lv.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty((lv.getReasonCode())),
					StringUtils.trimToEmpty(lv.getReasonDesc()));
			if (lv.getReason() != null) {
				this.reason.setAttribute("ReasonId", lv.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}

		this.remarks.setValue(lv.getRemarks());
		fillComboBox(this.recommendations, lv.getStatus(), FIStatus.getList());

		this.recordStatus.setValue(lv.getRecordStatus());
		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Verification details
		appendVerificationFieldDetails(lv);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append verification field details
	 */
	private void appendVerificationFieldDetails(LegalVerification lv) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.MODULE_NAME, lv.getCollateralType(),
					ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION);

			if (extendedFieldHeader == null) {
				return;
			}
			// Extended Field Details
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_LV");

			List<ExtendedFieldDetail> detailsList = extendedFieldHeader.getExtendedFieldDetails();
			int fieldSize = 0;
			if (detailsList != null && !detailsList.isEmpty()) {
				fieldSize = detailsList.size();
				if (fieldSize != 0) {
					fieldSize = fieldSize / 2;
					fieldSize = fieldSize + 1;
				}
			}
			this.observationsFieldTabPanel.setHeight((fieldSize * 37) + "px");

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(String.valueOf(lv.getVerificationId()), tableName.toString(), "_View");
			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			lv.setExtendedFieldHeader(extendedFieldHeader);
			lv.setExtendedFieldRender(extendedFieldRender);

			if (lv.getBefImage() != null) {
				lv.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				lv.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("LegalVerificationDialog_LegalVerificationExtFields"));/* "TechnicalVerificationDialog_TechVerificationExtFields" */
			extendedFieldCtrl.setWindow(this.window_LegalVerificationDialog);
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	private void appendDocumentDetailTab() {
		logger.debug("Entering");
		createTab("DOCUMENTDETAIL", true);
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getLegalVerification().getDocuments());
		map.put("module", DocumentCategories.VERIFICATION_FI.getKey());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel("DOCUMENTDETAIL"), map);
		logger.debug("Leaving");
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", VerificationType.TV.name());
		map.put("enqiryModule", enqiryModule);
		map.put("isEditable", !isReadOnly("LegalVerificationDialog_Documents"));

		return map;
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @throws ParseException
	 */
	public void doWriteComponentsToBean(LegalVerification lv) throws ParseException {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		lv.setCif(this.custCIF.getValue());
		lv.setKeyReference(this.finReference.getValue());
		lv.setCollateralType(this.collateralType.getValue());
		lv.setCollateralReference(this.collateralReference.getValue());

		// Extended Field validations
		if (lv.getExtendedFieldHeader() != null) {
			lv.setExtendedFieldRender(extendedFieldCtrl.save());
		}

		try {
			lv.setDate(this.verificationDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			lv.setAgentCode(this.agentCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			lv.setAgentName(this.agentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("0".equals(getComboboxValue(this.recommendations))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FieldInvestigationDialog_Recommendations.value") }));
			} else {
				lv.setStatus(Integer.parseInt(getComboboxValue(this.recommendations)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			lv.setReasonDesc(this.reason.getDescription());
			lv.setReasonCode(this.reason.getValue());
			this.reason.getValidatedValue();
			Object object = this.reason.getAttribute("ReasonId");
			if (object != null) {
				lv.setReason((Long.parseLong(object.toString())));
			} else {
				lv.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			lv.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		showErrorDetails(wve, this.verificationDetails);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param legalVerification
	 *            The entity that need to be render.
	 */
	public void doShowDialog(LegalVerification legalVerification) {
		logger.debug(Literal.LEAVING);

		if (legalVerification.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(legalVerification.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(legalVerification);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "Customer CIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustomerDetails(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		HashMap<String, Object> map = new HashMap<String, Object>();

		CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.legalVerification.getCustId());
		if (customerDetails != null) {
			map.put("customerDetails", customerDetails);
			map.put("isEnqProcess", true);
			map.put("CustomerEnq", true);
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul", null, map);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);
		
		if (this.verificationDate.isVisible() && !this.verificationDate.isReadonly()) {
			this.verificationDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_LegalVerificationDialog_VerificationDate.value"), true,
							DateUtil.getDatePart(legalVerification.getCreatedOn()),
							DateUtil.getDatePart(DateUtil.getSysDate()), true));
		}
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalVerificationDialog_AgentCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalVerificationDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(new PTListValidator(
					Labels.getLabel("label_LegalVerificationDialog_Recommendations.value"), FIStatus.getList(), true));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalVerificationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.agentCode.setConstraint("");
		this.agentName.setConstraint("");
		this.recommendations.setConstraint("");
		this.reason.setConstraint("");
		this.remarks.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a FieldInvestigation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalVerification entity = new LegalVerification();
		BeanUtils.copyProperties(this.legalVerification, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ entity.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(entity.getRecordType()).equals("")) {
				entity.setVersion(entity.getVersion() + 1);
				entity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					entity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					entity.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), entity.getNextTaskId(), entity);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(entity, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.legalVerification.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("LegalVerificationDialog_VerificationDate"), this.verificationDate);
		readOnlyComponent(isReadOnly("LegalVerificationDialog_AgentCode"), this.agentCode);
		readOnlyComponent(isReadOnly("LegalVerificationDialog_AgentName"), this.agentName);
		readOnlyComponent(isReadOnly("LegalVerificationDialog_Recommendations"), this.recommendations);
		readOnlyComponent(isReadOnly("LegalVerificationDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("LegalVerificationDialog_AgentRemarks"), this.remarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.legalVerification.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.custCIF.setReadonly(true);
		this.finReference.setReadonly(true);
		this.collateralType.setReadonly(true);
		this.collateralReference.setReadonly(true);

		this.agentCode.setReadonly(true);
		this.agentName.setReadonly(true);
		this.recommendations.setDisabled(true);
		this.reason.setReadonly(true);
		this.remarks.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			if (!enqiryModule) {
				this.userAction.setSelectedIndex(0);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws ParseException
	 */
	public void doSave() throws ParseException {
		logger.debug(Literal.ENTERING);
		final LegalVerification lv = new LegalVerification();
		BeanUtils.copyProperties(this.legalVerification, lv);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(lv);

		isNew = lv.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(lv.getRecordType())) {
				lv.setVersion(lv.getVersion() + 1);
				if (isNew) {
					lv.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					lv.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					lv.setNewRecord(true);
				}
			}
		} else {
			lv.setVersion(lv.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Document Details Saving

		if (documentDetailDialogCtrl != null) {
			lv.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			lv.setDocuments(getLegalVerification().getDocuments());
		}

		try {
			if (doProcess(lv, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(LegalVerification legalVerification, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		legalVerification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		legalVerification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		legalVerification.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			legalVerification.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(legalVerification.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, legalVerification);
				}

				if (isNotesMandatory(taskId, legalVerification)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			legalVerification.setTaskId(taskId);
			legalVerification.setNextTaskId(nextTaskId);
			legalVerification.setRoleCode(getRole());
			legalVerification.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (legalVerification.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = legalVerification.getExtendedFieldRender();
				details.setReference(String.valueOf(legalVerification.getVerificationId()));
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(legalVerification.getRecordStatus());
				details.setRecordType(legalVerification.getRecordType());
				details.setVersion(legalVerification.getVersion());
				details.setWorkflowId(legalVerification.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(legalVerification.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(legalVerification.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(legalVerification.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			// Document Details
			if (legalVerification.getDocuments() != null && !legalVerification.getDocuments().isEmpty()) {
				for (DocumentDetails details : legalVerification.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}

					details.setReferenceId(String.valueOf(legalVerification.getVerificationId()));
					details.setDocModule(VerificationType.LV.getCode());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(legalVerification.getRecordStatus());
					details.setWorkflowId(legalVerification.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(legalVerification.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(legalVerification.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(legalVerification, tranType);
			String operationRefs = getServiceOperations(taskId, legalVerification);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(legalVerification, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(legalVerification, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = legalVerificationService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = legalVerificationService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = legalVerificationService.doApprove(auditHeader);

						if (fieldInvestigation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = legalVerificationService.doReject(auditHeader);
						if (fieldInvestigation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_LegalVerificationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_LegalVerificationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.legalVerification), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LegalVerification legalVerification, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, legalVerification.getBefImage(), legalVerification);
		return new AuditHeader(getReference(), null, null, null, auditDetail, legalVerification.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.legalVerification.getId());
	}

	public FieldInvestigationListCtrl getFieldInvestigationListCtrl() {
		return fieldInvestigationListCtrl;
	}

	public void setFieldInvestigationListCtrl(FieldInvestigationListCtrl fieldInvestigationListCtrl) {
		this.fieldInvestigationListCtrl = fieldInvestigationListCtrl;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
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

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public LegalVerification getLegalVerification() {
		return legalVerification;
	}

	public void setLegalVerification(LegalVerification legalVerification) {
		this.legalVerification = legalVerification;
	}

}
