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
 * FileName    		:  TakafulProviderListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmasters.takafulprovider.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TakafulProviderListModelItemRenderer implements ListitemRenderer<TakafulProvider>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, TakafulProvider takafulProvider, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(takafulProvider.getTakafulCode());
		lc.setParent(item);
	  	lc = new Listcell(takafulProvider.getTakafulName());
		lc.setParent(item);
	  	lc = new Listcell(PennantStaticListUtil.getlabelDesc(takafulProvider.getTakafulType(),PennantStaticListUtil.getTakafulTypes()) );
		lc.setParent(item);
	  	lc = new Listcell(String.valueOf(takafulProvider.getTakafulRate()));
		lc.setParent(item);
	  	lc = new Listcell(takafulProvider.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(takafulProvider.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", takafulProvider);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onTakafulProviderItemDoubleClicked");
	}
}