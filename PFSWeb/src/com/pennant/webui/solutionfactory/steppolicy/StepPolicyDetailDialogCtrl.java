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
 * FileName    		:  StepPolicyDetailDialogCtrl.java                                                   * 	  
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

import java.io.Serializable;
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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/StepPolicy/StepPolicyDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class StepPolicyDetailDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(StepPolicyDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
	protected Label recordStatus;            // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	// not auto wired vars
	private StepPolicyDetail stepPolicyDetail; // overhanded per param
	private transient StepPolicyDetailDialogCtrl stepPolicyDetailDialogCtrl; // overhanded per
	// param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.

	private transient int oldVar_stepNumber;
	private transient BigDecimal oldVar_tenorSplitPerc;
	private transient BigDecimal oldVar_rateMargin;
	private transient BigDecimal oldVar_emiSplitPerc;


	private transient String oldVar_recordStatus;
	private transient boolean validationOn;
	private boolean notes_Entered = false;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_StepPolicyDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;      // autowire
	protected Button btnEdit;     // autowire
	protected Button btnDelete;   // autowire
	protected Button btnSave;     // autowire
	protected Button btnCancel;   // autowire
	protected Button btnClose;    // autowire
	protected Button btnHelp;     // autowire
	protected Button btnNotes;    // autowire
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected stepPolicyDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_StepPolicyDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */

			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("stepPolicyDetail")) {
				this.stepPolicyDetail = (StepPolicyDetail) args.get("stepPolicyDetail");
				StepPolicyDetail befImage = new StepPolicyDetail();
				BeanUtils.copyProperties(this.stepPolicyDetail, befImage);
				this.stepPolicyDetail.setBefImage(befImage);
				setStepPolicyDetail(this.stepPolicyDetail);
			} else {
				setStepPolicyDetail(null);
			}
			if (args.containsKey("policyDesc")) {
				this.policyDesc.setValue(args.get("policyDesc").toString());
			}
			if(args.containsKey("totTenorPerc")){
				this.totTenorPerc = (BigDecimal) args.get("totTenorPerc");
			}
			this.stepPolicyDetail.setWorkflowId(0);
			doLoadWorkFlow(this.stepPolicyDetail.isWorkflow(), this.stepPolicyDetail.getWorkflowId(), this.stepPolicyDetail.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "StepPolicyDetailDialog");
			}
			if (args.containsKey("role")) {
				userRole=args.get("role").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "StepPolicyDetailDialog");
			}
			doCheckRights();
			if (args.containsKey("stepPolicyDialogCtrl")) {
				setStepPolicyDialogCtrl((StepPolicyDialogCtrl) args.get("stepPolicyDialogCtrl"));
			} else {
				setStepPolicyDialogCtrl(null);
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
					.getValue().intValue()- PennantConstants.borderlayoutMainNorth;

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getStepPolicyDetail());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_StepPolicyDetailDialog.onClose();
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
		getUserWorkspace().alocateAuthorities("StepPolicyDetailDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDetailDialog_btnSave"));
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
	public void onClose$window_StepPolicyDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
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
		PTMessageUtils.showHelpWindow(event, window_StepPolicyDetailDialog);
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}


	// GUI Process
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
			logger.debug("isDataChanged : true");
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);
			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}
		if (close) {
			closePopUpWindow(this.window_StepPolicyDetailDialog,"StepPolicyDetailDialog");
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
			aStepPolicyDetail.setStepNumber(this.stepNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.tenorSplitPerc.getValue().compareTo(BigDecimal.ZERO) != 1){
				throw new WrongValueException(this.tenorSplitPerc, Labels.getLabel("FIELD_NO_NEGATIVE",
						new String[] { Labels.getLabel("label_TenorSplitPerc") }));
			}
			else if((this.totTenorPerc.add(this.tenorSplitPerc.getValue())).compareTo(new BigDecimal(100)) > 0){
				BigDecimal availTenorPerc = new BigDecimal(100).subtract(this.totTenorPerc);
				throw new WrongValueException(this.tenorSplitPerc, Labels.getLabel("Total_Percentage",
						new String[] { Labels.getLabel("label_TenorSplitPerc"),availTenorPerc.toString() }));
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
						new String[] { Labels.getLabel("label_EMISplitPerc") }));
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(StepPolicyDetail aStepPolicyDetail) throws InterruptedException {
		logger.debug("Entering");
		if (aStepPolicyDetail == null) {
			aStepPolicyDetail = null;
			setStepPolicyDetail(aStepPolicyDetail);
		} else {
			setStepPolicyDetail(aStepPolicyDetail);
		}
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
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_StepPolicyDetailDialog.setWidth("90%");
			this.window_StepPolicyDetailDialog.setHeight("50%");
			this.window_StepPolicyDetailDialog.doModal();
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
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_stepNumber = this.stepNumber.intValue();
		this.oldVar_tenorSplitPerc = this.tenorSplitPerc.getValue();
		this.oldVar_rateMargin = this.rateMargin.getValue();
		this.oldVar_emiSplitPerc = this.emiSplitPerc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.stepNumber.setValue(this.oldVar_stepNumber);
		this.tenorSplitPerc.setValue(this.oldVar_tenorSplitPerc);
		this.rateMargin.setValue(this.oldVar_rateMargin);
		this.emiSplitPerc.setValue(this.oldVar_emiSplitPerc);
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
		logger.debug("Entering");
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_stepNumber != this.stepNumber.intValue()) {
			return true;
		}
		if (this.oldVar_tenorSplitPerc != this.tenorSplitPerc.getValue()) {
			return true;
		}
		if (this.oldVar_rateMargin != this.rateMargin.getValue()) {
			return true;
		}
		if (this.oldVar_emiSplitPerc != this.emiSplitPerc.getValue()) {
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aStepPolicyDetail.getStepNumber();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aStepPolicyDetail.getRecordType()).equals("")) {
				aStepPolicyDetail.setVersion(aStepPolicyDetail.getVersion() + 1);
				aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aStepPolicyDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aStepPolicyDetail.getRecordType()).equals(PennantConstants.RCD_UPD)) {
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
					closePopUpWindow(this.window_StepPolicyDetailDialog,"StepPolicyDetailDialog");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
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
		// remember the old vars
		doStoreInitValues();
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
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aStepPolicyDetail.getRecordType()).equals("")) {
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
			if (StringUtils.trimToEmpty(aStepPolicyDetail.getRecordType()).equals("")) {
				aStepPolicyDetail.setVersion(aStepPolicyDetail.getVersion() + 1);
				aStepPolicyDetail.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aStepPolicyDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
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
				closePopUpWindow(this.window_StepPolicyDetailDialog,"StepPolicyDetailDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
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
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							stepPolicyDetailList.add(aStepPolicyDetail);
						} else if (aStepPolicyDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							stepPolicyDetailList.add(aStepPolicyDetail);
						} else if (aStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							List<StepPolicyDetail> savedList = getStepPolicyDialogCtrl().getStepPolicyHeader().getStepPolicyDetails();
							for (int j = 0; j < savedList.size(); j++) {
								StepPolicyDetail accType = savedList.get(j);
								if (accType.getPolicyCode().equals(aStepPolicyDetail.getPolicyCode())) {
									stepPolicyDetailList.add(accType);
								}
							}
						} else if (aStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aStepPolicyDetail.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
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



	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_StepPolicyDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("StepPolicyDetail");
		notes.setReference(getStepPolicyDetail().getPolicyCode());
		notes.setVersion(getStepPolicyDetail().getVersion());
		return notes;
	}

	private void doClearMessage() {
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
