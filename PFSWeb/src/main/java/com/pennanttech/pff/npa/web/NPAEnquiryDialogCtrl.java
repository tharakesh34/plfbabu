package com.pennanttech.pff.npa.web;

import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.npa.model.AssetClassification;

public class NPAEnquiryDialogCtrl extends GFCBaseCtrl<FinExpenseDetails> {
	private static final long serialVersionUID = 3184249234920071313L;

	protected Window window_NPAEnquiryDialog;

	protected Textbox finReference;
	protected Textbox effFinReference;
	protected Textbox finIsActive;
	protected Textbox npaClass;
	protected Textbox effNpaClass;
	protected Textbox npaSubClass;
	protected Textbox effNpaSubClass;
	protected Textbox pastDueDays;
	protected Textbox effPastDueDays;
	protected Textbox pastDueDate;
	protected Textbox effPastDueDate;
	protected Textbox npaPastDueDays;
	protected Textbox effNpaPastDueDays;
	protected Textbox npaPastDueDate;
	protected Textbox effNpaPastDueDate;
	protected Textbox npaStage;
	protected Textbox effNpaStage;
	protected Textbox effNpaStageInfo;

	private AssetClassification npa;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;

	public NPAEnquiryDialogCtrl() {
		super();
	}

	public void onCreate$window_NPAEnquiryDialog(ForwardEvent event) throws Exception {
		setPageComponents(window_NPAEnquiryDialog);

		if (arguments.containsKey("assetClassification")) {
			npa = (AssetClassification) arguments.get("assetClassification");
		} else {
			npa = new AssetClassification();
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		getBorderLayoutHeight();

		int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;

		this.window_NPAEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight + "px");

		doFillDetails();
	}

	private String concat(String code, String description) {
		if (code == null) {
			return "";
		}
		return code.concat(" - ").concat(description);
	}

	private void doFillDetails() {

		this.finReference.setValue(npa.getFinReference());
		this.effFinReference.setValue(npa.getEffFinReference());
		this.finIsActive.setValue(npa.isFinIsActive() ? "Active" : "Inactive");
		this.npaClass.setValue(concat(npa.getNpaClassCode(), npa.getNpaClassDesc()));
		this.effNpaClass.setValue(concat(npa.getEffNpaClassCode(), npa.getEffNpaClassDesc()));
		this.npaSubClass.setValue(concat(npa.getNpaSubClassCode(), npa.getNpaSubClassDesc()));
		this.effNpaSubClass.setValue(concat(npa.getEffNpaSubClassCode(), npa.getEffNpaSubClassDesc()));
		this.pastDueDays.setValue(String.valueOf(npa.getPastDueDays()));
		this.effPastDueDays.setValue(String.valueOf(npa.getEffPastDueDays()));
		this.pastDueDate.setValue(DateUtil.formatToLongDate(npa.getPastDueDate()));
		this.effPastDueDate.setValue(DateUtil.formatToLongDate(npa.getEffPastDueDate()));
		this.npaPastDueDays.setValue(String.valueOf(npa.getNpaPastDueDays()));
		this.effNpaPastDueDays.setValue(String.valueOf(npa.getEffNpaPastDueDays()));
		this.npaPastDueDate.setValue(DateUtil.formatToLongDate(npa.getNpaPastDueDate()));
		this.effNpaPastDueDate.setValue(DateUtil.formatToLongDate(npa.getEffNpaPastDueDate()));
		this.npaStage.setValue(npa.isNpaStage() ? "TRUE" : "FALSE");
		this.effNpaStage.setValue(npa.isEffNpaStage() ? "TRUE" : "FALSE");

		String style = "color:red";

		if (npa.getPastDueDays() > 0) {
			this.pastDueDays.setStyle(style);
		}

		if (npa.getEffPastDueDays() > 0) {
			this.effPastDueDays.setStyle(style);
		}

		if (npa.getNpaPastDueDays() > 0) {
			this.npaPastDueDays.setStyle(style);
		}

		if (npa.isNpaStage()) {
			this.npaStage.setStyle(style);
		}

		if (npa.getEffNpaPastDueDays() > 0) {
			this.effNpaPastDueDays.setStyle(style);
		}

		if (npa.isEffNpaStage()) {
			this.effNpaStage.setStyle(style);
		}

		if (!npa.isFinIsActive()) {
			this.finIsActive.setStyle(style);
		}

		if (npa.getFinReference() == null) {
			this.finIsActive.setValue("");
		}

	}
}
