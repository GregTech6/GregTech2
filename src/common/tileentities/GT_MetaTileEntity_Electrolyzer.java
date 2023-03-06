package gregtechmod.common.tileentities;

import gregtechmod.GT_Mod;
import gregtechmod.api.BaseMetaTileEntity;
import gregtechmod.api.GT_Recipe;
import gregtechmod.api.MetaTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

public class GT_MetaTileEntity_Electrolyzer extends MetaTileEntity {

	private int mProgresstime = 0, mMaxProgresstime = 0, mEUt = 0, mStoredWater = 0;
	private ItemStack mOutputItem1, mOutputItem2, mOutputItem3, mOutputItem4;
	
	public GT_MetaTileEntity_Electrolyzer(int aID, String mName, String mNameRegional) {
		super(aID, mName, mNameRegional);
	}
	
	public GT_MetaTileEntity_Electrolyzer() {
		
	}

    @Override public boolean isValidSlot(int aIndex) 				{return aIndex < 6;}
	@Override public boolean isSimpleMachine()						{return false;}
	@Override public boolean isFacingValid(int aFacing)				{return false;}
	@Override public boolean isEnetInput() 							{return true;}
	@Override public boolean isInputFacing(short aSide)				{return true;}
    @Override public int maxEUInput()								{return 128;}
    @Override public int maxEUStore()								{return 10000;}
	@Override public int getInvSize()								{return 7;}
	@Override public void onRightclick(EntityPlayer aPlayer)		{aPlayer.openGui(GT_Mod.instance, 109, mBaseMetaTileEntity.worldObj, mBaseMetaTileEntity.xCoord, mBaseMetaTileEntity.yCoord, mBaseMetaTileEntity.zCoord);}
	@Override public boolean isAccessAllowed(EntityPlayer aPlayer)	{return true;}
	public int getProgresstime()									{return mProgresstime;}
	public int maxProgresstime()									{return mMaxProgresstime;}
    
	@Override
	public MetaTileEntity newMetaEntity(BaseMetaTileEntity aTileEntity) {
		return new GT_MetaTileEntity_Electrolyzer();
	}
	
	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
    	aNBT.setInteger("mEUt", mEUt);
    	aNBT.setInteger("mStoredWater", mStoredWater);
    	aNBT.setInteger("mProgresstime", mProgresstime);
    	aNBT.setInteger("mMaxProgresstime", mMaxProgresstime);

        if (mOutputItem1 != null) {
            NBTTagCompound tNBT = new NBTTagCompound();
        	mOutputItem1.writeToNBT(tNBT);
            aNBT.setTag("mOutputItem1", tNBT);
        }
        if (mOutputItem2 != null) {
            NBTTagCompound tNBT = new NBTTagCompound();
        	mOutputItem2.writeToNBT(tNBT);
            aNBT.setTag("mOutputItem2", tNBT);
        }
        if (mOutputItem3 != null) {
            NBTTagCompound tNBT = new NBTTagCompound();
        	mOutputItem3.writeToNBT(tNBT);
            aNBT.setTag("mOutputItem3", tNBT);
        }
        if (mOutputItem4 != null) {
            NBTTagCompound tNBT = new NBTTagCompound();
        	mOutputItem4.writeToNBT(tNBT);
            aNBT.setTag("mOutputItem4", tNBT);
        }
	}
	
	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
    	mEUt = aNBT.getInteger("mEUt");
    	mStoredWater = aNBT.getInteger("mStoredWater");
    	mProgresstime = aNBT.getInteger("mProgresstime");
    	mMaxProgresstime = aNBT.getInteger("mMaxProgresstime");
    	
    	NBTTagCompound tNBT1 = (NBTTagCompound)aNBT.getTag("mOutputItem1");
    	if (tNBT1 != null) {
    		mOutputItem1 = ItemStack.loadItemStackFromNBT(tNBT1);
    	}
    	NBTTagCompound tNBT2 = (NBTTagCompound)aNBT.getTag("mOutputItem2");
    	if (tNBT2 != null) {
    		mOutputItem2 = ItemStack.loadItemStackFromNBT(tNBT2);
    	}
    	NBTTagCompound tNBT3 = (NBTTagCompound)aNBT.getTag("mOutputItem3");
    	if (tNBT3 != null) {
    		mOutputItem3 = ItemStack.loadItemStackFromNBT(tNBT3);
    	}
    	NBTTagCompound tNBT4 = (NBTTagCompound)aNBT.getTag("mOutputItem4");
    	if (tNBT4 != null) {
    		mOutputItem4 = ItemStack.loadItemStackFromNBT(tNBT4);
    	}
	}
	
    @Override
    public void receiveClientEvent(int aEventID, int aValue) {
    	super.receiveClientEvent(aEventID, aValue);
    	if (mBaseMetaTileEntity.worldObj.isRemote) {
	    	switch(aEventID) {
	    	case 10:
	    		mProgresstime = aValue;
	    		break;
	    	case 11:
	    		mMaxProgresstime = aValue;
	    		break;
	    	}
    	}
    }

    @Override
    public void onPostTick() {
	    if (!mBaseMetaTileEntity.worldObj.isRemote) {
	    	if (mStoredWater >= 1000 && (mInventory[6] == null || mInventory[6].stackSize < 64)) {
		    	if (mInventory[6] == null) {
		    		mInventory[6] = new ItemStack(Block.waterStill, 0);
		    	}
	    		mStoredWater -= 1000;
	    		mInventory[6].stackSize++;
	    	}
	    	
	    	if (mMaxProgresstime > 0) {
	    		mBaseMetaTileEntity.mActive = true;
	    		if (mBaseMetaTileEntity.decreaseStoredEnergy(mEUt, false)) {
	    			if (++mProgresstime>mMaxProgresstime) {
	    				addOutputProducts();
	    				mOutputItem1 = null;
	    				mOutputItem2 = null;
	    				mOutputItem3 = null;
	    				mOutputItem4 = null;
	    				mProgresstime = 0;
	    				mMaxProgresstime = 0;
	    				mBaseMetaTileEntity.mDisplayErrorCode = 0;
	    			}
	    		} else {
	    			mBaseMetaTileEntity.mActive = false;
    				if (GT_Mod.instance.mConstantEnergy) mProgresstime = 0;
    				mBaseMetaTileEntity.mDisplayErrorCode = 1;
	    		}
	    	} else {
	    		mBaseMetaTileEntity.mActive = false;
	    		if (mBaseMetaTileEntity.getStored() > 100) checkRecipe();
	    	}
	    	
	    	if (mBaseMetaTileEntity.mTickTimer%20==0) {
	    		mBaseMetaTileEntity.worldObj.addBlockEvent(mBaseMetaTileEntity.xCoord, mBaseMetaTileEntity.yCoord, mBaseMetaTileEntity.zCoord, GT_Mod.instance.mBlocks[1].blockID, 10, mProgresstime);
	    		mBaseMetaTileEntity.worldObj.addBlockEvent(mBaseMetaTileEntity.xCoord, mBaseMetaTileEntity.yCoord, mBaseMetaTileEntity.zCoord, GT_Mod.instance.mBlocks[1].blockID, 11, mMaxProgresstime);
    		}
		}
    }
    
    private void addOutputProducts() {
    	if (mOutputItem1 != null)
	    	if (mInventory[2] == null)
	    		mInventory[2] = mOutputItem1.copy();
	    	else if (mInventory[2].isItemEqual(mOutputItem1))
	    		mInventory[2].stackSize = Math.min(mOutputItem1.getMaxStackSize(), mOutputItem1.stackSize + mInventory[2].stackSize);
    	
    	if (mOutputItem2 != null)
	    	if (mInventory[3] == null)
	    		mInventory[3] = mOutputItem2.copy();
	    	else if (mInventory[3].isItemEqual(mOutputItem2))
	    		mInventory[3].stackSize = Math.min(mOutputItem2.getMaxStackSize(), mOutputItem2.stackSize + mInventory[3].stackSize);
    	
    	if (mOutputItem3 != null)
	    	if (mInventory[4] == null)
	    		mInventory[4] = mOutputItem3.copy();
	    	else if (mInventory[4].isItemEqual(mOutputItem3))
	    		mInventory[4].stackSize = Math.min(mOutputItem3.getMaxStackSize(), mOutputItem3.stackSize + mInventory[4].stackSize);
    	
    	if (mOutputItem4 != null)
	    	if (mInventory[5] == null)
	    		mInventory[5] = mOutputItem4.copy();
	    	else if (mInventory[5].isItemEqual(mOutputItem4))
	    		mInventory[5].stackSize = Math.min(mOutputItem4.getMaxStackSize(), mOutputItem4.stackSize + mInventory[5].stackSize);
    }
    
    private boolean spaceForOutput(int aRecipe) {
    	if (mInventory[2] == null || GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput1 == null || (mInventory[2].stackSize + GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput1.stackSize <= mInventory[2].getMaxStackSize() && mInventory[2].isItemEqual(GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput1)))
    	if (mInventory[3] == null || GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput2 == null || (mInventory[3].stackSize + GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput2.stackSize <= mInventory[3].getMaxStackSize() && mInventory[3].isItemEqual(GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput2)))
    	if (mInventory[4] == null || GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput3 == null || (mInventory[4].stackSize + GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput3.stackSize <= mInventory[4].getMaxStackSize() && mInventory[4].isItemEqual(GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput3)))
    	if (mInventory[5] == null || GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput4 == null || (mInventory[5].stackSize + GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput4.stackSize <= mInventory[5].getMaxStackSize() && mInventory[5].isItemEqual(GT_Recipe.sElectrolyzerRecipes.get(aRecipe).mOutput4)))
    		return true;
    	return false;
    }
    
    private boolean checkRecipe() {
    	int tRecipe = GT_Recipe.findEqualElectrolyzerRecipeIndex(mInventory[0], mInventory[1]);

    	if (tRecipe != -1) {
    		if (spaceForOutput(tRecipe) && GT_Recipe.sElectrolyzerRecipes.get(tRecipe).isRecipeInputEqual(true, true, mInventory[0], mInventory[1])) {
	        	if (mInventory[0] != null) if (mInventory[0].stackSize == 0) mInventory[0] = null;
	        	if (mInventory[1] != null) if (mInventory[1].stackSize == 0) mInventory[1] = null;
	        	
	        	mMaxProgresstime = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mDuration;
	        	mEUt = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mEUt;
	        	
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput1 == null) {
	        		mOutputItem1 = null;
	        	} else {
		        	mOutputItem1 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput1.copy();
	        	}
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput2 == null) {
	        		mOutputItem2 = null;
	        	} else {
		        	mOutputItem2 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput2.copy();
	        	}
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput3 == null) {
	        		mOutputItem3 = null;
	        	} else {
		        	mOutputItem3 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput3.copy();
	        	}
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput4 == null) {
	        		mOutputItem4 = null;
	        	} else {
		        	mOutputItem4 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput4.copy();
	        	}
	        	return true;
    		}
    	}

    	tRecipe = GT_Recipe.findEqualElectrolyzerRecipeIndex(mInventory[6], mInventory[1]);
    	
    	if (tRecipe != -1) {
    		if (spaceForOutput(tRecipe) && GT_Recipe.sElectrolyzerRecipes.get(tRecipe).isRecipeInputEqual(true, true, mInventory[6], mInventory[1])) {
	        	if (mInventory[6] != null) if (mInventory[6].stackSize == 0) mInventory[6] = null;
	        	if (mInventory[1] != null) if (mInventory[1].stackSize == 0) mInventory[1] = null;
	        	
	        	mMaxProgresstime = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mDuration;
	        	mEUt = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mEUt;
	        	
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput1 == null) {
	        		mOutputItem1 = null;
	        	} else {
		        	mOutputItem1 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput1.copy();
	        	}
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput2 == null) {
	        		mOutputItem2 = null;
	        	} else {
		        	mOutputItem2 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput2.copy();
	        	}
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput3 == null) {
	        		mOutputItem3 = null;
	        	} else {
		        	mOutputItem3 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput3.copy();
	        	}
	        	if (GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput4 == null) {
	        		mOutputItem4 = null;
	        	} else {
		        	mOutputItem4 = GT_Recipe.sElectrolyzerRecipes.get(tRecipe).mOutput4.copy();
	        	}
	        	return true;
    		}
    	}
    	return false;
    }
    
	@Override
	public int getInvSideIndex(ForgeDirection aSide, int aFacing) {
		if (aSide == ForgeDirection.UP) return 0;
		if (aSide == ForgeDirection.DOWN) return 1;
		return 2;
	}
	
	@Override
	public int getInvSideLength(ForgeDirection aSide, int aFacing) {
		if (aSide != ForgeDirection.UP && aSide != ForgeDirection.DOWN) return 4;
		return 1;
	}
	
	
	@Override
	public int getTextureIndex(int aSide, int aFacing) {
		if (aSide == 0)
			return 32;
		if (aSide == 1)
			return 29;
		return mBaseMetaTileEntity.mActive?65:64;
	}
	
	@Override
	public String getMainInfo() {
		return "Progress:";
	}
	@Override
	public String getSecondaryInfo() {
		return (mProgresstime/20)+"secs";
	}
	@Override
	public String getTertiaryInfo() {
		return "/"+(mMaxProgresstime/20)+"secs";
	}
	@Override
	public boolean isGivingInformation() {
		return true;
	}

	@Override
	public int fill(LiquidStack resource, boolean doFill) {
		if (resource == null || resource.itemID <= 0) return 0;
		LiquidStack tLiquid = new LiquidStack(Block.waterStill, mStoredWater);
		if (!tLiquid.isLiquidEqual(resource)) return 0;
		
		int space = getCapacity() - tLiquid.amount;
		if (resource.amount <= space) {
			if (doFill && space > 0) {
				mStoredWater += resource.amount;
			}
			return resource.amount;
		} else {
			if (doFill && space > 0) {
				mStoredWater = getCapacity();
			}
			return space;
		}
	}
	
	@Override public LiquidStack getLiquid() {return new LiquidStack(Block.waterStill, mStoredWater);}
	@Override public int getTankPressure() {return -100;}
	@Override public void setLiquid(LiquidStack liquid) {}
	@Override public void setCapacity(int capacity) {}
	@Override public int getCapacity() {return 10000;}
}
