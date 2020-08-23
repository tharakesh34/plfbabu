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
 * FileName    		:  BuilderUtilListbox.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.dedup.dedupfields;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import com.pennant.app.constants.ImplementationConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

public class BuilderUtilListbox {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ListModel operatorLabel() {

		List data = new ArrayList();
		data.add("EQUALS");
		data.add("LIKE");
		if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
			data.add("SIMILARITY >= 50%");
			data.add("SIMILARITY >= 60%");
			data.add("SIMILARITY >= 70%");
			data.add("SIMILARITY >= 80%");
			data.add("SIMILARITY >= 90%");
			data.add("SIMILARITY >= 100%");
		}
		return new ListModelList(data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ListModel operatorValue() {

		List data = new ArrayList();
		data.add(" = ");
		data.add(" LIKE ");
		if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
			data.add("SIMILARITY50");
			data.add("SIMILARITY60");
			data.add("SIMILARITY70");
			data.add("SIMILARITY80");
			data.add("SIMILARITY90");
			data.add("SIMILARITY100");
		}
		return new ListModelList(data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ListModel getLogicModel() {

		List data = new ArrayList();
		data.add("AND");
		data.add("OR");

		return new ListModelList(data);
	}

}
