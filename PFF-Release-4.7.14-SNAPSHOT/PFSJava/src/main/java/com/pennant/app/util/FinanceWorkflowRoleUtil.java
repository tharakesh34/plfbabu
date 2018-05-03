package com.pennant.app.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;

public class FinanceWorkflowRoleUtil {
	
	private static FinanceWorkFlowDAO financeWorkFlowDAO;
	private static final Logger logger = Logger.getLogger(FinanceWorkflowRoleUtil.class);
	
	
	private static LoadingCache<String, Set<String>> financeRoleCache = CacheBuilder.newBuilder().
			expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<String, Set<String>>() {
				@Override
				public Set<String> load(String finEvent) throws Exception {
					return getfinanceRoles(finEvent);
				}
			});
	
	
	private static Set<String> getfinanceRoles(String financeRoleKey){
		Set<String> roleSet=new HashSet<String>();
		
		String[] parmList = financeRoleKey.split("@");

		List<String> rolesList = getFinanceWorkFlowDAO().getFinanceWorkFlowRoles(parmList[0], parmList[1]);
		for (String roles : rolesList) {
			String[] arrRoles = roles.split(";");
			for (String role : arrRoles) {
				roleSet.add(role);
			}
		}
		return roleSet;
	} 

	public static Set<String> getFinanceRoles(String[] moduleNames,String finEvent){
		
		Set<String> finRolesSet = new HashSet<String>();
	
		for (String module : moduleNames) {
			String financeRoleKey = module+"@"+finEvent;
			// FIXME: Cache disabled as found issue of not getting refreshed in QC. Also observed no invalidate method usage.
//			try {
				//finRolesSet.addAll(financeRoleCache.get(financeRoleKey));
				finRolesSet.addAll(getfinanceRoles(financeRoleKey));
//			} catch (ExecutionException e) {
//				logger.warn("Unable to load data from cache: ", e);
//				finRolesSet.addAll(getfinanceRoles(financeRoleKey));
//			}
		}
		
		return finRolesSet;
	} 
	
	
	// Clear errorCache data.
		public static void clearRoleCache(String finEvent) {
			try {
				financeRoleCache.invalidate(finEvent);
			} catch (Exception ex) {
				logger.warn("Error clearing data from errorCache cache: ", ex);
			}
		}

		
	public static FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}


	public static void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		FinanceWorkflowRoleUtil.financeWorkFlowDAO = financeWorkFlowDAO;
	}


}
