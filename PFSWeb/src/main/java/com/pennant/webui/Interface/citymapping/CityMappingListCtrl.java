package com.pennant.webui.Interface.citymapping;

import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.cersai.CityMapping;
import com.pennant.backend.service.cersai.CityMappingService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.Interface.citymapping.model.CityMappingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Interface/CityMapping/CityMappingList.zul file.
 * 
 */
public class CityMappingListCtrl extends GFCBaseListCtrl<CityMapping> {
	private static final long serialVersionUID = 1L;

	protected Window window_CityMappingList;
	protected Borderlayout borderLayout_CityMappingList;
	protected Paging pagingCityMappingList;
	protected Listbox listBoxCityMapping;

	// List headers
	protected Listheader listheader_MappingType;
	protected Listheader listheader_CityCode;
	protected Listheader listheader_MappingValue;

	// checkRights
	protected Button button_CityMappingList_NewCityMapping;
	protected Button button_CityMappingList_CityMappingSearch;

	// Search Fields
	protected Combobox mappingType; // autowired
	protected Textbox cityCode; // autowired
	protected Textbox mappingValue; // autowired

	protected Listbox sortOperator_MappingType;
	protected Listbox sortOperator_CityCode;
	protected Listbox sortOperator_MappingValue;

	private transient CityMappingService cityMappingService;
	private List<ValueLabel> listMappingType = PennantStaticListUtil.getMappingTypes();

	/**
	 * default constructor.<br>
	 */
	public CityMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CityMapping";
		super.pageRightName = "CityMappingList";
		super.tableName = "CityMapping_AView";
		super.queueTableName = "CityMapping_View";
		super.enquiryTableName = "CityMapping_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CityMappingList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_CityMappingList, borderLayout_CityMappingList, listBoxCityMapping,
				pagingCityMappingList);
		setItemRender(new CityMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CityMappingList_CityMappingSearch);
		registerButton(button_CityMappingList_NewCityMapping, "button_CityMappingList_NewCityMapping", true);

		fillComboBox(this.mappingType, "", listMappingType, "");
		registerField("mappingType", listheader_MappingType, SortOrder.NONE, mappingType, sortOperator_MappingType,
				Operators.STRING);
		registerField("cityCode", listheader_CityCode, SortOrder.NONE, cityCode, sortOperator_CityCode,
				Operators.STRING);
		registerField("cityCodeName");
		registerField("mappingValue", listheader_MappingValue, SortOrder.NONE, mappingValue, sortOperator_MappingValue,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CityMappingList_CityMappingSearch(Event event) {
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
	public void onClick$button_CityMappingList_NewCityMapping(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		CityMapping citymapping = new CityMapping();
		citymapping.setNewRecord(true);
		citymapping.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(citymapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onCityMappingItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCityMapping.getSelectedItem();
		final int mappingType = Integer.valueOf((String) selectedItem.getAttribute("mappingType"));
		final String cityCode = (String) selectedItem.getAttribute("cityCode");
		final String mappingValue = (String) selectedItem.getAttribute("mappingValue");
		CityMapping citymapping = cityMappingService.getCityMapping(mappingType, cityCode, mappingValue);

		if (citymapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  MappingType = '");
		whereCond.append(citymapping.getMappingType());
		whereCond.append(" ' AND  CityCode = '");
		whereCond.append(citymapping.getCityCode());
		whereCond.append(" ' AND  MappingValue = '");
		whereCond.append(citymapping.getMappingValue());
		whereCond.append("' AND  version=");
		whereCond.append(citymapping.getVersion());

		if (doCheckAuthority(citymapping, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && citymapping.getWorkflowId() == 0) {
				citymapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(citymapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param citymapping The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CityMapping citymapping) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("cityMapping", citymapping);
		arg.put("cityMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Interface/CityMapping/CityMappingDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setCityMappingService(CityMappingService cityMappingService) {
		this.cityMappingService = cityMappingService;
	}
}