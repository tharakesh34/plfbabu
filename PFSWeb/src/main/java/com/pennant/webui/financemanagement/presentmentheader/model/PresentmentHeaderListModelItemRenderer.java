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
 * FileName    		:  PresentmentHeaderListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.presentmentheader.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class PresentmentHeaderListModelItemRenderer implements ListitemRenderer<PresentmentHeader>, Serializable {

	private static final long serialVersionUID = 1L;

	public PresentmentHeaderListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, PresentmentHeader presentmentHeader, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(presentmentHeader.getReference());
		lc.setParent(item);
		
		lc = new Listcell(DateUtility.formatToLongDate(presentmentHeader.getSchdate()));
		lc.setParent(item);
	  	
	  	lc = new Listcell(presentmentHeader.getPartnerBankName());
		lc.setParent(item);
		
	  	lc = new Listcell(PennantStaticListUtil.getlabelDesc(String.valueOf(presentmentHeader.getStatus()),
				PennantStaticListUtil.getPresentmentBatchStatusList()));
	  	lc.setParent(item);
	  	
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(presentmentHeader.getMandateType(), PennantStaticListUtil.getMandateTypeList()));
	  	lc.setParent(item);
	  	
	  	lc = new Listcell(DateUtility.formatToLongDate(presentmentHeader.getPresentmentDate()));
	  	lc.setParent(item);
	  	
	  	lc = new Listcell(presentmentHeader.getRecordStatus());
		lc.setParent(item);
		
		lc = new Listcell(PennantJavaUtil.getLabel(presentmentHeader.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("id", presentmentHeader.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onPresentmentHeaderItemDoubleClicked");
	}
}