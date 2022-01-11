package corviolis.corviolisutils.mixin;

import com.mojang.authlib.GameProfile;
import corviolis.corviolisutils.interfaces.PlayerEntityInf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerEntityInf {

    private NbtList backupInventory = new NbtList();
    private boolean adminMode = false;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    public void setAdminMode(boolean b) {
        adminMode = b;
    }

    public boolean isAdminMode() {
        return adminMode;
    }

    public void swapInventories() {
        PlayerInventory inventory = getInventory();
        NbtList current = inventory.writeNbt(new NbtList());
        inventory.readNbt(backupInventory);
        backupInventory = current;
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void read(NbtCompound nbt, CallbackInfo ci) {
        backupInventory = (NbtList) nbt.get("backupInventory");
        adminMode = nbt.getBoolean("adminMode");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void write(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("backupInventory", backupInventory);
        nbt.putBoolean("adminMode", adminMode);
    }
}
