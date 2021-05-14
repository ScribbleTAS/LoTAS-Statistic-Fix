package de.pfannekuchen.lotas.mixin.patches;

import java.io.File;
import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.pfannekuchen.lotas.core.MCVer;
import de.pfannekuchen.lotas.core.utils.ConfigUtils;
import de.pfannekuchen.lotas.core.utils.EventUtils.Timer;
import de.pfannekuchen.lotas.mixin.accessors.AccessorEntityPlayerMP;
import de.pfannekuchen.lotas.mixin.accessors.AccessorMinecraftClient;
import de.pfannekuchen.lotas.taschallenges.ChallengeLoader;
import de.pfannekuchen.lotas.taschallenges.ChallengeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
//#if MC>=10900
import net.minecraft.util.text.TextComponentString;
//#else
//$$ import net.minecraft.util.ChatComponentText;
//#endif
//#if MC>=11000
import net.minecraft.world.GameType;
//#else
//$$ import net.minecraft.world.WorldSettings.GameType;
//#endif
import net.minecraft.world.chunk.storage.AnvilSaveConverter;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMPPatch  {

	@Shadow
	public int respawnInvulnerabilityTicks;
	@Shadow
	public MinecraftServer mcServer;

	public boolean takenDamage = false;
	
	@Shadow
	public abstract boolean canPlayersAttack();

	@Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isDedicatedServer()Z", shift = At.Shift.AFTER))
	public void injectAfterFlag(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
		boolean flag = this.mcServer.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(source.damageType);
		
		//#if MC>=11100
		if (!ConfigUtils.getBoolean("tools", "takeDamage")) Minecraft.getMinecraft().getIntegratedServer().getPlayerList().getPlayers().forEach(p -> {
		//#else
		//#if MC>=10900
//$$ 		if (!ConfigUtils.getBoolean("tools", "takeDamage")) Minecraft.getMinecraft().getIntegratedServer().getPlayerList().getPlayerList().forEach(p -> {	
		//#else
//$$ 		if (!ConfigUtils.getBoolean("tools", "takeDamage")) Minecraft.getMinecraft().getIntegratedServer().getConfigurationManager().getPlayerList().forEach(p -> {	
		//#endif
		//#endif	
			if (((AccessorEntityPlayerMP) p).respawnInvulnerabilityTicks() <= 1 && p.dimension != 1) {
				((AccessorEntityPlayerMP) p).respawnInvulnerabilityTicks(60);
				takenDamage = true;
			}
		});
		//#if MC>=11100
		if (!flag && this.respawnInvulnerabilityTicks > 0 && source != DamageSource.OUT_OF_WORLD) {
		//#else
//$$ 		if (!flag && this.respawnInvulnerabilityTicks > 0 && source != DamageSource.outOfWorld) {	
		//#endif
			try {
				if (takenDamage) {
					takenDamage = false;
					EntityPlayerSP player = MCVer.player(Minecraft.getMinecraft());
					player.motionX = 0;
					player.motionY = 0;
					player.motionZ = 0;
					player.prevPosX = player.posX;
					player.prevPosY = player.posY;
					player.prevPosZ = player.posZ; 
				}
			} catch (Exception e) {
				
			}
		}
	}

	//#if MC>=11202
	@Inject(remap = false, at = @At(value = "INVOKE", remap = false, shift = Shift.BEFORE, target = "Lnet/minecraft/server/management/PlayerList;transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;ILnet/minecraftforge/common/util/ITeleporter;)V"), method = "changeDimension", cancellable = true)
	public void injectHere(int dimensionIn, net.minecraftforge.common.util.ITeleporter teleporter, CallbackInfoReturnable<Entity> ci) {
	//#else
	//#if MC>=10900
//$$ 	@Inject(at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/management/PlayerList;changePlayerDimension(Lnet/minecraft/entity/player/EntityPlayerMP;I)V", remap = false), method = "changeDimension", cancellable = true)
//$$ 	public void injectHere(int dimensionIn, CallbackInfoReturnable<Entity> ci) {
	//#else
//$$ 	@Inject(at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/management/ServerConfigurationManager;transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;I)V"), method = "travelToDimension", cancellable = true)
//$$ 	public void injectHere(int dimensionIn, CallbackInfo ci) {
	//#endif
	//#endif
		if (dimensionIn == 1 && ChallengeMap.currentMap != null) {
			((EntityPlayerMP) (Object) this).setGameType(GameType.SPECTATOR);
			Timer.running = false;
			GuiIngame chat = Minecraft.getMinecraft().ingameGUI;
			//#if MC>=11100
			chat.getChatGUI().clearChatMessages(true);
			//#else
//$$ 			chat.getChatGUI().clearChatMessages();
			//#endif
			//#if MC>=10900
			chat.getChatGUI().printChatMessage(new TextComponentString("You have completed: \u00A76" + ChallengeMap.currentMap.displayName + "\u00A7f! Your Time is: " + Timer.getCurrentTimerFormatted()));
			chat.getChatGUI().printChatMessage(new TextComponentString("Please submit your \u00A7craw \u00A7fvideo to \u00A77#new-misc-things \u00A7f on the Minecraft TAS Discord Server."));
			//#else
//$$ 			chat.getChatGUI().printChatMessage(new ChatComponentText("You have completed: \u00A76" + ChallengeMap.currentMap.displayName + "\u00A7f! Your Time is: " + Timer.getCurrentTimerFormatted()));
//$$ 			chat.getChatGUI().printChatMessage(new ChatComponentText("Please submit your \u00A7craw \u00A7fvideo to \u00A77#new-misc-things \u00A7f on the Minecraft TAS Discord Server."));
			//#endif
			ChallengeMap.currentMap = null;
        	ChallengeLoader.backupSession();
            try {
            	Field h = Minecraft.getMinecraft().getClass().getDeclaredField("field_71469_aa");
            	h.setAccessible(true);
            	//#if MC>=10900
            	h.set(Minecraft.getMinecraft(), new AnvilSaveConverter(new File(Minecraft.getMinecraft().mcDataDir, "saves"), Minecraft.getMinecraft().getDataFixer()));
            	//#else
            //$$ 	h.set(Minecraft.getMinecraft(), new AnvilSaveConverter(new File(Minecraft.getMinecraft().mcDataDir, "saves")));
            	//#endif
            } catch (Exception e) {
    			e.printStackTrace();
    		}
			
			//#if MC>=10900
            ci.setReturnValue((Entity) (Object) this);
			//#endif
            ci.cancel();
			return;
		}
		//#if MC>=10900
		if (dimensionIn == 1 && ((EntityPlayerMP) (Object) this).dimension == 0 && ((EntityPlayerMP) (Object) this).interactionManager.getGameType() == GameType.SPECTATOR) {
		//#else
//$$ 		if (dimensionIn == 1 && ((EntityPlayerMP) (Object) this).dimension == 0 && ((EntityPlayerMP) (Object) this).theItemInWorldManager.getGameType() == GameType.SPECTATOR) {
		//#endif
			//#if MC>=10900
            ci.setReturnValue((Entity) (Object) this);
			//#endif
			ci.cancel();
			return;
		}
	}
	
}
