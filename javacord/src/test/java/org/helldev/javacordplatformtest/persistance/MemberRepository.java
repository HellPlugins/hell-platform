package org.helldev.javacordplatformtest.persistance;

import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.persistence.repository.DocumentRepository;
import eu.okaeri.persistence.repository.annotation.DocumentCollection;
import eu.okaeri.persistence.repository.annotation.DocumentIndex;
import eu.okaeri.persistence.repository.annotation.DocumentPath;
import eu.okaeri.platform.core.annotation.DependsOn;
import org.javacord.api.entity.user.User;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


@DependsOn(
    type = DocumentPersistence.class,
    name = "persistence"
)
@DocumentCollection(path = "userid", keyLength = 36, indexes = {
    @DocumentIndex(path = "name", maxLength = 24),
})
public interface MemberRepository extends DocumentRepository<String, Member> {

    @DocumentPath("name")
    Optional<Member> findByName(String name);

    default Member get(User user) {
        Member properties = this.findOrCreateByPath(user.getIdAsString());

        if (user.getIdAsString() != null) {
            properties.setName(user.getDiscriminatedName());
        }

        return properties;
    }
}
