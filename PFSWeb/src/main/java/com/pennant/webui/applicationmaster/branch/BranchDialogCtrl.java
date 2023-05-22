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
 * * FileName : BranchDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * 09-05-2018 Vinay 0.2 Branch Code field working on rights * functionality changes. * * * *
 * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.branch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Branch/branchDialog.zul file.
 */
public class BranchDialogCtrl extends GFCBaseCtrl<Branch> {
	private static final long serialVersionUID = -4832204841676720745L;
	private static final Logger logger = LogManager.getLogger(BranchDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BranchDialog;

	protected Textbox branchCode;
	protected Textbox branchDesc;
	protected Textbox branchAddrLine1;
	protected Textbox branchAddrLine2;
	protected Textbox branchPOBox;
	protected ExtendedCombobox branchCity;
	protected ExtendedCombobox branchProvince;
	protected ExtendedCombobox branchCountry;
	protected Textbox branchFax;
	protected Textbox faxCountryCode;
	protected Textbox faxAreaCode;
	protected Textbox branchTel;
	protected Textbox phoneCountryCode;
	protected Textbox phoneAreaCode;
	protected Textbox branchSwiftBankCode;
	protected ExtendedCombobox branchSwiftCountry;
	protected Textbox branchSwiftLocCode;
	protected Textbox branchSwiftBrnCde;
	protected Textbox branchSortCode;
	protected Checkbox branchIsActive;
	protected Textbox cityName;
	protected Row row_NewBranch;
	protected ExtendedCombobox newBranchCode;
	protected Checkbox miniBranch;
	protected Combobox branchType;
	protected ExtendedCombobox parentBranch;
	protected Combobox region;
	protected Textbox bankRefNo;
	protected Textbox branchAddrHNbr;
	protected Textbox branchFlatNbr;
	protected Textbox branchAddrStreet;
	protected ExtendedCombobox pinCode;
	protected ExtendedCombobox entity;
	protected ExtendedCombobox cluster;
	protected ExtendedCombobox defChequeDDPrintLoc;
	protected Row row_org_struct;

	// not autoWired Var's
	private Branch branch;
	private transient BranchListCtrl branchListCtrl;

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient BranchService branchService;
	private transient ClusterService clusterService;
	private transient String sBranchCountry;
	private transient String sBranchProvince;
	private transient String sBranchCity;

	private final List<ValueLabel> branchTypeList = PennantStaticListUtil.getBranchTypeList();
	private final List<ValueLabel> regionList = PennantStaticListUtil.getRegionList();

	/**
	 * default constructor.<br>
	 */
	public BranchDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BranchDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Branch object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_BranchDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BranchDialog);

		try {
			if (PennantConstants.CITY_FREETEXT) {
				this.branchCity.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.branchCity.setVisible(true);
				this.cityName.setVisible(false);
			}

			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_ORGANISATIONAL_STRUCTURE)) {
				this.row_org_struct.setVisible(true);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("branch")) {
				this.branch = (Branch) arguments.get("branch");
				Branch befImage = new Branch();
				BeanUtils.copyProperties(this.branch, befImage);
				this.branch.setBefImage(befImage);

				setBranch(this.branch);
			} else {
				setBranch(null);
			}

			doLoadWorkFlow(this.branch.isWorkflow(), this.branch.getWorkflowId(), this.branch.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "BranchDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the branchListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete branch here.
			if (arguments.containsKey("branchListCtrl")) {
				setBranchListCtrl((BranchListCtrl) arguments.get("branchListCtrl"));
			} else {
				setBranchListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBranch());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BranchDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.branchCode.setMaxlength(LengthConstants.LEN_BRANCH);
		this.branchDesc.setMaxlength(50);
		this.branchAddrLine1.setMaxlength(50);
		this.branchAddrLine2.setMaxlength(50);
		this.branchPOBox.setMaxlength(8);
		this.branchCity.setMaxlength(50);
		this.cityName.setMaxlength(50);
		this.branchProvince.setMaxlength(8);
		this.branchCountry.setMaxlength(2);
		this.faxCountryCode.setMaxlength(4);
		this.faxAreaCode.setMaxlength(4);
		this.branchFax.setMaxlength(8);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneAreaCode.setMaxlength(3);
		this.branchTel.setMaxlength(8);
		this.branchSwiftBankCode.setMaxlength(4);
		this.branchSwiftLocCode.setMaxlength(2);
		this.branchSwiftBrnCde.setMaxlength(5);
		this.branchSortCode.setMaxlength(6);
		this.bankRefNo.setMaxlength(20);
		this.branchAddrHNbr.setMaxlength(50);
		this.branchFlatNbr.setMaxlength(50);
		this.branchAddrStreet.setMaxlength(50);

		this.branchCountry.setMandatoryStyle(true);
		this.branchCountry.setModuleName("Country");
		this.branchCountry.setValueColumn("CountryCode");
		this.branchCountry.setDescColumn("CountryDesc");
		this.branchCountry.setValidateColumns(new String[] { "CountryCode" });

		this.branchProvince.setMaxlength(8);
		this.branchProvince.setMandatoryStyle(true);
		this.branchProvince.setModuleName("Province");
		this.branchProvince.setValueColumn("CPProvince");
		this.branchProvince.setDescColumn("CPProvinceName");
		this.branchProvince.setValidateColumns(new String[] { "CPProvince" });

		this.branchCity.setMaxlength(8);
		this.branchCity.setMandatoryStyle(true);
		this.branchCity.setModuleName("City");
		this.branchCity.setValueColumn("PCCity");
		this.branchCity.setDescColumn("PCCityName");
		this.branchCity.setValidateColumns(new String[] { "PCCity" });

		this.branchSwiftCountry.setMaxlength(2);
		this.branchSwiftCountry.setMandatoryStyle(false);
		this.branchSwiftCountry.setModuleName("Country");
		this.branchSwiftCountry.setValueColumn("CountryCode");
		this.branchSwiftCountry.setDescColumn("CountryDesc");
		this.branchSwiftCountry.setValidateColumns(new String[] { "CountryCode" });

		this.newBranchCode.setMaxlength(12);
		this.newBranchCode.setMandatoryStyle(true);
		this.newBranchCode.setModuleName("Branch");
		this.newBranchCode.setValueColumn("BranchCode");
		this.newBranchCode.setDescColumn("BranchDesc");
		this.newBranchCode.setValidateColumns(new String[] { "BranchCode" });

		this.parentBranch.setMaxlength(12);
		this.parentBranch.setModuleName("Branch");
		this.parentBranch.setValueColumn("BranchCode");
		this.parentBranch.setDescColumn("BranchDesc");
		this.parentBranch.setValidateColumns(new String[] { "BranchCode", "BranchDesc" });

		this.pinCode.setMaxlength(10);
		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValueType(DataType.LONG);
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });
		this.pinCode.setInputAllowed(false);

		this.entity.setMaxlength(10);
		this.entity.setMandatoryStyle(true);
		this.entity.setModuleName("Entity");
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.addForward(ExtendedCombobox.ON_FUL_FILL, self, "onChangeEntity", null);
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		this.defChequeDDPrintLoc.setMaxlength(12);
		this.defChequeDDPrintLoc.setMandatoryStyle(true);
		this.defChequeDDPrintLoc.setModuleName("BankBranch");
		this.defChequeDDPrintLoc.setValueColumn("BranchCode");
		this.defChequeDDPrintLoc.setDescColumn("BranchDesc");
		this.defChequeDDPrintLoc.setValidateColumns(new String[] { "BranchCode" });

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_BranchDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.branch.getBefImage());
		doReadOnly();
		doSetNewBranchProp();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBranch Branch
	 */
	public void doWriteBeanToComponents(Branch aBranch) {
		logger.debug("Entering");
		this.branchCode.setValue(aBranch.getBranchCode());
		this.branchDesc.setValue(aBranch.getBranchDesc());
		this.branchAddrLine1.setValue(aBranch.getBranchAddrLine1());
		this.branchAddrLine2.setValue(aBranch.getBranchAddrLine2());
		this.branchPOBox.setValue(aBranch.getBranchPOBox());
		this.branchCity.setValue(aBranch.getBranchCity());
		this.cityName.setValue(aBranch.getBranchCity());
		this.branchProvince.setValue(aBranch.getBranchProvince());
		this.branchCountry.setValue(aBranch.getBranchCountry());
		this.bankRefNo.setValue(aBranch.getBankRefNo());
		this.branchAddrHNbr.setValue(aBranch.getBranchAddrHNbr());
		this.branchFlatNbr.setValue(aBranch.getBranchFlatNbr());
		this.branchAddrStreet.setValue(aBranch.getBranchAddrStreet());
		String[] fax = PennantApplicationUtil.unFormatPhoneNumber(aBranch.getBranchFax());
		this.faxCountryCode.setValue(fax[0]);
		this.faxAreaCode.setValue(fax[1]);
		this.branchFax.setValue(fax[2]);
		String[] telephone = PennantApplicationUtil.unFormatPhoneNumber(aBranch.getBranchTel());
		this.phoneCountryCode.setValue(telephone[0]);
		this.phoneAreaCode.setValue(telephone[1]);
		this.branchTel.setValue(telephone[2]);
		this.branchSwiftBankCode.setValue(aBranch.getBranchSwiftBankCde());
		this.branchSwiftCountry.setValue(aBranch.getBranchSwiftCountry());
		this.branchSwiftLocCode.setValue(aBranch.getBranchSwiftLocCode());
		this.branchSwiftBrnCde.setValue(aBranch.getBranchSwiftBrnCde());
		this.branchSortCode.setValue(aBranch.getBranchSortCode());
		this.branchIsActive.setChecked(aBranch.isBranchIsActive());
		this.newBranchCode.setValue(aBranch.getNewBranchCode());
		this.miniBranch.setChecked(aBranch.isMiniBranch());
		this.defChequeDDPrintLoc.setValue(aBranch.getDefChequeDDPrintLoc());
		fillComboBox(this.branchType, aBranch.getBranchType(), branchTypeList, "");
		fillComboBox(this.region, aBranch.getRegion(), regionList, "");

		if (aBranch.getPinCodeId() != null) {
			this.pinCode.setAttribute("pinCodeId", aBranch.getPinCodeId());
		} else {
			this.pinCode.setAttribute("pinCodeId", null);
		}

		this.pinCode.setValue(aBranch.getPinCode(), aBranch.getPinAreaDesc());
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setAttribute("branchCode", aBranch.getBranchCode());
			this.parentBranch.setValue(aBranch.getBranchCode(), aBranch.getBranchDesc());
		}

		if (StringUtils.isNotEmpty(aBranch.getEntity())) {
			Entity entity = new Entity();
			entity.setEntityCode(aBranch.getEntity());
			entity.setEntityDesc(aBranch.getEntityDesc());
			this.entity.setObject(entity);
			this.entity.setValue(aBranch.getEntity());
			onChangeEntity();
		}

		this.cluster.setValue(aBranch.getClusterCode());
		this.cluster.setDescription(aBranch.getClusterName());

		if (aBranch.getClusterId() != null && aBranch.getClusterId() > 0) {
			Cluster acluster = new Cluster();
			acluster.setId(aBranch.getClusterId());
			this.cluster.setObject(acluster);
		}

		if (aBranch.isNewRecord()) {
			this.branchCity.setDescription("");
			this.branchProvince.setDescription("");
			this.branchCountry.setDescription("");
			this.branchSwiftCountry.setDescription("");
			this.newBranchCode.setDescription("");
			this.parentBranch.setDescription("");
			this.pinCode.setDescription("");
			this.entity.setDescription("");
			this.cluster.setDescription("");
		} else {
			this.branchCity.setDescription(aBranch.getLovDescBranchCityName());
			this.branchProvince.setDescription(aBranch.getLovDescBranchProvinceName());
			this.branchCountry.setDescription(aBranch.getLovDescBranchCountryName());
			this.branchSwiftCountry.setDescription(aBranch.getLovDescBranchSwiftCountryName());
			this.newBranchCode.setDescription(aBranch.getNewBranchDesc());
			this.pinCode.setDescription(aBranch.getPinAreaDesc());
			this.entity.setDescription(aBranch.getEntityDesc());
			this.cluster.setDescription(aBranch.getClusterName());
		}
		this.recordStatus.setValue(aBranch.getRecordStatus());
		if (aBranch.isNewRecord() || (aBranch.getRecordType() != null ? aBranch.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.branchIsActive.setChecked(true);
			this.branchIsActive.setDisabled(true);
		}
		sBranchCountry = this.branchCountry.getValue();
		sBranchProvince = this.branchProvince.getValue();
		sBranchCity = this.branchCity.getValue();

		if (!aBranch.isNewRecord()) {
			Filter[] filterProvince = new Filter[1];
			filterProvince[0] = new Filter("CPCountry", sBranchCountry, Filter.OP_EQUAL);
			this.branchProvince.setFilters(filterProvince);
			Filter[] filterCity = new Filter[1];
			filterCity[0] = new Filter("PCProvince", sBranchProvince, Filter.OP_EQUAL);
			this.branchCity.setFilters(filterCity);

			ArrayList<Filter> filters = new ArrayList<Filter>();

			if (sBranchCountry != null && !sBranchCountry.isEmpty()) {
				Filter filterPin0 = new Filter("PCCountry", sBranchCountry, Filter.OP_EQUAL);
				filters.add(filterPin0);
			}

			if (sBranchProvince != null && !sBranchProvince.isEmpty()) {
				Filter filterPin1 = new Filter("PCProvince", sBranchProvince, Filter.OP_EQUAL);
				filters.add(filterPin1);
			}

			if (sBranchCity != null && !sBranchCity.isEmpty()) {
				Filter filterPin2 = new Filter("City", sBranchCity, Filter.OP_EQUAL);
				filters.add(filterPin2);
			}

			Filter[] filterPin = new Filter[filters.size()];
			for (int i = 0; i < filters.size(); i++) {
				filterPin[i] = filters.get(i);
			}
			this.pinCode.setFilters(filterPin);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBranch
	 */
	public void doWriteComponentsToBean(Branch aBranch) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBranch.setBranchCode(this.branchCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchDesc(this.branchDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchAddrLine1(this.branchAddrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchAddrLine2(this.branchAddrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchPOBox(this.branchPOBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (PennantConstants.CITY_FREETEXT) {
				aBranch.setBranchCity(StringUtils.trimToNull(this.cityName.getValue()));
			} else {
				aBranch.setLovDescBranchCityName(StringUtils.trimToNull(this.branchCity.getDescription()));
				aBranch.setBranchCity(StringUtils.trimToNull(this.branchCity.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setLovDescBranchCityName(this.branchCity.getDescription());
			aBranch.setBranchCity(this.branchCity.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setLovDescBranchProvinceName(this.branchProvince.getDescription());
			aBranch.setBranchProvince(this.branchProvince.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setLovDescBranchCountryName(this.branchCountry.getDescription());
			aBranch.setBranchCountry(this.branchCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchFax(PennantApplicationUtil.formatPhoneNumber(this.faxCountryCode.getValue(),
					this.faxAreaCode.getValue(), this.branchFax.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchTel(PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(),
					this.phoneAreaCode.getValue(), this.branchTel.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftBankCde(this.branchSwiftBankCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftCountry(this.branchSwiftCountry.getValidatedValue().toUpperCase());
			aBranch.setLovDescBranchSwiftCountryName(this.branchSwiftCountry.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftLocCode(this.branchSwiftLocCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftBrnCde(this.branchSwiftBrnCde.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSortCode(this.branchSortCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchIsActive(this.branchIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setNewBranchCode(this.newBranchCode.getValidatedValue());
			aBranch.setNewBranchDesc(this.newBranchCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setMiniBranch(this.miniBranch.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchType(this.branchType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setRegion(this.region.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.parentBranch.getValidatedValue();
			String parentBranch = String.valueOf(this.parentBranch.getAttribute("branchCode"));
			aBranch.setParentBranch(parentBranch);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.branchIsActive.isChecked()) {
			aBranch.setNewBranchCode("");
			aBranch.setNewBranchDesc("");
		}

		try {
			aBranch.setBankRefNo(this.bankRefNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchAddrHNbr(this.branchAddrHNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchFlatNbr(this.branchFlatNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchAddrStreet(this.branchAddrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String defChequeDDPrintLoc = String.valueOf(this.defChequeDDPrintLoc.getValidatedValue());
			aBranch.setDefChequeDDPrintLoc(defChequeDDPrintLoc);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Object obj = this.pinCode.getAttribute("pinCodeId");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aBranch.setPinCodeId(Long.valueOf((obj.toString())));
				}
			}
			aBranch.setPinCode(this.pinCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (aBranch.getClusterId() != null) {
			if (aBranch.getClusterId() == 0) {
				aBranch.setClusterId(null);
			}
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
			try {
				aBranch.setEntity(this.entity.getValidatedValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				this.cluster.getValidatedValue();

				Object aObject = (Object) this.cluster.getObject();
				if (aObject != null && aObject instanceof Cluster) {
					Cluster acluster = (Cluster) aObject;
					aBranch.setClusterId(acluster.getId());
				} else {
					aBranch.setClusterId(null);
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aBranch.setClusterId(null);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aBranch.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aBranch
	 */
	public void doShowDialog(Branch aBranch) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aBranch.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.branchCode.focus();
			this.branchCode.setReadonly(false);
		} else {
			this.branchCode.setReadonly(true);
			this.branchDesc.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aBranch.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBranch);
			doSetNewBranchProp();

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BranchDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doSetNewBranchProp() {
		logger.debug("Entering");
		if (!this.branchIsActive.isChecked() && !getBranch().isNewRecord()) {
			this.row_NewBranch.setVisible(true);
			this.newBranchCode.setMandatoryStyle(true);
		} else {
			this.row_NewBranch.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.branchCode.isReadonly()) {
			this.branchCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.branchDesc.isReadonly()) {
			this.branchDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.branchAddrLine1.isReadonly()) {
			this.branchAddrLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.branchAddrLine2.isReadonly()) {
			this.branchAddrLine2
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.branchPOBox.isReadonly()) {
			this.branchPOBox
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchPOBox.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.faxCountryCode.isReadonly()) {
			this.faxCountryCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_FaxCountryCode.value"), false, 1));
		}
		if (!this.faxAreaCode.isReadonly()) {
			this.faxAreaCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_FaxAreaCode.value"), false, 2));
		}
		if (!this.branchFax.isReadonly()) {
			this.branchFax.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_BranchFax.value"), false, 3));
		}
		if (!this.phoneCountryCode.isReadonly()) {
			this.phoneCountryCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_phoneCountryCode.value"), false, 1));
		}
		if (!this.phoneAreaCode.isReadonly()) {
			this.phoneAreaCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_phoneAreaCode.value"), false, 2));
		}
		if (!this.branchTel.isReadonly()) {
			this.branchTel.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_BranchTel.value"), false, 3));
		}
		if (!this.branchSwiftBankCode.isReadonly()) {
			this.branchSwiftBankCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftBankCde.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL4, false));
		}
		if (!this.branchSwiftLocCode.isReadonly()) {
			this.branchSwiftLocCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftLocCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL2, false));
		}
		if (!this.branchSwiftBrnCde.isReadonly()) {
			this.branchSwiftBrnCde
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftBrnCde.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.branchSortCode.isReadonly()) {
			this.branchSortCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSortCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL4, false));
		}
		if (!this.branchCountry.isReadonly()) {
			this.branchCountry.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCountry.value"), null, true, true));
		}
		if (!this.branchProvince.isReadonly()) {
			this.branchProvince.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BranchDialog_BranchProvince.value"), null, true, true));
		}
		if (!this.branchCity.isReadonly()) {
			this.branchCity.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCity.value"), null, true, true));
		}
		if (PennantConstants.CITY_FREETEXT) {
			if (!this.cityName.isReadonly()) {
				this.cityName.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_CityName.value"),
						PennantRegularExpressions.REGEX_NAME, false));
			}

		} else {
			if (!this.branchCity.isReadonly()) {
				this.branchCity.setConstraint(new PTStringValidator(
						Labels.getLabel("label_BranchDialog_BranchCity.value"), null, false, true));

			}

		}
		if (!this.branchSwiftCountry.isReadonly()) {
			this.branchSwiftCountry.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BranchDialog_BranchSwiftCountry.value"), null, false, true));
		}
		if (this.row_NewBranch.isVisible() && !this.newBranchCode.isReadonly()) {
			this.newBranchCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_NewBranchCode.value"), null, true, true));
		}
		if (!this.branchType.isDisabled()) {
			this.branchType.setConstraint(
					new StaticListValidator(branchTypeList, Labels.getLabel("label_BranchDialog_BranchType.value")));
		}
		if (!this.region.isDisabled()) {
			this.region.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_Region.value"), null, false, true));
		}
		if (!this.bankRefNo.isReadonly()) {
			this.bankRefNo.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BankRefNo.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, false));
		}
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_ParentBranch.value"), null, true, true));
		}
		if (!this.branchAddrHNbr.isReadonly()) {
			this.branchAddrHNbr
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrHNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.branchFlatNbr.isReadonly()) {
			this.branchFlatNbr
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchFlatNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.branchAddrStreet.isReadonly()) {
			this.branchAddrStreet
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrStreet.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (this.pinCode.isButtonVisible()) {
			this.pinCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_PinCode.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (this.row_org_struct.isVisible() && !this.entity.isReadonly()) {
			this.entity.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_Entity.value"), null, true, true));
		}
		if (this.row_org_struct.isVisible() && !this.cluster.isReadonly()) {
			this.cluster.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_Cluster.value"), null, true, true));
		}

		if (!this.defChequeDDPrintLoc.isReadonly()) {
			this.defChequeDDPrintLoc.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BranchDialog_DefChequeDDPrintLoc.value"), null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.branchCode.setConstraint("");
		this.branchDesc.setConstraint("");
		this.branchAddrLine1.setConstraint("");
		this.branchAddrLine2.setConstraint("");
		this.branchPOBox.setConstraint("");
		this.branchFax.setConstraint("");
		this.branchTel.setConstraint("");
		this.branchSwiftBankCode.setConstraint("");
		this.branchSwiftCountry.setConstraint("");
		this.branchSwiftLocCode.setConstraint("");
		this.branchSwiftBrnCde.setConstraint("");
		this.branchSortCode.setConstraint("");
		this.branchCountry.setConstraint("");
		this.branchCity.setConstraint("");
		this.cityName.setConstraint("");
		this.branchProvince.setConstraint("");
		this.branchSwiftCountry.setConstraint("");
		this.phoneCountryCode.setConstraint("");
		this.phoneAreaCode.setConstraint("");
		this.faxCountryCode.setConstraint("");
		this.faxAreaCode.setConstraint("");
		this.newBranchCode.setConstraint("");
		this.branchType.setConstraint("");
		this.region.setConstraint("");
		this.parentBranch.setConstraint("");
		this.bankRefNo.setConstraint("");
		this.branchAddrHNbr.setConstraint("");
		this.branchFlatNbr.setConstraint("");
		this.branchAddrStreet.setConstraint("");
		this.pinCode.setConstraint("");
		this.entity.setConstraint("");
		this.cluster.setConstraint("");
		this.defChequeDDPrintLoc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.branchCode.setErrorMessage("");
		this.branchDesc.setErrorMessage("");
		this.branchAddrLine1.setErrorMessage("");
		this.branchAddrLine2.setErrorMessage("");
		this.branchPOBox.setErrorMessage("");
		this.branchFax.setErrorMessage("");
		this.branchTel.setErrorMessage("");
		this.branchSwiftBankCode.setErrorMessage("");
		this.branchSwiftCountry.setErrorMessage("");
		this.branchSwiftLocCode.setErrorMessage("");
		this.branchSwiftBrnCde.setErrorMessage("");
		this.branchSortCode.setErrorMessage("");
		this.branchCity.setErrorMessage("");
		this.cityName.setErrorMessage("");
		this.branchProvince.setErrorMessage("");
		this.branchCountry.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		this.phoneAreaCode.setErrorMessage("");
		this.faxCountryCode.setErrorMessage("");
		this.faxAreaCode.setErrorMessage("");
		this.newBranchCode.setErrorMessage("");
		this.branchType.setErrorMessage("");
		this.parentBranch.setErrorMessage("");
		this.region.setErrorMessage("");
		this.bankRefNo.setErrorMessage("");
		this.branchAddrHNbr.setErrorMessage("");
		this.branchFlatNbr.setErrorMessage("");
		this.branchAddrStreet.setErrorMessage("");
		this.pinCode.setErrorMessage("");
		this.entity.setErrorMessage("");
		this.cluster.setErrorMessage("");
		this.defChequeDDPrintLoc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getBranchListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a Branch object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final Branch aBranch = new Branch();
		BeanUtils.copyProperties(getBranch(), aBranch);

		if (aBranch.getClusterId() != null) {
			if (aBranch.getClusterId() == 0) {
				aBranch.setClusterId(null);
			}
		}

		doDelete(Labels.getLabel("label_BranchDialog_BranchCode.value") + " : " + aBranch.getBranchCode(), aBranch);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getBranch().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.branchCountry.setMandatoryStyle(true);
			this.branchCity.setMandatoryStyle(true);
			this.branchProvince.setMandatoryStyle(true);
			this.pinCode.setMandatoryStyle(true);
			this.btnCancel.setVisible(true);
			this.entity.setMandatoryStyle(true);
			this.cluster.setMandatoryStyle(true);
		}

		this.branchDesc.setReadonly(isReadOnly("BranchDialog_branchDesc"));
		this.branchAddrLine1.setReadonly(isReadOnly("BranchDialog_branchAddrLine1"));
		this.branchAddrLine2.setReadonly(isReadOnly("BranchDialog_branchAddrLine2"));
		this.branchPOBox.setReadonly(isReadOnly("BranchDialog_branchPOBox"));
		this.branchCity.setReadonly(isReadOnly("BranchDialog_branchCity"));
		this.cityName.setReadonly(isReadOnly("BranchDialog_branchCity"));
		this.branchProvince.setReadonly(isReadOnly("BranchDialog_branchProvince"));
		this.branchCountry.setReadonly(isReadOnly("BranchDialog_branchCountry"));
		this.faxAreaCode.setReadonly(isReadOnly("BranchDialog_branchFax"));
		this.faxCountryCode.setReadonly(isReadOnly("BranchDialog_branchFax"));
		this.branchFax.setReadonly(isReadOnly("BranchDialog_branchFax"));
		this.branchTel.setReadonly(isReadOnly("BranchDialog_branchTel"));
		this.phoneAreaCode.setReadonly(isReadOnly("BranchDialog_branchTel"));
		this.phoneCountryCode.setReadonly(isReadOnly("BranchDialog_branchTel"));
		this.branchSwiftBankCode.setReadonly(isReadOnly("BranchDialog_branchSwiftBankCde"));
		this.branchSwiftCountry.setReadonly(isReadOnly("BranchDialog_branchSwiftCountry"));
		this.branchSwiftCountry.setReadonly(isReadOnly("BranchDialog_branchCountry"));
		this.branchSwiftLocCode.setReadonly(isReadOnly("BranchDialog_branchSwiftLocCode"));
		this.branchSwiftBrnCde.setReadonly(isReadOnly("BranchDialog_branchSwiftBrnCde"));
		this.branchSortCode.setReadonly(isReadOnly("BranchDialog_branchSortCode"));
		this.branchIsActive.setDisabled(isReadOnly("BranchDialog_branchIsActive"));
		this.branchType.setDisabled(isReadOnly("BranchDialog_BranchType"));
		this.miniBranch.setDisabled(isReadOnly("BranchDialog_MiniBranch"));
		this.bankRefNo.setReadonly(isReadOnly("BranchDialog_BankRefNo"));
		this.branchAddrHNbr.setReadonly(isReadOnly("BranchDialog_BranchAddrHNbr"));
		this.branchFlatNbr.setReadonly(isReadOnly("BranchDialog_BranchFlatNbr"));
		this.branchAddrStreet.setReadonly(isReadOnly("BranchDialog_BranchAddrStreet"));
		this.pinCode.setReadonly(isReadOnly("BranchDialog_PinCode"));
		this.entity.setReadonly(isReadOnly("BranchDialog_Entity"));
		this.cluster.setReadonly(isReadOnly("BranchDialog_ClusterType"));
		this.defChequeDDPrintLoc.setReadonly(isReadOnly("BranchDialog_DefChequeDDPrintLoc"));
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setReadonly(isReadOnly("BranchDialog_ParentBranch"));
		} else {
			this.parentBranch.setReadonly(true);
		}
		this.region.setDisabled(isReadOnly("BranchDialog_Region"));
		if (getBranch().isBranchIsActive()) {
			this.newBranchCode.setReadonly(isReadOnly("BranchDialog_newBranchCode"));
		} else {
			this.newBranchCode.setReadonly(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.branch.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.branchCode.setReadonly(true);
		this.branchDesc.setReadonly(true);
		this.branchAddrLine1.setReadonly(true);
		this.branchAddrLine2.setReadonly(true);
		this.branchPOBox.setReadonly(true);
		this.branchCity.setReadonly(true);
		this.cityName.setReadonly(true);
		this.branchProvince.setReadonly(true);
		this.branchCountry.setReadonly(true);
		this.branchFax.setReadonly(true);
		this.faxAreaCode.setReadonly(true);
		this.faxCountryCode.setReadonly(true);
		this.phoneAreaCode.setReadonly(true);
		this.phoneCountryCode.setReadonly(true);
		this.branchTel.setReadonly(true);
		this.branchSwiftBankCode.setReadonly(true);
		this.branchSwiftCountry.setReadonly(true);
		this.branchSwiftLocCode.setReadonly(true);
		this.branchSwiftBrnCde.setReadonly(true);
		this.branchSortCode.setReadonly(true);
		this.branchIsActive.setDisabled(true);
		this.newBranchCode.setReadonly(true);
		this.branchType.setReadonly(true);
		this.parentBranch.setReadonly(true);
		this.region.setReadonly(true);
		this.bankRefNo.setReadonly(true);
		this.branchAddrHNbr.setReadonly(true);
		this.branchFlatNbr.setReadonly(true);
		this.branchAddrStreet.setReadonly(true);
		this.pinCode.setReadonly(true);
		this.entity.setReadonly(true);
		this.cluster.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.branchCode.setValue("");
		this.branchDesc.setValue("");
		this.branchAddrLine1.setValue("");
		this.branchAddrLine2.setValue("");
		this.branchPOBox.setValue("");
		this.branchCity.setValue("");
		this.branchCity.setDescription("");
		this.cityName.setValue("");
		this.branchProvince.setValue("");
		this.branchProvince.setDescription("");
		this.branchCountry.setValue("");
		this.branchCountry.setDescription("");
		this.branchFax.setValue("");
		this.branchTel.setValue("");
		this.branchSwiftBankCode.setValue("");
		this.branchSwiftCountry.setValue("");
		this.branchSwiftCountry.setDescription("");
		this.branchSwiftLocCode.setValue("");
		this.branchSwiftBrnCde.setValue("");
		this.branchSortCode.setValue("");
		this.phoneCountryCode.setValue("");
		this.phoneAreaCode.setValue("");
		this.faxCountryCode.setValue("");
		this.faxAreaCode.setValue("");
		this.branchIsActive.setChecked(false);
		this.newBranchCode.setValue("");
		this.miniBranch.setValue("");
		this.branchType.setValue("");
		this.parentBranch.setValue("");
		this.region.setValue("");
		this.bankRefNo.setValue("");
		this.branchAddrStreet.setValue("");
		this.pinCode.setValue("");
		this.branchFlatNbr.setValue("");
		this.branchAddrHNbr.setValue("");
		this.parentBranch.setDescription("");
		this.entity.setDescription("");
		this.cluster.setDescription("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Branch aBranch = new Branch();
		BeanUtils.copyProperties(getBranch(), aBranch);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Branch object with the components data
		doWriteComponentsToBean(aBranch);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBranch.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBranch.getRecordType())) {
				aBranch.setVersion(aBranch.getVersion() + 1);
				if (isNew) {
					aBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBranch.setNewRecord(true);
				}
			}
		} else {
			aBranch.setVersion(aBranch.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aBranch, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aBranch  (Branch)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Branch aBranch, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBranch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBranch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBranch.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBranch.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBranch.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBranch);
				}

				if (isNotesMandatory(taskId, aBranch)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aBranch.setTaskId(taskId);
			aBranch.setNextTaskId(nextTaskId);
			aBranch.setRoleCode(getRole());
			aBranch.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBranch, tranType);

			String operationRefs = getServiceOperations(taskId, aBranch);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBranch, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBranch, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Branch aBranch = (Branch) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getBranchService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getBranchService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getBranchService().doApprove(auditHeader);

					if (aBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getBranchService().doReject(auditHeader);
					if (aBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BranchDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_BranchDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.branch), true);
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

	public void onFulfill$branchCountry(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = branchCountry.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			this.branchProvince.setValue("");
			this.branchProvince.setDescription("");
			this.branchCity.setValue("");
			this.branchCity.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Country country = (Country) dataObject;
			if (country == null) {
				fillProvinceDetails(null);
			}
			if (country != null) {
				this.branchProvince.setErrorMessage("");
				pcProvince = country.getCountryCode();
				fillProvinceDetails(pcProvince);
			} else {
				this.branchProvince.setObject("");
				this.branchCity.setObject("");
				this.pinCode.setObject("");
				this.branchProvince.setValue("");
				this.branchProvince.setDescription("");
				this.branchCity.setValue("");
				this.branchCity.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}
			fillPindetails(null, null);
		}
		logger.debug("Leaving" + event.toString());
	}

	private void fillProvinceDetails(String country) {
		this.branchProvince.setMandatoryStyle(true);
		this.branchProvince.setModuleName("Province");
		this.branchProvince.setValueColumn("CPProvince");
		this.branchProvince.setDescColumn("CPProvinceName");
		this.branchProvince.setValidateColumns(new String[] { "CPProvince" });

		Filter[] filters1 = new Filter[1];

		if (country == null || country.equals("")) {
			filters1[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.branchProvince.setFilters(filters1);
	}

	public void onFulfill$branchProvince(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = branchProvince.getObject();
		String pcProvince = this.branchProvince.getValue();
		if (dataObject instanceof String) {
			this.branchCity.setValue("");
			this.branchCity.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.branchProvince.setErrorMessage("");
				pcProvince = this.branchProvince.getValue();
				this.branchCountry.setValue(province.getCPCountry());
				this.branchCountry.setDescription(province.getLovDescCPCountryName());
				this.branchCity.setValue("");
				this.branchCity.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				fillPindetails(null, pcProvince);
			} else {
				this.branchCity.setObject("");
				this.pinCode.setObject("");
				this.branchCity.setValue("");
				this.branchCity.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}
		}
		fillCitydetails(pcProvince);
		logger.debug("Leaving" + event.toString());
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.branchCity.setModuleName("City");
		this.branchCity.setValueColumn("PCCity");
		this.branchCity.setDescColumn("PCCityName");
		this.branchCity.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[1];

		if (state == null || state.isEmpty()) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		this.branchCity.setFilters(filters1);
	}

	public void onFulfill$branchCity(Event event) {
		logger.debug("Entering");
		doRemoveValidation();
		doClearMessage();
		Object dataObject = branchCity.getObject();
		String cityValue = null;
		if (!(dataObject instanceof String)) {
			City details = (City) dataObject;
			if (details == null) {
				fillPindetails(null, null);
			}
			if (details != null) {
				this.branchProvince.setValue(details.getPCProvince());
				this.branchProvince.setDescription(details.getLovDescPCProvinceName());
				this.branchCountry.setValue(details.getPCCountry());
				this.branchCountry.setDescription(details.getLovDescPCCountryName());
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				cityValue = details.getPCCity();
				fillPindetails(cityValue, this.branchProvince.getValue());
			} else {
				this.branchCity.setObject("");
				this.pinCode.setObject("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				this.branchProvince.setErrorMessage("");
				this.branchCountry.setErrorMessage("");
				fillPindetails(null, this.branchProvince.getValue());
			}
		} else if ("".equals(dataObject)) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			this.branchProvince.setObject("");
		}
		logger.debug("Leaving");
	}

	private void fillPindetails(String id, String province) {
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });
		Filter[] filters1 = new Filter[1];

		if (id != null) {
			filters1[0] = new Filter("City", id, Filter.OP_EQUAL);
		} else if (province != null && !province.isEmpty()) {
			filters1[0] = new Filter("PCProvince", province, Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);
	}

	public void onFulfill$pinCode(Event event) {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
		if (dataObject instanceof String) {

		} else {
			PinCode details = (PinCode) dataObject;

			if (details != null) {
				this.branchCountry.setValue(details.getpCCountry());
				this.branchCountry.setDescription(details.getLovDescPCCountryName());
				this.branchCity.setValue(details.getCity());
				this.branchCity.setDescription(details.getPCCityName());
				this.branchProvince.setValue(details.getPCProvince());
				this.branchProvince.setDescription(details.getLovDescPCProvinceName());
				this.branchCity.setErrorMessage("");
				this.branchProvince.setErrorMessage("");
				this.branchCountry.setErrorMessage("");
				this.pinCode.setAttribute("pinCodeId", details.getPinCodeId());
				this.pinCode.setValue(details.getPinCode());
			}

		}
		Filter[] filters1 = new Filter[1];
		if (this.branchCity.getValue() != null && !this.branchCity.getValue().isEmpty()) {
			filters1[0] = new Filter("City", this.branchCity.getValue(), Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);

		logger.debug("Leaving");
	}

	public void onCheck$miniBranch(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setMandatoryStyle(true);
			readOnlyComponent(isReadOnly("BranchDialog_ParentBranch"), this.parentBranch);
		} else {
			this.parentBranch.setMandatoryStyle(false);
			readOnlyComponent(true, this.parentBranch);
			this.parentBranch.setValue("");
			this.parentBranch.setDescription("");
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$parentBranch(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = parentBranch.getObject();

		if (dataObject instanceof String) {
			this.parentBranch.setValue(dataObject.toString());

		} else {
			Branch details = (Branch) dataObject;

			if (details != null) {
				this.parentBranch.setAttribute("branchCode", details.getBranchCode());
				this.parentBranch.setValue(details.getBranchCode());
				this.parentBranch.setDescription(details.getBranchDesc());
			}
		}

		logger.debug("Leaving");

	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBranch
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Branch aBranch, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBranch.getBefImage(), aBranch);
		return new AuditHeader(String.valueOf(aBranch.getId()), null, null, null, auditDetail, aBranch.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.branch);
	}

	/**
	 * 
	 * @param event (Event)
	 */
	public void onCheck$branchIsActive(Event event) {
		logger.debug("Entering" + event.toString());
		doSetNewBranchProp();
		logger.debug("Leaving" + event.toString());
	}

	public void onChangeEntity(ForwardEvent event) {
		onChangeEntity();
	}

	private void onChangeEntity() {
		Object selectedEntiy = entity.getObject();

		if (selectedEntiy != null && selectedEntiy instanceof Entity) {
			doSetClusterFilter((Entity) selectedEntiy);
		} else {
			doSetClusterFilter(null);
		}
	}

	private void doSetClusterFilter(Entity entity) {
		this.cluster.setMaxlength(8);
		this.cluster.setMandatoryStyle(true);
		this.cluster.setModuleName("Cluster");
		this.cluster.setValueColumn("Code");
		this.cluster.setDescColumn("Name");
		this.cluster.setValidateColumns(new String[] { "Code" });

		if (entity == null) {
			this.cluster.setValue("");
			this.cluster.setFilters(new Filter[] { new Filter("Entity", null, Filter.OP_EQUAL) });
			return;
		}

		String selectedEntity = entity.getEntityCode();
		List<ClusterHierarchy> hierarchyList = clusterService.getClusterHierarcheyList(selectedEntity);
		Iterator<ClusterHierarchy> it = hierarchyList.iterator();

		String lowermostchild = null;

		if (it.hasNext()) {
			lowermostchild = it.next().getClusterType();
		}

		if (lowermostchild == null) {
			this.cluster.setValue("");
		}

		if (lowermostchild != null) {
			this.cluster.setFilters(new Filter[] { new Filter("Entity", selectedEntity, Filter.OP_EQUAL),
					new Filter("ClusterType", lowermostchild, Filter.OP_EQUAL) });
		} else {
			this.cluster.setFilters(new Filter[] { new Filter("Entity", null, Filter.OP_EQUAL),
					new Filter("ClusterType", null, Filter.OP_EQUAL) });

		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.branch.getBranchCode());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public Branch getBranch() {
		return this.branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public BranchService getBranchService() {
		return this.branchService;
	}

	public void setBranchListCtrl(BranchListCtrl branchListCtrl) {
		this.branchListCtrl = branchListCtrl;
	}

	public BranchListCtrl getBranchListCtrl() {
		return this.branchListCtrl;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

}