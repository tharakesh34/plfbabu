package com.pennant.webui.limit.limitdetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LimitCustomerReferenceDialogCtrl extends GFCBaseCtrl<LimitDetails> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LimitCustomerReferenceDialogCtrl.class);

	/*
	 * ************************************************************************ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ************************************************************************
	 */
	protected Window window_LimitCustomerReferenceDialog;

	protected Listbox listBoxLimitCustRef;
	protected Listbox listBoxTransactionDetails;
	protected Groupbox gb_transactiondetails;

	private transient LimitDetailService limitDetailService;
	private transient PagedListService pagedListService;

	public static ArrayList<ValueLabel> list = LimitConstants.getTransactionTypeList();

	/**
	 * default constructor.<br>
	 */
	public LimitCustomerReferenceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected LimitHeader object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_LimitCustomerReferenceDialog(Event event) {
		logger.debug("Entering");

		List<LimitReferenceMapping> limitReferences = new ArrayList<LimitReferenceMapping>();
		LimitDetails limitDetails = null;

		// Set the page level components.
		setPageComponents(window_LimitCustomerReferenceDialog);

		try {

			if (arguments.containsKey("limitDetails")) {
				limitDetails = (LimitDetails) arguments.get("limitDetails");
			}

			if (arguments.containsKey("limitReference")) {
				limitReferences = (List<LimitReferenceMapping>) arguments.get("limitReference");
			} else {
				limitReferences = null;
			}

			// fill the components with the data
			doWriteBeanToComponents(limitDetails, limitReferences);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param customerLimitDetails LimitHeader
	 */
	public void doWriteBeanToComponents(LimitDetails limitDetails, List<LimitReferenceMapping> mappings) {
		logger.debug("Entering");
		this.gb_transactiondetails.setVisible(false);

		String limit = limitDetails.getLimitLine();

		if (limit == null) {
			limit = limitDetails.getGroupCode();
		}

		if (mappings != null && mappings.size() > 0) {
			fillListbox(mappings);
		}

		logger.debug("Leaving");
	}

	public void fillListbox(List<LimitReferenceMapping> limitReferences) {
		this.listBoxLimitCustRef.getItems().clear();

		for (LimitReferenceMapping limitReferenceMapping : limitReferences) {

			Listitem item = new Listitem();
			Listcell lc;

			lc = new Listcell();
			A refLink = new A(limitReferenceMapping.getReferenceNumber());
			refLink.addForward("onClick", self, "onClickReference", limitReferenceMapping);
			refLink.setParent(lc);
			lc.setParent(item);

			String value = "";

			if (LimitConstants.FINANCE.equals(limitReferenceMapping.getReferenceCode())) {
				value = Labels.getLabel("lable_Finance");
			} else if (LimitConstants.COMMITMENT.equals(limitReferenceMapping.getReferenceCode())) {
				value = Labels.getLabel("lable_Commitment");
			}

			lc = new Listcell(value);
			lc.setParent(item);

			this.listBoxLimitCustRef.appendChild(item);
		}

	}

	public void onClickReference(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		LimitReferenceMapping lmtRefMap = (LimitReferenceMapping) event.getData();
		List<LimitTransactionDetail> transactionDetails = getLimitDetailService().getLimitTranDetails(
				lmtRefMap.getReferenceCode(), lmtRefMap.getReferenceNumber(), lmtRefMap.getHeaderId());

		if (transactionDetails != null && !transactionDetails.isEmpty()) {
			this.gb_transactiondetails.setVisible(true);
			doFillLimitTransactionListbox(transactionDetails);
		} else {
			this.gb_transactiondetails.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param LimitTransactionDetail LimitTransactionDetail
	 */
	private void doFillLimitTransactionListbox(List<LimitTransactionDetail> limitTransaction) {
		logger.debug("Entering");

		this.listBoxTransactionDetails.getItems().clear();
		for (LimitTransactionDetail trandetail : limitTransaction) {

			Listitem item = new Listitem();
			Listcell lc;

			lc = new Listcell(trandetail.getReferenceNumber());
			lc.setParent(item);

			lc = new Listcell(trandetail.getLimitCurrency());
			lc.setParent(item);

			lc = new Listcell(format(trandetail.getLimitAmount(), trandetail.getLimitCurrency()));
			lc.setParent(item);

			lc = new Listcell(trandetail.getTransactionCurrency());
			lc.setParent(item);

			lc = new Listcell(format(trandetail.getLimitAmount(), trandetail.getTransactionCurrency()));
			lc.setParent(item);

			lc = new Listcell(PennantStaticListUtil.getlabelDesc(trandetail.getTransactionType(), list));
			lc.setParent(item);

			lc = new Listcell(DateUtil.format(trandetail.getTransactionDate(), DateFormat.LONG_DATE_TIME));
			lc.setParent(item);
			this.listBoxTransactionDetails.appendChild(item);
		}

		logger.debug("Leaving");
	}

	private String format(BigDecimal decimal, String ccy) {
		return CurrencyUtil.format(decimal, CurrencyUtil.getFormat(ccy));
	}

	public void onClick$btnClose(Event event) {
		this.window_LimitCustomerReferenceDialog.onClose();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public LimitDetailService getLimitDetailService() {
		return limitDetailService;
	}

	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

}
