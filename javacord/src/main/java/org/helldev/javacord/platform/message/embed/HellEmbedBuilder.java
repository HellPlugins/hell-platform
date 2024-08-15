package org.helldev.javacord.platform.message.embed;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for creating and configuring an embed message for a Discord bot.
 */
@Getter
@Data
@RequiredArgsConstructor
public class HellEmbedBuilder {

    private final EmbedBuilder embedBuilder = new EmbedBuilder();

    private String title = null;
    private String description = null;
    private String url = null;
    private Instant timestamp = null;
    private Color color = null;

    private String footerText = null;
    private String footerIconUrl = null;

    private String imageUrl = null;

    private String authorName = null;
    private String authorUrl = null;
    private String authorIconUrl = null;

    private String thumbnailUrl = null;

    private final List<HellEmbedField> fields = new ArrayList<>();
    private final List<HellEmbedButtonBuilder> buttons = new ArrayList<>();
    private final List<HellSelectMenuBuilder> selectMenus = new ArrayList<>();

    /**
     * Sets the title of the embed.
     *
     * @param title The title of the embed.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setTitle(String title) {
        this.embedBuilder.setTitle(title);
        this.title = title;
        return this;
    }

    /**
     * Sets the description of the embed.
     *
     * @param description The description of the embed.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setDescription(String description) {
        this.embedBuilder.setDescription(description);
        this.description = description;
        return this;
    }

    /**
     * Sets the URL of the embed.
     *
     * @param url The URL of the embed.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setUrl(String url) {
        this.embedBuilder.setUrl(url);
        this.url = url;
        return this;
    }

    /**
     * Sets the timestamp of the embed to the current time.
     *
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setTimestampToNow() {
        this.embedBuilder.setTimestampToNow();
        this.timestamp = Instant.now();
        return this;
    }

    /**
     * Sets the timestamp of the embed.
     *
     * @param timestamp The timestamp of the embed.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setTimestamp(Instant timestamp) {
        this.embedBuilder.setTimestamp(timestamp);
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Sets the color of the embed.
     *
     * @param color The color of the embed.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setColor(Color color) {
        this.embedBuilder.setColor(color);
        this.color = color;
        return this;
    }

    /**
     * Sets the footer text of the embed.
     *
     * @param text The footer text.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setFooter(String text) {
        this.embedBuilder.setFooter(text);
        this.footerText = text;
        this.footerIconUrl = null;
        return this;
    }

    /**
     * Sets the footer text and icon URL of the embed.
     *
     * @param text The footer text.
     * @param iconUrl The icon URL of the footer.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setFooter(String text, String iconUrl) {
        this.embedBuilder.setFooter(text, iconUrl);
        this.footerText = text;
        this.footerIconUrl = iconUrl;
        return this;
    }

    /**
     * Sets the image URL of the embed.
     *
     * @param url The image URL.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setImage(String url) {
        this.embedBuilder.setImage(url);
        this.imageUrl = url;
        return this;
    }

    /**
     * Sets the author of the embed.
     *
     * @param name The name of the author.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setAuthor(String name) {
        this.embedBuilder.setAuthor(name);
        this.authorName = name;
        this.authorUrl = null;
        this.authorIconUrl = null;
        return this;
    }

    /**
     * Sets the author of the embed with a URL and an icon URL.
     *
     * @param name The name of the author.
     * @param url The URL of the author.
     * @param iconUrl The icon URL of the author.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setAuthor(String name, String url, String iconUrl) {
        this.embedBuilder.setAuthor(name, url, iconUrl);
        this.authorName = name;
        this.authorUrl = url;
        this.authorIconUrl = iconUrl;
        return this;
    }

    /**
     * Sets the thumbnail URL of the embed.
     *
     * @param url The thumbnail URL.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder setThumbnail(String url) {
        this.embedBuilder.setThumbnail(url);
        this.thumbnailUrl = url;
        return this;
    }

    /**
     * Adds an inline field to the embed.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder addInlineField(String name, String value) {
        this.embedBuilder.addField(name, value, true);
        this.fields.add(new HellEmbedField(name, value, true));
        return this;
    }

    /**
     * Adds a field to the embed.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder addField(String name, String value) {
        this.embedBuilder.addField(name, value, false);
        this.fields.add(new HellEmbedField(name, value, false));
        return this;
    }

    /**
     * Adds a field to the embed.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @param inline Whether the field should be displayed inline.
     * @return The current instance of HellEmbedBuilder.
     */
    public HellEmbedBuilder addField(String name, String value, boolean inline) {
        this.embedBuilder.addField(name, value, inline);
        this.fields.add(new HellEmbedField(name, value, inline));
        return this;
    }

    /**
     * Adds a button to the embed. Throws an exception if select menus are present.
     *
     * @param hellEmbedButtonBuilder The button builder.
     * @return The current instance of HellEmbedBuilder.
     * @throws IllegalStateException If select menus are already present.
     */
    public HellEmbedBuilder addButton(HellEmbedButtonBuilder hellEmbedButtonBuilder) {
        if (!this.selectMenus.isEmpty()) {
            throw new IllegalStateException("Cannot add buttons when select menus are present.");
        }
        this.buttons.add(hellEmbedButtonBuilder);
        return this;
    }

    /**
     * Adds a select menu to the embed. Throws an exception if buttons are present.
     *
     * @param hellSelectMenuBuilder The select menu builder.
     * @return The current instance of HellEmbedBuilder.
     * @throws IllegalStateException If buttons are already present.
     */
    public HellEmbedBuilder addSelectMenu(HellSelectMenuBuilder hellSelectMenuBuilder) {
        if (!this.buttons.isEmpty()) {
            throw new IllegalStateException("Cannot add select menus when buttons are present.");
        }
        this.selectMenus.add(hellSelectMenuBuilder);
        return this;
    }

    public HellSelectMenuBuilder getSelectMenuById(String id) {
        for (HellSelectMenuBuilder hellSelectMenuBuilder : selectMenus) {
            if (hellSelectMenuBuilder.getCustomId().equalsIgnoreCase(id)) return hellSelectMenuBuilder;
        }
        return null;
    }

    /**
     * Converts this builder into a Javacord EmbedBuilder.
     *
     * @return The EmbedBuilder.
     */
    public EmbedBuilder toEmbedBuilder() {
        return this.embedBuilder;
    }
}
