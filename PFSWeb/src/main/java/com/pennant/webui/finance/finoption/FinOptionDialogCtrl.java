package com.pennant.webui.finance.finoption;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.finance.FinOptionMaintanceService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.staticlist.AppStaticList;
import com.pennapps.core.util.ObjectUtil;

public class FinOptionDialogCtrl extends GFCBaseCtrl<FinOption> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(FinOptionDialogCtrl.class);

	protected Window finOptionListWindow;
	protected Rows finOptionRows;
	protected Groupbox finBasicdetails;
	private ArrayList<Object> headerList;
	protected FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected FinanceMainBaseCtrl financeMainBaseCtrl;
	protected FinanceDetail financeDetail;
	protected Button btnNew_NewFinOption;
	private Date maturityDate;
	private Date loanStartDt;
	protected String module = "";
	protected boolean loanEnquiry = false;
	protected FinMaintainInstruction finMaintainInstruction;
	private FinanceSelectCtrl financeSelectCtrl = null;
	@Autowired
	private transient FinOptionMaintanceService finOptionMaintanceService;

	protected North north;
	protected South south1;

	private boolean isFinOptionNew = false;
	private boolean newCustomer;
	private boolean fromLoan;
	private boolean isFinanceProcess = false;
	private FinOption finOption;
	private String moduleDefiner;

	protected Tabs finOptionTabs;
	protected Tab finOptionTab;
	protected Tabpanels finOptionTabPanels;

	private ExtendedFieldCtrl extendedFieldCtrl = null;

	public FinOptionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$finOptionListWindow(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(finOptionListWindow);

		if (arguments.get("finHeaderList") != null) {
			headerList = (ArrayList<Object>) arguments.get("finHeaderList");
		}

		if (arguments.get("enqiryModule") != null) {
			loanEnquiry = (boolean) arguments.get("enqiryModule");

		}
		if (arguments.get("financeSelectCtrl") != null) {
			financeSelectCtrl = (FinanceSelectCtrl) arguments.get("financeSelectCtrl");

		}

		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");

		if (arguments.containsKey("financeMainBaseCtrl")) {
			financeMainBaseCtrl = ((FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl"));
			financeMainBaseCtrl.setFinOptionDialogCtrl(this);
		} else {
			// finBasicdetails.setVisible(false);
		}

		if (arguments.get("module") != null) {
			module = (String) arguments.get("module");
		}

		if (arguments.get("moduleDefiner") != null) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (arguments.get("isFinanceProcess") != null) {
			isFinanceProcess = (boolean) arguments.get("isFinanceProcess");
		}

		if (module.equals("Maintanance")) {
			north.setVisible(true);
			south1.setVisible(true);
			btnNew_NewFinOption.setDisabled(false);
			finMaintainInstruction = (FinMaintainInstruction) arguments.get("finMaintainInstruction");

			// Store the before image.
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			BeanUtils.copyProperties(this.finMaintainInstruction, finMaintainInstruction);
			this.finMaintainInstruction.setBefImage(finMaintainInstruction);

			doLoadWorkFlow(this.finMaintainInstruction.isWorkflow(), this.finMaintainInstruction.getWorkflowId(),
					this.finMaintainInstruction.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else {
				this.south.setHeight("0px");
			}
		}
		if (arguments.containsKey("financeMainDialogCtrl")) {
			fromLoan = true;
			setNewCustomer(true);
		}

		maturityDate = this.financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate();
		loanStartDt = this.financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();

		if (maturityDate == null) {
			maturityDate = loanStartDt;
		}

		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void appendFinBasicDetails() {
		logger.debug(Literal.ENTERING);

		String url = "/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul";

		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", headerList);
			Executions.createComponents(url, this.finBasicdetails, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
		}
	}

	public void onClick$btnNotes(Event event) {
		if (!isFinanceProcess) {
			doShowNotes(this.finMaintainInstruction);
		}
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		doFillFinOptionDetails(this.financeDetail.getFinOptions());
		doEdit();
		doReadOnly(this.financeDetail.getFinOptions());

		if (!enqiryModule) {
			appendFinBasicDetails();
			if (StringUtils.equals(moduleDefiner, FinServiceEvent.FINOPTION)) {
				appendExtendedFieldDetails(financeDetail, moduleDefiner);
			}
		}

		if (module.equals("Maintanance")) {

			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendExtendedFieldDetails(FinanceDetail aFinanceDetail, String finEvent) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender extendedFieldRender = null;

		try {
			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

			if (aFinanceMain == null) {
				return;
			}

			if (finEvent.isEmpty()) {
				finEvent = FinServiceEvent.ORG;
			}

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, aFinanceMain.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());

			extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(aFinanceMain.getFinReference());

			extendedFieldCtrl.createTab(finOptionTabs, finOptionTabPanels);
			finMaintainInstruction.setExtendedFieldHeader(extendedFieldHeader);
			finMaintainInstruction.setExtendedFieldRender(extendedFieldRender);

			if (finMaintainInstruction.getBefImage() != null) {
				finMaintainInstruction.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				finMaintainInstruction.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(finOptionListWindow);
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.Invalid_Extended_Field_Config"), e);
			MessageUtil.showError(Labels.getLabel("message.error.Invalid_Extended_Field_Config"));
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		if (StringUtils.isNotEmpty(financeMain.getScheduleMethod())) {
			arrayList.add(2, financeMain.getScheduleMethod());
		} else {
			arrayList.add(2, "");
		}
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, financeMain.getProfitDaysBasis());
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(financeMain.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, financeMain.getFinCategory());
		String custShrtName = "";
		if (financeDetail.getCustomerDetails() != null && financeDetail.getCustomerDetails().getCustomer() != null) {
			custShrtName = financeDetail.getCustomerDetails().getCustomer().getCustShrtName();
		}
		arrayList.add(9, custShrtName);
		arrayList.add(10, financeDetail.getFinScheduleData().getFinanceMain().isNewRecord());
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	List<FinOption> finOptions = new ArrayList<>();

	public void onClick$btnNew_NewFinOption(Event event) {
		FinOption option = new FinOption();
		// FIXME GDP Default value set to 7, need to parameterized.
		option.setAlertDays(7);
		isFinOptionNew = true;
		option.setNewRecord(true);
		if (StringUtils.isBlank(option.getRecordType())) {
			option.setVersion(1);
			option.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}

		for (Component component : finOptionRows.getChildren()) {
			Row listitem = (Row) component;

			Combobox optionTypeCombo = (Combobox) getMandComponent(listitem, 1);
			String optionType = optionTypeCombo.getSelectedItem().getValue();

			if (PennantConstants.List_Select.equals(optionType)) {
				MessageUtil.showError("Option Type cannot be blank.");
				return;
			}
		}

		appendOption(option);
	}

	private void doFillFinOptionDetails(List<FinOption> finOptions) {
		if (this.finOptionRows.getChildren() != null) {
			this.finOptionRows.getChildren().clear();
		}

		for (FinOption option : finOptions) {
			appendOption(option);
		}
	}

	private void appendOption(FinOption option) {
		Row listitem = new Row();
		Hbox hbox;
		Space space;

		List<Property> optionTypes = AppStaticList.getFinOptions();

		listitem.setAttribute("option", option);
		Cell listcell;

		// Option
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		Combobox options = new Combobox();

		if (StringUtils.equals(option.getUserAction(), "Save")) {
			optionTypes = refreshOptionTypes(optionTypes);
		} else {
			options.setDisabled(true);
		}

		options.setWidth("100px");
		fillList(options, optionTypes, option.getOptionType());
		listcell = new Cell();
		hbox.appendChild(space);
		hbox.appendChild(options);
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		// Current option
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		Datebox currentOption = new Datebox();
		currentOption.setWidth("100px");
		currentOption.setFormat(DateFormat.SHORT_DATE.getPattern());
		currentOption.setValue(option.getCurrentOptionDate());
		listcell = new Cell();
		hbox.appendChild(space);
		hbox.appendChild(currentOption);
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		// Frequency
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		Combobox frequency = new Combobox();
		frequency.setWidth("100px");
		fillList(frequency, AppStaticList.getFrequencies(), option.getFrequency());
		listcell = new Cell();
		hbox.appendChild(space);
		hbox.appendChild(frequency);
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		// Notice Period Days
		Intbox noticePriodDays = new Intbox();
		noticePriodDays.setWidth("80px");
		noticePriodDays.setValue(option.getNoticePeriodDays());
		listcell = new Cell();
		listcell.appendChild(noticePriodDays);
		listcell.setParent(listitem);

		// Alert Days
		Intbox alertDays = new Intbox();
		alertDays.setWidth("80px");
		alertDays.setValue(option.getAlertDays());
		listcell = new Cell();
		listcell.appendChild(alertDays);
		listcell.setParent(listitem);

		// OptionExcercise
		Checkbox optionExcercise = new Checkbox();
		optionExcercise.setWidth("10px");
		optionExcercise.setChecked(option.isOptionExercise());
		listcell = new Cell();
		listcell.appendChild((Component) optionExcercise);
		listcell.setParent(listitem);

		optionExcercise.addForward("onClick", finOptionListWindow, "onChangeFrequency", listitem);

		appendNextOptionDate(option, listitem);

		// AlertType
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		Combobox alertType = new Combobox();
		alertType.setWidth("100px");
		fillList(alertType, AppStaticList.getAlertsFor(), option.getAlertType());
		listcell = new Cell();
		hbox.appendChild(space);
		hbox.appendChild(alertType);
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		ExtendedCombobox alertRoles = new ExtendedCombobox();
		alertRoles.setReadonly(true);
		alertRoles.setWidth("100px");
		alertRoles.setModuleName("OperationRoles");
		alertRoles.setValueColumn("RoleCd");
		alertRoles.setDescColumn("RoleDesc");
		alertRoles.setValidateColumns(new String[] { "RoleCd" });
		alertRoles.setMultySelection(true);
		alertRoles.setInputAllowed(false);
		listcell = new Cell();
		listcell.appendChild(alertRoles);
		listcell.setParent(listitem);

		if (option.getAlertToRoles() != null) {
			Map<String, Object> map = new HashMap<>();
			StringBuilder roles = new StringBuilder();
			for (String role : option.getAlertToRoles().split(",")) {
				role = StringUtils.trim(role);

				if (roles.length() > 0) {
					roles.append(",");
				}
				roles.append(role);

				SecurityRole securityRole = new SecurityRole();
				securityRole.setRoleCd(role);
				securityRole.setRoleCode(role);

				map.put(role, securityRole);
			}

			alertRoles.setValue(roles.toString());
			alertRoles.setAttribute("data", map);
			alertRoles.setTooltiptext(roles.toString());
			alertRoles.setInputAllowed(false);
			alertRoles.setSelectedValues(map);
		}

		ExtendedCombobox customerTemplate = new ExtendedCombobox();
		customerTemplate.setReadonly(true);
		customerTemplate.setWidth("100px");
		customerTemplate.setModuleName("MailTemplate");
		customerTemplate.setValueColumn("TemplateCode");
		customerTemplate.setValidateColumns(new String[] { "TemplateCode", "TemplateDesc" });
		Filter[] custTemplateFilter = new Filter[2];
		custTemplateFilter[0] = new Filter("TemplateFor", NotificationConstants.TEMPLATE_FOR_CN);
		custTemplateFilter[1] = new Filter("TemplateCode", "FIN_PUTCALL%", Filter.OP_LIKE);
		customerTemplate.setFilters(custTemplateFilter);

		if (option.getCustomerTemplateCode() != null) {
			customerTemplate.setValue(option.getCustomerTemplateCode());
		}

		listcell = new Cell();
		listcell.appendChild(customerTemplate);
		listcell.setParent(listitem);

		ExtendedCombobox userTemplate = new ExtendedCombobox();
		userTemplate.setReadonly(true);
		userTemplate.setWidth("100px");
		userTemplate.setModuleName("MailTemplate");
		userTemplate.setValueColumn("TemplateCode");
		userTemplate.setValidateColumns(new String[] { "TemplateCode", "TemplateDesc" });
		Filter[] userTemplateFilter = new Filter[2];
		userTemplateFilter[0] = new Filter("TemplateFor", NotificationConstants.TEMPLATE_FOR_AE);
		userTemplateFilter[1] = new Filter("TemplateCode", "FIN_PUTCALL%", Filter.OP_LIKE);
		userTemplate.setFilters(userTemplateFilter);

		if (option.getUserTemplateCode() != null) {
			userTemplate.setValue(option.getUserTemplateCode());
		}

		listcell = new Cell();
		listcell.appendChild(userTemplate);
		listcell.setParent(listitem);

		alertType.addForward("onChange", finOptionListWindow, "onChangeAlertType", listitem);

		frequency.addForward("onChange", finOptionListWindow, "onChangeFrequency", listitem);

		// Remarks
		Textbox remarks = new Textbox();
		remarks.setWidth("200px");
		remarks.setMaxlength(250);
		remarks.setValue(option.getRemarks());
		listcell = new Cell();
		listcell.appendChild(remarks);
		listcell.setParent(listitem);

		listcell = new Cell();
		if (option.isNewRecord()) {
			Button deleteButton = new Button();

			deleteButton.setLabel(Labels.getLabel("label_Finoption_Delete.value"));
			deleteButton.addForward("onClick", finOptionListWindow, "onButtonClick", listitem);
			deleteButton.setSclass("z-toolbarbutton");
			listcell.appendChild(deleteButton);
			listcell.setParent(listitem);
		}

		onChangeAlertTypes(listitem);

		this.recordStatus.setValue(option.getRecordStatus());

		this.finOptionRows.appendChild(listitem);
	}

	private List<Property> refreshOptionTypes(List<Property> optionTypes) {
		List<Property> list = new ArrayList<>();

		for (Property property : optionTypes) {
			boolean found = false;
			for (Component component : finOptionRows.getChildren()) {
				Row listitem = (Row) component;

				Combobox optionTypeCombo = (Combobox) getMandComponent(listitem, 1);
				String optionType = optionTypeCombo.getSelectedItem().getValue();

				if (StringUtils.equals(optionType, property.getValue())) {
					found = true;
				}
			}

			if (!found) {
				list.add(property);
			}
		}

		return list;
	}

	public void appendNextOptionDate(FinOption option, Row listitem) {
		Cell listcell;
		Datebox nextOption = new Datebox();
		nextOption.setReadonly(true);
		nextOption.setFormat(DateFormat.SHORT_DATE.getPattern());
		nextOption.setWidth("100px");
		String currentDate = option.getFrequency();

		if (currentDate == null) {
			listcell = new Cell();
			listcell.appendChild(nextOption);
			listcell.setParent(listitem);
			return;
		}

		if (currentDate.equalsIgnoreCase("M")) {
			nextOption.setValue(DateUtil.addMonths(option.getCurrentOptionDate(), 1));
		}

		if (currentDate.equalsIgnoreCase("Q")) {
			nextOption.setValue(DateUtil.addMonths(option.getCurrentOptionDate(), 3));
		}

		if (currentDate.equalsIgnoreCase("H")) {
			nextOption.setValue(DateUtil.addMonths(option.getCurrentOptionDate(), 6));
		}

		if (currentDate.equalsIgnoreCase("A")) {
			nextOption.setValue(DateUtil.addMonths(option.getCurrentOptionDate(), 12));
		}

		if (currentDate.equalsIgnoreCase("5")) {
			nextOption.setValue(DateUtil.addYears(option.getCurrentOptionDate(), 5));
		}

		if (currentDate.equalsIgnoreCase("8")) {
			nextOption.setValue(DateUtil.addYears(option.getCurrentOptionDate(), 8));
		}

		if (currentDate.equalsIgnoreCase("B")) {
			nextOption.setValue(DateUtil.addYears(option.getCurrentOptionDate(), 2));
		}

		if (option.getFrequency().equalsIgnoreCase("O")) {
			nextOption.setValue(null);
		}

		nextOption.setDisabled(true);
		listcell = new Cell();
		listcell.appendChild(nextOption);
		listcell.setParent(listitem);
	}

	public void onChangeFrequency(ForwardEvent event) {
		Row listitem = (Row) event.getData();
		onChangeFrequencyType(listitem);
	}

	public void onChangeAlertType(ForwardEvent event) {
		Row listitem = (Row) event.getData();
		onChangeAlertTypes(listitem);

	}

	private void onChangeAlertTypes(Row listitem) {
		Combobox alertType = (Combobox) getMandComponent(listitem, 8);
		String alertTypeValue = alertType.getSelectedItem().getValue();

		ExtendedCombobox alertRolesValue = (ExtendedCombobox) getComponent(listitem, 9);
		ExtendedCombobox customerTemplateValue = (ExtendedCombobox) getComponent(listitem, 10);
		ExtendedCombobox userTemplateValue = (ExtendedCombobox) getComponent(listitem, 11);

		userTemplateValue.setReadonly(false);
		customerTemplateValue.setReadonly(false);
		alertRolesValue.setReadonly(false);

		if ("#".equals(alertTypeValue)) {
			userTemplateValue.setReadonly(true);
			customerTemplateValue.setReadonly(true);
			alertRolesValue.setReadonly(true);
		} else if ("User".equals(alertTypeValue)) {
			customerTemplateValue.setReadonly(true);
			customerTemplateValue.setValue("");
		} else if ("Customer".equals(alertTypeValue)) {
			userTemplateValue.setReadonly(true);
			alertRolesValue.setReadonly(true);
			userTemplateValue.setValue("");
			alertRolesValue.setValue("");
		}

	}

	public void onChangeFrequencyType(Row listitem) {
		Datebox currentOption = (Datebox) getMandComponent(listitem, 2);
		Date option = currentOption.getValue();

		Datebox nextOption = (Datebox) getComponent(listitem, 7);
		Combobox frequencyComp = (Combobox) getMandComponent(listitem, 3);
		String frequency = frequencyComp.getSelectedItem().getValue();

		Checkbox optionExcersice = (Checkbox) getComponent(listitem, 6);
		boolean optExcerciseFlag = optionExcersice.isChecked();

		if (optExcerciseFlag) {
			nextOption.setValue(null);
		}

		if (option == null) {
			return;
		}

		if (frequency.equalsIgnoreCase("M") && !optExcerciseFlag) {
			nextOption.setValue(DateUtil.addMonths(option, 1));
		}

		if (frequency.equalsIgnoreCase("Q") && !optExcerciseFlag) {
			nextOption.setValue(DateUtil.addMonths(option, 3));
		}

		if (frequency.equalsIgnoreCase("H") && !optExcerciseFlag) {
			nextOption.setValue(DateUtil.addMonths(option, 6));
		}

		if (frequency.equalsIgnoreCase("A") && !optExcerciseFlag) {
			nextOption.setValue(DateUtil.addMonths(option, 12));
		}

		if (frequency.equalsIgnoreCase("5") && !optExcerciseFlag) {
			nextOption.setValue(DateUtil.addYears(option, 5));
		}

		if (frequency.equalsIgnoreCase("8") && !optExcerciseFlag) {
			nextOption.setValue(DateUtil.addYears(option, 8));
		}

		if (frequency.equalsIgnoreCase("B") && !optExcerciseFlag) {
			nextOption.setValue(DateUtil.addYears(option, 2));
		}

		if (frequency.equalsIgnoreCase("O")) {
			nextOption.setValue(null);
		}

		nextOption.setDisabled(true);

	}

	public void onButtonClick(ForwardEvent event) {
		Row listitem = (Row) event.getData();
		finOptionRows.removeChild(listitem);

	}

	private Component getComponent(Row row, int index) {
		int i = 1;
		for (Component component : row.getChildren()) {
			if (i == index) {
				return component.getFirstChild();
			}
			i++;
		}
		return null;
	}

	private Component getMandComponent(Row row, int index) {
		int i = 1;
		for (Component component : row.getChildren()) {
			if (i == index) {
				return component.getFirstChild().getFirstChild().getNextSibling();
			}
			i++;
		}
		return null;
	}

	public List<WrongValueException> doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);
		List<FinOption> finoptions = new ArrayList<>();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		Combobox firstOptionType = null;

		Set<String> optionTYpes = new HashSet<>();
		for (Component component : finOptionRows.getChildren()) {
			Row listitem = (Row) component;
			Combobox optionCombo = (Combobox) getMandComponent(listitem, 1);
			Clients.clearWrongValue(optionCombo);
			try {
				optionTYpes.add(optionCombo.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
				return wve;
			}

			if (optionTYpes.contains("PUT") || optionTYpes.contains("CALL") || optionTYpes.contains("PUT-CALL")) {
				if (firstOptionType == null) {
					firstOptionType = optionCombo;
				}
			}

		}

		boolean validOption = true;

		if (optionTYpes.contains("PUT") && optionTYpes.contains("CALL") && optionTYpes.contains("PUT-CALL")) {
			validOption = false;
		} else if (optionTYpes.contains("PUT") && optionTYpes.contains("CALL")) {
			validOption = false;
		} else if (optionTYpes.contains("PUT") && optionTYpes.contains("PUT-CALL")) {
			validOption = false;
		} else if (optionTYpes.contains("CALL") && optionTYpes.contains("PUT-CALL")) {
			validOption = false;
		}

		if (!validOption) {
			wve.add(new WrongValueException(firstOptionType, "Either PUT/CALL/PUT-CALL allowed in the list"));
		}

		for (Component component : finOptionRows.getChildren()) {
			Row listitem = (Row) component;
			FinOption option = (FinOption) listitem.getAttribute("option");

			try {
				Combobox optionCombo = (Combobox) getMandComponent(listitem, 1);
				option.setOptionType(optionCombo.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Datebox currentOption = (Datebox) getMandComponent(listitem, 2);
				option.setCurrentOptionDate(currentOption.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Combobox frequency = (Combobox) getMandComponent(listitem, 3);
				option.setFrequency(frequency.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Intbox noticePeriodDays = (Intbox) getComponent(listitem, 4);
				option.setNoticePeriodDays(noticePeriodDays.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Intbox alertDays = (Intbox) getComponent(listitem, 5);
				if (SysParamUtil.isAllowed(SMTParameterConstants.PUTCALL_DEFAULT_ALERTDAYS_REQUIRED)) {
					if (alertDays.getValue() == 0) {
						option.setAlertDays(7);
					}
				} else {
					option.setAlertDays(alertDays.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Checkbox optionExcersice = (Checkbox) getComponent(listitem, 6);
				option.setOptionExercise(optionExcersice.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Datebox nextOption = (Datebox) getComponent(listitem, 7);
				option.setNextOptionDate(nextOption.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Combobox alertType = (Combobox) getMandComponent(listitem, 8);
				option.setAlertType(alertType.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				ExtendedCombobox alertRoles = (ExtendedCombobox) getComponent(listitem, 9);

				alertRoles.getValue();

				if (StringUtils.isNotEmpty(alertRoles.getValue())) {
					Map<String, Object> roles = getSelectedValues(alertRoles);
					if (roles != null) {
						if (!alertRoles.getButton().isDisabled() && roles.size() > 5) {
							throw new WrongValueException(alertRoles.getButton(),
									"The number of roles should not exceed more than 5.");
						}

						StringBuilder data = new StringBuilder();
						for (String role : roles.keySet()) {
							if (data.length() > 0) {
								data.append(",");
							}
							data.append(role);
						}

						option.setAlertToRoles(data.toString());
					} else {
						option.setAlertToRoles(null);
					}
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				ExtendedCombobox userTemplate = (ExtendedCombobox) getComponent(listitem, 11);
				userTemplate.getValidatedValue();
				Object obj1 = userTemplate.getObject();
				if (obj1 != null) {
					option.setUserTemplate(((MailTemplate) obj1).getTemplateId());
					option.setUserTemplateCode(((MailTemplate) obj1).getTemplateCode());
				} else {
					option.setUserTemplateCode(null);
					option.setUserTemplateName(null);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				ExtendedCombobox customerTemplate = (ExtendedCombobox) getComponent(listitem, 10);
				customerTemplate.getValidatedValue();
				Object obj = customerTemplate.getObject();
				if (obj != null) {
					option.setCustomerTemplate(((MailTemplate) obj).getTemplateId());
					option.setCustomerTemplateCode(((MailTemplate) obj).getTemplateCode());
				} else {
					option.setCustomerTemplateCode(null);
					option.setCustomerTemplateName(null);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Textbox remarks = (Textbox) getComponent(listitem, 12);
				option.setRemarks(remarks.getText());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			boolean isNew = option.isNewRecord();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isBlank(option.getRecordType())) {
					option.setVersion(option.getVersion() + 1);
					if (isNew) {
						option.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						option.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						option.setNewRecord(true);
					}
				}
			} else {
				if (isNewCustomer()) {
					if (option.isNewRecord()) {
						option.setVersion(1);
						option.setRecordType(PennantConstants.RCD_ADD);
					} else {
					}

					if (StringUtils.isBlank(option.getRecordType())) {
						option.setVersion(option.getVersion() + 1);
						option.setRecordType(PennantConstants.RCD_UPD);
					}

					if (option.getRecordType().equals(PennantConstants.RCD_ADD) && option.isNewRecord()) {
					} else if (option.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						option.setVersion(option.getVersion() + 1);
					}
				} else {
					option.setVersion(option.getVersion() + 1);
					if (isNew) {
					} else {
					}
				}
			}
			option.setRecordStatus(this.recordStatus.getValue());
			finoptions.add(option);
		}

		this.financeDetail.setFinOptions(finoptions);

		logger.debug(Literal.LEAVING);

		return wve;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getSelectedValues(ExtendedCombobox extendedCombobox) {
		Object object = extendedCombobox.getAttribute("data");
		return (Map<String, Object>) object;
	}

	public boolean doSave(FinanceDetail aFinanceDetail, Tab tab) {
		logger.debug(Literal.ENTERING);
		doClearMessage();

		doSetValidation();

		List<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);

		List<FinOption> options = new ArrayList<>();

		for (Component component : finOptionRows.getChildren()) {
			Row listitem = (Row) component;
			FinOption option = (FinOption) listitem.getAttribute("option");
			option.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			options.add(option);
		}

		aFinanceDetail.setFinOptions(options);

		logger.debug(Literal.LEAVING);

		return true;
	}

	private void showErrorDetails(List<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

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

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		for (Component component : finOptionRows.getChildren()) {
			Row row = (Row) component;
			Combobox optionCombo = (Combobox) getMandComponent(row, 1);
			Datebox currentOption = (Datebox) getMandComponent(row, 2);
			Combobox frequency = (Combobox) getMandComponent(row, 3);
			Intbox alertDays = (Intbox) getComponent(row, 4);
			Intbox noticePeriodDays = (Intbox) getComponent(row, 5);
			Combobox alertType = (Combobox) getMandComponent(row, 8);
			ExtendedCombobox alertToRoles = (ExtendedCombobox) getComponent(row, 9);
			ExtendedCombobox userTemplate = (ExtendedCombobox) getComponent(row, 10);
			ExtendedCombobox customerTemplagte = (ExtendedCombobox) getComponent(row, 11);

			optionCombo.setConstraint("");
			currentOption.setConstraint("");
			frequency.setConstraint("");
			alertDays.setConstraint("");
			noticePeriodDays.setConstraint("");
			alertType.setConstraint("");
			alertToRoles.setConstraint("");
			userTemplate.setConstraint("");
			customerTemplagte.setConstraint("");

		}
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		loanStartDt = this.financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
		for (Component component : finOptionRows.getChildren()) {
			Row row = (Row) component;
			Combobox optionCombo = (Combobox) getMandComponent(row, 1);
			Datebox currentOption = (Datebox) getMandComponent(row, 2);
			Combobox frequency = (Combobox) getMandComponent(row, 3);
			Intbox noticePeriodDays = (Intbox) getComponent(row, 4);
			Intbox alertDays = (Intbox) getComponent(row, 5);
			Combobox alertType = (Combobox) getMandComponent(row, 8);
			ExtendedCombobox alertRoles = (ExtendedCombobox) getComponent(row, 9);
			ExtendedCombobox userTemplate = (ExtendedCombobox) getComponent(row, 10);
			ExtendedCombobox customerTemplate = (ExtendedCombobox) getComponent(row, 11);

			String frequencyValue = frequency.getSelectedItem().getValue();
			Date appDate = SysParamUtil.getAppDate();
			Date selectedOptionDate = currentOption.getValue();

			int days = 0;
			if (frequencyValue.equals("M"))
				days = 30;
			else if (frequencyValue.equals("Q"))
				days = 90;
			else if (frequencyValue.equals("H"))
				days = 180;
			else if (frequencyValue.equals("A"))
				days = 365;
			else if (frequencyValue.equals("5"))
				days = 1825;
			if (frequencyValue.equals("8"))
				days = 2920;
			else if (frequencyValue.equals("B"))
				days = 750;

			String optionComboLabel = Labels.getLabel("FinOption_Option.label");
			if (!optionCombo.isDisabled()) {
				optionCombo.setConstraint(new StaticListValidator(AppStaticList.getFinOptions(), optionComboLabel));
			}

			if (!currentOption.isDisabled()) {
				selectedOptionDate = currentOption.getValue();
				maturityDate = this.financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate();
				String receivableDateLabel = Labels.getLabel("label_FinOptionDialog_CurrentOptionDate.value");
				if (selectedOptionDate == null) {
					currentOption.setConstraint(new PTDateValidator(receivableDateLabel, true));
				} else if (DateUtil.compare(selectedOptionDate, appDate) < 0
						|| DateUtil.compare(selectedOptionDate, maturityDate) > 0) {

					if (maturityDate == loanStartDt) {
						currentOption
								.setConstraint(new PTDateValidator(receivableDateLabel, true, appDate, null, false));
					} else {

						currentOption.setConstraint(
								new PTDateValidator(receivableDateLabel, true, loanStartDt, maturityDate, false));
					}
				}
			}

			if (!frequency.isDisabled()) {
				frequency.setConstraint(new StaticListValidator(AppStaticList.getFrequencies(),
						Labels.getLabel("label_FinOptionDialog_Frequency.value")));
			}

			if (!alertDays.isDisabled() && alertDays.getValue() != 0) {
				alertDays.setConstraint(new PTNumberValidator(Labels.getLabel("label_FinOptionDialog_AlertDays.value"),
						true, false, 0, days));
			}

			if (!noticePeriodDays.isDisabled() && noticePeriodDays.getValue() != 0) {
				noticePeriodDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinOptionDialog_NoticePeriodDays.value"), true, false, 0, days));
			}

			if (!alertType.isDisabled()) {
				alertType.setConstraint(new StaticListValidator(AppStaticList.getAlertsFor(),
						Labels.getLabel("label_FinOptionDialog_AlertType.value")));

			}

			if (!alertRoles.getButton().isDisabled() && "User".equals(alertType) || "Both".equals(alertType)) {
				alertRoles.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinOptionDialog_AlertRoles.value"), null, true));
			}

			if (!userTemplate.getButton().isDisabled() && "User".equals(alertType)) {
				userTemplate.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinOptionDialog_UserTemplate.value"), null, true));
			}

			if (!customerTemplate.getButton().isDisabled() && "Customer".equals(alertType)) {
				customerTemplate.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinOptionDialog_CustomerTemplate.value"), null, true));
			}

		}
		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (loanEnquiry) {
			for (Component component : finOptionRows.getChildren()) {
				Row listitem = (Row) component;
				Combobox optionCombo = (Combobox) getMandComponent(listitem, 1);
				Datebox currentOption = (Datebox) getMandComponent(listitem, 2);
				Combobox frequency = (Combobox) getMandComponent(listitem, 3);
				Intbox alertDays = (Intbox) getComponent(listitem, 4);
				Intbox noticePeriodDays = (Intbox) getComponent(listitem, 5);
				Combobox alertType = (Combobox) getMandComponent(listitem, 8);
				ExtendedCombobox alertRoles = (ExtendedCombobox) getComponent(listitem, 9);
				ExtendedCombobox customerTemplate = (ExtendedCombobox) getComponent(listitem, 10);
				ExtendedCombobox UserTemplate = (ExtendedCombobox) getComponent(listitem, 11);

				optionCombo.setDisabled(true);
				currentOption.setDisabled(true);
				frequency.setDisabled(true);
				alertDays.setDisabled(true);
				noticePeriodDays.setDisabled(true);
				alertType.setDisabled(true);
				alertRoles.setReadonly(true);
				customerTemplate.setReadonly(true);
				UserTemplate.setReadonly(true);
			}
		}

		if (isWorkFlowEnabled()) {

			// Notes button
			if (!fromLoan) {
				if (StringUtils.isNotBlank(this.finMaintainInstruction.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
			}

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			List<FinOption> finOptions = financeDetail.getFinOptions();

			for (FinOption finOption : finOptions) {
				if (finOption.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}
			}

			this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
		}
		logger.debug(Literal.LEAVING);
	}

	private void doReadOnly(List<FinOption> finOptions) {

		for (FinOption finOption : finOptions) {
			if (finOption.getRecordStatus() != null && StringUtils.equals("Submitted", finOption.getRecordStatus())) {
				for (Component component : finOptionRows.getChildren()) {
					Row listitem = (Row) component;
					Combobox optionCombo = (Combobox) getMandComponent(listitem, 1);
					Datebox currentOption = (Datebox) getMandComponent(listitem, 2);
					Combobox frequency = (Combobox) getMandComponent(listitem, 3);
					Intbox alertDays = (Intbox) getComponent(listitem, 4);
					Intbox noticePeriodDays = (Intbox) getComponent(listitem, 5);
					Checkbox optionExcersice = (Checkbox) getComponent(listitem, 6);
					Datebox nextOption = (Datebox) getComponent(listitem, 7);
					Combobox alertType = (Combobox) getMandComponent(listitem, 8);
					ExtendedCombobox alertRoles = (ExtendedCombobox) getComponent(listitem, 9);
					ExtendedCombobox userTemplate = (ExtendedCombobox) getComponent(listitem, 10);
					ExtendedCombobox customerTemplate = (ExtendedCombobox) getComponent(listitem, 11);

					optionCombo.setDisabled(true);
					currentOption.setDisabled(true);
					frequency.setDisabled(true);
					alertDays.setDisabled(true);
					noticePeriodDays.setDisabled(true);
					alertType.setDisabled(true);
					optionExcersice.setDisabled(true);
					nextOption.setDisabled(true);
					alertRoles.setReadonly(true);
					userTemplate.setReadonly(true);
					customerTemplate.setReadonly(true);
				}
			}
		}
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void onClick$btnSave(Event event) {
		doSave();
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);

		FinMaintainInstruction aFinMaintainInstruction = new FinMaintainInstruction();
		aFinMaintainInstruction = ObjectUtil.clone(finMaintainInstruction);

		doClearMessage();

		doSetValidation();

		List<WrongValueException> wve = doWriteComponentsToBean();

		Tab tab = null;

		showErrorDetails(wve, tab);

		finMaintainInstruction.setFinReference(String.valueOf(headerList.get(3)));
		finMaintainInstruction.setEvent("FinOptions");

		// List
		finMaintainInstruction.setFinOptions(financeDetail.getFinOptions());
		finMaintainInstruction.setRecordStatus(this.recordStatus.getValue());

		setFinInsturctionDetails(aFinMaintainInstruction);

		if (aFinMaintainInstruction.getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			aFinMaintainInstruction.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		boolean isNew;
		isNew = aFinMaintainInstruction.isNewRecord();
		String tranType = null;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinMaintainInstruction.getRecordType())) {
				aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
				if (isNew) {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinMaintainInstruction.setNewRecord(true);
				}
			}
		} else {
			if (isNewCustomer()) {
				if (isFinOptionNew) {
					aFinMaintainInstruction.setVersion(1);
					aFinMaintainInstruction.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFinMaintainInstruction.getRecordType())) {
					aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
					aFinMaintainInstruction.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RCD_ADD) && isFinOptionNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (doProcess(aFinMaintainInstruction, tranType)) {
				refreshList();

				String msg = PennantApplicationUtil.getSavingStatus(aFinMaintainInstruction.getRoleCode(),
						aFinMaintainInstruction.getNextRoleCode(), aFinMaintainInstruction.getFinReference() + "",
						" Finoption Details ", aFinMaintainInstruction.getRecordStatus());
				if (StringUtils.equals(aFinMaintainInstruction.getRecordStatus(),
						PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Finoption Detail with Reference " + aFinMaintainInstruction.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}

				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	protected boolean doProcess(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFinMaintainInstruction.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinMaintainInstruction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinMaintainInstruction.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFinMaintainInstruction.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinMaintainInstruction.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinMaintainInstruction);
				}

				if (isNotesMandatory(taskId, aFinMaintainInstruction)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aFinMaintainInstruction.setTaskId(taskId);
			aFinMaintainInstruction.setNextTaskId(nextTaskId);
			aFinMaintainInstruction.setRoleCode(getRole());
			aFinMaintainInstruction.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			String operationRefs = getServiceOperations(taskId, aFinMaintainInstruction);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinMaintainInstruction, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = financeSelectCtrl.getSearchObj(true);
		financeSelectCtrl.getPagingFinanceList().setActivePage(0);
		financeSelectCtrl.getPagedListWrapper().setSearchObject(soFinanceMain);
		if (financeSelectCtrl.getListBoxFinance() != null) {
			financeSelectCtrl.getListBoxFinance().getListModel();
		}
	}

	private AuditHeader getAuditHeader(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinMaintainInstruction.getBefImage(),
				aFinMaintainInstruction);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinMaintainInstruction.getUserDetails(),
				getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FinMaintainInstruction aFinMaintainInstruction = (FinMaintainInstruction) aAuditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;
		int seqNo = 0;

		if (aFinMaintainInstruction.getExtendedFieldRender() != null) {
			ExtendedFieldRender details = aFinMaintainInstruction.getExtendedFieldRender();
			details.setReference(aFinMaintainInstruction.getFinReference());
			details.setSeqNo(++seqNo);
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(aFinMaintainInstruction.getRecordStatus());
			details.setRecordType(aFinMaintainInstruction.getRecordType());
			details.setVersion(aFinMaintainInstruction.getVersion());
			details.setWorkflowId(aFinMaintainInstruction.getWorkflowId());
			details.setTaskId(aFinMaintainInstruction.getTaskId());
			details.setNextTaskId(aFinMaintainInstruction.getNextTaskId());
			details.setRoleCode(aFinMaintainInstruction.getRoleCode());
			details.setNextRoleCode(aFinMaintainInstruction.getNextRoleCode());
			details.setNewRecord(aFinMaintainInstruction.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aFinMaintainInstruction.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(aFinMaintainInstruction.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = finOptionMaintanceService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = finOptionMaintanceService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = finOptionMaintanceService.doApprove(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = finOptionMaintanceService.doReject(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.finOptionListWindow, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.finOptionListWindow, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finMaintainInstruction), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());
		logger.debug("Leaving");
		return processCompleted;
	}

	public void setFinInsturctionDetails(FinMaintainInstruction aFinMaintainInstruction) {
		FinScheduleData schdData = this.financeDetail.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		aFinMaintainInstruction.setFinID(fm.getFinID());
		aFinMaintainInstruction.setFinReference(fm.getFinReference());
		aFinMaintainInstruction.setEvent(this.moduleCode);
		aFinMaintainInstruction.setRecordStatus(this.recordStatus.getValue());
		aFinMaintainInstruction.setFinOptions(this.financeDetail.getFinOptions());
	}

	@Override
	protected String getReference() {
		return String.valueOf(financeDetail.getFinScheduleData().getFinReference());
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
		if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	public void setExtendedFieldCtrl(ExtendedFieldCtrl extendedFieldCtrl) {
		this.extendedFieldCtrl = extendedFieldCtrl;
	}
}
