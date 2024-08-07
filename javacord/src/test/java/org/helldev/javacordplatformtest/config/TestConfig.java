package org.helldev.javacordplatformtest.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Variable;
import eu.okaeri.platform.core.annotation.Component;
import eu.okaeri.platform.core.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;
import org.helldev.javacord.platform.message.HellMessage;
import org.helldev.javacord.platform.message.serializer.HellMessageSerdes;
import org.helldev.javacord.platform.message.type.HellMessageType;

@Configuration(path = "config.yml")
@Getter
@Setter
public class TestConfig extends OkaeriConfig {

    @Comment("testowy config")
    public String test = "testowyString";

    @Comment("message")
    public HellMessage message = new HellMessage(HellMessageType.MESSAGE, "testowy mesedz {test}");

    @Comment("testMsg")
    public HellMessage testtt = new HellMessage(HellMessageType.MESSAGE, "testowy mesedz bez placeholdera {test}");


    @Comment("Storage settings")
    private StorageConfig storage = new StorageConfig();

    @Getter
    @Setter
    public static class StorageConfig extends OkaeriConfig {

        @Variable("OPE_STORAGE_BACKEND")
        @Comment("Type of the storage backend: FLAT, REDIS, MONGO, MYSQL, H2")
        private StorageBackend backend = StorageBackend.FLAT;

        @Variable("OPE_STORAGE_PREFIX")
        @Comment("Prefix for the storage: allows to have multiple instances using same database")
        @Comment("FLAT   : no effect due to local nature")
        @Comment("REDIS  : {storagePrefix}:{collection} -> ope:player")
        @Comment("MONGO  : {storagePrefix}:{collection} -> ope_player")
        @Comment("MYSQL  : {storagePrefix}:{collection} -> ope_player")
        @Comment("H2     : {storagePrefix}:{collection} -> ope_player")
        private String prefix = "ope";

        @Variable("OPE_STORAGE_URI")
        @Comment("FLAT   : ./storage")
        @Comment("REDIS  : redis://localhost")
        @Comment("MONGO  : mongodb://localhost:27017/db")
        @Comment("MYSQL  : jdbc:mysql://localhost:3306/db?user=root&password=1234")
        @Comment("H2     : jdbc:h2:file:./storage;mode=mysql")
        private String uri = "./storage";
    }
}
