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
 * FileName    		:  DashboardDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.dashboard;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.fusionchart.Fusionchart;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MaximizeEvent;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Vbox;

import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.common.menu.domain.MenuPendingDetail;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/DashboardDetails/DashboardDetail/dashboardDetailDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class DashboardCreate extends  GFCBaseListCtrl<Object> implements Serializable {
	private static final long serialVersionUID = -4201689911130684236L;
	private final static Logger logger = Logger.getLogger(DashboardCreate.class);
	protected  Fusionchart    fusionchart;
	private    String         borderLayoutHeight = getBorderLayoutHeight();
	private    int            myHeight=Integer.parseInt(StringUtils.replaceChars(borderLayoutHeight, "px","")) * 85/100;
	public     ChartDetail    chartDetail;
	ChartUtil  chartUtil=new ChartUtil();
	private DashboardConfigurationService dashboardConfigurationService;
	public Listbox listbox;

	/**
	 * Creates a new panel and inserts the Dash boards in to it. 
	 * 
	 * @param info
	 * @return
	 */
	public synchronized Panel  createPanel(DashboardConfiguration info,Portalchildren portalchildren) {

		logger.debug("Entering ");
		if(StringUtils.trimToEmpty(info.getDashboardType()).equalsIgnoreCase("listbox")){
			Panel panel = new Panel();
	 		panel.setBorder("normal");
			panel.setCollapsible(true);
			panel.setClosable(true);  
			panel.setMaximizable(true);
			panel.addEventListener(Events.ON_MAXIMIZE, new onListPanelMaximized());
			panel.setId(info.getDashboardCode());
			panel.setTitle(info.getDashboardDesc());
			Panelchildren panelchildren = new Panelchildren();
			Listhead listhead = new Listhead();
			listhead.setSizable(true);
			Listheader listHeader;
			Hbox hbox;
			Label label;
			this.listbox = new Listbox();
			this.listbox.setWidth("100%");
			this.listbox.setRows(7);
			this.listbox.setSpan(true);
			this.listbox.setSizedByContent(true);
			this.listbox.setEmptyMessage(Labels.getLabel("listbox.emptyMessage"));
			this.listbox.appendChild(listhead);
			listHeader = new Listheader();
			listHeader.setSort("auto");
			listHeader.setHflex("min");
			label = new Label("Finance Reference");
			label.setStyle("font-size: 12px;font-weight: bold;color:#3a5976;");
			listHeader.appendChild(label);
			listhead.appendChild(listHeader);
			listHeader = new Listheader();
			listHeader.setSort("auto");
			listHeader.setHflex("min");
			label = new Label("Customer CIF");
			label.setStyle("font-size: 12px;font-weight: bold;color:#3a5976;");
			listHeader.appendChild(label);
			listhead.appendChild(listHeader);
			listHeader = new Listheader();
			listHeader.setSort("auto");
			listHeader.setHflex("min");
			hbox = new Hbox();
			Button btn = new Button("Refresh");
			Space space = new Space();
			space.setSpacing("15px");
			Vbox vbox = new Vbox();
			label = new Label("Customer Name");
			label.setStyle("font-size: 12px;font-weight: bold;color:#3a5976;");
			btn.setHeight("20px");
			btn.setWidth("70px");
			btn.addEventListener(Events.ON_CLICK, new onButtonClicked());
			vbox.appendChild(new Label());
			vbox.appendChild(label);
			hbox.appendChild(vbox);
			hbox.appendChild(space);
			hbox.appendChild(btn);
			listHeader.appendChild(hbox);
			listhead.appendChild(listHeader);
			panelchildren.appendChild(this.listbox);
			doFillDetailListBox();
			panel.appendChild(panelchildren);
			return panel;
		}else{
			Panel dashboardPanel = initPanel(new Panel());// Get a panel template
			dashboardPanel.setId(info.getDashboardCode());
			dashboardPanel.setTitle(info.getDashboardDesc());
			Panelchildren panelchildren = dashboardPanel.getPanelchildren();
			ChartDetail chartDetail=getChartDetail(info);
			chartDetail.setChartId(info.getDashboardCode());
			chartDetail.setChartHeight("88%");
			chartDetail.setChartWidth("100%");
			chartDetail.setiFrameHeight("100%");
			chartDetail.setiFrameWidth("100%");
			setChartDetail(chartDetail);
			//Get HTML Content which renders fusion chart by calling chartUtil.getHtmlContent(chartDetail)
			panelchildren.appendChild(chartUtil.getHtmlContent(chartDetail));
			dashboardPanel.appendChild(panelchildren);
			return dashboardPanel;
		}
	}
	
	public void doFillDetailListBox() {
		logger.debug("Entering");
		if(getUserWorkspace().getUserRoles() != null && !getUserWorkspace().getUserRoles().isEmpty()){
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			JdbcSearchObject<MenuPendingDetail> jdbcSearchObject = new JdbcSearchObject<MenuPendingDetail>(MenuPendingDetail.class);
			jdbcSearchObject.addTabelName("FinancePendingDetailsByRole_View");
			jdbcSearchObject.addFilterIn("NextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
			List<MenuPendingDetail> menuPendingDetails = pagedListService.getBySearchObject(jdbcSearchObject);
			this.listbox.getItems().clear();
			Listgroup group = null;
			Listitem item = null;
			Listcell cell = null;
			String menuCode = "";
			int recordCount = 1;
			if(menuPendingDetails != null){
				for (MenuPendingDetail menuPendingDetail : menuPendingDetails){
					if(!menuCode.equalsIgnoreCase(menuPendingDetail.getMenuRef())){
						recordCount = 1;
						group = new Listgroup();
						cell = new Listcell(Labels.getLabel(menuPendingDetail.getMenuRef())+" : ");
						Label label = new Label();
						label.setId(menuPendingDetail.getMenuRef());
						cell.appendChild(label);
						cell.setParent(group);
						group.setAttribute("data", menuPendingDetail);
						group.addEventListener(Events.ON_DOUBLE_CLICK, new onListGroupClicked());
						this.listbox.appendChild(group);
					}
					if(group.getFellowIfAny(menuPendingDetail.getMenuRef()) != null){
						Label label =	(Label)group.getFellowIfAny(menuPendingDetail.getMenuRef());
						label.setValue(String.valueOf(recordCount));
					}
					recordCount ++;
					menuCode = menuPendingDetail.getMenuRef();
					item = new Listitem();
					Listcell lc;
					lc = new Listcell(menuPendingDetail.getFinReference());
					lc.setParent(item);
					lc = new Listcell(menuPendingDetail.getCustCIF());
					lc.setParent(item);
					lc = new Listcell(menuPendingDetail.getCustShrtName());
					lc.setParent(item);
					item.setAttribute("data", menuPendingDetail);
					item.addEventListener(Events.ON_DOUBLE_CLICK, new onListItemClicked());
					this.listbox.appendChild(item);
				}
			}
		}
		logger.debug("Leaving");
	}
	
	public void openZulpage(MenuPendingDetail menuPendingDetail,boolean isDetailScreen) throws InterruptedException{
		try {
			
				/* get an instance of the borderlayout defined in the zul-file */
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				/* get an instance of the searched CENTER layout area */
				final Center center = bl.getCenter();

				final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter")
						.getFellow("tabsIndexCenter");

				// Check if the tab is already open, if not than create them
				Tab checkTab = null;
				try {
					//checkTab = (Tab) tabs.getFellow("tab_" + menuPendingDetail.getMenuCode());
					checkTab = (Tab) tabs.getFellow(StringUtils.trimToEmpty(menuPendingDetail.getMenuRef()).replace("menu_Item_", "tab_"));
					checkTab.setSelected(true);
				} catch (final ComponentNotFoundException ex) {
					// Ignore if can not get tab.
				}
				if (checkTab != null) {
					checkTab.close();
					checkTab = null;
				}
				if (checkTab == null) {
					final Tab tab = new Tab();
					//tab.setId("tab_" +  menuPendingDetail.getMenuCode());
					tab.setId(StringUtils.trimToEmpty(menuPendingDetail.getMenuRef()).replace("menu_Item_", "tab_"));
					tab.setLabel(Labels.getLabel(menuPendingDetail.getMenuRef()));
					tab.setClosable(true);
					tab.setParent(tabs);

					final Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter")
							.getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter")
							.getFellow("tabpanelsBoxIndexCenter");
					final Tabpanel tabpanel = new Tabpanel();
					tabpanel.setHeight("100%");
					tabpanel.setStyle("padding: 0px;");
					tabpanel.setParent(tabpanels);
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("isDashboard", true);
					map.put("FinanceReference", menuPendingDetail.getFinReference());
					map.put("moduleDefiner", menuPendingDetail.getMenuCode());
					if(isDetailScreen){
					map.put("detailScreen", true);
					}
					Executions.createComponents(menuPendingDetail.getMenuZulPath(), tabpanel, map);
					tab.setSelected(true);
				}
		
			if (logger.isDebugEnabled()) {
				logger.debug("--> calling zul-file: " + menuPendingDetail.getMenuZulPath());
			}
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}

	}
	
	/**
	 * Initialize the panel
	 * @param panel
	 * @return
	 */
	private Panel initPanel(Panel panel) {
		panel.setBorder("normal");
		panel.setCollapsible(true);
		panel.setClosable(true);  
		panel.setMaximizable(true);
		panel.setHeight(myHeight/2+"px");
		panel.addEventListener(Events.ON_MAXIMIZE, new onPanelMaximized());
		panel.addEventListener(Events.ON_CLOSE, new onPanelClosed());
		panel.appendChild(new Panelchildren());
		return panel;
	}
	/**
	 * 
	 * Event listener for panel maximize event
	 *
	 */
	class onPanelMaximized implements EventListener<Event>{

		@Override
		public void onEvent(Event event) throws Exception {
			MaximizeEvent maxEvent = (MaximizeEvent) event; 
			Panel panel = (Panel) maxEvent.getTarget();  
			onPanelMaximizedHelper(panel);
		}
	}
	
	/**
	 * 
	 * Event listener for list item double click event
	 *
	 */
	class onListItemClicked implements EventListener<Event>{
		@Override
		public void onEvent(Event event) throws Exception {
			final Listitem item = listbox.getSelectedItem();
			final MenuPendingDetail menuPendingDetail = (MenuPendingDetail) item.getAttribute("data");
			if(menuPendingDetail != null){
				openZulpage(menuPendingDetail,true);
			}
		}
	}
	
	/**
	 * 
	 * Event listener for list group double click event
	 *
	 */
	class onListGroupClicked implements EventListener<Event>{
		@Override
		public void onEvent(Event event) throws Exception {
			final Listgroup listgroup = (Listgroup)event.getTarget();
			final MenuPendingDetail menuPendingDetail = (MenuPendingDetail) listgroup.getAttribute("data");
			if(menuPendingDetail != null){
			openZulpage(menuPendingDetail,false);
			}
		}
	}
	
	/**
	 * 
	 * Event listener for refresh button click event
	 *
	 */
	class onButtonClicked implements EventListener<Event>{
		@Override
		public void onEvent(Event event) throws Exception {
			doFillDetailListBox();
		}
	}
	/**
	 * 
	 * Event listener for refresh button click event
	 *
	 */
	class onListPanelMaximized implements EventListener<Event>{
		@Override
		public void onEvent(Event event) throws Exception {
			MaximizeEvent maxEvent = (MaximizeEvent) event; 
			Panel panel = (Panel) maxEvent.getTarget();  
			Panelchildren pc=(Panelchildren) panel.getChildren().get(0);
			Listbox   listbox = (Listbox)pc.getChildren().get(0);
			Portallayout portLayout=(Portallayout)panel.getParent().getParent();
			if(panel.isMaximized()){
				pc.getChildren().clear();
				pc.appendChild(listbox);
				listbox.setRows(16);
				listbox.setHeight("100%");
				portLayout.setHeight(myHeight+"px");
				panel.setWidth("100%");
				panel.setHeight(myHeight+"px");
			}else{
				pc.getChildren().clear();
				portLayout.setHeight("");
				listbox.setRows(7);
				pc.appendChild(listbox);
				panel.setHeight(myHeight/2+"px");
				panel.setOpen(true);
			}
		}
	}
	
	/**
	 * This method ReDraws  the chart when panel is maximized
	 * @param panel
	 */
	public void onPanelMaximizedHelper(Panel panel){
		logger.debug("Entering");

		Panelchildren pc=(Panelchildren) panel.getChildren().get(0);
		Html   html=(Html) pc.getChildren().get(0);
		Portallayout portLayout=(Portallayout)panel.getParent().getParent();
		if(panel.isMaximized()){
			pc.getChildren().clear();
			portLayout.setHeight(myHeight+"px");
			String HtmlContent =html.getContent();
			//check for values must not display
			if(!HtmlContent.contains("forceNoValues=&quot;1&quot;")){
				//make show values when panel maximized
				if(HtmlContent.contains("showValues=&quot;0&quot;")){
					HtmlContent=StringUtils.replace(HtmlContent, "showValues=&quot;0&quot;", "showValues=&quot;1&quot;");
				}
			}
			html.setContent(HtmlContent);
			pc.appendChild(html);
			panel.setWidth("100%");
			panel.setHeight(myHeight+"px");

		}else{
			pc.getChildren().clear();
			portLayout.setHeight("");
			String HtmlContent =html.getContent();
			if(HtmlContent.contains("showValues=&quot;1&quot;")){
				//make not to show values when panel minimized
				HtmlContent=StringUtils.replace(HtmlContent, "showValues=&quot;1&quot;", "showValues=&quot;0&quot;");
			}
			html.setContent(HtmlContent);
			pc.appendChild(html);
			panel.setHeight(myHeight/2+"px");
			panel.setOpen(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * 
	 * Event listener for panel maximize event
	 *
	 */
	class onPanelClosed implements EventListener<Event>{

		@Override
		public void onEvent(Event event) throws Exception {
			Event closeEvent = (Event) event; 
			Panel panel = (Panel) closeEvent.getTarget();
			Portallayout portLayout=(Portallayout)panel.getParent().getParent();
			portLayout.setHeight("");
		}
	}
	/**
	 * This method prepares  chart related data to ChartDetail object and returns it.
	 * @param aDashboardConfiguration
	 * @return
	 */
	public ChartDetail getChartDetail(DashboardConfiguration aDashboardConfiguration){
		logger.debug("Entering ");
		String chartStrXML="";
		ChartsConfig chartsConfig=new ChartsConfig(aDashboardConfiguration.getCaption(),aDashboardConfiguration.getSubCaption()
				,"","");
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		//check data retrieves from database or static XML data
		if(StringUtils.trimToEmpty(aDashboardConfiguration.getDataXML()).equals("")){
			//Get data from database and create XML String using result set by calling getLabelAndValues()
			chartStrXML=getLabelAndValues(aDashboardConfiguration);
		}else{
			//complete  chart XML  
			chartStrXML=aDashboardConfiguration.getDataXML();
		}
		ChartDetail chartDetail=new ChartDetail();
		chartDetail.setStrXML(chartStrXML);
		ChartUtil chartUtil=new ChartUtil();
		//Get Flash file against select type and dimension 2D or 3D/Pie or Bar like 
		chartDetail.setSwfFile(chartUtil.getSWFFileName(aDashboardConfiguration));
		logger.debug("Leaving ");
		return chartDetail;

	}
	/**
	 * This method returns labels and values of chart
	 * @param dashboardConfiguration
	 * @return
	 */
	public String getLabelAndValues(DashboardConfiguration dashboardConfiguration){
		logger.debug("Entering ");

		String whereCondition ="";
		dashboardConfiguration.setLovDescDataObject(dashboardConfiguration);
		//Retrieve data as list of ChartSetElement
		List<ChartSetElement> listSetElements=getDashboardConfigurationService()
		.getLabelAndValues(dashboardConfiguration,whereCondition);
		if(listSetElements!=null && listSetElements.size()>0){
			dashboardConfiguration.getLovDescChartsConfig().setSetElements(listSetElements);
			dashboardConfiguration.getLovDescChartsConfig().setRemarks(dashboardConfiguration.getRemarks());
		}
		
		if(chartUtil.isAGauge(dashboardConfiguration)){
			return dashboardConfiguration.getLovDescChartsConfig().getAGaugeXML();
		}else if(dashboardConfiguration.isDrillDownChart()){
			return dashboardConfiguration.getLovDescChartsConfig().getDrillDownChartXML();
		}else if(chartUtil.isMultiSeries(dashboardConfiguration)){
			return dashboardConfiguration.getLovDescChartsConfig().getSeriesChartXML(dashboardConfiguration.getRenderAs());
		}else{
			return dashboardConfiguration.getLovDescChartsConfig().getChartXML();
		}
	}

	public void setDashboardConfigurationService(
			DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}
	public DashboardConfigurationService getDashboardConfigurationService() {
		return dashboardConfigurationService;
	}
	public void setChartDetail(ChartDetail chartDetail) {
		this.chartDetail = chartDetail;
	}
	public ChartDetail getChartDetail() {
		return chartDetail;
	}
} 
