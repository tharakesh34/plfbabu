package com.pennant.backend.service.messages.impl;

import java.util.List;

import com.pennant.backend.dao.messages.OfflineUserMessagesBackupDAO;
import com.pennant.backend.dao.messages.UserContactsListDAO;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.model.messages.UserContactsList;
import com.pennant.backend.service.messages.MessagesService;

public class MessagesServiceImpl implements MessagesService {
	private OfflineUserMessagesBackupDAO offlineUserMessagesBackupDAO;
	private UserContactsListDAO userContactsListDAO;

	public MessagesServiceImpl(){
		super();
	}

	/**
	 * 
	 */
	@Override
	public List<OfflineUsersMessagesBackup> getOfflineUsersMessagesBackupByUsrId(
			String usrId) {
		return getOfflineUserMessagesBackupDAO().getMessagesBackupByUserId(usrId);
	}
	/**
	 * 
	 */
	@Override
	public void saveOfflineUsersMessages(List<OfflineUsersMessagesBackup> offlineusrmsgBkpList) {
		getOfflineUserMessagesBackupDAO().save(offlineusrmsgBkpList);

	}
	/**
	 * 
	 */
	@Override
	public void deleteOfflineUsersMessages(
			String usrId) {
		getOfflineUserMessagesBackupDAO().delete(usrId);

	}
	/**
	 * 
	 * @param offlineUserMessagesBackupDAO
	 */

	public void setOfflineUserMessagesBackupDAO(
			OfflineUserMessagesBackupDAO offlineUserMessagesBackupDAO) {
		this.offlineUserMessagesBackupDAO = offlineUserMessagesBackupDAO;
	}
	/**
	 * 
	 * @return
	 */
	public OfflineUserMessagesBackupDAO getOfflineUserMessagesBackupDAO() {
		return offlineUserMessagesBackupDAO;
	}
	/**
	 * 
	 */
	@Override
	public UserContactsList getUserContactsList(String usrId,String type ) {
		return getUserContactsListDAO().getUserContactsList(usrId,type);
	}
	/**
	 * 
	 */
	@Override
	public void saveUserContactsList(UserContactsList userContactsList) {
		getUserContactsListDAO().save(userContactsList);

	}
	/**
	 * 
	 */
	@Override
	public void deleteUserContactsList(String usrId,String type) {
		getUserContactsListDAO().delete(usrId,type);
	}





	public UserContactsListDAO getUserContactsListDAO() {
		return userContactsListDAO;
	}
	public void setUserContactsListDAO(UserContactsListDAO userContactsListDAO) {
		this.userContactsListDAO = userContactsListDAO;
	}

}