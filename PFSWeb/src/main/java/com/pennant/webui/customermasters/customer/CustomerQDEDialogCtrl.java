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
 * FileName    		:  CustomerQDEDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customer;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerQDE;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/customerQDEDialog.zul file.
 */
public class CustomerQDEDialogCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final long serialVersionUID = 398317712417132602L;
	private static final Logger logger = Logger.getLogger(CustomerQDEDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_CustomerQDEDialog; 					
	
	protected Textbox 	custCIF; 									
	protected Textbox 	custCoreBank; 								
	protected Textbox 	custCtgCode; 								
	protected Textbox 	custTypeCode; 								
	protected Textbox 	custParentCountry; 							
	
	//Retail Customer Details
	protected Textbox 	custFName; 									
	protected Textbox 	custMName; 									
	protected Textbox 	custLName; 									
	protected Datebox 	custDOB; 									
	protected Textbox 	custPassportNo;								
	protected Textbox 	custVisaNum;								
	protected Textbox 	custTradeLicenceNum; 						
	
	//Row(s) declaration visibility depend on selection of Customer Category 
	protected Row 		row_retailCustomerNames;					
	protected Row 		row_corpCustomerTL;							
	protected Row 		row_retailCustomerPPT;						
	
	//Set Fields are mandatory or not
	protected Space 	space_CustCoreBank;							
	protected Hbox		hbox_visaNum;								
	
	//Label(s) declarations of fields
	protected Label 	label_CustomerDialog_CustOrgName;			
	protected Label 	label_CustomerDialog_CustLName;				
	protected Label 	label_CustomerDialog_CustDOB;				
	protected Label 	label_CustomerDialog_CustDateOfIncorporation;
	protected Label 	label_CustomerDialog_CustVisaNum;			
	
	//LOV field declarations
	protected Textbox 	lovDescCustCtgCodeName;						
	protected Button 	btnSearchCustCtgCode; 						
	protected Textbox 	lovDescCustTypeCodeName;					
	protected Button 	btnSearchCustTypeCode; 						
	protected Textbox 	lovDescCustParentCountryName;				
	protected Button 	btnSearchCustParentCountry; 				
	
	private transient boolean validationOn;
	private transient String CUSTCIF_REGEX;
	private CustomerDetails customerDetails;
	private FinanceDetail financeDetail;
	private transient CustomerDetailsService customerDetailsService;
	private transient FinanceDetailService financeDetailService;
	private transient DedupParmService dedupParmService;
	private transient CustomerInterfaceService customerInterfaceService;
	
	private String custCtgType = "";
	Date appStartDate = DateUtility.getAppDate();
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	
	/**
	 * default constructor.<br>
	 */
	public CustomerQDEDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerQDE";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerQDEDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerQDEDialog);
		if (arguments.containsKey("customerDetails")) {
			this.customerDetails = (CustomerDetails) arguments.get("customerDetails");
			CustomerDetails befImage = new CustomerDetails();
			BeanUtils.copyProperties(this.customerDetails, befImage);
			this.customerDetails.setBefImage(befImage);
			setCustomerDetails(this.customerDetails);
		} else {
			setCustomerDetails(null);
		}
		
		// READ OVERHANDED params !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			
			if(arguments.containsKey("roleCode")){
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "CustomerDialog");
			}
			
		} else {
			this.financeDetail = null;
		}

		Customer customer = getCustomerDetails().getCustomer();
		doLoadWorkFlow(customer.isWorkflow(), customer.getWorkflowId(),customer.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction, false);
			if(this.financeDetail != null){
				if (this.userAction.getItemAtIndex(0) != null && 
						"Rejected".equals(this.userAction.getItemAtIndex(0).getValue())) {
					this.userAction.getItemAtIndex(0).setVisible(false);
					this.userAction.getItemAtIndex(1).setSelected(true);
				}
			}
			getUserWorkspace().allocateRoleAuthorities(getRole(),"CustomerDialog");
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(this.customerDetails);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.custCoreBank.setMaxlength(50);
		this.custCtgCode.setMaxlength(8);
		this.custFName.setMaxlength(50);
		this.custMName.setMaxlength(50);
		this.custLName.setMaxlength(50);
		this.custDOB.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custParentCountry.setMaxlength(2);
		this.custTradeLicenceNum.setMaxlength(20);
		this.custVisaNum.setMaxlength(20);
		this.custPassportNo.setMaxlength(50);
		this.CUSTCIF_REGEX = "[" + SysParamUtil.getValueAsString("CIF_CHAR") + "]{"
				+ SysParamUtil.getValueAsString("CIF_LENGTH") + "}";
		this.custCIF.setMaxlength(SysParamUtil.getValueAsInt("CIF_LENGTH"));

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 * @throws ParseException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		try {
			doSave();
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			MessageUtil.showError("Customer Not Created.");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerDetails aCustomerDetails)
			throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCustomerDetails.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				btnCancel.setVisible(false);
			}
		}
		
		this.custCIF.focus();
		setDialog(DialogType.EMBEDDED);
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (customerDetails.isNewRecord()) {
			this.btnCancel.setVisible(false);
		}
		
		this.custCIF.setReadonly(true);//isReadOnly("CustomerDialog_custCIF")
		if ("CIF".equals(SysParamUtil.getValueAsString("CB_CID"))) {
			this.custCoreBank.setReadonly(true);
			this.space_CustCoreBank.setSclass("");
		} else {
			this.custCoreBank.setReadonly(isReadOnly("CustomerDialog_custCoreBank"));
		}
		
		this.btnSearchCustCtgCode.setDisabled(isReadOnly("CustomerDialog_custCtgCode"));
		this.btnSearchCustTypeCode.setDisabled(isReadOnly("CustomerDialog_custCtgCode"));
		this.btnSearchCustParentCountry.setDisabled(isReadOnly("CustomerDialog_custParentCountry"));
		this.custFName.setReadonly(isReadOnly("CustomerDialog_custFName"));
		this.custMName.setReadonly(isReadOnly("CustomerDialog_custMName"));
		this.custLName.setReadonly(isReadOnly("CustomerDialog_custLName"));
		this.custDOB.setReadonly(isReadOnly("CustomerDialog_custDOB"));
		this.custTradeLicenceNum.setReadonly(isReadOnly("CustomerDialog_custTradeLicenceNum"));
		this.custVisaNum.setReadonly(isReadOnly("CustomerDialog_custVisaNum"));
		this.custPassportNo.setReadonly(isReadOnly("CustomerDialog_custPassportNo"));
		
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.custCoreBank.setReadonly(true);
		this.btnSearchCustCtgCode.setDisabled(true);
		this.btnSearchCustTypeCode.setDisabled(true);
		this.custDOB.setReadonly(true);
		this.custFName.setReadonly(true);
		this.custMName.setReadonly(true);
		this.custLName.setReadonly(true);
		this.custVisaNum.setReadonly(true);
		this.custPassportNo.setReadonly(true);
		this.custTradeLicenceNum.setReadonly(true);
		this.btnSearchCustParentCountry.setDisabled(true);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException 
	 */
	public void doSave() throws InterruptedException, ParseException, InterfaceException {
		logger.debug("Entering");
		
		final CustomerDetails aCustomerDetails = new CustomerDetails();
		BeanUtils.copyProperties(getCustomerDetails(), aCustomerDetails);
		boolean isNew = false;
		Customer aCustomer = aCustomerDetails.getCustomer();
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Customer object with the components data
		doWriteComponentsToBean(aCustomerDetails);
		aCustomer = aCustomerDetails.getCustomer();
		aCustomer.setCustomerQDE(aCustomerDetails.getCustomer().getCustomerQDE());
		
		//Create new CIF By Using Core Banking System
		if(this.financeDetail != null && StringUtils.isBlank(aCustomer.getCustCIF())){
			aCustomer.setLovDescCustCtgType(custCtgType);
			String custCIF = getCustomerInterfaceService().generateNewCIF("B", aCustomer,
					this.financeDetail.getFinScheduleData().getFinReference());
			aCustomer.setCustCIF(custCIF);
			if ("CIF".equals(SysParamUtil.getValueAsString("CB_CID"))) {
				aCustomer.setCustCoreBank(custCIF);
			}
		}
		
		// Show a confirm box
		final String msg = "Generated Customer CIF "+aCustomer.getCustCIF();

		MessageUtil.showMessage(msg);

		isNew = aCustomerDetails.isNewRecord();

		String tranType = "";

		if (isWorkFlowEnabled()) {
			if (StringUtils.isBlank(aCustomer.getRecordType())) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
				if (isNew) {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomer.setNewRecord(true);
					aCustomerDetails.setNewRecord(true);
				}
			}
		} else {
			aCustomer.setVersion(aCustomer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			aCustomerDetails.setCustomer(aCustomer);
			if (doProcess(aCustomerDetails, tranType)) {
				
				if(this.financeDetail != null){
					getFinanceDetailService().updateCustCIF(aCustomerDetails.getCustID(), 
							this.financeDetail.getFinScheduleData().getFinReference());
				}
				// Close the Existing Dialog
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.info(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations
	
	private String getServiceTasks(String taskId, Customer aCustomer,
			String finishedTasks) {
		logger.debug("Entering");
		
		String serviceTasks = getServiceOperations(taskId, aCustomer);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, Customer aCustomer) {
		logger.debug("Entering");
		
		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(aCustomer.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, aCustomer);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode = getTaskOwner(nextTasks[i]);
				}
			}
		}

		aCustomer.setTaskId(taskId);
		aCustomer.setNextTaskId(nextTaskId);
		aCustomer.setRoleCode(getRole());
		aCustomer.setNextRoleCode(nextRoleCode);
		logger.debug("Leaving");
		
	}

	/**
	 * Method to process the entered details
	 * 
	 * @param aCustoemrDetails
	 *            (CustomerDetails)
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * @throws CustomerNotFoundException 
	 * */
	private boolean doProcess(CustomerDetails aCustomerDetails, String tranType) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		Customer aCustomer = aCustomerDetails.getCustomer();

		aCustomer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomer.setUserDetails(getUserWorkspace().getLoggedInUser());

		aCustomerDetails.setCustID(aCustomer.getCustID());
		aCustomerDetails.setCustomer(aCustomer);
		aCustomerDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aCustomer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check whether required auditing notes entered or not
			if (isNotesMandatory(taskId, aCustomer)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aCustomer,finishedTasks);

			while (!"".equals(serviceTasks)) {
				String method = serviceTasks.split(";")[0];

				if ("doQdeDedup".equals(method)) {
					aCustomerDetails = FetchDedupDetails.getCustomerDedup(getRole(),
							aCustomerDetails, this.window_CustomerQDEDialog);
					if(aCustomerDetails.getCustomer().isDedupFound() && !aCustomerDetails.getCustomer().isSkipDedup()){
						processCompleted =false;						
					}else{
						processCompleted =true;	
					}

				}else {
					setNextTaskDetails(taskId, aCustomer);
					aCustomerDetails.setCustomer(aCustomer);
					auditHeader = getAuditHeader(aCustomerDetails, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, method);
					System.out.println("Testing");
				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				serviceTasks = getServiceTasks(taskId, aCustomer, finishedTasks);
			}

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, aCustomer);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {
				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aCustomer);
					aCustomerDetails.setCustomer(aCustomer);
					auditHeader = getAuditHeader(aCustomerDetails, tranType);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerDetails, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method to do save process
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * @throws CustomerNotFoundException 
	 * */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerDetailsService().delete(auditHeader);
					} else {
						auditHeader = getCustomerDetailsService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCustomerDetailsService().doApprove(auditHeader);
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerDetailsService().doReject(auditHeader);
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerQDEDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_CustomerQDEDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
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
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.custCIF.setValue("");
		this.custCoreBank.setValue("");
		this.custCtgCode.setValue("");
		this.lovDescCustCtgCodeName.setValue("");
		this.custFName.setValue("");
		this.custMName.setValue("");
		this.custLName.setValue("");
		this.custDOB.setText("");
		this.custVisaNum.setValue("");
		this.custTradeLicenceNum.setValue("");
		this.custParentCountry.setValue("");
		this.custPassportNo.setValue("");
		this.lovDescCustParentCountryName.setValue("");
		logger.debug("Leaving ");
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
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		// Basic Details Tab
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel(
					"MAND_FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel("label_CustomerDialog_CustCIF.value"),
					SysParamUtil.getValueAsString("CIF_CHAR"),
					SysParamUtil.getValueAsString("CIF_LENGTH") }),this.CUSTCIF_REGEX));
		}
		if (!this.custCoreBank.isReadonly() || "CIF".equals(SysParamUtil.getValueAsString("CB_CID"))) {
			/*this.custCoreBank.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCoreBank.value"),null,true));*/
		}
		this.custDOB.clearErrorMessage();
		if(this.row_retailCustomerPPT.isVisible() && this.row_retailCustomerNames.isVisible()){
			if (!this.custFName.isReadonly()) {
				this.custFName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustFName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.custMName.isReadonly()) {
				this.custMName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustMName.value"), PennantRegularExpressions.REGEX_NAME, false));
			}
			if (!this.custLName.isReadonly()) {
				this.custLName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustLName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.custDOB.isReadonly() ) {
				this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerDialog_CustDOB.value"),true,startDate,appStartDate,false));
			} 
			if (!this.custPassportNo.isReadonly() && this.custPassportNo.isVisible()) {
				this.custPassportNo.setConstraint(new PTStringValidator(Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
						"label_CustomerDialog_CustPassportNo.value") }),PennantRegularExpressions.REGEX_ALPHANUM_SPL));
			}
			if (!this.custVisaNum.isReadonly() && this.hbox_visaNum.isVisible()) {
				this.custVisaNum.setConstraint(new PTStringValidator(
						Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
						"label_CustomerDialog_CustVisaNum.value") }),PennantRegularExpressions.REGEX_ALPHANUM_SPL));
			}
		}else if(this.row_corpCustomerTL.isVisible()){

			if (!this.custLName.isReadonly()) {
				this.custLName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustOrgName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.custTradeLicenceNum.isReadonly() && this.custTradeLicenceNum.isVisible()) {
				this.custTradeLicenceNum.setConstraint(new PTStringValidator(Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
						"label_CustomerDialog_CustTradeLicenceNum.value") }),PennantRegularExpressions.ALPHA_NUMERIC_REGEX));
			}
			if (!this.custDOB.isReadonly() ) {
				this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"),true,startDate,appStartDate,false));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer
	 *            Customer
	 */
	public void doWriteBeanToComponents(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");
		Customer aCustomer = aCustomerDetails.getCustomer();

		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		this.custCoreBank.setValue(aCustomer.getCustCoreBank());
		this.custCtgCode.setValue(aCustomer.getCustCtgCode());
		this.custTypeCode.setValue(aCustomer.getCustTypeCode());
		this.custFName.setValue(aCustomer.getCustFName());
		this.custMName.setValue(aCustomer.getCustMName());
		this.custLName.setValue(aCustomer.getCustLName());
		this.custDOB.setValue(aCustomer.getCustDOB());
		this.custTradeLicenceNum.setValue(aCustomer.getCustTradeLicenceNum());
		this.custVisaNum.setValue(aCustomer.getCustVisaNum());
		this.custPassportNo.setValue(aCustomer.getCustVisaNum());
		this.custParentCountry.setValue(aCustomer.getCustParentCountry() == null ? SysParamUtil
				.getValueAsString("APP_DFT_COUNTRY") : aCustomer.getCustParentCountry());

		if (aCustomerDetails.isNewRecord()) {
			this.lovDescCustCtgCodeName.setValue("");
			this.lovDescCustTypeCodeName.setValue("");
			this.lovDescCustParentCountryName.setValue("");
		} else {
			this.lovDescCustCtgCodeName.setValue(aCustomer.getCustCtgCode()
					+ "-" + aCustomer.getLovDescCustCtgCodeName());
			this.lovDescCustTypeCodeName.setValue(aCustomer.getCustTypeCode()
					+ "-" + aCustomer.getLovDescCustTypeCodeName());
			this.lovDescCustParentCountryName.setValue(aCustomer.getCustParentCountry()
					+ "-"+ aCustomer.getLovDescCustParentCountryName());
		}
		this.recordStatus.setValue(aCustomer.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomer
	 * @throws ParseException
	 */
	public void doWriteComponentsToBean(CustomerDetails aCustomerDetails)
			throws ParseException {
		logger.debug("Entering");
		CustomerQDE aCustomer = aCustomerDetails.getCustomer().getCustomerQDE();

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCustomer.setCustCIF(StringUtils.trimToEmpty(this.custCIF.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustCoreBank(this.custCoreBank.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustCtgCodeName(this.lovDescCustCtgCodeName.getValue());
			aCustomer.setCustCtgCode(this.custCtgCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustFName(this.custFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustMName(this.custMName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustLName(this.custLName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustTypeCode(this.custTypeCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustDOB(new Timestamp(this.custDOB.getValue().getTime()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustTradeLicenceNum(this.custTradeLicenceNum.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustPassportNo(this.custPassportNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustVisaNum(this.custVisaNum.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustParentCountryName(this.lovDescCustParentCountryName.getValue());
			aCustomer.setCustParentCountry(this.custParentCountry.getValue());
			aCustomerDetails.getCustomer().setCustCOB(this.custParentCountry.getValue());
			aCustomerDetails.getCustomer().setLovDescCustCOBName(this.lovDescCustParentCountryName.getValue());
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
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.custCoreBank.setConstraint("");
		this.custFName.setConstraint("");
		this.custMName.setConstraint("");
		this.custLName.setConstraint("");
		this.custDOB.setConstraint("");
		this.custTradeLicenceNum.setConstraint("");
		this.custVisaNum.setConstraint("");
		this.custPassportNo.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method to set LOV validation
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustCtgCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCtgCode.value"),null,true));
		this.lovDescCustParentCountryName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustParentCountry.value"),null,true));
		logger.debug("Leaving");
	}

	/**
	 * Method to remove LOV validation on
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustCtgCodeName.setConstraint("");
		this.lovDescCustParentCountryName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.custCIF.clearErrorMessage();
		this.custCoreBank.clearErrorMessage();
		this.custFName.clearErrorMessage();
		this.custLName.clearErrorMessage();
		this.lovDescCustCtgCodeName.clearErrorMessage();
		this.lovDescCustParentCountryName.clearErrorMessage();
		this.custDOB.clearErrorMessage();
		this.custTradeLicenceNum.clearErrorMessage();
		this.custPassportNo.clearErrorMessage();
		this.custVisaNum.clearErrorMessage();
		logger.debug("Leaving");
	}

	// Search Button Component Events
	
	public void onClick$btnSearchCustCtgCode(Event event) {
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerQDEDialog, "CustomerCategory");
		
		if (dataObject instanceof String) {
			this.custCtgCode.setValue(dataObject.toString());
			this.lovDescCustCtgCodeName.setValue("");
			setFieldValues(true);	
		} else {
			CustomerCategory details = (CustomerCategory) dataObject;
			if (details != null) {
				this.custCtgCode.setValue(details.getCustCtgCode());
				this.lovDescCustCtgCodeName.setValue(details.getCustCtgCode()+ "-" + details.getCustCtgDesc());
				
				getCustomerDetails().getCustomer().setLovDescCustCtgType(details.getCustCtgType());
				
				if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, details.getCustCtgType())) {
					setFieldValues(false);
				} else if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, details.getCustCtgType())) {
					setFieldValues(true);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnSearchCustTypeCode(Event event) {
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerQDEDialog, "CustomerType");
		
		if (dataObject instanceof String) {
			this.custTypeCode.setValue(dataObject.toString());
			this.lovDescCustTypeCodeName.setValue("");
		} else {
			CustomerType details = (CustomerType) dataObject;
			if (details != null) {
				this.custTypeCode.setValue(details.getCustTypeCode());
				this.lovDescCustTypeCodeName.setValue(details.getCustTypeCode()+ "-" + details.getCustTypeDesc());
				this.custCtgType = details.getCustTypeCtg();
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	private void setFieldValues(boolean isRetailCustomer){
		
		this.row_retailCustomerNames.setVisible(isRetailCustomer);
		this.row_retailCustomerPPT.setVisible(isRetailCustomer);
		this.row_corpCustomerTL.setVisible(!isRetailCustomer);
		this.label_CustomerDialog_CustLName.setVisible(isRetailCustomer);
		this.label_CustomerDialog_CustOrgName.setVisible(!isRetailCustomer);
		this.hbox_visaNum.setVisible(isRetailCustomer);
		this.label_CustomerDialog_CustVisaNum.setVisible(isRetailCustomer);
		this.label_CustomerDialog_CustDOB.setVisible(isRetailCustomer);
		this.label_CustomerDialog_CustDateOfIncorporation.setVisible(!isRetailCustomer);
		this.custFName.setValue("");
		this.custMName.setValue("");
		this.custLName.setValue("");
		this.custDOB.setText("");
		this.custPassportNo.setValue("");
		this.custVisaNum.setValue("");
		this.custTradeLicenceNum.setValue("");

		if(isRetailCustomer){
			if (StringUtils.trimToEmpty(this.custParentCountry.getValue()).equals(
					SysParamUtil.getValueAsString("CURR_SYSTEM_COUNTRY"))) {
				this.label_CustomerDialog_CustVisaNum.setVisible(!isRetailCustomer);
				this.hbox_visaNum.setVisible(!isRetailCustomer);
			}
		}
		
	}

	public void onClick$btnSearchCustParentCountry(Event event) {
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerQDEDialog, "Country");
		
		if(StringUtils.isEmpty(this.custCtgCode.getValue()) || 
				StringUtils.equals(getCustomerDetails().getCustomer().getCustCtgCode(),PennantConstants.PFF_CUSTCTG_INDIV)){
			this.label_CustomerDialog_CustVisaNum.setVisible(true);
			this.hbox_visaNum.setVisible(true);
		}
		
		if (dataObject instanceof String) {
			this.custParentCountry.setValue(dataObject.toString());
			this.lovDescCustParentCountryName.setValue("");
			
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custParentCountry.setValue(details.getCountryCode());
				this.lovDescCustParentCountryName.setValue(details.getCountryCode() 
						+ "-" + details.getCountryDesc());

				String arr[] = SysParamUtil.getValueAsString("NONEED_VISA_COUNTRIES").split(",");
				for(int i=0;i<arr.length;i++){
					if(arr[i].equals(this.custParentCountry.getValue())){ //If selected country is in list of visa not needed countries make visa invisble.
						this.label_CustomerDialog_CustVisaNum.setVisible(false);
						this.hbox_visaNum.setVisible(false);
					}
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to get audit header
	 * 
	 * @param aCustomerDetails
	 *            (CustomerDetails)
	 * @param tranType
	 *            (String)
	 * 
	 * @return AuditHeader
	 * */
	private AuditHeader getAuditHeader(CustomerDetails aCustomerDetails,
			String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()),null, null, null, 
				auditDetail,aCustomerDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Method to show message
	 * 
	 * @param exception
	 * */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("", e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerQDEDialog,auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to set the value in Customer CIF field to upper case, if manually
	 * entered by user.
	 * 
	 * @param event
	 */
	public void onBlur$custCIF(Event event) {
		logger.debug("Entering");
		this.custCIF.setValue(this.custCIF.getValue().toUpperCase());
		if ("CIF".equals(SysParamUtil.getValueAsString("CB_CID"))) {
			this.custCoreBank.setValue(this.custCIF.getValue());
			this.custFName.setFocus(true);
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}
	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}
	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}
	
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return validationOn;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}
}
