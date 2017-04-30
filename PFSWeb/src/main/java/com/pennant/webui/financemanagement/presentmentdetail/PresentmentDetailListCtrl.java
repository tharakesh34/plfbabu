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
 * FileName    		:  PresentmentDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.presentmentdetail;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.financemanagement/PresentmentDetail/PresentmentDetailList.zul file.
 * 
 */
public class PresentmentDetailListCtrl extends GFCBaseListCtrl<PresentmentHeader> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(PresentmentDetailListCtrl.class);

	protected Window window_PresentmentDetailList;
	protected Borderlayout borderLayout_PresentmentDetailList;
	protected Paging pagingPresentmentDetailList;
	protected Listbox listBoxPresentmentDetail;

	protected Button button_PresentmentDetailList_PresentmentDetailSearch;

	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		this.searchObject.addFilterEqual("Status", 1);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentHeader";
		super.pageRightName = "PresentmentHeader";
		super.tableName = "PresentmentHeader";
		super.queueTableName = "PresentmentHeader";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentDetailList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_PresentmentDetailList, borderLayout_PresentmentDetailList, listBoxPresentmentDetail,
				pagingPresentmentDetailList);
		setItemRender(new PresentmentDetailListModelItemRenderer());

		registerField("Id");
		registerField("Reference");
		registerField("LastMntBy");
		registerField("LastMntOn");
		registerField("Status");

		registerButton(button_PresentmentDetailList_PresentmentDetailSearch);
		doRenderPage();
		search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_PresentmentDetailList_PresentmentDetailSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onItemDoubleClicked(Event event) {
		Listitem selectedItem = this.listBoxPresentmentDetail.getSelectedItem();

		final long id = (long) selectedItem.getAttribute("Id");
		Map<String, Object> arg = getDefaultArguments();

		arg.put("presentmentDetailListCtrl", this);
		arg.put("PresentmentId", id);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/PresentmentDetail/PresentmentDetailDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}
	}

	/**
	 * Item renderer for list items in the list box.
	 * 
	 */
	public class PresentmentDetailListModelItemRenderer implements ListitemRenderer<PresentmentHeader>, Serializable {

		private static final long serialVersionUID = 3736186724610414895L;

		public PresentmentDetailListModelItemRenderer() {

		}

		@Override
		public void render(Listitem item, PresentmentHeader object, int count) throws Exception {
			Listcell lc;

			lc = new Listcell(object.getReference());
			lc.setParent(item);

			lc = new Listcell(getUserWorkspace().getUserDetails().getUsername());
			lc.setParent(item);

			lc = new Listcell(DateUtility.formatToLongDate(object.getLastMntOn()));
			lc.setParent(item);

			lc = new Listcell(PennantStaticListUtil.getlabelDesc(String.valueOf(object.getStatus()),
					PennantStaticListUtil.getPresentmentBatchStatusList()));
			lc.setParent(item);

			item.setAttribute("Id", object.getId());
			ComponentsCtrl.applyForward(item, "onDoubleClick=onItemDoubleClicked");
		}
	}

}