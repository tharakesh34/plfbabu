package com.pennanttech.pffws;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface DealerSaopService {

	public VehicleDealer getDealer(@WebParam(name = "dealerId") long dealerId) throws ServiceException;

	public VehicleDealer createDealer(@WebParam(name = "dealer") VehicleDealer vehicleDealer) throws ServiceException;
}
