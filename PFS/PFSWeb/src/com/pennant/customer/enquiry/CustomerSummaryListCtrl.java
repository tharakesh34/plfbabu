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
 * FileName    		:  AcademicListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-05-2011    														*
 *                                                                  						*
 * Modified Date    :  23-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.customer.enquiry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.vo.CustomerLimit;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Academic/AcademicList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
@SuppressWarnings("rawtypes")
public class CustomerSummaryListCtrl extends GFCBaseListCtrl implements Serializable {

	private static final long	         serialVersionUID	= 5327118548986437717L;
	private final static Logger	         logger	          = Logger.getLogger(CustomerSummaryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	                 window_CustomerSummary;	                                         // autoWired
	protected Borderlayout	             borderLayout_AcademicList;	                                     // autoWired

	// List headers
	protected Listheader	             listheader_AcademicLevel;	                                         // autoWired
	protected Listheader	             listheader_AcademicDecipline;	                                     // autoWired
	protected Listheader	             listheader_AcademicDesc;	                                         // autoWired
	protected Listheader	             listheader_RecordStatus;	                                         // autoWired
	protected Listheader	             listheader_RecordType;

	// checkRights
	protected Button	                 btnHelp;	                                                         // autoWired

	protected Longbox	                 custID;
	protected Textbox	                 custCIF;
	protected Label	                     custShrtName;
	protected Button	                 btnSearchCustCIF;

	protected Listbox	                 listBoxFinance;
	protected Listbox	                 listBoxCommitment;
	protected Listbox	                 listBoxCustomerLimit;
	private CustomerLimitIntefaceService	customerLimitIntefaceService;
	private CurrencyService	             currencyService;
	protected JdbcSearchObject<Customer>	searchObj;
	// NEEDED for the ReUse in the SearchWindow

	Iframe	                             report;

	/**
	 * default constructor.<br>
	 */
	public CustomerSummaryListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AcademicCode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerSummary(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		this.borderLayout_AcademicList.setHeight(calculateBorderLayoutHeight() + 30 + "px");
		//		int height = calculateBorderLayoutHeight() - 45;
		//		int listboxheight = height / 3;
		//
		//		this.listBoxFinance.setHeight(listboxheight + "px");
		//		this.listBoxCommitment.setHeight(listboxheight + "px");
		//		this.listBoxCustomerLimit.setHeight(listboxheight + "px");

		//onLoad();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerSummary);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.searchObj=newSearchObject;
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		prepareTabs();
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustCIF(Event event) {
		try {
			onLoad();
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onLoad() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", searchObj);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	private void prepareTabs() {

		// Apply sorting for getting List in the ListBox
		//		this.listheader_AcademicLevel.setSortAscending(new FieldComparator("academicLevel", true));
		//		this.listheader_AcademicLevel.setSortDescending(new FieldComparator("academicLevel", false));

		if (this.custID.getValue() != null && this.custID.getValue() != 0) {
			// ++ create the searchObject and initial sorting ++//
			String custid = String.valueOf(this.custID.getValue());
			JdbcSearchObject<FinanceEnquiry> finSearchObj = new JdbcSearchObject<FinanceEnquiry>(FinanceEnquiry.class);
			finSearchObj.addTabelName("Financemain");
			finSearchObj.addSort("CustID", false);
			finSearchObj.addFilterEqual("CustID", custid);
			fillFiannceListBox(getPagedListWrapper().getPagedListService().getBySearchObject(finSearchObj), this.listBoxFinance);

			JdbcSearchObject<Commitment> cmtSearchObj = new JdbcSearchObject<Commitment>(Commitment.class);
			cmtSearchObj.addTabelName("Commitments_AView");
			cmtSearchObj.addSort("CustID", false);
			cmtSearchObj.addFilterEqual("CustID", custid);
			fillCommitmentListBox(getPagedListWrapper().getPagedListService().getBySearchObject(cmtSearchObj), this.listBoxCommitment);
			fillLimitListBox(this.custCIF.getValue());

		}

	}

	private void fillFiannceListBox(List<FinanceEnquiry> list, Listbox listbox) {
		listbox.getItems().clear();
		if (list != null && list.size() > 0) {
			for (FinanceEnquiry financeMain : list) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(financeMain.getFinReference());
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinType());
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinCcy());
				lc.setParent(item);
				lc = new Listcell(financeMain.getScheduleMethod() == null ? "" : financeMain.getScheduleMethod());
				lc.setParent(item);
				lc = new Listcell(financeMain.getProfitDaysBasis());
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinBranch());
				lc.setParent(item);
				item.setAttribute("data", financeMain);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceItemDoubleClicked");
				listbox.appendChild(item);
			}
		}

	}

	public void onFinanceItemDoubleClicked(Event event) {
		Listitem listitem = this.listBoxFinance.getSelectedItem();
		Object object = listitem.getAttribute("data");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeEnquiry", (FinanceEnquiry) object);
		map.put("enquiryType", "FINENQ");

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}

	}

	private void fillCommitmentListBox(List<Commitment> list, Listbox listbox) {
		listbox.getItems().clear();
		if (list != null && list.size() > 0) {
			for (Commitment commitment : list) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(commitment.getCmtReference());
				lc.setParent(item);
				lc = new Listcell(commitment.getCmtBranch());
				lc.setParent(item);
				lc = new Listcell(commitment.getCmtCcy());
				lc.setParent(item);
				lc = new Listcell(commitment.getCmtAccount());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formateDate(commitment.getCmtExpDate(), PennantConstants.dateFormat));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAmount(), commitment.getCcyEditField()));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtUtilizedAmount(), commitment.getCcyEditField()));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAvailable(), commitment.getCcyEditField()));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formateDate(commitment.getCmtStartDate(), PennantConstants.dateFormat));
				lc.setParent(item);
				item.setAttribute("data", commitment);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCommitmentItemDoubleClicked");
				listbox.appendChild(item);
			}
		}
	}

	public void onCommitmentItemDoubleClicked(Event event) {
		Listitem listitem = this.listBoxCommitment.getSelectedItem();
		Object object = listitem.getAttribute("data");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commitment", (Commitment) object);
		map.put("enqModule", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param custlimitCategoryList
	 * @throws CustomerLimitProcessException 
	 * @throws InterruptedException 
	 */
	public void fillLimitListBox(String custMnemonic) {
		logger.debug("Entering");
		this.listBoxCustomerLimit.getItems().clear();
		CustomerLimit limit = new CustomerLimit();
		limit.setCustMnemonic(custMnemonic);
		limit.setCustLocation("");
		List<com.pennant.coreinterface.vo.CustomerLimit> list = null;
		int formatter = 0;
		try {
			list = getCustomerLimitIntefaceService().fetchLimitEnquiryDetails(limit);
		} catch (CustomerLimitProcessException e) {
			logger.error(e.getMessage());
		}

		if (list != null) {
			for (CustomerLimit category : list) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(category.getLimitCategory());
				lc.setParent(item);
				lc = new Listcell(category.getLimitCategoryDesc());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(category.getRiskAmount().divide(new BigDecimal(Math.pow(10, formatter)), RoundingMode.HALF_UP)));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(category.getLimitAmount().divide(new BigDecimal(Math.pow(10, formatter)), RoundingMode.HALF_UP)));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(category.getAvailAmount().divide(new BigDecimal(Math.pow(10, formatter)), RoundingMode.HALF_UP)));
				lc.setParent(item);
				item.setAttribute("data", category);
				item.setId(category.getLimitCategory());
				//	ComponentsCtrl.applyForward(item, "onDoubleClick=onCategoryItemDoubleClicked");
				this.listBoxCustomerLimit.appendChild(item);
			}

		}
		logger.debug("Leaving");
	}

	public void setCustomerLimitIntefaceService(CustomerLimitIntefaceService customerLimitIntefaceService) {
		this.customerLimitIntefaceService = customerLimitIntefaceService;
	}

	public CustomerLimitIntefaceService getCustomerLimitIntefaceService() {
		return customerLimitIntefaceService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

}