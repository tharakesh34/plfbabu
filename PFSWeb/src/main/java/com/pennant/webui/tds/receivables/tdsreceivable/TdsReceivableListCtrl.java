/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : TdsReceivableListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.tds.receivables.tdsreceivable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.service.tds.receivables.TdsReceivableService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.webui.tds.receivables.tdsreceivable.model.TdsReceivableListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

/**
 * This is the controller class for the /WEB-INF/pages/tds.receivables/TdsReceivable/TdsReceivableList.zul file.
 * 
 */
public class TdsReceivableListCtrl extends GFCBaseListCtrl<TdsReceivable> {
	private static final long serialVersionUID = 1L;

	protected Window window_TdsReceivableList;
	protected Borderlayout borderLayout_TdsReceivableList;
	protected Paging pagingTdsReceivableList;
	protected Listbox listBoxTdsReceivable;

	// List headers
	protected Listheader listheader_TanNumber;
	protected Listheader listheader_TanHolderName;
	protected Listheader listheader_CertificateNumber;
	protected Listheader listheader_CertificateDate;
	protected Listheader listheader_AssessmentYear;
	protected Listheader listheader_DateOfReceipt;
	protected Listheader listheader_CertificateQuarter;

	// checkRights
	protected Button button_TdsReceivableList_NewTdsReceivable;
	protected Button button_TdsReceivableList_TdsReceivableSearch;

	// Search Fields
	protected ExtendedCombobox tanNumber; // autowired
	protected Textbox certificateNumber; // autowired
	protected Datebox certificateDate; // autowired
	protected Combobox assessmentYear; // autowired
	protected Datebox dateOfReceipt; // autowired
	protected Combobox certificateQuarter; // autowired

	protected Listbox sortOperator_TanNumber;
	protected Listbox sortOperator_CertificateNumber;
	protected Listbox sortOperator_CertificateDate;
	protected Listbox sortOperator_AssessmentYear;
	protected Listbox sortOperator_DateOfReceipt;
	protected Listbox sortOperator_CertificateQuarter;

	private transient TdsReceivableService tdsReceivableService;
	private static List<ValueLabel> financialYear;
	private DocumentDetails documentDetails;

	/**
	 * default constructor.<br>
	 */
	public TdsReceivableListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AddCertificate";
		super.pageRightName = "TdsReceivableList";
		super.tableName = "TDS_RECEIVABLES_AView";
		super.queueTableName = "TDS_RECEIVABLES_TView";
		super.enquiryTableName = "TDS_RECEIVABLES_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TdsReceivableList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_TdsReceivableList, borderLayout_TdsReceivableList, listBoxTdsReceivable,
				pagingTdsReceivableList);
		setItemRender(new TdsReceivableListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_TdsReceivableList_TdsReceivableSearch);
		registerButton(button_TdsReceivableList_NewTdsReceivable, "button_TdsReceivableList_NewTdsReceivable", true);

		registerField("id");
		registerField("tanNumber", listheader_TanNumber, SortOrder.NONE, tanNumber, sortOperator_TanNumber,
				Operators.STRING);
		registerField("tanHolderName", listheader_TanHolderName);
		registerField("certificateNumber", listheader_CertificateNumber, SortOrder.NONE, certificateNumber,
				sortOperator_CertificateNumber, Operators.STRING);
		registerField("certificateDate", listheader_CertificateDate, SortOrder.NONE, certificateDate,
				sortOperator_CertificateDate, Operators.DATE);
		registerField("assessmentYear", listheader_AssessmentYear, SortOrder.NONE, assessmentYear,
				sortOperator_AssessmentYear, Operators.STRING);
		registerField("dateOfReceipt", listheader_DateOfReceipt, SortOrder.NONE, dateOfReceipt,
				sortOperator_DateOfReceipt, Operators.DATE);
		registerField("certificateQuarter", listheader_CertificateQuarter, SortOrder.NONE, certificateQuarter,
				sortOperator_CertificateQuarter, Operators.STRING);
		registerField("docID");

		// Render the page and display the data.
		doRenderPage();
		doSetFieldProperties();
		search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_TdsReceivableList_TdsReceivableSearch(Event event) {
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
	public void onClick$button_TdsReceivableList_NewTdsReceivable(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		TdsReceivable tdsreceivable = new TdsReceivable();
		tdsreceivable.setNewRecord(true);
		tdsreceivable.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(tdsreceivable);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onTdsReceivableItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxTdsReceivable.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("iD");

		TdsReceivable tdsReceivable = tdsReceivableService.getTdsReceivable(id, TableType.TVIEW);

		if (tdsReceivable == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		documentDetails = tdsReceivableService.getDocumentDetails(tdsReceivable.getDocID(), "_View");
		tdsReceivable.setDocumentDetails(documentDetails);
		tdsReceivable.setDocName(tdsReceivable.getDocumentDetails().getDocName());

		String whereCond = " where id = ?";

		if (doCheckAuthority(tdsReceivable, whereCond, new Object[] { tdsReceivable.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && tdsReceivable.getWorkflowId() == 0) {
				tdsReceivable.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(tdsReceivable);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param tdsreceivable The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TdsReceivable tdsreceivable) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("tdsReceivable", tdsreceivable);
		arg.put("tdsReceivableListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivable/TdsReceivableDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.tanNumber.setMaxlength(19);
		this.certificateNumber.setMaxlength(8);
		this.certificateDate.setFormat(PennantConstants.dateFormat);
		this.assessmentYear.setMaxlength(50);
		this.dateOfReceipt.setFormat(PennantConstants.dateFormat);

		fillComboBox(certificateQuarter, "", PennantStaticListUtil.getCertificateQuarter(), "");
		fillComboBox(assessmentYear, "", getAssesmentYearList(), "");

		this.tanNumber.setButtonDisabled(false);
		this.tanNumber.setTextBoxWidth(140);
		this.tanNumber.setModuleName("TanDetail");
		this.tanNumber.setValueColumn("TanNumber");
		this.tanNumber.setValidateColumns(new String[] { "TanNumber" });

		logger.debug(Literal.LEAVING);
	}

	public List<ValueLabel> getAssesmentYearList() {
		String assessmentYear = (String) SysParamUtil.getValue(SMTParameterConstants.TDS_ASSESSMENT_YEAR);
		String years[] = assessmentYear.split(PennantConstants.KEY_SEPERATOR);
		int tooYear = Integer.valueOf(years[0]);

		financialYear = new ArrayList<>(1);
		Date appDate = SysParamUtil.getAppDate();
		int fromYear = DateUtil.getYear(appDate);

		Date strtFinDate = DateUtil.getDate(Integer.valueOf(DateUtil.getYear(appDate)), 3, 1);

		if (DateUtil.compare(appDate, strtFinDate) >= 0) {
			fromYear++;
		}

		int limit = fromYear - tooYear + 2;
		for (int i = 0; i < limit; i++) {
			String oAssessmentYear = (fromYear) + "-" + String.valueOf(fromYear + 1).substring(2, 4);
			financialYear.add(new ValueLabel(oAssessmentYear, oAssessmentYear));
			fromYear--;
		}

		return financialYear;
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_TdsReceivableList_PrintList(Event event) {
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

	public void setTdsReceivableService(TdsReceivableService tdsReceivableService) {
		this.tdsReceivableService = tdsReceivableService;
	}
}