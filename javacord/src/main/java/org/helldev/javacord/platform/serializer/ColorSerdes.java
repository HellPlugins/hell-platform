package org.helldev.javacord.platform.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

import java.awt.*;

public class ColorSerdes  implements ObjectSerializer<Color> {

    @Override
    public boolean supports(@NonNull Class<? super Color> type) {
        return Color.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Color object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("r", object.getRed());
        data.add("g", object.getGreen());
        data.add("b", object.getBlue());
    }

    @Override
    public Color deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        return new Color(
            data.get("r", Integer.class),
            data.get("g", Integer.class),
            data.get("b", Integer.class)
        );
    }
}
