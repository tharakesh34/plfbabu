package com.pennant.webui.applicationmasters.presentmentreasoncode;

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

import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;
import com.pennant.backend.service.applicationmaster.PresentmentReasonCodeService;
import com.pennant.webui.applicationmasters.presentmentreasoncode.model.PresentmentReasonCodeListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

public class PresentmentReasonCodeListCtrl extends GFCBaseListCtrl<PresentmentReasonCode> {
	private static final long serialVersionUID = 3226455931949186314L;
	private static final Logger logger = Logger.getLogger(PresentmentReasonCodeListCtrl.class);

	protected Window window_PresentmentReasonCodeList;
	protected Borderlayout borderLayout_PresentmentReasonCodeList;
	protected Paging pagingPresentmentReasonCodeList;
	protected Listbox listBoxPresentmentReasonCodeList;

	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_Active;

	protected Button button_PresentmentReasonCodeList_NewPresentmentReasonCodeList;
	protected Button button_PresentmentReasonCodeList_PresentmentReasonCodeSearchDialog;

	protected Textbox code;
	protected Textbox description;
	protected Checkbox active;

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Active;

	private transient PresentmentReasonCodeService presentmentReasonCodeService;

	/**
	 * default constructor.<br>
	 */
	public PresentmentReasonCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentReasonCode";
		super.pageRightName = "PresentmentReasonCodeList";
		super.tableName = "PresentmentReasonCode_AView";
		super.queueTableName = "PresentmentReasonCode_View";
	}
	
	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentReasonCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_PresentmentReasonCodeList, borderLayout_PresentmentReasonCodeList, listBoxPresentmentReasonCodeList,
				pagingPresentmentReasonCodeList);
		setItemRender(new PresentmentReasonCodeListModelItemRender());

		// Register buttons and fields.
		registerButton(button_PresentmentReasonCodeList_NewPresentmentReasonCodeList, "button_PresentmentReasonCodeList_NewPresentmentReasonCode", true);
		registerButton(button_PresentmentReasonCodeList_PresentmentReasonCodeSearchDialog);

		registerField("code", listheader_Code, SortOrder.ASC, code, sortOperator_Code,
				Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active,
				Operators.BOOLEAN);

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
	public void onClick$button_PresentmentReasonCodeList_PresentmentReasonCodeSearchDialog(Event event) {
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
	public void onClick$button_PresentmentReasonCodeList_NewPresentmentReasonCodeList(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		PresentmentReasonCode presentmentReasonCode = new PresentmentReasonCode();
		presentmentReasonCode.setNewRecord(true);
		presentmentReasonCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(presentmentReasonCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPresentmentReasonCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPresentmentReasonCodeList.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		PresentmentReasonCode presentmentReasonCode = presentmentReasonCodeService.getPresentmentReasonCodeById(id);

		if (presentmentReasonCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND Code='" + presentmentReasonCode.getCode() + "'" + " AND version="
				+ presentmentReasonCode.getVersion() + " ";

		if (doCheckAuthority(presentmentReasonCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && presentmentReasonCode.getWorkflowId() == 0) {
				presentmentReasonCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(presentmentReasonCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aPresentmentReasonCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PresentmentReasonCode aPresentmentReasonCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("presentmentReasonCode", aPresentmentReasonCode);
		arg.put("presentmentReasonCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/PresentmentReasonCode/PresentmentReasonCodeDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setPresentmentReasonCodeService(PresentmentReasonCodeService presentmentReasonCodeService) {
		this.presentmentReasonCodeService = presentmentReasonCodeService;
	}

}
