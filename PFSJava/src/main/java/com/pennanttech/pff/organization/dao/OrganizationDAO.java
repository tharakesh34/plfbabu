package com.pennanttech.pff.organization.dao;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.organization.model.Organization;

public interface OrganizationDAO  {

	Organization getOrganization(long id, String type);
	
	long save(Organization organization, TableType tableType);
	
	void update(Organization organization, TableType tableType);
	
	void delete(Organization organization, TableType tableType);

	boolean isDuplicateKey(Long custId, String code, TableType tableType);

}
