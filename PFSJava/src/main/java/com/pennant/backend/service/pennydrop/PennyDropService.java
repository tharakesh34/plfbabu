package com.pennant.backend.service.pennydrop;

import com.pennant.backend.model.pennydrop.PennyDropStatus;

public interface PennyDropService {

	void savePennyDropSts(PennyDropStatus pennyDropStatus);

	int getPennyDropCount(String accNumber, String ifsc);

	PennyDropStatus getPennyDropStatusDataByAcc(String accNumber, String ifsc);

}
