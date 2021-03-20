package de.pfannekuchen.lotas.mixin;

import java.io.IOException;
import java.time.Duration;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.pfannekuchen.lotas.challenges.ChallengeLoader;
import de.pfannekuchen.lotas.config.ConfigManager;
import de.pfannekuchen.lotas.gui.GuiChallengeEscape;
import de.pfannekuchen.lotas.hotkeys.Hotkeys;
import de.pfannekuchen.lotas.savestate.SavestateMod;
import de.pfannekuchen.lotas.tickratechanger.TickrateChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.Timer;
import net.minecraft.util.math.MathHelper;
import rlog.RLogAPI;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	/*
	 * This Mixin automatically pauses the Game when entering a World and 
	 * makes Tickrate 0 work
	 */
	
	@Shadow
	public int rightClickDelayTimer;
	
	@Shadow
	public GameSettings gameSettings;
	
	@Shadow
    public GuiScreen currentScreen;
	
	@Shadow
	public Timer timer;
	
	private int save = 6;
	
	private int das = 0;
	
	private boolean isLoadingWorld;
	
	@Shadow
	public EntityPlayerSP player;
	
	@Inject(method = "loadWorld", at = @At("HEAD"))
	public void injectloadWorld(WorldClient worldClientIn, CallbackInfo ci) {
		isLoadingWorld = ConfigManager.getBoolean("tools", "hitEscape") && worldClientIn != null;
		if (ChallengeLoader.startTimer) {
			ChallengeLoader.startTimer = false;
			de.pfannekuchen.lotas.tickratechanger.Timer.startTime = Duration.ofMillis(System.currentTimeMillis());
			de.pfannekuchen.lotas.tickratechanger.Timer.ticks = 1;
			de.pfannekuchen.lotas.tickratechanger.Timer.running = true;
		}
	}
	
	@Inject(method = "runTick", at = @At(value="HEAD"))
	public void injectrunTick(CallbackInfo ci) {
		if (ConfigManager.getBoolean("tools", "lAutoClicker")) rightClickDelayTimer = 0;
		
		TickrateChanger.show = !TickrateChanger.show;
		
		if (Hotkeys.shouldSavestate) {
			Hotkeys.shouldSavestate = false;
			try {
				SavestateMod.savestate();
			} catch (IOException e) {
				RLogAPI.logError(e, "[Savestate] Savestate Error #3");
			}
		}
		
		if (Hotkeys.shouldLoadstate) {
			Hotkeys.shouldLoadstate = false;
			try {
				SavestateMod.loadstate();
			} catch (IOException e) {
				RLogAPI.logError(e, "[Savestate] Loadstate Error #3");
			}
		}
		
		if (TickrateChanger.advanceClient) {
			TickrateChanger.resetAdvanceClient();
		}
		
		TickrateChanger.timeOffset = System.currentTimeMillis();
		TickrateChanger.ticksPassed++;
		if (de.pfannekuchen.lotas.tickratechanger.Timer.running) {
			if (currentScreen == null) de.pfannekuchen.lotas.tickratechanger.Timer.ticks++;
			else if (de.pfannekuchen.lotas.tickratechanger.Timer.allowed.contains(currentScreen.getClass().getSimpleName().toLowerCase())) de.pfannekuchen.lotas.tickratechanger.Timer.ticks++;
		}
		
		if (TickrateChanger.ticksToJump != -1 && Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu == false) {
			TickrateChanger.ticksToJump--;
			if (TickrateChanger.ticksToJump == 0) {
				TickrateChanger.ticksToJump = -1;
				Minecraft.getMinecraft().displayGuiScreen(new GuiIngameMenu());
			}
		}
	}
	
	private void moveRelatively(float strafe, float up, float forward, float friction) {
        float f = strafe * strafe + up * up + forward * forward;
        if (f >= 1.0E-4F)
        {
            f = MathHelper.sqrt(f);
            if (f < 1.0F) f = 1.0F;
            f = friction / f;
            strafe = strafe * f;
            up = up * f;
            forward = forward * f;
            float f1 = MathHelper.sin(player.rotationYaw * 0.017453292F);
            float f2 = MathHelper.cos(player.rotationYaw * 0.017453292F);
            player.posX += (double)(strafe * f2 - forward * f1);
            player.posY += (double)up;
            player.posZ += (double)(forward * f2 + strafe * f1);
        }
	}
	
    @Inject(method = "runGameLoop", at = @At(value="HEAD"))
    public void injectrunGameLoop(CallbackInfo ci) throws IOException {
    	das--;
    	
    	if (TickrateChanger.tickrate == 0 && Keyboard.isKeyDown(Hotkeys.advance.getKeyCode()) && das <= 0 && !Hotkeys.isFreecaming) {
    		TickrateChanger.advanceTick();
    		das = 15;
    	} 
    	
    	//Controls for freecam
    	if (Hotkeys.isFreecaming) {
    		if (Keyboard.isKeyDown(gameSettings.keyBindForward.getKeyCode())) {
    			moveRelatively(0.0F, 0.0F, 0.91F, 1.0F);
    		} 
    		if (Keyboard.isKeyDown(gameSettings.keyBindBack.getKeyCode())) {
    			moveRelatively(0.0F, 0.0F, -0.91F, 1.0F);
	   		} 
    		if (Keyboard.isKeyDown(gameSettings.keyBindLeft.getKeyCode())) {
	   			moveRelatively(0.91F, 0.0F, 0.0F, 1.0F);
			} 
    		if (Keyboard.isKeyDown(gameSettings.keyBindRight.getKeyCode())) {
				moveRelatively(-0.91F, 0.0F, 0.0F, 1.0F);
			} 
    		if (Keyboard.isKeyDown(gameSettings.keyBindJump.getKeyCode())) {
				moveRelatively(0.0F, 0.92F, 0.0F, 1.0F);
			} 
    		if (Keyboard.isKeyDown(gameSettings.keyBindSneak.getKeyCode())) {
				moveRelatively(0.0F, -0.92F, 0.0F, 1.0F);
			}
    	}
    	
    	if (Keyboard.isKeyDown(Hotkeys.freecam.getKeyCode()) && Minecraft.getMinecraft().currentScreen == null && das <= 0) {
    		das = 15;
    		if (Hotkeys.isFreecaming) {
    			Hotkeys.isFreecaming = false;
    			Minecraft.getMinecraft().renderGlobal.loadRenderers();
				TickrateChanger.updateTickrate(Hotkeys.savedTickrate);
			} else {
				Hotkeys.isFreecaming = true;
				Hotkeys.savedTickrate = (int)TickrateChanger.tickrate;
				TickrateChanger.updateTickrate(0);
			}
		}
    	
    	else if (Keyboard.isKeyDown(Hotkeys.zero.getKeyCode()) && das <= 0 && TickrateChanger.advanceClient == false && !Hotkeys.isFreecaming && Minecraft.getMinecraft().currentScreen == null) { 
    		if (TickrateChanger.tickrate > 0) {
    			save = TickrateChanger.index;
    			TickrateChanger.updateTickrate(0);
    			TickrateChanger.index = 0;
    		} else {
    			TickrateChanger.updateTickrate(TickrateChanger.ticks[save]);
    			TickrateChanger.index = save;
    		}
    		das = 15;
    	}
    }
    
    @ModifyVariable(method = "displayGuiScreen", at = @At("STORE"), index = 1, ordinal = 0)
    public GuiScreen changeGuiScreen(GuiScreen screenIn) {
    	if (ChallengeLoader.map != null && screenIn != null) {
    		if (screenIn instanceof GuiIngameMenu) return new GuiChallengeEscape();
    	}
		if (isLoadingWorld && screenIn == null) {
			isLoadingWorld = false;
			return ChallengeLoader.map == null ? new GuiIngameMenu() : new GuiChallengeEscape();
		}
		return screenIn;
    }
    
	@Inject(method = "displayGuiScreen", at = @At("HEAD"))
	public void injectdisplayGuiScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
    	if (guiScreenIn == null) {
    		if (SavestateMod.applyVelocity) {
    			SavestateMod.applyVelocity = false;
    			player.motionX = SavestateMod.motionX;
    			player.motionY = SavestateMod.motionY;
    			player.motionZ = SavestateMod.motionZ;
    		}
    	}
	}
	
}
