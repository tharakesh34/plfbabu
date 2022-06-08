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
package com.pennant.webui.verification.tv;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
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

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
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
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.StatuReasons;
import com.pennanttech.pennapps.pff.verification.VerificationCategory;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.TVStatus;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Verification/TechnicalVerification/technicalVerificationDialog.zul file. <br>
 */
public class TechnicalVerificationDialogCtrl extends GFCBaseCtrl<TechnicalVerification> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TechnicalVerificationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TechnicalVerificationDialog;

	protected Tab verificationDetails;
	protected Tab documentDetails;
	protected Tab onePagerReportTab;
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_summary;
	protected Tab extendedDetailsTab;
	protected Tabpanel extendedFieldTabpanel;
	protected Tabpanel observationsFieldTabPanel;
	protected Tabpanel onePagerExtFieldsTabpanel;

	// Basic Details
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox custName;
	protected Textbox collateralType;
	protected Textbox collateralReference;
	protected Textbox contactNumber1;
	protected Textbox contactNumber2;
	protected CurrencyBox valuationAmount;

	// Summary details
	protected Textbox agentCode;
	protected Textbox agentName;
	protected Combobox recommendations;
	protected ExtendedCombobox reason;
	protected Textbox summaryRemarks;
	protected Space space_Reason;
	protected Datebox verificationDate;

	protected North north;
	protected South south;
	protected Space space_AgentCode;
	protected Space space_AgentName;
	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected String selectMethodName = "onSelectTab";
	protected ExtendedCombobox loanType;
	protected ExtendedCombobox custBranch;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient TechnicalVerificationListCtrl technicalVerificationListCtrl;
	private transient TechnicalVerificationService technicalVerificationService;
	private transient CollateralSetupService collateralSetupService;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;
	@Autowired
	private transient ExtendedFieldDetailsService extendedFieldDetailsService;

	private TechnicalVerification technicalVerification = null;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	private ExtendedFieldCtrl onePagerExtendedFieldCtrl = null;
	private int ccyFormat;

	private boolean fromLoanOrg;
	private boolean isFromCollateralSetUp;

	protected Button btnSearchCustomerDetails;

	// One Pager Report
	private Textbox documentName;
	private Button btnUploadDoc;
	protected Div docDiv;
	protected Iframe onePagerDocumentView;
	private byte[] imagebyte;
	private static ScriptValidationService scriptValidationService;

	String prprtyAddr1 = "";
	String prprtyAddr2 = "";
	String prprtyAddr3 = "";

	/**
	 * default constructor.<br>
	 */
	public TechnicalVerificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TechnicalVerificationDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TechnicalVerificationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TechnicalVerificationDialog);

		try {
			// Get the required arguments.
			this.technicalVerification = (TechnicalVerification) arguments.get("technicalVerification");
			this.technicalVerificationListCtrl = (TechnicalVerificationListCtrl) arguments
					.get("technicalVerificationListCtrl");

			if (this.technicalVerification == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.get("LOAN_ORG") != null) {
				fromLoanOrg = true;
				enqiryModule = true;
			} else if (arguments.get("isFromCollateralSetUp") != null) {
				isFromCollateralSetUp = (boolean) arguments.get("isFromCollateralSetUp");
				enqiryModule = true;
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			// Store the before image.
			TechnicalVerification technicalVerification = new TechnicalVerification();
			BeanUtils.copyProperties(this.technicalVerification, technicalVerification);
			this.technicalVerification.setBefImage(technicalVerification);

			// Render the page and display the data.
			doLoadWorkFlow(this.technicalVerification.isWorkflow(), this.technicalVerification.getWorkflowId(),
					this.technicalVerification.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else if (fromLoanOrg) {
				setWorkFlowEnabled(true);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.technicalVerification);
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
			reasonFilter[0] = new Filter("ReasonTypecode", StatuReasons.TVSRES.getKey(), Filter.OP_EQUAL);
		}
		reason.setFilters(reasonFilter);

		this.agentCode.setMaxlength(8);
		this.agentName.setMaxlength(20);
		this.summaryRemarks.setMaxlength(500);
		this.verificationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valuationAmount.setProperties(true, PennantConstants.defaultCCYDecPos);
		this.valuationAmount.getCcyTextBox().setVisible(true);
		if (StringUtils.equals(SysParamUtil.getValueAsString(SMTParameterConstants.VERIFICATIONS_CUSTOMERVIEW),
				PennantConstants.YES)) {
			this.btnSearchCustomerDetails.setVisible(false);
		} else {
			this.btnSearchCustomerDetails.setVisible(true);
		}
		this.space_AgentCode.setVisible(!ImplementationConstants.VER_INIT_FROM_OUTSIDE);
		this.space_AgentName.setVisible(!ImplementationConstants.VER_INIT_FROM_OUTSIDE);

		loanType.setReadonly(true);
		custBranch.setReadonly(true);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnSave"));
		this.btnUploadDoc.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnUploadDoc"));
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
		doShowNotes(this.technicalVerification);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		technicalVerificationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.technicalVerification.getBefImage());
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
			if (details != null) {
				this.reason.setAttribute("ReasonId", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param fi
	 * 
	 */
	public void doWriteBeanToComponents(TechnicalVerification tv) {
		logger.debug(Literal.ENTERING);

		// Basic Details
		this.custCIF.setValue(tv.getCif());
		this.finReference.setValue(tv.getKeyReference());
		this.custName.setValue(tv.getCustName());
		this.collateralType.setValue(tv.getCollateralType());
		this.collateralReference.setValue(tv.getCollateralRef());
		this.contactNumber1.setValue(tv.getContactNumber1());
		this.contactNumber2.setValue(tv.getContactNumber2());
		this.loanType.setValue(tv.getLoanType(), tv.getLovDescLoanTypeName());
		this.custBranch.setValue(tv.getSourcingBranch(), tv.getLovDescSourcingBranch());
		// Summary Details
		this.verificationDate.setValue(tv.getVerifiedDate());
		if (!fromLoanOrg && !isFromCollateralSetUp) {
			if (getFirstTaskOwner().equals(getRole()) && tv.getVerifiedDate() == null) {
				this.verificationDate.setValue(SysParamUtil.getAppDate());
			}
		}
		this.agentCode.setValue(tv.getAgentCode());
		this.agentName.setValue(tv.getAgentName());
		this.valuationAmount.setValue(
				PennantApplicationUtil.formateAmount(tv.getValuationAmount(), PennantConstants.defaultCCYDecPos));

		if (!tv.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty(tv.getReasonCode()),
					StringUtils.trimToEmpty(tv.getReasonDesc()));
			if (tv.getReason() != null) {
				this.reason.setAttribute("ReasonId", tv.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}
		if (!tv.isNewRecord()) {
			visibleComponent(tv.getStatus());
		}
		this.summaryRemarks.setValue(tv.getSummaryRemarks());
		fillComboBox(this.recommendations, tv.getStatus(), TVStatus.getList());
		this.recordStatus.setValue(tv.getRecordStatus());

		// Extended Field details
		appendExtendedFieldDetails(tv);

		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Verification details
		appendVerificationFieldDetails(tv);

		// Append One Pager Report Extended Fields.
		if (tv.getVerificationCategory() == VerificationCategory.ONEPAGER.getKey()) {
			onePagerReportTab.setVisible(true);
			appendOnePagerReportExtDetails(tv);
			this.documentName.setValue(tv.getDocumentName());
			setOnePagerDocument(tv);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	private void appendDocumentDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab("DOCUMENTDETAIL", true);
		final Map<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getTechnicalVerification().getDocuments());
		map.put("module", DocumentCategories.VERIFICATION_TV.getKey());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel("DOCUMENTDETAIL"), map);
		logger.debug(Literal.LEAVING);
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
		map.put("moduleName", VerificationType.TV.name());
		map.put("enqiryModule", enqiryModule);
		map.put("isEditable",
				!isReadOnly(/* "TechnicalVerificationDialog_Documents" */"TechnicalVerificationDialog_AgentCode"));

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
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append extended field details
	 */
	private void appendExtendedFieldDetails(TechnicalVerification tv) {
		logger.debug(Literal.ENTERING);

		extendedFieldCtrl = new ExtendedFieldCtrl();
		ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
				CollateralConstants.MODULE_NAME, tv.getCollateralType(),
				ExtendedFieldConstants.EXTENDEDTYPE_EXTENDEDFIELD);

		if (extendedFieldHeader == null) {
			return;
		}
		// Extended Field Details
		StringBuilder tableName = new StringBuilder();
		tableName.append(CollateralConstants.MODULE_NAME);
		tableName.append("_");
		tableName.append(extendedFieldHeader.getSubModuleName());
		tableName.append("_ED");
		tableName.append("_TV");
		try {
			this.extendedDetailsTab.setLabel(technicalVerification.getCollateralType());
			extendedFieldCtrl.setExtendedFieldDetailsService(extendedFieldDetailsService);
			final Map<String, Object> map = getDefaultArguments(tv);
			map.put("dialogCtrl", this);
			map.put("extendedFieldHeader", extendedFieldHeader);
			map.put("fieldRenderList", extendedFieldCtrl.getVerificationExtendedFieldsList(tv.getVerificationId(),
					tableName.toString(), ""));
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldRenderDialog.zul",
					extendedFieldTabpanel, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public Map<String, Object> getDefaultArguments(TechnicalVerification tv) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails(tv));
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", CollateralConstants.MODULE_NAME);
		setCcyFormat(CurrencyUtil.getFormat(tv.getCollateralCcy()));
		return map;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails(TechnicalVerification tv) {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, tv.getCif());
		arrayList.add(1, tv.getCollateralRef());
		arrayList.add(2, tv.getCustName());
		arrayList.add(3, tv.getCollateralCcy());
		arrayList.add(4, tv.getCollateralType());
		arrayList.add(5, tv.getCollateralLoc());
		return arrayList;
	}

	/**
	 * This method is for append verification field details
	 */
	private void appendVerificationFieldDetails(TechnicalVerification tv) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.MODULE_NAME, tv.getCollateralType(),
					ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION);
			CollateralSetup collateralSetup = getCollateralSetupService()
					.getCollateralSetupByRef(this.collateralReference.getValue(), "", true);
			if (!(VerificationCategory.ONEPAGER.getKey() == tv.getVerificationCategory())) {
				extendedFieldHeader.setPostValidation(collateralSetup.getCollateralStructure().getPostValidation());
				extendedFieldCtrl.setExtendedFieldHeader(extendedFieldHeader);
			}

			if (extendedFieldHeader == null) {
				return;
			}
			// Extended Field Details
			StringBuilder tableName = null;

			List<ExtendedFieldDetail> detailsList = extendedFieldHeader.getExtendedFieldDetails();
			int fieldSize = 0;
			if (detailsList != null && !detailsList.isEmpty()) {
				fieldSize = detailsList.size();
				if (fieldSize != 0) {
					fieldSize = fieldSize / 2;
					fieldSize = fieldSize + 1;
				}
			}

			tableName = new StringBuilder();
			tableName.append(CollateralConstants.MODULE_NAME);
			tableName.append("_");
			tableName.append(tv.getCollateralType());
			tableName.append("_ED");

			Map<String, Object> extMapValues = null;
			ExtendedFieldRender extFieldRender = extendedFieldCtrl.getExtendedFieldRender(tv.getCollateralRef(),
					tableName.toString(), "_View");

			tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_TV");

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(String.valueOf(tv.getVerificationId()), tableName.toString(), "_View");

			if (!extFieldRender.getMapValues().isEmpty()) {
				extMapValues = extFieldRender.getMapValues();
			}

			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			extendedFieldCtrl.setTab(this.verificationDetails);
			extendedFieldCtrl.setExtendedFieldDetailsService(extendedFieldDetailsService);
			tv.setExtendedFieldHeader(extendedFieldHeader);
			tv.setExtendedFieldRender(extendedFieldRender);

			if (extendedFieldRender.getMapValues() == null) {
				Map<String, Object> mapValue = new HashMap<>();
				if (extMapValues.containsKey("PROJECTNAME")) {
					mapValue.put("PROJECTNAME", extMapValues.get("PROJECTNAME"));
				}
				if (extMapValues.containsKey("PROPADDRESSLINE1")) {
					mapValue.put("PROPERTYADDLINE1", extMapValues.get("PROPADDRESSLINE1"));
					prprtyAddr1 = (String) extMapValues.get("PROPADDRESSLINE1");
				}
				if (extMapValues.containsKey("PROPADDRESSLINE2")) {
					mapValue.put("PRPRTYADDLINE2", extMapValues.get("PROPADDRESSLINE2"));
					prprtyAddr2 = (String) extMapValues.get("PROPADDRESSLINE2");
				}
				if (extMapValues.containsKey("PROPADDRESSLINE3")) {
					mapValue.put("PRPRTYADDLINE3", extMapValues.get("PROPADDRESSLINE3"));
					prprtyAddr3 = (String) extMapValues.get("PROPADDRESSLINE3");
				}
				if (extMapValues.containsKey("AREALOCALTY")) {
					mapValue.put("AREALOCALITY", extMapValues.get("AREALOCALTY"));
				}
				if (extMapValues.containsKey("PROPERTYCITY")) {
					mapValue.put("PROPERTYCITY", extMapValues.get("PROPERTYCITY"));
				}
				if (extMapValues.containsKey("PROPERTYSTATE")) {
					mapValue.put("PROPERTYSTATE", extMapValues.get("PROPERTYSTATE"));
				}
				if (extMapValues.containsKey("PINCODE")) {
					mapValue.put("PROPERTYPINCODE", extMapValues.get("PINCODE"));
				}

				mapValue.put("VERIFICATEGORY", VerificationCategory.getType(tv.getVerificationCategory()));
				mapValue.put("PRODUCT", tv.getProductCategory());

				if (extMapValues.containsKey("SUPERBUILTUPARE")) {
					mapValue.put("SUPERBUILTUPARE", extMapValues.get("SUPERBUILTUPARE"));
				}
				if (extMapValues.containsKey("BUILTUPAREA")) {
					mapValue.put("BUILTUPAREA", extMapValues.get("BUILTUPAREA"));
				}
				if (extMapValues.containsKey("AREAVALUEDSQFT")) {
					mapValue.put("AREAVALUEDSQFT", extMapValues.get("AREAVALUEDSQFT"));
				}
				if (extMapValues.containsKey("RATEPSF")) {
					mapValue.put("RATEPSF", extMapValues.get("RATEPSF"));
				}
				if (extMapValues.containsKey("CARPETAREA")) {
					mapValue.put("CARPETAREA", extMapValues.get("CARPETAREA"));
				}

				if (extMapValues.containsKey("PROJECTNAME")
						&& StringUtils.isNotEmpty(extMapValues.get("PROJECTNAME").toString())) {
					mapValue.put("PROJECTNAME", "APF");
				}

				this.valuationAmount.setValue(PennantApplicationUtil.formateAmount(
						new BigDecimal(extMapValues.get("UNITPRICE").toString()), PennantConstants.defaultCCYDecPos));

				extendedFieldRender.setMapValues(mapValue);
			}

			if (tv.getBefImage() != null) {
				tv.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				tv.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("TechnicalVerificationDialog_Recommendations"));/*
																										 * "TechnicalVerificationDialog_TechVerificationExtFields"
																										 */
			extendedFieldCtrl.setWindow(this.window_TechnicalVerificationDialog);
			extendedFieldCtrl.render();
			this.verificationDetails
					.setLabel(Labels.getLabel("label_FieldInvestigationDialog_VerificationDetails.value"));
			this.observationsFieldTabPanel.setHeight((fieldSize * 37) + "px");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	// One Pager Report Extended Fields.
	private void appendOnePagerReportExtDetails(TechnicalVerification tv) {
		logger.debug(Literal.ENTERING);
		try {
			onePagerExtendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = onePagerExtendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.VERIFICATION_MODULE, ExtendedFieldConstants.VERIFICATION_TV);

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

			ExtendedFieldRender extendedFieldRender = onePagerExtendedFieldCtrl
					.getExtendedFieldRender(String.valueOf(tv.getVerificationId()), tableName.toString(), "_View");
			onePagerExtendedFieldCtrl.setTabpanel(onePagerExtFieldsTabpanel);
			onePagerExtendedFieldCtrl.setTab(this.onePagerReportTab);
			onePagerExtendedFieldCtrl.setExtendedFieldDetailsService(extendedFieldDetailsService);

			if (tv.getBefImage() != null) {
				tv.getBefImage().setOnePagerExtHeader(extendedFieldHeader);
				tv.getBefImage().setOnePagerExtRender(extendedFieldRender);
			}

			onePagerExtendedFieldCtrl.setCcyFormat(2);
			onePagerExtendedFieldCtrl.setReadOnly(isReadOnly("TechnicalVerificationDialog_OnePagerExtFields"));
			onePagerExtendedFieldCtrl.setWindow(this.window_TechnicalVerificationDialog);
			onePagerExtendedFieldCtrl.render();
			this.onePagerReportTab.setLabel(Labels.getLabel("label_TechnicalVerificationDialog_OnePagerReport.value"));
			this.onePagerExtFieldsTabpanel.setHeight((fieldSize * 37) + "px");
		} catch (Exception e) {
			closeDialog();
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	// Process for Document uploading
	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media, this.documentName);
		logger.debug("Leaving" + event.toString());
	}

	// Browse for Document uploading
	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");
		try {
			String docType = "";
			if (MediaUtil.isPdf(media)) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if (MediaUtil.isImage(media)) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
			}

			// Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.onePagerDocumentView.setContent(
						new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.onePagerDocumentView.setContent(media);
			}
			this.onePagerDocumentView.setVisible(true);
			textbox.setValue(fileName);
			imagebyte = media.getByteData();

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @throws ParseException
	 */
	public void doWriteComponentsToBean(TechnicalVerification tv) throws ParseException {
		logger.debug(Literal.ENTERING);
		final Object TOTALVALUATION = "TOTALVALUATION";
		final Object FAIRMARKETVALUE = "FAIRMARKETVALUE";
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		BigDecimal valAmt = BigDecimal.ZERO;
		// Extended Field validations
		if (tv.getExtendedFieldHeader() != null) {
			// to validate the TV post script, we need to pass collateral ED fields to resolve the script binding
			// error's
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.MODULE_NAME, tv.getCollateralType(),
					ExtendedFieldConstants.EXTENDEDTYPE_EXTENDEDFIELD);
			Map<String, Object> map = prepareMap(extendedFieldHeader.getExtendedFieldDetails());
			// along with collateral we need to pass TV field map for validation
			if (extendedFieldCtrl.getGenerator() != null) {
				Map<String, Object> tvMap = extendedFieldCtrl.getGenerator()
						.doSave(tv.getExtendedFieldHeader().getExtendedFieldDetails(), enqiryModule);
				if (MapUtils.isNotEmpty(tvMap)) {
					map.putAll(tvMap);
				}
			}
			// Post Script validations for TV
			if (!enqiryModule) {
				if (StringUtils.trimToNull(extendedFieldHeader.getPostValidation()) != null) {
					ScriptErrors postValidationErrors = scriptValidationService
							.getPostValidationErrors(extendedFieldHeader.getPostValidation(), map);
					// showing the error details on screen
					extendedFieldCtrl.setExtendedFieldHeader(tv.getExtendedFieldHeader());
					extendedFieldCtrl.showErrorDetails(postValidationErrors);
				}
			}

			// After post script validations we need to save only TV ED fields
			if (extendedFieldCtrl.getGenerator() != null) {
				map.clear();
				map = extendedFieldCtrl.getGenerator().doSave(tv.getExtendedFieldHeader().getExtendedFieldDetails(),
						enqiryModule);
				tv.getExtendedFieldRender().setMapValues(map);
			}

			if (tv.getVerificationCategory() != VerificationCategory.ONEPAGER.getKey()) {
				if (tv.getExtendedFieldRender().getMapValues().containsKey(TOTALVALUATION)) {
					valAmt = (BigDecimal) tv.getExtendedFieldRender().getMapValues().get(TOTALVALUATION);
				}
			}
		}

		// One Pager Report Extended fields Validation
		if (tv.getOnePagerExtHeader() != null) {
			tv.setOnePagerExtRender(onePagerExtendedFieldCtrl.save(true));
			if (tv.getVerificationCategory() == VerificationCategory.ONEPAGER.getKey()) {
				if (tv.getOnePagerExtRender().getMapValues().containsKey(FAIRMARKETVALUE)) {
					valAmt = (BigDecimal) tv.getOnePagerExtRender().getMapValues().get(FAIRMARKETVALUE);
				}
			}
		}

		if (valAmt.compareTo(BigDecimal.ZERO) > 0) {
			tv.setValuationAmount(valAmt);
		} else {
			valAmt = this.valuationAmount.getActualValue();
			tv.setValuationAmount(PennantApplicationUtil.unFormateAmount(valAmt, PennantConstants.defaultCCYDecPos));
		}

		try {
			Calendar calDate = Calendar.getInstance();
			if (this.verificationDate.getValue() != null) {
				calDate.setTime(this.verificationDate.getValue());
				Calendar calTimeNow = Calendar.getInstance();
				calDate.set(Calendar.HOUR_OF_DAY, calTimeNow.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calTimeNow.get(Calendar.MINUTE));
				calDate.set(Calendar.SECOND, calTimeNow.get(Calendar.SECOND));
				tv.setVerifiedDate(new Timestamp(calDate.getTimeInMillis()));
			} else {
				tv.setVerifiedDate(SysParamUtil.getAppDate());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			tv.setAgentCode(this.agentCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			tv.setAgentName(this.agentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.recommendations.isDisabled()
					&& TVStatus.SELECT.getKey().equals(Integer.parseInt(getComboboxValue(this.recommendations)))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_TechnicalVerificationDialog_Recommendations.value") }));
			} else {
				tv.setStatus(Integer.parseInt(getComboboxValue(this.recommendations)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			tv.setReasonDesc(this.reason.getDescription());
			tv.setReasonCode(this.reason.getValue());
			this.reason.getValidatedValue();
			Object object = this.reason.getAttribute("ReasonId");
			if (object != null) {
				tv.setReason((Long.parseLong(object.toString())));
			} else {
				tv.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			tv.setSummaryRemarks(this.summaryRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, this.verificationDetails);
		// DocumentName
		try {
			tv.setDocumentName(this.documentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Document Image
		try {
			tv.setDocImage(this.imagebyte);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		showErrorDetails(wve, this.onePagerReportTab);
		showErrorDetails(wve, this.extendedDetailsTab);
		logger.debug(Literal.LEAVING);

	}

	private Map<String, Object> prepareMap(List<ExtendedFieldDetail> extendedFieldDetails) {
		Map<String, Object> map = new HashMap<>();
		for (ExtendedFieldDetail extendedFieldDetail : extendedFieldDetails) {
			map.put(extendedFieldDetail.getFieldName(), extendedFieldDetail.getDefValue());
		}
		return map;
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
		if (type == TVStatus.NEGATIVE.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.TVPOSTVRTY.getKey();
		} else if (type == TVStatus.POSITIVE.getKey()) {
			this.reason.setMandatoryStyle(false);
			reasonType = StatuReasons.TVNTVRTY.getKey();
		} else if (type == TVStatus.REFERTOCREDIT.getKey()) {
			reasonType = StatuReasons.TVRFRRTY.getKey();
		}

		if (ImplementationConstants.VER_REASON_CODE_FILTER_BY_REASONTYPE) {
			Filter[] reasonFilter = new Filter[1];
			reasonFilter[0] = new Filter("ReasonTypecode", reasonType, Filter.OP_EQUAL);
			reason.setFilters(reasonFilter);
		}

	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param technicalVerification The entity that need to be render.
	 */
	public void doShowDialog(TechnicalVerification technicalVerification) {
		logger.debug(Literal.ENTERING);

		if (technicalVerification.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.verificationDate.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(technicalVerification.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
				// setFocus
				this.verificationDate.focus();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			this.south.setVisible(false);
		}

		if (fromLoanOrg) {
			north.setVisible(false);
			south.setVisible(false);
		}

		doWriteBeanToComponents(technicalVerification);
		if (!fromLoanOrg && !isFromCollateralSetUp) {
			setDialog(DialogType.EMBEDDED);
		} else if (fromLoanOrg) {
			this.window_TechnicalVerificationDialog.setHeight("100%");
		} else {
			this.window_TechnicalVerificationDialog.setHeight("70%");
			this.window_TechnicalVerificationDialog.setWidth("80%");
			this.window_TechnicalVerificationDialog.doModal();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "Collateral Reference" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCollateralRef(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Map<String, Object> map = new HashMap<String, Object>();

		CollateralSetup collateralSetup = getCollateralSetupService()
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
	 * When user clicks on button "Customer CIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustomerDetails(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Map<String, Object> map = new HashMap<String, Object>();

		CustomerDetails customerDetails = customerDetailsService
				.getCustomerById(this.technicalVerification.getCustId());
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
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (this.verificationDate.isVisible() && !this.verificationDate.isReadonly()) {
			this.verificationDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_TechnicalVerificationDialog_VerificationDate.value"),
							true, DateUtil.getDatePart(technicalVerification.getCreatedOn()),
							DateUtil.getDatePart(SysParamUtil.getAppDate()), true));
		}

		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_TechnicalVerificationDialog_AgentCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}

		if (!this.reason.isReadonly()) {
			this.reason.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_Reason.value"), null,
							this.reason.isMandatory(), true));
		}

		if (!this.summaryRemarks.isReadonly()) {
			this.summaryRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_CHAR, false));
		}

		if (!this.valuationAmount.isReadonly()) {
			this.valuationAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_TechnicalVerificationDialog_ValuationAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false));
		}

		if (onePagerReportTab.isVisible() && this.btnUploadDoc.isVisible()) {
			this.documentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_OnePagerDialog_DocumnetName.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.verificationDate.setConstraint("");
		this.agentCode.setConstraint("");
		this.agentName.setConstraint("");
		this.recommendations.setConstraint("");
		this.reason.setConstraint("");
		this.summaryRemarks.setConstraint("");
		this.valuationAmount.setConstraint("");
		// this.documentName.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.agentCode.setErrorMessage("");
		this.agentName.setErrorMessage("");
		this.recommendations.setErrorMessage("");
		this.reason.setErrorMessage("");
		this.summaryRemarks.setErrorMessage("");
		this.valuationAmount.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final TechnicalVerification entity = new TechnicalVerification();
		BeanUtils.copyProperties(this.technicalVerification, entity);

		doDelete(entity.getKeyReference(), entity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.technicalVerification.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_AgentCode"), this.verificationDate);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_AgentCode"), this.agentCode);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_AgentName"), this.agentName);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_Recommendations"), this.recommendations);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_Remarks"), this.summaryRemarks);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_Remarks"), this.valuationAmount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.technicalVerification.isNewRecord()) {
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

		this.verificationDate.setDisabled(true);
		this.agentCode.setReadonly(true);
		this.agentName.setReadonly(true);
		this.recommendations.setDisabled(true);
		this.reason.setReadonly(true);
		this.summaryRemarks.setReadonly(true);
		this.valuationAmount.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.userAction.setSelectedIndex(0);
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

		final TechnicalVerification tv = new TechnicalVerification();
		BeanUtils.copyProperties(this.technicalVerification, tv);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(tv);

		isNew = tv.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(tv.getRecordType())) {
				tv.setVersion(tv.getVersion() + 1);
				if (isNew) {
					tv.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					tv.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					tv.setNewRecord(true);
				}
			}
		} else {
			tv.setVersion(tv.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Document Details Saving

		if (SysParamUtil.isAllowed(SMTParameterConstants.TV_DOCUMENT_MANDATORY)
				&& this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("submit")) {
			if (documentDetailDialogCtrl != null
					&& CollectionUtils.sizeIsEmpty(documentDetailDialogCtrl.getDocumentDetailsList())) {
				MessageUtil.showError(Labels.getLabel("VERIFICATIONS_DOCUMENT_MANDATORY"));
				return;
			}
		}

		if (documentDetailDialogCtrl != null) {
			tv.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			tv.setDocuments(getTechnicalVerification().getDocuments());
		}

		try {
			if (doProcess(tv, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(tv.getRoleCode(), tv.getNextRoleCode(),
						tv.getKeyReference(), " Loan ", tv.getRecordStatus(), getNextTaskId());
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
	protected boolean doProcess(TechnicalVerification tv, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		tv.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		tv.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		tv.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			tv.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(tv.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, tv);
				}

				if (isNotesMandatory(taskId, tv)) {
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

			tv.setTaskId(taskId);
			tv.setNextTaskId(nextTaskId);
			tv.setRoleCode(getRole());
			tv.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (tv.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = tv.getExtendedFieldRender();
				details.setReference(String.valueOf(tv.getVerificationId()));
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(tv.getRecordStatus());
				details.setRecordType(tv.getRecordType());
				details.setVersion(tv.getVersion());
				details.setWorkflowId(tv.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(tv.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(tv.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(tv.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			// Document Details
			if (tv.getDocuments() != null && !tv.getDocuments().isEmpty()) {
				for (DocumentDetails details : tv.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}

					details.setReferenceId(String.valueOf(tv.getVerificationId()));
					details.setDocModule(VerificationType.TV.getCode());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(tv.getRecordStatus());
					details.setWorkflowId(tv.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					details.setCustomerCif(tv.getCif());
					details.setFinReference(tv.getKeyReference());
					if (PennantConstants.RECORD_TYPE_DEL.equals(tv.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(tv.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// One Pager Extended Field details
			if (tv.getOnePagerExtRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = tv.getOnePagerExtRender();
				details.setReference(String.valueOf(tv.getVerificationId()));
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(tv.getRecordStatus());
				details.setRecordType(tv.getRecordType());
				details.setVersion(tv.getVersion());
				details.setWorkflowId(tv.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(tv.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(tv.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(tv.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			auditHeader = getAuditHeader(tv, tranType);
			String operationRefs = getServiceOperations(taskId, tv);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(tv, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(tv, tranType);
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
		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = technicalVerificationService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = technicalVerificationService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = technicalVerificationService.doApprove(auditHeader);

					if (technicalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = technicalVerificationService.doReject(auditHeader);
					if (technicalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_TechnicalVerificationDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_TechnicalVerificationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.technicalVerification), true);
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
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TechnicalVerification technicalVerification, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, technicalVerification.getBefImage(),
				technicalVerification);
		return new AuditHeader(getReference(), null, null, null, auditDetail, technicalVerification.getUserDetails(),
				getOverideMap());
	}

	private void setOnePagerDocument(TechnicalVerification tv) {
		AMedia amedia = null;
		if (tv.getDocImage() != null) {
			amedia = new AMedia(tv.getDocumentName(), null, null, tv.getDocImage());
			imagebyte = tv.getDocImage();
			onePagerDocumentView.setContent(amedia);
		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.technicalVerification.getId());
	}

	public void setTechnicalVerificationService(TechnicalVerificationService technicalVerificationService) {
		this.technicalVerificationService = technicalVerificationService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public int getCcyFormat() {
		return ccyFormat;
	}

	public void setCcyFormat(int ccyFormat) {
		this.ccyFormat = ccyFormat;
	}

	public TechnicalVerification getTechnicalVerification() {
		return technicalVerification;
	}

	public void setTechnicalVerification(TechnicalVerification technicalVerification) {
		this.technicalVerification = technicalVerification;
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

	public static ScriptValidationService getScriptValidationService() {
		return scriptValidationService;
	}

	public static void setScriptValidationService(ScriptValidationService scriptValidationService) {
		TechnicalVerificationDialogCtrl.scriptValidationService = scriptValidationService;
	}

}
