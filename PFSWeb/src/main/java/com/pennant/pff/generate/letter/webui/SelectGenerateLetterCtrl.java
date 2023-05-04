package com.pennant.pff.generate.letter.webui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.generate.letter.model.GenerateLetter;
import com.pennant.pff.generate.letter.service.GenerateLetterService;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
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
	private FinanceMainService financeMainService;

	public SelectGenerateLetterCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GenerateLetter";
		super.pageRightName = "GenerateLetterDialog";
	}

	public void onCreate$windowSelectGenerateLetter(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		setPageComponents(windowSelectGenerateLetter);

		this.generateLetter = (GenerateLetter) arguments.get("generateLetter");
		this.generateLetterListCtrl = (GenerateLetterListCtrl) arguments.get("generateLetterListCtrl");

		this.windowSelectGenerateLetter.doModal();
		logger.debug(Literal.ENTERING + event.toString());
	}

	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		validateReference();
		doSetValidation();
		doWriteComponentsToBean();

		doShowDialog();
		this.windowSelectGenerateLetter.onClose();

		logger.debug(Literal.ENTERING + event.toString());
	}

	private void validateReference() throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			boolean isDataExists = generateLetterService.isReferenceExist(this.finReference.getValue());

			if (isDataExists) {
				throw new AppException("");
			}
		} catch (Exception e) {
			MessageUtil.showError(Labels.getLabel("label_GenarateLetter_Validation"));
			this.finReference.setValue("");
			logger.debug(Literal.LEAVING);
			return;
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {

		this.letterType.setConstraint(new PTStringValidator(Labels.getLabel("label_GenerateLetterDialog_LetterType"),
				PennantRegularExpressions.REGEX_NUMERIC, true));

		this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_listheader_Finreference"),
				PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
	}

	public void doWriteComponentsToBean() throws Exception {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isEmpty(this.letterType.getValue())) {
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

		Date appDate = SysParamUtil.getAppDate();

		final Map<String, Object> map = new HashMap<String, Object>();
		GenerateLetter geneLtr = new GenerateLetter();
		FinanceMain financeMain = financeMainService.getFinanceMainByFinRef(this.finReference.getValue());
		geneLtr.setFinanceMain(financeMain);
		geneLtr.setFinReference(this.finReference.getValue());
		geneLtr.setLetterType(this.letterType.getValue());
		geneLtr.setCreatedOn(appDate);
		geneLtr.setCreatedDate(appDate);
		geneLtr.setGeneratedDate(appDate);
		geneLtr.setGeneratedOn(appDate);
		geneLtr.setGeneratedBy(getUserWorkspace().getUserId());
		geneLtr.setWorkflowId(this.generateLetter.getWorkflowId());
		geneLtr.setNewRecord(this.generateLetter.isNewRecord());

		map.put("generateLetter", geneLtr);
		map.put("generateLetterListCtrl", generateLetterListCtrl);

		Executions.createComponents("/PFSWeb/WebContent/WEB-INF/pages/GenerateLetter/GenerateLetterDialog.zul", null,
				map);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug(Literal.ENTERING + event.toString());

		this.windowSelectGenerateLetter.onClose();

		logger.debug(Literal.LEAVING + event.toString());
	}
}
