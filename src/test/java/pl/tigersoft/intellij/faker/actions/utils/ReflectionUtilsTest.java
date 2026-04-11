package pl.tigersoft.intellij.faker.actions.utils;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilsTest {

    @Test
    void testValidateCategoryList() {
        List<Method> actualMethodList = ReflectionUtils.createCategoryList(Faker.class).collect(Collectors.toList());
        List<String> actualNameList = actualMethodList.stream().map(Method::getName).toList();


        assertThat(actualMethodList).hasSize(259);
        assertThat(actualNameList).contains("backToTheFuture");
        assertThat(actualNameList.get(0)).isEqualTo("address");
        validateSortedByName(actualNameList);
    }
    @Test
    void testValidateSubCategoryList() {
        var faker = new Faker();
        List<Method> actualMethodList = ReflectionUtils.createSubcategoryList(faker.gameOfThrones().getClass()).collect(Collectors.toList());
        List<String> actualNameList = actualMethodList.stream().map(Method::getName).toList();


        assertThat(actualMethodList).hasSize(5);

        assertThat(actualNameList).isEqualTo(List.of("character", "city", "dragon", "house", "quote"));
    }
    private static void validateSortedByName(List<String> actualNameList) {

        List<String> expectedList = new ArrayList<>(actualNameList);
        Collections.sort(expectedList);
        assertThat(actualNameList.subList(0, 150)).isEqualTo(expectedList.subList(0, 150));
    }

}
