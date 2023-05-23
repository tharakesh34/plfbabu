package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.systemmasters.OCRHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.financemanagement.ocr.OCRMaintenanceListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class FinOCRDialogCtrl extends GFCBaseCtrl<FinOCRHeader> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = LogManager.getLogger(FinOCRDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinOCRDialog;
	// OCR Definition Fields
	protected Listbox listBoxFinOCRSteps;
	protected Div ocrStepsDiv;
	protected Button btnNew_FinOCRStep;
	protected ExtendedCombobox ocrID;
	protected Textbox ocrDescription;
	protected Decimalbox customerPortion;
	protected Combobox ocrType;
	protected CurrencyBox totalDemand;
	protected CurrencyBox totalReceivable;
	// OCR capture Fields
	protected Textbox loanReference;
	protected CurrencyBox ocrCprTotReceivble;
	protected CurrencyBox ocrTotalDemand;
	protected CurrencyBox ocrTotalPaid;

	protected CurrencyBox tdTotalDemand;
	protected CurrencyBox tdTotalReceivable;

	protected South finOCRSouth;
	protected North finOCRNorth;

	protected Button btnNew_FinOCRCapture;
	protected Listbox listBoxOCRCapture;
	// For Dynamically calling of this Controller
	private Object financeMainDialogCtrl;
	private FinanceDetail financeDetail;
	private Component parent = null;
	private FinOCRHeader finOCRHeader;
	private List<FinOCRDetail> finOCRDetailList = new ArrayList<FinOCRDetail>();
	private String roleCode = "";
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private String allowedRoles;
	private FinanceType financeType;
	private boolean definitionApproved = false;
	private String moduleName;
	private ArrayList<Object> headerList;
	private List<ValueLabel> applicableList = PennantStaticListUtil.getOCRApplicableList();
	private OCRHeaderService ocrHeaderService;
	private FinOCRHeaderService finOCRHeaderService;
	@Autowired
	private SearchProcessor searchProcessor;
	// Fin OCR Capture list
	private List<FinOCRCapture> finOCRCaptureList = new ArrayList<FinOCRCapture>();
	private int ccyFormatter = 0;

	private Tab tabOCRDefinition;
	private boolean isFinanceProcess = false;
	private transient OCRMaintenanceListCtrl ocrMaintenanceListCtrl;
	private FinanceDetailService financeDetailService;
	private String parentRef;

	/**
	 * default constructor.<br>
	 */
	public FinOCRDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinOCRDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinOCRDialog object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinOCRDialog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FinOCRDialog);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setFinOCRDialogCtrl", this.getClass())
							.invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FinOCRDialog.setTitle("");
			}

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormatter = (Integer) arguments.get("ccyFormatter");
			}

			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}

			if (arguments.containsKey("definitionApproved")) {
				definitionApproved = (boolean) arguments.get("definitionApproved");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, this.pageRightName);
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (arguments.containsKey("allowedRoles")) {
				allowedRoles = (String) arguments.get("allowedRoles");
			}

			// moduleName
			if (arguments.containsKey("moduleName")) {
				this.moduleName = (String) arguments.get("moduleName");
			}

			if (arguments.containsKey("finHeaderList")) {
				headerList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (boolean) arguments.get("isFinanceProcess");
			}

			if (arguments.containsKey("ocrMaintenanceListCtrl")) {
				ocrMaintenanceListCtrl = (OCRMaintenanceListCtrl) arguments.get("ocrMaintenanceListCtrl");
			}

			FinanceDetail fd = getFinanceDetail();
			if (fd != null) {
				financeType = fd.getFinScheduleData().getFinanceType();
				FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
				OCRHeader ocrHeader = null;

				if (fm != null) {
					parentRef = fm.getParentRef();
				}

				if (fd.getFinOCRHeader() != null) {
					if (fm != null && StringUtils.isNotEmpty(parentRef)) {
						this.totalDemand.setDisabled(true);
					}
					setFinOCRHeader(fd.getFinOCRHeader());
				}

				FinOCRHeader finOCRHeader = null;
				if (isFinanceProcess && fm != null && StringUtils.isNotEmpty(fm.getParentRef())
						&& fm.isFinOcrRequired()) {
					finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(fm.getFinID(),
							TableType.VIEW.getSuffix());

					if (finOCRHeader != null) {
						ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(finOCRHeader.getOcrID(),
								TableType.AVIEW.getSuffix());
						if (fd != null && StringUtils.equals(fd.getModuleDefiner(), FinServiceEvent.ORG)) {
							setFinOCRHeader(copyOCRHeaderProperties(ocrHeader, finOCRHeader));
						}
						if (fd.getFinOCRHeader() != null
								&& StringUtils.isNotEmpty(fd.getFinOCRHeader().getRecordStatus())) {
							getFinOCRHeader().setNewRecord(false);
							getFinOCRHeader().setHeaderID(fd.getFinOCRHeader().getHeaderID());
							getFinOCRHeader().setTotalDemand(fd.getFinOCRHeader().getTotalDemand());
							getFinOCRHeader().setFinOCRCapturesList(fd.getFinOCRHeader().getFinOCRCapturesList());
							getFinOCRHeader().setRecordStatus(fd.getFinOCRHeader().getRecordStatus());
						}

						if (finOCRHeader.getTotalDemand() != null) {
							getFinOCRHeader().setTotalDemand(finOCRHeader.getTotalDemand());
						}
					}

					this.totalDemand.setDisabled(true);
				} else if (isFinanceProcess && financeType != null
						&& StringUtils.isNotEmpty(financeType.getDefaultOCR())
						&& (fd.getFinOCRHeader() != null && (StringUtils.isEmpty(fd.getFinOCRHeader().getOcrID())
								|| fd.getFinOCRHeader().isNewRecord()))) {
					// get default OCR header details from loan type
					ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(financeType.getDefaultOCR(),
							TableType.AVIEW.getSuffix());
					setFinOCRHeader(copyOCRHeaderProperties(ocrHeader, null));
					if (fd.getFinOCRHeader() != null
							&& StringUtils.isNotEmpty(fd.getFinOCRHeader().getRecordStatus())) {
						getFinOCRHeader().setNewRecord(false);
						getFinOCRHeader().setHeaderID(fd.getFinOCRHeader().getHeaderID());
						getFinOCRHeader().setTotalDemand(fd.getFinOCRHeader().getTotalDemand());
						getFinOCRHeader().setFinOCRCapturesList(fd.getFinOCRHeader().getFinOCRCapturesList());
					}
				}
			}
			if (getFinOCRHeader() == null) {
				this.finOCRHeader = new FinOCRHeader();
				this.finOCRHeader.setNewRecord(true);
			}

			if (!isFinanceProcess && fd.getFinOCRHeader() != null) {
				this.finOCRHeader.setWorkflowId(fd.getFinOCRHeader().getWorkflowId());
			}

			doLoadWorkFlow(this.finOCRHeader.isWorkflow(), this.finOCRHeader.getWorkflowId(),
					this.finOCRHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				if (!isFinanceProcess) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(finOCRHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.ocrID.setMandatoryStyle(true);
		this.ocrID.setMaxlength(20);
		this.ocrID.setTextBoxWidth(150);
		this.ocrID.setModuleName("OCRHeader");
		this.ocrID.setValueColumn("OcrID");
		this.ocrID.setValidateColumns(new String[] { "OcrID" });
		// Filters for OCR Id as per the loan type
		if (financeType != null && !StringUtils.isEmpty(financeType.getAllowedOCRS())) {
			List<String> detailsList = Arrays.asList(financeType.getAllowedOCRS().split(","));
			this.ocrID.setFilters(new Filter[] { new Filter("OcrID", detailsList, Filter.OP_IN) });
		}
		this.ocrDescription.setMaxlength(100);
		this.customerPortion.setMaxlength(6);
		this.totalDemand.setProperties(true, ccyFormatter);
		this.totalReceivable.setDisabled(true);
		this.totalReceivable.setProperties(true, ccyFormatter);
		this.ocrStepsDiv.setVisible(false);
		this.listBoxFinOCRSteps.setVisible(false);
		this.loanReference.setDisabled(true);
		this.ocrCprTotReceivble.setDisabled(true);
		this.ocrCprTotReceivble.setProperties(true, ccyFormatter);
		this.ocrTotalDemand.setDisabled(true);
		this.ocrTotalPaid.setDisabled(true);
		this.ocrCprTotReceivble.setScale(2);
		this.ocrTotalDemand.setScale(2);
		this.ocrTotalPaid.setProperties(true, ccyFormatter);
		this.tdTotalDemand.setProperties(true, ccyFormatter);
		this.tdTotalReceivable.setProperties(true, ccyFormatter);
		this.totalReceivable.setProperties(true, ccyFormatter);
		this.ocrCprTotReceivble.setProperties(true, ccyFormatter);
		this.ocrTotalDemand.setProperties(true, ccyFormatter);
		this.totalDemand.setProperties(true, ccyFormatter);
		this.tdTotalDemand.setDisabled(true);
		this.tdTotalReceivable.setDisabled(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!enqiryModule) {
			this.btnNew_FinOCRStep.setVisible(!isReadOnly("FinOCRDialog_btnNew_FinOCRStep"));
			this.btnNew_FinOCRCapture.setVisible(!isReadOnly("FinOCRDialog_btnNew_FinOCRCapture"));
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinOCRDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinOCRDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinOCRDialog_btnDelete"));
			if (StringUtils.isEmpty(parentRef)) {
				this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinOCRDialog_btnSave"));
			} else {
				this.btnNew_FinOCRStep.setVisible(false);
			}
		} else {
			this.btnNew_FinOCRStep.setVisible(false);
			this.btnNew_FinOCRCapture.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinOCRHeader finOCRHeader) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (finOCRHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finOCRHeader.getRecordType())) {
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
		}

		try {
			if (isFinanceProcess && !enqiryModule) {
				appendFinBasicDetails();
			}
			doEdit();
			doCheckEnquiry();
			doCheckDefApproved();
			doWriteBeanToComponents(finOCRHeader);
			this.listBoxFinOCRSteps.setHeight(((borderLayoutHeight - 80) / 2) - 100 + "px");
			this.listBoxOCRCapture.setHeight(borderLayoutHeight - 326 + "px");
			if (isFinanceProcess && parent != null) {
				this.window_FinOCRDialog.setHeight(borderLayoutHeight - 75 + "px");
				parent.appendChild(this.window_FinOCRDialog);
			}
			if (isFinanceProcess) {
				this.finOCRSouth.setVisible(false);
				this.finOCRNorth.setVisible(false);
			} else {
				this.finOCRNorth.setVisible(false);
				if (!enqiryModule) {
					this.finOCRSouth.setVisible(true);
					this.finOCRNorth.setVisible(true);
					setDialog(DialogType.EMBEDDED);
				}
			}
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_FinOCRDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	private void doReadOnly() {

	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finOCRHeader);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param finOCRHeader
	 * 
	 */
	public void doWriteBeanToComponents(FinOCRHeader finOCRHeader) {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.ocrType, finOCRHeader.getOcrType(), applicableList, "");
		this.ocrID.setValue(finOCRHeader.getOcrID());
		this.ocrDescription.setValue(finOCRHeader.getOcrDescription());
		this.customerPortion.setValue(finOCRHeader.getCustomerPortion());
		this.loanReference.setValue(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());

		this.totalDemand.setValue(finOCRHeader.getTotalDemand());
		this.totalReceivable
				.setValue(getCurrentTranchAmount(finOCRHeader.getTotalDemand(), finOCRHeader.getCustomerPortion()));
		if (StringUtils.equals(PennantConstants.SEGMENTED_VALUE, finOCRHeader.getOcrType())) {
			this.ocrStepsDiv.setVisible(true);
			this.listBoxFinOCRSteps.setVisible(true);
		} else {
			this.ocrStepsDiv.setVisible(false);
			this.listBoxFinOCRSteps.setVisible(false);
		}

		// FinOCRStep Details
		doFillFinOCRStepDetails(finOCRHeader.getOcrDetailList());
		financeDetail.setFinOCRHeader(finOCRHeader);
		for (FinOCRCapture finOCRCapture : finOCRHeader.getFinOCRCapturesList()) {
			if (finOCRCapture.getDocumentRef() != null) {
				finOCRCapture.setDocImage(finOCRHeaderService.getDocumentManImage(finOCRCapture.getDocumentRef()));
			}
		}
		// FinOCRStep Details
		doFillFinOCRCaptureDetails(finOCRHeader.getFinOCRCapturesList());
		if (!isFinanceProcess) {
			this.recordStatus.setValue(finOCRHeader.getRecordStatus());
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckEnquiry() {
		if (enqiryModule) {
			this.btnNew_FinOCRStep.setVisible(false);
			readOnlyComponent(true, this.ocrID);
			readOnlyComponent(true, this.ocrDescription);
			readOnlyComponent(true, this.customerPortion);
			readOnlyComponent(true, this.ocrType);
			readOnlyComponent(true, this.totalDemand);
			readOnlyComponent(true, this.totalReceivable);
			// readOnlyComponent(true, this.splitApplicable);
			this.btnNew_FinOCRCapture.setVisible(false);

		}
	}

	private void doCheckDefApproved() {
		if (definitionApproved) {
			this.btnNew_FinOCRStep.setVisible(false);
			readOnlyComponent(true, this.ocrID);
			readOnlyComponent(true, this.ocrDescription);
			readOnlyComponent(true, this.customerPortion);
			readOnlyComponent(true, this.ocrType);
			readOnlyComponent(true, this.totalDemand);
			readOnlyComponent(true, this.totalReceivable);
			// readOnlyComponent(true, this.splitApplicable);
		}

	}

	/**
	 * Double click event for FinOCRStep item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinOCRStepItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRStep);

		Listitem listitem = this.listBoxFinOCRSteps.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinOCRHeader aFinOCRHeader = new FinOCRHeader();
			BeanUtils.copyProperties(this.finOCRHeader, aFinOCRHeader);
			doWriteComponentsToBean(aFinOCRHeader);

			final FinOCRDetail aFinOCRDetail = (FinOCRDetail) listitem.getAttribute("data");

			if (!enqiryModule && isDeleteRecord(aFinOCRDetail)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
				return;
			}

			aFinOCRDetail.setNewRecord(false);
			aFinOCRDetail.setWorkflowId(0);

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finOCRDetail", aFinOCRDetail);
			map.put("finOCRHeader", aFinOCRHeader);
			if (isWorkFlowEnabled()) {
				map.put("roleCode", getRole());
			} else {
				map.put("roleCode", roleCode);
			}
			map.put("finOCRDialogCtrl", this);
			map.put("allowedRoles", allowedRoles);
			map.put("enqiryModule", enqiryModule);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRStepDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Double click event for FinOCRStep item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinOCRCaptureItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRCapture);
		Listitem listitem = this.listBoxOCRCapture.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinOCRCapture aFinOCRCapture = (FinOCRCapture) listitem.getAttribute("data");
			if (!enqiryModule && isDeleteRecord(aFinOCRCapture)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
				return;
			}

			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			if (StringUtils.isNotBlank(financeMain.getParentRef())) {
				if (predicate.test(financeMain.getParentRef())) {
					FinOCRHeader finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(financeMain.getFinID(),
							TableType.TEMP_TAB.getSuffix());
					if (finOCRHeader != null) {
						MessageUtil.showError(Labels.getLabel("label_FinOCRCapture_OCRMaintenance.value"));
						return;
					}
				}
			}

			aFinOCRCapture.setNewRecord(false);
			aFinOCRCapture.setWorkflowId(0);
			if (!enqiryModule) {
				financeDetail = getFinancedetailsFromBase();
			}
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finOCRCapture", aFinOCRCapture);
			map.put("financeDetail", financeDetail);
			if (isWorkFlowEnabled()) {
				map.put("roleCode", getRole());
			} else {
				map.put("roleCode", roleCode);
			}
			map.put("allowedRoles", allowedRoles);
			map.put("finOCRHeader", getFinOCRHeaderData());
			map.put("ccyFormatter", ccyFormatter);
			map.put("finOCRDialogCtrl", this);
			map.put("enqiryModule", enqiryModule);
			// call the ZUL-file with the parameters packed in a map try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRCaptureDialog.zul", null, map);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", headerList);
			map.put("moduleName", moduleName);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	public void doSave() {
		logger.debug("Entering");

		FinOCRHeader aFinOCRHeader = new FinOCRHeader();
		BeanUtils.copyProperties(this.finOCRHeader, aFinOCRHeader);
		aFinOCRHeader.setBefImage(this.finOCRHeader);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the FinOCRHeader object with the components data
		ArrayList<WrongValueException> wve = doWriteComponentsToBean(aFinOCRHeader);
		showErrorDetails(wve, null);
		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here
		boolean proceed = validateOCRDetails(financeDetail, null, aFinOCRHeader);
		if (!proceed) {
			return;
		}
		isNew = aFinOCRHeader.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinOCRHeader.getRecordType())) {
				aFinOCRHeader.setVersion(aFinOCRHeader.getVersion() + 1);
				if (isNew) {
					aFinOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinOCRHeader.setNewRecord(true);
				}
			}
		} else {
			aFinOCRHeader.setVersion(aFinOCRHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFinOCRHeader, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(aFinOCRHeader.getRoleCode(),
						aFinOCRHeader.getNextRoleCode(), aFinOCRHeader.getFinReference(), " OCR ",
						aFinOCRHeader.getRecordStatus(), getNextTaskId());
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	protected boolean doProcess(FinOCRHeader aFinOCRHeader, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFinOCRHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinOCRHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinOCRHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFinOCRHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinOCRHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinOCRHeader);
				}

				if (isNotesMandatory(taskId, aFinOCRHeader)) {
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

			aFinOCRHeader.setTaskId(taskId);
			aFinOCRHeader.setNextTaskId(nextTaskId);
			aFinOCRHeader.setRoleCode(getRole());
			aFinOCRHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinOCRHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aFinOCRHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinOCRHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinOCRHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FinOCRHeader aFinOCRHeader = (FinOCRHeader) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = finOCRHeaderService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = finOCRHeaderService.saveOrUpdate(aAuditHeader, financeDetail, false);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = finOCRHeaderService.doApprove(aAuditHeader, financeDetail, false);

					if (aFinOCRHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = finOCRHeaderService.doReject(aAuditHeader);

					if (aFinOCRHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FinOCRDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_FinOCRDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finOCRHeader), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());
		logger.debug("Leaving");
		return processCompleted;
	}

	private AuditHeader getAuditHeader(FinOCRHeader aFinOCRHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinOCRHeader.getBefImage(), aFinOCRHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinOCRHeader.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finOCRHeader.getHeaderID());
	}

	protected void refreshList() {
		ocrMaintenanceListCtrl.search();
	}

	public boolean doSave(FinanceDetail financeDetail, Tab tab, boolean recSave, Radiogroup userAction) {
		logger.debug(Literal.ENTERING);
		this.userAction = userAction;
		doClearMessage();
		doSetValidation();
		final FinOCRHeader aFinOCRHeader = new FinOCRHeader();
		BeanUtils.copyProperties(this.finOCRHeader, aFinOCRHeader);
		aFinOCRHeader.setBefImage(this.finOCRHeader);

		List<WrongValueException> wve = doWriteComponentsToBean(aFinOCRHeader);
		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, this.tabOCRDefinition);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		aFinOCRHeader.setFinID(financeMain.getFinID());
		aFinOCRHeader.setFinReference(financeMain.getFinReference());
		aFinOCRHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinOCRHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinOCRHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setFinOCRHeader(aFinOCRHeader);

		return validateOCRDetails(financeDetail, tab, aFinOCRHeader);
	}

	private boolean validateOCRDetails(FinanceDetail financeDetail, Tab tab, final FinOCRHeader aFinOCRHeader) {

		BigDecimal totalDemandAmt = BigDecimal.ZERO;
		BigDecimal totalOcrPaid = BigDecimal.ZERO;
		BigDecimal custPortion = aFinOCRHeader.getCustomerPortion();
		BigDecimal disbAmount = BigDecimal.ZERO;
		BigDecimal totalDemandRaised = BigDecimal.ZERO;
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		BigDecimal finAssetValue = financeMain.getFinAssetValue();
		BigDecimal feeAmount = BigDecimal.ZERO;
		List<FinFeeDetail> finFeeDetails = financeDetail.getFinScheduleData().getFinFeeDetailList();
		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
			List<FinFeeDetail> finFeeList = finFeeDetails.stream()
					.filter(finFeeDetail -> finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) == 0)
					.collect(Collectors.toList());
			for (FinFeeDetail finFee : finFeeList) {
				feeAmount = feeAmount.add(finFee.getActualAmount());
			}
		}
		finAssetValue = PennantApplicationUtil.formateAmount(finAssetValue.subtract(feeAmount), ccyFormatter);

		BigDecimal ocrpaid = BigDecimal.ZERO;
		BigDecimal demandAmt = BigDecimal.ZERO;
		List<FinOCRCapture> captureList = aFinOCRHeader.getFinOCRCapturesList();
		if (captureList != null) {
			for (FinOCRCapture finOCRCapture : captureList) {
				ocrpaid = ocrpaid.add(finOCRCapture.getPaidAmount());
				demandAmt = demandAmt.add(finOCRCapture.getDemandAmount());
			}
			demandAmt = PennantApplicationUtil.formateAmount(demandAmt, ccyFormatter);
			ocrpaid = PennantApplicationUtil.formateAmount(ocrpaid, ccyFormatter);
		}

		BigDecimal demandWithoutPaid = demandAmt.subtract(ocrpaid);

		if (demandWithoutPaid.compareTo(finAssetValue) > 0) {
			MessageUtil.showError(Labels.getLabel("label_FinOCRCapture_BuilderDemandAmount_Validation.value"));
			return false;
		}

		totalDemandAmt = this.tdTotalDemand.getActualValue();
		totalOcrPaid = this.tdTotalReceivable.getActualValue();

		if (StringUtils.isNotEmpty(financeMain.getParentRef())) {
			// Parent total demand amount
			FinOCRHeader finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(financeMain.getFinID(),
					TableType.VIEW.getSuffix());
			if (finOCRHeader != null) {
				BigDecimal customerportion = getCurrentTranchAmount(finOCRHeader.getTotalDemand(),
						finOCRHeader.getCustomerPortion());
				totalDemandRaised = totalDemandRaised.add(finOCRHeader.getTotalDemand().subtract(customerportion));
			}

		} else {
			BigDecimal customerportion = getCurrentTranchAmount(aFinOCRHeader.getTotalDemand(),
					finOCRHeader.getCustomerPortion());
			totalDemandRaised = totalDemandRaised.add(aFinOCRHeader.getTotalDemand().subtract(customerportion));
		}

		// Total builder demand raised is more than the Total Demand defined
		if (finOCRHeader.getTotalDemand().compareTo(totalDemandAmt) < 0) {
			MessageUtil.showError(Labels.getLabel("OCR_DEFINITION_BUILDER_DEMAND_AMT_VALIDATION_MSG"));
			return false;
		}

		disbAmount = getDisbAmount(financeDetail);
		if (disbAmount.compareTo(totalDemandRaised) > 0) {
			String msg = Labels.getLabel("OCR_DISB_AMOUNT_VALIDATION_MSG");
			MessageUtil.showError(msg);
			return false;
		}

		// rule for segmentations
		if (StringUtils.equals(this.ocrType.getSelectedItem().getValue(), PennantConstants.SEGMENTED_VALUE)) {
			BigDecimal cumCustSum = BigDecimal.ZERO;

			cumCustSum = getCumCustSum(financeDetail, this.tdTotalDemand.getActualValue());
			BigDecimal ocrPaid = BigDecimal.ZERO;
			ocrPaid = this.tdTotalReceivable.getActualValue();
			// Rule 1
			if (ocrPaid.compareTo(cumCustSum) < 0) {
				String msg = Labels.getLabel("OCR_NOT_SUFFICIENT_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}
			// Rule 2
			BigDecimal financierPortion = this.tdTotalDemand.getActualValue();
			BigDecimal customerPortion = this.tdTotalReceivable.getActualValue();
			if (disbAmount.add(customerPortion).compareTo(financierPortion) > 0) {
				String msg = Labels.getLabel("OCR_DISB_AMOUNT_CUM_VALIDATION_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}
		}

		// validation
		BigDecimal amountTobepaid = getCurrentTranchAmount(totalDemandAmt, custPortion);
		BigDecimal financieContribution = totalDemandAmt.subtract(amountTobepaid);

		if (StringUtils.equals(this.ocrType.getSelectedItem().getValue(), PennantConstants.PRORATA_VALUE)) {
			// Rule 1
			if (totalOcrPaid.compareTo(amountTobepaid) < 0) {
				String msg = Labels.getLabel("OCR_NOT_SUFFICIENT_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}

			// rule 2
			if (disbAmount.add(totalOcrPaid).compareTo(totalDemandAmt) > 0) {
				String msg = Labels.getLabel("OCR_DISB_AMOUNT_CUM_VALIDATION_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}
			// As per UD UD_OCR V1 7 not there these
			/*
			 * if (finAmount.compareTo(financieContribution) > 0) { String msg =
			 * Labels.getLabel("OCR_DISB_AMOUNT_VALIDATION", new String[] {
			 * Labels.getLabel("label_FinOCRDialog_FinancerContribution.value") }); if (MessageUtil.confirm(msg) ==
			 * MessageUtil.NO) { return false; } else { financeMain.setOcrDeviation(true); if (tab != null) {
			 * tab.setSelected(true); }
			 * 
			 * } }
			 */
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	private BigDecimal getDisbAmount(FinanceDetail financeDetail) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinAdvancePayments> finAdvancePayments = financeDetail.getAdvancePaymentsList();
		BigDecimal finAmount = BigDecimal.ZERO;
		List<FinanceMain> fmlist = null;
		// Current Loan disbursment amount
		if (CollectionUtils.isNotEmpty(finAdvancePayments)) {
			for (FinAdvancePayments disb : finAdvancePayments) {
				String status = disb.getStatus();
				if (!(DisbursementConstants.STATUS_REJECTED.equals(status)
						|| DisbursementConstants.STATUS_CANCEL.equals(status)
						|| DisbursementConstants.STATUS_REVERSED.equals(status))) {
					finAmount = finAmount.add(disb.getAmtToBeReleased());
				}
			}
		} else {
			finAmount.add(financeMain.getFinAmount());
		}

		// Parent Disbursement amounts
		if (StringUtils.isNotEmpty(financeMain.getParentRef())) {
			// Parent Disbursement amounts
			List<FinAdvancePayments> disbursementObject = getFinAdvancePaymentsObject(financeMain.getParentRef());
			if (disbursementObject != null) {
				for (FinAdvancePayments disb : disbursementObject) {
					finAmount = finAmount.add(disb.getAmtToBeReleased());
				}
			} else {
				List<FinanceMain> finmain = getFinanceMain(financeMain.getFinReference());
				if (CollectionUtils.isNotEmpty(finmain)) {
					finAmount.add(finmain.get(0).getFinAmount());
				}
			}

			// List of childs having parent of given FinReference
			fmlist = getFinanceMainObject(financeMain.getParentRef());
			for (FinanceMain financemain : fmlist) {
				if (StringUtils.equals(financemain.getFinReference(), financeMain.getFinReference())) {
					continue;
				}
				disbursementObject = getFinAdvancePaymentsObject(financemain.getFinReference());
				if (disbursementObject != null) {
					for (FinAdvancePayments disb : disbursementObject) {
						finAmount = finAmount.add(disb.getAmtToBeReleased());
					}
				} else {
					List<FinanceMain> finmain = getFinanceMain(financeMain.getFinReference());
					if (CollectionUtils.isNotEmpty(finmain)) {
						finAmount.add(finmain.get(0).getFinAmount());
					}
				}
			}

		} else {
			finAmount = calChildLoanDisbAmount(financeMain.getFinReference(), finAmount);
		}
		finAmount = PennantApplicationUtil.formateAmount(finAmount, ccyFormatter);

		return finAmount;
	}

	private BigDecimal calChildLoanDisbAmount(String finReference, BigDecimal disbAmount) {
		logger.debug(Literal.ENTERING);

		List<FinanceMain> financeMainList = getFinanceMainObject(finReference);
		if (CollectionUtils.isNotEmpty(financeMainList)) {
			for (FinanceMain financemain : financeMainList) {
				List<FinAdvancePayments> finAdvancePayments = getFinAdvancePaymentsObject(
						financemain.getFinReference());
				if (CollectionUtils.isNotEmpty(finAdvancePayments)) {
					for (FinAdvancePayments disb : finAdvancePayments) {
						disbAmount = disbAmount.add(disb.getAmtToBeReleased());
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return disbAmount;
	}

	private BigDecimal getCumCustSum(FinanceDetail financeDetail, BigDecimal demandAmt) {
		FinOCRHeader aFinOCRHeader = financeDetail.getFinOCRHeader();
		BigDecimal ocrtotalDemand = aFinOCRHeader.getTotalDemand();
		BigDecimal cumcustsum = BigDecimal.ZERO;
		BigDecimal cumfincsum = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal rem = BigDecimal.ZERO;
		boolean custr = false;
		boolean finc = false;
		// BigDecimal disbAmount = getDisbAmount(financeDetail);
		for (FinOCRDetail finOCRDetail : aFinOCRHeader.getOcrDetailList()) {
			if (finOCRDetail.getCustomerContribution().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal cust = getCurrentTranchAmount(ocrtotalDemand, finOCRDetail.getCustomerContribution());
				cumcustsum = cumcustsum.add(cust);
				total = total.add(cust);

				if (total.compareTo(demandAmt) > 0) {
					custr = true;
					rem = total.subtract(demandAmt);
					break;
				}
			}
			if (finOCRDetail.getFinancerContribution().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal fin = getCurrentTranchAmount(ocrtotalDemand, finOCRDetail.getFinancerContribution());
				cumfincsum = cumfincsum.add(fin);
				total = total.add(fin);

				if (total.compareTo(demandAmt) > 0) {
					finc = true;
					rem = total.subtract(demandAmt);
					break;
				}
			}
		}
		if (custr) {
			cumcustsum = cumcustsum.subtract(rem);

		}
		if (finc) {
			cumfincsum = cumfincsum.subtract(rem);
		}
		return cumcustsum;
	}

	private List<FinAdvancePayments> getFinAdvancePaymentsObject(String parentRef) {
		String[] status = new String[3];
		status[0] = DisbursementConstants.STATUS_REJECTED;
		status[1] = DisbursementConstants.STATUS_CANCEL;
		status[2] = DisbursementConstants.STATUS_REVERSED;

		Search search = new Search(FinAdvancePayments.class);
		search.addField("AmtToBeReleased");
		search.addTabelName("FinAdvancePayments_view");
		search.addFilter(new Filter("FinReference", parentRef, Filter.OP_EQUAL));
		search.addFilter(new Filter("Status", status, Filter.OP_NOT_IN));
		List<FinAdvancePayments> list = searchProcessor.getResults(search);
		return list;
	}

	private List<FinanceMain> getFinanceMainObject(String parentRef) {
		logger.debug(Literal.LEAVING);
		Search search = new Search(FinanceMain.class);
		search.addField("FinAmount");
		search.addField("FinID");
		search.addField("FinReference");
		search.addTabelName("FinanceMain_view");
		search.addFilter(new Filter("ParentRef", parentRef, Filter.OP_EQUAL));
		List<FinanceMain> list = searchProcessor.getResults(search);
		return list;
	}

	private List<FinanceMain> getFinanceMain(String finReference) {
		logger.debug(Literal.LEAVING);
		Search search = new Search(FinanceMain.class);
		search.addField("FinAmount");
		search.addTabelName("FinanceMain_view");
		search.addFilter(new Filter("finReference", finReference, Filter.OP_EQUAL));
		List<FinanceMain> list = searchProcessor.getResults(search);
		return list;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @return
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(FinOCRHeader aFinOCRHeader) {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinOCRHeader.setOcrID(this.ocrID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setOcrDescription(this.ocrDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setCustomerPortion(this.customerPortion.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setTotalDemand(this.totalDemand.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setTotalReceivable(this.totalReceivable.getActualValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.ocrType))) {
				if (!this.ocrType.isDisabled()) {
					throw new WrongValueException(this.ocrType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinOCRDialog_OCRApplicableOn.value") }));
				}
			} else {
				aFinOCRHeader.setOcrType(getComboboxValue(this.ocrType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// aFinOCRHeader.setSplitApplicable(this.splitApplicable.isChecked());
		// Fin OCR Step Details
		aFinOCRHeader.setOcrDetailList(getFinOCRDetailList());

		// Fin OCR Capture Details
		aFinOCRHeader.setFinOCRCapturesList(getFinOCRCaptureList());
		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		if (!this.ocrID.isReadonly()) {
			this.ocrID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinOCRDialog_OCRID.value"), null, true));
		}

		if (!this.ocrDescription.isReadonly()) {
			this.ocrDescription.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinOCRDialog_OCRDescription.value"), null, true, false));
		}

		if (!this.customerPortion.isReadonly()) {
			this.customerPortion
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinOCRDialog_CustomerPortion.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0, 100));
		}

		if (!this.totalDemand.isReadonly()) {
			this.totalDemand
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinOCRDialog_Totaldemand.value"),
							finFormatter, true, false, 1, Double.MAX_VALUE));
		}

		if (!this.totalReceivable.isReadonly()) {
			this.totalDemand.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinOCRDialog_TotalOCRReceivable.value"), null, true, false));
		}

		if (!this.totalDemand.isReadonly()) {
			this.totalDemand
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinOCRDialog_Totaldemand.value"),
							finFormatter, true, false, 1, Double.MAX_VALUE));
		}

		if (!this.totalReceivable.isReadonly()) {
			this.totalDemand.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinOCRDialog_TotalOCRReceivable.value"), null, true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(List<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (!wve.isEmpty()) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			if (tab != null) {
				tab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];

			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}

			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.ocrID.setConstraint("");
		this.ocrDescription.setConstraint("");
		this.customerPortion.setConstraint("");
		this.ocrType.setConstraint("");
		logger.debug(Literal.LEAVING);

	}

	private FinOCRHeader copyOCRHeaderProperties(OCRHeader ocrHeader, FinOCRHeader finOCRHeader) {
		FinOCRHeader newFinOCRHeader = new FinOCRHeader();
		newFinOCRHeader.setNewRecord(true);
		List<FinOCRDetail> finOCRDetailList = new ArrayList<>();
		if (ocrHeader != null) {
			newFinOCRHeader.setOcrType(ocrHeader.getOcrType());
			newFinOCRHeader.setOcrID(ocrHeader.getOcrID());
			newFinOCRHeader.setOcrDescription(ocrHeader.getOcrDescription());
			newFinOCRHeader.setCustomerPortion(ocrHeader.getCustomerPortion());
			// finOCRHeader.setSplitApplicable(ocrHeader.isSplitApplicable());
			if (StringUtils.isBlank(newFinOCRHeader.getRecordType())) {
				newFinOCRHeader.setVersion(newFinOCRHeader.getVersion() + 1);
				newFinOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}

			// setting the work flow values for
			if (StringUtils.isNotEmpty(parentRef) && finOCRHeader != null) {
				List<FinOCRDetail> finOCRDetails = finOCRHeader.getOcrDetailList();
				if (CollectionUtils.isNotEmpty(finOCRDetails)) {
					for (FinOCRDetail ocrDetail : finOCRDetails) {
						FinOCRDetail finOCRDetail = new FinOCRDetail();
						finOCRDetail.setStepSequence(ocrDetail.getStepSequence());
						finOCRDetail.setContributor(ocrDetail.getContributor());
						finOCRDetail.setCustomerContribution(ocrDetail.getCustomerContribution());
						finOCRDetail.setFinancerContribution(ocrDetail.getFinancerContribution());
						finOCRDetail.setNewRecord(true);
						finOCRDetailList.add(finOCRDetail);
					}
					newFinOCRHeader.setOcrDetailList(finOCRDetailList);
				}
			} else {
				if (getFinOCRHeader() != null && CollectionUtils.isEmpty(getFinOCRHeader().getOcrDetailList())) {
					if (!CollectionUtils.isEmpty(ocrHeader.getOcrDetailList())) {
						for (OCRDetail ocrDetail : ocrHeader.getOcrDetailList()) {
							FinOCRDetail finOCRDetail = new FinOCRDetail();
							finOCRDetail.setStepSequence(ocrDetail.getStepSequence());
							finOCRDetail.setContributor(ocrDetail.getContributor());
							finOCRDetail.setCustomerContribution(ocrDetail.getCustomerContribution());
							finOCRDetail.setFinancerContribution(ocrDetail.getFinancerContribution());
							finOCRDetail.setNewRecord(true);
							if (StringUtils.isBlank(finOCRDetail.getRecordType())) {
								finOCRDetail.setVersion(finOCRDetail.getVersion() + 1);
								finOCRDetail.setRecordType(PennantConstants.RCD_ADD);
							}
							finOCRDetailList.add(finOCRDetail);
						}
					}
					newFinOCRHeader.setOcrDetailList(finOCRDetailList);
				} else if (CollectionUtils.isNotEmpty(ocrHeader.getOcrDetailList()) && getFinOCRHeader() != null
						&& CollectionUtils.isNotEmpty(getFinOCRHeader().getOcrDetailList())) {
					newFinOCRHeader.setOcrDetailList(getFinOCRHeader().getOcrDetailList());
				}
			}
		}
		return newFinOCRHeader;

	}

	/**
	 * Called when changing the value of the text box
	 * 
	 * @param event
	 */
	public void onValueChange$totalDemand(Event event) {
		logger.trace(Literal.ENTERING);
		// this.totalReceivable.setValue((totalDemand.getValue()*customerPortion.getValue())/100);
		this.totalReceivable
				.setValue(getCurrentTranchAmount(this.totalDemand.getValidateValue(), this.customerPortion.getValue()));
		logger.trace(Literal.LEAVING);
	}

	/**
	 * Called when changing the value of the text box
	 * 
	 * @param event
	 */
	public void onFulfill$totalDemand(Event event) {
		logger.trace(Literal.ENTERING);
		// this.totalReceivable.setValue((totalDemand.getValue()*customerPortion.getValue())/100);
		this.totalReceivable
				.setValue(getCurrentTranchAmount(this.totalDemand.getActualValue(), this.customerPortion.getValue()));
		logger.trace(Literal.LEAVING);
	}

	/**
	 * This method will set the data from FinOCRHeader bean to components
	 * 
	 * @param event
	 */
	public void onFulfill$ocrID(Event event) {
		logger.debug(Literal.ENTERING);
		OCRHeader ocrHeader = (OCRHeader) this.ocrID.getObject();
		FinOCRHeader finOCRHeader = new FinOCRHeader();
		if (ocrHeader != null) {
			setFinOCRHeader(finOCRHeader);
			// render the Master OCR details in loan queue
			ocrHeader = ocrHeaderService.getApprovedOCRHeader(ocrHeader.getHeaderID());
			setFinOCRHeader(copyOCRHeaderProperties(ocrHeader, null));
			doWriteBeanToComponents(getFinOCRHeader());
		} else {
			doWriteBeanToComponents(finOCRHeader);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will render the ocr steps
	 * 
	 * @param finOCRDetailList
	 */
	public void doFillFinOCRStepDetails(List<FinOCRDetail> finOCRDetailList) {
		logger.debug(Literal.ENTERING);
		BigDecimal totalCust = BigDecimal.ZERO;
		BigDecimal totalFin = BigDecimal.ZERO;
		this.listBoxFinOCRSteps.getItems().clear();
		setFinOCRDetailList(finOCRDetailList);
		if (CollectionUtils.isNotEmpty(finOCRDetailList)) {
			Collections.sort(finOCRDetailList);
			for (FinOCRDetail detail : finOCRDetailList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(String.valueOf(detail.getStepSequence()));
				lc.setParent(item);
				// skipping the cancel and DELETE records
				if (!PennantConstants.RCD_DEL.equalsIgnoreCase(detail.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(detail.getRecordType())) {
					totalCust = totalCust.add(detail.getCustomerContribution());
					totalFin = totalFin.add(detail.getFinancerContribution());
				}

				String custContribution = "--";
				if (detail.getCustomerContribution().compareTo(BigDecimal.ZERO) > 0) {
					custContribution = String.valueOf(detail.getCustomerContribution()).concat("%");
				}
				lc = new Listcell(custContribution);
				lc.setParent(item);

				String finContribution = "--";
				if (detail.getFinancerContribution().compareTo(BigDecimal.ZERO) > 0) {
					finContribution = String.valueOf(detail.getFinancerContribution()).concat("%");
				}

				lc = new Listcell(finContribution);
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordStatus()));
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);

				item.setAttribute("data", detail);
				if (StringUtils.isEmpty(parentRef)) {
					ComponentsCtrl.applyForward(item, "onDoubleClick=onFinOCRStepItemDoubleClicked");
				}
				this.listBoxFinOCRSteps.appendChild(item);
			}
			// group total
			if (listBoxFinOCRSteps != null && listBoxFinOCRSteps.getItems().size() > 0) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(Labels.getLabel("listheader_AdvancePayments_GrandTotal.label"));
				lc.setStyle("font-weight:bold");
				lc.setParent(item);

				lc = new Listcell(String.valueOf(totalCust).concat("%"));
				lc.setParent(item);

				lc = new Listcell(String.valueOf(totalFin).concat("%"));
				lc.setParent(item);

				lc = new Listcell();
				lc.setParent(item);

				lc = new Listcell();
				lc.setParent(item);
				this.listBoxFinOCRSteps.appendChild(item);
			}
			this.customerPortion.setValue(totalCust);
			if (totalDemand.getActualValue().compareTo(BigDecimal.ZERO) > 0
					&& customerPortion.getValue().compareTo(BigDecimal.ZERO) > 0) {
				this.totalReceivable.setValue(
						getCurrentTranchAmount(this.totalDemand.getValidateValue(), this.customerPortion.getValue()));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	Predicate<String> predicate = finReferece -> financeDetailService.isFinReferenceExits(finReferece,
			TableType.MAIN_TAB.getSuffix(), false);

	/**
	 * This method will redirect to FinOCRCapture Dialog while click on new button
	 */
	public void onClick$btnNew_FinOCRCapture(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRCapture);
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (StringUtils.isNotBlank(financeMain.getParentRef())) {
			if (predicate.test(financeMain.getParentRef())) {
				FinOCRHeader finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(financeMain.getFinID(),
						TableType.TEMP_TAB.getSuffix());
				if (finOCRHeader != null) {
					MessageUtil.showError(Labels.getLabel("label_FinOCRCapture_OCRMaintenance.value"));
					return;
				}
			}
		}
		final FinOCRCapture aFinOCRCapture = new FinOCRCapture();
		aFinOCRCapture.setNewRecord(true);
		aFinOCRCapture.setWorkflowId(0);
		financeDetail = getFinancedetailsFromBase();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finOCRCapture", aFinOCRCapture);
		map.put("financeDetail", financeDetail);
		map.put("finOCRHeader", getFinOCRHeaderData());
		if (isWorkFlowEnabled()) {
			map.put("roleCode", getRole());
		} else {
			map.put("roleCode", roleCode);
		}
		map.put("allowedRoles", allowedRoles);
		map.put("ccyFormatter", ccyFormatter);
		map.put("finOCRDialogCtrl", this);
		// call the ZUL-file with the parameters packed in a map try {
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRCaptureDialog.zul", null, map);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private FinOCRHeader getFinOCRHeaderData() {
		if (finOCRHeader != null) {
			finOCRHeader.setCustomerPortion(this.customerPortion.getValue());
		}
		return finOCRHeader;

	}

	/**
	 * Validations for OCR Steps on ocrType change event
	 */
	private void doCheckFinOCRStepDetails() {
		if (StringUtils.equals(this.ocrType.getSelectedItem().getValue(), PennantConstants.SEGMENTED_VALUE)) {
			this.ocrStepsDiv.setVisible(true);
			this.listBoxFinOCRSteps.setVisible(true);
		} else {
			if (doCheckOCRStepsDeleted()) {
				MessageUtil.showError(Labels.getLabel("label_FinOCRDialog_error"));
				// this condition is for switching segmented to prorata
				fillComboBox(this.ocrType, PennantConstants.SEGMENTED_VALUE, applicableList, "");
				return;
			}
			this.ocrStepsDiv.setVisible(false);
			this.listBoxFinOCRSteps.setVisible(false);
		}
	}

	private boolean doCheckOCRStepsDeleted() {
		if (!CollectionUtils.isEmpty(getFinOCRDetailList())) {
			for (FinOCRDetail finOCRDetail : getFinOCRDetailList()) {
				// skipping the cancel and DELETE records
				if (!PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(finOCRDetail.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finOCRDetail.getRecordType())) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * onCheck Event For ocrType Combobox
	 */
	public void onSelect$ocrType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCheckFinOCRStepDetails();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public static boolean isDeleteRecord(AbstractWorkflowEntity abstractWorkflowEntity) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, abstractWorkflowEntity.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, abstractWorkflowEntity.getRecordType())) {
			return true;
		}
		return false;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		String parentRef = "";

		if (getFinanceDetail() != null) {
			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			parentRef = financeMain.getParentRef();
		}

		if (StringUtils.isNotEmpty(parentRef)) {
			this.ocrID.setReadonly(true);
		} else {
			if (finOCRHeader.isNewRecord()) {
				this.ocrID.setReadonly(false);
			} else {
				this.ocrID.setReadonly(true);
			}
		}

		this.ocrDescription.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_ocrDescription"));
		this.customerPortion.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_customerPortion"));
		this.totalDemand.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_TotalDemand"));
		this.totalReceivable.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_TotalReceivable"));
		this.ocrType.setDisabled(!getUserWorkspace().isAllowed("FinOCRDialog_OCRType"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finOCRHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will redirect to FinOCRStep Dialog while click on new button
	 */
	public void onClick$btnNew_FinOCRStep(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRStep);
		// with out selecting ocr header
		if (StringUtils.isEmpty(this.finOCRHeader.getOcrID())) {
			doWriteComponentsToBean(this.finOCRHeader);
		}
		final FinOCRHeader aFinOCRHeader = new FinOCRHeader();
		BeanUtils.copyProperties(this.finOCRHeader, aFinOCRHeader);
		doWriteComponentsToBean(aFinOCRHeader);
		final FinOCRDetail aFinOCRDetail = new FinOCRDetail();
		aFinOCRDetail.setNewRecord(true);
		aFinOCRDetail.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finOCRDetail", aFinOCRDetail);
		map.put("finOCRHeader", aFinOCRHeader);
		if (isWorkFlowEnabled()) {
			map.put("roleCode", getRole());
		} else {
			map.put("roleCode", roleCode);
		}
		map.put("finOCRDialogCtrl", this);
		map.put("allowedRoles", allowedRoles);

		// call the ZUL-file with the parameters packed in a map try {
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRStepDialog.zul", null, map);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private FinanceDetail getFinancedetailsFromBase() {
		try {
			if (financeMainDialogCtrl != null) {
				return (FinanceDetail) financeMainDialogCtrl.getClass().getMethod("getFinanceDetail")
						.invoke(financeMainDialogCtrl);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return financeDetail;

	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public FinOCRHeader getFinOCRHeader() {
		return finOCRHeader;
	}

	public void setFinOCRHeader(FinOCRHeader finOCRHeader) {
		this.finOCRHeader = finOCRHeader;
	}

	public OCRHeaderService getOcrHeaderService() {
		return ocrHeaderService;
	}

	public void setOcrHeaderService(OCRHeaderService ocrHeaderService) {
		this.ocrHeaderService = ocrHeaderService;
	}

	public FinOCRHeaderService getFinOCRHeaderService() {
		return finOCRHeaderService;
	}

	public void setFinOCRHeaderService(FinOCRHeaderService finOCRHeaderService) {
		this.finOCRHeaderService = finOCRHeaderService;
	}

	public List<FinOCRDetail> getFinOCRDetailList() {
		return finOCRDetailList;
	}

	public void setFinOCRDetailList(List<FinOCRDetail> finOCRDetailList) {
		this.finOCRDetailList = finOCRDetailList;
	}

	public List<FinOCRCapture> getFinOCRCaptureList() {
		return finOCRCaptureList;
	}

	public void setFinOCRCaptureList(List<FinOCRCapture> finOCRCaptureList) {
		this.finOCRCaptureList = finOCRCaptureList;
	}

	public void doFillFinOCRCaptureDetails(List<FinOCRCapture> finOCRCaptureList) {
		logger.debug(Literal.ENTERING);

		this.listBoxOCRCapture.getItems().clear();
		setFinOCRCaptureList(finOCRCaptureList);

		BigDecimal ocrTotReceiveble = BigDecimal.ZERO;
		BigDecimal ocrTotalDemand = BigDecimal.ZERO;
		BigDecimal ocrTotalpaid = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(finOCRCaptureList)) {
			Collections.sort(finOCRCaptureList,
					(ocrCapture1, ocrCapture2) -> ocrCapture1.getDisbSeq() > ocrCapture2.getDisbSeq() ? 1
							: ocrCapture1.getDisbSeq() < ocrCapture2.getDisbSeq() ? -1 : 0);

			for (FinOCRCapture detail : finOCRCaptureList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(String.valueOf(detail.getDisbSeq()));// 1
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getDemandAmount(), ccyFormatter));// 2
				lc.setParent(item);
				ocrTotalDemand = ocrTotalDemand.add(detail.getDemandAmount());
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getPaidAmount(), ccyFormatter));
				lc.setParent(item);
				ocrTotalpaid = ocrTotalpaid.add(detail.getPaidAmount());

				lc = new Listcell(DateUtil.format(detail.getReceiptDate(), DateFormat.SHORT_DATE));
				lc.setParent(item);

				lc = new Listcell(detail.getRemarks());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordStatus()));
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);

				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinOCRCaptureItemDoubleClicked");
				this.listBoxOCRCapture.appendChild(item);
			}

		}
		this.ocrTotalDemand.setValue(formate(ocrTotalDemand));
		this.ocrTotalPaid.setValue(formate(ocrTotalpaid));

		if (this.totalDemand.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			financeDetail.getFinOCRHeader().setTotalDemand(totalDemand.getActualValue());
		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (StringUtils.equals(financeDetail.getFinOCRHeader().getOcrType(), PennantConstants.SEGMENTED_VALUE)) {

			if (financeMain != null && StringUtils.isNotEmpty(financeMain.getParentRef())) {
				List<FinanceMain> parentFinanceMainList = getFinanceMainObject(financeMain.getParentRef());
				ocrAmountCalculationForSegemnt(ocrTotalDemand, ocrTotalpaid, financeMain, parentFinanceMainList);
			} else if (financeMain != null && StringUtils.isEmpty(financeMain.getParentRef())) {
				List<FinanceMain> childFinanceMainList = getFinanceMainObject(financeMain.getFinReference());
				if (CollectionUtils.isNotEmpty(childFinanceMainList)) {
					ocrAmountCalculationForSegemnt(ocrTotalDemand, ocrTotalpaid, financeMain, childFinanceMainList);
				} else {
					// this.ocrCprTotReceivble.setValue(getCumCustSum(financeDetail, formate(ocrTotalDemand)));
					consumer.accept(finOCRCaptureList);
				}
			}
			this.ocrCprTotReceivble
					.setValue(this.totalReceivable.getActualValue().subtract(this.tdTotalReceivable.getActualValue()));
		} else {
			ocrTotReceiveble = ocrTotReceiveble
					.add(getCurrentTranchAmount(ocrTotalDemand, financeDetail.getFinOCRHeader().getCustomerPortion()));
			// this.ocrCprTotReceivble.setValue(formate(ocrTotReceiveble));

			if (financeMain != null && StringUtils.isNotEmpty(financeMain.getParentRef())) {
				List<FinanceMain> parentFinanceMainList = getFinanceMainObject(financeMain.getParentRef());
				ocrAmountCalculationForProrata(ocrTotalDemand, ocrTotalpaid, financeMain, parentFinanceMainList);
			} else if (financeMain != null && StringUtils.isNotEmpty(financeMain.getFinReference())) {
				List<FinanceMain> childFinanceMainList = getFinanceMainObject(financeMain.getFinReference());
				if (CollectionUtils.isNotEmpty(childFinanceMainList)) {
					ocrAmountCalculationForProrata(ocrTotalDemand, ocrTotalpaid, financeMain, childFinanceMainList);
				} else {
					consumer.accept(finOCRCaptureList);
				}
			}
			this.ocrCprTotReceivble
					.setValue(this.totalReceivable.getActualValue().subtract(this.tdTotalReceivable.getActualValue()));
		}
	}

	private void ocrAmountCalculationForProrata(BigDecimal ocrTotalDemand, BigDecimal ocrTotalpaid,
			FinanceMain financeMain, List<FinanceMain> financeMainList) {

		FinOCRHeader finOCRHeader = null;
		if (StringUtils.isNotBlank(financeMain.getParentRef())) {
			finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(financeMain.getFinID(),
					TableType.VIEW.getSuffix());
		}
		BigDecimal parentocrpaid = BigDecimal.ZERO;
		BigDecimal parentdemandAmt = BigDecimal.ZERO;
		BigDecimal tdOCRPaid = BigDecimal.ZERO;

		if (finOCRHeader != null && CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
			for (FinOCRCapture cpr : finOCRHeader.getFinOCRCapturesList()) {
				parentocrpaid = parentocrpaid.add(cpr.getPaidAmount());
				parentdemandAmt = parentdemandAmt.add(cpr.getDemandAmount());
			}
		}

		if (CollectionUtils.isNotEmpty(financeMainList)) {
			for (FinanceMain childFinance : financeMainList) {
				if (StringUtils.equals(childFinance.getFinReference(), financeMain.getFinReference())) {
					continue;
				}
				finOCRHeader = null;
				finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(childFinance.getFinID(),
						TableType.VIEW.getSuffix());
				if (finOCRHeader != null) {
					for (FinOCRCapture cpr : finOCRHeader.getFinOCRCapturesList()) {
						parentocrpaid = parentocrpaid.add(cpr.getPaidAmount());
						parentdemandAmt = parentdemandAmt.add(cpr.getDemandAmount());
					}
				}
			}
		}
		parentocrpaid = formate(parentocrpaid);
		parentdemandAmt = formate(parentdemandAmt);
		ocrTotalDemand = formate(ocrTotalDemand);
		ocrTotalDemand = ocrTotalDemand.add(parentdemandAmt);
		tdOCRPaid = parentocrpaid.add(formate(ocrTotalpaid));

		this.tdTotalDemand.setValue(ocrTotalDemand);
		this.tdTotalReceivable.setValue(tdOCRPaid);
	}

	private void ocrAmountCalculationForSegemnt(BigDecimal ocrTotalDemand, BigDecimal ocrTotalpaid,
			FinanceMain financeMain, List<FinanceMain> financeMainList) {
		FinOCRHeader finOCRHeader = null;

		if (StringUtils.isNotBlank(financeMain.getParentRef())) {
			finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(financeMain.getFinID(),
					TableType.VIEW.getSuffix());
		}
		BigDecimal parentocrpaid = BigDecimal.ZERO;
		BigDecimal parentdemandAmt = BigDecimal.ZERO;
		BigDecimal parentfinancierpaid = BigDecimal.ZERO;
		BigDecimal cumcustsum = BigDecimal.ZERO;
		BigDecimal cumfincsum = BigDecimal.ZERO;
		BigDecimal tdOCRPaid = BigDecimal.ZERO;

		if (finOCRHeader != null && CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
			for (FinOCRCapture cpr : finOCRHeader.getFinOCRCapturesList()) {
				parentocrpaid = parentocrpaid.add(cpr.getPaidAmount());
				parentdemandAmt = parentdemandAmt.add(cpr.getDemandAmount());
			}
		}

		if (CollectionUtils.isNotEmpty(financeMainList)) {
			for (FinanceMain childFinance : financeMainList) {
				if (StringUtils.equals(childFinance.getFinReference(), financeMain.getFinReference())) {
					continue;
				}
				finOCRHeader = null;
				finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(childFinance.getFinID(),
						TableType.VIEW.getSuffix());
				if (finOCRHeader != null) {
					for (FinOCRCapture cpr : finOCRHeader.getFinOCRCapturesList()) {
						parentocrpaid = parentocrpaid.add(cpr.getPaidAmount());
						parentdemandAmt = parentdemandAmt.add(cpr.getDemandAmount());
					}
				}
			}
		}

		parentocrpaid = formate(parentocrpaid);
		parentdemandAmt = formate(parentdemandAmt);
		parentfinancierpaid = parentdemandAmt.subtract(parentocrpaid);
		ocrTotalDemand = formate(ocrTotalDemand);
		ocrTotalDemand = ocrTotalDemand.add(parentdemandAmt);
		cumcustsum = getCumCustSum(financeDetail, ocrTotalDemand);
		cumfincsum = ocrTotalDemand.subtract(cumcustsum);
		cumcustsum = cumcustsum.subtract(parentocrpaid);
		tdOCRPaid = parentocrpaid.add(formate(ocrTotalpaid));
		parentocrpaid = parentocrpaid.add(cumcustsum);
		cumfincsum = cumfincsum.subtract(parentfinancierpaid);
		parentfinancierpaid = parentfinancierpaid.add(cumfincsum);

		// this.ocrCprTotReceivble.setValue(cumcustsum);
		this.tdTotalDemand.setValue(parentfinancierpaid.add(parentocrpaid));
		this.tdTotalReceivable.setValue(tdOCRPaid);
	}

	Consumer<List<FinOCRCapture>> consumer = finOCRCaptureList -> {
		if (CollectionUtils.isNotEmpty(finOCRCaptureList)) {
			BigDecimal ocrPaid = BigDecimal.ZERO;
			BigDecimal ocrDemand = BigDecimal.ZERO;
			for (FinOCRCapture cpr : finOCRCaptureList) {
				if (cpr.getPaidAmount() != null) {
					ocrPaid = ocrPaid.add(cpr.getPaidAmount());
				}
				if (cpr.getDemandAmount() != null) {
					ocrDemand = ocrDemand.add(cpr.getDemandAmount());
				}
			}
			this.tdTotalDemand.setValue(PennantApplicationUtil.formateAmount(ocrDemand, ccyFormatter));
			this.tdTotalReceivable.setValue(PennantApplicationUtil.formateAmount(ocrPaid, ccyFormatter));
		}
	};

	private BigDecimal getCurrentTranchAmount(BigDecimal demand, BigDecimal customerPortion) {
		BigDecimal amount = BigDecimal.ZERO;
		if (finOCRHeader != null) {
			amount = demand.multiply(customerPortion).divide(new BigDecimal(100), ccyFormatter, RoundingMode.HALF_DOWN);
		}
		return amount;
	}

	private BigDecimal formate(BigDecimal amt) {
		return PennantApplicationUtil.formateAmount(amt, ccyFormatter);
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}
