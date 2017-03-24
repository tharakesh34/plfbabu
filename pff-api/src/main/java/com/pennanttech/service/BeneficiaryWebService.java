package com.pennanttech.service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.beneficiary.BeneficiaryDetail;

public interface BeneficiaryWebService {
	
	public Beneficiary getBeneficiary(long beneficiaryId) throws ServiceException;

	public Beneficiary createBeneficiary(Beneficiary beneficiary) throws ServiceException,PFFInterfaceException;

	public WSReturnStatus updateBeneficiary(Beneficiary beneficiary) throws ServiceException;

	public WSReturnStatus deleteBeneficiary(long beneficiaryId) throws ServiceException;

	public BeneficiaryDetail getBeneficiaries(String cif) throws ServiceException;
}
