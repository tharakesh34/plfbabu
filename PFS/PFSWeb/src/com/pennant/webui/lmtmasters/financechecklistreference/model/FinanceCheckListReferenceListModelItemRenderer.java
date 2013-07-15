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
 * FileName    		:  FinanceCheckListReferenceListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-12-2011    														*
 *                                                                  						*
 * Modified Date    :  08-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.financechecklistreference.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pennant.backend.model.applicationmaster.CheckListDetail;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinanceCheckListReferenceListModelItemRenderer implements ListitemRenderer<CheckListDetail>, Serializable {

	private static final long serialVersionUID = -5988686000244488795L;

	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CheckListDetail data, int count) throws Exception {
		item.setSelected(false);
		if (item instanceof Listgroup) { 
			final CheckListDetail checkListDetail= (CheckListDetail)data;
			item.appendChild(new Listcell(String.valueOf(checkListDetail.getLovDescCheckListDesc()))); 
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(2);
			item.appendChild(cell); 
		} else { 
			final CheckListDetail checkListDetail = (CheckListDetail) data;
			((Listbox)item.getParent()).setMultiple(true);

			Listcell listCell = new Listcell(checkListDetail.getAnsDesc());
			listCell.setParent(item);
			listCell =new Listcell();
			listCell.setId(checkListDetail.getCheckListId()+";"+checkListDetail.getAnsSeqNo());
			Textbox  txtBoxRemarks=new Textbox();
			txtBoxRemarks.setMaxlength(50);
			txtBoxRemarks.setWidth("400px");
			txtBoxRemarks.setVisible(false);
			ComponentsCtrl.applyForward(txtBoxRemarks, "onBlur=onBlurRemarksTextBox");

			txtBoxRemarks.setValue(checkListDetail.getLovDescRemarks());  
			if(checkListDetail.isRemarksAllow()){
				txtBoxRemarks.setVisible(true);
			}
			if(checkListDetail.getLovDescPrevAnsMap().containsKey(listCell.getId())){
				item.setSelected(true);
			}
			if(!checkListDetail.getLovDescFinRefDetail().getAllowInputInStage().contains(checkListDetail.getLovDescUserRole())){
				item.setDisabled(true);
				txtBoxRemarks.setReadonly(true);
			}
			listCell.appendChild(txtBoxRemarks);
			listCell.setParent(item);	
		}
		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onClick=onSelectListItem");

	}
}