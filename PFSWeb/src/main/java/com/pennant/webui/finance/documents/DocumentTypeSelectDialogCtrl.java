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
 * FileName    		:  DocumentDetailsDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.documents;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.facility.facility.FacilityDocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/Contributor/DocumentDetailsDialog.zul file.
 */
public class DocumentTypeSelectDialogCtrl extends GFCBaseCtrl<DocumentDetails> {
	private static final long serialVersionUID = -6959194080451993569L;
	private static final Logger logger = Logger.getLogger(DocumentTypeSelectDialogCtrl.class);

	protected Window	                  window_DocumentTypeSelectDialog;	
	//protected Combobox	                  docCategory;	                                                                 // autowired

	ExtendedCombobox            docCategory;                                                                  //autowired

	private DocumentDetails documentDetail;
	private List<DocumentType> documentTypes = PennantAppUtil.getDocumentTypesList();

	private DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private FacilityDocumentDetailDialogCtrl facilityDocumentDetailDialogCtrl;
	private Component window_documentDetailDialog;
	private FinanceDetail financeDetail;
	private Facility facility;
	private boolean isFacility = false;
	private boolean isNotFinanceProcess = false;
	private Map<String, List<Listitem>> checkListDocTypeMap = null;
	private List<Object> custDetails = null;
	private String module;
	
	/**
	 * default constructor.<br>
	 */
	public DocumentTypeSelectDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected DocumentDetails object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_DocumentTypeSelectDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DocumentTypeSelectDialog);

		try {

			if (arguments.containsKey("custDetails")) {
				setCustDetails((List<Object>)arguments.get("custDetails"));
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (arguments.containsKey("isFacility")) {
				setFacility((Boolean) arguments.get("isFacility"));
			}
			
			if (arguments.containsKey("isNotFinanceProcess")) {
				isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
			}
			
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}
						
			if (arguments.containsKey("checkListDocTypeMap")) {
				checkListDocTypeMap = (Map<String, List<Listitem>>) arguments
						.get("checkListDocTypeMap");
			}

			if (arguments.containsKey("documentDetailDialogCtrl")) {
				if (isFacility()) {
					if (arguments.containsKey("facility")) {
						setFacility((Facility) arguments.get("facility"));
					}
					this.facilityDocumentDetailDialogCtrl = (FacilityDocumentDetailDialogCtrl) arguments
							.get("documentDetailDialogCtrl");
				} else {
					this.documentDetailDialogCtrl = (DocumentDetailDialogCtrl) arguments
							.get("documentDetailDialogCtrl");
				}
			}

			if (arguments.containsKey("window")) {
				this.window_documentDetailDialog = (Component) arguments.get("window");
			}
			
			// Module
			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}
			
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_DocumentTypeSelectDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void onClick$btn_proceed(Event event) throws InterruptedException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		doProceed();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDocumentDetails
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			doSetFieldProperties();

			this.window_DocumentTypeSelectDialog.setHeight("150px");
			this.window_DocumentTypeSelectDialog.setWidth("80%");
			this.window_DocumentTypeSelectDialog.doModal();

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_DocumentTypeSelectDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}



	private void doSetFieldProperties() {
		logger.debug("Entering ");
		this.docCategory.setMaxlength(50);
		this.docCategory.setTextBoxWidth(160);
		this.docCategory.setMandatoryStyle(true);
		this.docCategory.setModuleName("DocumentType");
		this.docCategory.setValueColumn("DocTypeCode");
		this.docCategory.setDescColumn("DocTypeDesc");
		this.docCategory.setValidateColumns(new String[]{"DocTypeCode"});
		
		if (DocumentCategories.VERIFICATION_FI.getKey().equals(module)
				|| DocumentCategories.VERIFICATION_TV.getKey().equals(module)
				|| DocumentCategories.VERIFICATION_LV.getKey().equals(module)
				|| DocumentCategories.VERIFICATION_RCU.getKey().equals(module)
				|| DocumentCategories.FINANCE.getKey().equals(module)
				|| DocumentCategories.COLLATERAL.getKey().equals(module)) {
			this.docCategory.setFilters(new Filter[] { new Filter("CategoryCode", module, Filter.OP_EQUAL) });
		} else {
			if(module==null){
				Filter[] filters = new Filter[1];
				List<String> types=new ArrayList<>();
				types.add(DocumentCategories.FINANCE.getKey());
				types.add(DocumentCategories.COLLATERAL.getKey());
				filters[0]= Filter.in("CategoryCode", types.toArray(new String[types.size()]));
				this.docCategory.setFilters(filters);
			}else{				
				this.docCategory.setFilters(new Filter[] { new Filter("CategoryCode",DocumentCategories.CUSTOMER.getKey(), Filter.OP_EQUAL) });
			}
		}
		
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDocumentDetails
	 *            DocumentDetails
	 */
	public void doWriteBeanToComponents(DocumentDetails aDocumentDetails) {
		logger.debug("Entering");
		aDocumentDetails = new DocumentDetails();
		this.docCategory.setValue(aDocumentDetails.getDocCategory());
		
		logger.debug("Leaving");
	}
	


	public DocumentDetails getDocumentDetails(){
		logger.debug("Entering");
		DocumentDetails aDocumentDetails = new DocumentDetails();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.docCategory.getValue() == null || this.docCategory.getValue().equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.docCategory, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinDocumentDetailDialog_DocCategory.value") }));
			} else {
				this.docCategory.setReadonly(true);
			}
			aDocumentDetails.setDocCategory(this.docCategory.getValue());
			aDocumentDetails.setLovDescDocCategoryName(this.docCategory.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
		return aDocumentDetails;
	} 
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.docCategory.setErrorMessage("");
		this.docCategory.setConstraint("");

		logger.debug("Leaving");
	}

	public void doProceed() throws IllegalArgumentException, SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, InterruptedException {
		logger.debug("Entering");

		this.docCategory.setErrorMessage("");
		this.docCategory.setConstraint(new PTStringValidator(Labels.getLabel("label_FinDocumentDetailDialog_DocCategory.value"),null,true,true));

		DocumentType doctype = (DocumentType) this.docCategory.getObject();

		final HashMap<String, Object> map = new HashMap<String, Object>();
		if(isFacility()){
			map.put("DocumentDetailDialogCtrl", this.facilityDocumentDetailDialogCtrl);
		}else{
			map.put("DocumentDetailDialogCtrl", this.documentDetailDialogCtrl);
		}
		map.put("newRecord", "true");
		map.put("roleCode", getRole());

		if (checkListDocTypeMap != null) {
			map.put("checkListDocTypeMap", checkListDocTypeMap);
		}
		try {
			this.window_DocumentTypeSelectDialog.onClose();
			if(!(DocumentCategories.CUSTOMER.getKey().equals(doctype.getCategoryCode()))){
				DocumentDetails documentDetails = getDocumentDetails();
				documentDetails.setNewRecord(true);
				documentDetails.setWorkflowId(0);
				map.put("finDocumentDetail", documentDetails);
				map.put("isCheckList",false);
				map.put("docIsMandatory", false);
				if(getFinanceDetail() != null){
					map.put("isDocAllowedForInput", isDocAllowedForInput(doctype.getDocTypeCode()));
				}else{
					if(isNotFinanceProcess){
						map.put("isDocAllowedForInput", true);
					}
				}
				if(isFacility()){
					Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDocDetailDialog.zul", window_documentDetailDialog, map);
				}else{
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceDocuments/FinDocumentDetailDialog.zul", window_documentDetailDialog, map);
				}
			}else{
				CustomerDocument customerDocument = new CustomerDocument();
				customerDocument.setNewRecord(true);
				//customerDocument.setCustDocCategory(checkListDetail.getDocType());
				//	customerDocument.setLovDescCustDocCategory(checkListDetail.getAnsDesc());
				customerDocument.setWorkflowId(0);
				if(isFacility()){
					customerDocument.setCustID(getFacility().getCustID());
					customerDocument.setLovDescCustCIF(getFacility().getCustCIF());
					customerDocument.setLovDescCustShrtName(getFacility().getCustShrtName());
				}else{
					customerDocument.setCustID(getCustDetails() != null ? Long.valueOf(getCustDetails().get(0).toString()) :  0);
					customerDocument.setLovDescCustCIF(getCustDetails() != null ? String.valueOf(getCustDetails().get(1).toString()) : "");
					customerDocument.setLovDescCustShrtName(getCustDetails() != null ? String.valueOf(getCustDetails().get(2).toString()) : "");
					//if  documents type is Customer Documents then set mandatory from documents masters.
					for (DocumentType document : documentTypes){
						if(this.docCategory.getValue()!=null && this.docCategory.getValue().equals(document.getDocTypeCode())){
							customerDocument.setLovDescdocExpDateIsMand(document.isDocExpDateIsMand());
							customerDocument.setDocIdNumMand(document.isDocIdNumMand());
							customerDocument.setDocIssueDateMand(document.isDocIssueDateMand());
							customerDocument.setDocIssuedAuthorityMand(document.isDocIssuedAuthorityMand());
							break;
						}
					}
					if((PennantConstants.CPRCODE.equals(doctype.getDocTypeCode()) || 
							PennantConstants.PASSPORT.equals(doctype.getDocTypeCode()) || 
							PennantConstants.TRADELICENSE.equals(doctype.getDocTypeCode())) && documentDetailDialogCtrl != null && 
							documentDetailDialogCtrl.getFinanceMainDialogCtrl() != null){
						if (documentDetailDialogCtrl.getFinanceMainDialogCtrl().getClass().getMethod("getCustomerIDNumber",String.class) != null) {
							String idNumber  = (String)documentDetailDialogCtrl.getFinanceMainDialogCtrl().getClass().
									getMethod("getCustomerIDNumber",String.class).invoke(documentDetailDialogCtrl.
											getFinanceMainDialogCtrl(),doctype.getDocTypeCode());
							customerDocument.setCustDocTitle(idNumber);
						}
					}
				}
				customerDocument.setCustDocCategory(doctype.getDocTypeCode());
				customerDocument.setLovDescCustDocCategory(doctype.getDocTypeDesc());
				
				Filter[] countrysystemDefault=new Filter[1];
				countrysystemDefault[0]=new Filter("SystemDefault", 1 ,Filter.OP_EQUAL);
				Object countryObj=	PennantAppUtil.getSystemDefault("Country","", countrysystemDefault);

				if (countryObj!=null) {
					Country country=(Country) countryObj;
					customerDocument.setCustDocIssuedCountry(country.getCountryCode());
					customerDocument.setLovDescCustDocIssuedCountry(country.getCountryDesc());
				}
				map.put("isDocAllowedForInput", true);
				map.put("isCheckList",true);
				map.put("customerDocument", customerDocument);
				if(documentDetailDialogCtrl != null && documentDetailDialogCtrl.getFinanceMainDialogCtrl() != null){
					map.put("financeMainDialogCtrl", documentDetailDialogCtrl.getFinanceMainDialogCtrl());
				}
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	
	public boolean isDocAllowedForInput(String docCategory){
		List<FinanceReferenceDetail> financeReferenceDetails = getFinanceDetail().getCheckList();
		if(financeReferenceDetails != null && !financeReferenceDetails.isEmpty()){ 
			String roleCode=StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode());
			
			for (FinanceReferenceDetail financeReferenceDetail : financeReferenceDetails) {
				
				List<CheckListDetail> checkListDetails = financeReferenceDetail.getLovDesccheckListDetail();
				
				if(checkListDetails != null && !checkListDetails.isEmpty()){
					
					for (CheckListDetail checkListDetail : checkListDetails) {
						
						if(StringUtils.trimToEmpty(checkListDetail.getDocType()).equalsIgnoreCase(docCategory)){
							return StringUtils.trimToEmpty(financeReferenceDetail.getAllowInputInStage()).contains(roleCode);
						}
					}
					
				}
			}
		}
		return false;
	}

	public void setDocumentDetail(DocumentDetails documentDetail) {
		this.documentDetail = documentDetail;
	}

	public DocumentDetails getDocumentDetail() {
		return documentDetail;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public boolean isFacility() {
		return isFacility;
	}

	public void setFacility(boolean isFacility) {
		this.isFacility = isFacility;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public List<Object> getCustDetails() {
		return custDetails;
	}

	public void setCustDetails(List<Object> custDetails) {
		this.custDetails = custDetails;
	}

}
