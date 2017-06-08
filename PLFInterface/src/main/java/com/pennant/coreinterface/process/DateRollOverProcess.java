package com.pennant.coreinterface.process;

import java.util.Map;

import com.pennanttech.pff.core.InterfaceException;

public interface DateRollOverProcess {

	Map<String, String> getCalendarWorkingDays() throws InterfaceException;

}
