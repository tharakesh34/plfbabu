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
 * FileName    		:  CustomerAddresDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeraddres;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerListCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerAddres/customerAddresDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerAddresDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -221443986307588127L;
	private final static Logger logger = Logger.getLogger(CustomerAddresDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CustomerAddresDialog; 	// autoWired

	protected Longbox 	custID; 						// autoWired
	protected ExtendedCombobox 	custAddrType; 					// autoWired
	protected Textbox 	custAddrHNbr; 					// autoWired
	protected Textbox 	custFlatNbr; 					// autoWired
	protected Textbox 	custAddrStreet; 				// autoWired
	protected Textbox 	custAddrLine1; 					// autoWired
	protected Textbox 	custAddrLine2; 					// autoWired
	protected Textbox 	custPOBox; 						// autoWired
	protected ExtendedCombobox 	custAddrCountry; 				// autoWired
	protected ExtendedCombobox 	custAddrProvince; 				// autoWired
	protected ExtendedCombobox 	custAddrCity; 					// autoWired
	protected Textbox 	custAddrZIP; 					// autoWired
	protected Textbox 	custAddrPhone; 					// autoWired
	protected Textbox 	custCIF;						// autoWired
	protected Label 	custShrtName;					// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Label 		CustomerSname;				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South			south;

	// not autoWired variables
	private CustomerAddres customerAddres; // overHanded per parameter
	private transient CustomerAddresListCtrl customerAddresListCtrl; // overHanded

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long 		oldVar_custID;
	private transient String 	oldVar_custAddrType;
	private transient String 	oldVar_custAddrHNbr;
	private transient String 	oldVar_custFlatNbr;
	private transient String 	oldVar_custAddrStreet;
	private transient String 	oldVar_custAddrLine1;
	private transient String 	oldVar_custAddrLine2;
	private transient String 	oldVar_custPOBox;
	private transient String 	oldVar_custAddrCountry;
	private transient String 	oldVar_custAddrProvince;
	private transient String 	oldVar_custAddrCity;
	private transient String 	oldVar_custAddrZIP;
	private transient String 	oldVar_custAddrPhone;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerAddresDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 				// autoWired
	protected Button btnEdit; 				// autoWired
	protected Button btnDelete; 			// autoWired
	protected Button btnSave; 				// autoWired
	protected Button btnCancel; 			// autoWired
	protected Button btnClose; 				// autoWired
	protected Button btnHelp; 				// autoWired
	protected Button btnNotes; 				// autoWired
	protected Button btnSearchPRCustid; 	// autoWired

	private transient String 	oldVar_lovDescCustAddrTypeName;
	
	private transient String 	oldVar_lovDescCustAddrCountryName;
	
	private transient String 	oldVar_lovDescCustAddrProvinceName;
	
	private transient String 	oldVar_lovDescCustAddrCityName;

	// ServiceDAOs / Domain Classes
	private transient CustomerAddresService customerAddresService;
	protected transient CustomerListCtrl customerListCtrl;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;
	
	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerAddres> customerAddress;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType="";
    private transient String  mortgAddrCountryTemp;
    private transient String  mortgAddrProvinceTemp;
    private String userRole="";
	/**
	 * default constructor.<br>
	 */
	public CustomerAddresDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerAddres object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerAddresDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit,this.btnDelete, this.btnSave, 
				this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("customerAddres")) {
			this.customerAddres = (CustomerAddres) args.get("customerAddres");
			CustomerAddres befImage = new CustomerAddres();
			BeanUtils.copyProperties(this.customerAddres, befImage);
			this.customerAddres.setBefImage(befImage);
			setCustomerAddres(this.customerAddres);
		} else {
			setCustomerAddres(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}

		if(getCustomerAddres().isNewRecord()){
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

			this.customerAddres.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerAddresDialog");
			}
		}
		
		doLoadWorkFlow(this.customerAddres.isWorkflow(),
				this.customerAddres.getWorkflowId(),this.customerAddres.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"CustomerAddresDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerAddresListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerAddres here.
		if (args.containsKey("customerAddresListCtrl")) {
			setCustomerAddresListCtrl((CustomerAddresListCtrl) args.get("customerAddresListCtrl"));
		} else {
			setCustomerAddresListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerAddres());
		
		//Calling SelectCtrl For proper selection of Customer
		if(isNewRecord() & !isNewCustomer()){
			onLoad();
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.custAddrType.setMaxlength(8);
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
		
		this.custAddrCountry.setMaxlength(2);
		this.custAddrCountry.setMandatoryStyle(true);
		this.custAddrCountry.setModuleName("Country");
		this.custAddrCountry.setValueColumn("CountryCode");
		this.custAddrCountry.setDescColumn("CountryDesc");
		this.custAddrCountry.setValidateColumns(new String[] { "CountryCode" });
		
		this.custAddrProvince.setMaxlength(8);
		this.custAddrProvince.setMandatoryStyle(true);
		this.custAddrProvince.setModuleName("Province");
		this.custAddrProvince.setValueColumn("CPProvince");
		this.custAddrProvince.setDescColumn("CPProvinceName");
		this.custAddrProvince.setValidateColumns(new String[] { "CPProvince" });

		this.custAddrCity.setMaxlength(8);
		this.custAddrCity.setMandatoryStyle(true);
		this.custAddrCity.setModuleName("City");
		this.custAddrCity.setValueColumn("PCCity");
		this.custAddrCity.setDescColumn("PCCityName");
		this.custAddrCity.setValidateColumns(new String[] { "PCCity" });
		
		this.custAddrZIP.setMaxlength(50);
		this.custAddrPhone.setMaxlength(50);

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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerAddresDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerAddresDialog_btnSave"));
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
	public void onClose$window_CustomerAddresDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerAddresDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" +event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" +event.toString());
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
			logger.debug("doClose isDataChanged(): true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if (close) {
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
			closePopUpWindow(this.window_CustomerAddresDialog,"CustomerAddresDialog");
		}else{
			closeDialog(this.window_CustomerAddresDialog, "CustomerAddresDialog");
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
	 * @param aCustomerAddres
	 *            CustomerAddres
	 */
	public void doWriteBeanToComponents(CustomerAddres aCustomerAddres) throws Exception {
		logger.debug("Entering");
		
		if(aCustomerAddres.getCustID()!=Long.MIN_VALUE){
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
		this.custAddrZIP.setValue(aCustomerAddres.getCustAddrZIP());
		this.custAddrPhone.setValue(aCustomerAddres.getCustAddrPhone());
		this.custCIF.setValue(aCustomerAddres.getLovDescCustCIF()==null?"":
			aCustomerAddres.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerAddres.getLovDescCustShrtName()==null?"":
			aCustomerAddres.getLovDescCustShrtName().trim());

		if (aCustomerAddres.getCustAddrType() == null) {
			this.custAddrType.setDescription("");
			this.custAddrCountry.setDescription("");
			this.custAddrProvince.setDescription("");
			this.custAddrCity.setDescription("");
			
		} else {
			this.custAddrType.setDescription(aCustomerAddres.getLovDescCustAddrTypeName());
			this.custAddrCountry.setDescription(aCustomerAddres.getLovDescCustAddrCountryName());
			this.custAddrProvince.setDescription(aCustomerAddres.getLovDescCustAddrProvinceName());
			this.custAddrCity.setDescription(aCustomerAddres.getLovDescCustAddrCityName());
			this.custAddrType.setReadonly(true);
		}
		
		mortgAddrCountryTemp = this.custAddrCountry.getValue();
		mortgAddrProvinceTemp = this.custAddrProvince.getValue();
		doSetProvProp();
		doSetCityProp();
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
			aCustomerAddres.setLovDescCustAddrCityName(this.custAddrCity.getDescription());
			aCustomerAddres.setCustAddrCity(this.custAddrCity.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrZIP(this.custAddrZIP.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerAddres.setCustAddrPhone(this.custAddrPhone.getValue());
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
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerAddres
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerAddres aCustomerAddres) throws InterruptedException {
		logger.debug("Entering");
		
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custAddrType.getButton().focus();
		} else {
			this.custAddrHNbr.focus();
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
			doWriteBeanToComponents(aCustomerAddres);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			doCheckEnquiry();
			if(isNewCustomer()){
				this.window_CustomerAddresDialog.setHeight("70%");
				this.window_CustomerAddresDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerAddresDialog.doModal() ;
			}else{
				this.window_CustomerAddresDialog.setWidth("100%");
				this.window_CustomerAddresDialog.setHeight("100%");
				setDialog(this.window_CustomerAddresDialog);
			}
			
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if("ENQ".equals(this.moduleType)){
			//
		}
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_custAddrType = this.custAddrType.getValue();
		this.oldVar_lovDescCustAddrTypeName = this.custAddrType.getDescription();
		this.oldVar_custAddrHNbr = this.custAddrHNbr.getValue();
		this.oldVar_custFlatNbr = this.custFlatNbr.getValue();
		this.oldVar_custAddrStreet = this.custAddrStreet.getValue();
		this.oldVar_custAddrLine1 = this.custAddrLine1.getValue();
		this.oldVar_custAddrLine2 = this.custAddrLine2.getValue();
		this.oldVar_custPOBox = this.custPOBox.getValue();
		this.oldVar_custAddrCountry = this.custAddrCountry.getValue();
		this.oldVar_lovDescCustAddrCountryName = this.custAddrCountry.getDescription();
		this.oldVar_custAddrProvince = this.custAddrProvince.getValue();
		this.oldVar_lovDescCustAddrProvinceName = this.custAddrProvince.getDescription();
		this.oldVar_custAddrCity = this.custAddrCity.getValue();
		this.oldVar_lovDescCustAddrCityName = this.custAddrCity.getDescription();
		this.oldVar_custAddrZIP = this.custAddrZIP.getValue();
		this.oldVar_custAddrPhone = this.custAddrPhone.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.custID.setValue(this.oldVar_custID);
		this.custAddrType.setValue(this.oldVar_custAddrType);
		this.custAddrType.setDescription(this.oldVar_lovDescCustAddrTypeName);
		this.custAddrHNbr.setValue(this.oldVar_custAddrHNbr);
		this.custFlatNbr.setValue(this.oldVar_custFlatNbr);
		this.custAddrStreet.setValue(this.oldVar_custAddrStreet);
		this.custAddrLine1.setValue(this.oldVar_custAddrLine1);
		this.custAddrLine2.setValue(this.oldVar_custAddrLine2);
		this.custPOBox.setValue(this.oldVar_custPOBox);
		this.custAddrCountry.setValue(this.oldVar_custAddrCountry);
		this.custAddrCountry.setDescription(this.oldVar_lovDescCustAddrCountryName);
		this.custAddrProvince.setValue(this.oldVar_custAddrProvince);
		this.custAddrProvince.setDescription(this.oldVar_lovDescCustAddrProvinceName);
		this.custAddrCity.setValue(this.oldVar_custAddrCity);
		this.custAddrCity.setDescription(this.oldVar_lovDescCustAddrCityName);
		this.custAddrZIP.setValue(this.oldVar_custAddrZIP);
		this.custAddrPhone.setValue(this.oldVar_custAddrPhone);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
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
		
		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_custAddrType != this.custAddrType.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrHNbr != this.custAddrHNbr.getValue()) {
			return true;
		}
		if (this.oldVar_custFlatNbr != this.custFlatNbr.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrStreet != this.custAddrStreet.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrLine1 != this.custAddrLine1.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrLine2 != this.custAddrLine2.getValue()) {
			return true;
		}
		if (this.oldVar_custPOBox != this.custPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrCountry != this.custAddrCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrProvince != this.custAddrProvince.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrCity != this.custAddrCity.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrZIP != this.custAddrZIP.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrPhone != this.custAddrPhone.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custID.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrCIF.value"),null,true));
		}
		
		if (!this.custAddrHNbr.isReadonly()){
			this.custAddrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrHNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		
		if (!this.custFlatNbr.isReadonly()){
			this.custFlatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustFlatNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		
		boolean addressConstraint = false;
		if (StringUtils.trimToEmpty(this.custAddrStreet.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.custAddrLine1.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.custAddrLine2.getValue()).equals("")) {
			addressConstraint = true;
		}
		if (!this.custAddrStreet.isReadonly() && addressConstraint){
			this.custAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrStreet.value"),PennantRegularExpressions.REGEX_ADDRESS, true));
		}

		if (!this.custAddrLine1.isReadonly() && addressConstraint){
			this.custAddrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrLine1.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		
		if (!this.custAddrLine2.isReadonly() && addressConstraint){
			this.custAddrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrLine2.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		
		if (!this.custPOBox.isReadonly()){
			this.custPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		
		if (!this.custAddrZIP.isReadonly()){
			this.custAddrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrZIP.value"), PennantRegularExpressions.REGEX_ZIP, false));
		}
		
	/*	if (!this.custAddrPhone.isReadonly()){
			this.custAddrPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrPhone.value"),true));
		}*/
		
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
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		
		this.custAddrType.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrType.value"),null,true,true));
		
		this.custAddrCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrCountry.value"),null,true,true));
		
		this.custAddrProvince.setConstraint(new PTStringValidator( Labels.getLabel("label_CustomerAddresDialog_CustAddrProvince.value"),null,true,true));
		
		this.custAddrCity.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAddresDialog_CustAddrCity.value"),null,true,true));
		
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
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
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
		logger.debug("Leaving");
	}
	
	// Method for refreshing the list after successful update
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CustomerAddres> soCustomerAddress = getCustomerAddresListCtrl().getSearchObj();
		getCustomerAddresListCtrl().pagingCustomerAddresList.setActivePage(0);
		getCustomerAddresListCtrl().getPagedListWrapper().setSearchObject(soCustomerAddress);
		if (getCustomerAddresListCtrl().listBoxCustomerAddres != null) {
			getCustomerAddresListCtrl().listBoxCustomerAddres.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerAddres object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final CustomerAddres aCustomerAddres = new CustomerAddres();
		BeanUtils.copyProperties(getCustomerAddres(), aCustomerAddres);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aCustomerAddres.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerAddres.getRecordType()).equals("")) {
				aCustomerAddres.setVersion(aCustomerAddres.getVersion() + 1);
				aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerAddres.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()) {
					aCustomerAddres.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCustomerProcess(aCustomerAddres,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerAddresDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || 
							retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerAddress(this.customerAddress);
						// send the data back to customer
						closeWindow();
					}	
				}else if (doProcess(aCustomerAddres, tranType)) {
					refreshList();
					closeWindow();
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerAddres object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerAddres() in the front end.
		// we get it from the back end.
		final CustomerAddres aCustomerAddres = getCustomerAddresService().getNewCustomerAddres();
		aCustomerAddres.setNewRecord(true);
		setCustomerAddres(aCustomerAddres);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.custAddrType.getButton().focus();
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
			this.custAddrType.setReadonly(isReadOnly("CustomerAddresDialog_custAddrType"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custAddrType.setReadonly(true);
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
		this.custAddrZIP.setReadonly(isReadOnly("CustomerAddresDialog_custAddrZIP"));
		this.custAddrPhone.setReadonly(isReadOnly("CustomerAddresDialog_custAddrPhone"));

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
		boolean isCustomerWorkflow = false;
		if(getCustomerDialogCtrl() != null){
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow){
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
		this.custAddrPhone.setValue("");
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
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerAddres object with the components data
		doWriteComponentsToBean(aCustomerAddres);
		
		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here
		isNew = aCustomerAddres.isNew();
		String tranType = "";
		
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerAddres.getRecordType()).equals("")) {
				aCustomerAddres.setVersion(aCustomerAddres.getVersion() + 1);
				if (isNew) {
					aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerAddres.setNewRecord(true);
				}
			}
		} else {
			
			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerAddres.setVersion(1);
					aCustomerAddres.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerAddres.getRecordType()).equals("")){
					aCustomerAddres.setVersion(aCustomerAddres.getVersion()+1);
					aCustomerAddres.setRecordType(PennantConstants.RCD_UPD);
				}
				
				if(aCustomerAddres.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
			}else{
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
			if(isNewCustomer()){
				AuditHeader auditHeader =  newCustomerProcess(aCustomerAddres,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerAddresDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerAddress(this.customerAddress);
					// send the data back to customer
					closeWindow();
				}
			}else if (doProcess(aCustomerAddres, tranType)) {
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

	private AuditHeader newCustomerProcess(CustomerAddres aCustomerAddres,String tranType){
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerAddres, tranType);
		customerAddress = new ArrayList<CustomerAddres>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerAddres.getId());
		valueParm[1] = aCustomerAddres.getCustAddrType();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustAddrType") + ":"+valueParm[1];
		
		if(getCustomerDialogCtrl().getAddressList()!=null && getCustomerDialogCtrl().getAddressList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getAddressList().size(); i++) {
				CustomerAddres customerAddres = getCustomerDialogCtrl().getAddressList().get(i);
				
				if(aCustomerAddres.getCustAddrType().equals(customerAddres.getCustAddrType())){ // Both Current and Existing list addresses same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerAddress.add(aCustomerAddres);
						}else if(aCustomerAddres.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerAddress.add(aCustomerAddres);
						}else if(aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getAddressList().size(); j++) {
								CustomerAddres address =  getCustomerDialogCtrl().getCustomerDetails().getAddressList().get(j);
								if(address.getCustID() == aCustomerAddres.getCustID() && address.getCustAddrType().equals(aCustomerAddres.getCustAddrType())){
									customerAddress.add(address);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerAddress.add(customerAddres);
						}
					}
				}else{
					customerAddress.add(customerAddres);
				}
			}
		}
		
		if(!recordAdded){
			customerAddress.add(aCustomerAddres);
		}
		return auditHeader;
	} 
	
	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerAddres
	 *            (CustomerAddres)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerAddres aCustomerAddres, String tranType) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerAddres.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerAddres.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerAddres.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerAddres.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerAddres);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCustomerAddres))) {
					try {
						if (!isNotes_Entered()) {
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
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCustomerAddres.setTaskId(taskId);
			aCustomerAddres.setNextTaskId(nextTaskId);
			aCustomerAddres.setRoleCode(getRole());
			aCustomerAddres.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerAddres, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerAddres);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerAddres,PennantConstants.TRAN_WF);
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerAddres aCustomerAddres = (CustomerAddres) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerAddresService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerAddresService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCustomerAddresService().doApprove(auditHeader);
						
						if (aCustomerAddres.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerAddresService().doReject(
								auditHeader);
						
						if (aCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
						
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerAddresDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerAddresDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					
					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onFulfill$custAddrType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custAddrType.getObject();
		if (dataObject instanceof String) {
			this.custAddrType.setValue(dataObject.toString());
			this.custAddrType.setDescription("");
		} else {
			AddressType details = (AddressType) dataObject;
			if (details != null) {
				this.custAddrType.setValue(details.getLovValue());
				this.custAddrType.setDescription(details.getAddrTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custAddrCountry(Event event){
		logger.debug("Entering" + event.toString());
	
		Object dataObject = custAddrCountry.getObject();
		if (dataObject instanceof String){
			this.custAddrCountry.setValue(dataObject.toString());
			this.custAddrCountry.setDescription("");
		}else{
			Country details= (Country) dataObject;
			if (details != null) {
				this.custAddrCountry.setValue(details.getCountryCode());
				this.custAddrCountry.setDescription(details.getCountryDesc());
			}
		}
		doSetProvProp();
		doSetCityProp();
		logger.debug("Leaving" + event.toString());
	}	
	
	private void doSetProvProp(){
		if (!StringUtils.trimToEmpty(mortgAddrCountryTemp).equals(this.custAddrCountry.getValue())){
			this.custAddrProvince.setObject("");
			this.custAddrProvince.setValue("");
			this.custAddrProvince.setDescription("");
			this.custAddrCity.setObject("");
			this.custAddrCity.setValue("");
			this.custAddrCity.setDescription("");
		}
		mortgAddrCountryTemp = this.custAddrCountry.getValue();
		Filter[] filtersProvince = new Filter[1] ;
		filtersProvince[0]= new Filter("CPCountry", this.custAddrCountry.getValue(), Filter.OP_EQUAL);
		this.custAddrProvince.setFilters(filtersProvince);
	}
	
	public void onFulfill$custAddrProvince(Event event){
		logger.debug("Entering" + event.toString());
		
		Object dataObject = custAddrProvince.getObject();
		if (dataObject instanceof String){
			this.custAddrProvince.setValue(dataObject.toString());
			this.custAddrProvince.setDescription("");
		}else{
			Province details= (Province) dataObject;
			if (details != null) {
				this.custAddrProvince.setValue(details.getCPProvince());
				this.custAddrProvince.setDescription(details.getCPProvinceName());
			}
		}

		doSetCityProp();
		
		logger.debug("Leaving" + event.toString());
	}
	
	private void doSetCityProp(){
		if (!StringUtils.trimToEmpty(mortgAddrProvinceTemp).equals(this.custAddrProvince.getValue())){
			this.custAddrCity.setObject("");
			this.custAddrCity.setValue("");
			this.custAddrCity.setDescription("");   
		}
		mortgAddrProvinceTemp= this.custAddrProvince.getValue();
		Filter[] filtersCity = new Filter[2] ;
		filtersCity[0] = new Filter("PCCountry", this.custAddrCountry.getValue(),Filter.OP_EQUAL);
		filtersCity[1]= new Filter("PCProvince", this.custAddrProvince.getValue(), Filter.OP_EQUAL);
		this.custAddrCity.setFilters(filtersCity);
	}
	

	/**
	  * Method for Calling list Of existed Customers
	  * @param event
	  * @throws SuspendNotAllowedException
	  * @throws InterruptedException
	  */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException,
						InterruptedException {
		logger.debug("Entering" + event.toString());
		onLoad();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onLoad() throws SuspendNotAllowedException,
			InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
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
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * @param aCustomerAddres
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CustomerAddres aCustomerAddres, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerAddres.getBefImage(), aCustomerAddres);
		return new AuditHeader(getReference(), String.valueOf(aCustomerAddres.getCustID()),
				null, null, auditDetail, aCustomerAddres.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails( PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerAddresDialog, auditHeader);
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
		logger.debug("Entering" +event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerAddres");
		notes.setReference(getReference());
		notes.setVersion(getCustomerAddres().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	
	/** 
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerAddres().getCustID() + PennantConstants.KEY_SEPERATOR
		+ getCustomerAddres().getCustAddrType();
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

	public CustomerAddres getCustomerAddres() {
		return this.customerAddres;
	}
	public void setCustomerAddres(CustomerAddres customerAddres) {
		this.customerAddres = customerAddres;
	}

	public void setCustomerAddresService(
			CustomerAddresService customerAddresService) {
		this.customerAddresService = customerAddresService;
	}
	public CustomerAddresService getCustomerAddresService() {
		return this.customerAddresService;
	}

	public void setCustomerAddresListCtrl(
			CustomerAddresListCtrl customerAddresListCtrl) {
		this.customerAddresListCtrl = customerAddresListCtrl;
	}
	public CustomerAddresListCtrl getCustomerAddresListCtrl() {
		return this.customerAddresListCtrl;
	}
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
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
}
