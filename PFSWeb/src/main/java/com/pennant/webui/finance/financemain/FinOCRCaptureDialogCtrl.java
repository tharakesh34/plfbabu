package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinOCRCaptureDialogCtrl extends GFCBaseCtrl<FinOCRCapture> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinOCRCaptureDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window windowFinOCRCaptureDialog;
	protected Textbox loanReference;
	protected Intbox disbursementSequence;
	// protected Textbox ocrRecCurTranche;
	protected CurrencyBox builderDemand;
	protected CurrencyBox ocrPaid;
	protected Datebox ocrReceiptDate;
	protected Textbox remarks;
	protected Label fileUpload; // autoWired
	protected Button btnUpload; // autoWired
	protected Button btnUploadView;
	protected Textbox uploadedfileName;
	private FinOCRCapture finOCRCapture;
	private FinOCRDialogCtrl finOCRDialogCtrl;
	private transient boolean fromParent;
	private List<FinOCRCapture> finOCRCaptureList = new ArrayList<FinOCRCapture>();
	private String roleCode;
	private FinanceDetail financeDetail;
	List<FinanceDisbursement> financeDisbursement = new ArrayList<>();
	private List<FinanceDisbursement> approvedDisbursments;
	private FinOCRHeaderService finOCRHeaderService;
	private int ccyFormatter = 0;
	private FinOCRHeader finOCRHeader;
	protected Decimalbox disbDateAmount;
	private Media media;
	private byte[] docbyte;
	Date appDate = SysParamUtil.getAppDate();

	/**
	 * default constructor.<br>
	 */
	public FinOCRCaptureDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinOCRCaptureDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$windowFinOCRCaptureDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(windowFinOCRCaptureDialog);

		try {
			// Get the required arguments.
			this.finOCRCapture = (FinOCRCapture) arguments.get("finOCRCapture");

			if (this.finOCRCapture == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("finOCRDialogCtrl")) {
				this.finOCRDialogCtrl = (FinOCRDialogCtrl) arguments.get("finOCRDialogCtrl");
				setFromParent(true);
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}
			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				if (financeDetail != null) {
					this.financeDisbursement = financeDetail.getFinScheduleData().getDisbursementDetails();
				}
			}

			if (arguments.containsKey("approvedDisbursments")) {
				approvedDisbursments = (List<FinanceDisbursement>) arguments.get("approvedDisbursments");
			}

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormatter = (Integer) arguments.get("ccyFormatter");
			}

			if (arguments.containsKey("finOCRHeader")) {
				finOCRHeader = (FinOCRHeader) arguments.get("finOCRHeader");
			}
			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}
			// Store the before image.
			FinOCRCapture finOCRCapture = new FinOCRCapture();
			BeanUtils.copyProperties(this.finOCRCapture, finOCRCapture);
			this.finOCRCapture.setBefImage(finOCRCapture);

			// Render the page and display the data.
			doLoadWorkFlow(this.finOCRCapture.isWorkflow(), this.finOCRCapture.getWorkflowId(),
					this.finOCRCapture.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			} else if (!enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finOCRCapture);
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
		this.builderDemand.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.builderDemand.setScale(ccyFormatter);
		this.builderDemand.setTextBoxWidth(150);
		this.builderDemand.setMandatory(true);
		this.ocrPaid.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.ocrPaid.setScale(ccyFormatter);
		this.ocrPaid.setTextBoxWidth(150);
		this.ocrPaid.setMandatory(true);
		this.remarks.setMaxlength(500);
		this.disbursementSequence.setDisabled(true);
		this.ocrReceiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinOCRCaptureDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinOCRCaptureDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinOCRCaptureDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinOCRCaptureDialog_btnSave"));
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
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
		doShowNotes(this.finOCRCapture);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.finOCRCapture.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onUpload$btnUpload(UploadEvent event) {
		logger.debug("Entering" + event.toString());

		media = event.getMedia();

		if (!MediaUtil.isPdf(media) && !MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "pdf or excel" }));
			return;
		}

		if (media.getName().length() > 100) {
			throw new WrongValueException(this.uploadedfileName, Labels.getLabel("label_Filename_length_File"));
		} else {
			this.uploadedfileName.setValue(media.getName());
			this.docbyte = media.getByteData();
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnUploadView(Event event) {
		logger.debug(Literal.ENTERING);

		byte[] docImage = this.finOCRCapture.getDocImage();
		Long documentRef = this.finOCRCapture.getDocumentRef();
		if (docImage == null && documentRef != null && documentRef > 0) {
			this.finOCRCapture.setDocImage(finOCRHeaderService.getDocumentManImage(documentRef));
		}

		if (this.finOCRCapture.getDocImage() != null) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finOCRCapture", this.finOCRCapture);
			Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
		}
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param afinOCRCapture
	 * 
	 */
	public void doWriteBeanToComponents(FinOCRCapture afinOCRCapture) {
		logger.debug(Literal.ENTERING);
		if (financeDetail != null) {
			int seq = getDisbursementSequence(afinOCRCapture);
			if (financeDisbursement.size() == 1 && afinOCRCapture.isNewRecord()) {
				// seq = financeDisbursement.get(0).getDisbSeq();
			}

			this.disbursementSequence.setValue(seq);
			setDisbursmentAmount();
			if (afinOCRCapture.isNewRecord()) {
				this.loanReference.setValue(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			} else {
				this.loanReference.setValue(afinOCRCapture.getFinReference());
			}
		}
		this.builderDemand
				.setValue(PennantApplicationUtil.formateAmount(afinOCRCapture.getDemandAmount(), ccyFormatter));
		this.ocrPaid.setValue(PennantApplicationUtil.formateAmount(afinOCRCapture.getPaidAmount(), ccyFormatter));
		this.remarks.setValue(afinOCRCapture.getRemarks());
		if (afinOCRCapture.getReceiptDate() == null) {
			afinOCRCapture.setReceiptDate(appDate);
		}
		this.ocrReceiptDate.setValue(afinOCRCapture.getReceiptDate());
		this.uploadedfileName.setValue(afinOCRCapture.getFileName());
		setDocbyte(afinOCRCapture.getDocImage());
		if (finOCRHeader != null) {
			BigDecimal percentage = finOCRHeader.getCustomerPortion();
			getCurrentTranchAmount(afinOCRCapture.getDemandAmount(), percentage);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$builderDemand() {
		BigDecimal demand = this.builderDemand.getActualValue();
		demand = PennantApplicationUtil.unFormateAmount(demand, ccyFormatter);
		if (finOCRHeader != null) {
			BigDecimal percentage = finOCRHeader.getCustomerPortion();
			getCurrentTranchAmount(demand, percentage);
		}
	}

	private BigDecimal getCurrentTranchAmount(BigDecimal demand, BigDecimal customerPortion) {
		BigDecimal amout = BigDecimal.ZERO;
		if (finOCRHeader != null) {

			amout = demand.multiply(customerPortion.divide(new BigDecimal(100), ccyFormatter, RoundingMode.HALF_DOWN));
		}
		return amout;
	}

	/**
	 * Method to fill the combobox with given list of values and will exclude the the values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBox(Combobox combobox, int seq, List<FinanceDisbursement> list, boolean execuledApprove) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (FinanceDisbursement disbursement : list) {
			if (execuledApprove && isContainsInAppList(disbursement)) {
				continue;
			}
			// cancelled disbursement should not be allowed to process
			if (StringUtils.trimToEmpty(disbursement.getDisbStatus()).equals(FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}

			comboitem = new Comboitem();
			String label = DateUtil.formatToLongDate(disbursement.getDisbDate());
			label = label.concat(" , ") + disbursement.getDisbSeq();
			comboitem.setLabel(label);
			comboitem.setValue(disbursement.getDisbDate());
			comboitem.setAttribute("data", disbursement);
			combobox.appendChild(comboitem);
			if (seq == disbursement.getDisbSeq()) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	private boolean isContainsInAppList(FinanceDisbursement disbursement) {
		if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : approvedDisbursments) {
				if (disbursement.getDisbDate().getTime() == financeDisbursement.getDisbDate().getTime()
						&& disbursement.getDisbSeq() == financeDisbursement.getDisbSeq()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param afinOCRCapture
	 */
	public void doWriteComponentsToBean(FinOCRCapture afinOCRCapture) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Loan Reference
		try {
			afinOCRCapture.setFinReference(this.loanReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Disbursement Seq
		try {

			afinOCRCapture.setDisbSeq(this.disbursementSequence.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Builder Demand
		try {
			BigDecimal demand = this.builderDemand.getActualValue();
			if (demand.compareTo(BigDecimal.ZERO) < 0) {
				throw new WrongValueException(this.builderDemand, Labels.getLabel("NUMBER_MINVALUE_EQ",
						new String[] { Labels.getLabel("label_FinOCRCaptureDialog_BuilderDemand.value"), "zero" }));
			}
			afinOCRCapture.setDemandAmount(PennantApplicationUtil.unFormateAmount(demand, ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// OCR Paid
		try {
			BigDecimal ocrPaid = this.ocrPaid.getActualValue();
			if (ocrPaid.compareTo(BigDecimal.ZERO) < 0) {
				throw new WrongValueException(this.builderDemand, Labels.getLabel("NUMBER_MINVALUE_EQ",
						new String[] { Labels.getLabel("label_FinOCRCaptureDialog_OCRpaid.value"), "zero" }));
			}
			afinOCRCapture.setPaidAmount(PennantApplicationUtil.unFormateAmount(ocrPaid, ccyFormatter));

			if ((this.builderDemand.getActualValue().compareTo(BigDecimal.ZERO) == 0)
					&& (this.ocrPaid.getActualValue().compareTo(BigDecimal.ZERO) == 0)) {
				throw new WrongValueException(this.builderDemand, "Builder Demand and Paid Amount both cannot be Zero");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// This condition for either Builder demand or OCR Paid amount is mandatory
		try {
			BigDecimal ocrPaid = this.ocrPaid.getActualValue();
			BigDecimal demand = this.builderDemand.getActualValue();
			if (ocrPaid.compareTo(BigDecimal.ZERO) == 0 && demand.compareTo(BigDecimal.ZERO) == 0) {
				String msg = "Either ".concat(Labels.getLabel("label_FinOCRCaptureDialog_BuilderDemand.value"));
				msg = msg.concat(" or ").concat(Labels.getLabel("label_FinOCRCaptureDialog_OCRpaid.value"));
				throw new WrongValueException(this.builderDemand,
						Labels.getLabel("FIELD_NO_NEGATIVE", new String[] { msg }));
			}
			afinOCRCapture.setPaidAmount(PennantApplicationUtil.unFormateAmount(ocrPaid, ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Remarks
		try {
			afinOCRCapture.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Receipt Date
		try {
			if (DateUtil.compare(this.ocrReceiptDate.getValue(), appDate) > 0) {
				throw new WrongValueException(this.ocrReceiptDate,
						Labels.getLabel("DATE_NOT_AFTER",
								new String[] { Labels.getLabel("label_FinOCRCaptureDialog_OCRdate.value"),
										DateUtil.format(appDate, "dd/MM/yyyy") }));
			}
			afinOCRCapture.setReceiptDate(this.ocrReceiptDate.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			afinOCRCapture.setFileName(this.uploadedfileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Document Image
		try {
			afinOCRCapture.setDocImage(this.docbyte);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param afinOCRCapture The entity that need to be render.
	 */
	public void doShowDialog(FinOCRCapture afinOCRCapture) {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		if (StringUtils.equals(afinOCRCapture.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)) {
			this.btnUploadView.setVisible(true);
			this.btnUpload.setVisible(false);
		} else {
			this.btnUploadView.setVisible(false);
			this.btnUpload.setVisible(true);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (afinOCRCapture.isNewRecord()) {
			this.btnCtrl.setInitNew();
			afinOCRCapture.setReceiptDate(appDate);
			doEdit();
		} else {
			if (isFromParent()) {
				if (enqiryModule) {
					doReadOnly();
					this.btnCtrl.setBtnStatus_Enquiry();
					this.btnNotes.setVisible(false);
				} else {
					doEdit();
				}
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(afinOCRCapture);
		this.windowFinOCRCaptureDialog.setHeight("50%");
		this.windowFinOCRCaptureDialog.setWidth("80%");
		setDialog(DialogType.MODAL);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting the validation constraints for fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.disbursementSequence.setConstraint("");
		this.builderDemand.setConstraint("");
		this.ocrPaid.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.disbursementSequence.setErrorMessage("");
		this.builderDemand.setErrorMessage("");
		this.ocrPaid.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final FinOCRCapture afinOCRCapture, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = processFinOCRCaptureDetails(afinOCRCapture, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.windowFinOCRCaptureDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			finOCRDialogCtrl.doFillFinOCRCaptureDetails(this.finOCRCaptureList);
			return true;
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinOCRCapture afinOCRCapture = new FinOCRCapture();
		BeanUtils.copyProperties(this.finOCRCapture, afinOCRCapture);

		doDelete(String.valueOf(afinOCRCapture.getDisbSeq()), afinOCRCapture);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		readOnlyComponent(isReadOnly("FinOCRCaptureDialog_disbSeq"), this.disbursementSequence);
		readOnlyComponent(isReadOnly("FinOCRCaptureDialog_builderDemand"), this.builderDemand);
		readOnlyComponent(isReadOnly("FinOCRCaptureDialog_ocrPaid"), this.ocrPaid);
		readOnlyComponent(isReadOnly("FinOCRCaptureDialog_ocrReceiptDate"), this.ocrReceiptDate);
		readOnlyComponent(isReadOnly("FinOCRCaptureDialog_remarks"), this.remarks);

		if (PennantConstants.RCD_STATUS_APPROVED.equals(finOCRCapture.getRecordStatus())) {
			this.disbursementSequence.setReadonly(true);
			this.builderDemand.setReadonly(true);
			this.ocrPaid.setReadonly(true);
			this.ocrReceiptDate.setDisabled(true);
			this.remarks.setReadonly(true);
			this.btnDelete.setDisabled(true);
			this.btnSave.setDisabled(true);
			this.btnUpload.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.disbursementSequence);
		readOnlyComponent(true, this.builderDemand);
		readOnlyComponent(true, this.ocrPaid);
		readOnlyComponent(true, this.ocrReceiptDate);
		readOnlyComponent(true, this.remarks);
		readOnlyComponent(true, this.btnUpload);

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
		// this.disbursementSequence.setValue("");
		this.builderDemand.setValue("");
		this.ocrPaid.setValue("");
		this.remarks.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final FinOCRCapture afinOCRCapture = new FinOCRCapture();
		BeanUtils.copyProperties(this.finOCRCapture, afinOCRCapture);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(afinOCRCapture);

		isNew = afinOCRCapture.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(afinOCRCapture.getRecordType())) {
				afinOCRCapture.setVersion(afinOCRCapture.getVersion() + 1);
				if (isNew) {
					afinOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					afinOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					afinOCRCapture.setNewRecord(true);
				}
			}
		} else {
			afinOCRCapture.setVersion(afinOCRCapture.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				afinOCRCapture.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(afinOCRCapture.getRecordType())) {
				afinOCRCapture.setVersion(afinOCRCapture.getVersion() + 1);
				afinOCRCapture.setRecordType(PennantConstants.RCD_UPD);
			}

			if (PennantConstants.RCD_ADD.equals(afinOCRCapture.getRecordType()) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_NEW.equals(afinOCRCapture.getRecordType())) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			AuditHeader auditHeader = processFinOCRCaptureDetails(afinOCRCapture, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.windowFinOCRCaptureDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				finOCRDialogCtrl.doFillFinOCRCaptureDetails(this.finOCRCaptureList);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader processFinOCRCaptureDetails(FinOCRCapture afinOCRCapture, String tranType) {

		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(afinOCRCapture, tranType);
		// finOCRCaptureList = new ArrayList<FinOCRCapture>();

		if (CollectionUtils.isNotEmpty(getFinOCRDialogCtrl().getFinOCRCaptureList())) {
			if (!PennantConstants.TRAN_DEL.equals(tranType)) {
				auditHeader = validate(getFinOCRDialogCtrl().getFinOCRCaptureList(), afinOCRCapture, auditHeader);
			}
			if (!CollectionUtils.isEmpty(auditHeader.getErrorMessage())) {
				return auditHeader;
			}
			for (FinOCRCapture finOCRCapture : getFinOCRDialogCtrl().getFinOCRCaptureList()) {

				// Both Current and Existing list Sequence is same
				if (finOCRCapture.getDisbSeq() == afinOCRCapture.getDisbSeq()) {

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(afinOCRCapture.getRecordType())) {
							afinOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finOCRCaptureList.add(afinOCRCapture);
						} else if (PennantConstants.RCD_ADD.equals(afinOCRCapture.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(afinOCRCapture.getRecordType())) {
							afinOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finOCRCaptureList.add(afinOCRCapture);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(afinOCRCapture.getRecordType())) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							finOCRCaptureList.add(finOCRCapture);
						}
					}
				} else {
					finOCRCaptureList.add(finOCRCapture);
				}
			}
		}
		if (!recordAdded) {
			finOCRCaptureList.add(afinOCRCapture);
		}
		return auditHeader;

	}

	/**
	 * Validating OCR Capture Details
	 * 
	 * @param finOCRCaptureList
	 * @param afinOCRCapture
	 * @param auditHeader
	 * @return
	 */
	private AuditHeader validate(List<FinOCRCapture> finOCRCaptureList, FinOCRCapture afinOCRCapture,
			AuditHeader auditHeader) {

		return auditHeader;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param afinOCRCapture
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinOCRCapture afinOCRCapture, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinOCRCapture.getBefImage(), afinOCRCapture);

		return new AuditHeader(getReference(), String.valueOf(afinOCRCapture.getId()), null, null, auditDetail,
				afinOCRCapture.getUserDetails(), getOverideMap());
	}

	/**
	 * onChange Event For contributor
	 */
	public void onChange$contributor(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * checking user has access for the components or not
	 */
	public boolean isReadOnly(String componentName) {
		if (enqiryModule) {
			return true;
		} else if (isWorkFlowEnabled()) {
			return getUserWorkspace().isReadOnly(componentName);
		} else {
			return getUserWorkspace().isReadOnly(componentName);
		}
	}

	public void onSelect$disbursementSequence(ForwardEvent event) {
		setDisbursmentAmount();
	}

	private void setDisbursmentAmount() {
		// TODO
		/*
		 * Intbox item = this.disbursementSequence.getSelectedItem(); if (item != null && item.getValue() != null) {
		 * FinanceDisbursement disbursement = (FinanceDisbursement) item.getAttribute("data"); if (disbursement != null)
		 * { BigDecimal disAmt = getTotalByDisbursment(disbursement,
		 * financeDetail.getFinScheduleData().getFinanceMain());
		 * this.disbDateAmount.setValue(PennantApplicationUtil.formateAmount(disAmt, ccyFormatter)); } } else {
		 * this.disbDateAmount.setValue(BigDecimal.ZERO); }
		 */
	}

	public static BigDecimal getTotalByDisbursment(FinanceDisbursement financeDisbursement, FinanceMain main) {
		BigDecimal totdisbAmt = BigDecimal.ZERO;

		// check is first disbursement
		if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()
				&& financeDisbursement.getDisbSeq() == 1) {

			totdisbAmt = totdisbAmt.subtract(main.getDownPayment());
			totdisbAmt = totdisbAmt.subtract(main.getDeductFeeDisb());
			if (FinanceConstants.BPI_DISBURSMENT.equals(main.getBpiTreatment())) {
				totdisbAmt = totdisbAmt.subtract(main.getBpiAmount());
			}
		} else if (financeDisbursement.getDisbSeq() > 1) {
			totdisbAmt = totdisbAmt.subtract(financeDisbursement.getDeductFeeDisb());

		}
		totdisbAmt = totdisbAmt.add(financeDisbursement.getDisbAmount());
		return totdisbAmt;

	}

	/**
	 * Generating Step Sequence
	 * 
	 * @param OCRDetail
	 * @return
	 */
	private int getDisbursementSequence(FinOCRCapture ocrCapture) {
		int sequence = 1;
		if (ocrCapture.getDisbSeq() > 0) {
			return ocrCapture.getDisbSeq();
		}
		List<FinOCRCapture> list = finOCRDialogCtrl.getFinOCRCaptureList();
		if (!CollectionUtils.isEmpty(list)) {
			Collections.sort(list,
					(ocrCapture1, ocrCapture2) -> ocrCapture1.getDisbSeq() > ocrCapture2.getDisbSeq() ? -1
							: ocrCapture1.getDisbSeq() < ocrCapture2.getDisbSeq() ? 1 : 0);
			sequence = list.get(0).getDisbSeq() + 1;
		}

		return sequence;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinOCRDialogCtrl getFinOCRDialogCtrl() {
		return finOCRDialogCtrl;
	}

	public void setFinOCRDialogCtrl(FinOCRDialogCtrl finOCRDialogCtrl) {
		this.finOCRDialogCtrl = finOCRDialogCtrl;
	}

	public boolean isFromParent() {
		return fromParent;
	}

	public void setFromParent(boolean newFinance) {
		this.fromParent = newFinance;
	}

	public List<FinOCRCapture> getFinOCRCaptureList() {
		return finOCRCaptureList;
	}

	public void setFinOCRCaptureList(List<FinOCRCapture> finOCRCaptureList) {
		this.finOCRCaptureList = finOCRCaptureList;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinOCRHeaderService getFinOCRHeaderService() {
		return finOCRHeaderService;
	}

	public void setFinOCRHeaderService(FinOCRHeaderService finOCRHeaderService) {
		this.finOCRHeaderService = finOCRHeaderService;
	}

	public byte[] getDocbyte() {
		return docbyte;
	}

	public void setDocbyte(byte[] docbyte) {
		this.docbyte = docbyte;
	}

}
