/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LoanDetailsEnquiryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinApprovalStsInquiryDialogCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(FinApprovalStsInquiryDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_FinApprovalStsInquiryDialog;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Textbox custShrtName;
	protected Textbox custDocType;
	protected Textbox custID;
	protected Textbox mobileNo;
	protected Textbox emailID;
	protected Listbox listBoxFinApprovalStsInquiry;
	
	protected boolean approvedList;
	private CustomerFinanceDetail customerFinanceDetail;
	private FinApprovalStsInquiryListCtrl finApprovalStsInquiryListCtrl;
	protected Row rowFinance;
	
	boolean facility=false;
	protected Label label_windowTitle;
	protected Label label_FinApprovalStsInquiryDialog_FinReference;
	protected Label label_FinApprovalStsInquiryDialog_CustDocType;
	/**
	 * default constructor.<br>
	 */
	public FinApprovalStsInquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinApprovalStsInquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
 
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !
		if (args.containsKey("customerFinanceDetail")) {
			this.customerFinanceDetail = (CustomerFinanceDetail) args.get("customerFinanceDetail");
 		} else {
			setCustomerFinanceDetail(null);
		}
		if (args.containsKey("FinApprovalStsInquiryListCtrl")) {
			this.finApprovalStsInquiryListCtrl = (FinApprovalStsInquiryListCtrl) args.get("FinApprovalStsInquiryListCtrl");
		} else {
			setCustomerFinanceDetail(null);
		}
		
		if (args.containsKey("approvedList")) {
			this.approvedList = (Boolean) args.get("approvedList");
		} else {
			this.approvedList = false;
		}
		
		if (args.containsKey("facility")) {
			this.facility = (Boolean) args.get("facility");
		} 
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog();
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
	 * @param aFinanceMain
	 *            financeMain
	 * @throws InterruptedException
	 */
	public void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");
			this.custCIF.setValue(customerFinanceDetail.getCustCIF());
			this.custShrtName.setValue(customerFinanceDetail.getCustShrtName() );
			this.finReference.setValue(customerFinanceDetail.getFinReference() );
			this.finBranch.setValue(customerFinanceDetail.getFinBranch());
			this.custID.setValue(StringUtils.trimToEmpty(customerFinanceDetail.getFinCcy()));
			this.custDocType.setValue(customerFinanceDetail.getFinTypeDesc());
			this.mobileNo.setValue(PennantApplicationUtil.amountFormate(customerFinanceDetail.getFinAmount(),customerFinanceDetail.getCcyFormat()));
			this.emailID.setValue( DateUtility.formatUtilDate(customerFinanceDetail.getFinStartDate(), PennantConstants.dateFormate));
			fillAuditTransactions(customerFinanceDetail.getAuditTransactionsList(),customerFinanceDetail.getNotesList());
		logger.debug("Leaving");
	}
	
	/**
	 * Notes Item Clicked 
	 * @param event
	 */
	public void onNotesItemClicked(ForwardEvent event){
		logger.debug("Entering");
		Button btnNotes =  (Button) event.getOrigin().getTarget();
		List<Notes> notesList = customerFinanceDetail.getNotesList();
		List<Notes> userNotesList = new ArrayList<Notes>();
		final AuditTransaction auditTransaction = (AuditTransaction) btnNotes.getAttribute("data");
		int i = 0 ;
		while(i<notesList.size() && notesList.get(i).getInputDate().before(auditTransaction.getAuditDate())){
			if(notesList.get(i).getInputBy() == auditTransaction.getLastMntBy() ){
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Render Notes 
	 * @param appList
	 * @return
	 * @throws Exception
	 */
	public Listbox renderNotes(List<Notes> appList) throws Exception{
		logger.debug("Entering");
		
		//Retrieve Notes List By Module Reference
 		Listbox listboxNotes = new Listbox();
		Listitem item = null;
		Listcell lc = null;
		String alignSide = "right";
		for(int i=0; i<appList.size(); i++){
			
			Notes note =(Notes) appList.get(i);
			if(note != null) {

				item = new Listitem();
				lc = new Listcell();
				lc.setStyle("border:0px");
				Html html = new Html();
				
				if("right".equals(alignSide)){
					alignSide = "left";
				}else{
					alignSide = "right";
				}
				
				/*String usrAlign = "";
				if("right".equals(alignSide)){
					usrAlign = "left";
				}else{
					usrAlign = "right";
				}*/
				
				String content = "<p class='triangle-right "+alignSide+"'> <font style='font-weight:bold;'> "  +note.getRemarks()+" </font> <br>  ";
				String date = DateUtility.formatUtilDate(note.getInputDate(), PennantConstants.dateTimeAMPMFormat);
				if("I".equals(note.getRemarkType())){
					content = content +  "<font style='color:#FF0000;float:"+alignSide+";'>"+note.getUsrLogin().toLowerCase()+" : "+date+"</font></p>";
				}else{
					content = content +  "<font style='color:black;float:"+alignSide+";'>"+note.getUsrLogin().toLowerCase()+" : "+date+"</font></p>";
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
	 * @param auditTransactionsList
	 */
	private void fillAuditTransactions(List<AuditTransaction> auditTransactionsList,List<Notes> notesList) {
		if(notesList == null){
			notesList = new ArrayList<Notes>();
		}
		this.listBoxFinApprovalStsInquiry.getItems().clear();
		this.listBoxFinApprovalStsInquiry.setHeight(auditTransactionsList.size() * 26 + 100 + "px");
		int j = 0;
		Date prvTxn = null;
		Button btn_Notes;
		String date1,date2;
		Date d1 = null;
		Date d2;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
		long diffInMilliseconds = 0;
		Listitem item;
		AuditTransaction auditTransaction = null;
		
		for (int i = 0; i < auditTransactionsList.size(); i++) {
			auditTransaction = auditTransactionsList.get(i);
			if(auditTransaction.getRecordStatus().equals("Saved") 
					&& i < auditTransactionsList.size()-1){
				continue;
			} 
			item = new Listitem();
			Listcell lc;
			date1 = auditTransaction.getAuditDate().toString();
			try {
				d1 = df.parse(date1);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  
			lc = new Listcell(DateUtility.formatUtilDate(d1, PennantConstants.dateFormate).toString());
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatUtilDate(d1, PennantConstants.timeFormat).toString());
			//lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell(auditTransaction.getRoleDesc());
			lc.setParent(item);
			lc=new Listcell(auditTransaction.getUsrName());
			lc.setParent(item);
			lc = new Listcell(auditTransaction.getRecordStatus());
			lc.setParent(item);
			if(prvTxn == null){
				lc = new Listcell("0");
			}else{
				  date2 = prvTxn.toString();  

				try {
					d2 = df.parse(date2);
					diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());  
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				lc = new Listcell(DurationFormatUtils.formatDuration(diffInMilliseconds, "HH:mm:ss"));
			}
			prvTxn = auditTransaction.getAuditDate();
			lc.setParent(item);

			lc = new Listcell();
			btn_Notes = new Button("Notes");
			btn_Notes.setParent(lc);
			btn_Notes.setDisabled(true);
			while(j<notesList.size() && notesList.get(j).getInputDate().before(auditTransaction.getAuditDate())){
				if(notesList.get(j).getInputBy() == auditTransaction.getLastMntBy() ){
					btn_Notes.setDisabled(false);
				}
				j++;
			}
			lc.setParent(item);

			btn_Notes.setAttribute("data", auditTransaction);
			ComponentsCtrl.applyForward(btn_Notes, "onClick=onNotesItemClicked");
			this.listBoxFinApprovalStsInquiry.appendChild(item);
		}
		
		if(auditTransaction!= null && !approvedList && !auditTransaction.getRecordStatus().equals("Saved") ){
			item = new Listitem();
			Listcell lc;
			date1 = auditTransaction.getAuditDate().toString();
			try {
				d1 = df.parse(date1);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  
			lc = new Listcell(DateUtility.formatUtilDate(d1, PennantConstants.dateFormate).toString());
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatUtilDate(d1, PennantConstants.timeFormat).toString());
			//lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell(customerFinanceDetail.getNextRoleDesc());
			lc.setParent(item);
			lc=new Listcell("");
			lc.setParent(item);
			lc = new Listcell("Waiting");
			lc.setParent(item);
		
			date2 = auditTransaction.getAuditDate().toString();

			try {
				d1 = df.parse(new Timestamp(System.currentTimeMillis()).toString());
				d2 = df.parse(date2);
				diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());  
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			lc = new Listcell(DurationFormatUtils.formatDuration(diffInMilliseconds, "HH:mm:ss"));
			prvTxn = auditTransaction.getAuditDate();
			lc.setParent(item);

			lc = new Listcell();
			btn_Notes = new Button("Notes");
			btn_Notes.setParent(lc);
			btn_Notes.setDisabled(true);
			while(j<notesList.size() && notesList.get(j).getInputDate().before(auditTransaction.getAuditDate())){
				if(notesList.get(j).getInputBy() == auditTransaction.getLastMntBy() ){
					btn_Notes.setDisabled(false);
				}
				j++;
			}
			lc.setParent(item);

			btn_Notes.setAttribute("data", auditTransaction);
			ComponentsCtrl.applyForward(btn_Notes, "onClick=onNotesItemClicked");
			this.listBoxFinApprovalStsInquiry.appendChild(item);

			
		}
	}
 
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		doReadOnly();
		try {
			// fill the components with the data
			doWriteBeanToComponents();
			// stores the initial data for comparing if they are changed
			// during user action.
			doDesignByMode();
			setDialog(this.window_FinApprovalStsInquiryDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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
			closeDialog(this.window_FinApprovalStsInquiryDialog, "financeMain");
		logger.debug("Leaving " + event.toString());
	}
 
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
 	
	public CustomerFinanceDetail getCustomerFinanceDetail() {
		return customerFinanceDetail;
	}
	
	public void setCustomerFinanceDetail(CustomerFinanceDetail customerFinanceDetail) {
		this.customerFinanceDetail = customerFinanceDetail;
	}
	
	public FinApprovalStsInquiryListCtrl getFinApprovalStsInquiryListCtrl() {
		return finApprovalStsInquiryListCtrl;
	}
	
	public void setFinApprovalStsInquiryListCtrl(
			FinApprovalStsInquiryListCtrl finApprovalStsInquiryListCtrl) {
		this.finApprovalStsInquiryListCtrl = finApprovalStsInquiryListCtrl;
	}
	
	private void doDesignByMode(){
		if (facility) {
			this.label_windowTitle.setValue(Labels.getLabel("label_facilityApprovalStsInquiryDialog_Title"));
			this.label_FinApprovalStsInquiryDialog_FinReference.setValue(Labels.getLabel("label_FacilityApprovalStsInquiryList_CAFReference.value"));
			this.rowFinance.setVisible(false);
			this.label_FinApprovalStsInquiryDialog_CustDocType.setValue(Labels.getLabel("label_FacilityApprovalStsInquiryList_FacilityType.value"));
		}
		
	}
	
}
