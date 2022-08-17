package com.pennant.webui.tds.receivables.tdsreceivablestxn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectTdsReceivableDialogCtrl extends GFCBaseCtrl<TdsReceivablesTxn> {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(SelectTdsReceivableDialogCtrl.class);

	protected Window window_SelectTDSReceivableDialog;
	protected ExtendedCombobox tanNumber;
	protected ExtendedCombobox certificateNo;
	protected Datebox certificateDate;
	protected Button btnProceed;
	private TdsReceivablesTxnService tdsReceivablesTxnService;

	public TdsReceivablesTxnListCtrl tdsReceivablesTxnListCtrl;

	private TdsReceivablesTxn tdsReceivablesTxn;

	public SelectTdsReceivableDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_SelectTDSReceivableDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_SelectTDSReceivableDialog);

		if (arguments.containsKey("tdsReceivablesTxnListCtrl")) {
			this.tdsReceivablesTxnListCtrl = (TdsReceivablesTxnListCtrl) arguments.get("tdsReceivablesTxnListCtrl");
		}

		if (arguments.containsKey("tdsReceivablesTxn")) {
			this.tdsReceivablesTxn = (TdsReceivablesTxn) arguments.get("tdsReceivablesTxn");
		}

		doSetFieldProperties();
		showSelectTDSReceivableDialog();
		logger.debug(Literal.LEAVING);

	}

	private void showSelectTDSReceivableDialog() throws InterruptedException {

		logger.debug(Literal.ENTERING);
		try {
			// open the dialog in modal mode
			this.window_SelectTDSReceivableDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {

		logger.debug(Literal.ENTERING);

		this.tanNumber.setMaxlength(12);
		this.certificateDate.setFormat(PennantConstants.dateFormat);
		this.tanNumber.setMandatoryStyle(true);
		this.tanNumber.setModuleName("TanDetail");
		this.tanNumber.setValueColumn("TanNumber");
		this.tanNumber.setDescColumn("TanHolderName");
		this.tanNumber.setValidateColumns(new String[] { "TanNumber" });

		this.certificateNo.setMaxlength(12);
		this.certificateNo.setMandatoryStyle(true);
		this.certificateNo.setModuleName("AddCertificate");
		this.certificateNo.setValueColumn("CertificateNumber");
		this.certificateNo.setDescColumn("CertificateNumber");
		this.certificateNo.setValidateColumns(new String[] { "CertificateNumber" });
		this.certificateDate.setDisabled(true);
		this.certificateNo.setWhereClause(
				"NOT EXISTS(SELECT 1 FROM tds_receivables_temp WHERE (Id = TDS_RECEIVABLES_aView.Id)) AND Status is Null and BalanceAmount > 0");

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnProceed(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doFieldValidation();

		TdsReceivable tdsReceivable = (TdsReceivable) this.certificateNo.getObject();
		tdsReceivable.setNewRecord(true);
		tdsReceivable.setWorkflowId(getWorkFlowId());

		int count = tdsReceivablesTxnService.getPendingTransactions(tdsReceivable.getId());

		if (count == 0) {
			Map<String, Object> arg = new HashMap<>();
			arg.put("tdsReceivable", tdsReceivable);
			arg.put("tdsReceivablesTxn", tdsReceivablesTxn);
			arg.put("tdsReceivablesTxnListCtrl", tdsReceivablesTxnListCtrl);
			arg.put("module", PennantConstants.RECEIVABLE_ADJUSTMENT_MODULE);

			try {
				Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivablesTxn/TdsReceivablesTxnDialog.zul",
						null, arg);

				this.window_SelectTDSReceivableDialog.onClose();
			} catch (Exception e) {
				logger.error("Exception:", e);
				MessageUtil.showError(e);
			}
		} else {
			MessageUtil.showError(Labels.getLabel("Adjustment_Transaction_Pending"));

		}

		logger.debug(Literal.LEAVING);
	}

	private void doFieldValidation() throws InterruptedException {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.isBlank(this.tanNumber.getValue())) {
				throw new WrongValueException(this.tanNumber,
						Labels.getLabel("CHECK_NO_EMPTY", new String[] { Labels.getLabel("label_TanNumber.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			if (StringUtils.isBlank(this.certificateNo.getValue())) {
				throw new WrongValueException(this.certificateNo, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_CertificateNumber.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving ");
	}

	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

	public void onFulfill$tanNumber(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = this.tanNumber.getObject();

		if (dataObject instanceof String) {
			this.tanNumber.setValue(dataObject.toString());
			this.tanNumber.setDescription("");
		} else {
			TanDetail details = (TanDetail) dataObject;
			if (details != null) {
				this.tanNumber.setValue(details.getTanNumber());
				long tanId = details.getId();
				this.certificateNo.setValue("");
				this.certificateDate.setValue(null);
				Filter[] filters1 = new Filter[1];
				if (tanId != 0) {
					filters1[0] = new Filter("tanID", tanId, Filter.OP_EQUAL);
				} else {
					filters1[0] = new Filter("tanID", null, Filter.OP_NOT_EQUAL);
				}
				this.certificateNo.setFilters(filters1);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$certificateNo(Event event) {

		logger.debug("Entering " + event.toString());

		Object dataObject = this.certificateNo.getObject();

		if (dataObject instanceof String) {
			this.certificateNo.setValue(dataObject.toString());
			this.certificateNo.setDescription("");
			this.tanNumber.setValue("");
		} else {
			TdsReceivable details = (TdsReceivable) dataObject;
			if (details != null) {
				this.certificateNo.setValue(details.getCertificateNumber());
				this.certificateDate.setValue(details.getCertificateDate());
				this.tanNumber.setValue(details.getTanNumber());
			} else {
				this.certificateNo.setValue("");
				this.certificateDate.setValue(null);
				this.tanNumber.setValue("");
			}
		}

		logger.debug("Leaving " + event.toString());

	}

}
