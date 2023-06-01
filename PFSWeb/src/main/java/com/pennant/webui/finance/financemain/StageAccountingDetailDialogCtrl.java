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
 * * FileName : FinanceMainDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.TransactionDetail;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class StageAccountingDetailDialogCtrl extends GFCBaseCtrl<ReturnDataSet> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(StageAccountingDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_StageAccountingDetailsDialog; // autoWired

	// Stage Accounting Details Tab

	protected Button btnStageAccounting; // autoWired
	protected Label label_StageAccountingDisbCrVal; // autoWired
	protected Label label_StageAccountingDisbDrVal; // autoWired
	protected Label label_StageAccountingDisbSummaryVal; // autoWired
	protected Listbox listBoxFinStageAccountings; // autoWired

	protected BigDecimal stageDisbCrSum = BigDecimal.ZERO;
	protected BigDecimal stageDisbDrSum = BigDecimal.ZERO;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;

	// not auto wired variables
	private FinanceDetail financeDetail = null;
	private FinScheduleData finScheduleData = null;
	private FinanceMain financeMain = null;
	private Object financeMainDialogCtrl = null;

	private AEEvent aeEvent; // over handed per parameters
	protected boolean stageAccountingsExecuted = false;

	private Label label_PostAccountingDisbCrVal; // autoWired
	private Label label_PostAccountingDisbDrVal; // autoWired
	protected Listbox listBoxPostAccountings; // autoWired
	protected Tab accountDetails;
	protected Tab postAccountDetails;
	protected Tabpanel postAccountingtab;

	private transient BigDecimal disbCrSum = BigDecimal.ZERO;
	private transient BigDecimal disbDrSum = BigDecimal.ZERO;
	private PagedListService pagedListService;
	public List<ReturnDataSet> postingAccountSet = null;

	// Bean Setters by application Context
	private AccountEngineExecution engineExecution;

	/**
	 * default constructor.<br>
	 */
	public StageAccountingDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_StageAccountingDetailsDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_StageAccountingDetailsDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		getBorderLayoutHeight();
		this.listBoxFinStageAccountings.setHeight(this.borderLayoutHeight - 220 + "px");
		this.listBoxPostAccountings.setHeight(this.borderLayoutHeight - 280 + "px");
		this.window_StageAccountingDetailsDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		// Calling method to add asset, checklist and additionaldetails tabs
		doShowDialog(this.financeDetail);

		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) {
		logger.debug("Entering");
		try {

			// append finance basic details
			appendFinBasicDetails();

			// fill the components with the data
			dofillStageAccountingSetbox(afinanceDetail.getStageTransactionEntries());

			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setStageAccountingDetailDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Stage Accounting Details List
	 * 
	 * @param accountingSetEntries
	 * @param listbox
	 */
	public void dofillStageAccountingSetbox(List<?> accountingSetEntries) {
		logger.debug("Entering");

		stageDisbCrSum = BigDecimal.ZERO;
		stageDisbDrSum = BigDecimal.ZERO;

		int formatter = CurrencyUtil.getFormat(getFinanceMain().getFinCcy());

		this.listBoxFinStageAccountings.setSizedByContent(true);
		this.listBoxFinStageAccountings.getItems().clear();

		if (accountingSetEntries != null) {

			long grpid = 0;
			for (int i = 0; i < accountingSetEntries.size(); i++) {
				Listitem item = new Listitem();
				Listcell lc;

				if (accountingSetEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) accountingSetEntries.get(i);

					if (grpid == 0 || entry.getAccountSetid() != grpid) {
						Listgroup listgroup = new Listgroup();
						listgroup.setLabel(entry.getLovDescAccSetCodeName());
						grpid = entry.getAccountSetid();
						this.listBoxFinStageAccountings.appendChild(listgroup);
					}

					lc = new Listcell(PennantApplicationUtil.getLabelDesc(entry.getDebitcredit(),
							PennantStaticListUtil.getTranType()));

					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);

					if (grpid == 0 || entry.getAccSetId() != grpid) {
						Listgroup listgroup = new Listgroup();
						listgroup.setLabel(entry.getAccSetCodeName());
						grpid = entry.getAccSetId();
						this.listBoxFinStageAccountings.appendChild(listgroup);
					}

					Hbox hbox = new Hbox();
					Label label = new Label(PennantApplicationUtil.getLabelDesc(entry.getDrOrCr(),
							PennantStaticListUtil.getTranType()));
					hbox.appendChild(label);
					if (StringUtils.isNotBlank(entry.getPostStatus())) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setParent(item);
					if (entry.isShadowPosting()) {
						lc = new Listcell("Shadow");
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setParent(item);
					} else {
						lc = new Listcell(entry.getTranCode());
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setParent(item);
					}
					lc = new Listcell(entry.getAccountType());
					lc.setParent(item);
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
					lc.setStyle("font-weight:bold;");
					lc.setParent(item);
					BigDecimal amt = new BigDecimal(entry.getPostAmount().toString()).setScale(0, RoundingMode.FLOOR);
					lc = new Listcell(CurrencyUtil.format(amt, formatter));

					if (entry.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
						stageDisbCrSum = stageDisbCrSum.add(amt);
					} else if (entry.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)) {
						stageDisbDrSum = stageDisbDrSum.add(amt);
					}

					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(
							"0000".equals(StringUtils.trimToEmpty(entry.getErrorId())) ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
					stageAccountingsExecuted = true;
				}
				this.listBoxFinStageAccountings.appendChild(item);
			}

			this.label_StageAccountingDisbCrVal.setValue(CurrencyUtil.format(stageDisbCrSum, formatter));
			this.label_StageAccountingDisbDrVal.setValue(CurrencyUtil.format(stageDisbDrSum, formatter));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Stage Accounting Details List
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnStageAccounting(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		validateFinanceDetail();

		doSetStageAccounting();
		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("unchecked")
	public void doSetStageAccounting() {

		if (StringUtils.trimToEmpty(getFinanceMain().getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)) {
			getFinanceMain().setCurDisbursementAmt(getFinanceMain().getFinAmount());
		}

		aeEvent = AEAmounts.procAEAmounts(getFinanceMain(), getFinScheduleData().getFinanceScheduleDetails(),
				new FinanceProfitDetail(), AccountingEvent.STAGE, getFinanceMain().getFinStartDate(),
				getFinanceMain().getFinStartDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		aeEvent.setModuleDefiner(getFinanceDetail().getModuleDefiner());

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		// Fee Rules Map Fetching from Fee Details Dialog Controller for Accounting process
		Map<String, FeeRule> feeRuleDetailMap = null;
		try {
			feeRuleDetailMap = (Map<String, FeeRule>) getFinanceMainDialogCtrl().getClass()
					.getMethod("getFeeRuleDetailMap").invoke(getFinanceMainDialogCtrl());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			feeRuleDetailMap = null;
		}

		dataMap.putAll(feeRuleDetailMap);
		aeEvent.setDataMap(dataMap);
		engineExecution.getAccEngineExecResults(aeEvent);

		List<ReturnDataSet> accountingSetEntries = aeEvent.getReturnDataSet();

		getFinanceDetail().setStageAccountingList(accountingSetEntries);
		dofillStageAccountingSetbox(accountingSetEntries);
	}

	/**
	 * Validate basic Finance Details
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 **/
	public void validateFinanceDetail()
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		setFinanceDetail((FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("onExecuteStageAccDetail")
				.invoke(getFinanceMainDialogCtrl()));

	}

	/**
	 * when the "btnPrintStageAccounting" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPrintStageAccounting(Event event) {
		logger.debug("Entering" + event.toString());
		String usrName = getUserWorkspace().getUserDetails().getUsername();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		List<Object> list = null;

		if (stageAccountingsExecuted) {
			list = new ArrayList<Object>();
			List<TransactionDetail> accountingDetails = new ArrayList<TransactionDetail>();
			for (ReturnDataSet dataSet : getFinanceDetail().getStageAccountingList()) {
				TransactionDetail detail = new TransactionDetail();
				detail.setEventCode(dataSet.getFinEvent());
				detail.setEventDesc(dataSet.getLovDescEventCodeName());
				detail.setTranType(dataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT) ? "Credit" : "Debit");
				detail.setTransactionCode(dataSet.getTranCode());
				detail.setTransDesc(dataSet.getTranDesc());
				detail.setCcy(dataSet.getAcCcy());
				detail.setAccount(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
				detail.setPostAmount(CurrencyUtil.format(dataSet.getPostAmount(),
						dataSet.getFormatter() == 0 ? CurrencyUtil.getFormat(getFinanceMain().getFinCcy())
								: dataSet.getFormatter()));
				accountingDetails.add(detail);
			}

			Window window = (Window) this.window_StageAccountingDetailsDialog.getParent().getParent().getParent()
					.getParent().getParent().getParent();
			if (!accountingDetails.isEmpty()) {
				list.add(accountingDetails);
			}

			ReportsUtil.generatePDF("FINENQ_AccountingDetail", financeMain, list, usrName, window);
		} else {
			MessageUtil.showError(Labels.getLabel("btnPrintStageAccounting.Error_Message"));
		}
		logger.debug("Leaving" + event.toString());
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

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * Method to fill list box in Accounting Tab <br>
	 * 
	 * @param postingAccountingset (List)
	 * 
	 */
	public void doFillPostAccountings(List<ReturnDataSet> postingAccountingset) {
		logger.debug("Entering");

		setDisbCrSum(BigDecimal.ZERO);
		setDisbDrSum(BigDecimal.ZERO);

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		this.listBoxPostAccountings.getItems().clear();
		this.listBoxPostAccountings.setSizedByContent(true);
		if (postingAccountingset != null && !postingAccountingset.isEmpty()) {
			for (int i = 0; i < postingAccountingset.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;
				ReturnDataSet postAccountSet = (ReturnDataSet) postingAccountingset.get(i);

				// Highlighting Failed Posting Details
				String sClassStyle = "";
				if (StringUtils.isNotBlank(postAccountSet.getErrorId())
						&& !"0000".equals(StringUtils.trimToEmpty(postAccountSet.getErrorId()))) {
					sClassStyle = "color:#FF0000;";
				}
				Hbox hbox = new Hbox();
				Label label = new Label(PennantApplicationUtil.getLabelDesc(postAccountSet.getDrOrCr(),
						PennantStaticListUtil.getTranType()));
				label.setStyle(sClassStyle);
				hbox.appendChild(label);
				lc = new Listcell();
				lc.setStyle(sClassStyle);
				lc.appendChild(hbox);
				lc.setParent(item);
				lc = new Listcell(postAccountSet.getTranDesc());
				lc.setStyle(sClassStyle);
				lc.setParent(item);
				if (postAccountSet.isShadowPosting()) {
					lc = new Listcell("Shadow");
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell("Shadow");
					lc.setStyle(sClassStyle);
					lc.setParent(item);
				} else {
					lc = new Listcell(postAccountSet.getTranCode());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell(postAccountSet.getRevTranCode());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
				}
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(postAccountSet.getAccount()));
				lc.setStyle("font-weight:bold;");
				lc.setStyle(sClassStyle);
				lc.setParent(item);

				lc = new Listcell(postAccountSet.getAcCcy());
				lc.setParent(item);

				BigDecimal amt = postAccountSet.getPostAmount() != null ? postAccountSet.getPostAmount()
						: BigDecimal.ZERO;

				if (postAccountSet.getAcCcy()
						.equals(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())) {
					lc = new Listcell(PennantApplicationUtil.amountFormate(amt, formatter));

					if (postAccountSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
						setDisbCrSum(getDisbCrSum().add(amt));
					} else if (postAccountSet.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)) {
						setDisbDrSum(getDisbDrSum().add(amt));
					}

				} else {
					lc = new Listcell(PennantApplicationUtil.amountFormate(amt, postAccountSet.getFormatter()));
				}

				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setStyle(sClassStyle + "font-weight:bold;text-align:right;");
				lc.setParent(item);
				lc = new Listcell("0000".equals(StringUtils.trimToEmpty(postAccountSet.getErrorId())) ? ""
						: StringUtils.trimToEmpty(postAccountSet.getErrorId()));
				lc.setStyle("font-weight:bold;color:red;");
				lc.setTooltiptext(postAccountSet.getErrorMsg());
				lc.setParent(item);
				this.listBoxPostAccountings.appendChild(item);
			}

			this.getLabel_PostAccountingDisbCrVal()
					.setValue(PennantApplicationUtil.amountFormate(getDisbCrSum(), formatter));
			this.getLabel_PostAccountingDisbDrVal().setValue(CurrencyUtil.format(getDisbDrSum(), formatter));
		}
		logger.debug("Leaving");
	}

	/*
	 * Method to Get PostingAccount Details
	 */

	public List<ReturnDataSet> getPostingAccount(String finReference) {
		logger.debug("Entering");

		List<ReturnDataSet> postingAccount = new ArrayList<ReturnDataSet>();
		JdbcSearchObject<ReturnDataSet> searchObject = new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		searchObject.addTabelName("Postings_view");
		searchObject.addFilterEqual("finreference", finReference);
		List<ReturnDataSet> postings = pagedListService.getBySearchObject(searchObject);
		if (postings != null && !postings.isEmpty()) {
			return postings;
		}

		logger.debug("Leaving");
		return postingAccount;

	}

	public void onSelect$postAccountDetails(Event event) {
		logger.debug("Entering");
		if (postingAccountSet != null && !postingAccountSet.isEmpty()) {
			return;
		} else {
			List<ReturnDataSet> postingaccount = getPostingAccount(
					getFinanceDetail().getFinScheduleData().getFinReference());
			if (postingaccount != null && !postingaccount.isEmpty()) {
				doFillPostAccountings(postingaccount);
				postingAccountSet = postingaccount;
			}
			logger.debug("Leaving");
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public BigDecimal getStageDisbCrSum() {
		return stageDisbCrSum;
	}

	public void setStageDisbCrSum(BigDecimal stageDisbCrSum) {
		this.stageDisbCrSum = stageDisbCrSum;
	}

	public BigDecimal getStageDisbDrSum() {
		return stageDisbDrSum;
	}

	public void setStageDisbDrSum(BigDecimal stageDisbDrSum) {
		this.stageDisbDrSum = stageDisbDrSum;
	}

	public BigDecimal getDisbDrSum() {
		return disbDrSum;
	}

	public void setDisbDrSum(BigDecimal disbDrSum) {
		this.disbDrSum = disbDrSum;
	}

	public BigDecimal getDisbCrSum() {
		return disbCrSum;
	}

	public void setDisbCrSum(BigDecimal disbCrSum) {
		this.disbCrSum = disbCrSum;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public Label getLabel_PostAccountingDisbCrVal() {
		return label_PostAccountingDisbCrVal;
	}

	public void setLabel_PostAccountingDisbCrVal(Label label_PostAccountingDisbCrVal) {
		this.label_PostAccountingDisbCrVal = label_PostAccountingDisbCrVal;
	}

	public Label getLabel_PostAccountingDisbDrVal() {
		return label_PostAccountingDisbDrVal;
	}

	public void setLabel_PostAccountingDisbDrVal(Label label_PostAccountingDisbDrVal) {
		this.label_PostAccountingDisbDrVal = label_PostAccountingDisbDrVal;
	}

	public boolean isStageAccountingsExecuted() {
		return stageAccountingsExecuted;
	}

	public void setStageAccountingsExecuted(boolean stageAccountingsExecuted) {
		this.stageAccountingsExecuted = stageAccountingsExecuted;
	}
}