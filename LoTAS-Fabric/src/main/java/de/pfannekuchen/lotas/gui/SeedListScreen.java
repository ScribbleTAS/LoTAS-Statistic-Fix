package de.pfannekuchen.lotas.gui;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.platform.GlStateManager;

import de.pfannekuchen.lotas.mixin.accessors.AccessorCreateWorldScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class SeedListScreen extends Screen {

    public Seed selectedSeed = null;
    public static ArrayList<Seed> seeds = new ArrayList<>(); /** @see de.pfannekuchen.lotas.LoTAS List of all downloaded seeds */

    public static Map<String, Identifier> seedsId = new HashMap<>();

    /**
     * Seed class
     */
    public static class Seed {
        private String seed;
        private String name;
        private String description;

        public Seed(String seed, String name, String description) {
            this.seed = seed;
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getSeed() {  return seed; }
    }

    public SeedListScreen() {  super(new LiteralText("Seeds")); }

    /**
     * Listens for mouse clicks and selects the seed entry
     *
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseY -= 52;
        if (mouseY > 0 && mouseY < seeds.size() * 40) {
            int index = (int) Math.floor(mouseY / 40);
            selectedSeed = seeds.get(index);
            this.buttons.get(1).active = true;
        }
        return super.mouseClicked(mouseX, mouseY+52, button);
    }

    /**
     * Downloads the Seed to an Identifier
     * @param seed
     */
    public Identifier downloadSeed(String seed) throws IOException {
		if (seedsId.containsKey(seed + "")) return seedsId.get(seed + "");
		URL url = new URL("http://mgnet.work/seeds/" + seed + ".png");
		NativeImage image = NativeImage.read(url.openStream());
		NativeImageBackedTexture txt = new NativeImageBackedTexture(image);
		Identifier iff = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(seed + "", txt);
		seedsId.put(seed + "", iff);
		return iff;
    }

    /**
     * Adds Done and Create World buttons to Seed menu
     */
    @Override
    protected void init() {
        this.addButton(new ButtonWidget(width / 2 - 100, height - 28, 200,20, "Done", button -> {
            this.minecraft.openScreen(new SelectWorldScreen(new TitleScreen()));
        }));
        ButtonWidget create = new ButtonWidget(width / 2 - 100, height - 52, 200,20, "Create World", button -> {
            this.minecraft.openScreen(new ProgressScreen());
            CreateWorldScreen createWorldScreen = new CreateWorldScreen(this);
            AccessorCreateWorldScreen accessorCWS=(AccessorCreateWorldScreen) createWorldScreen;
            accessorCWS.setSeed(selectedSeed.seed);
            accessorCWS.setCheatsEnabled(true);
            this.minecraft.openScreen(createWorldScreen);
        });
        create.active = false;
        this.addButton(create);
        super.init();
    }

    /**
     * Renders background from top to bottom
     */
    protected void renderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex((double)0, (double)bottom, 0.0D).texture(0.0D, (double)((float)bottom / 32.0F)).color(64, 64, 64, alphaBottom).next();
        bufferBuilder.vertex((double)(0 + this.width), (double)bottom, 0.0D).texture((double)((float)this.width / 32.0F), (double)((float)bottom / 32.0F)).color(64, 64, 64, alphaBottom).next();
        bufferBuilder.vertex((double)(0 + this.width), (double)top, 0.0D).texture((double)((float)this.width / 32.0F), (double)((float)top / 32.0F)).color(64, 64, 64, alphaTop).next();
        bufferBuilder.vertex((double)0, (double)top, 0.0D).texture(0.0D, (double)((float)top / 32.0F)).color(64, 64, 64, alphaTop).next();
        tessellator.draw();
    }

    /**
     * Draw all Seed Entries from seeds
     */
    public void drawSeeds() throws IOException {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        int y = 52;
        int x = this.width / 3;
        int n = 32;
        for (Seed seed : seeds) {
            if (selectedSeed == seed) {
                double r = x;
                int q = x + this.minecraft.textRenderer.getStringWidth(seed.description) + 38;
                GlStateManager.disableTexture();
                float f = 1.0F;
                GlStateManager.color4f(f, f, f, 0.5F);
                bufferBuilder.begin(7, VertexFormats.POSITION);
                bufferBuilder.vertex((double)r-1, (double)(y + n + 2), 1.0D).next();
                bufferBuilder.vertex((double)q-1, (double)(y + n + 2), 1.0D).next();
                bufferBuilder.vertex((double)q-1, (double)(y - 2), 1.0D).next();
                bufferBuilder.vertex((double)r-1, (double)(y - 2), 1.0D).next();
                tessellator.draw();
                GlStateManager.color4f(0.0F, 0.0F, 0.0F, 0.5F);
                bufferBuilder.begin(7, VertexFormats.POSITION);
                bufferBuilder.vertex((double)(r), (double)(y + n + 1), 1.0D).next();
                bufferBuilder.vertex((double)(q - 2), (double)(y + n + 1), 1.0D).next();
                bufferBuilder.vertex((double)(q - 2), (double)(y - 1), 1.0D).next();
                bufferBuilder.vertex((double)(r), (double)(y - 1), 1.0D).next();
                tessellator.draw();
                GlStateManager.enableTexture();
            }
            this.minecraft.textRenderer.draw(seed.name, x + 35, y + 3, 16777215);
            this.minecraft.textRenderer.draw(seed.description,  x + 35, y + 14, 8421504);
            this.minecraft.textRenderer.draw(seed.seed,  x + 35, y + 24, 8421504);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            try {
            	this.minecraft.getTextureManager().bindTexture(downloadSeed(seed.seed));
            	DrawableHelper.blit(x+1, y, 0.0F, 0.0F, 32, 32, 32, 32);
            } catch (Exception e) {

            }
            y += 40;
        }
    }

    /**
     * Render UI.
     */
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int left = 0;
        int right = width;
        int top = 48;
        int bottom = height - 64;

        renderBackground();

        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex((double)left, (double)bottom, 0.0D).texture((double)((float)left / 32.0F), (double)((float)(bottom + (int)1) / 32.0F)).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double)right, (double)bottom, 0.0D).texture((double)((float)right / 32.0F), (double)((float)(bottom + (int)1) / 32.0F)).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double)right, (double)top, 0.0D).texture((double)((float)right / 32.0F), (double)((float)(top + (int)1) / 32.0F)).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double)left, (double)top, 0.0D).texture((double)((float)left / 32.0F), (double)((float)(top + (int)1) / 32.0F)).color(32, 32, 32, 255).next();
        tessellator.draw();

        // OpenGL Stuff
        GlStateManager.disableDepthTest();
        this.renderHoleBackground(0, top, 255, 255);
        this.renderHoleBackground(bottom, this.height, 255, 255);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();

        // Draw top gradient
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(left, top + 4, 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(right, top + 4, 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(right, top, 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(left, top, 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 255).next();
        tessellator.draw();

        // Draw bottom gradient
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(left, bottom, 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(right, bottom, 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(right, bottom - 4, 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(left, bottom - 4, 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 0).next();
        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();

        drawCenteredString(minecraft.textRenderer, "Seeds", width / 2, 8, 0xFFFFFF);

        try {
            drawSeeds();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.render(mouseX, mouseY, delta);
    }
}