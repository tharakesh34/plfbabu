package com.pennant.webui.solutionfactory.extendedfielddetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

public class ExtendedFieldListItemRenderer implements ListitemRenderer<ExtendedFieldDetail>, Serializable {
	
	private static final long serialVersionUID = 6321996138703133595L;

	public ExtendedFieldListItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, ExtendedFieldDetail detail,int count) throws Exception {
		
		Listcell lc;
		lc = new Listcell(detail.getFieldName());
		lc.setParent(item);
		lc = new Listcell(detail.getFieldLabel());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(detail.getFieldType(), PennantStaticListUtil.getFieldType()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(detail.getFieldSeqOrder()));
		lc.setParent(item);
		lc = new Listcell(detail.getRecordStatus());
		lc.setParent(item);
		
		if ("NEW".equals(detail.getRecordType())) {
			Button delete = new Button();
			delete.setLabel("Delete");
			delete.addForward("onClick", "self", "onClickDeleteColumn");
			lc = new Listcell();
			lc.appendChild(delete);
			lc.setParent(item);
		} else {
			lc = new Listcell("");
			lc.setParent(item);
		}
		
		lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
		lc.setParent(item);
		
		
		
		item.setAttribute("data", detail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldItemDoubleClicked");
	}

}
