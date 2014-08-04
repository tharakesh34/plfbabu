package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.service.finance.EligibilityRule;
import com.pennant.util.PennantAppUtil;

public class EligibilityCheckListItemRenderer implements ListitemRenderer<EligibilityRule>, Serializable{
	
	private static final long serialVersionUID = 5574543684897936853L;
	
	private int formatter;

	public EligibilityCheckListItemRenderer(int formatter) {
		super();
		this.formatter = formatter;
	}

	@Override
	public void render(Listitem item, EligibilityRule elgRule, int count) throws Exception {

		if (item instanceof Listgroup) { 
			item.appendChild(new Listcell(elgRule.getFinType() +" - "+elgRule.getFinTypeDesc()));
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(3);
			item.appendChild(cell); 
		} else { 

			Listcell lc;
			lc = new Listcell(elgRule.getRuleCode());
			item.appendChild(lc);
			
			lc = new Listcell(elgRule.getRuleCodeDesc());
			item.appendChild(lc);
			
			if("S".equals(elgRule.getRuleReturnType())){
				
				if(elgRule.getElgAmount().compareTo(new BigDecimal(-1)) == 0){
					lc = new Listcell("Not Eligible");
					lc.setStyle("font-weight:bold;color:red;");
				}else {
					lc = new Listcell("Eligible");
					lc.setStyle("font-weight:bold;color:green;");
				}
			}else if("B".equals(elgRule.getRuleReturnType())){
				
				if(elgRule.getElgAmount().compareTo(BigDecimal.ZERO) == 0){
					lc = new Listcell("Not Eligible");
					lc.setStyle("font-weight:bold;color:red;");
				}else {
					lc = new Listcell("Eligible");
					lc.setStyle("font-weight:bold;color:green;");
				}
			}else if("D".equals(elgRule.getRuleReturnType())){

				//IF Error in Executing the Rule
				if("DSRCAL".equals(elgRule.getRuleCode())){
					lc = new Listcell(elgRule.getElgAmount()+"%");
				}else{
					lc = new Listcell(PennantAppUtil.amountFormate(elgRule.getElgAmount(),formatter));
					lc.setStyle("font-weight:bold;text-align:right;");
				}
				
			}else if("I".equals(elgRule.getRuleReturnType())){

				lc = new Listcell(PennantAppUtil.amountFormate(elgRule.getElgAmount(), 0));
				lc.setStyle("font-weight:bold;text-align:right;");
			}
			item.appendChild(lc);
		}

	}
	
}
