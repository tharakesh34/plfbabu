package com.pennanttech.pffws;

import java.util.List;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.remark.RemarksResponse;

@WebService
public interface RemarksSoapService {

	WSReturnStatus addRemarks(@WebParam(name = "remarks") List<Notes> remarks) throws ServiceException;

	RemarksResponse getRemarks(@WebParam(name = "finReference") String finReference) throws ServiceException;

}
