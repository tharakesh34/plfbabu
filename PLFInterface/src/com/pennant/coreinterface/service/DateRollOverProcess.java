package com.pennant.coreinterface.service;

import java.util.Map;

import com.pennant.coreinterface.exception.EquationInterfaceException;

public interface DateRollOverProcess {

	Map<String, String> getCalendarWorkingDays() throws EquationInterfaceException;

}
