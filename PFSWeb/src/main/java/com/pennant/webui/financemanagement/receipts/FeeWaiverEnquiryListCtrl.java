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
 * * FileName : FinanceSelectCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.receipts;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.finance.financemain.model.FinanceMainSelectItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceSelect.zul file.
 */
public class FeeWaiverEnquiryListCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = -5081318673331825306L;
	private static final Logger logger = LogManager.getLogger(FeeWaiverEnquiryListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FeeWaiverEnquiry; // autowired
	protected Borderlayout borderlayout_FinanceSelect; // autowired
	protected Textbox custCIF; // autowired
	protected Textbox finReference; // autowired
	protected Textbox finType; // autowired
	protected Textbox finCcy; // autowired
	protected Textbox finBranch; // autowired
	protected Textbox scheduleMethod; // autowired
	protected Textbox profitDaysBasis; // autowired
	private Paging pagingFinanceList; // autowired
	private Listbox listBoxFinance; // autowired
	protected Button btnClose; // autowired
	protected Grid grid_FinanceDetails; // autowired
	protected Div div_ToolBar; // autowired

	protected Listbox sortOperator_custCIF; // autowired
	protected Listbox sortOperator_finReference; // autowired
	protected Listbox sortOperator_finType; // autowired
	protected Listbox sortOperator_finCcy; // autowired
	protected Listbox sortOperator_finBranch; // autowired
	protected Listbox sortOperator_scheduleMethod; // autowired
	protected Listbox sortOperator_profitDaysBasis; // autowired

	protected int oldVar_sortOperator_custCIF; // autowired
	protected int oldVar_sortOperator_finReference; // autowired
	protected int oldVar_sortOperator_finType; // autowired
	protected int oldVar_sortOperator_finCcy; // autowired
	protected int oldVar_sortOperator_finBranch; // autowired
	protected int oldVar_sortOperator_scheduleMethod; // autowired
	protected int oldVar_sortOperator_profitDaysBasis;// autowired

	// List headers
	protected Listheader listheader_FinType; // autoWired
	protected Listheader listheader_FinProduct; // autoWired
	protected Listheader listheader_CustCIF; // autoWired
	protected Listheader listheader_FinRef; // autoWired
	protected Listheader listheader_FinBranch; // autoWired
	protected Listheader listheader_FinStartDate; // autoWired
	protected Listheader listheader_NumberOfTerms; // autoWired
	protected Listheader listheader_MaturityDate; // autoWired
	protected Listheader listheader_FinCcy; // autoWired
	protected Listheader listheader_FinAmount; // autoWired
	protected Listheader listheader_CurFinAmount; // autoWired
	protected Listheader listheader_RequestStage; // autoWired
	protected Listheader listheader_RecordStatus; // autoWired
	private String moduleDefiner = "";
	private String eventCodeRef = "";
	private String menuItemRightName = null;
	private Tab tab;
	private Tabbox tabbox;

	// not auto wired vars
	private transient Object dialogCtrl = null;
	private transient WorkFlowDetails workFlowDetails = null;
	protected JdbcSearchObject<FinanceMain> searchObject;
	private List<Filter> filterList;
	protected Button btnClear;
	protected Button btnNew;
	private transient FinanceDetailService financeDetailService;
	private transient FeeWaiverHeaderService feeWaiverHeaderService;
	private boolean isEnquiry = true;

	private FinanceMain financeMain;
	final Map<String, Object> map = getDefaultArguments();
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	/**
	 * Default constructor
	 */
	public FeeWaiverEnquiryListCtrl() {
		super();
	}

	// Component Events

	/**
	 * Before binding the data and calling the Search window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FeeWaiverEnquiry(Event event) {
		logger.debug("Entering" + event.toString());

		if (event.getTarget() != null && event.getTarget().getParent() != null
				&& event.getTarget().getParent().getParent() != null
				&& event.getTarget().getParent().getParent().getParent() != null
				&& event.getTarget().getParent().getParent().getParent().getParent() != null) {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();

			String menuItemName = tabbox.getSelectedTab().getId();
			menuItemName = menuItemName.trim().replace("tab_", "menu_Item_");
			if (getUserWorkspace().getHasMenuRights().containsKey(menuItemName)) {
				menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemName);
			}

			/* set components visible dependent on the users rights */
			doCheckRights();
			map.put("enqiryModule", super.enqiryModule);

		}

		// Listbox Sorting

		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));

		this.listheader_FinProduct.setSortAscending(new FieldComparator("LovDescProductCodeName", true));
		this.listheader_FinProduct.setSortDescending(new FieldComparator("LovDescProductCodeName", false));

		this.listheader_CustCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));

		this.listheader_FinRef.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinRef.setSortDescending(new FieldComparator("finReference", false));

		this.listheader_FinBranch.setSortAscending(new FieldComparator("finBranch", true));
		this.listheader_FinBranch.setSortDescending(new FieldComparator("finBranch", false));

		this.listheader_FinStartDate.setSortAscending(new FieldComparator("finStartDate", true));
		this.listheader_FinStartDate.setSortDescending(new FieldComparator("finStartDate", false));

		this.listheader_NumberOfTerms.setSortAscending(new FieldComparator("numberOfTerms", true));
		this.listheader_NumberOfTerms.setSortDescending(new FieldComparator("numberOfTerms", false));

		this.listheader_MaturityDate.setSortAscending(new FieldComparator("maturityDate", true));
		this.listheader_MaturityDate.setSortDescending(new FieldComparator("maturityDate", false));

		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));

		this.listheader_FinAmount.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_FinAmount.setSortDescending(new FieldComparator("finAmount", false));

		this.listheader_CurFinAmount.setSortAscending(new FieldComparator("finRepaymentAmount", true));
		this.listheader_CurFinAmount.setSortDescending(new FieldComparator("finRepaymentAmount", false));

		this.listheader_RequestStage.setSortAscending(new FieldComparator("LovDescRequestStage", true));
		this.listheader_RequestStage.setSortDescending(new FieldComparator("LovDescRequestStage", false));

		this.listheader_RecordStatus.setSortAscending(new FieldComparator("RecordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("RecordStatus", false));

		// DropDown ListBox

		this.sortOperator_custCIF
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finReference
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finType
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finCcy
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finBranch
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_finBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_scheduleMethod
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_scheduleMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_profitDaysBasis
				.setModel(new ListModelList<SearchOperators>(new SearchOperators().getMultiStringOperators()));
		this.sortOperator_profitDaysBasis.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (arguments.containsKey("DialogCtrl")) {
			setDialogCtrl(arguments.get("DialogCtrl"));
		}
		if (arguments.containsKey("filtersList")) {
			filterList = (List<Filter>) arguments.get("filtersList");
		}

		// Stored search object and paging

		if (arguments.containsKey("searchObject")) {
			searchObject = (JdbcSearchObject<FinanceMain>) arguments.get("searchObject");
		}
		this.borderlayout_FinanceSelect.setHeight(getBorderLayoutHeight());
		this.listBoxFinance.setHeight(getListBoxHeight(this.grid_FinanceDetails.getRows().getVisibleItemCount() + 1));
		this.pagingFinanceList.setPageSize(getListRows());
		this.pagingFinanceList.setDetailed(true);

		if (searchObject != null) {
			// Render Search Object
			paging(searchObject);
			// get the filters from the searchObject
			final List<Filter> ft = searchObject.getFilters();
			for (final Filter filter : ft) {

				// restore founded properties
				if ("lovDescCustCIF".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(restoreString(filter.getValue().toString(), this.sortOperator_custCIF));
				} else if ("finReference".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finReference, filter);
					this.finReference
							.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finReference));
				} else if ("finType".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finType, filter);
					this.finType.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finType));
				} else if ("finCcy".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finCcy, filter);
					this.finCcy.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finCcy));
				} else if ("finBranch".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_finBranch, filter);
					this.finBranch.setValue(restoreString(filter.getValue().toString(), this.sortOperator_finBranch));
				} else if ("scheduleMethod".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_scheduleMethod, filter);
					this.scheduleMethod
							.setValue(restoreString(filter.getValue().toString(), this.sortOperator_scheduleMethod));
				} else if ("profitDaysBasis".equals(filter.getProperty())) {
					SearchOperators.resetOperator(this.sortOperator_profitDaysBasis, filter);
					this.profitDaysBasis
							.setValue(restoreString(filter.getValue().toString(), this.sortOperator_profitDaysBasis));
				}
			}
		}
		if (arguments.containsKey("isDashboard")) {
			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String) (arguments.get("moduleDefiner"));
			}
			Events.postEvent("onClick$btnSearch", window_FeeWaiverEnquiry, event);
			if (arguments.containsKey("detailScreen")) {
				if (arguments.containsKey("FinanceReference")) {
					this.finReference.setValue((String) (arguments.get("FinanceReference")));
				}
				Events.postEvent("onFinanceItemDoubleClicked", window_FeeWaiverEnquiry, event);
			}

		}
		if (isEnquiry) {
			this.listheader_FinProduct.setVisible(false);
			// this.listheader_CustCIF.setVisible(false);
			this.listheader_RequestStage.setVisible(false);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinanceSelectList");
		logger.debug("Leaving");
	}

	/**
	 * Method for replacing LIKE '%' operator in String of SearchObject
	 * 
	 * @param filterValue
	 * @param listbox
	 * @return
	 */
	private String restoreString(String filterValue, Listbox listbox) {
		if (listbox.getSelectedIndex() == 3) {
			return StringUtils.replaceChars(filterValue, "%", "");
		}
		return filterValue;
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug("Entering" + event.toString());
		doSearch(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * OnClick Event for Close button for Closing Window
	 * 
	 * @param eventtab
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		if (tab != null) {
			tab.close();
		}
		try {
			if (dialogCtrl != null) {
				if (dialogCtrl.getClass().getMethod("closeTab") != null) {
					dialogCtrl.getClass().getMethod("closeTab").invoke(dialogCtrl);
				}
			} else {
				this.window_FeeWaiverEnquiry.onClose();
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving" + event.toString());
	}

	// GUI operations

	/**
	 * closes the dialog window
	 * 
	 * @throws InterruptedException
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		this.window_FeeWaiverEnquiry.onClose();
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinRef(Event event) {
		logger.debug("Entering " + event.toString());

		if (this.searchObject == null) {
			doSearch(false);
		}

		Filter[] filters = this.searchObject.getFilters().toArray(new Filter[this.searchObject.getFilters().size()]);
		if (this.oldVar_sortOperator_finReference == Filter.OP_IN
				|| this.oldVar_sortOperator_finReference == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeWaiverEnquiry,
					"FinanceMaintenance", this.finReference.getValue(), filters,
					StringUtils.trimToNull(this.searchObject.getWhereClause()));
			if (selectedValues != null) {
				this.finReference.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FeeWaiverEnquiry, "FinanceMaintenance",
					this.finReference.getValue(), filters, StringUtils.trimToNull(this.searchObject.getWhereClause()));

			if (dataObject instanceof String) {
				this.finReference.setValue("");
			} else {
				FinanceMain details = (FinanceMain) dataObject;
				if (details != null) {
					this.finReference.setValue(details.getFinReference());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		if (this.oldVar_sortOperator_finBranch == Filter.OP_IN
				|| this.oldVar_sortOperator_finBranch == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeWaiverEnquiry, "Branch",
					this.finBranch.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finBranch.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FeeWaiverEnquiry, "Branch");
			if (dataObject instanceof String) {
				this.finBranch.setValue("");
			} else {
				Branch details = (Branch) dataObject;
				if (details != null) {
					this.finBranch.setValue(details.getBranchCode());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		if (this.oldVar_sortOperator_finType == Filter.OP_IN || this.oldVar_sortOperator_finType == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeWaiverEnquiry,
					"FinanceType", this.finType.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finType.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FeeWaiverEnquiry, "FinanceType");
			if (dataObject instanceof String) {
				this.finType.setValue("");
			} else {
				FinanceType details = (FinanceType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFinType());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinCurrency"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering " + event.toString());

		if (this.oldVar_sortOperator_finCcy == Filter.OP_IN || this.oldVar_sortOperator_finCcy == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeWaiverEnquiry, "Currency",
					this.finCcy.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finCcy.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FeeWaiverEnquiry, "Currency");
			if (dataObject instanceof String) {
				this.finCcy.setValue("");
			} else {
				Currency details = (Currency) dataObject;
				if (details != null) {
					this.finCcy.setValue(details.getCcyCode());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchSchdMethod(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		String whereClause = "";
		if (moduleDefiner.equals(FinServiceEvent.OVERDRAFTSCHD)) {
			whereClause = new String(
					" SchdMethod NOT IN ('EQUAL','GRCNDPAY','MAN_PRI','MANUAL','PRI','PRI_PFT','PFTCAP') ");
		}
		if (this.oldVar_sortOperator_scheduleMethod == Filter.OP_IN
				|| this.oldVar_sortOperator_scheduleMethod == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = null;
			if (StringUtils.isEmpty(whereClause)) {
				selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeWaiverEnquiry,
						"ScheduleMethod", this.scheduleMethod.getValue(), new Filter[] {});
			} else {
				selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeWaiverEnquiry,
						"ScheduleMethod", this.scheduleMethod.getValue(), new Filter[] {}, whereClause);
			}
			if (selectedValues != null) {
				this.scheduleMethod.setValue(selectedValues);
			}

		} else {
			Object dataObject = null;
			if (StringUtils.isEmpty(whereClause)) {
				dataObject = ExtendedSearchListBox.show(this.window_FeeWaiverEnquiry, "ScheduleMethod");
			} else {
				dataObject = ExtendedSearchListBox.show(this.window_FeeWaiverEnquiry, "ScheduleMethod", "",
						whereClause);
			}
			if (dataObject instanceof String) {
				this.scheduleMethod.setValue("");
			} else {
				ScheduleMethod details = (ScheduleMethod) dataObject;
				if (details != null) {
					this.scheduleMethod.setValue(details.getSchdMethod());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchPftDaysBasis(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		if (this.oldVar_sortOperator_profitDaysBasis == Filter.OP_IN
				|| this.oldVar_sortOperator_profitDaysBasis == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FeeWaiverEnquiry,
					"InterestRateBasisCode", this.profitDaysBasis.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.profitDaysBasis.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FeeWaiverEnquiry, "InterestRateBasisCode");
			if (dataObject instanceof String) {
				this.profitDaysBasis.setValue("");
			} else {
				InterestRateBasisCode details = (InterestRateBasisCode) dataObject;
				if (details != null) {
					this.profitDaysBasis.setValue(details.getIntRateBasisCode());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	// On Change Events for Multi-Selection Listbox's for Search operators

	public void onSelect$sortOperator_custCIF(Event event) {
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_custCIF, oldVar_sortOperator_custCIF,
				this.custCIF);
	}

	public void onSelect$sortOperator_finBranch(Event event) {
		this.oldVar_sortOperator_finBranch = doChangeStringOperator(sortOperator_finBranch,
				oldVar_sortOperator_finBranch, this.finBranch);
	}

	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_finType, oldVar_sortOperator_finType,
				this.finType);
	}

	public void onSelect$sortOperator_finReference(Event event) {
		this.oldVar_sortOperator_finReference = doChangeStringOperator(sortOperator_finReference,
				oldVar_sortOperator_finReference, this.finReference);
	}

	public void onSelect$sortOperator_finCcy(Event event) {
		this.oldVar_sortOperator_finCcy = doChangeStringOperator(sortOperator_finCcy, oldVar_sortOperator_finCcy,
				this.finCcy);
	}

	public void onSelect$sortOperator_scheduleMethod(Event event) {
		this.oldVar_sortOperator_scheduleMethod = doChangeStringOperator(sortOperator_scheduleMethod,
				oldVar_sortOperator_scheduleMethod, this.scheduleMethod);
	}

	public void onSelect$sortOperator_profitDaysBasis(Event event) {
		this.oldVar_sortOperator_profitDaysBasis = doChangeStringOperator(sortOperator_profitDaysBasis,
				oldVar_sortOperator_profitDaysBasis, this.profitDaysBasis);
	}

	private int doChangeStringOperator(Listbox listbox, int oldOperator, Textbox textbox) {

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

		if (oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN) {
			if (!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)) {
				textbox.setValue("");
			}
		} else {
			if (searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN) {
				textbox.setValue("");
			}
		}
		return searchOpId;

	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	public void doSearch(boolean isFilterSearch) {
		logger.debug("Entering");

		getSearchObj(false);

		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem itemCustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (itemCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemCustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("lovDescCustCIF",
							"%" + this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(
							new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(
							new Filter("lovDescCustCIF", this.custCIF.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem itemFinReference = this.sortOperator_finReference.getSelectedItem();

			if (itemFinReference != null) {
				final int searchOpId = ((SearchOperators) itemFinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("FinReference",
							"%" + this.finReference.getValue().trim().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(
							new Filter("FinReference", this.finReference.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("FinReference",
							this.finReference.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinReference", this.finReference.getValue().trim(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem itemFinType = this.sortOperator_finType.getSelectedItem();

			if (itemFinType != null) {
				final int searchOpId = ((SearchOperators) itemFinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("FinType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject
							.addFilter(new Filter("FinType", this.finType.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(
							new Filter("FinType", this.finType.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinType", this.finType.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finCcy.getValue())) {

			// get the search operator
			final Listitem itemFinCcy = this.sortOperator_finCcy.getSelectedItem();

			if (itemFinCcy != null) {
				final int searchOpId = ((SearchOperators) itemFinCcy.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("FinCcy", "%" + this.finCcy.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject
							.addFilter(new Filter("FinCcy", this.finCcy.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(
							new Filter("FinCcy", this.finCcy.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinCcy", this.finCcy.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finBranch.getValue())) {

			// get the search operator
			final Listitem itemFinBranch = this.sortOperator_finBranch.getSelectedItem();

			if (itemFinBranch != null) {
				final int searchOpId = ((SearchOperators) itemFinBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(
							new Filter("FinBranch", "%" + this.finBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(
							new Filter("FinBranch", this.finBranch.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(
							new Filter("FinBranch", this.finBranch.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("FinBranch", this.finBranch.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.scheduleMethod.getValue())) {

			// get the search operator
			final Listitem itemScheduleMethod = this.sortOperator_scheduleMethod.getSelectedItem();

			if (itemScheduleMethod != null) {
				final int searchOpId = ((SearchOperators) itemScheduleMethod.getAttribute("data"))
						.getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("ScheduleMethod",
							"%" + this.scheduleMethod.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("ScheduleMethod",
							this.scheduleMethod.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("ScheduleMethod",
							this.scheduleMethod.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("ScheduleMethod", this.scheduleMethod.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.profitDaysBasis.getValue())) {

			// get the search operator
			final Listitem itemProfitDaysBasis = this.sortOperator_profitDaysBasis.getSelectedItem();

			if (itemProfitDaysBasis != null) {
				final int searchOpId = ((SearchOperators) itemProfitDaysBasis.getAttribute("data"))
						.getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_LIKE) {
					searchObject.addFilter(new Filter("ProfitDaysBasis",
							"%" + this.profitDaysBasis.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == Filter.OP_IN) {
					this.searchObject.addFilter(new Filter("ProfitDaysBasis",
							this.profitDaysBasis.getValue().trim().split(","), Filter.OP_IN));
				} else if (searchOpId == Filter.OP_NOT_IN) {
					this.searchObject.addFilter(new Filter("ProfitDaysBasis",
							this.profitDaysBasis.getValue().trim().split(","), Filter.OP_NOT_IN));
				} else {
					searchObject.addFilter(new Filter("ProfitDaysBasis", this.profitDaysBasis.getValue(), searchOpId));
				}
			}
		}

		// Default Sort on the table

		StringBuilder whereClause = new StringBuilder();
		if (!isEnquiry) {
			whereClause.append(" FinIsActive = 1");
			if (App.DATABASE == Database.ORACLE) {
				whereClause.append(" AND (RcdMaintainSts IS NULL OR RcdMaintainSts = '" + moduleDefiner + "' ) ");
			} else {
				// for postgredb sometimes record type is null or empty('')
				whereClause.append(" AND ( (RcdMaintainSts IS NULL or RcdMaintainSts = '') OR RcdMaintainSts = '"
						+ moduleDefiner + "' ) ");
			}

			// Filtering added based on user branch and division
			whereClause.append(" AND FinReference IN (Select FinReference from FeeWaiverHeader)");
			whereClause.append(" ) AND ( " + getUsrFinAuthenticationQry(false));

		} else {
			// Filtering added based on user branch and division
			whereClause.append(" FinReference IN (Select FinReference from FeeWaiverHeader)");
			whereClause.append(" ) AND ( " + getUsrFinAuthenticationQry(false));

		}

		searchObject.addWhereClause(whereClause.toString());
		setSearchObj(searchObject);
		if (isFilterSearch) {
			paging(searchObject);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Render the getting list and set the pagination
	 * 
	 * @param searchObj
	 */
	private void paging(JdbcSearchObject<FinanceMain> searchObj) {
		logger.debug("Entering");
		getPagingFinanceList().setActivePage(0);
		getPagingFinanceList().setDetailed(true);
		getPagedListWrapper().init(searchObj, getListBoxFinance(), getPagingFinanceList());
		this.getListBoxFinance().setItemRenderer(new FinanceMainSelectItemRenderer());
		logger.debug("Leaving");
	}

	// When item double clicked
	public void onFinanceItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());
		final Listitem item;
		item = this.getListBoxFinance().getSelectedItem();
		if (item != null) {
			openFeeWaiverHeaderDialog(item);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param item
	 */
	private void openFeeWaiverHeaderDialog(Listitem item) {
		logger.debug("Entering ");
		// CAST AND STORE THE SELECTED OBJECT
		final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
		// Fee Waivers
		FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
		feeWaiverHeader.setFinID(aFinanceMain.getFinID());
		feeWaiverHeader.setFinReference(aFinanceMain.getFinReference());
		feeWaiverHeader = feeWaiverHeaderService.getFeeWiaverEnquiryList(feeWaiverHeader);
		if (feeWaiverHeader.getFeeWaiverDetails().isEmpty()) {
			MessageUtil.showMessage("Waiver is not Initiated for the LAN :" + aFinanceMain.getFinReference());
			return;
		}
		// FinanceDetails
		FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailForCovenants(aFinanceMain);

		showFeeWaiverHeaderView(feeWaiverHeader, financeDetail);
		logger.debug("Leaving ");
	}

	private void showFeeWaiverHeaderView(FeeWaiverHeader feeWaiverHeader, FinanceDetail financeDetail) {
		logger.debug("Entering");

		if (feeWaiverHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			feeWaiverHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		map.put("feeWaiverHeader", feeWaiverHeader);
		map.put("feeWaiverEnquiryListCtrl", this);
		map.put("financeDetail", financeDetail);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("isEnquiry", enqiryModule);
		map.put("isWaiverEnquiry", true);
		map.put("roleCode", getRole());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeeWaiverHeaderDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnClear(Event event) {
		logger.debug("Entering" + event.toString());

		this.custCIF.setValue("");
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_finType.setSelectedIndex(0);
		this.finCcy.setValue("");
		this.sortOperator_finCcy.setSelectedIndex(0);
		this.finBranch.setValue("");
		this.sortOperator_finBranch.setSelectedIndex(0);
		this.scheduleMethod.setValue("");
		this.sortOperator_scheduleMethod.setSelectedIndex(0);
		this.profitDaysBasis.setValue("");
		this.sortOperator_profitDaysBasis.setSelectedIndex(0);
		this.listBoxFinance.getItems().clear();
		this.oldVar_sortOperator_custCIF = 0;
		this.oldVar_sortOperator_finType = 0;
		this.oldVar_sortOperator_finBranch = 0;
		this.oldVar_sortOperator_finCcy = 0;
		this.oldVar_sortOperator_finReference = 0;
		this.oldVar_sortOperator_profitDaysBasis = 0;
		this.oldVar_sortOperator_scheduleMethod = 0;
		if (this.searchObject != null) {
			this.pagingFinanceList.setActivePage(0);
			this.pagingFinanceList.setDetailed(true);
			doSearch(true);
		}

		logger.debug("Leaving" + event.toString());

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JdbcSearchObject<FinanceMain> getSearchObj(boolean isRefresh) {

		if (isRefresh) {
			return searchObject;
		}

		if (searchObject == null) {
			this.searchObject = new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		} else {
			searchObject.getFilters().clear();
			searchObject.getSorts().clear();
			searchObject.addWhereClause("");
		}
		// PSD# 144918 && && 146986
		this.searchObject.addTabelName("FinanceMain_AView");
		// comminted by meena in clix feewaver enquiry not filtered
		// Filter[] rcdTypeFilter = new Filter[2];
		// rcdTypeFilter[0] = new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL);
		// rcdTypeFilter[1] = new Filter("RecordType", "", Filter.OP_EQUAL);
		if (!moduleDefiner.equals(FinServiceEvent.COVENANTS) && !moduleDefiner.equals(FinServiceEvent.FEEWAIVERS)) {
			// this.searchObject.addFilterOr(rcdTypeFilter);
		}

		if (filterList != null && !filterList.isEmpty()) {
			for (Filter filter : filterList) {
				this.searchObject.addFilter(filter);
			}
		}

		return this.searchObject;
	}

	public void setSearchObj(JdbcSearchObject<FinanceMain> searchObj) {
		this.searchObject = searchObj;
	}

	public Object getDialogCtrl() {
		return dialogCtrl;
	}

	public void setDialogCtrl(Object dialogCtrl) {
		this.dialogCtrl = dialogCtrl;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public Paging getPagingFinanceList() {
		return pagingFinanceList;
	}

	public void setPagingFinanceList(Paging pagingFinanceList) {
		this.pagingFinanceList = pagingFinanceList;
	}

	public Listbox getListBoxFinance() {
		return listBoxFinance;
	}

	public void setListBoxFinance(Listbox listBoxFinance) {
		this.listBoxFinance = listBoxFinance;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FeeWaiverHeaderService getFeeWaiverHeaderService() {
		return feeWaiverHeaderService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

}