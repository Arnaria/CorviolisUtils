package corviolis.corviolisutils.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.CorviolisUtils;
import corviolis.corviolisutils.services.api.NocodbAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.UUID;

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
        for (JsonElement element : NocodbAPI.getBans()) {
            JsonObject ban = element.getAsJsonObject();
            GameProfile profile = CorviolisUtils.getPlayer(UUID.fromString(ban.get("Offender_Id").getAsString()));
            BannedPlayerEntry entry = new BannedPlayerEntry(profile, null, ban.get("Reporter_Name").getAsString(), null, ban.get("reason").getAsString());
            bannedPlayers.add(entry);
            executor.sendMessage(new LiteralText("Banned: " + profile.getName()), false);
        }
        executor.sendMessage(new LiteralText("Done").formatted(Formatting.GREEN), false);
        return 1;
    }
}
