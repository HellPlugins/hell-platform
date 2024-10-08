package org.helldev.javacord.platform.message;

import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.helldev.javacord.platform.message.embed.HellEmbedBuilder;
import org.helldev.javacord.platform.message.embed.HellEmbedButtonBuilder;
import org.helldev.javacord.platform.message.embed.HellSelectMenuBuilder;
import org.helldev.javacord.platform.message.embed.HellSelectOptionBuilder;
import org.helldev.javacord.platform.message.type.HellMessageType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Data
@RequiredArgsConstructor
public class HellMessage {

    @NonNull
    private final HellMessageType messageType;
    @NonNull
    private final Object value;

    private static PlaceholderContext placeholderContext = null;
    private boolean ephemeral = false;

    public HellMessage with(@NonNull String key, @NonNull Object value) {
        if (placeholderContext == null) {
            placeholderContext = PlaceholderContext.create();
        }
        placeholderContext.with(key, value);
        return this;
    }

    public HellMessage setEphemeral() {
        this.ephemeral = true;
        return this;
    }

    public static String apply(String text) {
        CompiledMessage compiledMessage = CompiledMessage.of(text);
        return placeholderContext != null ? placeholderContext.apply(compiledMessage) : "";
    }

    public void send(@NonNull Messageable messageable, @NonNull Consumer<Message> onSuccess, @NonNull Consumer<Throwable> onFailure) {
        CompletableFuture<Message> futureMessage = createMessageFuture(messageable);
        futureMessage
            .thenAccept(message -> {
                onSuccess.accept(message);
                resetPlaceholders();
            })
            .exceptionally(e -> {
                onFailure.accept(e);
                resetPlaceholders();
                return null;
            });
    }

    public CompletableFuture<Message> send(@NonNull Messageable messageable) {
        CompletableFuture<Message> futureMessage = createMessageFuture(messageable);
        futureMessage.thenRun(this::resetPlaceholders);
        return futureMessage;
    }

    private CompletableFuture<Message> createMessageFuture(@NonNull Messageable messageable) {
        switch (this.messageType) {
            case MESSAGE:
                String processedMessage = getProcessedMessage();
                return new MessageBuilder().setContent(processedMessage).send(messageable);
            case EMBED:
                HellEmbedBuilder processedEmbed = getProcessedEmbed();
                MessageBuilder messageBuilder = new MessageBuilder().setEmbed(processedEmbed.toEmbedBuilder());
                addComponentsToMessageBuilder(messageBuilder, processedEmbed);
                return messageBuilder.send(messageable);
            default:
                throw new OkaeriException("Unknown message type: " + this.messageType);
        }
    }

    public HellMessage applyToResponder(@NonNull InteractionImmediateResponseBuilder responder) {
        if (placeholderContext == null) {
            placeholderContext = PlaceholderContext.create();
        }
        applyToResponder(responder, placeholderContext);
        return this;
    }

    public HellMessage applyToResponder(@NonNull InteractionImmediateResponseBuilder responder, @NonNull PlaceholderContext context) {
        placeholderContext = context;
        Object processedValue = processValue();
        applyProcessedValueToResponder(responder, processedValue);
        resetPlaceholders();
        return this;
    }

    private String getProcessedMessage() {
        return placeholderContext != null ? apply((String) this.value) : (String) this.value;
    }

    private HellEmbedBuilder getProcessedEmbed() {
        return placeholderContext != null ? applyEmbedBuilder((HellEmbedBuilder) this.value) : (HellEmbedBuilder) this.value;
    }

    private Object processValue() {
        return switch (this.messageType) {
            case MESSAGE -> getProcessedMessage();
            case EMBED -> getProcessedEmbed();
        };
    }

    private void applyProcessedValueToResponder(@NonNull InteractionImmediateResponseBuilder responder, @NonNull Object processedValue) {
        switch (this.messageType) {
            case MESSAGE:
                responder.setContent((String) processedValue);
                break;
            case EMBED:
                HellEmbedBuilder hellEmbedBuilder = (HellEmbedBuilder) processedValue;
                responder.addEmbed(hellEmbedBuilder.toEmbedBuilder());
                addComponentsToResponder(responder, hellEmbedBuilder);
                break;
            default:
                throw new OkaeriException("Unknown message type: " + this.messageType);
        }

        if (this.ephemeral) {
            responder.setFlags(MessageFlag.EPHEMERAL);
        }
    }

    private void addComponentsToMessageBuilder(MessageBuilder messageBuilder, HellEmbedBuilder hellEmbedBuilder) {
        List<ActionRow> actionRows = new ArrayList<>();
        hellEmbedBuilder.getButtons().forEach(button -> actionRows.add(ActionRow.of(button.toButtonBuilder().build())));
        hellEmbedBuilder.getSelectMenus().forEach(selectMenu -> actionRows.add(ActionRow.of(selectMenu.getSelectMenuBuilder().build())));
        actionRows.forEach(messageBuilder::addComponents);
    }

    private void addComponentsToResponder(InteractionImmediateResponseBuilder responder, HellEmbedBuilder hellEmbedBuilder) {
        List<ActionRow> actionRows = new ArrayList<>();
        hellEmbedBuilder.getButtons().forEach(button -> actionRows.add(ActionRow.of(button.toButtonBuilder().build())));
        hellEmbedBuilder.getSelectMenus().forEach(selectMenu -> actionRows.add(ActionRow.of(selectMenu.getSelectMenuBuilder().build())));
        actionRows.forEach(responder::addComponents);
    }

    public HellMessage addButton(@NonNull HellEmbedButtonBuilder buttonBuilder) {
        if (!(this.value instanceof HellEmbedBuilder)) {
            throw new OkaeriException("Cannot add button to non-embed message type.");
        }

        HellEmbedBuilder embedBuilder = (HellEmbedBuilder) this.value;
        embedBuilder.addButton(buttonBuilder);
        return this;
    }

    public HellMessage addSelectMenu(@NonNull HellSelectMenuBuilder selectMenuBuilder) {
        if (!(this.value instanceof HellEmbedBuilder)) {
            throw new OkaeriException("Cannot add select menu to non-embed message type.");
        }

        HellEmbedBuilder embedBuilder = (HellEmbedBuilder) this.value;
        embedBuilder.addSelectMenu(selectMenuBuilder);
        return this;
    }

    public HellMessage addSelectOption(@NonNull String selectMenuId, @NonNull HellSelectOptionBuilder selectOptionBuilder) {
        if (!(this.value instanceof HellEmbedBuilder embedBuilder)) {
            throw new OkaeriException("Cannot add select option to non-embed message type.");
        }

        HellSelectMenuBuilder selectMenuBuilder = embedBuilder.getSelectMenuById(selectMenuId);

        if (selectMenuBuilder == null) {
            throw new OkaeriException("Select menu with id " + selectMenuId + " not found.");
        }

        selectMenuBuilder.addOption(selectOptionBuilder);
        return this;
    }

    public static HellEmbedBuilder applyEmbedBuilder(@NonNull HellEmbedBuilder from) {
        HellEmbedBuilder fixedEmbedBuilder = new HellEmbedBuilder();

        if (from.getTitle() != null) fixedEmbedBuilder.setTitle(apply(from.getTitle()));
        if (from.getDescription() != null) fixedEmbedBuilder.setDescription(apply(from.getDescription()));
        if (from.getUrl() != null) fixedEmbedBuilder.setUrl(apply(from.getUrl()));
        if (from.getFooterText() != null) {
            if (from.getFooterIconUrl() != null) {
                fixedEmbedBuilder.setFooter(apply(from.getFooterText()), apply(from.getFooterIconUrl()));
            } else {
                fixedEmbedBuilder.setFooter(apply(from.getFooterText()));
            }
        }
        if (from.getImageUrl() != null) fixedEmbedBuilder.setImage(apply(from.getImageUrl()));
        if (from.getAuthorName() != null) {
            String authorUrl = from.getAuthorUrl() != null ? apply(from.getAuthorUrl()) : "";
            String authorIconUrl = from.getAuthorIconUrl() != null ? apply(from.getAuthorIconUrl()) : "";

            fixedEmbedBuilder.setAuthor(
                apply(from.getAuthorName()),
                authorUrl,
                authorIconUrl
            );
        }
        if (from.getColor() != null) fixedEmbedBuilder.setColor(from.getColor());
        if (from.getThumbnailUrl() != null) fixedEmbedBuilder.setThumbnail(apply(from.getThumbnailUrl()));

        from.getFields().forEach(field -> fixedEmbedBuilder.addField(
            apply(field.getName()),
            apply(field.getValue()),
            field.isInline()
        ));

        from.getButtons().forEach(buttonBuilder -> {

            HellEmbedButtonBuilder newButtonBuilder = new HellEmbedButtonBuilder().setButtonStyle(buttonBuilder.getButtonStyle()).setCustomId(apply(buttonBuilder.getCustomId())).setLabel(apply(buttonBuilder.getLabel()));

            if (buttonBuilder.getUrl() != null) newButtonBuilder.setUrl(buttonBuilder.getUrl());
            if (buttonBuilder.isDisabled()) newButtonBuilder.setDisabled(true);
            if (buttonBuilder.getEmoji() != null) newButtonBuilder.setEmoji(buttonBuilder.getEmoji());

            fixedEmbedBuilder.addButton(newButtonBuilder);
        });

        from.getSelectMenus().forEach(selectMenuBuilder -> {

            HellSelectMenuBuilder newSelectMenuBuilder = new HellSelectMenuBuilder(selectMenuBuilder.getComponentType()).setCustomId(apply(selectMenuBuilder.getCustomId())).setPlaceholder(apply(selectMenuBuilder.getPlaceHolder()));

            if (selectMenuBuilder.getMinimumValues() != 1) newSelectMenuBuilder.setMinimumValues(selectMenuBuilder.getMinimumValues());
            if (selectMenuBuilder.getMaximumValues() != 3) newSelectMenuBuilder.setMaximumValues(selectMenuBuilder.getMaximumValues());
            if (selectMenuBuilder.getChannelTypes() != null) newSelectMenuBuilder.setChannelTypes(selectMenuBuilder.getChannelTypes());
            if (selectMenuBuilder.isDisabled()) newSelectMenuBuilder.setDisabled(true);

            fixedEmbedBuilder.addSelectMenu(newSelectMenuBuilder);

            selectMenuBuilder.getOptions().forEach(selectOptionBuilder -> {
                HellSelectOptionBuilder newSelectOptionBuilder = new HellSelectOptionBuilder().setLabel(apply(selectOptionBuilder.getLabel())).setValue(apply(selectOptionBuilder.getValue()));

                if (selectOptionBuilder.isDefaultOption()) newSelectOptionBuilder.setDefault(true);
                if (selectOptionBuilder.getDescription() != null) newSelectOptionBuilder.setDescription(apply(selectOptionBuilder.getDescription()));
                if (selectOptionBuilder.getEmojiAsUnicode() != null) newSelectOptionBuilder.setEmoji(selectOptionBuilder.getEmojiAsUnicode());
                if (selectOptionBuilder.getEmoji() != null) newSelectOptionBuilder.setEmoji(selectOptionBuilder.getEmoji());

                newSelectMenuBuilder.addOption(newSelectOptionBuilder);
            });
        });

        return fixedEmbedBuilder;
    }

    private void resetPlaceholders() {
        placeholderContext = null;
    }
}
