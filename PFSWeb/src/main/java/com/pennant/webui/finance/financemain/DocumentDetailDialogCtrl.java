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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file.
 */
public class DocumentDetailDialogCtrl extends GFCBaseCtrl<DocumentDetails> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(DocumentDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_documentDetailDialog; // autoWired
	protected Borderlayout borderlayoutDocumentDetail; // autoWired

	protected Button btnNew_DocumentDetails; // autoWired
	protected Listbox listBoxDocumentDetails; // autoWired
	protected Map<String, DocumentDetails> docDetailMap = null;
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private transient FinanceDetailService financeDetailService = null;
	private transient CustomerDocumentService customerDocumentService = null;

	private Object financeMainDialogCtrl = null;
	private FinanceDetail financeDetail = null;
	
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl  collateralBasicDetailsCtrl;
	protected Groupbox finBasicdetails;
	
	private boolean headerNotrequired = false;
	private String moduleDefiner = "";
	private boolean isNotFinanceProcess = false;
	private String moduleName;

	/**
	 * default constructor.<br>
	 */
	public DocumentDetailDialogCtrl() {
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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_documentDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_documentDetailDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqiryModule = (boolean) arguments.get("enqModule");
			} else {
				enqiryModule = false;
			}
			
			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(financeDetail);
				setDocumentDetailsList(financeDetail.getDocumentDetailsList());
			}
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
			}
			if (arguments.containsKey("headerNotrequired")) {
				headerNotrequired = true;
			}
			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}
			if (arguments.containsKey("isNotFinanceProcess")) {
				isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
			}
			
			if (arguments.containsKey("moduleName")) {
				this.moduleName = (String) arguments.get("moduleName");
			}

			// append finance basic details 
			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object> )arguments.get("finHeaderList"));
			} else {
				this.finBasicdetails.setZclass("null");
			}
			
			//Document details
			if (arguments.containsKey("documentDetails")) {
				setDocumentDetailsList((List<DocumentDetails>) arguments.get("documentDetails"));
			}
			
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");

		try {
			
			//New button visibility.
			this.btnNew_DocumentDetails.setVisible(!enqiryModule);
			
			// fill the components with the data
			if (getDocumentDetailsList() != null && getDocumentDetailsList().size() > 0) {
				doFillDocumentDetails(getDocumentDetailsList());
			}

			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setDocumentDetailDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			if (headerNotrequired) {
				this.finBasicdetails.setVisible(false);
			}

			getBorderLayoutHeight();
			this.listBoxDocumentDetails.setHeight(this.borderLayoutHeight - 210 + "px");
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
	@SuppressWarnings("unchecked")
	public void onClick$btnNew_DocumentDetails(Event event) throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
		map.put("documentDetailDialogCtrl", this);
		if(!isNotFinanceProcess){
			map.put("custDetails", getCustomerBasicDetails());
			map.put("financeDetail", getFinanceDetail());
		}
		map.put("roleCode", getRole());
		map.put("moduleDefiner", moduleDefiner);
		map.put("isNotFinanceProcess", isNotFinanceProcess);

		FinanceCheckListReferenceDialogCtrl dialogCtrl = null;
		if (getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl") != null) {
			dialogCtrl = (FinanceCheckListReferenceDialogCtrl) getFinanceMainDialogCtrl().getClass()
					.getMethod("getFinanceCheckListReferenceDialogCtrl").invoke(getFinanceMainDialogCtrl());
			if (dialogCtrl != null) {
				Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass()
						.getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
				map.put("checkListDocTypeMap", checkListDocTypeMap);
			}
		}

		map.put("isFacility", false);
		map.put("window", window_documentDetailDialog);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/DocumentTypeSelectDialog.zul",
				window_documentDetailDialog, map);
		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("unchecked")
	public void createNewDocument(CheckListDetail checkListDetail, boolean isCheckList)
			throws InterruptedException, SecurityException, IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DocumentDetailDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("isCheckList", isCheckList);
		if (getFinanceDetail() != null) {
			map.put("isDocAllowedForInput", isDocAllowedForInput(checkListDetail.getDocType(),checkListDetail.getCheckListId()));
		} else {
			if (isNotFinanceProcess) {
				map.put("isDocAllowedForInput", true);
			}
		}
		
		List<Object> list = getCustomerBasicDetails();
		if (checkListDetail != null && checkListDetail.isDocIsCustDOC()) {
			CustomerDocument customerDocument = new CustomerDocument();
			customerDocument.setNewRecord(true);
			customerDocument.setCustDocCategory(checkListDetail.getDocType());
			customerDocument.setLovDescCustDocCategory(checkListDetail.getAnsDesc());
			customerDocument.setWorkflowId(0);
			customerDocument.setCustID(list != null ? Long.valueOf(list.get(0).toString()) :  0);
			customerDocument.setLovDescCustCIF(list != null ? String.valueOf(list.get(1).toString()) : "");
			customerDocument.setLovDescCustShrtName(list != null ? String.valueOf(list.get(2).toString()) : "");

			Filter[] countrysystemDefault = new Filter[1];
			countrysystemDefault[0] = new Filter("SystemDefault", "1", Filter.OP_EQUAL);
			Object countryObj = PennantAppUtil.getSystemDefault("Country", "", countrysystemDefault);

			if (countryObj != null) {
				Country country = (Country) countryObj;
				customerDocument.setCustDocIssuedCountry(country.getCountryCode());
				customerDocument.setLovDescCustDocIssuedCountry(country.getCountryDesc());
			}

			if ((PennantConstants.CPRCODE.equals(checkListDetail.getDocType()) || PennantConstants.PASSPORT
					.equals(checkListDetail.getDocType())) && getFinanceMainDialogCtrl() != null) {
				if (getFinanceMainDialogCtrl().getClass().getMethod("getCustomerIDNumber", String.class) != null) {
					String idNumber = (String) getFinanceMainDialogCtrl().getClass()
							.getMethod("getCustomerIDNumber", String.class)
							.invoke(getFinanceMainDialogCtrl(), checkListDetail.getDocType());
					customerDocument.setCustDocTitle(idNumber);
				}
			}
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
			map.put("customerDocument", doSetDocumentTypeMandProp(customerDocument));
		} else {
			DocumentDetails documentDetails = new DocumentDetails();
			if (checkListDetail != null) {
				documentDetails.setDocCategory(checkListDetail.getDocType());
			} else {
				documentDetails.setDocCategory("");
			}
			documentDetails.setNewRecord(true);
			documentDetails.setWorkflowId(0);
			map.put("finDocumentDetail", documentDetails);
		}

		Component component = window_documentDetailDialog;

		FinanceCheckListReferenceDialogCtrl dialogCtrl = null;
		if (getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl") != null) {
			dialogCtrl = (FinanceCheckListReferenceDialogCtrl) getFinanceMainDialogCtrl().getClass()
					.getMethod("getFinanceCheckListReferenceDialogCtrl").invoke(getFinanceMainDialogCtrl());
			if (dialogCtrl != null) {
				Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl.getClass()
						.getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
				map.put("checkListDocTypeMap", checkListDocTypeMap);
			}
			if (isCheckList) {
				component = dialogCtrl.window_FinanceCheckListReferenceDialog;
			}
		}

		try {

			if (checkListDetail != null && checkListDetail.isDocIsCustDOC()) {
				Executions.createComponents(
						"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceDocuments/FinDocumentDetailDialog.zul",
						component, map);
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
		ArrayList<ValueLabel> documentTypes = PennantAppUtil.getDocumentTypes();
		List<DocumentDetails> sortdocumentDetails = new ArrayList<DocumentDetails>();
		sortdocumentDetails.addAll(sortDocumentDetails(documentDetails));

		for (DocumentDetails documentDetail : sortdocumentDetails) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(getlabelDesc(documentDetail.getDocCategory(), documentTypes));
			listitem.appendChild(listcell);
			listcell = new Listcell(documentDetail.getDocName());
			listitem.appendChild(listcell);
			listcell = new Listcell(DateUtility.formatToLongDate(documentDetail.getDocReceivedDate()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantJavaUtil.getLabel(documentDetail.getRecordType()));
			listitem.appendChild(listcell);
			listitem.setAttribute("data", documentDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinDocumentItemDoubleClicked");
			if (!documentDetail.isDocIsCustDoc()) {
				this.listBoxDocumentDetails.appendChild(listitem);
			}
			docDetailMap.put(documentDetail.getDocCategory(), documentDetail);
		}
		logger.debug("Leaving");
	}

	public static String getlabelDesc(String value, List<ValueLabel> list) {
		for (ValueLabel valueLabel : list) {
			if (valueLabel.getValue().equalsIgnoreCase(value)) {
				return valueLabel.getLabel();
			}
		}
		return "";
	}

	public void onFinDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxDocumentDetails.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			DocumentDetails finDocumentDetail = (DocumentDetails) item.getAttribute("data");
			if (StringUtils.trimToEmpty(finDocumentDetail.getRecordType()).equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				updateExistingDocument(finDocumentDetail, 0, false);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public boolean isDocAllowedForInput(String docCategory,long checklistID) {
		logger.debug("Entering");
		List<FinanceReferenceDetail> list = getFinanceDetail().getCheckList();
		if (list != null && !list.isEmpty()) {
			String roleCode = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain()
					.getNextRoleCode());

			for (FinanceReferenceDetail financeReferenceDetail : list) {

				List<CheckListDetail> checkListDetails = financeReferenceDetail.getLovDesccheckListDetail();

				if (checkListDetails != null && !checkListDetails.isEmpty()) {

					for (CheckListDetail checkListDetail : checkListDetails) {

						if(checklistID != checkListDetail.getCheckListId()){
							continue;
						}

						String docType = StringUtils.trimToEmpty(checkListDetail.getDocType());

						if (StringUtils.trimToEmpty(docType).equalsIgnoreCase(docCategory)) {
							String[] roleList;
							boolean isExist = false;
							if (roleCode != null && roleCode.contains(",")) {
								roleList = roleCode.split(",");
								for (String roleCd : roleList) {
									isExist = StringUtils
											.trimToEmpty(financeReferenceDetail.getAllowInputInStage()).contains(
													roleCd);
									return isExist;
								}
							}
							return StringUtils.trimToEmpty(financeReferenceDetail.getAllowInputInStage()).contains(
									roleCode);
						}

					
					}
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	@SuppressWarnings("unchecked")
	public void updateExistingDocument(DocumentDetails finDocumentDetail, long checklistID, boolean viewProcess)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finDocumentDetail", finDocumentDetail);
		map.put("DocumentDetailDialogCtrl", this);
		map.put("roleCode", getRole());
		map.put("moduleType", "");
		map.put("enqiryModule", enqiryModule);
		map.put("isCheckList", finDocumentDetail.isDocIsCustDoc() ? true : (checklistID > 0 ? true : false));
		map.put("customerDialogCtrl", this);
		// map.put("newRecord", "true");
		if (getFinanceDetail() != null) {
			map.put("isDocAllowedForInput", isDocAllowedForInput(finDocumentDetail.getDocCategory(),checklistID));
		} else {
			if (isNotFinanceProcess) {
				map.put("isDocAllowedForInput", true);
			}
		}
		List<Object> list = getCustomerBasicDetails();
		if (!finDocumentDetail.isDocReceived() && finDocumentDetail.getDocImage() == null) {
			if (finDocumentDetail.isDocIsCustDoc()) {
				finDocumentDetail = getCustomerDocumentService().getCustDocByCustAndDocType(list != null ? Long.valueOf(list.get(0).toString()) :  0,
						finDocumentDetail.getDocCategory());
			} else {
				finDocumentDetail = getFinanceDetailService().getFinDocDetailByDocId(finDocumentDetail.getDocId(), "_View", true);
				map.put("finDocumentDetail", finDocumentDetail);
			}
			if (finDocumentDetail.getDocRefId() != Long.MIN_VALUE) {
				finDocumentDetail.setDocImage(PennantAppUtil.getDocumentImage(finDocumentDetail.getDocRefId()));
			}
		}

		CustomerDocument customerDocument = null;
		if (customerDocument == null && finDocumentDetail.isDocIsCustDoc()) {
			customerDocument = new CustomerDocument();

			customerDocument.setCustID(list != null ? Long.valueOf(list.get(0).toString()) :  0);
			customerDocument.setLovDescCustCIF(list != null ? String.valueOf(list.get(1).toString()) : "");
			customerDocument.setLovDescCustShrtName(list != null ? String.valueOf(list.get(2).toString()) : "");
			customerDocument.setCustDocImage(finDocumentDetail.getDocImage());
			customerDocument.setDocRefId(finDocumentDetail.getDocRefId());
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
			customerDocument.setVersion(finDocumentDetail.getVersion());
			customerDocument.setWorkflowId(0);
		}

		try {

			Component component = window_documentDetailDialog;

			FinanceCheckListReferenceDialogCtrl dialogCtrl = null;
			if (getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl") != null) {
				dialogCtrl = (FinanceCheckListReferenceDialogCtrl) getFinanceMainDialogCtrl().getClass()
						.getMethod("getFinanceCheckListReferenceDialogCtrl").invoke(getFinanceMainDialogCtrl());
				if (dialogCtrl != null) {
					Map<String, List<Listitem>> checkListDocTypeMap = (Map<String, List<Listitem>>) dialogCtrl
							.getClass().getMethod("getCheckListDocTypeMap").invoke(dialogCtrl);
					map.put("checkListDocTypeMap", checkListDocTypeMap);
				}
				if (checklistID > 0) {
					component = dialogCtrl.window_FinanceCheckListReferenceDialog;
				}
			}

			if (finDocumentDetail.isDocIsCustDoc()) {
				map.put("customerDocument", doSetDocumentTypeMandProp(customerDocument));
				map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
				Executions.createComponents(
						"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceDocuments/FinDocumentDetailDialog.zul",
						component, map);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Update Finance Main Details from the Finance Main Ctrl
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object> getCustomerBasicDetails() {
		try {
			List<Object> custBasicDetails = (List<Object>) getFinanceMainDialogCtrl().getClass().getMethod("getCustomerBasicDetails")
					.invoke(getFinanceMainDialogCtrl());
			if (custBasicDetails != null) {
				return custBasicDetails;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			map.put("moduleName", moduleName);
			if(isNotFinanceProcess){
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",this.finBasicdetails, map);
			}else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public CustomerDialogCtrl fetchCustomerDialogCtrl() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.debug("Entering");
		CustomerDialogCtrl customerDialogCtrl = null;
		if (getFinanceMainDialogCtrl().getClass().getMethod("getCustomerDialogCtrl") != null) {
			customerDialogCtrl = (CustomerDialogCtrl) getFinanceMainDialogCtrl().getClass()
					.getMethod("getCustomerDialogCtrl").invoke(getFinanceMainDialogCtrl());
		}
		logger.debug("Leaving");
		return customerDialogCtrl;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		if(isNotFinanceProcess){
			getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}else{
			getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}
	}

	// method for sorting document details
	private List<DocumentDetails> sortDocumentDetails(List<DocumentDetails> documentDetails) {
		if (documentDetails != null && documentDetails.size() > 0) {
			Collections.sort(documentDetails, new Comparator<DocumentDetails>() {

				@Override
				public int compare(DocumentDetails detail1, DocumentDetails detail2) {
					return detail1.getDocCategory().compareTo(detail2.getDocCategory());
				}
			});
		}

		return documentDetails;
	}

	private CustomerDocument doSetDocumentTypeMandProp(CustomerDocument customerDocument) {
		logger.debug("Entering");
		if (customerDocument != null) {
			JdbcSearchObject<DocumentType> jdbcSearchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
			jdbcSearchObject.addField("DocExpDateIsMand");
			jdbcSearchObject.addField("DocIssueDateMand");
			jdbcSearchObject.addField("DocIdNumMand");
			jdbcSearchObject.addField("DocIssuedAuthorityMand");
			jdbcSearchObject.addTabelName("BMTDocumentTypes");
			jdbcSearchObject.addFilterEqual("DocTypeCode", customerDocument.getCustDocCategory());
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<DocumentType> docTypeLIst = pagedListService.getBySearchObject(jdbcSearchObject);
			if (docTypeLIst != null && !docTypeLIst.isEmpty()) {
				DocumentType documentType = docTypeLIst.get(0);
				customerDocument.setLovDescdocExpDateIsMand(documentType.isDocExpDateIsMand());
				customerDocument.setDocIssueDateMand(documentType.isDocIssueDateMand());
				customerDocument.setDocIdNumMand(documentType.isDocIdNumMand());
				customerDocument.setDocIssuedAuthorityMand(documentType.isDocIssuedAuthorityMand());
			}
		}
		logger.debug("Leaving");
		return customerDocument;
	}
		
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}
	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}
	
}
