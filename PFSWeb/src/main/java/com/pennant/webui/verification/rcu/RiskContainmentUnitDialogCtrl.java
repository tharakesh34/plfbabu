package com.pennant.webui.verification.rcu;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
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
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
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
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.StatuReasons;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.RCUDocStatus;
import com.pennanttech.pennapps.pff.verification.fi.RCUDocVerificationType;
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

	private static final Logger logger = LogManager.getLogger(RiskContainmentUnitDialogCtrl.class);

	protected Window window_RiskContainmentUnitDialog;
	protected Tab verificationDetails;
	protected Groupbox gb_basicDetails;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox customerName;

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

	protected North north;
	protected South south;
	protected Space space_AgentCode;
	protected Space space_AgentName;

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

	private boolean fromLoanOrg;

	protected Button btnSearchCustomerDetails;
	private DMSService dMSService;

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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_RiskContainmentUnitDialog(Event event) {
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

			if (arguments.get("LOAN_ORG") != null) {
				fromLoanOrg = true;
				enqiryModule = true;
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
			} else if (fromLoanOrg) {
				setWorkFlowEnabled(true);
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

		Filter[] reasonFilter = new Filter[1];
		if (ImplementationConstants.VER_REASON_CODE_FILTER_BY_REASONTYPE) {
			reasonFilter[0] = new Filter("ReasonTypecode", null, Filter.OP_EQUAL);
		} else {
			reasonFilter[0] = new Filter("ReasonTypecode", StatuReasons.RCUSRES.getKey(), Filter.OP_EQUAL);
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

		this.space_AgentCode.setVisible(!ImplementationConstants.VER_INIT_FROM_OUTSIDE);
		this.space_AgentName.setVisible(!ImplementationConstants.VER_INIT_FROM_OUTSIDE);
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
		doShowNotes(this.riskContainmentUnit);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
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
		logger.debug(Literal.ENTERING);
		Object dataObject = reason.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.reason.setValue("");
			this.reason.setDescription("");
			this.reason.setAttribute("ReasonId", null);
		} else {
			ReasonCode details = (ReasonCode) dataObject;
			this.reason.setAttribute("ReasonId", details.getId());
		}
		logger.debug(Literal.LEAVING);
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
		this.customerName.setValue(rcu.getCustName());

		this.verificationDate.setValue(rcu.getVerificationDate());
		if (!fromLoanOrg) {
			if (getFirstTaskOwner().equals(getRole()) && rcu.getVerificationDate() == null) {
				this.verificationDate.setValue(SysParamUtil.getAppDate());
			}
		}
		this.agentCode.setValue(rcu.getAgentCode());
		this.agentName.setValue(rcu.getAgentName());
		this.recommendations.setValue(String.valueOf(rcu.getStatus()));
		if (!rcu.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty(rcu.getReasonCode()),
					StringUtils.trimToEmpty(rcu.getReasonDesc()));
			if (rcu.getReason() != null) {
				this.reason.setAttribute("ReasonId", rcu.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}
		if (!rcu.isNewRecord()) {
			visibleComponent(rcu.getStatus());
		}

		this.remarks.setValue(rcu.getRemarks());
		fillComboBox(this.recommendations, rcu.getStatus(), RCUStatus.getList());

		this.recordStatus.setValue(rcu.getRecordStatus());
		doFillRCUDocuments(rcu.getRcuDocuments());
		// Document Detail Tab Addition
		appendDocumentDetailTab();

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
				if (document.getDocumentType() == DocumentType.COLLATRL.getKey()) {
					Label label = new Label();
					label.setValue(" - " + document.getReferenceId());
					lc.appendChild(label);
					lc.setParent(item);
				} else if (document.getDocumentType() == DocumentType.COAPPLICANT.getKey()) {
					lc.setLabel(" CoApplicant-  " + document.getReferenceId());
				} else if (document.getDocumentType() == DocumentType.CUSTOMER.getKey()) {
					lc.setLabel(" Customer- " + document.getReferenceId());
				}

				lc = new Listcell();
				A docLink = new A();
				docLink.setLabel(document.getDescription());
				docLink.addForward("onClick", self, "onClickDoDownload", document);
				docLink.setStyle("text-decoration:underline;");
				lc.appendChild(docLink);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("VerificationType".concat(String.valueOf(i)));
				Combobox verificationType = new Combobox();
				verificationType.setDisabled(isReadOnly("RiskContainmentUnitDialog_VerificationType"));
				verificationType.setReadonly(true);
				verificationType.setValue(String.valueOf(document.getVerificationType()));
				verificationType.addForward("onChange", self, "onChangeVerificationType", item);
				lc.appendChild(verificationType);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("Status".concat(String.valueOf(i)));
				Combobox rcuDocStatus = new Combobox();
				rcuDocStatus.setDisabled(isReadOnly("RiskContainmentUnitDialog_RCUStatus"));
				rcuDocStatus.setReadonly(true);
				rcuDocStatus.setValue(String.valueOf(document.getStatus()));
				lc.appendChild(rcuDocStatus);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("PagesEyeBalled".concat(String.valueOf(i)));
				Intbox pagesEyeBalled = new Intbox();
				pagesEyeBalled.setReadonly(isReadOnly("RiskContainmentUnitDialog_PagesEyeBalled"));
				pagesEyeBalled.setValue(document.getPagesEyeballed());
				pagesEyeBalled.setMaxlength(4);
				pagesEyeBalled.setConstraint("no empty: Pages Eyeballed must be greater than zero");
				lc.appendChild(pagesEyeBalled);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("PagesSampled".concat(String.valueOf(i)));
				Intbox pagesSampled = new Intbox();
				pagesSampled.setReadonly(isReadOnly("RiskContainmentUnitDialog_PagesSampled"));
				pagesSampled.setValue(document.getPagesSampled());
				pagesSampled.setMaxlength(4);
				pagesSampled.setConstraint("no empty: Pages Sampled must be greater than zero");
				lc.appendChild(pagesSampled);
				lc.setParent(item);

				lc = new Listcell();
				lc.setId("Remarks".concat(String.valueOf(i)));
				Textbox remarks = new Textbox();
				remarks.setReadonly(isReadOnly("RiskContainmentUnitDialog_Remarks"));
				remarks.setValue(document.getAgentRemarks());
				remarks.setMaxlength(500);
				remarks.setMultiline(true);
				remarks.setHeight("30px");
				remarks.setWidth("350px");
				lc.appendChild(remarks);
				lc.setParent(item);
				int type = document.getVerificationType();
				if (ImplementationConstants.RCU_DOC_FIELDS_DISABLED) {
					type = 1;// Defaulting to Sampled and disabling
					verificationType.setDisabled(true);
					rcuDocStatus.setDisabled(true);
					pagesEyeBalled.setReadonly(true);
					pagesSampled.setReadonly(true);
				}
				fillComboBox(verificationType, type, RCUDocVerificationType.getList());
				fillComboBox(rcuDocStatus, document.getStatus(), RCUDocStatus.getList());
				readOnlyComponents(verificationType, pagesEyeBalled, pagesSampled);

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

		DocumentManager docDetails = riskContainmentUnitService.getDocumentById(details.getDocumentRefId());

		Long docRefId = details.getDocumentRefId();
		String docName = details.getDocName();
		String docUri = details.getDocumentUri();
		if (StringUtils.isNotBlank(docUri)) {
			DocumentDetails dd = dMSService.getExternalDocument(this.custCIF.getValue(), docName, docUri);
			docDetails.setDocImage(dd.getDocImage());
			details.setDocName(dd.getDocName());
		} else {
			if (docDetails.getDocImage() == null) {
				if (docRefId != null && docRefId != Long.MIN_VALUE) {
					docDetails.setDocImage(dMSService.getById(docRefId));
				}
			}
		}

		if (docDetails != null && docDetails.getDocImage() != null) {
			downloadFile(details.getDocType(), docDetails.getDocImage(), details.getDocName());
		} else {
			MessageUtil.showMessage("Document details not available.");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Showing the RCU Documents extended field details
	 */
	public void onClickExtendedDetails(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		RCUDocument details = (RCUDocument) event.getData();
		A extDetailsLink = (A) event.getOrigin().getTarget();
		Clients.clearWrongValue(extDetailsLink);

		if (details.getExtendedFieldRender() == null) {
			MessageUtil.showMessage("Extended details are not configured for the Document  :" + details.getCode()
					+ " - " + details.getDescription());
			return;
		}

		ExtendedFieldHeader fieldHeader = details.getExtendedFieldHeader();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("riskContainmentUnitModule", true);
		map.put("extendedFieldHeader", fieldHeader);
		map.put("extendedFieldRender", details.getExtendedFieldRender());
		map.put("ccyFormat", PennantConstants.defaultCCYDecPos);
		map.put("preValidationScript", fieldHeader.getPreValidation());
		map.put("postValidationScript", fieldHeader.getPostValidation());

		if (fromLoanOrg) {
			map.put("isReadOnly", true);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
		} else {
			map.put("isReadOnly", getUserWorkspace().isAllowed("RiskContainmentUnitDialog_VerificationDate"));
		}

		if (details.getExtendedFieldRender().getMapValues() != null) {
			map.put("newRecord", false);
		} else {
			map.put("newRecord", true);
		}
		map.put("maxSizeWindow", false);

		Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldCaptureDialog.zul",
				window_RiskContainmentUnitDialog, map);

		logger.debug(Literal.LEAVING);
	}

	public void onChangeVerificationType(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem listitem = (Listitem) event.getData();
		Combobox verificationType = (Combobox) getComponent(listitem, "VerificationType");

		Intbox pagesEyeBalled = (Intbox) getComponent(listitem, "PagesEyeBalled");
		Intbox pagesSampled = (Intbox) getComponent(listitem, "PagesSampled");

		readOnlyComponents(verificationType, pagesEyeBalled, pagesSampled);

		logger.debug(Literal.LEAVING);
	}

	private void readOnlyComponents(Combobox verificationType, Intbox pagesEyeBalled, Intbox pagesSampled) {
		logger.debug(Literal.ENTERING);
		int verificationTypeVal = Integer.parseInt(getComboboxValue(verificationType));
		if (!verificationType.isDisabled()) {
			if (RCUDocVerificationType.SAMPLED.getKey().equals(verificationTypeVal)) {
				pagesEyeBalled.setReadonly(true);
				pagesEyeBalled.setValue(0);
				pagesSampled.setReadonly(false);
			} else if (RCUDocVerificationType.EYEBALLED.getKey().equals(verificationTypeVal)) {
				pagesEyeBalled.setReadonly(false);
				pagesSampled.setValue(0);
				pagesSampled.setReadonly(true);
			} else {
				pagesEyeBalled.setReadonly(true);
				pagesSampled.setReadonly(true);
				pagesEyeBalled.setValue(0);
				pagesSampled.setValue(0);
			}
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
		map.put("documentDetails", getRiskContainmentUnit().getDocuments());
		map.put("module", DocumentCategories.VERIFICATION_RCU.getKey());
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
		ComponentsCtrl.applyForward(tab, "onSelect=" + selectMethodName);
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
		rcu.setCustName(this.customerName.getValue());

		for (Listitem listitem : listBoxRiskContainmentUnitDocuments.getItems()) {
			try {
				setValue(listitem, "VerificationType");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				setValue(listitem, "Status");
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

		try {
			Calendar calDate = Calendar.getInstance();
			if (this.verificationDate.getValue() != null) {
				calDate.setTime(this.verificationDate.getValue());
				Calendar calTimeNow = Calendar.getInstance();
				calDate.set(Calendar.HOUR_OF_DAY, calTimeNow.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calTimeNow.get(Calendar.MINUTE));
				calDate.set(Calendar.SECOND, calTimeNow.get(Calendar.SECOND));
				rcu.setVerificationDate(new Timestamp(calDate.getTimeInMillis()));
			} else {
				rcu.setVerificationDate(SysParamUtil.getAppDate());
			}
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
			if (!this.recommendations.isDisabled()
					&& RCUStatus.SELECT.getKey().equals(Integer.parseInt(getComboboxValue(this.recommendations)))) {
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
				rcu.setReason(Long.parseLong(object.toString()));
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

	public void onChange$recommendations(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.reason.setErrorMessage("");
		this.reason.setValue("");
		String type = this.recommendations.getSelectedItem().getValue();
		visibleComponent(Integer.parseInt(type));
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void visibleComponent(Integer type) {
		String reasonType = null;
		if (type == RCUStatus.NEGATIVE.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.RCUNTVRTY.getKey();
		} else if (type == RCUStatus.REFERTOCREDIT.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.RCURFRRTY.getKey();
		} else if (type == RCUStatus.POSITIVE.getKey()) {
			this.reason.setMandatoryStyle(false);
			reasonType = StatuReasons.RCUPOSTVRTY.getKey();
			// #PSD:168882:System displaying reason field as mandatory when select recommendation value as screened or
			// sampled or hold
		} else if (type == RCUStatus.HOLD.getKey()) {
			this.reason.setMandatoryStyle(false);
			PennantAppUtil.setReasonCodeFilters(this.reason, StatuReasons.RCUPOSTVRTY.getKey());
		} else if (type == RCUStatus.SAMPLED.getKey()) {
			this.reason.setMandatoryStyle(false);
			PennantAppUtil.setReasonCodeFilters(this.reason, StatuReasons.RCUPOSTVRTY.getKey());
		} else if (type == RCUStatus.SCREENED.getKey()) {
			this.reason.setMandatoryStyle(false);
			PennantAppUtil.setReasonCodeFilters(this.reason, StatuReasons.RCUPOSTVRTY.getKey());
		}

		if (ImplementationConstants.VER_REASON_CODE_FILTER_BY_REASONTYPE) {
			Filter[] reasonFilter = new Filter[1];
			reasonFilter[0] = new Filter("ReasonTypecode", reasonType, Filter.OP_EQUAL);
			reason.setFilters(reasonFilter);
		}

	}

	private void setValue(Listitem listitem, String comonentId) {
		RCUDocument rcuDocument = null;

		rcuDocument = (RCUDocument) listitem.getAttribute("data");
		switch (comonentId) {
		case "VerificationType":
			Combobox combobox = (Combobox) getComponent(listitem, "VerificationType");
			int verificationType = Integer.parseInt(getComboboxValue(combobox));
			if (!combobox.isDisabled() && RCUDocVerificationType.SELECT.getKey().equals(verificationType)) {
				throw new WrongValueException(combobox, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_RiskContainmentUnitDialog_VerificationType.value") }));
			}
			rcuDocument.setVerificationType(verificationType);

			break;
		case "Status":
			Combobox combobox1 = (Combobox) getComponent(listitem, "Status");
			int rcuStatus = Integer.parseInt(getComboboxValue(combobox1));
			if (!combobox1.isDisabled() && RCUDocStatus.SELECT.getKey().equals(rcuStatus)) {
				throw new WrongValueException(combobox1, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_RiskContainmentUnitDialog_RCUStatus.value") }));
			}
			rcuDocument.setStatus(rcuStatus);
			break;
		case "PagesEyeBalled":
			Intbox textBox = (Intbox) getComponent(listitem, "PagesEyeBalled");
			int pagesEyeballed = textBox.getValue();
			if (!textBox.isReadonly() && !(pagesEyeballed > 0 && pagesEyeballed <= 1000)) {
				throw new WrongValueException(textBox, Labels.getLabel("FIELD_RANGE", new Object[] {
						Labels.getLabel("label_RiskContainmentUnitDialog_PagesEyeBalled.value"), 1, 1000 }));
			}
			rcuDocument.setPagesEyeballed(pagesEyeballed);

			break;
		case "PagesSampled":
			Intbox textBox1 = (Intbox) getComponent(listitem, "PagesSampled");
			int pagesSampled = textBox1.getValue();
			if (!textBox1.isReadonly() && !(pagesSampled > 0 && pagesSampled <= 1000)) {
				throw new WrongValueException(textBox1, Labels.getLabel("FIELD_RANGE", new Object[] {
						Labels.getLabel("label_RiskContainmentUnitDialog_PagesSampled.value"), 1, 1000 }));
			}
			rcuDocument.setPagesSampled(pagesSampled);
			break;
		case "Remarks":
			rcuDocument.setAgentRemarks(((Textbox) getComponent(listitem, "Remarks")).getValue());
			break;

		default:
			break;
		}
		rcuDocument.setRecordStatus(this.recordStatus.getValue());
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
	 * @param rcu The entity that need to be render.
	 */
	public void doShowDialog(RiskContainmentUnit rcu) {
		logger.debug(Literal.LEAVING);

		if (rcu.isNewRecord()) {
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

		if (fromLoanOrg) {
			north.setVisible(false);
			south.setVisible(false);
		}

		doWriteBeanToComponents(rcu);

		if (!fromLoanOrg) {
			setDialog(DialogType.EMBEDDED);
		} else {
			window_RiskContainmentUnitDialog.setHeight("100%");
		}

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

		Map<String, Object> map = new HashMap<String, Object>();
		CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.riskContainmentUnit.getCustId());
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
							DateUtil.getDatePart(SysParamUtil.getAppDate()), true));
		}
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_RiskContainmentUnitDialog_AgentCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_RiskContainmentUnitDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(
					new PTListValidator<ValueLabel>(Labels.getLabel("label_RiskContainmentUnitDialog_Recommendations.value"),
							RCUStatus.getList(), true));
		}
		if (!this.reason.isReadonly()) {
			this.reason.setConstraint(
					new PTStringValidator(Labels.getLabel("label_RiskContainmentUnitDialog_Reason.value"), null,
							this.reason.isMandatory(), true));
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
		this.verificationDate.setConstraint("");

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

		final RiskContainmentUnit entity = new RiskContainmentUnit();
		BeanUtils.copyProperties(this.riskContainmentUnit, entity);

		doDelete(String.valueOf(entity.getId()), entity);

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

		isNew = rcu.isNewRecord();
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
		if (SysParamUtil.isAllowed(SMTParameterConstants.RCU_DOCUMENT_MANDATORY)
				&& this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("submit")) {
			if (documentDetailDialogCtrl != null
					&& CollectionUtils.sizeIsEmpty(documentDetailDialogCtrl.getDocumentDetailsList())) {
				MessageUtil.showError(Labels.getLabel("VERIFICATIONS_DOCUMENT_MANDATORY"));
				return;
			}
		}

		if (documentDetailDialogCtrl != null) {
			rcu.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			rcu.setDocuments(getRiskContainmentUnit().getDocuments());
		}

		rcu.setRcuDocuments(getRcuDocumentsList());

		try {
			if (doProcess(rcu, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(rcu.getRoleCode(), rcu.getNextRoleCode(),
						rcu.getKeyReference(), " Loan ", rcu.getRecordStatus(), getNextTaskId());
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
	protected boolean doProcess(RiskContainmentUnit riskContainmentUnit, String tranType) {
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
					details.setCustomerCif(riskContainmentUnit.getCif());
					details.setFinReference(riskContainmentUnit.getKeyReference());
					details.setCustId(riskContainmentUnit.getCustId());
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
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		RiskContainmentUnit rcu = (RiskContainmentUnit) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

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
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
			if (SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_RCU_EYEBALLED_VALUE)
					&& String.valueOf(RCUDocVerificationType.EYEBALLED.getValue()).equals(valueLabel.getLabel())) {
				continue;
			}
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

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}