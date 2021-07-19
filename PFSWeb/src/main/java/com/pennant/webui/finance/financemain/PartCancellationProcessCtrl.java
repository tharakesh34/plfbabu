package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.pff.core.schd.service.PartCancellationService;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/PartCancellationProcess.zul file.
 */
public class PartCancellationProcessCtrl extends GFCBaseListCtrl<FinServiceInstruction> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(PartCancellationProcessCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseListCtrl' GenericForwardComposer.
	 */
	protected Window window_PartCancellationProcess;
	protected Borderlayout borderLayout_PartCancellationProcess;
	protected ExtendedCombobox finReference;
	protected CurrencyBox refundAmt;
	protected Datebox refundDate;
	protected Button btnGetProcess;
	protected Button btnPostProcess;

	private int formatter = 0;

	private Groupbox grpScheduleBox;

	private PartCancellationService partCancellationService;

	/**
	 * default constructor.<br>
	 */
	public PartCancellationProcessCtrl() {
		super();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PartCancellationProcess(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		// Set the page level components.
		setPageComponents(window_PartCancellationProcess, borderLayout_PartCancellationProcess, null, null);

		// Render the page and display the data.
		doRenderPage();

		doSetFieldProperties();

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Finance Reference
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMandatoryStyle(true);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(140);

		// Refund Amount
		this.refundAmt.setSclass("");
		this.refundAmt.setTextBoxWidth(175);
		formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		this.refundAmt.setProperties(true, formatter);
		this.refundAmt.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, formatter));

		// Refund Date
		this.refundDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the Get Schedule button. Show the dialog page with a new
	 * entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnGetSchedule(Event event) {
		logger.debug(Literal.ENTERING);
		doSetValidation();
		doProcess(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the Post Schedule button. Show the dialog page with a new
	 * entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnPostSchedule(Event event) {
		logger.debug(Literal.ENTERING);
		doSetValidation();
		// Show a confirm box
		String msg = "Do you want to proceed with Post Schedule?";
		int conf = MessageUtil.confirm(msg);
		if (conf == MessageUtil.NO) {
			return;
		}
		doProcess(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		this.finReference.setConstraint(new PTStringValidator(
				Labels.getLabel("label_FinanceTaxDetailDialog_FinReference.value"), null, true, true));
		this.refundAmt.setConstraint(
				new PTStringValidator(Labels.getLabel("label_PartCancellation_RefundAmount.value"), null, true, true));
		this.refundDate.setConstraint(new PTDateValidator(Labels.getLabel("label_PartCancellation_RefundDate.value"),
				true, null, null, true));
	}

	private void doProcess(boolean save) {
		logger.debug(Literal.ENTERING);

		FinServiceInstruction finServiceInstc = new FinServiceInstruction();

		String finref = this.finReference.getValue();
		Date refunddate = this.refundDate.getValue();
		BigDecimal refundAmt = this.refundAmt.getActualValue();
		refundAmt = PennantApplicationUtil.unFormateAmount(refundAmt, 2);

		finServiceInstc.setFinReference(finref);
		finServiceInstc.setRefund(refundAmt);
		finServiceInstc.setPftChg(refundAmt);
		finServiceInstc.setFinEvent(FinServiceEvent.PART_CANCELLATION);
		finServiceInstc.setModuleDefiner(FinServiceEvent.PART_CANCELLATION);
		finServiceInstc.setFromDate(refunddate);
		finServiceInstc.setValueDate(refunddate);

		doRemoveValidation();

		String eventCode = AccountEventConstants.PART_CANCELATION;
		FinanceDetail finDetails = partCancellationService.getFinanceDetails(finServiceInstc, eventCode);

		// service level validations 
		AuditDetail auditDetail = partCancellationService.validateRequest(finServiceInstc, finDetails);
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail detail : auditDetail.getErrorDetails()) {
				MessageUtil.showError(detail);
				return;
			}
		}

		FinanceDetail newfinDetails = partCancellationService.doPartCancellation(finServiceInstc, finDetails);

		if (save) {
			finServiceInstc.setReqType("Post");
			newfinDetails.getFinScheduleData().getFinServiceInstructions().clear();
			newfinDetails.getFinScheduleData().getFinServiceInstructions().add(finServiceInstc);
			partCancellationService.postPartCancellation(finServiceInstc, newfinDetails);
		}

		doFillSchedule(newfinDetails);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint("");
		this.refundAmt.setConstraint("");
		this.refundDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doFillSchedule(FinanceDetail newfinanceDetail) {
		logger.debug(Literal.ENTERING);

		int sdSize = newfinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size();
		if (sdSize > 0) {
			this.grpScheduleBox.getChildren().clear();
			this.grpScheduleBox.setVisible(true);
			final HashMap<String, Object> map = new HashMap<>();
			map.put("financeDetail", newfinanceDetail);
			map.put("partCancellationProcesCtrl", this);
			map.put("isEnquiry", true);
			map.put("heightAdj", 40);
			map.put("printNotRequired", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul",
					this.grpScheduleBox, map);
		}

		logger.debug(Literal.LEAVING);
	}

	public PartCancellationService getPartCancellationService() {
		return partCancellationService;
	}

	public void setPartCancellationService(PartCancellationService partCancellationService) {
		this.partCancellationService = partCancellationService;
	}

}
