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
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FinanceWorkflowRoleUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.ExtendedFieldMaintenance;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.finance.ChangeTDSService;
import com.pennant.backend.service.finance.ExtendedFieldMaintenanceService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinCovenantMaintanceService;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.service.finance.FinOptionMaintanceService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.service.finance.LoanDownSizingService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.RepaymentCancellationService;
import com.pennant.backend.service.finance.validation.FinanceCancelValidator;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.fincancelupload.exception.FinCancelUploadError;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinanceMainSelectItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;

/**
 * 
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceSelect.zul file.
 */
public class FinanceSelectCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = -5081318673331825306L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceSelect; // autowired
	protected Borderlayout borderlayout_FinanceSelect; // autowired
	protected Textbox custCIF; // autowired
	protected Textbox finReference; // autowired
	protected Checkbox allowPreMaturedCases;
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
	private String workflowCode = "";
	private String eventCodeRef = "";
	private String menuItemRightName = null;
	private String menuItemName = null;
	private Tab tab;
	private Tabbox tabbox;

	// not auto wired vars
	private transient Object dialogCtrl = null;
	private transient WorkFlowDetails workFlowDetails = null;
	protected JdbcSearchObject<FinanceMain> searchObject;
	private List<Filter> filterList;
	protected Button btnClear;
	protected Button btnNew;
	protected Label label_FinanceMainSelect_AllowPreMaturedCases;
	private transient FinanceDetailService financeDetailService;
	private transient ManualPaymentService manualPaymentService;
	private transient ReceiptService receiptService;
	private transient FinanceWriteoffService financeWriteoffService;
	private transient FinanceCancellationService financeCancellationService;
	private transient FinanceMaintenanceService financeMaintenanceService;
	private transient RepaymentCancellationService repaymentCancellationService;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient FinanceTypeService financeTypeService;
	private transient FinCovenantMaintanceService finCovenantMaintanceService;
	private transient FinOptionMaintanceService finOptionMaintanceService;
	private transient FeeWaiverHeaderService feeWaiverHeaderService;
	private transient LinkedFinancesService linkedFinancesService;
	private transient FinOCRHeaderService finOCRHeaderService;
	private transient OverdrafLoanService overdrafLoanService;
	private transient ManualAdviseService manualAdviseService;

	private FinanceMain financeMain;
	private boolean isDashboard = false;
	private boolean isDetailScreen = false;
	private String buildedWhereCondition = "";
	final Map<String, Object> map = getDefaultArguments();
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private List<String> usrfinRolesList = new ArrayList<String>();
	private transient ChangeTDSService changeTDSService;// Clix Requirement added new change TDS Service
	private transient LoanDownSizingService loanDownSizingService;
	private transient FinServiceInstrutionDAO finServiceInstructionDAO;
	private transient FinExcessAmountDAO finExcessAmountDAO;
	private transient ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private transient FinReceiptHeaderDAO finReceiptHeaderDAO;
	private transient ExtendedFieldMaintenanceService extendedFieldMaintenanceService;
	private FinanceCancelValidator financeCancelValidator;

	/**
	 * Default constructor
	 */
	public FinanceSelectCtrl() {
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
	public void onCreate$window_FinanceSelect(Event event) {
		logger.debug("Entering" + event.toString());

		menuItemName = getMenuItemName(event, menuItemName);
		if (StringUtils.isNotEmpty(menuItemName)) {
			if (getUserWorkspace().getHasMenuRights().containsKey(menuItemName)) {
				menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemName);
			}

			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();

			/* set components visible dependent on the users rights */
			doCheckRights();
			map.put("enqiryModule", super.enqiryModule);

			checkAndSetModDef(tabbox);
		}

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
			this.searchObject.addField("Version");
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
			this.isDashboard = true;
			Events.postEvent("onClick$btnSearch", window_FinanceSelect, event);
			if (arguments.containsKey("detailScreen")) {
				this.isDetailScreen = true;
				if (arguments.containsKey("FinanceReference")) {
					this.finReference.setValue((String) (arguments.get("FinanceReference")));
				}
				Events.postEvent("onFinanceItemDoubleClicked", window_FinanceSelect, event);
			}

		}

		usrfinRolesList = getUserFinanceRoles(new String[] { "FINANCE" }, moduleDefiner);

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
				this.window_FinanceSelect.onClose();
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
		this.window_FinanceSelect.onClose();
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceSelect,
					"FinanceMaintenance", this.finReference.getValue(), filters,
					StringUtils.trimToNull(this.searchObject.getWhereClause()));
			if (selectedValues != null) {
				this.finReference.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "FinanceMaintenance",
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceSelect, "Branch",
					this.finBranch.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finBranch.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "Branch");
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceSelect, "FinanceType",
					this.finType.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finType.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "FinanceType");
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceSelect, "Currency",
					this.finCcy.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.finCcy.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "Currency");
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
				selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceSelect, "ScheduleMethod",
						this.scheduleMethod.getValue(), new Filter[] {});
			} else {
				selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceSelect, "ScheduleMethod",
						this.scheduleMethod.getValue(), new Filter[] {}, whereClause);
			}
			if (selectedValues != null) {
				this.scheduleMethod.setValue(selectedValues);
			}

		} else {
			Object dataObject = null;
			if (StringUtils.isEmpty(whereClause)) {
				dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "ScheduleMethod");
			} else {
				dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "ScheduleMethod", "", whereClause);
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
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceSelect,
					"InterestRateBasisCode", this.profitDaysBasis.getValue(), new Filter[] {});
			if (selectedValues != null) {
				this.profitDaysBasis.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_FinanceSelect, "InterestRateBasisCode");
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

		/*
		 * if (usrfinRolesList == null || usrfinRolesList.isEmpty()) { return; }
		 */

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
		Date appDate = SysParamUtil.getAppDate();
		StringBuilder whereClause = new StringBuilder();

		if (moduleDefiner.equals(FinServiceEvent.WRITEOFFPAY)) {
			whereClause = new StringBuilder(" FinIsActive = 0 ");
		} else if (moduleDefiner.equals(FinServiceEvent.BASICMAINTAIN)
				|| moduleDefiner.equals(FinServiceEvent.LINKDELINK)) {
			whereClause = new StringBuilder(" ");
		} else if (moduleDefiner.equals(FinServiceEvent.EXTENDEDFIELDS_MAINTAIN)) {
			whereClause = new StringBuilder(" FinIsActive in ( 0, 1) ");
		} else if (moduleDefiner.equals(FinServiceEvent.COLLATERAL)) {
			whereClause = new StringBuilder(" FinIsActive = 0");
		} else if (!moduleDefiner.equals(FinServiceEvent.RECEIPT)) {
			whereClause = new StringBuilder(" FinIsActive = 1 ");
		}
		// ### 11-10-2018,Ticket id:124998
		if (moduleDefiner.equals(FinServiceEvent.RECEIPT)) {
			whereClause.append(" CLOSINGSTATUS!='" + FinanceConstants.CLOSE_STATUS_CANCELLED
					+ "' or (CLOSINGSTATUS is null or CLOSINGSTATUS  in ('" + FinanceConstants.CLOSE_STATUS_EARLYSETTLE
					+ "','" + FinanceConstants.CLOSE_STATUS_WRITEOFF + "','" + FinanceConstants.CLOSE_STATUS_MATURED
					+ "')) and ProductCategory != '" + FinanceConstants.PRODUCT_GOLD + "'");
		} else if (!moduleDefiner.equals(FinServiceEvent.WRITEOFF)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_GOLD + "'");
		}
		if (StringUtils.isNotEmpty(buildedWhereCondition)) {
			if (moduleDefiner.equals(FinServiceEvent.BASICMAINTAIN)
					|| moduleDefiner.equals(FinServiceEvent.LINKDELINK)) {
				whereClause = new StringBuilder(" (" + buildedWhereCondition + ") ");
			} else {
				whereClause.append(" AND (" + buildedWhereCondition + ") ");
			}
		}
		if (!(moduleDefiner.equals(FinServiceEvent.EXTENDEDFIELDS_MAINTAIN))) {
			/*
			 * if (!!ImplementationConstants.ALLOW_ALL_SERV_RCDS) { if (App.DATABASE == Database.ORACLE) {
			 * whereClause.append(" AND (RcdMaintainSts IS NULL OR RcdMaintainSts = '" + moduleDefiner + "' ) "); } else
			 * { // for postgredb sometimes record type is null or empty('')
			 * whereClause.append(" AND ( (RcdMaintainSts IS NULL or RcdMaintainSts = '') OR RcdMaintainSts = '" +
			 * moduleDefiner + "' ) "); } }
			 */}

		int backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_RATECHG_BACK_DATE");
		Date backValueDate = DateUtil.addDays(appDate, backValueDays);

		if (moduleDefiner.equals(FinServiceEvent.RATECHG)) {
			whereClause.append(" AND (AllowGrcPftRvw = 1 OR AllowRepayRvw = 1 OR RateChgAnyDay = 1) ");
			whereClause.append(" AND FinCurrAssetValue > 0 ");

			/*
			 * whereClause.append(" OR (FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND ");
			 * whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "'))");
			 */
		} else if (moduleDefiner.equals(FinServiceEvent.CHGRPY)) {

		} else if (moduleDefiner.equals(FinServiceEvent.ADDDISB)) {
			whereClause.append(" AND AlwMultiDisb = 1  AND MaturityDate > '" + appDate + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.RLSDISB)) {
			whereClause.append(" AND AlwMultiDisb = 1  AND MaturityDate > '" + appDate + "'");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.POSTPONEMENT)) {
			whereClause.append(" AND (Defferments - AvailedDefRpyChange > 0 OR RcdMaintainSts ='"
					+ FinServiceEvent.POSTPONEMENT + "' ) ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.UNPLANEMIH)) {
			whereClause.append(" AND (MaxUnplannedEmi - AvailedUnPlanEmi > 0 OR RcdMaintainSts ='"
					+ FinServiceEvent.UNPLANEMIH + "') ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.ADDTERM)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.RMVTERM)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.RECALCULATE)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");

		} else if (moduleDefiner.equals(FinServiceEvent.SUBSCHD)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");

		} else if (moduleDefiner.equals(FinServiceEvent.CHGPFT)) {
			whereClause.append(" AND (AllowGrcPftRvw = 1 OR AllowRepayRvw = 1) ");
			/*
			 * whereClause.append(" OR (FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND ");
			 * whereClause.append(" FinStartDate >= '" + backValueDate.toString() + "'))");
			 */
		} else if (moduleDefiner.equals(FinServiceEvent.CHGFRQ)) {
			whereClause.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D + "' ");
			whereClause.append(" AND RepayPftFrq <> '" + "D0000" + "'");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.RESCHD)) {
			whereClause.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D + "' ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
			whereClause.append(" AND AllowGrcPeriod = 1");
			whereClause.append(" AND GrcPeriodEndDate >= '" + appDate + "' ");
		} else if (moduleDefiner.equals(FinServiceEvent.RECEIPT)) {
			// whereClause.append(" AND FinStartDate < '" + appDate+"' " );
			whereClause.append(" AND FinCurrAssetValue > 0 ");
		} else if (moduleDefiner.equals(FinServiceEvent.SCHDRPY)) {
			whereClause.append(" AND FinStartDate < '" + appDate + "' ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.EARLYSETTLE)) {
			whereClause.append(" AND FinStartDate < '" + appDate + "' ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {
			whereClause.append(" AND FinStartDate < '" + appDate + "' ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.WRITEOFF)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
			if (!this.allowPreMaturedCases.isChecked()) {
				whereClause.append(" AND MaturityDate < '" + appDate + "'");
			}
			whereClause.append(" AND ((fincurrassetvalue+feechargeamt+ TotalCpz) - finRepaymentAmount) > 0 ");
		} else if (moduleDefiner.equals(FinServiceEvent.WRITEOFFPAY)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		}
		if (moduleDefiner.equals(FinServiceEvent.WRITEOFF) || moduleDefiner.equals(FinServiceEvent.WRITEOFFPAY)) {
			if (!this.allowPreMaturedCases.isChecked()) {
				whereClause.append(" AND MaturityDate <= '" + appDate + "'");
			} else {
				whereClause.append(" AND MaturityDate > '" + appDate + "'");
			}
		} else if (moduleDefiner.equals(FinServiceEvent.CANCELRPY)) {
			// whereClause.append(" OR (FinIsActive = 0 AND ClosingStatus = 'M') ");
		} else if (moduleDefiner.equals(FinServiceEvent.CANCELFIN)) {
			backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_CANFIN_BACK_DATE");
			backValueDate = DateUtil.addDays(appDate, backValueDays);
			String backValDate = DateUtil.formatToFullDate(backValueDate);

			// whereClause.append(" AND MigratedFinance = 0 ");
			whereClause.append(" AND (");
			if (!ImplementationConstants.ALLOW_CANCEL_LOAN_AFTER_PAYMENTS) {
				whereClause.append(" FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate AND ");
			}
			whereClause.append(" FinStartDate >= '" + backValDate + "')");
			whereClause.append(" AND AllowCancelFin = 1");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
			whereClause.append(" AND RcdMaintainSts = 'CancelFinance' AND FinIsActive = 1 ");
		} else if (moduleDefiner.equals(FinServiceEvent.CANCELDISB)) {
			whereClause.append(" AND ( FinReference IN (select FinReference from FinDisbursementDetails");
			whereClause.append(" where DisbDate >= '" + appDate + "') ");
			whereClause.append(" AND ProductCategory = '" + FinanceConstants.PRODUCT_ODFACILITY + "' )");
		} else if (moduleDefiner.equals(FinServiceEvent.OVERDRAFTSCHD)) {
			whereClause.append(" AND MaturityDate > '" + appDate + "'");
			whereClause.append(" AND ProductCategory = '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if ((moduleDefiner.equals(FinServiceEvent.BASICMAINTAIN)
				|| moduleDefiner.equals(FinServiceEvent.LINKDELINK))) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.RPYBASICMAINTAIN)) {
		} else if (moduleDefiner.equals(FinServiceEvent.INSCHANGE)) {
			whereClause.append("AND FinReference IN (select  Reference from FinInsurances where PaymentMethod=" + "'"
					+ InsuranceConstants.PAYTYPE_SCH_FRQ + "')");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.PLANNEDEMI)) {
			whereClause.append(" AND PlanEMIHAlw = 1");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.REAGING)) {
			whereClause.append(" AND (MaxReAgeHolidays - AvailedReAgeH > 0 OR RcdMaintainSts ='"
					+ FinServiceEvent.REAGING + "') ");
			whereClause.append(
					" AND FinReference IN ( Select D.FinReference From FinODDetails D Where D.FinCurODAmt > 0 AND D.FinODSchdDate > GrcPeriodEndDate) ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.HOLDEMI)) {
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
			whereClause.append("AND FinReference NOT IN (select  FinReference from FinanceMain where finrepaymethod="
					+ "'" + InstrumentType.MANUAL.name() + "')");
		} else if (moduleDefiner.equals(FinServiceEvent.CHGSCHDMETHOD)) {
			whereClause
					.append(" AND RepayRateBasis <> '" + CalculationConstants.RATE_BASIS_D + "' AND StepFinance = 0 ");
			whereClause.append(" AND ProductCategory != '" + FinanceConstants.PRODUCT_ODFACILITY + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.FEEWAIVERS)) {
			/*
			 * whereClause.append(" AND FinReference IN (Select FinReference from ManualAdvise Where  AdviseType =" +
			 * "'" + FinanceConstants.MANUAL_ADVISE_RECEIVABLE + "' and AdviseAmount-PaidAmount-WaivedAmount >0)");
			 * whereClause.append(
			 * " OR FinReference IN (Select FinReference from finoddetails Where  totpenaltybal > 0 or lpiBal > 0)");
			 */
		} else if (moduleDefiner.equals(FinServiceEvent.CHANGETDS)) {
			whereClause.append(" AND  MaturityDate > '" + appDate + "' AND  FinStartDate <= '" + appDate + "'");
		} else if (moduleDefiner.equals(FinServiceEvent.LOANDOWNSIZING)) {
			whereClause.append(
					"AND FinAssetvalue > (CASE WHEN stepfinance = 1 and CalcOfSteps = 'AMT' THEN FinCurrAssetValue + TotalCpz else FinCurrAssetValue END) ");
		} else if (FinServiceEvent.RESTRUCTURE.equals(moduleDefiner)) {
			whereClause.append(" AND RcdMaintainSts = 'Restructure' AND FinIsActive = 1 ");
		} else if (moduleDefiner.equals(FinServiceEvent.COLLATERAL)) {
			whereClause.append("AND FinReference IN (SELECT Reference From CollateralAssignment)");
			whereClause.append(" AND ClosingStatus in ('W','E','C','M')");
		} else if (moduleDefiner.equals(FinServiceEvent.PRINH)) {
			whereClause.append(" AND StepFinance = 0 and ManualSchedule = 0 and Schedulemethod = '"
					+ CalculationConstants.SCHMTHD_EQUAL + "' ");
		}

		// Written Off Finance Reference Details Condition
		if (FinServiceEvent.WRITEOFFPAY.equals(moduleDefiner)) {
			whereClause.append(" AND FinReference IN (SELECT FinReference From FinWriteoffDetail) ");
		} else if (!(FinServiceEvent.BASICMAINTAIN.equals(moduleDefiner) || FinServiceEvent.CHGFRQ.equals(moduleDefiner)
				|| FinServiceEvent.RPYBASICMAINTAIN.equals(moduleDefiner))) {
			whereClause.append(" AND FinReference NOT IN (SELECT FinReference From FinWriteoffDetail) ");
		}

		// Filtering added based on user branch and division
		whereClause.append(" ) AND ( " + getUsrFinAuthenticationQry(false));

		// Along with Above events WriteOff Loans
		if (FinServiceEvent.HOLDEMI.equals(this.moduleDefiner) || FinServiceEvent.ADDDISB.equals(this.moduleDefiner)) {
			whereClause.append(" AND ( ClosingStatus IS NULL OR ClosingStatus !='"
					+ FinanceConstants.CLOSE_STATUS_WRITEOFF + "')");
		}

		// Filtering closed loans based on system parameter
		if (StringUtils.equals("N", SysParamUtil.getValueAsString("ALLOW_CLOSED_LOANS_IN_RECEIPTS"))) {
			whereClause.append(" AND FINISACTIVE = '1' ");
		}

		// Below servicing options will not be applicable to open amortization schedule
		if (String
				.join("|", FinServiceEvent.ADDTERM, FinServiceEvent.RMVTERM, FinServiceEvent.CHGFRQ,
						FinServiceEvent.CHGGRCEND, FinServiceEvent.CHGRPY, FinServiceEvent.OVERDRAFTSCHD,
						FinServiceEvent.PLANNEDEMI, FinServiceEvent.POSTPONEMENT, FinServiceEvent.REAGING,
						FinServiceEvent.RESCHD, FinServiceEvent.UNPLANEMIH, FinServiceEvent.CHGSCHDMETHOD)
				.contains(moduleDefiner)) {

			whereClause.append(" And ManualSchedule != 1 ");
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
	@SuppressWarnings("rawtypes")
	public void onFinanceItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());
		final Listitem item;
		if (isDashboard && isDetailScreen) {
			List<FinanceMain> financeMainList = getPagedListWrapper().getPagedListService()
					.getBySearchObject(this.searchObject);
			if (this.listBoxFinance != null && this.listBoxFinance.getItems().size() == 1 && financeMainList != null
					&& financeMainList.size() == 1) {
				item = (Listitem) this.listBoxFinance.getFirstChild().getNextSibling();
				final FinanceMain aFinanceMain = (FinanceMain) financeMainList.get(0);
				item.setAttribute("data", aFinanceMain);
			} else {
				return;
			}
		} else {
			item = this.getListBoxFinance().getSelectedItem();
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			int eodProgressCount = financeDetailService.getProgressCountByCust(aFinanceMain.getCustID());

			// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
			if (eodProgressCount > 0) {
				MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("60203", null)));
				logger.debug("Leaving");
				return;
			}

			if (StringUtils.isNotEmpty(moduleDefiner) && moduleDefiner.equals(FinServiceEvent.ADDDISB)) {
				boolean holdDisbursement = financeDetailService.isholdDisbursementProcess(aFinanceMain.getFinID());
				boolean limitBlock = overdrafLoanService.isLimitBlock(aFinanceMain.getFinID());

				if (holdDisbursement) {
					MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("HD99019", null)));
					logger.debug("Leaving");
					return;
				}

				// If OD loan is in blocked state, not allowed to do add disbursement
				if (limitBlock && FinanceConstants.PRODUCT_ODFACILITY.equals(aFinanceMain.getProductCategory())) {
					String[] valueParm = new String[1];
					valueParm[0] = "This OD Loan was Blocked.Not allowed to do Add Disbursement.";
					MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
					return;
				}

				// Add Disbursement, the Application will provide a warning message if there are some unadjusted dues in
				// customer against all loans.
				String addDisbDuesWarningMsgReq = SysParamUtil
						.getValueAsString(SMTParameterConstants.ADD_DISB_DUES_WARNG);

				if (StringUtils.equals(addDisbDuesWarningMsgReq, PennantConstants.YES)) {
					String finReferences = getFinanceDetailService()
							.getCustomerDueFinReferces(aFinanceMain.getCustID());

					if (finReferences.length() > 0) {
						MessageUtil.showMessage(
								Labels.getLabel("info.param_add_disb_warning", new Object[] { finReferences }));
					}
				}

				FinOCRHeader finOCRHeader = finOCRHeaderService.getFinOCRHeaderByRef(aFinanceMain.getFinID(),
						TableType.TEMP_TAB.getSuffix());
				if (finOCRHeader == null && StringUtils.isNotBlank(aFinanceMain.getParentRef())) {
					finOCRHeader = finOCRHeaderService.getFinOCRHeaderByRef(aFinanceMain.getFinID(),
							TableType.TEMP_TAB.getSuffix());
				}

				if (finOCRHeader != null) {
					MessageUtil.showError(Labels.getLabel("label_FinOCRDialog_OCRMaintenance.value"));
					return;
				}

			}

		}

		if (StringUtils.isNotEmpty(moduleDefiner) && !moduleDefiner.equals(FinServiceEvent.EARLYRPY)
				&& !moduleDefiner.equals(FinServiceEvent.RECEIPT) && !moduleDefiner.equals(FinServiceEvent.SCHDRPY)
				&& !moduleDefiner.equals(FinServiceEvent.EARLYSETTLE)
				&& !moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ) && !moduleDefiner.equals(FinServiceEvent.WRITEOFF)
				&& !moduleDefiner.equals(FinServiceEvent.CANCELFIN)
				&& !moduleDefiner.equals(FinServiceEvent.BASICMAINTAIN)
				&& !moduleDefiner.equals(FinServiceEvent.RPYBASICMAINTAIN)
				&& !moduleDefiner.equals(FinServiceEvent.CANCELRPY)
				&& !moduleDefiner.equals(FinServiceEvent.WRITEOFFPAY)
				&& !moduleDefiner.equals(FinServiceEvent.COVENANTS) && !moduleDefiner.equals(FinServiceEvent.FINOPTION)
				&& !moduleDefiner.equals(FinServiceEvent.FEEWAIVERS) && !moduleDefiner.equals(FinServiceEvent.CHANGETDS)
				&& !moduleDefiner.equals(FinServiceEvent.LOANDOWNSIZING)
				&& !moduleDefiner.equals(FinServiceEvent.COLLATERAL)
				&& !moduleDefiner.equals(FinServiceEvent.LINKDELINK)
				&& !moduleDefiner.equals(FinServiceEvent.EXTENDEDFIELDS_MAINTAIN)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinanceMainDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.EARLYRPY) || moduleDefiner.equals(FinServiceEvent.SCHDRPY)
				|| moduleDefiner.equals(FinServiceEvent.EARLYSETTLE)
				|| moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinanceRepaymentDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.RECEIPT)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinanceReceiptDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.WRITEOFF)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinanceWriteoffDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.CANCELFIN)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinanceCancellationDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.BASICMAINTAIN)
				|| moduleDefiner.equals(FinServiceEvent.RPYBASICMAINTAIN)
				|| moduleDefiner.equals(FinServiceEvent.WRITEOFFPAY)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinMaintenanceDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.CANCELRPY)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinanceRepayCancelDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.COVENANTS)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinCovenantMaintanceDialog(item);
		} else if (moduleDefiner.equals(FinServiceEvent.COLLATERAL)) {
			userAccessLog(menuItemName, item, moduleDefiner);
			openFinCollateralsMaintanceDialog(item);
		} else if (moduleDefiner.equals(FinServiceEvent.FINOPTION)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinFinoptionMaintanceDialog(item);
		} else if (moduleDefiner.equals(FinServiceEvent.FEEWAIVERS)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFeeWaiverHeaderDialog(item);
		} else if (moduleDefiner.equals(FinServiceEvent.LINKDELINK)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openLinkDelinkMaintenanceDialog(item);

		} else if (moduleDefiner.equals(FinServiceEvent.CHANGETDS)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openFinChangeTDSMaintanceDialog(item);
		} else if (moduleDefiner.equals(FinServiceEvent.EXTENDEDFIELDS_MAINTAIN)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openExtendedFieldsMaintanceDialog(item);
		} else if (moduleDefiner.equals(FinServiceEvent.LOANDOWNSIZING)) {

			userAccessLog(menuItemName, item, moduleDefiner);
			openLoanDownsizingDialog(item);
		} else {
			if (this.getListBoxFinance().getSelectedItem() != null) {
				final Listitem li = this.getListBoxFinance().getSelectedItem();
				final Object object = li.getAttribute("data");

				if (getDialogCtrl() != null) {
					dialogCtrl = (Object) getDialogCtrl();
				}
				try {

					Class[] paramType = { Class.forName("java.lang.Object"),
							Class.forName("com.pennant.backend.util.JdbcSearchObject") };
					Object[] stringParameter = { object, this.searchObject };
					if (dialogCtrl.getClass().getMethod("doSetFinance", paramType) != null) {
						dialogCtrl.getClass().getMethod("doSetFinance", paramType).invoke(dialogCtrl, stringParameter);
					}
					doClose();

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		this.isDashboard = false;
		this.isDetailScreen = false;
		logger.debug("Leaving" + event.toString());
	}

	private void openExtendedFieldsMaintanceDialog(Listitem item) {

		logger.debug(Literal.ENTERING);
		// get the selected FinanceMain object

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			// Check whether the user has authority to change/view the record.
			String whereCond1 = " where FinReference=?";

			if (!doCheckAuthority(aFinanceMain, whereCond1, new Object[] { aFinanceMain.getFinReference() })) {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return;
			}

			// Set WorkFlow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
			ExtendedFieldMaintenance extendedFieldMaintenance = null;
			// ExtendedFieldMaintenance
			extendedFieldMaintenance = extendedFieldMaintenanceService
					.getExtendedFieldMaintenanceByFinRef(aFinanceMain.getFinReference());

			if (ObjectUtils.isEmpty(extendedFieldMaintenance)) {
				extendedFieldMaintenance = new ExtendedFieldMaintenance();
				extendedFieldMaintenance.setReference(aFinanceMain.getFinReference());
				extendedFieldMaintenance.setNewRecord(true);
			}

			// Role Code State Checking
			String userRole = extendedFieldMaintenance.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			String nextroleCode = extendedFieldMaintenance.getNextRoleCode();
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aFinanceMain.getFinReference();
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}

			String whereCond = " where FinID = ?";
			if (isWorkFlowEnabled()) {

				if (doCheckAuthority(aFinanceMain, whereCond, new Object[] { aFinanceMain.getFinID() })
						|| StringUtils.equals(aFinanceMain.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
					showExtendedFieldsMaintainDetailView(extendedFieldMaintenance, aFinanceMain.getFinID());
				} else {
					MessageUtil.showError(Labels.getLabel("info.not_authorized"));
				}
			} else {
				if (doCheckAuthority(aFinanceMain, whereCond, new Object[] { aFinanceMain.getFinID() })
						|| StringUtils.equals(aFinanceMain.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
					showExtendedFieldsMaintainDetailView(extendedFieldMaintenance, aFinanceMain.getFinID());
				} else {
					MessageUtil.showError(Labels.getLabel("info.not_authorized"));
				}
			}
		}
		logger.debug(Literal.LEAVING);

	}

	private void showExtendedFieldsMaintainDetailView(ExtendedFieldMaintenance fldMnt, long finID) {

		logger.debug("Entering");

		if (fldMnt.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fldMnt.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		FinanceDetail financeDetail = financeDetailService.getFinSchdDetailById(finID, "_AView", false);

		map.put("extendedFieldMaintenance", fldMnt);
		map.put("financeSelectCtrl", this);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("financeDetail", financeDetail);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ExtendedFieldMaintenanceDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {

		// Finance Maintenance Workflow Check & Assignment
		if (StringUtils.isNotEmpty(workflowCode)) {
			String workflowTye = getFinanceWorkFlowService().getFinanceWorkFlowType(finType, workflowCode,
					isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION : PennantConstants.WORFLOW_MODULE_FINANCE);
			if (workflowTye != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(workflowTye);
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

	}

	private void openFinanceMainDialog(Listitem item) {
		logger.debug("Entering ");
		// get the selected FinanceMain object

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			// Validate Loan is INPROGRESS in any Other Servicing option or NOT
			// ?
			String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(aFinanceMain.getFinID());

			// Check whether the user has authority to change/view the record.
			String whereCond1 = " where FinReference=?";

			if (!doCheckAuthority(aFinanceMain, whereCond1, new Object[] { aFinanceMain.getFinReference() })) {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return;
			}

			if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
				MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
				return;
			}

			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			final FinanceDetail financeDetail = getFinanceDetailService().getServicingFinance(aFinanceMain.getFinID(),
					eventCodeRef, moduleDefiner, userRole);

			// Role Code State Checking
			String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = aFinanceMain.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			} else if (moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {

				Date validFrom = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
				List<FinanceScheduleDetail> scheduelist = financeDetail.getFinScheduleData()
						.getFinanceScheduleDetails();
				for (int i = 1; i < scheduelist.size(); i++) {

					FinanceScheduleDetail curSchd = scheduelist.get(i);
					Date appDate = SysParamUtil.getAppDate();
					if (curSchd.getSchDate().compareTo(appDate) < 0) {
						validFrom = appDate;
						continue;
					}
					if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
						validFrom = curSchd.getSchDate();
						continue;
					}

					if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0) {

						validFrom = curSchd.getSchDate();
						continue;
					}
				}

				if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()
						.compareTo(validFrom) <= 0) {
					ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41019", errParm, valueParm),
							getUserWorkspace().getUserLanguage());
					MessageUtil.showError(errorDetails.getError());

					logger.debug("Leaving");
					return;
				}
			}

			String maintainSts = "";
			if (financeDetail.getFinScheduleData().getFinanceMain() != null) {
				maintainSts = StringUtils
						.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {

				if (isWorkFlowEnabled()) {
					String whereCond = " AND FinReference='" + aFinanceMain.getFinReference() + "' AND version="
							+ aFinanceMain.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond,
							aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces) {
						showDetailView(financeDetail);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(financeDetail);
				}
			}
		}
		logger.debug("Leaving ");
	}

	private void openFinMaintenanceDialog(Listitem item) {
		logger.debug(Literal.ENTERING);

		if (item == null) {
			return;
		}

		final FinanceMain aFm = (FinanceMain) item.getAttribute("data");

		long finID = aFm.getFinID();
		String finRef = aFm.getFinReference();

		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);
		String whereCond1 = " Where FinReference = ?";

		if (!doCheckAuthority(aFm, whereCond1, new Object[] { finRef })) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return;
		}

		// Set Workflow Details
		setWorkflowDetails(aFm.getFinType(), StringUtils.isNotEmpty(aFm.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		String userRole = aFm.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		final FinanceDetail financeDetail = financeMaintenanceService.getFinanceDetailById(finID, "_View", userRole,
				moduleDefiner, eventCodeRef);
		financeDetail.setModuleDefiner(moduleDefiner);

		// Role Code State Checking
		String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finRef;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug("Leaving");
			return;
		}

		String maintainSts = "";
		if (financeDetail.getFinScheduleData().getFinanceMain() != null) {
			maintainSts = StringUtils
					.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finRef;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
		} else {

			if (isWorkFlowEnabled()) {
				String whereCond = " AND FinReference='" + finRef + "' AND version=" + aFm.getVersion() + " ";

				boolean userAcces = validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond, aFm.getTaskId(),
						aFm.getNextTaskId());
				if (userAcces) {
					showMaintainDetailView(financeDetail);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showMaintainDetailView(financeDetail);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void openFinanceRepaymentDialog(Listitem item) {
		logger.debug(Literal.ENTERING);

		if (item == null) {
			return;
		}

		// CAST AND STORE THE SELECTED OBJECT
		final FinanceMain aFm = (FinanceMain) item.getAttribute("data");

		String finRef = aFm.getFinReference();
		long finID = aFm.getFinID();

		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

		// Check whether the user has authority to change/view the record.
		String whereCond1 = " where FinReference=?";

		if (!doCheckAuthority(aFm, whereCond1, new Object[] { finRef })) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return;
		}

		// Set Workflow Details
		String userRole = "";
		if (!StringUtils.equals(moduleDefiner, FinServiceEvent.EARLYSTLENQ)) {
			setWorkflowDetails(aFm.getFinType(), StringUtils.isNotEmpty(aFm.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			userRole = aFm.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}
		}

		final RepayData repayData = getManualPaymentService().getRepayDataById(finID, eventCodeRef, moduleDefiner,
				userRole);

		// Role Code State Checking
		String nextroleCode = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finRef;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug("Leaving");
			return;
		}

		String maintainSts = "";
		if (repayData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null) {
			maintainSts = StringUtils.trimToEmpty(
					repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getRcdMaintainSts());
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finRef;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
		} else {

			if (isWorkFlowEnabled()) {
				String whereCond = " AND FinReference='" + finRef + "' AND version=" + aFm.getVersion() + " ";

				boolean userAcces = validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond, aFm.getTaskId(),
						aFm.getNextTaskId());
				if (userAcces) {
					showRepayDetailView(repayData);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showRepayDetailView(repayData);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void openFinanceReceiptDialog(Listitem item) {
		logger.debug(Literal.ENTERING);

		if (item == null) {
			return;
		}

		// CAST AND STORE THE SELECTED OBJECT
		final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

		long finID = aFinanceMain.getFinID();
		String finRef = aFinanceMain.getFinReference();
		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

		// Check whether the user has authority to change/view the record.
		String whereCond1 = " where FinReference=?";

		if (!doCheckAuthority(aFinanceMain, whereCond1, new Object[] { finRef })) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return;
		}

		// Set Workflow Details
		String userRole = "";
		setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		userRole = aFinanceMain.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}
		// check if payable amount present
		List<ManualAdvise> manualAdvise = financeWriteoffService.getPayableAdvises(finID, "");
		if (CollectionUtils.isNotEmpty(manualAdvise)) {
			MessageUtil.showError(Labels.getLabel("EXCESS/MANUALADVISE_EXITS"));
			return;
		}

		final FinReceiptData receiptData = getReceiptService().getFinReceiptDataById(finRef, SysParamUtil.getAppDate(),
				eventCodeRef, moduleDefiner, userRole);

		// Role Code State Checking
		String nextroleCode = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finRef;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug("Leaving");
			return;
		}

		String maintainSts = "";
		if (receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null) {
			maintainSts = StringUtils.trimToEmpty(
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getRcdMaintainSts());
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finRef;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
		} else if (receiptData.getReceiptHeader().isDepositProcess()) {

			// If record is in Deposit Process, not allowed to do the Process on Realization
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finRef;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "65034", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

		} else {

			if (isWorkFlowEnabled()) {
				String whereCond = " AND FinReference='" + finRef + "' AND version=" + aFinanceMain.getVersion() + " ";

				boolean userAcces = validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond,
						aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
				if (userAcces) {
					showReceiptDetailView(receiptData);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showReceiptDetailView(receiptData);
			}
		}
		logger.debug(Literal.ENTERING);
	}

	private void openFinanceWriteoffDialog(Listitem item) {
		logger.debug(Literal.ENTERING);

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			long finID = aFinanceMain.getFinID();
			String finRef = aFinanceMain.getFinReference();
			String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

			// Check whether the user has authority to change/view the record.
			String whereCond1 = " where FinReference=?";

			if (!doCheckAuthority(aFinanceMain, whereCond1, new Object[] { aFinanceMain.getFinReference() })) {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return;
			}

			if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
				MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
				return;
			}

			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			boolean isPending = receiptService.isReceiptsPending(finID, Long.MIN_VALUE);
			boolean receiptsQueue = receiptUploadDetailDAO.isReceiptsQueue(finRef);
			boolean presentmentsInQueue = finReceiptHeaderDAO.checkPresentmentsInQueue(finID);
			if (isPending || receiptsQueue || presentmentsInQueue) {
				MessageUtil.showError(PennantJavaUtil.getLabel("label_Receipts_Inprogress"));
				return;
			}

			boolean finExcessAmtExists = finExcessAmountDAO.isFinExcessAmtExists(finID);
			if (finExcessAmtExists) {
				MessageUtil.showError(Labels.getLabel("MANUALADVISE_EXITS"));
				return;
			}

			// check if payable amount present
			List<ManualAdvise> manualAdvise = financeWriteoffService.getPayableAdvises(finID, "");
			if (CollectionUtils.isNotEmpty(manualAdvise)) {
				MessageUtil.showError(Labels.getLabel("MANUALADVISE_EXITS"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			final FinanceWriteoffHeader writeoffHeader = financeWriteoffService.getFinanceWriteoffDetailById(finID,
					"_View", userRole, moduleDefiner);

			// Role Code State Checking
			String nextroleCode = writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain()
					.getNextRoleCode();
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}

			String maintainSts = "";
			if (writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain() != null) {
				maintainSts = StringUtils.trimToEmpty(
						writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			List<String> finEvents = finServiceInstructionDAO.getFinEventByFinRef(finRef, "_Temp");
			if (CollectionUtils.isNotEmpty(finEvents)) {
				rcdMaintainSts = finEvents.get(0);
				if (!rcdMaintainSts.equals(moduleDefiner)) {
					MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
					return;
				}
			}

			if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {

				if (isWorkFlowEnabled()) {
					String whereCond = " AND FinReference='" + aFinanceMain.getFinReference() + "' AND version="
							+ aFinanceMain.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond,
							aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces) {
						showWriteoffDetailView(writeoffHeader);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showWriteoffDetailView(writeoffHeader);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void openFinanceCancellationDialog(Listitem item) {
		logger.debug(Literal.ENTERING);

		if (item == null) {
			return;
		}

		// CAST AND STORE THE SELECTED OBJECT
		final FinanceMain fm = (FinanceMain) item.getAttribute("data");

		long finID = fm.getFinID();

		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

		// Check whether the user has authority to change/view the record.
		String whereCond1 = " where FinID = ?";

		if (!doCheckAuthority(fm, whereCond1, new Object[] { fm.getFinReference() })) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return;
		}

		// Set Workflow Details
		setWorkflowDetails(fm.getFinType(), StringUtils.isNotEmpty(fm.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		String userRole = fm.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		final FinanceDetail fd = financeCancellationService.getFinanceDetailById(finID, "_View", userRole,
				moduleDefiner);

		// Role Code State Checking
		FinScheduleData schdData = fd.getFinScheduleData();

		String nextroleCode = schdData.getFinanceMain().getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug("Leaving");
			return;
		}

		String maintainSts = StringUtils.trimToEmpty(schdData.getFinanceMain().getRcdMaintainSts());

		schdData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		FinCancelUploadError error = financeCancelValidator.validLoan(schdData.getFinanceMain(), schedules);

		if (error != null) {
			MessageUtil.showError(financeCancelValidator.getOverrideDescription(error, schdData.getFinanceMain()));
			return;
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
		} else {

			if (isWorkFlowEnabled()) {
				String whereCond = " AND FinID=" + finID + " AND version=" + fm.getVersion() + " ";

				boolean userAcces = validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond, fm.getTaskId(),
						fm.getNextTaskId());
				if (userAcces) {
					showCancellationDetailView(fd);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showCancellationDetailView(fd);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Finance Repayment Details
	 * 
	 * @param item
	 */
	private void openFinanceRepayCancelDialog(Listitem item) {
		logger.debug("Entering ");
		// get the selected FinanceMain object

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			long finID = aFinanceMain.getFinID();
			String finRef = aFinanceMain.getFinReference();
			String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

			// Check whether the user has authority to change/view the record.
			String whereCond1 = " where FinReference=?";

			if (!doCheckAuthority(aFinanceMain, whereCond1, new Object[] { finRef })) {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return;
			}

			if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
				MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
				return;
			}

			aFinanceMain.setFinCategory(financeDetailService.getFinCategory(aFinanceMain.getFinReference()));

			// Set Workflow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			String userRole = aFinanceMain.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			final FinanceDetail financeDetail = getRepaymentCancellationService().getFinanceDetailById(finID, "_View");

			// Role Code State Checking
			String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}

			String maintainSts = "";
			if (financeDetail.getFinScheduleData().getFinanceMain() != null) {
				maintainSts = StringUtils
						.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRcdMaintainSts());
			}

			if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {

				if (isWorkFlowEnabled()) {
					String whereCond = " AND FinReference='" + finRef + "' AND version=" + aFinanceMain.getVersion()
							+ " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond,
							aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces) {
						showRepayCancelView(financeDetail);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showRepayCancelView(financeDetail);
				}
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	private void showDetailView(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * list box List model. This is fine for synchronizing the data in the FinanceMainListbox from the dialog when
		 * we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeSelectCtrl", this);
		map.put("tabbox", tab);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("eventCode", eventCodeRef);
		map.put("menuItemRightName", menuItemRightName);

		// call the ZUL-file with the parameters packed in a map
		try {

			String productType = aFinanceMain.getProductCategory();
			productType = (productType.substring(0, 1)).toUpperCase() + (productType.substring(1)).toLowerCase();

			StringBuilder fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");

			if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_CONVENTIONAL)) {
				String pageName = PennantAppUtil.getFinancePageName(false);
				fileLocaation.append(pageName);
			} else if (moduleDefiner.equalsIgnoreCase(FinServiceEvent.LOANDOWNSIZING)) {
				fileLocaation.append("LoanDownSizingDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_ODFACILITY)) {
				fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/Overdraft/");
				fileLocaation.append("OverdraftFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_CD)) {
				fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/Cd/");
				fileLocaation.append("CDFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(FinanceConstants.PRODUCT_DISCOUNT)) {
				fileLocaation.append("DiscountFinanceMainDialog.zul");
			} else {
				fileLocaation.append("FinanceMainDialog.zul");
			}

			Executions.createComponents(fileLocaation.toString(), null, map);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	private void showMaintainDetailView(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * list box List model. This is fine for synchronizing the data in the FinanceMainListbox from the dialog when
		 * we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeSelectCtrl", this);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceMaintenanceDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	private void showRepayDetailView(RepayData repayData) {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		repayData.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		map.put("repayData", repayData);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("eventCode", eventCodeRef);
		map.put("menuItemRightName", menuItemRightName);
		map.put("financeSelectCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/ManualPayment.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	private void showReceiptDetailView(FinReceiptData receiptData) {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		map.put("receiptData", receiptData);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("eventCode", eventCodeRef);
		map.put("menuItemRightName", menuItemRightName);
		map.put("financeSelectCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	private void showWriteoffDetailView(FinanceWriteoffHeader writeoffHeader) {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		writeoffHeader.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeWriteoffHeader", writeoffHeader);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("eventCode", eventCodeRef);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Payments/FinanceWriteoffDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	public void showCancellationDetailView(FinanceDetail financeDetail) {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		financeDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		map.put("financeDetail", financeDetail);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("eventCode", eventCodeRef);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceCancellationDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 */
	private void showRepayCancelView(FinanceDetail financeDetail) {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		financeDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		map.put("financeDetail", financeDetail);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("financeSelectCtrl", this);
		map.put("eventCode", eventCodeRef);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Cancellation/RepayCancellationDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param item
	 */
	private void openFinCovenantMaintanceDialog(Listitem item) {
		logger.debug("Entering ");
		// get the selected FinanceMain object

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			long finID = aFinanceMain.getFinID();
			String finRef = aFinanceMain.getFinReference();
			String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

			// Check whether the user has authority to change/view the record.
			String whereCond1 = " where FinReference=?";

			if (!doCheckAuthority(aFinanceMain, whereCond1, new Object[] { finRef })) {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return;
			}

			if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
				MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
				return;
			}

			aFinanceMain.setFinCategory(financeDetailService.getFinCategory(aFinanceMain.getFinReference()));

			// Set WorkFlow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			// FinMaintainInstruction
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			if (StringUtils.equals(aFinanceMain.getRcdMaintainSts(), moduleDefiner)) {
				finMaintainInstruction = finCovenantMaintanceService.getFinMaintainInstructionByFinRef(finID,
						moduleDefiner);
			} else {
				finMaintainInstruction.setNewRecord(true);
			}
			// FinanceDetails
			FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailForCovenants(aFinanceMain);

			// Covenants List
			finMaintainInstruction.setFinCovenantTypeList(financeDetail.getCovenantTypeList());

			finMaintainInstruction.setCovenants(financeDetail.getCovenants());

			// Role Code State Checking
			String userRole = finMaintainInstruction.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			String nextroleCode = finMaintainInstruction.getNextRoleCode();
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}

			String maintainSts = "";
			if (finMaintainInstruction != null) {
				maintainSts = StringUtils.trimToEmpty(finMaintainInstruction.getEvent());
			}

			if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {

				if (isWorkFlowEnabled()) {
					String whereCond = " AND FinReference='" + finRef + "' AND version=" + aFinanceMain.getVersion()
							+ " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond,
							aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
					if (userAcces) {
						showFinCovenantMaintanceView(finMaintainInstruction, financeDetail);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showFinCovenantMaintanceView(finMaintainInstruction, financeDetail);
				}
			}
		}
		logger.debug("Leaving ");
	}

	private void openFinCollateralsMaintanceDialog(Listitem item) {

		logger.debug(Literal.ENTERING);

		if (item == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		final FinanceMain fm = (FinanceMain) item.getAttribute("data");
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Set WorkFlow Details
		setWorkflowDetails(fm.getFinType(), StringUtils.isNotEmpty(fm.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond1 = " Where FinReference = ?";
		if (!doCheckAuthority(fm, whereCond1, new Object[] { finReference })) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

		if (StringUtils.isNotEmpty(rcdMaintainSts) && !moduleDefiner.equals(rcdMaintainSts)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return;
		}

		// FinMaintainInstruction
		FinMaintainInstruction fmi = new FinMaintainInstruction();
		if (moduleDefiner.equals(fm.getRcdMaintainSts())) {
			fmi = finCovenantMaintanceService.getFinMaintainInstructionByFinRef(finID, moduleDefiner);
		} else {
			fmi.setNewRecord(true);
		}
		// FinanceDetails
		FinanceDetail financeDetail = financeDetailService.getFinanceDetailForCollateral(fm);

		fmi.setCollateralAssignments(financeDetail.getCollateralAssignmentList());

		// Role Code State Checking
		String userRole = fmi.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		String nextroleCode = fmi.getNextRoleCode();
		String userLanguage = getUserWorkspace().getUserLanguage();

		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug(Literal.LEAVING);
			return;
		}

		String maintainSts = "";
		if (fmi != null) {
			maintainSts = StringUtils.trimToEmpty(fmi.getEvent());
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);
			MessageUtil.showError(errorDetails.getError());
		} else {
			if (isWorkFlowEnabled()) {
				String whereCond = " AND FinReference='" + finReference + "' AND version=" + fm.getVersion() + " ";

				long userId = getUserWorkspace().getLoggedInUser().getUserId();
				boolean userAcces = validateUserAccess(workFlowDetails.getId(), userId, workflowCode, whereCond,
						fm.getTaskId(), fm.getNextTaskId());
				if (userAcces) {
					showFinCollateralMaintanceView(fmi, financeDetail);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showFinCollateralMaintanceView(fmi, financeDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void openFinFinoptionMaintanceDialog(Listitem item) {
		logger.debug(Literal.ENTERING);

		if (item == null) {
			logger.debug(Literal.LEAVING);
			return;
		}
		final FinanceMain fm = (FinanceMain) item.getAttribute("data");
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Set WorkFlow Details
		setWorkflowDetails(fm.getFinType(), StringUtils.isNotEmpty(fm.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

		if (StringUtils.isNotEmpty(rcdMaintainSts) && !moduleDefiner.equals(rcdMaintainSts)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return;
		}

		// FinMaintainInstruction
		FinMaintainInstruction fmi = new FinMaintainInstruction();

		if (moduleDefiner.equals(fm.getRcdMaintainSts())) {
			fmi = finOptionMaintanceService.getFinMaintainInstructionByFinRef(finID, moduleDefiner);
		} else {
			fmi.setNewRecord(true);
		}

		String whereCond1 = " where FinReference = ?";
		if (!doCheckAuthority(fmi, whereCond1, new Object[] { fmi.getFinReference() })) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		// FinanceDetails
		FinanceDetail financeDetail = financeDetailService.getFinanceDetailForFinOptions(fm);

		fmi.setFinServiceInstructions(
				financeMaintenanceService.getFinServiceInstructions(fm.getFinID(), moduleDefiner));

		// Covenants List
		// finMaintainInstruction.setFinCovenantTypeList(financeDetail.getCovenantTypeList());

		fmi.setFinOptions(financeDetail.getFinOptions());

		// Role Code State Checking
		String userRole = fmi.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		String nextroleCode = fmi.getNextRoleCode();
		String userLanguage = getUserWorkspace().getUserLanguage();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug(Literal.LEAVING);
			return;
		}

		String maintainSts = "";
		if (fmi != null) {
			maintainSts = StringUtils.trimToEmpty(fmi.getEvent());
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);
			MessageUtil.showError(errorDetails.getError());
		} else {
			if (isWorkFlowEnabled()) {
				String whereCond = " AND FinReference='" + finReference + "' AND version=" + fm.getVersion() + " ";

				long userId = getUserWorkspace().getLoggedInUser().getUserId();
				boolean userAcces = validateUserAccess(workFlowDetails.getId(), userId, workflowCode, whereCond,
						fm.getTaskId(), fm.getNextTaskId());
				if (userAcces) {
					showFinOptionMaintanceView(fmi, financeDetail);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showFinOptionMaintanceView(fmi, financeDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param item
	 */
	private void openFeeWaiverHeaderDialog(Listitem item) {
		logger.debug(Literal.ENTERING);

		if (item == null) {
			logger.debug(Literal.LEAVING);
			return;
		}
		final FinanceMain fm = (FinanceMain) item.getAttribute("data");
		long finID = fm.getFinID();
		String finRef = fm.getFinReference();

		// Set WorkFlow Details
		setWorkflowDetails(fm.getFinType(), StringUtils.isNotEmpty(fm.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

		if (StringUtils.isNotEmpty(rcdMaintainSts) && !moduleDefiner.equals(rcdMaintainSts)
				&& (FinServiceEvent.MANUALADVISE.equals(rcdMaintainSts) ? isValidateCancelManualAdvise(finID) : true)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return;
		}

		fm.setFinCategory(financeDetailService.getFinCategory(fm.getFinReference()));

		// Fee Waivers
		FeeWaiverHeader fwh = new FeeWaiverHeader();
		if (StringUtils.equals(fm.getRcdMaintainSts(), moduleDefiner)) {
			fwh.setFinID(finID);
			fwh.setFinReference(finRef);
			fwh = feeWaiverHeaderService.getFeeWaiverByFinRef(fwh);
		} else {
			// get fee waiver details from manual advise and finoddetails to prepare the list.
			fwh.setNewRecord(true);
			fwh.setFinID(finID);
			fwh.setFinReference(finRef);
			fwh = feeWaiverHeaderService.getFeeWaiverByFinRef(fwh);
		}

		String finReference = fwh.getFinReference();
		if (!fwh.isAlwtoProceed()) {
			MessageUtil.showMessage(Labels.getLabel("Recipt_Is_In_Process") + finReference);
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond1 = " where FinReference=?";

		if (!doCheckAuthority(fwh, whereCond1, new Object[] { finReference })) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return;
		}

		// FinanceDetails
		FinanceDetail financeDetail = financeDetailService.getFinanceDetailForCovenants(fm);

		fwh.setFinServiceInstructions(
				financeMaintenanceService.getFinServiceInstructions(fm.getFinID(), moduleDefiner));

		// Role Code State Checking
		String userRole = fwh.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		String nextroleCode = fwh.getNextRoleCode();
		String userLanguage = getUserWorkspace().getUserLanguage();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug(Literal.LEAVING);
			return;
		}

		String maintainSts = "";
		if (fwh != null) {
			maintainSts = StringUtils.trimToEmpty(fwh.getEvent());
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);
			MessageUtil.showError(errorDetails.getError());
		} else {
			if (isWorkFlowEnabled()) {
				String whereCond = " FinReference='" + finRef + "' AND version=" + fm.getVersion() + " ";

				long userId = getUserWorkspace().getLoggedInUser().getUserId();
				boolean userAcces = validateUserAccess(workFlowDetails.getId(), userId, workflowCode, whereCond,
						fm.getTaskId(), fm.getNextTaskId());
				if (userAcces) {
					showFeeWaiverHeaderView(fwh, financeDetail);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showFeeWaiverHeaderView(fwh, financeDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isValidateCancelManualAdvise(long finID) {
		List<ManualAdvise> list = manualAdviseService.getCancelledManualAdvise(finID);
		return list.stream().anyMatch(m -> m.getStatus() == null) ? true : false;
	}

	private void openLinkDelinkMaintenanceDialog(Listitem item) {

		logger.debug(Literal.ENTERING);

		if (item == null) {
			logger.debug(Literal.LEAVING);
			return;
		}
		final FinanceMain fm = (FinanceMain) item.getAttribute("data");
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Set WorkFlow Details
		setWorkflowDetails(fm.getFinType(), StringUtils.isNotEmpty(fm.getLovDescFinProduct()));
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		// FinMaintainInstruction
		String type = "";

		FinMaintainInstruction fmi = linkedFinancesService.getFinMaintainInstructionByFinRef(finID, moduleDefiner);

		if (fmi == null) {
			type = "_AView";
			fmi = new FinMaintainInstruction();
			fmi.setNewRecord(true);
		} else {
			type = "_TView";
		}

		fmi.setFinID(finID);
		fmi.setFinReference(finReference);
		fmi.setFinServiceInstructions(
				finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", moduleDefiner));

		FinanceDetail fd = new FinanceDetail();
		fd.getFinScheduleData().setFinanceMain(fm);

		String tempRef = fmi.getFinReference();
		List<LinkedFinances> list = linkedFinancesService.getLinkedFinancesByRef(tempRef, type);
		fd.setLinkedFinancesList(list);
		fd.getFinScheduleData().setFinMaintainInstruction(fmi);

		// Role Code State Checking
		String userRole = fmi.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		String nextroleCode = fmi.getNextRoleCode();
		String userLanguage = getUserWorkspace().getUserLanguage();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug(Literal.LEAVING);
			return;
		}

		String maintainSts = "";
		if (fmi != null) {
			maintainSts = StringUtils.trimToEmpty(fmi.getEvent());
		}

		if (StringUtils.isEmpty(fmi.getRecordStatus())) {
			fmi.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}

		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), userLanguage);

			MessageUtil.showError(errorDetails.getError());
		} else {
			String whereCond = " FinReference='" + finReference + "'";
			String recordStatus = fm.getRecordStatus();
			if (doCheckAuthority(fm, whereCond) || PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)) {
				showLinkDelinkMaintenanceView(fd);
			} else {
				MessageUtil.showError(Labels.getLabel("info.not_authorized"));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void showLinkDelinkMaintenanceView(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinMaintainInstruction fmi = financeDetail.getFinScheduleData().getFinMaintainInstruction();

		if (fmi.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fmi.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		map.put("finMaintainInstruction", fmi);
		map.put("financeSelectCtrl", this);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("financeDetail", financeDetail);
		map.put("roleCode", getRole());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/LinkedFinances/LinkedFinancesDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void showFinCovenantMaintanceView(FinMaintainInstruction finMaintainInstruction,
			FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		if (finMaintainInstruction.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			finMaintainInstruction.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		map.put("finMaintainInstruction", finMaintainInstruction);
		map.put("financeSelectCtrl", this);
		map.put("financeDetail", financeDetail);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("isEnquiry", false);
		map.put("roleCode", getRole());
		map.put("module", "Maintanance");
		map.put("financeSelectCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			String url = "/WEB-INF/pages/Finance/FinanceMain/FinCovenantMaintanceDialog.zul";
			if (ImplementationConstants.COVENANT_MODULE_NEW) {
				map.put("finHeaderList", getFinBasicDetails(financeDetail));
				url = "/WEB-INF/pages/Finance/Covenant/CovenantsList.zul";

			}
			Executions.createComponents(url, null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void showFinCollateralMaintanceView(FinMaintainInstruction fmi, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		if (fmi.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fmi.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		map.put("finMaintainInstruction", fmi);
		map.put("financeSelectCtrl", this);
		map.put("financeDetail", fd);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("isEnquiry", false);
		map.put("roleCode", getRole());
		map.put("module", "Maintanance");
		map.put("financeSelectCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			String url = "/WEB-INF/pages/CustomerMasters/CollateralDelink/CollateralDelinkDialog.zul";
			map.put("finHeaderList", getFinBasicDetails(fd));

			Executions.createComponents(url, null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void showFinOptionMaintanceView(FinMaintainInstruction fmi, FinanceDetail fd) {
		logger.debug("Entering");

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		fmi.setFinID(fm.getFinID());
		fmi.setFinReference(fm.getFinReference());

		if (fmi.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fmi.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		map.put("finMaintainInstruction", fmi);
		map.put("financeSelectCtrl", this);
		map.put("financeDetail", fd);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("isEnquiry", false);
		map.put("roleCode", getRole());
		map.put("module", "Maintanance");
		map.put("finHeaderList", getFinBasicDetails(fd));

		// call the ZUL-file with the parameters packed in a map
		try {
			String url = "/WEB-INF/pages/Finance/FinOption/FinOptionList.zul";
			Executions.createComponents(url, null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private List<Object> getFinBasicDetails(FinanceDetail financeDetail) {
		List<Object> arrayList = new ArrayList<>();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();

		arrayList.add(0, aFinanceMain.getFinType());
		arrayList.add(1, aFinanceMain.getFinCcy());
		arrayList.add(2, aFinanceMain.getScheduleMethod());
		arrayList.add(3, aFinanceMain.getFinReference());
		arrayList.add(4, aFinanceMain.getProfitDaysBasis());
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, customer == null ? "" : customer.getCustShrtName());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
	}

	private void showFeeWaiverHeaderView(FeeWaiverHeader feeWaiverHeader, FinanceDetail financeDetail) {
		logger.debug("Entering");

		if (feeWaiverHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			feeWaiverHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		map.put("feeWaiverHeader", feeWaiverHeader);
		map.put("financeSelectCtrl", this);
		map.put("financeDetail", financeDetail);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("isEnquiry", false);
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

		this.isDashboard = false;
		this.isDetailScreen = false;

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

	/**
	 * Method to check and set module definer value
	 * 
	 * @param tab (Tab)
	 */
	private void checkAndSetModDef(Tabbox tabbox) {
		logger.debug("Entering");
		filterList = new ArrayList<Filter>();

		if (tabbox != null) {
			tab = tabbox.getSelectedTab();
			if (tab != null) {
				if ("tab_BasicDetail".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.BASICMAINTAIN;
					eventCodeRef = AccountingEvent.AMENDMENT;
					workflowCode = FinServiceEvent.BASICMAINTAIN;
				} else if ("tab_RpyBasicDetail".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.RPYBASICMAINTAIN;
					eventCodeRef = AccountingEvent.SEGMENT;
					workflowCode = FinServiceEvent.RPYBASICMAINTAIN;
				} else if ("tab_AddRateChange".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.RATECHG;
					eventCodeRef = AccountingEvent.RATCHG;
					workflowCode = FinServiceEvent.RATECHG;
				} else if ("tab_InsChange".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.INSCHANGE;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.INSCHANGE;
				} else if ("tab_ChangeRepayment".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CHGRPY;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.CHGRPY;
				} else if ("tab_AddDisbursment".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.ADDDISB;
					eventCodeRef = AccountingEvent.ADDDBSN;
					workflowCode = FinServiceEvent.ADDDISB;
				} else if ("tab_RlsHoldDisbursment".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.RLSDISB;
					eventCodeRef = "";
					workflowCode = FinServiceEvent.RLSDISB;
				} else if ("tab_Postponement".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.POSTPONEMENT;
					eventCodeRef = AccountingEvent.DEFRPY;
					workflowCode = FinServiceEvent.POSTPONEMENT;
				} else if ("tab_UnPlannedEmi".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.UNPLANEMIH;
					eventCodeRef = AccountingEvent.EMIHOLIDAY;
					workflowCode = FinServiceEvent.UNPLANEMIH;
				} else if ("tab_AddTerms".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.ADDTERM;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.ADDTERM;
				} else if ("tab_RmvTerms".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.RMVTERM;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.RMVTERM;
				} else if ("tab_Recalculate".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.RECALCULATE;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.RECALCULATE;
				} else if ("tab_SubSchedule".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.SUBSCHD;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.SUBSCHD;
				} else if ("tab_ChangeProfit".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CHGPFT;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.CHGPFT;
				} else if ("tab_ChangeFrequency".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CHGFRQ;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.CHGFRQ;
				} else if ("tab_ReSchedule".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.RESCHD;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.RESCHD;
				} else if ("tab_ChangeGestation".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CHGGRCEND;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.CHGGRCEND;
				} else if ("tab_Receipts".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.RECEIPT;
					eventCodeRef = AccountingEvent.REPAY;
					setDialogCtrl("ReceiptDialogCtrl");
					workflowCode = FinServiceEvent.RECEIPT;
				} else if ("tab_PartialSettlement".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.EARLYRPY;
					eventCodeRef = AccountingEvent.EARLYPAY;
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode = FinServiceEvent.EARLYRPY;
				} else if ("tab_SchdRepayment".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.SCHDRPY;
					eventCodeRef = AccountingEvent.REPAY;
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode = FinServiceEvent.SCHDRPY;
				} else if ("tab_EarlySettlement".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.EARLYSETTLE;
					eventCodeRef = AccountingEvent.EARLYSTL;
					setDialogCtrl("ManualPaymentDialogCtrl");
					workflowCode = FinServiceEvent.EARLYSETTLE;
				} else if ("tab_EarlySettlementEnq".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.EARLYSTLENQ;
					setDialogCtrl("ManualPaymentDialogCtrl");
				} else if ("tab_WriteOff".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.WRITEOFF;
					setDialogCtrl("FinanceWriteoffDialogCtrl");
					workflowCode = FinServiceEvent.WRITEOFF;
					eventCodeRef = AccountingEvent.WRITEOFF;
					this.allowPreMaturedCases.setVisible(true);
					this.label_FinanceMainSelect_AllowPreMaturedCases.setVisible(true);
				} else if ("tab_WriteoffPayment".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.WRITEOFFPAY;
					eventCodeRef = AccountingEvent.WRITEBK;
					workflowCode = FinServiceEvent.WRITEOFFPAY;
					this.allowPreMaturedCases.setVisible(true);
					this.label_FinanceMainSelect_AllowPreMaturedCases.setVisible(true);
				} else if ("tab_CancelRepay".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CANCELRPY;
					setDialogCtrl("CancelRepayDialogCtrl");
					workflowCode = FinServiceEvent.CANCELRPY;
				} else if ("tab_CancelFinance".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CANCELFIN;
					eventCodeRef = AccountingEvent.CANCELFIN;
					setDialogCtrl("CancelFinanceDialogCtrl");
					workflowCode = FinServiceEvent.CANCELFIN;
					this.btnNew.setVisible(true);
				} else if ("tab_CancelDisbursement".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CANCELDISB;
					eventCodeRef = "";
					workflowCode = FinServiceEvent.CANCELDISB;
				} else if ("tab_OverdraftSchedule".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.OVERDRAFTSCHD;
					eventCodeRef = "";
					workflowCode = FinServiceEvent.OVERDRAFTSCHD;
				} else if ("tab_PlannedEMI".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.PLANNEDEMI;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.PLANNEDEMI;
				} else if ("tab_ReAgeHolidays".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.REAGING;
					eventCodeRef = AccountingEvent.REAGING;
					workflowCode = FinServiceEvent.REAGING;
				} else if ("tab_HoldEMI".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.HOLDEMI;
					eventCodeRef = AccountingEvent.HOLDEMI;
					workflowCode = FinServiceEvent.HOLDEMI;
				} else if ("tab_FinCovenants".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.COVENANTS;
					workflowCode = FinServiceEvent.COVENANTS;
				} else if ("tab_CollateralDelink".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.COLLATERAL;
					workflowCode = FinServiceEvent.COLLATERAL;
				} else if ("tab_FinOptions".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.FINOPTION;
					workflowCode = FinServiceEvent.FINOPTION;
				} else if ("tab_LinkingDelinking".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.LINKDELINK;
					workflowCode = FinServiceEvent.LINKDELINK;
				}

				else if ("tab_FeeWaivers".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.FEEWAIVERS;
					workflowCode = FinServiceEvent.FEEWAIVERS;
				} else if ("tab_ChangeSchdMethod".equals(tab.getId())) {
					moduleDefiner = FinServiceEvent.CHGSCHDMETHOD;
					eventCodeRef = AccountingEvent.SCDCHG;
					workflowCode = FinServiceEvent.CHGSCHDMETHOD;
				} else if ("tab_ChangeTDS".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.CHANGETDS;
					workflowCode = FinServiceEvent.CHANGETDS;
				} else if ("tab_LoanDownSizing".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.LOANDOWNSIZING;
					workflowCode = FinServiceEvent.LOANDOWNSIZING;
				} else if ("tab_Restructure".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.RESTRUCTURE;
					workflowCode = FinServiceEvent.RESTRUCTURE;
					eventCodeRef = AccountingEvent.RESTRUCTURE;
					this.btnNew
							.setVisible(getUserWorkspace().isAllowed("button_FinanceSelectList_NewRestructureDetail"));
				} else if ("tab_ExtendedFields".equals(tab.getId())) {
					eventCodeRef = "";
					moduleDefiner = FinServiceEvent.EXTENDEDFIELDS_MAINTAIN;
					workflowCode = FinServiceEvent.EXTENDEDFIELDS_MAINTAIN;
				} else if ("tab_PrincipleHoliday".equals(tab.getId())) {
					eventCodeRef = AccountingEvent.SCDCHG;
					moduleDefiner = FinServiceEvent.PRINH;
					workflowCode = FinServiceEvent.PRINH;
				}
				return;
			}
		} else {
			moduleDefiner = "";
			return;
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the FinanceMain dialog with a new empty entry.
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering " + event.toString());

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can call them with a object of the selected
		 * FinanceMain. For handed over these parameter only a Map is accepted. So we put the FinanceMain object in a
		 * HashMap.
		 */
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeSelectCtrl", this);
		map.put("tabbox", tab);
		map.put("moduleDefiner", moduleDefiner);
		map.put("workflowCode", workflowCode);
		map.put("eventCode", eventCodeRef);
		map.put("menuItemRightName", menuItemRightName);
		map.put("role", getUserWorkspace().getUserRoles());
		// call the ZUL-file with the parameters packed in a map
		try {
			if (FinServiceEvent.RESTRUCTURE.equals(moduleDefiner)) {
				doSearch(true);
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectRestructureDialog.zul", null,
						map);
			}
			if (FinServiceEvent.CANCELFIN.equals(moduleDefiner)) {
				doSearch(true);
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectFinanceCancellationDialog.zul",
						null, map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
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

		if (moduleDefiner.equals(FinServiceEvent.COVENANTS)) {
			this.searchObject.addTabelName("CovenantsMaintenance_View");
		} else if (moduleDefiner.equals(FinServiceEvent.FINOPTION)) {
			this.searchObject.addTabelName("FinoptionsMaintenance_View");
		} else if (moduleDefiner.equals(FinServiceEvent.COLLATERAL)) {
			this.searchObject.addTabelName("CollateralsMaintenance_view");
		} else if (moduleDefiner.equals(FinServiceEvent.LINKDELINK)) {
			this.searchObject.addTabelName("LinkedFinMaintenance_view");
		}

		else if (moduleDefiner.equals(FinServiceEvent.FEEWAIVERS)) {
			this.searchObject.addTabelName("FeeWaivers_View");
		} else if (moduleDefiner.equals(FinServiceEvent.EXTENDEDFIELDS_MAINTAIN)) {
			this.searchObject.addTabelName("Extended_Field_Mnt_View");
		} else {
			this.searchObject.addTabelName("FinanceMaintenance_View");
		}

		if (isDashboard) {
			this.searchObject.addFilterEqual("RcdMaintainSts", moduleDefiner);
		}

		buildedWhereCondition = "";

		if (StringUtils.isNotEmpty(workflowCode)) {

			if (App.DATABASE == Database.ORACLE) {
				buildedWhereCondition = " (NextRoleCode IS NULL ";
			} else {
				// for postgre db sometimes record type is null or empty('')
				buildedWhereCondition = " ( (NextRoleCode IS NULL or NextRoleCode = '') ";
			}
			buildedWhereCondition = buildedWhereCondition
					.concat(" AND FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ");
			buildedWhereCondition = buildedWhereCondition
					.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 ");
			buildedWhereCondition = buildedWhereCondition.concat(" WHERE WD.FinEvent = '");

			if (StringUtils.equals(workflowCode, FinServiceEvent.ADDFLEXIDISB)) {
				buildedWhereCondition = buildedWhereCondition.concat(workflowCode);
			} else {
				buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);
			}

			buildedWhereCondition = buildedWhereCondition
					.concat("' AND WF.FirstTaskOwner IN (SELECT RoleCd FROM UserOperationRoles_View WHERE ");

			buildedWhereCondition = buildedWhereCondition.concat(" UsrID = "
					+ getUserWorkspace().getUserDetails().getUserId() + " AND AppCode = '" + App.CODE + "')");

			buildedWhereCondition = buildedWhereCondition
					.concat(")) OR NextRoleCode IN (SELECT RoleCd FROM UserOperationRoles_View WHERE ");

			buildedWhereCondition = buildedWhereCondition.concat(" UsrID = "
					+ getUserWorkspace().getUserDetails().getUserId() + " AND AppCode = '" + App.CODE + "')");

			for (String role : usrfinRolesList) {
				if (buildedWhereCondition.length() > 0) {
					buildedWhereCondition = buildedWhereCondition.concat(" OR ");
				}

				buildedWhereCondition = buildedWhereCondition.concat("(',' ");

				buildedWhereCondition = buildedWhereCondition.concat(QueryUtil.getQueryConcat());
				buildedWhereCondition = buildedWhereCondition.concat(" nextRoleCode ");
				buildedWhereCondition = buildedWhereCondition.concat(QueryUtil.getQueryConcat());
				buildedWhereCondition = buildedWhereCondition.concat(" ',' LIKE '%,");
				buildedWhereCondition = buildedWhereCondition.concat(role);
				buildedWhereCondition = buildedWhereCondition.concat(",%')");
			}
		}

		Filter[] rcdTypeFilter = new Filter[2];
		rcdTypeFilter[0] = new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL);
		// rcdTypeFilter[1] = new Filter("RecordType", " ", Filter.OP_EQUAL);
		rcdTypeFilter[1] = Filter.isNull("RecordType");
		if (!moduleDefiner.equals(FinServiceEvent.COVENANTS) && !moduleDefiner.equals(FinServiceEvent.FEEWAIVERS)
				&& !moduleDefiner.equals(FinServiceEvent.FINOPTION) && !moduleDefiner.equals(FinServiceEvent.COLLATERAL)
				&& !moduleDefiner.equals(FinServiceEvent.LINKDELINK)) {
			this.searchObject.addFilterOr(rcdTypeFilter);
		}

		if (filterList != null && !filterList.isEmpty()) {
			for (Filter filter : filterList) {
				this.searchObject.addFilter(filter);
			}
		}

		return this.searchObject;
	}

	public ArrayList<String> getUserFinanceRoles(String[] moduleNames, String finEvent) {
		Set<String> finRoleSet = FinanceWorkflowRoleUtil.getFinanceRoles(moduleNames, finEvent);
		ArrayList<String> arrayRoleCode = new ArrayList<String>();

		Object[] roles = getUserWorkspace().getUserRoleSet().toArray();
		for (Object role : roles) {
			if (finRoleSet.contains(role.toString())) {
				arrayRoleCode.add(role.toString());
			}
		}

		return arrayRoleCode;
	}

	private void openFinChangeTDSMaintanceDialog(Listitem item) {
		logger.debug("Entering ");
		// get the selected FinanceMain object

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			long finID = aFinanceMain.getFinID();
			String finRef = aFinanceMain.getFinReference();

			// Validate Loan is INPROGRESS in any Other Servicing option or NOT ?

			String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

			// Check whether the user has authority to change/view the record.
			String whereCond1 = " where FinReference = ?";

			if (!doCheckAuthority(aFinanceMain, whereCond1, new Object[] { finRef })) {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return;
			}

			if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
				MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
				return;
			}

			// Set WorkFlow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			// FinMaintainInstruction
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			finMaintainInstruction.setFinID(finID);
			finMaintainInstruction.setFinReference(finRef);
			if (StringUtils.equals(aFinanceMain.getRcdMaintainSts(), moduleDefiner)) {
				finMaintainInstruction = finCovenantMaintanceService.getFinMaintainInstructionByFinRef(finID,
						moduleDefiner);
			} else {
				finMaintainInstruction.setNewRecord(true);
			}

			// Role Code State Checking
			String userRole = finMaintainInstruction.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			String nextroleCode = finMaintainInstruction.getNextRoleCode();
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());

				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
				logger.debug("Leaving");
				return;
			}

			String maintainSts = "";
			if (finMaintainInstruction != null) {
				maintainSts = StringUtils.trimToEmpty(finMaintainInstruction.getEvent());
			}

			if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {
				String whereCond = " where FinReference=?";
				if (isWorkFlowEnabled()) {

					if (doCheckAuthority(aFinanceMain, whereCond, new Object[] { finRef })
							|| StringUtils.equals(aFinanceMain.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
						showFinChangeTDSMaintanceView(finMaintainInstruction);
					} else {
						MessageUtil.showError(Labels.getLabel("info.not_authorized"));
					}
				} else {
					if (doCheckAuthority(aFinanceMain, whereCond, new Object[] { finRef })
							|| StringUtils.equals(aFinanceMain.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
						showFinChangeTDSMaintanceView(finMaintainInstruction);
					} else {
						MessageUtil.showError(Labels.getLabel("info.not_authorized"));
					}
				}
			}
		}
		logger.debug("Leaving ");
	}

	private void showFinChangeTDSMaintanceView(FinMaintainInstruction fmi) {

		logger.debug("Entering");

		if (fmi.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fmi.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		long finID = fmi.getFinID();
		FinanceMain financeMain = getChangeTDSService().getFinanceBasicDetailByRef(finID);
		FinanceDetail financeDetail = getFinanceDetailService().getFinSchdDetailById(finID, "_AView", false);
		boolean isTDSCheck = false;
		Date date = SysParamUtil.getAppDate();
		if (fmi.isNewRecord()) {
			isTDSCheck = changeTDSService.isTDSCheck(fmi.getFinReference(), date);
		} else {
			isTDSCheck = fmi.istDSApplicable();
		}

		map.put("finMaintainInstruction", fmi);
		map.put("financeSelectCtrl", this);
		map.put("financeMain", financeMain);
		map.put("TDSCheck", isTDSCheck);
		map.put("moduleCode", moduleDefiner);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("eventCode", eventCodeRef);
		map.put("isEnquiry", false);
		map.put("roleCode", fmi.getNextRoleCode());
		map.put("financeDetail", financeDetail);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ChangeTDSDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * 
	 * @param aFinanceDetail
	 */
	private void showLoanDownsizingView(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if (aFinanceMain.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		map.put("financeSelectCtrl", this);
		map.put("financeDetail", aFinanceDetail);
		map.put("moduleDefiner", moduleDefiner);
		map.put("workflowCode", workflowCode);
		map.put("eventCode", eventCodeRef);
		map.put("menuItemRightName", menuItemRightName);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/LoanDownSizingDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param item
	 */
	private void openLoanDownsizingDialog(Listitem item) {
		logger.debug("Entering ");

		if (item != null) {
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			// Validate Loan is INPROGRESS in any Other Servicing option or NOT ?
			long finID = aFinanceMain.getFinID();
			String finRef = aFinanceMain.getFinReference();
			String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

			if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, moduleDefiner)) {
				MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
				return;
			}

			// Set WorkFlow Details
			setWorkflowDetails(aFinanceMain.getFinType(), StringUtils.isNotEmpty(aFinanceMain.getLovDescFinProduct()));
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}

			// data retrieval
			final FinanceDetail financeDetail = loanDownSizingService.getDownSizingFinance(aFinanceMain,
					rcdMaintainSts);
			financeDetail.setModuleDefiner(moduleDefiner);

			// Role Code State Checking
			String userRole = aFinanceMain.getNextRoleCode();
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			String nextroleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
			if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {

				String[] errParm = new String[1];
				String[] valueParm = new String[1];

				valueParm[0] = finRef;
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + " : " + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());

				MessageUtil.showError(errorDetails.getError());
				Events.sendEvent(Events.ON_CLICK, this.btnClear, null);

				logger.debug("Leaving");
				return;
			}

			if (isWorkFlowEnabled()) {
				String whereCond = " AND FinReference = '" + finRef + "' AND version = " + aFinanceMain.getVersion()
						+ " ";

				boolean userAcces = validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoggedInUser().getUserId(), workflowCode, whereCond,
						aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());

				if (userAcces) {
					showLoanDownsizingView(financeDetail);
				} else {
					MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showLoanDownsizingView(financeDetail);
			}
		}

		logger.debug("Leaving ");
	}

	private void userAccessLog(String menu, Listitem item, String module) {
		final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
		logUserAccess(menu, aFinanceMain.getFinReference(), module);
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

	public ManualPaymentService getManualPaymentService() {
		return manualPaymentService;
	}

	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}

	public FinanceWriteoffService getFinanceWriteoffService() {
		return financeWriteoffService;
	}

	public void setFinanceWriteoffService(FinanceWriteoffService financeWriteoffService) {
		this.financeWriteoffService = financeWriteoffService;
	}

	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}

	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	public FinanceMaintenanceService getFinanceMaintenanceService() {
		return financeMaintenanceService;
	}

	public void setFinanceMaintenanceService(FinanceMaintenanceService financeMaintenanceService) {
		this.financeMaintenanceService = financeMaintenanceService;
	}

	public RepaymentCancellationService getRepaymentCancellationService() {
		return repaymentCancellationService;
	}

	public void setRepaymentCancellationService(RepaymentCancellationService repaymentCancellationService) {
		this.repaymentCancellationService = repaymentCancellationService;
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

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinCovenantMaintanceService getFinCovenantMaintanceService() {
		return finCovenantMaintanceService;
	}

	public void setFinCovenantMaintanceService(FinCovenantMaintanceService finCovenantMaintanceService) {
		this.finCovenantMaintanceService = finCovenantMaintanceService;
	}

	public FeeWaiverHeaderService getFeeWaiverHeaderService() {
		return feeWaiverHeaderService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

	public FinOptionMaintanceService getFinOptionMaintanceService() {
		return finOptionMaintanceService;
	}

	public void setFinOptionMaintanceService(FinOptionMaintanceService finOptionMaintanceService) {
		this.finOptionMaintanceService = finOptionMaintanceService;
	}

	public ChangeTDSService getChangeTDSService() {
		return changeTDSService;
	}

	public void setChangeTDSService(ChangeTDSService changeTDSService) {
		this.changeTDSService = changeTDSService;
	}

	public void setLoanDownSizingService(LoanDownSizingService loanDownSizingService) {
		this.loanDownSizingService = loanDownSizingService;
	}

	public void setFinOCRHeaderService(FinOCRHeaderService finOCRHeaderService) {
		this.finOCRHeaderService = finOCRHeaderService;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setLinkedFinancesService(LinkedFinancesService linkedFinancesService) {
		this.linkedFinancesService = linkedFinancesService;
	}

	public void setExtendedFieldMaintenanceService(ExtendedFieldMaintenanceService extendedFieldMaintenanceService) {
		this.extendedFieldMaintenanceService = extendedFieldMaintenanceService;
	}

	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	@Autowired
	public void setFinanceCancelValidator(FinanceCancelValidator financeCancelValidator) {
		this.financeCancelValidator = financeCancelValidator;
	}
}