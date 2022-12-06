package com.pennant.webui.rmtmasters.financetype;

import java.io.Serializable;
import java.util.List;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;

public class FinTypePartnerbankListModelItemRenderer implements ListitemRenderer<FinTypePartnerBank>, Serializable {
	private static final long serialVersionUID = 1L;

	public FinTypePartnerbankListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, FinTypePartnerBank finTypePartnerBank, int index) throws Exception {

		List<ValueLabel> purposeList = PennantStaticListUtil.getPurposeList();
		List<ValueLabel> paymentModesList = PennantStaticListUtil.getAllPaymentTypesWithIST();

		Listcell lc;

		lc = new Listcell(finTypePartnerBank.getFinType());
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.getLabelDesc(finTypePartnerBank.getPurpose(), purposeList));
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.getLabelDesc(finTypePartnerBank.getPaymentMode(), paymentModesList));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(finTypePartnerBank.getPartnerBankCode()));
		lc.setParent(item);

		if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("B")) {
			lc = new Listcell(finTypePartnerBank.getBranchDesc());
			lc.setParent(item);
		} else if (ImplementationConstants.PARTNERBANK_MAPPING_BRANCH_OR_CLUSTER.equals("C")) {
			lc = new Listcell(String.valueOf(finTypePartnerBank.getClusterCode()) + "-" + finTypePartnerBank.getName());
			lc.setParent(item);
		}

		lc = new Listcell(finTypePartnerBank.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(finTypePartnerBank.getRecordType());
		lc.setParent(item);

		item.setAttribute("data", finTypePartnerBank);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypePartnerBankMappingItemDoubleClicked");

	}

}
