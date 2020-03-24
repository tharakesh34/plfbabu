package com.pennanttech.pennapps.service;

import java.util.List;

import com.pennanttech.pennapps.core.model.GlobalVariable;

/**
 * Service declaration for methods that depends on <b>PFSParameter</b>.<br>
 * 
 */
public interface GlobalVariableService {
	
	List<GlobalVariable> getGlobalVariables();
}
