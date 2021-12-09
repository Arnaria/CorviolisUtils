package corviolis.corviolisutils.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.CorviolisUtils;
import corviolis.corviolisutils.services.api.airtable.AirtableAPI;
import corviolis.corviolisutils.services.api.TodoistAPI;
import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;

public class ReportCommand {

    private static final Table reportingPerms = CorviolisUtils.database.createTable("ReportingPermissions");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("report")
                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                .then(CommandManager.argument("reason", StringArgumentType.greedyString())
                .executes(context -> fileReport(context, GameProfileArgumentType.getProfileArgument(context, "player"), StringArgumentType.getString(context, "reason")))))
        );

        dispatcher.register(CommandManager.literal("reporting").requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                .then(CommandManager.argument("allowedToReport", BoolArgumentType.bool())
                .executes(context -> setReportingPermission(context, GameProfileArgumentType.getProfileArgument(context, "player"), BoolArgumentType.getBool(context, "allowedToReport")))))
        );
    }

    private static int fileReport(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets, String reason) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        reportingPerms.beginTransaction();
        DataContainer player = reportingPerms.get(executor.getUuid());
        if (player == null) {
            player = reportingPerms.createDataContainer(executor.getUuid());
            player.put("ALLOWED_TO_REPORT", true);
        }

        if (player.getBoolean("ALLOWED_TO_REPORT")) {
            for (GameProfile profile : targets) {
                String offender = profile.getName();
                long time = player.getLong(offender);

                if (profile.getId().equals(executor.getUuid())) {
                    executor.sendMessage(new LiteralText("You can't report yourself").formatted(Formatting.DARK_RED), false);
                    return 1;
                }

                if (time != -0 && new Date().compareTo(new Date(time)) < 0) {
                    executor.sendMessage(new LiteralText("You have already reported this player recently").formatted(Formatting.RED), false);
                } else {
                    if (time != -0) player.dropLong(offender);
                    player.put(offender, new Date().getTime());

                    executor.sendMessage(new LiteralText("Report Sent").formatted(Formatting.GREEN), false);
                    AirtableAPI.createReport(executor.getEntityName(), offender, reason);
                    TodoistAPI.createReport(executor.getEntityName(), offender, reason);
                }
            }
        } else executor.sendMessage(new LiteralText("You are not allowed to report. Please contact an admin if you believe this is a mistake").formatted(Formatting.RED), false);

        reportingPerms.endTransaction();
        return 1;
    }

    private static int setReportingPermission(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets, boolean allowedToReport) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        for (GameProfile profile : targets) {
            DataContainer player = reportingPerms.get(profile.getId());
            if (player == null) player = reportingPerms.createDataContainer(profile.getId());

            player.put("ALLOWED_TO_REPORT", allowedToReport);

            if (allowedToReport) executor.sendMessage(new LiteralText(profile.getName() + " is now allowed to makes report"), false);
            else executor.sendMessage(new LiteralText(profile.getName() + " is no longer allowed to make reports"), false);
        }
        return 1;
    }
}