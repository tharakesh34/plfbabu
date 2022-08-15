package com.pennanttech.pff.npa.web;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.npa.model.AssetClassCode;

public class AssetClassCodeListItemRenderer implements ListitemRenderer<AssetClassCode>, Serializable {
	private static final long serialVersionUID = 1L;

	public AssetClassCodeListItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssetClassCode classCode, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(classCode.getCode());
		lc.setParent(item);
		lc = new Listcell(classCode.getDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox active = new Checkbox();
		active.setDisabled(true);
		active.setChecked(classCode.isActive());
		lc.appendChild(active);
		lc.setParent(item);
		lc = new Listcell(classCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(classCode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", classCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetClassCodeItemDoubleClicked");
	}
}