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
import com.pennanttech.pff.cd.model.SchemeDealerGroup;
import com.pennanttech.pff.cd.service.SchemeDealerGroupService;

public class SchemeDealerGroupListCtrl extends GFCBaseListCtrl<SchemeDealerGroup> {
	private static final long serialVersionUID = 1L;

	protected Window window_schemeDealerGroup;
	protected Borderlayout borderLayout_schemeDealerGroup;
	protected Paging pagingSchemeDealerGroupList;
	protected Listbox listBoxSchemeDealerGroup;

	// List headers
	protected Listheader listheader_SchemeId;
	protected Listheader listheader_DealerGroupCode;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_SchemeDealerGroupList_NewSchemeDealerGroup;
	protected Button button_SchemeDealerGroupList_SchemeDealerGroupListSearch;

	// Search Fields
	protected Textbox schemeId;
	protected Intbox dealerGroupCode;
	protected Textbox active;

	protected Listbox sortOperator_SchemeId;
	protected Listbox sortOperator_DealerGroupCode;
	protected Listbox sortOperator_Active;

	private transient SchemeDealerGroupService schemeDealerGroupService;

	/**
	 * default constructor.<br>
	 */
	public SchemeDealerGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SchemeDealerGroup";
		super.pageRightName = "CDSchemeDealerGroupList";
		super.tableName = "CD_Scheme_DealerGroup_AView";
		super.queueTableName = "CD_Scheme_DealerGroup_View";
		super.enquiryTableName = "CD_Scheme_DealerGroup_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_schemeDealerGroup(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_schemeDealerGroup, borderLayout_schemeDealerGroup, listBoxSchemeDealerGroup,
				pagingSchemeDealerGroupList);
		setItemRender(new SchemeDealerGroupModelItemRenderer());
		registerButton(button_SchemeDealerGroupList_SchemeDealerGroupListSearch);
		registerButton(button_SchemeDealerGroupList_NewSchemeDealerGroup,
				"button_SchemeDealerGroupList_NewSchemeDealerGroup", true);
		registerField("SchemeDealerGroupId");
		registerField("PromotionId", listheader_SchemeId, SortOrder.NONE, schemeId, sortOperator_SchemeId,
				Operators.STRING);
		registerField("DealerGroupCode", listheader_DealerGroupCode, SortOrder.ASC, dealerGroupCode,
				sortOperator_DealerGroupCode, Operators.STRING);
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
	public void onClick$button_SchemeDealerGroupList_SchemeDealerGroupListSearch(Event event) {
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
	public void onClick$button_SchemeDealerGroupList_NewSchemeDealerGroup(Event event) {
		logger.debug(Literal.ENTERING);

		SchemeDealerGroup schemeDealerGroup = new SchemeDealerGroup();
		schemeDealerGroup.setNewRecord(true);
		schemeDealerGroup.setSave(true);
		schemeDealerGroup.setWorkflowId(getWorkFlowId());
		doShowDialogPage(schemeDealerGroup);

		logger.debug(Literal.LEAVING);
	}

	public void onSchemeDealerGroupListItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final long id = (long) this.listBoxSchemeDealerGroup.getSelectedItem().getAttribute("SchemeDealerGroupId");
		SchemeDealerGroup schemeDealerGroup = schemeDealerGroupService.getSchemeDealerGroup(id);

		if (schemeDealerGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(schemeDealerGroup, whereCond.toString(),
				new Object[] { schemeDealerGroup.getSchemeDealerGroupId() })) {
			if (isWorkFlowEnabled() && schemeDealerGroup.getWorkflowId() == 0) {
				schemeDealerGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(schemeDealerGroup);
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
	private void doShowDialogPage(SchemeDealerGroup schemeDealerGroup) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("SchemeDealerGroup", schemeDealerGroup);
		arg.put("schemeDealerGroupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BussinessMasters/SchemeDealerGroupDialogue.zul",
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

	public void setschemeDealerGroupService(SchemeDealerGroupService schemeDealerGroupService) {
		this.schemeDealerGroupService = schemeDealerGroupService;
	}
}
