package com.pennant.backend.service.finance.impl;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.finance.DealerResponseDAO;
import com.pennant.backend.model.finance.DealerResponse;
import com.pennant.backend.service.finance.DealerResponseService;

public class DealerResponseServiceImpl implements DealerResponseService {

	private static final Logger logger = Logger.getLogger(DealerResponseServiceImpl.class);

	private DealerResponseDAO dealerResponseDAO;

	public DealerResponseServiceImpl() {
		super();
	}
	
	public DealerResponseDAO getDealerResponseDAO() {
	    return dealerResponseDAO;
    }

	public void setDealerResponseDAO(DealerResponseDAO dealerResponseDAO) {
	    this.dealerResponseDAO = dealerResponseDAO;
    }

	@Override
    public void save(DealerResponse dealerResponse) {
		logger.debug(" Entering ");
		getDealerResponseDAO().save(dealerResponse, "");
	    logger.debug(" Leaving ");
	    
    }

	@Override
    public int getCountByProcessed(String finReference, boolean processed) {
	    return getDealerResponseDAO().getCountByProcessed(finReference, processed, "");
    }

}
