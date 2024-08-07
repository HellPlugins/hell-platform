package org.helldev.javacord.platform.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DefaultTokenConfig extends OkaeriConfig {

    @Comment("Bot token:")
    public String token = "";

}
