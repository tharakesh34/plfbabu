package com.pennant.backend.model.dashboard;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;

public class DashBoard implements java.io.Serializable {

	private static final long serialVersionUID = -7389503841169660425L;
	public Map<String, DashboardPosition> dashBoardPosition;
	public Map<String, DashboardConfiguration> dashboardConfigMap;
	List<SecurityRight> userAuthorities;

	public DashBoard() {
	    super();
	}

	public Map<String, DashboardConfiguration> getDashboardConfigMap() {
		return dashboardConfigMap;
	}

	public void setDashboardConfigMap(Map<String, DashboardConfiguration> dashboardConfigMap) {
		this.dashboardConfigMap = dashboardConfigMap;
	}

	public void setDashBoardPosition(Map<String, DashboardPosition> dashBoardPosition) {
		this.dashBoardPosition = dashBoardPosition;
	}

	public Map<String, DashboardPosition> getDashBoardPosition() {
		return dashBoardPosition;
	}

}
