package blusunrize.immersiveengineering.common.blocks.multiblocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.BlockMetalDecoration;
import blusunrize.immersiveengineering.common.blocks.metal.BlockMetalMultiblocks;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;

public class MultiblockDieselGenerator implements IMultiblock
{
	public static MultiblockDieselGenerator instance = new MultiblockDieselGenerator();

	static ItemStack[][][] structure = new ItemStack[3][5][3];
	static{
		for(int h=0;h<3;h++)
			for(int l=0;l<5;l++)
				for(int w=0;w<3;w++)
					if(h!=2 || l!=0)
					{
						int m = l==0?BlockMetalDecoration.META_generator: l==4?BlockMetalDecoration.META_radiator: BlockMetalDecoration.META_heavyEngineering;
						structure[h][l][w]=new ItemStack(IEContent.blockMetalDecoration,1,m);
					}
	}
	@Override
	public ItemStack[][][] getStructureManual()
	{
		return structure;
	}

	@Override
	public boolean isBlockTrigger(Block b, int meta)
	{
		return b==IEContent.blockMetalDecoration && (meta==BlockMetalDecoration.META_radiator||meta==BlockMetalDecoration.META_generator);
	}

	@Override
	public boolean createStructure(World world, int x, int y, int z, int side, EntityPlayer player)
	{
		if(side==0||side==1)
			return false;

		int startX=x;
		int startY=y;
		int startZ=z;
		if(world.getBlockMetadata(x, y, z)==BlockMetalDecoration.META_generator)
		{
			startX += (side==4?4: side==5?-4: 0);
			startZ += (side==2?4: side==3?-4: 0);

			side = ForgeDirection.OPPOSITES[side];
		}

		for(int l=0;l<5;l++)
			for(int w=-1;w<=1;w++)
				for(int h=-1;h<=(l==4?0:1);h++)
				{
					int xx = startX+ (side==4?l: side==5?-l: side==2?-w : w);
					int yy = startY+ h;
					int zz = startZ+ (side==2?l: side==3?-l: side==5?-w : w);
					if(l==0)
					{
						if(!(world.getBlock(xx, yy, zz).equals(IEContent.blockMetalDecoration) && world.getBlockMetadata(xx, yy, zz)==BlockMetalDecoration.META_radiator))
						{
							System.out.println("break: "+xx+","+yy+","+zz+", no radiator");
							return false;
						}
					}
					else if(l==4)
					{
						if(!(world.getBlock(xx, yy, zz).equals(IEContent.blockMetalDecoration) && world.getBlockMetadata(xx, yy, zz)==BlockMetalDecoration.META_generator))
						{
							System.out.println("break: "+xx+","+yy+","+zz+", no generator");
							return false;
						}
					}
					else
					{
						if(!(world.getBlock(xx, yy, zz).equals(IEContent.blockMetalDecoration) && world.getBlockMetadata(xx, yy, zz)==BlockMetalDecoration.META_heavyEngineering))
						{
							System.out.println("break: "+xx+","+yy+","+zz+", no engine");
							return false;
						}
					}
				}


		for(int l=0;l<5;l++)
			for(int w=-1;w<=1;w++)
				for(int h=-1;h<=(l==4?0:1);h++)
				{
					int xx = (side==4?l: side==5?-l: side==2?-w : w);
					int yy = h;
					int zz = (side==2?l: side==3?-l: side==5?-w : w);

					world.setBlock(startX+xx, startY+yy, startZ+zz, IEContent.blockMetalMultiblocks, BlockMetalMultiblocks.META_dieselGenerator, 3);
					if(world.getTileEntity(startX+xx, startY+yy, startZ+zz) instanceof TileEntityDieselGenerator)
					{
						TileEntityDieselGenerator tile = (TileEntityDieselGenerator)world.getTileEntity(startX+xx,startY+yy,startZ+zz);
						tile.facing=ForgeDirection.OPPOSITES[side];
						tile.formed=true;
						tile.pos = l*9 + (h+1)*3 + (w+1);
						tile.offset = new int[]{(side==5?(l-3): side==4?(3-l): w),h,(side==3?(l-3): side==2?(3-l): w)};
					}
				}
		return true;
	}

}
