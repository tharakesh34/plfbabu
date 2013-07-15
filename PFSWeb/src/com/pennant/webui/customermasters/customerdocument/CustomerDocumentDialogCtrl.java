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
 * FileName    		:  CustomerDocumentDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerdocument;

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
import org.zkoss.spring.SpringUtil;
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
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.LongValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerDocument/customerDocumentDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerDocumentDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8931742880858169171L;
	private final static Logger logger = Logger.getLogger(CustomerDocumentDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerDocumentDialog; // autoWired

	protected Longbox 	custID; 					// autoWired
	protected Textbox 	custDocType; 				// autoWired
	protected Textbox 	custDocTitle; 				// autoWired
	protected Textbox 	custDocSysName; 			// autoWired
	protected Datebox 	custDocRcvdOn; 				// autoWired
	protected Datebox 	custDocExpDate; 			// autoWired
	protected Datebox 	custDocIssuedOn; 			// autoWired
	protected Textbox 	custDocIssuedCountry; 		// autoWired
	protected Checkbox 	custDocIsVerified; 			// autoWired
	protected Longbox 	custDocVerifiedBy; 			// autoWired
	protected Checkbox 	custDocIsAcrive; 			// autoWired
	protected Textbox 	custCIF;					// autoWired
	protected Label 	custShrtName;				// autoWired
	protected Textbox 	lovDescCustDocVerifiedBy; 			// autoWired

	protected Label 		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired variables
	private CustomerDocument customerDocument; // overHanded per parameter
	private transient CustomerDocumentListCtrl customerDocumentListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long 		oldVar_custID;
	private transient String 	oldVar_custDocType;
	private transient String 	oldVar_custDocTitle;
	private transient String 	oldVar_custDocSysName;
	private transient Date 		oldVar_custDocRcvdOn;
	private transient Date 		oldVar_custDocExpDate;
	private transient Date 		oldVar_custDocIssuedOn;
	private transient String 	oldVar_custDocIssuedCountry;
	private transient boolean 	oldVar_custDocIsVerified;
	private transient long 		oldVar_custDocVerifiedBy;
	private transient boolean 	oldVar_custDocIsAcrive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerDocumentDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 			// autoWire
	protected Button btnEdit; 			// autoWire
	protected Button btnDelete; 		// autoWire
	protected Button btnSave; 			// autoWire
	protected Button btnCancel; 		// autoWire
	protected Button btnClose; 			// autoWire
	protected Button btnHelp; 			// autoWire
	protected Button btnNotes; 			// autoWire
	protected Button btnSearchPRCustid; // autoWire
	
	protected Button 	btnSearchCustDocType; 				// autoWire
	protected Textbox 	lovDescCustDocTypeName;
	private transient String oldVar_lovDescCustDocTypeName;
	
	protected Button 	btnSearchCustDocIssuedCountry; 		// autoWire
	protected Textbox 	lovDescCustDocIssuedCountryName;
	private transient String oldVar_lovDescCustDocIssuedCountryName;

	// ServiceDAOs / Domain Classes
	private transient CustomerDocumentService customerDocumentService;
	private transient PagedListService pagedListService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;
	
	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerDocument> customerDocuments;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	protected JdbcSearchObject<SecurityUser> secUserSearchObj;
	private PagedListWrapper<SecurityUser> secUserPagedListWrapper;
	private String moduleType="";

	/**
	 * default constructor.<br>
	 */
	public CustomerDocumentDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerDocument
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerDocumentDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		setSecUserPagedListWrapper();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("customerDocument")) {
			this.customerDocument = (CustomerDocument) args.get("customerDocument");
			CustomerDocument befImage = new CustomerDocument();
			BeanUtils.copyProperties(this.customerDocument, befImage);
			this.customerDocument.setBefImage(befImage);
			setCustomerDocument(this.customerDocument);
		} else {
			setCustomerDocument(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}

		if(getCustomerDocument().isNewRecord()){
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

			this.customerDocument.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"),
						"CustomerDocumentDialog");
			}
		}
		
		doLoadWorkFlow(this.customerDocument.isWorkflow(),
				this.customerDocument.getWorkflowId(),
				this.customerDocument.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerDocumentDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerDocumentListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerDocument here.
		if (args.containsKey("customerDocumentListCtrl")) {
			setCustomerDocumentListCtrl((CustomerDocumentListCtrl) args.get("customerDocumentListCtrl"));
		} else {
			setCustomerDocumentListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerDocument());
		
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
		
		// Empty sent any required attributes
		this.custID.setMaxlength(19);
		this.custDocType.setMaxlength(8);
		this.custDocTitle.setMaxlength(100);
		this.custDocSysName.setMaxlength(100);
		this.custDocRcvdOn.setFormat(PennantConstants.dateTimeFormat);
		this.custDocExpDate.setFormat(PennantConstants.dateFormat);
		this.custDocIssuedOn.setFormat(PennantConstants.dateFormat);
		this.custDocIssuedCountry.setMaxlength(2);

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
		
		getUserWorkspace().alocateAuthorities("CustomerDocumentDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnSave"));
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
	public void onClose$window_CustomerDocumentDialog(Event event)
			throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerDocumentDialog);
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
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

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
			window_CustomerDocumentDialog.onClose();	
		}else{
			closeDialog(this.window_CustomerDocumentDialog, "CustomerDocument");
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
	 * @param aCustomerDocument
	 *            CustomerDocument
	 */
	public void doWriteBeanToComponents(CustomerDocument aCustomerDocument) {
		logger.debug("Entering");
		
		if(aCustomerDocument.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerDocument.getCustID());	
		}
		this.custDocType.setValue(aCustomerDocument.getCustDocType());
		this.custDocTitle.setValue(aCustomerDocument.getCustDocTitle());
		this.custDocSysName.setValue(aCustomerDocument.getCustDocSysName());
		this.custDocRcvdOn.setValue(aCustomerDocument.getCustDocRcvdOn());
		this.custDocExpDate.setValue(aCustomerDocument.getCustDocExpDate());
		this.custDocIssuedOn.setValue(aCustomerDocument.getCustDocIssuedOn());
		this.custDocIssuedCountry.setValue(aCustomerDocument.getCustDocIssuedCountry());
		this.custDocIsVerified.setChecked(aCustomerDocument.isCustDocIsVerified());
		this.custDocIsAcrive.setChecked(aCustomerDocument.isCustDocIsAcrive());
		this.custCIF.setValue(aCustomerDocument.getLovDescCustCIF()==null?"":aCustomerDocument.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerDocument.getLovDescCustShrtName()==null?"":aCustomerDocument.getLovDescCustShrtName().trim());

		if (isNewRecord()){
			this.lovDescCustDocVerifiedBy.setValue(getUserWorkspace().getUserDetails().getUsername());
			this.lovDescCustDocTypeName.setValue("");
			this.lovDescCustDocIssuedCountryName.setValue("");
		}else{
			this.lovDescCustDocVerifiedBy.setValue(aCustomerDocument.getLovDescCustDocVerifiedBy());
			this.lovDescCustDocTypeName.setValue(aCustomerDocument.getLovDescCustDocType());
			this.lovDescCustDocIssuedCountryName.setValue(aCustomerDocument.getLovDescCustDocIssuedCountry());
			this.btnSearchCustDocType.setVisible(false);
		}
		
		if(aCustomerDocument.isNew() || aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.custDocIsAcrive.setChecked(true);
			this.custDocIsAcrive.setDisabled(true);
		}
		
		this.recordStatus.setValue(aCustomerDocument.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerDocument
	 */
	public void doWriteComponentsToBean(CustomerDocument aCustomerDocument) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerDocument.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerDocument.setCustID(this.custID.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setLovDescCustDocType(this.lovDescCustDocTypeName.getValue());
			aCustomerDocument.setCustDocType(this.custDocType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setCustDocTitle(this.custDocTitle.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setCustDocSysName(this.custDocSysName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custDocRcvdOn.getValue() != null) {
				if(!(this.custDocRcvdOn.getValue().before(this.custDocExpDate.getValue())
							&& this.custDocRcvdOn.getValue().after(this.custDocIssuedOn.getValue()))){
					throw new WrongValueException(custDocRcvdOn,  Labels.getLabel(
							"DATE_RANGE",new String[]{Labels.getLabel(
							"label_CustomerDocumentDialog_CustDocRcvdOn.value"),
							DateUtility.formatUtilDate(this.custDocExpDate.getValue(), PennantConstants.dateFormat),
							DateUtility.formatUtilDate(this.custDocIssuedOn.getValue(), PennantConstants.dateFormat)}));
				}
			
				aCustomerDocument.setCustDocRcvdOn(new Timestamp(this.custDocRcvdOn.getValue().getTime()));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custDocExpDate.getValue() != null) {
				if(!(this.custDocExpDate.getValue().after(this.custDocRcvdOn.getValue())
							&& this.custDocExpDate.getValue().after(this.custDocIssuedOn.getValue()))){
					throw new WrongValueException(custDocExpDate,  Labels.getLabel(
							"DATE_ALLOWED_AFTER",new String[]{Labels.getLabel(
							"label_CustomerDocumentDialog_CustDocExpDate.value"),
							DateUtility.formatUtilDate(this.custDocRcvdOn.getValue(), PennantConstants.dateFormat),
							DateUtility.formatUtilDate(this.custDocIssuedOn.getValue(), PennantConstants.dateFormat)}));
				}
				aCustomerDocument.setCustDocExpDate(this.custDocExpDate.getValue());
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			
			if (this.custDocIssuedOn.getValue() != null) {
				if (!(this.custDocIssuedOn.getValue().after((Date) SystemParameterDetails
								.getSystemParameterValue("APP_DFT_START_DATE")))) {
					throw new WrongValueException(this.custDocIssuedOn,Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[] {Labels.getLabel("label_CustomerDocumentDialog_CustDocIssuedOn.value"),SystemParameterDetails
							.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
				aCustomerDocument.setCustDocIssuedOn(this.custDocIssuedOn.getValue());
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setLovDescCustDocIssuedCountry(this.lovDescCustDocIssuedCountryName.getValue());
			aCustomerDocument.setCustDocIssuedCountry(this.custDocIssuedCountry.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setCustDocIsVerified(this.custDocIsVerified.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.custDocVerifiedBy.setValue(getUserWorkspace().getUserDetails().getUserId());
			aCustomerDocument.setCustDocVerifiedBy(this.custDocVerifiedBy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setCustDocIsAcrive(this.custDocIsAcrive.isChecked());
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

		aCustomerDocument.setRecordStatus(this.recordStatus.getValue());
		setCustomerDocument(aCustomerDocument);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerDocument
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerDocument aCustomerDocument)
			throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.btnSearchCustDocType.focus();
		} else {
			this.custDocTitle.focus();
			if (isNewCustomer()){
				doEdit();
			}else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerDocument);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			
			if(isNewCustomer()){
				this.window_CustomerDocumentDialog.setHeight("400px");
				this.window_CustomerDocumentDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_CustomerDocumentDialog.doModal() ;
			}else{
				this.window_CustomerDocumentDialog.setWidth("100%");
				this.window_CustomerDocumentDialog.setHeight("100%");
				setDialog(this.window_CustomerDocumentDialog);
			}
			
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
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
		this.oldVar_custDocType = this.custDocType.getValue();
		this.oldVar_lovDescCustDocTypeName = this.lovDescCustDocTypeName.getValue();
		this.oldVar_custDocTitle = this.custDocTitle.getValue();
		this.oldVar_custDocSysName = this.custDocSysName.getValue();
		this.oldVar_custDocRcvdOn = this.custDocRcvdOn.getValue();
		this.oldVar_custDocExpDate = this.custDocExpDate.getValue();
		this.oldVar_custDocIssuedOn = this.custDocIssuedOn.getValue();
		this.oldVar_custDocIssuedCountry = this.custDocIssuedCountry.getValue();
		this.oldVar_lovDescCustDocIssuedCountryName = this.lovDescCustDocIssuedCountryName.getValue();
		this.oldVar_custDocIsVerified = this.custDocIsVerified.isChecked();
		this.oldVar_custDocVerifiedBy = this.custDocVerifiedBy.longValue();
		this.oldVar_custDocIsAcrive = this.custDocIsAcrive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.custID.setValue(this.oldVar_custID);
		this.custDocType.setValue(this.oldVar_custDocType);
		this.lovDescCustDocTypeName.setValue(this.oldVar_lovDescCustDocTypeName);
		this.custDocTitle.setValue(this.oldVar_custDocTitle);
		this.custDocSysName.setValue(this.oldVar_custDocSysName);
		this.custDocRcvdOn.setValue(this.oldVar_custDocRcvdOn);
		this.custDocExpDate.setValue(this.oldVar_custDocExpDate);
		this.custDocIssuedOn.setValue(this.oldVar_custDocIssuedOn);
		this.custDocIssuedCountry.setValue(this.oldVar_custDocIssuedCountry);
		this.lovDescCustDocIssuedCountryName.setValue(this.oldVar_lovDescCustDocIssuedCountryName);
		this.custDocIsVerified.setChecked(this.oldVar_custDocIsVerified);
		this.custDocVerifiedBy.setValue(this.oldVar_custDocVerifiedBy);
		this.custDocIsAcrive.setChecked(this.oldVar_custDocIsAcrive);
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
		doClearErrorMessage();

		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_custDocType != this.custDocType.getValue()) {
			return true;
		}
		if (this.oldVar_custDocTitle != this.custDocTitle.getValue()) {
			return true;
		}
		if (this.oldVar_custDocSysName != this.custDocSysName.getValue()) {
			return true;
		}

		String old_custDocRcvdOn = "";
		String new_custDocRcvdOn = "";
		if (this.oldVar_custDocRcvdOn != null) {
			old_custDocRcvdOn = DateUtility.formatDate(
					this.oldVar_custDocRcvdOn, PennantConstants.dateFormat);
		}
		if (this.custDocRcvdOn.getValue() != null) {
			new_custDocRcvdOn = DateUtility.formatDate(this.custDocRcvdOn.getValue(),
							PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_custDocRcvdOn).equals(
				StringUtils.trimToEmpty(new_custDocRcvdOn))) {
			return true;
		}
		
		String old_custDocExpDate = "";
		String new_custDocExpDate = "";
		if (this.oldVar_custDocExpDate != null) {
			old_custDocExpDate = DateUtility.formatDate(
					this.oldVar_custDocExpDate, PennantConstants.dateFormat);
		}
		if (this.custDocExpDate.getValue() != null) {
			new_custDocExpDate = DateUtility.formatDate(this.custDocExpDate.getValue(),
							PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_custDocExpDate).equals(
				StringUtils.trimToEmpty(new_custDocExpDate))) {
			return true;
		}
		
		String old_custDocIssuedOn = "";
		String new_custDocIssuedOn = "";
		if (this.oldVar_custDocIssuedOn != null) {
			old_custDocIssuedOn = DateUtility.formatDate(
					this.oldVar_custDocIssuedOn, PennantConstants.dateFormat);
		}
		if (this.custDocIssuedOn.getValue() != null) {
			new_custDocIssuedOn = DateUtility.formatDate(this.custDocIssuedOn.getValue(),
					PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_custDocIssuedOn).equals(
				StringUtils.trimToEmpty(new_custDocIssuedOn))) {
			return true;
		}
		
		if (this.oldVar_custDocIssuedCountry != this.custDocIssuedCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custDocIsVerified != this.custDocIsVerified.isChecked()) {
			return true;
		}
		if (this.oldVar_custDocVerifiedBy != this.custDocVerifiedBy.longValue()) {
			return true;
		}
		if (this.oldVar_custDocIsAcrive != this.custDocIsAcrive.isChecked()) {
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
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_CustomerDocumentDialog_CustDocCIF.value")}));
		}
		
		if (!this.custDocTitle.isReadonly()){
			this.custDocTitle.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_SPACE_REGEX, 
					Labels.getLabel("FIELD_ALNUM_CAPS",new String[]{Labels.getLabel(
						"label_CustomerDocumentDialog_CustDocTitle.value")})));
		}
		
		if (!this.custDocSysName.isReadonly()){
			this.custDocSysName.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_SPACE_REGEX, 
					Labels.getLabel("FIELD_ALNUM_CAPS",new String[]{Labels.getLabel(
						"label_CustomerDocumentDialog_CustDocSysName.value")})));
		}
		
		if (!this.custDocRcvdOn.isReadonly() && !this.custDocRcvdOn.isDisabled()) {
			this.custDocRcvdOn.setConstraint("NO EMPTY,NO FUTURE:"+ Labels.getLabel(
				"DATE_EMPTY_FUTURE",new String[] { Labels.getLabel(
						"label_CustomerDocumentDialog_CustDocRcvdOn.value") }));
		}

		if (!this.custDocExpDate.isReadonly() && !this.custDocExpDate.isDisabled()) {
			this.custDocExpDate.setConstraint("NO EMPTY,NO PAST:"+ Labels.getLabel(
				"DATE_EMPTY_PAST",new String[] { Labels.getLabel(
						"label_CustomerDocumentDialog_CustDocExpDate.value") }));
		}

		if (!this.custDocIssuedOn.isReadonly() && !this.custDocIssuedOn.isDisabled()) {
			this.custDocIssuedOn.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel(
				"DATE_EMPTY_FUTURE_TODAY",new String[] { Labels.getLabel(
						"label_CustomerDocumentDialog_CustDocIssuedOn.value") }));
		}
		
		if (!this.custDocVerifiedBy.isReadonly()) {
			this.custDocVerifiedBy.setConstraint(new LongValidator(19,Labels.getLabel(
						"label_CustomerDocumentDialog_CustDocVerifiedBy.value")));
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
		this.custDocTitle.setConstraint("");
		this.custDocSysName.setConstraint("");
		this.custDocRcvdOn.setConstraint("");
		this.custDocExpDate.setConstraint("");
		this.custDocIssuedOn.setConstraint("");
		this.custDocVerifiedBy.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustDocTypeName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDocumentDialog_CustDocType.value") }));
		this.lovDescCustDocIssuedCountryName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDocumentDialog_CustDocIssuedCountry.value") }));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustDocTypeName.setConstraint("");
		this.lovDescCustDocIssuedCountryName.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearErrorMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custDocTitle.setErrorMessage("");
		this.custDocSysName.setErrorMessage("");
		this.custDocRcvdOn.setErrorMessage("");
		this.custDocExpDate.setErrorMessage("");
		this.custDocIssuedOn.setErrorMessage("");
		this.custDocVerifiedBy.setErrorMessage("");
		this.lovDescCustDocTypeName.setErrorMessage("");
		this.lovDescCustDocIssuedCountryName.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	// Method for refreshing the list after successful update
	private void refreshList() {
		logger.debug("Entering");
		getCustomerDocumentListCtrl().findSearchObject();
		if (getCustomerDocumentListCtrl().listBoxCustomerDocument != null) {
			getCustomerDocumentListCtrl().listBoxCustomerDocument.getListModel();
		}
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerDocument object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerDocument aCustomerDocument = new CustomerDocument();
		BeanUtils.copyProperties(getCustomerDocument(), aCustomerDocument);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aCustomerDocument.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerDocument.getRecordType()).equals("")) {
				aCustomerDocument.setVersion(aCustomerDocument.getVersion() + 1);
				aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aCustomerDocument.setNewRecord(true);

				if (isWorkFlowEnabled()) {
					aCustomerDocument.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCustomerProcess(aCustomerDocument,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerDocumentDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerDocuments(this.customerDocuments);
						// send the data back to customer
						closeWindow();
					}	
				}else if (doProcess(aCustomerDocument, tranType)) {
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
	 * Create a new CustomerDocument object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerDocument() in the frontEnd.
		// we get it from the backEnd.
		final CustomerDocument aCustomerDocument = getCustomerDocumentService().getNewCustomerDocument();
		aCustomerDocument.setNewRecord(true);
		setCustomerDocument(aCustomerDocument);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.btnSearchCustDocType.focus();
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
			this.btnSearchCustDocType.setDisabled(isReadOnly("CustomerDocumentDialog_custDocType"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.btnSearchCustDocType.setVisible(false);
		}
		
		this.custCIF.setReadonly(true);
		
		this.btnSearchCustDocType.setDisabled(isReadOnly("CustomerDocumentDialog_custDocType"));
		this.custDocTitle.setReadonly(isReadOnly("CustomerDocumentDialog_custDocTitle"));
		this.custDocSysName.setReadonly(isReadOnly("CustomerDocumentDialog_custDocSysName"));
		this.custDocRcvdOn.setDisabled(isReadOnly("CustomerDocumentDialog_custDocRcvdOn"));
		this.custDocExpDate.setDisabled(isReadOnly("CustomerDocumentDialog_custDocExpDate"));
		this.custDocIssuedOn.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIssuedOn"));
		this.btnSearchCustDocIssuedCountry.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIssuedCountry"));
		this.custDocIsVerified.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIsVerified"));
		this.custDocVerifiedBy.setReadonly(isReadOnly("CustomerDocumentDialog_custDocVerifiedBy"));
		this.custDocIsAcrive.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIsAcrive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerDocument.isNewRecord()) {
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
		this.custCIF.setReadonly(true);
		this.btnSearchCustDocType.setDisabled(true);
		this.custDocTitle.setReadonly(true);
		this.custDocSysName.setReadonly(true);
		this.custDocRcvdOn.setDisabled(true);
		this.custDocExpDate.setDisabled(true);
		this.custDocIssuedOn.setDisabled(true);
		this.btnSearchCustDocIssuedCountry.setDisabled(true);
		this.custDocIsVerified.setDisabled(true);
		this.custDocVerifiedBy.setReadonly(true);
		this.custDocIsAcrive.setDisabled(true);

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
		this.custCIF.setValue("");
		this.custDocType.setValue("");
		this.lovDescCustDocTypeName.setValue("");
		this.custDocTitle.setValue("");
		this.custDocSysName.setValue("");
		this.custDocRcvdOn.setText("");
		this.custDocExpDate.setText("");
		this.custDocIssuedOn.setText("");
		this.custDocIssuedCountry.setValue("");
		this.lovDescCustDocIssuedCountryName.setValue("");
		this.custDocIsVerified.setChecked(false);
		this.custDocVerifiedBy.setText("");
		this.custDocIsAcrive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerDocument aCustomerDocument = new CustomerDocument();
		BeanUtils.copyProperties(getCustomerDocument(), aCustomerDocument);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerDocument object with the components data
		doWriteComponentsToBean(aCustomerDocument);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerDocument.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerDocument.getRecordType()).equals("")) {
				aCustomerDocument.setVersion(aCustomerDocument.getVersion() + 1);
				if (isNew) {
					aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerDocument.setNewRecord(true);
				}
			}
		} 
		else{
			
			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerDocument.setVersion(1);
					aCustomerDocument.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerDocument.getRecordType()).equals("")){
					aCustomerDocument.setVersion(aCustomerDocument.getVersion()+1);
					aCustomerDocument.setRecordType(PennantConstants.RCD_UPD);
				}
				
				if(aCustomerDocument.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
				
			}else {
				aCustomerDocument.setVersion(aCustomerDocument.getVersion() + 1);
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
					AuditHeader auditHeader =  newCustomerProcess(aCustomerDocument,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerDocumentDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerDocuments(this.customerDocuments);
						// send the data back to customer
						closeWindow();
					}
				}else if (doProcess(aCustomerDocument, tranType)) {
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

	private AuditHeader newCustomerProcess(CustomerDocument aCustomerDocument,String tranType){
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerDocument, tranType);
		customerDocuments = new ArrayList<CustomerDocument>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerDocument.getId());
		valueParm[1] = aCustomerDocument.getCustDocType();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustDocType") + ":"+valueParm[1];
		
		if(getCustomerDialogCtrl().getDocumentsList()!=null && getCustomerDialogCtrl().getDocumentsList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getDocumentsList().size(); i++) {
				CustomerDocument customerDocument = getCustomerDialogCtrl().getDocumentsList().get(i);
				
				if(customerDocument.getCustDocType().equals(aCustomerDocument.getCustDocType())){ // Both Current and Existing list documents same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerDocuments.add(aCustomerDocument);
						}else if(aCustomerDocument.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerDocuments.add(aCustomerDocument);
						}else if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getRatingsList().size(); j++) {
								CustomerDocument document =  getCustomerDialogCtrl().getCustomerDetails().getCustomerDocumentsList().get(j);
								if(document.getCustID() == aCustomerDocument.getCustID() && document.getCustDocType().equals(aCustomerDocument.getCustDocType())){
									customerDocuments.add(document);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerDocuments.add(customerDocument);
						}
					}
				}else{
					customerDocuments.add(customerDocument);
				}
			}
		}
		if(!recordAdded){
			customerDocuments.add(aCustomerDocument);
		}
		return auditHeader;
	} 
	
	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerDocument
	 *            (CustomerDocument)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerDocument aCustomerDocument, String tranType) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerDocument.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerDocument.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerDocument.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerDocument.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerDocument);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aCustomerDocument))) {
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

			aCustomerDocument.setTaskId(taskId);
			aCustomerDocument.setNextTaskId(nextTaskId);
			aCustomerDocument.setRoleCode(getRole());
			aCustomerDocument.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerDocument, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerDocument);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerDocument,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerDocument, tranType);
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
		CustomerDocument aCustomerDocument = (CustomerDocument) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerDocumentService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerDocumentService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCustomerDocumentService().doApprove(auditHeader);
						
						if (aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerDocumentService().doReject(auditHeader);
						
						if (aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
										this.window_CustomerDocumentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_CustomerDocumentDialog, auditHeader);
				retValue = ErrorControl.showErrorControl(
						this.window_CustomerDocumentDialog, auditHeader);

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
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onClick$btnSearchCustDocType(Event event) {

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDocumentDialog, "DocumentType");
		if (dataObject instanceof String) {
			this.custDocType.setValue(dataObject.toString());
			this.lovDescCustDocTypeName.setValue("");
		} else {
			DocumentType details = (DocumentType) dataObject;
			if (details != null) {
				this.custDocType.setValue(details.getLovValue());
				this.lovDescCustDocTypeName.setValue(details.getLovValue()
						+ "-" + details.getDocTypeDesc());
			}
		}
	}

	public void onClick$btnSearchCustDocIssuedCountry(Event event) {

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDocumentDialog, "Country");
		if (dataObject instanceof String) {
			this.custDocIssuedCountry.setValue(dataObject.toString());
			this.lovDescCustDocIssuedCountryName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custDocIssuedCountry.setValue(details.getCountryCode());
				this.lovDescCustDocIssuedCountryName.setValue(details
						.getLovValue() + "-" + details.getCountryDesc());
			}
		}
	}

	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event)
			throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException,InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,
			JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
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
	 * @param aCustomerDocument
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CustomerDocument aCustomerDocument, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerDocument.getBefImage(), aCustomerDocument);
		return new AuditHeader(getReference(), String.valueOf(aCustomerDocument.getCustID()),
				null, null, auditDetail, aCustomerDocument.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerDocumentDialog, auditHeader);
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
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("CustomerDocument");
		notes.setReference(getReference());
		notes.setVersion(getCustomerDocument().getVersion());
		return notes;
	}
	/**
	 *  Get the Reference value
	 */
	private String getReference(){
		return getCustomerDocument().getCustID()
		+ PennantConstants.KEY_SEPERATOR + getCustomerDocument().getCustDocType();
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

	public CustomerDocument getCustomerDocument() {
		return this.customerDocument;
	}
	public void setCustomerDocument(CustomerDocument customerDocument) {
		this.customerDocument = customerDocument;
	}

	public void setCustomerDocumentService(
			CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}
	public CustomerDocumentService getCustomerDocumentService() {
		return this.customerDocumentService;
	}

	public void setCustomerDocumentListCtrl(
			CustomerDocumentListCtrl customerDocumentListCtrl) {
		this.customerDocumentListCtrl = customerDocumentListCtrl;
	}
	public CustomerDocumentListCtrl getCustomerDocumentListCtrl() {
		return this.customerDocumentListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public JdbcSearchObject<Customer> getSearchObj() {
		return searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Customer> searchObj) {
		this.searchObj = searchObj;
	}

	public JdbcSearchObject<SecurityUser> getSecUserSearchObj() {
		return secUserSearchObj;
	}
	public void setSecUserSearchObj(JdbcSearchObject<SecurityUser> secUserSearchObj) {
		this.secUserSearchObj = secUserSearchObj;
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

	public void setCustomerDocuments(List<CustomerDocument> customerDocuments) {
		this.customerDocuments = customerDocuments;
	}
	public List<CustomerDocument> getCustomerDocuments() {
		return customerDocuments;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}
	
	public PagedListWrapper<SecurityUser> getSecUserPagedListWrapper() {
		return secUserPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setSecUserPagedListWrapper() {
		if (this.secUserPagedListWrapper == null) {
			this.secUserPagedListWrapper = (PagedListWrapper<SecurityUser>) SpringUtil.getBean("pagedListWrapper");
		}
	}

}
