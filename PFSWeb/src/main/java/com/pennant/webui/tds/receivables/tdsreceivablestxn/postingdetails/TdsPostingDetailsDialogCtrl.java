package com.pennant.webui.tds.receivables.tdsreceivablestxn.postingdetails;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsComparator;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class TdsPostingDetailsDialogCtrl extends GFCBaseCtrl<ReturnDataSet> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(TdsPostingDetailsDialogCtrl.class);

	protected Window window_TDSPostingsDialog;
	protected Listbox listBoxTDSPostingDetail;
	private List<ReturnDataSet> postingDetails;
	protected long receiptId;
	protected long linkedTranId;
	private ReceiptService receiptService;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private TdsReceivablesTxnService tdsReceivablesTxnService;

	/**
	 * default constructor.<br>
	 */
	public TdsPostingDetailsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_TDSPostingsDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TDSPostingsDialog);

		try {

			if (arguments.containsKey("receiptId")) {
				receiptId = Long.parseLong(String.valueOf(arguments.get("receiptId")));
			}

			FinReceiptHeader header = receiptService.getFinReceiptHeaderById(receiptId, false, "_View");
			List<FinReceiptDetail> receiptdetails = header.getReceiptDetails();
			FinReceiptDetail receiptdetail = receiptdetails.get(0);
			FinRepayHeader repayHeader = financeRepaymentsDAO
					.getFinRepayHeadersByReceipt(receiptdetail.getReceiptSeqID(), "");

			linkedTranId = repayHeader.getLinkedTranId();
			doShowDialog();
			this.window_TDSPostingsDialog.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
			logger.error(Literal.EXCEPTION, e);
			this.window_TDSPostingsDialog.onClose();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doShowDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		postingDetails = tdsReceivablesTxnService.getPostingsByLinkTransId(linkedTranId, TableType.VIEW, false);
		this.listBoxTDSPostingDetail.getItems().clear();

		doGetListItemRenderer(postingDetails);

		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doGetListItemRenderer(List<ReturnDataSet> postingDetails) {
		this.listBoxTDSPostingDetail
				.setModel(new GroupsModelArray(postingDetails.toArray(), new FinanceEnquiryPostingsComparator()));
		this.listBoxTDSPostingDetail.setItemRenderer(new FinanceEnquiryPostingsListItemRenderer());
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

}
