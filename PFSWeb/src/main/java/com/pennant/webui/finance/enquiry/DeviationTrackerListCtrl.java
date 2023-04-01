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
 * * FileName : WIFFinanceMainListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/FinanceInquiry/DeviationTrackerList.zul file.
 */
public class DeviationTrackerListCtrl extends GFCBaseListCtrl<FinanceDeviations> {
	private static final long serialVersionUID = 2808357374960437326L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DeviationTrackerList; // autowired
	protected Borderlayout borderLayout_DeviationTrackerList; // autowired
	protected Listbox listBoxApprovedDeviationDetails; // autowired

	protected Textbox finreference; // autowired
	protected Listbox sortOperator_finreference; // autowired

	protected Datebox deviationDate; // autowired
	protected Listbox sortOperator_deviationDate; // autowired

	// List headers

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_DeviationTrackerList_Search; // autowired
	protected Button button_DeviationTrackerList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceDeviations> searchObj;

	/**
	 * default constructor.<br>
	 */
	public DeviationTrackerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {

	}

	public void onCreate$window_DeviationTrackerList(Event event) {
		logger.debug("Entering");

		this.sortOperator_deviationDate
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_deviationDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finreference
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finreference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_DeviationTrackerList.setHeight(getBorderLayoutHeight());
		this.listBoxApprovedDeviationDetails.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("WIFFinanceMainList");
		this.button_DeviationTrackerList_Search
				.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainList_WIFFinanceMainFindDialog"));
		/*
		 * this.button_DeviationTrackerList_PrintList.setVisible(getUserWorkspace ().isAllowed(
		 * "button_WIFFinanceMainList_PrintList"));
		 */

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
		MessageUtil.showHelpWindow(event, window_DeviationTrackerList);
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

		this.sortOperator_deviationDate.setSelectedIndex(0);
		this.deviationDate.setValue(null);

		this.sortOperator_finreference.setSelectedIndex(0);
		this.finreference.setValue("");

		doSearch();

		logger.debug("Leaving");
	}

	/*
	 * call the WIFFinanceMain dialog
	 */
	public void onClick$button_DeviationTrackerList_Search(Event event) {
		logger.debug("Entering");

		doSearch();

		logger.debug("Leaving");
	}

	/**
	 * When the wIFFinanceMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_WIFFinanceMainList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	public void doSearch() {
		logger.debug("Entering");

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceDeviations>(FinanceDeviations.class);
		this.searchObj.addTabelName("DeviationTracker_View");
		this.searchObj.addFilterEqual("Module", DeviationConstants.TY_CHECKLIST);
		this.searchObj.addFilterEqual("ApprovalStatus", PennantConstants.RCD_STATUS_APPROVED);

		if (this.deviationDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_deviationDate.getSelectedItem(),
					DateUtil.format(this.deviationDate.getValue(), PennantConstants.DBDateFormat), "DeviationDate");
		}
		if (StringUtils.isNotBlank(this.finreference.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finreference.getSelectedItem(),
					this.finreference.getValue(), "finreference");
		}

		// Set the ListModel for the articles.
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<FinanceDeviations> list = pagedListService.getBySearchObject(this.searchObj);

		doFillDeviationDetails(list, listBoxApprovedDeviationDetails);

		logger.debug("Leaving");
	}

	List<ValueLabel> chkdevTypes = PennantStaticListUtil.getCheckListDeviationType();

	public void doFillDeviationDetails(List<FinanceDeviations> financeDeviations, Listbox listbox) {
		logger.debug("Entering");

		listbox.getItems().clear();
		for (FinanceDeviations deviationDetail : financeDeviations) {

			Listcell listcell;

			Listgroup listGroup = new Listgroup();

			listcell = getNewListCell(deviationDetail.getFinReference());
			listGroup.appendChild(listcell);

			listcell = getNewListCell(deviationDetail.getCustCIF());
			listGroup.appendChild(listcell);

			String devCode = deviationDetail.getDeviationCode();

			CheckList checkList = getCheckList(devCode.substring(0, devCode.indexOf("_")));
			if (checkList == null) {
				continue;
			}
			String cskDevType = devCode.substring(devCode.indexOf("_"));

			listcell = getNewListCell(checkList.getCheckListDesc());
			listGroup.appendChild(listcell);

			listcell = getNewListCell(PennantStaticListUtil.getlabelDesc(cskDevType, chkdevTypes));
			listGroup.appendChild(listcell);

			listcell = getNewListCell(getDeviationValue(deviationDetail));
			listGroup.appendChild(listcell);

			listcell = new Listcell(PennantStaticListUtil.getlabelDesc(deviationDetail.getDelegationRole(),
					PennantAppUtil.getSecRolesList(null)));
			listGroup.appendChild(listcell);

			listcell = getNewListCell(DateUtil.formatToShortDate(deviationDetail.getDeviationDate()));
			listGroup.appendChild(listcell);

			long devaitiondate = deviationDetail.getDeviationDate().getTime();
			long day = 24 * 60 * 60 * 1000;
			int days = 0;
			if (DeviationConstants.DT_INTEGER.equals(deviationDetail.getDeviationType())) {
				days = Integer.parseInt(deviationDetail.getDeviationValue());
			}
			long expected = devaitiondate + (day * days);
			Date expecteddate = new Date(expected);
			listcell = getNewListCell(DateUtil.formatToShortDate(expecteddate));
			listGroup.appendChild(listcell);

			List<CheckListDetail> checkListDetails = getCheckListDetail(checkList.getCheckListId());
			String status = "";
			if (checkListDetails != null) {
				status = getStatus(deviationDetail, checkList, checkListDetails);
			}
			listcell = getNewListCell(status);
			listGroup.appendChild(listcell);

			listbox.appendChild(listGroup);

			if (checkListDetails == null) {
				continue;
			}

			for (CheckListDetail checkListDetail : checkListDetails) {
				Listitem listitem = new Listitem();

				listcell = getNewListCell("");
				listitem.appendChild(listcell);

				listcell = getNewListCell("");
				listitem.appendChild(listcell);

				listcell = getNewListCell(checkListDetail.getLovDescDocCategory());
				listitem.appendChild(listcell);

				listcell = getNewListCell("");
				listitem.appendChild(listcell);

				listcell = getNewListCell("");
				listitem.appendChild(listcell);

				listcell = new Listcell("");
				listitem.appendChild(listcell);

				listcell = getNewListCell("");
				listitem.appendChild(listcell);

				listcell = getNewListCell("");
				listitem.appendChild(listcell);

				listcell = getNewListCell("");
				listitem.appendChild(listcell);

				listbox.appendChild(listitem);
			}
		}

		logger.debug("Leaving");
	}

	private String getStatus(FinanceDeviations deviationDetail, CheckList checkList,
			List<CheckListDetail> checkListDetails) {

		List<String> docTypes = new ArrayList<>();
		for (CheckListDetail checkListDetail : checkListDetails) {
			docTypes.add(checkListDetail.getDocType());
		}

		List<CustomerDocument> documents = getCustomerDcuments(deviationDetail.getCustID(), docTypes);
		int docSize = documents.size();
		if (docSize >= checkList.getCheckMinCount()) {
			return "Deviation Cleared";
		} else {
			return "Deviation Pending";
		}

	}

	public String getDeviationValue(FinanceDeviations deviationDetail) {

		String devType = deviationDetail.getDeviationType();
		String devValue = deviationDetail.getDeviationValue();

		if (DeviationConstants.DT_BOOLEAN.equals(devType)) {

			return devValue;

		} else if (DeviationConstants.DT_PERCENTAGE.equals(devType)) {

			return devValue + " % ";

		} else if (DeviationConstants.DT_DECIMAL.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			return CurrencyUtil.format(amount, 2);

		} else if (DeviationConstants.DT_INTEGER.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			return Integer.toString(amount.intValue());
		}
		return "";
	}

	private Listcell getNewListCell(String val) {
		Listcell listcell = new Listcell(val);
		return listcell;
	}

	class CompareDeviation implements Comparator<FinanceDeviations> {

		public CompareDeviation() {

		}

		@Override
		public int compare(FinanceDeviations o1, FinanceDeviations o2) {
			return o1.getModule().compareTo(o2.getModule());
		}
	}

	/**
	 * @param value
	 * @return
	 */
	private CheckList getCheckList(String value) {

		logger.debug(" Entering ");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CheckList> searchObject = new JdbcSearchObject<CheckList>(CheckList.class);
		searchObject.addTabelName("BMTCheckList");
		searchObject.addFilterIn("CheckListId", value);
		List<CheckList> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {
			logger.debug(" Leaving ");
			return list.get(0);
		}
		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * @param value
	 * @return
	 */
	private List<CheckListDetail> getCheckListDetail(long value) {
		logger.debug(" Entering ");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CheckListDetail> searchObject = new JdbcSearchObject<CheckListDetail>(CheckListDetail.class);
		searchObject.addTabelName("RMTCheckListDetails_View");
		searchObject.addFilterEqual("CheckListId", value);
		searchObject.addFilterEqual("DocRequired", 1);
		logger.debug(" Leaving ");
		return pagedListService.getBySearchObject(searchObject);
	}

	/**
	 * @param value
	 * @return
	 */
	private List<CustomerDocument> getCustomerDcuments(long value, List<String> doctypes) {
		logger.debug(" Entering ");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CustomerDocument> searchObject = new JdbcSearchObject<CustomerDocument>(
				CustomerDocument.class);
		searchObject.addTabelName("CustomerDocuments");
		searchObject.addFilterEqual("CustID", value);
		searchObject.addFilterIn("CustDocCategory", doctypes, false);

		searchObject.addField("CustID");
		searchObject.addField("CustDocType");
		searchObject.addField("CustDocTitle");
		searchObject.addField("CustDocName");
		searchObject.addField("CustDocSysName");
		searchObject.addField("CustDocRcvdOn");
		searchObject.addField("CustDocExpDate");
		searchObject.addField("CustDocIssuedOn");

		logger.debug(" Leaving ");
		return pagedListService.getBySearchObject(searchObject);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<FinanceDeviations> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceDeviations> so) {
		this.searchObj = so;
	}

}