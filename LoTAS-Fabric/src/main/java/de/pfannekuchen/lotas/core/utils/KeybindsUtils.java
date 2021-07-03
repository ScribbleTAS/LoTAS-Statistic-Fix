package de.pfannekuchen.lotas.core.utils;

import java.time.Duration;

import org.lwjgl.glfw.GLFW;

import de.pfannekuchen.lotas.core.utils.EventUtils.Timer;
import de.pfannekuchen.lotas.gui.HudSettings;
import de.pfannekuchen.lotas.mods.AIManipMod;
import de.pfannekuchen.lotas.mods.DupeMod;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
//#if MC>=11700
//$$ import net.minecraft.client.option.KeyBinding;
//#else
import net.minecraft.client.options.KeyBinding;
//#endif

public class KeybindsUtils {

	public static KeyBinding saveStateKeybind = new KeyBinding("Savestate", GLFW.GLFW_KEY_J, "Stating");
	public static final KeyBinding loadStateKeybind = new KeyBinding("Loadstate", GLFW.GLFW_KEY_K, "Stating");
	public static final KeyBinding loadDupeKeybind = new KeyBinding("Load Items/Chests", GLFW.GLFW_KEY_O, "Duping");
	public static final KeyBinding saveDupeKeybind = new KeyBinding("Save Items/Chests", GLFW.GLFW_KEY_P, "Duping");
	public static final KeyBinding holdStrafeKeybind = new KeyBinding("Auto-Strafe", GLFW.GLFW_KEY_H, "Moving");
	public static final KeyBinding toggleFreecamKeybind = new KeyBinding("Freecam", GLFW.GLFW_KEY_I, "Moving");
	public static final KeyBinding increaseTickrateKeybind = new KeyBinding("Faster Tickrate", GLFW.GLFW_KEY_PERIOD, "Tickrate Changer");
	public static final KeyBinding decreaseTickrateKeybind = new KeyBinding("Slower Tickrate", GLFW.GLFW_KEY_COMMA, "Tickrate Changer");
	public static final KeyBinding advanceTicksKeybind = new KeyBinding("Advance Tick", GLFW.GLFW_KEY_F9, "Tickrate Changer");
	public static final KeyBinding toggleAdvanceKeybind = new KeyBinding("Tickrate Zero Toggle", GLFW.GLFW_KEY_F8, "Tickrate Changer");
	public static final KeyBinding toggleTimerKeybind = new KeyBinding("Start/Stop Timer", GLFW.GLFW_KEY_KP_5, "Tickrate Changer");
	public static final KeyBinding openInfoHud = new KeyBinding("Open InfoGui Editor", GLFW.GLFW_KEY_F6, "Misc");
	public static final KeyBinding test = new KeyBinding("Test", GLFW.GLFW_KEY_F12, "Misc");
	public static boolean shouldSavestate;
	public static boolean shouldLoadstate;
	public static boolean isFreecaming;
	public static int savedTickrate;

	public static boolean wasPressed = false;

	public static void keyEvent() {
		while (saveStateKeybind.wasPressed()) {
			shouldSavestate = true;
			MinecraftClient.getInstance().openScreen(new GameMenuScreen(true));
		}
		while (loadStateKeybind.wasPressed()) {
			MinecraftClient.getInstance().openScreen(new GameMenuScreen(true));
			shouldLoadstate = true;
		}
		while (loadDupeKeybind.wasPressed()) {
			DupeMod.load(MinecraftClient.getInstance());
		}
		while (saveDupeKeybind.wasPressed()) {
			DupeMod.save(MinecraftClient.getInstance());
		}
		while (toggleTimerKeybind.wasPressed()) {
			if (Timer.ticks < 1 || Timer.startTime == null) {
				Timer.startTime = Duration.ofMillis(System.currentTimeMillis());
				Timer.ticks = 1;
			}
			Timer.running = !Timer.running;
		}
		
		while (openInfoHud.wasPressed()) {
			MinecraftClient.getInstance().openScreen(new HudSettings());
		}

		if (wasPressed != holdStrafeKeybind.isPressed() && wasPressed == true) {
			//#if MC>=11601
//$$ 						KeyBinding.setKeyPressed(MinecraftClient.getInstance().options.keyRight.getDefaultKey(), false);
			//#else
			KeyBinding.setKeyPressed(MinecraftClient.getInstance().options.keyRight.getDefaultKeyCode(), false);
			//#endif
		} else if (wasPressed != holdStrafeKeybind.isPressed() && wasPressed == false) {
			//#if MC>=11700
//$$ 			float newyaw=MinecraftClient.getInstance().player.getYaw()-45;
//$$ 			MinecraftClient.getInstance().player.setYaw(newyaw);
			//#else
			MinecraftClient.getInstance().player.yaw -= 45;
			//#endif
		}
		if(test.wasPressed()) {
//			AIManipMod manip= new AIManipMod();
//			manip.quickDebug();
		}
		wasPressed = holdStrafeKeybind.isPressed();
	}

	public static void registerKeybinds() {
		KeyBindingHelper.registerKeyBinding(saveStateKeybind);
		KeyBindingHelper.registerKeyBinding(loadStateKeybind);
		KeyBindingHelper.registerKeyBinding(loadDupeKeybind);
		KeyBindingHelper.registerKeyBinding(saveDupeKeybind);
		KeyBindingHelper.registerKeyBinding(holdStrafeKeybind);
		KeyBindingHelper.registerKeyBinding(toggleFreecamKeybind);
		KeyBindingHelper.registerKeyBinding(increaseTickrateKeybind);
		KeyBindingHelper.registerKeyBinding(decreaseTickrateKeybind);
		KeyBindingHelper.registerKeyBinding(advanceTicksKeybind);
		KeyBindingHelper.registerKeyBinding(toggleAdvanceKeybind);
		KeyBindingHelper.registerKeyBinding(toggleTimerKeybind);
		KeyBindingHelper.registerKeyBinding(openInfoHud);
//		KeyBindingHelper.registerKeyBinding(test);
	}
}