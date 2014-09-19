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

import java.io.Serializable;
import java.sql.Timestamp;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FacilityReference;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.FacilityReferenceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.LongValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FacilityReferenceDetail
 * /facilityReferenceDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FacilityReferenceDetailDialogCtrl extends GFCBaseListCtrl<FacilityReferenceDetail> implements Serializable {

	private static final long serialVersionUID = 4224402842313630803L;
	private final static Logger logger = Logger.getLogger(FacilityReferenceDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FacilityReferenceDetailDialog; // auto wired
	protected Textbox finType; // auto wired
	protected Intbox finRefType; // auto wired
	protected Longbox finRefId; // auto wired
	protected Checkbox isActive; // auto wired
	protected Textbox showInStage; // auto wired
	protected Textbox mandInputInStage; // auto wired
	protected Textbox allowInputInStage; // auto wired

	protected Label recordStatus; // auto wired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;

	// not auto wired variables
	private FacilityReferenceDetail facilityReferenceDetail; // over handed per parameters
	private transient FacilityReferenceDetailListCtrl facilityReferenceDetailListCtrl; // over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_finType;
	private transient int oldVar_finRefType;
	private transient long oldVar_finRefId;
	private transient boolean oldVar_isActive;
	private transient String oldVar_showInStage;
	private transient String oldVar_mandInputInStage;
	private transient String oldVar_allowInputInStage;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FacilityReferenceDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // auto wired
	protected Button btnEdit; // auto wired
	protected Button btnDelete; // auto wired
	protected Button btnSave; // auto wired
	protected Button btnCancel; // auto wired
	protected Button btnClose; // auto wired
	protected Button btnHelp; // auto wired
	protected Button btnNotes; // auto wired

	// ServiceDAOs / Domain Classes
	private transient FacilityReferenceDetailService facilityReferenceDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

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

	private transient List<FacilityReferenceDetail> oldVar_CheckList;
	private transient List<FacilityReferenceDetail> oldVar_Agreement;
	private transient List<FacilityReferenceDetail> oldVar_ScoringGroup;
	private transient List<FacilityReferenceDetail> oldVar_CorpScoringGroup;
// Not Used Now may be Required In Future ... please do Not Remove With out Proper Approval.	
//	private transient List<FacilityReferenceDetail> oldVar_Eligibility;
//	private transient List<FacilityReferenceDetail> oldVar_AdvanceAccount;
	private transient List<FacilityReferenceDetail> oldVar_MailTemplate;
	
	private String roles;
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public FacilityReferenceDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FacilityReferenceDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FacilityReferenceDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
				this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("facilityReferenceDetail")) {
			this.facilityReferenceDetail = (FacilityReferenceDetail) args.get("facilityReferenceDetail");
			FacilityReferenceDetail befImage = new FacilityReferenceDetail();
			BeanUtils.copyProperties(this.facilityReferenceDetail, befImage);
			this.facilityReferenceDetail.setBefImage(befImage);

			setFacilityReferenceDetail(this.facilityReferenceDetail);
		} else {
			setFacilityReferenceDetail(null);
		}

		if (args.containsKey("facilityReference")) {
			this.facilityReference = (FacilityReference) args.get("facilityReference");
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
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FacilityReferenceDetailDialog");
		}

		// READ OVERHANDED parameters !
		// we get the facilityReferenceDetailListWindow controller. So we have
		// access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete facilityReferenceDetail here.
		if (args.containsKey("facilityReferenceDetailListCtrl")) {
			setFacilityReferenceDetailListCtrl((FacilityReferenceDetailListCtrl) args.get("facilityReferenceDetailListCtrl"));
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

		getUserWorkspace().alocateAuthorities("FacilityReferenceDetailDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnSave"));
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
	public void onClose$window_FacilityReferenceDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_FacilityReferenceDetailDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
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
			closeDialog(this.window_FacilityReferenceDetailDialog, "FacilityReferenceDetail");
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
		this.oldVar_CheckList = aFacilityReference.getCheckList();
		dofillListbox(aFacilityReference.getCheckList(), this.listBoxFinanceCheckList);
		
		this.oldVar_Agreement = aFacilityReference.getAggrementList();
		dofillListbox(aFacilityReference.getAggrementList(), this.listboxFinanceAgreementLink);
		

		
		this.oldVar_ScoringGroup = aFacilityReference.getScoringGroupList();
		dofillListbox(aFacilityReference.getScoringGroupList(), this.listBoxScoringGroup);
		
		this.oldVar_CorpScoringGroup = aFacilityReference.getCorpScoringGroupList();
		dofillListbox(aFacilityReference.getCorpScoringGroupList(), this.listBoxCorpScoringGroup);
		if(aFacilityReference.getCorpScoringGroupList()!=null && aFacilityReference.getCorpScoringGroupList().size() == 1){
			this.btnNew_FinCorpScoringGroup.setVisible(false);
		}
		
//		this.oldVar_Eligibility = aFacilityReference.getEligibilityRuleList();
//		dofillListbox(aFacilityReference.getEligibilityRuleList(), this.listBoxEligibilityRules);
//		
//		this.oldVar_AdvanceAccount = aFacilityReference.getAccountingList();
//		dofillListbox(aFacilityReference.getAccountingList(), this.listBoxAccounts);
//		
		this.oldVar_MailTemplate = aFacilityReference.getMailTemplateList();
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

		// if aFacilityReferenceDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aFacilityReference == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aFacilityReference = getFacilityReferenceDetailService().getFacilityReference("");

			setFacilityReference(aFacilityReference);
		} else {
			setFacilityReference(aFacilityReference);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFacilityReference);
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
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
			setDialog(this.window_FacilityReferenceDetailDialog);
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
	 * Stores the init values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_finRefType = this.finRefType.intValue();
		this.oldVar_finRefId = this.finRefId.longValue();
		this.oldVar_isActive = this.isActive.isChecked();
		this.oldVar_showInStage = this.showInStage.getValue();
		this.oldVar_mandInputInStage = this.mandInputInStage.getValue();
		this.oldVar_allowInputInStage = this.allowInputInStage.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();

		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finType.setValue(this.oldVar_finType);
		this.finRefType.setValue(this.oldVar_finRefType);
		this.finRefId.setValue(this.oldVar_finRefId);
		this.isActive.setChecked(this.oldVar_isActive);
		this.showInStage.setValue(this.oldVar_showInStage);
		this.mandInputInStage.setValue(this.oldVar_mandInputInStage);
		this.allowInputInStage.setValue(this.oldVar_allowInputInStage);
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

		if (this.oldVar_CheckList.size() != this.listBoxFinanceCheckList.getItemCount()) {
			return true;
		}
		if (this.oldVar_Agreement.size() != this.listboxFinanceAgreementLink.getItemCount()) {
			return true;
		}

		if (this.oldVar_ScoringGroup.size() != this.listBoxScoringGroup.getItemCount()) {
			return true;
		}
		if (this.oldVar_CorpScoringGroup.size() != this.listBoxCorpScoringGroup.getItemCount()) {
			return true;
		}
		
//		if (this.oldVar_Eligibility.size() != this.listBoxEligibilityRules.getItemCount()) {
//			return true;
//		}
//		
//		if (this.oldVar_AdvanceAccount.size() != this.listBoxAccounts.getItemCount()) {
//			return true;
//		}
		if (this.oldVar_MailTemplate.size() != this.listBoxTemplates.getItemCount()) {
			return true;
		}
		
		if (compare(this.oldVar_CheckList, this.listBoxFinanceCheckList)) {
			return true;
		} else if (compare(this.oldVar_Agreement, this.listboxFinanceAgreementLink)) {
			return true;
		} else  if (compare(this.oldVar_ScoringGroup, this.listBoxScoringGroup)) {
			return true;
		} else if (compare(this.oldVar_CorpScoringGroup, this.listBoxCorpScoringGroup)) {
			return true;
		}else if (compare(this.oldVar_MailTemplate, this.listBoxTemplates)) {
			return true;
		}
//		else if (compare(this.oldVar_Eligibility, this.listBoxEligibilityRules)) {
//			return true;
//		} else 	if (compare(this.oldVar_AdvanceAccount, this.listBoxAccounts)) {
//			return true;
//		} 
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityReferenceDetailDialog_FinType.value") }));
		}
		if (!this.finRefType.isReadonly()) {
			this.finRefType.setConstraint(new IntValidator(10, Labels.getLabel("label_FacilityReferenceDetailDialog_FinRefType.value")));
		}
		if (!this.finRefId.isReadonly()) {
			this.finRefId.setConstraint(new LongValidator(19, Labels.getLabel("label_FacilityReferenceDetailDialog_FinRefId.value")));
		}
		if (!this.showInStage.isReadonly()) {
			this.showInStage.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityReferenceDetailDialog_ShowInStage.value") }));
		}
		if (!this.mandInputInStage.isReadonly()) {
			this.mandInputInStage.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityReferenceDetailDialog_MandInputInStage.value") }));
		}
		if (!this.allowInputInStage.isReadonly()) {
			this.allowInputInStage.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityReferenceDetailDialog_AllowInputInStage.value") }));
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crude operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFacilityReferenceDetail.getRecordType()).equals("")) {
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
					closeDialog(this.window_FacilityReferenceDetailDialog, "FacilityReferenceDetail");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FacilityReferenceDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final FacilityReferenceDetail aFacilityReferenceDetail = getFacilityReferenceDetailService().getNewFacilityReferenceDetail();
		setFacilityReferenceDetail(aFacilityReferenceDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.isActive.focus();
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
		// remember the old variables
		doStoreInitValues();
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
			if (!tranType.equals("")) {
				doProcess(aFacilityReferenceDetail, tranType);
			}

		}
		
		try {
			items.clear();
			items = null;
			closeDialog(this.window_FacilityReferenceDetailDialog, "FacilityReferenceDetail");

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
	}

	private boolean doProcess(FacilityReferenceDetail aFacilityReferenceDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFacilityReferenceDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFacilityReferenceDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFacilityReferenceDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aFacilityReferenceDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFacilityReferenceDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFacilityReferenceDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aFacilityReferenceDetail))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
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

			aFacilityReferenceDetail.setTaskId(taskId);
			aFacilityReferenceDetail.setNextTaskId(nextTaskId);
			aFacilityReferenceDetail.setRoleCode(getRole());
			aFacilityReferenceDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFacilityReferenceDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aFacilityReferenceDetail);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FacilityReferenceDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FacilityReferenceDetailDialog, auditHeader);
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FacilityReferenceDetailDialog, auditHeader);
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

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("FacilityReferenceDetail");
		notes.setReference(String.valueOf(getFacilityReferenceDetail().getFinRefDetailId()));
		notes.setVersion(getFacilityReferenceDetail().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.finType.setErrorMessage("");
		this.finRefType.setErrorMessage("");
		this.finRefId.setErrorMessage("");
		this.showInStage.setErrorMessage("");
		this.mandInputInStage.setErrorMessage("");
		this.allowInputInStage.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
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

				if (facilityReferenceDetail.get(i).getFinRefType() != PennantConstants.CheckList) {
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
		callLinakgeZul(facilityReferenceDetail, PennantConstants.CheckList);
	}

	public void onClick$btnNew_FinanceAgreementLink(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, PennantConstants.Aggrement);
	}

	public void onClick$btnNew_FinanceEligibilityLink(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, PennantConstants.Eligibility);
	}

	public void onClick$btnNew_FinanceScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, PennantConstants.ScoringGroup);
	}
	
	public void onClick$btnNew_FinCorpScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, PennantConstants.CorpScoringGroup);
	}
	
	public void onClick$btnNew_FinanceAdvanceAccounting(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, PennantConstants.Accounting);
	}
	
	public void onClick$btnNew_FinanceMailTemplate(Event event) throws InterruptedException {
		callLinakgeZul(facilityReferenceDetail, PennantConstants.Template);
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
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void enableOrDisablelistitems(Listbox listbox, boolean disableItem) {
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			item.setDisabled(disableItem);
		}
	}

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
