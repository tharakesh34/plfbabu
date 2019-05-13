package com.pennanttech.pennapps.service;

import java.util.List;

import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.model.SysParam;

/**
 * Service declaration for methods that depends on <code>SysParam</code>.
 * 
 */
public interface SysParamService {
	SysParam getParameter(String code);

	List<GlobalVariable> getGlobaVariables();

}
