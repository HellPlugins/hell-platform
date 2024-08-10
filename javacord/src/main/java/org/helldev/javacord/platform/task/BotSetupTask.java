package org.helldev.javacord.platform.task;

import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import eu.okaeri.platform.core.plan.ExecutionTask;
import lombok.RequiredArgsConstructor;
import org.helldev.javacord.platform.HellBotBase;
import org.helldev.javacord.platform.annotations.SetAllIntents;
import org.helldev.javacord.platform.annotations.SetIntents;
import org.helldev.javacord.platform.annotations.Token;
import org.helldev.javacord.platform.config.DefaultTokenConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.File;
import java.util.Arrays;

@RequiredArgsConstructor
public class BotSetupTask implements ExecutionTask<HellBotBase> {

    @Override
    public void execute(HellBotBase platform) {
        Token discordToken = platform.getClass().getAnnotation(Token.class);
        SetAllIntents setAllIntents = platform.getClass().getAnnotation(SetAllIntents.class);
        SetIntents setIntents = platform.getClass().getAnnotation(SetIntents.class);
        String token;

        if (discordToken != null) {
            token = discordToken.value();
        } else {
            DefaultTokenConfig defaultTokenConfig = ConfigManager.create(DefaultTokenConfig.class, (it) -> {
                it.withConfigurer(new YamlSnakeYamlConfigurer());
                it.withBindFile(new File(platform.getDataFolder(), "token.yml"));
                it.withRemoveOrphans(true);
                it.saveDefaults();
                it.load(true);
            });

            token = defaultTokenConfig.getToken();

            if (token == null || token.isEmpty()) {
                throw new OkaeriException("Token is missing and no valid configuration found. Check for token.yml.");
            }
        }
        DiscordApiBuilder discordApiBuilder = new DiscordApiBuilder()
            .setToken(token);

        if (setIntents != null) {
            discordApiBuilder.setIntents(setIntents.value());
            platform.getLogger().info("Intents: " + Arrays.toString(setIntents.value()));

        }

        if (setAllIntents != null) {
            discordApiBuilder.setAllIntents();
            platform.getLogger().info("Intents: ALL");
        }

        DiscordApi discordApi = discordApiBuilder
            .login()
            .whenComplete((api, throwable) -> {
                if (throwable != null) {
                    throw new OkaeriException("Exception while logging in to Discord", throwable);
                }

                platform.getLogger().info("Logged in as: " + api.getYourself().getDiscriminatedName());
            })
            .join();

        platform.getInjector().registerInjectable("discordApi", discordApi);
        platform.setDiscordApi(discordApi);
    }
}
