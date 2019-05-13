package com.pennanttech.pffws;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.beneficiary.BeneficiaryDetail;

@Produces("application/json")
public interface BeneficiaryRestService {
	@GET
	@Path("/beneficiaryService/getBeneficiary/{beneficiaryId}")
	public Beneficiary getBeneficiary(@PathParam("beneficiaryId") long beneficiaryId) throws ServiceException;

	@POST
	@Path("/beneficiaryService/createBeneficiary")
	public Beneficiary createBeneficiary(Beneficiary beneficiary) throws ServiceException;

	@POST
	@Path("/beneficiaryService/updateBeneficiary")
	public WSReturnStatus updateBeneficiary(Beneficiary beneficiary) throws ServiceException;

	@DELETE
	@Path("/beneficiaryService/deleteBeneficiary/{beneficiaryId}")
	public WSReturnStatus deleteBeneficiary(@PathParam("beneficiaryId") long beneficiaryId) throws ServiceException;

	@GET
	@Path("/beneficiaryService/getBeneficiaries/{cif}")
	public BeneficiaryDetail getBeneficiaries(@PathParam("cif") String cif) throws ServiceException;

}
