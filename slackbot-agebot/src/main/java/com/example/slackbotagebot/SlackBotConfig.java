package com.example.slackbotagebot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackBotConfig {

    @Value("${slack.bot.token}")
    private String botToken;
    
    @Value("${slack.app.port}")
    private int appPort;

    @Bean
    public App slackApp() {
        App app = new App();
        app.command("/age", (req, ctx) -> {
            String dob = req.getPayload().getText();
            String age = calculateAge(dob);
            return ctx.ack("You are " + age + " years old.");
        });
        return app;
    }

    @Bean
    public SlackAppServer slackAppServer(App app) {
        SlackAppServer server = new SlackAppServer(app, appPort);
        //server.setPort(3000);
        return server;
    }
    
    public void sendMessage(String text, String channel) throws IOException, SlackApiException {
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .text(text)
                .channel(channel)
                .build();
        slackApp().client().chatPostMessage(request);
    }

    private String calculateAge(String dob) {
        try {
            // Parse the date of birth from the input
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate birthdate = LocalDate.parse(dob, formatter);

            // Calculate the difference between the birthdate and the current date
            LocalDate today = LocalDate.now();
            Period period = Period.between(birthdate, today);

            // Return the age in years
            int age = period.getYears();
            return String.valueOf(age);
        } catch (DateTimeParseException e) {
            // If the input cannot be parsed as a date, return an error message
            return "Invalid date format. Please enter your date of birth in the format MM/dd/yyyy.";
        }
    }

}
