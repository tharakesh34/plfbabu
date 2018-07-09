package com.pennanttech.webui.sampling;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.sampling.model.SamplingDetails;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.sampling.Decision;
import com.pennanttech.pff.service.sampling.SamplingService;

@Component(value = "finSamplingDialogCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FinSamplingDialogCtrl extends GFCBaseCtrl<Sampling> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(FinSamplingDialogCtrl.class);

	protected Window window_FinSamplingDialog;
	protected Groupbox finBasicdetails;
	protected Groupbox groupSamplingDetails;
	protected Rows rows_Sampling;

	protected Textbox samplingTolerance;
	protected Combobox samplingDecision;
	protected Textbox samplingRemarks;
	protected ExtendedCombobox samplingResubmitReason;
	protected CurrencyBox samplingFinalRcmdAmt;
	protected Button btnSampling;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private Sampling sampling;
	private transient FinanceDetail financeDetail;
	private transient boolean validationOn;

	private int ccyFormatter = 2;
	private boolean recSave;
	int formatter = 0;

	Set<String> remarks = new HashSet<>();

	@Autowired
	private SamplingService samplingService;

	/**
	 * default constructor.<br>
	 */
	public FinSamplingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_FinSamplingDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinSamplingDialog);
		if (arguments.containsKey("finHeaderList") && arguments.get("finHeaderList") != null) {
			appendFinBasicDetails(arguments.get("finHeaderList"));
		}
		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		if (arguments.containsKey("financeMainBaseCtrl") && arguments.get("financeMainBaseCtrl") != null) {
			((FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl")).setFinSamplingDialogCtrl(this);
		}
		this.sampling = financeDetail.getSampling();
		
		if (arguments.get("enqiryModule") != null) {
			enqiryModule = (Boolean) arguments.get("enqiryModule");
			finBasicdetails.setVisible(false);
		}
		
		if (this.sampling == null) {

			// this.sampling = new Sampling();
			rows_Sampling.getChildren().clear();
			Row emptyRow = new Row();
			Cell cel = new Cell();
			cel.setColspan(5);
			emptyRow.appendChild(cel);
			emptyRow.setValue(Labels.getLabel("listbox.emptyMessage"));
			emptyRow.setStyle("text-align:center;");

			groupSamplingDetails.setVisible(false);
			return;

		}

		doSetFieldProperties();
		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);
		renderList();
		doReadOnly();
		doWriteBeanToComponents(this.sampling);
		logger.debug(Literal.LEAVING);
	}
	
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		if (enqiryModule) {
			this.samplingTolerance.setReadonly(true);
			this.samplingDecision.setDisabled(true);
			this.samplingRemarks.setReadonly(true);
			this.samplingResubmitReason.setReadonly(true);
			this.samplingFinalRcmdAmt.setReadonly(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			if (!enqiryModule) {
				this.userAction.setSelectedIndex(0);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	private void fillReasons(ExtendedCombobox reason) {
		logger.debug(Literal.ENTERING);

		reason.setModuleName("VerificationWaiverReason");
		reason.setValueColumn("Code");
		reason.setDescColumn("Description");
		reason.setValidateColumns(new String[] { "Code" });
		Filter[] reasonFilter = new Filter[1];
		reasonFilter[0] = new Filter("ReasonTypecode", PennantConstants.SAMPLING_RESUBMIT_REASON, Filter.OP_EQUAL);
		reason.setFilters(reasonFilter);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSampling(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		if (this.sampling == null) {
			MessageUtil.showMessage(Labels.getLabel("info.sampling_not_exists"));
			return;
		}
		Sampling inquiryObject = new Sampling();
		BeanUtils.copyProperties(inquiryObject, this.sampling);
		inquiryObject = samplingService.getSampling(inquiryObject, "_view");

		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("sampling", inquiryObject);
		map.put("LOAN_ORG", true);
		map.put("enqiryModule", this.enqiryModule);
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Sampling/SamplingDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$samplingResubmitReason(Event event) {
		ExtendedCombobox reason = this.samplingResubmitReason;
		Object dataObject = reason.getObject();

		if (dataObject instanceof String) {
			reason.setValue(dataObject.toString());
			reason.setDescription("");
		} else {
			ReasonCode reasonCode = (ReasonCode) dataObject;
			if (reasonCode != null) {
				reason.setAttribute("reasonId", reasonCode.getId());
				if (StringUtils.isNotEmpty(reasonCode.getDescription())) {
					reason.setAttribute("reasonDesc", reasonCode.getDescription());
				}
			}
		}
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param sampling
	 *            Sampling
	 */
	public void doWriteBeanToComponents(Sampling sampling) {
		logger.debug(Literal.ENTERING);

		this.samplingRemarks.setValue(sampling.getRemarks());
		this.samplingTolerance.setValue(sampling.getSamplingTolerance());
		if (this.sampling.getDecision() != null) {
			fillComboBox(this.samplingDecision, this.sampling.getDecision(), Decision.getList());
		} else {
			fillComboBox(this.samplingDecision, Decision.SELECT.getKey(), Decision.getList());
		}

		if (sampling.getResubmitReason() != null) {
			this.samplingResubmitReason.setValue(sampling.getResubmitReasonCode());
			this.samplingResubmitReason.setAttribute("reasonId", sampling.getResubmitReason());
			if (sampling.getResubmitReasonDesc() != null) {
				this.samplingResubmitReason.setDescription(sampling.getResubmitReasonDesc());
			} else {
				this.samplingResubmitReason.setDescription("");
			}
		}
		if (sampling.getRecommendedAmount() != null) {
			this.samplingFinalRcmdAmt.setValue(
					PennantAppUtil.formateAmount(sampling.getRecommendedAmount(), sampling.getCcyeditfield()));
		} else {
			this.samplingFinalRcmdAmt.setValue(BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);
	}

	private void fillComboBox(Combobox combobox, int value, List<ValueLabel> list) {
		combobox.getChildren().clear();
		for (ValueLabel valueLabel : list) {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (Integer.parseInt(valueLabel.getValue()) == value) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final HashMap<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void renderList() {
		// renderFields();
		renderSamplingDtails();
		this.samplingResubmitReason.addForward("onFulfill", self, "onChangeReason");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.samplingFinalRcmdAmt.setProperties(true, ccyFormatter);
		this.samplingDecision.setReadonly(true);
		fillReasons(this.samplingResubmitReason);
		logger.debug("Leaving");
	}

	private void renderSamplingDtails() {
		formatter = sampling.getCcyeditfield();
		Textbox textbox;
		Row row;
		Label label;
		Cell cell;

		for (SamplingDetails sd : sampling.getSamplingDetailsList()) {

			// Render collateral Caption
			if (StringUtils.isNotEmpty(sd.getCaption())) {
				row = new Row();
				row.setParent(rows_Sampling);
				cell = new Cell();
				cell.setColspan(5);
				label = new Label(sd.getCaption());
				label.setStyle("font-weight:bold;color: #ff4500;");
				label.setParent(cell);
				cell.setParent(row);
				continue;
			}

			row = new Row();
			row.setParent(rows_Sampling);

			label = new Label();
			label.setValue(sd.getParameter());
			label.setParent(row);

			if (sd.isAlignLeft()) {
				textbox = getTextbox();
				textbox.setValue(sd.getBranchCam());
				textbox.setParent(row);

				textbox = getTextbox();
				textbox.setValue(sd.getCreditCam());
				textbox.setParent(row);

				textbox = getTextbox();
				textbox.setValue(sd.getVariance());
				textbox.setParent(row);

			} else {
				textbox = getRightAlignedTextbox();
				textbox.setValue(sd.getBranchCam());
				textbox.setParent(row);

				textbox = getRightAlignedTextbox();
				textbox.setValue(sd.getCreditCam());
				textbox.setParent(row);

				textbox = getRightAlignedTextbox();
				textbox.setValue(sd.getVariance());
				textbox.setParent(row);
			}
			setRemarksBox(row, sd.getRemarksId());
		}

	}

	private void setRemarksBox(Row row, String fieldName) {
		fieldName = StringUtils.trimToEmpty(fieldName);
		fieldName = fieldName.toUpperCase();
		Textbox textbox = getTextbox();
		textbox.setReadonly(false);
		
		if (this.enqiryModule) {
			textbox.setReadonly(true);
		}

		textbox.setId(fieldName);
		textbox.setParent(row);
		textbox.setWidth("300px");
		textbox.setMaxlength(500);

		Object value = this.sampling.getReamrksMap().get(fieldName);
		if (value != null) {
			textbox.setValue(value.toString());
		}

		remarks.add(fieldName);
	}

	private Textbox getRightAlignedTextbox() {
		Textbox textbox = getTextbox();
		textbox.setStyle("text-align:right;");
		return textbox;
	}

	private Textbox getTextbox() {
		Textbox textbox = new Textbox();
		textbox.setReadonly(true);
		textbox.setWidth("120px");
		return textbox;
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave(FinanceDetail financeDetail, Tab tab, boolean recSave, Radiogroup userAction) {
		logger.debug(Literal.ENTERING);

		if (financeDetail.getSampling() == null) {
			return;
		}

		this.recSave = recSave;
		this.userAction = userAction;
		doClearMessage();

		// force validation, if on, than execute by component.getValue()
		if (!recSave) {
			doSetValidation();
		}
		// fill the Sampling object with the components data
		List<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);

		this.sampling.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		this.sampling.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		this.sampling.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		financeDetail.setSampling(this.sampling);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(List<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

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

		logger.debug("Leaving");
	}

	/**
	 * Method for Getting Selected value From ComboBox
	 * 
	 * @param combobox
	 * @return
	 */
	public String getComboboxValue(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param sampling
	 */
	public List<WrongValueException> doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			int decision = Integer.parseInt(getComboboxValue(this.samplingDecision).toString());
			sampling.setDecision(decision);
			if (decision == 0 && !this.recSave) {
				throw new WrongValueException(this.samplingDecision,
						Labels.getLabel("STATIC_INVALID", new String[] { "Decision should be mandatory" }));
			}
			if (sampling.getDecision() == Decision.RESUBMIT.getKey()
					&& !userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_SAVED)) {
				throw new WrongValueException(this.samplingDecision,
						"Sampling Resubmit is allowed only when user action is save");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			sampling.setRemarks(this.samplingRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isNotEmpty(this.samplingResubmitReason.getValue())) {
				sampling.setResubmitReason(
						Long.parseLong(this.samplingResubmitReason.getAttribute("reasonId").toString()));
			} else {
				sampling.setResubmitReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			sampling.setRecommendedAmount(PennantAppUtil.unFormateAmount(this.samplingFinalRcmdAmt.getActualValue(),
					sampling.getCcyeditfield()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		setRemarks();
		doRemoveValidation();

		logger.debug(Literal.LEAVING);
		return wve;
	}

	private void setRemarks() {
		for (String field : this.remarks) {
			org.zkoss.zk.ui.Component component = rows_Sampling.getFellowIfAny(field);
			if (component != null) {
				sampling.getReamrksMap().put(field, ((Textbox) component).getValue());
			}
		}
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.samplingDecision.setConstraint("");
		this.samplingRemarks.setConstraint("");
		this.samplingResubmitReason.setConstraint("");
		this.samplingFinalRcmdAmt.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.samplingDecision.setErrorMessage("");
		this.samplingRemarks.setErrorMessage("");
		this.samplingResubmitReason.setErrorMessage("");
		this.samplingFinalRcmdAmt.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		boolean reasonMandatory = false;

		if (this.samplingDecision.getValue().equals(Decision.RESUBMIT.getValue())) {
			reasonMandatory = true;
		}

		setValidationOn(true);
		if (!this.samplingDecision.isDisabled()) {
			this.samplingDecision.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinSampling_Decision.value"), null, true));
		}

		if (!this.samplingResubmitReason.isReadonly()) {
			this.samplingResubmitReason.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinSampling_ResubmitReason.value"), null, reasonMandatory));
		}

		if (!this.samplingFinalRcmdAmt.isReadonly()) {
			this.samplingFinalRcmdAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinSampling_FinalRcmdAmt.value"), ccyFormatter, true, false));
		}

		setRemarksConstraint(this.samplingRemarks);

		logger.debug(Literal.LEAVING);
	}

	private void setRemarksConstraint(Textbox component) {
		if (!component.isReadonly()) {
			this.samplingRemarks.setConstraint(new PTStringValidator(Labels.getLabel("label_FinSampling_Remarks.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Sampling getVerification() {
		return sampling;
	}

	public void setVerification(Sampling verification) {
		this.sampling = verification;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
}
