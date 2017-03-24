package com.pennant.backend.service.dda.impl;

import com.pennant.backend.dao.dda.DDAProcessDAO;
import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.dda.DDAProcessService;

public class DDAProcessServiceImpl implements DDAProcessService {

	private DDAProcessDAO ddaProcessDAO;

	/**
	 * save the DDA request details i.e <br>
	 *  'VALIDATE' AND 'REGISTRATION'
	 * 
	 * @param ddaProcessData
	 * 
	 */
	@Override
	public long save(DDAProcessData ddaProcessData) {
		return getDdaProcessDAO().save(ddaProcessData);
	}

	/**
	 * Update the response details
	 * 
	 */
	@Override
	public void updateActiveStatus(String finrefrence) {
		getDdaProcessDAO().updateActiveStatus(finrefrence);
	}

	/**
	 * Method for Fetch DDA Validation request details by id
	 * 
	 * @param finReference
	 * @param reqTypeValidate
	 * 
	 * @return {@link DDAProcessData}
	 */
	@Override
    public DDAProcessData getDDADetailsById(String finReference, String reqTypeValidate) {
	    return getDdaProcessDAO().getDDADetailsById(finReference, reqTypeValidate);
    }

	@Override
    public FinanceType getFinTypeDetails(String finType) {
		return getDdaProcessDAO().getFinTypeDetails(finType);
    }
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DDAProcessDAO getDdaProcessDAO() {
		return ddaProcessDAO;
	}

	public void setDdaProcessDAO(DDAProcessDAO ddaProcessDAO) {
		this.ddaProcessDAO = ddaProcessDAO;
	}

	@Override
	public DDAProcessData getDDADetailsByReference(String finReference,
			String reqTypeValidate) {
		return getDdaProcessDAO().getDDADetailsByReference(finReference,reqTypeValidate);
	}

}
