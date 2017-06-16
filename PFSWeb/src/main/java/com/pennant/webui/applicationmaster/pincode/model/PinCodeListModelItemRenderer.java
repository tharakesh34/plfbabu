package com.pennant.webui.applicationmaster.pincode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.util.PennantJavaUtil;

public class PinCodeListModelItemRenderer implements ListitemRenderer<PinCode>, Serializable {

	private static final long serialVersionUID = 1L;

	public PinCodeListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, PinCode pinCode, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(pinCode.getPinCode());
		lc.setParent(item);
	  	lc = new Listcell(pinCode.getPCCityName());
		lc.setParent(item);
		lc = new Listcell(pinCode.getAreaName());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(pinCode.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
	  	lc = new Listcell(pinCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(pinCode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("pinCodeId", pinCode.getPinCodeId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onPinCodeItemDoubleClicked");
	}
}