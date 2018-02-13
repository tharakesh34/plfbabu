package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class PresentmentReasonCode extends AbstractWorkflowEntity{

		private static final long serialVersionUID = 71023783624451043L;
		
		private String code;
		private String description;
		private boolean active;
		private boolean newRecord;
		private String lovValue;
		private PresentmentReasonCode befImage;
		private LoggedInUser userDetails;
		
		public boolean isNew() {
			return isNewRecord();
		}

		public PresentmentReasonCode() {
			super();
		}

		public PresentmentReasonCode(String id) {
			super();
			this.setId(id);
		}

		// ******************************************************//
		// ****************** getter / setter *******************//
		// ******************************************************//
		
		public String getId() {
			return code;
		}
		public void setId(String id) {
			this.code = id;
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
		public PresentmentReasonCode getBefImage() {
			return befImage;
		}
		public void setBefImage(PresentmentReasonCode befImage) {
			this.befImage = befImage;
		}
		public LoggedInUser getUserDetails() {
			return userDetails;
		}
		public void setUserDetails(LoggedInUser userDetails) {
			this.userDetails = userDetails;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		public boolean isActive() {
		    return active;
	    }

		public void setActive(boolean active) {
		    this.active = active;
	    }

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
