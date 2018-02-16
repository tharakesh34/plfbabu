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
package com.pennanttech.framework.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class AbstractListController<T> extends AbstractController<T> {
	private static final long serialVersionUID = 6332080471910971732L;
	private final Logger logger = Logger.getLogger(getClass());
	
	private static final int SEARCH_ROW_SIZE = 27;
	private static final int LIST_ROW_SIZE = 26;
	private static final int PAGGING_SIZE = 27;
	
	protected Borderlayout contentArea;
	protected Grid searchGrid;
	protected Listbox listbox;
	protected Paging paging;

	// Workflow properties
	protected Listheader listheader_RecordStatus;
	protected Listbox sortOperator_RecordStatus;
	protected Textbox recordStatus;

	protected Listheader listheader_RecordType;
	protected Listbox sortOperator_RecordType;
	protected Listbox recordType;

	protected Row row_AlwWorkflow;
	protected Row workFlowFrom;
	protected Radio fromApproved;
	protected Radio fromWorkFlow;

	protected String tableName;
	protected String queueTableName;
	protected String enquiryTableName;

	protected JdbcSearchObject<T> searchObject;
	private ListitemRenderer<T> listitemRenderer;
	private Comparator<Object> comparator;
	
	

	protected List<SearchFilterControl> searchControls = new ArrayList<SearchFilterControl>();
	protected transient PagedListService pagedListService;

	protected AbstractListController() {
		super();
		
	}

	protected AbstractListController(Window window) {
		super();
		this.window = window;
	}

	protected void setPageComponents(Window window, Borderlayout contentArea, Listbox listbox, Paging paging) {
		setWindow(window);
		this.contentArea = contentArea;
		this.listbox = listbox;
		this.paging = paging;
	}
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		doLoadWorkflow();

		if (moduleCode != null) {
			this.searchObject = new JdbcSearchObject<T>();
			searchObject.setSearchClass(ModuleUtil.getModuleClass(moduleCode));
			if (isWorkFlowEnabled()) {
				searchObject.addTabelName(queueTableName);
			} else {
				searchObject.addTabelName(tableName);
			}
		}

		if ("Y".equalsIgnoreCase(getArgument("enqiryModule")) || "Yes".equalsIgnoreCase(getArgument("enqiryModule"))) {
			enqiryModule = true;
		}

		// Register default components
		registerButton(print);
		registerButton(help);
		
		// FIXME the below components should be removed in all the child classes.
		if (listheader_RecordStatus != null && listheader_RecordType != null) {
			if (recordStatus == null && recordType == null) {
				registerField("recordStatus", listheader_RecordStatus, SortOrder.NONE);
				registerField("recordType", listheader_RecordType, SortOrder.NONE);
			} else {
				registerField("recordStatus", listheader_RecordStatus, SortOrder.NONE, recordStatus,
						sortOperator_RecordStatus, Operators.STRING);
				registerField("recordType", listheader_RecordType, SortOrder.NONE, recordType, sortOperator_RecordType,
						Operators.SIMPLE_NUMARIC);
			}
		}
	}

	protected final void doRenderPage() {
		logger.debug("Entering");
		
		if (recordType != null) {
			this.recordType = setRecordType(this.recordType);
		}

		// Hide the workflow related fields if workflow is not configured.
		if (!isWorkFlowEnabled()) {
			if (row_AlwWorkflow != null) {
				row_AlwWorkflow.setVisible(false);
			}
			if (listheader_RecordStatus != null) {
				listheader_RecordStatus.setVisible(false);
			}
			if (listheader_RecordType != null) {
				listheader_RecordType.setVisible(false);
			}
		}

		if (enqiryModule && isWorkFlowEnabled()) {
			if (workFlowFrom != null) {
				workFlowFrom.setVisible(true);
			}
		} else {
			if (workFlowFrom != null) {
				workFlowFrom.setVisible(false);
			}
		}

		// Set the heights based on the client's desktop.
		contentArea.setHeight(getContentAreaHeight() + "px");
		
		if (listbox!=null) {
			listbox.setHeight((getListSize(false) - 32) + "px");
		}
		
		if (paging != null) {
			paging.setPageSize(getPageSize());
			paging.setDetailed(true);
		}

		// Set the components based on the rights.
		checkRights();

		logger.debug("Leaving");
	}

	// FIXME: Name to be changed to doSearch
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void search() {
		logger.debug("Entering");

		// Set the first page as the active page.
		if (paging != null) {
			this.paging.setActivePage(0);
		}

		if (enqiryModule) {
			if (fromApproved != null && fromWorkFlow != null) {
				if (fromApproved.isChecked()) {
					this.searchObject.addTabelName(tableName);
				} else if (fromWorkFlow.isChecked()) {
					this.searchObject.addTabelName(enquiryTableName);
				} else {
					this.searchObject.addTabelName(tableName);
				}
			}
		}

		doAddFilters();

		this.listbox.setItemRenderer(listitemRenderer);

		if (comparator == null) {
			getPagedListWrapper().setPagedListService(pagedListService);
			getPagedListWrapper().init(this.searchObject, this.listbox, this.paging); // FIXME when paging is null
		} else {
			final SearchResult<?> searchResult = pagedListService.getSRBySearchObject(this.searchObject);
			listbox.setModel(new GroupsModelArray(searchResult.getResult().toArray(), comparator));
		}

		logger.debug("Leaving");
	}

	protected void doReset() {
		logger.debug("Entering");

		for (SearchFilterControl searchControl : searchControls) {
			searchControl.resetFilters();
		}

		logger.debug("Leaving");
	}

	protected void doPrintResults() {
		logger.debug("Entering");

		if (this.paging != null) {
			try {
				new PTListReportUtils(moduleCode, searchObject, this.paging.getTotalSize() + 1);
			} catch (InterruptedException e) {
				logger.error("Exception:", e);
			}
		} else {
			try {
				new PTListReportUtils(moduleCode, searchObject, listbox.getItemCount());
			} catch (InterruptedException e) {
				logger.error("Exception:", e);
			}
		}

		logger.debug("Leaving");
	}

	protected void doShowHelp(Event event) {
		MessageUtil.showHelpWindow(event, window);
	}

	/**
	 * Adds the search filters registered through <code>registerField</code> to the <code>searchObject</code>.
	 * <p>
	 * Additional filters can be added by overriding this method and adding the additional filters to the
	 * <code>searchObject</code>. A subclass that override this method should call <code>super.doAddFilters()</code> or
	 * the default search filters registered through <code>registerField</code> will not work.
	 * </p>
	 */
	protected void doAddFilters() {
		logger.debug("Entering");

		this.searchObject.clearFilters();

		if (isWorkFlowEnabled() && !enqiryModule) {
			this.searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				if (App.DATABASE == Database.ORACLE && "recordType".equals(filter.getProperty())
						&& Filter.OP_NOT_EQUAL == filter.getOperator()) {
					Filter[] filters = new Filter[2];
					filters[0] = Filter.isNull(filter.getProperty());
					filters[1] = filter;

					this.searchObject.addFilterOr(filters);
				} else {
					this.searchObject.addFilter(filter);
				}
			}
		}

		logger.debug("Leaving");
	}

	protected boolean doCheckAuthority(T entity, String whereCond) {
		return true;
	}

	/**
	 * <p>
	 * Get the process flow type used for the module to which the specified name of the module is registered.
	 * </p>
	 * <p>
	 * If the process flow type is configured, load the process flow details to which the specified process flow
	 * configured.
	 * </p>
	 * <p>
	 * If process flow is available.
	 * <ol>
	 * <li>Module will be consider as process flow enabled and set <code>this.workFlowEnabled = true</code></li>
	 * <li>If the user contains the role which is is configured in process flow as first task owner then the user will
	 * be consider as first task owner and set <code>this.firstTask = true</code></li>
	 * <li>Set the process flow id <code>this.firstTask = true</code></li>
	 * </ol>
	 * </p>
	 * 
	 * @throws IllegalAccessError
	 *             - If the specified name of the module is not registered.
	 * @throws Exception
	 *             - If process flow details are not available to which the specified process flow configured.
	 */
	private void doLoadWorkflow() {
		logger.debug("Entering");
		if (moduleCode == null) {
			return;
		}

		// Get the workflow type configured for the module.
		String workflowType = ModuleUtil.getWorkflowType(moduleCode);
		if (workflowType == null) {
			return;
		}

		// Get the active workflow available for the specified type.
		WorkFlowDetails workflow = WorkFlowUtil.getDetailsByType(workflowType);
		if (workflow == null) {
			((Tab) getTabbox().getFirstChild().getLastChild()).close();
			MessageUtil.showError(Labels.getLabel("error.unhandled"));
			return;
		}

		// Set the active workflow attributes.
		setWorkFlowEnabled(true);

		setWorkFlowId(workflow.getId());

		if (workflow.getFirstTaskOwner().contains(",")) {
			String[] firstTaskOwners = workflow.getFirstTaskOwner().split(",");

			for (String firstTaskOwner : firstTaskOwners) {
				if (getUserWorkspace().isRoleContains(firstTaskOwner)) {
					setFirstTask(true);

					break;
				}
			}
		} else {
			setFirstTask(getUserWorkspace().isRoleContains(workflow.getFirstTaskOwner()));
		}
		logger.debug("Leaving");
	}

	protected void doLoadWorkflow(String financeType, String moduleDefiner) throws Exception {
		logger.debug("Entering");

		WorkFlowDetails workflow = null;

		if (StringUtils.isNotEmpty(moduleDefiner)) {
			FinanceWorkFlow financeWorkflow = this.financeWorkFlowService.getApprovedFinanceWorkFlowById(financeType,
					moduleDefiner, FinanceConstants.MODULE_NAME);
			if (financeWorkflow != null && financeWorkflow.getWorkFlowType() != null) {
				workflow = WorkFlowUtil.getDetailsByType(financeWorkflow.getWorkFlowType());
			}
		}

		if (workflow == null) {
			throw new Exception(Labels.getLabel("error.finWorkflow.notAssigned", new String[] { financeType }));
		}

		// Set the active workflow attributes.
		setWorkFlowEnabled(true);
		setWorkFlowId(workflow.getId());
		setFirstTask(getUserWorkspace().isRoleContains(workflow.getFirstTaskOwner()));
		setFirstTaskRole(workflow.getFirstTaskOwner());

		logger.debug("Leaving");
	}

	private Tabbox getTabbox() {
		return (Tabbox) borderlayoutMain.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
	}

	/**
	 * <p>
	 * This Filed will be added to the JdbcSearchObject.
	 * </p>
	 * 
	 * @param fieldName
	 *            Name of the property that needs to be render to the bean property of the provided
	 *            <code>ListitemRenderer</code>.
	 * @throws IllegalArgumentException
	 *             If the specified fieldName <code>fieldName></code> is null or empty.
	 */
	protected void registerField(String fieldName) {
		if (StringUtils.trimToNull(fieldName) == null) {
			throw new IllegalArgumentException();
		}
		searchObject.addField(fieldName);
	}

	/**
	 * <p>
	 * This Field will be added to the JdbcSearchObject
	 * </p>
	 * 
	 * @param fieldName
	 *            Name of the property that needs to be render to the bean pro-perty of the provided
	 *            <code>ListitemRenderer</code>.
	 * 
	 * @param listheader
	 *            Id of the Listheader to which sets the ascending/descending sorter.
	 * @throws IllegalArgumentException
	 *             If the specified <code>fieldName</code> is null or empty.
	 * @throws IllegalArgumentException
	 *             If the specified <code>listheader</code> is null.
	 */
	protected void registerField(String fieldName, Listheader listheader) {
		registerField(fieldName);

		if (listheader == null) {
			throw new IllegalArgumentException();
		}

		listheader.setSortAscending(new FieldComparator(fieldName, true));
		listheader.setSortDescending(new FieldComparator(fieldName, false));
	}

	/**
	 * <p>
	 * This Field will be added to the JdbcSearchObject and add sort by property.
	 * </p>
	 * 
	 * @param fieldName
	 *            Name of the property that needs to be render to the bean property of the provided
	 *            <code>ListitemRenderer</code>.
	 * @param desc
	 *            Sorting order for the property <code>fieldName</code>. Ascending if desc == false, descending if desc
	 *            == true.
	 * @param listheader
	 *            Id of the Listheader to which sets the ascending/descending sorter.
	 * @throws IllegalArgumentException
	 *             If the specified <code>fieldName</code> is null or empty.
	 * @throws IllegalArgumentException
	 *             If the specified <code>listheader</code> is null.
	 */
	protected void registerField(String fieldName, Listheader listheader, SortOrder defaultOrder) {
		registerField(fieldName);
		if (defaultOrder != SortOrder.NONE) {
			searchObject.addSort(fieldName, defaultOrder == SortOrder.DESC ? true : false);
		}

		if (listheader == null) {
			throw new IllegalArgumentException();
		}

		listheader.setSortAscending(new FieldComparator(fieldName, true));
		listheader.setSortDescending(new FieldComparator(fieldName, false));
	}

	/**
	 * @param fieldName
	 *            Name of the property that needs to be render to the bean property of the provided
	 *            <code>ListitemRenderer</code>.
	 * @param desc
	 *            Sorting order for the property <code>fieldName</code>. Ascending if desc == false, descending if desc
	 *            == true.
	 * @param listheader
	 *            Id of the Listheader to which sets the ascending/descending sorter.
	 * @param searchField
	 *            Id of search field to allow the search for the filed <code>fieldName</code> registered.
	 * @throws IllegalArgumentException
	 *             If the specified <code>fieldName</code> is null or empty.
	 * @throws IllegalArgumentException
	 *             If the specified <code>listheader</code> is null.
	 * @throws IllegalArgumentException
	 *             If the specified <code>searchField</code> is null.
	 */
	protected void registerField(String fieldName, Listheader listheader, SortOrder defaultOrder,
			Component searchField, Listbox searchOperator, Operators operators) {
		registerField(fieldName, listheader, defaultOrder);
		addSearchControll(fieldName, searchField, searchOperator, operators);
	}

	protected void registerField(String fieldName, Component searchField, SortOrder defaultOrder, Listbox searchOperator, Operators operators) {
		registerField(fieldName);
		addSearchControll(fieldName, searchField, searchOperator, operators);
	}

	protected void registerField(String fieldName, SortOrder defaultOrder) {
		registerField(fieldName);
		if (defaultOrder != SortOrder.NONE) {
			searchObject.addSort(fieldName, defaultOrder == SortOrder.DESC ? true : false);
		}
	}

	private void addSearchControll(String fieldName, Component searchField, Listbox searchOperator, Operators operators) {
		if (fieldName == null || searchField == null || searchOperator == null || operators == null) {
			throw new IllegalArgumentException();
		}
		searchControls.add(new SearchFilterControl(fieldName, searchField, searchOperator, operators));
	}
	
	private int getListSize(boolean hasPaging) {
		int gridRowCount = 0;

		Component component = window.getFellowIfAny("searchGrid");
		if (component != null) {
			gridRowCount = ((Grid) component).getRows().getVisibleItemCount();
		}

		return getContentAreaHeight() - (gridRowCount * SEARCH_ROW_SIZE) - (hasPaging ? PAGGING_SIZE : 0);
	}

	private int getPageSize() {
		int gridRowCount = 0;

		Component component = window.getFellowIfAny("searchGrid");
		if (component != null) {
			gridRowCount = ((Grid) component).getRows().getVisibleItemCount();
		}

		int height = getContentAreaHeight() - (gridRowCount * SEARCH_ROW_SIZE) - (LIST_ROW_SIZE) - (PAGGING_SIZE);
		return height / LIST_ROW_SIZE;
	}
	
	protected Map<String, Object> getDefaultArguments() {
		HashMap<String, Object> aruments = new HashMap<String, Object>();
		
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);
		
		return aruments;
	}
	
	

	public void setItemRender(ListitemRenderer<T> listitemRenderer) {
		this.listitemRenderer = listitemRenderer;
	}

	public void setComparator(Comparator<Object> comparator) {
		this.comparator = comparator;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public JdbcSearchObject<T> getSearchObject() {
		return searchObject;
	}
	
	public String getModuleCode() {
		return moduleCode;
	}
}
