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
	public void save(UserContactsList userContactsList);
	public void delete(String usrID,String type) ;
	public UserContactsList getUserContactsList(String toUsrId,String type);
	void update(UserContactsList userContactsList);
	
}
