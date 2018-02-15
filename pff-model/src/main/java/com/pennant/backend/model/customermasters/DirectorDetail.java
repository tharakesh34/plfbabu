/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  DirectorDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>DirectorDetail table</b>.<br>
 *
 */
public class DirectorDetail extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = -7532246118876551387L;
	
	private long directorId = Long.MIN_VALUE;
	private long custID;
	private String firstName;
	private String middleName;
	private String lastName;
	private String shortName;
	private String custGenderCode;
	private String lovDescCustGenderCodeName;
	private String custSalutationCode;
	private String lovDescCustSalutationCodeName;
	private String custAddrHNbr;
	private String custFlatNbr;
	private String custAddrStreet;
	private String custAddrLine1;
	private String custAddrLine2;
	private String custPOBox;
	private String custAddrCity;
	private String lovDescCustAddrCityName;
	private String custAddrProvince;
	private String lovDescCustAddrProvinceName;
	private String custAddrCountry;
	private String lovDescCustAddrCountryName;
	private String custAddrZIP;
	private String custAddrPhone;
	private Date custAddrFrom;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private boolean shareholder = false;
	private boolean director = false;
	private String designation;
	private String lovDescDesignationName;
	private String idType;
	private String idReference;
	private String nationality;
	private String lovDescNationalityName;
	private String lovDescCustDocCategoryName;
	private Date dob;
	private boolean idReferenceMand = false;
	
	private boolean newRecord=false;
	private String lovValue;
	private DirectorDetail befImage;
	private LoggedInUser userDetails;

	private BigDecimal sharePerc;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public DirectorDetail() {
		super();
	}

	public DirectorDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("idReferenceMand");
		return excludeFields;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return directorId;
	}
	public void setId (long id) {
		this.directorId = id;
	}
	
	public long getDirectorId() {
		return directorId;
	}
	public void setDirectorId(long directorId) {
		this.directorId = directorId;
	}
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getCustGenderCode() {
		return custGenderCode;
	}
	public void setCustGenderCode(String custGenderCode) {
		this.custGenderCode = custGenderCode;
	}

	public String getLovDescCustGenderCodeName() {
		return this.lovDescCustGenderCodeName;
	}
	public void setLovDescCustGenderCodeName (String lovDescCustGenderCodeName) {
		this.lovDescCustGenderCodeName = lovDescCustGenderCodeName;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}
	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public String getLovDescCustSalutationCodeName() {
		return this.lovDescCustSalutationCodeName;
	}
	public void setLovDescCustSalutationCodeName (String lovDescCustSalutationCodeName) {
		this.lovDescCustSalutationCodeName = lovDescCustSalutationCodeName;
	}
	
	public String getCustAddrHNbr() {
		return custAddrHNbr;
	}
	public void setCustAddrHNbr(String custAddrHNbr) {
		this.custAddrHNbr = custAddrHNbr;
	}
	
	public String getCustFlatNbr() {
		return custFlatNbr;
	}
	public void setCustFlatNbr(String custFlatNbr) {
		this.custFlatNbr = custFlatNbr;
	}
	
	public String getCustAddrStreet() {
		return custAddrStreet;
	}
	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}
	
	public String getCustAddrLine1() {
		return custAddrLine1;
	}
	public void setCustAddrLine1(String custAddrLine1) {
		this.custAddrLine1 = custAddrLine1;
	}
	
	public String getCustAddrLine2() {
		return custAddrLine2;
	}
	public void setCustAddrLine2(String custAddrLine2) {
		this.custAddrLine2 = custAddrLine2;
	}
	
	public String getCustPOBox() {
		return custPOBox;
	}
	public void setCustPOBox(String custPOBox) {
		this.custPOBox = custPOBox;
	}
	
	public String getCustAddrCity() {
		return custAddrCity;
	}
	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getLovDescCustAddrCityName() {
		return this.lovDescCustAddrCityName;
	}
	public void setLovDescCustAddrCityName (String lovDescCustAddrCityName) {
		this.lovDescCustAddrCityName = lovDescCustAddrCityName;
	}
	
	public String getCustAddrProvince() {
		return custAddrProvince;
	}
	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getLovDescCustAddrProvinceName() {
		return this.lovDescCustAddrProvinceName;
	}
	public void setLovDescCustAddrProvinceName (String lovDescCustAddrProvinceName) {
		this.lovDescCustAddrProvinceName = lovDescCustAddrProvinceName;
	}
	
	public String getCustAddrCountry() {
		return custAddrCountry;
	}
	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getLovDescCustAddrCountryName() {
		return this.lovDescCustAddrCountryName;
	}
	public void setLovDescCustAddrCountryName (String lovDescCustAddrCountryName) {
		this.lovDescCustAddrCountryName = lovDescCustAddrCountryName;
	}
	
	public String getCustAddrZIP() {
		return custAddrZIP;
	}
	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}
	
	public String getCustAddrPhone() {
		return custAddrPhone;
	}
	public void setCustAddrPhone(String custAddrPhone) {
		this.custAddrPhone = custAddrPhone;
	}
	
	public Date getCustAddrFrom() {
		return custAddrFrom;
	}
	public void setCustAddrFrom(Date custAddrFrom) {
		this.custAddrFrom = custAddrFrom;
	}
	
	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}
	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public DirectorDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(DirectorDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	
	public void setLoginDetails(LoggedInUser userDetails){
		setLastMntBy(userDetails.getUserId());
		this.userDetails=userDetails;
		
	}

	public BigDecimal getSharePerc() {
    	return sharePerc;
    }

	public void setSharePerc(BigDecimal sharePerc) {
    	this.sharePerc = sharePerc;
    }

	public boolean isShareholder() {
	    return shareholder;
    }

	public void setShareholder(boolean shareholder) {
	    this.shareholder = shareholder;
    }

	public boolean isDirector() {
	    return director;
    }

	public void setDirector(boolean director) {
	    this.director = director;
    }

	public String getDesignation() {
	    return designation;
    }

	public void setDesignation(String designation) {
	    this.designation = designation;
    }

	public String getLovDescDesignationName() {
	    return lovDescDesignationName;
    }

	public void setLovDescDesignationName(String lovDescDesignationName) {
	    this.lovDescDesignationName = lovDescDesignationName;
    }

	public String getIdType() {
	    return idType;
    }

	public void setIdType(String idType) {
	    this.idType = idType;
    }

	public String getIdReference() {
	    return idReference;
    }

	public void setIdReference(String idReference) {
	    this.idReference = idReference;
    }

	public String getNationality() {
	    return nationality;
    }

	public void setNationality(String nationality) {
	    this.nationality = nationality;
    }

	public String getLovDescNationalityName() {
	    return lovDescNationalityName;
    }

	public void setLovDescNationalityName(String lovDescNationalityName) {
	    this.lovDescNationalityName = lovDescNationalityName;
    }

	public String getLovDescCustDocCategoryName() {
    	return lovDescCustDocCategoryName;
    }

	public void setLovDescCustDocCategoryName(String lovDescCustDocCategoryName) {
    	this.lovDescCustDocCategoryName = lovDescCustDocCategoryName;
    }

	public Date getDob() {
	    return dob;
    }

	public void setDob(Date dob) {
	    this.dob = dob;
    }

	public boolean isIdReferenceMand() {
		return idReferenceMand;
	}
	public void setIdReferenceMand(boolean idReferenceMand) {
		this.idReferenceMand = idReferenceMand;
	}
	
}
