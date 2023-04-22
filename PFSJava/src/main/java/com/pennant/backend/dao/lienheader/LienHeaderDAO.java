package com.pennant.backend.dao.lienheader;

import com.pennanttech.model.lien.LienHeader;

public interface LienHeaderDAO {

	long save(LienHeader lien);

	void update(LienHeader lien);

	void delete(long lienId, String finreference);

	LienHeader getLienByAcc(String accnumber);

	int getCountReference(String AccNumber);

	LienHeader getLienByReference(String finreference);

}
