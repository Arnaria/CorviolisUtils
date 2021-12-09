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
import java.util.UUID;

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

                .then(CommandManager.literal("info")
                .executes(context -> viewInfo(context, GameProfileArgumentType.getProfileArgument(context, "player"))))

                .then(CommandManager.literal("allowedToReport")
                .then(CommandManager.argument("allowedToReport", BoolArgumentType.bool())
                .executes(context -> setReportingPermission(context, GameProfileArgumentType.getProfileArgument(context, "player"), BoolArgumentType.getBool(context, "allowedToReport"))))))
        );
    }

    private static DataContainer getProfile(UUID uuid) {
        reportingPerms.beginTransaction();
        DataContainer profile = reportingPerms.get(uuid);
        if (profile == null) {
            profile = reportingPerms.createDataContainer(uuid);
            profile.put("ALLOWED_TO_REPORT", true);
            profile.put("REPORTS", 0);
        }
        reportingPerms.endTransaction();
        return profile;
    }

    private static int fileReport(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets, String reason) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();
        DataContainer profile = getProfile(executor.getUuid());

        if (profile.getBoolean("ALLOWED_TO_REPORT")) {
            for (GameProfile p : targets) {
                if (p.getId().equals(executor.getUuid())) {
                    executor.sendMessage(new LiteralText("You can't report yourself").formatted(Formatting.DARK_RED), false);
                    return 1;
                }

                String offender = p.getName();
                long time = profile.getLong(offender);

                if (time + CorviolisUtils.settings.reportDelay <= new Date().getTime()) {
                    if (time != -0) profile.dropLong(offender);
                    profile.put(offender, new Date().getTime());
                    profile.put("REPORTS", profile.getInt("REPORTS") + 1);

                    executor.sendMessage(new LiteralText("Report Sent").formatted(Formatting.GREEN), false);
                    AirtableAPI.createReport(executor.getEntityName(), offender, reason);
                    TodoistAPI.createReport(executor.getEntityName(), offender, reason);

                } else executor.sendMessage(new LiteralText("You have already reported this player recently").formatted(Formatting.RED), false);
            }
        } else executor.sendMessage(new LiteralText("You are not allowed to report. Please contact an admin if you believe this is a mistake").formatted(Formatting.RED), false);
        return 1;
    }

    private static int setReportingPermission(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets, boolean allowedToReport) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        for (GameProfile p : targets) {
            DataContainer profile = getProfile(p.getId());
            profile.put("ALLOWED_TO_REPORT", allowedToReport);
            if (allowedToReport) executor.sendMessage(new LiteralText(p.getName() + " is now allowed to make reports"), false);
            else executor.sendMessage(new LiteralText(p.getName() + " is no longer allowed to make reports"), false);
        }
        return 1;
    }

    private static int viewInfo(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets) throws CommandSyntaxException {
        PlayerEntity executor = context.getSource().getPlayer();

        for (GameProfile p : targets) {
            DataContainer profile = getProfile(p.getId());
            executor.sendMessage(new LiteralText(p.getName() + ":").formatted(Formatting.AQUA), false);
            executor.sendMessage(new LiteralText("Allowed to report: " + profile.getBoolean("ALLOWED_TO_REPORT")), false);
            executor.sendMessage(new LiteralText("Reports: " + profile.getInt("REPORTS")), false);
        }
        return 1;
    }
}