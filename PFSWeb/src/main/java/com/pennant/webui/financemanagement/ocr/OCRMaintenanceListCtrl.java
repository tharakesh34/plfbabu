package com.pennant.webui.financemanagement.ocr;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class OCRMaintenanceListCtrl extends GFCBaseListCtrl<FinOCRHeader> {
	private static final long serialVersionUID = -5251147204336501834L;

	private static final Logger logger = LogManager.getLogger(OCRMaintenanceListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OCRMaintenanceList;
	protected Borderlayout borderLayout_OCRMaintenanceList;
	protected Listbox listBoxOCRMaintenance;
	protected Paging pagingOCRMaintenanceList;

	protected Uppercasebox finReference;
	protected Longbox totalDemand;
	protected Textbox ocrType;

	protected Listbox sortOperator_loanReference;
	protected Listbox sortOperator_totalDemand;
	protected Listbox sortOperator_ocrType;

	// List headers
	protected Listheader listheader_OCRMaintenanceLoanReference;
	protected Listheader listheader_OCRMaintenanceTotalDemand;
	protected Listheader listheader_OCRMaintenanceOCRType;

	// checkRights
	protected Button button_OCRMaintenanceList_OCRMaintenanceSearchDialog;
	private transient FinOCRHeaderService finOCRHeaderService;
	private transient FinanceTypeService financeTypeService;
	private transient FinanceMainService financeMainService;
	private transient FinAdvancePaymentsService finAdvancePaymentsService;
	protected FinFeeDetailService finFeeDetailService;

	/**
	 * default constructor.<br>
	 */
	public OCRMaintenanceListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinOCRHeader";
		super.pageRightName = "OCRMaintenanceList";
		super.tableName = "FinOCRHeader_View";
		super.queueTableName = "FinOCRHeader_View";
	}

	public void onCreate$window_OCRMaintenanceList(Event event) {

		// Set the page level components.
		setPageComponents(window_OCRMaintenanceList, borderLayout_OCRMaintenanceList, listBoxOCRMaintenance,
				pagingOCRMaintenanceList);
		setItemRender(new OCRMaintenanceListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_OCRMaintenanceList_OCRMaintenanceSearchDialog);

		registerField("FinID");
		registerField("finReference", listheader_OCRMaintenanceLoanReference, SortOrder.ASC, finReference,
				sortOperator_loanReference, Operators.STRING);
		registerField("totalDemand", listheader_OCRMaintenanceTotalDemand, SortOrder.ASC, totalDemand,
				sortOperator_totalDemand, Operators.NUMERIC);
		registerField("ocrType", listheader_OCRMaintenanceOCRType, SortOrder.ASC, ocrType, sortOperator_ocrType,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject
					.addWhereClause(" finreference in(select finreference from financemain where FinisActive=1)");
		}
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onOCRMaintenanceItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxOCRMaintenance.getSelectedItem();

		// Get the selected entity.
		long finID = (Long) selectedItem.getAttribute("finID");
		FinOCRHeader finOcrHeader = finOCRHeaderService.getFinOCRHeaderByRef(finID, "_View");

		if (finOcrHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where HeaderID=?";

		if (doCheckAuthority(finOcrHeader, whereCond, new Object[] { finOcrHeader.getHeaderID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && finOcrHeader.getWorkflowId() == 0) {
				finOcrHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(finOcrHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	private void doShowDialogPage(FinOCRHeader finOcrHeader) {
		if (isWorkFlowEnabled()) {

		}
		FinanceDetail financeDetail = new FinanceDetail();
		FinanceMain financeMain = financeMainService.getFinanceMain(finOcrHeader.getFinID(), new String[] { "FinType",
				"FinReference", "FinCcy", "ParentRef", "FinAmount", "FinAssetValue", "FinOcrRequired" }, "");
		if (StringUtils.isNotEmpty(financeMain.getParentRef())) {
			FinOCRHeader parentFinOcrHeader = finOCRHeaderService.getFinOCRHeaderByRef(financeMain.getParentRef(),
					"_View");
			if (parentFinOcrHeader != null) {
				finOcrHeader.setOcrDetailList(parentFinOcrHeader.getOcrDetailList());
			}
		}
		FinanceType financeType = financeTypeService.getFinanceTypeByFinType(financeMain.getFinType());
		List<FinAdvancePayments> finAdvPaymentList = finAdvancePaymentsService
				.getFinAdvancePaymentsById(finOcrHeader.getFinID(), "");
		List<FinFeeDetail> feeDetails = finFeeDetailService.getFinFeeDetailById(finOcrHeader.getFinID(), false, "");
		financeDetail.getFinScheduleData().setFinanceType(financeType);
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		financeDetail.setFinOCRHeader(finOcrHeader);
		financeDetail.setAdvancePaymentsList(finAdvPaymentList);
		financeDetail.getFinScheduleData().setFinFeeDetailList(feeDetails);

		Map<String, Object> map = getDefaultArguments();
		map.put("financeDetail", financeDetail);
		map.put("ocrMaintenanceListCtrl", this);
		map.put("ccyFormatter", CurrencyUtil.getFormat(financeMain.getFinCcy()));

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRDialog.zul", window, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_OCRMaintenanceList_OCRMaintenanceSearchDialog(Event event) {
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

	public void setFinOCRHeaderService(FinOCRHeaderService finOCRHeaderService) {
		this.finOCRHeaderService = finOCRHeaderService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

}
