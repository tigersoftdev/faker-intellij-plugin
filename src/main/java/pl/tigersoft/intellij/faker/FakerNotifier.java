package pl.tigersoft.intellij.faker;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class FakerNotifier {

    public static final String NOTIFICATION_GROUP = "FakerEvents";
    private FakerNotifier() {
    }

    public static void info(Project project, String content) {
        notify(content, NotificationType.INFORMATION, project);
    }

    public static void warn(Project project, String content) {
        notify(content, NotificationType.WARNING, project);
    }

    public static void error(Project project, String content) {
        notify(content, NotificationType.ERROR, project);
    }

    public static void notify(String content, NotificationType notificationType, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(NOTIFICATION_GROUP)
                .createNotification("Faker", content, notificationType)
                .notify(project);
    }
}
