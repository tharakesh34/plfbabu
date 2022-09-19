package com.pennant.webui.cersai.areaunit.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.AreaUnit;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AreaUnitListModelItemRenderer implements ListitemRenderer<AreaUnit>, Serializable {

	private static final long serialVersionUID = 1L;

	public AreaUnitListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AreaUnit areaUnit, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.formateLong(areaUnit.getId()));
		lc.setParent(item);
		lc.setStyle("text-align:Right;");
		lc = new Listcell(areaUnit.getDescription());
		lc.setParent(item);
		lc = new Listcell(areaUnit.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(areaUnit.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", areaUnit.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAreaUnitItemDoubleClicked");
	}
}