package de.pfannekuchen.lotas.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

import org.lwjgl.opengl.GL11;

import de.pfannekuchen.lotas.core.utils.VideoUpspeeder;
import net.minecraft.client.gui.components.Button;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

public class VideoUpspeederScreen extends Screen {

	String[] PROGRESS_BAR_STAGES = new String[] { "oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO" };
	public static Duration est = Duration.ZERO;
	public static String installingProgress = "Idle";
	public static String codecFFmpeg = null;
	public static boolean downloadingFFmpeg;
	public static boolean isEncoding;
	private static File selectedFile;
	private static int tickrate = 20;

	static String videoFormat = "Unknown";
	static String codec = "Unknown";
	static String resolution = "Unknown";
	static String length = "Unknown";
	static String filesize = "Unknown";
	public static String bitrate = "0";
	public static long framesDone = 0L;
	static long lengthInMilliseconds = 0L;
	public static float progress;
	public static float size;

	public static Minecraft client;

	public VideoUpspeederScreen() {
		super(new TextComponent(""));
		client = Minecraft.getInstance();
		if (!VideoUpspeeder.instantiate(new File(client.gameDirectory, "ffmpeg"))) {
			VideoUpspeeder.installFFmpeg(client);
		}
		if (codecFFmpeg == null)
			codecFFmpeg = GL11.glGetString(GL11.GL_VENDOR).toUpperCase().contains("NVIDIA") ? "nvenc_h264" : "h264";
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		super.charTyped(chr, modifiers);
		try {
			tickrate = Integer.parseInt(((EditBox) buttons.get(buttons.size() - 1)).getValue());
		} catch (Exception e) {

		}
		return true;
	}

	public static String getDuration(Duration d) {
		return String.format("%02d", d.toHours()) + ":" + String.format("%02d", d.toMinutes() % 60) + ":" + String.format("%02d", d.getSeconds() % 60);
	}

	@Override
	protected void init() {
		if (isEncoding) {
			addButton(new Button(width / 2 - 153, height - 40, 306, 20, "Continue encoding in the background >>", (b) -> {
				Minecraft.getInstance().setScreen(new TitleScreen());
			}));
			return;
		}

		addButton(new EditBox(client.font, (width / 12) * 1 - (width / 24), (height / 8), (width / 12) * 9, 20, "")).setMaxLength(999);
		addButton(new Button((width / 12) * 10 + 5 - (width / 24), (height / 8), (width / 12) * 2, 20, "Select File", (b) -> {
			if (client.window.isFullscreen())
				client.window.toggleFullScreen();
			new Thread(new Runnable() {

				@Override
				public void run() {
					System.setProperty("java.awt.headless", "false");
					FileDialog dialog = new FileDialog((Frame) null, "Select File to Open", FileDialog.LOAD);
					dialog.setMultipleMode(false);
					dialog.setVisible(true);
					try {
						selectedFile = dialog.getFiles()[0];
						((EditBox)buttons).setValue(selectedFile.getAbsolutePath());
						FFmpegProbeResult result = VideoUpspeeder.ffprobe(selectedFile);

						videoFormat = result.format.format_name.split(",")[0];
						codec = result.getStreams().get(0).codec_name;
						length = getDuration(Duration.ofMillis((long) (result.format.duration * 1000)));
						resolution = result.streams.get(0).width + "x" + result.streams.get(0).height;
						filesize = (Files.size(selectedFile.toPath()) / 1024 / 1024) + " MB";
						lengthInMilliseconds = (long) (result.format.duration * 1000);
						buttons.get(4).active = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}));
		addButton(new Button((width / 2) - 70, (height / 8) * 2 + 20, 20, 20, "-", (b) -> {
			if (tickrate != 1)
				tickrate--;
			((EditBox) buttons.get(buttons.size() - 1)).setValue(tickrate + "");
		}));
		addButton(new Button((width / 2) + 50, (height / 8) * 2 + 20, 20, 20, "+", (b) -> {
			tickrate++;
			((EditBox) buttons.get(buttons.size() - 1)).setValue(tickrate + "");
		}));
		addButton(new Button((width / 2) - 98, this.height - (this.height / 10) - 15 - 20 - 5, 204, 20, "Speed up video", (b) -> {
			b.active = false;
			isEncoding = true;
			VideoUpspeeder.speedup(tickrate, bitrate(), codecFFmpeg, (long) ((lengthInMilliseconds / 16L) * (tickrate / 20F)));
		})).active = selectedFile == null ? false : selectedFile.exists();
		addButton(new Checkbox(2, this.height - 22, 20, 20, "High Quality", false));
		addButton(new EditBox(client.font, (width / 2) - 45, (height / 8) * 2 + 23, 90, 14, "20")).setValue("20");
		super.init();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		if (isEncoding) {
			int i = this.width / 2 - 150;
			int j = this.width / 2 + 150;
			int k = this.height / 4 + 100;
			int l = k + 10;
			int m = 0;
			fill(i - 1, k - 1, j + 1, l + 1, -16777216);
			int n;
			n = Mth.floor(progress * (float) (j - i));
			fill(i + m, k, i + m + n, l, -13408734);
			super.render(mouseX, mouseY, delta);
			int var10004 = k + (l - k) / 2;

			drawString(client.font, "Est: " + getDuration(est), i, 40 + (9 + 3) * 2, 10526880);
			drawString(client.font, bitrate + "", i, 40 + 9 + 3, 10526880);

			drawCenteredString(client.font, framesDone + " / " + (long) ((lengthInMilliseconds / 16L) * (tickrate / 20F)) + " Frames", width / 2, var10004 - 9 / 2, 0xFFFFFF);

			return;
		}

		if (VideoUpspeederScreen.downloadingFFmpeg) {
			this.renderBackground();
			drawCenteredString(client.font, installingProgress, this.width / 2, this.height / 2, 16777215);
			String var10002 = PROGRESS_BAR_STAGES[(int) (Util.getMillis() / 150L % (long) PROGRESS_BAR_STAGES.length)];
			int var10003 = this.width / 2;
			int var10004 = this.height / 2;
			drawCenteredString(client.font, var10002, var10003, var10004 + 9 * 2, 16777215);
			return;
		}

		drawCenteredString(client.font, "Tickrate", (width / 2), (height / 8) * 2, 0xFFFFFF);

		if (selectedFile != null)
			drawString(client.font, selectedFile.getAbsolutePath(), (width / 12) * 1 - (width / 24) + 4, (height / 8) + 6, 0xFFFFFF);

		drawCenteredString(client.font, "Input File", (width / 4), (height / 8) * 3 + 10, 0x808080);
		drawCenteredString(client.font, "Output File", (width / 4) * 3, (height / 8) * 3 + 10, 0x808080);

		drawString(client.font, "Format: " + videoFormat, (width / 4) - (width / 12), (height / 8) * 4, 0xFFFFFF);
		drawString(client.font, "Codec: " + codec, (width / 4) - (width / 12), (height / 8) * 4 + 10, 0xFFFFFF);
		drawString(client.font, "Resolution: " + resolution, (width / 4) - (width / 12), (height / 8) * 4 + 20, 0xFFFFFF);
		drawString(client.font, "Length: " + length, (width / 4) - (width / 12), (height / 8) * 4 + 30, 0xFFFFFF);
		drawString(client.font, "Size: " + filesize, (width / 4) - (width / 12), (height / 8) * 4 + 40, 0xFFFFFF);

		drawString(client.font, "Format: mp4", (width / 4) * 3 - (width / 12), (height / 8) * 4, 0xFFFFFF);
		drawString(client.font, "Encoder: " + codecFFmpeg, (width / 4) * 3 - (width / 12), (height / 8) * 4 + 10, 0xFFFFFF);
		drawString(client.font, "Resolution: " + resolution, (width / 4) * 3 - (width / 12), (height / 8) * 4 + 20, 0xFFFFFF);
		String dur = null;
		try {
			dur = getDuration(Duration.ofMillis((long) (lengthInMilliseconds * (tickrate / 20F))));
		} catch (Exception e) {

		}
		drawString(client.font, "Length: " + dur, (width / 4) * 3 - (width / 12), (height / 8) * 4 + 30, 0xFFFFFF);
		drawString(client.font, "Size: " + calcSize(), (width / 4) * 3 - (width / 12), (height / 8) * 4 + 40, 0xFFFFFF);

		super.render(mouseX, mouseY, delta);
	}

	/* Gets the Bitrate from the button */
	private int bitrate() {
		return ((Checkbox) buttons.get(5)).selected() ? 20000000 : 8000000;
	}

	/* Calculates the Size if the Video with a max bitrate: bitrate * frames */
	private String calcSize() {
		try {
			return "~" + (int) (((bitrate() * lengthInMilliseconds / 1000F) / 8F / 1000F / 1000F) * (tickrate / 20F)) + " MB";
		} catch (final Exception e) {
			return "Unknown";
		}
	}

	public static void onStatsReady() {
		downloadingFFmpeg = false;
	}

}
