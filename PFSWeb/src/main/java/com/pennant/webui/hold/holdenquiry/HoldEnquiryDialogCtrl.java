package com.pennant.webui.hold.holdenquiry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

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
	protected Listbox holdDetails;
	protected Label windowTitle;
	protected Label title;
	protected Listheader listheaderHoldReference;
	protected Textbox finReference;

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

			doShowDialog(holdDetail, headerType);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(List<HoldMarkingDetail> holdDetail, boolean headerType) {
		try {
			doFillHeaderList(holdDetail, headerType);

			if (!headerType) {
				listheaderHoldReference.setLabel(Labels.getLabel("label_SelectHoldEnquiryList_accNum.value"));
			}

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doFillHeaderList(List<HoldMarkingDetail> lu, boolean headerType) {
		for (HoldMarkingDetail hd : lu) {
			Listitem li = new Listitem();

			li.appendChild(new Listcell(String.valueOf(hd.getHoldID())));
			li.appendChild(new Listcell(String.valueOf(hd.getAccountNumber())));
			li.appendChild(new Listcell(String.valueOf(hd.getHoldReference())));
			if (PennantConstants.HOLD_MARKING.equals(String.valueOf(hd.getHoldType()))) {
				li.appendChild(new Listcell(String.valueOf(hd.getHoldAmount())));
				li.appendChild(new Listcell(String.valueOf(BigDecimal.ZERO)));
				li.appendChild(new Listcell(String.valueOf(BigDecimal.ZERO)));
			} else {

				BigDecimal releaseAmount = hd.getReleaseAmount();
				releaseAmount = releaseAmount.add(hd.getReleaseAmount());
				li.appendChild(new Listcell(String.valueOf(hd.getAmount())));
				li.appendChild(new Listcell(String.valueOf(releaseAmount)));
				li.appendChild(new Listcell(String.valueOf(hd.getBalance())));
			}

			li.appendChild(new Listcell(String.valueOf(hd.getHoldType())));
			li.appendChild(new Listcell(DateUtil.formatToLongDate(hd.getMovementDate())));
			li.appendChild(new Listcell(hd.getMarking()));
			li.appendChild(new Listcell(hd.getFinReference()));
			li.appendChild(new Listcell(hd.getStatus()));
			li.appendChild(new Listcell(hd.getHoldReleaseReason()));

			this.holdDetails.appendChild(li);
		}
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}
}
