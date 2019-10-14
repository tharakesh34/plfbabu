package com.pennant.backend.service.pennydrop;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.pennydrop.PennyDropDAO;
import com.pennant.backend.model.pennydrop.PennyDropStatus;
import com.pennant.backend.service.GenericService;

public class PennyDropServiceImpl extends GenericService<PennyDropStatus> implements PennyDropService {
	private static final Logger logger = Logger.getLogger(PennyDropServiceImpl.class);

	private PennyDropDAO pennyDropDAO;

	@Override
	public void savePennyDropSts(PennyDropStatus pennyDropStatus) {
		getPennyDropDAO().savePennyDropSts(pennyDropStatus);

	}

	@Override
	public int getPennyDropCount(String accNumber, String ifsc) {
		return getPennyDropDAO().getPennyDropCount(accNumber, ifsc);
	}

	@Override
	public PennyDropStatus getPennyDropStatusDataByAcc(String accNumber, String ifsc) {
		return getPennyDropDAO().getPennyDropStatusByAcc(accNumber, ifsc);
	}

	public PennyDropDAO getPennyDropDAO() {
		return pennyDropDAO;
	}

	public void setPennyDropDAO(PennyDropDAO pennyDropDAO) {
		this.pennyDropDAO = pennyDropDAO;
	}
}
