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
 * * FileName : AcademicListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-05-2011 * * Modified
 * Date : 23-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.customer.enquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jaxen.JaxenException;
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
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Academic/CustomerSummary.zul file.
 */
public class CustomerSummaryListCtrl extends GFCBaseListCtrl<Customer> {
	private static final long serialVersionUID = 5327118548986437717L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
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
	private CustomerLimitIntefaceService customerLimitIntefaceService;
	protected JdbcSearchObject<Customer> searchObj;
	// NEEDED for the ReUse in the SearchWindow
	private boolean finance = false;
	private String custBranch = "";

	/**
	 * default constructor.<br>
	 */
	public CustomerSummaryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {

	}

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected AcademicCode object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustomerSummary(Event event) {
		logger.debug("Entering" + event.toString());
		if (arguments.containsKey("finance")) {
			finance = true;
		}
		if (finance) {
			if (arguments.get("custid") != null) {
				long custid = Long.parseLong(arguments.get("custid").toString());
				this.custID.setValue(custid);
				this.custCIF.setValue(arguments.get("custCIF") != null ? arguments.get("custCIF").toString() : "");
				this.custShrtName.setValue(
						arguments.get("custShrtName") != null ? arguments.get("custShrtName").toString() : "");
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
			this.borderLayout_AcademicList.setHeight(borderLayoutHeight + "px");
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
		MessageUtil.showHelpWindow(event, window_CustomerSummary);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.searchObj = newSearchObject;
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.custBranch = aCustomer.getCustDftBranch();
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
			throw new WrongValueException(this.custCIF,
					Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CustCIF.value") }));
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

		Map<String, Object> map = getDefaultArguments();
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
				JdbcSearchObject<FinanceEnquiry> finSearchObj = new JdbcSearchObject<FinanceEnquiry>(
						FinanceEnquiry.class);
				finSearchObj.addTabelName("Financemain_AView");
				finSearchObj.addSort("CustID", false);
				finSearchObj.addFilterEqual("CustID", custid);
				fillFiannceListBox(getPagedListWrapper().getPagedListService().getBySearchObject(finSearchObj),
						this.listBoxFinance);
				JdbcSearchObject<Commitment> cmtSearchObj = new JdbcSearchObject<Commitment>(Commitment.class);
				cmtSearchObj.addTabelName("Commitments_AView");
				cmtSearchObj.addSort("CustID", false);
				cmtSearchObj.addFilterEqual("CustID", custid);
				fillCommitmentListBox(getPagedListWrapper().getPagedListService().getBySearchObject(cmtSearchObj),
						this.listBoxCommitment);
				fillLimitListBox(this.custCIF.getValue(), custBranch);

				JdbcSearchObject<CustomerCollateral> collateralSearchObj = new JdbcSearchObject<CustomerCollateral>(
						CustomerCollateral.class);
				collateralSearchObj.addTabelName("Collateral_summary_view");
				collateralSearchObj.addFilterEqual("custID", custid);
				doFillCustomerCollateral(
						getPagedListWrapper().getPagedListService().getBySearchObject(collateralSearchObj),
						this.listBoxCustCollateral);
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
		Map<String, Object> map = getDefaultArguments();
		map.put("financeEnquiry", (FinanceEnquiry) object);
		map.put("enquiryType", "FINENQ");
		try {
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul", null,
					map);
		} catch (Exception e) {
			logger.error("Exception: ", e);
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

				int formatter = CurrencyUtil.getFormat(commitment.getCmtCcy());

				lc = new Listcell(commitment.getCmtReference());
				lc.setParent(item);
				lc = new Listcell(commitment.getCmtBranch());
				lc.setParent(item);
				lc = new Listcell(commitment.getCmtCcy());
				lc.setParent(item);
				lc = new Listcell(commitment.getCmtAccount());
				lc.setParent(item);
				lc = new Listcell(DateUtil.format(commitment.getCmtExpDate(), PennantConstants.dateFormat));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAmount(), formatter));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtUtilizedAmount(), formatter));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAvailable(), formatter));
				lc.setParent(item);
				lc = new Listcell(DateUtil.format(commitment.getCmtStartDate(), PennantConstants.dateFormat));
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
		Map<String, Object> map = getDefaultArguments();
		map.put("commitment", (Commitment) object);
		map.put("enqModule", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param custlimitCategoryList
	 * @throws JaxenException
	 * @throws InterfaceException
	 * @throws CustomerLimitProcessException
	 * @throws InterruptedException
	 */
	public void fillLimitListBox(String custMnemonic, String custBranch) throws JaxenException, InterfaceException {
		logger.debug("Entering");
		this.listBoxCustomerLimit.getItems().clear();
		CustomerLimit limit = new CustomerLimit();

		limit.setCustMnemonic(custMnemonic);
		if (StringUtils.isBlank(custBranch)) {
			Customer customer = (Customer) PennantAppUtil.getCustomerObject(custMnemonic, null);
			custBranch = customer.getCustDftBranch();
		}
		limit.setLimitBranch(custBranch);
		limit.setCustLocation("");
		List<com.pennant.coreinterface.model.CustomerLimit> list = null;
		try {
			list = getCustomerLimitIntefaceService().fetchLimitEnquiryDetails(limit);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		if (list != null) {
			for (CustomerLimit category : list) {
				Currency currency = getCurrencyService().getCurrencyById(category.getLimitCurrency());
				category.setLimitCcyEdit(currency != null ? currency.getCcyEditField() : 0);
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(category.getLimitCategory());
				lc.setParent(item);
				lc = new Listcell(category.getLimitCategoryDesc());
				lc.setParent(item);
				/*
				 * lc = new Listcell(PennantAppUtil.amountFormate(category.getRiskAmount(),
				 * category.getLimitCcyEdit())); lc.setParent(item);
				 */
				lc = new Listcell(CurrencyUtil.format(category.getLimitAmount(), category.getLimitCcyEdit()));
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(category.getAvailAmount(), category.getLimitCcyEdit()));
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
	private void doFillCustomerCollateral(List<CustomerCollateral> list, Listbox listbox) {
		this.listBoxCustCollateral.getItems().clear();
		if (list != null && !list.isEmpty()) {
			// int formatter = 6;
			for (CustomerCollateral customerCollateral : list) {
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

				cell = new Listcell(
						DateUtil.format((Date) customerCollateral.getCollExpDate(), PennantConstants.dateFormat));
				cell.setParent(item);
				cell = new Listcell(DateUtil.format((Date) customerCollateral.getColllastRvwDate(),
						PennantConstants.dateFormat));
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollValue().toString());
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollBankVal().toString());
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				BigDecimal per = new BigDecimal(customerCollateral.getCollBankValMar().toString())
						.divide(BigDecimal.valueOf(Math.pow(10, 2)), RoundingMode.HALF_UP);
				cell = new Listcell(per + "%");
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getColllocationDesc());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getColllocation());
				cell.setParent(item);
				listbox.appendChild(item);
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
}