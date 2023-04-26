package com.pennant.webui.applicationmaster.loantypewriteoff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.LoanTypeWriteOffService;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.rmtmasters.financetype.FinanceTypeListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectLoanTypeWriteOffDialog extends GFCBaseCtrl<FinTypeWriteOff> {
	private static final Logger logger = LogManager.getLogger(SelectLoanTypeWriteOffDialog.class);

	private static final long serialVersionUID = -5898229156972529248L;
	protected Window windowSelectLoanTypeWriteOffDialog;
	protected ExtendedCombobox finType;
	protected Button btnProceed;

	private LoanTypeWriteOffListCtrl loanTypeWriteOffListCtrl;
	private FinTypeWriteOff loanTypeWriteOff;
	private transient LoanTypeWriteOffService loanTypeWriteOffService;
	private FinanceTypeListCtrl financeTypeListCtrl;
	private FinanceType financeType;

	/**
	 * default constructor.<br>
	 */
	public SelectLoanTypeWriteOffDialog() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LoanTypeWriteOffDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$windowSelectLoanTypeWriteOffDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(windowSelectLoanTypeWriteOffDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("loanTypeWriteOffListCtrl")) {
			this.loanTypeWriteOffListCtrl = (LoanTypeWriteOffListCtrl) arguments.get("loanTypeWriteOffListCtrl");
		}

		if (arguments.containsKey("loanTypeWriteOff")) {
			this.loanTypeWriteOff = (FinTypeWriteOff) arguments.get("loanTypeWriteOff");
		}

		doLoadWorkFlow(this.loanTypeWriteOff.isWorkflow(), this.loanTypeWriteOff.getWorkflowId(),
				this.loanTypeWriteOff.getNextTaskId());

		if (isWorkFlowEnabled() && !enqiryModule) {
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
		}

		doSetFieldProperties();
		doCheckRights();

		this.windowSelectLoanTypeWriteOffDialog.doModal();

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnProceed.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		doSetValidation();
		doWriteComponentsToBean(this.loanTypeWriteOff);
		List<FinTypeWriteOff> finTypeList = loanTypeWriteOffService.getWriteOffMappingById(this.finType.getValue());

		if (finTypeList.size() > 0) {
			MessageUtil.showMessage("Loan type already exist.");
			return;
		}
		doShowDialogPage(this.loanTypeWriteOff);

		logger.debug(Literal.LEAVING + event.toString());

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financeType The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinTypeWriteOff financeType) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> aruments = new HashMap<>();

		aruments.put("loanTypeWriteOff", financeType);
		aruments.put("loanTypeWriteOffListCtrl", this.loanTypeWriteOffListCtrl);
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);

		// call the ZUL-file with the parameters packed in a map
		try {

			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/LoanTypeWriteOff/LoanTypeWriteOffDialog.zul",
					null, aruments);

			this.windowSelectLoanTypeWriteOffDialog.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SelectLoanTypeWriteOffDialog_FinType.value"), null, true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finType.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceType
	 */
	public void doWriteComponentsToBean(FinTypeWriteOff aFinanceType) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Finance Type
		try {
			String finTypeValue = StringUtils.trimToEmpty(this.finType.getValue());
			aFinanceType.setLoanType(finTypeValue);
			aFinanceType.setFinTypeDesc(this.finType.getDescription());
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceTypeListCtrl getFinanceTypeListCtrl() {
		return financeTypeListCtrl;
	}

	public void setFinanceTypeListCtrl(FinanceTypeListCtrl financeTypeListCtrl) {
		this.financeTypeListCtrl = financeTypeListCtrl;
	}

	public FinanceType getFinanceType() {
		return financeType;
	}

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public LoanTypeWriteOffListCtrl getLoanTypeWriteOffListCtrl() {
		return loanTypeWriteOffListCtrl;
	}

	public void setLoanTypeWriteOffListCtrl(LoanTypeWriteOffListCtrl loanTypeWriteOffListCtrl) {
		this.loanTypeWriteOffListCtrl = loanTypeWriteOffListCtrl;
	}

	public FinTypeWriteOff getLoanTypeWriteOff() {
		return loanTypeWriteOff;
	}

	public void setLoanTypeWriteOff(FinTypeWriteOff loanTypeWriteOff) {
		this.loanTypeWriteOff = loanTypeWriteOff;
	}

	public void setLoanTypeWriteOffService(LoanTypeWriteOffService loanTypeWriteOffService) {
		this.loanTypeWriteOffService = loanTypeWriteOffService;
	}
}
