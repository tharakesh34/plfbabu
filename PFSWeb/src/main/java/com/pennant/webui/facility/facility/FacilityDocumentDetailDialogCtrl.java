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
package com.pennant.webui.facility.facility;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file.
 */
public class FacilityDocumentDetailDialogCtrl extends GFCBaseCtrl<DocumentDetails> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(FacilityDocumentDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_documentDetailDialog; 
	protected Borderlayout borderlayoutDocumentDetail; 
	// Finance Document Details Tab
	protected Button btnNew_DocumentDetails; 
	protected Listbox listBoxDocumentDetails; 
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private transient FinanceDetailService financeDetailService = null;
	private transient CustomerDocumentService customerDocumentService = null;

	private boolean enqModule = false;
	private Facility facility = null;
	private Object ctrlObject = null;

	/**
	 * default constructor.<br>
	 */
	public FacilityDocumentDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_documentDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_documentDetailDialog);

		try {

			if (arguments.containsKey("facility")) {
				this.facility = (Facility) arguments.get("facility");
				setDocumentDetailsList(facility.getDocumentDetailsList());
			}
			if (arguments.containsKey("control")) {
				this.ctrlObject = (Object) arguments.get("control");
			}

			if (arguments.containsKey("enqModule")) {
				enqModule = true;
			} else {
				enqModule = false;
			}

			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_documentDetailDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");
		try {
			// Document Details Tab
			// fill the components with the data
			if (getDocumentDetailsList() != null && getDocumentDetailsList().size() > 0) {
				doFillDocumentDetails(getDocumentDetailsList());
			}
			
			try {
				getCtrlObject().getClass().getMethod("setFacilityDocumentDetailDialogCtrl", this.getClass()).invoke(getCtrlObject(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			
			if (enqModule) {
				this.btnNew_DocumentDetails.setVisible(false);
			}
			
			
			getBorderLayoutHeight();
			this.listBoxDocumentDetails.setHeight(this.borderLayoutHeight - 150 + "px");
			this.window_documentDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_documentDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// New Button & Double Click Events for Finance Contributor List
	
	// Finance Document Details Tab
	public void onClick$btnNew_DocumentDetails(Event event) throws InterruptedException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", getCtrlObject());
		map.put("documentDetailDialogCtrl", this);
		if (getCtrlObject().getClass().getMethod("getFacility") != null) {
			Facility facility = (Facility) getCtrlObject().getClass().getMethod("getFacility").invoke(getCtrlObject());
			map.put("facility", facility);
		}
		FacilityCheckListReferenceDialogCtrl dialogCtrl = null;
		try {
			if (getCtrlObject().getClass().getMethod("getFacilityCheckListReferenceDialogCtrl") != null) {
				dialogCtrl = (FacilityCheckListReferenceDialogCtrl) getCtrlObject().getClass().getMethod("getFacilityCheckListReferenceDialogCtrl").invoke(getCtrlObject());
				if (dialogCtrl != null) {
					@SuppressWarnings("unchecked")
					Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass().getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
					map.put("checkListDocTypeMap", checkListDocTypeMap);
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		map.put("isFacility", true);
		map.put("window", window_documentDetailDialog);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/DocumentTypeSelectDialog.zul", window_documentDetailDialog, map);
		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("unchecked")
	public void createNewDocument(CheckListDetail checkListDetail, boolean isCheckList, Facility financeMain ) throws InterruptedException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DocumentDetailDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("isCheckList",isCheckList);
		if (enqModule) {
			map.put("enqModule", enqModule);
		}
		if(checkListDetail != null && (DocumentCategories.CUSTOMER.getKey().equals(checkListDetail.getCategoryCode()))){
			CustomerDocument customerDocument = new CustomerDocument();
			customerDocument.setNewRecord(true);
			customerDocument.setCustDocCategory(checkListDetail.getDocType());
			customerDocument.setLovDescCustDocCategory(checkListDetail.getAnsDesc());
			customerDocument.setWorkflowId(0);
			customerDocument.setCustID(financeMain.getCustID());
			customerDocument.setLovDescCustCIF(financeMain.getCustCIF());
			customerDocument.setLovDescCustShrtName(financeMain.getCustShrtName());
			
			Filter[] countrysystemDefault=new Filter[1];
			countrysystemDefault[0]=new Filter("SystemDefault", 1 ,Filter.OP_EQUAL);
			Object countryObj=	PennantAppUtil.getSystemDefault("Country","", countrysystemDefault);
			
			if (countryObj!=null) {
				Country country=(Country) countryObj;
				customerDocument.setCustDocIssuedCountry(country.getCountryCode());
				customerDocument.setLovDescCustDocIssuedCountry(country.getCountryDesc());
			}
			
			map.put("customerDocument", customerDocument);
		}else{
			DocumentDetails documentDetails = new DocumentDetails();
			if (checkListDetail!=null) {
				documentDetails.setDocCategory(checkListDetail.getDocType());
			}
			documentDetails.setNewRecord(true);
			documentDetails.setWorkflowId(0);
			map.put("finDocumentDetail", documentDetails);
		}
		Component component = window_documentDetailDialog;
		FacilityCheckListReferenceDialogCtrl dialogCtrl = null;
		try {
			if (getCtrlObject().getClass().getMethod("getFacilityCheckListReferenceDialogCtrl") != null) {
				dialogCtrl = (FacilityCheckListReferenceDialogCtrl) getCtrlObject().getClass().getMethod("getFacilityCheckListReferenceDialogCtrl").invoke(getCtrlObject());
				if (dialogCtrl != null) {
					Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass().getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
					map.put("checkListDocTypeMap", checkListDocTypeMap);
				}
				if (isCheckList) {
					component = dialogCtrl.window_FinanceCheckListReferenceDialog;
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		try {
			if(checkListDetail!= null && (DocumentCategories.CUSTOMER.getKey().equals(checkListDetail.getCategoryCode()))){
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDocDetailDialog.zul", component, map);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
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
				MessageUtil.showError("Not Allowed to maintain This Record");
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

	@SuppressWarnings("unchecked")
	public void updateExistingDocument(DocumentDetails finDocumentDetail, boolean isCheckList, boolean viewProcess) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finDocumentDetail", finDocumentDetail);
		map.put("DocumentDetailDialogCtrl", this);
		map.put("roleCode", getRole());
		map.put("moduleType", "");
		map.put("viewProcess", viewProcess);
		map.put("isCheckList",(DocumentCategories.CUSTOMER.getKey().equals(finDocumentDetail.getCategoryCode()))?true:isCheckList);
		map.put("customerDialogCtrl", this);
		//map.put("newRecord", "true");
		map.put("roleCode", getRole());
		if (enqModule) {
			map.put("enqModule", enqModule);
		}
		Facility financeMain = updateFinanceMain();
		if(finDocumentDetail.getDocImage() == null){
			if(DocumentCategories.CUSTOMER.getKey().equals(finDocumentDetail.getCategoryCode())){
				finDocumentDetail = getCustomerDocumentService().getCustDocByCustAndDocType(financeMain.getCustID(), finDocumentDetail.getDocCategory());
			}else{
				getFinanceDetailService().getFinDocDetailByDocId(finDocumentDetail.getDocId());
			}
		}
		CustomerDocument customerDocument = null;
		if(customerDocument == null && (DocumentCategories.CUSTOMER.getKey().equals(finDocumentDetail.getCategoryCode()))){
			customerDocument = 	new CustomerDocument();
 
			if(financeMain != null){
				customerDocument.setCustID(financeMain.getCustID());
				customerDocument.setLovDescCustCIF(financeMain.getCustCIF());
				customerDocument.setLovDescCustShrtName(financeMain.getCustShrtName());
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
			FacilityCheckListReferenceDialogCtrl dialogCtrl = null;
			
			try {
				if (getCtrlObject().getClass().getMethod("getFacilityCheckListReferenceDialogCtrl") != null) {
					dialogCtrl = (FacilityCheckListReferenceDialogCtrl) getCtrlObject().getClass().getMethod("getFacilityCheckListReferenceDialogCtrl").invoke(getCtrlObject());
					if (dialogCtrl != null) {
						Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass().getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
						map.put("checkListDocTypeMap", checkListDocTypeMap);
					}
					if (isCheckList) {
						component = dialogCtrl.window_FinanceCheckListReferenceDialog;
					}
				}
			} catch (Exception e) {
				logger.debug(e);
			}
			if(DocumentCategories.CUSTOMER.getKey().equals(finDocumentDetail.getCategoryCode())){
				map.put("customerDocument", customerDocument);
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDocDetailDialog.zul", component, map);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Update Finance Main Details from the Finance Main Ctrl 
	 * @return
	 */
	private Facility updateFinanceMain(){
		Facility main = null;
		try {
			Object object = getCtrlObject().getClass().getMethod("getFacility").invoke(getCtrlObject());
			if (object != null) {
				main = (Facility) object;
				return main;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public Object getCtrlObject() {
		return ctrlObject;
	}

	public void setCtrlObject(Object financeMainDialogCtrl) {
		this.ctrlObject = financeMainDialogCtrl;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
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
