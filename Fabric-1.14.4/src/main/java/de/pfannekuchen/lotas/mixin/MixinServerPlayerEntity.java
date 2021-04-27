package de.pfannekuchen.lotas.mixin;

import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import de.pfannekuchen.lotas.mixin.accessors.ServerPlayerEntityAccessor;
import de.pfannekuchen.lotas.utils.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
    @Final
    @Shadow
    public MinecraftServer server;

    public MixinServerPlayerEntity(World world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    private boolean method_14230() {
        return this.server.isPvpEnabled();
    }

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> returnable) {
        if (ConfigManager.getBoolean("tools", "takeDamage")) return;
        boolean bl = this.server.isDedicated() && this.method_14230() && "fall".equals(source.name);
        AtomicBoolean flag = new AtomicBoolean(false);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (((ServerPlayerEntityAccessor) player).getField_13998() <= 0) {
                ((ServerPlayerEntityAccessor) player).setField_13998(60);
                flag.set(true);
            }
        });
        if (!bl && source != DamageSource.OUT_OF_WORLD) {
            if (flag.get()) {
                MinecraftClient.getInstance().player.setVelocity(0, 0, 0);
                MinecraftClient.getInstance().player.velocityModified = true;
                MinecraftClient.getInstance().player.prevX = this.x;
                MinecraftClient.getInstance().player.prevY = this.y;
                MinecraftClient.getInstance().player.prevZ = this.z;
            }
            returnable.setReturnValue(false);
            returnable.cancel();
        }
    }
}
