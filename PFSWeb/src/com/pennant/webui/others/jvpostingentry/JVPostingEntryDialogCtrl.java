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
 * FileName    		:  JVPostingEntryDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.others.jvpostingentry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.others.jvposting.JVPostingDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Others/JVPostingEntry/jVPostingEntryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class JVPostingEntryDialogCtrl extends GFCBaseCtrl implements Serializable {/*

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(JVPostingEntryDialogCtrl.class);

	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window window_JVPostingEntryDialog;
	protected Row row0;
	protected Label label_BatchReference;
	protected Hlayout hlayout_BatchReference;
	protected Space space_BatchReference;

	protected Textbox batchReference;
	protected Label label_Account;
	protected Hlayout hlayout_Account;
	protected Space space_Account;

	protected Textbox account;
	protected Button btnSearchaccountName;
	protected Row row1;
	protected Label label_AccountName;
	protected Hlayout hlayout_AccountName;
	protected Space space_AccountName;

	protected Textbox accountName;
	protected Label label_TxnCCy;

	protected ExtendedCombobox txnCCy;
	protected Textbox accCcy;
	protected Textbox baseCCy;

	protected Row row2;
	protected Label label_TxnCode;
	protected Hlayout hlayout_TxnCode;
	protected Space space_TxnCode;

	protected Combobox txnCode;
	protected Label label_PostingDate;
	protected Hlayout hlayout_PostingDate;
	protected Space space_PostingDate;

	protected Datebox postingDate;
	protected Row row3;
	protected Label label_ValueDate;
	protected Hlayout hlayout_ValueDate;
	protected Space space_ValueDate;

	protected Datebox valueDate;
	protected Label label_TxnAmount;
	protected Hlayout hlayout_TxnAmount;
	protected Space space_TxnAmount;

	protected Decimalbox txnAmount;
	protected Row row4;
	protected Label label_TxnReference;
	protected Hlayout hlayout_TxnReference;
	protected Space space_TxnReference;

	protected Textbox txnReference;
	protected Label label_NarrLine1;
	protected Hlayout hlayout_NarrLine1;
	protected Space space_NarrLine1;

	protected Textbox narrLine1;
	protected Row row5;
	protected Label label_NarrLine2;
	protected Hlayout hlayout_NarrLine2;
	protected Space space_NarrLine2;

	protected Textbox narrLine2;
	protected Label label_NarrLine3;
	protected Hlayout hlayout_NarrLine3;
	protected Space space_NarrLine3;

	protected Textbox narrLine3;
	protected Row row6;
	protected Label label_NarrLine4;
	protected Hlayout hlayout_NarrLine4;
	protected Space space_NarrLine4;

	protected Textbox narrLine4;
	protected Label label_ExchRate_Batch;
	protected Hlayout hlayout_ExchRate_Batch;
	protected Space space_ExchRate_Batch;

	protected Decimalbox calExchRate;
	protected Decimalbox exchRate_Batch;
	protected Row row7;
	protected Label label_ExchRate_Ac;
	protected Hlayout hlayout_ExchRate_Ac;
	protected Space space_ExchRate_Ac;

	protected Decimalbox exchRate_Ac;
	protected Label label_TxnAmount_Batch;
	protected Hlayout hlayout_TxnAmount_Batch;
	protected Space space_TxnAmount_Batch;

	protected Decimalbox txnAmount_Batch;
	protected Row row8;
	protected Label label_TxnAmount_Ac;
	protected Hlayout hlayout_TxnAmount_Ac;
	protected Space space_TxnAmount_Ac;

	protected Decimalbox txnAmount_Ac;

	protected Label recordStatus;
	protected Label recordType;
	protected Radiogroup userAction;
	protected Groupbox gb_statusDetails;
	protected Groupbox groupboxWf;
	protected South south;
	private boolean enqModule = false;

	// not auto wired vars
	private JVPostingEntry jVPostingEntry; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_BatchReference;
	private transient String oldVar_Account;
	private transient String oldVar_AccountName;
	private transient String oldVar_TxnCCy;
	private transient String oldVar_TxnCode;
	private transient Timestamp oldVar_PostingDate;
	private transient Timestamp oldVar_ValueDate;
	private transient double oldVar_TxnAmount;
	private transient String oldVar_TxnReference;
	private transient String oldVar_NarrLine1;
	private transient String oldVar_NarrLine2;
	private transient String oldVar_NarrLine3;
	private transient String oldVar_NarrLine4;
	private transient double oldVar_ExchRate_Batch;
	private transient double oldVar_ExchRate_Ac;
	private transient double oldVar_TxnAmount_Batch;
	private transient double oldVar_TxnAmount_Ac;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_JVPostingEntryDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;
	protected Button btnEdit;
	protected Button btnDelete;
	protected Button btnSave;
	protected Button btnCancel;
	protected Button btnClose;
	protected Button btnHelp;
	protected Button btnNotes;

	private transient String oldVar_TxnCCyName;

	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private List<ValueLabel> listTxnCode = PennantStaticListUtil.getTranType();
	//==========================JV Posting Entry
	private JVPostingDialogCtrl jVPostingDialogCtrl;
	private List<JVPostingEntry> jVPostingEntryList;
	private int accCccyFormatter = 0;
	private int txnCccyFormatter = 0;
	private String baseCcy = SystemParameterDetails.getSystemParameterValue("EXT_BASE_CCY").toString();

	*//**
	 * default constructor.<br>
	 *//*
	public JVPostingEntryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	*//**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected JVPostingEntry object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onCreate$window_JVPostingEntryDialog(Event event) throws Exception {
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
			if (args.containsKey("jVPostingEntry")) {
				this.jVPostingEntry = (JVPostingEntry) args.get("jVPostingEntry");
				JVPostingEntry befImage = new JVPostingEntry();
				BeanUtils.copyProperties(this.jVPostingEntry, befImage);
				this.jVPostingEntry.setBefImage(befImage);

				setJVPostingEntry(this.jVPostingEntry);
			} else {
				setJVPostingEntry(null);
			}
			this.jVPostingEntry.setWorkflowId(0);
			doLoadWorkFlow(this.jVPostingEntry.isWorkflow(), this.jVPostingEntry.getWorkflowId(), this.jVPostingEntry.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "JVPostingEntryDialog");
			} else {
				getUserWorkspace().alocateAuthorities("JVPostingEntryDialog");
			}

			 set components visible dependent of the users rights 
			doCheckRights();
			if (args.containsKey("role")) {
				getUserWorkspace().alocateRoleAuthorities(args.get("role").toString(), "JVPostingEntryDialog");
			}

			// READ OVERHANDED params !
			// we get the jVPostingEntryListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete jVPostingEntry here.
			if (args.containsKey("jVPostingDialogCtrl")) {
				setJVPostingDialogCtrl((JVPostingDialogCtrl) args.get("jVPostingDialogCtrl"));
			} else {
				setJVPostingDialogCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getJVPostingEntry());
		} catch (Exception e) {
			createException(window_JVPostingEntryDialog, e);
			logger.error(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doStoreInitValues();
		displayComponents(ScreenCTL.SCRN_GNEDT);
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doResetInitValues();
		displayComponents(ScreenCTL.SCRN_GNINT);
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_JVPostingEntryDialog);
		logger.debug("Leaving" + event.toString());
	}

	*//**
	* when the "close" button is clicked. <br>
	* 
	* @param event
	* @throws InterruptedException
	*//*
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	*//**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onClose$window_JVPostingEntryDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 *//*
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(getNotes("JVPostingEntry", getJVPostingEntry().getBatchReference(), getJVPostingEntry().getVersion()), this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onFulfill$txnCCy(Event event) {
		Object dataObject = txnCCy.getObject();
		if (dataObject instanceof String) {
			this.txnCCy.setValue(dataObject.toString());
			this.txnCCy.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.txnCCy.setValue(details.getCcyCode());
				this.txnCCy.setDescription(details.getCcyDesc());
				txnCccyFormatter = details.getCcyEditField();
			}
		}
		calcJVPostings();
	}

	public void onChange$txnCode(Event event) {
		calcJVPostings();
	}

	public void onChange$txnAmount(Event event) {
		calcJVPostings();
	}

	public void onClick$btnSearchaccountName(Event event) {
		Object dataObject = ExtendedSearchListBox.show(this.window_JVPostingEntryDialog, "Accounts");
		if (dataObject instanceof String) {
			this.account.setValue(PennantApplicationUtil.formatAccountNumber(dataObject.toString()));
			this.accountName.setValue("");
			this.accCcy.setValue("");
		} else {
			Accounts details = (Accounts) dataObject;
			if (details != null) {
				this.account.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
				this.accountName.setValue(details.getAcShortName());
				this.accCcy.setValue(details.getAcCcy());
				accCccyFormatter = details.getLovDescFinFormatter();
			}
		}
		calcJVPostings();
	}

	protected Decimalbox AmtInAccCurrency;
	protected Decimalbox AmtInBaseCurrency;

	private void calcJVPostings() {
		try {
			String txnCode = getTransSelected();
			Currency baseCurrency = PennantAppUtil.getCurrencyBycode(baseCcy);
			BigDecimal accRate = BigDecimal.ZERO;
			BigDecimal txnRate = BigDecimal.ZERO;
			this.txnAmount.setScale(txnCccyFormatter);
			this.txnAmount.setFormat(PennantAppUtil.getAmountFormate(txnCccyFormatter));
			this.txnAmount_Ac.setScale(accCccyFormatter);
			this.txnAmount_Ac.setFormat(PennantAppUtil.getAmountFormate(accCccyFormatter));
			this.txnAmount_Batch.setScale(txnCccyFormatter);
			this.txnAmount_Batch.setFormat(PennantAppUtil.getAmountFormate(txnCccyFormatter));
			if (baseCurrency != null) {
				if (baseCurrency.getCcyCode().equals(StringUtils.trimToEmpty(this.accCcy.getValue()))) {
					accRate = BigDecimal.ONE;
				} else {
					Currency AcCurrency = PennantAppUtil.getCurrencyBycode(StringUtils.trimToEmpty(this.accCcy.getValue()));
					if (!txnCode.equals("") && AcCurrency != null) {
						if (PennantConstants.DEBIT.equals(txnCode)) {
							accRate = (AcCurrency.getCcyUserRateBuy());
						} else {
							accRate = (AcCurrency.getCcyUserRateSell());
						}
					} else {
						accRate = (BigDecimal.ZERO);
					}

				}
				if (baseCurrency.getCcyCode().equals(StringUtils.trimToEmpty(this.txnCCy.getValue()))) {
					txnRate = (BigDecimal.ONE);
				} else {
					Currency txnCurrency = PennantAppUtil.getCurrencyBycode(StringUtils.trimToEmpty(this.txnCCy.getValue()));
					if (!txnCode.equals("") && txnCurrency != null) {
						if (PennantConstants.DEBIT.equals(txnCode)) {
							txnRate = (txnCurrency.getCcyUserRateBuy());
						} else {
							txnRate = (txnCurrency.getCcyUserRateSell());
						}
					} else {
						txnRate = (BigDecimal.ZERO);
					}

				}
			}
			if (this.txnAmount.getValue() != null && this.txnAmount.getValue().compareTo(BigDecimal.ZERO) != 0) {
				this.exchRate_Ac.setValue(accRate);
				this.txnAmount_Ac.setValue((accRate.multiply(this.txnAmount.getValue())));
				this.exchRate_Batch.setValue(txnRate);
				this.txnAmount_Batch.setValue((txnRate.multiply(this.txnAmount.getValue())));
				BigDecimal txnRateAcCcy = txnRate.divide(accRate, 9, RoundingMode.HALF_DOWN);
				this.calExchRate.setValue(txnRateAcCcy);
				this.AmtInAccCurrency.setValue((txnRateAcCcy.multiply(this.txnAmount.getValue())));
				this.AmtInBaseCurrency.setValue((txnRate.multiply(this.txnAmount.getValue())));
			} else {
				this.AmtInAccCurrency.setValue(BigDecimal.ZERO);
				this.AmtInBaseCurrency.setValue(BigDecimal.ZERO);
				this.calExchRate.setValue(BigDecimal.ZERO);
				this.exchRate_Ac.setValue(BigDecimal.ZERO);
				this.exchRate_Batch.setValue(BigDecimal.ZERO);
			}


		} catch (Exception e) {
			logger.debug(e);
		}

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aJVPostingEntry
	 * @throws InterruptedException
	 *//*
	public void doShowDialog(JVPostingEntry aJVPostingEntry) throws InterruptedException {
		logger.debug("Entering");

		// if aJVPostingEntry == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aJVPostingEntry == null) {
			*//** !!! DO NOT BREAK THE TIERS !!! *//*
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			//aJVPostingEntry = getJVPostingEntryService().getNewJVPostingEntry();

			setJVPostingEntry(aJVPostingEntry);
		} else {
			setJVPostingEntry(aJVPostingEntry);
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aJVPostingEntry);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aJVPostingEntry.isNewRecord()));

			doStoreInitValues();

			// stores the initial data for comparing if they are changed
			// during user action.
			getJVPostingDialogCtrl().getWindow_JVPostingDialog().setVisible(false);
			getJVPostingDialogCtrl().getWindow_JVPostingDialog().getParent().appendChild(window_JVPostingEntryDialog);

			//setDialog(this.window_JVPostingEntryDialog);

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

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(), this.userAction, this.batchReference, this.account));

		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnSave"));
		if (getJVPostingEntry().isNewRecord()) {
			setComponentAccessType("JVPostingEntryDialog_BatchReference", false, this.batchReference, this.space_BatchReference, this.label_BatchReference,
			        this.hlayout_BatchReference, null);
			this.txnReference.setReadonly(false);
		} else {
			this.txnReference.setReadonly(true);
		}

		logger.debug("Leaving");
	}

	*//**
	 * Set the components to ReadOnly. <br>
	 *//*
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		if (readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(jVPostingEntry.getRecordType())))) {
		}
		this.btnSearchaccountName.setDisabled(getUserWorkspace().isReadOnly("JVPostingEntryDialog_Account"));
		//this.accountName.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_AccountName"));
		this.txnCCy.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_TxnCCy"));

		this.txnCode.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_TxnCode"));
		this.postingDate.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_PostingDate"));
		this.valueDate.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_ValueDate"));
		this.txnAmount.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_TxnAmount"));
		this.txnReference.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_TxnReference"));

		//this.exchRate_Ac.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_ExchRate_Ac"));
		//this.exchRate_Batch.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_ExchRate_Batch"));
		//this.txnAmount_Ac.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_TxnAmount_Ac"));
		//this.txnAmount_Batch.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_TxnAmount_Batch"));

		this.narrLine1.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_NarrLine1"));
		this.narrLine2.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_NarrLine2"));
		this.narrLine3.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_NarrLine3"));
		this.narrLine4.setReadonly(getUserWorkspace().isReadOnly("JVPostingEntryDialog_NarrLine4"));

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 *//*
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("JVPostingEntryDialog");
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnSave"));
		}

		 create the Button Controller. Disable not used buttons during working 
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
		        this.btnClose, this.btnNotes);

		logger.debug("Leaving");
	}

	*//**
	 * Set the properties of the fields, like maxLength.<br>
	 *//*
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.batchReference.setMaxlength(50);
		this.account.setMaxlength(20);
		this.accountName.setMaxlength(50);
		this.txnCCy.setMaxlength(3);
        this.txnCCy.setMandatoryStyle(true);
		this.txnCCy.setModuleName("Currency");
		this.txnCCy.setValueColumn("CcyCode");
		this.txnCCy.setDescColumn("CcyDesc");
		this.txnCCy.setValidateColumns(new String[] { "CcyCode" });
		this.postingDate.setFormat(PennantConstants.dateFormat);
		this.valueDate.setFormat(PennantConstants.dateFormat);
		this.txnAmount.setMaxlength(18);
		this.txnAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.txnAmount.setScale(0);
		this.txnReference.setMaxlength(50);
		this.narrLine1.setMaxlength(100);
		this.narrLine2.setMaxlength(100);
		this.narrLine3.setMaxlength(100);
		this.narrLine4.setMaxlength(100);
		this.exchRate_Batch.setMaxlength(13);
		this.exchRate_Batch.setFormat(PennantConstants.rateFormate9);
		this.exchRate_Batch.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.exchRate_Batch.setScale(9);
		this.exchRate_Ac.setMaxlength(13);
		this.exchRate_Ac.setFormat(PennantConstants.rateFormate9);
		this.exchRate_Ac.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.exchRate_Ac.setScale(9);
		this.calExchRate.setMaxlength(13);
		this.calExchRate.setFormat(PennantConstants.rateFormate9);
		this.calExchRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.calExchRate.setScale(9);
		this.txnAmount_Batch.setMaxlength(18);
		this.txnAmount_Batch.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.txnAmount_Batch.setScale(0);
		this.txnAmount_Ac.setMaxlength(18);
		this.txnAmount_Ac.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.txnAmount_Ac.setScale(0);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	*//**
	 * Stores the initialinitial values to member variables. <br>
	 *//*
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_BatchReference = this.batchReference.getValue();
		this.oldVar_Account = PennantApplicationUtil.unFormatAccountNumber(this.account.getValue());
		this.oldVar_AccountName = this.accountName.getValue();
		this.oldVar_TxnCCy = this.txnCCy.getValue();
		this.oldVar_TxnCCyName = this.txnCCy.getDescription();
		this.oldVar_TxnCode = PennantConstants.List_Select;
		if (this.txnCode.getSelectedItem() != null) {
			this.oldVar_TxnCode = this.txnCode.getSelectedItem().getValue().toString();
		}
		this.oldVar_PostingDate = PennantApplicationUtil.getTimestamp(this.postingDate.getValue());
		this.oldVar_ValueDate = PennantApplicationUtil.getTimestamp(this.valueDate.getValue());
		this.oldVar_TxnAmount = this.txnAmount.doubleValue();
		this.oldVar_TxnReference = this.txnReference.getValue();
		this.oldVar_NarrLine1 = this.narrLine1.getValue();
		this.oldVar_NarrLine2 = this.narrLine2.getValue();
		this.oldVar_NarrLine3 = this.narrLine3.getValue();
		this.oldVar_NarrLine4 = this.narrLine4.getValue();
		this.oldVar_ExchRate_Batch = this.exchRate_Batch.doubleValue();
		this.oldVar_ExchRate_Ac = this.exchRate_Ac.doubleValue();
		this.oldVar_TxnAmount_Batch = this.txnAmount_Batch.doubleValue();
		this.oldVar_TxnAmount_Ac = this.txnAmount_Ac.doubleValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	*//**
	 * Resets the initial values from member variables. <br>
	 *//*
	private void doResetInitValues() {
		logger.debug("Entering");
		this.batchReference.setValue(this.oldVar_BatchReference);
		this.account.setValue(PennantApplicationUtil.formatAccountNumber(this.oldVar_Account));
		this.accountName.setValue(this.oldVar_AccountName);
		this.txnCCy.setValue(this.oldVar_TxnCCy);
		this.txnCCy.setDescription(this.oldVar_TxnCCyName);
		this.postingDate.setValue(this.oldVar_PostingDate);
		this.valueDate.setValue(this.oldVar_ValueDate);
		this.txnAmount.setValue(new BigDecimal(this.oldVar_TxnAmount));
		this.txnReference.setValue(this.oldVar_TxnReference);
		this.narrLine1.setValue(this.oldVar_NarrLine1);
		this.narrLine2.setValue(this.oldVar_NarrLine2);
		this.narrLine3.setValue(this.oldVar_NarrLine3);
		this.narrLine4.setValue(this.oldVar_NarrLine4);
		this.exchRate_Batch.setValue(new BigDecimal(this.oldVar_ExchRate_Batch));
		this.exchRate_Ac.setValue(new BigDecimal(this.oldVar_ExchRate_Ac));
		this.txnAmount_Batch.setValue(new BigDecimal(this.oldVar_TxnAmount_Batch));
		this.txnAmount_Ac.setValue(new BigDecimal(this.oldVar_TxnAmount_Ac));
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled() & !enqModule) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aJVPostingEntry
	 *            JVPostingEntry
	 *//*
	public void doWriteBeanToComponents(JVPostingEntry aJVPostingEntry) {
		logger.debug("Entering");
		this.baseCCy.setValue(this.baseCcy);
		this.accCcy.setValue(aJVPostingEntry.getAccCCy());
		this.batchReference.setValue(aJVPostingEntry.getBatchReference());
		this.account.setValue(PennantApplicationUtil.formatAccountNumber(aJVPostingEntry.getAccount()));
		this.accountName.setValue(aJVPostingEntry.getAccountName());
		this.txnCCy.setValue(aJVPostingEntry.getTxnCCy());
		fillComboBox(this.txnCode, aJVPostingEntry.getTxnCode(), listTxnCode, "");
		this.postingDate.setValue(aJVPostingEntry.getPostingDate());
		this.valueDate.setValue(aJVPostingEntry.getValueDate());
		this.txnReference.setValue(aJVPostingEntry.getTxnReference());
		this.narrLine1.setValue(aJVPostingEntry.getNarrLine1());
		this.narrLine2.setValue(aJVPostingEntry.getNarrLine2());
		this.narrLine3.setValue(aJVPostingEntry.getNarrLine3());
		this.narrLine4.setValue(aJVPostingEntry.getNarrLine4());
		this.exchRate_Ac.setValue(aJVPostingEntry.getExchRate_Ac());
		this.exchRate_Batch.setValue(aJVPostingEntry.getExchRate_Batch());
		accCccyFormatter = aJVPostingEntry.getAccCCyEditField();
		txnCccyFormatter = aJVPostingEntry.getTxnCCyEditField();
		this.txnAmount.setScale(txnCccyFormatter);
		this.txnAmount.setFormat(PennantAppUtil.getAmountFormate(txnCccyFormatter));

		this.txnAmount.setValue(PennantAppUtil.formateAmount(aJVPostingEntry.getTxnAmount(), txnCccyFormatter));
		this.txnAmount_Ac.setValue(PennantAppUtil.formateAmount(aJVPostingEntry.getTxnAmount_Ac(), accCccyFormatter));
		this.txnAmount_Batch.setValue(PennantAppUtil.formateAmount(aJVPostingEntry.getTxnAmount_Batch(), txnCccyFormatter));
		if (aJVPostingEntry.isNewRecord()) {
			this.txnCCy.setDescription("");
		} else {
			this.txnCCy.setDescription(aJVPostingEntry.getTxnCCyName());
		}
		calcJVPostings();
		this.recordStatus.setValue(aJVPostingEntry.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aJVPostingEntry.getRecordType()));
		logger.debug("Leaving");
	}

	*//**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPostingEntry
	 *//*
	public void doWriteComponentsToBean(JVPostingEntry aJVPostingEntry) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Batch Reference
		try {
			aJVPostingEntry.setBatchReference(this.batchReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Account
		try {
			aJVPostingEntry.setAccount(PennantApplicationUtil.unFormatAccountNumber(this.account.getValue()));
			aJVPostingEntry.setAccCCy(this.accCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Account Name
		try {
			aJVPostingEntry.setAccountName(this.accountName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Txn C Cy
		try {
			aJVPostingEntry.setTxnCCyName(this.txnCCy.getDescription());
			aJVPostingEntry.setTxnCCy(this.txnCCy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Txn Code
		try {
			String strTxnCode = null;
			if (this.txnCode.getSelectedItem() != null) {
				strTxnCode = this.txnCode.getSelectedItem().getValue().toString();
			}
			if (strTxnCode != null && !PennantConstants.List_Select.equals(strTxnCode)) {
				aJVPostingEntry.setTxnCode(strTxnCode);
			} else {
				aJVPostingEntry.setTxnCode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Posting Date
		try {
			if (this.postingDate.getValue() != null) {
				aJVPostingEntry.setPostingDate(new Timestamp(this.postingDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Value Date
		try {
			if (this.valueDate.getValue() != null) {
				aJVPostingEntry.setValueDate(new Timestamp(this.valueDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Txn Amount
		try {
			if (this.txnAmount.getValue() != null) {
				aJVPostingEntry.setTxnAmount(PennantAppUtil.unFormateAmount(this.txnAmount.getValue(), txnCccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Txn Reference
		try {
			aJVPostingEntry.setTxnReference(this.txnReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Narr Line1
		try {
			aJVPostingEntry.setNarrLine1(this.narrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Narr Line2
		try {
			aJVPostingEntry.setNarrLine2(this.narrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Narr Line3
		try {
			aJVPostingEntry.setNarrLine3(this.narrLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Narr Line4
		try {
			aJVPostingEntry.setNarrLine4(this.narrLine4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Exch Rate_ Batch
		try {
			if (this.exchRate_Batch.getValue() != null) {
				aJVPostingEntry.setExchRate_Batch(this.exchRate_Batch.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Exch Rate_ Ac
		try {
			if (this.exchRate_Ac.getValue() != null) {
				aJVPostingEntry.setExchRate_Ac(this.exchRate_Ac.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Txn Amount_ Batch
		try {
			if (this.txnAmount_Batch.getValue() != null) {

				aJVPostingEntry.setTxnAmount_Batch(PennantAppUtil.unFormateAmount(this.txnAmount_Batch.getValue(), txnCccyFormatter));
				aJVPostingEntry.setTxnCCyEditField(txnCccyFormatter);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Txn Amount_ Ac
		try {
			if (this.txnAmount_Ac.getValue() != null) {
				aJVPostingEntry.setTxnAmount_Ac(PennantAppUtil.unFormateAmount(this.txnAmount_Ac.getValue(), accCccyFormatter));
				aJVPostingEntry.setAccCCyEditField(accCccyFormatter);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

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

	*//**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 *//*
	private boolean isDataChanged() {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();

		if (!StringUtils.trimToEmpty(this.oldVar_BatchReference).equals(StringUtils.trimToEmpty(this.batchReference.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_Account).equals(StringUtils.trimToEmpty(PennantApplicationUtil.unFormatAccountNumber(this.account.getValue())))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_AccountName).equals(StringUtils.trimToEmpty(this.accountName.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_TxnCCy).equals(StringUtils.trimToEmpty(this.txnCCy.getValue()))) {
			return true;
		}
		String strTxnCode = PennantConstants.List_Select;
		if (this.txnCode.getSelectedItem() != null) {
			strTxnCode = this.txnCode.getSelectedItem().getValue().toString();
		}

		if (!StringUtils.trimToEmpty(this.oldVar_TxnCode).equals(strTxnCode)) {
			return true;
		}
		Timestamp new_PostingDate = null;

		if (this.postingDate.getValue() != null) {
			new_PostingDate = new Timestamp(this.postingDate.getValue().getTime());
		}

		if (this.oldVar_PostingDate!=null && new_PostingDate!=null && this.oldVar_PostingDate.compareTo(new_PostingDate)!=0) {
			return true;
		}
		Timestamp new_ValueDate = null;

		if (this.valueDate.getValue() != null) {
			new_ValueDate = new Timestamp(this.valueDate.getValue().getTime());
		}

		if (this.oldVar_ValueDate!=null && new_ValueDate!=null && this.oldVar_ValueDate.compareTo(new_ValueDate)!=0) {
			return true;
		}
		if (this.oldVar_TxnAmount != this.txnAmount.doubleValue()) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_TxnReference).equals(StringUtils.trimToEmpty(this.txnReference.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_NarrLine1).equals(StringUtils.trimToEmpty(this.narrLine1.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_NarrLine2).equals(StringUtils.trimToEmpty(this.narrLine2.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_NarrLine3).equals(StringUtils.trimToEmpty(this.narrLine3.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_NarrLine4).equals(StringUtils.trimToEmpty(this.narrLine4.getValue()))) {
			return true;
		}
		if (this.oldVar_ExchRate_Batch != this.exchRate_Batch.doubleValue()) {
			return true;
		}
		if (this.oldVar_ExchRate_Ac != this.exchRate_Ac.doubleValue()) {
			return true;
		}
		if (this.oldVar_TxnAmount_Batch != this.txnAmount_Batch.doubleValue()) {
			return true;
		}
		if (this.oldVar_TxnAmount_Ac != this.txnAmount_Ac.doubleValue()) {
			return true;
		}
 		logger.debug("Leaving");
		return false;
	}

	*//**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 *//*
	private void doSetValidation() {
		logger.debug("Entering");
		//Account
		if (!this.btnSearchaccountName.isDisabled()) {
			this.account.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_Account.value"), PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, true));
		}
		//Account Name
		if (!this.accountName.isReadonly()) {
			this.accountName.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_AccountName.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		//Txn Code
		if (!this.txnCode.isReadonly()) {
			this.txnCode.setConstraint(new StaticListValidator(listTxnCode, Labels.getLabel("label_JVPostingEntryDialog_TxnCode.value")));
		}
		//Posting Date
		if (!this.postingDate.isReadonly()) {
			//this.postingDate.setConstraint(new PTDateValidator(Labels.getLabel("label_JVPostingEntryDialog_PostingDate.value"),true,false));
		}
		//Value Date
		if (!this.valueDate.isReadonly()) {
			//this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_JVPostingEntryDialog_ValueDate.value"),true,false));
		}
		//Txn Amount
		if (!this.txnAmount.isReadonly()) {
			this.txnAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnAmount.value"), txnCccyFormatter, true, false, 0));
		}
		//Txn Reference
		if (!this.txnReference.isReadonly()) {
			this.txnReference
			        .setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnReference.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		//		//Narr Line1
		//		if (!this.narrLine1.isReadonly()) {
		//			this.narrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_NarrLine1.value"), PennantRegularExpressions.REGEX_NAME, true));
		//		}
		//		//Narr Line2
		//		if (!this.narrLine2.isReadonly()) {
		//			this.narrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_NarrLine2.value"), PennantRegularExpressions.REGEX_NAME, true));
		//		}
		//		//Narr Line3
		//		if (!this.narrLine3.isReadonly()) {
		//			this.narrLine3.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_NarrLine3.value"), PennantRegularExpressions.REGEX_NAME, true));
		//		}
		//		//Narr Line4
		//		if (!this.narrLine4.isReadonly()) {
		//			this.narrLine4.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_NarrLine4.value"), PennantRegularExpressions.REGEX_NAME, true));
		//		}
		//Exch Rate_ Batch
		if (!this.exchRate_Batch.isReadonly()) {
			this.exchRate_Batch.setConstraint(new PTDecimalValidator(Labels.getLabel("label_JVPostingEntryDialog_ExchRate_Batch.value"), 9, true, false, 0));
		}
		//Exch Rate_ Ac
		if (!this.exchRate_Ac.isReadonly()) {
			this.exchRate_Ac.setConstraint(new PTDecimalValidator(Labels.getLabel("label_JVPostingEntryDialog_ExchRate_Ac.value"), 9, true, false, 0));
		}
		//Txn Amount_ Batch
		if (!this.txnAmount_Batch.isReadonly()) {
			this.txnAmount_Batch.setConstraint(new PTDecimalValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnAmount_Batch.value"), 0, true, false, 0));
		}
		//Txn Amount_ Ac
		if (!this.txnAmount_Ac.isReadonly()) {
			this.txnAmount_Ac.setConstraint(new PTDecimalValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnAmount_Ac.value"), 0, true, false, 0));
		}
		logger.debug("Leaving");
	}

	*//**
	 * Remove the Validation by setting empty constraints.
	 *//*
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.batchReference.setConstraint("");
		this.account.setConstraint("");
		this.accountName.setConstraint("");
		this.txnCode.setConstraint("");
		this.postingDate.setConstraint("");
		this.valueDate.setConstraint("");
		this.txnAmount.setConstraint("");
		this.txnReference.setConstraint("");
		this.narrLine1.setConstraint("");
		this.narrLine2.setConstraint("");
		this.narrLine3.setConstraint("");
		this.narrLine4.setConstraint("");
		this.exchRate_Batch.setConstraint("");
		this.exchRate_Ac.setConstraint("");
		this.txnAmount_Batch.setConstraint("");
		this.txnAmount_Ac.setConstraint("");
		logger.debug("Leaving");
	}

	*//**
	 * Set Validations for LOV Fields
	 *//*

	private void doSetLOVValidation() {
		//Txn C Cy
		if (txnCCy.isButtonVisible()) {
			this.txnCCy.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_JVPostingEntryDialog_TxnCCy.value") }));
		}
	}

	*//**
	 * Remove the Validation by setting empty constraints.
	 *//*

	private void doRemoveLOVValidation() {
		this.txnCCy.setConstraint("");
	}

	*//**
	 * Remove Error Messages for Fields
	 *//*

	private void doClearMessage() {
		logger.debug("Entering");
		this.batchReference.setErrorMessage("");
		this.account.setErrorMessage("");
		this.accountName.setErrorMessage("");
		this.txnCCy.setErrorMessage("");
		this.txnCode.setErrorMessage("");
		this.postingDate.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.txnAmount.setErrorMessage("");
		this.txnReference.setErrorMessage("");
		this.narrLine1.setErrorMessage("");
		this.narrLine2.setErrorMessage("");
		this.narrLine3.setErrorMessage("");
		this.narrLine4.setErrorMessage("");
		this.exchRate_Batch.setErrorMessage("");
		this.exchRate_Ac.setErrorMessage("");
		this.txnAmount_Batch.setErrorMessage("");
		this.txnAmount_Ac.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 *//*
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
			window_JVPostingEntryDialog.onClose();
			getJVPostingDialogCtrl().getWindow_JVPostingDialog().setVisible(true);
			//closeDialog(this.window_JVPostingEntryDialog, "JVPostingEntry");
		}

		logger.debug("Leaving");
	}

	*//**
	 * Deletes a JVPostingEntry object from database.<br>
	 * 
	 * @throws InterruptedException
	 *//*
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		BeanUtils.copyProperties(getJVPostingEntry(), aJVPostingEntry);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aJVPostingEntry.getBatchReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aJVPostingEntry.getRecordType()).equals("")) {
				aJVPostingEntry.setVersion(aJVPostingEntry.getVersion() + 1);
				aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aJVPostingEntry.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					aJVPostingEntry.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aJVPostingEntry.getNextTaskId(), aJVPostingEntry);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aJVPostingEntry.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aJVPostingEntry.setVersion(aJVPostingEntry.getVersion() + 1);
				aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = processJVPostingEntry(aJVPostingEntry, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_JVPostingEntryDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getJVPostingDialogCtrl().doFilllistbox(this.jVPostingEntryList);
					window_JVPostingEntryDialog.onClose();
					getJVPostingDialogCtrl().getWindow_JVPostingDialog().setVisible(true);
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_JVPostingEntryDialog, e);
			}

		}
		logger.debug("Leaving");
	}

	*//**
	 * Clears the components values. <br>
	 *//*
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.batchReference.setValue("");
		this.account.setValue("");
		this.accountName.setValue("");
		this.txnCCy.setValue("");
		this.txnCCy.setDescription("");
		this.txnCode.setSelectedIndex(0);
		this.postingDate.setText("");
		this.valueDate.setText("");
		this.txnAmount.setValue("");
		this.txnReference.setValue("");
		this.narrLine1.setValue("");
		this.narrLine2.setValue("");
		this.narrLine3.setValue("");
		this.narrLine4.setValue("");
		this.exchRate_Batch.setValue("");
		this.exchRate_Ac.setValue("");
		this.txnAmount_Batch.setValue("");
		this.txnAmount_Ac.setValue("");
		logger.debug("Leaving");
	}

	*//**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 *//*
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		BeanUtils.copyProperties(getJVPostingEntry(), aJVPostingEntry);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aJVPostingEntry.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aJVPostingEntry.getNextTaskId(), aJVPostingEntry);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aJVPostingEntry.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the JVPostingEntry object with the components data
			doWriteComponentsToBean(aJVPostingEntry);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aJVPostingEntry.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aJVPostingEntry.getRecordType()).equals("")) {
				aJVPostingEntry.setVersion(aJVPostingEntry.getVersion() + 1);
				if (isNew) {
					aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aJVPostingEntry.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aJVPostingEntry.setVersion(1);
				aJVPostingEntry.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.trimToEmpty(aJVPostingEntry.getRecordType()).equals("")) {
				aJVPostingEntry.setVersion(aJVPostingEntry.getVersion() + 1);
				aJVPostingEntry.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aJVPostingEntry.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}

		}

		// save it to database
		try {

			AuditHeader auditHeader = processJVPostingEntry(aJVPostingEntry, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_JVPostingEntryDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getJVPostingDialogCtrl().doFilllistbox(this.jVPostingEntryList);
				window_JVPostingEntryDialog.onClose();
				getJVPostingDialogCtrl().getWindow_JVPostingDialog().setVisible(true);
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_JVPostingEntryDialog, e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader processJVPostingEntry(JVPostingEntry aJVPostingEntry, String tranType) {

		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aJVPostingEntry, tranType);
		jVPostingEntryList = new ArrayList<JVPostingEntry>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aJVPostingEntry.getBatchReference();
		valueParm[1] = aJVPostingEntry.getTxnReference();

		errParm[0] = PennantJavaUtil.getLabel("label_BatchReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_TxnReference") + ":" + valueParm[1];
		if (getJVPostingDialogCtrl().getJVPostingEntryList() != null && getJVPostingDialogCtrl().getJVPostingEntryList().size() > 0) {
			for (int i = 0; i < getJVPostingDialogCtrl().getJVPostingEntryList().size(); i++) {
				JVPostingEntry jVPostingEntry = getJVPostingDialogCtrl().getJVPostingEntryList().get(i);

				if (jVPostingEntry.getTxnReference().equals(aJVPostingEntry.getTxnReference())) {
					// Both Current and Existing list rating same

					if (aJVPostingEntry.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace()
						        .getUserLanguage()));
						return auditHeader;
					}

					if (tranType == PennantConstants.TRAN_DEL) {
						if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							jVPostingEntryList.add(aJVPostingEntry);
						} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							jVPostingEntryList.add(aJVPostingEntry);
						} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;

							for (int j = 0; j < getJVPostingDialogCtrl().getJVPosting().getJVPostingEntrysList().size(); j++) {
								JVPostingEntry jvPosEntry = getJVPostingDialogCtrl().getJVPosting().getJVPostingEntrysList().get(j);
								if (jvPosEntry.getTxnReference().equals(aJVPostingEntry.getTxnReference())) {
									jVPostingEntryList.add(jvPosEntry);
								}
							}
						} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aJVPostingEntry.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							jVPostingEntryList.add(jVPostingEntry);
						}
					}
				} else {
					jVPostingEntryList.add(jVPostingEntry);
				}
			}
		}
		if (!recordAdded) {
			jVPostingEntryList.add(aJVPostingEntry);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	*//**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 *//*

	private AuditHeader getAuditHeader(JVPostingEntry aJVPostingEntry, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJVPostingEntry.getBefImage(), aJVPostingEntry);
		return new AuditHeader(aJVPostingEntry.getBatchReference(), null, null, null, auditDetail, aJVPostingEntry.getUserDetails(), getOverideMap());
	}

	*//**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 *//*

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

	public JVPostingEntry getJVPostingEntry() {
		return this.jVPostingEntry;
	}

	public void setJVPostingEntry(JVPostingEntry jVPostingEntry) {
		this.jVPostingEntry = jVPostingEntry;
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

	public void setJVPostingDialogCtrl(JVPostingDialogCtrl jVPostingDialogCtrl) {
		this.jVPostingDialogCtrl = jVPostingDialogCtrl;
	}

	public JVPostingDialogCtrl getJVPostingDialogCtrl() {
		return jVPostingDialogCtrl;
	}

	private String getTransSelected() {
		if (this.txnCode.getSelectedItem() != null && !this.txnCode.getSelectedItem().getValue().toString().equals("#")) {
			return this.txnCode.getSelectedItem().getValue().toString();
		} else {
			return "";
		}
	}

*/}
