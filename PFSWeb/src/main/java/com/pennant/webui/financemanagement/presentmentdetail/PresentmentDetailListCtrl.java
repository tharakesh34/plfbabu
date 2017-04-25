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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pff.core.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.financemanagement/PresentmentDetail/PresentmentDetailList.zul file.
 * 
 */
public class PresentmentDetailListCtrl extends GFCBaseListCtrl<PresentmentDetail> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(PresentmentDetailListCtrl.class);

	protected Window window_PresentmentDetailList;
	protected Borderlayout borderLayout_PresentmentDetailList;
	protected Paging pagingPresentmentDetailList;
	protected Listbox listBoxPresentmentDetail;
	
	protected Button button_PresentmentDetailList_PresentmentDetailSearch;
	protected Button button_PresentmentDetailList_CreateBatch;

	protected Combobox mandateType;
	protected ExtendedCombobox product;
	protected Combobox exclusionStatus;
	protected Combobox batchReference;

	private Map<Long, String> presentmentIdMap = new HashMap<Long, String>();
	private transient PresentmentDetailService presentmentDetailService;

	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentDetail";
		super.pageRightName = "PresentmentDetailList";
		super.tableName = "PresentmentDetails";
		super.queueTableName = "PresentmentDetails";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentDetailList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_PresentmentDetailList, borderLayout_PresentmentDetailList, listBoxPresentmentDetail, pagingPresentmentDetailList);
		setItemRender(new PresentmentDetailListModelItemRenderer());

		registerButton(button_PresentmentDetailList_PresentmentDetailSearch);
		registerButton(button_PresentmentDetailList_CreateBatch);

		doRenderPage();
		doSetFieldProperties();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the component level properties.
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mandateType, "", PennantStaticListUtil.getMandateTypeList(), "");
		fillComboBox(this.exclusionStatus, "", PennantStaticListUtil.getMandateTypeList(), "");//FIXME
		fillComboBox(this.batchReference, "", getPresentmentReference(), "");

		this.product.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.product.setModuleName("FinanceType");
		this.product.setValueColumn("FinType");
		this.product.setDescColumn("FinTypeDesc");
		this.product.setValidateColumns(new String[] { "FinType" });

		this.presentmentIdMap.clear();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_PresentmentDetailList_PresentmentDetailSearch(Event event) {
		doSetValidations();
		search();
	}

	@Override
	public void search() {
		logger.debug("Entering");
		// Set the first page as the active page.
		if (paging != null) {
			this.paging.setActivePage(0);
		}

		JdbcSearchObject<PresentmentDetail> searchObject = new JdbcSearchObject<>();
		searchObject.addField("DETAILID");
		searchObject.addFilterEqual("EXTRACTID", Long.valueOf(this.batchReference.getValue()));

		this.listbox.setItemRenderer(new PresentmentDetailListModelItemRenderer());

		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().init(this.searchObject, this.listbox, this.paging);

		logger.debug("Leaving");
	}
	
	private void doSetValidations() {
		Clients.clearWrongValue(this.batchReference);
		
		if (Labels.getLabel("Combo.Select").equals(this.batchReference.getSelectedItem().getLabel())) {
			throw new WrongValueException(this.batchReference, " Batch Reference is mandatory.");
		}
	}

	public void onClick$button_PresentmentDetailList_CreateBatch(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doSetValidations();

		if (this.listBoxPresentmentDetail.getItems().size() > 0) {
			MessageUtil.showErrorMessage("No records are available to extract.");
		}

		PresentmentHeader presentmentHeader = new PresentmentHeader();
		
		presentmentHeader.setPresentmentID(0);
		presentmentHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		presentmentHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		presentmentDetailService.savePresentmentHeader(presentmentHeader);

		List<Long> extractIdList = getPresentmentIds();
		presentmentDetailService.processPresentmentDetails(extractIdList);

		logger.debug(Literal.LEAVING);
	}

	
	/**
	 * Getting the mandate list using JdbcSearchObject with search criteria..
	 */
	private List<Long> getPresentmentIds() {

		JdbcSearchObject<Map<String, Long>> searchObject = new JdbcSearchObject<>();
		searchObject.addFilterEqual("EXTRACTID", Long.valueOf(this.batchReference.getValue()));
		searchObject.addFilterEqual("DETAILID", Long.valueOf(this.batchReference.getValue()));
		searchObject.addTabelName("Presentmentdetails");

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				searchObject.addFilter(filter);
			}
		}

		List<Map<String, Long>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		List<Long> idList = new ArrayList<Long>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Long> map = (Map<String, Long>) list.get(i);
				idList.add(Long.parseLong(String.valueOf(map.get("EXTRACTID"))));
			}
		}
		return idList;
	}
	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
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

	private static ArrayList<ValueLabel> getPresentmentReference() {
		ArrayList<ValueLabel> list = new ArrayList<ValueLabel>();
		PagedListService service = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<ValueLabel> so = new JdbcSearchObject<ValueLabel>(ValueLabel.class);

		so.addTabelName("PRESENTMENTDETAILHEADER");
		so.addField(" ExtractId Value");
		so.addField(" ExtractReference AS Label");
		List<ValueLabel> ids = service.getBySearchObject(so);

		ValueLabel label = null;
		for (int i = 0; i < ids.size(); i++) {
			label = new ValueLabel(ids.get(i).getLabel(), ids.get(i).getValue());
			list.add(label);
		}
		return list;
	}

	/**
	 * Item renderer for list items in the list box.
	 * 
	 */
	public class PresentmentDetailListModelItemRenderer implements ListitemRenderer<PresentmentDetail>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, PresentmentDetail presentmentDetail, int count) throws Exception {

			Listcell lc;

			lc = new Listcell("Customer");
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

		}
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

}