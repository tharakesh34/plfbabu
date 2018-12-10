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

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  VehicleDealerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 01-05-2018		Vinay					 0.2  		                                                                                         * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.amtmasters.vehicledealer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMaster/VehicleDealer/vehicleDealerDialog.zul file. <br>
 */
public class VehicleDealerDialogCtrl extends GFCBaseCtrl<VehicleDealer> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VehicleDealerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VehicleDealerDialog; // autowired
	protected Label windowTitle;
	protected Row row_DealerType; // autowired
	protected Combobox dealerType; // autowired
	protected Textbox dealerName; // autowired
	protected Textbox dealerTelephone; // autowired
	protected Textbox dealerFax; // autowired
	protected Textbox dealerAddress1; // autowired
	protected Textbox dealerAddress2; // autowired
	protected Textbox dealerAddress3; // autowired
	protected Textbox dealerAddress4; // autowired
	protected ExtendedCombobox dealerCountry; // autowired
	protected ExtendedCombobox dealerCity; // autowired
	protected ExtendedCombobox dealerProvince; // autowired
	protected Textbox email;
	protected Textbox dealerPoBox;
	protected Space space_dealerPoBox;
	protected ExtendedCombobox zipCode;
	protected Textbox panNumber;
	protected Textbox taxNumber;
	protected Checkbox active;
	protected ExtendedCombobox productCtg;
	protected ExtendedCombobox emirates;
	protected Combobox commisionPaid;
	protected ExtendedCombobox commisionCalRule;
	protected Combobox paymentMode;
	protected Space space_PaymentMode;
	protected AccountSelectionBox accountNumber;
	protected Textbox iBANnumber;
	protected Label label_VehicleDealerDialog_AccountNumber;
	protected Space space_IBANnumber;
	protected ExtendedCombobox accountingSetId;
	protected Label label_VehicleDealerDialog_AccountingTreatment;
	protected long accountid;
	protected Combobox sellerType; // autowired
	protected Textbox cityName; // autoWired
	protected Space space_Code;
	protected Textbox code;
	protected Uppercasebox shortCode;
	protected Space space_shortCode;
	protected Label label_VehicleDealerDialog_shortCode;

/*	protected Textbox faxCountryCode;
	protected Textbox faxAreaCode;
	protected Textbox phoneCountryCode;
	protected Textbox phoneAreaCode;*/

	// not auto wired vars
	private VehicleDealer vehicleDealer; // overhanded per param
	private transient VehicleDealerListCtrl vehicleDealerListCtrl; // overhanded
																	// per param

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient VehicleDealerService vehicleDealerService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();
	private final List<ValueLabel> paymentModes = PennantStaticListUtil.getPaymentModes();
	private final List<ValueLabel> commisionPaidList = PennantStaticListUtil.getCommisionPaidList();
	private final List<ValueLabel> sellerTypes = PennantStaticListUtil.getSellerTypes();

	private transient String sDealerCountry;
	private transient String sDealerProvince;
	private String module = "";

	/**
	 * default constructor.<br>
	 */
	public VehicleDealerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VehicleDealerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected VehicleDealer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleDealerDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_VehicleDealerDialog);

		try {

			if (PennantConstants.CITY_FREETEXT) {
				this.dealerCity.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.dealerCity.setVisible(true);
				this.cityName.setVisible(false);
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("vehicleDealer")) {
				this.vehicleDealer = (VehicleDealer) arguments.get("vehicleDealer");
				VehicleDealer befImage = new VehicleDealer();
				BeanUtils.copyProperties(this.vehicleDealer, befImage);
				this.vehicleDealer.setBefImage(befImage);

				setVehicleDealer(this.vehicleDealer);
			} else {
				setVehicleDealer(null);
			}

			doLoadWorkFlow(this.vehicleDealer.isWorkflow(), this.vehicleDealer.getWorkflowId(),
					this.vehicleDealer.getNextTaskId());

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "VehicleDealerDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED params !
			// we get the vehicleDealerListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete vehicleDealer here.
			if (arguments.containsKey("vehicleDealerListCtrl")) {
				setVehicleDealerListCtrl((VehicleDealerListCtrl) arguments.get("vehicleDealerListCtrl"));
			} else {
				setVehicleDealerListCtrl(null);
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVehicleDealer());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_VehicleDealerDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		// Empty sent any required attributes
		this.dealerName.setMaxlength(50);
		this.dealerTelephone.setMaxlength(10);
		this.dealerFax.setMaxlength(10);
		this.dealerAddress1.setMaxlength(50);
		this.dealerAddress2.setMaxlength(50);
		this.dealerAddress3.setMaxlength(50);
		this.dealerAddress4.setMaxlength(50);
		this.dealerType.setMaxlength(50);
		this.sellerType.setMaxlength(50);
		this.cityName.setMaxlength(50);
		this.code.setMaxlength(20);
		this.dealerCountry.setMandatoryStyle(true);
		this.dealerCountry.setModuleName("Country");
		this.dealerCountry.setValueColumn("CountryCode");
		this.dealerCountry.setDescColumn("CountryDesc");
		this.dealerCountry.setValidateColumns(new String[] { "CountryCode" });

		this.dealerProvince.setMandatoryStyle(true);
		this.dealerProvince.setModuleName("Province");
		this.dealerProvince.setValueColumn("CPProvince");
		this.dealerProvince.setDescColumn("CPProvinceName");
		this.dealerProvince.setValidateColumns(new String[] { "CPProvince" });

		this.dealerCity.setMandatoryStyle(true);
		this.dealerCity.setModuleName("City");
		this.dealerCity.setValueColumn("PCCity");
		this.dealerCity.setDescColumn("PCCityName");
		this.dealerCity.setValidateColumns(new String[] { "PCCity" });

		this.accountNumber.setAcountDetails(AccountConstants.ACTYPES_DELAER, "", true);
		this.accountNumber.setFormatter(SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
		this.accountNumber.setMandatoryStyle(true);
		this.accountNumber.setTextBoxWidth(161);
		this.accountNumber.setButtonVisible(false);
		this.email.setMaxlength(50);

		this.shortCode.setMaxlength(3);
		this.email.setMaxlength(100);

		this.dealerPoBox.setMaxlength(8);

		this.zipCode.setMaxlength(50);
		this.zipCode.setTextBoxWidth(121);
		this.zipCode.setMandatoryStyle(true);
		this.zipCode.setModuleName("PinCode");
		this.zipCode.setValueColumn("PinCode");
		this.zipCode.setDescColumn("AreaName");
		this.zipCode.setValidateColumns(new String[] { "PinCode" });

		if (module.equals("DSA")) {
			//this.space_dealerPoBox.setSclass("");
			this.space_Code.setClass(PennantConstants.mandateSclass);
		} else {
			//this.space_dealerPoBox.setSclass(PennantConstants.mandateSclass);
			this.space_Code.setSclass("");
		}

		if (module.equals("DMA") || "CONN".equals(module)) {
			this.space_Code.setClass(PennantConstants.mandateSclass);
		}
		this.zipCode.setMaxlength(8);
		this.panNumber.setMaxlength(10);
		this.taxNumber.setMaxlength(15);

		this.productCtg.setModuleName("VASProductCategory");
		this.productCtg.setMandatoryStyle(true);
		this.productCtg.setValueColumn("ProductCtg");
		this.productCtg.setDescColumn("ProductCtgDesc");
		this.productCtg.setDisplayStyle(2);
		this.productCtg.setValidateColumns(new String[] { "productCtg", "ProductCtgDesc" });
		Filter[] filtersProductCtg = new Filter[1];
		filtersProductCtg[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.productCtg.setFilters(filtersProductCtg);

		this.emirates.setMandatoryStyle(true);
		this.emirates.setTextBoxWidth(161);
		this.emirates.setModuleName("Province");
		this.emirates.setValueColumn("CPProvince");
		this.emirates.setDescColumn("CPProvinceName");
		this.emirates.setValidateColumns(new String[] { "CPProvince" });
		Filter[] emiratesRegNumb = new Filter[1];
		emiratesRegNumb[0] = new Filter("CPCountry", "AE", Filter.OP_EQUAL);
		this.emirates.setFilters(emiratesRegNumb);

		this.commisionCalRule.setMandatoryStyle(false);
		this.commisionCalRule.setTextBoxWidth(161);
		this.commisionCalRule.setModuleName("Rule");
		this.commisionCalRule.setValueColumn("RuleCode");
		this.commisionCalRule.setDescColumn("RuleCodeDesc");
		this.commisionCalRule.setValidateColumns(new String[] { "RuleCode" });
		Filter[] calRuleFilters = new Filter[1];
		calRuleFilters[0] = new Filter("RuleEvent", "ADDDBS", Filter.OP_EQUAL);
		this.commisionCalRule.setFilters(calRuleFilters);

		this.email.setMaxlength(50);
		this.accountingSetId.setMandatoryStyle(true);
		this.accountingSetId.setModuleName("AccountingSet");
		this.accountingSetId.setValueColumn("AccountSetCode");
		this.accountingSetId.setDescColumn("AccountSetCodeName");
		this.accountingSetId.setValidateColumns(new String[] { "AccountSetCode" });
		Filter[] accountingFilters = new Filter[1];
		accountingFilters[0] = new Filter("EventCode", "VENCOM", Filter.OP_EQUAL);
		this.accountingSetId.setFilters(accountingFilters);

		if ("VASM".equals(module)) {
			this.label_VehicleDealerDialog_shortCode.setVisible(true);
			this.space_shortCode.setVisible(true);
			this.shortCode.setVisible(true);
		}
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);

		} else {
			this.groupboxWf.setVisible(false);
		}

		setWindowTitle();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting the window title based on the module
	 */
	private void setWindowTitle() {
		if (module.equals(VASConsatnts.VASAGAINST_PARTNER)) {
			this.windowTitle.setValue(Labels.getLabel("window_VehicleDealerDialog_Patner.title"));
		} else if (module.equals(Agencies.FIAGENCY.getKey())) {
			this.windowTitle.setValue(Agencies.FIAGENCY.getValue());
		} else if (module.equals(Agencies.LVAGENCY.getKey())) {
			this.windowTitle.setValue(Agencies.LVAGENCY.getValue());
		} else if (module.equals(Agencies.RCUVAGENCY.getKey())) {
			this.windowTitle.setValue(Agencies.RCUVAGENCY.getValue());
		} else if (module.equals(Agencies.TVAGENCY.getKey())) {
			this.windowTitle.setValue(Agencies.TVAGENCY.getValue());
		} else if (module.equals(Agencies.DMA.getKey())) {
			this.windowTitle.setValue(Agencies.DMA.getValue());
		} else if (module.equals(Agencies.DSA.getKey())) {
			this.windowTitle.setValue(Agencies.DSA.getValue());
		} else if (module.equals(Agencies.CONN.getKey())) {
			this.windowTitle.setValue(Agencies.CONN.getValue());
		}
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_VehicleDealerDialog);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
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
		doClose(this.btnSave.isVisible());
	}

	// GUI Process



	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.vehicleDealer.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}
	public void onFulfill$productCtg(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = productCtg.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.productCtg.setValue("");
			this.productCtg.setDescription("");
			this.productCtg.setAttribute("ProductCtg", null);
		} else {
			VASProductCategory details = (VASProductCategory) dataObject;
			this.productCtg.setAttribute("ProductCtg", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVehicleDealer
	 *            VehicleDealer
	 */
	public void doWriteBeanToComponents(VehicleDealer aVehicleDealer) {
		logger.debug(Literal.ENTERING);
		fillComboBox(dealerType, module, PennantStaticListUtil.getDealerType(), "");
		this.dealerName.setValue(aVehicleDealer.getDealerName());
		this.dealerTelephone.setValue(aVehicleDealer.getDealerTelephone());
		this.dealerFax.setValue(aVehicleDealer.getDealerFax());
		this.dealerAddress1.setValue(aVehicleDealer.getDealerAddress1());
		this.dealerAddress2.setValue(aVehicleDealer.getDealerAddress2());
		this.dealerAddress3.setValue(aVehicleDealer.getDealerAddress3());
		this.dealerAddress4.setValue(aVehicleDealer.getDealerAddress4());
		this.dealerCountry.setValue(aVehicleDealer.getDealerCountry());
		this.code.setValue(vehicleDealer.getCode());
		this.shortCode.setValue(vehicleDealer.getShortCode());

		this.dealerProvince.setValue(aVehicleDealer.getDealerProvince());
		this.cityName.setValue(aVehicleDealer.getDealerCity());
		if (!PennantConstants.CITY_FREETEXT) {
			this.dealerCity.setValue(aVehicleDealer.getDealerCity());
		}
		sDealerCountry = this.dealerCountry.getValue();
		sDealerProvince = this.dealerProvince.getValue();
		doSetDealerProvince();
		doSetDealerCity();

		this.dealerCountry.setDescription(aVehicleDealer.getLovDescCountry());
		this.dealerProvince.setDescription(aVehicleDealer.getLovDescProvince());
		this.dealerCity.setDescription(aVehicleDealer.getLovDescCity());
		this.accountingSetId.setDescription(aVehicleDealer.getAccountingSetDesc());
		this.commisionCalRule.setDescription(aVehicleDealer.getCalRuleDesc());
		this.emirates.setDescription(aVehicleDealer.getEmiratesDescription());

		fillComboBox(this.commisionPaid, aVehicleDealer.getCommisionPaidAt(), commisionPaidList, "");
		fillComboBox(this.paymentMode, aVehicleDealer.getPaymentMode(), paymentModes, "");
		checkPaymentMode();
		this.iBANnumber.setReadonly(true);
		if (this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.AHBACCOUNT)) {
			this.accountNumber.setValue(StringUtils.trimToEmpty(aVehicleDealer.getAccountNumber()));
		} else if (this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.FTS)) {
			this.iBANnumber.setValue(StringUtils.trimToEmpty(aVehicleDealer.getAccountNumber()));
		}
		this.email.setValue(aVehicleDealer.getEmail());
		this.emirates.setValue(aVehicleDealer.getEmirates());
		this.dealerPoBox.setValue(aVehicleDealer.getPOBox());
		this.zipCode.setValue(aVehicleDealer.getZipCode());
		this.panNumber.setValue(aVehicleDealer.getPanNumber());
		this.taxNumber.setValue(aVehicleDealer.getTaxNumber());
		if (!aVehicleDealer.isNewRecord()) {
			this.productCtg.setValue(StringUtils.trimToEmpty(aVehicleDealer.getProductCtg()),
					StringUtils.trimToEmpty(aVehicleDealer.getProductCtgDesc()));
			if (aVehicleDealer.getProductCtg() != null) {
				this.productCtg.setAttribute("ProductCtg", aVehicleDealer.getProductCtg());
			} else {
				this.productCtg.setAttribute("ProductCtg", null);
			}
		}
		this.active.setChecked(aVehicleDealer.isActive());
		this.accountingSetId.setValue(aVehicleDealer.getAccountingSetCode());
		this.commisionCalRule.setValue(aVehicleDealer.getCalculationRule());
		fillComboBox(sellerType, aVehicleDealer.getSellerType(), sellerTypes, "");
		this.recordStatus.setValue(aVehicleDealer.getRecordStatus());
		if (aVehicleDealer.isNew() || (aVehicleDealer.getRecordType() != null ? aVehicleDealer.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVehicleDealer
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(VehicleDealer aVehicleDealer) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.row_DealerType.isVisible()
					&& this.dealerType.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.dealerType, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_VehicleDealerDialog_DealerType.value") }));
			}

			aVehicleDealer.setDealerType(this.dealerType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerName(this.dealerName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerTelephone(this.dealerTelephone.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerFax(this.dealerFax.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress1(this.dealerAddress1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress2(this.dealerAddress2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress3(this.dealerAddress3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress4(this.dealerAddress4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setLovDescCountry(this.dealerCountry.getDescription());
			aVehicleDealer.setDealerCountry(this.dealerCountry.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (PennantConstants.CITY_FREETEXT) {
				aVehicleDealer.setDealerCity(StringUtils.trimToNull(this.cityName.getValue()));
			} else {
				aVehicleDealer.setLovDescCity(this.dealerCity.getDescription());
				aVehicleDealer.setDealerCity(this.dealerCity.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aVehicleDealer.setLovDescProvince(this.dealerProvince.getDescription());
			aVehicleDealer.setDealerProvince(this.dealerProvince.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setPOBox(this.dealerPoBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setZipCode(this.zipCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setPanNumber(this.panNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setTaxNumber(this.taxNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setProductCtgDesc(this.productCtg.getDescription());
			aVehicleDealer.setProductCtg(this.productCtg.getValue());
			this.productCtg.getValidatedValue();
			Object object = this.productCtg.getAttribute("ProductCtg");
			if (object != null) {
				aVehicleDealer.setProductCtg(object.toString());
			} else {
				aVehicleDealer.setProductCtg(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.commisionPaid.isVisible()
					&& !StringUtils.trimToEmpty(this.commisionPaid.getSelectedItem().getValue().toString())
							.equals(PennantConstants.List_Select)) {
				aVehicleDealer.setCommisionPaidAt(this.commisionPaid.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.paymentMode.getSelectedItem() != null
					&& !StringUtils.trimToEmpty(this.paymentMode.getSelectedItem().getValue().toString())
							.equals(PennantConstants.List_Select)) {
				aVehicleDealer.setPaymentMode(this.paymentMode.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.paymentMode.getSelectedIndex() > 0) {
				if (this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.FTS)) {
					aVehicleDealer.setAccountNumber(this.iBANnumber.getValue());
				} else if (this.paymentMode.getSelectedItem().getValue().toString()
						.equals(PennantConstants.AHBACCOUNT)) {
					this.accountNumber.validateValue();
					aVehicleDealer.setAccountNumber(
							PennantApplicationUtil.unFormatAccountNumber(this.accountNumber.getValue()));
				} else {
					aVehicleDealer.setAccountNumber("");

				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setEmail(this.email.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aVehicleDealer.setCalculationRule(this.commisionCalRule.getValue());
			aVehicleDealer.setCalRuleDesc(this.commisionCalRule.getDescription());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setEmirates(this.emirates.getValue());
			aVehicleDealer.setEmiratesDescription(this.emirates.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setAccountingSetCode(this.accountingSetId.getValue());
			aVehicleDealer.setAccountingSetDesc(this.accountingSetId.getDescription());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aVehicleDealer.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setShortCode(this.shortCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.sellerType.getSelectedItem().getValue().equals(PennantConstants.List_Select)
					&& !this.sellerType.isVisible()) {
				throw new WrongValueException(this.sellerType, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_VehicleDealerDialog_SellerType.value") }));
			}

			aVehicleDealer.setSellerType(this.sellerType.getSelectedItem().getValue().toString());
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

		aVehicleDealer.setRecordStatus(this.recordStatus.getValue());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aVehicleDealer
	 * @throws Exception
	 */
	public void doShowDialog(VehicleDealer aVehicleDealer) throws Exception {
		logger.debug(Literal.ENTERING);

		// set Readonly mode accordingly if the object is new or not.
		if (aVehicleDealer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.dealerName.focus();
		} else {
			this.dealerName.focus();
			if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aVehicleDealer);

			setDialog(DialogType.EMBEDDED);
			checkCommisionpaid();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_VehicleDealerDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		doClearMessage();

		if (!this.dealerName.isReadonly()) {
			this.dealerName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerName.value"),
							PennantRegularExpressions.REGEX_UPPERCASENAME, true));
		}

		if (!this.dealerTelephone.isReadonly()) {
			this.dealerTelephone.setConstraint(new PTMobileNumberValidator(
					Labels.getLabel("label_VehicleDealerDialog_DealerTelephone.value"), true, PennantRegularExpressions.MOBILE_REGEX));
		}

		if (!this.dealerAddress1.isReadonly()) {
			this.dealerAddress1.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerAddress1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.dealerAddress2.isReadonly()) {
			this.dealerAddress2.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerAddress2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.dealerCountry.isReadonly()) {
			this.dealerCountry.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VehicleDealerDialog_DealerCountry.value"), null, true, true));
		}
		if (!this.dealerProvince.isReadonly()) {
			this.dealerProvince.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VehicleDealerDialog_DealerProvince.value"), null, true, true));
		}

		if (!this.dealerFax.isReadonly()) {
			this.dealerFax.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_VehicleDealerDialog_DealerFax.value"), true,
							PennantRegularExpressions.TELEPHONE_FAX_REGEX));
		}

		if (!this.email.isReadonly()) {
			this.email.setConstraint(
					new PTEmailValidator(Labels.getLabel("label_VehicleDealerDialog_Email.value"), true));
		}
		if (!this.emirates.isReadonly()) {
			this.emirates.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VehicleDealerDialog_Emirates.value"), null, false, true));
		}
		if (!this.accountingSetId.isReadonly()
				&& !"N".equals(this.commisionPaid.getSelectedItem().getValue().toString())
				&& !this.commisionPaid.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
			this.accountingSetId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VehicleDealerDialog_AccountingTreatment.value"), null, true, true));
		}
		if (!this.commisionCalRule.isReadonly()) {
			this.commisionCalRule.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VehicleDealerDialog_CommisionCalRule.value"), null, false, true));
		}
		if (!this.paymentMode.isDisabled() && !"N".equals(this.commisionPaid.getSelectedItem().getValue().toString())
				&& !this.commisionPaid.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
			this.paymentMode.setConstraint(new PTListValidator(
					Labels.getLabel("label_VehicleDealerDialog_PaymentMode.value"), paymentModes, true));
		}
		if (!this.commisionPaid.isDisabled()) {
			this.commisionPaid.setConstraint(new PTListValidator(
					Labels.getLabel("label_VehicleDealerDialog_CommisionPaidAt.value"), commisionPaidList, true));
		}
		if (!module.equals("DSA")) {
			if (!this.dealerPoBox.isReadonly()) {
				this.dealerPoBox
						.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_Pobox.value"),
								PennantRegularExpressions.REGEX_NUMERIC, false));
			}
		}
		if (!this.zipCode.isReadonly()) {
			this.zipCode.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_ZipCode.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		if (!this.panNumber.isReadonly()) {
			this.panNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_PANNumber.value"),
							PennantRegularExpressions.REGEX_PANNUMBER, false));
		}
		if (!this.taxNumber.isReadonly()) {
			this.taxNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_TaxNumber.value"),
							PennantRegularExpressions.REGEX_GSTIN, false));
		}
		if (!this.productCtg.isReadonly()) {
			this.productCtg.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VehicleDealerDialog_ProductCtg.value"), null, true, true));
		}
		if (!this.accountNumber.isReadonly()) {
			this.accountNumber.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VehicleDealerDialog_AccountNumber.value"), null, true));
		}
		if (!this.iBANnumber.isReadonly()) {
			this.iBANnumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_IBANNumber.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL23, true));
		}

		if (PennantConstants.CITY_FREETEXT) {
			if (!this.cityName.isReadonly()) {
				this.cityName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerCity.value"),
								PennantRegularExpressions.REGEX_NAME, true));
			}
		}

		if (!this.dealerCity.isReadonly()) {
			this.dealerCity.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerCity.value"), null, true));
		}
		if (module.equals("DSA") || module.equals("DMA") || "CONN".equals(module)) {
			if (!this.code.isReadonly()) {
				this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_Code.value"),
						PennantRegularExpressions.REGEX_SPECIAL_REGX, true));
			}
		}
		if ("VASM".equals(module)) {
			if (!this.shortCode.isReadonly()) {
				this.shortCode
						.setConstraint(
								new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_ShortCode.value"),
										PennantRegularExpressions.REGEX_ALPHANUM, true));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.dealerType.setConstraint("");
		this.dealerName.setConstraint("");
		this.dealerTelephone.setConstraint("");
		this.dealerFax.setConstraint("");
		this.dealerAddress1.setConstraint("");
		this.dealerAddress2.setConstraint("");
		this.dealerAddress3.setConstraint("");
		this.dealerAddress4.setConstraint("");
		this.dealerCountry.setConstraint("");
		this.dealerCity.setConstraint("");
		this.dealerProvince.setConstraint("");
		this.accountNumber.setConstraint("");
		this.iBANnumber.setConstraint("");
		this.paymentMode.setConstraint("");
		this.commisionPaid.setConstraint("");
		this.commisionCalRule.setConstraint("");
		this.code.setConstraint("");
		this.email.setConstraint("");
		this.emirates.setConstraint("");
		this.dealerPoBox.setConstraint("");
		this.zipCode.setConstraint("");
		this.sellerType.setConstraint("");
		this.cityName.setConstraint("");
		this.shortCode.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	// CRUD operations

	/**
	 * Deletes a VehicleDealer object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final VehicleDealer aVehicleDealer = new VehicleDealer();
		BeanUtils.copyProperties(getVehicleDealer(), aVehicleDealer);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_VehicleDealerDialog_DealerName.value") + " : "
				+ aVehicleDealer.getDealerName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aVehicleDealer.getRecordType())) {
				aVehicleDealer.setVersion(aVehicleDealer.getVersion() + 1);
				aVehicleDealer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aVehicleDealer.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aVehicleDealer, tranType)) {
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
		logger.debug(Literal.ENTERING);

		int count = vehicleDealerService.getVASManufactureCode(getVehicleDealer().getDealerName());
		if (getVehicleDealer().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.dealerType.setDisabled(false);
			this.sellerType.setDisabled(false);
		} else {
			this.emirates.setMandatoryStyle(true);
			this.commisionCalRule.setMandatoryStyle(true);
			this.dealerType.setDisabled(true);
			this.btnCancel.setVisible(true);
			if (this.paymentMode.getSelectedItem() != null
					&& this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.PAYORDER)) {
				this.accountNumber.setReadonly(true);
			} else {
				this.accountNumber.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_AccountNumber"));
				this.iBANnumber.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_AccountNumber"));
			}
		}
		this.dealerName.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerName"));
		this.dealerTelephone.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerName"));
		this.dealerFax.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerFax"));
		this.dealerAddress1.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerAddress1"));
		this.dealerAddress2.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerAddress2"));
		this.dealerAddress3.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerAddress3"));
		this.dealerAddress4.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerAddress4"));
		this.dealerCountry.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerCountry"));
		this.dealerCountry.setMandatoryStyle(!isReadOnly("VehicleDealerDialog_" + module + "_dealerCountry"));
		this.dealerCity.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerCity"));
		this.dealerProvince.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerProvince"));
		this.paymentMode.setDisabled(isReadOnly("VehicleDealerDialog_" + module + "_PaymentMode"));
		this.emirates.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_Emirates"));
		this.email.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_Email"));
		this.accountingSetId.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_AccountingTreatment"));
		this.accountingSetId.setMandatoryStyle(!isReadOnly("VehicleDealerDialog_" + module + "_AccountingTreatment"));
		this.commisionPaid.setDisabled(isReadOnly("VehicleDealerDialog_" + module + "_CommisionPaidAt"));
		this.commisionCalRule.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_CalculationRule"));
		this.dealerPoBox.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_PoBox"));
		this.sellerType.setDisabled(isReadOnly("VehicleDealerDialog_" + module + "_SellerType"));
		this.cityName.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_dealerCity"));
		this.zipCode.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_ZipCode"));
		this.panNumber.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_PanNumber"));
		this.taxNumber.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_TaxNumber"));
		this.productCtg.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_TaxNumber"));
		this.active.setDisabled(isReadOnly("VehicleDealerDialog_" + module + "_Active"));
		this.code.setReadonly(isReadOnly("VehicleDealerDialog_" + module + "_Code"));
		this.shortCode.setReadonly(isReadOnly("VehicleDealerDialog_VASM_Code"));
		// this.code.setReadonly(false);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.vehicleDealer.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		if (count > 0) {
			this.btnDelete.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.dealerType.setDisabled(true);
		this.dealerName.setReadonly(true);
		this.dealerTelephone.setReadonly(true);
		this.dealerFax.setReadonly(true);
		this.dealerAddress1.setReadonly(true);
		this.dealerAddress2.setReadonly(true);
		this.dealerAddress3.setReadonly(true);
		this.dealerAddress4.setReadonly(true);
		this.dealerCountry.setReadonly(true);
		this.dealerCity.setReadonly(true);
		this.dealerProvince.setReadonly(true);
		this.emirates.setReadonly(true);
		this.paymentMode.setDisabled(true);
		this.email.setReadonly(true);
		this.accountingSetId.setReadonly(true);
		this.commisionPaid.setDisabled(true);
		this.commisionCalRule.setReadonly(true);
		this.iBANnumber.setReadonly(true);
		this.dealerPoBox.setReadonly(true);
		this.accountNumber.setReadonly(true);
		this.sellerType.setDisabled(true);
		this.cityName.setReadonly(true);
		this.zipCode.setReadonly(true);
		this.active.setDisabled(true);
		this.code.setReadonly(true);
		this.shortCode.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		// remove validation, if there are a save before

		this.dealerType.setValue("");
		this.dealerName.setValue("");
		this.dealerTelephone.setValue("");
		this.dealerFax.setValue("");
		this.dealerAddress1.setValue("");
		this.dealerAddress2.setValue("");
		this.dealerAddress3.setValue("");
		this.dealerAddress4.setValue("");
		this.dealerCountry.setValue("");
		this.dealerCity.setValue("");
		this.dealerProvince.setValue("");
		this.dealerCountry.setDescription("");
		this.dealerCity.setDescription("");
		this.dealerProvince.setDescription("");
		this.emirates.setValue("");
		this.emirates.setDescription("");
		this.email.setValue("");
		this.accountingSetId.setValue("");
		this.accountingSetId.setDescription("");
		this.commisionPaid.setValue("");
		this.commisionCalRule.setValue("");
		this.iBANnumber.setValue("");
		this.dealerPoBox.setValue("");
		this.accountNumber.setValue("");
		this.sellerType.setValue("");
		this.cityName.setText("");
		this.zipCode.setValue("");
		this.shortCode.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final VehicleDealer aVehicleDealer = new VehicleDealer();
		BeanUtils.copyProperties(getVehicleDealer(), aVehicleDealer);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the VehicleDealer object with the components data
		doWriteComponentsToBean(aVehicleDealer);
		if (aVehicleDealer.isNew()) {
			doSearchValid(aVehicleDealer);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aVehicleDealer.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVehicleDealer.getRecordType())) {
				aVehicleDealer.setVersion(aVehicleDealer.getVersion() + 1);
				if (isNew) {
					aVehicleDealer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVehicleDealer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVehicleDealer.setNewRecord(true);
				}
			}
		} else {
			aVehicleDealer.setVersion(aVehicleDealer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aVehicleDealer, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSearchValid(VehicleDealer aVehicleDealer) {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// DealerName
		try {

			boolean status = getVehicleDealerService().SearchByName(aVehicleDealer.getDealerName(),
					aVehicleDealer.getDealerType());
			if (status) {
				throw new WrongValueException(this.dealerName, "DealerName already exists...");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

	}

	private boolean doProcess(VehicleDealer aVehicleDealer, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aVehicleDealer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVehicleDealer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVehicleDealer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVehicleDealer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVehicleDealer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVehicleDealer);
				}

				if (isNotesMandatory(taskId, aVehicleDealer)) {
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

			aVehicleDealer.setTaskId(taskId);
			aVehicleDealer.setNextTaskId(nextTaskId);
			aVehicleDealer.setRoleCode(getRole());
			aVehicleDealer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aVehicleDealer, tranType);

			String operationRefs = getServiceOperations(taskId, aVehicleDealer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aVehicleDealer, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aVehicleDealer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		VehicleDealer aVehicleDealer = (VehicleDealer) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getVehicleDealerService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getVehicleDealerService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getVehicleDealerService().doApprove(auditHeader);

						if (aVehicleDealer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getVehicleDealerService().doReject(auditHeader);
						if (aVehicleDealer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_VehicleDealerDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_VehicleDealerDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.vehicleDealer), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// Changing the fields to Mandatory Based on the selection of Commisionpaid
	// fields

	public void onSelect$commisionPaid(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		checkCommisionpaid();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void checkCommisionpaid() {
		logger.debug(Literal.ENTERING);
		if (!"N".equals(this.commisionPaid.getSelectedItem().getValue().toString())
				&& !this.commisionPaid.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			this.space_PaymentMode.setSclass("mandatory");
			this.accountingSetId.setVisible(true);
			this.label_VehicleDealerDialog_AccountingTreatment.setVisible(true);
		} else {
			this.accountingSetId.setVisible(false);
			this.label_VehicleDealerDialog_AccountingTreatment.setVisible(false);
			this.space_PaymentMode.setSclass("");
		}
		logger.debug(Literal.LEAVING);
	}

	// When PaymentMode Combobobx is Selected

	public void onSelect$paymentMode(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		checkPaymentMode();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void checkPaymentMode() {
		logger.debug(Literal.ENTERING);
		if (!this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)
				&& !"N".equals(this.commisionPaid.getSelectedItem().getValue().toString())) {
			this.space_PaymentMode.setSclass("mandatory");
		}
		this.iBANnumber.setVisible(true);
		this.accountNumber.setMandatoryStyle(false);
		this.accountNumber.setVisible(false);
		this.space_IBANnumber.setSclass("");

		if (this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.AHBACCOUNT)) {
			this.iBANnumber.setVisible(false);
			this.accountNumber.setVisible(true);
			this.accountNumber.setMandatoryStyle(true);
			this.label_VehicleDealerDialog_AccountNumber
					.setValue(Labels.getLabel("label_VehicleDealerDialog_AccountNumber.value"));
			this.accountNumber.setReadonly(isReadOnly("VehicleDealerDialog_AccountNumber"));
			this.iBANnumber.setValue("");

		} else if (this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.FTS)) {
			this.iBANnumber.setReadonly(isReadOnly("VehicleDealerDialog_AccountNumber"));
			this.space_IBANnumber.setSclass("mandatory");
			this.label_VehicleDealerDialog_AccountNumber
					.setValue(Labels.getLabel("label_VehicleDealerDialog_IBANNumber.value"));
			this.accountNumber.setValue("");

		} else if (this.paymentMode.getSelectedItem().getValue().toString().equals(PennantConstants.PAYORDER)) {
			this.accountNumber.setValue("");
			this.iBANnumber.setValue("");
			this.iBANnumber.setReadonly(true);
			this.label_VehicleDealerDialog_AccountNumber
					.setValue(Labels.getLabel("label_VehicleDealerDialog_AccountNumber.value"));
		} else {
			this.iBANnumber.setValue("");
			this.iBANnumber.setReadonly(true);
			this.label_VehicleDealerDialog_AccountNumber
					.setValue(Labels.getLabel("label_VehicleDealerDialog_AccountNumber.value"));
			this.accountNumber.setValue("");
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$accountingSetId(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = accountingSetId.getObject();
		if (dataObject instanceof String) {
			this.accountingSetId.setValue(dataObject.toString());
			this.accountingSetId.setDescription("");
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.accountingSetId.setValue(details.getAccountSetCode());
				this.accountingSetId.setDescription(details.getAccountSetCodeName());
				accountid = details.getAccountSetid();
				vehicleDealer.setAccountingSetId(accountid);
			}

		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.dealerCity.setModuleName("City");
		this.dealerCity.setValueColumn("PCCity");
		this.dealerCity.setDescColumn("PCCityName");
		this.dealerCity.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters = new Filter[2];

		if (state == null) {
			filters[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		this.dealerCity.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$dealerProvince(Event event) {
		logger.debug("Entering");

		Object dataObject = dealerProvince.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			fillPindetails(null, null);
		} else {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.dealerProvince.setErrorMessage("");
				pcProvince = this.dealerProvince.getValue();
				fillPindetails(null, pcProvince);
			}
		}

		this.dealerCity.setObject("");
		this.zipCode.setObject("");
		this.dealerCity.setValue("");
		this.dealerCity.setDescription("");
		this.zipCode.setValue("");
		this.zipCode.setDescription("");
		fillCitydetails(pcProvince);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$dealerCity(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = dealerCity.getObject();

		String cityValue = null;
		if (dataObject instanceof String) {
			this.dealerCity.setValue("");
			this.dealerCity.setDescription("");
			fillPindetails(null, null);
		} else {
			City city = (City) dataObject;
			if (city != null) {
				this.dealerCity.setErrorMessage("");
				this.dealerProvince.setErrorMessage("");

				this.dealerProvince.setValue(city.getPCProvince());
				this.dealerProvince.setDescription(city.getLovDescPCProvinceName());
				cityValue = this.dealerCity.getValue();
			}
		}

		fillPindetails(cityValue, this.dealerProvince.getValue());

		this.zipCode.setObject("");
		this.zipCode.setValue("");
		this.zipCode.setDescription("");

		Filter[] filters = new Filter[2];
		filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);

		if (this.dealerProvince != null) {
			filters[0] = new Filter("PCProvince", dealerProvince.getActualValue(), Filter.OP_EQUAL);
		}

		this.dealerCity.setFilters(filters);

		logger.debug("Leaving");
	}

	public void onFulfill$zipCode(Event event) throws InterruptedException {
		logger.debug("Entering");
		Object dataObject = zipCode.getObject();
		if (dataObject instanceof String) {
			this.zipCode.setValue("");
			this.zipCode.setDescription("");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.dealerCity.setValue(pinCode.getCity());
				this.dealerCity.setDescription(pinCode.getPCCityName());
				this.dealerProvince.setValue(pinCode.getPCProvince());
				this.dealerProvince.setDescription(pinCode.getLovDescPCProvinceName());
				this.dealerCountry.setValue(pinCode.getpCCountry());
				this.dealerCountry.setDescription(pinCode.getLovDescPCCountryName());
				this.dealerCity.setErrorMessage("");
				this.dealerProvince.setErrorMessage("");
				this.zipCode.setErrorMessage("");
			}
		}

		logger.debug("Leaving");
	}

	private void fillPindetails(String cityValue, String provice) {
		logger.debug("Entering");

		this.zipCode.setModuleName("PinCode");
		this.zipCode.setValueColumn("PinCode");
		this.zipCode.setDescColumn("AreaName");
		this.zipCode.setValidateColumns(new String[] { "PinCode" });
		Filter[] filters = new Filter[2];

		if (cityValue != null) {
			filters[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if (provice != null && !provice.isEmpty()) {
			filters[0] = new Filter("PCProvince", provice, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		filters[1] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.zipCode.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$dealerCountry(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doSetDealerProvince();
		doSetDealerCity();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSetDealerProvince() {
		if (!StringUtils.trimToEmpty(sDealerCountry).equals(this.dealerCountry.getValue())) {
			this.dealerProvince.setValue("");
			this.dealerProvince.setObject("");
			this.dealerProvince.setDescription("");
			this.dealerCity.setValue("");
			this.dealerCity.setDescription("");
			this.dealerCity.setObject("");
			this.zipCode.setObject("");

		}
		sDealerCountry = this.dealerCountry.getValue();
		Filter[] filtersProvince = new Filter[1];
		filtersProvince[0] = new Filter("CPCountry", this.dealerCountry.getValue(), Filter.OP_EQUAL);
		this.dealerProvince.setFilters(filtersProvince);
	}

	private void doSetDealerCity() {
		if (!StringUtils.trimToEmpty(sDealerProvince).equals(this.dealerProvince.getValue())) {
			this.dealerCity.setObject("");
			this.dealerCity.setValue("");
			this.dealerCity.setDescription("");
		}
		sDealerProvince = this.dealerProvince.getValue();
		Filter[] filtersCity = new Filter[2];
		filtersCity[0] = new Filter("PCCountry", this.dealerCountry.getValue(), Filter.OP_EQUAL);
		filtersCity[1] = new Filter("PCProvince", this.dealerProvince.getValue(), Filter.OP_EQUAL);
		this.dealerCity.setFilters(filtersCity);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//custAddrProvince
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public VehicleDealer getVehicleDealer() {
		return this.vehicleDealer;
	}

	public void setVehicleDealer(VehicleDealer vehicleDealer) {
		this.vehicleDealer = vehicleDealer;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public VehicleDealerService getVehicleDealerService() {
		return this.vehicleDealerService;
	}

	public void setVehicleDealerListCtrl(VehicleDealerListCtrl vehicleDealerListCtrl) {
		this.vehicleDealerListCtrl = vehicleDealerListCtrl;
	}

	public VehicleDealerListCtrl getVehicleDealerListCtrl() {
		return this.vehicleDealerListCtrl;
	}

	private AuditHeader getAuditHeader(VehicleDealer aVehicleDealer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVehicleDealer.getBefImage(), aVehicleDealer);
		return new AuditHeader(String.valueOf(aVehicleDealer.getDealerId()), null, null, null, auditDetail,
				aVehicleDealer.getUserDetails(), getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_VehicleDealerDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(Literal.EXCEPTION, exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.vehicleDealer);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.dealerType.setErrorMessage("");
		this.dealerName.setErrorMessage("");
		this.dealerTelephone.setErrorMessage("");
		this.dealerFax.setErrorMessage("");
		this.dealerAddress1.setErrorMessage("");
		this.dealerAddress2.setErrorMessage("");
		this.dealerAddress3.setErrorMessage("");
		this.dealerAddress4.setErrorMessage("");
		this.dealerCountry.setErrorMessage("");
		this.dealerCity.setErrorMessage("");
		this.dealerProvince.setErrorMessage("");
		this.paymentMode.setErrorMessage("");
		this.emirates.setErrorMessage("");
		this.email.setErrorMessage("");
		this.accountingSetId.setErrorMessage("");
		this.commisionPaid.setErrorMessage("");
		this.commisionCalRule.setErrorMessage("");
		this.iBANnumber.setErrorMessage("");
		this.dealerPoBox.setErrorMessage("");
		this.sellerType.setErrorMessage("");
		this.cityName.setErrorMessage("");
		this.zipCode.setErrorMessage("");
		this.shortCode.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getVehicleDealerListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.vehicleDealer.getDealerId());
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}
}
