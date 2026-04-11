package pl.tigersoft.intellij.faker.actions.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MethodWrapperTest {

    @ParameterizedTest
    @MethodSource("provideInputAndOutput")
    void validatePrettyName(String input, String output) {
        String prettyName1 = MethodWrapper.createPrettyName(input);

        assertThat(prettyName1).isEqualTo(output);
    }

    private static Stream<Arguments> provideInputAndOutput() {
        return Stream.of(
                Arguments.of("battlefield1", "Battlefield 1"),
                Arguments.of("backToTheFuture", "Back To The Future")
        );
    }
}
