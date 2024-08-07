package org.helldev.javacord.platform.command;

import lombok.Getter;
import lombok.NonNull;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

@Getter
public abstract class Command {

    private final String name;
    private final String description;
    private final SlashCommandBuilder slashCommandBuilder;

    public Command(@NonNull String name, @NonNull String description) {
        this.name = name;
        this.description = description;
        this.slashCommandBuilder = SlashCommand.with(name, description);
    }

    public abstract @NonNull SlashCommandCreateListener respond();

    public @NonNull AutocompleteCreateListener autocomplete() {
        return (event) -> {
        };
    }


}
