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
 * FileName    		:  EducationalLoanListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.educationalloan.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class EducationalLoanListModelItemRenderer implements ListitemRenderer<EducationalLoan>, Serializable {

	private static final long serialVersionUID = 8016193331928366977L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, EducationalLoan educationalLoan, int count) throws Exception {

		//final EducationalLoan educationalLoan = (EducationalLoan) data;
		Listcell lc;
	  	lc = new Listcell(educationalLoan.getLoanRefNumber());
	  	lc.setParent(item);
	  	lc = new Listcell(educationalLoan.getEduCourse());
		lc.setParent(item);
	  	lc = new Listcell(educationalLoan.getEduSpecialization());
		lc.setParent(item);
	  	lc = new Listcell(educationalLoan.getEduCourseType());
		lc.setParent(item);
	  	lc = new Listcell(educationalLoan.getEduCourseFromBranch());
		lc.setParent(item);
	  	lc = new Listcell(educationalLoan.getEduAffiliatedTo());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(educationalLoan.getEduCommenceDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(educationalLoan.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(educationalLoan.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", educationalLoan);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onEducationalLoanItemDoubleClicked");
	}
}