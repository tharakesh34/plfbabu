/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
package com.pennant.webui.verification.tv;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.pennant.app.util.CurrencyUtil;
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
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Verification/TechnicalVerification/technicalVerificationDialog.zul
 * file. <br>
 */
public class TechnicalVerificationDialogCtrl extends GFCBaseCtrl<TechnicalVerification> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TechnicalVerificationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TechnicalVerificationDialog;

	protected Tab verificationDetails;
	protected Tab documentDetails;
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_summary;
	protected Tab extendedDetailsTab;
	protected Tabpanel extendedFieldTabpanel;
	protected Tabpanel observationsFieldTabPanel;

	//Basic Details
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox custName;
	protected Textbox collateralType;
	protected Textbox collateralReference;
	protected Textbox contactNumber1;
	protected Textbox contactNumber2;
	protected CurrencyBox valuationAmount;

	//Summary details
	protected Textbox agentCode;
	protected Textbox agentName;
	protected Combobox recommendations;
	protected ExtendedCombobox reason;
	protected Textbox summaryRemarks;
	protected Space space_Reason;
	protected Datebox verificationDate;
	
	protected North north;
	protected South south;
	
	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected String selectMethodName = "onSelectTab";
	
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient TechnicalVerificationListCtrl technicalVerificationListCtrl;
	private transient TechnicalVerificationService technicalVerificationService;
	private transient CollateralSetupService collateralSetupService;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;

	private TechnicalVerification technicalVerification = null;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	private int ccyFormat;
	
	private boolean fromLoanOrg;
	
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
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_TechnicalVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TechnicalVerificationDialog);

		try {
			// Get the required arguments.
			this.technicalVerification = (TechnicalVerification) arguments.get("technicalVerification");
			this.technicalVerificationListCtrl = (TechnicalVerificationListCtrl) arguments.get("technicalVerificationListCtrl");

			if (this.technicalVerification == null) {
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
		this.reason.setModuleName("TVStatusReason");
		this.reason.setValueColumn("Code");
		this.reason.setDescColumn("Description");
		this.reason.setValidateColumns(new String[] { "Code" });
		this.agentCode.setMaxlength(8);
		this.agentName.setMaxlength(20);
		this.summaryRemarks.setMaxlength(50);
		this.verificationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valuationAmount.setProperties(true, PennantConstants.defaultCCYDecPos);
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
	 * The framework calls this event handler when user clicks the delete
	 * button.
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
	 * The framework calls this event handler when user clicks the cancel
	 * button.
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
		doShowNotes(this.technicalVerification);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
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

		//Basic Details
		this.custCIF.setValue(tv.getCif());
		this.finReference.setValue(tv.getKeyReference());
		this.custName.setValue(tv.getCustName());
		this.collateralType.setValue(tv.getCollateralType());
		this.collateralReference.setValue(tv.getCollateralRef());
		this.contactNumber1.setValue(tv.getContactNumber1());
		this.contactNumber2.setValue(tv.getContactNumber2());
		
		//Summary Details
		this.verificationDate.setValue(tv.getDate());
		this.agentCode.setValue(tv.getAgentCode());
		this.agentName.setValue(tv.getAgentName());
		this.valuationAmount.setValue(PennantApplicationUtil.formateAmount(tv.getValuationAmount(), PennantConstants.defaultCCYDecPos));
		
		if (!tv.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty((tv.getReasonCode())), StringUtils.trimToEmpty(tv.getReasonDesc()));
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
		fillComboBox(this.recommendations, tv.getStatus(), FIStatus.getList());
		
		// Extended Field details
		appendExtendedFieldDetails(tv);
		
		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Verification details
		appendVerificationFieldDetails(tv);
		
		this.recordStatus.setValue(tv.getRecordStatus());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	private void appendDocumentDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab("DOCUMENTDETAIL", true);
		final HashMap<String, Object> map = getDefaultArguments();
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

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", VerificationType.FI.name());
		map.put("enqiryModule", enqiryModule);
		map.put("isEditable", !isReadOnly(/*"TechnicalVerificationDialog_Documents"*/"TechnicalVerificationDialog_AgentCode"));
		
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
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * This method is for append extended field details
	 */
	private void appendExtendedFieldDetails(TechnicalVerification tv) {
		logger.debug(Literal.ENTERING);

		extendedFieldCtrl = new ExtendedFieldCtrl();
		ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(CollateralConstants.MODULE_NAME, tv.getCollateralType(),
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
			final HashMap<String, Object> map = getDefaultArguments(tv);
			map.put("dialogCtrl", this);
			map.put("extendedFieldHeader", extendedFieldHeader);
			map.put("fieldRenderList", extendedFieldCtrl.getExtendedFieldRendeList(tv.getVerificationId(), tableName.toString(), ""));
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldRenderDialog.zul", extendedFieldTabpanel, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	
	public HashMap<String, Object> getDefaultArguments(TechnicalVerification tv) {
		final HashMap<String, Object> map = new HashMap<String, Object>();
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
					CollateralConstants.MODULE_NAME, tv.getCollateralType(), ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION);

			if (extendedFieldHeader == null) {
				return;
			}
			// Extended Field Details
			StringBuilder tableName = new StringBuilder();
			tableName.append( CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_TV");
			
			
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
			
			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(String.valueOf(tv.getVerificationId()), tableName.toString(),  "_View");
			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			tv.setExtendedFieldHeader(extendedFieldHeader);
			tv.setExtendedFieldRender(extendedFieldRender);

			if (tv.getBefImage() != null) {
				tv.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				tv.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("TechnicalVerificationDialog_Recommendations"));/*"TechnicalVerificationDialog_TechVerificationExtFields"*/
			extendedFieldCtrl.setWindow(this.window_TechnicalVerificationDialog);
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @throws ParseException 
	 */
	public void doWriteComponentsToBean(TechnicalVerification tv) throws ParseException {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Extended Field validations
		if (tv.getExtendedFieldHeader() != null) {
			tv.setExtendedFieldRender(extendedFieldCtrl.save());
		}
		
		try {
			tv.setDate(this.verificationDate.getValue());
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
			if ("0".equals(getComboboxValue(this.recommendations))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_TechnicalVerificationDialog_Recommendations.value") }));
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

		try {
			tv.setValuationAmount(PennantApplicationUtil.unFormateAmount(this.valuationAmount.getActualValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		showErrorDetails(wve, this.verificationDetails);
		showErrorDetails(wve, this.extendedDetailsTab);
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
		if (type == FIStatus.NOT_COMPLETED.getKey()) {
			this.reason.setMandatoryStyle(true);
		} else {
			this.reason.setMandatoryStyle(false);
		}
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param technicalVerification
	 *            The entity that need to be render.
	 */
	public void doShowDialog(TechnicalVerification technicalVerification) {
		logger.debug(Literal.LEAVING);

		if (technicalVerification.isNew()) {
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
		
		if(fromLoanOrg) {
			north.setVisible(false);
			south.setVisible(false);
		}

		doWriteBeanToComponents(technicalVerification);
		if (!fromLoanOrg) {
			setDialog(DialogType.EMBEDDED);
		} else {
			window_TechnicalVerificationDialog.setHeight("100%");
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

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		CollateralSetup collateralSetup = getCollateralSetupService().getCollateralSetupByRef(this.collateralReference.getValue(), "", true);
		if (collateralSetup != null) {
			map.put("collateralSetup", collateralSetup);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null, map);
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

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		 CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.technicalVerification.getCustId());
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
		logger.debug(Literal.LEAVING);

		if (this.verificationDate.isVisible() && !this.verificationDate.isReadonly()) {
			this.verificationDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_TechnicalVerificationDialog_VerificationDate.value"), true,
							DateUtil.getDatePart(technicalVerification.getCreatedOn()),
							DateUtil.getDatePart(DateUtil.getSysDate()), true));
		}
		
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_AgentCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
			
		if (!this.reason.isReadonly()) {
			this.reason.setConstraint(new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_Reason.value"), null, this.reason.isMandatory(), true));
		}

		if (!this.summaryRemarks.isReadonly()) {
			this.summaryRemarks.setConstraint(new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_Remarks.value"), PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		if (!this.valuationAmount.isReadonly()) {
			this.valuationAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_TechnicalVerificationDialog_ValuationAmount.value"), PennantConstants.defaultCCYDecPos, true, false));
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog
	 * controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		
		this.agentCode.setErrorMessage("");
		this.agentName.setErrorMessage("");
		this.recommendations.setErrorMessage("");
		this.reason.setErrorMessage("");
		this.summaryRemarks.setErrorMessage("");
		this.valuationAmount.setErrorMessage("");
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a TechnicalVerification object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final TechnicalVerification entity = new TechnicalVerification();
		BeanUtils.copyProperties(this.technicalVerification, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ entity.getKeyReference();
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
		logger.debug(Literal.LEAVING);

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
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * @throws ParseException 
	 */
	public void doSave() throws ParseException {
		logger.debug(Literal.ENTERING);
		
		final TechnicalVerification tv = new TechnicalVerification();
		BeanUtils.copyProperties(this.technicalVerification, tv);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(tv);

		isNew = tv.isNew();
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

		if (documentDetailDialogCtrl != null) {
			tv.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			tv.setDocuments(getTechnicalVerification().getDocuments());
		}

		try {
			if (doProcess(tv, tranType)) {
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
	private boolean doProcess(TechnicalVerification tv, String tranType) {
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
					if (PennantConstants.RECORD_TYPE_DEL.equals(tv.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(tv.getRecordType());
							details.setNewRecord(true);
						}
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
		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
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
		AuditDetail auditDetail = new AuditDetail(tranType, 1, technicalVerification.getBefImage(), technicalVerification);
		return new AuditHeader(getReference(), null, null, null, auditDetail, technicalVerification.getUserDetails(),
				getOverideMap());
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
	
}
