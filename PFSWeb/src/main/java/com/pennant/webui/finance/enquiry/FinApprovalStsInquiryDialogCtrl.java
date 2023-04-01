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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class FinApprovalStsInquiryDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(FinApprovalStsInquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinApprovalStsInquiryDialog;
	protected Window window_FinBasicDetails;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Textbox custShrtName;
	protected Textbox custDocType;
	protected Textbox custID;
	protected Textbox mobileNo;
	protected Textbox emailID;
	protected Listbox listBoxFinApprovalStsInquiry;
	protected A reasonDeatilsLog;

	protected boolean approvedList;
	private CustomerFinanceDetail customerFinanceDetail;
	private FinApprovalStsInquiryListCtrl finApprovalStsInquiryListCtrl;
	protected Row rowFinance;

	boolean facility = false;
	boolean userActivityLog = false;
	protected Label label_windowTitle;
	protected Label label_FinApprovalStsInquiryDialog_FinReference;
	protected Label label_FinApprovalStsInquiryDialog_CustDocType;
	private List<ReasonDetailsLog> reasonDetailsList = null;

	private boolean isCustomer360 = false;

	/**
	 * default constructor.<br>
	 */
	public FinApprovalStsInquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "financeMain";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinApprovalStsInquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinApprovalStsInquiryDialog);

		try {
			if (arguments.containsKey("customerFinanceDetail")) {
				this.customerFinanceDetail = (CustomerFinanceDetail) arguments.get("customerFinanceDetail");
			} else {
				setCustomerFinanceDetail(null);
			}
			if (arguments.containsKey("FinApprovalStsInquiryListCtrl")) {
				this.finApprovalStsInquiryListCtrl = (FinApprovalStsInquiryListCtrl) arguments
						.get("FinApprovalStsInquiryListCtrl");
			} else {
				// setCustomerFinanceDetail(null);
				setFinApprovalStsInquiryListCtrl(null);
			}

			if (arguments.containsKey("approvedList")) {
				this.approvedList = (Boolean) arguments.get("approvedList");
			} else {
				this.approvedList = false;
			}

			if (arguments.containsKey("facility")) {
				this.facility = (Boolean) arguments.get("facility");
			}

			if (arguments.containsKey("userActivityLog")) {
				this.userActivityLog = (Boolean) arguments.get("userActivityLog");
			}

			if (arguments.containsKey("reasonDetailsList")) {
				this.reasonDetailsList = ((List<ReasonDetailsLog>) arguments.get("reasonDetailsList"));
			}

			if (arguments.containsKey("customer360")) {
				isCustomer360 = (boolean) arguments.get("customer360");
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinApprovalStsInquiryDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering");
		this.custCIF.setValue(customerFinanceDetail.getCustCIF());
		this.custShrtName.setValue(customerFinanceDetail.getCustShrtName());
		this.finReference.setValue(customerFinanceDetail.getFinReference());
		this.finBranch.setValue(customerFinanceDetail.getFinBranch());
		this.custID.setValue(StringUtils.trimToEmpty(customerFinanceDetail.getFinCcy()));
		this.custDocType.setValue(customerFinanceDetail.getFinTypeDesc());
		this.mobileNo.setValue(PennantApplicationUtil.amountFormate(
				customerFinanceDetail.getFinAmount().add(customerFinanceDetail.getFeeChargeAmt()),
				CurrencyUtil.getFormat(customerFinanceDetail.getFinCcy())));
		this.emailID.setValue(DateUtil.formatToLongDate(customerFinanceDetail.getFinStartDate()));
		fillAuditTransactions(customerFinanceDetail.getAuditTransactionsList(), customerFinanceDetail.getNotesList());
		logger.debug("Leaving");
	}

	/**
	 * Notes Item Clicked
	 * 
	 * @param event
	 */
	public void onNotesItemClicked(ForwardEvent event) {
		logger.debug("Entering");
		Button btnNotes = (Button) event.getOrigin().getTarget();
		List<Notes> notesList = customerFinanceDetail.getNotesList();
		List<Notes> userNotesList = new ArrayList<Notes>();
		final AuditTransaction auditTransaction = (AuditTransaction) btnNotes.getAttribute("data");
		int i = 0;
		while (i < notesList.size() && notesList.get(i).getInputDate().before(auditTransaction.getAuditDate())) {
			if (notesList.get(i).getInputBy() == auditTransaction.getLastMntBy()) {
				userNotesList.add(notesList.get(i));
			}
			i++;
		}
		try {
			Listbox listboxNotes = renderNotes(userNotesList);
			Window window = new Window();
			window.setTitle(Labels.getLabel("fin_Approval_Notes"));
			window.setClosable(true);
			window.setParent(window_FinApprovalStsInquiryDialog);

			listboxNotes.setParent(window);

			window.setHeight("80%");
			window.setWidth("60%");
			listboxNotes.setHeight(userNotesList.size() * 25 + 100 + "px");

			window.setHeight(listboxNotes.getHeight() + 140 + "px");
			window.doModal();
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Render Notes
	 * 
	 * @param appList
	 * @return
	 */
	public Listbox renderNotes(List<Notes> appList) {
		logger.debug("Entering");

		// Retrieve Notes List By Module Reference
		Listbox listboxNotes = new Listbox();
		Listitem item = null;
		Listcell lc = null;
		String alignSide = "right";
		for (int i = 0; i < appList.size(); i++) {

			Notes note = (Notes) appList.get(i);
			if (note != null) {

				item = new Listitem();
				lc = new Listcell();
				lc.setStyle("border:0px");
				Html html = new Html();

				if ("right".equals(alignSide)) {
					alignSide = "left";
				} else {
					alignSide = "right";
				}

				/*
				 * String usrAlign = ""; if("right".equals(alignSide)){ usrAlign = "left"; }else{ usrAlign = "right"; }
				 */

				String content = "<p class='triangle-right " + alignSide + "'> <font style='font-weight:bold;'> "
						+ note.getRemarks() + " </font> <br>  ";
				String date = DateUtil.format(note.getInputDate(), PennantConstants.dateTimeAMPMFormat);
				if ("I".equals(note.getRemarkType())) {
					content = content + "<font style='color:#FF0000;float:" + alignSide + ";'>"
							+ note.getUsrLogin().toLowerCase() + " : " + date + "</font></p>";
				} else {
					content = content + "<font style='color:black;float:" + alignSide + ";'>"
							+ note.getUsrLogin().toLowerCase() + " : " + date + "</font></p>";
				}
				html.setContent(content);
				lc.appendChild(html);
				lc.setParent(item);
				listboxNotes.appendChild(item);
			}
		}
		logger.debug("Leaving");
		return listboxNotes;
	}

	/**
	 * Fill Audit Transactions List
	 * 
	 * @param auditTransactionsList
	 */
	private void fillAuditTransactions(List<AuditTransaction> auditTransactionsList, List<Notes> notesList) {
		if (notesList == null) {
			notesList = new ArrayList<Notes>();
		}
		this.listBoxFinApprovalStsInquiry.getItems().clear();
		this.listBoxFinApprovalStsInquiry.setHeight(auditTransactionsList.size() * 26 + 100 + "px");
		int j = 0;
		Date prvTxn = null;
		Button btn_Notes;
		String date1, date2;
		Date d1 = null;
		Date d2;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		long diffInMilliseconds = 0;
		Listitem item;
		AuditTransaction auditTransaction = null;
		String prvUsrName = null;
		String prvRoleCode = null;
		for (int i = 0; i < auditTransactionsList.size(); i++) {
			auditTransaction = auditTransactionsList.get(i);

			/*
			 * if(auditTransaction.getUsrName().equals(prvUsrName) &&
			 * auditTransaction.getRoleCode().equals(prvRoleCode)) { continue; }
			 */
			prvUsrName = auditTransaction.getUsrName();
			prvRoleCode = auditTransaction.getRoleCode();

			item = new Listitem();
			Listcell lc;
			date1 = auditTransaction.getAuditDate().toString();
			try {
				d1 = df.parse(date1);
			} catch (ParseException e1) {
				logger.error(Literal.EXCEPTION, e1);
			}
			lc = new Listcell(DateUtil.formatToLongDate(d1));
			lc.setParent(item);
			lc = new Listcell(DateUtil.format(d1, DateUtil.DateFormat.LONG_TIME.getPattern()));
			lc.setParent(item);
			lc = new Listcell(auditTransaction.getRoleDesc());
			lc.setParent(item);
			lc = new Listcell(auditTransaction.getUsrName());
			lc.setParent(item);
			lc = new Listcell(auditTransaction.getRecordStatus());
			lc.setParent(item);
			if (prvTxn == null) {
				lc = new Listcell("0");
			} else {
				date2 = prvTxn.toString();

				try {
					d2 = df.parse(date2);
					diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());
				} catch (ParseException e) {
					logger.error("Exception: ", e);
				}
				lc = new Listcell(DurationFormatUtils.formatDuration(diffInMilliseconds, "HH:mm:ss"));
			}
			prvTxn = auditTransaction.getAuditDate();
			lc.setParent(item);

			lc = new Listcell();
			btn_Notes = new Button("Notes");
			btn_Notes.setParent(lc);
			btn_Notes.setVisible(false);
			while (j < notesList.size() && notesList.get(j).getInputDate().before(auditTransaction.getAuditDate())) {
				if (notesList.get(j).getInputBy() == auditTransaction.getLastMntBy()) {
					btn_Notes.setVisible(true);
				}
				j++;
			}
			lc.setParent(item);

			btn_Notes.setAttribute("data", auditTransaction);
			btn_Notes.setVisible(!isCustomer360);
			ComponentsCtrl.applyForward(btn_Notes, "onClick=onNotesItemClicked");
			this.listBoxFinApprovalStsInquiry.appendChild(item);
		}

		if (auditTransaction != null && !approvedList && !"Saved".equals(auditTransaction.getRecordStatus())) {
			item = new Listitem();
			Listcell lc;
			date1 = auditTransaction.getAuditDate().toString();
			try {
				d1 = df.parse(date1);
			} catch (ParseException e1) {
				logger.error(Literal.EXCEPTION, e1);
			}
			lc = new Listcell(DateUtil.formatToLongDate(d1));
			lc.setParent(item);
			lc = new Listcell(DateUtil.format(d1, DateUtil.DateFormat.LONG_TIME.getPattern()));
			lc.setParent(item);
			lc = new Listcell(customerFinanceDetail.getNextRoleDesc());
			lc.setParent(item);
			lc = new Listcell(fetchFinCurrentUser(customerFinanceDetail.getFinReference()));
			lc.setParent(item);
			lc = new Listcell("Waiting");
			lc.setParent(item);

			date2 = auditTransaction.getAuditDate().toString();

			try {
				d1 = df.parse(new Timestamp(System.currentTimeMillis()).toString());
				d2 = df.parse(date2);
				diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());
			} catch (ParseException e) {
				logger.error("Exception: ", e);
			}
			lc = new Listcell(DurationFormatUtils.formatDuration(diffInMilliseconds, "HH:mm:ss"));
			prvTxn = auditTransaction.getAuditDate();
			lc.setParent(item);

			lc = new Listcell();
			btn_Notes = new Button("Notes");
			btn_Notes.setParent(lc);
			btn_Notes.setVisible(false);
			while (j < notesList.size() && notesList.get(j).getInputDate().before(auditTransaction.getAuditDate())) {
				if (notesList.get(j).getInputBy() == auditTransaction.getLastMntBy()) {
					btn_Notes.setVisible(true);
				}
				j++;
			}
			lc.setParent(item);

			btn_Notes.setAttribute("data", auditTransaction);
			ComponentsCtrl.applyForward(btn_Notes, "onClick=onNotesItemClicked");
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.listBoxFinApprovalStsInquiry.appendChild(item);
			}
		}
	}

	private String fetchFinCurrentUser(String finReference) {
		logger.debug("Entering");
		String currentUserName = "";
		try {
			JdbcSearchObject<TaskOwners> jdbcSearchObject = new JdbcSearchObject<TaskOwners>(TaskOwners.class);
			jdbcSearchObject.addFilters(new Filter[] { new Filter("PROCESSED", 0, Filter.OP_EQUAL),
					new Filter("REFERENCE", finReference, Filter.OP_EQUAL) });
			List<TaskOwners> finTaskOwnerList = getPagedListWrapper().getPagedListService()
					.getBySearchObject(jdbcSearchObject);
			if (finTaskOwnerList != null && !finTaskOwnerList.isEmpty()) {
				TaskOwners takOwner = finTaskOwnerList.get(0);
				currentUserName = PennantApplicationUtil.getUserDesc(takOwner.getCurrentOwner());
			}
		} catch (Exception e) {
			logger.info(e);
		}
		logger.debug("Leaving");
		return currentUserName;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		doReadOnly();
		try {
			// fill the components with the data
			doWriteBeanToComponents();
			// stores the initial data for comparing if they are changed
			// during user action.
			doDesignByMode();
			if (!userActivityLog) {
				setDialog(DialogType.EMBEDDED);
			} else {
				this.window_FinApprovalStsInquiryDialog.setWidth("70%");
				this.window_FinApprovalStsInquiryDialog.setHeight("70%");
				this.window_FinApprovalStsInquiryDialog.doModal();
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinApprovalStsInquiryDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.custShrtName.setReadonly(true);
		this.custDocType.setReadonly(true);
		this.custID.setReadonly(true);
		this.mobileNo.setReadonly(true);
		this.emailID.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		if (!userActivityLog) {
			closeDialog();
		} else {
			this.window_FinApprovalStsInquiryDialog.onClose();
		}

		logger.debug("Leaving " + event.toString());
	}

	public void onClick$reasonDeatilsLog(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doReasonDeatilsLog();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doReasonDeatilsLog() {
		logger.debug(Literal.ENTERING);

		final Map<String, Object> map = new HashMap<String, Object>();

		if (reasonDetailsList != null && !reasonDetailsList.isEmpty()) {
			map.put("reasonDetails", reasonDetailsList);
			map.put("customerFinanceDetail", customerFinanceDetail);
			try {
				Executions.createComponents("/WEB-INF/pages/ReasonDetail/ReasonDetailsLogDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} else if (customerFinanceDetail.getFinReference() != null) {
			MessageUtil.showError(
					"No Reason details are available for the reference : " + customerFinanceDetail.getFinReference());
		}

		logger.debug(Literal.LEAVING);
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerFinanceDetail getCustomerFinanceDetail() {
		return customerFinanceDetail;
	}

	public void setCustomerFinanceDetail(CustomerFinanceDetail customerFinanceDetail) {
		this.customerFinanceDetail = customerFinanceDetail;
	}

	public FinApprovalStsInquiryListCtrl getFinApprovalStsInquiryListCtrl() {
		return finApprovalStsInquiryListCtrl;
	}

	public void setFinApprovalStsInquiryListCtrl(FinApprovalStsInquiryListCtrl finApprovalStsInquiryListCtrl) {
		this.finApprovalStsInquiryListCtrl = finApprovalStsInquiryListCtrl;
	}

	private void doDesignByMode() {
		if (facility) {
			this.label_windowTitle.setValue(Labels.getLabel("label_facilityApprovalStsInquiryDialog_Title"));
			this.label_FinApprovalStsInquiryDialog_FinReference
					.setValue(Labels.getLabel("label_FacilityApprovalStsInquiryList_CAFReference.value"));
			this.rowFinance.setVisible(false);
			this.label_FinApprovalStsInquiryDialog_CustDocType
					.setValue(Labels.getLabel("label_FacilityApprovalStsInquiryList_FacilityType.value"));
		}

	}

}
