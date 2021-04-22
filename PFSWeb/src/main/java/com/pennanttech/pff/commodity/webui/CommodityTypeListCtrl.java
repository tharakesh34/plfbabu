package com.pennanttech.pff.commodity.webui;

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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.component.Uppercasebox;
import com.pennant.webui.applicationmaster.covenant.CovenantTypeListCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.commodity.service.CommodityTypeService;
import com.pennanttech.pff.staticlist.AppStaticList;

public class CommodityTypeListCtrl extends GFCBaseListCtrl<CommodityType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CovenantTypeListCtrl.class);

	protected Window window_commodityType;
	protected Borderlayout borderLayout_commodityType;
	protected Paging pagingCommodityTypeList;
	protected Listbox listBoxCommodityType;

	// List headers
	protected Listheader listheader_CommodityType;
	protected Listheader listheader_UnitType;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_CommodityTypeList_NewCommodityType;
	protected Button button_CommodityTypeList_CommodityTypeSearch;

	// Search Fields
	protected Uppercasebox commodityType;
	protected Combobox unitType;
	protected Textbox active;

	protected Listbox sortOperator_CommodityType;
	protected Listbox sortOperator_UnitType;
	protected Listbox sortOperator_Active;

	private transient CommodityTypeService commodityTypeService;

	/**
	 * default constructor.<br>
	 */
	public CommodityTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CommodityType";
		super.pageRightName = "CommodityTypeList";
		super.tableName = "Commodity_Types_AView";
		super.queueTableName = "Commodity_Types_View";
		super.enquiryTableName = "Commodity_Types_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_commodityType(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_commodityType, borderLayout_commodityType, listBoxCommodityType,
				pagingCommodityTypeList);
		setItemRender(new CommodityTypeListModelItemRenderer());
		fillList(unitType, AppStaticList.getCommodityUnitTypes(), null);
		registerButton(button_CommodityTypeList_CommodityTypeSearch);
		registerButton(button_CommodityTypeList_NewCommodityType, "button_CommodityTypeList_NewCommodityType", true);
		registerField("id");
		registerField("Code", listheader_CommodityType, SortOrder.NONE, commodityType, sortOperator_CommodityType,
				Operators.STRING);
		registerField("UnitType", listheader_UnitType, SortOrder.ASC, unitType, sortOperator_UnitType,
				Operators.STRING);
		registerField("Active", listheader_Active, SortOrder.ASC, active, sortOperator_Active, Operators.STRING);
		doRenderPage();
		search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CommodityTypeList_CommodityTypeSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CommodityTypeList_NewCommodityType(Event event) {
		logger.debug(Literal.ENTERING);

		CommodityType commodityType = new CommodityType();
		commodityType.setNewRecord(true);
		commodityType.setWorkflowId(getWorkFlowId());
		doShowDialogPage(commodityType);

		logger.debug(Literal.LEAVING);
	}

	public void onCommodityTypeItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final long id = (long) this.listBoxCommodityType.getSelectedItem().getAttribute("id");
		CommodityType commodityType = commodityTypeService.getCommodityType(id);

		if (commodityType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  where  Id =?");

		if (doCheckAuthority(commodityType, whereCond.toString(), new Object[] { commodityType.getCode() })) {
			if (isWorkFlowEnabled() && commodityType.getWorkflowId() == 0) {
				commodityType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(commodityType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param covenanttype
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CommodityType commodityType) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commodityType", commodityType);
		arg.put("commodityTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Commodity/CommodityTypeDialogue.zul", null,
					arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		doPrintResults();
	}

	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void onCheck$fromApproved(Event event) {
		search();
	}

	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setCommodityTypeService(CommodityTypeService commodityTypeService) {
		this.commodityTypeService = commodityTypeService;
	}

}
