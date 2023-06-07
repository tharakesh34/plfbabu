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
 * * FileName : LoanDetailsEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.rmtmasters.TransactionDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsComparator;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsListItemRenderer;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class PostingsEnquiryDialogCtrl extends GFCBaseCtrl<ReturnDataSet> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static Logger logger = LogManager.getLogger(PostingsEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PostingsEnquiryDialog;
	protected Listbox listBoxFinPostings;
	protected Label label_showAccruals;
	protected Label label_showZeroCals;
	protected Checkbox showAccrual;
	protected Checkbox showZeroCals;
	private Tabpanel tabPanel_dialogWindow;
	protected Combobox postingGroup;
	protected Toolbar toolbar_printButton;
	protected Groupbox finBasicdetails;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;

	private List<ReturnDataSet> postingDetails;
	private String finReference = "";
	private FinanceDetailService financeDetailService;
	private FinanceEnquiry enquiry;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	StringBuilder accEvents = new StringBuilder("");

	private String tableType = "_View";
	private boolean fromRejectFinance = false;
	private boolean isModelWindow = false;

	/**
	 * default constructor.<br>
	 */
	public PostingsEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_PostingsEnquiryDialog(ForwardEvent event) {
		logger.info(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PostingsEnquiryDialog);

		try {
			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
			}

			if (arguments.containsKey("finReference")) {
				this.finReference = (String) arguments.get("finReference");
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("enquiry")) {
				this.enquiry = (FinanceEnquiry) arguments.get("enquiry");
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
			}

			if (arguments.containsKey("isModelWindow")) {
				isModelWindow = (Boolean) arguments.get("isModelWindow");
			}

			if (fromRejectFinance && arguments.containsKey("reinstateFinance")) {
				ReinstateFinance reinstateFinance = (ReinstateFinance) arguments.get("reinstateFinance");
				if (reinstateFinance != null) {
					appendFinBasicDetails(getFinBasicDetails(reinstateFinance));
				}

			} else {
				this.finBasicdetails.setVisible(false);
			}

			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PostingsEnquiryDialog.onClose();
		}
		logger.info(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.info(Literal.LEAVING);

		try {
			// Fill Posting Details
			this.showAccrual.setChecked(true);
			doFillPostings();

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();

				int rowsHeight;
				if (financeEnquiryHeaderDialogCtrl != null) {
					rowsHeight = (financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20)
							+ 1;
				} else {
					rowsHeight = 20;
				}

				this.listBoxFinPostings.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_PostingsEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight + "px");
				tabPanel_dialogWindow.appendChild(this.window_PostingsEnquiryDialog);

			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PostingsEnquiryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/** ============================================================ */
	/** Check Events For Postings */
	/** ============================================================ */
	public void onCheck$showAccrual(Event event) {
		this.listBoxFinPostings.getItems().clear();
		doFillPostings();
	}

	public void onCheck$showZeroCals(Event event) {
		this.listBoxFinPostings.getItems().clear();
		doFillPostings();
	}

	/**
	 * Method for rendering list of postings in Listbox
	 */
	private void doFillPostings() {
		logger.debug("Entering");
		fillComboBox(this.postingGroup, PennantConstants.EVENTBASE, PennantStaticListUtil.getPostingGroupList(), "");

		StringBuilder events = new StringBuilder();
		events.append("ADDDBSF, ADDDBSN, ADDDBSP, COMPOUND, DEFFRQ, DEFRPY, DPRCIATE, EARLYPAY, EARLYSTL");
		events.append(", LATEPAY, PIS_NORM, NORM_PIS, RATCHG, REPAY, SCDCHG, WRITEOFF, CMTDISB,  STAGE");
		events.append(", ISTBILL,  GRACEEND, DISBINS, FEEPAY, VASFEE, MANFEE, INSTDATE, PAYMTINS,  REAGING");
		events.append(", JVPOST,  D2C,  CHQ2B,  ASSIGN, INSADJ, INSPAY, CANINS, LPPAMZ,  WAIVER,  INSPAY");
		events.append(", ADVDUE,  WRITEBK,  OEMSBV,  MIGR,  PROVSN,  PROVCHG, PRVSN_MN,  FEREFUND,  PRSNT");
		events.append(", PRSNTRSP,  PARTCAN,  MANSUB, RESTRUCT, NPACHNG, PROVSN, CRSLANFR, CRSLANTO, EXTRF, REVWRITE");
		events.append(", BRNCHG");
		if (this.showAccrual.isChecked()) {
			events.append(", AMZ, AMZSUSP, AMZ_REV, INDAS, EXPENSE");
		}
		accEvents = events;
		if (StringUtils.isNotEmpty(events.toString())) {
			postingDetails = financeDetailService.getPostingsByFinRefAndEvent(finReference, events.toString(),
					this.showZeroCals.isChecked(), "", tableType);
			// 29-08-19 Code Removed For insurance postings
		}
		doGetListItemRenderer(postingDetails);
		logger.debug("Leaving");
	}

	public void onSelect$postingGroup(Event event) {
		logger.debug("Entering" + event.toString());
		List<ReturnDataSet> postingList = new ArrayList<>();
		postingDetails = getFinanceDetailService().getPostingsByFinRefAndEvent(finReference, accEvents.toString(),
				this.showZeroCals.isChecked(), this.postingGroup.getSelectedItem().getValue().toString(), tableType);

		logger.debug("Leaving" + event.toString());
		for (ReturnDataSet returnDataSet : postingDetails) {
			returnDataSet.setPostingGroupBy(this.postingGroup.getSelectedItem().getValue().toString());
		}
		postingList.addAll(postingDetails);
		doGetListItemRenderer(postingList);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doGetListItemRenderer(List<ReturnDataSet> postingDetails) {
		this.listBoxFinPostings
				.setModel(new GroupsModelArray(postingDetails.toArray(), new FinanceEnquiryPostingsComparator()));
		this.listBoxFinPostings.setItemRenderer(new FinanceEnquiryPostingsListItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * when the "btnPrintAccounting" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPrintAccounting(Event event) {
		logger.info(Literal.ENTERING);
		String usrName = getUserWorkspace().getLoggedInUser().getUserName();
		List<Object> list = null;

		list = new ArrayList<Object>();
		List<TransactionDetail> accountingDetails = new ArrayList<>();
		for (ReturnDataSet dataSet : postingDetails) {
			TransactionDetail detail = new TransactionDetail();
			detail.setEventCode(dataSet.getFinEvent());
			detail.setEventDesc(dataSet.getLovDescEventCodeName());
			detail.setTranType("C".equals(dataSet.getDrOrCr()) ? "Credit" : "Debit");
			detail.setTransactionCode(dataSet.getTranCode());
			detail.setTransDesc(dataSet.getTranDesc());
			detail.setCcy(dataSet.getAcCcy());
			detail.setGlCode(dataSet.getGlCode());
			detail.setAccount(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
			detail.setPostAmount(
					CurrencyUtil.format(dataSet.getPostAmount(), CurrencyUtil.getFormat(dataSet.getAcCcy())));
			detail.setRevTranCode(dataSet.getRevTranCode());
			detail.setPostDate(DateUtil.format(dataSet.getPostDate(), DateFormat.LONG_DATE.getPattern()));
			detail.setValueDate(DateUtil.format(dataSet.getValueDate(), DateFormat.LONG_DATE.getPattern()));
			accountingDetails.add(detail);
		}

		Window window = (Window) this.window_PostingsEnquiryDialog.getParent().getParent().getParent().getParent()
				.getParent().getParent();
		if (!accountingDetails.isEmpty()) {
			list.add(accountingDetails);
		}

		Map<String, Object> aruments = new HashMap<>();
		aruments.put("isModelWindow", isModelWindow);
		list.add(aruments);

		ReportsUtil.generatePDF("FINENQ_AccountingDetail", enquiry, list, usrName, window);
		logger.info(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		logger.debug(Literal.ENTERING);

		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails(ReinstateFinance reinstateFinance) {
		logger.debug(Literal.ENTERING);

		// FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, reinstateFinance.getFinType());
		arrayList.add(1, reinstateFinance.getFinCcy());
		arrayList.add(2, reinstateFinance.getScheduleMethod());
		arrayList.add(3, reinstateFinance.getFinReference());
		arrayList.add(4, reinstateFinance.getProfitDaysBasis());
		arrayList.add(5, reinstateFinance.getGrcPeriodEndDate());
		arrayList.add(6, reinstateFinance.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(reinstateFinance.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, reinstateFinance.getFinCategory());
		arrayList.add(9, reinstateFinance.getCustShrtName());
		arrayList.add(10, reinstateFinance.isNewRecord());
		arrayList.add(11, "");
		logger.debug(Literal.LEAVING);
		return arrayList;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

}
