package com.pennanttech.pff.commodity.webui;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.webui.applicationmaster.covenant.CovenantTypeListCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.service.CommoditiesService;

public class CommoditiesListCtrl extends GFCBaseListCtrl<Commodity> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CovenantTypeListCtrl.class);

	protected Window window_commodities;
	protected Borderlayout borderLayout_commodities;
	protected Paging pagingCommoditiesList;
	protected Listbox listBoxCommodities;

	// List headers
	protected Listheader listheader_CommodityType;
	protected Listheader listheader_Code;
	protected Listheader listheader_HSNCode;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_CommodityTypeList_NewCommodityType;
	protected Button button_CommodityTypeList_CommodityTypeSearch;

	// Search Fields
	protected Textbox commodityCode;
	protected Textbox hsnCode;
	protected Textbox active;

	protected Listbox sortOperator_CommodityType;
	protected Listbox sortOperator_CommodityCode;
	protected Listbox sortOperator_HSNCode;
	protected Listbox sortOperator_Active;

	private transient CommoditiesService commoditiesService;

	/**
	 * default constructor.<br>
	 */
	public CommoditiesListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Commodity";
		super.pageRightName = "CommoditiesList";
		super.tableName = "COMMODITIES_AView";
		super.queueTableName = "COMMODITIES_View";
		super.enquiryTableName = "COMMODITIES_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_commodities(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_commodities, borderLayout_commodities, listBoxCommodities, pagingCommoditiesList);
		setItemRender(new CommoditiesListModelItemRenderer());
		// Register buttons and fields.
		registerButton(button_CommodityTypeList_CommodityTypeSearch);
		registerButton(button_CommodityTypeList_NewCommodityType, "button_CommodityTypeList_NewCommodityType", true);
		registerField("Id");
		registerField("CommodityTypeCode");
		registerField("CommodityType");
		registerField("Code", listheader_Code, SortOrder.ASC, commodityCode, sortOperator_CommodityCode,
				Operators.STRING);
		registerField("HSNCode", listheader_HSNCode, SortOrder.ASC, hsnCode, sortOperator_HSNCode, Operators.STRING);
		registerField("Active", listheader_Active, SortOrder.ASC, active, sortOperator_Active, Operators.DEFAULT);
		doSetFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
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

		Commodity commodity = new Commodity();
		commodity.setNewRecord(true);
		commodity.setWorkflowId(getWorkFlowId());
		doShowDialogPage(commodity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
	}

	public void onStockCompanyItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxCommodities.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		Commodity commodity = commoditiesService.getCommodities(id);

		if (commodity == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(commodity, whereCond.toString(), new Object[] { commodity.getCommodityType() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && commodity.getWorkflowId() == 0) {
				commodity.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(commodity);
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
	private void doShowDialogPage(Commodity commodity) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commodity", commodity);
		arg.put("commoditiesListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Commodity/CommoditiesDialogue.zul", null,
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

	public void setCommoditiesService(CommoditiesService commoditiesService) {
		this.commoditiesService = commoditiesService;
	}

}
