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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.LongValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/LMTMasters/FinanceReferenceDetail
 * /financeReferenceDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceReferenceDetailDialogLinkCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2872130825329784644L;
	private final static Logger logger = Logger.getLogger(FinanceReferenceDetailDialogLinkCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting auto wired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceReferenceDetailDialogLink; // auto wired
	protected Textbox finType; // auto wired
	protected Intbox finRefType; // auto wired
	protected Longbox finRefId; // auto wired
	protected Checkbox isActive; // auto wired
	protected Textbox showInStage; // auto wired
	protected Textbox mandInputInStage; // auto wired
	protected Textbox allowInputInStage; // auto wired
	protected Checkbox overRide;
	protected Intbox overRideValue;
	protected Textbox lovDescRefDesc;
	protected Label recordStatus; // auto wired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;
	
	
	// not auto wired variables
	private FinanceReferenceDetail financeReferenceDetail; // over handed per parameter
	private FinanceReferenceDetail prvFinanceReferenceDetail; // over handed per parameter
	private transient FinanceReferenceDetailDialogCtrl financeReferenceDetailDialogCtrl; // over handed per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_finType;
	private transient int oldVar_finRefType;
	private transient long oldVar_finRefId;
	private transient boolean oldVar_isActive;
	private transient String oldVar_showInStage;
	private transient String oldVar_mandInputInStage;
	private transient String oldVar_allowInputInStage;
	private transient boolean oldVar_overRide;
	private transient int oldVar_overRideValue;
	private transient String oldVar_recordStatus;
	private transient boolean validationOn;
	private boolean notes_Entered = false;
	
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceReferenceDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // auto wire
	protected Button btnEdit; // auto wire
	protected Button btnDelete; // auto wire
	protected Button btnSave; // auto wire
	protected Button btnCancel; // auto wire
	protected Button btnClose; // auto wire
	protected Button btnHelp; // auto wire
	protected Button btnNotes; // auto wire

	// ServiceDAOs / Domain Classes
	private transient FinanceReferenceDetailService financeReferenceDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private String roleCodes;
	
	protected Listbox listboxshowInStage; // auto wired
	protected Listbox listboxmandInputInStage; // auto wired
	protected Listbox listboxallowInputInStage;
	
	private Map<String, String> checkShowInStageMap = new HashMap<String, String>();
	private Map<String, String> checkMandInputInStageMap = new HashMap<String, String>();
	private Map<String, String> checkAllowInputInStage = new HashMap<String, String>();
	
	// List od values
	protected Button btnSearchElgRule;
	protected Button btnSearchAggCode;
	protected Button btnSearchQuestionId;
	protected Button btnSearchScoringGroup;
	protected Button btnSearchCorpScoringGroup;
	protected Button btnSearchAccounting;
	protected Button btnSearchTemplate;
	
	protected Label label_FinanceReferenceDetailDialog_FinRefId;
	protected Row rowSingleListbox;
	protected Row rowDoubleListbox;
	protected Row rowOverRide;
	
	protected Label label_FinanceReferenceDetailDialogLink;
	
	protected Label label_FinanceReferenceDetailDialog_ShowInStage;
	protected Label label_FinanceReferenceDetailDialog_AllowInputInStage;
	protected Label label_FinanceReferenceDetailDialog_MandInputInStage;
	
	protected Listheader listheadShowInStage;
	protected Listheader listheadAllowInputInStage;
	protected Listheader listheadMandInputInStage;

	/**
	 * default constructor.<br>
	 */
	public FinanceReferenceDetailDialogLinkCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceReferenceDetail object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCreate$window_FinanceReferenceDetailDialogLink(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
				this.btnClose, this.btnNotes);

		/* get the parameters map that are over handed by creation. */
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeReferenceDetail")) {
			this.financeReferenceDetail = (FinanceReferenceDetail) args.get("financeReferenceDetail");
			
			boolean addBefImage = true;
			if(PennantConstants.RECORD_TYPE_NEW.equals(this.financeReferenceDetail.getRecordType()) ||
					PennantConstants.RECORD_TYPE_UPD.equals(this.financeReferenceDetail.getRecordType())){
				if(!this.financeReferenceDetail.isNewRecord()){
					addBefImage = false;
				}
			}
			if((addBefImage)){
				FinanceReferenceDetail befImage = new FinanceReferenceDetail();
				BeanUtils.copyProperties(this.financeReferenceDetail, befImage);
				this.financeReferenceDetail.setBefImage(befImage);
			}
			setFinanceReferenceDetail(this.financeReferenceDetail);
		} else {
			setFinanceReferenceDetail(null);
		}

		if (args.containsKey("financeReferenceDetailDialogCtrl")) {
			setFinanceReferenceDetailDialogCtrl((FinanceReferenceDetailDialogCtrl) args.get("financeReferenceDetailDialogCtrl"));
		} else {
			setFinanceReferenceDetailDialogCtrl(null);
		}

		if (args.containsKey("roleCodeList")) {
			roleCodes = args.get("roleCodeList").toString();
		}

		doLoadWorkFlow(this.financeReferenceDetail.isWorkflow(), this.financeReferenceDetail.getWorkflowId(), this.financeReferenceDetail.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceReferenceDetailDialog");
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceReferenceDetail());

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
		this.overRideValue.setMaxlength(4);
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceReferenceDetailDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnDelete"));
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
		PTMessageUtils.showHelpWindow(event, window_FinanceReferenceDetailDialogLink);
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
			this.window_FinanceReferenceDetailDialogLink.onClose();
			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(true);
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
	public void doWriteBeanToComponents(FinanceReferenceDetail aFinanceReferenceDetail) {
		logger.debug("Entering");
		this.finType.setValue(aFinanceReferenceDetail.getFinType());
		this.finRefType.setValue(aFinanceReferenceDetail.getFinRefType());
		this.finRefId.setValue(aFinanceReferenceDetail.getFinRefId());
		this.isActive.setChecked(aFinanceReferenceDetail.isIsActive());
		this.showInStage.setValue(aFinanceReferenceDetail.getShowInStage());
		this.mandInputInStage.setValue(aFinanceReferenceDetail.getMandInputInStage());
		this.allowInputInStage.setValue(aFinanceReferenceDetail.getAllowInputInStage());
		this.overRide.setChecked(aFinanceReferenceDetail.isOverRide());
		this.overRideValue.setValue(aFinanceReferenceDetail.getOverRideValue());
		this.lovDescRefDesc.setValue(aFinanceReferenceDetail.getLovDescRefDesc());

		if (aFinanceReferenceDetail.getShowInStage() != null && 
				!aFinanceReferenceDetail.getShowInStage().equals("")) {
			String[] roles = aFinanceReferenceDetail.getShowInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkShowInStageMap.put(roles[i], roles[i]);
			}
		}
		if (aFinanceReferenceDetail.getAllowInputInStage() != null && 
				!aFinanceReferenceDetail.getAllowInputInStage().equals("")) {
			String[] roles = aFinanceReferenceDetail.getAllowInputInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkAllowInputInStage.put(roles[i], roles[i]);
			}
		}
		if (aFinanceReferenceDetail.getMandInputInStage() != null && 
				!aFinanceReferenceDetail.getMandInputInStage().equals("")) {
			String[] roles = aFinanceReferenceDetail.getMandInputInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkMandInputInStageMap.put(roles[i], roles[i]);
			}
		}

		fillListBox(this.listboxshowInStage, roleCodes, checkShowInStageMap, 
				PennantConstants.ShowInStage);
		fillListBox(this.listboxallowInputInStage, roleCodes, checkAllowInputInStage, 
				PennantConstants.AllowInputInStage);
		fillListBox(this.listboxmandInputInStage, roleCodes, checkMandInputInStageMap, 
				PennantConstants.MandInputInStage);
		
		doDesignByType(getFinanceReferenceDetail());
		this.recordStatus.setValue(aFinanceReferenceDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceReferenceDetail
	 */
	public void doWriteComponentsToBean(FinanceReferenceDetail aFinanceReferenceDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.finType.getValue() == null || this.finType.getValue().equals("")) {
				throw new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value") }));
			}
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
			if (!this.showInStage.isReadonly()) {
				// Set checked values
				this.showInStage.setValue(getCheckedValues(listboxshowInStage));
				// then check for empty
				if (this.showInStage.getValue() == null || this.showInStage.getValue().equals("")) {
					throw new WrongValueException(this.listboxshowInStage, 
							Labels.getLabel("FIELD_NO_EMPTY", new String[] { this.showInStage.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setShowInStage(this.showInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.allowInputInStage.isReadonly()) {
				// Set checked values
				this.allowInputInStage.setValue(getCheckedValues(listboxallowInputInStage));
				// then check for empty
				if (this.allowInputInStage.getValue() == null || 
						this.allowInputInStage.getValue().equals("")) {
					throw new WrongValueException(this.listboxallowInputInStage, 
							Labels.getLabel("FIELD_NO_EMPTY", 
									new String[] { this.allowInputInStage.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setAllowInputInStage(this.allowInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.mandInputInStage.isReadonly()) {
				// Set checked values
				this.mandInputInStage.setValue(getCheckedValues(listboxmandInputInStage));
				// then check for empty
				if (this.mandInputInStage.getValue() == null || 
						this.mandInputInStage.getValue().equals("")) {
					throw new WrongValueException(this.listboxmandInputInStage, 
							Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { this.mandInputInStage.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setMandInputInStage(this.mandInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.lovDescRefDesc.getValue() == null || this.lovDescRefDesc.getValue().equals("")) {
				throw new WrongValueException(this.lovDescRefDesc, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value") }));
			}
			aFinanceReferenceDetail.setLovDescRefDesc(this.lovDescRefDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			aFinanceReferenceDetail.setOverRide(this.overRide.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.overRideValue.isReadonly() && this.overRide.isChecked()) {
				if (this.overRideValue.getValue() == null || this.overRideValue.getValue() <= 0) {
					throw new WrongValueException(this.overRideValue,
							Labels.getLabel("FIELD_NO_EMPTY", 
									new String[] { this.overRideValue.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setOverRideValue(this.overRideValue.getValue());
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

		aFinanceReferenceDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceReferenceDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceReferenceDetail aFinanceReferenceDetail) throws InterruptedException {
		logger.debug("Entering");

		/*
		 * if aFinanceReferenceDetail == null then we opened the Dialog without arguments for a given entity, so we get
		 * a new Object().
		 */
		if (aFinanceReferenceDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			/*
			 * We don't create a new DomainObject() in the front end. We GET it from the back end.
			 */
			aFinanceReferenceDetail = getFinanceReferenceDetailService().getNewFinanceReferenceDetail();
			setFinanceReferenceDetail(aFinanceReferenceDetail);
		} else {
			setFinanceReferenceDetail(aFinanceReferenceDetail);
		}

		/* fill the components with the data */
		doWriteBeanToComponents(aFinanceReferenceDetail);
		/*
		 * stores the initial data for comparing if they are changed during user action.
		 */
		doStoreInitValues();
		/* set Read only mode accordingly if the object is new or not. */
		if (aFinanceReferenceDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.isActive.focus();
			this.btnCancel.setVisible(false);
		} else {
			this.isActive.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				btnEdit.setVisible(true);
				this.btnDelete.setVisible(true);
			}
		}

		try {

			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(false);
			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.getParent().appendChild(window_FinanceReferenceDetailDialogLink);

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
		this.overRide.setChecked(this.oldVar_overRide);
		this.overRideValue.setValue(this.oldVar_overRideValue);
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
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finRefType != this.finRefType.intValue()) {
			return true;
		}
		if (this.oldVar_finRefId != this.finRefId.longValue()) {
			return true;
		}
		if (this.oldVar_isActive != this.isActive.isChecked()) {
			return true;
		}
		if (this.oldVar_showInStage != this.showInStage.getValue()) {
			return true;
		}
		if (this.oldVar_mandInputInStage != this.mandInputInStage.getValue()) {
			return true;
		}
		if (this.oldVar_allowInputInStage != this.allowInputInStage.getValue()) {
			return true;
		}
		if (this.oldVar_overRide != this.overRide.isChecked()) {
			return true;
		}
		if (this.oldVar_overRideValue != this.overRideValue.getValue()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */

	@SuppressWarnings("unused")
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.finType.isReadonly()) {
			this.finType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value") }));
		}
		if (!this.finRefType.isReadonly()) {
			this.finRefType.setConstraint(new IntValidator(10, Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefType.value")));
		}
		if (!this.finRefId.isReadonly()) {
			this.finRefId.setConstraint(new LongValidator(19, Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefId.value")));
		}
		if (!this.showInStage.isReadonly()) {
			this.showInStage.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_ShowInStage.value") }));
		}
		if (!this.mandInputInStage.isReadonly()) {
			this.mandInputInStage.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_MandInputInStage.value") }));
		}
		if (!this.allowInputInStage.isReadonly()) {
			this.allowInputInStage.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_AllowInputInStage.value") }));
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
		final FinanceReferenceDetail aFinanceReferenceDetail = new FinanceReferenceDetail();
		BeanUtils.copyProperties(getFinanceReferenceDetail(), aFinanceReferenceDetail);
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceReferenceDetail.getLovDescRefDesc();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			aFinanceReferenceDetail.setRecordType(PennantConstants.RCD_DEL);
			try {
				// Process Delete
				deleteFinRrefDetails(aFinanceReferenceDetail);
				// Close window
				this.window_FinanceReferenceDetailDialogLink.onClose();
				// Set parent window visible
				getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(true);
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

		final FinanceReferenceDetail aFinanceReferenceDetail = getFinanceReferenceDetailService().getNewFinanceReferenceDetail();
		setFinanceReferenceDetail(aFinanceReferenceDetail);
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

		if (getFinanceReferenceDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.isActive.setChecked(true);
			this.isActive.setDisabled(true);
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchAggCode.setDisabled(true);
			this.btnSearchElgRule.setDisabled(true);
			this.btnSearchQuestionId.setDisabled(true);
			this.btnSearchScoringGroup.setDisabled(true);
			this.btnSearchCorpScoringGroup.setDisabled(true);
			this.isActive.setDisabled(isReadOnly("FinanceReferenceDetailDialog_isActive"));
		}
		if (getFinanceReferenceDetail().getFinRefType() == PennantConstants.ScoringGroup ||
				getFinanceReferenceDetail().getFinRefType() == PennantConstants.CorpScoringGroup ||
				getFinanceReferenceDetail().getFinRefType() == PennantConstants.Accounting ||
				getFinanceReferenceDetail().getFinRefType() == PennantConstants.Template) {
			doToggleInReadOnlyMode(this.listboxmandInputInStage, isReadOnly("FinanceReferenceDetailDialog_isActive"));
		} else {
			doToggleInReadOnlyMode(this.listboxshowInStage, isReadOnly("FinanceReferenceDetailDialog_isActive"));
			doEnableByChecked(this.listboxallowInputInStage);
			doEnableByOtherChecked(this.listboxshowInStage, this.listboxallowInputInStage);
			if (getFinanceReferenceDetail().getFinRefType() == PennantConstants.CheckList) {
				doEnableByChecked(this.listboxmandInputInStage);
				doEnableByOtherChecked(this.listboxallowInputInStage, this.listboxmandInputInStage);
			}
		}
		this.finType.setReadonly(true);
		this.finRefType.setReadonly(isReadOnly("FinanceReferenceDetailDialog_finRefType"));
		this.finRefId.setReadonly(isReadOnly("FinanceReferenceDetailDialog_finRefId"));
		this.overRide.setDisabled(false);
		this.overRideValue.setReadonly(false);
		/*
		 * this.showInStage.setReadonly(isReadOnly( "FinanceReferenceDetailDialog_showInStage"));
		 * this.mandInputInStage.setReadonly (isReadOnly("FinanceReferenceDetailDialog_mandInputInStage"));
		 * this.allowInputInStage .setReadonly(isReadOnly("FinanceReferenceDetailDialog_allowInputInStage" ));
		 */

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
		this.btnSearchAggCode.setDisabled(true);
		this.btnSearchElgRule.setDisabled(true);
		this.btnSearchQuestionId.setDisabled(true);
		this.btnSearchScoringGroup.setDisabled(true);
		this.btnSearchCorpScoringGroup.setDisabled(true);
		this.overRide.setDisabled(true);
		this.overRideValue.setReadonly(true);
		doToggleInReadOnlyMode(this.listboxshowInStage, true);
		doToggleInReadOnlyMode(this.listboxmandInputInStage, true);
		doToggleInReadOnlyMode(this.listboxallowInputInStage, true);
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
		logger.debug("Entering");
		final FinanceReferenceDetail aFinanceReferenceDetail = new FinanceReferenceDetail();
		BeanUtils.copyProperties(getFinanceReferenceDetail(), aFinanceReferenceDetail);
		doWriteComponentsToBean(aFinanceReferenceDetail);
		// save it to database
		try {
			processFinRefDetails(aFinanceReferenceDetail);
		} catch (final InterruptedException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
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

	public FinanceReferenceDetailDialogCtrl getFinanceReferenceDetailDialogCtrl() {
		return financeReferenceDetailDialogCtrl;
	}

	public void setFinanceReferenceDetailDialogCtrl(FinanceReferenceDetailDialogCtrl financeReferenceDetailDialogCtrl) {
		this.financeReferenceDetailDialogCtrl = financeReferenceDetailDialogCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinanceReferenceDetailDialogLink, auditHeader);
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
		logger.debug("Entering onClick$btnNotes()");
		logger.debug("Entering" + event.toString());
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
		logger.debug("Leaving onClick$btnNotes()");
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

	public FinanceReferenceDetail getPrvFinanceReferenceDetail() {
		return prvFinanceReferenceDetail;
	}

	// =======================================//
	public void onClick$btnSearchQuestionId(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "CheckList");
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			CheckList details = (CheckList) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getCheckListId());
				this.lovDescRefDesc.setValue(details.getCheckListDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchAggCode(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "AgreementDefinition");
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			AgreementDefinition details = (AgreementDefinition) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getAggId());
				this.lovDescRefDesc.setValue(details.getAggCode() + "-" + details.getAggName());
				getFinanceReferenceDetail().setLovDescCodelov(details.getAggCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getAggName());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchScoringGroup(Event event) {
		logger.debug("Entering");
		Filter[] filters=new Filter[1];
		filters[0]=new Filter("CategoryType","I",Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "ScoringGroup", filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			ScoringGroup details = (ScoringGroup) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getScoreGroupId());
				this.lovDescRefDesc.setValue(details.getScoreGroupCode() + "-" + details.getScoreGroupName());
				getFinanceReferenceDetail().setLovDescCodelov(details.getScoreGroupCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getScoreGroupName());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCorpScoringGroup(Event event) {
		logger.debug("Entering");
		Filter[] filters=new Filter[1];
		filters[0]=new Filter("CategoryType","C",Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "ScoringGroup", filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			ScoringGroup details = (ScoringGroup) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getScoreGroupId());
				this.lovDescRefDesc.setValue(details.getScoreGroupCode() + "-" + details.getScoreGroupName());
				getFinanceReferenceDetail().setLovDescCodelov(details.getScoreGroupCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getScoreGroupName());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchAccounting(Event event) {
		logger.debug("Entering");
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("EventCode", "STAGE", Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "AccountingSet", filter);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getAccountSetid());
				this.lovDescRefDesc.setValue(details.getEventCode() + "-" + details.getAccountSetCodeName());
				getFinanceReferenceDetail().setLovDescNamelov(details.getEventCode());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchTemplate(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("TemplateFor", PennantConstants.TEMPLATE_FOR_CN, Filter.OP_EQUAL);
		filters[1] = new Filter("Module", PennantConstants.MAIL_MODULE_FIN, Filter.OP_EQUAL);
		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "MailTemplate", filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			MailTemplate details = (MailTemplate) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getTemplateId());
				this.lovDescRefDesc.setValue(details.getTemplateCode() + "-" + details.getTemplateDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getTemplateDesc());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getTemplateDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchElgRule(Event event) {
		logger.debug("Entering");
		// RuleModule
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", "ELGRULE", Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "Rule", filter);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getRuleId());
				this.lovDescRefDesc.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
				getFinanceReferenceDetail().setLovDescCodelov(details.getRuleCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getRuleCode());

			}
		}
		logger.debug("Leaving");
	}

	public void fillListBox(Listbox listbox, String roleCodes, Map<String, String> checkedlist, int type) {
		logger.debug("Entering");
		listbox.getItems().clear();
		String[] roles = roleCodes.split(";");
		for (int i = 0; i < roles.length; i++) {
			Listitem item = new Listitem();
			Listcell lc;
			Checkbox checkbox = new Checkbox();
			checkbox.setTabindex(type);
			checkbox.setValue(roles[i]);
			checkbox.setLabel(roles[i]);
			checkbox.setChecked(checkedlist.containsKey(roles[i]));
			checkbox.addEventListener("onCheck", new onCheckBoxCheked());
			lc = new Listcell();
			lc.appendChild(checkbox);
			lc.setParent(item);
			listbox.appendChild(item);
		}
		logger.debug("Leaving");

	}

	public final class onCheckBoxCheked implements EventListener<Event> {

		public void onEvent(Event event) throws Exception {
			logger.debug("onEvent()");
			Checkbox checkbox = (Checkbox) event.getTarget();
			switch (checkbox.getTabindex()) {
			//Upgraded to ZK-6.5.1.1 Added casting to String
			case PennantConstants.ShowInStage:
				if (checkbox.isChecked()) {
					doToggleDisableByChkVal(listboxallowInputInStage, checkbox.getValue().toString(), false);
				} else {
					doToggleDisableByChkVal(listboxallowInputInStage, checkbox.getValue().toString(), true);
					doToggleDisableByChkVal(listboxmandInputInStage, checkbox.getValue().toString(), true);
				}
				break;
			case PennantConstants.AllowInputInStage:
				if (checkbox.isChecked()) {
					doToggleDisableByChkVal(listboxmandInputInStage, checkbox.getValue().toString(), false);
				} else {
					doToggleDisableByChkVal(listboxmandInputInStage, checkbox.getValue().toString(), true);
				}
				break;
			default:
				break;
			}

			logger.debug("Leaving onEvent()");
		}

	}

	// ============Design the zul file===========//
	private void doDesignByType(FinanceReferenceDetail finRefDetail) {
		logger.debug("Entering");
		switch (finRefDetail.getFinRefType()) {
		
		case PennantConstants.CheckList:
			
			// For validations
			this.showInStage.setReadonly(false);
			this.allowInputInStage.setReadonly(false);
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));
			
			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			
			// LOV List
			this.btnSearchQuestionId.setVisible(true);// show

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId.setValue(Labels.getLabel("label_FinRefDialogLink_Question.value"));
			
			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show
			this.rowSingleListbox.setVisible(true);// Show
			
			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			
			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage.setLabel(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			
			doEnableByChecked(this.listboxallowInputInStage);
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceCheckListList.title"));
			break;
			
		case PennantConstants.Aggrement:
			
			// For validations
			this.showInStage.setReadonly(false);
			this.allowInputInStage.setReadonly(false);
			this.mandInputInStage.setReadonly(true);// not required
			
			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.mandInputInStage.setLeft("");
			
			// LOV List
			this.btnSearchAggCode.setVisible(true);// show
			
			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId.setValue(Labels.getLabel("label_FinRefDialogLink_Agreement.value"));
			
			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue("");// not required
			
			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.listheadMandInputInStage.setLabel("");// not required
			
			doEnableByChecked(this.listboxallowInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceAgreementList.title"));
			break;
			
		case PennantConstants.Eligibility:
			
			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.mandInputInStage.setLeft("");
			// For validations
			this.showInStage.setReadonly(false);
			this.allowInputInStage.setReadonly(false); 
			this.mandInputInStage.setReadonly(true);
			
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));
			
			// error labels
			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.mandInputInStage.setLeft("");
			
			// LOV List
			this.btnSearchElgRule.setVisible(true);// show
			
			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId.setValue(Labels.getLabel("label_FinReferDialogLink_Eligibility.value"));
			
			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show

			
			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue("");// not required
			
			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.listheadMandInputInStage.setLabel("");// not required
			
			doEnableByChecked(this.listboxallowInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceAgreementList.title"));
			break;
			
		case PennantConstants.ScoringGroup:
			
			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			
			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// LOV List
			this.btnSearchScoringGroup.setVisible(true);// show
			
			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId.setValue(Labels.getLabel("label_FinReferDialogLink_ScoringGroup.value"));
			
			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show
			
			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceScoringList.title"));
			break;
			
		case PennantConstants.CorpScoringGroup:
			
			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			
			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// LOV List
			this.btnSearchCorpScoringGroup.setVisible(true);// show
			
			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId.setValue(Labels.getLabel("label_FinReferDialogLink_ScoringGroup.value"));
			
			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show
			
			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceCorpScoringList.title"));
			break;
			
		case PennantConstants.Accounting:
			
			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			
			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// LOV List
			this.btnSearchAccounting.setVisible(true);
			
			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId.setValue(Labels.getLabel("label_FinRefDialogLink_Accounting.value"));
			
			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show
			
			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceAccountingList.title"));			
			CheckOverride();
			break;
			
		case PennantConstants.Template:
			
			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			
			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// LOV List
			this.btnSearchTemplate.setVisible(true);
			
			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId.setValue(Labels.getLabel("label_FinRefDialogLink_Template.value"));
			
			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show
			
			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceMailTemplateList.title"));			
			CheckOverride();
			break;
			
		default:
			break;
		}
		logger.debug("Leaving");
	}

	// =====ADD or Update========//
	private void processFinRefDetails(FinanceReferenceDetail financeReferenceDetail) throws InterruptedException {
		logger.debug("Entering");
		if (financeReferenceDetail.getRecordType() != null) {
			if (financeReferenceDetail.getRecordType().equals(PennantConstants.RCD_ADD) || financeReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
					|| financeReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				if (financeReferenceDetail.getUserAction().equals("Save")) {
					financeReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else {
				if (financeReferenceDetail.getRecordType().equals(PennantConstants.RCD_DEL)) {
					financeReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
		} else {
			financeReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeReferenceDetail.setNewRecord(true);
		}
		switch (financeReferenceDetail.getFinRefType()) {
		case PennantConstants.CheckList:
			processAddOrUpdate(financeReferenceDetail, getFinanceReferenceDetailDialogCtrl().listBoxFinanceCheckList);
			break;
		case PennantConstants.Aggrement:
			processAddOrUpdate(financeReferenceDetail, getFinanceReferenceDetailDialogCtrl().listboxFinanceAgreementLink);
			break;
		case PennantConstants.Eligibility:
			processAddOrUpdate(financeReferenceDetail, getFinanceReferenceDetailDialogCtrl().listBoxEligibilityRules);
			break;
		case PennantConstants.ScoringGroup:
			processAddOrUpdate(financeReferenceDetail, getFinanceReferenceDetailDialogCtrl().listBoxScoringGroup);
			break;
		case PennantConstants.CorpScoringGroup:
			processAddOrUpdate(financeReferenceDetail, getFinanceReferenceDetailDialogCtrl().listBoxCorpScoringGroup);
			break;
		case PennantConstants.Accounting:
			processAddOrUpdate(financeReferenceDetail, getFinanceReferenceDetailDialogCtrl().listBoxAccounts);
			break;
		case PennantConstants.Template:
			processAddOrUpdate(financeReferenceDetail, getFinanceReferenceDetailDialogCtrl().listBoxTemplates);
			break;
		default:
			break;
		}
		logger.debug("Leaving");
	}

	public void processAddOrUpdate(FinanceReferenceDetail newFinrefDet, Listbox listbox) throws InterruptedException {
		logger.debug("Entering");
		boolean contains = false;
		List<Listitem> avlFinRef = listbox.getItems();
		for (int i = 0; i < avlFinRef.size(); i++) {
			FinanceReferenceDetail finRefDet = (FinanceReferenceDetail) avlFinRef.get(i).getAttribute("data");
			if (finRefDet.getFinRefId() == newFinrefDet.getFinRefId()) {
				if (newFinrefDet.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
				} else if (finRefDet.getRecordType().equals(PennantConstants.RCD_DEL)) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					newFinrefDet.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					newFinrefDet.setNewRecord(false);
					newFinrefDet.setFinRefDetailId(finRefDet.getFinRefDetailId());
					newFinrefDet.setVersion(finRefDet.getVersion());
					
					FinanceReferenceDetail befImage = new FinanceReferenceDetail();
					BeanUtils.copyProperties(finRefDet.getBefImage(), befImage);
					newFinrefDet.setBefImage(befImage);
				} else {
					Messagebox.show("E0045:" + newFinrefDet.getLovDescRefDesc() + " already linked.", "error", 
							Messagebox.OK, Messagebox.ERROR);
					contains = true;
				}
				break;
			}
		}

		if (!contains) {
			List<FinanceReferenceDetail> finRefDetailList = new ArrayList<FinanceReferenceDetail>();
			finRefDetailList.add(newFinrefDet);
			getFinanceReferenceDetailDialogCtrl().dofillListbox(finRefDetailList, listbox);
			this.window_FinanceReferenceDetailDialogLink.onClose();
			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(true);
		}
		
		if(listbox.getId().equals("listBoxCorpScoringGroup") && listbox.getVisibleItemCount() == 1){
			getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(false);
		}
		logger.debug("Leaving");
	}

	// ====== Delete ===============//
	private void deleteFinRrefDetails(FinanceReferenceDetail finRefDetail) {
		logger.debug("Entering");
		switch (finRefDetail.getFinRefType()) {
		case PennantConstants.CheckList:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxFinanceCheckList);
			break;
		case PennantConstants.Aggrement:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listboxFinanceAgreementLink);
			break;
		case PennantConstants.Eligibility:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxEligibilityRules);
			break;
		case PennantConstants.ScoringGroup:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxScoringGroup);
			break;
		case PennantConstants.CorpScoringGroup:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxCorpScoringGroup);
			break;
		case PennantConstants.Accounting:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxAccounts);
			break;
		case PennantConstants.Template:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxTemplates);
			break;
		default:
			break;
		}
		logger.debug("Leaving");

	}

	public void processDelet(FinanceReferenceDetail newFinrefDet, Listbox listbox) {
		logger.debug("Entering");
		List<Listitem> avlFinRef = listbox.getItems();
		for (int i = 0; i < avlFinRef.size(); i++) {
			FinanceReferenceDetail finRefDet = (FinanceReferenceDetail) avlFinRef.get(i).getAttribute("data");
			if (finRefDet.getFinRefId() == newFinrefDet.getFinRefId()) {
				if (finRefDet.getRecordStatus().equals(PennantConstants.RCD_STATUS_APPROVED) ||
						(finRefDet.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD) && !finRefDet.isNewRecord())) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					List<FinanceReferenceDetail> finRefDetailList = new ArrayList<FinanceReferenceDetail>();
					finRefDetailList.add(newFinrefDet);
					getFinanceReferenceDetailDialogCtrl().dofillListbox(finRefDetailList, listbox);
					if(listbox.getId().equals("listBoxCorpScoringGroup") && listbox.getVisibleItemCount() == 1){
						getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(false);
					}else{
						getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(true);
					}
				} else {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					if(listbox.getId().equals("listBoxCorpScoringGroup") && listbox.getItemCount() == 0){
						getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(true);
					}
				}

				break;
			}
		}
		logger.debug("Leaving");
	}

	// ===========Helpers========//

	public void doToggleInReadOnlyMode(Listbox listbox, boolean enableOrDisable) {
		logger.debug("Entering");
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			ck.setDisabled(enableOrDisable);

		}
		logger.debug("Leaving");
	}

	public void doToggleDisableByChkVal(Listbox listbox, String id, boolean enableOrDisable) {
		logger.debug("Entering");
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.getValue().equals(id)) {
				if (enableOrDisable) {
					ck.setChecked(!enableOrDisable);
				}
				ck.setDisabled(enableOrDisable);
				break;
			}
		}
		logger.debug("Leaving");
	}

	public void doEnableByChecked(Listbox listbox) {
		logger.debug("Entering");
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.isChecked()) {
				ck.setDisabled(false);
			} else {
				ck.setDisabled(true);
			}
		}
		logger.debug("Leaving");
	}

	public void doEnableByOtherChecked(Listbox fromlistbox, Listbox tolistbox) {
		logger.debug("Entering");
		for (int i = 0; i < fromlistbox.getItems().size(); i++) {
			Listitem item = (Listitem) fromlistbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.isChecked()) {
				//Upgraded to ZK-6.5.1.1 Added casting to String
				doToggleDisableByChkVal(tolistbox, ck.getValue().toString(), false);

			}
		}
		logger.debug("Leaving");
	}

	public String getCheckedValues(Listbox listbox) {
		logger.debug("Entering");
		String value = "";
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.isChecked()) {
				value = value + ck.getValue() + ",";
			}
		}
		logger.debug("Leaving");
		return value;
	}

	public void onCheck$overRide(Event event) {
		logger.debug("Entering" + event.toString());
		CheckOverride();
		logger.debug("Leaving" + event.toString());
	}

	private void CheckOverride() {
		if (this.overRide.isChecked()) {
			this.overRideValue.setReadonly(false);
		} else {
			this.overRideValue.setValue(0);
			this.overRideValue.setReadonly(true);
		}
	}

}
