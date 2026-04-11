package pl.tigersoft.intellij.faker.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@State(
        name = "pl.tigersoft.intellij.faker.settings.FakerSettings",
        storages = @Storage("FakerPlugin.xml")
)
public final class FakerSettings implements PersistentStateComponent<FakerSettings.State> {

    public static class State {
        public String locale = "en";
    }

    private State myState = new State();

    public static FakerSettings getInstance() {
        return ApplicationManager.getApplication().getService(FakerSettings.class);
    }

    @Override
    public @NotNull State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public Locale getLocale() {
        String tag = myState.locale.replace("_", "-");
        return Locale.forLanguageTag(tag);
    }
}