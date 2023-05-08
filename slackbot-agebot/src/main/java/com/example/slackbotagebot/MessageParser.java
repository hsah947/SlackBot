package com.example.slackbotagebot;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class MessageParser {

    private final Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public Optional<LocalDate> parseDate(String message) {
        Matcher matcher = datePattern.matcher(message);
        if (matcher.find()) {
            String dateString = matcher.group();
            return Optional.of(LocalDate.parse(dateString));
        }
        return Optional.empty();
    }

}
