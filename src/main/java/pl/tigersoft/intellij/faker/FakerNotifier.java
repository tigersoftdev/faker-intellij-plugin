package pl.tigersoft.intellij.faker;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;

public class FakerNotifier {

	private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("Faker Events",
		NotificationDisplayType.BALLOON, true);

	public static Notification info(String content) {
		return notify(content, NotificationType.INFORMATION);
	}

	public static Notification warn(String content) {
		return notify(content, NotificationType.WARNING);
	}

	public static Notification error(String content) {
		return notify(content, NotificationType.ERROR);
	}

	public static Notification notify(String content, NotificationType notificationType) {
		final Notification notification = NOTIFICATION_GROUP.createNotification("Faker", null, content, notificationType);
		notification.notify(null);
		return notification;
	}

}
