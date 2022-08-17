package com.pennanttech.pff.npa.web;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.npa.model.AssetSubClassCode;

public class AssetSubClassCodeListItemRenderer implements ListitemRenderer<AssetSubClassCode>, Serializable {
	private static final long serialVersionUID = 1L;

	public AssetSubClassCodeListItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssetSubClassCode subclassCode, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(subclassCode.getClassCode());
		lc.setParent(item);
		lc = new Listcell(subclassCode.getCode());
		lc.setParent(item);
		lc = new Listcell(subclassCode.getClassDescription());
		lc.setParent(item);
		lc = new Listcell(subclassCode.getDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox active = new Checkbox();
		active.setDisabled(true);
		active.setChecked(subclassCode.isActive());
		lc.appendChild(active);
		lc.setParent(item);
		lc = new Listcell(subclassCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(subclassCode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", subclassCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetSubClassCodeItemDoubleClicked");
	}
}