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
 * FileName    		:  ScheduleEnquiryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class ScheduleEnquiryDialogCtrl extends GFCBaseCtrl<FinanceScheduleDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(ScheduleEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ScheduleEnquiryDialog; 		
	protected Listbox 		listBoxSchedule;
	
	protected Borderlayout  borderlayoutScheduleEnquiry;		
	private Tabpanel 		tabPanel_dialogWindow;
	protected Tab 			repayGraphTab;
	private Tabpanel 		tabpanel_graph;
	protected Div           graphDivTabDiv;
	// Step Finance Fields
	protected Listheader    listheader_SchFee;
	protected Listheader    listHeader_cashFlowEffect;
	protected Listheader    listHeader_vSProfit;
	protected Listheader    listHeader_orgPrincipalDue;
	protected Listheader    listheader_SupplementRent;
	protected Listheader    listheader_IncreasedCost;
	protected Listheader    listheader_SchAdvProfit;
	protected Listheader    listheader_AdvTotal;
	protected Listheader    listheader_Rebate;
	protected Listheader    listheader_Total;
	protected Listheader 	listheader_TDSAmount;
	
	// Overdraft Details Headers
	protected Listheader listheader_LimitChange;
	protected Listheader listheader_AvailableLimit;
	protected Listheader listheader_ODLimit;
	
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private FinScheduleData finScheduleData;
	private FinScheduleListItemRenderer finRender;
	private FinanceScheduleDetail prvSchDetail;
	private int formatter;
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
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScheduleEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScheduleEnquiryDialog);

		if(event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
			setFinScheduleData(finScheduleData);
		}else{
			setFinScheduleData(null);
		}
		
		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments.get("financeEnquiryHeaderDialogCtrl");
		}
		doShowDialog();
		
		//Schedule Fee Column Visibility Check
		boolean isSchdFee = false;
		List<FinFeeDetail> finFeeDetailList = getFinScheduleData().getFinFeeDetailList();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if(!StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE) &&
					!StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
				isSchdFee = true;
				break;
			}
		}
 
		if (isSchdFee) {
			this.listheader_SchFee.setVisible(true);
		} else {
			this.listheader_SchFee.setVisible(false);
		}
		
		this.listHeader_cashFlowEffect.setVisible(false);
		this.listHeader_vSProfit.setVisible(false);
		this.listHeader_orgPrincipalDue.setVisible(false);
		if(getFinScheduleData().getFinanceMain().isStepFinance()){
			if(getFinScheduleData().getFinanceMain().isAlwManualSteps()){  
				this.listHeader_cashFlowEffect.setLabel(Labels.getLabel("listheader_sellingPricePft.label"));
				this.listHeader_vSProfit.setLabel(Labels.getLabel("listheader_rebateBucket.label"));
			}
		} 
			
		String product = getFinScheduleData().getFinanceType().getFinCategory();
		if (StringUtils.equals(product,FinanceConstants.PRODUCT_STRUCTMUR)) {
			this.listheader_SchAdvProfit.setVisible(true);
			this.listheader_AdvTotal.setVisible(true);
			this.listheader_Rebate.setVisible(true);
			this.listheader_Total.setVisible(false);
		}else if ((StringUtils.equals(product,FinanceConstants.PRODUCT_IJARAH) || StringUtils.equals(product,FinanceConstants.PRODUCT_FWIJARAH))||
				StringUtils.equals(product,FinanceConstants.PRODUCT_ISTISNA)) {
			this.listheader_SupplementRent.setVisible(true);
			this.listheader_IncreasedCost.setVisible(true);
		}
		
		if(!getFinScheduleData().getFinanceMain().isTDSApplicable()){
			this.listheader_TDSAmount.setVisible(false);
		}else{
			this.listheader_TDSAmount.setVisible(true);
		}
		
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, getFinScheduleData().getFinanceMain().getProductCategory())){
			this.listheader_TDSAmount.setVisible(false);
			this.listheader_AvailableLimit.setVisible(true);
			this.listheader_ODLimit.setVisible(true);
			if(getFinScheduleData().getFinanceType().isDroplineOD()){
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
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
			if (getFinScheduleData().getFinanceScheduleDetails()!=null) {
				this.repayGraphTab.setVisible(true);
				graphDivTabDiv = new Div();
				this.graphDivTabDiv.setStyle("overflow:auto");
				tabpanel_graph.appendChild(graphDivTabDiv);
				doShowReportChart();
			}
			
			// fill the components with the data
			doFillScheduleList(this.finScheduleData);
			
			if(tabPanel_dialogWindow != null){
				
				getBorderLayoutHeight();
				if(financeEnquiryHeaderDialogCtrl != null){
					int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
					this.listBoxSchedule.setHeight(this.borderLayoutHeight-rowsHeight-100+"px");
					this.window_ScheduleEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-30+"px");
				}else{
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
	 * Method to fill the ScheduleList
	 * 
	 * @param FinanceDetail
	 *            (aFinanceDetail)
	 */
	public void doFillScheduleList(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finRender = new FinScheduleListItemRenderer();
		if (finScheduleData.getFinanceScheduleDetails() != null) {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();
			
			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
			if(finScheduleData.getRepayDetails() != null && finScheduleData.getRepayDetails().size() > 0){
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : finScheduleData.getRepayDetails()) {
					if(rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())){
						ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}else{
						ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}
				}
			}
			
			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
			if(finScheduleData.getPenaltyDetails() != null && finScheduleData.getPenaltyDetails().size() > 0){
				penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

				for (OverdueChargeRecovery penaltyDetail : finScheduleData.getPenaltyDetails()) {
					if(penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())){
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap.get(penaltyDetail.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}else{
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}
			
			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			BigDecimal totalAdvPft = BigDecimal.ZERO;

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				boolean showRate = false;
				boolean showAdvRate = false;
				if (i == 0) {
					prvSchDetail = curSchd;
					showRate = true;
					
					if (finScheduleData.getFinanceType().getFinCategory().equals(FinanceConstants.PRODUCT_STRUCTMUR)) {
						showAdvRate = true;
					}
				} else {
					prvSchDetail = finScheduleData.getFinanceScheduleDetails().get(i - 1);
					if(curSchd.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
					if(curSchd.getAdvCalRate().compareTo(prvSchDetail.getAdvCalRate())!=0){
						showAdvRate = true;
					}
				}
				
				//Preparing Total Advance Profit Amount
				totalAdvPft = totalAdvPft.add(curSchd.getAdvProfit());

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", finScheduleData);
				map.put("financeScheduleDetail", curSchd);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("penaltyDetailsMap", penaltyDetailsMap);
				map.put("accrueValue", finScheduleData.getAccrueValue());
				map.put("window", this.window_ScheduleEnquiryDialog);
				map.put("totalAdvPft", totalAdvPft);
				map.put("showAdvRate", showAdvRate);
				
				finRender.render(map, prvSchDetail, false, false, true, finScheduleData.getFinFeeDetailList(), showRate,false);
				if(i == sdSize-1){						
					finRender.render(map, prvSchDetail, true, false,true, finScheduleData.getFinFeeDetailList(), showRate, false);				
					break;
				}
			}
		}
		logger.debug("Leaving");
	}
	
	private List<FinanceScheduleDetail> sortSchdDetails(
	        List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {

				@Override
				public int compare(FinanceScheduleDetail detail1,FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}
	
	/** =========================================================*/
	/** 			Graph Report Preparation					 */
	/** =========================================================*/
	
	public void doShowReportChart() {
		logger.debug("Entering ");
		
		DashboardConfiguration aDashboardConfiguration=new DashboardConfiguration();
		ChartDetail chartDetail=new ChartDetail();
		ChartUtil chartUtil=new ChartUtil();
		
		//For Finance Vs Amounts Chart 
		List<ChartSetElement> listChartSetElement= getReportDataForFinVsAmount();

		ChartsConfig  chartsConfig=new ChartsConfig("Loan Vs Amounts","Loan Amount ="
				+PennantAppUtil.amountFormate(getFinScheduleData().getFinanceMain().getFinAmount(), formatter),"","");
		aDashboardConfiguration=new DashboardConfiguration();
		chartsConfig.setSetElements(listChartSetElement);
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		chartsConfig.setRemarks("pieRadius='90' startingAngle='310'" +
				"formatNumberScale='0'enableRotation='1'  forceDecimals='1'  decimals='"+formatter+"'");
		String chartStrXML=chartsConfig.getChartXML();
		chartDetail=new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("Pie3D.swf");
		chartDetail.setChartHeight("160");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");

		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));
		
		//For Repayments Chart 
		chartsConfig=new ChartsConfig("Repayments","","","");
		chartsConfig.setSetElements(getReportDataForRepayments());
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		chartsConfig.setRemarks("labelDisplay='ROTATE' formatNumberScale='0'" +
				"rotateValues='0' startingAngle='310' showValues='0' forceDecimals='1' skipOverlapLabels='0'  decimals='"+formatter+"'");
		chartStrXML = chartsConfig.getSeriesChartXML(aDashboardConfiguration.getRenderAs());

		chartDetail=new ChartDetail();
		chartDetail.setChartId("form_Repayments");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("MSLine.swf");
		chartDetail.setChartHeight("270");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("320px");
		chartDetail.setiFrameWidth("95%");

		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));
		
		logger.debug("Leaving ");
	}
	
	public List<ChartSetElement> getReportDataForFinVsAmount(){

		BigDecimal downPayment= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal scheduleProfit= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP); 
		BigDecimal schedulePrincipal= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail= getFinScheduleData().getFinanceScheduleDetails();
		if(listScheduleDetail!=null){
			ChartSetElement chartSetElement;
			
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				downPayment=downPayment.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getDownPaymentAmount(), formatter));
				capitalized=capitalized.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getCpzAmount(), formatter));

				scheduleProfit=scheduleProfit.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getProfitSchd(),formatter));
				schedulePrincipal=schedulePrincipal.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getPrincipalSchd(), formatter));

			}
			
			chartSetElement=new ChartSetElement("DownPayment",downPayment);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("Capitalized",capitalized);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("Schedule interest",scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("Schedule Principal",schedulePrincipal);
			listChartSetElement.add(chartSetElement);
		}
		
		return listChartSetElement;
	}
	
	/**
	 * This method returns data for Repayments Chart
	 * @return
	 */
	public List<ChartSetElement> getReportDataForRepayments(){
		logger.debug("Entering ");

		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail= getFinScheduleData().getFinanceScheduleDetails();
		ChartSetElement chartSetElement;
		if(listScheduleDetail!=null){
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if(curSchd.isRepayOnSchDate() ||
						(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)){
					chartSetElement=new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),"RepayAmount",
							PennantAppUtil.formateAmount(curSchd.getRepayAmount(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if(curSchd.isRepayOnSchDate() || 
						(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)){
					chartSetElement=new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),"PrincipalSchd",
							PennantAppUtil.formateAmount(curSchd.getPrincipalSchd(), formatter).setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if(curSchd.isRepayOnSchDate() ||
						(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)){
					chartSetElement=new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),"ProfitSchd",
							PennantAppUtil.formateAmount(curSchd.getProfitSchd(),formatter).setScale(formatter, RoundingMode.HALF_UP));
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
	
}
