package pl.tigersoft.intellij.faker.actions;

import com.github.javafaker.Faker;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javassist.Modifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tigersoft.intellij.faker.FakerNotifier;

public class FakerActionGroup extends ActionGroup {

	private static final Logger log = Logger.getInstance(FakerActionGroup.class);

	private static final List<String> excludedClasses = Arrays.asList("Options", "Number", "CreditCardType", "Faker");

	private static final Comparator<Method> methodComparator = (method1, method2) -> method1.getName()
		.compareToIgnoreCase(method2.getName());

	@NotNull
	@Override
	public AnAction[] getChildren(@Nullable AnActionEvent event) {
		Faker faker = Faker.instance();
		return Arrays.stream(faker.getClass().getDeclaredMethods())
			.filter(categoryFilter())
			.sorted(methodComparator)
			.map(method -> buildActionGroup(method, faker))
			.toArray(AnAction[]::new);
	}

	@NotNull
	private ActionGroup buildActionGroup(Method method, Faker faker) {
		return new ActionGroup(prettyName(method.getName()), true) {

			@NotNull
			@Override
			public AnAction[] getChildren(@Nullable AnActionEvent event) {
				return invokeMethod(method, faker)
					.map(object -> Arrays.stream(object.getClass().getDeclaredMethods())
						.filter(subcategoryFilter())
						.sorted(methodComparator)
						.map(method -> getAction(method, object))
						.toArray(AnAction[]::new))
					.orElse(new AnAction[0]);
			}
		};
	}

	@NotNull
	private AnAction getAction(Method method, Object object) {
		return new AnAction(prettyName(method.getName())) {

			@Override
			public void actionPerformed(@NotNull AnActionEvent event) {
				invokeMethod(method, object).ifPresent(fake -> insertFake(event, fake));
			}

			@Override
			public void update(@NotNull final AnActionEvent event) {
				final Project project = event.getProject();
				final Editor editor = event.getData(CommonDataKeys.EDITOR);

				event.getPresentation().setEnabledAndVisible(project != null && editor != null);
			}
		};
	}

	private void insertFake(AnActionEvent event, Object fake) {
		String text = String.valueOf(fake);
		final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
		final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
		final Document document = editor.getDocument();

		Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
		int start = primaryCaret.getSelectionStart();
		int end = primaryCaret.getSelectionEnd();

		WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, text));
		primaryCaret.moveToOffset(start + text.length());
		primaryCaret.removeSelection();
	}

	private static Optional<Object> invokeMethod(Method method, Object object) {
		if (method == null || object == null) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(method.invoke(object));
		} catch (IllegalAccessException | InvocationTargetException e) {
			String msg = String.format("Unable to invoke method '%s()' on object '%s'",
				method.getName(), object.getClass().getSimpleName());
			log.error(msg);
			FakerNotifier.error(msg);
		}
		return Optional.empty();
	}

	private static String prettyName(String name) {
		return StringUtils
			.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), StringUtils.SPACE));
	}

	private static Predicate<Method> categoryFilter() {
		return subcategoryFilter()
			.and(method -> !excludedClasses.contains(method.getReturnType().getSimpleName()));
	}

	private static Predicate<Method> subcategoryFilter() {
		return method -> method.getParameters().length == 0
			&& Modifier.isPublic(method.getModifiers());
	}

}
