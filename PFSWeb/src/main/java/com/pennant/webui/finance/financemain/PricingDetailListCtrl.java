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
 *******************************************************************************************************
 * FILE HEADER *
 *******************************************************************************************************
 *
 * FileName : PricingDetailListCtrl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 11-07-2011 *
 * 
 * Modified Date : 11-07-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************************
 * 11-07-2018 Pennant 0.1 * * 11-07-2018 Satya 0.2 PSD - Ticket : 127846 * Changes related to Fees calculation for the *
 * selection type DropLinePOS. * * * * * * * * * *
 ********************************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PricingDetail;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.PricingDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/PricingDetailList.zul file.
 */
public class PricingDetailListCtrl extends GFCBaseCtrl<PricingDetail> {
	private static final Logger logger = LogManager.getLogger(PricingDetailListCtrl.class);
	private static final long serialVersionUID = 4157448822555239535L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PricingDetailList;

	protected Listbox listBoxPaymentDetails;

	// For Dynamically calling of this Controller
	private FinanceDetail financeDetail;
	private Object financeMainDialogCtrl;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxPricingDetail;
	protected Listbox listBoxChargesDetail;
	protected Listbox listBoxVasDetail;
	protected Listbox listBoxAdditionalDetail;
	public Hbox hbox_Split;
	private Component parent = null;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private String roleCode = "";
	public Checkbox split;
	private Button btnAdd_AddPricingTopUp;
	private Button btnNew_Insurance;
	private Button btnRemove_RemovePricingTopUp;
	private int topUpCount = 0;
	protected Map<String, FinanceType> topUpFinType = new HashMap<>();

	private int finFormatter = PennantConstants.defaultCCYDecPos;
	private PricingDetail pricingDetail;
	public BigDecimal loanAmount_TopUp_0;
	protected Textbox remarks;
	private CreditReviewData creditReviewData;

	Map<String, String> finReferenceMap = new HashMap<String, String>();
	Map<String, List<FinFeeDetail>> finFeeDetailMap = new HashMap<String, List<FinFeeDetail>>();
	Map<String, List<VASRecording>> vasRecordingMap = new HashMap<String, List<VASRecording>>();

	Tab parenttab = null;
	private PricingDetailService pricingDetailService;
	private boolean isNewFinance = false;
	private static final String BOOLEAN_TRUE = "1";
	private BigDecimal finAssetValue = BigDecimal.ZERO;

	@Autowired
	private FinFeeDetailService finFeeDetailService;
	@Autowired
	private FinanceDetailService financeDetailService;

	boolean splitted = false;
	boolean partiallySplitted = false;
	int noofTopUps;
	FinFeeDetailListCtrl finFeeDetailListCtrl = null;

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

	public static final String FEE_UNIQUEID_PAYMENTMETHOD = "PAYMETHD";
	public static final String FEE_UNIQUEID_FEESCHEDULEMETHOD = "FEEMTHD";
	public static final String FEE_UNIQUEID_TERMS = "TERMS";
	public static final String FEE_UNIQUEID_ADJUST = "ADJUST";
	public static final String FEE_UNIQUEID_GSTDETAILS = "ADJUST";

	private BigDecimal roi;

	/**
	 * default constructor.<br>
	 */
	public PricingDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PricingDetailListCtrl";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinAdvancePayment object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_PricingDetailList(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PricingDetailList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			this.pricingDetail = (PricingDetail) arguments.get("pricingDetail");

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
				this.window_PricingDetailList.setTitle("");
				setNewFinance(true);
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "PricingDetailListCtrl");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (arguments.containsKey("repayRate")) {
				this.roi = (BigDecimal) arguments.get("repayRate");
			}

			if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
				finFeeDetailListCtrl = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getFinFeeDetailListCtrl();
			}

			if (arguments.containsKey("tab")) {
				parenttab = (Tab) arguments.get("tab");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setPricingDetailListCtrl", this.getClass())
							.invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_PricingDetailList.setTitle("");
			}

			if (arguments.containsKey("CreditReviewData")) {
				setCreditReviewData((CreditReviewData) arguments.get("CreditReviewData"));
			}

			if (arguments.containsKey("Role")) {
				this.role = (String) arguments.get("Role");
			}

			FinScheduleData schdData = financeDetail.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();
			List<Long> finIdList = pricingDetailService.getInvestmentRefifAny(fm.getFinReference(), "_Temp");
			if (CollectionUtils.isNotEmpty(finIdList)) {
				partiallySplitted = true;
				noofTopUps = finIdList.size();
			}

			if (CollectionUtils.isEmpty(finIdList)) {
				finIdList = pricingDetailService.getParentRefifAny(fm.getFinReference(), "_Temp");
				if (CollectionUtils.isNotEmpty(finIdList)) {
					partiallySplitted = true;
					splitted = true;
				}

			}

			List<FinanceMain> tempList = new ArrayList<FinanceMain>();
			List<FinFeeDetail> tempFeeList = new ArrayList<FinFeeDetail>();
			List<VASRecording> tempVASList = new ArrayList<VASRecording>();

			if (CollectionUtils.isNotEmpty(finIdList)) {
				PricingDetail detail = new PricingDetail();

				for (Long finID : finIdList) {
					FinanceMain finMain = pricingDetailService.getFinanceMain(finID, "_TView");
					tempList.add(finMain);

					String finReference = finMain.getFinReference();

					if (!topUpFinType.containsKey(finMain.getFinType())) {
						topUpFinType.put(finMain.getFinType(),
								pricingDetailService.getFinanceTypeById(finMain.getFinType()));
					}

					finAssetValue = finAssetValue.add(finMain.getFinAssetValue());

					detail.getFinanceMains().add(finMain);
					List<FinFeeDetail> finFeeDetailList = pricingDetailService.getFinFeeDetailById(finID, false,
							"_View");
					finFeeDetailMap.put(finReference, finFeeDetailList);

					tempFeeList.addAll(finFeeDetailList);
					detail.setTopUpFinFeeDetails(finFeeDetailList);
					pricingDetail.setTopUpFinFeeDetails(finFeeDetailList);

					List<VASRecording> vasList = pricingDetailService.getVASRecordingsByLinkRef(finReference, "_View");
					vasRecordingMap.put(finReference, vasList);
					tempVASList.addAll(vasList);
					pricingDetail.setTopUpVasDetails(vasList);

				}
				detail.setFinanceMains(tempList);
				detail.setTopUpFinFeeDetails(tempFeeList);
				detail.setTopUpVasDetails(tempVASList);
				getFinanceDetail().setPricingDetail(detail);
			}
			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			// Bugfix: After loan split we have to display only parent loan amount as FinAssetValue
			if (financeMain.isLoanSplitted()) {
				setFinAssetValue(financeMain.getFinAssetValue());
			} else {
				setFinAssetValue(finAssetValue.add(financeMain.getFinAssetValue()));
			}
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getFinanceDetail());
			// configuredRole =
			// StringUtils.trimToEmpty(SysParamUtil.getValueAsString(SMTParameterConstants.BRANCH_OPS_ROLE));
		} catch (Exception e) {
			MessageUtil.showError(e);
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public boolean doSave(FinanceDetail aFinanceDetail, Tab pricingTab, boolean recSave) {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		pricingDetail.getActualFinFeeDetails().clear();
		pricingDetail.getActualVasDetails().clear();
		pricingDetail.getFinanceMains().clear();

		if (!isValidAmount(aFinanceDetail)) {
			MessageUtil.showError(Labels.getLabel("label_PricingDetail_TotalAmount.value"));
			return false;
		}

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(pricingDetail, recSave);

		if (!wve.isEmpty() && pricingTab != null) {
			pricingTab.setSelected(true);
		}

		showErrorDetails(wve);
		if (!aFinanceDetail.isActionSave() && (!isReadOnly("PricingDetailListCtrl_Add_Pricing")
				|| !isReadOnly("PricingDetailListCtrl_Split_Check"))) {
			if (!splitted && pricingDetail != null && !validateTotalFeeAmount()) {
				MessageUtil.showError(Labels.getLabel("label_FeeAmount_Validation.Value"));
				return false;
			}

		}

		aFinanceDetail.setPricingDetail(pricingDetail);
		if (!aFinanceDetail.isActionSave() && this.split.isChecked()) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLoanSplitted(true);
		}

		if (CollectionUtils.isNotEmpty(aFinanceDetail.getFinFeeDetails())) {
			aFinanceDetail.getFinFeeDetails().clear();
		}

		FinanceMain fm = getFinanceDetail().getFinScheduleData().getFinanceMain();

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);

		List<FinFeeDetail> templist = pricingDetail.getActualFinFeeDetails();
		for (FinFeeDetail finFeeDetail : aFinanceDetail.getFinScheduleData().getFinFeeDetailList()) {
			if (finFeeDetail.getVasReference() != null) {
				templist.add(finFeeDetail);
			}
		}

		if (CollectionUtils.isNotEmpty(templist)) {
			for (FinFeeDetail finFeeDetail : templist) {
				this.finFeeDetailService.calculateFees(finFeeDetail, fm, taxPercentages);
			}
		}

		aFinanceDetail.getFinScheduleData().setFinFeeDetailList(templist);

		if (CollectionUtils.isNotEmpty(aFinanceDetail.getFinScheduleData().getVasRecordingList())) {
			aFinanceDetail.getFinScheduleData().getVasRecordingList().clear();
		}

		if (CollectionUtils.isNotEmpty(pricingDetail.getTopUpFinFeeDetails())) {
			for (FinFeeDetail finFeeDetail : pricingDetail.getTopUpFinFeeDetails()) {
				this.finFeeDetailService.calculateFees(finFeeDetail, fm, taxPercentages);
			}
		}

		aFinanceDetail.getFinScheduleData().setVasRecordingList(pricingDetail.getActualVasDetails());
		logger.debug(Literal.LEAVING);
		closeDialog();
		return true;
	}

	private boolean validateTotalFeeAmount() {
		logger.debug(Literal.ENTERING);

		BigDecimal totalFeeAmount = BigDecimal.ZERO;

		for (FinFeeDetail feeDetail : pricingDetail.getActualFinFeeDetails()) {
			if (StringUtils.equals(CalculationConstants.REMFEE_PART_OF_DISBURSE, feeDetail.getFeeScheduleMethod())) {
				totalFeeAmount = totalFeeAmount.add(feeDetail.getActualAmount());
			}
		}

		for (VASRecording vasDetail : pricingDetail.getActualVasDetails()) {
			if (StringUtils.equals(CalculationConstants.REMFEE_PART_OF_DISBURSE, vasDetail.getFeePaymentMode())) {
				totalFeeAmount = totalFeeAmount.add(vasDetail.getFee());
			}
		}

		if (pricingDetail.getFinanceMain().getFinAssetValue().compareTo(totalFeeAmount) <= 0) {
			return false;
		}

		for (FinanceMain financeMain : pricingDetail.getFinanceMains()) {
			String topUpFinRef = financeMain.getFinReference();
			BigDecimal totalTopUpFeeAmount = BigDecimal.ZERO;
			for (FinFeeDetail feeDetail : pricingDetail.getTopUpFinFeeDetails()) {
				if (StringUtils.equals(topUpFinRef, feeDetail.getFinReference()) && StringUtils
						.equals(CalculationConstants.REMFEE_PART_OF_DISBURSE, feeDetail.getFeeScheduleMethod())) {
					totalTopUpFeeAmount = totalTopUpFeeAmount.add(feeDetail.getActualAmount());
				}
			}

			for (VASRecording vasRecording : pricingDetail.getTopUpVasDetails()) {
				if (StringUtils.equals(topUpFinRef, vasRecording.getPrimaryLinkRef()) && StringUtils
						.equals(CalculationConstants.REMFEE_PART_OF_DISBURSE, vasRecording.getFeePaymentMode())) {
					totalTopUpFeeAmount = totalTopUpFeeAmount.add(vasRecording.getFee());
				}
			}

			if (financeMain.getFinAssetValue().compareTo(totalTopUpFeeAmount) <= 0) {
				return false;
			}

		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		// doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parenttab != null) {
				parenttab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isValidAmount(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		CurrencyBox parentLoanAmtBox = (CurrencyBox) listBoxPricingDetail.getFellowIfAny("LoanAmount_TopUp_0");
		BigDecimal parentLoanAmt = getBigDecimalValue(parentLoanAmtBox);

		BigDecimal topUpLoan0Amt = BigDecimal.ZERO;
		BigDecimal topUpLoan1Amt = BigDecimal.ZERO;

		if (listBoxPricingDetail.getFellowIfAny("LoanAmount_TopUp_1") != null) {
			CurrencyBox toupLoanAmt0Box = (CurrencyBox) listBoxPricingDetail.getFellowIfAny("LoanAmount_TopUp_1");
			topUpLoan0Amt = getBigDecimalValue(toupLoanAmt0Box);
		}

		if (listBoxPricingDetail.getFellowIfAny("LoanAmount_TopUp_2") != null) {
			CurrencyBox toupLoanAmt1Box = (CurrencyBox) listBoxPricingDetail.getFellowIfAny("LoanAmount_TopUp_2");
			topUpLoan1Amt = getBigDecimalValue(toupLoanAmt1Box);
		}
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		BigDecimal totalAmt = BigDecimal.ZERO;
		// Bugfix: After loan split we have to display only parent loan amount as FinAssetValue
		if (financeMain.isLoanSplitted()) {
			totalAmt = parentLoanAmt;
		} else {
			totalAmt = parentLoanAmt.add(topUpLoan0Amt).add(topUpLoan1Amt);
		}

		if (totalAmt.compareTo(financeMain.getFinAssetValue()) != 0) {
			return false;
		}

		logger.debug(Literal.LEAVING);

		return true;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		if (parent != null) {
			this.window_PricingDetailList.setHeight(borderLayoutHeight - 75 + "px");
			parent.appendChild(this.window_PricingDetailList);
		}
		btnAdd_AddPricingTopUp.setVisible(true);
		btnRemove_RemovePricingTopUp.setVisible(true);
		hbox_Split.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, roleCode);
		String loanCategory = getFinanceDetail().getFinScheduleData().getFinanceMain().getLoanCategory();

		if ((FinanceConstants.LOAN_CATEGORY_BT.equals(loanCategory)
				|| FinanceConstants.LOAN_CATEGORY_FP.equals(loanCategory))) {
			this.hbox_Split.setVisible(true);
		}
		if (isReadonly()) {
			readOnlyComponent(true, this.btnAdd_AddPricingTopUp);
			readOnlyComponent(true, this.btnNew_Insurance);
			readOnlyComponent(true, this.btnRemove_RemovePricingTopUp);
			readOnlyComponent(true, this.split);
		} else {
			readOnlyComponent(isReadOnly("PricingDetailListCtrl_Add_Pricing"), this.btnAdd_AddPricingTopUp);
			readOnlyComponent(isReadOnly("PricingDetailListCtrl_Add_Insurance"), this.btnNew_Insurance);
			readOnlyComponent(isReadOnly("PricingDetailListCtrl_Remove_Pricing"), this.btnRemove_RemovePricingTopUp);
			readOnlyComponent(isReadOnly("PricingDetailListCtrl_Split_Check"), this.split);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail financeDetail) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			appendFinBasicDetails();
			doWriteBeanToComponents(financeDetail);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public ArrayList<WrongValueException> doWriteComponentsToBean(PricingDetail pricingDetail, boolean saveAction) {
		logger.debug(Literal.ENTERING);

		FinanceMain parentFinMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		String topup_label = "";

		int count = topUpCount;

		FinanceMain finMain = null;

		List<FinanceMain> newFinMains = new ArrayList<FinanceMain>();
		List<FinanceMain> oldFinMains = new ArrayList<FinanceMain>();
		Map<String, FinanceMain> finReferences = new HashMap<>();

		for (Listitem listitem : listBoxPricingDetail.getItems()) {

			if (count == 0 && topUpCount != 0) {
				break;
			}

			for (int i = 0; i <= topUpCount; i++) {
				topup_label = "TopUp_" + i;

				CurrencyBox calBox = (CurrencyBox) listitem.getFellowIfAny("LoanAmount_" + topup_label);

				Clients.clearWrongValue(calBox);

				if (calBox.getActualValue().compareTo(BigDecimal.ZERO) <= 0 && !calBox.isReadonly()) {
					parenttab.setSelected(true);
					throw new WrongValueException(calBox,
							Labels.getLabel("const_const_NO_NEGATIVE_ZERO", new String[] { "Loan Amount" }));
				}

				finMain = (FinanceMain) calBox.getAttribute("finMain");
				int format = CurrencyUtil.getFormat(finMain.getFinCcy());
				String numberOnly = topup_label.replaceAll("[^0-9]", "");
				int topUpCount = Integer.valueOf(numberOnly);
				Checkbox checkbox = (Checkbox) listBoxPricingDetail.getItems().get(topUpCount)
						.getFellowIfAny("RM_" + topUpCount);
				if (checkbox == null || !checkbox.isChecked()) {

					if (i > 0) {
						finMain.setNewRecord((Boolean) calBox.getAttribute("newRecord"));
					}

					BigDecimal amt = getBigDecimalValue(calBox);
					finMain.setFinAssetValue(amt);

					try {
						if (finMain.getFinAssetValue() != null && finMain.getFinAmount() != null
								&& finMain.getFinAssetValue().compareTo(finMain.getFinAmount()) < 0) {
							throw new WrongValueException(calBox, Labels.getLabel("NUMBER_MINVALUE_EQ", new String[] {
									PennantApplicationUtil.amountFormate(finMain.getFinAssetValue(), format),
									String.valueOf(Labels.getLabel("label_FinanceMainDialog_FinAmount.value")) }));
						}
					} catch (WrongValueException e) {
						parenttab.setSelected(true);
						throw e;
					}

					Decimalbox rateBox = (Decimalbox) listitem.getFellowIfAny("ROI_" + topup_label);
					Clients.clearWrongValue(rateBox);
					rateBox.clearErrorMessage();
					String roi = getValueFromComponent(listitem.getFellowIfAny("ROI_" + topup_label));

					BigDecimal roiPerc = BigDecimal.ZERO;
					if (roi == null) {
						FinanceMainBaseCtrl ctrl = (FinanceMainBaseCtrl) financeMainDialogCtrl;
						roiPerc = ctrl.repayRate.getEffRateValue();
					} else {
						roiPerc = new BigDecimal(roi);
					}

					if (roiPerc == null || roiPerc.compareTo(BigDecimal.ZERO) == 0) {
						parenttab.setSelected(true);
						throw new WrongValueException(rateBox,
								Labels.getLabel("FIELD_IS_MAND", new String[] { "ROI %" }));
					}

					if (!this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
						if (roiPerc.compareTo(new BigDecimal(4)) < 0
								|| roiPerc.compareTo(new BigDecimal(99)) > 0 && (!rateBox.isReadonly())) {
							parenttab.setSelected(true);
							throw new WrongValueException(rateBox,
									Labels.getLabel("NUMBER_RANGE_EQ", new String[] { "ROI %", "4", "99" }));
						}
					}
					if (StringUtils.isEmpty(finMain.getRepayBaseRate())) {
						finMain.setRepayProfitRate(roiPerc);
					}

					Intbox tenure = (Intbox) listitem.getFellowIfAny("Tenure_" + topup_label);
					// Clients.clearWrongValue(tenure);
					// tenure.clearErrorMessage();
					try {
						if (!tenure.isReadonly() && tenure.intValue() <= 0) {
							parenttab.setSelected(true);
							throw new WrongValueException(tenure,
									Labels.getLabel("const_const_NO_NEGATIVE_ZERO", new String[] { "Tenure" }));
						}
					} catch (WrongValueException e) {
						parenttab.setSelected(true);
						throw e;
					}

					String compon = getValueFromComponent(tenure);
					int noOfTerms = Integer.valueOf(compon);
					FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
					int minTerms = finType.getFinMinTerm();
					int maxTerms = finType.getFinMaxTerm();
					if (noOfTerms < minTerms || noOfTerms > maxTerms) {
						parenttab.setSelected(true);
						throw new WrongValueException(tenure,
								Labels.getLabel("NUMBER_RANGE_EQ",
										new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
												String.valueOf(minTerms), String.valueOf(maxTerms) }));
					}
					finMain.setNumberOfTerms(noOfTerms);
					finMain.setMaturityDate(null);

					if (i > 0) {
						if (finMain.isNewRecord()) {
							finMain.setSwiftBranchCode(parentFinMain.getFinReference().substring(1, 4));
							finMain.setFinID(0);
							finMain.setFinReference(
									ReferenceGenerator.generateFinRef(finMain, topUpFinType.get(finMain.getFinType())));
						}

						if (!saveAction && !this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
							// Role based configured
							if ((this.hbox_Split.isVisible() && this.split.isChecked())
									|| (!finMain.isNewRecord() && StringUtils.isBlank(finMain.getInvestmentRef()))) {
								finMain.setInvestmentRef("");
								finMain.setParentRef(parentFinMain.getFinReference());
								finMain.setLoanSplitted(true);
							} else {
								finMain.setInvestmentRef(parentFinMain.getFinReference());
							}
						} else {
							finMain.setInvestmentRef(parentFinMain.getFinReference());
							this.split.setChecked(false);
							pricingDetail.setSplit(false);
						}

						finReferenceMap.put("Topup" + i, finMain.getFinReference());
					}

					boolean isNew = false;
					isNew = finMain.isNewRecord();
					String tranType = "";
					boolean recordAdded = false;
					if (finMain.isNewRecord()) {
						finMain.setVersion(1);
						finMain.setRecordType(PennantConstants.RCD_ADD);
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isBlank(finMain.getRecordType())) {
						finMain.setVersion(finMain.getVersion() + 1);
						finMain.setRecordType(PennantConstants.RCD_UPD);
					}

					if (finMain.getRecordType().equals(PennantConstants.RCD_ADD) && finMain.isNewRecord()) {
						tranType = PennantConstants.TRAN_ADD;
					} else if (finMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						tranType = PennantConstants.TRAN_UPD;
					} else {
						finMain.setVersion(finMain.getVersion() + 1);
						if (isNew) {
							tranType = PennantConstants.TRAN_ADD;
						} else {
							tranType = PennantConstants.TRAN_UPD;
						}
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (finMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							finMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
						} else if (finMain.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (finMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							finMain.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
						} else if (finMain.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							if (finMain.getRecordType().equals(PennantConstants.RCD_ADD)) {
								recordAdded = true;
							}
						}
					}

					if (i == 0) {
						pricingDetail.setFinanceMain(finMain);
						count--;
						continue;
					}

					if (!recordAdded) {
						oldFinMains.add(finMain);
					} else {
						newFinMains.add(finMain);
					}

					if (count != 0)
						count--;

				} else {
					finMain = (FinanceMain) calBox.getAttribute("finMain");
					if (!finMain.isNewRecord()) {
						if (oldFinMains.size() > 1) {
							for (FinanceMain financeMain2 : oldFinMains) {
								if (!finReferences.containsKey(financeMain2.getFinReference())) {
									oldFinMains.add(finMain);
								}
							}
						} else {
							finReferences.put(finMain.getFinReference(), finMain);
							oldFinMains.add(finMain);
						}
					}
				}
			}
		}

		pricingDetail.getFinanceMains().addAll(newFinMains);
		pricingDetail.getFinanceMains().addAll(oldFinMains);

		List<FinFeeDetail> pricingFinFeeDetailList = new ArrayList<FinFeeDetail>();
		List<FinFeeDetail> oldTopupFinFeeDetailList = new ArrayList<FinFeeDetail>();
		List<FinFeeDetail> newTopupFinFeeDetailList = new ArrayList<FinFeeDetail>();

		int feeCount = topUpCount;
		for (int i = 1; i <= topUpCount; i++) {
		}

		for (Listitem listitem : listBoxChargesDetail.getItems()) {

			FinFeeDetail finFeeDetail = (FinFeeDetail) listitem.getAttribute("parentFinFeeDetail");
			FinFeeDetail childFinFeeDetail = (FinFeeDetail) listitem.getAttribute("finFeeDetail");

			if (listitem.getAttribute("isParent") != null && (Boolean) listitem.getAttribute("isParent")
					&& childFinFeeDetail == null) {

				for (Component child : listitem.getChildren()) {

					// Actual Fee Box
					if (child.getFirstChild() instanceof Decimalbox) {
						Decimalbox calBox = (Decimalbox) child.getFirstChild();
						// Prepare Fin Fee Details
						prepareFinFeeDetail(finFeeDetail, calBox);
					} else if (child.getFirstChild() instanceof Combobox) { // Fee Schedule Method Box
						Combobox combobox = ((Combobox) child.getFirstChild());
						// Prepare Fee Schedule method and fee Details
						prepareFeeScheduleMethod(wve, finFeeDetail, child, combobox);
					}
				}

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmountOriginal()
							.subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmountOriginal()));
				}

				finFeeDetail.setDataModified(isDataMaintained(finFeeDetail, finFeeDetail.getBefImage())); // For Data
																											// Saving

				boolean isNew = finFeeDetail.isNewRecord();
				String tranType = "";

				if (finFeeDetail.isNewRecord()) {
					finFeeDetail.setVersion(1);
					finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(finFeeDetail.getRecordType())) {
					finFeeDetail.setVersion(finFeeDetail.getVersion() + 1);
					finFeeDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if (finFeeDetail.getRecordType().equals(PennantConstants.RCD_ADD) && finFeeDetail.isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				} else {
					finFeeDetail.setVersion(finFeeDetail.getVersion() + 1);
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}
				if (PennantConstants.TRAN_DEL.equals(tranType)) {
					if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finFeeDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
					} else if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					} else if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
					}
				} else {
					if (!PennantConstants.TRAN_UPD.equals(tranType)) {
						if (finFeeDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
						}
					}
				}
				pricingDetail.getActualFinFeeDetails().add(finFeeDetail);

			} else {
				// Top up fees
				String tpCount = (String) listitem.getAttribute("TopUpCount_");
				String numberOnly = tpCount.replaceAll("[^0-9]", "");
				int topUpCount = Integer.valueOf(numberOnly);

				Checkbox checkbox = (Checkbox) listBoxPricingDetail.getItems().get(topUpCount)
						.getFellowIfAny("RM_" + topUpCount);

				if (checkbox == null || !checkbox.isChecked()) {
					boolean decimalBoxEnable = false;
					boolean comboBoxEnable = false;

					for (Component child : listitem.getChildren()) {

						if (child.getFirstChild() instanceof Decimalbox) {
							Decimalbox calBox = (Decimalbox) child.getFirstChild();
							// Prepare Fin Fee Details
							prepareFinFeeDetail(childFinFeeDetail, calBox);
							// For preparing Workflow Details
							decimalBoxEnable = true;
						} else if (child.getFirstChild() instanceof Combobox) {
							Combobox combobox = ((Combobox) child.getFirstChild());
							// Prepare Fee Schedule method and fee Details
							prepareFeeScheduleMethod(wve, childFinFeeDetail, child, combobox);
							// For preparing Workflow Details
							comboBoxEnable = true;
						}

						if (child.getFirstChild() != null && decimalBoxEnable && comboBoxEnable) {

							if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE
									.equals(childFinFeeDetail.getTaxComponent())) {
								childFinFeeDetail.setRemainingFeeOriginal(childFinFeeDetail.getActualAmountOriginal()
										.subtract(childFinFeeDetail.getWaivedAmount())
										.subtract(childFinFeeDetail.getPaidAmountOriginal()));
							}

							childFinFeeDetail
									.setFinReference(finReferenceMap.get("Topup" + listitem.getAttribute("TopUp")));
							childFinFeeDetail.setDataModified(
									isDataMaintained(childFinFeeDetail, childFinFeeDetail.getBefImage()));
							boolean isNew = childFinFeeDetail.isNewRecord();
							boolean recordAdded = false;
							String tranType = "";

							if (childFinFeeDetail.isNewRecord()) {
								childFinFeeDetail.setVersion(1);
								childFinFeeDetail.setRecordType(PennantConstants.RCD_ADD);
							} else {
								tranType = PennantConstants.TRAN_UPD;
							}

							if (StringUtils.isBlank(childFinFeeDetail.getRecordType())) {
								childFinFeeDetail.setVersion(childFinFeeDetail.getVersion() + 1);
								childFinFeeDetail.setRecordType(PennantConstants.RCD_UPD);
							}

							if (PennantConstants.RCD_ADD.equals(childFinFeeDetail.getRecordType())
									&& childFinFeeDetail.isNewRecord()) {
								tranType = PennantConstants.TRAN_ADD;
							} else if (PennantConstants.RECORD_TYPE_NEW.equals(childFinFeeDetail.getRecordType())) {
								tranType = PennantConstants.TRAN_UPD;
							} else {
								childFinFeeDetail.setVersion(childFinFeeDetail.getVersion() + 1);
								if (isNew) {
									tranType = PennantConstants.TRAN_ADD;
								} else {
									tranType = PennantConstants.TRAN_UPD;
								}
							}
							if (PennantConstants.TRAN_DEL.equals(tranType)) {
								if (childFinFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
									childFinFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
									recordAdded = true;
								} else if (childFinFeeDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
									recordAdded = true;
								} else if (childFinFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
									childFinFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
									recordAdded = true;
								} else if (childFinFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
									recordAdded = true;
								}
							} else {
								if (!PennantConstants.TRAN_UPD.equals(tranType)) {
									if (childFinFeeDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
										recordAdded = true;
									}
								}
							}

							if (!recordAdded) {
								oldTopupFinFeeDetailList.add(childFinFeeDetail);
							} else {
								newTopupFinFeeDetailList.add(childFinFeeDetail);
							}
						}
					}

				} else {
					// If check cancel type
					childFinFeeDetail = (FinFeeDetail) listitem.getAttribute("finFeeDetail");
					if (!childFinFeeDetail.isNewRecord()) {
						oldTopupFinFeeDetailList.add(childFinFeeDetail);
					}
				}
			}
		}
		pricingFinFeeDetailList.addAll(newTopupFinFeeDetailList);
		pricingFinFeeDetailList.addAll(oldTopupFinFeeDetailList);
		pricingDetail.setTopUpFinFeeDetails(pricingFinFeeDetailList);

		if (feeCount > 0) {
			feeCount--;
		}

		List<VASRecording> pricingVasList = new ArrayList<VASRecording>();
		List<VASRecording> oldTopUpVasList = new ArrayList<VASRecording>();
		List<VASRecording> newTopUpVasList = new ArrayList<VASRecording>();

		int vasCount = topUpCount;
		for (int i = 1; i <= topUpCount; i++) {
		}

		for (Listitem listitem : listBoxVasDetail.getItems()) {
			FinTypeVASProducts vasRecording = (FinTypeVASProducts) listitem.getAttribute("VasDetail");
			if (listitem.getAttribute("isParent") != null && (Boolean) listitem.getAttribute("isParent")
					&& vasRecording == null) {
				if (StringUtils.isBlank((String) listitem.getAttribute("vasReference"))
						&& listitem.getChildren().get(1).getChildren().get(0) instanceof Decimalbox) {
					if (BigDecimal
							.valueOf(((Decimalbox) listitem.getChildren().get(1).getChildren().get(0)).doubleValue())
							.compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}
				}
			} else {
				if (CollectionUtils.isNotEmpty(listitem.getChildren().get(3).getChildren())) {
					if (StringUtils.isBlank((String) listitem.getAttribute("vasReference"))
							&& listitem.getChildren().get(3).getChildren().get(0) instanceof Decimalbox) {
						if (BigDecimal
								.valueOf(
										((Decimalbox) listitem.getChildren().get(3).getChildren().get(0)).doubleValue())
								.compareTo(BigDecimal.ZERO) == 0) {
							continue;
						}
					}
				} else if (CollectionUtils.isNotEmpty(listitem.getChildren().get(5).getChildren())) {
					if (StringUtils.isBlank((String) listitem.getAttribute("vasReference"))
							&& listitem.getChildren().get(5).getChildren().get(0) instanceof Decimalbox) {
						if (BigDecimal
								.valueOf(
										((Decimalbox) listitem.getChildren().get(5).getChildren().get(0)).doubleValue())
								.compareTo(BigDecimal.ZERO) == 0) {
							continue;
						}
					}
				}
			}

			VASRecording vasRecordings = new VASRecording();
			if (listitem.getAttribute("isParent") != null && (Boolean) listitem.getAttribute("isParent")
					&& vasRecording == null) {

				String vasReference = (String) listitem.getAttribute("vasReference");
				Date valueDate = null;
				if (listitem.getAttribute("valueDate") != null) {
					valueDate = (Date) listitem.getAttribute("valueDate");
				}

				for (Component child : listitem.getChildren()) {

					if (child.getFirstChild() instanceof Decimalbox) {
						Decimalbox calBox = (Decimalbox) child.getFirstChild();
						vasRecordings.setNewRecord((Boolean) calBox.getAttribute("newRecord"));
						vasRecordings.setProductCode(((Listcell) listitem.getChildren().get(0)).getLabel());
						vasRecordings.setFee(
								PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(calBox.doubleValue()), 2));
						vasRecordings.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
						vasRecordings.setEntityCode(finMain.getEntityCode());
						vasRecordings.setFinanceProcess(false);

						if (vasRecordings.isNewRecord()) {
							vasRecordings.setVasReference(ReferenceUtil.generateVASRef());
						} else {
							if (StringUtils.isBlank(vasReference)) {
								vasRecordings.setNewRecord(true);
								vasRecordings.setVersion(vasRecordings.getVersion() + 1);
								vasRecordings.setVasReference(ReferenceUtil.generateVASRef());
							} else {
								vasRecordings.setVasReference(vasReference);
								vasRecordings.setVersion(vasRecordings.getVersion() + 1);
							}
						}
						vasRecordings.setPrimaryLinkRef(
								getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
						if (StringUtils
								.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordType())) {
							vasRecordings.setRecordType(
									getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordType());
						} else {
							vasRecordings.setRecordType(PennantConstants.RCD_ADD);
						}
						vasRecordings.setValueDate(valueDate);
					} else if (child.getFirstChild() instanceof Combobox) {
						Combobox combobox = ((Combobox) child.getFirstChild());
						combobox.clearErrorMessage();
						String compon = getValueFromComponent(child.getFirstChild());

						if (!combobox.isDisabled() && PennantConstants.List_Select.equals(compon)
								&& vasRecordings.getFee().compareTo(BigDecimal.ZERO) > 0
								&& !this.userAction.getSelectedItem().getLabel().contains("Resubmit")
								&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {
							parenttab.setSelected(true);
							wve.add(new WrongValueException(child.getFirstChild(), Labels.getLabel("STATIC_INVALID",
									new String[] { Labels.getLabel("FeeDetail_FeeScheduleMethod") })));
						}
						vasRecordings.setFeePaymentMode(compon);
					}
				}
				pricingDetail.getActualVasDetails().add(vasRecordings);

			} else {

				String tpCount = (String) listitem.getAttribute("TopUpCount_");
				String numberOnly = tpCount.replaceAll("[^0-9]", "");
				int topUpCount = Integer.valueOf(numberOnly);
				Checkbox checkbox = (Checkbox) listBoxPricingDetail.getItems().get(topUpCount)
						.getFellowIfAny("RM_" + topUpCount);
				String vasReference = (String) listitem.getAttribute("vasReference");
				if (checkbox == null || !checkbox.isChecked()) {
					boolean flagD = false;
					boolean flagC = false;
					for (Component child : listitem.getChildren()) {
						if (child.getFirstChild() instanceof Decimalbox) {
							Decimalbox calBox = (Decimalbox) child.getFirstChild();
							vasRecordings.setNewRecord((Boolean) calBox.getAttribute("newRecord"));

							vasRecordings.setFee(PennantApplicationUtil
									.unFormateAmount(BigDecimal.valueOf(calBox.doubleValue()), 2));
							vasRecordings.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
							vasRecordings.setEntityCode(finMain.getEntityCode());
							vasRecordings.setFinanceProcess(false);
							if (vasRecordings.isNewRecord()) {
								vasRecordings.setVasReference(ReferenceUtil.generateVASRef());
							} else {
								if (StringUtils.isBlank(vasReference)) {
									vasRecordings.setNewRecord(true);
									vasRecordings.setVersion(vasRecordings.getVersion() + 1);
									vasRecordings.setVasReference(ReferenceUtil.generateVASRef());
								} else {
									vasRecordings.setVasReference(vasReference);
									vasRecordings.setVersion(vasRecordings.getVersion() + 1);
								}
							}
							// vasRecordings.setVasReference(ReferenceUtil.generateVASRef());
							vasRecordings.setProductCode(((Listcell) listitem.getChildren().get(0)).getLabel());
							flagD = true;

						} else if (child.getFirstChild() instanceof Combobox) {
							Combobox combobox = (Combobox) child.getFirstChild();
							if (combobox != null && !combobox.isDisabled()) {
								combobox.clearErrorMessage();
								String compon = getValueFromComponent(combobox);

								vasRecordings.setFeePaymentMode(compon);
								if (!combobox.isDisabled() && PennantConstants.List_Select.equals(compon)
										&& !this.userAction.getSelectedItem().getLabel().contains("Resubmit")
										&& !this.userAction.getSelectedItem().getLabel().contains("Reject")
										&& vasRecordings.getFee().compareTo(BigDecimal.ZERO) > 0) {
									wve.add(new WrongValueException(child.getFirstChild(),
											Labels.getLabel("STATIC_INVALID",
													new String[] { Labels.getLabel("FeeDetail_FeeScheduleMethod") })));
								}
								flagC = true;
							}
						}
						if (child.getFirstChild() != null && flagD && flagC) {

							vasRecordings
									.setPrimaryLinkRef(finReferenceMap.get("Topup" + listitem.getAttribute("TopUp")));
							boolean isNew = false;
							isNew = vasRecordings.isNewRecord();
							String tranType = "";
							boolean recordAdded = false;
							if (vasRecordings.isNewRecord()) {
								vasRecordings.setVersion(1);
								vasRecordings.setRecordType(PennantConstants.RCD_ADD);
							} else {
								tranType = PennantConstants.TRAN_UPD;
							}

							if (StringUtils.isBlank(vasRecordings.getRecordType())) {
								vasRecordings.setVersion(vasRecordings.getVersion() + 1);
								vasRecordings.setRecordType(PennantConstants.RCD_UPD);
							}

							if (vasRecordings.getRecordType().equals(PennantConstants.RCD_ADD)
									&& vasRecordings.isNewRecord()) {
								tranType = PennantConstants.TRAN_ADD;
							} else if (vasRecordings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								tranType = PennantConstants.TRAN_UPD;
							} else {
								vasRecordings.setVersion(vasRecordings.getVersion() + 1);
								if (isNew) {
									tranType = PennantConstants.TRAN_ADD;
								} else {
									tranType = PennantConstants.TRAN_UPD;
								}
							}
							if (PennantConstants.TRAN_DEL.equals(tranType)) {
								if (vasRecordings.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
									vasRecordings.setRecordType(PennantConstants.RECORD_TYPE_DEL);
									recordAdded = true;
								} else if (vasRecordings.getRecordType().equals(PennantConstants.RCD_ADD)) {
									recordAdded = true;
								} else if (vasRecordings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
									vasRecordings.setRecordType(PennantConstants.RECORD_TYPE_CAN);
									recordAdded = true;
								} else if (vasRecordings.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
									recordAdded = true;
								}
							} else {
								if (!PennantConstants.TRAN_UPD.equals(tranType)) {
									if (vasRecordings.getRecordType().equals(PennantConstants.RCD_ADD)) {
										recordAdded = true;
									}
								}
							}

							if (!recordAdded) {
								oldTopUpVasList.add(vasRecordings);
							} else {
								newTopUpVasList.add(vasRecordings);
							}
						}
					}
				} else {
					if (!vasRecordings.isNewRecord() && vasReference != null) {
						if (listitem.getAttribute("VasDetail") instanceof VASRecording) {
							vasRecordings = (VASRecording) listitem.getAttribute("VasDetail");
						} else if (listitem.getAttribute("VasDetail") instanceof FinTypeVASProducts) {
							vasRecordings = convertToFinTypeVasObj(
									(FinTypeVASProducts) listitem.getAttribute("VasDetail"));
						}

						if (vasRecordings != null && listitem.isDisabled()) {
							vasRecordings.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							vasRecordings.setVasReference(vasReference);
							oldTopUpVasList.add(vasRecordings);
						}
					}
				}
			}

		}
		pricingVasList.addAll(newTopUpVasList);
		pricingVasList.addAll(oldTopUpVasList);
		pricingDetail.setTopUpVasDetails(pricingVasList);

		if (vasCount > 0) {
			vasCount--;
		}

		for (FinFeeDetail finFeeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
			for (VASRecording vasRecording : pricingDetail.getActualVasDetails()) {
				if (StringUtils.isNotBlank(finFeeDetail.getVasReference())
						&& finFeeDetail.getVasReference().equals(vasRecording.getVasReference())) {
					finFeeDetail.setActualAmount(vasRecording.getFee());
					finFeeDetail.setActualAmountOriginal(vasRecording.getFee());
					finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return wve;
	}

	private BigDecimal getBigDecimalValue(Component compon) {
		CurrencyBox currencyBox = (CurrencyBox) compon;
		return PennantApplicationUtil.unFormateAmount(
				currencyBox.isReadonly() ? currencyBox.getActualValue() : currencyBox.getValidateValue(), 2);

	}

	private void prepareFinFeeDetail(FinFeeDetail fee, Decimalbox calBox) {
		logger.debug(Literal.ENTERING);

		if (fee == null || calBox == null || calBox.getAttribute("newRecord") == null) {
			return;
		}

		FinanceDetail fd = getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		fee.setNewRecord((Boolean) calBox.getAttribute("newRecord"));
		fee.setFinID(finID);
		fee.setFinReference(finReference);

		BigDecimal actualAmount = PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(calBox.doubleValue()), 2);

		BigDecimal netAmount = actualAmount.subtract(fee.getWaivedAmount());

		BigDecimal actualAmountOriginal = BigDecimal.ZERO;

		fee.setActualAmountOriginal(actualAmountOriginal);
		fee.setActualAmount(actualAmount);

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fee.getTaxComponent())) {
			fee.setNetAmountOriginal(actualAmountOriginal.subtract(fee.getWaivedAmount()));
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent())) {
			fee.setNetAmount(netAmount);
		} else {
			fee.setActualAmountOriginal(actualAmount);
			fee.setActualAmountGST(BigDecimal.ZERO);
			fee.setActualAmount(actualAmount);
			fee.setNetAmountOriginal(actualAmountOriginal);
			fee.setNetAmountGST(BigDecimal.ZERO);
			fee.setNetAmount(actualAmountOriginal);
		}

		if (fee.getCalculatedAmount().compareTo(actualAmount) != 0) {
			fee.setFeeModified(true);
		}

		fee.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		fee.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (fee.getLastMntOn() == null) {
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}

		logger.debug(Literal.LEAVING);
	}

	private VASRecording convertToFinTypeVasObj(FinTypeVASProducts vasProduct) {
		logger.debug(Literal.ENTERING);

		VASRecording vasRecording = null;
		if (vasProduct != null) {
			vasRecording = new VASRecording();
			vasRecording.setProductCode(vasProduct.getVasProduct());
			vasRecording.setFee(vasProduct.getVasFee());
			// vasRecording.setFeePaymentMode(vasProduct.getFeePaymentMode());
		}

		logger.debug(Literal.LEAVING);

		return vasRecording;
	}

	private void prepareFeeScheduleMethod(ArrayList<WrongValueException> wve, FinFeeDetail finFeeDetail,
			Component child, Combobox combobox) {
		logger.debug(Literal.ENTERING);

		if (combobox == null || finFeeDetail == null || child == null) {
			return;
		}

		combobox.clearErrorMessage();
		String feeScheduleMethod = getValueFromComponent(child.getFirstChild());

		if (!combobox.isDisabled() && PennantConstants.List_Select.equals(feeScheduleMethod)
				&& !this.userAction.getSelectedItem().getLabel().contains("Resubmit")
				&& !this.userAction.getSelectedItem().getLabel().contains("Reject")
				&& finFeeDetail.getActualAmount() != null
				&& finFeeDetail.getActualAmount().compareTo(BigDecimal.ZERO) > 0) {
			parenttab.setSelected(true);
			wve.add(new WrongValueException(child.getFirstChild(), Labels.getLabel("STATIC_INVALID",
					new String[] { Labels.getLabel("FeeDetail_FeeScheduleMethod") })));
		} else if (!combobox.isDisabled() && !PennantConstants.List_Select.equals(feeScheduleMethod)
				&& !this.userAction.getSelectedItem().getLabel().contains("Resubmit")
				&& !this.userAction.getSelectedItem().getLabel().contains("Reject")
				&& finFeeDetail.getActualAmount() != null
				&& finFeeDetail.getActualAmount().compareTo(BigDecimal.ZERO) == 0) {
			feeScheduleMethod = PennantConstants.List_Select;
		}

		finFeeDetail.setFeeScheduleMethod(feeScheduleMethod);

		if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeScheduleMethod)) {
			finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
			finFeeDetail.setPaidAmountOriginal(finFeeDetail.getActualAmountOriginal());
			finFeeDetail.setWaivedAmount(BigDecimal.ZERO);
		} else {
			finFeeDetail.setPaidAmountOriginal(BigDecimal.ZERO);
			finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);
			finFeeDetail.setPaidAmount(BigDecimal.ZERO);
		}
		logger.debug(Literal.LEAVING);
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

	/**
	 * To get the value from the component based on the component type
	 * 
	 * @param compon
	 * @return
	 */
	private String getValueFromComponent(Component compon) {
		logger.debug(Literal.ENTERING);

		if (compon instanceof Combobox) {
			Combobox combobox = (Combobox) compon;
			return getComboboxValue(combobox);
		} else if (compon instanceof Intbox) {
			Intbox intbox = (Intbox) compon;
			return Integer.toString(intbox.intValue());
		} else if (compon instanceof Decimalbox) {
			Decimalbox decimalbox = (Decimalbox) compon;
			if (decimalbox.getValue() != null) {
				return decimalbox.getValue().toString();
			}
		} else if (compon instanceof Textbox) {
			Textbox textbox = (Textbox) compon;
			return textbox.getValue();
		} else if (compon instanceof Checkbox) {
			Checkbox checkbox = (Checkbox) compon;
			if (checkbox.isChecked()) {
				logger.debug(Literal.LEAVING);
				return BOOLEAN_TRUE;
			}
		}

		logger.debug(Literal.LEAVING);

		return null;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param financeDetail
	 * 
	 */
	public void doWriteBeanToComponents(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
		this.split.setChecked(financeMain.isLoanSplitted());
		doFillBasicDetailList(financeMain, finType);

		List<FinFeeDetail> finFeeDetail = new ArrayList<FinFeeDetail>();

		if (CollectionUtils.isEmpty(financeDetail.getFinScheduleData().getFinFeeDetailList())) {
			finFeeDetail = convertToFinanceFees(
					financeDetail.getFinScheduleData().getFinanceType().getFinTypeFeesList());
		} else {
			finFeeDetail = financeDetail.getFinScheduleData().getFinFeeDetailList();
		}

		doFillChargesDetailList(finFeeDetail, financeMain.isNewRecord());

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<FinTypeVASProducts> searchObject = new JdbcSearchObject<FinTypeVASProducts>(
				FinTypeVASProducts.class);
		searchObject.addTabelName("FinTypeVASProducts_View");
		searchObject.addFilter(new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL));

		List<FinTypeVASProducts> vasProducts = pagedListService.getBySearchObject(searchObject);

		if (financeMain.isNewRecord()) {
			doFillVasDetailList(vasProducts, financeMain.isNewRecord(), new ArrayList<String>());
		} else {
			if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getVasRecordingList())) {
				List<String> productList = doFillSavedVasDetailList(
						financeDetail.getFinScheduleData().getVasRecordingList(), financeMain.isNewRecord(),
						financeMain.getRecordStatus(), financeMain.getFinCcy());

				doFillVasDetailList(vasProducts, financeMain.isNewRecord(), productList);
			} else {
				doFillVasDetailList(vasProducts, financeMain.isNewRecord(), new ArrayList<String>());
			}
		}

		if (!financeMain.isNewRecord() && financeDetail.getPricingDetail() != null) {
			topUpCount = financeDetail.getPricingDetail().getFinanceMains().size();
		}

		PricingDetail pricingDetail = financeDetail.getPricingDetail();
		if (pricingDetail != null && CollectionUtils.isNotEmpty(pricingDetail.getFinanceMains())) {
			for (int i = 1; i <= pricingDetail.getFinanceMains().size(); i++) {
				FinanceMain finMain = pricingDetail.getFinanceMains().get(i - 1);
				FinanceMain befImage = new FinanceMain();
				BeanUtils.copyProperties(finMain, befImage);
				finMain.setBefImage(befImage);
				appendTopUpBasicDetails(finMain, false, i, topUpFinType.get(finMain.getFinType()));
				appendTopUpChargeDetails(finFeeDetailMap.get(finMain.getFinReference()), false, i);
				if (topUpFinType == null) {
					appendSavedVasDetails(vasRecordingMap.get(finMain.getFinReference()), false, i, new ArrayList<>());
				} else {
					appendSavedVasDetails(vasRecordingMap.get(finMain.getFinReference()), false, i,
							topUpFinType.get(finMain.getFinType()).getFinTypeVASProductsList());
				}
				doCheckRights();

			}

		}

		try {
			financeMainDialogCtrl.getClass().getMethod("doSetFinAmount", BigDecimal.class).invoke(
					getFinanceMainDialogCtrl(), PennantApplicationUtil.formateAmount(getFinAssetValue(), finFormatter));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doFillBasicDetailList(FinanceMain financeMain, FinanceType financeType) {
		logger.debug(Literal.ENTERING);

		this.listBoxPricingDetail.getItems().clear();
		this.listBoxPricingDetail.setVflex("max");

		if (StringUtils.isNotBlank(financeMain.getParentRef())) {
			((Listheader) this.listBoxPricingDetail.getListhead().getChildren().get(1)).setLabel("TopUp");
		}

		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		// Loan Type
		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell("Loan Type");
		lc.setParent(item);
		String topup_label = "TopUp_" + topUpCount;

		Textbox loanType = new Textbox();
		loanType.setId("LoanType_" + topup_label);
		loanType.setAttribute("finMain", financeMain);
		loanType.setValue(financeMain.getFinType());
		loanType.setReadonly(true);

		lc = new Listcell();
		lc.appendChild(loanType);
		lc.setParent(item);
		item.setParent(listBoxPricingDetail);

		// Loan Amount
		Listitem item0 = new Listitem();
		Listcell lc0;
		lc0 = new Listcell("Loan Amount");
		lc0.setParent(item0);

		CurrencyBox calBox = new CurrencyBox();
		calBox.setProperties(true, finFormatter);
		calBox.setId("LoanAmount_" + topup_label);
		calBox.setAttribute("finMain", financeMain);
		calBox.setValue(PennantApplicationUtil.formateAmount(financeMain.getFinAssetValue(), finFormatter));
		calBox.setProperties(true, format);
		calBox.setReadonly(isReadonly());

		lc0 = new Listcell();
		lc0.appendChild(calBox);
		lc0.setParent(item0);
		item0.setParent(listBoxPricingDetail);

		// ROI
		Listitem item1 = new Listitem();
		Listcell lc1;
		lc1 = new Listcell("ROI %");
		lc1.setParent(item1);
		item1.setParent(listBoxPricingDetail);

		Decimalbox roiBox = new Decimalbox();
		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		hbox.appendChild(space);
		roiBox.setMaxlength(18);
		roiBox.setId("ROI_" + topup_label);
		roiBox.setReadonly(true);
		roiBox.setFormat(PennantConstants.rateFormate9);
		roiBox.setRoundingMode(RoundingMode.DOWN.ordinal());
		roiBox.setScale(9);
		roiBox.setValue(roi);

		lc1 = new Listcell();
		hbox.appendChild(roiBox);
		lc1.appendChild(hbox);
		lc1.setParent(item1);

		// Tenure
		Listitem item2 = new Listitem();
		Listcell lc2;
		lc2 = new Listcell("Tenure");
		lc2.setParent(item2);
		item2.setParent(listBoxPricingDetail);

		Intbox tenureBox = new Intbox();
		Hbox tenureHbox = new Hbox();
		Space tenureSpace = new Space();
		tenureSpace.setSpacing("2px");
		tenureSpace.setSclass(PennantConstants.mandateSclass);
		tenureHbox.appendChild(tenureSpace);
		tenureBox.setMaxlength(18);
		tenureBox.setId("Tenure_" + topup_label);
		tenureBox.setValue(financeMain.getNumberOfTerms());
		tenureBox.setStyle("text-align:right");
		tenureBox.setReadonly(true);
		lc2 = new Listcell();
		tenureHbox.appendChild(tenureBox);
		lc2.appendChild(tenureHbox);
		lc2.setParent(item2);
		logger.debug(Literal.LEAVING);
	}

	private void doFillChargesDetailList(List<FinFeeDetail> finFeeDetails, boolean isNew) {
		logger.debug(Literal.ENTERING);

		boolean readOnly = isReadonly();
		this.listBoxChargesDetail.getItems().clear();
		listBoxChargesDetail.setVflex("max");

		if (CollectionUtils.isEmpty(finFeeDetails)) {
			return;
		}

		for (FinFeeDetail fee : finFeeDetails) {
			if (StringUtils.isNotEmpty(fee.getVasReference())) {
				continue;
			}

			String taxComponent = null;

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fee.getTaxComponent())) {
				taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
			} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent())) {
				taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
			} else {
				taxComponent = Labels.getLabel("label_GST_NotApplicable");
			}

			String feeType = fee.getFeeTypeDesc() + " - (" + taxComponent + ")";
			if (StringUtils.isNotEmpty(fee.getVasReference())) {
				feeType = fee.getVasReference();
				fee.setFeeTypeCode(feeType);
				fee.setFeeTypeDesc(feeType);
			}

			Listitem item = new Listitem();
			Listcell lc;
			String feeTypeDesc = StringUtils.trimToEmpty(fee.getFeeTypeDesc());
			lc = new Listcell(feeTypeDesc);
			lc.setParent(item);

			String topup_label = "TopUp_" + topUpCount;

			Decimalbox calBox = new Decimalbox();
			calBox.setId(fee.getFeeTypeCode() + "_" + topup_label);
			calBox.setAttribute("newRecord", isNew);
			calBox.setMaxlength(18);
			calBox.setFormat(PennantApplicationUtil.getAmountFormate(2));
			calBox.setValue(PennantApplicationUtil.formateAmount(fee.getActualAmount(), 2));
			calBox.setAttribute("finFeeDetail", fee);
			calBox.setReadonly(readOnly);
			lc = new Listcell();
			lc.appendChild(calBox);
			lc.setParent(item);
			item.setParent(listBoxChargesDetail);

			String excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + "," + ","
					+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ",";

			Combobox modeOfColl = new Combobox();
			fillComboBox(modeOfColl, fee.getFeeScheduleMethod(), PennantStaticListUtil.getRemFeeSchdMethods(),
					excludeFields);
			modeOfColl.setId(fee.getFeeTypeCode() + "_MOC_" + topup_label);
			modeOfColl.setAttribute("finFeeDetail", fee);
			modeOfColl.setReadonly(true);
			modeOfColl.setDisabled(readOnly);
			lc = new Listcell();
			lc.appendChild(modeOfColl);
			lc.setParent(item);
			item.setAttribute("parentFinFeeDetail", fee);
			item.setAttribute("isParent", true);
			item.setParent(listBoxChargesDetail);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doFillVasDetailList(List<FinTypeVASProducts> vasList, boolean newRecord, List<String> productList) {
		logger.debug(Literal.ENTERING);

		boolean readOnly = isReadonly();
		int count = 0;
		// this.listBoxVasDetail.getItems().clear();
		listBoxVasDetail.setVflex("max");

		if (CollectionUtils.isEmpty(vasList)) {
			return;
		}

		for (FinTypeVASProducts finTypeVASProducts : vasList) {

			String productDesc = finTypeVASProducts.getVasProduct();

			for (String vasRecProduct : productList) {
				if (StringUtils.equals(vasRecProduct, productDesc)) {
					count = 1;
				}
			}

			if (count == 1) {
				count = 0;
				continue;
			}

			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(productDesc);
			lc.setParent(item);

			String topup_label = "TopUp_" + topUpCount;

			Decimalbox calBox = new Decimalbox();
			calBox.setMaxlength(18);
			calBox.setAttribute("newRecord", newRecord);
			calBox.setFormat(PennantApplicationUtil.getAmountFormate(2));
			// calBox.setValue(PennantApplicationUtil.formateAmount(financeMain.getFinAmount(), formatter));
			calBox.setId(finTypeVASProducts.getVasProduct() + "_" + topup_label);
			calBox.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
			calBox.setReadonly(readOnly);

			lc = new Listcell();
			lc.appendChild(calBox);
			lc.setParent(item);
			item.setParent(listBoxVasDetail);

			String excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + "," + ","
					+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ",";

			Combobox modeOfColl = new Combobox();
			modeOfColl.setReadonly(true);
			modeOfColl.setDisabled(readOnly);
			fillComboBox(modeOfColl, "", PennantStaticListUtil.getRemFeeSchdMethods(), excludeFields);
			lc = new Listcell();
			lc.appendChild(modeOfColl);
			lc.setParent(item);
			item.setAttribute("parentVasDetail", finTypeVASProducts);
			item.setAttribute("isParent", true);
			item.setParent(listBoxVasDetail);
		}

		logger.debug(Literal.LEAVING);
	}

	private List<String> doFillSavedVasDetailList(List<VASRecording> vasList, boolean newRecord, String rcdStatus,
			String formatter) {

		logger.debug(Literal.ENTERING);

		int finFormatter = CurrencyUtil.getFormat(formatter);
		boolean readOnly = isReadonly();
		this.listBoxVasDetail.getItems().clear();
		listBoxVasDetail.setVflex("max");
		List<String> productList = new ArrayList<>();

		if (CollectionUtils.isEmpty(vasList)) {
			return productList;
		}

		for (VASRecording vASRecording : vasList) {
			Listitem item = new Listitem();
			Listcell lc;
			String productDesc = vASRecording.getProductCode();
			productList.add(productDesc);
			lc = new Listcell(productDesc);
			lc.setParent(item);

			Decimalbox calBox = new Decimalbox();
			calBox.setMaxlength(18);
			calBox.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));

			if (readOnly) {
				readOnlyComponent(true, calBox);
			}

			calBox.setValue(PennantApplicationUtil.formateAmount(vASRecording.getFee(), finFormatter));

			calBox.setAttribute("newRecord", newRecord);
			lc = new Listcell();
			lc.appendChild(calBox);
			lc.setParent(item);
			item.setParent(listBoxVasDetail);

			String excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + "," + ","
					+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ",";

			Combobox modeOfColl = new Combobox();
			if (readOnly) {
				readOnlyComponent(true, modeOfColl);
			}

			fillComboBox(modeOfColl, vASRecording.getFeePaymentMode(), PennantStaticListUtil.getRemFeeSchdMethods(),
					excludeFields);
			lc = new Listcell();
			lc.appendChild(modeOfColl);
			lc.setParent(item);
			item.setAttribute("isParent", true);
			item.setAttribute("vasReference", vASRecording.getVasReference());
			item.setAttribute("valueDate", vASRecording.getValueDate());
			item.setParent(listBoxVasDetail);
		}

		logger.debug(Literal.LEAVING);
		return productList;
	}

	/**
	 * Event for To show splitting loans
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$split(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (this.split.isChecked()) {
			if (topUpCount <= 0) {
				MessageUtil.showMessage("There are no TopUps to split");
				this.split.setChecked(false);
			} else {
				MessageUtil.confirm("Are you sure to split loans ?", evnt -> {
					if (Messagebox.ON_YES.equals(evnt.getName())) {
						pricingDetail.setSplit(true);
						pricingDetail.setNewRecord(true);
					} else {
						this.split.setChecked(false);
						pricingDetail.setSplit(false);
					}
				});
			}
		} else {
			pricingDetail.setSplit(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendTopUpBasicDetails(FinanceMain financeMain, boolean newRecord, int topUpCount,
			FinanceType financeType) {
		logger.debug(Literal.ENTERING);

		boolean readOnly = isReadonly();
		financeMain.setNewRecord(newRecord);
		financeMain.setMaturityDate(null);
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		Listcell lc = null;
		String topup_label = "";

		for (int i = 0; i < listBoxPricingDetail.getItems().size(); i++) {
			Listitem item = listBoxPricingDetail.getItems().get(i);

			if (i == 0) {
				Auxhead auxHead = new Auxhead();
				auxHead.setId("auxHead_" + topUpCount);
				auxHead.setDraggable("false");

				Auxheader auxHeader = new Auxheader("");
				auxHeader.setColspan(1);
				auxHeader.setStyle("font-size: 14px");
				auxHeader.setParent(auxHead);
				auxHeader.setAlign("center");

				Listheader listHeader = new Listheader();
				listHeader.setStyle("font-size: 12px");
				listHeader.setHflex("min");
				// listHeader.setWidth("25px");

				topup_label = "TopUp_" + topUpCount;

				listHeader.setLabel("TopUp_" + topUpCount);
				listHeader.setParent(listBoxPricingDetail.getListhead());

				Space space = new Space();
				listHeader.appendChild(space);
				Checkbox checkbox = new Checkbox();
				checkbox.setId("RM_" + topUpCount);
				checkbox.addForward("onClick", self, "onClick_listCellRemove");
				if (readOnly) {
					checkbox.setVisible(false);
				} else {
					checkbox.setVisible(!isReadOnly("PricingDetailListCtrl_Remove_Pricing"));
				}

				listHeader.appendChild(checkbox);
				Label label = new Label("Remove");
				if (readOnly) {
					label.setVisible(false);
				} else {
					label.setVisible(!isReadOnly("PricingDetailListCtrl_Remove_Pricing"));
				}
				listHeader.appendChild(label);

				if (listBoxPricingDetail.getItemCount() < 0) {
					auxHead.setParent(listBoxPricingDetail);
				}
			}

			if (i == 0) {
				Textbox childLoanType = new Textbox();
				childLoanType.setId("ChildLoanType_" + topup_label);
				childLoanType.setAttribute("newRecord", newRecord);
				childLoanType.setAttribute("finMain", financeMain);
				if (!newRecord) {
					childLoanType.setValue(financeMain.getFinType());
				} else {
					childLoanType.setValue(financeType.getFinType());
				}
				// calBox.addForward("onFulfill", window_PricingDetailList, "onChangeTopUpLoanAmount", calBox);
				childLoanType.setReadonly(readOnly);
				lc = new Listcell();
				lc.appendChild(childLoanType);
				item.setAttribute("Basic" + topup_label, financeMain);
				lc.setParent(item);
				continue;
			}

			if (i == 1) {
				CurrencyBox calBox = new CurrencyBox();
				calBox.setId("LoanAmount_" + topup_label);
				calBox.setAttribute("newRecord", newRecord);
				calBox.setAttribute("finMain", financeMain);
				calBox.setProperties(true, finFormatter);
				calBox.setBalUnvisible(false, false);
				calBox.setReadonly(false);
				calBox.setTextBoxWidth(100);
				if (!newRecord) {
					calBox.setValue(PennantApplicationUtil.formateAmount(financeMain.getFinAssetValue(), finFormatter));
				} else {
					calBox.setValue(BigDecimal.ZERO);
				}
				calBox.setReadonly(readOnly);
				// calBox.addForward("onFulfill", window_PricingDetailList, "onChangeTopUpLoanAmount", calBox);
				lc = new Listcell();
				lc.appendChild(calBox);
				item.setAttribute("Basic" + topup_label, financeMain);
				lc.setParent(item);
				continue;
			}

			if (i == 2) {
				Decimalbox roiBox = new Decimalbox();
				Hbox hbox = new Hbox();
				Space space = new Space();
				space.setSpacing("2px");
				space.setSclass(PennantConstants.mandateSclass);
				hbox.appendChild(space);
				roiBox.setId("ROI_" + topup_label);
				roiBox.setMaxlength(18);
				roiBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				roiBox.setValue(PennantApplicationUtil.formatRate(financeMain.getRepayProfitRate().doubleValue(), 2));
				roiBox.setDisabled(false);
				roiBox.setReadonly(readOnly);
				hbox.appendChild(roiBox);
				lc = new Listcell();
				lc.appendChild(hbox);
				item.setAttribute("Basic" + topup_label, financeMain);
				lc.setParent(item);
				continue;
			}

			if (i == 3) {
				Intbox tenureBox = new Intbox();
				Hbox hbox = new Hbox();
				Space space = new Space();
				space.setSpacing("2px");
				space.setSclass(PennantConstants.mandateSclass);
				hbox.appendChild(space);
				tenureBox.setId("Tenure_" + topup_label);
				tenureBox.setMaxlength(18);
				tenureBox.setDisabled(false);
				tenureBox.setStyle("text-align:right");
				if (!newRecord) {
					tenureBox.setValue(financeMain.getNumberOfTerms());
				} else {
					tenureBox.setValue(0);
				}
				tenureBox.setReadonly(readOnly);
				hbox.appendChild(tenureBox);
				lc = new Listcell();
				lc.appendChild(hbox);
				item.setAttribute("Basic" + topup_label, financeMain);
				lc.setParent(item);
				continue;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendTopUpChargeDetails(List<FinFeeDetail> finFeeDetails, boolean isNewRecord, int count) {
		logger.debug(Literal.ENTERING);

		boolean readOnly = isReadonly();
		Listcell lc = null;
		listBoxChargesDetail.getItems();
		Listitem item = null;
		String topup_label = "";
		for (int i = 0; i <= listBoxChargesDetail.getItems().size(); i++) {

			item = listBoxChargesDetail.getItemAtIndex(i);
			Listcell lc1 = new Listcell();
			lc1.setParent(item);

			Listcell lc2 = new Listcell();
			lc2.setParent(item);

			if (i == 0) {
				Auxhead auxHead = new Auxhead();
				auxHead.setId("auxHead_Charge" + count);
				auxHead.setDraggable("false");

				Auxheader auxHeader = new Auxheader("");
				auxHeader.setColspan(1);
				auxHeader.setStyle("font-size: 14px");
				auxHeader.setParent(auxHead);
				auxHeader.setAlign("center");

				Listheader listHeader = new Listheader();
				listHeader.setStyle("font-size: 12px");
				listHeader.setHflex("min");
				// listHeader.setWidth("30px");

				topup_label = "TopUp_" + count;

				listHeader.setLabel("TopUp_" + count);
				listHeader.setParent(listBoxChargesDetail.getListhead());

				listHeader = new Listheader();
				listHeader.setStyle("font-size: 12px");
				listHeader.setHflex("min");
				// listHeader.setWidth("30px");
				listHeader.setLabel("Mode Of Collection");
				listHeader.setParent(listBoxChargesDetail.getListhead());

				auxHead.setParent(listBoxChargesDetail);
			}
		}

		Listitem item1 = null;
		int cellCount = 2 * count;
		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
			for (int k = 0; k < finFeeDetails.size(); k++) {
				FinFeeDetail finFeeDetail = finFeeDetails.get(k);
				if (StringUtils.isEmpty(finFeeDetail.getVasReference())) {
					item1 = new Listitem();
					item1.setAttribute("TopUp", count);
					String feeTypeDesc = finFeeDetail.getFeeTypeDesc();
					lc = new Listcell(feeTypeDesc);
					lc.setParent(item1);

					for (int i = 0; i < cellCount; i++) {
						lc = new Listcell();
						lc.setParent(item1);
					}

					Decimalbox calBox = new Decimalbox();

					finFeeDetail.setNewRecord(isNewRecord);
					calBox.setId(finFeeDetail.getFeeTypeCode() + "_" + topup_label);
					calBox.setWidth("85px");
					calBox.setMaxlength(18);
					calBox.setFormat(PennantApplicationUtil.getAmountFormate(2));
					calBox.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getActualAmount(), 2));
					calBox.setAttribute("newRecord", isNewRecord);
					calBox.setReadonly(readOnly);
					lc = new Listcell();
					lc.appendChild(calBox);
					lc.setParent(item1);
					item1.setAttribute("Charge" + topup_label, finFeeDetail);
					item1.setParent(listBoxChargesDetail);

					String excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
							+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + "," + ","
							+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ",";

					Combobox modeOfColl = new Combobox();
					modeOfColl.setId(finFeeDetail.getFeeTypeCode() + "_MOC_" + topup_label);
					fillComboBox(modeOfColl, finFeeDetail.getFeeScheduleMethod(),
							PennantStaticListUtil.getRemFeeSchdMethods(), excludeFields);
					modeOfColl.setDisabled(readOnly);
					lc = new Listcell();
					lc.appendChild(modeOfColl);
					lc.setParent(item1);
					item1.setAttribute("finFeeDetail", finFeeDetail);
					item1.setAttribute("Charge_" + topup_label, finFeeDetail);
					item1.setAttribute("TopUpCount_", topup_label);
					item1.setParent(listBoxChargesDetail);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendSavedVasDetails(List<VASRecording> list, boolean newRecord, int count,
			List<FinTypeVASProducts> productList) {
		logger.debug(Literal.ENTERING);

		boolean readonly = isReadonly();
		Listcell lc = null;
		listBoxVasDetail.getItems();
		Listitem item = null;
		String topup_label = "";
		List<String> vasNames = new ArrayList<>();

		for (int i = 0; i <= listBoxVasDetail.getItems().size(); i++) {
			item = listBoxVasDetail.getItemAtIndex(i);
			Listcell lc1 = new Listcell();
			lc1.setParent(item);

			Listcell lc2 = new Listcell();
			lc2.setParent(item);

			if (i == 0) {
				Auxhead auxHead = new Auxhead();
				auxHead.setId("auxHead_VAS_" + count);
				auxHead.setDraggable("false");

				Auxheader auxHeader = new Auxheader("");
				auxHeader.setColspan(1);
				auxHeader.setStyle("font-size: 14px");
				auxHeader.setParent(auxHead);
				auxHeader.setAlign("center");

				Listheader listHeader = new Listheader();
				listHeader.setStyle("font-size: 12px");
				listHeader.setHflex("min");
				topup_label = "TopUp_" + count;
				listHeader.setLabel(topup_label);
				listHeader.setParent(listBoxVasDetail.getListhead());

				listHeader = new Listheader();
				listHeader.setStyle("font-size: 12px");
				listHeader.setHflex("min");
				listHeader.setLabel("Mode Of Collection");
				listHeader.setParent(listBoxVasDetail.getListhead());

				auxHead.setParent(listBoxVasDetail);
			}
		}

		Listitem item1 = null;
		int cellCount = 2 * count;
		List<FinTypeVASProducts> vasProducts = convertToFinTypeVas(list);

		for (int k = 0; k < productList.size(); k++) {
			boolean isNew = true;
			FinTypeVASProducts finTypeVas = productList.get(k);
			String productDesc = finTypeVas.getVasProduct();
			String feePaymentMode = finTypeVas.getFeePaymentMode();
			String vasReference = "";

			if (vasNames.contains(productDesc)) {
				continue;
			}

			for (FinTypeVASProducts vasRecProduct : vasProducts) {
				if (StringUtils.equals(vasRecProduct.getVasProduct(), productDesc)) {
					finTypeVas.setVasFee(vasRecProduct.getVasFee());
					finTypeVas.setFeePaymentMode(vasRecProduct.getFeePaymentMode());
					vasReference = vasRecProduct.getVasReference();
					vasNames.add(productDesc);
					isNew = false;
					break;
				}
			}

			item1 = new Listitem();
			item1.setAttribute("TopUp", count);

			lc = new Listcell(productDesc);
			lc.setParent(item1);

			for (int i = 0; i < cellCount; i++) {
				lc = new Listcell();
				lc.setParent(item1);
			}

			Decimalbox calBox = new Decimalbox();
			finTypeVas.setNewRecord(newRecord);
			calBox.setId(finTypeVas.getVasProduct() + "_" + topup_label);
			calBox.setWidth("85px");
			calBox.setMaxlength(18);
			calBox.setFormat(PennantApplicationUtil.getAmountFormate(2));
			if (isNew) {
				calBox.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, 2));
			} else {
				calBox.setValue(PennantApplicationUtil.formateAmount(finTypeVas.getVasFee(), 2));
			}
			calBox.setAttribute("newRecord", newRecord);
			calBox.setReadonly(readonly);
			lc = new Listcell();
			lc.appendChild(calBox);
			lc.setParent(item1);
			item1.setParent(listBoxVasDetail);

			String excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + "," + ","
					+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ",";

			Combobox modeOfColl = new Combobox();
			modeOfColl.setId(finTypeVas.getVasProduct() + "_MOC_" + topup_label);

			if (StringUtils.isNotEmpty(feePaymentMode)) {
				fillComboBox(modeOfColl, feePaymentMode, PennantStaticListUtil.getRemFeeSchdMethods(), excludeFields);
			} else {
				fillComboBox(modeOfColl, finTypeVas.getFeePaymentMode(), PennantStaticListUtil.getRemFeeSchdMethods(),
						excludeFields);
			}
			modeOfColl.setDisabled(readonly);
			lc = new Listcell();
			lc.appendChild(modeOfColl);
			lc.setParent(item1);
			item1.setAttribute("VasDetail", productList.get(k));
			item1.setAttribute("TopUpCount_", topup_label);
			item1.setAttribute("vasReference", vasReference);

			item1.setAttribute("Vas_" + topup_label, productList.get(k));
			item1.setParent(listBoxVasDetail);

		}

		logger.debug(Literal.LEAVING);
	}

	private List<FinTypeVASProducts> convertToFinTypeVas(List<VASRecording> vasRecordingList) {
		logger.debug(Literal.ENTERING);

		List<FinTypeVASProducts> finTypeVASProducts = new ArrayList<FinTypeVASProducts>();

		if (vasRecordingList != null && !vasRecordingList.isEmpty()) {

			FinTypeVASProducts finTypeVasProducts = null;
			for (VASRecording vasRecording : vasRecordingList) {

				finTypeVasProducts = new FinTypeVASProducts();
				finTypeVasProducts.setNewRecord(true);
				finTypeVasProducts.setVasProduct(vasRecording.getProductCode());
				finTypeVasProducts.setVasFee(vasRecording.getFee());
				finTypeVasProducts.setFeePaymentMode(vasRecording.getFeePaymentMode());
				finTypeVasProducts.setVasReference(vasRecording.getVasReference());
				finTypeVASProducts.add(finTypeVasProducts);
			}
		}

		logger.debug(Literal.LEAVING);

		return finTypeVASProducts;
	}

	private List<FinFeeDetail> convertToFinanceFees(List<FinTypeFees> finTypeFeesList) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> finFeeDetails = new ArrayList<FinFeeDetail>();

		if (finTypeFeesList != null && !finTypeFeesList.isEmpty()) {
			FinFeeDetail finFeeDetail = null;
			String userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();
			String finBranch = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinBranch();
			String finCCY = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();

			long custId = 0;
			if (financeDetail.getCustomerDetails() != null) {
				custId = financeDetail.getCustomerDetails().getCustomer().getCustID();
			}

			Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(custId, finCCY, userBranch,
					finBranch, financeDetail.getFinanceTaxDetail());

			for (FinTypeFees finTypeFee : finTypeFeesList) {
				finFeeDetail = new FinFeeDetail();
				finFeeDetail.setNewRecord(true);
				finFeeDetail.setOriginationFee(finTypeFee.isOriginationFee());
				finFeeDetail.setFinEvent(finTypeFee.getFinEvent());
				finFeeDetail.setFinEventDesc(finTypeFee.getFinEventDesc());
				finFeeDetail.setFeeTypeID(finTypeFee.getFeeTypeID());
				finFeeDetail.setFeeOrder(finTypeFee.getFeeOrder());
				finFeeDetail.setFeeTypeCode(finTypeFee.getFeeTypeCode());
				finFeeDetail.setFeeTypeDesc(finTypeFee.getFeeTypeDesc());
				finFeeDetail.setFeeScheduleMethod(finTypeFee.getFeeScheduleMethod());
				finFeeDetail.setCalculationType(finTypeFee.getCalculationType());
				finFeeDetail.setRuleCode(finTypeFee.getRuleCode());

				BigDecimal finAmount = CalculationUtil.roundAmount(finTypeFee.getAmount(),
						financeDetail.getFinScheduleData().getFinanceMain().getCalRoundingMode(),
						financeDetail.getFinScheduleData().getFinanceMain().getRoundingTarget());
				finTypeFee.setAmount(finAmount);

				finFeeDetail.setFixedAmount(finTypeFee.getAmount());
				finFeeDetail.setPercentage(finTypeFee.getPercentage());
				finFeeDetail.setCalculateOn(finTypeFee.getCalculateOn());
				finFeeDetail.setAlwDeviation(finTypeFee.isAlwDeviation());
				finFeeDetail.setMaxWaiverPerc(finTypeFee.getMaxWaiverPerc());
				finFeeDetail.setAlwModifyFee(finTypeFee.isAlwModifyFee());
				finFeeDetail.setAlwModifyFeeSchdMthd(finTypeFee.isAlwModifyFeeSchdMthd());
				finFeeDetail.setCalculatedAmount(finTypeFee.getAmount());
				finFeeDetail.setTaxComponent(finTypeFee.getTaxComponent());
				finFeeDetail.setTaxApplicable(finTypeFee.isTaxApplicable());

				if (finTypeFee.isTaxApplicable()) {
					this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, getFinanceDetail(),
							taxPercentages);
				} else {
					finFeeDetail.setActualAmountOriginal(finTypeFee.getAmount());
					finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
					finFeeDetail.setActualAmount(finTypeFee.getAmount());

					BigDecimal netAmountOriginal = finFeeDetail.getActualAmountOriginal()
							.subtract(finFeeDetail.getWaivedAmount());

					finFeeDetail.setNetAmountOriginal(netAmountOriginal);
					finFeeDetail.setNetAmountGST(BigDecimal.ZERO);
					finFeeDetail.setNetAmount(netAmountOriginal);

					if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
						finFeeDetail.setPaidAmountOriginal(finTypeFee.getAmount());
						finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);
						finFeeDetail.setPaidAmount(finTypeFee.getAmount());
					}

					if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
						finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
					}

					finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmount()
							.subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
					finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount())
							.subtract(finFeeDetail.getPaidAmount()));
				}

				finFeeDetails.add(finFeeDetail);
			}
		}

		logger.debug(Literal.LEAVING);
		return finFeeDetails;
	}

	public void onClick$btnAdd_AddPricingTopUp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doShowLoanTypeSelectionDialog();
		// appendTopupLoan();
		logger.debug(Literal.LEAVING);
	}

	protected void appendTopupLoan(String finType) {
		int topupCount = listBoxPricingDetail.getItemCount();
		String topUpLabe = "";

		for (int i = 1; i < topupCount; i++) {
			Checkbox checkbox = (Checkbox) listBoxPricingDetail.getItems().get(i).getFellowIfAny("RM_" + i);
			topUpLabe = "TopUp_" + i;
			if (checkbox != null && checkbox.isChecked()) {
				checkbox.setChecked(false);
				enableTopUp(topUpLabe);
				return;
			}
		}

		if (topUpCount == 2) {
			MessageUtil.showError("Maximum Two Top-Ups allowed");
			return;
		}

		topUpCount++;
		FinScheduleData finSchdData = financeDetail.getFinScheduleData();
		FinScheduleData topUpFinSchdData = ObjectUtil.clone(finSchdData);
		doRenderItems(finType, topUpFinSchdData, true);

		if (pricingDetail == null) {
			logger.debug("priceDetail became null");
		}
	}

	private void doShowLoanTypeSelectionDialog() {
		try {
			Map<String, Object> arg = new HashMap<>();
			arg.put("financeType", getFinanceDetail().getFinScheduleData().getFinanceType().getFinType());
			arg.put("pricingDetailListCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectChildLoanFinanceTypeDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onClick$btnRemove_RemovePricingTopUp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		int topupCount = listBoxPricingDetail.getItemCount();
		String topUpLabe = "";
		for (int i = 1; i < topupCount; i++) {
			Checkbox checkbox = (Checkbox) listBoxPricingDetail.getItems().get(i).getFellowIfAny("RM_" + i);
			topUpLabe = "TopUp_" + i;
			if (checkbox != null && checkbox.isChecked()) {
				removeTopUp(topUpLabe);
			} else {
				enableTopUp(topUpLabe);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void enableTopUp(String topUpLabe) {
		logger.debug(Literal.ENTERING);

		// Vas Details
		int vasCount = this.listBoxVasDetail.getItemCount();
		for (int j = 0; j < vasCount; j++) {
			if (this.listBoxVasDetail.getItems().get(j).getAttribute("Vas_" + topUpLabe) != null) {
				FinTypeVASProducts vasProducts = (FinTypeVASProducts) this.listBoxVasDetail.getItems().get(j)
						.getAttribute("Vas_" + topUpLabe);
				this.listBoxVasDetail.getItems().get(j).setDisabled(false);
				if (!vasProducts.isNewRecord()) {
					// vasProducts.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
		}

		// Charge Details
		int chargeCount = this.listBoxChargesDetail.getItemCount();
		for (int j = 0; j < chargeCount; j++) {
			if (this.listBoxChargesDetail.getItems().get(j).getAttribute("Charge_" + topUpLabe) != null) {
				FinFeeDetail chanrges = (FinFeeDetail) this.listBoxChargesDetail.getItems().get(j)
						.getAttribute("Charge_" + topUpLabe);
				this.listBoxChargesDetail.getItems().get(j).setDisabled(false);
				if (!chanrges.isNewRecord()) {
					// chanrges.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
		}

		// Loan Details
		int loanCount = this.listBoxPricingDetail.getItemCount();
		for (int j = 0; j < loanCount; j++) {
			if (this.listBoxPricingDetail.getItems().get(j).getAttribute("Basic" + topUpLabe) != null) {
				Listitem listitem = this.listBoxPricingDetail.getItems().get(j);
				FinanceMain loans = (FinanceMain) this.listBoxPricingDetail.getItems().get(j)
						.getAttribute("Basic" + topUpLabe);

				CurrencyBox calBox = (CurrencyBox) listitem.getFellowIfAny("LoanAmount_" + topUpLabe);
				Decimalbox roi = (Decimalbox) listitem.getFellowIfAny("ROI_" + topUpLabe);
				Intbox tenure = (Intbox) listitem.getFellowIfAny("Tenure_" + topUpLabe);

				readOnlyComponent(false, calBox);
				readOnlyComponent(false, roi);
				readOnlyComponent(false, tenure);
				if (!loans.isNewRecord()) {
					// loans.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void removeTopUp(String topUpLabe) {
		logger.debug(Literal.ENTERING);

		// Vas Details
		int vasCount = this.listBoxVasDetail.getItemCount();
		for (int j = 0; j < vasCount; j++) {
			if (this.listBoxVasDetail.getItems().get(j).getAttribute("Vas_" + topUpLabe) != null) {
				FinTypeVASProducts vasProducts = (FinTypeVASProducts) this.listBoxVasDetail.getItems().get(j)
						.getAttribute("Vas_" + topUpLabe);
				this.listBoxVasDetail.getItems().get(j).setDisabled(true);
				if (!vasProducts.isNewRecord()) {
					vasProducts.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
			}
		}

		// Charge Details
		int chargeCount = this.listBoxChargesDetail.getItemCount();
		for (int j = 0; j < chargeCount; j++) {
			if (this.listBoxChargesDetail.getItems().get(j).getAttribute("Charge_" + topUpLabe) != null) {
				FinFeeDetail charge = (FinFeeDetail) this.listBoxChargesDetail.getItems().get(j)
						.getAttribute("Charge_" + topUpLabe);
				this.listBoxChargesDetail.getItems().get(j).setDisabled(true);
				if (!charge.isNewRecord()) {
					charge.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
			}
		}

		// Loan Details
		int loanCount = this.listBoxPricingDetail.getItemCount();
		for (int j = 0; j < loanCount; j++) {
			if (this.listBoxPricingDetail.getItems().get(j).getAttribute("Basic" + topUpLabe) != null) {
				Listitem listitem = this.listBoxPricingDetail.getItems().get(j);
				FinanceMain loans = (FinanceMain) this.listBoxPricingDetail.getItems().get(j)
						.getAttribute("Basic" + topUpLabe);

				CurrencyBox calBox = (CurrencyBox) listitem.getFellowIfAny("LoanAmount_" + topUpLabe);
				calBox.setValue(BigDecimal.ZERO);
				Decimalbox roi = (Decimalbox) listitem.getFellowIfAny("ROI_" + topUpLabe);
				roi.setValue(BigDecimal.ZERO);
				Intbox compon = (Intbox) listitem.getFellowIfAny("Tenure_" + topUpLabe);
				compon.setValue(0);
				readOnlyComponent(true, calBox);
				readOnlyComponent(true, roi);
				readOnlyComponent(true, compon);
				if (!loans.isNewRecord()) {
					loans.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRenderItems(String finType, FinScheduleData topUpFinSchdData, boolean isnewRecord) {
		logger.debug(Literal.ENTERING);

		if (topUpFinType.isEmpty()) {
			MessageUtil.showError("No TopUp Loan Type was configured for " + finType);
			topUpCount--;
			return;
		}

		FinanceMain parentFm = topUpFinSchdData.getFinanceMain();

		FinanceType financeType = topUpFinType.get(finType);

		FinanceMain fm = new FinanceMain();

		financeDetailService.setDefaultFinanceMain(fm, financeType);

		fm.setCustID(parentFm.getCustID());
		fm.setFinBranch(parentFm.getFinBranch());

		appendTopUpBasicDetails(fm, isnewRecord, topUpCount, financeType);

		List<FinFeeDetail> finFeeDetail = convertToFinanceFees(financeType.getFinTypeFeesList());
		appendTopUpChargeDetails(finFeeDetail, true, topUpCount);
		appendVasDetails(financeType.getFinTypeVASProductsList(), topUpCount, true);

		logger.debug(Literal.LEAVING);
	}

	private void appendVasDetails(List<FinTypeVASProducts> topUpFinTypeVasList, int count, boolean isNewRecord) {
		logger.debug(Literal.ENTERING);

		boolean readOnly = isReadonly();
		Listcell lc = null;
		Listitem item = null;
		String topup_label = "";

		for (int i = 0; i <= listBoxVasDetail.getItems().size(); i++) {
			item = listBoxVasDetail.getItemAtIndex(i);
			Listcell lc1 = new Listcell();
			lc1.setParent(item);

			Listcell lc2 = new Listcell();
			lc2.setParent(item);

			if (i == 0) {
				Auxhead auxHead = new Auxhead();
				auxHead.setId("auxHead_VAS_" + topUpCount);
				auxHead.setDraggable("false");

				Auxheader auxHeader = new Auxheader("");
				auxHeader.setColspan(1);
				auxHeader.setStyle("font-size: 14px");
				auxHeader.setParent(auxHead);
				auxHeader.setAlign("center");

				Listheader listHeader = new Listheader();
				listHeader.setStyle("font-size: 12px");
				listHeader.setHflex("min");
				topup_label = "TopUp_" + topUpCount;
				listHeader.setLabel(topup_label);
				listHeader.setParent(listBoxVasDetail.getListhead());

				listHeader = new Listheader();
				listHeader.setStyle("font-size: 12px");
				listHeader.setHflex("min");
				listHeader.setLabel("Mode Of Collection");
				listHeader.setParent(listBoxVasDetail.getListhead());

				auxHead.setParent(listBoxVasDetail);
			}

		}

		Listitem item1 = null;
		int cellCount = 2 * count;
		for (int k = 0; k < topUpFinTypeVasList.size(); k++) {

			FinTypeVASProducts vasProducts = topUpFinTypeVasList.get(k);
			item1 = new Listitem();
			item1.setAttribute("TopUp", count);
			String feeTypeDesc = vasProducts.getVasProduct();
			lc = new Listcell(feeTypeDesc);
			lc.setParent(item1);

			for (int i = 0; i < cellCount; i++) {
				lc = new Listcell();
				lc.setParent(item1);
			}

			Decimalbox calBox = new Decimalbox();
			vasProducts.setNewRecord(isNewRecord);
			calBox.setId(vasProducts.getVasProduct() + "_" + topup_label);
			calBox.setWidth("85px");
			calBox.setMaxlength(18);
			calBox.setFormat(PennantApplicationUtil.getAmountFormate(2));

			calBox.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
			calBox.setAttribute("newRecord", isNewRecord);
			calBox.setReadonly(readOnly);
			lc = new Listcell();
			lc.appendChild(calBox);
			lc.setParent(item1);
			item1.setAttribute("Vas" + topup_label, vasProducts);
			item1.setParent(listBoxVasDetail);

			String excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + "," + ","
					+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + "," + ","
					+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ",";

			Combobox modeOfColl = new Combobox();
			modeOfColl.setId(vasProducts.getVasProduct() + "_MOC_" + topup_label);
			fillComboBox(modeOfColl, "", PennantStaticListUtil.getRemFeeSchdMethods(), excludeFields);
			modeOfColl.setDisabled(readOnly);
			lc = new Listcell();
			lc.appendChild(modeOfColl);
			lc.setParent(item1);
			item1.setAttribute("VasDetail", vasProducts);
			item1.setAttribute("Vas_" + topup_label, vasProducts);
			item1.setAttribute("TopUpCount_", topup_label);
			item1.setParent(listBoxVasDetail);
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isReadonly() {
		if (splitted || StringUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinanceMain().getParentRef())) {
			return true;
		}
		return false;
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

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public PricingDetailService getPricingDetailService() {
		return pricingDetailService;
	}

	public void setPricingDetailService(PricingDetailService pricingDetailService) {
		this.pricingDetailService = pricingDetailService;
	}

	public boolean isNewFinance() {
		return isNewFinance;
	}

	public void setNewFinance(boolean isNewFinance) {
		this.isNewFinance = isNewFinance;
	}

	public PricingDetail getPricingDetail() {
		return pricingDetail;
	}

	public void setPricingDetail(PricingDetail pricingDetail) {
		this.pricingDetail = pricingDetail;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public CreditReviewData getCreditReviewData() {
		return creditReviewData;
	}

	public void setCreditReviewData(CreditReviewData creditReviewData) {
		this.creditReviewData = creditReviewData;
	}

	public Map<String, FinanceType> getTopUpFinType() {
		return topUpFinType;
	}

	public void setTopUpFinType(Map<String, FinanceType> topUpFinType) {
		this.topUpFinType = topUpFinType;
	}

}
