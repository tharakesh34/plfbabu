package com.pennanttech.pff.mmfl.cd.webui;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cd.model.MerchantDetails;
import com.pennanttech.pff.cd.service.MerchantDetailsService;

public class MerchantDetailsListCtrl extends GFCBaseListCtrl<MerchantDetails> {
	private static final long serialVersionUID = 1L;

	protected Window window_merchantDetails;
	protected Borderlayout borderLayout_MerchantDetails;
	protected Paging pagingMerchantDetailsList;
	protected Listbox listBoxMerchantDetailsList;

	// List headers
	protected Listheader listheader_MerchantName;
	protected Listheader listheader_StoreId;
	protected Listheader listheader_StoreName;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_MerchantDetailsList_NewMerchantDetails;
	protected Button button_MerchantDetailsList_MerchantDetailsListSearch;

	// Search Fields
	protected Textbox merchantName;
	protected Intbox storeId;
	protected Textbox storeName;
	protected Textbox active;

	protected Listbox sortOperator_MerchantName;
	protected Listbox sortOperator_StoreId;
	protected Listbox sortOperator_StoreName;
	protected Listbox sortOperator_Active;

	private transient MerchantDetailsService merchantDetailsService;

	/**
	 * default constructor.<br>
	 */
	public MerchantDetailsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MerchantDetails";
		super.pageRightName = "MerchantList";
		super.tableName = "CD_Merchants_AView";
		super.queueTableName = "CD_Merchants_View";
		super.enquiryTableName = "CD_Merchants_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_merchantDetails(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_merchantDetails, borderLayout_MerchantDetails, listBoxMerchantDetailsList,
				pagingMerchantDetailsList);
		setItemRender(new MerchantDetailsListModelItemRenderer());
		registerButton(button_MerchantDetailsList_MerchantDetailsListSearch);
		registerButton(button_MerchantDetailsList_NewMerchantDetails, "button_MerchantDetailsList_NewMerchantDetails",
				true);
		registerField("MerchantId");
		registerField("MerchantName", listheader_MerchantName, SortOrder.NONE, merchantName, sortOperator_MerchantName,
				Operators.STRING);
		registerField("StoreId", listheader_StoreId, SortOrder.ASC, storeId, sortOperator_StoreId, Operators.STRING);
		registerField("StoreName", listheader_StoreName, SortOrder.ASC, storeName, sortOperator_StoreName,
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
	public void onClick$button_MerchantDetailsList_MerchantDetailsListSearch(Event event) {
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
	public void onClick$button_MerchantDetailsList_NewMerchantDetails(Event event) {
		logger.debug(Literal.ENTERING);

		MerchantDetails merchantDetails = new MerchantDetails();
		merchantDetails.setNewRecord(true);
		merchantDetails.setWorkflowId(getWorkFlowId());
		doShowDialogPage(merchantDetails);

		logger.debug(Literal.LEAVING);
	}

	public void onMerchantDetailsListItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final long id = (long) this.listBoxMerchantDetailsList.getSelectedItem().getAttribute("MerchantId");
		MerchantDetails merchantDetails = merchantDetailsService.getMerchantDetails(id);

		if (merchantDetails == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(merchantDetails, whereCond.toString(), new Object[] { merchantDetails.getMerchantId() })) {
			if (isWorkFlowEnabled() && merchantDetails.getWorkflowId() == 0) {
				merchantDetails.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(merchantDetails);
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
	private void doShowDialogPage(MerchantDetails merchantDetails) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("MerchantDetails", merchantDetails);
		arg.put("merchantDetailsListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BussinessMasters/MerchantDetailsDialogue.zul",
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

	public void setMerchantDetailsService(MerchantDetailsService merchantDetailsService) {
		this.merchantDetailsService = merchantDetailsService;
	}

}
