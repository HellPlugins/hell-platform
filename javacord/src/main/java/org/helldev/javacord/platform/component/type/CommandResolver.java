package org.helldev.javacord.platform.component.type;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;
import eu.okaeri.platform.core.component.ComponentHelper;
import eu.okaeri.platform.core.component.creator.ComponentCreator;
import eu.okaeri.platform.core.component.creator.ComponentResolver;
import eu.okaeri.platform.core.component.manifest.BeanManifest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.helldev.javacord.platform.HellBotBase;
import org.helldev.javacord.platform.command.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CommandResolver implements ComponentResolver {

    @Inject("discordApi")
    private DiscordApi discordApi;

    @Inject
    private HellBotBase hellBotBase;

    private final Map<String, Command> commandInstances = new HashMap<>();

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return Command.class.isAssignableFrom(type);
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return false;
    }

    @Override
    public Command make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {
        long start = System.currentTimeMillis();

        Command commandInstance = (Command) injector.createInstance(manifest.getType());

        hellBotBase.getCommandsList().add(commandInstance);

        discordApi.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            if (interaction.getCommandName().equalsIgnoreCase(commandInstance.getName())) {
                commandInstance.respond().onSlashCommandCreate(event);
            }
        });

        discordApi.addAutocompleteCreateListener(event -> {
            SlashCommandInteraction interaction = event.getAutocompleteInteraction();
            if (interaction.getCommandName().equalsIgnoreCase(commandInstance.getName())) {
                commandInstance.autocomplete().onAutocompleteCreate(event);
            }
        });

        long took = System.currentTimeMillis() - start;
        if (took > 1) {
            creator.log(ComponentHelper.buildComponentMessage()
                .type("Added command component")
                .name(manifest.getType().getSimpleName())
                .took(took)
                .build());
        }
        creator.increaseStatistics("commands", 1);

        return commandInstance;
    }

}
