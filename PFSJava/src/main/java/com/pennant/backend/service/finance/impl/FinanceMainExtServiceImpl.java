package com.pennant.backend.service.finance.impl;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.finance.FinanceMainExtDAO;
import com.pennant.backend.model.finance.FinanceMainExt;
import com.pennant.backend.service.finance.FinanceMainExtService;

public class FinanceMainExtServiceImpl implements FinanceMainExtService {

	private static Logger logger = Logger.getLogger(FinanceMainExtServiceImpl.class);

	public FinanceMainExtServiceImpl() {
		super();
	}

	private FinanceMainExtDAO financeMainExtDAO;

	@Override
	public void saveFinanceMainExtDetails(FinanceMainExt financeMainExt) {
		logger.debug("Entering");
		
		FinanceMainExt aFinanceMainExt = getFinanceMainExtDAO().getFinanceMainExtByRef(financeMainExt.getFinReference());
		if(aFinanceMainExt == null) {
			getFinanceMainExtDAO().save(financeMainExt);
		} else {
			getFinanceMainExtDAO().update(financeMainExt);
		}
		
		logger.debug("Leaving");

	}

	/**
	 * Method for fetch NSTL Account number
	 * 
	 * @param finReference
	 * 
	 */
	@Override
    public FinanceMainExt getNstlAccNumber(String finReference, boolean processFlag) {
		FinanceMainExt financeMainExt = getFinanceMainExtDAO().getNstlAccNumber(finReference, processFlag);
		return financeMainExt;
    }
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainExtDAO getFinanceMainExtDAO() {
		return financeMainExtDAO;
	}

	public void setFinanceMainExtDAO(FinanceMainExtDAO financeMainExtDAO) {
		this.financeMainExtDAO = financeMainExtDAO;
	}

	@Override
	public FinanceMainExt getFinanceMainExtByRef(String finReference) {
		
		return getFinanceMainExtDAO().getFinanceMainExtByRef(finReference);
	}
}
