package com.pennant.webui.applicationmaster.bouncecode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;

public class PresentmentExcludeCodeListModelItemRenderer
		implements ListitemRenderer<PresentmentExcludeCode>, Serializable {

	private static final long serialVersionUID = -6336194516320385692L;

	public PresentmentExcludeCodeListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, PresentmentExcludeCode bounceCode, int index) throws Exception {
		Listcell lc;
		final Checkbox dueDate = new Checkbox();

		lc = new Listcell(bounceCode.getCode());
		lc.setParent(item);
		lc = new Listcell(bounceCode.getDescription());
		lc.setParent(item);
		lc = new Listcell();

		dueDate.setDisabled(true);
		dueDate.setChecked(bounceCode.isCreateBounceOnDueDate());
		lc.appendChild(dueDate);
		lc.setParent(item);

		lc = new Listcell(String.valueOf(bounceCode.getBounceCode()));
		lc.setParent(item);

		item.setAttribute("code", bounceCode.getCode());

		lc = new Listcell(bounceCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(bounceCode.getRecordType()));
		lc.setParent(item);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBounceCodeItemDoubleClicked");
	}

}
