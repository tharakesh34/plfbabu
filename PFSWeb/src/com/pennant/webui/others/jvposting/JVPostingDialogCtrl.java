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

import java.io.File;
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
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
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
import org.zkoss.zul.North;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
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
	private final static Logger logger = Logger
			.getLogger(JVPostingDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_JVPostingDialog;
	protected Label window_JVPostingDialog_Title;
	protected Tab tab_JVSummary;
	protected Tab tab_Accounting;

	protected Row row0;
	protected Label label_BatchReference;
	protected Hlayout hlayout_BatchReference;
	protected Space space_BatchReference;

	protected Textbox batchReference;
	protected Label label_Batch;
	protected Hlayout hlayout_Batch;
	protected Space space_Batch;

	protected Label label_BaseCcy;
	protected ExtendedCombobox baseCCy;
	protected Hlayout hlayout_baseCCy;
	protected Space space_baseCCy;

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
	protected Row row5;
	protected Label label_BatchPurpose;
	protected Hlayout hlayout_BatchPurpose;
	protected Space space_BatchPurpose;
	protected Textbox batchPurpose;
	protected Label label_Upload;
	protected Hlayout hlayout_Upload;
	protected Space space_Upload;
	protected Textbox fileName;
	protected Button btn_Upload;

	protected Textbox batch;
	protected Label label_ExchangeRateType;
	protected ExtendedCombobox exchangeRateType;
	protected Space space_exchangeRateType;
	protected Hlayout hlayout_exchangeRateType;

	protected Label label_PostingBranch;
	protected ExtendedCombobox postingBranch;
	protected Space space_postingBranch;
	protected Hlayout hlayout_postingBranch;

	protected Label recordStatus;
	protected Label recordType;
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected South south;
	private boolean enqModule = false;
	private boolean rePostingModule = false;

	// not auto wired vars
	private JVPosting jVPosting; // overhanded per param
	private transient JVPostingListCtrl jVPostingListCtrl; // overhanded per
	protected Listbox listBoxJVPostingEntry;
	protected Listbox listBoxJVPostingAccounting;
	private List<JVPostingEntry> jVPostingEntryList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> deletedJVPostingEntryList = new ArrayList<JVPostingEntry>();

	// param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_Batch;
	private transient String oldVar_FileName;
	private transient String oldVar_BatchReference;
	private transient String oldVar_baseCCy;
	private transient String oldVar_baseCCyDesc;
	private transient String oldVar_postingBranch;
	private transient String oldVar_postingBranchDesc;
	private transient String oldVar_exchangeRateType;
	private transient String oldVar_exchangeRateTypeDesc;
	private transient double oldVar_TotDebitsByBatchCcy;
	private transient double oldVar_TotCreditsByBatchCcy;
	private transient int oldVar_DebitCount;
	private transient int oldVar_CreditsCount;
	private transient String oldVar_BatchPurpose;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered = false;

	protected Button importFile;
	private File directory;
	protected Paging pagingBatchImportList;
	private boolean saveUploadFile;
	private Media media;
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
	protected Tabbox tabbox;
	protected Component jVSummaryEntryListPage;
	protected Component accountingEntryListPage;

	// ServiceDAOs / Domain Classes
	private transient JVPostingService jVPostingService;
	private boolean proceed = false;
	protected Button btnNewJVPostingEntry; // autowired
	protected Button btnValidate; // autowired
	protected Textbox moduleType; // autowired
	private Currency aCurrency = null;
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
			} else if (args.containsKey("rePostingModule")) {
				rePostingModule = (Boolean) args.get("rePostingModule");
			} else {
				enqModule = false;
			}

			if (args.containsKey("jVPostingListCtrl")) {
				setJVPostingListCtrl((JVPostingListCtrl) args
						.get("jVPostingListCtrl"));
			} else {
				setJVPostingListCtrl(null);
			}

			// READ OVERHANDED params !
			if (args.containsKey("jVPosting")) {
				this.jVPosting = (JVPosting) args.get("jVPosting");
				JVPosting befImage = new JVPosting();
				BeanUtils.copyProperties(this.jVPosting, befImage);
				befImage.setLastMntOn(this.jVPosting.getLastMntOn());
				this.jVPosting.setBefImage(befImage);
				// Setting Re-Posting Module
				this.jVPosting.setRePostingModule(rePostingModule);

				setJVPosting(this.jVPosting);
			} else {
				setJVPosting(null);
			}
			doLoadWorkFlow(this.jVPosting.isWorkflow(),
					this.jVPosting.getWorkflowId(),
					this.jVPosting.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(),
						"JVPostingDialog");
			} else {
				getUserWorkspace().alocateAuthorities("JVPostingDialog");
			}
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (getJVPosting().isNewRecord()) {
				aCurrency = PennantAppUtil.getCurrencyBycode(SystemParameterDetails
						.getSystemParameterValue("EXT_BASE_CCY")
						.toString());
				getJVPosting().setCurrency(aCurrency.getCcyCode());
				getJVPosting().setCurrencyDesc(aCurrency.getCcyDesc());
				getJVPosting().setCcyNumber(aCurrency.getCcyNumber());
				getJVPosting().setCurrencyEditField(aCurrency
						.getCcyEditField());
				getJVPosting().setCreditCCyEditField(aCurrency
						.getCcyEditField());
				getJVPosting().setDebitCCyEditField(aCurrency
						.getCcyEditField());
			}
			doSetFieldProperties();
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxJVPostingEntry.setHeight(this.borderLayoutHeight - 350 + "px");
			this.listBoxJVPostingAccounting.setHeight(this.borderLayoutHeight - 350 + "px");
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

			ScreenCTL.displayNotes(
					getNotes("JVPosting", String.valueOf(getJVPosting().getBatchReference()),
							getJVPosting().getVersion()), this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * Call the JVPosting dialog with a new empty entry. <br>
	 */
	public void onClick$btnNewJVPostingEntry(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new JVPosting object, We GET it from the backend.
		JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		aJVPostingEntry.setNewRecord(true);
		aJVPostingEntry.setWorkflowId(0);
		aJVPostingEntry.setBatchReference(Long.valueOf(this.batchReference.getValue()));
		aJVPostingEntry.setFileName(getJVPosting().getFilename());
		aJVPostingEntry.setTxnReference(this.listBoxJVPostingEntry.getItems().size()+1);
		showDetailView(aJVPostingEntry, false, true);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param JVPosting
	 *            (aJVPosting)
	 * @throws Exception
	 */
	private void showDetailView(JVPostingEntry aJVPostingEntry, boolean isFilter, boolean setNewRecord)
			throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("jVPosting", aJVPosting);
		if (this.moduleType != null
				&& this.moduleType.getValue().equalsIgnoreCase(
						PennantConstants.MODULETYPE_ENQ)) {
			map.put("enqModule", true);
		} else if (this.moduleType != null
				&& this.moduleType.getValue().equalsIgnoreCase(
						PennantConstants.MODULETYPE_REPOSTING)) {
			map.put("rePostingModule", true);
		} else {
			map.put("enqModule", false);
		}
		if(setNewRecord){
			map.put("newRecord", true);
		}

		map.put("roleCode", getRole());
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the JVPostingListbox from the
		 * dialog when we do a delete, edit or insert a JVPosting.
		 */

		map.put("jVPostingDialogCtrl", this);
		map.put("jVPostingEntry", aJVPostingEntry);

		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		doSetValidation();
		// fill the JVPosting object with the components data
		doWriteComponentsToBean(aJVPosting, false);
		map.put("jVPosting",aJVPosting);

		// call the zul-file with the parameters packed in a map
		try {
			String fileName = null;
			if (isFilter) {
				fileName = "/WEB-INF/pages/Others/JVPosting/JVPostingEntrySearch.zul";
			} else {
				fileName = "/WEB-INF/pages/Others/JVPosting/JVPostingEntryDialog.zul";
			}
			Executions.createComponents(fileName, window_JVPostingDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see:
	 * com.pennant.webui.others.jvposting.model.JVPostingListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onJVPostingEntryItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());
		// get the selected JVPosting object
		final Listitem item = this.listBoxJVPostingEntry.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final JVPostingEntry aJVPostingEntry = (JVPostingEntry) item
					.getAttribute("data");
			showDetailView(aJVPostingEntry, false, false);
		}
		logger.debug("Leaving");
	}


	public void onFulfill$baseCCy(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			Object currency = this.baseCCy.getObject();
			if(currency != null && !(currency instanceof String)){
				Currency aCurrency = (Currency) this.baseCCy.getObject();
				getJVPosting().setCurrency(aCurrency.getCcyCode());
				getJVPosting().setCcyNumber(aCurrency.getCcyNumber());
				getJVPosting().setCurrencyDesc(aCurrency.getCcyDesc());
				getJVPosting().setCurrencyEditField(
						aCurrency.getCcyEditField());
				this.totDebitsByBatchCcy.setFormat(PennantApplicationUtil
						.amountFormate(BigDecimal.ZERO,
								getJVPosting().getCurrencyEditField()));
				this.totCreditsByBatchCcy.setFormat(PennantApplicationUtil
						.amountFormate(BigDecimal.ZERO,
								getJVPosting().getCurrencyEditField()));
				renderJVPostingEntries(getJVPostingEntryList());
				this.tab_JVSummary.setSelected(true);
			} 
		} catch (final Exception e) {
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

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(),
					aJVPosting.isNewRecord()));
			// stores the initial data for comparing if they are changed
			doStoreInitValues();

			doSetWindowTitle();
			setDialog(this.window_JVPostingDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doSetWindowTitle() {
		logger.debug("Entering");
		if (this.enqModule) {
			this.window_JVPostingDialog_Title.setValue(Labels
					.getLabel("window_JVPostingEnqDialog.title"));
		} else if (this.rePostingModule) {
			this.window_JVPostingDialog_Title.setValue(Labels
					.getLabel("window_JVPostingRePostingDialog.title"));
		} else {
			this.window_JVPostingDialog_Title.setValue(Labels
					.getLabel("window_JVPostingDialog.title"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPosting
	 */
	public void doWriteBatchDetailsToBean(JVPosting aJVPosting)
			throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Batch
		try {
			aJVPosting.setBatch(this.batch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Batch Reference
		try {
			aJVPosting.setBatchReference(Long.valueOf(this.batchReference
					.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Batch Currency
		try {
			aJVPosting.setCurrency(this.baseCCy.getValue());
			aJVPosting.setCurrencyDesc(this.baseCCy.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// exchangeRateType
		try {
			aJVPosting.setExchangeRateType(this.exchangeRateType.getValue());
			aJVPosting.setRateTypeDescription(this.exchangeRateType
					.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tot Debits By Batch Ccy
		try {
			aJVPosting.setTotDebitsByBatchCcy(PennantApplicationUtil
					.unFormateAmount(this.totDebitsByBatchCcy.getValue(),
							getJVPosting().getCurrencyEditField()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tot Credits By Batch Ccy
		try {
			aJVPosting.setTotCreditsByBatchCcy(PennantApplicationUtil
					.unFormateAmount(this.totCreditsByBatchCcy.getValue(),
							getJVPosting().getCurrencyEditField()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Debit Count
		try {
			aJVPosting.setDebitCount(this.debitCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Credits Count
		try {
			aJVPosting.setCreditsCount(this.creditsCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Batch Purpose
		try {
			aJVPosting.setBatchPurpose(this.batchPurpose.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// fileName
		try {
			aJVPosting.setFilename(this.fileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
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

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes,
				isWorkFlowEnabled(), isFirstTask(), this.userAction,
				this.batch, null));

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;

		if (readOnly
				|| (!readOnly && (PennantConstants.RECORD_TYPE_DEL
						.equals(jVPosting.getRecordType()))) || enqModule) {
			tempReadOnly = true;
		}
		// Batch Header Details		
		readOnlyComponent(isReadOnly("JVPostingDialog_Batch"), this.batch);
		readOnlyComponent(true, this.batchReference);
		readOnlyComponent(isReadOnly("JVPostingDialog_BatchCcy"), this.baseCCy);
		readOnlyComponent(isReadOnly("JVPostingDialog_Branch"), this.postingBranch);

		setExtAccess("JVPostingDialog_ExchRateType", tempReadOnly,
				this.exchangeRateType, row2);

		readOnlyComponent(true, this.totCreditsByBatchCcy);

		readOnlyComponent(true, this.totDebitsByBatchCcy);
		readOnlyComponent(true, this.debitCount);
		readOnlyComponent(true, this.creditsCount);
		readOnlyComponent(isReadOnly("JVPostingDialog_BatchPurpose"), this.batchPurpose);
		//readOnlyComponent(isReadOnly("JVPostingDialog_btnUpload"), this.btn_Upload);

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
		getUserWorkspace().alocateAuthorities("JVPosting");
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed(
					"button_JVPostingEntry_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed(
					"button_JVPostingDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed(
					"button_JVPostingDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed(
					"button_JVPostingDialog_btnSave"));
		}
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);
		// Hard Coded
		this.btnSave.setVisible(true);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.batchReference.setMaxlength(50);
		this.fileName.setMaxlength(50);
		this.baseCCy.setMaxlength(3);
		this.baseCCy.setMandatoryStyle(true);
		this.baseCCy.setTextBoxWidth(40);
		this.baseCCy.setModuleName("Currency");
		this.baseCCy.setValueColumn("CcyCode");
		this.baseCCy.setDescColumn("CcyDesc");
		this.baseCCy.setValidateColumns(new String[] { "CcyCode" });

		this.postingBranch.setMaxlength(4);
		this.postingBranch.setMandatoryStyle(true);
		this.postingBranch.setTextBoxWidth(40);
		this.postingBranch.setModuleName("Branch");
		this.postingBranch.setValueColumn("BranchCode");
		this.postingBranch.setDescColumn("BranchDesc");
		this.postingBranch.setValidateColumns(new String[] { "BranchCode" });		

		this.exchangeRateType.setMaxlength(8);
		this.exchangeRateType.setMandatoryStyle(true);
		this.exchangeRateType.setTextBoxWidth(40);
		this.exchangeRateType.setModuleName("RateType");
		this.exchangeRateType.setValueColumn("RateTypeCode");
		this.exchangeRateType.setDescColumn("RateTypeDescription");
		this.exchangeRateType
		.setValidateColumns(new String[] { "RateTypeCode" });
		this.debitCount.setMaxlength(10);
		this.creditsCount.setMaxlength(10);
		this.totDebitsByBatchCcy.setMaxlength(18);
		this.totDebitsByBatchCcy.setFormat(PennantApplicationUtil
				.amountFormate(this.totDebitsByBatchCcy.getValue(),
						getJVPosting().getCurrencyEditField()));
		this.totCreditsByBatchCcy.setMaxlength(18);
		this.totCreditsByBatchCcy.setFormat(PennantApplicationUtil
				.amountFormate(this.totCreditsByBatchCcy.getValue(),
						getJVPosting().getCurrencyEditField()));
		this.batchPurpose.setMaxlength(35);

		logger.debug("Leaving");
	}

	/**
	 * Stores the initial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_Batch = this.batch.getValue();
		this.oldVar_FileName = this.fileName.getValue();
		this.oldVar_BatchReference = this.batchReference.getValue();
		this.oldVar_baseCCy = this.baseCCy.getValue();
		this.oldVar_baseCCyDesc = this.baseCCy.getDescription();
		this.oldVar_postingBranch = this.postingBranch.getValue();
		this.oldVar_postingBranchDesc = this.postingBranch.getDescription();
		this.oldVar_exchangeRateType = this.exchangeRateType.getValue();
		this.oldVar_exchangeRateTypeDesc = this.exchangeRateType
				.getDescription();
		this.oldVar_DebitCount = this.debitCount.intValue();
		this.oldVar_CreditsCount = this.creditsCount.intValue();
		this.oldVar_TotDebitsByBatchCcy = this.totDebitsByBatchCcy
				.doubleValue();
		this.oldVar_TotCreditsByBatchCcy = this.totCreditsByBatchCcy
				.doubleValue();
		this.oldVar_BatchPurpose = this.batchPurpose.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.batch.setValue(this.oldVar_Batch);
		this.batchReference.setValue(this.oldVar_BatchReference);
		this.baseCCy.setValue(this.oldVar_baseCCy);
		this.baseCCy.setDescription(this.oldVar_baseCCyDesc);

		this.postingBranch.setValue(this.oldVar_postingBranch);
		this.postingBranch.setDescription(this.oldVar_postingBranchDesc);

		this.exchangeRateType.setValue(this.oldVar_exchangeRateType);
		this.exchangeRateType.setDescription(this.oldVar_exchangeRateTypeDesc);
		this.fileName.setValue(this.oldVar_FileName);
		this.debitCount.setValue(this.oldVar_DebitCount);
		this.creditsCount.setValue(this.oldVar_CreditsCount);
		this.totDebitsByBatchCcy.setValue(new BigDecimal(
				this.oldVar_TotDebitsByBatchCcy));
		this.totCreditsByBatchCcy.setValue(new BigDecimal(
				this.oldVar_TotCreditsByBatchCcy));
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
		this.batch.setValue(aJVPosting.getBatch());
		if (aJVPosting.isNewRecord()) {
			this.batchReference.setValue("0");
			this.baseCCy.setValue(aJVPosting.getCurrency());
			this.postingBranch.setValue(getUserWorkspace().getLoginUserDetails().getLoginBranchCode());
			getJVPosting().setPostingDate(DateUtility.getSystemDate());
		} else {
			this.batchReference.setValue(String.valueOf(aJVPosting
					.getBatchReference()));
			this.baseCCy.setValue(aJVPosting.getCurrency());
			this.baseCCy.setDescription(aJVPosting.getCurrencyDesc());
			this.postingBranch.setValue(aJVPosting.getBranch());
			this.postingBranch.setDescription(aJVPosting.getBranchDesc());
		}
		this.exchangeRateType.setValue(aJVPosting.getExchangeRateType());
		this.exchangeRateType.setDescription(aJVPosting
				.getRateTypeDescription());
		this.fileName.setValue(aJVPosting.getFilename());
		this.debitCount.setValue(aJVPosting.getDebitCount());
		this.creditsCount.setValue(aJVPosting.getCreditsCount());
		this.totDebitsByBatchCcy.setValue(PennantAppUtil.formateAmount(
				getJVPosting().getTotDebitsByBatchCcy(),
				aJVPosting.getCurrencyEditField()));
		this.totCreditsByBatchCcy.setValue(PennantAppUtil.formateAmount(
				getJVPosting().getTotCreditsByBatchCcy(),
				aJVPosting.getCurrencyEditField()));
		this.batchPurpose.setValue(aJVPosting.getBatchPurpose());

		this.recordStatus.setValue(aJVPosting.getRecordStatus());
		doFillJVPostingEntryDetails(aJVPosting.getJVPostingEntrysList());
		logger.debug("Leaving");
	}

	public void doFillJVPostingEntryDetails(List<JVPostingEntry> aJVPostingEntryList) {
		logger.debug("Entering");
		setJVPostingEntryList(aJVPostingEntryList);
		renderJVPostingEntries(getJVPostingEntryList());

		if(aJVPostingEntryList != null && aJVPostingEntryList.size() > 0){
			this.postingBranch.setReadonly(true);
			this.listBoxJVPostingEntry.setHeight((aJVPostingEntryList.size()*25)+200+"px");
		}else {
			this.postingBranch.setReadonly(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPosting
	 */
	public void doWriteComponentsToBean(JVPosting aJVPosting, boolean addList)
			throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Batch Branch
		try {
			aJVPosting.setBranch(this.postingBranch.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Batch Reference
		try {
			aJVPosting.setBatchReference(Long.valueOf(this.batchReference
					.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Batch
		try {
			aJVPosting.setBatch(this.batch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Batch Currency
		try {
			aJVPosting.setCurrency(this.baseCCy.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// fileName
		try {
			aJVPosting.setFilename(this.fileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// exchangeRateType
		try {
			aJVPosting.setExchangeRateType(this.exchangeRateType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Debit Count
		try {
			aJVPosting.setDebitCount(this.debitCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Credits Count
		try {
			aJVPosting.setCreditsCount(this.creditsCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tot Debits By Batch Ccy
		try {
			aJVPosting.setTotDebitsByBatchCcy(PennantAppUtil.unFormateAmount(
					this.totDebitsByBatchCcy.getValue(), getJVPosting()
					.getCurrencyEditField()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tot Credits By Batch Ccy
		try {
			aJVPosting.setTotCreditsByBatchCcy(PennantAppUtil.unFormateAmount(
					this.totCreditsByBatchCcy.getValue(), getJVPosting()
					.getCurrencyEditField()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Batch Purpose
		try {
			aJVPosting.setBatchPurpose(this.batchPurpose.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*// jVPostingEntryList setting
		try {
			// Preparing Updated List with Deleted Flag
			doUpdateJVPostingEntrysList(aJVPosting);
		} catch (WrongValueException we) {
			wve.add(we);
		}*/
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if(addList){
			aJVPosting.setJVPostingEntrysList(this.jVPostingEntryList);
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

		if (!StringUtils.trimToEmpty(this.oldVar_BatchReference).equals(
				StringUtils.trimToEmpty(this.batchReference.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_Batch).equals(
				StringUtils.trimToEmpty(this.batch.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_baseCCy).equals(StringUtils.trimToEmpty(this.baseCCy.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_postingBranch).equals(StringUtils.trimToEmpty(this.postingBranch.getValue()))) {
			return true;
		}
		if (this.oldVar_DebitCount != this.debitCount.intValue()) {
			return true;
		}
		if (this.oldVar_CreditsCount != this.creditsCount.intValue()) {
			return true;
		}
		if (this.oldVar_TotDebitsByBatchCcy != this.totDebitsByBatchCcy
				.doubleValue()) {
			return true;
		}
		if (this.oldVar_TotCreditsByBatchCcy != this.totCreditsByBatchCcy
				.doubleValue()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_BatchPurpose).equals(
				StringUtils.trimToEmpty(this.batchPurpose.getValue()))) {
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
		// Batch
		if (!this.batch.isReadonly()) {
			this.batch.setConstraint(new PTStringValidator(Labels
					.getLabel("label_JVPostingDialog_Batch.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}



		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.batch.setConstraint("");
		this.batchReference.setConstraint("");
		this.fileName.setConstraint("");
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
		// Cmt Branch
		if (baseCCy.isButtonVisible()) {
			this.baseCCy.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_BatchCcy.value"),null,true,true));
		}
		// Cmt Ccy
		if (postingBranch.isButtonVisible()) {
			this.postingBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_PostingBranch.value"),null,true,true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.baseCCy.setConstraint("");
		this.postingBranch.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
		logger.debug("Entering");
		this.batch.setErrorMessage("");
		this.batchReference.setErrorMessage("");
		this.fileName.setErrorMessage("");
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
		final JdbcSearchObject<JVPosting> soJVPosting = getJVPostingListCtrl()
				.getSearchObj();
		getJVPostingListCtrl().pagingJVPostingList.setActivePage(0);
		getJVPostingListCtrl().getPagedListWrapper().setSearchObject(
				soJVPosting);
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
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				/*if (getjVPostingEntry().getBatchReference() != 0
						&& getjVPostingEntry().getTxnReference() != 0) {
					getJVPostingService().update(getjVPostingEntry(), "_Temp");
				}*/
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			getJVPostingListCtrl().refreshList();
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
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aJVPosting.getBatchReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aJVPosting.getRecordType()).equals("")) {
				aJVPosting.setVersion(aJVPosting.getVersion() + 1);
				aJVPosting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aJVPosting.setRecordStatus(userAction.getSelectedItem()
							.getValue().toString());
					aJVPosting.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(),
							aJVPosting.getNextTaskId(), aJVPosting);
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
		this.fileName.setValue("");
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

		if(("Submit".equals(userAction.getSelectedItem().getLabel()) || 
				"Approve".equals(userAction.getSelectedItem().getLabel())) && !proceed){
			PTMessageUtils.showErrorMessage(Labels.getLabel("VALIDATE_ACCOUNTS"));
			return;
		}
		
		if (getJVPosting().isNewRecord() &&  getJVPostingService()
				.getJVPostingByFileName(
						this.batch.getValue()) != null) {
			PTMessageUtils.showErrorMessage(Labels.getLabel(
					"BATCH_ALREADY_EXISTS",
					new String[] {this.batch.getValue(), DateUtility.getSystemDate().toString() }));
			return;
		}

		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		boolean isNew = false;
		if (isWorkFlowEnabled()) {
			aJVPosting.setRecordStatus(userAction.getSelectedItem().getValue()
					.toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(),
					aJVPosting.getNextTaskId(), aJVPosting);
		}
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL
				.equals(aJVPosting.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the JVPosting object with the components data
			doWriteComponentsToBean(aJVPosting, true);
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
			if(aJVPosting.getRecordStatus().equals("Cancelled")){
				if (doProcess(aJVPosting, tranType)) {
					// doWriteBeanToComponents(aJVPosting);
					// refreshList();
					getJVPostingListCtrl().refreshList();
					closeDialog(this.window_JVPostingDialog, "JVPosting");
				}
			}else if (aJVPosting.getTotCreditsByBatchCcy().compareTo(
					aJVPosting.getTotDebitsByBatchCcy()) == 0) {
				if (doProcess(aJVPosting, tranType)) {
					// doWriteBeanToComponents(aJVPosting);
					// refreshList();
					getJVPostingListCtrl().refreshList();
					closeDialog(this.window_JVPostingDialog, "JVPosting");
				}
			} else {
				PTMessageUtils
				.showErrorMessage(Labels.getLabel(
						"FIELD_NOT_MATCHED",
						new String[] {
								Labels.getLabel("label_JVPostingDialog_TotDebitsByBatchCcy.value"),
								Labels.getLabel("label_JVPostingDialog_TotCreditsByBatchCcy.value") }));
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
		aJVPosting.setLastMntBy(getUserWorkspace().getLoginUserDetails()
				.getLoginUsrID());
		aJVPosting.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aJVPosting.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
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
				processCompleted = doSaveProcess(
						getAuditHeader(aJVPosting, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aJVPosting,
						PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(
					getAuditHeader(aJVPosting, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		JVPosting aJVPosting = (JVPosting) auditHeader.getAuditDetail()
				.getModelData();
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader
							.getAuditTranType())) {
						auditHeader = getJVPostingService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getJVPostingService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove
							.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getJVPostingService().doApprove(
								auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aJVPosting
								.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject
							.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getJVPostingService().doReject(
								auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aJVPosting
								.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(
								this.window_JVPostingDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_JVPostingDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(
								getNotes("JVPosting",
										String.valueOf(aJVPosting.getBatchReference()),
										aJVPosting.getVersion()), true);
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
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aJVPosting.getBefImage(), aJVPosting);
		return new AuditHeader(aJVPosting.getBatchReference() + "", null, null,
				null, auditDetail, aJVPosting.getUserDetails(), getOverideMap());
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
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setDefaultDirectory() throws Exception {
		String path = "C:/Pennant/UPP";
		File file = new File(path);

		if (!file.exists() || !file.canWrite()) {
			throw new Exception(Labels.getLabel("dir_not_found"));
		}

		setDirectory(file);
	}

	public void doUpdateBatchDetails(JVPosting jVPosting) {
		this.debitCount.setValue(jVPosting.getDebitCount());
		this.creditsCount.setValue(jVPosting.getCreditsCount());
		this.totCreditsByBatchCcy.setValue(PennantAppUtil.formateAmount(
				jVPosting.getTotCreditsByBatchCcy(), getJVPosting()
				.getCurrencyEditField()));
		this.totDebitsByBatchCcy.setValue(PennantAppUtil.formateAmount(
				jVPosting.getTotDebitsByBatchCcy(), getJVPosting()
				.getCurrencyEditField()));
	}

	public void onSelect$tab_Accounting(Event event) {
		fillAccountingTab();
	}

	private void fillAccountingTab(){
		List<JVPostingEntry> acEntryList = new ArrayList<JVPostingEntry>();
		for (Listitem li : this.listBoxJVPostingEntry.getItems()) {
			acEntryList.add((JVPostingEntry) li.getAttribute("data"));
		}
		acEntryList = PostingsPreparationUtil.prepareAccountingEntryList(acEntryList, getJVPosting().getCurrency(),
				getJVPosting().getCcyNumber(), getJVPosting().getCurrencyEditField());
		if(acEntryList != null && acEntryList.size() > 0){
			this.listBoxJVPostingEntry.setHeight((acEntryList.size()*25)+200+"px");
		}
		renderAccountingEntries(acEntryList, getJVPosting().getCurrencyEditField());
	}

	private void renderAccountingEntries(List<JVPostingEntry> acEntryList, int batchCcyEditField) {
		this.listBoxJVPostingAccounting.getItems().clear();
		Listitem item = null;
		Listcell lc;
		for (JVPostingEntry accountingEntry : acEntryList) {
			item = new Listitem();
			lc = new Listcell(
					PennantApplicationUtil.formatAccountNumber(accountingEntry.getAccount()));
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getAccountName());
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getAcType());
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getTxnEntry());
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getTxnCCy());
			lc.setParent(item);	
			lc = new Listcell(PennantAppUtil.amountFormate(
					accountingEntry.getTxnAmount(),
					accountingEntry.getTxnCCyEditField()));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getAccCCy());
			lc.setParent(item);					
			lc = new Listcell(PennantAppUtil.amountFormate(
					accountingEntry.getTxnAmount_Ac(),
					accountingEntry.getAccCCyEditField()));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getPostingStatus());
			lc.setParent(item);

			this.listBoxJVPostingAccounting.appendChild(item);
		}
	}

	public void renderJVPostingEntries(List<JVPostingEntry> entryList) {
		BigDecimal creditAmount = BigDecimal.ZERO;
		BigDecimal debitAmount = BigDecimal.ZERO;
		int creditCount = 0;
		int debitCount = 0;
		this.listBoxJVPostingEntry.getItems().clear();
		Listitem item = null;
		Listcell lc;
		for (JVPostingEntry  jvPostingEntry: entryList) {
			if(jvPostingEntry.isExternalAccount()){
				item = new Listitem();
				lc = new Listcell(String.valueOf(jvPostingEntry.getTxnReference()));
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.formatAccountNumber(jvPostingEntry
								.getAccount()));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(
						jvPostingEntry.getPostingDate(), PennantConstants.dateFormat));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(
						jvPostingEntry.getValueDate(), PennantConstants.dateFormat));
				lc.setParent(item);
				if (jvPostingEntry.getTxnEntry().equalsIgnoreCase(PennantConstants.CREDIT)) {
					lc = new Listcell(Labels.getLabel("common.Credit"));
				} else {
					lc = new Listcell(Labels.getLabel("common.Debit"));
				}
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getTxnCCy());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(
						jvPostingEntry.getTxnAmount(),
						jvPostingEntry.getTxnCCyEditField()));
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getNarrLine1());
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getRecordType());
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getModifiedFlag());
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getRecordType());
				if (jvPostingEntry.isDeletedFlag()) {
					item.setStyle("background-color: #E87575;");
				}
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getValidationStatus());
				lc.setTooltiptext(jvPostingEntry.getValidationStatus());
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getPostingStatus());
				lc.setTooltiptext(jvPostingEntry.getPostingStatus());
				lc.setParent(item);
				item.setAttribute("data", jvPostingEntry);
				if(!jvPostingEntry.isDeletedFlag()){
					if(jvPostingEntry.getTxnEntry().equalsIgnoreCase(PennantConstants.CREDIT)){
						creditAmount = creditAmount.add(PennantAppUtil.unFormateAmount(
								PennantAppUtil.formateAmount(CalculationUtil.getConvertedAmount(jvPostingEntry.getTxnCCy(), 
										getJVPosting().getCurrency(), jvPostingEntry.getTxnAmount()),
										getJVPosting().getCurrencyEditField()),getJVPosting().getCurrencyEditField()));
						creditCount = creditCount+1;
					}else {
						debitAmount = debitAmount.add(PennantAppUtil.unFormateAmount(
								PennantAppUtil.formateAmount(CalculationUtil.getConvertedAmount(jvPostingEntry.getTxnCCy(), 
										getJVPosting().getCurrency(), jvPostingEntry.getTxnAmount()),
										getJVPosting().getCurrencyEditField()),getJVPosting().getCurrencyEditField()));
						debitCount =  debitCount+1;
					}
					ComponentsCtrl.applyForward(item,
							"onDoubleClick=onJVPostingEntryItemDoubleClicked");
				}
				this.listBoxJVPostingEntry.appendChild(item);
			}
		}
		if(baseCCy.getValue().equals(PennantConstants.CURRENCY_BHD) || baseCCy.getValue().equals(PennantConstants.CURRENCY_KWD)){
			creditAmount = creditAmount.setScale(3,RoundingMode.HALF_DOWN);
			debitAmount = debitAmount.setScale(3,RoundingMode.HALF_DOWN);
		}else {
			creditAmount = creditAmount.setScale(2,RoundingMode.HALF_DOWN);
			debitAmount = debitAmount.setScale(2,RoundingMode.HALF_DOWN);
		}
		this.totCreditsByBatchCcy.setValue(PennantAppUtil.formateAmount(
				creditAmount, getJVPosting()
				.getCurrencyEditField()));
		this.creditsCount.setValue(creditCount);
		this.totDebitsByBatchCcy.setValue(PennantAppUtil.formateAmount(
				debitAmount, getJVPosting()
				.getCurrencyEditField()));
		this.debitCount.setValue(debitCount);
	}

	/**
	 * when the "validate" button is clicked. <br>
	 * Stores the default values, sets the validation and validates the given
	 * finance details.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception,AccountNotFoundException {
		logger.debug("Entering" + event.toString());
		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		aJVPosting.setUserDetails(getUserWorkspace().getLoginUserDetails());
		if(getJVPostingService().doAccountValidation(aJVPosting, getJVPostingEntryList())){
			this.setProceed(true);
		}
		renderJVPostingEntries(getJVPostingEntryList());
		
		logger.debug("Leaving" + event.toString());
	}

	public Window getWindow_JVPostingDialog() {
		return window_JVPostingDialog;
	}

	public void setWindow_JVPostingDialog(Window windowJVPostingDialog) {
		this.window_JVPostingDialog = windowJVPostingDialog;
	}

	public void setJVPostingEntryList(List<JVPostingEntry> jVPostingEntryList) {
		this.jVPostingEntryList = jVPostingEntryList;
	}

	public List<JVPostingEntry> getJVPostingEntryList() {
		return jVPostingEntryList;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public boolean isSaveUploadFile() {
		return saveUploadFile;
	}

	public void setSaveUploadFile(boolean saveUploadFile) {
		this.saveUploadFile = saveUploadFile;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public List<JVPostingEntry> getDeletedJVPostingEntryList() {
		return deletedJVPostingEntryList;
	}

	public void setDeletedJVPostingEntryList(
			List<JVPostingEntry> deletedJVPostingEntryList) {
		this.deletedJVPostingEntryList = deletedJVPostingEntryList;
	}

	public boolean isProceed() {
		return proceed;
	}

	public void setProceed(boolean proceed) {
		this.proceed = proceed;
	}
}
