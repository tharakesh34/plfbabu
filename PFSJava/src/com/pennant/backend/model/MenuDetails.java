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
 * FileName    		:  MenuDetails.java														*                           
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
package com.pennant.backend.model;

import java.sql.Timestamp;

public class MenuDetails implements java.io.Serializable, Entity {
	
private static final long serialVersionUID = -8921214349365225047L;

	
	private long menuId;
	private long menuApp;
	private String menuCode;
	private String menuRef; 
	private String menuZulPath;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String appCode;

	public MenuDetails () {
		
	}
	
	public MenuDetails(long menuId, long menuApp, String menuCode, String menuRef,
			 String menuZulPath, long lastMntBy,Timestamp lastMntOn) {
		this.menuId = menuId;
		this.menuApp = menuApp;
		this.menuCode = menuCode;
		this.menuRef = menuRef;
		this.menuZulPath = menuZulPath;
		this.lastMntBy = lastMntBy;
		this.lastMntOn = lastMntOn;
	}
	
	public boolean isNew() {
		return (getId() == Long.MIN_VALUE);
	}

	public long getId(){
		return menuId;
	}

	public void setId(long Id){
		this.menuId=Id;
	}
	
	public long getMenuId() {
		return menuId;
	}

	public void setMenuId(long menuId) {
		this.menuId = menuId;
	}
	public long getMenuApp() {
		return menuApp;
	}
	public void setMenuApp(long menuApp) {
		this.menuApp = menuApp;
	}

	public String getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}

	public String getMenuRef() {
		return menuRef;
	}

	public void setMenuRef(String menuRef) {
		this.menuRef = menuRef;
	}

	public String getMenuZulPath() {
		return menuZulPath;
	}

	public void setMenuZulPath(String menuZulPath) {
		this.menuZulPath = menuZulPath;
	}


	public long getLastMntBy() {
		return this.lastMntBy;
	}


	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}


	public Timestamp getLastMntOn() {
		return this.lastMntOn;
	}
	
	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}
	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof MenuDetails ) {
			MenuDetails  menuDetails  = (MenuDetails ) obj;
			return equals(menuDetails );
		}

		return false;
	}

}
