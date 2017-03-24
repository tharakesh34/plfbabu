package com.pennant.coreinterface.process;

import java.util.Map;

import com.pennant.exception.PFFInterfaceException;

public interface DateRollOverProcess {

	Map<String, String> getCalendarWorkingDays() throws PFFInterfaceException;

}
