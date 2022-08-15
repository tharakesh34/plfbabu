package com.pennant.webui.systemmasters.ocr.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.util.PennantJavaUtil;

public class OCRListModelItemRenderer implements ListitemRenderer<OCRHeader>, Serializable {

	private static final long serialVersionUID = -2115424367644659335L;

	public OCRListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, OCRHeader ocrHeader, int index) {
		Listcell lc;
		lc = new Listcell(ocrHeader.getOcrID());
		lc.setParent(item);
		lc = new Listcell(ocrHeader.getOcrDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox ocrIsActive = new Checkbox();
		ocrIsActive.setDisabled(true);
		ocrIsActive.setChecked(ocrHeader.isActive());
		lc.appendChild(ocrIsActive);
		lc.setParent(item);
		lc = new Listcell(ocrHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(ocrHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", ocrHeader.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onOCRItemDoubleClicked");

	}

}
