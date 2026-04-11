package pl.tigersoft.intellij.faker.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FakerSettingsTest {

    @Test
    void defaultLocaleIsEnglish() {
        FakerSettings.State state = new FakerSettings.State();
        assertThat(state.locale).isEqualTo("en");
    }

    @ParameterizedTest
    @CsvSource({
            "en, en",
            "pl, pl",
            "zh-CN, zh-CN",
            "pt-BR, pt-BR",
            "en-US, en-US",
            "de-AT, de-AT",
    })
    void getLocaleConvertsCodeToLocale(String code, String expectedTag) {
        FakerSettings settings = new FakerSettings();
        settings.getState().locale = code;

        Locale locale = settings.getLocale();

        assertThat(locale).isEqualTo(Locale.forLanguageTag(expectedTag));
    }

    @Test
    void loadStatePersistsLocale() {
        FakerSettings settings = new FakerSettings();
        FakerSettings.State newState = new FakerSettings.State();
        newState.locale = "ja";

        settings.loadState(newState);

        assertThat(settings.getState().locale).isEqualTo("ja");
        assertThat(settings.getLocale()).isEqualTo(Locale.JAPANESE);
    }
}