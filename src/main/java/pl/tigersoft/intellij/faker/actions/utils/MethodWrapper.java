package pl.tigersoft.intellij.faker.actions.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import pl.tigersoft.intellij.faker.FakerNotifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class MethodWrapper {
    private static final Logger log = Logger.getInstance(MethodWrapper.class);
    private final Method method;
    public final String prettyName;

    public MethodWrapper(Method method) {
        this.method = method;
        prettyName = createPrettyName(method.getName());
    }

    static String createPrettyName(String name) {
        return StringUtils
                .capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), StringUtils.SPACE));
    }

    public Optional<Object> invokeMethodOnEvent(Object object, AnActionEvent event) {
        return invokeMethod(method, object, event);
    }

    private static Optional<Object> invokeMethod(Method method, Object object, AnActionEvent event) {
        if (method == null || object == null) {
            return Optional.empty();
        }
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        try {
            return Optional.ofNullable(method.invoke(object));
        } catch (IllegalAccessException | InvocationTargetException e) {
            String msg = String.format("Unable to invoke method '%s()' on object '%s'",
                    method.getName(), object.getClass().getSimpleName());
            log.error(msg);
            FakerNotifier.error(project, msg);
        }
        return Optional.empty();
    }
}
