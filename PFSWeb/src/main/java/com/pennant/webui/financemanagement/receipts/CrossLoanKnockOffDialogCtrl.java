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
 * 
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ReceiptDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-06-2011 * * Modified
 * Date : 03-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-06-2011 Pennant 0.1 * 29-09-2018 somasekhar 0.2 added backdate sp also, * 10-10-2018 somasekhar 0.3 Ticket
 * id:124998,defaulting receipt* purpose and excessadjustto for * closed loans * Ticket id:124998 * 13-06-2018 Siva 0.2
 * Receipt auto printing on approval * * 13-06-2018 Siva 0.3 Receipt Print Option Added * * 17-06-2018 Srinivasa Varma
 * 0.4 PSD 126950 * * 19-06-2018 Siva 0.5 Auto Receipt Number Generation * * 28-06-2018 Siva 0.6 Stop printing Receipt
 * if receipt mode status is either cancel or Bounce * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.receipts;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainListCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.financemanagement.paymentMode.ReceiptListCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.util.ReceiptUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/CrossLoanKnockOffDialog.zul
 */
public class CrossLoanKnockOffDialogCtrl extends GFCBaseCtrl<CrossLoanKnockOff> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffDialogCtrl.class);

	protected Window windowCrossLoanKnockOffDialog;
	protected Borderlayout blCrossLoanKnockOff;
	protected Label windowTitle;
	protected Textbox custCIF;
	protected Textbox fromFinReference;
	protected Textbox toFinReference;
	protected Decimalbox priBal;
	protected Decimalbox pftBal;
	protected Decimalbox priDue;
	protected Decimalbox pftDue;
	protected Button btnSearchCustCIF;
	protected Button btnSearchFromFinreference;
	protected Button btnSearchToFinreference;
	protected Button btnSearchReceiptInProcess;
	protected Groupbox gbReceiptDetails;
	protected Textbox receiptId;
	protected Combobox receiptPurpose;
	protected Combobox receiptMode;
	protected Label receiptTypeLabel;
	protected Combobox subReceiptMode;
	protected Datebox receiptDate;
	protected CurrencyBox receiptAmount;
	protected Combobox excessAdjustTo;
	protected Combobox receivedFrom;
	protected Combobox allocationMethod;
	protected Uppercasebox externalRefrenceNumber;
	protected Textbox remarks;
	protected Label scheduleLabel;
	protected Combobox effScheduleMethod;
	protected Groupbox gbInstrumentDetails;
	protected Uppercasebox favourNo;
	protected Datebox valueDate;
	protected ExtendedCombobox bankCode;
	protected Textbox favourName;
	protected Uppercasebox depositNo;
	protected Uppercasebox transactionRef;
	protected AccountSelectionBox chequeAcNo;
	protected Uppercasebox paymentRef;
	protected Textbox drawerName;
	protected Textbox excessRef;
	protected Groupbox gbPayable;
	protected Listbox listBoxExcess;
	protected Label labelFavourNo;
	protected Row rowFavourNo;
	protected Row rowBankCode;
	protected Row rowPaymentRef;
	protected Row rowChequeAcNo;
	protected Row rowRemarks;
	protected Listbox listBoxPastdues;
	protected Listbox listBoxSchedule;
	protected Listheader lhScheduleEndBal;
	protected Listheader lhSchFee;
	protected Listheader lhLimitChange;
	protected Listheader lhAvailableLimit;
	protected Listheader lhODLimit;
	protected Label finSchType;
	protected Label finSchCcy;
	protected Label finSchMethod;
	protected Label finSchProfitDaysBasis;
	protected Label finSchReference;
	protected Label finSchGracePeriodEndDate;
	protected Label effectiveRateOfReturn;
	protected Label labelFinGracePeriodEndDate;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab receiptDetailsTab;
	protected Tab effectiveScheduleTab;
	protected Button btnPrint;
	protected Button btnReceipt;
	protected Button btnChangeReceipt;
	protected Button btnCalcReceipts;
	protected Decimalbox remBalAfterAllocation;
	protected ExtendedCombobox cancelReason;
	protected Textbox cancelRemarks;
	protected Row rowCancelReason;

	private CustomerDetailsService customerDetailsService;
	private ReceiptService receiptService;
	private FinanceDetailService financeDetailService;
	private ReceiptCalculator receiptCalculator;
	private AccrualService accrualService;
	private AgreementDetailDialogCtrl agreementDetailDialogCtrl = null;
	private FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl = null;
	protected FinanceMainListCtrl financeMainListCtrl = null;
	protected ReceiptListCtrl receiptListCtrl = null;
	private FinReceiptData receiptData = null;
	private FinReceiptData orgReceiptData = null;
	private FinanceDetail financeDetail;
	private FinanceDetail orgFinanceDetail;
	private Map<String, BigDecimal> taxPercMap = null;

	private String recordType = "";
	private CrossLoanKnockOff befImage;
	private List<ChartDetail> chartDetailList = new ArrayList<>();
	private List<FinanceScheduleDetail> orgScheduleList = new ArrayList<>();
	private List<FinReceiptDetail> recDtls = new ArrayList<>();

	protected String curRoleCode;
	protected String curNextRoleCode;
	protected String curTaskId;
	protected String curNextTaskId;
	protected String curNextUserId;

	protected String module = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	private int formatter = 0;
	private String amountFormat = null;
	private int receiptPurposeCtg = -1;
	private boolean dateChange = true;

	protected boolean recSave = false;
	protected Component checkListChildWindow = null;
	protected boolean isEnquiry = false;
	protected Map<String, ArrayList<ErrorDetail>> overideMap = new HashMap<>();

	private boolean isKnockOff = false;
	private boolean isForeClosure = false;
	private boolean isEarlySettle = false;
	private boolean isCancel = false;
	private transient CrossLoanKnockOffService crossLoanKnockOffService;
	protected CrossLoanKnockOff crossLoanHeader;
	protected CrossLoanKnockOffListCtrl crossLoanKnockOffListCtrl = null;

	/**
	 * default constructor.<br>
	 */
	public CrossLoanKnockOffDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CrossLoanKnockOffDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$windowCrossLoanKnockOffDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(windowCrossLoanKnockOffDialog);
		FinReceiptData receiptData = new FinReceiptData();
		FinanceMain financeMain = null;
		FinReceiptHeader finReceiptHeader = new FinReceiptHeader();

		try {
			if (arguments.containsKey("receiptData")) {
				setReceiptData((FinReceiptData) arguments.get("receiptData"));
				receiptData = getReceiptData();

				finReceiptHeader = receiptData.getReceiptHeader();

				financeDetail = receiptData.getFinanceDetail();
				financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				setFinanceDetail(financeDetail);
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("crossLoanHeader")) {
				crossLoanHeader = (CrossLoanKnockOff) arguments.get("crossLoanHeader");
				if (crossLoanHeader != null) {
					setReceiptData(crossLoanHeader.getFinReceiptData());
					receiptData = getReceiptData();

					finReceiptHeader = receiptData.getReceiptHeader();

					financeDetail = receiptData.getFinanceDetail();
					financeMain = financeDetail.getFinScheduleData().getFinanceMain();

					setFinanceDetail(financeDetail);

					formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
					amountFormat = PennantApplicationUtil.getAmountFormate(formatter);

					recordType = finReceiptHeader.getRecordType();
					isCancel = crossLoanHeader.isCancelProcess();

					befImage = ObjectUtil.clone(crossLoanHeader);
					orgFinanceDetail = ObjectUtil.clone(financeDetail);
					crossLoanHeader.setBefImage(befImage);
				}
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}
			if (arguments.containsKey("enqiryModule")) {
				isEnquiry = (boolean) arguments.get("enqiryModule");
				enqiryModule = isEnquiry;
			}
			if (arguments.containsKey("isKnockOff")) {
				isKnockOff = (boolean) arguments.get("isKnockOff");
			}
			if (arguments.containsKey("isForeClosure")) {
				isForeClosure = (boolean) arguments.get("isForeClosure");
			}

			if (arguments.containsKey("crossLoanKnockOffListCtrl")) {
				setCrossLoanKnockOffListCtrl((CrossLoanKnockOffListCtrl) arguments.get("crossLoanKnockOffListCtrl"));
			}
			if (arguments.containsKey("moduleCode")) {
				moduleCode = (String) arguments.get("moduleCode");
			}

			doLoadWorkFlow(crossLoanHeader.isWorkflow(), crossLoanHeader.getWorkflowId(),
					crossLoanHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				String recStatus = StringUtils.trimToEmpty(finReceiptHeader.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
				}
			} else {
				this.south.setHeight("0px");
			}

			if (enqiryModule) {
				setWorkFlowEnabled(false);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doStoreServiceIds(finReceiptHeader);

			// READ OVERHANDED parameters !
			FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();

			FinanceProfitDetail finPftDeatils = fsd.getFinPftDeatil();
			finPftDeatils = accrualService.calProfitDetails(financeMain, fsd.getFinanceScheduleDetails(), finPftDeatils,
					receiptData.getReceiptHeader().getReceiptDate());

			// set if new receord
			if (StringUtils.isBlank(finReceiptHeader.getRecordType())) {
				if (StringUtils.isBlank(receiptData.getReceiptHeader().getAllocationType())) {
					receiptData.getReceiptHeader().setAllocationType("A");
				}
				if (StringUtils.isBlank(receiptData.getReceiptHeader().getExcessAdjustTo())) {
					receiptData.getReceiptHeader().setExcessAdjustTo("E");
				}
			}

			// receiptData =
			// getReceiptCalculator().removeUnwantedManAloc(receiptData);
			setSummaryData(false);
			// set Read only mode accordingly if the object is new or not.
			if (StringUtils.isBlank(finReceiptHeader.getRecordType())) {
				doEdit();
				this.btnReceipt.setDisabled(true);
			}

			doShowDialog(finReceiptHeader);

			// set default data for closed loans
			setClosedLoanDetails(finReceiptHeader.getFinID());

			// Setting tile Name based on Service Action
			this.blCrossLoanKnockOff.setHeight(getBorderLayoutHeight());
			this.listBoxSchedule.setHeight(getListBoxHeight(6));
			this.receiptDetailsTab.setSelected(true);
			if (receiptData.isCalReq()) {
				this.btnCalcReceipts.setDisabled(false);
			} else {
				this.btnCalcReceipts.setDisabled(true);
				this.btnCalcReceipts.setVisible(false);
			}
			this.windowTitle.setValue(Labels.getLabel(module + "_Window.Title"));
			setDialog(DialogType.EMBEDDED);
			if (receiptPurposeCtg > 1) {
				this.excessAdjustTo.setDisabled(true);
				fillComboBox(allocationMethod, "A", PennantStaticListUtil.getAllocationMethods(), ",M,");
				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);
				this.allocationMethod.setReadonly(true);
			}
			if (isForeClosure || isEarlySettle) {
				this.gbPayable.setVisible(true);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.windowCrossLoanKnockOffDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Map<String, Object> map = new HashMap<>();
		CustomerDetails customerDetails = customerDetailsService
				.getCustomerById(getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
		String pageName = PennantAppUtil.getCustomerPageName();
		map.put("customerDetails", customerDetails);
		map.put("enqiryModule", true);
		map.put("dialogCtrl", this);
		map.put("newRecord", false);
		map.put("CustomerEnq", "CustomerEnq");

		Executions.createComponents(pageName, null, map);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	/**
	 * Method for Showing Finance details on Clicking Finance View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchFinreference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		// Preparation of Finance Enquiry Data
		FinReceiptHeader finReceiptHeader = receiptData.getReceiptHeader();
		FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
		aFinanceEnq.setFinID(finReceiptHeader.getFinID());
		aFinanceEnq.setFinReference(finReceiptHeader.getReference());
		aFinanceEnq.setFinType(finReceiptHeader.getFinType());
		aFinanceEnq.setLovDescFinTypeName(finReceiptHeader.getFinTypeDesc());
		aFinanceEnq.setFinCcy(finReceiptHeader.getFinCcy());
		aFinanceEnq.setScheduleMethod(finReceiptHeader.getScheduleMethod());
		aFinanceEnq.setProfitDaysBasis(finReceiptHeader.getPftDaysBasis());
		aFinanceEnq.setFinBranch(finReceiptHeader.getFinBranch());
		aFinanceEnq.setLovDescFinBranchName(finReceiptHeader.getFinBranchDesc());
		aFinanceEnq.setLovDescCustCIF(finReceiptHeader.getCustCIF());

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("moduleCode", moduleCode);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", aFinanceEnq);
		map.put("ReceiptDialog", this);
		map.put("enquiryType", "FINENQ");
		map.put("isModelWindow", true);
		map.put("ReceiptDialogPage", true);
		map.put("window_ReceiptDialog", this.windowCrossLoanKnockOffDialog);
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.windowCrossLoanKnockOffDialog, map);

		logger.debug("Leaving " + event.toString());
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
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole(), menuItemRightName);

		if (isCancel) {
			this.btnReceipt.setVisible(true);
			this.btnReceipt.setDisabled(false);
			this.btnChangeReceipt.setVisible(true);
			this.btnChangeReceipt.setDisabled(false);

			return;
		}

		this.btnReceipt.setVisible(getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnReceipt"));
		this.btnChangeReceipt
				.setVisible(getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnChangeReceipt"));

		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnReceipt"));
		this.btnChangeReceipt
				.setDisabled(!getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnChangeReceipt"));
		logger.debug("Leaving");
	}

	/**
	 * ticket id:124998,checking closed loans and setting default data
	 * 
	 * @param finReference
	 */
	private void setClosedLoanDetails(long finId) {
		FinanceMain finMain = receiptService.getClosingStatus(finId, TableType.MAIN_TAB, false);

		if (StringUtils.isNotEmpty(finMain.getClosingStatus())
				&& !StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)
				&& !finMain.isWriteoffLoan()) {
			fillComboBox(this.receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,EarlySettlement,EarlyPayment,");

			Set<String> exclude = new HashSet<>();
			exclude.add("A");
			if (!finMain.isUnderSettlement()) {
				exclude.add("S");
			}

			List<ValueLabel> excessAdjustmentTypes = PennantStaticListUtil.getExcessAdjustmentTypes();

			fillComboBox(this.excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EXCESS,
					excludeComboBox(excessAdjustmentTypes, exclude));
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		// Receipts Details
		this.priBal.setFormat(amountFormat);
		this.pftBal.setFormat(amountFormat);
		this.priDue.setFormat(amountFormat);
		this.pftDue.setFormat(amountFormat);

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receiptAmount.setProperties(true, formatter);

		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(false);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);
		this.drawerName.setMaxlength(50);

		// this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(500);
		this.favourName.setMaxlength(50);
		this.favourName.setDisabled(true);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourNo.setMaxlength(6);
		this.depositNo.setMaxlength(50);
		this.paymentRef.setMaxlength(50);
		this.transactionRef.setMaxlength(50);

		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode" });

		this.cancelReason.setModuleName("RejectDetail");
		this.cancelReason.setMandatoryStyle(true);
		this.cancelReason.setValueColumn("RejectCode");
		this.cancelReason.setDescColumn("RejectDesc");
		this.cancelReason.setDisplayStyle(2);
		this.cancelReason.setValidateColumns(new String[] { "RejectCode" });
		this.cancelReason.setFilters(
				new Filter[] { new Filter("RejectType", PennantConstants.Reject_Payment, Filter.OP_EQUAL) });
		this.cancelReason.setMaxlength(10);
		this.cancelRemarks.setMaxlength(100);
		/*
		 * if (DisbursementConstants.PAYMENT_TYPE_MOB .equals(receiptData.getReceiptHeader().getReceiptChannel())) {
		 * this.collectionAgentId.setMandatoryStyle(true); }
		 */

		if (isCancel) {
			this.rowCancelReason.setVisible(true);
		}

		appendScheduleMethod(receiptData.getReceiptHeader());

		if (isKnockOff) {
			this.gbInstrumentDetails.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param Receipt
	 * @throws Exception
	 */
	public void doShowDialog(FinReceiptHeader finReceiptHeader) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (finReceiptHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents();

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.windowCrossLoanKnockOffDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		// Receipt Details
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_excessAdjustTo"), this.excessAdjustTo);
		// Open Amortization CR
		if (financeMain.isManualSchedule() && receiptPurposeCtg == 1) {
			readOnlyComponent(true, this.allocationMethod);
			readOnlyComponent(true, this.effScheduleMethod);
		} else {
			readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_allocationMethod"), this.allocationMethod);
			readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_effScheduleMethod"), this.effScheduleMethod);
		}
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_remarks"), this.remarks);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_receivedFrom"), this.receivedFrom);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_externalRefrenceNumber"), this.externalRefrenceNumber);

		// Receipt Details
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_favourNo"), this.favourNo);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_bankCode"), this.bankCode);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_favourName"), this.favourName);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_depositNo"), this.depositNo);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_chequeAcNo"), this.chequeAcNo);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_transactionRef"), this.transactionRef);
		// readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_cashReceivedDate"),
		// this.receivedDate);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_DrawerName"), this.drawerName);

		// Receipt Details
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_favourNo"), this.favourNo);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_bankCode"), this.bankCode);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_favourName"), this.favourName);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_depositNo"), this.depositNo);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_chequeAcNo"), this.chequeAcNo);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_paymentRef"), this.paymentRef);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_transactionRef"), this.transactionRef);
		// readOnlyComponent(true, this.receivedDate);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_remarks"), this.remarks);
		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_DrawerName"), this.drawerName);

		if (isCancel && FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_APPROVER.equals(module)) {
			readOnlyComponent(true, this.cancelReason);
			readOnlyComponent(true, this.cancelRemarks);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadOnly(boolean isUserAction) {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(true, this.excessAdjustTo);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(true, this.effScheduleMethod);
		readOnlyComponent(true, this.externalRefrenceNumber);

		// Receipt Details
		if (isUserAction) {
			readOnlyComponent(true, this.favourNo);
			readOnlyComponent(true, this.valueDate);
			readOnlyComponent(true, this.bankCode);
			readOnlyComponent(true, this.favourName);
			readOnlyComponent(true, this.depositNo);
			readOnlyComponent(true, this.chequeAcNo);
			readOnlyComponent(true, this.paymentRef);
			readOnlyComponent(true, this.transactionRef);
			// readOnlyComponent(true, this.receivedDate);
			readOnlyComponent(true, this.remarks);
			readOnlyComponent(true, this.drawerName);

		}

		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgReceipt
	 */
	private boolean setSummaryData(boolean isChgReceipt) {
		logger.debug(Literal.ENTERING);
		receiptPurposeCtg = ReceiptUtil.getReceiptPurpose(receiptData.getReceiptHeader().getReceiptPurpose());
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		boolean isClosrMaturedLAN = false;
		if (receiptPurposeCtg == 2) {
			isClosrMaturedLAN = !isClosrMaturedLAN && isForeClosure && isClosureMaturedLAN(receiptData);
			receiptData.setForeClosure(isForeClosure);
			receiptData.setClosrMaturedLAN(isClosrMaturedLAN);
			if (!isForeClosure) {
				isEarlySettle = true;
			}
		}

		Date valDate = rch.getValueDate();
		receiptData.setValueDate(valDate);
		if (orgReceiptData != null) {
			receiptData = orgReceiptData;
		} else {
			receiptService.calcuateDues(receiptData);
			if (!AllocationType.MANUAL.equals(receiptData.getReceiptHeader().getAllocationType())
					&& receiptData.isCalReq()) {
				receiptData = receiptCalculator.recalAutoAllocation(receiptData, false);
			}
			if (!receiptData.isCalReq()) {
				for (ReceiptAllocationDetail allocate : receiptData.getAllocList()) {
					allocate.setTotalPaid(allocate.getPaidAmount().add(allocate.getTdsPaid()));
					allocate.setTotRecv(allocate.getTotalDue().add(allocate.getTdsDue()));
					if (allocate.getAllocationTo() == 0
							|| Allocation.BOUNCE.equalsIgnoreCase(allocate.getAllocationType())) {
						allocate.setTypeDesc(
								Labels.getLabel("label_RecceiptDialog_AllocationType_" + allocate.getAllocationType()));
					} else if (RepayConstants.PAYSTATUS_BOUNCE.equals(rch.getReceiptModeStatus())
							|| RepayConstants.PAYSTATUS_DEPOSITED.equals(rch.getReceiptModeStatus())
							|| RepayConstants.PAYSTATUS_REALIZED.equals(rch.getReceiptModeStatus())
							|| RepayConstants.PAYSTATUS_CANCEL.equals(rch.getReceiptModeStatus())) {
						if (StringUtils.isNotBlank(allocate.getTypeDesc())) {
							allocate.setTypeDesc(allocate.getTypeDesc());
						} else {
							allocate.setTypeDesc(Labels
									.getLabel("label_RecceiptDialog_AllocationType_" + allocate.getAllocationType()));
						}

					}
					if (!PennantStaticListUtil.getExcludeDues().contains(allocate.getAllocationType())) {
						allocate.setEditable(true);
					}
				}
				receiptData.getReceiptHeader().setAllocations(receiptData.getAllocList());
				receiptCalculator.setTotals(receiptData, 0);
			}
		}

		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		orgScheduleList = schdData.getFinanceScheduleDetails();
		RepayMain rpyMain = receiptData.getRepayMain();

		receiptData.setAccruedTillLBD(schdData.getFinanceMain().getLovDescAccruedTillLBD());
		rpyMain.setLovDescFinFormatter(formatter);

		String custCIFname = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
			custCIFname = customer.getCustCIF();
			if (StringUtils.isNotBlank(customer.getCustShrtName())) {
				custCIFname = custCIFname + "-" + customer.getCustShrtName();
			}
		}

		this.priBal.setValue(PennantApplicationUtil.formateAmount(rpyMain.getPrincipalBalance(), formatter));
		this.pftBal.setValue(PennantApplicationUtil.formateAmount(rpyMain.getProfitBalance(), formatter));
		this.priDue.setValue(PennantApplicationUtil.formateAmount(rpyMain.getOverduePrincipal(), formatter));
		this.pftDue.setValue(PennantApplicationUtil.formateAmount(rpyMain.getOverdueProfit(), formatter));

		// Receipt Basic Details
		this.custCIF.setValue(custCIFname);

		setBalances();
		logger.debug(Literal.LEAVING);
		return false;
	}

	private boolean isClosureMaturedLAN(FinReceiptData recData) {
		try {
			FinanceMain fm = recData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			Date maturityDate = fm.getMaturityDate();
			return (DateUtil.compare(maturityDate, SysParamUtil.getAppDate()) < 0) && fm.isFinIsActive();
		} catch (NullPointerException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return false;
	}

	/**
	 * Method for setting data for Child Tab Headers
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
		arrayList.add(0, finMain.getFinType());
		arrayList.add(1, finMain.getFinCcy());
		arrayList.add(2, finMain.getScheduleMethod());
		arrayList.add(3, finMain.getFinReference());
		arrayList.add(4, finMain.getProfitDaysBasis());
		arrayList.add(5, finMain.getGrcPeriodEndDate());
		arrayList.add(6, finMain.isAllowGrcPeriod());

		// In case of Promotion Product will be Empty
		if (StringUtils.isEmpty(finType.getProduct())) {
			arrayList.add(7, false);
		} else {
			arrayList.add(7, true);
		}
		arrayList.add(8, finType.getFinCategory());
		arrayList.add(9, this.custCIF.getValue());
		arrayList.add(10, false);
		arrayList.add(11, module);
		return arrayList;
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {

		deAllocateAuthorities("FinanceMainDialog");

		doClose(this.btnReceipt.isVisible());
	}

	/**
	 * Method for calculation of Schedule Repayment details List of data
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException
	 * @throws WrongValueException
	 */
	public void onClick$btnCalcReceipts(Event event)
			throws InterruptedException, WrongValueException, InterfaceException {
		logger.debug("Entering" + event.toString());
		if (!isValidateData(true)) {
			return;
		}
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(orgScheduleList);
		FinReceiptData tempReceiptData = ObjectUtil.clone(receiptData);
		setOrgReceiptData(tempReceiptData);
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		boolean isCalcCompleted = true;
		Date valuDate = receiptData.getReceiptHeader().getValueDate();
		if (finMain.isFinIsActive() && DateUtil.compare(valuDate, finMain.getMaturityDate()) <= 0) {

			if (receiptPurposeCtg > 0) {
				isCalcCompleted = recalEarlyPaySchd(true);
				if (isCalcCompleted) {
					this.effectiveScheduleTab.setVisible(true);
				}
			} else {
				isCalcCompleted = true;
				/*
				 * receiptData = calculateRepayments(); setRepayDetailData();
				 */
			}
		}

		Listitem item;
		for (int i = 0; i < receiptData.getReceiptHeader().getAllocationsSummary().size(); i++) {

			item = listBoxPastdues.getItems().get(i);
			CurrencyBox allocationWaived = (CurrencyBox) item.getFellowIfAny("AllocateWaived_" + i);
			allocationWaived.setReadonly(true);
		}

		// Do readonly to all components
		if (isCalcCompleted) {
			doReadOnly(true);
			this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnReceipt"));
			this.btnChangeReceipt.setDisabled(true);
			this.btnCalcReceipts.setDisabled(true);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Processing Calculation button visible , if amount modified
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$receiptAmount(Event event) throws InterruptedException {
		logger.debug("Entering");

		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts
				.setDisabled(!getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnCalcReceipts"));

		BigDecimal receiptAmount = this.receiptAmount.getActualValue();
		receiptAmount = PennantApplicationUtil.unFormateAmount(receiptAmount, formatter);
		receiptData.getReceiptHeader().setReceiptAmount(receiptAmount);

		resetAllocationPayments();
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Calculation button visible , if Value Date modified
	 * 
	 * @param event
	 */
	public void onChange$receivedDate(Event event) {
		logger.debug("Entering");

		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts
				.setDisabled(!getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnCalcReceipts"));

		readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_allocationMethod"), this.allocationMethod);

		List<ValueLabel> allocationMethods = PennantStaticListUtil.getAllocationMethods();

		Set<String> exclude = new HashSet<>();
		FinanceMain fm = this.financeDetail.getFinScheduleData().getFinanceMain();

		if (!fm.isUnderSettlement()) {
			exclude.add(AllocationType.NO_ALLOC);
		}

		fillComboBox(this.allocationMethod, AllocationType.AUTO, excludeComboBox(allocationMethods, exclude));

		resetAllocationPayments();

		logger.debug("Leaving");
	}

	public void onFulfill$bankCode(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = bankCode.getObject();

		if (dataObject instanceof String) {
			this.bankCode.setValue(dataObject.toString());
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setAttribute("bankCode", details.getBankCode());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Processing Captured details based on Receipt Purpose
	 * 
	 * @param event
	 * @throws InterruptedException
	 */

	/**
	 * Method for Processing Captured details based on Receipt Mode
	 * 
	 * @param event
	 */
	public void onChange$receiptMode(Event event) {
		logger.debug("Entering");

		String dType = this.receiptMode.getSelectedItem().getValue().toString();

		if (!StringUtils.isEmpty(dType) && !StringUtils.equals(dType, PennantConstants.List_Select)
				&& StringUtils.equals(dType, ReceiptMode.ESCROW)) {

			fillComboBox(this.receiptPurpose, FinServiceEvent.EARLYRPY, PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,");
			this.receiptPurpose.setDisabled(true);
		} else {
			this.receiptPurpose.setDisabled(false);
		}
		checkByReceiptMode(dType, true);
		resetAllocationPayments();

		logger.debug("Leaving");
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * 
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");
		if (isUserAction) {
			this.receiptAmount.setValue(BigDecimal.ZERO);
			this.favourNo.setValue("");
			this.valueDate.setValue(SysParamUtil.getAppDate());
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.bankCode.setObject(null);
			this.favourName.setValue("");
			this.depositNo.setValue("");
			this.transactionRef.setValue("");
			this.chequeAcNo.setValue("");
			this.drawerName.setValue("");
		}

		if (StringUtils.equals(recMode, PennantConstants.List_Select)) {
			this.gbReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else {

			/*
			 * if (StringUtils.isEmpty(this.paymentRef.getValue())) {
			 * this.paymentRef.setValue(ReferenceGenerator.generateNewReceiptNo( )); }
			 */

			this.gbReceiptDetails.setVisible(true);
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_receiptAmount"), this.receiptAmount);
			// readOnlyComponent(isReadOnly("CrossLoanKnockOffDialog_fundingAccount"),
			// this.fundingAccount);

			FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
			Filter fundingAcFilters[] = new Filter[4];
			fundingAcFilters[0] = new Filter("Purpose", RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL);
			fundingAcFilters[1] = new Filter("FinType", finType.getFinType(), Filter.OP_EQUAL);
			fundingAcFilters[2] = new Filter("PaymentMode", recMode, Filter.OP_EQUAL);
			if (ReceiptMode.ONLINE.equals(recMode)) {
				fundingAcFilters[2] = new Filter("PaymentMode", receiptData.getReceiptHeader().getSubReceiptMode(),
						Filter.OP_EQUAL);
			}
			fundingAcFilters[3] = new Filter("EntityCode", finType.getLovDescEntityCode(), Filter.OP_EQUAL);
			Filter.and(fundingAcFilters);
			// this.row_fundingAcNo.setVisible(true);
			this.rowRemarks.setVisible(true);

			if (StringUtils.equals(recMode, ReceiptMode.CHEQUE) || StringUtils.equals(recMode, ReceiptMode.DD)) {

				this.rowFavourNo.setVisible(true);
				this.rowBankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				// IMD Changes
				if (StringUtils.equals(recMode, ReceiptMode.CHEQUE)) {
					this.bankCode.setMandatoryStyle(false);
				}
				// this.row_DepositDate.setVisible(true);
				this.rowPaymentRef.setVisible(false);

				if (StringUtils.equals(recMode, ReceiptMode.CHEQUE)) {
					this.rowChequeAcNo.setVisible(true);
					this.labelFavourNo.setValue(Labels.getLabel("label_CrossLoanKnockOffDialog_ChequeFavourNo.value"));

					if (isUserAction) {
						this.valueDate.setValue(SysParamUtil.getAppDate());
					}

				} else {
					this.rowChequeAcNo.setVisible(false);
					this.labelFavourNo.setValue(Labels.getLabel("label_CrossLoanKnockOffDialog_DDFavourNo.value"));

					if (isUserAction) {
						this.valueDate.setValue(SysParamUtil.getAppDate());
					}
				}

				if (isUserAction) {
					this.favourName.setValue(Labels.getLabel("label_ClientName"));
				}

			} else if (StringUtils.equals(recMode, ReceiptMode.CASH)) {

				this.rowFavourNo.setVisible(false);
				this.rowBankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.rowChequeAcNo.setVisible(false);
				this.rowPaymentRef.setVisible(false);
				// this.row_fundingAcNo.setVisible(false);

				if (isUserAction) {
					// this.receivedDate.setValue(DateUtility.getAppDate());
				}

			} else {
				this.rowFavourNo.setVisible(false);
				this.rowBankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.rowChequeAcNo.setVisible(false);
				this.rowPaymentRef.setVisible(true);
			}
		}

		// Due to changes in Receipt Amount, call Auto Allocations
		if (isUserAction) {
			resetAllocationPayments();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * 
	 * @param event
	 */
	public void onChange$allocationMethod(Event event) {
		logger.debug("Entering");
		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);

		if (AllocationType.AUTO.equals(allocateMthd)) {
			resetAllocationPayments();
		} else if (AllocationType.MANUAL.equals(allocateMthd)) {
			receiptData.getReceiptHeader().setAllocationType(allocateMthd);
			doFillAllocationDetail();
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * 
	 * @param event
	 */
	/**
	 * Method for Allocation Details recalculation
	 */
	private void resetAllocationPayments() {
		logger.debug("Entering");

		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		this.receiptPurpose.setConstraint("");
		this.receiptPurpose.setErrorMessage("");
		// this.receivedDate.setConstraint("");
		// this.receivedDate.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);
		String recPurpose = getComboboxValue(this.receiptPurpose);
		Date valueDate = receiptData.getReceiptHeader().getReceiptDate();

		receiptData.setBuildProcess("I");
		receiptData.getReceiptHeader().setReceiptPurpose(recPurpose);
		receiptData.getReceiptHeader().getAllocations().clear();
		FinScheduleData schData = receiptData.getFinanceDetail().getFinScheduleData();

		if (receiptPurposeCtg == 2 && dateChange) {
			dateChange = false;
			receiptData.getReceiptHeader().setValueDate(null);
			try {
				receiptData.getRepayMain().setEarlyPayOnSchDate(valueDate);
				recalEarlyPaySchd(false);
			} catch (Exception e) {

			}
		}

		// Initiation of Receipt Data object
		receiptData = receiptCalculator.initiateReceipt(receiptData, false);

		// Excess Adjustments After calculation of Total Paid's
		BigDecimal totReceiptAmount = receiptData.getTotReceiptAmount();
		receiptData.setTotReceiptAmount(totReceiptAmount);
		receiptData.setAccruedTillLBD(schData.getFinanceMain().getLovDescAccruedTillLBD());

		// Allocation Process start
		if (StringUtils.equals(allocateMthd, RepayConstants.ALLOCTYPE_AUTO)) {
			receiptData = receiptCalculator.recalAutoAllocation(receiptData, false);
		}

		doFillAllocationDetail();
		setBalances();

		if (receiptPurposeCtg == 1) {
			// if no extra balance or partial pay disable excessAdjustTo
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			if (receiptPurposeCtg == 0) {
				this.excessAdjustTo.setDisabled(true);
			} else {
				this.excessAdjustTo.setDisabled(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for on Changing Waiver Amounts
	 */
	private void changeWaiver() {
		receiptData = receiptCalculator.changeAllocations(receiptData);
		doFillAllocationDetail();
		setBalances();
	}

	/**
	 * Method for on Changing Paid Amounts
	 */
	private void changeDue() {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		if (StringUtils.equals(RepayConstants.ALLOCTYPE_AUTO, rch.getAllocationType())) {
			receiptData = receiptCalculator.recalAutoAllocation(receiptData, false);
		}
		setBalances();
		doFillAllocationDetail();
	}

	/**
	 * Method for on Changing Paid Amounts
	 */
	private void changePaid() {
		receiptData = receiptCalculator.setTotals(receiptData, 0);
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		receiptData.getReceiptHeader().setAllocationsSummary(allocationList);

		setSummaryData(true);
		setBalances();
		doFillAllocationDetail();
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param receiptData
	 * @throws InterruptedException
	 */
	public boolean recalEarlyPaySchd(boolean isRecal) throws InterruptedException {
		logger.debug("Entering");
		// Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		if (receiptPurposeCtg == 1) {
			rch.setEffectSchdMethod(getComboboxValue(this.effScheduleMethod));
		}

		receiptData = receiptService.recalEarlyPaySchedule(receiptData);
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
		// Finding Last maturity date after recalculation.
		List<FinanceScheduleDetail> schList = fsd.getFinanceScheduleDetails();
		Date actualMaturity = fsd.getFinanceMain().getCalMaturity();
		for (int i = schList.size() - 1; i >= 0; i--) {
			if (schList.get(i).getClosingBalance().compareTo(BigDecimal.ZERO) > 0) {
				break;
			}
			actualMaturity = schList.get(i).getSchDate();
		}

		// Validation against Future Disbursements, if Closing balance is
		// becoming BigDecimal.ZERO before future disbursement date
		List<FinanceDisbursement> disbList = fsd.getDisbursementDetails();
		String eventDesc = PennantApplicationUtil.getLabelDesc(receiptData.getReceiptHeader().getReceiptPurpose(),
				PennantStaticListUtil.getReceiptPurpose());
		for (int i = 0; i < disbList.size(); i++) {
			FinanceDisbursement curDisb = disbList.get(i);
			if (curDisb.getDisbDate().compareTo(actualMaturity) > 0) {
				MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("30577", new String[] { eventDesc })));
				Events.sendEvent(Events.ON_CLICK, this.btnChangeReceipt, null);
				logger.debug("Leaving");
				return false;
			}
		}

		getFinanceDetail().setFinScheduleData(fsd);
		FinanceMain aFinanceMain = fsd.getFinanceMain();
		// aFinanceMain.setWorkflowId(getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());

		// Object Setting for Future save purpose
		setFinanceDetail(getFinanceDetail());
		receiptData.setFinanceDetail(getFinanceDetail());
		doFillScheduleList(fsd);

		if (isRecal) {

			this.finSchType.setValue(aFinanceMain.getFinType());
			this.finSchCcy.setValue(aFinanceMain.getFinCcy());
			this.finSchMethod.setValue(aFinanceMain.getScheduleMethod());
			this.finSchProfitDaysBasis.setValue(PennantApplicationUtil.getLabelDesc(aFinanceMain.getProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis()));
			this.finSchReference.setValue(aFinanceMain.getFinReference());
			this.finSchGracePeriodEndDate.setValue(DateUtil.formatToLongDate(aFinanceMain.getGrcPeriodEndDate()));
			this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn().toString() + "%");

			// Fill Effective Schedule Details
			this.effectiveScheduleTab.setVisible(true);

			/*
			 * // Dashboard Details Report doLoadTabsData(); doShowReportChart(fsd);
			 */

			// Repayments Calculation
			/*
			 * receiptData = calculateRepayments(); setRepayDetailData();
			 */
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * 
	 * @param aFinScheduleData (FinScheduleData)
	 * 
	 */
	public void doFillScheduleList(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = aFinScheduleData.getFinanceMain();

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
				|| financeMain.isAlwFlexi()) {

			this.lhAvailableLimit.setVisible(true);
			this.lhODLimit.setVisible(true);
			this.lhLimitChange.setVisible(true);

			if (financeMain.isAlwFlexi()) {

				labelFinGracePeriodEndDate
						.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinPureFlexiPeriodEndDate.value"));
				lhScheduleEndBal.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchdUtilization"));
				lhODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_DropLineLimit"));
				lhLimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitDrop"));

			} else {

				lhLimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitChange"));
				lhODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ODLimit"));
			}
			lhAvailableLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_AvailableLimit"));
		}

		FinanceScheduleDetail prvSchDetail = null;
		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();

		if (sdSize == 0) {
			logger.debug("Leaving");
			return;
		}

		// Find Out Finance Repayment Details on Schedule
		Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
		aFinScheduleData = financeDetailService.getFinMaintainenceDetails(aFinScheduleData);
		if (aFinScheduleData.getRepayDetails() != null && aFinScheduleData.getRepayDetails().size() > 0) {
			rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

			for (FinanceRepayments rpyDetail : aFinScheduleData.getRepayDetails()) {
				if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
					ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				} else {
					ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				}
			}
		}

		// Find Out Finance Repayment Details on Schedule
		Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
		if (aFinScheduleData.getPenaltyDetails() != null && aFinScheduleData.getPenaltyDetails().size() > 0) {
			penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

			for (OverdueChargeRecovery penaltyDetail : aFinScheduleData.getPenaltyDetails()) {
				if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
					ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
							.get(penaltyDetail.getFinODSchdDate());
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				} else {
					ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				}
			}
		}

		// Schedule Fee Column Visibility Check
		boolean isSchdFee = false;
		List<FinanceScheduleDetail> schdList = aFinScheduleData.getFinanceScheduleDetails();
		for (int i = 0; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if (curSchd.getFeeSchd().compareTo(BigDecimal.ZERO) > 0) {
				isSchdFee = true;
				break;
			}
		}

		this.lhSchFee.setVisible(isSchdFee);

		// Clear all the listitems in listbox
		this.listBoxSchedule.getItems().clear();
		aFinScheduleData.setFinanceScheduleDetails(
				ScheduleCalculator.sortSchdDetails(aFinScheduleData.getFinanceScheduleDetails()));

		for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
			boolean showRate = false;
			FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
			if (i == 0) {
				prvSchDetail = aScheduleDetail;
				showRate = true;
			} else {
				prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i - 1);
				if (aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
					showRate = true;
				}
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finSchdData", aFinScheduleData);

			map.put("financeScheduleDetail", aScheduleDetail);
			map.put("paymentDetailsMap", rpyDetailsMap);
			map.put("penaltyDetailsMap", penaltyDetailsMap);
			map.put("window", this.windowCrossLoanKnockOffDialog);
			map.put("formatter", formatter);

			finRender.render(map, prvSchDetail, false, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
					false);
			boolean lastRecord = false;
			if (aScheduleDetail.getClosingBalance().compareTo(BigDecimal.ZERO) == 0 && !financeMain.isSanBsdSchdle()
					&& !(financeMain.isInstBasedSchd())) {
				if (!(financeMain.isManualSchedule())
						|| aScheduleDetail.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
					lastRecord = true;
				}
			}

			if (i == sdSize - 1 || lastRecord) {
				finRender.render(map, prvSchDetail, true, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
						false);
				break;
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Calculating Allocations based on Receipt Details
	 * 
	 * @return
	 */
	private FinReceiptData calculateRepayments() {
		logger.debug("Entering");

		receiptData.setBuildProcess("R");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		recDtls = rch.getReceiptDetails();

		// Ticket Id : 173036 -- Start--
		// Due to global variable, list is not clearing when exception raised.

		Iterator<FinReceiptDetail> iter = recDtls.iterator();
		while (iter.hasNext()) {
			FinReceiptDetail p = iter.next();
			if (p.getReceiptSeqID() == 0) {
				iter.remove();
			}
		}

		// --End--

		// Prepare Receipt Details Data
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();

		BigDecimal pastDues = receiptCalculator.getTotalNetPastDue(receiptData);
		receiptData.setTotalPastDues(pastDues);

		if (isKnockOff) {
			String payType = payType(getComboboxValue(receiptMode));
			receiptData = receiptService.updateExcessPay(receiptData, payType, rch.getKnockOffRefId(),
					rch.getReceiptAmount());
			receiptData = createXcessRCD();
		} else if (isForeClosure || isEarlySettle) {
			receiptData = createXcessRCD();
			if (isEarlySettle) {
				receiptData = createNonXcessRCD();
			}
		} else {
			receiptData = createNonXcessRCD();
		}

		rch.setRemarks(this.remarks.getValue());

		logger.debug("Leaving");
		return receiptData;
	}

	private String payType(String mode) {
		String payType = "";
		if (ReceiptMode.EMIINADV.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_EMIINADV;
		} else if (ReceiptMode.EXCESS.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_EXCESS;
		} else if (ReceiptMode.CASHCLT.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_CASHCLT;
		} else if (ReceiptMode.DSF.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_DSF;
		} else {
			payType = RepayConstants.EXAMOUNTTYPE_PAYABLE;
		}
		return payType;
	}

	private FinReceiptData createXcessRCD() {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		receiptData.getReceiptHeader().setReceiptDetails(rcdList);

		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		Map<String, BigDecimal> taxPercMap = null;

		// Create a new Receipt Detail for every type of excess/payable
		for (int i = 0; i < xcessPayables.size(); i++) {
			XcessPayables payable = xcessPayables.get(i);

			if (payable.getTotPaidNow().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);

			if (RepayConstants.EXAMOUNTTYPE_EMIINADV.equals(payable.getPayableType())) {
				rcd.setPaymentType(ReceiptMode.EMIINADV);
			} else if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(payable.getPayableType())) {
				rcd.setPaymentType(ReceiptMode.EXCESS);
			} else if (RepayConstants.EXAMOUNTTYPE_ADVINT.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_ADVINT);
			} else if (RepayConstants.EXAMOUNTTYPE_ADVEMI.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_ADVEMI);
			} else if (RepayConstants.EXAMOUNTTYPE_CASHCLT.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_CASHCLT);
			} else if (RepayConstants.EXAMOUNTTYPE_DSF.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_DSF);
			} else {
				rcd.setPaymentType(ReceiptMode.PAYABLE);
			}

			rcd.setPayAgainstID(payable.getPayableID());
			if (receiptData.getTotalPastDues().compareTo(payable.getTotPaidNow()) >= 0) {
				rcd.setDueAmount(payable.getTotPaidNow());
				receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(payable.getPaidNow()));
			} else {
				rcd.setDueAmount(receiptData.getTotalPastDues());
				receiptData.setTotalPastDues(BigDecimal.ZERO);
			}
			if (receiptPurposeCtg < 2) {
				rcd.setAmount(receiptData.getReceiptHeader().getReceiptAmount());
			} else {
				rcd.setAmount(rcd.getDueAmount());
			}
			rcd.setValueDate(rch.getValueDate());
			rcd.setReceivedDate(rch.getValueDate());
			// rcd.setReceivedDate(this.receivedDate.getValue());
			rcd.setPayOrder(rcdList.size() + 1);
			rcd.setReceiptSeqID(getReceiptSeqID(rcd));

			ManualAdviseMovements mam = new ManualAdviseMovements();

			mam.setAdviseID(payable.getPayableID());
			mam.setMovementDate(rcd.getReceivedDate());
			mam.setMovementAmount(payable.getTotPaidNow());
			mam.setTaxComponent(payable.getTaxType());
			mam.setPaidAmount(payable.getTotPaidNow());
			mam.setFeeTypeCode(payable.getFeeTypeCode());

			// GST Calculations
			if (StringUtils.isNotBlank(payable.getTaxType())) {
				if (taxPercMap == null) {
					taxPercMap = GSTCalculator.getTaxPercentages(fm);
				}

				TaxHeader taxHeader = new TaxHeader();
				taxHeader.setNewRecord(true);
				taxHeader.setRecordType(PennantConstants.RCD_ADD);
				taxHeader.setVersion(taxHeader.getVersion() + 1);

				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				taxDetails.add(getTaxDetail(RuleConstants.CODE_CGST, taxPercMap.get(RuleConstants.CODE_CGST),
						payable.getPaidCGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_SGST, taxPercMap.get(RuleConstants.CODE_SGST),
						payable.getPaidSGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_IGST, taxPercMap.get(RuleConstants.CODE_IGST),
						payable.getPaidIGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_UGST, taxPercMap.get(RuleConstants.CODE_UGST),
						payable.getPaidUGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_CESS, taxPercMap.get(RuleConstants.CODE_CESS),
						payable.getPaidCESS()));

				mam.setTaxHeader(taxHeader);
			} else {
				mam.setTaxHeader(null);
			}

			rcd.setPayAdvMovement(mam);

			if (rcd.getReceiptSeqID() <= 0) {
				rcdList.add(rcd);
			}

			if (receiptData.getTotalPastDues().compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
		}

		// rch.setReceiptDetails(rcdList);
		return receiptData;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, BigDecimal taxAmount) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		taxes.setNetTax(taxAmount);
		taxes.setActualTax(taxAmount);
		return taxes;
	}

	private FinReceiptData createNonXcessRCD() {

		if (ReceiptMode.EXCESS.equals(receiptData.getReceiptHeader().getReceiptMode())
				|| ReceiptMode.EMIINADV.equals(receiptData.getReceiptHeader().getReceiptMode())
				|| StringUtils.equals(ReceiptMode.PAYABLE, receiptData.getReceiptHeader().getReceiptMode())) {
			return receiptData;
		}
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.setTransactionRef(this.favourNo.getValue());
		rch.setBankCode(this.bankCode.getValue());
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		FinReceiptDetail rcd = new FinReceiptDetail();

		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(rch.getSubReceiptMode());
		rcd.setPayAgainstID(0);
		rcd.setAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()));
		if (receiptData.getTotalPastDues().compareTo(rch.getReceiptAmount()) >= 0) {
			rcd.setDueAmount(rch.getReceiptAmount());
			receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(rch.getReceiptAmount()));
		} else {
			rcd.setDueAmount(receiptData.getTotalPastDues());
			receiptData.setTotalPastDues(BigDecimal.ZERO);
		}

		rcd.setFavourNumber(this.favourNo.getValue());
		rcd.setValueDate(rch.getValueDate());
		rcd.setBankCode(this.bankCode.getValue());
		rcd.setFavourName(this.favourName.getValue());
		rcd.setDepositNo(this.depositNo.getValue());
		rcd.setPaymentRef(this.paymentRef.getValue());
		rcd.setTransactionRef(this.transactionRef.getValue());
		rcd.setChequeAcNo(this.chequeAcNo.getValue());
		rcd.setReceivedDate(rch.getValueDate());

		rcd.setReceiptSeqID(getReceiptSeqID(rcd));
		rcd.setReceiptID(rch.getReceiptID());

		// rcd.setReceivedDate(this.receivedDate.getValue());
		if (rcd.getReceiptSeqID() <= 0) {
			rcd.setPayOrder(rcdList.size() + 1);
			rcdList.add(rcd);
		} else {
			for (int i = 0; i < rcdList.size(); i++) {
				FinReceiptDetail finReceiptDetail = rcdList.get(i);
				if (finReceiptDetail.getReceiptSeqID() == rcd.getReceiptSeqID()) {
					rcdList.remove(finReceiptDetail);
					rcd.setPayOrder(finReceiptDetail.getPayOrder());
					rcd.setRepayHeader(finReceiptDetail.getRepayHeader());
					rcd.setDueAmount(finReceiptDetail.getDueAmount());
					rcdList.add(rcd);
				}
			}
		}

		rch.setReceiptDetails(rcdList);
		return receiptData;
	}

	private long getReceiptSeqID(FinReceiptDetail recDtl) {
		long receiptSeqId = 0;
		if (recDtls.isEmpty()) {
			return receiptSeqId;
		}
		for (FinReceiptDetail dtl : recDtls) {
			if (recDtl.getPaymentType().equals(dtl.getPaymentType())
					&& recDtl.getPayAgainstID() == dtl.getPayAgainstID()) {
				receiptSeqId = dtl.getReceiptSeqID();
			}
		}
		return receiptSeqId;
	}

	private void setRepayDetailData() throws InterruptedException {
		logger.debug("Entering");

		// Repay Schedule Data rebuild
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();
		for (int i = 0; i < receiptDetailList.size(); i++) {
			FinRepayHeader rph = receiptDetailList.get(i).getRepayHeader();
			if (rph != null) {
				if (rph.getRepayScheduleDetails() != null) {
					rpySchdList.addAll(rph.getRepayScheduleDetails());
				}
			}

		}

		// Making Single Set of Repay Schedule Details and sent to Rendering
		List<RepayScheduleDetail> tempRpySchdList = ObjectUtil.clone(rpySchdList);
		Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
		for (RepayScheduleDetail rpySchd : tempRpySchdList) {

			RepayScheduleDetail curRpySchd = null;
			if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
				curRpySchd = rpySchdMap.get(rpySchd.getSchDate());

				if (curRpySchd.getPrincipalSchdBal().compareTo(rpySchd.getPrincipalSchdBal()) < 0) {
					curRpySchd.setPrincipalSchdBal(rpySchd.getPrincipalSchdBal());
				}

				if (curRpySchd.getProfitSchdBal().compareTo(rpySchd.getProfitSchdBal()) < 0) {
					curRpySchd.setProfitSchdBal(rpySchd.getProfitSchdBal());
				}

				curRpySchd.setPrincipalSchdPayNow(
						curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
				curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
				curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
				curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
				rpySchdMap.remove(rpySchd.getSchDate());
			} else {
				curRpySchd = rpySchd;
			}

			// Adding New Repay Schedule Object to Map after Summing data
			rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
		}

		doFillRepaySchedules(sortRpySchdDetails(new ArrayList<>(rpySchdMap.values())));
		if (rpySchdMap.isEmpty()) {
			this.receiptDetailsTab.setSelected(true);
		}

		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnReceipt"));
		this.btnChangeReceipt
				.setDisabled(!getUserWorkspace().isAllowed("button_CrossLoanKnockOffDialog_btnChangeReceipt"));
		this.btnCalcReceipts.setDisabled(true);

		if (isCancel) {
			this.btnReceipt.setDisabled(false);
			this.btnChangeReceipt.setDisabled(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayments Amount
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnChangeReceipt(Event event)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();
		// this.btnChangeReceipt.setDisabled(true);
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnReceipt(Event event) throws Exception {
		doSave();
		crossLoanKnockOffListCtrl.search();
	}

	public void doSave() throws WrongValueException, InterruptedException {
		logger.debug("Entering");
		try {
			boolean recReject = false;
			if (this.userAction.getSelectedItem() != null
					&& ("Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
				recReject = true;
			}

			// PSD:182995 Not allowed to cancel the Receipt for Cancelled Loans
			if (!recReject) {
				boolean canProcessReceipt = receiptService.canProcessReceipt(Long.valueOf(this.receiptId.getValue()));

				if (!canProcessReceipt && !enqiryModule) {
					String[] valueParm = new String[1];
					valueParm[0] = "Unable to process the request, Loan is in in-active state... Please Cancel/Reject the Record ";
					MessageUtil.showError(valueParm[0]);
					return;
				}
			}

			doClearMessage();
			doSetValidation();
			doWriteComponentsToBean();
			if (!recReject) {
				// FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
				FinReceiptData data = receiptData;

				if (!StringUtils.equals(receiptData.getReceiptHeader().getReceiptModeStatus(),
						RepayConstants.PAYSTATUS_BOUNCE)
						&& !StringUtils.equals(receiptData.getReceiptHeader().getReceiptModeStatus(),
								RepayConstants.PAYSTATUS_CANCEL)) {
					calculateRepayments();
				}
				List<FinReceiptDetail> receiptDetails = data.getReceiptHeader().getReceiptDetails();

				BigDecimal totReceiptAmt = receiptData.getTotReceiptAmount();
				BigDecimal feeAmount = receiptData.getReceiptHeader().getTotFeeAmount();
				data.getReceiptHeader().setTotFeeAmount(feeAmount);
				data.getReceiptHeader().setReceiptAmount(totReceiptAmt);
				data.getReceiptHeader().setRemarks(this.remarks.getValue());

				for (FinReceiptDetail receiptDetail : receiptDetails) {
					if (!StringUtils.equals(RepayConstants.PAYTYPE_EXCESS, data.getReceiptHeader().getReceiptMode())
							&& StringUtils.equals(receiptDetail.getPaymentType(),
									data.getReceiptHeader().getSubReceiptMode())) {
						receiptDetail.setFavourNumber(this.favourNo.getValue());
						receiptDetail.setValueDate(this.valueDate.getValue());
						receiptDetail.setBankCode(this.bankCode.getValue());
						receiptDetail.setFavourName(this.favourName.getValue());
						receiptDetail.setDepositNo(this.depositNo.getValue());
						receiptDetail.setPaymentRef(this.paymentRef.getValue());
						receiptDetail.setTransactionRef(this.transactionRef.getValue());
						receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());

						// Setting data
						if (StringUtils.equals(ReceiptMode.CHEQUE, receiptDetail.getPaymentType())
								|| StringUtils.equals(ReceiptMode.DD, receiptDetail.getPaymentType())) {
							receiptData.getReceiptHeader().setTransactionRef(this.favourNo.getValue());
						} else {
							receiptData.getReceiptHeader().setTransactionRef(this.transactionRef.getValue());
						}
						receiptData.getReceiptHeader().setValueDate(this.valueDate.getValue());
						receiptData.getReceiptHeader().setPartnerBankId(receiptDetail.getFundingAc());
						receiptData.getReceiptHeader().setRemarks(this.remarks.getValue());
					}
				}

			}

			if (recReject || isValidateData(false)) {
				// If Schedule Re-modified Save into DB or else only add
				// Repayments Details
				doProcessReceipt();
			}

			deAllocateAuthorities("FinanceMainDialog");

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		} catch (AppException pfe) {
			MessageUtil.showError(pfe.getMessage());
			return;
		} catch (WrongValuesException we) {
			throw we;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Process Repayment Details
	 * 
	 * @throws Exception
	 */
	private void doProcessReceipt() throws Exception {
		logger.debug("Entering");
		receiptData.getFinanceDetail().setUserAction(this.userAction.getSelectedItem().getLabel());
		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
			}
		}

		// set Crossloan Header
		crossLoanHeader.setFinReceiptData(receiptData);

		if (isWorkFlowEnabled()) {

			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			crossLoanHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(crossLoanHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, crossLoanHeader);
				}

				if ("".equals(nextTaskId)) {
					nextRoleCode = getFirstTaskOwner();
				} else {
					String[] nextTasks = nextTaskId.split(";");

					if (nextTasks.length > 0) {
						for (int i = 0; i < nextTasks.length; i++) {
							if (nextRoleCode.length() > 1) {
								nextRoleCode = nextRoleCode.concat(",");
							}
							nextRoleCode += getTaskOwner(nextTasks[i]);
						}
					}
				}

				if (isNotesMandatory(taskId, crossLoanHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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
			crossLoanHeader.setTaskId(taskId);
			crossLoanHeader.setNextTaskId(nextTaskId);
			crossLoanHeader.setRoleCode(getRole());
			crossLoanHeader.setNextRoleCode(nextRoleCode);
			crossLoanHeader.setRecordType(recordType);
			crossLoanHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			crossLoanHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			crossLoanHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		}

		// Duplicate Creation of Object
		CrossLoanKnockOff crossLoanHKnockOff = ObjectUtil.clone(crossLoanHeader);

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(crossLoanHKnockOff.getRecordType())) {

				crossLoanHKnockOff.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				crossLoanHKnockOff.setVersion(1);
				if (crossLoanHKnockOff.isNewRecord()) {
					crossLoanHKnockOff.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					if (isCancel) {
						crossLoanHKnockOff.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else {
					crossLoanHKnockOff.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					crossLoanHKnockOff.setNewRecord(true);
				}
			}

		} else {
			crossLoanHKnockOff.setVersion(crossLoanHKnockOff.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		crossLoanHKnockOff.getFinReceiptData().setEventCodeRef(eventCode);

		// save it to database
		try {

			if (doProcess(crossLoanHKnockOff, tranType)) {

				if (receiptListCtrl != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(crossLoanHKnockOff.getNextTaskId())) {
					crossLoanHKnockOff.setNextRoleCode("");

				}
				String nextRoleCode = crossLoanHKnockOff.getNextRoleCode();

				String msg = PennantApplicationUtil.getSavingStatus(crossLoanHKnockOff.getRoleCode(), nextRoleCode,
						crossLoanHKnockOff.getCrossLoanTransfer().getToFinReference(), " Loan ",
						crossLoanHKnockOff.getRecordStatus(), false);
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 * 
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData schData = financeDetail.getFinScheduleData();
		FinanceType finType = schData.getFinanceType();
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		this.favourName.setValue(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getEntityDesc());
		this.fromFinReference.setValue(crossLoanHeader.getCrossLoanTransfer().getFromFinReference());
		this.excessRef.setValue(String.valueOf(crossLoanHeader.getCrossLoanTransfer().getExcessId()));
		this.toFinReference.setValue(crossLoanHeader.getCrossLoanTransfer().getToFinReference());
		this.valueDate.setValue(rch.getValueDate());
		if (StringUtils.isEmpty(rch.getAllocationType())) {
			rch.setAllocationType(RepayConstants.ALLOCTYPE_AUTO);
		}

		fillComboBox(this.receiptPurpose, rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(),
				",FeePayment,");
		this.receiptPurpose.setDisabled(true);
		/*
		 * fillComboBox(this.excessAdjustTo, rch.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(),
		 * "");
		 */

		if (finType.isDeveloperFinance()) {
			fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getKnockOffFromVlaues(), "");
		} else {
			fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getKnockOffFromVlaues(), "");
		}

		if (isKnockOff) {
			fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getKnockOffFromVlaues(), "A,P");
		}

		this.receiptMode.setDisabled(true);
		appendReceiptMode(rch);
		// appendScheduleMethod(rch);

		this.receiptAmount.setValue(PennantApplicationUtil
				.formateAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()), formatter));
		if (isEarlySettle) {
			this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(rch.getReceiptAmount(), formatter));
		}
		this.receiptAmount.setDisabled(true);
		// this.remarks.setValue(crossLoanHeader.getRem);
		this.receiptDate.setValue(rch.getReceiptDate());
		this.receiptId.setValue(String.valueOf(rch.getReceiptID()));
		this.receiptDate.setDisabled(true);
		if (isCancel) {
			this.cancelReason.setValue(rch.getCancelReason());
			this.cancelRemarks.setValue(rch.getCancelRemarks());
		}

		List<ValueLabel> allocationMethods = PennantStaticListUtil.getAllocationMethods();

		Set<String> exclude = new HashSet<>();

		if ((StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE) && isEarlySettle)
				|| (StringUtils.equals(FinServiceEvent.EARLYRPY, rch.getReceiptPurpose())
						&& "N".equals(SysParamUtil.getValueAsString("ALW_PP_MANUAL_ALLOC")))) {
			exclude.add(AllocationType.MANUAL);
			this.allocationMethod.setDisabled(true);
		}

		if (!schData.getFinanceMain().isUnderSettlement()) {
			exclude.add(AllocationType.NO_ALLOC);
		}

		fillComboBox(this.allocationMethod, rch.getAllocationType(), excludeComboBox(allocationMethods, exclude));

		fillComboBox(this.receivedFrom, rch.getReceivedFrom(), PennantStaticListUtil.getReceivedFrom(), "");

		// doFillEarlyPayMethods(valueDate);
		appendScheduleMethod(receiptData.getReceiptHeader());
		// Receipt Mode Status Details

		// Receipt Mode Details , if FinReceiptDetails Exists
		setBalances();
		checkByReceiptMode(rch.getReceiptMode(), false);
		this.valueDate.setValue(rch.getValueDate());
		// Separating Receipt Amounts based on user entry, if exists
		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail rcd = rch.getReceiptDetails().get(i);

				if (!ReceiptMode.EXCESS.equals(rcd.getPaymentType())
						&& !ReceiptMode.EMIINADV.equals(rcd.getPaymentType())
						&& !ReceiptMode.PAYABLE.equals(rcd.getPaymentType())
						&& !ReceiptMode.ADVINT.equals(rcd.getPaymentType())) {

					this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(rcd.getAmount(), formatter));
					this.favourNo.setValue(rcd.getFavourNumber());
					this.valueDate.setValue(rcd.getValueDate());
					this.bankCode.setValue(rcd.getBankCode());
					this.bankCode.setDescription(rcd.getBankCodeDesc());
					this.favourName.setValue(rcd.getFavourName());
					this.depositNo.setValue(rcd.getDepositNo());
					this.paymentRef.setValue(rcd.getPaymentRef());
					this.transactionRef.setValue(rcd.getTransactionRef());
					this.externalRefrenceNumber.setValue(rch.getExtReference());
					this.chequeAcNo.setValue(rcd.getChequeAcNo());
				}
			}
		}
		doFillExcessPayables();
		// Render Excess Amount Details
		doFillAllocationDetail();

		// Only In case of partial settlement process, Display details for
		// effective Schedule
		boolean visibleSchdTab = true;
		if (receiptPurposeCtg == 1) {

			FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
			finScheduleData.setFinanceMain(getFinanceDetail().getFinScheduleData().getFinanceMain());
			finScheduleData
					.setFinanceScheduleDetails(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());

			// Fill Effective Schedule Details
			doFillScheduleList(finScheduleData);

			// Dashboard Details Report
			/*
			 * doLoadTabsData(); doShowReportChart(finScheduleData);
			 */

		}

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		// On Loading Data Render for Schedule
		if (receiptHeader != null && receiptHeader.getReceiptDetails() != null
				&& !receiptHeader.getReceiptDetails().isEmpty()) {
			this.btnCalcReceipts.setDisabled(true);
			setRepayDetailData();
		}

		getFinanceDetail().setModuleDefiner(FinServiceEvent.RECEIPT);

		if (visibleSchdTab) {
			appendScheduleDetailTab(true, false);
		}

		this.recordStatus.setValue(receiptHeader.getRecordStatus());
		if (receiptPurposeCtg == 2 && (StringUtils.equals(ReceiptMode.CHEQUE, receiptHeader.getReceiptMode())
				|| StringUtils.equals(ReceiptMode.DD, receiptHeader.getReceiptMode()))) {
			this.valueDate.setValue(rch.getValueDate());
			this.valueDate.setReadonly(true);
			this.valueDate.setDisabled(true);
		}

		// Receipt Type
		// this.rcptReason.setValue(rch.getRcptReason());

		logger.debug("Leaving");
	}

	private void appendReceiptMode(FinReceiptHeader rch) {
		if (StringUtils.equals(rch.getSubReceiptMode(), PennantConstants.List_Select)
				&& StringUtils.equals(rch.getReceiptChannel(), PennantConstants.List_Select)) {
			receiptTypeLabel.setVisible(false);
			subReceiptMode.setVisible(false);
			return;
		}

		if ((ReceiptMode.ONLINE.equals(rch.getReceiptMode())) && rch.getSubReceiptMode() != null
				&& !StringUtils.equals(rch.getSubReceiptMode(), PennantConstants.List_Select)) {
			receiptTypeLabel.setVisible(true);
			subReceiptMode.setVisible(true);
			receiptTypeLabel.setValue(Labels.getLabel("label_ReceiptPayment_SubReceiptMode.value"));
			// fillComboBox(subReceiptMode, rch.getSubReceiptMode(),
			// PennantStaticListUtil.getSubReceiptPaymentModes(),"");
			fillComboBox(subReceiptMode, rch.getSubReceiptMode(), PennantAppUtil.getFieldCodeList("SUBRECMODE"), "");
			this.subReceiptMode.setDisabled(true);
		}
	}

	private void appendScheduleMethod(FinReceiptHeader rch) {

		if (receiptPurposeCtg != 1) {
			scheduleLabel.setValue(Labels.getLabel("label_ReceiptPayment_ExcessAmountAdjustment.value"));
			this.excessAdjustTo.setVisible(true);
			this.excessAdjustTo.setDisabled(false);

			if (receiptPurposeCtg == 0) {
				this.excessAdjustTo.setDisabled(true);
			}

			Set<String> exclude = new HashSet<>();

			if (receiptPurposeCtg == 2) {
				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);

				exclude.add("A");
				exclude.add("B");
				exclude.add("S");
			}

			List<ValueLabel> excessAdjustToList = PennantStaticListUtil.getExcessAdjustmentTypes();

			fillComboBox(excessAdjustTo, rch.getExcessAdjustTo(), excludeComboBox(excessAdjustToList, exclude));
		} else {
			this.effScheduleMethod.setVisible(true);
			this.effScheduleMethod.setDisabled(false);
			this.excessAdjustTo.setVisible(false);
			this.excessAdjustTo.setDisabled(true);
			scheduleLabel.setValue(Labels.getLabel("label_CrossLoanKnockOffDialog_EffecScheduleMethod.value"));

			List<ValueLabel> epyMethodList = getEffectiveSchdMethods();
			String defaultMethod = "";

			// final String defMethod = getFinanceDetail().getFinScheduleData().getFinanceType().getFinScheduleOn();

			if (!epyMethodList.isEmpty()) {
				defaultMethod = StringUtils.isEmpty(rch.getEffectSchdMethod()) ? epyMethodList.get(0).getValue()
						: rch.getEffectSchdMethod();

				// PSD : 165320
				/*
				 * if(epyMethodList.stream().filter(o -> o.getValue().equals(defMethod)).findFirst().isPresent()){
				 * defaultMethod = defMethod; }
				 */
			}

			if (!getFinanceMain().isManualSchedule()) {
				fillComboBox(effScheduleMethod, defaultMethod, getEffectiveSchdMethods(), "");
			} else {

				fillComboBox(effScheduleMethod, "", getEffectiveSchdMethods(), "");
			}
		}

	}

	private List<ValueLabel> getEffectiveSchdMethods() {
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType finType = finScheduleData.getFinanceType();
		List<ValueLabel> repyMethodList = new ArrayList<>();
		boolean isRpyStp = false;

		if (finMain.isStepFinance() && (CalculationConstants.SCHMTHD_PRI_PFT.equals(finMain.getScheduleMethod())
				|| CalculationConstants.SCHMTHD_PRI.equals(finMain.getScheduleMethod()))) {
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
		}

		if (finMain.isStepFinance()
				&& StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
			List<FinanceStepPolicyDetail> stpDetails = getFinanceDetail().getFinScheduleData().getStepPolicyDetails();
			if (CollectionUtils.isNotEmpty(stpDetails)) {
				for (FinanceStepPolicyDetail stp : stpDetails) {
					if (StringUtils.equals(stp.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
						isRpyStp = true;
						break;
					}
				}
			}
		}

		if (finMain.isApplySanctionCheck()) {
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_ADJMUR, Labels.getLabel("label_Adjust_To_Maturity")));
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
		} else if (finMain.isAlwFlexi() || finType.isDeveloperFinance()) {
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
		} else {
			if (finMain.isStepFinance() && PennantConstants.STEPPING_CALC_PERC.equals(finMain.getCalcOfSteps())
					&& finMain.isAllowGrcPeriod() && FinanceConstants.STEPTYPE_PRIBAL.equals(finMain.getStepType())
					&& DateUtil.compare(receiptData.getValueDate(), finMain.getGrcPeriodEndDate()) <= 0
					&& (CalculationConstants.SCHMTHD_PRI.equals(finMain.getScheduleMethod())
							|| CalculationConstants.SCHMTHD_PRI_PFT.equals(finMain.getScheduleMethod()))) {
				repyMethodList
						.add(new ValueLabel(CalculationConstants.RPYCHG_STEPPOS, Labels.getLabel("label_POSStep")));
			} else if (finMain.isStepFinance() && isRpyStp) {
				if (finMain.getFinCurrAssetValue().compareTo(finMain.getFinAssetValue()) == 0) {
					repyMethodList.add(new ValueLabel(CalculationConstants.RPYCHG_ADJTNR_STEP,
							Labels.getLabel("label_Step_Adj_Tenor")));
					repyMethodList.add(new ValueLabel(CalculationConstants.RPYCHG_ADJEMI_STEP,
							Labels.getLabel("label_Step_Adj_EMI")));
				} else {
					repyMethodList.add(new ValueLabel(CalculationConstants.RPYCHG_ADJTNR_STEP,
							Labels.getLabel("label_Step_Adj_Tenor")));
				}
			} else {
				if (StringUtils.isNotEmpty(finType.getAlwEarlyPayMethods())) {
					String[] epMthds = finType.getAlwEarlyPayMethods().trim().split(",");
					if (epMthds.length > 0) {
						List<String> list = Arrays.asList(epMthds);
						for (ValueLabel label : PennantStaticListUtil.getEarlyPayEffectOn()) {
							if (list.contains(label.getValue().trim())) {
								repyMethodList.add(label);
							}
						}
					}
				}
			}
		}
		return repyMethodList;
	}

	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * 
	 * @param header
	 * @param allocatePaidMap
	 */

	public void doFillAllocationDetail() {
		logger.debug("Entering");
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocationsSummary();
		if (!receiptData.isCalReq()) {
			allocationList = receiptData.getReceiptHeader().getAllocations();
		}
		this.listBoxPastdues.getItems().clear();

		// Get Receipt Purpose to Make Waiver amount Editable
		String label = Labels.getLabel("label_RecceiptDialog_AllocationType_");
		boolean isManAdv = false;
		doRemoveValidation();
		doClearMessage();

		for (int i = 0; i < allocationList.size(); i++) {
			createAllocateItem(allocationList.get(i), isManAdv, label, i);
		}

		addDueFooter(formatter);
		addExcessAmt();

		if (receiptData.getPaidNow()
				.compareTo(receiptData.getReceiptHeader().getReceiptAmount().add(receiptData.getExcessAvailable())) > 0
				&& !receiptData.isForeClosure()) {
			String[] err = new String[2];

			err[0] = PennantApplicationUtil.formatAmount(receiptData.getPaidNow(), formatter);
			err[1] = PennantApplicationUtil.formatAmount(
					receiptData.getReceiptHeader().getReceiptAmount().add(receiptData.getExcessAvailable()), formatter);
			MessageUtil
					.showError(new ErrorDetail("WFEE12", Labels.getLabel("label_Allocation_More_than_receipt"), err));
			return;
		}

		logger.debug("Leaving");
	}

	private void addExcessAmt() {
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0) {
			Listitem item = new Listitem();
			Listcell lc = null;
			item = new Listitem();
			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_ExcessType_EXCESS"));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(receiptData.getRemBal(), formatter));

			lc.setId("ExcessAmount");
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			this.listBoxPastdues.appendChild(item);
		}
	}

	private void createAllocateItem(ReceiptAllocationDetail allocate, boolean isManAdv, String desc, int idx) {
		logger.debug(Literal.ENTERING);
		String allocateMthd = getComboboxValue(this.allocationMethod);

		if (Allocation.NPFT.equals(allocate.getAllocationType())
				|| Allocation.FUT_NPFT.equals(allocate.getAllocationType())) {
			return;
		}

		Listitem item = new Listitem();
		Listcell lc = null;
		addBoldTextCell(item, allocate.getTypeDesc(), allocate.isSubListAvailable(), idx);
		if (allocate.getAllocationTo() < 0) {
			for (FinFeeDetail fee : receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
				if (allocate.getAllocationTo() == -(fee.getFeeTypeID())
						&& "PERCENTG".equals(fee.getCalculationType())) {
					lc = (Listcell) item.getChildren().get(0);
					Button button = new Button("Fee Details");
					button.setId(String.valueOf(idx));
					button.addForward("onClick", windowCrossLoanKnockOffDialog, "onFeeDetailsClick", button.getId());
					lc.appendChild(button);

					break;
				}
			}
		}
		addAmountCell(item, allocate.getTotRecv(), ("AllocateActualDue_" + idx), false);
		// FIXME: PV. Pending code to get in process allocations
		addAmountCell(item, allocate.getInProcess(), ("AllocateInProess_" + idx), true);
		// addAmountCell(item, allocate.getDueGST(), ("AllocateCurGST_" + idx), true);
		// addAmountCell(item, allocate.getTdsDue(), ("AllocateTDSDue_" + idx), true);
		addAmountCell(item, allocate.getTotalDue(), ("AllocateCurDue_" + idx), true);

		lc = new Listcell();
		CurrencyBox allocationPaid = new CurrencyBox();
		allocationPaid.setStyle("text-align:right;");
		allocationPaid.setBalUnvisible(true, true);
		setProps(allocationPaid, false, formatter, 120);
		allocationPaid.setId("AllocatePaid_" + idx);
		allocationPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getTotalPaid(), formatter));
		allocationPaid.addForward("onFulfill", this.windowCrossLoanKnockOffDialog, "onAllocatePaidChange", idx);
		allocationPaid.setReadonly(true);
		lc.appendChild(allocationPaid);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		// Editable Amount - Total Paid

		lc = new Listcell();
		CurrencyBox allocationNetPaid = new CurrencyBox();
		allocationNetPaid.setStyle("text-align:right;");
		allocationNetPaid.setBalUnvisible(true, true);
		setProps(allocationNetPaid, false, formatter, 120);
		allocationNetPaid.setId("AllocateNetPaid_" + idx);
		allocationNetPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getPaidAmount(), formatter));
		allocationNetPaid.addForward("onFulfill", this.windowCrossLoanKnockOffDialog, "onAllocateNetPaidChange", idx);
		allocationNetPaid.setReadonly(true);

		lc.appendChild(allocationNetPaid);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		// addAmountCell(item, allocate.getPaidGST(), ("PaidGST_" + idx), true);
		// addAmountCell(item, allocate.getTdsPaid(), ("PaidTDS_" + idx), true);

		lc = new Listcell();
		CurrencyBox allocationWaived = new CurrencyBox();
		allocationWaived.setStyle("text-align:right;");
		allocationWaived.setBalUnvisible(true, true);
		setProps(allocationWaived, false, formatter, 120);
		allocationWaived.setId("AllocateWaived_" + idx);
		allocationWaived.setValue(PennantApplicationUtil.formateAmount(allocate.getWaivedAmount(), formatter));
		allocationWaived.addForward("onFulfill", this.windowCrossLoanKnockOffDialog, "onAllocateWaivedChange", idx);
		allocationWaived.setReadonly(true);
		if (allocate.getAllocationTo() < 0 && PennantConstants.YES.equals(allocate.getWaiverAccepted())) {
			allocationWaived.setReadonly(!getUserWorkspace().isAllowed("CrossLoanKnockOffDialog_WaivedAmount"));
		}

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
			String[] arg = new String[1];
			arg[0] = PennantApplicationUtil.amountFormate(allocate.getDueAmount(), formatter);
			allocationWaived.setTooltiptext(Labels.getLabel("label_WaivedAllocation_More_than_receipt", arg));
		}

		if (isForeClosure) {
			allocationWaived.setReadonly(!getUserWorkspace().isAllowed("CrossLoanKnockOffDialog_WaivedAmount"));
			if (PennantStaticListUtil.getNoWaiverList().contains(allocate.getAllocationType())) {
				allocationWaived.setReadonly(true);
			}
		}

		lc.appendChild(allocationWaived);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		if (AllocationType.MANUAL.equals(allocateMthd)) {
			allocationNetPaid.setReadonly(!getUserWorkspace().isAllowed("CrossLoanKnockOffDialog_PaidAmount"));
			// allocationPaid.setReadonly(false);
		}

		// Balance Due AMount
		addAmountCell(item, allocate.getBalance(), ("AllocateBalDue_" + idx), true);

		// if (allocate.isEditable()){
		this.listBoxPastdues.appendChild(item);
		// }

		logger.debug(Literal.LEAVING);
	}

	public void onFeeDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("data", receiptData);
		map.put("buttonId", buttonId);
		map.put("crossLoanKnockOffDialogCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/EventFeeDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("details",
				receiptData.getReceiptHeader().getAllocationsSummary().get(Integer.parseInt(buttonId)).getSubList());
		map.put("buttonId", buttonId);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/BounceDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Adding footer to show Totals
	 * 
	 * @param dueAmount
	 * @param paidAmount
	 * @param waivedAmount
	 * @param formatter
	 * @param isPastDue
	 */
	private void addDueFooter(int formatter) {
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);
		BigDecimal totRecv = BigDecimal.ZERO;
		BigDecimal totDue = BigDecimal.ZERO;
		BigDecimal totGST = BigDecimal.ZERO;
		BigDecimal inProc = BigDecimal.ZERO;
		BigDecimal totPaid = BigDecimal.ZERO;
		BigDecimal paid = BigDecimal.ZERO;
		BigDecimal paidGST = BigDecimal.ZERO;
		BigDecimal waived = BigDecimal.ZERO;
		BigDecimal waivedGST = BigDecimal.ZERO;
		BigDecimal gstAmount = BigDecimal.ZERO;
		BigDecimal tdsPaid = BigDecimal.ZERO;
		BigDecimal tdsDue = BigDecimal.ZERO;

		List<ReceiptAllocationDetail> allocList = receiptData.getReceiptHeader().getAllocationsSummary();

		if (!receiptData.isCalReq()) {
			allocList = receiptData.getReceiptHeader().getAllocations();
		}

		for (ReceiptAllocationDetail allocate : allocList) {

			if (!Allocation.EMI.equals(allocate.getAllocationType())
					&& !Allocation.NPFT.equals(allocate.getAllocationType())
					&& !Allocation.FUT_NPFT.equals(allocate.getAllocationType())) {
				totRecv = totRecv.add(allocate.getTotRecv());
				totGST = totGST.add(allocate.getDueGST());
				totDue = totDue.add(allocate.getTotalDue());
				inProc = inProc.add(allocate.getInProcess());
				totPaid = totPaid.add(allocate.getTotalPaid());
				paid = paid.add(allocate.getPaidAmount());
				paidGST = paidGST.add(allocate.getPaidGST());
				waived = waived.add(allocate.getWaivedAmount());
				tdsDue = tdsDue.add(allocate.getTdsDue());
				tdsPaid = tdsPaid.add(allocate.getTdsPaid());

			}

		}
		receiptData.setPaidNow(paid);
		addAmountCell(item, totRecv, null, true);
		addAmountCell(item, inProc, null, true);
		// addAmountCell(item, totGST, null, true);
		// addAmountCell(item, tdsDue, null, true);
		addAmountCell(item, totDue, null, true);
		addAmountCell(item, totPaid, null, true);
		addAmountCell(item, paid, null, true);
		// addAmountCell(item, paidGST, null, true);
		// addAmountCell(item, tdsPaid, null, true);
		addAmountCell(item, waived.subtract(gstAmount), null, true);
		addAmountCell(item, totDue.subtract(paid).subtract(waived), null, true);

		this.listBoxPastdues.appendChild(item);
	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAllocateDueChange(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		int idx = (int) event.getData();
		String id = "AllocateActualDue_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationDue = (CurrencyBox) this.listBoxPastdues.getFellow(id);

		BigDecimal dueAmount = PennantApplicationUtil.unFormateAmount(allocationDue.getValidateValue(), formatter);
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())
					&& allocteDtl.getAllocationTo() == allocate.getAllocationTo()) {
				allocteDtl.setTotRecv(dueAmount);
				allocteDtl.setTotalDue(dueAmount);
			}
		}

		changeDue();
		// if no extra balance or partial pay disable excessAdjustTo
		if (receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			if (receiptPurposeCtg == 0) {
				this.excessAdjustTo.setDisabled(true);
			} else {
				this.excessAdjustTo.setDisabled(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAllocatePaidChange(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		BigDecimal tds = BigDecimal.ZERO;
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocatePaid_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationPaid = (CurrencyBox) this.listBoxPastdues.getFellow(id);

		BigDecimal paidAmount = PennantApplicationUtil.unFormateAmount(allocationPaid.getValidateValue(), formatter);
		BigDecimal dueAmount = allocate.getTotRecv().subtract(allocate.getInProcess());
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
			dueAmount = dueAmount.add(allocate.getDueGST());
		}
		BigDecimal waivedAmount = rch.getAllocationsSummary().get(idx).getWaivedAmount();
		if (paidAmount.compareTo(dueAmount.subtract(waivedAmount)) > 0) {
			paidAmount = dueAmount.subtract(waivedAmount);
		}

		allocate.setTotalPaid(paidAmount);
		allocate.setPaidAmount(paidAmount);

		BigDecimal excGst = receiptCalculator.getExclusiveGSTAmount(allocate, paidAmount);
		if (allocate.isTdsReq()) {
			tds = receiptCalculator.getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					excGst);
		}
		if (allocate.isSubListAvailable()) {
			receiptCalculator.splitAllocSummary(receiptData, idx);
		} else {
			if (Allocation.EMI.equals(allocate.getAllocationType())) {
				allocateEmi(paidAmount);
			} else if (Allocation.PFT.equals(allocate.getAllocationType())) {
				allocateNPft(paidAmount);
			} else if (Allocation.PRI.equals(allocate.getAllocationType())) {
				allocatePRI(paidAmount);
			} else {
				for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
					if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())
							&& allocteDtl.getAllocationTo() == allocate.getAllocationTo()) {
						allocteDtl.setTotalPaid(paidAmount);
						allocteDtl.setPaidAmount(paidAmount.subtract(tds));
						allocteDtl.setTdsPaid(tds);
						// GST Calculation(always paid amount we are taking the
						// inclusive type here because we are doing reverse
						// calculation here)
						if (allocteDtl.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
							allocteDtl.setPaidCGST(BigDecimal.ZERO);
							allocteDtl.setPaidSGST(BigDecimal.ZERO);
							allocteDtl.setPaidUGST(BigDecimal.ZERO);
							allocteDtl.setPaidIGST(BigDecimal.ZERO);
							allocteDtl.setPaidGST(BigDecimal.ZERO);
							allocteDtl.setPaidCESS(BigDecimal.ZERO);
							receiptCalculator.calAllocationGST(financeDetail, paidAmount, allocteDtl,
									FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
						}
					}
				}
			}
		}

		changePaid();

		// if no extra balance or partial pay disable excessAdjustTo
		if (receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void allocateNPft(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal netPft = receiptCalculator.getNetProfit(receiptData, paidAmount);
		BigDecimal pri = BigDecimal.ZERO;
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				pri = allocteDtl.getPaidAmount();
			}
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				if (netPft.compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					netPft = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(netPft);
				allocteDtl.setTdsPaid(paidAmount.subtract(netPft));
			}

		}
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(pri.add(netPft));
				allocteDtl.setPaidAmount(pri.add(netPft));
				break;
			}

		}

	}

	private void allocateEmi(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, paidAmount);
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				if (emiSplit[2].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[2] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[1]);
				allocteDtl.setPaidAmount(emiSplit[2]);
				allocteDtl.setTdsPaid(emiSplit[1].subtract(emiSplit[2]));
			}

			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				if (emiSplit[0].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[0] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[0]);
				allocteDtl.setPaidAmount(emiSplit[0]);
			}
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(paidAmount);
			}
		}

	}

	private void allocatePft(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal pft = receiptCalculator.getPftAmount(receiptData.getFinanceDetail().getFinScheduleData(), paidAmount,
				rch.isExcldTdsCal());
		BigDecimal pri = BigDecimal.ZERO;
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				pri = allocteDtl.getPaidAmount();
			}
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				if (pft.compareTo(allocteDtl.getTotalDue().add(allocteDtl.getTdsDue())
						.subtract(allocteDtl.getWaivedAmount())) > 0) {
					pft = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(pft);
				allocteDtl.setPaidAmount(paidAmount);
				allocteDtl.setTdsPaid(pft.subtract(paidAmount));
				if (allocteDtl.getTdsPaid().compareTo(BigDecimal.ZERO) <= 0) {
					allocteDtl.setTdsPaid(BigDecimal.ZERO);
				}
			}

		}
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(pri.add(paidAmount));
				allocteDtl.setPaidAmount(pri.add(paidAmount));
				break;
			}

		}

	}

	private void allocatePRI(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal npft = BigDecimal.ZERO;
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(paidAmount);
			}
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				npft = allocteDtl.getPaidAmount();
			}
		}
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(npft.add(paidAmount));
				allocteDtl.setPaidAmount(npft.add(paidAmount));
				break;
			}

		}

	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAllocateWaivedChange(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		int idx = (int) event.getData();
		String id = "AllocateWaived_" + idx;

		boolean isEmiWaived = false;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationWaived = (CurrencyBox) this.listBoxPastdues.getFellow(id);
		BigDecimal waivedAmount = PennantApplicationUtil.unFormateAmount(allocationWaived.getValidateValue(),
				formatter);

		BigDecimal dueAmount = allocate.getTotalDue();

		if (waivedAmount.compareTo(dueAmount) > 0) {
			waivedAmount = dueAmount;
		}
		allocate.setWaivedAmount(waivedAmount);

		adjustWaiver(allocate, waivedAmount);

		BigDecimal totalPaid = receiptCalculator.getPaidAmount(allocate, allocate.getPaidAmount());

		if (StringUtils.isNotBlank(allocate.getTaxType())) {
			// always paid amount we are taking the inclusive type here because
			// we are doing reverse calculation here
			allocate.setPaidCGST(BigDecimal.ZERO);
			allocate.setPaidSGST(BigDecimal.ZERO);
			allocate.setPaidUGST(BigDecimal.ZERO);
			allocate.setPaidIGST(BigDecimal.ZERO);
			allocate.setPaidCESS(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			receiptCalculator.calAllocationPaidGST(financeDetail, totalPaid, allocate,
					FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
		}

		BigDecimal tdsPaidNow = BigDecimal.ZERO;
		if (allocate.isTdsReq()) {
			tdsPaidNow = receiptCalculator.getTDSAmount(financeDetail.getFinScheduleData().getFinanceMain(), totalPaid);
			allocate.setTdsPaid(tdsPaidNow);
			allocate.setTotalPaid(totalPaid.add(tdsPaidNow));
		}

		if (Allocation.PRI.equals(allocate.getAllocationType())
				|| Allocation.PFT.equals(allocate.getAllocationType())) {
			isEmiWaived = true;
		}

		if (allocate.isSubListAvailable()) {
			receiptCalculator.splitNetAllocSummary(receiptData, idx);
		} else {
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())
						&& allocteDtl.getAllocationTo() == allocate.getAllocationTo()) {
					allocteDtl.setPaidCGST(BigDecimal.ZERO);
					allocteDtl.setPaidSGST(BigDecimal.ZERO);
					allocteDtl.setPaidIGST(BigDecimal.ZERO);
					allocteDtl.setPaidUGST(BigDecimal.ZERO);
					allocteDtl.setPaidGST(BigDecimal.ZERO);
					// Waiver GST
					allocteDtl.setWaivedCGST(BigDecimal.ZERO);
					allocteDtl.setWaivedSGST(BigDecimal.ZERO);
					allocteDtl.setWaivedIGST(BigDecimal.ZERO);
					allocteDtl.setWaivedUGST(BigDecimal.ZERO);
					allocteDtl.setWaivedGST(BigDecimal.ZERO);
					allocteDtl.setWaivedAmount(allocate.getWaivedAmount());
					allocteDtl.setPaidAmount(allocate.getPaidAmount());
					allocteDtl.setTotalPaid(allocate.getTotalPaid());
				}
			}
		}

		if (Allocation.FUT_PFT.equals(allocate.getAllocationType())) {
			FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
			List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();
			FinanceScheduleDetail lastSchd = schdDtls.get(schdDtls.size() - 1);
			if (lastSchd.isTDSApplicable()) {
				BigDecimal pftNow = receiptCalculator.getNetOffTDS(
						receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), allocate.getPaidAmount());
				allocate.setTotalPaid(pftNow);
				allocate.setTdsPaid(pftNow.subtract(allocate.getPaidAmount()));
			} else {
				allocate.setTotalPaid(allocate.getPaidAmount());
				allocate.setTdsPaid(BigDecimal.ZERO);
			}
		}

		if (Allocation.PFT.equals(allocate.getAllocationType())) {
			BigDecimal pftPaid = receiptCalculator.getPftAmount(receiptData.getFinanceDetail().getFinScheduleData(),
					allocate.getPaidAmount(), rch.isExcldTdsCal());
			isEmiWaived = true;
			allocate.setTotalPaid(pftPaid);
			allocate.setTdsPaid(pftPaid.subtract(allocate.getPaidAmount()));

		}
		// Adjusting emi waiver
		if (isEmiWaived) {
			BigDecimal paid = BigDecimal.ZERO;
			BigDecimal waived = BigDecimal.ZERO;
			BigDecimal totPaid = BigDecimal.ZERO;
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocationsSummary()) {
				if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
					allocteDtl.setPaidAmount(paid);
					allocteDtl.setTotalPaid(totPaid);
					allocteDtl.setWaivedAmount(waived);
					break;
				}
			}
			paid = BigDecimal.ZERO;
			waived = BigDecimal.ZERO;
			totPaid = BigDecimal.ZERO;
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
					allocteDtl.setPaidAmount(paid);
					allocteDtl.setTotalPaid(totPaid);
					allocteDtl.setWaivedAmount(waived);
					break;
				}
			}
		}

		changeWaiver();
		if (receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public ReceiptAllocationDetail adjustWaiver(ReceiptAllocationDetail allocate, BigDecimal waiverNow) {
		BigDecimal dueAmount = allocate.getTotalDue();
		BigDecimal paidAmount = allocate.getTotalPaid();
		BigDecimal waivedAmount = allocate.getWaivedAmount();
		BigDecimal balAmount = dueAmount.subtract(paidAmount).subtract(waivedAmount);
		if (waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (balAmount.compareTo(BigDecimal.ZERO) == 0) {
				if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
					paidAmount = paidAmount.add(waivedAmount);
				}
			}
			waivedAmount = BigDecimal.ZERO;
		}
		balAmount = dueAmount.subtract(paidAmount).subtract(waivedAmount);
		if (waiverNow.compareTo(balAmount) > 0) {
			paidAmount = paidAmount.subtract(waiverNow.subtract(balAmount));
		}
		allocate.setTotalPaid(paidAmount);
		allocate.setWaivedAmount(waiverNow);
		allocate.setPaidAmount(paidAmount);
		return allocate;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		// Future Dated Receipts
		Date fromDate = rch.getValueDate();

		Date toDate = SysParamUtil.getAppDate(); // DateUtility.getAppDate()
		if (DateUtil.compare(fromDate, toDate) > 0) {
			toDate = SysParamUtil.getDerivedAppDate();
		}

		if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			fromDate = rch.getReceiptDate();
		}

		if (!this.receiptPurpose.isDisabled()) {
			this.receiptPurpose.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptPurpose(),
					Labels.getLabel("label_ReceiptDialog_ReceiptPurpose.value")));
		}

		String recptMode = getComboboxValue(receiptMode);
		if (!this.receiptMode.isDisabled()) {
			this.receiptMode.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptModes(),
					Labels.getLabel("label_ReceiptDialog_ReceiptMode.value")));
		}
		if (!this.receivedFrom.isDisabled()) {
			this.receivedFrom.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceivedFrom(),
					Labels.getLabel("label_ReceiptDialog_ReceivedFrom.value")));
		}
		if (this.excessAdjustTo.isVisible() && !this.excessAdjustTo.isDisabled()) {
			this.excessAdjustTo.setConstraint(new StaticListValidator(PennantStaticListUtil.getExcessAdjustmentTypes(),
					Labels.getLabel("label_ReceiptDialog_ExcessAdjustTo.value")));
		}
		if (!this.allocationMethod.isDisabled()) {
			this.allocationMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getAllocationMethods(),
					Labels.getLabel("label_ReceiptDialog_AllocationMethod.value")));
		}
		if (this.effScheduleMethod.isVisible() && !this.effScheduleMethod.isDisabled()) {
			this.effScheduleMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getEarlyPayEffectOn(),
					Labels.getLabel("label_ReceiptDialog_EffecScheduleMethod.value")));
		}

		if (this.rowCancelReason.isVisible() && !this.cancelReason.isReadonly()) {
			this.cancelReason.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CrossLoanKnockoffDialog_CancelReason.value"), null, true, true));
		}

		if (StringUtils.equals(recptMode, ReceiptMode.CHEQUE)) {

			if (!this.chequeAcNo.isReadonly()) {
				this.chequeAcNo.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_ChequeAccountNo.value"),
								PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
			}

			if (!this.drawerName.isReadonly()) {
				this.drawerName
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_DrawerName.value"),
								PennantRegularExpressions.REGEX_ALPHA_SPACE_SPL, false));
			}
		}

		if (StringUtils.equals(recptMode, ReceiptMode.DD) || StringUtils.equals(recptMode, ReceiptMode.CHEQUE)) {

			if (!this.favourNo.isReadonly()) {
				String label = Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value");
				if (StringUtils.equals(recptMode, ReceiptMode.DD)) {
					label = Labels.getLabel("label_ReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(
						new PTStringValidator(label, PennantRegularExpressions.REGEX_NUMERIC, true, 1, 6));
			}

			if (!this.valueDate.isDisabled()) {
				this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ValueDate.value"),
						true, financeMain.getFinStartDate(), SysParamUtil.getAppDate(), true));
			}

			if (!this.bankCode.isReadonly()) {
				String mode = this.receiptMode.getSelectedItem().getValue().toString();
				if (StringUtils.equals(mode, ReceiptMode.CHEQUE)) {
					this.bankCode.setConstraint(new PTStringValidator(
							Labels.getLabel("label_ReceiptDialog_IssuingBank.value"), null, false, true));
				} else {
					this.bankCode.setConstraint(new PTStringValidator(
							Labels.getLabel("label_ReceiptDialog_IssuingBank.value"), null, true, true));
				}

			}

			if (!this.favourName.isReadonly()) {
				this.favourName
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_favourName.value"),
								PennantRegularExpressions.REGEX_FAVOURING_NAME, true));
			}

			/*
			 * if (!this.depositDate.isReadonly()) { Date valueTillDate = rch.getValueDate(); Date appDate =
			 * DateUtility.getAppDate(); if (DateUtility.compare(appDate, valueTillDate)>=0){ this.depositDate
			 * .setConstraint(new PTDateValidator(Labels.getLabel( "label_ReceiptDialog_DepositDate.value"), true,
			 * valueTillDate, appDate, true)); }else{ this.depositDate .setConstraint(new
			 * PTDateValidator(Labels.getLabel( "label_ReceiptDialog_DepositDate.value"), true, appDate, valueTillDate,
			 * true)); } }
			 */
			if (!this.depositNo.isReadonly()) {
				this.depositNo
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_depositNo.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		if (StringUtils.equals(recptMode, ReceiptMode.ONLINE)) {

			if (!this.transactionRef.isReadonly()) {
				this.transactionRef.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_tranReference.value"), null, true));// PSD:199323
																														// To
																														// remove
																														// the
																														// Constraint
																														// same
																														// As
																														// API.
			}
		}

		if (!StringUtils.equals(recptMode, RepayConstants.PAYTYPE_EXCESS)) {
			if (!this.paymentRef.isReadonly()) {
				this.paymentRef.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_paymentReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		/*
		 * if (!this.remarks.isReadonly()) { this.remarks.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_ReceiptDialog_Remarks.value" ),
		 * PennantRegularExpressions.REGEX_DESCRIPTION, true)); }
		 */

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.receiptPurpose.setConstraint("");
		this.receiptMode.setConstraint("");
		this.excessAdjustTo.setConstraint("");
		this.allocationMethod.setConstraint("");
		this.effScheduleMethod.setConstraint("");

		this.favourNo.setConstraint("");
		this.valueDate.setConstraint("");
		this.bankCode.setConstraint("");
		this.favourName.setConstraint("");
		this.depositNo.setConstraint("");
		this.paymentRef.setConstraint("");
		this.transactionRef.setConstraint("");
		this.chequeAcNo.setConstraint("");
		// this.receivedDate.setConstraint("");
		this.remarks.setConstraint("");
		this.drawerName.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.receiptPurpose.setErrorMessage("");
		this.receiptMode.setErrorMessage("");
		this.excessAdjustTo.setErrorMessage("");
		this.allocationMethod.setErrorMessage("");
		this.effScheduleMethod.setErrorMessage("");

		this.favourNo.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.favourName.setErrorMessage("");
		this.depositNo.setErrorMessage("");
		this.paymentRef.setErrorMessage("");
		this.transactionRef.setErrorMessage("");
		this.chequeAcNo.setErrorMessage("");
		// this.receivedDate.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.drawerName.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Method for capturing Fields data from components to bean
	 * 
	 * @return
	 */
	private void doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<>();
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		int finFormatter = CurrencyUtil.getFormat(finMain.getFinCcy());

		Date curBussDate = SysParamUtil.getAppDate();
		FinReceiptHeader header = receiptData.getReceiptHeader();
		header.setReceiptDate(curBussDate);
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReference(this.toFinReference.getValue());

		try {
			header.setReceiptPurpose(getComboboxValue(receiptPurpose));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setReceiptMode(getComboboxValue(receiptMode));
			if (header.getSubReceiptMode() == null || StringUtils.equalsIgnoreCase("#", header.getSubReceiptMode())) {
				header.setSubReceiptMode(header.getReceiptMode());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (excessAdjustTo.isVisible()) {
			try {
				header.setExcessAdjustTo(getComboboxValue(excessAdjustTo));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			header.setAllocationType(getComboboxValue(allocationMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!isForeClosure) {
				header.setReceiptAmount(
						PennantApplicationUtil.unFormateAmount(receiptAmount.getValidateValue(), finFormatter));
				if (isEarlySettle) {
					header.setReceiptAmount(header.getReceiptAmount().add(receiptData.getExcessAvailable()));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (effScheduleMethod.isVisible() && !effScheduleMethod.isDisabled() && !finMain.isManualSchedule()) {
			try {
				header.setEffectSchdMethod(getComboboxValue(effScheduleMethod));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			header.setReceiptDate(this.receiptDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.favourNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.valueDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.bankCode.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.favourName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			Date curBusDate = SysParamUtil.getAppDate();
			if (curBusDate.compareTo(header.getValueDate()) < 0) {
				curBusDate = SysParamUtil.getDerivedAppDate();
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.depositNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.paymentRef.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.transactionRef.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.chequeAcNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.drawerName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			/*
			 * validateReceivedDate(); this.receivedDate.getValue();
			 */
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setExtReference(this.externalRefrenceNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (receivedFrom.isVisible()) {
				header.setReceivedFrom(getComboboxValue(receivedFrom));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// RecAppDate
		if (header.getRecAppDate() == null) {
			header.setRecAppDate(SysParamUtil.getAppDate());
		}

		if (isCancel) {
			header.setReceiptModeStatus(RepayConstants.PAYSTATUS_CANCEL);
			header.setNewRecord(false);

			try {
				header.setCancelReason(cancelReason.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				header.setCancelRemarks(cancelRemarks.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		doRemoveValidation();
		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				wve.clear();
			}
		}
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			this.receiptDetailsTab.setSelected(true);
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Checklist Details when Check list Tab selected
	 */
	public void onSelectCheckListDetailsTab(ForwardEvent event)
			throws ParseException, InterruptedException, IllegalAccessException, InvocationTargetException {
		this.doWriteComponentsToBean();

		if (financeCheckListReferenceDialogCtrl != null) {
			financeCheckListReferenceDialogCtrl.doSetLabels(getFinBasicDetails());
			financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(receiptData.getFinanceDetail().getCheckList(),
					receiptData.getFinanceDetail().getFinanceCheckList(), false);
		}

	}

	/**
	 * Method for Processing Agreement Details when Agreement list Tab selected
	 */
	public void onSelectAgreementDetailTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException, ParseException {
		this.doWriteComponentsToBean();

		// refresh template tab
		if (agreementDetailDialogCtrl != null) {
			agreementDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			agreementDetailDialogCtrl.doShowDialog(false);
		}
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws Exception
	 */
	public FinanceDetail onExecuteStageAccDetail() throws Exception {
		logger.debug("Entering");

		receiptData.getFinanceDetail().setModuleDefiner(FinServiceEvent.RECEIPT);

		logger.debug("Leaving");
		return receiptData.getFinanceDetail();
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, CrossLoanKnockOff rch, String finishedTasks) {
		logger.debug("Entering");
		String serviceTasks = getServiceOperations(taskId, rch);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, CrossLoanKnockOff clkHeader) {
		logger.debug("Entering");
		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(clkHeader.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, clkHeader);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
				}
			}
		}

		clkHeader.setTaskId(taskId);
		clkHeader.setNextTaskId(nextTaskId);
		clkHeader.setRoleCode(getRole());
		clkHeader.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws Exception
	 */
	public boolean doProcess(CrossLoanKnockOff crossLoanKnocKOff, String tranType) throws Exception {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		crossLoanKnocKOff.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		crossLoanKnocKOff.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		crossLoanKnocKOff.setUserDetails(getUserWorkspace().getLoggedInUser());
		crossLoanKnocKOff.setWorkflowId(getWorkFlowId());

		crossLoanKnocKOff.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			crossLoanKnocKOff.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, crossLoanKnocKOff, finishedTasks);

			if (isNotesMandatory(taskId, crossLoanKnocKOff)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(crossLoanKnocKOff, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					processCompleted = true;
				} else {
					CrossLoanKnockOff clkHeader = (CrossLoanKnockOff) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, clkHeader);
					auditHeader.getAuditDetail().setModelData(clkHeader);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				serviceTasks = getServiceTasks(taskId, crossLoanKnocKOff, finishedTasks);

			}

			CrossLoanKnockOff clkHeader = (CrossLoanKnockOff) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, crossLoanKnocKOff);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, clkHeader);
					auditHeader.getAuditDetail().setModelData(clkHeader);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(crossLoanKnocKOff, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws Exception
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		CrossLoanKnockOff crossLoanHeader = (CrossLoanKnockOff) auditHeader.getAuditDetail().getModelData();
		crossLoanHeader.getFinReceiptData().setForeClosure(false);

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {

					auditHeader = crossLoanKnockOffService.saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

						if (crossLoanHeader.isNewRecord()) {
							((FinReceiptData) auditHeader.getAuditDetail().getModelData()).getFinanceDetail()
									.setDirectFinalApprove(true);
						}

						auditHeader = crossLoanKnockOffService.doApprove(auditHeader);

						if (crossLoanHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = crossLoanKnockOffService.doReject(auditHeader);
						if (crossLoanHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReversal)) {
						// auditHeader = getCrossLoanKnockOffService().doReversal(auditHeader);
						if (crossLoanHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.windowCrossLoanKnockOffDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.windowCrossLoanKnockOffDialog, auditHeader);
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
			setOverideMap(auditHeader.getOverideMap());

		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		// setRepaySchdList(sortRpySchdDetails(repaySchdList));
		// this.listBoxPayment.getItems().clear();
		BigDecimal totalRefund = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalTds = BigDecimal.ZERO;
		BigDecimal totalLatePft = BigDecimal.ZERO;
		BigDecimal totalPri = BigDecimal.ZERO;
		BigDecimal totalCharge = BigDecimal.ZERO;

		BigDecimal totInsPaid = BigDecimal.ZERO;
		BigDecimal totSchdFeePaid = BigDecimal.ZERO;

		Listcell lc;
		Listitem item;

		if (repaySchdList != null) {
			for (int i = 0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtil.formatToLongDate(repaySchd.getSchDate()));
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getProfitSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getPrincipalSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()), formatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getTdsSchdPayNow(), formatter));
				totalTds = totalTds.add(repaySchd.getTdsSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()), formatter));
				totalLatePft = totalLatePft
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()), formatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil
						.amountFormate(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()), formatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (repaySchd.getDaysLate() > 0) {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getMaxWaiver(), formatter));
				} else {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getRefundMax(), formatter));
				}
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal refundPft = BigDecimal.ZERO;
				if (repaySchd.isAllowRefund() || repaySchd.isAllowWaiver()) {
					if (repaySchd.isAllowRefund()) {
						refundPft = repaySchd.getRefundReq();
						totalRefund = totalRefund.add(refundPft);
					} else if (repaySchd.isAllowWaiver()) {
						refundPft = repaySchd.getWaivedAmt();
						totalWaived = totalWaived.add(refundPft);
					}
				}

				lc = new Listcell(PennantApplicationUtil.amountFormate(refundPft, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Fee Details
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getSchdFeePayNow(), formatter));
				lc.setStyle("text-align:right;");
				totSchdFeePaid = totSchdFeePaid.add(repaySchd.getSchdFeePayNow());
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow())
						.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()))
						.add(repaySchd.getSchdFeePayNow())
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()))
						.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()).subtract(refundPft));
				lc = new Listcell(PennantApplicationUtil.amountFormate(netPay, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal netBalance = repaySchd.getProfitSchdBal().add(repaySchd.getPrincipalSchdBal())
						.add(repaySchd.getSchdFeeBal());

				lc = new Listcell(PennantApplicationUtil.amountFormate(
						netBalance.subtract(netPay.subtract(totalCharge).subtract(totalLatePft)), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				// this.listBoxPayment.appendChild(item);
			}

			// Summary Details
			Map<String, BigDecimal> paymentMap = new HashMap<String, BigDecimal>();
			paymentMap.put("totalRefund", totalRefund);
			paymentMap.put("totalCharge", totalCharge);
			paymentMap.put("totalPft", totalPft);
			paymentMap.put("totalTds", totalTds);
			paymentMap.put("totalLatePft", totalLatePft);
			paymentMap.put("totalPri", totalPri);

			paymentMap.put("insPaid", totInsPaid);
			paymentMap.put("schdFeePaid", totSchdFeePaid);

			doFillSummaryDetails(paymentMap);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * 
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(Map<String, BigDecimal> paymentMap) {
		Listitem item = new Listitem();
		Listcell lc = new Listcell(Labels.getLabel("listcell_summary.label"));

		lc.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(15);
		lc.setParent(item);

		BigDecimal totalSchAmount = BigDecimal.ZERO;

		if (paymentMap.get("totalPri").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPri"));
			fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), paymentMap.get("totalPri"));
		}

		fillListItem(Labels.getLabel("listcell_totalSchAmount.label"), totalSchAmount);
	}

	private void fillListItem(String label, BigDecimal fieldValue) {
		Listitem item = new Listitem();
		Listcell lc = new Listcell();
		lc.setParent(item);
		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;");
		lc.setSpan(2);
		lc.setParent(item);
		lc = new Listcell(
				PennantApplicationUtil.amountFormate(fieldValue, receiptData.getRepayMain().getLovDescFinFormatter()));
		lc.setStyle("text-align:right;color:#f36800;");
		lc.setParent(item);
		lc = new Listcell();
		lc.setSpan(12);
		lc.setParent(item);
	}

	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> repayScheduleDetails) {
		if (repayScheduleDetails != null && repayScheduleDetails.size() > 0) {
			Collections.sort(repayScheduleDetails, new Comparator<>() {
				@Override
				public int compare(RepayScheduleDetail detail1, RepayScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return repayScheduleDetails;
	}

	private boolean isValidateData(boolean isCalProcess) throws InterruptedException, InterfaceException {
		logger.debug(Literal.ENTERING);
		if (isCalProcess) {
			doClearMessage();
			doSetValidation();
			doWriteComponentsToBean();
		}

		boolean isOverDraft = false;
		if (FinanceConstants.PRODUCT_ODFACILITY
				.equals(getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverDraft = true;
		}

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date receiptValueDate = rch.getValueDate();
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		List<FinanceScheduleDetail> scheduleList = finScheduleData.getFinanceScheduleDetails();

		// in case of early pay,do not allow in subvention period
		if (receiptPurposeCtg == 1 && financeMain.isAllowSubvention()) {
			boolean isInSubVention = receiptService.isInSubVention(
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), receiptValueDate);
			if (isInSubVention) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_SubVention_EndDates"));
				return false;
			}
		}

		BigDecimal payableAmt = rch.getReceiptAmount();

		if (!isCalProcess && !isForeClosure && isEarlySettle) {
			payableAmt = payableAmt.add(receiptData.getExcessAvailable());
		}

		if (receiptData.getPaidNow().compareTo(payableAmt) > 0) {
			String[] args = new String[2];

			args[0] = PennantApplicationUtil.amountFormate(receiptData.getPaidNow(), formatter);
			args[1] = PennantApplicationUtil.amountFormate(payableAmt, formatter);
			MessageUtil.showError(Labels.getLabel("label_Allocation_More_than_receipt", args));
			return false;
		}

		if (receiptPurposeCtg == 2 && !financeType.isAlwCloBefDUe() && !isOverDraft) {
			if (financeMain.getFinApprovedDate() != null
					&& rch.getValueDate().compareTo(financeMain.getFinApprovedDate()) < 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_First_Inst_Date",
						new String[] { DateUtil.formatToLongDate(financeMain.getFinApprovedDate()) }));
				return false;
			}
		}

		if (receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null && receiptPurposeCtg > 0
				&& receiptValueDate.compareTo(financeMain.getFinStartDate()) == 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Date"));
			return false;
		}

		if (isForeClosure && receiptData.isFCDueChanged()) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_DueAmounts_Changed"));
			return false;
		}

		// Receipt Calculation Value date should not be equal to Any Holiday
		// Schedule Date

		// Entered Receipt Amount Match case test with allocations
		BigDecimal totReceiptAmount = receiptData.getTotReceiptAmount();
		if (totReceiptAmount.compareTo(BigDecimal.ZERO) == 0 && !isForeClosure) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_NoReceiptAmount"));
			return false;
		}

		// Past due Details
		BigDecimal balPending = rch.getTotalPastDues().getBalance().add(rch.getTotalRcvAdvises().getBalance())
				.add(rch.getTotalFees().getBalance());

		// User entered Receipt amounts and paid on manual Allocation validation
		if (receiptData.getRemBal().compareTo(BigDecimal.ZERO) < 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_InsufficientAmount"));
			return false;
		}

		if (receiptPurposeCtg == 2) {
			if (balPending.compareTo(BigDecimal.ZERO) != 0) {
				MessageUtil.showError(
						Labels.getLabel("label_ReceiptDialog_Valid_Settlement", new String[] { PennantApplicationUtil
								.getLabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
				return false;
			}
		}

		// Manual Schedule Validations
		if (finScheduleData.getFinanceMain().isManualSchedule()
				&& StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.EARLYRPY)
				&& (isCalProcess || !("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| this.userAction.getSelectedItem().getLabel().contains("Reject")
						|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
						|| this.userAction.getSelectedItem().getLabel().contains("Decline")))) {

			ManualScheduleHeader scheduleHeader = finScheduleData.getManualScheduleHeader();

			if (scheduleHeader == null || CollectionUtils.isEmpty(scheduleHeader.getManualSchedules())
					|| (scheduleHeader != null && !scheduleHeader.isValidSchdUpload())) {
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);

				if (tab != null) {
					tab.setSelected(true);
				}

				MessageUtil.showError(Labels.getLabel("MANUAL_SCHD_REQ"));
				return false;
			}

			int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
			BigDecimal bal = PennantApplicationUtil.formateAmount(receiptData.getRemBal(), formatter);

			if (scheduleHeader.getCurPOSAmt().subtract(bal).compareTo(scheduleHeader.getTotPrincipleAmt()) != 0) {
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
				MessageUtil.showError(Labels.getLabel("PRIAMT_FINAMT_NOTMATCH"));
				return false;
			}
		}

		if (!isCalProcess) {
			return true;
		}

		// Finance Should not allow for Partial Settlement & Early settlement
		// when Maturity Date reaches Current application Date
		if ((receiptPurposeCtg == 1 || receiptPurposeCtg == 2) && !receiptData.isForeClosure()) {

			if (financeMain.getMaturityDate().compareTo(receiptValueDate) < 0) {
				MessageUtil.showError(
						Labels.getLabel("label_ReceiptDialog_Valid_MaturityDate", new String[] { PennantApplicationUtil
								.getLabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
				return false;
			}
		}

		// No excess amount validation on partial Settlement
		if (receiptPurposeCtg == 1 && !isOverDraft) {
			if (receiptData.getRemBal().compareTo(BigDecimal.ZERO) <= 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_PartialSettlement"));
				return false;
			} else {

				// Check the max Schedule payment amount
				BigDecimal closingBal = BigDecimal.ZERO;
				boolean isValidPPDate = true;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) == 0
							&& StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_UNPLANNED.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_HOLIDAY.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_POSTPONE.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_HOLDEMI.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
						isValidPPDate = false;
					}
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) >= 0) {
						closingBal = curSchd.getClosingBalance();
						continue;
					}
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) == 0 || closingBal == null) {
						closingBal = closingBal.subtract(curSchd.getSchdPriPaid().subtract(curSchd.getSchdPftPaid()));
						break;
					}
				}

				if (!isValidPPDate) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Invalid_ValueDate"));
					return false;
				}

				if (closingBal != null) {
					if ((receiptData.getRemBal().compareTo(closingBal) >= 0 && !isOverDraft)
							|| ((receiptData.getRemBal().compareTo(closingBal)) > 0 && isOverDraft)) {
						if (!isOverDraft) {
							MessageUtil.showError(Labels.getLabel("FIELD_IS_LESSER",
									new String[] {
											Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
											PennantApplicationUtil.amountFormate(closingBal, formatter) }));
							return false;
						} else {
							MessageUtil.showError(Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] {
											Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
											PennantApplicationUtil.amountFormate(closingBal, formatter) }));
						}
					}
				} else {
					if (isOverDraft) {
						MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_PartialSettlement"));
						return false;
					}
				}
			}
		}

		// Early settlement Validation , if entered amount not sufficient with
		// paid and waived amounts
		if (receiptPurposeCtg == 2) {
			BigDecimal earlySettleBal = totReceiptAmount.subtract(balPending);
			if (earlySettleBal.compareTo(BigDecimal.ZERO) < 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_EarlySettlement"));
				return false;
			}

			// Paid amount still not cleared by paid's or waivers amounts
			if (isForeClosure && balPending.compareTo(BigDecimal.ZERO) > 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Paids_EarlySettlement"));
				return false;
			}

			if (!ImplementationConstants.RECEIPT_ALLOW_FULL_WAIVER) {
				if (isForeClosure && receiptData.getPaidNow().compareTo(BigDecimal.ZERO) == 0) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Allow_FullWaiver"));
					return false;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(CrossLoanKnockOff crossLoanHeader, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, crossLoanHeader);
		return new AuditHeader(crossLoanHeader.getCrossLoanTransfer().getFromFinReference(), null, null, null,
				auditDetail, crossLoanHeader.getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {

		doShowNotes(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain());
	}

	protected void refreshMaintainList() {
		receiptListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(receiptData.getReceiptHeader().getReceiptID());
	}

	/**
	 * Method which returns customer document title
	 * 
	 */
	public String getCustomerIDNumber(String docTypeCode) {

		if (getFinanceDetail() != null) {
			for (CustomerDocument custDocs : getFinanceDetail().getCustomerDetails().getCustomerDocumentsList()) {
				if (StringUtils.equals(custDocs.getCustDocCategory(), docTypeCode)) {
					return custDocs.getCustDocTitle();
				}
			}
		}
		return null;
	}

	/** new code to display chart by skipping jsps code start */
	public void onSelectDashboardTab(Event event) throws InterruptedException {
		logger.debug("Entering");

		for (ChartDetail chartDetail : chartDetailList) {
			String strXML = chartDetail.getStrXML();
			strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
			strXML = StringEscapeUtils.escapeJavaScript(strXML);
			chartDetail.setStrXML(strXML);

			Executions.createComponents("/Charts/Chart.zul", tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"),
					Collections.singletonMap("chartDetail", chartDetail));
		}
		chartDetailList = new ArrayList<ChartDetail>(); // Resetting
		logger.debug("Leaving");
	}

	public void onClick$btnPrint(Event event) throws Exception {
		//
	}

	public void addAmountCell(Listitem item, BigDecimal value, String cellID, boolean isBold) {
		Listcell lc = new Listcell(PennantApplicationUtil.amountFormate(value, formatter));

		if (isBold) {
			lc.setStyle("text-align:right;font-weight:bold;");
		} else {
			lc.setStyle("text-align:right;");
		}

		if (!StringUtils.isBlank(cellID)) {
			lc.setId(cellID);
		}

		lc.setParent(item);
	}

	public void addSimpleTextCell(Listitem item, String value) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);
	}

	public void addBoldTextCell(Listitem item, String value, boolean hasChild, int buttonId) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		if (hasChild) {
			Button button = new Button("Details");
			button.setId(String.valueOf(buttonId));
			button.addForward("onClick", windowCrossLoanKnockOffDialog, "onDetailsClick", button.getId());
			lc.appendChild(button);
		}
		lc.setParent(item);
	}

	private void setBalances() {
		BigDecimal remBal = receiptData.getRemBal();
		if (remBal.compareTo(BigDecimal.ZERO) <= 0) {
			remBal = BigDecimal.ZERO;
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
			if (receiptPurposeCtg == 0) {
				this.excessAdjustTo.setDisabled(true);
			}
		}

		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBal, formatter));
	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		notes.setVersion(getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion());
		notes.setRoleCode(getRole());
		logger.debug("Leaving ");
		return notes;
	}

	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null) {
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");

				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
						|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

			} else if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		// Open Window For maintenance
		if (StringUtils.isNotEmpty(module)) {

			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {

				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		if (!onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {

			final HashMap<String, Object> map = getDefaultArguments();

			map.put("financeMainDialogCtrl", this);
			map.put("moduleDefiner", module);
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("isEnquiry", true);

			map.put("financeDetail", orgFinanceDetail);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
				tab.setSelected(true);
			}
		}
		logger.debug("Leaving");
	}

	public void appendEffectScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null) {
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");

				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
						|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

			} else if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		// Open Window For maintenance
		if (StringUtils.isNotEmpty(module)) {

			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {

				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		if (!onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {

			final HashMap<String, Object> map = getDefaultArguments();

			map.put("financeMainDialogCtrl", this);
			map.put("moduleDefiner", module);
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("isEnquiry", true);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
				tab.setSelected(true);
			}
		}
		logger.debug("Leaving");
	}

	protected void doStoreServiceIds(FinReceiptHeader finReceiptHeader) {
		this.curRoleCode = finReceiptHeader.getRoleCode();
		this.curNextRoleCode = finReceiptHeader.getNextRoleCode();
		this.curTaskId = finReceiptHeader.getTaskId();
		this.curNextTaskId = finReceiptHeader.getNextTaskId();
		// this.curNextUserId = finReceiptHeader.getNextUserId();
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));

		return map;
	}

	private void doFillExcessPayables() {
		logger.debug("Entering");
		if (!isForeClosure && !isEarlySettle) {
			return;
		}
		List<XcessPayables> xcessPayableList = receiptData.getReceiptHeader().getXcessPayables();
		this.listBoxExcess.getItems().clear();

		for (int i = 0; i < xcessPayableList.size(); i++) {
			XcessPayables xcessPayable = xcessPayableList.get(i);
			if (xcessPayable.getAvailableAmt().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			createXcessPayableItem(xcessPayableList.get(i), i);
		}
		addXcessFooter(formatter);
		logger.debug("Leaving");
	}

	private void createXcessPayableItem(XcessPayables xcessPayable, int idx) {
		// List Item
		Listitem item = new Listitem();

		addBoldTextCell(item, xcessPayable.getPayableDesc(), false, idx);
		addAmountCell(item, xcessPayable.getAvailableAmt(), null, false);
		addAmountCell(item, xcessPayable.getTotPaidNow(), null, false);
		addAmountCell(item, xcessPayable.getBalanceAmt(), null, false);
		this.listBoxExcess.appendChild(item);
	}

	private void addXcessFooter(int formatter) {
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell("TOTALS");
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);
		BigDecimal avalAmt = BigDecimal.ZERO;
		BigDecimal paidAmt = BigDecimal.ZERO;
		BigDecimal balAmt = BigDecimal.ZERO;
		List<XcessPayables> xcessPayableList = receiptData.getReceiptHeader().getXcessPayables();

		for (int i = 0; i < xcessPayableList.size(); i++) {
			XcessPayables xcessPayable = xcessPayableList.get(i);
			avalAmt = avalAmt.add(xcessPayable.getAvailableAmt());
			paidAmt = paidAmt.add(xcessPayable.getTotPaidNow());
			balAmt = balAmt.add(xcessPayable.getBalanceAmt());
		}

		addAmountCell(item, avalAmt, null, true);
		addAmountCell(item, paidAmt, null, true);
		addAmountCell(item, balAmt, null, true);

		this.listBoxExcess.appendChild(item);
	}

	public void onClick$btnSearchFromFinreference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		// Preparation of Finance Enquiry Data
		FinReceiptHeader finReceiptHeader = receiptData.getReceiptHeader();
		CrossLoanTransfer crossLoanTransfer = crossLoanHeader.getCrossLoanTransfer();
		FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
		aFinanceEnq.setFinID(crossLoanTransfer.getFromFinID());
		aFinanceEnq.setFinReference(crossLoanTransfer.getFromFinReference());
		aFinanceEnq.setFinType(finReceiptHeader.getFinType());
		aFinanceEnq.setLovDescFinTypeName(finReceiptHeader.getFinTypeDesc());
		aFinanceEnq.setFinCcy(finReceiptHeader.getFinCcy());
		aFinanceEnq.setScheduleMethod(finReceiptHeader.getScheduleMethod());
		aFinanceEnq.setProfitDaysBasis(finReceiptHeader.getPftDaysBasis());
		aFinanceEnq.setFinBranch(finReceiptHeader.getFinBranch());
		aFinanceEnq.setLovDescFinBranchName(finReceiptHeader.getFinBranchDesc());
		aFinanceEnq.setLovDescCustCIF(finReceiptHeader.getCustCIF());
		aFinanceEnq.setFinIsActive(finReceiptHeader.isFinIsActive());

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("moduleCode", moduleCode);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", aFinanceEnq);
		map.put("ReceiptDialog", this);
		map.put("enquiryType", "FINENQ");
		map.put("isModelWindow", true);
		map.put("ReceiptDialogPage", true);
		map.put("window_ReceiptDialog", this.windowCrossLoanKnockOffDialog);
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.windowCrossLoanKnockOffDialog, map);

		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnSearchToFinreference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		// Preparation of Finance Enquiry Data
		FinReceiptHeader finReceiptHeader = receiptData.getReceiptHeader();
		CrossLoanTransfer crossLoanTransfer = crossLoanHeader.getCrossLoanTransfer();
		FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
		aFinanceEnq.setFinID(crossLoanTransfer.getToFinID());
		aFinanceEnq.setFinReference(crossLoanTransfer.getToFinReference());
		aFinanceEnq.setFinType(finReceiptHeader.getFinType());
		aFinanceEnq.setLovDescFinTypeName(finReceiptHeader.getFinTypeDesc());
		aFinanceEnq.setFinCcy(finReceiptHeader.getFinCcy());
		aFinanceEnq.setScheduleMethod(finReceiptHeader.getScheduleMethod());
		aFinanceEnq.setProfitDaysBasis(finReceiptHeader.getPftDaysBasis());
		aFinanceEnq.setFinBranch(finReceiptHeader.getFinBranch());
		aFinanceEnq.setLovDescFinBranchName(finReceiptHeader.getFinBranchDesc());
		aFinanceEnq.setLovDescCustCIF(finReceiptHeader.getCustCIF());
		aFinanceEnq.setFinIsActive(finReceiptHeader.isFinIsActive());

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("moduleCode", moduleCode);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", aFinanceEnq);
		map.put("ReceiptDialog", this);
		map.put("enquiryType", "FINENQ");
		map.put("isModelWindow", true);
		map.put("ReceiptDialogPage", true);
		map.put("window_ReceiptDialog", this.windowCrossLoanKnockOffDialog);
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.windowCrossLoanKnockOffDialog, map);

		logger.debug("Leaving " + event.toString());
	}

	public void onAllocateNetPaidChange(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocateNetPaid_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationPaid = (CurrencyBox) this.listBoxPastdues.getFellow(id);

		BigDecimal paidAmount = PennantApplicationUtil.unFormateAmount(allocationPaid.getValidateValue(), formatter);
		BigDecimal dueAmount = rch.getAllocationsSummary().get(idx).getTotalDue();
		BigDecimal waivedAmount = rch.getAllocationsSummary().get(idx).getWaivedAmount();
		if (paidAmount.compareTo(dueAmount.subtract(waivedAmount)) > 0) {
			paidAmount = dueAmount.subtract(waivedAmount);
		}
		BigDecimal totalPaid = receiptCalculator.getPaidAmount(allocate, paidAmount);
		allocate.setTotalPaid(paidAmount);
		allocate.setPaidAmount(paidAmount);
		// allocate.setPaidAmount(allocate.getTotRecv());

		// GST Calculations
		if (StringUtils.isNotBlank(allocate.getTaxType())) {
			// always paid amount we are taking the inclusive type here because
			// we are doing reverse calculation here
			allocate.setPaidCGST(BigDecimal.ZERO);
			allocate.setPaidSGST(BigDecimal.ZERO);
			allocate.setPaidUGST(BigDecimal.ZERO);
			allocate.setPaidIGST(BigDecimal.ZERO);
			allocate.setPaidCESS(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			receiptCalculator.calAllocationPaidGST(financeDetail, totalPaid, allocate,
					FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
		}

		BigDecimal tdsPaidNow = BigDecimal.ZERO;
		if (allocate.isTdsReq()) {
			if (dueAmount.equals(paidAmount)) {
				tdsPaidNow = allocate.getTdsDue();
			} else {
				tdsPaidNow = receiptCalculator.getTDSAmount(financeDetail.getFinScheduleData().getFinanceMain(),
						totalPaid);
				allocate.setTdsPaid(tdsPaidNow);
			}
			allocate.setTotalPaid(totalPaid.add(tdsPaidNow));
		}

		if (allocate.isSubListAvailable()) {
			receiptCalculator.splitNetAllocSummary(receiptData, idx);
		} else {
			if (Allocation.EMI.equals(allocate.getAllocationType())) {
				allocateEmi(paidAmount);
			} else if (Allocation.PFT.equals(allocate.getAllocationType())) {
				allocatePft(paidAmount);
			} else if (Allocation.PRI.equals(allocate.getAllocationType())) {
				allocatePRI(paidAmount);
			} else {
				for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
					if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())
							&& allocteDtl.getAllocationTo() == allocate.getAllocationTo()) {
						allocteDtl.setTotalPaid(paidAmount.add(tdsPaidNow));
						allocteDtl.setPaidAmount(paidAmount);
						allocteDtl.setTdsPaid(tdsPaidNow);
						if (allocteDtl.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
							allocteDtl.setPaidCGST(BigDecimal.ZERO);
							allocteDtl.setPaidSGST(BigDecimal.ZERO);
							allocteDtl.setPaidUGST(BigDecimal.ZERO);
							allocteDtl.setPaidIGST(BigDecimal.ZERO);
							allocteDtl.setPaidCESS(BigDecimal.ZERO);
							allocteDtl.setPaidGST(BigDecimal.ZERO);
							receiptCalculator.calAllocationPaidGST(financeDetail, allocteDtl.getTotalPaid(), allocteDtl,
									FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
						}
					}
				}
			}
		}

		changePaid();

		// if no extra balance or partial pay disable excessAdjustTo
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) <= 0 || receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public Map<String, BigDecimal> getTaxPercMap() {
		return taxPercMap;
	}

	public void setTaxPercMap(Map<String, BigDecimal> taxPercMap) {
		this.taxPercMap = taxPercMap;
	}

	public void setOrgFinanceDetail(FinanceDetail orgFinanceDetail) {
		this.orgFinanceDetail = orgFinanceDetail;
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	public FinanceMain getFinanceMain() {
		if (getFinanceDetail() != null) {
			return getFinanceDetail().getFinScheduleData().getFinanceMain();
		}
		return null;
	}

	public FinReceiptData getReceiptData() {
		return receiptData;
	}

	public void setReceiptData(FinReceiptData receiptData) {
		this.receiptData = receiptData;
	}

	public FinReceiptData getOrgReceiptData() {
		return orgReceiptData;
	}

	public void setOrgReceiptData(FinReceiptData orgReceiptData) {
		this.orgReceiptData = orgReceiptData;
	}

	public void setReceiptListCtrl(ReceiptListCtrl receiptListCtrl) {
		this.receiptListCtrl = receiptListCtrl;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public void setCrossLoanKnockOffListCtrl(CrossLoanKnockOffListCtrl crossLoanKnockOffListCtrl) {
		this.crossLoanKnockOffListCtrl = crossLoanKnockOffListCtrl;
	}

	@Autowired
	public void setCrossLoanKnockOffService(CrossLoanKnockOffService crossLoanKnockOffService) {
		this.crossLoanKnockOffService = crossLoanKnockOffService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

}