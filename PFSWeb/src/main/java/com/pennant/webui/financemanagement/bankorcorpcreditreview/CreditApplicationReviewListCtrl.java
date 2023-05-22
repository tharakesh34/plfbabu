/**
 * \ * Copyright 2011 - Pennant Technologies
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
 * * FileName : CreditReviewDetailsListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * *
 * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.bankorcorpcreditreview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.financemanagement.bankorcorpcreditreview.model.CreditApplicationReviewListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/RulesFactory/FinCreditReviewDetails/CreditReviewDetailsList.zul
 * file.
 */
public class CreditApplicationReviewListCtrl extends GFCBaseListCtrl<FinCreditReviewDetails> {
	private static final long serialVersionUID = 4322539879503951300L;

	protected Window window_CreditApplicationReviewList;
	protected Borderlayout borderLayout_CreditApplicationReviewList;
	protected Paging pagingCreditApplicationReviewList;
	protected Listbox listBoxCreditApplicationReview;
	private List<ValueLabel> categoryesList = PennantAppUtil.getcustCtgCodeList();
	private List<ValueLabel> auditYearsList;

	protected Textbox custCIF;
	protected Listbox sortOperator_custCIF;
	protected Combobox custCreditReviewCode;
	protected Listbox sortOperator_custCreditReviewCode;
	protected Combobox custAuditYear;
	protected Listbox sortOperator_custAuditYear;
	protected Textbox custBankName;
	protected Listbox sortOperator_custBankName;
	protected Textbox custName;
	protected Listbox sortOperator_CustName;
	protected Textbox moduleName;

	protected Textbox moduleType;

	// List headers
	protected Listheader listheader_DetailId;
	protected Listheader listheader_CreditCustCIF;
	protected Listheader listheader_CreditCustID;
	protected Listheader listheader_CreditRevCode;
	protected Listheader listheader_AuditedYear;
	protected Listheader listheader_AuditPeriod;
	protected Listheader listheader_BankName;
	protected Listheader listheader_CreditMaxAudYear;
	protected Listheader listheader_CreditMinAudYear;
	protected Listheader listheader_CreditCustShrtName;

	protected Button button_CreditApplicationReviewList_NewCreditApplicationReview;
	protected Button button_CreditApplicationReviewList_FileUploadCreditApplicationReview;

	protected Button button_CreditAppReviewList_CreditAppReviewSearch;
	protected Button print;

	private transient CreditApplicationReviewService creditApplicationReviewService;

	private boolean isMaintinence = false;
	private String creditDivision = "";
	int dateAppCurrentYear = DateUtil.getYear(SysParamUtil.getAppDate());
	int dateAppPrevYear = dateAppCurrentYear - 1;

	/**
	 * default constructor.<br>
	 */
	public CreditApplicationReviewListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		Map<?, ?> args = Executions.getCurrent().getArg();

		if (args != null) {
			moduleCode = (String) args.get("moduleName");
		}
		super.pageRightName = "CreditApplicationReviewList";

		if ("CreditReviewMaintinence".equals(this.moduleName.getValue())) {
			isMaintinence = true;
			setWorkFlowEnabled(true);
			super.searchObject = new JdbcSearchObject<FinCreditReviewDetails>(FinCreditReviewDetails.class,
					getListRows());
			this.searchObject.addTabelName("FinCreditReviewDetails_AMView");
		} else {
			if ("CommCreditAppReview".equals(moduleCode)) {
				creditDivision = FacilityConstants.CREDIT_DIVISION_COMMERCIAL;
			} else if ("CorpCreditAppReview".equals(moduleCode)) {
				creditDivision = FacilityConstants.CREDIT_DIVISION_CORPORATE;
			}
			isMaintinence = false;
			super.tableName = "FinCreditReviewDetails_View";
			super.queueTableName = "FinCreditReviewDetails_View";

		}
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (isMaintinence) {
			this.searchObject.addFilterNotIn("RecordStatus", "'Approved'");
		} else {
			this.searchObject.addSort("lovDescCustCIF", false);

			String auditYears = "'" + dateAppCurrentYear + "','" + dateAppPrevYear + "','" + (dateAppPrevYear - 1)
					+ "'";
			String whereCondition = "AuditYear IN(" + auditYears + ")" + " OR " + " (AuditYear < " + "'"
					+ (dateAppPrevYear - 1) + "'" + ")";
			this.searchObject.addWhereClause(whereCondition);
		}

		if (!isMaintinence) {
			// this.searchObject.addFilterEqual("Division", creditDivision);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CreditApplicationReviewList(Event event) {
		// Set the page level components.
		setPageComponents(window_CreditApplicationReviewList, borderLayout_CreditApplicationReviewList,
				listBoxCreditApplicationReview, pagingCreditApplicationReviewList);
		setItemRender(new CreditApplicationReviewListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CreditApplicationReviewList_NewCreditApplicationReview,
				"button_CreditApplicationReviewList_NewCreditApplicationReview", true);

		if (!isMaintinence) {
			registerButton(button_CreditApplicationReviewList_NewCreditApplicationReview,
					"button_CreditApplicationReviewList_NewCreditApplicationReview", true);
		} else {
			// TODO
		}
		registerButton(button_CreditAppReviewList_CreditAppReviewSearch);

		registerField("DetailId");
		registerField("Division");
		if ("CorpCreditAppReview".equals(moduleCode)) {
			registerField("AuditPeriod", listheader_AuditPeriod);
		}
		registerField("lovDescCustCIF", listheader_CreditCustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("lovDescCustShrtName", listheader_CreditCustShrtName, SortOrder.NONE, custName,
				sortOperator_CustName, Operators.STRING);

		fillComboBox(custCreditReviewCode, "", categoryesList, ",I,");
		registerField("CreditRevCode", listheader_CreditRevCode, SortOrder.NONE, custCreditReviewCode,
				sortOperator_custCreditReviewCode, Operators.STRING);
		registerField("BankName", listheader_BankName, SortOrder.NONE, custBankName, sortOperator_custBankName,
				Operators.STRING);
		if (!isMaintinence) {
			fillComboBox(custAuditYear, "", getAuditYearsList(dateAppPrevYear, dateAppCurrentYear), "");
			registerField("AuditYear", listheader_AuditedYear, SortOrder.NONE, custAuditYear,
					sortOperator_custAuditYear, Operators.STRING);
		}

		if (isMaintinence) {
			registerField("lovdescmaxaudityear", listheader_CreditMaxAudYear);
			registerField("lovdescminaudityear", listheader_CreditMinAudYear);
			this.listheader_CreditCustID.setVisible(false);
			this.listheader_BankName.setVisible(true);
			this.listheader_CreditCustShrtName.setVisible(true);
			this.listheader_CreditRevCode.setVisible(true);
		}
		if (enqiryModule) {
			this.button_CreditApplicationReviewList_FileUploadCreditApplicationReview.setVisible(false);
		}

		// Render the page and display the data.
		doRenderPage();

		search();

		logger.debug("Entering");

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CreditAppReviewList_CreditAppReviewSearch(Event event) {
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

	public void doReset() {
		super.doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$button_CreditApplicationReviewList_NewCreditApplicationReview(Event event)
			throws InterruptedException {
		logger.debug("Entering" + event.toString());
		// create a new WIFFinanceMain object, We GET it from the backend.
		final FinCreditReviewDetails aCreditReviewDetails = getCreditApplicationReviewService()
				.getNewCreditReviewDetails();
		aCreditReviewDetails.setNewRecord(true);
		aCreditReviewDetails.setWorkflowId(getWorkFlowId());
		aCreditReviewDetails.setDivision(creditDivision);

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can call them with a object of the selected
		 * FinanceMain. For handed over these parameter only a Map is accepted. So we put the FinanceMain object in a
		 * HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("creditApplicationReviewDialogCtrl", new CreditApplicationReviewDialogCtrl());
		// map.put("searchObject", this.searchObjCreditReviewDetails);
		map.put("aCreditReviewDetails", aCreditReviewDetails);
		map.put("creditApplicationReviewListCtrl", this);
		// map.put("loanType", this.loanType.getValue());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationRevSelectCategoryType.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$button_CreditApplicationReviewList_FileUploadCreditApplicationReview(Event event)
			throws InterruptedException {
		logger.debug("Entering" + event.toString());
		// create a new WIFFinanceMain object, We GET it from the backend.
		final FinCreditReviewDetails aCreditReviewDetails = getCreditApplicationReviewService()
				.getNewCreditReviewDetails();
		aCreditReviewDetails.setNewRecord(true);
		aCreditReviewDetails.setWorkflowId(getWorkFlowId());
		aCreditReviewDetails.setDivision(creditDivision);

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can call them with a object of the selected
		 * FinanceMain. For handed over these parameter only a Map is accepted. So we put the FinanceMain object in a
		 * HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("creditApplicationReviewDialogCtrl", new CreditApplicationReviewDialogCtrl());
		map.put("corporateApplicationFinanceFileUploadDialogCtrl",
				new CorporateApplicationFinanceFileUploadDialogCtrl());

		// map.put("searchObject", this.searchObjCreditReviewDetails);
		map.put("aCreditReviewDetails", aCreditReviewDetails);
		map.put("creditApplicationReviewListCtrl", this);
		// map.put("loanType", this.loanType.getValue());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CorporateApplicationFinanceFileUploadDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onCreditApplicationReviewItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		// get the selected FinCreditReviewDetails object
		final Listitem item = this.listBoxCreditApplicationReview.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinCreditReviewDetails aCreditReviewDetails = (FinCreditReviewDetails) item.getAttribute("data");
			aCreditReviewDetails.setWorkflowId(getWorkFlowId());
			final FinCreditReviewDetails creditReviewDetails = getCreditApplicationReviewService()
					.getCreditReviewDetailsById(aCreditReviewDetails.getDetailId());

			if (creditReviewDetails == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(aCreditReviewDetails.getDetailId());
				errParm[0] = PennantJavaUtil.getLabel("label_CreditReviewId") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {
				List<FinCreditReviewSummary> listOfFinCreditReviewSummary = getCreditApplicationReviewService()
						.getListCreditReviewSummaryById(aCreditReviewDetails.getDetailId(), "_View", false);
				creditReviewDetails.setCreditReviewSummaryEntries(listOfFinCreditReviewSummary);

				creditReviewDetails.setDivision(aCreditReviewDetails.getDivision());
				creditReviewDetails.setWorkflowId(getWorkFlowId());
				if (isWorkFlowEnabled()) {
					String whereCond = " AND Detailid=" + creditReviewDetails.getDetailId() + " AND version="
							+ creditReviewDetails.getVersion() + " ";

					boolean userAcces = validateUserAccess(creditReviewDetails.getWorkflowId(),
							getUserWorkspace().getLoggedInUser().getUserId(), "FinCreditReviewDetails", whereCond,
							creditReviewDetails.getTaskId(), creditReviewDetails.getNextTaskId());
					if (userAcces) {
						doShowDialogPage(creditReviewDetails);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					doShowDialogPage(creditReviewDetails);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCreditReviewDetails The entity that need to be passed to the dialog.
	 * @throws InterruptedException
	 */
	private void doShowDialogPage(FinCreditReviewDetails aCreditReviewDetails) throws InterruptedException {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("creditReviewDetails", aCreditReviewDetails);
		arg.put("creditApplicationReviewListCtrl", this);

		try {
			if (!isMaintinence) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewDialog.zul",
						null, arg);
			} else {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewEnquiry.zul",
						null, arg);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		String code = "CRRVW";
		if (isMaintinence) {
			code = "MAINTAIN".concat(code);
		} else {
			code = creditDivision.concat(code);
		}
		new PTListReportUtils(code, super.searchObject, this.pagingCreditApplicationReviewList.getTotalSize() + 1);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public List<ValueLabel> getAuditYearsList(int startYear, int endYear) {
		auditYearsList = new ArrayList<ValueLabel>();
		for (; endYear >= startYear; endYear--) {
			auditYearsList.add(new ValueLabel(String.valueOf(endYear), String.valueOf(endYear)));
		}
		return auditYearsList;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}
}