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
package com.pennant.webui.facility.facility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/Contributor/DocumentDetailsDialog.zul file.
 */
public class FacilityDocDetailDialogCtrl extends GFCBaseCtrl<DocumentDetails> {
	private static final long	          serialVersionUID	      = -6959194080451993569L;
	private static final Logger	          logger	              = Logger.getLogger(FacilityDocDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window	                  window_FinDocumentDetailDialog;	                                                 // autowired

	protected Combobox	                  docCategory;	                                                                 // autowired
	protected Textbox	                  documnetName;	                                                             // autowired
	protected Div		                  finDocumentDiv;	                                                             // autowired
	protected Row	                      statusRow;

	// not auto wired vars
	private DocumentDetails	              finDocumentDetail;	                                                     // overhanded per param

	private transient boolean	          validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CustomerSelectCtrl	customerSelectCtrl;

	private boolean	                      newRecord	              = false;
	private boolean	                      newDocument	      = false;
	private List<DocumentDetails>	      documentDetailList;
	private FacilityDocumentDetailDialogCtrl	  facilityDocumentDetailDialogCtrl;
	protected JdbcSearchObject<Customer>	newSearchObject;
	private String	                      moduleType	          = "";
	private boolean 					  viewProcess = false;
	private boolean 					  isCheckList = false;
	protected Button	                  btnUploadDoc;
	protected Iframe	                  finDocumentPdfView;
	private List<ValueLabel>	documentTypes	      = PennantAppUtil.getDocumentTypes();
	private Map<String, List<Listitem>>  checkListDocTypeMap = null;
	protected Div docDiv;
	private boolean enqModule = false;

	/**
	 * default constructor.<br>
	 */
	public FacilityDocDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DocumentDetailsDialog";
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
	public void onCreate$window_FinDocumentDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinDocumentDetailDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("enqModule")) {
			enqModule = true;
		} else {
			enqModule = false;
		}
		
		// READ OVERHANDED params !
		if (arguments.containsKey("finDocumentDetail")) {
			this.finDocumentDetail = (DocumentDetails) arguments.get("finDocumentDetail");
			DocumentDetails befImage = new DocumentDetails();
			BeanUtils.copyProperties(this.finDocumentDetail, befImage);
			this.finDocumentDetail.setBefImage(befImage);
			setDocumentDetails(this.finDocumentDetail);
		} else {
			setDocumentDetails(null);
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}
		
		if (arguments.containsKey("viewProcess")) {
			this.viewProcess = (Boolean) arguments.get("viewProcess");
		}
		if (arguments.containsKey("isCheckList")) {
			this.isCheckList = (Boolean) arguments.get("isCheckList");
		}
		
		if (arguments.containsKey("checkListDocTypeMap")) {
			checkListDocTypeMap = (Map<String, List<Listitem>>) arguments.get("checkListDocTypeMap");
		}

		if (getDocumentDetails().isNewRecord()) {
			setNewRecord(true);
		}
	
		if (arguments.containsKey("DocumentDetailDialogCtrl")) {

			setFacilityDocumentDetailDialogCtrl((FacilityDocumentDetailDialogCtrl) arguments.get("DocumentDetailDialogCtrl"));
			setNewDocument(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.finDocumentDetail.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "DocumentDetailsDialog");
			}
		}

		doLoadWorkFlow(this.finDocumentDetail.isWorkflow(), this.finDocumentDetail.getWorkflowId(), this.finDocumentDetail.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "DocumentDetailsDialog");
		}

		this.finDocumentDiv.setHeight(this.borderLayoutHeight - 152+ "px");// 425px
		this.finDocumentPdfView.setHeight(this.borderLayoutHeight - 152+ "px");// 425px
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getDocumentDetails());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 * @throws InterruptedException 
	 * @throws SuspendNotAllowedException 
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes

		this.documnetName.setMaxlength(200);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnSave.setVisible(true);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_FinDocumentDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		String doctype = this.docCategory.getSelectedItem().getValue().toString(); 
		logger.debug("Entering" + event.toString());
		doDelete(doctype);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	@Override
	public void closeDialog() {
		if (isNewDocument()) {
			closeWindow();
			return;
		}

		super.closeDialog();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.finDocumentDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
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
		
		fillComboBox(this.docCategory, aDocumentDetails.getDocCategory(), documentTypes, "");
		if(checkListDocTypeMap != null && checkListDocTypeMap.containsKey(aDocumentDetails.getDocCategory())){
			this.docCategory.setDisabled(true);
		}
		
		this.documnetName.setValue(aDocumentDetails.getDocName());
		this.documnetName.setAttribute("data", aDocumentDetails);
		
		AMedia amedia = null;
		if (aDocumentDetails.getDocImage() != null) {
			final InputStream data = new ByteArrayInputStream(aDocumentDetails.getDocImage());
			if (aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_PDF)) {
				amedia = new AMedia("document.pdf", "pdf", "application/pdf", data);
			} else if (aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_IMAGE)) {
				amedia = new AMedia("document.jpg", "jpeg", "image/jpeg", data);
			}else if (aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_WORD) || aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_MSG) ) {
				this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent("<a href='' style = 'font-weight:bold'>" + aDocumentDetails.getDocName()+ "</a> ");
				
				List<Object> list = new ArrayList<Object>();
				list.add(aDocumentDetails.getDoctype());
				list.add(aDocumentDetails.getDocImage());
				
				ageementLink.addForward("onClick", window_FinDocumentDetailDialog, "onDocumentClicked", list);
				this.docDiv.appendChild(ageementLink);
			}
			finDocumentPdfView.setContent(amedia);
		}

		this.recordStatus.setValue(aDocumentDetails.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDocumentDetails
	 */
	public void doWriteComponentsToBean(DocumentDetails aDocumentDetails) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		aDocumentDetails.setDocModule(FacilityConstants.MODULE_NAME);
		try {
			if (this.docCategory.getSelectedItem() == null || this.docCategory.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.docCategory, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinDocumentDetailDialog_DocCategory.value") }));
			} else {
				this.docCategory.getSelectedItem().setDisabled(true);
			}
			aDocumentDetails.setDocCategory(this.docCategory.getSelectedItem().getValue().toString());
			aDocumentDetails.setLovDescDocCategoryName(this.docCategory.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.documnetName.getValue() == null || StringUtils.isEmpty(this.documnetName.getValue()) || this.documnetName.getAttribute("data") == null) {
				throw new WrongValueException(this.documnetName, Labels.getLabel("MUST_BE_UPLOADED", new String[] { Labels.getLabel("label_FinDocumentDetailDialog_DocumnetName.value") }));
			}
			aDocumentDetails.setDocName(this.documnetName.getValue());
			DocumentDetails details = (DocumentDetails) this.documnetName.getAttribute("data");
			aDocumentDetails.setDocImage(details.getDocImage());
			aDocumentDetails.setDoctype(details.getDoctype());
		

		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aDocumentDetails.setRecordStatus(this.recordStatus.getValue());
		setDocumentDetails(aDocumentDetails);
		logger.debug("Leaving");

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
	public void doShowDialog(DocumentDetails aDocumentDetails) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.documnetName.focus();
		} else {

			if (isNewDocument()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aDocumentDetails);

			if (isCheckList) {
				this.btnDelete.setVisible(false);
			}
			
			if (isCheckList && StringUtils.trimToEmpty(aDocumentDetails.getRecordType()).equals(PennantConstants.RECORD_TYPE_CAN)) {
				viewProcess=true;
			}
			if(isCheckList && viewProcess){
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
				this.btnUploadDoc.setVisible(false);
			}
		
			doCheckEnquiry();
			if (isNewDocument()) {
				this.window_FinDocumentDetailDialog.setHeight("70%");
				this.window_FinDocumentDetailDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_FinDocumentDetailDialog.doModal();
			} else {
				this.window_FinDocumentDetailDialog.setWidth("100%");
				this.window_FinDocumentDetailDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// Helpers

	private void doCheckEnquiry() {
		if (enqModule) {
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnCancel.setVisible(false);
			this.btnUploadDoc.setVisible(false);
			this.docCategory.setDisabled(true);
			this.documnetName.setDisabled(true);
		}
		
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.documnetName.isReadonly()) {
			this.documnetName.setConstraint(new PTStringValidator(Labels.getLabel("label_DocumentDetailsDialog_CustID.value"),null,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.documnetName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.documnetName.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a DocumentDetails object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete(String doctype) throws InterruptedException {
		logger.debug("Entering");
		final DocumentDetails aDocumentDetails = new DocumentDetails();
		BeanUtils.copyProperties(getDocumentDetails(), aDocumentDetails);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aDocumentDetails.getDocName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDocumentDetails.getRecordType())) {
				aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
				aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aDocumentDetails.setNewRecord(true);

				if (isWorkFlowEnabled()) {
					aDocumentDetails.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewDocument()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newDocumentProcess(aDocumentDetails, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_FinDocumentDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getFacilityDocumentDetailDialogCtrl().doFillDocumentDetails(this.documentDetailList);
						
						if(checkListDocTypeMap != null && checkListDocTypeMap.containsKey(aDocumentDetails.getDocCategory())){
							List<Listitem>  list = checkListDocTypeMap.get(aDocumentDetails.getDocCategory());
							for (int i = 0; i < list.size(); i++) {
								list.get(i).setSelected(false);
							}
						}
						closeDialog();
					}

				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			this.docCategory.setDisabled(false);
			if (isNewDocument()) {
				this.btnCancel.setVisible(false);
			}
		} else {
			this.docCategory.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		this.docCategory.setDisabled(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.finDocumentDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {

			if (newDocument) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newDocument);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}

		if (isNewRecord()) {
			btnDelete.setVisible(false);
		} else {
			btnDelete.setVisible(true);
		}

		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewDocument()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.documnetName.setReadonly(true);

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

		this.documnetName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DocumentDetails aDocumentDetails = new DocumentDetails();
		BeanUtils.copyProperties(getDocumentDetails(), aDocumentDetails);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aDocumentDetails);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aDocumentDetails.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDocumentDetails.getRecordType())) {
				aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
				if (isNew) {
					aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDocumentDetails.setNewRecord(true);
				}
			}
		} else {

			if (isNewDocument()) {
				if (isNewRecord()) {
					aDocumentDetails.setVersion(1);
					aDocumentDetails.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aDocumentDetails.getRecordType())) {
					aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
					aDocumentDetails.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aDocumentDetails.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aDocumentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewDocument()) {
				AuditHeader auditHeader = newDocumentProcess(aDocumentDetails, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinDocumentDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFacilityDocumentDetailDialogCtrl().doFillDocumentDetails(this.documentDetailList);
					// send the data back to customer
					if(checkListDocTypeMap != null && checkListDocTypeMap.containsKey(aDocumentDetails.getDocCategory())){
						List<Listitem>  list = checkListDocTypeMap.get(aDocumentDetails.getDocCategory());
						for (int i = 0; i < list.size(); i++) {
							list.get(i).setDisabled(false);
							list.get(i).setSelected(true);
							list.get(i).setDisabled(true);
						}
					}
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newDocumentProcess(DocumentDetails aDocumentDetails, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aDocumentDetails, tranType);
		documentDetailList = new ArrayList<DocumentDetails>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aDocumentDetails.getDocName();
		valueParm[1] = aDocumentDetails.getReferenceId();

		errParm[0] = PennantJavaUtil.getLabel("label_DocumnetName") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[1];

		if (getFacilityDocumentDetailDialogCtrl().getDocumentDetailsList() != null && getFacilityDocumentDetailDialogCtrl().getDocumentDetailsList().size() > 0) {
			for (int i = 0; i < getFacilityDocumentDetailDialogCtrl().getDocumentDetailsList().size(); i++) {
				DocumentDetails documentDetails = getFacilityDocumentDetailDialogCtrl().getDocumentDetailsList().get(i);

				if (documentDetails.getDocCategory().equals(aDocumentDetails.getDocCategory())) { // Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aDocumentDetails.getRecordType())) {
							aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							documentDetailList.add(aDocumentDetails);
						} else if (PennantConstants.RCD_ADD.equals(aDocumentDetails.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aDocumentDetails.getRecordType())) {
							aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							documentDetailList.add(aDocumentDetails);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aDocumentDetails.getRecordType())) {
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

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DocumentDetails aDocumentDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDocumentDetails.getBefImage(), aDocumentDetails);

		return new AuditHeader(getReference(), String.valueOf(aDocumentDetails.getDocId()), null, null, auditDetail, aDocumentDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinDocumentDetailDialog, auditHeader);
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
		doShowNotes(finDocumentDetail);
	}

	public void onSelect$docCategory(Event  event){
		
		
	}

	/** 
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getDocumentDetails().getDocId() + PennantConstants.KEY_SEPERATOR + getDocumentDetails().getReferenceId();
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

	public DocumentDetails getDocumentDetails() {
		return this.finDocumentDetail;
	}

	public void setDocumentDetails(DocumentDetails customerRating) {
		this.finDocumentDetail = customerRating;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}

	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewDocument() {
		return newDocument;
	}

	public void setNewDocument(boolean newDocument) {
		this.newDocument = newDocument;
	}

	
	public FacilityDocumentDetailDialogCtrl getFacilityDocumentDetailDialogCtrl() {
		return facilityDocumentDetailDialogCtrl;
	}

	public void setFacilityDocumentDetailDialogCtrl(FacilityDocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.facilityDocumentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media);
		logger.debug("Leaving" + event.toString());
	}

	private void browseDoc(Media media) throws InterruptedException {
		logger.debug("Entering");
		try {
			boolean isSupported = true;
			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
				
			} else {
				isSupported = false;
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
			}
			if (isSupported) {
				String fileName = media.getName();
				byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
				// Data Fill by QR Bar Code Reader
				if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
					this.finDocumentPdfView.setContent(new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

				} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
					this.finDocumentPdfView.setContent(new AMedia("document.jpg", "jpg", "image", new ByteArrayInputStream(ddaImageData)));
				}else if (docType.equals(PennantConstants.DOC_TYPE_WORD)  || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
					this.docDiv.getChildren().clear();
					Html ageementLink = new Html();
					ageementLink.setStyle("padding:10px;");
					ageementLink.setContent("<a href='' style = 'font-weight:bold'>" + fileName+ "</a> ");
					
					List<Object> list = new ArrayList<Object>();
					list.add(docType);
					list.add(ddaImageData);
					
					ageementLink.addForward("onClick", window_FinDocumentDetailDialog, "onDocumentClicked", list);
					this.docDiv.appendChild(ageementLink);
				}

				if (docType.equals(PennantConstants.DOC_TYPE_WORD)) {
					this.docDiv.setVisible(true);
					this.finDocumentPdfView.setVisible(false);
				}else{
					this.docDiv.setVisible(false);
					this.finDocumentPdfView.setVisible(true);
				}
				
				
				 this.documnetName.setValue(fileName);
				if ( this.documnetName.getAttribute("data") == null) {
					DocumentDetails documentDetails = new DocumentDetails(FacilityConstants.MODULE_NAME, "", docType, 
							fileName, ddaImageData);
					 this.documnetName.setAttribute("data", documentDetails);
				} else {
					DocumentDetails documentDetails = (DocumentDetails)  this.documnetName.getAttribute("data");
					documentDetails.setDoctype(docType);
					documentDetails.setDocImage(ddaImageData);
					 this.documnetName.setAttribute("data", documentDetails);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		logger.debug("Leaving");
	}
	
	@SuppressWarnings("unchecked")
	public void onDocumentClicked(Event event) throws Exception {
		List<Object> list  = (List<Object>) event.getData();
		
		String docType = (String) list.get(0);
		byte[] ddaImageData= (byte[]) list.get(1);
		
		if(docType.equals(PennantConstants.DOC_TYPE_WORD)){
			Filedownload.save(ddaImageData, "application/msword", this.documnetName.getValue());
		}else if(docType.equals(PennantConstants.DOC_TYPE_MSG)){
			Filedownload.save(ddaImageData, "application/octet-stream", this.documnetName.getValue());
		}
	}

}
