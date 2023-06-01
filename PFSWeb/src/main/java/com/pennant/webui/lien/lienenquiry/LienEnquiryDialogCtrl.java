package com.pennant.webui.lien.lienenquiry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	protected Borderlayout borderlayoutLienEnquiry;
	protected Listbox lienDetails;
	protected Label windowTitle;
	protected Label title;
	protected Listheader listheaderLienReference;
	protected Textbox finReference;
	protected Paging paging;

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

			this.borderlayoutLienEnquiry.setHeight(getBorderLayoutHeight());
			this.lienDetails.setHeight(getListBoxHeight(0));
			this.paging.setPageSize(getListRows());
			this.paging.setDetailed(true);

			this.lienDetails.setItemRenderer(new ListModelItemRenderer(headerType));

			this.paging.setActivePage(0);

			pagedListWrapper.initList(lienDetail, this.lienDetails, this.paging);
			doShowDialog(lienDetail, headerType);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(List<LienDetails> lienDetail, boolean headerType) {
		try {
			if (!headerType) {
				listheaderLienReference.setLabel(Labels.getLabel("label_SelectLienEnquiryList_accNum.value"));
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

	public class ListModelItemRenderer implements ListitemRenderer<LienDetails>, Serializable {
		private static final long serialVersionUID = 1L;

		private boolean headerType;

		public ListModelItemRenderer(boolean headerType) {
			this.headerType = headerType;
		}

		@Override
		public void render(Listitem item, LienDetails ld, int index) throws Exception {

			item.appendChild(new Listcell(String.valueOf(ld.getLienID())));
			item.appendChild(new Listcell(ld.getLienReference()));
			item.appendChild(new Listcell(ld.getSource()));
			item.appendChild(new Listcell(DateUtil.formatToLongDate(ld.getMarkingDate())));
			item.appendChild(new Listcell(ld.getMarking()));
			item.appendChild(new Listcell(headerType ? ld.getReference() : ld.getAccountNumber()));
			item.appendChild(new Listcell(ld.getMarkingReason()));
			item.appendChild(new Listcell(ld.isLienStatus() ? "ACTIVE" : "IN-ACTIVE"));
			item.appendChild(new Listcell(ld.getInterfaceStatus()));
			item.appendChild(new Listcell(ld.getInterfaceRemarks()));
			item.appendChild(new Listcell(DateUtil.formatToLongDate(ld.getDemarkingDate())));
			item.appendChild(new Listcell(ld.getDemarking()));
			item.appendChild(new Listcell(ld.getDemarkingReason()));

		}
	}

}