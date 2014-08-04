/**
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
 * FileName    		:  ContractorAssetDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.contractor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.service.finance.contractor.ContractorAssetDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DisbursementDetailDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceDetails/ContractorAssetDetail/contractorAssetDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ContractorAssetDetailDialogCtrl extends GFCBaseListCtrl<ContractorAssetDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(ContractorAssetDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ContractorAssetDetailDialog; 

	protected Row 			row0; 
	protected Label	        label_custID;
	protected Hlayout	    hlayout_custID;
	protected Space	        space_custID;
	protected Longbox	    custID;
	protected Textbox 		custIDName;
	protected Label	        labelCustIDName;
	protected Button	   	btnSearchcustID;

	protected Label 		label_FinReference;
	protected Hlayout 		hlayout_FinReference;
	protected Space 		space_FinReference;

	protected Textbox 		finReference; 
	protected Label 		label_AssetDesc;
	protected Hlayout 		hlayout_AssetDesc;
	protected Space 		space_AssetDesc; 

	protected Textbox 		assetDesc; 
	protected Row 			row1; 
	protected Label 		label_AssetValue;

	protected CurrencyBox	assetValue; 

	protected Label 		recordStatus; 
	protected Label 		recordType;	 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;

	// not auto wired vars
	private ContractorAssetDetail contractorAssetDetail; // overhanded per param
	private transient DisbursementDetailDialogCtrl disbursementDetailDialogCtrl; // overhanded per param
	private boolean newRecord=false;
	private boolean newContractor=false;
	private int ccyFormatter = 0;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_FinReference;
	private transient long	    	oldVar_custID;
	private transient String	    oldVar_custIDName;
	private transient String  		oldVar_AssetDesc;
	private transient BigDecimal  	oldVar_AssetValue;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ContractorAssetDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 
	protected Button btnEdit; 
	protected Button btnDelete; 
	protected Button btnSave; 
	protected Button btnCancel; 
	protected Button btnClose; 
	protected Button btnHelp; 
	protected Button btnNotes; 

	// ServiceDAOs / Domain Classes
	private transient ContractorAssetDetailService contractorAssetDetailService;
	private List<ContractorAssetDetail> contractorAssetDetails;
	/**
	 * default constructor.<br>
	 */
	public ContractorAssetDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ContractorAssetDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ContractorAssetDetailDialog(Event event) throws Exception {
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
			if (args.containsKey("contractorAssetDetail")) {
				this.contractorAssetDetail = (ContractorAssetDetail) args.get("contractorAssetDetail");
				ContractorAssetDetail befImage = new ContractorAssetDetail();
				BeanUtils.copyProperties(this.contractorAssetDetail, befImage);
				this.contractorAssetDetail.setBefImage(befImage);

				setContractorAssetDetail(this.contractorAssetDetail);
			} else {
				setContractorAssetDetail(null);
			}

			if(getContractorAssetDetail().isNewRecord()){
				setNewRecord(true);
			}

			if(args.containsKey("disbursementDetailDialogCtrl")){
				setDisbursementDetailDialogCtrl((DisbursementDetailDialogCtrl)args.get("disbursementDetailDialogCtrl"));
			}

			if(args.containsKey("financeMainDialogCtrl")){
				ccyFormatter = getDisbursementDetailDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();

				setNewContractor(true);
				if(args.containsKey("newRecord")){
					setNewRecord(true);
				}else{
					setNewRecord(false);
				}

				this.contractorAssetDetail.setWorkflowId(0);
				if(args.containsKey("roleCode")){
					setRole((String) args.get("roleCode"));
					getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "ContractorAssetDetailDialog");
				}
			}else{
				ccyFormatter = 2;
			}

			doLoadWorkFlow(this.contractorAssetDetail.isWorkflow(),this.contractorAssetDetail.getWorkflowId(),this.contractorAssetDetail.getNextTaskId());

			if (isWorkFlowEnabled()){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "ContractorAssetDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getContractorAssetDetail());
		} catch (Exception e) {
			createException(window_ContractorAssetDetailDialog, e);
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
		doStoreInitValues();
		doEdit();
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
		doResetInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_ContractorAssetDetailDialog);
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
	public void onClose$window_ContractorAssetDetailDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
	}



	public void onChange$custIDName(Event event) {
		logger.debug("Entering" + event.toString());

		this.custIDName.clearErrorMessage();

		Customer details = (Customer)PennantAppUtil.getCustomerObject(this.custIDName.getValue(), null);

		if(details == null) {
			this.custID.setValue(Long.valueOf(0));
			this.labelCustIDName.setValue("");
			throw new WrongValueException( this.custIDName, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CommitmentDialog_custID.value") }));
		} else {
			if (details != null) {
				this.custID.setValue(Long.valueOf(details.getCustID()));
				this.custIDName.setValue(String.valueOf(details.getCustCIF()));
				this.labelCustIDName.setValue(details.getCustShrtName());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchcustID(Event event) {

		Object dataObject = ExtendedSearchListBox.show(this.window_ContractorAssetDetailDialog, "Customer");
		if (dataObject instanceof String) {
			this.custID.setText("");
			this.custIDName.setValue("");
			this.labelCustIDName.setValue("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setValue(Long.valueOf(details.getCustID()));
				this.custIDName.setValue(String.valueOf(details.getCustCIF()));
				this.labelCustIDName.setValue(details.getCustShrtName());
			}
		}
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
			ScreenCTL.displayNotes(getNotes("ContractorAssetDetail",getContractorAssetDetail().getFinReference(),getContractorAssetDetail().getVersion()),this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());

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
	 * @param aContractorAssetDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(ContractorAssetDetail aContractorAssetDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aContractorAssetDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aContractorAssetDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aContractorAssetDetail = getContractorAssetDetailService().getNewContractorAssetDetail();

			setContractorAssetDetail(aContractorAssetDetail);
		} else {
			setContractorAssetDetail(aContractorAssetDetail);
		}

		try {
			
			// set ReadOnly mode accordingly if the object is new or not.
			if (isNewRecord()) {
				this.btnCtrl.setInitNew();
				doEdit();
				// setFocus
				this.custIDName.focus();
			} else {
				if (isNewContractor()) {
					doEdit();
				} else if (isWorkFlowEnabled()) {
					this.btnNotes.setVisible(true);
					doEdit();
				} else {
					this.btnCtrl.setBtnStatus_Enquiry();
					doReadOnly();
					btnCancel.setVisible(false);
				}
			}

			// fill the components with the data
			doWriteBeanToComponents(aContractorAssetDetail);
			
			// set ReadOnly mode accordingly if the object is new or not.
			doStoreInitValues();

			// stores the initial data for comparing if they are changed
			// during user action.
			getBorderLayoutHeight();
			this.window_ContractorAssetDetailDialog.setHeight(this.borderLayoutHeight-200+"px");
			this.window_ContractorAssetDetailDialog.setWidth("70%");
			this.window_ContractorAssetDetailDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			 this.btnSearchcustID.setDisabled(false);
			if (isNewContractor()) {
				this.btnCancel.setVisible(false);
			}
			this.custIDName.setReadonly(isReadOnly("ContractorAssetDetailDialog_custID"));
			this.btnSearchcustID.setVisible(!isReadOnly("ContractorAssetDetailDialog_custID"));
			this.btnSearchcustID.setDisabled(isReadOnly("ContractorAssetDetailDialog_custID"));
		} else {
			this.custID.setReadonly(true);
			this.custIDName.setReadonly(true);
			this.btnSearchcustID.setDisabled(true);
			this.btnSearchcustID.setVisible(false);
			this.btnCancel.setVisible(true);
		}
		
		this.finReference.setReadonly(isReadOnly("ContractorAssetDetailDialog_FinReference"));
		this.assetDesc.setReadonly(isReadOnly("ContractorAssetDetailDialog_AssetDesc"));
		this.assetValue.setDisabled(isReadOnly("ContractorAssetDetailDialog_AssetValue"));
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.contractorAssetDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(newContractor){
				if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newContractor);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewContractor()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}


	public void doReadOnly() {
		logger.debug("Entering");
		this.custID.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		this.finReference.setReadonly(true);
		this.custIDName.setReadonly(true);
		this.btnSearchcustID.setVisible(false);
		this.btnSearchcustID.setDisabled(true);
		this.assetDesc.setReadonly(true);
		this.assetValue.setDisabled(true);
		
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

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

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
		
		if(!enqModule){
			getUserWorkspace().alocateAuthorities("ContractorAssetDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finReference.setMaxlength(50);
		this.assetDesc.setMaxlength(50);
		this.assetValue.setMaxlength(18);
		this.assetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.assetValue.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.assetValue.setScale(ccyFormatter);

		setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
	}


	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_custIDName = this.custIDName.getValue();
		this.oldVar_custID = this.custID.getValue();
		this.oldVar_FinReference = this.finReference.getValue();
		this.oldVar_AssetDesc = this.assetDesc.getValue();
		this.oldVar_AssetValue = this.assetValue.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custIDName.setValue(this.oldVar_custIDName);
		this.custID.setValue(this.oldVar_custID);
		this.finReference.setValue(this.oldVar_FinReference);
		this.assetDesc.setValue(this.oldVar_AssetDesc);
		this.assetValue.setValue(this.oldVar_AssetValue);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aContractorAssetDetail
	 *            ContractorAssetDetail
	 */
	public void doWriteBeanToComponents(ContractorAssetDetail aContractorAssetDetail) {
		logger.debug("Entering") ;
		this.custID.setValue(aContractorAssetDetail.getCustID());
		this.custIDName.setValue(aContractorAssetDetail.getLovDescCustCIF());
		this.labelCustIDName.setValue(aContractorAssetDetail.getLovDescCustShrtName());
		this.finReference.setValue(aContractorAssetDetail.getFinReference());
		this.finReference.setValue(aContractorAssetDetail.getFinReference());
		this.assetDesc.setValue(aContractorAssetDetail.getAssetDesc());
		this.assetValue.setValue(PennantApplicationUtil.formateAmount(aContractorAssetDetail.getAssetValue(), ccyFormatter));

		this.recordStatus.setValue(aContractorAssetDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aContractorAssetDetail.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aContractorAssetDetail
	 */
	public void doWriteComponentsToBean(ContractorAssetDetail aContractorAssetDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Fin Reference
		try {
			aContractorAssetDetail.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aContractorAssetDetail.setLovDescCustCIF(this.custIDName.getValue());
			aContractorAssetDetail.setLovDescCustShrtName(this.labelCustIDName.getValue());
			aContractorAssetDetail.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Asset Desc
		try {
			aContractorAssetDetail.setAssetDesc(this.assetDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Asset Value
		try {
			if(this.assetValue.getValue()!=null){
				aContractorAssetDetail.setAssetValue(PennantApplicationUtil.unFormateAmount(this.assetValue.getValue(), ccyFormatter));
			}
			if(aContractorAssetDetail.getAssetValue().compareTo(aContractorAssetDetail.getTotClaimAmt()) < 0){
				throw new WrongValueException(this.assetValue, "Asset Value cannot be Greater than the claim amount of " + 
						PennantApplicationUtil.formateAmount(aContractorAssetDetail.getTotClaimAmt(), ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		if(aContractorAssetDetail.getAssetValue().compareTo(BigDecimal.ZERO) != 0){
			BigDecimal amount = (aContractorAssetDetail.getTotClaimAmt().divide(aContractorAssetDetail.getAssetValue(), 
					2, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100)) ;

			aContractorAssetDetail.setLovDescClaimPercent(PennantApplicationUtil.unFormateAmount(amount, 2));
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

		if (!StringUtils.trimToEmpty(this.oldVar_FinReference).equals(StringUtils.trimToEmpty(this.finReference.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_AssetDesc).equals(StringUtils.trimToEmpty(this.assetDesc.getValue()))) {
			return true;
		}
		if (this.oldVar_AssetValue != this.assetValue.getValue()) {
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
		//Fin Reference
		/*if (!this.finReference.isReadonly()){
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_ContractorAssetDetailDialog_FinReference.value"),PennantRegularExpressions.REGEX_NAME,true));
		}*/
		//Asset Desc
		if (!this.assetDesc.isReadonly()){
			this.assetDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_ContractorAssetDetailDialog_AssetDesc.value"),PennantRegularExpressions.REGEX_ALPHANUM_SPACE,true));
		}
		//Asset Value
		if (!this.assetValue.isReadonly()){
			this.assetValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ContractorAssetDetailDialog_AssetValue.value"), ccyFormatter, true, false, 0));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		this.assetDesc.setConstraint("");
		this.assetValue.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		if (this.btnSearchcustID.isVisible() && !this.btnSearchcustID.isDisabled()) {
			this.custIDName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_ContractorAssetDetailDialog_custID.value") }));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.assetDesc.setErrorMessage("");
		this.assetValue.setErrorMessage("");
		logger.debug("Leaving");
	}

	/*	*//**
	 * Method for Refreshing List after Save/Delete a Record
	 *//*

	private void refreshList(){
		final JdbcSearchObject<ContractorAssetDetail> soContractorAssetDetail = getdisbursementDetailDialogCtrl().getSearchObj();
		getdisbursementDetailDialogCtrl().pagingContractorAssetDetailList.setActivePage(0);
		getdisbursementDetailDialogCtrl().getPagedListWrapper().setSearchObject(soContractorAssetDetail);
		if(getdisbursementDetailDialogCtrl().listBoxContractorAssetDetail!=null){
			getdisbursementDetailDialogCtrl().listBoxContractorAssetDetail.getListModel();
		}
	} 
	  */

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
			closePopUpWindow(this.window_ContractorAssetDetailDialog, "ContractorAssetDetailDialog");
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Deletes a ContractorAssetDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final ContractorAssetDetail aContractorAssetDetail = new ContractorAssetDetail();
		BeanUtils.copyProperties(getContractorAssetDetail(), aContractorAssetDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aContractorAssetDetail.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");
			
			if(getDisbursementDetailDialogCtrl() != null){
				List<FinanceDisbursement> list = getDisbursementDetailDialogCtrl().getDisbursementDetails();
				if(list != null && !list.isEmpty()){
					for (FinanceDisbursement disbursement : list) {
						if(disbursement.getDisbBeneficiary() == aContractorAssetDetail.getCustID() && 
								!StringUtils.trimToEmpty(disbursement.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)){
							PTMessageUtils.showErrorMessage("Not Allowed to Delete This Record. Disbursement Details Exist on this Contractor.");
							return;
						}
					}
				}
			}

			if (StringUtils.trimToEmpty(aContractorAssetDetail.getRecordType()).equals("")){
				aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion()+1);
				aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aContractorAssetDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aContractorAssetDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aContractorAssetDetail.getNextTaskId(), aContractorAssetDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewContractor()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newContractor(aContractorAssetDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_ContractorAssetDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getDisbursementDetailDialogCtrl().doFillContractorDetails(this.contractorAssetDetails);
						closePopUpWindow(this.window_ContractorAssetDetailDialog, "ContractorAssetDetailDialog");
					}	
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_ContractorAssetDetailDialog,e);
			}

		}
		logger.debug("Leaving");
	}


	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.finReference.setValue("");
		this.assetDesc.setValue("");
		this.assetValue.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ContractorAssetDetail aContractorAssetDetail = new ContractorAssetDetail();
		BeanUtils.copyProperties(getContractorAssetDetail(), aContractorAssetDetail);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aContractorAssetDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aContractorAssetDetail.getNextTaskId(), aContractorAssetDetail);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aContractorAssetDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the ContractorAssetDetail object with the components data
			doWriteComponentsToBean(aContractorAssetDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aContractorAssetDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aContractorAssetDetail.getRecordType()).equals("")){
				aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion()+1);
				if(isNew){
					aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aContractorAssetDetail.setNewRecord(true);
				}
			}
		}else{

			if(isNewContractor()){
				if(isNewRecord()){
					aContractorAssetDetail.setVersion(1);
					aContractorAssetDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aContractorAssetDetail.getRecordType()).equals("")){
					aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion()+1);
					aContractorAssetDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
			}else{
				aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}			
		}

		// save it to database
		try {

			AuditHeader auditHeader =  newContractor(aContractorAssetDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_ContractorAssetDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getDisbursementDetailDialogCtrl().doFillContractorDetails(this.contractorAssetDetails);
				closePopUpWindow(this.window_ContractorAssetDetailDialog, "ContractorAssetDetailDialog");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_ContractorAssetDetailDialog,e);
		}
		logger.debug("Leaving");
	}


	private AuditHeader newContractor(ContractorAssetDetail aContractorAssetDetail, String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aContractorAssetDetail, tranType);
		this.contractorAssetDetails = new ArrayList<ContractorAssetDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aContractorAssetDetail.getLovDescCustCIF();
		valueParm[1] = String.valueOf(aContractorAssetDetail.getFinReference());

		errParm[0] = PennantJavaUtil.getLabel("label_ContractorAssetDetailDialog_Contractor.value") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ContractorAssetDetailDialog_FinReference.value") + ":"+valueParm[1];

		if(getDisbursementDetailDialogCtrl().getContractorAssetDetails() !=null && getDisbursementDetailDialogCtrl().getContractorAssetDetails().size()>0){
			for (int i = 0; i < getDisbursementDetailDialogCtrl().getContractorAssetDetails().size(); i++) {
				ContractorAssetDetail loanDetail = getDisbursementDetailDialogCtrl().getContractorAssetDetails().get(i);

				if(aContractorAssetDetail.getCustID() == loanDetail.getCustID()){ // Both Current and Existing list same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
								"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType == PennantConstants.TRAN_DEL){
						if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							contractorAssetDetails.add(aContractorAssetDetail);
						}else if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							contractorAssetDetails.add(aContractorAssetDetail);
						}else if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							//No Such Case
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							contractorAssetDetails.add(loanDetail);
						}
					}
				}else{
					contractorAssetDetails.add(loanDetail);
				}
			}
		}

		if(!recordAdded){
			contractorAssetDetails.add(aContractorAssetDetail);
		}
		return auditHeader;
	} 

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ContractorAssetDetail aContractorAssetDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aContractorAssetDetail.getBefImage(), aContractorAssetDetail);   
		return new AuditHeader(aContractorAssetDetail.getFinReference(),null,null,null,auditDetail,aContractorAssetDetail.getUserDetails(),getOverideMap());
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

	public ContractorAssetDetail getContractorAssetDetail() {
		return this.contractorAssetDetail;
	}

	public void setContractorAssetDetail(ContractorAssetDetail contractorAssetDetail) {
		this.contractorAssetDetail = contractorAssetDetail;
	}

	public void setContractorAssetDetailService(ContractorAssetDetailService contractorAssetDetailService) {
		this.contractorAssetDetailService = contractorAssetDetailService;
	}

	public ContractorAssetDetailService getContractorAssetDetailService() {
		return this.contractorAssetDetailService;
	}

	public void setDisbursementDetailDialogCtrl(DisbursementDetailDialogCtrl disbursementDetailDialogCtrl) {
		this.disbursementDetailDialogCtrl = disbursementDetailDialogCtrl;
	}

	public DisbursementDetailDialogCtrl getDisbursementDetailDialogCtrl() {
		return this.disbursementDetailDialogCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewContractor() {
		return newContractor;
	}

	public void setNewContractor(boolean newContractor) {
		this.newContractor = newContractor;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public List<ContractorAssetDetail> getContractorAssetDetails() {
		return contractorAssetDetails;
	}

	public void setContractorAssetDetails(
			List<ContractorAssetDetail> contractorAssetDetails) {
		this.contractorAssetDetails = contractorAssetDetails;
	}



}
