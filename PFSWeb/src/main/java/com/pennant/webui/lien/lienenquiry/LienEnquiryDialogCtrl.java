package com.pennant.webui.lien.lienenquiry;

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

import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.model.lien.LienDetails;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LienEnquiryDialogCtrl extends GFCBaseCtrl<LienDetails> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(LienEnquiryDialogCtrl.class);

	protected Window windowLienEnquiryDialog;
	protected Listbox lienDetails;
	protected Label windowTitle;
	protected Label title;
	protected Listheader listheaderLienReference;
	protected Textbox finReference;

	public LienEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LienEnquiry";
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$windowLienEnquiryDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowLienEnquiryDialog);

		List<LienDetails> lienDetail = new ArrayList<>();
		boolean headerType = false;

		try {
			if (arguments.containsKey("lien")) {
				lienDetail = (List<LienDetails>) arguments.get("lien");
			}

			if (lienDetail == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("header")) {
				headerType = (boolean) arguments.get("header");
			}

			doShowDialog(lienDetail, headerType);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(List<LienDetails> lienDetail, boolean headerType) {
		try {
			doFillHeaderList(lienDetail, headerType);

			if (!headerType) {
				listheaderLienReference.setLabel(Labels.getLabel("label_SelectLienEnquiryList_accNum.value"));
			}

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doFillHeaderList(List<LienDetails> lu, boolean headerType) {
		for (LienDetails ld : lu) {
			Listitem li = new Listitem();

			li.appendChild(new Listcell(String.valueOf(ld.getLienID())));
			li.appendChild(new Listcell(ld.getLienReference()));
			li.appendChild(new Listcell(ld.getSource()));
			li.appendChild(new Listcell(DateUtil.formatToLongDate(ld.getMarkingDate())));
			li.appendChild(new Listcell(ld.getMarking()));
			li.appendChild(new Listcell(headerType ? ld.getReference() : ld.getAccountNumber()));
			li.appendChild(new Listcell(ld.getMarkingReason()));
			li.appendChild(new Listcell(ld.isLienStatus() ? "ACTIVE" : "IN-ACTIVE"));
			li.appendChild(new Listcell(ld.getInterfaceStatus()));
			li.appendChild(new Listcell(ld.getInterfaceRemarks()));
			li.appendChild(new Listcell(DateUtil.formatToLongDate(ld.getDemarkingDate())));
			li.appendChild(new Listcell(ld.getDemarking()));
			li.appendChild(new Listcell(ld.getDemarkingReason()));

			this.lienDetails.appendChild(li);
		}
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}
}