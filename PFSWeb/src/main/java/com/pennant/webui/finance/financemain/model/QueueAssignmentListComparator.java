package com.pennant.webui.finance.financemain.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.QueueAssignmentHeader;

public class QueueAssignmentListComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = -8606975433219761922L;

	public QueueAssignmentListComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		QueueAssignmentHeader data = (QueueAssignmentHeader) o1;
		QueueAssignmentHeader data2 = (QueueAssignmentHeader) o2;
		return String.valueOf(data.getUserId()).compareTo(String.valueOf(data2.getUserId()));

	}
}
