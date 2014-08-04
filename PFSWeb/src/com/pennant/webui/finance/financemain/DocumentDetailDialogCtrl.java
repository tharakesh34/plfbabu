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
 * FileName    		:  ScheduleDetailDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DocumentDetailDialogCtrl extends GFCBaseListCtrl<FinanceScheduleDetail> implements Serializable {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(DocumentDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_documentDetailDialog; // autoWired
	protected Borderlayout borderlayoutDocumentDetail; // autoWired
	// Finance Document Details Tab
	protected Label docDtl_finType; // autoWired
	protected Label docDtl_finCcy; // autoWired
	protected Label docDtl_scheduleMethod; // autoWired
	protected Label docDtl_profitDaysBasis; // autoWired
	protected Label docDtl_finReference; // autoWired
	protected Label label_DocumentDetailDialog_GrcEndDate; // autoWired
	protected Label docDtl_grcEndDate; // autoWired
	protected Button btnNew_DocumentDetails; // autoWired
	protected Listbox listBoxDocumentDetails; // autoWired
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private transient FinanceDetailService financeDetailService = null;
	private transient CustomerDocumentService customerDocumentService = null;

	private Object financeMainDialogCtrl = null;
	private FinanceDetail financeDetail = null;
	private List<ValueLabel> profitDaysBasisList = new ArrayList<ValueLabel>();
	private List<ValueLabel> schMethodList = new ArrayList<ValueLabel>();

	/**
	 * default constructor.<br>
	 */
	public DocumentDetailDialogCtrl() {
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
	@SuppressWarnings("unchecked")
	public void onCreate$window_documentDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			setFinanceDetail(financeDetail);
			setDocumentDetailsList(financeDetail.getDocumentDetailsList());
		}
		
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}
		
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("schMethodList")) {
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}
		
		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
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
	
		try {
		
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			
			// Document Details Tab
			this.docDtl_finType.setValue(StringUtils.trimToEmpty(finMain.getLovDescFinTypeName()));
			this.docDtl_finCcy.setValue(StringUtils.trimToEmpty(finMain.getLovDescFinCcyName()));
			this.docDtl_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(finMain.getScheduleMethod(), schMethodList));
			this.docDtl_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(finMain.getProfitDaysBasis(), profitDaysBasisList));
			this.docDtl_finReference.setValue(StringUtils.trimToEmpty(finMain.getFinReference()));
			this.docDtl_grcEndDate.setValue(DateUtility.formatDate(finMain.getGrcPeriodEndDate(), PennantConstants.dateFormate));
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType() == null || 
					!getFinanceDetail().getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {
				label_DocumentDetailDialog_GrcEndDate.setVisible(false);
				docDtl_grcEndDate.setVisible(false);
			}
			
			// fill the components with the data
			if (getDocumentDetailsList() != null && getDocumentDetailsList().size() > 0) {
				doFillDocumentDetails(getDocumentDetailsList());
			}
			
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setDocumentDetailDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error(e);
			}
			
			getBorderLayoutHeight();
			this.listBoxDocumentDetails.setHeight(this.borderLayoutHeight - 220 + "px");
			this.window_documentDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++ New Button & Double Click Events for Finance Contributor List+++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	// Finance Document Details Tab
	@SuppressWarnings("unchecked")
	public void onClick$btnNew_DocumentDetails(Event event) throws InterruptedException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
	
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
		map.put("documentDetailDialogCtrl", this);
		map.put("financeMain", updateFinanceMain());
		map.put("financeDetail",  getFinanceDetail());
		
		FinanceCheckListReferenceDialogCtrl dialogCtrl = null;
		if (getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl") != null) {
			dialogCtrl = (FinanceCheckListReferenceDialogCtrl) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl").invoke(getFinanceMainDialogCtrl());
			if (dialogCtrl != null) {
				Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass().getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
				map.put("checkListDocTypeMap", checkListDocTypeMap);
			}
		}
		
		map.put("isFacility", false);
		map.put("window", window_documentDetailDialog);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/DocumentTypeSelectDialog.zul", window_documentDetailDialog, map);
		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("unchecked")
	public void createNewDocument(CheckListDetail checkListDetail, boolean isCheckList, FinanceMain financeMain ) throws InterruptedException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DocumentDetailDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("isCheckList",isCheckList);
		if(getFinanceDetail() != null){
			map.put("isDocAllowedForInput", isDocAllowedForInput(checkListDetail.getDocType()));
		}
		
		if(checkListDetail != null && checkListDetail.isDocIsCustDOC()){
			CustomerDocument customerDocument = new CustomerDocument();
			customerDocument.setNewRecord(true);
			customerDocument.setCustDocCategory(checkListDetail.getDocType());
			customerDocument.setLovDescCustDocCategory(checkListDetail.getAnsDesc());
			customerDocument.setWorkflowId(0);
			customerDocument.setCustID(financeMain.getCustID());
			customerDocument.setLovDescCustCIF(financeMain.getLovDescCustCIF());
			customerDocument.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
			
			PFSParameter parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_COUNTRY");
			customerDocument.setCustDocIssuedCountry(parameter.getSysParmValue().trim());
			customerDocument.setLovDescCustDocIssuedCountry(parameter.getSysParmDescription());
			
			map.put("customerDocument", customerDocument);
		}else{
			DocumentDetails documentDetails = new DocumentDetails();
			if(checkListDetail != null){
				documentDetails.setDocCategory(checkListDetail.getDocType());
			}else{
				documentDetails.setDocCategory("");
			}
			documentDetails.setNewRecord(true);
			documentDetails.setWorkflowId(0);
			map.put("finDocumentDetail", documentDetails);
		}
		
		Component component = window_documentDetailDialog;
		
		FinanceCheckListReferenceDialogCtrl dialogCtrl = null;
		if (getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl") != null) {
			dialogCtrl = (FinanceCheckListReferenceDialogCtrl) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl").invoke(getFinanceMainDialogCtrl());
			if (dialogCtrl != null) {
				Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass().getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
				map.put("checkListDocTypeMap", checkListDocTypeMap);
			}
			if (isCheckList) {
				component = dialogCtrl.window_FinanceCheckListReferenceDialog;
			}
		}
		
		try {
		
			if(checkListDetail!= null && checkListDetail.isDocIsCustDOC()){
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceDocuments/FinDocumentDetailDialog.zul", component, map);
			}

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void doFillDocumentDetails(List<DocumentDetails> documentDetails) {
		logger.debug("Entering");
		
		docDetailMap = new HashMap<String, DocumentDetails>();
		this.listBoxDocumentDetails.getItems().clear();
		setDocumentDetailsList(documentDetails);
		
		for (DocumentDetails documentDetail : documentDetails) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(PennantAppUtil.getlabelDesc(documentDetail.getDocCategory(), PennantAppUtil.getDocumentTypes()));
			listitem.appendChild(listcell);
			listcell = new Listcell(documentDetail.getDocName());
			listitem.appendChild(listcell);
			listcell = new Listcell(documentDetail.getRecordType());
			listitem.appendChild(listcell);
			listitem.setAttribute("data", documentDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinDocumentItemDoubleClicked");
			this.listBoxDocumentDetails.appendChild(listitem);
			docDetailMap.put(documentDetail.getDocCategory(), documentDetail);
		}
		logger.debug("Leaving");
	}

	public void onFinDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxDocumentDetails.getSelectedItem();
		
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			 DocumentDetails finDocumentDetail = (DocumentDetails) item.getAttribute("data");
			if (StringUtils.trimToEmpty(finDocumentDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				boolean viewProcess = false;
				if (!StringUtils.trimToEmpty(finDocumentDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					viewProcess = true;
				}
				updateExistingDocument(finDocumentDetail, false, viewProcess);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public boolean isDocAllowedForInput(String docCategory){
		List<FinanceReferenceDetail> financeReferenceDetails = getFinanceDetail().getCheckList();
		if(financeReferenceDetails != null && !financeReferenceDetails.isEmpty()){ 
			for (FinanceReferenceDetail financeReferenceDetail : financeReferenceDetails) {
				List<CheckListDetail> checkListDetails = financeReferenceDetail.getLovDesccheckListDetail();
				if(checkListDetails != null && !checkListDetails.isEmpty()){
					for (CheckListDetail checkListDetail : checkListDetails) {
						if(StringUtils.trimToEmpty(checkListDetail.getDocType()).equalsIgnoreCase(docCategory)){
							return StringUtils.trimToEmpty(financeReferenceDetail.getAllowInputInStage()).contains(getFinanceDetail().getFinScheduleData()
									.getFinanceMain().getNextRoleCode());
						}
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void updateExistingDocument(DocumentDetails finDocumentDetail, boolean isCheckList, boolean viewProcess) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finDocumentDetail", finDocumentDetail);
		map.put("DocumentDetailDialogCtrl", this);
		map.put("roleCode", getRole());
		map.put("moduleType", "");
		map.put("viewProcess", viewProcess);
		map.put("isCheckList",finDocumentDetail.isDocIsCustDoc()?true:isCheckList);
		map.put("customerDialogCtrl", this);
		//map.put("newRecord", "true");
		if(getFinanceDetail() != null){
		map.put("isDocAllowedForInput", isDocAllowedForInput(finDocumentDetail.getDocCategory()));
		}
		FinanceMain financeMain = updateFinanceMain();
		if(finDocumentDetail.getDocImage() == null){
			if(finDocumentDetail.isDocIsCustDoc()){
				finDocumentDetail = getCustomerDocumentService().getCustDocByCustAndDocType(financeMain.getCustID(), finDocumentDetail.getDocCategory());
			}else{
				finDocumentDetail = getFinanceDetailService().getFinDocDetailByDocId(finDocumentDetail.getDocId());
			}
		}
		
		CustomerDocument customerDocument = null;
		if(customerDocument == null && finDocumentDetail.isDocIsCustDoc()){
			customerDocument = 	new CustomerDocument();
 
			if(financeMain != null){
				customerDocument.setCustID(financeMain.getCustID());
				customerDocument.setLovDescCustCIF(financeMain.getLovDescCustCIF());
				customerDocument.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
			}			
			customerDocument.setCustDocImage(finDocumentDetail.getDocImage());
			customerDocument.setCustDocImage(finDocumentDetail.getDocImage());
			customerDocument.setCustDocType(finDocumentDetail.getDoctype());
			customerDocument.setCustDocCategory(finDocumentDetail.getDocCategory());
			customerDocument.setCustDocName(finDocumentDetail.getDocName());
			customerDocument.setLovDescCustDocCategory(finDocumentDetail.getLovDescDocCategoryName());
			customerDocument.setCustDocExpDate(finDocumentDetail.getCustDocExpDate());
			customerDocument.setCustDocIsAcrive(finDocumentDetail.isCustDocIsAcrive());
			customerDocument.setCustDocIssuedCountry(finDocumentDetail.getCustDocIssuedCountry());
			customerDocument.setLovDescCustDocIssuedCountry(finDocumentDetail.getLovDescCustDocIssuedCountry());
			customerDocument.setCustDocIssuedOn(finDocumentDetail.getCustDocIssuedOn());
			customerDocument.setCustDocIsVerified(finDocumentDetail.isCustDocIsVerified());
			customerDocument.setCustDocRcvdOn(finDocumentDetail.getCustDocRcvdOn());
			customerDocument.setCustDocSysName(finDocumentDetail.getCustDocSysName());
			customerDocument.setCustDocTitle(finDocumentDetail.getCustDocTitle());
			customerDocument.setCustDocVerifiedBy(finDocumentDetail.getCustDocVerifiedBy());
			customerDocument.setRecordType(finDocumentDetail.getRecordType());
			customerDocument.setRecordStatus(finDocumentDetail.getRecordStatus());
			customerDocument.setLastMntBy(finDocumentDetail.getLastMntBy());
			customerDocument.setLastMntOn(finDocumentDetail.getLastMntOn());
			customerDocument.setWorkflowId(0);
		}
		
		try {
		
			Component component = window_documentDetailDialog;
			
			FinanceCheckListReferenceDialogCtrl dialogCtrl = null;
			if (getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl") != null) {
				dialogCtrl = (FinanceCheckListReferenceDialogCtrl) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl").invoke(getFinanceMainDialogCtrl());
				if (dialogCtrl != null) {
					Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass().getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
					map.put("checkListDocTypeMap", checkListDocTypeMap);
				}
				if (isCheckList) {
					component = dialogCtrl.window_FinanceCheckListReferenceDialog;
				}
			}
			
			if(finDocumentDetail.isDocIsCustDoc()){
				map.put("customerDocument", customerDocument);
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceDocuments/FinDocumentDetailDialog.zul", component, map);
			}

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Update Finance Main Details from the Finance Main Ctrl 
	 * @return
	 */
	private FinanceMain updateFinanceMain(){
		FinanceMain main = null;
		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("getFinanceMain").invoke(getFinanceMainDialogCtrl());
			if (object != null) {
				main = (FinanceMain) object;
				return main;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}
	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public Map<String, DocumentDetails> getDocDetailMap() {
		return docDetailMap;
	}
	public void setDocDetailMap(Map<String, DocumentDetails> docDetailMap) {
		this.docDetailMap = docDetailMap;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public CustomerDocumentService getCustomerDocumentService() {
		return customerDocumentService;
	}
	public void setCustomerDocumentService(
			CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

}
