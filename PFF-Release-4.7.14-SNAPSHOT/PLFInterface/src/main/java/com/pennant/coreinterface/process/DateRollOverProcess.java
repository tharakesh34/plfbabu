package com.pennant.coreinterface.process;

import java.util.Map;

import com.pennanttech.pennapps.core.InterfaceException;

public interface DateRollOverProcess {

	Map<String, String> getCalendarWorkingDays() throws InterfaceException;

}
