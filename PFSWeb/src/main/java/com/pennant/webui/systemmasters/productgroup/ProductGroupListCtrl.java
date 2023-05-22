package com.pennant.webui.systemmasters.productgroup;

import java.util.Map;

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

import com.pennant.backend.model.systemmasters.ProductGroup;
import com.pennant.backend.service.systemmasters.ProductGroupService;
import com.pennant.webui.systemmasters.productgroup.model.ProductGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ProductGroupListCtrl extends GFCBaseListCtrl<ProductGroup> {
	private static final long serialVersionUID = 1L;

	protected Window window_ProductGroup;
	protected Borderlayout borderLayout_ProductGroup;
	protected Paging pagingProductGroupList;
	protected Listbox listBoxProductGroup;

	// List headers
	protected Listheader listheader_ModelCode;
	protected Listheader listheader_ProductCategory;
	protected Listheader listheader_Channel;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_ProductGroupList_NewProductGroup;
	protected Button button_ProductGroupList_ProductGroupListSearch;

	// Search Fields
	protected Textbox modelId;
	protected Textbox productCategoryId;
	protected Textbox channel;
	protected Textbox active;

	protected Listbox sortOperator_ModelCode;
	protected Listbox sortOperator_ProductCategory;
	protected Listbox sortOperator_Channel;
	protected Listbox sortOperator_Active;

	private transient ProductGroupService productGroupService;

	/**
	 * default constructor.<br>
	 */
	public ProductGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ProductGroup";
		super.pageRightName = "ProductGroupList";
		super.tableName = "ProductGroup_AVIEW";
		super.queueTableName = "ProductGroup_VIEW";
		super.enquiryTableName = "ProductGroup_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ProductGroup(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_ProductGroup, borderLayout_ProductGroup, listBoxProductGroup, pagingProductGroupList);
		setItemRender(new ProductGroupListModelItemRenderer());
		registerButton(button_ProductGroupList_ProductGroupListSearch);
		registerButton(button_ProductGroupList_NewProductGroup, "button_ProductGroupList_NewProductGroup", true);
		registerField("ProductGroupId");
		registerField("modelId", listheader_ModelCode, SortOrder.NONE, modelId, sortOperator_ModelCode,
				Operators.STRING);
		registerField("productCategoryId", listheader_ProductCategory, SortOrder.ASC, productCategoryId,
				sortOperator_ProductCategory, Operators.STRING);
		registerField("Active", listheader_Active, SortOrder.ASC, active, sortOperator_Active, Operators.STRING);
		registerField("Channel", listheader_Channel, SortOrder.ASC, channel, sortOperator_Channel, Operators.STRING);
		doRenderPage();
		search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ProductGroupList_ProductGroupListSearch(Event event) {
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
	public void onClick$button_ProductGroupList_NewProductGroup(Event event) {
		logger.debug(Literal.ENTERING);

		ProductGroup productGroup = new ProductGroup();
		productGroup.setNewRecord(true);
		productGroup.setWorkflowId(getWorkFlowId());
		doShowDialogPage(productGroup);

		logger.debug(Literal.LEAVING);
	}

	public void onProductGroupListItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final long id = (long) this.listBoxProductGroup.getSelectedItem().getAttribute("ProductGroupId");
		ProductGroup productGroup = productGroupService.getProductGroup(id);

		if (productGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  productGroupId =? ");

		if (doCheckAuthority(productGroup, whereCond.toString(), new Object[] { productGroup.getProductGroupId() })) {
			if (isWorkFlowEnabled() && productGroup.getWorkflowId() == 0) {
				productGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(productGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param covenanttype The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ProductGroup ProductGroup) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("productGroup", ProductGroup);
		arg.put("productGroupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ProductGroup/ProductGroupDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onProductGroupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxProductGroup.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		ProductGroup productGroup = productGroupService.getProductGroup(id);

		if (productGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =? ");

		if (doCheckAuthority(productGroup, whereCond.toString(), new Object[] { productGroup.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && productGroup.getWorkflowId() == 0) {
				productGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(productGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
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

	public void setProductGroupService(ProductGroupService productGroupService) {
		this.productGroupService = productGroupService;
	}

}
