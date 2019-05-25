package com.pennanttech.pffws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface RemarksSoapService {

	WSReturnStatus addRemarks(@WebParam(name = "remarks") List<Notes> remarks) throws ServiceException;

}
