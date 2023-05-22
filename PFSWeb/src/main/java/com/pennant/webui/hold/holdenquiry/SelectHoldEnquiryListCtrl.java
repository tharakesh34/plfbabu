package com.pennant.webui.hold.holdenquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.pff.holdmarking.model.HoldMarkingDetail;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingDetailDAO;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectHoldEnquiryListCtrl extends GFCBaseCtrl<HoldMarkingDetail> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger logger = LogManager.getLogger(SelectHoldEnquiryListCtrl.class);

	protected Window windowSelectHoldEnquiryList;
	protected Textbox finReference;
	protected Button btnProceed;
	private boolean isModelWindow = false;
	private String finRefValue;
	protected Textbox accNumber;
	private String accountNumber;
	private HoldMarkingDetailDAO holdMarkingDetailDAO;

	public SelectHoldEnquiryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "HoldEnquiry";
		super.pageRightName = "";
	}

	public void onCreate$windowSelectHoldEnquiryList(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(windowSelectHoldEnquiryList);

		if (arguments.containsKey("isModelWindow")) {
			isModelWindow = (boolean) arguments.get("isModelWindow");
		}

		if (arguments.containsKey("finReference")) {
			finRefValue = (String) arguments.get("finReference");
		}

		if (arguments.containsKey("accNumber")) {
			accountNumber = (String) arguments.get("accNumber");
		}

		try {
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		this.windowSelectHoldEnquiryList.doModal();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotEmpty(finRefValue)) {
			this.finReference.setValue(finRefValue);
			this.finReference.setReadonly(true);
		} else {
			accNumber.setDisabled(true);
			logger.debug(Literal.LEAVING);
		}
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean(finRefValue, accountNumber);
		doShowDialogPage(this.finRefValue, this.accountNumber);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		this.windowSelectHoldEnquiryList.onClose();
		if (!isModelWindow) {

			final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
					.getFellow("tabBoxIndexCenter");
			tabbox.getSelectedTab().close();
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(String finReference, String accountNumber) {
		logger.debug(Literal.ENTERING);

		doWriteComponentsToBean(finReference, accountNumber);

		List<HoldMarkingDetail> hold = holdMarkingDetailDAO.getHoldDtlsByRefAndAcc(finReference, accountNumber);

		if (hold == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		Map<String, Object> aruments = new HashMap<>();
		aruments.put("hold", hold);
		aruments.put("enquiryModule", true);
		aruments.put("header", !StringUtils.isEmpty(finReference));

		try {
			Executions.createComponents("/WEB-INF/pages/HoldEnquiry/HoldEnquiyDialog.zul", null, aruments);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doRemoveValidation();

		this.finReference.setConstraint(new PTStringValidator(
				Labels.getLabel("label_SelectHoldEnquiryList_finReference.value"), null, true, false));
		this.accNumber.setConstraint(
				new PTStringValidator(Labels.getLabel("label_SelectHoldEnquiryList_accNum.value"), null, true, false));

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint("");
		this.accNumber.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(String finRefValue, String accountNumber) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finRefValue = "";
			if (!this.finReference.isDisabled())
				this.finRefValue = this.finReference.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.accountNumber = "";
			if (!this.accNumber.isDisabled())
				this.accountNumber = this.accNumber.getValue();
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

	public void onCheck$loanReference(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.accNumber.setDisabled(true);
		this.finReference.setDisabled(false);
		this.accNumber.setValue("");
		this.accNumber.setConstraint("");
		this.accNumber.setReadonly(true);

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$accountNumber(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doClearMessage();
		doRemoveValidation();

		this.accNumber.setReadonly(false);
		this.accNumber.setDisabled(false);
		this.finReference.setValue("");
		this.finReference.setConstraint("");
		this.finReference.setDisabled(true);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$accNumber(ForwardEvent event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		accountNumber = this.accNumber.getValue();

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finReference(Event event) {
		this.finRefValue = this.finReference.getValue();
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.finReference.setErrorMessage("");
		this.accNumber.setErrorMessage("");

		logger.debug("Leaving");
	}

	@Autowired
	public void setHoldMarkingDetailDAO(HoldMarkingDetailDAO holdMarkingDetailDAO) {
		this.holdMarkingDetailDAO = holdMarkingDetailDAO;
	}
}
