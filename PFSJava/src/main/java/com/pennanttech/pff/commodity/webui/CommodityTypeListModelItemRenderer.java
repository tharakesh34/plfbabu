package com.pennanttech.pff.commodity.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.commodity.model.CommodityType;

public class CommodityTypeListModelItemRenderer implements ListitemRenderer<CommodityType>, Serializable {
	private static final long serialVersionUID = 1L;

	public CommodityTypeListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, CommodityType commodity, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(commodity.getCode());
		lc.setParent(item);
		if (commodity.getUnitType() == 1) {
			lc = new Listcell("Killos");
			lc.setParent(item);
		} else if (commodity.getUnitType() == 2) {
			lc = new Listcell("Tonnes");
			lc.setParent(item);
		} else if (commodity.getUnitType() == 3) {
			lc = new Listcell("Grams");
			lc.setParent(item);
		} else if (commodity.getUnitType() == 4) {
			lc = new Listcell("Ounce");
			lc.setParent(item);
		} else if (commodity.getUnitType() == 5) {
			lc = new Listcell("Quantity");
			lc.setParent(item);
		} else {
			lc = new Listcell();
			lc.setParent(item);
		}
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

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCommodityTypeItemDoubleClicked");
	}

}
