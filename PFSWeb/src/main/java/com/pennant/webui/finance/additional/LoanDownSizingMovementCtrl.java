package com.pennant.webui.finance.additional;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanDownSizingMovementCtrl extends GFCBaseCtrl<FinAssetAmtMovement> {

	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(LoanDownSizingMovementCtrl.class);

	protected Window window_LoanDownSizingMovement;
	protected Listbox listBoxMovementDetails;

	int formatter = 0;
	private List<FinAssetAmtMovement> assetAmtMvntList = null;

	/**
	 * default constructor.<br>
	 */
	public LoanDownSizingMovementCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_LoanDownSizingMovement(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_LoanDownSizingMovement);

		try {
			if (arguments.containsKey("assetAmtMvntList")) {
				assetAmtMvntList = (List<FinAssetAmtMovement>) arguments.get("assetAmtMvntList");
			}

			if (arguments.containsKey("formatter")) {
				formatter = (Integer) arguments.get("formatter");
			}

			if (assetAmtMvntList != null && !assetAmtMvntList.isEmpty()) {
				doFillFinAssetAmtMovements(assetAmtMvntList);
			}

			this.listBoxMovementDetails.setHeight(this.borderLayoutHeight - 315 + "px");
			this.window_LoanDownSizingMovement.setHeight(this.borderLayoutHeight - 265 + "px");

			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LoanDownSizingMovement.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sanction Amount Movements
	 * 
	 * @param assetAmtMvntList
	 */
	private void doFillFinAssetAmtMovements(List<FinAssetAmtMovement> assetAmtMvntList) {
		logger.debug("Entering");

		this.listBoxMovementDetails.getItems().clear();
		for (FinAssetAmtMovement movement : assetAmtMvntList) {

			Listitem item = new Listitem();
			Listcell lc;

			lc = new Listcell(DateUtil.formatToLongDate(movement.getMovementDate()));
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getMovementAmount(), formatter));
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getSanctionedAmt(), formatter));
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getRevisedSanctionedAmt(), formatter));
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getDisbursedAmt(), formatter));
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getAvailableAmt(), formatter));
			lc.setParent(item);

			this.listBoxMovementDetails.appendChild(item);
		}

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}
}
