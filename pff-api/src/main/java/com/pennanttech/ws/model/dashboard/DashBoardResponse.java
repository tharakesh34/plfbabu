package com.pennanttech.ws.model.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.fusioncharts.ChartSetElement;

public class DashBoardResponse {

	private DashboardConfiguration dashboardConfiguration;
	private List<ChartSetElement> chartSetElement = new ArrayList<>();
	private WSReturnStatus returnStatus;

	public DashboardConfiguration getDashboardConfiguration() {
		return dashboardConfiguration;
	}

	public void setDashboardConfiguration(DashboardConfiguration dashboardConfiguration) {
		this.dashboardConfiguration = dashboardConfiguration;
	}

	public List<ChartSetElement> getChartSetElement() {
		return chartSetElement;
	}

	public void setChartSetElement(List<ChartSetElement> chartSetElement) {
		this.chartSetElement = chartSetElement;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
