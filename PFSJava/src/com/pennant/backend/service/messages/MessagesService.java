/**
 * 
 */
package com.pennant.backend.service.messages;

import java.util.List;

import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.model.messages.UserContactsList;


/**
 * @author s057
 *
 */
public interface MessagesService {
	
 public List<OfflineUsersMessagesBackup> getOfflineUsersMessagesBackupByUsrId(String usrId);
 public void  saveOfflineUsersMessages(List<OfflineUsersMessagesBackup> offlineMsgsList);
 public void deleteOfflineUsersMessages(String usrId);
 public UserContactsList getUserContactsList(String usrId,String type );
 public void  saveUserContactsList(UserContactsList userContactsList);
 public void deleteUserContactsList(String usrId,String type);
	
}
