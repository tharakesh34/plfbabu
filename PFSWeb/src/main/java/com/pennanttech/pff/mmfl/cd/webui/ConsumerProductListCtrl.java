package com.pennanttech.pff.mmfl.cd.webui;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cd.model.ConsumerProduct;
import com.pennanttech.pff.cd.service.ConsumerProductService;

public class ConsumerProductListCtrl extends GFCBaseListCtrl<ConsumerProduct> {
	private static final long serialVersionUID = 1L;

	protected Window window_product;
	protected Borderlayout borderLayout_product;
	protected Paging pagingProductList;
	protected Listbox listBoxProduct;

	// List headers
	protected Listheader listheader_ModelId;
	protected Listheader listheader_AssetDescription;
	protected Listheader listheader_ModelStatus;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_ProductList_NewProduct;
	protected Button button_ProductList_ProductListSearch;

	// Search Fields
	protected Uppercasebox modelId;
	protected Textbox assetDescription;
	protected CurrencyBox minimumAmount;
	protected CurrencyBox maximumAmount;
	protected Textbox modelStatus;
	protected Textbox active;

	protected Listbox sortOperator_ModelId;
	protected Listbox sortOperator_AssetDescription;
	protected Listbox sortOperator_ModelStatus;
	protected Listbox sortOperator_Active;

	private transient ConsumerProductService consumerProductService;

	/**
	 * default constructor.<br>
	 */
	public ConsumerProductListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ConsumerProduct";
		super.pageRightName = "ProductList";
		super.tableName = "CD_Products_AView";
		super.queueTableName = "CD_Products_View";
		super.enquiryTableName = "CD_Products_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_product(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_product, borderLayout_product, listBoxProduct, pagingProductList);
		setItemRender(new ConsumerProductListModelRenderer());
		registerButton(button_ProductList_ProductListSearch);
		registerButton(button_ProductList_NewProduct, "button_ProductList_NewProduct", true);
		registerField("ProductId");
		registerField("ModelId", listheader_ModelId, SortOrder.NONE, modelId, sortOperator_ModelId, Operators.STRING);
		registerField("AssetDescription", listheader_AssetDescription, SortOrder.ASC, assetDescription,
				sortOperator_AssetDescription, Operators.STRING);
		registerField("ModelDescription");
		registerField("ModelStatus", listheader_ModelStatus, SortOrder.ASC, modelStatus, sortOperator_ModelStatus,
				Operators.STRING);
		registerField("Active", listheader_Active, SortOrder.ASC, active, sortOperator_Active, Operators.STRING);
		doRenderPage();
		search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ProductList_ProductListSearch(Event event) {
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
	public void onClick$button_ProductList_NewProduct(Event event) {
		logger.debug(Literal.ENTERING);

		ConsumerProduct consumerProduct = new ConsumerProduct();
		consumerProduct.setNewRecord(true);
		consumerProduct.setWorkflowId(getWorkFlowId());
		doShowDialogPage(consumerProduct);

		logger.debug(Literal.LEAVING);
	}

	public void onConsumerProductListItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final long id = (long) this.listBoxProduct.getSelectedItem().getAttribute("ProductId");
		ConsumerProduct consumerProduct = consumerProductService.getConsumerProduct(id);

		if (consumerProduct == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(consumerProduct, whereCond.toString(), new Object[] { consumerProduct.getProductId() })) {
			if (isWorkFlowEnabled() && consumerProduct.getWorkflowId() == 0) {
				consumerProduct.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(consumerProduct);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param Consumer Product The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ConsumerProduct consumerProduct) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("ConsumerProduct", consumerProduct);
		arg.put("consumerProductListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BussinessMasters/ConsumerProductDialogue.zul",
					null, arg);
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

	public void setConsumerProductService(ConsumerProductService consumerProductService) {
		this.consumerProductService = consumerProductService;
	}

}
