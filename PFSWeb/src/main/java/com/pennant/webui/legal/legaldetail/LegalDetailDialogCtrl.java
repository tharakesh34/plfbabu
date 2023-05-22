/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : LegalDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-06-2018 * *
 * Modified Date : 16-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legaldetail;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennant.backend.model.legal.LegalECDetail;
import com.pennant.backend.model.legal.LegalNote;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.legal.LegalDetailService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.AgreementEngine;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.finance.financemain.FinCovenantTypeListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.engine.workflow.Operation;
import com.pennanttech.pennapps.core.engine.workflow.ProcessUtil;
import com.pennanttech.pennapps.core.engine.workflow.model.ServiceTask;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This is the controller class for the /WEB-INF/pages/Legal/LegalDetail/legalDetailDialog.zul file. <br>
 */
public class LegalDetailDialogCtrl extends GFCBaseCtrl<LegalDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalDetailDialogCtrl.class);

	protected Window window_LegalDetailDialog;
	protected Label window_LegalDetailDialog_title;

	protected Tab applicationDetailsTab;
	protected Tab propertryDetailsTab;
	protected Tab documentDetailTab;
	protected Tab queryModuleTab;
	protected Tab propertryTittleTab;
	protected Tab encumbranceCertificateTab;
	protected Tab legalNotesTab;
	protected Tab coventsTab;
	protected Tab legalDecisionTab;
	protected Tab legalDocumentsTab;

	protected Tabpanel applicationDetailTabPanel;
	protected Tabpanel propertyDetailTabPanel;
	protected Tabpanel documentDetailTabPanel;
	protected Tabpanel queryModuleTabpanel;
	protected Tabpanel propertyTittleTabPanel;
	protected Tabpanel propertyEncumbranceTabPanel;
	protected Tabpanel coventsTabPanel;
	protected Tabpanel legalDecisionTabPanel;
	protected Tabpanel legalDocumentsTabPanel;

	// Tabs Headers Labels
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

	protected Label label_DocLoanReference;
	protected Label label_DocCollateralRef;
	protected Label label_DocLoanBranch;
	protected Label label_DocDate;

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

	// Legal Decision
	protected Combobox legalDecision;
	protected Textbox legalRemarks;

	// Legal Documents
	protected Listbox listBoxGenaratedDocuments;

	private LegalDetail legalDetail;
	private transient LegalDetailListCtrl legalDetailListCtrl;
	private transient LegalDetailService legalDetailService;
	private transient SecurityUserOperationsService securityUserOperationsService;
	private transient Configuration freemarkerMailConfiguration;

	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private SearchProcessor searchProcessor;
	private FinCovenantTypeListCtrl finCovenantTypeListCtrl;

	// Module Usage
	private List<LegalApplicantDetail> applicantDetailList = null;
	private List<LegalPropertyDetail> legalPropertyDetailList = null;
	private List<LegalDocument> legalDocumentList = null;
	private List<LegalPropertyTitle> propertyTitleList = null;
	private List<LegalECDetail> ecdDetailList = null;
	private List<LegalNote> legalNotesList = null;
	private String method = null;
	private boolean newApplicants = false;

	private boolean fromLoan = false;
	private boolean newRecord = false;

	private LegalDetailLoanListCtrl legalDetailLoanListCtrl;

	@Autowired
	private EmailEngine emailEngine;

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
		return this.legalDetail.getLegalReference();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_LegalDetailDialog);

		try {
			// Get the required arguments.
			this.legalDetail = (LegalDetail) arguments.get("legalDetail");

			if (arguments.containsKey("fromLoan")) {
				this.fromLoan = (boolean) arguments.get("fromLoan");
				setNewRecord(true);
			}

			if (fromLoan) {
				setLegalDetailLoanListCtrl((LegalDetailLoanListCtrl) arguments.get("dialogCtrl"));
			} else {
				this.legalDetailListCtrl = (LegalDetailListCtrl) arguments.get("legalDetailListCtrl");
			}

			if (this.legalDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// Store the before image.
			LegalDetail legalDetail = new LegalDetail();
			BeanUtils.copyProperties(this.legalDetail, legalDetail);
			this.legalDetail.setBefImage(legalDetail);

			// Render the page and display the data.
			if (!fromLoan) {
				doLoadWorkFlow(this.legalDetail.isWorkflow(), this.legalDetail.getWorkflowId(),
						this.legalDetail.getNextTaskId());
			}

			if (isWorkFlowEnabled() && !enqiryModule) {
				if (!fromLoan) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
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

		if (!this.enqiryModule) {
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
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		if (fromLoan) {
			doSaveLoanLegalDetails();
		} else {
			doSave();
		}
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.legalDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
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
	 * @param legalDetail The entity that need to be render.
	 */
	public void doShowDialog(LegalDetail legalDetail) {
		logger.debug(Literal.ENTERING);
		if (legalDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled() || fromLoan) {
				if (StringUtils.isNotBlank(legalDetail.getRecordType())) {
					if (!fromLoan) {
						this.btnNotes.setVisible(true);
					}
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
		if (fromLoan) {
			this.window_LegalDetailDialog.setHeight("80%");
			this.window_LegalDetailDialog.setWidth("90%");
			this.groupboxWf.setVisible(false);
			this.window_LegalDetailDialog.doModal();
		} else {
			this.window_LegalDetailDialog.setWidth("100%");
			this.window_LegalDetailDialog.setHeight("100%");
			setDialog(DialogType.EMBEDDED);
		}

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

		setChildRecordsSeqNum(aLegalDetail);

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

		// Query MOdule
		appendQueryModuleTab(aLegalDetail);

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

		// Convents tab
		if (this.coventsTab.isVisible()) {
			selectedTab = true;
		}
		appendCovenantTypeTab();

		// Legal Decision
		fillComboBox(this.legalDecision, aLegalDetail.getLegalDecision(), PennantStaticListUtil.getDecisionList(), "");
		this.legalRemarks.setValue(aLegalDetail.getLegalRemarks());

		// Document details
		if (this.documentDetailTab.isVisible()) {
			if (!selectedTab) {
				this.documentDetailTab.setSelected(true);
			}
		}
		doFillDocumentDetails(aLegalDetail.getDocumentList());

		if (enqiryModule) {
			this.legalDocumentsTab.setVisible(true);
			doFillGenaratedDocuments(aLegalDetail);
		}

		if (!enqiryModule) {
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
		}
		this.recordStatus.setValue(aLegalDetail.getRecordStatus());

		setLabels(aLegalDetail);

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

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final LegalDetail aLegalDetail = new LegalDetail();
		BeanUtils.copyProperties(this.legalDetail, aLegalDetail);

		doDelete(aLegalDetail.getLegalReference(), aLegalDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (enqiryModule) {
			doReadOnly();
			return;
		}
		// APplicant details
		this.applicationDetailsTab
				.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_applicationDetailsTabEnquiry"));
		if (!this.applicationDetailsTab.isVisible()) {
			this.applicationDetailsTab
					.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_applicationDetailsTab"));
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

		// Query module
		this.queryModuleTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_querryModuleTab"));

		// Property Title
		this.propertryTittleTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_propertryTittleTab"));
		readOnlyComponent(isReadOnly("LegalDetailDialog_PropertyDetailModt"), this.propertyDetailModt);

		// Ec details
		this.encumbranceCertificateTab
				.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_encumbranceCertificateTab"));
		readOnlyComponent(isReadOnly("LegalDetailDialog_PropertyDetailECDate"), this.propertyDetailECDate);
		readOnlyComponent(isReadOnly("LegalDetailDialog_ECPropertyOwnerName"), this.ecPropertyOwnerName);

		// Notes Tab
		this.legalNotesTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_legalNotesTab"));

		// Covents tab
		this.coventsTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_coventsTab"));
		if (!ImplementationConstants.COVENANT_REQUIRED) {
			coventsTab.setVisible(false);
		}

		// Decision details
		this.legalDecisionTab.setVisible(getUserWorkspace().isAllowed("tab_LegalDetailDialog_legalDecisionTab"));
		readOnlyComponent(isReadOnly("LegalDetailDialog_LegalDecision"), this.legalDecision);
		readOnlyComponent(isReadOnly("LegalDetailDialog_LegalRemarks"), this.legalRemarks);

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (fromLoan) {
			return getUserWorkspace().isReadOnly(componentName);
		} else {
			return super.isReadOnly(componentName);
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		if (enqiryModule) {
			this.btnNew_ApplicantDetails.setVisible(false);
			this.btnNew_PropertyDetails.setVisible(false);
			this.btnNew_DocumentDetails.setVisible(false);
			this.btnNew_PropertyTitleDetails.setVisible(false);
			this.btnNew_ECTitleDetails.setVisible(false);
			this.btnNew_NotesDetails.setVisible(false);
		}
		this.scheduleLevelArea.setReadonly(true);
		this.propertyDetailModt.setReadonly(true);
		this.propertyDetailECDate.setDisabled(true);
		this.ecPropertyOwnerName.setReadonly(true);
		this.legalDecision.setDisabled(true);
		this.legalRemarks.setReadonly(true);

		if (!enqiryModule && isWorkFlowEnabled()) {
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

	// Validate the data and render into list
	private void doSaveLoanLegalDetails() {
		logger.debug(Literal.ENTERING);

		final LegalDetail aLegalDetail = new LegalDetail();
		if (doWriteComponentsToBean(aLegalDetail)) {
			return;
		}

		boolean isNew = aLegalDetail.isNewRecord();
		if (this.newRecord) {
			if (isNew) {
				aLegalDetail.setVersion(1);
				aLegalDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}
			if (StringUtils.isBlank(aLegalDetail.getRecordType())) {
				aLegalDetail.setVersion(aLegalDetail.getVersion() + 1);
				aLegalDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aLegalDetail.setNewRecord(true);
			}
		} else {
			aLegalDetail.setVersion(aLegalDetail.getVersion() + 1);
		}
		aLegalDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLegalDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLegalDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		aLegalDetail.setRoleCode(getRole());
		aLegalDetail.setModule(FinanceConstants.MODULE_NAME);

		if (getLegalDetailLoanListCtrl() != null) {
			List<LegalDetail> legalDetails = getLegalDetailLoanListCtrl().getLegalDetailsList();
			for (LegalDetail legalDetail : legalDetails) {
				if (legalDetail.getCollateralReference().equals(aLegalDetail.getCollateralReference())) {
					BeanUtils.copyProperties(aLegalDetail, legalDetail);
					break;
				}
			}
			getLegalDetailLoanListCtrl().doFillLegalDetailDetails(legalDetails);
		}
		closeDialog();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final LegalDetail aLegalDetail = new LegalDetail();
		if (doWriteComponentsToBean(aLegalDetail)) {
			return;
		}

		boolean isNew = aLegalDetail.isNewRecord();
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
				publishNotification(Notify.ROLE, aLegalDetail.getCollateralReference(), aLegalDetail);

				// Mail Alert Notification
				if (PennantConstants.YES
						.equals(SysParamUtil.getValueAsString("ESFB_LEGAL_DETAIL_ALERT_NOTIFICATION"))) {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
						sendMailNotificationAlert(aLegalDetail);
					}
				}

				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aLegalDetail.getRoleCode(),
						aLegalDetail.getNextRoleCode(), aLegalDetail.getCollateralReference(), " Collateral ",
						aLegalDetail.getRecordStatus());

				// Download documents
				String fileNmae = null;
				if (PennantConstants.YES.equals(SysParamUtil.getValueAsString("ESFB_LEGAL_DETAIL_DOCUMENT_DOWNLOAD"))) {
					fileNmae = downloadDocuments(aLegalDetail);
				}

				if (fileNmae != null) {
					msg = msg.concat("  ".concat(fileNmae).concat(" document downloaded successfully."));
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean doWriteComponentsToBean(LegalDetail aLegalDetail) {
		Date appDate = SysParamUtil.getAppDate();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		BeanUtils.copyProperties(this.legalDetail, aLegalDetail);

		// Setting the formatted fin amount for approval process
		aLegalDetail.setFinAmount(
				CurrencyUtil.parse(aLegalDetail.getFinAmount(), CurrencyUtil.getFormat(aLegalDetail.getFinCcy())));

		doRemoveValidation();

		// Applicant details
		aLegalDetail.setApplicantDetailList(getApplicantDetailList());
		if (this.applicationDetailsTab.isVisible() && CollectionUtils.isEmpty(getApplicantDetailList())) {
			this.applicationDetailsTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_ApplicantDetails_Validation"));
			return true;
		} else if (this.applicationDetailsTab.isVisible() && aLegalDetail.getApplicantDetailList().size() == 1) {
			String recordType = getApplicantDetailList().get(0).getRecordType();
			if (PennantConstants.RECORD_TYPE_CAN.equals(recordType)
					|| PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
				MessageUtil.showError(Labels.getLabel("label_LegalDetail_ApplicantDetails_Del_Validation"));
				return true;
			}
		}

		// Property details
		aLegalDetail.setPropertyDetailList(getLegalPropertyDetailList());
		if (this.propertryDetailsTab.isVisible() && CollectionUtils.isEmpty(getLegalPropertyDetailList())) {
			this.propertryDetailsTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_PropertyDetails_Validation"));
			return true;
		}

		if (this.propertryDetailsTab.isVisible() && CollectionUtils.isNotEmpty(getLegalPropertyDetailList())) {
			aLegalDetail.setSchedulelevelArea(StringUtils.trimToNull(this.scheduleLevelArea.getValue()));
		}

		if (this.propertryDetailsTab.isVisible() && aLegalDetail.getPropertyDetailList().size() == 1) {
			String recordType = getLegalPropertyDetailList().get(0).getRecordType();
			if (PennantConstants.RECORD_TYPE_CAN.equals(recordType)
					|| PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
				MessageUtil.showError(Labels.getLabel("label_LegalDetail_PropertyDetails_Del_Validation"));
				return true;
			}
		}

		// Document Details
		aLegalDetail.setDocumentList(getLegalDocumentList());
		if (this.documentDetailTab.isVisible() && CollectionUtils.isEmpty(getLegalDocumentList())) {
			this.documentDetailTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_DocumentDetails_Validation"));
			return true;
		}

		// Title details
		aLegalDetail.setPropertyTitleList(getPropertyTitleList());
		if (this.propertryTittleTab.isVisible() && CollectionUtils.isEmpty(getPropertyTitleList())) {
			this.propertryTittleTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_PropertryTittle_Validation"));
			return true;
		}
		if (this.propertryTittleTab.isVisible() && CollectionUtils.isNotEmpty(getPropertyTitleList())) {
			aLegalDetail.setPropertyDetailModt(StringUtils.trimToNull(this.propertyDetailModt.getValue()));
		}
		if (this.propertryTittleTab.isVisible() && aLegalDetail.getPropertyTitleList().size() == 1) {
			String recordType = getPropertyTitleList().get(0).getRecordType();
			if (PennantConstants.RECORD_TYPE_CAN.equals(recordType)
					|| PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
				MessageUtil.showError(Labels.getLabel("label_LegalDetail_PropertryTittle_Del_Validation"));
				return true;
			}
		}

		// EC details
		aLegalDetail.setEcdDetailsList(getEcdDetailList());
		if (this.encumbranceCertificateTab.isVisible() && CollectionUtils.isEmpty(getEcdDetailList())) {
			this.encumbranceCertificateTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_ECDetails_Validation"));
			return true;
		}
		if (this.encumbranceCertificateTab.isVisible() && CollectionUtils.isNotEmpty(getEcdDetailList())) {
			if (this.propertyDetailECDate.getValue() == null) {
				aLegalDetail.setPropertyDetailECDate(this.propertyDetailECDate.getValue());
			} else if (StringUtils.trimToNull(this.ecPropertyOwnerName.getValue()) == null) {
				this.encumbranceCertificateTab.setSelected(true);
				throw new WrongValueException(this.ecPropertyOwnerName, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_LegalPropertyTitleDialog_ECPropertyOwnerName.value") }));
			} else if (this.propertyDetailECDate.getValue() != null) {
				this.propertyDetailECDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_LegalPropertyTitleDialog_ECdate.value"), true, null, appDate, true));
				try {
					aLegalDetail.setPropertyDetailECDate(this.propertyDetailECDate.getValue());
				} catch (WrongValueException we) {
					this.encumbranceCertificateTab.setSelected(true);
					wve.add(we);
				}
				aLegalDetail.setEcPropertyOwnerName(this.ecPropertyOwnerName.getValue());
			}
		}
		if (this.encumbranceCertificateTab.isVisible() && aLegalDetail.getEcdDetailsList().size() == 1) {
			String recordType = getEcdDetailList().get(0).getRecordType();
			if (PennantConstants.RECORD_TYPE_CAN.equals(recordType)
					|| PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
				MessageUtil.showError(Labels.getLabel("label_LegalDetail_ECDetails_Del_Validation"));
				return true;
			}
		}

		// Legal Notes details
		aLegalDetail.setLegalNotesList(getLegalNotesList());
		if (this.legalNotesTab.isVisible() && CollectionUtils.isEmpty(getLegalNotesList())) {
			this.legalNotesTab.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_LegalDetail_LegalNotes_Validation"));
			return true;
		}
		if (this.legalNotesTab.isVisible() && aLegalDetail.getLegalNotesList().size() == 1) {
			String recordType = getLegalNotesList().get(0).getRecordType();
			if (PennantConstants.RECORD_TYPE_CAN.equals(recordType)
					|| PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
				MessageUtil.showError(Labels.getLabel("label_LegalDetail_LegalNotes_DEL_Validation"));
				return true;
			}
		}
		// Covenant Details Saving
		if (getFinCovenantTypeListCtrl() != null) {
			aLegalDetail.setCovenantTypeList(getFinCovenantTypeListCtrl().getFinCovenantTypeDetailList());
		} else {
			aLegalDetail.setCovenantTypeList(getLegalDetail().getCovenantTypeList());
		}

		// Legal Decision
		if (this.legalDecisionTab.isVisible()) {
			String legalDecision = this.legalDecision.getSelectedItem().getValue();
			if (StringUtils.trimToNull(legalDecision) == null || PennantConstants.List_Select.equals(legalDecision)) {
				this.legalDecisionTab.setSelected(true);
				throw new WrongValueException(this.legalDecision, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_LegalDetailDialog_LegalDecision.value") }));
			} else if (StringUtils.trimToNull(legalRemarks.getValue()) == null) {
				this.legalDecisionTab.setSelected(true);
				throw new WrongValueException(this.legalRemarks, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_LegalDetailDialog_LegalRemarks.value") }));
			} else {
				aLegalDetail.setLegalDecision(legalDecision);
				aLegalDetail.setLegalRemarks(legalRemarks.getValue());
			}
		}

		// Final validations of document details
		if (this.documentDetailTab.isVisible() && CollectionUtils.isNotEmpty(getLegalDocumentList())) {
			int cnt = 0;
			for (LegalDocument document : getLegalDocumentList()) {
				String recordType = document.getRecordType();
				if (PennantConstants.RECORD_TYPE_CAN.equals(recordType)
						|| PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
					cnt++;
				}
				if (cnt == getLegalDocumentList().size()) {
					MessageUtil.showError(Labels.getLabel("label_LegalDetail_DocumentDetails_Del_Validation"));
					return true;
				}
			}
		}

		if (this.documentDetailTab.isVisible() && CollectionUtils.isNotEmpty(getLegalDocumentList())
				&& ((fromLoan ? fromLoan : !"Resubmit".equals(this.userAction.getSelectedItem().getLabel())))) {
			for (LegalDocument document : getLegalDocumentList()) {
				if (isRoleContains("ESFB_LEGAL_DETAIL_DOCUMENTNAME_MANDATORY_ROLES", getRole())) {
					if (StringUtils.trimToNull(document.getDocumentName()) == null) {
						this.documentDetailTab.setSelected(true);
						MessageUtil.showError(Labels.getLabel("label_LegalDetail_DocumentDetails_List_Validation"));
						return true;
					} else if (isRoleContains("ESFB_LEGAL_DETAIL_DOCUMENTTYPE_MANDATORY_ROLES", getRole())) {
						if (PennantConstants.List_Select.equals(document.getDocumentTypeApprove())
								|| Labels.getLabel("Combo.Select").equals(document.getDocumentTypeApprove())) {
							this.documentDetailTab.setSelected(true);
							MessageUtil.showError(Labels.getLabel("label_LegalDetail_DocumentDetails_List_Validation"));
							return true;
						}
					}
				} else if (isRoleContains("ESFB_LEGAL_DETAIL_DOCUMENTTYPE_MANDATORY_ROLES", getRole())) {
					if (PennantConstants.List_Select.equals(document.getDocumentTypeApprove())
							|| Labels.getLabel("Combo.Select").equals(document.getDocumentTypeApprove())) {
						this.documentDetailTab.setSelected(true);
						MessageUtil.showError(Labels.getLabel("label_LegalDetail_DocumentDetails_List_Validation"));
						return true;
					}
				}
			}
		}
		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		return false;
	}

	private boolean isRoleContains(String smtParameter, String reqRole) {
		String rolesList = SysParamUtil.getValueAsString(smtParameter);
		if (StringUtils.isEmpty(rolesList)) {
			return false;
		}
		String[] roles = rolesList.split(",");
		for (String role : roles) {
			if (StringUtils.equalsIgnoreCase(role, reqRole)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(LegalDetail aLegalDetail, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;

		aLegalDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLegalDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLegalDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aLegalDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check whether notes is mandatory and specified by the user.
			if (isNotesMandatory(taskId, aLegalDetail) && !notesEntered) {
				MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));

				return false;
			}

			// Get the next queue details.
			String nextTaskId = ProcessUtil.getNextTask(workFlow, taskId, userAction.getSelectedItem().getLabel(),
					aLegalDetail.getNextTaskId(), aLegalDetail);
			Map<String, String> nextRoles = ProcessUtil.getNextRoles(workFlow, nextTaskId);
			String nextRoleCode = StringUtils.join(nextRoles.keySet(), ",");

			// Set work-flow details.
			aLegalDetail.setTaskId(taskId);
			aLegalDetail.setNextTaskId(nextTaskId);
			aLegalDetail.setRoleCode(getRole());
			aLegalDetail.setNextRoleCode(nextRoleCode);
			aLegalDetail.setModule(PennantConstants.QUERY_LEGAL_VERIFICATION);

			// Applicant details
			List<LegalApplicantDetail> legalApplicantDetails = aLegalDetail.getApplicantDetailList();
			if (CollectionUtils.isNotEmpty(legalApplicantDetails)) {
				for (LegalApplicantDetail details : legalApplicantDetails) {
					details.setLegalId(aLegalDetail.getLegalId());
					details.setLegalReference(aLegalDetail.getLegalReference());
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
			if (CollectionUtils.isNotEmpty(legalPropertyDetail)) {
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
			if (CollectionUtils.isNotEmpty(legaldDocumentDetails)) {
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
					details.setCustId(aLegalDetail.getCustId());
					details.setFinReference(aLegalDetail.getLoanReference());
					if (PennantConstants.RECORD_TYPE_DEL.equals(aLegalDetail.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aLegalDetail.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// Legal Property Title
			List<LegalPropertyTitle> legalPropertyTitles = aLegalDetail.getPropertyTitleList();
			if (CollectionUtils.isNotEmpty(legalPropertyTitles)) {
				for (LegalPropertyTitle details : legalPropertyTitles) {
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

			// Legal EC Details
			List<LegalECDetail> legalECDetails = aLegalDetail.getEcdDetailsList();
			if (CollectionUtils.isNotEmpty(legalECDetails)) {
				for (LegalECDetail details : legalECDetails) {
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

			// Legal Notes
			List<LegalNote> legalNotes = aLegalDetail.getLegalNotesList();
			if (CollectionUtils.isNotEmpty(legalNotes)) {
				for (LegalNote details : legalNotes) {
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

			// Covenet details
			List<FinCovenantType> covenantTypeDetails = aLegalDetail.getCovenantTypeList();
			if (CollectionUtils.isNotEmpty(covenantTypeDetails)) {
				for (FinCovenantType details : covenantTypeDetails) {
					details.setFinReference(aLegalDetail.getLoanReference());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				}
			}

			auditHeader = getAuditHeader(aLegalDetail, tranType);

			// Execute service tasks.
			List<ServiceTask> serviceTasks = workFlow.getServiceTasks(taskId, aLegalDetail);
			String finalOperation = null;

			for (ServiceTask task : serviceTasks) {
				if (ProcessUtil.isPersistentTask(task)) {
					finalOperation = task.getOperation();

					break;
				}

				// No service tasks implemented.
				auditHeader.setErrorDetails(
						new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
				ErrorControl.showErrorControl(this.window_LegalDetailDialog, auditHeader);

				return false;
			}

			// Save the data.
			if (StringUtils.isNotEmpty(aLegalDetail.getNextTaskId())) {
				// If the parallel flow not completed, don't consider the final
				// operation.
				finalOperation = null;
			}

			processCompleted = doSaveProcess(auditHeader, finalOperation);
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
	 * @param AuditHeader auditHeader
	 * @param operation   (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String operation) {
		logger.debug(Literal.ENTERING);

		setMethod(operation);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		LegalDetail aLegalDetail = (LegalDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			switch (Operation.methodOf(operation)) {
			case DEFAULT:
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = legalDetailService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = legalDetailService.saveOrUpdate(auditHeader);
				}

				break;
			case APPROVE:
				auditHeader = legalDetailService.doApprove(auditHeader);

				if (aLegalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
					deleteNotes = true;
				}

				break;
			case REJECT:
				auditHeader = legalDetailService.doReject(auditHeader);

				if (aLegalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					deleteNotes = true;
				}

				break;
			default:
				auditHeader.setErrorDetails(
						new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
				retValue = ErrorControl.showErrorControl(this.window_LegalDetailDialog, auditHeader);

				return processCompleted;
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
	 * The framework calls this event handler when user clicks the btnNew_ApplicantDetails button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_ApplicantDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalApplicantDetail legalApplicantDetail = new LegalApplicantDetail();
		legalApplicantDetail.setNewRecord(true);
		legalApplicantDetail.setWorkflowId(0);
		legalApplicantDetail.setSeqNum(getAppSeqNum());

		final Map<String, Object> map = new HashMap<String, Object>();
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

	private int getAppSeqNum() {
		int seqNum = 0;
		if (CollectionUtils.isNotEmpty(getApplicantDetailList())) {
			for (LegalApplicantDetail detail : getApplicantDetailList()) {
				if (detail.getSeqNum() > seqNum) {
					seqNum = detail.getSeqNum();
				}
			}
		}
		return seqNum + 1;
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 */
	public void onLegalApplicantDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalApplicantDetail.getSelectedItem();

		if (item != null) {
			LegalApplicantDetail applicantDetail = (LegalApplicantDetail) item.getAttribute("object");
			if (applicantDetail.isDefault()) {
				applicantDetail.setNewRecord(true);
				applicantDetail.setWorkflowId(0);
				applicantDetail.setSeqNum(getAppSeqNum());
			}
			if (StringUtils.equalsIgnoreCase(applicantDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("legalApplicantDetail", applicantDetail);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				if (enqiryModule) {
					map.put("enquiry", true);
				} else {
					map.put("enquiry",
							getUserWorkspace().isAllowed("tab_LegalDetailDialog_applicationDetailsTabEnquiry"));
				}
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
		List<LegalApplicantDetail> detailList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(applicantDetailList)) {
			detailList.addAll(applicantDetailList);
		}
		if (!enqiryModule && (CollectionUtils.isEmpty(applicantDetailList) || isNewApplicants())) {
			List<Customer> customerList = getLegalDetail().getCustomerList();
			if (CollectionUtils.isNotEmpty(customerList)) {
				setNewApplicants(true);
				getReqApplicantsList(detailList, customerList);
			}
		}
		doFillApplicantDetail(detailList);

		logger.debug(Literal.LEAVING);
	}

	private void getReqApplicantsList(List<LegalApplicantDetail> detailList, List<Customer> customerList) {

		for (Customer customer : customerList) {
			boolean idAdd = false;
			if (CollectionUtils.isNotEmpty(detailList)) {
				for (LegalApplicantDetail detail : detailList) {
					if (customer.getCustID() == detail.getCustomerId()) {
						idAdd = true;
					}
				}
			}
			if (!idAdd) {
				LegalApplicantDetail applicantDetail = new LegalApplicantDetail();
				applicantDetail.setDefault(true);
				applicantDetail.setTitle(customer.getCustSalutationCode());
				applicantDetail.setTitleName(customer.getLovDescCustSalutationCodeName());
				applicantDetail.setPropertyOwnersName(customer.getCustShrtName());
				applicantDetail.setAge(getAge(customer.getCustDOB()));
				applicantDetail.setCustomer(customer);
				detailList.add(applicantDetail);
			}
		}

	}

	private void doFillApplicantDetail(List<LegalApplicantDetail> applicantDetailList) {

		if (applicantDetailList != null && !applicantDetailList.isEmpty()) {

			for (LegalApplicantDetail applicantDetail : applicantDetailList) {

				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(applicantDetail.getTitleName());
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
	}

	private int getAge(Date dob) {
		if (dob == null) {
			return 0;
		}
		int years = 0;
		Date appDate = SysParamUtil.getAppDate();
		if (dob.compareTo(appDate) < 0) {
			int months = DateUtil.getMonthsBetween(appDate, dob);
			years = months / 12;
		}
		return years;
	}

	/**
	 ********************************************************************************************
	 * Property Details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the btnNew_PropertyDetails button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_PropertyDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalPropertyDetail propertyDetail = new LegalPropertyDetail();
		propertyDetail.setNewRecord(true);
		propertyDetail.setWorkflowId(0);
		propertyDetail.setSeqNum(getPropDetSeqNum());
		final Map<String, Object> map = new HashMap<String, Object>();
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

	private int getPropDetSeqNum() {
		int seqNum = 0;
		if (CollectionUtils.isNotEmpty(getLegalPropertyDetailList())) {
			for (LegalPropertyDetail detail : getLegalPropertyDetailList()) {
				if (detail.getSeqNum() > seqNum) {
					seqNum = detail.getSeqNum();
				}
			}
		}
		return seqNum + 1;
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 */
	public void onLegalPropertyDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalPropertyDetail.getSelectedItem();

		if (item != null) {
			int index = item.getIndex();
			LegalPropertyDetail legalPropertyDetail = (LegalPropertyDetail) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalPropertyDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("legalPropertyDetail", legalPropertyDetail);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enquiry", this.enqiryModule);
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
				lc = new Listcell(legalPropertyDetail.getRegistrationDistrict());
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
			listcell = new Listcell("Summary");
			listcell.setStyle("font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			listcell = new Listcell("Total Extent or Area");
			listcell.setStyle("text-align:right;font-weight:bold");
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
	 * Query Module *
	 ********************************************************************************************
	 */
	private void appendQueryModuleTab(LegalDetail aLegalDetail) {
		logger.debug(Literal.ENTERING);

		ComponentsCtrl.applyForward(queryModuleTab, ("onSelect=onSelectQueryTab"));

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isNotFinanceProcess", true);
		map.put("ccyFormatter", 2);
		map.put("enquiry", this.enqiryModule);
		map.put("queryDetail", aLegalDetail.getQueryDetail());
		map.put("legalDetail", aLegalDetail);
		map.put("moduleName", CollateralConstants.LEGAL_MODULE);
		Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/FinQueryDetailList.zul", queryModuleTabpanel,
				map);

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
		legalDocument.setSeqNum(getDocSeqNum());
		final Map<String, Object> map = new HashMap<String, Object>();
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

	private int getDocSeqNum() {
		int seqNum = 0;
		if (CollectionUtils.isNotEmpty(getLegalDocumentList())) {
			for (LegalDocument detail : getLegalDocumentList()) {
				if (detail.getSeqNum() > seqNum) {
					seqNum = detail.getSeqNum();
				}
			}
		}
		return seqNum + 1;
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 */
	public void onLegalDocumentItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalDocument.getSelectedItem();

		if (item != null) {
			LegalDocument legalDocument = (LegalDocument) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalDocument.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("legalDocument", legalDocument);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enquiry", this.enqiryModule);
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

		List<ValueLabel> documentTypesList = PennantStaticListUtil.getDocumentTypes();
		List<ValueLabel> documentCategoryList = getDocumentCategoryList();
		List<ValueLabel> documentAccepted = PennantStaticListUtil.getDocumentAcceptedList();

		if (documentDetailList != null && !documentDetailList.isEmpty()) {
			for (LegalDocument legalDocument : documentDetailList) {

				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(String.valueOf(++i));
				lc.setParent(item);

				lc = new Listcell(DateUtil.format(legalDocument.getDocumentDate(), PennantConstants.dateFormat));
				lc.setParent(item);

				lc = new Listcell(legalDocument.getDocumentNo());
				lc.setParent(item);

				lc = new Listcell(
						PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentType(), documentTypesList));
				lc.setParent(item);

				lc = new Listcell(
						PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentCategory(), documentCategoryList));
				lc.setParent(item);

				lc = new Listcell(legalDocument.getScheduleType());
				lc.setParent(item);

				lc = new Listcell(
						PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentTypeVerify(), documentTypesList));
				lc.setParent(item);

				lc = new Listcell(
						PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentTypeApprove(), documentTypesList));
				lc.setParent(item);

				lc = new Listcell(
						PennantStaticListUtil.getlabelDesc(legalDocument.getDocumentAccepted(), documentAccepted));
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

	private ArrayList<ValueLabel> getDocumentCategoryList() {
		ArrayList<ValueLabel> documentCategoryList = new ArrayList<>();
		List<DocumentDetails> collateralDocumentList = getLegalDetail().getCollateralDocumentList();
		if (CollectionUtils.isNotEmpty(collateralDocumentList)) {
			for (DocumentDetails documentDetails : collateralDocumentList) {
				ValueLabel valueLabel = new ValueLabel();
				valueLabel.setLabel(documentDetails.getDocCategory());
				valueLabel.setValue(documentDetails.getDocCategory());
				documentCategoryList.add(valueLabel);
			}
		}
		return documentCategoryList;
	}

	/**
	 ********************************************************************************************
	 * Property Title Details *
	 ********************************************************************************************
	 */
	/**
	 * The framework calls this event handler when user clicks the btnNew_PropertyTitleDetails button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_PropertyTitleDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalPropertyTitle legalPropertyTitle = new LegalPropertyTitle();
		legalPropertyTitle.setNewRecord(true);
		legalPropertyTitle.setWorkflowId(0);
		legalPropertyTitle.setSeqNum(getPropTitSeqNum());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("legalPropertyTitle", legalPropertyTitle);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalPropertyTitleDialog.zul",
					window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private int getPropTitSeqNum() {
		int seqNum = 0;
		if (CollectionUtils.isNotEmpty(getPropertyTitleList())) {
			for (LegalPropertyTitle detail : getPropertyTitleList()) {
				if (detail.getSeqNum() > seqNum) {
					seqNum = detail.getSeqNum();
				}
			}
		}
		return seqNum + 1;
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 */
	public void onLegalPropertyTitleItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBoxLegalPropertyTitle.getSelectedItem();
		if (item != null) {
			LegalPropertyTitle legalPropertyTitle = (LegalPropertyTitle) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalPropertyTitle.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("legalPropertyTitle", legalPropertyTitle);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enquiry", this.enqiryModule);
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalPropertyTitleDialog.zul",
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
	 * The framework calls this event handler when user clicks the btnNew_ECTitleDetails button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_ECTitleDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalECDetail legalECDetail = new LegalECDetail();
		legalECDetail.setNewRecord(true);
		legalECDetail.setWorkflowId(0);
		legalECDetail.setSeqNum(getECSeqNum());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("legalECDetail", legalECDetail);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalECDetailDialog.zul",
					window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private int getECSeqNum() {
		int seqNum = 0;
		if (CollectionUtils.isNotEmpty(getEcdDetailList())) {
			for (LegalECDetail detail : getEcdDetailList()) {
				if (detail.getSeqNum() > seqNum) {
					seqNum = detail.getSeqNum();
				}
			}
		}
		return seqNum + 1;
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 */
	public void onLegalECDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem item = this.listBoxLegalECDetail.getSelectedItem();
		if (item != null) {
			LegalECDetail legalECDetail = (LegalECDetail) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalECDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("legalECDetail", legalECDetail);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enquiry", this.enqiryModule);
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalECDetailDialog.zul",
							window_LegalDetailDialog, map);
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
		ArrayList<ValueLabel> ecTypesTypesList = PennantStaticListUtil.getEcTypes();
		this.listBoxLegalECDetail.getItems().clear();
		setEcdDetailList(legalECDetailList);

		if (legalECDetailList != null && !legalECDetailList.isEmpty()) {
			for (LegalECDetail legalECDetail : legalECDetailList) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(DateUtil.format(legalECDetail.getEcDate(), PennantConstants.dateFormat));
				lc.setParent(item);
				lc = new Listcell(legalECDetail.getDocument());
				lc.setParent(item);

				lc = new Listcell(legalECDetail.getEcNumber());
				lc.setParent(item);

				lc = new Listcell(DateUtil.format(legalECDetail.getEcFrom(), PennantConstants.dateFormat));
				lc.setParent(item);

				lc = new Listcell(DateUtil.format(legalECDetail.getEcTo(), PennantConstants.dateFormat));
				lc.setParent(item);

				lc = new Listcell(PennantStaticListUtil.getlabelDesc(legalECDetail.getEcType(), ecTypesTypesList));
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
	 * The framework calls this event handler when user clicks the btnNew_NotesDetails button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_NotesDetails(Event event) {
		logger.debug(Literal.ENTERING);

		LegalNote legalNote = new LegalNote();
		legalNote.setNewRecord(true);
		legalNote.setWorkflowId(0);
		legalNote.setSeqNum(getNotesSeqNum());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("legalNote", legalNote);
		map.put("legalDetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalNoteDialog.zul",
					window_LegalDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private int getNotesSeqNum() {
		int seqNum = 0;
		if (CollectionUtils.isNotEmpty(getLegalNotesList())) {
			for (LegalNote detail : getLegalNotesList()) {
				if (detail.getSeqNum() > seqNum) {
					seqNum = detail.getSeqNum();
				}
			}
		}
		return seqNum + 1;
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 */
	public void onLegalNoteItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem item = this.listBoxLegalNote.getSelectedItem();
		if (item != null) {
			LegalNote legalNote = (LegalNote) item.getAttribute("object");
			if (StringUtils.equalsIgnoreCase(legalNote.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("legalNote", legalNote);
				map.put("legalDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enquiry", this.enqiryModule);
				try {
					Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalNoteDialog.zul",
							window_LegalDetailDialog, map);
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
	 * Covenant Type details *
	 ********************************************************************************************
	 */

	private void appendCovenantTypeTab() {
		logger.debug(Literal.ENTERING);

		ComponentsCtrl.applyForward(coventsTab, ("onSelect=onSelectCovTab"));

		final Map<String, Object> map = getDefaultArguments();
		map.put("covenantTypes", getLegalDetail().getCovenantTypeList());
		map.put("legalDetail", getLegalDetail());
		map.put("allowedRoles", getLoanWorkFlowRoles());
		map.put("roleCode", getRole());
		map.put("enquiry", this.enqiryModule);

		String url = "/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeList.zul";

		if (ImplementationConstants.COVENANT_MODULE_NEW) {
			url = "/WEB-INF/pages/Finance/Covenant/CovenantsList.zul";
		}

		Executions.createComponents(url, coventsTabPanel, map);
		logger.debug(Literal.LEAVING);
	}

	private String getLoanWorkFlowRoles() {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isEmpty(getLegalDetail().getFinType())) {
			return "";
		}

		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowDAO().getFinanceWorkFlow(getLegalDetail().getFinType(),
				FinServiceEvent.ORG, PennantConstants.WORFLOW_MODULE_FINANCE, "_FTView");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
		String roles = workFlowDetails.getWorkFlowRoles();
		logger.debug(Literal.LEAVING);
		return roles;
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", CollateralConstants.LEGAL_MODULE);
		return map;
	}

	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		String legalDate = "";
		if (getLegalDetail().getLegalDate() != null) {
			legalDate = DateUtil.format(getLegalDetail().getLegalDate(), DateFormat.LONG_DATE.getPattern());
		}
		arrayList.add(0, getLegalDetail().getLoanReference());
		arrayList.add(1, getLegalDetail().getCollateralReference());
		arrayList.add(2, getLegalDetail().getBranchDesc());
		arrayList.add(3, legalDate);
		return arrayList;
	}

	public void onSelectCovTab(ForwardEvent event) {
		if (getFinCovenantTypeListCtrl() != null) {
			getFinCovenantTypeListCtrl().doSetLabels(getHeaderBasicDetails());
		}
	}

	/**
	 ********************************************************************************************
	 * Document downloads *
	 ********************************************************************************************
	 */
	private void doFillGenaratedDocuments(LegalDetail aLegalDetail) {
		List<DocumentDetails> detailsList = legalDetailService.getDocumentDetails(aLegalDetail.getLegalReference(),
				CollateralConstants.LEGAL_MODULE);
		if (CollectionUtils.isNotEmpty(detailsList)) {
			doFillGenaratedDocuments(detailsList);
		}
	}

	/**
	 * Rendering the Document Details
	 * 
	 * @param detailsList
	 */
	private void doFillGenaratedDocuments(List<DocumentDetails> detailsList) {
		logger.debug(Literal.ENTERING);

		this.listBoxGenaratedDocuments.getItems().clear();
		List<ValueLabel> list = PennantAppUtil.getDocumentTypes();
		Listitem listitem = null;
		Listcell lc = null;
		for (DocumentDetails doc : detailsList) {

			listitem = new Listitem();
			lc = new Listcell(String.valueOf(doc.getDocId()));
			listitem.appendChild(lc);

			lc = new Listcell(PennantApplicationUtil.getLabelDesc(doc.getDocCategory(), list));
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDoctype());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDocName());
			listitem.appendChild(lc);

			lc = new Listcell();
			Button viewBtn = new Button("View");
			if (StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)) {
				viewBtn.setLabel("Download");
			}
			viewBtn.addForward("onClick", window_LegalDetailDialog, "onDocViewButtonClicked", doc.getDocId());
			lc.appendChild(viewBtn);
			viewBtn.setStyle("font-weight:bold;");
			listitem.appendChild(lc);

			this.listBoxGenaratedDocuments.appendChild(listitem);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onDocViewButtonClicked(Event event) {
		logger.debug(Literal.ENTERING);

		long docId = Long.parseLong(event.getData().toString());
		DocumentDetails detail = legalDetailService.getDocDetailByDocId(docId, "_View", true);

		if (StringUtils.isNotBlank(detail.getDocName()) && detail.getDocImage() != null
				&& StringUtils.isNotBlank(detail.getDocImage().toString())) {
			try {
				if (StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)) {
					Filedownload.save(detail.getDocImage(), "application/msword", detail.getDocName());
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("FinDocumentDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				}
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		} else if (StringUtils.isNotBlank(detail.getDocUri())) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("documentRef", detail);
			Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
		} else {
			MessageUtil.showError("Document Details not Found.");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting the window title based on the role
	 */
	private void setWindowTittle() {
		String roleCode = getRole();
		if (enqiryModule) {
			this.window_LegalDetailDialog_title.setValue(Labels.getLabel("window_LegalDetailDialogEnquiry.title"));
			return;
		}
		if (StringUtils.isEmpty(roleCode)) {
			this.window_LegalDetailDialog_title.setValue(Labels.getLabel("window_LegalDetailDialog.title"));
			return;
		}
		Search search = new Search(SecurityRole.class);
		search.addTabelName("secroles");
		search.addFilterEqual("rolecd", roleCode);
		List<SecurityRole> securityRolesList = getSearchProcessor().getResults(search);

		if (CollectionUtils.isEmpty(securityRolesList)) {
			this.window_LegalDetailDialog_title.setValue(Labels.getLabel("window_LegalDetailDialog.title"));
			return;
		}
		this.window_LegalDetailDialog_title.setValue(securityRolesList.get(0).getRoleDesc());
	}

	/**
	 * Setting the tabs header labels
	 * 
	 * @param aLegalDetail
	 */
	private void setLabels(LegalDetail aLegalDetail) {

		String legalDate = "";
		if (aLegalDetail.getLegalDate() != null) {
			legalDate = DateUtil.format(aLegalDetail.getLegalDate(), DateFormat.LONG_DATE.getPattern());
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

		this.label_DocLoanReference.setValue(aLegalDetail.getLoanReference());
		this.label_DocCollateralRef.setValue(aLegalDetail.getCollateralReference());
		this.label_DocLoanBranch.setValue(aLegalDetail.getBranchDesc());
		this.label_DocDate.setValue(legalDate);
	}

	/**
	 * Setting the all child records Sequences
	 * 
	 * @param aLegalDetail
	 */
	private void setChildRecordsSeqNum(LegalDetail aLegalDetail) {
		// Documents
		List<LegalDocument> documentList = aLegalDetail.getDocumentList();
		if (CollectionUtils.isNotEmpty(documentList)) {
			for (int i = 0; i < documentList.size(); i++) {
				documentList.get(i).setSeqNum(i + 1);
			}
		}

		// Applicant
		List<LegalApplicantDetail> applicantList = aLegalDetail.getApplicantDetailList();
		if (CollectionUtils.isNotEmpty(applicantList)) {
			for (int i = 0; i < applicantList.size(); i++) {
				applicantList.get(i).setSeqNum(i + 1);
			}
		}

		// LegalPropertyDetail
		List<LegalPropertyDetail> propertyDetailList = aLegalDetail.getPropertyDetailList();
		if (CollectionUtils.isNotEmpty(propertyDetailList)) {
			for (int i = 0; i < propertyDetailList.size(); i++) {
				propertyDetailList.get(i).setSeqNum(i + 1);
			}
		}

		// LegalPropertyTitle
		List<LegalPropertyTitle> propertyTittleList = aLegalDetail.getPropertyTitleList();
		if (CollectionUtils.isNotEmpty(propertyTittleList)) {
			for (int i = 0; i < propertyTittleList.size(); i++) {
				propertyTittleList.get(i).setSeqNum(i + 1);
			}
		}

		// LegalECDetail
		List<LegalECDetail> ecdDetailsList = aLegalDetail.getEcdDetailsList();
		if (CollectionUtils.isNotEmpty(ecdDetailsList)) {
			for (int i = 0; i < ecdDetailsList.size(); i++) {
				ecdDetailsList.get(i).setSeqNum(i + 1);
			}
		}

		// LegalNote
		List<LegalNote> legalNoteList = aLegalDetail.getLegalNotesList();
		if (CollectionUtils.isNotEmpty(legalNoteList)) {
			for (int i = 0; i < legalNoteList.size(); i++) {
				legalNoteList.get(i).setSeqNum(i + 1);
			}
		}
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

	/**
	 * Downloading the documents based on the role
	 * 
	 * @param legalDetail
	 */
	private String downloadDocuments(LegalDetail legalDetail) {
		logger.debug(Literal.ENTERING);
		try {
			if (isRoleContains("ESFB_LEGAL_DETAIL_PRELIMINARY_DOC_ROLES", getRole())
					&& (!"Resubmit".equals(this.userAction.getSelectedItem().getLabel()))) {
				// Legal Preliminary Document
				return downloadDocument(legalDetail, "PRELIMINARY Draft", true);
			} else if ((isRoleContains("ESFB_LEGAL_DETAIL_FINAL_OPINION_DOC_ROLES", getRole()))
					&& (!"Resubmit".equals(this.userAction.getSelectedItem().getLabel()))) {
				// Legal Final Opinion Document
				return downloadDocument(legalDetail, "FINAL OPINION Draft", false);
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private String downloadDocument(LegalDetail legalDetail, String template, boolean isPreliminary) throws Exception {
		logger.debug(Literal.ENTERING);
		String templateName = "Legal/".concat(template.concat(PennantConstants.DOC_TYPE_WORD_EXT));
		legalDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		legalDetail = legalDetailService.formatLegalDetails(legalDetail);
		String fileName = template.concat(PennantConstants.DOC_TYPE_PDF_EXT);
		AgreementEngine engine = new AgreementEngine();
		engine.setTemplate(templateName);
		engine.loadTemplate();
		engine.mergeFields(legalDetail);
		byte[] docData = engine.getDocumentInByteArray(SaveFormat.PDF);
		showDocument(docData, legalDetailListCtrl.window_LegalDetailList, fileName, SaveFormat.PDF);

		// Will save the data in one table for another menu option download
		legalDetail.setDocImage(engine.getDocumentInByteArray(SaveFormat.PDF));

		DocumentDetails details = new DocumentDetails();
		details.setDocModule(CollateralConstants.LEGAL_MODULE);
		if (isPreliminary) {
			details.setDocCategory("LEG001");
		} else {
			details.setDocCategory("LEG002");
		}
		details.setDoctype(PennantConstants.DOC_TYPE_PDF);
		details.setDocName(fileName);
		details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		details.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		details.setFinEvent(FinServiceEvent.ORG);
		details.setDocImage(legalDetail.getDocImage());
		details.setReferenceId(legalDetail.getLegalReference());
		details.setFinReference(legalDetail.getLoanReference());
		legalDetailService.saveDocumentDetails(details);

		engine.close();
		logger.debug(Literal.LEAVING);

		return fileName;
	}

	/*
	 * Sending the mail notification
	 */
	private void sendMailNotificationAlert(LegalDetail aLegalDetail) {
		logger.debug(Literal.ENTERING);
		try {
			String[] emails = getUserEmails(aLegalDetail);
			if (emails == null) {
				return;
			}

			// User details
			LoggedInUser userDetail = aLegalDetail.getUserDetails();
			aLegalDetail.setUserName(userDetail.getFullName());
			aLegalDetail.setDesgnation(userDetail.getDepartmentCode());
			aLegalDetail.setEmpCode(userDetail.getStaffId());

			String subject = "Legal Notification!";

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("vo", aLegalDetail);

			StringTemplateLoader loader = new StringTemplateLoader();
			loader.putTemplate("legalTemplate", new String(getContent(), "UTF-8"));
			getFreemarkerMailConfiguration().setTemplateLoader(loader);
			Template template = getFreemarkerMailConfiguration().getTemplate("legalTemplate");
			String result = "";

			try {
				result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
				throw new Exception("Unable to read or process freemarker configuration or template", e);
			} catch (TemplateException e) {
				logger.error(Literal.EXCEPTION, e);
				throw new Exception("Problem initializing freemarker or rendering template ", e);
			}

			Notification emailMessage = new Notification();
			emailMessage.setKeyReference("");
			emailMessage.setModule(subject);
			emailMessage.setSubModule(subject);
			emailMessage.setStage(aLegalDetail.getRoleCode());
			emailMessage.setSubject(subject);
			emailMessage.setContent(result.getBytes(Charset.forName("UTF-8")));
			emailMessage.setContentType(EmailBodyType.HTML.getKey());

			for (String mailId : emails) {
				MessageAddress address = new MessageAddress();
				address.setEmailId(mailId);
				address.setRecipientType(RecipientType.TO.getKey());
				emailMessage.getAddressesList().add(address);
			}

			emailEngine.sendEmail(emailMessage);

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private byte[] getContent() throws Exception {
		byte[] emailContent = null;
		try {
			emailContent = FileUtils.readFileToByteArray(
					new File(PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS) + "/Legal/LegalNotification.html"));
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		}
		return emailContent;
	}

	private String[] getUserEmails(LegalDetail aLegalDetail) {
		List<String> emails = null;
		if (PennantConstants.method_doApprove.equals(getMethod())) {
			emails = getSecurityUserOperationsService().getUsrMailsByRoleIds(aLegalDetail.getFinNextRoleCode());
		} else {
			emails = getSecurityUserOperationsService().getUsrMailsByRoleIds(aLegalDetail.getNextRoleCode());
		}
		if (CollectionUtils.isNotEmpty(emails)) {
			int arraySize = 0;
			for (String email : emails) {
				if (StringUtils.isNotBlank(email)) {
					arraySize = arraySize + 1;
				}
			}
			String[] emailArray = new String[arraySize];
			int i = 0;
			for (String email : emails) {
				if (StringUtils.isNotBlank(email)) {
					emailArray[i] = email;
					i++;
				}
			}
			return emailArray;
		}
		return null;
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

	public FinCovenantTypeListCtrl getFinCovenantTypeListCtrl() {
		return finCovenantTypeListCtrl;
	}

	public void setFinCovenantTypeListCtrl(FinCovenantTypeListCtrl finCovenantTypeListCtrl) {
		this.finCovenantTypeListCtrl = finCovenantTypeListCtrl;
	}

	public SearchProcessor getSearchProcessor() {
		return searchProcessor;
	}

	public void setSearchProcessor(SearchProcessor searchProcessor) {
		this.searchProcessor = searchProcessor;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}

	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	public SecurityUserOperationsService getSecurityUserOperationsService() {
		return securityUserOperationsService;
	}

	public void setSecurityUserOperationsService(SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	public boolean isNewApplicants() {
		return newApplicants;
	}

	public void setNewApplicants(boolean newApplicants) {
		this.newApplicants = newApplicants;
	}

	public Configuration getFreemarkerMailConfiguration() {
		return freemarkerMailConfiguration;
	}

	public void setFreemarkerMailConfiguration(Configuration freemarkerMailConfiguration) {
		this.freemarkerMailConfiguration = freemarkerMailConfiguration;
	}

	public LegalDetailLoanListCtrl getLegalDetailLoanListCtrl() {
		return legalDetailLoanListCtrl;
	}

	public void setLegalDetailLoanListCtrl(LegalDetailLoanListCtrl legalDetailLoanListCtrl) {
		this.legalDetailLoanListCtrl = legalDetailLoanListCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

}
