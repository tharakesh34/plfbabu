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
 * FileName    		:  PartnerBankDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-03-2017    														*
 *                                                                  						*
 * Modified Date    :  09-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-03-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.partnerbank.partnerbank;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.partnerbank.PartnerBranchModes;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionStaticListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/partnerbank/PartnerBank/partnerBankDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class PartnerBankDialogCtrl extends GFCBaseCtrl<PartnerBank> {

	private static final long				serialVersionUID	= 1L;
	private static final Logger				logger				= Logger.getLogger(PartnerBankDialogCtrl.class);

	protected Window						window_PartnerBankDialog;

	protected Borderlayout					borderlayoutPartnerBank;
	protected Textbox						partnerBankCode;
	protected Textbox						partnerBankName;
	protected ExtendedCombobox				bankCode;
	protected ExtendedCombobox				bankBranchCode;
	protected Textbox						branchMICRCode;
	protected Textbox						branchIFSCCode;
	protected Textbox						branchCity;
	protected Textbox						utilityCode;
	protected Textbox						accountNo;
	protected Checkbox						active;
	protected ExtendedCombobox				acType;
	protected Checkbox						reqFileDownload;
	protected Intbox						inFavourLength;
	protected Button						btnSearchModeDisbursment;
	protected Textbox						modeDisbursment;
	protected Space							space_modeDisbursments;
	protected Space							space_FileName;
	protected Checkbox						alwDisburment;
	protected Label							label_PartnerBankDialog_ModeDisbursment;
	protected Button						btnSearchModePayments;
	protected Textbox						modePayments;
	protected Space							space_modePayments;
	protected Checkbox						alwPayments;
	protected Label							label_PartnerBankDialog_ModePayments;
	protected Button						btnSearchModeReceipts;
	protected Textbox						modeReceipts;
	protected Space							space_modeReceipts;
	protected Checkbox						alwReceipts;
	protected Label							label_PartnerBankDialog_ModeReceipts;
	protected Textbox						sapGLCode;
	protected Textbox						profitCenterID;
	protected Textbox 						costCenterID;
	protected Textbox 						fileName;
	protected Row							AlwBranchCode;
	protected Textbox						alwBankBranchCode;
	protected Button						btnSearchBranchCode;
	protected Space							space_AlwBankBranchCode;
	
	private PartnerBank						partnerBank;															
	private transient PartnerBankListCtrl	partnerBankListCtrl;													

	private transient PartnerBankService	partnerBankService;
	private transient BankDetailService		bankDetailService;
	//protected int							accNoLength;
	
	/**
	 * default constructor.<br>
	 */
	public PartnerBankDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PartnerBankDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_PartnerBankDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());

		// Set the page level components.
		setPageComponents(window_PartnerBankDialog);

		try {
			// Get the required arguments.
			this.partnerBank = (PartnerBank) arguments.get("partnerBank");
			this.partnerBankListCtrl = (PartnerBankListCtrl) arguments.get("partnerBankListCtrl");

			if (this.partnerBank == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			PartnerBank partnerBank = new PartnerBank();
			BeanUtils.copyProperties(this.partnerBank, partnerBank);
			this.partnerBank.setBefImage(partnerBank);

			// Render the page and display the data.
			doLoadWorkFlow(this.partnerBank.isWorkflow(), this.partnerBank.getWorkflowId(),
					this.partnerBank.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.partnerBank);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.partnerBankCode.setMaxlength(8);
		this.partnerBankName.setMaxlength(50);
		this.fileName.setMaxlength(30);
		
		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "bankCode" });

		this.bankBranchCode.setModuleName("BankBranch");
		this.bankBranchCode.setMandatoryStyle(true);
		this.bankBranchCode.setDisplayStyle(2);
		this.bankBranchCode.setValueColumn("BranchCode");
		this.bankBranchCode.setDescColumn("BranchDesc");
		this.bankBranchCode.setValidateColumns(new String[] { "BranchCode" });
		this.bankBranchCode.setVisible(false);

		this.branchMICRCode.setMaxlength(20);
		this.branchMICRCode.setReadonly(true);
		this.branchIFSCCode.setMaxlength(20);
		this.branchIFSCCode.setReadonly(true);
		this.branchCity.setMaxlength(50);
		this.branchCity.setReadonly(true);
		this.utilityCode.setMaxlength(8);
		this.accountNo.setMaxlength(50);
		this.inFavourLength.setMaxlength(2);
		/*if(StringUtils.isNotBlank(this.partnerBank.getBankCode())){
			accNoLength = getBankDetailService().getAccNoLengthByCode(this.partnerBank.getBankCode());
		}*/
		
		this.acType.setModuleName("AccountType");
		this.acType.setMandatoryStyle(true);
		this.acType.setValueColumn("AcType");
		this.acType.setDescColumn("AcTypeDesc");
		this.acType.setDisplayStyle(2);
		this.acType.setValidateColumns(new String[] { "AcType" });
		
		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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

		doWriteBeanToComponents(this.partnerBank.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aPartnerBank
	 * 
	 */
	public void doWriteBeanToComponents(PartnerBank aPartnerBank) {
		logger.debug("Entering");
		
		this.partnerBankCode.setValue(aPartnerBank.getPartnerBankCode());
		this.partnerBankName.setValue(aPartnerBank.getPartnerBankName());
		this.bankCode.setValue(aPartnerBank.getBankCode());
		this.bankBranchCode.setValue(aPartnerBank.getBankBranchCode());
		this.bankBranchCode.setVisible(true);
		this.branchMICRCode.setValue(aPartnerBank.getBranchMICRCode());
		this.branchIFSCCode.setValue(aPartnerBank.getBranchIFSCCode());
		this.branchCity.setValue(aPartnerBank.getBranchCity());
		this.utilityCode.setValue(aPartnerBank.getUtilityCode());
		this.accountNo.setValue(aPartnerBank.getAccountNo());
		this.acType.setValue(aPartnerBank.getAcType());
		this.reqFileDownload.setChecked(aPartnerBank.isAlwFileDownload());
		this.inFavourLength.setValue(aPartnerBank.getInFavourLength());
		this.active.setChecked(aPartnerBank.isActive());
		this.sapGLCode.setValue(aPartnerBank.getHostGLCode());
		this.profitCenterID.setValue(aPartnerBank.getProfitCenterID());
		this.costCenterID.setValue(aPartnerBank.getCostCenterID());
		this.fileName.setValue(aPartnerBank.getFileName());
		
		this.alwDisburment.setChecked(aPartnerBank.isAlwDisb());
		
		if (this.alwDisburment.isChecked()) {
			this.btnSearchModeDisbursment.setDisabled(isReadOnly("button_PartnerBankDialog_ModeDisbursment"));
			this.modeDisbursment.setReadonly(true);
			this.space_modeDisbursments.setSclass(PennantConstants.mandateSclass);
			
			if (this.reqFileDownload.isChecked()) {
				this.space_FileName.setSclass("");
			} else {
				this.space_FileName.setSclass(PennantConstants.mandateSclass);
			}
		} else {
			this.modeDisbursment.setReadonly(true);
			this.modeDisbursment.setValue("");
			this.space_modeDisbursments.setSclass("");
			this.btnSearchModeDisbursment.setDisabled(true);
		}
		
		this.alwPayments.setChecked(aPartnerBank.isAlwPayment());
		if (this.alwPayments.isChecked()) {
			this.modePayments.setReadonly(true);
			this.space_modePayments.setSclass(PennantConstants.mandateSclass);
			this.btnSearchModePayments.setDisabled(isReadOnly("button_PartnerBankDialog_ModePayments"));
		} else {
			this.modePayments.setReadonly(true);
			this.modePayments.setValue("");
			this.space_modePayments.setSclass("");
			this.btnSearchModePayments.setDisabled(true);
		}
		
		this.alwReceipts.setChecked(aPartnerBank.isAlwReceipt());
		if (this.alwReceipts.isChecked()) {
			this.modeReceipts.setReadonly(true);
			this.space_modeReceipts.setSclass(PennantConstants.mandateSclass);
			this.btnSearchModeReceipts.setDisabled(isReadOnly("button_PartnerBankDialog_ModeReceipts"));
			this.btnSearchBranchCode.setDisabled(isReadOnly("button_PartnerBankDialog_BranchCode"));
		} else {
			this.modeReceipts.setReadonly(true);
			this.modeReceipts.setValue("");
			this.space_modeReceipts.setSclass("");
			this.btnSearchModeReceipts.setDisabled(true);
			this.btnSearchBranchCode.setDisabled(true);
		}
		
		if (this.alwReceipts.isChecked() || this.alwDisburment.isChecked() ) {
			this.reqFileDownload.setDisabled(isReadOnly("PartnerBankDialog_AlwFileDownload"));
		} else {
			this.reqFileDownload.setDisabled(true);
		}
		
		if (aPartnerBank.isNewRecord()) {
			this.bankCode.setDescription("");
			this.bankBranchCode.setDescription("");
			this.acType.setDescription("");
		} else {
			this.bankCode.setDescription(aPartnerBank.getBankCodeName());
			this.bankBranchCode.setDescription(aPartnerBank.getBankBranchCodeName());
			this.acType.setDescription(aPartnerBank.getAcTypeName());
		}
		
		if(aPartnerBank.isNew() || (aPartnerBank.getRecordType() != null ? aPartnerBank.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(aPartnerBank.getRecordStatus());
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPartnerBank
	 */
	public void doWriteComponentsToBean(PartnerBank aPartnerBank) {
		logger.debug("Entering");
		
		doSetLOVValidation();
		List<PartnerBankModes> 		  partneBankModesList = new ArrayList<PartnerBankModes>() ;
		List<PartnerBranchModes>      partnerBranchModesList= new ArrayList<PartnerBranchModes>();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Partner Bank Code
		try {

			aPartnerBank.setPartnerBankCode(this.partnerBankCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Partner Bank Name
		try {
			aPartnerBank.setPartnerBankName(this.partnerBankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bank Code
		try {
			aPartnerBank.setBankCode(this.bankCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bank Branch Code
		try {
			aPartnerBank.setBankBranchCode(this.bankBranchCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch MICR Code
		try {
			aPartnerBank.setBranchMICRCode(this.branchMICRCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch IFSC Code
		try {
			aPartnerBank.setBranchIFSCCode(this.branchIFSCCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch City
		try {
			aPartnerBank.setBranchCity(this.branchCity.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Utility Code
		try {
			aPartnerBank.setUtilityCode(this.utilityCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Account No
		try {
			aPartnerBank.setAccountNo(this.accountNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Account Type
		try {
			aPartnerBank.setAcType(this.acType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Required File Download
		try {
			aPartnerBank.setAlwFileDownload(this.reqFileDownload.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Allowed Disbursement 
		try {
			aPartnerBank.setAlwDisb(this.alwDisburment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Allowed Payments
		try {
			aPartnerBank.setAlwPayment(this.alwPayments.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Allowed Receipts
		try {
			aPartnerBank.setAlwReceipt(this.alwReceipts.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//In Favor Length
		try {
			aPartnerBank.setInFavourLength(this.inFavourLength.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Host GL Code
		try {
			aPartnerBank.setHostGLCode(this.sapGLCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Profit Centre
		try {
			aPartnerBank.setProfitCenterID(this.profitCenterID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Cross Centre
		try {
			aPartnerBank.setCostCenterID(this.costCenterID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Active
		try {
			aPartnerBank.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//File Name
		try {
			aPartnerBank.setFileName(this.fileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.modeDisbursment.getValue() != null) {
				preparePaymentModes(this.modeDisbursment.getValue(), AccountConstants.PARTNERSBANK_DISB, partneBankModesList);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.modeReceipts.getValue()!=null){
				preparePaymentModes(this.modeReceipts.getValue(), AccountConstants.PARTNERSBANK_RECEIPTS, partneBankModesList);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.modePayments.getValue() != null) {
				preparePaymentModes(this.modePayments.getValue(), AccountConstants.PARTNERSBANK_PAYMENT, partneBankModesList);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		aPartnerBank.setPartnerBankModesList(partneBankModesList);
		
		try {
			if(this.alwBankBranchCode.getValue()!=null){
				prepareCashModes(this.alwBankBranchCode.getValue(),DisbursementConstants.PAYMENT_TYPE_CASH,partnerBranchModesList);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		aPartnerBank.setPartnerBranchModesList(partnerBranchModesList);
		
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	private List<PartnerBranchModes> prepareCashModes(String cashmode, String paymentTypeCash, List<PartnerBranchModes> partneBankModesList) {
	
		String[] cashMode = cashmode.split(",");
		PartnerBranchModes	paymentMode ;

		for (int i = 0; i < cashMode.length; i++) {
				paymentMode = new PartnerBranchModes();
				paymentMode.setBranchCode(cashMode[i]);
				paymentMode.setPaymentMode(paymentTypeCash);
				partneBankModesList.add(paymentMode);
    		}

		return partneBankModesList;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aPartnerBank
	 *            The entity that need to be render.
	 */
	public void doShowDialog(PartnerBank aPartnerBank) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (partnerBank.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.partnerBankCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.partnerBankName.focus();
				if (StringUtils.isNotBlank(partnerBank.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// fill the components with the data
		doWriteBeanToComponents(partnerBank);
		if(aPartnerBank.getPartnerBankModesList()!=null && !aPartnerBank.getPartnerBankModesList().isEmpty()){
				setAccTypeModeDescription(aPartnerBank.getPartnerBankModesList());
		}
		if (aPartnerBank.getPartnerBranchModesList() != null && !aPartnerBank.getPartnerBranchModesList().isEmpty()) {
			setAccCashModeDescription(aPartnerBank.getPartnerBranchModesList());
			
		}
		for (PartnerBranchModes partnerBranchModesList : aPartnerBank.getPartnerBranchModesList()) {
			if(partnerBranchModesList.getBranchCode()!=null){
				this.btnSearchBranchCode.setDisabled(isReadOnly("button_PartnerBankDialog_BranchCode"));
				this.AlwBranchCode.setVisible(true);
				this.space_AlwBankBranchCode.setSclass(PennantConstants.mandateSclass);
			}else{
				this.AlwBranchCode.setVisible(false);
				this.btnSearchBranchCode.setDisabled(true);
				this.space_AlwBankBranchCode.setSclass("");
			}
		}
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");

	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		doClearMessage();
		
		//Partner Bank Code
		if (!this.partnerBankCode.isReadonly()) {
			this.partnerBankCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_PartnerBankCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		
		//Partner Bank name
		if (!this.partnerBankName.isReadonly()) {
			this.partnerBankName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_PartnerBankName.value"), PennantRegularExpressions.REGEX_DESCRIPTION,
					true));
		}
		
		//Bank Code
		if (!this.bankCode.isReadonly()) {
			this.bankCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_PartnerBankDialog_BankCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		//Bank Branch Code
		if (!this.bankBranchCode.isReadonly()) {
			this.bankBranchCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_BankBranchCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		//Branch MICR Code
		if (!this.branchMICRCode.isReadonly()) {
			this.branchMICRCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_BranchMICRCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					false));
		}
		//Branch IFSC Code
		if (!this.branchIFSCCode.isReadonly()) {
			this.branchIFSCCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_BranchIFSCCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					false));
		}
		//Branch City
		if (!this.branchCity.isReadonly()) {
			this.branchCity
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PartnerBankDialog_BranchCity.value"),
							null, false));
		}
		
		//Account No
		if (!this.accountNo.isReadonly()) {
			/*this.accountNo.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_AccountNo.value"), PennantRegularExpressions.REGEX_ACCOUNTNUMBER,
					true,accNoLength,accNoLength));*/
			this.accountNo.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_AccountNo.value"), PennantRegularExpressions.REGEX_ACCOUNTNUMBER,true));
		}
		
		//Disbursement
		if (!this.btnSearchModeDisbursment.isDisabled()) {
			this.modeDisbursment.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_ModeDisbursment.value"), null, true));
		}
		
		//Receipts
		if (!this.btnSearchModeReceipts.isDisabled()) {
			this.modeReceipts.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_ModeReceipts.value"), null, true));
		}
		
		//Payments
		if (!this.btnSearchModePayments.isDisabled() ) {
			this.modePayments.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_ModePayments.value"), null, true));
		}

		//Account Type Code
		if (!this.acType.isReadonly()) {
			this.acType.setConstraint(new PTStringValidator(Labels.getLabel("label_PartnerBankDialog_GLCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		
		//Host GL Code
		if (!this.sapGLCode.isReadonly()) {
			this.sapGLCode.setConstraint(new PTStringValidator(Labels.getLabel("label_PartnerBankDialog_SAPGLCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, false));
		}
		
		//Profit Centre
		if (!this.profitCenterID.isDisabled()) {
			this.profitCenterID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_ProfitCenter.value"), PennantRegularExpressions.REGEX_ALPHANUM_FSLASH_SPACE,
					false));
		}
		
		//Profit Centre
		if (!this.costCenterID.isReadonly()) {
			this.costCenterID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_CostCenter.value"), PennantRegularExpressions.REGEX_ALPHANUM_FSLASH_SPACE,
					false));
		}
		//Profit Centre
		if (!this.inFavourLength.isReadonly()) {
			this.inFavourLength.setConstraint(new PTNumberValidator(Labels.getLabel("label_PartnerBankDialog_FavourLength.value"), false,false,99));
		}
		
		if (!this.btnSearchBranchCode.isDisabled() && !this.alwBankBranchCode.isVisible()) {
			this.alwBankBranchCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_PartnerBankDialog_AlwBankBranchCode.value"), null, true));
		}

		//Branch IFSC Code
		if (!this.fileName.isReadonly() && this.alwDisburment.isChecked() || this.alwPayments.isChecked() && !this.reqFileDownload.isChecked()) {
			this.fileName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PartnerBankDialog_FileName.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, !this.reqFileDownload.isChecked()));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		
		this.partnerBankCode.setConstraint("");
		this.partnerBankName.setConstraint("");
		this.bankCode.setConstraint("");
		this.bankBranchCode.setConstraint("");
		this.branchMICRCode.setConstraint("");
		this.branchIFSCCode.setConstraint("");
		this.branchCity.setConstraint("");
		this.utilityCode.setConstraint("");
		this.accountNo.setConstraint("");
		this.acType.setConstraint("");
		this.modeDisbursment.setConstraint("");
		this.modeReceipts.setConstraint("");
		this.modePayments.setConstraint("");
		this.sapGLCode.setConstraint("");
		this.profitCenterID.setConstraint("");
		this.costCenterID.setConstraint("");
		this.alwBankBranchCode.setConstraint("");
		this.fileName.setConstraint("");
		
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		
		this.partnerBankCode.setErrorMessage("");
		this.partnerBankName.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.bankBranchCode.setErrorMessage("");
		this.branchMICRCode.setErrorMessage("");
		this.branchIFSCCode.setErrorMessage("");
		this.branchCity.setErrorMessage("");
		this.utilityCode.setErrorMessage("");
		this.accountNo.setErrorMessage("");
		this.acType.setErrorMessage("");
		this.modeDisbursment.setErrorMessage("");
		this.modePayments.setErrorMessage("");
		this.modeReceipts.setErrorMessage("");
		this.sapGLCode.setErrorMessage("");
		this.profitCenterID.setErrorMessage("");
		this.costCenterID.setErrorMessage("");
		this.alwBankBranchCode.setErrorMessage("");
		this.fileName.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	/**
	 * Deletes a Academic entity from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");
		final PartnerBank aPartnerBank = new PartnerBank();
		BeanUtils.copyProperties(this.partnerBank, aPartnerBank);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_PartnerBankDialog_PartnerBankCode.value") + " : "
				+ aPartnerBank.getPartnerBankCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aPartnerBank.getRecordType()).equals("")) {
				aPartnerBank.setVersion(aPartnerBank.getVersion() + 1);
				aPartnerBank.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPartnerBank.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aPartnerBank.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aPartnerBank.getNextTaskId(),
							aPartnerBank);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aPartnerBank, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$bankCode(Event event) {
		logger.debug("Entering");
		Object dataObject = bankCode.getObject();
		if (dataObject instanceof String) {
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.bankBranchCode.setValue("");
			this.bankBranchCode.setDescription("");
			this.bankBranchCode.setObject(null);
			Filter[] bankBranchCode = new Filter[1];
			bankBranchCode[0] = new Filter("BankCode","", Filter.OP_EQUAL);
			this.bankBranchCode.setFilters(bankBranchCode);
			this.branchCity.setValue("");
			this.branchMICRCode.setValue("");
			this.branchIFSCCode.setValue("");
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setValue(String.valueOf(details.getBankCode()));
				this.bankCode.setDescription(details.getBankName());
				Filter[] bankBranchCode = new Filter[1];
				bankBranchCode[0] = new Filter("BankCode",this.bankCode.getValue(), Filter.OP_EQUAL);
				this.bankBranchCode.setFilters(bankBranchCode);
				this.bankBranchCode.setValue("");
				this.bankBranchCode.setDescription("");
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$bankBranchCode(Event event) {
		logger.debug("Entering");
		Object dataObject = bankBranchCode.getObject();
		if (dataObject instanceof String) {
			this.bankBranchCode.setValue("");
			this.bankBranchCode.setDescription("");
			this.branchCity.setValue("");
			this.branchMICRCode.setValue("");
			this.branchIFSCCode.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.bankBranchCode.setValue(String.valueOf(details.getBranchCode()));
				this.bankBranchCode.setDescription(details.getBranchDesc());
				this.branchCity.setValue(details.getCity());
				this.branchMICRCode.setValue(details.getMICR());
				this.branchIFSCCode.setValue(details.getIFSC());
				/*if(StringUtils.isNotBlank(details.getBankCode())){
					accNoLength = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
				}*/
			}
		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$acType(Event event) {
		logger.debug("Entering");
		Object dataObject = acType.getObject();
		if (dataObject instanceof String) {
			this.acType.setValue("");
			this.acType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.acType.setValue(String.valueOf(details.getAcType()));
				this.acType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.partnerBank.isNewRecord()) {
			this.partnerBankCode.setReadonly(false);
			this.partnerBankName.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.partnerBankCode.setReadonly(true);
			this.partnerBankName.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.bankCode.setReadonly(isReadOnly("PartnerBankDialog_BankCode"));
		this.bankBranchCode.setReadonly(isReadOnly("PartnerBankDialog_BankBranchCode"));
		this.branchMICRCode.setReadonly(true);//"PartnerBankDialog_BranchMICRCode"
		this.branchIFSCCode.setReadonly(true);//"PartnerBankDialog_BranchIFSCCode"
		this.branchCity.setReadonly(true);//"PartnerBankDialog_BranchCity"
		this.utilityCode.setReadonly(isReadOnly("PartnerBankDialog_UtilityCode"));
		this.accountNo.setReadonly(isReadOnly("PartnerBankDialog_AccountNo"));
		this.acType.setReadonly(isReadOnly("PartnerBankDialog_AccType"));
		this.reqFileDownload.setDisabled(isReadOnly("PartnerBankDialog_AlwFileDownload"));
		this.inFavourLength.setDisabled(isReadOnly("PartnerBankDialog_InFavourLength"));
		this.modeDisbursment.setReadonly(isReadOnly("PartnerBankDialog_DisbursmentMode"));
		this.modePayments.setReadonly(isReadOnly("PartnerBankDialog_PaymentsMode"));
		this.modeReceipts.setReadonly(isReadOnly("PartnerBankDialog_ReceiptsMode"));
		this.active.setDisabled(isReadOnly("PartnerBankDialog_Active"));
		this.alwDisburment.setDisabled(isReadOnly("PartnerBankDialog_AlwDisbursement"));
		this.alwReceipts.setDisabled(isReadOnly("PartnerBankDialog_AlwReceipt"));
		this.alwPayments.setDisabled(isReadOnly("PartnerBankDialog_AlwPayment"));
		this.btnSearchModeDisbursment.setDisabled(isReadOnly("button_PartnerBankDialog_ModeDisbursment"));
		this.btnSearchModePayments.setDisabled(isReadOnly("button_PartnerBankDialog_ModePayments"));
		this.btnSearchModeReceipts.setDisabled(isReadOnly("button_PartnerBankDialog_ModeReceipts"));
		this.btnSearchBranchCode.setDisabled(isReadOnly("button_PartnerBankDialog_BranchCode"));
		this.sapGLCode.setReadonly(isReadOnly("PartnerBankDialog_HostGLCode"));
		this.profitCenterID.setReadonly(isReadOnly("PartnerBankDialog_ProfitCenter"));
		this.costCenterID.setReadonly(isReadOnly("PartnerBankDialog_CrossCentre"));
		this.fileName.setReadonly(isReadOnly("PartnerBankDialog_CrossCentre"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.partnerBank.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {

		logger.debug("Entering");
		this.partnerBankCode.setReadonly(true);
		this.partnerBankName.setReadonly(true);
		this.bankCode.setReadonly(true);
		this.bankBranchCode.setReadonly(true);
		this.branchMICRCode.setReadonly(true);
		this.branchIFSCCode.setReadonly(true);
		this.branchCity.setReadonly(true);
		this.utilityCode.setReadonly(true);
		this.accountNo.setReadonly(true);
		this.acType.setReadonly(true);
		this.reqFileDownload.setDisabled(true);
		this.inFavourLength.setReadonly(true);
		this.active.setDisabled(true);
		this.alwDisburment.setDisabled(true);
		this.alwPayments.setDisabled(true);
		this.alwReceipts.setDisabled(true);
		this.modeDisbursment.setReadonly(true);
		this.modePayments.setReadonly(true);
		this.modeReceipts.setReadonly(true);
		this.fileName.setReadonly(true);

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

		this.partnerBankCode.setValue("");
		this.partnerBankName.setValue("");
		this.bankCode.setValue("");
		this.bankCode.setDescription("");
		this.bankBranchCode.setValue("");
		this.bankBranchCode.setDescription("");
		this.branchMICRCode.setValue("");
		this.branchIFSCCode.setValue("");
		this.branchCity.setValue("");
		this.utilityCode.setValue("");
		this.accountNo.setValue("");
		this.acType.setValue("");
		this.active.setChecked(false);
		this.modeDisbursment.setValue("");
		this.modeDisbursment.setTooltiptext("");
		this.modePayments.setValue("");
		this.modePayments.setTooltiptext("");
		this.modeReceipts.setValue("");
		this.modeReceipts.setTooltiptext("");
		this.alwDisburment.setChecked(false);
		this.alwReceipts.setChecked(false);
		this.alwPayments.setChecked(false);
		this.modeDisbursment.setValue("");
		this.modePayments.setValue("");
		this.modeReceipts.setValue("");
		this.sapGLCode.setValue("");
		this.profitCenterID.setValue("");
		this.costCenterID.setValue("");
		this.fileName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() {
		logger.debug("Entering");
		final PartnerBank aPartnerBank = new PartnerBank();
		BeanUtils.copyProperties(this.partnerBank, aPartnerBank);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the Academic object with the components data
		doWriteComponentsToBean(aPartnerBank);
	
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aPartnerBank.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPartnerBank.getRecordType())) {
				aPartnerBank.setVersion(aPartnerBank.getVersion() + 1);
				if (isNew) {
					aPartnerBank.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPartnerBank.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPartnerBank.setNewRecord(true);
				}
			}
		} else {
			aPartnerBank.setVersion(aPartnerBank.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aPartnerBank, tranType)) {
				//doWriteBeanToComponents(aPartnerBank);
				refreshList();
				closeDialog();
			}

		}  catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	
	private List<PartnerBankModes> preparePaymentModes(String disbmode,String purpose, List<PartnerBankModes> disbModeList) {
		logger.debug("Entering");
		
		String[] disbMode = disbmode.split(",");
		PartnerBankModes	paymentMode ;

		for (int i = 0; i < disbMode.length; i++) {
				paymentMode = new PartnerBankModes();
				paymentMode.setPurpose(purpose);
				paymentMode.setPaymentMode(disbMode[i]);
				disbModeList.add(paymentMode);
    		}

		logger.debug("Leaving");

		return disbModeList;
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

	private boolean doProcess(PartnerBank aPartnerBank, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aPartnerBank.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPartnerBank.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPartnerBank.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aPartnerBank.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPartnerBank.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPartnerBank);
				}

				if (isNotesMandatory(taskId, aPartnerBank)) {

					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aPartnerBank.setTaskId(taskId);
			aPartnerBank.setNextTaskId(nextTaskId);
			aPartnerBank.setRoleCode(getRole());
			aPartnerBank.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPartnerBank, tranType);
			String operationRefs = getServiceOperations(taskId, aPartnerBank);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPartnerBank, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPartnerBank, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		PartnerBank aPartnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = partnerBankService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = partnerBankService.saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = partnerBankService.doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aPartnerBank.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = partnerBankService.doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aPartnerBank.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_PartnerBankDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PartnerBankDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.partnerBank), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(PartnerBank aPartnerBank, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPartnerBank.getBefImage(), aPartnerBank);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aPartnerBank.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.partnerBank);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		partnerBankListCtrl.search();
	}
	

	private void setAccTypeModeDescription(List<PartnerBankModes> partnerBankModeList) {
		logger.debug("Entering");

		String disbMode = "";
		String payMode= "";
		String recptMode= "";

		for (PartnerBankModes partnerBankModes : partnerBankModeList) {
			if (StringUtils.equals(AccountConstants.PARTNERSBANK_DISB, partnerBankModes.getPurpose())) {
				if (partnerBankModes.getPaymentMode() != null) {

					disbMode = disbMode + partnerBankModes.getPaymentMode().concat(",");
				}

			}
			if (StringUtils.equals(AccountConstants.PARTNERSBANK_PAYMENT, partnerBankModes.getPurpose())) {
				if (partnerBankModes.getPaymentMode() != null) {
					payMode = payMode + partnerBankModes.getPaymentMode().concat(",");

				}
			}
			if (StringUtils.equals(AccountConstants.PARTNERSBANK_RECEIPTS, partnerBankModes.getPurpose())) {
				if (partnerBankModes.getPaymentMode() != null) {
					recptMode = recptMode + partnerBankModes.getPaymentMode().concat(",");
				}
			}
			
		}
			this.modeDisbursment.setValue(getFormattedPayMode(disbMode));
			this.modePayments.setValue(getFormattedPayMode(payMode));
			this.modeReceipts.setValue(getFormattedPayMode(recptMode));

		
		

		logger.debug("Leaving");

	}
	
	
	private void setAccCashModeDescription(List<PartnerBranchModes> partnerBranchModesList) {
		logger.debug("Entering");

		String disbMode = "";

		for (PartnerBranchModes partnerBranchModes : partnerBranchModesList) {
				if (partnerBranchModes.getBranchCode()!= null) {

					disbMode = disbMode + partnerBranchModes.getBranchCode().concat(",");
				}

			}
			
			this.alwBankBranchCode.setValue(getFormattedPayMode(disbMode));

		logger.debug("Leaving");

	}
	
	public String getFormattedPayMode(String paymode) {
		if (paymode!=null && paymode.length()>0 && paymode.charAt(paymode.length() - 1) == ',') {
			paymode = paymode.substring(0, paymode.length() - 1);
		}
		return paymode;
	}
	
	
	public void onClick$btnSearchModeDisbursment(Event event) {
		logger.debug("Entering  " + event.toString());
		doRemoveValidation();
		this.modeDisbursment.setErrorMessage("");
		Textbox txtbx = (Textbox) btnSearchModeDisbursment.getPreviousSibling();
		String selectedValues = (String) MultiSelectionStaticListBox.show(this.window_PartnerBankDialog,
				"AccountTypeDisbModes", txtbx.getValue());
		if (selectedValues != null) {
			txtbx.setValue(selectedValues);
		}
		logger.debug("Leaving  " + event.toString());
	}
	
	public void onClick$btnSearchModePayments(Event event) {
		logger.debug("Entering  " + event.toString());
		doRemoveValidation();
		this.modePayments.setErrorMessage("");
		Textbox txtbx = (Textbox) btnSearchModePayments.getPreviousSibling();
		String selectedValues = (String) MultiSelectionStaticListBox.show(this.window_PartnerBankDialog,
				"AccountTypePayModes", txtbx.getValue());
		if (selectedValues != null) {
			txtbx.setValue(selectedValues);
		}
		logger.debug("Leaving  " + event.toString());
	}
	
	public void onClick$btnSearchModeReceipts(Event event) {
		logger.debug("Entering  " + event.toString());
		this.modeReceipts.setErrorMessage("");
		doRemoveValidation();
		Textbox txtbx = (Textbox) btnSearchModeReceipts.getPreviousSibling();
		String selectedValues = (String) MultiSelectionStaticListBox.show(this.window_PartnerBankDialog,
				"AccountTypeRecptModes", txtbx.getValue());
		if (selectedValues != null) {
			txtbx.setValue(selectedValues);
			if (txtbx.getValue().contains(DisbursementConstants.PAYMENT_TYPE_CASH)) {
				this.AlwBranchCode.setVisible(true);
				this.space_AlwBankBranchCode.setSclass(PennantConstants.mandateSclass);
				this.alwBankBranchCode.setValue("");
				this.alwBankBranchCode.setReadonly(false);
				this.btnSearchBranchCode.setDisabled(isReadOnly("button_PartnerBankDialog_BranchCode"));
			} else {
				this.AlwBranchCode.setVisible(false);
				this.space_AlwBankBranchCode.setSclass("");
				this.alwBankBranchCode.setValue("");
				this.alwBankBranchCode.setReadonly(true);
				this.btnSearchBranchCode.setDisabled(true);
				
			}

		}
		logger.debug("Leaving  " + event.toString());
	}
	
	
	
	public void onClick$btnSearchBranchCode(Event event) {
		logger.debug("Entering  " + event.toString());
		this.alwBankBranchCode.setErrorMessage("");
		doRemoveValidation();
		Textbox txtbx = (Textbox) btnSearchBranchCode.getPreviousSibling();
		Object dataObject = MultiSelectionSearchListBox.show(this.window_PartnerBankDialog,"Branch",txtbx.getValue(), null);
		
		if (dataObject != null) {
			String details = (String) dataObject;
			txtbx.setValue(details);
		}
		
		logger.debug("Leaving  " + event.toString());
	}
	
	public void onCheck$reqFileDownload(Event event) {
		logger.debug("Entering");
		
		doRemoveValidation();
		
		if (this.reqFileDownload.isChecked()) {
			this.space_FileName.setSclass("");
			this.fileName.setErrorMessage("");
		} else {
			this.space_FileName.setSclass(PennantConstants.mandateSclass);
		}
		
		logger.debug("Leaving");
	}
	
	public void onCheck$alwDisburment(Event event) {
		logger.debug("Entering");
		
		onCheckDisburment();
		
		logger.debug("Leaving");
	}
	
	public void onCheck$alwPayments(Event event) {
		logger.debug("Entering");
		onCheckPayments();
		logger.debug("Leaving");
	}
	
	public void onCheck$alwReceipts(Event event) {
		logger.debug("Entering");
		onCheckReceipts();
		logger.debug("Leaving");
	}
	
	private void onCheckDisburment() {
		logger.debug("Entering");
		
		doRemoveValidation();

		if (this.alwDisburment.isChecked() || this.alwPayments.isChecked()) {
			this.reqFileDownload.setDisabled(false);
		} else {
			this.reqFileDownload.setDisabled(true);
			this.reqFileDownload.setChecked(false);
			this.space_FileName.setSclass("");
		}

		if (this.alwDisburment.isChecked()) {
			this.btnSearchModeDisbursment.setDisabled(false);
			this.modeDisbursment.setReadonly(true);
			this.modeDisbursment.setValue("");
			this.space_modeDisbursments.setSclass(PennantConstants.mandateSclass);
			
			if (this.reqFileDownload.isChecked()) {
				this.space_FileName.setSclass("");
				this.fileName.setErrorMessage("");
			} else {
				this.space_FileName.setSclass(PennantConstants.mandateSclass);
			}
		} else {
			this.btnSearchModeDisbursment.setDisabled(true);
			this.modeDisbursment.setReadonly(true);
			
			this.space_modeDisbursments.setSclass("");
			this.modeDisbursment.setValue("");
			
			this.fileName.setErrorMessage("");
		}
		
		logger.debug("Leaving");
	}

	private void onCheckPayments() {
		logger.debug("Entering");

		doRemoveValidation();

		if (this.alwPayments.isChecked()) {
			this.modePayments.setReadonly(true);
			this.btnSearchModePayments.setDisabled(false);
			this.modePayments.setValue("");
			this.space_modePayments.setSclass(PennantConstants.mandateSclass);
			this.reqFileDownload.setDisabled(false);
			this.space_FileName.setSclass(PennantConstants.mandateSclass);
			
			if (this.reqFileDownload.isChecked()) {
				this.space_FileName.setSclass("");
				this.fileName.setErrorMessage("");
			} else {
				this.space_FileName.setSclass(PennantConstants.mandateSclass);
			}

		} else {
			this.modePayments.setReadonly(true);
			this.btnSearchModePayments.setDisabled(true);
			this.space_modePayments.setSclass("");
			this.modePayments.setValue("");
			if(!this.alwDisburment.isChecked()){
				this.reqFileDownload.setDisabled(true);
				this.reqFileDownload.setChecked(false);
				this.space_FileName.setSclass("");
			}
			this.fileName.setErrorMessage("");

		}
		
		

		logger.debug("Leaving");
	}

	private void onCheckReceipts() {
		logger.debug("Entering");
		doRemoveValidation();
		if (this.alwPayments.isChecked() || this.alwDisburment.isChecked() ) {
			this.reqFileDownload.setDisabled(false);
		} else {
			this.reqFileDownload.setDisabled(true);
			this.reqFileDownload.setChecked(false);
			
		}
		if (this.alwReceipts.isChecked()) {
			this.modeReceipts.setReadonly(true);
			this.btnSearchModeReceipts.setDisabled(false);
			this.modeReceipts.setValue("");
			this.space_modeReceipts.setSclass(PennantConstants.mandateSclass);
			this.alwBankBranchCode.setValue("");
			this.AlwBranchCode.setVisible(false);
		} else {
			this.modeReceipts.setReadonly(true);
			this.btnSearchModeReceipts.setDisabled(true);
			this.space_modeReceipts.setSclass("");
			this.modeReceipts.setValue("");
			this.alwBankBranchCode.setValue("");
			this.AlwBranchCode.setVisible(false);

		}
		logger.debug("Leaving");
	}
	

	@Override
	protected String getReference() {
		return String.valueOf(this.partnerBank.getPartnerBankCode());
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}
}
