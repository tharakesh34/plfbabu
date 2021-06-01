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
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class StepDetailDialogCtrl extends GFCBaseCtrl<StepPolicyHeader> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(StepDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_StepDetailDialog; // autoWired

	//Finance Step Details Tab
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

	private boolean isEnquiry = false;
	private boolean dataChanged = false;

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
	 * @throws Exception
	 */
	public void onCreate$window_StepDetailDialog(Event event) throws Exception {
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

		if (arguments.containsKey("alwManualSteps")) {
			setAllowedManualSteps((Boolean) arguments.get("alwManualSteps"));
		}

		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (Integer) arguments.get("ccyFormatter");
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
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException, ParseException {
		logger.debug(Literal.ENTERING);

		try {
			// append finance basic details 
			appendFinBasicDetails();

			getFinanceMainDialogCtrl().getClass().getMethod("setStepDetailDialogCtrl", this.getClass())
					.invoke(getFinanceMainDialogCtrl(), this);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		//Stooping to Enter New Step policies and Allowed only for Maintenance
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

		doEditStep(financeDetail.getFinScheduleData());
		try {
			doWriteBeanToComponents(afinanceDetail, true);
			doFillStepDetais(getFinScheduleData().getStepPolicyDetails());
			doFillStepDetaisForGrace(getFinScheduleData().getStepPolicyDetails());
			if (isEnquiry) {
				doReadOnly();
			}
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
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		// Step Finance Field Properties
		this.noOfSteps.setMaxlength(2);
		this.noOfSteps.setStyle("text-align:right;");
		this.grcSteps.setMaxlength(2);
		this.grcSteps.setStyle("text-align:right;");
		this.stepType.setReadonly(true);

		this.stepPolicy.setProperties("StepPolicyHeader", "PolicyCode", "PolicyDesc", true, 8);
		String[] alwdStepPolices = StringUtils.trimToEmpty(financeType.getAlwdStepPolicies()).split(",");
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("PolicyCode", Arrays.asList(alwdStepPolices), Filter.OP_IN);
		this.stepPolicy.setFilters(filter);

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
					Labels.getLabel("label_FinanceMainDialog_NumberOfSteps.value"), false, false, 0, 99));
		}

	}

	public ArrayList<ErrorDetail> ValidatePaymentMethod(ArrayList<ErrorDetail> errorList, String cbScheduleMethod,
			String repayRateBasis) {
		String stepTypeVal = this.stepType.getSelectedItem().getValue().toString();
		if (StringUtils.equals(stepTypeVal, FinanceConstants.STEPTYPE_PRIBAL)
				&& StringUtils.equals(cbScheduleMethod, CalculationConstants.SCHMTHD_EQUAL)) {
			errorList.add(new ErrorDetail("StepFinance", "30555",
					new String[] { Labels.getLabel("label_ScheduleMethod_Equal") }, new String[] {}));
		}

		if (StringUtils.equals(stepTypeVal, FinanceConstants.STEPTYPE_EMI)
				&& !StringUtils.equals(cbScheduleMethod, CalculationConstants.SCHMTHD_EQUAL)) {
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
		if (isDataChanged()) {
			return true;
		}
		return false;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData
	 *            (FinScheduleData)
	 * @param tab
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData, Tab tab, String method)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {

		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
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

		if (StringUtils.isEmpty(aFinanceMain.getStepsAppliedFor())) {
			aFinanceMain.setStepsAppliedFor(financeType.getStepsAppliedFor());
		}

		if (StringUtils.isEmpty(aFinanceMain.getCalcOfSteps())) {
			aFinanceMain.setCalcOfSteps(financeType.getCalcOfSteps());
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
			tab.setSelected(true);
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

		FinanceType financeType = finScheduleData.getFinanceType();
		String calcOfSteps = "";
		String stepAppliedFor = "";

		stepAppliedFor = financeMain.isStepFinance() ? financeMain.getStepsAppliedFor()
				: financeType.getStepsAppliedFor();
		calcOfSteps = financeMain.isStepFinance() ? financeMain.getCalcOfSteps() : financeType.getCalcOfSteps();

		this.gb_grace.setVisible(false);
		this.gb_emi.setVisible(false);
		this.grcSteps.setReadonly(true);

		if (StringUtils.equals(stepAppliedFor, PennantConstants.STEPPING_APPLIED_GRC)) {
			this.gb_grace.setVisible(true);
			if (financeMain.isAllowGrcPeriod()) {
				this.btnNew_FinStepPolicyGrace.setVisible(true);
				this.grcSteps.setReadonly(false);
			}
		} else if (StringUtils.equals(stepAppliedFor, PennantConstants.STEPPING_APPLIED_EMI)) {
			this.gb_emi.setVisible(true);
		} else if (StringUtils.equals(stepAppliedFor, PennantConstants.STEPPING_APPLIED_BOTH)) {
			this.gb_grace.setVisible(true);
			this.gb_emi.setVisible(true);
			if (financeMain.isAllowGrcPeriod()) {
				this.btnNew_FinStepPolicyGrace.setVisible(true);
				this.grcSteps.setReadonly(false);
			}
		}

		if (StringUtils.equals(calcOfSteps, PennantConstants.STEPPING_CALC_AMT)) {
			this.listheader_StepFinance_SteppedEMI.setVisible(true);
			this.listheader_StepFinance_EMIDiff.setVisible(true);
			this.listheader_StepFinance_EMIStepPercent.setVisible(false);
		} else if (StringUtils.equals(calcOfSteps, PennantConstants.STEPPING_CALC_PERC)) {
			this.listheader_StepFinance_SteppedEMI.setVisible(false);
			this.listheader_StepFinance_EMIDiff.setVisible(false);
			this.listheader_StepFinance_EMIStepPercent.setVisible(true);
		}

		if (StringUtils.equals(calcOfSteps, PennantConstants.STEPPING_CALC_PERC)) {
			this.stepPolicy.setReadonly(false);
			this.stepType.setDisabled(false);
			if (financeMain.isAlwManualSteps()) {
				this.stepPolicy.setReadonly(true);
			} else {
				this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
				this.hbox_numberOfSteps.setVisible(false);
				this.space_noOfSteps.setSclass("");
			}
			if (!financeType.isAlwManualSteps()) {
				this.alwManualSteps.setDisabled(true);
			}
		} else {
			this.stepPolicy.setMandatoryStyle(false);
			this.stepPolicy.setReadonly(true);
			this.stepType.setDisabled(true);
			this.alwManualSteps.setChecked(true);
			this.alwManualSteps.setDisabled(true);
			//this.space_noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.btnNew_FinStepPolicy.setVisible(true);
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
		this.stepType.setDisabled(false);

		Object dataObject = stepPolicy.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.stepPolicy.setValue(dataObject.toString());
				this.stepPolicy.setDescription("");
			}
			fillComboBox(this.stepType, PennantConstants.List_Select, PennantStaticListUtil.getStepType(), "");
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();

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
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		String calOfSteps = "";
		calOfSteps = financeMain.isStepFinance() ? financeMain.getCalcOfSteps() : financeType.getCalcOfSteps();
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");

		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");

		if (this.alwManualSteps.isChecked()) {
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			//this.space_noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setValue(0);
			this.stepType.setVisible(true);
			this.stepType.setSclass(PennantConstants.mandateSclass);
			this.btnNew_FinStepPolicy.setVisible(true);
			this.stepPolicy.setValue("", "");
			this.stepPolicy.setReadonly(true);
			this.space_stepType.setSclass(PennantConstants.mandateSclass);
			this.stepType.setDisabled(false);
			if (isAction) {
				List<StepPolicyDetail> policyList = new ArrayList<StepPolicyDetail>();
				if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
					policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
				}
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
				doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
				fillComboBox(this.stepType, FinanceConstants.STEPTYPE_EMI, PennantStaticListUtil.getStepType(), "");
			} else {
				fillComboBox(this.stepType, getFinanceDetail().getFinScheduleData().getFinanceMain().getStepType(),
						PennantStaticListUtil.getStepType(), "");
			}
			if (StringUtils.equals(calOfSteps, PennantConstants.STEPPING_CALC_AMT)) {
				fillComboBox(this.stepType, PennantConstants.List_Select, PennantStaticListUtil.getStepType(), "");
				this.stepType.setDisabled(true);
			}
		} else {

			if (this.financeDetail.isNewRecord()) {
				this.stepPolicy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicy());
				this.stepPolicy.setDescription(
						getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescDftStepPolicyName());
				this.stepType.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType());
				fillComboBox(this.stepType,
						getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType(),
						PennantStaticListUtil.getStepType(), "");
			} else {
				this.stepPolicy.setValue(getFinanceDetail().getFinScheduleData().getFinanceMain().getStepPolicy());
				this.stepPolicy.setDescription(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescStepPolicyName());
				fillComboBox(this.stepType, getFinanceDetail().getFinScheduleData().getFinanceMain().getStepType(),
						PennantStaticListUtil.getStepType(), "");
			}
			this.stepPolicy.setMandatoryStyle(true);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
			this.space_noOfSteps.setSclass("");
			this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
			this.stepType.setReadonly(isReadOnly("FinanceMainDialog_stepType"));
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);
			if (isReadOnly("FinanceMainDialog_alwManualSteps")) {
				this.alwManualSteps.setVisible(false);
			}

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
	 * @param aFinanceMain
	 *            financeMain
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
		//doAlwManualStepsCheck(false);
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.stepPolicy.setDescription(aFinanceMain.getLovDescStepPolicyName());
		this.grcSteps.setValue(aFinanceMain.getNoOfGrcSteps());
		doStoreDftValues();
		if (!aFinanceMain.isStepFinance()) {
			doStepPolicyCheck();
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

					//Setting Bean Property Field Details
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

				//If Any Step Policy have Zero installments while on Calculation
				if (hadZeroInstStep) {
					errorList.add(new ErrorDetail("30569", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_MinInstallment"), " 1 " }));
				}

				//Tenor Percentage Validation for Step Policy Details
				if (calTotTenorSplit.compareTo(new BigDecimal(100)) != 0) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TenorSplitPerc"), "100.00 %" }));
				}

				//Average EMI Percentage/ Total Percentage based on Step Type Validation for Step Policy Details
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
				errorList.add(new ErrorDetail("30542", PennantConstants.KEY_SEPERATOR, new String[] { label, label }));
				return errorList;
			}

			if (CollectionUtils.isNotEmpty(spdList)) {

				if (isAlwManualSteps && StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_REG_EMI)
						&& totalTerms != rpyTerms) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TotStepInstallments", new String[] { "Repay" }),
									Labels.getLabel("label_TotalTerms") }));
					return errorList;
				}

				if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_GRACE) && totalTerms != grcTerms) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TotStepInstallments", new String[] { "Grace" }),
									Labels.getLabel("label_GrcTotalTerms") }));
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
								&& financeStepPolicyDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) <= 0) {
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

			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {

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
				ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
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

			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {

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
				ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyForGraceItemDoubleClicked");
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
			openFinStepPolicyDetailDialog(aFinStepPolicy, false);

		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * onClick Event For btnNew_FinStepPolicy Button
	 */
	public void onClick$btnNew_FinStepPolicy(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		int stepValue = this.noOfSteps.intValue();
		if (stepValue == 0) {
			throw new WrongValueException(this.noOfSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceMainDialog_RepaySteps.value") }));
		}

		FinanceStepPolicyDetail financeStepPolicyDetail = new FinanceStepPolicyDetail();
		financeStepPolicyDetail.setNewRecord(true);
		financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
		openFinStepPolicyDetailDialog(financeStepPolicyDetail, true);
		logger.debug("Leaving");
	}

	public void onClick$btnNew_FinStepPolicyGrace(Event event) throws Exception {
		logger.debug("Entering");

		int grcSteps = this.grcSteps.intValue();
		if (grcSteps == 0) {
			throw new WrongValueException(this.grcSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceMainDialog_GrcSteps.value") }));
		}
		FinanceStepPolicyDetail financeStepPolicyDetail = new FinanceStepPolicyDetail();
		financeStepPolicyDetail.setNewRecord(true);
		financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_GRACE);
		openFinStepPolicyDetailDialog(financeStepPolicyDetail, true);
		logger.debug("Leaving");
	}

	public void openFinStepPolicyDetailDialog(FinanceStepPolicyDetail finStepPolicy, boolean isNewRecord)
			throws InterruptedException {
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
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
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

	public void setAlwGraceChanges(boolean isVisible) {
		this.btnNew_FinStepPolicyGrace.setVisible(isVisible);
		this.grcSteps.setReadonly(!isVisible);
		this.grcSteps.setValue(0);
	}
}
