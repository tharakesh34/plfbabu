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
 * FileName    		:  TakafulProviderDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.takafulprovider;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.TakafulProviderService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PTWebValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/TakafulProvider/takafulProviderDialog.zul file.
 */
public class TakafulProviderDialogCtrl extends GFCBaseCtrl<TakafulProvider> {
	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger.getLogger(TakafulProviderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_TakafulProviderDialog;

	protected Uppercasebox						takafulCode;
	protected Textbox							takafulName;
	protected Combobox							takafulType;
	protected AccountSelectionBox				accountNumber;
	protected Decimalbox						takafulRate;
	protected Datebox							establishedDate;
	protected Textbox							street;
	protected Textbox							houseNumber;
	protected Textbox							addrLine1;
	protected Textbox							addrLine2;
	protected ExtendedCombobox					country;
	protected ExtendedCombobox					province;
	protected ExtendedCombobox					city;
	protected Textbox							phone;
	protected Textbox							phoneCountryCode;
	protected Textbox							phoneAreaCode;
	protected Textbox							fax;
	protected Textbox							faxCountryCode;
	protected Textbox							faxAreaCode;
	protected Textbox							zipCode;
	protected Textbox							emailId;
	protected Textbox							webSite;
	protected Textbox							contactPerson;
	protected Textbox							contactPersonNo;
	protected Textbox							cpPhoneCountryCode;
	protected Textbox							cpPhoneAreaCode;
	protected Datebox							expiryDate;
	protected Combobox							providerType;
	protected Textbox							cityName;

	private boolean								enqModule			= false;

	// not auto wired vars
	private TakafulProvider						takafulProvider;															// overhanded per param
	private transient TakafulProviderListCtrl	takafulProviderListCtrl;													// overhanded per param

	private transient String					sCountry;
	private transient String					sProvince;
	// ServiceDAOs / Domain Classes
	private transient TakafulProviderService	takafulProviderService;

	/**
	 * default constructor.<br>
	 */
	public TakafulProviderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TakafulProviderDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected TakafulProvider object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TakafulProviderDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TakafulProviderDialog);

		try {
			if (PennantConstants.CITY_FREETEXT) {
				this.city.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.city.setVisible(true);
				this.cityName.setVisible(false);
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("takafulProvider")) {
				this.takafulProvider = (TakafulProvider) arguments.get("takafulProvider");
				TakafulProvider befImage = new TakafulProvider();
				BeanUtils.copyProperties(this.takafulProvider, befImage);
				this.takafulProvider.setBefImage(befImage);

				setTakafulProvider(this.takafulProvider);
			} else {
				setTakafulProvider(null);
			}
			doLoadWorkFlow(this.takafulProvider.isWorkflow(), this.takafulProvider.getWorkflowId(),
					this.takafulProvider.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "TakafulProviderDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			// READ OVERHANDED params !
			// we get the takafulProviderListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete takafulProvider here.
			if (arguments.containsKey("takafulProviderListCtrl")) {
				setTakafulProviderListCtrl((TakafulProviderListCtrl) arguments.get("takafulProviderListCtrl"));
			} else {
				setTakafulProviderListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getTakafulProvider());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_TakafulProviderDialog.onClose();
		}

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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_TakafulProviderDialog);
		logger.debug("Leaving" + event.toString());
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

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(
					getNotes("TakafulProvider", String.valueOf(getTakafulProvider().getTakafulCode()),
							getTakafulProvider().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onFulfill$country(Event event) {
		logger.debug("Entering" + event.toString());
		doSetProvProp();
		doSetCityProp();
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$province(Event event) {
		logger.debug("Entering" + event.toString());
		doSetCityProp();
		logger.debug("Leaving" + event.toString());
	}

	private void doSetProvProp() {
		if (!StringUtils.trimToEmpty(sCountry).equals(this.country.getValue())) {
			this.province.setObject("");
			this.province.setValue("");
			this.province.setDescription("");
			this.city.setObject("");
			this.city.setValue("");
			this.city.setDescription("");
		}
		sCountry = this.country.getValue();
		Filter[] filtersProvince = new Filter[1];
		filtersProvince[0] = new Filter("CPCountry", this.country.getValue(), Filter.OP_EQUAL);
		this.province.setFilters(filtersProvince);
	}

	private void doSetCityProp() {
		if (!StringUtils.trimToEmpty(sProvince).equals(this.province.getValue())) {
			this.city.setObject("");
			this.city.setValue("");
			this.city.setDescription("");
		}
		sProvince = this.province.getValue();
		Filter[] filtersCity = new Filter[2];
		filtersCity[0] = new Filter("PCCountry", this.country.getValue(), Filter.OP_EQUAL);
		filtersCity[1] = new Filter("PCProvince", this.province.getValue(), Filter.OP_EQUAL);
		this.city.setFilters(filtersCity);
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aTakafulProvider
	 * @throws Exception
	 */
	public void doShowDialog(TakafulProvider aTakafulProvider) throws Exception {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aTakafulProvider.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.takafulCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.takafulName.focus();
				if (StringUtils.isNotBlank(aTakafulProvider.getRecordType())) {
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
			doWriteBeanToComponents(aTakafulProvider);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_TakafulProviderDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("TakafulProviderDialog", getRole());
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnSave"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.takafulCode.setMaxlength(8);
		this.takafulName.setMaxlength(50);

		this.accountNumber.setAcountDetails(AccountConstants.ACTYPES_TAKAFULPROVIDER, "", true);
		this.accountNumber.setMandatoryStyle(false);
		this.accountNumber.setTextBoxWidth(161);
		this.accountNumber.setButtonVisible(false);

		this.takafulRate.setMaxlength(13);
		this.takafulRate.setFormat(PennantConstants.rateFormate9);
		this.takafulRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.takafulRate.setScale(9);

		this.establishedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.houseNumber.setMaxlength(50);
		this.street.setMaxlength(50);
		this.addrLine1.setMaxlength(50);
		this.addrLine2.setMaxlength(50);
		this.country.setMaxlength(2);
		this.province.setMaxlength(8);
		this.city.setMaxlength(8);
		this.cityName.setMaxlength(50);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneAreaCode.setMaxlength(3);
		this.phone.setMaxlength(8);
		this.faxCountryCode.setMaxlength(3);
		this.faxAreaCode.setMaxlength(3);
		this.fax.setMaxlength(8);
		this.emailId.setMaxlength(100);
		this.webSite.setMaxlength(100);
		this.contactPerson.setMaxlength(20);
		this.cpPhoneCountryCode.setMaxlength(3);
		this.cpPhoneAreaCode.setMaxlength(3);
		this.contactPersonNo.setMaxlength(8);
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.country.setMandatoryStyle(false);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] { "CountryCode" });

		this.province.setMandatoryStyle(false);
		this.province.setModuleName("Province");
		this.province.setValueColumn("CPProvince");
		this.province.setDescColumn("CPProvinceName");
		this.province.setValidateColumns(new String[] { "CPProvince" });

		this.city.setMandatoryStyle(false);
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.takafulProvider.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aTakafulProvider
	 *            TakafulProvider
	 */
	public void doWriteBeanToComponents(TakafulProvider aTakafulProvider) {
		logger.debug("Entering");
		this.takafulCode.setValue(aTakafulProvider.getTakafulCode());
		this.takafulName.setValue(aTakafulProvider.getTakafulName());
		fillComboBox(this.takafulType, aTakafulProvider.getTakafulType(), PennantStaticListUtil.getInsuranceTypes(), "");
		this.takafulRate.setValue(aTakafulProvider.getTakafulRate());
		this.accountNumber.setValue(aTakafulProvider.getAccountNumber());
		this.establishedDate.setValue(aTakafulProvider.getEstablishedDate());
		this.houseNumber.setValue(aTakafulProvider.getHouseNumber());
		this.street.setValue(aTakafulProvider.getStreet());
		this.expiryDate.setValue(aTakafulProvider.getExpiryDate());
		fillComboBox(this.providerType, aTakafulProvider.getProviderType(), PennantStaticListUtil.getProviderTypes(),
				"");
		this.addrLine1.setValue(aTakafulProvider.getAddrLine1());
		this.addrLine2.setValue(aTakafulProvider.getAddrLine2());
		this.country.setValue(aTakafulProvider.getCountry());
		this.province.setValue(aTakafulProvider.getProvince());
		this.city.setValue(aTakafulProvider.getCity());
		this.cityName.setValue(aTakafulProvider.getCity());
		String[] phone = PennantApplicationUtil.unFormatPhoneNumber(aTakafulProvider.getPhone());
		this.phoneCountryCode.setValue(phone[0]);
		this.phoneAreaCode.setValue(phone[1]);
		this.phone.setValue(phone[2]);
		this.zipCode.setValue(aTakafulProvider.getZipCode());
		String[] faxNo = PennantApplicationUtil.unFormatPhoneNumber(aTakafulProvider.getFax());
		this.faxCountryCode.setValue(faxNo[0]);
		this.faxAreaCode.setValue(faxNo[1]);
		this.fax.setValue(faxNo[2]);
		this.emailId.setValue(aTakafulProvider.getEmailId());
		this.webSite.setValue(aTakafulProvider.getWebSite());
		this.contactPerson.setValue(aTakafulProvider.getContactPerson());
		String[] mobile = PennantApplicationUtil.unFormatPhoneNumber(aTakafulProvider.getContactPersonNo());
		this.cpPhoneCountryCode.setValue(mobile[0]);
		this.cpPhoneAreaCode.setValue(mobile[1]);
		this.contactPersonNo.setValue(mobile[2]);
		if (aTakafulProvider.isNewRecord()) {
			this.country.setDescription("");
			this.province.setDescription("");
			this.city.setDescription("");
		} else {
			this.country.setDescription(aTakafulProvider.getLovDescCountryDesc());
			this.province.setDescription(aTakafulProvider.getLovDescProvinceDesc());
			this.city.setDescription(aTakafulProvider.getLovDescCityDesc());
		}
		this.recordStatus.setValue(aTakafulProvider.getRecordStatus());
		sCountry = this.country.getValue();
		sProvince = this.province.getValue();
		doSetCityProp();
		doSetProvProp();
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTakafulProvider
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(TakafulProvider aTakafulProvider) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aTakafulProvider.setTakafulCode(this.takafulCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setTakafulName(this.takafulName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//TODO remove after confirming
		aTakafulProvider.setTakafulType("G");

		try {
			if (this.takafulRate.getValue() != null) {
				aTakafulProvider.setTakafulRate(this.takafulRate.getValue());
			} else {
				aTakafulProvider.setTakafulRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.accountNumber.validateValue();
			aTakafulProvider.setAccountNumber(PennantApplicationUtil.unFormatAccountNumber(this.accountNumber
					.getValue()));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.establishedDate.getValue() != null) {
				if (this.establishedDate.getValue().after(DateUtility.getAppDate())) {
					throw new WrongValueException(this.establishedDate, Labels.getLabel("DATE_EMPTY_FUTURE",
							new String[] { Labels.getLabel("label_TakafulProviderDialog_EstablishedDate.value"),
									DateUtility.getAppDate(DateFormat.LONG_DATE) }));
				}
				aTakafulProvider.setEstablishedDate(new Timestamp(this.establishedDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setHouseNumber(this.houseNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setStreet(this.street.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setAddrLine1(this.addrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setAddrLine2(this.addrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.country.getValidatedValue())) {
				aTakafulProvider.setLovDescCountryDesc(null);
				aTakafulProvider.setCountry(null);
			} else {
				aTakafulProvider.setLovDescCountryDesc(this.country.getDescription());
				aTakafulProvider.setCountry(this.country.getValidatedValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setLovDescProvinceDesc(this.province.getDescription());
			aTakafulProvider.setProvince(this.province.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			if (PennantConstants.CITY_FREETEXT) {
				aTakafulProvider.setCity(StringUtils.trimToNull(this.cityName.getValue()));
			} else {
				aTakafulProvider.setLovDescCityDesc(StringUtils.trimToNull(this.city.getDescription()));
				aTakafulProvider.setCity(StringUtils.trimToNull(this.city.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setPhone(PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(),
					this.phoneAreaCode.getValue(), this.phone.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setZipCode(this.zipCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setFax(PennantApplicationUtil.formatPhoneNumber(this.faxCountryCode.getValue(),
					this.faxAreaCode.getValue(), this.fax.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setEmailId(this.emailId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setWebSite(this.webSite.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setContactPerson(this.contactPerson.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setContactPersonNo(PennantApplicationUtil.formatPhoneNumber(
					this.cpPhoneCountryCode.getValue(), this.cpPhoneAreaCode.getValue(),
					this.contactPersonNo.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTakafulProvider.setProviderType(getComboboxValue(this.providerType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		 
		try {
			if (this.expiryDate.getValue() != null && this.establishedDate.getValue()!=null) {
				if (!this.expiryDate.getValue().after(this.establishedDate.getValue())) {
					throw new WrongValueException(this.expiryDate, Labels.getLabel(
							"DATE_ALLOWED_AFTER",
							new String[] { Labels.getLabel("label_TakafulProviderDialog_ExpiryDate.value"),
									Labels.getLabel("label_TakafulProviderDialog_EstablishedDate.value") }));
				}
				aTakafulProvider.setExpiryDate(new Timestamp(this.expiryDate.getValue().getTime()));
			}
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

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		if (!this.takafulCode.isReadonly()) {
			this.takafulCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_TakafulProviderDialog_TakafulCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}
		if (!this.takafulName.isReadonly()) {
			this.takafulName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_TakafulProviderDialog_TakafulName.value"), PennantRegularExpressions.REGEX_NAME,
					true));
		}
		/*if (!this.takafulRate.isDisabled()) {
			this.takafulRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_TakafulProviderDialog_TakafulRate.value"), 4, true, false, 0, 9999));
		}*/
		if (!this.accountNumber.isReadonly()) {
			this.accountNumber.setConstraint(new PTStringValidator(Labels
					.getLabel("label_TakafulProviderDialog_AccountNumber.value"), null, false));
		}
		if (!this.establishedDate.isDisabled()) {
			this.establishedDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_TakafulProviderDialog_EstablishedDate.value"), false, null, DateUtility.getAppDate(), false));
		}
		if (!this.addrLine1.isReadonly()) {
			this.addrLine1.setConstraint(new PTStringValidator(Labels
					.getLabel("label_TakafulProviderDialog_AddrLine1.value"), PennantRegularExpressions.REGEX_ADDRESS,
					false));
		}
		if (!this.addrLine2.isReadonly()) {
			this.addrLine2.setConstraint(new PTStringValidator(Labels
					.getLabel("label_TakafulProviderDialog_AddrLine2.value"), PennantRegularExpressions.REGEX_ADDRESS,
					false));
		}
		if (!this.zipCode.isReadonly()) {
			this.zipCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_TakafulProviderDialog_ZipCode.value"), PennantRegularExpressions.REGEX_ZIP, false));
		}
		if (!this.phoneCountryCode.isReadonly()) {
			this.phoneCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_phoneCountryCode.value"), false, 1));
		}
		if (!this.phoneAreaCode.isReadonly()) {
			this.phoneAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_phoneAreaCode.value"), false, 2));
		}
		if (!this.phone.isReadonly()) {
			this.phone.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_Phone.value"), false, 3));
		}
		if (!this.faxCountryCode.isReadonly()) {
			this.faxCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_faxCountryCode.value"), false, 1));
		}
		if (!this.faxAreaCode.isReadonly()) {
			this.faxAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_faxAreaCode.value"), false, 2));
		}
		if (!this.fax.isReadonly()) {
			this.fax.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_TakafulProviderDialog_Fax.value"),
					false, 3));
		}
		if (!this.emailId.isReadonly()) {
			this.emailId.setConstraint(new PTEmailValidator(Labels
					.getLabel("label_TakafulProviderDialog_emailId.value"), false));
		}
		if (!this.webSite.isReadonly()) {
			this.webSite.setConstraint(new PTWebValidator(Labels.getLabel("label_TakafulProviderDialog_WebSite.value"),
					false));
		}
		if (!this.contactPerson.isReadonly()) {
			this.contactPerson.setConstraint(new PTStringValidator(Labels
					.getLabel("label_TakafulProviderDialog_ContactPerson.value"), PennantRegularExpressions.REGEX_NAME,
					false));
		}
		if (!cpPhoneCountryCode.isReadonly()) {
			this.cpPhoneCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_phoneNoCountryCode.value"), false, 1));
		}
		if (!cpPhoneAreaCode.isReadonly()) {
			this.cpPhoneAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_phoneNoAreaCode.value"), false, 2));
		}
		if (!this.contactPersonNo.isReadonly()) {
			this.contactPersonNo.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_TakafulProviderDialog_ContactPersonNo.value"), false, 3));
		}
		if (!this.expiryDate.isDisabled()) {
			this.expiryDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_TakafulProviderDialog_ExpiryDate.value"), false, null, null, false));
		}

		if (PennantConstants.CITY_FREETEXT) {
			if (!this.cityName.isReadonly()) {
				this.cityName.setConstraint(new PTStringValidator(Labels
						.getLabel("label_TakafulProviderDialog_CityName.value"), PennantRegularExpressions.REGEX_NAME,
						false));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.takafulCode.setConstraint("");
		this.takafulName.setConstraint("");
		this.takafulRate.setConstraint("");
		this.establishedDate.setConstraint("");
		this.houseNumber.setConstraint("");
		this.street.setConstraint("");
		this.addrLine1.setConstraint("");
		this.addrLine2.setConstraint("");
		this.phone.setConstraint("");
		this.fax.setConstraint("");
		this.emailId.setConstraint("");
		this.webSite.setConstraint("");
		this.contactPerson.setConstraint("");
		this.contactPersonNo.setConstraint("");
		this.cpPhoneCountryCode.setConstraint("");
		this.cpPhoneAreaCode.setConstraint("");
		this.phoneCountryCode.setConstraint("");
		this.phoneAreaCode.setConstraint("");
		this.faxCountryCode.setConstraint("");
		this.faxAreaCode.setConstraint("");
		this.expiryDate.setConstraint("");
		this.cityName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {

		/*
		 * if (!this.country.isReadonly()) { this.country.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_Country.value"), null, true,true)); } if
		 * (!this.province.isReadonly()) { this.province.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_Province.value"), null, true,true)); }
		 */

		if (!PennantConstants.CITY_FREETEXT) {
			this.city.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_City.value"),
					null, false, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.country.setConstraint("");
		this.province.setConstraint("");
		this.city.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.takafulCode.setErrorMessage("");
		this.takafulName.setErrorMessage("");
		this.takafulType.setErrorMessage("");
		this.takafulRate.setErrorMessage("");
		this.accountNumber.setErrorMessage("");
		this.establishedDate.setErrorMessage("");
		this.houseNumber.setErrorMessage("");
		this.street.setErrorMessage("");
		this.addrLine1.setErrorMessage("");
		this.addrLine2.setErrorMessage("");
		this.country.setErrorMessage("");
		this.province.setErrorMessage("");
		this.city.setErrorMessage("");
		this.cityName.setErrorMessage("");
		this.phone.setErrorMessage("");
		this.zipCode.setErrorMessage("");
		this.fax.setErrorMessage("");
		this.emailId.setErrorMessage("");
		this.webSite.setErrorMessage("");
		this.contactPerson.setErrorMessage("");
		this.contactPersonNo.setErrorMessage("");
		this.cpPhoneCountryCode.setErrorMessage("");
		this.cpPhoneAreaCode.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		this.phoneAreaCode.setErrorMessage("");
		this.faxCountryCode.setErrorMessage("");
		this.faxAreaCode.setErrorMessage("");
		this.providerType.setErrorMessage("");
		this.expiryDate.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getTakafulProviderListCtrl().search();
	}

	/**
	 * Deletes a TakafulProvider object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final TakafulProvider aTakafulProvider = new TakafulProvider();
		BeanUtils.copyProperties(getTakafulProvider(), aTakafulProvider);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_TakafulProviderDialog_TakafulCode.value") + " : "
				+ aTakafulProvider.getTakafulCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aTakafulProvider.getRecordType())) {
				aTakafulProvider.setVersion(aTakafulProvider.getVersion() + 1);
				aTakafulProvider.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aTakafulProvider.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aTakafulProvider.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTakafulProvider.getNextTaskId(),
							aTakafulProvider);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aTakafulProvider, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getTakafulProvider().isNewRecord()) {
			this.takafulCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.takafulCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.takafulName.setReadonly(isReadOnly("TakafulProviderDialog_takafulName"));
		this.takafulType.setDisabled(isReadOnly("TakafulProviderDialog_takafulType"));
		this.accountNumber.setReadonly(isReadOnly("TakafulProviderDialog_accountNumber"));
		this.takafulRate.setReadonly(isReadOnly("TakafulProviderDialog_takafulRate"));
		this.establishedDate.setDisabled(isReadOnly("TakafulProviderDialog_establishedDate"));
		this.street.setReadonly(isReadOnly("TakafulProviderDialog_street"));
		this.houseNumber.setReadonly(isReadOnly("TakafulProviderDialog_houseNumber"));
		this.addrLine1.setReadonly(isReadOnly("TakafulProviderDialog_addrLine1"));
		this.addrLine2.setReadonly(isReadOnly("TakafulProviderDialog_addrLine2"));
		this.country.setReadonly(isReadOnly("TakafulProviderDialog_country"));
		this.province.setReadonly(isReadOnly("TakafulProviderDialog_province"));
		this.city.setReadonly(isReadOnly("TakafulProviderDialog_city"));
		this.cityName.setReadonly(isReadOnly("TakafulProviderDialog_city"));
		this.zipCode.setReadonly(isReadOnly("TakafulProviderDialog_zipCode"));
		this.phone.setReadonly(isReadOnly("TakafulProviderDialog_phone"));
		this.phoneAreaCode.setReadonly(isReadOnly("TakafulProviderDialog_phone"));
		this.phoneCountryCode.setReadonly(isReadOnly("TakafulProviderDialog_phone"));
		this.fax.setReadonly(isReadOnly("TakafulProviderDialog_fax"));
		this.faxAreaCode.setReadonly(isReadOnly("TakafulProviderDialog_fax"));
		this.faxCountryCode.setReadonly(isReadOnly("TakafulProviderDialog_fax"));
		this.emailId.setReadonly(isReadOnly("TakafulProviderDialog_emailId"));
		this.webSite.setReadonly(isReadOnly("TakafulProviderDialog_webSite"));
		this.contactPerson.setReadonly(isReadOnly("TakafulProviderDialog_contactPerson"));
		this.contactPersonNo.setReadonly(isReadOnly("TakafulProviderDialog_contactPersonNo"));
		this.cpPhoneAreaCode.setReadonly(isReadOnly("TakafulProviderDialog_contactPersonNo"));
		this.cpPhoneCountryCode.setReadonly(isReadOnly("TakafulProviderDialog_contactPersonNo"));
		this.providerType.setDisabled(isReadOnly("TakafulProviderDialog_providerType"));
		this.expiryDate.setDisabled(isReadOnly("TakafulProviderDialog_expiryDate"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.takafulProvider.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.takafulCode.setReadonly(true);
		this.takafulName.setReadonly(true);
		this.takafulType.setDisabled(true);
		this.accountNumber.setReadonly(true);
		this.takafulRate.setReadonly(true);
		this.establishedDate.setDisabled(true);
		this.street.setReadonly(true);
		this.houseNumber.setReadonly(true);
		this.addrLine1.setReadonly(true);
		this.addrLine2.setReadonly(true);
		this.country.setReadonly(true);
		this.province.setReadonly(true);
		this.city.setReadonly(true);
		this.cityName.setReadonly(true);
		this.zipCode.setReadonly(true);
		this.phone.setReadonly(true);
		this.fax.setReadonly(true);
		this.faxAreaCode.setReadonly(true);
		this.faxCountryCode.setReadonly(true);
		this.emailId.setReadonly(true);
		this.webSite.setReadonly(true);
		this.contactPerson.setReadonly(true);
		this.contactPersonNo.setReadonly(true);
		this.phoneAreaCode.setReadonly(true);
		this.phoneCountryCode.setReadonly(true);
		this.cpPhoneAreaCode.setReadonly(true);
		this.cpPhoneCountryCode.setReadonly(true);
		this.providerType.setDisabled(true);
		this.expiryDate.setDisabled(true);
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
		this.takafulCode.setValue("");
		this.takafulName.setValue("");
		this.takafulRate.setValue("");
		this.establishedDate.setText("");
		this.houseNumber.setValue("");
		this.street.setValue("");
		this.addrLine1.setValue("");
		this.addrLine2.setValue("");
		this.country.setValue("");
		this.country.setDescription("");
		this.province.setValue("");
		this.province.setDescription("");
		this.city.setValue("");
		this.city.setDescription("");
		this.cityName.setValue("");
		this.phone.setValue("");
		this.zipCode.setValue("");
		this.fax.setValue("");
		this.emailId.setValue("");
		this.webSite.setValue("");
		this.contactPerson.setValue("");
		this.contactPersonNo.setValue("");
		this.cpPhoneCountryCode.setValue("");
		this.cpPhoneAreaCode.setValue("");
		this.phoneCountryCode.setValue("");
		this.phoneAreaCode.setValue("");
		this.faxCountryCode.setValue("");
		this.faxAreaCode.setValue("");
		this.takafulType.setSelectedIndex(0);
		this.providerType.setSelectedIndex(0);
		this.expiryDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final TakafulProvider aTakafulProvider = new TakafulProvider();
		BeanUtils.copyProperties(getTakafulProvider(), aTakafulProvider);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aTakafulProvider.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTakafulProvider.getNextTaskId(),
					aTakafulProvider);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aTakafulProvider.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the TakafulProvider object with the components data
			doWriteComponentsToBean(aTakafulProvider);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aTakafulProvider.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTakafulProvider.getRecordType())) {
				aTakafulProvider.setVersion(aTakafulProvider.getVersion() + 1);
				if (isNew) {
					aTakafulProvider.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTakafulProvider.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTakafulProvider.setNewRecord(true);
				}
			}
		} else {
			aTakafulProvider.setVersion(aTakafulProvider.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aTakafulProvider, tranType)) {
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(TakafulProvider aTakafulProvider, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aTakafulProvider.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aTakafulProvider.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTakafulProvider.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aTakafulProvider.setTaskId(getTaskId());
			aTakafulProvider.setNextTaskId(getNextTaskId());
			aTakafulProvider.setRoleCode(getRole());
			aTakafulProvider.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aTakafulProvider, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aTakafulProvider, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aTakafulProvider, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		TakafulProvider aTakafulProvider = (TakafulProvider) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getTakafulProviderService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getTakafulProviderService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getTakafulProviderService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aTakafulProvider.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getTakafulProviderService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aTakafulProvider.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_TakafulProviderDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_TakafulProviderDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(
								getNotes("TakafulProvider", aTakafulProvider.getTakafulCode(),
										aTakafulProvider.getVersion()), true);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(TakafulProvider aTakafulProvider, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTakafulProvider.getBefImage(), aTakafulProvider);
		return new AuditHeader(String.valueOf(aTakafulProvider.getTakafulCode()), null, null, null, auditDetail,
				aTakafulProvider.getUserDetails(), getOverideMap());
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public TakafulProvider getTakafulProvider() {
		return this.takafulProvider;
	}

	public void setTakafulProvider(TakafulProvider takafulProvider) {
		this.takafulProvider = takafulProvider;
	}

	public void setTakafulProviderService(TakafulProviderService takafulProviderService) {
		this.takafulProviderService = takafulProviderService;
	}

	public TakafulProviderService getTakafulProviderService() {
		return this.takafulProviderService;
	}

	public void setTakafulProviderListCtrl(TakafulProviderListCtrl takafulProviderListCtrl) {
		this.takafulProviderListCtrl = takafulProviderListCtrl;
	}

	public TakafulProviderListCtrl getTakafulProviderListCtrl() {
		return this.takafulProviderListCtrl;
	}

}
