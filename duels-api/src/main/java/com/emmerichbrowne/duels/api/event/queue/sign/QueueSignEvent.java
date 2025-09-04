package com.emmerichbrowne.duels.api.event.queue.sign;

import com.emmerichbrowne.duels.api.event.SourcedEvent;
import com.emmerichbrowne.duels.api.queue.sign.QueueSign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class QueueSignEvent extends SourcedEvent {

    private final Player source;
    private final QueueSign queueSign;

    QueueSignEvent(@NotNull final Player source, @NotNull final QueueSign queueSign) {
        super(source);
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(queueSign, "queueSign");
        this.source = source;
        this.queueSign = queueSign;
    }

    @NotNull
    @Override
    public Player getSource() {
        return source;
    }

    @NotNull
    public QueueSign getQueueSign() {
        return queueSign;
    }
}
