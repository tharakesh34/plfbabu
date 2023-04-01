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
 * * FileName : CustomerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul file.
 */
public class FinCustomerDetailsEnqCtrl extends GFCBaseCtrl<FinanceSummary> {
	private static final long serialVersionUID = 9086034736503097868L;
	private static final Logger logger = LogManager.getLogger(FinCustomerDetailsEnqCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUl-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinCustomerDetailsEnq; // autowired

	// ===================Customer Infomation
	protected Tabbox tabboxFinCustomer;
	protected Tab customerInfoTab;
	protected Textbox finCustSysref;
	protected Textbox finCustBranch;
	protected Textbox finCustName;
	protected Textbox finCustIDTypeNo;
	protected Textbox finCustEmployer;
	protected Textbox finCustCYOE;
	protected Textbox finCustAccNo;
	protected Textbox finCustNationality;
	protected Textbox finCustOccupation;
	protected Textbox finCustEducation;
	protected Textbox finCustPrvEmployer;
	protected Textbox finCustPrvYOE;
	protected Textbox finCustFDOB;
	protected Textbox finCustFAge;
	protected Textbox finCustSDOB;
	protected Textbox finCustSAge;
	protected Textbox finCustSector;
	protected Textbox finCustSubSector;
	protected Textbox finCustPhone;
	protected Textbox finCustFax;
	protected Textbox finCustMail;
	protected Textbox finCustCPR;
	protected Listbox listBoxCustomerIncome;
	protected Listbox listBoxFinances;
	private List<CustomerIncome> incomeList = null;
	private List<FinanceSummary> financeMains = null;
	private AgreementDetail custAgreementData = null;
	private AgreementDetail jointCustAgreementData = null;
	private List<CustomerEmploymentDetail> customerEmploymentDetails = null;
	private PagedListService pagedListService;
	private String employer[] = null;
	private long custid = 0;
	private String jointcustCif = "";
	private int finFormatter = 0;
	private String finReference = "";

	/**
	 * default constructor.<br>
	 */
	public FinCustomerDetailsEnqCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinCustomerDetailsEnq(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCustomerDetailsEnq);

		if (arguments.containsKey("custid")) {
			custid = (Long) arguments.get("custid");
		}
		if (arguments.containsKey("jointcustid")) {
			jointcustCif = (String) arguments.get("jointcustid");
		}
		if (arguments.containsKey("finFormatter")) {
			finFormatter = (Integer) arguments.get("finFormatter");
		}
		if (arguments.containsKey("finReference")) {
			finReference = (String) arguments.get("finReference");
		}
		this.tabboxFinCustomer.setHeight(this.borderLayoutHeight - 30 + "px");// 325px
		this.listBoxFinances.setHeight(this.borderLayoutHeight - 130 + "px");// 325px
		this.listBoxCustomerIncome.setHeight(this.borderLayoutHeight - 130 + "px");// 315px
		loadData();
		this.window_FinCustomerDetailsEnq.doModal();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		this.window_FinCustomerDetailsEnq.onClose();
		logger.debug("Leaving " + event.toString());
	}

	public void loadData() {
		try {
			Date appldate = SysParamUtil.getAppDate();
			if (custid != 0) {
				boolean isCustomerChanged = false;
				if (custAgreementData == null) {
					// Customer Data
					isCustomerChanged = true;
					custAgreementData = getCustomerAggrementData(custid, null);
				} else {
					// if Joint Customer chnaged Data
					if (custAgreementData.getCustId() != custid) {
						isCustomerChanged = true;
						custAgreementData = getCustomerAggrementData(custid, null);
					}
				}
				if (StringUtils.isNotBlank(jointcustCif) && jointCustAgreementData == null) {
					// Joint Customer Data
					jointCustAgreementData = getCustomerAggrementData(0, jointcustCif);
				}
				this.finCustSysref.setValue(finReference);
				this.finCustBranch.setValue(custAgreementData.getFinBranch());
				this.finCustName.setValue(custAgreementData.getCustName());
				this.finCustIDTypeNo.setValue("Passport" + "-" + custAgreementData.getCustPassport());// Passport-03
				this.finCustAccNo.setValue("");
				this.finCustNationality.setValue(custAgreementData.getCustNationality());
				this.finCustOccupation.setValue(custAgreementData.getCustEmpStsCode());
				this.finCustEducation.setValue("");
				// Customer Employment
				if (customerEmploymentDetails == null) {
					customerEmploymentDetails = getCustomerEmpDetails(custid);
				}
				if (customerEmploymentDetails != null && customerEmploymentDetails.size() > 0) {
					if (employer == null) {
						employer = getEmploymentDetails(customerEmploymentDetails);
					}
				}
				if (employer != null && employer.length == 4) {
					this.finCustEmployer.setValue(employer[0]);
					this.finCustCYOE.setValue(employer[1]);
					this.finCustPrvEmployer.setValue(employer[2]);
					this.finCustPrvYOE.setValue(employer[3]);
				}
				if (StringUtils.isNotBlank(custAgreementData.getCustDOB())) {
					Date dob = DateUtil.parseFullDate(custAgreementData.getCustDOB());
					this.finCustFDOB.setValue(DateUtil.formatToLongDate(dob));
					this.finCustFAge.setValue(String.valueOf(DateUtil.getYearsBetween(dob, appldate)));
				}
				if (jointCustAgreementData != null && StringUtils.isNotBlank(jointCustAgreementData.getCustDOB())) {
					Date dob = DateUtil.parseFullDate(jointCustAgreementData.getCustDOB());
					this.finCustSDOB.setValue(DateUtil.formatToLongDate(dob));
					this.finCustSAge.setValue(String.valueOf(DateUtil.getYearsBetween(dob, appldate)));
				}
				this.finCustSector.setValue("");// custAgreementData.getCustSector()
				this.finCustSubSector.setValue("");// custAgreementData.getCustSubSector()
				this.finCustPhone.setValue(custAgreementData.getCustMobile());
				this.finCustFax.setValue(custAgreementData.getCustFax());
				this.finCustMail.setValue("");
				this.finCustCPR.setValue(custAgreementData.getCustCPRNo());
				// Income And Expense
				if (incomeList == null || isCustomerChanged) {
					incomeList = getCustomerIncomeDetails(custid, null);
				}
				createIncomeGroupList(incomeList, custAgreementData.getFormatter());
				if (financeMains == null || isCustomerChanged) {
					// Finance
					financeMains = getFinanceDetailsByCustomer(custid);
				}
				doFillExistingFinanceList(financeMains);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	private void doFillExistingFinanceList(List<FinanceSummary> financeMains) {
		this.listBoxFinances.getItems().clear();
		if (financeMains != null && financeMains.size() > 0) {
			for (FinanceSummary financeMain : financeMains) {
				Listitem item = new Listitem();
				Listcell cell;
				cell = new Listcell(formatdDate(financeMain.getFinStartDate()));
				cell.setParent(item);
				cell = new Listcell(financeMain.getFinType());
				cell.setParent(item);
				cell = new Listcell(financeMain.getFinReference());
				cell.setParent(item);
				cell = new Listcell(formatdAmount(financeMain.getTotalOriginal()));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				/*
				 * int installmentMnts =
				 * DateUtility.getMonthsBetween(financeMain.getFinStartDate(),financeMain.getMaturityDate(), true); cell
				 * = new Listcell(formatdAmount(financeMain.getTotalRepayAmt().divide(new BigDecimal(installmentMnts),
				 * RoundingMode.HALF_DOWN))); cell.setStyle("text-align:right;"); cell.setParent(item);
				 */ cell = new Listcell(formatdAmount(financeMain.getTotalOutStanding()));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(financeMain.getFinStatus());
				cell.setParent(item);
				cell = new Listcell(String.valueOf(financeMain.getOverDueInstlments()));
				cell.setParent(item);
				this.listBoxFinances.appendChild(item);
				cell = new Listcell(
						formatdAmount(financeMain.getOverDuePrincipal().add(financeMain.getOverDueProfit())));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
			}
		}
	}

	private void createIncomeGroupList(List<CustomerIncome> incomes, int ccyFormatter) {
		BigDecimal totIncome = BigDecimal.ZERO;
		BigDecimal totExpense = BigDecimal.ZERO;
		Map<String, List<CustomerIncome>> incomeMap = new HashMap<String, List<CustomerIncome>>();
		Map<String, List<CustomerIncome>> expenseMap = new HashMap<String, List<CustomerIncome>>();
		for (CustomerIncome customerIncome : incomes) {
			if (customerIncome.getIncomeExpense().equals(PennantConstants.INCOME)) {
				totIncome = totIncome.add(customerIncome.getIncome());
				if (incomeMap.containsKey(customerIncome.getCategory())) {
					incomeMap.get(customerIncome.getCategory()).add(customerIncome);
				} else {
					ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
					list.add(customerIncome);
					incomeMap.put(customerIncome.getCategory(), list);
				}
			} else {
				totExpense = totExpense.add(customerIncome.getIncome());
				if (expenseMap.containsKey(customerIncome.getCategory())) {
					expenseMap.get(customerIncome.getCategory()).add(customerIncome);
				} else {
					ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
					list.add(customerIncome);
					expenseMap.put(customerIncome.getCategory(), list);
				}
			}
		}
		renderIncomeExpense(incomeMap, totIncome, expenseMap, totExpense, ccyFormatter);
	}

	private void renderIncomeExpense(Map<String, List<CustomerIncome>> incomeMap, BigDecimal totIncome,
			Map<String, List<CustomerIncome>> expenseMap, BigDecimal totExpense, int ccyFormatter) {
		this.listBoxCustomerIncome.getItems().clear();
		Listitem item;
		Listcell cell;
		Listgroup group;
		if (incomeMap != null) {
			for (String category : incomeMap.keySet()) {
				List<CustomerIncome> list = incomeMap.get(category);
				if (list != null && list.size() > 0) {
					group = new Listgroup();
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getCategoryDesc());
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getIncomeTypeDesc());
						cell.setParent(item);
						total = total.add(customerIncome.getIncome());
						cell = new Listcell(CurrencyUtil.format(customerIncome.getIncome(),
								CurrencyUtil.getFormat(customerIncome.getToCcy())));
						cell.setStyle("text-align:right;");
						cell.setParent(item);
						item.setAttribute("data", customerIncome);
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					cell = new Listcell("Total");
					cell.setStyle("font-weight:bold;cursor:default");
					cell.setParent(item);
					cell = new Listcell(CurrencyUtil.format(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-weight:bold; text-align:right;cursor:default");
					cell.setParent(item);
					this.listBoxCustomerIncome.appendChild(item);
				}
			}
			item = new Listitem();
			cell = new Listcell("Gross Income");
			cell.setStyle("font-weight:bold;cursor:default");
			cell.setParent(item);
			cell = new Listcell(CurrencyUtil.format(totIncome, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-weight:bold; text-align:right;cursor:default");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		if (expenseMap != null) {
			for (String category : expenseMap.keySet()) {
				List<CustomerIncome> list = expenseMap.get(category);
				if (list != null) {
					group = new Listgroup();
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getCategoryDesc());
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getIncomeTypeDesc());
						cell.setParent(item);
						total = total.add(customerIncome.getIncome());
						cell = new Listcell(CurrencyUtil.format(customerIncome.getIncome(),
								CurrencyUtil.getFormat(customerIncome.getToCcy())));
						cell.setStyle("text-align:right;");
						cell.setParent(item);
						item.setAttribute("data", customerIncome);
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					cell = new Listcell("Total");
					cell.setStyle("font-weight:bold;cursor:default");
					cell.setParent(item);
					cell = new Listcell(CurrencyUtil.format(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-weight:bold; text-align:right;cursor:default");
					cell.setParent(item);
					cell = new Listcell();
					cell.setSpan(2);
					cell.setParent(item);
					this.listBoxCustomerIncome.appendChild(item);
				}
			}
			item = new Listitem();
			cell = new Listcell("Gross Expense");
			cell.setStyle("font-weight:bold;cursor:default");
			cell.setParent(item);
			cell = new Listcell(CurrencyUtil.format(totExpense, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-weight:bold; text-align:right;cursor:default");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		item = new Listitem();
		cell = new Listcell("Net Income");
		cell.setStyle("font-weight:bold;");
		cell.setParent(item);
		cell = new Listcell(CurrencyUtil.format(totIncome.subtract(totExpense), ccyFormatter));
		cell.setSpan(2);
		cell.setStyle("font-weight:bold; text-align:right;");
		cell.setParent(item);
		this.listBoxCustomerIncome.appendChild(item);
	}

	private AgreementDetail getCustomerAggrementData(long custID, String custCIF) {
		// AgreementDetail Data
		JdbcSearchObject<AgreementDetail> jdbcSearchObject = new JdbcSearchObject<AgreementDetail>(
				AgreementDetail.class);
		jdbcSearchObject.addTabelName("CustomerAgreementDetail_View");
		if (StringUtils.isNotBlank(custCIF)) {
			jdbcSearchObject.addFilterEqual("CustCIF", custCIF);
		} else {
			jdbcSearchObject.addFilterEqual("CustID", custID);
		}
		List<AgreementDetail> agreementDetails = getPagedListService().getBySearchObject(jdbcSearchObject);
		if (agreementDetails != null && agreementDetails.size() > 0) {
			return agreementDetails.get(0);
		}
		return null;
	}

	private List<CustomerIncome> getCustomerIncomeDetails(long custID, String incomeExpense) {
		// Customer Income and Expense Data
		JdbcSearchObject<CustomerIncome> jdbcSearchObject = new JdbcSearchObject<CustomerIncome>(CustomerIncome.class);
		jdbcSearchObject.addTabelName("CustomerIncomes_AView");
		jdbcSearchObject.addFilterEqual("CustID", custID);
		if (StringUtils.isNotBlank(incomeExpense)) {
			jdbcSearchObject.addFilterEqual("IncomeExpense", incomeExpense);
		}
		return getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	private List<CustomerEmploymentDetail> getCustomerEmpDetails(long custID) {
		// Customer Income and Expense Data
		JdbcSearchObject<CustomerEmploymentDetail> jdbcSearchObject = new JdbcSearchObject<CustomerEmploymentDetail>(
				CustomerEmploymentDetail.class);
		jdbcSearchObject.addTabelName("CustomerEmpDetails_AView");
		jdbcSearchObject.addFilterEqual("CustID", custID);
		return getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	private String[] getEmploymentDetails(List<CustomerEmploymentDetail> customerEmploymentDetails2) {
		String employer[] = new String[] { "", "", "", "" };
		Date fromDate = null;
		Date toDate = null;
		Date previousDate = null;
		boolean isCurrentEmp = false;
		for (CustomerEmploymentDetail customerEmploymentDetail : customerEmploymentDetails) {
			if (customerEmploymentDetail.isCurrentEmployer()) {
				isCurrentEmp = true;
				employer[0] = customerEmploymentDetail.getLovDesccustEmpName();
				toDate = SysParamUtil.getAppDate();
			}
			if (fromDate == null) {
				fromDate = customerEmploymentDetail.getCustEmpFrom();
			} else {
				if (customerEmploymentDetail.getCustEmpFrom() != null
						&& customerEmploymentDetail.getCustEmpFrom().compareTo(fromDate) < 0) {
					fromDate = customerEmploymentDetail.getCustEmpFrom();
				}
			}
			if (!isCurrentEmp) {
				if (toDate == null) {
					toDate = customerEmploymentDetail.getCustEmpTo();
					employer[0] = customerEmploymentDetail.getLovDesccustEmpName();
				} else {
					if (customerEmploymentDetail.getCustEmpTo() != null
							&& customerEmploymentDetail.getCustEmpTo().compareTo(toDate) > 0) {
						toDate = customerEmploymentDetail.getCustEmpTo();
						employer[0] = customerEmploymentDetail.getLovDesccustEmpName();
					}
				}
			}
		}
		if (customerEmploymentDetails.size() == 1) {
			employer[2] = employer[0];
		} else {
			for (CustomerEmploymentDetail customerEmpDetail : customerEmploymentDetails) {
				if (previousDate == null) {
					if (customerEmpDetail.getCustEmpTo().compareTo(toDate) != 0) {
						previousDate = customerEmpDetail.getCustEmpTo();
						employer[2] = customerEmpDetail.getLovDesccustEmpName();
					}
				} else {
					if (customerEmpDetail.getCustEmpTo() != null
							&& customerEmpDetail.getCustEmpTo().compareTo(previousDate) > 0) {
						previousDate = customerEmpDetail.getCustEmpTo();
						employer[2] = customerEmpDetail.getLovDesccustEmpName();
					}
				}
			}
		}
		employer[1] = fromDate == null || toDate == null ? ""
				: String.valueOf(DateUtil.getYearsBetween(fromDate, toDate));
		employer[3] = fromDate == null || previousDate == null ? ""
				: String.valueOf(DateUtil.getYearsBetween(fromDate, previousDate));
		return employer;
	}

	private List<FinanceSummary> getFinanceDetailsByCustomer(long custID) {
		// Customer FinanceDetails
		JdbcSearchObject<FinanceSummary> jdbcSearchObject = new JdbcSearchObject<FinanceSummary>(FinanceSummary.class);
		jdbcSearchObject.addTabelName("CustFinanceExposure_View");
		jdbcSearchObject.addFilterEqual("CustID", custID);
		return getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	private String formatdDate(Date date) {
		return DateUtil.formatToLongDate(date);
	}

	private String formatdAmount(BigDecimal amount) {
		return PennantApplicationUtil.amountFormate(amount, finFormatter);
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
}