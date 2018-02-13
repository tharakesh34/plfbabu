/**
\ * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  FinCollateralDetailsDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.ChequeVerifyInterfaceService;
import com.pennant.Interface.service.DepositInterfaceService;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.constants.InterfaceConstants;
import com.pennant.coreinterface.model.chequeverification.ChequeStatus;
import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinCollateralDetailDialog.zul file.
 */
public class FinCollateralDetailDialogCtrl extends GFCBaseCtrl<FinCollaterals> {
	private static final long serialVersionUID = -6959194080451993569L;
	private static final Logger logger = Logger.getLogger(FinCollateralDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinCollateralDetailDialog; 

	protected Borderlayout borderlayoutFinCollateralDetail; 
	// Finance Collaterals Details Tab
	protected Label 			finType; 				
	protected Label 			finReference; 			
	protected Label 			custID; 				
	protected Label 			finAmount; 				
	protected Combobox 			collateralType; 		

	protected Groupbox 			gb_fdDetails; 			
	protected Textbox 			FDReference; 			
	protected Textbox 			FDCurrency; 			
	protected Decimalbox 		FDAmount;				
	protected Intbox 			FDTenor; 				
	protected Decimalbox 		FDRate; 				
	protected Datebox 			FDStartDate; 			
	protected Datebox 			FDMaturityDate; 		
	protected Textbox 			fdRemarks;

	protected Groupbox 			gb_pdcDetails;
	protected Textbox 			pdcReference;
	protected Textbox 			beneficiaryName;
	protected ExtendedCombobox 	bankName;
	protected Textbox 			firstChequeNbr;
	protected Textbox 			lastChequeNbr;
	protected Textbox 			pdcStatus;
	protected Textbox 			pdcRemarks;
	protected Button 			btnVerifyCheque;
	
	private String 				roleCode = "";
	private FinanceMain 		financeMain = null;
				

	private transient String oldVar_firstChequeNbr;
	private transient String oldVar_lastChequeNbr;

	protected List<ValueLabel> collateralList = PennantStaticListUtil.getCollateralTypes();
	private List<FinCollaterals> finCollateralList;

	private FinCollaterals finCollateral = null;
	private transient boolean validationOn;
	
	private boolean newRecord = false;
	private boolean newFinCollateral = false;
	private String moduleType = "";
	
	private FinCollateralHeaderDialogCtrl 	 finCollateralHeaderDialogCtrl;
	private DepositInterfaceService			 depositInterfaceService;
	private ChequeVerifyInterfaceService 	 chequeVerifyInterfaceService;


	/**
	 * default constructor.<br>
	 * o
	 */
	public FinCollateralDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCollateralDetailsDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinCollateralDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCollateralDetailDialog);

		try {
			if (arguments.containsKey("finCollateralDetail")) {
				this.finCollateral = (FinCollaterals) arguments
						.get("finCollateralDetail");
				setFinCollateral(finCollateral);
			}

			if (arguments.containsKey("financeMain")) {
				this.financeMain = (FinanceMain) arguments.get("financeMain");
			}
			

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getFinCollateral().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("finCollateralHeaderDialogCtrl")) {

				setFinCollateralHeaderDialogCtrl((FinCollateralHeaderDialogCtrl) arguments.get("finCollateralHeaderDialogCtrl"));
				setNewFinCollateral(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.finCollateral.setWorkflowId(0);
				if(arguments.containsKey("roleCode")){
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "FinCollateralDetailsDialog");
				}
			}

			doLoadWorkFlow(this.finCollateral.isWorkflow(),
					this.finCollateral.getWorkflowId(),
					this.finCollateral.getNextTaskId());

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"FinCollateralDetailsDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinCollateral());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinCollateralDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		this.bankName.setMaxlength(5);
		this.bankName.setMandatoryStyle(true);
		this.bankName.setModuleName("BankDetail");
		this.bankName.setValueColumn("BankCode");
		this.bankName.setDescColumn("BankName");
		this.bankName.setValidateColumns(new String[] { "BankCode" });

		this.beneficiaryName.setValue(SysParamUtil.getValueAsString("BANK_NAME"));
		this.fdRemarks.setMaxlength(500);
		this.pdcRemarks.setMaxlength(500);
		this.pdcStatus.setMaxlength(50);
		this.pdcStatus.setReadonly(true);
		this.FDReference.setMaxlength(50);
		this.pdcReference.setMaxlength(50);
		this.FDStartDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.FDMaturityDate.setFormat(DateFormat.LONG_DATE.getPattern());
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
		getUserWorkspace().allocateAuthorities("FinCollateralDetailsDialog",
				this.roleCode);
		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_FinCollateralDetailsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_FinCollateralDetailsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_FinCollateralDetailsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_FinCollateralDetailsDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_FinCollateralDetailDialog);
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
		
		doWriteBeanToComponents(this.finCollateral.getBefImage());
		doReadOnly();
		
		this.btnCtrl.setInitEdit();
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param finCollaterals
	 *            FinCollaterals
	 */
	public void doWriteBeanToComponents(FinCollaterals finCollaterals) {

		logger.debug("Entering");
		fillComboBox(this.collateralType, finCollaterals.getCollateralType(),
				collateralList, "");
		showDetails();
		
		this.FDCurrency.setValue(finCollaterals.getCcy());
		this.FDAmount.setValue(PennantAppUtil.formateAmount(finCollaterals
				.getCoverage(), CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
		this.FDTenor.setValue(finCollaterals.getTenor());
		this.FDRate.setValue(finCollaterals.getRate());
		this.FDStartDate.setValue(finCollaterals.getStartDate());
		this.FDMaturityDate.setValue(finCollaterals.getMaturityDate());

		this.beneficiaryName.setValue(SysParamUtil.getValueAsString("BANK_NAME"));
		this.bankName.setValue(finCollaterals.getBankName());
		this.firstChequeNbr.setValue(finCollaterals.getFirstChequeNo());
		this.lastChequeNbr.setValue(finCollaterals.getLastChequeNo());
		this.pdcStatus.setValue(finCollaterals.getStatus());

		if (StringUtils.equals(finCollaterals.getCollateralType(),
				FinanceConstants.COLLATERAL_FIXEDDEPOSIT)) {
			this.FDReference.setValue(finCollaterals.getReference());
			this.fdRemarks.setValue(finCollaterals.getRemarks());
		} else {
			this.pdcReference.setValue(finCollaterals.getReference());
			this.pdcRemarks.setValue(finCollaterals.getRemarks());
		}

		this.recordStatus.setValue(finCollaterals.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param finCollaterals
	 */
	public void doWriteComponentsToBean(FinCollaterals finCollaterals) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.collateralType.isDisabled() && "#".equals(getComboboxValue(this.collateralType))) {
				throw new WrongValueException(this.collateralType, Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_FinCollateralDetailDialog_CollateralType.value") }));
			}
			finCollaterals.setCollateralType(getComboboxValue(this.collateralType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (getComboboxValue(this.collateralType).equals(FinanceConstants.COLLATERAL_FIXEDDEPOSIT)) {
			try {
				finCollaterals.setReference(this.FDReference.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setCcy(this.FDCurrency.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setValue(PennantAppUtil.unFormateAmount(
						this.FDAmount.getValue(), CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setCoverage(PennantAppUtil.unFormateAmount(
						this.FDAmount.getValue(), CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setTenor(this.FDTenor.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setRate(this.FDRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setStartDate(this.FDStartDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setMaturityDate(this.FDMaturityDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				finCollaterals.setRemarks(this.fdRemarks.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else if (getComboboxValue(this.collateralType).equals(FinanceConstants.COLLATERAL_SECURITYCHEQUE)) {
			try {
				finCollaterals.setReference(this.pdcReference.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			/*try {
				finCollaterals.setReference((this.beneficiaryName.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}*/

			try {
				finCollaterals.setBankName(this.bankName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setFirstChequeNo(this.firstChequeNbr.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setLastChequeNo(this.lastChequeNbr.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setStatus(this.pdcStatus.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				finCollaterals.setRemarks(this.pdcRemarks.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
		}

		doRemoveValidation();
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinCollateral
	 * @throws Exception
	 */
	public void doShowDialog(FinCollaterals aFinCollateral) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.collateralType.focus();
		} else {

			if (isNewFinCollateral()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aFinCollateral);

			if (isNewFinCollateral()) {
				this.window_FinCollateralDetailDialog.setHeight("70%");
				this.window_FinCollateralDetailDialog.setWidth("70%");
				//this.groupboxWf.setVisible(false);
				this.window_FinCollateralDetailDialog.doModal();
			} else {
				this.window_FinCollateralDetailDialog.setWidth("100%");
				this.window_FinCollateralDetailDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinCollateralDetailDialog.onClose();
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

		doClearMessage();

		if (gb_fdDetails.isVisible() && !this.FDReference.isReadonly()) {
			this.FDReference.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCollateralDetailDialog_FDReference.value"), null, true));
			this.fdRemarks.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCollateralDetailDialog_Remarks.value"), null, true));
		}
		if (gb_pdcDetails.isVisible() && !this.pdcReference.isReadonly()) {
			this.pdcReference.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCollateralDetailDialog_PDCReference.value"), null, true));
			
			this.pdcRemarks.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCollateralDetailDialog_Remarks.value"), null, true));
			
			this.bankName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCollateralDetailDialog_BankName.value"), null, true));
			
			if(!this.pdcStatus.isReadonly()){
				this.pdcStatus.setConstraint(new PTStringValidator(Labels
						.getLabel("label_FinCollateralDetailDialog_pdcStatus.value"), null, true));
			}
			
			this.firstChequeNbr.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCollateralDetailDialog_FirstChequeNbr.value"), null, true));
			
			this.lastChequeNbr.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCollateralDetailDialog_LastChequeNbr.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * /** Disables the Validation by setting empty constraints.
	 */
	public void doRemoveValidation() {
		logger.debug("Entering");
		this.fdRemarks.setConstraint("");
		this.pdcRemarks.setConstraint("");
		this.pdcStatus.setConstraint("");
		this.firstChequeNbr.setConstraint("");
		this.lastChequeNbr.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages.
	 * */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.collateralType.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.FDReference.setErrorMessage("");
		this.pdcReference.setErrorMessage("");
		this.fdRemarks.setErrorMessage("");
		this.pdcRemarks.setErrorMessage("");
		this.pdcStatus.setErrorMessage("");
		this.firstChequeNbr.setErrorMessage("");
		this.lastChequeNbr.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FinCollaterals object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinCollaterals aFinCollaterals = new FinCollaterals();
		BeanUtils.copyProperties(getFinCollateral(), aFinCollaterals);
		String tranType = PennantConstants.TRAN_WF;

		String repayMthd = StringUtils.trimToEmpty(this.collateralType.getSelectedItem().getValue().toString());
		String fieldName = "";
		if (StringUtils.equals(FinanceConstants.COLLATERAL_FIXEDDEPOSIT, repayMthd)) {
			fieldName = Labels.getLabel("label_FinCollateralDetailDialog_FDReference.value");
		} else if (StringUtils.equals(FinanceConstants.COLLATERAL_SECURITYCHEQUE, repayMthd)) {
			fieldName = Labels.getLabel("label_FinCollateralDetailDialog_PDCReference.value");
		}
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				fieldName +" : "+aFinCollaterals.getReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinCollaterals.getRecordType())) {
				aFinCollaterals.setVersion(aFinCollaterals.getVersion() + 1);
				aFinCollaterals.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aFinCollaterals.setNewRecord(true);

				if (isWorkFlowEnabled()) {
					aFinCollaterals.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewFinCollateral()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newCollateralProcess(
							aFinCollaterals, tranType);
					auditHeader = ErrorControl.showErrorDetails(
							this.window_FinCollateralDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE
							|| retValue == PennantConstants.porcessOVERIDE) {
						getFinCollateralHeaderDialogCtrl()
								.doFillCollateralDetails(this.finCollateralList);
						closeDialog();
					}

				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			this.collateralType.setDisabled(isReadOnly("FinCollateralDetailsDialog_collateralType"));
			this.FDReference
			.setReadonly(isReadOnly("FinCollateralDetailsDialog_fdReference"));
			this.pdcReference
			.setReadonly(isReadOnly("FinCollateralDetailsDialog_pdcReference"));
			if (isNewFinCollateral()) {
				this.btnCancel.setVisible(false);
			}
		} else {
			this.collateralType.setDisabled(true);
			this.FDReference.setDisabled(true);
			this.pdcReference.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		this.collateralType
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_collateralType"));		
		this.FDCurrency.setReadonly(true);
		this.FDAmount.setReadonly(true);
		this.FDTenor.setReadonly(true);
		this.FDRate.setReadonly(true);
		this.FDStartDate.setReadonly(true);
		this.FDStartDate.setButtonVisible(false);
		this.FDMaturityDate.setReadonly(true);
		this.FDMaturityDate.setButtonVisible(false);
		this.fdRemarks
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_fdRemarks"));

		this.beneficiaryName
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_beneficiaryName"));
		this.bankName
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_bankName"));
		this.firstChequeNbr
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_firstChequeNbr"));
		this.lastChequeNbr
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_lastChequeNbr"));
		this.pdcStatus
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_pdcStatus"));
		this.pdcRemarks
				.setReadonly(isReadOnly("FinCollateralDetailsDialog_pdcRemarks"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.finCollateral.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {

			if (newFinCollateral) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newFinCollateral);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinCollateral()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

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

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinCollaterals aFinCollaterals = new FinCollaterals();
		BeanUtils.copyProperties(getFinCollateral(), aFinCollaterals);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinCollaterals object with the components data
		doWriteComponentsToBean(aFinCollaterals);

		// Write the additional validations as per below example
		// Do data level validations here
		
		// Validate cheque range
		doRevalidateChequeRange();
		
		isNew = aFinCollaterals.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinCollaterals.getRecordType())) {
				aFinCollaterals.setVersion(aFinCollaterals.getVersion() + 1);
				if (isNew) {
					aFinCollaterals
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinCollaterals.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinCollaterals.setNewRecord(true);
				}
			}
		} else {

			if (isNewFinCollateral()) {
				if (isNewRecord()) {
					aFinCollaterals.setVersion(1);
					aFinCollaterals.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFinCollaterals.getRecordType())) {
					aFinCollaterals.setVersion(aFinCollaterals.getVersion() + 1);
					aFinCollaterals.setRecordType(PennantConstants.RCD_UPD);
					aFinCollaterals.setNewRecord(true);
				}

				if (aFinCollaterals.getRecordType().equals(
						PennantConstants.RCD_ADD)
						&& isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFinCollaterals.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aFinCollaterals.setVersion(aFinCollaterals.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewFinCollateral()) {
				AuditHeader auditHeader = newCollateralProcess(aFinCollaterals,
						tranType);
				auditHeader = ErrorControl.showErrorDetails(
						this.window_FinCollateralDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE
						|| retValue == PennantConstants.porcessOVERIDE) {
					getFinCollateralHeaderDialogCtrl().doFillCollateralDetails(
							this.finCollateralList);
					closeDialog();
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for checking the cheque range data has been changed or not and Re-validate if required
	 * 
	 * @throws InterruptedException
	 */
	private void doRevalidateChequeRange() throws InterruptedException {
		logger.debug("Entering");
		
		doClearMessage();
		boolean dataChanged = false;
		if(!StringUtils.equals(this.firstChequeNbr.getValue(), this.oldVar_firstChequeNbr)) {
			dataChanged = true;
		}
		
		if(!StringUtils.equals(this.lastChequeNbr.getValue(), this.oldVar_lastChequeNbr)) {
			dataChanged = true;
		}
		if(dataChanged) {
			if (MessageUtil.confirm(Labels.getLabel("CHEQUE_REVALIDATE")) == MessageUtil.YES) {
				this.pdcStatus.setValue("");
				return;
			} else {
				this.firstChequeNbr.setValue(this.oldVar_firstChequeNbr);
				this.lastChequeNbr.setValue(this.oldVar_lastChequeNbr);
			}
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCollateralProcess(FinCollaterals aFinCollaterals,
			String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aFinCollaterals, tranType);
		finCollateralList = new ArrayList<FinCollaterals>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aFinCollaterals.getCollateralType();
		valueParm[1] = StringUtils.trimToEmpty(aFinCollaterals.getReference());
		errParm[0] = PennantJavaUtil.getLabel("label_FinCollateralType") + ":"
				+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FDReference") + ":"
				+ valueParm[1];
		

		if (getFinCollateralHeaderDialogCtrl().getFinCollateralDetailsList() != null
				&& getFinCollateralHeaderDialogCtrl()
						.getFinCollateralDetailsList().size() > 0) {
			for (int i = 0; i < getFinCollateralHeaderDialogCtrl()
					.getFinCollateralDetailsList().size(); i++) {
				FinCollaterals finCollateral = getFinCollateralHeaderDialogCtrl()
						.getFinCollateralDetailsList().get(i);

				if (StringUtils.equals(finCollateral.getReference(),
						aFinCollaterals.getReference())) { // Both Current and Existing list are having same record.

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(
								aFinCollaterals.getRecordType())) {
							aFinCollaterals
									.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finCollateralList.add(aFinCollaterals);
						} else if (PennantConstants.RCD_ADD.equals(
								aFinCollaterals.getRecordType())) {
							recordAdded=true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(
								aFinCollaterals.getRecordType())) {
							aFinCollaterals
									.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finCollateralList.add(aFinCollaterals);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(
								aFinCollaterals.getRecordType())) {
							recordAdded = true;
						}
					} else {
						if ( !PennantConstants.TRAN_UPD.equals(tranType)) {
							finCollateralList.add(finCollateral);
						}
					}
				} else {
					finCollateralList.add(finCollateral);
				}
			}
		}
		if (!recordAdded) {
			finCollateralList.add(aFinCollaterals);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinCollaterals
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinCollaterals aFinCollaterals,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aFinCollaterals.getBefImage(), aFinCollaterals);

		return new AuditHeader(getReference(), String.valueOf(aFinCollaterals
				.getCollateralSeq()), null, null, auditDetail,
				aFinCollaterals.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(
					this.window_FinCollateralDetailDialog, auditHeader);
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
		doShowNotes(this.finCollateral);
	}

	public void onSelect$collateralType(Event event) {
		logger.debug("Entering");
		showDetails();
		logger.debug("Leaving");
	}
	
	private void showDetails(){
		logger.debug("Entering");
		doClearMessage();
		gb_fdDetails.setVisible(false);
		this.gb_pdcDetails.setVisible(false);
		clearValues();
		if (this.collateralType.getSelectedIndex() != 0) {
			String repayMthd = StringUtils.trimToEmpty(this.collateralType
					.getSelectedItem().getValue().toString());
			if (repayMthd.equals(FinanceConstants.COLLATERAL_FIXEDDEPOSIT)) {
				this.gb_fdDetails.setVisible(true);
				this.gb_pdcDetails.setVisible(false);
			} else if (repayMthd.equals(FinanceConstants.COLLATERAL_SECURITYCHEQUE)) {
				this.gb_fdDetails.setVisible(false);
				this.gb_pdcDetails.setVisible(true);
				this.pdcStatus.setReadonly(true);
			}
		}
		logger.debug("Leaving");
	}

	private void clearValues() {
		this.FDReference.setValue("");
		this.pdcReference.setValue("");
		this.FDCurrency.setValue("");
		this.FDAmount.setValue(BigDecimal.ZERO);
		this.FDTenor.setText("0");
		this.FDRate.setValue(BigDecimal.ZERO);
		this.FDStartDate.setText("");
		this.FDMaturityDate.setText("");
		this.beneficiaryName.setValue("");
		this.bankName.setValue("");
		this.firstChequeNbr.setValue("");
		this.lastChequeNbr.setValue("");
		this.pdcStatus.setValue("");
		this.fdRemarks.setValue("");
		this.pdcRemarks.setValue("");
	}

	public void onChange$FDReference(Event event) throws InterruptedException, WrongValueException, 
														InterfaceException, ParseException {
		logger.debug("Entering" + event.toString());

		FinCollaterals finCollaterals = null;
		
		// Fetch Deposit Details from T24 interface
		try {
			if (!StringUtils.isBlank(this.FDReference.getValue())) {
				finCollaterals = getDepositInterfaceService().fetchDepositDetails(this.FDReference.getValue());
			}

			if (finCollaterals != null) {
				this.FDReference.clearErrorMessage();
				this.FDCurrency.setValue(finCollaterals.getCcy());
				this.FDAmount.setValue(PennantAppUtil.formateAmount(finCollaterals.getValue(), 
						CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
				this.FDTenor.setValue(finCollaterals.getTenor());
				this.FDRate.setValue(finCollaterals.getRate());
				this.FDStartDate.setValue(finCollaterals.getStartDate());
				this.FDMaturityDate.setValue(finCollaterals.getMaturityDate());
			} else {
				clearDepositDetails();
			}
		} catch (InterfaceException e) {
			clearDepositDetails();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}
	
	private void clearDepositDetails() {
		this.FDCurrency.setValue("");
		this.FDAmount.setText("");
		this.FDTenor.setText("");
		this.FDRate.setText("");
		this.FDStartDate.setText("");
		this.FDMaturityDate.setText("");
	}

	/**
	 * Event for sending cheque verification request to middleware
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws WrongValueException
	 * @throws InterfaceException
	 * @throws ParseException
	 */
	public void onClick$btnVerifyCheque(Event event) throws InterruptedException, WrongValueException, 
			InterfaceException, ParseException {
		logger.debug("Entering" + event.toString());

		// Validate the mandatory fields
		if (StringUtils.isBlank(this.firstChequeNbr.getValue())) {
			throw new WrongValueException(this.firstChequeNbr, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_FinCollateralDetailDialog_FirstChequeNbr.value") }));
		}
		if (StringUtils.isBlank(this.lastChequeNbr.getValue())) {
			throw new WrongValueException(this.lastChequeNbr, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_FinCollateralDetailDialog_LastChequeNbr.value") }));
		}

		// Verify Customer cheque details by sending request to middleware
		try {
			ChequeVerification chequeVerifyResponse = doChequeVerification();

			if (chequeVerifyResponse != null) {
				if (StringUtils.equals(chequeVerifyResponse.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
					
					this.pdcStatus.setValue("Valid range of cheques");
					this.oldVar_firstChequeNbr = this.firstChequeNbr.getValue();
					this.oldVar_lastChequeNbr = this.lastChequeNbr.getValue();
					
				} else {
					if (chequeVerifyResponse.getChequeStsList() != null) {
						
						StringBuilder chkStatus = new StringBuilder();
						
						for (ChequeStatus chequeStatus : chequeVerifyResponse.getChequeStsList()) {
							if (!StringUtils.isBlank(chkStatus.toString())) {
								chkStatus.append(PennantConstants.DELIMITER_COMMA);
							}
							
							chkStatus.append(chequeStatus.getChequeNo());
							chkStatus.append("-");
							chkStatus.append(chequeStatus.getValidity());
						}
						
						// setting response status into status field
						MessageUtil.showError(chkStatus.toString());
						return;
					} else {
						throw new InterfaceException(chequeVerifyResponse.getReturnCode(),
								chequeVerifyResponse.getReturnText());
					}
				}
			} else {
				throw new InterfaceException("PTI3001",	Labels.getLabel("FAILED_CHEQUE_VERIFICATION"));
			}
		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for prepare capture cheque details and send to middleware to validate
	 * 
	 * @return ChequeVerification
	 * @throws InterfaceException
	 */
	private ChequeVerification doChequeVerification() throws InterfaceException {
		logger.debug("Entering");

		CustomerDetails customerDetails = getFinCollateralHeaderDialogCtrl().getFinanceDetail().getCustomerDetails();
		String custBranchCode = customerDetails.getCustomer().getCustDftBranch();

		ChequeVerification chequeVerification = new ChequeVerification();
		chequeVerification.setCustCIF(customerDetails.getCustomer().getCustCIF());
		chequeVerification.setFinanceRef(getFinanceMain().getFinReference());
		chequeVerification.setChequeRangeFrom(this.firstChequeNbr.getValue());
		chequeVerification.setChequeRangeTo(this.lastChequeNbr.getValue());

		String remarks = this.pdcRemarks.getValue();
		if(!StringUtils.isBlank(remarks) && remarks.length() > 50) {
			remarks = remarks.substring(0, 49);
		}
		chequeVerification.setRemarks(remarks);
		chequeVerification.setBranchCode(custBranchCode);

		// Send Cheque verification request to middleware
		ChequeVerification chequeVerifyResponse = getChequeVerifyInterfaceService().verifySecurityCheque(chequeVerification);
		
		logger.debug("Leaving");
		return chequeVerifyResponse;
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getFinCollateral().getCollateralSeq()
				+ PennantConstants.KEY_SEPERATOR
				+ getFinCollateral().getFinReference();
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewFinCollateral() {
		return newFinCollateral;
	}

	public void setNewFinCollateral(boolean newFinCollateral) {
		this.newFinCollateral = newFinCollateral;
	}

	public FinCollaterals getFinCollateral() {
		return finCollateral;
	}

	public void setFinCollateral(FinCollaterals finCollateral) {
		this.finCollateral = finCollateral;
	}

	public FinCollateralHeaderDialogCtrl getFinCollateralHeaderDialogCtrl() {
		return finCollateralHeaderDialogCtrl;
	}

	public void setFinCollateralHeaderDialogCtrl(
			FinCollateralHeaderDialogCtrl finCollateralHeaderDialogCtrl) {
		this.finCollateralHeaderDialogCtrl = finCollateralHeaderDialogCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}
	
	public DepositInterfaceService getDepositInterfaceService() {
		return depositInterfaceService;
	}

	public void setDepositInterfaceService(
			DepositInterfaceService depositInterfaceService) {
		this.depositInterfaceService = depositInterfaceService;
	}
	public ChequeVerifyInterfaceService getChequeVerifyInterfaceService() {
		return chequeVerifyInterfaceService;
	}

	public void setChequeVerifyInterfaceService(
			ChequeVerifyInterfaceService chequeVerifyInterfaceService) {
		this.chequeVerifyInterfaceService = chequeVerifyInterfaceService;
	}

}
