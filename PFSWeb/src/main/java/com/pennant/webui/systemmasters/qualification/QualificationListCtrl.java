package com.pennant.webui.systemmasters.qualification;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.systemmasters.Qualification;
import com.pennant.backend.service.systemmasters.QualificationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.qualification.model.QualificationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class QualificationListCtrl extends GFCBaseListCtrl<Qualification> {
	private static final long serialVersionUID = 269967917185319880L;
	private static final Logger logger = Logger.getLogger(QualificationListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_QualificationList;
	protected Borderlayout borderLayout_QualificationList;
	protected Paging pagingQualificationList;
	protected Listbox listBoxQualification;

	protected Textbox qualificationCode;
	protected Textbox qualificationDesc;
	protected Checkbox qualificationIsActive;

	protected Listbox sortOperator_QualificationDesc;
	protected Listbox sortOperator_QualificationCode;
	protected Listbox sortOperator_QualificationIsActive;

	// List headers
	protected Listheader listheader_QualificationCode;
	protected Listheader listheader_QualificationDesc;
	protected Listheader listheader_QualificationSelfEmployee;
	protected Listheader listheader_QualificationIsActive;

	// checkRights
	protected Button button_QualificationList_NewQualification;
	protected Button button_QualificationList_QualificationSearchDialog;

	private transient QualificationService qualificationService;

	/**
	 * default constructor.<br>
	 */
	public QualificationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Qualification";
		super.pageRightName = "QualificationList";
		super.tableName = "Qualification_AView";
		super.queueTableName = "Qualification_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("code", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_QualificationList(Event event) {
		// Set the page level components.
		setPageComponents(window_QualificationList, borderLayout_QualificationList, listBoxQualification,
				pagingQualificationList);
		setItemRender(new QualificationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_QualificationList_NewQualification, "button_QualificationList_NewQualification", true);
		registerButton(button_QualificationList_QualificationSearchDialog);

		registerField("code", listheader_QualificationCode, SortOrder.ASC, qualificationCode,
				sortOperator_QualificationCode, Operators.STRING);
		registerField("description", listheader_QualificationDesc, SortOrder.NONE, qualificationDesc,
				sortOperator_QualificationDesc, Operators.STRING);
		registerField("active", listheader_QualificationIsActive, SortOrder.NONE, qualificationIsActive,
				sortOperator_QualificationIsActive, Operators.BOOLEAN);

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
	public void onClick$button_QualificationList_QualificationSearchDialog(Event event) {
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
	public void onClick$button_QualificationList_NewQualification(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Qualification Qualification = new Qualification();
		Qualification.setNewRecord(true);
		Qualification.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(Qualification);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onQualificationItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxQualification.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Qualification Qualification = qualificationService.getQualificationById(id);

		if (Qualification == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND Code='" + Qualification.getCode() + "' AND version=" + Qualification.getVersion()
				+ " ";
		if (doCheckAuthority(Qualification, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && Qualification.getWorkflowId() == 0) {
				Qualification.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(Qualification);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param Qualification
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Qualification Qualification) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("qualification", Qualification);
		arg.put("qualificationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Qualification/QualificationDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setQualificationService(QualificationService QualificationService) {
		this.qualificationService = QualificationService;
	}
}
