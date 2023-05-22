/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : JVPostingDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified
 * Date : 21-06-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.others.jvposting;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.expenses.LegalExpensesService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.AccountingUtil;
import com.pennant.pff.accounting.PostAgainst;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/others/JVPosting/jVPostingDialog.zul file.
 */
public class JVPostingDialogCtrl extends GFCBaseCtrl<JVPosting> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(JVPostingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_JVPostingDialog;
	protected Label window_JVPostingDialog_Title;
	protected Tab tab_JVSummary;
	protected Tab tab_Accounting;

	protected Row row_expReference;
	protected Label label_ExpReference;
	protected Label label_ExpAmoount;

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

	protected ExtendedCombobox expReference;
	protected ExtendedCombobox postingDivision;
	protected ExtendedCombobox reference;
	protected Combobox postingAgainst;

	protected Textbox batch;
	protected Label label_ExchangeRateType;
	protected ExtendedCombobox exchangeRateType;
	protected Space space_exchangeRateType;

	protected Label label_PostingBranch;
	protected ExtendedCombobox postingBranch;
	protected Space space_postingBranch;

	protected Label recordType;
	private boolean enqModule = false;
	private boolean rePostingModule = false;
	private boolean isExpRequired = false;
	// not auto wired vars
	private JVPosting jVPosting; // overhanded per param
	private transient JVPostingListCtrl jVPostingListCtrl; // overhanded per
	protected Listbox listBoxJVPostingEntry;
	protected Listbox listBoxJVPostingAccounting;
	private List<JVPostingEntry> jVPostingEntryList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> deletedJVPostingEntryList = new ArrayList<JVPostingEntry>();

	protected Button importFile;
	private File directory;
	protected Paging pagingBatchImportList;
	private boolean saveUploadFile;
	private Media media;

	protected Tabbox tabbox;
	protected Component jVSummaryEntryListPage;
	protected Component accountingEntryListPage;

	// ServiceDAOs / Domain Classes
	private transient JVPostingService jVPostingService;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	private boolean proceed = false;
	protected Button btnNewJVPostingEntry; // autowired
	protected Button btnValidate; // autowired
	protected Textbox moduleType; // autowired
	private Currency aCurrency = null;
	private Decimalbox expAmount;
	private transient LegalExpensesService legalExpensesService;

	/**
	 * default constructor.<br>
	 */
	public JVPostingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "JVPostingDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected JVPosting object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_JVPostingDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_JVPostingDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else if (arguments.containsKey("rePostingModule")) {
				rePostingModule = (Boolean) arguments.get("rePostingModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("jVPostingListCtrl")) {
				setJVPostingListCtrl((JVPostingListCtrl) arguments.get("jVPostingListCtrl"));
			} else {
				setJVPostingListCtrl(null);
			}
			if (arguments.containsKey("isExpRequired")) {
				isExpRequired = (Boolean) arguments.get("isExpRequired");
				if (!isExpRequired) {
					this.row_expReference.setVisible(false);
				}
			}
			// READ OVERHANDED params !
			if (arguments.containsKey("jVPosting")) {
				this.jVPosting = (JVPosting) arguments.get("jVPosting");
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
			doLoadWorkFlow(this.jVPosting.isWorkflow(), this.jVPosting.getWorkflowId(), this.jVPosting.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "JVPostingDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (getJVPosting().isNewRecord()) {
				aCurrency = PennantAppUtil.getCurrencyBycode(SysParamUtil.getValueAsString("EXT_BASE_CCY"));
				getJVPosting().setCurrency(aCurrency.getCcyCode());
			}
			doSetFieldProperties();
			this.listBoxJVPostingEntry.setHeight(this.borderLayoutHeight - 280 + "px");
			this.listBoxJVPostingAccounting.setHeight(this.borderLayoutHeight - 280 + "px");
			doShowDialog(getJVPosting());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_JVPostingDialog.onClose();
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
		doWriteBeanToComponents(this.jVPosting.getBefImage());
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
		MessageUtil.showHelpWindow(event, window_JVPostingDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	protected void doPostClose() {
		getJVPostingListCtrl().refreshList();
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(getNotes("JVPosting", String.valueOf(getJVPosting().getBatchReference()),
					getJVPosting().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * Call the JVPosting dialog with a new empty entry. <br>
	 */
	public void onClick$btnNewJVPostingEntry(Event event) {
		logger.debug(event.toString());
		// create a new JVPosting object, We GET it from the backend.
		JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		aJVPostingEntry.setNewRecord(true);
		aJVPostingEntry.setWorkflowId(0);
		aJVPostingEntry.setFileName(getJVPosting().getFilename());
		aJVPostingEntry.setTxnReference(this.listBoxJVPostingEntry.getItems().size() + 1);
		showDetailView(aJVPostingEntry, false, true);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param JVPosting (aJVPosting)
	 */
	private void showDetailView(JVPostingEntry aJVPostingEntry, boolean isFilter, boolean setNewRecord) {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */

		final Map<String, Object> map = new HashMap<String, Object>();
		// map.put("jVPosting", aJVPosting);
		if (this.moduleType != null && this.moduleType.getValue().equalsIgnoreCase(PennantConstants.MODULETYPE_ENQ)
				|| enqModule) {
			map.put("enqModule", true);
		} else if (this.moduleType != null
				&& this.moduleType.getValue().equalsIgnoreCase(PennantConstants.MODULETYPE_REPOSTING)) {
			map.put("rePostingModule", true);
		} else {
			map.put("enqModule", false);
		}
		if (setNewRecord) {
			map.put("newRecord", true);
		}

		map.put("roleCode", getRole());
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listbox Listmodel. This is fine for synchronizing the data in the JVPostingListbox from the dialog when we do
		 * a delete, edit or insert a JVPosting.
		 */

		map.put("jVPostingDialogCtrl", this);
		map.put("jVPostingEntry", aJVPostingEntry);

		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		doSetValidation();
		// fill the JVPosting object with the components data
		doWriteComponentsToBean(aJVPosting, false);
		map.put("jVPosting", aJVPosting);

		// call the zul-file with the parameters packed in a map
		try {
			String fileName = null;
			if (isFilter) {
				fileName = "/WEB-INF/pages/Others/JVPosting/JVPostingEntrySearch.zul";
			} else {
				fileName = "/WEB-INF/pages/Others/JVPosting/JVPostingEntryDialog.zul";
			}
			Executions.createComponents(fileName, window_JVPostingDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.others.jvposting.model.JVPostingListModelItemRenderer .java <br>
	 * 
	 * @param event
	 */
	public void onJVPostingEntryItemDoubleClicked(Event event) {
		logger.debug(event.toString());
		// get the selected JVPosting object
		final Listitem item = this.listBoxJVPostingEntry.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final JVPostingEntry aJVPostingEntry = (JVPostingEntry) item.getAttribute("data");

			if (aJVPostingEntry.getDerivedTxnRef() == 0) {
				JVPostingEntry jVPostingEntry = getEntryList(aJVPostingEntry.getTxnReference());
				if (jVPostingEntry != null) {
					aJVPostingEntry.setDebitAccount(jVPostingEntry.getAccount());
					aJVPostingEntry.setDebitTxnCode(jVPostingEntry.getTxnCode());
					if (aJVPostingEntry.getTxnEntry().equals("D")) {
						aJVPostingEntry.setDebitTxnDesc(jVPostingEntry.getDebitTxnDesc());
					} else {
						aJVPostingEntry.setDebitTxnDesc(jVPostingEntry.getTxnDesc());
					}
					showDetailView(aJVPostingEntry, false, false);
				}
			}

			// if(!aJVPostingEntry.getRecordType().equals("ADD")){}else{
			// if (aJVPostingEntry.getTxnEntry().equals(AccountConstants.TRANTYPE_CREDIT)) {
			// showDetailView(aJVPostingEntry, false, false);
			// }
			// }
		}
		logger.debug("Leaving");
	}

	public JVPostingEntry getEntryList(long derRef) {
		List<JVPostingEntry> list = getJVPostingEntryList();
		for (JVPostingEntry jvPostingEntry : list) {
			if (derRef == jvPostingEntry.getDerivedTxnRef()) {
				return jvPostingEntry;
			}
		}
		return null;
	}

	public void onFulfill$baseCCy(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			Object currency = this.baseCCy.getObject();
			if (currency != null && !(currency instanceof String)) {
				Currency aCurrency = (Currency) this.baseCCy.getObject();
				getJVPosting().setCurrency(aCurrency.getCcyCode());
				this.totDebitsByBatchCcy
						.setFormat(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, aCurrency.getCcyEditField()));
				this.totCreditsByBatchCcy
						.setFormat(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, aCurrency.getCcyEditField()));
				renderJVPostingEntries(getJVPostingEntryList());
				this.tab_JVSummary.setSelected(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$expReference(Event event) throws InterruptedException {
		logger.debug("Entering");
		Object dataObject = this.expReference.getObject();

		if (dataObject instanceof String) {
			expAmount.setVisible(false);
		} else {
			LegalExpenses details = (LegalExpenses) dataObject;
			if (details != null) {
				expAmount.setValue(CurrencyUtil.parse(details.getAmountdue(),
						CurrencyUtil.getFormat(getJVPosting().getCurrency())));
				expAmount.setVisible(true);
				this.expReference.appendChild(expAmount);

			} else {
				expAmount.setVisible(false);
			}
		}
		logger.debug("Leaving");

	}
	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aJVPosting
	 */
	public void doShowDialog(JVPosting aJVPosting) {
		logger.debug("Entering");
		// set Read only mode accordingly if the object is new or not.
		if (aJVPosting.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aJVPosting.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
				btnCancel.setVisible(false);
			}
			if (enqModule) {
				doReadOnly(true);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aJVPosting);

			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aJVPosting.isNewRecord()));

			doSetWindowTitle();
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_JVPostingDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getJVPosting().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.jVPosting.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	private void doSetWindowTitle() {
		logger.debug("Entering");
		if (this.enqModule) {
			this.window_JVPostingDialog_Title.setValue(Labels.getLabel("window_JVPostingEnqDialog.title"));
		} else if (this.rePostingModule) {
			this.window_JVPostingDialog_Title.setValue(Labels.getLabel("window_JVPostingRePostingDialog.title"));
		} else {
			this.window_JVPostingDialog_Title.setValue(Labels.getLabel("window_JVPostingDialog.title"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPosting
	 */
	public void doWriteBatchDetailsToBean(JVPosting aJVPosting) throws InterruptedException {
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
			aJVPosting.setBatchReference(Long.valueOf(this.batchReference.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fin Reference
		try {
			if (this.expReference.getValue() != null) {
				aJVPosting.setExpReference(this.expReference.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Batch Currency
		try {
			aJVPosting.setCurrency(this.baseCCy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// exchangeRateType
		try {
			aJVPosting.setExchangeRateType(this.exchangeRateType.getValue());
			aJVPosting.setRateTypeDescription(this.exchangeRateType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tot Debits By Batch Ccy
		try {
			aJVPosting.setTotDebitsByBatchCcy(PennantApplicationUtil.unFormateAmount(
					this.totDebitsByBatchCcy.getValue(), CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tot Credits By Batch Ccy
		try {
			aJVPosting.setTotCreditsByBatchCcy(PennantApplicationUtil.unFormateAmount(
					this.totCreditsByBatchCcy.getValue(), CurrencyUtil.getFormat(getJVPosting().getCurrency())));
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

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.batch, null));

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;

		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.jVPosting.getRecordType()) || enqModule) {
			tempReadOnly = true;
		}

		// Batch Header Details

		readOnlyComponent(isReadOnly("JVPostingDialog_expReference"), this.expReference);
		readOnlyComponent(isReadOnly("JVPostingDialog_Batch"), this.batch);
		readOnlyComponent(true, this.batchReference);
		readOnlyComponent(isReadOnly("JVPostingDialog_BatchCcy"), this.baseCCy);
		// readOnlyComponent(isReadOnly("JVPostingDialog_Branch"), this.postingBranch);
		readOnlyComponent(isReadOnly("JVPostingDialog_PostingAgainst"), this.postingAgainst);
		readOnlyComponent(isReadOnly("JVPostingDialog_Reference"), this.reference);
		readOnlyComponent(isReadOnly("JVPostingDialog_Reference"), this.postingDivision);

		setExtAccess("JVPostingDialog_ExchRateType", tempReadOnly, this.exchangeRateType, row2);

		readOnlyComponent(true, this.totCreditsByBatchCcy);

		readOnlyComponent(true, this.totDebitsByBatchCcy);
		readOnlyComponent(true, this.debitCount);
		readOnlyComponent(true, this.creditsCount);
		readOnlyComponent(isReadOnly("JVPostingDialog_BatchPurpose"), this.batchPurpose);
		// readOnlyComponent(isReadOnly("JVPostingDialog_btnUpload"), this.btn_Upload);

		if (enqModule) {
			readOnlyComponent(true, this.expReference);
			readOnlyComponent(true, this.batch);
			readOnlyComponent(true, this.batchReference);
			readOnlyComponent(true, this.baseCCy);
			readOnlyComponent(true, this.postingBranch);
			readOnlyComponent(true, this.exchangeRateType);
			readOnlyComponent(true, this.totCreditsByBatchCcy);
			readOnlyComponent(true, this.totDebitsByBatchCcy);
			readOnlyComponent(true, this.debitCount);
			readOnlyComponent(true, this.creditsCount);
			readOnlyComponent(true, this.batchPurpose);
			readOnlyComponent(true, this.postingAgainst);
			readOnlyComponent(true, this.reference);
			readOnlyComponent(true, this.postingDivision);
			this.btnNewJVPostingEntry.setVisible(false);
			this.btnValidate.setVisible(false);
			this.btnNotes.setVisible(false);
		}
		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("JVPostingDialog", getRole());
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnSave"));

			this.btnNewJVPostingEntry
					.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnNewJVPostingEntry"));
			this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_JVPostingDialog_btnValidate"));
			this.btnValidate.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		int currFormatter = CurrencyUtil.getFormat(getJVPosting().getCurrency());
		this.batchReference.setMaxlength(50);

		this.expReference.setMandatoryStyle(false);
		this.expReference.setModuleName("LegalExpenses");
		this.expReference.setValueColumn("ExpReference");
		this.expReference.setValidateColumns(new String[] { "ExpReference" });
		this.expReference.getButton().setVisible(false);

		this.baseCCy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.baseCCy.setMandatoryStyle(true);
		this.baseCCy.setModuleName("Currency");
		this.baseCCy.setValueColumn("CcyCode");
		this.baseCCy.setDescColumn("CcyDesc");
		this.baseCCy.setValidateColumns(new String[] { "CcyCode" });

		this.postingBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.postingBranch.setMandatoryStyle(true);
		this.postingBranch.setTextBoxWidth(40);
		this.postingBranch.setModuleName("UserDivBranch");
		this.postingBranch.setValueColumn("UserBranch");
		this.postingBranch.setDescColumn("UserBranchDesc");
		Filter[] postingBranch = new Filter[1];
		postingBranch[0] = Filter.in("usrId", getUserWorkspace().getLoggedInUser().getUserId());
		this.postingBranch.setFilters(postingBranch);
		this.postingBranch.setValidateColumns(new String[] { "UserBranch" });

		this.exchangeRateType.setMaxlength(8);
		this.exchangeRateType.setMandatoryStyle(true);
		this.exchangeRateType.setTextBoxWidth(40);
		this.exchangeRateType.setModuleName("Currency");
		this.exchangeRateType.setValueColumn("CcyCode");
		this.exchangeRateType.setDescColumn("CcyDesc");
		this.exchangeRateType.setValidateColumns(new String[] { "CcyCode" });

		this.debitCount.setMaxlength(10);
		this.creditsCount.setMaxlength(10);
		this.totDebitsByBatchCcy.setMaxlength(18);
		this.totDebitsByBatchCcy.setFormat(PennantApplicationUtil.getAmountFormate(currFormatter));
		this.totCreditsByBatchCcy.setMaxlength(18);
		this.totCreditsByBatchCcy.setFormat(PennantApplicationUtil.getAmountFormate(currFormatter));

		/*
		 * this.expAmount.setMaxlength(18); this.expAmount.setFormat(PennantApplicationUtil.getAmountFormate(2));
		 * this.expAmount.setScale(2);
		 */
		this.batchPurpose.setMaxlength(35);
		this.reference.setMandatoryStyle(true);

		this.postingDivision.setMandatoryStyle(true);
		this.postingDivision.setModuleName("DivisionDetail");
		this.postingDivision.setValueColumn("DivisionCode");
		this.postingDivision.setDescColumn("DivisionCodeDesc");
		this.postingDivision.setValidateColumns(new String[] { "DivisionCode" });

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aJVPosting JVPosting
	 */
	public void doWriteBeanToComponents(JVPosting aJVPosting) {
		logger.debug("Entering");
		LegalExpenses expenses;
		this.batch.setValue(aJVPosting.getBatch());
		if (aJVPosting.isNewRecord()) {
			this.batchReference.setValue("");
			this.baseCCy.setValue(aJVPosting.getCurrency());
			getJVPosting().setPostingDate(SysParamUtil.getAppDate());
			fillComboBox(this.postingAgainst, "", AccountingUtil.getJVPurposeList(), "");
		} else {
			this.batchReference.setValue(String.valueOf(aJVPosting.getBatchReference()));
			this.baseCCy.setValue(aJVPosting.getCurrency());
			this.postingBranch.setValue(aJVPosting.getBranch());
			this.postingBranch.setDescription(aJVPosting.getBranchDesc());
			this.postingBranch.setReadonly(true);
			fillComboBox(this.postingAgainst, aJVPosting.getPostAgainst(), AccountingUtil.getJVPurposeList(), "");
		}

		// Added to map with legal expenses
		if (!StringUtils.isEmpty(aJVPosting.getExpReference())) {
			this.expReference.setValue(aJVPosting.getExpReference());
			expenses = getLegalExpensesService().getLegalExpensesById(aJVPosting.getExpReference());
			if (expenses != null) {
				expAmount.setVisible(true);
				expAmount.setValue(
						CurrencyUtil.parse(expenses.getAmount(), CurrencyUtil.getFormat(getJVPosting().getCurrency())));
				this.expReference.appendChild(expAmount);
			}

		}

		this.exchangeRateType.setValue(aJVPosting.getExchangeRateType());
		this.exchangeRateType.setDescription(aJVPosting.getRateTypeDescription());
		this.debitCount.setValue(aJVPosting.getDebitCount());
		this.creditsCount.setValue(aJVPosting.getCreditsCount());
		this.totDebitsByBatchCcy.setValue(CurrencyUtil.parse(getJVPosting().getTotDebitsByBatchCcy(),
				CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		this.totCreditsByBatchCcy.setValue(CurrencyUtil.parse(getJVPosting().getTotCreditsByBatchCcy(),
				CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		this.batchPurpose.setValue(aJVPosting.getBatchPurpose());
		setFilters(StringUtils.equals(null, aJVPosting.getPostAgainst()) ? aJVPosting.getPostAgainst()
				: aJVPosting.getPostAgainst().trim());
		this.reference.setValue(aJVPosting.getReference());
		this.postingDivision.setValue(aJVPosting.getPostingDivision(), aJVPosting.getDivisionCodeDesc());

		this.recordStatus.setValue(aJVPosting.getRecordStatus());
		doFillJVPostingEntryDetails(aJVPosting.getJVPostingEntrysList());
		logger.debug("Leaving");
	}

	public void doFillJVPostingEntryDetails(List<JVPostingEntry> aJVPostingEntryList) {
		logger.debug("Entering");
		setJVPostingEntryList(aJVPostingEntryList);
		renderJVPostingEntries(getJVPostingEntryList());
		fillAccountingTab();

		if (aJVPostingEntryList != null && aJVPostingEntryList.size() > 0) {
			this.postingBranch.setReadonly(true);
		} else {
			this.postingBranch.setReadonly(false);
		}
		logger.debug("Leaving");
	}

	public void onFulfill$reference(Event event) {
		logger.debug("Entering");

		String postAgainst = this.postingAgainst.getSelectedItem().getValue().toString();

		if (StringUtils.isBlank(this.reference.getValue()) || PennantConstants.List_Select.equals(postAgainst)) {
			this.reference.setValue("", "");
		} else {
			if (PostAgainst.isLoan(postAgainst) && this.reference.getObject() != null) {
				FinanceMain financeMain = (FinanceMain) this.reference.getObject();
				this.reference.setValue(financeMain.getFinReference(), financeMain.getFinType());
				this.postingDivision.setValue(financeMain.getLovDescFinDivision());
				this.postingDivision.setReadonly(true);
				this.postingBranch.setValue(financeMain.getFinBranch());
				this.postingBranch.setReadonly(true);
			}
			if (PostAgainst.isCustomer(postAgainst)) {
				Customer customer = (Customer) this.reference.getObject();
				this.reference.setValue(customer.getCustCIF(), customer.getCustShrtName());
				this.postingBranch.setValue(customer.getCustDftBranch());
				this.postingBranch.setReadonly(true);
			}
		}
		logger.debug("Leaving");

	}

	public void onFulfill$postingBranch(Event event) {
		if (!StringUtils.isEmpty(this.postingBranch.getValue())) {
			this.postingBranch.setReadonly(true);
		}

		String postAgaint = this.postingAgainst.getSelectedItem().getValue().toString();

		if (PostAgainst.isLoan(postAgaint) && this.postingBranch.getValue() != null) {
			Filter[] reference = new Filter[1];
			reference[0] = Filter.in("finbranch", this.postingBranch.getValue());
			this.reference.setFilters(reference);
		}

		if (PostAgainst.isCustomer(postAgaint) && this.postingBranch.getValue() != null) {
			Filter[] reference = new Filter[1];
			reference[0] = Filter.in("custdftbranch", this.postingBranch.getValue());
			this.reference.setFilters(reference);
		}

		if (PostAgainst.isLimit(postAgaint) && this.postingBranch.getValue() != null) {
			Filter[] reference = new Filter[1];
			reference[0] = Filter.in("ResponsibleBranch", this.postingBranch.getValue());
			this.reference.setFilters(reference);
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPosting
	 */
	public void doWriteComponentsToBean(JVPosting aJVPosting, boolean addList) {
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
			if (!StringUtils.equals("", this.batchReference.getValue())) {
				aJVPosting.setBatchReference(Long.valueOf(this.batchReference.getValue()));
			}
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
			aJVPosting.setTotDebitsByBatchCcy(CurrencyUtil.unFormat(this.totDebitsByBatchCcy.getValue(),
					CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tot Credits By Batch Ccy
		try {
			aJVPosting.setTotCreditsByBatchCcy(CurrencyUtil.unFormat(this.totCreditsByBatchCcy.getValue(),
					CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Batch Purpose
		try {
			aJVPosting.setBatchPurpose(this.batchPurpose.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fin Reference
		try {
			if (this.expReference.getValue() != null) {
				aJVPosting.setExpReference(this.expReference.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// reference
		try {
			if (this.reference.getValue() != null) {
				aJVPosting.setReference(this.reference.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aJVPosting.setPostingDivision(this.postingDivision.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// postingagainst
		try {
			if (!this.postingAgainst.isReadonly()
					&& getComboboxValue(this.postingAgainst).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.postingAgainst, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_JVPostingDialog_PostingAgainst.value") }));
			}
			aJVPosting.setPostAgainst(getComboboxValue(this.postingAgainst));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		 * // jVPostingEntryList setting try { // Preparing Updated List with Deleted Flag
		 * doUpdateJVPostingEntrysList(aJVPosting); } catch (WrongValueException we) { wve.add(we); }
		 */
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (addList) {
			aJVPosting.setJVPostingEntrysList(this.jVPostingEntryList);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Batch
		if (!this.batch.isReadonly()) {
			this.batch.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_Batch.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		// Exp Reference
		if (!this.expReference.isReadonly()) {
			this.expReference
					.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_ExpReference.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}
		// Reference
		if (!this.reference.isReadonly()) {
			this.reference.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingDialog_Reference.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.postingDivision.isButtonDisabled()) {
			this.postingDivision.setConstraint(new PTStringValidator(
					Labels.getLabel("label_JVPostingDialog_PostingDivision.value"), null, true, true));
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
		this.debitCount.setConstraint("");
		this.creditsCount.setConstraint("");
		this.totDebitsByBatchCcy.setConstraint("");
		this.totCreditsByBatchCcy.setConstraint("");
		this.batchPurpose.setConstraint("");
		this.reference.setConstraint("");
		this.postingDivision.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		// Cmt Branch
		if (this.baseCCy.isButtonVisible()) {
			this.baseCCy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_JVPostingDialog_BatchCcy.value"), null, true, true));
		}
		// Cmt Ccy
		if (this.postingBranch.isButtonVisible() && !this.postingBranch.isReadonly()
				&& this.postingBranch.isMandatory()) {
			this.postingBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_JVPostingDialog_PostingBranch.value"), null, true, true));
		} else {
			this.postingBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_JVPostingDialog_PostingBranch.value"), null, false, true));
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

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.batch.setErrorMessage("");
		this.batchReference.setErrorMessage("");
		this.debitCount.setErrorMessage("");
		this.creditsCount.setErrorMessage("");
		this.totDebitsByBatchCcy.setErrorMessage("");
		this.totCreditsByBatchCcy.setErrorMessage("");
		this.batchPurpose.setErrorMessage("");
		this.reference.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	protected void refreshList() {
		final JdbcSearchObject<JVPosting> jvPostings = getJVPostingListCtrl().getSearchObject();
		getJVPostingListCtrl().pagingJVPostingList.setActivePage(0);
		getJVPostingListCtrl().getPagedListWrapper().setSearchObject(jvPostings);
		if (getJVPostingListCtrl().listBoxJVPosting != null) {
			getJVPostingListCtrl().listBoxJVPosting.getListModel();
		}
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);

		doDelete(String.valueOf(aJVPosting.getBatchReference()), aJVPosting);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.batchReference.setValue("");
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

		/*
		 * if(("Submit".equals(userAction.getSelectedItem().getLabel())||
		 * "Approve".equals(userAction.getSelectedItem().getLabel())) && !proceed){
		 * MessageUtil.showErrorMessage(Labels.getLabel("VALIDATE_ACCOUNTS")); return; }
		 */
		if (getJVPosting().isNewRecord()
				&& getJVPostingService().getJVPostingByFileName(this.batch.getValue()) != null) {
			MessageUtil.showError(Labels.getLabel("BATCH_ALREADY_EXISTS",
					new String[] { this.batch.getValue(), DateUtil.getSysDate().toString() }));
			return;
		}

		if (this.listBoxJVPostingEntry.getItemCount() == 0) {
			throw new WrongValueException(this.btnNewJVPostingEntry, "Click to add New Postings");
		}

		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		boolean isNew = false;
		if (isWorkFlowEnabled()) {
			aJVPosting.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aJVPosting.getNextTaskId(), aJVPosting);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aJVPosting.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the JVPosting object with the components data
			doWriteComponentsToBean(aJVPosting, true);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aJVPosting.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aJVPosting.getRecordType())) {
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
			if ("Cancelled".equals(aJVPosting.getRecordStatus()) || "Saved".equals(aJVPosting.getRecordStatus())) {
				if (doProcess(aJVPosting, tranType)) {
					// doWriteBeanToComponents(aJVPosting);
					// refreshList();
					getJVPostingListCtrl().refreshList();
					closeDialog();
				}
			} else if (aJVPosting.getTotCreditsByBatchCcy().compareTo(aJVPosting.getTotDebitsByBatchCcy()) == 0) {
				if (doProcess(aJVPosting, tranType)) {
					// User Notification for Role Identification
					if (StringUtils.isBlank(aJVPosting.getNextTaskId())) {
						aJVPosting.setNextRoleCode("");
					}
					// List Detail Refreshment
					refreshList();

					// Confirmation message
					String msg = PennantApplicationUtil.getSavingStatus(aJVPosting.getRoleCode(),
							aJVPosting.getNextRoleCode(), aJVPosting.getReference(), " Miscellaneous Postings ",
							aJVPosting.getRecordStatus());
					if (StringUtils.equals(aJVPosting.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
						msg = "Miscellaneous Postings with Reference " + aJVPosting.getReference()
								+ " Approved Succesfully.";
					}
					Clients.showNotification(msg, "info", null, null, -1);

					closeDialog();
				}
			} else {
				MessageUtil.showError(Labels.getLabel("FIELD_NOT_MATCHED",
						new String[] { Labels.getLabel("label_JVPostingDialog_TotDebitsByBatchCcy.value"),
								Labels.getLabel("label_JVPostingDialog_TotCreditsByBatchCcy.value") }));
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_JVPostingDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */

	protected boolean doProcess(JVPosting aJVPosting, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aJVPosting.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aJVPosting.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aJVPosting.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aJVPosting.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aJVPosting, finishedTasks);

			if (isNotesMandatory(taskId, aJVPosting)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aJVPosting, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else {
					JVPosting tJVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, aJVPosting);
					auditHeader.getAuditDetail().setModelData(tJVPosting);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				JVPosting tJVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tJVPosting, finishedTasks);

			}

			JVPosting tJVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tJVPosting);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aJVPosting);
					auditHeader.getAuditDetail().setModelData(tJVPosting);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			processCompleted = doSaveProcess(getAuditHeader(aJVPosting, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	protected String getServiceTasks(String taskId, JVPosting jvPosting, String finishedTasks) {
		logger.debug("Entering");
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(jvPosting.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, jvPosting);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, JVPosting jvPosting) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(jvPosting.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			} else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, jvPosting);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		jvPosting.setTaskId(taskId);
		jvPosting.setNextTaskId(nextTaskId);
		jvPosting.setRoleCode(getRole());
		jvPosting.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		JVPosting aJVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
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
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_JVPostingDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_JVPostingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes("JVPosting", String.valueOf(aJVPosting.getBatchReference()),
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
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(JVPosting aJVPosting, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJVPosting.getBefImage(), aJVPosting);
		return new AuditHeader(Long.toString(aJVPosting.getBatchReference()), null, null, null, auditDetail,
				aJVPosting.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setDefaultDirectory() {
		String path = "C:/Pennant/UPP";
		File file = new File(path);

		if (!file.exists() || !file.canWrite()) {
			throw new AppException(Labels.getLabel("dir_not_found"));
		}

		setDirectory(file);
	}

	public void doUpdateBatchDetails(JVPosting jVPosting) {
		this.debitCount.setValue(jVPosting.getDebitCount());
		this.creditsCount.setValue(jVPosting.getCreditsCount());
		this.totCreditsByBatchCcy.setValue(CurrencyUtil.parse(jVPosting.getTotCreditsByBatchCcy(),
				CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		this.totDebitsByBatchCcy.setValue(CurrencyUtil.parse(jVPosting.getTotDebitsByBatchCcy(),
				CurrencyUtil.getFormat(getJVPosting().getCurrency())));
	}

	/*
	 * public void onSelect$tab_Accounting(Event event) { fillAccountingTab(); }
	 */

	private void fillAccountingTab() {
		List<JVPostingEntry> acEntryList = new ArrayList<JVPostingEntry>();
		for (Listitem li : this.listBoxJVPostingEntry.getItems()) {
			acEntryList.add((JVPostingEntry) li.getAttribute("data"));
		}
		acEntryList = getPostingsPreparationUtil().prepareAccountingEntryList(acEntryList, getJVPosting().getCurrency(),
				CurrencyUtil.getCcyNumber(jVPosting.getCurrency()),
				CurrencyUtil.getFormat(getJVPosting().getCurrency()));
		renderAccountingEntries(acEntryList, CurrencyUtil.getFormat(getJVPosting().getCurrency()));
	}

	private void renderAccountingEntries(List<JVPostingEntry> acEntryList, int batchCcyEditField) {
		this.listBoxJVPostingAccounting.getItems().clear();
		Listitem item = null;
		Listcell lc;
		for (JVPostingEntry accountingEntry : acEntryList) {
			item = new Listitem();

			String hostAccount = AccountingConfigCache.getAccountMapping(accountingEntry.getAccount());
			lc = new Listcell(hostAccount);
			lc.setParent(item);

			AccountMapping am = AccountingConfigCache.getAccountMappingForAccounting(accountingEntry.getAccount());

			lc = new Listcell(PennantApplicationUtil.formatAccountNumber(accountingEntry.getAccount()));
			lc.setParent(item);
			lc = new Listcell(am.getAccountTypeDesc());
			lc.setParent(item);
			lc = new Listcell(am.getAccountType());
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getTxnEntry());
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getTxnCCy());
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(accountingEntry.getTxnAmount(),
					CurrencyUtil.getFormat(accountingEntry.getAccCCy())));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell(accountingEntry.getAccCCy());
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(accountingEntry.getTxnAmount(),
					CurrencyUtil.getFormat(accountingEntry.getAccCCy())));
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
		int count = 0;
		this.listBoxJVPostingEntry.getItems().clear();
		JVPostingEntry newJvPostingEntry = new JVPostingEntry();
		newJvPostingEntry.setDaySeqDate(DateUtil.getDatePart(DateUtil.getSysDate()));
		count = jVPostingService.getMaxSeqNumForCurrentDay(newJvPostingEntry);
		Listitem item = null;
		Listcell lc;
		for (JVPostingEntry jvPostingEntry : entryList) {
			if (jvPostingEntry.isExternalAccount()) {
				item = new Listitem();
				if (jvPostingEntry.getRecordStatus() != null) {
					lc = new Listcell(String.valueOf(jvPostingEntry.getTxnReference()));
					lc.setParent(item);
				} else {
					lc = new Listcell(String.valueOf(++count));
					lc.setParent(item);
				}
				lc = new Listcell(String.valueOf(AccountingConfigCache.getAccountMapping(jvPostingEntry.getAccount())));
				lc.setParent(item);

				if (jvPostingEntry.getTxnEntry().equalsIgnoreCase(AccountConstants.TRANTYPE_CREDIT)) {
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(jvPostingEntry.getAccount()));

				} else {
					if (jvPostingEntry.isNewRecord()) {
						lc = new Listcell(PennantApplicationUtil.formatAccountNumber(jvPostingEntry.getDebitAccount()));
					} else {
						lc = new Listcell(PennantApplicationUtil.formatAccountNumber(jvPostingEntry.getAccount()));

					}
				}
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(jvPostingEntry.getPostingDate()));
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(jvPostingEntry.getValueDate()));
				lc.setParent(item);
				if (jvPostingEntry.getTxnEntry().equalsIgnoreCase(AccountConstants.TRANTYPE_CREDIT)) {
					lc = new Listcell(Labels.getLabel("common.Credit"));
				} else {
					lc = new Listcell(Labels.getLabel("common.Debit"));
				}
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getTxnCCy());
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(jvPostingEntry.getTxnAmount(),
						CurrencyUtil.getFormat(jvPostingEntry.getAccCCy())));
				lc.setParent(item);

				lc = new Listcell(jvPostingEntry.getPostingStatus());
				lc.setTooltiptext(jvPostingEntry.getPostingStatus());
				lc.setParent(item);

				lc = new Listcell(jvPostingEntry.getNarrLine1());
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(jvPostingEntry.getRecordType());
				lc.setParent(item);

				item.setAttribute("data", jvPostingEntry);
				if (!jvPostingEntry.isDeletedFlag()) {
					int formatter = CurrencyUtil.getFormat(getJVPosting().getCurrency());

					if (jvPostingEntry.getTxnEntry().equalsIgnoreCase(AccountConstants.TRANTYPE_CREDIT)) {
						creditAmount = creditAmount
								.add(CurrencyUtil.unFormat(CurrencyUtil.parse(
										CalculationUtil.getConvertedAmount(jvPostingEntry.getTxnCCy(),
												getJVPosting().getCurrency(), jvPostingEntry.getTxnAmount()),
										formatter), formatter));
						creditCount = creditCount + 1;
					} else {
						debitAmount = debitAmount
								.add(CurrencyUtil.unFormat(CurrencyUtil.parse(
										CalculationUtil.getConvertedAmount(jvPostingEntry.getTxnCCy(),
												getJVPosting().getCurrency(), jvPostingEntry.getTxnAmount()),
										formatter), formatter));
						debitCount = debitCount + 1;
					}
					ComponentsCtrl.applyForward(item, "onDoubleClick=onJVPostingEntryItemDoubleClicked");
				}
				Clients.clearWrongValue(this.btnNewJVPostingEntry);
				this.listBoxJVPostingEntry.appendChild(item);
			}
		}

		creditAmount = creditAmount.setScale(2, RoundingMode.HALF_DOWN);
		debitAmount = debitAmount.setScale(2, RoundingMode.HALF_DOWN);

		this.totCreditsByBatchCcy
				.setValue(CurrencyUtil.parse(creditAmount, CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		this.creditsCount.setValue(creditCount);
		this.totDebitsByBatchCcy
				.setValue(CurrencyUtil.parse(debitAmount, CurrencyUtil.getFormat(getJVPosting().getCurrency())));
		this.debitCount.setValue(debitCount);
	}

	/**
	 * when the "validate" button is clicked. <br>
	 * Stores the default values, sets the validation and validates the given finance details.
	 * 
	 * @param event
	 */
	public void onClick$btnValidate(Event event) {
		logger.debug("Entering" + event.toString());
		final JVPosting aJVPosting = new JVPosting();
		BeanUtils.copyProperties(getJVPosting(), aJVPosting);
		aJVPosting.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (getJVPostingService().doAccountValidation(aJVPosting, getJVPostingEntryList())) {
			this.setProceed(true);
		}
		this.tab_Accounting.setSelected(true);
		renderJVPostingEntries(getJVPostingEntryList());

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$postingAgainst(Event event) {
		this.reference.setConstraint("");
		this.reference.setErrorMessage("");
		this.reference.setValue("", "");
		this.postingDivision.setValue("", "");
		this.postingDivision.setReadonly(false);
		this.postingBranch.setValue("");
		this.postingBranch.setReadonly(false);
		this.reference.setFilters(null);
		this.reference.setObject(null);
		this.postingBranch.setObject(null);
		setFilters(this.postingAgainst.getSelectedItem().getValue().toString());
	}

	private void setFilters(String postValue) {

		this.postingBranch.setMandatoryStyle(true);

		if (StringUtils.equals(postValue, PennantConstants.List_Select)) {
			// addFilters("", "", "");
		}

		if (PostAgainst.isLoan(postValue)) {
			addFilters("FinanceMain", "FinReference", "FinType");
		}

		if (PostAgainst.isCustomer(postValue)) {
			addFilters("Customer", "CustCIF", "CustShrtName");
		}

		if (PostAgainst.isCollateral(postValue)) {
			addFilters("CollateralSetup", "CollateralRef", "CollateralType");
		}

		if (PostAgainst.isLimit(postValue)) {
			addFilters("LimitHeader", "HeaderId", "ResponsibleBranch");
		}

		if (PostAgainst.isEntity(postValue)) {
			addFilters("Entity", "EntityCode", "EntityDesc");
			this.postingBranch.setMandatoryStyle(false);
		}
	}

	private void addFilters(String modulename, String valuecolumn, String descColumn) {
		this.reference.setModuleName(modulename);
		this.reference.setValueColumn(valuecolumn);
		this.reference.setDescColumn(descColumn);
		this.reference.setValidateColumns(new String[] { valuecolumn });
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

	public void setDeletedJVPostingEntryList(List<JVPostingEntry> deletedJVPostingEntryList) {
		this.deletedJVPostingEntryList = deletedJVPostingEntryList;
	}

	public boolean isProceed() {
		return proceed;
	}

	public void setProceed(boolean proceed) {
		this.proceed = proceed;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public LegalExpensesService getLegalExpensesService() {
		return legalExpensesService;
	}

	public void setLegalExpensesService(LegalExpensesService legalExpensesService) {
		this.legalExpensesService = legalExpensesService;
	}

}