package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.service.finance.EligibilityRule;
import com.pennant.backend.util.RuleConstants;
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
			
			
			String labelCode = "";
			String StyleCode = "";
			
			if (elgRule.getElgAmount().compareTo(BigDecimal.ZERO) <= 0) {
				labelCode="Not Eligible";
				StyleCode="font-weight:bold;color:red;";
			}else{
				labelCode="Eligible";
				StyleCode="font-weight:bold;color:green;";
				
			}
			
			if(RuleConstants.RETURNTYPE_DECIMAL.equals(elgRule.getRuleReturnType())){
				if(RuleConstants.ELGRULE_DSRCAL.equals(elgRule.getRuleCode()) ||
						RuleConstants.ELGRULE_PDDSRCAL.equals(elgRule.getRuleCode())){
					BigDecimal val = elgRule.getElgAmount();
					val=val.setScale(2,RoundingMode.HALF_DOWN);
					labelCode = String.valueOf(val)+"%";
				}else{
					labelCode =	PennantAppUtil.amountFormate(elgRule.getElgAmount(),formatter);
				}
				
				StyleCode = "font-weight:bold;text-align:right;";
			}
			
			if (RuleConstants.RETURNTYPE_INTEGER.equals(elgRule.getRuleReturnType())) {
				labelCode=PennantAppUtil.amountFormate(elgRule.getElgAmount(), 0);
				StyleCode = "font-weight:bold;text-align:right;";
			}
			
			lc = new Listcell(labelCode);
			lc.setStyle(StyleCode);
			
			item.appendChild(lc);
		}

	}
	
}
