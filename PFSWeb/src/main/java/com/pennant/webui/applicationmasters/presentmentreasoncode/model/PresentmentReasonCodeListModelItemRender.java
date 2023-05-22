package com.pennant.webui.applicationmasters.presentmentreasoncode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;
import com.pennant.backend.util.PennantJavaUtil;

public class PresentmentReasonCodeListModelItemRender implements ListitemRenderer<PresentmentReasonCode>, Serializable {

	private static final long serialVersionUID = 9099171990501035267L;

	public PresentmentReasonCodeListModelItemRender() {
	    super();
	}

	@Override
	public void render(Listitem item, PresentmentReasonCode presentmentReasonCode, int count) {

		Listcell lc;
		lc = new Listcell(presentmentReasonCode.getCode());
		lc.setParent(item);
		lc = new Listcell(presentmentReasonCode.getDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox isActive = new Checkbox();
		isActive.setDisabled(true);
		isActive.setChecked(presentmentReasonCode.isActive());
		lc.appendChild(isActive);
		lc.setParent(item);
		lc = new Listcell(presentmentReasonCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(presentmentReasonCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", presentmentReasonCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPresentmentReasonCodeItemDoubleClicked");
	}
}
