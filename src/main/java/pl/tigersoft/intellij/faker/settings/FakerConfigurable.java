package pl.tigersoft.intellij.faker.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

public class FakerConfigurable implements Configurable {

    private ComboBox<LocaleItem> localeComboBox;

    static final LocaleItem[] SUPPORTED_LOCALES = {
            new LocaleItem("ar", "Arabic"),
            new LocaleItem("be", "Belarusian"),
            new LocaleItem("bg", "Bulgarian"),
            new LocaleItem("ca", "Catalan"),
            new LocaleItem("ca-CAT", "Catalan (Catalonia)"),
            new LocaleItem("cs", "Czech"),
            new LocaleItem("cs-CZ", "Czech (Czech Republic)"),
            new LocaleItem("da-DK", "Danish (Denmark)"),
            new LocaleItem("de", "German"),
            new LocaleItem("de-AT", "German (Austria)"),
            new LocaleItem("de-CH", "German (Switzerland)"),
            new LocaleItem("el-GR", "Greek (Greece)"),
            new LocaleItem("en", "English"),
            new LocaleItem("en-AU", "English (Australia)"),
            new LocaleItem("en-CA", "English (Canada)"),
            new LocaleItem("en-GB", "English (United Kingdom)"),
            new LocaleItem("en-IE", "English (Ireland)"),
            new LocaleItem("en-IN", "English (India)"),
            new LocaleItem("en-NG", "English (Nigeria)"),
            new LocaleItem("en-NZ", "English (New Zealand)"),
            new LocaleItem("en-PH", "English (Philippines)"),
            new LocaleItem("en-PK", "English (Pakistan)"),
            new LocaleItem("en-SG", "English (Singapore)"),
            new LocaleItem("en-UG", "English (Uganda)"),
            new LocaleItem("en-US", "English (United States)"),
            new LocaleItem("en-ZA", "English (South Africa)"),
            new LocaleItem("es", "Spanish"),
            new LocaleItem("es-AR", "Spanish (Argentina)"),
            new LocaleItem("es-MX", "Spanish (Mexico)"),
            new LocaleItem("es-PY", "Spanish (Paraguay)"),
            new LocaleItem("et", "Estonian"),
            new LocaleItem("fa", "Persian"),
            new LocaleItem("fi-FI", "Finnish (Finland)"),
            new LocaleItem("fr", "French"),
            new LocaleItem("fr-CA", "French (Canada)"),
            new LocaleItem("fr-CH", "French (Switzerland)"),
            new LocaleItem("he", "Hebrew"),
            new LocaleItem("hr", "Croatian"),
            new LocaleItem("hu", "Hungarian"),
            new LocaleItem("hy", "Armenian"),
            new LocaleItem("id", "Indonesian"),
            new LocaleItem("id-ID", "Indonesian (Indonesia)"),
            new LocaleItem("it", "Italian"),
            new LocaleItem("ja", "Japanese"),
            new LocaleItem("ka", "Georgian"),
            new LocaleItem("ko", "Korean"),
            new LocaleItem("lv", "Latvian"),
            new LocaleItem("mk", "Macedonian"),
            new LocaleItem("nb-NO", "Norwegian Bokmål (Norway)"),
            new LocaleItem("nl", "Dutch"),
            new LocaleItem("nl-BE", "Dutch (Belgium)"),
            new LocaleItem("no-NO", "Norwegian (Norway)"),
            new LocaleItem("pl", "Polish"),
            new LocaleItem("pt", "Portuguese"),
            new LocaleItem("pt-BR", "Portuguese (Brazil)"),
            new LocaleItem("ro-MD", "Romanian (Moldova)"),
            new LocaleItem("ru", "Russian"),
            new LocaleItem("ru-MD", "Russian (Moldova)"),
            new LocaleItem("sk", "Slovak"),
            new LocaleItem("sq", "Albanian"),
            new LocaleItem("sv", "Swedish"),
            new LocaleItem("sv-SE", "Swedish (Sweden)"),
            new LocaleItem("ta", "Tamil"),
            new LocaleItem("ta-IN", "Tamil (India)"),
            new LocaleItem("th", "Thai"),
            new LocaleItem("tr", "Turkish"),
            new LocaleItem("uk", "Ukrainian"),
            new LocaleItem("uz", "Uzbek"),
            new LocaleItem("vi", "Vietnamese"),
            new LocaleItem("zh-CN", "Chinese (Simplified)"),
            new LocaleItem("zh-TW", "Chinese (Traditional)"),
    };

    @Nls
    @Override
    public String getDisplayName() {
        return "Faker";
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        LocaleItem[] sorted = Arrays.copyOf(SUPPORTED_LOCALES, SUPPORTED_LOCALES.length);
        Arrays.sort(sorted, Comparator.comparing(item -> item.displayName));

        localeComboBox = new ComboBox<>(sorted);
        localeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LocaleItem item) {
                    setText(item.displayName + " (" + item.code + ")");
                }
                return this;
            }
        });

        reset();

        panel.add(new JBLabel("Locale: "));
        panel.add(localeComboBox);
        return panel;
    }

    @Override
    public boolean isModified() {
        if (localeComboBox == null) return false;
        String current = FakerSettings.getInstance().getState().locale;
        LocaleItem selected = (LocaleItem) localeComboBox.getSelectedItem();
        return selected != null && !selected.code.equals(current);
    }

    @Override
    public void apply() {
        if (localeComboBox == null) return;
        LocaleItem selected = (LocaleItem) localeComboBox.getSelectedItem();
        if (selected != null) {
            FakerSettings.getInstance().getState().locale = selected.code;
        }
    }

    @Override
    public void reset() {
        if (localeComboBox == null) return;
        String current = FakerSettings.getInstance().getState().locale;
        for (int i = 0; i < localeComboBox.getItemCount(); i++) {
            if (localeComboBox.getItemAt(i).code.equals(current)) {
                localeComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    static class LocaleItem {
        final String code;
        final String displayName;

        LocaleItem(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName + " (" + code + ")";
        }
    }
}