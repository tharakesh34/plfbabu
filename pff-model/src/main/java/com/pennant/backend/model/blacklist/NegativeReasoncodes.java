package com.pennant.backend.model.blacklist;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class NegativeReasoncodes extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String blackListCIF;
	private Long reasonId;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private NegativeReasoncodes befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public NegativeReasoncodes() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("reasonCode");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBlackListCIF() {
		return blackListCIF;
	}

	public void setBlackListCIF(String blackListCIF) {
		this.blackListCIF = blackListCIF;
	}

	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public NegativeReasoncodes getBefImage() {
		return this.befImage;
	}

	public void setBefImage(NegativeReasoncodes beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
