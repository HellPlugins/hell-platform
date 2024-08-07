package org.helldev.javacord.platform.message.embed.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.helldev.javacord.platform.message.embed.HellEmbedField;

public class HellEmbedFieldSerializer implements ObjectSerializer<HellEmbedField> {

    @Override
    public boolean supports(@NonNull Class<? super HellEmbedField> type) {
        return HellEmbedField.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull HellEmbedField object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("field-name", object.getName());
        data.add("field-value", object.getValue());
        data.add("field-inline", object.isInline());
    }
    @Override
    public HellEmbedField deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        return new HellEmbedField(
                data.get("field-name", String.class),
                data.get("field-value", String.class),
                data.get("field-inline", Boolean.class)
        );
    }
}
