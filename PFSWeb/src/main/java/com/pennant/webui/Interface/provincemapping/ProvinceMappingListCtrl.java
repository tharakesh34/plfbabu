package com.pennant.webui.Interface.provincemapping;

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
import com.pennant.backend.model.cersai.ProvinceMapping;
import com.pennant.backend.service.cersai.ProvinceMappingService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.Interface.provincemapping.model.ProvinceMappingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/interface/ProvinceMapping/ProvinceMappingList.zul file.
 * 
 */
public class ProvinceMappingListCtrl extends GFCBaseListCtrl<ProvinceMapping> {
	private static final long serialVersionUID = 1L;

	protected Window window_ProvinceMappingList;
	protected Borderlayout borderLayout_ProvinceMappingList;
	protected Paging pagingProvinceMappingList;
	protected Listbox listBoxProvinceMapping;

	// List headers
	protected Listheader listheader_MappingType;
	protected Listheader listheader_MappingValue;
	protected Listheader listheader_Province;

	// checkRights
	protected Button button_ProvinceMappingList_NewProvinceMapping;
	protected Button button_ProvinceMappingList_ProvinceMappingSearch;

	// Search Fields
	protected Combobox mappingType; // autowired
	protected Textbox province; // autowired
	protected Textbox mappingValue; // autowired

	protected Listbox sortOperator_MappingType;
	protected Listbox sortOperator_Province;
	protected Listbox sortOperator_MappingValue;

	private transient ProvinceMappingService provinceMappingService;
	private List<ValueLabel> listMappingType = PennantStaticListUtil.getMappingTypes();

	/**
	 * default constructor.<br>
	 */
	public ProvinceMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ProvinceMapping";
		super.pageRightName = "ProvinceMappingList";
		super.tableName = "ProvinceMapping_AView";
		super.queueTableName = "ProvinceMapping_View";
		super.enquiryTableName = "ProvinceMapping_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ProvinceMappingList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ProvinceMappingList, borderLayout_ProvinceMappingList, listBoxProvinceMapping,
				pagingProvinceMappingList);
		setItemRender(new ProvinceMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ProvinceMappingList_ProvinceMappingSearch);
		registerButton(button_ProvinceMappingList_NewProvinceMapping, "button_ProvinceMappingList_NewProvinceMapping",
				true);
		fillComboBox(this.mappingType, "", listMappingType, "");

		registerField("mappingType", listheader_MappingType, SortOrder.NONE, mappingType, sortOperator_MappingType,
				Operators.STRING);

		// registerField("mappingTypeName");
		registerField("province", listheader_Province, SortOrder.NONE, province, sortOperator_Province,
				Operators.STRING);
		registerField("provinceName");
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
	public void onClick$button_ProvinceMappingList_ProvinceMappingSearch(Event event) {
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
	public void onClick$button_ProvinceMappingList_NewProvinceMapping(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ProvinceMapping provincemapping = new ProvinceMapping();
		provincemapping.setNewRecord(true);
		provincemapping.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(provincemapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onProvinceMappingItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxProvinceMapping.getSelectedItem();
		final int mappingType = Integer.valueOf((String) selectedItem.getAttribute("mappingType"));
		final String province = (String) selectedItem.getAttribute("province");
		final String mappingValue = (String) selectedItem.getAttribute("mappingValue");
		ProvinceMapping provincemapping = provinceMappingService.getProvinceMapping(mappingType, province,
				mappingValue);

		if (provincemapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  MappingType = '");
		whereCond.append(provincemapping.getMappingType());
		whereCond.append(" ' AND  Province = '");
		whereCond.append(provincemapping.getProvince());
		whereCond.append(" ' AND  MappingValue = '");
		whereCond.append(provincemapping.getMappingValue());
		whereCond.append("' AND  version=");
		whereCond.append(provincemapping.getVersion());

		if (doCheckAuthority(provincemapping, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && provincemapping.getWorkflowId() == 0) {
				provincemapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(provincemapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param provincemapping The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ProvinceMapping provincemapping) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("provinceMapping", provincemapping);
		arg.put("provinceMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Interface/ProvinceMapping/ProvinceMappingDialog.zul", null,
					arg);
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

	public void setProvinceMappingService(ProvinceMappingService provinceMappingService) {
		this.provinceMappingService = provinceMappingService;
	}
}