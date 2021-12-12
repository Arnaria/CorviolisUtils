package corviolis.corviolisutils.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.services.api.airtable.AirtableAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(BanCommand.class)
public class BanCommandMixin {

    @Inject(method = "ban", at = @At("HEAD"))
    private static void logBan(ServerCommandSource source, Collection<GameProfile> targets, Text reason, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        PlayerEntity executor = source.getPlayer();

        for (GameProfile p : targets) {
            AirtableAPI.createReport(executor.getEntityName(), p.getName(), reason.asString(), "ban");
        }
    }
}
