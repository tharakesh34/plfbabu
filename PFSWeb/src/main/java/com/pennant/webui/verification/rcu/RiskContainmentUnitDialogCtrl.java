package com.pennant.webui.verification.rcu;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
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
import com.pennant.backend.model.documentdetails.DocumentManager;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.StatuReasons;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.fi.RCUStatus;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.service.RiskContainmentUnitService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Verification/RiskContainmentUnit/RiskContainmentUnitDialog.zul
 * file. <br>
 */
public class RiskContainmentUnitDialogCtrl extends GFCBaseCtrl<RiskContainmentUnit> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(RiskContainmentUnitDialogCtrl.class);

	protected Window window_RiskContainmentUnitDialog;
	protected Tab verificationDetails;
	protected Groupbox gb_basicDetails;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox customerName;
	protected Textbox rcuReference;

	protected Listbox listBoxRiskContainmentUnitDocuments;
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
	
	private RiskContainmentUnit riskContainmentUnit;
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<>();
	private List<RCUDocument> rcuDocumentsList = new ArrayList<>();
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient RiskContainmentUnitListCtrl riskContainmentUnitListCtrl;

	@Autowired
	private transient RiskContainmentUnitService riskContainmentUnitService;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;
	private ExtendedFieldCtrl extendedFieldCtrl = null;

	/**
	 * default constructor.<br>
	 */
	public RiskContainmentUnitDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RiskContainmentUnitDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_RiskContainmentUnitDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_RiskContainmentUnitDialog);

		try {
			// Get the required arguments.
			this.riskContainmentUnit = (RiskContainmentUnit) arguments.get("riskContainmentUnit");

			if (arguments.get("riskContainmentUnitListCtrl") != null) {
				this.riskContainmentUnitListCtrl = (RiskContainmentUnitListCtrl) arguments
						.get("riskContainmentUnitListCtrl");
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (this.riskContainmentUnit == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			RiskContainmentUnit rcu = new RiskContainmentUnit();
			BeanUtils.copyProperties(this.riskContainmentUnit, rcu);
			this.riskContainmentUnit.setBefImage(rcu);

			// Render the page and display the data.
			doLoadWorkFlow(this.riskContainmentUnit.isWorkflow(), this.riskContainmentUnit.getWorkflowId(),
					this.riskContainmentUnit.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.riskContainmentUnit);
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
		Filter reasonFilter[] = new Filter[1];
		reasonFilter[0] = new Filter("ReasonTypecode", StatuReasons.RCUSRES.getKey(), Filter.OP_EQUAL);
		reason.setFilters(reasonFilter);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RiskContainmentUnitList_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RiskContainmentUnitDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RiskContainmentUnitDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RiskContainmentUnitDialog_btnSave"));

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
		doShowNotes(this.riskContainmentUnit);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		riskContainmentUnitListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.riskContainmentUnit.getBefImage());
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
	 * @param rcu
	 * 
	 */
	public void doWriteBeanToComponents(RiskContainmentUnit rcu) {
		logger.debug(Literal.ENTERING);

		this.custCIF.setValue(rcu.getCif());
		this.finReference.setValue(rcu.getKeyReference());
		this.customerName.setValue(rcu.getCustomerName());
		this.rcuReference.setValue(rcu.getRcuReference());

		this.verificationDate.setValue(rcu.getDate());
		this.agentCode.setValue(rcu.getAgentCode());
		this.agentName.setValue(rcu.getAgentName());
		this.recommendations.setValue(String.valueOf(rcu.getStatus()));
		if (!rcu.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty((rcu.getReasonCode())),
					StringUtils.trimToEmpty(rcu.getReasonDesc()));
			if (rcu.getReason() != null) {
				this.reason.setAttribute("ReasonId", rcu.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}

		this.remarks.setValue(rcu.getRemarks());
		fillComboBox(this.recommendations, rcu.getStatus(), RCUStatus.getList());

		this.recordStatus.setValue(rcu.getRecordStatus());
		doFillRCUDocuments(rcu.getRcuDocuments());
		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Verification details
		appendVerificationFieldDetails(rcu);

		logger.debug(Literal.LEAVING);
	}

	private void doFillRCUDocuments(List<RCUDocument> rcuDocuments) {
		logger.debug(Literal.ENTERING);

		this.listBoxRiskContainmentUnitDocuments.getItems().clear();
		if (rcuDocuments != null) {

			int i = 0;
			for (RCUDocument document : rcuDocuments) {
				i++;
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(document.getDocModule());
				lc.setParent(item);
				A docLink = new A();
				docLink.setLabel(document.getDescription());
				lc = new Listcell();
				docLink.addForward("onClick", self, "onClickDoDownload", document);
				docLink.setStyle("text-decoration:underline;");
				lc.appendChild(docLink);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("VerificationType".concat(String.valueOf(i)));
				Combobox verificationType = new Combobox();
				verificationType.setReadonly(isReadOnly("RiskContainmentUnitDialog_VerificationType"));
				verificationType.setValue(document.getVerificationType());
				verificationType.addForward("onChange", self, "onChangeVerificationType", document);
				lc.appendChild(verificationType);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("RCUStatus".concat(String.valueOf(i)));
				Combobox rcuStatus = new Combobox();
				rcuStatus.setReadonly(isReadOnly("RiskContainmentUnitDialog_RCUStatus"));
				rcuStatus.setValue(document.getRcuStatus());
				lc.appendChild(rcuStatus);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("PagesEyeBalled".concat(String.valueOf(i)));
				Intbox pagesEyeBalled = new Intbox();
				pagesEyeBalled.setReadonly(isReadOnly("RiskContainmentUnitDialog_PagesEyeBalled"));
				pagesEyeBalled.setValue(document.getPagesEyeballed());
				pagesEyeBalled.setMaxlength(2);
				lc.appendChild(pagesEyeBalled);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("PagesSampled".concat(String.valueOf(i)));
				Intbox pagesSampled = new Intbox();
				pagesSampled.setReadonly(isReadOnly("RiskContainmentUnitDialog_PagesSampled"));
				pagesSampled.setValue(document.getPagesEyeballed());
				pagesSampled.setMaxlength(2);
				lc.appendChild(pagesSampled);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("Remarks".concat(String.valueOf(i)));
				Textbox remarks = new Textbox();
				remarks.setReadonly(isReadOnly("RiskContainmentUnitDialog_Remarks"));
				remarks.setValue(document.getRemarks());
				remarks.setMaxlength(500);
				remarks.setMultiline(true);
				remarks.setHeight("30px");
				remarks.setWidth("350px");
				lc.appendChild(remarks);
				lc.setParent(item);

				if (!verificationType.isDisabled()) {
					if (verificationType.getValue() == "1") {
						pagesEyeBalled.setDisabled(false);
						pagesSampled.setDisabled(true);
					} else if (verificationType.getValue() == "2") {
						pagesEyeBalled.setDisabled(true);
						pagesSampled.setDisabled(false);
					} else {
						pagesEyeBalled.setDisabled(true);
						pagesSampled.setDisabled(true);
					}
				}
				
				item.setAttribute("data", document);
				this.listBoxRiskContainmentUnitDocuments.appendChild(item);
			}
			setRcuDocumentsList(rcuDocuments);
		}

	}

	/**
	 * To Download the upload Document
	 */
	public void onClickDoDownload(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		RCUDocument details = (RCUDocument) event.getData();

		DocumentManager docDetails = riskContainmentUnitService.getDocumentById(details.getDocRefId());
		AMedia amedia = null;
		if (docDetails.getDocImage() != null) {
			final InputStream data = new ByteArrayInputStream(docDetails.getDocImage());
			String docName = details.getDocName();
			if (details.getDocType().equals(PennantConstants.DOC_TYPE_PDF)) {
				amedia = new AMedia(docName, "pdf", "application/pdf", data);
			} else if (details.getDocType().equals(PennantConstants.DOC_TYPE_IMAGE)) {
				amedia = new AMedia(docName, "jpeg", "image/jpeg", data);
			} else if (details.getDocType().equals(PennantConstants.DOC_TYPE_WORD)
					|| details.getDocType().equals(PennantConstants.DOC_TYPE_MSG)) {
				amedia = new AMedia(docName, "docx", "application/pdf", data);
			}
			Filedownload.save(amedia);

		}
		logger.debug(Literal.LEAVING);
	}

	public void onChangeVerificationType(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		RCUDocument details = (RCUDocument) event.getData();
		List<Component> components = event.getTarget().getChildren();
		Combobox verificationType = null;
		Textbox pagesEyeBalled = null;
		Textbox pagesSampled = null;
		for (Component component : components) {
			Row row = (Row) component;
			if (row != null) {
				verificationType = (Combobox) row.getFirstChild().getNextSibling().getNextSibling();
				pagesEyeBalled = (Textbox) verificationType.getNextSibling().getNextSibling();
				pagesSampled = (Textbox) verificationType.getNextSibling().getNextSibling();
			}
		}
		if (!verificationType.isDisabled()) {
			if (verificationType.getValue() == "1") {
				pagesEyeBalled.setDisabled(false);
				pagesSampled.setDisabled(true);
			} else if (verificationType.getValue() == "2") {
				pagesEyeBalled.setDisabled(true);
				pagesSampled.setDisabled(false);
			} else {
				pagesEyeBalled.setDisabled(true);
				pagesSampled.setDisabled(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append verification extended field details
	 */
	private void appendVerificationFieldDetails(RiskContainmentUnit rcu) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.VERIFICATION_MODULE, ExtendedFieldConstants.VERIFICATION_RCU);

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
			this.observationsFieldTabPanel.setHeight((fieldSize * 37) + "px");

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(String.valueOf(rcu.getVerificationId()), tableName.toString(), "_View");
			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			extendedFieldCtrl.setTab(this.verificationDetails);
			rcu.setExtendedFieldHeader(extendedFieldHeader);
			rcu.setExtendedFieldRender(extendedFieldRender);

			if (rcu.getBefImage() != null) {
				rcu.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				rcu.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("LegalVerificationDialog_LegalVerificationExtFields"));
			extendedFieldCtrl.setWindow(this.window_RiskContainmentUnitDialog);
			extendedFieldCtrl.render();
			this.verificationDetails
					.setLabel(Labels.getLabel("label_LegalVerificationDialog_VerificationDetails.value"));
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
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getRiskContainmentUnit().getDocuments());
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
		map.put("moduleName", VerificationType.RCU.name());
		map.put("enqiryModule", enqiryModule);
		map.put("isEditable", !isReadOnly("RiskContainmentUnitDialog_Documents"));

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
	public void doWriteComponentsToBean(RiskContainmentUnit rcu) throws ParseException {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		rcu.setCif(this.custCIF.getValue());
		rcu.setKeyReference(this.finReference.getValue());
		rcu.setCustomerName(this.customerName.getValue());
		rcu.setRcuReference(this.rcuReference.getValue());

		for (Listitem listitem : listBoxRiskContainmentUnitDocuments.getItems()) {
			try {
				setValue(listitem, "VerificationType");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				setValue(listitem, "RCUStatus");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "PagesEyeBalled");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "PagesSampled");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "Remarks");
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		// Extended Field validations
		if (rcu.getExtendedFieldHeader() != null) {
			rcu.setExtendedFieldRender(extendedFieldCtrl.save());
		}

		try {
			rcu.setDate(this.verificationDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			rcu.setAgentCode(this.agentCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			rcu.setAgentName(this.agentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("0".equals(getComboboxValue(this.recommendations))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_RiskContainmentUnitDialog_Recommendations.value") }));
			} else {
				rcu.setStatus(Integer.parseInt(getComboboxValue(this.recommendations)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			rcu.setReasonDesc(this.reason.getDescription());
			rcu.setReasonCode(this.reason.getValue());
			this.reason.getValidatedValue();
			Object object = this.reason.getAttribute("ReasonId");
			if (object != null) {
				rcu.setReason((Long.parseLong(object.toString())));
			} else {
				rcu.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			rcu.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		rcu.setRcuDocuments(this.rcuDocumentsList);
		doRemoveValidation();

		showErrorDetails(wve, this.verificationDetails);
		logger.debug(Literal.LEAVING);
	}

	private void setValue(Listitem listitem, String comonentId) {
		RCUDocument rcuDocument = null;

		rcuDocument = (RCUDocument) listitem.getAttribute("data");
		switch (comonentId) {
		case "VerificationType":
			rcuDocument.setVerificationType(((Textbox) getComponent(listitem, "VerificationType")).getValue());
			break;
		case "RCUStatus":
			rcuDocument.setRcuStatus(((Textbox) getComponent(listitem, "RCUStatus")).getValue());
			break;
		case "PagesEyeBalled":
			rcuDocument.setPagesEyeballed(((Intbox) getComponent(listitem, "PagesEyeBalled")).getValue());
			break;
		case "PagesSampled":
			rcuDocument.setPagesSampled(((Intbox) getComponent(listitem, "PagesSampled")).getValue());
			break;
		case "Remarks":
			rcuDocument.setRemarks(((Textbox) getComponent(listitem, "Remarks")).getValue());
			break;

		default:
			break;
		}
		rcuDocument.setRecordStatus(this.recordStatus.getValue());
	}

	private org.zkoss.zk.ui.Component getComponent(Listitem listitem, String listcellId) {
		List<Listcell> listcels = listitem.getChildren();

		for (Listcell listcell : listcels) {
			String id = StringUtils.trimToNull(listcell.getId());

			if (id == null) {
				continue;
			}

			id = id.substring(0, id.length() - 1);
			if (StringUtils.equals(id, listcellId)) {
				return listcell.getFirstChild();
			}
		}
		return null;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param rcu
	 *            The entity that need to be render.
	 */
	public void doShowDialog(RiskContainmentUnit rcu) {
		logger.debug(Literal.LEAVING);

		if (rcu.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(rcu.getRecordType())) {
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

		doWriteBeanToComponents(rcu);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.riskContainmentUnit.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("RiskContainmentUnitDialog_VerificationDate"), this.verificationDate);
		readOnlyComponent(isReadOnly("RiskContainmentUnitDialog_AgentCode"), this.agentCode);
		readOnlyComponent(isReadOnly("RiskContainmentUnitDialog_AgentName"), this.agentName);
		readOnlyComponent(isReadOnly("RiskContainmentUnitDialog_Recommendations"), this.recommendations);
		readOnlyComponent(isReadOnly("RiskContainmentUnitDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("RiskContainmentUnitDialog_SummaryRemarks"), this.remarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.riskContainmentUnit.isNewRecord()) {
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
		this.customerName.setReadonly(true);
		this.rcuReference.setReadonly(true);

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
	 * When user clicks on button "Customer CIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustomerDetails(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		HashMap<String, Object> map = new HashMap<String, Object>();

		CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.riskContainmentUnit.getCustId());
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
					new PTDateValidator(Labels.getLabel("label_RiskContainmentUnitDialog_VerificationDate.value"), true,
							DateUtil.getDatePart(riskContainmentUnit.getCreatedOn()),
							DateUtil.getDatePart(DateUtil.getSysDate()), true));
		}
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_RiskContainmentUnitDialog_AgentCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_RiskContainmentUnitDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(
					new PTListValidator(Labels.getLabel("label_RiskContainmentUnitDialog_Recommendations.value"),
							FIStatus.getList(), true));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_RiskContainmentUnitDialog_Remarks.value"),
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
	 * Deletes a RiskContainmentUnit object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final RiskContainmentUnit entity = new RiskContainmentUnit();
		BeanUtils.copyProperties(this.riskContainmentUnit, entity);
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
	 * Saves the components to table. <br>
	 * 
	 * @throws ParseException
	 */
	public void doSave() throws ParseException {
		logger.debug(Literal.ENTERING);
		final RiskContainmentUnit rcu = new RiskContainmentUnit();
		BeanUtils.copyProperties(this.riskContainmentUnit, rcu);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(rcu);

		isNew = rcu.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(rcu.getRecordType())) {
				rcu.setVersion(rcu.getVersion() + 1);
				if (isNew) {
					rcu.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					rcu.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					rcu.setNewRecord(true);
				}
			}
		} else {
			rcu.setVersion(rcu.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Document Details Saving

		if (documentDetailDialogCtrl != null) {
			rcu.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			rcu.setDocuments(getRiskContainmentUnit().getDocuments());
		}

		rcu.setRcuDocuments(getRcuDocumentsList());

		try {
			if (doProcess(rcu, tranType)) {
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
	private boolean doProcess(RiskContainmentUnit riskContainmentUnit, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		riskContainmentUnit.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		riskContainmentUnit.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		riskContainmentUnit.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			riskContainmentUnit.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(riskContainmentUnit.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, riskContainmentUnit);
				}

				if (isNotesMandatory(taskId, riskContainmentUnit)) {
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

			riskContainmentUnit.setTaskId(taskId);
			riskContainmentUnit.setNextTaskId(nextTaskId);
			riskContainmentUnit.setRoleCode(getRole());
			riskContainmentUnit.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (riskContainmentUnit.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = riskContainmentUnit.getExtendedFieldRender();
				details.setReference(String.valueOf(riskContainmentUnit.getVerificationId()));
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(riskContainmentUnit.getRecordStatus());
				details.setRecordType(riskContainmentUnit.getRecordType());
				details.setVersion(riskContainmentUnit.getVersion());
				details.setWorkflowId(riskContainmentUnit.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(riskContainmentUnit.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(riskContainmentUnit.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(riskContainmentUnit.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			// Document Details
			if (riskContainmentUnit.getDocuments() != null && !riskContainmentUnit.getDocuments().isEmpty()) {
				for (DocumentDetails details : riskContainmentUnit.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}

					details.setReferenceId(String.valueOf(riskContainmentUnit.getVerificationId()));
					details.setDocModule(VerificationType.RCU.getCode());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(riskContainmentUnit.getRecordStatus());
					details.setWorkflowId(riskContainmentUnit.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(riskContainmentUnit.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(riskContainmentUnit.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// RCU Document Details
			if (riskContainmentUnit.getRcuDocuments() != null && !riskContainmentUnit.getRcuDocuments().isEmpty()) {
				for (RCUDocument details : riskContainmentUnit.getRcuDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(riskContainmentUnit.getRecordStatus());
					details.setWorkflowId(riskContainmentUnit.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(riskContainmentUnit.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(riskContainmentUnit.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(riskContainmentUnit, tranType);
			String operationRefs = getServiceOperations(taskId, riskContainmentUnit);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(riskContainmentUnit, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(riskContainmentUnit, tranType);
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
		RiskContainmentUnit rcu = (RiskContainmentUnit) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = riskContainmentUnitService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = riskContainmentUnitService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = riskContainmentUnitService.doApprove(auditHeader);

						if (rcu.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = riskContainmentUnitService.doReject(auditHeader);
						if (rcu.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RiskContainmentUnitDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RiskContainmentUnitDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.riskContainmentUnit), true);
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

	private AuditHeader getAuditHeader(RiskContainmentUnit rcu, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, rcu.getBefImage(), rcu);
		return new AuditHeader(getReference(), null, null, null, auditDetail, rcu.getUserDetails(), getOverideMap());
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

	@Override
	protected String getReference() {
		return String.valueOf(this.riskContainmentUnit.getId());
	}

	public RiskContainmentUnit getRiskContainmentUnit() {
		return riskContainmentUnit;
	}

	public void setRiskContainmentUnit(RiskContainmentUnit riskContainmentUnit) {
		this.riskContainmentUnit = riskContainmentUnit;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public RiskContainmentUnitListCtrl getRiskContainmentUnitListCtrl() {
		return riskContainmentUnitListCtrl;
	}

	public void setRiskContainmentUnitListCtrl(RiskContainmentUnitListCtrl riskContainmentUnitListCtrl) {
		this.riskContainmentUnitListCtrl = riskContainmentUnitListCtrl;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public List<RCUDocument> getRcuDocumentsList() {
		return rcuDocumentsList;
	}

	public void setRcuDocumentsList(List<RCUDocument> rcuDocumentsList) {
		this.rcuDocumentsList = rcuDocumentsList;
	}

}