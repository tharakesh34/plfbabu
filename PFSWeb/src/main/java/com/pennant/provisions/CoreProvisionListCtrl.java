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
 * * FileName : CoreProvisionListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.provisions;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ProvisionCalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.provisions.model.CoreProvisionListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Provisions/ProvisionsList.zul file.
 */
public class CoreProvisionListCtrl extends GFCBaseListCtrl<Provision> {
	private static final long serialVersionUID = -2437455376763752382L;

	protected Window window_CoreProvisionList;
	protected Borderlayout borderLayout_CoreProvisionList;
	protected Paging pagingCoreProvisionList;
	protected Listbox listBoxProvisions;

	protected Listbox sortOperator_Finreference;
	protected Textbox finreference;

	// List headers
	protected Listheader listheader_Prov_Finreference;
	protected Listheader listheader_ProvisionCalDate;
	protected Listheader listheader_ProvosionAmt;
	protected Listheader listheader_ProvosionAmtCal;
	protected Listheader listheader_NoFormlaProvision;
	protected Listheader listheader_UseNFProv;
	protected Listheader listheader_PrevProvDate;
	protected Listheader listheader_PrevProvAmt;
	protected Listheader listheader_TranRef;

	// checkRights
	protected Button button_ProvisionsList_ProvisionProcess;
	protected Button button_CoreProvisionList_ProvosionsSearchDialog;

	// NEEDED for the ReUse in the SearchWindow

	private ProvisionCalculationUtil provisionCalculationUtil;
	private ProvisionService provisionService;

	/**
	 * default constructor.<br>
	 */
	public CoreProvisionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Provisions";
		super.pageRightName = "CoreProvisionList";
		super.tableName = "SAS_PFF_FIN_PROV_DETAILS";
		super.queueTableName = "SAS_PFF_FIN_PROV_DETAILS";
		super.enquiryTableName = "SAS_PFF_FIN_PROV_DETAILS";
	}

	@Override
	public void search() {
		super.search();

		logger.debug("Entering");
		searchObject.clearFilters();

		if (StringUtils.isNotBlank(this.finreference.getValue())) {
			searchObject = getSearchFilter(searchObject, this.sortOperator_Finreference.getSelectedItem(),
					this.finreference.getValue(), "finreference");
		}

		// Set the ListModel for the articles.

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("extPagedListService");
		List<Provision> provisionList = pagedListService.getBySearchObject(searchObject);
		// List<Provision> processedProvisions = getProvisionService().getProcessedProvisions();

		/*
		 * if (processedProvisions.isEmpty()) { // render provisionList if processed provisions are empty
		 * getPagedListWrapper().initList(provisionList, this.listBoxProvisions, this.pagingCoreProvisionList); } else {
		 * for (Provision provision : processedProvisions) { Iterator<Provision> it = provisionList.iterator(); while
		 * (it.hasNext()) { Provision provisions = (Provision) it.next(); if
		 * (provision.getFinReference().equals(provisions.getFinReference())) { it.remove(); } } }
		 * getPagedListWrapper().initList(provisionList, this.listBoxProvisions, this.pagingCoreProvisionList); }
		 */
		this.listBoxProvisions.setItemRenderer(new CoreProvisionListModelItemRender());
		listBoxProvisions.setCheckmark(true);
		listBoxProvisions.setMultiple(true);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CoreProvisionList(Event event) {
		// Set the page level components.
		setPageComponents(window_CoreProvisionList, borderLayout_CoreProvisionList, listBoxProvisions,
				pagingCoreProvisionList);
		setItemRender(new CoreProvisionListModelItemRender());

		// Register buttons and fields.
		registerButton(button_CoreProvisionList_ProvosionsSearchDialog);

		registerField("FinID");
		registerField("finReference", listheader_Prov_Finreference, SortOrder.ASC, finreference,
				sortOperator_Finreference, Operators.STRING);
		registerField("provisionCalDate", listheader_ProvisionCalDate);
		registerField("provisionAmt", listheader_ProvosionAmtCal);
		registerField("provisionAmtCal", listheader_ProvisionCalDate);
		registerField("nonFormulaProv", listheader_NoFormlaProvision);
		registerField("prevProvisionCalDate", listheader_PrevProvDate);
		registerField("prevProvisionedAmt", listheader_PrevProvAmt);
		registerField("transRef", listheader_TranRef);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CoreProvisionList_ProvosionsSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_CoreProvisionList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the Provisions print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CoreProvisionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("Provisions", searchObject, this.pagingCoreProvisionList.getTotalSize() + 1);
		logger.debug("Leaving" + event.toString());
	}

	public void onProvisionItemChecked(Event event) {
		logger.debug("Entering");
		if (this.listBoxProvisions.getSelectedCount() > 0) {
			this.button_ProvisionsList_ProvisionProcess.setVisible(true);
		} else {
			this.button_ProvisionsList_ProvisionProcess.setVisible(false);
		}

		logger.debug("Leaving");
	}

	public void onSelect$listBoxProvisions(Event event) throws InterruptedException {
		logger.debug("Entering");
		this.button_ProvisionsList_ProvisionProcess.setVisible(true);
		logger.debug("Leaving");
	}

	public void onClick$button_ProvisionsList_ProvisionProcess(Event event) throws InterruptedException {

		logger.debug("Entering" + event.toString());
		Date dateValueDate = SysParamUtil.getAppValueDate();

		int count = this.listBoxProvisions.getSelectedCount();
		int successCount = 0;
		int failCount = 0;

		if (count > 0) {

			for (Listitem itemSelected : listBoxProvisions.getSelectedItems()) {

				if (itemSelected != null) {
					try {
						final Provision aProvisions = (Provision) itemSelected.getAttribute("data");
						getProvisionCalculationUtil().processProvCalculations(aProvisions, dateValueDate, false, true,
								true);

						successCount++;

					} catch (Exception e) {
						logger.error("Exception: ", e);
						failCount++;
					}
				}
			}

			search();
			MessageUtil.showMessage("Total Processed  :" + count + "," + "Successfully processed :" + successCount + ","
					+ "failed :" + failCount);
		}
		logger.debug("Leaving" + event.toString());
	}

	public ProvisionCalculationUtil getProvisionCalculationUtil() {
		return provisionCalculationUtil;
	}

	public void setProvisionCalculationUtil(ProvisionCalculationUtil provisionCalculationUtil) {
		this.provisionCalculationUtil = provisionCalculationUtil;
	}

	public ProvisionService getProvisionService() {
		return provisionService;
	}

	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

}