package pl.tigersoft.intellij.faker.settings;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FakerConfigurableTest {

    @Test
    void supportedLocalesIsNotEmpty() {
        assertThat(FakerConfigurable.SUPPORTED_LOCALES).isNotEmpty();
    }

    @Test
    void allLocaleCodesAreUnique() {
        List<String> codes = Arrays.stream(FakerConfigurable.SUPPORTED_LOCALES)
                .map(item -> item.code)
                .toList();

        Set<String> uniqueCodes = new HashSet<>(codes);
        assertThat(codes).hasSameSizeAs(uniqueCodes);
    }

    @Test
    void allLocaleDisplayNamesAreNonEmpty() {
        for (FakerConfigurable.LocaleItem item : FakerConfigurable.SUPPORTED_LOCALES) {
            assertThat(item.displayName).isNotBlank();
            assertThat(item.code).isNotBlank();
        }
    }

    @Test
    void englishLocaleIsPresent() {
        boolean hasEnglish = Arrays.stream(FakerConfigurable.SUPPORTED_LOCALES)
                .anyMatch(item -> "en".equals(item.code));
        assertThat(hasEnglish).isTrue();
    }

    @Test
    void allSupportedLocalesCreateValidFaker() {
        for (FakerConfigurable.LocaleItem item : FakerConfigurable.SUPPORTED_LOCALES) {
            Locale locale = Locale.forLanguageTag(item.code.replace("_", "-"));
            Faker faker = new Faker(locale);
            assertThat(faker.name().fullName()).isNotBlank();
        }
    }

    @Test
    void localeItemToStringContainsCodeAndDisplayName() {
        FakerConfigurable.LocaleItem item = new FakerConfigurable.LocaleItem("pl", "Polish");
        assertThat(item.toString()).isEqualTo("Polish (pl)");
    }

    @Test
    void displayNameReturnsCorrectValue() {
        FakerConfigurable configurable = new FakerConfigurable();
        assertThat(configurable.getDisplayName()).isEqualTo("Faker");
    }
}