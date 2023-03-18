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

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : BuilderCompanyDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-05-2017 * *
 * Modified Date : 22-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.buildercompany;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.systemmasters.BuilderCompanyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/masters/BuilderCompany/builderCompanyDialog.zul file. <br>
 */
public class BuilderCompanyDialogCtrl extends GFCBaseCtrl<BuilderCompany> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BuilderCompanyDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BuilderCompanyDialog;
	protected Textbox name;
	protected ExtendedCombobox segmentation;
	protected ExtendedCombobox groupId;
	private BuilderCompany builderCompany; // overhanded per param
	protected Longbox custId;
	protected Textbox custCIF;
	protected Combobox apfType;
	protected Textbox peDevId;
	protected Combobox entityType;
	protected Textbox emailId;
	protected Combobox cityType;
	protected Textbox address1;
	protected Textbox address2;
	protected Textbox address3;
	protected ExtendedCombobox city;
	protected ExtendedCombobox state;
	protected ExtendedCombobox code;
	protected Intbox devavailablity;
	protected Decimalbox magnitude;
	protected Decimalbox absavailablity;
	protected Intbox totalProj;
	protected Combobox approved;
	protected Textbox remarks;
	protected Textbox panDetails;
	protected Textbox benfName;
	protected Textbox accountNo;
	protected Textbox bankName;
	protected ExtendedCombobox bankBranch;
	protected Textbox ifsc;
	protected CurrencyBox limitOnAmt;
	protected Decimalbox limitOnUnits;
	protected Intbox currentExpUni;
	protected CurrencyBox currentExpAmt;
	protected Datebox dateOfInCop;
	protected Intbox noOfProj;
	protected Intbox assHLPlayers;
	protected Intbox onGoingProj;
	protected Intbox expInBusiness;
	protected Combobox recommendation;
	protected Intbox magintudeInLacs;
	protected Intbox noOfProjCons;
	protected Label bankNameDesc;
	protected Button btnSearchCustId;
	protected Label custDesc;
	protected JdbcSearchObject<Customer> custIdSearchObject;

	private CustomerDetailsService customerDetailsService;
	private transient BuilderCompanyListCtrl buildercompanyListCtrl; // overhanded per param
	private transient BuilderCompanyService builderCompanyService;
	private List<ValueLabel> aPFTypeList = PennantStaticListUtil.getApfType();
	private List<ValueLabel> locationList = PennantStaticListUtil.getcityType();
	private List<ValueLabel> approvedList = PennantStaticListUtil.getapproved();
	private List<ValueLabel> recommendationList = PennantStaticListUtil.getRecommendation();
	private List<ValueLabel> builderEntityTypeList = PennantStaticListUtil.getBuilderEntityType();

	protected Space space_NoOfProj;
	protected Space space_AssHLPlayers;
	protected Space space_OnGoingProj;
	protected Space space_ExpInBusiness;
	protected Space space_Recommendation;
	protected Space space_MagintudeInLacs;
	protected Space space_NoOfProjCons;
	protected Space space_CityType;
	protected Space space_Active;
	protected Checkbox active;

	/**
	 * default constructor.<br>
	 */
	public BuilderCompanyDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BuilderCompanyDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.builderCompany.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BuilderCompanyDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_BuilderCompanyDialog);

		try {
			// Get the required arguments.
			this.builderCompany = (BuilderCompany) arguments.get("buildercompany");
			this.buildercompanyListCtrl = (BuilderCompanyListCtrl) arguments.get("buildercompanyListCtrl");

			if (this.builderCompany == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BuilderCompany builderCompany = new BuilderCompany();
			BeanUtils.copyProperties(this.builderCompany, builderCompany);
			this.builderCompany.setBefImage(builderCompany);

			// Render the page and display the data.
			doLoadWorkFlow(this.builderCompany.isWorkflow(), this.builderCompany.getWorkflowId(),
					this.builderCompany.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "BuilderCompanyDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.builderCompany);
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

		this.name.setMaxlength(50);
		this.segmentation.setModuleName("LovFieldDetail");
		this.segmentation.setMandatoryStyle(true);
		this.segmentation.setValueColumn("FieldCodeValue");
		this.segmentation.setDescColumn("ValueDesc");
		this.segmentation.setDisplayStyle(2);
		this.segmentation.setValidateColumns(new String[] { "FieldCodeValue" });
		Filter segmentFilter[] = new Filter[1];
		segmentFilter[0] = new Filter("FieldCode", "SEGMENT", Filter.OP_EQUAL);
		this.segmentation.setFilters(segmentFilter);
		this.groupId.setModuleName("BuilderGroup");
		this.groupId.setValueColumn("Id");
		this.groupId.setDescColumn("Name");
		this.groupId.setValidateColumns(new String[] { "Id" });
		this.groupId.setMandatoryStyle(true);
		this.groupId.setValueType(DataType.LONG);
		this.custId.setVisible(false);
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		this.city.setMandatoryStyle(true);

		// state
		this.state.setModuleName("Province");
		this.state.setValueColumn("CPProvince");
		this.state.setDescColumn("CPProvinceName");
		this.state.setValidateColumns(new String[] { "CPProvince" });
		this.state.setMandatoryStyle(true);

		this.code.setModuleName("PinCode");
		this.code.setValueColumn("PinCode");
		this.code.setDescColumn("AreaName");
		this.code.setValidateColumns(new String[] { "PinCode" });
		this.code.setMandatoryStyle(true);
		this.bankBranch.setModuleName("BankBranch");
		this.bankBranch.setValueColumn("BranchCode");
		this.bankBranch.setDescColumn("BranchDesc");
		this.bankBranch.setValidateColumns(new String[] { "BranchCode" });
		this.peDevId.setMaxlength(100);
		this.emailId.setMaxlength(100);
		this.address1.setMaxlength(255);
		this.address2.setMaxlength(255);
		this.address3.setMaxlength(255);
		this.devavailablity.setMaxlength(6);

		this.absavailablity.setMaxlength(19);
		this.absavailablity.setFormat(PennantConstants.rateFormate9);
		this.absavailablity.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.absavailablity.setScale(2);
		this.totalProj.setMaxlength(3);
		this.approved.setMaxlength(30);
		this.remarks.setMaxlength(500);
		this.panDetails.setMaxlength(10);
		this.benfName.setMaxlength(150);
		this.accountNo.setMaxlength(20);
		this.ifsc.setMaxlength(11);
		// this.limitOnAmt.setProperties(false, getCcyFormat());
		this.limitOnAmt.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.limitOnUnits.setMaxlength(16);
		this.limitOnUnits.setFormat(PennantConstants.rateFormate11);
		this.limitOnUnits.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.limitOnUnits.setScale(2);
		this.currentExpUni.setMaxlength(18);
		this.currentExpAmt.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.dateOfInCop.setFormat(PennantConstants.dateFormat);
		this.dateOfInCop.setConstraint("no future");
		this.noOfProj.setMaxlength(3);
		this.assHLPlayers.setMaxlength(3);
		this.onGoingProj.setMaxlength(3);
		this.expInBusiness.setMaxlength(3);
		this.recommendation.setMaxlength(50);
		this.noOfProjCons.setMaxlength(3);
		this.magintudeInLacs.setMaxlength(3);
		this.magnitude.setMaxlength(19);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnSave"));
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
		doShowNotes(this.builderCompany);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		buildercompanyListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.builderCompany.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$apfType(Event event) {
		logger.debug(Literal.ENTERING);
		String apfType = this.apfType.getSelectedItem().getValue();
		doEnableRequiredFields(apfType);
		logger.debug(Literal.LEAVING);
	}

	private void doEnableRequiredFields(String apfType) {
		if (PennantConstants.BRANCH_APF.equals(apfType)) {
			this.space_NoOfProj.setSclass("mandatory");
			this.space_AssHLPlayers.setSclass("mandatory");
			this.space_ExpInBusiness.setSclass("mandatory");
			this.space_MagintudeInLacs.setSclass("mandatory");
			this.space_NoOfProj.setSclass("mandatory");
			// this.space_NoOfProjCons.setSclass("mandatory");
			this.space_OnGoingProj.setSclass("mandatory");
			this.space_Recommendation.setSclass("mandatory");
			this.space_CityType.setSclass("mandatory");

		} else {
			this.space_NoOfProj.setSclass("");
			this.space_AssHLPlayers.setSclass("");
			this.space_ExpInBusiness.setSclass("");
			this.space_MagintudeInLacs.setSclass("");
			this.space_NoOfProj.setSclass("");
			// this.space_NoOfProjCons.setSclass("");
			this.space_OnGoingProj.setSclass("");
			this.space_Recommendation.setSclass("");
			this.space_CityType.setSclass("");
			this.recommendation.setConstraint("");
			this.recommendation.clearErrorMessage();
			this.assHLPlayers.clearErrorMessage();
			this.noOfProj.clearErrorMessage();
			this.magintudeInLacs.clearErrorMessage();
			this.expInBusiness.clearErrorMessage();
			this.onGoingProj.clearErrorMessage();
		}
	}

	public void onFulfill$bankBranch(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = this.bankBranch.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.bankBranch.setValue("");
			this.bankBranch.setDescription("");
			this.bankBranch.setAttribute("bankBranch", null);
			this.ifsc.setValue("");
			this.bankName.setValue("");
			this.bankNameDesc.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			this.bankBranch.setAttribute("bankBranch", details.getId());
			this.ifsc.setValue(details.getIFSC());
			this.bankName.setValue(details.getBankCode());
			this.bankNameDesc.setValue(details.getBankName());
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfillSegmentation(Event event) {
		logger.debug(Literal.ENTERING);

		if (!this.segmentation.getDescription().equals("")) {

		} else {

		}

		logger.debug(Literal.LEAVING);
	}

	/*
	 * public void onFulfill$groupId(Event event) throws InterruptedException { logger.debug("Entering" +
	 * event.toString());
	 * 
	 * Object dataObject = groupId.getObject(); if (dataObject instanceof String) {
	 * this.groupId.setValue(dataObject.toString()); this.groupId.setDescription(""); } else { BuilderGroup builderGroup
	 * = (BuilderGroup) dataObject; if (builderGroup != null) { this.groupId.setAttribute("groupId",
	 * builderGroup.getBuilderGroupId()); } }
	 * 
	 * logger.debug("Leaving"); }
	 */

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param builderCompany
	 * 
	 */
	public void doWriteBeanToComponents(BuilderCompany aBuilderCompany) {
		logger.debug(Literal.ENTERING);
		/* this.name.setValue(aBuilderCompany.getName()); */
		this.name.setValue(aBuilderCompany.getName());
		this.custCIF.setValue(aBuilderCompany.getCustCIF());
		this.segmentation.setValue(aBuilderCompany.getSegmentation(), aBuilderCompany.getSegmentationName());
		String groupId = "";
		if (aBuilderCompany.getGroupId() > 0) {
			groupId = String.valueOf(aBuilderCompany.getGroupId());
		}
		this.groupId.setValue(groupId, aBuilderCompany.getGroupIdName());
		fillComboBox(this.apfType, aBuilderCompany.getApfType(), this.aPFTypeList, "");
		this.peDevId.setValue(aBuilderCompany.getPeDevId());
		this.entityType.setValue(aBuilderCompany.getEntityType());
		this.emailId.setValue(aBuilderCompany.getEmailId());
		this.approved.setValue(aBuilderCompany.getApproved());
		this.cityType.setValue(aBuilderCompany.getCityType());
		this.address1.setValue(aBuilderCompany.getAddress1());
		this.address2.setValue(aBuilderCompany.getAddress2());
		this.address3.setValue(aBuilderCompany.getAddress3());
		this.city.setValue(aBuilderCompany.getCity());
		this.state.setValue(aBuilderCompany.getState());

		if (aBuilderCompany.getPinCodeId() != null) {
			this.code.setAttribute("pinCodeId", aBuilderCompany.getPinCodeId());
		} else {
			this.code.setAttribute("pinCodeId", null);
		}

		this.code.setValue(StringUtils.trimToEmpty(aBuilderCompany.getCode()),
				StringUtils.trimToEmpty(aBuilderCompany.getAreaName()));
		this.devavailablity.setValue(aBuilderCompany.getDevavailablity());
		this.magnitude.setValue(aBuilderCompany.getMagnitude());
		this.absavailablity.setValue(aBuilderCompany.getAbsavailablity());
		this.totalProj.setValue(aBuilderCompany.getTotalProj());
		this.approved.setValue(aBuilderCompany.getApproved());
		this.remarks.setValue(aBuilderCompany.getRemarks());
		this.panDetails.setValue(aBuilderCompany.getPanDetails());
		this.benfName.setValue(aBuilderCompany.getBenfName());
		this.accountNo.setValue(aBuilderCompany.getAccountNo());
		this.bankName.setValue(aBuilderCompany.getBankName());
		this.bankBranch.setValue(String.valueOf(aBuilderCompany.getBankBranchId()));
		this.ifsc.setValue(aBuilderCompany.getIfsc());
		this.dateOfInCop.setValue(aBuilderCompany.getDateOfInCop());
		this.noOfProj.setValue(aBuilderCompany.getNoOfProj());
		this.assHLPlayers.setValue(aBuilderCompany.getAssHLPlayers());
		this.onGoingProj.setValue(aBuilderCompany.getOnGoingProj());
		this.expInBusiness.setValue(aBuilderCompany.getExpInBusiness());
		this.magintudeInLacs.setValue(aBuilderCompany.getMagintudeInLacs());
		this.noOfProjCons.setValue(aBuilderCompany.getNoOfProjCons());
		this.limitOnAmt.setValue(PennantApplicationUtil.formateAmount(aBuilderCompany.getLimitOnAmt(), getCcyFormat()));
		this.currentExpAmt
				.setValue(PennantApplicationUtil.formateAmount(aBuilderCompany.getCurrentExpAmt(), getCcyFormat()));
		this.limitOnUnits.setValue(aBuilderCompany.getLimitOnUnits());
		this.currentExpUni.setValue(aBuilderCompany.getCurrentExpUni());
		fillComboBox(this.approved, aBuilderCompany.getApproved(), this.approvedList, "");
		fillComboBox(this.cityType, aBuilderCompany.getCityType(), this.locationList, "");
		fillComboBox(this.recommendation, aBuilderCompany.getRecommendation(), this.recommendationList, "");
		fillComboBox(this.entityType, aBuilderCompany.getEntityType(), this.builderEntityTypeList, "");
		this.active.setChecked(aBuilderCompany.isActive());
		if (aBuilderCompany.isNewRecord()
				|| StringUtils.equals(aBuilderCompany.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(aBuilderCompany.getRecordStatus());
		doEnableRequiredFields(aBuilderCompany.getApfType());

		if (!aBuilderCompany.isNewRecord()) {
			Filter[] filterPin = new Filter[1];

			if (aBuilderCompany.getCity() != null && !aBuilderCompany.getCity().isEmpty()) {
				filterPin[0] = new Filter("City", aBuilderCompany.getCity(), Filter.OP_EQUAL);
				this.code.setFilters(filterPin);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderCompany
	 */
	public void doWriteComponentsToBean(BuilderCompany aBuilderCompany) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Name
		try {
			aBuilderCompany.setName(this.name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Segmentation
		try {
			this.segmentation.getValidatedValue();
			aBuilderCompany.setSegmentation(this.segmentation.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// CustId
		try {
			Customer customerDetails = fetchCustomerData();
			if (customerDetails != null && StringUtils.isNotEmpty(custCIF.getValue())) {
				// throw new WrongValueException(custCIF, Labels.getLabel("Cust_NotFound"));
				aBuilderCompany.setCustId(customerDetails.getCustID());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Builder Group
		try {
			this.groupId.getValidatedValue();
			aBuilderCompany.setGroupId(Long.valueOf(groupId.getValue()));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setApfType(this.apfType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setCityType(this.cityType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setApproved(this.approved.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setCity(this.city.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setState(this.state.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setEntityType(this.entityType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			Object obj = this.code.getAttribute("pinCodeId");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aBuilderCompany.setPinCodeId(Long.valueOf((obj.toString())));
				}
			} else {
				aBuilderCompany.setPinCodeId(null);
			}
			aBuilderCompany.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setBankName(this.bankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!StringUtils.isEmpty(this.bankBranch.getValue())) {
				aBuilderCompany.setBankBranchId(Long.valueOf(this.bankBranch.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderCompany.setPeDevId(this.peDevId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setEmailId(this.emailId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setAddress1(this.address1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setAddress2(this.address2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setAddress3(this.address3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.absavailablity.getValue() != null) {
				aBuilderCompany.setAbsavailablity(this.absavailablity.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setDevavailablity(this.devavailablity.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setMagnitude(this.magnitude.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		 * try { aBuilderCompany.setTotalProj( Integer.valueOf(this.totalProj.getValue().equals("") ? "0" :
		 * this.totalProj.getValue())); } catch (WrongValueException we) { wve.add(we); }
		 */
		try {
			aBuilderCompany.setTotalProj(this.totalProj.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderCompany.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setPanDetails(this.panDetails.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setBenfName(this.benfName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setAccountNo(this.accountNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			doValidate(this.noOfProj, "label_BuilderCompanyDialog_NoOfProj.value");
			aBuilderCompany.setNoOfProj(this.noOfProj.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setCurrentExpAmt(
					PennantApplicationUtil.unFormateAmount(this.currentExpAmt.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setLimitOnAmt(
					PennantApplicationUtil.unFormateAmount(this.limitOnAmt.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.currentExpUni.getValue() != null) {
				aBuilderCompany.setCurrentExpUni(this.currentExpUni.intValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.limitOnUnits.getValue() != null) {
				aBuilderCompany.setLimitOnUnits(this.limitOnUnits.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setDateOfInCop(this.dateOfInCop.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setNoOfProjCons(this.noOfProjCons.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			doValidate(this.assHLPlayers, "label_BuilderCompanyDialog_AssHLPlayers.value");
			aBuilderCompany.setAssHLPlayers(this.assHLPlayers.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			doValidate(this.onGoingProj, "label_BuilderCompanyDialog_OnGoingProj.value");
			aBuilderCompany.setOnGoingProj(this.onGoingProj.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			doValidate(this.expInBusiness, "label_BuilderCompanyDialog_ExpInBusiness.value");
			aBuilderCompany.setExpInBusiness(this.expInBusiness.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			doValidate(this.magintudeInLacs, "label_BuilderCompanyDialog_MagintudeInLacs.value");
			aBuilderCompany.setMagintudeInLacs(this.magintudeInLacs.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setRecommendation(this.recommendation.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderCompany.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doValidate(Intbox component, String lable) {
		Integer value = component.getValue();
		if (value == null && PennantConstants.BRANCH_APF.equals(getComboboxValue(this.apfType))) {
			throw new WrongValueException(component,
					Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel(lable) }));
		} else if (value != null && value < 0) {
			throw new WrongValueException(component,
					Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] { Labels.getLabel(lable) }));
		}
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param builderCompany The entity that need to be render.
	 */
	public void doShowDialog(BuilderCompany builderCompany) {
		logger.debug(Literal.LEAVING);

		if (builderCompany.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.name.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(builderCompany.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.segmentation.focus();
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
		doWriteBeanToComponents(builderCompany);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.name.isReadonly()) {
			this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_name.value"),
					PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
		}
		if (!StringUtils.equals(this.apfType.getSelectedItem().getValue(), PennantConstants.BRANCH_APF)) {
			if (!this.segmentation.isReadonly()) {
				this.segmentation.setConstraint(new PTStringValidator(
						Labels.getLabel("label_BuilderCompanyDialog_segmentation.value"), null, true, true));
			}
		}
		if (!this.groupId.isReadonly()) {
			this.groupId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderCompanyDialog_groupId.value"), null, true, true));
		}
		if (!this.apfType.isDisabled()) {
			this.apfType.setConstraint(
					new PTListValidator(Labels.getLabel("label_BuilderCompany_BranchAPF.value"), aPFTypeList, false));
		}
		if (!this.cityType.isDisabled()) {
			this.cityType.setConstraint(new PTListValidator(
					Labels.getLabel("label_BuilderCompanyDialog_CityType.value"), locationList, false));
		}
		if (!this.approved.isDisabled()) {
			this.approved.setConstraint(new PTListValidator(
					Labels.getLabel("label_BuilderCompanyDialog_Approved.value"), approvedList, false));
		}
		if (!this.city.isReadonly()) {
			this.city.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_City.value"), null, true, true));
		}
		if (!this.state.isReadonly()) {
			this.state.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_State.value"), null, true, true));
		}

		if (!this.entityType.isDisabled()) {
			this.entityType.setConstraint(new PTListValidator(
					Labels.getLabel("label_BuilderCompanyDialog_entityType.value"), builderEntityTypeList, false));
		}
		if (!this.code.isReadonly()) {
			this.code.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_Code.value"), null, true, true));
		}
		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderCompanyDialog_BankName.value"), null, false, false));
		}
		if (!this.bankBranch.isReadonly()) {
			this.bankBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderCompanyDialog_BankBranch.value"), null, false, false));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false, false));
		}
		if (!this.benfName.isReadonly()) {
			this.benfName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_BenfName.value"),
							PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, false));
		}

		if (!this.emailId.isReadonly()) {
			this.emailId.setConstraint(
					new PTEmailValidator(Labels.getLabel("label_BuilderCompanyDialog_emailId.value"), false));
		}
		if (!this.panDetails.isReadonly()) {
			this.panDetails
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_PANDetails.value"),
							PennantRegularExpressions.REGEX_PANNUMBER, false));
		}
		if (!this.accountNo.isReadonly()) {
			this.accountNo
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_AccountNo.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, false));
		}
		if (!this.magnitude.isReadonly()) {
			this.magnitude.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_BuilderCompanyDialog_Magnitude.value"), 9, false, false, 9999));
		}
		if (!this.absavailablity.isReadonly()) {
			this.absavailablity.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_BuilderCompanyDialog_AbsAvailablity.value"), 9, false, false, 9999));
		}
		if (!this.limitOnUnits.isReadonly()) {
			this.limitOnUnits.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_BuilderCompanyDialog_LimitOnNoOfUnits.value"), 2, false, false, 999999999));
		}
		if (!this.totalProj.isReadonly()) {
			this.totalProj
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_TotalProj.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.address1.isReadonly()) {
			this.address1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_address1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false, false));
		}
		if (!this.address2.isReadonly()) {
			this.address2
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_address2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false, false));
		}
		if (!this.address3.isReadonly()) {
			this.address3
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_address3.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false, false));
		}
		if (!this.peDevId.isReadonly()) {
			this.peDevId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_PEdevId.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_CHAR, false, false));
		}
		if (!this.devavailablity.isReadonly()) {
			this.devavailablity.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_BuilderCompanyDialog_DevAvailablity.value"), false, false, 0, 999999));
		}
		if (!this.currentExpUni.isReadonly()) {
			this.currentExpUni.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_BuilderCompanyDialog_CurrentExpUni.value"), false, false, 0, 999999999));
		}
		if (!this.limitOnAmt.isReadonly()) {
			this.limitOnAmt.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_BuilderCompanyDialog_LimitOnAmt.value"),
							PennantConstants.defaultCCYDecPos, false, false));
		}
		if (!this.currentExpAmt.isReadonly()) {
			this.currentExpAmt.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_BuilderCompanyDialog_CurrentExpAmt.value"),
							PennantConstants.defaultCCYDecPos, false, false));
		}
		if (!this.noOfProjCons.isReadonly()) {
			this.noOfProjCons.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_BuilderCompanyDialog_NoOfProjCons.value"), false, false, 0, 9999));
		}
		if (this.apfType.getSelectedItem() != null
				&& this.apfType.getSelectedItem().getValue().equals(PennantConstants.BRANCH_APF)) {
			this.recommendation.setConstraint(new PTListValidator(
					Labels.getLabel("label_BuilderCompanyDialog_Recommendation.value"), recommendationList, true));

			String cityTypeValue = this.cityType.getSelectedItem().getValue();
			if (StringUtils.equals(cityTypeValue, PennantConstants.List_Select)) {
				this.cityType.setConstraint(new StaticListValidator(PennantStaticListUtil.getcityType(),
						Labels.getLabel("label_BuilderCompanyDialog_CityType.value")));
			}

		} else {
			this.recommendation.setConstraint("");
			this.cityType.setConstraint("");
			String cityTypeValue = this.cityType.getSelectedItem().getValue();
			if (StringUtils.equals(cityTypeValue, PennantConstants.List_Select)) {
				this.cityType.setConstraint("");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.name.setConstraint("");
		this.segmentation.setConstraint("");
		this.groupId.setConstraint("");
		this.custId.setConstraint("");
		this.custCIF.setConstraint("");
		this.apfType.setConstraint("");
		this.cityType.setConstraint("");
		this.city.setConstraint("");
		this.state.setConstraint("");
		this.entityType.setConstraint("");
		this.code.setConstraint("");
		this.bankName.setConstraint("");
		this.bankBranch.setConstraint("");
		this.approved.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// id
		// Name
		// Ssegmentation
		// Builder Group

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		this.name.setErrorMessage("");
		this.segmentation.setErrorMessage("");
		this.groupId.setErrorMessage("");
		this.apfType.setErrorMessage("");
		this.cityType.setErrorMessage("");
		this.city.setErrorMessage("");
		this.state.setErrorMessage("");
		this.entityType.setErrorMessage("");
		this.code.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.bankBranch.setErrorMessage("");
		this.assHLPlayers.setErrorMessage("");
		this.noOfProj.setErrorMessage("");
		this.magintudeInLacs.setErrorMessage("");
		this.noOfProjCons.setErrorMessage("");
		this.onGoingProj.setErrorMessage("");
		this.recommendation.setErrorMessage("");
		this.expInBusiness.setErrorMessage("");
		this.onGoingProj.setErrorMessage("");
		this.cityType.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustId(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerID();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerID() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custIdSearchObject);
		List<Filter> filtersList = new ArrayList<Filter>();
		Filter filter = new Filter("CUSTCTGCODE", PennantConstants.PFF_CUSTCTG_CORP, Filter.OP_EQUAL);
		filtersList.add(filter);
		map.put("filtersList", filtersList);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	protected Map<String, Object> getDefaultArguments() {
		Map<String, Object> aruments = new HashMap<String, Object>();
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);
		return aruments;
	}

	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custId.clearErrorMessage();
		this.custIdSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custId.setValue(customer.getCustID());
			this.custCIF.setValue(customer.getCustCIF());
			this.custDesc.setValue(customer.getCustShrtName());
			// this.custCif.setDisabled(true);
		} else {
			// this.custId.setValue();
			this.custDesc.setValue("");
		}
		logger.debug("Leaving ");
	}

	public void onChange$custCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Customer CustomerData = fetchCustomerData();
		if (CustomerData != null) {
			this.custDesc.setValue(CustomerData.getCustShrtName());
			this.custId.setValue(CustomerData.getCustID());
		} else {
			this.custDesc.setValue("");
			this.custId.setValue(null);
			// this.custCIF.setValue(null);
		}
		// doSearchCustomerID();
		logger.debug("Leaving ");
	}

	public Customer fetchCustomerData() {
		logger.debug("Entering");

		Customer customer = null;
		// Get the data of Customer from Core Banking Customer
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		String cif = StringUtils.trimToEmpty(this.custCIF.getValue());
		// If customer exist is checked
		if (StringUtils.isNotEmpty(cif)) {
			// check Customer Data in LOCAL PFF system
			customer = customerDetailsService.getCheckCustomerByCIF(cif);
		}

		logger.debug("Leaving");
		return customer;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BuilderCompany aBuilderCompany = new BuilderCompany();
		BeanUtils.copyProperties(this.builderCompany, aBuilderCompany);

		doDelete(aBuilderCompany.getName(), aBuilderCompany);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.builderCompany.isNewRecord()) {
			readOnlyComponent(false, this.name);
			readOnlyComponent(false, this.groupId);
			readOnlyComponent(false, this.custCIF);
			this.btnCancel.setVisible(false);
			/*
			 * readOnlyComponent(true, this.bankName); readOnlyComponent(true, this.ifsc);
			 */
		} else {
			readOnlyComponent(true, this.name);
			this.btnCancel.setVisible(true);

		}
		// display fields
		readOnlyComponent(true, this.ifsc);
		readOnlyComponent(true, this.bankName);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_groupId"), this.groupId);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_segmentation"), this.segmentation);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_custCIF"), this.custCIF);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_custCIF"), this.btnSearchCustId);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_APFType"), this.apfType);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_PEDevId"), this.peDevId);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_EntityType"), this.entityType);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_EmailId"), this.emailId);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_CityType"), this.cityType);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Address1"), this.address1);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Address2"), this.address2);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Address3"), this.address3);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_City"), this.city);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_State"), this.state);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Code"), this.code);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_DevAvailablity"), this.devavailablity);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Magnitude"), this.magnitude);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_AbsAvailablity"), this.absavailablity);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_TotalProj"), this.totalProj);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Approved"), this.approved);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Remarks"), this.remarks);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_PanDetails"), this.panDetails);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_BenfName"), this.benfName);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_AccountNo"), this.accountNo);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_BankBranch"), this.bankBranch);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_LimitOnAmt"), this.limitOnAmt);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_limitOnUnits"), this.limitOnUnits);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_currentExpUni"), this.currentExpUni);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_currentExpAmt"), this.currentExpAmt);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_DateOfInCop"), this.dateOfInCop);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_NoOfProj"), this.noOfProj);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_AssHLPlayers"), this.assHLPlayers);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_OnGoingProj"), this.onGoingProj);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_ExpInBusiness"), this.expInBusiness);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Recommendation"), this.recommendation);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_MagintudeInLacs"), this.magintudeInLacs);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_NoOfProjCons"), this.noOfProjCons);
		readOnlyComponent(isReadOnly("BuilderCompanyDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.builderCompany.isNewRecord()) {
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

		readOnlyComponent(true, this.name);
		readOnlyComponent(true, this.segmentation);
		readOnlyComponent(true, this.groupId);
		readOnlyComponent(true, this.custId);
		readOnlyComponent(true, this.city);
		readOnlyComponent(true, this.state);
		readOnlyComponent(true, this.entityType);
		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.bankName);
		readOnlyComponent(true, this.bankBranch);
		readOnlyComponent(true, this.active);
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
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.name.setValue("");
		this.segmentation.setValue("");
		this.segmentation.setDescription("");
		this.groupId.setValue("");
		this.groupId.setDescription("");
		this.apfType.setSelectedIndex(0);
		this.cityType.setSelectedIndex(0);
		this.city.setDescription("");
		this.city.setValue("");
		this.state.setValue("");
		this.state.setDescription("");
		this.code.setValue("");
		this.code.setDescription("");
		this.bankName.setValue("");
		this.bankBranch.setDescription("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final BuilderCompany aBuilderCompany = new BuilderCompany();
		BeanUtils.copyProperties(this.builderCompany, aBuilderCompany);
		boolean isNew = false;

		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean(aBuilderCompany);

		isNew = aBuilderCompany.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBuilderCompany.getRecordType())) {
				aBuilderCompany.setVersion(aBuilderCompany.getVersion() + 1);
				if (isNew) {
					aBuilderCompany.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBuilderCompany.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBuilderCompany.setNewRecord(true);
				}
			}
		} else {
			aBuilderCompany.setVersion(aBuilderCompany.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aBuilderCompany, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	protected boolean doProcess(BuilderCompany aBuilderCompany, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBuilderCompany.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBuilderCompany.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBuilderCompany.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBuilderCompany.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBuilderCompany.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBuilderCompany);
				}

				if (isNotesMandatory(taskId, aBuilderCompany)) {
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

			aBuilderCompany.setTaskId(taskId);
			aBuilderCompany.setNextTaskId(nextTaskId);
			aBuilderCompany.setRoleCode(getRole());
			aBuilderCompany.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBuilderCompany, tranType);
			String operationRefs = getServiceOperations(taskId, aBuilderCompany);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBuilderCompany, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBuilderCompany, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BuilderCompany aBuilderCompany = (BuilderCompany) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = builderCompanyService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = builderCompanyService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = builderCompanyService.doApprove(auditHeader);

					if (aBuilderCompany.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = builderCompanyService.doReject(auditHeader);
					if (aBuilderCompany.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BuilderCompanyDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_BuilderCompanyDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.builderCompany), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	public int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(BuilderCompany aBuilderCompany, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBuilderCompany.getBefImage(), aBuilderCompany);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aBuilderCompany.getUserDetails(),
				getOverideMap());
	}

	public void onFulfill$state(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = state.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			fillPindetails(null, null);
		} else {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.state.setErrorMessage("");
				pcProvince = this.state.getValue();
				fillPindetails(null, pcProvince);
			}
		}

		this.city.setObject("");
		this.code.setObject("");
		this.city.setValue("");
		this.city.setDescription("");
		this.code.setValue("");
		this.code.setDescription("");
		fillCitydetails(pcProvince);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * based on state param ,city will be filtered
	 * 
	 * @param state
	 */
	private void fillCitydetails(String state) {
		logger.debug(Literal.ENTERING);

		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters = new Filter[1];
		if (state != null && !state.isEmpty()) {
			filters[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		}
		this.city.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * onFulfill custAddrCity
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$city(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Object dataObject = city.getObject();

		String cityValue = null;
		if (dataObject instanceof String) {
			this.city.setValue("");
			this.city.setDescription("");
			fillPindetails(null, null);
		} else {
			City city = (City) dataObject;
			if (city != null) {
				this.city.setErrorMessage("");
				this.state.setErrorMessage("");

				this.state.setValue(city.getPCProvince());
				this.state.setDescription(city.getLovDescPCProvinceName());
				cityValue = this.city.getValue();
			} else {
				fillCitydetails(state.getValue());
			}
		}

		fillPindetails(cityValue, this.state.getValue());

		this.code.setObject("");
		this.code.setValue("");
		this.code.setDescription("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * based on param values,custaddrzip is filtered
	 * 
	 * @param cityValue
	 * @param provice
	 */

	private void fillPindetails(String cityValue, String provice) {
		logger.debug(Literal.ENTERING);

		this.code.setModuleName("PinCode");
		this.code.setValueColumn("PinCode");
		this.code.setDescColumn("AreaName");
		this.code.setValidateColumns(new String[] { "PinCode" });
		Filter[] filters = new Filter[1];

		if (cityValue != null && !cityValue.isEmpty()) {
			filters[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if (provice != null && !provice.isEmpty()) {
			filters[0] = new Filter("PCProvince", provice, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		}
		this.code.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * onFulfill custAddrZip.based on custAddrZip,custAddrCity and custAddrprovince will auto populate
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$code(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Object dataObject = code.getObject();
		if (dataObject instanceof String) {
			this.code.setValue("");
			this.code.setDescription("");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.city.setValue(pinCode.getCity());
				this.city.setDescription(pinCode.getPCCityName());
				this.state.setValue(pinCode.getPCProvince());
				this.state.setDescription(pinCode.getLovDescPCProvinceName());

				this.city.setErrorMessage("");
				this.state.setErrorMessage("");
				this.code.setErrorMessage("");
			} else {
				fillPindetails(city.getValue(), state.getValue());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void setBuilderCompanyService(BuilderCompanyService builderCompanyService) {
		this.builderCompanyService = builderCompanyService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}