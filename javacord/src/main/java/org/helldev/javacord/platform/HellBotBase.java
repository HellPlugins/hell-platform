package org.helldev.javacord.platform;

import eu.okaeri.commands.Commands;
import eu.okaeri.commands.OkaeriCommands;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.serdes.okaeri.SerdesOkaeri;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import eu.okaeri.injector.Injector;
import eu.okaeri.persistence.Persistence;
import eu.okaeri.persistence.document.ConfigurerProvider;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.platform.core.OkaeriPlatform;
import eu.okaeri.platform.core.component.ComponentHelper;
import eu.okaeri.platform.core.component.creator.ComponentCreator;
import eu.okaeri.platform.core.placeholder.SimplePlaceholdersFactory;
import eu.okaeri.platform.core.plan.ExecutionPlan;
import eu.okaeri.platform.core.plan.ExecutionResult;
import eu.okaeri.platform.core.plan.ExecutionTask;
import eu.okaeri.platform.core.plan.task.*;
import eu.okaeri.platform.standalone.component.ApplicationComponentCreator;
import eu.okaeri.platform.standalone.i18n.SystemLocaleProvider;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.helldev.javacord.platform.command.Command;
import org.helldev.javacord.platform.component.BotCreatorRegistry;
import org.helldev.javacord.platform.serializer.SerdesJavacord;
import org.helldev.javacord.platform.task.BotSetupTask;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eu.okaeri.platform.core.plan.ExecutionPhase.*;

public class HellBotBase implements OkaeriPlatform {

    private final @Getter Logger logger = LoggerFactory.getLogger(this.getClass());
    private final @Getter File dataFolder = new File(".");
    private final @Getter File file = ComponentHelper.getJarFile(HellBotBase.class);

    private @Getter @Setter Injector injector;
    private @Getter @Setter ComponentCreator creator;
    private @Getter @Setter DiscordApi discordApi;

    private final @Getter List<Command> commandsList = new ArrayList<>();

    @Override
    public void log(@NonNull String message) {
        this.getLogger().info(message);
    }

    @Override
    public void plan(@NonNull ExecutionPlan plan) {

        plan.add(PRE_SETUP, new InjectorSetupTask());
        plan.add(PRE_SETUP, (ExecutionTask<HellBotBase>) platform -> {
            platform.registerInjectable("dataFolder", platform.getDataFolder());
            platform.registerInjectable("jarFile", platform.getFile());
            platform.registerInjectable("logger", platform.getLogger());
            platform.registerInjectable("bot", platform);
            platform.registerInjectable("placeholders", Placeholders.create(true));
            platform.registerInjectable("defaultConfigurerProvider", (ConfigurerProvider) YamlSnakeYamlConfigurer::new);
            platform.registerInjectable("defaultConfigurerSerdes", new Class[]{
                SerdesCommons.class,
                SerdesOkaeri.class,
                SerdesJavacord.class
            });
            platform.registerInjectable("defaultPlaceholdersFactory", new SimplePlaceholdersFactory());
            platform.registerInjectable("i18nLocaleProvider", new SystemLocaleProvider());
        });

        plan.add(PRE_SETUP, new BotSetupTask());

        plan.add(SETUP, new CommandsSetupTask(new OkaeriCommands()));
        plan.add(SETUP, new CreatorSetupTask(ApplicationComponentCreator.class, BotCreatorRegistry.class));

        plan.add(POST_SETUP, new BeanManifestCreateTask());
        plan.add(POST_SETUP, new BeanManifestExecuteTask());
        plan.add(POST_SETUP, new CommandsRegisterTask());

        plan.add(POST_SETUP, (ExecutionTask<HellBotBase>) platform -> {
            this.discordApi.bulkOverwriteGlobalApplicationCommands(
                commandsList.stream().map(Command::getSlashCommandBuilder).collect(Collectors.toSet())
            ).join();
        });

        plan.add(SHUTDOWN, new CloseableShutdownTask(Persistence.class));
        plan.add(SHUTDOWN, new CloseableShutdownTask(Commands.class));
    }

    public static <T extends HellBotBase> T run(@NonNull T app, @NonNull String[] args) {
        ExecutionResult result = ExecutionPlan.dispatch(app);
        app.log(app.getCreator().getSummaryText(result.getTotalMillis()));

        Thread shutdownHook = new Thread(() -> result.getPlan().execute(Arrays.asList(PRE_SHUTDOWN, SHUTDOWN, POST_SHUTDOWN)));
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        return app;
    }

    @SneakyThrows
    public static <T extends HellBotBase> T run(@NonNull Class<? extends T> type, @NonNull String[] args) {
        return run(type.getConstructor().newInstance(), args);
    }
}
