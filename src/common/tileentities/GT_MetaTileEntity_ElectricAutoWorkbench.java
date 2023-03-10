package gregtechmod.common.tileentities;

import gregtechmod.GT_Mod;
import gregtechmod.api.BaseMetaTileEntity;
import gregtechmod.api.MetaTileEntity;
import gregtechmod.common.GT_ModHandler;
import gregtechmod.common.GT_OreDictUnificator;
import gregtechmod.common.GT_Utility;
import gregtechmod.common.items.GT_MetaItem_SmallDust;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class GT_MetaTileEntity_ElectricAutoWorkbench extends MetaTileEntity {
	
	public int mMode = 0, mCurrentSlot = 0, mThroughPut = 0, mTicksUntilNextUpdate = 20;
	
	public GT_MetaTileEntity_ElectricAutoWorkbench(int aID, String mName, String mNameRegional) {
		super(aID, mName, mNameRegional);
	}
	
	public GT_MetaTileEntity_ElectricAutoWorkbench() {
		
	}

	@Override public boolean isSimpleMachine()						{return false;}
	@Override public boolean isValidSlot(int aIndex)				{return aIndex < 19;}
	@Override public boolean isFacingValid(int aFacing)				{return true;}
	@Override public boolean isEnetInput() 							{return true;}
	@Override public boolean isEnetOutput() 						{return true;}
	@Override public boolean isInputFacing(short aSide)				{return !isOutputFacing(aSide);}
	@Override public boolean isOutputFacing(short aSide)			{return ForgeDirection.getOrientation(mBaseMetaTileEntity.mFacing).getOpposite() == ForgeDirection.getOrientation(aSide);}
	@Override public int getMinimumStoredEU()						{return 3000;}
    @Override public int maxEUInput()								{return 32;}
    @Override public int maxEUOutput()								{return mThroughPut%2==0?32:0;}
    @Override public int maxEUStore()								{return 10000;}
	@Override public int getInvSize()								{return 30;}
	@Override public void onRightclick(EntityPlayer aPlayer)		{aPlayer.openGui(GT_Mod.instance, 100, mBaseMetaTileEntity.worldObj, mBaseMetaTileEntity.xCoord, mBaseMetaTileEntity.yCoord, mBaseMetaTileEntity.zCoord);}
	@Override public boolean isAccessAllowed(EntityPlayer aPlayer)	{return true;}
    
	@Override
	public MetaTileEntity newMetaEntity(BaseMetaTileEntity aTileEntity) {
		return new GT_MetaTileEntity_ElectricAutoWorkbench();
	}

	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
    	aNBT.setInteger("mMode", mMode);
    	aNBT.setInteger("mThroughPut", mThroughPut);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
		mMode = aNBT.getInteger("mMode");
		mThroughPut = aNBT.getInteger("mThroughPut");
	}
	
	public void switchMode() {
		mMode = (mMode + 1) % 7;
		mInventory[28] = null;
	}

	public void switchThrough() {
		mThroughPut = (mThroughPut + 1) % 4;
	}
	
	public void onPostTick() {
		if (!mBaseMetaTileEntity.worldObj.isRemote && mBaseMetaTileEntity.getStored() >= 3000 && --mTicksUntilNextUpdate<1) {
			mTicksUntilNextUpdate = 40;
			if (mInventory[18] == null) {
				ItemStack[] tRecipe = new ItemStack[9];
				ItemStack tTempStack = null, tOutput = null;
				
				if (mInventory[8] != null && mThroughPut < 2 && mMode != 0) {
					if (mInventory[18] == null) {
						mInventory[18] = mInventory[8];
						mInventory[8] = null;
					}
				} else {
					mCurrentSlot = (mCurrentSlot+1)%8;
					for (int i = 0; i < 7 && mInventory[mCurrentSlot] == null; i++)
						mCurrentSlot = (mCurrentSlot+1)%8;
					switch (mMode) {
					case 0:
						if (mInventory[mCurrentSlot] != null && !isItemTypeExcluded(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						for (int i = 0; i < 9; i++) {
							tRecipe[i] = mInventory[i+19];
							if (tRecipe[i] != null) {
								tRecipe[i] = tRecipe[i].copy();
								tRecipe[i].stackSize = 1;
							}
						}
						break;
					case 1:
						if (isItemTypeExcluded(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = mInventory[mCurrentSlot].copy();
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						if (GT_ModHandler.getRecipeOutput(tRecipe) == null) {
							tRecipe[1] = tTempStack;
							tRecipe[3] = tTempStack;
							tRecipe[4] = tTempStack;
						} else break;
						if (GT_ModHandler.getRecipeOutput(tRecipe) == null) {
							tRecipe[2] = tTempStack;
							tRecipe[5] = tTempStack;
							tRecipe[6] = tTempStack;
							tRecipe[7] = tTempStack;
							tRecipe[8] = tTempStack;
						} else break;
						if (GT_ModHandler.getRecipeOutput(tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 2:
						if (isItemTypeExcluded(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = mInventory[mCurrentSlot].copy();
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						if (GT_ModHandler.getRecipeOutput(tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 3:
						if (isItemTypeExcluded(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = mInventory[mCurrentSlot].copy();
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						tRecipe[1] = tTempStack;
						tRecipe[3] = tTempStack;
						tRecipe[4] = tTempStack;
						if (GT_ModHandler.getRecipeOutput(tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 4:
						if (isItemTypeExcluded(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = mInventory[mCurrentSlot].copy();
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						tRecipe[1] = tTempStack;
						tRecipe[2] = tTempStack;
						tRecipe[3] = tTempStack;
						tRecipe[4] = tTempStack;
						tRecipe[5] = tTempStack;
						tRecipe[6] = tTempStack;
						tRecipe[7] = tTempStack;
						tRecipe[8] = tTempStack;
						if (GT_ModHandler.getRecipeOutput(tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 5:
						if (isItemTypeExcluded(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = mInventory[mCurrentSlot].copy();
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						
						tOutput = GT_OreDictUnificator.get(tTempStack);
						
						if (tOutput != null && tOutput.isItemEqual(tTempStack)) tOutput = null;
						
						if (tOutput == null) {
							tRecipe[0] = null;
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
						}
						break;
					case 6:
						if (isItemTypeExcluded(mInventory[mCurrentSlot]) || !(mInventory[mCurrentSlot].getItem() instanceof GT_MetaItem_SmallDust)) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = mInventory[mCurrentSlot].copy();
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						tRecipe[1] = tTempStack;
						tRecipe[3] = tTempStack;
						tRecipe[4] = tTempStack;
						if (GT_ModHandler.getRecipeOutput(tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					}
				}
				
				if (tOutput == null) tOutput = GT_ModHandler.getRecipeOutput(tRecipe);
	
				if (tOutput != null || mMode == 0) mInventory[28] = tOutput;
				
				if (tOutput != null) {
					if (mMode < 5 && (tTempStack = GT_OreDictUnificator.get(tOutput)) != null) {
						tTempStack.stackSize = tOutput.stackSize;
						tOutput = tTempStack;
					}
					
					mInventory[28] = tOutput.copy();
					ArrayList<ItemStack> tList = recipeContent(tRecipe), tContent = benchContent();
					if (tList.size() > 0 && tContent.size() > 0) {
						
						boolean success = true;
						for (int i = 0; i < tList.size() && success; i++) {
							success = false;
							for (int j = 0; j < tContent.size() && !success; j++) {
								if (tList.get(i).itemID == tContent.get(j).itemID) {
									if (tList.get(i).getItemDamage() == tContent.get(j).getItemDamage()) {
										if (tList.get(i).stackSize <= tContent.get(j).stackSize) {
											success = true;
										}
									}
								}
							}
						}
						
						if (success) {
							int tCellCount = -GT_Mod.getCapsuleCellContainerCount(tOutput)*tOutput.stackSize;
							
							for (int i = 8; i > -1; i--) {
								for (int j = 8; j > -1; j--) {
									if (tRecipe[i] != null && mInventory[j] != null) {
										if (tRecipe[i].itemID == mInventory[j].itemID) {
											if (tRecipe[i].getItemDamage() == mInventory[j].getItemDamage()) {
												if (mInventory[j].getItem().hasContainerItem()) {
													ItemStack tStack = mInventory[j].getItem().getContainerItemStack(mInventory[j]);
													if (tStack != null) {
														for (int k = 9; k < 18; k++) {
															if (mInventory[k] == null) {
																mInventory[k] = tStack.copy();
																break;
															} else if (mInventory[k].isItemEqual(tStack) && mInventory[k].stackSize + tStack.stackSize <= tStack.getMaxStackSize()) {
																mInventory[k].stackSize += tStack.stackSize;
																break;
															}
														}
													}
												} else if (GT_Mod.getCapsuleCellContainerCount(mInventory[j]) != 0) {
													tCellCount += GT_Mod.getCapsuleCellContainerCount(mInventory[j]);
												}
												mBaseMetaTileEntity.decrStackSize(j, 1);
												break;
											}
										}
									}
								}
							}
							
							if (tCellCount > 0) {
								for (int k = 9; k < 18; k++) if (mInventory[k] == null || mInventory[k].isItemEqual(GT_ModHandler.getIC2Item("cell", 1))) {
									if (mInventory[k] == null) {
										mInventory[k] = GT_ModHandler.getIC2Item("cell", 1);
										tCellCount--;
									}
									while (mInventory[k].stackSize < 64 && tCellCount-- > 0) mInventory[k].stackSize++;
									break;
								}
							}
							
							mInventory[18] = tOutput.copy();
							mBaseMetaTileEntity.decreaseStoredEnergy(mMode==5||mMode==6?500:2500, true);
							mTicksUntilNextUpdate = 1;
						} else {
							if (mInventory[8] != null && mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[8];
								mInventory[8] = null;
								mTicksUntilNextUpdate = 5;
							}
						}
					}
				}
			}
			
			if (mThroughPut < 2) {
				TileEntity tTileEntity1, tTileEntity2;
				
				int xDir = ForgeDirection.getOrientation(mBaseMetaTileEntity.mFacing).offsetX, yDir = ForgeDirection.getOrientation(mBaseMetaTileEntity.mFacing).offsetY, zDir = ForgeDirection.getOrientation(mBaseMetaTileEntity.mFacing).offsetZ;
				
				tTileEntity1 = mBaseMetaTileEntity;
				tTileEntity2 = mBaseMetaTileEntity.worldObj.getBlockTileEntity(mBaseMetaTileEntity.xCoord-xDir, mBaseMetaTileEntity.yCoord-yDir, mBaseMetaTileEntity.zCoord-zDir);
				
				if (tTileEntity1 != null && tTileEntity2 != null) {
					if (tTileEntity1 instanceof IInventory && tTileEntity2 instanceof IInventory) {
						mBaseMetaTileEntity.decreaseStoredEnergy(GT_Utility.moveOneItemStack((IInventory)tTileEntity1, (IInventory)tTileEntity2, ForgeDirection.getOrientation(mBaseMetaTileEntity.mFacing).getOpposite(), ForgeDirection.getOrientation(mBaseMetaTileEntity.mFacing), null, 1, 1, 1, false)*(getInvSize()>10?2:1), true);
					}
				}
			}
		}
		
		
		if (!mBaseMetaTileEntity.worldObj.isRemote && mBaseMetaTileEntity.mTickTimer > 50) mBaseMetaTileEntity.mActive = mBaseMetaTileEntity.mRedstone;
	}
	
	private boolean isItemTypeExcluded(ItemStack aStack) {
		if (aStack == null) return true;
		for (int i = 19; i < 28; i++) if (mInventory[i] != null && mInventory[i].isItemEqual(aStack)) return true;
		return false;
	}
	
	private ArrayList<ItemStack> recipeContent(ItemStack[] tRecipe) {
		ArrayList<ItemStack> tList = new ArrayList<ItemStack>();
		for (int i = 0; i < 9; i++) {
			if (tRecipe[i] != null) {
				boolean temp = false;
				for (int j = 0; j < tList.size(); j++) {
					if (tRecipe[i].itemID == tList.get(j).itemID) {
						if (tRecipe[i].getItemDamage() == tList.get(j).getItemDamage()) {
							tList.get(j).stackSize++;
							temp = true;
							break;
						}
					}
				}
				if (!temp) tList.add(tRecipe[i].copy());
			}
		}
		return tList;
	}

	private ArrayList<ItemStack> benchContent() {
		ArrayList<ItemStack> tList = new ArrayList<ItemStack>();
		for (int i = 0; i < 9; i++) {
			if (mInventory[i] != null) {
				boolean temp = false;
				for (int j = 0; j < tList.size(); j++) {
					if (mInventory[i].itemID == tList.get(j).itemID) {
						if (mInventory[i].getItemDamage() == tList.get(j).getItemDamage()) {
							tList.get(j).stackSize += mInventory[i].stackSize;
							temp = true;
							break;
						}
					}
				}
				if (!temp) tList.add(mInventory[i].copy());
			}
		}
		return tList;
	}
	
	
	@Override
	public int getInvSideIndex(ForgeDirection aSide, int aFacing) {
		if (aSide == ForgeDirection.getOrientation(aFacing))
			return 0;
		else
			if (aSide.getOpposite() == ForgeDirection.getOrientation(aFacing))
				return 18;
		return 9;
	}
	
	@Override
	public int getInvSideLength(ForgeDirection aSide, int aFacing) {
		if (aSide == ForgeDirection.getOrientation(aFacing))
			return 9;
		else
			if (aSide.getOpposite() == ForgeDirection.getOrientation(aFacing))
				return 1;
		return 9;
	}
	
	
	@Override
	public int getTextureIndex(int aSide, int aFacing) {
		if (aSide == aFacing)
			return 112;
		if (ForgeDirection.getOrientation(aSide).getOpposite() == ForgeDirection.getOrientation(aFacing))
			return 113;
		return 114;
	}
}
