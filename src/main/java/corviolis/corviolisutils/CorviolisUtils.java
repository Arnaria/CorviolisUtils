package corviolis.corviolisutils;

import com.mojang.brigadier.CommandDispatcher;
import corviolis.corviolisutils.commands.ReportCommand;
import corviolis.corviolisutils.commands.RulesCommand;
import corviolis.corviolisutils.services.api.airtable.AirtableAPI;
import corviolis.corviolisutils.services.api.TodoistAPI;
import corviolis.corviolisutils.util.Settings;
import mc.microconfig.MicroConfig;
import mrnavastar.sqlib.api.databases.Database;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;

public class CorviolisUtils implements ModInitializer {
    public static String MODID = "CorviolisUtils";
    public static Database database;
    public static Settings settings;

    @Override
    public void onInitialize() {
        database = new SQLiteDatabase("UTILS", "/home/ethan/Documents/Programming/Database");
        settings = MicroConfig.getOrCreate(MODID, new Settings());

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            TodoistAPI.init();
            AirtableAPI.init();

            CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
            ReportCommand.register(dispatcher);
            RulesCommand.register(dispatcher);
        });
    }
}
