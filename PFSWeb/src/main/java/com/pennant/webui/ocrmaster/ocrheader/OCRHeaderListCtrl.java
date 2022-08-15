package com.pennant.webui.ocrmaster.ocrheader;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.service.systemmasters.OCRHeaderService;
import com.pennant.webui.systemmasters.ocr.model.OCRListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class OCRHeaderListCtrl extends GFCBaseListCtrl<OCRHeader> {
	private static final Logger logger = LogManager.getLogger(OCRHeaderListCtrl.class);
	private static final long serialVersionUID = 485796535935527728L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OCRList;
	protected Borderlayout borderLayout_OCRList;
	protected Paging pagingOCRList;
	protected Listbox listBoxOCR;

	protected Listbox sortOperator_ocrID;
	protected Listbox sortOperator_ocrDescription;
	protected Listbox sortOperator_Active;

	protected Textbox ocrID;
	protected Textbox ocrDescription;
	protected Checkbox active;

	// List headers
	protected Listheader listheader_OCRID;
	protected Listheader listheader_OCRDescription;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_OCRList_NewOCR;
	protected Button button_OCRList_OCRSearchDialog;

	private transient OCRHeaderService ocrHeaderService;

	/**
	 * default constructor.<br>
	 */
	public OCRHeaderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "OCRHeader";
		super.pageRightName = "OCRHeaderList";
		super.tableName = "OCRHEADER_aview";
		super.queueTableName = "OCRHEADER_view";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_OCRList(Event event) {
		// Set the page level components.
		setPageComponents(window_OCRList, borderLayout_OCRList, listBoxOCR, pagingOCRList);
		setItemRender(new OCRListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_OCRList_NewOCR, "button_OCRList_NewOCR", true);
		registerButton(button_OCRList_OCRSearchDialog);
		registerField("headerID");
		registerField("ocrID", listheader_OCRID, SortOrder.ASC, this.ocrID, sortOperator_ocrID, Operators.STRING);
		registerField("ocrDescription", listheader_OCRDescription, SortOrder.NONE, this.ocrDescription,
				sortOperator_ocrDescription, Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_OCRList_OCRSearchDialog(Event event) {
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
	public void onClick$button_OCRList_NewOCR(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		OCRHeader ocrHeader = new OCRHeader();
		ocrHeader.setNewRecord(true);
		ocrHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(ocrHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onOCRItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxOCR.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		final long headerID = (long) selectedItem.getAttribute("id");

		OCRHeader ocrHeader = ocrHeaderService.getOCRHeader(headerID);

		if (ocrHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ocrID='" + ocrHeader.getOcrID() + "'AND ocrDescription='"
				+ ocrHeader.getOcrDescription() + "' AND version=" + ocrHeader.getVersion() + " ";

		if (doCheckAuthority(ocrHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && ocrHeader.getWorkflowId() == 0) {
				ocrHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(ocrHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param OCRHeader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(OCRHeader ocrHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("ocrHeader", ocrHeader);
		arg.put("ocrHeaderListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/OCR/OCRHeaderDialog.zul", null, arg);
		} catch (Exception e) {
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

	public OCRHeaderService getOcrHeaderService() {
		return ocrHeaderService;
	}

	public void setOcrHeaderService(OCRHeaderService ocrHeaderService) {
		this.ocrHeaderService = ocrHeaderService;
	}

}
