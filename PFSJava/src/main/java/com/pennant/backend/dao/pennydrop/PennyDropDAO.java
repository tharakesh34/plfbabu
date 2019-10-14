package com.pennant.backend.dao.pennydrop;

import com.pennant.backend.model.pennydrop.PennyDropStatus;

public interface PennyDropDAO {
	void savePennyDropSts(PennyDropStatus pennyDropStatus);

	int getPennyDropCount(String accNumber, String ifsc);

	PennyDropStatus getPennyDropStatusByAcc(String accNum, String ifsc);
}
