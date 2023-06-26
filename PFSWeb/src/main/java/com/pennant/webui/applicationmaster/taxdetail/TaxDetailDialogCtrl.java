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
 * * FileName : TaxDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-06-2017 * * Modified
 * Date : 14-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.taxdetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.financetaxdetail.GSTINInfo;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.TaxDetailService;
import com.pennant.backend.service.gstn.validation.GSTNValidationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.systemmasters.province.ProvinceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/TaxDetail/taxDetailDialog.zul file. <br>
 */
public class TaxDetailDialogCtrl extends GFCBaseCtrl<TaxDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TaxDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TaxDetailDialog;
	protected ExtendedCombobox country;
	protected ExtendedCombobox stateCode;
	protected ExtendedCombobox entityCode;
	protected Textbox gstStateCode;
	protected Textbox panNumber;
	protected Textbox taxCode;
	protected Textbox addressLine1;
	protected Textbox addressLine2;
	protected Textbox addressLine3;
	protected Textbox addressLine4;
	protected ExtendedCombobox pinCode;
	protected ExtendedCombobox cityCode;
	protected Textbox hSNNumber;
	protected Textbox natureService;
	protected Space space_HSNNumber;
	protected Space space_NatureService;

	private TaxDetail taxDetail;
	private transient TaxDetailListCtrl taxDetailListCtrl;
	private transient TaxDetailService taxDetailService;
	private transient ProvinceDialogCtrl provinceDialogCtrl;
	private List<TaxDetail> listTaxDetails;
	private boolean fromProvince = false;
	private String taxStateCode = "";
	private String old_country = "";
	private String old_state = "";
	private String old_city = "";

	private GSTNValidationService gstnValidationService;

	/**
	 * default constructor.<br>
	 */
	public TaxDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TaxDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.taxDetail.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TaxDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TaxDetailDialog);

		try {
			// Get the required arguments.
			this.taxDetail = (TaxDetail) arguments.get("taxdetail");

			if (this.taxDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("taxdetailListCtrl")) {
				this.taxDetailListCtrl = (TaxDetailListCtrl) arguments.get("taxdetailListCtrl");
			}

			// Store the before image.
			TaxDetail taxDetail = new TaxDetail();
			BeanUtils.copyProperties(this.taxDetail, taxDetail);
			this.taxDetail.setBefImage(taxDetail);

			if (arguments.containsKey("provinceDialogCtrl")) {
				this.provinceDialogCtrl = (ProvinceDialogCtrl) arguments.get("provinceDialogCtrl");
				fromProvince = true;
				this.taxDetail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole(arguments.get("roleCode").toString());
					getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
				}
				if (arguments.containsKey("taxStateCode")) {
					taxStateCode = (String) arguments.get("taxStateCode");
				}
			}

			// Render the page and display the data.
			doLoadWorkFlow(this.taxDetail.isWorkflow(), this.taxDetail.getWorkflowId(), this.taxDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.taxDetail);
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

		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] { "CountryCode" });

		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[] { "CPProvince" });

		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValueType(DataType.LONG);
		this.pinCode.setInputAllowed(false);
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });

		this.cityCode.setMandatoryStyle(true);
		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] { "PCCity" });

		this.entityCode.setMaxlength(8);
		this.pinCode.setMaxlength(10);

		this.gstStateCode.setMaxlength(2);
		this.panNumber.setMaxlength(10);
		this.taxCode.setMaxlength(3);
		this.gstStateCode.setReadonly(true);
		this.panNumber.setReadonly(true);

		this.cityCode.setMaxlength(8);
		this.country.setMaxlength(8);
		this.stateCode.setMaxlength(8);
		this.addressLine1.setMaxlength(100);
		this.addressLine2.setMaxlength(100);
		this.addressLine3.setMaxlength(100);
		this.addressLine4.setMaxlength(100);
		this.hSNNumber.setMaxlength(100);
		this.natureService.setMaxlength(100);
		this.space_HSNNumber.setSclass(PennantConstants.mandateSclass);
		this.space_NatureService.setSclass(PennantConstants.mandateSclass);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doSave();

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$country(Event event) {
		logger.debug("Entering");

		Object dataObject = country.getObject();
		doClearMessage();

		if (dataObject instanceof String) {
			this.country.setValue("", "");
			this.stateCode.setValue("", "");
			this.cityCode.setValue("", "");
			this.pinCode.setValue("", "");
			filterProvinceDetails(null);
			filterCitydetails(null, null);
			filterPindetails(null, null, null);
			old_country = "";
			old_state = "";
			old_city = "";
		} else {
			Country country = (Country) dataObject;
			if (country != null) {
				if (!StringUtils.equals(old_country, country.getCountryCode())) {
					this.stateCode.setValue("", "");
					this.cityCode.setValue("", "");
					this.pinCode.setValue("", "");
					this.stateCode.setFilters(null);
					this.cityCode.setFilters(null);
					this.pinCode.setFilters(null);
					old_state = "";
					old_city = "";

					filterCitydetails(null, country.getCountryCode());
					filterPindetails(null, null, country.getCountryCode());
				}
				filterProvinceDetails(country.getCountryCode());
				old_country = country.getCountryCode();
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$stateCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = stateCode.getObject();
		doClearMessage();

		this.gstStateCode.setValue("");
		if (dataObject instanceof String) {
			this.stateCode.setValue("", "");
			this.cityCode.setValue("", "");
			this.pinCode.setValue("", "");
			filterCitydetails(null, old_country);
			filterPindetails(null, null, old_country);
			old_state = "";
			old_city = "";
		} else {
			Province province = (Province) dataObject;
			if (province != null) {
				if (!StringUtils.equals(old_state, this.stateCode.getValue())) {
					this.cityCode.setValue("", "");
					this.pinCode.setValue("", "");
					this.cityCode.setFilters(null);
					this.pinCode.setFilters(null);
					old_city = "";

					filterCitydetails(this.stateCode.getValue(), null);
					filterPindetails(null, this.stateCode.getValue(), null);
				}

				String taxStateCode = province.getTaxStateCode() == null ? "" : province.getTaxStateCode();
				this.gstStateCode.setValue(taxStateCode);

				if (StringUtils.isBlank(this.country.getValue())) {
					this.country.setValue(province.getCPCountry(), province.getLovDescCPCountryName());
					this.old_country = province.getCPCountry();
				}
				this.old_state = this.stateCode.getValue();
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$cityCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = cityCode.getObject();
		doClearMessage();

		if (dataObject instanceof String) {
			this.cityCode.setValue("", "");
			this.pinCode.setValue("", "");
			filterPindetails(null, this.old_state, this.old_country);
			old_city = "";
		} else {
			City city = (City) dataObject;
			if (city != null) {
				this.pinCode.setFilters(null);
				filterPindetails(this.cityCode.getValue(), null, null);

				if (!StringUtils.equals(old_city, this.cityCode.getValue())) {
					this.pinCode.setValue("", "");
				}

				String citytaxStateCode = city.getTaxStateCode() == null ? "" : city.getTaxStateCode();
				this.gstStateCode.setValue(citytaxStateCode);

				if (StringUtils.isBlank(this.country.getValue())) {
					this.country.setValue(city.getPCCountry(), city.getLovDescPCCountryName());
					this.old_country = city.getPCCountry();
				}

				if (StringUtils.isBlank(this.stateCode.getValue())) {
					this.stateCode.setValue(city.getPCProvince(), city.getLovDescPCProvinceName());
					filterProvinceDetails(city.getPCCountry());
					this.old_state = city.getPCProvince();
					String gstInValue = city.getTaxStateCode() == null ? "" : city.getTaxStateCode();
					this.gstStateCode.setValue(gstInValue);
				}

				this.old_city = this.cityCode.getValue();
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
		doClearMessage();

		if (dataObject instanceof String) {
			this.pinCode.setValue("", "");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.pinCode.setAttribute("pinCodeId", pinCode.getPinCodeId());
				this.pinCode.setValue(pinCode.getPinCode());
				if (StringUtils.isBlank(this.country.getValue())) {
					this.country.setValue(pinCode.getpCCountry(), pinCode.getLovDescPCCountryName());
					this.old_country = pinCode.getpCCountry();
				}

				if (StringUtils.isBlank(this.stateCode.getValue())) {
					this.stateCode.setValue(pinCode.getPCProvince(), pinCode.getLovDescPCProvinceName());
					String gstInValue = pinCode.getGstin() == null ? "" : pinCode.getGstin();
					this.gstStateCode.setValue(gstInValue);
					filterProvinceDetails(pinCode.getpCCountry());
					this.old_state = pinCode.getPCProvince();
				}

				if (StringUtils.isBlank(this.cityCode.getValue())) {
					this.cityCode.setValue(pinCode.getCity(), pinCode.getPCCityName());
					filterCitydetails(pinCode.getPCProvince(), pinCode.getpCCountry());
					this.old_city = pinCode.getCity();
				}
			}
		}

		logger.debug("Leaving");
	}

	private void filterProvinceDetails(String country) {
		logger.debug("Entering");

		Filter[] provinceFilter = null;
		if (StringUtils.isNotBlank(country)) {
			provinceFilter = new Filter[1];
			provinceFilter[0] = new Filter("CpCountry", country, Filter.OP_EQUAL);
		}

		this.stateCode.setFilters(provinceFilter);

		logger.debug("Leaving");
	}

	private void filterCitydetails(String state, String country) {
		logger.debug("Entering");

		Filter[] cityFilter = null;
		if (StringUtils.isNotBlank(state)) {
			cityFilter = new Filter[1];
			cityFilter[0] = new Filter("PcProvince", state, Filter.OP_EQUAL);
		} else if (StringUtils.isNotBlank(country)) {
			cityFilter = new Filter[1];
			cityFilter[0] = new Filter("PcCountry", country, Filter.OP_EQUAL);
		}

		this.cityCode.setFilters(cityFilter);

		logger.debug("Leaving");
	}

	private void filterPindetails(String cityValue, String state, String country) {
		logger.debug("Entering");

		Filter[] pinFilter = null;
		if (StringUtils.isNotBlank(cityValue)) {
			pinFilter = new Filter[1];
			pinFilter[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if (StringUtils.isNotBlank(state)) {
			pinFilter = new Filter[1];
			pinFilter[0] = new Filter("PcProvince", state, Filter.OP_EQUAL);
		} else if (StringUtils.isNotBlank(country)) {
			pinFilter = new Filter[1];
			pinFilter[0] = new Filter("PcCountry", country, Filter.OP_EQUAL);
		}

		this.pinCode.setFilters(pinFilter);

		logger.debug("Leaving");
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$entityCode(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Object dataObject = entityCode.getObject();
		this.taxCode.setErrorMessage("");
		this.panNumber.setValue("");

		if (dataObject instanceof String) {
			this.entityCode.setValue("", "");
		} else {
			Entity entity = (Entity) dataObject;
			if (entity != null) {
				this.panNumber.setValue(entity.getPANNumber());
			}
		}

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

		doShowNotes(this.taxDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);

		taxDetailListCtrl.search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.taxDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param taxDetail
	 * 
	 */
	public void doWriteBeanToComponents(TaxDetail aTaxDetail) {
		logger.debug(Literal.ENTERING);

		this.addressLine1.setValue(aTaxDetail.getAddressLine1());
		this.addressLine2.setValue(aTaxDetail.getAddressLine2());
		this.addressLine3.setValue(aTaxDetail.getAddressLine3());
		this.addressLine4.setValue(aTaxDetail.getAddressLine4());
		this.hSNNumber.setValue(aTaxDetail.getHsnNumber());
		this.natureService.setValue(aTaxDetail.getNatureService());

		this.country.setValue(aTaxDetail.getCountry(), aTaxDetail.getCountryName());
		this.stateCode.setValue(aTaxDetail.getStateCode(), aTaxDetail.getProvinceName());
		this.cityCode.setValue(aTaxDetail.getCityCode(), aTaxDetail.getCityName());

		if (aTaxDetail.getPinCodeId() != null) {
			this.pinCode.setAttribute("pinCodeId", aTaxDetail.getPinCodeId());
		} else {
			this.pinCode.setAttribute("pinCodeId", null);
		}

		this.pinCode.setValue(aTaxDetail.getPinCode(), aTaxDetail.getAreaName());
		this.entityCode.setValue(aTaxDetail.getEntityCode(), aTaxDetail.getEntityDesc());

		old_country = aTaxDetail.getCountry();
		old_state = aTaxDetail.getStateCode();
		old_city = aTaxDetail.getCityCode();

		if (StringUtils.isNotBlank(aTaxDetail.getTaxCode()) && aTaxDetail.getTaxCode().length() == 15) {
			this.gstStateCode.setValue(aTaxDetail.getTaxCode().substring(0, 2));
			this.panNumber.setValue(aTaxDetail.getTaxCode().substring(2, 12));
			this.taxCode.setValue(aTaxDetail.getTaxCode().substring(12));
		}
		if (StringUtils.isNotBlank(taxStateCode)) {
			this.gstStateCode.setValue(taxStateCode);
		}

		filterProvinceDetails(aTaxDetail.getCountry());
		filterCitydetails(aTaxDetail.getStateCode(), aTaxDetail.getCountry());
		filterPindetails(aTaxDetail.getCityCode(), aTaxDetail.getStateCode(), aTaxDetail.getCountry());

		this.recordStatus.setValue(aTaxDetail.getRecordStatus());

		this.hSNNumber.setValue(aTaxDetail.getHsnNumber());
		this.natureService.setValue(aTaxDetail.getNatureService());

		if (fromProvince) {
			this.country.setReadonly(true);
			this.stateCode.setReadonly(true);

			if ((StringUtils.isNotBlank(this.taxDetail.getRecordStatus())
					|| StringUtils.isNotBlank(this.taxDetail.getRecordType())) && !enqiryModule) {
				this.btnDelete.setVisible(true);
			}
		}

		if (!aTaxDetail.isNewRecord()) {
			ArrayList<Filter> filters = new ArrayList<Filter>();

			if (this.country.getValue() != null && !this.country.getValue().isEmpty()) {
				Filter filterPin0 = new Filter("PCCountry", this.country.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin0);
			}

			if (this.stateCode.getValue() != null && !this.stateCode.getValue().isEmpty()) {
				Filter filterPin1 = new Filter("PCProvince", this.stateCode.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin1);
			}

			if (this.cityCode.getValue() != null && !this.cityCode.getValue().isEmpty()) {
				Filter filterPin2 = new Filter("City", this.cityCode.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin2);
			}

			Filter[] filterPin = new Filter[filters.size()];
			for (int i = 0; i < filters.size(); i++) {
				filterPin[i] = filters.get(i);
			}
			this.pinCode.setFilters(filterPin);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTaxDetail
	 */
	public void doWriteComponentsToBean(TaxDetail aTaxDetail) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Country
		try {
			aTaxDetail.setCountry(this.country.getValidatedValue());
			aTaxDetail.setCountryName(this.country.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// State Code
		String stateCodeValue = null;
		String entityCodeValue = null;
		try {
			stateCodeValue = this.stateCode.getValidatedValue();
			aTaxDetail.setStateCode(stateCodeValue);
			aTaxDetail.setProvinceName(this.stateCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Entity Code
		try {
			entityCodeValue = this.entityCode.getValidatedValue();
			aTaxDetail.setEntityCode(entityCodeValue);
			aTaxDetail.setEntityDesc(this.entityCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tax Code
		try {
			String taxCodeValue = this.taxCode.getValue();

			if (StringUtils.isNotBlank(stateCodeValue) && StringUtils.isNotBlank(entityCodeValue)) {
				String gstStateCodeValue = this.gstStateCode.getValue();
				String panNumberValue = this.panNumber.getValue();

				if (StringUtils.isBlank(gstStateCodeValue)) {
					throw new WrongValueException(this.taxCode, Labels.getLabel("label_TaxDetailDialog_StateCode.value")
							+ " should not contain " + Labels.getLabel("label_ProvinceDialog_TaxStateCode.value"));
				} else if (StringUtils.isBlank(panNumberValue)) {
					throw new WrongValueException(this.taxCode,
							Labels.getLabel("label_TaxDetailDialog_EntityCode.value") + " should not contain "
									+ Labels.getLabel("label_EntityDialog_PANNumber.value"));
				}

				aTaxDetail.setTaxCode(gstStateCodeValue.concat(panNumberValue).concat(taxCodeValue));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 1
		try {
			aTaxDetail.setAddressLine1(this.addressLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 2
		try {
			aTaxDetail.setAddressLine2(this.addressLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 3
		try {
			aTaxDetail.setAddressLine3(this.addressLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 4
		try {
			aTaxDetail.setAddressLine4(this.addressLine4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Pin Code
		try {
			Object obj = this.pinCode.getAttribute("pinCodeId");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aTaxDetail.setPinCodeId(Long.valueOf((obj.toString())));
				}
			} else {
				aTaxDetail.setPinCodeId(null);
			}
			aTaxDetail.setPinCode(this.pinCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// City Code
		try {
			aTaxDetail.setCityCode(this.cityCode.getValidatedValue());
			aTaxDetail.setCityName(this.cityCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// HSN Number
		try {
			aTaxDetail.setHsnNumber(this.hSNNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Nature Service
		try {
			aTaxDetail.setNatureService(this.natureService.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param taxDetail The entity that need to be render.
	 */
	public void doShowDialog(TaxDetail taxDetail) {
		logger.debug(Literal.LEAVING);

		if (taxDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.country.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(taxDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.country.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				if (fromProvince && !enqiryModule) {
					doEdit();
				}
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(taxDetail);

		if (fromProvince) {
			this.window_TaxDetailDialog.setHeight("80%");
			this.window_TaxDetailDialog.setWidth("80%");
			this.groupboxWf.setVisible(false);
			this.window_TaxDetailDialog.doModal();
		} else {
			this.window_TaxDetailDialog.setWidth("100%");
			this.window_TaxDetailDialog.setHeight("100%");
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.country.isReadonly()) {
			this.country.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_Country.value"), null, true, true));
		}
		if (!this.stateCode.isReadonly()) {
			this.stateCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_StateCode.value"), null, true, true));
		}
		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_EntityCode.value"), null, true, true));
		}
		if (!this.taxCode.isReadonly()) {
			this.taxCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_TaxCode.value"),
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_FL3, true, 3));
		}
		if (!this.addressLine1.isReadonly()) {
			this.addressLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.addressLine2.isReadonly()) {
			this.addressLine2
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.addressLine3.isReadonly()) {
			this.addressLine3
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine3.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.addressLine4.isReadonly()) {
			this.addressLine4
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine4.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (this.pinCode.isButtonVisible()) {
			this.pinCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_PinCode.value"), null, true, true));
		}
		if (!this.cityCode.isReadonly()) {
			this.cityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_CityCode.value"), null, true, true));
		}

		if (!this.hSNNumber.isReadonly()) {
			this.hSNNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_HSNNumber.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.natureService.isReadonly()) {
			this.natureService.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_NatureService.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.country.setConstraint("");
		this.stateCode.setConstraint("");
		this.entityCode.setConstraint("");
		this.taxCode.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.addressLine4.setConstraint("");
		this.pinCode.setConstraint("");
		this.cityCode.setConstraint("");
		this.hSNNumber.setConstraint("");
		this.natureService.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

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

		this.country.setErrorMessage("");
		this.stateCode.setErrorMessage("");
		this.entityCode.setErrorMessage("");
		this.taxCode.setErrorMessage("");
		this.addressLine1.setErrorMessage("");
		this.addressLine2.setErrorMessage("");
		this.addressLine3.setErrorMessage("");
		this.addressLine4.setErrorMessage("");
		this.pinCode.setErrorMessage("");
		this.cityCode.setErrorMessage("");
		this.hSNNumber.setErrorMessage("");
		this.natureService.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final TaxDetail aTaxDetail = new TaxDetail();
		BeanUtils.copyProperties(this.taxDetail, aTaxDetail);

		if (fromProvince) {
			doDelete("", aTaxDetail);
		} else {
			doDelete(String.valueOf(aTaxDetail.getId()), aTaxDetail);
		}

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final TaxDetail aTaxDetail) {
		String tranType = PennantConstants.TRAN_WF;
		if (fromProvince) {
			aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);

			if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
				aTaxDetail.setNewRecord(true);
			}

			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newTaxDetailProcess(aTaxDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_TaxDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getProvinceDialogCtrl().doFillGSTINMappingDetails(this.listTaxDetails);
				// send the data back to customer
				closeDialog();
			}
		} else {
			try {
				if (StringUtils.isBlank(aTaxDetail.getRecordType())) {

					aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
					aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

					if (isWorkFlowEnabled()) {
						aTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						aTaxDetail.setNewRecord(true);
						tranType = PennantConstants.TRAN_WF;
						getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTaxDetail.getNextTaskId(),
								aTaxDetail);
					} else {
						tranType = PennantConstants.TRAN_DEL;
					}
				} else if (PennantConstants.RCD_UPD.equals(aTaxDetail.getRecordType())) {
					aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
					aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
				if (doProcess(aTaxDetail, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.taxDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.taxCode);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("TaxDetailDialog_TaxCode"), this.taxCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_Country"), this.country);
		readOnlyComponent(isReadOnly("TaxDetailDialog_StateCode"), this.stateCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_EntityCode"), this.entityCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine1"), this.addressLine1);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine2"), this.addressLine2);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine3"), this.addressLine3);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine4"), this.addressLine4);
		readOnlyComponent(isReadOnly("TaxDetailDialog_PinCode"), this.pinCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_CityCode"), this.cityCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_CityCode"), this.hSNNumber); // TODO
																					// create
																					// rights
		readOnlyComponent(isReadOnly("TaxDetailDialog_CityCode"), this.natureService);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.taxDetail.isNewRecord()) {
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

		readOnlyComponent(true, this.country);
		readOnlyComponent(true, this.stateCode);
		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.taxCode);
		readOnlyComponent(true, this.addressLine1);
		readOnlyComponent(true, this.addressLine2);
		readOnlyComponent(true, this.addressLine3);
		readOnlyComponent(true, this.addressLine4);
		readOnlyComponent(true, this.pinCode);
		readOnlyComponent(true, this.cityCode);
		readOnlyComponent(true, this.hSNNumber);
		readOnlyComponent(true, this.natureService);

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
		this.country.setValue("");
		this.country.setDescription("");
		this.stateCode.setValue("");
		this.stateCode.setDescription("");
		this.entityCode.setValue("");
		this.taxCode.setValue("");
		this.addressLine1.setValue("");
		this.addressLine2.setValue("");
		this.addressLine3.setValue("");
		this.addressLine4.setValue("");
		this.pinCode.setValue("");
		this.cityCode.setValue("");
		this.cityCode.setDescription("");
		this.hSNNumber.setValue("");
		this.natureService.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final TaxDetail aTaxDetail = new TaxDetail();
		BeanUtils.copyProperties(this.taxDetail, aTaxDetail);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aTaxDetail);

		// GSTIN Validation
		String gSTNNumber = aTaxDetail.getTaxCode();
		if (!this.taxCode.isReadonly() && (!StringUtils.equals(gSTNNumber, aTaxDetail.getBefImage().getTaxCode()))) {
			try {
				GSTINInfo gstinInfo = new GSTINInfo();
				gstinInfo.setgSTNNumber(gSTNNumber);
				gstinInfo.setUsrID(getUserWorkspace().getUserId());

				gstinInfo = this.gstnValidationService.validateGSTNNumber(gstinInfo);

				if (null != gstinInfo) {
					StringBuilder msg = new StringBuilder();
					msg.append(gstinInfo.getStatusCode()).append("_").append(gstinInfo.getStatusDesc());
					msg.append("\n").append(" GSTIN :").append(gstinInfo.getgSTNNumber());
					msg.append("\n").append(" GSTIN Date :").append(gstinInfo.getRegisterDateStr());
					msg.append("\n").append(" Name :").append(gstinInfo.getLegelName());
					msg.append("\n").append(" Type Of Ownership :").append(gstinInfo.getCxdt());

					if (MessageUtil.confirm(msg.toString(),
							MessageUtil.CANCEL | MessageUtil.OK) == MessageUtil.CANCEL) {
						return;
					}
				}
			} catch (InterfaceException e) {
				if (MessageUtil.confirm(e.getErrorCode() + " - " + e.getErrorMessage(),
						MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
					return;
				}
			}
		}
		isNew = aTaxDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
				aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
				if (isNew) {
					aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTaxDetail.setNewRecord(true);
				}
			}
		} else {
			if (fromProvince) {
				if (isNew) {
					aTaxDetail.setVersion(1);
					aTaxDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
					aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
					aTaxDetail.setRecordType(PennantConstants.RCD_UPD);
				}
			} else {
				aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
			}

			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (fromProvince) {
				AuditHeader auditHeader = newTaxDetailProcess(aTaxDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_TaxDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getProvinceDialogCtrl().doFillGSTINMappingDetails(this.listTaxDetails);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(aTaxDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	private AuditHeader newTaxDetailProcess(TaxDetail aTaxDetail, String tranType) {
		logger.debug("Entering");

		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aTaxDetail, tranType);
		this.listTaxDetails = new ArrayList<TaxDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aTaxDetail.getProvinceName();
		valueParm[1] = aTaxDetail.getEntityDesc();

		errParm[0] = PennantJavaUtil.getLabel("label_TaxDetailDialog_StateCode.value") + ":" + valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_TaxDetailDialog_EntityCode.value") + ":" + valueParm[1];

		if (getProvinceDialogCtrl().getTaxDetailList() != null
				&& (getProvinceDialogCtrl().getTaxDetailList().size() > 0)) {

			for (int i = 0; i < getProvinceDialogCtrl().getTaxDetailList().size(); i++) {

				TaxDetail taxDetail = getProvinceDialogCtrl().getTaxDetailList().get(i);

				if (aTaxDetail.getStateCode().equals(taxDetail.getStateCode())
						&& aTaxDetail.getEntityCode().equals(taxDetail.getEntityCode())) { // Both
																							// Current
																							// and
																							// Existing
																							// list
																							// addresses
																							// same

					if (aTaxDetail.isNewRecord() && !PennantConstants.TRAN_DEL.equals(tranType)) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aTaxDetail.getRecordType())) {
							aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.listTaxDetails.add(aTaxDetail);
						} else if (PennantConstants.RCD_ADD.equals(aTaxDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aTaxDetail.getRecordType())) {
							aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.listTaxDetails.add(aTaxDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aTaxDetail.getRecordType())) {
							recordAdded = true;

							for (int j = 0; j < getProvinceDialogCtrl().getTaxDetailList().size(); j++) {
								TaxDetail detail = getProvinceDialogCtrl().getTaxDetailList().get(j);
								if (aTaxDetail.getStateCode().equals(taxDetail.getStateCode())
										&& aTaxDetail.getEntityCode().equals(taxDetail.getEntityCode())) {
									this.listTaxDetails.add(detail);
								}
							}
						} else if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
							aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.listTaxDetails.add(aTaxDetail);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.listTaxDetails.add(taxDetail);
						}
					}
				} else if (aTaxDetail.getTaxCode().equals(taxDetail.getTaxCode())
						&& !PennantConstants.TRAN_DEL.equals(tranType)) {

					valueParm = new String[1];
					errParm = new String[1];

					valueParm[0] = aTaxDetail.getTaxCode();
					errParm[0] = PennantJavaUtil.getLabel("label_TaxDetailDialog_TaxCode.value") + ":" + valueParm[0];

					auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
							getUserWorkspace().getUserLanguage()));
					return auditHeader;
				} else {
					this.listTaxDetails.add(taxDetail);
				}
			}
		}

		if (!recordAdded) {
			this.listTaxDetails.add(aTaxDetail);
		}

		logger.debug("Leaving");

		return auditHeader;
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
	protected boolean doProcess(TaxDetail aTaxDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aTaxDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTaxDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aTaxDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aTaxDetail);
				}

				if (isNotesMandatory(taskId, aTaxDetail)) {
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

			aTaxDetail.setTaskId(taskId);
			aTaxDetail.setNextTaskId(nextTaskId);
			aTaxDetail.setRoleCode(getRole());
			aTaxDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aTaxDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aTaxDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aTaxDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aTaxDetail, tranType);
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		TaxDetail aTaxDetail = (TaxDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = taxDetailService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = taxDetailService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = taxDetailService.doApprove(auditHeader);

					if (aTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = taxDetailService.doReject(auditHeader);
					if (aTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_TaxDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_TaxDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.taxDetail), true);
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

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TaxDetail aTaxDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTaxDetail.getBefImage(), aTaxDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aTaxDetail.getUserDetails(),
				getOverideMap());
	}

	public void setTaxDetailService(TaxDetailService taxDetailService) {
		this.taxDetailService = taxDetailService;
	}

	public List<TaxDetail> getListTaxDetails() {
		return listTaxDetails;
	}

	public void setListTaxDetails(List<TaxDetail> listTaxDetails) {
		this.listTaxDetails = listTaxDetails;
	}

	public ProvinceDialogCtrl getProvinceDialogCtrl() {
		return provinceDialogCtrl;
	}

	public void setProvinceDialogCtrl(ProvinceDialogCtrl provinceDialogCtrl) {
		this.provinceDialogCtrl = provinceDialogCtrl;
	}

	public void setGstnValidationService(GSTNValidationService gstnValidationService) {
		this.gstnValidationService = gstnValidationService;
	}

}
