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
package com.pennant.webui.verification.personaldiscussion;

import java.sql.Timestamp;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
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
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
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
import com.pennanttech.pennapps.pff.verification.StatuReasons;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.PDStatus;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.service.PersonalDiscussionService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Verification/PersonalDiscussion/personalDiscussionDialog.zul
 * file. <br>
 */
public class PersonalDiscussionDialogCtrl extends GFCBaseCtrl<PersonalDiscussion> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PersonalDiscussionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PersonalDiscussionDialog;
	protected Tab verificationDetails;
	protected Groupbox gb_basicDetails;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox custName;
	protected Textbox addrType;
	protected Textbox houseNo;
	protected Textbox flatNo;
	protected Textbox street;
	protected Textbox addressLine1;
	protected Textbox addressLine2;
	protected Textbox postBox;
	protected Textbox country;
	protected Label countryDesc;
	protected Textbox province;
	protected Label provinceDesc;
	protected Textbox city;
	protected Label CityDesc;
	protected Textbox zipCode;
	protected Textbox contactNumber1;
	protected Textbox contactNumber2;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected String selectMethodName = "onSelectTab";

	protected Tabpanel observationsFieldTabPanel;

	protected Groupbox gb_summary;
	protected Datebox verificationDate;
	protected Textbox agentCode;
	protected Textbox agentName;
	protected Combobox recommendations;
	protected ExtendedCombobox reason;
	protected Textbox summaryRemarks;
	protected North north;
	protected South south;
	protected Space space_AgentCode;
	protected Space space_AgentName;
	private PersonalDiscussion personalDiscussion;
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient PersonalDiscussionListCtrl personalDiscussionListCtrl;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;
	private boolean fromLoanOrg;

	@Autowired
	private transient PersonalDiscussionService personalDiscussionService;

	protected Button btnSearchCustomerDetails;

	/**
	 * default constructor.<br>
	 */
	public PersonalDiscussionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PersonalDiscussionDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PersonalDiscussionDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PersonalDiscussionDialog);

		try {
			// Get the required arguments.
			this.personalDiscussion = (PersonalDiscussion) arguments.get("personalDiscussion");

			if (arguments.get("personalDiscussionListCtrl") != null) {
				this.personalDiscussionListCtrl = (PersonalDiscussionListCtrl) arguments
						.get("personalDiscussionListCtrl");
			}

			if (arguments.get("LOAN_ORG") != null) {
				fromLoanOrg = true;
				enqiryModule = true;
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (this.personalDiscussion == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			PersonalDiscussion personalDiscussion = new PersonalDiscussion();
			BeanUtils.copyProperties(this.personalDiscussion, personalDiscussion);
			this.personalDiscussion.setBefImage(personalDiscussion);

			// Render the page and display the data.
			doLoadWorkFlow(this.personalDiscussion.isWorkflow(), this.personalDiscussion.getWorkflowId(),
					this.personalDiscussion.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else if (fromLoanOrg) {
				setWorkFlowEnabled(true);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.personalDiscussion);
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
			reasonFilter[0] = new Filter("ReasonTypecode", StatuReasons.FISRES.getKey(), Filter.OP_EQUAL);
		}
		reason.setFilters(reasonFilter);

		this.agentCode.setMaxlength(8);
		this.agentName.setMaxlength(50);
		this.verificationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.summaryRemarks.setMaxlength(500);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PersonalDiscussionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PersonalDiscussionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PersonalDiscussionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PersonalDiscussionDialog_btnSave"));

		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
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
		doShowNotes(this.personalDiscussion);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		personalDiscussionListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.personalDiscussion.getBefImage());
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
	 * @param fi
	 * 
	 */
	public void doWriteBeanToComponents(PersonalDiscussion pd) {
		logger.debug(Literal.ENTERING);

		this.custCIF.setValue(pd.getCif());
		this.finReference.setValue(pd.getKeyReference());
		this.custName.setValue(pd.getName());
		this.addrType.setValue(pd.getAddressType());
		this.houseNo.setValue(pd.getHouseNumber());
		this.flatNo.setValue(pd.getFlatNumber());
		this.street.setValue(pd.getStreet());
		this.addressLine1.setValue(pd.getAddressLine1());
		this.addressLine2.setValue(pd.getAddressLine2());
		this.postBox.setValue(pd.getPoBox());
		this.country.setValue(pd.getCountry());
		this.countryDesc.setValue(pd.getCountryDesc());
		this.city.setValue(pd.getCity());
		this.CityDesc.setValue(pd.getCityDesc());
		this.province.setValue(pd.getProvince());
		this.provinceDesc.setValue(pd.getProvinceDesc());
		this.zipCode.setValue(pd.getZipCode());
		this.contactNumber1.setValue(pd.getContactNumber1());
		this.contactNumber2.setValue(pd.getContactNumber2());

		this.verificationDate.setValue(pd.getVerifiedDate());
		if (!fromLoanOrg) {
			if (getFirstTaskOwner().equals(getRole()) && pd.getVerifiedDate() == null) {
				this.verificationDate.setValue(SysParamUtil.getAppDate());
			}
		}
		this.agentCode.setValue(pd.getAgentCode());
		this.agentName.setValue(pd.getAgentName());
		this.recommendations.setValue(String.valueOf(pd.getStatus()));
		if (!pd.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty(pd.getReasonCode()),
					StringUtils.trimToEmpty(pd.getReasonDesc()));
			if (pd.getReason() != null) {
				this.reason.setAttribute("ReasonId", pd.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}
		if (!pd.isNewRecord()) {
			visibleComponent(pd.getStatus());
		}
		this.summaryRemarks.setValue(pd.getSummaryRemarks());

		fillComboBox(this.recommendations, pd.getStatus(), PDStatus.getList());

		this.recordStatus.setValue(pd.getRecordStatus());
		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Verification details
		appendVerificationFieldDetails(pd);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append verification extended field details
	 */
	private void appendVerificationFieldDetails(PersonalDiscussion pd) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.VERIFICATION_MODULE, ExtendedFieldConstants.VERIFICATION_PD);

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
					.getExtendedFieldRender(String.valueOf(pd.getVerificationId()), tableName.toString(), "_View");
			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			extendedFieldCtrl.setTab(this.verificationDetails);
			pd.setExtendedFieldHeader(extendedFieldHeader);
			pd.setExtendedFieldRender(extendedFieldRender);

			if (pd.getBefImage() != null) {
				pd.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				pd.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("PersonalDiscussionDialog_PersonalDiscussionExtFields"));
			extendedFieldCtrl.setWindow(this.window_PersonalDiscussionDialog);
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
		logger.debug(Literal.ENTERING);
		createTab("DOCUMENTDETAIL", true);
		final Map<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getPersonalDiscussion().getDocuments());
		map.put("module", DocumentCategories.VERIFICATION_PD.getKey());
		map.put("enqModule", this.enqiryModule);
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
		map.put("moduleName", VerificationType.PD.name());
		map.put("enqiryModule", enqiryModule);
		map.put("isEditable", !isReadOnly("PersonalDiscussionDialog_Documents"));

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
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 */
	public void doWriteComponentsToBean(PersonalDiscussion pd) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		pd.setCif(this.custCIF.getValue());
		pd.setKeyReference(this.finReference.getValue());
		pd.setName(this.custName.getValue());
		pd.setAddressType(this.addrType.getValue());
		pd.setHouseNumber(this.houseNo.getValue());
		pd.setStreet(this.street.getValue());
		pd.setAddressLine1(this.addressLine1.getValue());
		pd.setAddressLine2(this.addressLine2.getValue());
		pd.setAddressLine3(StringUtils.trimToEmpty(this.personalDiscussion.getAddressLine3()));
		pd.setAddressLine4(StringUtils.trimToEmpty(this.personalDiscussion.getAddressLine4()));
		pd.setAddressLine5(StringUtils.trimToEmpty(this.personalDiscussion.getAddressLine5()));
		pd.setPoBox(this.postBox.getValue());
		pd.setCountry(this.country.getValue());
		pd.setProvince(this.province.getValue());
		pd.setCity(this.city.getValue());
		pd.setZipCode(this.zipCode.getValue());
		pd.setContactNumber1(this.contactNumber1.getValue());
		pd.setContactNumber2(this.contactNumber2.getValue());

		// Extended Field validations
		if (pd.getExtendedFieldHeader() != null) {
			pd.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		try {
			if (this.verificationDate.getValue() != null) {
				pd.setVerifiedDate(this.verificationDate.getValue());
			} else {
				pd.setVerifiedDate(SysParamUtil.getAppDate());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			pd.setAgentCode(this.agentCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			pd.setAgentName(this.agentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.recommendations.isDisabled()
					&& PDStatus.SELECT.getKey().equals(Integer.parseInt(getComboboxValue(this.recommendations)))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_PersonalDiscussionDialog_Recommendations.value") }));
			} else {
				pd.setStatus(Integer.parseInt(getComboboxValue(this.recommendations)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			pd.setReasonDesc(this.reason.getDescription());
			pd.setReasonCode(this.reason.getValue());
			this.reason.getValidatedValue();
			Object object = this.reason.getAttribute("ReasonId");
			if (object != null) {
				pd.setReason(Long.parseLong(object.toString()));
			} else {
				pd.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			pd.setSummaryRemarks(this.summaryRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

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
		if (type == PDStatus.NEGATIVE.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.PDNTVRTY.getKey();
		} else if (type == PDStatus.REFERTOCREDIT.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.PDRFRRTY.getKey();
		} else if (type == PDStatus.POSITIVE.getKey()) {
			this.reason.setMandatoryStyle(false);
			reasonType = StatuReasons.PDPOSTVRTY.getKey();
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
	 * @param personalDiscusssion The entity that need to be render.
	 */
	public void doShowDialog(PersonalDiscussion personalDiscussion) {
		logger.debug(Literal.ENTERING);

		if (personalDiscussion.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(personalDiscussion.getRecordType())) {
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
			this.south.setVisible(false);
		}

		if (fromLoanOrg) {
			north.setVisible(false);
			south.setVisible(false);
		}

		doWriteBeanToComponents(personalDiscussion);
		if (!fromLoanOrg) {
			setDialog(DialogType.EMBEDDED);
		} else {
			window_PersonalDiscussionDialog.setHeight("100%");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "Customer CIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustomerDetails(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = new HashMap<String, Object>();

		CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.personalDiscussion.getCustId());
		String pageName = PennantAppUtil.getCustomerPageName();
		if (customerDetails != null) {
			map.put("customerDetails", customerDetails);
			map.put("isEnqProcess", true);
			map.put("CustomerEnq", true);
			Executions.createComponents(pageName, null, map);
		}

		logger.debug(Literal.LEAVING);
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
					new PTDateValidator(Labels.getLabel("label_PersonalDiscussionDialog_VerificationDate.value"), true,
							DateUtil.getDatePart(personalDiscussion.getCreatedOn()),
							DateUtil.getDatePart(SysParamUtil.getAppDate()), true));
		}
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_PersonalDiscussionDialog_AgentCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PersonalDiscussionDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_PersonalDiscussionDialog_Status.value"), PDStatus.getList(), true));
		}
		if (!this.reason.isReadonly()) {
			this.reason
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PersonalDiscussionDialog_Reason.value"),
							null, this.reason.isMandatory(), true));
		}
		if (!this.summaryRemarks.isReadonly()) {
			this.summaryRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PersonalDiscussionDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.agentCode.setConstraint("");
		this.agentName.setConstraint("");
		this.recommendations.setConstraint("");
		this.reason.setConstraint("");
		this.summaryRemarks.setConstraint("");
		this.summaryRemarks.setConstraint("");
		this.verificationDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PersonalDiscussion entity = new PersonalDiscussion();
		BeanUtils.copyProperties(this.personalDiscussion, entity);

		doDelete(String.valueOf(entity.getId()), entity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.personalDiscussion.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("PersonalDiscussionDialog_Date"), this.verificationDate);
		readOnlyComponent(isReadOnly("PersonalDiscussionDialog_AgentCode"), this.agentCode);
		readOnlyComponent(isReadOnly("PersonalDiscussionDialog_AgentName"), this.agentName);
		readOnlyComponent(isReadOnly("PersonalDiscussionDialog_Recommendations"), this.recommendations);
		readOnlyComponent(isReadOnly("PersonalDiscussionDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("PersonalDiscussionDialog_AgentRemarks"), this.summaryRemarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.personalDiscussion.isNewRecord()) {
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
		this.custName.setReadonly(true);
		this.addrType.setReadonly(true);
		this.houseNo.setReadonly(true);
		this.flatNo.setReadonly(true);
		this.street.setReadonly(true);
		this.addressLine1.setReadonly(true);
		this.addressLine2.setReadonly(true);
		this.postBox.setReadonly(true);
		this.country.setReadonly(true);
		this.province.setReadonly(true);
		this.city.setReadonly(true);
		this.zipCode.setReadonly(true);
		this.contactNumber1.setReadonly(true);
		this.contactNumber2.setReadonly(true);

		this.agentCode.setReadonly(true);
		this.agentName.setReadonly(true);
		this.recommendations.setDisabled(true);
		this.reason.setReadonly(true);
		this.summaryRemarks.setReadonly(true);

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
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final PersonalDiscussion pd = new PersonalDiscussion();
		BeanUtils.copyProperties(this.personalDiscussion, pd);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(pd);

		isNew = pd.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(pd.getRecordType())) {
				pd.setVersion(pd.getVersion() + 1);
				if (isNew) {
					pd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					pd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					pd.setNewRecord(true);
				}
			}
		} else {
			pd.setVersion(pd.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Document Details Saving

		if (SysParamUtil.isAllowed(SMTParameterConstants.PD_DOCUMENT_MANDATORY)
				&& this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("submit")) {
			if (documentDetailDialogCtrl != null
					&& CollectionUtils.sizeIsEmpty(documentDetailDialogCtrl.getDocumentDetailsList())) {
				MessageUtil.showError(Labels.getLabel("VERIFICATIONS_DOCUMENT_MANDATORY"));
				return;
			}
		}

		if (documentDetailDialogCtrl != null) {
			pd.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			pd.setDocuments(getPersonalDiscussion().getDocuments());
		}

		try {
			if (doProcess(pd, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(pd.getRoleCode(), pd.getNextRoleCode(),
						pd.getKeyReference(), " Loan ", pd.getRecordStatus(), getNextTaskId());
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
	protected boolean doProcess(PersonalDiscussion personalDiscussion, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		personalDiscussion.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		personalDiscussion.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		personalDiscussion.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			personalDiscussion.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(personalDiscussion.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, personalDiscussion);
				}

				if (isNotesMandatory(taskId, personalDiscussion)) {
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

			personalDiscussion.setTaskId(taskId);
			personalDiscussion.setNextTaskId(nextTaskId);
			personalDiscussion.setRoleCode(getRole());
			personalDiscussion.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (personalDiscussion.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = personalDiscussion.getExtendedFieldRender();
				details.setReference(String.valueOf(personalDiscussion.getVerificationId()));
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(personalDiscussion.getRecordStatus());
				details.setRecordType(personalDiscussion.getRecordType());
				details.setVersion(personalDiscussion.getVersion());
				details.setWorkflowId(personalDiscussion.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(personalDiscussion.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(personalDiscussion.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(personalDiscussion.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			// Document Details
			if (personalDiscussion.getDocuments() != null && !personalDiscussion.getDocuments().isEmpty()) {
				for (DocumentDetails details : personalDiscussion.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}

					details.setReferenceId(String.valueOf(personalDiscussion.getVerificationId()));
					details.setDocModule(VerificationType.PD.getCode());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(personalDiscussion.getRecordStatus());
					details.setWorkflowId(personalDiscussion.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(personalDiscussion.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(personalDiscussion.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(personalDiscussion, tranType);
			String operationRefs = getServiceOperations(taskId, personalDiscussion);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(personalDiscussion, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(personalDiscussion, tranType);
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
		PersonalDiscussion personalDiscussion = (PersonalDiscussion) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = personalDiscussionService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = personalDiscussionService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = personalDiscussionService.doApprove(auditHeader);

					if (personalDiscussion.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = personalDiscussionService.doReject(auditHeader);
					if (personalDiscussion.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_PersonalDiscussionDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_PersonalDiscussionDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.personalDiscussion), true);
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

	private AuditHeader getAuditHeader(PersonalDiscussion personalDiscussion, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, personalDiscussion.getBefImage(), personalDiscussion);
		return new AuditHeader(getReference(), null, null, null, auditDetail, personalDiscussion.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.personalDiscussion.getId());
	}

	public PersonalDiscussion getPersonalDiscussion() {
		return personalDiscussion;
	}

	public void setPersonalDiscussion(PersonalDiscussion personalDiscussion) {
		this.personalDiscussion = personalDiscussion;
	}

	public PersonalDiscussionListCtrl getPersonalDiscussionListCtrl() {
		return personalDiscussionListCtrl;
	}

	public void setPersonalDiscussionListCtrl(PersonalDiscussionListCtrl personalDiscussionListCtrl) {
		this.personalDiscussionListCtrl = personalDiscussionListCtrl;
	}

	public void setPersonalDiscussionService(PersonalDiscussionService personalDiscussionService) {
		this.personalDiscussionService = personalDiscussionService;
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

}
