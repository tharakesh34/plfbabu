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
 * FileName    		:  QueryDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-05-2018    														*
 *                                                                  						*
 * Modified Date    :  09-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-05-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.loanquery.querydetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.loanquery.QueryDetail;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class QueryDetailListModelItemRenderer implements ListitemRenderer<QueryDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public QueryDetailListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, QueryDetail queryDetail, int count) throws Exception {

		Listcell lc;
		
	  	lc = new Listcell(queryDetail.getFinReference());
		lc.setParent(item);
	    lc = new Listcell(String.valueOf(queryDetail.getId()));
		lc.setParent(item);
	  	lc = new Listcell(String.valueOf(queryDetail.getRaisedBy())+" - "+queryDetail.getUsrLogin());
		lc.setParent(item);
	  	lc = new Listcell(DateUtility.formatDate(queryDetail.getRaisedOn(),"dd/MM/yyy"));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(queryDetail.getCategoryCode()+" - "+queryDetail.getCategoryDescription()));
		lc.setParent(item);
		lc = new Listcell(queryDetail.getQryNotes());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(queryDetail.getStatus()));
		lc.setParent(item);
		item.setAttribute("id", queryDetail.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onQueryDetailItemDoubleClicked");
	}
}