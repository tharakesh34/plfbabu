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
 *
 * FileName    		:  GFCBAseListCtl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.util;

import java.util.List;

import org.zkoss.zul.Listitem;

import com.pennant.backend.model.AuditLogDetils;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.service.AuditLogService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.pagging.PagedBindingListWrapper;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * Extended the GFCBase controller for a pagedListWrapper for a single type.
 * 
 */
public class GFCBaseListCtrl<T> extends GFCBaseCtrl {

	private static final long	       serialVersionUID	  = -3741197830243792411L;

	private PagedListWrapper<T>	       pagedListWrapper;

	private PagedBindingListWrapper<T>	pagedBindingListWrapper;

	private AuditLogService	           auditLogService;

	public PagedListWrapper<T> getPagedListWrapper() {
		return pagedListWrapper;
	}

	public void setPagedListWrapper(PagedListWrapper<T> pagedListWrapper) {
		this.pagedListWrapper = pagedListWrapper;
	}

	public void setPagedBindingListWrapper(PagedBindingListWrapper<T> pagedBindingListWrapper) {
		this.pagedBindingListWrapper = pagedBindingListWrapper;
	}

	public PagedBindingListWrapper<T> getPagedBindingListWrapper() {
		return pagedBindingListWrapper;
	}

	public AuditLogService getAuditLogService() {
		return auditLogService;
	}

	public void setAuditLogService(AuditLogService auditLogService) {
		this.auditLogService = auditLogService;
	}

	public boolean validateUserAccess(long workFlowId, long userID, String modName, String whereCond, String taskID, String nextTaskId) {

		List<AuditLogDetils> listLogDetils = this.auditLogService.getLogDetails(modName, whereCond);
		System.out.println(listLogDetils.size());
		return true;
	}


	public boolean validateUserAccess(String moduleName, String[] keyFields, String recordRole, long currentUser, Object beanObject) {

		List<AuditLogDetils> listLogDetils = this.auditLogService.getLogDetails(moduleName, keyFields, recordRole, currentUser, beanObject);

		if (listLogDetils != null && !listLogDetils.isEmpty()) {
			return false;
		}
		return true;
	}
	public JdbcSearchObject<T> getSearchFilter(JdbcSearchObject<T> searchObj,Listitem selectedItem, Object value, String fieldName ){
		
		if (selectedItem != null) {
			int searchOpId = -1;
			if(selectedItem.getAttribute("data")!=null){
				searchOpId =((SearchOperators) selectedItem.getAttribute("data")).getSearchOperatorId(); 
			}
					
			switch (searchOpId) {
			case -1:
				break;
			case Filter.OP_LIKE:
				searchObj.addFilter(new Filter(fieldName, "%" + value + "%", searchOpId));
				break;
			default:
				searchObj.addFilter(new Filter(fieldName, value, searchOpId));
				break;
			}
		}
		
		return searchObj;
	}
	
	/*
	 * Method For Getting UsrFinAuthentication By Branch and Division
	 */
	public String getUsrFinAuthenticationQry(boolean isForReports) {
		StringBuilder wherQuery = new StringBuilder();
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList() == null) {
			getUserWorkspace().getLoginUserDetails().setSecurityUserDivBranchList(PennantAppUtil.getSecurityUserDivBranchList(getUserWorkspace().getLoginUserDetails().getLoginUsrID())); // TODO
		}

		String divisionField = "";
		String branchField = "";
		if (isForReports) {
			divisionField = "FinDivision";
			branchField = "BranchCode";
		} else {
			divisionField = "lovDescFinDivision";
			branchField = "FinBranch";
		}

		String divisionCode = "";
		String branchCode = "";
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList().isEmpty()) {
			return " ( " + divisionField + "= '' and (" + branchField + "= '' ))";
		}
		for (SecurityUserDivBranch securityUserDivBranch : getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList()) {
			if (!divisionCode.equals("") && !divisionCode.equals(securityUserDivBranch.getUserDivision())) {
				divisionCode = securityUserDivBranch.getUserDivision();
				wherQuery.append("  )) or (( " + divisionField + "= '");
				wherQuery.append(divisionCode + "' ) And " + branchField + " In( ");
				branchCode = "";
			} else if (divisionCode.equals("")) {
				divisionCode = securityUserDivBranch.getUserDivision();
				wherQuery.append(" ((( " + divisionField + "= '" + divisionCode + "' ) And " + branchField + " In( ");
			} else if (!branchCode.equals("")) {
				wherQuery.append(", " + securityUserDivBranch.getUserBranch() + " ");
			}
			if (branchCode.equals("")) {
				wherQuery.append(securityUserDivBranch.getUserBranch() + " ");
			}
			branchCode = securityUserDivBranch.getUserBranch();
		}
		wherQuery.append(" ))) ");
		return wherQuery.toString();
	}
	
	/*
	 * Method For Getting UsrFinAuthentication By FinDivision
	 */
	public String getUsrFinDivisionAuthenticationQry(boolean isForReports) {
		StringBuilder wherQuery = new StringBuilder();
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList() == null) {
			getUserWorkspace().getLoginUserDetails().setSecurityUserDivBranchList(PennantAppUtil.getSecurityUserDivBranchList(getUserWorkspace().getLoginUserDetails().getLoginUsrID())); // TODO
		}
		
		String divisionField = "";
		if (isForReports) {
			divisionField = "FinDivision";
		} else {
			divisionField = "lovDescFinDivision";
		}
		
		String divisionCode = "";
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList().isEmpty()) {
			return " ( " + divisionField + "= '' )";
		}
		for (SecurityUserDivBranch securityUserDivBranch : getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList()) {
			if (!divisionCode.equals("") && wherQuery.toString().contains(divisionField)) {
				divisionCode = securityUserDivBranch.getUserDivision();
				wherQuery.append( ",'"+divisionCode+"'");
			} else if (divisionCode.equals("")) {
				divisionCode = securityUserDivBranch.getUserDivision();
				wherQuery.append(" ( " + divisionField + " IN ('" + divisionCode+"'");
			} 
		}
		wherQuery.append(" )) ");
		return wherQuery.toString();
	}
	
	/*
	 * Method For Getting UsrFinBranchAuthentication By Branch
	 */
	public String getUsrFinBranchAuthenticationQry(boolean isForReports) {
		StringBuilder wherQuery = new StringBuilder();
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList() == null) {
			getUserWorkspace().getLoginUserDetails().setSecurityUserDivBranchList(PennantAppUtil.getSecurityUserDivBranchList(getUserWorkspace().getLoginUserDetails().getLoginUsrID())); // TODO
		}

		String branchField = "";
		if (isForReports) {
			branchField = "BranchCode";
		} else {
			branchField = "FinBranch";
		}

		String branchCode = "";
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList().isEmpty()) {
			return "(" + branchField + "= '' ))";
		}
		wherQuery.append(branchField+" IN (");
		for (SecurityUserDivBranch securityUserDivBranch : getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList()) {
		 if (!branchCode.equals("") && !wherQuery.toString().contains(securityUserDivBranch.getUserBranch())) {
				wherQuery.append(", " + securityUserDivBranch.getUserBranch() + " ");
			}
			if (branchCode.equals("")) {
				wherQuery.append(securityUserDivBranch.getUserBranch() + " ");
			}
			branchCode = securityUserDivBranch.getUserBranch();
		}
		wherQuery.append(" )");
		return wherQuery.toString();
	}

}