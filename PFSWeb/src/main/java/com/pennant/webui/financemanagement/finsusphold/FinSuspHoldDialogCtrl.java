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
 * FileName    		:  FinSuspHoldDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.finsusphold;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinSuspHold;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.FinSuspHoldService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/FinSuspHold
 * /finSuspHoldDialog.zul file.
 */
public class FinSuspHoldDialogCtrl extends GFCBaseCtrl<FinSuspHold> {
	private static final long serialVersionUID = -2489293301745014852L;
	private static final Logger logger = Logger.getLogger(FinSuspHoldDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_FinSuspHoldDialog;	// autoWired

	protected ExtendedCombobox 		product; 					// autoWired
	protected ExtendedCombobox 		finType; 					// autoWired
	protected ExtendedCombobox 		finReference; 				// autoWired
	protected Textbox               custCIF; 				    // autowired
	protected Longbox 	            custID;						// autowired
	protected Button 	            btnSearchCustCIF;			// autowired
	protected Label 	            custShrtName;			        // autowired
	protected Checkbox 		        active; 					// autoWired


	// not autoWired variables
	private FinSuspHold finSuspHold; // overHanded per parameter
	private transient FinSuspHoldListCtrl finSuspHoldListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	// ServiceDAOs / Domain Classes
	private transient FinSuspHoldService finSuspHoldService;
	
	private String product_Temp = ""; 
	private String finType_Temp = ""; 

	/**
	 * default constructor.<br>
	 */
	public FinSuspHoldDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinSuspHoldDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinSuspHold
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinSuspHoldDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinSuspHoldDialog);

		try {
			if (arguments.containsKey("finSuspHold")) {
				this.finSuspHold = (FinSuspHold) arguments.get("finSuspHold");
				FinSuspHold befImage = new FinSuspHold();
				BeanUtils.copyProperties(this.finSuspHold, befImage);
				this.finSuspHold.setBefImage(befImage);
				setFinSuspHold(this.finSuspHold);
			} else {
				setFinSuspHold(null);
			}

			doLoadWorkFlow(this.finSuspHold.isWorkflow(),
					this.finSuspHold.getWorkflowId(),
					this.finSuspHold.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),"FinSuspHoldDialog");
			}

			// READ OVERHANDED parameters !
			// we get the finSuspHoldListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete finSuspHold here.
			if (arguments.containsKey("finSuspHoldListCtrl")) {
				setFinSuspHoldListCtrl((FinSuspHoldListCtrl) arguments
						.get("finSuspHoldListCtrl"));
			} else {
				setFinSuspHoldListCtrl(null);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinSuspHold());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinSuspHoldDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}


	public void onFulfill$product(Event event) {
		logger.debug("Entering");

		Object dataObject = product.getObject();
		if (dataObject instanceof String) {
			this.product.setValue(dataObject.toString());
			this.product.setDescription("");
		} else {
			Product details = (Product) dataObject;
			if (details != null) {
				this.product.setValue(details.getProductCode());
				this.product.setDescription(details.getProductDesc());
			}
		}
		if(!StringUtils.equals(this.product.getValue(),this.product_Temp)){
			this.finType.setValue("");
			this.finType.setDescription("");
			this.finType.setFilters(null);
			this.finType.setObject(null);
			this.finReference.setValue("");
			this.finReference.setDescription("");
			this.finReference.setFilters(null);
			this.finReference.setObject(null);
			doSetFinTypeProperties(this.product.getValue());
		}
		 this.product_Temp = this.product.getValue();
		logger.debug("Leaving");
	}


	public void onFulfill$finType(Event event) {
		logger.debug("Entering");
		Object dataObject = finType.getObject();
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getFinTypeDesc());
				this.product.setValue(details.getFinCategory());
				Product productTemp =  getProductDetails(details.getFinCategory());
				if(productTemp != null){
					this.product.setValue(productTemp.getProductCode());
					this.product.setDescription(productTemp.getProductDesc());
					this.product.setObject(productTemp);
				}
			}
		}
		
		if(!StringUtils.equals(this.finType.getValue(), this.finType_Temp)){
			this.finReference.setValue("");
			this.finReference.setDescription("");
			this.finReference.setFilters(null);
			this.finReference.setObject(null);
			doSetFinanceProperties(this.finType.getValue());
		}
		this.product_Temp = this.product.getValue();
		this.finType_Temp = this.finType.getValue();
		logger.debug("Leaving");
	}
	
	public void onFulfill$finReference(Event event) {
		logger.debug("Entering");
		Object dataObject = finReference.getObject();
		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
				this.finType.setValue(details.getFinType());
				this.custID.setValue(details.getCustID());
				this.custCIF.setValue(details.getLovDescCustCIF());
				this.custShrtName.setValue(details.getLovDescCustShrtName());
				FinanceType financeTypeTemp =  getFinanceTypeDetails(details.getFinType());
				if(financeTypeTemp != null){
					this.finType.setDescription(financeTypeTemp.getFinTypeDesc());
					this.finType.setObject(financeTypeTemp);
				}
				this.product.setValue(financeTypeTemp.getFinCategory());
				Product productTemp =  getProductDetails(financeTypeTemp.getFinCategory());
				if(productTemp != null){
					this.product.setDescription(productTemp.getProductDesc());
					this.product.setObject(productTemp);
				}
			}
		}
		this.product_Temp = this.product.getValue();
		 this.finType_Temp = this.finType.getValue();
		logger.debug("Leaving");
	}

	
	private FinanceType getFinanceTypeDetails(String finTypeTemp){
		logger.debug("Entering");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<FinanceType> searchObject = new JdbcSearchObject<FinanceType>(FinanceType.class);
		searchObject.addSort("FinType", false);
		searchObject.addFilter(new Filter("FinType",finTypeTemp, Filter.OP_EQUAL));
		List<FinanceType> finTypeList = pagedListService.getBySearchObject(searchObject);
		if(finTypeList != null && !finTypeList.isEmpty()){
			return finTypeList.get(0);
		}
		logger.debug("Leaving");
		return null;
	}
	
	private Product getProductDetails(String productTemp){
		logger.debug("Entering");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Product> searchObject = new JdbcSearchObject<Product>(Product.class);
		searchObject.addSort("ProductCode", false);
		searchObject.addFilter(new Filter("ProductCode",productTemp, Filter.OP_EQUAL));
		List<Product> productList = pagedListService.getBySearchObject(searchObject);
		if(productList != null && !productList.isEmpty()){
			return productList.get(0);
		}
		logger.debug("Leaving");
		return null;
	}
	
	private void doSetFinTypeProperties(String productTemp){
		logger.debug("Entering");
		if(StringUtils.isNotEmpty(productTemp)){
			this.finType.setFilters(new Filter[]{new Filter("FinCategory", productTemp, Filter.OP_EQUAL)});
		}
		logger.debug("Leaving");
	}


	private void doSetFinanceProperties(String finType){
		logger.debug("Entering");
		if(StringUtils.isNotEmpty(finType)){
			this.finReference.setFilters(new Filter[]{new Filter("FinType", finType, Filter.OP_EQUAL)});
		}
		logger.debug("Leaving");
	}
	
	public void onChange$custCIF(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());

		this.custCIF.clearErrorMessage();
		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);
		if(customer == null) {	
			this.custCIF.setValue("");
			this.custID.setText("");
			this.custShrtName.setValue("");
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
		}
		logger.debug("Leaving" + event.toString());
	}



	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if(customer != null){
			this.custCIF.setValue(customer.getCustCIF());
			this.custID.setValue(customer.getCustID());
			this.custShrtName.setValue(customer.getCustShrtName());
		}else{
			this.custCIF.setValue("");
			this.custID.setText("");
			this.custShrtName.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null, map);
		logger.debug("Leaving");
	}




	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.product.setMaxlength(8);
		this.product.setTextBoxWidth(151);
		this.product.setModuleName("Product");
		this.product.setValueColumn("ProductCode");
		this.product.setDescColumn("ProductDesc");
		this.product.setValidateColumns(new String[] { "ProductCode" });

		this.finType.setMaxlength(8);
		this.finType.setTextBoxWidth(151);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(151);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		this.custCIF.setWidth("153px");
		this.btnSearchCustCIF.setWidth("30px");

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
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
		getUserWorkspace().allocateAuthorities("FinSuspHoldDialog",getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinSuspHoldDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinSuspHoldDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinSuspHoldDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinSuspHoldDialog_btnSave"));
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
		logger.debug("Entering" + event.toString());
		doSave();
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_FinSuspHoldDialog);
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
		doWriteBeanToComponents(this.finSuspHold.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinSuspHold
	 *            FinSuspHold
	 */
	public void doWriteBeanToComponents(FinSuspHold aFinSuspHold) {
		logger.debug("Entering");

		this.product.setValue(aFinSuspHold.getProduct());
		this.product.setDescription(StringUtils.trimToEmpty(aFinSuspHold.getProductDesc()));
		this.finType.setValue(aFinSuspHold.getFinType());
		this.finType.setDescription(StringUtils.trimToEmpty(aFinSuspHold.getFinTypeDesc()));
		this.finReference.setValue(aFinSuspHold.getFinReference());
		this.custID.setValue(aFinSuspHold.getCustID());
		this.custCIF.setValue(aFinSuspHold.getCustCIF());
		this.custShrtName.setValue(aFinSuspHold.getCustShrtName());
		this.active.setChecked(aFinSuspHold.isActive());
		this.recordStatus.setValue(aFinSuspHold.getRecordStatus());

		this.product_Temp = this.product.getValue();
		this.finType_Temp = this.finType.getValue();
		
		doSetFinTypeProperties(this.product.getValue());
		doSetFinanceProperties(this.finType.getValue());
		if(aFinSuspHold.isNew() || StringUtils.equals(aFinSuspHold.getRecordType(),PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinSuspHold
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(FinSuspHold aFinSuspHold){
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinSuspHold.setProduct(this.product.getValidatedValue());
			aFinSuspHold.setProductDesc(this.product.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinSuspHold.setFinType(this.finType.getValidatedValue());
			aFinSuspHold.setFinTypeDesc(this.finType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinSuspHold.setFinReference(this.finReference.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinSuspHold.setCustID(this.custID.getValue()==null?0:this.custID.getValue());
			aFinSuspHold.setCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinSuspHold.setActive(this.active.isChecked());
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

		aFinSuspHold.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinSuspHold
	 * @throws Exception
	 */
	public void doShowDialog(FinSuspHold aFinSuspHold) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aFinSuspHold.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.product.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
			} else {
				this.btnCtrl.setInitNew();
				this.btnDelete.setVisible(true);
			}
			doEdit();
		}
		this.btnCancel.setVisible(false);
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinSuspHold);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinSuspHoldDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		doClearMessage();

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.product.setConstraint("");
		this.finType.setConstraint("");
		this.finReference.setConstraint("");
		this.custCIF.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.product.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FinSuspHold object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final FinSuspHold aFinSuspHold = new FinSuspHold();
		BeanUtils.copyProperties(getFinSuspHold(), aFinSuspHold);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + getValidationMsg();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinSuspHold.getRecordType())) {
				aFinSuspHold.setVersion(aFinSuspHold.getVersion() + 1);
				aFinSuspHold.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinSuspHold.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFinSuspHold, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	
	private String getValidationMsg(){
		logger.debug("Entering");
		String errMsg = "";
		if(StringUtils.isNotEmpty(this.product.getValue())){
			errMsg = Labels.getLabel("label_FinSuspHold_Product")+" : "+ this.product.getValue();
		}
		if(StringUtils.isNotEmpty(this.finType.getValue())){
			if(StringUtils.isEmpty(errMsg)){
				errMsg = Labels.getLabel("label_FinSuspHold_FinType")+" : "+ this.finType.getValue();
			}else{
				errMsg = errMsg +","+Labels.getLabel("label_FinSuspHold_FinType")+" : "+ this.finType.getValue();
			}
		}
		if(StringUtils.isNotEmpty(this.finReference.getValue())){
			if(StringUtils.isEmpty(errMsg)){
				errMsg = Labels.getLabel("label_FinSuspHold_FinReference")+" : "+ this.finReference.getValue();
			}else{
				errMsg = errMsg +","+Labels.getLabel("label_FinSuspHold_FinReference")+" : "+ this.finReference.getValue();
			}
		}
		if(StringUtils.isNotEmpty(this.custCIF.getValue())){
			if(StringUtils.isEmpty(errMsg)){
				errMsg = Labels.getLabel("label_FinSuspHold_CustCIF")+" : "+ this.custCIF.getValue();
			}else{
				errMsg = errMsg +","+Labels.getLabel("label_FinSuspHold_CustCIF")+" : "+ this.custCIF.getValue();
			}
		}
		logger.debug("Leaving");
		return errMsg;
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinSuspHold().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		this.product.setReadonly(isReadOnly("FinSuspHoldDialog_product"));
		this.finType.setReadonly(isReadOnly("FinSuspHoldDialog_finType"));
		this.finReference.setReadonly(isReadOnly("FinSuspHoldDialog_finReference"));
		this.custCIF.setReadonly(isReadOnly("FinSuspHoldDialog_custCIF"));
		this.btnSearchCustCIF.setDisabled(isReadOnly("FinSuspHoldDialog_custCIF"));
		this.active.setDisabled(isReadOnly("FinSuspHoldDialog_active"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finSuspHold.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.product.setReadonly(true);
		this.finType.setReadonly(true);
		this.finReference.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.btnSearchCustCIF.setDisabled(true);

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
		this.product.setValue("");
		this.finType.setValue("");
		this.finReference.setValue("");
		this.custCIF.setValue("");
		this.custShrtName.setValue("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinSuspHold aFinSuspHold = new FinSuspHold();
		BeanUtils.copyProperties(getFinSuspHold(), aFinSuspHold);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinSuspHold object with the components data
		doWriteComponentsToBean(aFinSuspHold);

		if(!PennantConstants.RECORD_TYPE_DEL.equals(aFinSuspHold.getRecordType()) 
				&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
				&& !"Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
				&& !"Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())){
			if(!validateData()){
				return;
			}
		}
		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aFinSuspHold.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinSuspHold.getRecordType())) {
				aFinSuspHold.setVersion(aFinSuspHold.getVersion() + 1);
				if (isNew) {
					aFinSuspHold.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinSuspHold.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinSuspHold.setNewRecord(true);
				}
			}
		} else {
			aFinSuspHold.setVersion(aFinSuspHold.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFinSuspHold, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
				
				String msg = PennantApplicationUtil.getSavingStatus(aFinSuspHold.getRoleCode(),aFinSuspHold.getNextRoleCode(), 
						aFinSuspHold.getFinReference(), " Finance ", aFinSuspHold.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}


	private boolean validateData() throws InterruptedException{
		logger.debug("Entering");
		boolean validData = true;
		if(StringUtils.isEmpty(this.product.getValidatedValue()) && 
				StringUtils.isEmpty(this.finType.getValidatedValue()) && 
				StringUtils.isEmpty(this.finReference.getValidatedValue()) && 
				StringUtils.isEmpty(this.custCIF.getValue())){

			String msg = Labels.getLabel("FinSuspHold_Mandatory",new String[]{
					Labels.getLabel("label_FinSuspHoldDialog_Product.value"),
					Labels.getLabel("label_FinSuspHoldDialog_FinType.value"),
					Labels.getLabel("label_FinSuspHoldDialog_CustCIF.value"),
					Labels.getLabel("label_FinSuspHoldDialog_FinReference.value")});
			MessageUtil.showError(msg);
			validData = false;
		}
		logger.debug("Leaving");
		return validData;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinSuspHold
	 *            (FinSuspHold)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(FinSuspHold aFinSuspHold, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aFinSuspHold.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aFinSuspHold.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinSuspHold.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aFinSuspHold.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aFinSuspHold, finishedTasks);

			if (isNotesMandatory(taskId, aFinSuspHold)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinSuspHold, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_DDAMaintenance)){
					processCompleted = true;
				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else {
					FinSuspHold tFinSuspHold=  (FinSuspHold) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, aFinSuspHold);
					auditHeader.getAuditDetail().setModelData(tFinSuspHold);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinSuspHold tFinSuspHold=  (FinSuspHold) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinSuspHold,finishedTasks);

			}

			FinSuspHold tFinSuspHold=  (FinSuspHold) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinSuspHold);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}
			
			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId)|| "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aFinSuspHold);
					auditHeader.getAuditDetail().setModelData(tFinSuspHold);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			auditHeader = getAuditHeader(aFinSuspHold, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	protected String getServiceTasks(String taskId, FinSuspHold finSuspHold,
			String finishedTasks) {
		logger.debug("Entering");
      // changes regarding parallel work flow 
		String nextRoleCode = StringUtils.trimToEmpty(finSuspHold.getNextRoleCode());
		String nextRoleCodes[]=nextRoleCode.split(",");
		
		if (nextRoleCodes.length > 1 ) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, finSuspHold);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}
	
	protected void setNextTaskDetails(String taskId, FinSuspHold finSuspHold) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(finSuspHold.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			}else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, finSuspHold);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole= StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		finSuspHold.setTaskId(taskId);
		finSuspHold.setNextTaskId(nextTaskId);
		finSuspHold.setRoleCode(getRole());
		finSuspHold.setNextRoleCode(nextRoleCode);
		
		logger.debug("Leaving");
	}
	

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinSuspHold aFinSuspHold = (FinSuspHold) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinSuspHoldService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFinSuspHoldService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinSuspHoldService().doApprove(auditHeader);

						if (aFinSuspHold.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinSuspHoldService().doReject(auditHeader);

						if (aFinSuspHold.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinSuspHoldDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinSuspHoldDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.finSuspHold), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinSuspHold
	 *            (FinSuspHold)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(FinSuspHold aFinSuspHold, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinSuspHold.getBefImage(), aFinSuspHold);
		return new AuditHeader(String.valueOf(aFinSuspHold.getId()),
				null, null, null, auditDetail, aFinSuspHold.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinSuspHoldDialog, auditHeader);
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
		doShowNotes(this.finSuspHold);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getFinSuspHoldListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.finSuspHold.getSuspHoldID());
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

	public FinSuspHold getFinSuspHold() {
		return this.finSuspHold;
	}
	public void setFinSuspHold(FinSuspHold finSuspHold) {
		this.finSuspHold = finSuspHold;
	}

	public void setFinSuspHoldService(FinSuspHoldService finSuspHoldService) {
		this.finSuspHoldService = finSuspHoldService;
	}
	public FinSuspHoldService getFinSuspHoldService() {
		return this.finSuspHoldService;
	}

	public void setFinSuspHoldListCtrl(FinSuspHoldListCtrl finSuspHoldListCtrl) {
		this.finSuspHoldListCtrl = finSuspHoldListCtrl;
	}
	public FinSuspHoldListCtrl getFinSuspHoldListCtrl() {
		return this.finSuspHoldListCtrl;
	}

}
