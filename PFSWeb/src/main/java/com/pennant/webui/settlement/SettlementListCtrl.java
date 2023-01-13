package com.pennant.webui.settlement;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.settlement.FinSettlementHeader;
import com.pennant.backend.service.settlement.SettlementService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.settlement.model.SettlementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

import io.micrometer.core.instrument.util.StringUtils;

public class SettlementListCtrl extends GFCBaseListCtrl<FinSettlementHeader> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SettlementListCtrl.class);

	protected Window window_SettlementList;
	protected Borderlayout borderLayout_SettlementList;
	protected Paging pagingSettlementList;
	protected Listbox listBoxSettlement;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_SettlementType;
	protected Listheader listheader_SettlementStatus;

	// checkRights
	protected Button button_SettlementList_NewSettlement;
	protected Button button_SettlementList_SettlementSearchDialog;

	protected ExtendedCombobox finReference;
	protected ExtendedCombobox settlementType;
	protected Combobox settlementStatus;

	protected Listbox sortOperator_finReference;
	protected Listbox sortOperator_SettlementType;
	protected Listbox sortOperator_SettlementStatus;

	private transient SettlementService settlementService;
	private String module = "";

	/**
	 * default constructor.<br>
	 */
	public SettlementListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Settlement";
		super.pageRightName = "pagingSettlementList";
		super.tableName = "FIN_SETTLEMENT_HEADER_VIEW";
		super.queueTableName = "FIN_SETTLEMENT_HEADER_VIEW";
		super.enquiryTableName = "FIN_SETTLEMENT_HEADER_TVIEW";

		String module = getArgument("module");
		if (StringUtils.isNotBlank(module)) {
			this.module = module;
		}
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");

		this.searchObject.clearFilters();
		addRegisteredFilters();

		searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		if (FinanceConstants.SETTLEMENT_CANCEL.equals(this.module)) {
			searchObject.addWhereClause(
					"CancelReasonCode Is Not Null OR (CancelReasonCode Is Null AND RecordType Is Null)");
		} else {
			searchObject.addWhereClause("CancelReasonCode Is Null");
		}

		logger.debug("Leaving");
	}

	public void addRegisteredFilters() {
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
	}

	public void onCreate$window_SettlementList(Event event) {
		// Set the page level components.
		setPageComponents(window_SettlementList, borderLayout_SettlementList, listBoxSettlement, pagingSettlementList);
		setItemRender(new SettlementListModelItemRenderer());

		// Register buttons and fields.

		if (FinanceConstants.SETTLEMENT.equals(this.module)) {
			registerButton(button_SettlementList_NewSettlement, "button_SettlementList_NewSettlement", true);
		}

		registerButton(button_SettlementList_SettlementSearchDialog);

		registerField("ID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("settlementType", listheader_SettlementType, SortOrder.NONE, settlementType,
				sortOperator_SettlementType, Operators.STRING);
		fillComboBox(this.settlementStatus, "", PennantStaticListUtil.getEnquirySettlementStatus(), "");
		registerField("settlementStatus", listheader_SettlementStatus, SortOrder.NONE, settlementStatus,
				sortOperator_SettlementStatus, Operators.STRING);

		// set Field Properties
		doSetFieldProperties();

		// Render the page and display the data.
		doRenderPage();

		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		this.settlementType.setModuleName("SettlementTypeDetail");
		this.settlementType.setValueColumn("ID");
		this.settlementType.setDescColumn("SettlementCode");
		this.settlementType.setValidateColumns(new String[] { "ID" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_SettlementList_SettlementSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_SettlementList_NewSettlement(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinSettlementHeader settlement = new FinSettlementHeader();
		settlement.setNewRecord(true);
		settlement.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(settlement, true);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onSettlementItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSettlement.getSelectedItem();

		// Get the selected entity.
		final long id = (long) selectedItem.getAttribute("ID");
		FinSettlementHeader finSettlementHeader = settlementService.getsettlementById(id);

		if (finSettlementHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where ID = ?";

		if (doCheckAuthority(finSettlementHeader, whereCond, new Object[] { finSettlementHeader.getID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && finSettlementHeader.getWorkflowId() == 0) {
				finSettlementHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(finSettlementHeader, false);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param ea The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinSettlementHeader settlement, boolean fromNew) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("settlement", settlement);
		arg.put("SettlementListCtrl", this);
		arg.put("module", this.module);

		try {
			if (fromNew) {
				Executions.createComponents("/WEB-INF/pages/Settlement/SelectSettlementDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/Settlement/SettlementDialog.zul", null, arg);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public SettlementService getSettlementService() {
		return settlementService;
	}

	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

}
