package com.pennanttech.pff.mmfl.cd.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.cd.model.SchemeDealerGroup;

public class SchemeDealerGroupModelItemRenderer implements ListitemRenderer<SchemeDealerGroup>, Serializable {
	private static final long serialVersionUID = 1L;

	public SchemeDealerGroupModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, SchemeDealerGroup schemeDealerGroup, int count) {

		Listcell lc;
		lc = new Listcell(schemeDealerGroup.getPromotionId());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(schemeDealerGroup.getDealerGroupCode()));
		lc.setParent(item);
		if (schemeDealerGroup.isActive()) {
			lc = new Listcell("1");
			lc.setParent(item);
		} else {
			lc = new Listcell("0");
			lc.setParent(item);
		}
		lc = new Listcell(schemeDealerGroup.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(schemeDealerGroup.getRecordType()));
		lc.setParent(item);
		item.setAttribute("SchemeDealerGroupId", schemeDealerGroup.getSchemeDealerGroupId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSchemeDealerGroupListItemDoubleClicked");
	}
}
