package com.pennant.webui.lien.lienEnquiry;

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

import com.pennant.ExtendedCombobox;
import com.pennant.backend.dao.liendetails.LienDetailsDAO;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.model.lien.LienDetails;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectLienEnquiryListCtrl extends GFCBaseCtrl<LienDetails> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger logger = LogManager.getLogger(SelectLienEnquiryListCtrl.class);

	protected Window windowSelectLienEnquiryList;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;
	private boolean isModelWindow = false;
	private String finRefValue;
	protected Textbox accNumber;
	private String accountNumber;
	private LienDetailsDAO lienDetailsDAO;

	public SelectLienEnquiryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LienEnquiry";
		super.pageRightName = "";
	}

	public void onCreate$windowSelectLienEnquiryList(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(windowSelectLienEnquiryList);

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
		this.windowSelectLienEnquiryList.doModal();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotEmpty(finRefValue)) {
			this.finReference.setValue(finRefValue);
			this.finReference.setReadonly(true);
		} else {
			accNumber.setDisabled(true);
			this.finReference.setMandatoryStyle(true);
			this.finReference.setModuleName("FinanceDetail");
			this.finReference.setValueColumn("FinReference");
			this.finReference.setDescColumn("FinType");
			this.finReference.setValidateColumns(new String[] { "FinReference" });

			logger.debug(Literal.LEAVING);
		}
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);

		doShowDialogPage(this.finReference.getValue(), this.accNumber.getValue());
		doClear();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		this.windowSelectLienEnquiryList.onClose();
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

		List<LienDetails> lien = lienDetailsDAO.getLienDtlsByRefAndAcc(finReference, accountNumber);

		if (lien == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		Map<String, Object> aruments = new HashMap<>();
		aruments.put("lien", lien);
		aruments.put("enquiryModule", true);
		aruments.put("header", !finReference.isEmpty());

		try {
			Executions.createComponents("/WEB-INF/pages/LienEnquiry/LienEnquiryDialog.zul", null, aruments);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint(new PTStringValidator(
				Labels.getLabel("label_SelectLienEnquiryList_finReference.value"), null, false, true));
		this.accNumber.setConstraint(
				new PTStringValidator(Labels.getLabel("label_SelectLienEnquiryList_accNum.value"), null, false, true));

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
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finRefValue = this.finReference.getValidatedValue();
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
		this.finReference.setButtonDisabled(false);
		this.accNumber.setValue("");

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$accountNumber(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.finReference.getButton().setDisabled(true);
		this.finReference.getTextbox().setReadonly(true);

		this.accNumber.setDisabled(false);
		this.finReference.setValue("");

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

	private void doClear() {
		logger.debug(Literal.ENTERING);

		this.finReference.setValue("");
		this.accNumber.setValue("");

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setLienDetailsDAO(LienDetailsDAO lienDetailsDAO) {
		this.lienDetailsDAO = lienDetailsDAO;
	}

}
