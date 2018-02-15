package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.systemmasters.Academic;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Entities extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= -1472467289111692722L;

	private String				entityCode;
	private String				entityDesc;
	private boolean				active;
	private boolean				newRecord;
	private String				lovValue;
	private Academic			befImage;
	@XmlTransient
	private LoggedInUser		userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public Entities() {
		super();
	}

	public Entities(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		return new HashSet<String>();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return entityCode;
	}

	public void setId(String id) {
		this.entityCode = id;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public Academic getBefImage() {
		return befImage;
	}

	public void setBefImage(Academic befImage) {
		this.befImage = befImage;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
