package corviolis.corviolisutils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import corviolis.corviolisutils.commands.AdminModeCommand;
import corviolis.corviolisutils.commands.ReportCommand;
import corviolis.corviolisutils.commands.RulesCommand;
import corviolis.corviolisutils.commands.SyncBansCommand;
import corviolis.corviolisutils.services.api.NocodbAPI;
import corviolis.corviolisutils.services.api.TodoistAPI;
import corviolis.corviolisutils.util.Settings;
import mc.microconfig.MicroConfig;
import mrnavastar.sqlib.api.databases.Database;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.UserCache;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class CorviolisUtils implements ModInitializer {
    public static String MODID = "CorviolisUtils";
    public static Database database;
    public static Settings settings;
    public static PlayerManager playerManager;
    private static UserCache userCache;
    public static Scoreboard scoreboard;

    @Override
    public void onInitialize() {
        settings = MicroConfig.getOrCreate(MODID, new Settings());

        if (!settings.databaseDirectory.equals("/my/dir")) {
            database = new SQLiteDatabase(settings.databaseName, settings.databaseDirectory);

            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                TodoistAPI.init();
                NocodbAPI.init();

                userCache = server.getUserCache();
                playerManager = server.getPlayerManager();
                scoreboard = server.getScoreboard();

                //admin mode stuff
                scoreboard.addObjective("Reports", ScoreboardCriterion.AIR, new LiteralText("Reports"), ScoreboardCriterion.RenderType.INTEGER);
            });

            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                AdminModeCommand.register(dispatcher);
                ReportCommand.register(dispatcher);
                SyncBansCommand.register(dispatcher);
                RulesCommand.register(dispatcher);
            });
        }
    }

    public static GameProfile getPlayer(UUID uuid) {
        Optional<GameProfile> profile = userCache.getByUuid(uuid);
        return profile.orElse(null);
    }
}
