package corviolis.corviolisutils.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.services.api.NocodbAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class RulesCommand {

    private static JsonArray rules = NocodbAPI.getRules();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("rules")
                .executes(RulesCommand::viewRules)

                .then(CommandManager.literal("update").requires(source -> source.hasPermissionLevel(4))
                    .executes(RulesCommand::updateRules))
        );
    }

    private static int viewRules(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        executor.sendMessage(new LiteralText("<-------------SERVER-RULES------------->").formatted(Formatting.AQUA), false);
        for (JsonElement element : rules) {
            JsonObject rule = element.getAsJsonObject();
            MutableText id = new LiteralText("  [" + rule.get("id").getAsString() + "]").formatted(Formatting.GREEN);
            executor.sendMessage(id.append(new LiteralText(" | " + rule.get("Title").getAsString())), false);
        }
        executor.sendMessage(new LiteralText(""), false);
        executor.sendMessage(new LiteralText("  Please report any violations using /report"), false);
        executor.sendMessage(new LiteralText("<-------------------------------------->").formatted(Formatting.AQUA), false);
        return 1;
    }

    private static int updateRules(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        executor.sendMessage(new LiteralText("Fetching..."), false);
        rules = NocodbAPI.getRules();
        executor.sendMessage(new LiteralText("Done").formatted(Formatting.GREEN), false);
        return 1;
    }
}