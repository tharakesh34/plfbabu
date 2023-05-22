package com.pennant.backend.dao.lienheader;

import java.util.List;

import com.pennanttech.model.lien.LienHeader;

public interface LienHeaderDAO {

	long save(LienHeader lien);

	void update(LienHeader lien);

	void delete(long lienId, String finreference);

	LienHeader getLienByAcc(String accnumber);

	int getCountReference(String accNumber);

	LienHeader getLienByReference(String finreference, String accNum);

	List<LienHeader> getLienHeaderList(String finReference);

	LienHeader getLienByAccAndStatus(String accnumber, Boolean isActive);

}
