package com.pennant.pff.excess.web;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.excess.model.FinExcessTransfer;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class ExcessTransferListModelItemRenderer implements ListitemRenderer<FinExcessTransfer>, Serializable {

	private static final long serialVersionUID = 6906998807263283546L;

	public ExcessTransferListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, FinExcessTransfer finExcessTransfer, int index) throws Exception {

		Listcell lc;

		lc = new Listcell(String.valueOf(finExcessTransfer.getId()));
		lc.setParent(item);

		lc = new Listcell(finExcessTransfer.getCustCIF());
		lc.setParent(item);

		lc = new Listcell(finExcessTransfer.getFinReference());
		lc.setParent(item);

		lc = new Listcell(DateUtil.format(finExcessTransfer.getTransferDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);

		lc = new Listcell(finExcessTransfer.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(finExcessTransfer.getRecordType()));
		lc.setParent(item);

		item.setAttribute("transferId", finExcessTransfer.getId());
		item.setAttribute("finExcessTransfer", finExcessTransfer);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onExcessTransferItemDoubleClicked");
	}

}
