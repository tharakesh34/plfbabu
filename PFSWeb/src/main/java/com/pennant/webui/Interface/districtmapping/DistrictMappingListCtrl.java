package com.pennant.webui.Interface.districtmapping;

import java.util.List;
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
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.cersai.DistrictMapping;
import com.pennant.backend.service.cersai.DistrictMappingService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.Interface.districtmapping.model.DistrictMappingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Interface/DistrictMapping/DistrictMappingList.zul file.
 * 
 */
public class DistrictMappingListCtrl extends GFCBaseListCtrl<DistrictMapping> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DistrictMappingListCtrl.class);

	protected Window window_DistrictMappingList;
	protected Borderlayout borderLayout_DistrictMappingList;
	protected Paging pagingDistrictMappingList;
	protected Listbox listBoxDistrictMapping;

	// List headers
	protected Listheader listheader_MappingType;
	protected Listheader listheader_District;
	protected Listheader listheader_MappingValue;

	// checkRights
	protected Button button_DistrictMappingList_NewDistrictMapping;
	protected Button button_DistrictMappingList_DistrictMappingSearch;

	// Search Fields
	protected Combobox mappingType; // autowired
	protected Textbox district; // autowired
	protected Textbox mappingValue; // autowired

	protected Listbox sortOperator_MappingType;
	protected Listbox sortOperator_District;
	protected Listbox sortOperator_MappingValue;

	private transient DistrictMappingService districtMappingService;
	private List<ValueLabel> listMappingType = PennantStaticListUtil.getMappingTypes();

	/**
	 * default constructor.<br>
	 */
	public DistrictMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DistrictMapping";
		super.pageRightName = "DistrictMappingList";
		super.tableName = "DistrictMapping_AView";
		super.queueTableName = "DistrictMapping_View";
		super.enquiryTableName = "DistrictMapping_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DistrictMappingList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_DistrictMappingList, borderLayout_DistrictMappingList, listBoxDistrictMapping,
				pagingDistrictMappingList);
		setItemRender(new DistrictMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DistrictMappingList_DistrictMappingSearch);
		registerButton(button_DistrictMappingList_NewDistrictMapping, "button_DistrictMappingList_NewDistrictMapping",
				true);
		fillComboBox(this.mappingType, "", listMappingType, "");
		registerField("mappingType", listheader_MappingType, SortOrder.NONE, mappingType, sortOperator_MappingType,
				Operators.STRING);
		registerField("district", listheader_District, SortOrder.NONE, district, sortOperator_District,
				Operators.STRING);
		registerField("districtName");
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
	public void onClick$button_DistrictMappingList_DistrictMappingSearch(Event event) {
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
	public void onClick$button_DistrictMappingList_NewDistrictMapping(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		DistrictMapping districtmapping = new DistrictMapping();
		districtmapping.setNewRecord(true);
		districtmapping.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(districtmapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onDistrictMappingItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDistrictMapping.getSelectedItem();
		final int mappingType = Integer.valueOf((String) selectedItem.getAttribute("mappingType"));
		final String district = (String) selectedItem.getAttribute("district");
		final String mappingValue = (String) selectedItem.getAttribute("mappingValue");
		DistrictMapping districtmapping = districtMappingService.getDistrictMapping(mappingType, district,
				mappingValue);

		if (districtmapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  MappingType = '");
		whereCond.append(districtmapping.getMappingType());
		whereCond.append(" ' AND  District = '");
		whereCond.append(districtmapping.getDistrict());
		whereCond.append(" ' AND  MappingValue = '");
		whereCond.append(districtmapping.getMappingValue());
		whereCond.append("' AND  version=");
		whereCond.append(districtmapping.getVersion());

		if (doCheckAuthority(districtmapping, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && districtmapping.getWorkflowId() == 0) {
				districtmapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(districtmapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param districtmapping The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DistrictMapping districtmapping) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("districtMapping", districtmapping);
		arg.put("districtMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Interface/DistrictMapping/DistrictMappingDialog.zul", null,
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

	public void setDistrictMappingService(DistrictMappingService districtMappingService) {
		this.districtMappingService = districtMappingService;
	}
}