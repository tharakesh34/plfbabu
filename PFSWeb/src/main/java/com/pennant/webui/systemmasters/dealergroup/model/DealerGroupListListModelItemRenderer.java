package com.pennant.webui.systemmasters.dealergroup.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.DealerGroup;
import com.pennant.backend.util.PennantJavaUtil;

public class DealerGroupListListModelItemRenderer implements ListitemRenderer<DealerGroup>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, DealerGroup dealerGroup, int index) {
		Listcell lc;
		lc = new Listcell(dealerGroup.getDealerCode());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(dealerGroup.getDealerCategoryId()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(dealerGroup.getChannel()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbDealerIsActive = new Checkbox();
		cbDealerIsActive.setDisabled(true);
		cbDealerIsActive.setChecked(dealerGroup.isActive());
		lc.appendChild(cbDealerIsActive);

		lc.setParent(item);
		lc = new Listcell(dealerGroup.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(dealerGroup.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", dealerGroup.getDealerGroupId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDealerGroupItemDoubleClicked");
	}

}
