package com.example.slackbotagebot;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.slack.api.app_backend.util.JsonPayloadExtractor;
import com.slack.api.bolt.util.JsonOps;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppMentionEvent;

@RestController
@RequestMapping("/slack/events")
public class SlackBotController {
	
	@Autowired
    private final SlackBotConfig slackBot;
    
    @Autowired
    private final MessageParser messageParser;
    
    @Autowired
    private final AgeCalculator ageCalculator;

    public SlackBotController(SlackBotConfig slackBot, MessageParser messageParser, AgeCalculator ageCalculator) {
        this.slackBot = slackBot;
        this.messageParser = messageParser;
        this.ageCalculator = ageCalculator;
    }
    @PostMapping("/slack/events")
    public ResponseEntity<?> handleEvent(@RequestBody String payload) throws IOException, SlackApiException {
        AppMentionEvent event = JsonOps.fromJson(payload, AppMentionEvent.class);
        String messageText = event.getText();
        Optional<LocalDate> maybeDateOfBirth = messageParser.parseDate(messageText);
        if (maybeDateOfBirth.isPresent()) {
            LocalDate dateOfBirth = maybeDateOfBirth.get();
            int age = ageCalculator.calculateAge(dateOfBirth);
            String responseText = "You are " + age + " years old.";
            slackBot.sendMessage(responseText, event.getChannel());
        }
        return ResponseEntity.ok().build();
    }

}

