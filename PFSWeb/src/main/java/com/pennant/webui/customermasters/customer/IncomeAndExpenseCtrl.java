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
 * * FileName : CustomerDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * 09-05-2018 Vinay 0.2 Extended Details tab changes for * Customer Enquiry menu based on
 * rights * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file.
 */
/**
 * @author rajesh.n
 *
 */
public class IncomeAndExpenseCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final Logger logger = LogManager.getLogger(IncomeAndExpenseCtrl.class);
	private static final long serialVersionUID = -1289772081447044673L;
	private static final String FONT_WEIGHT_BOLD = "font-weight:bold;";
	private static final String DEFAULT_CURSOR = "cursor:default";
	private static final String TEXT_ALIGNMENT_RIGHT = "text-align:right;";
	public static final String CUSTOMER = "Customer";
	public static final String CUST_GRC_EXP_CELL = "CUSTGRCEXPCELL";
	public static final String CUST_GRC_INCOME_CELL = "CUSTGRCINCOMECELL";
	public static final String CUST_NET_INCOME_CELL = "CUSTNETINCOMECELL";

	public IncomeAndExpenseCtrl() {
		super();
	}

	public void doRenderIncomeList(List<CustomerIncome> customerIncomes, Listbox listbox, int ccyFormatter,
			boolean isEnquiry) {

		Map<String, List<CustomerIncome>> mapData = prepareGroup(customerIncomes);
		// render
		renderData(listbox, ccyFormatter, mapData, isEnquiry);
		// Gross Income
		appendTotalItem(listbox, ccyFormatter, BigDecimal.ZERO, "Gross Income", CUST_GRC_INCOME_CELL);
		// Gross Expense
		appendTotalItem(listbox, ccyFormatter, BigDecimal.ZERO, "Gross Expense", CUST_GRC_EXP_CELL);
		// Net Income
		appendTotalItem(listbox, ccyFormatter, BigDecimal.ZERO, "Net Income", CUST_NET_INCOME_CELL);

		calculateTotal(listbox, ccyFormatter);
	}

	/**
	 * Method to prepare income and expense map by using the income list
	 * 
	 * @param incomes
	 * @return Map<String, List<CustomerIncome>>
	 */
	private Map<String, List<CustomerIncome>> prepareGroup(List<CustomerIncome> incomes) {

		Map<String, List<CustomerIncome>> mapData = new TreeMap<String, List<CustomerIncome>>();

		if (ImplementationConstants.POPULATE_DFT_INCOME_DETAILS) {
			mapData = new HashMap<String, List<CustomerIncome>>();
		}
		if (incomes != null && !incomes.isEmpty()) {

			for (CustomerIncome customerIncome : incomes) {
				String incomeExpense = StringUtils.trimToEmpty(customerIncome.getIncomeExpense());
				String category = StringUtils.trimToEmpty(customerIncome.getCategory());
				String key = incomeExpense + "-" + category;

				if (mapData.containsKey(key)) {
					mapData.get(key).add(customerIncome);
				} else {
					ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
					list.add(customerIncome);
					mapData.put(key, list);
				}

			}
		}
		return mapData;

	}

	/**
	 * Method will render the income and expense data map
	 * 
	 * @param listbox
	 * @param ccyFormatter
	 * @param mapData
	 */
	private void renderData(Listbox listbox, int ccyFormatter, Map<String, List<CustomerIncome>> mapData,
			boolean isEnquiry) {
		// render start
		listbox.getItems().clear();

		Set<Entry<String, List<CustomerIncome>>> entrySet = mapData.entrySet();

		for (Entry<String, List<CustomerIncome>> entry : entrySet) {
			String key = entry.getKey();
			List<CustomerIncome> data = entry.getValue();

			// add Group
			addlistGroup(data.get(0), listbox);

			for (CustomerIncome customerIncome2 : data) {
				doFillIncomeAndExpense(customerIncome2, listbox, ccyFormatter, false, isEnquiry);
			}
			// add sub totals
			appendTotalItem(listbox, ccyFormatter, BigDecimal.ZERO, "Total", key);
		}
	}

	/**
	 * Method will prepare list group according to the income type
	 * 
	 * @param customerIncome
	 * @param listbox
	 */
	private void addlistGroup(CustomerIncome customerIncome, Listbox listbox) {
		Listgroup group = new Listgroup();
		String value = customerIncome.getIncomeExpense() + "-" + customerIncome.getCategoryDesc();
		Listcell cell = new Listcell(value);
		cell.setParent(group);
		listbox.appendChild(group);

	}

	/**
	 * This method will prepare the list components for income and expense list render
	 * 
	 * @param custinc
	 * @param listbox
	 * @param ccyFormatter
	 */
	public void doFillIncomeAndExpense(CustomerIncome custinc, Listbox listbox, int ccyFormatter, boolean isNewRecord,
			boolean isEnquiry) {
		logger.debug(Literal.ENTERING);
		Space space = null;
		Hbox hbox = null;

		Listitem item = new Listitem();
		Listcell cellIncomeExpense = new Listcell();
		Listcell cellIncomeType = new Listcell();
		Listcell cellmargin = new Listcell();
		Listcell cellIncome = new Listcell();
		Listcell cellcalcualteAmount = new Listcell();
		cellcalcualteAmount.setStyle(TEXT_ALIGNMENT_RIGHT);
		Listcell cellRecordType = new Listcell();
		Listcell cellDelete = new Listcell();

		// ************** income or expense

		cellIncomeExpense.setLabel(StringUtils.trimToEmpty(custinc.getIncomeExpense()));

		// **************** income Type
		ExtendedCombobox custIncomeType = getIncomeType(custinc);
		Object[] inccomeExpData = new Object[2];
		inccomeExpData[0] = cellIncomeExpense;
		inccomeExpData[1] = cellmargin;
		custIncomeType.addForward("onFulfill", self, "onFulfillCustIncomeType", inccomeExpData);
		custIncomeType.setReadonly(getUserWorkspace().isReadOnly("CustomerDialog_custIncomeType"));
		if (!custinc.isNewRecord()) {
			custIncomeType.setReadonly(true);
		}
		cellIncomeType.appendChild(custIncomeType);
		// ********************* margin
		Decimalbox margin = new Decimalbox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		// space.setSclass("mandatory");
		margin.setMaxlength(6);
		margin.setValue(PennantApplicationUtil.formateAmount(custinc.getMargin(), ccyFormatter));
		/*
		 * margin.setConstraint( new PTDecimalValidator(Labels.getLabel("listheader_Margin.label"), ccyFormatter, true,
		 * false));
		 */
		margin.setReadonly(getUserWorkspace().isReadOnly("CustomerDialog_custIncomeType"));
		Object[] margindata = new Object[2];
		margindata[0] = cellcalcualteAmount;
		margindata[1] = cellIncome;
		margin.addForward("onChange", self, "onChangeMargin", margindata);
		hbox.appendChild(space);
		hbox.appendChild(margin);
		cellmargin.appendChild(hbox);

		// ********************************** income

		CurrencyBox incomeAmount = new CurrencyBox();
		incomeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		incomeAmount.setScale(ccyFormatter);
		incomeAmount.setMandatory(true);
		incomeAmount.setValue(PennantApplicationUtil.formateAmount(custinc.getIncome(), ccyFormatter));
		incomeAmount.setReadonly(isEnquiry || getUserWorkspace().isReadOnly("CustomerDialog_custIncomeType"));
		/*
		 * incomeAmount.setConstraint( new PTDecimalValidator(Labels.getLabel("listheader_CustIncome.label"),
		 * ccyFormatter, true, false));
		 */
		Object[] incomedata = new Object[2];
		incomedata[0] = cellcalcualteAmount;
		incomedata[1] = cellmargin;
		incomeAmount.addForward("onFulfill", self, "onFulfillIncomeAmount", incomedata);
		cellIncome.appendChild(incomeAmount);

		// *************** Calculated amount

		cellcalcualteAmount.setLabel(PennantApplicationUtil.amountFormate(custinc.getCalculatedAmount(), ccyFormatter));

		// ******************
		cellRecordType.setLabel(PennantJavaUtil.getLabel(custinc.getRecordType()));

		// Delete action
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel(Labels.getLabel("btnDelete.label"));
		button.setDisabled(isEnquiry || getUserWorkspace().isReadOnly("CustomerDialog_custIncomeType"));
		button.addForward("onClick", self, "onClickFinancialButtonDelete", item);
		hbox.appendChild(space);
		hbox.appendChild(button);
		cellDelete.appendChild(hbox);

		// set parent
		cellIncomeExpense.setParent(item);
		cellIncomeType.setParent(item);
		cellmargin.setParent(item);
		cellIncome.setParent(item);
		cellcalcualteAmount.setParent(item);
		cellIncomeExpense.setParent(item);
		cellRecordType.setParent(item);
		cellDelete.setParent(item);

		item.setAttribute("data", custinc);
		if (isNewRecord) {
			if (listbox.getItemCount() > 0) {
				Listitem listitem = listbox.getItems().get(0);
				listbox.insertBefore(item, listitem);
			} else {
				listbox.appendChild(item);
			}
		} else {
			listbox.appendChild(item);
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(custinc.getRecordType())) {
			doReadOnly(item);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will prepare the customer income and expense list for data saving
	 * 
	 * @param aCustomerDetails
	 * @param listBoxCustomerIncome
	 * @param ccyFormatter
	 * @param isEnquiry
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> prepareCustomerIncomeExpenseData(CustomerDetails aCustomerDetails,
			Listbox listBoxCustomerIncome, int ccyFormatter, boolean isEnquiry) {
		logger.debug(Literal.ENTERING);
		List<Listitem> listItems = listBoxCustomerIncome.getItems();
		List<CustomerIncome> customerIncomes = new ArrayList<>();
		Customer customer = aCustomerDetails.getCustomer();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		Map<String, List> incomeData = new HashMap<>();
		if (!CollectionUtils.isEmpty(listItems)) {

			for (int i = 0; i < listItems.size(); i++) {
				Listitem listItem = listItems.get(i);
				CustomerIncome aCustomerIncome = (CustomerIncome) listItem.getAttribute("data");
				if (aCustomerIncome != null) {
					aCustomerIncome.setInputSource(CUSTOMER);
					aCustomerIncome.setCustShrtName(customer.getCustShrtName());
					aCustomerIncome.setCustId(customer.getCustID());
					aCustomerIncome.setCustCif(customer.getCustCIF());

					List<Component> listCells = listItem.getChildren();
					try {
						// Getting Income Type
						ExtendedCombobox extIncomeType = (ExtendedCombobox) listCells.get(1).getChildren().get(0);
						if (aCustomerIncome.isNewRecord() && StringUtils.isEmpty(extIncomeType.getValidatedValue())) {
							throw new WrongValueException(extIncomeType.getTextbox(),
									Labels.getLabel("listheader_IncomeHead.label"));
						}
						doSetIncomeTypeDetails(aCustomerIncome, extIncomeType);
					} catch (WrongValueException we) {
						wve.add(we);
					}

					try {
						// Getting Margin
						Decimalbox margin = (Decimalbox) listCells.get(2).getChildren().get(0).getLastChild();
						if (margin != null) {
							/*
							 * if (!margin.isReadonly() && (margin.getValue() == null)) { throw new
							 * WrongValueException(margin, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[]
							 * { Labels.getLabel("listheader_Margin.label") })); }
							 */
							aCustomerIncome
									.setMargin(PennantApplicationUtil.unFormateAmount(margin.getValue(), ccyFormatter));
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}

					try {
						// Getting Income
						CurrencyBox income = (CurrencyBox) listCells.get(3).getChildren().get(0);
						if (income != null) {
							if (!income.isReadonly() && (income.getActualValue() == null)) {
								throw new WrongValueException(income.getCcyTextBox(),
										Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
												new String[] { Labels.getLabel("listheader_CustIncome.label") }));
							}
							aCustomerIncome.setIncome(
									PennantApplicationUtil.unFormateAmount(income.getActualValue(), ccyFormatter));
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}

					CustomerIncome oldData = isINexsistingList(aCustomerIncome,
							aCustomerDetails.getCustomerIncomeList());
					if (oldData == null) {
						aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
						aCustomerIncome.setRecordType(PennantConstants.RCD_ADD);
						aCustomerIncome.setId(0);
						aCustomerIncome.setLinkId(0);
					} else {
						isRecordUpdated(aCustomerIncome, aCustomerDetails.getCustomerIncomeList());
					}
					customerIncomes.add(aCustomerIncome);
				}
			}
			incomeData.put("errorList", wve);
			incomeData.put("customerIncomes", customerIncomes);
		}
		logger.debug(Literal.LEAVING);
		return incomeData;
	}

	/**
	 * This method will calculate the group total's, gross income, gross expense and net income
	 * 
	 * @param listbox
	 * @param ccyFormatter
	 */
	public Map<String, Object> calculateTotal(Listbox listbox, int ccyFormatter) {

		List<Listitem> listItems = listbox.getItems();

		BigDecimal totIncome = BigDecimal.ZERO;
		BigDecimal totExpense = BigDecimal.ZERO;
		BigDecimal netIncome = BigDecimal.ZERO;
		Map<String, BigDecimal> mapTotal = new HashMap<>();
		Map<String, Object> returnMap = new HashMap<>();

		for (Listitem listIt : listItems) {

			try {
				if (listIt instanceof Listgroup || listIt.getAttribute("Total") != null) {
					continue;
				}

				List<Component> listCells = listIt.getChildren();
				Listcell incExp = (Listcell) listCells.get(0);

				// getting income or expense from first cell
				String incomeExp = incExp.getLabel();

				// income type
				ExtendedCombobox extincomeType = (ExtendedCombobox) listCells.get(1).getChildren().get(0);
				IncomeType incomeType = (IncomeType) extincomeType.getObject();
				// Getting Margin
				Decimalbox margin = (Decimalbox) listCells.get(2).getChildren().get(0).getLastChild();
				// Getting Income
				CurrencyBox income = (CurrencyBox) listCells.get(3).getChildren().get(0);

				BigDecimal actAmount = getCalculatedAmount(margin.getValue(), income.getActualValue());

				String recordType = ((CustomerIncome) listIt.getAttribute("data")).getRecordType();

				// for gross income and expense total's
				if (!(PennantConstants.RECORD_TYPE_CAN.equals(recordType)
						|| PennantConstants.RECORD_TYPE_DEL.equals(recordType))) {
					if (PennantConstants.INCOME.equals(incomeExp)) {
						totIncome = totIncome.add(actAmount);
					} else if (PennantConstants.EXPENSE.equals(incomeExp)) {
						totExpense = totExpense.add(actAmount);
					}
				}

				// Preparing group total map
				if (incomeType != null) {
					String groupKey = incomeType.getIncomeExpense() + "-" + incomeType.getCategory();
					if (mapTotal.containsKey(groupKey)) {
						BigDecimal mapValue = mapTotal.get(groupKey);
						mapValue = mapValue.add(actAmount);
						mapTotal.put(groupKey, mapValue);
					} else {
						mapTotal.put(groupKey, actAmount);
					}
				}
			} catch (WrongValueException e) {
				logger.debug(e);
			}
		}

		// sub group totals
		for (String groupkey : mapTotal.keySet()) {
			// Setting the group total values
			Listcell gropTotalCell = (Listcell) listbox.getFellowIfAny(groupkey);
			if (gropTotalCell != null) {
				gropTotalCell.setLabel(PennantApplicationUtil.formatAmount(mapTotal.get(groupkey), ccyFormatter));
			}
		}

		// Gross Income
		Listcell grsIncCell = (Listcell) listbox.getFellowIfAny(CUST_GRC_INCOME_CELL);
		grsIncCell.setLabel(PennantApplicationUtil.formatAmount(totIncome, ccyFormatter));

		// Gross Expense
		Listcell grsExpCell = (Listcell) listbox.getFellowIfAny(CUST_GRC_EXP_CELL);
		grsExpCell.setLabel(PennantApplicationUtil.formatAmount(totExpense, ccyFormatter));

		// Net Income
		Listcell netIncCell = (Listcell) listbox.getFellowIfAny(CUST_NET_INCOME_CELL);
		netIncome = totIncome.subtract(totExpense);
		netIncCell.setLabel(PennantApplicationUtil.formatAmount(netIncome, ccyFormatter));
		returnMap.put("NET_ANNUAL",
				PennantApplicationUtil.unFormateAmount(netIncome.multiply(new BigDecimal(12)), ccyFormatter));

		return returnMap;

	}

	/**
	 * This method will append the summary totals for the group
	 * 
	 * @param listbox
	 * @param ccyFormatter
	 * @param amount
	 * @param cellText
	 * @param key
	 */
	public void appendTotalItem(Listbox listbox, int ccyFormatter, BigDecimal amount, String cellText, String key) {
		Listitem item = new Listitem();
		item.setAttribute("Total", true);
		Listcell cell = new Listcell(cellText);
		cell.setStyle(FONT_WEIGHT_BOLD);
		cell.setParent(item);
		cell = new Listcell(PennantApplicationUtil.amountFormate(amount, ccyFormatter));
		cell.setId(key);
		cell.setSpan(4);
		cell.setStyle(FONT_WEIGHT_BOLD + "" + TEXT_ALIGNMENT_RIGHT);
		cell.setParent(item);
		cell = new Listcell();
		cell.setSpan(4);
		cell.setStyle(DEFAULT_CURSOR);
		cell.setParent(item);
		listbox.appendChild(item);
	}

	/**
	 * Method will return the caluculated amount
	 * 
	 * @param margin
	 * @param income
	 * @return
	 */
	public BigDecimal getCalculatedAmount(BigDecimal margin, BigDecimal income) {
		if (margin == null || income == null) {
			return BigDecimal.ZERO;
		} else {
			return income.multiply(margin).divide(new BigDecimal(100), RoundingMode.HALF_UP);
		}
	}

	/**
	 * This method will perform the delete operation
	 * 
	 * @param listbox
	 * @param listitem
	 */
	public void doDelete(Listbox listbox, Listitem listitem, int ccyFormatter, boolean isFinanceProcess) {

		if (listitem != null && listitem.getAttribute("data") != null) {
			CustomerIncome customerIncome = (CustomerIncome) listitem.getAttribute("data");
			Listcell cellRecordType = (Listcell) listitem.getChildren().get(5);

			String incomeExpense = customerIncome.getIncomeExpense();
			String category = customerIncome.getCategory();
			String incomeType = customerIncome.getIncomeType();
			String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
			if (StringUtils.isNotEmpty(incomeExpense) && StringUtils.isNotEmpty(category)
					&& StringUtils.isNotEmpty(incomeType)) {
				msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
						+ Labels.getLabel("label_IncomeTypeDialog_IncomeExpense.value") + " : " + incomeExpense + ","
						+ Labels.getLabel("label_IncomeTypeDialog_Category.value") + " : " + category + ","
						+ Labels.getLabel("label_IncomeTypeDialog_IncomeTypeCode.value") + " : " + incomeType;
			}
			// Show a confirm box
			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doReadOnly(listitem);
				if (customerIncome.isNewRecord()) {
					listbox.removeChild(listitem);
					calculateTotal(listbox, ccyFormatter);
					return;
				} else if (StringUtils.isBlank(customerIncome.getRecordType())) {
					customerIncome.setVersion(customerIncome.getVersion() + 1);
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					if (!isFinanceProcess) {
						customerIncome.setNewRecord(true);
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equals(customerIncome.getRecordType())) {
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RECORD_TYPE_NEW.equals(customerIncome.getRecordType())) {
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}

				cellRecordType.setLabel(PennantJavaUtil.getLabel(customerIncome.getRecordType()));
			}
		}

	}

	/**
	 * Setting the components to read only
	 * 
	 * @param listitem
	 */
	private void doReadOnly(Listitem listitem) {
		List<Component> listCells = listitem.getChildren();
		Decimalbox margin = (Decimalbox) listCells.get(2).getChildren().get(0).getLastChild();
		CurrencyBox income = (CurrencyBox) listCells.get(3).getChildren().get(0);
		Button delete = (Button) listCells.get(6).getChildren().get(0).getLastChild();
		margin.setReadonly(true);
		income.setReadonly(true);
		delete.setDisabled(true);
	}

	/**
	 * Method will set the income type default values to CustomerIncome bean
	 * 
	 * @param customerIncome
	 * @param extIncomeType
	 */
	private void doSetIncomeTypeDetails(CustomerIncome customerIncome, ExtendedCombobox extIncomeType) {

		// Getting Income Type
		if (extIncomeType != null) {
			if (extIncomeType.getObject() != null && customerIncome.isNewRecord()) {
				IncomeType incomeType = (IncomeType) extIncomeType.getObject();
				customerIncome.setIncomeExpense(incomeType.getIncomeExpense());
				customerIncome.setIncomeTypeDesc(incomeType.getIncomeTypeDesc());
				customerIncome.setCategory(incomeType.getCategory());
				customerIncome.setCategoryDesc(incomeType.getLovDescCategoryName());
				customerIncome.setIncomeType(incomeType.getId());
				if (incomeType.getMargin() != null) {
					customerIncome.setMarginDeviation(incomeType.getMargin().equals(customerIncome.getMargin()));
				}
			}
		}
	}

	/**
	 * Method will check if the newly added record is already available in the list
	 * 
	 * @param aCustInc
	 * @param custIncList
	 * @return
	 */
	private CustomerIncome isINexsistingList(CustomerIncome aCustInc, List<CustomerIncome> custIncList) {

		for (CustomerIncome customerIncome : custIncList) {
			if (StringUtils.equals(aCustInc.getIncomeExpense(), customerIncome.getIncomeExpense())
					&& StringUtils.equals(aCustInc.getCategory(), customerIncome.getCategory())
					&& StringUtils.equals(aCustInc.getIncomeType(), customerIncome.getIncomeType())
					&& (aCustInc.getCustId() == customerIncome.getCustId())) {
				return customerIncome;
			}
		}
		return null;

	}

	/**
	 * Method will check if the newly added record is already available in the list
	 * 
	 * @param aCustomerIncome
	 * @param custIncList
	 * @return
	 */
	private CustomerIncome isRecordUpdated(CustomerIncome aCustomerIncome, List<CustomerIncome> custIncList) {

		for (CustomerIncome customerIncome : custIncList) {
			if (PennantConstants.RECORD_TYPE_DEL.equals(customerIncome.getRecordType())
					|| PennantConstants.RECORD_TYPE_CAN.equals(customerIncome.getRecordType())
					|| aCustomerIncome.isNewRecord()) {
				continue;
			}
			if (StringUtils.equals(aCustomerIncome.getIncomeExpense(), customerIncome.getIncomeExpense())
					&& StringUtils.equals(aCustomerIncome.getCategory(), customerIncome.getCategory())
					&& StringUtils.equals(aCustomerIncome.getIncomeType(), customerIncome.getIncomeType())
					&& (aCustomerIncome.getCustId() == customerIncome.getCustId())) {

				// checking data is updated or not
				if (!aCustomerIncome.getMargin().equals(customerIncome.getMargin())
						|| !aCustomerIncome.getIncome().equals(customerIncome.getIncome())) {
					aCustomerIncome.setNewRecord(false);
					aCustomerIncome.setId(customerIncome.getId());
					aCustomerIncome.setLinkId(customerIncome.getLinkId());
					if (StringUtils.isBlank(aCustomerIncome.getRecordType())) {
						aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
						aCustomerIncome.setRecordType(PennantConstants.RCD_UPD);
					}
					return aCustomerIncome;
				}
			}
		}
		return null;

	}

	/**
	 * Creating extended combo box component
	 * 
	 * @param custinc
	 * @return
	 */
	private ExtendedCombobox getIncomeType(CustomerIncome custinc) {
		ExtendedCombobox custIncomeType = new ExtendedCombobox();
		custIncomeType.setMaxlength(20);
		custIncomeType.getTextbox().setMaxlength(50);
		custIncomeType.setMandatoryStyle(true);
		custIncomeType.setTextBoxWidth(150);
		custIncomeType.setModuleName("IncomeExpense");
		custIncomeType.setValueColumn("IncomeTypeCode");
		custIncomeType.setDescColumn("IncomeTypeDesc");
		custIncomeType.setValidateColumns(new String[] { "IncomeTypeCode" });
		custIncomeType.setValue(custinc.getIncomeType(), custinc.getIncomeTypeDesc());
		// Object data preparing
		IncomeType incomeType = new IncomeType();
		incomeType.setCategory(custinc.getCategory());
		incomeType.setIncomeExpense(custinc.getIncomeExpense());
		custIncomeType.setObject(incomeType);
		return custIncomeType;
	}
}
