package corviolis.corviolisutils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.services.api.airtable.AirtableAPI;
import corviolis.corviolisutils.services.api.airtable.Rule;
import corviolis.corviolisutils.util.IdSorter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class RulesCommand {

    private static ArrayList<Rule> rules = AirtableAPI.getRules();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("rules")
                .executes(RulesCommand::viewRules)

                .then(CommandManager.literal("update").requires(source -> source.hasPermissionLevel(4))
                    .executes(RulesCommand::updateRules))
        );

        rules.sort(new IdSorter());
    }

    private static int viewRules(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        executor.sendMessage(new LiteralText("<-------------SERVER-RULES------------->").formatted(Formatting.AQUA), false);
        for (Rule rule : rules) {
            MutableText id = new LiteralText("  [" + rule.getId() + "]").formatted(Formatting.GREEN);
            executor.sendMessage(id.append(new LiteralText(" | " + rule.getTitle())), false);
        }
        executor.sendMessage(new LiteralText(""), false);
        executor.sendMessage(new LiteralText("  Please report any violations using /report"), false);
        executor.sendMessage(new LiteralText("<-------------------------------------->").formatted(Formatting.AQUA), false);
        return 1;
    }

    private static int updateRules(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        executor.sendMessage(new LiteralText("Fetching..."), false);
        rules = AirtableAPI.getRules();
        rules.sort(new IdSorter());
        executor.sendMessage(new LiteralText("Updated").formatted(Formatting.GREEN), false);
        return 1;
    }
}