package com.pennant.webui.rmtmasters.financetype;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.applicationmaster.InsurancePolicy;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinTypeInsuranceDialogCtrl extends GFCBaseCtrl<FinTypeInsurances> {
	private static final long			serialVersionUID	= -6945930303723518608L;
	private static final Logger			logger				= Logger.getLogger(FinTypeInsuranceDialogCtrl.class);

	protected Window					window_FinTypeInsurnaceDialog;
	protected Textbox					financeType;
	protected ExtendedCombobox			insuranceType;
	protected ExtendedCombobox			policyType;
	protected Combobox					paymentType;
	protected Combobox					calcType;
	protected Checkbox					mandatory;
	protected Checkbox					alwChange;
	protected Label						label_FinTypeInsuranceDialog_AmountRule;

	private FinTypeInsurances			finTypeInsurance;

	//private FinanceTypeDialogCtrl		financeTypeDialogCtrl;
	private FinTypeInsuranceListCtrl	finTypeInsuranceListCtrl;
	private transient PagedListService	pagedListService;
	private List<FinTypeInsurances>		finTypeInsuranceList;

	private ExtendedCombobox			amountRule;
	private CurrencyBox					constAmount;
	private Decimalbox					percentage;
    private Combobox					calculateOn;
	private int							amountFormatter;
	private String						userRole			= "";
	private Label						label_finTypeDesc;

	private Row							row_Rule;
	private Row							row_ConstAmount;
	private Row							row_Percentage;
	private Row							row_CalculaedOn;
	private Row							row_AlwRateChange;
	
	private Label						 label_FinTypeInsuranceDialog_AllowRateChange;

	/**
	 * default constructor.<br>
	 */
	public FinTypeInsuranceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeInsuranceDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_FinTypeInsurnaceDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeInsurnaceDialog);
		if (arguments.containsKey("finTypeInsurances")) {
			this.finTypeInsurance = (FinTypeInsurances) arguments.get("finTypeInsurances");
			FinTypeInsurances befImage = new FinTypeInsurances();
			BeanUtils.copyProperties(this.finTypeInsurance, befImage);
			this.finTypeInsurance.setBefImage(befImage);
			setFinTypeInsurance(this.finTypeInsurance);

		} else {
			setFinTypeInsurance(null);
		}
		
		if (arguments.containsKey("finTypeInsuranceListCtrl")) {
			setFinTypeInsuranceListCtrl( (FinTypeInsuranceListCtrl) arguments.get("finTypeInsuranceListCtrl"));
		} else {
			setFinTypeInsuranceListCtrl(null);
		}
		
		if (arguments.containsKey("amountFormatter")) {
			amountFormatter =  (Integer) arguments.get("amountFormatter");
		}
		
		/*if (arguments.containsKey("moduleId")) {
			moduleId = (int) arguments.get("moduleId");
		}*/
		
		if (arguments.containsKey("role")) {
			userRole = arguments.get("role").toString();
			getUserWorkspace().allocateRoleAuthorities(arguments.get("role").toString(), "FinTypeInsuranceDialog");
		}
		
		this.finTypeInsurance.setWorkflowId(0);
		doLoadWorkFlow(this.finTypeInsurance.isWorkflow(), this.finTypeInsurance.getWorkflowId(), this.finTypeInsurance.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
		}

		doCheckRights();
		doSetFieldProperties();
		doShowDialog(getFinTypeInsurance());

		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.insuranceType.setMandatoryStyle(true);
		this.insuranceType.setModuleName("InsuranceType");
		this.insuranceType.setValueColumn("InsuranceType");
		this.insuranceType.setDescColumn("InsuranceTypeDesc");
		this.insuranceType.setTextBoxWidth(145);
		this.insuranceType.setValidateColumns(new String[] { "InsuranceType" });

		this.policyType.setMandatoryStyle(true);
		this.policyType.setModuleName("InsurancePolicy");
		this.policyType.setValueColumn("PolicyCode");
		this.policyType.setDescColumn("PolicyDesc");
		this.policyType.setTextBoxWidth(145);
		this.policyType.setValidateColumns(new String[] { "PolicyCode" });
		
		this.percentage.setMaxlength(6);

		this.constAmount.setMandatory(true);
		this.constAmount.setFormat(PennantApplicationUtil.getAmountFormate(amountFormatter));
		this.constAmount.setScale(amountFormatter);

		this.amountRule.setMandatoryStyle(true);
		this.amountRule.getTextbox().setReadonly(true);
		this.amountRule.setTextBoxWidth(144);
		this.amountRule.setModuleName("Rule");
		this.amountRule.setValueColumn("RuleCode");
		this.amountRule.setDescColumn("RuleCodeDesc");
		this.amountRule.setValidateColumns(new String[] { "RuleCode" });
		Filter[] calRuleFilters = new Filter[1];
		calRuleFilters[0] = new Filter("RuleModule", "INSRULE", Filter.OP_EQUAL);
		this.amountRule.setFilters(calRuleFilters);
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities("FinTypeInsuranceDialog", userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinTypeInsuranceDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinTypeInsuranceDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeInsuranceDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypeInsuranceDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
	}

	private void doShowDialog(FinTypeInsurances aFinTypeInsurance) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinTypeInsurance.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.policyType.focus();

		} else {
			this.financeType.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeInsuranceDialog_btnDelete"));
		}
		
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinTypeInsurance);
			this.window_FinTypeInsurnaceDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	private void doEdit() {
		logger.debug("Entering");
	
		if (getFinTypeInsurance().isNewRecord()) {
			this.insuranceType.setReadonly(isReadOnly("FinTypeInsuranceDialog_InsuranceType"));
			this.policyType.setReadonly(isReadOnly("FinTypeInsuranceDialog_InsuranceType"));
		} else {
			this.insuranceType.setTextBoxWidth(175);
			this.policyType.setTextBoxWidth(175);
			this.insuranceType.setReadonly(true);
			this.policyType.setReadonly(true);
		}

		this.paymentType.setDisabled(isReadOnly("FinTypeInsuranceDialog_PaymentType"));
		this.calcType.setDisabled(isReadOnly("FinTypeInsuranceDialog_CalculationType"));
		this.amountRule.setReadonly(isReadOnly("FinTypeInsuranceDialog_AmountRule"));
		this.constAmount.setReadonly(isReadOnly("FinTypeInsuranceDialog_ConstAmount"));
		this.percentage.setReadonly(isReadOnly("FinTypeInsuranceDialog_Percentage"));
		this.calculateOn.setDisabled(isReadOnly("FinTypeInsuranceDialog_CalculateOn"));
		readOnlyComponent(isReadOnly("FinTypeInsuranceDialog_Mandatory"), this.mandatory);
		readOnlyComponent(isReadOnly("FinTypeInsuranceDialog_Mandatory"), this.alwChange);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finTypeInsurance.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	private void doWriteBeanToComponents(FinTypeInsurances aFinTypeInsurance) {
		logger.debug("Entering");
		
		this.financeType.setValue(this.finTypeInsurance.getFinType());
		this.label_finTypeDesc.setValue(this.finTypeInsurance.getFinTypeDesc());
		this.insuranceType.setValue(aFinTypeInsurance.getInsuranceType());
		this.insuranceType.setDescription(aFinTypeInsurance.getInsuranceTypeDesc());
		this.policyType.setValue(aFinTypeInsurance.getPolicyType());
		this.policyType.setDescription(aFinTypeInsurance.getPolicyDesc());
		fillComboBox(this.paymentType, aFinTypeInsurance.getDftPayType(), PennantStaticListUtil.getInsurancePaymentType(), "");
		
		if (this.paymentType.getSelectedItem().getValue().equals(InsuranceConstants.PAYTYPE_SCH_FRQ)) {
			fillComboBox(this.calcType, getFinTypeInsurance().getCalType(),
					PennantStaticListUtil.getInsuranceCalType(), "," + InsuranceConstants.CALTYPE_RULE + ",");
		} else {
			fillComboBox(this.calcType, getFinTypeInsurance().getCalType(), PennantStaticListUtil.getInsuranceCalType(), "");
		}
		
		fillComboBox(this.calculateOn, aFinTypeInsurance.getCalculateOn(), PennantStaticListUtil.getInsuranceCalculatedOn(), "");

		if (StringUtils.equals(aFinTypeInsurance.getCalType(), InsuranceConstants.CALTYPE_RULE)) {
			this.row_Rule.setVisible(true);
			this.amountRule.setValue(aFinTypeInsurance.getAmountRule());
			this.amountRule.setDescription(aFinTypeInsurance.getRuleCodeDesc());
			this.row_AlwRateChange.setVisible(false);
		} else if (StringUtils.equals(aFinTypeInsurance.getCalType(), InsuranceConstants.CALTYPE_CON_AMT)) {
			this.row_ConstAmount.setVisible(true);
			this.constAmount.setValue(PennantAppUtil.formateAmount(aFinTypeInsurance.getConstAmt(), amountFormatter));
			this.row_AlwRateChange.setVisible(true);
			this.label_FinTypeInsuranceDialog_AllowRateChange.setValue(Labels.getLabel("label_FinTypeInsuranceDialog_AllowAmontChange.value"));
		} else if (StringUtils.equals(aFinTypeInsurance.getCalType(), InsuranceConstants.CALTYPE_PERCENTAGE)) {
			this.row_Percentage.setVisible(true);
			this.row_CalculaedOn.setVisible(true);
			this.percentage.setValue(aFinTypeInsurance.getPercentage());
			this.row_AlwRateChange.setVisible(true);
			this.label_FinTypeInsuranceDialog_AllowRateChange.setValue(Labels.getLabel("label_FinTypeInsuranceDialog_AllowPerChange.value"));
		} else if (StringUtils.equals(aFinTypeInsurance.getCalType(), InsuranceConstants.CALTYPE_PROVIDERRATE)) {
			this.row_CalculaedOn.setVisible(true);
			this.row_AlwRateChange.setVisible(true);
			this.row_AlwRateChange.setVisible(true);
			this.label_FinTypeInsuranceDialog_AllowRateChange.setValue(Labels.getLabel("label_FinTypeInsuranceDialog_AllowRateChange.value"));
		}
		
		this.mandatory.setChecked(aFinTypeInsurance.isMandatory());
		this.alwChange.setChecked(aFinTypeInsurance.isAlwRateChange());
		
		this.recordStatus.setValue(aFinTypeInsurance.getRecordStatus());

		logger.debug("Leaving");
	}

	private void doWriteComponentsToBean(FinTypeInsurances aFinTypeInsurance) {
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			aFinTypeInsurance.setFinType(this.financeType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.paymentType))) {
				throw new WrongValueException(this.paymentType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeInsuranceDialog_PaymentType.value") }));
			}
			aFinTypeInsurance.setDftPayType(getComboboxValue(this.paymentType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinTypeInsurance.setInsuranceType(this.insuranceType.getValue());
			aFinTypeInsurance.setInsuranceTypeDesc(this.insuranceType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinTypeInsurance.setPolicyType(this.policyType.getValue());
			aFinTypeInsurance.setPolicyDesc(this.policyType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.paymentType))) {
				throw new WrongValueException(this.paymentType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeInsuranceDialog_PaymentType.value") }));
			}
			aFinTypeInsurance.setDftPayType(getComboboxValue(this.paymentType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.calcType))) {
				throw new WrongValueException(this.calcType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeInsuranceDialog_CalcType.value") }));
			}
			aFinTypeInsurance.setCalType(getComboboxValue(this.calcType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinTypeInsurance.setMandatory(this.mandatory.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinTypeInsurance.setAlwRateChange(this.alwChange.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinTypeInsurance.setAmountRule(null);
			if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_RULE)) {
				aFinTypeInsurance.setAmountRule(this.amountRule.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
				if (isValidComboValue(this.calculateOn, Labels.getLabel("label_FinTypeInsuranceDialog_CalculateOn.value"))) {
					aFinTypeInsurance.setCalculateOn(getComboboxValue(this.calculateOn));
				}
			} else if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
				if (isValidComboValue(this.calculateOn, Labels.getLabel("label_FinTypeInsuranceDialog_CalculateOn.value"))) {
					aFinTypeInsurance.setCalculateOn(getComboboxValue(this.calculateOn));
				}
			}else{
				aFinTypeInsurance.setCalculateOn(PennantConstants.List_Select);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_RULE)) {
				aFinTypeInsurance.setAmountRule(this.amountRule.getValue());
			} else if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
				if (isValidComboValue(this.calculateOn, Labels.getLabel("label_FinTypeInsuranceDialog_CalculateOn.value"))) {
					aFinTypeInsurance.setCalculateOn(getComboboxValue(this.calculateOn));
				}
			} else if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
				if (isValidComboValue(this.calculateOn, Labels.getLabel("label_FinTypeInsuranceDialog_CalculateOn.value"))) {
					aFinTypeInsurance.setCalculateOn(getComboboxValue(this.calculateOn));
				}
			}else{
				aFinTypeInsurance.setCalculateOn(PennantConstants.List_Select);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinTypeInsurance.setConstAmt(BigDecimal.ZERO);
			if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_CON_AMT)) {
				aFinTypeInsurance.setConstAmt(PennantAppUtil.unFormateAmount(
						this.constAmount.isReadonly() ? this.constAmount.getActualValue() : this.constAmount
								.getValidateValue(), amountFormatter));
			} 
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinTypeInsurance.setPercentage(BigDecimal.ZERO);
			if (calcType.getSelectedItem().getValue().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
				aFinTypeInsurance.setPercentage(this.percentage.getValue());
			} 
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinTypeInsurance.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	public void onChange$calcType(Event event) {
		logger.debug("Entering");
		
		clearValues();
		if (calcType.getSelectedItem().getValue().toString().equals(InsuranceConstants.CALTYPE_RULE)) {
			this.row_Rule.setVisible(true);
			this.row_ConstAmount.setVisible(false);
			this.row_Percentage.setVisible(false);
			this.row_CalculaedOn.setVisible(false);
			this.row_AlwRateChange.setVisible(false);
		} else if (calcType.getSelectedItem().getValue().toString().equals(InsuranceConstants.CALTYPE_CON_AMT)) {
			this.row_Rule.setVisible(false);
			this.row_ConstAmount.setVisible(true);
			this.constAmount.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, amountFormatter));
			this.row_Percentage.setVisible(false);
			this.row_CalculaedOn.setVisible(false);
			this.row_AlwRateChange.setVisible(true);
			this.label_FinTypeInsuranceDialog_AllowRateChange.setValue(Labels
					.getLabel("label_FinTypeInsuranceDialog_AllowAmontChange.value"));

		} else if (calcType.getSelectedItem().getValue().toString().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
			this.row_Rule.setVisible(false);
			this.row_ConstAmount.setVisible(false);
			this.percentage.setText("");
			this.row_Percentage.setVisible(true);
			this.row_CalculaedOn.setVisible(true);
			this.row_AlwRateChange.setVisible(true);
			this.label_FinTypeInsuranceDialog_AllowRateChange.setValue(Labels
					.getLabel("label_FinTypeInsuranceDialog_AllowPerChange.value"));

		} else if (calcType.getSelectedItem().getValue().toString().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
			this.row_Rule.setVisible(false);
			this.row_ConstAmount.setVisible(false);
			this.row_Percentage.setVisible(false);
			this.row_CalculaedOn.setVisible(true);
			this.row_AlwRateChange.setVisible(true);
			this.label_FinTypeInsuranceDialog_AllowRateChange.setValue(Labels
					.getLabel("label_FinTypeInsuranceDialog_AllowRateChange.value"));

		}

		logger.debug("Leaving");
	}
	
	public void onChange$paymentType(Event event) {
		logger.debug("Entering");
		
		if(this.paymentType.getSelectedItem().getValue().equals(InsuranceConstants.PAYTYPE_SCH_FRQ)){
			fillComboBox(this.calcType, getFinTypeInsurance().getCalType(), PennantStaticListUtil.getInsuranceCalType(), ","+InsuranceConstants.CALTYPE_RULE+",");
		}else{
			fillComboBox(this.calcType, getFinTypeInsurance().getCalType(), PennantStaticListUtil.getInsuranceCalType(),"");
		}
		
		logger.debug("Leaving");
	}
	
	
	
	public void onFulfill$insuranceType(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = insuranceType.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.insuranceType.setValue(dataObject.toString());
				this.insuranceType.setDescription("");
			}

			this.policyType.setFilters(null);
			this.insuranceType.setFilters(null);
			this.policyType.setValue("");
			this.policyType.setDescription("");
			this.insuranceType.setValue("");
			this.insuranceType.setDescription("");
		} else {
			Filter filter[] = new Filter[1];
			if (this.insuranceType.getValue() != null) {
				filter[0] = new Filter("InsuranceType", this.insuranceType.getValue(), Filter.OP_EQUAL);
				this.policyType.setFilters(filter);
			}
		}
		
		logger.debug("Leaving");
	}

	public void onFulfill$policyType(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = policyType.getObject();
		
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.policyType.setValue(dataObject.toString());
				this.policyType.setDescription("");
			}

			this.policyType.setFilters(null);
			this.insuranceType.setFilters(null);
			this.policyType.setValue("");
			this.policyType.setDescription("");
			this.insuranceType.setValue("");
			this.insuranceType.setDescription("");
		} else {
			InsurancePolicy detail = (InsurancePolicy) dataObject;
			if (detail != null) {
				this.insuranceType.setValue(detail.getInsuranceType());
				this.insuranceType.setDescription(detail.getInsuranceTypeDesc());
			}
			/*Filter filter[] = new Filter[1];
			if (this.policyType.getValue() != null) {
				filter[0] = new Filter("policyCode", this.policyType.getValue(), Filter.OP_EQUAL);
				this.insuranceType.setFilters(filter);
			}*/
		}
		
		logger.debug("Leaving");
	}

	private void clearValues() {
		doClearMessage();
		this.amountRule.setValue("");
		this.amountRule.setDescription("");
		this.calculateOn.setSelectedIndex(0);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnValidateSave(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		doSave();
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		
		doSave();
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinTypeInsurances aFinTypeInsurance = new FinTypeInsurances();
		BeanUtils.copyProperties(getFinTypeInsurance(), aFinTypeInsurance);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();

		// fill the TransactionEntry object with the components data
		doWriteComponentsToBean(aFinTypeInsurance);

		// Write the additional validations as per below example
		// get the selected branch object from the lisBox
		// Do data level validations here

		isNew = aFinTypeInsurance.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinTypeInsurance.getRecordType())) {
				aFinTypeInsurance.setVersion(aFinTypeInsurance.getVersion() + 1);
				if (isNew) {
					aFinTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinTypeInsurance.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aFinTypeInsurance.setVersion(1);
				aFinTypeInsurance.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aFinTypeInsurance.getRecordType())) {
				aFinTypeInsurance.setVersion(aFinTypeInsurance.getVersion() + 1);
				aFinTypeInsurance.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aFinTypeInsurance.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFinTypeInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newFinTypeInsurnaceEntryProcess(aFinTypeInsurance, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeInsurnaceDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinTypeInsuranceListCtrl().doFillFinInsuranceTypes(this.finTypeInsuranceList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
	
		logger.debug("Leaving");
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinTypeInsurnaceDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	private AuditHeader getAuditHeader(FinTypeInsurances aFinTypeInsurance, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypeInsurance.getBefImage(), aFinTypeInsurance);
		return new AuditHeader(aFinTypeInsurance.getFinType(), null, null, null, auditDetail,
				aFinTypeInsurance.getUserDetails(), getOverideMap());
	}

	private AuditHeader newFinTypeInsurnaceEntryProcess(FinTypeInsurances aFinTypeInsurance, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aFinTypeInsurance, tranType);
		finTypeInsuranceList = new ArrayList<FinTypeInsurances>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aFinTypeInsurance.getFinType();
		valueParm[1] = aFinTypeInsurance.getPolicyType();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeInsuranceDialog_FinanceType.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FinTypeInsuranceDialog_PolicyType.value") + ":" + valueParm[1];
		List<FinTypeInsurances> list = getFinTypeInsuranceListCtrl().getFinTypeInsuranceList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				FinTypeInsurances finTypeInsurance = list.get(i);
				if (finTypeInsurance.getFinType().equals(aFinTypeInsurance.getFinType())
						&& finTypeInsurance.getPolicyType().equals(aFinTypeInsurance.getPolicyType())) {
					// Both Current and Existing list rating same
					if (aFinTypeInsurance.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aFinTypeInsurance.getRecordType())) {
							aFinTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finTypeInsuranceList.add(aFinTypeInsurance);
						} else if (PennantConstants.RCD_ADD.equals(aFinTypeInsurance.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aFinTypeInsurance.getRecordType())) {
							aFinTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finTypeInsuranceList.add(aFinTypeInsurance);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aFinTypeInsurance.getRecordType())) {
							recordAdded = true;
							//List<FinTypeInsurances> savedList = getFinanceTypeDialogCtrl().getFinanceType().getFinTypeInsurances();
							List<FinTypeInsurances> savedList = getFinTypeInsuranceListCtrl().getFinTypeInsuranceList();
							for (int j = 0; j < savedList.size(); j++) {
								FinTypeInsurances insType = savedList.get(j);
								if (insType.getFinType().equals(aFinTypeInsurance.getFinType())) {
									finTypeInsuranceList.add(insType);
								}
							}
						} else if (PennantConstants.RECORD_TYPE_DEL.equals(aFinTypeInsurance.getRecordType())) {
							aFinTypeInsurance.setNewRecord(true);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							finTypeInsuranceList.add(finTypeInsurance);
						}
					}
				} else {
					finTypeInsuranceList.add(finTypeInsurance);
				}
			}
		}
		if (!recordAdded) {
			finTypeInsuranceList.add(aFinTypeInsurance);
		}
		
		logger.debug("Leaving");

		return auditHeader;
	}

	private void doSetValidation() {
		logger.debug("Entering");
		
		if (!this.insuranceType.isReadonly()) {
			this.insuranceType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinTypeInsuranceDialog_InsuranceType.value"), null, true, true));
		}

		if (!this.policyType.isReadonly()) {
			this.policyType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinTypeInsuranceDialog_PolicyType.value"), null, true, true));
		}

		if (!this.paymentType.isDisabled()) {
			this.paymentType.setConstraint(new StaticListValidator(PennantStaticListUtil.getInsurancePaymentType(), Labels
					.getLabel("label_FinTypeInsuranceDialog_PaymentType.value")));
		}
		
		if (!this.calcType.isDisabled()) {
			this.calcType.setConstraint(new StaticListValidator(PennantStaticListUtil.getInsuranceCalType(), Labels
					.getLabel("label_FinTypeInsuranceDialog_CalcType.value")));
		}
		
		if (this.row_Rule.isVisible() && !this.amountRule.isButtonDisabled()) {
			this.amountRule.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinTypeInsuranceDialog_AmountRule.value"), null,
					true, true));
		}
		
		if (this.row_ConstAmount.isVisible() && !this.constAmount.isReadonly()) {
			this.constAmount.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_FinTypeInsuranceDialog_ConstAmount.value"), amountFormatter, false, false));
		}

		if (this.row_Percentage.isVisible() && !this.percentage.isReadonly()) {
			this.percentage.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_FinTypeInsuranceDialog_Percentage.value"), 2, true, false, 100));

		}
		if (this.row_CalculaedOn.isVisible() && !this.calculateOn.isDisabled()) {
			this.calculateOn.setConstraint(new StaticListValidator(PennantStaticListUtil.getInsuranceCalculatedOn(), Labels
					.getLabel("label_FinTypeInsuranceDialog_CalculateOn.value")));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		
		this.insuranceType.setConstraint("");
		this.policyType.setConstraint("");
		this.paymentType.setConstraint("");
		this.calcType.setConstraint("");
		this.calculateOn.setConstraint("");
		this.percentage.setConstraint("");
		this.amountRule.setConstraint("");
		this.constAmount.setConstraint("");
	
		logger.debug("Leaving");
	}

	protected boolean isDataChanged() {
		doClearMessage();
		return true;
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		
		this.amountRule.setErrorMessage("");
		this.percentage.setErrorMessage("");
		this.constAmount.setErrorMessage("");
		this.insuranceType.setErrorMessage("");
		this.policyType.setErrorMessage("");
		this.paymentType.setErrorMessage("");
		this.calcType.setErrorMessage("");
		this.calculateOn.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		
		doDelete();
		
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FinTypeAccount object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final FinTypeInsurances finTypeInsurance = new FinTypeInsurances();
		BeanUtils.copyProperties(getFinTypeInsurance(), finTypeInsurance);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_FinTypeInsuranceDialog_FinanceType.value") + " : "
				+ finTypeInsurance.getFinType() + ","
				+ Labels.getLabel("label_FinTypeInsuranceDialog_InsuranceType.value") + " : "
				+ finTypeInsurance.getInsuranceType();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(finTypeInsurance.getRecordType())) {
				finTypeInsurance.setVersion(finTypeInsurance.getVersion() + 1);
				finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					finTypeInsurance.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(finTypeInsurance.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				finTypeInsurance.setVersion(finTypeInsurance.getVersion() + 1);
				finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFinTypeInsurnaceEntryProcess(finTypeInsurance, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeInsurnaceDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinTypeInsuranceListCtrl().doFillFinInsuranceTypes(this.finTypeInsuranceList);
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		
		logger.debug("Leaving");
	}

	public FinTypeInsurances getFinTypeInsurance() {
		return finTypeInsurance;
	}

	public void setFinTypeInsurance(FinTypeInsurances finTypeInsurance) {
		this.finTypeInsurance = finTypeInsurance;
	}

	/*public FinanceTypeDialogCtrl getFinanceTypeDialogCtrl() {
		return financeTypeDialogCtrl;
	}

	public void setFinanceTypeDialogCtrl(FinanceTypeDialogCtrl financeTypeDialogCtrl) {
		this.financeTypeDialogCtrl = financeTypeDialogCtrl;
	}*/

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public List<FinTypeInsurances> getFinTypeInsuranceList() {
		return finTypeInsuranceList;
	}

	public void setFinTypeInsuranceList(List<FinTypeInsurances> finTypeInsurances) {
		this.finTypeInsuranceList = finTypeInsurances;
	}

	public FinTypeInsuranceListCtrl getFinTypeInsuranceListCtrl() {
		return finTypeInsuranceListCtrl;
	}

	public void setFinTypeInsuranceListCtrl(FinTypeInsuranceListCtrl finTypeInsuranceListCtrl) {
		this.finTypeInsuranceListCtrl = finTypeInsuranceListCtrl;
	}
}
