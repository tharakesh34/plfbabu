package com.pennant.backend.dao.applicationmaster;


import com.pennant.backend.model.MMAgreement.MMAgreement;


public interface MMAgreementDAO {

	MMAgreement getMMAgreement();
	MMAgreement getNewMMAgreement();
	void update(MMAgreement aMMAgreement,String type);
	void delete(MMAgreement aMMAgreement,String type);
	long save(MMAgreement aMMAgreement,String type);
	MMAgreement getMMAgreementById(long id, String string);
	MMAgreement getMMAgreementByMMARef( String mMAReferance, String type);
}
