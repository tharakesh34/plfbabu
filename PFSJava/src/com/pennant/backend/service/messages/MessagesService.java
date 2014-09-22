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
	
 List<OfflineUsersMessagesBackup> getOfflineUsersMessagesBackupByUsrId(String usrId);
 void  saveOfflineUsersMessages(List<OfflineUsersMessagesBackup> offlineMsgsList);
 void deleteOfflineUsersMessages(String usrId);
 UserContactsList getUserContactsList(String usrId,String type );
 void  saveUserContactsList(UserContactsList userContactsList);
 void deleteUserContactsList(String usrId,String type);
}
