package com.pennanttech.pffws;

import jakarta.jws.WebParam;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.ws.exception.ServiceException;

public interface RelationshipOfficersSoapService {

	public RelationshipOfficer createRelationshipOfficer(
			@WebParam(name = "relationshipOfficer") RelationshipOfficer relationshipOfficer) throws ServiceException;

	public WSReturnStatus updateRelationshipOfficer(
			@WebParam(name = "relationshipOfficer") RelationshipOfficer relationshipOfficer) throws ServiceException;

}
