package com.pennant.webui.financemanagement.receipts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectLoanClosureEnquiryListCtrl extends GFCBaseCtrl<ForeClosure> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger logger = Logger.getLogger(SelectLoanClosureEnquiryListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectLoanClosureEnquiryList;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;
	private LoanClosureEnquiryDialogCtrl loanClosureEnquiryDialogCtrl;
	private String finRefValue;
	protected Tabbox tabbox;

	/**
	 * default constructor.<br>
	 */
	public SelectLoanClosureEnquiryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LoanClosureEnquiry";
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectLoanClosureEnquiryList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectLoanClosureEnquiryList);
		try {
			doCheckRights();
			doSetFieldProperties();
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
		} catch (Exception e) {
			closeDialog();
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		this.window_SelectLoanClosureEnquiryList.doModal();

		logger.debug("Leaving ");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceDetail");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		Filter[] filter = new Filter[3];
		filter[0] = new Filter("FinStartDate", DateUtility.getAppDate(), Filter.OP_LESS_OR_EQUAL);
		filter[1] = new Filter("MaturityDate", DateUtility.getAppDate(), Filter.OP_GREATER_OR_EQUAL);
		filter[2] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		this.finReference.setFilters(filter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering ");
		doShowDialogPage(this.finRefValue);
		logger.debug("Leaving ");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financeType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(String finReference) {
		logger.debug("Entering");
		doWriteComponentsToBean(finReference);

		Map<String, Object> aruments = new HashMap<String, Object>();
		aruments.put("finReference", this.finRefValue);
		aruments.put("enquiryModule", true);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/LoanClosureEnquiryDialog.zul", null,
					aruments);
			//this.window_SelectLoanClosureEnquiryList.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint(new PTStringValidator(
				Labels.getLabel("label_SelectLoanClosureEnquiryList_FinReference.value"), null, true, true));
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceType
	 */
	public void doWriteComponentsToBean(String finRefValue) {
		logger.debug("Entering");
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finRefValue = this.finReference.getValidatedValue();
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

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(false)) {
			if (tabbox != null) {
				tabbox.getSelectedTab().close();
			}
		}
	}

	public LoanClosureEnquiryDialogCtrl getLoanClosureEnquiryDialogCtrl() {
		return loanClosureEnquiryDialogCtrl;
	}

	public void setLoanClosureEnquiryDialogCtrl(LoanClosureEnquiryDialogCtrl loanClosureEnquiryDialogCtrl) {
		this.loanClosureEnquiryDialogCtrl = loanClosureEnquiryDialogCtrl;
	}

}
