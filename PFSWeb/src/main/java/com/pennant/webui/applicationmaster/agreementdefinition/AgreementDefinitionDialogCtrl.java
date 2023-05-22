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
 * * FileName : AgreementDefinitionDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-11-2011 * *
 * Modified Date : 23-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.agreementdefinition;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.template.TemplateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/AgreementDefinition
 * /agreementDefinitionDialog.zul file.
 */
public class AgreementDefinitionDialogCtrl extends GFCBaseCtrl<AgreementDefinition> {
	private static final long serialVersionUID = 675917331534316816L;
	private static final Logger logger = LogManager.getLogger(AgreementDefinitionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AgreementDefinitionDialog; // autoWired
	protected Textbox aggCode; // autoWired
	protected Textbox aggName; // autoWired
	protected Textbox aggDesc; // autoWired
	protected Textbox aggReportName; // autoWired
	protected ExtendedCombobox agrRule; // autowired
	// protected Textbox aggReportPath; // autoWired
	protected Checkbox aggIsActive; // autoWired
	protected Checkbox aggCheck_SelectAll; // autoWired
	protected Combobox aggType; // autoWired
	protected Combobox moduleType; // autoWired
	protected Checkbox allowMultiple; // autoWired
	protected Space space_ModuleType;
	protected Vlayout agreementDetails; // autoWired
	protected Combobox moduleName;
	protected Space space_ModuleName;
	protected Row agrRule_row;
	protected Row auto_check;
	protected Row allowMultiple_row;
	protected ExtendedCombobox docType;
	protected Checkbox autoGeneration;
	protected Checkbox autoDownload;
	protected ExtendedCombobox doctype_Check;
	protected Checkbox pwdProtected;
	protected Label label_PwdProtected;

	// protected Button brwAgreementDoc; // autoWired
	// protected Div signCopyPdf; // autoWired
	protected Div orgDetailTabDiv;
	// protected Iframe agreementDocView; // autoWired

	// not auto wired variables
	@SuppressWarnings("unused")
	private String aggImage = null;
	private AgreementDefinition agreementDefinition; // overHanded per parameter
	private AgreementDefinition prvAgreementDefinition; // overHanded per
														// parameter
	private transient AgreementDefinitionListCtrl agreementDefinitionListCtrl; // overHanded
																				// per
																				// parameter
	private org.zkoss.zul.Label label_AgreementDefinitionDialog_autoDownload;
	private org.zkoss.zul.Label label_AgreementDefinitionDialog_doc_Type;
	private org.zkoss.zul.Label label_AgreementDefinitionDialog_AutoGeneration;

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient AgreementDefinitionService agreementDefinitionService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	static final List<ValueLabel> agreementDetailsList = PennantStaticListUtil.getAggDetails();

	/**
	 * default constructor.<br>
	 */
	public AgreementDefinitionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AgreementDefinitionDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected AgreementDefinition object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_AgreementDefinitionDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AgreementDefinitionDialog);

		try {
			/* set components visible dependent of the users rights */

			if (arguments.containsKey("agreementDefinition")) {
				this.agreementDefinition = (AgreementDefinition) arguments.get("agreementDefinition");
				AgreementDefinition befImage = new AgreementDefinition();
				BeanUtils.copyProperties(this.agreementDefinition, befImage);
				this.agreementDefinition.setBefImage(befImage);

				setAgreementDefinition(this.agreementDefinition);
			} else {
				setAgreementDefinition(null);
			}

			doLoadWorkFlow(this.agreementDefinition.isWorkflow(), this.agreementDefinition.getWorkflowId(),
					this.agreementDefinition.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "AgreementDefinitionDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
			if (arguments.containsKey("agreementDefinitionListCtrl")) {
				setAgreementDefinitionListCtrl(
						(AgreementDefinitionListCtrl) arguments.get("agreementDefinitionListCtrl"));
			} else {
				setAgreementDefinitionListCtrl(null);
			}
			doCheckRights();
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getAgreementDefinition());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AgreementDefinitionDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.aggCode.setMaxlength(50);
		this.aggDesc.setMaxlength(100);
		this.aggReportName.setMaxlength(500);

		this.docType.setMandatoryStyle(true);
		this.docType.setModuleName("DocumentType");
		this.docType.setValueColumn("DocTypeCode");
		this.docType.setDescColumn("DocTypeDesc");
		this.docType.setValidateColumns(new String[] { "DocTypeCode" });

		this.agrRule.setMandatoryStyle(false);
		this.agrRule.setModuleName("Rule");
		this.agrRule.setValueColumn("RuleCode");
		this.agrRule.setDescColumn("RuleCodeDesc");
		this.agrRule.setValidateColumns(new String[] { "RuleCode" });

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("RuleModule", RuleConstants.MODULE_AGRRULE, Filter.OP_EQUAL);
		filters[1] = new Filter("RuleEvent", RuleConstants.EVENT_AGRRULE, Filter.OP_EQUAL);
		this.agrRule.setFilters(filters);

		// this.aggReportPath.setMaxlength(100);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		this.label_PwdProtected.setVisible(false);
		this.pwdProtected.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnSave"));
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
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the selectAll CheckBox is checked . <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$aggCheck_SelectAll(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		for (int i = 0; i < agreementDetailsList.size(); i++) {
			Checkbox checkBox = (Checkbox) agreementDetails.getChildren().get(i);
			if (aggCheck_SelectAll.isChecked()) {
				checkBox.setChecked(true);
			} else {
				checkBox.setChecked(false);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$allowMultiple(Event event) {
		logger.debug("Entering" + event.toString());
		this.moduleType.setSelectedIndex(0);
		this.moduleType.setErrorMessage("");
		doCheckAllowMuliple();
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckAllowMuliple() {
		logger.debug("Entering");
		this.moduleType.setDisabled(true);
		if (this.allowMultiple.isChecked()) {
			this.space_ModuleType.setSclass(PennantConstants.mandateSclass);
			this.moduleType.setDisabled(isReadOnly("AgreementDefinitionDialog_aggType"));

		} else {
			this.space_ModuleType.setSclass("");
		}
		logger.debug("Leaving");
	}

	public void doCheckAllowDoctType() {
		logger.debug("Entering");

		String autoGenCheckbox = autoGeneration.getValue().toString();
		if (autoGenCheckbox.equals(true)) {
			this.auto_check.setSclass(PennantConstants.mandateSclass);
			this.docType.setVisible(true);
			this.docType.setButtonDisabled(false);
			this.autoDownload.setVisible(true);
			this.auto_check.setVisible(true);
		} else {
			this.auto_check.setVisible(false);
		}
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
		MessageUtil.showHelpWindow(event, window_AgreementDefinitionDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
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
	 * @param event An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.agreementDefinition.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAgreementDefinition AgreementDefinition
	 */
	public void doWriteBeanToComponents(AgreementDefinition aAgreementDefinition) {
		logger.debug("Entering");
		this.aggCode.setValue(aAgreementDefinition.getAggCode());
		this.aggName.setValue(aAgreementDefinition.getAggName());
		this.aggDesc.setValue(aAgreementDefinition.getAggDesc());
		this.aggReportName.setValue(aAgreementDefinition.getAggReportName());
		this.agrRule.setValue(aAgreementDefinition.getAgrRule());
		this.agrRule.setDescription(aAgreementDefinition.getLovDescAgrRuleDesc());
		// this.aggReportPath.setValue(aAgreementDefinition.getAggReportPath());
		this.aggIsActive.setChecked(aAgreementDefinition.isAggIsActive());
		this.recordStatus.setValue(aAgreementDefinition.getRecordStatus());
		fillComboBox(this.aggType, aAgreementDefinition.getAggtype(), TemplateUtil.getAgreementType(), "");
		fillComboBox(this.moduleType, aAgreementDefinition.getModuleType(), PennantStaticListUtil.getModulType(), "");
		this.allowMultiple.setChecked(aAgreementDefinition.isAllowMultiple());
		if (this.agreementDefinition.isNewRecord()) {
			fillComboBox(this.moduleName, PennantConstants.WORFLOW_MODULE_FINANCE,
					PennantStaticListUtil.getWorkFlowModules(), "");
		} else {
			fillComboBox(this.moduleName, aAgreementDefinition.getModuleName(),
					PennantStaticListUtil.getWorkFlowModules(), "");
		}
		if (aAgreementDefinition.isAutoGeneration()) {
			this.autoGeneration.setChecked(aAgreementDefinition.isAutoGeneration());
			this.autoDownload.setVisible(true);
			this.autoDownload.setChecked(aAgreementDefinition.isAutoDownload());
			this.label_AgreementDefinitionDialog_autoDownload.setVisible(true);
			this.docType.setValue(agreementDefinition.getDocType());
			this.docType.setDescription(agreementDefinition.getLovDescDocumentType());

		} else {
			this.autoDownload.setVisible(false);
			this.label_AgreementDefinitionDialog_autoDownload.setVisible(false);
		}
		if (aAgreementDefinition.isNewRecord()
				|| (aAgreementDefinition.getRecordType() != null ? aAgreementDefinition.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.aggIsActive.setChecked(true);
			this.aggIsActive.setDisabled(true);
		}
		this.aggImage = aAgreementDefinition.getAggImage();
		doFillAggDetailsList(aAgreementDefinition);
		doCheckAllowMuliple();

		String modulename = this.moduleName.getSelectedItem().getValue().toString();
		doModuleSelection(modulename);

		/*
		 * AMedia amedia = null; String docType = aAgreementDefinition.getAggtype(); if
		 * (aAgreementDefinition.getAggImage() != null) { final InputStream data = new
		 * ByteArrayInputStream(aAgreementDefinition.getAggImage());
		 * 
		 * if("JPEG".equals(docType)){ amedia = new AMedia("document.jpg", "jpeg", "image/jpeg", data); } else
		 * if("PNG".equals(docType)){ amedia = new AMedia("document.png", "png", "image/png", data); } else
		 * if("GIF".equals(docType)){ amedia = new AMedia("document.gif", "gif", "image/gif", data); } else
		 * if("PDF".equals(docType)){ amedia = new AMedia("document.pdf", "pdf", "application/pdf", data); } else
		 * if("TEXT".equals(docType)){ amedia = new AMedia("document.txt", "txt", "text/plain", data); }
		 * 
		 * try{ if (docType.equals("WORD")) {
		 * 
		 * FileOutputStream out = new FileOutputStream(aAgreementDefinition.getAggReportName());
		 * out.write(aAgreementDefinition.getAggImage()); out.close();
		 * 
		 * Document doc = new Document(aAgreementDefinition.getAggReportName());
		 * 
		 * String pdfFileName = aAgreementDefinition.getAggReportName().substring(0,
		 * aAgreementDefinition.getAggReportName().lastIndexOf(".")); pdfFileName = pdfFileName +".pdf";
		 * 
		 * doc.save(pdfFileName, SaveFormat.PDF); amedia = new AMedia("document.pdf", "pdf", "application/pdf", new
		 * FileInputStream(pdfFileName));
		 * 
		 * 
		 * } }catch (Exception e) { logger.warn("Exception: ", e); } agreementDocView.setContent(amedia); }
		 */
		this.pwdProtected.setChecked(aAgreementDefinition.isPwdProtected());
		if (PennantConstants.DOC_TYPE_PDF.equals(aAgreementDefinition.getAggtype())) {
			this.pwdProtected.setVisible(true);
			this.label_PwdProtected.setVisible(true);
		} else {
			this.pwdProtected.setVisible(false);
			this.label_PwdProtected.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAgreementDefinition
	 */
	public void doWriteComponentsToBean(AgreementDefinition aAgreementDefinition) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAgreementDefinition.setAggCode(this.aggCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAggName(this.aggName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAggDesc(this.aggDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAgrRule(this.agrRule.getValue());
			aAgreementDefinition.setLovDescAgrRuleDesc(this.agrRule.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAggReportName(this.aggReportName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.moduleName))) {
				throw new WrongValueException(this.moduleName, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_AgreementDefinitionDialog_ModuleName.value") }));
			}
			aAgreementDefinition.setModuleName(getComboboxValue(this.moduleName));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.aggType))) {
				throw new WrongValueException(this.aggType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_AgreementDefinitionDialog_AggType.value") }));
			}
			aAgreementDefinition.setAggtype(getComboboxValue(this.aggType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.allowMultiple.isChecked() && "#".equals(getComboboxValue(this.moduleType))) {
				throw new WrongValueException(this.moduleType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_AgreementDefinitionDialog_ModuleType.value") }));
			}
			aAgreementDefinition.setModuleType(getComboboxValue(this.moduleType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAutoGeneration(this.autoGeneration.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDefinition.setAutoDownload(this.autoDownload.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDefinition.setDocType(this.docType.getValue());
			aAgreementDefinition.setLovDescDocumentType(this.docType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDefinition.setAllowMultiple(this.allowMultiple.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDefinition.setAggIsActive(this.aggIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDefinition.setPwdProtected(this.pwdProtected.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			doSaveAggDetailsList(aAgreementDefinition);
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

		aAgreementDefinition.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on select "Selection Item based on moduleName"
	 * 
	 * @param event
	 */
	public void onSelect$moduleName(Event event) throws SuspendNotAllowedException, InterruptedException {

		logger.debug("Entering " + event.toString());
		String modulename = this.moduleName.getSelectedItem().getValue().toString();
		doModuleSelection(modulename);
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * When user change aggType
	 * 
	 * @param event
	 */
	public void onChange$aggType(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		String type = getComboboxValue(this.aggType);
		if (PennantConstants.DOC_TYPE_PDF.equals(type)) {
			this.pwdProtected.setVisible(true);
			this.label_PwdProtected.setVisible(true);
		} else {
			this.pwdProtected.setVisible(false);
			this.label_PwdProtected.setVisible(false);
			// if the type is not PDF then it will be unchecked
			this.pwdProtected.setChecked(false);
		}
		logger.debug(Literal.LEAVING + event.toString());

	}

	private void doModuleSelection(String modulename) {

		if ((PennantConstants.WORFLOW_MODULE_FINANCE.equals(modulename))) {
			this.auto_check.setVisible(true);
			Filter[] filtersDoc = new Filter[1];
			filtersDoc[0] = new Filter("categorycode", PennantConstants.WORFLOW_MODULE_FINANCE, Filter.OP_EQUAL);
			this.docType.setFilters(filtersDoc);
		}

		if ((PennantConstants.WORFLOW_MODULE_FACILITY.equals(modulename))
				|| (PennantConstants.WORFLOW_MODULE_PROMOTION.equals(modulename))
				|| (PennantConstants.WORFLOW_MODULE_COLLATERAL.equals(modulename))
				|| (PennantConstants.WORFLOW_MODULE_VAS.equals(modulename))
				|| (PennantConstants.WORFLOW_MODULE_COMMITMENT.equals(modulename))) {
			this.agrRule_row.setVisible(false);
			this.allowMultiple_row.setVisible(false);
			this.autoGeneration.setVisible(false);
			this.label_AgreementDefinitionDialog_AutoGeneration.setVisible(false);
			this.docType.setVisible(false);
			this.label_AgreementDefinitionDialog_doc_Type.setVisible(false);
			this.autoDownload.setVisible(false);
			this.label_AgreementDefinitionDialog_autoDownload.setVisible(false);
		} else {
			this.agrRule_row.setVisible(true);
			this.allowMultiple_row.setVisible(true);
			this.autoGeneration.setVisible(true);
			this.label_AgreementDefinitionDialog_AutoGeneration.setVisible(true);
			if (autoGeneration.isChecked()) {
				this.autoDownload.setVisible(true);
				this.label_AgreementDefinitionDialog_autoDownload.setVisible(true);
				this.docType.setVisible(true);
				this.label_AgreementDefinitionDialog_doc_Type.setVisible(true);
			} else {
				this.docType.setButtonDisabled(true);
				this.docType.setVisible(true);
				this.label_AgreementDefinitionDialog_doc_Type.setVisible(true);
			}

		}

	}

	public void onCheck$autoGeneration(Event event) {
		logger.debug("Entering" + event.toString());

		if (autoGeneration.isChecked()) {
			this.autoDownload.setVisible(true);
			this.autoDownload.setChecked(false);
			this.label_AgreementDefinitionDialog_autoDownload.setVisible(true);
			this.docType.setSclass(PennantConstants.mandateSclass);
			this.docType.setValue("");
			this.docType.setMandatoryStyle(true);
			this.docType.setButtonDisabled(false);
			this.label_AgreementDefinitionDialog_doc_Type.setVisible(true);
			this.docType.setVisible(true);
		} else {
			this.docType.setMandatoryStyle(false);
			this.docType.setValue("");
			this.docType.setButtonDisabled(true);
			this.autoDownload.setVisible(false);
			this.autoDownload.setChecked(false);
			this.label_AgreementDefinitionDialog_autoDownload.setVisible(false);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method Fills Agreement Details in Listbox
	 */
	private void doFillAggDetailsList(AgreementDefinition aAgreementDefinition) {
		logger.debug("Entering");

		this.agreementDetails.getChildren().clear();

		String aggDetail1 = aAgreementDefinition.getAggImage() == null ? "" : aAgreementDefinition.getAggImage();
		List<String> aggDetailList = new ArrayList<String>(Arrays.asList(aggDetail1.split(",")));

		if (aggDetailList.size() == agreementDetailsList.size()) {
			aggCheck_SelectAll.setChecked(true);
		} else {
			aggCheck_SelectAll.setChecked(false);
		}

		for (ValueLabel agreementDetail : agreementDetailsList) {
			if (StringUtils.isNotEmpty(agreementDetail.getValue())) {
				Checkbox checkbox = new Checkbox();
				checkbox.setId(agreementDetail.getValue());
				checkbox.setLabel(agreementDetail.getLabel());
				checkbox.setDisabled(isReadOnly("AgreementDefinitionDialog_aggDesc"));
				if (aggDetail1.contains(agreementDetail.getValue())) {
					checkbox.setChecked(true);
				}
				this.agreementDetails.appendChild(checkbox);
			}
		}
		logger.debug("Leaving");
	}

	private void doSaveAggDetailsList(AgreementDefinition aAgreementDefinition) {
		String aggImageTemp = "";
		List<Component> components = agreementDetails.getChildren();
		for (Component component : components) {
			Checkbox checkBox = (Checkbox) component;
			if (checkBox.isChecked()) {
				aggImageTemp = aggImageTemp + checkBox.getId() + ",";
			}
		}
		aAgreementDefinition.setAggImage(aggImageTemp);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aAgreementDefinition
	 */
	public void doShowDialog(AgreementDefinition aAgreementDefinition) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAgreementDefinition.isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.autoDownload.setVisible(false);
			this.label_AgreementDefinitionDialog_autoDownload.setVisible(false);
			doModuleSelection(PennantConstants.WORFLOW_MODULE_FINANCE);
			doEdit();
			// setFocus
			this.aggCode.focus();
		} else {
			this.aggName.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aAgreementDefinition.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAgreementDefinition);

			if (!aAgreementDefinition.isNewRecord() && !isWorkFlowEnabled()) {
				doDisable(true);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AgreementDefinitionDialog.onClose();
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

		if (!this.aggCode.isReadonly()) {
			this.aggCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AgreementDefinitionDialog_AggCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.aggDesc.isReadonly()) {
			this.aggDesc.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AgreementDefinitionDialog_AggDesc.value"), null, true));
		}

		if (!this.aggName.isReadonly()) {
			this.aggName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AgreementDefinitionDialog_AggName.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}

		if (!this.aggReportName.isReadonly()) {
			this.aggReportName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AgreementDefinitionDialog_AggReportName.value"), null, true));
		}

		if (!this.docType.isReadonly()) {
			this.docType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AgreementDefinitionDialog_doc_Type.value"), null,
							autoGeneration.isChecked()));
		}

		/*
		 * if (!this.aggReportPath.isReadonly()){ this.aggReportPath.setConstraint(new SimpleConstraint(
		 * PennantConstants.PATH_REGEX, Labels.getLabel( "MAND_FIELD_ALPHANUMERIC_SPECIALCHARS",new
		 * String[]{Labels.getLabel( "label_AgreementDefinitionDialog_AggReportPath.value")}))); }
		 */
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.aggCode.setConstraint("");
		this.aggName.setConstraint("");
		this.aggDesc.setConstraint("");
		this.agrRule.setConstraint("");
		this.aggReportName.setConstraint("");

		this.docType.setConstraint("");
		// this.aggReportPath.setConstraint("");
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
		this.aggCode.setErrorMessage("");
		this.aggName.setErrorMessage("");
		this.aggDesc.setErrorMessage("");
		this.agrRule.setErrorMessage("");
		this.aggReportName.setErrorMessage("");
		this.aggType.setErrorMessage("");
		this.moduleType.setErrorMessage("");
		this.moduleName.setErrorMessage("");
		this.docType.setErrorMessage("");
		// this.aggReportPath.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a AgreementDefinition object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AgreementDefinition aAgreementDefinition = new AgreementDefinition();
		BeanUtils.copyProperties(getAgreementDefinition(), aAgreementDefinition);

		doDelete(Labels.getLabel("label_AgreementDefinitionDialog_AggCode.value") + " : "
				+ aAgreementDefinition.getAggCode(), aAgreementDefinition);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAgreementDefinition().isNewRecord()) {
			this.aggCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.aggCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.aggName.setReadonly(isReadOnly("AgreementDefinitionDialog_aggName"));
		this.aggDesc.setReadonly(isReadOnly("AgreementDefinitionDialog_aggDesc"));
		this.aggReportName.setReadonly(isReadOnly("AgreementDefinitionDialog_aggReportName"));
		this.agrRule.setReadonly(isReadOnly("AgreementDefinitionDialog_agrRule"));
		this.aggType.setDisabled(isReadOnly("AgreementDefinitionDialog_aggType"));
		this.moduleType.setDisabled(isReadOnly("AgreementDefinitionDialog_moduleType"));
		this.allowMultiple.setDisabled(isReadOnly("AgreementDefinitionDialog_allowMultiple"));
		// this.aggReportPath.setReadonly(isReadOnly("AgreementDefinitionDialog_aggReportPath"));
		this.aggIsActive.setDisabled(isReadOnly("AgreementDefinitionDialog_aggIsActive"));
		this.aggCheck_SelectAll.setDisabled(isReadOnly("AgreementDefinitionDialog_aggDesc"));
		this.moduleName.setDisabled(isReadOnly("AgreementDefinitionDialog_moduleName"));
		this.autoGeneration.setDisabled(isReadOnly("AgreementDefinitionDialog_autoGenerate"));
		this.autoDownload.setDisabled(isReadOnly("AgreementDefinitionDialog_autoDownload"));
		this.docType.setReadonly(isReadOnly("AgreementDefinitionDialog_docType"));
		this.pwdProtected.setDisabled(isReadOnly("AgreementDefinitionDialog_pwdProtected"));
		doDisable(isReadOnly("AgreementDefinitionDialog_aggDesc"));
		if (this.allowMultiple.isChecked()) {
			this.moduleType.setDisabled(isReadOnly("AgreementDefinitionDialog_aggType"));
		} else {
			this.moduleType.setDisabled(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.agreementDefinition.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.aggCode.setReadonly(true);
		this.aggName.setReadonly(true);
		this.aggDesc.setReadonly(true);
		this.aggReportName.setReadonly(true);
		this.agrRule.setReadonly(true);
		this.aggCheck_SelectAll.setDisabled(true);
		this.aggType.setDisabled(true);
		this.moduleType.setDisabled(true);
		this.allowMultiple.setDisabled(true);
		this.moduleName.setDisabled(true);
		this.docType.setReadonly(true);
		this.aggIsActive.setDisabled(true);
		this.pwdProtected.setDisabled(true);
		doDisable(true);
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

		this.aggCode.setValue("");
		this.aggName.setValue("");
		this.aggDesc.setValue("");
		this.aggReportName.setValue("");
		this.agrRule.setValue("");
		// added 3 new Fields
		this.docType.setValue("");
		this.autoGeneration.setValue("");
		this.autoDownload.setValue("");

		this.moduleType.setValue("");
		this.aggType.setValue("");
		this.allowMultiple.setChecked(false);
		// this.aggReportPath.setValue("");
		this.aggIsActive.setChecked(false);
		this.moduleName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final AgreementDefinition aAgreementDefinition = new AgreementDefinition();
		BeanUtils.copyProperties(getAgreementDefinition(), aAgreementDefinition);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the AgreementDefinition object with the components data
		doWriteComponentsToBean(aAgreementDefinition);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAgreementDefinition.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAgreementDefinition.getRecordType())) {
				aAgreementDefinition.setVersion(aAgreementDefinition.getVersion() + 1);
				if (isNew) {
					aAgreementDefinition.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAgreementDefinition.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAgreementDefinition.setNewRecord(true);
				}
			}
		} else {
			aAgreementDefinition.setVersion(aAgreementDefinition.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAgreementDefinition, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAgreementDefinition (AgreementDefinition)
	 * 
	 * @param tranType             (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(AgreementDefinition aAgreementDefinition, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAgreementDefinition.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAgreementDefinition.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAgreementDefinition.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAgreementDefinition.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAgreementDefinition.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAgreementDefinition);
				}

				if (isNotesMandatory(taskId, aAgreementDefinition)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aAgreementDefinition.setTaskId(taskId);
			aAgreementDefinition.setNextTaskId(nextTaskId);
			aAgreementDefinition.setRoleCode(getRole());
			aAgreementDefinition.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAgreementDefinition, tranType);

			String operationRefs = getServiceOperations(taskId, aAgreementDefinition);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAgreementDefinition, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aAgreementDefinition, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		AgreementDefinition aAgreementDefinition = (AgreementDefinition) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getAgreementDefinitionService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getAgreementDefinitionService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getAgreementDefinitionService().doApprove(auditHeader);

					if (aAgreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getAgreementDefinitionService().doReject(auditHeader);
					if (aAgreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AgreementDefinitionDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AgreementDefinitionDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.agreementDefinition), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAddressType
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(AgreementDefinition aAgreementDefinition, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAgreementDefinition.getBefImage(),
				aAgreementDefinition);
		return new AuditHeader(String.valueOf(aAgreementDefinition.getAggId()), null, null, null, auditDetail,
				aAgreementDefinition.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes(this.agreementDefinition));
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getAgreementDefinitionListCtrl().search();
	}

	/*
	 * public void onUpload$brwAgreementDoc(UploadEvent event) { logger.debug("Entering" + event.toString()); Media
	 * media = event.getMedia();
	 * 
	 * if (!PennantAppUtil.uploadDocFormatValidation(media)) { return; } browseDoc(media, getAgreementDefinition());
	 * logger.debug("Leaving" + event.toString()); }
	 */

	/*
	 * private void browseDoc(Media media, AgreementDefinition agreementDefinition) { logger.debug("Entering"); try {
	 * boolean isSupported = true; String docType = ""; String fileName = media.getName(); String mediaDocType =
	 * media.getContentType(); if (mediaDocType.equals("image/gif")) { docType = "GIF"; } else if
	 * (mediaDocType.equals("image/png")) { docType = "PNG"; } else if (mediaDocType.equals("image/jpeg")) { docType =
	 * "JPEG"; } else if (mediaDocType.equals("application/pdf")) { docType = "PDF"; } else if
	 * (mediaDocType.equals("application/msword")) { docType = "WORD"; } else if (mediaDocType.equals("text/plain")) {
	 * docType = "TEXT"; } else { isSupported = false;
	 * MessageUtil.showErrorMessage("Un Supported Format.only "+PennantConstants
	 * .AGREEMENT_DEFINITION_DOCS+" are allowed"); } if (isSupported) { byte[] imageData = null; if(media.isBinary()) {
	 * imageData = IOUtils.toByteArray(media.getStreamData()); } else { imageData =
	 * IOUtils.toByteArray(media.getReaderData()); } agreementDefinition.setAggImage(imageData);
	 * agreementDefinition.setAggtype(docType); this.aggReportName.setValue(fileName); //
	 * this.aggReportPath.setValue(fileName); if(docType.equals("WORD")) { FileOutputStream out = new
	 * FileOutputStream(fileName); out.write(imageData); out.close(); Document doc = new Document(fileName); String
	 * pdfFileName = fileName.substring(0, media.getName().lastIndexOf(".")); pdfFileName = pdfFileName +".pdf";
	 * doc.save(pdfFileName, SaveFormat.PDF); imageData = IOUtils.toByteArray(new FileInputStream(pdfFileName)); }
	 * if("JPEG".equals(docType)){ this.agreementDocView.setContent(new AMedia("document.jpg", "image/jpeg",
	 * mediaDocType, imageData)); } else if("PNG".equals(docType)){ this.agreementDocView.setContent(new
	 * AMedia("document.png", "image/png", mediaDocType, imageData)); } else if("GIF".equals(docType)){
	 * this.agreementDocView.setContent(new AMedia("document.gif", "image/gif", mediaDocType, imageData)); } else
	 * if("PDF".equals(docType) || "WORD".equals(docType)){ this.agreementDocView.setContent(new AMedia("document.pdf",
	 * "pdf", "application/pdf", imageData)); } else if("TEXT".equals(docType)){ this.agreementDocView.setContent(new
	 * AMedia("document.txt", "txt", "text/plain", imageData)); }
	 * 
	 * }
	 * 
	 * 
	 * } catch (Exception e) { logger.warn("Exception: ", e); } logger.debug("Leaving" + event.toString()); }
	 */

	public void doDisable(boolean dsiable) {
		List<Component> list = this.agreementDetails.getChildren();
		for (Component component : list) {
			if (component instanceof Checkbox) {
				Checkbox checkbox = (Checkbox) component;
				checkbox.setDisabled(dsiable);
			}
		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.agreementDefinition.getAggId());
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

	public AgreementDefinition getAgreementDefinition() {
		return this.agreementDefinition;
	}

	public void setAgreementDefinition(AgreementDefinition agreementDefinition) {
		this.agreementDefinition = agreementDefinition;
	}

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return this.agreementDefinitionService;
	}

	public void setAgreementDefinitionListCtrl(AgreementDefinitionListCtrl agreementDefinitionListCtrl) {
		this.agreementDefinitionListCtrl = agreementDefinitionListCtrl;
	}

	public AgreementDefinitionListCtrl getAgreementDefinitionListCtrl() {
		return this.agreementDefinitionListCtrl;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public AgreementDefinition getPrvAgreementDefinition() {
		return prvAgreementDefinition;
	}

}
