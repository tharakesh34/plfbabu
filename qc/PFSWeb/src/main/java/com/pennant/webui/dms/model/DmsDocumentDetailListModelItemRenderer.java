package com.pennant.webui.dms.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennanttech.model.dms.DMSDocumentDetails;

public class DmsDocumentDetailListModelItemRenderer implements ListitemRenderer<DMSDocumentDetails>, Serializable {

	private static final long serialVersionUID = -963011383945619844L;

	public DmsDocumentDetailListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, DMSDocumentDetails data, int index) throws Exception {
		Listcell lc;
		lc = new Listcell(data.getFinReference());
		lc.setParent(item);

		lc = new Listcell(String.valueOf(data.getDocRefId()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(data.getId()));
		lc.setParent(item);

		lc = new Listcell(data.getStatus());
		lc.setParent(item);
		item.setAttribute("dmsId", data.getId());
		item.setAttribute("dmsDocumentDetail", data);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDmsDocumentDetailItemDoubleClicked");
	}

}
