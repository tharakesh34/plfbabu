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
 * FileName    		:  CustomerPRelationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerprelation;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.customermasters.CustomerPRelationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerPRelation/customerPRelationDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerPRelationDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4928858312962975781L;
	private final static Logger logger = Logger.getLogger(CustomerPRelationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerPRelationDialog; // autowired

	protected Intbox 		pRCustPRSNo; 					// autowired
	protected Textbox 		pRRelationCode; 				// autowired
	protected Textbox 		pRRelationCustID; 				// autowired
	protected Checkbox 		pRisGuardian; 					// autowired
	protected Textbox 		pRFName; 						// autowired
	protected Textbox 		pRMName; 						// autowired
	protected Textbox 		pRLName; 						// autowired
	protected Textbox 		pRSName; 						// autowired
	protected Textbox 		pRFNameLclLng; 					// autowired
	protected Textbox 		pRMNameLclLng; 					// autowired
	protected Textbox 		pRLNameLclLng; 					// autowired
	protected Datebox 		pRDOB; 							// autowired
	protected Textbox 		pRAddrHNbr; 					// autowired
	protected Textbox 		pRAddrFNbr; 					// autowired
	protected Textbox 		pRAddrStreet; 					// autowired
	protected Textbox 		pRAddrLine1; 					// autowired
	protected Textbox 		pRAddrLine2; 					// autowired
	protected Textbox 		pRAddrPOBox; 					// autowired
	protected Textbox 		pRAddrCity; 					// autowired
	protected Textbox 		pRAddrProvince; 				// autowired
	protected Textbox 		pRAddrCountry; 					// autowired
	protected Textbox 		pRAddrZIP; 						// autowired
	protected Textbox 		pRPhone; 						// autowired
	protected Textbox 		pRMail; 						// autowired
	protected Longbox 		pRCustID;						// autowired
	protected Textbox 		custCIF;						// autowired
	protected Label 		custShrtName;					// autowired
	protected Label 		recordStatus; 					// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	
	// Space Id's Checking for Mandatory or not
	protected Space space_pRRelationCustID;
	// not auto wired vars
	private CustomerPRelation customerPRelation; // overhanded per param
	private transient CustomerPRelationListCtrl customerPRelationListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient int  			oldVar_pRCustPRSNo;
	private transient String  		oldVar_pRRelationCode;
	private transient String  		oldVar_pRRelationCustID;
	private transient boolean  		oldVar_pRisGuardian;
	private transient String  		oldVar_pRFName;
	private transient String  		oldVar_pRMName;
	private transient String  		oldVar_pRLName;
	private transient String  		oldVar_pRSName;
	private transient String  		oldVar_pRFNameLclLng;
	private transient String  		oldVar_pRMNameLclLng;
	private transient String  		oldVar_pRLNameLclLng;
	private transient Date  		oldVar_pRDOB;
	private transient String  		oldVar_pRAddrHNbr;
	private transient String  		oldVar_pRAddrFNbr;
	private transient String  		oldVar_pRAddrStreet;
	private transient String  		oldVar_pRAddrLine1;
	private transient String  		oldVar_pRAddrLine2;
	private transient String  		oldVar_pRAddrPOBox;
	private transient String  		oldVar_pRAddrCity;
	private transient String  		oldVar_pRAddrProvince;
	private transient String  		oldVar_pRAddrCountry;
	private transient String  		oldVar_pRAddrZIP;
	private transient String  		oldVar_pRPhone;
	private transient String  		oldVar_pRMail;
	private transient String 		oldVar_recordStatus;
	private transient long 			oldVar_pRCustID;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerPRelationDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire
	protected Button btnSearchPRCustid;

	protected Button btnSearchPRRelationCode; 	// autowire
	protected Textbox lovDescPRRelationCodeName;
	private transient String 		oldVar_lovDescPRRelationCodeName;

	protected Button btnSearchPRAddrCity; 		// autowire
	protected Textbox lovDescPRAddrCityName;
	private transient String 		oldVar_lovDescPRAddrCityName;

	protected Button btnSearchPRAddrProvince; 	// autowire
	protected Textbox lovDescPRAddrProvinceName;
	private transient String 		oldVar_lovDescPRAddrProvinceName;

	protected Button btnSearchPRAddrCountry; 	// autowire
	protected Textbox lovDescPRAddrCountryName;
	private transient String 		oldVar_lovDescPRAddrCountryName;

	// ServiceDAOs / Domain Classes
	private transient CustomerPRelationService customerPRelationService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerPRelation> customerPRelations;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private Boolean minor=false;
	private transient String CUSTCIF_REGEX;
	String parms[] = new String[4];
	private String moduleType="";
	
	/**
	 * default constructor.<br>
	 */
	public CustomerPRelationDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerPRelation object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerPRelationDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("customerPRelation")) {
			this.customerPRelation = (CustomerPRelation) args.get("customerPRelation");
			CustomerPRelation befImage =new CustomerPRelation();
			BeanUtils.copyProperties(this.customerPRelation, befImage);
			this.customerPRelation.setBefImage(befImage);

			setCustomerPRelation(this.customerPRelation);
		} else {
			setCustomerPRelation(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}

		if(getCustomerPRelation().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("customerDialogCtrl")){

			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerDialogCtrl"));
			setNewCustomer(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerPRelation.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), 
				"CustomerPRelationDialog");
			}
		}

		doLoadWorkFlow(this.customerPRelation.isWorkflow(),this.customerPRelation.getWorkflowId(),
				this.customerPRelation.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerPRelationDialog");
		}

		// READ OVERHANDED params !
		// we get the customerPRelationListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerPRelation here.
		if (args.containsKey("customerPRelationListCtrl")) {
			setCustomerPRelationListCtrl((CustomerPRelationListCtrl) args.get("customerPRelationListCtrl"));
		} else {
			setCustomerPRelationListCtrl(null);
		}

		if (args.containsKey("isMinor")) {
			setMinor((Boolean) args.get("isMinor"));
		}else{
			setMinor(getCustomerPRelation().isPRisGuardian());
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerPRelation());
		this.pRCustPRSNo.setReadonly(true);

		//Calling SelectCtrl For proper selection of Customer
		if(isNewRecord() & !isNewCustomer()){
			onload();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.pRCustPRSNo.setMaxlength(10);
		this.pRRelationCode.setMaxlength(8);
		this.pRFName.setMaxlength(50);
		this.pRMName.setMaxlength(50);
		this.pRLName.setMaxlength(50);
		this.pRSName.setMaxlength(50);
		this.pRFNameLclLng.setMaxlength(50);
		this.pRMNameLclLng.setMaxlength(50);
		this.pRLNameLclLng.setMaxlength(50);
		this.pRDOB.setFormat(PennantConstants.dateFormat);
		this.pRAddrHNbr.setMaxlength(50);
		this.pRAddrFNbr.setMaxlength(50);
		this.pRAddrStreet.setMaxlength(50);
		this.pRAddrLine1.setMaxlength(50);
		this.pRAddrLine2.setMaxlength(50);
		this.pRAddrPOBox.setMaxlength(8);
		this.pRAddrCity.setMaxlength(8);
		this.pRAddrProvince.setMaxlength(8);
		this.pRAddrCountry.setMaxlength(2);
		this.pRAddrZIP.setMaxlength(50);
		this.pRPhone.setMaxlength(50);
		this.pRMail.setMaxlength(50);

		// Set Regexion For Customer CIF Field
		parms[0] = SystemParameterDetails.getSystemParameterValue("CIF_CHAR").toString();
		parms[1] = SystemParameterDetails.getSystemParameterValue("CIF_LENGTH").toString();

		this.CUSTCIF_REGEX = "[" + parms[0] + "]{" + parms[1] + "}";
		this.pRRelationCustID.setMaxlength(Integer.parseInt(parms[1]));

		if(isNewRecord()){
			this.btnSearchPRAddrProvince.setVisible(false);
			this.btnSearchPRAddrCity.setVisible(false);
		}

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerPRelationDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerPRelationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerPRelationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerPRelationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerPRelationDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CustomerPRelationDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
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
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Entering" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerPRelationDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnCheck CheckBox Events+++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onCheck$pRisGuardian(Event event) {
		gaurdianCheck(true);
	}

	/**
	 * Check Whether DateOfBirth field Entered or Not
	 * 
	 */
	private void gaurdianCheck(boolean isMinor) {
		logger.debug("Entering");
		if(isMinor){
			this.pRisGuardian.setDisabled(isReadOnly("CustomerPRelationDialog_pRisGuardian"));
			if (this.pRisGuardian.isChecked()) {
				this.space_pRRelationCustID.setStyle("background-color:red");
			} else {
				this.pRRelationCustID.clearErrorMessage();
				this.pRRelationCustID.setValue("");
				this.space_pRRelationCustID.setStyle("background-color:white");
			}
		}else{
			this.pRisGuardian.setChecked(false);
			this.pRisGuardian.setDisabled(true);
			this.space_pRRelationCustID.setStyle("background-color:white");
		}
		logger.debug("Leaving");
	}



	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
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
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		if(close){
			closeWindow();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewCustomer()){
			window_CustomerPRelationDialog.onClose();	
		}else{
			closeDialog(this.window_CustomerPRelationDialog, "CustomerPRelation");
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
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerPRelation
	 *            CustomerPRelation
	 */
	public void doWriteBeanToComponents(CustomerPRelation aCustomerPRelation) {
		logger.debug("Entering");

		if(aCustomerPRelation.getPRCustID()!=Long.MIN_VALUE){
			this.pRCustID.setValue(aCustomerPRelation.getPRCustID());
		}

		this.pRCustPRSNo.setValue(aCustomerPRelation.getPRCustPRSNo());
		this.pRRelationCode.setValue(aCustomerPRelation.getPRRelationCode());
		this.pRRelationCustID.setValue(StringUtils.trimToEmpty(aCustomerPRelation.getPRRelationCustID()));

		if(isMinor()){
			this.pRisGuardian.setChecked(aCustomerPRelation.isPRisGuardian());
		}
		gaurdianCheck(isMinor());

		this.pRFName.setValue(aCustomerPRelation.getPRFName());
		this.pRMName.setValue(aCustomerPRelation.getPRMName());
		this.pRLName.setValue(aCustomerPRelation.getPRLName());
		this.pRSName.setValue(aCustomerPRelation.getPRSName());
		this.pRFNameLclLng.setValue(aCustomerPRelation.getPRFNameLclLng());
		this.pRMNameLclLng.setValue(aCustomerPRelation.getPRMNameLclLng());
		this.pRLNameLclLng.setValue(aCustomerPRelation.getPRLNameLclLng());
		this.pRDOB.setValue(aCustomerPRelation.getPRDOB());
		this.pRAddrHNbr.setValue(aCustomerPRelation.getPRAddrHNbr());
		this.pRAddrFNbr.setValue(aCustomerPRelation.getPRAddrFNbr());
		this.pRAddrStreet.setValue(aCustomerPRelation.getPRAddrStreet());
		this.pRAddrLine1.setValue(aCustomerPRelation.getPRAddrLine1());
		this.pRAddrLine2.setValue(aCustomerPRelation.getPRAddrLine2());
		this.pRAddrPOBox.setValue(aCustomerPRelation.getPRAddrPOBox());
		this.pRAddrCity.setValue(aCustomerPRelation.getPRAddrCity());
		this.pRAddrProvince.setValue(aCustomerPRelation.getPRAddrProvince());
		this.pRAddrCountry.setValue(aCustomerPRelation.getPRAddrCountry());
		this.pRAddrZIP.setValue(aCustomerPRelation.getPRAddrZIP());
		this.pRPhone.setValue(aCustomerPRelation.getPRPhone());
		this.pRMail.setValue(aCustomerPRelation.getPRMail());
		this.custCIF.setValue(aCustomerPRelation.getLovDescCustCIF()==null?"":aCustomerPRelation.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerPRelation.getLovDescCustShrtName()==null?"":aCustomerPRelation.getLovDescCustShrtName().trim());

		if (isNewRecord()){
			this.lovDescPRRelationCodeName.setValue("");
			this.lovDescPRAddrCityName.setValue("");
			this.lovDescPRAddrProvinceName.setValue("");
			this.lovDescPRAddrCountryName.setValue("");
		}else{
			if(isNewCustomer()){
				this.lovDescPRRelationCodeName.setValue(aCustomerPRelation.getLovDescPRRelationCodeName());
				this.lovDescPRAddrCityName.setValue(aCustomerPRelation.getLovDescPRAddrCityName());
				this.lovDescPRAddrProvinceName.setValue(aCustomerPRelation.getLovDescPRAddrProvinceName());
				this.lovDescPRAddrCountryName.setValue(aCustomerPRelation.getLovDescPRAddrCountryName());

			}else{
				this.lovDescPRRelationCodeName.setValue(aCustomerPRelation.getPRRelationCode()+"-"+aCustomerPRelation.getLovDescPRRelationCodeName());
				this.lovDescPRAddrCityName.setValue(aCustomerPRelation.getPRAddrCity()+"-"+aCustomerPRelation.getLovDescPRAddrCityName());
				this.lovDescPRAddrProvinceName.setValue(aCustomerPRelation.getPRAddrProvince()+"-"+aCustomerPRelation.getLovDescPRAddrProvinceName());
				this.lovDescPRAddrCountryName.setValue(aCustomerPRelation.getPRAddrCountry()+"-"+aCustomerPRelation.getLovDescPRAddrCountryName());
			}
		}
		this.recordStatus.setValue(aCustomerPRelation.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerPRelation
	 */
	public void doWriteComponentsToBean(CustomerPRelation aCustomerPRelation) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCustomerPRelation.setPRCustID(this.pRCustID.longValue());
			aCustomerPRelation.setLovDescCustCIF(this.custCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRCustPRSNo(this.pRCustPRSNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setLovDescPRRelationCodeName(this.lovDescPRRelationCodeName.getValue());
			aCustomerPRelation.setPRRelationCode(this.pRRelationCode.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRRelationCustID(StringUtils.trimToEmpty(this.pRRelationCustID.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRisGuardian(this.pRisGuardian.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRFName(this.pRFName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRMName(this.pRMName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRLName(this.pRLName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRSName(this.pRSName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRFNameLclLng(this.pRFNameLclLng.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRMNameLclLng(this.pRMNameLclLng.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRLNameLclLng(this.pRLNameLclLng.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRDOB(this.pRDOB.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRAddrHNbr(this.pRAddrHNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRAddrFNbr(this.pRAddrFNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRAddrStreet(this.pRAddrStreet.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRAddrLine1(this.pRAddrLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRAddrLine2(this.pRAddrLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRAddrPOBox(this.pRAddrPOBox.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setLovDescPRAddrCityName(this.lovDescPRAddrCityName.getValue());
			aCustomerPRelation.setPRAddrCity(this.pRAddrCity.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setLovDescPRAddrProvinceName(this.lovDescPRAddrProvinceName.getValue());
			aCustomerPRelation.setPRAddrProvince(this.pRAddrProvince.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setLovDescPRAddrCountryName(this.lovDescPRAddrCountryName.getValue());
			aCustomerPRelation.setPRAddrCountry(this.pRAddrCountry.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRAddrZIP(this.pRAddrZIP.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRPhone(this.pRPhone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPRelation.setPRMail(this.pRMail.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCustomerPRelation.setRecordStatus(this.recordStatus.getValue());
		setCustomerPRelation(aCustomerPRelation);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerPRelation
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerPRelation aCustomerPRelation) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custCIF.focus();
			if (isNewCustomer()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerPRelation);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			if(isNewCustomer()){
				this.window_CustomerPRelationDialog.setHeight("60%");
				this.window_CustomerPRelationDialog.setWidth("85%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerPRelationDialog.doModal() ;
			}else{
				this.window_CustomerPRelationDialog.setWidth("100%");
				this.window_CustomerPRelationDialog.setHeight("100%");
				setDialog(this.window_CustomerPRelationDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Entering");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_pRCustID = this.pRCustID.longValue();
		this.oldVar_pRCustPRSNo = this.pRCustPRSNo.intValue();	
		this.oldVar_pRRelationCode = this.pRRelationCode.getValue();
		this.oldVar_lovDescPRRelationCodeName = this.lovDescPRRelationCodeName.getValue();
		this.oldVar_pRRelationCustID = this.pRRelationCustID.getValue();
		this.oldVar_pRisGuardian = this.pRisGuardian.isChecked();
		this.oldVar_pRFName = this.pRFName.getValue();
		this.oldVar_pRMName = this.pRMName.getValue();
		this.oldVar_pRLName = this.pRLName.getValue();
		this.oldVar_pRSName = this.pRSName.getValue();
		this.oldVar_pRFNameLclLng = this.pRFNameLclLng.getValue();
		this.oldVar_pRMNameLclLng = this.pRMNameLclLng.getValue();
		this.oldVar_pRLNameLclLng = this.pRLNameLclLng.getValue();
		this.oldVar_pRDOB = this.pRDOB.getValue();
		this.oldVar_pRAddrHNbr = this.pRAddrHNbr.getValue();
		this.oldVar_pRAddrFNbr = this.pRAddrFNbr.getValue();
		this.oldVar_pRAddrStreet = this.pRAddrStreet.getValue();
		this.oldVar_pRAddrLine1 = this.pRAddrLine1.getValue();
		this.oldVar_pRAddrLine2 = this.pRAddrLine2.getValue();
		this.oldVar_pRAddrPOBox = this.pRAddrPOBox.getValue();
		this.oldVar_pRAddrCity = this.pRAddrCity.getValue();
		this.oldVar_lovDescPRAddrCityName = this.lovDescPRAddrCityName.getValue();
		this.oldVar_pRAddrProvince = this.pRAddrProvince.getValue();
		this.oldVar_lovDescPRAddrProvinceName = this.lovDescPRAddrProvinceName.getValue();
		this.oldVar_pRAddrCountry = this.pRAddrCountry.getValue();
		this.oldVar_lovDescPRAddrCountryName = this.lovDescPRAddrCountryName.getValue();
		this.oldVar_pRAddrZIP = this.pRAddrZIP.getValue();
		this.oldVar_pRPhone = this.pRPhone.getValue();
		this.oldVar_pRMail = this.pRMail.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.pRCustID.setValue(this.oldVar_pRCustID);
		this.pRCustPRSNo.setValue(this.oldVar_pRCustPRSNo);
		this.pRRelationCode.setValue(this.oldVar_pRRelationCode);
		this.lovDescPRRelationCodeName.setValue(this.oldVar_lovDescPRRelationCodeName);
		this.pRRelationCustID.setValue(this.oldVar_pRRelationCustID);
		this.pRisGuardian.setChecked(this.oldVar_pRisGuardian);
		this.pRFName.setValue(this.oldVar_pRFName);
		this.pRMName.setValue(this.oldVar_pRMName);
		this.pRLName.setValue(this.oldVar_pRLName);
		this.pRSName.setValue(this.oldVar_pRSName);
		this.pRFNameLclLng.setValue(this.oldVar_pRFNameLclLng);
		this.pRMNameLclLng.setValue(this.oldVar_pRMNameLclLng);
		this.pRLNameLclLng.setValue(this.oldVar_pRLNameLclLng);
		this.pRDOB.setValue(this.oldVar_pRDOB);
		this.pRAddrHNbr.setValue(this.oldVar_pRAddrHNbr);
		this.pRAddrFNbr.setValue(this.oldVar_pRAddrFNbr);
		this.pRAddrStreet.setValue(this.oldVar_pRAddrStreet);
		this.pRAddrLine1.setValue(this.oldVar_pRAddrLine1);
		this.pRAddrLine2.setValue(this.oldVar_pRAddrLine2);
		this.pRAddrPOBox.setValue(this.oldVar_pRAddrPOBox);
		this.pRAddrCity.setValue(this.oldVar_pRAddrCity);
		this.lovDescPRAddrCityName.setValue(this.oldVar_lovDescPRAddrCityName);
		this.pRAddrProvince.setValue(this.oldVar_pRAddrProvince);
		this.lovDescPRAddrProvinceName.setValue(this.oldVar_lovDescPRAddrProvinceName);
		this.pRAddrCountry.setValue(this.oldVar_pRAddrCountry);
		this.lovDescPRAddrCountryName.setValue(this.oldVar_lovDescPRAddrCountryName);
		this.pRAddrZIP.setValue(this.oldVar_pRAddrZIP);
		this.pRPhone.setValue(this.oldVar_pRPhone);
		this.pRMail.setValue(this.oldVar_pRMail);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
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

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_pRCustID != this.pRCustID.longValue()) {
			return true;
		}
		if (this.oldVar_pRCustPRSNo != this.pRCustPRSNo.intValue()) {
			return true;
		}
		if (this.oldVar_pRRelationCode != this.pRRelationCode.getValue()) {
			return true;
		}
		if (this.oldVar_pRRelationCustID != this.pRRelationCustID.getValue()) {
			return true;
		}
		if (this.oldVar_pRisGuardian != this.pRisGuardian.isChecked()) {
			return true;
		}
		if (this.oldVar_pRFName != this.pRFName.getValue()) {
			return true;
		}
		if (this.oldVar_pRMName != this.pRMName.getValue()) {
			return true;
		}
		if (this.oldVar_pRLName != this.pRLName.getValue()) {
			return true;
		}
		if (this.oldVar_pRSName != this.pRSName.getValue()) {
			return true;
		}
		if (this.oldVar_pRFNameLclLng != this.pRFNameLclLng.getValue()) {
			return true;
		}
		if (this.oldVar_pRMNameLclLng != this.pRMNameLclLng.getValue()) {
			return true;
		}
		if (this.oldVar_pRLNameLclLng != this.pRLNameLclLng.getValue()) {
			return true;
		}
		String oldPRDOB = "";
		String newPRDOB ="";
		if (this.oldVar_pRDOB!=null){
			oldPRDOB=DateUtility.formatDate(this.oldVar_pRDOB,PennantConstants.dateFormat);
		}
		if (this.pRDOB.getValue()!=null){
			newPRDOB=DateUtility.formatDate(this.pRDOB.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldPRDOB).equals(StringUtils.trimToEmpty(newPRDOB))) {
			return true;
		}
		if (this.oldVar_pRAddrHNbr != this.pRAddrHNbr.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrFNbr != this.pRAddrFNbr.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrStreet != this.pRAddrStreet.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrLine1 != this.pRAddrLine1.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrLine2 != this.pRAddrLine2.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrPOBox != this.pRAddrPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrCity != this.pRAddrCity.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrProvince != this.pRAddrProvince.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrCountry != this.pRAddrCountry.getValue()) {
			return true;
		}
		if (this.oldVar_pRAddrZIP != this.pRAddrZIP.getValue()) {
			return true;
		}
		if (this.oldVar_pRPhone != this.pRPhone.getValue()) {
			return true;
		}
		if (this.oldVar_pRMail != this.pRMail.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();
		setValidationOn(true);

		if (!this.pRCustID.isReadonly()){
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_CustomerPRelationDialog_PRCustID.value")}));
		}	
		if (!this.pRFName.isReadonly()){
			this.pRFName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRFName.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}	
		if (!this.pRMName.isReadonly()){
			if(!StringUtils.trimToEmpty(this.pRMName.getValue()).equals("")){
				this.pRMName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRMName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
		}
		if(this.pRisGuardian.isChecked()){
			this.pRRelationCustID.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX,Labels.getLabel(
					"MAND_FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel(
					"label_CustomerDialog_CustCIF.value"),parms[0], parms[1] })));
		}
		if (!this.pRLName.isReadonly()){
			this.pRLName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRLName.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}	
		if (!this.pRSName.isReadonly()){
			this.pRSName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRSName.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}	
		if(!this.pRDOB.isReadonly()){
			this.pRDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY",
					new String[]{Labels.getLabel("label_CustomerPRelationDialog_PRDOB.value")}));
		}
		boolean addressConstraint = false;
		if (StringUtils.trimToEmpty(this.pRAddrHNbr.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.pRAddrFNbr.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.pRAddrStreet.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.pRAddrLine1.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.pRAddrLine2.getValue()).equals("")) {
			addressConstraint = true;
		}
		if (addressConstraint) {
			this.pRAddrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRAddrHNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}	
		if (addressConstraint) {
			this.pRAddrFNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRAddrFNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (addressConstraint) {
			this.pRAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRAddrStreet.value"),PennantRegularExpressions.REGEX_ADDRESS, true));
		}	
		if (addressConstraint) {
			this.pRAddrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRAddrLine1.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (addressConstraint) {
			this.pRAddrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRAddrLine2.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		if (!this.pRPhone.isReadonly()){
			this.pRPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CustomerPRelationDialog_PRPhone.value"),true));
		}	
		if (!this.pRAddrPOBox.isReadonly()){
			this.pRAddrPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRAddrPOBox.value"), PennantRegularExpressions.REGEX_ALPHANUM, false));
		}	
		if (!this.pRAddrZIP.isReadonly()){
				this.pRAddrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPRelationDialog_PRAddrZIP.value"), PennantRegularExpressions.REGEX_ZIP, false));
		}	
		if (!this.pRMail.isReadonly()){
			this.pRMail.setConstraint(new PTEmailValidator(Labels.getLabel("label_CustomerPRelationDialog_PRMail.value"),false));
		}
		if(!this.pRRelationCustID.getValue().equals("")){
			this.pRRelationCustID.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX,
					Labels.getLabel("FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel("label_CustomerPRelationDialog_PRRelationCustID.value"),
							SystemParameterDetails.getSystemParameterValue("CIF_CHAR").toString(),
							SystemParameterDetails.getSystemParameterValue("CIF_LENGTH").toString() })));
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
		this.pRCustPRSNo.setConstraint("");
		this.pRRelationCustID.setConstraint("");
		this.pRFName.setConstraint("");
		this.pRMName.setConstraint("");
		this.pRLName.setConstraint("");
		this.pRSName.setConstraint("");
		this.pRFNameLclLng.setConstraint("");
		this.pRMNameLclLng.setConstraint("");
		this.pRLNameLclLng.setConstraint("");
		this.pRDOB.setConstraint("");
		this.pRAddrHNbr.setConstraint("");
		this.pRAddrFNbr.setConstraint("");
		this.pRAddrStreet.setConstraint("");
		this.pRAddrLine1.setConstraint("");
		this.pRAddrLine2.setConstraint("");
		this.pRAddrPOBox.setConstraint("");
		this.pRAddrZIP.setConstraint("");
		this.pRPhone.setConstraint("");
		this.pRMail.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		this.lovDescPRRelationCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerPRelationDialog_PRRelationCode.value")}));

		this.lovDescPRAddrCityName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerPRelationDialog_PRAddrCity.value")}));

		this.lovDescPRAddrProvinceName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerPRelationDialog_PRAddrProvince.value")}));

		this.lovDescPRAddrCountryName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerPRelationDialog_PRAddrCountry.value")}));

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescPRRelationCodeName.setConstraint("");
		this.lovDescPRAddrCityName.setConstraint("");
		this.lovDescPRAddrProvinceName.setConstraint("");
		this.lovDescPRAddrCountryName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.pRCustPRSNo.setErrorMessage("");
		this.pRRelationCustID.setErrorMessage("");
		this.pRFName.setErrorMessage("");
		this.pRMName.setErrorMessage("");
		this.pRLName.setErrorMessage("");
		this.pRSName.setErrorMessage("");
		this.pRFNameLclLng.setErrorMessage("");
		this.pRMNameLclLng.setErrorMessage("");
		this.pRLNameLclLng.setErrorMessage("");
		this.pRDOB.setErrorMessage("");
		this.pRAddrHNbr.setErrorMessage("");
		this.pRAddrFNbr.setErrorMessage("");
		this.pRAddrStreet.setErrorMessage("");
		this.pRAddrLine1.setErrorMessage("");
		this.pRAddrLine2.setErrorMessage("");
		this.pRAddrPOBox.setErrorMessage("");
		this.pRAddrZIP.setErrorMessage("");
		this.pRPhone.setErrorMessage("");
		this.pRMail.setErrorMessage("");
		this.lovDescPRRelationCodeName.setErrorMessage("");
		this.lovDescPRAddrCityName.setErrorMessage("");
		this.lovDescPRAddrProvinceName.setErrorMessage("");
		this.lovDescPRAddrCountryName.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CustomerPRelation> soCustomerPRelation = getCustomerPRelationListCtrl().getSearchObj();
		getCustomerPRelationListCtrl().pagingCustomerPRelationList.setActivePage(0);
		getCustomerPRelationListCtrl().getPagedListWrapper().setSearchObject(soCustomerPRelation);
		if (getCustomerPRelationListCtrl().listBoxCustomerPRelation != null) {
			getCustomerPRelationListCtrl().listBoxCustomerPRelation.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerPRelation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final CustomerPRelation aCustomerPRelation = new CustomerPRelation();
		BeanUtils.copyProperties(getCustomerPRelation(), aCustomerPRelation);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + 
		"\n\n --> " + aCustomerPRelation.getPRCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerPRelation.getRecordType()).equals("")){
				aCustomerPRelation.setVersion(aCustomerPRelation.getVersion()+1);
				aCustomerPRelation.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aCustomerPRelation.setNewRecord(true);

				if (isWorkFlowEnabled()){
					aCustomerPRelation.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCustomerProcess(aCustomerPRelation,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPRelationDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						//getCustomerDialogCtrl().doFillCustomerPRelations(this.customerPRelations);
						// send the data back to customer
						closeWindow();
					}	
				}else if(doProcess(aCustomerPRelation,tranType)){
					refreshList();
					closeWindow();
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerPRelation object. <br>
	 */
	private void doNew() {
		logger.debug("Leaving");
		// remember the old vars
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerPRelation() in the frontend.
		// we get it from the backend.
		final CustomerPRelation aCustomerPRelation = getCustomerPRelationService().getNewCustomerPRelation();
		setCustomerPRelation(aCustomerPRelation);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.pRRelationCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()){

			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
		}else{
			this.btnCancel.setVisible(true);
			this.pRCustID.setReadonly(true);
			this.btnSearchPRCustid.setVisible(false);
		}

		this.pRCustPRSNo.setReadonly(true);
		this.btnSearchPRRelationCode.setDisabled(isReadOnly("CustomerPRelationDialog_pRRelationCode"));
		this.pRRelationCustID.setReadonly(isReadOnly("CustomerPRelationDialog_pRRelationCustID"));
		this.pRisGuardian.setDisabled(isReadOnly("CustomerPRelationDialog_pRisGuardian"));
		this.pRFName.setReadonly(isReadOnly("CustomerPRelationDialog_pRFName"));
		this.pRMName.setReadonly(isReadOnly("CustomerPRelationDialog_pRMName"));
		this.pRLName.setReadonly(isReadOnly("CustomerPRelationDialog_pRLName"));
		this.pRSName.setReadonly(isReadOnly("CustomerPRelationDialog_pRSName"));
		this.pRFNameLclLng.setReadonly(isReadOnly("CustomerPRelationDialog_pRFNameLclLng"));
		this.pRMNameLclLng.setReadonly(isReadOnly("CustomerPRelationDialog_pRMNameLclLng"));
		this.pRLNameLclLng.setReadonly(isReadOnly("CustomerPRelationDialog_pRLNameLclLng"));
		this.pRDOB.setDisabled(isReadOnly("CustomerPRelationDialog_pRDOB"));
		this.pRAddrHNbr.setReadonly(isReadOnly("CustomerPRelationDialog_pRAddrHNbr"));
		this.pRAddrFNbr.setReadonly(isReadOnly("CustomerPRelationDialog_pRAddrFNbr"));
		this.pRAddrStreet.setReadonly(isReadOnly("CustomerPRelationDialog_pRAddrStreet"));
		this.pRAddrLine1.setReadonly(isReadOnly("CustomerPRelationDialog_pRAddrLine1"));
		this.pRAddrLine2.setReadonly(isReadOnly("CustomerPRelationDialog_pRAddrLine2"));
		this.pRAddrPOBox.setReadonly(isReadOnly("CustomerPRelationDialog_pRAddrPOBox"));
		this.btnSearchPRAddrCity.setDisabled(isReadOnly("CustomerPRelationDialog_pRAddrCity"));
		this.btnSearchPRAddrProvince.setDisabled(isReadOnly("CustomerPRelationDialog_pRAddrProvince"));
		this.btnSearchPRAddrCountry.setDisabled(isReadOnly("CustomerPRelationDialog_pRAddrCountry"));
		this.pRAddrZIP.setReadonly(isReadOnly("CustomerPRelationDialog_pRAddrZIP"));
		this.pRPhone.setReadonly(isReadOnly("CustomerPRelationDialog_pRPhone"));
		this.pRMail.setReadonly(isReadOnly("CustomerPRelationDialog_pRMail"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerPRelation.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if(newCustomer){
				if("ENQ".equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewCustomer()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.pRCustPRSNo.setReadonly(true);
		this.btnSearchPRRelationCode.setDisabled(true);
		this.pRRelationCustID.setReadonly(true);
		this.pRisGuardian.setDisabled(true);
		this.pRFName.setReadonly(true);
		this.pRMName.setReadonly(true);
		this.pRLName.setReadonly(true);
		this.pRSName.setReadonly(true);
		this.pRFNameLclLng.setReadonly(true);
		this.pRMNameLclLng.setReadonly(true);
		this.pRLNameLclLng.setReadonly(true);
		this.pRDOB.setDisabled(true);
		this.pRAddrHNbr.setReadonly(true);
		this.pRAddrFNbr.setReadonly(true);
		this.pRAddrStreet.setReadonly(true);
		this.pRAddrLine1.setReadonly(true);
		this.pRAddrLine2.setReadonly(true);
		this.pRAddrPOBox.setReadonly(true);
		this.btnSearchPRAddrCity.setDisabled(true);
		this.btnSearchPRAddrProvince.setDisabled(true);
		this.btnSearchPRAddrCountry.setDisabled(true);
		this.pRAddrZIP.setReadonly(true);
		this.pRPhone.setReadonly(true);
		this.pRMail.setReadonly(true);
		this.btnSearchPRCustid.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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
		this.pRCustID.setText("0");
		this.custCIF.setValue("");
		this.custShrtName.setValue("");
		this.pRCustPRSNo.setText("");
		this.pRRelationCode.setValue("");
		this.lovDescPRRelationCodeName.setValue("");
		this.pRRelationCustID.setText("");
		this.pRisGuardian.setChecked(false);
		this.pRFName.setValue("");
		this.pRMName.setValue("");
		this.pRLName.setValue("");
		this.pRSName.setValue("");
		this.pRFNameLclLng.setValue("");
		this.pRMNameLclLng.setValue("");
		this.pRLNameLclLng.setValue("");
		this.pRDOB.setText("");
		this.pRAddrHNbr.setValue("");
		this.pRAddrFNbr.setValue("");
		this.pRAddrStreet.setValue("");
		this.pRAddrLine1.setValue("");
		this.pRAddrLine2.setValue("");
		this.pRAddrPOBox.setValue("");
		this.pRAddrCity.setValue("");
		this.lovDescPRAddrCityName.setValue("");
		this.pRAddrProvince.setValue("");
		this.lovDescPRAddrProvinceName.setValue("");
		this.pRAddrCountry.setValue("");
		this.lovDescPRAddrCountryName.setValue("");
		this.pRAddrZIP.setValue("");
		this.pRPhone.setValue("");
		this.pRMail.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerPRelation aCustomerPRelation = new CustomerPRelation();
		BeanUtils.copyProperties(getCustomerPRelation(), aCustomerPRelation);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerPRelation object with the components data
		doWriteComponentsToBean(aCustomerPRelation);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerPRelation.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerPRelation.getRecordType()).equals("")){
				aCustomerPRelation.setVersion(aCustomerPRelation.getVersion()+1);
				if(isNew){
					aCustomerPRelation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerPRelation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerPRelation.setNewRecord(true);
				}
			}
		}else{
			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerPRelation.setVersion(1);
					aCustomerPRelation.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerPRelation.getRecordType()).equals("")){
					aCustomerPRelation.setVersion(aCustomerPRelation.getVersion()+1);
					aCustomerPRelation.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerPRelation.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerPRelation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerPRelation.setVersion(aCustomerPRelation.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if(isNewCustomer()){
				AuditHeader auditHeader =  newCustomerProcess(aCustomerPRelation,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPRelationDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					//getCustomerDialogCtrl().doFillCustomerPRelations(this.customerPRelations);
					// send the data back to customer
					closeWindow();
				}
			}else if(doProcess(aCustomerPRelation,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeWindow();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCustomerProcess(CustomerPRelation aCustomerPRelation,String tranType){
		return null;} 

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerPRelation (CustomerPRelation)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerPRelation aCustomerPRelation,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCustomerPRelation.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerPRelation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerPRelation.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerPRelation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerPRelation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerPRelation);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aCustomerPRelation))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCustomerPRelation.setTaskId(taskId);
			aCustomerPRelation.setNextTaskId(nextTaskId);
			aCustomerPRelation.setRoleCode(getRole());
			aCustomerPRelation.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerPRelation, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerPRelation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerPRelation, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerPRelation, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		CustomerPRelation  aCustomerPRelation= (CustomerPRelation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while(retValue==PennantConstants.porcessOVERIDE){
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerPRelationService().delete(auditHeader);
						deleteNotes = true;
					}else{
						auditHeader = getCustomerPRelationService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCustomerPRelationService().doApprove(auditHeader);
						if (aCustomerPRelation.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCustomerPRelationService().doReject(auditHeader);
						if (aCustomerPRelation.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(
								this.window_CustomerPRelationDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(
						this.window_CustomerPRelationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchPRRelationCode(Event event){
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerPRelationDialog,"PRelationCode");
		if (dataObject instanceof String){
			this.pRRelationCode.setValue(dataObject.toString());
			this.lovDescPRRelationCodeName.setValue("");
		}else{
			PRelationCode details= (PRelationCode) dataObject;
			if (details != null) {
				this.pRRelationCode.setValue(details.getLovValue());
				this.lovDescPRRelationCodeName.setValue(details.getLovValue()+"-"+details.getPRelationDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchPRAddrCity(Event event){
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("PCProvince", this.pRAddrProvince.getValue(), Filter.OP_EQUAL);  

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerPRelationDialog,"City",filters );
		if (dataObject instanceof String){
			this.pRAddrCity.setValue(dataObject.toString());
			this.lovDescPRAddrCityName.setValue("");
		}else{
			City details= (City) dataObject;
			if (details != null) {
				this.pRAddrCity.setValue(StringUtils.trimToEmpty(details.getPCCity()));
				this.lovDescPRAddrCityName.setValue(StringUtils.trimToEmpty(details.getPCCity())+"-"+details.getPCCityName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchPRAddrProvince(Event event){
		logger.debug("Entering" + event.toString());

		String sPRAddrProvince= this.pRAddrProvince.getValue();
		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("CPCountry", this.pRAddrCountry.getValue(), Filter.OP_EQUAL);  

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerPRelationDialog,"Province",filters);
		if (dataObject instanceof String){
			this.pRAddrProvince.setValue(dataObject.toString());
			this.lovDescPRAddrProvinceName.setValue("");
		}else{
			Province details= (Province) dataObject;
			if (details != null) {
				this.pRAddrProvince.setValue(details.getCPProvince());
				this.lovDescPRAddrProvinceName.setValue(details.getLovValue()+"-"+details.getCPProvinceName());
			}
		}

		if (!StringUtils.trimToEmpty(sPRAddrProvince).equals(this.pRAddrProvince.getValue())){
			this.pRAddrCity.setValue("");
			this.lovDescPRAddrCityName.setValue("");
			this.btnSearchPRAddrCity.setVisible(false);
		}

		if(!this.lovDescPRAddrProvinceName.getValue().equals("")){		   
			this.btnSearchPRAddrCity.setVisible(true);		   
		}else{
			this.lovDescPRAddrCityName.setValue("");
			this.btnSearchPRAddrCity.setVisible(false);	
		}
		logger.debug("Leaving" + event.toString()); 
	}

	public void onClick$btnSearchPRAddrCountry(Event event){
		logger.debug("Entering" + event.toString());

		String sPRAddrCountry= this.pRAddrCountry.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerPRelationDialog,"Country");
		if (dataObject instanceof String){
			this.pRAddrCountry.setValue(dataObject.toString());
			this.lovDescPRAddrCountryName.setValue("");
		}else{
			Country details= (Country) dataObject;
			if (details != null) {
				this.pRAddrCountry.setValue(details.getLovValue());
				this.lovDescPRAddrCountryName.setValue(details.getLovValue()+"-"+details.getCountryDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sPRAddrCountry).equals(this.pRAddrCountry.getValue())){
			this.pRAddrProvince.setValue("");
			this.lovDescPRAddrProvinceName.setValue("");
			this.pRAddrCity.setValue("");
			this.lovDescPRAddrCityName.setValue("");
			this.btnSearchPRAddrCity.setVisible(false);
		}

		if(!this.lovDescPRAddrCountryName.getValue().equals("")){		   
			this.btnSearchPRAddrProvince.setVisible(true);		   
		}else{		   
			this.lovDescPRAddrProvinceName.setValue("");
			this.lovDescPRAddrCityName.setValue("");
			this.btnSearchPRAddrProvince.setVisible(false);	
			this.btnSearchPRAddrCity.setVisible(false);	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("custCtgType","I");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",	null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 
		this.pRCustID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		if(aCustomer.isCustIsMinor()){
			this.pRisGuardian.setChecked(aCustomer.isCustIsMinor());
		}
		gaurdianCheck(aCustomer.isCustIsMinor());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerPRelation aCustomerPRelation, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerPRelation.getBefImage(), aCustomerPRelation);

		return new AuditHeader(getReference(),String.valueOf(aCustomerPRelation.getPRCustID()), null,
				null, auditDetail, aCustomerPRelation.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerPRelationDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
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

		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerPRelation");
		notes.setReference(getReference());
		notes.setVersion(getCustomerPRelation().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return String.valueOf(getCustomerPRelation().getPRCustID())+
		PennantConstants.KEY_SEPERATOR+String.valueOf(getCustomerPRelation().getPRCustPRSNo());
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CustomerPRelation getCustomerPRelation() {
		return this.customerPRelation;
	}
	public void setCustomerPRelation(CustomerPRelation customerPRelation) {
		this.customerPRelation = customerPRelation;
	}

	public void setCustomerPRelationService(CustomerPRelationService customerPRelationService) {
		this.customerPRelationService = customerPRelationService;
	}
	public CustomerPRelationService getCustomerPRelationService() {
		return this.customerPRelationService;
	}

	public void setCustomerPRelationListCtrl(CustomerPRelationListCtrl customerPRelationListCtrl) {
		this.customerPRelationListCtrl = customerPRelationListCtrl;
	}
	public CustomerPRelationListCtrl getCustomerPRelationListCtrl() {
		return this.customerPRelationListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}
	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
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

	public void setCustomerPRelations(List<CustomerPRelation> customerPRelations) {
		this.customerPRelations = customerPRelations;
	}
	public List<CustomerPRelation> getCustomerPRelations() {
		return customerPRelations;
	}

	public Boolean isMinor() {
		return minor;
	}

	public void setMinor(Boolean minor) {
		this.minor = minor;
	}

}