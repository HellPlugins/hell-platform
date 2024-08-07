package org.helldev.javacord.platform.serializer;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import lombok.NonNull;
import org.helldev.javacord.platform.message.embed.serdes.*;
import org.helldev.javacord.platform.message.serializer.HellMessageSerdes;

public class SerdesJavacord implements OkaeriSerdesPack {
    @Override
    public void register(@NonNull SerdesRegistry registry) {
        registry.register(new HellMessageSerdes());
        registry.register(new ColorSerdes());
        registry.register(new HellEmbedBuilderSerializer());
        registry.register(new HellEmbedFieldSerializer());
        registry.register(new HellEmbedButtonSerializer());
        registry.register(new HellSelectMenuBuilderSerializer());
        registry.register(new HellSelectOptionBuilderSerializer());
    }
}
