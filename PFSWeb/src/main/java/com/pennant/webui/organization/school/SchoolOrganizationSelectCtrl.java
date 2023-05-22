package com.pennant.webui.organization.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.incomeexpensedetail.service.IncomeExpenseDetailService;
import com.pennanttech.pff.organization.OrganizationUtil;
import com.pennanttech.pff.organization.model.IncomeExpenseHeader;
import com.pennanttech.pff.organization.model.Organization;

public class SchoolOrganizationSelectCtrl extends GFCBaseCtrl<IncomeExpenseHeader> {
	private static final long serialVersionUID = 3473801015405406986L;
	private static final Logger logger = LogManager.getLogger(SchoolOrganizationSelectCtrl.class);

	protected Window window_SchoolOrganizationSelect;
	protected Borderlayout borderLayout_SchoolOrganizationTypeList;

	protected Row customerRow;
	protected ExtendedCombobox customer;
	protected Row financialYearRow;
	protected Combobox financialYear;

	protected IncomeExpenseDetailListCtrl incomeExpenseDetailListCtrl;
	private IncomeExpenseHeader incomeExpenseHeader;

	@Autowired
	private IncomeExpenseDetailService incomeExpenseDetailService;

	private List<ValueLabel> years = OrganizationUtil.getFinancialYears();

	public SchoolOrganizationSelectCtrl() {
		super();
	}

	public void onCreate$window_SchoolOrganizationSelect(Event event) {
		logger.debug(Literal.ENTERING);

		this.incomeExpenseHeader = (IncomeExpenseHeader) arguments.get("incomeExpenseHeader");

		if (arguments.containsKey("incomeExpenseDetailListCtrl")) {
			this.incomeExpenseDetailListCtrl = (IncomeExpenseDetailListCtrl) arguments
					.get("incomeExpenseDetailListCtrl");
		}

		// Store the before image.
		IncomeExpenseHeader incomeExpenseHeader = new IncomeExpenseHeader();
		BeanUtils.copyProperties(this.incomeExpenseHeader, incomeExpenseHeader);
		this.incomeExpenseHeader.setBefImage(incomeExpenseHeader);

		// Render the page and display the data.
		doLoadWorkFlow(this.incomeExpenseHeader.isWorkflow(), this.incomeExpenseHeader.getWorkflowId(),
				this.incomeExpenseHeader.getNextTaskId());

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

		fillComboBox(this.financialYear, "", years, "");
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

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
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

		doSetValidation();
		doWriteComponentsToBean(incomeExpenseHeader);
		boolean isExist = incomeExpenseDetailService.isExist(incomeExpenseHeader.getCustCif(),
				incomeExpenseHeader.getFinancialYear());
		if (isExist) {
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
			Executions.createComponents("/WEB-INF/pages/Organization/School/IncomeExpenseDetailDialog.zul", null, arg);
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
		if (!this.financialYear.isDisabled()) {
			this.financialYear.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_SchoolOrganizationSelect_financialYear.value"), years, true));
		}

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
			if (!"#".equals(getComboboxValue(this.financialYear))) {
				incomeExpenseHeader.setFinancialYear(Integer.parseInt(getComboboxValue(this.financialYear)));
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
