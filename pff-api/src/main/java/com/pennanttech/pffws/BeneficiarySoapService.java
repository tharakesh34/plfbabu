package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.beneficiary.BeneficiaryDetail;

@WebService
public interface BeneficiarySoapService {

	public Beneficiary getBeneficiary(@WebParam(name = "beneficiaryId") long beneficiaryId) throws ServiceException;

	public Beneficiary createBeneficiary(@WebParam(name = "beneficiary") Beneficiary beneficiary) throws ServiceException;

	public WSReturnStatus updateBeneficiary(@WebParam(name = "beneficiary") Beneficiary beneficiary) throws ServiceException;

	public WSReturnStatus deleteBeneficiary(@WebParam(name = "beneficiaryId") long beneficiaryId) throws ServiceException;

	public BeneficiaryDetail getBeneficiaries(@WebParam(name = "cif") String cif) throws ServiceException;

}
