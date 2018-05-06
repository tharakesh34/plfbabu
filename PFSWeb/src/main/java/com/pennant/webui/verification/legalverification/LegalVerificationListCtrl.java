package com.pennant.webui.verification.legalverification;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.verification.legalverification.model.LegalVerificationListModelItemRender;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Verification/LegalVerification/LegalVerificationList.zul file.
 * 
 */
public class LegalVerificationListCtrl extends GFCBaseListCtrl<LegalVerification> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LegalVerificationListCtrl.class);

	protected Window window_LegalVerification;
	protected Borderlayout borderLayout_LegalVerificationList;
	protected Paging pagingLegalVerificationList;
	protected Listbox listBoxLegalVerification;

	// List headers
	protected Listheader listheader_CIF;
	protected Listheader listheader_LoanReference;
	protected Listheader listheader_Agency;
	protected Listheader listheader_CreatedOn;

	// checkRights
	protected Button button_LegalVerificationList_LegalVerificationSearch;

	// Search Fields
	protected Listbox sortOperator_CIF;
	protected Listbox sortOperator_LoanReference;
	protected Listbox sortOperator_Agency;
	protected Listbox sortOperator_CreatedOn;

	protected Textbox cif;
	protected Textbox loanReference;
	protected ExtendedCombobox agency;
	protected Datebox createdOn;

	private String module = "";

	@Autowired
	private transient LegalVerificationService legalVerificationService;

	/**
	 * default constructor.<br>
	 */
	public LegalVerificationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LegalVerification";
		super.pageRightName = "LegalVerificationList";
		super.tableName = "verification_lv_view";
		super.queueTableName = "verification_lv_view";
		super.enquiryTableName = "verification_lv_view";
		this.module = getArgument("module");
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalVerification(Event event) {
		logger.debug(Literal.ENTERING);

		if ("ENQ".equals(this.module)) {
			enqiryModule = true;
		}

		doSetFieldProperties();
		// Set the page level components.
		setPageComponents(window_LegalVerification, borderLayout_LegalVerificationList, listBoxLegalVerification,
				pagingLegalVerificationList);
		setItemRender(new LegalVerificationListModelItemRender());

		// Register buttons and fields.label_LegalVerificationList_RecordType.value
		registerButton(button_LegalVerificationList_LegalVerificationSearch);

		registerField("id");
		registerField("verificationId");
		registerField("documentid");
		registerField("documentsubid");
		registerField("cif", listheader_CIF, SortOrder.ASC, cif, sortOperator_CIF, Operators.STRING);
		registerField("keyReference", listheader_LoanReference, SortOrder.ASC, loanReference,
				sortOperator_LoanReference, Operators.STRING);
		registerField("createdOn", listheader_CreatedOn, SortOrder.NONE, createdOn, sortOperator_CreatedOn,
				Operators.DATE);
		registerField("agencyName", listheader_Agency, SortOrder.ASC, agency, sortOperator_Agency, Operators.DEFAULT);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Agency
		this.agency.setMaxlength(50);
		this.agency.setTextBoxWidth(120);
		this.agency.setModuleName("VerificationAgencies");
		this.agency.setValueColumn("DealerName");
		this.agency.setDescColumn("DealerCity");
		this.agency.setValidateColumns(new String[] { "DealerName", "DealerCity" });
		Filter agencyFilter[] = new Filter[1];
		agencyFilter[0] = new Filter("DealerType", Agencies.LVAGENCY.getKey(), Filter.OP_EQUAL);
		agency.setFilters(agencyFilter);

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject.addFilter(new Filter("recordType", "", Filter.OP_NOT_EQUAL));
		}
		if (agency.getAttribute("agency") != null) {
			this.searchObject.removeFiltersOnProperty("agency");
			long agencyId = Long.parseLong(agency.getAttribute("agency").toString());
			this.searchObject.addFilter(new Filter("agency", agencyId, Filter.OP_EQUAL));
		}
	}

	public void onFulfill$agency(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = agency.getObject();
		if (dataObject instanceof String) {
			this.agency.setValue(dataObject.toString());
			this.agency.setDescription("");
			this.agency.setAttribute("agency", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.agency.setAttribute("agency", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doReset() {
		super.doReset();
		this.agency.setAttribute("agency", null);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_LegalVerificationList_LegalVerificationSearch(Event event) {
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

	public void onLegalVerificationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLegalVerification.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		final long documentId = (long) selectedItem.getAttribute("documentId");
		final String documentSubId = (String) selectedItem.getAttribute("documentSubId");
		final long verificationId = (long) selectedItem.getAttribute("verificationId");
		
		LegalVerification lv=new LegalVerification();
		
		lv.setId(id);
		lv.setDocumentId(documentId);
		lv.setDocumentSubId(documentSubId);
		lv.setVerificationId(verificationId);
		lv = legalVerificationService.getLegalVerification(lv);

		if (lv == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(lv.getId());
		whereCond.append(" AND  version=");
		whereCond.append(lv.getVersion());

		if (doCheckAuthority(lv, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && lv.getWorkflowId() == 0) {
				lv.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(lv);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param fieldinvestigation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LegalVerification legalVerification) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("legalVerification", legalVerification);
		arg.put("legalVerificationListCtrl", this);
		arg.put("enqiryModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Verification/LegalVerification/LegalVerificationDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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

}
