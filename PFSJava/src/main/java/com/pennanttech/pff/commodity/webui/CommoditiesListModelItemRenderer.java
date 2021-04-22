package com.pennanttech.pff.commodity.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.commodity.model.Commodity;

public class CommoditiesListModelItemRenderer implements ListitemRenderer<Commodity>, Serializable {
	private static final long serialVersionUID = 1L;

	public CommoditiesListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, Commodity commodity, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(commodity.getCommodityTypeCode());
		lc.setParent(item);
		lc = new Listcell(commodity.getCode());
		lc.setParent(item);
		lc = new Listcell(commodity.getHSNCode());
		lc.setParent(item);
		if (commodity.isActive()) {
			lc = new Listcell("1");
			lc.setParent(item);
		} else {
			lc = new Listcell("0");
			lc.setParent(item);
		}
		lc = new Listcell(commodity.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(commodity.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", commodity.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onStockCompanyItemDoubleClicked");
	}

}
