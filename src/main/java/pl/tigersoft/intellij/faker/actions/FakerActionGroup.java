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
import java.util.List;

import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tigersoft.intellij.faker.actions.utils.MethodWrapper;
import pl.tigersoft.intellij.faker.actions.utils.ReflectionUtils;
import pl.tigersoft.intellij.faker.settings.FakerSettings;

public class FakerActionGroup extends ActionGroup {

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent eventIgnore) {
        Faker faker = new Faker(FakerSettings.getInstance().getLocale());
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
                    new UiUpdater(event).insertFakeTextAtAllCarets(wrapper, object);
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

            void insertFakeTextAtAllCarets(MethodWrapper wrapper, Object object) {
                final Editor editor = event.getData(CommonDataKeys.EDITOR);
                final Project project = event.getData(CommonDataKeys.PROJECT);
                if (editor == null || project == null) return;

                final Document document = editor.getDocument();
                List<Caret> carets = editor.getCaretModel().getAllCarets();

                WriteCommandAction.runWriteCommandAction(project, () -> {
                    // Process carets in reverse order to preserve offsets
                    for (int i = carets.size() - 1; i >= 0; i--) {
                        Caret caret = carets.get(i);
                        String text = wrapper.invokeMethodOnEvent(object, event)
                                .map(String::valueOf)
                                .orElse("");
                        if (text.isEmpty()) continue;

                        int start = caret.getSelectionStart();
                        int end = caret.getSelectionEnd();
                        document.replaceString(start, end, text);
                        caret.moveToOffset(start + text.length());
                        caret.removeSelection();
                    }
                });
            }
        }
    }

}