package com.pennant.webui.verification.legalverification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;

public class LegalVerificationListModelItemRender implements ListitemRenderer<LegalVerification>, Serializable {
	private static final long serialVersionUID = 1L;

	List<Long> list=new ArrayList<>();
	public LegalVerificationListModelItemRender() {
		super();
	}

	@Override
	public void render(Listitem item, LegalVerification lv, int count) throws Exception {
		
		Listcell lc;
	    lc = new Listcell(String.valueOf(lv.getCif()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(lv.getCollateralType()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(lv.getReferenceFor()));
		lc.setParent(item);
		lc = new Listcell(lv.getKeyReference());
	  	lc.setParent(item);
		lc = new Listcell(lv.getAgencyName());
	  	lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(lv.getCreatedOn()));
	  	lc.setParent(item);
	  	lc = new Listcell(lv.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(lv.getRecordType()));
		lc.setParent(item);

		item.setAttribute("documentId", lv.getDocumentId());
		item.setAttribute("documentSubId", lv.getDocumentSubId());
		item.setAttribute("verificationId", lv.getVerificationId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalVerificationItemDoubleClicked");
	}
	
}
