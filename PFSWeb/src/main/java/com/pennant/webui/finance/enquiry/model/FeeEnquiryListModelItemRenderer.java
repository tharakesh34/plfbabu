package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.util.PennantAppUtil;

public class FeeEnquiryListModelItemRenderer implements ListitemRenderer<FinFeeDetail>, Serializable{

	private static final long serialVersionUID = 3541122568618470160L;
	private int formatter;

	public FeeEnquiryListModelItemRenderer(int formatter) {
		super();
		this.formatter = formatter;
	}
	@Override
	public void render(Listitem item, FinFeeDetail finFeeDetail, int count) throws Exception {
		
		if (item instanceof Listgroup) { 
			item.appendChild(new Listcell(finFeeDetail.getFinEvent()));
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell); 
		} else { 

			Listcell lc;
			String feeType = finFeeDetail.getFeeTypeDesc();
			if (StringUtils.isNotEmpty(finFeeDetail.getVasReference())) {
				feeType = finFeeDetail.getVasReference();
				finFeeDetail.setFeeTypeCode(feeType);
				finFeeDetail.setFeeTypeDesc(feeType);
			}
			lc = new Listcell(feeType);
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.amountFormate(finFeeDetail.getCalculatedAmount(), formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.amountFormate(finFeeDetail.getActualAmount(), formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.amountFormate(finFeeDetail.getWaivedAmount(), formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.amountFormate(finFeeDetail.getPaidAmount(), formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.amountFormate(finFeeDetail.getRemainingFee(), formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			if (StringUtils.equals("#",finFeeDetail.getPaymentRef())) {
				lc = new Listcell("");
			} else {
				lc = new Listcell(finFeeDetail.getPaymentRef());
			}
			lc.setParent(item);

			if (StringUtils.equals("#",finFeeDetail.getPaymentRef())) {
				lc = new Listcell("");
			} else {
				lc = new Listcell(finFeeDetail.getFeeScheduleMethod());
			}
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.formateInt(finFeeDetail.getTerms()));
			lc.setParent(item);

		}
	}
}
