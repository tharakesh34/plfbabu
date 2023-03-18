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

import com.pennant.ExtendedCombobox;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cd.model.Manufacturer;
import com.pennanttech.pff.cd.service.ManufacturerService;

public class ManufacturerListCtrl extends GFCBaseListCtrl<Manufacturer> {
	private static final long serialVersionUID = 1L;

	protected Window window_manufacturer;
	protected Borderlayout borderLayout_manufacturer;
	protected Paging pagingManufacturerList;
	protected Listbox listBoxManufacturer;

	// List headers
	protected Listheader listheader_ManufacturerName;
	protected Listheader listheader_Description;
	protected Listheader listheader_Channel;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_ManufacturerList_NewManufacturer;
	protected Button button_ManufacturerList_ManufacturerListSearch;

	// Search Fields
	protected Textbox manufacturerName;
	protected Textbox description;
	protected ExtendedCombobox channel;
	protected Textbox active;

	protected Listbox sortOperator_ManufacturerName;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Channel;
	protected Listbox sortOperator_Active;

	private transient ManufacturerService manufacturerService;

	/**
	 * default constructor.<br>
	 */
	public ManufacturerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Manufacturer";
		super.pageRightName = "ManufacturerList";
		super.tableName = "CD_Manufacturers_AView";
		super.queueTableName = "CD_Manufacturers_View";
		super.enquiryTableName = "CD_Manufacturers_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_manufacturer(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_manufacturer, borderLayout_manufacturer, listBoxManufacturer, pagingManufacturerList);
		setItemRender(new ManufacturerListModelItemRenderer());
		registerButton(button_ManufacturerList_ManufacturerListSearch);
		registerButton(button_ManufacturerList_NewManufacturer, "button_ManufacturerList_NewManufacturer", true);
		registerField("ManufacturerId");
		registerField("Name", listheader_ManufacturerName, SortOrder.NONE, manufacturerName,
				sortOperator_ManufacturerName, Operators.STRING);
		registerField("Description", listheader_Description, SortOrder.ASC, description, sortOperator_Description,
				Operators.STRING);
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
	public void onClick$button_ManufacturerList_ManufacturerListSearch(Event event) {
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
	public void onClick$button_ManufacturerList_NewManufacturer(Event event) {
		logger.debug(Literal.ENTERING);

		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setNewRecord(true);
		manufacturer.setWorkflowId(getWorkFlowId());
		doShowDialogPage(manufacturer);

		logger.debug(Literal.LEAVING);
	}

	public void onManufacturerListItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final long id = (long) this.listBoxManufacturer.getSelectedItem().getAttribute("ManufacturerId");
		Manufacturer manufacturer = manufacturerService.getManufacturer(id);

		if (manufacturer == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(manufacturer, whereCond.toString(), new Object[] { manufacturer.getManufacturerId() })) {
			if (isWorkFlowEnabled() && manufacturer.getWorkflowId() == 0) {
				manufacturer.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(manufacturer);
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
	private void doShowDialogPage(Manufacturer manufacturer) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("Manufacturer", manufacturer);
		arg.put("manufacturerListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BussinessMasters/ManufacturerDialogue.zul", null,
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

	public void setManufacturerService(ManufacturerService manufacturerService) {
		this.manufacturerService = manufacturerService;
	}

}