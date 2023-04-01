package com.pennant.webui.finance.financemain.stepfinance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class StepDetailDialogCtrl extends GFCBaseCtrl<StepPolicyHeader> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(StepDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_StepDetailDialog; // autoWired

	// Finance Step Details Tab
	protected Label stepDetail_finType; // autoWired
	protected Label stepDetail_finCcy; // autoWired
	protected Label stepDetail_scheduleMethod; // autoWired
	protected Label stepDetail_profitDaysBasis; // autoWired
	protected Label stepDetail_finReference; // autoWired
	protected Label stepDetail_grcEndDate; // autoWired
	protected Label label_StepDetailDialog_FinType; // autoWired
	protected Label label_StepDetailDialog_GrcEndDate; // autoWired

	protected Listbox listBoxStepdetails; // autoWired
	protected Button btnNew_FinStepPolicy; // autoWired
	protected Listbox listBoxStepdetailsforGrace;
	protected Button btnNew_FinStepPolicyGrace;
	// Step Finance Details
	protected ExtendedCombobox stepPolicy;
	protected Label label_FinanceMainDialog_StepPolicy;
	protected Label label_FinanceMainDialog_numberOfSteps;
	protected Checkbox alwManualSteps;
	protected Intbox noOfSteps;
	protected Space space_StepPolicy;
	protected Space space_noOfSteps;
	protected Hbox hbox_numberOfSteps;
	protected Combobox stepType;
	protected Space space_stepType;
	protected transient String oldVar_stepPolicy;
	protected transient boolean oldVar_alwManualSteps;
	protected transient int oldVar_noOfSteps;
	protected transient int oldVar_stepType;
	protected transient int oldVar_noOfGrcSteps;
	protected Groupbox gb_grace;
	protected Groupbox gb_emi;
	protected Intbox grcSteps;

	// not auto wired variables
	private FinanceDetail financeDetail = null;
	private FinScheduleData finScheduleData = null;
	private FinanceMain financeMain = null;
	private transient StepPolicyService stepPolicyService;

	private Object financeMainDialogCtrl = null;
	private boolean isWIF = false;
	public List<FinanceStepPolicyDetail> finStepPolicyList = null;

	private String roleCode = "";
	private boolean allowedManualSteps = false;
	private int ccyFormatter = 0;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;
	protected Listheader listheader_StepFinance_EMIStepPercent;
	protected Listheader listheader_StepFinance_SteppedEMI;
	protected Listheader listheader_StepFinance_EMIDiff;
	private boolean dataChanged = false;
	private boolean stepReadonly = false;
	protected Combobox stepsAppliedFor;
	protected Space space_stepsAppliedFor;
	protected Label label_FinanceTypeDialog_StepsAppliedFor;
	protected Combobox calcOfSteps;
	protected Space space_calcOfSteps;
	protected Label label_FinanceTypeDialog_CalcOfSteps;
	protected transient String oldVar_calcOfSteps;
	protected transient String oldVar_stepsAppliedFor;
	private boolean alwGrace = false;
	private boolean allowGrace = false;
	private boolean isFinAlwGrace = false;

	private boolean isEnquiry = false;
	private List<Object> finHeaderList = null;

	/**
	 * default constructor.<br>
	 */
	public StepDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "StepDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_StepDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_StepDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}

		if (arguments.containsKey("enquiryModule")) {
			isEnquiry = (Boolean) arguments.get("enquiryModule");
		}

		if (arguments.containsKey("roleCode")) {
			roleCode = (String) arguments.get("roleCode");
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateAuthorities("StepDetailDialog", getRole());
		}

		if (arguments.containsKey("isAlwGrace")) {
			setFinAlwGrace((Boolean) arguments.get("isAlwGrace"));
		}

		if (arguments.containsKey("alwManualSteps")) {
			setAllowedManualSteps((Boolean) arguments.get("alwManualSteps"));
		}

		if (arguments.containsKey("stepReadonly")) {
			stepReadonly = (Boolean) arguments.get("stepReadonly");
		}
		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (Integer) arguments.get("ccyFormatter");
		}

		if (arguments.containsKey("finHeaderList")) {
			finHeaderList = (List<Object>) arguments.get("finHeaderList");
		}

		doShowDialog(this.financeDetail);
		// set Field Properties
		doSetFieldProperties();

		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) {
		logger.debug(Literal.ENTERING);

		try {
			// append finance basic details
			appendFinBasicDetails();
			if (getFinanceMainDialogCtrl() != null) {
				getFinanceMainDialogCtrl().getClass().getMethod("setStepDetailDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Stooping to Enter New Step policies and Allowed only for Maintenance
		if (!isAllowedManualSteps()) {
			this.btnNew_FinStepPolicy.setVisible(false);
		} else {
			if (isWIF) {
				this.btnNew_FinStepPolicy.setVisible(true);
			} else {
				this.btnNew_FinStepPolicy.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNew"));
			}
		}

		getBorderLayoutHeight();

		if (isFinAlwGrace) {
			setAlwGraceChanges(true, true);
		} else {
			setAlwGraceChanges(false, true);
		}

		doEditStep(financeDetail.getFinScheduleData());
		try {
			doWriteBeanToComponents(afinanceDetail, true);
			doFillStepDetais(getFinScheduleData().getStepPolicyDetails());
			doFillStepDetaisForGrace(getFinScheduleData().getStepPolicyDetails());
			doCheckRights();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	protected void doStoreDftSchdValues() {

		doClearMessage();
		// Step Finance Details
		this.oldVar_stepPolicy = this.stepPolicy.getValue();
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();
		this.oldVar_stepType = this.stepType.getSelectedIndex();
		this.oldVar_calcOfSteps = this.calcOfSteps.getValue();
		this.oldVar_stepsAppliedFor = this.stepsAppliedFor.getValue();
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		// Step Finance Field Properties
		this.noOfSteps.setMaxlength(3);
		this.noOfSteps.setStyle("text-align:right;");
		this.grcSteps.setMaxlength(3);
		this.grcSteps.setStyle("text-align:right;");
		this.stepType.setReadonly(true);

		this.stepPolicy.setProperties("StepPolicyHeader", "PolicyCode", "PolicyDesc", true, 8);
		String alwdStepPolicies = financeType.getAlwdStepPolicies();
		if (StringUtils.isEmpty(alwdStepPolicies)) {
			this.stepPolicy.setFilters(null);
		} else {
			String[] alwdStepPolices = StringUtils.trimToEmpty(alwdStepPolicies).split(",");
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("PolicyCode", Arrays.asList(alwdStepPolices), Filter.OP_IN);
			this.stepPolicy.setFilters(filter);
		}

		if (StringUtils.isEmpty(alwdStepPolicies)) {
			this.stepPolicy.setFilters(null);
		} else {
			String[] alwdStepPolices = StringUtils.trimToEmpty(alwdStepPolicies).split(",");
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("PolicyCode", Arrays.asList(alwdStepPolices), Filter.OP_IN);
			this.stepPolicy.setFilters(filter);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	protected void doSetValidation() {
		logger.debug("Entering");

		if (!this.stepPolicy.isReadonly() && !this.alwManualSteps.isChecked()) {
			this.stepPolicy.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_StepPolicy.value"), null, true, true));
		}

		if (!this.noOfSteps.isReadonly() && this.alwManualSteps.isChecked()) {
			this.noOfSteps.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_RepaySteps.value"), false, false, 0, 999));
		}

		if (this.stepsAppliedFor.getSelectedItem().getValue().equals("#")) {
			throw new WrongValueException(this.stepsAppliedFor, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor.value") }));
		}

		if (this.calcOfSteps.getSelectedItem().getValue().equals("#")) {
			throw new WrongValueException(this.calcOfSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceTypeDialog_CalcOfSteps.value") }));
		}

	}

	protected void doCheckRights() {
		if (stepReadonly || isEnquiry) {
			this.stepsAppliedFor.setDisabled(true);
			this.calcOfSteps.setDisabled(true);
			this.stepPolicy.setReadonly(true);
			this.stepType.setDisabled(true);
			this.alwManualSteps.setDisabled(true);
			this.grcSteps.setReadonly(true);
			this.noOfSteps.setReadonly(true);
			this.btnNew_FinStepPolicyGrace.setVisible(false);
			this.btnNew_FinStepPolicy.setVisible(false);
		}
	}

	public ArrayList<ErrorDetail> ValidatePaymentMethod(ArrayList<ErrorDetail> errorList, String cbScheduleMethod,
			String repayRateBasis) {
		String stepTypeVal = this.stepType.getSelectedItem().getValue().toString();
		if (FinanceConstants.STEPTYPE_EMI.equals(stepTypeVal)
				&& !CalculationConstants.SCHMTHD_EQUAL.equals(cbScheduleMethod)) {
			errorList.add(new ErrorDetail("StepFinance", "30703",
					new String[] { Labels.getLabel("label_ScheduleMethod_Equal") }, new String[] {}));
		}
		return errorList;
	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	public void doStoreDftValues() {

		doClearMessage();
		// Step Finance Details
		this.oldVar_stepPolicy = StringUtils.trimToEmpty(this.stepPolicy.getValue());
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();
		this.oldVar_stepType = this.stepType.getSelectedIndex();
		this.oldVar_noOfGrcSteps = this.grcSteps.intValue();
		this.oldVar_calcOfSteps = this.calcOfSteps.getSelectedItem().getValue();
		this.oldVar_stepsAppliedFor = this.stepsAppliedFor.getSelectedItem().getValue();

	}

	public boolean isScdlRegenerate() {

		if (!StringUtils.equals(this.oldVar_stepPolicy, this.stepPolicy.getValue())) {
			this.oldVar_stepPolicy = this.stepPolicy.getValue();
			return true;
		}
		if (this.oldVar_alwManualSteps != this.alwManualSteps.isChecked()) {
			this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
			return true;
		}
		if (this.oldVar_noOfSteps != this.noOfSteps.intValue()) {
			this.oldVar_noOfSteps = this.noOfSteps.intValue();
			return true;
		}
		if (this.oldVar_stepType != this.stepType.getSelectedIndex()) {
			this.oldVar_stepType = this.stepType.getSelectedIndex();
			return true;
		}
		if (this.oldVar_noOfGrcSteps != this.grcSteps.intValue()) {
			this.oldVar_noOfGrcSteps = this.grcSteps.intValue();
			return true;
		}
		if (!this.oldVar_calcOfSteps.equals(this.calcOfSteps.getSelectedItem().getValue())) {
			this.oldVar_calcOfSteps = this.calcOfSteps.getSelectedItem().getValue();
			return true;
		}
		if (!this.oldVar_stepsAppliedFor.equals(this.stepsAppliedFor.getSelectedItem().getValue())) {
			this.oldVar_stepsAppliedFor = this.calcOfSteps.getSelectedItem().getValue();
			return true;
		}
		if (isDataChanged()) {
			return true;
		}
		return false;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData (FinScheduleData)
	 * @param tab
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData, Tab tab, String method) {
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		doClearMessage();
		doSetValidation();

		try {
			aFinanceMain.setStepPolicy(this.stepPolicy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setStepType(this.stepType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setAlwManualSteps(this.alwManualSteps.isChecked());

		try {
			aFinanceMain.setNoOfSteps(this.noOfSteps.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setStepsAppliedFor(this.stepsAppliedFor.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setCalcOfSteps(this.calcOfSteps.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.stepType.isDisabled() && this.alwManualSteps.isChecked()
					&& StringUtils.equals(aFinanceMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_PERC)
					&& StringUtils.equals(aFinanceMain.getStepsAppliedFor(), PennantConstants.STEPPING_APPLIED_EMI)) {
				if (getComboboxValue(this.stepType).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.stepType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_StepType.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setNoOfGrcSteps(this.grcSteps.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (tab != null) {
				tab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}

		doStoreDftValues();
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.noOfSteps.setErrorMessage("");
		this.stepType.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public void doEditStep(FinScheduleData finScheduleData) {
		this.gb_grace.setVisible(false);
		this.gb_emi.setVisible(false);
		this.grcSteps.setReadonly(true);

		FinanceType ft = finScheduleData.getFinanceType();
		boolean stepFinance = financeMain.isStepFinance();

		String stepAppliedFor = stepFinance ? financeMain.getStepsAppliedFor() : ft.getStepsAppliedFor();

		if (StringUtils.isNotEmpty(stepAppliedFor)) {
			setStepAppliedForRights(stepAppliedFor);
		}

		String calcOfSteps = StringUtils.trimToEmpty(stepFinance ? financeMain.getCalcOfSteps() : ft.getCalcOfSteps());
		setCalcOfStepsRights(calcOfSteps, ft.isAlwManualSteps());

	}

	private void setStepAppliedForRights(String stepAppliedFor) {
		switch (stepAppliedFor) {
		case PennantConstants.STEPPING_APPLIED_GRC:
			this.gb_grace.setVisible(true);
			this.noOfSteps.setReadonly(true);
			if (financeMain.isAllowGrcPeriod()) {
				this.btnNew_FinStepPolicyGrace
						.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNewGraceStep"));
				this.grcSteps.setReadonly(!getUserWorkspace().isAllowed("StepDetailDialog_noOfGrcSteps"));
			}
			break;
		case PennantConstants.STEPPING_APPLIED_EMI:
			this.gb_emi.setVisible(true);
			break;
		case PennantConstants.STEPPING_APPLIED_BOTH:
			this.gb_grace.setVisible(true);
			this.gb_emi.setVisible(true);
			if (financeMain.isAllowGrcPeriod()) {
				this.btnNew_FinStepPolicyGrace
						.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNewGraceStep"));
				this.grcSteps.setReadonly(!getUserWorkspace().isAllowed("StepDetailDialog_noOfGrcSteps"));
			}

			break;
		}
	}

	private void setCalcOfStepsRights(String calcOfSteps, boolean alwManualSteps) {
		switch (calcOfSteps) {
		case PennantConstants.STEPPING_CALC_AMT:
			this.listheader_StepFinance_SteppedEMI.setVisible(true);
			this.listheader_StepFinance_EMIDiff.setVisible(true);
			this.listheader_StepFinance_EMIStepPercent.setVisible(false);

			this.stepPolicy.setMandatoryStyle(false);
			this.stepPolicy.setReadonly(true);
			this.stepType.setDisabled(true);
			this.alwManualSteps.setChecked(true);
			this.alwManualSteps.setDisabled(true);
			// this.space_noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.btnNew_FinStepPolicy.setVisible(true);

			break;
		case PennantConstants.STEPPING_CALC_PERC:
			this.listheader_StepFinance_SteppedEMI.setVisible(false);
			this.listheader_StepFinance_EMIDiff.setVisible(false);
			this.listheader_StepFinance_EMIStepPercent.setVisible(true);

			this.stepPolicy.setReadonly(false);
			this.stepType.setDisabled(false);
			this.alwManualSteps.setChecked(false);
			this.alwManualSteps.setDisabled(false);
			if (financeMain.isAlwManualSteps()) {
				this.stepPolicy.setReadonly(true);
			} else {
				this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
				this.hbox_numberOfSteps.setVisible(false);
				this.space_noOfSteps.setSclass("");
			}
			if (!alwManualSteps) {
				this.alwManualSteps.setDisabled(true);
			}

			break;
		default:

			this.stepPolicy.setMandatoryStyle(false);
			this.stepPolicy.setReadonly(true);
			this.stepType.setDisabled(true);
			this.alwManualSteps.setChecked(true);
			this.alwManualSteps.setDisabled(true);
			// this.space_noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.noOfSteps.setReadonly(!getUserWorkspace().isAllowed("StepDetailDialog_noOfSteps"));
			this.btnNew_FinStepPolicy.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNew"));
			break;
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	protected void doReadOnly() {

		// Step Finance Fields
		this.stepPolicy.setReadonly(true);
		this.alwManualSteps.setDisabled(true);
		this.noOfSteps.setReadonly(true);
		this.stepType.setDisabled(true);
		this.btnNew_FinStepPolicy.setVisible(false);
		this.btnNew_FinStepPolicyGrace.setVisible(false);
		this.grcSteps.setReadonly(true);
		this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
		this.hbox_numberOfSteps.setVisible(true);
		this.btnNew_FinStepPolicy.setVisible(true);
	}

	public void onChange$calcOfSteps(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		visibilityFieldsForCalcOfSteps();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void visibilityFieldsForCalcOfSteps() {

		this.gb_grace.setVisible(false);
		this.gb_emi.setVisible(false);
		this.grcSteps.setReadonly(true);
		this.noOfSteps.setReadonly(false);
		this.listBoxStepdetails.getItems().clear();
		this.listBoxStepdetailsforGrace.getItems().clear();
		if (CollectionUtils.isNotEmpty(finStepPolicyList)) {
			this.finStepPolicyList.clear();
		}

		String stepAppliedFor = getComboboxValue(this.stepsAppliedFor);
		setStepAppliedForRights(stepAppliedFor);

		if (!stepAppliedFor.equals(PennantConstants.STEPPING_APPLIED_EMI)) {
			allowGrace = setAlwGraceChanges(false, false);
			if (allowGrace) {
				this.btnNew_FinStepPolicyGrace.setVisible(true);
				this.grcSteps.setReadonly(false);
			}
		}

		String calcOfSteps = getComboboxValue(this.calcOfSteps);
		setCalcOfStepsRights(calcOfSteps, true);

		visibiltyForPerc(true);

		if (calcOfSteps.equals(PennantConstants.STEPPING_CALC_AMT)) {
			this.stepsAppliedFor.setDisabled(false);
		}
		financeMain.setCalcOfSteps(this.calcOfSteps.getSelectedItem().getValue().toString());
	}

	private void visibiltyForPerc(boolean isChange) {
		FinScheduleData schData = getFinanceDetail().getFinScheduleData();

		String calcOfSteps = getComboboxValue(this.calcOfSteps);
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");

		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");

		if (this.alwManualSteps.isChecked()) {
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.space_noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setValue(0);
			this.stepType.setVisible(true);
			this.stepType.setSclass(PennantConstants.mandateSclass);
			this.btnNew_FinStepPolicy.setVisible(true);
			this.stepPolicy.setValue("", "");
			this.stepPolicy.setReadonly(true);
			this.space_stepType.setSclass(PennantConstants.mandateSclass);
			this.stepType.setDisabled(false);
			if (isChange && calcOfSteps.equals(PennantConstants.STEPPING_CALC_PERC)) {
				List<StepPolicyDetail> policyList = new ArrayList<StepPolicyDetail>();
				if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
					policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
				}
				schData.resetStepPolicyDetails(policyList);
				doFillStepDetais(schData.getStepPolicyDetails());
				fillComboBox(this.stepType, FinanceConstants.STEPTYPE_EMI, PennantStaticListUtil.getStepType(), "");
			} else {
				fillComboBox(this.stepType, schData.getFinanceMain().getStepType(), PennantStaticListUtil.getStepType(),
						"");
			}
			if (StringUtils.equals(calcOfSteps, PennantConstants.STEPPING_CALC_AMT)) {
				fillComboBox(this.stepType, PennantConstants.List_Select, PennantStaticListUtil.getStepType(), "");
				this.stepType.setDisabled(true);
			}
		} else {

			if (this.financeDetail.isNewRecord()) {
				this.stepPolicy.setValue(schData.getFinanceType().getDftStepPolicy());
				this.stepPolicy.setDescription(schData.getFinanceType().getLovDescDftStepPolicyName());
				this.stepType.setValue(schData.getFinanceType().getDftStepPolicyType());
				fillComboBox(this.stepType, schData.getFinanceType().getDftStepPolicyType(),
						PennantStaticListUtil.getStepType(), "");
			} else {
				this.stepPolicy.setValue(schData.getFinanceMain().getStepPolicy());
				this.stepPolicy.setDescription(schData.getFinanceMain().getLovDescStepPolicyName());
				fillComboBox(this.stepType, schData.getFinanceMain().getStepType(), PennantStaticListUtil.getStepType(),
						"");
			}
			this.stepPolicy.setMandatoryStyle(true);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
			this.space_noOfSteps.setSclass("");
			this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
			this.stepType.setReadonly(isReadOnly("FinanceMainDialog_stepType"));
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);
			this.alwManualSteps.setVisible(true);
			if (StringUtils.isEmpty(this.stepPolicy.getValue())) {
				this.stepPolicy.setProperties("StepPolicyHeader", "PolicyCode", "PolicyDesc", true, 8);
				this.stepPolicy.setFilters(null);
			}

		}

		setAllowedManualSteps(this.alwManualSteps.isChecked());

		// Filling Step Policy Details List
		if ((isChange || financeMain.isStepFinance()) && calcOfSteps.equals(PennantConstants.STEPPING_CALC_PERC)) {
			if (financeMain.isStepFinance() && this.alwManualSteps.isChecked() && this.financeDetail.isNewRecord()) {
				fillComboBox(this.stepType, FinanceConstants.STEPTYPE_EMI, PennantStaticListUtil.getStepType(), "");
			}
			List<StepPolicyDetail> policyList = new ArrayList<>();

			if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
				policyList = stepPolicyService.getStepPolicyDetailsById(this.stepPolicy.getValue());
			}

			List<FinanceStepPolicyDetail> tfspd = schData.getStepPolicyDetails();

			if (CollectionUtils.isNotEmpty(policyList)) {
				schData.resetStepPolicyDetails(policyList);
			}

			schData.getStepPolicyDetails().forEach(l1 -> {
				l1.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
				for (FinanceStepPolicyDetail fspd : tfspd) {
					if (l1.getStepNo() == fspd.getStepNo()) {
						l1.setInstallments(fspd.getInstallments());
					}
				}
			});

			doFillStepDetais(schData.getStepPolicyDetails());
		}

	}

	public void onChange$stepsAppliedFor(Event event) {
		logger.debug("Entering : " + event.toString());
		String stepsAppliedFor = getComboboxValue(this.stepsAppliedFor);
		visibilityFieldsForStepApplied(stepsAppliedFor, PennantConstants.List_Select, false);
		logger.debug("Leaving : " + event.toString());
	}

	private void visibilityFieldsForStepApplied(String stepsAppliedFor, String calcOfStep, boolean alwManualStep) {

		this.grcSteps.setValue(0);
		visibilityFieldsForCalcOfSteps();
		this.stepPolicy.setValue("", "");
		if ((PennantConstants.STEPPING_APPLIED_GRC.equals(stepsAppliedFor))
				|| PennantConstants.STEPPING_APPLIED_BOTH.equals(stepsAppliedFor)) {
			this.calcOfSteps.setDisabled(true);
			fillComboBox(this.calcOfSteps, PennantConstants.STEPPING_CALC_AMT,
					PennantStaticListUtil.getCalcOfStepsList(), "");
			this.alwManualSteps.setChecked(true);
			this.alwManualSteps.setDisabled(true);
			this.stepPolicy.setReadonly(true);
			this.stepType.setDisabled(true);
		} else {
			this.calcOfSteps.setDisabled(false);
			this.space_calcOfSteps.setSclass(PennantConstants.mandateSclass);
			fillComboBox(this.calcOfSteps, calcOfStep, PennantStaticListUtil.getCalcOfStepsList(), "");
			this.alwManualSteps.setChecked(alwManualStep);
			this.alwManualSteps.setDisabled(false);
		}
		financeMain.setCalcOfSteps(this.calcOfSteps.getSelectedItem().getValue().toString());
	}

	/**
	 * when clicks on button "Step Policy Detail"
	 * 
	 * @param event
	 */
	public void onFulfill$stepPolicy(Event event) {
		logger.debug("Entering " + event.toString());

		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");
		this.stepType.setConstraint("");
		this.stepPolicy.clearErrorMessage();
		this.noOfSteps.setErrorMessage("");
		this.stepType.setErrorMessage("");

		Object dataObject = stepPolicy.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.stepPolicy.setValue(dataObject.toString());
				this.stepPolicy.setDescription("");
			}
			fillComboBox(this.stepType, PennantConstants.List_Select, PennantStaticListUtil.getStepType(), "");
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();
			doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
		} else {
			StepPolicyHeader detail = (StepPolicyHeader) dataObject;
			if (detail != null) {
				this.stepPolicy.setValue(detail.getPolicyCode(), detail.getPolicyDesc());
				fillComboBox(this.stepType, detail.getStepType(), PennantStaticListUtil.getStepType(), "");
				// Fetch Step Policy Details List
				List<StepPolicyDetail> policyList = getStepPolicyService()
						.getStepPolicyDetailsById(this.stepPolicy.getValue());
				this.noOfSteps.setValue(policyList.size());
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
				List<FinanceStepPolicyDetail> policyDetails = getFinanceDetail().getFinScheduleData()
						.getStepPolicyDetails();
				for (FinanceStepPolicyDetail financeStepPolicyDetail : policyDetails) {
					financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
				}
				doFillStepDetais(policyDetails);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void doStepPolicyCheck() {

		this.stepPolicy.setMandatoryStyle(false);
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");
		this.stepPolicy.setValue("", "");

		FinanceType type = getFinanceDetail().getFinScheduleData().getFinanceType();

		this.label_FinanceMainDialog_StepPolicy.setVisible(true);
		if (!StringUtils.trimToEmpty(type.getDftStepPolicy()).equals(PennantConstants.List_Select)) {
			this.stepPolicy.setValue(type.getDftStepPolicy(), type.getLovDescDftStepPolicyName());
		}

		if (PennantConstants.STEPPING_APPLIED_EMI.equals(type.getStepsAppliedFor())) {
			fillComboBox(this.stepType, type.getDftStepPolicyType(), PennantStaticListUtil.getStepType(), "");
			this.space_stepType.setSclass("");
		}

		if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
			List<StepPolicyDetail> policyList = getStepPolicyService()
					.getStepPolicyDetailsById(this.stepPolicy.getValue());
			getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails();
			List<FinanceStepPolicyDetail> stepPolicyDetails = getFinanceDetail().getFinScheduleData()
					.getStepPolicyDetails();
			for (FinanceStepPolicyDetail financeStepPolicyDetail : stepPolicyDetails) {
				financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
			}
			doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());

			this.alwManualSteps.setChecked(false);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
		}

	}

	/*
	 * onCheck Event For Manual Steps Check Box
	 */
	public void onCheck$alwManualSteps(Event event) {
		logger.debug("Entering : " + event.toString());
		doAlwManualStepsCheck(true);
		logger.debug("Leaving : " + event.toString());
	}

	private void doAlwManualStepsCheck(boolean isAction) {
		FinScheduleData schData = getFinanceDetail().getFinScheduleData();
		FinanceType financeType = schData.getFinanceType();

		String calOfSteps = "";
		calOfSteps = financeMain.isStepFinance() ? financeMain.getCalcOfSteps() : financeType.getCalcOfSteps();
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");

		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");

		if (this.alwManualSteps.isChecked()) {
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.space_noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setReadonly(!getUserWorkspace().isAllowed("StepDetailDialog_noOfSteps"));
			this.noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setValue(0);
			this.stepType.setVisible(true);
			this.stepType.setSclass(PennantConstants.mandateSclass);
			this.btnNew_FinStepPolicy.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNew"));
			this.stepPolicy.setValue("", "");
			this.stepPolicy.setReadonly(true);
			this.space_stepType.setSclass(PennantConstants.mandateSclass);
			this.stepType.setDisabled(false);
			if (isAction) {
				List<StepPolicyDetail> policyList = new ArrayList<StepPolicyDetail>();
				if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
					policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
				}
				schData.resetStepPolicyDetails(policyList);
				doFillStepDetais(schData.getStepPolicyDetails());
				fillComboBox(this.stepType, FinanceConstants.STEPTYPE_EMI, PennantStaticListUtil.getStepType(), "");
			} else {
				fillComboBox(this.stepType, schData.getFinanceMain().getStepType(), PennantStaticListUtil.getStepType(),
						"");
			}
			if (StringUtils.equals(calOfSteps, PennantConstants.STEPPING_CALC_AMT)) {
				if (getComboboxValue(this.calcOfSteps).equals(PennantConstants.STEPPING_CALC_AMT)) {
					fillComboBox(this.stepType, PennantConstants.List_Select, PennantStaticListUtil.getStepType(), "");
					this.stepType.setDisabled(true);
				}
			}
			this.stepsAppliedFor.setDisabled(true);
			this.calcOfSteps.setDisabled(false);
		} else {

			if (this.financeDetail.isNewRecord()) {
				this.stepPolicy.setValue(schData.getFinanceType().getDftStepPolicy());
				this.stepPolicy.setDescription(schData.getFinanceType().getLovDescDftStepPolicyName());
				this.stepType.setValue(schData.getFinanceType().getDftStepPolicyType());
				fillComboBox(this.stepType, schData.getFinanceType().getDftStepPolicyType(),
						PennantStaticListUtil.getStepType(), "");
			} else {
				this.stepPolicy.setValue(schData.getFinanceMain().getStepPolicy());
				this.stepPolicy.setDescription(schData.getFinanceMain().getLovDescStepPolicyName());
				fillComboBox(this.stepType, schData.getFinanceMain().getStepType(), PennantStaticListUtil.getStepType(),
						"");
			}
			this.stepPolicy.setMandatoryStyle(true);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
			this.space_noOfSteps.setSclass("");
			this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
			this.stepType.setReadonly(isReadOnly("FinanceMainDialog_stepType"));
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);
			this.stepsAppliedFor.setDisabled(true);
			this.calcOfSteps.setDisabled(true);
			if (isReadOnly("FinanceMainDialog_alwManualSteps")) {
				this.alwManualSteps.setVisible(false);
			}

		}

		setAllowedManualSteps(this.alwManualSteps.isChecked());

		// Filling Step Policy Details List
		if (isAction || financeMain.isStepFinance()) {
			if (financeMain.isStepFinance() && this.alwManualSteps.isChecked() && this.financeDetail.isNewRecord()) {
				fillComboBox(this.stepType, FinanceConstants.STEPTYPE_EMI, PennantStaticListUtil.getStepType(), "");
			}
			List<StepPolicyDetail> policyList = new ArrayList<>();

			if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
				policyList = stepPolicyService.getStepPolicyDetailsById(this.stepPolicy.getValue());
			}

			List<FinanceStepPolicyDetail> tfspd = schData.getStepPolicyDetails();

			if (CollectionUtils.isNotEmpty(policyList)) {
				schData.resetStepPolicyDetails(policyList);
			}

			schData.getStepPolicyDetails().forEach(l1 -> {
				l1.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
				for (FinanceStepPolicyDetail fspd : tfspd) {
					if (l1.getStepNo() == fspd.getStepNo()) {
						l1.setInstallments(fspd.getInstallments());
					}
				}
			});

			doFillStepDetais(schData.getStepPolicyDetails());
		}
	}

	/**
	 * Method for getting selected Step Type Using In Step Detail Dialog Controller for EMI Validation
	 * 
	 * @return
	 */
	public String getStepType() {
		this.stepType.setConstraint("");
		this.stepType.setErrorMessage("");
		return getComboboxValue(this.stepType);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException,
			InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String calOfSteps = "";
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		calOfSteps = financeMain.isStepFinance() ? financeMain.getCalcOfSteps() : financeType.getCalcOfSteps();
		if (aFinanceMain.isStepFinance()) {
			this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
		} else {
			if (PennantConstants.STEPPING_CALC_AMT.equals(calOfSteps)) {
				this.alwManualSteps.setChecked(true);
			}
		}

		this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		if (PennantConstants.STEPPING_CALC_PERC.equals(calOfSteps)) {
			this.stepPolicy.setVisible(true);
			this.stepPolicy.setSclass(PennantConstants.mandateSclass);
		} else {
			this.stepPolicy.setVisible(true);
		}

		fillComboBox(this.stepType, aFinanceMain.getStepType(), PennantStaticListUtil.getStepType(), "");
		if (PennantConstants.STEPPING_CALC_PERC.equals(calOfSteps)) {
			doAlwManualStepsCheck(false);
			this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		}
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.stepPolicy.setDescription(aFinanceMain.getLovDescStepPolicyName());
		this.grcSteps.setValue(aFinanceMain.getNoOfGrcSteps());
		doSetStepFieldsData(financeType, aFinanceMain);
		doStoreDftValues();
		if (!aFinanceMain.isStepFinance()) {
			doStepPolicyCheck();
		}
	}

	public void doSetStepFieldsData(FinanceType financeType, FinanceMain financeMain) {
		boolean isNewFinance;
		isNewFinance = !financeMain.isStepFinance();
		String calcOfSteps = "";
		String stepsAppliedFor = "";
		boolean alwManualSteps = false;
		if (financeMain.getCalcOfSteps() == null || financeMain.getStepsAppliedFor() == null) {
			calcOfSteps = financeType.getCalcOfSteps();
			stepsAppliedFor = financeType.getStepsAppliedFor();
			alwManualSteps = financeType.isAlwManualSteps();
		} else {
			calcOfSteps = financeMain.getCalcOfSteps();
			stepsAppliedFor = financeMain.getStepsAppliedFor();
			alwManualSteps = financeMain.isAlwManualSteps();
		}
		fillComboBox(this.calcOfSteps, calcOfSteps, PennantStaticListUtil.getCalcOfStepsList(), "");
		fillComboBox(this.stepsAppliedFor, stepsAppliedFor, PennantStaticListUtil.getStepsAppliedFor(), "");
		if (!stepsAppliedFor.equals(PennantConstants.STEPPING_APPLIED_EMI)) {
			this.calcOfSteps.setDisabled(true);
		}
		if (calcOfSteps.equals(PennantConstants.STEPPING_CALC_AMT)) {
			this.stepsAppliedFor.setDisabled(false);
			this.calcOfSteps.setDisabled(true);
		}
		if (!isNewFinance) {
			this.alwManualSteps.setChecked(financeMain.isAlwManualSteps());
		}
		if (!alwManualSteps) {
			this.calcOfSteps.setDisabled(true);
			this.stepsAppliedFor.setDisabled(true);
			this.alwManualSteps.setDisabled(true);

		}
	}

	/**
	 * Method for Validate Finance Step Policy Details either Entered manually or fetching from Existing Step Policies
	 * 
	 * @param totalTerms
	 * @param isAlwManualSteps
	 * @return
	 */
	public List<ErrorDetail> doValidateStepDetails(FinanceMain financeMain, int totalTerms, boolean isAlwManualSteps,
			int noOfSteps, String stepType) {
		logger.debug("Entering");

		List<ErrorDetail> errorList = new ArrayList<ErrorDetail>();
		if (this.finStepPolicyList != null && !finStepPolicyList.isEmpty()) {

			if (isAlwManualSteps && noOfSteps != finStepPolicyList.size()) {
				errorList.add(new ErrorDetail("30542", PennantConstants.KEY_SEPERATOR,
						new String[] { Labels.getLabel("label_FinanceMainDialog_RepaySteps.value"),
								Labels.getLabel("label_FinanceMainDialog_RepaySteps.value") }));
			} else {

				int sumInstallments = 0;
				BigDecimal sumTenurePerc = BigDecimal.ZERO;

				BigDecimal calTotTenorSplit = BigDecimal.ZERO;
				BigDecimal calTotEmiStepPercent = BigDecimal.ZERO;
				int calTotTerms = 0;
				boolean hadZeroInstStep = false;

				for (int i = 0; i < finStepPolicyList.size(); i++) {
					FinanceStepPolicyDetail stepPolicy = finStepPolicyList.get(i);

					if (stepPolicy.getInstallments() > 0 && isAlwManualSteps) {

						BigDecimal tenurePerc = (new BigDecimal(stepPolicy.getInstallments())
								.multiply(new BigDecimal(100))).divide(new BigDecimal(totalTerms), 2,
										RoundingMode.HALF_DOWN);
						stepPolicy.setTenorSplitPerc(tenurePerc);
						sumTenurePerc = sumTenurePerc.add(tenurePerc);
						sumInstallments = sumInstallments + stepPolicy.getInstallments();
						if (i == (finStepPolicyList.size() - 1) && sumInstallments == totalTerms) {
							if (sumTenurePerc.compareTo(new BigDecimal(100)) != 0) {
								stepPolicy.setTenorSplitPerc(stepPolicy.getTenorSplitPerc().add(new BigDecimal(100))
										.subtract(sumTenurePerc));
							}
						}

					} else if (stepPolicy.getTenorSplitPerc().compareTo(BigDecimal.ZERO) > 0) {

						BigDecimal terms = stepPolicy.getTenorSplitPerc().multiply(new BigDecimal(totalTerms))
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
						sumInstallments = sumInstallments + Integer.parseInt(terms.toString());
						stepPolicy.setInstallments(Integer.parseInt(terms.toString()));
						if (i == (finStepPolicyList.size() - 1)) {
							if (sumInstallments != totalTerms) {
								stepPolicy.setInstallments(stepPolicy.getInstallments() + totalTerms - sumInstallments);
							}
						}
						sumTenurePerc = sumTenurePerc.add(stepPolicy.getTenorSplitPerc());
					}

					if (stepPolicy.getInstallments() == 0) {
						hadZeroInstStep = true;
					}

					calTotTerms = calTotTerms + stepPolicy.getInstallments();
					calTotTenorSplit = calTotTenorSplit.add(stepPolicy.getTenorSplitPerc());
					calTotEmiStepPercent = calTotEmiStepPercent.add(stepPolicy.getEmiSplitPerc());

					// Setting Bean Property Field Details
					if (StringUtils.isBlank(stepPolicy.getRecordType())) {
						stepPolicy.setVersion(stepPolicy.getVersion() + 1);
						stepPolicy.setRecordType(PennantConstants.RCD_ADD);
						stepPolicy.setNewRecord(true);
					}
					stepPolicy.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					stepPolicy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					stepPolicy.setUserDetails(getUserWorkspace().getLoggedInUser());
				}

				doFillStepDetais(finStepPolicyList);

				// If Any Step Policy have Zero installments while on Calculation
				if (hadZeroInstStep) {
					errorList.add(new ErrorDetail("30569", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_MinInstallment"), " 1 " }));
				}

				// Tenor Percentage Validation for Step Policy Details
				if (calTotTenorSplit.compareTo(new BigDecimal(100)) != 0) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TenorSplitPerc"), "100.00 %" }));
				}

				// Average EMI Percentage/ Total Percentage based on Step Type Validation for Step Policy Details
				if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)) {
					BigDecimal emiStepPercAvg = calTotEmiStepPercent.divide(new BigDecimal(finStepPolicyList.size()), 0,
							RoundingMode.HALF_DOWN);
					if (emiStepPercAvg.compareTo(new BigDecimal(100)) != 0) {
						errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
								new String[] { Labels.getLabel("label_AvgEMISplitPerc"), "100.00 %" }));
					}
				} else if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_PRIBAL)) {
					if (calTotEmiStepPercent.compareTo(new BigDecimal(100)) != 0) {
						errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
								new String[] { Labels.getLabel("label_OutStandingPrincipalSplitPerc"), "100.00 %" }));
					}
				}
			}
		} else {
			if (isAlwManualSteps) {
				errorList.add(new ErrorDetail("30541", PennantConstants.KEY_SEPERATOR, new String[] {}));
			}
		}
		logger.debug("Leaving");
		return errorList;
	}

	public List<ErrorDetail> doValidateStepDetails(FinanceMain financeMain, int totalTerms, String specifier,
			String stepAppliedOn) {
		logger.debug(Literal.ENTERING);

		boolean isAlwManualSteps = this.alwManualSteps.isChecked();
		this.noOfSteps.setErrorMessage("");
		int noOfSteps = 0;
		int stepCount = 0;
		financeMain.setRpyStps(false);
		financeMain.setGrcStps(false);
		String stepType = this.stepType.getSelectedItem().getValue();
		String calculatedOn = "";
		int repaySteps = 0;
		int grcSteps = 0;
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		calculatedOn = financeMain.isStepFinance() ? financeMain.getCalcOfSteps() : financeType.getCalcOfSteps();
		List<ErrorDetail> errorList = new ArrayList<ErrorDetail>();

		if (CollectionUtils.isNotEmpty(finStepPolicyList)) {

			List<FinanceStepPolicyDetail> graceSpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> rpySpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> spdList = new ArrayList<>();
			int rpyTerms = 0;
			int grcTerms = 0;

			for (FinanceStepPolicyDetail spd : finStepPolicyList) {
				if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
					repaySteps = repaySteps + 1;
					rpyTerms = rpyTerms + spd.getInstallments();
					rpySpdList.add(spd);
					financeMain.setRpyStps(true);
				} else if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_GRACE)) {
					grcSteps = grcSteps + 1;
					graceSpdList.add(spd);
					grcTerms = grcTerms + spd.getInstallments();
					financeMain.setGrcStps(true);
				}
			}

			String label = "";
			if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(specifier)) {
				spdList = rpySpdList;
				noOfSteps = this.noOfSteps.intValue();
				label = Labels.getLabel("label_FinanceMainDialog_RepaySteps.value");
				stepCount = repaySteps;
			} else {
				spdList = graceSpdList;
				noOfSteps = this.grcSteps.intValue();
				label = Labels.getLabel("label_FinanceMainDialog_GrcSteps.value");
				stepCount = grcSteps;
			}

			if (isAlwManualSteps) {

				if ((StringUtils.equals(stepAppliedOn, PennantConstants.STEPPING_APPLIED_BOTH)
						&& (CollectionUtils.isEmpty(rpySpdList) && CollectionUtils.isEmpty(graceSpdList)))
						|| (StringUtils.equals(stepAppliedOn, PennantConstants.STEPPING_APPLIED_EMI)
								&& CollectionUtils.isEmpty(rpySpdList))
						|| (StringUtils.equals(stepAppliedOn, PennantConstants.STEPPING_APPLIED_GRC)
								&& CollectionUtils.isEmpty(graceSpdList))) {
					errorList.add(new ErrorDetail("30541", PennantConstants.KEY_SEPERATOR, new String[] {}));
					return errorList;
				}
			}

			if (isAlwManualSteps && stepCount != noOfSteps) {
				errorList.add(new ErrorDetail("STP005", PennantConstants.KEY_SEPERATOR, new String[] { label, label }));
				return errorList;
			}

			if (CollectionUtils.isNotEmpty(spdList)) {

				if (isAlwManualSteps && StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_REG_EMI)
						&& totalTerms != rpyTerms) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] {
									Labels.getLabel("label_TotStepInstallments", new String[] { "Repay" }) + " "
											+ String.valueOf(rpyTerms),
									Labels.getLabel("label_TotalTerms") + " " + String.valueOf(totalTerms) }));
					return errorList;
				}

				if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_GRACE) && totalTerms != grcTerms) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] {
									Labels.getLabel("label_TotStepInstallments", new String[] { "Grace" }) + " "
											+ String.valueOf(grcTerms),
									Labels.getLabel("label_GrcTotalTerms") + " " + String.valueOf(totalTerms) }));
					return errorList;
				}

				int sumInstallments = 0;
				BigDecimal sumTenurePerc = BigDecimal.ZERO;

				BigDecimal calTotTenorSplit = BigDecimal.ZERO;
				BigDecimal calTotEmiStepPercent = BigDecimal.ZERO;
				boolean hadZeroInstStep = false;
				BigDecimal tenurePerc = BigDecimal.ZERO;

				for (int i = 0; i < spdList.size(); i++) {
					FinanceStepPolicyDetail stepPolicy = spdList.get(i);

					if (stepPolicy.getInstallments() > 0 && isAlwManualSteps) {
						tenurePerc = (new BigDecimal(stepPolicy.getInstallments()).multiply(new BigDecimal(100)))
								.divide(new BigDecimal(totalTerms), 2, RoundingMode.HALF_DOWN);
						stepPolicy.setTenorSplitPerc(tenurePerc);
						sumTenurePerc = sumTenurePerc.add(tenurePerc);
						sumInstallments = sumInstallments + stepPolicy.getInstallments();
						if (i == (spdList.size() - 1) && sumInstallments == totalTerms) {
							if (sumTenurePerc.compareTo(new BigDecimal(100)) != 0) {
								stepPolicy.setTenorSplitPerc(stepPolicy.getTenorSplitPerc().add(new BigDecimal(100))
										.subtract(sumTenurePerc));
							}
						}

					} else if (stepPolicy.getTenorSplitPerc().compareTo(BigDecimal.ZERO) > 0) {

						BigDecimal terms = stepPolicy.getTenorSplitPerc().multiply(new BigDecimal(totalTerms))
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
						sumInstallments = sumInstallments + Integer.parseInt(terms.toString());
						stepPolicy.setInstallments(Integer.parseInt(terms.toString()));
						if (i == (spdList.size() - 1)) {
							if (sumInstallments != totalTerms) {
								stepPolicy.setInstallments(stepPolicy.getInstallments() + totalTerms - sumInstallments);
							}
						}
						sumTenurePerc = sumTenurePerc.add(stepPolicy.getTenorSplitPerc());
					}

					if (stepPolicy.getInstallments() == 0) {
						hadZeroInstStep = true;
					}

					calTotTenorSplit = calTotTenorSplit.add(stepPolicy.getTenorSplitPerc());

					calTotEmiStepPercent = calTotEmiStepPercent.add(stepPolicy.getEmiSplitPerc());

					// Setting Bean Property Field Details
					if (StringUtils.isBlank(stepPolicy.getRecordType())) {
						stepPolicy.setVersion(stepPolicy.getVersion() + 1);
						stepPolicy.setRecordType(PennantConstants.RCD_ADD);
						stepPolicy.setNewRecord(true);
					}
					stepPolicy.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					stepPolicy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					stepPolicy.setUserDetails(getUserWorkspace().getLoggedInUser());

				}

				if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_REG_EMI)) {
					doFillStepDetais(finStepPolicyList);
				} else if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_GRACE)) {
					doFillStepDetaisForGrace(finStepPolicyList);
				}

				// If Any Step Policy have Zero installments while on
				// Calculation
				if (hadZeroInstStep) {
					errorList.add(new ErrorDetail("30569", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_MinInstallment"), " 1 " }));
					return errorList;
				}

				// Tenor Percentage Validation for Repay Step Policy Details
				if (calTotTenorSplit.compareTo(new BigDecimal(100)) != 0) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TenorSplitPerc"), "100.00 %" }));
					return errorList;
				}

				// Average EMI Percentage/ Total Percentage based on Step Type
				// Validation for Step Policy Details
				if (StringUtils.equals(calculatedOn, PennantConstants.STEPPING_CALC_PERC)) {
					if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)) {
						BigDecimal emiStepPercAvg = calTotEmiStepPercent
								.divide(new BigDecimal(finStepPolicyList.size()), 0, RoundingMode.HALF_DOWN);
						if (emiStepPercAvg.compareTo(new BigDecimal(100)) != 0) {
							errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("label_AvgEMISplitPerc"), "100.00 %" }));
							return errorList;
						}
					} else if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_PRIBAL)) {
						if (calTotEmiStepPercent.compareTo(new BigDecimal(100)) != 0) {
							errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR, new String[] {
									Labels.getLabel("label_OutStandingPrincipalSplitPerc"), "100.00 %" }));
							return errorList;
						}
					}
				}

				if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_GRACE)) {
					errorList.add(new ErrorDetail("STP001", PennantConstants.KEY_SEPERATOR, null));
				}

				if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(specifier)
						&& StringUtils.equals(calculatedOn, PennantConstants.STEPPING_CALC_AMT)) {

					for (FinanceStepPolicyDetail financeStepPolicyDetail : spdList) {
						if (financeStepPolicyDetail.getStepNo() > repaySteps) {
							errorList.add(new ErrorDetail("90405", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("listheader_StepFinanceGrace_StepNo.label") + " "
											+ String.valueOf(financeStepPolicyDetail.getStepNo()) }));
							return errorList;
						} else if (financeStepPolicyDetail.getStepNo() < repaySteps
								&& financeStepPolicyDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) <= 0
								&& !ImplementationConstants.ALLOW_ZERO_STEP_AMOUNT_PERC) {
							errorList.add(new ErrorDetail("91121", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value"),
											String.valueOf(BigDecimal.ZERO) }));
							return errorList;
						} else if (financeStepPolicyDetail.getStepNo() == repaySteps
								&& financeStepPolicyDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) > 0) {
							errorList.add(new ErrorDetail("90277", PennantConstants.KEY_SEPERATOR,
									new String[] {
											"Last " + Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value"),
											String.valueOf(BigDecimal.ZERO) }));
							return errorList;
						}
					}
				}

				if (PennantConstants.STEP_SPECIFIER_GRACE.equals(specifier)) {
					for (FinanceStepPolicyDetail spd : spdList) {
						if (spd.getStepNo() > grcSteps) {
							errorList.add(new ErrorDetail("90405", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("listheader_StepFinanceGrace_StepNo.label") + " "
											+ String.valueOf(spd.getStepNo()) }));
							return errorList;
						} else if (spd.getStepNo() < grcSteps && spd.isAutoCal()) {
							errorList.add(new ErrorDetail("STP004", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("listheader_StepFinanceGrace_StepNo.label") + " "
											+ String.valueOf(spd.getStepNo()) }));
						}
					}
				}

			}
		} else {
			if (isAlwManualSteps) {
				errorList.add(new ErrorDetail("30541", PennantConstants.KEY_SEPERATOR, new String[] {}));
			}
		}
		logger.debug(Literal.LEAVING);
		return errorList;
	}

	/**
	 * Method for Filling Step Policy Details
	 * 
	 * @param finStepPolicyDetails
	 */
	public void doFillStepDetais(List<FinanceStepPolicyDetail> finStepPolicyDetails) {
		logger.debug(Literal.ENTERING);

		Listitem listItem = null;
		Listcell lc = null;

		BigDecimal tenorPerc = BigDecimal.ZERO;
		int totInstallments = 0;
		BigDecimal avgRateMargin = BigDecimal.ZERO;
		BigDecimal avgAplliedRate = BigDecimal.ZERO;
		BigDecimal avgEmiPerc = BigDecimal.ZERO;

		this.listBoxStepdetails.getItems().clear();
		setFinStepPoliciesList(finStepPolicyDetails);
		if (CollectionUtils.isNotEmpty(finStepPolicyDetails)) {
			Comparator<Object> comp = new BeanComparator<Object>("stepNo");
			Collections.sort(finStepPolicyDetails, comp);
			BigDecimal firstStepAmount = BigDecimal.ZERO;
			Date appDate = SysParamUtil.getAppDate();
			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {
				int minTerms = 1;
				boolean isEMIChange = true;
				boolean isDelete = true;
				if (!StringUtils.equals(PennantConstants.STEP_SPECIFIER_REG_EMI,
						financeStepPolicyDetail.getStepSpecifier())) {
					continue;
				}

				if (financeStepPolicyDetail.getStepNo() == 1) {
					firstStepAmount = PennantApplicationUtil.formateAmount(financeStepPolicyDetail.getSteppedEMI(),
							ccyFormatter);
				}

				listItem = new Listitem();

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getStepNo()));
				lc.setParent(listItem);

				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.formatRate(financeStepPolicyDetail.getTenorSplitPerc().doubleValue(),
						2));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getInstallments()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(
						PennantApplicationUtil.formatRate(financeStepPolicyDetail.getRateMargin().doubleValue(), 9));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				BigDecimal appliedRate = financeStepPolicyDetail.getRateMargin();
				if (getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayProfitRate() != null) {
					appliedRate = appliedRate
							.add(getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayProfitRate());
				}
				lc.setLabel(
						PennantApplicationUtil.formatRate(financeStepPolicyDetail.getRateMargin().doubleValue(), 9));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(
						PennantApplicationUtil.formatRate(financeStepPolicyDetail.getEmiSplitPerc().doubleValue(), 2));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(
						PennantApplicationUtil.amountFormate(financeStepPolicyDetail.getSteppedEMI(), ccyFormatter));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				if (financeStepPolicyDetail.getStepNo() == 1) {
					lc.setLabel("100%");
				} else if (firstStepAmount.compareTo(BigDecimal.ZERO) > 0) {
					lc.setLabel(
							PennantApplicationUtil.formateAmount(financeStepPolicyDetail.getSteppedEMI(), ccyFormatter)
									.multiply(new BigDecimal(100)).divide(firstStepAmount, 0, RoundingMode.HALF_DOWN)
									.toString().concat("%"));
				}
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				tenorPerc = tenorPerc.add(financeStepPolicyDetail.getTenorSplitPerc());
				totInstallments = totInstallments + financeStepPolicyDetail.getInstallments();
				avgRateMargin = avgRateMargin.add(financeStepPolicyDetail.getRateMargin());
				avgAplliedRate = avgAplliedRate.add(appliedRate);
				avgEmiPerc = avgEmiPerc.add(financeStepPolicyDetail.getEmiSplitPerc());

				listItem.setParent(this.listBoxStepdetails);
				listItem.setAttribute("data", financeStepPolicyDetail);
				if (FinServiceEvent.RESCHD.equals(this.financeDetail.getModuleDefiner())
						&& StringUtils.equals(this.financeDetail.getFinScheduleData().getFinanceMain().getCalcOfSteps(),
								PennantConstants.STEPPING_CALC_AMT)
						&& financeStepPolicyDetail.getStepStart() != null
						&& financeStepPolicyDetail.getStepEnd() != null) {
					if (financeStepPolicyDetail.getStepStart().compareTo(appDate) > 0) {
						listItem.setAttribute("minTerms", minTerms);
						listItem.setAttribute("isEMIChange", isEMIChange);
						listItem.setAttribute("message", "");
						listItem.setAttribute("isDelete", isDelete);
						ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
					} else if (financeStepPolicyDetail.getStepEnd().compareTo(appDate) > 0) {
						int paidMnths = DateUtil.getMonthsBetween(financeStepPolicyDetail.getStepStart(), appDate);
						if (paidMnths > 0) {
							minTerms = paidMnths;
							isEMIChange = false;
							isDelete = false;
						}

						listItem.setAttribute("minTerms", minTerms);
						listItem.setAttribute("isEMIChange", isEMIChange);
						listItem.setAttribute("message", "");
						listItem.setAttribute("isDelete", isDelete);
						ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
					} else {
						isDelete = false;
						listItem.setAttribute("minTerms", minTerms);
						listItem.setAttribute("isEMIChange", isEMIChange);
						listItem.setAttribute("message",
								"Tenor already completed for the selected step. Not able to edit.");
						listItem.setAttribute("isDelete", isDelete);
						ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
					}
				} else {
					listItem.setAttribute("minTerms", minTerms);
					listItem.setAttribute("isEMIChange", isEMIChange);
					listItem.setAttribute("message", "");
					listItem.setAttribute("isDelete", isDelete);
					ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
				}
			}

		}

		if (CollectionUtils.isNotEmpty(finStepPolicyDetails)) {
			String stepType = "";
			if (this.stepType.getSelectedItem() != null) {
				stepType = this.stepType.getSelectedItem().getValue();
			}
			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {
				if (StringUtils.equals(financeStepPolicyDetail.getStepSpecifier(),
						PennantConstants.STEP_SPECIFIER_REG_EMI)) {
					addFoolter(finStepPolicyDetails.size(), tenorPerc, totInstallments, avgRateMargin, avgAplliedRate,
							avgEmiPerc, stepType);
					break;
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void addFoolter(int size, BigDecimal tenorPerc, int totInstallments, BigDecimal avgRateMargin,
			BigDecimal avgAplliedRate, BigDecimal avgEmiPerc, String stepType) {

		Listitem listItem = new Listitem();
		listItem.setStyle("background-color: #C0EBDF;");

		Listcell lc = new Listcell(Labels.getLabel("label_StepDetailsFooter"));
		lc.setStyle("text-align:left;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(PennantApplicationUtil.formatRate(tenorPerc.doubleValue(), 2) + "%");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(String.valueOf(totInstallments));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		avgRateMargin = avgRateMargin.divide(new BigDecimal(size), 9, RoundingMode.HALF_DOWN);
		lc = new Listcell(PennantApplicationUtil.formatRate(avgRateMargin.doubleValue(), 9));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		avgAplliedRate = avgAplliedRate.divide(new BigDecimal(size), 9, RoundingMode.HALF_DOWN);
		lc = new Listcell(PennantApplicationUtil.formatRate(avgAplliedRate.doubleValue(), 9));
		lc.setParent(listItem);
		lc.setStyle("text-align:right;font-weight:bold;");

		if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)) {
			avgEmiPerc = avgEmiPerc.divide(new BigDecimal(size), 2, RoundingMode.HALF_DOWN);
		}
		lc = new Listcell(PennantApplicationUtil.formatRate(avgEmiPerc.doubleValue(), 2) + "%");
		lc.setParent(listItem);
		lc.setStyle("text-align:right;font-weight:bold;");

		lc = new Listcell("");
		lc.setStyle("text-align:right;");
		lc.setParent(listItem);

		lc = new Listcell("");
		lc.setStyle("text-align:right;");
		lc.setParent(listItem);

		this.listBoxStepdetails.appendChild(listItem);
	}

	private void addFoolterForGrace(int size, BigDecimal tenorPerc, int totInstallments) {

		Listitem listItem = new Listitem();
		listItem.setStyle("background-color: #C0EBDF;");

		Listcell lc = new Listcell(Labels.getLabel("label_StepDetailsFooter"));
		lc.setStyle("text-align:left;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(PennantApplicationUtil.formatRate(tenorPerc.doubleValue(), 2) + "%");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(String.valueOf(totInstallments));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell("");
		lc.setStyle("text-align:right;");
		lc.setParent(listItem);

		this.listBoxStepdetailsforGrace.appendChild(listItem);
	}

	/**
	 * Double Click on Step Policy Item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinStepPolicyItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		// get the selected Academic object
		final Listitem item = this.listBoxStepdetails.getSelectedItem();

		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceStepPolicyDetail aFinStepPolicy = (FinanceStepPolicyDetail) item.getAttribute("data");
			int minTerms = (int) item.getAttribute("minTerms");
			boolean isEMIChange = (boolean) item.getAttribute("isEMIChange");
			boolean isDelete = (boolean) item.getAttribute("isDelete");
			String msg = (String) item.getAttribute("message");
			if (StringUtils.isNotEmpty(msg)) {
				MessageUtil.showMessage(msg);
				return;
			}
			aFinStepPolicy.setMinTerms(minTerms);
			aFinStepPolicy.setEMIChange(isEMIChange);
			aFinStepPolicy.setDelete(isDelete);
			openFinStepPolicyDetailDialog(aFinStepPolicy, false);

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Filling Step Policy Details
	 * 
	 * @param finStepPolicyDetails
	 */
	public void doFillStepDetaisForGrace(List<FinanceStepPolicyDetail> finStepPolicyDetails) {
		logger.debug("Entering ");

		Listitem listItem = null;
		Listcell lc = null;

		BigDecimal tenorPerc = BigDecimal.ZERO;
		int totInstallments = 0;

		this.listBoxStepdetailsforGrace.getItems().clear();

		setFinStepPoliciesList(finStepPolicyDetails);
		if (finStepPolicyDetails != null) {

			Comparator<Object> comp = new BeanComparator<Object>("stepNo");
			Collections.sort(finStepPolicyDetails, comp);
			Date appDate = SysParamUtil.getAppDate();
			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {
				int minTerms = 1;
				boolean isEMIChange = true;
				boolean isDelete = true;
				if (financeStepPolicyDetail.getStepSpecifier() != null && !StringUtils
						.equals(PennantConstants.STEP_SPECIFIER_GRACE, financeStepPolicyDetail.getStepSpecifier())) {
					continue;
				}

				listItem = new Listitem();

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getStepNo()));
				lc.setParent(listItem);

				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.formatRate(financeStepPolicyDetail.getTenorSplitPerc().doubleValue(),
						2));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getInstallments()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(
						PennantApplicationUtil.amountFormate(financeStepPolicyDetail.getSteppedEMI(), ccyFormatter));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				tenorPerc = tenorPerc.add(financeStepPolicyDetail.getTenorSplitPerc());
				totInstallments = totInstallments + financeStepPolicyDetail.getInstallments();

				listItem.setParent(this.listBoxStepdetailsforGrace);

				listItem.setAttribute("data", financeStepPolicyDetail);
				if (FinServiceEvent.RESCHD.equals(this.financeDetail.getModuleDefiner())
						&& StringUtils.equals(this.financeDetail.getFinScheduleData().getFinanceMain().getCalcOfSteps(),
								PennantConstants.STEPPING_CALC_AMT)
						&& financeStepPolicyDetail.getStepStart() != null
						&& financeStepPolicyDetail.getStepEnd() != null) {
					if (financeStepPolicyDetail.getStepStart().compareTo(appDate) > 0) {
						listItem.setAttribute("minTerms", minTerms);
						listItem.setAttribute("isEMIChange", isEMIChange);
						listItem.setAttribute("message", "");
						listItem.setAttribute("isDelete", isDelete);
						ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyForGraceItemDoubleClicked");
					} else if (financeStepPolicyDetail.getStepEnd().compareTo(appDate) > 0) {
						int paidMnths = DateUtil.getMonthsBetween(financeStepPolicyDetail.getStepStart(), appDate);
						if (paidMnths > 0) {
							minTerms = paidMnths;
							isEMIChange = false;
							isDelete = false;
						}

						listItem.setAttribute("minTerms", minTerms);
						listItem.setAttribute("isEMIChange", isEMIChange);
						listItem.setAttribute("message", "");
						listItem.setAttribute("isDelete", isDelete);
						ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyForGraceItemDoubleClicked");
					} else {
						isDelete = false;
						listItem.setAttribute("minTerms", minTerms);
						listItem.setAttribute("isEMIChange", isEMIChange);
						listItem.setAttribute("message",
								"Tenor already completed for the selected step. Not able to edit.");
						listItem.setAttribute("isDelete", isDelete);
						ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyForGraceItemDoubleClicked");
					}
				} else {
					listItem.setAttribute("minTerms", minTerms);
					listItem.setAttribute("isEMIChange", isEMIChange);
					listItem.setAttribute("message", "");
					listItem.setAttribute("isDelete", isDelete);
					ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyForGraceItemDoubleClicked");
				}
			}

		}

		if (CollectionUtils.isNotEmpty(finStepPolicyDetails)) {
			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {
				if (StringUtils.equals(financeStepPolicyDetail.getStepSpecifier(),
						PennantConstants.STEP_SPECIFIER_GRACE)) {
					addFoolterForGrace(finStepPolicyDetails.size(), tenorPerc, totInstallments);
					break;
				}
			}
		}

		logger.debug("Leaving ");
	}

	/**
	 * Double Click on Step Policy For Grace Item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinStepPolicyForGraceItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		// get the selected Academic object
		final Listitem item = this.listBoxStepdetailsforGrace.getSelectedItem();

		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceStepPolicyDetail aFinStepPolicy = (FinanceStepPolicyDetail) item.getAttribute("data");
			int minTerms = (int) item.getAttribute("minTerms");
			boolean isEMIChange = (boolean) item.getAttribute("isEMIChange");
			String msg = (String) item.getAttribute("message");
			boolean isDelete = (boolean) item.getAttribute("isDelete");
			if (StringUtils.isNotEmpty(msg)) {
				MessageUtil.showMessage(msg);
				return;
			}
			aFinStepPolicy.setMinTerms(minTerms);
			aFinStepPolicy.setEMIChange(isEMIChange);
			aFinStepPolicy.setDelete(isDelete);
			openFinStepPolicyDetailDialog(aFinStepPolicy, false);

		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * onClick Event For btnNew_FinStepPolicy Button
	 */
	public void onClick$btnNew_FinStepPolicy(Event event) {
		logger.debug(Literal.ENTERING);

		int stepValue = this.noOfSteps.intValue();
		String calcOfsteps = getComboboxValue(this.calcOfSteps);
		if (stepValue == 0) {
			throw new WrongValueException(this.noOfSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceMainDialog_RepaySteps.value") }));
		}

		if (stepValue < 0) {
			throw new WrongValueException(this.noOfSteps, Labels.getLabel("NUMBER_NOT_NEGATIVE",
					new String[] { Labels.getLabel("label_FinanceMainDialog_RepaySteps.value") }));
		}

		if (calcOfsteps.equals("#")) {
			throw new WrongValueException(this.calcOfSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceTypeDialog_CalcOfSteps.value") }));
		}

		FinanceStepPolicyDetail financeStepPolicyDetail = new FinanceStepPolicyDetail();
		financeStepPolicyDetail.setNewRecord(true);
		financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
		financeStepPolicyDetail.setMinTerms(1);
		financeStepPolicyDetail.setEMIChange(true);
		financeStepPolicyDetail.setDelete(true);
		openFinStepPolicyDetailDialog(financeStepPolicyDetail, true);
		logger.debug("Leaving");
	}

	public void onClick$btnNew_FinStepPolicyGrace(Event event) {
		logger.debug("Entering");

		int grcSteps = this.grcSteps.intValue();
		if (grcSteps == 0) {
			throw new WrongValueException(this.grcSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceMainDialog_GrcSteps.value") }));
		}
		if (grcSteps < 0) {
			throw new WrongValueException(this.grcSteps, Labels.getLabel("NUMBER_NOT_NEGATIVE",
					new String[] { Labels.getLabel("label_FinanceMainDialog_GrcSteps.value") }));
		}
		FinanceStepPolicyDetail financeStepPolicyDetail = new FinanceStepPolicyDetail();
		financeStepPolicyDetail.setNewRecord(true);
		financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_GRACE);
		financeStepPolicyDetail.setMinTerms(1);
		financeStepPolicyDetail.setEMIChange(true);
		financeStepPolicyDetail.setDelete(true);
		openFinStepPolicyDetailDialog(financeStepPolicyDetail, true);
		logger.debug("Leaving");
	}

	public void openFinStepPolicyDetailDialog(FinanceStepPolicyDetail finStepPolicy, boolean isNewRecord) {
		try {

			this.noOfSteps.setErrorMessage("");
			this.grcSteps.setErrorMessage("");
			final Map<String, Object> map = new HashMap<>();
			map.put("financeStepPolicyDetail", finStepPolicy);
			map.put("stepDetailDialogCtrl", this);
			map.put("newRecord", isNewRecord);
			map.put("roleCode", roleCode);
			map.put("finStepPoliciesList", getFinStepPoliciesList());
			map.put("alwDeletion", this.alwManualSteps.isChecked());
			map.put("alwManualStep", this.alwManualSteps.isChecked());
			map.put("ccyFormatter", this.ccyFormatter);
			map.put("financeDetail", financeDetail);
			map.put("enquiryModule", isEnquiry);
			map.put("rpySteps", this.noOfSteps.intValue());
			map.put("grcSteps", this.grcSteps.intValue());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinStepPolicyDetailDialog.zul",
					window_StepDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		doFillStepDetais(getFinStepPoliciesList());
		doFillStepDetaisForGrace(getFinStepPoliciesList());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public List<FinanceStepPolicyDetail> getFinStepPoliciesList() {
		return this.finStepPolicyList;
	}

	public void setFinStepPoliciesList(List<FinanceStepPolicyDetail> finStepPoliciesList) {

		this.finStepPolicyList = new ArrayList<>();
		List<FinanceStepPolicyDetail> spdList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(finStepPoliciesList)) {
			List<FinanceStepPolicyDetail> graceSpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> rpySpdList = new ArrayList<>();

			for (FinanceStepPolicyDetail spd : finStepPoliciesList) {
				if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_GRACE)) {
					graceSpdList.add(spd);
				} else {
					rpySpdList.add(spd);
				}
			}
			Collections.sort(graceSpdList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
					: step1.getStepNo() < step2.getStepNo() ? -1 : 0);
			Collections.sort(rpySpdList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
					: step1.getStepNo() < step2.getStepNo() ? -1 : 0);

			spdList.addAll(graceSpdList);
			spdList.addAll(rpySpdList);
			if (CollectionUtils.isNotEmpty(finStepPolicyList)) {
				this.finStepPolicyList.clear();
			}
		}
		this.finStepPolicyList.addAll(spdList);
	}

	public boolean isAllowedManualSteps() {
		return this.alwManualSteps.isChecked();
	}

	public void setAllowedManualSteps(boolean allowedManualSteps) {
		this.allowedManualSteps = allowedManualSteps;
		if (this.allowedManualSteps) {
			if (isWIF) {
				this.btnNew_FinStepPolicy.setVisible(true);
			} else {
				this.btnNew_FinStepPolicy.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNew"));
			}
		} else {
			this.btnNew_FinStepPolicy.setVisible(false);
		}
	}

	public boolean isDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Label getLabel_FinanceMainDialog_StepPolicy() {
		return label_FinanceMainDialog_StepPolicy;
	}

	public void setLabel_FinanceMainDialog_StepPolicy(Label labelFinanceMainDialogStepPolicy) {
		this.label_FinanceMainDialog_StepPolicy = labelFinanceMainDialogStepPolicy;
	}

	public Label getLabel_FinanceMainDialog_numberOfSteps() {
		return label_FinanceMainDialog_numberOfSteps;
	}

	public void setLabel_FinanceMainDialog_numberOfSteps(Label labelFinanceMainDialogNumberOfSteps) {
		this.label_FinanceMainDialog_numberOfSteps = labelFinanceMainDialogNumberOfSteps;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public boolean isFinAlwGrace() {
		return isFinAlwGrace;
	}

	public void setFinAlwGrace(boolean isFinAlwGrace) {
		this.isFinAlwGrace = isFinAlwGrace;
	}

	public boolean setAlwGraceChanges(boolean isVisible, boolean fromBasicDetails) {
		if (isVisible) {
			if (StringUtils.trimToNull(this.stepsAppliedFor.getValue()) != null
					&& getComboboxValue(this.stepsAppliedFor).equals(PennantConstants.STEPPING_APPLIED_EMI)) {
				this.btnNew_FinStepPolicyGrace.setVisible(false);
				this.grcSteps.setReadonly(true);
				this.grcSteps.setValue(0);
			} else {
				this.btnNew_FinStepPolicyGrace
						.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNewGraceStep"));
				this.grcSteps.setReadonly(!getUserWorkspace().isAllowed("StepDetailDialog_noOfGrcSteps"));
				this.grcSteps.setValue(0);
			}
		} else {
			this.btnNew_FinStepPolicyGrace.setVisible(isVisible);
			this.grcSteps.setReadonly(!isVisible);
			this.grcSteps.setValue(0);
		}
		if (fromBasicDetails) {
			if (isVisible) {
				alwGrace = isVisible;
			} else {
				alwGrace = false;
			}
		}

		return alwGrace;
	}
}
