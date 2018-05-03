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
 * FileName    		:  FacilityReferenceDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.lmtmasters.facilityreferencedetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FacilityReference;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.FacilityReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FacilityReferenceDetail
 * /facilityReferenceDetailDialog.zul file.
 */
public class FacilityReferenceDetailDialogCtrl extends GFCBaseCtrl<FacilityReferenceDetail> {
	private static final long serialVersionUID = 4224402842313630803L;
	private static final Logger logger = Logger.getLogger(FacilityReferenceDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FacilityReferenceDetailDialog; // auto wired
	protected Textbox finType; // auto wired
	protected Intbox finRefType; // auto wired
	protected Longbox finRefId; // auto wired
	protected Checkbox isActive; // auto wired
	protected Textbox showInStage; // auto wired
	protected Textbox mandInputInStage; // auto wired
	protected Textbox allowInputInStage; // auto wired


	// not auto wired variables
	private FacilityReferenceDetail facilityReferenceDetail; // over handed per parameters
	private transient FacilityReferenceDetailListCtrl facilityReferenceDetailListCtrl; // over handed per parameters

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient FacilityReferenceDetailService facilityReferenceDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

	private FacilityReference facilityReference;

	protected Button btnNew_FinanceCheckList;
	protected Listbox listBoxFinanceCheckList;
	protected Button btnNew_FinanceAgreementLink;
	protected Listbox listboxFinanceAgreementLink;
	protected Button btnNew_FinanceEligibilityLink;
	protected Listbox listBoxEligibilityRules;
	protected Button btnNew_FinanceScoringGroup;
	protected Listbox listBoxScoringGroup;
	protected Button btnNew_FinCorpScoringGroup;
	protected Listbox listBoxCorpScoringGroup;
	protected Grid grid_Basicdetails;
	protected Label lovDescFinTypeDescName;
	protected Listbox listBoxAccounts;
	protected Button btnNew_FinanceAdvanceAccounting;
	protected Listbox listBoxTemplates;
	protected Button btnNew_FinanceMailTemplate;

	
	private String roles;
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public FacilityReferenceDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FacilityReferenceDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FacilityReferenceDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FacilityReferenceDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FacilityReferenceDetailDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		// READ OVERHANDED parameters !
		if (arguments.containsKey("facilityReferenceDetail")) {
			this.facilityReferenceDetail = (FacilityReferenceDetail) arguments.get("facilityReferenceDetail");
			FacilityReferenceDetail befImage = new FacilityReferenceDetail();
			BeanUtils.copyProperties(this.facilityReferenceDetail, befImage);
			this.facilityReferenceDetail.setBefImage(befImage);

			setFacilityReferenceDetail(this.facilityReferenceDetail);
		} else {
			setFacilityReferenceDetail(null);
		}

		if (arguments.containsKey("facilityReference")) {
			this.facilityReference = (FacilityReference) arguments.get("facilityReference");
			FacilityReference befImage = new FacilityReference();
			BeanUtils.copyProperties(this.facilityReference, befImage);
			setFacilityReference(this.facilityReference);
			roles = facilityReference.getLovDescWorkFlowRolesName();
		} else {
			setFacilityReference(null);
		}

		doLoadWorkFlow(this.facilityReferenceDetail.isWorkflow(), this.facilityReferenceDetail.getWorkflowId(), this.facilityReferenceDetail.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "FacilityReferenceDetailDialog");
		}

		// READ OVERHANDED parameters !
		// we get the facilityReferenceDetailListWindow controller. So we have
		// access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete facilityReferenceDetail here.
		if (arguments.containsKey("facilityReferenceDetailListCtrl")) {
			setFacilityReferenceDetailListCtrl((FacilityReferenceDetailListCtrl) arguments.get("facilityReferenceDetailListCtrl"));
		} else {
			setFacilityReferenceDetailListCtrl(null);
		}
		
		getBorderLayoutHeight();
		grid_Basicdetails.getRows().getVisibleItemCount();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 100 +25; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		
		this.listboxFinanceAgreementLink.setHeight(listboxHeight+"px");
		this.listBoxEligibilityRules.setHeight(listboxHeight+"px");
		this.listBoxFinanceCheckList.setHeight(listboxHeight+"px");
		this.listBoxScoringGroup.setHeight(listboxHeight+"px");
		this.listBoxCorpScoringGroup.setHeight(listboxHeight+"px");
		this.listBoxAccounts.setHeight(listboxHeight+"px");
		this.listBoxTemplates.setHeight(listboxHeight+"px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFacilityReference());
		this.btnDelete.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finRefType.setMaxlength(10);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_FacilityReferenceDetailDialog);
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
		doWriteBeanToComponents(this.facilityReference.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnDelete.setVisible(false);
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFacilityReferenceDetail
	 *            FacilityReferenceDetail
	 */
	public void doWriteBeanToComponents(FacilityReference aFacilityReference) {
		logger.debug("Entering");

		this.finType.setValue(aFacilityReference.getFinType());
		// this.finRefType.setValue(aFacilityReferenceDetail.getFinRefType());
		// this.finRefId.setValue(aFacilityReferenceDetail.getFinRefId());
		// this.isActive.setChecked(aFacilityReferenceDetail.isIsActive());
		// this.showInStage.setValue(aFacilityReferenceDetail.getShowInStage());
		// this.mandInputInStage.setValue(aFacilityReferenceDetail.getMandInputInStage());
		// this.allowInputInStage.setValue(aFacilityReferenceDetail.getAllowInputInStage());
		// this.recordStatus.setValue(aFacilityReferenceDetail.getRecordStatus());
		
		this.lovDescFinTypeDescName.setValue(aFacilityReference.getLovDescFinTypeDescName());
		dofillListbox(aFacilityReference.getCheckList(), this.listBoxFinanceCheckList);
		
		dofillListbox(aFacilityReference.getAggrementList(), this.listboxFinanceAgreementLink);
				
		dofillListbox(aFacilityReference.getScoringGroupList(), this.listBoxScoringGroup);
		
		dofillListbox(aFacilityReference.getCorpScoringGroupList(), this.listBoxCorpScoringGroup);
		if(aFacilityReference.getCorpScoringGroupList()!=null && aFacilityReference.getCorpScoringGroupList().size() == 1){
			this.btnNew_FinCorpScoringGroup.setVisible(false);
		}
		
		dofillListbox(aFacilityReference.getMailTemplateList(), this.listBoxTemplates);
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFacilityReferenceDetail
	 */
	public void doWriteComponentsToBean(FacilityReferenceDetail aFacilityReferenceDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFacilityReferenceDetail.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setFinRefType(this.finRefType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setFinRefId(this.finRefId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setIsActive(this.isActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setShowInStage(this.showInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setMandInputInStage(this.mandInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setAllowInputInStage(this.allowInputInStage.getValue());
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

		aFacilityReferenceDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFacilityReferenceDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FacilityReference aFacilityReference) throws InterruptedException {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFacilityReference);
			this.isActive.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				btnSave.setVisible(true);
	
			}
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityReferenceDetailDialog_FinType.value"),null,true));
		}
		if (!this.finRefType.isReadonly()) {
			this.finRefType.setConstraint(new PTNumberValidator(Labels.getLabel("label_FacilityReferenceDetailDialog_FinRefType.value"), true));
		}
		if (!this.finRefId.isReadonly()) {
			this.finRefId.setConstraint(new PTNumberValidator(Labels.getLabel("label_FacilityReferenceDetailDialog_FinRefId.value"), true));
		}
		if (!this.showInStage.isReadonly()) {
			this.showInStage.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityReferenceDetailDialog_ShowInStage.value"),null,true));
		}
		if (!this.mandInputInStage.isReadonly()) {
			this.mandInputInStage.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityReferenceDetailDialog_MandInputInStage.value"),null,true));
		}
		if (!this.allowInputInStage.isReadonly()) {
			this.allowInputInStage.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityReferenceDetailDialog_AllowInputInStage.value"),null,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finType.setConstraint("");
		this.finRefType.setConstraint("");
		this.finRefId.setConstraint("");
		this.showInStage.setConstraint("");
		this.mandInputInStage.setConstraint("");
		this.allowInputInStage.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FacilityReferenceDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FacilityReferenceDetail aFacilityReferenceDetail = new FacilityReferenceDetail();
		BeanUtils.copyProperties(getFacilityReferenceDetail(), aFacilityReferenceDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFacilityReferenceDetail.getFinRefDetailId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFacilityReferenceDetail.getRecordType())) {
				aFacilityReferenceDetail.setVersion(aFacilityReferenceDetail.getVersion() + 1);
				aFacilityReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFacilityReferenceDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFacilityReferenceDetail, tranType)) {
					// refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
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

		if (getFacilityReferenceDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.finType.setReadonly(true);
		this.finRefType.setReadonly(isReadOnly("FacilityReferenceDetailDialog_finRefType"));
		this.finRefId.setReadonly(isReadOnly("FacilityReferenceDetailDialog_finRefId"));
		this.isActive.setDisabled(isReadOnly("FacilityReferenceDetailDialog_isActive"));
		this.showInStage.setReadonly(isReadOnly("FacilityReferenceDetailDialog_showInStage"));
		this.mandInputInStage.setReadonly(isReadOnly("FacilityReferenceDetailDialog_mandInputInStage"));
		this.allowInputInStage.setReadonly(isReadOnly("FacilityReferenceDetailDialog_allowInputInStage"));

		this.btnNew_FinanceCheckList.setDisabled(false);
		this.btnNew_FinanceAgreementLink.setDisabled(false);
		this.btnNew_FinanceEligibilityLink.setDisabled(false);
		this.btnNew_FinanceScoringGroup.setDisabled(false);
		this.btnNew_FinCorpScoringGroup.setDisabled(false);
		this.btnNew_FinanceAdvanceAccounting.setDisabled(false);
		this.btnNew_FinanceMailTemplate.setDisabled(false);

		enableOrDisablelistitems(this.listBoxFinanceCheckList, false);
		enableOrDisablelistitems(this.listboxFinanceAgreementLink, false);
		enableOrDisablelistitems(this.listBoxEligibilityRules, false);
		enableOrDisablelistitems(this.listBoxScoringGroup, false);
		enableOrDisablelistitems(this.listBoxCorpScoringGroup, false);
		enableOrDisablelistitems(this.listBoxAccounts, false);
		enableOrDisablelistitems(this.listBoxTemplates, false);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.facilityReferenceDetail.isNewRecord()) {
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
		this.finType.setReadonly(true);
		this.finRefType.setReadonly(true);
		this.finRefId.setReadonly(true);
		this.isActive.setDisabled(true);
		this.showInStage.setReadonly(true);
		this.mandInputInStage.setReadonly(true);
		this.allowInputInStage.setReadonly(true);

/*		this.btnNew_FinanceCheckList.setDisabled(true);
		this.btnNew_FinanceAgreementLink.setDisabled(true);
		this.btnNew_FinanceEligibilityLink.setDisabled(true);
		this.btnNew_FinanceScoringGroup.setDisabled(true);
		this.btnNew_FinCorpScoringGroup.setDisabled(true);

		enableOrDisablelistitems(this.listBoxFinanceCheckList, true);
		enableOrDisablelistitems(this.listboxFinanceAgreementLink, true);
		enableOrDisablelistitems(this.listBoxEligibilityRules, true);
		enableOrDisablelistitems(this.listBoxScoringGroup, true);
		enableOrDisablelistitems(this.listBoxCorpScoringGroup, true);*/

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

		this.finType.setValue("");
		this.finRefType.setText("");
		this.finRefId.setText("");
		this.isActive.setChecked(false);
		this.showInStage.setValue("");
		this.mandInputInStage.setValue("");
		this.allowInputInStage.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		doSetValidation();
		// doWriteComponentsToBean(getFinanceAgreementList());

		final FacilityReferenceDetail aFacilityReferenceDetail = new FacilityReferenceDetail();
		List<Listitem> items = new ArrayList<Listitem>();
		items.addAll(this.listBoxFinanceCheckList.getItems());
		items.addAll(this.listboxFinanceAgreementLink.getItems());
		items.addAll(this.listBoxScoringGroup.getItems());
		items.addAll(this.listBoxCorpScoringGroup.getItems());
		//items.addAll(this.listBoxEligibilityRules.getItems());
		//items.addAll(this.listBoxAccounts.getItems());
		items.addAll(this.listBoxTemplates.getItems());
		
		for (int i = 0; i < items.size(); i++) {
			FacilityReferenceDetail lsFacilityReferenceDetail = (FacilityReferenceDetail) items.get(i).getAttribute("data");
			setFacilityReferenceDetail(lsFacilityReferenceDetail);
			BeanUtils.copyProperties(getFacilityReferenceDetail(), aFacilityReferenceDetail);
			boolean isNew = false;
			isNew = aFacilityReferenceDetail.isNew();
			String tranType = "";
			aFacilityReferenceDetail.setVersion(aFacilityReferenceDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				if (aFacilityReferenceDetail.getRecordType().equals(PennantConstants.RCD_DEL)) {
					tranType = PennantConstants.TRAN_DEL;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			if (aFacilityReferenceDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
				tranType = "";
			}
			// save it to database
			aFacilityReferenceDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			aFacilityReferenceDetail.setRecordType(PennantConstants.RCD_ADD);
			if (StringUtils.isNotEmpty(tranType)) {
				doProcess(aFacilityReferenceDetail, tranType);
			}

		}
		
		try {
			items.clear();
			items = null;
			closeDialog();

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
	}

	private boolean doProcess(FacilityReferenceDetail aFacilityReferenceDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFacilityReferenceDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFacilityReferenceDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFacilityReferenceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFacilityReferenceDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFacilityReferenceDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFacilityReferenceDetail);
				}

				if (isNotesMandatory(taskId, aFacilityReferenceDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aFacilityReferenceDetail.setTaskId(taskId);
			aFacilityReferenceDetail.setNextTaskId(nextTaskId);
			aFacilityReferenceDetail.setRoleCode(getRole());
			aFacilityReferenceDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFacilityReferenceDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aFacilityReferenceDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFacilityReferenceDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aFacilityReferenceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FacilityReferenceDetail aFacilityReferenceDetail = (FacilityReferenceDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFacilityReferenceDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFacilityReferenceDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFacilityReferenceDetailService().doApprove(auditHeader);

						if (aFacilityReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFacilityReferenceDetailService().doReject(auditHeader);
						if (aFacilityReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FacilityReferenceDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FacilityReferenceDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.facilityReferenceDetail), true);
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
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
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

	public FacilityReferenceDetail getFacilityReferenceDetail() {
		return this.facilityReferenceDetail;
	}

	public void setFacilityReferenceDetail(FacilityReferenceDetail facilityReferenceDetail) {
		this.facilityReferenceDetail = facilityReferenceDetail;
	}

	public void setFacilityReferenceDetailService(FacilityReferenceDetailService facilityReferenceDetailService) {
		this.facilityReferenceDetailService = facilityReferenceDetailService;
	}

	public FacilityReferenceDetailService getFacilityReferenceDetailService() {
		return this.facilityReferenceDetailService;
	}

	public void setFacilityReferenceDetailListCtrl(FacilityReferenceDetailListCtrl facilityReferenceDetailListCtrl) {
		this.facilityReferenceDetailListCtrl = facilityReferenceDetailListCtrl;
	}

	public FacilityReferenceDetailListCtrl getFacilityReferenceDetailListCtrl() {
		return this.facilityReferenceDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(FacilityReferenceDetail aFacilityReferenceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFacilityReferenceDetail.getBefImage(), aFacilityReferenceDetail);
		return new AuditHeader(String.valueOf(aFacilityReferenceDetail.getFinRefDetailId()), null, null, null, auditDetail, aFacilityReferenceDetail.getUserDetails(),
				getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FacilityReferenceDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.facilityReferenceDetail);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.facilityReferenceDetail.getFinRefDetailId());
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finType.setErrorMessage("");
		this.finRefType.setErrorMessage("");
		this.finRefId.setErrorMessage("");
		this.showInStage.setErrorMessage("");
		this.mandInputInStage.setErrorMessage("");
		this.allowInputInStage.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public FacilityReference getFacilityReference() {
		return facilityReference;
	}

	public void setFacilityReference(FacilityReference facilityReference) {
		this.facilityReference = facilityReference;
	}

	// ===================

	public void dofillListbox(List<FacilityReferenceDetail> facilityReferenceDetail, Listbox listbox) {

		if (facilityReferenceDetail != null) {
			for (int i = 0; i < facilityReferenceDetail.size(); i++) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(facilityReferenceDetail.get(i).getFinRefId()));
				lc.setParent(item);

				if (facilityReferenceDetail.get(i).getFinRefType() != FinanceConstants.PROCEDT_CHECKLIST) {
					lc = new Listcell(facilityReferenceDetail.get(i).getLovDescCodelov());
					lc.setParent(item);
					lc = new Listcell(facilityReferenceDetail.get(i).getLovDescNamelov());
					lc.setParent(item);
				}

				lc = new Listcell(facilityReferenceDetail.get(i).getLovDescRefDesc());
				lc.setParent(item);

				String active = "N";
				if (facilityReferenceDetail.get(i).isIsActive()) {
					active = "Y";
				}
				lc = new Listcell(active);
				lc.setParent(item);
				lc = new Listcell(facilityReferenceDetail.get(i).getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(facilityReferenceDetail.get(i).getRecordType());
				lc.setParent(item);
				item.setAttribute("data", facilityReferenceDetail.get(i));

				if (facilityReferenceDetail.get(i).getRecordType().equals(PennantConstants.RCD_DEL)) {
					item.setVisible(false);
				}
				listbox.appendChild(item);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCheckListItemDoubleClicked");
			}

		}

	}

	public void onClick$btnNew_FinanceCheckList(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, FinanceConstants.PROCEDT_CHECKLIST);
	}

	public void onClick$btnNew_FinanceAgreementLink(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, FinanceConstants.PROCEDT_AGREEMENT);
	}

	public void onClick$btnNew_FinanceEligibilityLink(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, FinanceConstants.PROCEDT_ELIGIBILITY);
	}

	public void onClick$btnNew_FinanceScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, FinanceConstants.PROCEDT_RTLSCORE);
	}
	
	public void onClick$btnNew_FinCorpScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, FinanceConstants.PROCEDT_CORPSCORE);
	}
	
	public void onClick$btnNew_FinanceAdvanceAccounting(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, FinanceConstants.PROCEDT_STAGEACC);
	}
	
	public void onClick$btnNew_FinanceMailTemplate(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, FinanceConstants.PROCEDT_TEMPLATE);
	}

	public void onCheckListItemDoubleClicked(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		// Get the event target
		Listitem item = (Listitem) event.getOrigin().getTarget();
		
		FacilityReferenceDetail itemdata = (FacilityReferenceDetail) item.getAttribute("data");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCodeList", roles);
		map.put("facilityReferenceDetail", itemdata);
		map.put("facilityReferenceDetailDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FacilityReferenceDetail/FacilityReferenceDetailDialogLink.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	private void callLinakgeZul(FacilityReferenceDetail facilityReferenceDetail, int type) throws InterruptedException {
		logger.debug("Entering");
		
		facilityReferenceDetail.setFinType(this.finType.getValue());
		facilityReferenceDetail.setFinRefType(type);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCodeList", roles);
		map.put("facilityReferenceDetail", facilityReferenceDetail);
		map.put("facilityReferenceDetailDialogCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FacilityReferenceDetail/FacilityReferenceDetailDialogLink.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void enableOrDisablelistitems(Listbox listbox, boolean disableItem) {
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			item.setDisabled(disableItem);
		}
	}

	@SuppressWarnings("unused")
	private boolean compare(List<FacilityReferenceDetail> referenceDetails, Listbox listbox) {
		for (int i = 0; i < referenceDetails.size(); i++) {
			FacilityReferenceDetail oldFinrefDet = referenceDetails.get(i);
			Listitem item = (Listitem) listbox.getItems().get(i);
			FacilityReferenceDetail newFinrefDet = (FacilityReferenceDetail) item.getAttribute("data");
			if (oldFinrefDet.getRecordType() != newFinrefDet.getRecordType()) {
				return true;
			}
		}
		return false;
	}

}
