
package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.finance.PricingDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/SelectChildLoanFinanceTypeDialog.zul file.
 */
public class SelectChildLoanFinTypeDialogCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final Logger logger = LogManager.getLogger(SelectFinanceTypeDialogCtrl.class);

	private static final long serialVersionUID = 8556168885363682933L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */

	protected Window window_SelectChildLoanFinanceTypeDialog;
	protected Combobox finType;
	protected ExtendedCombobox finType1;
	protected Button btnProceed;
	protected PricingDetailListCtrl pricingDetailListCtrl; // over handed
															// parameter
	private String financeType = "";
	private PricingDetailService pricingDetailService;

	/**
	 * default constructor.<br>
	 */
	public SelectChildLoanFinTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SelectChildLoanFinanceTypeDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_SelectChildLoanFinanceTypeDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("pricingDetailListCtrl")) {
			this.pricingDetailListCtrl = (PricingDetailListCtrl) arguments.get("pricingDetailListCtrl");
			setPricingDetailListCtrl(this.pricingDetailListCtrl);
		} else {
			setPricingDetailListCtrl(null);
		}

		if (arguments.containsKey("financeType")) {
			this.financeType = (String) arguments.get("financeType");
		}
		setFinTypes(financeType);
		this.window_SelectChildLoanFinanceTypeDialog.doModal();
		logger.debug(Literal.LEAVING);
	}

	private void setFinTypes(String finType) {
		List<ValueLabel> childLoanTypes = PennantAppUtil.getChildLoanFinType(finType);
		fillComboBox(this.finType, "", childLoanTypes, "");

	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		doFieldValidation();
		String finType = this.finType.getSelectedItem().getValue();
		pricingDetailListCtrl.getTopUpFinType().put(finType, pricingDetailService.getFinanceTypeById(finType));
		pricingDetailListCtrl.appendTopupLoan(finType);
		closeDialog();
	}

	private void doFieldValidation() {
		doClearMessage();
		doRemoveValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (getComboboxValue(this.finType).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_FinType.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Finance Type
		this.finType1.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.finType1.setMandatoryStyle(true);
		this.finType1.setModuleName("FinanceType");
		this.finType1.setValueColumn("FinType");
		this.finType1.setDescColumn("FinTypeDesc");
		this.finType1.setValidateColumns(new String[] { "FinType" });
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("FinType", this.financeType, Filter.OP_EQUAL);
		this.finType1.setFilters(filters);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		this.finType.setErrorMessage("");
	}

	/**
	 * Method for remove constraints of fields
	 */
	private void doRemoveValidation() {
		this.finType.setConstraint("");
	}

	public PricingDetailListCtrl getPricingDetailListCtrl() {
		return pricingDetailListCtrl;
	}

	public void setPricingDetailListCtrl(PricingDetailListCtrl pricingDetailListCtrl) {
		this.pricingDetailListCtrl = pricingDetailListCtrl;
	}

	public PricingDetailService getPricingDetailService() {
		return pricingDetailService;
	}

	public void setPricingDetailService(PricingDetailService pricingDetailService) {
		this.pricingDetailService = pricingDetailService;
	}

}
