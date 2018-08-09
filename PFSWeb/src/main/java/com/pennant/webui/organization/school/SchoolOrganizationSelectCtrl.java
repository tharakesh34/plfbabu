package com.pennant.webui.organization.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.incomeexpensedetail.service.IncomeExpenseDetailService;
import com.pennanttech.pff.organization.model.Organization;
import com.pennanttech.pff.organization.school.model.IncomeExpenseHeader;

public class SchoolOrganizationSelectCtrl extends GFCBaseCtrl<IncomeExpenseHeader> {
	private static final long serialVersionUID = 3473801015405406986L;

	private static final Logger logger = Logger.getLogger(SchoolOrganizationSelectCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SchoolOrganizationSelect; 				// autoWired
	protected Borderlayout borderLayout_SchoolOrganizationTypeList; 	// autoWired
		
	protected Radiogroup custType;
	protected Radio custType_Existing;
	protected Radio custType_Prospect;
	
	protected Row customerRow;
	protected ExtendedCombobox customer;
	protected Row typeRow;
	protected Combobox type;
	protected Row       incomeTypeRow;
	protected Combobox incomeType;
	protected Row categoryRow;
	protected Combobox category;
	
	protected Row       academicYearRow;
	protected Combobox    academicYear;
	
	List<ValueLabel> types = new ArrayList<>();
	List<ValueLabel> years = new ArrayList<>();
	List<ValueLabel> categories = new ArrayList<>();
	
	protected IncomeExpenseDetailListCtrl incomeExpenseDetailListCtrl;
	private IncomeExpenseHeader incomeExpenseHeader;
	@Autowired
	protected IncomeExpenseDetailService incomeExpenseDetailService;

	/**
	 * default constructor.<br>
	 */
	public SchoolOrganizationSelectCtrl() {
		super();
	}

	// Component Events

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SchoolOrganizationSelect(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		this.incomeExpenseHeader = (IncomeExpenseHeader) arguments.get("incomeExpenseHeader");
		
		if (arguments.containsKey("incomeExpenseDetailListCtrl")) {
			this.incomeExpenseDetailListCtrl = (IncomeExpenseDetailListCtrl) arguments.get("incomeExpenseDetailListCtrl");
		} 
		
		// Store the before image.
		IncomeExpenseHeader incomeExpenseHeader = new IncomeExpenseHeader();
		BeanUtils.copyProperties(this.incomeExpenseHeader, incomeExpenseHeader);
		this.incomeExpenseHeader.setBefImage(incomeExpenseHeader);

		// Render the page and display the data.
		doLoadWorkFlow(this.incomeExpenseHeader.isWorkflow(), this.incomeExpenseHeader.getWorkflowId(),
				this.incomeExpenseHeader.getNextTaskId());

		/*if (isWorkFlowEnabled() && !enqiryModule) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
		}*/
		doSetFieldProperties();
		this.window_SchoolOrganizationSelect.doModal();
	
		logger.debug(Literal.LEAVING);
	}
	
	
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Customer
		this.customer.setMaxlength(50);
		this.customer.setTextBoxWidth(120);
		this.customer.setMandatoryStyle(true);
		this.customer.setModuleName("Organization");
		this.customer.setValueColumn("Cif");
		this.customer.setDescColumn("CustShrtName");
		this.customer.setValidateColumns(new String[] { "Cif" });
		/*Filter[] custCifFilter = new Filter[1];
		List<Long> custid = organizationService.getCustomerId();
		custCifFilter[0] = new Filter("custid",custid, Filter.OP_IN);
		customer.setFilters(custCifFilter);*/
		
		types.add(new ValueLabel("1", "Income"));
		types.add(new ValueLabel("2", "Expense"));
		fillComboBox(this.type, "", types,"");
		List<ValueLabel> incomeTypes = new ArrayList<>();
		
		int currentYear = DateUtil.getYear(DateUtility.getAppDate());
		for(int i=0; i<=10;i++){
			int year=currentYear;
			currentYear = year-1;
			//years.add(new ValueLabel(String.valueOf(year),String.valueOf(currentYear+"-").concat(String.valueOf(year))));
			years.add(new ValueLabel(String.valueOf(year),String.valueOf(year)));
		}
		fillComboBox(this.academicYear, "", years,"");
		
		categories.add(new ValueLabel("LKG", "LKG"));
		categories.add(new ValueLabel("UKG", "UKG"));
		fillComboBox(this.category, "", categories,"");
		
		incomeTypes.add(new ValueLabel("1", "CoreIncome"));
		incomeTypes.add(new ValueLabel("2", "NonCoreIncome"));
		fillComboBox(this.incomeType, "", incomeTypes,"");
		logger.debug(Literal.LEAVING);
	}
	
	public void onFulfill$customer(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = customer.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.customer.setValue("");
			this.customer.setDescription("");
			this.customer.setAttribute("CustId", null);
		} else {
			Organization details = (Organization) dataObject;
				this.customer.setAttribute("CustId", details.getCustId());
				this.customer.setAttribute("Name", details.getName());
				this.customer.setAttribute("OrgId", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}
	
	public void onChange$type(Event event) {
		logger.debug(Literal.ENTERING);
		String type = this.type.getSelectedItem().getValue();
		if ("#".equals(type)) {
			this.incomeTypeRow.setVisible(true);
		} else {
			visibleComponents(Integer.parseInt(type));
		}
		logger.debug(Literal.LEAVING);
	}
	
	private void visibleComponents(Integer type) {
		if (type == 2) {
			this.incomeTypeRow.setVisible(false);
		} else {
			this.incomeTypeRow.setVisible(true);
		}
	}
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}
	

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final IncomeExpenseHeader incomeExpenseHeader = new IncomeExpenseHeader();
		BeanUtils.copyProperties(this.incomeExpenseHeader, incomeExpenseHeader);
		boolean isNew = false;
		doSetValidation();
		doWriteComponentsToBean(incomeExpenseHeader);
		boolean isExist = incomeExpenseDetailService.isExist(incomeExpenseHeader.getCustCif(), incomeExpenseHeader.getFinancialYear());
		if(isExist){
			StringBuilder errorMsg = new StringBuilder();
			errorMsg.append("CustCif:");
			errorMsg.append(incomeExpenseHeader.getCustCif());
			errorMsg.append(" and FinancialYear:");
			errorMsg.append(String.valueOf(incomeExpenseHeader.getFinancialYear()));
			errorMsg.append("alreay exist.");
			MessageUtil.showError(errorMsg.toString());
			return;
		}
		doShowDialog(incomeExpenseHeader);
		this.window_SchoolOrganizationSelect.onClose();
		logger.debug(Literal.LEAVING);
	}    
	
	
	private void doShowDialog(IncomeExpenseHeader incomeExpenseHeader) {
		Map<String, Object> arg = new HashMap<>();
		arg.put("incomeExpenseHeader", incomeExpenseHeader);
		arg.put("incomeExpenseDetailListCtrl", this.incomeExpenseDetailListCtrl);
		arg.put("enqiryModule", enqiryModule);
		try {
			Executions.createComponents("/WEB-INF/pages/Organization/School/IncomeExpenseDetailDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.customer.isReadonly()) {
			this.customer.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SchoolOrganizationSelect_CustomerCIF.value"), null, true, true));
		}
		if (!this.academicYear.isDisabled()) {
			this.academicYear.setConstraint(new PTListValidator(
					Labels.getLabel("label_SchoolOrganizationSelect_academicYear.value"), years, true));
		}
		/*if (!this.type.isDisabled()) {
			this.type.setConstraint(new PTListValidator(
					Labels.getLabel("label_SchoolOrganizationSelect_type.value"), types, true));
		}
		if (this.incomeTypeRow.isVisible() && !this.incomeType.isDisabled()) {
			this.type.setConstraint(new PTListValidator(
					Labels.getLabel("label_SchoolOrganizationSelect_Incometype.value"), types, true));
		}
		if (!this.category.isDisabled()) {
			this.category.setConstraint(new PTListValidator(
					Labels.getLabel("label_SchoolOrganizationSelect_category.value"), categories, true));
		}*/
		logger.debug(Literal.LEAVING);
	}
	
	public void doWriteComponentsToBean(IncomeExpenseHeader incomeExpenseHeader) {
		logger.debug(Literal.ENTERING);
		List<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.customer.getValidatedValue();
			Object object = this.customer.getAttribute("CustId");
			incomeExpenseHeader.setOrgId(Long.parseLong(this.customer.getAttribute("OrgId").toString()));
			incomeExpenseHeader.setName(this.customer.getAttribute("Name").toString());
			incomeExpenseHeader.setCustCif(this.customer.getValue());
			if (object != null) {
				incomeExpenseHeader.setCustId(Long.parseLong(object.toString()));
			} else {
				incomeExpenseHeader.setCustId(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!"#".equals(getComboboxValue(this.academicYear))) {
				incomeExpenseHeader.setFinancialYear(Integer.parseInt(getComboboxValue(this.academicYear)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}
	
}
