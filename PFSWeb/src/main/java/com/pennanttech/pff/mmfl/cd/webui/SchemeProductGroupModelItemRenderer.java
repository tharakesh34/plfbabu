package com.pennanttech.pff.mmfl.cd.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.mmfl.cd.model.SchemeProductGroup;

public class SchemeProductGroupModelItemRenderer implements ListitemRenderer<SchemeProductGroup>, Serializable {
	private static final long serialVersionUID = 1L;

	public SchemeProductGroupModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, SchemeProductGroup schemeDealerGroup, int count) {

		Listcell lc;
		lc = new Listcell(schemeDealerGroup.getPromotionId());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(schemeDealerGroup.getProductGroupCode()));
		lc.setParent(item);
		lc = new Listcell(schemeDealerGroup.isPosVendor() ? "1" : "0");
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
		item.setAttribute("SchemeProductGroupId", schemeDealerGroup.getSchemeProductGroupId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSchemeProductGroupListItemDoubleClicked");
	}

}
