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
 * FileName    		:  StepPolicyDetailDialogCtrl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.solutionfactory.steppolicy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/StepPolicy/StepPolicyDetailDialog.zul file.
 */
public class StepPolicyDetailDialogCtrl extends GFCBaseCtrl<StepPolicyDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(StepPolicyDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_StepPolicyDetailDialog; // autowired

	protected Textbox policyCode;            // autowired
	protected Textbox policyDesc;            // autowired
	//	protected Intbox noOfSteps;              // autowired
	protected Intbox stepNumber;             // autowired
	protected Decimalbox tenorSplitPerc;     // autowired
	protected Decimalbox rateMargin;         // autowired
	protected Decimalbox emiSplitPerc;       // autowired
	protected Button btnSearchAccountTypes;  // autowired
	
	// not auto wired vars
	private StepPolicyDetail stepPolicyDetail; // overhanded per param
	private transient StepPolicyDetailDialogCtrl stepPolicyDetailDialogCtrl; // overhanded per
	// param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	private String userRole="";
	private StepPolicyDialogCtrl stepPolicyDialogCtrl;
	private List<StepPolicyDetail> stepPolicyDetailList;
	protected Map<String, Object> accounTypesDataMap = new HashMap<String, Object>();
	private BigDecimal totTenorPerc = BigDecimal.ZERO;
	/**
	 * default constructor.<br>
	 */
	public StepPolicyDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "StepPolicyDetailDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected stepPolicyDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_StepPolicyDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_StepPolicyDetailDialog);

		try {
			if (arguments.containsKey("stepPolicyDetail")) {
				this.stepPolicyDetail = (StepPolicyDetail) arguments.get("stepPolicyDetail");
				StepPolicyDetail befImage = new StepPolicyDetail();
				BeanUtils.copyProperties(this.stepPolicyDetail, befImage);
				this.stepPolicyDetail.setBefImage(befImage);
				setStepPolicyDetail(this.stepPolicyDetail);
			} else {
				setStepPolicyDetail(null);
			}
			if (arguments.containsKey("policyDesc")) {
				this.policyDesc.setValue(arguments.get("policyDesc").toString());
			}
			if(arguments.containsKey("totTenorPerc")){
				this.totTenorPerc = (BigDecimal) arguments.get("totTenorPerc");
			}
			this.stepPolicyDetail.setWorkflowId(0);
			doLoadWorkFlow(this.stepPolicyDetail.isWorkflow(), this.stepPolicyDetail.getWorkflowId(), this.stepPolicyDetail.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "StepPolicyDetailDialog");
			}
			if (arguments.containsKey("role")) {
				userRole=arguments.get("role").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "StepPolicyDetailDialog");
			}
			doCheckRights();
			if (arguments.containsKey("stepPolicyDialogCtrl")) {
				setStepPolicyDialogCtrl((StepPolicyDialogCtrl) arguments.get("stepPolicyDialogCtrl"));
			} else {
				setStepPolicyDialogCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getStepPolicyDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_StepPolicyDetailDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.stepNumber.setMaxlength(2);
		this.tenorSplitPerc.setMaxlength(6);
		this.emiSplitPerc.setMaxlength(6);

		this.rateMargin.setMaxlength(13);
		this.rateMargin.setFormat(PennantApplicationUtil.getRateFormate(9));
		this.rateMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rateMargin.setScale(9);

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
		getUserWorkspace().allocateAuthorities("StepPolicyDetailDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnSave"));
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
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_StepPolicyDetailDialog);
		logger.debug("Leaving");
	}


	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
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
		doWriteBeanToComponents(this.stepPolicyDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aStepPolicyDetail
	 *            StepPolicyDetail
	 */
	public void doWriteBeanToComponents(StepPolicyDetail aStepPolicyDetail) {
		logger.debug("Entering");
		this.policyCode.setValue(aStepPolicyDetail.getPolicyCode());
		this.stepNumber.setValue(aStepPolicyDetail.getStepNumber()); 
		this.tenorSplitPerc.setValue(aStepPolicyDetail.getTenorSplitPerc()); 
		this.rateMargin.setValue(aStepPolicyDetail.getRateMargin()); 
		this.emiSplitPerc.setValue(aStepPolicyDetail.getEmiSplitPerc()); 

		this.recordStatus.setValue(aStepPolicyDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aStepPolicyDetail
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(StepPolicyDetail aStepPolicyDetail) throws InterruptedException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(this.stepNumber.intValue() == 0){
				throw new WrongValueException(this.stepNumber, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_StepPolicyDetailDialog_StepNumber.value") }));
			}
			if(this.stepNumber.getValue() < 0){
				throw new WrongValueException(this.stepNumber, Labels.getLabel("FIELD_NO_NEGATIVE",
						new String[] { Labels.getLabel("label_StepPolicyDetailDialog_StepNumber.value") }));
			}
			aStepPolicyDetail.setStepNumber(this.stepNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.tenorSplitPerc.intValue() == 0){
				throw new WrongValueException(this.tenorSplitPerc, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_StepPolicyDetailDialog_TenorSplitPerc.value"), "1" }));
			}
			if(this.tenorSplitPerc.getValue().compareTo(BigDecimal.ZERO) != 1){
				throw new WrongValueException(this.tenorSplitPerc, Labels.getLabel("FIELD_NO_NEGATIVE",
						new String[] { Labels.getLabel("label_StepPolicyDetailDialog_TenorSplitPerc.value") }));
			}else if((this.totTenorPerc.add(this.tenorSplitPerc.getValue())).compareTo(new BigDecimal(100)) > 0){
				BigDecimal availTenorPerc = new BigDecimal(100).subtract(this.totTenorPerc);
				throw new WrongValueException(this.tenorSplitPerc, Labels.getLabel("Total_Percentage",
						new String[] { Labels.getLabel("label_StepPolicyDetailDialog_TenorSplitPerc.value"),availTenorPerc.toString() }));
			}

			aStepPolicyDetail.setTenorSplitPerc(new BigDecimal(PennantApplicationUtil.formatRate(this.tenorSplitPerc.getValue().doubleValue(),2)));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aStepPolicyDetail.setRateMargin(this.rateMargin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.emiSplitPerc.intValue() == 0){
				throw new WrongValueException(this.emiSplitPerc, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_StepPolicyDetailDialog_EMISplitPerc.value") }));
			}
			if(this.emiSplitPerc.getValue().compareTo(BigDecimal.ZERO) != 1){
				throw new WrongValueException(this.emiSplitPerc, Labels.getLabel("FIELD_NO_NEGATIVE",
						new String[] { Labels.getLabel("label_StepPolicyDetailDialog_EMISplitPerc.value") }));
			}
			aStepPolicyDetail.setEmiSplitPerc(new BigDecimal(PennantApplicationUtil.formatRate(this.emiSplitPerc.getValue().doubleValue(),2)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aStepPolicyDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aStepPolicyDetail
	 * @throws Exception
	 */
	public void doShowDialog(StepPolicyDetail aStepPolicyDetail) throws Exception {
		logger.debug("Entering");
		if (aStepPolicyDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.stepNumber.focus();
		} else {
			this.tenorSplitPerc.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aStepPolicyDetail);

			this.window_StepPolicyDetailDialog.setWidth("90%");
			this.window_StepPolicyDetailDialog.setHeight("25%");
			this.window_StepPolicyDetailDialog.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_StepPolicyDetailDialog.onClose();
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

		if (!this.rateMargin.isDisabled()) {
			this.rateMargin.setConstraint(new PTDecimalValidator(Labels.getLabel("label_StepPolicyDetailDialog_RateMargin.value"),9,false,true,-9999,9999));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.stepNumber.setConstraint("");
		this.rateMargin.setConstraint("");
		this.emiSplitPerc.setConstraint("");
		this.tenorSplitPerc.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations
	
	/**
	 * Deletes a StepPolicyDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final StepPolicyDetail aStepPolicyDetail = new StepPolicyDetail();
		BeanUtils.copyProperties(getStepPolicyDetail(), aStepPolicyDetail);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_StepPolicyDetailDialog_StepNumber.value")+" : "+aStepPolicyDetail.getStepNumber();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aStepPolicyDetail.getRecordType())) {
				aStepPolicyDetail.setVersion(aStepPolicyDetail.getVersion() + 1);
				aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aStepPolicyDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (PennantConstants.RCD_UPD.equals(aStepPolicyDetail.getRecordType())) {
				aStepPolicyDetail.setVersion(aStepPolicyDetail.getVersion() + 1);
				aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newCustAccTypesProcess(aStepPolicyDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_StepPolicyDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getStepPolicyDialogCtrl().doFillStepPolicyDetails(this.stepPolicyDetailList);
					closeDialog();
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

		if (getStepPolicyDetail().isNewRecord()) {
			this.stepNumber.setReadonly(false);
		} else {
			this.stepNumber.setReadonly(true);
		}
		this.tenorSplitPerc.setDisabled(isReadOnly("StepPolicyDetailDialog_tenorSplitPerc"));
		this.rateMargin.setDisabled(isReadOnly("StepPolicyDetailDialog_rateMargin"));
		this.emiSplitPerc.setDisabled(isReadOnly("StepPolicyDetailDialog_emiSplitPerc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.stepPolicyDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		
		logger.debug("Leaving");
	}
	public boolean isReadOnly(String componentName){
		return getUserWorkspace().isReadOnly(componentName);
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.policyCode.setReadonly(true);
		this.policyDesc.setReadonly(true);
		//this.noOfSteps.setDisabled(true);
		this.stepNumber.setReadonly(true);
		this.tenorSplitPerc.setDisabled(true);
		this.rateMargin.setDisabled(true);
		this.emiSplitPerc.setDisabled(true);

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
		this.policyCode.setValue("");
		this.policyDesc.setValue("");
		//	this.noOfSteps.setValue(0);
		this.stepNumber.setValue(0);
		this.tenorSplitPerc.setValue("");
		this.rateMargin.setValue("");
		this.emiSplitPerc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final StepPolicyDetail aStepPolicyDetail = new StepPolicyDetail();
		BeanUtils.copyProperties(getStepPolicyDetail(), aStepPolicyDetail);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the StepPolicyDetail object with the components data
		doWriteComponentsToBean(aStepPolicyDetail);

		// Write the additional validations as per below example
		// get the selected StepPolicyDetail object from the listbox
		// Do data level validations here
		isNew = aStepPolicyDetail.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aStepPolicyDetail.getRecordType())) {
				aStepPolicyDetail.setVersion(aStepPolicyDetail.getVersion() + 1);
				if (isNew) {
					aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aStepPolicyDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aStepPolicyDetail.setVersion(1);
				aStepPolicyDetail.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aStepPolicyDetail.getRecordType())) {
				aStepPolicyDetail.setVersion(aStepPolicyDetail.getVersion() + 1);
				aStepPolicyDetail.setRecordType(PennantConstants.RCD_UPD);
			}
			if (PennantConstants.RCD_ADD.equals(aStepPolicyDetail.getRecordType()) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_NEW.equals(aStepPolicyDetail.getRecordType())) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			AuditHeader auditHeader = newCustAccTypesProcess(aStepPolicyDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_StepPolicyDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getStepPolicyDialogCtrl().doFillStepPolicyDetails(stepPolicyDetailList);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method validates  StepPolicyDetail details <br>
	 * and will return AuditHeader
	 *
	 */
	private AuditHeader newCustAccTypesProcess(StepPolicyDetail aStepPolicyDetail, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aStepPolicyDetail, tranType);
		stepPolicyDetailList = new ArrayList<StepPolicyDetail>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aStepPolicyDetail.getPolicyCode();
		valueParm[1] = String.valueOf(aStepPolicyDetail.getStepNumber());
		errParm[0] = PennantJavaUtil.getLabel("label_StepPolicyDetailDialog_PolicyCode.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_StepPolicyDetailDialog_StepNumber.value") + ":" + valueParm[1];
		List<StepPolicyDetail> list = getStepPolicyDialogCtrl().getStepPolicyDetailList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				StepPolicyDetail stepPolicyDetail = list.get(i);
				if (stepPolicyDetail.getPolicyCode().equals(aStepPolicyDetail.getPolicyCode()) && stepPolicyDetail.getStepNumber() == aStepPolicyDetail.getStepNumber()) {
					// Both Current and Existing list rating same
					if (aStepPolicyDetail.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aStepPolicyDetail.getRecordType())) {
							aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							stepPolicyDetailList.add(aStepPolicyDetail);
						} else if (PennantConstants.RCD_ADD.equals(aStepPolicyDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aStepPolicyDetail.getRecordType())) {
							aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							stepPolicyDetailList.add(aStepPolicyDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aStepPolicyDetail.getRecordType())) {
							recordAdded = true;
							List<StepPolicyDetail> savedList = getStepPolicyDialogCtrl().getStepPolicyHeader().getStepPolicyDetails();
							for (int j = 0; j < savedList.size(); j++) {
								StepPolicyDetail accType = savedList.get(j);
								if (accType.getPolicyCode().equals(aStepPolicyDetail.getPolicyCode())) {
									stepPolicyDetailList.add(accType);
								}
							}
						} else if (PennantConstants.RECORD_TYPE_DEL.equals(aStepPolicyDetail.getRecordType())) {
							aStepPolicyDetail.setNewRecord(true);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							stepPolicyDetailList.add(stepPolicyDetail);
						}
					}
				} else {
					stepPolicyDetailList.add(stepPolicyDetail);
				}
			}
		}
		if (!recordAdded) {
			stepPolicyDetailList.add(aStepPolicyDetail);
		}
		logger.debug("Leaving");
		return auditHeader;
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

	private AuditHeader getAuditHeader(StepPolicyDetail aStepPolicyDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aStepPolicyDetail.getBefImage(), aStepPolicyDetail);
		return new AuditHeader(aStepPolicyDetail.getPolicyCode(), null, null, null, auditDetail, aStepPolicyDetail.getUserDetails(), getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_StepPolicyDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.stepPolicyDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.stepPolicyDetail.getPolicyCode());
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.stepNumber.setErrorMessage("");
		this.emiSplitPerc.setErrorMessage("");
		this.tenorSplitPerc.setErrorMessage("");
		this.rateMargin.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public StepPolicyDetailDialogCtrl getStepPolicyDetailDialogCtrl() {
		return stepPolicyDetailDialogCtrl;
	}

	public void setStepPolicyDetailDialogCtrl(
			StepPolicyDetailDialogCtrl stepPolicyDetailDialogCtrl) {
		this.stepPolicyDetailDialogCtrl = stepPolicyDetailDialogCtrl;
	}

	public StepPolicyDialogCtrl getStepPolicyDialogCtrl() {
		return stepPolicyDialogCtrl;
	}

	public void setStepPolicyDialogCtrl(StepPolicyDialogCtrl stepPolicyDialogCtrl) {
		this.stepPolicyDialogCtrl = stepPolicyDialogCtrl;
	}

	public StepPolicyDetail getStepPolicyDetail() {
		return stepPolicyDetail;
	}

	public void setStepPolicyDetail(StepPolicyDetail stepPolicyDetail) {
		this.stepPolicyDetail = stepPolicyDetail;
	}


}
