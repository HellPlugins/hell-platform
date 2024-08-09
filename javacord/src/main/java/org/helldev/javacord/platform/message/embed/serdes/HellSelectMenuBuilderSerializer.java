package org.helldev.javacord.platform.message.embed.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.helldev.javacord.platform.message.embed.HellSelectMenuBuilder;
import org.helldev.javacord.platform.message.embed.HellSelectOptionBuilder;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.message.component.ComponentType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class HellSelectMenuBuilderSerializer implements ObjectSerializer<HellSelectMenuBuilder> {

    @Override
    public boolean supports(@NonNull Class<? super HellSelectMenuBuilder> type) {
        return HellSelectMenuBuilder.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull HellSelectMenuBuilder object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        addIfNotNull(data, "component-type", object.getSelectMenuComponentType());
        addIfNotNull(data, "custom-id", object.getCustomId());
        addIfNotNull(data, "placeholder", object.getPlaceHolder());
        addIfNotNull(data, "min-values", object.getMinimumValues());
        addIfNotNull(data, "max-values", object.getMaximumValues());
        addIfNotNull(data, "disabled", object.isDisabled());

        if (object.getChannelTypes() != null) {
            List<String> channelTypeNames = StreamSupport.stream(object.getChannelTypes().spliterator(), false)
                    .map(ChannelType::name)
                    .collect(Collectors.toList());
            data.add("channel-types", channelTypeNames);
        }

        if (!object.getOptions().isEmpty()) {
            data.add("options", object.getOptions());
        }
    }

    @Override
    public HellSelectMenuBuilder deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        ComponentType componentType = data.get("component-type", ComponentType.class);
        String customID = data.get("custom-id", String.class);

        HellSelectMenuBuilder builder = new HellSelectMenuBuilder(componentType, customID);

        builder.setSelectMenuPlaceholder(data.get("placeholder", String.class));
        builder.setMinimumValues(data.get("min-values", Integer.class));
        builder.setMaximumValues(data.get("max-values", Integer.class));
        builder.setDisabled(data.get("disabled", Boolean.class));

        if (data.containsKey("channel-types")) {
            List<String> channelTypeNames = data.getAsList("channel-types", String.class);
            Iterable<ChannelType> channelTypes = channelTypeNames.stream()
                    .map(ChannelType::valueOf)
                    .collect(Collectors.toList());
            builder.addSelectMenuChannelTypes(channelTypes);
        }

        if (data.containsKey("options")) {
            data.getAsList("options", HellSelectOptionBuilder.class)
                    .forEach(builder::addOption);
        }

        return builder;
    }

    private void addIfNotNull(SerializationData data, String key, Object value) {
        if (value != null) {
            data.add(key, value);
        }
    }
}
