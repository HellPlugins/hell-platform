package org.helldev.javacordplatformtest.listener;

import eu.okaeri.injector.annotation.Inject;


import lombok.RequiredArgsConstructor;
import org.helldev.javacordplatformtest.config.TestConfig;
import org.helldev.javacordplatformtest.persistance.Member;
import org.helldev.javacordplatformtest.persistance.MemberRepository;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;


import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageListener  implements MessageCreateListener {

    private final TestConfig testConfig;
    private final MemberRepository memberRepository;

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessageContent().equalsIgnoreCase("gejmil to")) {
            messageCreateEvent.getChannel().sendMessage("Kamil K. zamieszkujacy lodz, naczelny pedzel lodzki");
        }
        if (messageCreateEvent.getMessageContent().equalsIgnoreCase("test")) {
            testConfig.message.send(messageCreateEvent.getChannel());
        }

        if (messageCreateEvent.getMessageContent().equalsIgnoreCase("testMember")) {
            Member member = memberRepository.get(messageCreateEvent.getMessage().getUserAuthor().get());

            testConfig.message.with("test", "woooow placeholder").send(messageCreateEvent.getChannel());
        }

        if (messageCreateEvent.getMessageContent().equalsIgnoreCase("testMember")) {
            Member member = memberRepository.get(messageCreateEvent.getMessage().getUserAuthor().get());

            testConfig.testtt.send(messageCreateEvent.getChannel());
        }


    }
}
