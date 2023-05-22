package com.pennant.webui.systemmasters.covenant.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.util.PennantJavaUtil;

public class CovenantListModelItemRenderer implements ListitemRenderer<FinCovenantType>, Serializable {

	private static final long serialVersionUID = -7313678083684001775L;

	public CovenantListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinCovenantType finCovenantType, int count) {

		Listcell lc;
		lc = new Listcell(finCovenantType.getFinReference());
		lc.setParent(item);
		lc = new Listcell(finCovenantType.getCovenantType() + "-" + finCovenantType.getCovenantTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox alwWaiver = new Checkbox();
		alwWaiver.setDisabled(true);
		alwWaiver.setChecked(finCovenantType.isAlwWaiver());
		lc.appendChild(alwWaiver);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox alwPostDoc = new Checkbox();
		alwPostDoc.setDisabled(true);
		alwPostDoc.setChecked(finCovenantType.isAlwPostpone());
		lc.appendChild(alwPostDoc);
		lc.setParent(item);
		lc = new Listcell(finCovenantType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finCovenantType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("data", finCovenantType);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCovenantItemDoubleClicked");
	}

}
