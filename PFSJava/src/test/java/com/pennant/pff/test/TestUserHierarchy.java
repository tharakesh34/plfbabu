package com.pennant.pff.test;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.administration.SecurityUserHierarchyService;

public class TestUserHierarchy {

	public static void main(String[] args) {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:user_hierarchy_test.xml");

			SecurityUserHierarchyService service = context.getBean(SecurityUserHierarchyService.class);

			// 1-Satish, 2-murthy, 3-sai

			List<ReportingManager> repotingManagers = new ArrayList<>();
			ReportingManager reportingManager = new ReportingManager();
			reportingManager.setUserId(3);
			reportingManager.setReportingTo(Long.parseLong("2"));
			repotingManagers.add(reportingManager);

			SecurityUser securityUser = new SecurityUser();
			securityUser.setUsrID(3);
			securityUser.setReportingManagersList(repotingManagers);
			service.refreshUserHierarchy(securityUser);

			//murthy
			repotingManagers = new ArrayList<>();
			reportingManager = new ReportingManager();
			reportingManager.setUserId(2);
			reportingManager.setReportingTo(Long.parseLong("1"));
			repotingManagers.add(reportingManager);

			securityUser.setUsrID(2);
			service.refreshUserHierarchy(securityUser);

			repotingManagers = new ArrayList<>();
			reportingManager = new ReportingManager();
			reportingManager.setUserId(3);
			reportingManager.setReportingTo(Long.parseLong("2"));
			repotingManagers.add(reportingManager);

			securityUser.setUsrID(3);
			service.refreshUserHierarchy(securityUser);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
