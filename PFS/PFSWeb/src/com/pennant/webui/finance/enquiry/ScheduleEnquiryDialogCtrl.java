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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ScheduleEnquiryDialogCtrl extends GFCBaseListCtrl<FinanceScheduleDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(ScheduleEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ScheduleEnquiryDialog; 		// autoWired
	protected Listbox 		listBoxSchedule; 					// autoWired
	protected Borderlayout  borderlayoutScheduleEnquiry;		// autoWired
	private Tabpanel 		tabPanel_dialogWindow;
	protected Tab 			repayGraphTab;
	private Tabpanel 		tabpanel_graph;
	protected Div           graphDivTabDiv;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private FinScheduleData finScheduleData;
	private FinScheduleListItemRenderer finRender;
	private boolean lastRec;
	private FinanceScheduleDetail prvSchDetail;
	private int formatter;
	
	/**
	 * default constructor.<br>
	 */
	public ScheduleEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScheduleEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) args.get("finScheduleData");
			setFinScheduleData(finScheduleData);
		}else{
			setFinScheduleData(null);
		}
		
		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
		}

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
			formatter = getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
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
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxSchedule.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.window_ScheduleEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-30+"px");
				tabPanel_dialogWindow.appendChild(this.window_ScheduleEnquiryDialog);

			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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

		lastRec = false;
		finRender = new FinScheduleListItemRenderer();
		if (finScheduleData.getFinanceScheduleDetails() != null) {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();
			
			Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
			
			if(finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0){
				feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
				
				for (FeeRule fee : finScheduleData.getFeeRules()) {
					if(feeChargesMap.containsKey(fee.getSchDate())){
						ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}else{
						ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}
				}
			}
			
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

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail aScheduleDetail = finScheduleData.getFinanceScheduleDetails().get(i);
				boolean showRate = false;
				if (i == 0) {
					prvSchDetail = aScheduleDetail;
					showRate = true;
				} else {
					prvSchDetail = finScheduleData.getFinanceScheduleDetails().get(i - 1);
					if(aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
				}

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", finScheduleData);
				if(finScheduleData.getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", finScheduleData.getDefermentMap().get(aScheduleDetail.getSchDate()));
				}else {
					map.put("defermentDetail", null);
				}
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("window", this.window_ScheduleEnquiryDialog);
				finRender.render(map, prvSchDetail, lastRec, false,true, feeChargesMap, showRate);

				if(i == sdSize-1){						
					lastRec = true;
					finRender.render(map, prvSchDetail, lastRec, false,true, feeChargesMap, showRate);				
					break;
				}
			}
		}
		logger.debug("Leaving");
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

		ChartsConfig  chartsConfig=new ChartsConfig("Finance Vs Amounts","FinanceAmount ="
				+PennantAppUtil.amountFormate(getFinScheduleData().getFinanceMain().getFinAmount(), formatter),"","");
		aDashboardConfiguration=new DashboardConfiguration();
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(listChartSetElement);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("pieRadius='90' startingAngle='310'" +
				"formatNumberScale='0'enableRotation='1'  forceDecimals='1'  decimals='"+formatter+"'");
		String chartStrXML=aDashboardConfiguration.getLovDescChartsConfig().getChartXML();
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
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(getReportDataForRepayments());
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("labelDisplay='ROTATE' formatNumberScale='0'" +
				"rotateValues='0' startingAngle='310' showValues='0' forceDecimals='1' skipOverlapLabels='0'  decimals='"+formatter+"'");
		chartStrXML=aDashboardConfiguration.getLovDescChartsConfig().getSeriesChartXML(aDashboardConfiguration.getRenderAs());

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

		BigDecimal downPayment= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);;
		BigDecimal scheduleProfit= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);; 
		BigDecimal schedulePrincipal= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);
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
			chartSetElement=new ChartSetElement("ScheduleProfit",scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("SchedulePrincipal",schedulePrincipal);
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
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"RepayAmount",
							PennantAppUtil.formateAmount(listScheduleDetail.get(i).getRepayAmount(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"PrincipalSchd",PennantAppUtil.formateAmount(
									listScheduleDetail.get(i).getPrincipalSchd(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"ProfitSchd",PennantAppUtil.formateAmount(
									listScheduleDetail.get(i).getProfitSchd(),formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);

				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}
	
}
