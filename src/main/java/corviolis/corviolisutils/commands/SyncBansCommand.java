package corviolis.corviolisutils.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.CorviolisUtils;
import corviolis.corviolisutils.services.api.airtable.AirtableAPI;
import corviolis.corviolisutils.services.api.airtable.Report;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class SyncBansCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("syncBans").requires(source -> source.hasPermissionLevel(4))
                .executes(SyncBansCommand::syncBans)
        );
    }

    private static int syncBans(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();
        BannedPlayerList bannedPlayers = CorviolisUtils.playerManager.getUserBanList();

        executor.sendMessage(new LiteralText("Fetching..."), false);
        for (Report report : AirtableAPI.getBans()) {
            GameProfile profile = CorviolisUtils.getPlayer(report.getOffenderUuid());
            BannedPlayerEntry entry = new BannedPlayerEntry(profile, null, report.getReporter(), null, report.getReason());
            bannedPlayers.add(entry);
        }
        executor.sendMessage(new LiteralText("Updated").formatted(Formatting.GREEN), false);
        return 1;
    }
}
