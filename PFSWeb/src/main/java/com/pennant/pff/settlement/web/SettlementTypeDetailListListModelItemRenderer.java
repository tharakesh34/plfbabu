package com.pennant.pff.settlement.web;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.settlement.model.SettlementTypeDetail;

public class SettlementTypeDetailListListModelItemRenderer
		implements ListitemRenderer<SettlementTypeDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public SettlementTypeDetailListListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, SettlementTypeDetail std, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(std.getSettlementCode());
		lc.setParent(item);
		lc = new Listcell(std.getSettlementDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox isActive = new Checkbox();
		isActive.setDisabled(true);
		isActive.setChecked(std.isActive());
		lc.appendChild(isActive);
		lc.setParent(item);
		lc = new Listcell(std.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(std.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", std.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onItemDoubleClicked");
	}
}
