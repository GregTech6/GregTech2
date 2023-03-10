package gregtechmod.common.gui;

import gregtechmod.api.BaseMetaTileEntity;
import gregtechmod.common.containers.GT_Container_ImplosionCompressor;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GT_GUIContainer_ImplosionCompressor extends GT_GUIContainerMetaTile_Machine {
	
    public GT_GUIContainer_ImplosionCompressor(InventoryPlayer aInventoryPlayer, BaseMetaTileEntity aTileEntity, int aID) {
        super(new GT_Container_ImplosionCompressor(aInventoryPlayer, aTileEntity, aID), aTileEntity, aID);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 94, 4210752);
        fontRenderer.drawString("Implosion Compressor", 8,  4, 4210752);
        if (!((GT_Container_ImplosionCompressor)mContainer).mMachine)
        	fontRenderer.drawString("Incomplete Machine Casing!", 8, ySize - 103, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        int texture = mc.renderEngine.getTexture("/gregtechmod/textures/gui/Implosion.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        
        if (mContainer != null) {
        	int tScale = ((GT_Container_ImplosionCompressor)mContainer).mProgressScale;
        	this.drawTexturedModalRect(x + 58, y + 28, 176, 0, tScale, 11);
        }
    }
}
