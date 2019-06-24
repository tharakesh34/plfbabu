package com.pennanttech.pennapps.service;

import java.util.List;

import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennanttech.pennapps.core.model.GlobalVariable;

/**
 * Service declaration for methods that depends on <code>PFSParameter</code>.
 * 
 */
public interface SysParamService {
	PFSParameter getParameter(String code);

	List<GlobalVariable> getGlobaVariables();

	void update(String code, String value, String tableType);

}
