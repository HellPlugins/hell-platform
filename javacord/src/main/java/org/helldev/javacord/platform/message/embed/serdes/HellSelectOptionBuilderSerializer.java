package org.helldev.javacord.platform.message.embed.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.helldev.javacord.platform.message.embed.HellSelectOptionBuilder;
import org.javacord.api.entity.emoji.Emoji;

@RequiredArgsConstructor
public class HellSelectOptionBuilderSerializer implements ObjectSerializer<HellSelectOptionBuilder> {

    @Override
    public boolean supports(@NonNull Class<? super HellSelectOptionBuilder> type) {
        return HellSelectOptionBuilder.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull HellSelectOptionBuilder object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        addIfNotNull(data, "label", object.getLabel());
        addIfNotNull(data, "value", object.getValue());
        addIfNotNull(data, "description", object.getDescription());
        addIfNotNull(data, "emoji-as-unicode", object.getEmojiAsUnicode());
        addIfNotNull(data, "emoji", object.getEmoji());
        addIfNotNull(data, "default", object.isDefaultOption());
    }

    @Override
    public HellSelectOptionBuilder deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        HellSelectOptionBuilder builder = new HellSelectOptionBuilder();

        if (data.containsKey("value")) {
            builder.setValue(data.get("value", String.class));
        }

        if (data.containsKey("label")) {
            builder.setLabel(data.get("label", String.class));
        }

        if (data.containsKey("description")) {
            builder.setDescription(data.get("description", String.class));
        }
        if (data.containsKey("emoji-as-unicode")) {
            builder.setEmoji(data.get("emoji-as-unicode", String.class));
        }
        if (data.containsKey("emoji")) {
            builder.setEmoji(data.get("emoji", Emoji.class));
        }
        if (data.containsKey("default")) {
            builder.setDefault(data.get("default", Boolean.class));
        }

        return builder;
    }

    private void addIfNotNull(SerializationData data, String key, Object value) {
        if (value != null) {
            data.add(key, value);
        }
    }
}
