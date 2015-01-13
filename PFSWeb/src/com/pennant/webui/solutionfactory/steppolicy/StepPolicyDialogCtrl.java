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
 * FileName    		:  StepPolicyDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/StepPolicyHeader/stepPolicyDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class StepPolicyDialogCtrl extends GFCBaseListCtrl<StepPolicyDetail> implements Serializable {

	private static final long serialVersionUID = 8602015982512929710L;
	private final static Logger logger = Logger.getLogger(StepPolicyDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_StepPolicyDialog;      // autowired

	protected Uppercasebox 	policyCode; 				  // autowired
	protected Textbox 		policyDesc; 			      // autowired
	protected Intbox 		noOfSteps; 		              // autowired

	protected Label 		recordStatus; 				  // autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Grid 			grid_Basicdetails;			  // autoWired

	protected Listbox 		listBoxStepPolicyDetail;

	// not auto wired vars
	private StepPolicyHeader stepPolicyHeader; // overhanded per param
	private transient StepPolicyListCtrl stepPolicyListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_policyCode;
	private transient String oldVar_policyDesc;
	private transient int oldVar_noOfSteps;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_StepPolicyDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	protected Button button_StepPolicyDialog_btnNew_StepPolicyDetail; // autowired


	// ServiceDAOs / Domain Classes
	private transient StepPolicyService stepPolicyService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private List<StepPolicyDetail> stepPolicyDetailList = new ArrayList<StepPolicyDetail>();
	private List<StepPolicyDetail> oldVar_stepPolicyDetailList = new ArrayList<StepPolicyDetail>();
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public StepPolicyDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected StepPolicyHeader object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_StepPolicyDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit,this.btnDelete, this.btnSave, this.btnCancel, 
				this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("stepPolicyHeader")) {
			this.stepPolicyHeader = (StepPolicyHeader) args.get("stepPolicyHeader");
			StepPolicyHeader befImage = new StepPolicyHeader();
			BeanUtils.copyProperties(this.stepPolicyHeader, befImage);
			this.stepPolicyHeader.setBefImage(befImage);
			setStepPolicyHeader(this.stepPolicyHeader);
		} else {
			setStepPolicyHeader(null);
		}

		doLoadWorkFlow(this.stepPolicyHeader.isWorkflow(), 
				this.stepPolicyHeader.getWorkflowId(), this.stepPolicyHeader.getNextTaskId());

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "StepPolicyDialog");
		}

		// READ OVERHANDED params !
		// we get the stepPolicyListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete stepPolicyHeader here.
		if (args.containsKey("stepPolicyListCtrl")) {
			setStepPolicyListCtrl((StepPolicyListCtrl) args.get("stepPolicyListCtrl"));
		} else {
			setStepPolicyListCtrl(null);
		}

		getBorderLayoutHeight();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 100; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxStepPolicyDetail.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 24)-1;

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getStepPolicyHeader());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
	
		this.policyCode.setMaxlength(8);
		this.policyDesc.setMaxlength(50);
		this.noOfSteps.setMaxlength(2);

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

		getUserWorkspace().alocateAuthorities("StepPolicyDialog",getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.button_StepPolicyDialog_btnNew_StepPolicyDetail.setVisible(getUserWorkspace().isAllowed("button_StepPolicyDialog_btnNew_StepPolicyDetail"));

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
	public void onClose$window_StepPolicyDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_StepPolicyDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

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
			closeDialog(this.window_StepPolicyDialog, "StepPolicyDialog");
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
	 * @param aStepPolicyHeader
	 *            (StepPolicyHeader)
	 */
	public void doWriteBeanToComponents(StepPolicyHeader aStepPolicyHeader) {
		logger.debug("Entering");

		this.policyCode.setValue(aStepPolicyHeader.getPolicyCode());
		this.policyDesc.setValue(aStepPolicyHeader.getPolicyDesc());

		doFillStepPolicyDetails(aStepPolicyHeader.getStepPolicyDetails());

		this.recordStatus.setValue(aStepPolicyHeader.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aStepPolicyHeader
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(StepPolicyHeader aStepPolicyHeader) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aStepPolicyHeader.setPolicyCode(this.policyCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aStepPolicyHeader.setPolicyDesc(this.policyDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}


		aStepPolicyHeader.setStepPolicyDetails(getStepPolicyDetailList());
		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aStepPolicyHeader.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}


	public boolean validateStepPolicies() throws InterruptedException{
		logger.debug("Entering");
		BigDecimal tenorPercTotal = BigDecimal.ZERO;
		BigDecimal emiPercTotal = BigDecimal.ZERO;
		int totalSteps = 0;
		if(StringUtils.trimToEmpty(this.policyCode.getValue()).equals("") || StringUtils.trimToEmpty(this.policyDesc.getValue()).equals("")){
			return false;
		}
		if(getStepPolicyDetailList() != null && !getStepPolicyDetailList().isEmpty()){
			for (StepPolicyDetail stepPolicyDetail : getStepPolicyDetailList()) {
				if(!StringUtils.trimToEmpty(stepPolicyDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) && 
						!StringUtils.trimToEmpty(stepPolicyDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)){
					tenorPercTotal = tenorPercTotal.add(stepPolicyDetail.getTenorSplitPerc());
					emiPercTotal = emiPercTotal.add(stepPolicyDetail.getEmiSplitPerc());
					totalSteps++;
				}
			}
			if(tenorPercTotal.compareTo(new BigDecimal(100)) != 0) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("TenorSplitPerc_IS_EQUAL_OR_LESSER"));
				return false;
			}
			BigDecimal emiPerc = emiPercTotal.divide(new BigDecimal(totalSteps),RoundingMode.HALF_UP);
			if(emiPerc.compareTo(new BigDecimal(100)) != 0) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("EMISplitPerc_IS_EQUAL_OR_LESSER"));
				return false;
			}
		}else{
			PTMessageUtils.showErrorMessage(Labels.getLabel("StepDetail_NoEmpty"));
			return false;
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aStepPolicyHeader
	 * @throws InterruptedException
	 */
	public void doShowDialog(StepPolicyHeader aStepPolicyHeader) throws InterruptedException {
		logger.debug("Entering");

		if (aStepPolicyHeader == null) {
			aStepPolicyHeader = getStepPolicyService().getNewStepPolicyHeader();
			setStepPolicyHeader(aStepPolicyHeader);
		} else {
			setStepPolicyHeader(aStepPolicyHeader);
		}

		if (aStepPolicyHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.policyDesc.focus();
		} else {
			this.policyDesc.focus();
			if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aStepPolicyHeader);

			doStoreInitValues();
			setDialog(this.window_StepPolicyDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of StepPolicyDetail
	 * 
	 * @param stepPolicyDetailList
	 */
	@SuppressWarnings("unchecked")
	public void doFillStepPolicyDetails(List<StepPolicyDetail> stepPolicyDetails) {
		logger.debug("Entering");
		this.listBoxStepPolicyDetail.getItems().clear();
		setStepPolicyDetailList(stepPolicyDetails);
		if(stepPolicyDetails != null && !stepPolicyDetails.isEmpty()){
			Comparator<Object> comp = new BeanComparator("stepNumber");
			Collections.sort(stepPolicyDetails,comp);
			this.noOfSteps.setValue(stepPolicyDetails.size());
			for (StepPolicyDetail stepPolicyDetail : stepPolicyDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(stepPolicyDetail.getStepNumber()));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(stepPolicyDetail.getTenorSplitPerc().doubleValue(),2)+"%");
				lc.setStyle("text-align: right");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(stepPolicyDetail.getRateMargin().doubleValue(), 9));
				lc.setStyle("text-align: right");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(stepPolicyDetail.getEmiSplitPerc().doubleValue(),2)+"%");
				lc.setStyle("text-align: right");
				lc.setParent(item);
				lc = new Listcell(stepPolicyDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(stepPolicyDetail.getRecordType());
				lc.setParent(item);
				item.setAttribute("data", stepPolicyDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onStepPolicyDetailItemDoubleClicked");
				this.listBoxStepPolicyDetail.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}



	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_policyCode = this.policyCode.getValue();
		this.oldVar_policyDesc = this.policyDesc.getValue();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_stepPolicyDetailList = getStepPolicyDetailList();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.policyCode.setValue(this.oldVar_policyCode);
		this.policyDesc.setValue(this.oldVar_policyDesc);
		this.noOfSteps.setValue(this.oldVar_noOfSteps);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.stepPolicyDetailList = this.oldVar_stepPolicyDetailList;

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
		doClearMessage();

		if (this.oldVar_policyCode != this.policyCode.getValue()) {
			return true;
		}
		if (this.oldVar_policyDesc != this.policyDesc.getValue()) {
			return true;
		}
		if (this.oldVar_noOfSteps != this.noOfSteps.intValue()) {
			return true;
		}
		if(this.oldVar_stepPolicyDetailList != this.stepPolicyDetailList){
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

		if (!this.policyCode.isReadonly()){
			this.policyCode.setConstraint(new PTStringValidator(Labels.getLabel("label_StepPolicyDialog_PolicyCode.value"), PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE,true));
		}
		if (!this.policyDesc.isReadonly()){
			this.policyDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_StepPolicyDialog_PolicyDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION,true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.policyCode.setConstraint("");
		this.policyDesc.setConstraint("");
		this.noOfSteps.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Disables the Validation by setting empty constraints to the LOVFields.
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Method for Clear the Error Messages
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.policyCode.setErrorMessage("");
		this.policyDesc.setErrorMessage("");
		this.noOfSteps.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for refreshing the list in ListCtrl
	 */
	private void refreshList() {
		logger.debug("Entering");
		getStepPolicyListCtrl().findSearchObject();
		if (getStepPolicyListCtrl().listBoxStepPolicy != null) {
			getStepPolicyListCtrl().listBoxStepPolicy.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a StepPolicyHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final StepPolicyHeader aStepPolicyHeader = new StepPolicyHeader();
		BeanUtils.copyProperties(getStepPolicyHeader(), aStepPolicyHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
				aStepPolicyHeader.getPolicyCode()+":"+aStepPolicyHeader.getPolicyDesc();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aStepPolicyHeader.getRecordType()).equals("")) {
				aStepPolicyHeader.setVersion(aStepPolicyHeader.getVersion() + 1);
				aStepPolicyHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aStepPolicyHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aStepPolicyHeader, tranType)) {
					refreshList();
					// do Close the dialog
					closeDialog(this.window_StepPolicyDialog, "StepPolicyDialog");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new StepPolicyHeader object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		doStoreInitValues();

		final StepPolicyHeader aStepPolicyHeader = getStepPolicyService().getNewStepPolicyHeader();
		setStepPolicyHeader(aStepPolicyHeader);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.policyDesc.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getStepPolicyHeader().isNewRecord()) {
			this.policyCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.policyCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.policyDesc.setReadonly(isReadOnly("StepPolicyDialog_policyDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.stepPolicyHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.policyCode.setReadonly(true);
		this.policyDesc.setReadonly(true);
		this.noOfSteps.setReadonly(true);

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
		this.policyCode.setValue("");
		this.policyDesc.setValue("");
		this.noOfSteps.setValue(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final StepPolicyHeader aStepPolicyHeader = new StepPolicyHeader();
		BeanUtils.copyProperties(getStepPolicyHeader(), aStepPolicyHeader);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the StepPolicyHeader object with the components data
		doWriteComponentsToBean(aStepPolicyHeader);

		if(!validateStepPolicies()){
			return;
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aStepPolicyHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aStepPolicyHeader.getRecordType()).equals("")) {
				aStepPolicyHeader.setVersion(aStepPolicyHeader.getVersion() + 1);
				if (isNew) {
					aStepPolicyHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aStepPolicyHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aStepPolicyHeader.setNewRecord(true);
				}
			}
		} else {
			aStepPolicyHeader.setVersion(aStepPolicyHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aStepPolicyHeader, tranType)) {
				refreshList();
				// do Close the Dialog window
				closeDialog(this.window_StepPolicyDialog, "StepPolicyDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aStepPolicyHeader
	 *            (StepPolicyHeader)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(StepPolicyHeader aStepPolicyHeader, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aStepPolicyHeader.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aStepPolicyHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aStepPolicyHeader.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aStepPolicyHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aStepPolicyHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aStepPolicyHeader);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, 
						aStepPolicyHeader))) {
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

			aStepPolicyHeader.setTaskId(taskId);
			aStepPolicyHeader.setNextTaskId(nextTaskId);
			aStepPolicyHeader.setRoleCode(getRole());
			aStepPolicyHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aStepPolicyHeader, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aStepPolicyHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aStepPolicyHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aStepPolicyHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;

		StepPolicyHeader aStepPolicyHeader = (StepPolicyHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getStepPolicyService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getStepPolicyService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getStepPolicyService().doApprove(auditHeader);

						if (aStepPolicyHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getStepPolicyService().doReject(auditHeader);
						if (aStepPolicyHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_StepPolicyDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_StepPolicyDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//




	/**
	 * Call the StepPolicyDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_StepPolicyDialog_btnNew_StepPolicyDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new StepPolicyDetail object, We GET it from the backEnd.
		final StepPolicyDetail aStepPolicyDetail = getStepPolicyService().getNewStepPolicyDetail();
		aStepPolicyDetail.setPolicyCode(this.policyCode.getValue());

		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("stepPolicyDetail", aStepPolicyDetail);
		map.put("stepPolicyDialogCtrl", this);
		map.put("policyDesc", this.policyDesc.getValue());
		map.put("role", getRole());
		map.put("totTenorPerc",getTotTenorPerc());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/StepPolicy/StepPolicyDetailDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Double Click the Transaction Entry Item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onStepPolicyDetailItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem item = (Listitem) event.getOrigin().getTarget();
		StepPolicyDetail stepPolicyDetail = (StepPolicyDetail) item.getAttribute("data");

		if (StringUtils.trimToEmpty(stepPolicyDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) || StringUtils.trimToEmpty(stepPolicyDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
			PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
		}else{
			stepPolicyDetail.setPolicyCode(this.policyCode.getValue());
			stepPolicyDetail.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("stepPolicyDetail", stepPolicyDetail);
			map.put("stepPolicyDialogCtrl", this);
			map.put("policyDesc", this.policyDesc.getValue());
			map.put("role", getRole());
			BigDecimal totTenorPerc = getTotTenorPerc();
			if(stepPolicyDetail.getTenorSplitPerc() != null){
				totTenorPerc = totTenorPerc.subtract(stepPolicyDetail.getTenorSplitPerc());
			}
			map.put("totTenorPerc",totTenorPerc);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/SolutionFactory/StepPolicy/StepPolicyDetailDialog.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}


	public BigDecimal getTotTenorPerc(){
		BigDecimal totTenorPerc = BigDecimal.ZERO;
		if(getStepPolicyDetailList() != null && !getStepPolicyDetailList().isEmpty()) {
			for (StepPolicyDetail stepPolicyDetail : getStepPolicyDetailList()) {
				if(stepPolicyDetail.getTenorSplitPerc() != null && !StringUtils.trimToEmpty(stepPolicyDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) && 
				!StringUtils.trimToEmpty(stepPolicyDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)){
					totTenorPerc = totTenorPerc.add(stepPolicyDetail.getTenorSplitPerc());
				}
			}
		}
		return totTenorPerc;
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
	private AuditHeader getAuditHeader(StepPolicyHeader aStepPolicyHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aStepPolicyHeader.getBefImage(), aStepPolicyHeader);
		return new AuditHeader(String.valueOf(aStepPolicyHeader.getPolicyCode()), 
				null, null, null, auditDetail, aStepPolicyHeader.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_StepPolicyDialog, auditHeader);
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
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("StepPolicyHeader");
		notes.setReference(String.valueOf(getStepPolicyHeader().getPolicyCode()));
		notes.setVersion(getStepPolicyHeader().getVersion());
		logger.debug("Leaving");
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

	public StepPolicyHeader getStepPolicyHeader() {
		return this.stepPolicyHeader;
	}
	public void setStepPolicyHeader(StepPolicyHeader stepPolicyHeader) {
		this.stepPolicyHeader = stepPolicyHeader;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
	public StepPolicyService getStepPolicyService() {
		return this.stepPolicyService;
	}

	public StepPolicyListCtrl getStepPolicyListCtrl() {
		return stepPolicyListCtrl;
	}

	public void setStepPolicyListCtrl(StepPolicyListCtrl stepPolicyListCtrl) {
		this.stepPolicyListCtrl = stepPolicyListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setStepPolicyDetailList(List<StepPolicyDetail> stepPolicyDetailList) {
		this.stepPolicyDetailList = stepPolicyDetailList;
	}
	public List<StepPolicyDetail> getStepPolicyDetailList() {
		return stepPolicyDetailList;
	}

}
