package com.pennant.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.http.WebManager;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUtil;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.messages.MessagesService;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class EventManager implements ServletContextListener {
	public static final String				QUEUE_NAME	= "AppNotificationQueue";
	private static ServletContext			servletContext;
	private SecurityUserOperationsService	securityUserOperationsService;
	private MessagesService					messagesService;

	public enum Notify {
		USER, ROLE
	}

	public EventManager() {
		super();
	}

	/**
	 * Publishes the message on behalf of the sender
	 * 
	 * @param message
	 *            The message that need to be published and will be formatted to include sender information
	 * @param from
	 *            The login name of the sender, if not provided defaulted to "SYSTEM"
	 * @param notify
	 *            Type of recipients
	 * @param to
	 *            List of recipients in the distribution list
	 *            <ul>
	 *            <li>Notify.USER: Login user names of the users
	 *            <li>Notify.ROLE: Codes of the roles
	 *            </ul>
	 * @throws Exception
	 */
	public void publish(String message, String from, Notify notify, String[] to) throws Exception {
		if (to.length == 0) {
			throw new Exception("There must be at least one user or role in the distribution list.");
		}

		if (StringUtils.isEmpty(from)) {
			from = "SYSTEM";
		}

		// Prepare the message text
		StringBuilder messageText = new StringBuilder();
		messageText.append("From:\t");
		messageText.append(from);
		messageText.append("\nSent:\t");
		messageText.append(DateUtility.getSysDate(DateFormat.LONG_DATE_TIME));
		messageText.append("\n\n");
		messageText.append(message);
		messageText.append("\n\n*******************   END   *******************\n");

		// Prepare the data for the event to be published
		Object[] data = new Object[4];
		data[0] = messageText.toString();
		data[1] = from;
		data[2] = notify;
		data[3] = to;

		// Publish the event
		EventQueue<Event> queue;

		if (Executions.getCurrent() == null) {
			queue = EventQueues.lookup(QUEUE_NAME, WebManager.getWebManager(servletContext).getWebApp(), true);
		} else {
			queue = EventQueues.lookup(QUEUE_NAME, EventQueues.APPLICATION, true);
		}

		queue.publish(new Event(QUEUE_NAME, null, data));

		// Save the message for off line users
		String[] recipients = null;

		if (notify == Notify.USER) {
			recipients = Arrays.copyOf(to, to.length);
		} else if (notify == Notify.ROLE) {
			List<String> userLogins = securityUserOperationsService.getUsersByRoles(to);

			recipients = new String[userLogins.size()];
			int i = 0;

			for (String login : userLogins) {
				recipients[i++] = login;
			}
		}

		Map<String, Boolean> activeUsers = new HashMap<>();
		List<OfflineUsersMessagesBackup> offlineMessages = new ArrayList<>();
		if (Executions.getCurrent() != null) {
			try {
				activeUsers = getActiveUsers();
			} catch (Exception e) {
				// No Current Session
				return;
			}
		}

		for (String recipient : recipients) {
			boolean online = activeUsers.get(recipient) == null ? false : activeUsers.get(recipient);

			if (!online) {
				OfflineUsersMessagesBackup offlineMessage = new OfflineUsersMessagesBackup();
				offlineMessage.setFromUsrID(from);
				offlineMessage.setToUsrID(recipient);
				offlineMessage.setSendTime(DateUtility.getSysDate());
				offlineMessage.setMessage(messageText.toString());

				offlineMessages.add(offlineMessage);
			}
		}

		if (!offlineMessages.isEmpty()) {
			messagesService.saveOfflineUsersMessages(offlineMessages);
		}
	}

	/**
	 * Publishes the message on behalf of the System
	 * 
	 * @param message
	 *            The message that need to be published and will be formatted to include sender information
	 * @param notify
	 *            Type of recipients
	 * @param to
	 *            List of recipients in the distribution list
	 *            <ul>
	 *            <li>Notify.USER: Login user names of the users
	 *            <li>Notify.ROLE: Codes of the roles
	 *            </ul>
	 * @throws Exception
	 */
	public void publish(String message, Notify notify, String[] to) throws Exception {
		publish(message, "SYSTEM", notify, to);
	}

	public void publish(String message, String[] toRoles, String division, String branch) throws Exception {
		List<String> userLogins = securityUserOperationsService.getUsersByRoles(toRoles, division, branch);

		publish(message, "SYSTEM", Notify.USER, userLogins.toArray(new String[0]));
	}

	/**
	 * Gets active, logged-in, users along with their desktop status
	 * 
	 * @return A map of active users and their desktop status
	 */
	public static Map<String, Boolean> getActiveUsers() {
		Map<String, Boolean> users = new HashMap<>();

		for (String userId : SessionUtil.getCurrentLoginUsers().keySet()) {
			users.put(userId, true);
		}

		return users;
	}

	/**
	 * Returns whether the the user is the recipient of the message
	 * 
	 * @param data
	 *            Event data
	 * @param user
	 *            User name
	 * @param roles
	 *            Roles of the user
	 * @return Whether the the user is the recipient of the message
	 */
	public static boolean isRecipient(Object[] data, String user, List<SecurityRole> roles) {
		String from = (String) data[1];
		Notify notify = (Notify) data[2];
		String[] to = (String[]) data[3];

		if (from.equals(user)) {
			return false;
		}

		if (notify == Notify.USER) {
			return contains(to, user);
		} else if (notify == Notify.ROLE) {
			if (roles != null) {
				for (SecurityRole role : roles) {
					if (contains(to, role.getRoleCd())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private static boolean contains(String[] array, String key) {
		for (String value : array) {
			if (key.equals(value)) {
				return true;
			}
		}

		return false;
	}

	public void setSecurityUserOperationsService(SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	public void setMessagesService(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		servletContext = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		servletContext = arg0.getServletContext();
	}
}
