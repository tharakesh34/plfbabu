package com.pennant.webui.hold.holdenquiry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.holdmarking.model.HoldMarkingDetail;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class HoldEnquiryDialogCtrl extends GFCBaseCtrl<HoldMarkingDetail> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(HoldEnquiryDialogCtrl.class);

	protected Window windowHoldEnquiryDialog;
	protected Borderlayout borderlayoutHoldEnquiry;
	protected Label windowTitle;
	protected Label title;
	protected Listheader listheaderHoldReference;
	protected Textbox finReference;
	protected Listbox listBoxHold;
	protected Paging paging;

	public HoldEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "HoldEnquiry";
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$windowHoldEnquiryDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowHoldEnquiryDialog);

		List<HoldMarkingDetail> holdDetail = new ArrayList<>();
		boolean headerType = false;

		try {
			if (arguments.containsKey("hold")) {
				holdDetail = (List<HoldMarkingDetail>) arguments.get("hold");
			}

			if (holdDetail == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("header")) {
				headerType = (boolean) arguments.get("header");
			}

			this.borderlayoutHoldEnquiry.setHeight(getBorderLayoutHeight());
			this.listBoxHold.setHeight(getListBoxHeight(0));
			this.paging.setPageSize(getListRows());
			this.paging.setDetailed(true);

			if (CollectionUtils.isNotEmpty(holdDetail)) {
				holdDetail = holdDetail.stream().sorted((l1, l2) -> Long.compare(l1.getHoldID(), l2.getHoldID()))
						.toList();
			}

			this.listBoxHold.setItemRenderer(new ListModelItemRenderer(headerType));

			this.paging.setActivePage(0);

			pagedListWrapper.initList(holdDetail, this.listBoxHold, this.paging);

			doShowDialog(headerType);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(boolean headerType) {
		try {
			if (headerType) {
				this.listheaderHoldReference
						.setLabel(Labels.getLabel("label_SelectHoldEnquiryList_FinReference.value"));
			}

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	public class ListModelItemRenderer implements ListitemRenderer<HoldMarkingDetail> {
		private boolean headerType;

		public ListModelItemRenderer(boolean headerType) {
			this.headerType = headerType;
		}

		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		BigDecimal releaseAmount = BigDecimal.ZERO;
		List<Long> holdIDList = new ArrayList<>();

		@Override
		public void render(Listitem item, HoldMarkingDetail hd, int index) throws Exception {

			if (holdIDList == null || !holdIDList.contains(hd.getHoldID())) {
				holdIDList.add(hd.getHoldID());
				releaseAmount = BigDecimal.ZERO;
			}

			item.appendChild(new Listcell(String.valueOf(hd.getHoldID())));
			item.appendChild(new Listcell(headerType ? hd.getFinReference() : hd.getAccountNumber()));
			item.appendChild(new Listcell(String.valueOf(hd.getHoldReference())));

			if (PennantConstants.HOLD_MARKING.equals(String.valueOf(hd.getHoldType()))) {
				item.appendChild(new Listcell(PennantApplicationUtil.amountFormate(hd.getHoldAmount(), formatter)));
				item.appendChild(new Listcell(String.valueOf(BigDecimal.ZERO)));
				item.appendChild(new Listcell(PennantApplicationUtil.amountFormate(hd.getHoldAmount(), formatter)));
			} else {
				releaseAmount = releaseAmount.add(hd.getAmount());
				item.appendChild(new Listcell(PennantApplicationUtil.amountFormate(hd.getHoldAmount(), formatter)));
				item.appendChild(new Listcell(PennantApplicationUtil.amountFormate(releaseAmount, formatter)));
				item.appendChild(new Listcell(
						PennantApplicationUtil.amountFormate(hd.getHoldAmount().subtract(releaseAmount), formatter)));
			}

			item.appendChild(new Listcell(String.valueOf(hd.getHoldType())));
			item.appendChild(new Listcell(DateUtil.formatToLongDate(hd.getMovementDate())));
			item.appendChild(new Listcell(hd.getMarking()));
			item.appendChild(new Listcell(hd.getStatus()));
			item.appendChild(new Listcell(hd.getHoldReleaseReason()));

		}

	}
}
