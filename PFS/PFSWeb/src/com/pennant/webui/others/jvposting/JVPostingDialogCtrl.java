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
 * FileName    		:  JVPostingDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.others.jvposting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/others/JVPosting/jVPostingDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class JVPostingDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(JVPostingDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_JVPostingDialog;
	protected Row row0;
	protected Label label_BatchReference;
	protected Hlayout hlayout_BatchReference;
	protected Space space_BatchReference;

	protected Textbox batchReference;
	protected Label label_Batch;
	protected Hlayout hlayout_Batch;
	protected Space space_Batch;

	protected Textbox batch;
	protected Textbox baseCCy;

	protected Row row2;
	protected Label label_DebitCount;
	protected Hlayout hlayout_DebitCount;
	protected Space space_DebitCount;

	protected Intbox debitCount;
	protected Label label_CreditsCount;
	protected Hlayout hlayout_CreditsCount;
	protected Space space_CreditsCount;

	protected Intbox creditsCount;
	protected Row row3;
	protected Label label_TotDebitsByBatchCcy;
	protected Hlayout hlayout_TotDebitsByBatchCcy;
	protected Space space_TotDebitsByBatchCcy;

	protected Decimalbox totDebitsByBatchCcy;
	protected Label label_TotCreditsByBatchCcy;
	protected Hlayout hlayout_TotCreditsByBatchCcy;
	protected Space space_TotCreditsByBatchCcy;

	protected Decimalbox totCreditsByBatchCcy;
	protected Row row4;
	protected Label label_BatchPurpose;
	protected Hlayout hlayout_BatchPurpose;
	protected Space space_BatchPurpose;

	protected Textbox batchPurpose;

	protected Label recordStatus;
	protected Label recordType;
	protected Radiogroup userAction;
	protected Groupbox gb_statusDetails;
	protected Groupbox groupboxWf;
	protected South south;
	private boolean enqModule = false;

	// not auto wired vars
	private JVPosting jVPosting; // overhanded per param
	private transient JVPostingListCtrl jVPostingListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_BatchReference;
	private transient String oldVar_Batch;
	private transient int oldVar_DebitCount;
	private transient int oldVar_CreditsCount;
	private transient double oldVar_TotDebitsByBatchCcy;
	private transient double oldVar_TotCreditsByBatchCcy;
	private transient String oldVar_BatchPurpose;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_JVPostingDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;
	protected Button btnEdit;
	protected Button btnDelete;
	protected Button btnSave;
	protected Button btnCancel;
	protected Button btnClose;
	protected Button btnHelp;
	protected Button btnNotes;

	// ServiceDAOs / Domain Classes
	private transient JVPostingService jVPostingService;
	private transient PagedListService pagedListService;
	private String baseCcy = SystemParameterDetails.getSystemParameterValue("EXT_BASE_CCY").toString();
	private Currency baseCurrency = PennantAppUtil.getCuurencyBycode(baseCcy);

	int debitTotCount = 0;
	int creditTotcount = 0;
	BigDecimal debitAmount = BigDecimal.ZERO;
	BigDecimal creditAmount = BigDecimal.ZERO;
	private int borderLayoutHeight = 0;

	/**
	 * default constructor.<br>
	 */
	public JVPostingDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected JVPosting object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_JVPostingDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule = (Boolean) args.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (args.containsKey("jVPosting")) {
				this.jVPosting = (JVPosting) args.get("jVPosting");
				JVPosting befImage = new JVPosting();
				BeanUtils.copyProperties(this.jVPosting, befImage);
				this.jVPosting.setBefImage(befImage);

				setJVPosting(this.jVPosting);
			} else {
				setJVPosting(null);
			}
			doLoadWorkFlow(this.jVPosting.isWorkflow(), this.jVPosting.getWorkflowId(), this.jVPosting.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "JVPostingDialog");
			} else {
				getUserWorkspace().alocateAuthorities("JVPostingDialog");
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxJVPostingEntry.setHeight(borderLayoutHeight - 240 + "px");
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the jVPostingListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete jVPosting here.
			if (args.containsKey("jVPostingListCtrl")) {
				setJVPostingListCtrl((JVPostingListCtrl) args.get("jVPostingListCtrl"));
			} else {
				setJVPostingListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getJVPosting());
		} catch (Exception e) {
			createException(window_JVPostingDialog, e);
			logger.error(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doStoreInitValues();
		displayComponents(ScreenCTL.SCRN_GNEDT);
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doResetInitValues();
		displayComponents(ScreenCTL.SCRN_GNINT);
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
		PTMessageUtils.showHelpWindow(event, window_JVPostingDialog);
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
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_JVPostingDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		try {

			ScreenCTL.displayNotes(getNotes("JVPosting", getJVPosting().getBatchReference(), getJVPosting().getVersion()), this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aJVPosting
	 * @throws InterruptedException
	 */
	public void doShowDialog(JVPosting aJVPosting) throws InterruptedException {
		logger.debug("Entering");

		// if aJVPosting == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aJVPosting == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aJVPosting = getJVPostingService().getNewJVPosting();

			setJVPosting(aJVPosting);
		} else {
			setJVPosting(aJVPosting);
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aJVPosting);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aJVPosting.isNewRecord()));

			doStoreInitValues();

			// stores the initial data for comparing if they are changed
			// during user action.
			disableNewJVPostingEntry();
			checkListboxcount();
			disablefileds();
			setDialog(this.window_JVPostingDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(), this.userAction, this.batchReference, this.batch));

		if (getJVPosting().isNewRecord()) {
			setComponentAccessType("JVPostingDialog_BatchReference", false, this.batchReference, this.space_BatchReference, this.label_BatchReference, this.hlayout_BatchReference,
			        null);
		} else {
			this.batchReference.setReadonly(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;

		if (readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(jVPosting.getRecordType())))) {
			tempReadOnly = true;
		}

		//setComponentAccessType("JVPostingDialog_BatchReference", true, this.batchReference, this.space_BatchReference, this.label_BatchReference, this.hlayout_BatchReference,null);		
		setComponentAccessType("JVPostingDialog_Batch", tempReadOnly, this.batch, this.space_Batch, this.label_Batch, this.hlayout_Batch, null);
		setRowInvisible(this.row0, this.hlayout_BatchReference, this.hlayout_Batch);
		setComponentAccessType("JVPostingDialog_DebitCount", tempReadOnly, this.debitCount,null, this.label_DebitCount, this.hlayout_DebitCount, null);
		setComponentAccessType("JVPostingDialog_CreditsCount", tempReadOnly, this.creditsCount, null, this.label_CreditsCount, this.hlayout_CreditsCount, null);
		setRowInvisible(this.row2, this.hlayout_DebitCount, this.hlayout_CreditsCount);
		setComponentAccessType("JVPostingDialog_TotDebitsByBatchCcy", tempReadOnly, this.totDebitsByBatchCcy, this.space_TotDebitsByBatchCcy, this.label_TotDebitsByBatchCcy,
		        this.hlayout_TotDebitsByBatchCcy, null);
		setComponentAccessType("JVPostingDialog_TotCreditsByBatchCcy", tempReadOnly, this.totCreditsByBatchCcy, this.space_TotCreditsByBatchCcy, this.label_TotCreditsByBatchCcy,
		        this.hlayout_TotCreditsByBatchCcy, null);
		setRowInvisible(this.row3, this.hlayout_TotDebitsByBatchCcy, this.hlayout_TotCreditsByBatchCcy);
		setComponentAccessType("JVPostingDialog_BatchPurpose", tempReadOnly, this.batchPurpose, null, this.label_BatchPurpose, this.hlayout_BatchPurpose, null);
		setRowInvisible(this.row4, this.hlayout_BatchPurpose, null);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		getUserWorkspace().alocateAuthorities("JVPostingDialog");
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnSave"));
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
		        this.btnClose, this.btnNotes);

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.batchReference.setMaxlength(50);
		this.batch.setMaxlength(20);
		this.debitCount.setMaxlength(10);
		this.creditsCount.setMaxlength(10);
		this.totDebitsByBatchCcy.setMaxlength(18);
		this.totDebitsByBatchCcy.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.totDebitsByBatchCcy.setScale(0);
		this.totCreditsByBatchCcy.setMaxlength(18);
		this.totCreditsByBatchCcy.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.totCreditsByBatchCcy.setScale(0);
		this.batchPurpose.setMaxlength(200);

		//setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);TODO Check
		logger.debug("Leaving");
	}

	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_BatchReference = this.batchReference.getValue();
		this.oldVar_Batch = this.batch.getValue();
		this.oldVar_DebitCount = this.debitCount.intValue();
		this.oldVar_CreditsCount = this.creditsCount.intValue();
		this.oldVar_TotDebitsByBatchCcy = this.totDebitsByBatchCcy.doubleValue();
		this.oldVar_TotCreditsByBatchCcy = this.totCreditsByBatchCcy.doubleValue();
		this.oldVar_BatchPurpose = this.batchPurpose.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.batchReference.setValue(this.oldVar_BatchReference);
		this.batch.setValue(this.oldVar_Batch);
		this.debitCount.setValue(this.oldVar_DebitCount);
		this.creditsCount.setValue(this.oldVar_CreditsCount);
		this.totDebitsByBatchCcy.setValue(new BigDecimal(this.oldVar_TotDebitsByBatchCcy));
		this.totCreditsByBatchCcy.setValue(new BigDecimal(this.oldVar_TotCreditsByBatchCcy));
		this.batchPurpose.setValue(this.oldVar_BatchPurpose);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled() & !enqModule) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aJVPosting
	 *            JVPosting
	 */
	public void doWriteBeanToComponents(JVPosting aJVPosting) {
		logger.debug("Entering");
		this.batchReference.setValue(aJVPosting.getBatchReference());
		this.batch.setValue(aJVPosting.getBatch());
		this.debitCount.setValue(aJVPosting.getDebitCount());
		this.creditsCount.setValue(aJVPosting.getCreditsCount());
		this.baseCCy.setValue(this.baseCcy);

		this.totDebitsByBatchCcy.setValue(PennantAppUtil.formateAmount(aJVPosting.getTotDebitsByBatchCcy(), baseCurrency.getCcyEditField()));
		this.totCreditsByBatchCcy.setValue(PennantAppUtil.formateAmount(aJVPosting.getTotCreditsByBatchCcy(), baseCurrency.getCcyEditField()));
		this.batchPurpose.setValue(aJVPosting.getBatchPurpose());
		doFilllistbox(aJVPosting.getJVPostingEntrysList());
		this.recordStatus.setValue(aJVPosting.getRecordStatus());
		//this.recordType.setValue(PennantJavaUtil.getLabel(aJVPosting.getRecordType()));//TODO Check
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPosting
	 */
	public void doWriteComponentsToBean(JVPosting aJVPosting) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Batch Reference
		try {
			aJVPosting.setBatchReference(this.batchReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Batch
		try {
			aJVPosting.setBatch(this.batch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Debit Count
		try {
			aJVPosting.setDebitCount(this.debitCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Credits Count
		try {
			aJVPosting.setCreditsCount(this.creditsCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Tot Debits By Batch Ccy
		try {
			if (this.totDebitsByBatchCcy.getValue() != null) {
				aJVPosting.setTotDebitsByBatchCcy(PennantAppUtil.unFormateAmount(this.totDebitsByBatchCcy.getValue(), baseCurrency.getCcyEditField()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Tot Credits By Batch Ccy
		try {
			if (this.totCreditsByBatchCcy.getValue() != null) {
				aJVPosting.setTotCreditsByBatchCcy(PennantAppUtil.unFormateAmount(this.totCreditsByBatchCcy.getValue(), baseCurrency.getCcyEditField()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Batch Purpose
		try {
			aJVPosting.setBatchPurpose(this.batchPurpose.getValue());
			verifyAmounts();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aJVPosting.setJVPostingEntrysList(this.jVPostingEntryList);
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
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
		//To clear the Error Messages
		doClearMessage();

		if (!StringUtils.trimToEmpty(this.oldVar_BatchReference).equals(StringUtils.trimToEmpty(this.batchReference.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_Batch).equals(StringUtils.trimToEmpty(this.batch.getValue()))) {
			return true;
		}
		if (this.oldVar_DebitCount != this.debitCount.intValue()) {
			return true;
		}
		if (this.oldVar_CreditsCount != this.creditsCount.intValue()) {
			return true;
		}
		if (this.oldVar_TotDebitsByBatchCcy != this.totDebitsByBatchCcy.doubleValue()) {
			return true;
		}
		if (this.oldVar_TotCreditsByBatchCcy != this.totCreditsByBatchCcy.doubleValue()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_BatchPurpose).equals(StringUtils.trimToEmpty(this.batchPurpose.getValue()))) {
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
		//Batch Reference
		if (!this.batchReference.isReadonly()) {
			this.batchReference.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_BatchReference.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		//Batch
		if (!this.batch.isReadonly()) {
			this.batch.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_Batch.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		//Debit Count
		if (!this.debitCount.isReadonly()) {
			this.debitCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_JVPostingDialog_DebitCount.value"), true, false, 0));
		}
		//Credits Count
		if (!this.creditsCount.isReadonly()) {
			this.creditsCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_JVPostingDialog_CreditsCount.value"), true, false, 0));
		}
		//Tot Debits By Batch Ccy
		if (!this.totDebitsByBatchCcy.isReadonly()) {
			this.totDebitsByBatchCcy.setConstraint(new PTDecimalValidator(Labels.getLabel("label_JVPostingDialog_TotDebitsByBatchCcy.value"), 0, true, false, 0));
		}
		//Tot Credits By Batch Ccy
		if (!this.totCreditsByBatchCcy.isReadonly()) {
			this.totCreditsByBatchCcy.setConstraint(new PTDecimalValidator(Labels.getLabel("label_JVPostingDialog_TotCreditsByBatchCcy.value"), 0, true, false, 0));
		}
		//Batch Purpose
		if (!this.batchPurpose.isReadonly()) {
			//this.batchPurpose.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_BatchPurpose.value"), PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.batchReference.setConstraint("");
		this.batch.setConstraint("");
		this.debitCount.setConstraint("");
		this.creditsCount.setConstraint("");
		this.totDebitsByBatchCcy.setConstraint("");
		this.totCreditsByBatchCcy.setConstraint("");
		this.batchPurpose.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		//Batch Ccy
		//		if(!btnSearchBatchCcy.isVisible()){
		//			this.batchCcyName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_JVPostingDialog_BatchCcy.value")}));
		//		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		//		this.batchCcyName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
		logger.debug("Entering");
		this.batchReference.setErrorMessage("");
		this.batch.setErrorMessage("");
		this.debitCount.setErrorMessage("");
		this.creditsCount.setErrorMessage("");
		this.totDebitsByBatchCcy.setErrorMessage("");
		this.totCreditsByBatchCcy.setErrorMessage("");
		this.batchPurpose.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList() {
		final JdbcSearchObject<JVPosting> soJVPosting = getJVPostingListCtrl().getSearchObj();
		getJVPostingListCtrl().pagingJVPostingList.setActivePage(0);
		getJVPostingListCtrl().getPagedListWrapper().setSearchObject(soJVPosting);
		if (getJVPostingListCtrl().listBoxJVPosting != null) {
			getJVPostingListCtrl().listBoxJVPosting.getListModel();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		if (!enqModule && isDataChanged()) {
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
			closeDialog(this.window_JVPostingDialog, "JVPosting");
		}

		logger.debug("Leaving");
	}

	/**
	 * Deletes a JVPosting object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aJVPosting.getBatchReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aJVPosting.getRecordType()).equals("")) {
				aJVPosting.setVersion(aJVPosting.getVersion() + 1);
				aJVPosting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aJVPosting.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aJVPosting.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aJVPosting.getNextTaskId(), aJVPosting);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aJVPosting, tranType)) {
					refreshList();
					closeDialog(this.window_JVPostingDialog, "JVPosting");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_JVPostingDialog, e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.batchReference.setValue("");
		this.batch.setValue("");
		this.debitCount.setText("");
		this.creditsCount.setText("");
		this.totDebitsByBatchCcy.setValue("");
		this.totCreditsByBatchCcy.setValue("");
		this.batchPurpose.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		boolean isNew = false;
		if (isWorkFlowEnabled()) {
			aJVPosting.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aJVPosting.getNextTaskId(), aJVPosting);
		}
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aJVPosting.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the JVPosting object with the components data
			doWriteComponentsToBean(aJVPosting);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aJVPosting.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aJVPosting.getRecordType()).equals("")) {
				aJVPosting.setVersion(aJVPosting.getVersion() + 1);
				if (isNew) {
					aJVPosting.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aJVPosting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aJVPosting.setNewRecord(true);
				}
			}
		} else {
			aJVPosting.setVersion(aJVPosting.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aJVPosting, tranType)) {
				//doWriteBeanToComponents(aJVPosting);
				refreshList();
				closeDialog(this.window_JVPostingDialog, "JVPosting");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_JVPostingDialog, e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(JVPosting aJVPosting, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aJVPosting.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aJVPosting.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aJVPosting.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
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

			aJVPosting.setTaskId(getTaskId());
			aJVPosting.setNextTaskId(getNextTaskId());
			aJVPosting.setRoleCode(getRole());
			aJVPosting.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aJVPosting, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aJVPosting, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aJVPosting, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param  AuditHeader auditHeader
	 * @param method  (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		JVPosting aJVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getJVPostingService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getJVPostingService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getJVPostingService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aJVPosting.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getJVPostingService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aJVPosting.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null,null));
						retValue = ErrorControl.showErrorControl(this.window_JVPostingDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_JVPostingDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes("JVPosting", aJVPosting.getBatchReference(), aJVPosting.getVersion()), true);
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
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(JVPosting aJVPosting, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJVPosting.getBefImage(), aJVPosting);
		return new AuditHeader(aJVPosting.getBatchReference(), null, null, null, auditDetail, aJVPosting.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JVPosting getJVPosting() {
		return this.jVPosting;
	}

	public void setJVPosting(JVPosting jVPosting) {
		this.jVPosting = jVPosting;
	}

	public void setJVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

	public JVPostingService getJVPostingService() {
		return this.jVPostingService;
	}

	public void setJVPostingListCtrl(JVPostingListCtrl jVPostingListCtrl) {
		this.jVPostingListCtrl = jVPostingListCtrl;
	}

	public JVPostingListCtrl getJVPostingListCtrl() {
		return this.jVPostingListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	//==================JVPosting Entry
	protected Listbox listBoxJVPostingEntry;
	protected Button button_JVPostingEntryList_NewJVPostingEntry;
	private List<JVPostingEntry> jVPostingEntryList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> oldVar_jVPostingEntryList = new ArrayList<JVPostingEntry>();

	public void onClick$button_JVPostingEntryList_NewJVPostingEntry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new JVPostingEntry object, We GET it from the backEnd.
		final JVPostingEntry aJVPostingEntry = getJVPostingService().getNewJVPostingEntry();
		aJVPostingEntry.setBatchReference(this.batchReference.getValue());
		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("jVPostingEntry", aJVPostingEntry);
		map.put("jVPostingDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Others/JVPostingEntry/JVPostingEntryDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onJVPostingEntryItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem item = (Listitem) event.getOrigin().getTarget();
		JVPostingEntry itemdata = (JVPostingEntry) item.getAttribute("data");
		itemdata.setNewRecord(false);
		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("jVPostingEntry", itemdata);
		map.put("jVPostingDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Others/JVPostingEntry/JVPostingEntryDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFilllistbox(List<JVPostingEntry> JVPostingEntryList) {
		logger.debug("Entering");
		if (JVPostingEntryList != null) {
			getJVPostingEntryList().clear();
			setJVPostingEntryList(JVPostingEntryList);
			fillJVPostingEntry(JVPostingEntryList);
			calculateTotals(getJVPostingEntryList());
		}
		checkListboxcount();

		logger.debug("Leaving");
	}

	private void calculateTotals(List<JVPostingEntry> jVPostingEntryList) {
		try {
			debitTotCount = 0;
			creditTotcount = 0;
			debitAmount = BigDecimal.ZERO;
			creditAmount = BigDecimal.ZERO;

			if (jVPostingEntryList != null && jVPostingEntryList.size() > 0) {
				for (JVPostingEntry jvPostingEntry : jVPostingEntryList) {
					if (jvPostingEntry.getTxnCode().equals(PennantConstants.DEBIT)) {
						debitTotCount = debitTotCount + 1;
						if (baseCurrency.getCcyCode().equals(jvPostingEntry.getTxnCCy())) {
							debitAmount = debitAmount.add(jvPostingEntry.getTxnAmount());
						}
					} else {
						creditTotcount = creditTotcount + 1;
						if (baseCurrency.getCcyCode().equals(jvPostingEntry.getTxnCCy())) {
							creditAmount = creditAmount.add(jvPostingEntry.getTxnAmount());
						}
					}
				}
			}
			this.debitCount.setValue(debitTotCount);
			//this.totDebitsByBatchCcy.setValue(PennantAppUtil.formateAmount(debitAmount, baseCurrency.getCcyEditField()));
			this.creditsCount.setValue(creditTotcount);
			//this.totCreditsByBatchCcy.setValue(PennantAppUtil.formateAmount(creditAmount, baseCurrency.getCcyEditField()));
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	private void fillJVPostingEntry(List<JVPostingEntry> JVPostingEntryList) {
		this.listBoxJVPostingEntry.getItems().clear();
		for (JVPostingEntry jvPostingEntry : JVPostingEntryList) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(jvPostingEntry.getTxnReference());
			listcell.setParent(listitem);
			listcell = new Listcell(jvPostingEntry.getAccount());
			listcell.setParent(listitem);
			listcell = new Listcell(jvPostingEntry.getAccountName());
			listcell.setParent(listitem);
			listcell = new Listcell(jvPostingEntry.getTxnCCy());
			listcell.setParent(listitem);
			listcell = new Listcell(jvPostingEntry.getTxnCode());
			listcell.setParent(listitem);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(jvPostingEntry.getTxnAmount(), jvPostingEntry.getTxnCCyEditField()));
			listcell.setStyle("text-align:right");
			BigDecimal txnRateAcCcy = jvPostingEntry.getExchRate_Batch().divide(jvPostingEntry.getExchRate_Ac(), 9, RoundingMode.HALF_DOWN);
			listcell.setParent(listitem);
			listcell = new Listcell(PennantApplicationUtil.amountFormate((txnRateAcCcy.multiply(jvPostingEntry.getTxnAmount())), jvPostingEntry.getTxnCCyEditField()));
			listcell.setStyle("text-align:right");
			listcell.setParent(listitem);
			listcell = new Listcell(PennantApplicationUtil.amountFormate((jvPostingEntry.getExchRate_Batch().multiply(jvPostingEntry.getTxnAmount())),
			        jvPostingEntry.getTxnCCyEditField()));
			listcell.setStyle("text-align:right");
			listcell.setParent(listitem);
			listcell = new Listcell(jvPostingEntry.getRecordStatus());
			listcell.setParent(listitem);
			listcell = new Listcell(jvPostingEntry.getRecordType());
			listcell.setParent(listitem);
			listitem.setAttribute("data", jvPostingEntry);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onJVPostingEntryItemDoubleClicked");
			this.listBoxJVPostingEntry.appendChild(listitem);

		}
	}

	public Window getWindow_JVPostingDialog() {
		return window_JVPostingDialog;
	}

	public void setWindow_JVPostingDialog(Window window_JVPostingDialog) {
		this.window_JVPostingDialog = window_JVPostingDialog;
	}

	public void setJVPostingEntryList(List<JVPostingEntry> jVPostingEntryList) {
		this.jVPostingEntryList = jVPostingEntryList;
	}

	public List<JVPostingEntry> getJVPostingEntryList() {
		return jVPostingEntryList;
	}

	public void setOldVar_jVPostingEntryList(List<JVPostingEntry> oldVar_jVPostingEntryList) {
		this.oldVar_jVPostingEntryList = oldVar_jVPostingEntryList;
	}

	public List<JVPostingEntry> getOldVar_jVPostingEntryList() {
		return oldVar_jVPostingEntryList;
	}

	public void onChange$batchReference(Event event) {
		disableNewJVPostingEntry();
	}

	private void disableNewJVPostingEntry() {
		logger.debug("Entering");
		if (StringUtils.trimToEmpty(this.batchReference.getValue()).equals("")) {
			this.button_JVPostingEntryList_NewJVPostingEntry.setDisabled(true);
		} else {
			this.button_JVPostingEntryList_NewJVPostingEntry.setDisabled(false);
		}
		logger.debug("Leaving");
	}

	private void checkListboxcount() {
		logger.debug("Entering");
		if (this.listBoxJVPostingEntry.getItemCount() > 0) {
			this.batchReference.setDisabled(true);
		} else {
			this.batchReference.setDisabled(false);
		}
		logger.debug("Leaving");
	}

	private void disablefileds() {
		this.debitCount.setReadonly(true);
		this.creditsCount.setReadonly(true);
		//		this.totCreditsByBatchCcy.setReadonly(false);
		//		this.totDebitsByBatchCcy.setReadonly(false);
	}

	public void onClick$btnVerify(Event event) {
		verifyAmounts();
	}

	public void verifyAmounts() {

		BigDecimal tempCredit = PennantAppUtil.formateAmount(creditAmount, baseCurrency.getCcyEditField());
		BigDecimal tempdebit = PennantAppUtil.formateAmount(debitAmount, baseCurrency.getCcyEditField());
		

		if (this.totDebitsByBatchCcy.getValue() != null && this.totDebitsByBatchCcy.getValue().compareTo(BigDecimal.ZERO) != 0) {
			if (this.totDebitsByBatchCcy.getValue().compareTo(tempdebit) != 0) {
				String label = Labels.getLabel("label_JVPostingDialog_TotDebitsByBatchCcy.value");
				throw new WrongValueException(this.totDebitsByBatchCcy, Labels.getLabel("AMOUNT_EQUAL_COMPARE", new String[] { label, label +":"+PennantAppUtil.amountFormate(tempdebit, 0) }));
			}
		}
		if (this.totCreditsByBatchCcy.getValue() != null && this.totCreditsByBatchCcy.getValue().compareTo(BigDecimal.ZERO) != 0) {
			if (this.totCreditsByBatchCcy.getValue().compareTo(tempCredit) != 0) {
				String label = Labels.getLabel("label_JVPostingDialog_TotCreditsByBatchCcy.value");
				throw new WrongValueException(this.totCreditsByBatchCcy, Labels.getLabel("AMOUNT_EQUAL_COMPARE", new String[] { label, label +":"+ PennantAppUtil.amountFormate(tempCredit, 0) }));
			}
		}

	}

}
