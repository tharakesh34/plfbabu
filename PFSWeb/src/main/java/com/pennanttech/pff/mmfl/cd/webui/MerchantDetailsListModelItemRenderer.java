package com.pennanttech.pff.mmfl.cd.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.cd.model.MerchantDetails;

public class MerchantDetailsListModelItemRenderer implements ListitemRenderer<MerchantDetails>, Serializable {
	private static final long serialVersionUID = 1L;

	public MerchantDetailsListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, MerchantDetails merchantDetails, int count) {

		Listcell lc;
		lc = new Listcell(merchantDetails.getMerchantName());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(merchantDetails.getStoreId()));
		lc.setParent(item);
		lc = new Listcell(merchantDetails.getStoreName());
		lc.setParent(item);
		if (merchantDetails.isActive()) {
			lc = new Listcell("1");
			lc.setParent(item);
		} else {
			lc = new Listcell("0");
			lc.setParent(item);
		}
		lc = new Listcell(merchantDetails.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(merchantDetails.getRecordType()));
		lc.setParent(item);
		item.setAttribute("MerchantId", merchantDetails.getMerchantId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onMerchantDetailsListItemDoubleClicked");
	}
}
