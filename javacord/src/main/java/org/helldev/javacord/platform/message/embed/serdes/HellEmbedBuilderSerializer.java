package org.helldev.javacord.platform.message.embed.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.helldev.javacord.platform.message.embed.HellEmbedBuilder;
import org.helldev.javacord.platform.message.embed.HellEmbedButtonBuilder;
import org.helldev.javacord.platform.message.embed.HellEmbedField;
import org.helldev.javacord.platform.message.embed.HellSelectMenuBuilder;

import java.awt.*;
import java.time.Instant;

@RequiredArgsConstructor
public class HellEmbedBuilderSerializer implements ObjectSerializer<HellEmbedBuilder> {

    @Override
    public boolean supports(@NonNull Class<? super HellEmbedBuilder> type) {
        return HellEmbedBuilder.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull HellEmbedBuilder object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        addIfNotNull(data, "embed-title", object.getTitle());
        addIfNotNull(data, "embed-description", object.getDescription());
        addIfNotNull(data, "embed-url", object.getUrl());
        addIfNotNull(data, "embed-timestamp", object.getTimestamp());
        addIfNotNull(data, "embed-color", object.getColor());
        addIfNotNull(data, "embed-footer-text", object.getFooterText());
        addIfNotNull(data, "embed-footer-icon", object.getFooterIconUrl());
        addIfNotNull(data, "embed-image", object.getImageUrl());
        addIfNotNull(data, "embed-author-name", object.getAuthorName());
        addIfNotNull(data, "embed-author-url", object.getAuthorUrl());
        addIfNotNull(data, "embed-author-icon", object.getAuthorIconUrl());
        addIfNotNull(data, "embed-thumbnail", object.getThumbnailUrl());

        if (!object.getFields().isEmpty()) {
            data.add("embed-fields", object.getFields());
        }

        if (!object.getButtons().isEmpty()) {
            data.add("embed-buttons", object.getButtons());
        }

        if (!object.getSelectMenus().isEmpty()) {
            data.add("embed-selectmenus", object.getSelectMenus());
        }

    }

    @Override
    public HellEmbedBuilder deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        HellEmbedBuilder builder = new HellEmbedBuilder();

        if (data.containsKey("embed-title")) {
            builder.setTitle(data.get("embed-title", String.class));
        }
        if (data.containsKey("embed-description")) {
            builder.setDescription(data.get("embed-description", String.class));
        }
        if (data.containsKey("embed-url")) {
            builder.setUrl(data.get("embed-url", String.class));
        }
        if (data.containsKey("embed-timestamp")) {
            builder.setTimestamp(data.get("embed-timestamp", Instant.class));
        }
        if (data.containsKey("embed-color")) {
            builder.setColor(data.get("embed-color", Color.class));
        }
        if (data.containsKey("embed-footer-text")) {
            String footerText = data.get("embed-footer-text", String.class);
            String footerIconUrl = data.containsKey("embed-footer-icon") ? data.get("embed-footer-icon", String.class) : null;
            builder.setFooter(footerText, footerIconUrl);
        }
        if (data.containsKey("embed-image")) {
            builder.setImage(data.get("embed-image", String.class));
        }
        if (data.containsKey("embed-author-name")) {
            String authorName = data.get("embed-author-name", String.class);
            String authorUrl = data.containsKey("embed-author-url") ? data.get("embed-author-url", String.class) : null;
            String authorIconUrl = data.containsKey("embed-author-icon") ? data.get("embed-author-icon", String.class) : null;
            builder.setAuthor(authorName, authorUrl, authorIconUrl);
        }
        if (data.containsKey("embed-thumbnail")) {
            builder.setThumbnail(data.get("embed-thumbnail", String.class));
        }

        if (data.containsKey("embed-fields")) {
            data.getAsList("embed-fields", HellEmbedField.class).forEach(field ->
                    builder.addField(field.getName(), field.getValue(), field.isInline()));
        }

        if (data.containsKey("embed-buttons")) {
            data.getAsList("embed-buttons", HellEmbedButtonBuilder.class).forEach(builder::addButton);
        }

        if (data.containsKey("embed-selectmenus")) {
            data.getAsList("embed-selectmenus", HellSelectMenuBuilder.class).forEach(builder::addSelectMenu);
        }


        return builder;
    }

    private void addIfNotNull(SerializationData data, String key, Object value) {
        if (value != null) {
            data.add(key, value);
        }
    }
}
