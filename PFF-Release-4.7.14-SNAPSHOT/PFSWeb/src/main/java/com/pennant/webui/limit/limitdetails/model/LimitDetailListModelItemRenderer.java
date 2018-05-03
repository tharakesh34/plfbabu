/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LimitDetailListModelItemRenderer.java                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.webui.limit.limitdetails.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class LimitDetailListModelItemRenderer implements ListitemRenderer<LimitHeader>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, LimitHeader limitHeader, int count) throws Exception {

		Listcell lc;
		Label lb;
		if(limitHeader.getRuleCode()!=null && StringUtils.isNotEmpty(limitHeader.getRuleCode())){
			
			lc = new Listcell(String.valueOf(limitHeader.getRuleCode()));
		  	lc.setParent(item);
		  	
		  	lc = new Listcell(String.valueOf(limitHeader.getQueryDesc()));
		  	lc.setParent(item);
		  			  
		}else{
		if(limitHeader.getCustomerId()!=0 ){
			lc = new Listcell(String.valueOf(limitHeader.getCustCIF()));
		  	lc.setParent(item);
		  	
		  	lc = new Listcell();
		  	lb=new Label();
		  	lb.setValue(limitHeader.getCustShrtName());
		  	lb.setParent(lc);
			lc.setParent(item);
			lc.setTooltip(limitHeader.getCustShrtName());
		}else{
			lc = new Listcell(String.valueOf(limitHeader.getCustGrpCode()));
		  	lc.setParent(item);
		  	lc = new Listcell();
		  	lb=new Label();
			lb.setValue(limitHeader.getGroupName());
		  	lb.setParent(lc);
			lc.setParent(item);		
			lc.setTooltip(limitHeader.getGroupName());
		}
		}
		lc = new Listcell(limitHeader.getLimitStructureCode());
		lc.setParent(item);
		lc = new Listcell(limitHeader.getResponsibleBranchName());
		lc.setParent(item);
	  	lc = new Listcell(limitHeader.getLimitCcy());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(limitHeader.getLimitExpiryDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(limitHeader.getLimitRvwDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
		
		lc = new Listcell();		
		Checkbox ckActive= new Checkbox();
		ckActive.setChecked(limitHeader.isActive());
		ckActive.setDisabled(true);
		ckActive.setParent(lc);
		lc.setParent(item);
		
	  	lc = new Listcell(limitHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(limitHeader.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", limitHeader.getHeaderId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLimitDetailsItemDoubleClicked");
	}
}