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
 * FileName    		:  LegalDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.legal.legaldetail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennant.backend.model.legal.LegalECDetail;
import com.pennant.backend.model.legal.LegalNote;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennant.backend.service.legal.LegalDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Legal/LegalDetail/legalDetailDialog.zul file. <br>
 */
public class LegalDetailDialogCtrl extends GFCBaseCtrl<LegalDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LegalDetailDialogCtrl.class);
	
	protected Window window_LegalDetailDialog;
	protected Label window_LegalDetailDialog_title;

	protected Tab applicationDetailsTab;
	protected Tab propertryDetailsTab;
	protected Tab documentDetailTab;
	//protected Tab querryModuleTab;
	protected Tab propertryTittleTab;
	protected Tab encumbranceCertificateTab;
	protected Tab legalNotesTab;
	protected Tab coventsTab;
	protected Tab legalDecisionTab;

	protected Tabpanel applicationDetailTabPanel;
	protected Tabpanel propertyDetailTabPanel;
	protected Tabpanel documentDetailTabPanel;
	//protected Tabpanel querryModuleTabpanel;
	protected Tabpanel propertyTittleTabPanel;
	protected Tabpanel propertyEncumbranceTabPanel;
	protected Tabpanel legalDecisionTabPanel;

	//Tabs Headers Labels
	protected Label label_LoanReference;
	protected Label label_CollateralRef;
	protected Label label_LoanBranch;
	protected Label label_Date;

	protected Label label_PLoanReference;
	protected Label label_PCollateralRef;
	protected Label label_PLoanBranch;
	protected Label label_PDate;

	protected Label label_DLoanReference;
	protected Label label_DCollateralRef;
	protected Label label_DLoanBranch;
	protected Label label_DDate;

	protected Label label_PTLoanReference;
	protected Label label_PTCollateralRef;
	protected Label label_PTLoanBranch;
	protected Label label_PTDate;

	protected Label label_ECLoanReference;
	protected Label label_ECCollateralRef;
	protected Label label_ECLoanBranch;
	protected Label label_ECDate;

	protected Label label_NTLoanReference;
	protected Label label_NTCollateralRef;
	protected Label label_NTLoanBranch;
	protected Label label_NTDate;

	protected Label label_LDLoanReference;
	protected Label label_LDCollateralRef;
	protected Label label_LDLoanBranch;
	protected Label label_LDDate;
	
	// Applicant details
	protected Button btnNew_ApplicantDetails;
	protected Listbox listBoxLegalApplicantDetail;

	// Property details
	protected Button btnNew_PropertyDetails;
	protected Listbox listBoxLegalPropertyDetail;
	protected Textbox scheduleLevelArea;

	// Document details
	protected Button btnNew_DocumentDetails;
	protected Listbox listBoxLegalDocument;
	protected Listheader listheader_DocumentTypeVerify;
	protected Listheader listheader_DocumentTypeAppprove;
	protected Listheader listheader_DocumentTypeAccepted;

	// Property Title details
	protected Button btnNew_PropertyTitleDetails;
	protected Listbox listBoxLegalPropertyTitle;
	protected Textbox propertyDetailModt;
	
	// EC details
	protected Button btnNew_ECTitleDetails;
	protected Listbox listBoxLegalECDetail;
	protected Datebox propertyDetailECDate;
	protected Textbox ecPropertyOwnerName;

	// Notes details
	protected Button btnNew_NotesDetails;
	protected Listbox listBoxLegalNote;
	
	//Legal Decision
	protected Combobox legalDecision;
	protected Textbox legalRemarks;

	private LegalDetail legalDetail;
	private transient LegalDetailListCtrl legalDetailListCtrl;
	private transient LegalDetailService legalDetailService;
	
	@Autowired
	private SearchProcessor searchProcessor;
	private EventManager eventManager;
	
	// Module Usage
	private List<LegalApplicantDetail> applicantDetailList = null;
	private List<LegalPropertyDetail> legalPropertyDetailList = null;
	private List<LegalDocument> legalDocumentList = null;
	private List<LegalPropertyTitle> propertyTitleList = null;
	private List<LegalECDetail> ecdDetailList = null;
	private List<LegalNote> legalNotesList = null;
	
	private boolean documentsValidate = false;
	
	/**
	 * default constructor.<br>
	 */
	public LegalDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalDetailDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.legalDetail.getLegalReference()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_LegalDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_LegalDetailDialog);

		try {
			// Get the required arguments.
			this.legalDetail = (LegalDetail) arguments.get("legalDetail");
			this.legalDetailListCtrl = (LegalDetailListCtrl) arguments.get("legalDetailListCtrl");

			if (this.legalDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			LegalDetail legalDetail = new LegalDetail();
			BeanUtils.copyProperties(this.legalDetail, legalDetail);
			this.legalDetail.setBefImage(legalDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalDetail.isWorkflow(), this.legalDetail.getWorkflowId(),
					this.legalDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.scheduleLevelArea.setMaxlength(2000);
		
		this.propertyDetailModt.setMaxlength(3000);
		
		this.propertyDetailECDate.setFormat(PennantConstants.dateFormat);
		this.ecPropertyOwnerName.setMaxlength(3000);
		
		fillComboBox(legalDecision, "", PennantStaticListUtil.getDecisionList(), "");
		this.legalRemarks.setMaxlength(3000);
		
		setStatusDetails();
		setWindowTittle();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalDetailDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnDelete.setVisible(false);

		this.btnNew_ApplicantDetails
				.setVisible(getUserWorkspace().isAllowed("button_LegalDetailDialog_btnNew_ApplicantDetails"));
		this.btnNew_PropertyDetails
				.setVisible(getUserWorkspace().isAllowed("button_LegalDetailDialog_btnNew_PropertyDetails"));
		this.btnNew_DocumentDetails
				.setVisible(getUserWorkspace().isAllowed("button_LegalDetailDialog_btnNew_DocumentDetails"));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.legalDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		legalDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.legalDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param legalDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(LegalDetail legalDetail) {
		logger.debug(Literal.ENTERING);
		if (legalDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(legalDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}
		doWriteBeanToComponents(legalDetail);
		setDialog(DialogType.EMBEDDED);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param legalDetail
	 * 
	 */
	public void doWriteBeanToComponents(LegalDetail aLegalDetail) {
		logger.debug(Literal.ENTERING);
		
		boolean selectedTab = false;

		// Applicant Details
		if (this.applicationDetailsTab.isVisible()) {
			selectedTab = true;
		}
		doFillApplicantDetails(aLegalDetail.getApplicantDetailList());

		// Property Details
		if (this.propertryDetailsTab.isVisible()) {
			selectedTab = true;
		}
		doFillPropertyDetails(aLegalDetail.getPropertyDetailList());
		this.scheduleLevelArea.setValue(aLegalDetail.getSchedulelevelArea());

		// Title details
		if (this.propertryTittleTab.isVisible()) {
			this.propertyDetailModt.setValue(aLegalDetail.getPropertyDetailModt());
			selectedTab = true;
		}
		doFillPropertyTitleDetails(aLegalDetail.getPropertyTitleList());

		// EC details
		if (this.encumbranceCertificateTab.isVisible()) {
			this.propertyDetailECDate.setValue(aLegalDetail.getPropertyDetailECDate());
			this.ecPropertyOwnerName.setValue(aLegalDetail.getEcPropertyOwnerName());
			selectedTab = true;
		}
		doFillECDDetails(aLegalDetail.getEcdDetailsList());

		// Legal Notes details
		if (this.legalNotesTab.isVisible()) {
			selectedTab = true;
		}
		doFillLegalNotesDetails(aLegalDetail.getLegalNotesList());

		// LegalDecision
		fillComboBox(this.legalDecision, aLegalDetail.getLegalDecision(), PennantStaticListUtil.getDecisionList(), "");
		this.legalRemarks.setValue(aLegalDetail.getLegalRemarks());

		// Query MOdule
		//appendQueryModule();
		
		// Document details
		if (this.documentDetailTab.isVisible()) {
			if (!selectedTab) {
				this.documentDetailTab.setSelected(true);
			}
		}
		doFillDocumentDetails(aLegalDetail.getDocumentList());

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.legalDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		this.recordStatus.setValue(aLegalDetail.getRecordStatus());
		
		setLabels(aLegalDetail);
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalDetail
	 */
	public void doWriteComponentsToBean(LegalDetail aLegalDetail) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.scheduleLevelArea);
		Clients.clearWrongValue(this.propertyDetailModt);
		Clients.clearWrongValue(this.propertyDetailECDate);
		Clients.clearWrongValue(this.ecPropertyOwnerName);
		Clients.clearWrongValue(this.legalDecision);
		Clients.clearWrongValue(this.legalRemarks);
		
		this.scheduleLevelArea.setConstraint("");
		this.propertyDetailModt.setConstraint("");
		this.propertyDetailECDate.setConstraint("");
		this.ecPropertyOwnerName.setConstraint("");
		this.legalDecision.setConstraint("");
		this.legalRemarks.setConstraint("");
		
		this.scheduleLevelArea.setErrorMessage("");
		this.propertyDetailModt.setErrorMessage("");
		this.propertyDetailECDate.setErrorMessage("");
		this.ecPropertyOwnerName.setErrorMessage("");
		this.legalDecision.setErrorMessage("");
		this.legalRemarks.setErrorMessage("");
		
		logger.debug(Literal.LEAVING);
	}
	/**
	 * Deletes a LegalDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final LegalDetail aLegalDetail = new LegalDetail();
		BeanUtils.copyProperties(this.legalDetail, aLegalDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aLegalDetail.getLegalReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aLegalDetail.getRecordType()).equals("")) {
				aLegalDetail.setVersion(aLegalDetail.getVersion() + 1);
				aLegalDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aLegalDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aLegalDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aLegalDetail.getNextTaskId(),
							aLegalDetail);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aLegalDetail, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		
		//APplicant details
		this.applicationDetailsTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_applicationDetailsTabEnquiry"));
		if (!this.applicationDetailsTab.isVisible()) {
			this.applicationDetailsTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_applicationDetailsTab"));
		}
		
		// Property Details
		this.propertryDetailsTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_propertryDetailsTab"));
		readOnlyComponent(isReadOnly("LegalDetailDialog_ScheduleLevelArea"), this.scheduleLevelArea);
		
		// Document Details
		this.documentDetailTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_documentDetailTab"));
		this.listheader_DocumentTypeVerify
				.setVisible(getUserWorkspace().isAllowed("LegalDetailDialog_Listheader_DocumentTypeVerify"));
		this.listheader_DocumentTypeAppprove
				.setVisible(getUserWorkspace().isAllowed("LegalDetailDialog_Listheader_DocumentTypeAppprover"));
		this.listheader_DocumentTypeAccepted
				.setVisible(getUserWorkspace().isAllowed("LegalDetailDialog_Listheader_DocumentTypeAccepted"));

		//Query module
		//this.querryModuleTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_querryModuleTab"));
		
		//Property Title
		this.propertryTittleTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_propertryTittleTab"));
		readOnlyComponent(isReadOnly("LegalDetailDialog_PropertyDetailModt"), this.propertyDetailModt);
		
		//Ec details
		this.encumbranceCertificateTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_encumbranceCertificateTab"));
		readOnlyComponent(isReadOnly("LegalDetailDialog_PropertyDetailECDate"), this.propertyDetailECDate);
		readOnlyComponent(isReadOnly("LegalDetailDialog_ECPropertyOwnerName"), this.ecPropertyOwnerName);

		//Notes Tab
		this.legalNotesTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_legalNotesTab"));
	
		//Decision details
		this.legalDecisionTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_legalDecisionTab"));
		readOnlyComponent(isReadOnly("LegalDetailDialog_LegalDecision"), this.legalDecision);
		readOnlyComponent(isReadOnly("LegalDetailDialog_LegalRemarks"), this.legalRemarks);
		
		//this.coventsTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_coventsTab"));
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final LegalDetail aLegalDetail = new LegalDetail();
		BeanUtils.copyProperties(this.legalDetail, aLegalDetail);
		boolean isNew = false;
		
		// Setting the formatted fin amount for approval process
		aLegalDetail.setFinAmount(PennantAppUtil.formateAmount(aLegalDetail.getFinAmount(), CurrencyUtil.getFormat(aLegalDetail.getFinCcy())));

		doRemoveValidation();
		doWriteComponentsToBean(aLegalDetail);

		// Applicant details
		aLegalDetail.setApplicantDetailList(getApplicantDetailList());
		if (this.applicationDetailsTab.isVisible() && CollectionUtils.isEmpty(getApplicantDetailList())) {
			this.applicationDetailsTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_ApplicantDetails_Validation"));
			return;
		}

		// Property details
		aLegalDetail.setPropertyDetailList(getLegalPropertyDetailList());
		if (this.propertryDetailsTab.isVisible() && CollectionUtils.isEmpty(getLegalPropertyDetailList())) {
			this.propertryDetailsTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_PropertyDetails_Validation"));
			return;
		}
		if (this.propertryDetailsTab.isVisible() && CollectionUtils.isNotEmpty(getLegalPropertyDetailList())) {
			if (StringUtils.trimToNull(this.scheduleLevelArea.getValue()) == null) {
				this.propertryDetailsTab.setSelected(true);
				throw new WrongValueException(this.scheduleLevelArea, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_LegalApplicantDetails_SchedulelevelArea.value") }));
			} else {
				aLegalDetail.setSchedulelevelArea(this.scheduleLevelArea.getValue());
			}
		}
		
		//Document Details
		aLegalDetail.setDocumentList(getLegalDocumentList());
		if (this.documentDetailTab.isVisible() && CollectionUtils.isEmpty(getLegalDocumentList())) {
			this.documentDetailTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_DocumentDetails_Validation"));
			return;
		}
		
		// Title details
		aLegalDetail.setPropertyTitleList(getPropertyTitleList());
		if (this.propertryTittleTab.isVisible() && CollectionUtils.isEmpty(getPropertyTitleList())) {
			this.propertryTittleTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_PropertryTittle_Validation"));
			return;
		}
		if (this.propertryTittleTab.isVisible() && CollectionUtils.isNotEmpty(getPropertyTitleList())) {
			if (StringUtils.trimToNull(this.propertyDetailModt.getValue()) == null) {
				this.propertryTittleTab.setSelected(true);
				throw new WrongValueException(this.propertyDetailModt, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_LegalPropertyTitleDialog_MODT.value") }));
			} else {
				aLegalDetail.setPropertyDetailModt(this.propertyDetailModt.getValue());
			}
		}
		
		// EC  details
		aLegalDetail.setEcdDetailsList(getEcdDetailList());
		if (this.encumbranceCertificateTab.isVisible() && CollectionUtils.isEmpty(getEcdDetailList())) {
			this.encumbranceCertificateTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_ECDetails_Validation"));
			return;
		}
		if (this.encumbranceCertificateTab.isVisible() && CollectionUtils.isNotEmpty(getEcdDetailList())) {
			if (this.propertyDetailECDate.getValue() == null) {
				this.encumbranceCertificateTab.setSelected(true);
				throw new WrongValueException(this.propertyDetailECDate, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_LegalPropertyTitleDialog_ECdate.value") }));
			} else if (StringUtils.trimToNull(this.ecPropertyOwnerName.getValue()) == null) {
				this.encumbranceCertificateTab.setSelected(true);
				throw new WrongValueException(this.ecPropertyOwnerName, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_LegalPropertyTitleDialog_ECPropertyOwnerName.value") }));
			} else {
				aLegalDetail.setPropertyDetailECDate(this.propertyDetailECDate.getValue());
				aLegalDetail.setEcPropertyOwnerName(this.ecPropertyOwnerName.getValue());
			}
		}
		
		// Legal Notes details
		aLegalDetail.setLegalNotesList(getLegalNotesList());
		if (this.legalNotesTab.isVisible() && CollectionUtils.isEmpty(getLegalNotesList())) {
			this.legalNotesTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_LegalNotes_Validation"));
			return;
		}
		
		//Legal Decision
		if (this.legalDecisionTab.isVisible()) {
			String legalDecision = this.legalDecision.getSelectedItem().getValue();
			if (StringUtils.trimToNull(legalDecision) == null || PennantConstants.List_Select.equals(legalDecision)) {
				this.legalDecisionTab.setSelected(true);
				throw new WrongValueException(this.legalDecision, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_LegalDetailDialog_LegalDecision.value")}));
			} else if(StringUtils.trimToNull(legalRemarks.getValue()) == null){
				this.legalDecisionTab.setSelected(true);
				throw new WrongValueException(this.legalRemarks, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_LegalDetailDialog_LegalRemarks.value") }));
			} else {
				aLegalDetail.setLegalDecision(legalDecision);
				aLegalDetail.setLegalRemarks(legalRemarks.getValue());
			}
		}
		
		// Final validations of document details
		if (this.documentDetailTab.isVisible() && CollectionUtils.isNotEmpty(getLegalDocumentList())) {
			if (!isDocumentsValidate()) {
				this.documentDetailTab.setSelected(true);
				MessageUtil.showError(Labels.getLabel("label_LegalDetail_DocumentDetails_List_Validation"));
				return;
			}
		}
		
		isNew = aLegalDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalDetail.getRecordType())) {
				aLegalDetail.setVersion(aLegalDetail.getVersion() + 1);
				if (isNew) {
					aLegalDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalDetail.setNewRecord(true);
				}
			}
		} else {
			aLegalDetail.setVersion(aLegalDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(aLegalDetail, tranType)) {
				
				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						// Send message Notification to Users
						String reference = aLegalDetail.getCollateralReference();
						String nextRoleCodes = aLegalDetail.getNextRoleCode();
						if (StringUtils.isNotEmpty(nextRoleCodes)) {
							Notify notify = Notify.valueOf("ROLE");
							String[] to = nextRoleCodes.split(",");
							if (StringUtils.isNotEmpty(reference)) {
								if (!PennantConstants.RCD_STATUS_CANCELLED
										.equalsIgnoreCase(aLegalDetail.getRecordStatus())) {
									getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference" + ":" + reference, notify, to);
								}
							} else {
								getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), notify, to);
							}
						}
					}
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}

				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aLegalDetail.getRoleCode(),
						aLegalDetail.getNextRoleCode(), aLegalDetail.getCollateralReference(), " Collateral ",
						aLegalDetail.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(LegalDetail aLegalDetail, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aLegalDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLegalDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLegalDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aLegalDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLegalDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aLegalDetail);
				}

				if (isNotesMandatory(taskId, aLegalDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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
			aLegalDetail.setTaskId(taskId);
			aLegalDetail.setNextTaskId(nextTaskId);
			aLegalDetail.setRoleCode(getRole());
			aLegalDetail.setNextRoleCode(nextRoleCode);

			// Applicant details
			List<LegalApplicantDetail> legalApplicantDetails = aLegalDetail.getApplicantDetailList();
			if (legalApplicantDetails != null && !legalApplicantDetails.isEmpty()) {
				for (LegalApplicantDetail details : legalApplicantDetails) {
					details.setLegalId(aLegalDetail.getLegalId());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setRecordStatus(aLegalDetail.getRecordStatus());
					details.setWorkflowId(aLegalDetail.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aLegalDetail.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aLegalDetail.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// Property details
			List<LegalPropertyDetail> legalPropertyDetail = aLegalDetail.getPropertyDetailList();
			if (legalPropertyDetail != null && !legalPropertyDetail.isEmpty()) {
				for (LegalPropertyDetail details : legalPropertyDetail) {
					details.setLegalId(aLegalDetail.getLegalId());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setRecordStatus(aLegalDetail.getRecordStatus());
					details.setWorkflowId(aLegalDetail.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aLegalDetail.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aLegalDetail.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
			// Document details
			List<LegalDocument> legaldDocumentDetails = aLegalDetail.getDocumentList();
			if (legaldDocumentDetails != null && !legaldDocumentDetails.isEmpty()) {
				for (LegalDocument details : legaldDocumentDetails) {
					details.setLegalId(aLegalDetail.getLegalId());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setRecordStatus(aLegalDetail.getRecordStatus());
					details.setWorkflowId(aLegalDetail.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aLegalDetail.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aLegalDetail.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(aLegalDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aLegalDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aLegalDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aLegalDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		LegalDetail aLegalDetail = (LegalDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = legalDetailService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = legalDetailService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = legalDetailService.doApprove(auditHeader);

						if (aLegalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = legalDetailService.doReject(auditHeader);
						if (aLegalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_LegalDetailDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_LegalDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.legalDetail), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 ********************************************************************************************
	 * Applicant Details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the
	 * btnNew_ApplicantDetails button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_ApplicantDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalApplicantDetail legalApplicantDetail = new LegalApplicantDetail();
		legalApplicantDetail.setNewRecord(true);
		legalApplicantDetail.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("legalApplicantDetail", legalApplicantDetail);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalApplicantDetailDialog.zul",
					window_LegalDetailDialog, map);
		} catch (Exception e) {
			logger.debug(Literal.LEAVING);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLegalApplicantDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalApplicantDetail.getSelectedItem();

		if (item != null) {
			LegalApplicantDetail applicantDetail = (LegalApplicantDetail) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(applicantDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("legalApplicantDetail", applicantDetail);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enquiry", getUserWorkspace().isAllowed("tab_LegalDetailDialog_applicationDetailsTabEnquiry"));
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalApplicantDetailDialog.zul",
							window_LegalDetailDialog, map);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Rendering the application details
	 * 
	 * @param applicantDetailList
	 */
	public void doFillApplicantDetails(List<LegalApplicantDetail> applicantDetailList) {
		logger.debug(Literal.ENTERING);

		this.listBoxLegalApplicantDetail.getItems().clear();

		setApplicantDetailList(applicantDetailList);

		if (applicantDetailList != null && !applicantDetailList.isEmpty()) {

			for (LegalApplicantDetail applicantDetail : applicantDetailList) {

				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(applicantDetail.getTitle());
				lc.setParent(item);
				lc = new Listcell(applicantDetail.getPropertyOwnersName());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formateInt(applicantDetail.getAge()));
				lc.setParent(item);
				lc = new Listcell(applicantDetail.getRelationshipType());
				lc.setParent(item);
				lc = new Listcell(applicantDetail.getIDTypeName());
				lc.setParent(item);
				lc = new Listcell(applicantDetail.getIDNo());
				lc.setParent(item);
				lc = new Listcell(applicantDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(applicantDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("object", applicantDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalApplicantDetailItemDoubleClicked");
				this.listBoxLegalApplicantDetail.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 ********************************************************************************************
	 * Property Details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the
	 * btnNew_PropertyDetails button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_PropertyDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalPropertyDetail propertyDetail = new LegalPropertyDetail();
		propertyDetail.setNewRecord(true);
		propertyDetail.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("legalPropertyDetail", propertyDetail);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalPropertyDetailDialog.zul",
					window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLegalPropertyDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalPropertyDetail.getSelectedItem();

		if (item != null) {
			int index = item.getIndex();
			LegalPropertyDetail legalPropertyDetail = (LegalPropertyDetail) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalPropertyDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("legalPropertyDetail", legalPropertyDetail);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				;
				map.put("index", index);
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalPropertyDetailDialog.zul",
							window_LegalDetailDialog, map);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Rendering the application details
	 * 
	 * @param propertyDetailList
	 */
	public void doFillPropertyDetails(List<LegalPropertyDetail> propertyDetailList) {
		logger.debug(Literal.ENTERING);

		this.listBoxLegalPropertyDetail.getItems().clear();
		setLegalPropertyDetailList(propertyDetailList);

		BigDecimal totMeasurementVal = BigDecimal.ZERO;

		if (propertyDetailList != null && !propertyDetailList.isEmpty()) {
			for (LegalPropertyDetail legalPropertyDetail : propertyDetailList) {

				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(legalPropertyDetail.getScheduleType());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getPropertySchedule());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getPropertyType());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getNorthBy());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getSouthBy());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getEastBy());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getWestBy());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(legalPropertyDetail.getMeasurement()));
				lc.setStyle("text-align:right;");

				totMeasurementVal = totMeasurementVal.add(legalPropertyDetail.getMeasurement());

				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getRegistrationOffice());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getPropertyOwner());
				lc.setParent(item);
				lc = new Listcell(legalPropertyDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(legalPropertyDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("object", legalPropertyDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalPropertyDetailItemDoubleClicked");
				this.listBoxLegalPropertyDetail.appendChild(item);
			}

			Listcell listcell;
			Listitem item;

			// Summary
			item = new Listitem();
			listcell = new Listcell("Summmary");
			listcell.setStyle("font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(totMeasurementVal).concat(" Sq.ft"));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			this.listBoxLegalPropertyDetail.appendChild(item);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 ********************************************************************************************
	 * Property document Details *
	 ********************************************************************************************
	 */
	public void onClick$btnNew_DocumentDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalDocument legalDocument = new LegalDocument();
		legalDocument.setNewRecord(true);
		legalDocument.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("legalDocument", legalDocument);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalDocumentDialog.zul",
					window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLegalDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalDocument.getSelectedItem();

		if (item != null) {
			LegalDocument legalDocument = (LegalDocument) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalDocument.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("legalDocument", legalDocument);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				;
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalDocumentDialog.zul",
							window_LegalDetailDialog, map);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Rendering the application details
	 * 
	 * @param propertydocumentDetailList
	 */
	public void doFillDocumentDetails(List<LegalDocument> documentDetailList) {
		logger.debug(Literal.ENTERING);

		this.listBoxLegalDocument.getItems().clear();
		setLegalDocumentList(documentDetailList);

		int i = 0;
		
		ArrayList<ValueLabel> documentTypesList = PennantStaticListUtil.getDocumentTypes();
		ArrayList<ValueLabel> documentCategoryList = new ArrayList<>();
		ArrayList<ValueLabel> documentAccepted = PennantStaticListUtil.getDocumentAcceptedList();
		
		if (documentDetailList != null && !documentDetailList.isEmpty()) {
			for (LegalDocument legalDocument : documentDetailList) {

				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(String.valueOf(++i));
				lc.setParent(item);

				lc = new Listcell(DateUtility.formateDate(legalDocument.getDocumentDate(), PennantConstants.dateFormat));
				lc.setParent(item);

				lc = new Listcell(legalDocument.getDocumentNo());
				lc.setParent(item);
				
				lc = new Listcell(PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentType(), documentTypesList));
				lc.setParent(item);

				lc = new Listcell(PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentCategory(), documentCategoryList));
				lc.setParent(item);

				lc = new Listcell(legalDocument.getScheduleType());
				lc.setParent(item);

				lc = new Listcell(PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentTypeVerify(), documentTypesList));
				lc.setParent(item);

				lc = new Listcell(PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentTypeApprove(), documentTypesList));
				lc.setParent(item);

				lc = new Listcell(PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentAccepted(), documentAccepted));
				lc.setParent(item);

				lc = new Listcell(legalDocument.getRecordStatus());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(legalDocument.getRecordType()));
				lc.setParent(item);

				item.setAttribute("object", legalDocument);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalDocumentItemDoubleClicked");
				this.listBoxLegalDocument.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 ********************************************************************************************
	 * Property Title Details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the
	 * btnNew_PropertyTitleDetails button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_PropertyTitleDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalPropertyTitle legalPropertyTitle = new LegalPropertyTitle();
		legalPropertyTitle.setNewRecord(true);
		legalPropertyTitle.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("legalPropertyTitle", legalPropertyTitle);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalPropertyTitleDialog.zul", window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLegalPropertyTitleItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalPropertyTitle.getSelectedItem();
		if (item != null) {
			LegalPropertyTitle legalPropertyTitle = (LegalPropertyTitle) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalPropertyTitle.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("legalPropertyTitle", legalPropertyTitle);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalPropertyTitleDialog.zul", window_LegalDetailDialog, map);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Rendering the application details
	 * 
	 * @param propertyDetailList
	 */
	public void doFillPropertyTitleDetails(List<LegalPropertyTitle> propertyTitleList) {
		logger.debug(Literal.ENTERING);

		this.listBoxLegalPropertyTitle.getItems().clear();
		setPropertyTitleList(propertyTitleList);
		int i = 0;
		
		if (propertyTitleList != null && !propertyTitleList.isEmpty()) {
			for (LegalPropertyTitle title : propertyTitleList) {
				Listitem item = new Listitem();
				Listcell lc;
				
				lc = new Listcell(String.valueOf(++i));
				lc.setParent(item);
				
				lc = new Listcell(title.getTitle());
				lc.setParent(item);
				
				lc = new Listcell(title.getRecordStatus());
				lc.setParent(item);
				
				lc = new Listcell(PennantJavaUtil.getLabel(title.getRecordType()));
				lc.setParent(item);
				
				item.setAttribute("object", title);
				
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalPropertyTitleItemDoubleClicked");
				this.listBoxLegalPropertyTitle.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 ********************************************************************************************
	 * EC Details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the
	 * btnNew_ECTitleDetails button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_ECTitleDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalECDetail legalECDetail = new LegalECDetail();
		legalECDetail.setNewRecord(true);
		legalECDetail.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("legalECDetail", legalECDetail);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalECDetailDialog.zul",window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLegalECDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		Listitem item = this.listBoxLegalECDetail.getSelectedItem();
		if (item != null) {
			LegalECDetail legalECDetail = (LegalECDetail) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalECDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("legalECDetail", legalECDetail);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalECDetailDialog.zul", window_LegalDetailDialog, map);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Rendering the application details
	 * 
	 * @param legalECDetailList
	 */
	public void doFillECDDetails(List<LegalECDetail> legalECDetailList) {
		logger.debug(Literal.ENTERING);

		this.listBoxLegalECDetail.getItems().clear();
		setEcdDetailList(legalECDetailList);

		if (legalECDetailList != null && !legalECDetailList.isEmpty()) {
			for (LegalECDetail legalECDetail : legalECDetailList) {
				Listitem item = new Listitem();
				Listcell lc;
			  	lc = new Listcell(DateUtility.formateDate(legalECDetail.getEcDate(), PennantConstants.dateFormat));
			  	lc.setParent(item);
			  	lc = new Listcell(legalECDetail.getDocument());
				lc.setParent(item);
			  	lc = new Listcell(legalECDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(legalECDetail.getRecordType()));
				lc.setParent(item);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalECDetailItemDoubleClicked");
				item.setAttribute("object", legalECDetail);
				this.listBoxLegalECDetail.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 ********************************************************************************************
	 * Legal Notes details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the
	 * btnNew_NotesDetails button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_NotesDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalNote legalNote = new LegalNote();
		legalNote.setNewRecord(true);
		legalNote.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("legalNote", legalNote);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalNoteDialog.zul", window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLegalNoteItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		Listitem item = this.listBoxLegalNote.getSelectedItem();
		if (item != null) {
			LegalNote legalNote = (LegalNote) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalNote.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("legalNote", legalNote);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalNoteDialog.zul", window_LegalDetailDialog, map);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Rendering the application details
	 * 
	 * @param legalNoteslList
	 */
	public void doFillLegalNotesDetails(List<LegalNote> legalNoteslList) {
		logger.debug(Literal.ENTERING);

		this.listBoxLegalNote.getItems().clear();
		setLegalNotesList(legalNoteslList);
		if (legalNoteslList != null && !legalNoteslList.isEmpty()) {
			for (LegalNote legalNote : legalNoteslList) {
				
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(legalNote.getCode());
				lc.setParent(item);
				lc = new Listcell(legalNote.getDescription());
				lc.setParent(item);
				lc = new Listcell(legalNote.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(legalNote.getRecordType()));
				lc.setParent(item);
				item.setAttribute("object", legalNote);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalNoteItemDoubleClicked");
				this.listBoxLegalNote.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 ********************************************************************************************
	 * Legal Decision details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the
	 * btnNew_LegalDecisionDetails button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_LegalDecisionDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalNote legalNote = new LegalNote();
		legalNote.setNewRecord(true);
		legalNote.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("legalNote", legalNote);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalDecisionDialog.zul",
					window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	
	/**
	 * Setting the window title based on the role
	 */
	private void setWindowTittle() {
		String roleCode = getRole();
		if (StringUtils.isEmpty(roleCode)) {
			this.window_LegalDetailDialog_title.setValue(Labels.getLabel("window_LegalDetailDialog.title"));
			return;
		}
		Search search = new Search(SecurityRole.class);
		search.addTabelName("secroles");
		search.addFilterEqual("rolecd", roleCode);
		List<SecurityRole> securityRolesList = searchProcessor.getResults(search);

		if (CollectionUtils.isEmpty(securityRolesList)) {
			this.window_LegalDetailDialog_title.setValue(Labels.getLabel("window_LegalDetailDialog.title"));
			return;
		}
		this.window_LegalDetailDialog_title.setValue(securityRolesList.get(0).getRoleDesc());
	}
	
	/**
	 * Setting the tabs header labels
	 * @param aLegalDetail
	 */
	private void setLabels(LegalDetail aLegalDetail) {

		String legalDate = "";
		if (aLegalDetail.getLegalDate() != null) {
			legalDate = DateUtility.formatDate(aLegalDetail.getLegalDate(), DateFormat.LONG_DATE.getPattern());
		}
		this.label_LoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_CollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_LoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_Date.setValue(legalDate);

		this.label_PLoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_PCollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_PLoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_PDate.setValue(legalDate);

		this.label_DLoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_DCollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_DLoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_DDate.setValue(legalDate);

		this.label_PTLoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_PTCollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_PTLoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_PTDate.setValue(legalDate);

		this.label_ECLoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_ECCollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_ECLoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_ECDate.setValue(legalDate);

		this.label_NTLoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_NTCollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_NTLoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_NTDate.setValue(legalDate);

		this.label_LDLoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_LDCollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_LDLoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_LDDate.setValue(legalDate);
	}

	/**
	 * @param aLegalDetail
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(LegalDetail aLegalDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalDetail.getBefImage(), aLegalDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aLegalDetail.getUserDetails(),
				getOverideMap());
	}

	// Getters and setters
	public void setLegalDetailService(LegalDetailService legalDetailService) {
		this.legalDetailService = legalDetailService;
	}

	public List<LegalPropertyDetail> getLegalPropertyDetailList() {
		return legalPropertyDetailList;
	}

	public void setLegalPropertyDetailList(List<LegalPropertyDetail> legalPropertyDetailList) {
		this.legalPropertyDetailList = legalPropertyDetailList;
	}

	public List<LegalDocument> getLegalDocumentList() {
		return legalDocumentList;
	}

	public void setLegalDocumentList(List<LegalDocument> legalDocumentList) {
		this.legalDocumentList = legalDocumentList;
	}

	public List<LegalApplicantDetail> getApplicantDetailList() {
		return applicantDetailList;
	}

	public void setApplicantDetailList(List<LegalApplicantDetail> applicantDetailList) {
		this.applicantDetailList = applicantDetailList;
	}
	
	public LegalDetail getLegalDetail() {
		return legalDetail;
	}

	public void setLegalDetail(LegalDetail legalDetail) {
		this.legalDetail = legalDetail;
	}

	public List<LegalPropertyTitle> getPropertyTitleList() {
		return propertyTitleList;
	}

	public void setPropertyTitleList(List<LegalPropertyTitle> propertyTitleList) {
		this.propertyTitleList = propertyTitleList;
	}

	public List<LegalECDetail> getEcdDetailList() {
		return ecdDetailList;
	}

	public void setEcdDetailList(List<LegalECDetail> ecdDetailList) {
		this.ecdDetailList = ecdDetailList;
	}

	public List<LegalNote> getLegalNotesList() {
		return legalNotesList;
	}

	public void setLegalNotesList(List<LegalNote> legalNotesList) {
		this.legalNotesList = legalNotesList;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public boolean isDocumentsValidate() {
		return documentsValidate;
	}

	public void setDocumentsValidate(boolean documentsValidate) {
		this.documentsValidate = documentsValidate;
	}

}
