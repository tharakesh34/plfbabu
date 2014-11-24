package com.pennant.corebanking.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.service.DateRollOverProcess;

public class DateRollOverProcessImpl implements DateRollOverProcess{
	
	private static Logger logger = Logger.getLogger(DateRollOverProcessImpl.class);
	
	private InterfaceDAO interfaceDAO;
	
	/**
	 * Method for Changing Calendar Days from CoreBanking
	 * @return
	 * @throws EquationInterfaceException 
	 */
	@Override
	public Map<String, String> getCalendarWorkingDays() throws EquationInterfaceException{
		logger.debug("Entering");
		
		Map<String, String> calendarDaysMap = new HashMap<String, String>() ;
		try {
			calendarDaysMap = getInterfaceDAO().getCalendarWorkingDays();
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new EquationInterfaceException(e.getMessage());
		} 
	
		logger.debug("Leaving");
		return calendarDaysMap;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
