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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.FinServicingEvent;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.lmtmasters.FinanceReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.delegationdeviation.DeviationConfigCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceReferenceDetail
 * /financeReferenceDetailDialog.zul file.
 */
public class FinanceReferenceDetailDialogCtrl extends GFCBaseCtrl<FinanceReferenceDetail> {
	private static final long serialVersionUID = 4224402842313630803L;
	private static final Logger logger = Logger.getLogger(FinanceReferenceDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinanceReferenceDetailDialog; 	// autoWired
	protected Textbox 		finType; 								// autoWired
	protected Intbox 		finRefType; 							// autoWired
	protected Longbox 		finRefId; 								// autoWired
	protected Checkbox 		isActive; 								// autoWired
	protected Textbox 		showInStage; 							// autoWired
	protected Textbox 		mandInputInStage; 						// autoWired
	protected Textbox 		allowInputInStage; 						// autoWired
	protected Row 			row_finEvent; 							// autoWired
	protected Combobox 		finEvent; 								// autoWired


	// not autoWired variables
	private FinanceReferenceDetail financeReferenceDetail; // over handed per parameters
	private transient FinanceReferenceDetailListCtrl financeReferenceDetailListCtrl; // over handed per parameters

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient FinanceReferenceDetailService financeReferenceDetailService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

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
	protected Listbox listBoxCustDedupRules;
	protected Button btnNew_CustomerDedupeLink;
	protected Listbox listBoxBlackListRules;
	protected Button btnNew_CustBlackListLink;
	protected Listbox listBoxPoliceRules;
	protected Button btnNew_CustPoliceLink;
	protected Listbox listBoxLimitService;
	protected Button btnNew_LimitServiceLink;
	protected Listbox listBoxTatNotification;
	protected Button btnNew_TatNotificationLink;
	protected Listbox listBoxReturnCheques;
	protected Button btnNew_ReturnChequeLink;
	protected Button btnNew_FinanceTabs;
	protected Listbox listboxFinanceTabs;
	
	// Tab Details 
	protected Tab tabFinanceCheckList;
	protected Tab tabFinanceAgreement;
	protected Tab tabFinanceEligibility;
	protected Tab tabFinanceScoring;
	protected Tab tabFinanceAccounting;
	protected Tab tabFinanceMailTemplate;
	protected Tab tabFinanceDedupe;
	protected Tab tabCustomerDedupAll;
	protected Tab tabDeviation;
	protected Tab tabCustLimitCheck;	
	protected Tab tabTatNotification;	
	protected Tab tabFinanceTabs;	
	
/*	private transient List<FinanceReferenceDetail> oldVar_CheckList;
	private transient List<FinanceReferenceDetail> oldVar_Agreement;
	private transient List<FinanceReferenceDetail> oldVar_Eligibility;
	private transient List<FinanceReferenceDetail> oldVar_ScoringGroup;
	private transient List<FinanceReferenceDetail> oldVar_CorpScoringGroup;
	private transient List<FinanceReferenceDetail> oldVar_AdvanceAccount;
	private transient List<FinanceReferenceDetail> oldVar_MailTemplate;
	private transient List<FinanceReferenceDetail> oldVar_FinanceDedupe;
	private transient List<FinanceReferenceDetail> oldVar_CustomerDedupe;
	private transient List<FinanceReferenceDetail> oldVar_BlackListDedupe;
	private transient List<FinanceReferenceDetail> oldVar_PoliceDedupe;
	private transient List<FinanceReferenceDetail> oldVar_ReturnChq;
	private transient List<FinanceReferenceDetail> oldVar_LimitCodeDetail;
	private transient List<FinanceReferenceDetail> oldVar_TatNotification;*/
	
	private String roles;
	int listRows;
	private String eventAction = "";
	private String moduleName = "";
	private boolean isOverDraft = false;
	
	protected Listbox delationDeviation;
	@Autowired
	private DeviationConfigCtrl deviationConfigCtrl;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceReferenceDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceReferenceDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceReferenceDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceReferenceDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceReferenceDetailDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("financeReferenceDetail")) {
				this.financeReferenceDetail = (FinanceReferenceDetail) arguments
						.get("financeReferenceDetail");
				FinanceReferenceDetail befImage = new FinanceReferenceDetail();
				BeanUtils.copyProperties(this.financeReferenceDetail, befImage);
				this.financeReferenceDetail.setBefImage(befImage);

				setFinanceReferenceDetail(this.financeReferenceDetail);
			} else {
				setFinanceReferenceDetail(null);
			}

			if (arguments.containsKey("financeReference")) {
				this.financeReference = (FinanceReference) arguments
						.get("financeReference");
				FinanceReference befImage = new FinanceReference();
				BeanUtils.copyProperties(this.financeReference, befImage);
				setFinanceReference(this.financeReference);
				roles = financeReference.getLovDescWorkFlowRolesName();
			} else {
				setFinanceReference(null);
			}
			
			// Event Name 
			if (arguments.containsKey("eventAction")) {
				eventAction = (String) arguments.get("eventAction");
			}
			
			// Module Name 
			if (arguments.containsKey("moduleName")) {
				moduleName = (String) arguments.get("moduleName");
			}
			// Product Category
			if (arguments.containsKey("isOverDraft")) {
				isOverDraft = (boolean) arguments.get("isOverDraft");
			}

			doLoadWorkFlow(this.financeReferenceDetail.isWorkflow(),
					this.financeReferenceDetail.getWorkflowId(),
					this.financeReferenceDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"FinanceReferenceDetailDialog");
			}

			// READ OVERHANDED parameters !
			// we get the financeReferenceDetailListWindow controller. So we
			// have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete financeReferenceDetail here.
			if (arguments.containsKey("financeReferenceDetailListCtrl")) {
				setFinanceReferenceDetailListCtrl((FinanceReferenceDetailListCtrl) arguments
						.get("financeReferenceDetailListCtrl"));
			} else {
				setFinanceReferenceDetailListCtrl(null);
			}

			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows()
					.getVisibleItemCount() * 20 + 100 + 25;
			int listboxHeight = borderLayoutHeight - dialogHeight;

			this.listboxFinanceAgreementLink.setHeight(listboxHeight + "px");
			this.listBoxEligibilityRules.setHeight(listboxHeight + "px");
			this.listBoxFinanceCheckList.setHeight(listboxHeight + "px");
			this.listBoxScoringGroup.setHeight(listboxHeight + "px");
			this.listBoxCorpScoringGroup.setHeight(listboxHeight + "px");
			this.listBoxAccounts.setHeight(listboxHeight + "px");
			this.listBoxTemplates.setHeight(listboxHeight + "px");
			this.listBoxDedupRules.setHeight(listboxHeight + "px");
			this.listBoxCustDedupRules.setHeight(listboxHeight + "px");
			this.listBoxBlackListRules.setHeight(listboxHeight + "px");
			this.listBoxPoliceRules.setHeight(listboxHeight + "px");
			this.listBoxReturnCheques.setHeight(listboxHeight + "px");
			this.delationDeviation.setHeight((listboxHeight+80) + "px");
			this.listBoxLimitService.setHeight((listboxHeight+80) + "px");
			this.listBoxTatNotification.setHeight((listboxHeight+80) + "px");
			this.listboxFinanceTabs.setHeight((listboxHeight+80) + "px");
			
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceReference());
			this.btnDelete.setVisible(false);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceReferenceDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finRefType.setMaxlength(10);
		deviationConfigCtrl.setFinFormatter(CurrencyUtil.getFormat(""));

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
		this.btnNew.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnNew")
		this.btnEdit.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnEdit")
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnSave"));
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
		logger.debug("Entering"+event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering"+event.toString());
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
		logger.debug("Entering"+event.toString());
		MessageUtil.showHelpWindow(event, window_FinanceReferenceDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering"+event.toString());
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
		doWriteBeanToComponents(this.financeReference.getBefImage());
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
		
		List<FinServicingEvent> events;
		if(StringUtils.equals(eventAction, FinanceConstants.FINSER_EVENT_ORG)){
			events = PennantStaticListUtil.getFinServiceEvents(false);
		}else{
			events = PennantStaticListUtil.getFinServiceEvents(true);
		}

		List<ValueLabel> list = PennantStaticListUtil.getValueLabels(events);

		this.finType.setValue(aFinanceReference.getFinType());
		fillComboBox(this.finEvent, aFinanceReference.getFinEvent(), list, "");
		// this.finRefType.setValue(aFinanceReferenceDetail.getFinRefType());
		// this.finRefId.setValue(aFinanceReferenceDetail.getFinRefId());
		// this.isActive.setChecked(aFinanceReferenceDetail.isIsActive());
		// this.showInStage.setValue(aFinanceReferenceDetail.getShowInStage());
		// this.mandInputInStage.setValue(aFinanceReferenceDetail.getMandInputInStage());
		// this.allowInputInStage.setValue(aFinanceReferenceDetail.getAllowInputInStage());
		// this.recordStatus.setValue(aFinanceReferenceDetail.getRecordStatus());
		deviationConfigCtrl.fillProductDeviations();
		
		this.lovDescFinTypeDescName.setValue(aFinanceReference.getLovDescFinTypeDescName());
		dofillListbox(aFinanceReference.getCheckList(), this.listBoxFinanceCheckList);
		
		dofillListbox(aFinanceReference.getAggrementList(), this.listboxFinanceAgreementLink);
		
		dofillListbox(aFinanceReference.getEligibilityRuleList(), this.listBoxEligibilityRules);
		
		dofillListbox(aFinanceReference.getScoringGroupList(), this.listBoxScoringGroup);
		
		dofillListbox(aFinanceReference.getCorpScoringGroupList(), this.listBoxCorpScoringGroup);
		if(aFinanceReference.getCorpScoringGroupList().size() == 1){
			this.btnNew_FinCorpScoringGroup.setVisible(false);
		}
		
		dofillListbox(aFinanceReference.getAccountingList(), this.listBoxAccounts);
		
		dofillListbox(aFinanceReference.getMailTemplateList(), this.listBoxTemplates);
		
		dofillListbox(aFinanceReference.getFinanceDedupeList(), this.listBoxDedupRules);
		
		dofillListbox(aFinanceReference.getCustomerDedupeList(), this.listBoxCustDedupRules);
		
		dofillListbox(aFinanceReference.getBlackListDedupeList(), this.listBoxBlackListRules);
		
		dofillListbox(aFinanceReference.getPoliceDedupeList(), this.listBoxPoliceRules);
		
		dofillListbox(aFinanceReference.getReturnChequeList(), this.listBoxReturnCheques);
		
		dofillListbox(aFinanceReference.getLimitCodeDetailList(), this.listBoxLimitService);
		
		dofillListbox(aFinanceReference.getTatNotificationList(), this.listBoxTatNotification);
		
		dofillListbox(aFinanceReference.getFinanceTabsList(), this.listboxFinanceTabs);
		
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
	 * @throws Exception
	 */
	public void doShowDialog(FinanceReference aFinanceReference) throws Exception {
		logger.debug("Entering");

		try {
			//To get delegating authorities
			WorkflowEngine workflow = new WorkflowEngine(WorkFlowUtil.getDetailsByType(getFinanceReference().getWorkFlowType()).getWorkFlowXml());
			deviationConfigCtrl.init(this.delationDeviation, aFinanceReference.getFinType(),
					workflow.getActors(true));
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
			 doCheckEvent();			
		
			setDialog(DialogType.EMBEDDED);
			
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceReferenceDetailDialog.onClose();
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

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value"),null,true));
		}
		if (!this.finRefType.isReadonly()) {
			this.finRefType.setConstraint(new PTNumberValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefType.value"), true));
		}
		if (!this.finRefId.isReadonly()) {
			this.finRefId.setConstraint(new PTNumberValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefId.value"), true));
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

	// CRUD operations

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
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinanceReferenceDetail.getRecordType())) {
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
		this.btnNew_CustomerDedupeLink.setDisabled(false);
		this.btnNew_CustBlackListLink.setDisabled(false);
		this.btnNew_CustPoliceLink.setDisabled(false);
		this.btnNew_ReturnChequeLink.setDisabled(false);

		enableOrDisablelistitems(this.listBoxFinanceCheckList, false);
		enableOrDisablelistitems(this.listboxFinanceAgreementLink, false);
		enableOrDisablelistitems(this.listBoxEligibilityRules, false);
		enableOrDisablelistitems(this.listBoxScoringGroup, false);
		enableOrDisablelistitems(this.listBoxCorpScoringGroup, false);
		enableOrDisablelistitems(this.listBoxAccounts, false);
		enableOrDisablelistitems(this.listBoxTemplates, false);
		enableOrDisablelistitems(this.listBoxDedupRules, false);
		enableOrDisablelistitems(this.listBoxCustDedupRules, false);
		enableOrDisablelistitems(this.listBoxBlackListRules, false);
		enableOrDisablelistitems(this.listBoxPoliceRules, false);
		enableOrDisablelistitems(this.listBoxReturnCheques, false);
		enableOrDisablelistitems(this.listboxFinanceTabs, false);
		
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
	 * 
	 */
	private void doCheckEvent(){
		logger.debug(" Entering ");
		if((!StringUtils.equals(eventAction, FinanceConstants.FINSER_EVENT_ORG) && 
				!StringUtils.equals(eventAction, FinanceConstants.FINSER_EVENT_PREAPPROVAL)) || 
				StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COLLATERAL) ||
				StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_VAS) ||
				StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COMMITMENT)){
			this.tabFinanceEligibility.setVisible(false);
			this.tabFinanceScoring.setVisible(false);
			this.tabCustomerDedupAll.setVisible(false);
			this.tabDeviation.setVisible(false);
			this.tabFinanceDedupe.setVisible(false);
			this.tabCustLimitCheck.setVisible(false);
			this.tabTatNotification.setVisible(false);
			this.tabFinanceTabs.setVisible(false);
			
			if(StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COLLATERAL) ||
					StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_VAS) ||
					StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COMMITMENT)){
				this.tabFinanceAccounting.setVisible(false);
			}
		}
		
		if (StringUtils.equals(eventAction, FinanceConstants.FINSER_EVENT_PREAPPROVAL)) {
			this.tabDeviation.setVisible(false);
			this.tabFinanceTabs.setVisible(false);
		}
		if (StringUtils.equals(eventAction, FinanceConstants.FINSER_EVENT_ADDDISB)) {
			this.tabCustLimitCheck.setVisible(true);
		}
		if(isOverDraft){
			this.tabCustLimitCheck.setVisible(false);
		}
		logger.debug(" Leaving ");

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

	
	@SuppressWarnings("unchecked")
	private boolean validateFeeAccounting(){
		List<FinanceReferenceDetail> finReferenceDetails = new ArrayList<FinanceReferenceDetail>();
		if(this.listBoxAccounts.getItems() != null && !this.listBoxAccounts.getItems().isEmpty()){
			for (Listitem listitem : this.listBoxAccounts.getItems()) {
				FinanceReferenceDetail fReferenceDetail = (FinanceReferenceDetail) listitem.getAttribute("data");
				finReferenceDetails.add(fReferenceDetail);
			}
		}

		List<Long> accSetIdList = new ArrayList<Long>();
		for (FinanceReferenceDetail finReferenceDetail : finReferenceDetails) {
			if(finReferenceDetail.getFinRefType() == FinanceConstants.PROCEDT_STAGEACC){
				accSetIdList.add(finReferenceDetail.getFinRefId());
			}
		}
		
		List<Long> finTypeAccSetIds =  getFinanceReferenceDetailService().getFinTypeAccounting(this.finType.getValue(), getEventCodes(true));
		if(finTypeAccSetIds != null && !finTypeAccSetIds.isEmpty()){
			accSetIdList.addAll(finTypeAccSetIds);
		}
		
		Map<String,String>  accountingFeeMap =  new HashMap<String,String>();
		if(!accSetIdList.isEmpty()){
			accountingFeeMap = getFinanceReferenceDetailService().getAccountingFeeCodes(accSetIdList);
		}

		List<FinTypeFees> finTypeFeesList =  getFinanceReferenceDetailService().getFinTypeFeesList(this.finType.getValue(), getEventCodes(true), "_AView", FinanceConstants.MODULEID_FINTYPE);
		
		if(finTypeFeesList != null && !finTypeFeesList.isEmpty()){
			Comparator<FinTypeFees> beanComp = new BeanComparator("finEvent");
			Collections.sort(finTypeFeesList, beanComp);
			Map<String,String> finTypeFeeMap = new HashMap<String,String>();
			for (FinTypeFees finTypeFeeTemp : finTypeFeesList) {
				if(finTypeFeeMap.containsKey(finTypeFeeTemp.getFinEvent())){
					String feeCodes = finTypeFeeMap.get(finTypeFeeTemp.getFinEvent());
					feeCodes = feeCodes+","+finTypeFeeTemp.getFeeTypeCode();
					finTypeFeeMap.put(finTypeFeeTemp.getFinEvent(), feeCodes);
				}else{
					finTypeFeeMap.put(finTypeFeeTemp.getFinEvent(), finTypeFeeTemp.getFeeTypeCode());
				}
			}
			if(validateFees(finTypeFeeMap, accountingFeeMap, true)){
				if(!validateFees(accountingFeeMap, finTypeFeeMap, false)){
					return false;	
				}
			}else{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This Method checks TV and Sampling Tabs are assigned to same loan Type or not. <br>
	 * if assigned display error message.
	 * 
	 */
	private boolean validateSamplingAndTV() {

		List<String> tabs = new ArrayList<>();

		// get the tabs
		for (Listitem initListItem : listBoxLimitService.getItems()) {
			FinanceReferenceDetail financeReferenceDetail = (FinanceReferenceDetail) initListItem.getAttribute("data");
			if (!financeReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tabs.add(financeReferenceDetail.getLovDescNamelov());
			}
		}
		
		return false;
	}

	private Boolean validateStages(String initId, String approvalId, VerificationType vrfType)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);
		String type;
		if (vrfType != null) {
			type = vrfType.toString();
		} else {
			type = "sampling";
		}
		// Get the Workflow details.
		WorkFlowDetails workflow = WorkFlowUtil.getDetailsByType(financeReference.getWorkFlowType());
		// Workflow Engine
		WorkflowEngine engine = new WorkflowEngine(workflow.getWorkFlowXml());
		String initStages[] = null;
		String apprStage = null;

		// get the Initiation Stages and Approval Stage
		for (Listitem initListItem : listBoxLimitService.getItems()) {
			FinanceReferenceDetail financeReferenceDetail = (FinanceReferenceDetail) initListItem.getAttribute("data");
			if (!financeReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.equals(financeReferenceDetail.getLovDescNamelov(), initId)) {
					initStages = financeReferenceDetail.getMandInputInStage().split(",");
				} else if (StringUtils.equals(financeReferenceDetail.getLovDescNamelov(), approvalId)) {
					apprStage = financeReferenceDetail.getMandInputInStage().replace(",", "");
				}
			}
		}

		// Check whether the Both Initiation and Approval Stages should be
		// Mentioned.
		if (initStages != null && apprStage != null) {
			for (String fiInitStage : initStages) {
				String task = engine.getUserTaskId(fiInitStage);
				String nextTask = engine.getUserTaskId(apprStage);

				// Check whether the Approval Stage is a Successor to the Initiation Stage.
				if (engine.compareTo(task, nextTask) != Flow.SUCCESSOR) {
					MessageUtil.showError(type + " initiation stage must be predecessors to the " + type
							+ " approval stage in miscellaneous tab.");
					tabCustLimitCheck.setSelected(true);
					return false;
				}
			}
		} else if ((initStages != null && apprStage == null) || (initStages == null && apprStage != null)) {
			MessageUtil.showError("Either both " + type + " initiation & " + type
					+ " approval stages should be saved or none of them.");
			tabCustLimitCheck.setSelected(true);
			return false;
		}

		logger.debug(Literal.LEAVING);
		return true;

	}

	private boolean validateFees(Map<String,String> sourceFeeMap,Map<String,String>  destFeeMap,boolean isFinTypeFeesValidate){

		List<ErrorDetail> feeErrorDetails = new ArrayList<ErrorDetail>();

		for (String finEvent : sourceFeeMap.keySet()) {
			String sourceFeeCodes = sourceFeeMap.get(finEvent);
			
			if(!isFinTypeFeesValidate && StringUtils.equals(finEvent, AccountEventConstants.ACCEVENT_STAGE)){
				continue;
			}
			
			String missedFees = getMissedFees(sourceFeeCodes, finEvent, destFeeMap);
			
			if(StringUtils.isNotEmpty(missedFees)){
				if(isFinTypeFeesValidate){
					String[] errParm = new String[3];
					String[] valueParm = new String[3];
					valueParm[0] = finEvent;
					errParm[0] = PennantJavaUtil.getLabel("label_Fees_FinEvent") + ":" + valueParm[0];
					valueParm[1] = missedFees;
					errParm[1] = PennantJavaUtil.getLabel("FeeTypes") + ":" + valueParm[1];
					errParm[2] = PennantJavaUtil.getLabel("FinanceType_label");
					feeErrorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail
							(PennantConstants.KEY_FIELD, "WFEE06", errParm, valueParm), getUserWorkspace().getLoggedInUser().getLanguage()));
				}else{
					String[] errParm = new String[3];
					String[] valueParm = new String[3];
					valueParm[0] = finEvent;
					errParm[0] = PennantJavaUtil.getLabel("label_Fees_FinEvent") + ":" + valueParm[0];
					errParm[1] = PennantJavaUtil.getLabel("FinanceType_label");
					valueParm[2] = missedFees;
					errParm[2] = PennantJavaUtil.getLabel("FeeTypes") + ":" + valueParm[2];
					feeErrorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail
							(PennantConstants.KEY_FIELD, "WFEE07", errParm, valueParm), getUserWorkspace().getLoggedInUser().getLanguage()));
				}
			}
		}
		if(feeErrorDetails != null && !feeErrorDetails.isEmpty()){
			String errorMsg = "";
			String warningMsg = "";
			int errorCount = 0;
			int warningCount = 0;
			for (ErrorDetail errorDetail : feeErrorDetails) {
				if (errorDetail.getSeverity().equalsIgnoreCase(PennantConstants.ERR_SEV_ERROR)){
					errorCount++;
					if(StringUtils.isEmpty(errorMsg)){
						errorMsg = errorCount+")"+errorDetail.getError();
					}else{
						errorMsg = errorMsg + " \n  \n "+errorCount+")"+errorDetail.getError();
					}
				}else{
					warningCount++;
					if(StringUtils.isEmpty(warningMsg)){
						warningMsg = warningCount+")"+errorDetail.getError();
					}else{
						warningMsg = warningMsg + " \n \n "+warningCount+")"+errorDetail.getError();
					}
				}
			}
			if (StringUtils.isNotEmpty(errorMsg)){
				MessageUtil.showError(errorMsg);
				return false;
			}else if(StringUtils.isNotEmpty(warningMsg)){
				warningMsg = warningMsg + "\n\n" + "Do you want to proceed?";

				if (MessageUtil.confirm(warningMsg) != MessageUtil.YES) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	private String getMissedFees(String sourcefeeCodes,String finEventTemp,Map<String,String>  destFeeMap){
		String feesMissed = "";
		if(StringUtils.isNotEmpty(sourcefeeCodes)){
			if(sourcefeeCodes.contains(",")){
				String[] feeCodesArr = sourcefeeCodes.split(",");
				for (String feeCodeTemp : feeCodesArr) {
					if(!checkFeeExist(feeCodeTemp, finEventTemp, destFeeMap)){
						if(StringUtils.isEmpty(feesMissed)){
							feesMissed = feeCodeTemp;
						}else{
							feesMissed = feesMissed+","+feeCodeTemp;
						}
					}
				}
			}else{
				if(!checkFeeExist(sourcefeeCodes, finEventTemp, destFeeMap)){
					if(StringUtils.isEmpty(feesMissed)){
						feesMissed = sourcefeeCodes;
					}else{
						feesMissed = feesMissed+","+sourcefeeCodes;
					}
				}
			}
		}
		return feesMissed;
	}
	
	
	private boolean checkFeeExist(String sourcefeeCodes,String finEventTemp,Map<String,String>  destFeeMap){
		if(destFeeMap.containsKey(finEventTemp)){
			String accFeeCodes = destFeeMap.get(finEventTemp);
			String stageFeeCodes = destFeeMap.get(AccountEventConstants.ACCEVENT_STAGE);
			if(StringUtils.isNotEmpty(stageFeeCodes)){
				if(StringUtils.isEmpty(accFeeCodes)){
					accFeeCodes = stageFeeCodes;
				}else{
					accFeeCodes = accFeeCodes +","+ stageFeeCodes;
				}
			}
			
			if(accFeeCodes.contains(",")){
				String[] accFeeCodesArr = accFeeCodes.split(",");
				for (String accFeeCode : accFeeCodesArr) {
					if(StringUtils.equals(sourcefeeCodes, accFeeCode)){
						return true;
					}
				}
			}else{
				if(StringUtils.equals(sourcefeeCodes, accFeeCodes)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	private List<String> getEventCodes(boolean isOrigination){
		List<AccountEngineEvent> eventList = null;
		List<String> eventCodes = new ArrayList<String>();
		if(isOrigination){
			eventList = PennantAppUtil.getOriginationAccountingEvents();
		}else{
			eventList = PennantAppUtil.getOverdraftAccountingEvents();
		}
		for (AccountEngineEvent accountEngineEvent : eventList) {
			eventCodes.add(accountEngineEvent.getAEEventCode());
		}
		return eventCodes;
	}
	
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		doSetValidation();
		// doWriteComponentsToBean(getFinanceAgreementList());

		if(!validateFeeAccounting()){
			return;
		}
		if (!validateStages(FinanceConstants.PROCEDT_VERIFICATION_FI_INIT,
				FinanceConstants.PROCEDT_VERIFICATION_FI_APPR, VerificationType.FI)) {
			return;
		}
		if (!validateStages(FinanceConstants.PROCEDT_VERIFICATION_TV_INIT,
				FinanceConstants.PROCEDT_VERIFICATION_TV_APPR, VerificationType.TV)) {
			return;
		}
		if (!validateStages(FinanceConstants.PROCEDT_VERIFICATION_LV_INIT,
				FinanceConstants.PROCEDT_VERIFICATION_LV_APPR, VerificationType.LV)) {
			return;
		}
		if (!validateStages(FinanceConstants.PROCEDT_VERIFICATION_RCU_INIT,
				FinanceConstants.PROCEDT_VERIFICATION_RCU_APPR, VerificationType.RCU)) {
			return;
		}
		if (!validateStages(FinanceConstants.PROCEDT_SAMPLING_INIT,
				FinanceConstants.PROCEDT_SAMPLING_APPR,null)) {
			return;
		}
		if (validateSamplingAndTV()) {
			return;
		}
		
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
		items.addAll(this.listBoxCustDedupRules.getItems());
		items.addAll(this.listBoxBlackListRules.getItems());
		items.addAll(this.listBoxPoliceRules.getItems());
		items.addAll(this.listBoxReturnCheques.getItems());
		items.addAll(this.listBoxLimitService.getItems());
		items.addAll(this.listBoxTatNotification.getItems());
		items.addAll(this.listboxFinanceTabs.getItems());
		
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
			if (StringUtils.isNotEmpty(tranType)) {
				doProcess(aFinanceReferenceDetail, tranType);
			}

		}
		
		if (StringUtils.equals(eventAction, FinanceConstants.FINSER_EVENT_ORG)) {
			deviationConfigCtrl.processDeviationDelegation(this.finType.getValue(),getUserWorkspace().getLoggedInUser());
		}
		try {
			items.clear();
			items = null;
			closeDialog();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(FinanceReferenceDetail aFinanceReferenceDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceReferenceDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceReferenceDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceReferenceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinanceReferenceDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceReferenceDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinanceReferenceDetail);
				}

				if (isNotesMandatory(taskId, aFinanceReferenceDetail)) {
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

			aFinanceReferenceDetail.setTaskId(taskId);
			aFinanceReferenceDetail.setNextTaskId(nextTaskId);
			aFinanceReferenceDetail.setRoleCode(getRole());
			aFinanceReferenceDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceReferenceDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aFinanceReferenceDetail);

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

				if (StringUtils.isBlank(method)) {
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceReferenceDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceReferenceDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.financeReferenceDetail), true);
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

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinanceReferenceDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeReferenceDetail);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.financeReferenceDetail.getFinRefDetailId());
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

	public FinanceReference getFinanceReference() {
		return financeReference;
	}
	public void setFinanceReference(FinanceReference financeReference) {
		this.financeReference = financeReference;
	}
	

	// ===================

	public void dofillListbox(List<FinanceReferenceDetail> financeReferenceDetail, Listbox listbox) {
		logger.debug("Entering");
		if (financeReferenceDetail != null) {
			for (int i = 0; i < financeReferenceDetail.size(); i++) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(financeReferenceDetail.get(i).getFinRefId()));
				lc.setParent(item);

				if (financeReferenceDetail.get(i).getFinRefType() != FinanceConstants.PROCEDT_CHECKLIST) {
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

				if (StringUtils.trimToEmpty(financeReferenceDetail.get(i).getRecordType()).equals(PennantConstants.RCD_DEL)) {
					item.setVisible(false);
				}
				listbox.appendChild(item);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCheckListItemDoubleClicked");
			}

		}
		
		// Deviations not allowed for Collateral , Commitment & VAS
		if(!StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COLLATERAL) &&
				!StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_VAS) &&
				!StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COMMITMENT)){
			checkDeviationForRefDetails(listbox);
		}
		logger.debug("Leaving");

	}

	public void onClick$btnNew_FinanceCheckList(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_CHECKLIST);
	}

	public void onClick$btnNew_FinanceAgreementLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_AGREEMENT);
	}

	public void onClick$btnNew_FinanceEligibilityLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_ELIGIBILITY);
	}

	public void onClick$btnNew_FinanceScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_RTLSCORE);
	}
	
	public void onClick$btnNew_FinCorpScoringGroup(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_CORPSCORE);
	}
	
	public void onClick$btnNew_FinanceAdvanceAccounting(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_STAGEACC);
	}
	
	public void onClick$btnNew_FinanceMailTemplate(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_TEMPLATE);
	}
	
	public void onClick$btnNew_FinanceDedupeLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_FINDEDUP);
	}
	
	public void onClick$btnNew_CustomerDedupeLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_CUSTDEDUP);
	}
	
	public void onClick$btnNew_CustBlackListLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_BLACKLIST);
	}
	
	public void onClick$btnNew_CustPoliceLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_POLICEDEDUP);
	}
	
	public void onClick$btnNew_ReturnChequeLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_RETURNCHQ);
	}
	
	public void onClick$btnNew_LimitServiceLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_LIMIT);
	}
	
	public void onClick$btnNew_TatNotificationLink(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_TATNOTIFICATION);
	}
	
	public void onClick$btnNew_FinanceTabs(Event event) throws InterruptedException {
		callLinakgeZul(financeReferenceDetail, FinanceConstants.PROCEDT_FINANCETABS);
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
		map.put("moduleName", moduleName);
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailDialogLink.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

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
		map.put("moduleName", moduleName);
		map.put("eventAction", eventAction);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailDialogLink.zul", null, map);
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

	private void checkDeviationForRefDetails(Listbox listbox) {
		if (listbox.getId().equals(this.listBoxEligibilityRules.getId())) {
			deviationConfigCtrl.fillEligibilityDeviations(this.listBoxEligibilityRules);
		} else if (listbox.getId().equals(this.listBoxFinanceCheckList.getId())) {
			deviationConfigCtrl.fillCheckListDeviations(this.listBoxFinanceCheckList);
		} else if (listbox.getId().equals(this.listBoxAccounts.getId())) {
			deviationConfigCtrl.fillFeeDeviations(this.listBoxAccounts,this.finType.getValue());
		} else if (listbox.getId().equals(this.listBoxScoringGroup.getId())) {
			deviationConfigCtrl.fillScoringDeviations(this.listBoxScoringGroup);
		}

	}
	
}
