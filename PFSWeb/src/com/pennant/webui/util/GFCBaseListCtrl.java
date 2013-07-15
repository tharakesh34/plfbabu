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

import org.zkoss.zk.ui.Path;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listitem;

import com.pennant.backend.model.AuditLogDetils;
import com.pennant.backend.service.AuditLogService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
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

	private int	                       listRows	          = 0;
	private int	                       gridRows	          = 0;
	public int	                       borderLayoutHeight	= 0;

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

	public int getGridRows() {
		return gridRows;
	}

	public int getListRows() {
		return this.listRows;
	}

	public String getBorderLayoutHeight() {
		return calculateBorderLayoutHeight()+ "px";
	}
	public int calculateBorderLayoutHeight() {
		if (this.borderLayoutHeight == 0) {
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.gridRows = Math.round(this.borderLayoutHeight / 31) - 1;
			this.listRows = Math.round(this.borderLayoutHeight / 24) - 1;
		}
		return borderLayoutHeight;
	}

	public String getListBoxHeight(int gridRowCount) {
		int rowheight = 31;
		if (this.borderLayoutHeight == 0) {
			getBorderLayoutHeight();
		}
		int listboxheight = this.borderLayoutHeight;
		listboxheight = listboxheight - (gridRowCount * rowheight) - 35;
		this.listRows = Math.round(listboxheight / 24) - 1;
		return listboxheight + "px";
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
}