package de.pfannekuchen.lotas.core.utils;


//#if MC>=11800
//$$ public interface AccessorScreen2 {
	//#if MC>=11903
//$$ 	public <T extends net.minecraft.client.gui.components.events.GuiEventListener & net.minecraft.client.gui.components.Renderable & net.minecraft.client.gui.narration.NarratableEntry> T addRenderableWidget(T widget);
	//#else
//$$ 	public <T extends net.minecraft.client.gui.components.events.GuiEventListener & net.minecraft.client.gui.components.Widget & net.minecraft.client.gui.narration.NarratableEntry> T addRenderableWidget(T widget);
	//#endif
//#else
//#if MC>=11700
//$$ public interface AccessorScreen2 {
//$$ 	public <T extends net.minecraft.client.gui.components.events.GuiEventListener & net.minecraft.client.gui.components.Widget & net.minecraft.client.gui.narration.NarratableEntry> T addRenderableWidget(T widget);
//#else
public class AccessorScreen2 {
//#endif
//#endif
}
