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
package com.pennant.webui.verification.fieldinvestigation;

import java.sql.Timestamp;
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
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.service.FieldInvestigationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Verification/FieldInvestigation/fieldInvestigationDialog.zul
 * file. <br>
 */
public class FieldInvestigationDialogCtrl extends GFCBaseCtrl<FieldInvestigation> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FieldInvestigationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FieldInvestigationDialog;
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
	private FieldInvestigation fieldInvestigation;
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient FieldInvestigationListCtrl fieldInvestigationListCtrl;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	@Autowired
	private transient FieldInvestigationService fieldInvestigationService;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;
	private boolean fromLoanOrg;
	protected Button btnSearchCustomerDetails;

	/**
	 * default constructor.<br>
	 */
	public FieldInvestigationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FieldInvestigationDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FieldInvestigationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FieldInvestigationDialog);

		try {
			// Get the required arguments.
			this.fieldInvestigation = (FieldInvestigation) arguments.get("fieldInvestigation");

			if (arguments.get("fieldInvestigationListCtrl") != null) {
				this.fieldInvestigationListCtrl = (FieldInvestigationListCtrl) arguments
						.get("fieldInvestigationListCtrl");
			}

			if (arguments.get("LOAN_ORG") != null) {
				fromLoanOrg = true;
				enqiryModule = true;
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (this.fieldInvestigation == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FieldInvestigation fieldInvestigation = new FieldInvestigation();
			BeanUtils.copyProperties(this.fieldInvestigation, fieldInvestigation);
			this.fieldInvestigation.setBefImage(fieldInvestigation);

			// Render the page and display the data.
			doLoadWorkFlow(this.fieldInvestigation.isWorkflow(), this.fieldInvestigation.getWorkflowId(),
					this.fieldInvestigation.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else if (fromLoanOrg) {
				setWorkFlowEnabled(true);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.fieldInvestigation);
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
		this.space_AgentCode.setVisible(!ImplementationConstants.VER_INIT_AGENT_MANDATORY);
		this.space_AgentName.setVisible(!ImplementationConstants.VER_INIT_FROM_OUTSIDE);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnSave"));

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
		doShowNotes(this.fieldInvestigation);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		fieldInvestigationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.fieldInvestigation.getBefImage());
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
	public void doWriteBeanToComponents(FieldInvestigation fi) {
		logger.debug(Literal.ENTERING);

		this.custCIF.setValue(fi.getCif());
		this.finReference.setValue(fi.getKeyReference());
		this.custName.setValue(fi.getName());
		this.addrType.setValue(fi.getAddressType());
		this.houseNo.setValue(fi.getHouseNumber());
		this.flatNo.setValue(fi.getFlatNumber());
		this.street.setValue(fi.getStreet());
		this.addressLine1.setValue(fi.getAddressLine1());
		this.addressLine2.setValue(fi.getAddressLine2());
		this.postBox.setValue(fi.getPoBox());
		this.country.setValue(fi.getCountry());
		this.countryDesc.setValue(fi.getCountryDesc());
		this.city.setValue(fi.getCity());
		this.CityDesc.setValue(fi.getCityDesc());
		this.province.setValue(fi.getProvince());
		this.provinceDesc.setValue(fi.getProvinceDesc());
		this.zipCode.setValue(fi.getZipCode());
		this.contactNumber1.setValue(fi.getContactNumber1());
		this.contactNumber2.setValue(fi.getContactNumber2());

		this.verificationDate.setValue(fi.getVerifiedDate());
		if (!fromLoanOrg) {
			if (getFirstTaskOwner().equals(getRole()) && fi.getVerifiedDate() == null) {
				this.verificationDate.setValue(SysParamUtil.getAppDate());
			}
		}
		this.agentCode.setValue(fi.getAgentCode());
		this.agentName.setValue(fi.getAgentName());
		this.recommendations.setValue(String.valueOf(fi.getStatus()));
		if (!fi.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty(fi.getReasonCode()),
					StringUtils.trimToEmpty(fi.getReasonDesc()));
			if (fi.getReason() != null) {
				this.reason.setAttribute("ReasonId", fi.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}
		if (!fi.isNewRecord()) {
			visibleComponent(fi.getStatus());
		}
		this.summaryRemarks.setValue(fi.getSummaryRemarks());

		fillComboBox(this.recommendations, fi.getStatus(), FIStatus.getList());

		this.recordStatus.setValue(fi.getRecordStatus());
		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Verification details
		appendVerificationFieldDetails(fi);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append verification extended field details
	 */
	private void appendVerificationFieldDetails(FieldInvestigation fi) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.VERIFICATION_MODULE, ExtendedFieldConstants.VERIFICATION_FI);

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
					.getExtendedFieldRender(String.valueOf(fi.getVerificationId()), tableName.toString(), "_View");
			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			extendedFieldCtrl.setTab(this.verificationDetails);
			fi.setExtendedFieldHeader(extendedFieldHeader);
			fi.setExtendedFieldRender(extendedFieldRender);

			if (fi.getBefImage() != null) {
				fi.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				fi.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("FieldInvestigationDialog_FieldInvestigationExtFields"));
			extendedFieldCtrl.setWindow(this.window_FieldInvestigationDialog);
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
		map.put("documentDetails", getFieldInvestigation().getDocuments());
		map.put("module", DocumentCategories.VERIFICATION_FI.getKey());
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
		map.put("moduleName", VerificationType.FI.name());
		map.put("enqiryModule", enqiryModule);
		map.put("isEditable", !isReadOnly("FieldInvestigationDialog_Documents"));

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
	public void doWriteComponentsToBean(FieldInvestigation fi) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		fi.setCif(this.custCIF.getValue());
		fi.setKeyReference(this.finReference.getValue());
		fi.setName(this.custName.getValue());
		fi.setAddressType(this.addrType.getValue());
		fi.setHouseNumber(this.houseNo.getValue());
		fi.setStreet(this.street.getValue());
		fi.setAddressLine1(this.addressLine1.getValue());
		fi.setAddressLine2(this.addressLine2.getValue());
		fi.setAddressLine3(StringUtils.trimToEmpty(this.fieldInvestigation.getAddressLine3()));
		fi.setAddressLine4(StringUtils.trimToEmpty(this.fieldInvestigation.getAddressLine4()));
		fi.setAddressLine5(StringUtils.trimToEmpty(this.fieldInvestigation.getAddressLine5()));
		fi.setPoBox(this.postBox.getValue());
		fi.setCountry(this.country.getValue());
		fi.setProvince(this.province.getValue());
		fi.setCity(this.city.getValue());
		fi.setZipCode(this.zipCode.getValue());
		fi.setContactNumber1(this.contactNumber1.getValue());
		fi.setContactNumber2(this.contactNumber2.getValue());

		// Extended Field validations
		if (fi.getExtendedFieldHeader() != null) {
			fi.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		try {
			Calendar calDate = Calendar.getInstance();
			if (this.verificationDate.getValue() != null) {
				calDate.setTime(this.verificationDate.getValue());
				Calendar calTimeNow = Calendar.getInstance();
				calDate.set(Calendar.HOUR_OF_DAY, calTimeNow.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calTimeNow.get(Calendar.MINUTE));
				calDate.set(Calendar.SECOND, calTimeNow.get(Calendar.SECOND));
				fi.setVerifiedDate(new Timestamp(calDate.getTimeInMillis()));
			} else {
				fi.setVerifiedDate(SysParamUtil.getAppDate());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setAgentCode(this.agentCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setAgentName(this.agentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.recommendations.isDisabled()
					&& FIStatus.SELECT.getKey().equals(Integer.parseInt(getComboboxValue(this.recommendations)))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FieldInvestigationDialog_Recommendations.value") }));
			} else {
				fi.setStatus(Integer.parseInt(getComboboxValue(this.recommendations)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setReasonDesc(this.reason.getDescription());
			fi.setReasonCode(this.reason.getValue());
			this.reason.getValidatedValue();
			Object object = this.reason.getAttribute("ReasonId");
			if (object != null) {
				fi.setReason(Long.parseLong(object.toString()));
			} else {
				fi.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setSummaryRemarks(this.summaryRemarks.getValue());
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
		if (type == FIStatus.NEGATIVE.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.FINTVRTY.getKey();
		} else if (type == FIStatus.REFER_TO_CREDIT.getKey()) {
			this.reason.setMandatoryStyle(true);
			reasonType = StatuReasons.FIRFRRTY.getKey();
		} else if (type == FIStatus.POSITIVE.getKey()) {
			this.reason.setMandatoryStyle(false);
			reasonType = StatuReasons.FIPOSTVRTY.getKey();
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
	 * @param fieldInvestigation The entity that need to be render.
	 */
	public void doShowDialog(FieldInvestigation fieldInvestigation) {
		logger.debug(Literal.ENTERING);

		if (fieldInvestigation.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(fieldInvestigation.getRecordType())) {
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

		doWriteBeanToComponents(fieldInvestigation);
		if (!fromLoanOrg) {
			setDialog(DialogType.EMBEDDED);
		} else {
			window_FieldInvestigationDialog.setHeight("100%");
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

		CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.fieldInvestigation.getCustId());
		// Loading the customer page as per the system param
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
					new PTDateValidator(Labels.getLabel("label_FieldInvestigationDialog_VerificationDate.value"), true,
							DateUtil.getDatePart(fieldInvestigation.getCreatedOn()),
							DateUtil.getDatePart(SysParamUtil.getAppDate()), true));// Calendar.getInstance().getTime()
		}
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FieldInvestigationDialog_AgentCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, ImplementationConstants.VER_INIT_FROM_OUTSIDE));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_FieldInvestigationDialog_Status.value"), FIStatus.getList(), true));
		}
		if (!this.reason.isReadonly()) {
			this.reason
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_Reason.value"),
							null, this.reason.isMandatory(), true));
		}
		if (!this.summaryRemarks.isReadonly()) {
			this.summaryRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_Remarks.value"),
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

		final FieldInvestigation entity = new FieldInvestigation();
		BeanUtils.copyProperties(this.fieldInvestigation, entity);

		doDelete(String.valueOf(entity.getId()), entity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.fieldInvestigation.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Date"), this.verificationDate);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_AgentCode"), this.agentCode);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_AgentName"), this.agentName);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Recommendations"), this.recommendations);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_AgentRemarks"), this.summaryRemarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.fieldInvestigation.isNewRecord()) {
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
		final FieldInvestigation fi = new FieldInvestigation();
		BeanUtils.copyProperties(this.fieldInvestigation, fi);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(fi);

		isNew = fi.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(fi.getRecordType())) {
				fi.setVersion(fi.getVersion() + 1);
				if (isNew) {
					fi.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					fi.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					fi.setNewRecord(true);
				}
			}
		} else {
			fi.setVersion(fi.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Document Details Saving

		if (SysParamUtil.isAllowed(SMTParameterConstants.FI_DOCUMENT_MANDATORY)
				&& this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("submit")) {
			if (documentDetailDialogCtrl != null
					&& CollectionUtils.sizeIsEmpty(documentDetailDialogCtrl.getDocumentDetailsList())) {
				MessageUtil.showError(Labels.getLabel("VERIFICATIONS_DOCUMENT_MANDATORY"));
				return;
			}
		}

		if (documentDetailDialogCtrl != null) {
			fi.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			fi.setDocuments(getFieldInvestigation().getDocuments());
		}

		try {
			if (doProcess(fi, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(fi.getRoleCode(), fi.getNextRoleCode(),
						fi.getKeyReference(), " Loan ", fi.getRecordStatus(), getNextTaskId());
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
	protected boolean doProcess(FieldInvestigation fieldInvestigation, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		fieldInvestigation.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		fieldInvestigation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		fieldInvestigation.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			fieldInvestigation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(fieldInvestigation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, fieldInvestigation);
				}

				if (isNotesMandatory(taskId, fieldInvestigation)) {
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

			fieldInvestigation.setTaskId(taskId);
			fieldInvestigation.setNextTaskId(nextTaskId);
			fieldInvestigation.setRoleCode(getRole());
			fieldInvestigation.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (fieldInvestigation.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = fieldInvestigation.getExtendedFieldRender();
				details.setReference(String.valueOf(fieldInvestigation.getVerificationId()));
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(fieldInvestigation.getRecordStatus());
				details.setRecordType(fieldInvestigation.getRecordType());
				details.setVersion(fieldInvestigation.getVersion());
				details.setWorkflowId(fieldInvestigation.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(fieldInvestigation.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(fieldInvestigation.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(fieldInvestigation.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			// Document Details
			if (fieldInvestigation.getDocuments() != null && !fieldInvestigation.getDocuments().isEmpty()) {
				for (DocumentDetails details : fieldInvestigation.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}

					details.setReferenceId(String.valueOf(fieldInvestigation.getVerificationId()));
					details.setDocModule(VerificationType.FI.getCode());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(fieldInvestigation.getRecordStatus());
					details.setWorkflowId(fieldInvestigation.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					details.setCustomerCif(fieldInvestigation.getCif());
					details.setCustId(fieldInvestigation.getCustId());
					details.setFinReference(fieldInvestigation.getKeyReference());
					if (PennantConstants.RECORD_TYPE_DEL.equals(fieldInvestigation.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(fieldInvestigation.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(fieldInvestigation, tranType);
			String operationRefs = getServiceOperations(taskId, fieldInvestigation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(fieldInvestigation, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(fieldInvestigation, tranType);
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
		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = fieldInvestigationService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = fieldInvestigationService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = fieldInvestigationService.doApprove(auditHeader);

					if (fieldInvestigation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = fieldInvestigationService.doReject(auditHeader);
					if (fieldInvestigation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FieldInvestigationDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_FieldInvestigationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.fieldInvestigation), true);
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

	private AuditHeader getAuditHeader(FieldInvestigation fieldInvestigation, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, fieldInvestigation.getBefImage(), fieldInvestigation);
		return new AuditHeader(getReference(), null, null, null, auditDetail, fieldInvestigation.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.fieldInvestigation.getId());
	}

	public FieldInvestigation getFieldInvestigation() {
		return fieldInvestigation;
	}

	public void setFieldInvestigation(FieldInvestigation fieldInvestigation) {
		this.fieldInvestigation = fieldInvestigation;
	}

	public FieldInvestigationListCtrl getFieldInvestigationListCtrl() {
		return fieldInvestigationListCtrl;
	}

	public void setFieldInvestigationListCtrl(FieldInvestigationListCtrl fieldInvestigationListCtrl) {
		this.fieldInvestigationListCtrl = fieldInvestigationListCtrl;
	}

	public void setFieldInvestigationService(FieldInvestigationService fieldInvestigationService) {
		this.fieldInvestigationService = fieldInvestigationService;
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
