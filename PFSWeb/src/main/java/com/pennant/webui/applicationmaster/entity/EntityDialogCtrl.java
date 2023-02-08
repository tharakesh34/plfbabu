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
 * * FileName : EntityDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-06-2017 * * Modified
 * Date : 15-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.entity;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/Entity/entityDialog.zul file. <br>
 */
public class EntityDialogCtrl extends GFCBaseCtrl<Entity> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(EntityDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EntityDialog;
	protected Textbox entityCode;
	protected Textbox entityDesc;
	protected Uppercasebox pANNumber;
	protected ExtendedCombobox country;
	protected ExtendedCombobox stateCode;
	protected ExtendedCombobox cityCode;
	protected ExtendedCombobox pinCode;
	protected Checkbox gstinAvailable;
	protected Checkbox active;
	private Entity entity; // overhanded per param
	protected Textbox entityAddrLine1;
	protected Textbox entityAddrLine2;
	protected Textbox entityAddrHNbr;
	protected Textbox entityFlatNbr;
	protected Textbox entityAddrStreet;
	protected Textbox entityPOBox;

	private transient EntityListCtrl entityListCtrl; // overhanded per param
	private transient EntityService entityService;

	// Adding the CIN NO Field
	private Uppercasebox cINNumber;

	/**
	 * default constructor.<br>
	 */
	public EntityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EntityDialog";
	}

	@Override
	protected String getReference() {
		return this.entity.getEntityCode();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_EntityDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_EntityDialog);

		try {
			// Get the required arguments.
			this.entity = (Entity) arguments.get("entity");
			this.entityListCtrl = (EntityListCtrl) arguments.get("entityListCtrl");

			if (this.entity == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Entity entity = new Entity();
			BeanUtils.copyProperties(this.entity, entity);
			this.entity.setBefImage(entity);

			// Render the page and display the data.
			doLoadWorkFlow(this.entity.isWorkflow(), this.entity.getWorkflowId(), this.entity.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.entity);
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
		this.country.setTextBoxWidth(180);

		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[] { "CPProvince" });
		this.stateCode.setTextBoxWidth(180);

		this.cityCode.setMandatoryStyle(true);
		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] { "PCCity" });
		this.cityCode.setTextBoxWidth(180);

		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValueType(DataType.LONG);
		this.pinCode.setInputAllowed(false);
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });
		this.pinCode.setTextBoxWidth(180);
		this.pinCode.setTextBoxWidth(180);

		this.entityCode.setMaxlength(8);
		this.entityDesc.setMaxlength(50);
		this.pANNumber.setMaxlength(10);
		this.pinCode.setMaxlength(10);
		this.stateCode.setMaxlength(8);
		this.entityAddrLine1.setMaxlength(50);
		this.entityAddrLine2.setMaxlength(50);
		this.entityAddrHNbr.setMaxlength(50);
		this.entityFlatNbr.setMaxlength(50);
		this.entityAddrStreet.setMaxlength(50);
		this.entityPOBox.setMaxlength(8);

		this.cINNumber.setMaxlength(21);

		setStatusDetails();

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$country(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = country.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			this.stateCode.setValue("");
			this.stateCode.setDescription("");
			this.cityCode.setValue("");
			this.cityCode.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Country country = (Country) dataObject;
			if (country == null) {
				fillProvinceDetails(null);
			}
			if (country != null) {
				this.stateCode.setErrorMessage("");
				pcProvince = country.getCountryCode();
				fillProvinceDetails(pcProvince);
			} else {
				this.stateCode.setObject("");
				this.cityCode.setObject("");
				this.pinCode.setObject("");
				this.stateCode.setValue("");
				this.stateCode.setDescription("");
				this.cityCode.setValue("");
				this.cityCode.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}
			fillPindetails(null, null);
		}
		logger.debug("Leaving" + event.toString());
	}

	private void fillProvinceDetails(String country) {
		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[] { "CPProvince" });

		Filter[] filters1 = new Filter[1];

		if (country == null || country.equals("")) {
			filters1[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.stateCode.setFilters(filters1);
	}

	public void onFulfill$stateCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = stateCode.getObject();
		String pcProvince = this.stateCode.getValue();
		if (dataObject instanceof String) {
			this.cityCode.setValue("");
			this.cityCode.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.stateCode.setErrorMessage("");
				pcProvince = this.stateCode.getValue();
				this.country.setValue(province.getCPCountry());
				this.country.setDescription(province.getLovDescCPCountryName());
				this.cityCode.setValue("");
				this.cityCode.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				fillPindetails(null, pcProvince);
			} else {
				this.cityCode.setObject("");
				this.pinCode.setObject("");
				this.cityCode.setValue("");
				this.cityCode.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}
		}
		fillCitydetails(pcProvince);
		logger.debug("Leaving" + event.toString());
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[1];

		if (state == null || state.isEmpty()) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		this.cityCode.setFilters(filters1);
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$cityCode(Event event) throws InterruptedException {
		logger.debug("Entering");
		doRemoveValidation();
		doClearMessage();
		Object dataObject = cityCode.getObject();
		String cityValue = null;
		if (!(dataObject instanceof String)) {
			City details = (City) dataObject;
			if (details == null) {
				fillPindetails(null, null);
			}
			if (details != null) {
				this.stateCode.setValue(details.getPCProvince());
				this.stateCode.setDescription(details.getLovDescPCProvinceName());
				this.country.setValue(details.getPCCountry());
				this.country.setDescription(details.getLovDescPCCountryName());
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				cityValue = details.getPCCity();
				fillPindetails(cityValue, this.stateCode.getValue());
			} else {
				this.cityCode.setObject("");
				this.pinCode.setObject("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				this.stateCode.setErrorMessage("");
				this.country.setErrorMessage("");
				fillPindetails(null, this.stateCode.getValue());
			}
		} else if ("".equals(dataObject)) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			this.stateCode.setObject("");
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

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
		if (dataObject instanceof String) {

		} else {
			PinCode details = (PinCode) dataObject;

			if (details != null) {
				this.country.setValue(details.getpCCountry());
				this.country.setDescription(details.getLovDescPCCountryName());
				this.cityCode.setValue(details.getCity());
				this.cityCode.setDescription(details.getPCCityName());
				this.stateCode.setValue(details.getPCProvince());
				this.stateCode.setDescription(details.getLovDescPCProvinceName());
				this.cityCode.setErrorMessage("");
				this.stateCode.setErrorMessage("");
				this.country.setErrorMessage("");
				this.pinCode.setAttribute("pinCodeId", details.getPinCodeId());
				this.pinCode.setValue(details.getPinCode());
			}

		}
		Filter[] filters1 = new Filter[1];
		if (this.cityCode.getValue() != null && !this.cityCode.getValue().isEmpty()) {
			filters1[0] = new Filter("City", this.cityCode.getValue(), Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);

		logger.debug("Leaving");
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
		doShowNotes(this.entity);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		entityListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.entity.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param entity
	 * 
	 */
	public void doWriteBeanToComponents(Entity aEntity) {
		logger.debug(Literal.ENTERING);

		this.entityCode.setValue(aEntity.getEntityCode());
		this.entityDesc.setValue(aEntity.getEntityDesc());
		this.pANNumber.setValue(aEntity.getPANNumber());
		this.country.setValue(aEntity.getCountry());
		this.stateCode.setValue(aEntity.getStateCode());
		this.cityCode.setValue(aEntity.getCityCode());

		if (aEntity.getPinCodeId() != null) {
			this.pinCode.setAttribute("pinCodeId", aEntity.getPinCodeId());
		} else {
			this.pinCode.setAttribute("pinCodeId", null);
		}
		this.pinCode.setValue(aEntity.getPinCode(), aEntity.getPinCodeName());
		this.active.setChecked(aEntity.isActive());
		this.gstinAvailable.setChecked(aEntity.isGstinAvailable());
		this.entityAddrLine1.setValue(aEntity.getEntityAddrLine1());
		this.entityAddrLine2.setValue(aEntity.getEntityAddrLine2());
		this.entityAddrHNbr.setValue(aEntity.getEntityAddrHNbr());
		this.entityFlatNbr.setValue(aEntity.getEntityFlatNbr());
		this.entityAddrStreet.setValue(aEntity.getEntityAddrStreet());
		this.entityPOBox.setValue(aEntity.getEntityPOBox());
		if (aEntity.isNewRecord()) {
			this.country.setDescription("");
			this.stateCode.setDescription("");
			this.cityCode.setDescription("");
			this.pinCode.setDescription("");
		} else {
			this.country.setDescription(aEntity.getCountryName());
			this.stateCode.setDescription(aEntity.getProvinceName());
			this.cityCode.setDescription(aEntity.getCityName());
			this.pinCode.setDescription(aEntity.getPinCodeName());
		}

		if (aEntity.isNewRecord() || (aEntity.getRecordType() != null ? aEntity.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		this.cINNumber.setValue(aEntity.getcINNumber());

		this.recordStatus.setValue(aEntity.getRecordStatus());

		if (!aEntity.isNewRecord()) {
			ArrayList<Filter> filters = new ArrayList<Filter>();

			if (this.country.getValue() != null && !this.country.getValue().isEmpty()) {
				Filter filterPin0 = new Filter("PCCountry", country.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin0);
			}

			if (this.stateCode.getValue() != null && !this.stateCode.getValue().isEmpty()) {
				Filter filterPin1 = new Filter("PCProvince", stateCode.getValue(), Filter.OP_EQUAL);
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
	 * @param aEntity
	 */
	public void doWriteComponentsToBean(Entity aEntity) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Entity Code
		try {
			String entityCode = this.entityCode.getValue();
			if (aEntity.isNewRecord()) {
				if (StringUtils.isNotBlank(entityCode) && entityCode.length() < 2) {
					throw new WrongValueException(this.entityCode,
							Labels.getLabel("label_EntityDialog_EntityCode.value")
									+ " lenth should be greater than 1.");
				}
			}
			aEntity.setEntityCode(entityCode);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Entity Name
		try {
			aEntity.setEntityDesc(this.entityDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// PAN Number
		try {
			aEntity.setPANNumber(this.pANNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Country
		try {
			aEntity.setCountry(this.country.getValidatedValue());
			aEntity.setCountryName(this.country.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// State Code
		try {
			aEntity.setStateCode(this.stateCode.getValidatedValue());
			aEntity.setStateCodeName(this.stateCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// City Code
		try {
			aEntity.setCityCode(this.cityCode.getValidatedValue());
			aEntity.setCityCodeName(this.cityCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Pin Code
		try {
			Object obj = this.pinCode.getAttribute("pinCodeId");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aEntity.setPinCodeId(Long.valueOf((obj.toString())));
				}
			} else {
				aEntity.setPinCodeId(null);
			}
			aEntity.setPinCode(this.pinCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// AddOfBranch
		try {
			aEntity.setEntityAddrLine1(this.entityAddrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// AddOfBranch
		try {
			aEntity.setEntityAddrLine2(this.entityAddrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEntity.setEntityAddrHNbr(this.entityAddrHNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aEntity.setEntityFlatNbr(this.entityFlatNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aEntity.setEntityAddrStreet(this.entityAddrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEntity.setEntityPOBox(this.entityPOBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aEntity.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// CIN Number
		try {
			aEntity.setcINNumber(this.cINNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// gstinAvailable
		try {
			aEntity.setGstinAvailable(this.gstinAvailable.isChecked());
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
	 * @param entity The entity that need to be render.
	 */
	public void doShowDialog(Entity entity) {
		logger.debug(Literal.LEAVING);

		if (entity.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.entityCode.focus();
		} else {
			this.entityCode.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(entity.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.entityDesc.focus();
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

		doWriteBeanToComponents(entity);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.entityDesc.isReadonly()) {
			this.entityDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityDesc.value"),
					PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
		}
		if (!this.pANNumber.isReadonly()) {
			this.pANNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_PANNumber.value"),
					PennantRegularExpressions.REGEX_PANNUMBER, true));
		}
		if (!this.country.isReadonly()) {
			this.country.setConstraint(
					new PTStringValidator(Labels.getLabel("label_EntityDialog_Country.value"), null, true, true));
		}
		if (!this.stateCode.isReadonly()) {
			this.stateCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_EntityDialog_StateCode.value"), null, true, true));
		}
		if (!this.cityCode.isReadonly()) {
			this.cityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_EntityDialog_CityCode.value"), null, true, true));
		}
		if (this.pinCode.isButtonVisible()) {
			this.pinCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_EntityDialog_PinCode.value"), null, true, true));
		}
		if (!this.entityAddrLine1.isReadonly()) {
			this.entityAddrLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityAddrLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.entityAddrLine2.isReadonly()) {
			this.entityAddrLine2
					.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityAddrLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.entityPOBox.isReadonly()) {
			this.entityPOBox
					.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityPOBox.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.entityAddrHNbr.isReadonly()) {
			this.entityAddrHNbr
					.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityAddrHNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.entityFlatNbr.isReadonly()) {
			this.entityFlatNbr
					.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityFlatNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.entityAddrStreet.isReadonly()) {
			this.entityAddrStreet
					.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityAddrStreet.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		// CIN Number Validation
		if (!this.cINNumber.isReadonly()) {
			this.cINNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_EntityDialog_CINNumber.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.entityCode.setConstraint("");
		this.entityDesc.setConstraint("");
		this.pANNumber.setConstraint("");
		this.country.setConstraint("");
		this.stateCode.setConstraint("");
		this.cityCode.setConstraint("");
		this.pinCode.setConstraint("");
		this.entityAddrLine1.setConstraint("");
		this.entityAddrLine2.setConstraint("");
		this.entityPOBox.setConstraint("");
		this.entityAddrHNbr.setConstraint("");
		this.entityFlatNbr.setConstraint("");
		this.entityAddrStreet.setConstraint("");

		this.cINNumber.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Entity Code
		// Entity Name
		// PAN Number
		// Country
		// State Code
		// City Code
		// Pin Code
		// Active

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
		this.entityAddrLine1.setErrorMessage("");
		this.country.setErrorMessage("");
		this.stateCode.setErrorMessage("");
		this.entityAddrLine2.setErrorMessage("");
		this.entityPOBox.setErrorMessage("");
		this.entityAddrHNbr.setErrorMessage("");
		this.entityFlatNbr.setErrorMessage("");
		this.entityAddrStreet.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a Entity object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Entity aEntity = new Entity();
		BeanUtils.copyProperties(this.entity, aEntity);

		doDelete(aEntity.getEntityCode(), aEntity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.entity.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.entityCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.entityCode);

		}

		readOnlyComponent(isReadOnly("EntityDialog_EntityDesc"), this.entityDesc);
		readOnlyComponent(isReadOnly("EntityDialog_PANNumber"), this.pANNumber);
		readOnlyComponent(isReadOnly("EntityDialog_Country"), this.country);
		readOnlyComponent(isReadOnly("EntityDialog_StateCode"), this.stateCode);
		readOnlyComponent(isReadOnly("EntityDialog_CityCode"), this.cityCode);
		readOnlyComponent(isReadOnly("EntityDialog_PinCode"), this.pinCode);
		readOnlyComponent(isReadOnly("EntityDialog_EntityAddrLine1"), this.entityAddrLine1);
		readOnlyComponent(isReadOnly("EntityDialog_EntityAddrLine2"), this.entityAddrLine2);
		readOnlyComponent(isReadOnly("EntityDialog_EntityPOBox"), this.entityFlatNbr);
		readOnlyComponent(isReadOnly("EntityDialog_EntityAddrHNbr"), this.entityAddrHNbr);
		readOnlyComponent(isReadOnly("EntityDialog_EntityFlatNbr"), this.entityAddrStreet);
		readOnlyComponent(isReadOnly("EntityDialog_EntityAddrStreet"), this.entityPOBox);
		readOnlyComponent(isReadOnly("EntityDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("EntityDialog_Active"), this.gstinAvailable); // TODO crate right

		readOnlyComponent(isReadOnly("EntityDialog_CINNumber"), this.cINNumber);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.entity.isNewRecord()) {
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

		this.active.setDisabled(true);
		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.entityDesc);
		readOnlyComponent(true, this.pANNumber);
		readOnlyComponent(true, this.country);
		readOnlyComponent(true, this.stateCode);
		readOnlyComponent(true, this.cityCode);
		readOnlyComponent(true, this.pinCode);
		readOnlyComponent(true, this.entityAddrLine1);
		readOnlyComponent(true, this.entityAddrLine2);
		readOnlyComponent(true, this.entityAddrHNbr);
		readOnlyComponent(true, this.entityAddrStreet);
		readOnlyComponent(true, this.entityFlatNbr);
		readOnlyComponent(true, this.entityPOBox);
		readOnlyComponent(true, this.gstinAvailable);

		// readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.cINNumber);

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
		this.entityCode.setValue("");
		this.entityDesc.setValue("");
		this.pANNumber.setValue("");
		this.country.setValue("");
		this.country.setDescription("");
		this.stateCode.setValue("");
		this.stateCode.setDescription("");
		this.cityCode.setValue("");
		this.cityCode.setDescription("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		this.entityAddrLine1.setValue("");
		this.entityAddrLine2.setValue("");
		this.entityPOBox.setValue("");
		this.entityFlatNbr.setValue("");
		this.entityAddrHNbr.setValue("");
		this.entityAddrStreet.setValue("");

		this.active.setChecked(false);
		this.gstinAvailable.setChecked(false);

		this.cINNumber.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final Entity aEntity = new Entity();
		BeanUtils.copyProperties(this.entity, aEntity);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aEntity);

		isNew = aEntity.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aEntity.getRecordType())) {
				aEntity.setVersion(aEntity.getVersion() + 1);
				if (isNew) {
					aEntity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aEntity.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEntity.setNewRecord(true);
				}
			}
		} else {
			aEntity.setVersion(aEntity.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aEntity, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
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
	protected boolean doProcess(Entity aEntity, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aEntity.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aEntity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEntity.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aEntity.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aEntity.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aEntity);
				}

				if (isNotesMandatory(taskId, aEntity)) {
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

			aEntity.setTaskId(taskId);
			aEntity.setNextTaskId(nextTaskId);
			aEntity.setRoleCode(getRole());
			aEntity.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aEntity, tranType);
			String operationRefs = getServiceOperations(taskId, aEntity);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aEntity, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aEntity, tranType);
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
		Entity aEntity = (Entity) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = entityService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = entityService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = entityService.doApprove(auditHeader);

					if (aEntity.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = entityService.doReject(auditHeader);
					if (aEntity.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_EntityDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_EntityDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.entity), true);
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

	private AuditHeader getAuditHeader(Entity aEntity, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEntity.getBefImage(), aEntity);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aEntity.getUserDetails(),
				getOverideMap());
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

}