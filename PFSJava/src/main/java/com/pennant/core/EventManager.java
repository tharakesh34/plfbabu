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

import com.pennant.app.util.SessionUtil;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.messages.MessagesService;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class EventManager implements ServletContextListener {
	public static final String QUEUE_NAME = "AppNotificationQueue";
	private static ServletContext servletContext;
	private SecurityUserOperationsService securityUserOperationsService;
	private MessagesService messagesService;
	private static final String DEFAULT_FROM = "SYSTEM";

	public enum Notify {
		USER, ROLE
	}

	public EventManager() {
		super();
	}

	/**
	 * Publishes the message on behalf of the sender
	 * 
	 * @param message The message that need to be published and will be formatted to include sender information
	 * @param from    The login name of the sender, if not provided defaulted to "SYSTEM"
	 * @param notify  Type of recipients
	 * @param to      List of recipients in the distribution list
	 *                <ul>
	 *                <li>Notify.USER: Login user names of the users
	 *                <li>Notify.ROLE: Codes of the roles
	 *                </ul>
	 */
	public void publish(String message, String from, Notify notify, String[] to) {
		if (to.length == 0) {
			throw new AppException("There must be at least one user or role in the distribution list.");
		}

		if (StringUtils.isEmpty(from)) {
			from = DEFAULT_FROM;
		}

		// Prepare the message text
		StringBuilder messageText = new StringBuilder();
		messageText.append("From:\t");
		messageText.append(from);
		messageText.append("\nSent:\t");
		messageText.append(DateUtil.getSysDate(DateFormat.LONG_DATE_TIME));
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

		if (recipients == null) {
			recipients = new String[0];
		}

		for (String recipient : recipients) {
			boolean online = activeUsers.get(recipient) == null ? false : activeUsers.get(recipient);

			if (!online) {
				OfflineUsersMessagesBackup offlineMessage = new OfflineUsersMessagesBackup();
				offlineMessage.setFromUsrID(from);
				offlineMessage.setToUsrID(recipient);
				offlineMessage.setSendTime(DateUtil.getSysDate());
				offlineMessage.setMessage(messageText.toString());

				offlineMessages.add(offlineMessage);
			}
		}

		if (!offlineMessages.isEmpty()) {
			messagesService.saveOfflineUsersMessages(offlineMessages);
		}
	}

	/**
	 * Publishes the message on behalf of the System.
	 * 
	 * @param message The message that need to be published and will be formatted to include sender information.
	 * @param notify  Type of recipients.
	 * @param to      List of recipients in the distribution list.
	 *                <ul>
	 *                <li>Notify.USER: Login user names of the users
	 *                <li>Notify.ROLE: Codes of the roles
	 *                </ul>
	 */
	public void publish(String message, Notify notify, String[] to) {
		publish(message, DEFAULT_FROM, notify, to);
	}

	/**
	 * Publishes the message on behalf of the System.
	 * 
	 * @param message The message that need to be published and will be formatted to include sender information.
	 * @param notify  Type of recipients.
	 * @param to      List of recipients in the distribution list.
	 *                <ul>
	 *                <li>Notify.USER: Login user names of the users
	 *                <li>Notify.ROLE: Codes of the roles
	 *                </ul>
	 */
	public void publish(String message, Notify notify, String from, String[] to) {
		publish(message, from, notify, to);
	}

	/**
	 * Publishes the message on behalf of the System.
	 * 
	 * @param message  The message that need to be published and will be formatted to include sender information.
	 * @param toRoles  List of role codes of the recipients.
	 * @param division Division of the recipients.
	 * @param branch   Branch of the recipients.
	 */
	public void publish(String message, String[] toRoles, String division, String branch) {
		List<String> userLogins = securityUserOperationsService.getUsersByRoles(toRoles, division, branch);

		publish(message, DEFAULT_FROM, Notify.USER, userLogins.toArray(new String[userLogins.size()]));
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
	 * @param data  Event data
	 * @param user  User name
	 * @param roles Roles of the user
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
