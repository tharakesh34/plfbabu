package com.pennant.corebanking.process.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.process.DateRollOverProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class DateRollOverProcessImpl implements DateRollOverProcess{
	
	private static Logger logger = Logger.getLogger(DateRollOverProcessImpl.class);
	
	private InterfaceDAO interfaceDAO;
	
	public DateRollOverProcessImpl() {
		super();
	}
	
	/**
	 * Method for Changing Calendar Days from CoreBanking
	 * @return
	 * @throws EquationInterfaceException 
	 */
	@Override
	public Map<String, String> getCalendarWorkingDays() throws InterfaceException{
		logger.debug("Entering");
		
		Map<String, String> calendarDaysMap = new HashMap<String, String>() ;
		try {
			calendarDaysMap = getInterfaceDAO().getCalendarWorkingDays();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} 
	
		logger.debug("Leaving");
		return calendarDaysMap;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
