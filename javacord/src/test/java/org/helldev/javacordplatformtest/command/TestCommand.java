package org.helldev.javacordplatformtest.command;

import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import org.helldev.javacord.platform.command.Command;
import org.helldev.javacordplatformtest.config.TestConfig;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class TestCommand extends Command {

    @Inject
    private TestConfig testConfig;

    public TestCommand() {
        super("testcommand", "A test command");
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            testConfig.message.applyToResponder(responder);
            responder.respond();
        };
    }
}
