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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.pff.settlement.model.SettlementTypeDetail;
import com.pennant.pff.settlement.service.SettlementTypeDetailService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SettlementTypeDetailListCtrl extends GFCBaseListCtrl<SettlementTypeDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SettlementTypeDetailListCtrl.class);

	protected Window windowSettlementType;
	protected Borderlayout blSettlementType;
	protected Paging settlementTypePaging;
	protected Listbox settlementTypeListBox;
	protected Listheader lhCode;
	protected Listheader lhDescription;
	protected Button btnNew;
	protected Button btnSearch;
	protected Textbox code;
	protected Textbox description;
	protected Listbox soCode;
	protected Listbox soDescription;

	private transient SettlementTypeDetailService settlementTypeDetailService;

	public SettlementTypeDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SettlementTypeDetail";
		super.pageRightName = "SettlementTypeDetailList";
		super.tableName = "Settlement_Types_AView";
		super.queueTableName = "Settlement_Types_View";
		super.enquiryTableName = "Settlement_Types_View";
	}

	public void onCreate$windowSettlementType(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowSettlementType, blSettlementType, settlementTypeListBox, settlementTypePaging);
		setItemRender(new SettlementTypeDetailListListModelItemRenderer());

		registerButton(btnSearch);
		registerButton(btnNew, "button_SettlementTypeDetailList_NewSettlementTypeDetail", true);

		registerField("ID");
		registerField("settlementCode", lhCode, SortOrder.NONE, code, soCode, Operators.STRING);
		registerField("settlementDesc", lhDescription, SortOrder.NONE, description, soDescription, Operators.STRING);
		registerField("active");

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

		SettlementTypeDetail std = new SettlementTypeDetail();
		std.setNewRecord(true);
		std.setWorkflowId(getWorkFlowId());

		doShowDialogPage(std);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Listitem selectedItem = this.settlementTypeListBox.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		final long settlementTypeID = (long) selectedItem.getAttribute("id");
		SettlementTypeDetail std = settlementTypeDetailService.getSettlementById(settlementTypeID);

		if (std == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = " where id = ?";
		if (doCheckAuthority(std, whereCond, new Object[] { std.getSettlementCode() })) {
			if (isWorkFlowEnabled() && std.getWorkflowId() == 0) {
				std.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(std);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$print(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doPrintResults();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$help(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doShowHelp(event);
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doShowDialogPage(SettlementTypeDetail std) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("settlementTypeDetail", std);
		arg.put("settlementTypeDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Settlement/SettlementTypeDetailDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setSettlementTypeDetailService(SettlementTypeDetailService settlementTypeDetailService) {
		this.settlementTypeDetailService = settlementTypeDetailService;
	}

}
