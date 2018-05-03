package com.pennant.webui.applicationmaster.MMAgreement.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

public class MMAgreementListModelItemRenderer implements ListitemRenderer<MMAgreement>, Serializable {

	private static final long serialVersionUID = -4562142056572229437L;
	
	public MMAgreementListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, MMAgreement aMMAgreement, int count) throws Exception {

		Listcell lc;
		lc = new Listcell();
		lc = new Listcell(aMMAgreement.getMMAReference());
		lc.setParent(item);		
	  	lc = new Listcell(aMMAgreement.getCustCIF());
		lc.setParent(item);
		int finFormatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
		lc = new Listcell(PennantAppUtil.amountFormate(aMMAgreement.getContractAmt(), finFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(aMMAgreement.getContractDate()));
		lc.setParent(item);
		if(aMMAgreement.getRate() == null){
			lc = new Listcell("");
			lc.setParent(item);
		}else{
			lc = new Listcell(PennantApplicationUtil.formatRate(aMMAgreement.getRate().doubleValue(),9));
			lc.setParent(item);		
		}
	  	lc = new Listcell(aMMAgreement.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(aMMAgreement.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", aMMAgreement.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onMMAgreementItemDoubleClicked");
	}

}
