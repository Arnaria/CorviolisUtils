package corviolis.corviolisutils.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import corviolis.corviolisutils.services.api.NocodbAPI;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.PardonCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(PardonCommand.class)
public class PardonCommandMixin {

    @Inject(method = "pardon", at = @At("HEAD"))
    private static void syncBans(ServerCommandSource source, Collection<GameProfile> targets, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        for (GameProfile target : targets) {
            NocodbAPI.removeBan(target.getId());
        }
    }
}
