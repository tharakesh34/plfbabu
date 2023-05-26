package com.pennant.pff.noc.webui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.letter.LetterUtil;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectGenerateLetterCtrl extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectGenerateLetterCtrl.class);

	protected Window windowSelectGenerateLetter;
	protected Textbox finReference;
	protected Combobox letterType;
	protected Button btnProceed;

	private GenerateLetterListCtrl generateLetterListCtrl;
	private GenerateLetter generateLetter;
	private GenerateLetterService generateLetterService;

	private final List<ValueLabel> letterTypeList = LetterUtil.getLetterTypes();

	public SelectGenerateLetterCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GenerateLetter";
		super.pageRightName = "GenerateLetterMaker";
	}

	public void onCreate$windowSelectGenerateLetter(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		setPageComponents(windowSelectGenerateLetter);

		this.generateLetter = (GenerateLetter) arguments.get("generateLetter");
		this.generateLetterListCtrl = (GenerateLetterListCtrl) arguments.get("generateLetterListCtrl");

		fillComboBox(this.letterType, "", letterTypeList, "");
		this.letterType.setReadonly(true);

		this.windowSelectGenerateLetter.doModal();

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		doWriteComponentsToBean();
		if (!validateReference()) {
			return;
		}

		doShowDialog();
		this.windowSelectGenerateLetter.onClose();

		logger.debug(Literal.LEAVING + event.toString());
	}

	private boolean validateReference() throws Exception {
		logger.debug(Literal.ENTERING);

		this.generateLetter.setFinReference(this.finReference.getValue());
		this.generateLetter.setLetterType(getComboboxValue(letterType));
		this.generateLetter.setWorkflowId(this.generateLetter.getWorkflowId());
		this.generateLetter.setNewRecord(this.generateLetter.isNewRecord());
		generateLetterService.getFinanceDetailById(this.generateLetter);

		FinanceMain fm = this.generateLetter.getFinanceDetail().getFinScheduleData().getFinanceMain();

		if (fm == null) {
			MessageUtil.showError("Invalid " + Labels.getLabel("label_listheader_Finreference")
					+ " :".concat(this.finReference.getValue()));
			return false;
		}

		List<LoanTypeLetterMapping> letterMapping = generateLetterService.getFinTypeMap(fm.getFinType());

		if (CollectionUtils.isEmpty(letterMapping)) {
			MessageUtil.showError("Loan Type Letter mapping not available");
			return false;
		}

		String sellectedLetterType = getComboboxValue(letterType);
		if (!letterMapping.stream().anyMatch(l -> l.getLetterType().equals(sellectedLetterType))) {
			MessageUtil.showError(
					"Loan Type Letter mapping not available for the selected Letter Type : " + sellectedLetterType);
			return false;
		}

		this.generateLetter.setFinID(this.generateLetter.getFinanceDetail().getFinScheduleData().getFinID());

		if (generateLetterService.isLetterInitiated(fm.getFinID(), this.generateLetter.getLetterType())) {
			MessageUtil.showError(Labels.getLabel("label_listheader_LetterType")
					+ " is Already Initiated For".concat(this.finReference.getValue()));
			return false;
		}

		if (generateLetterService.letterIsInQueu(fm.getFinID(), this.generateLetter.getLetterType())) {
			MessageUtil.showError(Labels.getLabel("label_listheader_LetterType").concat(this.letterType.getValue())
					+ " is Already Initiated For ".concat(this.finReference.getValue())
					+ " and is in queue for letter generation.");
			return false;
		}

		if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())
				&& !this.generateLetter.getLetterType().equals(LetterType.CANCELLATION.name())) {
			MessageUtil.showError("Invalid " + Labels.getLabel("label_listheader_LetterType")
					+ " for ".concat(this.finReference.getValue()));
			return false;
		}

		if ((FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus())
				&& !(this.generateLetter.getLetterType().equals(LetterType.NOC.name())
						|| this.generateLetter.getLetterType().equals(LetterType.CLOSURE.name())))) {
			MessageUtil.showError("Invalid " + Labels.getLabel("label_listheader_LetterType")
					+ " for ".concat(this.finReference.getValue()));
			return false;
		}

		if (fm.isFinIsActive() && fm.getClosingStatus() == null) {
			MessageUtil.showError("Active Loans are not allowed to Generate Letter");
			return false;
		}

		logger.debug(Literal.LEAVING);

		return true;
	}

	public void doWriteComponentsToBean() throws Exception {
		List<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if ("#".equals(getComboboxValue(this.letterType))) {
				throw new WrongValueException(this.letterType, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_GenerateLetterDialog_LetterType") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.finReference.getValue())) {
				throw new WrongValueException(this.finReference, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_listheader_Finreference") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doClearMessage();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.letterType.setConstraint("");
		this.finReference.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("generateLetter", this.generateLetter);
		map.put("moduleCode", this.moduleCode);
		map.put("generateLetterListCtrl", generateLetterListCtrl);

		Executions.createComponents("/WEB-INF/pages/NOC/GenerateLetterDialog.zul", null, map);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug(Literal.ENTERING + event.toString());

		this.windowSelectGenerateLetter.onClose();

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void setGenerateLetterListCtrl(GenerateLetterListCtrl generateLetterListCtrl) {
		this.generateLetterListCtrl = generateLetterListCtrl;
	}

	@Autowired
	public void setGenerateLetterService(GenerateLetterService generateLetterService) {
		this.generateLetterService = generateLetterService;
	}
}
