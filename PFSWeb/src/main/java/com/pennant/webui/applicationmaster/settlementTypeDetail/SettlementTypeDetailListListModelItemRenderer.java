package com.pennant.webui.applicationmaster.settlementTypeDetail;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennant.backend.util.PennantJavaUtil;

public class SettlementTypeDetailListListModelItemRenderer
		implements ListitemRenderer<SettlementTypeDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public SettlementTypeDetailListListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, SettlementTypeDetail settlementTypeDetail, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(settlementTypeDetail.getSettlementCode());
		lc.setParent(item);
		lc = new Listcell(settlementTypeDetail.getSettlementDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox isActive = new Checkbox();
		isActive.setDisabled(true);
		isActive.setChecked(settlementTypeDetail.isActive());
		lc.appendChild(isActive);
		lc.setParent(item);
		lc = new Listcell(settlementTypeDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(settlementTypeDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", settlementTypeDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSettlementTypeDetailItemDoubleClicked");
	}
}
