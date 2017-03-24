/**
 * 
 */
package com.pennant.backend.dao.messages;

import java.util.List;

import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;


/**
 * @author s057
 *
 */
public interface OfflineUserMessagesBackupDAO {
	void save(List<OfflineUsersMessagesBackup> offlineusrmsgBkpList);
	void delete(String toUsrId);
	List<OfflineUsersMessagesBackup> getMessagesBackupByUserId(String toUsrId);
	
}
