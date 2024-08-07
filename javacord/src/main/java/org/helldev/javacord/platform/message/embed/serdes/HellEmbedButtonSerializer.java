package org.helldev.javacord.platform.message.embed.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.helldev.javacord.platform.message.embed.HellEmbedButtonBuilder;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.component.ButtonStyle;

public class HellEmbedButtonSerializer implements ObjectSerializer<HellEmbedButtonBuilder> {

    @Override
    public boolean supports(@NonNull Class<? super HellEmbedButtonBuilder> type) {
        return HellEmbedButtonBuilder.class.isAssignableFrom(type);
    }


    @Override
    public void serialize(@NonNull HellEmbedButtonBuilder object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        addIfNotNull(data, "button-style", object.getButtonStyle());
        addIfNotNull(data, "button-customid", object.getCustomId());
        addIfNotNull(data, "button-label", object.getLabel());
        addIfNotNull(data, "button-url", object.getUrl());
        addIfNotNull(data, "button-disabled", object.isDisabled());
        addIfNotNull(data, "button-emoji", object.getEmoji());

    }

    @Override
    public HellEmbedButtonBuilder deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        HellEmbedButtonBuilder builder = new HellEmbedButtonBuilder();

        if (data.containsKey("button-style")) {
            builder.setButtonStyle(data.get("button-style", ButtonStyle.class));
        }
        if (data.containsKey("button-customid")) {
            builder.setCustomId(data.get("button-customid", String.class));
        }
        if (data.containsKey("button-label")) {
            builder.setLabel(data.get("button-label", String.class));
        }
        if (data.containsKey("button-url")) {
            builder.setUrl(data.get("button-url", String.class));
        }
        if (data.containsKey("button-disabled")) {
            builder.setDisabled(data.get("button-disabled", boolean.class));
        }

        if (data.containsKey("button-emoji")) {
            builder.setEmoji(data.get("button-emoji", Emoji.class));
        }

        return builder;
    }

    private void addIfNotNull(SerializationData data, String key, Object value) {
        if (value != null) {
            data.add(key, value);
        }
    }
}
