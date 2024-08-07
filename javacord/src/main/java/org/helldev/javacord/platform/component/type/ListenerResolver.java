package org.helldev.javacord.platform.component.type;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;
import eu.okaeri.platform.core.annotation.Service;
import eu.okaeri.platform.core.component.ComponentHelper;
import eu.okaeri.platform.core.component.creator.ComponentCreator;
import eu.okaeri.platform.core.component.creator.ComponentResolver;
import eu.okaeri.platform.core.component.manifest.BeanManifest;
import lombok.NonNull;
import org.javacord.api.DiscordApi;
import org.javacord.api.listener.GloballyAttachableListener;

import java.lang.reflect.Method;

public class ListenerResolver implements ComponentResolver {

    @Inject("discordApi")
    private DiscordApi discordApi;

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return GloballyAttachableListener.class.isAssignableFrom(type);
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return false;
    }

    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        long start = System.currentTimeMillis();
        Object result = injector.createInstance(manifest.getType());

        if (result instanceof GloballyAttachableListener) {
            discordApi.addListener((GloballyAttachableListener) result);
        } else {
            throw new IllegalArgumentException("The provided class does not implement GloballyAttachableListener: " + manifest.getType().getName());
        }

        long took = System.currentTimeMillis() - start;
        if (took > 1) {
            creator.log(ComponentHelper.buildComponentMessage()
                .type("Added listener component")
                .name(manifest.getType().getSimpleName())
                .took(took)
                .build());
        }
        creator.increaseStatistics("listeners", 1);

        return result;
    }
}
