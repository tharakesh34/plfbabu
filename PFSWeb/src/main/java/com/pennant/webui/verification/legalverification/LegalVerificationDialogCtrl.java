package com.pennant.webui.verification.legalverification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.StatuReasons;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.LVStatus;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Verification/LegalVerification/LegalVerificationDialog.zul file.
 * <br>
 */
public class LegalVerificationDialogCtrl extends GFCBaseCtrl<LegalVerification> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalVerificationDialogCtrl.class);

	protected Window window_LegalVerificationDialog;
	protected Tab verificationDetails;
	protected Groupbox gb_basicDetails;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox collateralType;
	protected Textbox collateralReference;

	protected Listbox listBoxLegalVerificationDocuments;
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
	protected North north;
	protected South south;
	protected Space space_AgentCode;
	protected Space space_AgentName;

	private boolean fromLoanOrg;

	private LegalVerification legalVerification;
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private List<LVDocument> lvDocumentsList = new ArrayList<>();
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient LegalVerificationListCtrl legalVerificationListCtrl;

	private ExtendedFieldCtrl extendedFieldCtrl = null;

	@Autowired
	private transient LegalVerificationService legalVerificationService;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;
	@Autowired
	private transient CollateralSetupService collateralSetupService;

	protected Button btnSearchCustomerDetails;

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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalVerificationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalVerificationDialog);

		try {
			// Get the required arguments.
			this.legalVerification = (LegalVerification) arguments.get("legalVerification");

			if (arguments.get("legalVerificationListCtrl") != null) {
				this.legalVerificationListCtrl = (LegalVerificationListCtrl) arguments.get("legalVerificationListCtrl");
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (this.legalVerification == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.get("LOAN_ORG") != null) {
				fromLoanOrg = true;
				enqiryModule = true;
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			// Store the before image.
			LegalVerification legalVerification = new LegalVerification();
			BeanUtils.copyProperties(this.legalVerification, legalVerification);
			this.legalVerification.setBefImage(legalVerification);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalVerification.isWorkflow(), this.legalVerification.getWorkflowId(),
					this.legalVerification.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else if (fromLoanOrg) {
				setWorkFlowEnabled(true);
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
		this.reason.setModuleName("VerificationReasons");
		this.reason.setValueColumn("Code");
		this.reason.setDescColumn("Description");
		this.reason.setValidateColumns(new String[] { "Code" });

		Filter[] reasonFilter = new Filter[1];
		if (ImplementationConstants.VER_REASON_CODE_FILTER_BY_REASONTYPE) {
			reasonFilter[0] = new Filter("ReasonTypecode", null, Filter.OP_EQUAL);
		} else {
			reasonFilter[0] = new Filter("ReasonTypecode", StatuReasons.LVSRES.getKey(), Filter.OP_EQUAL);
		}
		reason.setFilters(reasonFilter);

		this.verificationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.agentCode.setMaxlength(8);
		this.agentName.setMaxlength(20);
		this.remarks.setMaxlength(500);

		if (StringUtils.equals(SysParamUtil.getValueAsString(SMTParameterConstants.VERIFICATIONS_CUSTOMERVIEW),
				PennantConstants.YES)) {
			this.btnSearchCustomerDetails.setVisible(false);
		} else {
			this.btnSearchCustomerDetails.setVisible(true);
		}
		this.space_AgentCode.setVisible(!ImplementationConstants.VER_INIT_AGENT_MANDATORY);
		this.space_AgentName.setVisible(!ImplementationConstants.VER_INIT_AGENT_MANDATORY);
		setStatusDetails();

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
		readOnlyComponent(isReadOnly("LegalVerificationDialog_Remarks"), this.remarks);

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
	 * @param event An event sent to the event handler of the component.
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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.legalVerification);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		legalVerificationListCtrl.search();
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
		if (dataObject instanceof String || dataObject == null) {
			this.reason.setValue("");
			this.reason.setDescription("");
			this.reason.setAttribute("ReasonId", null);
		} else {
			ReasonCode details = (ReasonCode) dataObject;
			this.reason.setAttribute("ReasonId", details.getId());
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
		this.collateralReference.setValue(lv.getReferenceFor());

		this.verificationDate.setValue(lv.getVerificationDate());
		if (!fromLoanOrg) {
			if (getFirstTaskOwner().equals(getRole()) && lv.getVerificationDate() == null) {
				this.verificationDate.setValue(SysParamUtil.getAppDate());
			}
		}
		this.agentCode.setValue(lv.getAgentCode());
		this.agentName.setValue(lv.getAgentName());
		this.recommendations.setValue(String.valueOf(lv.getStatus()));
		if (!lv.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty(lv.getReasonCode()),
					StringUtils.trimToEmpty(lv.getReasonDesc()));
			if (lv.getReason() != null) {
				this.reason.setAttribute("ReasonId", lv.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}
		if (!lv.isNewRecord()) {
			visibleComponent(lv.getStatus());
		}
		this.remarks.setValue(lv.getRemarks());
		fillComboBox(this.recommendations, lv.getStatus(), LVStatus.getList());

		this.recordStatus.setValue(lv.getRecordStatus());
		doFillLVDocuments(lv.getLvDocuments());
		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Verification details
		appendVerificationFieldDetails(lv);

		logger.debug(Literal.LEAVING);
	}

	private void doFillLVDocuments(List<LVDocument> lvDocuments) {
		logger.debug("Entering");

		this.listBoxLegalVerificationDocuments.getItems().clear();
		if (lvDocuments != null) {

			int i = 0;
			for (LVDocument document : lvDocuments) {
				i++;
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(document.getDocModule());
				lc.setParent(item);
				if (document.getDocumentType() == DocumentType.COLLATRL.getKey()) {
					Label label = new Label();
					label.setValue(" - " + document.getReferenceId());
					lc.appendChild(label);
					lc.setParent(item);
				}

				lc = new Listcell();
				A docLink = new A();
				docLink.setLabel(document.getDescription());
				docLink.addForward("onClick", self, "onClickDoDownload", document);
				docLink.setStyle("text-decoration:underline;");
				lc.appendChild(docLink);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("RemarksFin".concat(String.valueOf(i)));
				Textbox remarks1 = new Textbox();
				remarks1.setReadonly(isReadOnly("LegalVerificationDialog_Remarks1"));
				remarks1.setValue(document.getRemarks1());
				remarks1.setMaxlength(500);
				remarks1.setMultiline(true);
				remarks1.setHeight("30px");
				remarks1.setWidth("350px");
				lc.appendChild(remarks1);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("RemarksCol".concat(String.valueOf(i)));
				Textbox remarks2 = new Textbox();
				remarks2.setReadonly(isReadOnly("LegalVerificationDialog_Remarks2"));
				remarks2.setValue(document.getRemarks2());
				remarks2.setMaxlength(500);
				remarks2.setMultiline(true);
				remarks2.setHeight("30px");
				remarks2.setWidth("350px");
				lc.appendChild(remarks2);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("RemarksCust".concat(String.valueOf(i)));
				Textbox remarks3 = new Textbox();
				remarks3.setReadonly(isReadOnly("LegalVerificationDialog_Remarks3"));
				remarks3.setValue(document.getRemarks3());
				remarks3.setMaxlength(500);
				remarks3.setMultiline(true);
				remarks3.setHeight("30px");
				remarks3.setWidth("350px");
				lc.appendChild(remarks3);
				lc.setParent(item);

				item.setAttribute("data", document);
				this.listBoxLegalVerificationDocuments.appendChild(item);
			}
			setLvDocumentsList(lvDocuments);
		}
	}

	/**
	 * To Download the upload Document
	 */
	public void onClickDoDownload(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		LVDocument details = (LVDocument) event.getData();

		DocumentManager docDetails = legalVerificationService.getDocumentById(details.getDocRefID());
		AMedia amedia = null;
		if (docDetails != null && docDetails.getDocImage() != null) {
			final InputStream data = new ByteArrayInputStream(docDetails.getDocImage());
			String docName = details.getDocName();
			if (details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_PDF)) {
				amedia = new AMedia(docName, "pdf", "application/pdf", data);
			} else if (details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_IMAGE)) {
				amedia = new AMedia(docName, "jpeg", "image/jpeg", data);
			} else if (details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_WORD)
					|| details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_MSG)) {
				amedia = new AMedia(docName, "docx", "application/pdf", data);
			} else if (details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_ZIP)) {
				amedia = new AMedia(docName, "x-zip-compressed", "application/x-zip-compressed", data);
			} else if (details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_7Z)) {
				amedia = new AMedia(docName, "octet-stream", "application/octet-stream", data);
			} else if (details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_RAR)) {
				amedia = new AMedia(docName, "x-rar-compressed", "application/x-rar-compressed", data);
			} else if (details.getDocType().equalsIgnoreCase(PennantConstants.DOC_TYPE_JPG)) {
				amedia = new AMedia(docName, "jpeg", "image/jpeg", data);
			}
			Filedownload.save(amedia);

		} else {
			MessageUtil.showMessage("Document details not available.");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append verification extended field details
	 */
	private void appendVerificationFieldDetails(LegalVerification lv) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.VERIFICATION_MODULE, ExtendedFieldConstants.VERIFICATION_LV);

			if (extendedFieldHeader == null) {
				return;
			}
			// Extended Field Details
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			List<ExtendedFieldDetail> detailsList = extendedFieldHeader.getExtendedFieldDetails();
			int fieldSize = 0;
			if (detailsList != null && !detailsList.isEmpty()) {
				fieldSize = detailsList.size();
				if (fieldSize != 0) {
					fieldSize = fieldSize / 2;
					fieldSize = fieldSize + 1;
				}
			}

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(String.valueOf(lv.getVerificationId()), tableName.toString(), "_View");
			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			extendedFieldCtrl.setTab(this.verificationDetails);
			lv.setExtendedFieldHeader(extendedFieldHeader);
			lv.setExtendedFieldRender(extendedFieldRender);

			if (lv.getBefImage() != null) {
				lv.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				lv.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("LegalVerificationDialog_LegalVerificationExtFields"));
			extendedFieldCtrl.setWindow(this.window_LegalVerificationDialog);
			extendedFieldCtrl.render();
			this.verificationDetails
					.setLabel(Labels.getLabel("label_LegalVerificationDialog_VerificationDetails.value"));
			this.observationsFieldTabPanel.setHeight((fieldSize * 37) + "px");
		} catch (Exception e) {
			closeDialog();
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
		final Map<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getLegalVerification().getDocuments());
		map.put("module", DocumentCategories.VERIFICATION_LV.getKey());
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

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", VerificationType.LV.name());
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
		ComponentsCtrl.applyForward(tab, "onSelect=" + selectMethodName);
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
		lv.setReferenceFor(this.collateralReference.getValue());

		for (Listitem listitem : listBoxLegalVerificationDocuments.getItems()) {
			try {
				setValue(listitem, "RemarksFin");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				setValue(listitem, "RemarksCol");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "RemarksCust");
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// Extended Field validations
		if (lv.getExtendedFieldHeader() != null) {
			lv.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		try {
			Calendar calDate = Calendar.getInstance();
			if (this.verificationDate.getValue() != null) {
				calDate.setTime(this.verificationDate.getValue());
				Calendar calTimeNow = Calendar.getInstance();
				calDate.set(Calendar.HOUR_OF_DAY, calTimeNow.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calTimeNow.get(Calendar.MINUTE));
				calDate.set(Calendar.SECOND, calTimeNow.get(Calendar.SECOND));
				lv.setVerificationDate(new Timestamp(calDate.getTimeInMillis()));
			} else {
				lv.setVerificationDate(SysParamUtil.getAppDate());
			}

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
			if (!this.recommendations.isDisabled()
					&& LVStatus.SELECT.getKey().equals(Integer.parseInt(getComboboxValue(this.recommendations)))) {
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
				lv.setReason(Long.parseLong(object.toString()));
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

		lv.setLvDocuments(this.lvDocumentsList);
		doRemoveValidation();

		showErrorDetails(wve, this.verificationDetails);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$recommendations(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.reason.setErrorMessage("");
		String type = this.recommendations.getSelectedItem().getValue();
		visibleComponent(Integer.parseInt(type));
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void visibleComponent(Integer type) {
		String reasonType = null;
		if (type == LVStatus.NEGATIVE.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.LVNTVRTY.getKey();
		} else {
			this.reason.setMandatoryStyle(false);
			reasonType = StatuReasons.LVPOSTVRTY.getKey();
		}

		if (ImplementationConstants.VER_REASON_CODE_FILTER_BY_REASONTYPE) {
			Filter[] reasonFilter = new Filter[1];
			reasonFilter[0] = new Filter("ReasonTypecode", reasonType, Filter.OP_EQUAL);
			this.reason.setFilters(reasonFilter);
		}

	}

	private void setValue(Listitem listitem, String componentId) {
		LVDocument lvDoc = null;

		lvDoc = (LVDocument) listitem.getAttribute("data");
		switch (componentId) {
		case "RemarksFin":
			lvDoc.setRemarks1(((Textbox) getComponent(listitem, "RemarksFin")).getValue());
			break;
		case "RemarksCol":
			lvDoc.setRemarks2(((Textbox) getComponent(listitem, "RemarksCol")).getValue());
			break;
		case "RemarksCust":
			lvDoc.setRemarks3(((Textbox) getComponent(listitem, "RemarksCust")).getValue());
			break;

		default:
			break;
		}
		lvDoc.setRecordStatus(this.recordStatus.getValue());
	}

	private Component getComponent(Listitem listitem, String listcellId) {
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

	/**
	 * Displays the dialog page.
	 * 
	 * @param legalVerification The entity that need to be render.
	 */
	public void doShowDialog(LegalVerification legalVerification) {
		logger.debug(Literal.LEAVING);

		if (legalVerification.isNewRecord()) {
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

		if (fromLoanOrg) {
			north.setVisible(false);
			south.setVisible(false);
		}

		doWriteBeanToComponents(legalVerification);
		if (!fromLoanOrg) {
			setDialog(DialogType.EMBEDDED);
		} else {
			window_LegalVerificationDialog.setHeight("100%");
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "Customer CIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustomerDetails(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Map<String, Object> map = new HashMap<String, Object>();

		CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.legalVerification.getCustId());
		String pageName = PennantAppUtil.getCustomerPageName();
		if (customerDetails != null) {
			map.put("customerDetails", customerDetails);
			map.put("isEnqProcess", true);
			map.put("CustomerEnq", true);
			Executions.createComponents(pageName, null, map);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * When user clicks on button "Collateral Reference" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCollateralRef(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Map<String, Object> map = new HashMap<String, Object>();

		CollateralSetup collateralSetup = collateralSetupService
				.getCollateralSetupByRef(this.collateralReference.getValue(), "", true);
		if (collateralSetup != null) {
			map.put("collateralSetup", collateralSetup);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null,
					map);
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
							DateUtil.getDatePart(SysParamUtil.getAppDate()), true));
		}
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalVerificationDialog_AgentCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM,
							ImplementationConstants.VER_INIT_AGENT_MANDATORY));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_LegalVerificationDialog_AgentName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, ImplementationConstants.VER_INIT_AGENT_MANDATORY));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalVerificationDialog_Recommendations.value"), LVStatus.getList(), true));
		}
		if (!this.reason.isReadonly()) {
			this.reason
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalVerificationDialog_Reason.value"),
							null, this.reason.isMandatory(), true));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalVerificationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.verificationDate.setConstraint("");
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

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalVerification entity = new LegalVerification();
		BeanUtils.copyProperties(this.legalVerification, entity);

		doDelete(String.valueOf(entity.getVerificationId()), entity);

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

		isNew = lv.isNewRecord();
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

		if (SysParamUtil.isAllowed(SMTParameterConstants.LV_DOCUMENT_MANDATORY)
				&& this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("submit")) {
			if (documentDetailDialogCtrl != null
					&& CollectionUtils.sizeIsEmpty(documentDetailDialogCtrl.getDocumentDetailsList())) {
				MessageUtil.showError(Labels.getLabel("VERIFICATIONS_DOCUMENT_MANDATORY"));
				return;
			}
		}

		if (documentDetailDialogCtrl != null) {
			lv.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			lv.setDocuments(getLegalVerification().getDocuments());
		}

		lv.setLvDocuments(getLvDocumentsList());

		try {
			if (doProcess(lv, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(lv.getRoleCode(), lv.getNextRoleCode(),
						lv.getKeyReference(), " Loan ", lv.getRecordStatus(), getNextTaskId());
				Clients.showNotification(msg, "info", null, null, -1);
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
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(LegalVerification legalVerification, String tranType) {
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
					details.setCustomerCif(legalVerification.getCif());
					details.setFinReference(legalVerification.getKeyReference());
					if (PennantConstants.RECORD_TYPE_DEL.equals(legalVerification.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(legalVerification.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// LV Document Details
			if (legalVerification.getLvDocuments() != null && !legalVerification.getLvDocuments().isEmpty()) {
				for (LVDocument details : legalVerification.getLvDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}

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
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		LegalVerification legalVerification = (LegalVerification) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

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

					if (legalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = legalVerificationService.doReject(auditHeader);
					if (legalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		return String.valueOf(this.legalVerification.getVerificationId());
	}

	public LegalVerificationListCtrl getLegalVerificationListCtrl() {
		return legalVerificationListCtrl;
	}

	public void setLegalVerificationListCtrl(LegalVerificationListCtrl legalVerificationListCtrl) {
		this.legalVerificationListCtrl = legalVerificationListCtrl;
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

	public List<LVDocument> getLvDocumentsList() {
		return lvDocumentsList;
	}

	public void setLvDocumentsList(List<LVDocument> lvDocumentsList) {
		this.lvDocumentsList = lvDocumentsList;
	}
}
