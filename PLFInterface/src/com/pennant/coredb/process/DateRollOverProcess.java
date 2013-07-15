package com.pennant.coredb.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.coredb.dao.CoreDBDAO;
import com.pennant.coreinterface.exception.EquationInterfaceException;

public class DateRollOverProcess {
	
	private static Logger logger = Logger.getLogger(DateRollOverProcess.class);
	
	private CoreDBDAO coreDBDao;
	
	/**
	 * Method for Changing Calendar Days from CoreBanking
	 * @return
	 * @throws EquationInterfaceException 
	 */
	public Map<String, String> getCalendarWorkingDays() throws EquationInterfaceException{
		logger.debug("Entering");
	
		Map<String, String> calendarDaysMap = new HashMap<String, String>() ;
	
		try {
			
			calendarDaysMap = getCoreDBDao().getCalendarWorkingDays();
			/*Map<String, Object> map = getCoreDBDao().getCalendarWorkingDays();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				calendarDaysMap.put(entry.getKey(), entry.getValue().toString());
			}*/
	
	
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

	public CoreDBDAO getCoreDBDao() {
		return coreDBDao;
	}

	public void setCoreDBDao(CoreDBDAO coreDBDao) {
		this.coreDBDao = coreDBDao;
	}
}
