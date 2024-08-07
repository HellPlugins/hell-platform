package org.helldev.javacord.platform.message.embed;

import lombok.Data;
import lombok.Getter;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.component.SelectMenuOptionBuilder;

@Getter
@Data
public class HellSelectOptionBuilder {

    private SelectMenuOptionBuilder selectMenuOptionBuilder;

    private String label;
    private String value;
    private String description = null;
    private String emojiAsUnicode = null;
    private Emoji emoji = null;
    private boolean defaultOption = false;

    public HellSelectOptionBuilder(String value, String label) {
        selectMenuOptionBuilder = new SelectMenuOptionBuilder().setValue(value).setLabel(label);
        this.label = label;
        this.value = value;
    }

    public HellSelectOptionBuilder setDescription(String description) {
        selectMenuOptionBuilder.setDescription(description);
        this.description = description;
        return this;
    }

    public HellSelectOptionBuilder setEmoji(String emojiAsUnicode) {
        selectMenuOptionBuilder.setEmoji(emojiAsUnicode);
        this.emojiAsUnicode = emojiAsUnicode;
        this.emoji = null;
        return this;
    }

    public HellSelectOptionBuilder setEmoji(Emoji emoji) {
        selectMenuOptionBuilder.setEmoji(emoji);
        this.emoji = emoji;
        this.emojiAsUnicode = null;
        return this;
    }

    public HellSelectOptionBuilder setDefault(boolean defaultOption) {
        selectMenuOptionBuilder.setDefault(defaultOption);
        this.defaultOption = defaultOption;
        return this;
    }

    public SelectMenuOptionBuilder toSelectMenuOptionBuilder() {
        return this.selectMenuOptionBuilder;
    }
}
