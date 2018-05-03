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
 * FileName    		:  WIFFinanceMainListCtrl.java                                                   * 	  
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
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.DdaPresentment;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.ddapayments.impl.DDARepresentmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Enquiry/FinanceInquiry/DDARepresentmentList.zul file.
 */
public class DDARepresentmentListCtrl extends GFCBaseListCtrl<DdaPresentment> {
	private static final long serialVersionUID = 2808357374960437326L;
	private static final Logger logger = Logger
			.getLogger(DDARepresentmentListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DDARepresentmentList; // autowired
	protected Borderlayout borderLayout_DDAPresentmentList; // autowired
	protected Paging pagingWIFFinanceMainList; // autowired
	protected Listbox listBoxDdaPresentment; // autowired

	protected Datebox ddaDate; // autowired
	protected Listbox sortOperator_DDADate; // autowired
	protected Checkbox noPay; // autowired

	// List headers
	protected Listheader listheader_DDAReference; // autowired
	protected Listheader listheader_DDADate;
	protected Listheader listheader_FinReference; // autowired
	protected Listheader listheader_InstallmentDate; // autowired
	protected Listheader listheader_Status; // autowired
	protected Listheader listheader_Reason; // autowired

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_DDARepresentmentList_Search; // autowired
	protected Button btnRepresent;

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DdaPresentment> searchObj;
	protected Grid searchGrid;

	private transient DDARepresentmentService ddaRepresentmentService;

	/**
	 * default constructor.<br>
	 */
	public DDARepresentmentListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		
	}

	public void onCreate$window_DDARepresentmentList(Event event)
			throws Exception {
		logger.debug("Entering");

		this.sortOperator_DDADate.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_DDADate
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_DDAPresentmentList.setHeight(getBorderLayoutHeight());
		this.listBoxDdaPresentment.setHeight(getListBoxHeight(searchGrid
				.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingWIFFinanceMainList.setPageSize(getListRows());
		this.pagingWIFFinanceMainList.setDetailed(true);

		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("WIFFinanceMainList");

		this.button_DDARepresentmentList_Search
				.setVisible(getUserWorkspace().isAllowed(
						"button_WIFFinanceMainList_WIFFinanceMainFindDialog"));

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_DDARepresentmentList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering");

		this.sortOperator_DDADate.setSelectedIndex(0);
		this.ddaDate.setValue(null);
		this.noPay.setChecked(false);

		doSearch();

		logger.debug("Leaving");
	}

	/*
	 * call the WIFFinanceMain dialog
	 */

	public void onClick$button_DDARepresentmentList_Search(Event event)
			throws Exception {
		logger.debug("Entering");

		doSearch();

		logger.debug("Leaving");
	}

	public void doSearch() {
		logger.debug("Entering");

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<DdaPresentment>(
				DdaPresentment.class);
		this.searchObj.addSort("ddaReference", false);
		this.searchObj.addField("ddaReference");
		this.searchObj.addField("ddaDate");
		this.searchObj.addField("ddaStatus");
		this.searchObj.addField("hostReference");
		this.searchObj.addField("requestData");
		this.searchObj.addField("responseData");

		this.searchObj.clearFilters();

		this.searchObj.addTabelName("DDARepresentment");

		if (this.ddaDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_DDADate.getSelectedItem(),
					DateUtility.formatDate(this.ddaDate.getValue(),
							PennantConstants.DBDateFormat), "ddaDate");
		}
		if (this.noPay.isChecked()) {
			searchObj = getSearchFilter(searchObj, Filter.OP_EQUAL, "NOPAY",
					"ddaStatus");
		}

		// Set the ListModel for the articles.
		PagedListService pagedListService = (PagedListService) SpringUtil
				.getBean("extPagedListService");
		List<DdaPresentment> list = pagedListService
				.getBySearchObject(this.searchObj);

		doFillList(list, listBoxDdaPresentment);

		logger.debug("Leaving");
	}

	public void doFillList(List<DdaPresentment> list, Listbox listbox) {
		logger.debug("Entering");

		listbox.getItems().clear();

		if (list == null || list.isEmpty()) {
			btnRepresent.setDisabled(true);
			return;
		} else {
			if (this.noPay.isChecked()) {
				btnRepresent.setDisabled(false);
			} else {
				btnRepresent.setDisabled(true);
			}
		}

		for (DdaPresentment item : list) {
			Listitem listitem = new Listitem();
			Listcell listcell;

			listcell = new Listcell(item.getDdaReference());
			listitem.appendChild(listcell);

			listcell = new Listcell(DateUtility.format(item.getDdaDate(),
					DateFormat.SHORT_DATE));
			listitem.appendChild(listcell);

			listcell = new Listcell(item.getFinReference());
			listitem.appendChild(listcell);

			listcell = new Listcell(item.getInstallmentDate());
			listitem.appendChild(listcell);

			listcell = new Listcell(item.getDdaStatus());
			listitem.appendChild(listcell);

			listcell = new Listcell(item.getReason());
			listitem.appendChild(listcell);

			listitem.setAttribute("data", item);
			listbox.appendChild(listitem);
		}

		logger.debug("Leaving");
	}

	public void onClick$btnRepresent(Event event) throws InterruptedException {
		logger.debug("Entering");

		List<DdaPresentment> presentments = new ArrayList<>();

		for (Listitem listitem : listBoxDdaPresentment.getItems()) {
			presentments.add((DdaPresentment) listitem.getAttribute("data"));
		}

		ddaRepresentmentService.representment(presentments);

		doSearch();

		MessageUtil.showMessage("ECS Representment batch processed successfully.");

		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<DdaPresentment> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<DdaPresentment> so) {
		this.searchObj = so;
	}

	public DDARepresentmentService getDdaRepresentmentService() {
		return ddaRepresentmentService;
	}

	public void setDdaRepresentmentService(
			DDARepresentmentService ddaRepresentmentService) {
		this.ddaRepresentmentService = ddaRepresentmentService;
	}
}