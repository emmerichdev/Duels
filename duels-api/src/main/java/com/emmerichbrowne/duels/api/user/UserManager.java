package com.emmerichbrowne.duels.api.user;

import com.emmerichbrowne.duels.api.kit.Kit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface UserManager {


    boolean isLoaded();


    @Nullable
    User get(@NotNull final String name);


    @Nullable
    User get(@NotNull final UUID uuid);


    @Nullable
    User get(@NotNull final Player player);


    @Nullable
    TopEntry getTopWins();


    @Nullable
    TopEntry getTopLosses();


    @Nullable
    TopEntry getTopRatings();


    @Nullable
    TopEntry getTopRatings(@NotNull final Kit kit);

    Collection<User> getAllUsers();

    class TopEntry {

        private final long creation;
        private final String type, identifier;
        private final List<TopData> data;

        public TopEntry(@NotNull final String type, @NotNull final String identifier, @NotNull final List<TopData> data) {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(identifier, "identifier");
            Objects.requireNonNull(data, "data");
            this.creation = System.currentTimeMillis();
            this.type = type;
            this.identifier = identifier;
            this.data = data;
        }

        public long getCreation() {
            return creation;
        }

        public String getType() {
            return type;
        }

        public String getIdentifier() {
            return identifier;
        }

        public List<TopData> getData() {
            return data;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            final TopEntry topEntry = (TopEntry) other;
            return Objects.equals(type, topEntry.type) && Objects.equals(identifier, topEntry.identifier) && Objects.equals(data, topEntry.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, identifier, data);
        }
    }

    record TopData(UUID uuid, String name, int value) implements Comparable<TopData> {

            public TopData(@NotNull final UUID uuid, @NotNull final String name, final int value) {
                Objects.requireNonNull(uuid, "uuid");
                Objects.requireNonNull(name, "name");
                this.uuid = uuid;
                this.name = name;
                this.value = value;
            }

            @Override
            public int compareTo(@NotNull final TopData data) {
                Objects.requireNonNull(data, "data");
                return Integer.compare(value, data.value);
            }

    }
}
