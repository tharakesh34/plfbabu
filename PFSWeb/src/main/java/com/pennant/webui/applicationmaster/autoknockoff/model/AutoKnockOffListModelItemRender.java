package com.pennant.webui.applicationmaster.autoknockoff.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.util.PennantJavaUtil;

public class AutoKnockOffListModelItemRender implements ListitemRenderer<AutoKnockOff>, Serializable {

	private static final long serialVersionUID = 1L;

	public AutoKnockOffListModelItemRender() {
		super();
	}

	@Override
	public void render(Listitem item, AutoKnockOff knockOff, int index) {

		Listcell lc;
		lc = new Listcell(knockOff.getCode());
		lc.setParent(item);

		lc = new Listcell(knockOff.getDescription());
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(knockOff.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);

		lc = new Listcell(knockOff.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(knockOff.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", knockOff.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAutoKnockOffItemDoubleClicked");
	}

}
