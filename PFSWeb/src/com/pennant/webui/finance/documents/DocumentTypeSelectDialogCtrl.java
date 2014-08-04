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

import java.io.Serializable;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.facility.facility.FacilityDocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/Contributor/DocumentDetailsDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class DocumentTypeSelectDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long	          serialVersionUID	      = -6959194080451993569L;
	private final static Logger	          logger	              = Logger.getLogger(DocumentTypeSelectDialogCtrl.class);

	protected Window	                  window_DocumentTypeSelectDialog;	
	protected Combobox	                  docCategory;	                                                                 // autowired


	// Button controller for the CRUD buttons
	private transient final String	      btnCtroller_ClassPrefix	= "button_DocumentDetailsDialog_";
	private transient ButtonStatusCtrl	  btnCtrl;
	protected Button	                  btnNew;	                                                                     // autowire
	protected Button	                  btnEdit;	                                                                     // autowire
	protected Button	                  btnDelete;	                                                                 // autowire
	protected Button	                  btnSave;	                                                                     // autowire
	protected Button	                  btnCancel;	                                                                 // autowire
	protected Button	                  btnClose;	                                                                 // autowire
	protected Button	                  btnHelp;	                                                                     // autowire
	protected Button	                  btnNotes;	   

	private DocumentDetails	              documentDetail;	
	private List<DocumentType>	documentTypes	      = PennantAppUtil.getDocumentTypesList();

	private DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private FacilityDocumentDetailDialogCtrl facilityDocumentDetailDialogCtrl;
	private Component window_documentDetailDialog;
	private FinanceMain financeMain;
	private FinanceDetail financeDetail;
	private Facility facility;
	private boolean isFacility = false;
	private Map<String, List<Listitem>>  checkListDocTypeMap = null;
	/**
	 * default constructor.<br>
	 */
	public DocumentTypeSelectDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
		logger.debug("Entering" + event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !

		if (args.containsKey("financeMain")) {
			setFinanceMain((FinanceMain) args.get("financeMain"));
		}
		
		if (args.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) args.get("financeDetail"));
		}

		if (args.containsKey("isFacility")) {
			setFacility((Boolean)args.get("isFacility"));
		}

		if (args.containsKey("checkListDocTypeMap")) {
			checkListDocTypeMap = (Map<String, List<Listitem>>) args.get("checkListDocTypeMap");
		}
		
		if (args.containsKey("documentDetailDialogCtrl")) {
			if(isFacility()){
				if (args.containsKey("facility")) {
					setFacility((Facility) args.get("facility"));
				}
				this.facilityDocumentDetailDialogCtrl =  (FacilityDocumentDetailDialogCtrl) args.get("documentDetailDialogCtrl");
			}
			else{
				this.documentDetailDialogCtrl =  (DocumentDetailDialogCtrl) args.get("documentDetailDialogCtrl");
			}
		}

		if (args.containsKey("window")) {
			this.window_documentDetailDialog =  (Component) args.get("window");
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		doShowDialog();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDocumentDetails
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// fill the components with the data
			fillComboBox(this.docCategory, "", documentTypes);

			this.window_DocumentTypeSelectDialog.setHeight("30%");
			this.window_DocumentTypeSelectDialog.setWidth("50%");
			this.window_DocumentTypeSelectDialog.doModal();

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
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
		fillComboBox(this.docCategory, aDocumentDetails.getDocCategory(), documentTypes);

		logger.debug("Leaving");
	}
	private void fillComboBox(Combobox combobox, String value, List<DocumentType> list) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (DocumentType documentType : list) {
			comboitem = new Comboitem();
			comboitem.setValue(documentType.getDocTypeCode());
			comboitem.setLabel(documentType.getDocTypeDesc());
			comboitem.setAttribute("data", documentType);
			if (documentType.isDocIsCustDoc()) {
				if (checkListDocTypeMap != null && checkListDocTypeMap.containsKey(documentType.getDocTypeCode())) {
					combobox.appendChild(comboitem);
				}
			} else {
				combobox.appendChild(comboitem);
			}

		}
		logger.debug("Leaving fillComboBox()");
	}


	public DocumentDetails getDocumentDetails(){
		logger.debug("Entering");
		DocumentDetails aDocumentDetails = new DocumentDetails();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.docCategory.getSelectedItem() == null || this.docCategory.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.docCategory, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinDocumentDetailDialog_DocCategory.value") }));
			} else {
				this.docCategory.getSelectedItem().setDisabled(true);
			}
			aDocumentDetails.setDocCategory(this.docCategory.getSelectedItem().getValue().toString());
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
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");

		this.window_DocumentTypeSelectDialog.onClose();

		logger.debug("Leaving");
	}



	public void doProceed() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException{
		logger.debug("Entering");
		if(this.docCategory.getSelectedItem().getValue().toString().equals("#")){
			throw new WrongValueException(this.docCategory, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_FinDocumentDetailDialog_DocCategory.value") }));
		}
		Comboitem comboitem = this.docCategory.getSelectedItem();		
		DocumentType doctype=(DocumentType) comboitem.getAttribute("data");

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
			if(!doctype.isDocIsCustDoc()){
				DocumentDetails documentDetails = getDocumentDetails();
				documentDetails.setNewRecord(true);
				documentDetails.setWorkflowId(0);
				map.put("finDocumentDetail", documentDetails);
				map.put("isCheckList",false);
				if(getFinanceDetail() != null){
					map.put("isDocAllowedForInput", isDocAllowedForInput(doctype.getDocTypeCode()));
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
					customerDocument.setCustID(getFinanceMain().getCustID());
					customerDocument.setLovDescCustCIF(getFinanceMain().getLovDescCustCIF());
					customerDocument.setLovDescCustShrtName(getFinanceMain().getLovDescCustShrtName());
				}
				customerDocument.setCustDocCategory(doctype.getDocTypeCode());
				customerDocument.setLovDescCustDocCategory(doctype.getDocTypeDesc());
				PFSParameter parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_COUNTRY");
				customerDocument.setCustDocIssuedCountry(parameter.getSysParmValue().trim());
				customerDocument.setLovDescCustDocIssuedCountry(parameter.getSysParmDescription());
				map.put("isCheckList",true);
				map.put("customerDocument", customerDocument);
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
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
	

	public void setDocumentDetail(DocumentDetails documentDetail) {
		this.documentDetail = documentDetail;
	}

	public DocumentDetails getDocumentDetail() {
		return documentDetail;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
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

}
