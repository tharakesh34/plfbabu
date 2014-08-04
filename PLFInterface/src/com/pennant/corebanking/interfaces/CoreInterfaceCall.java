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
 * FileName    		:  CoreInterfaceCall.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2013    														*
 *                                                                  						*
 * Modified Date    :  31-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.corebanking.interfaces;

import java.util.List;

import com.pennant.equation.process.EquationAccountType;
import com.pennant.equation.process.EquationCurrency;
import com.pennant.equation.process.EquationCustomerGroup;
import com.pennant.equation.process.EquationCustomerRating;
import com.pennant.equation.process.EquationCustomerType;
import com.pennant.equation.process.EquationDepartment;
import com.pennant.equation.process.EquationRelationshipOfficer;



public interface CoreInterfaceCall {
	
	public List<EquationCurrency>  importCurrencyDetails() throws Exception;
	public List<EquationRelationshipOfficer>  importRelationShipOfficersDetails() throws Exception;
	public List<EquationCustomerType>   importCustomerTypeDetails() throws Exception;
	public List<EquationDepartment>  importDepartmentDetails() throws Exception;
	public List<EquationCustomerGroup>  importCustomerGroupDetails() throws Exception;
	public List<EquationAccountType>  importAccountTypeDetails() throws Exception;
	public List<EquationCustomerRating> importCustomerRatingDetails() throws Exception;

}
