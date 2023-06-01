package com.pennant.backend.service.finance.globalvariable;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.service.GenericService;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.service.GlobalVariableService;

/**
 * Service implementation for methods that depends on <b>PFSParameter</b>.<br>
 * 
 */
public class GlobalVariableServiceImpl extends GenericService<List<GlobalVariable>> implements GlobalVariableService {
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
		return getCachedEntity("GlobalVariables");
	}

	@Override
	protected List<GlobalVariable> getEntity(String code) {
		if (globalVariablesList == null) {
			globalVariablesList = getPFSParameterDAO().getGlobaVariables();
		}

		if (globalVariablesList == null) {
			globalVariablesList = new ArrayList<GlobalVariable>(1);
		}
		return globalVariablesList;
	}

}
