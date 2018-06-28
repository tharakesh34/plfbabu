package com.pennant.webui.dms;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.service.dms.DMSIdentificationService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.dms.model.DmsDocumentDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DmsDocumentDetailListCtrl extends GFCBaseListCtrl<DMSDocumentDetails> {

	private static final long serialVersionUID = -296172461658303162L;
	private static final Logger logger = Logger.getLogger(DmsDocumentDetailListCtrl.class);

	@Autowired
	private DMSIdentificationService dmsIdentificationService;

	private Listbox listBoxDmsDocumentDetail;
	private Paging pagingDocumentDetailList;
	private Borderlayout borderLayout_DmsDocumentDetailList;
	private Window window_DmsDocumentDetailList;

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_DmsDocumentStatus;

	// Search Fields
	protected Textbox finReference; // autowired
	protected Combobox dmsDocumentStatus; // autowired

	// List headers
	protected Listheader listheader_DmsDocFinReference;
	protected Listheader listheader_DmsDocumentStatus;
	protected Listheader listheader_DmsDocRef;
	protected Listheader listheader_DmsId;

	public DmsDocumentDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DmsDocumentDetails";
		super.pageRightName = "PaymentHeaderList";
		super.tableName = "dmsdocprocesslog";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DmsDocumentDetailList(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DmsDocumentDetailList, borderLayout_DmsDocumentDetailList, listBoxDmsDocumentDetail,
				pagingDocumentDetailList);
		setItemRender(new DmsDocumentDetailListModelItemRenderer());

		// Register buttons and fields.
		doSetFieldProperties();

		registerField("finReference", listheader_DmsDocFinReference, SortOrder.NONE, finReference,
				sortOperator_FinReference, Operators.STRING);
		registerField("docRefId", listheader_DmsDocRef, SortOrder.NONE);
		registerField("id", listheader_DmsId, SortOrder.NONE);
		registerField("status", listheader_DmsDocumentStatus, SortOrder.NONE, dmsDocumentStatus,
				sortOperator_DmsDocumentStatus, Operators.STRING);

		doRenderPage();
		search();
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// finReference
		this.finReference.setMaxlength(20);
		/*this.finReference.setTextBoxWidth(120);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });*/

		//paymentType
		fillComboBox(this.dmsDocumentStatus, "", PennantStaticListUtil.getDmsDocumentStatusTypes(), "");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_DmsDocumentDetailList_DmsDocumentDetailSearch(Event event) {
		this.dmsDocumentStatus.getValue();
		
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onDmsDocumentDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxDmsDocumentDetail.getSelectedItem();
		final long dmsId = (long) selectedItem.getAttribute("dmsId");
		DMSDocumentDetails dmsDocumentDetails =(DMSDocumentDetails) selectedItem.getAttribute("dmsDocumentDetail");
		doShowDialogPage(dmsId, dmsDocumentDetails);

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(long dmsId, DMSDocumentDetails dmsDocumentDetails) {
		logger.debug(Literal.ENTERING);

		List<DMSDocumentDetails> dmsDocumentDetaillog = dmsIdentificationService.getDmsDocumentDetails(dmsId);
		Map<String, Object> arg = getDefaultArguments();
		arg.put("dmsDocumentDetailListCtrl", this);
		arg.put("dmsDocumentDetaillog", dmsDocumentDetaillog);
		arg.put("dmsDocumentDetails", dmsDocumentDetails);
		arg.put("enqiryModule", enqiryModule);
		try {
			Executions.createComponents("/WEB-INF/pages/DmsDocumentDetails/DmsDocumentDetailsDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

}
