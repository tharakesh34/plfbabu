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
 * FileName    		:  JVPostingListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.others.jvposting.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class JVPostingListModelItemRenderer implements ListitemRenderer<JVPosting>, Serializable {

	private static final long serialVersionUID = 1L;
	int format=getformatter();
	@Override
	public void render(Listitem item, JVPosting jVPosting, int count) throws Exception {
		Listcell lc;
	  	lc = new Listcell(jVPosting.getBatchReference());
		lc.setParent(item);
	  	lc = new Listcell(jVPosting.getBatch());
		lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateInt(jVPosting.getDebitCount()));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateInt(jVPosting.getCreditsCount()));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.amountFormate(jVPosting.getTotDebitsByBatchCcy(),format));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.amountFormate(jVPosting.getTotCreditsByBatchCcy(),format));
	  	lc.setParent(item);
	  	lc = new Listcell(jVPosting.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(jVPosting.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", jVPosting);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onJVPostingItemDoubleClicked");
	}
	
	private int getformatter() {
		Currency currency= PennantAppUtil.getCuurencyBycode(SystemParameterDetails.getSystemParameterValue("EXT_BASE_CCY").toString());
		if (currency!=null) {
	        return currency.getCcyEditField();
        }else{
        	return 0;
        }
	}
}