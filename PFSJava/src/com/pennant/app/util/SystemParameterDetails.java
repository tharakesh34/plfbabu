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
 * FileName    		:  SystemParameterDetails.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
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
package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.smtmasters.PFSParameter;

/**
 * Used to get System Parameter values from <b>SystemParameterDetails</b> class.
 */
public class SystemParameterDetails {
	
	private final static Logger logger = Logger.getLogger(SystemParameterDetails.class);

	private static PFSParameterDAO pFSParameterDAO;
	private static HashMap<String,PFSParameter> parmDetails = null;
	private static List<GlobalVariable> globalVariablesList = null;

	/**
	 * Initialization of <b>SystemParameterDetails</b> class.
	 *
	 */
	public static void Init(){
		parmDetails=null;
		getParmList();
	}
	
	/**
	 * Get the List of System Parameters
	 * 
	 * @return HashMap
	 */
	public static HashMap<String,PFSParameter> getParmList(){
		logger.debug("Entering");
		final List<PFSParameter> pfsParameters = getPFSParameterDAO().getAllPFSParameter();
		if (pfsParameters!=null){
			parmDetails = new HashMap<String, PFSParameter>(pfsParameters.size());			
			for (int i = 0; i < pfsParameters.size(); i++) {
				parmDetails.put(pfsParameters.get(i).getSysParmCode(), pfsParameters.get(i));
			}
		}
		logger.debug("Leaving");
		return parmDetails;
	}
	
	public static void setParmDetails(String code, String value){
		logger.debug("Entering");
		if(parmDetails != null){
			PFSParameter pfsParameter = parmDetails.get(code);
			parmDetails.remove(code);
			pfsParameter.setSysParmValue(value);
			parmDetails.put(code, pfsParameter);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the List of System Parameters
	 * 
	 * @return HashMap
	 */
	public static List<GlobalVariable> getGlobaVariableList(){
		logger.debug("Entering");
		
		if (globalVariablesList == null){
			globalVariablesList = getPFSParameterDAO().getGlobaVariables();
		}
		logger.debug("Leaving");
		return globalVariablesList;
	}
	
	/**
	 * Get the System Parameter Value 
	 * 
	 * @param ParmCode (String)
	 * 
	 * @return object
	 */
	public static Object getSystemParameterValue(String ParmCode) {
		return getSystemParameterValue(parmDetails.get(ParmCode));			
	}
	
	/**
	 * Method for get the Record Data of PFSParameter
	 * @param ParmCode
	 * @return
	 */
	public static PFSParameter getSystemParameterObject(String ParmCode) {
		return parmDetails.get(ParmCode);			
	}
	
	/**
	 * Get the System Parameter Value 
	 * 
	 * @param ParmCode (PFSParameter)
	 * 
	 * @return object
	 */
	public static Object getSystemParameterValue(PFSParameter parameter) {
		logger.debug("Entering");
		Object object = null;
		if(parameter!=null){
			String parmType = StringUtils.trimToEmpty(parameter.getSysParmType()).trim();
			String strValue = parameter.getSysParmValue();
			
			if(parmType.equalsIgnoreCase("Date")){
				object=DateUtility.getDBDate(strValue);
			}else if(parmType.equalsIgnoreCase("Double")){
				BigDecimal doubleValue = new BigDecimal(strValue);
				object = doubleValue.divide(new BigDecimal(Math.pow(10, parameter.getSysParmDec())));
			}else{
				object=strValue;
			}
		}
		logger.debug("Leaving");
		return object;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	private static PFSParameterDAO getPFSParameterDAO() {
		return pFSParameterDAO;
	}
	public void setPFSParameterDAO(PFSParameterDAO pFSParameterDAO) {
		SystemParameterDetails.pFSParameterDAO = pFSParameterDAO;
	}

}
