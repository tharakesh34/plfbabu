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
 * FileName    		:  FinanceEligibilityRuleResultCtrl.java                                                   * 	  
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.finance.EligibilityRule;
import com.pennant.backend.service.finance.FinanceEligibility;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the 
 * /WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityRuleResult.zul file.
 */
public class FinanceEligibilityRuleResultCtrl extends GFCBaseCtrl<EligibilityRule> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(FinanceEligibilityRuleResultCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinElgRuleResult; 		

	protected Borderlayout	borderlayoutElgRuleResult;	
	protected Listbox		listBoxElgRule;

	// not auto wired variables
	private int formatter = 3;
	private FinanceDetail financeDetail;

	private transient PromotionPickListCtrl promotionPickListCtrl;
	private List<FinanceEligibility> finElgRuleDetailList; 
	
	private FinanceTypeService financeTypeService;
	/**
	 * default constructor.<br>
	 */
	public FinanceEligibilityRuleResultCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, 
	 * if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinElgRuleResult(ForwardEvent event)  throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinElgRuleResult);
		
		if (arguments.containsKey("formatter")) {
			this.formatter = (Integer) arguments.get("formatter");
		}
		if (arguments.containsKey("financeDetail")) {
			 financeDetail = (FinanceDetail) arguments.get("financeDetail");
		}
		if (arguments.containsKey("promotionPickListCtrl")) {
			this.promotionPickListCtrl = (PromotionPickListCtrl) arguments.get("promotionPickListCtrl");
		}
		
		getBorderLayoutHeight();
		this.listBoxElgRule.setHeight(borderLayoutHeight - 159+"px");
		
		if (arguments.containsKey("finElgRuleDetailList")) {
			finElgRuleDetailList = (List<FinanceEligibility>) arguments.get("finElgRuleDetailList");
			doFillEligibilityDetailList(finElgRuleDetailList);
		}
		
		setDialog(DialogType.MODAL);
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	
	public void doFillEligibilityDetailList(List<FinanceEligibility> finElgRuleDetailList){
		logger.debug("Entering");
		if(finElgRuleDetailList != null){
			String productTemp = "";
			for (FinanceEligibility finEligibility : finElgRuleDetailList) {
				Listgroup listgroup;
				if(!finEligibility.getProduct().equals(productTemp)){
					listgroup = new Listgroup();
					Listcell lc;
					lc = new Listcell(finEligibility.getProduct()+"-"+finEligibility.getProductDesc());
					listgroup.appendChild(lc);
					this.listBoxElgRule.appendChild(listgroup);
				}
				productTemp = finEligibility.getProduct();
				
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(finEligibility.getPromotionCode());
				item.appendChild(lc);
				lc = new Listcell(finEligibility.getPromotionDesc());
				item.appendChild(lc);
				lc = new Listcell(String.valueOf(finEligibility.getNumberOfTerms()));
				lc.setStyle("text-align:right");
				item.appendChild(lc);
				lc = new Listcell(String.valueOf(finEligibility.getRepayProfitRate())+" %");
				lc.setStyle("text-align:right");
				item.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(finEligibility.getElgAmount(),formatter));
				lc.setStyle("text-align:right");
				item.appendChild(lc);
				lc = new Listcell();
				Html html=new Html();
				html.setContent(finEligibility.getProductFeature());
				lc.appendChild(html);
				lc.setStyle("cursor:default;");
				item.appendChild(lc);
				item.setAttribute("data", finEligibility);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onEligibilityCheckListItemDoubleClicked");
				this.listBoxElgRule.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}
	
	public void onEligibilityCheckListItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected City object
		final Listitem item = this.listBoxElgRule.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceEligibility finEligibility = (FinanceEligibility) item.getAttribute("data");
			// call the ZUL-file with the parameters packed in a map

			if(finEligibility.getFinAssetType() != null && !finEligibility.getFinAssetType().isEmpty()){

				List<ProductAsset> productAssetlist = getFinanceTypeService().getFinPurposeByAssetId(
						getAssetTypeList(finEligibility.getFinAssetType()),"");

				if(productAssetlist != null && !productAssetlist.isEmpty()){
					if(productAssetlist.size() == 1){
						finEligibility.setFinPurpose(productAssetlist.get(0).getAssetCode());
						finEligibility.setLovDescFinPurposeName(productAssetlist.get(0).getAssetDesc());
						doCreateFinanceWindow(finEligibility);
					}else{
						final HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("productAssetlist", productAssetlist);
						map.put("finEligibility", finEligibility);
						map.put("finElgRuleResultCtrl", this);
						Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinancePurposeSelectDialog.zul",
								null, map);
					}
				}
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	public void doCreateFinanceWindow(FinanceEligibility finEligibility) throws InterruptedException{
		logger.debug("Entering");
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
				checkTab = (Tab) tabs.getFellow("tab_NewFinanceMain");
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
				tab.setId("tab_NewFinanceMain");
				tab.setLabel(Labels.getLabel("menu_Item_NewFinanceMain"));
				tab.setSelected(true);
				tab.setClosable(true);
				tab.setParent(tabs);

				final Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter")
						.getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter")
						.getFellow("tabpanelsBoxIndexCenter");
				final Tabpanel tabpanel = new Tabpanel();
				tabpanel.setHeight("100%");
				tabpanel.setStyle("padding: 0px;");
				tabpanel.setParent(tabpanels);
				tab.setSelected(true);

				final HashMap<String, Object> map = new HashMap<String, Object>();
				finEligibility.setFinanceDetail(this.financeDetail); 
				map.put("fromEligibleScreen", true);
				map.put("finEligibility", finEligibility);

				closeDialog();
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul",tabpanel,map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	private ArrayList<String> getAssetTypeList(String finAssetType) {
		ArrayList<String> finAssetTypelist = new ArrayList<String>();
		if(finAssetType.contains(",")){
			String[]finAssetTypes = finAssetType.split(",");
			for (String assetType : finAssetTypes) {
				finAssetTypelist.add(assetType);
			}
		}else{
			finAssetTypelist.add(finAssetType);
		}
		return finAssetTypelist;
	}
	
	@Override
	public void closeDialog() {
		super.closeDialog();
		if (promotionPickListCtrl != null) {
			promotionPickListCtrl.closeDialog();
		}
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}
	
	
}
