package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface RelationshipOfficersRestService {

	@POST
	@Path("/relationshipOfficerService/createRelationshipOfficer")
	public RelationshipOfficer createRelationshipOfficer(RelationshipOfficer relationshipOfficer)
			throws ServiceException;

	@POST
	@Path("/relationshipOfficerService/updateRelationshipOfficer")
	public WSReturnStatus updateRelationshipOfficer(RelationshipOfficer relationshipOfficer) throws ServiceException;
}
