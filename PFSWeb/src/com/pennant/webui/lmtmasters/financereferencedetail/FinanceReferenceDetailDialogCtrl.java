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
 * FileName    		:  FinanceReferenceDetailDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.financereferencedetail;

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
import com.pennant.backend.model.lmtmasters.FinanceReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.LongValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceReferenceDetail
 * /financeReferenceDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceReferenceDetailDialogCtrl extends GFCBaseListCtrl<FinanceReferenceDetail> implements Serializable {

	private static final long serialVersionUID = 4224402842313630803L;
	private final static Logger logger = Logger.getLogger(FinanceReferenceDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceReferenceDetailDialog; // auto wired
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
	private FinanceReferenceDetail financeReferenceDetail; // over handed per parameters
	private transient FinanceReferenceDetailListCtrl financeReferenceDetailListCtrl; // over handed per parameters

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
	private transient final String btnCtroller_ClassPrefix = "button_FinanceReferenceDetailDialog_";
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
	private transient FinanceReferenceDetailService financeReferenceDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	private FinanceReference financeReference;

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
	protected Listbox listBoxDedupRules;
	protected Button btnNew_FinanceDedupeLink;
	protected Listbox listBoxBlackListRules;
	protected Button btnNew_CustBlackListLink;
	protected Listbox listBoxPoliceRules;
	protected Button btnNew_CustPoliceLink;

	private transient List<FinanceReferenceDetail> oldVar_CheckList;
	private transient List<FinanceReferenceDetail> oldVar_Agreement;
	private transient List<FinanceReferenceDetail> oldVar_Eligibility;
	private transient List<FinanceReferenceDetail> oldVar_ScoringGroup;
	private transient List<FinanceReferenceDetail> oldVar_CorpScoringGroup;
	private transient List<FinanceReferenceDetail> oldVar_AdvanceAccount;
	private transient List<FinanceReferenceDetail> oldVar_MailTemplate;
	private transient List<FinanceReferenceDetail> oldVar_FinanceDedupe;
	private transient List<FinanceReferenceDetail> oldVar_BlackListDedupe;
	private transient List<FinanceReferenceDetail> oldVar_PoliceDedupe;
	
	private String roles;
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public FinanceReferenceDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceReferenceDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceReferenceDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
				this.btnClose, this.btnNotes);

		/* set components visible dependent of the users rights */
		doCheckRights();

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeReferenceDetail")) {
			this.financeReferenceDetail = (FinanceReferenceDetail) args.get("financeReferenceDetail");
			FinanceReferenceDetail befImage = new FinanceReferenceDetail();
			BeanUtils.copyProperties(this.financeReferenceDetail, befImage);
			this.financeReferenceDetail.setBefImage(befImage);

			setFinanceReferenceDetail(this.financeReferenceDetail);
		} else {
			setFinanceReferenceDetail(null);
		}

		if (args.containsKey("financeReference")) {
			this.financeReference = (FinanceReference) args.get("financeReference");
			FinanceReference befImage = new FinanceReference();
			BeanUtils.copyProperties(this.financeReference, befImage);
			setFinanceReference(this.financeReference);
			roles = financeReference.getLovDescWorkFlowRolesName();
		} else {
			setFinanceReference(null);
		}

		doLoadWorkFlow(this.financeReferenceDetail.isWorkflow(), this.financeReferenceDetail.getWorkflowId(), this.financeReferenceDetail.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceReferenceDetailDialog");
		}

		// READ OVERHANDED parameters !
		// we get the financeReferenceDetailListWindow controller. So we have
		// access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeReferenceDetail here.
		if (args.containsKey("financeReferenceDetailListCtrl")) {
			setFinanceReferenceDetailListCtrl((FinanceReferenceDetailListCtrl) args.get("financeReferenceDetailListCtrl"));
		} else {
			setFinanceReferenceDetailListCtrl(null);
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
		this.listBoxDedupRules.setHeight(listboxHeight+"px");
		this.listBoxBlackListRules.setHeight(listboxHeight+"px");
		this.listBoxPoliceRules.setHeight(listboxHeight+"px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceReference());
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

		getUserWorkspace().alocateAuthorities("FinanceReferenceDetailDialog");
		this.btnNew.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnNew")
		this.btnEdit.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnEdit")
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnSave"));
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
	public void onClose$window_FinanceReferenceDetailDialog(Event event) throws Exception {
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
		// remember the old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_FinanceReferenceDetailDialog);
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
			closeDialog(this.window_FinanceReferenceDetailDialog, "FinanceReferenceDetailDialog");
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
	 * @param aFinanceReferenceDetail
	 *            FinanceReferenceDetail
	 */
	public void doWriteBeanToComponents(FinanceReference aFinanceReference) {
		logger.debug("Entering");

		this.finType.setValue(aFinanceReference.getFinType());
		// this.finRefType.setValue(aFinanceReferenceDetail.getFinRefType());
		// this.finRefId.setValue(aFinanceReferenceDetail.getFinRefId());
		// this.isActive.setChecked(aFinanceReferenceDetail.isIsActive());
		// this.showInStage.setValue(aFinanceReferenceDetail.getShowInStage());
		// this.mandInputInStage.setValue(aFinanceReferenceDetail.getMandInputInStage());
		// this.allowInputInStage.setValue(aFinanceReferenceDetail.getAllowInputInStage());
		// this.recordStatus.setValue(aFinanceReferenceDetail.getRecordStatus());
		
		this.lovDescFinTypeDescName.setValue(aFinanceReference.getLovDescFinTypeDescName());
		this.oldVar_CheckList = aFinanceReference.getCheckList();
		dofillListbox(aFinanceReference.getCheckList(), this.listBoxFinanceCheckList);
		
		this.oldVar_Agreement = aFinanceReference.getAggrementList();
		dofillListbox(aFinanceReference.getAggrementList(), this.listboxFinanceAgreementLink);
		
		this.oldVar_Eligibility = aFinanceReference.getEligibilityRuleList();
		dofillListbox(aFinanceReference.getEligibilityRuleList(), this.listBoxEligibilityRules);
		
		this.oldVar_ScoringGroup = aFinanceReference.getScoringGroupList();
		dofillListbox(aFinanceReference.getScoringGroupList(), this.listBoxScoringGroup);
		
		this.oldVar_CorpScoringGroup = aFinanceReference.getCorpScoringGroupList();
		dofillListbox(aFinanceReference.getCorpScoringGroupList(), this.listBoxCorpScoringGroup);
		if(aFinanceReference.getCorpScoringGroupList().size() == 1){
			this.btnNew_FinCorpScoringGroup.setVisible(false);
		}
		
		this.oldVar_AdvanceAccount = aFinanceReference.getAccountingList();
		dofillListbox(aFinanceReference.getAccountingList(), this.listBoxAccounts);
		
		this.oldVar_MailTemplate = aFinanceReference.getMailTemplateList();
		dofillListbox(aFinanceReference.getMailTemplateList(), this.listBoxTemplates);
		
		this.oldVar_FinanceDedupe = aFinanceReference.getFinanceDedupeList();
		dofillListbox(aFinanceReference.getFinanceDedupeList(), this.listBoxDedupRules);
		
		this.oldVar_BlackListDedupe = aFinanceReference.getBlackListDedupeList();
		dofillListbox(aFinanceReference.getBlackListDedupeList(), this.listBoxBlackListRules);
		
		this.oldVar_PoliceDedupe = aFinanceReference.getPoliceDedupeList();
		dofillListbox(aFinanceReference.getPoliceDedupeList(), this.listBoxPoliceRules);
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceReferenceDetail
	 */
	public void doWriteComponentsToBean(FinanceReferenceDetail aFinanceReferenceDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinanceReferenceDetail.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setFinRefType(this.finRefType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setFinRefId(this.finRefId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setIsActive(this.isActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setShowInStage(this.showInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setMandInputInStage(this.mandInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setAllowInputInStage(this.allowInputInStage.getValue());
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

		aFinanceReferenceDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceReferenceDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceReference aFinanceReference) throws InterruptedException {
		logger.debug("Entering");

		// if aFinanceReferenceDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aFinanceReference == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aFinanceReference = getFinanceReferenceDetailService().getFinanceReference("");

			setFinanceReference(aFinanceReference);
		} else {
			setFinanceReference(aFinanceReference);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceReference);
			// stores the initial data for comparing if they are changed
			this.isActive.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				//this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				btnSave.setVisible(true);
	
			}
			
			// during user action.
			doStoreInitValues();
			setDialog(this.window_FinanceReferenceDetailDialog);
			
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
		if (this.oldVar_Eligibility.size() != this.listBoxEligibilityRules.getItemCount()) {
			return true;
		}
		if (this.oldVar_ScoringGroup.size() != this.listBoxScoringGroup.getItemCount()) {
			return true;
		}
		if (this.oldVar_CorpScoringGroup.size() != this.listBoxCorpScoringGroup.getItemCount()) {
			return true;
		}
		if (this.oldVar_AdvanceAccount.size() != this.listBoxAccounts.getItemCount()) {
			return true;
		}
		if (this.oldVar_MailTemplate.size() != this.listBoxTemplates.getItemCount()) {
			return true;
		}
		if (this.oldVar_FinanceDedupe.size() != this.listBoxDedupRules.getItemCount()) {
			return true;
		}
		if (this.oldVar_BlackListDedupe.size() != this.listBoxBlackListRules.getItemCount()) {
			return true;
		}
		if (this.oldVar_PoliceDedupe.size() != this.listBoxPoliceRules.getItemCount()) {
			return true;
		}
		if (compare(this.oldVar_CheckList, this.listBoxFinanceCheckList)) {
			return true;
		} else if (compare(this.oldVar_Agreement, this.listboxFinanceAgreementLink)) {
			return true;
		} else if (compare(this.oldVar_Eligibility, this.listBoxEligibilityRules)) {
			return true;
		} else if (compare(this.oldVar_ScoringGroup, this.listBoxScoringGroup)) {
			return true;
		} else if (compare(this.oldVar_CorpScoringGroup, this.listBoxCorpScoringGroup)) {
			return true;
		} else if (compare(this.oldVar_AdvanceAccount, this.listBoxAccounts)) {
			return true;
		} else if (compare(this.oldVar_MailTemplate, this.listBoxTemplates)) {
			return true;
		} else if (compare(this.oldVar_FinanceDedupe, this.listBoxDedupRules)) {
			return true;
		} else if (compare(this.oldVar_BlackListDedupe, this.listBoxBlackListRules)) {
			return true;
		} else if (compare(this.oldVar_PoliceDedupe, this.listBoxPoliceRules)) {
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

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value"),null,true));
		}
		if (!this.finRefType.isReadonly()) {
			this.finRefType.setConstraint(new IntValidator(10, Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefType.value")));
		}
		if (!this.finRefId.isReadonly()) {
			this.finRefId.setConstraint(new LongValidator(19, Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefId.value")));
		}
		if (!this.showInStage.isReadonly()) {
			this.showInStage.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_ShowInStage.value"),null,true));
		}
		if (!this.mandInputInStage.isReadonly()) {
			this.mandInputInStage.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_MandInputInStage.value"),null,true));
		}
		if (!this.allowInputInStage.isReadonly()) {
			this.allowInputInStage.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_AllowInputInStage.value"),null,true));
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
	 * Deletes a FinanceReferenceDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinanceReferenceDetail aFinanceReferenceDetail = new FinanceReferenceDetail();
		BeanUtils.copyProperties(getFinanceReferenceDetail(), aFinanceReferenceDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceReferenceDetail.getFinRefDetailId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceReferenceDetail.getRecordType()).equals("")) {
				aFinanceReferenceDetail.setVersion(aFinanceReferenceDetail.getVersion() + 1);
				aFinanceReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinanceReferenceDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFinanceReferenceDetail, tranType)) {
					// refreshList();
					closeDialog(this.window_FinanceReferenceDetailDialog, "FinanceReferenceDetailDialog");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinanceReferenceDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old variables
		doStoreInitValues();

		final FinanceReferenceDetail aFinanceReferenceDetail = getFinanceReferenceDetailService().getNewFinanceReferenceDetail();
		setFinanceReferenceDetail(aFinanceReferenceDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.isActive.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinanceReferenceDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.finType.setReadonly(true);
		this.finRefType.setReadonly(isReadOnly("FinanceReferenceDetailDialog_finRefType"));
		this.finRefId.setReadonly(isReadOnly("FinanceReferenceDetailDialog_finRefId"));
		this.isActive.setDisabled(isReadOnly("FinanceReferenceDetailDialog_isActive"));
		this.showInStage.setReadonly(isReadOnly("FinanceReferenceDetailDialog_showInStage"));
		this.mandInputInStage.setReadonly(isReadOnly("FinanceReferenceDetailDialog_mandInputInStage"));
		this.allowInputInStage.setReadonly(isReadOnly("FinanceReferenceDetailDialog_allowInputInStage"));

		this.btnNew_FinanceCheckList.setDisabled(false);
		this.btnNew_FinanceAgreementLink.setDisabled(false);
		this.btnNew_FinanceEligibilityLink.setDisabled(false);
		this.btnNew_FinanceScoringGroup.setDisabled(false);
		this.btnNew_FinCorpScoringGroup.setDisabled(false);
		this.btnNew_FinanceAdvanceAccounting.setDisabled(false);
		this.btnNew_FinanceMailTemplate.setDisabled(false);
		this.btnNew_FinanceDedupeLink.setDisabled(false);
		this.btnNew_CustBlackListLink.setDisabled(false);
		this.btnNew_CustPoliceLink.setDisabled(false);

		enableOrDisablelistitems(this.listBoxFinanceCheckList, false);
		enableOrDisablelistitems(this.listboxFinanceAgreementLink, false);
		enableOrDisablelistitems(this.listBoxEligibilityRules, false);
		enableOrDisablelistitems(this.listBoxScoringGroup, false);
		enableOrDisablelistitems(this.listBoxCorpScoringGroup, false);
		enableOrDisablelistitems(this.listBoxAccounts, false);
		enableOrDisablelistitems(this.listBoxTemplates, false);
		enableOrDisablelistitems(this.listBoxDedupRules, false);
		enableOrDisablelistitems(this.listBoxBlackListRules, false);
		enableOrDisablelistitems(this.listBoxPoliceRules, false);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeReferenceDetail.isNewRecord()) {
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

		final FinanceReferenceDetail aFinanceReferenceDetail = new FinanceReferenceDetail();
		List<Listitem> items = new ArrayList<Listitem>();
		items.addAll(this.listBoxFinanceCheckList.getItems());
		items.addAll(this.listboxFinanceAgreementLink.getItems());
		items.addAll(this.listBoxEligibilityRules.getItems());
		items.addAll(this.listBoxScoringGroup.getItems());
		items.addAll(this.listBoxCorpScoringGroup.getItems());
		items.addAll(this.listBoxAccounts.getItems());
		items.addAll(this.listBoxTemplates.getItems());
		items.addAll(this.listBoxDedupRules.getItems());
		items.addAll(this.listBoxBlackListRules.getItems());
		items.addAll(this.listBoxPoliceRules.getItems());
		
		for (int i = 0; i < items.size(); i++) {
			FinanceReferenceDetail lsFinanceReferenceDetail = (FinanceReferenceDetail) items.get(i).getAttribute("data");
			setFinanceReferenceDetail(lsFinanceReferenceDetail);
			BeanUtils.copyProperties(getFinanceReferenceDetail(), aFinanceReferenceDetail);
			boolean isNew = false;
			isNew = aFinanceReferenceDetail.isNew();
			String tranType = "";
			aFinanceReferenceDetail.setVersion(aFinanceReferenceDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				if (aFinanceReferenceDetail.getRecordType().equals(PennantConstants.RCD_DEL)) {
					tranType = PennantConstants.TRAN_DEL;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			if (aFinanceReferenceDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
				tranType = "";
			}
			// save it to database
			aFinanceReferenceDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			aFinanceReferenceDetail.setRecordType(PennantConstants.RCD_ADD);
			if (!tranType.equals("")) {
				doProcess(aFinanceReferenceDetail, tranType);
			}

		}
		
		try {
			items.clear();
			items = null;
			closeDialog(this.window_FinanceReferenceDetailDialog, "FinanceReferenceDetailDialog");

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
	}

	private boolean doProcess(FinanceReferenceDetail aFinanceReferenceDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceReferenceDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceReferenceDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceReferenceDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aFinanceReferenceDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceReferenceDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceReferenceDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aFinanceReferenceDetail))) {
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

			aFinanceReferenceDetail.setTaskId(taskId);
			aFinanceReferenceDetail.setNextTaskId(nextTaskId);
			aFinanceReferenceDetail.setRoleCode(getRole());
			aFinanceReferenceDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceReferenceDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aFinanceReferenceDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceReferenceDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aFinanceReferenceDetail, tranType);
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

		FinanceReferenceDetail aFinanceReferenceDetail = (FinanceReferenceDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceReferenceDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceReferenceDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceReferenceDetailService().doApprove(auditHeader);

						if (aFinanceReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceReferenceDetailService().doReject(auditHeader);
						if (aFinanceReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceReferenceDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceReferenceDetailDialog, auditHeader);
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

	public FinanceReferenceDetail getFinanceReferenceDetail() {
		return this.financeReferenceDetail;
	}

	public void setFinanceReferenceDetail(FinanceReferenceDetail financeReferenceDetail) {
		this.financeReferenceDetail = financeReferenceDetail;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return this.financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailListCtrl(FinanceReferenceDetailListCtrl financeReferenceDetailListCtrl) {
		this.financeReferenceDetailListCtrl = financeReferenceDetailListCtrl;
	}

	public FinanceReferenceDetailListCtrl getFinanceReferenceDetailListCtrl() {
		return this.financeReferenceDetailListCtrl;
	}

	private AuditHeader getAuditHeader(FinanceReferenceDetail aFinanceReferenceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceReferenceDetail.getBefImage(), aFinanceReferenceDetail);
		return new AuditHeader(String.valueOf(aFinanceReferenceDetail.getFinRefDetailId()), null, null, null, auditDetail, aFinanceReferenceDetail.getUserDetails(),
				getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinanceReferenceDetailDialog, auditHeader);
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
		notes.setModuleName("FinanceReferenceDetail");
		notes.setReference(String.valueOf(getFinanceReferenceDetail().getFinRefDetailId()));
		notes.setVersion(getFinanceReferenceDetail().getVersion());
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

	public FinanceReference getFinanceReference() {
		return financeReference;
	}

	public void setFinanceReference(FinanceReference financeReference) {
		this.financeReference = financeReference;
	}

	// ===================

	public void dofillListbox(List<FinanceReferenceDetail> financeReferenceDetail, Listbox listbox) {

		if (financeReferenceDetail != null) {
			for (int i = 0; i < financeReferenceDetail.size(); i++) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(financeReferenceDetail.get(i).getFinRefId()));
				lc.setParent(item);

				if (financeReferenceDetail.get(i).getFinRefType() != PennantConstants.CheckList) {
					lc = new Listcell(financeReferenceDetail.get(i).getLovDescCodelov());
					lc.setParent(item);
					lc = new Listcell(financeReferenceDetail.get(i).getLovDescNamelov());
					lc.setParent(item);
				}

				lc = new Listcell(financeReferenceDetail.get(i).getLovDescRefDesc());
				lc.setParent(item);

				String active = "N";
				if (financeReferenceDetail.get(i).isIsActive()) {
					active = "Y";
				}
				lc = new Listcell(active);
				lc.setParent(item);
				lc = new Listcell(financeReferenceDetail.get(i).getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(financeReferenceDetail.get(i).getRecordType());
				lc.setParent(item);
				item.setAttribute("data", financeReferenceDetail.get(i));

				if (financeReferenceDetail.get(i).getRecordType().equals(PennantConstants.RCD_DEL)) {
					item.setVisible(false);
				}
				listbox.appendChild(item);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCheckListItemDoubleClicked");
			}

		}

	}

	public void onClick$btnNew_FinanceCheckList(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.CheckList);
	}

	public void onClick$btnNew_FinanceAgreementLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.Aggrement);
	}

	public void onClick$btnNew_FinanceEligibilityLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.Eligibility);
	}

	public void onClick$btnNew_FinanceScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.ScoringGroup);
	}
	
	public void onClick$btnNew_FinCorpScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.CorpScoringGroup);
	}
	
	public void onClick$btnNew_FinanceAdvanceAccounting(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.Accounting);
	}
	
	public void onClick$btnNew_FinanceMailTemplate(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.Template);
	}
	
	public void onClick$btnNew_FinanceDedupeLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.FinanceDedupe);
	}
	
	public void onClick$btnNew_CustBlackListLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.BlackListDedupe);
	}
	
	public void onClick$btnNew_CustPoliceLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, PennantConstants.PoliceDedupe);
	}

	public void onCheckListItemDoubleClicked(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		// Get the event target
		Listitem item = (Listitem) event.getOrigin().getTarget();
		
		FinanceReferenceDetail itemdata = (FinanceReferenceDetail) item.getAttribute("data");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCodeList", roles);
		map.put("financeReferenceDetail", itemdata);
		map.put("financeReferenceDetailDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailDialogLink.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");

	}

	private void callLinakgeZul(FinanceReferenceDetail financeReferenceDetail, int type) throws InterruptedException {
		logger.debug("Entering");
		
		financeReferenceDetail.setFinType(this.finType.getValue());
		financeReferenceDetail.setNewRecord(true);
		financeReferenceDetail.setLovDescNamelov("");
		financeReferenceDetail.setLovDescRefDesc("");
		financeReferenceDetail.setFinRefType(type);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCodeList", roles);
		map.put("financeReferenceDetail", financeReferenceDetail);
		map.put("financeReferenceDetailDialogCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailDialogLink.zul", null, map);
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

	private boolean compare(List<FinanceReferenceDetail> referenceDetails, Listbox listbox) {
		for (int i = 0; i < referenceDetails.size(); i++) {
			FinanceReferenceDetail oldFinrefDet = referenceDetails.get(i);
			Listitem item = (Listitem) listbox.getItems().get(i);
			FinanceReferenceDetail newFinrefDet = (FinanceReferenceDetail) item.getAttribute("data");
			if (oldFinrefDet.getRecordType() != newFinrefDet.getRecordType()) {
				return true;
			}
		}
		return false;
	}

}
