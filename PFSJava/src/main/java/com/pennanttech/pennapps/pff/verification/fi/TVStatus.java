package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;

public enum TVStatus {

	SELECT(0, Labels.getLabel("Combo.Select")), POSITIVE(1, Labels.getLabel("label_POSITIVE")),
	NEGATIVE(2, Labels.getLabel("label_NEGATIVE")), REFERTOCREDIT(3, Labels.getLabel("label_REFERTOCREDIT"));

	private final Integer key;
	private final String value;

	private TVStatus(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static TVStatus getType(Integer key) {
		for (TVStatus type : values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		String tvReqCodes = SysParamUtil.getValueAsString(PennantConstants.REQ_TV_STATUS_CODES);
		for (TVStatus status : values()) {
			if (tvReqCodes != null && StringUtils.contains(tvReqCodes, String.valueOf(status.getKey()))) {
				list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
			} else {
				list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
			}
		}
		return list;
	}
}