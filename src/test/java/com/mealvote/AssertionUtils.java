package com.mealvote;

import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mealvote.JsonParseUtils.readFromJsonMvcResult;
import static com.mealvote.JsonParseUtils.readListFromJsonMvcResult;
import static org.assertj.core.api.Assertions.assertThat;

public class AssertionUtils {

    public static <T> List<T> asSortedList(Comparator<? super T> comparator, T... elements) {
        if (elements.length == 0) return Collections.emptyList();
        List<T> list = Arrays.asList(elements);
        list.sort(comparator);
        return list;
    }

    public static <T> void assertMatch(T actual, T expected, String[] ignoredFields) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, ignoredFields);
    }

    public static <T> void assertMatch(Iterable<T> actual, Iterable<T> expected, String[] ignoredFields) {
        assertThat(actual).usingElementComparatorIgnoringFields(ignoredFields).isEqualTo(expected);
    }

    public static <T> ResultMatcher contentJson(List<T> expected, Class<T> clazz, String[] ignoredFields) {
        return result -> assertMatch(readListFromJsonMvcResult(result, clazz), expected, ignoredFields);
    }

    public static <T> ResultMatcher contentJson(T expected, Class<T> clazz, String[] ignoredFields) {
        return result -> assertMatch(readFromJsonMvcResult(result, clazz), expected, ignoredFields);
    }
}