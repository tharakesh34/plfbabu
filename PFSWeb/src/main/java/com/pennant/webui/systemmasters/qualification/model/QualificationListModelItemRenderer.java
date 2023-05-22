package com.pennant.webui.systemmasters.qualification.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Qualification;
import com.pennant.backend.util.PennantJavaUtil;

public class QualificationListModelItemRenderer implements ListitemRenderer<Qualification>, Serializable {

	private static final long serialVersionUID = -2463856192651940409L;

	public QualificationListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Qualification qualification, int count) {
		Listcell lc;
		lc = new Listcell(qualification.getCode());
		lc.setParent(item);
		lc = new Listcell(qualification.getDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbQualificationIsActive = new Checkbox();
		cbQualificationIsActive.setDisabled(true);
		cbQualificationIsActive.setChecked(qualification.isActive());
		lc.appendChild(cbQualificationIsActive);
		lc.setParent(item);
		lc = new Listcell(qualification.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(qualification.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", qualification.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onQualificationItemDoubleClicked");
	}
}
