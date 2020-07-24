package com.pennant.backend.service.finance.globalvariable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.service.GenericService;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.service.GlobalVariableService;

/**
 * Service implementation for methods that depends on <b>PFSParameter</b>.<br>
 * 
 */
public class GlobalVariableServiceImpl extends GenericService<List<GlobalVariable>> implements GlobalVariableService {
	private static final Logger logger = Logger.getLogger(SysParamUtil.class);

	private PFSParameterDAO pFSParameterDAO;
	private static List<GlobalVariable> globalVariablesList = null;

	public GlobalVariableServiceImpl() {
		super(true, "GlobalVariables");
	}

	public PFSParameterDAO getPFSParameterDAO() {
		return pFSParameterDAO;
	}

	public void setPFSParameterDAO(PFSParameterDAO pFSParameterDAO) {
		this.pFSParameterDAO = pFSParameterDAO;
	}

	/**
	 * Get the List of System Parameters
	 * 
	 * @return HashMap
	 */
	@Override
	public List<GlobalVariable> getGlobalVariables() {
		logger.debug("Entering");
		return getCachedEntity("GlobalVariables");
	}

	@Override
	protected List<GlobalVariable> getEntity(String code) {
		logger.debug("Entering");

		globalVariablesList = getPFSParameterDAO().getGlobaVariables();

		if (globalVariablesList == null) {
			globalVariablesList = new ArrayList<GlobalVariable>(1);
		}
		logger.debug("Leaving");
		return globalVariablesList;
	}

}
