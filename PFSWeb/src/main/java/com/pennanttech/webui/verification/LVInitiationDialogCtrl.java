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
 * * FileName : LVInitiationDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-04-2018 * *
 * Modified Date : 26-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.webui.verification;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.CollateralSetupFetchingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.fi.LVStatus;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/Verification/LVInitiationDialog.zul file.
 */
@Component(value = "lvInitiationDialogCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LVInitiationDialogCtrl extends GFCBaseCtrl<Verification> {
	private static final long serialVersionUID = -3093280086658721485L;
	private static final Logger logger = LogManager.getLogger(LVInitiationDialogCtrl.class);

	protected Window window_LVInitiationDialog;

	protected Listbox listBoxCollateralDocuments;
	protected Listbox listBoxLoanDocuments;
	protected Listbox listBoxCustomerDocuments;
	protected Textbox remarks;

	// Initiation components
	protected ExtendedCombobox collateral;
	protected ExtendedCombobox agency;
	protected Row collateralRow;
	protected Row agencyRow;
	protected Row loanRow;
	protected Row customerRow;
	protected Row verificationRow;

	// Waiver components
	protected ExtendedCombobox reason;
	protected Row reasonRow;
	protected Combobox verificationCategory;
	protected Button btnSearchCollateralRef;
	// not auto wired vars
	private Verification verification;
	private transient LVerificationCtrl lVerificationCtrl;
	private transient boolean validationOn;

	private boolean newRecord = false;
	private List<Verification> verifications;
	private String moduleType = "";
	private StringBuilder collateralRefList = new StringBuilder();
	private boolean initiation = false;
	private List<LVDocument> lvDocuments;

	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private FinanceDetail financeDetail;

	private Set<String> lvRequiredDocs = new HashSet<>();
	private Map<String, String> documentMap = new HashMap<>();

	private Map<Long, Verification> documentStatus = new HashedMap<>();

	@Autowired
	private SearchProcessor searchProcessor;
	@Autowired
	private transient VerificationService verificationService;
	@Autowired
	private transient LegalVerificationService legalVerificationService;
	@Autowired
	private transient CollateralSetupFetchingService collateralSetupFetchingService;
	@Autowired
	private transient CollateralSetupService collateralSetupService;
	private String collateralAddrCol = ImplementationConstants.VER_TV_COLL_ED_ADDR_COLUMN;

	/**
	 * default constructor.<br>
	 */
	public LVInitiationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LVInitiationDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerPhoneNumber object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_LVInitiationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LVInitiationDialog);

		try {

			if (arguments.containsKey("verification")) {
				this.verification = (Verification) arguments.get("verification");
				Verification befImage = new Verification();
				BeanUtils.copyProperties(this.verification, befImage);
				this.verification.setBefImage(befImage);
				setVerification(this.verification);
			} else {
				setVerification(null);
			}

			if (arguments.containsKey("initiation")) {
				this.initiation = (boolean) arguments.get("initiation");
			}

			if (arguments.containsKey("lvDocuments")) {
				this.lvDocuments = (List<LVDocument>) arguments.get("lvDocuments");
			}

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(this.financeDetail);
				if (!financeDetail.getCollateralAssignmentList().isEmpty()) {
					for (CollateralAssignment item : financeDetail.getCollateralAssignmentList()) {
						if (collateralRefList.length() > 0) {
							collateralRefList.append(", ");
						}
						collateralRefList.append("'" + item.getCollateralRef() + "'");
					}
				}
			}

			if (arguments.containsKey("financeMainBaseCtrl")) {
				this.financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");

				for (String collteralRef : financeMainDialogCtrl.getAssignCollateralRef()) {
					if (collateralRefList.length() > 0) {
						collateralRefList.append(", ");
					}
					collateralRefList.append("'" + collteralRef + "'");
				}
			}

			if (getVerification().isNewRecord()) {
				setNewRecord(true);
			}

			doLoadWorkFlow(this.verification.isWorkflow(), this.verification.getWorkflowId(),
					this.verification.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "LVInitiationDialog");
			}

			if (arguments.containsKey("legalVerificationListCtrl")) {
				setLegalVerificationListCtrl((LVerificationCtrl) arguments.get("legalVerificationListCtrl"));
			} else {
				setLegalVerificationListCtrl(null);
			}

			setDocumentDetails();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVerification());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LVInitiationDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	private void fillListBox() {
		if (lVerificationCtrl != null) {
			if (verification.isWaiveTab()) {
				lVerificationCtrl.renderLVInitiationList();
				lVerificationCtrl.renderLVWaiverList();
			} else if (initiation) {
				lVerificationCtrl.renderLVInitiationList();
			} else {
				lVerificationCtrl.renderLVWaiverList();
			}
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.collateral.setMandatoryStyle(true);
		this.collateral.setTextBoxWidth(121);
		this.collateral.setModuleName("CollateralSetup");
		this.collateral.setValueColumn("CollateralRef");
		this.collateral.setDescColumn("CollateralType");
		this.collateral.setValidateColumns(new String[] { "CollateralRef" });
		this.collateral.addForward("onFulfill", self, "onChangeCollateral");

		if (initiation) {
			this.agency.setMandatoryStyle(true);
			this.agency.setTextBoxWidth(121);
			this.agency.setModuleName("VerificationAgencies");
			this.agency.setValueColumn("DealerName");
			this.agency.setDescColumn("DealerCity");
			this.agency.setValidateColumns(new String[] { "DealerName" });
			this.agency
					.setFilters(new Filter[] { new Filter("DealerType", Agencies.LVAGENCY.getKey(), Filter.OP_EQUAL) });

		} else {

			this.reason.setMandatoryStyle(true);
			this.reason.setTextBoxWidth(121);
			this.reason.setModuleName("VerificationWaiverReason");
			this.reason.setValueColumn("Code");
			this.reason.setDescColumn("Description");
			this.reason.setValidateColumns(new String[] { "Code" });
			this.reason.setFilters(
					new Filter[] { new Filter("ReasonTypecode", WaiverReasons.LVWRES.getKey(), Filter.OP_EQUAL) });

			if (isWorkFlowEnabled()) {
				this.groupboxWf.setVisible(true);
			} else {
				this.groupboxWf.setVisible(false);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	// Getting the approved collateral setup values from search object and adding the newly created collateral setup
	// list
	public void setCollateralTypeList(List<CollateralAssignment> collateralAsssignments,
			List<CollateralSetup> collateralSetupList) {

		collateralSetupList = getCollateralSetupFetchingService().getResultantCollateralsList(collateralAsssignments,
				collateralSetupList);

		StringBuilder whereClause = new StringBuilder();
		if (collateralRefList.length() > 0) {
			whereClause.append("CollateralRef in (" + collateralRefList + ")");
		} else {
			whereClause.append("CollateralRef in (" + "''" + ")");
		}

		Search search = new Search(CollateralSetup.class);
		search.addTabelName("CollateralSetup_AView");
		search.addWhereClause(whereClause.toString());
		List<CollateralSetup> collateralSetupSearchList = searchProcessor.getResults(search);

		if (CollectionUtils.isEmpty(collateralSetupSearchList)) {
			collateralSetupSearchList = new ArrayList<CollateralSetup>();
		}

		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			collateralSetupSearchList.addAll(collateralSetupList);
		}
		this.collateral.setList(collateralSetupSearchList);
	}

	public void onChangeCollateral(ForwardEvent event) {
		Object dataObject = this.collateral.getObject();
		if (dataObject != null) {
			if (listBoxCollateralDocuments.getItems() != null) {
				listBoxCollateralDocuments.getItems().clear();
			}
			if (dataObject instanceof String) {
				collateral.setValue(dataObject.toString());
				collateral.setDescription("");
			} else {
				CollateralSetup object = (CollateralSetup) dataObject;
				collateral.setAttribute("collateralRef", object.getCollateralRef());

				List<Verification> list = verificationService.getCollateralDocumentsStatus(object.getCollateralRef());
				documentStatus.clear();
				for (Verification item : list) {
					documentStatus.put(item.getDocumentId(), item);
				}
				fillDocuments(this.listBoxCollateralDocuments, this.lvDocuments, DocumentType.COLLATRL,
						object.getCollateralRef());
			}
		}
	}

	/**
	 * This method will set the agency filters by using collateral city
	 * 
	 * @param collateralRef
	 */
	private void setAgencyFiltersByCollateralCity(String collateralRef) {
		if (StringUtils.isBlank(collateralRef) || !ImplementationConstants.VER_AGENCY_FILTER_BY_CITY) {
			return;
		}

		List<String> collateralCities = new ArrayList<>(1);
		CollateralSetup collateralSetup = collateralSetupService.getCollateralSetupByRef(collateralRef, "", true);

		if (collateralSetup == null) {
			return;
		}

		if (CollectionUtils.isEmpty(collateralSetup.getExtendedFieldRenderList())) {
			return;
		}

		agency.setValue("");
		agency.setObject(null);

		for (ExtendedFieldRender fieldRender : collateralSetup.getExtendedFieldRenderList()) {
			Map<String, Object> mapValues = fieldRender.getMapValues();
			if (mapValues != null && mapValues.containsKey(collateralAddrCol)) {
				if (!StringUtils.isEmpty((String) mapValues.get(collateralAddrCol))) {
					collateralCities.add((String) mapValues.get(collateralAddrCol));
				}
			}
		}

		Filter[] filter = new Filter[2];
		filter[0] = new Filter("DealerType", Agencies.LVAGENCY.getKey(), Filter.OP_EQUAL);
		if (CollectionUtils.isNotEmpty(collateralCities)) {
			filter[1] = new Filter("DealerCity", collateralCities, Filter.OP_IN);
		}

		agency.setFilters(filter);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		doSave();
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param eventl
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		MessageUtil.showHelpWindow(event, window_LVInitiationDialog);
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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
		doWriteBeanToComponents(this.verification.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVerification CustomerPhoneNumber
	 */
	public void doWriteBeanToComponents(Verification aVerification) {
		logger.debug(Literal.ENTERING);

		this.remarks.setValue(aVerification.getRemarks());

		if (verification.getRequestType() == RequestType.INITIATE.getKey()) {
			if (aVerification.isWaiveTab()) {
				this.collateral.setValue(aVerification.getReferenceFor());
				this.collateral.setDescription(aVerification.getCollateralSetup().getCollateralType());
			} else if (initiation) {
				this.collateral.setValue(aVerification.getReferenceFor());
				this.collateral.setDescription(aVerification.getReferenceType());
				setAgencyFiltersByCollateralCity(aVerification.getReferenceFor());
			}
		}
		this.collateral.setAttribute("collateralType", aVerification.getReferenceType());
		fillComboBox(this.verificationCategory, String.valueOf(verification.getVerificationCategory()),
				PennantStaticListUtil.getLegalVerificationCategories(), "");
		setCollateralTypeList(getFinanceDetail().getCollateralAssignmentList(), getFinanceDetail().getCollaterals());

		if (initiation) {
			this.agency.setValue(aVerification.getAgencyName());
			this.agency.setDescription(aVerification.getAgencyCity());
			this.agency.setAttribute("agencyId", aVerification.getAgency());
		} else {
			this.reason.setValue(aVerification.getReasonName());
			this.reason.setAttribute("reasonName", aVerification.getReasonName());
		}

		if (verification.getReferenceFor() != null) {
			fillDocuments(this.listBoxCollateralDocuments, this.lvDocuments, DocumentType.COLLATRL,
					verification.getReferenceFor());
		}
		if (initiation) {
			fillDocuments(this.listBoxLoanDocuments, this.lvDocuments, DocumentType.LOAN, null);
			fillDocuments(this.listBoxCustomerDocuments, this.lvDocuments, DocumentType.CUSTOMER, null);
		}

		logger.debug(Literal.LEAVING);
	}

	private List<String> getCheckedDocuments(List<LVDocument> lvDocuments) {
		List<String> docKeys = new ArrayList<>();
		for (LVDocument document : lvDocuments) {
			if (document.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				docKeys.add(String.valueOf(document.getDocumentId()));
			} else {
				docKeys.add(String.valueOf(document.getDocumentType()).concat(document.getDocumentSubId()));
			}
		}
		return docKeys;
	}

	private List<String> getOldDocumentRefIds(List<LVDocument> oldDocuments) {
		List<String> documentRefIds = new ArrayList<>();
		for (LVDocument oldDoc : oldDocuments) {
			documentRefIds.add(String.valueOf(oldDoc.getDocumentRefId()));
		}
		return documentRefIds;
	}

	public void fillDocuments(Listbox listbox, List<LVDocument> documents, DocumentType docType, String collateralRef) {
		List<String> checkedDocuments = new ArrayList<>();
		Map<Long, String> changedDocuments = new HashMap<>();
		List<String> idList = new ArrayList<>();
		List<String> oldDocumentRefIds;

		if (initiation) {
			listbox.getItems().clear();
			if (!verification.getLvDocuments().isEmpty()) {
				checkedDocuments = getCheckedDocuments(this.verification.getLvDocuments());
			}
		}

		List<LVDocument> oldDocuments = legalVerificationService.getLVDocuments(verification.getKeyReference());
		oldDocumentRefIds = getOldDocumentRefIds(oldDocuments);

		// Find changed collateral document and added it as new Document
		if (collateralRef != null) {
			for (LVDocument newDoc : documents) {
				for (LVDocument oldDoc : oldDocuments) {
					if (newDoc.getDocumentType() == DocumentType.COLLATRL.getKey()
							&& newDoc.getDocumentId().equals(oldDoc.getDocumentId())) {
						Verification vrf = new Verification();
						if (newDoc.getDocumentRefId() != null && oldDoc.getDocumentRefId() != null) {
							if (!newDoc.getDocumentRefId().equals(oldDoc.getDocumentRefId())
									&& verificationService.isVerificationInRecording(vrf, VerificationType.LV)
									&& !oldDocumentRefIds.contains(String.valueOf(newDoc.getDocumentRefId()))) {
								changedDocuments.put(newDoc.getDocumentRefId(), String.valueOf(newDoc.getDocumentId()));
							}
						}
					}
				}
			}
		}

		// get saved collateral documents ids.
		for (LVDocument lvDocument : oldDocuments) {
			idList.add(String.valueOf(lvDocument.getDocumentId()));
		}

		for (LVDocument document : documents) {
			if (docType.getKey() != document.getDocumentType()) {
				continue;
			}

			String reference = String.valueOf(document.getDocumentType()).concat(document.getDocumentSubId());

			if (document.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				if (StringUtils.isNotEmpty(collateralRef) && !collateralRef.equals(document.getCollateralRef())) {
					continue;
				} else {
					reference = String.valueOf(document.getDocumentId());
				}
			}

			// invisible the saved collateral documents
			if (idList.contains(reference) && !checkedDocuments.contains(reference)
					&& !changedDocuments.values().contains(reference)) {
				continue;
			}

			Listitem item = new Listitem();
			Listcell lc;
			Checkbox checkbox = new Checkbox();
			checkbox.setValue(document);
			checkbox.setLabel(getDocumentName(document.getDocumentSubId()));

			if ((initiation && lvRequiredDocs.contains(document.getDocumentSubId()) && this.verification.isNewRecord())
					|| checkedDocuments.contains(reference)) {
				checkbox.setChecked(true);
			}

			lc = new Listcell();
			lc.appendChild(checkbox);
			lc.setParent(item);

			if (document.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				Verification object = documentStatus.get(document.getDocumentId());

				if (object == null) {
					object = new Verification();
				}

				lc = new Listcell();
				lc.appendChild(new Label(object.getLastAgency()));
				lc.setParent(item);

				lc = new Listcell();
				if (object.getStatus() != 0) {
					lc.appendChild(new Label(LVStatus.getType(object.getStatus()).getValue()));
				}
				lc.setParent(item);

				lc = new Listcell();
				lc.appendChild(new Label(DateUtil.formatToShortDate(object.getVerificationDate())));
				lc.setParent(item);
			}

			listbox.appendChild(item);
		}
	}

	private String getDocumentName(String code) {
		StringBuilder builder = new StringBuilder();

		builder.append(code);
		if (documentMap.containsKey(code)) {
			builder.append(" - ");
			builder.append(documentMap.get(code));
		}

		return builder.toString();

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVerification
	 */
	public void doWriteComponentsToBean(Verification aVerification) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// collateral Type
		try {
			if (StringUtils.isNotBlank(this.collateral.getValue())) {
				Object object = this.collateral.getObject();
				if (object != null) {
					CollateralSetup collateralSetup = (CollateralSetup) object;
					aVerification.setReferenceFor(collateralSetup.getCollateralRef());
					if (initiation) {
						aVerification.setReferenceType(collateralSetup.getCollateralType());
					}
				} else {
					aVerification.setReferenceFor(String.valueOf(this.collateral.getValue()));
					if (initiation) {
						aVerification.setReferenceType(String.valueOf(collateral.getAttribute("collateralType")));
					}
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (getComboboxValue(this.verificationCategory).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.verificationCategory, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_LVInitiationDialog_VerificationCategory.value") }));
			}
			aVerification.setVerificationCategory(Integer.parseInt(getComboboxValue(this.verificationCategory)));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (initiation) {
			// agency
			try {
				if (StringUtils.isNotBlank(this.agency.getValue())) {
					Object object = this.agency.getAttribute("agency");
					if (object != null) {
						aVerification.setAgency(Long.parseLong(object.toString()));
					} else {
						aVerification.setAgency(Long.parseLong(this.agency.getAttribute("agencyId").toString()));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			// Reason
			try {
				if (StringUtils.isNotBlank(this.reason.getValue())) {
					Object object = this.reason.getObject();
					if (object != null) {
						ReasonCode reasonCode = (ReasonCode) object;
						aVerification.setReason(reasonCode.getId());
						aVerification.setReasonName(String.valueOf(reasonCode.getCode()));
					} else {
						aVerification.setReason(Long.parseLong(this.reason.getValue()));
						aVerification.setReasonName(String.valueOf(this.reason.getAttribute("reasonName")));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		// Remarks
		try {
			aVerification.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// DocumentIds
		setLVDocuments();

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		setVerification(aVerification);
		logger.debug(Literal.LEAVING);
	}

	private void setLVDocuments() {
		verification.getLvDocuments().clear();
		for (Listitem listitem : listBoxCollateralDocuments.getItems()) {
			Checkbox docIdBox = (Checkbox) listitem.getFirstChild().getFirstChild();
			if (docIdBox.isChecked()) {
				this.verification.getLvDocuments().add(docIdBox.getValue());
			}
		}
		// Removed mandatory validation for collateral document
		/*
		 * if (this.verification.getLvDocuments().isEmpty()) { throw new WrongValueException(listBoxCollateralDocuments,
		 * Labels.getLabel("ATLEAST_ONE", new String[] { "Collateral Document" })); }
		 */

		for (Listitem listitem : listBoxLoanDocuments.getItems()) {
			Checkbox docIdBox = (Checkbox) listitem.getFirstChild().getFirstChild();
			if (docIdBox.isChecked()) {
				this.verification.getLvDocuments().add(docIdBox.getValue());
			}
		}
		for (Listitem listitem : listBoxCustomerDocuments.getItems()) {
			Checkbox docIdBox = (Checkbox) listitem.getFirstChild().getFirstChild();
			if (docIdBox.isChecked()) {
				this.verification.getLvDocuments().add(docIdBox.getValue());
			}
		}

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param verification
	 */
	public void doShowDialog(Verification verification) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			// setFocus
			this.collateral.focus();
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			this.agency.focus();
			boolean isLVExists = legalVerificationService.isLVExists(verification.getId());
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitNew();
				doReadOnly();
				btnCancel.setVisible(false);
				if (isLVExists) {
					this.btnSave.setVisible(false);
					this.verificationCategory.setDisabled(true);
				}
			}
		}

		if (initiation) {
			this.reasonRow.setVisible(false);
		} else {
			this.loanRow.setVisible(false);
			this.customerRow.setVisible(false);
			this.agencyRow.setVisible(false);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(verification);

			getLegalVerificationListCtrl().setLvInitiationDialogCtrl(this);

			doCheckEnquiry();
			this.window_LVInitiationDialog.setHeight("80%");
			this.window_LVInitiationDialog.setWidth("75%");
			this.groupboxWf.setVisible(false);
			this.window_LVInitiationDialog.doModal();

		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_LVInitiationDialog.onClose();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		int divHieght = this.borderLayoutHeight - 80;
		int borderlayoutHeights = divHieght / 3;
		this.listBoxCollateralDocuments.setHeight(borderlayoutHeights - 30 + "px");
		this.listBoxLoanDocuments.setHeight(borderlayoutHeights - 30 + "px");
		this.listBoxCustomerDocuments.setHeight(borderlayoutHeights - 30 + "px");

		logger.debug(Literal.LEAVING);
	}

	private void doCheckEnquiry() {
		if ("ENQ".equals(this.moduleType)) {
			this.listBoxCollateralDocuments.setDisabled(true);
			this.agency.setReadonly(true);
			this.remarks.setReadonly(true);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.verificationCategory.setDisabled(true);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		doClearMessage();

		if (!this.collateral.isReadonly()) {
			this.collateral.setConstraint(new PTStringValidator(
					Labels.getLabel("label_LVInitiationDialog_Collateral.value"), null, true, true));
		}

		if (initiation) {
			if (!this.agency.isReadonly()) {
				this.agency.setConstraint(new PTStringValidator(
						Labels.getLabel("label_LVInitiationDialog_Agency.value"), null, true, true));
			}
		} else {
			if (!this.reason.isReadonly()) {
				this.reason.setConstraint(new PTStringValidator(
						Labels.getLabel("label_LVInitiationDialog_Reason.value"), null, true, true));
			}
		}
		if (!this.verificationCategory.isDisabled()) {
			this.verificationCategory.setConstraint(
					new PTListValidator(Labels.getLabel("label_LVInitiationDialog_VerificationCategory.value"),
							PennantStaticListUtil.getLegalVerificationCategories(), true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.collateral.setConstraint("");
		this.agency.setConstraint("");
		this.reason.setConstraint("");
		this.verificationCategory.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.collateral.setErrorMessage("");
		this.agency.setErrorMessage("");
		this.reason.setErrorMessage("");
		this.verificationCategory.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		fillListBox();
	}

	// CRUD operations

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.collateral.setReadonly(false);
			if (verification.isApproveTab() && verification.getRequestType() == RequestType.INITIATE.getKey()) {
				this.collateral.setReadonly(true);
			}
		} else {
			this.btnCancel.setVisible(true);
			this.collateral.setReadonly(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.verification.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if ("ENQ".equals(this.moduleType)) {
				this.btnCtrl.setBtnStatus_New();
				this.btnSave.setVisible(false);
				btnCancel.setVisible(false);
			} else if (isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(false);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.collateral.setReadonly(true);
		this.reason.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		// remove validation, if there are a save before
		if (initiation) {
			this.collateral.setValue("");
			this.agency.setValue("");
		} else {
			this.reason.setValue("");
		}
		this.remarks.setValue("");
		this.listBoxCollateralDocuments.getChildren().clear();
		this.listBoxLoanDocuments.getChildren().clear();
		this.listBoxCustomerDocuments.getChildren().clear();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();

		// fill the CustomerPhoneNumber object with the components data
		doWriteComponentsToBean(this.verification);

		if (!validateLVDocuments()) {
			return;
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = verification.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(verification.getRecordType())) {
				verification.setVersion(verification.getVersion() + 1);
				if (isNew) {
					verification.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					verification.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					verification.setNewRecord(true);
				}
			}
		} else {
			verification.setVersion(verification.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to list
		try {
			verification.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			if (verification.getCreatedOn() == null) {
				verification.setCreatedOn(SysParamUtil.getAppDate());
			}
			if (doProcess(verification, tranType)) {

				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @return
	 */
	private boolean validateLVDocuments() {
		for (Listitem listitem : listBoxCollateralDocuments.getItems()) {
			Checkbox docIdBox = (Checkbox) listitem.getFirstChild().getFirstChild();
			LVDocument document = docIdBox.getValue();
			if (!docIdBox.isChecked() && lvRequiredDocs.contains(document.getDocumentSubId()) && initiation) {
				if (MessageUtil.YES == MessageUtil
						.confirm("Required collateral documents are not selected, Do you want to proceed?")) {
					return true;
				} else {
					return false;
				}
			}

		}
		return true;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aVerification (CustomerAddres)
	 * 
	 * @param tranType      (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Verification aVerification, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aVerification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVerification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVerification.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVerification.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVerification.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVerification);
				}

				if (isNotesMandatory(taskId, aVerification)) {
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

			aVerification.setTaskId(taskId);
			aVerification.setNextTaskId(nextTaskId);
			aVerification.setRoleCode(getRole());
			aVerification.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aVerification, tranType);

			String operationRefs = getServiceOperations(taskId, aVerification);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aVerification, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aVerification, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
		Verification aVerification = (Verification) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					verificationService.delete(auditHeader);
					deleteNotes = true;
				} else {
					if (verification.isApproveTab()) {
						verificationService.savereInitLegalVerification(financeDetail, verification);
					} else {
						verificationService.saveLegalVerification(verification);
					}
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					verificationService.doApprove(auditHeader);

					if (aVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					verificationService.doReject(auditHeader);

					if (aVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.window_LVInitiationDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_LVInitiationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.verification), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// Search Button Component Events

	public void onFulfill$agency(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = agency.getObject();

		if (dataObject instanceof String) {
			agency.setValue(dataObject.toString());
			agency.setDescription("");
		} else {
			VehicleDealer vehicleDealer = (VehicleDealer) dataObject;
			if (vehicleDealer != null) {
				agency.setAttribute("agency", vehicleDealer.getId());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void setDocumentDetails() {
		this.lvRequiredDocs.clear();
		this.documentMap.clear();

		Search search = new Search(com.pennant.backend.model.systemmasters.DocumentType.class);
		search.addField("doctypecode");
		search.addField("docTypeDesc");
		search.addField("lvreq");
		search.addTabelName("BMTDocumentTypes");
		List<com.pennant.backend.model.systemmasters.DocumentType> list = searchProcessor.getResults(search);

		for (com.pennant.backend.model.systemmasters.DocumentType documentType : list) {
			if (documentType.isLvReq()) {
				this.lvRequiredDocs.add(documentType.getDocTypeCode());
			}

			this.documentMap.put(documentType.getDocTypeCode(), documentType.getDocTypeDesc());
		}

	}

	public void onFulfill$collateral(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = collateral.getObject();

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("DealerType", Agencies.LVAGENCY.getKey(), Filter.OP_EQUAL);
		agency.setFilters(filter);

		if (dataObject instanceof String) {
			collateral.setValue(dataObject.toString());
			collateral.setDescription("");
			setAgencyFiltersByCollateralCity(dataObject.toString());
		} else {
			CollateralSetup collateralSetup = (CollateralSetup) dataObject;
			if (collateralSetup != null) {
				collateral.setAttribute("collateralType", collateralSetup.getCollateralType());
				setAgencyFiltersByCollateralCity(collateralSetup.getCollateralRef());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	// WorkFlow Components

	/**
	 * @param verification
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(Verification verification, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, verification.getBefImage(), verification);
		return new AuditHeader(String.valueOf(verification.getId()), null, null, null, auditDetail,
				verification.getUserDetails(), getOverideMap());

	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.verification);
	}

	/**
	 * When user clicks on button "Collateral Reference" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCollateralRef(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		if (StringUtils.isEmpty(collateral.getValue())) {
			throw new WrongValueException(this.collateral, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_LVInitiationDialog_Collateral.value") }));
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		CollateralSetup collateralSetup = collateralSetupService.getCollateralSetupByRef(this.collateral.getValue(), "",
				true);
		if (collateralSetup != null) {
			map.put("collateralSetup", collateralSetup);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null,
					map);
		}

		logger.debug(Literal.LEAVING + event.toString());
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

	public Verification getVerification() {
		return verification;
	}

	public void setVerification(Verification verification) {
		this.verification = verification;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public List<Verification> getVerifications() {
		return verifications;
	}

	public void setVerifications(List<Verification> verifications) {
		this.verifications = verifications;
	}

	public LVerificationCtrl getLegalVerificationListCtrl() {
		return lVerificationCtrl;
	}

	public void setLegalVerificationListCtrl(LVerificationCtrl lVerificationCtrl) {
		this.lVerificationCtrl = lVerificationCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public CollateralSetupFetchingService getCollateralSetupFetchingService() {
		return collateralSetupFetchingService;
	}

	public void setCollateralSetupFetchingService(CollateralSetupFetchingService collateralSetupFetchingService) {
		this.collateralSetupFetchingService = collateralSetupFetchingService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

}
