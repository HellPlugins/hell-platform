package org.helldev.javacord.platform.message.embed;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.component.ButtonBuilder;
import org.javacord.api.entity.message.component.ButtonStyle;

/**
 * Builder class for creating and configuring a button in an embed message for a Discord bot.
 */
@Getter
@Data
@RequiredArgsConstructor
public class HellEmbedButtonBuilder {

    private final ButtonBuilder builder = new ButtonBuilder();

    private ButtonStyle buttonStyle = null;
    private String customId = null;
    private String label = null;
    private String url = null;
    private boolean disabled = false;
    private Emoji emoji = null;

    /**
     * Sets the style of the button.
     *
     * @param buttonStyle The style of the button.
     * @return The updated HellEmbedButtonBuilder object.
     */
    public HellEmbedButtonBuilder setButtonStyle(ButtonStyle buttonStyle) {
        this.builder.setStyle(buttonStyle);
        this.buttonStyle = buttonStyle;
        return this;
    }

    /**
     * Sets the custom ID of the button.
     *
     * @param customId The custom ID of the button.
     * @return The updated HellEmbedButtonBuilder object.
     */
    public HellEmbedButtonBuilder setCustomId(String customId) {
        this.builder.setCustomId(customId);
        this.customId = customId;
        return this;
    }

    /**
     * Sets the label of the button.
     *
     * @param label The label of the button.
     * @return The updated HellEmbedButtonBuilder object.
     */
    public HellEmbedButtonBuilder setLabel(String label) {
        this.builder.setLabel(label);
        this.label = label;
        return this;
    }

    /**
     * Sets the URL of the button. This is applicable only for link buttons.
     *
     * @param url The URL of the button.
     * @return The updated HellEmbedButtonBuilder object.
     */
    public HellEmbedButtonBuilder setUrl(String url) {
        this.builder.setUrl(url);
        this.url = url;
        return this;
    }

    /**
     * Sets whether the button is disabled.
     *
     * @param disabled True if the button should be disabled, false otherwise.
     * @return The updated HellEmbedButtonBuilder object.
     */
    public HellEmbedButtonBuilder setDisabled(boolean disabled) {
        this.builder.setDisabled(disabled);
        this.disabled = disabled;
        return this;
    }

    /**
     * Sets the emoji of the button.
     *
     * @param emoji The emoji to set on the button.
     * @return The updated HellEmbedButtonBuilder object.
     */
    public HellEmbedButtonBuilder setEmoji(Emoji emoji) {
        this.builder.setEmoji(emoji);
        this.emoji = emoji;
        return this;
    }

    /**
     * Converts the HellEmbedButtonBuilder to a ButtonBuilder object.
     *
     * @return The ButtonBuilder object.
     */
    public ButtonBuilder toButtonBuilder() {
        return this.builder;
    }
}
