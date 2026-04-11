package pl.tigersoft.intellij.faker.actions.utils;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReflectionUtils {
    private static final List<String> excludedClasses = Arrays.asList("Options", "Number", "CreditCardType", "Faker", "Get");

    private static final List<String> excludedMethods = Arrays.asList("wait", "notify", "notifyAll", "toString", "locality", "getClass", "hashCode", "getFaker");

    public static final Comparator<Method> methodComparator = (method1, method2) ->
            method1.getName().compareToIgnoreCase(method2.getName());

    private ReflectionUtils() {

    }

    public static Stream<Method> createCategoryList(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        return Arrays.stream(methods)
                .filter(ReflectionUtils.categoryFilter())
                .sorted(methodComparator);
    }


    private static Predicate<Method> categoryFilter() {
        return subcategoryFilter()
                .and(method -> !excludedClasses.contains(method.getReturnType().getSimpleName()))
                .and(method -> !excludedMethods.contains(method.getName()));
    }

    private static Predicate<Method> subcategoryFilter() {
        return method -> method.getParameters().length == 0
                && Modifier.isPublic(method.getModifiers());
    }

    public static Stream<Method> createSubcategoryList(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(ReflectionUtils.subcategoryFilter())
                .sorted(ReflectionUtils.methodComparator);
    }
}
