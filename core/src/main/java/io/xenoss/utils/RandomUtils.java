package io.xenoss.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static final String PATTERN_FORMAT = "dd_MM_yyyy_HH_mm_ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                                                                        .withZone(ZoneId.systemDefault());

    private static final String PATTERN_WITH_MILLISECONDS_FORMAT = PATTERN_FORMAT + "_SSS";
    private static final DateTimeFormatter FORMATTER_WITH_MILLISECONDS =
            DateTimeFormatter.ofPattern(PATTERN_WITH_MILLISECONDS_FORMAT)
                    .withZone(ZoneId.systemDefault());

    private static final Random SHARED_RANDOM = new Random();

    public static String randomUuid() {
        return UUID.randomUUID()
                   .toString();
    }

    public static String randomIpV4() {
        return String.format("%s.%s.%s.%s",
                randomNumber(1, 255),
                randomNumber(1, 255),
                randomNumber(1, 255),
                randomNumber(1, 255));
    }

    public static String randomIpV6() {
        var chars = "123456789abcde";
        return String.format("%s:%s:%s:%s:%s:%s:%s:%s",
                randomStringFromChars(chars, 4),
                randomStringFromChars(chars, 4),
                randomStringFromChars(chars, 4),
                randomStringFromChars(chars, 4),
                randomStringFromChars(chars, 4),
                randomStringFromChars(chars, 4),
                randomStringFromChars(chars, 4),
                randomStringFromChars(chars, 4));
    }
    public static String randomUrl() {
        return String.format("https://%s.example.com/%s", randomUuid(), randomUuid());
    }

    public static Integer randomNumber(int min, int max) {
        return ThreadLocalRandom.current()
                                .nextInt(min, max == Integer.MAX_VALUE ? max : max + 1);
    }

    public static Float numberWithRandomDeviation (int initialNumber, int maxDeviationPercents) {
        var plus = "PLUS";
        var minus = "MINUS";

        float deviation = (float)randomNumber(1, maxDeviationPercents)/100 + 1;
        var deviationSign = randomNumber(0, 1000) < 500
                ? minus
                : plus;

        return deviationSign.equals(plus)
                ? initialNumber*deviation
                : initialNumber/deviation;
    }

    public static String currentTimestamp() {
        return FORMATTER.format(Instant.now());
    }

    public static String currentTimestampWithMilliseconds() {
        return FORMATTER_WITH_MILLISECONDS.format(Instant.now());
    }

    public static String randomStringWithTimestamp(String prefix) {
        return String.format("%s_%s_%s", prefix, currentTimestamp(), randomNumber(10000, 99999));
    }

    public static String randomStringOfLength(int length) {
        return RandomStringUtils.randomAlphanumeric(length)
                                .toUpperCase();
    }

    public static String randomStringFromChars(String chars, int length) {
        StringBuilder salt = new StringBuilder();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (SHARED_RANDOM.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }
        return salt.toString();
    }

    public static String randomFirstName() {
        return randomStringWithTimestamp("First");
    }

    public static String randomLastName() {
        return randomStringWithTimestamp("Last");
    }

    public static String randomEmail() {
        return String.format("%s@example.com", randomStringWithTimestamp("email"));
    }

    public static <T> T randomItemFromList(T[] list) {
        return randomItemFromList(Arrays.stream(list).toList());
    }

    public static <T> T randomItemFromList(List<T> list) {
        return randomItemFromList(list, List.of());
    }

    public static <T> T randomItemFromList(List<T> list, List<T> exceptOf) {
        var filteredList = list.stream()
                               .filter(item -> !exceptOf.contains(item))
                               .toList();
        return filteredList.get(randomNumber(0, filteredList.size() - 1));
    }

    public static boolean randomBoolean() {
        return randomItemFromList(List.of(true, false));
    }
}
