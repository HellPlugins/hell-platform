package org.helldev.javacord.platform.component;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.standalone.component.ApplicationCreatorRegistry;
import org.helldev.javacord.platform.component.type.CommandResolver;
import org.helldev.javacord.platform.component.type.ListenerResolver;

public class BotCreatorRegistry extends ApplicationCreatorRegistry {

    @Inject
    public BotCreatorRegistry(Injector injector) {
        super(injector);
    }

    @Override
    public void register() {
        this.register(ListenerResolver.class);
        this.register(CommandResolver.class);
    }
}
