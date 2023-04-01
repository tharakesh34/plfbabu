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
 * * FileName : ScheduleEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * 30-04-2018 Vinay 0.2 As Discussed with Raju and Siva, * IRR Code calculation functionality
 * * implemented. * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.ChartType;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class ScheduleEnquiryDialogCtrl extends GFCBaseCtrl<FinanceScheduleDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(ScheduleEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ScheduleEnquiryDialog;
	protected Listbox listBoxSchedule;
	protected Listbox iRRListBox; // autoWired

	protected Borderlayout borderlayoutScheduleEnquiry;
	private Tabpanel tabPanel_dialogWindow;
	protected Tab repayGraphTab;
	// ####_0.2
	protected Tab irrDetailsTab; // autoWired
	private Tabpanel tabpanel_graph;
	// Step Finance Fields
	protected Listheader listheader_SchFee;
	protected Listheader listheader_SchTax;
	protected Listheader listHeader_orgPrincipalDue;
	protected Listheader listheader_Total;
	protected Listheader listheader_TDSAmount;

	// Overdraft Details Headers
	protected Listheader listheader_LimitChange;
	protected Listheader listheader_AvailableLimit;
	protected Listheader listheader_ODLimit;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private FinScheduleData finScheduleData;
	private FinScheduleListItemRenderer finRender;
	private FinanceScheduleDetail prvSchDetail;
	private int formatter;
	private List<ChartDetail> chartDetailList = new ArrayList<ChartDetail>(); // storing ChartDetail for feature use
	private boolean chartReportLoaded;

	/**
	 * default constructor.<br>
	 */
	public ScheduleEnquiryDialogCtrl() {
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
	public void onCreate$window_ScheduleEnquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScheduleEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
			setFinScheduleData(finScheduleData);
		} else {
			setFinScheduleData(null);
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}
		doShowDialog();

		// Schedule Fee Column Visibility Check
		boolean isSchdFee = false;
		List<FinFeeDetail> finFeeDetailList = getFinScheduleData().getFinFeeDetailList();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (!StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				isSchdFee = true;
				break;
			}
		}

		if (isSchdFee) {
			this.listheader_SchFee.setVisible(true);
			this.listheader_SchTax.setVisible(true);
		} else {
			this.listheader_SchFee.setVisible(false);
			this.listheader_SchTax.setVisible(false);
		}

		this.listHeader_orgPrincipalDue.setVisible(false);

		this.listheader_TDSAmount.setVisible(TDSCalculator.isTDSApplicable(getFinScheduleData().getFinanceMain()));

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinScheduleData().getFinanceMain().getProductCategory())) {
			this.listheader_TDSAmount.setVisible(false);
			this.listheader_AvailableLimit.setVisible(true);
			this.listheader_ODLimit.setVisible(true);
			if (getFinScheduleData().getFinanceType().isDroplineOD()) {
				this.listheader_LimitChange.setVisible(true);
				listheader_LimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitChange"));
			}
			listheader_AvailableLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_AvailableLimit"));
			listheader_ODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ODLimit"));
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		try {
			formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
			if (getFinScheduleData().getFinanceScheduleDetails() != null) {
				this.repayGraphTab.setVisible(true);
			}

			// fill the components with the data
			doFillScheduleList(this.finScheduleData);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				if (financeEnquiryHeaderDialogCtrl != null) {
					int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()
							* 20;
					this.listBoxSchedule.setHeight(this.borderLayoutHeight - rowsHeight - 100 + "px");
					this.window_ScheduleEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				} else {
					this.window_ScheduleEnquiryDialog.setStyle("padding-top:0px");
				}
				tabPanel_dialogWindow.appendChild(this.window_ScheduleEnquiryDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering IRR Details
	 */
	// ####_0.2
	public void doFillIrrDetails(List<FinIRRDetails> irrCodeNvalueList) {
		if (!irrCodeNvalueList.isEmpty()) {
			this.iRRListBox.getItems().clear();
			this.irrDetailsTab.setVisible(true);
		} else {
			this.irrDetailsTab.setVisible(false);
		}

		for (FinIRRDetails ResultOfIRRFeeType : irrCodeNvalueList) {
			Listitem listitem = new Listitem();
			Listcell irrcode = new Listcell();
			Listcell irrdesc = new Listcell();
			Listcell calcamountWithFee = new Listcell();

			irrcode.setLabel(ResultOfIRRFeeType.getiRRCode());
			irrdesc.setLabel(ResultOfIRRFeeType.getIrrCodeDesc());
			calcamountWithFee.setLabel(ResultOfIRRFeeType.getIRR().toString());

			listitem.appendChild(irrcode);
			listitem.appendChild(irrdesc);
			listitem.appendChild(calcamountWithFee);

			iRRListBox.setHeight(this.borderLayoutHeight - 142 + "px");

			iRRListBox.appendChild(listitem);
		}
	}

	/**
	 * Method to fill the ScheduleList
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 */
	public void doFillScheduleList(FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);

		finRender = new FinScheduleListItemRenderer();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		int advEMITerms = financeMain.getAdvTerms();

		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();

		if (CollectionUtils.isEmpty(schedules)) {
			return;
		}

		int sdSize = schedules.size();
		if (AdvanceType.hasAdvEMI(financeMain.getAdvType()) && AdvanceStage.hasFrontEnd(financeMain.getAdvStage())
				&& advEMITerms > 0) {
			sdSize = sdSize - advEMITerms;
		}
		// Clear all the list items in list box
		this.listBoxSchedule.getItems().clear();

		// Find Out Finance Repayment Details on Schedule
		Map<Date, List<FinanceRepayments>> rpyDetailsMap = null;
		List<FinanceRepayments> repayDetails = finScheduleData.getRepayDetails();
		if (CollectionUtils.isNotEmpty(repayDetails)) {
			rpyDetailsMap = new HashMap<Date, List<FinanceRepayments>>();

			for (FinanceRepayments rpyDetail : repayDetails) {
				if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
					List<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				} else {
					List<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				}
			}
		}

		// Find Out Finance Repayment Details on Schedule
		Map<Date, List<OverdueChargeRecovery>> penaltyDetailsMap = null;
		List<OverdueChargeRecovery> penaltyDetails = finScheduleData.getPenaltyDetails();
		if (CollectionUtils.isNotEmpty(penaltyDetails)) {
			penaltyDetailsMap = new HashMap<Date, List<OverdueChargeRecovery>>();

			for (OverdueChargeRecovery penaltyDetail : penaltyDetails) {
				if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
					List<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
							.get(penaltyDetail.getFinODSchdDate());
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				} else {
					List<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				}
			}
		}

		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(schedules));
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = schedules.get(i);
			boolean showRate = false;
			if (i == 0) {
				prvSchDetail = curSchd;
				showRate = true;

			} else {
				prvSchDetail = schedules.get(i - 1);
				if (curSchd.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
					showRate = true;
				}
			}
			// ####_0.2
			doFillIrrDetails(finScheduleData.getiRRDetails());

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("finSchdData", finScheduleData);
			map.put("financeScheduleDetail", curSchd);
			map.put("paymentDetailsMap", rpyDetailsMap);
			map.put("penaltyDetailsMap", penaltyDetailsMap);
			map.put("accrueValue", finScheduleData.getAccrueValue());
			map.put("window", this.window_ScheduleEnquiryDialog);
			map.put("formatter", formatter);

			finRender.render(map, prvSchDetail, false, false, true, finScheduleData.getFinFeeDetailList(), showRate,
					false);
			if (i == sdSize - 1) {
				finRender.render(map, prvSchDetail, true, false, true, finScheduleData.getFinFeeDetailList(), showRate,
						false);
				break;
			}

			// SubventionDetails
			if (finScheduleData.getFinanceMain().isAllowSubvention()) {
				String listBoxHeight = this.borderLayoutHeight - 270 + "px";
				if (CollectionUtils.isNotEmpty(finScheduleData.getDisbursementDetails())) {
					boolean subventionSchedule = false;
					for (FinanceDisbursement disbursement : finScheduleData.getDisbursementDetails()) {
						if (CollectionUtils.isNotEmpty(disbursement.getSubventionSchedules())) {
							subventionSchedule = true;
							break;
						}
					}
					if (subventionSchedule) {
						finRender.renderSubvention(finScheduleData, listBoxHeight);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {

				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/** ========================================================= */
	/** Graph Report Preparation */
	/** ========================================================= */

	public void doShowReportChart() {
		logger.debug("Entering ");

		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		ChartDetail chartDetail = new ChartDetail();
		// For Finance Vs Amounts Chart
		List<ChartSetElement> listChartSetElement = getReportDataForFinVsAmount();

		ChartsConfig chartsConfig = new ChartsConfig("Loan Vs Amounts",
				"Loan Amount =" + CurrencyUtil.format(getFinScheduleData().getFinanceMain().getFinAmount(), formatter),
				"", "");
		aDashboardConfiguration = new DashboardConfiguration();
		chartsConfig.setSetElements(listChartSetElement);
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		chartsConfig.setRemarks(ChartType.PIE3D.getRemarks() + " decimals='" + formatter + "'");
		String chartStrXML = chartsConfig.getChartXML();
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.PIE3D.toString());
		chartDetail.setChartHeight("180");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");
		chartDetailList.add(chartDetail);

		// For Repayments Chart
		chartsConfig = new ChartsConfig("Repayments", "", "", "");
		chartsConfig.setSetElements(getReportDataForRepayments());
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		chartsConfig.setRemarks(ChartType.MSLINE.getRemarks() + " decimals='" + formatter + "'");
		chartStrXML = chartsConfig.getSeriesChartXML(aDashboardConfiguration.getRenderAs());

		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_Repayments");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.MSLINE.toString());
		chartDetail.setChartHeight("270");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("320px");
		chartDetail.setiFrameWidth("95%");
		chartDetailList.add(chartDetail);

		logger.debug("Leaving ");
	}

	public List<ChartSetElement> getReportDataForFinVsAmount() {

		BigDecimal downPayment = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal scheduleProfit = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal schedulePrincipal = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = getFinScheduleData().getFinanceScheduleDetails();
		if (listScheduleDetail != null) {
			ChartSetElement chartSetElement;

			for (int i = 0; i < listScheduleDetail.size(); i++) {

				downPayment = downPayment
						.add(CurrencyUtil.parse(listScheduleDetail.get(i).getDownPaymentAmount(), formatter));
				capitalized = capitalized.add(CurrencyUtil.parse(listScheduleDetail.get(i).getCpzAmount(), formatter));

				scheduleProfit = scheduleProfit
						.add(CurrencyUtil.parse(listScheduleDetail.get(i).getProfitSchd(), formatter));
				schedulePrincipal = schedulePrincipal
						.add(CurrencyUtil.parse(listScheduleDetail.get(i).getPrincipalSchd(), formatter));

			}

			chartSetElement = new ChartSetElement("DownPayment", downPayment);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Capitalized", capitalized);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Schedule interest", scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Schedule Principal", schedulePrincipal);
			listChartSetElement.add(chartSetElement);
		}

		return listChartSetElement;
	}

	/**
	 * This method returns data for Repayments Chart
	 * 
	 * @return
	 */
	public List<ChartSetElement> getReportDataForRepayments() {
		logger.debug("Entering ");

		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = getFinScheduleData().getFinanceScheduleDetails();
		ChartSetElement chartSetElement;
		if (listScheduleDetail != null) {
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()),
							"RepayAmount", CurrencyUtil.parse(curSchd.getRepayAmount(), formatter).setScale(formatter,
									RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()),
							"PrincipalSchd", CurrencyUtil.parse(curSchd.getPrincipalSchd(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()),
							"ProfitSchd", CurrencyUtil.parse(curSchd.getProfitSchd(), formatter).setScale(formatter,
									RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);

				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	/** new code to display chart by skipping jsps code start */
	public void onSelect$repayGraphTab(Event event) throws InterruptedException {
		logger.debug("Entering");
		if (chartReportLoaded) {
			return;
		}
		doShowReportChart(); // new code to display charts on click repayGraphTab
		for (ChartDetail chartDetail : chartDetailList) {
			String strXML = chartDetail.getStrXML();
			strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
			strXML = StringEscapeUtils.escapeJavaScript(strXML);
			chartDetail.setStrXML(strXML);

			Executions.createComponents("/Charts/Chart.zul", tabpanel_graph,
					Collections.singletonMap("chartDetail", chartDetail));
		}
		chartReportLoaded = true;
		logger.debug("Leaving");
	}
	/** new code to display chart by skipping jsps code end */
}
