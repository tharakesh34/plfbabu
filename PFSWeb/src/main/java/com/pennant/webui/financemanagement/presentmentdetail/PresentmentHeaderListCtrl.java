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
 * FileName    		:  PresentmentHeaderListCtrl.java                                                   * 	  
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.web.components.MultiLineMessageBox;
import com.pennanttech.pff.core.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.financemanagement/PresentmentHeader/PresentmentHeaderList.zul file.
 * 
 */
public class PresentmentHeaderListCtrl extends GFCBaseListCtrl<PresentmentHeader> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PresentmentHeaderListCtrl.class);

	protected Window window_PresentmentHeaderList;
	protected Borderlayout borderLayout_PresentmentHeaderList;
	protected Paging pagingPresentmentHeaderList;
	protected Listbox listBoxPresentmentHeader;

	protected Button btnApprove;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox list_CheckBox;

	private transient PresentmentDetailService presentmentDetailService;
	private Map<Long, Object> presentmentMap = new HashMap<Long, Object>();

	/**
	 * default constructor.<br>
	 */
	public PresentmentHeaderListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		this.searchObject.addFilterEqual("Status", 2);
	}
	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentHeader";
		super.pageRightName = "PresentmentHeaderList";
		super.tableName = "PresentmentHeader";
		super.queueTableName = "PresentmentHeader";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_PresentmentHeaderList, borderLayout_PresentmentHeaderList, listBoxPresentmentHeader, pagingPresentmentHeaderList);
		setItemRender(new PresentmentHeaderListModelItemRenderer());

		registerButton(btnApprove);

		registerField("Id");
		registerField("Reference");
		registerField("LastMntBy");
		registerField("LastMntOn");
		registerField("Status");

		doSetFieldProperties();
		doRenderPage();
		search();
	}

	private void doSetFieldProperties() {

		listItem_Checkbox = new Listitem();
		listCell_Checkbox = new Listcell();
		listHeader_CheckBox_Comp = new Checkbox();
		listCell_Checkbox.appendChild(listHeader_CheckBox_Comp);
		listHeader_CheckBox_Comp.addForward("onClick", self, "onClick_listHeaderCheckBox");
		listItem_Checkbox.appendChild(listCell_Checkbox);

		if (listHeader_CheckBox_Name.getChildren() != null) {
			listHeader_CheckBox_Name.getChildren().clear();
		}
		listHeader_CheckBox_Name.appendChild(listHeader_CheckBox_Comp);
	}

	/**
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		for (int i = 0; i < listBoxPresentmentHeader.getItems().size(); i++) {
			Listitem listitem = listBoxPresentmentHeader.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked() && listBoxPresentmentHeader.getItems().size() > 0) {
			presentmentMap = getPresentmentMap();
		} else {
			presentmentMap.clear();
		}

		logger.debug("Leaving");
	}

	private Map<Long, Object> getPresentmentMap() {

		JdbcSearchObject<Map<Long, Object>> searchObject = new JdbcSearchObject<>();
		searchObject.addField("Id");
		searchObject.addFilterEqual("Status", 2);
		searchObject.addTabelName(this.tableName);

		List<Map<Long, Object>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);

		Map<Long, Object> preMap = new HashMap<Long, Object>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<Long, Object> map = (Map<Long, Object>) list.get(i);
				long paymentid = Long.parseLong(String.valueOf(map.get("Id")));
				preMap.put(paymentid, null);
			}
		}
		return preMap;
	}

	/**
	 * Filling the MandateIdMap details based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		long id = (Long) checkBox.getAttribute("iD");

		if (checkBox.isChecked()) {
			presentmentMap.put(id, null);
		} else {
			presentmentMap.remove(id);
		}

		if (presentmentMap.size() == this.pagingPresentmentHeaderList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
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
	 * When user clicks on "btnApprove"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnApprove(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		List<Long> presentmentList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			presentmentMap.clear();
			presentmentMap = getPresentmentMap();
			presentmentList = new ArrayList<Long>(presentmentMap.keySet());
		} else {
			presentmentList = new ArrayList<Long>(presentmentMap.keySet());
		}

		if (listBoxPresentmentHeader.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}

		if (presentmentList.isEmpty() || listBoxPresentmentHeader.getItems().size() <= 0) {
			MessageUtil.showErrorMessage("Please select at least one record");
			return;
		}

		// Show a confirm box
		String msg = " " + this.presentmentMap.size() + "/" + this.pagingPresentmentHeaderList.getTotalSize()
				+ " batches are selected for process.\n Do you want to continue? ";
		MultiLineMessageBox.doSetTemplate();
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Conformation"), MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, Messagebox.QUESTION, true);
		if (conf == MultiLineMessageBox.NO) {
			return;
		}
		try {
			btnApprove.setDisabled(true);
			presentmentDetailService.processDetails(presentmentList);
		} catch (Exception e) {
			logger.error("Exception :", e);
		} finally {
			this.presentmentMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			search();
			btnApprove.setDisabled(false);
			logger.debug("Leaving");
		}
	}

	/**
	 * Item renderer for list items in the list box.
	 * 
	 */
	public class PresentmentHeaderListModelItemRenderer implements ListitemRenderer<PresentmentHeader>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, PresentmentHeader object, int count) throws Exception {

			Listcell lc;

			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("iD", object.getId());
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {
				list_CheckBox.setChecked(presentmentMap.containsKey(object.getId()));
			}
			lc.setParent(item);

			lc = new Listcell(object.getReference());
			lc.setParent(item);

			lc = new Listcell(getUserWorkspace().getUserDetails().getUsername());
			lc.setParent(item);

			lc = new Listcell(DateUtility.formatToLongDate(object.getLastMntOn()));
			lc.setParent(item);

			lc = new Listcell(PennantStaticListUtil.getlabelDesc(String.valueOf(object.getStatus()),
					PennantStaticListUtil.getPresentmentBatchStatusList()));
			lc.setParent(item);
		}
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

}