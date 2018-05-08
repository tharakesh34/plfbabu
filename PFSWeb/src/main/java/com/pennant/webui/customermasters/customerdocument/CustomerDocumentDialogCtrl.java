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
 * FileName    		:  CustomerDocumentDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customerdocument;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.CustomerViewDialogCtrl;
import com.pennant.webui.delegationdeviation.DeviationExecutionCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerDocument/customerDocumentDialog.zul
 * file.
 */
public class CustomerDocumentDialogCtrl extends GFCBaseCtrl<CustomerDocument> {
	private static final long serialVersionUID = -8931742880858169171L;
	private static final Logger logger = Logger.getLogger(CustomerDocumentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerDocumentDialog; 
	protected Grid   grid_basicDetails;				

	protected Longbox 	custID; 					
	protected Textbox	documnetName;	            
	protected ExtendedCombobox 	custDocType; 				
	protected Uppercasebox 	custDocTitle; 				
	protected Textbox 	custDocSysName; 			
	protected Datebox 	custDocRcvdOn; 				
	protected Datebox 	custDocExpDate; 			
	protected Datebox 	custDocIssuedOn; 			
	protected ExtendedCombobox 	custDocIssuedCountry; 		
	protected Checkbox 	custDocIsVerified; 			
	protected Longbox 	custDocVerifiedBy; 			
	protected Checkbox 	custDocIsAcrive; 			
	protected Textbox 	custCIF;					
	protected Label 	custShrtName;				
	protected Textbox 	lovDescCustDocVerifiedBy; 	
	protected Button	btnUploadDoc;
	protected Iframe	finDocumentPdfView;
	protected Div		finDocumentDiv;	            
	protected Div       docDiv;
	protected Space		space_documnetName;	

	protected Space     space_CustDocExpDate;       
	protected Space     space_custDocIssuedOn;		
	protected Space     space_CustIDNumber;			
	protected Space     space_CustDocSysName;     
	protected Row       passwordRow;
	protected Textbox   pdfPassword;
	// not auto wired variables
	private CustomerDocument customerDocument; // overHanded per parameter
	private transient CustomerDocumentListCtrl customerDocumentListCtrl; // overHanded per parameter
	private transient CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl;

	private transient boolean validationOn;

	protected Button btnSearchPRCustid; 


	String primaryIdRegex = null;

	String primaryIdLabel = null;


	// ServiceDAOs / Domain Classes
	private transient CustomerDocumentService customerDocumentService;
	private transient PagedListService pagedListService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerDocument> customerDocuments;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	protected JdbcSearchObject<SecurityUser> secUserSearchObj;
	private PagedListWrapper<SecurityUser> secUserPagedListWrapper;
	private String moduleType="";
	private Object documentDetailDialogCtrl=null;
	private boolean   isCheckList = false;
	private boolean 		viewProcess = false;
	private Map<String, List<Listitem>>  checkListDocTypeMap = null;
	private List<DocumentDetails> documentDetailList = null;
	private String userRole="";
	Date appStartDate = DateUtility.getAppDate();
	Date endDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	private boolean isFinanceProcess = false;
	private Object financeMainDialogCtrl = null;
	private boolean   expDateIsMand = false;
	private boolean   isIssueDateMand = false;
	private boolean   isIdNumMand = false;
	private boolean   isRetailCustomer = false;
	private boolean   isIssuedAuth=false;
	private boolean   isDocuploadMand=false;
	private DeviationExecutionCtrl deviationExecutionCtrl; 
	/**
	 * default constructor.<br>
	 */
	public CustomerDocumentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerDocumentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerDocument
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerDocumentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerDocumentDialog);

		try {
			setSecUserPagedListWrapper();


			if (arguments.containsKey("customerDocument")) {
				Cloner cloner = new Cloner();
				this.customerDocument = cloner
						.deepClone((CustomerDocument) arguments
								.get("customerDocument"));
				CustomerDocument befImage = new CustomerDocument();
				BeanUtils.copyProperties(this.customerDocument, befImage);
				this.customerDocument.setBefImage(befImage);
				setCustomerDocument(this.customerDocument);
			} else {
				setCustomerDocument(null);
			}

			if (arguments.containsKey("DocumentDetailDialogCtrl")) {
				this.documentDetailDialogCtrl = arguments
						.get("DocumentDetailDialogCtrl");
				DocumentDetailDialogCtrl docDetailDialogCtrl = (DocumentDetailDialogCtrl) (this.documentDetailDialogCtrl);
				setCustomerDialogCtrl(docDetailDialogCtrl.fetchCustomerDialogCtrl());
				if(getCustomerDialogCtrl() != null && getCustomerDocument() != null){
					isRetailCustomer = getCustomerDialogCtrl().isRetailCustomer();
					if(StringUtils.equals(PennantConstants.CPRCODE, getCustomerDocument().getCustDocCategory())){
						getCustomerDocument().setCustDocTitle(getCustomerDialogCtrl().getCustIDNumber(PennantConstants.CPRCODE));
					}else if(StringUtils.equals(PennantConstants.TRADELICENSE, getCustomerDocument().getCustDocCategory())){
						getCustomerDocument().setCustDocTitle(getCustomerDialogCtrl().getCustIDNumber(PennantConstants.TRADELICENSE));
					}
				}
			}

			if (arguments.containsKey("viewProcess")) {
				viewProcess = (Boolean) arguments.get("viewProcess");
			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments
						.get("financeMainDialogCtrl");
			}

			if (arguments.containsKey("creditApplicationReviewDialogCtrl")) {
				creditApplicationReviewDialogCtrl = (CreditApplicationReviewDialogCtrl) arguments
						.get("creditApplicationReviewDialogCtrl");
			}

			if (getCustomerDocument().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("isCheckList")) {
				this.isCheckList = (Boolean) arguments.get("isCheckList");
			} else {
				if (arguments.containsKey("customerDialogCtrl")) {
					setCustomerDialogCtrl((CustomerDialogCtrl) arguments
							.get("customerDialogCtrl"));
					setNewCustomer(true);

					if (arguments.containsKey("newRecord")) {
						setNewRecord(true);
					} else {
						setNewRecord(false);
					}

					this.customerDocument.setWorkflowId(0);
				}
			}
			
			if (arguments.containsKey("isCheckList")) {
				this.isCheckList = (Boolean) arguments.get("isCheckList");
			} else {
				if (arguments.containsKey("customerViewDialogCtrl")) {
					setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments
							.get("customerViewDialogCtrl"));
					setNewCustomer(true);
					
					if (arguments.containsKey("newRecord")) {
						setNewRecord(true);
					} else {
						setNewRecord(false);
					}
					
					this.customerDocument.setWorkflowId(0);
				}
			}
			
			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole,
						"CustomerDocumentDialog");
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (arguments.containsKey("isRetailCustomer")) {
				this.isRetailCustomer = (Boolean) arguments.get("isRetailCustomer");
			}

			if (arguments.containsKey("checkListDocTypeMap")) {
				checkListDocTypeMap = (Map<String, List<Listitem>>) arguments
						.get("checkListDocTypeMap");
			}

			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}

			if (enqiryModule) {
				this.moduleType = PennantConstants.MODULETYPE_ENQ;
			}

			doLoadWorkFlow(this.customerDocument.isWorkflow(),
					this.customerDocument.getWorkflowId(),
					this.customerDocument.getNextTaskId());

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CustomerDocumentDialog");
			}

			// READ OVERHANDED parameters !
			// we get the customerDocumentListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete customerDocument here.
			if (arguments.containsKey("customerDocumentListCtrl")) {
				setCustomerDocumentListCtrl((CustomerDocumentListCtrl) arguments
						.get("customerDocumentListCtrl"));
			} else {
				setCustomerDocumentListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			getBorderLayoutHeight();
			int dialogHeight = grid_basicDetails.getRows()
					.getVisibleItemCount() * 20 + 80;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			this.finDocumentPdfView.setHeight(listboxHeight + "px");

			doShowDialog(getCustomerDocument());

			// Calling SelectCtrl For proper selection of Customer
			if (isNewRecord() && !isNewCustomer() && !isCheckList) {
				// onload();
			}
			setDeviationExecutionCtrl();

			if (isCheckList) {// TODO Need to add a condition based visibility
				// for delete button
				btnDelete.setVisible(false);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerDocumentDialog.onClose();
		}
		logger.debug("Leaving" +event.toString());
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.custID.setMaxlength(19);
		this.custDocType.setMaxlength(50);
		this.custDocType.setMandatoryStyle(true);
		this.custDocType.setTextBoxWidth(110);
		this.custDocType.setModuleName("CustDocumentType");
		this.custDocType.setValueColumn("DocTypeCode");
		this.custDocType.setDescColumn("DocTypeDesc");
		this.custDocType.setValidateColumns(new String[] { "DocTypeCode" });

		this.custDocTitle.setMaxlength(100);
		this.custDocSysName.setMaxlength(100);
		this.custDocRcvdOn.setFormat(PennantConstants.dateTimeFormat);
		this.custDocExpDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custDocIssuedOn.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.custDocIssuedCountry.setMaxlength(2);
		this.custDocIssuedCountry.setMandatoryStyle(true);
		this.custDocIssuedCountry.getTextbox().setWidth("40px");
		this.custDocIssuedCountry.setModuleName("Country");
		this.custDocIssuedCountry.setValueColumn("CountryCode");
		this.custDocIssuedCountry.setDescColumn("CountryDesc");
		this.custDocIssuedCountry.setValidateColumns(new String[] { "CountryCode" });
		if(getCustomerDocument().isDocIsPasswordProtected()){
			this.passwordRow.setVisible(true);
		}


		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("CustomerDocumentDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerDocumentDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_CustomerDocumentDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		this.btnCancel.setVisible(false);
		if(isCheckList){
			this.btnDelete.setVisible(false);
		}
		if(creditApplicationReviewDialogCtrl != null && 
				!StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)){
			this.btnDelete.setVisible(false);
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.customerDocument.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerDocument
	 *            CustomerDocument
	 */
	public void doWriteBeanToComponents(CustomerDocument aCustomerDocument) {
		logger.debug("Entering");

		if(aCustomerDocument.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerDocument.getCustID());	
		}
		this.custDocType.setValue(aCustomerDocument.getCustDocCategory());

		if(PennantConstants.CPRCODE.equalsIgnoreCase(aCustomerDocument.getCustDocCategory())){
			this.custDocTitle.setValue(PennantApplicationUtil.formatEIDNumber(aCustomerDocument.getCustDocTitle()));
		}else{
			this.custDocTitle.setValue(aCustomerDocument.getCustDocTitle());
		}

		this.custDocSysName.setValue(aCustomerDocument.getCustDocSysName());
		this.pdfPassword.setValue(aCustomerDocument.getPdfPassWord());
		this.custDocRcvdOn.setValue(aCustomerDocument.getCustDocRcvdOn());
		this.custDocExpDate.setValue(aCustomerDocument.getCustDocExpDate());
		this.custDocIssuedOn.setValue(aCustomerDocument.getCustDocIssuedOn());
		this.custDocIssuedCountry.setValue(aCustomerDocument.getCustDocIssuedCountry());

		if(aCustomerDocument.isNew() && StringUtils.isBlank(this.custDocIssuedCountry.getValue())){
			Filter[] countrysystemDefault=new Filter[1];
			countrysystemDefault[0]=new Filter("SystemDefault", 1,Filter.OP_EQUAL);
			Object countryObj=	PennantAppUtil.getSystemDefault("Country","", countrysystemDefault);

			if (countryObj!=null) {
				Country country=(Country) countryObj;
				this.custDocIssuedCountry.setValue(country.getCountryCode());
				this.custDocIssuedCountry.setDescription(country.getCountryDesc());
			}
		}
		this.custDocIsVerified.setChecked(aCustomerDocument.isCustDocIsVerified());
		this.custDocIsAcrive.setChecked(aCustomerDocument.isCustDocIsAcrive());
		this.custCIF.setValue(aCustomerDocument.getLovDescCustCIF());
		this.custShrtName.setValue(aCustomerDocument.getLovDescCustShrtName()==null?"":aCustomerDocument.getLovDescCustShrtName().trim());

		if(StringUtils.isBlank(aCustomerDocument.getCustDocCategory())){
			this.custDocType.setDescription("");
		}else{
			this.custDocType.setDescription(aCustomerDocument.getLovDescCustDocCategory());
		}
		if(StringUtils.isNotBlank(aCustomerDocument.getCustDocIssuedCountry())){
			this.custDocIssuedCountry.setDescription(aCustomerDocument.getLovDescCustDocIssuedCountry());
		}

		if(isNewRecord()){
			this.lovDescCustDocVerifiedBy.setValue(getUserWorkspace().getUserDetails().getUsername());
		}else{
			this.lovDescCustDocVerifiedBy.setValue(aCustomerDocument.getLovDescCustDocVerifiedBy());
			this.custDocType.setReadonly(true);
		}

		if(aCustomerDocument.isNew() || (aCustomerDocument.getRecordType() != null && aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW))){
			this.custDocIsAcrive.setChecked(true);
			this.custDocIsAcrive.setDisabled(true);
		}

		if(aCustomerDocument.isLovDescdocExpDateIsMand()){
			this.space_CustDocExpDate.setSclass(PennantConstants.mandateSclass);
			expDateIsMand=true;
		}else{
			this.space_CustDocExpDate.setSclass("");
		}

		if(aCustomerDocument.isDocIssueDateMand()){
			this.space_custDocIssuedOn.setSclass(PennantConstants.mandateSclass);
			isIssueDateMand=true;
		}else{
			this.space_custDocIssuedOn.setSclass("");			
		}

		if(aCustomerDocument.isDocIdNumMand()){
			this.space_CustIDNumber.setSclass(PennantConstants.mandateSclass);
			isIdNumMand=true;
		}else{
			this.space_CustIDNumber.setSclass("");
		}

		if(aCustomerDocument.isDocIssuedAuthorityMand()){
			this.space_CustDocSysName.setSclass(PennantConstants.mandateSclass);
			isIssuedAuth=true;
		}else{
			this.space_CustDocSysName.setSclass("");			
		}

		if(aCustomerDocument.isDocIsMandatory()){
			this.space_documnetName.setSclass(PennantConstants.mandateSclass);
			isDocuploadMand=true;
		}else{
			this.space_documnetName.setSclass("");			
		}


		this.documnetName.setValue(aCustomerDocument.getCustDocName());
		this.documnetName.setAttribute("data", aCustomerDocument);

		AMedia amedia = null;

		if (aCustomerDocument.getCustDocImage() != null) {
			if (aCustomerDocument.getCustDocType().equals(
					PennantConstants.DOC_TYPE_WORD) || aCustomerDocument.getCustDocType().equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent("<a href='' style = 'font-weight:bold'>"
						+ aCustomerDocument.getCustDocName() + "</a> ");

				List<Object> list = new ArrayList<Object>();
				list.add(aCustomerDocument.getCustDocType());
				list.add(aCustomerDocument.getCustDocImage());

				ageementLink.addForward("onClick", window_CustomerDocumentDialog, "onDocumentClicked", list);
				this.docDiv.appendChild(ageementLink);
			}else{
				amedia = new AMedia(aCustomerDocument.getCustDocName(), null, null, aCustomerDocument.getCustDocImage());
			}
			finDocumentPdfView.setContent(amedia);
		}
		this.recordStatus.setValue(aCustomerDocument.getRecordStatus());
		logger.debug("Leaving");
	}

	public void onChange$custDocTitle(Event event){
		logger.debug("Entering");
		if(PennantConstants.CPRCODE.equalsIgnoreCase(this.custDocType.getValue())){
			this.custDocTitle.setValue(PennantApplicationUtil.formatEIDNumber(this.custDocTitle.getValue()));
		}
		logger.debug("Leaving");
	}
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerDocument
	 */
	public void doWriteComponentsToBean(CustomerDocument aCustomerDocument) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerDocument.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerDocument.setCustID(this.custID.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setLovDescCustDocCategory(this.custDocType.getDescription());
			aCustomerDocument.setCustDocCategory(this.custDocType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(PennantConstants.CPRCODE.equalsIgnoreCase(aCustomerDocument.getCustDocCategory())){
				aCustomerDocument.setCustDocTitle(PennantApplicationUtil.unFormatEIDNumber(this.custDocTitle.getValue()));
			}else{
				aCustomerDocument.setCustDocTitle(this.custDocTitle.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setCustDocSysName(this.custDocSysName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isDocuploadMand && (this.documnetName.getValue() == null || StringUtils.isEmpty(this.documnetName.getValue()))){
				throw new WrongValueException(this.documnetName, Labels.getLabel("MUST_BE_UPLOADED", new String[] { Labels.getLabel("label_FinDocumentDetailDialog_DocumnetName.value") }));
			}
			aCustomerDocument.setCustDocName(this.documnetName.getValue());
			CustomerDocument details = (CustomerDocument) this.documnetName.getAttribute("data");
			aCustomerDocument.setCustDocImage(details.getCustDocImage());
			aCustomerDocument.setCustDocType(details.getCustDocType());
			aCustomerDocument.setDocRefId(Long.MIN_VALUE);
			aCustomerDocument.setDocIsPasswordProtected(details.isDocIsPasswordProtected());
			aCustomerDocument.setPdfMappingRef(details.getPdfMappingRef());
			aCustomerDocument.setDocIsPdfExtRequired(details.isDocIsPdfExtRequired());

		} catch (WrongValueException we) {
			wve.add(we);
		}		
		try {
			aCustomerDocument.setCustDocExpDate(this.custDocExpDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setCustDocIssuedOn(this.custDocIssuedOn.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setLovDescCustDocIssuedCountry(custDocIssuedCountry.getDescription());
			aCustomerDocument.setCustDocIssuedCountry(this.custDocIssuedCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerDocument.setPdfPassWord(this.pdfPassword.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aCustomerDocument.setLovDescdocExpDateIsMand(expDateIsMand);
		aCustomerDocument.setDocIssueDateMand(isIssueDateMand);
		aCustomerDocument.setDocIdNumMand(isIdNumMand);
		aCustomerDocument.setDocIssuedAuthorityMand(isIssuedAuth);
		aCustomerDocument.setDocIsMandatory(isDocuploadMand);
		aCustomerDocument.setRecordStatus(this.recordStatus.getValue());
		setCustomerDocument(aCustomerDocument);
		logger.debug("Leaving");
	}

	private void checkDocumentExpired(CustomerDocument aCustomerDocument) {
		boolean deviationallowed = false;
		//		Date date = aCustomerDocument.getCustDocExpDate();
		//		if (date != null && date.compareTo(DateUtility.getAppDate()) <= 0) {
		//			if (deviationExecutionCtrl!=null) {
		//				deviationallowed=deviationExecutionCtrl.checkDeviationForDocument(aCustomerDocument);
		//			}
		//		}
		if (!deviationallowed) {
			if (!this.custDocExpDate.isReadonly() && !this.custDocExpDate.isDisabled()) {
				this.custDocExpDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"), expDateIsMand, DateUtility.addDays(appStartDate, 1), endDate, true));
				this.custDocExpDate.getValue();//Call the validation
			}
		}

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerDocument
	 * @throws Exception
	 */
	public void doShowDialog(CustomerDocument aCustomerDocument)
			throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custDocType.getButton().focus();
		} else {
			this.custDocTitle.focus();
			if (isNewCustomer()){
				doEdit();
			}else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			}else if(isCheckList && !viewProcess){
				doEdit();
			}else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerDocument);

			if(this.creditApplicationReviewDialogCtrl != null){
				this.btnSearchPRCustid.setVisible(false);
			}

			if(isCheckList){
				//this.custCIF.setValue(financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustShrtName());
				if(viewProcess){
					this.btnUploadDoc.setVisible(false);
					this.btnSave.setVisible(false);
					this.btnEdit.setVisible(false);
					this.btnCancel.setVisible(false);
					this.btnDelete.setVisible(false);
				}else{
					this.btnSave.setVisible(true);
				}
				this.custDocType.setDescription(customerDocument.getLovDescCustDocCategory());
				this.btnSearchPRCustid.setVisible(false);
				this.custDocType.setReadonly(true);
			}

			doCheckEnquiry();

			if (isNewCustomer()) {
				this.groupboxWf.setVisible(false);
			}
			
			if(enqiryModule) {
				this.window_CustomerDocumentDialog.setHeight("80%");
				this.window_CustomerDocumentDialog.setWidth("70%");
				this.window_CustomerDocumentDialog.doModal();
			} else {
				setDialog(DialogType.OVERLAPPED);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerDocumentDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custID.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocCIF.value"),null,true));
		}
		// ### 01-05-2018 TuleApp ID : #360
		if (!this.custDocTitle.isReadonly()) {
			if (StringUtils.trimToEmpty(this.custDocType.getValue())
					.equalsIgnoreCase(SysParamUtil.getValueAsString("CUST_PRIMARY_ID_RETL_DOC_TYPE"))) {
				this.custDocTitle.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocTitle.value"),
								primaryIdRegex, isIdNumMand));

			} else if (StringUtils.trimToEmpty(this.custDocType.getValue())
					.equalsIgnoreCase(SysParamUtil.getValueAsString("CUST_PRIMARY_ID_CORP_DOC_TYPE"))) {
				this.custDocTitle.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocTitle.value"),
								primaryIdRegex, isIdNumMand));
			} else if (StringUtils.trimToEmpty(this.custDocType.getValue())
					.equalsIgnoreCase(PennantConstants.CPRCODE)) {
				this.custDocTitle.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocTitle.value"),
								PennantRegularExpressions.REGEX_AADHAR_NUMBER, isIdNumMand));
			} else {
				this.custDocTitle.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocTitle.value"),
								PennantRegularExpressions.REGEX_ALPHANUM_CODE, isIdNumMand));
			}
		}


		if (!this.custDocSysName.isReadonly()){
			this.custDocSysName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocSysName.value"), 
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, isIssuedAuth));
		}

		if (!this.custDocRcvdOn.isReadonly() && !this.custDocRcvdOn.isDisabled()) {
			this.custDocRcvdOn.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocRcvdOn.value"),true,startDate,appStartDate,true));
		}


		if (!this.custDocIssuedOn.isDisabled()) {
			this.custDocIssuedOn.setConstraint(new PTDateValidator(Labels
					.getLabel("label_CustomerDocumentDialog_CustDocIssuedOn.value"), isIssueDateMand, startDate, true,
					false));
		}


		if (!this.custDocExpDate.isDisabled()) {
			this.custDocExpDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"), expDateIsMand));
		}

		if (!this.custDocVerifiedBy.isReadonly()) {
			this.custDocVerifiedBy.setConstraint(new PTNumberValidator(Labels.getLabel(
					"label_CustomerDocumentDialog_CustDocVerifiedBy.value"), true));
		}

		if (this.passwordRow.isVisible()) {
			this.pdfPassword.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_pdf_password.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.custDocTitle.setConstraint("");
		this.custDocSysName.setConstraint("");
		this.pdfPassword.setConstraint("");
		this.custDocRcvdOn.setConstraint("");
		this.custDocExpDate.setConstraint("");
		this.custDocIssuedOn.setConstraint("");
		this.custDocVerifiedBy.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custDocType.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDocumentDialog_CustDocType.value"),null,true,true));
		this.custDocIssuedCountry.setConstraint(new PTStringValidator( Labels.getLabel("label_CustomerDocumentDialog_CustDocIssuedCountry.value"),null,true,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custDocType.setConstraint("");
		this.custDocIssuedCountry.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custDocTitle.setErrorMessage("");
		this.custDocSysName.setErrorMessage("");
		this.pdfPassword.setErrorMessage("");
		this.custDocRcvdOn.setErrorMessage("");
		this.custDocExpDate.setErrorMessage("");
		this.custDocIssuedOn.setErrorMessage("");
		this.custDocVerifiedBy.setErrorMessage("");
		this.custDocType.setErrorMessage("");
		this.custDocIssuedCountry.setErrorMessage("");
		this.documnetName.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful update
	private void refreshList() {
		getCustomerDocumentListCtrl().search();
	}

	// CRUD operations


	public void getCreditApplicationRevDialog(){
		logger.debug("Entering");
		Window windowDocDetails = creditApplicationReviewDialogCtrl.window_CreditApplicationReviewDialog;
		Tab tab = (Tab) windowDocDetails.getFellowIfAny("documentDetailsTab");
		//Tab tab = (Tab) creditApplicationReviewDialogCtrl.window_CreditApplicationReviewDialog.getFellowIfAny("documentDetailsTab");
		Tabs tabs = (Tabs) windowDocDetails.getFellowIfAny("tabsIndexCenter");
		Tabpanel tabPanel = (Tabpanel) windowDocDetails.getFellowIfAny("documentsTabPanel");
		Tabpanels tabPanels = (Tabpanels) windowDocDetails.getFellowIfAny("tabpanelsBoxIndexCenter");
		tabPanels.removeChild(tabPanel);
		//tab.removeChild(tabPanels);
		tabs.removeChild(tab);
		/*List<Component> docComponentsList = (List<Component>) window_docDetails.getFellows();
		int i=1;

		for(Component component : docComponentsList){
			if(component.getId().startsWith("document")){
				window_docDetails.removeChild(component);
				System.out.println(i);
			    i++;
			}
		}*/
		creditApplicationReviewDialogCtrl.window_CreditApplicationReviewDialog.removeChild(tab);
		creditApplicationReviewDialogCtrl.appendDocumentDetailTab();
		((Tab) windowDocDetails.getFellowIfAny("documentDetailsTab")).setSelected(true);
		logger.debug("Leaving");

	}






	/**
	 * Deletes a CustomerDocument object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerDocument aCustomerDocument = new CustomerDocument();
		BeanUtils.copyProperties(getCustomerDocument(), aCustomerDocument);
		String tranType = PennantConstants.TRAN_WF;

		if(isFinanceProcess && StringUtils.equals(PennantConstants.PANNUMBER,aCustomerDocument.getCustDocCategory())){
			MessageUtil.showError("Document with PAN Number Can't be deleted!!!");
		}else{

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
					Labels.getLabel("label_CustomerDocumentDialog_CustDocType.value")+" : "+aCustomerDocument.getCustDocCategory();

			int conf = MessageUtil.confirm(msg);

			if (conf == MessageUtil.YES && this.creditApplicationReviewDialogCtrl == null) {
				logger.debug("doDelete: Yes");

				if (StringUtils.isBlank(aCustomerDocument.getRecordType())) {
					aCustomerDocument.setVersion(aCustomerDocument.getVersion() + 1);
					aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					if(!isFinanceProcess && getCustomerDialogCtrl() != null &&  
							getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
						aCustomerDocument.setNewRecord(true);
					}
					if (isWorkFlowEnabled()) {
						aCustomerDocument.setNewRecord(true);
						tranType = PennantConstants.TRAN_WF;
					} else {
						tranType = PennantConstants.TRAN_DEL;
					}
				}

				try {
					if(isNewCustomer()){
						tranType=PennantConstants.TRAN_DEL;
						AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerDocument,tranType);
						auditHeader = ErrorControl.showErrorDetails(this.window_CustomerDocumentDialog, auditHeader);
						int retValue = auditHeader.getProcessStatus();
						if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
							getCustomerDialogCtrl().doFillDocumentDetails(this.customerDocuments);
							if (isFinanceProcess) {
								processChecklistDocuments(aCustomerDocument, false);
							}

							closeDialog();
						}	
					}else if (doProcess(aCustomerDocument, tranType)) {
						refreshList();
						closeDialog();
					}

				} catch (DataAccessException e) {
					MessageUtil.showError(e);
				}
			} else if (conf == MessageUtil.YES && this.creditApplicationReviewDialogCtrl != null) {
				this.creditApplicationReviewDialogCtrl.custDocList.remove(aCustomerDocument);
				this.creditApplicationReviewDialogCtrl.customerDocumentList.remove(aCustomerDocument);
				getCreditApplicationRevDialog();
				closeDialog();
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()){

			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custDocType.setReadonly(isReadOnly("CustomerDocumentDialog_custDocType"));
		}else{
			if(!isCheckList){
				this.btnCancel.setVisible(true);
			}
			this.btnSearchPRCustid.setVisible(false);
			this.custDocType.setReadonly(true);
		}

		this.custCIF.setReadonly(true);

		this.custDocType.setReadonly(isReadOnly("CustomerDocumentDialog_custDocType"));
		this.custDocTitle.setReadonly(isReadOnly("CustomerDocumentDialog_custDocTitle"));
		this.btnUploadDoc.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIssuedCountry"));
		this.custDocSysName.setReadonly(isReadOnly("CustomerDocumentDialog_custDocSysName"));
		this.pdfPassword.setReadonly(isReadOnly("CustomerDocumentDialog_custDocSysName"));
		this.custDocRcvdOn.setDisabled(isReadOnly("CustomerDocumentDialog_custDocRcvdOn"));
		this.custDocExpDate.setDisabled(isReadOnly("CustomerDocumentDialog_custDocExpDate"));
		this.custDocIssuedOn.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIssuedOn"));
		this.custDocIssuedCountry.setReadonly(isReadOnly("CustomerDocumentDialog_custDocIssuedCountry"));
		this.custDocIssuedCountry.setMandatoryStyle(!isReadOnly("CustomerDocumentDialog_custDocIssuedCountry"));
		this.custDocIsVerified.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIsVerified"));
		this.custDocVerifiedBy.setReadonly(isReadOnly("CustomerDocumentDialog_custDocVerifiedBy"));
		this.custDocIsAcrive.setDisabled(isReadOnly("CustomerDocumentDialog_custDocIsAcrive"));

		if(!isNewRecord()){
			this.custDocType.setReadonly(true);
			this.btnSearchPRCustid.setVisible(false);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerDocument.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(newCustomer){
				if(PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
			}
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
			this.custCIF.setReadonly(true);
			this.custDocType.setReadonly(true);
			this.custDocTitle.setReadonly(true);
			this.btnUploadDoc.setDisabled(true);
			this.custDocSysName.setReadonly(true);
			this.pdfPassword.setReadonly(true);
			this.custDocRcvdOn.setDisabled(true);
			this.custDocExpDate.setDisabled(true);
			this.custDocIssuedOn.setDisabled(true);
			this.custDocIssuedCountry.setReadonly(true);
			this.custDocIsVerified.setDisabled(true);
			this.custDocVerifiedBy.setReadonly(true);
			this.custDocIsAcrive.setDisabled(true);
		}
	}

	public boolean isReadOnly(String componentName){
		boolean isCustomerWorkflow = false;
		if(getCustomerDialogCtrl() != null){
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.custDocType.setReadonly(true);
		this.custDocTitle.setReadonly(true);
		this.btnUploadDoc.setDisabled(true);
		this.custDocSysName.setReadonly(true);
		this.pdfPassword.setReadonly(true);
		this.custDocRcvdOn.setDisabled(true);
		this.custDocExpDate.setDisabled(true);
		this.custDocIssuedOn.setDisabled(true);
		this.custDocIssuedCountry.setReadonly(true);
		this.custDocIsVerified.setDisabled(true);
		this.custDocVerifiedBy.setReadonly(true);
		this.custDocIsAcrive.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		// remove validation, if there are a save before
		this.custCIF.setValue("");
		this.custDocType.setValue("");
		this.custDocType.setDescription("");
		this.custDocTitle.setValue("");
		this.custDocSysName.setValue("");
		this.pdfPassword.setValue("");
		this.custDocRcvdOn.setText("");
		this.custDocExpDate.setText("");
		this.custDocIssuedOn.setText("");
		this.custDocIssuedCountry.setValue("");
		this.custDocIssuedCountry.setDescription("");
		this.custDocIsVerified.setChecked(false);
		this.custDocVerifiedBy.setText("");
		this.custDocIsAcrive.setChecked(false);
		logger.debug("Leaving");
	}

	private DocumentDetails getCustDocumentDetail(CustomerDocument aCustomerDocument){
		logger.debug("Entering");
		DocumentDetails aDocumentDetails = new DocumentDetails();
		aDocumentDetails.setDocImage(aCustomerDocument.getCustDocImage());
		aDocumentDetails.setDocRefId(aCustomerDocument.getDocRefId());
		aDocumentDetails.setDoctype(aCustomerDocument.getCustDocType());
		aDocumentDetails.setDocCategory(aCustomerDocument.getCustDocCategory());
		aDocumentDetails.setDocName(aCustomerDocument.getCustDocName());
		aDocumentDetails.setLovDescDocCategoryName(aCustomerDocument.getLovDescCustDocCategory());
		aDocumentDetails.setCustDocExpDate(aCustomerDocument.getCustDocExpDate());
		aDocumentDetails.setCustDocIsAcrive(aCustomerDocument.isCustDocIsAcrive());
		aDocumentDetails.setCustDocIssuedCountry(aCustomerDocument.getCustDocIssuedCountry());
		aDocumentDetails.setLovDescCustDocIssuedCountry(aCustomerDocument.getLovDescCustDocIssuedCountry());
		aDocumentDetails.setCustDocIssuedOn(aCustomerDocument.getCustDocIssuedOn());
		aDocumentDetails.setCustDocIsVerified(aCustomerDocument.isCustDocIsVerified());
		aDocumentDetails.setCustDocRcvdOn(aCustomerDocument.getCustDocRcvdOn());
		aDocumentDetails.setCustDocSysName(aCustomerDocument.getCustDocSysName());
		aDocumentDetails.setCustDocTitle(aCustomerDocument.getCustDocTitle());
		aDocumentDetails.setCustDocVerifiedBy(aCustomerDocument.getCustDocVerifiedBy());
		aDocumentDetails.setRecordStatus(aCustomerDocument.getRecordStatus());
		aDocumentDetails.setRecordType(StringUtils.trimToEmpty(aCustomerDocument.getRecordType()));
		aDocumentDetails.setLastMntBy(aCustomerDocument.getLastMntBy());
		aDocumentDetails.setLastMntOn(aCustomerDocument.getLastMntOn());
		aDocumentDetails.setCategoryCode(DocumentCategories.CUSTOMER.getKey());
		aDocumentDetails.setVersion(aCustomerDocument.getVersion());
		aDocumentDetails.setDocIsPasswordProtected(aCustomerDocument.isDocIsPasswordProtected());
		aDocumentDetails.setPdfMappingRef(aCustomerDocument.getPdfMappingRef());
		aDocumentDetails.setPdfPassWord(aCustomerDocument.getPdfPassWord());
		aDocumentDetails.setDocIsPdfExtRequired(aCustomerDocument.isDocIsPdfExtRequired());
		if(aCustomerDocument.getBefImage() != null){
			aDocumentDetails.setBefImage(getCustDocumentDetail(aCustomerDocument.getBefImage()));
		}
		logger.debug("Leaving");
		return aDocumentDetails;
	}

	private void processChecklistDocuments(CustomerDocument aCustomerDocument,boolean isSaveProcess){
		logger.debug("Entering");
		if(getFinanceMainDialogCtrl() !=null){
			try{
				if(getFinanceMainDialogCtrl().getClass().getMethod("getFinanceCheckListReferenceDialogCtrl") != null && 
						getFinanceMainDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl") != null) {

					FinanceCheckListReferenceDialogCtrl chkCtrl = (FinanceCheckListReferenceDialogCtrl) getFinanceMainDialogCtrl().
							getClass().getMethod("getFinanceCheckListReferenceDialogCtrl").invoke(getFinanceMainDialogCtrl());

					DocumentDetailDialogCtrl docDetailDilogCtrl = (DocumentDetailDialogCtrl)getFinanceMainDialogCtrl().getClass().
							getMethod("getDocumentDetailDialogCtrl").invoke(getFinanceMainDialogCtrl());
					//Document Details  
					List<DocumentDetails> newCustDocList = new ArrayList<DocumentDetails>();
					if(docDetailDilogCtrl.getDocumentDetailsList() != null){
						for (DocumentDetails docDetails : docDetailDilogCtrl.getDocumentDetailsList()) {
							if(!StringUtils.trimToEmpty(docDetails.getDocCategory()).
									equalsIgnoreCase(aCustomerDocument.getCustDocCategory())){
								newCustDocList.add(docDetails);
							}
						}
					}
					if(isSaveProcess){
						DocumentDetails detail = getCustDocumentDetail(aCustomerDocument);
						detail.setRecordType("");
						detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						newCustDocList.add(detail);
					}

					docDetailDilogCtrl.doFillDocumentDetails(newCustDocList);

					//CheckList Details
					if (chkCtrl != null && chkCtrl.getCheckListDocTypeMap() != null && chkCtrl.getCheckListDocTypeMap().
							containsKey(aCustomerDocument.getCustDocCategory())) {
						List<Listitem> list = chkCtrl.getCheckListDocTypeMap().get(aCustomerDocument.getCustDocCategory());
						for (int i = 0; i < list.size(); i++) {
							list.get(i).setDisabled(false);
							list.get(i).setSelected(isSaveProcess);
							list.get(i).setDisabled(true);
						}
					}
				}
			}catch (Exception e) {
				logger.debug(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		int docCount=0;

		final CustomerDocument aCustomerDocument = new CustomerDocument();
		BeanUtils.copyProperties(getCustomerDocument(), aCustomerDocument);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerDocument object with the components data
		doWriteComponentsToBean(aCustomerDocument);
		checkDocumentExpired(aCustomerDocument);		

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerDocument.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerDocument.getRecordType())) {
				aCustomerDocument.setVersion(aCustomerDocument.getVersion() + 1);
				if (isNew) {
					aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerDocument.setNewRecord(true);
				}
			}
		} else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerDocument.setVersion(1);
					aCustomerDocument.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aCustomerDocument.getRecordType())){
					aCustomerDocument.setVersion(aCustomerDocument.getVersion()+1);
					aCustomerDocument.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerDocument.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else {
				aCustomerDocument.setVersion(aCustomerDocument.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		try {
			if(!isCheckList && creditApplicationReviewDialogCtrl == null){
				// save it to database
				if(isNewCustomer()){
					AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerDocument,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerDocumentDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getCustomerDialogCtrl().doFillDocumentDetails(this.customerDocuments);
						if (isFinanceProcess) {
							processChecklistDocuments(aCustomerDocument, true);
						}
						closeDialog();
					}
				}else if (doProcess(aCustomerDocument, tranType)) {
					refreshList();
					// Close the Existing Dialog
					closeDialog();
				}

			}else{
				DocumentDetails aDocumentDetails = getCustDocumentDetail(aCustomerDocument);

				if (isNewRecord()) {
					aDocumentDetails.setVersion(1);
					aDocumentDetails.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aDocumentDetails.getRecordType())) {
					//aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
					aDocumentDetails.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aDocumentDetails.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aDocumentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
				CustomerDialogCtrl	custDialogCtrl = null;
				try{
					if(getFinanceMainDialogCtrl() != null && isFinanceProcess){
						if (getFinanceMainDialogCtrl().getClass().getMethod("getCustomerDialogCtrl") != null) {
							custDialogCtrl = (CustomerDialogCtrl) getFinanceMainDialogCtrl().getClass().getMethod("getCustomerDialogCtrl").invoke(getFinanceMainDialogCtrl());
							if (custDialogCtrl != null) {
								aDocumentDetails.setRecordType("");
								aDocumentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
							}
						}
					}
				}catch (Exception e) {
					logger.debug(e);
				}
				//	if (isNewDocument()) {
				AuditHeader auditHeader = newDocumentProcess(aDocumentDetails, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerDocumentDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					try {
						if(documentDetailDialogCtrl !=null){
							getDocumentDetailDialogCtrl().getClass().getMethod("doFillDocumentDetails",java.util.List.class).invoke(getDocumentDetailDialogCtrl(), this.documentDetailList);
							// send the data back to customer
							if (checkListDocTypeMap != null && checkListDocTypeMap.containsKey(aDocumentDetails.getDocCategory())) {
								List<Listitem> list = checkListDocTypeMap.get(aDocumentDetails.getDocCategory());
								for (int i = 0; i < list.size(); i++) {
									list.get(i).setDisabled(false);
									list.get(i).setSelected(true);
									list.get(i).setDisabled(true);
								}
							}
							closeDialog();
						} else if(creditApplicationReviewDialogCtrl != null){
							aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							aCustomerDocument.setRecordStatus("Saved");
							List<CustomerDocument> custDocList = this.creditApplicationReviewDialogCtrl.custDocList;
							if(custDocList.size() > 0 ){
								if(aCustomerDocument.isNewRecord()){
									for(CustomerDocument document : custDocList){
										if(document.getCustDocCategory().equals(aCustomerDocument.getCustDocCategory())){
											MessageUtil.showError(aCustomerDocument.getLovDescCustDocCategory()
													+ " is Already Existed");
											docCount++;
										} 
									} 
								}
							}
							if(docCount < 1 ){
								for(CustomerDocument document : custDocList){
									if(document.getCustDocTitle().equalsIgnoreCase(aCustomerDocument.getCustDocTitle())){
										custDocList.remove(document);
										if(!document.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
											aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
										}
										break;
									}
								}
								custDocList.add(aCustomerDocument);
								this.creditApplicationReviewDialogCtrl.customerDocumentList.add(aCustomerDocument);
							}
							if(docCount == 0 ){
								getCreditApplicationRevDialog();
								closeDialog();
							}
						}
						if (getCustomerDialogCtrl() != null) {
							List<CustomerDocument> newCustDocList = new ArrayList<CustomerDocument>();
							boolean isDocNewRecord = true;
							if(getCustomerDialogCtrl().getCustomerDocumentDetailList() != null){
								for (CustomerDocument custDocument : getCustomerDialogCtrl().getCustomerDocumentDetailList()) {
									if(StringUtils.trimToEmpty(custDocument.getCustDocCategory()).
											equalsIgnoreCase(aCustomerDocument.getCustDocCategory())){
										isDocNewRecord = false;
										aCustomerDocument.setBefImage(custDocument.getBefImage());
										aCustomerDocument.setVersion(custDocument.getVersion());
										aCustomerDocument.setRecordType(custDocument.getRecordType());
										aCustomerDocument.setLastMntOn(custDocument.getLastMntOn());
									}else{
										newCustDocList.add(custDocument);
									}
								}
							}
							if(isDocNewRecord){
								aCustomerDocument.setRecordType("");
							}
							aCustomerDocument.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
							aCustomerDocument.setUserDetails(getUserWorkspace().getLoggedInUser());

							if(isNewRecord()){
								if(StringUtils.isBlank(aCustomerDocument.getRecordType())){
									aCustomerDocument.setVersion(1);
									aCustomerDocument.setRecordType(PennantConstants.RCD_ADD);
								}else{
									aCustomerDocument.setNewRecord(false);
									aCustomerDocument.setRecordType(PennantConstants.RCD_UPD);
								}
							}
							if(StringUtils.isBlank(aCustomerDocument.getRecordType())){
								aCustomerDocument.setVersion(aCustomerDocument.getVersion()+1);
								aCustomerDocument.setRecordType(PennantConstants.RCD_UPD);
							}
							newCustDocList.add(aCustomerDocument);
							getCustomerDialogCtrl().doFillDocumentDetails(newCustDocList);
							if(isRetailCustomer){
								if(StringUtils.equals(PennantConstants.CPRCODE,this.custDocType.getValue())){
									getCustomerDialogCtrl().setMandatoryIDNumber(this.custDocTitle.getValue());
								}
							}else{
								if(StringUtils.equals(PennantConstants.TRADELICENSE,this.custDocType.getValue())){
									getCustomerDialogCtrl().setMandatoryIDNumber(this.custDocTitle.getValue());
									getCustomerDialogCtrl().setCustDob(this.custDocIssuedOn.getValue());
								}
							}
						}
					} catch (Exception e) {
						logger.debug(e);
					}
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		if(getCustomerDialogCtrl() != null){
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				if(isRetailCustomer){
					if(StringUtils.equals(PennantConstants.CPRCODE,this.custDocType.getValue())){
						getCustomerDialogCtrl().setMandatoryIDNumber(this.custDocTitle.getValue());
					}
				}else{
					if(StringUtils.equals(PennantConstants.TRADELICENSE,this.custDocType.getValue())){
						getCustomerDialogCtrl().setMandatoryIDNumber(this.custDocTitle.getValue());
						getCustomerDialogCtrl().setCustDob(this.custDocIssuedOn.getValue());
					}
				}
			}else{
				if(isRetailCustomer){
					if(StringUtils.equals(PennantConstants.PANNUMBER,this.custDocType.getValue())){
						getCustomerDialogCtrl().setMandatoryIDNumber(this.custDocTitle.getValue());
					}
				}else{
					if(StringUtils.equals(PennantConstants.TRADELICENSE,this.custDocType.getValue())){
						getCustomerDialogCtrl().setCustDob(this.custDocIssuedOn.getValue());
					}
				}

			}
		}
		logger.debug("Leaving");
	}

	/**
	 * New Document process
	 * @param aDocumentDetails
	 * @param tranType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private AuditHeader newDocumentProcess(DocumentDetails aDocumentDetails, String tranType) {
		boolean recordAdded = false;

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aDocumentDetails.getBefImage(), aDocumentDetails);
		AuditHeader auditHeader = new AuditHeader(getReference(), String.valueOf(aDocumentDetails.getDocCategory()),
				null, null, auditDetail, aDocumentDetails.getUserDetails(), getOverideMap());

		documentDetailList = new ArrayList<DocumentDetails>();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = aDocumentDetails.getLovDescDocCategoryName();
		errParm[0] = PennantJavaUtil.getLabel("CustDocType_label") + " : " + valueParm[0];
		List<DocumentDetails> documentDetailslist=null;
		Object object=null;
		try {
			object = getDocumentDetailDialogCtrl().getClass().getMethod("getDocumentDetailsList").invoke(getDocumentDetailDialogCtrl());
			if (object!=null ) {
				documentDetailslist=(List<DocumentDetails>) object;
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		if (documentDetailslist != null && documentDetailslist.size() > 0) {
			for (int i = 0; i < documentDetailslist.size(); i++) {
				DocumentDetails documentDetails = documentDetailslist.get(i);

				if (documentDetails.getDocCategory().equals(aDocumentDetails.getDocCategory())) { // Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aDocumentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							documentDetailList.add(aDocumentDetails);
						} else if (aDocumentDetails.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aDocumentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							documentDetailList.add(aDocumentDetails);
						} else if (aDocumentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							/*		for (int j = 0; j < getFinanceMainDialogCtrl().getFinanceDetail().getFinContributorHeader().getContributorDetailList().size(); j++) {
										DocumentDetails detail =  getFinanceMainDialogCtrl().getFinanceDetail().getFinContributorHeader().getContributorDetailList().get(j);
										if(detail.getCustID() == aDocumentDetails.getCustID()){
											contributorDetails.add(detail);
										}
									}*/
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							documentDetailList.add(documentDetails);
						}
					}
				} else {
					documentDetailList.add(documentDetails);
				}
			}
		}
		if (!recordAdded) {
			documentDetailList.add(aDocumentDetails);
		}
		return auditHeader;
	}



	private AuditHeader newFinanceCustomerProcess(CustomerDocument aCustomerDocument,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aCustomerDocument, tranType);
		customerDocuments = new ArrayList<CustomerDocument>();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = aCustomerDocument.getLovDescCustDocCategory();
		errParm[0] = PennantJavaUtil.getLabel("CustDocType_label") + " : " + valueParm[0];

		if(getCustomerDialogCtrl().getCustomerDocumentDetailList()!=null && getCustomerDialogCtrl().getCustomerDocumentDetailList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerDocumentDetailList().size(); i++) {
				CustomerDocument customerDocument = getCustomerDialogCtrl().getCustomerDocumentDetailList().get(i);

				if(customerDocument.getCustDocCategory().equals(aCustomerDocument.getCustDocCategory())){ // Both Current and Existing list documents same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerDocuments.add(aCustomerDocument);
						}else if(aCustomerDocument.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerDocuments.add(aCustomerDocument);
						}else if(aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerDocumentsList().size(); j++) {
								CustomerDocument document =  getCustomerDialogCtrl().getCustomerDetails().getCustomerDocumentsList().get(j);
								if(document.getCustID() == aCustomerDocument.getCustID() && document.getCustDocCategory().equals(aCustomerDocument.getCustDocCategory())){
									customerDocuments.add(document);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							customerDocuments.add(customerDocument);
						}
					}
				} else {
					customerDocuments.add(customerDocument);
				}
			}
		}
		if (!recordAdded) {
			customerDocuments.add(aCustomerDocument);
		}
		return auditHeader;
	} 


	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerDocument
	 *            (CustomerDocument)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerDocument aCustomerDocument, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerDocument.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerDocument.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerDocument.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerDocument.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerDocument);
				}

				if (isNotesMandatory(taskId, aCustomerDocument)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCustomerDocument.setTaskId(taskId);
			aCustomerDocument.setNextTaskId(nextTaskId);
			aCustomerDocument.setRoleCode(getRole());
			aCustomerDocument.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerDocument, tranType);
			String operationRefs = getServiceOperations(taskId, aCustomerDocument);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerDocument,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerDocument, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerDocument aCustomerDocument = (CustomerDocument) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerDocumentService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerDocumentService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCustomerDocumentService().doApprove(auditHeader);

						if (aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerDocumentService().doReject(auditHeader);

						if (aCustomerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_CustomerDocumentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_CustomerDocumentDialog, auditHeader);
				retValue = ErrorControl.showErrorControl(
						this.window_CustomerDocumentDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.customerDocument), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// Search Button Component Events

	public void onFulfill$custDocType(Event event) {
		logger.debug("Entering" + event.toString());
		doClearMandatory();
		Object dataObject = custDocType.getObject();
		if (dataObject instanceof String) {
			this.custDocType.setValue(dataObject.toString());
			this.custDocType.setDescription("");
		} else {
			DocumentType details = (DocumentType) dataObject;
			if (details != null) {
				this.custDocType.setValue(details.getDocTypeCode());
				this.custDocType.setDescription(details.getDocTypeDesc());
				if (details.isDocExpDateIsMand()) {
					expDateIsMand = true;
					this.space_CustDocExpDate.setSclass(PennantConstants.mandateSclass);
				}
				if (details.isDocIssueDateMand()) {
					isIssueDateMand = true;
					this.space_custDocIssuedOn.setSclass(PennantConstants.mandateSclass);
				}
				if (details.isDocIdNumMand()) {
					isIdNumMand = true;
					this.space_CustIDNumber.setSclass(PennantConstants.mandateSclass);
				}
				if (details.isDocIssuedAuthorityMand()) {
					isIssuedAuth = true;
					this.space_CustDocSysName.setSclass(PennantConstants.mandateSclass);
				}
				if (details.isDocIsMandatory()) {
					isDocuploadMand = true;
					this.space_documnetName.setSclass(PennantConstants.mandateSclass);
				}
				if (details.isDocIsPasswordProtected()) {
					this.passwordRow.setVisible(true);
				} else {
					this.passwordRow.setVisible(false);
				}
				getCustomerDocument().setDocIsPdfExtRequired(details.isDocIsPdfExtRequired());
				getCustomerDocument().setPdfMappingRef(details.getPdfMappingRef());
				getCustomerDocument().setDocIsPasswordProtected(details.isDocIsPasswordProtected());
			}
		}
		this.custDocTitle.setValue("");

		// ### 01-05-2018 TuleApp ID : #360

		if (isRetailCustomer) {
			String retValue = SysParamUtil.getValueAsString("CUST_PRIMARY_ID_RETL_DOC_TYPE");
			if (retValue.equals(this.custDocType.getValue())) {
				this.custDocTitle.setValue(getCustomerDialogCtrl().getCustIDNumber(this.custDocType.getValue()));
			}
		} else {
			String corpValue = SysParamUtil.getValueAsString("CUST_PRIMARY_ID_CORP_DOC_TYPE");
			if (corpValue.equals(this.custDocType.getValue())) {
				this.custDocTitle.setValue(getCustomerDialogCtrl().getCustIDNumber(this.custDocType.getValue()));
			}
		}
		
		Map<String, String> attributes = new HashMap<>();

		if (isRetailCustomer) {
			attributes = PennantStaticListUtil.getPrimaryIdAttributes(PennantConstants.PFF_CUSTCTG_INDIV);
		} else {
			attributes = PennantStaticListUtil.getPrimaryIdAttributes(PennantConstants.PFF_CUSTCTG_CORP);

		}

		primaryIdRegex = attributes.get("REGEX");
		
		// ### 01-05-2018 - End	

		
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * When Selection Customer Document Type reset The Conditional 
	 * Mandatory Fields 
	 * 
	 */
	public void doClearMandatory(){
		expDateIsMand=false;
		isIssueDateMand=false;
		isIdNumMand=false;
		isIssuedAuth=false;
		isDocuploadMand=false;
		this.custDocExpDate.setErrorMessage("");
		this.custDocIssuedOn.setErrorMessage("");
		this.custDocTitle.setErrorMessage("");
		this.custDocSysName.setErrorMessage("");
		this.documnetName.setErrorMessage("");
		this.space_CustDocExpDate.setSclass("");
		this.space_custDocIssuedOn.setSclass("");
		this.space_CustIDNumber.setSclass("");
		this.space_CustDocSysName.setSclass("");
		this.space_documnetName.setSclass("");
	}
	public void onFulfill$custDocIssuedCountry(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custDocIssuedCountry.getObject();
		if (dataObject instanceof String) {
			this.custDocIssuedCountry.setValue(dataObject.toString());
			this.custDocIssuedCountry.setDescription("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custDocIssuedCountry.setValue(details.getCountryCode());
				this.custDocIssuedCountry.setDescription(details.getCountryDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media, this.documnetName);
		logger.debug("Leaving" + event.toString());
	}

	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");
		try {
			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
			} else if (media.getName().endsWith(".msg")) {
				docType = PennantConstants.DOC_TYPE_MSG;
			} else {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
			}

			//Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.finDocumentPdfView.setContent(new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.finDocumentPdfView.setContent(media);
			}else if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent("<a href='' style = 'font-weight:bold'>" + fileName+ "</a> ");

				List<Object> list = new ArrayList<Object>();
				list.add(docType);
				list.add(ddaImageData);
				ageementLink.addForward("onClick", window_CustomerDocumentDialog, "onDocumentClicked", list);
				this.docDiv.appendChild(ageementLink);
			}

			if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.setVisible(true);
				this.finDocumentPdfView.setVisible(false);
			}else{
				this.docDiv.setVisible(false);
				this.finDocumentPdfView.setVisible(true);
			}


			textbox.setValue(fileName);
			if (textbox.getAttribute("data") == null) {
				CustomerDocument documentDetails = new CustomerDocument("", docType, fileName, ddaImageData);
				textbox.setAttribute("data", documentDetails);
			} else {
				CustomerDocument documentDetails = (CustomerDocument) textbox.getAttribute("data");
				documentDetails.setCustDocType(docType);
				documentDetails.setCustDocImage(ddaImageData);
				textbox.setAttribute("data", documentDetails);
			}
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	public void onDocumentClicked(Event event) throws Exception {

		@SuppressWarnings("unchecked")
		List<Object> list  = (List<Object>) event.getData();
		String docType = (String) list.get(0);
		byte[] ddaImageData= (byte[]) list.get(1);

		if(docType.equals(PennantConstants.DOC_TYPE_WORD)){
			Filedownload.save(ddaImageData, "application/msword", this.documnetName.getValue());
		}else if(docType.equals(PennantConstants.DOC_TYPE_MSG)){
			Filedownload.save(ddaImageData, "application/octet-stream", this.documnetName.getValue());
		}
	}

	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event)
			throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException,InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,
			JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * @param aCustomerDocument
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CustomerDocument aCustomerDocument, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerDocument.getBefImage(), aCustomerDocument);
		return new AuditHeader(getReference(), String.valueOf(aCustomerDocument.getCustID()),
				null, null, auditDetail, aCustomerDocument.getUserDetails(), getOverideMap());
	}



	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerDocumentDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.customerDocument);

	}

	/**
	 *  Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerDocument().getCustID()
				+ PennantConstants.KEY_SEPERATOR + getCustomerDocument().getCustDocCategory();
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CustomerDocument getCustomerDocument() {
		return this.customerDocument;
	}
	public void setCustomerDocument(CustomerDocument customerDocument) {
		this.customerDocument = customerDocument;
	}

	public void setCustomerDocumentService(
			CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}
	public CustomerDocumentService getCustomerDocumentService() {
		return this.customerDocumentService;
	}

	public void setCustomerDocumentListCtrl(
			CustomerDocumentListCtrl customerDocumentListCtrl) {
		this.customerDocumentListCtrl = customerDocumentListCtrl;
	}
	public CustomerDocumentListCtrl getCustomerDocumentListCtrl() {
		return this.customerDocumentListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public JdbcSearchObject<Customer> getSearchObj() {
		return searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Customer> searchObj) {
		this.searchObj = searchObj;
	}

	public JdbcSearchObject<SecurityUser> getSecUserSearchObj() {
		return secUserSearchObj;
	}
	public void setSecUserSearchObj(JdbcSearchObject<SecurityUser> secUserSearchObj) {
		this.secUserSearchObj = secUserSearchObj;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}
	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}
	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setCustomerDocuments(List<CustomerDocument> customerDocuments) {
		this.customerDocuments = customerDocuments;
	}
	public List<CustomerDocument> getCustomerDocuments() {
		return customerDocuments;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public CreditApplicationReviewDialogCtrl getCreditApplicationReviewDialogCtrl() {
		return creditApplicationReviewDialogCtrl;
	}

	public void setCreditApplicationReviewDialogCtrl(
			CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl) {
		this.creditApplicationReviewDialogCtrl = creditApplicationReviewDialogCtrl;
	}

	public PagedListWrapper<SecurityUser> getSecUserPagedListWrapper() {
		return secUserPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setSecUserPagedListWrapper() {
		if (this.secUserPagedListWrapper == null) {
			this.secUserPagedListWrapper = (PagedListWrapper<SecurityUser>) SpringUtil.getBean("pagedListWrapper");
		}
	}

	public Object getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(Object documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public void setDeviationExecutionCtrl() throws Exception {
		if(getFinanceMainDialogCtrl()!=null && isFinanceProcess){
			deviationExecutionCtrl = (DeviationExecutionCtrl) getFinanceMainDialogCtrl().getClass()
					.getMethod("getDeviationExecutionCtrl").invoke(getFinanceMainDialogCtrl());
		}
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

}
