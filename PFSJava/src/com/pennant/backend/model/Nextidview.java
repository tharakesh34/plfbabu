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
 * FileName    		:  Nextview.java														*                           
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
 *
*/package com.pennant.backend.model;

public class Nextidview implements java.io.Serializable {

	private static final long serialVersionUID = 8543471037915270000L;

	private Long nextval;

	public Nextidview() {
	}

	public Nextidview(Long nextval) {
		this.nextval = nextval;
	}

	public Long getNextval() {
		return this.nextval;
	}

	public void setNextval(Long nextval) {
		this.nextval = nextval;
	}

	@Override
	public int hashCode() {
		return getNextval().hashCode();
	}

	public boolean equals(Nextidview nextidview) {
		return getNextval() == nextidview.getNextval();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Nextidview) {
			Nextidview nextidview = (Nextidview) obj;
			return equals(nextidview);
		}

		return false;
	}
}
