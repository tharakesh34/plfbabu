package com.pennant.backend.service.pennydrop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.pennydrop.PennyDropDAO;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.service.GenericService;

public class PennyDropServiceImpl extends GenericService<BankAccountValidation> implements PennyDropService {
	private static final Logger logger = LogManager.getLogger(PennyDropServiceImpl.class);

	private PennyDropDAO pennyDropDAO;

	@Override
	public void savePennyDropSts(BankAccountValidation pennyDropStatus) {
		pennyDropDAO.savePennyDropSts(pennyDropStatus);
	}

	@Override
	public int getPennyDropCount(String accNumber, String ifsc) {
		return pennyDropDAO.getPennyDropCount(accNumber, ifsc);
	}

	@Override
	public BankAccountValidation getPennyDropStatusDataByAcc(String accNumber, String ifsc) {
		return pennyDropDAO.getPennyDropStatusByAcc(accNumber, ifsc);
	}

	public void setPennyDropDAO(PennyDropDAO pennyDropDAO) {
		this.pennyDropDAO = pennyDropDAO;
	}
}
