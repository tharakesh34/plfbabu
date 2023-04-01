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
 * * FileName : FinFeeDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified
 * Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FeePaymentDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.webui.financemanagement.receipts.ReceiptDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul file.
 */
public class FinFeeDetailListCtrl extends GFCBaseCtrl<FinFeeDetail> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = LogManager.getLogger(FinFeeDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FeeDetailList;

	protected Button btnNew_NewPaymentDetail;
	protected Button btn_autoAllocate;

	protected Listbox listBoxPaymentDetails;
	protected Listbox listBoxFeeDetail;
	protected Listbox listBoxFinFeeReceipts;
	protected Groupbox gb_PaymentDetails;
	protected Groupbox gb_FinFeeReceipts;
	protected Listheader listheader_FeeDetailList_PaymentRef;

	protected Listheader listheader_FeeDetailList_FeeScheduleMethod;
	protected Listheader listheader_FeeDetailList_PaidAmount;
	protected Listheader listheader_FeeDetailList_Terms;
	protected Listheader listheader_FeeDetailList_Adjust;

	protected Listheader listheader_FeeDetailList_NetTdsAmount;
	protected Listheader listheader_FeeDetailList_PaidTDS;
	protected Listheader listheader_FeeDetailList_RemainingTDS;

	protected Div div_AutoAllocate;

	// For Dynamically calling of this Controller
	private FinanceDetail financeDetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private List<FinFeeDetail> finFeeDetailList = new ArrayList<>();
	private List<FeePaymentDetail> feePaymentDetailList = new ArrayList<>();
	private int ccyFormat = 0;
	private String roleCode = "";
	private boolean isEnquiry = false;
	private boolean isReceiptsProcess = false;
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private String ModuleType_Loan = "LOAN";
	private boolean isWIF = false;

	public static final String FEE_UNIQUEID_CALCULATEDAMOUNT = "CALAMT";
	public static final String FEE_UNIQUEID_ACTUALAMOUNT = "ACTUALAMT";
	public static final String FEE_UNIQUEID_WAIVEDAMOUNT = "WAIVEDAMT";

	// GST Added
	public static final String FEE_UNIQUEID_NET_ORIGINAL = "NETORIGINAL";
	public static final String FEE_UNIQUEID_NET_GST = "NETGST";
	public static final String FEE_UNIQUEID_NET_TOTALAMOUNT = "TOTALNET";
	public static final String FEE_UNIQUEID_PAID_ORIGINALAMOUNT = "PAIDORIGINALAMOUNT";
	public static final String FEE_UNIQUEID_PAID_GST = "PAIDGST";
	public static final String FEE_UNIQUEID_PAID_AMOUNT = "PAIDAMT";
	public static final String FEE_UNIQUEID_REMAINING_ORIGINAL = "REMORIGINAL";
	public static final String FEE_UNIQUEID_REMAININ_GST = "REMGST";
	public static final String FEE_UNIQUEID_REMAINING_FEE = "REMFEE";

	// tds
	public static final String FEE_UNIQUEID_NET_TDS = "NETTDS";
	public static final String FEE_UNIQUEID_PAID_TDS = "PAIDTDS";
	public static final String FEE_UNIQUEID_REMAININ_TDS = "REMTDS";

	public static final String FEE_UNIQUEID_PAYMENTMETHOD = "PAYMETHD";
	public static final String FEE_UNIQUEID_FEESCHEDULEMETHOD = "FEEMTHD";
	public static final String FEE_UNIQUEID_TERMS = "TERMS";
	public static final String FEE_UNIQUEID_ADJUST = "ADJUST";
	public static final String FEE_UNIQUEID_GSTDETAILS = "ADJUST";

	private FinanceDetailService financeDetailService;
	private FinFeeDetailService finFeeDetailService;
	private RuleService ruleService;
	private FeeTypeService feeTypeService;
	private FinanceMain financeMain = null;
	private boolean dataChanged = false;
	private String numberOfTermsLabel = "";
	private String moduleDefiner = "";
	private String eventCode = "";
	private Map<String, FeeRule> feeRuleDetailsMap = null;
	private Map<Long, List<FinFeeReceipt>> finFeeReceiptMap = new LinkedHashMap<>();

	private FeeDetailService feeDetailService;

	/**
	 * default constructor.<br>
	 */
	public FinFeeDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinFeeDetailListCtrl";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinAdvancePayment object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FeeDetailList(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FeeDetailList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_FeeDetailList.setTitle("");
				setNewFinance(true);
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "FinFeeDetailListCtrl");
			}

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormat = Integer.parseInt(arguments.get("ccyFormatter").toString());
			}

			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("numberOfTermsLabel")) {
				numberOfTermsLabel = (String) arguments.get("numberOfTermsLabel");
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}

			if (arguments.containsKey("isReceiptsProcess")) {
				isReceiptsProcess = (Boolean) arguments.get("isReceiptsProcess");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setFinFeeDetailListCtrl", this.getClass())
							.invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FeeDetailList.setTitle("");
			}

			if (arguments.containsKey("isWIF")) {
				isWIF = (Boolean) arguments.get("isWIF");
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			doEdit();
			doCheckRights();
			doSetFieldProperties();
			doShowDialog(getFinanceDetail());
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_AUTO_SCHD_BUILD)
					&& !isReadOnly("FinanceMainDialog_AutoScheduleBuild") && !isWIF) {
				getFinanceMainDialogCtrl().getClass().getMethod("autoFinStartDateUpdation", FinanceDetail.class)
						.invoke(getFinanceMainDialogCtrl(), getFinanceDetail());
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		int divHeight = this.borderLayoutHeight - 80;
		int semiBorderlayoutHeights = divHeight / 2;
		this.listBoxPaymentDetails.setHeight(semiBorderlayoutHeights - 105 + "px");

		if (isWIF) {
			this.gb_PaymentDetails.setVisible(false);
			this.listheader_FeeDetailList_PaymentRef.setVisible(false);
		} else {
			this.gb_PaymentDetails.setVisible(false);
			this.listheader_FeeDetailList_PaymentRef.setVisible(false);
		}

		if (parent != null) {
			this.window_FeeDetailList.setHeight(borderLayoutHeight - 75 + "px");
			parent.appendChild(this.window_FeeDetailList);
		}

		if (StringUtils.isNotBlank(this.moduleDefiner)) {
			this.listheader_FeeDetailList_FeeScheduleMethod.setVisible(false);
			this.listheader_FeeDetailList_Terms.setVisible(false);
		}

		if (FinServiceEvent.ADDDISB.equals(this.moduleDefiner)
				|| FinServiceEvent.RESTRUCTURE.equals(this.moduleDefiner)) {
			this.listheader_FeeDetailList_FeeScheduleMethod.setVisible(true);
			this.listheader_FeeDetailList_PaidAmount.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, roleCode);
		this.btnNew_NewPaymentDetail.setVisible(getUserWorkspace().isAllowed("FinFeeDetailListCtrl_NewPaymentDetail"));

		this.btn_autoAllocate.setVisible(getUserWorkspace().isAllowed("FinFeeDetailListCtrl_Adjust"));

		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWIF) {
			return false;
		} else if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail financeDetail) throws InterruptedException {
		logger.debug("Entering");

		try {
			appendFinBasicDetails();
			doCheckEnquiry();

			String feeEvent = "";

			if (StringUtils.isBlank(moduleDefiner)) {
				feeEvent = PennantApplicationUtil.getEventCode(financeMain.getFinStartDate());
			} else {
				feeEvent = eventCode;
				this.listheader_FeeDetailList_Adjust.setVisible(false);
				this.div_AutoAllocate.setVisible(false);
				this.listBoxFinFeeReceipts.setVisible(false);
			}

			financeDetail.getFinScheduleData().setFeeEvent(feeEvent);
			doWriteBeanToComponents(financeDetail);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * 
	 */
	public void doWriteBeanToComponents(FinanceDetail financeDetail) {
		logger.debug("Entering ");

		doFillFeePaymentDetails(financeDetail.getFeePaymentDetailList(), false);

		List<FinFeeDetail> finFeeDetailActualList = financeDetail.getFinScheduleData().getFinFeeDetailList();

		if (!financeDetail.isNewRecord()) {
			if (CollectionUtils.isNotEmpty(finFeeDetailActualList)) {
				for (FinFeeDetail finFee : finFeeDetailActualList) {
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFee.getTaxComponent())) {
						if (finFee.getCalculatedAmount().compareTo(finFee.getActualAmountOriginal()) != 0) {
							finFee.setFeeModified(true);
						}
					} else {
						if (finFee.getCalculatedAmount().compareTo(finFee.getActualAmount()) != 0) {
							finFee.setFeeModified(true);
						}
					}
					// Prepare Before Image
					FinFeeDetail befImage = new FinFeeDetail();
					BeanUtils.copyProperties(finFee, befImage);
					finFee.setBefImage(befImage);
				}
			}
		}

		if (financeDetail.isNewRecord() || StringUtils.isEmpty(financeMain.getRecordType())) {

			if (CollectionUtils.isNotEmpty(finFeeDetailActualList)
					&& !FinServiceEvent.RESTRUCTURE.equals(financeDetail.getModuleDefiner())) {

				List<FinFeeDetail> originationFeeList = new ArrayList<>();
				originationFeeList.addAll(finFeeDetailActualList);
				String wifReference = financeMain.getWifReference();

				if ((StringUtils.isNotBlank(financeDetail.getModuleDefiner())
						&& !FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner()))
						|| StringUtils.isBlank(wifReference)) {
					String branchCode = getUserWorkspace().getLoggedInUser().getBranchCode();
					finFeeDetailList = finFeeDetailService.convertToFinanceFees(financeDetail, branchCode);
				} else {
					// for WIF loans in loan origination
					for (FinFeeDetail finFeeDetail : finFeeDetailActualList) {
						finFeeDetail.setNewRecord(true);
						finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
					}
				}

				boolean receiptFlag = false;
				if (isReceiptsProcess && this.financeMainDialogCtrl != null
						&& this.financeMainDialogCtrl instanceof ReceiptDialogCtrl) {
					isReceiptsProcess = false;
					receiptFlag = true;
				}

				calculateFees(finFeeDetailList, financeDetail.getFinScheduleData(), financeDetail.getValueDate());

				financeDetail.getFinScheduleData().getFinFeeDetailList().addAll(originationFeeList);

				if (receiptFlag) {
					isReceiptsProcess = true;
				}

				doFillFinFeeDetailList(financeDetail.getFinScheduleData().getFinFeeDetailList());
			} else {
				String branchCode = getUserWorkspace().getLoggedInUser().getBranchCode();
				finFeeDetailList = finFeeDetailService.convertToFinanceFees(financeDetail, branchCode);
				calculateFees(finFeeDetailList, financeDetail.getFinScheduleData(), financeDetail.getValueDate());
				doFillFinFeeDetailList(finFeeDetailList);
			}
		} else {
			if (FeeExtension.ALLOW_FEES_RECALCULATE) {
				setFinFeeDetailList(finFeeDetailActualList);
				calculateFees(finFeeDetailActualList, financeDetail.getFinScheduleData(), financeDetail.getValueDate());
			} else {
				doFillFinFeeDetailList(financeDetail.getFinScheduleData().getFinFeeDetailList());
			}
		}

		// Fee Receipts
		long receiptid = 0;
		boolean receiptFound = false;
		BigDecimal receiptAmount = BigDecimal.ZERO;
		List<FinReceiptHeader> imdReceipts = financeDetail.getFinScheduleData().getImdReceipts();
		List<FinFeeReceipt> prevFeeReceipts = financeDetail.getFinScheduleData().getFinFeeReceipts();

		for (FinReceiptHeader rch : imdReceipts) {
			String reference = rch.getTransactionRef();
			BigDecimal receiptAvlAmount = BigDecimal.ZERO;
			BigDecimal receiptPaidAmount = BigDecimal.ZERO;
			receiptid = rch.getReceiptID();
			receiptAmount = rch.getReceiptAmount();
			List<FinFeeReceipt> currentFeeReceipts = new ArrayList<>();

			if (prevFeeReceipts.isEmpty()) {
				receiptAvlAmount = rch.getReceiptAmount();
			} else {
				for (FinFeeReceipt finFeeReceipt : prevFeeReceipts) {
					if (receiptid == finFeeReceipt.getReceiptID()) {
						if (!finFeeReceipt.isNewRecord()) {
							FinFeeReceipt befImage = new FinFeeReceipt();
							BeanUtils.copyProperties(finFeeReceipt, befImage);
							finFeeReceipt.setBefImage(befImage);
						}

						receiptPaidAmount = receiptPaidAmount.add(finFeeReceipt.getPaidAmount());
						receiptAvlAmount = receiptAmount.subtract(receiptPaidAmount);
						finFeeReceipt.setReceiptReference(reference);
						finFeeReceipt.setAvailableAmount(receiptAvlAmount);
						receiptFound = true;
						currentFeeReceipts.add(finFeeReceipt);
					}
				}
			}

			if (receiptFound) {
				receiptFound = false;
			} else {
				FinFeeReceipt finFeeReceipt = new FinFeeReceipt();
				finFeeReceipt.setNewRecord(true);
				finFeeReceipt.setReceiptAmount(rch.getReceiptAmount());
				finFeeReceipt.setReceiptReference(reference);
				finFeeReceipt.setReceiptType(rch.getReceiptMode());
				finFeeReceipt.setRemainingFee(rch.getReceiptAmount());
				finFeeReceipt.setAvailableAmount(rch.getReceiptAmount());
				finFeeReceipt.setReceiptID(receiptid);
				finFeeReceipt.setWorkflowId(financeMain.getWorkflowId());
				finFeeReceipt.setRecordType(PennantConstants.RCD_ADD);
				currentFeeReceipts.add(finFeeReceipt);
			}

			this.finFeeReceiptMap.put(receiptid, currentFeeReceipts);
		}

		doFillFinFeeReceipts(this.finFeeReceiptMap);

		logger.debug("Leaving ");
	}

	public void doFillFinFeeReceipts(Map<Long, List<FinFeeReceipt>> finFeeReceiptMap) {
		logger.debug("Entering");

		if (finFeeReceiptMap.size() > 0) {
			this.gb_FinFeeReceipts.setVisible(true);
			this.btn_autoAllocate.setVisible(getUserWorkspace().isAllowed("FinFeeDetailListCtrl_Adjust"));
		} else {
			this.btn_autoAllocate.setVisible(false);
			this.gb_FinFeeReceipts.setVisible(false);
		}
		// IMD there is no auto allocate
		this.btn_autoAllocate.setVisible(false);

		this.div_AutoAllocate.setVisible(this.btn_autoAllocate.isVisible());

		List<FinFeeReceipt> finFeeReceipts;
		this.listBoxFinFeeReceipts.getItems().clear();
		this.finFeeReceiptMap = finFeeReceiptMap;
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		BigDecimal totReceiptAmount = BigDecimal.ZERO;
		BigDecimal totPaidAmount = BigDecimal.ZERO;
		BigDecimal totBalanceAmount = BigDecimal.ZERO;

		for (Long key : finFeeReceiptMap.keySet()) {
			finFeeReceipts = finFeeReceiptMap.get(key);

			BigDecimal paidAmount = BigDecimal.ZERO;
			for (int i = 0; i < finFeeReceipts.size(); i++) {
				FinFeeReceipt finFeeReceipt = finFeeReceipts.get(i);
				Listitem item = new Listitem();
				Listcell lc;

				if (i == 0) {
					// Receipt Type
					lc = new Listcell(finFeeReceipt.getReceiptType());
					lc.setParent(item);

					// Receipt Reference
					lc = new Listcell(finFeeReceipt.getReceiptReference());
					lc.setParent(item);

					// Receipt Amount
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(finFeeReceipt.getReceiptAmount(), formatter));
					lc.setStyle("text-align:right;");
					lc.setParent(item);

					totReceiptAmount = totReceiptAmount.add(finFeeReceipt.getReceiptAmount());
				} else {
					// Receipt Type
					lc = new Listcell();
					lc.setParent(item);

					// Receipt Reference
					lc = new Listcell();
					lc.setParent(item);

					// Receipt Amount
					lc = new Listcell();
					lc.setParent(item);
				}

				// Fee Type
				if (StringUtils.isBlank(finFeeReceipt.getVasReference())) {
					lc = new Listcell(finFeeReceipt.getFeeTypeDesc());
				} else {
					lc = new Listcell(finFeeReceipt.getVasReference());
					finFeeReceipt.setFeeTypeCode(finFeeReceipt.getVasReference());
					finFeeReceipt.setFeeTypeDesc(finFeeReceipt.getVasReference());
				}
				lc.setParent(item);

				// Paid Amount
				lc = new Listcell(PennantApplicationUtil.amountFormate(finFeeReceipt.getPaidAmount(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				paidAmount = paidAmount.add(finFeeReceipt.getPaidAmount());
				totPaidAmount = totPaidAmount.add(finFeeReceipt.getPaidAmount());

				// Remaining Fee
				if (finFeeReceipts.size() - 1 == i) {
					BigDecimal remBalance = finFeeReceipt.getReceiptAmount().subtract(paidAmount);
					lc = new Listcell(PennantApplicationUtil.amountFormate(remBalance, formatter));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					totBalanceAmount = totBalanceAmount.add(remBalance);
				} else {
					lc = new Listcell();
					lc.setParent(item);
				}

				this.listBoxFinFeeReceipts.appendChild(item);
			}

		}

		// List Footer
		Listitem item = new Listitem();
		item.setStyle("background-color:#b7dee8;");
		Listcell lc;

		// Receipt Type
		lc = new Listcell("TOTALS :");
		lc.setParent(item);
		lc.setStyle("font-weight:bold;text-align:right;");

		// Receipt Reference
		lc = new Listcell();
		lc.setParent(item);

		// Receipt Amount
		lc = new Listcell(PennantApplicationUtil.amountFormate(totReceiptAmount, formatter));
		lc.setParent(item);
		lc.setStyle("font-weight:bold;text-align:right;");

		// Fee Type
		lc = new Listcell();
		lc.setParent(item);

		// Paid Amount
		lc = new Listcell(PennantApplicationUtil.amountFormate(totPaidAmount, formatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc.setStyle("font-weight:bold;text-align:right;");

		// Remaining Fee
		// lc = new Listcell(PennantApplicationUtil.amountFormate(totBalanceAmount, formatter));
		// lc.setStyle("text-align:right;");

		// Balance Amount
		lc = new Listcell();
		Decimalbox receiptAmountBox = new Decimalbox();
		receiptAmountBox.setId("FeeReceipts_RemainingFee");
		receiptAmountBox.setMaxlength(18);
		receiptAmountBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		receiptAmountBox.setDisabled(true);
		receiptAmountBox.setValue(PennantApplicationUtil.formateAmount(totBalanceAmount, formatter));
		receiptAmountBox.setParent(lc);
		lc.setStyle("font-weight:bold;text-align:right;");
		lc.setParent(item);

		this.listBoxFinFeeReceipts.appendChild(item);

		logger.debug("Leaving ");
	}

	private void doCheckEnquiry() {
		if (isEnquiry) {
			this.btnNew_NewPaymentDetail.setVisible(false);
		}
	}

	public void onClick$btn_autoAllocate(Event event) throws InterruptedException {
		logger.debug("Entering");

		List<FinFeeDetail> finFeeDetailList = fetchFeeDetails(getFinanceDetail().getFinScheduleData(), true);
		Map<String, FinFeeDetail> finFeeDetailsMapTemp = new LinkedHashMap<>();
		Map<String, FinFeeDetail> finFeeDetailsMap = new LinkedHashMap<>();
		Map<String, BigDecimal> availableFeeAmount = new LinkedHashMap<>();
		List<FinReceiptHeader> imdReceipts = financeDetail.getFinScheduleData().getImdReceipts();

		BigDecimal totFeesPaidAmount = BigDecimal.ZERO;
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {

				if (StringUtils.isBlank(finFeeDetail.getVasReference())) {
					finFeeDetailsMapTemp.put(String.valueOf(finFeeDetail.getFeeTypeID()), finFeeDetail);
					finFeeDetailsMap.put(String.valueOf(finFeeDetail.getFeeTypeID()), finFeeDetail);
					availableFeeAmount.put(String.valueOf(finFeeDetail.getFeeTypeID()), finFeeDetail.getPaidAmount());
				} else {
					finFeeDetailsMapTemp.put(finFeeDetail.getVasReference(), finFeeDetail);
					finFeeDetailsMap.put(finFeeDetail.getVasReference(), finFeeDetail);
					availableFeeAmount.put(finFeeDetail.getVasReference(), finFeeDetail.getPaidAmount());
				}
				totFeesPaidAmount = totFeesPaidAmount.add(finFeeDetail.getPaidAmount());
			}
		}

		BigDecimal totReceiptPaidAmount = BigDecimal.ZERO;
		for (FinReceiptHeader rch : imdReceipts) {
			totReceiptPaidAmount = totReceiptPaidAmount.add(rch.getReceiptAmount());
		}
		if (totReceiptPaidAmount.compareTo(totFeesPaidAmount) < 0) {
			MessageUtil.showError(Labels.getLabel("label_FinFeeReceiptDialog_PaiBox_Error.value"));
			return;
		} else {
			// Fee Receipts
			long receiptid = 0;
			boolean receiptFound = false;
			BigDecimal receiptAmount = BigDecimal.ZERO;
			Map<Long, List<FinFeeReceipt>> curFeeReceiptMap = new LinkedHashMap<>();

			for (FinReceiptHeader rch : imdReceipts) {
				String reference = rch.getTransactionRef();
				receiptid = rch.getReceiptID();
				receiptAmount = rch.getReceiptAmount();
				List<FinFeeReceipt> currentFeeReceipts = new ArrayList<>();

				for (String key : finFeeDetailsMap.keySet()) {
					if (!finFeeDetailsMapTemp.containsKey(key)) {
						continue;
					}
					receiptFound = true;
					FinFeeDetail finFeeDetail = finFeeDetailsMap.get(key);

					FinFeeReceipt finFeeReceipt = new FinFeeReceipt();
					if (StringUtils.isBlank(finFeeDetail.getVasReference())) {
						finFeeReceipt.setFeeTypeId(finFeeDetail.getFeeTypeID());
						finFeeReceipt.setFeeTypeCode(finFeeDetail.getFeeTypeCode());
						finFeeReceipt.setFeeTypeDesc(finFeeDetail.getFeeTypeDesc());
					} else {
						finFeeReceipt.setVasReference(finFeeDetail.getVasReference());
						finFeeReceipt.setFeeTypeCode(finFeeDetail.getVasReference());
						finFeeReceipt.setFeeTypeDesc(finFeeDetail.getVasReference());
					}
					finFeeReceipt.setReceiptReference(reference);
					finFeeReceipt.setReceiptType(rch.getReceiptMode());
					finFeeReceipt.setRemainingFee(rch.getReceiptAmount());
					finFeeReceipt.setAvailableAmount(rch.getReceiptAmount());
					finFeeReceipt.setReceiptID(receiptid);
					finFeeReceipt.setWorkflowId(financeMain.getWorkflowId());
					finFeeReceipt.setReceiptAmount(rch.getReceiptAmount());
					currentFeeReceipts.add(finFeeReceipt);

					BigDecimal feepaidAmount = availableFeeAmount.get(key);
					if (receiptAmount.compareTo(feepaidAmount) >= 0) {
						finFeeReceipt.setPaidAmount(feepaidAmount);
						finFeeDetailsMapTemp.remove(key);
					} else {
						finFeeReceipt.setPaidAmount(receiptAmount);
						availableFeeAmount.put(key, feepaidAmount.subtract(receiptAmount));
					}
					receiptAmount = receiptAmount.subtract(finFeeReceipt.getPaidAmount());

					if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
				}

				if (receiptFound) {
					receiptFound = false;
				} else {
					FinFeeReceipt finFeeReceipt = new FinFeeReceipt();
					finFeeReceipt.setNewRecord(true);
					finFeeReceipt.setReceiptAmount(rch.getReceiptAmount());
					finFeeReceipt.setReceiptReference(reference);
					finFeeReceipt.setReceiptType(rch.getReceiptMode());
					finFeeReceipt.setRemainingFee(rch.getReceiptAmount());
					finFeeReceipt.setAvailableAmount(rch.getReceiptAmount());
					finFeeReceipt.setReceiptID(receiptid);
					finFeeReceipt.setWorkflowId(financeMain.getWorkflowId());
					finFeeReceipt.setRecordType(PennantConstants.RCD_ADD);
					currentFeeReceipts.add(finFeeReceipt);
				}

				curFeeReceiptMap.put(receiptid, currentFeeReceipts);
			}

			doFillFinFeeReceipts(curFeeReceiptMap);
		}

		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewPaymentDetail(Event event) throws InterruptedException {
		logger.debug("Entering");
		Clients.clearWrongValue(this.btnNew_NewPaymentDetail);
		doClearFeeWrongValueExceptions();

		final FeePaymentDetail aFeePaymentDetail = new FeePaymentDetail();
		aFeePaymentDetail.setFinReference(getFinanceDetail().getFinScheduleData().getFinReference());
		aFeePaymentDetail.setNewRecord(true);
		aFeePaymentDetail.setWorkflowId(0);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("feePaymentDetail", aFeePaymentDetail);
		map.put("ccyFormatter", ccyFormat);
		map.put("finFeeDetailListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
		map.put("moduleType", ModuleType_Loan);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeePaymentDetailDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onPaymentDetailDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering");

		Clients.clearWrongValue(this.btnNew_NewPaymentDetail);
		doClearFeeWrongValueExceptions();

		Listitem listitem = this.listBoxPaymentDetails.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FeePaymentDetail aFeePaymentDetail = (FeePaymentDetail) listitem.getAttribute("data");
			if (isDeleteRecord(aFeePaymentDetail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				aFeePaymentDetail.setNewRecord(false);

				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("feePaymentDetail", aFeePaymentDetail);
				map.put("ccyFormatter", ccyFormat);
				map.put("finFeeDetailListCtrl", this);
				map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
				map.put("roleCode", roleCode);
				map.put("enqModule", isEnquiry);
				map.put("moduleType", ModuleType_Loan);

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeePaymentDetailDialog.zul", null,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}

		logger.debug("Leaving");
	}

	public void doFillFeePaymentDetails(List<FeePaymentDetail> feePaymentDetails, boolean isDataModify) {
		logger.debug("Entering");

		this.listBoxPaymentDetails.getItems().clear();
		setFeePaymentDetailList(feePaymentDetails);
		getFinanceDetail().setFeePaymentDetailList(feePaymentDetails);
		if (feePaymentDetails != null && !feePaymentDetails.isEmpty()) {
			for (FeePaymentDetail detail : feePaymentDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getPaymentReference());
				lc.setParent(item);
				lc = new Listcell(detail.getPaymentMethod());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getPaymentAmount(), ccyFormat));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(detail.getValueDate()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onPaymentDetailDoubleClicked");
				this.listBoxPaymentDetails.appendChild(item);
			}
		}

		if (isDataModify) {
			doFillPaymentRefData();
		}

		logger.debug("Leaving");
	}

	public void processFeeDetails(FinScheduleData schdData, boolean isWif) {
		logger.debug("Entering");

		if (schdData == null) {
			return;
		}

		List<FinFeeDetail> finFeeDetailList = fetchFeeDetails(schdData, true);

		// For New Loan with New Customer
		CustomerDetails custDetails = financeDetail.getCustomerDetails();
		if (custDetails != null && custDetails.getCustomer().getCustID() <= 0) {
			caluclateFeeGST(finFeeDetailList, schdData);
		}

		FinanceMain fm = schdData.getFinanceMain();
		if (!fm.isNewRecord() && StringUtils.isBlank(this.moduleDefiner)) {
			List<FinFeeDetail> finFeeDetails = this.finFeeDetailService.getFinFeeDetailById(fm.getFinID(), isWif,
					"_Temp");

			if (CollectionUtils.isNotEmpty(finFeeDetails)) {
				for (FinFeeDetail feeDetail : finFeeDetails) {

					if (AccountingEvent.VAS_FEE.equals(feeDetail.getFinEvent())) {
						boolean found = false;

						if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
							for (FinFeeDetail fiinFeeDetail : finFeeDetailList) {
								if (AccountingEvent.VAS_FEE.equals(fiinFeeDetail.getFinEvent())
										&& StringUtils.equals(fiinFeeDetail.getVasReference(),
												feeDetail.getVasReference())
										&& (fiinFeeDetail.getFeeID() == feeDetail.getFeeID())) {

									found = true;
									break;
								}
							}
						}

						if (!found) {
							feeDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							feeDetail.setDataModified(true);
							finFeeDetailList.add(feeDetail);
						}
					}
				}
			}
		}

		boolean feeChanges = false;
		boolean readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance");
		if (financeMain.isQuickDisb() && readOnly && StringUtils.isBlank(this.moduleDefiner)) {
			readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance_QDP");
		}
		Cloner cloner = new Cloner();
		finFeeDetailList = cloner.deepClone(finFeeDetailList);
		if (finFeeDetailList != null && !finFeeDetailList.isEmpty()) {
			for (FinFeeDetail fee : finFeeDetailList) {
				fee.setFinReference(fm.getFinReference());
				fee.setFinID(fm.getFinID());
				fee.setRecordStatus(fm.getRecordStatus());
				fee.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				fee.setUserDetails(getUserWorkspace().getLoggedInUser());

				if (!readOnly && !PennantConstants.RECORD_TYPE_CAN.equals(fee.getRecordType()) && fee.isAlwModifyFee()
						&& !feeChanges && fee.isRcdVisible()) {
					if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, fee.getTaxComponent())) {
						if (fee.getNetAmount().compareTo(fee.getCalculatedAmount()) != 0) {
							feeChanges = true;
						}
					} else {
						if (fee.getActualAmountOriginal().compareTo(fee.getCalculatedAmount()) != 0) {
							feeChanges = true;
						}
					}
				}
			}
		}

		// if we have any Difference between calculated fee amount and actual fee amount
		if (feeChanges) {
			List<WrongValueException> wve = new ArrayList<>();
			final String msg = "Difference between calculated fee amount and actual fee amount. Do you want to proceed?";
			MessageUtil.confirm(msg, evnt -> {
				if (Messagebox.ON_NO.equals(evnt.getName())) {
					wve.add(new WrongValueException("Difference between calculated fee amount and actual fee amount."));
				}
			});
			if (wve.size() > 0) {
				showErrorDetails(wve);
			}
		}

		schdData.setFinFeeDetailList(finFeeDetailList);

		List<FinFeeReceipt> finFeeReceipts = null;
		for (FinFeeReceipt oldFinFeeReceipt : schdData.getFinFeeReceipts()) {

			finFeeReceipts = this.finFeeReceiptMap.get(oldFinFeeReceipt.getReceiptID());
			boolean receiptFound = false;
			if (finFeeReceipts == null) {
				continue;
			}

			for (FinFeeReceipt feeReceipt : finFeeReceipts) {

				if (oldFinFeeReceipt.getFeeID() == feeReceipt.getFeeID()) {

					if (StringUtils.isBlank(feeReceipt.getRecordType())) {
						FinFeeReceipt befImage = new FinFeeReceipt();
						BeanUtils.copyProperties(oldFinFeeReceipt, befImage);
						oldFinFeeReceipt.setBefImage(befImage);

						BigDecimal paidAmt = feeReceipt.getPaidAmount();
						BeanUtils.copyProperties(oldFinFeeReceipt, feeReceipt);
						feeReceipt.setPaidAmount(paidAmt);
					}

					receiptFound = true;
				}
			}

			if (!receiptFound) {
				oldFinFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				finFeeReceipts.add(oldFinFeeReceipt);
			}
		}

		finFeeReceipts = new ArrayList<FinFeeReceipt>();
		for (Long key : this.finFeeReceiptMap.keySet()) {

			List<FinFeeReceipt> finFeeReceiptsList = new ArrayList<FinFeeReceipt>();
			finFeeReceiptsList.addAll(this.finFeeReceiptMap.get(key));

			for (int i = 0; i < finFeeReceiptsList.size(); i++) {

				FinFeeReceipt finFeeReceiptTemp = finFeeReceiptsList.get(i);

				if (StringUtils.isBlank(finFeeReceiptTemp.getRecordType())) {
					finFeeReceiptTemp.setNewRecord(true);
					finFeeReceiptTemp.setRecordType(PennantConstants.RCD_ADD);
				} else if (StringUtils.isBlank(finFeeReceiptTemp.getFeeTypeCode())
						&& StringUtils.equals(finFeeReceiptTemp.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					finFeeReceiptsList.add(finFeeReceiptTemp);
				}

				if (StringUtils.isBlank(finFeeReceiptTemp.getFeeTypeCode())) {
					finFeeReceiptsList.remove(i);
					i = 0;
				}
			}
			finFeeReceipts.addAll(finFeeReceiptsList);
		}

		if (finFeeReceipts != null && !finFeeReceipts.isEmpty()) {
			for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
				finFeeReceipt.setRecordStatus(fm.getRecordStatus());
				finFeeReceipt.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				finFeeReceipt.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finFeeReceipt.setUserDetails(getUserWorkspace().getLoggedInUser());
			}
		}

		schdData.setFinFeeReceipts(finFeeReceipts);

		logger.debug("Leaving");
	}

	/**
	 * Method for rendering or adding VAS fee to existing Fee Details
	 * 
	 * @param vasFee
	 */
	public void renderVASFee(FinFeeDetail vasFee) {
		logger.debug("Entering");

		List<FinFeeDetail> feelist = fetchFeeDetails(getFinanceDetail().getFinScheduleData(), false);
		if (feelist == null) {
			feelist = new ArrayList<>();
		} else {
			String vasReference = vasFee.getVasReference();
			for (int i = 0; i < feelist.size(); i++) {
				FinFeeDetail feeDetail = feelist.get(i);

				if (StringUtils.equals(feeDetail.getVasReference(), vasReference)) {
					vasFee.setFeeSeq(feeDetail.getFeeSeq());
					vasFee.setFeeID(feeDetail.getFeeID());
					if (PennantConstants.RCD_ADD.equals(feeDetail.getRecordType())) {
						vasFee.setRecordType(PennantConstants.RCD_ADD);
					} else {
						vasFee.setRecordType(PennantConstants.RCD_UPD);
					}
					vasFee.setLastMntBy(feeDetail.getLastMntBy());
					vasFee.setLastMntOn(feeDetail.getLastMntOn());
					vasFee.setWorkflowId(feeDetail.getWorkflowId());
					feelist.remove(i); // Removing Finance Fee Detail
					break;
				}
			}
		}
		feelist.add(vasFee);
		doFillFinFeeDetailList(feelist);
		setFinFeeDetailList(feelist);
		this.dataChanged = true;

		logger.debug("Leaving");
	}

	/**
	 * Method for rendering or adding VAS fee to existing Fee Details
	 * 
	 * @param vasFee
	 */
	public void removeVASFee(String vasReferene) {
		logger.debug("Entering");

		List<FinFeeDetail> finFeeDetailsList = fetchFeeDetails(getFinanceDetail().getFinScheduleData(), false);

		if (finFeeDetailsList == null) {
			finFeeDetailsList = new ArrayList<>();
		} else {
			for (int count = 0; count < finFeeDetailsList.size(); count++) {
				FinFeeDetail feeDetail = finFeeDetailsList.get(count);
				if (StringUtils.equals(feeDetail.getVasReference(), vasReferene)) {
					removeFinFeeReceipt(feeDetail); // Removing Finance Fee Receipts
					finFeeDetailsList.remove(count); // Removing Finance Fee Detail
					break;
				}

			}
		}

		doFillFinFeeDetailList(finFeeDetailsList);
		this.dataChanged = true;

		logger.debug("Leaving");
	}

	/**
	 * Removing Fin Fee Details
	 * 
	 * @param finFeeDetail
	 */
	private void removeFinFeeReceipt(FinFeeDetail finFeeDetail) {
		logger.debug("Entering");

		if (this.finFeeReceiptMap != null && !this.finFeeReceiptMap.isEmpty()) {
			boolean receiptFound = false;

			for (Long key : this.finFeeReceiptMap.keySet()) {
				List<FinFeeReceipt> finFeeReceipts = this.finFeeReceiptMap.get(key);

				for (int i = 0; i < finFeeReceipts.size(); i++) {
					FinFeeReceipt finFeeReceipt = finFeeReceipts.get(i);

					if ((finFeeDetail.getFeeTypeID() > 0 && finFeeDetail.getFeeTypeID() == finFeeReceipt.getFeeTypeId())
							|| (StringUtils.isNotBlank(finFeeDetail.getVasReference()) && StringUtils
									.equals(finFeeDetail.getVasReference(), finFeeReceipt.getFeeTypeCode()))) {

						FinFeeReceipt finFeeReceiptTemp = null;
						if (finFeeReceipts.size() == 1) {
							finFeeReceiptTemp = new FinFeeReceipt();
							finFeeReceiptTemp.setNewRecord(true);
							finFeeReceiptTemp.setReceiptAmount(finFeeReceipt.getReceiptAmount());
							finFeeReceiptTemp.setReceiptReference(finFeeReceipt.getReceiptReference());
							finFeeReceiptTemp.setReceiptType(finFeeReceipt.getReceiptType());
							finFeeReceiptTemp.setRemainingFee(finFeeReceipt.getReceiptAmount());
							finFeeReceiptTemp.setAvailableAmount(finFeeReceipt.getReceiptAmount());
							finFeeReceiptTemp.setReceiptID(finFeeReceipt.getReceiptID());
							finFeeReceiptTemp.setWorkflowId(financeMain.getWorkflowId());
							finFeeReceiptTemp.setRecordType(PennantConstants.RCD_ADD);
						}

						finFeeReceipts.remove(i);

						if (finFeeReceiptTemp != null) {
							finFeeReceipts.add(finFeeReceiptTemp);
						}

						receiptFound = true;
						break;
					}
				}
			}

			if (receiptFound) {
				doFillFinFeeReceipts(this.finFeeReceiptMap);
			}
		}

		logger.debug("Leaving");
	}

	private List<FinFeeDetail> fetchFeeDetails(FinScheduleData aFinScheduleData, boolean validate) {
		logger.debug("Entering");

		doClearFeeWrongValueExceptions();
		Decimalbox calbox;
		Decimalbox actualBox;
		Decimalbox paidBox;
		Decimalbox waivedBox;
		Decimalbox remFeeBox;
		Combobox feeSchdMthdbox;
		Combobox paymentMthdbox;
		Intbox termsbox;

		if (CollectionUtils.isEmpty(this.finFeeDetailList)) {
			return this.finFeeDetailList;
		}

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());

		for (FinFeeDetail fee : this.finFeeDetailList) {
			fee.setDataModified(isDataMaintained(fee, fee.getBefImage()));

			if (!fee.isRcdVisible() || (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())
					&& PennantConstants.RECORD_TYPE_CAN.equals(fee.getRecordType()))) {
				if (StringUtils.isBlank(aFinScheduleData.getFinanceMain().getRecordType())) {
					fee.setNewRecord(true);
					fee.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

				continue;
			}

			calbox = getDecimalbox(FEE_UNIQUEID_CALCULATEDAMOUNT, fee);
			actualBox = getDecimalbox(FEE_UNIQUEID_ACTUALAMOUNT, fee);
			paidBox = getDecimalbox(FEE_UNIQUEID_PAID_AMOUNT, fee);
			waivedBox = getDecimalbox(FEE_UNIQUEID_WAIVEDAMOUNT, fee);
			remFeeBox = getDecimalbox(FEE_UNIQUEID_REMAINING_FEE, fee);
			paymentMthdbox = getCombobox(FEE_UNIQUEID_PAYMENTMETHOD, fee);
			feeSchdMthdbox = getCombobox(FEE_UNIQUEID_FEESCHEDULEMETHOD, fee);
			termsbox = getIntBox(FEE_UNIQUEID_TERMS, fee);

			if (calbox != null) {
				if (validate && calbox.getValue().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(calbox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
							Labels.getLabel("FeeDetail_CalculateAmount"), String.valueOf(BigDecimal.ZERO) }));
				}

				fee.setCalculatedAmount(
						PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(calbox.doubleValue()), formatter));
			}

			if (actualBox != null) {
				if (validate && actualBox.getValue() != null && actualBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(actualBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
							Labels.getLabel("FeeDetail_ActualAmount"), String.valueOf(BigDecimal.ZERO) }));
				}
				fee.setActualAmountOriginal(
						PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(actualBox.doubleValue()), formatter));
			}

			try {
				if (paidBox != null) {
					if (validate && paidBox.getValue() != null && paidBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(paidBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
								Labels.getLabel("FeeDetail_PaidAmount"), String.valueOf(BigDecimal.ZERO) }));
					}
					if (validate && BigDecimal.valueOf(paidBox.doubleValue())
							.compareTo(PennantApplicationUtil.formateAmount(fee.getNetAmount(), formatter)) > 0) {
						throw new WrongValueException(paidBox,
								Labels.getLabel("label_FeeDetail_Validation_Exceed",
										new String[] { Labels.getLabel("FeeDetail_PaidAmount"),
												Labels.getLabel("FeeDetail_ActualAmount") }));
					}
					fee.setPaidAmount(PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(paidBox.doubleValue()),
							formatter));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (waivedBox != null) {
				if (validate && waivedBox.getValue() != null && waivedBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(waivedBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
							Labels.getLabel("FeeDetail_WaivedAmount"), String.valueOf(BigDecimal.ZERO) }));
				}
				BigDecimal waivedAmtTemp = BigDecimal.valueOf(waivedBox.doubleValue());
				try {
					if (validate && waivedAmtTemp
							.compareTo(PennantApplicationUtil.formateAmount(fee.getActualAmount(), formatter)) > 0) {
						throw new WrongValueException(waivedBox,
								Labels.getLabel("label_FeeDetail_Validation_Exceed",
										new String[] { Labels.getLabel("FeeDetail_WaivedAmount"),
												Labels.getLabel("FeeDetail_ActualAmount") }));
					}
					if (fee.getMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal alwWaiverAmt = fee.getActualAmount().multiply(fee.getMaxWaiverPerc())
								.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
						alwWaiverAmt = PennantApplicationUtil.formateAmount(alwWaiverAmt, formatter);
						if (validate && waivedAmtTemp.compareTo(alwWaiverAmt) > 0) {
							throw new WrongValueException(waivedBox,
									Labels.getLabel("label_Fee_WaiverPercentage_Exceed", new String[] {
											String.valueOf(fee.getMaxWaiverPerc()), alwWaiverAmt.toPlainString() }));
						}
					}
					fee.setWaivedAmount(PennantApplicationUtil.unFormateAmount(waivedAmtTemp, formatter));
					BigDecimal nonOutstandingFeeAmt = fee.getPaidAmount().add(fee.getWaivedAmount());
					if (validate && fee.getPaidAmount().compareTo(fee.getActualAmount()) <= 0
							&& nonOutstandingFeeAmt.compareTo(fee.getActualAmount()) > 0) {
						throw new WrongValueException(waivedBox, Labels.getLabel("label_Fee_PaidWaiverAmount_Exceed"));
					}

				} catch (WrongValueException we) {
					wve.add(we);
				}
			}

			try {
				if (remFeeBox != null) {
					if (validate && remFeeBox.getValue() != null
							&& remFeeBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(remFeeBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
								Labels.getLabel("FeeDetail_RemFeeAmount"), String.valueOf(BigDecimal.ZERO) }));
					}
					BigDecimal remainingFee = PennantApplicationUtil
							.unFormateAmount(BigDecimal.valueOf(remFeeBox.doubleValue()), formatter);

					if (StringUtils.isNotBlank(this.moduleDefiner)
							&& !FinServiceEvent.RESTRUCTURE.equals(this.moduleDefiner)
							&& remainingFee.compareTo(BigDecimal.ZERO) != 0
							&& !FinServiceEvent.ADDDISB.equals(this.moduleDefiner)) {
						throw new WrongValueException(remFeeBox,
								Labels.getLabel("label_Fee_PaidWaiverAmount_NotEqual"));
					}

					fee.setRemainingFee(remainingFee);
				}
			} catch (WrongValueException we) {
				wve.add(we);

			}

			try {
				if (paymentMthdbox != null) {
					if (validate && fee.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
						if ("#".equals(getComboboxValue(paymentMthdbox))) {
							// throw new WrongValueException(paymentMthdbox, Labels.getLabel("STATIC_INVALID", new
							// String[] { Labels.getLabel("FeeDetail_PaymentRef") }));
						}
					}
					fee.setPaymentRef(getComboboxValue(paymentMthdbox));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (feeSchdMthdbox != null) {
					if (validate && fee.getRemainingFee().compareTo(BigDecimal.ZERO) > 0) {
						if ("#".equals(getComboboxValue(feeSchdMthdbox))) {
							throw new WrongValueException(feeSchdMthdbox, Labels.getLabel("STATIC_INVALID",
									new String[] { Labels.getLabel("FeeDetail_FeeScheduleMethod") }));
						}
					}
					fee.setFeeScheduleMethod(getComboboxValue(feeSchdMthdbox));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (termsbox != null) {
					if (validate && !termsbox.isReadonly() && !termsbox.isDisabled() && termsbox.intValue() == 0) {
						throw new WrongValueException(termsbox,
								Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_Fee_Terms") }));
					}
					if (validate && !termsbox.isReadonly() && !termsbox.isDisabled() && termsbox.intValue() < 0) {
						throw new WrongValueException(termsbox, Labels.getLabel("NUMBER_NOT_NEGATIVE",
								new String[] { Labels.getLabel("label_Fee_Terms") }));
					}
					if (validate && !termsbox.isReadonly() && !termsbox.isDisabled()
							&& termsbox.intValue() > aFinScheduleData.getFinanceMain().getNumberOfTerms()) {
						throw new WrongValueException(termsbox, Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {
								Labels.getLabel("listheader_FeeDetailList_Terms.label"), numberOfTermsLabel }));
					}
					fee.setTerms(termsbox.intValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (aFinScheduleData.getFinanceMain().isWorkflow()) {
				// Saving Properties
				if (fee.isNewRecord()) {
					fee.setDataModified(true);
					fee.setVersion(1);
					fee.setRecordType(PennantConstants.RCD_ADD);
				} else {
					fee.setDataModified(isDataMaintained(fee, fee.getBefImage()));
				}
			} else {
				if (fee.isNewRecord()) {
					fee.setDataModified(true);
					fee.setVersion(1);
					fee.setRecordType(PennantConstants.RCD_ADD);
				} else {
					fee.setDataModified(isDataMaintained(fee, fee.getBefImage()));
					fee.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}

			fee.setRecordStatus(this.userAction != null ? this.recordStatus.getValue() : "");
		}

		showErrorDetails(wve);

		logger.debug("Leaving");

		return this.finFeeDetailList;
	}

	/**
	 * Method for Fetching Paid amount from the existing List on Servicing(Receipts) to Make allocations correctlys
	 * 
	 * @return
	 */
	public BigDecimal getFeePaidAmount(int formatter) {
		logger.debug("Entering");

		BigDecimal totalPaidAmt = BigDecimal.ZERO;
		if (this.finFeeDetailList != null && !this.finFeeDetailList.isEmpty()) {
			for (FinFeeDetail finFeeDetail : this.finFeeDetailList) {
				if (!finFeeDetail.isRcdVisible()) {
					continue;
				}
				Decimalbox totalNetFeeBox = (Decimalbox) this.listBoxFeeDetail
						.getFellow(getComponentId(FEE_UNIQUEID_NET_TOTALAMOUNT, finFeeDetail));
				BigDecimal netAmt = PennantApplicationUtil.unFormateAmount(totalNetFeeBox.getValue(), formatter);
				totalPaidAmt = totalPaidAmt.add(netAmt);
			}
		}

		logger.debug("Leaving");
		return totalPaidAmt;
	}

	public BigDecimal getExcessReceiptAmount(int formatter) {
		logger.debug("Entering");
		Decimalbox receiptbox = (Decimalbox) listBoxFinFeeReceipts.getFellowIfAny("FeeReceipts_RemainingFee");

		BigDecimal excessAmount = PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(receiptbox.doubleValue()),
				2);
		logger.debug("Leaving");
		return excessAmount;
	}

	private boolean isDataMaintained(FinFeeDetail finFeeDetail, FinFeeDetail finFeeDetailBefImage) {
		if (finFeeDetailBefImage == null) {
			return true;
		} else {
			for (Field field : finFeeDetail.getClass().getDeclaredFields()) {
				if (StringUtils.equalsIgnoreCase(field.getName(), "befImage")
						|| StringUtils.equalsIgnoreCase(field.getName(), "validateFinFeeDetail")) {
					continue;
				}
				try {
					field.setAccessible(true);
					if (field.get(finFeeDetail) == null && field.get(finFeeDetailBefImage) != null) {
						return true;
					} else if (field.get(finFeeDetail) != null && field.get(finFeeDetailBefImage) == null) {
						return true;
					} else if (field.get(finFeeDetail) != null && field.get(finFeeDetailBefImage) != null
							&& !field.get(finFeeDetail).equals(field.get(finFeeDetailBefImage))) {
						return true;
					}
				} catch (Exception e) {
					return true;
				}
			}
		}
		return false;
	}

	private void showErrorDetails(List<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			if (parentTab != null) {
				parentTab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	public void doFillFinFeeDetailList(List<FinFeeDetail> finFeeDetails) {
		logger.debug("Entering");

		List<ValueLabel> remFeeSchList = PennantStaticListUtil.getRemFeeSchdMethods();
		this.listBoxFeeDetail.getItems().clear();
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		finFeeDetails = sortFeesByFeeOrder(finFeeDetails);
		setFinFeeDetailList(finFeeDetails);

		boolean readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance");
		if (financeMain.isQuickDisb() && readOnly && StringUtils.isBlank(this.moduleDefiner)) {
			readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance_QDP");
		}

		this.listheader_FeeDetailList_RemainingTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		this.listheader_FeeDetailList_PaidTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		this.listheader_FeeDetailList_NetTdsAmount.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);

		boolean isDisbServ = false;
		if (StringUtils.equals(this.moduleDefiner, FinServiceEvent.ADDDISB)) {
			isDisbServ = true;
		}

		if (CollectionUtils.isNotEmpty(finFeeDetails)) {

			for (FinFeeDetail finFeeDetail : finFeeDetails) {

				if (!finFeeDetail.isRcdVisible() || (AccountingEvent.VAS_FEE.equals(finFeeDetail.getFinEvent())
						&& PennantConstants.RECORD_TYPE_CAN.equals(finFeeDetail.getRecordType()))) {
					continue;
				}

				// setting paid amount and remaining amount
				if (isDisbServ) {
					finFeeDetail.setPaidAmount(BigDecimal.ZERO);
					finFeeDetail
							.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()));
				}

				TaxHeader taxHeader = finFeeDetail.getTaxHeader();
				BigDecimal totNetGST = BigDecimal.ZERO;
				BigDecimal totPaidGST = BigDecimal.ZERO;
				BigDecimal totRemGST = BigDecimal.ZERO;

				if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
					for (Taxes tax : taxHeader.getTaxDetails()) {
						totNetGST = totNetGST.add(tax.getNetTax());
						totPaidGST = totPaidGST.add(tax.getPaidTax());
						totRemGST = totRemGST.add(tax.getRemFeeTax());
					}
				}
				Listitem item = new Listitem();
				Listcell lc;
				String taxComponent = null;

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
				} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
				} else {
					taxComponent = Labels.getLabel("label_GST_NotApplicable");
				}

				String feeType = finFeeDetail.getFeeTypeDesc() + " - (" + taxComponent + ")";
				if (StringUtils.isNotEmpty(finFeeDetail.getVasReference())) {
					feeType = finFeeDetail.getVasReference();
					finFeeDetail.setFeeTypeCode(feeType);
					finFeeDetail.setFeeTypeDesc(feeType);
				}

				// Fee Type
				if (finFeeDetail.getFeeTypeID() <= 0) {
					lc = new Listcell(feeType); // For VAS Fees
				} else {
					A feeTypeLink = new A(); // For Normal Fees
					feeTypeLink.setLabel(feeType);
					lc = new Listcell();
					feeTypeLink.addForward("onClick", self, "onClickFeeType", finFeeDetail);
					feeTypeLink.setStyle("text-decoration:none;");
					lc.appendChild(feeTypeLink);
				}
				lc.setParent(item);

				// Calculate Amount
				Decimalbox calBox = new Decimalbox();
				calBox.setWidth("85px");
				calBox.setMaxlength(18);
				calBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				calBox.setDisabled(true);
				calBox.setId(getComponentId(FEE_UNIQUEID_CALCULATEDAMOUNT, finFeeDetail));
				calBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getCalculatedAmount(), formatter));
				lc = new Listcell();
				lc.appendChild(calBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Actual Amount Original
				Decimalbox actualBox = new Decimalbox();
				actualBox.setWidth("85px");
				actualBox.setMaxlength(18);
				actualBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, finFeeDetail.getTaxComponent())) {
					actualBox.setDisabled(true);
				} else {
					actualBox.setDisabled(readOnly ? true : !finFeeDetail.isAlwModifyFee());
				}

				if (finFeeDetail.isRestructureFee()
						|| (getFinanceDetail().getFinScheduleData().getRestructureDetail() != null
								&& FinServiceEvent.RESTRUCTURE.equals(getFinanceDetail().getModuleDefiner()))) {
					actualBox.setDisabled(true);
				}

				actualBox.setId(getComponentId(FEE_UNIQUEID_ACTUALAMOUNT, finFeeDetail));
				actualBox.setValue(
						PennantApplicationUtil.formateAmount(finFeeDetail.getActualAmountOriginal(), formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(actualBox);
				lc.setParent(item);

				// Waived Amount
				Decimalbox waiverBox = new Decimalbox();
				waiverBox.setWidth("85px");
				waiverBox.setMaxlength(18);
				waiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				if (!FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())
						&& finFeeDetail.getMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
					waiverBox.setDisabled(readOnly);
				} else {
					waiverBox.setDisabled(true);
				}
				waiverBox.setId(getComponentId(FEE_UNIQUEID_WAIVEDAMOUNT, finFeeDetail));
				waiverBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getWaivedAmount(), formatter));
				lc = new Listcell();
				lc.appendChild(waiverBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Net Fee Original
				Decimalbox netFeeBoxOriginal = new Decimalbox();
				netFeeBoxOriginal.setWidth("75px");
				netFeeBoxOriginal.setMaxlength(18);
				netFeeBoxOriginal.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				netFeeBoxOriginal.setDisabled(true);
				netFeeBoxOriginal.setId(getComponentId(FEE_UNIQUEID_NET_ORIGINAL, finFeeDetail));
				netFeeBoxOriginal
						.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmountOriginal(), formatter));
				lc = new Listcell();
				lc.appendChild(netFeeBoxOriginal);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// NET Fee GST
				Decimalbox netFeeGstBox = new Decimalbox();
				netFeeGstBox.setWidth("75px");
				netFeeGstBox.setMaxlength(18);
				netFeeGstBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				netFeeGstBox.setDisabled(true);
				netFeeGstBox.setId(getComponentId(FEE_UNIQUEID_NET_GST, finFeeDetail));
				netFeeGstBox.setValue(PennantApplicationUtil.formateAmount(totNetGST, formatter));
				lc = new Listcell();
				lc.appendChild(netFeeGstBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// NET Fee TDS
				Decimalbox netTDSBox = new Decimalbox();
				netTDSBox.setWidth("75px");
				netTDSBox.setMaxlength(18);
				netTDSBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				netTDSBox.setDisabled(true);
				netTDSBox.setId(getComponentId(FEE_UNIQUEID_NET_TDS, finFeeDetail));
				netTDSBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetTDS(), formatter));
				lc = new Listcell();
				lc.appendChild(netTDSBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Net Fee
				Decimalbox netFeeBox = new Decimalbox();
				netFeeBox.setWidth("85px");
				netFeeBox.setMaxlength(18);
				netFeeBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

				if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, finFeeDetail.getTaxComponent())) {
					netFeeBox.setDisabled(readOnly ? true : !finFeeDetail.isAlwModifyFee());
				} else {
					netFeeBox.setDisabled(true);
				}
				netFeeBox.setId(getComponentId(FEE_UNIQUEID_NET_TOTALAMOUNT, finFeeDetail));
				netFeeBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmount(), formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(netFeeBox);
				lc.setParent(item);

				// Paid Fee Original
				Decimalbox curPaidFeeBox = new Decimalbox();
				curPaidFeeBox.setWidth("75px");
				curPaidFeeBox.setMaxlength(18);
				curPaidFeeBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					curPaidFeeBox.setDisabled(readOnly);
				} else {
					curPaidFeeBox.setDisabled(true);
				}
				curPaidFeeBox.setId(getComponentId(FEE_UNIQUEID_PAID_ORIGINALAMOUNT, finFeeDetail));
				curPaidFeeBox.setValue(
						PennantApplicationUtil.formateAmount(finFeeDetail.getPaidAmountOriginal(), formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(curPaidFeeBox);
				lc.setParent(item);

				// Paid GST Fee
				Decimalbox paidGSTBox = new Decimalbox();
				paidGSTBox.setWidth("75px");
				paidGSTBox.setMaxlength(18);
				paidGSTBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				paidGSTBox.setDisabled(true);
				paidGSTBox.setId(getComponentId(FEE_UNIQUEID_PAID_GST, finFeeDetail));
				paidGSTBox.setValue(PennantApplicationUtil.formateAmount(totPaidGST, formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(paidGSTBox);
				lc.setParent(item);

				// Paid TDS
				Decimalbox paidTDSBox = new Decimalbox();
				paidTDSBox.setWidth("75px");
				paidTDSBox.setMaxlength(18);
				paidTDSBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				paidTDSBox.setDisabled(true);
				paidTDSBox.setId(getComponentId(FEE_UNIQUEID_PAID_TDS, finFeeDetail));
				paidTDSBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getPaidTDS(), formatter));
				lc = new Listcell();
				lc.appendChild(paidTDSBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Paid Fee
				Decimalbox paidBox = new Decimalbox();
				paidBox.setWidth("75px");
				paidBox.setMaxlength(18);
				paidBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				if (!FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					paidBox.setDisabled(readOnly);
				} else {
					paidBox.setDisabled(true);
				}

				if (AdvanceRuleCode.ADVEMI.name().equals(finFeeDetail.getFeeTypeCode())
						|| AdvanceRuleCode.ADVINT.name().equals(finFeeDetail.getFeeTypeCode())) {
					paidBox.setDisabled(true);
				}

				paidBox.setId(getComponentId(FEE_UNIQUEID_PAID_AMOUNT, finFeeDetail));
				paidBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getPaidAmount(), formatter));
				lc = new Listcell();
				lc.appendChild(paidBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Remaining Fee Original
				Decimalbox remainingOriginalBox = new Decimalbox();
				remainingOriginalBox.setWidth("75px");
				remainingOriginalBox.setMaxlength(18);
				remainingOriginalBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				remainingOriginalBox.setDisabled(true);
				remainingOriginalBox.setId(getComponentId(FEE_UNIQUEID_REMAINING_ORIGINAL, finFeeDetail));
				remainingOriginalBox.setValue(
						PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFeeOriginal(), formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(remainingOriginalBox);
				lc.setParent(item);

				// Remaining Fee GST
				Decimalbox remainingGSTBox = new Decimalbox();
				remainingGSTBox.setWidth("75px");
				remainingGSTBox.setMaxlength(18);
				remainingGSTBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				remainingGSTBox.setDisabled(true);
				remainingGSTBox.setId(getComponentId(FEE_UNIQUEID_REMAININ_GST, finFeeDetail));
				remainingGSTBox.setValue(PennantApplicationUtil.formateAmount(totRemGST, formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(remainingGSTBox);
				lc.setParent(item);

				// Remaining TDS
				Decimalbox remainingTDSBox = new Decimalbox();
				remainingTDSBox.setWidth("75px");
				remainingTDSBox.setMaxlength(18);
				remainingTDSBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				remainingTDSBox.setDisabled(true);
				remainingTDSBox.setId(getComponentId(FEE_UNIQUEID_REMAININ_TDS, finFeeDetail));
				remainingTDSBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemTDS(), formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(remainingTDSBox);
				lc.setParent(item);

				// Remaining Fee
				Decimalbox remFeeBox = new Decimalbox();
				remFeeBox.setWidth("75px");
				remFeeBox.setMaxlength(18);
				remFeeBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				remFeeBox.setDisabled(true);
				remFeeBox.setId(getComponentId(FEE_UNIQUEID_REMAINING_FEE, finFeeDetail));

				if (finFeeDetail.getActualAmount()
						.equals(finFeeDetail.getWaivedAmount().add(finFeeDetail.getWaivedGST()))) {
					finFeeDetail.setRemainingFee(BigDecimal.ZERO);
				}

				remFeeBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFee(), formatter));
				lc = new Listcell();
				lc.appendChild(remFeeBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Payment Method
				lc = new Listcell();
				Combobox payMethCombo = new Combobox();
				fillPaymentRefComboBox(payMethCombo, finFeeDetail.getPaymentRef(), this.feePaymentDetailList, "");
				payMethCombo.setWidth("96%");
				payMethCombo.setId(getComponentId(FEE_UNIQUEID_PAYMENTMETHOD, finFeeDetail));
				payMethCombo.setDisabled(readOnly);
				lc.appendChild(payMethCombo);
				lc.setParent(item);

				// Remaining Fee schedule Method
				lc = new Listcell();
				String excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ",";
				String feeScheduleMethod = finFeeDetail.getFeeScheduleMethod();

				if (finFeeDetail.isTaxApplicable()) {
					if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT, feeScheduleMethod)
							|| StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR, feeScheduleMethod)
							|| StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS,
									feeScheduleMethod)) {
						feeScheduleMethod = "";
					}
					excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + ",";
				}

				if (FinServiceEvent.RESTRUCTURE.equals(this.moduleDefiner)) {
					excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
							+ CalculationConstants.REMFEE_PART_OF_DISBURSE + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + ",";

					feeScheduleMethod = CalculationConstants.REMFEE_PART_OF_SALE_PRICE;
					finFeeDetail.setFeeScheduleMethod(feeScheduleMethod);
				}

				if (StringUtils.equals(CalculationConstants.REMFEE_WAIVED_BY_BANK, feeScheduleMethod)) {
					feeScheduleMethod = "";
				}
				Combobox feeSchdMethCombo = new Combobox();
				fillComboBox(feeSchdMethCombo, feeScheduleMethod, remFeeSchList, excludeFields);
				feeSchdMethCombo.setWidth("135px");
				feeSchdMethCombo.setId(getComponentId(FEE_UNIQUEID_FEESCHEDULEMETHOD, finFeeDetail));
				boolean feeSchdMthdDisable = true;
				if (finFeeDetail.isAlwModifyFeeSchdMthd()
						&& finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) > 0) {
					feeSchdMthdDisable = readOnly;
				}

				if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeScheduleMethod)) {
					feeSchdMthdDisable = true;
				}

				feeSchdMethCombo.setDisabled(feeSchdMthdDisable);

				if (finFeeDetail.isRestructureFee()
						|| (getFinanceDetail().getFinScheduleData().getRestructureDetail() != null
								&& FinServiceEvent.RESTRUCTURE.equals(getFinanceDetail().getModuleDefiner()))) {
					feeSchdMethCombo.setDisabled(true);
				}

				lc.appendChild(feeSchdMethCombo);
				lc.setParent(item);

				/*
				 * if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
				 * CalculationConstants.REMFEE_WAIVED_BY_BANK)) { remFeeBox.setValue(BigDecimal.ZERO);
				 * paidBox.setValue(BigDecimal.ZERO); waiverBox.setValue(actualBox.getValue()); } else if
				 * (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
				 * CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) { remFeeBox.setValue(BigDecimal.ZERO);
				 * waiverBox.setValue(BigDecimal.ZERO); paidBox.setValue(actualBox.getValue()); }
				 */

				// Terms
				Intbox termsBox = new Intbox();
				termsBox.setWidth("50px");
				termsBox.setMaxlength(5);
				termsBox.setId(getComponentId(FEE_UNIQUEID_TERMS, finFeeDetail));
				termsBox.setValue(finFeeDetail.getTerms());
				boolean termsDisable = false;
				if (finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) > 0) {
					if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
						termsDisable = readOnly;
					} else {
						termsDisable = true;
					}
				} else {
					termsDisable = true;
				}
				termsBox.setDisabled(termsDisable);
				lc = new Listcell();
				lc.appendChild(termsBox);
				lc.setParent(item);

				// Adjust Button
				Button adjust = new Button("Adjust");
				adjust.setId(getComponentId(FEE_UNIQUEID_ADJUST, finFeeDetail));
				lc = new Listcell();
				lc.appendChild(adjust);
				if ((finFeeDetail.isNewRecord() && finFeeDetail.isOriginationFee())
						|| (paidBox.getValue().compareTo(BigDecimal.ZERO) == 0)) {
					readOnlyComponent(true, adjust);
				} else {
					readOnlyComponent(isReadOnly("FinFeeDetailListCtrl_Adjust"), adjust);
				}
				lc.setParent(item);

				// GST Details Button
				Button gstDetails = new Button("GST Details");
				gstDetails.setId(getComponentId(FEE_UNIQUEID_GSTDETAILS, finFeeDetail));
				lc = new Listcell();
				lc.appendChild(gstDetails);
				readOnlyComponent(!finFeeDetail.isTaxApplicable(), gstDetails);
				lc.setParent(item);

				// As per IMD requirement paid fields should be readOnly
				curPaidFeeBox.setDisabled(true);
				paidBox.setDisabled(true);
				if (paidBox.getValue().compareTo(BigDecimal.ZERO) != 0) {
					readOnlyComponent(true, feeSchdMethCombo);
				}

				List<Object> amountBoxlist = new ArrayList<Object>(11);
				amountBoxlist.add(actualBox); // 0
				amountBoxlist.add(paidBox); // 1
				amountBoxlist.add(waiverBox); // 2
				amountBoxlist.add(remFeeBox); // 3
				amountBoxlist.add(feeSchdMethCombo); // 4
				amountBoxlist.add(termsBox); // 5
				amountBoxlist.add(finFeeDetail); // 6
				amountBoxlist.add(financeMain.isQuickDisb()); // 7
				amountBoxlist.add(feeType); // 8
				amountBoxlist.add(finFeeDetail.getFeeTypeID()); // 9
				amountBoxlist.add(adjust); // 10
				amountBoxlist.add(curPaidFeeBox); // 11
				amountBoxlist.add(paidGSTBox); // 12
				amountBoxlist.add(remainingOriginalBox); // 13
				amountBoxlist.add(remainingGSTBox); // 14
				amountBoxlist.add(netFeeBoxOriginal); // 15
				amountBoxlist.add(netFeeGstBox); // 16
				amountBoxlist.add(netFeeBox); // 17
				amountBoxlist.add(netTDSBox); // 18
				amountBoxlist.add(paidTDSBox); // 19
				amountBoxlist.add(remainingTDSBox); // 20

				// Actual Fees
				actualBox.addForward("onChange", window_FeeDetailList, "onChangeActualBox", amountBoxlist);

				// Paid Fees
				paidBox.addForward("onChange", window_FeeDetailList, "onChangeFeeAmount", amountBoxlist);
				curPaidFeeBox.addForward("onChange", window_FeeDetailList, "onChangeFeeAmount", amountBoxlist);

				// Waiver Fees
				waiverBox.addForward("onChange", window_FeeDetailList, "onChangeFeeAmount", amountBoxlist);

				// Net Amounts
				netFeeBox.addForward("onChange", window_FeeDetailList, "onChangeNetAmount", amountBoxlist);

				// Terms
				termsBox.addForward("onChange", window_FeeDetailList, "onChangeFeeTerms", null);

				// Fee Schedule Method
				feeSchdMethCombo.addForward("onChange", window_FeeDetailList, "onChangeFeeScheduleMethod",
						amountBoxlist);

				// Adjust Button
				adjust.addForward("onClick", window_FeeDetailList, "onClickAdjust", amountBoxlist);

				// GST Details Button
				gstDetails.addForward("onClick", window_FeeDetailList, "onClickGSTDetails", finFeeDetail);

				this.listBoxFeeDetail.appendChild(item);
			}
		}

		// To Reset Totals
		if (isReceiptsProcess && this.financeMainDialogCtrl != null) {
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("resetFeeAmounts", Boolean.class)
						.invoke(getFinanceMainDialogCtrl(), true);
			} catch (Exception e) {
				logger.info(e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * onClick FeeType Hyper Link it will redirect to FeeType Master
	 * 
	 * @param event
	 */
	public void onClickFeeType(ForwardEvent event) {
		logger.debug("Entering");

		FinFeeDetail details = (FinFeeDetail) event.getData();

		FeeType feeType = feeTypeService.getFeeTypeById(details.getFeeTypeID());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("feeType", feeType);
		map.put("feeTypeEnquiry", true);

		try {
			Executions.createComponents("/WEB-INF/pages/FeeType/FeeType/FeeTypeDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<FinFeeDetail> sortFeesByFeeOrder(List<FinFeeDetail> finFeeDetailList) {
		Comparator<FinFeeDetail> beanComp = new BeanComparator("feeOrder");
		Collections.sort(finFeeDetailList, beanComp);
		return finFeeDetailList;
	}

	/**
	 * Method for Record each log Entry of Modification either Waiver/Paid By Customer
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeFeeAmount(ForwardEvent event) {
		logger.debug("Entering");

		List<Object> list = (List<Object>) event.getData();
		feeChanges(list);

		logger.debug("Leaving");
	}

	/**
	 * Method for Record each log Entry of Modification Net Amount
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeNetAmount(ForwardEvent event) {
		logger.debug("Entering");

		List<Object> list = (List<Object>) event.getData();
		FinFeeDetail finFeeDetail = (FinFeeDetail) list.get(6);
		finFeeDetail.setFeeModified(true);
		feeChanges(list);

		logger.debug("Leaving");
	}

	private void feeChanges(List<Object> list) {
		logger.debug("Entering");

		Decimalbox actualBox = (Decimalbox) list.get(0);
		Decimalbox paidBox = (Decimalbox) list.get(1);
		Decimalbox waiverBox = (Decimalbox) list.get(2);
		Decimalbox remFeeBox = (Decimalbox) list.get(3);
		Combobox feeSchdMthdBox = (Combobox) list.get(4);
		Intbox termsBox = (Intbox) list.get(5);
		FinFeeDetail finFeeDetail = (FinFeeDetail) list.get(6);
		boolean quickDisb = (boolean) list.get(7);
		Button adjustButton = (Button) list.get(10);
		Decimalbox paidBoxOriginal = (Decimalbox) list.get(11);
		Decimalbox paidBoxGST = (Decimalbox) list.get(12);
		Decimalbox remainingOriginal = (Decimalbox) list.get(13);
		Decimalbox remainingGSTBox = (Decimalbox) list.get(14);

		Decimalbox netFeeBoxOriginal = (Decimalbox) list.get(15);
		Decimalbox netFeeGstBox = (Decimalbox) list.get(16);
		Decimalbox netFeeBox = (Decimalbox) list.get(17);

		Decimalbox netTdsBox = (Decimalbox) list.get(18);
		Decimalbox paidTdsBox = (Decimalbox) list.get(19);
		Decimalbox remTdsBox = (Decimalbox) list.get(20);

		actualBox.setErrorMessage("");
		paidBox.setErrorMessage("");
		waiverBox.setErrorMessage("");
		remFeeBox.setErrorMessage("");
		feeSchdMthdBox.setErrorMessage("");
		termsBox.setErrorMessage("");

		String finCcy = financeMain.getFinCcy();
		int formatter = CurrencyUtil.getFormat(finCcy);

		finFeeDetail.setWaivedAmount(PennantApplicationUtil.unFormateAmount(waiverBox.getValue(), formatter));
		finFeeDetail
				.setPaidAmountOriginal(PennantApplicationUtil.unFormateAmount(paidBoxOriginal.getValue(), formatter));

		finFeeDetail.setNetAmount(PennantApplicationUtil.unFormateAmount(netFeeBox.getValue(), formatter));
		finFeeDetail.setPaidAmount(PennantApplicationUtil.unFormateAmount(paidBox.getValue(), formatter));

		boolean isSubventionFee = PennantConstants.FEETYPE_SUBVENTION.equals(finFeeDetail.getFeeTypeCode());

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
			TaxAmountSplit taxSplit;
			if (isSubventionFee) {
				taxSplit = GSTCalculator.getInclusiveGST(finFeeDetail.getNetAmount(), getDealerTaxPercentages());
			} else {
				taxSplit = GSTCalculator.getInclusiveGST(finFeeDetail.getNetAmount(), getTaxPercentages());
			}
			BigDecimal netAmountOrginal = finFeeDetail.getNetAmount().subtract(taxSplit.gettGST());
			finFeeDetail.setNetAmountGST(taxSplit.gettGST());
			finFeeDetail.setNetAmountOriginal(netAmountOrginal);
		}

		if (netFeeBoxOriginal.isDisabled() || paidBoxOriginal.isDisabled()) {
			resetTDSonFee(finFeeDetail, financeMain);
		}
		if (isSubventionFee) {
			this.finFeeDetailService.calculateFees(finFeeDetail, financeMain, getDealerTaxPercentages());
		} else {
			this.finFeeDetailService.calculateFees(finFeeDetail, financeMain, getTaxPercentages());
		}
		resetTDSonFee(finFeeDetail, financeMain);

		// Paid Fee
		paidBoxOriginal.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getPaidAmountOriginal(), formatter));
		paidBoxGST.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getPaidAmountGST(), formatter));
		paidTdsBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getPaidTDS(), formatter));
		paidBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getPaidAmount(), formatter));

		// Remaining Fee
		remainingOriginal
				.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFeeOriginal(), formatter));
		remainingGSTBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFeeGST(), formatter));
		remTdsBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemTDS(), formatter));
		remFeeBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFee(), formatter));

		// NET Fee
		netFeeBoxOriginal
				.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmountOriginal(), formatter));
		netFeeGstBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmountGST(), formatter));
		netTdsBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetTDS(), formatter));
		netFeeBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmount(), formatter));

		// Actual Fee
		actualBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getActualAmountOriginal(), formatter));

		boolean readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance");

		if (quickDisb && readOnly) {
			readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance_QDP");
		}

		boolean feeSchdMthdDisable = false;
		if (finFeeDetail.isAlwModifyFeeSchdMthd() && remFeeBox.getValue().compareTo(BigDecimal.ZERO) == 0) {
			feeSchdMthdDisable = readOnly;
		}

		feeSchdMthdBox.setDisabled(feeSchdMthdDisable);

		if (BigDecimal.valueOf(paidBox.doubleValue()).compareTo(BigDecimal.ZERO) == 0) {
			adjustButton.setDisabled(true);
			removeFinFeeReceipt(finFeeDetail); // Removing Fin Fee Receipts
		} else if (BigDecimal.valueOf(paidBox.doubleValue()).compareTo(BigDecimal.ZERO) < 0) {
			adjustButton.setDisabled(true);
		} else {
			if (getFinanceDetail().getFinScheduleData().getImdReceipts().isEmpty()) {
				adjustButton.setDisabled(true);
			} else {
				readOnlyComponent(isReadOnly("FinFeeDetailListCtrl_Adjust"), adjustButton);
			}
		}

		this.dataChanged = true;

		// PSD Ticket : 133623 (Early Settlement Due Payment Details Refreshed any changes done on Fees)
		// Can be utilized only on Receipts Process
		/*
		 * if (isReceiptsProcess && this.financeMainDialogCtrl != null) { try {
		 * getFinanceMainDialogCtrl().getClass().getMethod("onFeeAmountChange").invoke(getFinanceMainDialogCtrl()); }
		 * catch (Exception e) { logger.info(e); } }
		 */

		logger.debug("Leaving");
	}

	/**
	 * Method for Record each log Entry of Modification either Waiver/Paid By Customer
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeActualBox(ForwardEvent event) {
		logger.debug("Entering");

		List<Object> list = (List<Object>) event.getData();

		Decimalbox actualBox = (Decimalbox) list.get(0);
		Decimalbox paidBox = (Decimalbox) list.get(1);
		Decimalbox waiverBox = (Decimalbox) list.get(2);
		Decimalbox remFeeBox = (Decimalbox) list.get(3);
		Combobox feeSchdMthdBox = (Combobox) list.get(4);
		Intbox termsBox = (Intbox) list.get(5);
		FinFeeDetail finFeeDetail = (FinFeeDetail) list.get(6);
		boolean quickDisb = (boolean) list.get(7);
		@SuppressWarnings("unused")
		Button adjustButton = (Button) list.get(10);

		Decimalbox paidBoxOriginal = (Decimalbox) list.get(11);
		@SuppressWarnings("unused")
		Decimalbox paidBoxGST = (Decimalbox) list.get(12);

		Decimalbox remainingOriginal = (Decimalbox) list.get(13);
		Decimalbox remainingGSTBox = (Decimalbox) list.get(14);

		Decimalbox netFeeBoxOriginal = (Decimalbox) list.get(15);
		Decimalbox netFeeGstBox = (Decimalbox) list.get(16);
		Decimalbox netFeeBox = (Decimalbox) list.get(17);

		Decimalbox netTdsBox = (Decimalbox) list.get(18);
		Decimalbox paidTdsBox = (Decimalbox) list.get(19);
		Decimalbox remTdsBox = (Decimalbox) list.get(20);

		actualBox.setErrorMessage("");
		paidBox.setErrorMessage("");
		waiverBox.setErrorMessage("");
		remFeeBox.setErrorMessage("");
		feeSchdMthdBox.setErrorMessage("");
		termsBox.setErrorMessage("");

		finFeeDetail.setFeeModified(true);

		String feeSchedule = getComboboxValue(feeSchdMthdBox);
		if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
			waiverBox.setValue(actualBox.getValue());
		} else {

			String finCcy = financeMain.getFinCcy();
			int formatter = CurrencyUtil.getFormat(finCcy);

			finFeeDetail
					.setActualAmountOriginal(PennantApplicationUtil.unFormateAmount(actualBox.getValue(), formatter));

			if (!finFeeDetail.isTaxApplicable()) {
				finFeeDetail.setActualAmount(PennantApplicationUtil.unFormateAmount(actualBox.getValue(), formatter));
				finFeeDetail.setActualAmountGST(PennantApplicationUtil.unFormateAmount(BigDecimal.ZERO, formatter));
			}

			finFeeDetail.setWaivedAmount(PennantApplicationUtil.unFormateAmount(waiverBox.getValue(), formatter));

			if (!FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
				finFeeDetail.setPaidAmountOriginal(
						PennantApplicationUtil.unFormateAmount(paidBoxOriginal.getValue(), formatter));
			}

			if (netFeeBoxOriginal.isDisabled() || paidBoxOriginal.isDisabled()) {
				resetTDSonFee(finFeeDetail, financeMain);
			}

			String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;
			if (subventionFeeCode.equals(finFeeDetail.getFeeTypeCode())) {
				this.finFeeDetailService.calculateFees(finFeeDetail, financeMain, getDealerTaxPercentages());
			} else {
				this.finFeeDetailService.calculateFees(finFeeDetail, financeMain, getTaxPercentages());
			}

			resetTDSonFee(finFeeDetail, financeMain);

			remainingOriginal
					.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFeeOriginal(), formatter));
			remainingGSTBox
					.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFeeGST(), formatter));
			remTdsBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemTDS(), formatter));
			remFeeBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getRemainingFee(), formatter));

			netFeeBoxOriginal
					.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmountOriginal(), formatter));
			netFeeGstBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmountGST(), formatter));
			netTdsBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetTDS(), formatter));
			netFeeBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmount(), formatter));

			// remFeeBox.setValue(BigDecimal.valueOf(actualBox.doubleValue()).subtract(BigDecimal.valueOf(waiverBox.doubleValue())).subtract(BigDecimal.valueOf(paidBox.doubleValue())));

			if (finFeeDetail.isAlwModifyFeeSchdMthd() && remFeeBox.getValue().compareTo(BigDecimal.ZERO) == 0) {
				feeSchdMthdBox.setDisabled(true);
				feeSchdMthdBox.setSelectedIndex(0);
			} else {
				boolean readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance");
				if (quickDisb && readOnly) {
					readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance_QDP");
				}
				boolean feeSchdMthdDisable = false;
				if (finFeeDetail.isAlwModifyFeeSchdMthd() && remFeeBox.getValue().compareTo(BigDecimal.ZERO) > 0) {
					feeSchdMthdDisable = readOnly;
				} else {
					feeSchdMthdDisable = true;
				}

				feeSchdMthdBox.setDisabled(feeSchdMthdDisable);
			}
		}

		this.dataChanged = true;

		// Can be utilized only on Receipts Process
		if (isReceiptsProcess && this.financeMainDialogCtrl != null) {
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("onFeeAmountChange").invoke(getFinanceMainDialogCtrl());
			} catch (Exception e) {
				logger.info(e);
			}
		}

		// calculateFees(finFeeDetailList, financeDetail.getFinScheduleData(), financeDetail.getValueDate());

		logger.debug("Leaving");
	}

	/**
	 * Method for actions to be made on fee terms change
	 * 
	 * @param event
	 */
	public void onChangeFeeTerms(ForwardEvent event) {
		logger.debug("Entering");

		this.dataChanged = true;

		logger.debug("Leaving");
	}

	/**
	 * Method to make related changes with respective of Fee Schedule method change
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeFeeScheduleMethod(ForwardEvent event) {
		logger.debug("Entering");

		List<Object> list = (List<Object>) event.getData();

		Decimalbox actualBox = (Decimalbox) list.get(0);
		Decimalbox paidBox = (Decimalbox) list.get(1);
		Decimalbox waiverBox = (Decimalbox) list.get(2);
		Decimalbox remFeeBox = (Decimalbox) list.get(3);
		Combobox feeSchdMethodBox = (Combobox) list.get(4);
		Intbox termBox = (Intbox) list.get(5);

		String feeSchedule = getComboboxValue(feeSchdMethodBox);
		if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
			termBox.setDisabled(false);
		} else {
			termBox.setDisabled(true);
			termBox.setValue(0);
			if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				remFeeBox.setValue(BigDecimal.ZERO);
				paidBox.setValue(BigDecimal.ZERO);
				paidBox.setDisabled(true);
				waiverBox.setDisabled(true);
				waiverBox.setValue(actualBox.getValue());
			}
		}

		this.dataChanged = true;

		logger.debug("Leaving");
	}

	/**
	 * Assigning the Receipts for Fees
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public void onClickAdjust(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");

		if (this.finFeeReceiptMap == null || this.finFeeReceiptMap.isEmpty()) {
			MessageUtil.showError("Fee receipts are not available.");
			return;
		}

		List<Object> list = (List<Object>) event.getData();

		Decimalbox actualBox = (Decimalbox) list.get(0);
		Decimalbox paidBox = (Decimalbox) list.get(1);
		Decimalbox waiverBox = (Decimalbox) list.get(2);
		FinFeeDetail finFeeDetail = (FinFeeDetail) list.get(6);

		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		BigDecimal feeAmount = PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(actualBox.doubleValue()),
				formatter);
		BigDecimal waivedAmount = PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(waiverBox.doubleValue()),
				formatter);
		BigDecimal paidAmount = PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(paidBox.doubleValue()),
				formatter);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("FeeAmount", feeAmount);
		map.put("PaidAmount", paidAmount);
		map.put("PaidAmountValue", BigDecimal.valueOf(paidBox.doubleValue()));
		map.put("WaiverAmount", waivedAmount);
		map.put("finFeeDetailListCtrl", this);
		map.put("role", getRole());
		map.put("feeTypeCode", finFeeDetail.getFeeTypeCode());
		map.put("feeTypeDesc", finFeeDetail.getFeeTypeDesc());
		map.put("feeTypeId", finFeeDetail.getFeeTypeID());
		map.put("financeDetail", getFinanceDetail());
		map.put("finFeeReceiptMap", this.finFeeReceiptMap);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeReceiptDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Assigning the Receipts for Fees
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClickGSTDetails(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");

		FinFeeDetail finFeeDetail = (FinFeeDetail) event.getData();

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finFeeDetail", finFeeDetail);
		map.put("financeDetail", getFinanceDetail());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeGSTDetailsDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	private String getComponentId(String feeField, FinFeeDetail finFeeDetail) {
		if (StringUtils.isEmpty(finFeeDetail.getVasReference())) {
			return feeField + finFeeDetail.getFinEvent() + "_" + finFeeDetail.getFeeTypeCode();
		} else {
			return feeField + finFeeDetail.getFinEvent() + "_" + finFeeDetail.getVasReference();
		}
	}

	private boolean isDeleteRecord(String rcdType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, rcdType)
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)) {
			return true;
		}
		return false;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillPaymentRefComboBox(Combobox combobox, String value, List<FeePaymentDetail> list,
			String excludeFields) {
		logger.trace(Literal.ENTERING);

		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		if (list != null) {
			for (FeePaymentDetail feePaymentDetail : list) {
				if (!excludeFields.contains("," + feePaymentDetail.getPaymentReference() + ",")) {
					comboitem = new Comboitem();
					comboitem.setValue(feePaymentDetail.getPaymentReference());
					comboitem.setLabel(feePaymentDetail.getPaymentReference());
					combobox.appendChild(comboitem);
				}
				if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(feePaymentDetail.getPaymentReference()))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}

		logger.trace(Literal.LEAVING);
	}

	private void doFillPaymentRefData() {
		if (this.finFeeDetailList != null && !this.finFeeDetailList.isEmpty()) {
			Component comp = null;
			for (FinFeeDetail finFeeDetail : this.finFeeDetailList) {
				comp = this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_PAYMENTMETHOD, finFeeDetail));
				if (comp != null) {
					Combobox paymentMthdbox = (Combobox) comp;
					paymentMthdbox.setErrorMessage("");
					fillPaymentRefComboBox(paymentMthdbox, getComboboxValue(paymentMthdbox), this.feePaymentDetailList,
							"");
				}
			}
		}
	}

	public void doExecuteFeeCharges(boolean isSchdCal, FinScheduleData finScheduleData) {
		logger.debug("Entering");

		Clients.clearWrongValue(this.listBoxFeeDetail.getChildren());

		if (!isSchdCal && finScheduleData.getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
			return;
		}

		finScheduleData.setFinFeeDetailList(fetchFeeDetails(finScheduleData, false));

		if (StringUtils.isBlank(moduleDefiner)) {
			doSetFeeChanges(finScheduleData);
		}

		calculateFees(getFinFeeDetailList(), finScheduleData, null);

		doFillFinFeeDetailList(getFinFeeDetailList());

		if (StringUtils.isBlank(moduleDefiner)) {
			fetchFeeDetails(finScheduleData, true);
		}

		logger.debug("Leaving");
	}

	private void doSetFeeChanges(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		String feeEvent = PennantApplicationUtil.getEventCode(finScheduleData.getFinanceMain().getFinStartDate());

		if (!StringUtils.equals(finScheduleData.getFeeEvent(), feeEvent)) {
			List<FinTypeFees> finTypeFeesList = getFinanceDetailService().getFinTypeFees(
					finScheduleData.getFinanceMain().getFinType(), feeEvent, true, FinanceConstants.MODULEID_FINTYPE);

			getFinanceDetail().setFinTypeFeesList(finTypeFeesList);

			Map<String, FinFeeDetail> feeDetailMap = new HashMap<String, FinFeeDetail>();

			for (FinFeeDetail finFeeDetail : getFinFeeDetailUpdateList()) {
				if (!finFeeDetail.isNewRecord()) {
					if (!finFeeDetail.isRcdVisible()
							&& StringUtils.equals(finFeeDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
						finFeeDetail.setRcdVisible(true);
						finFeeDetail.setDataModified(true);
						finFeeDetail.setNewRecord(false);
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
					} else {
						finFeeDetail.setVersion(finFeeDetail.getVersion() + 1);
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						finFeeDetail.setRcdVisible(false);
						finFeeDetail.setDataModified(true);
						feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
					}
				}
			}

			String branchCode = getUserWorkspace().getLoggedInUser().getBranchCode();
			List<FinFeeDetail> finFeeDetailListNew = finFeeDetailService.convertToFinanceFees(financeDetail,
					branchCode);
			for (FinFeeDetail finFeeDetail : finFeeDetailListNew) {
				if (!feeDetailMap.containsKey(getUniqueID(finFeeDetail))) {
					feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
				}
			}

			setFinFeeDetailList(new ArrayList<FinFeeDetail>(feeDetailMap.values()));
		}
		finScheduleData.setFeeEvent(feeEvent);

		logger.debug("Leaving");
	}

	private List<FinFeeDetail> calculateFees(List<FinFeeDetail> fees, FinScheduleData schdData, Date valueDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();
		// Calculate Fee Rules
		calculateFeeRules(fees, schdData);

		// Calculate the fee Percentage
		calculateFeePercentageAmount(schdData, valueDate);

		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;
		// Calculating GST
		Date appDate = SysParamUtil.getAppDate();

		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			if (subventionFeeCode.equals(finFeeDetail.getFeeTypeCode())) {
				this.finFeeDetailService.calculateFees(finFeeDetail, schdData, getDealerTaxPercentages());
			} else {
				this.finFeeDetailService.calculateFees(finFeeDetail, schdData, getTaxPercentages());
			}

			if (finFeeDetail.isOriginationFee()) {
				finFeeDetail.setValueDate(fm.getFinStartDate());
			} else {
				finFeeDetail.setValueDate(appDate);
			}
		}

		getCalculatedTdsOnFees(getFinFeeDetailList(), financeDetail.getFinScheduleData());

		feeDetailService.setFeeAmount(getFinanceDetail().getModuleDefiner(), schdData, getFinFeeDetailList());

		doFillFinFeeDetailList(getFinFeeDetailUpdateList());

		schdData.setFinFeeDetailList(getFinFeeDetailList());

		logger.debug(Literal.LEAVING);

		return fees;
	}

	private void calculateFeeRules(List<FinFeeDetail> fees, FinScheduleData scheduleData) {
		List<String> ruleCodes = new ArrayList<>();
		for (FinFeeDetail fee : fees) {
			if (StringUtils.isNotEmpty(fee.getRuleCode())) {
				ruleCodes.add(fee.getRuleCode());
			}
		}

		if (ruleCodes.isEmpty()) {
			return;
		}

		List<Rule> rules = this.ruleService.getRuleDetailList(ruleCodes, RuleConstants.MODULE_FEES,
				scheduleData.getFeeEvent());

		if (CollectionUtils.isEmpty(rules)) {
			return;
		}

		Map<String, Object> executionMap = new HashMap<>();
		Map<String, String> ruleSqlMap = new HashMap<>();
		List<Object> objectList = new ArrayList<>();

		if (getFinanceDetail().getCustomerDetails() != null) {
			objectList.add(getFinanceDetail().getCustomerDetails().getCustomer());
			if (getFinanceDetail().getCustomerDetails().getCustEmployeeDetail() != null) {
				objectList.add(getFinanceDetail().getCustomerDetails().getCustEmployeeDetail());
			}
			List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
			if (CollectionUtils.isNotEmpty(addressList)) {
				for (CustomerAddres customerAddres : addressList) {
					if (customerAddres.getCustAddrPriority() == Integer
							.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						executionMap.put("customerProvince", customerAddres.getCustAddrProvince());
						break;
					}
				}
			} else {
				executionMap.put("customerProvince", "");
			}
		}

		if (getFinanceDetail().getFinScheduleData() != null) {
			objectList.add(financeMain);
			objectList.add(getFinanceDetail().getFinScheduleData().getFinanceType());
		}

		for (Rule feeRule : rules) {
			if (feeRule.getFields() != null) {
				String[] fields = feeRule.getFields().split(",");
				for (String field : fields) {
					if (!executionMap.containsKey(field)) {
						RuleExecutionUtil.setExecutionMap(field, objectList, executionMap);
					}
				}
			}
			ruleSqlMap.put(feeRule.getRuleCode(), feeRule.getSQLRule());
		}

		String finCcy = null;
		int formatter = 0;
		String finReference = null;

		if (financeMain != null) {
			finCcy = financeMain.getFinCcy();
			formatter = CurrencyUtil.getFormat(finCcy);

			if (financeMain.getFinStartDate() != null) {
				int finAge = DateUtil.getMonthsBetween(SysParamUtil.getAppDate(), financeMain.getFinStartDate());
				executionMap.put("finAgetilldate", finAge);
			}

			finReference = financeMain.getFinReference();
			long finID = financeMain.getFinID();
			if (StringUtils.isNotBlank(finReference) && StringUtils.isNotBlank(moduleDefiner)) {
				FinanceProfitDetail finProfitDetail = financeDetailService.getFinProfitDetailsById(finID);
				if (finProfitDetail != null) {
					BigDecimal outStandingFeeBal = this.financeDetailService.getOutStandingBalFromFees(finID);
					executionMap.put("totalOutStanding", finProfitDetail.getTotalPftBal());
					executionMap.put("principalOutStanding", finProfitDetail.getTotalPriBal());
					executionMap.put("principalSchdOutstanding",
							finProfitDetail.getTotalpriSchd().subtract(finProfitDetail.getTdSchdPri()));
					executionMap.put("totOSExcludeFees",
							finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()));
					executionMap.put("totOSIncludeFees", finProfitDetail.getTotalPftBal()
							.add(finProfitDetail.getTotalPriBal()).add(outStandingFeeBal));
					executionMap.put("unearnedAmount", finProfitDetail.getUnearned());
				}
			}

			executionMap.putAll(financeMain.getDeclaredFieldValues());
		}

		Map<String, BigDecimal> taxPercentages = getTaxPercentages();
		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;

		for (FinFeeDetail fee : getFinFeeDetailList()) {
			if (StringUtils.isEmpty(fee.getRuleCode())) {
				continue;
			}

			BigDecimal feeResult = this.finFeeDetailService.getFeeResult(ruleSqlMap.get(fee.getRuleCode()),
					executionMap, finCcy);
			// unFormating feeResult
			feeResult = PennantApplicationUtil.unFormateAmount(feeResult, formatter);

			fee.setCalculatedAmount(feeResult);

			if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent())) {
				feeResult = feeResult.subtract(fee.getNetTDS());
			}
			if (fee.isTaxApplicable()) {
				if (StringUtils.equals(subventionFeeCode, fee.getFeeTypeCode())) {
					this.finFeeDetailService.processGSTCalForRule(fee, feeResult, financeDetail,
							getDealerTaxPercentages(), false);
				} else {
					this.finFeeDetailService.processGSTCalForRule(fee, feeResult, financeDetail, taxPercentages, false);
				}
			} else {
				if (!fee.isFeeModified() || !fee.isAlwModifyFee()) {
					fee.setActualAmountOriginal(feeResult);
					fee.setActualAmountGST(BigDecimal.ZERO);
					fee.setActualAmount(feeResult);
				}

				fee.setRemainingFee(
						fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
			}
		}

	}

	private String getUniqueID(FinFeeDetail finFeeDetail) {
		return StringUtils.trimToEmpty(finFeeDetail.getFinEvent()) + "_" + String.valueOf(finFeeDetail.getFeeTypeID());
	}

	private void calculateFeePercentageAmount(FinScheduleData finScheduleData, Date valueDate) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(getFinFeeDetailList())) {
			logger.debug(Literal.LEAVING);
			return;
		}

		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;

		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			if (PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE.equals(finFeeDetail.getCalculationType())) {
				BigDecimal calPercentageFee = getCalculatedPercentageFee(finFeeDetail, finScheduleData, valueDate);
				finFeeDetail.setCalculatedAmount(calPercentageFee);

				if (CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(finFeeDetail.getFeeScheduleMethod())) {
					finFeeDetail.setWaivedAmount(calPercentageFee);
				}

				if (finFeeDetail.isTaxApplicable()) { // if GST applicable
					if (StringUtils.equals(subventionFeeCode, finFeeDetail.getFeeTypeCode())) {
						this.finFeeDetailService.processGSTCalForPercentage(finFeeDetail, calPercentageFee,
								financeDetail, getDealerTaxPercentages(), false);
					} else {
						this.finFeeDetailService.processGSTCalForPercentage(finFeeDetail, calPercentageFee,
								financeDetail, getTaxPercentages(), false);
					}
				} else {
					if (!finFeeDetail.isFeeModified() || !finFeeDetail.isAlwModifyFee()) {
						finFeeDetail.setActualAmountOriginal(calPercentageFee);
						finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
						finFeeDetail.setActualAmount(calPercentageFee);
					}
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
							.subtract(finFeeDetail.getWaivedAmount()));
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private BigDecimal getCalculatedPercentageFee(FinFeeDetail finFeeDetail, FinScheduleData finScheduleData,
			Date valueDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finScheduleData.getFinanceMain();

		BigDecimal calculatedAmt = BigDecimal.ZERO;

		switch (finFeeDetail.getCalculateOn()) {
		case PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
			calculatedAmt = fm.getFinAssetValue();
			break;
		case PennantConstants.FEE_CALCULATEDON_LOANAMOUNT:
			calculatedAmt = fm.getFinAmount().subtract(fm.getDownPayment());
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL:
			calculatedAmt = fm.getFinCurrAssetValue().add(fm.getFeeChargeAmt()).subtract(fm.getFinRepaymentAmount());
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDPRINCIFUTURE:
			if (valueDate == null) {
				valueDate = SysParamUtil.getAppDate();
			}

			List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();

			for (FinanceScheduleDetail schd : schdList) {
				if (DateUtil.compare(valueDate, schd.getSchDate()) == 0) {
					calculatedAmt = schd.getClosingBalance();
					if (calculatedAmt.compareTo(BigDecimal.ZERO) == 0) {
						List<FinanceScheduleDetail> apdSchdList = getFinanceDetailService()
								.getFinScheduleList(finScheduleData.getFinanceMain().getFinID());
						for (FinanceScheduleDetail curSchd : apdSchdList) {
							if (DateUtil.compare(valueDate, curSchd.getSchDate()) == 0) {
								calculatedAmt = curSchd.getClosingBalance();
							}
							if (DateUtil.compare(valueDate, curSchd.getSchDate()) <= 0) {
								break;
							}
							calculatedAmt = curSchd.getClosingBalance();
						}
						break;
					}
				}
				if (DateUtil.compare(valueDate, schd.getSchDate()) <= 0) {
					break;
				}
				calculatedAmt = schd.getClosingBalance();
			}
			break;
		case PennantConstants.FEE_CALCULATEDON_PAYAMOUNT:
			try {
				ReceiptDialogCtrl rec = (ReceiptDialogCtrl) getFinanceMainDialogCtrl();
				// FIXME: PV. CODE RELATED TO RECEIPT DAILOG CTRL DELETED FROM HERE
				// calculatedAmt = rec.getTotalReceiptAmount(false);
				// calculatedAmt = (BigDecimal) getFinanceMainDialogCtrl().getClass().getMethod("getTotalReceiptAmount",
				// Boolean.class).invoke(getFinanceMainDialogCtrl(), false);
			} catch (Exception e) {
				logger.info(e);
			}
			break;
		default:
			break;
		}

		calculatedAmt = calculatedAmt.multiply(finFeeDetail.getPercentage()).divide(BigDecimal.valueOf(100), 2,
				RoundingMode.HALF_DOWN);
		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, fm.getCalRoundingMode(), fm.getRoundingTarget());
		logger.debug("Leaving");

		return calculatedAmt;
	}

	/**
	 * Method for Fetching Customer Data to Calculate
	 * 
	 * @return
	 */
	public AEAmountCodes getCustomerData(AEAmountCodes amountCodes) {
		logger.debug("Entering");

		try {
			amountCodes = (AEAmountCodes) getFinanceMainDialogCtrl().getClass()
					.getMethod("doGetFeeCustomerData", AEAmountCodes.class)
					.invoke(getFinanceMainDialogCtrl(), amountCodes);
			logger.debug("Leaving");
			return amountCodes;
		} catch (Exception e) {
			logger.info(e);
		}

		logger.debug("Leaving");

		return amountCodes;
	}

	private Component getComponent(String id, FinFeeDetail fee) {
		id = getComponentId(id, fee);
		return this.listBoxFeeDetail.getFellowIfAny(id);
	}

	private Decimalbox getDecimalbox(String id, FinFeeDetail fee) {
		Component component = getComponent(id, fee);

		if (component != null) {
			return (Decimalbox) component;
		}

		return null;
	}

	private Combobox getCombobox(String id, FinFeeDetail fee) {
		Component component = getComponent(id, fee);

		if (component != null) {
			return (Combobox) component;
		}

		return null;
	}

	private Intbox getIntBox(String id, FinFeeDetail fee) {
		Component component = getComponent(id, fee);

		if (component != null) {
			return (Intbox) component;
		}

		return null;
	}

	private void doClearFeeWrongValueExceptions() {

		if (CollectionUtils.isEmpty(this.finFeeDetailList)) {
			return;
		}

		for (FinFeeDetail fee : this.finFeeDetailList) {
			if (!fee.isRcdVisible() || (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())
					&& PennantConstants.RECORD_TYPE_CAN.equals(fee.getRecordType()))) {
				continue;
			}

			Decimalbox calbox = getDecimalbox(FEE_UNIQUEID_CALCULATEDAMOUNT, fee);
			if (calbox != null) {
				calbox.setErrorMessage("");
			}

			Decimalbox waivedbox = getDecimalbox(FEE_UNIQUEID_WAIVEDAMOUNT, fee);
			if (waivedbox != null) {
				waivedbox.setErrorMessage("");
			}

			Decimalbox paidbox = getDecimalbox(FEE_UNIQUEID_PAID_AMOUNT, fee);
			if (paidbox != null) {
				paidbox.setErrorMessage("");
			}

			Decimalbox remFeeBox = getDecimalbox(FEE_UNIQUEID_REMAINING_FEE, fee);
			if (remFeeBox != null) {
				remFeeBox.setErrorMessage("");
			}

			Combobox payMethodBox = getCombobox(FEE_UNIQUEID_PAYMENTMETHOD, fee);
			if (payMethodBox != null) {
				payMethodBox.setErrorMessage("");
			}

			Combobox feeSchdBox = getCombobox(FEE_UNIQUEID_FEESCHEDULEMETHOD, fee);
			if (feeSchdBox != null) {
				feeSchdBox.setErrorMessage("");
			}

			Intbox termbox = getIntBox(FEE_UNIQUEID_TERMS, fee);
			if (termbox != null) {
				termbox.setErrorMessage("");
			}
		}

	}

	// tds on fees
	private void getCalculatedTdsOnFees(List<FinFeeDetail> finFeeDetailsList, FinScheduleData finScheduleData) {

		for (FinFeeDetail feeDetails : finFeeDetailsList) {
			resetTDSonFee(feeDetails, finScheduleData.getFinanceMain());
		}
	}

	// tds on fees
	private void resetTDSonFee(FinFeeDetail feeDetails, FinanceMain finMain) {

		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		BigDecimal tdsNetFee = BigDecimal.ZERO;
		BigDecimal tdsPaidFee = BigDecimal.ZERO;

		FeeType fee = feeTypeService.getFeeTypeById(feeDetails.getFeeTypeID());

		if (fee == null || !TDSCalculator.isTDSApplicable(finMain, fee)) {
			feeDetails.setNetAmount(feeDetails.getNetAmount().add(feeDetails.getNetTDS()));
			feeDetails.setRemainingFee(feeDetails.getRemainingFee().add(feeDetails.getRemTDS()));
			feeDetails.setNetTDS(tdsNetFee);
			feeDetails.setPaidTDS(tdsPaidFee);
			feeDetails.setRemTDS(tdsNetFee.subtract(tdsPaidFee));
			return;
		}

		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

		tdsNetFee = (feeDetails.getNetAmountOriginal().multiply(tdsPerc)).divide(new BigDecimal(100), 0,
				RoundingMode.HALF_DOWN);
		tdsNetFee = CalculationUtil.roundAmount(tdsNetFee, tdsRoundMode, tdsRoundingTarget);

		tdsPaidFee = (feeDetails.getPaidAmount().multiply(tdsPerc)).divide(new BigDecimal(100), 0,
				RoundingMode.HALF_DOWN);
		tdsPaidFee = CalculationUtil.roundAmount(tdsPaidFee, tdsRoundMode, tdsRoundingTarget);
		if (feeDetails.getPaidAmount().compareTo(feeDetails.getNetAmount()) == 0) {
			BigDecimal diffTDS = tdsNetFee.subtract(tdsPaidFee);
			tdsPaidFee = tdsPaidFee.add(diffTDS);
		}
		feeDetails.setPaidAmountOriginal(
				feeDetails.getPaidAmount().subtract(feeDetails.getPaidAmountGST()).add(tdsPaidFee));

		// Remaining Fee Calculation
		feeDetails.setRemainingFeeOriginal(
				feeDetails.getNetAmountOriginal().subtract(feeDetails.getPaidAmountOriginal()));
		feeDetails.setRemainingFeeGST(feeDetails.getNetAmountGST().subtract(feeDetails.getPaidAmountGST()));

		feeDetails.setNetTDS(tdsNetFee);
		feeDetails.setPaidTDS(tdsPaidFee);
		feeDetails.setRemTDS(tdsNetFee.subtract(tdsPaidFee));

		feeDetails.setNetAmount(
				feeDetails.getNetAmountOriginal().add(feeDetails.getNetAmountGST()).subtract(feeDetails.getNetTDS()));
		feeDetails.setRemainingFee(feeDetails.getRemainingFeeOriginal().add(feeDetails.getRemainingFeeGST())
				.subtract(feeDetails.getRemTDS()));
		feeDetails.setPaidAmount(feeDetails.getPaidAmountOriginal().add(feeDetails.getPaidAmountGST())
				.subtract(feeDetails.getPaidTDS()));

	}

	/**
	 * @param financeMain
	 * @param customer
	 * @param financeType
	 * @return
	 */
	private Map<String, Object> getDataMap(FinanceMain financeMain, Customer customer, FinanceType financeType) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (financeMain != null) {
			dataMap.putAll(financeMain.getDeclaredFieldValues());
		}
		if (customer != null) {
			dataMap.putAll(customer.getDeclaredFieldValues());
		}
		if (financeType != null) {
			dataMap.putAll(financeType.getDeclaredFieldValues());
		}
		return dataMap;
	}

	public void doReSetDataChanged() {
		this.dataChanged = false;
	}

	public boolean isDataChanged() {
		return dataChanged;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	private Map<String, BigDecimal> getTaxPercentages() {
		String userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();
		String finBranch = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinBranch();
		String finCCY = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();

		long custId = 0;
		if (financeDetail.getCustomerDetails() != null) {
			custId = financeDetail.getCustomerDetails().getCustomer().getCustID();

		}

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(custId, finCCY, userBranch, finBranch,
				financeDetail.getFinanceTaxDetail());

		return taxPercentages;
	}

	private void caluclateFeeGST(List<FinFeeDetail> fees, FinScheduleData fsd) {
		FinanceMain finMain = fsd.getFinanceMain();
		String userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();
		String finBranch = finMain.getFinBranch();
		String finCCY = finMain.getFinCcy();

		CustomerDetails custDetails = financeDetail.getCustomerDetails();

		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;

		for (FinFeeDetail finFeeDetail : fees) {

			if (subventionFeeCode.equals(finFeeDetail.getFeeTypeCode())) {
				this.finFeeDetailService.calculateFees(finFeeDetail, fsd, getDealerTaxPercentages());
			} else {
				if (custDetails == null) {
					continue;
				}
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(custDetails, finCCY,
						userBranch, finBranch, financeDetail.getFinanceTaxDetail());
				this.finFeeDetailService.calculateFees(finFeeDetail, fsd, taxPercentages);
			}
		}

	}

	private Map<String, BigDecimal> getDealerTaxPercentages() {

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		String userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();
		String finBranch = financeMain.getFinBranch();
		String finCCY = financeMain.getFinCcy();

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getDealerTaxPercentages(
				financeMain.getManufacturerDealerId(), finCCY, userBranch, finBranch,
				financeDetail.getFinanceTaxDetail());

		return taxPercentages;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public List<FinFeeDetail> getFinFeeDetailUpdateList() {
		return this.finFeeDetailList;
	}

	public List<FinFeeDetail> getFinFeeDetailList() {
		List<FinFeeDetail> finFeeDetailTemp = new ArrayList<FinFeeDetail>();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (finFeeDetail.isRcdVisible()) {
				finFeeDetailTemp.add(finFeeDetail);
			}
		}
		return finFeeDetailTemp;
	}

	public void setFinFeeDetailList(List<FinFeeDetail> finFeeDetailList) {
		this.finFeeDetailList = finFeeDetailList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinanceMain(financeDetail.getFinScheduleData().getFinanceMain());
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<FeePaymentDetail> getFeePaymentDetailList() {
		return feePaymentDetailList;
	}

	public void setFeePaymentDetailList(List<FeePaymentDetail> feePaymentDetailList) {
		this.feePaymentDetailList = feePaymentDetailList;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public Map<String, FeeRule> getFeeRuleDetailsMap() {
		return feeRuleDetailsMap;
	}

	public void setFeeRuleDetailsMap(Map<String, FeeRule> feeRuleDetailsMap) {
		this.feeRuleDetailsMap = feeRuleDetailsMap;
	}

	public boolean isReceiptsProcess() {
		return isReceiptsProcess;
	}

	public void setReceiptsProcess(boolean isReceiptsProcess) {
		this.isReceiptsProcess = isReceiptsProcess;
	}

	public FeeTypeService getFeeTypeService() {
		return feeTypeService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	@Autowired
	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

}
