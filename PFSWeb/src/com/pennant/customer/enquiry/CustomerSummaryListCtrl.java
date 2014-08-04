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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.corebanking.interfaces.CustomerInterfaceCall;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.vo.CustomerCollateral;
import com.pennant.coreinterface.vo.CustomerLimit;
import com.pennant.util.PennantAppUtil;
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
	private static final long serialVersionUID = 5327118548986437717L;
	private final static Logger logger = Logger.getLogger(CustomerSummaryListCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerSummary;
	protected Borderlayout borderLayout_AcademicList;
	protected Button btnHelp;
	protected Longbox custID;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected Button btnSearchCustCIF;
	protected Listbox listBoxFinance;
	protected Listbox listBoxCommitment;
	protected Listbox listBoxCustomerLimit;
	protected Listbox listBoxCustCollateral;
	private CurrencyService currencyService;
	private CustomerInterfaceCall customerInterfaceCall;
	private CustomerLimitIntefaceService customerLimitIntefaceService;
	protected JdbcSearchObject<Customer> searchObj;
	// NEEDED for the ReUse in the SearchWindow
	private boolean finance = false;

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
		Map<String, Object> args = getCreationArgsMap(event);
		if (args.containsKey("finance")) {
			finance = true;
		}
		if (finance) {
			if (args.get("custid") != null) {
				long custid = Long.parseLong(args.get("custid").toString());
				this.custID.setValue(custid);
				this.custCIF.setValue(args.get("custCIF") != null ? args.get("custCIF").toString() : "");
				this.custShrtName.setValue(args.get("custShrtName") != null ? args.get("custShrtName").toString() : "");
				prepareTabs();
			}
			this.btnSearchCustCIF.setVisible(false);
			this.custCIF.setDisabled(true);
			this.listBoxFinance.setTooltiptext("");
			this.listBoxCommitment.setTooltiptext("");
			this.window_CustomerSummary.setWidth("95%");
			this.window_CustomerSummary.setHeight("95%");
			this.window_CustomerSummary.setClosable(true);
			this.window_CustomerSummary.setTitle(Labels.getLabel("menu_Item_CustomerSummary"));
			this.window_CustomerSummary.doModal();
		} else {
			this.borderLayout_AcademicList.setHeight(calculateBorderLayoutHeight()+ "px");
		}
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
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.searchObj = newSearchObject;
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		prepareTabs();
		logger.debug("Leaving");
	}

	/**
	 * To onChange$custCIF
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$custCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.custCIF.clearErrorMessage();
		Customer customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);
		if (customer == null) {
			this.custShrtName.setValue("");
			this.custID.setValue(Long.valueOf(0));
			if (listBoxFinance.getItems() != null) {
				listBoxFinance.getItems().clear();
			}
			if (listBoxCommitment.getItems() != null) {
				listBoxCommitment.getItems().clear();
			}
			if (listBoxCustomerLimit.getItems() != null) {
				listBoxCustomerLimit.getItems().clear();
			}
			if (listBoxCustCollateral.getItems() != null) {
				listBoxCustCollateral.getItems().clear();
			}
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To onClick$btnSearchCustCIF
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) {
		try {
			onLoad();
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
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

	/**
	 * To prepareTabs
	 */
	private void prepareTabs() {
		try {
			if (this.custID.getValue() != null && this.custID.getValue() != 0) {
				String custid = String.valueOf(this.custID.getValue());
				JdbcSearchObject<FinanceEnquiry> finSearchObj = new JdbcSearchObject<FinanceEnquiry>(FinanceEnquiry.class);
				finSearchObj.addTabelName("Financemain_AView");
				finSearchObj.addSort("CustID", false);
				finSearchObj.addFilterEqual("CustID", custid);
				fillFiannceListBox(getPagedListWrapper().getPagedListService().getBySearchObject(finSearchObj), this.listBoxFinance);
				JdbcSearchObject<Commitment> cmtSearchObj = new JdbcSearchObject<Commitment>(Commitment.class);
				cmtSearchObj.addTabelName("Commitments_AView");
				cmtSearchObj.addSort("CustID", false);
				cmtSearchObj.addFilterEqual("CustID", custid);
				fillCommitmentListBox(getPagedListWrapper().getPagedListService().getBySearchObject(cmtSearchObj), this.listBoxCommitment);
				fillLimitListBox(this.custCIF.getValue());
				doFillCustomerCollateral(getCustomerInterfaceCall().getCustomerCollateral(this.custCIF.getValue()));
			}
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	/**
	 * To fillFiannceListBox
	 * 
	 * @param list
	 * @param listbox
	 */
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
				if (!finance) {
					ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceItemDoubleClicked");
				}
				listbox.appendChild(item);
			}
		}
	}

	/**
	 * To onFinanceItemDoubleClicked
	 * 
	 * @param event
	 */
	public void onFinanceItemDoubleClicked(Event event) {
		Listitem listitem = this.listBoxFinance.getSelectedItem();
		Object object = listitem.getAttribute("data");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeEnquiry", (FinanceEnquiry) object);
		map.put("enquiryType", "FINENQ");
		try {
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}
	}

	/**
	 * To fillCommitmentListBox
	 * 
	 * @param list
	 * @param listbox
	 */
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
				if (!finance) {
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCommitmentItemDoubleClicked");
				}
				listbox.appendChild(item);
			}
		}
	}

	/**
	 * To onCommitmentItemDoubleClicked
	 * 
	 * @param event
	 */
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
		int formatter = 3;
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
				lc = new Listcell(PennantAppUtil.amountFormate(category.getRiskAmount().divide(new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP), 0));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(category.getLimitAmount().divide(new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP), 0));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(category.getAvailAmount().divide(new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP), 0));
				lc.setParent(item);
				this.listBoxCustomerLimit.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * To doFillCustomerCollateral
	 * 
	 * @param collaterals
	 */
	private void doFillCustomerCollateral(List<CustomerCollateral> collaterals) {
		this.listBoxCustCollateral.getItems().clear();
		if (collaterals != null && !collaterals.isEmpty()) {
			int formatter = 6;
			for (CustomerCollateral customerCollateral : collaterals) {
				Listitem item = new Listitem();
				Listcell cell;
				cell = new Listcell(customerCollateral.getCollReference());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollType());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollTypeDesc());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollComplete());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollCcy());
				cell.setParent(item);
				Date date = DateUtility.convertDateFromAS400(new BigDecimal(customerCollateral.getCollExpDate().toString()));
				cell = new Listcell(DateUtility.formatUtilDate(date, PennantConstants.dateFormate));
				cell.setParent(item);
				Date date1 = DateUtility.convertDateFromAS400(new BigDecimal(customerCollateral.getColllastRvwDate().toString()));
				cell = new Listcell(DateUtility.formatUtilDate(date1, PennantConstants.dateFormate));
				cell.setParent(item);
				cell = new Listcell(PennantAppUtil.amountFormate(new BigDecimal(customerCollateral.getCollValue().toString()).divide(new BigDecimal(Math.pow(10, formatter)), RoundingMode.HALF_UP), 0));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(PennantAppUtil.amountFormate(new BigDecimal(customerCollateral.getCollBankVal().toString()).divide(new BigDecimal(Math.pow(10, formatter)), RoundingMode.HALF_UP), 0));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				BigDecimal per = new BigDecimal(customerCollateral.getCollBankValMar().toString()).divide(new BigDecimal(Math.pow(10, 2)), RoundingMode.HALF_UP);
				cell = new Listcell(per + "%");
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getColllocationDesc());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getColllocation());
				cell.setParent(item);
				this.listBoxCustCollateral.appendChild(item);
			}
		}
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

	public void setCustomerInterfaceCall(CustomerInterfaceCall customerInterfaceCall) {
		this.customerInterfaceCall = customerInterfaceCall;
	}

	public CustomerInterfaceCall getCustomerInterfaceCall() {
		return customerInterfaceCall;
	}
}