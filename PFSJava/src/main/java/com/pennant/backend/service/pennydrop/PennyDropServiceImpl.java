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
		getPennyDropDAO().savePennyDropSts(pennyDropStatus);

	}

	@Override
	public int getPennyDropCount(String accNumber, String ifsc) {
		return getPennyDropDAO().getPennyDropCount(accNumber, ifsc);
	}

	@Override
	public BankAccountValidation getPennyDropStatusDataByAcc(String accNumber, String ifsc) {
		return getPennyDropDAO().getPennyDropStatusByAcc(accNumber, ifsc);
	}

	public PennyDropDAO getPennyDropDAO() {
		return pennyDropDAO;
	}

	public void setPennyDropDAO(PennyDropDAO pennyDropDAO) {
		this.pennyDropDAO = pennyDropDAO;
	}
}
