package com.pennant.pff.settlement.web;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.service.SettlementService;
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

	protected Window windowSettlementList;
	protected Borderlayout blSettlementList;
	protected Paging pagingSettlementList;
	protected Listbox listBoxSettlement;
	protected Listheader lhFinReference;
	protected Listheader lhType;
	protected Listheader lhStatus;
	protected Button btnNew;
	protected Button btnSearch;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox type;
	protected Combobox status;
	protected Listbox sofinReference;
	protected Listbox soType;
	protected Listbox soStatus;

	private String module = "";

	private transient SettlementService settlementService;

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
		logger.debug(Literal.ENTERING);

		this.searchObject.clearFilters();
		addRegisteredFilters();

		searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		if (FinanceConstants.SETTLEMENT_CANCEL.equals(this.module)) {
			searchObject.addWhereClause(
					"CancelReasonCode Is Not Null OR (CancelReasonCode Is Null AND RecordType Is Null)");
		} else {
			searchObject.addWhereClause("CancelReasonCode Is Null");
		}

		logger.debug(Literal.LEAVING);
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

	public void onCreate$windowSettlementList(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowSettlementList, blSettlementList, listBoxSettlement, pagingSettlementList);
		setItemRender(new SettlementListModelItemRenderer());

		if (FinanceConstants.SETTLEMENT.equals(this.module)) {
			registerButton(btnNew, "button_SettlementList_NewSettlement", true);
		}

		registerButton(btnSearch);

		registerField("ID");
		registerField("finReference", lhFinReference, SortOrder.NONE, finReference, sofinReference, Operators.STRING);
		registerField("settlementType", lhType, SortOrder.NONE, type, soType, Operators.STRING);
		fillComboBox(this.status, "", PennantStaticListUtil.getEnquirySettlementStatus(), "");
		registerField("settlementStatus", lhStatus, SortOrder.NONE, status, soStatus, Operators.STRING);

		doSetFieldProperties();

		doRenderPage();

		search();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		search();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doReset();
		search();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnNew(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		FinSettlementHeader settlement = new FinSettlementHeader();
		settlement.setNewRecord(true);
		settlement.setWorkflowId(getWorkFlowId());

		doShowDialogPage(settlement, true);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onSettlementItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Listitem selectedItem = this.listBoxSettlement.getSelectedItem();

		final long id = (long) selectedItem.getAttribute("ID");
		FinSettlementHeader finSettlementHeader = settlementService.getsettlementById(id);

		if (finSettlementHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = " Where ID = ?";

		if (doCheckAuthority(finSettlementHeader, whereCond, new Object[] { finSettlementHeader.getId() })) {
			if (isWorkFlowEnabled() && finSettlementHeader.getWorkflowId() == 0) {
				finSettlementHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(finSettlementHeader, false);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doShowDialogPage(FinSettlementHeader settlement, boolean fromNew) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		this.type.setModuleName("SettlementTypeDetail");
		this.type.setValueColumn("ID");
		this.type.setDescColumn("SettlementCode");
		this.type.setValidateColumns(new String[] { "SettlementCode" });

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

}
