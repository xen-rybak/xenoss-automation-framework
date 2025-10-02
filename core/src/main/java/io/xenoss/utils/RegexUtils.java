package io.xenoss.utils;

import org.testng.TestException;
import java.util.regex.Pattern;

public class RegexUtils {
    public static String extractByRegex(String initialString, String pattern) {
        return extractByRegex(initialString, Pattern.compile(pattern));
    }

    public static String extractByRegex(String initialString, Pattern pattern) {
        var matcher = pattern.matcher(initialString);
        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new TestException(String.format(
                "Unable to extract value using regex.%nInitial string: %s%nPattern:%s", initialString, pattern));
    }

    public static Boolean stringMatches(String stringToCheck, String pattern) {
        return stringMatches(stringToCheck, Pattern.compile(pattern));
    }

    public static Boolean stringMatches(String stringToCheck, Pattern pattern) {
        var matcher = pattern.matcher(stringToCheck);
        return matcher.find();
    }
}
