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
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

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
	protected ExtendedCombobox custEmpName; 								// autoWired
	
  	protected Datebox custEmpFrom; 								// autoWired
  	protected Datebox custEmpTo; 								// autoWired
  	protected Checkbox currentEmployer; 						// autoWired
	protected ExtendedCombobox custEmpDesg; 								// autoWired
	protected ExtendedCombobox custEmpDept; 								// autoWired
	protected ExtendedCombobox custEmpType; 								// autoWired
	
	protected Textbox custCIF;									// autoWired
	protected Label   custShrtName;								// autoWired

	protected Label 		recordStatus; 						// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South			south;

	// not auto wired variables
	private CustomerEmploymentDetail customerEmploymentDetail; // overHanded per parameter
	private transient CustomerEmploymentDetailListCtrl customerEmploymentDetailListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long  		oldVar_custID;
	private transient long  		oldVar_custEmpName;
	private transient Date  		oldVar_custEmpFrom;
	private transient Date  		oldVar_custEmpTo;
	private transient boolean  		oldVar_currentEmployer;
	private transient String  		oldVar_custEmpDesg;
	private transient String  		oldVar_custEmpDept;
	private transient String  		oldVar_custEmpType;
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
	
	private transient String 		oldVar_lovDescCustEmpDesgName;
	
	private transient String 		oldVar_lovDescCustEmpDeptName;
	
	private transient String 		oldVar_lovDescCustEmpTypeName;
	
	
	// ServiceDAOs / Domain Classes
	private transient CustomerEmploymentDetailService customerEmploymentDetailService;
	private transient CustomerSelectCtrl customerSelectCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	
	
	private boolean newRecord=false;
	private boolean newCustomer=false;
	private CustomerDialogCtrl customerDialogCtrl;
	private List<CustomerEmploymentDetail> customerEmploymentDetails;
	private String moduleType="";
	private String userRole="";
	private boolean isCurrentEmp = false;
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
	
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}
		if (args.containsKey("currentEmployer")) {
			this.isCurrentEmp = (Boolean) args.get("currentEmployer");
		}
		if(getCustomerEmploymentDetail().isNewRecord()){
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
			this.customerEmploymentDetail.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerEmploymentDetailDialog");
			}
		}
		
		doLoadWorkFlow(this.customerEmploymentDetail.isWorkflow(),
				this.customerEmploymentDetail.getWorkflowId(),this.customerEmploymentDetail.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();
		
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
		if(isNewRecord() & !isNewCustomer()){
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
		this.custEmpName.setInputAllowed(false);
		this.custEmpName.setDisplayStyle(3);
        this.custEmpName.setMandatoryStyle(true);
		this.custEmpName.setModuleName("EmployerDetail");
		this.custEmpName.setValueColumn("EmployerId");
		this.custEmpName.setDescColumn("EmpName");
		this.custEmpName.setValidateColumns(new String[] { "EmployerId" });
		
	 	this.custEmpFrom.setFormat(PennantConstants.dateFormat);
	 	this.custEmpTo.setFormat(PennantConstants.dateFormat);
		this.custEmpDesg.setMaxlength(8);
		this.custEmpDesg.setMandatoryStyle(true);
		this.custEmpDesg.getTextbox().setWidth("110px");
		this.custEmpDesg.setModuleName("GeneralDesignation");
		this.custEmpDesg.setValueColumn("GenDesignation");
		this.custEmpDesg.setDescColumn("GenDesgDesc");
		this.custEmpDesg.setValidateColumns(new String[] { "GenDesignation" });
		
		this.custEmpDept.setMaxlength(8);
		this.custEmpDept.setMandatoryStyle(true);
		this.custEmpDept.getTextbox().setWidth("110px");
		this.custEmpDept.setModuleName("GeneralDepartment");
		this.custEmpDept.setValueColumn("GenDepartment");
		this.custEmpDept.setDescColumn("GenDeptDesc");
		this.custEmpDept.setValidateColumns(new String[] { "GenDepartment" });	
		
		this.custEmpType.setInputAllowed(false);
		this.custEmpType.setDisplayStyle(3);
        this.custEmpType.setMandatoryStyle(true);
		this.custEmpType.setModuleName("EmploymentType");
		this.custEmpType.setValueColumn("EmpType");
		this.custEmpType.setDescColumn("EmpTypeDesc");
		this.custEmpType.setValidateColumns(new String[] { "EmpType" });
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
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
		getUserWorkspace().alocateAuthorities("CustomerEmploymentDetailDialog",userRole);
		
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
			closeWindow();
		}
		logger.debug("Leaving");
	}
	
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewCustomer()){
			closePopUpWindow(this.window_CustomerEmploymentDetailDialog,"CustomerEmploymentDetailDialog");
		}else{
			closeDialog(this.window_CustomerEmploymentDetailDialog, "CustomerEmploymentDetailDialog");
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
		
		this.custEmpName.setValue(String.valueOf(aCustomerEmploymentDetail.getCustEmpName()));
		this.custEmpFrom.setValue(aCustomerEmploymentDetail.getCustEmpFrom());
		this.custEmpTo.setValue(aCustomerEmploymentDetail.getCustEmpTo());
		this.custEmpDesg.setValue(aCustomerEmploymentDetail.getCustEmpDesg());
		this.custEmpDept.setValue(aCustomerEmploymentDetail.getCustEmpDept());
		this.custEmpType.setValue(aCustomerEmploymentDetail.getCustEmpType());
		this.custCIF.setValue(aCustomerEmploymentDetail.getLovDescCustCIF()==null?"":aCustomerEmploymentDetail.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerEmploymentDetail.getLovDescCustShrtName()==null?"":aCustomerEmploymentDetail.getLovDescCustShrtName().trim());
		this.currentEmployer.setChecked(aCustomerEmploymentDetail.isCurrentEmployer());
		if (aCustomerEmploymentDetail.getCustEmpName() == 0){
			this.custEmpDesg.setDescription("");
			this.custEmpDept.setDescription("");
			this.custEmpType.setDescription("");
			this.custEmpName.setDescription("");
		}else{
			this.custEmpDesg.setDescription(aCustomerEmploymentDetail.getLovDescCustEmpDesgName());
			this.custEmpDept.setDescription(aCustomerEmploymentDetail.getLovDescCustEmpDeptName());
			this.custEmpType.setDescription(aCustomerEmploymentDetail.getLovDescCustEmpTypeName());
			this.custEmpName.setDescription(aCustomerEmploymentDetail.getLovDesccustEmpName());
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
			aCustomerEmploymentDetail.setLovDesccustEmpName(this.custEmpName.getDescription());
		    aCustomerEmploymentDetail.setCustEmpName(Long.valueOf(this.custEmpName.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.custEmpFrom.getValue() != null){
			if (!this.custEmpFrom.getValue().after(((Date) SystemParameterDetails
							.getSystemParameterValue("APP_DFT_START_DATE")))) {
				throw new WrongValueException(this.custEmpFrom,Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[] {Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value"),
							SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
			}
			if (this.custEmpFrom.getValue().compareTo(((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR))) != -1) {
				throw new WrongValueException(this.custEmpFrom, Labels.getLabel("DATE_FUTURE_TODAY", new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value"), SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
			}
			aCustomerEmploymentDetail.setCustEmpFrom(new Timestamp(this.custEmpFrom.getValue().getTime()));
			}else{
			aCustomerEmploymentDetail.setCustEmpFrom(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (this.custEmpTo.getValue()!=null) {
				if (!this.custEmpTo.getValue().after(((Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE")))) {
					throw new WrongValueException(this.custEmpTo, Labels.getLabel("DATE_ALLOWED_AFTER", new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpTo.value"), SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
				if (this.custEmpTo.getValue().compareTo(((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR))) != -1) {
					throw new WrongValueException(this.custEmpTo, Labels.getLabel("DATE_FUTURE_TODAY", new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpTo.value"), SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
				aCustomerEmploymentDetail.setCustEmpTo(new Timestamp(this.custEmpTo.getValue().getTime()));
			}else{
				aCustomerEmploymentDetail.setCustEmpTo(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpDesgName(this.custEmpDesg.getDescription());
	 		aCustomerEmploymentDetail.setCustEmpDesg(this.custEmpDesg.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpDeptName(this.custEmpDept.getDescription());
	 		aCustomerEmploymentDetail.setCustEmpDept(this.custEmpDept.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCustomerEmploymentDetail.setLovDescCustEmpTypeName(this.custEmpType.getDescription());
	 		aCustomerEmploymentDetail.setCustEmpType(this.custEmpType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aCustomerEmploymentDetail.setCurrentEmployer(this.currentEmployer.isChecked());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}



		//		try {
		//		    aCustomerEmploymentDetail.setCustEmpHNbr(this.custEmpHNbr.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//		    aCustomerEmploymentDetail.setCustEMpFlatNbr(this.custEMpFlatNbr.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//		    aCustomerEmploymentDetail.setCustEmpAddrStreet(this.custEmpAddrStreet.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//		    aCustomerEmploymentDetail.setCustEMpAddrLine1(this.custEMpAddrLine1.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//		    aCustomerEmploymentDetail.setCustEMpAddrLine2(this.custEMpAddrLine2.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//		    aCustomerEmploymentDetail.setCustEmpPOBox(this.custEmpPOBox.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//	 		aCustomerEmploymentDetail.setLovDescCustEmpAddrCityName(this.lovDescCustEmpAddrCityName.getValue());
		//	 		aCustomerEmploymentDetail.setCustEmpAddrCity(this.custEmpAddrCity.getValue());	
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//	 		aCustomerEmploymentDetail.setLovDescCustEmpAddrProvinceName(this.lovDescCustEmpAddrProvinceName.getValue());
		//	 		aCustomerEmploymentDetail.setCustEmpAddrProvince(this.custEmpAddrProvince.getValue());	
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//	 		aCustomerEmploymentDetail.setLovDescCustEmpAddrCountryName(this.lovDescCustEmpAddrCountryName.getValue());
		//	 		aCustomerEmploymentDetail.setCustEmpAddrCountry(this.custEmpAddrCountry.getValue());	
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//		    aCustomerEmploymentDetail.setCustEmpAddrZIP(this.custEmpAddrZIP.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
		//		try {
		//		    aCustomerEmploymentDetail.setCustEmpAddrPhone(this.custEmpAddrPhone.getValue());
		//		}catch (WrongValueException we ) {
		//			wve.add(we);
		//		}
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
		
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custEmpName.focus();
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
		if(this.isCurrentEmp){
			this.currentEmployer.setDisabled(true);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerEmploymentDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			checkCurretEmployer();
			doStoreInitValues();
			doCheckEnquiry();
			if(isNewCustomer()){
				this.window_CustomerEmploymentDetailDialog.setHeight("35%");
				this.window_CustomerEmploymentDetailDialog.setWidth("80%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerEmploymentDetailDialog.doModal() ;
			}else{
				this.window_CustomerEmploymentDetailDialog.setWidth("100%");
				this.window_CustomerEmploymentDetailDialog.setHeight("100%");
				setDialog(this.window_CustomerEmploymentDetailDialog);
			}
			
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
		}
	}

	private void doCheckEnquiry() {
		if("ENQ".equals(this.moduleType)){
			this.custEmpFrom.setDisabled(true);
			this.custEmpTo.setDisabled(true);
			this.custEmpDesg.setReadonly(true);
			this.custEmpDept.setReadonly(true);
			this.custEmpType.setReadonly(true);
			this.currentEmployer.setDisabled(true);
			
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
		this.oldVar_custEmpName = Long.valueOf(this.custEmpName.getValue());
		this.oldVar_custEmpFrom = this.custEmpFrom.getValue();	
		this.oldVar_custEmpTo = this.custEmpTo.getValue();	
		this.oldVar_currentEmployer = this.currentEmployer.isChecked();
 		this.oldVar_custEmpDesg = this.custEmpDesg.getValue();
 		this.oldVar_lovDescCustEmpDesgName = this.custEmpDesg.getDescription();
 		this.oldVar_custEmpDept = this.custEmpDept.getValue();
 		this.oldVar_lovDescCustEmpDeptName = this.custEmpDept.getDescription();
 		this.oldVar_custEmpType = this.custEmpType.getValue();
		this.oldVar_lovDescCustEmpTypeName = this.custEmpType.getDescription();
 		this.oldVar_recordStatus = this.recordStatus.getValue();
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.custID.setValue(this.oldVar_custID);
		this.custEmpName.setValue(String.valueOf(this.oldVar_custEmpName));
		this.custEmpFrom.setValue(this.oldVar_custEmpFrom);
		this.custEmpTo.setValue(this.oldVar_custEmpTo);
		this.currentEmployer.setChecked(this.oldVar_currentEmployer);
 		this.custEmpDesg.setValue(this.oldVar_custEmpDesg);
 		this.custEmpDesg.setDescription(this.oldVar_lovDescCustEmpDesgName);
 		this.custEmpDept.setValue(this.oldVar_custEmpDept);
 		this.custEmpDept.setDescription(this.oldVar_lovDescCustEmpDeptName);
 		this.custEmpType.setValue(this.oldVar_custEmpType);
 		this.custEmpType.setDescription(this.oldVar_lovDescCustEmpTypeName);
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
		if (this.oldVar_custEmpFrom != this.custEmpFrom.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpTo != this.custEmpTo.getValue()) {
			return true;
		}
		if (this.oldVar_currentEmployer!= this.currentEmployer.isChecked()) {
			return true;
		}
		if (this.oldVar_custEmpDesg != this.custEmpDesg.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpDept != this.custEmpDept.getValue()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_custEmpType).equals(StringUtils.trimToEmpty(this.custEmpType.getValue()))) {
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
		if (this.custEmpName.isButtonVisible()) {
			this.custEmpName.setConstraint(	new PTStringValidator(Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpName.value"),null,true,true));
		}
		/*if (this.custEmpName.isButtonVisible()){
			this.custEmpName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
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
		this.custEmpName.setConstraint("");
		this.custEmpFrom.setConstraint("");
		this.custEmpTo.setConstraint("");
		
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		
		this.custEmpDesg.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpDesg.value"),null,true,true));
		
		this.custEmpDept.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpDept.value"),null,true,true));
		
		this.custEmpType.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpType.value"),null,true,true));
		
		
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custEmpDesg.setConstraint("");
		this.custEmpDept.setConstraint("");
		this.custEmpType.setConstraint("");
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
		this.custEmpTo.setErrorMessage("");
		this.custEmpDesg.setErrorMessage("");
		this.custEmpDept.setErrorMessage("");
		this.custEmpType.setErrorMessage("");
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
									"\n\n --> " + aCustomerEmploymentDetail.getCustID()+" with "+aCustomerEmploymentDetail.getLovDesccustEmpName();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerEmploymentDetail.getRecordType()).equals("")){
				aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion()+1);
				aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerEmploymentDetail.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()){
					aCustomerEmploymentDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCusomerProcess(aCustomerEmploymentDetail,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerEmploymentDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerEmploymentDetail(this.customerEmploymentDetails);
						// send the data back to customer
						closeWindow();
					}	

				}else if(doProcess(aCustomerEmploymentDetail,tranType)){
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
		this.custEmpDesg.getButton().focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		this.custID.setReadonly(true);
		
		if (isNewRecord()){
			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			this.btnDelete.setVisible(false);
			this.custEmpName.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpName"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custEmpName.setReadonly(true);
		}
		this.custCIF.setReadonly(true);
	 	this.custEmpFrom.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpFrom"));
	 	this.custEmpTo.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpFrom"));
	 	this.currentEmployer.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpFrom"));
	  	this.custEmpDesg.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpDesg"));
	  	this.custEmpDesg.setMandatoryStyle(!isReadOnly("CustomerEmploymentDetailDialog_custEmpDesg"));
	  	this.custEmpDept.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpDept"));
	  	this.custEmpDept.setMandatoryStyle(!isReadOnly("CustomerEmploymentDetailDialog_custEmpDept"));
	  	this.custEmpType.setReadonly(isReadOnly("CustomerEmploymentDetailDialog_custEmpType"));

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
		
		this.custID.setReadonly(true);
		this.custEmpName.setReadonly(true);
		this.custEmpFrom.setDisabled(true);
		this.custEmpTo.setDisabled(true);
		this.currentEmployer.setDisabled(true);
		this.custEmpDesg.setReadonly(true);
		this.custEmpDept.setReadonly(true);
		this.custEmpType.setReadonly(true);
		
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
		this.custEmpFrom.setText("");
		this.custEmpTo.setText("");
	  	this.custEmpDesg.setValue("");
		this.custEmpDesg.setDescColumn("");
	  	this.custEmpDept.setValue("");
		this.custEmpDept.setDescription("");
	  	this.custEmpType.setValue("");
		this.custEmpType.setDescription("");
		
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


			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerEmploymentDetail.setVersion(1);
					aCustomerEmploymentDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerEmploymentDetail.getRecordType()).equals("")){
					aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion()+1);
					aCustomerEmploymentDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerEmploymentDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerEmploymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion()+1);
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
				AuditHeader auditHeader =  newCusomerProcess(aCustomerEmploymentDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerEmploymentDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerEmploymentDetail(this.customerEmploymentDetails);
					//true;
					// send the data back to customer
					closeWindow();

				}

			}else if(doProcess(aCustomerEmploymentDetail,tranType)){
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
	private AuditHeader newCusomerProcess(CustomerEmploymentDetail aCustomerRating,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aCustomerRating, tranType);
		customerEmploymentDetails = new ArrayList<CustomerEmploymentDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerRating.getId());
		valueParm[1] = String.valueOf(aCustomerRating.getLovDesccustEmpName());

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustEmpName") + ":"+valueParm[1];

		if(getCustomerDialogCtrl().getCustomerEmploymentDetailList()!=null && getCustomerDialogCtrl().getCustomerEmploymentDetailList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerEmploymentDetailList().size(); i++) {
				CustomerEmploymentDetail customerRating = getCustomerDialogCtrl().getCustomerEmploymentDetailList().get(i);
				if(customerRating.getCustEmpName() == aCustomerRating.getCustEmpName()){ // Both Current and Existing list rating same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}


					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerEmploymentDetails.add(aCustomerRating);
						}else if(aCustomerRating.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerEmploymentDetails.add(aCustomerRating);
						}else if(aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getRatingsList().size(); j++) {
								CustomerEmploymentDetail rating =  getCustomerDialogCtrl().getCustomerDetails().getEmploymentDetailsList().get(j);
								if(rating.getCustID() == aCustomerRating.getCustID() && rating.getCustEmpName()==aCustomerRating.getCustEmpName()){
									customerEmploymentDetails.add(rating);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerEmploymentDetails.add(customerRating);
						}
					}
				}else{
					customerEmploymentDetails.add(customerRating);
				}
			}
		}
		if(!recordAdded){
			customerEmploymentDetails.add(aCustomerRating);
		}
		return auditHeader;
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

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}
	
	public void onCheck$currentEmployer(Event event){
		checkCurretEmployer();
	}
	
	private void checkCurretEmployer(){
		if (this.currentEmployer.isChecked()) {
			this.custEmpTo.setReadonly(true);
			this.custEmpTo.setDisabled(true);
			this.custEmpTo.setValue(null);
		}else{
			this.custEmpTo.setReadonly(false);
			this.custEmpTo.setDisabled(isReadOnly("CustomerEmploymentDetailDialog_custEmpFrom"));
		}
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
