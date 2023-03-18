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

import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cd.model.SchemeProductGroup;
import com.pennanttech.pff.cd.service.SchemeProductGroupService;

public class SchemeProductGroupListCtrl extends GFCBaseListCtrl<SchemeProductGroup> {
	private static final long serialVersionUID = 1L;

	protected Window window_schemeProductGroup;
	protected Borderlayout borderLayout_schemeProductGroup;
	protected Paging pagingSchemeProductGroupList;
	protected Listbox listBoxSchemeProductGroup;

	// List headers
	protected Listheader listheader_SchemeId;
	protected Listheader listheader_ProductGroupCode;
	protected Listheader listheader_POSVendor;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_SchemeProductGroupList_NewSchemeProductGroup;
	protected Button button_SchemeProductGroupList_SchemeProductGroupListSearch;

	// Search Fields
	protected Textbox schemeId;
	protected Intbox productGroupCode;
	protected Uppercasebox posVendor;
	protected Textbox active;

	protected Listbox sortOperator_SchemeId;
	protected Listbox sortOperator_ProductGroupCode;
	protected Listbox sortOperator_POSVendor;
	protected Listbox sortOperator_Active;

	private transient SchemeProductGroupService schemeProductGroupService;

	/**
	 * default constructor.<br>
	 */
	public SchemeProductGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SchemeProductGroup";
		super.pageRightName = "CDSchemeProductGroupList";
		super.tableName = "CD_Scheme_ProductGroup_AView";
		super.queueTableName = "CD_Scheme_ProductGroup_View";
		super.enquiryTableName = "CD_Scheme_ProductGroup_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_schemeProductGroup(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_schemeProductGroup, borderLayout_schemeProductGroup, listBoxSchemeProductGroup,
				pagingSchemeProductGroupList);
		setItemRender(new SchemeProductGroupModelItemRenderer());
		registerButton(button_SchemeProductGroupList_SchemeProductGroupListSearch);
		registerButton(button_SchemeProductGroupList_NewSchemeProductGroup,
				"button_SchemeProductGroupList_NewSchemeProductGroup", true);
		registerField("SchemeProductGroupId");
		registerField("PromotionId", listheader_SchemeId, SortOrder.NONE, schemeId, sortOperator_SchemeId,
				Operators.STRING);
		registerField("ProductGroupCode", listheader_ProductGroupCode, SortOrder.ASC, productGroupCode,
				sortOperator_ProductGroupCode, Operators.NUMERIC);
		registerField("POSVendor", listheader_POSVendor, SortOrder.ASC, posVendor, sortOperator_POSVendor,
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
	public void onClick$button_SchemeProductGroupList_SchemeProductGroupListSearch(Event event) {
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
	public void onClick$button_SchemeProductGroupList_NewSchemeProductGroup(Event event) {
		logger.debug(Literal.ENTERING);

		SchemeProductGroup schemeProductGroup = new SchemeProductGroup();
		schemeProductGroup.setNewRecord(true);
		schemeProductGroup.setSave(true);
		schemeProductGroup.setWorkflowId(getWorkFlowId());
		doShowDialogPage(schemeProductGroup);

		logger.debug(Literal.LEAVING);
	}

	public void onSchemeProductGroupListItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final long id = (long) this.listBoxSchemeProductGroup.getSelectedItem().getAttribute("SchemeProductGroupId");
		SchemeProductGroup schemeProductGroup = schemeProductGroupService.getSchemeProductGroup(id);

		if (schemeProductGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =?");

		if (doCheckAuthority(schemeProductGroup, whereCond.toString(),
				new Object[] { schemeProductGroup.getSchemeProductGroupId() })) {
			if (isWorkFlowEnabled() && schemeProductGroup.getWorkflowId() == 0) {
				schemeProductGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(schemeProductGroup);
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
	private void doShowDialogPage(SchemeProductGroup schemeProductGroup) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("SchemeProductGroup", schemeProductGroup);
		arg.put("schemeProductGroupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BussinessMasters/SchemeProductGroupDialogue.zul",
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

	public void setschemeProductGroupService(SchemeProductGroupService schemeProductGroupService) {
		this.schemeProductGroupService = schemeProductGroupService;
	}

}
