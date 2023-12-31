package com.pennanttech.pffws;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface LimitSoapService {

	@WebResult(name = "limitStructure")
	public LimitStructure getCustomerLimitStructure(@WebParam(name = "structureCode") String structureCode)
			throws ServiceException;

	@WebResult(name = "limitSetup")
	public LimitHeader getLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@WebResult(name = "limitSetup")
	public LimitHeader createLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@WebResult(name = "limitSetup")
	public WSReturnStatus updateLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader)
			throws ServiceException;

	@WebResult(name = "limitSetup")
	public WSReturnStatus reserveLimit(@WebParam(name = "limitSetup") LimitTransactionDetail limitTransDetail)
			throws ServiceException;

	@WebResult(name = "limitSetup")
	public WSReturnStatus cancelLimitReserve(@WebParam(name = "limitSetup") LimitTransactionDetail limitTransDetail)
			throws ServiceException;

	@WebResult(name = "limitSetup")
	public WSReturnStatus blockLimit(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@WebResult(name = "limitSetup")
	public WSReturnStatus unBlockLimit(@WebParam(name = "limitSetup") LimitHeader limitHeader) throws ServiceException;

	@WebResult(name = "limitSetup")
	public LimitHeader getInstitutionLimitSetup(@WebParam(name = "limitSetup") LimitHeader limitHeader)
			throws ServiceException;
}
