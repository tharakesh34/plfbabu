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
 * FileName    		:  FinanceWorkFlowDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.lmtmasters.financeworkflow;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CAFFacilityType;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceWorkFlow/financeWorkFlowDialog.zul file.
 */
public class FinanceWorkFlowDialogCtrl extends GFCBaseCtrl<FinanceWorkFlow> {
	private static final long serialVersionUID = -4959034105708570551L;
	private static final Logger logger = Logger.getLogger(FinanceWorkFlowDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinanceWorkFlowDialog; 	// autoWired
	protected ExtendedCombobox 	finType; 					// autoWired
	protected Combobox 		finEvent; 						// autoWired
	protected Combobox 		screenCode; 					// autoWired
	protected ExtendedCombobox 	workFlowType; 				// autoWired
	protected Combobox 		moduleName; 					// autoWired
	protected Row 			row_finEvent; 					// autoWired
	protected Label 		label_Title; 					// autoWired

	// not auto wired variables
	private FinanceWorkFlow financeWorkFlow; // overHanded per parameter
	private transient FinanceWorkFlowListCtrl financeWorkFlowListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	private boolean isPromotion = false;
	private boolean isCollateral = false;
	private boolean isVAS = false;
	private boolean isCommitment = false;
	private String dialogName = "";
	private String eventAction = "";

	// ServiceDAOs / Domain Classes
	private transient FinanceWorkFlowService financeWorkFlowService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public FinanceWorkFlowDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceWorkFlow object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceWorkFlowDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceWorkFlowDialog);

		try {

			dialogName = "FinanceWorkFlowDialog";
			if (arguments.containsKey("isPromotion")) {
				isPromotion = (Boolean) arguments.get("isPromotion");
				if (isPromotion) {
					dialogName = "PromotionWorkFlowDialog";
				}
			}
			
			if (arguments.containsKey("isCollateral")) {
				isCollateral = (Boolean) arguments.get("isCollateral");
				if (isCollateral) {
					dialogName = "CollateralWorkFlowDialog";
				}
			}
			
			if (arguments.containsKey("isCommitment")) {
				isCommitment = (Boolean) arguments.get("isCommitment");
				if (isCommitment) {
					dialogName = "CommitmentWorkFlowDialog";
				}
			}
			
			if (arguments.containsKey("isVAS")) {
				isVAS = (Boolean) arguments.get("isVAS");
				if (isVAS) {
					dialogName = "VASWorkFlowDialog";
				}
			}

			// Event Name 
			if (arguments.containsKey("eventAction")) {
				eventAction = (String) arguments.get("eventAction");
			}
			
			super.pageRightName = dialogName;


			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeWorkFlow")) {
				this.financeWorkFlow = (FinanceWorkFlow) arguments
						.get("financeWorkFlow");
				FinanceWorkFlow befImage = new FinanceWorkFlow();
				BeanUtils.copyProperties(this.financeWorkFlow, befImage);
				this.financeWorkFlow.setBefImage(befImage);

				setFinanceWorkFlow(this.financeWorkFlow);
			} else {
				setFinanceWorkFlow(null);
			}

			doLoadWorkFlow(this.financeWorkFlow.isWorkflow(),
					this.financeWorkFlow.getWorkflowId(),
					this.financeWorkFlow.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), dialogName);
			}else{
				getUserWorkspace().allocateAuthorities(dialogName);
			}

			// READ OVERHANDED parameters !
			// we get the financeWorkFlowListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete financeWorkFlow here.
			if (arguments.containsKey("financeWorkFlowListCtrl")) {
				setFinanceWorkFlowListCtrl((FinanceWorkFlowListCtrl) arguments.get("financeWorkFlowListCtrl"));
			} else {
				setFinanceWorkFlowListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceWorkFlow());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceWorkFlowDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		doSetFinTypeProperties();
		this.workFlowType.setMandatoryStyle(true);
		this.workFlowType.setModuleName("WorkFlowDetails");
		this.workFlowType.setValueColumn("WorkFlowType");
		this.workFlowType.setDescColumn("WorkFlowDesc");
		this.workFlowType.setValidateColumns(new String[] { "WorkFlowType" });
		readOnlyComponent(true, this.screenCode);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}


	private void doSetFinTypeProperties(){
		logger.debug("Entering") ;
		if (this.moduleName.getSelectedItem() != null && !this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_FINANCE)) {
				this.finType.setModuleName("FinanceType");
				this.finType.setValueColumn("FinType");
				this.finType.setDescColumn("FinTypeDesc");
				this.finType.setValidateColumns(new String[] { "FinType" });
				this.row_finEvent.setVisible(true);
			}else if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_PROMOTION)) {
				this.finType.setModuleName("Promotion");
				this.finType.setValueColumn("PromotionCode");
				this.finType.setDescColumn("PromotionDesc");
				this.finType.setValidateColumns(new String[] { "PromotionCode" });
				this.row_finEvent.setVisible(true);
			}else if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_COLLATERAL)) {
				this.finType.setModuleName("CollateralStructure");
				this.finType.setValueColumn("CollateralType");
				this.finType.setDescColumn("CollateralDesc");
				this.finType.setValidateColumns(new String[] { "CollateralType" });
				this.row_finEvent.setVisible(false);
			}else if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_COMMITMENT)) {
				this.finType.setModuleName("CommitmentType");
				this.finType.setValueColumn("TypeCode");
				this.finType.setDescColumn("Description");
				this.finType.setValidateColumns(new String[] { "TypeCode" });
				this.row_finEvent.setVisible(false);
			}else if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_VAS)) {
				this.finType.setModuleName("VASConfiguration");
				this.finType.setValueColumn("ProductCode");
				this.finType.setDescColumn("ProductDesc");
				this.finType.setValidateColumns(new String[] { "ProductCode" });
				this.row_finEvent.setVisible(false);
			}else if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_FACILITY)) {
				this.finType.setModuleName("CAFFacilityType");
				this.finType.setValueColumn("FacilityType");
				this.finType.setDescColumn("FacilityDesc");
				this.finType.setValidateColumns(new String[] { "FacilityType" });
				this.row_finEvent.setVisible(false);
			}
		}
		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;
		
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
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
		MessageUtil.showHelpWindow(event, window_FinanceWorkFlowDialog);
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
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.financeWorkFlow.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceWorkFlow
	 *            FinanceWorkFlow
	 */
	public void doWriteBeanToComponents(FinanceWorkFlow aFinanceWorkFlow) {
		logger.debug("Entering") ;

		String exclFields = "";
		if(isPromotion){
			exclFields = ","+PennantConstants.WORFLOW_MODULE_FINANCE+","+PennantConstants.WORFLOW_MODULE_FACILITY+","+PennantConstants.WORFLOW_MODULE_COLLATERAL+","+PennantConstants.WORFLOW_MODULE_VAS+","+PennantConstants.WORFLOW_MODULE_COMMITMENT+",";
		}else{
			//Remove Facility If Facility module Exists
			exclFields = ","+PennantConstants.WORFLOW_MODULE_PROMOTION+","+PennantConstants.WORFLOW_MODULE_COLLATERAL+","+PennantConstants.WORFLOW_MODULE_VAS+","+PennantConstants.WORFLOW_MODULE_COMMITMENT+",";
		}
		
		String moduleName = aFinanceWorkFlow.getModuleName();
		if(isCollateral){
			exclFields = ","+PennantConstants.WORFLOW_MODULE_FINANCE+","+PennantConstants.WORFLOW_MODULE_FACILITY+","+PennantConstants.WORFLOW_MODULE_VAS+","+PennantConstants.WORFLOW_MODULE_PROMOTION+","+PennantConstants.WORFLOW_MODULE_COMMITMENT+",";
			moduleName = PennantConstants.WORFLOW_MODULE_COLLATERAL;
			this.moduleName.setDisabled(true);
		}
		if(isCommitment){
			exclFields = ","+PennantConstants.WORFLOW_MODULE_FINANCE+","+PennantConstants.WORFLOW_MODULE_FACILITY+","+PennantConstants.WORFLOW_MODULE_COLLATERAL+","+PennantConstants.WORFLOW_MODULE_PROMOTION+","+PennantConstants.WORFLOW_MODULE_VAS+",";
			moduleName = PennantConstants.WORFLOW_MODULE_COMMITMENT;
			this.moduleName.setDisabled(true);
		}
		if(isVAS){
			exclFields = ","+PennantConstants.WORFLOW_MODULE_FINANCE+","+PennantConstants.WORFLOW_MODULE_FACILITY+","+PennantConstants.WORFLOW_MODULE_COLLATERAL+","+PennantConstants.WORFLOW_MODULE_PROMOTION+","+PennantConstants.WORFLOW_MODULE_COMMITMENT+",";
			moduleName = PennantConstants.WORFLOW_MODULE_VAS;
			this.moduleName.setDisabled(true);
		}
		ArrayList<ValueLabel> list = null;
		if(StringUtils.equals(eventAction, FinanceConstants.FINSER_EVENT_ORG)){
			list = PennantStaticListUtil.getFinServiceEvents(false);
		}else{
			list = PennantStaticListUtil.getFinServiceEvents(true);
		}
		
		fillComboBox(this.moduleName, moduleName, PennantStaticListUtil.getWorkFlowModules(), exclFields);
		doSetFinTypeProperties();
		this.finType.setValue(aFinanceWorkFlow.getFinType());
		fillComboBox(this.screenCode, aFinanceWorkFlow.getScreenCode(), PennantStaticListUtil.getScreenCodes(), "");
		fillComboBox(this.finEvent, aFinanceWorkFlow.getFinEvent(), sortFinanceEvents(list), "");
		this.workFlowType.setValue(aFinanceWorkFlow.getWorkFlowType());

		if (aFinanceWorkFlow.isNewRecord()){
			this.finType.setDescription("");
			this.workFlowType.setDescription("");
		}else{
			if (aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_FINANCE)
					|| aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_PROMOTION)) {
				this.finType.setDescription(aFinanceWorkFlow.getLovDescFinTypeName());
			}else if (aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_COLLATERAL)) {
				this.finType.setDescription(aFinanceWorkFlow.getCollateralDesc());
			}else if (aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_VAS)) {
				this.finType.setDescription(aFinanceWorkFlow.getVasProductDesc());
			}else if (aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_COMMITMENT)) {
				this.finType.setDescription(aFinanceWorkFlow.getCommitmentTypeDesc());
			}else if (aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_FACILITY)) {
				this.finType.setDescription(aFinanceWorkFlow.getLovDescFacilityTypeName());
			}
			this.workFlowType.setDescription(aFinanceWorkFlow.getLovDescWorkFlowTypeName());
		}
		this.recordStatus.setValue(aFinanceWorkFlow.getRecordStatus());
		logger.debug("Leaving");
	}
	
	/**
	 * For Sorting Events in alphabetical order
	 * 
	 * @param eventList
	 * @return
	 */
	private List<ValueLabel> sortFinanceEvents(List<ValueLabel> eventList) {
		if (eventList != null && eventList.size() > 0) {
			Collections.sort(eventList, new Comparator<ValueLabel>() {
				@Override
				public int compare(ValueLabel detail1, ValueLabel detail2) {
					return detail1.getLabel().compareTo(detail2.getLabel());
				}
			});
		}

		return eventList;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceWorkFlow
	 */
	public void doWriteComponentsToBean(FinanceWorkFlow aFinanceWorkFlow) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.moduleName.getSelectedItem() == null || this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.moduleName,Labels.getLabel("FIELD_IS_MAND",new String[]{Labels.getLabel("label_FinanceWorkFlowDialog_moduleName.value")}));
			}
			aFinanceWorkFlow.setModuleName(this.moduleName.getSelectedItem().getValue().toString());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceWorkFlow.setLovDescFinTypeName(this.finType.getDescription());
			aFinanceWorkFlow.setFinType(this.finType.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceWorkFlow.setScreenCode(this.screenCode.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			
			if (!this.finEvent.isDisabled() && this.row_finEvent.isVisible() && (this.finEvent.getSelectedItem() == null || 
					this.finEvent.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select))) {
				throw new WrongValueException(this.finEvent,Labels.getLabel("FIELD_IS_MAND",new String[]{Labels.getLabel("label_FinanceWorkFlowDialog_FinEvent.value")}));
			}
			
			if(StringUtils.equals(aFinanceWorkFlow.getModuleName(), PennantConstants.WORFLOW_MODULE_FINANCE) ||
					StringUtils.equals(aFinanceWorkFlow.getModuleName(), PennantConstants.WORFLOW_MODULE_PROMOTION)){
				aFinanceWorkFlow.setFinEvent(this.finEvent.getSelectedItem().getValue().toString());
				aFinanceWorkFlow.setLovDescFinTypeName(this.finType.getDescription());
			}else if(StringUtils.equals(aFinanceWorkFlow.getModuleName(), PennantConstants.WORFLOW_MODULE_COLLATERAL)){
				aFinanceWorkFlow.setFinEvent(this.finEvent.getItemAtIndex(1).getValue().toString());
				aFinanceWorkFlow.setCollateralDesc(this.finType.getDescription());
			}else if(StringUtils.equals(aFinanceWorkFlow.getModuleName(), PennantConstants.WORFLOW_MODULE_VAS)){
				aFinanceWorkFlow.setFinEvent(this.finEvent.getItemAtIndex(1).getValue().toString());
				aFinanceWorkFlow.setVasProductDesc(this.finType.getDescription());
			}else if(StringUtils.equals(aFinanceWorkFlow.getModuleName(), PennantConstants.WORFLOW_MODULE_COMMITMENT)){
				aFinanceWorkFlow.setFinEvent(this.finEvent.getItemAtIndex(1).getValue().toString());
				aFinanceWorkFlow.setCommitmentTypeDesc(this.finType.getDescription());
			}else if(StringUtils.equals(aFinanceWorkFlow.getModuleName(), PennantConstants.WORFLOW_MODULE_FACILITY)){
				aFinanceWorkFlow.setFinEvent(this.finEvent.getItemAtIndex(1).getValue().toString());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceWorkFlow.setLovDescWorkFlowTypeName(this.workFlowType.getDescription());
			aFinanceWorkFlow.setWorkFlowType(this.workFlowType.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinanceWorkFlow.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceWorkFlow
	 * @throws Exception
	 */
	public void doShowDialog(FinanceWorkFlow aFinanceWorkFlow) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceWorkFlow.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.moduleName.focus();
		} else {
			this.screenCode.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceWorkFlow);

			if(isPromotion){
				this.label_Title.setValue(Labels.getLabel("window_PromotionWorkFlowDialog.title"));
			} else if(isCollateral){
				this.label_Title.setValue(Labels.getLabel("window_CollateralWorkFlowDialog.title"));
			} else if(isCommitment){
				this.label_Title.setValue(Labels.getLabel("window_CommitmentWorkFlowDialog.title"));
			} else if(isVAS){
				this.label_Title.setValue(Labels.getLabel("window_VASWorkFlowDialog.title"));
			}
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceWorkFlowDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.screenCode.isDisabled()){
			this.screenCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_ScreenCode.value"), null, true));
		}	
		if (!this.finEvent.isDisabled()){
			this.finEvent.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_FinEvent.value"), null, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.screenCode.setConstraint("");
		this.finEvent.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */	
	private void doSetLOVValidation() {
		this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_FinType.value"), null, true,true));
		this.workFlowType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_WorkFlowType.value"), null, true,true));
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.finType.setConstraint("");
		this.workFlowType.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finType.setErrorMessage("");
		this.screenCode.setErrorMessage("");
		this.finEvent.setErrorMessage("");
		this.workFlowType.setErrorMessage("");
		this.moduleName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getFinanceWorkFlowListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a FinanceWorkFlow object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceWorkFlow aFinanceWorkFlow = new FinanceWorkFlow();
		BeanUtils.copyProperties(getFinanceWorkFlow(), aFinanceWorkFlow);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_FinanceWorkFlowDialog_FinType.value")+" : "+aFinanceWorkFlow.getFinType();
		if(!StringUtils.equals(aFinanceWorkFlow.getFinEvent(), FinanceConstants.FINSER_EVENT_ORG)){
			msg = msg.concat(" & "+Labels.getLabel("label_FinanceWorkFlowDialog_FinEvent.value")+" : "+aFinanceWorkFlow.getFinEvent());
		}
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinanceWorkFlow.getRecordType())){
				aFinanceWorkFlow.setVersion(aFinanceWorkFlow.getVersion()+1);
				aFinanceWorkFlow.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aFinanceWorkFlow.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinanceWorkFlow,tranType)){
					refreshList();
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
		
		int count = financeWorkFlowService.getVASProductCode(getFinanceWorkFlow().getFinType());
		if (getFinanceWorkFlow().isNewRecord()){
			this.finType.setReadonly(false);
			this.moduleName.setDisabled(false);
			this.btnCancel.setVisible(false);
			this.finEvent.setDisabled(isReadOnly(dialogName+"_finEvent"));
		}else{
			this.finType.setReadonly(true);
			this.moduleName.setDisabled(true);
			this.btnCancel.setVisible(true);
			this.finEvent.setDisabled(true);
		}

		this.screenCode.setDisabled(isReadOnly(dialogName+"_screenCode"));
		this.workFlowType.setReadonly(isReadOnly(dialogName+"_workFlowType"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeWorkFlow.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		if(isVAS && count > 0){
			this.btnDelete.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finType.setReadonly(true);
		this.screenCode.setDisabled(true);
		this.finEvent.setDisabled(true);
		this.workFlowType.setReadonly(true);

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
		this.finType.setValue("");
		this.finType.setDescription("");
		this.screenCode.setValue("");
		this.finEvent.setSelectedIndex(0);
		this.workFlowType.setValue("");
		this.workFlowType.setDescription("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceWorkFlow aFinanceWorkFlow = new FinanceWorkFlow();
		BeanUtils.copyProperties(getFinanceWorkFlow(), aFinanceWorkFlow);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinanceWorkFlow object with the components data
		doWriteComponentsToBean(aFinanceWorkFlow);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinanceWorkFlow.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceWorkFlow.getRecordType())){
				aFinanceWorkFlow.setVersion(aFinanceWorkFlow.getVersion()+1);
				if(isNew){
					aFinanceWorkFlow.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceWorkFlow.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceWorkFlow.setNewRecord(true);
				}
			}
		}else{
			aFinanceWorkFlow.setVersion(aFinanceWorkFlow.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aFinanceWorkFlow,tranType)){
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**	
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinanceWorkFlow (FinanceWorkFlow)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(FinanceWorkFlow aFinanceWorkFlow,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aFinanceWorkFlow.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceWorkFlow.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceWorkFlow.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinanceWorkFlow.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceWorkFlow.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinanceWorkFlow);
				}

				if (isNotesMandatory(taskId, aFinanceWorkFlow)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();

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

			aFinanceWorkFlow.setTaskId(taskId);
			aFinanceWorkFlow.setNextTaskId(nextTaskId);
			aFinanceWorkFlow.setRoleCode(getRole());
			aFinanceWorkFlow.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aFinanceWorkFlow, tranType);

			String operationRefs = getServiceOperations(taskId, aFinanceWorkFlow);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinanceWorkFlow, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aFinanceWorkFlow, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
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
		boolean deleteNotes=false;

		FinanceWorkFlow aFinanceWorkFlow = (FinanceWorkFlow) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceWorkFlowService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFinanceWorkFlowService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceWorkFlowService().doApprove(auditHeader);

						if(aFinanceWorkFlow.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceWorkFlowService().doReject(auditHeader);
						if(aFinanceWorkFlow.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceWorkFlowDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_FinanceWorkFlowDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.financeWorkFlow),true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	// Search Button Component Events

	public void onFulfill$finType(Event event){
		logger.debug("Entering" + event.toString());

		if (this.moduleName.getSelectedItem() == null || this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			throw new WrongValueException(this.moduleName,Labels.getLabel("FIELD_IS_MAND",new String[]{Labels.getLabel("label_FinanceWorkFlowDialog_moduleName.value")}));
		}
		Object dataObject=null;
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else {
			if (dataObject instanceof FinanceType) {
				FinanceType details = (FinanceType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFinType());
					this.finType.setDescription(details.getFinTypeDesc());
				}
			} else if (dataObject instanceof CAFFacilityType) {
				CAFFacilityType details = (CAFFacilityType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFacilityType());
					this.finType.setDescription(details.getFacilityDesc());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$workFlowType(Event event){
		logger.debug("Entering" + event.toString());

		Object dataObject = workFlowType.getObject();
		if (dataObject instanceof String){
			this.workFlowType.setValue(dataObject.toString());
			this.workFlowType.setDescription("");
		}else{
			WorkFlowDetails details= (WorkFlowDetails) dataObject;
			if (details != null) {
				this.workFlowType.setValue(details.getWorkFlowType());
				this.workFlowType.setDescription(details.getWorkFlowDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$moduleName(Event event){
		logger.debug("Entering" + event.toString());
		this.finType.setValue("");
		this.finType.setDescription("");
		doSetFinTypeProperties();
		logger.debug("Leaving" + event.toString());
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aSubSegment
	 *            (SubSegment)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(FinanceWorkFlow aFinanceWorkFlow, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceWorkFlow.getBefImage(), aFinanceWorkFlow);   
		return new AuditHeader(aFinanceWorkFlow.getFinType(),null,null,null,auditDetail,aFinanceWorkFlow.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceWorkFlowDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.financeWorkFlow);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.financeWorkFlow.getFinType());
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

	public FinanceWorkFlow getFinanceWorkFlow() {
		return this.financeWorkFlow;
	}
	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return this.financeWorkFlowService;
	}

	public void setFinanceWorkFlowListCtrl(FinanceWorkFlowListCtrl financeWorkFlowListCtrl) {
		this.financeWorkFlowListCtrl = financeWorkFlowListCtrl;
	}
	public FinanceWorkFlowListCtrl getFinanceWorkFlowListCtrl() {
		return this.financeWorkFlowListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
