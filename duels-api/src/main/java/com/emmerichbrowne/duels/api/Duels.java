package com.emmerichbrowne.duels.api;

import com.emmerichbrowne.duels.api.arena.ArenaManager;
import com.emmerichbrowne.duels.api.command.SubCommand;
import com.emmerichbrowne.duels.api.kit.KitManager;
import com.emmerichbrowne.duels.api.queue.DQueueManager;
import com.emmerichbrowne.duels.api.queue.sign.QueueSignManager;
import com.emmerichbrowne.duels.api.spectate.SpectateManager;
import com.emmerichbrowne.duels.api.user.UserManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.scheduling.ScheduledTask;


public interface Duels extends Plugin {

    @NotNull
    UserManager getUserManager();

    @NotNull
    ArenaManager getArenaManager();

    @NotNull
    KitManager getKitManager();

    @NotNull
    SpectateManager getSpectateManager();

    @NotNull
    DQueueManager getQueueManager();

    @NotNull
    QueueSignManager getQueueSignManager();

    boolean registerSubCommand(@NotNull final String command, @NotNull final SubCommand subCommand);

    void registerListener(@NotNull final Listener listener);

    ScheduledTask doSync(@NotNull final Runnable task);

    ScheduledTask doSyncAfter(@NotNull final Runnable task, long delay);

    ScheduledTask doSyncRepeat(@NotNull final Runnable task, long delay, long interval);

    ScheduledTask doAsync(@NotNull final Runnable task);

    ScheduledTask doAsyncAfter(@NotNull final Runnable task, long delay);

    ScheduledTask doAsyncRepeat(@NotNull final Runnable task, long delay, long interval);

    void cancelTask(@NotNull final ScheduledTask task);

    void info(@NotNull final String message);

    void warn(@NotNull final String message);

    void error(@NotNull final String message);

    void error(@NotNull final String message, @NotNull Throwable thrown);

    String getVersion();
}
