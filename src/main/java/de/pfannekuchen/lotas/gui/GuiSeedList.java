package de.pfannekuchen.lotas.gui;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.pfannekuchen.lotas.core.MCVer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
//#if MC>=11000
import net.minecraft.world.GameType;
//#else
//$$ import net.minecraft.world.WorldSettings.GameType;
//#endif
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;

public class GuiSeedList extends GuiScreen {
	
	SeedListExtended list;
	
	@Override
	public void initGui() {
		this.buttonList.add(new GuiButton(1, this.width / 2 - 154, this.height - 28, 304, 20, I18n.format("selectWorld.create")));
		list = new SeedListExtended(width, height, 32, height - 64, 36);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		list.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		list.handleMouseInput();
	}

    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        list.mouseReleased(mouseX, mouseY, state);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
    	if (button.id == 1 && SeedListExtended.selectedIndex != -1) {
    		GuiCreateWorld guicreateworld = new GuiCreateWorld(this);
    		SeedListExtended.SeedEntry et = SeedListExtended.seeds.get(SeedListExtended.selectedIndex);
    		WorldSettings set = new WorldSettings(Long.parseLong(et.seed), GameType.SURVIVAL, true, false, net.minecraft.world.WorldType.DEFAULT);
    		set = set.enableCommands();
    		guicreateworld.recreateFromExistingWorld(new WorldInfo(set, et.name));
    		Minecraft.getMinecraft().displayGuiScreen(guicreateworld);
    	}
    }
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		list.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(MCVer.getFontRenderer(Minecraft.getMinecraft()), "Seeds", this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	public static class SeedListExtended extends GuiListExtended {

		public SeedListExtended(int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
			super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		}

		public static int selectedSeed = 1234;
		public static int selectedIndex = -1;
		public static List<SeedEntry> seeds = new ArrayList<SeedListExtended.SeedEntry>();
		
		public static class SeedEntry implements Serializable, IGuiListEntry {

			private static final long serialVersionUID = 4428898479076411871L;
			public String name;
			public String description;
			public String seed;
			private int index;
			public ResourceLocation loc = null;
			
			public SeedEntry(String name, String description, String seed, int index) {
				this.name = name;
				this.description = description;
				this.seed = seed;
				this.index = index;
			}
			
			
			
			//#if MC>=11200
			@Override
			public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) { }
			//#else
//$$ 			@Override
//$$ 			public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) { }
			//#endif
			
			//#if MC>=11200
			@Override public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
			//#else
//$$ 			@Override public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			//#endif
				String s = name;
				String s1 = description;
				String s2 = seed;

				if (StringUtils.isEmpty(s)) {
					s = I18n.format("selectWorld.world") + " " + (slotIndex + 1);
				}

				s2 = "Seed: " + s2;

				MCVer.getFontRenderer(Minecraft.getMinecraft()).drawString(s, x + 32 + 3, y + 1, 16777215);
				MCVer.getFontRenderer(Minecraft.getMinecraft()).drawString(s1, x + 32 + 3, y + MCVer.getFontRenderer(Minecraft.getMinecraft()).FONT_HEIGHT + 3, 8421504);
				MCVer.getFontRenderer(Minecraft.getMinecraft()).drawString(s2, x + 32 + 3, y + MCVer.getFontRenderer(Minecraft.getMinecraft()).FONT_HEIGHT + MCVer.getFontRenderer(Minecraft.getMinecraft()).FONT_HEIGHT + 3, 8421504);
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableBlend();
				Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
				GlStateManager.disableBlend();
			}

		    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
				selectedIndex = index;
		        return false;
		    }

			@Override
			public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

			}

		}
		
		@Override
		protected boolean isSelected(int slotIndex) {
			return selectedIndex == slotIndex;
		}
		
		@Override
		public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
			return super.mouseClicked(mouseX, mouseY, mouseEvent);
		}
		
		@Override
		public IGuiListEntry getListEntry(int index) {
			return seeds.get(index);
		}
		
		@Override
		protected int getSize() {
			return seeds.size();
		}
	}
	
}