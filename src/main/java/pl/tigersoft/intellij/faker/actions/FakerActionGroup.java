package pl.tigersoft.intellij.faker.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.lang.reflect.Method;

import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tigersoft.intellij.faker.actions.utils.MethodWrapper;
import pl.tigersoft.intellij.faker.actions.utils.ReflectionUtils;

public class FakerActionGroup extends ActionGroup {

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent eventIgnore) {
        Faker faker = new Faker();
        return ReflectionUtils.createCategoryList(Faker.class)
                .map(method -> UiHelper.buildRootActionGroup(method, faker))
                .toArray(AnAction[]::new);
    }

    private static class UiHelper {
        @NotNull
        private static ActionGroup buildRootActionGroup(Method method, Faker faker) {
            var wrapper = new MethodWrapper(method);
            return new ActionGroup(wrapper.prettyName, true) {

                @NotNull
                @Override
                public AnAction[] getChildren(@Nullable AnActionEvent event) {
                    return wrapper.invokeMethodOnEvent(faker, event)
                            .map(subcategory -> ReflectionUtils.createSubcategoryList(subcategory.getClass())
                                    .map(method -> buildAction(method, subcategory))
                                    .toArray(AnAction[]::new))
                            .orElse(new AnAction[0]);
                }
            };
        }

        @NotNull
        private static AnAction buildAction(Method method, Object object) {
            var wrapper = new MethodWrapper(method);
            return new AnAction(wrapper.prettyName) {

                @Override
                public void actionPerformed(@NotNull AnActionEvent event) {
                    wrapper.invokeMethodOnEvent(object, event).ifPresent(
                            fakeText -> new UiUpdater(event).insertFakeText(fakeText));
                }

                @Override
                public void update(@NotNull final AnActionEvent event) {
                    event.getPresentation().setEnabledAndVisible(isVisible(event));
                }

                private boolean isVisible(AnActionEvent event) {
                    final Project project = event.getProject();
                    final Editor editor = event.getData(CommonDataKeys.EDITOR);
                    return project != null && editor != null;
                }
            };
        }

        private static class UiUpdater {
            private final AnActionEvent event;

            UiUpdater(AnActionEvent event) {
                this.event = event;
            }

            void insertFakeText(Object fakeTextInput) {
                String text = String.valueOf(fakeTextInput);
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
        }
    }

}