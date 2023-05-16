package com.pennant.backend.dao.liendetails;

import java.util.List;

import com.pennanttech.model.lien.LienDetails;

public interface LienDetailsDAO {

	void save(LienDetails lien);

	void update(LienDetails lien);

	void delete(long lienId, String finreference);

	LienDetails getLienById(String finreference);

	int getCountReference(String AccNumber);

	List<LienDetails> getLienDtlsByRefAndAcc(String finreference, String AccNumber);

	List<LienDetails> getLienListByLienId(Long lienId);

	LienDetails getLienByHeaderId(Long id);
}
