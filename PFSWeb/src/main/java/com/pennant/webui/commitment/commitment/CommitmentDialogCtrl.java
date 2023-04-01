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
 * * FileName : CommitmentDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * * Modified
 * Date : 25-03-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.commitment.commitment;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.Interface.model.IAccounts;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.commitment.CommitmentRate;
import com.pennant.backend.model.commitment.CommitmentSummary;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.CommitmentRuleData;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CommitmentConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.CollateralHeaderDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/Commitment/Commitment/commitmentDialog.zul file.
 */
public class CommitmentDialogCtrl extends GFCBaseCtrl<Commitment> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CommitmentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CommitmentDialog;
	protected Label label_windowTitle;
	protected Row row0;
	protected Label label_CmtReference;
	protected Hlayout hlayout_CmtReference;
	protected Space space_CmtReference;
	protected Textbox cmtReference;
	protected Label label_CustCIF;
	protected Hlayout hlayout_CustCIF;
	protected Space space_CustCIF;

	protected Button btnSearchCustCIF;
	protected Button viewCustInfo;
	protected Longbox custID;
	protected Textbox custCIF;
	protected Label custName;

	protected Row row1;
	protected Label label_CmtBranch;
	protected ExtendedCombobox cmtBranch;
	protected Label label_OpenAccount;
	protected Hlayout hlayout_OpenAccount;
	protected Space space_OpenAccount;
	protected Checkbox openAccount;
	protected Row row2;
	protected Label label_CmtAccount;
	protected AccountSelectionBox cmtAccount;
	protected Label label_CmtCcy;
	protected ExtendedCombobox cmtCcy;
	protected ExtendedCombobox facilityRef;
	protected ExtendedCombobox limitLine;
	protected Row row3;
	protected Label label_CmtPftRateMin;
	protected Hlayout hlayout_CmtPftRateMin;
	protected Space space_CmtPftRateMin;
	protected Decimalbox cmtPftRateMin;
	protected Label label_CmtPftRateMax;
	protected Hlayout hlayout_CmtPftRateMax;
	protected Space space_CmtPftRateMax;
	protected Decimalbox cmtPftRateMax;
	protected Row row4;
	protected Label label_CmtAmount;
	protected Hlayout hlayout_CmtAmount;
	protected Space space_CmtAmount;
	protected CurrencyBox cmtAmount;
	protected Label label_CmtUtilizedAmount;
	protected Hlayout hlayout_CmtUtilizedAmount;
	protected Space space_CmtUtilizedAmount;
	protected Decimalbox cmtUtilizedAmount;
	protected Row row5;
	protected Label label_CmtAvailable;
	protected Hlayout hlayout_CmtAvailable;
	protected Space space_CmtAvailable;
	protected Decimalbox cmtAvailable;
	protected Label label_CmtPromisedDate;
	protected Hlayout hlayout_CmtPromisedDate;
	protected Space space_CmtPromisedDate;
	protected Datebox cmtPromisedDate;
	protected Row row6;
	protected Label label_CmtStartDate;
	protected Hlayout hlayout_CmtStartDate;
	protected Space space_CmtStartDate;
	protected Datebox cmtStartDate;
	protected Label label_CmtExpDate;
	protected Hlayout hlayout_CmtExpDate;
	protected Space space_CmtExpDate;
	protected Datebox cmtExpDate;
	protected Row row7;
	protected Label label_CmtTitle;
	protected Hlayout hlayout_CmtTitle;
	protected Space space_CmtTitle;
	protected Textbox cmtTitle;
	protected Label label_CmtNotes;
	protected Hlayout hlayout_CmtNotes;
	protected Space space_CmtNotes;
	protected Textbox cmtNotes;
	protected Row row8;
	protected Label label_Revolving;
	protected Hlayout hlayout_Revolving;
	protected Space space_Revolving;
	protected Checkbox revolving;
	protected Label label_SharedCmt;
	protected Hlayout hlayout_SharedCmt;
	protected Space space_SharedCmt;
	protected Checkbox sharedCmt;
	protected Row row_Status;
	protected Label label_MultiBranch;
	protected Hlayout hlayout_MultiBranch;
	protected Space space_MultiBranch;
	protected Checkbox multiBranch;
	protected Row row10;
	protected Row row11;
	protected Row row12;
	protected Label label_CmtCharges;
	protected Hlayout hlayout_CmtCharges;
	protected CurrencyBox cmtCharges;
	protected Hlayout hlayout_CmtChargesAccount;
	protected Label label_CmtChargesAccount;
	protected AccountSelectionBox cmtChargesAccount;
	protected Label label_CmtActiveStatus;
	protected Hlayout hlayout_CmtActiveStatus;
	protected Space space_CmtActiveStatus;
	protected Checkbox cmtActiveStatus;
	protected Label label_CmtNonperformingStatus;
	protected Hlayout hlayout_CmtNonperformingStatus;
	protected Space space_CmtNonperformingStatus;
	protected Checkbox cmtNonperformingStatus;
	protected Row row13;
	protected Label cmtCommitments;
	protected Label cmtTotAmount;
	protected Label cmtUtilizedTotAmount;
	protected Label cmtUnUtilizedAmount;
	protected Checkbox cmtStopRateRange;
	protected Label label_CmtCommitments;
	protected Label label_CmtTotAmount;
	protected Label label_CmtUtilizedTotAmount;
	protected Label label_CmtUnUtilizedAmount;
	protected Tab tab_CommitmentDetails;
	protected Tab tab_CommitmentMovementDetails;
	protected Tab tab_CommitmentPostingDetails;
	protected Listbox listBoxCommitmentMovement;
	protected Listbox listBoxCommitmentPostings;
	protected Listbox listBoxCommitmentFinance;
	protected Row rowCmtSummary;
	protected Row rowCmtCount;
	protected Row rowCmtTotAmount;
	protected Row rowCmtUtilized;
	protected Row rowCmtUnUtilized;

	protected Groupbox gbCommitmentSummary;
	protected Grid gridSummary;
	protected Listbox commitmentSummary;

	protected Button btnSearchCommitmentFlags;
	protected Textbox commitmentFlags;
	protected Label label_CmtAvailableMonths;
	protected Hlayout hlayout_CmtAvailableMonths;
	protected Space space_CmtAvailableMonths;
	protected Intbox cmtAvailableMonths;
	protected Label label_CmtRvwDate;
	protected Hlayout hlayout_CmtRvwDate;
	protected Space space_CmtRvwDate;
	protected Datebox cmtRvwDate;
	protected Label label_CollateralRequired;
	protected Hlayout hlayout_CollateralRequired;
	protected Space space_CollateralRequired;
	protected Checkbox collateralRequired;
	protected Datebox cmtEndDate;

	// Commitment Posting Details list headers
	protected Listheader listheader_Posting_LinkedTranId;
	protected Listheader listheader_Posting_DebitOrCredit;
	protected Listheader listheader_Posting_EntryDesc;
	protected Listheader listheader_Posting_PostDate;
	protected Listheader listheader_Posting_FinReference;
	protected Listheader listheader_Posting_AccountNo;
	protected Listheader listheader_Posting_Amount;

	// Commitment Rates Review
	protected Button btnNew_CmtRate;
	protected Listbox listBoxCmtRates;

	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab tab_Collateral;
	protected Tab tab_Customer;
	protected Tab tab_CommitmentAdditionalDetails;

	// Additional details
	protected Combobox bnkAggrmt;
	protected Combobox lmtCondition;
	protected Textbox reference;
	protected Textbox reference1;
	protected Intbox tenor;

	protected Component checkListChildWindow;
	protected Component collateralAssignmentWindow;

	// Controllers
	private transient CommitmentListCtrl commitmentListCtrl;
	private transient CustomerDialogCtrl customerDialogCtrl;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient CollateralHeaderDialogCtrl collateralHeaderDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;

	// Bean
	private Commitment commitment;

	// ServiceDAOs / Domain Classes
	private transient CommitmentService commitmentService;
	private transient PagedListService pagedListService;
	private transient AccountsService accountsService;
	private CustomerDataService customerDataService;

	private CommitmentDAO commitmentDAO;

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private NotificationService notificationService;
	private LimitDetails limitDetails;

	protected String selectMethodName = "onSelectTab";
	private BigDecimal oldCmtAmount = BigDecimal.ZERO;
	// private BigDecimal oldCmtCharges = BigDecimal.ZERO;
	private boolean maintain = false;
	private boolean newMaintain = false;
	private boolean proceed = true;
	private boolean isValidCust = true;

	private boolean fromLoan = false;

	private List<CommitmentRate> commitmentRateDetailList = new ArrayList<CommitmentRate>();
	private List<FinFlagsDetail> cmtFlagsDetailList = null;
	private List<FinanceCheckListReference> collateralChecklists = null;
	private Map<Long, Long> selectedAnsCountMap = null;

	// Default Values
	private Date appDate = SysParamUtil.getAppDate();
	private Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
	private int defaultCCYDecPos = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

	/**
	 * default constructor.<br>
	 */
	public CommitmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommitmentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Commitment object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CommitmentDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CommitmentDialog);

		try {
			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (Boolean) arguments.get("enqiryModule");
			} else {
				enqiryModule = false;
			}
			if (arguments.containsKey("fromLoan")) {
				fromLoan = true;
			}
			if (arguments.containsKey("commitment")) {
				this.commitment = (Commitment) arguments.get("commitment");
				Commitment befImage = new Commitment();
				BeanUtils.copyProperties(this.commitment, befImage);
				this.commitment.setBefImage(befImage);

				setCommitment(this.commitment);
			} else {
				setCommitment(null);
			}
			doLoadWorkFlow(this.commitment.isWorkflow(), this.commitment.getWorkflowId(),
					this.commitment.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CommitmentDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			registerListHeaders();

			if (arguments.containsKey("commitmentListCtrl")) {
				setCommitmentListCtrl((CommitmentListCtrl) arguments.get("commitmentListCtrl"));
			} else {
				setCommitmentListCtrl(null);
			}

			if (StringUtils.trimToEmpty(commitment.getRecordType()).equals(PennantConstants.RECORD_TYPE_UPD)) {
				maintain = true;
			} else if (StringUtils.isBlank(commitment.getRecordType()) && StringUtils
					.trimToEmpty(commitment.getRecordStatus()).equals(PennantConstants.RCD_STATUS_APPROVED)) {
				maintain = true;
				newMaintain = true;
				oldCmtAmount = getCommitment().getCmtAmount();
				// oldCmtCharges = getCommitment().getCmtCharges();
			}

			// Currency
			if (!getCommitment().isNewRecord()) {
				defaultCCYDecPos = CurrencyUtil.getFormat(getCommitment().getCmtCcy());
			}

			// Limit Line
			limitDetails = getCommitmentService().getLimitLineByDetailId(getCommitment().getLimitLineId());

			setListBoxHeight();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCommitment());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CommitmentDialog.onClose();
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
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());

		doSave();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCancel(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doWriteBeanToComponents(this.commitment.getBefImage());
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
		MessageUtil.showHelpWindow(event, window_CommitmentDialog);
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

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(
					getNotes("Commitment", getCommitment().getCmtReference(), getCommitment().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		doSearchCustomerCIF();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onChange$custCIF(Event event) throws InterruptedException, InterfaceException {
		logger.debug("Entering" + event.toString());

		List<Filter> filterList = new ArrayList<>();
		// filterList.add(new Filter("CustCoreBank", "", Filter.OP_NOT_EQUAL));

		this.custCIF.clearErrorMessage();
		Clients.clearWrongValue(this.btnSearchCustCIF);

		Customer customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), filterList);
		if (customer == null) {
			doResetValues();
			isValidCust = false;
			this.tab_Customer.setVisible(false);
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CommitmentDialog_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * View customer information
	 * 
	 * @param event
	 */
	public void onClick$viewCustInfo(Event event) {
		logger.debug("Entering");

		if (StringUtils.isBlank(this.custCIF.getValue())) {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CommitmentDialog_CustCIF.value") }));
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("custCIF", this.custCIF.getValue());
			map.put("custid", this.custID.longValue());
			map.put("custShrtName", this.custName.getValue());
			map.put("finFormatter", defaultCCYDecPos);
			map.put("finReference", this.cmtReference.getValue());
			map.put("finance", true);

			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
					window_CommitmentDialog, map);

			// Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
			// window_CommitmentDialog, map);

		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}

		logger.debug("Entering");
	}

	/**
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");

		List<Filter> filterList = new ArrayList<>();
		// filterList.add(new Filter("CustCoreBank", "", Filter.OP_NOT_EQUAL));

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtersList", filterList);
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException, InterfaceException {
		logger.debug("Entering");

		doClearMessage();
		doResetValues();
		isValidCust = true;

		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.custCIF.setValue(customer.getCustCIF());
			this.custName.setValue(customer.getCustShrtName());
			doChangeCustomer(customer);
		} else {
			this.custID.setValue(Long.valueOf(0));
			this.custCIF.setValue("");
		}

		logger.debug("Leaving ");
	}

	/**
	 * On Changing the Customer ID
	 * 
	 * @param details
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	private void doChangeCustomer(Customer details) throws InterruptedException, InterfaceException {
		logger.debug("Entering");

		CaluculateSummary();
		// this.openAccount.setChecked(true);
		// doCheckOpenAccount();
		this.custID.setValue(details.getCustID());
		this.custCIF.setValue(details.getCustCIF());
		this.cmtBranch.setValue(details.getCustDftBranch(), details.getLovDescCustDftBranchName());
		this.cmtChargesAccount.setCustCIF(details.getCustCIF());

		if (StringUtils.isNotBlank(this.cmtCcy.getValidatedValue())) {
			this.cmtCcy.getValidatedValue();
			Currency currency = (Currency) cmtCcy.getObject();
			if (currency != null) {
				doChangeCurrencyDetails(currency);
			}
		}

		// Customer Data Fetching
		CustomerDetails customerDetails = fetchCustomerData(details.getCustCIF());
		getCommitment().setCustomerDetails(customerDetails);

		appendCustomerDetailTab(false);
		this.tab_Customer.setVisible(true);

		// Facility Reference
		Filter[] filters = new Filter[1];

		filters[0] = new Filter("CustID", customerDetails.getCustID(), Filter.OP_EQUAL);
		this.facilityRef.setFilters(filters);

		// Limit Header
		long headerId = 0;
		LimitHeader limitHeader = getCommitmentService().getLimitHeaderByCustomerId(customerDetails.getCustID());
		if (limitHeader != null) {
			headerId = limitHeader.getHeaderId();
			limitHeader.setCustFullName(PennantApplicationUtil.getFullName(limitHeader.getCustFName(),
					limitHeader.getCustMName(), limitHeader.getCustFullName()));
		}
		this.limitLine.setFilters(getDefaultFilters(headerId));

		// Fetching Commitment Check Lists Details
		if (isWorkFlowEnabled()) {
			setCommitment(
					getCommitmentService().getProcessEditorDetails(getCommitment(), getRole(), FinServiceEvent.ORG));// TODO
																														// role
																														// or
																														// NextRole
		}

		// Agreement Details Tab
		setAgreementDetailTab();

		// Fill Check List Details based on Rule Execution if Rule Exist
		appendCheckListDetailTab(getCommitment(), false);

		// Collateral Details Tab
		appendCollateralAssignmentTab();

		// Document Details
		if (documentDetailDialogCtrl != null) {
			documentDetailDialogCtrl.doFillDocumentDetails(getCommitment().getDocuments());
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param headerId
	 * @param size
	 * @return
	 */
	private Filter[] getDefaultFilters(long headerId) {
		logger.debug("Entering");

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("LimitLine", "", Filter.OP_NOT_NULL);
		filters[1] = new Filter("LimitLine", LimitConstants.LIMIT_ITEM_UNCLSFD, Filter.OP_NOT_EQUAL);
		filters[2] = new Filter("LimitHeaderId", headerId, Filter.OP_EQUAL);

		logger.debug("Leaving");
		return filters;
	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public CustomerDetails fetchCustomerData(String cif) throws InterruptedException, InterfaceException {
		logger.debug("Entering");

		CustomerDetails customerDetails = null;
		// Get the data of Customer from Core Banking Customer
		try {
			Customer customer = null;
			// check Customer Data in LOCAL PFF system
			customer = customerDataService.getCheckCustomerByCIF(cif);

			if (customer != null) {
				customerDetails = customerDataService.getCustomerDetailsbyID(customer.getId(), true, "_View");
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
		return customerDetails;
	}

	/**
	 * On Changing commitment currency Details
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$cmtCcy(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = cmtCcy.getObject();
		if (dataObject instanceof String) {
			this.cmtCcy.setValue(dataObject.toString());
			this.cmtCcy.setDescription("");
			this.cmtChargesAccount.setFormatter(SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				doChangeCurrencyDetails(details);
			}
			if (this.custCIF.getValue() != null) {
				CaluculateSummary();
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * onChanging Currency details
	 * 
	 * @param details
	 * @throws InterruptedException
	 */
	private void doChangeCurrencyDetails(Currency details) throws InterruptedException {
		logger.debug("Entering");

		this.cmtCcy.clearErrorMessage();
		Clients.clearWrongValue(this.cmtCcy);

		this.cmtCcy.setValue(details.getCcyCode());
		this.cmtCcy.setDescription(details.getCcyDesc());

		// Format Amount based on the currency
		defaultCCYDecPos = details.getCcyEditField();
		this.cmtAmount.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtAmount.setScale(defaultCCYDecPos);

		this.cmtCharges.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtCharges.setScale(defaultCCYDecPos);

		setFormatByCCy(this.cmtUtilizedAmount, defaultCCYDecPos);
		setFormatByCCy(this.cmtAvailable, defaultCCYDecPos);

		this.cmtChargesAccount.setFormatter(details.getCcyEditField());

		// this.openAccount.setChecked(true);
		// doCheckOpenAccount();

		logger.debug("Leaving");
	}

	/**
	 * Validate Customer CIF
	 */
	private void doValidateCustCIF() {
		logger.debug("Entering");

		this.custCIF.clearErrorMessage();
		Clients.clearWrongValue(this.btnSearchCustCIF);

		if (StringUtils.isBlank(this.custCIF.getValue())) {
			this.tab_CommitmentDetails.setSelected(true);
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CommitmentDialog_CustCIF.value") }));
		}

		logger.debug("Leaving");
	}

	/**
	 * onChanging Facility details
	 * 
	 * @param event
	 */
	public void onFulfill$facilityRef(Event event) {
		logger.debug("Entering");

		if (!maintain) {
			Object dataObject = null;
			dataObject = this.facilityRef.getObject();
			if (dataObject instanceof String) {
				this.cmtReference.setValue("");
				this.cmtReference.setReadonly(false);
			} else {
				FacilityDetail details = (FacilityDetail) dataObject;
				if (details != null) {
					this.cmtReference.setValue(details.getFacilityRef());
					this.cmtReference.setReadonly(true);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * onChanging Facility details
	 * 
	 * @param event
	 */
	public void onFulfill$limitLine(Event event) {
		logger.debug("Entering");

		this.limitLine.clearErrorMessage();
		Clients.clearWrongValue(this.limitLine);

		long limitLineId = 0;
		limitDetails = null;
		Object dataObject = this.limitLine.getObject();

		if (dataObject != null) {
			if (dataObject instanceof LimitDetails) {
				limitDetails = (LimitDetails) dataObject;
				limitLineId = limitDetails.getDetailId();
				doCheckRevolving(limitDetails);
			}
		}
		this.limitLine.setAttribute("limitLineId", limitLineId);
		doCheckRevolving(limitDetails);

		logger.debug("Leaving");
	}

	/**
	 * linked limit line is of revolving type Default to TRUE and allow change.<br>
	 * linked limit line is of non-revolving type Default to FALSE and disable from maintenance.
	 * 
	 * @param details
	 */
	public void doCheckRevolving(LimitDetails details) {
		logger.debug("Entering");

		if (details != null && details.isRevolving()) {
			this.revolving.setChecked(true);
			readOnlyComponent(false, this.revolving);
		} else {
			this.revolving.setChecked(false);
			readOnlyComponent(true, this.revolving);
		}

		logger.debug("Leaving");
	}

	/**
	 * Check Method for collateral Required
	 * 
	 * @param event
	 */
	public void onCheck$collateralRequired(Event event) {
		logger.debug("Entering " + event.toString());

		if (tab_Collateral != null) {
			if (this.collateralRequired.isChecked()) {
				tab_Collateral.setVisible(true);
			} else {
				tab_Collateral.setVisible(false);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCommitment
	 */
	public void doShowDialog(Commitment aCommitment) {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCommitment);

			// set ReadOnly mode accordingly if the object is new or not.
			displayComponents(ScreenCTL.getMode(enqiryModule, isWorkFlowEnabled(), aCommitment.isNewRecord()));

			if (!enqiryModule) {
				doDesignByMode();
			} else {
				this.south.setVisible(false);
			}

			if (StringUtils.isBlank(this.cmtTitle.getValue())) {
				setComponentAccessType("CommitmentDialog_CmtTitle", false, this.cmtTitle, this.space_CmtTitle,
						this.label_CmtTitle, this.hlayout_CmtTitle, null);
			}

			// Enquiry Mode
			doCheckEnq();

			// during user action.
			if (fromLoan) {
				setDialog(DialogType.MODAL);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_CommitmentDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * RegisterListHeaders
	 */
	private void registerListHeaders() {

		this.listheader_Posting_LinkedTranId.setSortAscending(new FieldComparator("LinkedTranId", true));
		this.listheader_Posting_LinkedTranId.setSortDescending(new FieldComparator("LinkedTranId", false));
		this.listheader_Posting_DebitOrCredit.setSortAscending(new FieldComparator("DrOrCr", true));
		this.listheader_Posting_DebitOrCredit.setSortDescending(new FieldComparator("DrOrCr", false));
		this.listheader_Posting_EntryDesc.setSortAscending(new FieldComparator("TranDesc", true));
		this.listheader_Posting_EntryDesc.setSortDescending(new FieldComparator("TranDesc", false));
		this.listheader_Posting_PostDate.setSortAscending(new FieldComparator("PostDate", true));
		this.listheader_Posting_PostDate.setSortDescending(new FieldComparator("PostDate", false));
		this.listheader_Posting_FinReference.setSortAscending(new FieldComparator("FinReference", true));
		this.listheader_Posting_FinReference.setSortDescending(new FieldComparator("FinReference", false));
		this.listheader_Posting_AccountNo.setSortAscending(new FieldComparator("Account", true));
		this.listheader_Posting_AccountNo.setSortDescending(new FieldComparator("Account", false));
		this.listheader_Posting_Amount.setSortAscending(new FieldComparator("PostAmount", true));
		this.listheader_Posting_Amount.setSortDescending(new FieldComparator("PostAmount", false));

	}

	/**
	 * Set ListBoxHeight
	 */
	private void setListBoxHeight() {
		logger.debug("Entering");

		this.listBoxCommitmentPostings.setHeight(this.borderLayoutHeight - 175 + "px");
		int divKycHeight = this.borderLayoutHeight - 80;// TODO
		int borderlayoutHeights = divKycHeight / 2;
		this.listBoxCmtRates.setHeight(borderlayoutHeights - 90 + "px");

		logger.debug("Leaving ");
	}

	/**
	 * 
	 * @param mode
	 */
	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.cmtTitle, this.custCIF));

		if (getCommitment().isNewRecord()) {
			setComponentAccessType("CommitmentDialog_CmtTitle", false, this.cmtTitle, this.space_CmtTitle,
					this.label_CmtTitle, this.hlayout_CmtTitle, null);
			setComponentAccessType("CommitmentDialog_CustCIF", false, this.custCIF, this.space_CustCIF,
					this.label_CustCIF, this.hlayout_CustCIF, null);
			setComponentAccessType("CommitmentDialog_CustCIF", false, this.btnSearchCustCIF, this.space_CustCIF,
					this.label_CustCIF, this.hlayout_CustCIF, null);
			setComponentAccessType("CommitmentDialog_CmtReference", false, this.cmtReference, this.space_CmtReference,
					this.label_CmtReference, this.hlayout_CmtReference, null);
			readOnlyComponent(true, this.cmtStopRateRange);

			this.space_CmtPftRateMax.setSclass("");
			this.tab_Customer.setVisible(false);
		} else {
			setComponentAccessType("CommitmentDialog_CmtTitle", true, this.cmtTitle, this.space_CmtTitle,
					this.label_CmtTitle, this.hlayout_CmtTitle, null);
			setComponentAccessType("CommitmentDialog_CustCIF", true, this.custCIF, this.space_CustCIF,
					this.label_CustCIF, this.hlayout_CustCIF, null);
			setComponentAccessType("CommitmentDialog_CustCIF", true, this.btnSearchCustCIF, this.space_CustCIF,
					this.label_CustCIF, this.hlayout_CustCIF, null);
			setComponentAccessType("CommitmentDialog_CmtReference", true, this.cmtReference, this.space_CmtReference,
					this.label_CmtReference, this.hlayout_CmtReference, null);

			readOnlyComponent(isReadOnly("CommitmentDialog_CmtStopRateRange"), this.cmtStopRateRange);
		}

		if (getCommitment().isCollateralRequired() && this.tab_Collateral != null) {
			this.tab_Collateral.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;

		if (PennantConstants.RECORD_TYPE_DEL.equals(commitment.getRecordType())) {
			tempReadOnly = true;
		}
		setExtAccess("CommitmentDialog_CmtBranch", tempReadOnly, this.cmtBranch, null);
		setExtAccess("CommitmentDialog_CmtCcy", tempReadOnly, this.cmtCcy, null);

		setExtAccess("CommitmentDialog_CmtLimitLine", tempReadOnly, this.limitLine, null);
		setExtAccess("CommitmentDialog_FacilityRef", tempReadOnly, this.facilityRef, null);

		this.btnSearchCommitmentFlags.setDisabled(isReadOnly("CommitmentDialog_CommitmentFlags"));

		/*
		 * setComponentAccessType("CommitmentDialog_OpenAccount", tempReadOnly, this.openAccount,
		 * this.space_OpenAccount, this.label_OpenAccount, this.hlayout_OpenAccount, null);
		 */ setAccountBoxAccess("CommitmentDialog_CmtAccount", tempReadOnly, this.cmtAccount, null);

		setComponentAccessType("CommitmentDialog_CmtPftRateMin", tempReadOnly, this.cmtPftRateMin,
				this.space_CmtPftRateMin, this.label_CmtPftRateMin, this.hlayout_CmtPftRateMin, null);
		setComponentAccessType("CommitmentDialog_CmtPftRateMax", tempReadOnly, this.cmtPftRateMax,
				this.space_CmtPftRateMax, this.label_CmtPftRateMax, this.hlayout_CmtPftRateMax, null);

		setCurrencyBoxAccess("CommitmentDialog_CmtAmount", tempReadOnly, this.cmtAmount, null);

		setComponentAccessType("CommitmentDialog_CmtPromisedDate", tempReadOnly, this.cmtPromisedDate, null,
				this.label_CmtPromisedDate, this.hlayout_CmtPromisedDate, null);
		setComponentAccessType("CommitmentDialog_CmtStartDate", tempReadOnly, this.cmtStartDate, null,
				this.label_CmtStartDate, this.hlayout_CmtStartDate, null);
		setComponentAccessType("CommitmentDialog_CmtEndDate", tempReadOnly, this.cmtEndDate, null,
				this.label_CmtStartDate, this.hlayout_CmtStartDate, null);
		setComponentAccessType("CommitmentDialog_CmtExpDate", tempReadOnly, this.cmtExpDate, null,
				this.label_CmtExpDate, this.hlayout_CmtExpDate, null);
		setComponentAccessType("CommitmentDialog_CmtRvwDate", tempReadOnly, this.cmtRvwDate, space_CmtRvwDate,
				this.label_CmtRvwDate, this.hlayout_CmtRvwDate, null);

		setComponentAccessType("CommitmentDialog_MultiBranch", tempReadOnly, this.multiBranch, null,
				this.label_MultiBranch, this.hlayout_MultiBranch, null);
		setComponentAccessType("CommitmentDialog_SharedCmt", tempReadOnly, this.sharedCmt, null, this.label_SharedCmt,
				this.hlayout_SharedCmt, null);

		setComponentAccessType("CommitmentDialog_CmtActiveStatus", tempReadOnly, this.cmtActiveStatus, null,
				this.label_CmtActiveStatus, this.hlayout_CmtActiveStatus, null);
		setComponentAccessType("CommitmentDialog_CmtNonperformingStatus", tempReadOnly, this.cmtNonperformingStatus,
				null, this.label_CmtNonperformingStatus, this.hlayout_CmtNonperformingStatus, null);
		setComponentAccessType("CommitmentDialog_CollateralRequired", tempReadOnly, this.collateralRequired, null,
				this.label_CollateralRequired, this.hlayout_CollateralRequired, null);
		setCurrencyBoxAccess("CommitmentDialog_CmtCharges", tempReadOnly, this.cmtCharges, null);

		setComponentAccessType("CommitmentDialog_CmtNotes", tempReadOnly, this.cmtNotes, null, this.label_CmtNotes,
				this.hlayout_CmtNotes, null);

		// Commitment Revolving
		if (limitDetails != null && limitDetails.isRevolving()) {
			readOnlyComponent(isReadOnly("CommitmentDialog_Revolving"), this.revolving);
		} else {
			readOnlyComponent(true, this.revolving);
		}

		// Commitment Available Months
		if (getCommitment().getCmtExpDate() != null && getCommitment().getCmtExpDate().compareTo(appEndDate) < 0) {
			setComponentAccessType("CommitmentDialog_CmtAvailableMonths", tempReadOnly, this.cmtAvailableMonths,
					this.space_CmtAvailableMonths, this.label_CmtAvailableMonths, this.hlayout_CmtAvailableMonths,
					null);
		} else {
			readOnlyComponent(true, this.cmtAvailableMonths);
		}

		if (!getCommitment().isNewRecord() && readOnly) {
			setComponentAccessType("CommitmentDialog_CmtChargesAccount", true, this.cmtChargesAccount, null,
					this.label_CmtChargesAccount, this.hlayout_CmtChargesAccount, null);
		} else {
			setComponentAccessType("CommitmentDialog_CmtChargesAccount", false, this.cmtChargesAccount, null,
					this.label_CmtChargesAccount, this.hlayout_CmtChargesAccount, null);
		}

		this.cmtCharges.setMandatory(false);

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

		getUserWorkspace().allocateAuthorities("CommitmentDialog", getRole());

		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnSave"));

			this.btnNew_CmtRate.setVisible(getUserWorkspace().isAllowed("button_CommitmentDialog_btnNewCmtRate"));
		}

		this.viewCustInfo.setVisible(true);

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.cmtReference.setMaxlength(20);

		this.cmtBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.cmtBranch.setTextBoxWidth(120);
		this.cmtBranch.setMandatoryStyle(true);
		this.cmtBranch.setModuleName("Branch");
		this.cmtBranch.setValueColumn("BranchCode");
		this.cmtBranch.setDescColumn("BranchDesc");
		this.cmtBranch.setValidateColumns(new String[] { "BranchCode" });

		this.cmtAccount.setAcountDetails(AccountConstants.ACTYPES_COMMITMENT, "", true);
		this.cmtAccount.setFormatter(defaultCCYDecPos);
		this.cmtAccount.setTextBoxWidth(165);

		this.cmtCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.cmtCcy.setMandatoryStyle(true);
		this.cmtCcy.setModuleName("Currency");
		this.cmtCcy.setValueColumn("CcyCode");
		this.cmtCcy.setDescColumn("CcyDesc");
		this.cmtCcy.setValidateColumns(new String[] { "CcyCode" });

		// Limit Line
		this.limitLine.setMandatoryStyle(true);
		this.limitLine.setMaxlength(8);
		this.limitLine.setTextBoxWidth(120);
		this.limitLine.setModuleName("CMTLimitLine");
		this.limitLine.setValueColumn("LimitLine");
		this.limitLine.setDescColumn("LimitLineDesc");
		this.limitLine.setValidateColumns(new String[] { "LimitLine" });

		if (limitDetails != null) {
			this.limitLine.setFilters(getDefaultFilters(limitDetails.getLimitHeaderId()));
		} else {
			// this.limitLine.setFilters(getDefaultFilters(0));
		}

		this.facilityRef.setMaxlength(20);
		this.facilityRef.setMandatoryStyle(false);
		this.facilityRef.setModuleName("FacilityDetail");
		this.facilityRef.setValueColumn("FacilityRef");
		this.facilityRef.setDescColumn("FacilityType");
		this.facilityRef.setValidateColumns(new String[] { "FacilityRef" });

		this.cmtPftRateMin.setMaxlength(13);
		this.cmtPftRateMin.setFormat(PennantConstants.rateFormate9);
		this.cmtPftRateMin.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.cmtPftRateMin.setScale(9);
		this.space_CmtPftRateMin.setSclass("mandatory");
		this.cmtPftRateMax.setMaxlength(13);
		this.cmtPftRateMax.setFormat(PennantConstants.rateFormate9);
		this.cmtPftRateMax.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.cmtPftRateMax.setScale(9);

		this.cmtAmount.setTextBoxWidth(155);
		this.cmtAmount.setMandatory(true);
		this.cmtAmount.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtAmount.setScale(defaultCCYDecPos);

		this.cmtUtilizedAmount.setMaxlength(18);
		this.cmtUtilizedAmount.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtUtilizedAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.cmtUtilizedAmount.setScale(defaultCCYDecPos);

		this.cmtAvailable.setMaxlength(18);
		this.cmtAvailable.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtAvailable.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.cmtAvailable.setScale(defaultCCYDecPos);

		this.cmtPromisedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.cmtStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.cmtExpDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.cmtCharges.setMandatory(false);
		this.cmtCharges.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.cmtCharges.setScale(defaultCCYDecPos);
		this.cmtCharges.setTextBoxWidth(170);

		this.cmtTitle.setMaxlength(50);
		this.cmtNotes.setMaxlength(500);

		this.cmtChargesAccount.setTextBoxWidth(165);
		this.cmtChargesAccount.setAccountDetails("", AccountConstants.ACTYPES_COMMITCHARGE, "", true);
		this.cmtChargesAccount.setFormatter(defaultCCYDecPos);

		this.cmtRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.cmtEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.cmtAvailableMonths.setMaxlength(5);

		this.tab_CommitmentAdditionalDetails
				.setVisible(SysParamUtil.isAllowed(SMTParameterConstants.COMMITE_ADDTNAL_FIELDS_REQ));

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCommitment Commitment
	 */
	public void doWriteBeanToComponents(Commitment aCommitment) {
		logger.debug("Entering");

		if (aCommitment.isNewRecord()) { // New Record
			this.custName.setValue("");
			this.cmtBranch.setDescription("");
			this.facilityRef.setDescription("");

			this.limitLine.setValue(aCommitment.getLimitLineCode());
			this.limitLine.setDescription("");

			this.cmtPromisedDate.setValue(appDate);

			this.cmtTotAmount.setValue(String.valueOf(BigDecimal.ZERO));
			this.cmtUtilizedTotAmount.setValue(String.valueOf(BigDecimal.ZERO));
			this.cmtUnUtilizedAmount.setValue(String.valueOf(BigDecimal.ZERO));
			this.cmtCommitments.setValue(String.valueOf(BigDecimal.ZERO));

			this.openAccount.setChecked(true);
			this.multiBranch.setChecked(true);
			this.sharedCmt.setChecked(true);

			// Currency
			PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_DFT_CURR");
			this.cmtCcy.setValue(parameter.getSysParmValue().trim());
			this.cmtCcy.setDescription(parameter.getSysParmDescription());
		} else {
			this.custName.setValue(aCommitment.getCustShrtName());
			this.cmtPromisedDate.setValue(aCommitment.getCmtPromisedDate());

			// Commitment Currency
			this.cmtCcy.setValue(aCommitment.getCmtCcy());
			if (StringUtils.isNotBlank(aCommitment.getCmtCcy())) {
				this.cmtCcy.setDescription(CurrencyUtil.getCcyDesc(aCommitment.getCmtCcy()));
			}

			this.openAccount.setChecked(aCommitment.isOpenAccount());
			this.multiBranch.setChecked(aCommitment.isMultiBranch());
			this.sharedCmt.setChecked(aCommitment.isSharedCmt());

			// Limit Line
			if (limitDetails != null) {
				this.limitLine.setAttribute("limitLineId", aCommitment.getLimitLineId());
				this.limitLine.setValue(limitDetails.getLimitLine(),
						StringUtils.trimToEmpty(limitDetails.getLimitLineDesc()));
			}
		}

		this.cmtTitle.setValue(aCommitment.getCmtTitle());
		this.cmtReference.setValue(aCommitment.getCmtReference());
		this.custCIF.setValue(aCommitment.getCustCIF());
		this.custID.setValue(aCommitment.getCustID());

		// Commitment Branch
		this.cmtBranch.setValue(aCommitment.getCmtBranch());
		if (StringUtils.isNotBlank(aCommitment.getCmtBranch())) {
			this.cmtBranch.setDescription(aCommitment.getBranchDesc());
		}

		// Facility Reference
		Filter[] filters = new Filter[1];
		long custid = StringUtils.trimToEmpty(this.custCIF.getValue()) == "" ? 0
				: Long.valueOf(this.custCIF.getValue());
		filters[0] = new Filter("CustID", custid, Filter.OP_EQUAL);
		this.facilityRef.setFilters(filters);

		this.facilityRef.setValue(aCommitment.getFacilityRef());
		if (!StringUtils.trimToEmpty(aCommitment.getFacilityRef()).equals("")) {
			this.facilityRef.setDescription(aCommitment.getFacilityRefDesc());
		}

		this.cmtStartDate.setValue(aCommitment.getCmtStartDate());
		this.cmtEndDate.setValue(aCommitment.getCmtEndDate());
		this.cmtExpDate.setValue(aCommitment.getCmtExpDate());
		this.cmtRvwDate.setValue(aCommitment.getCmtRvwDate());

		this.cmtAvailableMonths.setValue(aCommitment.getCmtAvailableMonths());

		this.cmtPftRateMin.setValue(aCommitment.getCmtPftRateMin());
		this.cmtPftRateMax.setValue(aCommitment.getCmtPftRateMax());

		this.cmtStopRateRange.setChecked(aCommitment.isCmtStopRateRange());
		this.cmtActiveStatus.setChecked(aCommitment.isCmtActive());
		this.cmtNonperformingStatus.setChecked(aCommitment.isNonperformingStatus());
		this.revolving.setChecked(aCommitment.isRevolving());
		this.cmtAccount.setValue(aCommitment.getCmtAccount());

		// Changes Account
		this.cmtChargesAccount.setValue(aCommitment.getChargesAccount());
		if (StringUtils.isNotBlank(aCommitment.getChargesAccount())) {
			// this.cmtChargesAccountName.setValue(PennantApplicationUtil.formatAccountNumber(aCommitment.getChargesAccount()));
		}

		// Changes
		if (aCommitment.getCmtCharges() != null) {
			this.cmtCharges
					.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtCharges(), defaultCCYDecPos));
		} else {
			this.cmtCharges.setValue(aCommitment.getCmtCharges());
		}

		this.collateralRequired.setChecked(aCommitment.isCollateralRequired());
		this.cmtNotes.setValue(aCommitment.getCmtNotes());

		this.cmtAmount.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtAmount(), defaultCCYDecPos));
		this.cmtUtilizedAmount
				.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtUtilizedAmount(), defaultCCYDecPos));
		this.cmtAvailable
				.setValue(PennantApplicationUtil.formateAmount(aCommitment.getCmtAvailable(), defaultCCYDecPos));

		if (!maintain) {
			CaluculateSummary();
			this.gbCommitmentSummary.setVisible(true);
			// this.summaryCell.setValign("Top");
		} else {
			this.gridSummary.setVisible(true);
			this.cmtTotAmount
					.setValue(PennantApplicationUtil.amountFormate(aCommitment.getCmtAmount(), defaultCCYDecPos));
			this.cmtUtilizedTotAmount.setValue(
					PennantApplicationUtil.amountFormate(aCommitment.getCmtUtilizedAmount(), defaultCCYDecPos));
			this.cmtUnUtilizedAmount
					.setValue(PennantApplicationUtil.amountFormate(aCommitment.getCmtAvailable(), defaultCCYDecPos));
		}

		// Additional fields added
		this.fillComboBox(bnkAggrmt, aCommitment.getBankingArrangement(), PennantStaticListUtil.getBankingArrangement(),
				"");
		// this.bnkAggrmt.setValue(aCommitment.getBankingArrangement());

		this.fillComboBox(lmtCondition, aCommitment.getLimitCondition(), PennantStaticListUtil.getLimitCondition(), "");
		// this.lmtCondition.setValue(aCommitment.getLimitCondition());
		this.reference.setValue(aCommitment.getExternalRef());
		this.reference1.setValue(aCommitment.getExternalRef1());
		this.tenor.setValue(aCommitment.getTenor());

		// Customer Details Tab Addition
		appendCustomerDetailTab(true);

		// Commitment Flags
		doFillCommitmentFlagDetails(aCommitment.getCmtFlagDetailList());

		// Render the Commitment Review Rates
		doFillCommitmentRateDetails(aCommitment.getCommitmentRateList());

		// Collateral Detail Tab Addition
		appendCollateralAssignmentTab();

		// Agreements Detail Tab Addition
		appendAgreementsDetailTab(true);

		// CheckList Details Tab Addition
		appendCheckListDetailTab(aCommitment, true);

		// Document Detail Tab Addition
		appendDocumentDetailTab();

		// Recommend & Comments Details Tab Addition
		appendRecommendDetailTab(true);

		this.recordStatus.setValue(aCommitment.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommitment
	 */
	public void doWriteComponentsToBean(Commitment aCommitment) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Commitment Title
		try {
			aCommitment.setCmtTitle(this.cmtTitle.getValue());
			if (!this.cmtTitle.isReadonly() && StringUtils.isBlank(this.cmtTitle.getValue())) {
				throw new WrongValueException(this.cmtTitle, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_CommitmentDialog_CmtTitle.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Reference
		try {
			aCommitment.setCmtReference(this.cmtReference.getValue());
			if (StringUtils.isBlank(this.cmtReference.getValue())) {
				throw new WrongValueException(this.cmtReference, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_CommitmentDialog_CmtReference.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Customer CIF
		try {
			if (StringUtils.isEmpty(this.custCIF.getValue()) || !isValidCust) {
				wve.add(new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_CommitmentDialog_CustCIF.value") })));
			} else {
				aCommitment.setCustID(this.custID.longValue());
				aCommitment.setCustCIF(this.custCIF.getValue());
			}
			aCommitment.setCustShrtName(this.custName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Branch
		try {
			aCommitment.setBranchDesc(this.cmtBranch.getDescription());
			if (StringUtils.isEmpty(this.cmtBranch.getValue())) {
				wve.add(new WrongValueException(this.cmtBranch, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_CommitmentDialog_CmtBranch.value") })));
			} else {
				aCommitment.setCmtBranch(this.cmtBranch.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Currency
		try {
			if (StringUtils.isEmpty(this.cmtCcy.getValue())) {
				wve.add(new WrongValueException(this.cmtCcy, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_CommitmentDialog_CmtCcy.value") })));
			} else {
				aCommitment.setCmtCcy(this.cmtCcy.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Open Account
		try {
			aCommitment.setOpenAccount(this.openAccount.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Commitment Account
		try {
			aCommitment.setCmtAccount(PennantApplicationUtil.unFormatAccountNumber(this.cmtAccount.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Minimum Rate
		try {
			if (this.cmtPftRateMin.getValue() != null) {
				aCommitment.setCmtPftRateMin(this.cmtPftRateMin.getValue());
			} else {
				aCommitment.setCmtPftRateMin(BigDecimal.ZERO);
			}

			// Maximum Rate
			if (aCommitment.getCmtPftRateMin().compareTo(BigDecimal.ZERO) > 0) {
				if (this.cmtPftRateMax.getValue() != null) {
					aCommitment.setCmtPftRateMax(this.cmtPftRateMax.getValue());
				} else {
					aCommitment.setCmtPftRateMax(BigDecimal.ZERO);
				}
			}

			if (aCommitment.getCmtPftRateMax() == null) {
				aCommitment.setCmtPftRateMax(BigDecimal.ZERO);
			}

			if (aCommitment.getCmtPftRateMax().compareTo(aCommitment.getCmtPftRateMin()) < 0) {

				throw new WrongValueException(this.cmtPftRateMax,
						Labels.getLabel("FIELD_IS_GREATER",
								new String[] { Labels.getLabel("label_CommitmentDialog_CmtPftRateMax.value"),
										String.valueOf(this.cmtPftRateMin.getValue()) }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Amount
		try {
			if (maintain) {
				// New Commitment amount cannot be less than Utilized Amount
				if (this.cmtAmount.getValidateValue().compareTo(this.cmtUtilizedAmount.getValue()) < 0) {
					/*
					 * throw new WrongValueException(this.cmtAmount, Labels.getLabel("AMOUNT_NO_LESS", new String[] {
					 * Labels.getLabel("label_CommitmentDialog_CmtAmount.value"),
					 * Labels.getLabel("label_CommitmentDialog_CmtUtilizedAmount.value") }));
					 */
				}

				ErrorDetail errorDetails = null;
				if (this.cmtAmount.getActualValue().compareTo(this.cmtUtilizedAmount.getValue()) < 0) {
					BigDecimal percentage = this.cmtUtilizedAmount.getValue().subtract(this.cmtAmount.getActualValue())
							.multiply(BigDecimal.valueOf(100))
							.divide(this.cmtAmount.getActualValue(), RoundingMode.HALF_DOWN);

					if (percentage.compareTo(BigDecimal.valueOf(20)) <= 0) {
						errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60101",
								new String[] { percentage.toString() }, null), "");
					} else {
						errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60102",
								new String[] { percentage.toString() }, null), "");
					}

					if (errorDetails != null) {
						String errMsgs[] = errorDetails.getError().split("%");
						final String msg = errMsgs[0] + "% \n" + errMsgs[1];
						proceed = true;

						if (MessageUtil.confirm(msg) == MessageUtil.NO) {
							proceed = false;
						}
					}
				}

			}

			// Commitment Amount Validation with Available limit amount of the limit line assigned.
			if (limitDetails != null) {
				BigDecimal limitAmt = PennantApplicationUtil.formateAmount(
						limitDetails.getLimitSanctioned().subtract(limitDetails.getReservedLimit()), defaultCCYDecPos);
				if (this.cmtAmount.getActualValue().compareTo(limitAmt) > 0) {
					wve.add(new WrongValueException(this.cmtAmount,
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_CommitmentDialog_CmtAmount.value"),
											Labels.getLabel("label_CommitmentDialog_LimitLineAvailableAmt.value") })));
				}
			}
			if (this.cmtAmount.getValidateValue() != null) {
				aCommitment.setCmtAmount(
						PennantApplicationUtil.unFormateAmount(this.cmtAmount.getValidateValue(), defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Utilized Amount
		try {
			if (this.cmtUtilizedAmount.getValue() != null) {
				aCommitment.setCmtUtilizedAmount(
						PennantApplicationUtil.unFormateAmount(this.cmtUtilizedAmount.getValue(), defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Available Amount
		try {
			if (this.cmtAmount.getValidateValue() != null) {
				aCommitment.setCmtAvailable(aCommitment.getCmtAmount().subtract(aCommitment.getCmtUtilizedAmount()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Promised Date / Contract Date
		try {
			if (this.cmtPromisedDate.getValue() == null) {
				aCommitment.setCmtPromisedDate(appDate);
			} else {
				aCommitment.setCmtPromisedDate(this.cmtPromisedDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Start Date / First DrawdownDate
		try {
			if (this.cmtStartDate.getValue() == null) {
				if (aCommitment.getCmtPromisedDate() != null
						&& aCommitment.getCmtPromisedDate().compareTo(appDate) >= 0) {
					aCommitment.setCmtStartDate(aCommitment.getCmtPromisedDate());
				} else {
					aCommitment.setCmtStartDate(appDate);
				}
			} else {
				if (!this.cmtStartDate.isReadonly() && !this.cmtStartDate.isDisabled()) {

					if (this.cmtPromisedDate.getValue() != null
							&& this.cmtPromisedDate.getValue().after(this.cmtStartDate.getValue())) {
						throw new WrongValueException(this.cmtStartDate,
								Labels.getLabel("DATE_ALLOWED_ON_AFTER",
										new String[] { Labels.getLabel("label_CommitmentDialog_CmtStartDate.value"),
												Labels.getLabel("label_CommitmentDialog_CmtPromisedDate.value") }));

					} else {

						if (this.cmtStartDate.getValue().before(appDate)
								|| this.cmtStartDate.getValue().after(appEndDate)) {
							throw new WrongValueException(this.cmtStartDate,
									Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
											new String[] { Labels.getLabel("label_CommitmentDialog_CmtStartDate.value"),
													DateUtil.formatToShortDate(appDate),
													DateUtil.formatToShortDate(appEndDate) }));
						}
					}
				}

				aCommitment.setCmtStartDate(this.cmtStartDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Expire Date
		try {
			if (this.cmtExpDate.getValue() == null) {
				aCommitment.setCmtExpDate(appEndDate);
			} else {
				if (!this.cmtExpDate.isReadonly() && !this.cmtExpDate.isDisabled()) {

					if (limitDetails != null && limitDetails.getExpiryDate() != null) {
						if (limitDetails.getExpiryDate().before(this.cmtExpDate.getValue())) {
							throw new WrongValueException(this.cmtExpDate,
									Labels.getLabel("DATE_ALLOWED_ON_BEFORE", new String[] {
											Labels.getLabel("label_CommitmentDialog_CmtExpDate.value"),
											Labels.getLabel("label_CommitmentDialog_LimitLineExpDate.value") }));
						}

					} else if (this.cmtStartDate.getValue() != null
							&& this.cmtStartDate.getValue().after(this.cmtExpDate.getValue())) {
						throw new WrongValueException(this.cmtExpDate,
								Labels.getLabel("DATE_ALLOWED_ON_AFTER",
										new String[] { Labels.getLabel("label_CommitmentDialog_CmtExpDate.value"),
												Labels.getLabel("label_CommitmentDialog_CmtStartDate.value") }));

					} else if (this.cmtPromisedDate.getValue() != null
							&& this.cmtPromisedDate.getValue().after(this.cmtExpDate.getValue())) {
						throw new WrongValueException(this.cmtExpDate,
								Labels.getLabel("DATE_ALLOWED_ON_AFTER",
										new String[] { Labels.getLabel("label_CommitmentDialog_CmtExpDate.value"),
												Labels.getLabel("label_CommitmentDialog_CmtPromisedDate.value") }));

					} else {

						if (this.cmtExpDate.getValue().before(appDate)
								|| this.cmtExpDate.getValue().after(appEndDate)) {
							throw new WrongValueException(this.cmtExpDate,
									Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
											new String[] { Labels.getLabel("label_CommitmentDialog_CmtExpDate.value"),
													DateUtil.formatToShortDate(appDate),
													DateUtil.formatToShortDate(appEndDate) }));
						}
					}
				}

				aCommitment.setCmtExpDate(this.cmtExpDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment End Date
		try {
			if (this.cmtEndDate.getValue() == null) {
				if (aCommitment.getCmtExpDate() != null && aCommitment.getCmtExpDate().compareTo(appEndDate) <= 0) {
					aCommitment.setCmtEndDate(aCommitment.getCmtExpDate());
				} else {
					aCommitment.setCmtEndDate(appEndDate);
				}
			} else {
				if (!this.cmtEndDate.isReadonly() && !this.cmtEndDate.isDisabled()) {

					if (limitDetails != null && limitDetails.getExpiryDate() != null) {
						if (limitDetails.getExpiryDate().before(this.cmtEndDate.getValue())) {
							throw new WrongValueException(this.cmtEndDate,
									Labels.getLabel("DATE_ALLOWED_ON_BEFORE", new String[] {
											Labels.getLabel("label_CommitmentDialog_CmtEndDate.value"),
											Labels.getLabel("label_CommitmentDialog_LimitLineExpDate.value") }));
						}

					} else if (this.cmtExpDate.getValue() != null
							&& this.cmtExpDate.getValue().before(this.cmtEndDate.getValue())) {
						throw new WrongValueException(this.cmtEndDate,
								Labels.getLabel("DATE_ALLOWED_ON_BEFORE",
										new String[] { Labels.getLabel("label_CommitmentDialog_CmtEndDate.value"),
												Labels.getLabel("label_CommitmentDialog_CmtExpDate.value") }));

					} else if (this.cmtStartDate.getValue() != null
							&& this.cmtStartDate.getValue().after(this.cmtEndDate.getValue())) {
						throw new WrongValueException(this.cmtEndDate,
								Labels.getLabel("DATE_ALLOWED_ON_AFTER",
										new String[] { Labels.getLabel("label_CommitmentDialog_CmtEndDate.value"),
												Labels.getLabel("label_CommitmentDialog_CmtStartDate.value") }));

					} else if (this.cmtPromisedDate.getValue() != null
							&& this.cmtPromisedDate.getValue().after(this.cmtEndDate.getValue())) {
						throw new WrongValueException(this.cmtEndDate,
								Labels.getLabel("DATE_ALLOWED_ON_AFTER",
										new String[] { Labels.getLabel("label_CommitmentDialog_CmtEndDate.value"),
												Labels.getLabel("label_CommitmentDialog_CmtPromisedDate.value") }));
					} else {

						if (this.cmtEndDate.getValue().before(appDate)
								|| this.cmtEndDate.getValue().after(appEndDate)) {
							throw new WrongValueException(this.cmtEndDate,
									Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
											new String[] { Labels.getLabel("label_CommitmentDialog_CmtEndDate.value"),
													DateUtil.formatToShortDate(appDate),
													DateUtil.formatToShortDate(appEndDate) }));
						}
					}
				}

				aCommitment.setCmtEndDate(this.cmtEndDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Available Months
		try {
			if (!this.cmtAvailableMonths.isReadonly() && !this.cmtAvailableMonths.isDisabled()) {
				if (this.cmtExpDate.getValue() != null && (this.cmtExpDate.getValue().compareTo(appDate) > 0
						&& this.cmtExpDate.getValue().compareTo(appEndDate) < 0)) {
					if (this.cmtAvailableMonths.intValue() == 0) {

						throw new WrongValueException(this.cmtAvailableMonths, Labels.getLabel("FIELD_IS_MAND",
								new String[] { Labels.getLabel("label_CommitmentDialog_CmtAvailableMonths.value") }));
					}
				}
			}

			aCommitment.setCmtAvailableMonths(this.cmtAvailableMonths.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Renewal Date
		try {
			aCommitment.setCmtRvwDate(this.cmtRvwDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Charges
		try {
			aCommitment.setCmtCharges(
					PennantApplicationUtil.unFormateAmount(this.cmtCharges.getValidateValue(), defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment Charges Account
		try {
			if (this.cmtCharges.getActualValue() != null
					&& this.cmtCharges.getActualValue().compareTo(BigDecimal.ZERO) != 0
					&& StringUtils.isBlank(this.cmtChargesAccount.getValue())) {

				throw new WrongValueException(this.cmtChargesAccount, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CommitmentDialog_CmtChargesAccount.value") }));
			}
			aCommitment
					.setChargesAccount(PennantApplicationUtil.unFormatAccountNumber(this.cmtChargesAccount.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment ActiveStatus
		try {
			aCommitment.setActiveStatus(this.cmtActiveStatus.isChecked());
			aCommitment.setCmtActive(this.cmtActiveStatus.isChecked());
			aCommitment.setCmtStopRateRange(this.cmtStopRateRange.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Commitment NonperformingStatus
		try {
			aCommitment.setNonperformingStatus(this.cmtNonperformingStatus.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Notes
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
		// Shared Commitment
		try {
			aCommitment.setSharedCmt(this.sharedCmt.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// MultiBranch
		try {
			aCommitment.setMultiBranch(this.multiBranch.isChecked());
			if (wve.size() == 0) {
				// aCommitment.setCmtUtilizedAmount(BigDecimal.ZERO);
				aCommitment.setCmtAvailable(aCommitment.getCmtAmount().subtract(aCommitment.getCmtUtilizedAmount()));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Facility Reference
		try {
			aCommitment.setFacilityRefDesc(this.facilityRef.getDescription());
			if (this.facilityRef.getValue().equals("")) {
				// wve.add(new WrongValueException(this.facilityRef, Labels.getLabel("FIELD_NO_INVALID", new String[] {
				// Labels.getLabel("label_CommitmentDialog_FacilityRef.value") })));
			} else {
				if (!this.facilityRef.isReadonly()) {
					this.facilityRef.validateValue(false);
				}
			}
			aCommitment.setFacilityRef(this.facilityRef.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Limit Line
		try {
			Object object = this.limitLine.getAttribute("limitLineId");

			if (StringUtils.isEmpty(this.limitLine.getValue()) || object == null) {
				wve.add(new WrongValueException(this.limitLine, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_CommitmentDialog_LimitLine.value") })));
			} else {
				aCommitment.setLimitLineId(Long.valueOf(object.toString()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Collateral Required
		try {
			aCommitment.setCollateralRequired(this.collateralRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Additional fields added
		try {
			aCommitment.setBankingArrangement(this.bnkAggrmt.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCommitment.setLimitCondition(this.lmtCondition.getSelectedItem().getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCommitment.setExternalRef(this.reference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCommitment.setExternalRef1(this.reference1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCommitment.setTenor(this.tenor.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Commitment Rates
		Cloner cloner = new Cloner();
		aCommitment.setCommitmentRateList(cloner.deepClone(this.commitmentRateDetailList));

		try {
			if (wve.size() == 0) {
				aCommitment.getCommitmentMovement().setCmtReference(this.cmtReference.getValue());
				aCommitment.getCommitmentMovement().setFinReference("");
				aCommitment.getCommitmentMovement().setFinBranch("");
				aCommitment.getCommitmentMovement().setFinType("");
				aCommitment.getCommitmentMovement().setMovementDate(appDate);
				aCommitment.getCommitmentMovement().setCmtAmount(aCommitment.getCmtAmount());
				aCommitment.getCommitmentMovement().setCmtCharges(aCommitment.getCmtCharges());
				aCommitment.getCommitmentMovement().setCmtUtilizedAmount(aCommitment.getCmtUtilizedAmount());
				aCommitment.getCommitmentMovement().setCmtAvailable(aCommitment.getCmtAvailable());

				if (newMaintain) {
					// get previous commitment amount
					aCommitment.getCommitmentMovement()
							.setMovementAmount(aCommitment.getCmtAmount().subtract(oldCmtAmount));
				} else {
					if (aCommitment.isNewRecord()) {
						aCommitment.getCommitmentMovement().setMovementAmount(aCommitment.getCmtAmount());
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Show Wrong Value Exceptions
		showErrorDetails(wve, this.tab_CommitmentDetails);

		logger.debug("Leaving");
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	protected void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];

			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 */
	protected void appendCustomerDetailTab(boolean onLoad) {
		logger.debug("Entering");

		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_CUSTOMERS, true);
				tab_Customer = getTab(AssetConstants.UNIQUE_ID_CUSTOMERS);

			} else {
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_CUSTOMERS);
				Map<String, Object> map = getDefaultArguments();
				String pageName = PennantAppUtil.getCustomerPageName();
				map.put("customerDetails", getCommitment().getCustomerDetails());
				map.put("moduleType", PennantConstants.MODULETYPE_ENQ);

				Executions.createComponents(pageName, getTabpanel(AssetConstants.UNIQUE_ID_CUSTOMERS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Joint account and guaranteer Details Data in Commitment
	 */
	protected void appendAgreementsDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");

		boolean createTab = false;
		if (getCommitment().getAggrements() == null || getCommitment().getAggrements().isEmpty()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_AGREEMENT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_AGREEMENT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_AGREEMENT);
			if (getCommitment().getAggrements() != null && !getCommitment().getAggrements().isEmpty()) {
				final Map<String, Object> map = getDefaultArguments();
				map.put("agreementList", getCommitment().getAggrements());

				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENT), map);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param commitment
	 * @param finIsNewRecord
	 * @param map
	 */
	protected void appendCheckListDetailTab(Commitment commitment, boolean onLoadProcess) {
		logger.debug("Entering");

		boolean createTab = false;
		if (commitment.getCheckLists() != null && !commitment.getCheckLists().isEmpty()) {
			if (getTab(AssetConstants.UNIQUE_ID_CHECKLIST) == null) {
				createTab = true;
			}
		} else if (onLoadProcess) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_CHECKLIST, false);
		}
		if (onLoadProcess && commitment.getCheckLists() != null && !commitment.getCheckLists().isEmpty()) {
			boolean createcheckLsitTab = false;
			for (FinanceReferenceDetail chkList : commitment.getCheckLists()) {
				if (chkList.getShowInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
				if (chkList.getAllowInputInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
			}
			if (createcheckLsitTab) {
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_CHECKLIST);
				final Map<String, Object> map = getDefaultArguments();
				map.put("checkList", getCommitment().getCheckLists());
				map.put("finCheckRefList", getCommitment().getCommitmentCheckLists());

				checkListChildWindow = Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_CHECKLIST), map);
				Tab tab = getTab(AssetConstants.UNIQUE_ID_CHECKLIST);
				if (tab != null) {
					tab.setVisible(true);
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Collateral Details Data in Commitment
	 */
	protected void appendCollateralAssignmentTab() {
		logger.debug("Entering");

		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_COLLATERAL) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_COLLATERAL, false);
			tab_Collateral = getTab(AssetConstants.UNIQUE_ID_COLLATERAL);
		}

		clearTabpanelChildren(AssetConstants.UNIQUE_ID_COLLATERAL);
		final Map<String, Object> map = getDefaultArguments();
		map.put("collateralAssignmentList", getCommitment().getCollateralAssignmentList());
		map.put("customerId",
				StringUtils.trimToEmpty(this.custCIF.getValue()) == "" ? 0 : Long.valueOf(this.custCIF.getValue()));
		map.put("utilizedAmount", getCommitment().getCmtUtilizedAmount() == null ? BigDecimal.ZERO
				: getCommitment().getCmtUtilizedAmount());
		map.put("totalValue",
				getCommitment().getCmtAmount() == null ? BigDecimal.ZERO : getCommitment().getCmtAmount());

		collateralAssignmentWindow = Executions.createComponents(
				"/WEB-INF/pages/Finance/FinanceMain/CollateralHeaderDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_COLLATERAL), map);

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Document Details Data in Commitment
	 */
	protected void appendDocumentDetailTab() {
		logger.debug("Entering");

		createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);

		final Map<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getCommitment().getDocuments());

		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);

		logger.debug("Leaving");
	}

	/**
	 * Method for Append Recommend Details Tab
	 */
	protected void appendRecommendDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");

		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_RECOMMENDATIONS, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			Map<String, Object> map = getDefaultArguments();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", false);
			map.put("userRole", getRole());
			map.put("notes", getNotes(this.commitment));
			map.put("control", this);
			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS), map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$openAccount(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		// doCheckOpenAccount();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @throws InterruptedException
	 */
	private void doCheckOpenAccount() throws InterruptedException {
		logger.debug("Entering");

		this.custCIF.setErrorMessage("");
		this.cmtBranch.setErrorMessage("");
		this.cmtCcy.setErrorMessage("");

		if (this.openAccount.isChecked()) {

			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

			// Customer CIF
			try {
				if (StringUtils.isBlank(this.custCIF.getValue())) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CommitmentDialog_CustCIF.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// Commitment Branch
			try {
				if (StringUtils.isBlank(this.cmtBranch.getValidatedValue())) {
					throw new WrongValueException(this.cmtBranch, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CommitmentDialog_CmtBranch.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// Commitment Currency
			try {
				if (StringUtils.isBlank(this.cmtCcy.getValidatedValue())) {
					throw new WrongValueException(this.cmtCcy, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CommitmentDialog_CmtCcy.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (!wve.isEmpty()) {
				this.openAccount.setChecked(false);
				showErrorDetails(wve, this.tab_CommitmentDetails);
			}

			IAccounts accounts = getAccNumber();
			if (accounts != null) {
				this.cmtAccount.setValue(accounts.getAccountId());
				this.cmtAccount.setReadonly(true);
				this.cmtAccount.setMandatoryStyle(false);
			} else {
				this.openAccount.setChecked(false);
				MessageUtil.showError(Labels.getLabel("COMMITMENT_NEW_ACCOUNT_CREATION_ERROR"));
			}
		} else {
			this.cmtAccount.setValue("");
			this.cmtAccount.setReadonly(false);
			this.cmtAccount.setMandatoryStyle(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		doClearWrongValueMessages();

		// Commitment Reference
		if (!this.cmtReference.isReadonly()) {
			this.cmtReference
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtReference.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));

		}
		// Customer CIF
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CustCIF.value"), null, true, true));
		}

		// Commitment Title
		if (!this.cmtTitle.isReadonly()) {
			this.cmtTitle.setConstraint(new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtTitle.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, true));
		}

		// Commitment Profit Rate Min
		if (!this.cmtPftRateMin.isReadonly()) {
			this.cmtPftRateMin.setConstraint(
					new RateValidator(13, 9, Labels.getLabel("label_CommitmentDialog_CmtPftRateMin.value")));
		}
		// Commitment Profit Rate Max
		if (!this.cmtPftRateMax.isReadonly()) {
			this.cmtPftRateMax.setConstraint(
					new RateValidator(13, 9, Labels.getLabel("label_CommitmentDialog_CmtPftRateMax.value")));
		}
		// Commitment Amount
		if (!this.cmtAmount.isReadonly()) {
			this.cmtAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CommitmentDialog_CmtAmount.value"), defaultCCYDecPos, true, false, 0));
		}
		// Commitment Utilized Amount
		if (!this.cmtUtilizedAmount.isReadonly()) {
			this.cmtUtilizedAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtUtilizedAmount.value"),
							defaultCCYDecPos, false, false, 0));
		}

		// Available Amount
		/*
		 * if (!this.cmtAvailable.isReadonly()) { this.cmtAvailable.setConstraint(new
		 * PTDecimalValidator(Labels.getLabel("label_CommitmentDialog_CmtAvailable.value"), defaultCCYDecPos, false,
		 * false, 0)); }
		 */

		// Promised Date
		if (!this.cmtPromisedDate.isReadonly() && !this.cmtPromisedDate.isDisabled()) {
			this.cmtPromisedDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_CommitmentDialog_CmtPromisedDate.value"), false, appDate, appEndDate, true));
		}

		// CmtStart Date
		if (!this.cmtStartDate.isReadonly() && !this.cmtStartDate.isDisabled()) {
			// this.cmtStartDate.setConstraint(new
			// PTDateValidator(Labels.getLabel("label_CommitmentDialog_CmtStartDate.value"), false, null, appEndDate,
			// true));
		}

		// CmtEndDate
		if (!this.cmtEndDate.isReadonly() && !this.cmtEndDate.isDisabled()) {
			// this.cmtEndDate.setConstraint(new
			// PTDateValidator(Labels.getLabel("label_CommitmentDialog_CmtStartDate.value"), false, null, appEndDate,
			// true));
		}

		// CmtExp Date
		if (!this.cmtExpDate.isReadonly() && !this.cmtExpDate.isDisabled()) {
			// this.cmtExpDate.setConstraint(new
			// PTDateValidator(Labels.getLabel("label_CommitmentDialog_CmtExpDate.value"), false, null, appEndDate,
			// true));
		}

		// CmtRvwDate
		if (!this.cmtRvwDate.isReadonly() && !this.cmtRvwDate.isDisabled()) {
			this.cmtRvwDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_CommitmentDialog_CmtRvwDate.value"), true, appDate, appEndDate, true));
		}

		// Commitment Charges
		if (!this.cmtCharges.isReadonly()) {
			this.cmtCharges.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CommitmentDialog_CmtCharges.value"), defaultCCYDecPos, false, false, 0));
		}

		// Commitment Notes
		if (!this.cmtNotes.isReadonly()) {
			this.cmtNotes.setConstraint(new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtNotes.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		// CmtAvailableMonths
		if (!this.cmtAvailableMonths.isReadonly() && !this.cmtAvailableMonths.isDisabled()) {
			this.cmtAvailableMonths.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CommitmentDialog_CmtAvailableMonths.value"), false, false));
		}

		// Commitment Flags
		if (!this.btnSearchCommitmentFlags.isDisabled()) {
			this.commitmentFlags.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CommitmentDialog_CommitmentFlags.value"), null, false));
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

		this.commitmentFlags.setConstraint("");
		this.cmtRvwDate.setConstraint("");
		this.cmtEndDate.setConstraint("");
		this.cmtAvailableMonths.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug("Entering");

		// Commitment Branch
		if (cmtBranch.isButtonVisible()) {
			this.cmtBranch.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtBranch.value"), null, true, true));
		}

		// Commitment Currency
		if (cmtCcy.isButtonVisible()) {
			this.cmtCcy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtCcy.value"), null, true, true));
		}
		// Commitment Account
		if (!openAccount.isChecked() && !this.cmtAccount.isReadonly()) {
			this.cmtAccount.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommitmentDialog_CmtAccount.value"), null, true));
		}

		/*
		 * if (btnSearchCmtChargesAccount.isVisible() && this.cmtCharges.getValue() != null &&
		 * this.cmtCharges.getValue().compareTo(BigDecimal.ZERO) != 0) {
		 * this.cmtChargesAccountName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] {
		 * Labels.getLabel("label_CommitmentDialog_CmtChargesAccount.value") })); }
		 */

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug("Entering");

		this.custCIF.setConstraint("");
		this.cmtBranch.setConstraint("");
		this.cmtCcy.setConstraint("");
		this.cmtAccount.setConstraint("");
		// this.cmtChargesAccountName.setConstraint("");
		this.limitLine.setConstraint("");
		this.facilityRef.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.cmtReference.setErrorMessage("");
		this.cmtTitle.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		Clients.clearWrongValue(this.btnSearchCustCIF);
		this.cmtBranch.setErrorMessage("");
		this.cmtAccount.setErrorMessage("");
		this.cmtAvailable.setErrorMessage("");
		this.cmtCcy.setErrorMessage("");
		this.cmtAmount.setErrorMessage("");
		Clients.clearWrongValue(this.limitLine);
		this.facilityRef.setErrorMessage("");
		this.cmtPftRateMin.setErrorMessage("");
		this.cmtPftRateMax.setErrorMessage("");
		this.commitmentFlags.setErrorMessage("");
		this.cmtAvailableMonths.setErrorMessage("");
		this.cmtPromisedDate.setErrorMessage("");
		this.cmtRvwDate.setErrorMessage("");

		doClearWrongValueMessages();
		logger.debug("Leaving");
	}

	private void doClearWrongValueMessages() {
		logger.debug("Entering");

		this.cmtStartDate.clearErrorMessage();
		this.cmtEndDate.clearErrorMessage();
		this.cmtExpDate.clearErrorMessage();

		logger.debug("Leaving");
	}

	/**
	 * Reset the values when customer changed
	 */
	private void doResetValues() {
		logger.debug("Entering");

		this.cmtChargesAccount.setCustCIF("");
		this.cmtChargesAccount.setValue("");
		this.limitLine.setValue("", "");
		this.facilityRef.setValue("", "");
		this.custName.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	protected void refreshList() {
		logger.debug("Entering");

		getCommitmentListCtrl().search();

		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Commitment aCommitment = new Commitment();
		BeanUtils.copyProperties(getCommitment(), aCommitment);

		final String keyReference = Labels.getLabel("label_CommitmentDialog_CmtReference.value") + " : "
				+ aCommitment.getCmtReference();

		doDelete(keyReference, aCommitment);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final Commitment aCommitment) {
		String tranType = PennantConstants.TRAN_WF;
		if (StringUtils.isBlank(aCommitment.getRecordType())) {
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

				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.cmtReference.setValue("");
		this.custCIF.setValue("");
		this.cmtBranch.setDescription("");
		this.openAccount.setChecked(false);
		this.cmtAccount.setValue("");
		this.cmtCcy.setValue("");
		this.cmtCcy.setDescription("");
		this.cmtPftRateMin.setValue(BigDecimal.ZERO);
		this.cmtPftRateMax.setValue(BigDecimal.ZERO);
		this.cmtAmount.setValue("");
		this.cmtUtilizedAmount.setValue("");
		this.cmtAvailable.setValue("");
		this.cmtPromisedDate.setText("");
		this.cmtStartDate.setText("");
		this.cmtExpDate.setText("");
		this.cmtCharges.setValue("");
		this.cmtChargesAccount.setValue("");
		// this.cmtChargesAccountName.setValue("");
		this.cmtActiveStatus.setChecked(false);
		this.cmtNonperformingStatus.setChecked(false);
		this.cmtTitle.setValue("");
		this.cmtNotes.setValue("");
		this.revolving.setChecked(false);
		this.sharedCmt.setChecked(false);
		this.multiBranch.setChecked(false);
		this.cmtStopRateRange.setChecked(false);
		this.facilityRef.setValue("");
		this.facilityRef.setDescription("");
		this.limitLine.setValue("");
		this.limitLine.setDescription("");

		this.commitmentFlags.setValue("");
		this.cmtAvailableMonths.setText("");
		this.cmtRvwDate.setText("");
		this.cmtEndDate.setText("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");

		final Commitment aCommitment = new Commitment();
		BeanUtils.copyProperties(getCommitment(), aCommitment);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aCommitment.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			aCommitment.getCommitmentMovement().setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aCommitment.getNextTaskId(), aCommitment);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aCommitment.getRecordType()) && isValidation()
				&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
				&& !"Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
				&& !"Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

			// Validations
			doSetValidation();

			// fill the Commitment object with the components data
			doWriteComponentsToBean(aCommitment);
			if (!proceed) {
				return;
			}

			// Commitment Flags
			fetchFlagDetals();
			if (getCmtFlagsDetailList() != null && !getCmtFlagsDetailList().isEmpty()) {
				aCommitment.setCmtFlagDetailList(getCmtFlagsDetailList());
			} else {
				aCommitment.setCmtFlagDetailList(null);
			}

			// Commitment CheckList Details Saving
			if (checkListChildWindow != null) {
				boolean validationSuccess = doSave_CheckList(aCommitment, false);
				if (!validationSuccess) {
					return;
				}
			} else {
				aCommitment.setCheckLists(null);
			}

			// Collateral Assignment Details
			if (aCommitment.isCollateralRequired()) {
				if (collateralHeaderDialogCtrl != null) {
					aCommitment.setCollateralAssignmentList(collateralHeaderDialogCtrl.getCollateralAssignments());
				} else {
					aCommitment.setCollateralAssignmentList(null);
				}
			} else {
				if (aCommitment.getCollateralAssignmentList() != null
						&& aCommitment.getCollateralAssignmentList().size() > 0) {
					for (CollateralAssignment collateralAssignment : aCommitment.getCollateralAssignmentList()) {

						if (StringUtils.isBlank(collateralAssignment.getRecordType())) {
							collateralAssignment.setVersion(collateralAssignment.getVersion() + 1);
							collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							collateralAssignment.setNewRecord(true);

						} else if (collateralAssignment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						}
					}
				}
			}

			// Document Details Saving
			if (documentDetailDialogCtrl != null) {
				aCommitment.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
			} else {
				aCommitment.setDocuments(null);
			}
		}

		isNew = aCommitment.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCommitment.getRecordType())) {
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
				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE); // FIXME Check with siva
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);

					notification.setModule("COMMITMENT");
					notification.setSubModule(FinServiceEvent.ORG);
					notification.setKeyReference(aCommitment.getCmtReference());
					notification.setStage(aCommitment.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());

					String finType = CommitmentConstants.WF_NEWCOMMITMENT;
					if (StringUtils.isEmpty(commitment.getRecordType())) {
						finType = CommitmentConstants.WF_MAINTAINCOMMITMENT;
					}

					notificationService.sendNotifications(notification, aCommitment, finType, null);
				}

				// User Notifications Message/Alert
				publishNotification(Notify.ROLE, aCommitment.getCmtReference(), aCommitment);

				// List Detail Refreshment
				refreshList();

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aCommitment.getNextTaskId())) {
					aCommitment.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aCommitment.getRoleCode(),
						aCommitment.getNextRoleCode(), aCommitment.getCmtReference(), " Commitment ",
						aCommitment.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Closing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method set the check list details to aCommitment
	 * 
	 * @param aCommitment
	 */
	protected boolean doSave_CheckList(Commitment aCommitment, boolean isForAgreementGen) {
		logger.debug("Entering ");

		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("financeMainDialogCtrl", this);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());

		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
		}
		try {
			financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getCommitmentChecklists();
		selAnsCountMap = getSelectedAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			aCommitment.setCommitmentCheckLists(chkList);
			aCommitment.setSelAnsCountMap(selAnsCountMap);
		}

		logger.debug("Leaving ");
		return validationSuccess;

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

	protected boolean doProcess(Commitment aCommitment, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		aCommitment.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCommitment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommitment.setUserDetails(getUserWorkspace().getLoggedInUser());
		aCommitment.setCustID(aCommitment.getCustomerDetails().getCustID());

		aCommitment.getCommitmentMovement().setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCommitment.getCommitmentMovement().setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommitment.getCommitmentMovement().setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
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

			if (StringUtils.isBlank(getOperationRefs())) {
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Commitment aCommitment = (Commitment) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
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
					auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
							Labels.getLabel("InvalidWorkFlowMethod"), null, null));

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
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Commitment aCommitment, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommitment.getBefImage(), aCommitment);
		return new AuditHeader(aCommitment.getCmtReference(), null, null, null, auditDetail,
				aCommitment.getUserDetails(), getOverideMap());
	}

	@Override
	public String getReference() {
		return this.cmtReference.getValue();
	}

	public void onClick$btnSearchCmtChargesAccount(Event event) {
		logger.debug("Entering " + event.toString());

		this.custCIF.clearErrorMessage();
		this.cmtBranch.clearErrorMessage();
		this.cmtCcy.clearErrorMessage();
		doValidateAccount();
		Object dataObject;
		List<IAccounts> iAccountList = new ArrayList<IAccounts>();
		IAccounts iAccount = new IAccounts();
		iAccount.setAcCcy(this.cmtCcy.getValue());
		iAccount.setAcType("");
		iAccount.setAcCustCIF(this.custCIF.getValue());
		iAccount.setDivision(getDivisionByBranch());
		try {
			dataObject = ExtendedSearchListBox.show(this.window_CommitmentDialog, "Accounts", iAccountList);
			if (dataObject instanceof String) {
				this.cmtChargesAccount.setValue("");
			} else {
				IAccounts details = (IAccounts) dataObject;
				if (details != null) {
					this.cmtChargesAccount.setValue(details.getAccountId());
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError("Account Details not Found!!!");
		}

		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnSearchCmtAccount(Event event) {
		logger.debug("Entering " + event.toString());

		this.custCIF.clearErrorMessage();
		if (StringUtils.isNotBlank(this.custCIF.getValue())) {
			Object dataObject;
			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.cmtCcy.getValue());
			iAccount.setAcType(SysParamUtil.getValueAsString("COMMITMENT_AC_TYPE"));
			iAccount.setAcCustCIF(this.custCIF.getValue());
			iAccount.setDivision(this.cmtBranch.getValue());

			try {
				dataObject = ExtendedSearchListBox.show(this.window_CommitmentDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.cmtAccount.setValue("");
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.cmtAccount.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
				MessageUtil.showError("Account Details not Found!!!");
			}
		} else {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CommitmentDialog_CustCIF.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCommitmentFlags(Event event) {
		logger.debug("Entering  " + event.toString());

		this.commitmentFlags.setErrorMessage("");
		Object dataObject = MultiSelectionSearchListBox.show(this.window_CommitmentDialog, "Flag",
				this.commitmentFlags.getValue(), null);

		if (dataObject != null) {
			String details = (String) dataObject;
			this.commitmentFlags.setValue(details);
		}

		logger.debug("Leaving  " + event.toString());

	}

	private void doValidateAccount() {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.isBlank(this.custCIF.getValue())) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CommitmentDialog_CustCIF.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isBlank(this.cmtBranch.getValue())) {
				throw new WrongValueException(this.cmtBranch, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CommitmentDialog_CmtBranch.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isBlank(this.cmtCcy.getValue())) {
				throw new WrongValueException(this.cmtCcy, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CommitmentDialog_CmtCcy.value") }));
			}
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

	private String getDivisionByBranch() {
		return FinanceConstants.FIN_DIVISION_RETAIL;
	}

	private CommitmentRuleData prepareDate() {
		logger.debug("Entering prepareDate()");

		CommitmentRuleData commitmentRuleData = new CommitmentRuleData();
		commitmentRuleData.setCmtAmount(this.cmtAmount.getActualValue());

		logger.debug("Leaving prepareDate()");
		return commitmentRuleData;
	}

	private String executeRule(String ruleCode, CommitmentRuleData ruleObject) {
		logger.debug("Entering");

		Map<String, Object> dataMap = new HashMap<>();
		Object result = "0";
		try {
			// Add fields and values
			for (String filed : ruleObject.getDeclaredFieldsAndValue().keySet()) {
				dataMap.put(filed, ruleObject.getDeclaredFieldsAndValue().get(filed));
			}

			result = RuleExecutionUtil.executeRule(ruleCode, dataMap, null, RuleReturnType.DECIMAL);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
		return result.toString();
	}

	private void CaluculateSummary() {
		logger.debug("Entering");

		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {
			List<CommitmentSummary> list = getCommitmentService().getCommitmentSummary(this.custID.longValue());
			doFillCommitmentSummary(list);
		}

		logger.debug("Leaving");

	}

	private void doFillCommitmentSummary(List<CommitmentSummary> commitmentSummaries) {
		logger.debug("Entering");

		this.commitmentSummary.getItems().clear();
		if (commitmentSummaries != null && commitmentSummaries.size() > 0) {
			for (CommitmentSummary commitmentSummary : commitmentSummaries) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(commitmentSummary.getCmtCcy());
				listcell.setParent(listitem);
				listcell = new Listcell(String.valueOf(commitmentSummary.getTotCommitments()));
				listcell.setParent(listitem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentSummary.getTotComtAmount(),
						commitmentSummary.getCcyEditField()));
				listcell.setParent(listitem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentSummary.getTotUtilizedAmoun(),
						commitmentSummary.getCcyEditField()));
				listcell.setParent(listitem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(
						commitmentSummary.getTotComtAmount().subtract(commitmentSummary.getTotUtilizedAmoun()),
						commitmentSummary.getCcyEditField()));
				listcell.setParent(listitem);

				this.commitmentSummary.appendChild(listitem);
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$cmtAmount(Event event) {
		logger.debug("Entering");

		calculateCharges();

		logger.debug("Leaving");
	}

	public void onFulfill$cmtCharges(Event event) {
		logger.debug("Entering");

		calculateCharges();

		logger.debug("Leaving");
	}

	private void calculateCharges() {
		logger.debug("Entering");

		if (this.cmtAmount.getActualValue() != null && this.cmtCharges.getActualValue() == null) {
			if (this.cmtAmount.getActualValue().compareTo(BigDecimal.ZERO) != 0) {
				String result = null;
				List<Rule> list = null;
				if (maintain) {
					list = getCommitmentService().getRuleByModuleAndEvent(RuleConstants.MODULE_FEES,
							RuleConstants.EVENT_MNTCMT);
				} else {
					list = getCommitmentService().getRuleByModuleAndEvent(RuleConstants.MODULE_FEES,
							RuleConstants.EVENT_NEWCMT);
				}
				if (list != null && list.size() > 0) {
					Rule rule = list.get(0);
					result = executeRule(rule.getSQLRule(), prepareDate());
				}
				if (StringUtils.isNotBlank(result)) {
					this.cmtCharges.setValue(new BigDecimal(result));
				}
			}
		}

		if (this.cmtCharges.getActualValue() != null
				&& this.cmtCharges.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			this.cmtChargesAccount.setMandatoryStyle(true);
		} else {
			this.cmtChargesAccount.setMandatoryStyle(false);
			Clients.clearWrongValue(this.cmtChargesAccount);
		}

		logger.debug("Leaving");
	}

	/**
	 * onChange Event For cmtPftRateMin
	 */
	public void onChange$cmtPftRateMin(Event event) {
		logger.debug("Entering :" + event.toString());

		if (this.cmtPftRateMin.getValue() != null && this.cmtPftRateMin.getValue().compareTo(BigDecimal.ZERO) > 0) {
			this.space_CmtPftRateMax.setSclass("mandatory");
		} else {
			this.space_CmtPftRateMax.setSclass("");
		}

		if ((this.cmtPftRateMin.getValue() != null && this.cmtPftRateMin.getValue().compareTo(BigDecimal.ZERO) > 0)
				|| (this.cmtPftRateMax.getValue() != null
						&& this.cmtPftRateMax.getValue().compareTo(BigDecimal.ZERO) > 0)) {

			this.cmtStopRateRange.setDisabled(false);
		} else {
			this.cmtStopRateRange.setChecked(false);
			this.cmtStopRateRange.setDisabled(true);
		}

		logger.debug("Leaving :" + event.toString());
	}

	/**
	 * onChange Event For cmtPftRateMax
	 */
	public void onChange$cmtPftRateMax(Event event) {
		logger.debug("Entering :" + event.toString());

		if ((this.cmtPftRateMax.getValue() != null && this.cmtPftRateMax.getValue().compareTo(BigDecimal.ZERO) > 0)
				|| (this.cmtPftRateMin.getValue() != null
						&& this.cmtPftRateMin.getValue().compareTo(BigDecimal.ZERO) > 0)) {

			this.cmtStopRateRange.setDisabled(false);
		} else {
			this.cmtStopRateRange.setChecked(false);
			this.cmtStopRateRange.setDisabled(true);
		}

		logger.debug("Leaving :" + event.toString());
	}

	/**
	 * onChange Event For cmtExpDate
	 */
	public void onChange$cmtExpDate(Event event) {
		logger.debug("Entering :" + event.toString());

		this.cmtExpDate.clearErrorMessage();
		this.cmtAvailableMonths.clearErrorMessage();

		if (this.cmtExpDate.getValue() != null && (this.cmtExpDate.getValue().compareTo(appDate) > 0
				&& this.cmtExpDate.getValue().compareTo(appEndDate) < 0)) {

			this.space_CmtAvailableMonths.setSclass("mandatory");
			this.cmtAvailableMonths.setReadonly(false);
		} else {
			this.cmtAvailableMonths.setValue(0);
			this.space_CmtAvailableMonths.setSclass("");
			this.cmtAvailableMonths.setReadonly(true);
		}

		logger.debug("Leaving :" + event.toString());
	}

	private void getFinaceDetails(String cmtReference) {
		logger.debug("Entering");

		JdbcSearchObject<FinanceMain> searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		searchObj.addSort("FinReference", false);
		searchObj.addTabelName("FinanceMain_AView");
		searchObj.addFilterEqual("FinCommitmentRef", cmtReference);
		doFillCommitmentFinace(getPagedListService().getBySearchObject(searchObj));

		logger.debug("Leaving");
	}

	private void doFillCommitmentFinace(List<FinanceMain> financeMains) {
		logger.debug("Entering");

		if (financeMains != null && financeMains.size() > 0) {
			for (FinanceMain financeMain : financeMains) {
				Listitem item = new Listitem();
				// final FinanceMain wIFFinanceMain = (FinanceMain) data;
				Listcell lc;
				lc = new Listcell(financeMain.getFinReference());
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinType());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(financeMain.getFinAmount(),
						CurrencyUtil.getFormat(financeMain.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinCcy());
				lc.setParent(item);
				lc = new Listcell(financeMain.getScheduleMethod());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(financeMain.getNumberOfTerms()));
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
				lc.setParent(item);
				if (financeMain.getGrcPeriodEndDate() != null) {
					lc = new Listcell(DateUtil.formatToLongDate(financeMain.getGrcPeriodEndDate()));
				} else {
					lc = new Listcell();
				}
				lc.setParent(item);
				if (financeMain.getMaturityDate() != null) {
					lc = new Listcell(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
				} else {
					lc = new Listcell();
				}
				lc.setParent(item);
				item.setAttribute("data", financeMain);
				item.addForward("onDoubleClick", this.window_CommitmentDialog, "onFinanceMainItemDoubleClicked",
						financeMain.getFinReference());
				this.listBoxCommitmentFinance.appendChild(item);
			}
		}

		logger.debug("Leaving");
	}

	public void onFinanceMainItemDoubleClicked(Event event) {
		Object object = event.getData();
		if (object != null) {
			getCommitmentMovementDetails(this.cmtReference.getValue(), object.toString());
		}
	}

	private void getCommitmentMovementDetails(String cmtReference, String finReference) {
		logger.debug("Entering");

		JdbcSearchObject<CommitmentMovement> searchObject = new JdbcSearchObject<CommitmentMovement>(
				CommitmentMovement.class);
		searchObject.addTabelName("CommitmentMovements");
		searchObject.addFilterEqual("CmtReference", cmtReference);
		searchObject.addSortDesc("MovementDate, MovementOrder");
		List<CommitmentMovement> commitmentMovements = getPagedListService().getBySearchObject(searchObject);
		doFillCommitmovements(commitmentMovements);

		logger.debug("Leaving");
	}

	private void doFillCommitmovements(List<CommitmentMovement> commitmentMovements) {
		logger.debug("Entering");

		this.listBoxCommitmentMovement.getItems().clear();
		if (commitmentMovements != null && commitmentMovements.size() > 0) {
			for (CommitmentMovement commitmentMovement : commitmentMovements) {
				Listitem listItem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(PennantApplicationUtil.getLabelDesc(commitmentMovement.getMovementType(),
						PennantStaticListUtil.getCmtMovementTypes()));
				listcell.setParent(listItem);
				listcell = new Listcell(String.valueOf(commitmentMovement.getMovementOrder()));
				listcell.setParent(listItem);
				listcell = new Listcell(commitmentMovement.getFinReference());
				listcell.setParent(listItem);
				listcell = new Listcell(DateUtil.formatToLongDate(commitmentMovement.getMovementDate()));
				listcell.setParent(listItem);
				listcell = new Listcell(
						PennantApplicationUtil.amountFormate(commitmentMovement.getMovementAmount(), defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(
						PennantApplicationUtil.amountFormate(commitmentMovement.getCmtAmount(), defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(commitmentMovement.getCmtUtilizedAmount(),
						defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(
						PennantApplicationUtil.amountFormate(commitmentMovement.getCmtAvailable(), defaultCCYDecPos));
				listcell.setParent(listItem);
				listcell = new Listcell(
						PennantApplicationUtil.amountFormate(commitmentMovement.getCmtCharges(), defaultCCYDecPos));
				listcell.setParent(listItem);
				this.listBoxCommitmentMovement.appendChild(listItem);
			}

		}
		logger.debug("Leaving");
	}

	public class compareLinkedTransId implements Comparator<ReturnDataSet>, Serializable {
		private static final long serialVersionUID = -3639465555049007637L;

		public compareLinkedTransId() {

		}

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

		private static final long serialVersionUID = 3413747054505038584L;

		public postingGroupListModelItemRenderer() {

		}

		@Override
		public void render(Listitem item, ReturnDataSet returnDataSet, int count) {
			logger.debug("Entering");

			if (item instanceof Listgroup) {
				Listcell lc;
				lc = new Listcell(String.valueOf(returnDataSet.getLovDescEventCodeName()));
				lc.setStyle("cursor:default;");
				lc.setSpan(7);
				lc.setParent(item);
			} else {
				Listcell listcell;
				listcell = new Listcell("");
				listcell.setParent(item);
				listcell = new Listcell(PennantApplicationUtil.getLabelDesc(returnDataSet.getDrOrCr(),
						PennantStaticListUtil.getTranType()));
				listcell.setParent(item);
				listcell = new Listcell(returnDataSet.getTranDesc());
				listcell.setParent(item);
				listcell = new Listcell(DateUtil.formatToLongDate(returnDataSet.getPostDate()));
				listcell.setParent(item);
				listcell = new Listcell(returnDataSet.getFinReference());
				listcell.setParent(item);
				listcell = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
				listcell.setParent(item);
				listcell = new Listcell(
						PennantApplicationUtil.amountFormate(returnDataSet.getPostAmount(), defaultCCYDecPos));
				listcell.setParent(item);
			}
			logger.debug("Leaving");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getCommitmentPostingDetails(String cmtReference) {
		logger.debug("Entering");

		JdbcSearchObject<ReturnDataSet> searchObject = new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		searchObject.addTabelName("Postings_View");
		searchObject.addSortDesc("LinkedTranId");
		searchObject.addFilterEqual("FinReference", cmtReference);

		this.listBoxCommitmentPostings.setItemRenderer(new postingGroupListModelItemRenderer());
		this.listBoxCommitmentPostings.setModel(new GroupsModelArray(
				getPagedListService().getBySearchObject(searchObject).toArray(), new compareLinkedTransId()));

		logger.debug("Leaving");
	}

	private void doDesignByMode() {
		logger.debug("Entering doDesignByMode()");

		this.row_Status.setVisible(maintain);
		if (maintain) {
			this.label_windowTitle.setValue(Labels.getLabel("window_MaintainCommitmentDialog.title"));
			this.cmtTitle.setReadonly(true);
			setStyle(this.cmtTitle);
			this.custCIF.setReadonly(true);
			this.btnSearchCustCIF.setDisabled(true);
			this.cmtBranch.setReadonly(true);
			this.cmtCcy.setReadonly(true);
			this.limitLine.setReadonly(true);

			this.openAccount.setDisabled(true);
			this.multiBranch.setDisabled(true);
			this.cmtPromisedDate.setDisabled(true);
			this.cmtStartDate.setDisabled(true);
			this.revolving.setDisabled(true);
			this.sharedCmt.setDisabled(true);
			this.rowCmtSummary.setVisible(false);
			this.rowCmtCount.setVisible(false);

			if (!isReadOnly("CommitmentDialog_CmtReference")
					&& StringUtils.trimToEmpty(this.facilityRef.getValue()).equals("")) {
				this.facilityRef.setReadonly(false);
			}

			if (!isReadOnly("CommitmentDialog_CmtReference") && StringUtils.isBlank(this.limitLine.getValue())) {
				this.limitLine.setReadonly(false);
			}
		} else {
			this.label_windowTitle.setValue(Labels.getLabel("window_NewCommitmentDialog.title"));
		}

		if (this.openAccount.isChecked()) {
			this.cmtAccount.setMandatoryStyle(false);
			this.cmtAccount.setReadonly(true);
		} else {
			this.cmtAccount.setMandatoryStyle(!isReadOnly("CommitmentDialog_CmtAccount"));
		}

		logger.debug("Leaving doDesignByMode()");
	}

	private void doCheckEnq() {
		logger.debug("Entering doCheckEnq()");

		if (enqiryModule) {
			this.cmtTitle.setReadonly(true);
			this.custCIF.setReadonly(true);
			this.btnSearchCustCIF.setDisabled(true);
			this.cmtBranch.setReadonly(true);
			this.cmtCcy.setReadonly(true);
			this.openAccount.setDisabled(true);
			this.cmtAccount.setReadonly(true);
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
			this.cmtNotes.setReadonly(true);
			this.cmtActiveStatus.setDisabled(true);
			this.cmtNonperformingStatus.setDisabled(true);
			this.cmtEndDate.setDisabled(true);
			this.cmtRvwDate.setDisabled(true);
			this.cmtAvailableMonths.setDisabled(true);
			this.commitmentFlags.setReadonly(true);
			this.btnSearchCommitmentFlags.setDisabled(true);
			this.collateralRequired.setDisabled(true);
			this.tab_CommitmentMovementDetails.setVisible(true);
			this.tab_CommitmentPostingDetails.setVisible(true);
			this.label_windowTitle.setValue(Labels.getLabel("window_EnqCommitmentDialog.title"));
			this.rowCmtSummary.setVisible(false);
			this.rowCmtCount.setVisible(false);
			this.cmtChargesAccount.setReadonly(true);
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
		decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(decPos));
		decimalbox.setScale(decPos);
	}

	private IAccounts getAccNumber() {
		logger.debug("Entering");

		try {
			String acType = SysParamUtil.getValueAsString("COMMITMENT_AC_TYPE");
			List<IAccounts> iAccountList = new ArrayList<IAccounts>(1);
			IAccounts newAccount = new IAccounts();
			newAccount.setAcCustCIF(this.custCIF.getValue());
			newAccount.setAcBranch(this.cmtBranch.getValue());
			newAccount.setAcCcy(this.cmtCcy.getValue());
			newAccount.setAcType(acType);
			newAccount.setFlagCreateIfNF(true);
			newAccount.setFlagCreateNew(true);
			newAccount.setInternalAc(false);
			newAccount.setTransOrder("");
			newAccount.setDivision(getDivisionByBranch());
			iAccountList.add(newAccount);
			return newAccount;
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method Used for set list of values been class to components Commitment flags list
	 * 
	 * @param Commitment
	 */
	private void doFillCommitmentFlagDetails(List<FinFlagsDetail> flagDetails) {
		logger.debug("Entering");

		setCmtFlagsDetailList(flagDetails);
		if (flagDetails == null || flagDetails.isEmpty()) {
			return;
		}

		String tempflagcode = "";
		for (FinFlagsDetail flagDetail : flagDetails) {
			if (!StringUtils.equals(flagDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempflagcode)) {
					tempflagcode = flagDetail.getFlagCode();
				} else {
					tempflagcode = tempflagcode.concat(",").concat(flagDetail.getFlagCode());
				}
			}
		}
		this.commitmentFlags.setValue(tempflagcode);

		logger.debug("Entering");
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param cmtFlagDetailList
	 */
	private void fetchFlagDetals() {
		logger.debug("Entering");

		Map<String, FinFlagsDetail> flagMap = new HashMap<>();
		List<String> finFlagList = Arrays.asList(this.commitmentFlags.getValue().split(","));

		if (this.cmtFlagsDetailList == null) {
			this.cmtFlagsDetailList = new ArrayList<>();
		}

		for (FinFlagsDetail flagDetail : cmtFlagsDetailList) {
			flagMap.put(flagDetail.getFlagCode(), flagDetail);
		}
		for (String flagCode : finFlagList) {
			if (StringUtils.isEmpty(flagCode)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (flagMap.containsKey(flagCode)) {
				// Do Nothing

				// Removing from map to identify existing modifications
				boolean isDelete = false;
				if (this.userAction.getSelectedItem() != null) {
					if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
						isDelete = true;
					}
				}

				if (!isDelete) {
					flagMap.remove(flagCode);
				}
			} else {
				FinFlagsDetail aCmtFlagsDetail = new FinFlagsDetail();
				aCmtFlagsDetail.setFlagCode(flagCode);
				aCmtFlagsDetail.setModuleName(CommitmentConstants.MODULE_NAME);
				aCmtFlagsDetail.setNewRecord(true);
				aCmtFlagsDetail.setVersion(1);
				aCmtFlagsDetail.setRecordType(PennantConstants.RCD_ADD);

				this.cmtFlagsDetailList.add(aCmtFlagsDetail);
			}
		}

		// Removing unavailable records from DB by using Work flow details
		if (flagMap.size() > 0) {
			for (FinFlagsDetail cmtFlagsDetail : cmtFlagsDetailList) {
				if (flagMap.containsKey(cmtFlagsDetail.getFlagCode())) {

					if (StringUtils.isBlank(cmtFlagsDetail.getRecordType())) {
						cmtFlagsDetail.setNewRecord(true);
						cmtFlagsDetail.setVersion(cmtFlagsDetail.getVersion() + 1);
						cmtFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else {
						if (!StringUtils.equals(cmtFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
							cmtFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						}
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnNew_CmtRate(Event event) {
		logger.debug("Entering");

		CommitmentRate commitmentRate = new CommitmentRate();
		commitmentRate.setNewRecord(true);
		commitmentRate.setWorkflowId(0);

		doSetValidation();// TODO
		doWriteComponentsToBean(getCommitment());

		commitmentRate.setCmtReference(this.cmtReference.getValue());
		commitmentRate.setCmtCcy(this.cmtCcy.getValue());
		commitmentRate.setCmtPftRateMin(this.cmtPftRateMin.getValue());
		commitmentRate.setCmtPftRateMax(this.cmtPftRateMax.getValue());

		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("commitmentRate", commitmentRate);
		map.put("commitmentDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		// map.put("isFinanceProcess", isFinanceProcess);

		try {
			Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentRateDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onCommitmentRateItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCmtRates.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final CommitmentRate commitmentRate = (CommitmentRate) item.getAttribute("data");

			if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, commitmentRate.getRecordType())
					|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, commitmentRate.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));

			} else {
				if (!enqiryModule) {
					doSetValidation();// TODO
					doWriteComponentsToBean(getCommitment());
				}
				commitmentRate.setCmtReference(this.cmtReference.getValue());
				commitmentRate.setCmtCcy(this.cmtCcy.getValue());
				commitmentRate.setCmtPftRateMin(this.cmtPftRateMin.getValue());
				commitmentRate.setCmtPftRateMax(this.cmtPftRateMax.getValue());

				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("commitmentRate", commitmentRate);
				map.put("commitmentDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enqiryModule", enqiryModule);
				// map.put("isFinanceProcess", isFinanceProcess);

				try {
					Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentRateDialog.zul", null,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param commitmentRates
	 */
	public void doFillCommitmentRateDetails(List<CommitmentRate> commitmentRates) {
		logger.debug("Entering");

		this.listBoxCmtRates.getItems().clear();
		if (commitmentRates != null) {
			for (CommitmentRate commitmentRate : commitmentRates) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(commitmentRate.getCmtRvwFrq());
				lc.setParent(item);
				lc = new Listcell(commitmentRate.getCmtBaseRate());
				lc.setParent(item);

				if (commitmentRate.getCmtBaseRate() != null) {
					lc = new Listcell(PennantApplicationUtil.formatRate(
							(commitmentRate.getCmtMargin() == null ? BigDecimal.ZERO : commitmentRate.getCmtMargin())
									.doubleValue(),
							2));
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else {
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.formatRate(commitmentRate.getCmtActualRate().doubleValue(), 2));// PennantConstants.rateFormate
																													// //
																													// SATYA
					lc.setParent(item);
				}
				lc = new Listcell(
						PennantApplicationUtil.formatRate(commitmentRate.getCmtCalculatedRate().doubleValue(), 2));
				lc.setParent(item);

				item.setAttribute("data", commitmentRate);
				ComponentsCtrl.applyForward(item, "onDoubleClick = onCommitmentRateItemDoubleClicked");
				this.listBoxCmtRates.appendChild(item);
			}
			setCommitmentRateDetailList(commitmentRates);
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	public void setAgreementDetailTab() {// TODO
		Tab tab = getTab(AssetConstants.UNIQUE_ID_AGREEMENT);
		if (tab != null) {
			if (!getCommitment().getAggrements().isEmpty()) {
				tab.setVisible(true);
				ComponentsCtrl.applyForward(tab, selectMethodName);
			} else {
				tab.setVisible(false);
			}
		}
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");

		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", CommitmentConstants.MODULE_NAME);
		return map;
	}

	/**
	 * fill Commitment basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.custCIF.getValue());
		arrayList.add(1, this.cmtReference.getValue());
		arrayList.add(2, this.cmtCcy.getValue());
		arrayList.add(3,
				PennantApplicationUtil.amountFormate(
						PennantApplicationUtil.unFormateAmount(this.cmtAmount.getActualValue(), defaultCCYDecPos),
						defaultCCYDecPos));
		arrayList.add(4,
				PennantApplicationUtil.amountFormate(
						PennantApplicationUtil.unFormateAmount(this.cmtUtilizedAmount.getValue(), defaultCCYDecPos),
						defaultCCYDecPos));
		arrayList.add(5,
				PennantApplicationUtil.amountFormate(
						PennantApplicationUtil.unFormateAmount(this.cmtAvailable.getValue(), defaultCCYDecPos),
						defaultCCYDecPos));
		return arrayList;
	}

	/**
	 * 
	 * @param event
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onSelectTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException, ParseException {

		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		doRemoveValidation();
		doClearMessage();

		switch (module) {
		case AssetConstants.UNIQUE_ID_CUSTOMERS:
			if (customerDialogCtrl != null) {
				customerDialogCtrl.doSetLabels(getHeaderBasicDetails());
			} else {
				appendCustomerDetailTab(false);
			}
			break;

		case AssetConstants.UNIQUE_ID_AGREEMENT:
			this.doSetValidation();
			this.doWriteComponentsToBean(getCommitment());

			if (agreementDetailDialogCtrl != null) {
				agreementDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
				agreementDetailDialogCtrl.doShowDialog(false);
			} else {
				appendAgreementsDetailTab(false);
			}
			break;

		case AssetConstants.UNIQUE_ID_CHECKLIST:
			// this.doSetValidation(); //TODO
			// this.doWriteComponentsToBean(getCommitment());

			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
			} else {
				appendCheckListDetailTab(getCommitment(), false);
			}
			break;

		case AssetConstants.UNIQUE_ID_COLLATERAL:
			if (collateralHeaderDialogCtrl != null) {
				collateralHeaderDialogCtrl.doSetLabels(getHeaderBasicDetails());
			}
			doValidateCustCIF();
			break;

		case AssetConstants.UNIQUE_ID_DOCUMENTDETAIL:
			if (documentDetailDialogCtrl != null) {
				documentDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
			}
			break;

		case AssetConstants.UNIQUE_ID_RECOMMENDATIONS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendRecommendDetailTab(false);
			break;
		default:
			break;
		}

		logger.debug(tab.getId() + " --> " + "Leaving");
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	/**
	 * Method for Fetching Document Details for Check list processing
	 * 
	 * @return
	 */
	public List<DocumentDetails> getDocumentDetails() {
		if (documentDetailDialogCtrl != null) {
			return documentDetailDialogCtrl.getDocumentDetailsList();
		}
		return new ArrayList<DocumentDetails>();
	}

	/**
	 * Method for fetching Customer Basic Details for Document Details processing
	 * 
	 * @return
	 */
	public List<Object> getCustomerBasicDetails() {

		List<Object> custBasicDetails = null;
		if (commitment.getCustomerDetails() != null && commitment.getCustomerDetails().getCustomer() != null) {
			custBasicDetails = new ArrayList<>();
			custBasicDetails.add(commitment.getCustomerDetails().getCustomer().getCustID());
			custBasicDetails.add(commitment.getCustomerDetails().getCustomer().getCustCIF());
			custBasicDetails.add(commitment.getCustomerDetails().getCustomer().getCustShrtName());
		}
		return custBasicDetails;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public List<CommitmentRate> getCommitmentRateDetailList() {
		return commitmentRateDetailList;
	}

	public void setCommitmentRateDetailList(List<CommitmentRate> commitmentRateDetailList) {
		this.commitmentRateDetailList = commitmentRateDetailList;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public void setCollateralHeaderDialogCtrl(CollateralHeaderDialogCtrl collateralHeaderDialogCtrl) {
		this.collateralHeaderDialogCtrl = collateralHeaderDialogCtrl;
	}

	public List<FinFlagsDetail> getCmtFlagsDetailList() {
		return cmtFlagsDetailList;
	}

	public void setCmtFlagsDetailList(List<FinFlagsDetail> cmtFlagsDetailList) {
		this.cmtFlagsDetailList = cmtFlagsDetailList;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public Map<Long, Long> getSelectedAnsCountMap() {
		return selectedAnsCountMap;
	}

	public void setSelectedAnsCountMap(Map<Long, Long> selectedAnsCountMap) {
		this.selectedAnsCountMap = selectedAnsCountMap;
	}

	public List<FinanceCheckListReference> getCommitmentChecklists() {
		return collateralChecklists;
	}

	public void setCommitmentChecklists(List<FinanceCheckListReference> collateralChecklists) {
		this.collateralChecklists = collateralChecklists;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}
