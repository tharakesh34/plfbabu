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
 * FileName    		:  ScoringMetricsListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.scoringmetrics.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ScoringMetricsListModelItemRenderer implements ListitemRenderer<ScoringMetrics>, Serializable {

	private static final long serialVersionUID = -428249648119783107L;

	public ScoringMetricsListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, ScoringMetrics scoringMetrics, int count) {

		Listcell lc;
		lc = new Listcell(scoringMetrics.getLovDescScoringCode());
		lc.setParent(item);
		lc = new Listcell(scoringMetrics.getLovDescScoringCodeDesc());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(scoringMetrics.getLovDescMetricMaxPoints()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(scoringMetrics.getLovDescMetricTotPerc());
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(scoringMetrics.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(scoringMetrics.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", scoringMetrics);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onScoringMetricsItemDoubleClicked");
	}
}