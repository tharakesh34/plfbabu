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
 * FileName    		:  LoginStatus.java														*                           
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoginStatus implements Serializable {

	private static final long serialVersionUID = -3863392491172579819L;

	private int id;
	private String lgsStatus;

	public LoginStatus() {
	}

	public LoginStatus(int id, String lgsStatus) {
		this.setId(id);
		this.lgsStatus = lgsStatus;

	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setLgsStatus(String lgsStatus) {
		this.lgsStatus = lgsStatus;
	}

	public String getStpTypname() {
		return lgsStatus;
	}

	public List<LoginStatus> getAllTypes() {

		List<LoginStatus> result = new ArrayList<LoginStatus>();

		result.add(new LoginStatus(0, "login failed"));
		result.add(new LoginStatus(1, "login"));
		result.add(new LoginStatus(2, "login"));

		return result;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(getId()).hashCode();
	}

	public boolean equals(LoginStatus loginStatus) {
		return getId() == loginStatus.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof LoginStatus) {
			LoginStatus loginStatus = (LoginStatus) obj;
			return equals(loginStatus);
		}

		return false;
	}

}
