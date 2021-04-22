package com.pennant.webui.applicationmaster.policecase.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

public class PoliceCaseListModelItemRenderer implements ListitemRenderer<PoliceCaseDetail>, Serializable {
	private static final long serialVersionUID = 9099171990501035267L;

	public PoliceCaseListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, PoliceCaseDetail policeCaseDetail, int index) throws Exception {
		Listcell lc;
		lc = new Listcell(policeCaseDetail.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(policeCaseDetail.getCustDOB()));
		lc.setParent(item);
		lc = new Listcell(policeCaseDetail.getCustFName());
		lc.setParent(item);
		lc = new Listcell(policeCaseDetail.getCustLName());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatEIDNumber(policeCaseDetail.getCustCRCPR()));
		lc.setParent(item);
		lc = new Listcell(policeCaseDetail.getCustPassportNo());
		lc.setParent(item);
		lc = new Listcell(policeCaseDetail.getMobileNumber());
		lc.setParent(item);
		lc = new Listcell(policeCaseDetail.getCustNationality());
		lc.setParent(item);
		lc = new Listcell(policeCaseDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(policeCaseDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", policeCaseDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPoliceCaseItemDoubleClicked");
	}
}
