package org.helldev.javacordplatformtest;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zaxxer.hikari.HikariConfig;
import eu.okaeri.configs.json.simple.JsonSimpleConfigurer;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.persistence.PersistencePath;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.persistence.flat.FlatPersistence;
import eu.okaeri.persistence.jdbc.H2Persistence;
import eu.okaeri.persistence.jdbc.MariaDbPersistence;
import eu.okaeri.persistence.mongo.MongoPersistence;
import eu.okaeri.persistence.redis.RedisPersistence;
import eu.okaeri.platform.core.annotation.Bean;
import eu.okaeri.platform.core.annotation.Scan;
import eu.okaeri.platform.core.plan.ExecutionPhase;
import eu.okaeri.platform.core.plan.Planned;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.helldev.javacord.platform.HellBotBase;
import org.helldev.javacord.platform.annotations.SetAllIntents;
import org.helldev.javacord.platform.annotations.SetIntents;
import org.helldev.javacord.platform.serializer.SerdesJavacord;
import org.helldev.javacordplatformtest.config.TestConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.intent.Intent;

import java.io.File;


@Scan(value = "org.helldev.javacordplatformtest", deep = true)
@SetAllIntents
//@SetIntents({Intent.DIRECT_MESSAGE_REACTIONS, Intent.DIRECT_MESSAGE_REACTIONS})
public class BotTest extends HellBotBase {

    public static void main(String[] args) {
        HellBotBase.run(new BotTest(), args);
    }

    @Planned(ExecutionPhase.STARTUP)
    public void startup(@Inject("discordApi") DiscordApi discordApi) {
        discordApi.updateActivity("test activity");
    }

    @Bean(value = "persistence")
    public DocumentPersistence configurePersistence(@Inject("dataFolder") File dataFolder, TestConfig config) {

        // jdbc drivers may require initialization for jdbc urls to work
        // @formatter:off
        try { Class.forName("org.mariadb.jdbc.Driver"); } catch (ClassNotFoundException ignored) { }
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException ignored) { }
        // @formatter:on

        // remember that if plugin is not intended to have shared state
        // between multiple instances you must allow users to set persistence's
        // basePath manually or add some other possibility to differ keys
        // otherwise plugin usage would be limited to one instance per one redis
        PersistencePath basePath = PersistencePath.of(config.getStorage().getPrefix());

        // multiple backends are possible with an easy switch
        switch (config.getStorage().getBackend()) {
            case FLAT:
                // specify custom child dir in dataFolder or other custom location
                File storagePath = new File(config.getStorage().getUri());
                return new DocumentPersistence(new FlatPersistence(storagePath, ".yml"), YamlSnakeYamlConfigurer::new, new SerdesJavacord());
            case REDIS:
                // construct redis client based on your needs, e.g. using config
                RedisURI redisUri = RedisURI.create(config.getStorage().getUri());
                RedisClient redisClient = RedisClient.create(redisUri);
                // it is HIGHLY recommended to use json configurer for the redis backend
                // other formats may not be supported in the future, json has native support
                // on the redis side thanks to cjson available in lua scripting
                return new DocumentPersistence(new RedisPersistence(basePath, redisClient), JsonSimpleConfigurer::new, new SerdesJavacord());
            case MONGO:
                // construct mongo client based on your needs, e.g. using config
                ConnectionString mongoUri = new ConnectionString(config.getStorage().getUri());
                MongoClient mongoClient = MongoClients.create(mongoUri);
                // validate if uri contains database
                if (mongoUri.getDatabase() == null) {
                    throw new IllegalArgumentException("Mongo URI needs to specify the database");
                }
                // it is REQUIRED to use json configurer for the mongo backend
                return new DocumentPersistence(new MongoPersistence(basePath, mongoClient, mongoUri.getDatabase()), JsonSimpleConfigurer::new, new SerdesJavacord());
            case MYSQL:
                // setup hikari based on your needs, e.g. using config
                HikariConfig mariadbHikari = new HikariConfig();
                mariadbHikari.setJdbcUrl(config.getStorage().getUri());
                // it is REQUIRED to use json configurer for the mariadb backend
                return new DocumentPersistence(new MariaDbPersistence(basePath, mariadbHikari), JsonSimpleConfigurer::new, new SerdesJavacord());
            case H2:
                // setup hikari based on your needs, e.g. using config
                HikariConfig jdbcHikari = new HikariConfig();
                jdbcHikari.setJdbcUrl(config.getStorage().getUri());
                //it is HIGHLY recommended to use json configurer for the jdbc backend
                return new DocumentPersistence(new H2Persistence(basePath, jdbcHikari), JsonSimpleConfigurer::new, new SerdesJavacord());
            default:
                throw new RuntimeException("unsupported storage backend: " + config.getStorage().getBackend());
        }
    }
}
