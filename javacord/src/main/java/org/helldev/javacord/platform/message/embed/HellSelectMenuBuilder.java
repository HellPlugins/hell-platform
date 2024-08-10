package org.helldev.javacord.platform.message.embed;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.message.component.ComponentType;
import org.javacord.api.entity.message.component.SelectMenuBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for creating and configuring a select menu with options in a Discord bot.
 */
@Getter
@Data
@RequiredArgsConstructor
public class HellSelectMenuBuilder {

    private final SelectMenuBuilder selectMenuBuilder;
    private final List<HellSelectOptionBuilder> options = new ArrayList<>();

    private ComponentType componentType = null;
    private String customId = null;
    private String placeHolder = null;
    private int minimumValues = 1;
    private int maximumValues = 3;
    private Iterable<ChannelType> channelTypes;
    private boolean disabled = false;

    /**
     * Constructor to initialize the select menu with component type and custom ID.
     *
     * @param componentType The type of the component.
     */
    public HellSelectMenuBuilder(ComponentType componentType) {
        this.selectMenuBuilder = new SelectMenuBuilder(componentType, null);
        this.componentType = componentType;
    }

    public HellSelectMenuBuilder setCustomId(String customId) {
        this.selectMenuBuilder.setCustomId(customId);
        this.customId = customId;
        return this;
    }

    /**
     * Sets the placeholder text for the select menu.
     *
     * @param placeholder The placeholder text.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder setPlaceholder(String placeholder) {
        selectMenuBuilder.setPlaceholder(placeholder);
        this.placeHolder = placeholder;
        return this;
    }

    /**
     * Sets the minimum number of values that can be selected.
     *
     * @param minimumValues The minimum number of selectable values.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder setMinimumValues(int minimumValues) {
        selectMenuBuilder.setMinimumValues(minimumValues);
        this.minimumValues = minimumValues;
        return this;
    }

    /**
     * Sets the maximum number of values that can be selected.
     *
     * @param maximumValues The maximum number of selectable values.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder setMaximumValues(int maximumValues) {
        selectMenuBuilder.setMaximumValues(maximumValues);
        this.maximumValues = maximumValues;
        return this;
    }

    /**
     * Adds channel types that can be selected in the menu.
     *
     * @param channelTypes The channel types to add.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder addChannelTypes(Iterable<ChannelType> channelTypes) {
        selectMenuBuilder.addChannelTypes(channelTypes);
        this.channelTypes = channelTypes;
        return this;
    }

    /**
     * Adds a single option to the select menu.
     *
     * @param hellSelectOptionBuilder The option to add.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder addOption(HellSelectOptionBuilder hellSelectOptionBuilder) {
        selectMenuBuilder.addOption(hellSelectOptionBuilder.toSelectMenuOptionBuilder().build());
        this.options.add(hellSelectOptionBuilder);
        return this;
    }

    /**
     * Adds multiple options to the select menu.
     *
     * @param hellSelectOptionBuilders The options to add.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder addOptions(HellSelectOptionBuilder... hellSelectOptionBuilders) {
        for (HellSelectOptionBuilder optionBuilder : hellSelectOptionBuilders) {
            selectMenuBuilder.addOption(optionBuilder.toSelectMenuOptionBuilder().build());
            this.options.add(optionBuilder);
        }
        return this;
    }

    /**
     * Removes a single option from the select menu.
     *
     * @param hellSelectOptionBuilder The option to remove.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder removeOption(HellSelectOptionBuilder hellSelectOptionBuilder) {
        selectMenuBuilder.removeOption(hellSelectOptionBuilder.toSelectMenuOptionBuilder().build());
        this.options.remove(hellSelectOptionBuilder);
        return this;
    }

    /**
     * Removes multiple options from the select menu.
     *
     * @param hellSelectOptionBuilders The options to remove.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder removeOptions(HellSelectOptionBuilder... hellSelectOptionBuilders) {
        for (HellSelectOptionBuilder optionBuilder : hellSelectOptionBuilders) {
            selectMenuBuilder.removeOption(optionBuilder.toSelectMenuOptionBuilder().build());
            this.options.remove(optionBuilder);
        }
        return this;
    }

    /**
     * Removes all options from the select menu.
     *
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder removeAllOptions() {
        selectMenuBuilder.removeAllOptions();
        this.options.clear();
        return this;
    }

    /**
     * Sets the select menu as disabled or enabled.
     *
     * @param disabled True to disable the select menu, false to enable it.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder setDisabled(boolean disabled) {
        selectMenuBuilder.setDisabled(disabled);
        this.disabled = disabled;
        return this;
    }

    /**
     * Copies the properties of another HellSelectMenuBuilder object into this one.
     *
     * @param hellSelectMenuBuilder The HellSelectMenuBuilder object to copy from.
     * @return The updated HellSelectMenuBuilder object.
     */
    public HellSelectMenuBuilder copy(HellSelectMenuBuilder hellSelectMenuBuilder) {
        selectMenuBuilder.copy(hellSelectMenuBuilder.selectMenuBuilder.build());
        this.componentType = hellSelectMenuBuilder.componentType;
        this.customId = hellSelectMenuBuilder.customId;
        this.placeHolder = hellSelectMenuBuilder.placeHolder;
        this.minimumValues = hellSelectMenuBuilder.minimumValues;
        this.maximumValues = hellSelectMenuBuilder.maximumValues;
        this.channelTypes = hellSelectMenuBuilder.channelTypes;
        this.disabled = hellSelectMenuBuilder.disabled;
        this.options.clear();
        this.options.addAll(hellSelectMenuBuilder.options);
        return this;
    }
}
