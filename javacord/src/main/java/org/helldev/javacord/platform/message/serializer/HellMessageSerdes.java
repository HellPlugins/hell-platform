package org.helldev.javacord.platform.message.serializer;

import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.helldev.javacord.platform.message.HellMessage;
import org.helldev.javacord.platform.message.embed.HellEmbedBuilder;
import org.helldev.javacord.platform.message.type.HellMessageType;

public class HellMessageSerdes implements ObjectSerializer<HellMessage> {

    @Override
    public boolean supports(@NonNull Class<? super HellMessage> type) {
        return HellMessage.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull HellMessage object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("type", object.getMessageType());
        data.add("value", object.getValue());
    }

    @Override
    public HellMessage deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        final HellMessageType messageType = data.get("type", HellMessageType.class);

        if (messageType.equals(HellMessageType.MESSAGE)) {
            return new HellMessage(
                    messageType,
                    data.get("value", String.class)
            );
        }

        if (messageType.equals(HellMessageType.EMBED)) {
            return new HellMessage(
                    messageType,
                    data.get("value", HellEmbedBuilder.class)
            );
        }

        throw new OkaeriException("Cannot resolve unknown notice-type: " + messageType);
    }
}
