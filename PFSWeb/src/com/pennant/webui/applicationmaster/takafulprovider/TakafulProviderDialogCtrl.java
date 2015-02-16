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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.TakafulProviderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PTWebValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/TakafulProvider/takafulProviderDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class TakafulProviderDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TakafulProviderDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_TakafulProviderDialog; 

	protected Textbox 		takafulCode; 
	protected Textbox 		takafulName; 
	protected Combobox      takafulType;
	protected AccountSelectionBox	accountNumber;
	protected Decimalbox	takafulRate;
	protected Datebox 		establishedDate; 
	protected Textbox 		street; 
	protected Textbox 		houseNumber; 
	protected Textbox 		addrLine1; 
	protected Textbox 		addrLine2; 
	protected ExtendedCombobox 		country; 
	protected ExtendedCombobox 		province; 
	protected ExtendedCombobox 		city; 
	protected Textbox 		phone; 
	protected Textbox 		fax; 
	protected Textbox 		zipCode; 
	protected Textbox 		emailId; 
	protected Textbox 		webSite; 
	protected Textbox 		contactPerson; 
	protected Textbox 		contactPersonNo; 

	protected Label 		recordStatus; 
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;

	// not auto wired vars
	private TakafulProvider takafulProvider; // overhanded per param
	private transient TakafulProviderListCtrl takafulProviderListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_TakafulCode;
	private transient String  		oldVar_TakafulName;
	private transient String  		oldVar_TakafulType;
	private transient String  		oldVar_AccountNumber;
	private transient double	    oldVar_TakafulRate;
	private transient Date  		oldVar_EstablishedDate;
	private transient String  		oldVar_HouseNumber;
	private transient String  		oldVar_Street;
	private transient String  		oldVar_AddrLine1;
	private transient String  		oldVar_AddrLine2;
	private transient String  		oldVar_Country;
	private transient String  		oldVar_Province;
	private transient String  		oldVar_City;
	private transient String  		oldVar_Phone;
	private transient String  		oldVar_ZIPCode;
	private transient String  		oldVar_Fax;
	private transient String  		oldVar_EmailId;
	private transient String  		oldVar_WebSite;
	private transient String  		oldVar_ContactPerson;
	private transient String  		oldVar_ContactPersonNo;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_TakafulProviderDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 
	protected Button btnEdit; 
	protected Button btnDelete; 
	protected Button btnSave; 
	protected Button btnCancel; 
	protected Button btnClose; 
	protected Button btnHelp; 
	protected Button btnNotes; 
	private transient String 		oldVar_LovDescCountryDesc;
	private transient String 		oldVar_LovDescProvinceName;
	private transient String 		oldVar_LovDescCityName;

	private transient PagedListService pagedListService;
	
	private transient String   sCountry;
	private transient String sProvince;
	// ServiceDAOs / Domain Classes
	private transient TakafulProviderService takafulProviderService;

	/**
	 * default constructor.<br>
	 */
	public TakafulProviderDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected TakafulProvider object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TakafulProviderDialog(Event event) throws Exception {
		logger.debug("Entring" +event.toString());
		try {

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}

			// READ OVERHANDED params !
			if (args.containsKey("takafulProvider")) {
				this.takafulProvider = (TakafulProvider) args.get("takafulProvider");
				TakafulProvider befImage =new TakafulProvider();
				BeanUtils.copyProperties(this.takafulProvider, befImage);
				this.takafulProvider.setBefImage(befImage);

				setTakafulProvider(this.takafulProvider);
			} else {
				setTakafulProvider(null);
			}
			doLoadWorkFlow(this.takafulProvider.isWorkflow(),this.takafulProvider.getWorkflowId(),this.takafulProvider.getNextTaskId());
			
			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "TakafulProviderDialog");
			}else{
				getUserWorkspace().alocateAuthorities("TakafulProviderDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
			
			// READ OVERHANDED params !
			// we get the takafulProviderListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete takafulProvider here.
			if (args.containsKey("takafulProviderListCtrl")) {
				setTakafulProviderListCtrl((TakafulProviderListCtrl) args.get("takafulProviderListCtrl"));
			} else {
				setTakafulProviderListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getTakafulProvider());
		} catch (Exception e) {
			createException(window_TakafulProviderDialog, e);
			logger.error(e);
		}

		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();	
        logger.debug("Leaving" +event.toString());
	}

	
	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_TakafulProviderDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_TakafulProviderDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("TakafulProvider",
					String.valueOf(getTakafulProvider().getTakafulCode()),
					getTakafulProvider().getVersion()),this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());

	}

	public void onFulfill$country(Event event){
		logger.debug("Entering" + event.toString());
		doSetProvProp();
		doSetCityProp();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$province(Event event){
		logger.debug("Entering" + event.toString());
		doSetCityProp();
		logger.debug("Leaving" + event.toString());
	}
	private void doSetProvProp(){
		if (!StringUtils.trimToEmpty(sCountry).equals(this.country.getValue())){
			this.province.setObject("");
			this.province.setValue("");
			this.province.setDescription("");
			this.city.setObject("");
			this.city.setValue("");
			this.city.setDescription("");
		}
		sCountry = this.country.getValue();
		Filter[] filtersProvince = new Filter[1] ;
		filtersProvince[0]= new Filter("CPCountry", this.country.getValue(), Filter.OP_EQUAL);
		this.province.setFilters(filtersProvince);
	}
	
	private void doSetCityProp(){
		if (!StringUtils.trimToEmpty(sProvince).equals(this.province.getValue())){
			this.city.setObject("");
			this.city.setValue("");
			this.city.setDescription("");   
		}
		sProvince= this.province.getValue();
		Filter[] filtersCity = new Filter[2] ;
		filtersCity[0] = new Filter("PCCountry", this.country.getValue(),Filter.OP_EQUAL);
		filtersCity[1]= new Filter("PCProvince", this.province.getValue(), Filter.OP_EQUAL);
		this.city.setFilters(filtersCity);
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aTakafulProvider
	 * @throws InterruptedException
	 */
	public void doShowDialog(TakafulProvider aTakafulProvider) throws InterruptedException {
		logger.debug("Entering");
		// if aAcademic == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aTakafulProvider == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aTakafulProvider = getTakafulProviderService().getNewTakafulProvider();

			setTakafulProvider(aTakafulProvider);
		} else {
			setTakafulProvider(aTakafulProvider);
		}
		// set ReadOnly mode accordingly if the object is new or not.
		if (aTakafulProvider.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.takafulCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.takafulName.focus();
				if (!StringUtils.trimToEmpty(aTakafulProvider.getRecordType()).equals("")) {
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_TakafulProviderDialog);
		} catch (final Exception e) {
			logger.error("doShowDialog() " + e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit


	

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;
		getUserWorkspace().alocateAuthorities("TakafulProviderDialog",getRole());
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TakafulProviderDialog_btnSave"));	
		}
		logger.debug("Leaving") ;
	}
	
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.takafulCode.setMaxlength(8);
		this.takafulName.setMaxlength(50);
		this.accountNumber.setMandatoryStyle(true);
		this.takafulRate.setMaxlength(13);
		this.takafulRate.setFormat(PennantConstants.rateFormate9);
		this.takafulRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.takafulRate.setScale(9);
		
		this.establishedDate.setFormat(PennantConstants.dateFormat);
		this.houseNumber.setMaxlength(50);
		this.street.setMaxlength(50);
		this.addrLine1.setMaxlength(50);
		this.addrLine2.setMaxlength(50);
		this.country.setMaxlength(2);
		this.province.setMaxlength(8);
		this.city.setMaxlength(8);
		this.phone.setMaxlength(13);
		this.fax.setMaxlength(13);
		this.emailId.setMaxlength(100);
		this.webSite.setMaxlength(100);
		this.contactPerson.setMaxlength(20);
		this.contactPersonNo.setMaxlength(13);
		
		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[]{"CountryCode"});
		
		this.province.setMandatoryStyle(true);
		this.province.setModuleName("Province");
		this.province.setValueColumn("CPProvince");
		this.province.setDescColumn("CPProvinceName");
		this.province.setValidateColumns(new String[] { "CPProvince" });
		
		this.city.setMandatoryStyle(true);
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}


	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_TakafulCode = this.takafulCode.getValue();
		this.oldVar_TakafulName = this.takafulName.getValue();
		this.oldVar_AccountNumber = this.accountNumber.getValue();
		this.oldVar_TakafulRate = this.takafulRate.doubleValue();
		this.oldVar_EstablishedDate = this.establishedDate.getValue();
		this.oldVar_HouseNumber = this.houseNumber.getValue();
		this.oldVar_Street = this.street.getValue();
		this.oldVar_AddrLine1 = this.addrLine1.getValue();
		this.oldVar_AddrLine2 = this.addrLine2.getValue();
		this.oldVar_Country = this.country.getValue();
		this.oldVar_LovDescCountryDesc = this.country.getDescription();
		this.oldVar_Province = this.province.getValue();
		this.oldVar_LovDescProvinceName = this.province.getDescription();
		this.oldVar_City = this.city.getValue();
		this.oldVar_LovDescCityName = this.city.getDescription();
		this.oldVar_Phone = this.phone.getValue();
		this.oldVar_ZIPCode = this.zipCode.getValue();
		this.oldVar_Fax = this.fax.getValue();
		this.oldVar_EmailId = this.emailId.getValue();
		this.oldVar_WebSite = this.webSite.getValue();
		this.oldVar_ContactPerson = this.contactPerson.getValue();
		this.oldVar_ContactPersonNo = this.contactPersonNo.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_TakafulType = PennantConstants.List_Select;
		if(this.takafulType.getSelectedItem() !=null){
			this.oldVar_TakafulType=this.takafulType.getSelectedItem().getValue().toString();
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.takafulCode.setValue(this.oldVar_TakafulCode);
		this.takafulName.setValue(this.oldVar_TakafulName);
		this.accountNumber.setValue(this.oldVar_AccountNumber);
		this.takafulRate.setValue(new BigDecimal(this.oldVar_TakafulRate));
		this.establishedDate.setValue(this.oldVar_EstablishedDate);
		this.houseNumber.setValue(this.oldVar_HouseNumber);
		this.street.setValue(this.oldVar_Street);
		this.addrLine1.setValue(this.oldVar_AddrLine1);
		this.addrLine2.setValue(this.oldVar_AddrLine2);
		this.country.setValue(this.oldVar_Country);
		this.country.setDescription(this.oldVar_LovDescCountryDesc);
		this.province.setValue(this.oldVar_Province);
		this.province.setDescription(this.oldVar_LovDescProvinceName);
		this.city.setValue(this.oldVar_City);
		this.city.setDescription(this.oldVar_LovDescCityName);
		this.phone.setValue(this.oldVar_Phone);
		this.zipCode.setValue(this.oldVar_ZIPCode);
		this.fax.setValue(this.oldVar_Fax);
		this.emailId.setValue(this.oldVar_EmailId);
		this.webSite.setValue(this.oldVar_WebSite);
		this.contactPerson.setValue(this.oldVar_ContactPerson);
		this.contactPersonNo.setValue(this.oldVar_ContactPersonNo);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
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
		doResetInitValues();
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
		logger.debug("Entering") ;
		this.takafulCode.setValue(aTakafulProvider.getTakafulCode());
		this.takafulName.setValue(aTakafulProvider.getTakafulName());
		fillComboBox(this.takafulType, aTakafulProvider.getTakafulType(), PennantStaticListUtil.getTakafulTypes(),"");
		this.takafulRate.setValue(aTakafulProvider.getTakafulRate());
		this.accountNumber.setValue(aTakafulProvider.getAccountNumber());
		this.establishedDate.setValue(aTakafulProvider.getEstablishedDate());
		this.houseNumber.setValue(aTakafulProvider.getHouseNumber());
		this.street.setValue(aTakafulProvider.getStreet());
		this.addrLine1.setValue(aTakafulProvider.getAddrLine1());
		this.addrLine2.setValue(aTakafulProvider.getAddrLine2());
		this.country.setValue(aTakafulProvider.getCountry());
		this.province.setValue(aTakafulProvider.getProvince());
		this.city.setValue(aTakafulProvider.getCity());
		this.phone.setValue(aTakafulProvider.getPhone());
		this.zipCode.setValue(aTakafulProvider.getZipCode());
		this.fax.setValue(aTakafulProvider.getFax());
		this.emailId.setValue(aTakafulProvider.getEmailId());
		this.webSite.setValue(aTakafulProvider.getWebSite());
		this.contactPerson.setValue(aTakafulProvider.getContactPerson());
		this.contactPersonNo.setValue(aTakafulProvider.getContactPersonNo());

		if (aTakafulProvider.isNewRecord()){
			this.country.setDescription("");
			this.province.setDescription("");
			this.city.setDescription("");
		}else{
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
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aTakafulProvider.setTakafulCode(this.takafulCode.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setTakafulName(this.takafulName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if( getComboboxValue(this.takafulType).equals("#")) {
				throw new WrongValueException(this.takafulType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_TakafulProviderDialog_TakafulType.value") }));
			}
			aTakafulProvider.setTakafulType(getComboboxValue(this.takafulType));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.takafulRate.getValue() != null) {
				aTakafulProvider.setTakafulRate(this.takafulRate.getValue());
			} else {
				aTakafulProvider.setTakafulRate(BigDecimal.ZERO);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			this.accountNumber.validateValue();
			aTakafulProvider.setAccountNumber(PennantApplicationUtil.unFormatAccountNumber(this.accountNumber.getValue()));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.establishedDate.getValue() != null){
				if (this.establishedDate.getValue().after(((Date) SystemParameterDetails
						.getSystemParameterValue("APP_DATE")))) {
					throw new WrongValueException(this.establishedDate,Labels.getLabel("DATE_EMPTY_FUTURE",
							new String[] {Labels.getLabel("label_TakafulProviderDialog_EstablishedDate.value"),
							SystemParameterDetails.getSystemParameterValue("APP_DATE").toString() }));
				}
				aTakafulProvider.setEstablishedDate(new Timestamp(this.establishedDate.getValue().getTime()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setHouseNumber(this.houseNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setStreet(this.street.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setAddrLine1(this.addrLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setAddrLine2(this.addrLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aTakafulProvider.setLovDescCountryDesc(this.country.getDescription());
			aTakafulProvider.setCountry(this.country.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setLovDescProvinceDesc(this.province.getDescription());
			aTakafulProvider.setProvince(this.province.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setLovDescCityDesc(this.city.getDescription());
			aTakafulProvider.setCity(this.city.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setPhone(this.phone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setZipCode(this.zipCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setFax(this.fax.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setEmailId(this.emailId.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setWebSite(this.webSite.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setContactPerson(this.contactPerson.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTakafulProvider.setContactPersonNo(this.contactPersonNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}


	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();

		if (!StringUtils.trimToEmpty(this.oldVar_TakafulCode).equals(StringUtils.trimToEmpty(this.takafulCode.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_TakafulName).equals(StringUtils.trimToEmpty(this.takafulName.getValue()))) {
			return true;
		}
		if (this.oldVar_TakafulRate != this.takafulRate.doubleValue()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_AccountNumber).equals(StringUtils.trimToEmpty(this.accountNumber.getValue()))) {
			return true;
		}
		String oldEstablishDate = "";
		String newEstablishDate ="";
		if (this.oldVar_EstablishedDate!=null){
			oldEstablishDate=DateUtility.formatDate(this.oldVar_EstablishedDate,PennantConstants.dateFormat);
		}
		if (this.establishedDate.getValue()!=null){
			newEstablishDate=DateUtility.formatDate(this.establishedDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldEstablishDate).equals(StringUtils.trimToEmpty(newEstablishDate))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_HouseNumber).equals(StringUtils.trimToEmpty(this.houseNumber.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_Street).equals(StringUtils.trimToEmpty(this.street.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_AddrLine1).equals(StringUtils.trimToEmpty(this.addrLine1.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_AddrLine2).equals(StringUtils.trimToEmpty(this.addrLine2.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_Country).equals(StringUtils.trimToEmpty(this.country.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_Province).equals(StringUtils.trimToEmpty(this.province.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_City).equals(StringUtils.trimToEmpty(this.city.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_Phone).equals(StringUtils.trimToEmpty(this.phone.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_ZIPCode).equals(StringUtils.trimToEmpty(this.zipCode.getValue()))) {
			return true;
		}
		
		if (!StringUtils.trimToEmpty(this.oldVar_Fax).equals(StringUtils.trimToEmpty(this.fax.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmailId).equals(StringUtils.trimToEmpty(this.emailId.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_WebSite).equals(StringUtils.trimToEmpty(this.webSite.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_ContactPerson).equals(StringUtils.trimToEmpty(this.contactPerson.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_ContactPersonNo).equals(StringUtils.trimToEmpty(this.contactPersonNo.getValue()))) {
			return true;
		}
		String strTakafulType =PennantConstants.List_Select;
		if(this.takafulType.getSelectedItem()!=null){
			strTakafulType = this.takafulType.getSelectedItem().getValue().toString();	
		}

		if (!StringUtils.trimToEmpty(this.oldVar_TakafulType).equals(strTakafulType)) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		this.takafulCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_TakafulCode.value"), 
				PennantRegularExpressions.REGEX_NAME, true));
		this.takafulName.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_TakafulName.value"), 
				PennantRegularExpressions.REGEX_NAME, true));
		if (!this.takafulRate.isReadonly()) {
			this.takafulRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_TakafulProviderDialog_TakafulRate.value"),false));
		}	
		this.accountNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_AccountNumber.value"),null,true));
		this.establishedDate.setConstraint(new PTDateValidator(Labels.getLabel("label_TakafulProviderDialog_EstablishedDate.value"), true, null, new Date(), false));
		this.addrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_AddrLine1.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		this.addrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_AddrLine2.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		this.zipCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_ZipCode.value"), 
				PennantRegularExpressions.REGEX_ZIP, true));
		this.phone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_TakafulProviderDialog_Phone.value"),true));
		this.fax.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_TakafulProviderDialog_Fax.value"),true));
		this.emailId.setConstraint(new PTEmailValidator(Labels.getLabel("label_TakafulProviderDialog_emailId.value"),true));
		this.webSite.setConstraint(new PTWebValidator(Labels.getLabel("label_TakafulProviderDialog_WebSite.value"), true));
		this.contactPerson.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_ContactPerson.value"), 
				PennantRegularExpressions.REGEX_NAME, true));
		this.contactPersonNo.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_TakafulProviderDialog_ContactPersonNo.value"),true));
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
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
		logger.debug("Leaving");
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
			this.country.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_Country.value"), null, true,true));
			this.province.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_Province.value"), null, true,true));
			this.city.setConstraint(new PTStringValidator(Labels.getLabel("label_TakafulProviderDialog_City.value"), null, true,true));
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.takafulCode.setConstraint("");
		this.country.setConstraint("");
		this.province.setConstraint("");
		this.city.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
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
		this.phone.setErrorMessage("");
		this.zipCode.setErrorMessage("");
		this.fax.setErrorMessage("");
		this.emailId.setErrorMessage("");
		this.webSite.setErrorMessage("");
		this.contactPerson.setErrorMessage("");
		this.contactPersonNo.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList(){
		final JdbcSearchObject<TakafulProvider> soTakafulProvider = getTakafulProviderListCtrl().getSearchObj();
		getTakafulProviderListCtrl().pagingTakafulProviderList.setActivePage(0);
		getTakafulProviderListCtrl().getPagedListWrapper().setSearchObject(soTakafulProvider);
		if(getTakafulProviderListCtrl().listBoxTakafulProvider!=null){
			getTakafulProviderListCtrl().listBoxTakafulProvider.getListModel();
		}
	} 


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close=true;
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_TakafulProviderDialog, "TakafulProviderDialog");	
		}

		logger.debug("Leaving") ;
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
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aTakafulProvider.getTakafulCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aTakafulProvider.getRecordType()).equals("")){
				aTakafulProvider.setVersion(aTakafulProvider.getVersion()+1);
				aTakafulProvider.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aTakafulProvider.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aTakafulProvider.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTakafulProvider.getNextTaskId(), aTakafulProvider);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aTakafulProvider,tranType)){
					refreshList();
					closeDialog(this.window_TakafulProviderDialog, "TakafulProviderDialog"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_TakafulProviderDialog,e);
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
		if(isReadOnly("TakafulProviderDialog_accountNumber")){
			this.accountNumber.setReadonly(true);
		}
		this.takafulRate.setReadonly(isReadOnly("TakafulProviderDialog_takafulRate"));
		this.establishedDate.setDisabled(isReadOnly("TakafulProviderDialog_establishedDate"));
		this.street.setReadonly(isReadOnly("TakafulProviderDialog_street"));
		this.houseNumber.setReadonly(isReadOnly("TakafulProviderDialog_houseNumber"));
		this.addrLine1.setReadonly(isReadOnly("TakafulProviderDialog_addrLine1"));
		this.addrLine2.setReadonly(isReadOnly("TakafulProviderDialog_addrLine2"));
		this.country.setReadonly(isReadOnly("TakafulProviderDialog_country"));
		this.province.setReadonly(isReadOnly("TakafulProviderDialog_province"));
		this.city.setReadonly(isReadOnly("TakafulProviderDialog_city"));
		this.zipCode.setReadonly(isReadOnly("TakafulProviderDialog_zipCode"));
		this.phone.setReadonly(isReadOnly("TakafulProviderDialog_phone"));
		this.fax.setReadonly(isReadOnly("TakafulProviderDialog_fax"));
		this.emailId.setReadonly(isReadOnly("TakafulProviderDialog_emailId"));
		this.webSite.setReadonly(isReadOnly("TakafulProviderDialog_webSite"));
		this.contactPerson.setReadonly(isReadOnly("TakafulProviderDialog_contactPerson"));
		this.contactPersonNo.setReadonly(isReadOnly("TakafulProviderDialog_contactPersonNo"));

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
			//btnCancel.setVisible(true);
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
		this.zipCode.setReadonly(true);
		this.phone.setReadonly(true);
		this.fax.setReadonly(true);
		this.emailId.setReadonly(true);
		this.webSite.setReadonly(true);
		this.contactPerson.setReadonly(true);
		this.contactPersonNo.setReadonly(true);
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
		this.phone.setValue("");
		this.zipCode.setValue("");
		this.fax.setValue("");
		this.emailId.setValue("");
		this.webSite.setValue("");
		this.contactPerson.setValue("");
		this.contactPersonNo.setValue("");
		this.takafulType.setSelectedIndex(0);
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

		if(isWorkFlowEnabled()){
			aTakafulProvider.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTakafulProvider.getNextTaskId(), aTakafulProvider);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aTakafulProvider.getRecordType()) && isValidation()) {
			doClearMessage();
			doSetValidation();
			// fill the TakafulProvider object with the components data
			doWriteComponentsToBean(aTakafulProvider);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aTakafulProvider.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aTakafulProvider.getRecordType()).equals("")){
				aTakafulProvider.setVersion(aTakafulProvider.getVersion()+1);
				if(isNew){
					aTakafulProvider.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aTakafulProvider.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTakafulProvider.setNewRecord(true);
				}
			}
		}else{
			aTakafulProvider.setVersion(aTakafulProvider.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aTakafulProvider,tranType)){
				//doWriteBeanToComponents(aTakafulProvider);
				refreshList();
				closeDialog(this.window_TakafulProviderDialog, "TakafulProviderDialog");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_TakafulProviderDialog,e);
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

	private boolean doProcess(TakafulProvider aTakafulProvider,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aTakafulProvider.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aTakafulProvider.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTakafulProvider.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			aTakafulProvider.setTaskId(getTaskId());
			aTakafulProvider.setNextTaskId(getNextTaskId());
			aTakafulProvider.setRoleCode(getRole());
			aTakafulProvider.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aTakafulProvider, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aTakafulProvider, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aTakafulProvider, tranType), null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param  AuditHeader auditHeader
	 * @param method  (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		TakafulProvider aTakafulProvider = (TakafulProvider) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getTakafulProviderService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getTakafulProviderService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getTakafulProviderService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aTakafulProvider.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getTakafulProviderService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aTakafulProvider.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_TakafulProviderDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_TakafulProviderDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("TakafulProvider",aTakafulProvider.getTakafulCode(),aTakafulProvider.getVersion()),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}		

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TakafulProvider aTakafulProvider, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTakafulProvider.getBefImage(), aTakafulProvider);   
		return new AuditHeader(String.valueOf(aTakafulProvider.getTakafulCode()),null,null,null,auditDetail,aTakafulProvider.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
