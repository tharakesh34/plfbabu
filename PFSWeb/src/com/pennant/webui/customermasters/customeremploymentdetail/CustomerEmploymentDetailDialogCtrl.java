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
 * FileName    		:  CustomerEmploymentDetailDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeremploymentdetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/customerEmploymentDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerEmploymentDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4626382073313654611L;
	private final static Logger logger = Logger.getLogger(CustomerEmploymentDetailDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_CustomerEmploymentDetailDialog; 	// autoWired

   	protected Longbox custID; 									// autoWired
	protected Textbox custEmpName; 								// autoWired
  	protected Datebox custEmpFrom; 								// autoWired
	protected Textbox custEmpDesg; 								// autoWired
	protected Textbox custEmpDept; 								// autoWired
	protected Textbox custEmpID; 								// autoWired
	protected Textbox custEmpType; 								// autoWired
	protected Textbox custEmpHNbr; 								// autoWired
	protected Textbox custEMpFlatNbr; 							// autoWired
	protected Textbox custEmpAddrStreet; 						// autoWired
	protected Textbox custEMpAddrLine1; 						// autoWired
	protected Textbox custEMpAddrLine2; 						// autoWired
	protected Textbox custEmpPOBox; 							// autoWired
	protected Textbox custEmpAddrCity; 							// autoWired
	protected Textbox custEmpAddrProvince; 						// autoWired
	protected Textbox custEmpAddrCountry; 						// autoWired
	protected Textbox custEmpAddrZIP; 							// autoWired
	protected Textbox custEmpAddrPhone; 						// autoWired
	
	protected Textbox custCIF;									// autoWired
	protected Label   custShrtName;								// autoWired

	protected Label 		recordStatus; 						// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;


	// not auto wired variables
	private CustomerEmploymentDetail customerEmploymentDetail; // overHanded per parameter
	private transient CustomerEmploymentDetailListCtrl customerEmploymentDetailListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long  		oldVar_custID;
	private transient String  		oldVar_custEmpName;
	private transient Date  		oldVar_custEmpFrom;
	private transient String  		oldVar_custEmpDesg;
	private transient String  		oldVar_custEmpDept;
	private transient String  		oldVar_custEmpID;
	private transient String  		oldVar_custEmpType;
	private transient String  		oldVar_custEmpHNbr;
	private transient String  		oldVar_custEMpFlatNbr;
	private transient String  		oldVar_custEmpAddrStreet;
	private transient String  		oldVar_custEMpAddrLine1;
	private transient String  		oldVar_custEMpAddrLine2;
	private transient String  		oldVar_custEmpPOBox;
	private transient String  		oldVar_custEmpAddrCity;
	private transient String  		oldVar_custEmpAddrProvince;
	private transient String  		oldVar_custEmpAddrCountry;
	private transient String  		oldVar_custEmpAddrZIP;
	private transient String  		oldVar_custEmpAddrPhone;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerEmploymentDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
	protected Button btnSearchPRCustid; 
	
	protected Button btnSearchCustEmpDesg; 			// autoWire
	protected Textbox lovDescCustEmpDesgName;
	private transient String 		oldVar_lovDescCustEmpDesgName;
	
	protected Button btnSearchCustEmpDept; 			// autoWire
	protected Textbox lovDescCustEmpDeptName;
	private transient String 		oldVar_lovDescCustEmpDeptName;
	
	protected Button btnSearchCustEmpType; 			// autoWire
	protected Textbox lovDescCustEmpTypeName;
	private transient String 		oldVar_lovDescCustEmpTypeName;
	
	protected Button btnSearchCustEmpAddrCity; 		// autoWire
	protected Textbox lovDescCustEmpAddrCityName;
	private transient String 		oldVar_lovDescCustEmpAddrCityName;
	
	protected Button btnSearchCustEmpAddrProvince; 	// autoWire
	protected Textbox lovDescCustEmpAddrProvinceName;
	private transient String 		oldVar_lovDescCustEmpAddrProvinceName;
	
	protected Button btnSearchCustEmpAddrCountry; 	// autoWire
	protected Textbox lovDescCustEmpAddrCountryName;
	private transient String 		oldVar_lovDescCustEmpAddrCountryName;
	
	// ServiceDAOs / Domain Classes
	private transient CustomerEmploymentDetailService customerEmploymentDetailService;
	private transient CustomerSelectCtrl customerSelectCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerEmploymentDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerEmploymentDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerEmploymentDetailDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true,
				this.btnNew,this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
				this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("customerEmploymentDetail")) {
			this.customerEmploymentDetail = (CustomerEmploymentDetail) args.get("customerEmploymentDetail");
			CustomerEmploymentDetail befImage =new CustomerEmploymentDetail();
			BeanUtils.copyProperties(this.customerEmploymentDetail, befImage);
			this.customerEmploymentDetail.setBefImage(befImage);
			setCustomerEmploymentDetail(this.customerEmploymentDetail);
		} else {
			setCustomerEmploymentDetail(null);
		}
	
		doLoadWorkFlow(this.customerEmploymentDetail.isWorkflow(),
				this.customerEmploymentDetail.getWorkflowId(),this.customerEmploymentDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerEmploymentDetailDialog");
		}
	
		// READ OVERHANDED parameters !
		// we get the customerEmploymentDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerEmploymentDetail here.
		if (args.containsKey("customerEmploymentDetailListCtrl")) {
			setCustomerEmploymentDetailListCtrl((CustomerEmploymentDetailListCtrl) args.get(
					"customerEmploymentDetailListCtrl"));
		} else {
			setCustomerEmploymentDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerEmploymentDetail());
		
		//Calling SelectCtrl For proper selection of Customer
		if(getCustomerEmploymentDetail().isNewRecord()){
			onload();
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		//Empty sent any required attributes
		this.custEmpName.setMaxlength(50);
	 	this.custEmpFrom.setFormat(PennantConstants.dateFormat);
		this.custEmpDesg.setMaxlength(8);
		this.custEmpDept.setMaxlength(8);
		this.custEmpID.setMaxlength(50);
		this.custEmpType.setMaxlength(8);
		this.custEmpHNbr.setMaxlength(50);
		this.custEMpFlatNbr.setMaxlength(50);
		this.custEmpAddrStreet.setMaxlength(50);
		this.custEMpAddrLine1.setMaxlength(50);
		this.custEMpAddrLine2.setMaxlength(50);
		this.custEmpPOBox.setMaxlength(8);
		this.custEmpAddrCity.setMaxlength(8);
		this.custEmpAddrProvince.setMaxlength(8);
		this.custEmpAddrCountry.setMaxlength(2);
		this.custEmpAddrZIP.setMaxlength(50);
		this.custEmpAddrPhone.setMaxlength(50);
		this.btnSearchCustEmpAddrProvince.setVisible(false);
		this.btnSearchCustEmpAddrCity.setVisible(false);
		
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
		getUserWorkspace().alocateAuthorities("CustomerEmploymentDetailDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnSave"));
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
	public void onClose$window_CustomerEmploymentDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerEmploymentDetailDialog);
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
			closeDialog(this.window_CustomerEmploymentDetailDialog, "CustomerEmploymentDetail");
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
	 * @param aCustomerEmploymentDetail
	 *            CustomerEmploymentDetail
	 */
	public void doWriteBeanToComponents(CustomerEmploymentDetail aCustomerEmploymentDetail) {
		logger.debug("Entering");
		
		if(aCustomerEmploymentDetail.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerEmploymentDetail.getCustID());	
		}
		
		this.custEmpName.setValue(aCustomerEmploymentDetail.getCustEmpName());
		this.custEmpFrom.setValue(aCustomerEmploymentDetail.getCustEmpFrom());
		this.custEmpDesg.setValue(aCustomerEmploymentDetail.getCustEmpDesg());
		this.custEmpDept.setValue(aCustomerEmploymentDetail.getCustEmpDept());
		this.custEmpID.setValue(aCustomerEmploymentDetail.getCustEmpID());
		this.custEmpType.setValue(aCustomerEmploymentDetail.getCustEmpType());
		this.custEmpHNbr.setValue(aCustomerEmploymentDetail.getCustEmpHNbr());
		this.custEMpFlatNbr.setValue(aCustomerEmploymentDetail.getCustEMpFlatNbr());
		this.custEmpAddrStreet.setValue(aCustomerEmploymentDetail.getCustEmpAddrStreet());
		this.custEMpAddrLine1.setValue(aCustomerEmploymentDetail.getCustEMpAddrLine1());
		this.custEMpAddrLine2.setValue(aCustomerEmploymentDetail.getCustEMpAddrLine2());
		this.custEmpPOBox.setValue(aCustomerEmploymentDetail.getCustEmpPOBox());
		this.custEmpAddrCity.setValue(aCustomerEmploymentDetail.getCustEmpAddrCity());
		this.custEmpAddrProvince.setValue(aCustomerEmploymentDetail.getCustEmpAddrProvince());
		this.custEmpAddrCountry.setValue(aCustomerEmploymentDetail.getCustEmpAddrCountry());
		this.custEmpAddrZIP.setValue(aCustomerEmploymentDetail.getCustEmpAddrZIP());
		this.custEmpAddrPhone.setValue(aCustomerEmploymentDetail.getCustEmpAddrPhone());
		this.custCIF.setValue(aCustomerEmploymentDetail.getLovDescCustCIF()==null?"":aCustomerEmploymentDetail.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerEmploymentDetail.getLovDescCustShrtName()==null?"":aCustomerEmploymentDetail.getLovDescCustShrtName().trim());

		if (aCustomerEmploymentDetail.isNewRecord()){
			this.lovDescCustEmpDesgName.setValue("");
			this.lovDescCustEmpDeptName.setValue("");
			this.lovDescCustEmpTypeName.setValue("");
			this.lovDescCustEmpAddrCityName.setValue("");
			this.lovDescCustEmpAddrProvinceName.setValue("");
			this.lovDescCustEmpAddrCountryName.setValue("");
		}else{
			this.lovDescCustEmpDesgName.setValue(aCustomerEmploymentDetail.getCustEmpDesg()+"-"+aCustomerEmploymentDetail.getLovDescCustEmpDesgName());
			this.lovDescCustEmpDeptName.setValue(aCustomerEmploymentDetail.getCustEmpDept()+"-"+aCustomerEmploymentDetail.getLovDescCustEmpDeptName());
			this.lovDescCustEmpTypeName.setValue(aCustomerEmploymentDetail.getCustEmpType()+"-"+aCustomerEmploymentDetail.getLovDescCustEmpTypeName());
			this.lovDescCustEmpAddrCityName.setValue(aCustomerEmploymentDetail.getCustEmpAddrCity()+"-"+aCustomerEmploymentDetail.getLovDescCustEmpAddrCityName());
			this.lovDescCustEmpAddrProvinceName.setValue(aCustomerEmploymentDetail.getCustEmpAddrProvince()+"-"+aCustomerEmploymentDetail.getLovDescCustEmpAddrProvinceName());
			this.lovDescCustEmpAddrCountryName.setValue(aCustomerEmploymentDetail.getCustEmpAddrCountry()+"-"+aCustomerEmploymentDetail.getLovDescCustEmpAddrCountryName());
		}
		this.recordStatus.setValue(aCustomerEmploymentDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerEmploymentDetail
	 */
	public void doWriteComponentsToBean(CustomerEmploymentDetail aCustomerEmploymentDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aCustomerEmploymentDetail.setCustID(this.custID.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEmpName(this.custEmpName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (!this.custEmpFrom.getValue().after(((Date) SystemParameterDetails
							.getSystemParameterValue("APP_DFT_START_DATE")))) {
				throw new WrongValueException(this.custEmpFrom,Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[] {Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value"),
							SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
			}
			aCustomerEmploymentDetail.setCustEmpFrom(new Timestamp(this.custEmpFrom.getValue().getTime()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpDesgName(this.lovDescCustEmpDesgName.getValue());
	 		aCustomerEmploymentDetail.setCustEmpDesg(this.custEmpDesg.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpDeptName(this.lovDescCustEmpDeptName.getValue());
	 		aCustomerEmploymentDetail.setCustEmpDept(this.custEmpDept.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEmpID(this.custEmpID.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpTypeName(this.lovDescCustEmpTypeName.getValue());
	 		aCustomerEmploymentDetail.setCustEmpType(this.custEmpType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEmpHNbr(this.custEmpHNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEMpFlatNbr(this.custEMpFlatNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEmpAddrStreet(this.custEmpAddrStreet.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEMpAddrLine1(this.custEMpAddrLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEMpAddrLine2(this.custEMpAddrLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEmpPOBox(this.custEmpPOBox.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpAddrCityName(this.lovDescCustEmpAddrCityName.getValue());
	 		aCustomerEmploymentDetail.setCustEmpAddrCity(this.custEmpAddrCity.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpAddrProvinceName(this.lovDescCustEmpAddrProvinceName.getValue());
	 		aCustomerEmploymentDetail.setCustEmpAddrProvince(this.custEmpAddrProvince.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpAddrCountryName(this.lovDescCustEmpAddrCountryName.getValue());
	 		aCustomerEmploymentDetail.setCustEmpAddrCountry(this.custEmpAddrCountry.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEmpAddrZIP(this.custEmpAddrZIP.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerEmploymentDetail.setCustEmpAddrPhone(this.custEmpAddrPhone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		aCustomerEmploymentDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerEmploymentDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerEmploymentDetail aCustomerEmploymentDetail) throws InterruptedException {
		logger.debug("Entering");
		
		// if aCustomerEmploymentDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomerEmploymentDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCustomerEmploymentDetail = getCustomerEmploymentDetailService().getNewCustomerEmploymentDetail();
			setCustomerEmploymentDetail(aCustomerEmploymentDetail);
		} else {
			setCustomerEmploymentDetail(aCustomerEmploymentDetail);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCustomerEmploymentDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custEmpName.focus();
		} else {
			this.custEmpName.focus();
			if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aCustomerEmploymentDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerEmploymentDetailDialog);
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
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
		this.oldVar_custEmpName = this.custEmpName.getValue();
		this.oldVar_custEmpFrom = this.custEmpFrom.getValue();	
 		this.oldVar_custEmpDesg = this.custEmpDesg.getValue();
 		this.oldVar_lovDescCustEmpDesgName = this.lovDescCustEmpDesgName.getValue();
 		this.oldVar_custEmpDept = this.custEmpDept.getValue();
 		this.oldVar_lovDescCustEmpDeptName = this.lovDescCustEmpDeptName.getValue();
		this.oldVar_custEmpID = this.custEmpID.getValue();
 		this.oldVar_custEmpType = this.custEmpType.getValue();
 		this.oldVar_lovDescCustEmpTypeName = this.lovDescCustEmpTypeName.getValue();
		this.oldVar_custEmpHNbr = this.custEmpHNbr.getValue();
		this.oldVar_custEMpFlatNbr = this.custEMpFlatNbr.getValue();
		this.oldVar_custEmpAddrStreet = this.custEmpAddrStreet.getValue();
		this.oldVar_custEMpAddrLine1 = this.custEMpAddrLine1.getValue();
		this.oldVar_custEMpAddrLine2 = this.custEMpAddrLine2.getValue();
		this.oldVar_custEmpPOBox = this.custEmpPOBox.getValue();
 		this.oldVar_custEmpAddrCity = this.custEmpAddrCity.getValue();
 		this.oldVar_lovDescCustEmpAddrCityName = this.lovDescCustEmpAddrCityName.getValue();
 		this.oldVar_custEmpAddrProvince = this.custEmpAddrProvince.getValue();
 		this.oldVar_lovDescCustEmpAddrProvinceName = this.lovDescCustEmpAddrProvinceName.getValue();
 		this.oldVar_custEmpAddrCountry = this.custEmpAddrCountry.getValue();
 		this.oldVar_lovDescCustEmpAddrCountryName = this.lovDescCustEmpAddrCountryName.getValue();
		this.oldVar_custEmpAddrZIP = this.custEmpAddrZIP.getValue();
		this.oldVar_custEmpAddrPhone = this.custEmpAddrPhone.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.custID.setValue(this.oldVar_custID);
		this.custEmpName.setValue(this.oldVar_custEmpName);
		this.custEmpFrom.setValue(this.oldVar_custEmpFrom);
 		this.custEmpDesg.setValue(this.oldVar_custEmpDesg);
 		this.lovDescCustEmpDesgName.setValue(this.oldVar_lovDescCustEmpDesgName);
 		this.custEmpDept.setValue(this.oldVar_custEmpDept);
 		this.lovDescCustEmpDeptName.setValue(this.oldVar_lovDescCustEmpDeptName);
		this.custEmpID.setValue(this.oldVar_custEmpID);
 		this.custEmpType.setValue(this.oldVar_custEmpType);
 		this.lovDescCustEmpTypeName.setValue(this.oldVar_lovDescCustEmpTypeName);
		this.custEmpHNbr.setValue(this.oldVar_custEmpHNbr);
		this.custEMpFlatNbr.setValue(this.oldVar_custEMpFlatNbr);
		this.custEmpAddrStreet.setValue(this.oldVar_custEmpAddrStreet);
		this.custEMpAddrLine1.setValue(this.oldVar_custEMpAddrLine1);
		this.custEMpAddrLine2.setValue(this.oldVar_custEMpAddrLine2);
		this.custEmpPOBox.setValue(this.oldVar_custEmpPOBox);
 		this.custEmpAddrCity.setValue(this.oldVar_custEmpAddrCity);
 		this.lovDescCustEmpAddrCityName.setValue(this.oldVar_lovDescCustEmpAddrCityName);
 		this.custEmpAddrProvince.setValue(this.oldVar_custEmpAddrProvince);
 		this.lovDescCustEmpAddrProvinceName.setValue(this.oldVar_lovDescCustEmpAddrProvinceName);
 		this.custEmpAddrCountry.setValue(this.oldVar_custEmpAddrCountry);
 		this.lovDescCustEmpAddrCountryName.setValue(this.oldVar_lovDescCustEmpAddrCountryName);
		this.custEmpAddrZIP.setValue(this.oldVar_custEmpAddrZIP);
		this.custEmpAddrPhone.setValue(this.oldVar_custEmpAddrPhone);
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
		
		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_custEmpName != this.custEmpName.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpFrom != this.custEmpFrom.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpDesg != this.custEmpDesg.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpDept != this.custEmpDept.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpID != this.custEmpID.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpType != this.custEmpType.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpHNbr != this.custEmpHNbr.getValue()) {
			return true;
		}
		if (this.oldVar_custEMpFlatNbr != this.custEMpFlatNbr.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpAddrStreet != this.custEmpAddrStreet.getValue()) {
			return true;
		}
		if (this.oldVar_custEMpAddrLine1 != this.custEMpAddrLine1.getValue()) {
			return true;
		}
		if (this.oldVar_custEMpAddrLine2 != this.custEMpAddrLine2.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpPOBox != this.custEmpPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpAddrCity != this.custEmpAddrCity.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpAddrProvince != this.custEmpAddrProvince.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpAddrCountry != this.custEmpAddrCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpAddrZIP != this.custEmpAddrZIP.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpAddrPhone != this.custEmpAddrPhone.getValue()) {
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
		
		if(!this.custID.isReadonly()){
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_CustomerEmploymentDetailDialog_CustID.value")}));
		}
		if (!this.custEmpID.isReadonly()) {
			this.custEmpID.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_REGEX,Labels.getLabel(
				"MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpID.value") })));
		}
		if (!this.custEmpName.isReadonly()){
			this.custEmpName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,Labels.getLabel(
					"MAND_FIELD_CHARACTER",new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpName.value") })));
		}	
		if (!this.custEmpFrom.isReadonly()) {
			this.custEmpFrom.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel("DATE_EMPTY_FUTURE_TODAY",
				new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value") }));
		}
		if (!this.custEmpPOBox.isReadonly()) {
			this.custEmpPOBox.setConstraint(new SimpleConstraint(PennantConstants.NUM_REGEX,Labels.getLabel(
				"MAND_NUMBER",new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpPOBox.value") })));
		}
		if (!this.custEmpAddrZIP.isReadonly()) {
			if (!StringUtils.trimToEmpty(this.custEmpAddrZIP.getValue()).equals("")) {
				this.custEmpAddrZIP.setConstraint(new SimpleConstraint(PennantConstants.ZIP_REGEX,Labels.getLabel(
					"MAND_NUMBER",new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrZIP.value") })));
			}
		}
		if (!this.custEmpAddrPhone.isReadonly()) {
			this.custEmpAddrPhone.setConstraint(new SimpleConstraint(PennantConstants.PH_REGEX,Labels.getLabel(
				"MAND_NUMBER",new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrPhone.value") })));
		}
		boolean addressConstraint = false;
		if (StringUtils.trimToEmpty(this.custEmpHNbr.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.custEMpFlatNbr.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.custEmpAddrStreet.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.custEMpAddrLine1.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.custEMpAddrLine2.getValue()).equals("")) {
			addressConstraint = true;
		}
		if (addressConstraint) {
			this.custEmpHNbr.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_ADDRESS",
				new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpHNbr.value") }));
		}
		if (addressConstraint) {
			this.custEMpFlatNbr.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_ADDRESS",
				new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEMpFlatNbr.value") }));
		}
		if (addressConstraint) {
			this.custEmpAddrStreet.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_ADDRESS",
				new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrStreet.value") }));
		}
		if (addressConstraint) {
			this.custEMpAddrLine1.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_ADDRESS",
				new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEMpAddrLine1.value") }));
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
		this.custEmpName.setConstraint("");
		this.custEmpFrom.setConstraint("");
		this.custEmpID.setConstraint("");
		this.custEmpHNbr.setConstraint("");
		this.custEMpFlatNbr.setConstraint("");
		this.custEmpAddrStreet.setConstraint("");
		this.custEMpAddrLine1.setConstraint("");
		this.custEMpAddrLine2.setConstraint("");
		this.custEmpPOBox.setConstraint("");
		this.custEmpAddrZIP.setConstraint("");
		this.custEmpAddrPhone.setConstraint("");
		
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		
		this.lovDescCustEmpDesgName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpDesg.value")}));
		
		this.lovDescCustEmpDeptName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpDept.value")}));
		
		this.lovDescCustEmpTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpType.value")}));
		
		this.lovDescCustEmpAddrCityName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrCity.value")}));
		
		this.lovDescCustEmpAddrProvinceName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrProvince.value")}));
		
		this.lovDescCustEmpAddrCountryName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrCountry.value")}));
		
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustEmpDesgName.setConstraint("");
		this.lovDescCustEmpDeptName.setConstraint("");
		this.lovDescCustEmpTypeName.setConstraint("");
		this.lovDescCustEmpAddrCityName.setConstraint("");
		this.lovDescCustEmpAddrProvinceName.setConstraint("");
		this.lovDescCustEmpAddrCountryName.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custEmpName.setErrorMessage("");
		this.custEmpFrom.setErrorMessage("");
		this.custEmpID.setErrorMessage("");
		this.custEmpHNbr.setErrorMessage("");
		this.custEMpFlatNbr.setErrorMessage("");
		this.custEmpAddrStreet.setErrorMessage("");
		this.custEMpAddrLine1.setErrorMessage("");
		this.custEMpAddrLine2.setErrorMessage("");
		this.custEmpPOBox.setErrorMessage("");
		this.custEmpAddrZIP.setErrorMessage("");
		this.custEmpAddrPhone.setErrorMessage("");
		this.lovDescCustEmpDesgName.setErrorMessage("");
		this.lovDescCustEmpDeptName.setErrorMessage("");
		this.lovDescCustEmpTypeName.setErrorMessage("");
		this.lovDescCustEmpAddrCityName.setErrorMessage("");
		this.lovDescCustEmpAddrProvinceName.setErrorMessage("");
		this.lovDescCustEmpAddrCountryName.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CustomerEmploymentDetail> soCustomerEmploymentDetail = getCustomerEmploymentDetailListCtrl().getSearchObj();
		getCustomerEmploymentDetailListCtrl().pagingCustomerEmploymentDetailList.setActivePage(0);
		getCustomerEmploymentDetailListCtrl().getPagedListWrapper().setSearchObject(soCustomerEmploymentDetail);
		if (getCustomerEmploymentDetailListCtrl().listBoxCustomerEmploymentDetail != null) {
			getCustomerEmploymentDetailListCtrl().listBoxCustomerEmploymentDetail.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerEmploymentDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final CustomerEmploymentDetail aCustomerEmploymentDetail = new CustomerEmploymentDetail();
		BeanUtils.copyProperties(getCustomerEmploymentDetail(), aCustomerEmploymentDetail);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + 
									"\n\n --> " + aCustomerEmploymentDetail.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerEmploymentDetail.getRecordType()).equals("")){
				aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion()+1);
				aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aCustomerEmploymentDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCustomerEmploymentDetail,tranType)){
					refreshList();
					closeDialog(this.window_CustomerEmploymentDetailDialog, "CustomerEmploymentDetail"); 
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerEmploymentDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerEmploymentDetail() in the frontEnd.
		// we get it from the backEnd.
		final CustomerEmploymentDetail aCustomerEmploymentDetail = getCustomerEmploymentDetailService().getNewCustomerEmploymentDetail();
		aCustomerEmploymentDetail.setNewRecord(true);
		setCustomerEmploymentDetail(aCustomerEmploymentDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.btnSearchCustEmpDesg.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getCustomerEmploymentDetail().isNewRecord()){
		  	this.custID.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.btnSearchPRCustid.setVisible(true);
		}else{
			this.custID.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
		}
		this.custCIF.setReadonly(true);
		this.custEmpName.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpName"));
	 	this.custEmpFrom.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpFrom"));
	  	this.btnSearchCustEmpDesg.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpDesg"));
	  	this.btnSearchCustEmpDept.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpDept"));
		this.custEmpID.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpID"));
	  	this.btnSearchCustEmpType.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpType"));
		this.custEmpHNbr.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpHNbr"));
		this.custEMpFlatNbr.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEMpFlatNbr"));
		this.custEmpAddrStreet.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpAddrStreet"));
		this.custEMpAddrLine1.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEMpAddrLine1"));
		this.custEMpAddrLine2.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEMpAddrLine2"));
		this.custEmpPOBox.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpPOBox"));
	  	this.btnSearchCustEmpAddrCity.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpAddrCity"));
	  	this.btnSearchCustEmpAddrProvince.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpAddrProvince"));
	  	this.btnSearchCustEmpAddrCountry.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpAddrCountry"));
		this.custEmpAddrZIP.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpAddrZIP"));
		this.custEmpAddrPhone.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpAddrPhone"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.customerEmploymentDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
		this.custID.setReadonly(true);
		this.custEmpName.setReadonly(true);
		this.custEmpFrom.setDisabled(true);
		this.btnSearchCustEmpDesg.setDisabled(true);
		this.btnSearchCustEmpDept.setDisabled(true);
		this.custEmpID.setReadonly(true);
		this.btnSearchCustEmpType.setDisabled(true);
		this.custEmpHNbr.setReadonly(true);
		this.custEMpFlatNbr.setReadonly(true);
		this.custEmpAddrStreet.setReadonly(true);
		this.custEMpAddrLine1.setReadonly(true);
		this.custEMpAddrLine2.setReadonly(true);
		this.custEmpPOBox.setReadonly(true);
		this.btnSearchCustEmpAddrCity.setDisabled(true);
		this.btnSearchCustEmpAddrProvince.setDisabled(true);
		this.btnSearchCustEmpAddrCountry.setDisabled(true);
		this.custEmpAddrZIP.setReadonly(true);
		this.custEmpAddrPhone.setReadonly(true);
		
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
		this.custID.setText("");
		this.custEmpName.setValue("");
		this.custEmpFrom.setText("");
	  	this.custEmpDesg.setValue("");
		this.lovDescCustEmpDesgName.setValue("");
	  	this.custEmpDept.setValue("");
		this.lovDescCustEmpDeptName.setValue("");
		this.custEmpID.setValue("");
	  	this.custEmpType.setValue("");
		this.lovDescCustEmpTypeName.setValue("");
		this.custEmpHNbr.setValue("");
		this.custEMpFlatNbr.setValue("");
		this.custEmpAddrStreet.setValue("");
		this.custEMpAddrLine1.setValue("");
		this.custEMpAddrLine2.setValue("");
		this.custEmpPOBox.setValue("");
	  	this.custEmpAddrCity.setValue("");
		this.lovDescCustEmpAddrCityName.setValue("");
	  	this.custEmpAddrProvince.setValue("");
		this.lovDescCustEmpAddrProvinceName.setValue("");
	  	this.custEmpAddrCountry.setValue("");
		this.lovDescCustEmpAddrCountryName.setValue("");
		this.custEmpAddrZIP.setValue("");
		this.custEmpAddrPhone.setValue("");
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerEmploymentDetail aCustomerEmploymentDetail = new CustomerEmploymentDetail();
		BeanUtils.copyProperties(getCustomerEmploymentDetail(), aCustomerEmploymentDetail);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerEmploymentDetail object with the components data
		doWriteComponentsToBean(aCustomerEmploymentDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aCustomerEmploymentDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerEmploymentDetail.getRecordType()).equals("")){
				aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion()+1);
				if(isNew){
					aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerEmploymentDetail.setNewRecord(true);
				}
			}
		}else{
			aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			if(doProcess(aCustomerEmploymentDetail,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerEmploymentDetailDialog, "CustomerEmploymentDetail");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerEmploymentDetail (CustomerRating)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerEmploymentDetail aCustomerEmploymentDetail,String tranType){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aCustomerEmploymentDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerEmploymentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerEmploymentDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerEmploymentDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerEmploymentDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerEmploymentDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCustomerEmploymentDetail))) {
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

			aCustomerEmploymentDetail.setTaskId(taskId);
			aCustomerEmploymentDetail.setNextTaskId(nextTaskId);
			aCustomerEmploymentDetail.setRoleCode(getRole());
			aCustomerEmploymentDetail.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCustomerEmploymentDetail, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerEmploymentDetail);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerEmploymentDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerEmploymentDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		CustomerEmploymentDetail aCustomerEmploymentDetail = (CustomerEmploymentDetail) auditHeader
				.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		
		try {
			while(retValue==PennantConstants.porcessOVERIDE){
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerEmploymentDetailService().delete(auditHeader);
						deleteNotes = true;
					}else{
						auditHeader = getCustomerEmploymentDetailService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCustomerEmploymentDetailService().doApprove(auditHeader);
						if (aCustomerEmploymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCustomerEmploymentDetailService().doReject(auditHeader);
						if (aCustomerEmploymentDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(
								this.window_CustomerEmploymentDetailDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}
				
				retValue = ErrorControl.showErrorControl(this.window_CustomerEmploymentDetailDialog, auditHeader);
				
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
	
   public void onClick$btnSearchCustEmpDesg(Event event){
	   logger.debug("Entering" + event.toString());
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_CustomerEmploymentDetailDialog,"GeneralDesignation");
	   if (dataObject instanceof String){
		   this.custEmpDesg.setValue(dataObject.toString());
		   this.lovDescCustEmpDesgName.setValue("");
	   }else{
		   GeneralDesignation details= (GeneralDesignation) dataObject;
			if (details != null) {
				this.custEmpDesg.setValue(details.getLovValue());
				this.lovDescCustEmpDesgName.setValue(details.getLovValue()+"-"+details.getGenDesgDesc());
			}
	   }
	   logger.debug("Leaving" + event.toString());
	}
   
   public void onClick$btnSearchCustEmpDept(Event event){
	   logger.debug("Entering" + event.toString());
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_CustomerEmploymentDetailDialog,"GeneralDepartment");
	   if (dataObject instanceof String){
		   this.custEmpDept.setValue(dataObject.toString());
		   this.lovDescCustEmpDeptName.setValue("");
	   }else{
		   GeneralDepartment details= (GeneralDepartment) dataObject;
			if (details != null) {
				this.custEmpDept.setValue(details.getLovValue());
				this.lovDescCustEmpDeptName.setValue(details.getLovValue()+"-"+details.getGenDeptDesc());
			}
	   }
	   logger.debug("Leaving" + event.toString());
	}
   
   public void onClick$btnSearchCustEmpType(Event event){
	   logger.debug("Entering" + event.toString());
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_CustomerEmploymentDetailDialog,"EmploymentType");
	   if (dataObject instanceof String){
		   this.custEmpType.setValue(dataObject.toString());
		   this.lovDescCustEmpTypeName.setValue("");
	   }else{
		   EmploymentType details= (EmploymentType) dataObject;
			if (details != null) {
				this.custEmpType.setValue(details.getLovValue());
				this.lovDescCustEmpTypeName.setValue(details.getLovValue()+"-"+details.getEmpTypeDesc());
			}
	   }
	   logger.debug("Leaving" + event.toString());
	}
   
   public void onClick$btnSearchCustEmpAddrCity(Event event){
	   logger.debug("Entering" + event.toString());
	   
	   Filter[] filters = new Filter[1] ;
	   filters[0]= new Filter("PCProvince", this.custEmpAddrProvince.getValue(), Filter.OP_EQUAL);  
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_CustomerEmploymentDetailDialog,"City",filters);
	   if (dataObject instanceof String){
		   this.custEmpAddrCity.setValue(dataObject.toString());
		   this.lovDescCustEmpAddrCityName.setValue("");
	   }else{
		   City details= (City) dataObject;
			if (details != null) {
				this.custEmpAddrCity.setValue(details.getPCCity());
				this.lovDescCustEmpAddrCityName.setValue(details.getLovValue()+"-"+details.getPCCityName());
			}
	   }
	   logger.debug("Leaving" + event.toString());
	}
   
   public void onClick$btnSearchCustEmpAddrProvince(Event event){
	   logger.debug("Entering" + event.toString());
	   
	   String sCustEmpAddrProvince= this.custEmpAddrProvince.getValue();
	   Filter[] filters = new Filter[1] ;
	   filters[0]= new Filter("CPCountry", this.custEmpAddrCountry.getValue(), Filter.OP_EQUAL);  
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_CustomerEmploymentDetailDialog,"Province",filters);
	   if (dataObject instanceof String){
		   this.custEmpAddrProvince.setValue(dataObject.toString());
		   this.lovDescCustEmpAddrProvinceName.setValue("");
	   }else{
		   Province details= (Province) dataObject;
			if (details != null) {
				this.custEmpAddrProvince.setValue(details.getCPProvince());
				this.lovDescCustEmpAddrProvinceName.setValue(details.getLovValue()+"-"+details.getCPProvinceName());
			}
	   }
	   if (!StringUtils.trimToEmpty(sCustEmpAddrProvince).equals(this.custEmpAddrProvince.getValue())){
		   this.custEmpAddrCity.setValue("");
		   this.lovDescCustEmpAddrCityName.setValue("");
	   }

	   if(!this.lovDescCustEmpAddrProvinceName.getValue().equals("")){
		   this.btnSearchCustEmpAddrCity.setVisible(true);   
	   }
	   else{
		   this.btnSearchCustEmpAddrCity.setVisible(false);
		   this.lovDescCustEmpAddrCityName.setValue("");
	   }
	   logger.debug("Leaving" + event.toString());
	}
   
   public void onClick$btnSearchCustEmpAddrCountry(Event event){
	   logger.debug("Entering" + event.toString());
	   
	   String sCustEmpAddrCountry= this.custEmpAddrCountry.getValue();
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_CustomerEmploymentDetailDialog,"Country");
	   if (dataObject instanceof String){
		   this.custEmpAddrCountry.setValue(dataObject.toString());
		   this.lovDescCustEmpAddrCountryName.setValue("");
	   }else{
		   Country details= (Country) dataObject;
			if (details != null) {
				this.custEmpAddrCountry.setValue(details.getCountryCode());
				this.lovDescCustEmpAddrCountryName.setValue(details.getLovValue()+"-"+details.getCountryDesc());
			}
	   }
	   
	   if (!StringUtils.trimToEmpty(sCustEmpAddrCountry).equals(this.custEmpAddrCountry.getValue())){
		   this.custEmpAddrProvince.setValue("");
		   this.lovDescCustEmpAddrProvinceName.setValue("");
		   this.custEmpAddrCity.setValue("");
		   this.lovDescCustEmpAddrCityName.setValue("");
		   this.btnSearchCustEmpAddrCity.setVisible(false);
	   }

	   if(!this.lovDescCustEmpAddrCountryName.getValue().equals("")){
		   this.btnSearchCustEmpAddrProvince.setVisible(true);
	   }else{
		   this.btnSearchCustEmpAddrProvince.setVisible(false);
		   this.lovDescCustEmpAddrProvinceName.setValue("");
		   this.btnSearchCustEmpAddrCity.setVisible(false);
		   this.lovDescCustEmpAddrCityName.setValue("");
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
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
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
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerEmploymentDetail aCustomerEmploymentDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerEmploymentDetail.getBefImage(), aCustomerEmploymentDetail);

		return new AuditHeader(String.valueOf(aCustomerEmploymentDetail.getId())
				, String.valueOf(aCustomerEmploymentDetail.getCustID()), null,
				null, auditDetail, aCustomerEmploymentDetail.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerEmploymentDetailDialog, auditHeader);
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
		
		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
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
		Notes notes = new Notes();
		notes.setModuleName("CustomerEmploymentDetail");
		notes.setReference(String.valueOf(getCustomerEmploymentDetail().getCustID()));
		notes.setVersion(getCustomerEmploymentDetail().getVersion());
		return notes;
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

	public CustomerEmploymentDetail getCustomerEmploymentDetail() {
		return this.customerEmploymentDetail;
	}
	public void setCustomerEmploymentDetail(CustomerEmploymentDetail customerEmploymentDetail) {
		this.customerEmploymentDetail = customerEmploymentDetail;
	}

	public void setCustomerEmploymentDetailService(CustomerEmploymentDetailService customerEmploymentDetailService) {
		this.customerEmploymentDetailService = customerEmploymentDetailService;
	}
	public CustomerEmploymentDetailService getCustomerEmploymentDetailService() {
		return this.customerEmploymentDetailService;
	}

	public void setCustomerEmploymentDetailListCtrl(CustomerEmploymentDetailListCtrl customerEmploymentDetailListCtrl) {
		this.customerEmploymentDetailListCtrl = customerEmploymentDetailListCtrl;
	}
	public CustomerEmploymentDetailListCtrl getCustomerEmploymentDetailListCtrl() {
		return this.customerEmploymentDetailListCtrl;
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

}
