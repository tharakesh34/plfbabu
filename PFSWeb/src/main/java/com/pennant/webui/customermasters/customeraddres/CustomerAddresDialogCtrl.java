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
 * * FileName : CustomerAddresDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customeraddres;

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
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.District;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.CustomerViewDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerAddres/customerAddresDialog.zul file.
 */
public class CustomerAddresDialogCtrl extends GFCBaseCtrl<CustomerAddres> {
	private static final long serialVersionUID = -221443986307588127L;
	private static final Logger logger = LogManager.getLogger(CustomerAddresDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerAddresDialog; // autoWired

	protected Longbox custID; // autoWired
	protected ExtendedCombobox custAddrType; // autoWired
	protected ExtendedCombobox custCifAlternate;
	protected Combobox sameasAddressType;
	protected Label label_CustomerAddresDialog_CustAlternateCIF;
	protected Label label_CustomerAddresDialog_SameAsAddressType;
	protected Textbox custAddrHNbr; // autoWired
	protected Textbox custFlatNbr; // autoWired
	protected Textbox custAddrStreet; // autoWired
	protected Textbox custAddrLine1; // autoWired
	protected Textbox custAddrLine2; // autoWired
	protected Textbox custPOBox; // autoWired
	protected ExtendedCombobox custAddrCountry; // autoWired
	protected ExtendedCombobox custAddrProvince; // autoWired
	protected ExtendedCombobox custAddrCity; // autoWired
	protected ExtendedCombobox custAddrZIP; // autoWired
	protected Textbox custAddrPhone; // autoWired
	protected Textbox custCIF; // autoWired
	protected Label custShrtName; // autoWired
	protected Combobox custAddrPriority; // autoWired
	protected Textbox custCareOfAddr; // autoWired
	protected Textbox custSubDist; // autoWired
	protected ExtendedCombobox custDistrict; // autoWired

	protected Label CustomerSname; // autoWired
	protected Textbox cityName; // autowired
	protected Space space_custAddrStreet;

	// not autoWired variables
	private CustomerAddres customerAddres; // overHanded per parameter
	private transient CustomerAddresListCtrl customerAddresListCtrl; // overHanded

	private transient boolean validationOn;

	protected Button btnSearchPRCustid; // autoWired

	// ServiceDAOs / Domain Classes
	private transient CustomerAddresService customerAddresService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerAddres> customerAddress;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	private boolean workflow = false;
	private List<String> custCIFs = null;
	private final List<ValueLabel> CustomerPriorityList = PennantStaticListUtil.getCustomerEmailPriority();
	private List<CustomerAddres> approvedCustomerAddressList;

	/**
	 * default constructor.<br>
	 */
	public CustomerAddresDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerAddresDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerAddres object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerAddresDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerAddresDialog);

		if (PennantConstants.CITY_FREETEXT) {
			this.custAddrCity.setVisible(false);
			this.cityName.setVisible(true);
		} else {
			this.custAddrCity.setVisible(true);
			this.cityName.setVisible(false);
		}

		if (arguments.containsKey("customerAddres")) {
			this.customerAddres = (CustomerAddres) arguments.get("customerAddres");
			CustomerAddres befImage = new CustomerAddres();
			BeanUtils.copyProperties(this.customerAddres, befImage);
			this.customerAddres.setBefImage(befImage);
			setCustomerAddres(this.customerAddres);
		} else {
			setCustomerAddres(null);
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}

		if (getCustomerAddres().isNewRecord()) {
			setNewRecord(true);
		}

		if (arguments.containsKey("customerDialogCtrl")) {
			setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}

			this.customerAddres.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerAddresDialog");
			}
		}
		if (arguments.containsKey("customerViewDialogCtrl")) {
			setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments.get("customerViewDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}

			this.customerAddres.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerAddresDialog");
			}
		}
		if (arguments.containsKey("isFinanceProcess")) {
			isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
		}

		if (arguments.containsKey("fromLoan")) {
			isFinanceProcess = (Boolean) arguments.get("fromLoan");
		}
		if (arguments.containsKey("custCIFs")) {
			this.custCIFs = (List<String>) arguments.get("custCIFs");
		}

		if (getCustomerDialogCtrl() != null && !isFinanceProcess) {
			workflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}

		if (arguments.containsKey("fieldVerificationDialogCtrl")) {
			setNewCustomer(true);
		}
		doLoadWorkFlow(this.customerAddres.isWorkflow(), this.customerAddres.getWorkflowId(),
				this.customerAddres.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerAddresDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerAddresListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerAddres here.
		if (arguments.containsKey("customerAddresListCtrl")) {
			setCustomerAddresListCtrl((CustomerAddresListCtrl) arguments.get("customerAddresListCtrl"));
		} else {
			setCustomerAddresListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerAddres());

		// Calling SelectCtrl For proper selection of Customer
		if (isNewRecord() && !isNewCustomer()) {
			onLoad();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.custAddrType.setMaxlength(8);
		this.custAddrType.setTextBoxWidth(121);
		this.custAddrType.setMandatoryStyle(true);
		this.custAddrType.setModuleName("AddressType");
		this.custAddrType.setValueColumn("AddrTypeCode");
		this.custAddrType.setDescColumn("AddrTypeDesc");
		this.custAddrType.setValidateColumns(new String[] { "AddrTypeCode" });

		this.custAddrHNbr.setMaxlength(50);
		this.custFlatNbr.setMaxlength(50);
		this.custAddrStreet.setMaxlength(50);
		this.custAddrLine1.setMaxlength(50);
		this.custAddrLine2.setMaxlength(50);
		this.custPOBox.setMaxlength(8);
		this.custCareOfAddr.setMaxlength(50);
		this.custSubDist.setMaxlength(50);

		this.custAddrCountry.setMaxlength(2);
		this.custAddrCountry.setTextBoxWidth(121);
		this.custAddrCountry.setMandatoryStyle(true);
		this.custAddrCountry.setModuleName("Country");
		this.custAddrCountry.setValueColumn("CountryCode");
		this.custAddrCountry.setDescColumn("CountryDesc");
		this.custAddrCountry.setValidateColumns(new String[] { "CountryCode" });

		this.custAddrProvince.setMaxlength(8);
		this.custAddrProvince.setTextBoxWidth(121);
		this.custAddrProvince.setMandatoryStyle(true);
		this.custAddrProvince.setModuleName("Province");
		this.custAddrProvince.setValueColumn("CPProvince");
		this.custAddrProvince.setDescColumn("CPProvinceName");
		this.custAddrProvince.setValidateColumns(new String[] { "CPProvince" });

		this.custAddrCity.setMaxlength(50);
		this.custAddrCity.setTextBoxWidth(121);
		this.custAddrCity.setMandatoryStyle(true);
		this.custAddrCity.setModuleName("City");
		this.custAddrCity.setValueColumn("PCCity");
		this.custAddrCity.setDescColumn("PCCityName");
		this.custAddrCity.setValidateColumns(new String[] { "PCCity" });

		this.custDistrict.setMaxlength(8);
		this.custDistrict.setTextBoxWidth(121);
		this.custDistrict.setMandatoryStyle(false);
		this.custDistrict.setModuleName("District");
		this.custDistrict.setValueColumn("Code");
		this.custDistrict.setDescColumn("Name");
		this.custDistrict.setValidateColumns(new String[] { "Code" });

		this.custAddrZIP.setMaxlength(50);
		this.custAddrZIP.setTextBoxWidth(121);
		this.custAddrZIP.setMandatoryStyle(true);
		this.custAddrZIP.setModuleName("PinCode");
		this.custAddrZIP.setValueColumn("PinCodeId");
		this.custAddrZIP.setDescColumn("AreaName");
		this.custAddrZIP.setValueType(DataType.LONG);
		this.custAddrZIP.setValidateColumns(new String[] { "PinCodeId" });
		this.custAddrZIP.setInputAllowed(false);

		this.custAddrPhone.setMaxlength(50);
		this.cityName.setMaxlength(50);

		this.custCifAlternate.setTextBoxWidth(121);
		this.custCifAlternate.setModuleName("Customer");
		this.custCifAlternate.setValueColumn("CustCIF");
		this.custCifAlternate.setDescColumn("CustShrtName");
		this.custCifAlternate.setValidateColumns(new String[] { "CustCIF" });

		if (!ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
			this.space_custAddrStreet.setSclass("mandatory");
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
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
		getUserWorkspace().allocateAuthorities("CustomerAddresDialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerAddresDialog);
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
		doWriteBeanToComponents(this.customerAddres.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerAddres CustomerAddres
	 */
	public void doWriteBeanToComponents(CustomerAddres aCustomerAddres) {
		logger.debug("Entering");

		if (aCustomerAddres.getCustID() != Long.MIN_VALUE) {
			this.custID.setValue(aCustomerAddres.getCustID());
		}

		this.custAddrType.setValue(aCustomerAddres.getCustAddrType());
		this.custAddrHNbr.setValue(aCustomerAddres.getCustAddrHNbr());
		this.custFlatNbr.setValue(aCustomerAddres.getCustFlatNbr());
		this.custAddrStreet.setValue(aCustomerAddres.getCustAddrStreet());
		this.custAddrLine1.setValue(aCustomerAddres.getCustAddrLine1());
		this.custAddrLine2.setValue(aCustomerAddres.getCustAddrLine2());
		this.custPOBox.setValue(aCustomerAddres.getCustPOBox());
		this.custAddrCountry.setValue(aCustomerAddres.getCustAddrCountry());
		this.custAddrProvince.setValue(aCustomerAddres.getCustAddrProvince());
		this.custAddrCity.setValue(aCustomerAddres.getCustAddrCity());

		if (aCustomerAddres.getPinCodeId() != null) {
			this.custAddrZIP.setAttribute("pinCodeId", aCustomerAddres.getPinCodeId());
		} else {
			this.custAddrZIP.setAttribute("pinCodeId", null);
		}

		this.custAddrZIP.setValue(aCustomerAddres.getCustAddrZIP(), aCustomerAddres.getLovDescCustAddrZip());
		this.custAddrPhone.setValue(aCustomerAddres.getCustAddrPhone());
		this.cityName.setValue(aCustomerAddres.getCustAddrCity());
		this.custCIF.setValue(
				aCustomerAddres.getLovDescCustCIF() == null ? "" : aCustomerAddres.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerAddres.getLovDescCustShrtName() == null ? ""
				: aCustomerAddres.getLovDescCustShrtName().trim());
		this.custCareOfAddr.setValue(aCustomerAddres.getCustAddrLine3());
		this.custSubDist.setValue(aCustomerAddres.getCustAddrLine4());
		this.custDistrict.setValue(aCustomerAddres.getCustDistrict());

		if (aCustomerAddres.getCustAddrType() == null) {
			this.custAddrType.setDescription("");
			this.custAddrProvince.setDescription("");
			this.custAddrCity.setDescription("");
			this.custAddrZIP.setDescription("");
			this.custAddrCountry.setDescription(aCustomerAddres.getLovDescCustAddrCountryName());
		} else {
			this.custAddrType.setDescription(aCustomerAddres.getLovDescCustAddrTypeName());
			this.custAddrCountry.setDescription(aCustomerAddres.getLovDescCustAddrCountryName());
			this.custAddrProvince.setDescription(aCustomerAddres.getLovDescCustAddrProvinceName());
			this.custAddrCity.setDescription(aCustomerAddres.getLovDescCustAddrCityName());
			this.custAddrZIP.setDescription(aCustomerAddres.getLovDescCustAddrZip());
			this.custAddrType.setReadonly(true);
		}

		fillComboBox(this.custAddrPriority, String.valueOf(aCustomerAddres.getCustAddrPriority()), CustomerPriorityList,
				"");

		ArrayList<Filter> filters = new ArrayList<Filter>();

		if (this.custAddrCountry.getValue() != null && !this.custAddrCountry.getValue().isEmpty()) {
			Filter filterPin0 = new Filter("PCCountry", this.custAddrCountry.getValue(), Filter.OP_EQUAL);
			filters.add(filterPin0);
		}

		if (this.custAddrProvince.getValue() != null && !this.custAddrProvince.getValue().isEmpty()) {
			Filter filterPin1 = new Filter("PCProvince", this.custAddrProvince.getValue(), Filter.OP_EQUAL);
			filters.add(filterPin1);
		}

		if (this.custAddrCity.getValue() != null && !this.custAddrCity.getValue().isEmpty()) {
			Filter filterPin2 = new Filter("City", this.custAddrCity.getValue(), Filter.OP_EQUAL);
			filters.add(filterPin2);
		}

		Filter[] filterPin3 = new Filter[1];
		if (!StringUtils.isEmpty(this.custCIF.getValue())) {
			if (isFinanceProcess && custCIFs != null) {
				filterPin3[0] = new Filter("CustCIF", custCIFs, Filter.OP_IN);
			} else {
				filterPin3[0] = new Filter("CustCIF", this.custCIF.getValue(), Filter.OP_NOT_EQUAL);
			}

			custCifAlternate.setFilters(filterPin3);
		}

		Filter[] filterPin = new Filter[filters.size()];
		for (int i = 0; i < filters.size(); i++) {
			filterPin[i] = filters.get(i);
		}
		this.custAddrZIP.setFilters(filterPin);

		this.recordStatus.setValue(aCustomerAddres.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerAddres
	 */
	public void doWriteComponentsToBean(CustomerAddres aCustomerAddres) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerAddres.setCustID(this.custID.longValue());
			aCustomerAddres.setLovDescCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setLovDescCustAddrTypeName(this.custAddrType.getDescription());
			aCustomerAddres.setCustAddrType(this.custAddrType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrHNbr(this.custAddrHNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustFlatNbr(this.custFlatNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrStreet(this.custAddrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrLine1(this.custAddrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrLine2(this.custAddrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustPOBox(this.custPOBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setLovDescCustAddrCountryName(getLovDescription(this.custAddrCountry.getDescription()));
			aCustomerAddres.setCustAddrCountry(this.custAddrCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setLovDescCustAddrProvinceName(this.custAddrProvince.getDescription());
			aCustomerAddres.setCustAddrProvince(this.custAddrProvince.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (PennantConstants.CITY_FREETEXT) {
				aCustomerAddres.setCustAddrCity(StringUtils.trimToNull(this.cityName.getValue()));
			} else {
				aCustomerAddres.setLovDescCustAddrCityName(StringUtils.trimToNull(this.custAddrCity.getDescription()));
				aCustomerAddres.setCustAddrCity(StringUtils.trimToNull(this.custAddrCity.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			Object obj = this.custAddrZIP.getAttribute("pinCodeId");

			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aCustomerAddres.setPinCodeId(Long.valueOf((obj.toString())));
				}
			} else {
				aCustomerAddres.setPinCodeId(null);
			}

			aCustomerAddres.setCustAddrZIP(this.custAddrZIP.getValue());
			aCustomerAddres.setLovDescCustAddrZip(this.custAddrZIP.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrPhone(this.custAddrPhone.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.custAddrPriority.getSelectedItem() != null
					&& !StringUtils.trimToEmpty(this.custAddrPriority.getSelectedItem().getValue().toString())
							.equals(PennantConstants.List_Select)) {
				aCustomerAddres.setCustAddrPriority(
						Integer.parseInt(this.custAddrPriority.getSelectedItem().getValue().toString()));
			} else {
				aCustomerAddres.setCustAddrPriority(0);
			}

			if ("#".equals(getComboboxValue(this.custAddrPriority))) {
				throw new WrongValueException(this.custAddrPriority, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerAddresDialog_CustAddrPriority.value") }));
			} else {
				aCustomerAddres.setCustAddrPriority(
						Integer.parseInt(this.custAddrPriority.getSelectedItem().getValue().toString()));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomerAddres.setLovDescCustDistrictName(StringUtils.trimToNull(this.custDistrict.getDescription()));
			aCustomerAddres.setCustDistrict(StringUtils.trimToNull(this.custDistrict.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrLine4(this.custSubDist.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrLine3(this.custCareOfAddr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCustomerAddres.setRecordStatus(this.recordStatus.getValue());
		setCustomerAddres(aCustomerAddres);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerAddres
	 */
	public void doShowDialog(CustomerAddres aCustomerAddres) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custAddrType.getButton().focus();
		} else {
			this.custAddrPriority.focus();
			if (isNewCustomer()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerAddres);

			doCheckEnquiry();
			if (isNewCustomer()) {
				this.window_CustomerAddresDialog.setHeight("60%");
				this.window_CustomerAddresDialog.setWidth("95%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerAddresDialog.doModal();
			} else {
				this.window_CustomerAddresDialog.setWidth("60%");
				this.window_CustomerAddresDialog.setHeight("95%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
			this.custAddrType.setReadonly(true);
			this.custAddrHNbr.setReadonly(true);
			this.custFlatNbr.setReadonly(true);
			this.custAddrStreet.setReadonly(true);
			this.custAddrLine1.setReadonly(true);
			this.custAddrLine2.setReadonly(true);
			this.custPOBox.setReadonly(true);
			this.custAddrCountry.setReadonly(true);
			this.custAddrProvince.setReadonly(true);
			this.custAddrCity.setReadonly(true);
			this.custAddrZIP.setReadonly(true);
			this.custAddrPhone.setReadonly(true);
			this.cityName.setReadonly(true);
			this.custAddrPriority.setDisabled(true);
			this.custCareOfAddr.setReadonly(true);
			this.custSubDist.setReadonly(true);
			this.custDistrict.setReadonly(true);
			this.btnSearchPRCustid.setVisible(false);

		}

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		doClearMessage();

		if (!this.custID.isReadonly()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrCIF.value"), null, true));
		}

		if (!this.custAddrHNbr.isReadonly()) {
			this.custAddrHNbr.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrHNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}

		if (!this.custFlatNbr.isReadonly()) {
			this.custFlatNbr.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustFlatNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		boolean addressConstraint = false;
		if (StringUtils.isBlank(this.custAddrStreet.getValue()) && StringUtils.isBlank(this.custAddrLine1.getValue())
				&& StringUtils.isBlank(this.custAddrLine2.getValue())) {
			addressConstraint = true;
		}

		if (!ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
			if (!this.custAddrStreet.isReadonly() && addressConstraint) {
				this.custAddrStreet.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrStreet.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
		}

		if (!this.custAddrLine1.isReadonly() && addressConstraint) {
			this.custAddrLine1.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		if (!this.custAddrLine2.isReadonly() && addressConstraint) {
			this.custAddrLine2.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		if (!this.custPOBox.isReadonly()) {
			this.custPOBox
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustPOBox.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}

		if (this.custAddrZIP.isButtonVisible()) {
			this.custAddrZIP.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerAddresDialog_CustAddrZIP.value"), null, true, true));
		}

		if (!this.custAddrPriority.isDisabled()) {
			this.custAddrPriority.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerAddresDialog_CustAddrPriority.value"), null, true));
		}

		if (PennantConstants.CITY_FREETEXT) {
			if (!this.cityName.isReadonly()) {
				this.cityName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CityName.value"),
								PennantRegularExpressions.REGEX_NAME, false));
			}
		}
		if (!this.custCareOfAddr.isReadonly() && addressConstraint) {
			this.custCareOfAddr.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustCareOfAddr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.custSubDist.isReadonly() && addressConstraint) {
			this.custSubDist.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustSubDist.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.custAddrHNbr.setConstraint("");
		this.custFlatNbr.setConstraint("");
		this.custAddrStreet.setConstraint("");
		this.custAddrLine1.setConstraint("");
		this.custAddrLine2.setConstraint("");
		this.custPOBox.setConstraint("");
		this.custAddrZIP.setConstraint("");
		this.custAddrPhone.setConstraint("");
		this.cityName.setConstraint("");
		this.custAddrPriority.setConstraint("");
		this.custCareOfAddr.setConstraint("");
		this.custSubDist.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		this.custAddrType.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CustomerAddresDialog_CustAddrType.value"), null, true, true));

		this.custAddrCountry.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CustomerAddresDialog_CustAddrCountry.value"), null, true, true));

		this.custAddrProvince.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CustomerAddresDialog_CustAddrProvince.value"), null, true, true));

		if (!PennantConstants.CITY_FREETEXT) {
			this.custAddrCity.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerAddresDialog_CustAddrCity.value"), null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custAddrType.setConstraint("");
		this.custAddrCountry.setConstraint("");
		this.custAddrProvince.setConstraint("");
		this.custAddrCity.setConstraint("");
		this.custDistrict.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custAddrHNbr.setErrorMessage("");
		this.custFlatNbr.setErrorMessage("");
		this.custAddrStreet.setErrorMessage("");
		this.custAddrLine1.setErrorMessage("");
		this.custAddrLine2.setErrorMessage("");
		this.custPOBox.setErrorMessage("");
		this.custAddrZIP.setErrorMessage("");
		this.custAddrPhone.setErrorMessage("");
		this.custAddrType.setErrorMessage("");
		this.custAddrCountry.setErrorMessage("");
		this.custAddrProvince.setErrorMessage("");
		this.custAddrCity.setErrorMessage("");
		this.cityName.setErrorMessage("");
		this.custAddrPriority.setErrorMessage("");
		this.custCareOfAddr.setErrorMessage("");
		this.custDistrict.setErrorMessage("");
		this.custSubDist.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustomerAddresListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CustomerAddres aCustomerAddres = new CustomerAddres();
		BeanUtils.copyProperties(getCustomerAddres(), aCustomerAddres);

		final String keyReference = "Address Type :" + aCustomerAddres.getCustAddrType();

		doDelete(keyReference, aCustomerAddres);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final CustomerAddres aCustomerAddres) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.isBlank(aCustomerAddres.getRecordType())) {
			aCustomerAddres.setVersion(aCustomerAddres.getVersion() + 1);
			aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getCustomerDialogCtrl() != null
					&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
				aCustomerAddres.setNewRecord(true);
			}
			if (isWorkFlowEnabled()) {
				aCustomerAddres.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		} else if (StringUtils.equals(aCustomerAddres.getRecordType(), PennantConstants.RCD_UPD)) {
			aCustomerAddres.setNewRecord(true);
		}

		try {
			if (isNewCustomer()) {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFinanceCustomerProcess(aCustomerAddres, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerAddresDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerAddressDetails(this.customerAddress);
					closeDialog();
				}
			} else if (doProcess(aCustomerAddres, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
			if (isNewCustomer()) {
				this.btnCancel.setVisible(false);
				this.btnSearchPRCustid.setVisible(false);
				this.label_CustomerAddresDialog_SameAsAddressType.setVisible(true);
				this.label_CustomerAddresDialog_CustAlternateCIF.setVisible(true);
				this.custCifAlternate.setVisible(true);
				this.sameasAddressType.setVisible(true);
				this.sameasAddressType.setDisabled(true);
			} else {
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custAddrType.setReadonly(isReadOnly("CustomerAddresDialog_custAddrType"));
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custAddrType.setReadonly(true);
			this.label_CustomerAddresDialog_SameAsAddressType.setVisible(false);
			this.label_CustomerAddresDialog_CustAlternateCIF.setVisible(false);
			this.custCifAlternate.setVisible(false);
			this.sameasAddressType.setVisible(false);
		}

		this.custCIF.setReadonly(true);

		this.custAddrHNbr.setReadonly(isReadOnly("CustomerAddresDialog_custAddrHNbr"));
		this.custFlatNbr.setReadonly(isReadOnly("CustomerAddresDialog_custFlatNbr"));
		this.custAddrStreet.setReadonly(isReadOnly("CustomerAddresDialog_custAddrStreet"));
		this.custAddrLine1.setReadonly(isReadOnly("CustomerAddresDialog_custAddrLine1"));
		this.custAddrLine2.setReadonly(isReadOnly("CustomerAddresDialog_custAddrLine2"));
		this.custPOBox.setReadonly(isReadOnly("CustomerAddresDialog_custPOBox"));
		this.custAddrCountry.setReadonly(isReadOnly("CustomerAddresDialog_custAddrCountry"));
		this.custAddrProvince.setReadonly(isReadOnly("CustomerAddresDialog_custAddrProvince"));
		this.custAddrCity.setReadonly(isReadOnly("CustomerAddresDialog_custAddrCity"));
		this.cityName.setReadonly(isReadOnly("CustomerAddresDialog_custAddrCity"));
		this.custAddrZIP.setReadonly(isReadOnly("CustomerAddresDialog_custAddrZIP"));
		this.custAddrPhone.setReadonly(isReadOnly("CustomerAddresDialog_custAddrPhone"));
		this.custAddrPriority.setDisabled(isReadOnly("CustomerAddresDialog_custAddrPriority"));
		this.custCareOfAddr.setDisabled(isReadOnly("CustomerAddresDialog_custCareOfAddr"));
		this.custSubDist.setDisabled(isReadOnly("CustomerAddresDialog_custSubDist"));
		this.custDistrict.setReadonly(isReadOnly("CustomerAddresDialog_custDistrict"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerAddres.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCustomer) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		boolean isCustomerWorkflow = false;
		if (getCustomerDialogCtrl() != null) {
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow || isFinanceProcess) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.custAddrType.setReadonly(true);
		this.custAddrHNbr.setReadonly(true);
		this.custFlatNbr.setReadonly(true);
		this.custAddrStreet.setReadonly(true);
		this.custAddrLine1.setReadonly(true);
		this.custAddrLine2.setReadonly(true);
		this.custPOBox.setReadonly(true);
		this.custAddrCountry.setReadonly(true);
		this.custAddrProvince.setReadonly(true);
		this.custAddrCity.setReadonly(true);
		this.custAddrZIP.setReadonly(true);
		this.custAddrPhone.setReadonly(true);
		this.cityName.setReadonly(true);
		this.custAddrPriority.setDisabled(true);
		this.custCareOfAddr.setReadonly(true);
		this.custSubDist.setReadonly(true);
		this.custDistrict.setReadonly(true);

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
		this.custCIF.setText("");
		this.custAddrType.setValue("");
		this.custAddrType.setDescription("");
		this.custAddrHNbr.setValue("");
		this.custFlatNbr.setValue("");
		this.custAddrStreet.setValue("");
		this.custAddrLine1.setValue("");
		this.custAddrLine2.setValue("");
		this.custPOBox.setValue("");
		this.custAddrCountry.setValue("");
		this.custAddrCountry.setDescription("");
		this.custAddrProvince.setValue("");
		this.custAddrProvince.setDescription("");
		this.custAddrCity.setValue("");
		this.custAddrCity.setDescription("");
		this.custAddrZIP.setValue("");
		this.custAddrZIP.setDescription("");
		this.custAddrPhone.setValue("");
		this.cityName.setValue("");
		this.custAddrPriority.setText("");
		this.custCareOfAddr.setValue("");
		this.custSubDist.setValue("");
		this.custDistrict.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerAddres aCustomerAddres = new CustomerAddres();
		BeanUtils.copyProperties(getCustomerAddres(), aCustomerAddres);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerAddres object with the components data
		doWriteComponentsToBean(aCustomerAddres);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here
		isNew = aCustomerAddres.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerAddres.getRecordType())) {
				aCustomerAddres.setVersion(aCustomerAddres.getVersion() + 1);
				if (isNew) {
					aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerAddres.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomerAddres.setVersion(1);
					aCustomerAddres.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if (workflow && !isFinanceProcess && StringUtils.isBlank(aCustomerAddres.getRecordType())) {
						aCustomerAddres.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(aCustomerAddres.getRecordType())) {
					aCustomerAddres.setVersion(aCustomerAddres.getVersion() + 1);
					aCustomerAddres.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomerAddres.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aCustomerAddres.setVersion(aCustomerAddres.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewCustomer()) {
				AuditHeader auditHeader = newFinanceCustomerProcess(aCustomerAddres, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerAddresDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerAddressDetails(this.customerAddress);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(aCustomerAddres, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFinanceCustomerProcess(CustomerAddres aCustomerAddres, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomerAddres, tranType);
		customerAddress = new ArrayList<CustomerAddres>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aCustomerAddres.getLovDescCustCIF();
		valueParm[1] = aCustomerAddres.getCustAddrType();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustAddrType") + ":" + valueParm[1];

		if (getCustomerDialogCtrl().getCustomerAddressDetailList() != null
				&& getCustomerDialogCtrl().getCustomerAddressDetailList().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerAddressDetailList().size(); i++) {
				CustomerAddres customerAddres = getCustomerDialogCtrl().getCustomerAddressDetailList().get(i);

				if (!PennantConstants.TRAN_DEL.equals(tranType)) {
					if (!StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, customerAddres.getRecordType())
							&& !StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, customerAddres.getRecordType())
							&& !StringUtils.equals(customerAddres.getCustAddrType(), aCustomerAddres.getCustAddrType())
							&& aCustomerAddres.getCustAddrPriority() == Integer
									.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)
							&& customerAddres.getCustAddrPriority() == aCustomerAddres.getCustAddrPriority()) {
						valueParm[1] = this.custAddrPriority.getSelectedItem().getLabel();
						errParm[1] = PennantJavaUtil.getLabel("label_CustAddrPriority") + ":" + valueParm[1];
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "30702", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
				}

				if (aCustomerAddres.getCustAddrType().equals(customerAddres.getCustAddrType())) { // Both Current and
																									// Existing list
																									// addresses same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomerAddres.getRecordType().equals(PennantConstants.RCD_UPD)) {
							aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							customerAddress.add(aCustomerAddres);
						} else if (aCustomerAddres.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							customerAddress.add(aCustomerAddres);
						} else if (aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getAddressList()
									.size(); j++) {
								CustomerAddres address = getCustomerDialogCtrl().getCustomerDetails().getAddressList()
										.get(j);
								if (address.getCustID() == aCustomerAddres.getCustID()
										&& address.getCustAddrType().equals(aCustomerAddres.getCustAddrType())) {
									customerAddress.add(address);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							customerAddress.add(customerAddres);
						}
					}
				} else {
					customerAddress.add(customerAddres);
				}
			}
		}

		if (!recordAdded) {
			customerAddress.add(aCustomerAddres);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerAddres (CustomerAddres)
	 * 
	 * @param tranType        (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CustomerAddres aCustomerAddres, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerAddres.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerAddres.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerAddres.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerAddres.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerAddres);
				}

				if (isNotesMandatory(taskId, aCustomerAddres)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aCustomerAddres.setTaskId(taskId);
			aCustomerAddres.setNextTaskId(nextTaskId);
			aCustomerAddres.setRoleCode(getRole());
			aCustomerAddres.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerAddres, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerAddres);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerAddres, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerAddres, tranType);
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
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerAddres aCustomerAddres = (CustomerAddres) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCustomerAddresService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getCustomerAddresService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getCustomerAddresService().doApprove(auditHeader);

					if (aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCustomerAddresService().doReject(auditHeader);

					if (aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CustomerAddresDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerAddresDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.customerAddres), true);
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

	// Search Button Component Events

	public void onFulfill$custAddrType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custAddrType.getObject();
		if (dataObject instanceof String) {
			this.custAddrType.setValue(dataObject.toString());
			this.custAddrType.setDescription("");
		} else {
			AddressType details = (AddressType) dataObject;
			if (details != null) {
				this.custAddrType.setValue(details.getAddrTypeCode());
				this.custAddrType.setDescription(details.getAddrTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * on fulfill custAddrProvince
	 * 
	 * @param event
	 */

	public void onFulfill$custAddrCountry(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custAddrCountry.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			this.custAddrProvince.setValue("");
			this.custAddrProvince.setDescription("");
			this.custAddrCity.setValue("");
			this.custAddrCity.setDescription("");
			this.custAddrZIP.setValue("");
			this.custAddrZIP.setDescription("");
		} else if (!(dataObject instanceof String)) {
			Country country = (Country) dataObject;
			if (country != null) {
				this.custAddrProvince.setErrorMessage("");
				pcProvince = country.getCountryCode();
				fillProvinceDetails(pcProvince);
			} else {
				this.custAddrProvince.setObject("");
				this.custAddrCity.setObject("");
				this.custAddrZIP.setObject("");
				this.custAddrProvince.setValue("");
				this.custAddrProvince.setDescription("");
				this.custAddrCity.setValue("");
				this.custAddrCity.setDescription("");
				this.custAddrZIP.setValue("");
				this.custAddrZIP.setDescription("");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private void fillProvinceDetails(String country) {
		this.custAddrProvince.setMandatoryStyle(true);
		this.custAddrProvince.setModuleName("Province");
		this.custAddrProvince.setValueColumn("CPProvince");
		this.custAddrProvince.setDescColumn("CPProvinceName");
		this.custAddrProvince.setValidateColumns(new String[] { "CPProvince" });

		Filter[] filters1 = new Filter[1];

		if (country == null || country.equals("")) {
			filters1[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.custAddrProvince.setFilters(filters1);
	}

	public void onFulfill$custAddrProvince(Event event) {
		logger.debug("Entering");

		Object dataObject = custAddrProvince.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			fillPindetails(null, null);
		} else {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.custAddrProvince.setErrorMessage("");
				pcProvince = this.custAddrProvince.getValue();
				fillPindetails(null, pcProvince);
			}
		}

		this.custAddrCity.setObject("");
		this.custAddrZIP.setObject("");
		this.custAddrCity.setValue("");
		this.custAddrCity.setDescription("");
		this.custAddrZIP.setValue("");
		this.custAddrZIP.setDescription("");
		fillCitydetails(pcProvince);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * based on state param ,city will be filtered
	 * 
	 * @param state
	 */
	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.custAddrCity.setModuleName("City");
		this.custAddrCity.setValueColumn("PCCity");
		this.custAddrCity.setDescColumn("PCCityName");
		this.custAddrCity.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters = new Filter[2];

		if (state == null) {
			filters[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		this.custAddrCity.setFilters(filters);

		logger.debug("Leaving");
	}

	/**
	 * onFulfill custAddrCity
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$custAddrCity(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = custAddrCity.getObject();

		String cityValue = null;
		if (dataObject instanceof String) {
			this.custAddrCity.setValue("");
			this.custAddrCity.setDescription("");
			fillPindetails(null, null);
		} else {
			City city = (City) dataObject;
			if (city != null) {
				this.custAddrCity.setErrorMessage("");
				this.custAddrProvince.setErrorMessage("");

				this.custAddrProvince.setValue(city.getPCProvince());
				this.custAddrProvince.setDescription(city.getLovDescPCProvinceName());
				this.custDistrict.setValue(city.getDistrictCode());
				cityValue = this.custAddrCity.getValue();
			} else {
				fillCitydetails(custAddrProvince.getValue());
			}
		}

		fillPindetails(cityValue, this.custAddrProvince.getValue());

		this.custAddrZIP.setObject("");
		this.custAddrZIP.setValue("");
		this.custAddrZIP.setDescription("");

		Filter[] filters = null;
		if (StringUtils.isNotBlank(custAddrProvince.getValue())) {
			filters = new Filter[2];
			filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
			filters[0] = new Filter("PCProvince", custAddrProvince.getValue(), Filter.OP_EQUAL);
		} else {
			filters = new Filter[1];
			filters[0] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		}

		this.custAddrCity.setFilters(filters);

		logger.debug("Leaving");
	}

	/**
	 * onFulfill custAddrCity
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$custDistrict(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = custDistrict.getObject();

		if (dataObject instanceof String) {
			this.custDistrict.setValue("");
			this.custDistrict.setDescription("");
		} else {
			District district = (District) dataObject;
			if (district != null) {
				this.custDistrict.setErrorMessage("");
				this.custDistrict.setValue(district.getCode());
				this.custDistrict.setDescColumn(district.getName());

			}
		}
		logger.debug("Leaving");
	}

	/**
	 * based on param values,custaddrzip is filtered
	 * 
	 * @param cityValue
	 * @param provice
	 */

	private void fillPindetails(String cityValue, String provice) {
		logger.debug("Entering");

		this.custAddrZIP.setModuleName("PinCode");
		this.custAddrZIP.setValueColumn("PinCodeId");
		this.custAddrZIP.setDescColumn("AreaName");
		this.custAddrZIP.setValidateColumns(new String[] { "PinCodeId" });
		Filter[] filters = new Filter[2];

		if (cityValue != null) {
			filters[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if (provice != null && !provice.isEmpty()) {
			filters[0] = new Filter("PCProvince", provice, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		filters[1] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.custAddrZIP.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * onFulfill custAddrZip.based on custAddrZip,custAddrCity and custAddrprovince will auto populate
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$custAddrZIP(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = custAddrZIP.getObject();
		if (dataObject instanceof String) {
			this.custAddrZIP.setValue("");
			this.custAddrZIP.setDescription("");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.custAddrCity.setValue(pinCode.getCity());
				this.custAddrCity.setDescription(pinCode.getPCCityName());
				this.custAddrProvince.setValue(pinCode.getPCProvince());
				this.custAddrProvince.setDescription(pinCode.getLovDescPCProvinceName());
				this.custAddrCountry.setValue(pinCode.getpCCountry());
				this.custAddrCountry.setDescription(pinCode.getLovDescPCCountryName());

				this.custAddrCity.setErrorMessage("");
				this.custAddrProvince.setErrorMessage("");
				this.custAddrZIP.setErrorMessage("");
				this.custAddrZIP.setAttribute("pinCodeId", pinCode.getPinCodeId());
				this.custAddrZIP.setValue(pinCode.getPinCode());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onLoad();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 */
	private void onLoad() {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * @param aCustomerAddres
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CustomerAddres aCustomerAddres, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerAddres.getBefImage(), aCustomerAddres);
		return new AuditHeader(getReference(), String.valueOf(aCustomerAddres.getCustID()), null, null, auditDetail,
				aCustomerAddres.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerAddresDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.customerAddres);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerAddres().getCustID() + PennantConstants.KEY_SEPERATOR + getCustomerAddres().getCustAddrType();
	}

	public void onChange$sameasAddressType(Event event) {
		if (!"#".equals(getComboboxValue(this.sameasAddressType))) {
			for (CustomerAddres address : getApprovedCustomerAddressList()) {
				if (StringUtils.equals(getComboboxValue(this.sameasAddressType), address.getCustAddrType())) {
					final CustomerAddres customerAddres = new CustomerAddres();
					BeanUtils.copyProperties(address, customerAddres);
					customerAddres.setCustAddrType(StringUtils.isEmpty(this.custAddrType.getValue()) ? null : this.custAddrType.getValue());
					customerAddres.setLovDescCustAddrTypeName(StringUtils.isEmpty(this.custAddrType.getDescription()) ? null : this.custAddrType.getDescription());
					customerAddres.setCustID(Long.MIN_VALUE);
					customerAddres.setLovDescCustCIF(this.custCIF.getValue());
					customerAddres.setLovDescCustShrtName(this.custShrtName.getValue());
					doWriteBeanToComponents(customerAddres);
					break;
				}
			}
		} else {
			CustomerAddres customerAddres = new CustomerAddres();
			customerAddres.setLovDescCustCIF(this.custCIF.getValue());
			doWriteBeanToComponents(customerAddres);
		}
	}

	private void doFillAddressType(Customer customer) {
		List<ValueLabel> tempList = new ArrayList<ValueLabel>();
		if (customer != null) {
			this.sameasAddressType.setDisabled(false);
			resetData();
			setApprovedCustomerAddressList(
					getCustomerAddresService().getApprovedCustomerAddresById(customer.getCustID()));
			if (CollectionUtils.isNotEmpty(getApprovedCustomerAddressList())) {
				for (CustomerAddres address : getApprovedCustomerAddressList()) {
					tempList.add(new ValueLabel(address.getCustAddrType(), address.getCustAddrType()));
					fillComboBox(this.sameasAddressType, "", tempList, "");
				}
			}
		} else {
			this.sameasAddressType.setDisabled(true);
			fillComboBox(this.sameasAddressType, "", tempList, "");
		}
	}

	public void onFulfill$custCifAlternate(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.custCifAlternate.getObject() != null) {
			Customer customer = (Customer) this.custCifAlternate.getObject();
			doFillAddressType(customer);
		} else {
			doFillAddressType(null);
			resetData();
		}
		logger.debug(Literal.LEAVING);
	}

	public void resetData() {
		logger.debug(Literal.ENTERING);
		this.custAddrHNbr.setValue("");
		this.custFlatNbr.setValue("");
		this.custAddrStreet.setValue("");
		this.custAddrLine1.setValue("");
		this.custAddrLine2.setValue("");
		this.custPOBox.setValue("");
		this.custAddrCountry.setValue("");
		this.custAddrCountry.setDescription("");
		this.custAddrProvince.setValue("");
		this.custAddrProvince.setDescription("");
		this.custAddrCity.setValue("");
		this.custAddrCity.setDescription("");
		this.custAddrZIP.setValue("");
		this.custAddrZIP.setDescription("");
		this.custAddrPhone.setValue("");
		this.cityName.setValue("");
		this.custAddrPriority.setValue(Labels.getLabel("Combo.Select"));
		this.custCareOfAddr.setValue("");
		this.custSubDist.setValue("");
		this.custDistrict.setValue("");
		logger.debug(Literal.LEAVING);
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

	public CustomerAddres getCustomerAddres() {
		return this.customerAddres;
	}

	public void setCustomerAddres(CustomerAddres customerAddres) {
		this.customerAddres = customerAddres;
	}

	public void setCustomerAddresService(CustomerAddresService customerAddresService) {
		this.customerAddresService = customerAddresService;
	}

	public CustomerAddresService getCustomerAddresService() {
		return this.customerAddresService;
	}

	public void setCustomerAddresListCtrl(CustomerAddresListCtrl customerAddresListCtrl) {
		this.customerAddresListCtrl = customerAddresListCtrl;
	}

	public CustomerAddresListCtrl getCustomerAddresListCtrl() {
		return this.customerAddresListCtrl;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}

	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setCustomerAddress(List<CustomerAddres> customerAddress) {
		this.customerAddress = customerAddress;
	}

	public List<CustomerAddres> getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	private String getLovDescription(String value) {
		value = StringUtils.trimToEmpty(value);

		try {
			value = StringUtils.split(value, "-", 2)[1];
		} catch (Exception e) {
			//
		}

		return value;
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

	public List<CustomerAddres> getApprovedCustomerAddressList() {
		return approvedCustomerAddressList;
	}

	public void setApprovedCustomerAddressList(final List<CustomerAddres> approvedCustomerAddressList) {
		this.approvedCustomerAddressList = approvedCustomerAddressList;
	}
}
