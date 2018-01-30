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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetail;
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
import com.pennant.webui.customermasters.customer.CustomerEnquiryDialogCtrlr;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail
 * /customerEmploymentDetailDialog.zul file.
 */
public class CustomerEmploymentDetailDialogCtrl extends GFCBaseCtrl<CustomerEmploymentDetail> {
	private static final long serialVersionUID = -4626382073313654611L;
	private static final Logger logger = Logger.getLogger(CustomerEmploymentDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  window_CustomerEmploymentDetailDialog; 	

   	protected Longbox custID; 									
	protected ExtendedCombobox custEmpName; 								
	
  	protected Datebox custEmpFrom; 								
  	protected Datebox custEmpTo; 								
  	protected Checkbox currentEmployer; 						
	protected ExtendedCombobox custEmpDesg; 								
	protected ExtendedCombobox custEmpDept; 								
	protected ExtendedCombobox custEmpType; 								
	
	protected Textbox custCIF;									
	protected Label   custShrtName;								

	// not auto wired variables
	private CustomerEmploymentDetail customerEmploymentDetail; // overHanded per parameter
	private transient CustomerEmploymentDetailListCtrl customerEmploymentDetailListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	protected Button btnSearchPRCustid; 
	
	// ServiceDAOs / Domain Classes
	private transient CustomerEmploymentDetailService customerEmploymentDetailService;
	private transient CustomerSelectCtrl customerSelectCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	
	private boolean newRecord=false;
	private boolean newCustomer=false;
	private CustomerDialogCtrl customerDialogCtrl;

	public CustomerEnquiryDialogCtrlr getCustomerEnquiryDialogCtrlr() {
		return customerEnquiryDialogCtrlr;
	}

	public void setCustomerEnquiryDialogCtrlr(CustomerEnquiryDialogCtrlr customerEnquiryDialogCtrlr) {
		this.customerEnquiryDialogCtrlr = customerEnquiryDialogCtrlr;
	}

	private CustomerEnquiryDialogCtrlr customerEnquiryDialogCtrlr;
	private List<CustomerEmploymentDetail> customerEmploymentDetails;
	private String moduleType="";
	private String userRole="";
	private boolean isCurrentEmp = false;
	private boolean isFinanceProcess = false;
	/**
	 * default constructor.<br>
	 */
	public CustomerEmploymentDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerEmploymentDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerEmploymentDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerEmploymentDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerEmploymentDetailDialog);

		if (arguments.containsKey("customerEmploymentDetail")) {
			this.customerEmploymentDetail = (CustomerEmploymentDetail) arguments.get("customerEmploymentDetail");
			CustomerEmploymentDetail befImage =new CustomerEmploymentDetail();
			BeanUtils.copyProperties(this.customerEmploymentDetail, befImage);
			this.customerEmploymentDetail.setBefImage(befImage);
			setCustomerEmploymentDetail(this.customerEmploymentDetail);
		} else {
			setCustomerEmploymentDetail(null);
		}
	
		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}
		if (arguments.containsKey("currentEmployer")) {
			this.isCurrentEmp = (Boolean) arguments.get("currentEmployer");
		}
		if(getCustomerEmploymentDetail().isNewRecord()){
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
			this.customerEmploymentDetail.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerEmploymentDetailDialog");
			}
		}
		
		if (arguments.containsKey("customerEnquiryDialogCtrlr")) {

			setCustomerEnquiryDialogCtrlr((CustomerEnquiryDialogCtrlr) arguments.get("customerEnquiryDialogCtrlr"));
			setNewCustomer(true);

			if(arguments.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerEmploymentDetail.setWorkflowId(0);
			if(arguments.containsKey("roleCode")){
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerEmploymentDetailDialog");
			}
		}
		if (arguments.containsKey("isFinanceProcess")) {
			isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
		}
		doLoadWorkFlow(this.customerEmploymentDetail.isWorkflow(),
				this.customerEmploymentDetail.getWorkflowId(),this.customerEmploymentDetail.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerEmploymentDetailDialog");
		}
	
		// READ OVERHANDED parameters !
		// we get the customerEmploymentDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerEmploymentDetail here.
		if (arguments.containsKey("customerEmploymentDetailListCtrl")) {
			setCustomerEmploymentDetailListCtrl((CustomerEmploymentDetailListCtrl) arguments.get(
					"customerEmploymentDetailListCtrl"));
		} else {
			setCustomerEmploymentDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerEmploymentDetail());
		
		//Calling SelectCtrl For proper selection of Customer
		if (isNewRecord() && !isNewCustomer()) {
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
		this.custEmpName.setDescColumn("EmpCity");
		this.custEmpName.setValidateColumns(new String[] { "EmpName" });
		
	 	this.custEmpFrom.setFormat(DateFormat.SHORT_DATE.getPattern());
	 	this.custEmpTo.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custEmpDesg.setMaxlength(8);
		this.custEmpDesg.setMandatoryStyle(true);
		this.custEmpDesg.setTextBoxWidth(110);
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
		getUserWorkspace().allocateAuthorities("CustomerEmploymentDetailDialog",userRole);
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerEmploymentDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerEmploymentDetailDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.customerEmploymentDetail.getBefImage());
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
		    aCustomerEmploymentDetail.setCustEmpName(Long.parseLong(this.custEmpName.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (this.custEmpFrom.getValue() != null) {
				if (!this.custEmpFrom.getValue().after(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"))) {
					throw new WrongValueException(this.custEmpFrom, Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value"),
									SysParamUtil.getValueAsString("APP_DFT_START_DATE") }));
				}
				if (this.custEmpFrom.getValue().compareTo(DateUtility.getAppDate()) != -1) {
					throw new WrongValueException(this.custEmpFrom, Labels.getLabel("DATE_FUTURE_TODAY",
							new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value"),
									SysParamUtil.getValueAsString("APP_DFT_START_DATE") }));
				}
				aCustomerEmploymentDetail.setCustEmpFrom(new Timestamp(this.custEmpFrom.getValue().getTime()));
			} else {
				aCustomerEmploymentDetail.setCustEmpFrom(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (this.custEmpTo.getValue()!=null) {
				if (!this.custEmpTo.getValue().after(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"))) {
					throw new WrongValueException(this.custEmpTo, Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpTo.value"),
									SysParamUtil.getValueAsString("APP_DFT_START_DATE") }));
				}
				if (this.custEmpTo.getValue().compareTo(DateUtility.getAppDate()) != -1) {
					throw new WrongValueException(this.custEmpTo, Labels.getLabel("DATE_FUTURE_TODAY", new String[] {
							Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpTo.value"),
							SysParamUtil.getValueAsString("APP_DFT_START_DATE") }));
				}
				aCustomerEmploymentDetail.setCustEmpTo(new Timestamp(this.custEmpTo.getValue().getTime()));
			}else{
				aCustomerEmploymentDetail.setCustEmpTo(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			if (this.custEmpTo.getValue()!=null && this.custEmpFrom.getValue()!=null) {
				if (this.custEmpTo.getValue().before(this.custEmpFrom.getValue())) {
					throw new WrongValueException(this.custEmpFrom,  Labels.getLabel("DATE_NOT_AFTER", 
							new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value"),
							Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpTo.value") }));
					
				}
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

			checkCurretEmployer();
			
			doCheckEnquiry();
			
			if(isNewCustomer()){
				this.window_CustomerEmploymentDetailDialog.setHeight("35%");
				this.window_CustomerEmploymentDetailDialog.setWidth("80%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerEmploymentDetailDialog.doModal() ;
			}else{
				this.window_CustomerEmploymentDetailDialog.setWidth("100%");
				this.window_CustomerEmploymentDetailDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
			
		} catch (Exception e) {
			MessageUtil.showError(e);
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

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();
		setValidationOn(true);
		
		if(!this.custID.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerEmploymentDetailDialog_CustID.value"),null,true));
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
	@Override
	protected void doClearMessage() {
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
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCustomerEmploymentDetailListCtrl().search();
	}

	// CRUD operations

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
									"\n\n --> " +Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpName.value")+" : "+aCustomerEmploymentDetail.getLovDesccustEmpName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerEmploymentDetail.getRecordType())){
				aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion()+1);
				aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(!isFinanceProcess && getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
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
						closeDialog();
					}	

				}else if(doProcess(aCustomerEmploymentDetail,tranType)){
					refreshList();
					closeDialog(); 
				}
			}catch (DataAccessException e){
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
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
		
		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aCustomerEmploymentDetail.getRecordType())){
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

				if(StringUtils.isBlank(aCustomerEmploymentDetail.getRecordType())){
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
					closeDialog();

				}

			}else if(doProcess(aCustomerEmploymentDetail,tranType)){
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
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
								new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}


					if (PennantConstants.TRAN_DEL.equals(tranType)) {
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
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
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
		
		aCustomerEmploymentDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerEmploymentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerEmploymentDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerEmploymentDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerEmploymentDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerEmploymentDetail);
				}

				if (isNotesMandatory(taskId, aCustomerEmploymentDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");
				
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {
						
						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCustomerEmploymentDetail.setTaskId(taskId);
			aCustomerEmploymentDetail.setNextTaskId(nextTaskId);
			aCustomerEmploymentDetail.setRoleCode(getRole());
			aCustomerEmploymentDetail.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCustomerEmploymentDetail, tranType);
			
			String operationRefs = getServiceOperations(taskId, aCustomerEmploymentDetail);
			
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
				if (StringUtils.isBlank(method)){
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, 
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
						deleteNotes(getNotes(this.customerEmploymentDetail), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	// Search Button Component Events

   
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
		map.put("custCtgType",PennantConstants.PFF_CUSTCTG_INDIV);
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

	// WorkFlow Components

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
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerEmploymentDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.customerEmploymentDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.customerEmploymentDetail.getCustID());
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
	
}
