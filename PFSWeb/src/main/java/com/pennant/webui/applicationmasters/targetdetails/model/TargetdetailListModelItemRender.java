package com.pennant.webui.applicationmasters.targetdetails.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.util.PennantJavaUtil;

public class TargetdetailListModelItemRender implements ListitemRenderer<TargetDetail>, Serializable {

	private static final long serialVersionUID = 9099171990501035267L;

	public TargetdetailListModelItemRender() {
	    super();
	}

	@Override
	public void render(Listitem item, TargetDetail targetDetail, int count) {

		Listcell lc;
		lc = new Listcell(targetDetail.getTargetCode());
		lc.setParent(item);
		lc = new Listcell(targetDetail.getTargetDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbTargetIsActive = new Checkbox();
		cbTargetIsActive.setDisabled(true);
		cbTargetIsActive.setChecked(targetDetail.isActive());
		lc.appendChild(cbTargetIsActive);
		lc.setParent(item);
		lc = new Listcell(targetDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(targetDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", targetDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onTargetDetailItemDoubleClicked");
	}

}
