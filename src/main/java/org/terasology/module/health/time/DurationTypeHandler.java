// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.time;

import org.terasology.engine.persistence.typeHandling.RegisterTypeHandler;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.PersistedDataSerializer;
import org.terasology.persistence.typeHandling.TypeHandler;

import java.util.Optional;

@RegisterTypeHandler
public class DurationTypeHandler extends TypeHandler<Duration> {
    @Override
    protected PersistedData serializeNonNull(Duration value, PersistedDataSerializer serializer) {
        return serializer.serialize(value.getInMillis());
    }

    @Override
    public Optional<Duration> deserialize(PersistedData data) {
        //TODO: allow non-number format, e.g., "1.2s" or "400ms".
        if (data.isNumber()) {
            return Optional.of(Duration.fromMillis(data.getAsLong()));
        }
        return Optional.empty();
    }
}
