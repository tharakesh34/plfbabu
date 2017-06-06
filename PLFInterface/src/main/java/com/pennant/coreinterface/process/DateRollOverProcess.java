package com.pennant.coreinterface.process;

import java.util.Map;

import com.pennant.exception.InterfaceException;

public interface DateRollOverProcess {

	Map<String, String> getCalendarWorkingDays() throws InterfaceException;

}
