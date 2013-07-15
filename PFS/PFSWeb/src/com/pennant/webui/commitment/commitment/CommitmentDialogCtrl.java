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
 * FileName    		:  CommitmentDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2013    														*
 *                                                                  						*
 * Modified Date    :  25-03-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.commitment.commitment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.CommitmentRuleData;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Commitment/Commitment/commitmentDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CommitmentDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long	         serialVersionUID	     = 1L;
	private final static Logger	         logger	                 = Logger.getLogger(CommitmentDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	                 window_CommitmentDialog;
	protected Row	                     row0;
	protected Label	                     label_CmtReference;
	protected Hlayout	                 hlayout_CmtReference;
	protected Space	                     space_CmtReference;

	protected Textbox	                 cmtReference;
	protected Label	                     label_custID;

	protected Hlayout	                 hlayout_custID;
	protected Space	                     space_custID;

	protected Longbox	                 custID;
	protected Row	                     row1;

	protected Label	                     label_CmtBranch;
	protected Hlayout	                 hlayout_CmtBranch;
	protected Space	                     space_CmtBranch;

	protected Textbox	                 cmtBranch;
	protected Label	                     label_OpenAccount;
	protected Hlayout	                 hlayout_OpenAccount;
	protected Space	                     space_OpenAccount;

	protected Checkbox	                 openAccount;
	protected Row	                     row2;
	protected Label	                     label_CmtAccount;
	protected Hlayout	                 hlayout_CmtAccount;
	protected Space	                     space_CmtAccount;

	protected Textbox	                 cmtAccount;
	protected Label	                     label_CmtCcy;
	protected Hlayout	                 hlayout_CmtCcy;
	protected Space	                     space_CmtCcy;

	protected Textbox	                 cmtCcy;
	protected Row	                     row3;
	protected Label	                     label_CmtPftRateMin;
	protected Hlayout	                 hlayout_CmtPftRateMin;
	protected Space	                     space_CmtPftRateMin;

	protected Decimalbox	             cmtPftRateMin;
	protected Label	                     label_CmtPftRateMax;
	protected Hlayout	                 hlayout_CmtPftRateMax;
	protected Space	                     space_CmtPftRateMax;

	protected Decimalbox	             cmtPftRateMax;
	protected Row	                     row4;
	protected Label	                     label_CmtAmount;
	protected Hlayout	                 hlayout_CmtAmount;
	protected Space	                     space_CmtAmount;

	protected Decimalbox	             cmtAmount;
	protected Label	                     label_CmtUtilizedAmount;
	protected Hlayout	                 hlayout_CmtUtilizedAmount;
	protected Space	                     space_CmtUtilizedAmount;

	protected Decimalbox	             cmtUtilizedAmount;
	protected Row	                     row5;
	protected Label	                     label_CmtAvailable;
	protected Hlayout	                 hlayout_CmtAvailable;
	protected Space	                     space_CmtAvailable;

	protected Decimalbox	             cmtAvailable;
	protected Label	                     label_CmtPromisedDate;
	protected Hlayout	                 hlayout_CmtPromisedDate;
	protected Space	                     space_CmtPromisedDate;

	protected Datebox	                 cmtPromisedDate;
	protected Row	                     row6;
	protected Label	                     label_CmtStartDate;
	protected Hlayout	                 hlayout_CmtStartDate;
	protected Space	                     space_CmtStartDate;

	protected Datebox	                 cmtStartDate;
	protected Label	                     label_CmtExpDate;
	protected Hlayout	                 hlayout_CmtExpDate;
	protected Space	                     space_CmtExpDate;

	protected Datebox	                 cmtExpDate;
	protected Row	                     row7;
	protected Label	                     label_CmtTitle;
	protected Hlayout	                 hlayout_CmtTitle;
	protected Space	                     space_CmtTitle;

	protected Textbox	                 cmtTitle;
	protected Label	                     label_CmtNotes;
	protected Hlayout	                 hlayout_CmtNotes;
	protected Space	                     space_CmtNotes;

	protected Textbox	                 cmtNotes;
	protected Row	                     row8;
	protected Label	                     label_Revolving;
	protected Hlayout	                 hlayout_Revolving;
	protected Space	                     space_Revolving;

	protected Checkbox	                 revolving;
	protected Label	                     label_SharedCmt;
	protected Hlayout	                 hlayout_SharedCmt;
	protected Space	                     space_SharedCmt;

	protected Checkbox	                 sharedCmt;
	protected Row	                     row9;
	protected Label	                     label_MultiBranch;
	protected Hlayout	                 hlayout_MultiBranch;
	protected Space	                     space_MultiBranch;

	protected Checkbox	                 multiBranch;
	protected Row	                     row10;
	protected Row	                     row11;
	protected Row	                     row12;
	protected Label	                     label_CmtCharges;
	protected Hlayout	                 hlayout_CmtCharges;
	protected Space	                     space_CmtCharges;
	protected Decimalbox	             cmtCharges;

	protected Label	                     label_CmtChargesAccount;
	protected Hlayout	                 hlayout_CmtChargesAccount;
	protected Space	                     space_CmtChargesAccount;
	protected Textbox	                 cmtChargesAccount;

	protected Label	                     label_CmtActiveStatus;
	protected Hlayout	                 hlayout_CmtActiveStatus;
	protected Space	                     space_CmtActiveStatus;
	protected Checkbox	                 cmtActiveStatus;

	protected Label	                     label_CmtNonperformingStatus;
	protected Hlayout	                 hlayout_CmtNonperformingStatus;
	protected Space	                     space_CmtNonperformingStatus;
	protected Checkbox	                 cmtNonperformingStatus;

	protected Row	                     row13;

	protected Label	                     cmtCommitments;
	protected Label	                     cmtTotAmount;
	protected Label	                     cmtUtilizedTotAmount;
	protected Label	                     cmtUnUtilizedAmount;

	protected Hlayout	                 hlayout_CmtUnUtilizedAmount;
	protected Space	                     space_CmtUnUtilizedAmount;

	protected Label	                     recordStatus;
	protected Label	                     recordType;
	protected Radiogroup	             userAction;
	protected Groupbox	                 gb_statusDetails;
	protected Groupbox	                 groupboxWf;
	protected South	                     south;
	private boolean	                     enqModule	             = false;

	protected Button	                 btnSearchcustID;

	// not auto wired vars
	private Commitment	                 commitment;	                                                                                            // overhanded
	                                                                                                                                                // per
	                                                                                                                                                // param
	private transient CommitmentListCtrl	commitmentListCtrl;	                                                                                // overhanded
	                                                                                                                                                // per
	                                                                                                                                                // param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String	         oldVar_CmtReference;
	private transient long	             oldVar_custID;
	private transient String	         oldVar_CmtBranch;
	private transient boolean	         oldVar_OpenAccount;
	private transient String	         oldVar_CmtAccount;
	private transient String	         oldVar_CmtCcy;
	private transient double	         oldVar_CmtPftRateMin;
	private transient double	         oldVar_CmtPftRateMax;
	private transient double	         oldVar_CmtAmount;
	private transient double	         oldVar_CmtUtilizedAmount;
	private transient double	         oldVar_CmtAvailable;
	private transient Date	             oldVar_CmtPromisedDate;
	private transient Date	             oldVar_CmtStartDate;
	private transient Date	             oldVar_CmtExpDate;
	private transient double	         oldVar_CmtCharges;
	private transient String	         oldVar_cmtChargesAccount;
	private transient boolean	         oldVar_cmtActiveStatus;
	private transient boolean	         oldVar_cmtNonperformingStatus;
	private transient String	         oldVar_CmtTitle;
	private transient String	         oldVar_CmtNotes;
	private transient boolean	         oldVar_Revolving;
	private transient boolean	         oldVar_SharedCmt;
	private transient boolean	         oldVar_MultiBranch;
	private transient String	         oldVar_recordStatus;
	private boolean	                     notes_Entered	         = false;

	// Button controller for the CRUD buttons
	private transient final String	     btnCtroller_ClassPrefix	= "button_CommitmentDialog_";
	private transient ButtonStatusCtrl	 btnCtrl;
	protected Button	                 btnNew;
	protected Button	                 btnEdit;
	protected Button	                 btnDelete;
	protected Button	                 btnSave;
	protected Button	                 btnCancel;
	protected Button	                 btnClose;
	protected Button	                 btnHelp;
	protected Button	                 btnNotes;

	protected Textbox	                 custIDName;
	private transient String	         oldVar_custIDName;
	protected Button	                 btnSearchCmtBranch;
	protected Textbox	                 cmtBranchName;
	private transient String	         oldVar_CmtBranchName;
	protected Button	                 btnSearchCmtCcy;
	protected Textbox	                 cmtCcyName;
	private transient String	         oldVar_CmtCcyName;
	protected Button	                 btnSearchCmtAccount;
	protected Textbox	                 cmtAccountName;
	private transient String	         oldVar_cmtAccountName;

	protected Button	                 btnSearchCmtChargesAccount;
	protected Textbox	                 cmtChargesAccountName;
	private transient String	         oldVar_cmtChargesAccountName;

	// ServiceDAOs / Domain Classes
	private transient CommitmentService	 commitmentService;
	private transient PagedListService	 pagedListService;
	private CommitmentDAO	             commitmentDAO;
	private transient AccountsService	 accountsService;

	protected Checkbox	                 cmtStopRateRange;

	protected Label	                     label_CmtCommitments;
	protected Label	                     label_CmtTotAmount;
	protected Label	                     label_CmtUtilizedTotAmount;
	protected Label	                     label_CmtUnUtilizedAmount;

	protected Tab	                     tab_CommitmentDetails;
	protected Tab	                     tab_CommitmentMovementDetails;
	protected Tab	                     tab_CommitmentPostingDetails;
	protected Listbox	                 listBoxCommitmentMovement;
	protected Listbox	                 listBoxCommitmentPostings;
	protected Listbox	                 listBoxCommitmentFinance;
	protected Row	                     rowCmtSummary;
	protected Row	                     rowCmtCount;
	protected Row	                     rowCmtTotAmount;
	protected Row	                     rowCmtUtilized;
	protected Row	                     rowCmtUnUtilized;

	protected Decimalbox	             calCmtCharges;
	private AccountInterfaceService	     accountInterfaceService;
	private int	                         defaultCCYDecPos	     = 2;

	boolean	                             maintain	             = false;
	boolean	                             newMaintain	         = false;
	BigDecimal	                         oldCmtAmount	         = BigDecimal.ZERO;
	protected Label	                     labelCustIDName;
	String	                             acType	                 = SystemParameterDetails.getSystemParameterValue("COMMITMENT_AC_TYPE").toString();
	public int	                       borderLayoutHeight	= 0;
	Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());

	/**
	 * default constructor.<br>
	 */
	public CommitmentDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Commitment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommitmentDialog(Event event) throws Exception {
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
			if (args.containsKey("commitment")) {
				this.commitment = (Commitment) args.get("commitment");
				Commitment befImage = new Commitment();
				BeanUtils.copyProperties(this.commitment, befImage);
				this.commitment.setBefImage(befImage);

				setCommitment(this.commitment);
			} else {
				setCommitment(null);
			}
			doLoadWorkFlow(this.commitment.isWorkflow(), this.commitment.getWorkflowId(), this.commitment.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "CommitmentDialog");
			} else {
				getUserWorkspace().alocateAuthorities("CommitmentDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the commitmentListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete commitment here.
			if (args.containsKey("commitmentListCtrl")) {
				setCommitmentListCtrl((CommitmentListCtrl) args.get("commitmentListCtrl"));
			} else {
				setCommitmentListCtrl(null);
			}

			if (StringUtils.trimToEmpty(commitment.getRecordType()).equals(PennantConstants.RECORD_TYPE_UPD)) {
				maintain = true;
			} else if (StringUtils.trimToEmpty(commitment.getRecordType()).equals("")
			        && StringUtils.trimToEmpty(commitment.getRecordStatus()).equals(PennantConstants.RCD_STATUS_APPROVED)) {
				maintain = true;
				newMaintain = true;
				oldCmtAmount = getCommitment().getCmtAmount();
			}

			defaultCCYDecPos = getCommitment().getCcyEditField();
			
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxCommitmentPostings.setHeight(this.borderLayoutHeight-175+"px");
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCommitment());
		} catch (Exception e) {
			createException(window_CommitmentDialog, e);
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
		PTMessageUtils.showHelpWindow(event, window_CommitmentDialog);
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
	public void onClose$window_CommitmentDialog(Event event) throws Exception {
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

			ScreenCTL.displayNotes(getNotes("Commitment", getCommitment().getCmtReference(), getCommitment().getVersion()), this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onClick$btnSearchcustID(Event event) {

		Object dataObject = ExtendedSearchListBox.show(this.window_CommitmentDialog, "Customer");
		if (dataObject instanceof String) {
			this.custID.setText("");
			this.custIDName.setValue("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setValue(Long.valueOf(details.getCustID()));
				this.custIDName.setValue(String.valueOf(details.getCustCIF()));
				this.labelCustIDName.setValue(details.getCustShrtName());
			}
		}

		if (this.custID.getValue() != null) {
			CaluculateSummary();
		}

	}

	public void onClick$btnSearchCmtBranch(Event event) {

		Object dataObject = ExtendedSearchListBox.show(this.window_CommitmentDialog, "Branch");
		if (dataObject instanceof String) {
			this.cmtBranch.setValue(dataObject.toString());
			this.cmtBranchName.setValue("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.cmtBranch.setValue(details.getBranchCode());
				this.cmtBranchName.setValue(details.getBranchCode() + "-" + details.getBranchDesc());
			}
		}
	}

	public void onClick$btnSearchCmtCcy(Event event) {

		Object dataObject = ExtendedSearchListBox.show(this.window_CommitmentDialog, "Currency");
		if (dataObject instanceof String) {
			this.cmtCcy.setValue(dataObject.toString());
			this.cmtCcyName.setValue("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.cmtCcy.setValue(details.getCcyCode());
				this.cmtCcyName.setValue(details.getCcyCode() + "-" + details.getCcyDesc());
				// To Format Amount based on the currency
				defaultCCYDecPos = details.getCcyEditField();
				setFormatByCCy(this.cmtAmount, defaultCCYDecPos);
				setFormatByCCy(this.cmtUtilizedAmount, defaultCCYDecPos);
				setFormatByCCy(this.cmtAvailable, defaultCCYDecPos);
				setFormatByCCy(this.cmtCharges, defaultCCYDecPos);
				setFormatByCCy(this.calCmtCharges, defaultCCYDecPos);
			}
			if (this.custID.getValue() != null) {
				CaluculateSummary();
			}
		}
	}

	/*
	 * public void onClick$btnSearchCmtAccount(Event event){
	 * 
	 * String sCmtAccount= this.cmtAccount.getValue();
	 * 
	 * Object dataObject =
	 * ExtendedSearchListBox.show(this.window_CommitmentDialog,"Account"); if
	 * (dataObject instanceof String){
	 * this.cmtAccount.setValue(dataObject.toString());
	 * this.cmtAccountName.setValue(""); }else{ Account details= (Account)
	 * dataObject; if (details != null) {
	 * this.cmtAccount.setValue(details.getCcyCode());
	 * this.cmtAccountName.setValue(details.getCcyDesc()); } } }
	 */

	/*
	 * public void onClick$btnSearchCmtChargesAccount(Event event){
	 * 
	 * String sCmtChargesAccount= this.cmtChargesAccount.getValue();
	 * 
	 * Object dataObject =
	 * ExtendedSearchListBox.show(this.window_CommitmentDialog
	 * ,"ChargesAccount"); if (dataObject instanceof String){
	 * this.cmtChargesAccount.setValue(dataObject.toString());
	 * this.cmtChargesAccountName.setValue(""); }else{ ChargesAccount details=
	 * (ChargesAccount) dataObject; if (details != null) {
	 * this.cmtChargesAccount.setValue(details.getBranchCode());
	 * this.cmtChargesAccountName.setValue(details.getBranchDesc()); } } }
	 */

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommitment
	 * @throws InterruptedException
	 */
	public void doShowDialog(Commitment aCommitment) throws InterruptedException {
		logger.debug("Entering");

		// if aCommitment == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCommitment == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCommitment = getCommitmentService().getNewCommitment();

			setCommitment(aCommitment);
		} else {
			setCommitment(aCommitment);
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aCommitment);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aCommitment.isNewRecord()));

			doStoreInitValues();
			if (!enqModule) {
				doDesignByMode();
			}else{
				this.south.setVisible(false);
			}
			doCheckEnq();
			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_CommitmentDialog);
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

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(), this.userAction, this.cmtReference, this.custID));

		if (getCommitment().isNewRecord()) {
			setComponentAccessType("CommitmentDialog_CmtReference", false, this.cmtReference, this.space_CmtReference, this.label_CmtReference, this.hlayout_CmtReference, null);
		} else {
			this.cmtReference.setReadonly(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;

		if (readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(commitment.getRecordType())))) {
			tempReadOnly = true;
		}

		//setComponentAccessType("CommitmentDialog_CmtReference", true, this.cmtReference, this.space_CmtReference, this.label_CmtReference, this.hlayout_CmtReference, this.row0);
		setComponentAccessType("CommitmentDialog_CmtTitle", tempReadOnly, this.cmtTitle, this.space_CmtTitle, this.label_CmtTitle, this.hlayout_CmtTitle, null);
		setRowInvisible(this.row0, this.hlayout_CmtReference, this.hlayout_CmtTitle);
		setLovAccess("CommitmentDialog_custID", tempReadOnly, this.btnSearchcustID, this.space_custID, this.label_custID, this.hlayout_custID, null);
		setLovAccess("CommitmentDialog_CmtBranch", tempReadOnly, this.btnSearchCmtBranch, this.space_CmtBranch, this.label_CmtBranch, this.hlayout_CmtBranch, null);
		setRowInvisible(this.row1, this.hlayout_custID, this.hlayout_CmtBranch);
		setLovAccess("CommitmentDialog_CmtCcy", tempReadOnly, this.btnSearchCmtCcy, this.space_CmtCcy, this.label_CmtCcy, this.hlayout_CmtCcy, null);
		setRowInvisible(this.row2, this.hlayout_CmtCcy, null);
		setComponentAccessType("CommitmentDialog_OpenAccount", tempReadOnly, this.openAccount, this.space_OpenAccount, this.label_OpenAccount, this.hlayout_OpenAccount, null);
		setLovAccess("CommitmentDialog_CmtAccount", tempReadOnly, this.btnSearchCmtAccount, this.space_CmtAccount, this.label_CmtAccount, this.hlayout_CmtAccount, null);
		setRowInvisible(this.row3, this.hlayout_OpenAccount, hlayout_CmtAccount);
		setComponentAccessType("CommitmentDialog_CmtPftRateMin", tempReadOnly, this.cmtPftRateMin, null, this.label_CmtPftRateMin, this.hlayout_CmtPftRateMin, null);
		setComponentAccessType("CommitmentDialog_CmtPftRateMax", tempReadOnly, this.cmtPftRateMax, null, this.label_CmtPftRateMax, this.hlayout_CmtPftRateMax, null);
		setRowInvisible(this.row4, this.hlayout_CmtPftRateMin, this.hlayout_CmtPftRateMax);
		/*
		 * setComponentAccessType("CommitmentDialog_CmtAvailable", tempReadOnly,
		 * this.cmtAvailable, null, this.label_CmtAvailable,
		 * this.hlayout_CmtAvailable,null);
		 * setComponentAccessType("CommitmentDialog_CmtUtilizedAmount",
		 * tempReadOnly, this.cmtUtilizedAmount, null,
		 * this.label_CmtUtilizedAmount, this.hlayout_CmtUtilizedAmount,null);
		 * setRowInvisible(this.row5,
		 * this.hlayout_CmtAvailable,this.hlayout_CmtUtilizedAmount);
		 */
		setComponentAccessType("CommitmentDialog_CmtAmount", tempReadOnly, this.cmtAmount, this.space_CmtAmount, this.label_CmtAmount, this.hlayout_CmtAmount, null);
		setComponentAccessType("CommitmentDialog_CmtPromisedDate", tempReadOnly, this.cmtPromisedDate, null, this.label_CmtPromisedDate, this.hlayout_CmtPromisedDate, null);
		setRowInvisible(this.row6, this.hlayout_CmtAmount, this.hlayout_CmtPromisedDate);
		setComponentAccessType("CommitmentDialog_CmtStartDate", tempReadOnly, this.cmtStartDate, null, this.label_CmtStartDate, this.hlayout_CmtStartDate, null);
		setComponentAccessType("CommitmentDialog_CmtExpDate", tempReadOnly, this.cmtExpDate, null, this.label_CmtExpDate, this.hlayout_CmtExpDate, null);
		setRowInvisible(this.row7, this.hlayout_CmtStartDate, this.hlayout_CmtExpDate);
		setRowInvisible(this.row8, this.hlayout_CmtCharges, this.hlayout_CmtChargesAccount);
		setRowInvisible(this.row9, this.hlayout_CmtActiveStatus, this.hlayout_CmtNonperformingStatus);
		setComponentAccessType("CommitmentDialog_Revolving", tempReadOnly, this.revolving, null, this.label_Revolving, this.hlayout_Revolving, null);
		setComponentAccessType("CommitmentDialog_MultiBranch", tempReadOnly, this.multiBranch, null, this.label_MultiBranch, this.hlayout_MultiBranch, null);
		setRowInvisible(this.row10, this.hlayout_Revolving, this.hlayout_MultiBranch);
		setComponentAccessType("CommitmentDialog_SharedCmt", tempReadOnly, this.sharedCmt, null, this.label_SharedCmt, this.hlayout_SharedCmt, null);
		setRowInvisible(this.row11, this.hlayout_SharedCmt, null);
		setComponentAccessType("CommitmentDialog_CmtNotes", tempReadOnly, this.cmtNotes, null, this.label_CmtNotes, this.hlayout_CmtNotes, null);
		setRowInvisible(this.row12, this.hlayout_CmtNotes, null);

		setComponentAccessType("CommitmentDialog_CmtCharges", tempReadOnly, this.cmtCharges, null, this.label_CmtCharges, this.hlayout_CmtCharges, null);
		setLovAccess("CommitmentDialog_CmtChargesAccount", tempReadOnly, this.btnSearchCmtChargesAccount, null, this.label_CmtChargesAccount, this.hlayout_CmtChargesAccount, null);
		setComponentAccessType("CommitmentDialog_CmtActiveStatus", tempReadOnly, this.cmtActiveStatus, null, this.label_CmtActiveStatus, this.hlayout_CmtActiveStatus, null);
		setComponentAccessType("CommitmentDialog_CmtNonperformingStatus", tempReadOnly, this.cmtNonperformingStatus, null, this.label_CmtNonperformingStatus,
		        this.hlayout_CmtNonperformingStatus, null);

		if (isReadOnly("CommitmentDialog_CmtStopRateRange")) {
			this.cmtStopRateRange.setDisabled(true);
		} else {
			this.cmtStopRateRange.setDisabled(false);
		}
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
		getUserWorkspace().alocateAuthorities("CommitmentDialog");
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnSave"));
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
		// Empty sent any required attributes
		this.cmtReference.setMaxlength(20);
		this.custID.setMaxlength(19);
		this.cmtBranch.setMaxlength(8);
		this.cmtAccount.setMaxlength(20);
		this.cmtCcy.setMaxlength(3);
		this.cmtPftRateMin.setMaxlength(13);
		this.cmtPftRateMin.setFormat(PennantConstants.rateFormate9);
		this.cmtPftRateMin.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtPftRateMin.setScale(9);
		this.cmtPftRateMax.setMaxlength(13);
		this.cmtPftRateMax.setFormat(PennantConstants.rateFormate9);
		this.cmtPftRateMax.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtPftRateMax.setScale(9);
		this.cmtAmount.setMaxlength(18);
		this.cmtAmount.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtAmount.setScale(defaultCCYDecPos);
		this.cmtUtilizedAmount.setMaxlength(18);
		this.cmtUtilizedAmount.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtUtilizedAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtUtilizedAmount.setScale(defaultCCYDecPos);
		this.cmtAvailable.setMaxlength(18);
		this.cmtAvailable.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtAvailable.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtAvailable.setScale(defaultCCYDecPos);
		this.cmtPromisedDate.setFormat(PennantConstants.dateFormat);
		this.cmtStartDate.setFormat(PennantConstants.dateFormat);
		this.cmtExpDate.setFormat(PennantConstants.dateFormat);
		this.cmtCharges.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtCharges.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtCharges.setScale(defaultCCYDecPos);
		this.cmtTitle.setMaxlength(50);
		this.cmtNotes.setMaxlength(500);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_CmtReference = this.cmtReference.getValue();
		this.oldVar_custID = this.custID.getValue();
		this.oldVar_custIDName = this.custIDName.getValue();
		this.oldVar_CmtBranch = this.cmtBranch.getValue();
		this.oldVar_CmtBranchName = this.cmtBranchName.getValue();
		this.oldVar_OpenAccount = this.openAccount.isChecked();
		this.oldVar_CmtAccount = this.cmtAccount.getValue();
		this.oldVar_cmtAccountName = this.cmtAccountName.getValue();
		this.oldVar_CmtCcy = this.cmtCcy.getValue();
		this.oldVar_CmtCcyName = this.cmtCcyName.getValue();
		this.oldVar_CmtPftRateMin = this.cmtPftRateMin.doubleValue();
		this.oldVar_CmtPftRateMax = this.cmtPftRateMax.doubleValue();
		this.oldVar_CmtAmount = this.cmtAmount.doubleValue();
		this.oldVar_CmtUtilizedAmount = this.cmtUtilizedAmount.doubleValue();
		this.oldVar_CmtAvailable = this.cmtAvailable.doubleValue();
		this.oldVar_CmtPromisedDate = this.cmtPromisedDate.getValue();
		this.oldVar_CmtStartDate = this.cmtStartDate.getValue();
		this.oldVar_CmtExpDate = this.cmtExpDate.getValue();
		this.oldVar_CmtCharges = this.cmtCharges.doubleValue();
		this.oldVar_cmtChargesAccountName = this.cmtChargesAccountName.getValue();
		this.oldVar_cmtActiveStatus = this.cmtActiveStatus.isChecked();
		this.oldVar_cmtNonperformingStatus = this.cmtNonperformingStatus.isChecked();
		this.oldVar_CmtTitle = this.cmtTitle.getValue();
		this.oldVar_CmtNotes = this.cmtNotes.getValue();
		this.oldVar_Revolving = this.revolving.isChecked();
		this.oldVar_SharedCmt = this.sharedCmt.isChecked();
		this.oldVar_MultiBranch = this.multiBranch.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.cmtReference.setValue(this.oldVar_CmtReference);
		this.custID.setValue(this.oldVar_custID);
		this.custIDName.setValue(this.oldVar_custIDName);
		this.cmtBranch.setValue(this.oldVar_CmtBranch);
		this.cmtBranchName.setValue(this.oldVar_CmtBranchName);
		this.openAccount.setChecked(this.oldVar_OpenAccount);
		this.cmtAccount.setValue(this.oldVar_CmtAccount);
		this.cmtAccountName.setValue(this.oldVar_cmtAccountName);
		this.cmtCcy.setValue(this.oldVar_CmtCcy);
		this.cmtCcyName.setValue(this.oldVar_CmtCcyName);
		this.cmtPftRateMin.setValue(new BigDecimal(this.oldVar_CmtPftRateMin));
		this.cmtPftRateMax.setValue(new BigDecimal(this.oldVar_CmtPftRateMax));
		this.cmtAmount.setValue(new BigDecimal(this.oldVar_CmtAmount));
		this.cmtUtilizedAmount.setValue(new BigDecimal(this.oldVar_CmtUtilizedAmount));
		this.cmtAvailable.setValue(new BigDecimal(this.oldVar_CmtAvailable));
		this.cmtPromisedDate.setValue(this.oldVar_CmtPromisedDate);
		this.cmtStartDate.setValue(this.oldVar_CmtStartDate);
		this.cmtExpDate.setValue(this.oldVar_CmtExpDate);
		this.cmtCharges.setValue(new BigDecimal(this.oldVar_CmtCharges));
		this.cmtChargesAccount.setValue(this.oldVar_cmtChargesAccount);
		this.cmtChargesAccountName.setValue(this.oldVar_cmtChargesAccountName);
		this.cmtActiveStatus.setChecked(this.oldVar_cmtActiveStatus);
		this.cmtNonperformingStatus.setChecked(this.oldVar_cmtNonperformingStatus);
		this.cmtTitle.setValue(this.oldVar_CmtTitle);
		this.cmtNotes.setValue(this.oldVar_CmtNotes);
		this.revolving.setChecked(this.oldVar_Revolving);
		this.sharedCmt.setChecked(this.oldVar_SharedCmt);
		this.multiBranch.setChecked(this.oldVar_MultiBranch);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled() & !enqModule) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCommitment
	 *            Commitment
	 */
	public void doWriteBeanToComponents(Commitment aCommitment) {
		logger.debug("Entering");

		this.cmtReference.setValue(aCommitment.getCmtReference());
		this.custID.setValue(aCommitment.getCustID());
		this.custIDName.setValue(aCommitment.getCustCIF());
		this.labelCustIDName.setValue(aCommitment.getCustShrtName());

		this.cmtBranch.setValue(aCommitment.getCmtBranch());
		if (!StringUtils.trimToEmpty(aCommitment.getCmtBranch()).equals("")) {
			this.cmtBranchName.setValue(aCommitment.getCmtBranch() + "-" + aCommitment.getBranchDesc());
		}
		this.cmtCcy.setValue(aCommitment.getCmtCcy());
		if (!StringUtils.trimToEmpty(aCommitment.getCmtCcy()).equals("")) {
			this.cmtCcyName.setValue(aCommitment.getCmtCcy() + "-" + aCommitment.getCcyDesc());
		}
		this.cmtAccount.setValue(aCommitment.getCmtAccount());
		if (!StringUtils.trimToEmpty(aCommitment.getCmtAccount()).equals("")) {
			this.cmtAccountName.setValue(aCommitment.getCmtAccount());
		}
		this.cmtChargesAccount.setValue(aCommitment.getChargesAccount());

		if (!StringUtils.trimToEmpty(aCommitment.getChargesAccount()).equals("")) {
			this.cmtChargesAccountName.setValue(aCommitment.getChargesAccount());
		}
		if (aCommitment.getCmtCharges() != null) {
			this.cmtCharges.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtCharges(), defaultCCYDecPos));
			this.calCmtCharges.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtCharges(), defaultCCYDecPos));
		} else {
			this.cmtCharges.setValue(aCommitment.getCmtCharges());

		}
		this.openAccount.setChecked(aCommitment.isOpenAccount());
	

		this.cmtPftRateMin.setValue(aCommitment.getCmtPftRateMin());
		this.cmtPftRateMax.setValue(aCommitment.getCmtPftRateMax());

		this.cmtAmount.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtAmount(), defaultCCYDecPos));
		this.cmtUtilizedAmount.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtUtilizedAmount(), defaultCCYDecPos));
		this.cmtAvailable.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtAvailable(), defaultCCYDecPos));

		this.cmtStartDate.setValue(aCommitment.getCmtStartDate());
		this.cmtExpDate.setValue(aCommitment.getCmtExpDate());

		this.cmtActiveStatus.setChecked(aCommitment.isActiveStatus());
		this.cmtNonperformingStatus.setChecked(aCommitment.isNonperformingStatus());
		this.cmtTitle.setValue(aCommitment.getCmtTitle());
		this.cmtNotes.setValue(aCommitment.getCmtNotes());
		this.revolving.setChecked(aCommitment.isRevolving());
		this.sharedCmt.setChecked(aCommitment.isSharedCmt());
		this.multiBranch.setChecked(aCommitment.isMultiBranch());
		this.cmtStopRateRange.setChecked(aCommitment.isCmtStopRateRange());

		if (aCommitment.isNewRecord()) {
			this.custIDName.setValue("");
			this.cmtBranchName.setValue("");
			this.cmtCcyName.setValue("");
			this.cmtPromisedDate.setValue(dateAppDate);

			this.cmtCommitments.setValue("0");
			this.cmtTotAmount.setValue("0");
			this.cmtUtilizedTotAmount.setValue("0");
			this.cmtUnUtilizedAmount.setValue("0");
		} else {
			this.cmtPromisedDate.setValue(aCommitment.getCmtPromisedDate());
		}
		if (this.custID.getValue() != null && !maintain) {
			CaluculateSummary();
		} else {
			this.cmtTotAmount.setValue(PennantApplicationUtil.amountFormate(aCommitment.getCmtAmount(), defaultCCYDecPos));
			this.cmtUtilizedTotAmount.setValue(PennantApplicationUtil.amountFormate(aCommitment.getCmtUtilizedAmount(), defaultCCYDecPos));
			this.cmtUnUtilizedAmount.setValue(PennantApplicationUtil.amountFormate(aCommitment.getCmtAvailable(), defaultCCYDecPos));
		}

		this.recordStatus.setValue(aCommitment.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aCommitment.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommitment
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(Commitment aCommitment) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Cmt Reference
		try {
			aCommitment.setCmtReference(this.cmtReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// cust ID
		try {
			aCommitment.setCustShrtName(this.custIDName.getValue());
			aCommitment.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Branch
		try {
			aCommitment.setBranchDesc(this.cmtBranchName.getValue());
			aCommitment.setCmtBranch(this.cmtBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Open Account
		try {
			aCommitment.setOpenAccount(this.openAccount.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Account
		try {
			aCommitment.setCmtAccountName(this.cmtAccountName.getValue());
			aCommitment.setCmtAccount(this.cmtAccount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Ccy
		try {
			aCommitment.setCcyDesc(this.cmtCcyName.getValue());
			aCommitment.setCmtCcy(this.cmtCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Pft Rate Min
		try {
			if (this.cmtPftRateMin.getValue() != null) {
				aCommitment.setCmtPftRateMin(this.cmtPftRateMin.getValue());
			} else {
				aCommitment.setCmtPftRateMin(BigDecimal.ZERO);
			}
			if (this.cmtPftRateMax.getValue() != null) {
				aCommitment.setCmtPftRateMax(this.cmtPftRateMax.getValue());
			} else {
				aCommitment.setCmtPftRateMax(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Pft Rate Max
		try {

			if (aCommitment.getCmtPftRateMin().compareTo(BigDecimal.ZERO) != 0 && aCommitment.getCmtPftRateMax().compareTo(BigDecimal.ZERO) != 0) {
				if (aCommitment.getCmtPftRateMax().compareTo(aCommitment.getCmtPftRateMin()) < 0) {

					throw new WrongValueException(this.cmtPftRateMax, Labels.getLabel("RATE_RANGE", new String[] { Labels.getLabel("label_CommitmentDialog_CmtPftRateMin.value"),
					        String.valueOf(this.cmtPftRateMax.getValue()), String.valueOf(this.cmtPftRateMin.getValue()) }));
				}

			}
		} catch (WrongValueException we) {

			wve.add(we);
		}
		// Cmt Amount
		try {
			if (maintain) {
				//New Commitment amount cannot be less than Utilized Amount
				if (this.cmtAmount.getValue().compareTo(this.cmtUtilizedAmount.getValue()) < 0) {
					throw new WrongValueException(this.cmtAmount, Labels.getLabel("AMOUNT_NO_LESS", new String[] { Labels.getLabel("label_CommitmentDialog_CmtAmount.value"),
					        Labels.getLabel("label_CommitmentDialog_CmtUtilizedAmount.value") }));
				}
			}
			if (this.cmtAmount.getValue() != null) {
				aCommitment.setCmtAmount(PennantApplicationUtil.unFormateAmount(this.cmtAmount.getValue(), defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Utilized Amount
		try {
			if (this.cmtUtilizedAmount.getValue() != null) {
				aCommitment.setCmtUtilizedAmount(PennantApplicationUtil.unFormateAmount(this.cmtUtilizedAmount.getValue(), defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Available
		try {
			if (this.cmtAvailable.getValue() != null) {
				aCommitment.setCmtAvailable(PennantApplicationUtil.unFormateAmount(this.cmtAvailable.getValue(), defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Promised Date
		try {
		
			if (this.cmtPromisedDate.getValue() != null) {
				aCommitment.setCmtPromisedDate(this.cmtPromisedDate.getValue());
			} else {
				aCommitment.setCmtPromisedDate(dateAppDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Cmt Start Date
		try {
			if (this.cmtStartDate.getValue() != null) {
				if (this.cmtStartDate.getValue().before(this.cmtPromisedDate.getValue())) {
					throw new WrongValueException(cmtStartDate, Labels.getLabel(
					        "DATE_ALLOWED_AFTER",
					        new String[] { Labels.getLabel("label_CommitmentDialog_CmtStartDate.value"),
					                DateUtility.formatUtilDate(this.cmtPromisedDate.getValue(), PennantConstants.dateFormat),
					                DateUtility.formatUtilDate(this.cmtStartDate.getValue(), PennantConstants.dateFormat) }));
				}
				aCommitment.setCmtStartDate(this.cmtStartDate.getValue());
			} else {
				if (aCommitment.getCmtPromisedDate() != null && aCommitment.getCmtPromisedDate().compareTo(dateAppDate) >= 0) {
					aCommitment.setCmtStartDate(aCommitment.getCmtPromisedDate());
				} else {
					aCommitment.setCmtStartDate(dateAppDate);

				}

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Cmt Exp Date
		try {
			if (this.cmtExpDate.getValue() != null) {

				if (this.cmtExpDate.getValue().before(this.cmtStartDate.getValue())) {
					throw new WrongValueException(cmtExpDate, Labels.getLabel(
					        "DATE_ALLOWED_AFTER",
					        new String[] { Labels.getLabel("label_CommitmentDialog_CmtExpDate.value"),
					                DateUtility.formatUtilDate(this.cmtStartDate.getValue(), PennantConstants.dateFormat),
					                DateUtility.formatUtilDate(this.cmtExpDate.getValue(), PennantConstants.dateFormat) }));
				}
				aCommitment.setCmtExpDate(this.cmtExpDate.getValue());
			} else {
				aCommitment.setCmtExpDate(PennantConstants.MAXIMUM_DATE);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Charges
		try {
			this.cmtCharges.getValue();
			aCommitment.setCmtCharges(PennantApplicationUtil.unFormateAmount(this.calCmtCharges.getValue(), defaultCCYDecPos));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Cmt Branch
		try {
			if (this.calCmtCharges.getValue() != null && this.calCmtCharges.getValue().compareTo(BigDecimal.ZERO) != 0
			        && StringUtils.trimToEmpty(this.cmtChargesAccount.getValue()).equals("")) {
				throw new WrongValueException(cmtChargesAccountName, Labels.getLabel("FIELD_NO_EMPTY",
				        new String[] { Labels.getLabel("label_CommitmentDialog_CmtChargesAccount.value") }));
			}

			aCommitment.setChargesAccountName(this.cmtChargesAccountName.getValue());
			aCommitment.setChargesAccount(this.cmtChargesAccount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// cmt ActiveStatus
		try {
			aCommitment.setActiveStatus(this.cmtActiveStatus.isChecked());
			aCommitment.setCmtStopRateRange(this.cmtStopRateRange.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// cmt NonperformingStatus
		try {
			aCommitment.setNonperformingStatus(this.cmtNonperformingStatus.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Title
		try {
			aCommitment.setCmtTitle(this.cmtTitle.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cmt Notes
		try {
			aCommitment.setCmtNotes(this.cmtNotes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Revolving
		try {
			aCommitment.setRevolving(this.revolving.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Shared Cmt
		try {
			aCommitment.setSharedCmt(this.sharedCmt.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Multi Branch
		try {
			aCommitment.setMultiBranch(this.multiBranch.isChecked());
			if (wve.size() == 0) {
				aCommitment.setCmtUtilizedAmount(BigDecimal.ZERO);
				aCommitment.setCmtAvailable(aCommitment.getCmtAmount().subtract(aCommitment.getCmtUtilizedAmount()));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (wve.size() == 0) {
				aCommitment.getCommitmentMovement().setCmtReference(this.cmtReference.getValue().toString());
				aCommitment.getCommitmentMovement().setFinReference("");
				aCommitment.getCommitmentMovement().setFinBranch("");
				aCommitment.getCommitmentMovement().setFinType("");
				aCommitment.getCommitmentMovement().setMovementDate(dateAppDate);
				aCommitment.getCommitmentMovement().setCmtAmount(aCommitment.getCmtAmount());
				aCommitment.getCommitmentMovement().setCmtCharges(aCommitment.getCmtCharges());
				aCommitment.getCommitmentMovement().setCmtUtilizedAmount(aCommitment.getCmtUtilizedAmount());
				aCommitment.getCommitmentMovement().setCmtAvailable(aCommitment.getCmtAvailable());
				if (newMaintain) {
					// get previous commitment amount
					aCommitment.getCommitmentMovement().setMovementAmount(aCommitment.getCmtAmount().subtract(oldCmtAmount));
				} else {
					if (aCommitment.isNewRecord()) {
						aCommitment.getCommitmentMovement().setMovementAmount(aCommitment.getCmtAmount());
					}
				}
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

	public void onCheck$openAccount(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (this.openAccount.isChecked()) {
			if (StringUtils.trimToEmpty(this.custIDName.getValue()).equals("") || StringUtils.trimToEmpty(this.cmtBranch.getValue()).equals("")
			        || StringUtils.trimToEmpty(this.cmtCcy.getValue()).equals("")) {
				this.openAccount.setChecked(false);
			} else {
				this.space_CmtAccount.setSclass("");
				btnSearchCmtAccount.setVisible(false);
				IAccounts accounts = getAccNumber();
				if (accounts != null) {
					this.cmtAccount.setValue(accounts.getAccountId());
					this.cmtAccountName.setValue(accounts.getAccountId());
				}

			}
		} else {
			btnSearchCmtAccount.setVisible(true);
			this.space_CmtAccount.setSclass("mandatory");
			this.cmtAccount.setValue("");
			this.cmtAccountName.setValue("");
		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * public void onSelect$btnSearchcustID(Event event) throws
	 * InterruptedException { logger.debug("Entering" + event.toString());
	 * if(this.custID.getValue() != null){
	 * this.cmtCommitments.setValue(getCommitmentDAO
	 * ().getCmtAmountCount(this.custID.getValue()));
	 * 
	 * } logger.debug("Leaving" + event.toString()); }
	 */

	/*
	 * //Cmt Commitment count try {
	 * getCommitmentDAO().getCmtAmountCount(this.custID.getValue());
	 * aCommitment.setCmtPromisedDate(this.cmtPromisedDate.getValue()); }catch
	 * (WrongValueException we ) { wve.add(we); }
	 */

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

		if (!StringUtils.trimToEmpty(this.oldVar_CmtReference).equals(StringUtils.trimToEmpty(this.cmtReference.getValue()))) {
			return true;
		}

		/*
		 * if (!StringUtils.trimToEmpty(this.oldVar_custID).equals(StringUtils.
		 * trimToEmpty(this.custID.getValue()))) { return true; }
		 */

		if (this.oldVar_custID != this.custID.getValue()) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_CmtBranch).equals(StringUtils.trimToEmpty(this.cmtBranch.getValue()))) {
			return true;
		}
		if (this.oldVar_OpenAccount != this.openAccount.isChecked()) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_CmtAccount).equals(StringUtils.trimToEmpty(this.cmtAccount.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_CmtCcy).equals(StringUtils.trimToEmpty(this.cmtCcy.getValue()))) {
			return true;
		}
		if (this.oldVar_CmtPftRateMin != this.cmtPftRateMin.doubleValue()) {
			return true;
		}
		if (this.oldVar_CmtPftRateMax != this.cmtPftRateMax.doubleValue()) {
			return true;
		}
		if (this.oldVar_CmtAmount != this.cmtAmount.doubleValue()) {
			return true;
		}
		if (this.oldVar_CmtUtilizedAmount != this.cmtUtilizedAmount.doubleValue()) {
			return true;
		}
		if (this.oldVar_CmtAvailable != this.cmtAvailable.doubleValue()) {
			return true;
		}
		String old_CmtPromisedDate = "";
		String new_CmtPromisedDate = "";
		if (this.oldVar_CmtPromisedDate != null) {
			old_CmtPromisedDate = DateUtility.formatDate(this.oldVar_CmtPromisedDate, PennantConstants.dateFormat);
		}
		if (this.cmtPromisedDate.getValue() != null) {
			new_CmtPromisedDate = DateUtility.formatDate(this.cmtPromisedDate.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_CmtPromisedDate).equals(StringUtils.trimToEmpty(new_CmtPromisedDate))) {
			return true;
		}
		String old_CmtStartDate = "";
		String new_CmtStartDate = "";
		if (this.oldVar_CmtStartDate != null) {
			old_CmtStartDate = DateUtility.formatDate(this.oldVar_CmtStartDate, PennantConstants.dateFormat);
		}
		if (this.cmtStartDate.getValue() != null) {
			new_CmtStartDate = DateUtility.formatDate(this.cmtStartDate.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_CmtStartDate).equals(StringUtils.trimToEmpty(new_CmtStartDate))) {
			return true;
		}
		String old_CmtExpDate = "";
		String new_CmtExpDate = "";
		if (this.oldVar_CmtExpDate != null) {
			old_CmtExpDate = DateUtility.formatDate(this.oldVar_CmtExpDate, PennantConstants.dateFormat);
		}
		if (this.cmtExpDate.getValue() != null) {
			new_CmtExpDate = DateUtility.formatDate(this.cmtExpDate.getValue(), PennantConstants.dateFormat);
		}
		if (this.oldVar_CmtCharges != this.cmtCharges.doubleValue()) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_cmtChargesAccount).equals(StringUtils.trimToEmpty(this.cmtChargesAccount.getValue()))) {
			return true;
		}
		if (this.oldVar_cmtActiveStatus != this.cmtActiveStatus.isChecked()) {
			return true;
		}
		if (this.oldVar_cmtNonperformingStatus != this.cmtNonperformingStatus.isChecked()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(old_CmtExpDate).equals(StringUtils.trimToEmpty(new_CmtExpDate))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_CmtTitle).equals(StringUtils.trimToEmpty(this.cmtTitle.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_CmtNotes).equals(StringUtils.trimToEmpty(this.cmtNotes.getValue()))) {
			return true;
		}
		if (this.oldVar_Revolving != this.revolving.isChecked()) {
			return true;
		}
		if (this.oldVar_SharedCmt != this.sharedCmt.isChecked()) {
			return true;
		}
		if (this.oldVar_MultiBranch != this.multiBranch.isChecked()) {
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
		// Cmt Reference
		if (!this.cmtReference.isReadonly()) {
			this.cmtReference.setConstraint(new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtReference.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));

		}
		// Cmt Title
		if (!this.cmtTitle.isReadonly()) {
			this.cmtTitle.setConstraint(new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtTitle.value"), PennantRegularExpressions.REGEX_NAME, true));
		}

		// Cmt Pft Rate Min
		if (!this.cmtPftRateMin.isReadonly()) {
			this.cmtPftRateMin.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtPftRateMin.value"), 9, false, false, 0));
		}
		// Cmt Pft Rate Max
		if (!this.cmtPftRateMax.isReadonly()) {
			this.cmtPftRateMax.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtPftRateMax.value"), 9, false, false, 0));
		}
		// Cmt Amount
		if (!this.cmtAmount.isReadonly()) {
			this.cmtAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtAmount.value"), defaultCCYDecPos, true, false, 0));
		}
		// Cmt Utilized Amount
		if (!this.cmtUtilizedAmount.isReadonly()) {
			this.cmtUtilizedAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtUtilizedAmount.value"), defaultCCYDecPos, false, false, 0));
		}
		// Cmt Available
		if (!this.cmtAvailable.isReadonly()) {
			this.cmtAvailable.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtAvailable.value"), defaultCCYDecPos, false, false, 0));
		}
		// Cmt Promised Date
		if (!this.cmtPromisedDate.isReadonly() && !this.cmtPromisedDate.isDisabled()) {
			this.cmtPromisedDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CommitmentDialog_CmtPromisedDate.value"), false, dateAppDate,null, true));
		}
		// Cmt Start Date
		if (!this.cmtStartDate.isReadonly() && !this.cmtStartDate.isDisabled()) {
			this.cmtStartDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CommitmentDialog_CmtStartDate.value"), false, dateAppDate,null, true));
		}
		// Cmt Exp Date
		if (!this.cmtExpDate.isReadonly()) {
			this.cmtExpDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CommitmentDialog_CmtExpDate.value"), false, false, null, true));
		}

		// Cmt Charges
		if (!this.cmtCharges.isReadonly()) {
			this.cmtCharges.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtCharges.value"), defaultCCYDecPos, false, false, 0));
		}

		// Cmt Notes
		if (!this.cmtNotes.isReadonly()) {
			this.cmtNotes.setConstraint(new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtNotes.value"), PennantRegularExpressions.REGEX_NAME, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.cmtReference.setConstraint("");
		this.cmtPftRateMin.setConstraint("");
		this.cmtPftRateMax.setConstraint("");
		this.cmtAmount.setConstraint("");
		this.cmtUtilizedAmount.setConstraint("");
		this.cmtAvailable.setConstraint("");
		this.cmtPromisedDate.setConstraint("");
		this.cmtStartDate.setConstraint("");
		this.cmtExpDate.setConstraint("");
		this.cmtCharges.setConstraint("");
		this.cmtTitle.setConstraint("");
		this.cmtNotes.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		// cust ID
		if (btnSearchcustID.isVisible()) {
			this.custIDName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CommitmentDialog_custID.value") }));
		}
		// Cmt Branch
		if (btnSearchCmtBranch.isVisible()) {
			this.cmtBranchName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CommitmentDialog_CmtBranch.value") }));
		}
		// Cmt Ccy
		if (btnSearchCmtCcy.isVisible()) {
			this.cmtCcyName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CommitmentDialog_CmtCcy.value") }));
		}
		// Cmt Account
		if (openAccount.isChecked() == false) {
			if (btnSearchCmtAccount.isVisible()) {
				this.cmtAccountName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CommitmentDialog_CmtAccount.value") }));
			}
		}
		//Cmt Branch 
		if (btnSearchCmtChargesAccount.isVisible() && this.cmtCharges.getValue() != null && this.cmtCharges.getValue().compareTo(BigDecimal.ZERO) != 0) {
			this.cmtChargesAccountName.setConstraint("NO EMPTY:"
			        + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CommitmentDialog_CmtChargesAccount.value") }));
		}

	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.custIDName.setConstraint("");
		this.cmtBranchName.setConstraint("");
		this.cmtCcyName.setConstraint("");
		this.cmtAccountName.setConstraint("");
		this.cmtChargesAccountName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
		logger.debug("Entering");
		this.cmtReference.setErrorMessage("");
		this.cmtTitle.setErrorMessage("");
		this.custIDName.setErrorMessage("");
		this.cmtBranchName.setErrorMessage("");
		this.cmtAccountName.setErrorMessage("");
		this.cmtCcyName.setErrorMessage("");
		this.cmtAmount.setErrorMessage("");
		this.cmtChargesAccountName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList() {
		final JdbcSearchObject<Commitment> soCommitment = getCommitmentListCtrl().getSearchObj();
		getCommitmentListCtrl().pagingCommitmentList.setActivePage(0);
		getCommitmentListCtrl().getPagedListWrapper().setSearchObject(soCommitment);
		if (getCommitmentListCtrl().listBoxCommitment != null) {
			getCommitmentListCtrl().listBoxCommitment.getListModel();
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
			closeDialog(this.window_CommitmentDialog, "Commitment");
		}

		logger.debug("Leaving");
	}

	/**
	 * Deletes a Commitment object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Commitment aCommitment = new Commitment();
		BeanUtils.copyProperties(getCommitment(), aCommitment);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCommitment.getCmtReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCommitment.getRecordType()).equals("")) {
				aCommitment.setVersion(aCommitment.getVersion() + 1);
				aCommitment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aCommitment.getCommitmentMovement().setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCommitment.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aCommitment.getCommitmentMovement().setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aCommitment.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aCommitment.getNextTaskId(), aCommitment);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCommitment, tranType)) {
					refreshList();
					closeDialog(this.window_CommitmentDialog, "Commitment");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_CommitmentDialog, e);
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

		this.cmtReference.setValue("");
		this.custID.setText("");
		this.custIDName.setValue("");
		this.cmtBranch.setValue("");
		this.cmtBranchName.setValue("");
		this.openAccount.setChecked(false);
		this.cmtAccount.setValue("");
		this.cmtAccountName.setValue("");
		this.cmtCcy.setValue("");
		this.cmtCcyName.setValue("");
		this.cmtPftRateMin.setValue("");
		this.cmtPftRateMax.setValue("");
		this.cmtAmount.setValue("");
		this.cmtUtilizedAmount.setValue("");
		this.cmtAvailable.setValue("");
		this.cmtPromisedDate.setText("");
		this.cmtStartDate.setText("");
		this.cmtExpDate.setText("");
		this.cmtCharges.setValue("");
		this.cmtChargesAccount.setValue("");
		this.cmtChargesAccountName.setValue("");
		this.cmtActiveStatus.setChecked(false);
		this.cmtNonperformingStatus.setChecked(false);
		this.cmtTitle.setValue("");
		this.cmtNotes.setValue("");
		this.revolving.setChecked(false);
		this.sharedCmt.setChecked(false);
		this.multiBranch.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Commitment aCommitment = new Commitment();
		BeanUtils.copyProperties(getCommitment(), aCommitment);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aCommitment.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			aCommitment.getCommitmentMovement().setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aCommitment.getNextTaskId(), aCommitment);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aCommitment.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the Commitment object with the components data
			doWriteComponentsToBean(aCommitment);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCommitment.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCommitment.getRecordType()).equals("")) {
				aCommitment.setVersion(aCommitment.getVersion() + 1);
				if (isNew) {
					aCommitment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					aCommitment.getCommitmentMovement().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCommitment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCommitment.getCommitmentMovement().setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCommitment.setNewRecord(true);
				}
			}
		} else {
			aCommitment.setVersion(aCommitment.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;

			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCommitment, tranType)) {
				refreshList();
				closeDialog(this.window_CommitmentDialog, "Commitment");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_CommitmentDialog, e);
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

	private boolean doProcess(Commitment aCommitment, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aCommitment.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCommitment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommitment.setUserDetails(getUserWorkspace().getLoginUserDetails());

		aCommitment.getCommitmentMovement().setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCommitment.getCommitmentMovement().setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommitment.getCommitmentMovement().setUserDetails(getUserWorkspace().getLoginUserDetails());

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

			aCommitment.setTaskId(getTaskId());
			aCommitment.setNextTaskId(getNextTaskId());
			aCommitment.setRoleCode(getRole());
			aCommitment.setNextRoleCode(getNextRoleCode());

			aCommitment.getCommitmentMovement().setTaskId(getTaskId());
			aCommitment.getCommitmentMovement().setNextTaskId(getNextTaskId());
			aCommitment.getCommitmentMovement().setRoleCode(getRole());
			aCommitment.getCommitmentMovement().setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aCommitment, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aCommitment, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aCommitment, tranType), null);
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

		Commitment aCommitment = (Commitment) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getCommitmentService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCommitmentService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getCommitmentService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aCommitment.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getCommitmentService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aCommitment.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						// auditHeader.setErrorDetails(new
						// ErrorDetails(PennantConstants.ERR_9999,
						// Labels.getLabel("InvalidWorkFlowMethod"),
						// null,PennantConstants.ERR_SEV_ERROR));
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null, null));

						retValue = ErrorControl.showErrorControl(this.window_CommitmentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CommitmentDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes("Commitment", aCommitment.getCmtReference(), aCommitment.getVersion()), true);
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

	private AuditHeader getAuditHeader(Commitment aCommitment, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommitment.getBefImage(), aCommitment);
		return new AuditHeader(aCommitment.getCmtReference(), null, null, null, auditDetail, aCommitment.getUserDetails(), getOverideMap());
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

	public Commitment getCommitment() {
		return this.commitment;
	}

	public void setCommitment(Commitment commitment) {
		this.commitment = commitment;
	}

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public CommitmentService getCommitmentService() {
		return this.commitmentService;
	}

	public void setCommitmentListCtrl(CommitmentListCtrl commitmentListCtrl) {
		this.commitmentListCtrl = commitmentListCtrl;
	}

	public CommitmentListCtrl getCommitmentListCtrl() {
		return this.commitmentListCtrl;
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

	public CommitmentDAO getCommitmentDAO() {
		return commitmentDAO;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public AccountsService getAccountsService() {
		return accountsService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public void onClick$btnSearchCmtChargesAccount(Event event) {
		logger.debug("Entering " + event.toString());
		this.custIDName.clearErrorMessage();
		if (!StringUtils.trimToEmpty(this.custIDName.getValue()).equals("")) {
			Object dataObject;
			List<Accounts> accountList = new ArrayList<Accounts>();
			accountList = getAccountsService().getAccountsByAcPurpose("M");
			String acType = "";
			for (int i = 0; i < accountList.size(); i++) {
				acType = acType + accountList.get(i).getAcType();
			}
			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.cmtCcy.getValue());
			iAccount.setAcType(acType);
			iAccount.setAcCustCIF(this.custIDName.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_CommitmentDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.cmtChargesAccount.setValue(dataObject.toString());
					this.cmtChargesAccountName.setValue("");
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.cmtChargesAccount.setValue(details.getAccountId());
						this.cmtChargesAccountName.setValue(details.getAccountId() + "-" + details.getAcShortName());
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error"), Messagebox.ABORT, Messagebox.ERROR);
			}
		} else {
			throw new WrongValueException(this.custIDName, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnSearchCmtAccount(Event event) {
		logger.debug("Entering " + event.toString());
		this.custIDName.clearErrorMessage();
		if (!StringUtils.trimToEmpty(this.custIDName.getValue()).equals("")) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.cmtCcy.getValue());
			iAccount.setAcType(acType);
			iAccount.setAcCustCIF(this.custIDName.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_CommitmentDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.cmtAccount.setValue(dataObject.toString());
					this.cmtAccountName.setValue("");
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.cmtAccount.setValue(details.getAccountId());
						this.cmtAccountName.setValue(details.getAccountId() + "-" + details.getAcShortName());
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error"), Messagebox.ABORT, Messagebox.ERROR);
			}
		} else {
			throw new WrongValueException(this.custIDName, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	private CommitmentRuleData prepareDate() {
		logger.debug("Entering prepareDate()");
		CommitmentRuleData commitmentRuleData = new CommitmentRuleData();
		commitmentRuleData.setCmtAmount(this.cmtAmount.getValue());
		//TODO SET REQUIRES DATA
		logger.debug("Leaving prepareDate()");
		return commitmentRuleData;
	}

	private String executeRule(String ruleCode, CommitmentRuleData ruleObject) {
		logger.debug("Entering executeRule()");
		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		String result = "0";
		try {
			//Add fields and values
			for (String filed : ruleObject.getDeclaredFieldsAndValue().keySet()) {
				engine.put(filed, ruleObject.getDeclaredFieldsAndValue().get(filed));
			}
			// Execute the engine
			String rule = "function Rule(){" + ruleCode + "}Rule();";
			BigDecimal tempResult = new BigDecimal("0");
			if (engine.eval(rule) != null) {
				tempResult = new BigDecimal(engine.eval(rule).toString());
				result = tempResult.toString();
			} else {
				if (engine.get("Result") != null) {
					result = engine.get("Result").toString();
				}
			}
		} catch (Exception e) {
			Messagebox.show(e.toString());
			e.printStackTrace();
			logger.debug(e);
		}
		logger.debug("Leaving executeRule()");
		return result;
	}

	private void CaluculateSummary() {
		logger.debug("Entering CaluculateSummary()");
		Map<String, Object> map = getCommitmentService().getAmountSummary(this.custID.getValue());
		String cmtCount = "0";
		BigDecimal cmtTotAmount = BigDecimal.ZERO;
		BigDecimal cmtUtilizedTotAmount = BigDecimal.ZERO;
		if (map != null) {
			cmtCount = map.get(PennantConstants.CMT_TOTALCMT).toString();
			cmtTotAmount = new BigDecimal(map.get(PennantConstants.CMT_TOTALCMTAMT).toString());
			cmtUtilizedTotAmount = new BigDecimal(map.get(PennantConstants.CMT_TOTALUTZAMT).toString());
		}
		this.cmtCommitments.setValue(cmtCount);
		this.cmtTotAmount.setValue(PennantApplicationUtil.amountFormate(cmtTotAmount, defaultCCYDecPos));
		this.cmtUtilizedTotAmount.setValue(PennantApplicationUtil.amountFormate(cmtUtilizedTotAmount, defaultCCYDecPos));
		this.cmtUnUtilizedAmount.setValue(PennantApplicationUtil.amountFormate(cmtTotAmount.subtract(cmtUtilizedTotAmount), defaultCCYDecPos));
		logger.debug("Leaving CaluculateSummary()");

	}

	public void onChange$cmtAmount(Event event) {
		calculateCharges();
	}

	public void onChange$cmtCharges(Event event) {
		calculateCharges();
	}

	private void calculateCharges() {
		logger.debug("Entering calculateCharges()");
		this.calCmtCharges.setFormat(PennantAppUtil.getAmountFormate(defaultCCYDecPos));
		if (this.cmtAmount.getValue() != null && this.cmtCharges.getValue() == null) {
			if (this.cmtAmount.getValue().compareTo(BigDecimal.ZERO) != 0) {
				String result = null;
				List<Rule> list = null;
				if (maintain) {
					list = getCommitmentService().getRuleByModuleAndEvent("FEES", "MNTCMT");
				} else {
					list = getCommitmentService().getRuleByModuleAndEvent("FEES", "NEWCMT");
				}
				if (list != null && list.size() > 0) {
					Rule rule = list.get(0);
					result = executeRule(rule.getSQLRule(), prepareDate());
				}
				if (!StringUtils.trimToEmpty(result).equals("")) {
					this.calCmtCharges.setValue(new BigDecimal(result));

				}
			} else {
				this.calCmtCharges.setValue(BigDecimal.ZERO);
			}
		} else {
			this.calCmtCharges.setValue(this.cmtCharges.getValue());
		}
		if (this.calCmtCharges.getValue() != null && this.calCmtCharges.getValue().compareTo(BigDecimal.ZERO) != 0) {
			this.space_CmtChargesAccount.setSclass("mandatory");
		} else {
			this.space_CmtChargesAccount.setSclass("");
		}
		logger.debug("Leaving calculateCharges()");
	}

	private void getFinaceDetails(String cmtReference) {
		JdbcSearchObject<FinanceMain> searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		searchObj.addSort("FinReference", false);
		searchObj.addTabelName("FinanceMain_AView");
		searchObj.addFilterEqual("FinCommitmentRef", cmtReference);
		fillCommitmentFinace(getPagedListService().getBySearchObject(searchObj));

	}

	private void fillCommitmentFinace(List<FinanceMain> financeMains) {
		if (financeMains != null && financeMains.size() > 0) {
			for (FinanceMain financeMain : financeMains) {
				Listitem item = new Listitem();
				//final FinanceMain wIFFinanceMain = (FinanceMain) data;
				Listcell lc;
				lc = new Listcell(financeMain.getFinReference());
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinType());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(financeMain.getFinAmount(), financeMain.getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinCcy());
				lc.setParent(item);
				lc = new Listcell(financeMain.getScheduleMethod());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(financeMain.getNumberOfTerms()));
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatUtilDate(financeMain.getFinStartDate(), PennantConstants.dateFormate));
				lc.setParent(item);
				if (financeMain.getGrcPeriodEndDate() != null) {
					lc = new Listcell(DateUtility.formatUtilDate(financeMain.getGrcPeriodEndDate(), PennantConstants.dateFormate));
				} else {
					lc = new Listcell();
				}
				lc.setParent(item);
				if (financeMain.getMaturityDate() != null) {
					lc = new Listcell(DateUtility.formatUtilDate(financeMain.getMaturityDate(), PennantConstants.dateFormate));
				} else {
					lc = new Listcell();
				}
				lc.setParent(item);
				item.setAttribute("data", financeMain);
				item.addForward("onDoubleClick", this.window_CommitmentDialog, "onFinanceMainItemDoubleClicked", financeMain.getFinReference());
				this.listBoxCommitmentFinance.appendChild(item);
			}
		}
	}

	public void onFinanceMainItemDoubleClicked(Event event) throws Exception {
		Object object = event.getData();
		if (object != null) {
			getCommitmentMovementDetails(this.cmtReference.getValue(), object.toString());
		}
	}

	private void getCommitmentMovementDetails(String cmtReference, String finReference) {
		logger.debug("Entering");
		JdbcSearchObject<CommitmentMovement> searchObject = new JdbcSearchObject<CommitmentMovement>(CommitmentMovement.class);
		searchObject.addTabelName("CommitmentMovements");
		searchObject.addFilterEqual("CmtReference", cmtReference);
		searchObject.addSortDesc("MovementOrder");
		List<CommitmentMovement> commitmentMovements = getPagedListService().getBySearchObject(searchObject);
		fillCommitmovements(commitmentMovements);
		logger.debug("Leaving");
	}

	private void fillCommitmovements(List<CommitmentMovement> commitmentMovements) {
		logger.debug("Entering");
		this.listBoxCommitmentMovement.getItems().clear();
		if (commitmentMovements != null && commitmentMovements.size() > 0) {
			for (CommitmentMovement commitmentMovement : commitmentMovements) {
				Listitem listItem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(commitmentMovement.getMovementType());
				listcell.setParent(listItem);
				listcell = new Listcell(String.valueOf(commitmentMovement.getMovementOrder()));
				listcell.setParent(listItem);
				listcell = new Listcell(PennantApplicationUtil.formateDate(commitmentMovement.getMovementDate(), PennantConstants.dateFormat));
				listcell.setParent(listItem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentMovement.getMovementAmount(), defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentMovement.getCmtAmount(), defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentMovement.getCmtUtilizedAmount(), defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentMovement.getCmtAvailable(), defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentMovement.getCmtCharges(), defaultCCYDecPos));
				listcell.setParent(listItem);
				this.listBoxCommitmentMovement.appendChild(listItem);
			}

		}
		logger.debug("Leaving");
	}
	

	public class compareLinkedTransId implements Comparator<ReturnDataSet>, Serializable {
		private static final long	serialVersionUID	= -3639465555049007637L;

		@Override
		public int compare(ReturnDataSet commitment, ReturnDataSet commitment2) {

			if (commitment.getLinkedTranId() == commitment2.getLinkedTranId()) {
				return 0;
			} else {
				return 1;
			}

		}
	}
	
	class postingGroupListModelItemRenderer implements ListitemRenderer<ReturnDataSet>, Serializable {

		private static final long	serialVersionUID	= 1L;

		@Override
		public void render(Listitem item, ReturnDataSet returnDataSet, int count) throws Exception {
			if (item instanceof Listgroup) {
				Listcell lc;
				lc = new Listcell(String.valueOf(returnDataSet.getLinkedTranId()));
				lc.setStyle("cursor:default;");
				lc.setSpan(6);
				lc.setParent(item);
			} else {
				Listcell listcell;
				listcell = new Listcell("");
				listcell.setParent(item);
				listcell = new Listcell(PennantAppUtil.getlabelDesc(returnDataSet.getDrOrCr(), PennantAppUtil.getTranType()));
				listcell.setParent(item);
				listcell = new Listcell(returnDataSet.getTranDesc());
				listcell.setParent(item);
				listcell = new Listcell(PennantApplicationUtil.formateDate(returnDataSet.getPostDate(), PennantConstants.dateFormat));
				listcell.setParent(item);
				listcell = new Listcell(returnDataSet.getFinReference());
				listcell.setParent(item);
				listcell = new Listcell(returnDataSet.getAccount());
				listcell.setParent(item);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(returnDataSet.getPostAmount(), defaultCCYDecPos));
				listcell.setParent(item);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getCommitmentPostingDetails(String cmtReference) {
		logger.debug("Entering");
		JdbcSearchObject<ReturnDataSet> searchObject = new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		searchObject.addTabelName("Postings");
		searchObject.addSortDesc("LinkedTranId");
		searchObject.addFilterEqual("FinReference", cmtReference);
		
		this.listBoxCommitmentPostings.setItemRenderer(new postingGroupListModelItemRenderer());
		this.listBoxCommitmentPostings.setModel(new GroupsModelArray(getPagedListService().getBySearchObject(searchObject).toArray(),
				new compareLinkedTransId()));
		logger.debug("Leaving");
	}

	private void doDesignByMode() {
		logger.debug("Entering doDesignByMode()");
		this.row9.setVisible(maintain);
		if (maintain) {
			this.window_CommitmentDialog.setTitle(Labels.getLabel("window_MaintainCommitmentDialog.title"));
			this.cmtTitle.setReadonly(true);
			setStyle(this.cmtTitle);
			this.btnSearchcustID.setVisible(false);
			setStyle(this.btnSearchcustID);
			this.btnSearchCmtBranch.setVisible(false);
			setStyle(this.btnSearchCmtBranch);
			this.btnSearchCmtCcy.setVisible(false);
			setStyle(this.btnSearchCmtCcy);
			this.btnSearchCmtAccount.setVisible(false);
			setStyle(this.btnSearchCmtAccount);
			this.openAccount.setDisabled(true);
			this.multiBranch.setDisabled(true);
			this.cmtPromisedDate.setDisabled(true);
			this.cmtStartDate.setDisabled(true);
			this.revolving.setDisabled(true);
			this.sharedCmt.setDisabled(true);
			this.rowCmtSummary.setVisible(false);
			this.rowCmtCount.setVisible(false);
		} else {
			this.window_CommitmentDialog.setTitle(Labels.getLabel("window_NewCommitmentDialog.title"));
		}
		
		if (this.openAccount.isChecked()) {
			this.space_CmtAccount.setSclass("");
			btnSearchCmtAccount.setVisible(false);
		}		
		
		logger.debug("Leaving doDesignByMode()");
	}

	private void doCheckEnq() {
		logger.debug("Entering doCheckEnq()");
		if (enqModule) {
			this.cmtTitle.setReadonly(true);
			this.btnSearchcustID.setVisible(false);
			this.btnSearchCmtBranch.setVisible(false);
			this.btnSearchCmtCcy.setVisible(false);
			this.openAccount.setDisabled(true);
			this.btnSearchCmtAccount.setVisible(false);
			this.cmtPftRateMin.setDisabled(true);
			this.cmtPftRateMax.setDisabled(true);
			this.cmtStopRateRange.setDisabled(true);
			this.multiBranch.setDisabled(true);
			this.cmtAmount.setDisabled(true);
			this.cmtPromisedDate.setDisabled(true);
			this.cmtStartDate.setDisabled(true);
			this.cmtExpDate.setDisabled(true);
			this.revolving.setDisabled(true);
			this.sharedCmt.setDisabled(true);
			this.cmtCharges.setDisabled(true);
			this.btnSearchCmtChargesAccount.setVisible(false);
			this.cmtNotes.setReadonly(true);
			this.cmtActiveStatus.setDisabled(true);
			this.cmtNonperformingStatus.setDisabled(true);
			this.tab_CommitmentMovementDetails.setVisible(true);
			this.tab_CommitmentPostingDetails.setVisible(true);
			this.window_CommitmentDialog.setTitle(Labels.getLabel("window_EnqCommitmentDialog.title"));
			this.rowCmtSummary.setVisible(false);
			this.rowCmtCount.setVisible(false);
			getFinaceDetails(getCommitment().getCmtReference());
			getCommitmentMovementDetails(getCommitment().getCmtReference(), "");
			getCommitmentPostingDetails(getCommitment().getCmtReference());
		} else {
			this.tab_CommitmentMovementDetails.setVisible(false);
			this.tab_CommitmentPostingDetails.setVisible(false);
		}
		logger.debug("Leaving doCheckEnq()");
	}

	private void setStyle(Component component) {
		logger.debug("Entering setStyle()");
		Space space = (Space) component.getParent().getFirstChild();
		space.setSclass("");
		logger.debug("Leaving setStyle()");

	}

	private void setFormatByCCy(Decimalbox decimalbox, int decPos) {
		decimalbox.setFormat(PennantAppUtil.getAmountFormate(decPos));
		decimalbox.setScale(decPos);
	}

	private IAccounts getAccNumber() {
		try {
			List<IAccounts> iAccountList = new ArrayList<IAccounts>(1);
			IAccounts newAccount = new IAccounts();
			newAccount.setAcCustCIF(this.custIDName.getValue());
			newAccount.setAcBranch(this.cmtBranch.getValue());
			newAccount.setAcCcy(this.cmtCcy.getValue());
			newAccount.setAcType(acType);
			newAccount.setFlagCreateIfNF(true);
			newAccount.setFlagCreateNew(true);
			newAccount.setInternalAc(false);
			newAccount.setTransOrder("");
			iAccountList.add(newAccount);
			return getAccountInterfaceService().fetchExistAccount(iAccountList, "N", true).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
