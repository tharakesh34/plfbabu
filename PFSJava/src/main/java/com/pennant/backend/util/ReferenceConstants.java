/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * 
 * FileName : PennantConstants.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.util;


/**
 * This stores all constants required for running the application
 */
public class ReferenceConstants {
	
	private ReferenceConstants() {
		super();
	}

	public static final int BRANCH_PRODUCT_SEQUENCE=1; //Sequence generated with Branch and product combination  EX: HYD CON 000000000001  
	public static final int DIVISION_SEQUENCE=2;     //Sequence generated with division and sequence number EX:  PB 1600200523
	public static final String DIVISION_IDENTIFIER="PA";     //
	
	
}
