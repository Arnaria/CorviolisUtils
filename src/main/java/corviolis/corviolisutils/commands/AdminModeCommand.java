package corviolis.corviolisutils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.CorviolisUtils;
import corviolis.corviolisutils.interfaces.PlayerEntityInf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;

public class AdminModeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("adminMode").requires(source -> source.hasPermissionLevel(4))
            .executes(AdminModeCommand::adminMode)
        );
    }

    private static int adminMode(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity executor = context.getSource().getPlayer();

        if (((PlayerEntityInf) executor).isAdminMode()) {
            ((PlayerEntityInf) executor).setAdminMode(false);
            ((PlayerEntityInf) executor).swapInventories();
            executor.changeGameMode(GameMode.DEFAULT);
            executor.sendMessage(new LiteralText("Admin mode disabled"), false);
        } else {
            ((PlayerEntityInf) executor).setAdminMode(true);
            ((PlayerEntityInf) executor).swapInventories();
            executor.changeGameMode(GameMode.CREATIVE);
            executor.sendMessage(new LiteralText("Admin mode enabled"), false);
        }
        return 1;
    }
}
