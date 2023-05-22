package com.pennant.backend.model.limit;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LimitGroupLines extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String limitGroupCode;
	private String groupCode;
	private String groupName;
	private String limitLine;
	private String limitLineDesc;
	private String limitLines;
	private int itemSeq;

	private long createdBy;
	private Timestamp createdOn;
	private String lovValue;
	private LimitGroup befImage;
	private LoggedInUser userDetails;

	private int key;

	public LimitGroupLines() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("LimitGroup"));
	}

	public LimitGroupLines(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("limitLineDesc");
		excludeFields.add("groupName");
		excludeFields.add("key");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public String getId() {
		return limitGroupCode;
	}

	public void setId(String id) {
		this.limitGroupCode = id;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getLimitLine() {
		return limitLine;
	}

	public void setLimitLine(String limitLine) {
		this.limitLine = limitLine;
	}

	public String getLimitLineDesc() {
		return limitLineDesc;
	}

	public void setLimitLineDesc(String limitLineDesc) {
		this.limitLineDesc = limitLineDesc;
	}

	public String getLimitLines() {
		return limitLines;
	}

	public void setLimitLines(String limitLines) {
		this.limitLines = limitLines;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public XMLGregorianCalendar getCreatedDate() throws DatatypeConfigurationException {

		if (createdOn == null) {
			return null;
		}
		return DateUtil.getXMLDate(createdOn);
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public LimitGroup getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LimitGroup beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLimitGroupCode() {
		return limitGroupCode;
	}

	public void setLimitGroupCode(String limitGroupCode) {
		this.limitGroupCode = limitGroupCode;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getItemSeq() {
		return itemSeq;
	}

	public void setItemSeq(int itemSeq) {
		this.itemSeq = itemSeq;
	}
}
