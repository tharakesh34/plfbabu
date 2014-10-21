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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/Contributor/DocumentDetailsDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class FinDocumentDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long	          serialVersionUID	      = -6959194080451993569L;
	private final static Logger	          logger	              = Logger.getLogger(FinDocumentDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	                  window_FinDocumentDetailDialog;	                                                 // autowired

	protected Combobox	                  docCategory;	                                                                 // autowired
	protected Textbox	                  documnetName;	                                                             // autowired
	protected Div		                  finDocumentDiv;	                                                             // autowired

	protected Label	                      recordStatus;	                                                             // autowired
	protected Radiogroup	              userAction;
	protected Groupbox	                  groupboxWf;
	protected Row	                      statusRow;

	// not auto wired vars
	private DocumentDetails	              finDocumentDetail;	                                                     // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String	          oldVar_documnetName;

	private transient String	          oldVar_recordStatus;

	private transient boolean	          validationOn;
	private boolean	                      notes_Entered	          = false;

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
	protected Button	                  btnNotes;	                                                                 // autowire

	// ServiceDAOs / Domain Classes
	private transient CustomerSelectCtrl	customerSelectCtrl;

	private boolean	                      newRecord	              = false;
	private boolean	                      newDocument	      = false;
	private List<DocumentDetails>	      documentDetailList;
	private DocumentDetailDialogCtrl	  documentDetailDialogCtrl;
	protected JdbcSearchObject<Customer>	newSearchObject;
	private String	                      moduleType	          = "";
	private boolean 					  viewProcess = false;
	private boolean 					  isCheckList = false;
	private boolean 					  isDocAllowedForInput = false;
	protected Button	                  btnUploadDoc;
	protected Iframe	                  finDocumentPdfView;
	private List<ValueLabel>	documentTypes	      = PennantAppUtil.getDocumentTypes();
	private Map<String, List<Listitem>>  checkListDocTypeMap = null;
	public int borderLayoutHeight = 0;
	protected Div docDiv;

	/**
	 * default constructor.<br>
	 */
	public FinDocumentDetailDialogCtrl() {
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
	public void onCreate$window_FinDocumentDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("finDocumentDetail")) {
			this.finDocumentDetail = (DocumentDetails) args.get("finDocumentDetail");
			DocumentDetails befImage = new DocumentDetails();
			BeanUtils.copyProperties(this.finDocumentDetail, befImage);
			this.finDocumentDetail.setBefImage(befImage);
			setDocumentDetails(this.finDocumentDetail);
		} else {
			setDocumentDetails(null);
		}

		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}
		
		if (args.containsKey("viewProcess")) {
			this.viewProcess = (Boolean) args.get("viewProcess");
		}
		if (args.containsKey("isCheckList")) {
			this.isCheckList = (Boolean) args.get("isCheckList");
		}
		
		if (args.containsKey("checkListDocTypeMap")) {
			checkListDocTypeMap = (Map<String, List<Listitem>>) args.get("checkListDocTypeMap");
		}
		
		if (args.containsKey("isDocAllowedForInput")) {
			isDocAllowedForInput = (Boolean) args.get("isDocAllowedForInput");
		}

		if (getDocumentDetails().isNewRecord()) {
			setNewRecord(true);
		}
	
		if (args.containsKey("DocumentDetailDialogCtrl")) {

			setDocumentDetailDialogCtrl((DocumentDetailDialogCtrl) args.get("DocumentDetailDialogCtrl"));
			setNewDocument(true);

			if (args.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.finDocumentDetail.setWorkflowId(0);
			if (args.containsKey("roleCode")) {
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "DocumentDetailsDialog");
			}
		}

		doLoadWorkFlow(this.finDocumentDetail.isWorkflow(), this.finDocumentDetail.getWorkflowId(), this.finDocumentDetail.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "DocumentDetailsDialog");
		}

		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
		.getValue().intValue()- PennantConstants.borderlayoutMainNorth;
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
		getUserWorkspace().alocateAuthorities("DocumentDetailsDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DocumentDetailsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnSave.setVisible(true);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_DocumentDetailsDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
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
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_FinDocumentDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if (close) {
			BeanUtils.copyProperties(getDocumentDetails().getBefImage(),getDocumentDetails());
			closeWindow();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException {
		logger.debug("Entering");

		if (isNewDocument()) {
			window_FinDocumentDetailDialog.onClose();
		} else {
			closeDialog(this.window_FinDocumentDetailDialog, "DocumentDetails");
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
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
		this.docCategory.setDisabled(true);
		
		this.documnetName.setValue(aDocumentDetails.getDocName());
		this.documnetName.setAttribute("data", aDocumentDetails);
		
		AMedia amedia = null;
		if (aDocumentDetails.getDocImage() != null) {
			final InputStream data = new ByteArrayInputStream(aDocumentDetails.getDocImage());
			if (aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_PDF)) {
				amedia = new AMedia("document.pdf", "pdf", "application/pdf", data);
			} else if (aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_IMAGE)) {
				amedia = new AMedia("document.jpg", "jpeg", "image/jpeg", data);
			}else if (aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_WORD) || aDocumentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_MSG)) {
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
		aDocumentDetails.setDocModule("FINANCEMAIN");
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
			if (this.documnetName.getValue() == null || this.documnetName.getValue().equals("") || this.documnetName.getAttribute("data") == null) {
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			
			if (isCheckList && StringUtils.trimToEmpty(aDocumentDetails.getRecordType()).equals(PennantConstants.RECORD_TYPE_CAN)) {
				viewProcess=true;
			}
			if(isCheckList && viewProcess){
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
				this.btnUploadDoc.setVisible(false);
			}
			if(checkListDocTypeMap!= null && checkListDocTypeMap.containsKey(aDocumentDetails.getDocCategory())){
				if(!isDocAllowedForInput){
					this.btnDelete.setVisible(false);
					this.btnSave.setVisible(false);
					this.btnUploadDoc.setVisible(false);
				}
			}
		
			 //doCheckDocumentOwner();
			if (isNewDocument()) {
				this.window_FinDocumentDetailDialog.setHeight("70%");
				this.window_FinDocumentDetailDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_FinDocumentDetailDialog.doModal();
			} else {
				this.window_FinDocumentDetailDialog.setWidth("100%");
				this.window_FinDocumentDetailDialog.setHeight("100%");
				setDialog(this.window_FinDocumentDetailDialog);
			}

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_documnetName = this.documnetName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.documnetName.setValue(this.oldVar_documnetName);
		//this.totalInvestmentPerc.setValue(this.oldVar_totalInvestmentPerc);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.documnetName.isReadonly()) {
			this.documnetName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_DocumentDetailsDialog_CustID.value") }));
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
	private void doClearMessage() {
		logger.debug("Entering");
		this.documnetName.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aDocumentDetails.getRecordType()).equals("")) {
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
						getDocumentDetailDialogCtrl().doFillDocumentDetails(this.documentDetailList);
						
						if(checkListDocTypeMap != null && checkListDocTypeMap.containsKey(aDocumentDetails.getDocCategory())){
							List<Listitem>  list = checkListDocTypeMap.get(aDocumentDetails.getDocCategory());
							for (int i = 0; i < list.size(); i++) {
								list.get(i).setSelected(false);
							}
						}
						// send the data back to customer
						closeWindow();
					}

				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new DocumentDetails object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new DocumentDetails() in the frontEnd.
		// we get it from the backEnd.
		final DocumentDetails aDocumentDetails = new DocumentDetails();
		aDocumentDetails.setNewRecord(true);
		setDocumentDetails(aDocumentDetails);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.documnetName.focus();
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aDocumentDetails.getRecordType()).equals("")) {
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

				if (StringUtils.trimToEmpty(aDocumentDetails.getRecordType()).equals("")) {
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
					getDocumentDetailDialogCtrl().doFillDocumentDetails(this.documentDetailList);
					// send the data back to customer
					if(checkListDocTypeMap != null && checkListDocTypeMap.containsKey(aDocumentDetails.getDocCategory())){
						List<Listitem>  list = checkListDocTypeMap.get(aDocumentDetails.getDocCategory());
						for (int i = 0; i < list.size(); i++) {
							list.get(i).setDisabled(false);
							list.get(i).setSelected(true);
							list.get(i).setDisabled(true);
						}
					}
					closeWindow();
				}
			}
		} catch (final DataAccessException e) {
			logger.error(e);
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

		if (getDocumentDetailDialogCtrl().getDocumentDetailsList() != null && getDocumentDetailDialogCtrl().getDocumentDetailsList().size() > 0) {
			for (int i = 0; i < getDocumentDetailDialogCtrl().getDocumentDetailsList().size(); i++) {
				DocumentDetails documentDetails = getDocumentDetailDialogCtrl().getDocumentDetailsList().get(i);

				if (documentDetails.getDocCategory().equals(aDocumentDetails.getDocCategory())) { // Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (tranType == PennantConstants.TRAN_DEL) {
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
						if (tranType != PennantConstants.TRAN_UPD) {
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinDocumentDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$docCategory(Event  event){
		
		
	}
	
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("DocumentDetails");
		notes.setReference(getReference());
		notes.setVersion(getDocumentDetails().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/** 
	 * Get the Reference value
	 */
	private String getReference() {
		return getDocumentDetails().getDocId() + PennantConstants.KEY_SEPERATOR + getDocumentDetails().getReferenceId();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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

	
	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(
			DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
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
			boolean isSupported = true;
			String docType = "";
			if (media.getContentType().equals("application/pdf")) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if (media.getContentType().equals("image/jpeg") || media.getContentType().equals("image/png")) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
			} else if (media.getName().endsWith(".msg")) {
				docType = PennantConstants.DOC_TYPE_MSG;
			} else {
				isSupported = false;
				PTMessageUtils.showErrorMessage(Labels.getLabel("UnSupported_Document"));
			}
			if (isSupported) {
				String fileName = media.getName();
				byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
				// Data Fill by QR Bar Code Reader
				if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
					this.finDocumentPdfView.setContent(new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

				} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
					this.finDocumentPdfView.setContent(new AMedia("document.jpg", "jpg", "image", new ByteArrayInputStream(ddaImageData)));
				}else if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
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

				if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
					this.docDiv.setVisible(true);
					this.finDocumentPdfView.setVisible(false);
				}else{
					this.docDiv.setVisible(false);
					this.finDocumentPdfView.setVisible(true);
				}
				
				
				textbox.setValue(fileName);
				if (textbox.getAttribute("data") == null) {
					DocumentDetails documentDetails = new DocumentDetails("FINANCEMAIN", "", docType, fileName, ddaImageData);
					textbox.setAttribute("data", documentDetails);
				} else {
					DocumentDetails documentDetails = (DocumentDetails) textbox.getAttribute("data");
					documentDetails.setDoctype(docType);
					documentDetails.setDocImage(ddaImageData);
					textbox.setAttribute("data", documentDetails);
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
	
	/*private void doCheckDocumentOwner(){
		if (getDocumentDetails().getLastMntBy() !=0 && getUserWorkspace().getLoginUserDetails().getLoginUsrID() !=getDocumentDetails().getLastMntBy()) {
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnUploadDoc.setVisible(false);
		}
	} 
	*/
	
	
}
