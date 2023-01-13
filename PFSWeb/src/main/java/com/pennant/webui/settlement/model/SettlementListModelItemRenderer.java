package com.pennant.webui.settlement.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.settlement.FinSettlementHeader;
import com.pennant.backend.util.PennantJavaUtil;

public class SettlementListModelItemRenderer implements ListitemRenderer<FinSettlementHeader>, Serializable {
	private static final long serialVersionUID = 1L;

	public SettlementListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, FinSettlementHeader settlement, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(settlement.getFinReference());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(settlement.getSettlementType()));
		lc.setParent(item);
		lc = new Listcell(settlement.getSettlementStatus());
		lc.setParent(item);
		lc = new Listcell(settlement.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(settlement.getRecordType()));
		lc.setParent(item);

		item.setAttribute("ID", settlement.getID());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSettlementItemDoubleClicked");
	}
}