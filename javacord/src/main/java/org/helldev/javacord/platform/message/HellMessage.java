package org.helldev.javacord.platform.message;

import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.helldev.javacord.platform.message.embed.HellEmbedBuilder;
import org.helldev.javacord.platform.message.type.HellMessageType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Represents a message that can be sent to a Discord channel or user.
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class HellMessage {

    @NonNull
    private final HellMessageType messageType;
    @NonNull
    private final Object value;

    private PlaceholderContext placeholderContext;

    /**
     * Adds a placeholder value.
     *
     * @param key   The placeholder key.
     * @param value The placeholder value.
     * @return The HellMessage instance with the updated placeholder.
     */
    public HellMessage with(@NonNull String key, @NonNull Object value) {
        if (this.placeholderContext == null) {
            this.placeholderContext = PlaceholderContext.of(CompiledMessage.of(this.value.toString()));
        }
        this.placeholderContext.with(key, value);
        return this;
    }

    /**
     * Sends the message to a specified target.
     *
     * @param messageable The target to send the message to.
     * @param onSuccess   Callback for successful sending.
     * @param onFailure   Callback for failed sending.
     */
    public void send(@NonNull Messageable messageable, @NonNull Consumer<Message> onSuccess, @NonNull Consumer<Throwable> onFailure) {
        switch (this.messageType) {
            case MESSAGE:
                String processedMessage = placeholderContext != null ? placeholderContext.apply() : (String) this.value;
                new MessageBuilder()
                    .setContent(processedMessage)
                    .send(messageable)
                    .thenAccept(message -> {
                        onSuccess.accept(message);
                        resetPlaceholders();
                    })
                    .exceptionally(e -> {
                        onFailure.accept(e);
                        resetPlaceholders();
                        return null;
                    });
                break;
            case EMBED:
                HellEmbedBuilder processedEmbed = placeholderContext != null ? applyEmbedBuilder((HellEmbedBuilder) this.value, placeholderContext) : (HellEmbedBuilder) this.value;
                MessageBuilder messageBuilder = new MessageBuilder()
                    .setEmbed(processedEmbed.toEmbedBuilder());
                addComponentsToMessageBuilder(messageBuilder, processedEmbed);
                messageBuilder.send(messageable)
                    .thenAccept(message -> {
                        onSuccess.accept(message);
                        resetPlaceholders();
                    })
                    .exceptionally(e -> {
                        onFailure.accept(e);
                        resetPlaceholders();
                        return null;
                    });
                break;
            default:
                throw new OkaeriException("Cannot resolve unknown message-type: " + this.messageType);
        }
    }

    /**
     * Sends the message to a specified target.
     *
     * @param messageable The target to send the message to.
     * @return A CompletableFuture that will be completed with the sent message.
     */
    public CompletableFuture<Message> send(@NonNull Messageable messageable) {
        CompletableFuture<Message> futureMessage;
        switch (this.messageType) {
            case MESSAGE:
                String processedMessage = placeholderContext != null ? placeholderContext.apply() : (String) this.value;
                futureMessage = new MessageBuilder()
                    .setContent(processedMessage)
                    .send(messageable);
                break;
            case EMBED:
                HellEmbedBuilder processedEmbed = placeholderContext != null ? applyEmbedBuilder((HellEmbedBuilder) this.value, placeholderContext) : (HellEmbedBuilder) this.value;
                MessageBuilder messageBuilder = new MessageBuilder()
                    .setEmbed(processedEmbed.toEmbedBuilder());
                addComponentsToMessageBuilder(messageBuilder, processedEmbed);
                futureMessage = messageBuilder.send(messageable);
                break;
            default:
                throw new OkaeriException("Cannot resolve unknown message-type: " + this.messageType);
        }
        futureMessage.thenRun(this::resetPlaceholders);
        return futureMessage;
    }

    /**
     * Applies the message to an interaction responder.
     *
     * @param responder The interaction responder.
     */
    public void applyToResponder(@NonNull InteractionImmediateResponseBuilder responder) {
        Object processedValue = processValue();
        applyProcessedValueToResponder(responder, processedValue);
        resetPlaceholders();
    }

    /**
     * Adds components (buttons or select menus) to the message builder.
     *
     * @param messageBuilder   The message builder.
     * @param hellEmbedBuilder The embed builder.
     */
    private void addComponentsToMessageBuilder(MessageBuilder messageBuilder, HellEmbedBuilder hellEmbedBuilder) {
        if (!hellEmbedBuilder.getButtons().isEmpty() && !hellEmbedBuilder.getSelectMenus().isEmpty()) {
            throw new IllegalStateException("Cannot have both buttons and select menus in the same message.");
        }
        List<ActionRow> actionRows = new ArrayList<>();
        hellEmbedBuilder.getButtons().forEach(button -> actionRows.add(ActionRow.of(button.toButtonBuilder().build())));
        hellEmbedBuilder.getSelectMenus().forEach(selectMenu -> actionRows.add(ActionRow.of(selectMenu.getSelectMenuBuilder().build())));
        actionRows.forEach(messageBuilder::addComponents);
    }

    /**
     * Applies the message to an interaction responder with a placeholder context.
     *
     * @param responder The interaction responder.
     */
    public void applyToResponder(@NonNull InteractionImmediateResponseBuilder responder, @NonNull PlaceholderContext context) {
        this.placeholderContext = context;
        Object processedValue = processValue();
        applyProcessedValueToResponder(responder, processedValue);
        resetPlaceholders();
    }

    /**
     * Processes the value based on the placeholder context.
     *
     * @return The processed value.
     */
    private Object processValue() {
        if (this.messageType == HellMessageType.MESSAGE) {
            return placeholderContext != null ? placeholderContext.apply() : this.value;
        } else if (this.messageType == HellMessageType.EMBED) {
            return placeholderContext != null ? applyEmbedBuilder((HellEmbedBuilder) this.value, placeholderContext) : this.value;
        } else {
            throw new OkaeriException("Cannot process unknown message-type: " + this.messageType);
        }
    }

    /**
     * Applies processed value to the responder.
     *
     * @param responder       The interaction responder.
     * @param processedValue The processed value.
     */
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
                throw new OkaeriException("Cannot resolve unknown message-type: " + this.messageType);
        }
    }

    /**
     * Adds components (buttons or select menus) to the interaction responder.
     *
     * @param responder        The interaction responder.
     * @param hellEmbedBuilder The embed builder.
     */
    private void addComponentsToResponder(InteractionImmediateResponseBuilder responder, HellEmbedBuilder hellEmbedBuilder) {
        if (!hellEmbedBuilder.getButtons().isEmpty() && !hellEmbedBuilder.getSelectMenus().isEmpty()) {
            throw new IllegalStateException("Cannot have both buttons and select menus in the same message.");
        }
        List<ActionRow> actionRows = new ArrayList<>();
        hellEmbedBuilder.getButtons().forEach(button -> actionRows.add(ActionRow.of(button.toButtonBuilder().build())));
        hellEmbedBuilder.getSelectMenus().forEach(selectMenu -> actionRows.add(ActionRow.of(selectMenu.getSelectMenuBuilder().build())));
        actionRows.forEach(responder::addComponents);
    }

    /**
     * Applies placeholders to an embed builder.
     *
     * @param from               The original embed builder.
     * @param placeholderContext The placeholder context.
     * @return The processed embed builder.
     */
    public static HellEmbedBuilder applyEmbedBuilder(@NonNull HellEmbedBuilder from, @NonNull PlaceholderContext placeholderContext) {
        HellEmbedBuilder fixedEmbedBuilder = new HellEmbedBuilder();

        Optional.ofNullable(from.getTitle()).ifPresent(title -> fixedEmbedBuilder.setTitle(placeholderContext.with("title", title).apply()));
        Optional.ofNullable(from.getDescription()).ifPresent(description -> fixedEmbedBuilder.setDescription(placeholderContext.with("description", description).apply()));
        Optional.ofNullable(from.getUrl()).ifPresent(url -> fixedEmbedBuilder.setUrl(placeholderContext.with("url", url).apply()));

        if (from.getFooterText() != null) {
            if (from.getFooterIconUrl() != null) {
                fixedEmbedBuilder.setFooter(
                    placeholderContext.with("footerText", from.getFooterText()).apply(),
                    placeholderContext.with("footerIconUrl", from.getFooterIconUrl()).apply()
                );
            } else {
                fixedEmbedBuilder.setFooter(placeholderContext.with("footerText", from.getFooterText()).apply());
            }
        }

        Optional.ofNullable(from.getImageUrl()).ifPresent(imageUrl -> fixedEmbedBuilder.setImage(placeholderContext.with("imageUrl", imageUrl).apply()));
        Optional.ofNullable(from.getThumbnailUrl()).ifPresent(thumbnailUrl -> fixedEmbedBuilder.setThumbnail(placeholderContext.with("thumbnailUrl", thumbnailUrl).apply()));

        if (from.getAuthorName() != null) {
            fixedEmbedBuilder.setAuthor(
                placeholderContext.with("authorName", from.getAuthorName()).apply(),
                placeholderContext.with("authorUrl", from.getAuthorUrl()).apply(),
                placeholderContext.with("authorIconUrl", from.getAuthorIconUrl()).apply()
            );
        }

        from.getFields().forEach(field -> fixedEmbedBuilder.addField(
            placeholderContext.with("fieldName", field.getName()).apply(),
            placeholderContext.with("fieldValue", field.getValue()).apply(),
            field.isInline()
        ));

        return fixedEmbedBuilder;
    }

    /**
     * Resets the placeholder context.
     */
    private void resetPlaceholders() {
        this.placeholderContext = null;
    }
}
