package org.helldev.javacordplatformtest.persistance;

import eu.okaeri.persistence.document.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class Member extends Document {

    private String name;

    public UUID getUniqueId() {
        return this.getPath().toUUID();
    }
}
