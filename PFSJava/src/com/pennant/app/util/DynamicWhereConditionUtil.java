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
 * FileName    		:  DynamicWhereConditionUtil.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-04-2012   														*
 *                                                                  						*
 * Modified Date    :  31-04-2012      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  31-04-2012        Pennant	                 0.1                                            * 
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
package com.pennant.app.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.policy.model.UserImpl;

public class DynamicWhereConditionUtil implements Serializable {

    private static final long serialVersionUID = 8679640623211482353L;
    
	/**
	 * This method replaces dynamic where condition constants with related values 
	 * E.g &ROLES replaces with ('MAKER',"APPROVER')
	 * @param aDashboardConfiguration
	 * @return
	 */
	public static String getModifiedQuery(DashboardConfiguration aDashboardConfiguration){
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		UserImpl userDetails = (UserImpl) currentUser.getPrincipal();

		Map<String,String> listDynWhereConditions = getWhereConditionsList();
		String query=aDashboardConfiguration.getQuery().toUpperCase();
		for(String valueLabel:listDynWhereConditions.keySet()){
			String whereCondtion=valueLabel.toUpperCase();
			if(StringUtils.contains(query, whereCondtion)){
				if(StringUtils.equalsIgnoreCase(whereCondtion, Labels
						.getLabel("label_WhereConditonConstant_&ROLES"))){    //Replacing User Roles  

					List<SecurityRole> roles=userDetails.getSecurityRole();
					String role="(";
					for (int i = 0; i < roles.size(); i++) {
						role=role+"'"+roles.get(i).getRoleCd()+"'";

						if(i!=roles.size()-1){
							role=role+",";
						}
					}
					role=role+")";
					query=StringUtils.replace(query, whereCondtion,role);
					return query;
				}else if(StringUtils.equalsIgnoreCase(whereCondtion, Labels       //Replacing UserLogin 
						.getLabel("label_WhereConditonConstant_&USERLOGIN"))){

					SecurityUser secUser=userDetails.getSecurityUser();
					query=StringUtils.replace(query, whereCondtion,secUser.getUsrLogin());
					return query;
				}
			}
		}
		return query;
	}
	
	/**
	 * This method returns dynamic where condition constants
	 * @return
	 */
	private static Map<String,String> getWhereConditionsList() {
		Map<String,String> dynWhereConditionMap = new HashMap<String,String>();
		dynWhereConditionMap.put(Labels
				.getLabel("label_WhereConditonConstant_&ROLES"), Labels
				.getLabel("label_WhereConditonConstant_&ROLES"));
		dynWhereConditionMap.put(Labels
				.getLabel("label_WhereConditonConstant_&USERLOGIN"), Labels
				.getLabel("label_WhereConditonConstant_&USERLOGIN"));
		return dynWhereConditionMap;
	}

}
