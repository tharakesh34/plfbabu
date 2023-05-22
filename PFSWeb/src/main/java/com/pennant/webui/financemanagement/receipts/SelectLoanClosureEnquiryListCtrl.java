package com.pennant.webui.financemanagement.receipts;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.pff.receipt.ClosureType;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectLoanClosureEnquiryListCtrl extends GFCBaseCtrl<ForeClosure> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger logger = LogManager.getLogger(SelectLoanClosureEnquiryListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectLoanClosureEnquiryList;
	protected Label windowTitle;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;
	private LoanClosureEnquiryDialogCtrl loanClosureEnquiryDialogCtrl;
	private String finRefValue;
	private boolean isModelWindow = false;
	private transient String moduleType;
	private boolean isMatured = false;
	protected Label title;
	protected Combobox closureType;
	private String closureTypeValue;
	private transient FinanceMainDAO financeMainDAO;
	Date appDate = SysParamUtil.getAppDate();

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
	 */
	public void onCreate$window_SelectLoanClosureEnquiryList(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectLoanClosureEnquiryList);

		if (arguments.containsKey("isModelWindow")) {
			isModelWindow = (boolean) arguments.get("isModelWindow");
		}

		if (arguments.containsKey("finReference")) {
			finRefValue = (String) arguments.get("finReference");
		}

		if (arguments.containsKey("closureType")) {
			closureTypeValue = (String) arguments.get("closureType");
		}

		try {
			doCheckRights();
			doSetFieldProperties();
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

		this.moduleType = getArgument("module");
		if (StringUtils.equals(moduleType, "Matured")) {
			isMatured = true;
		}
		if (StringUtils.isNotEmpty(closureTypeValue)) {
			this.closureType.setValue(closureTypeValue);
			this.closureType.setReadonly(true);
		} else {
			fillComboBox(this.closureType, "", ClosureType.getTypes());
		}

		if (StringUtils.isNotEmpty(finRefValue)) {
			this.finReference.setValue(finRefValue);
			this.finReference.setReadonly(true);
		} else {
			this.finReference.setMandatoryStyle(true);
			this.finReference.setModuleName("FinanceDetail");
			this.finReference.setValueColumn("FinReference");
			this.finReference.setDescColumn("FinType");
			this.finReference.setValidateColumns(new String[] { "FinReference" });

			if (isMatured) {
				windowTitle.setValue(App.getLabel("window_SelectMaturedLoanClosureEnquiryList.title"));
				Filter[] filter = new Filter[3];
				filter[0] = new Filter("FinStartDate", SysParamUtil.getAppDate(), Filter.OP_LESS_OR_EQUAL);
				filter[1] = new Filter("MaturityDate", SysParamUtil.getAppDate(), Filter.OP_LESS_THAN);
				filter[2] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
				this.finReference.setFilters(filter);
			} else {
				Filter[] filter = new Filter[3];
				filter[0] = new Filter("FinStartDate", SysParamUtil.getAppDate(), Filter.OP_LESS_OR_EQUAL);
				filter[1] = new Filter("MaturityDate", SysParamUtil.getAppDate(), Filter.OP_GREATER_OR_EQUAL);
				filter[2] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
				this.finReference.setFilters(filter);
			}
			logger.debug(Literal.LEAVING);
		}
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering ");
		doShowDialogPage(this.finRefValue);
		logger.debug("Leaving ");
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		// Close the current window
		this.window_SelectLoanClosureEnquiryList.onClose();
		if (!isModelWindow) {
			// Close the current menu item
			final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
					.getFellow("tabBoxIndexCenter");
			tabbox.getSelectedTab().close();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financeType The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(String finReference) {
		logger.debug("Entering");
		doWriteComponentsToBean(finReference);

		Map<String, Object> aruments = new HashMap<String, Object>();
		aruments.put("finReference", this.finRefValue);
		aruments.put("enquiryModule", true);
		aruments.put("isWIF", false);
		aruments.put("isModelWindow", isModelWindow);
		aruments.put("isMatured", isMatured);
		aruments.put("closureType", this.closureType.getValue());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/LoanClosureEnquiryDialog.zul", null,
					aruments);
			// this.window_SelectLoanClosureEnquiryList.onClose();
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

		this.closureType.setConstraint(new PTStringValidator(
				Labels.getLabel("label_SelectLoanClosureEnquiryList_ClosureType.value"), null, true, true));

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		this.closureType.setConstraint("");
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

		try {
			this.closureType.getValue();
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

	public void onFulfill$finReference(Event event) {
		int channelIdx = this.closureType.getSelectedIndex();
		Date maturitydate = financeMainDAO.getMaturityDatebyFinID(ComponentUtil.getFinID(this.finReference));

		if (maturitydate != null) {

			String excludeFields = ",CLOSURE,SETTLEMENT,";
			if (maturitydate.compareTo(appDate) <= 0) {
				excludeFields = ",FORE-CLOSURE,CANCEL,SETTLEMENT,";
			}

			fillComboBox(this.closureType, "", ClosureType.getTypes(), excludeFields);
			this.closureType.setSelectedIndex(1);

			if (channelIdx > 0) {
				this.closureType.setSelectedIndex(channelIdx);
			}
		} else {
			this.closureType.setDisabled(false);
			fillComboBox(this.closureType, "", ClosureType.getTypes());
		}
	}

	public LoanClosureEnquiryDialogCtrl getLoanClosureEnquiryDialogCtrl() {
		return loanClosureEnquiryDialogCtrl;
	}

	public void setLoanClosureEnquiryDialogCtrl(LoanClosureEnquiryDialogCtrl loanClosureEnquiryDialogCtrl) {
		this.loanClosureEnquiryDialogCtrl = loanClosureEnquiryDialogCtrl;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
