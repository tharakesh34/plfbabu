/**
 * 
 */
package com.pennant.backend.dao.messages;

import com.pennant.backend.model.messages.UserContactsList;


/**
 * @author s057
 *
 */
public interface UserContactsListDAO {
	void save(UserContactsList userContactsList);
	void delete(String usrID,String type) ;
	UserContactsList getUserContactsList(String toUsrId,String type);
	void update(UserContactsList userContactsList);
	
}
