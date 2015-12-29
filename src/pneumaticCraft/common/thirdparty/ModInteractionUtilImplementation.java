package pneumaticCraft.common.thirdparty;

public class ModInteractionUtilImplementation extends ModInteractionUtils{
    /* @Override TODO BC dep
     @Optional.Method(modid = ModIds.BUILDCRAFT)
     protected boolean isBCWrench(Item item){
         return item instanceof IToolWrench;
     }

     @Override
     @Optional.Method(modid = ModIds.COFH_CORE)
     protected boolean isTEWrench(Item item){
         return item instanceof IToolHammer;
     }

     @Override
     @Optional.Method(modid = ModIds.BUILDCRAFT)
     public ItemStack exportStackToBCPipe(TileEntity te, ItemStack stack, EnumFacing side){
         if(isBCPipe(te)) {
             int amount = ((IPipeTile)te).injectItem(stack, true, side);
             stack.stackSize -= amount;
             if(stack.stackSize <= 0) stack = null;
         }
         return stack;
     }

     @Override
     @Optional.Method(modid = ModIds.BUILDCRAFT)
     public boolean isBCPipe(TileEntity te){
         return te instanceof IPipeTile && ((IPipeTile)te).getPipeType() == IPipeTile.PipeType.ITEM;
     }

     @Override TODO BC TE dep
     @Optional.Method(modid = ModIds.TE)
     public ItemStack exportStackToTEPipe(TileEntity te, ItemStack stack, EnumFacing side){
         return stack;//TODO when TE updates for 1.7
     }

     @Override
     @Optional.Method(modid = ModIds.TE)
     public boolean isTEPipe(TileEntity te){
         return false;//TODO when TE updates for 1.7
     }*/

    /**
     *  ForgeMultipart
     */

    /*  @Override TODO FMP dep
      @Optional.Method(modid = ModIds.FMP)
      public IPneumaticMachine getMachine(TileEntity te){
          if(te instanceof TileMultipart) {
              return FMP.getMultiPart((TileMultipart)te, IPneumaticMachine.class);
          } else {
              return super.getMachine(te);
          }
      }

      @Override
      @Optional.Method(modid = ModIds.FMP)
      public IPneumaticWrenchable getWrenchable(TileEntity te){
          if(te instanceof TileMultipart) {
              return FMP.getMultiPart((TileMultipart)te, IPneumaticWrenchable.class);
          } else {
              return super.getWrenchable(te);
          }
      }

      @Override
      @Optional.Method(modid = ModIds.FMP)
      public boolean isMultipart(TileEntity te){
          return te instanceof TileMultipart;
      }

      @Override
      @Optional.Method(modid = ModIds.FMP)
      public boolean isMultipartWiseConnected(Object part, EnumFacing dir){
          return ((PartPressureTube)part).passesOcclusionTest(dir);
      }

      @Override
      @Optional.Method(modid = ModIds.FMP)
      public void sendDescriptionPacket(IPneumaticPosProvider te){
          if(te instanceof TMultiPart) {
              ((TMultiPart)te).sendDescUpdate();
          } else {
              super.sendDescriptionPacket(te);
          }
      }

      @Override
      @Optional.Method(modid = ModIds.FMP)
      public TileEntityPressureTube getTube(Object potentialTube){
          if(potentialTube instanceof PartPressureTube) {
              return ((PartPressureTube)potentialTube).getTube();
          } else if(potentialTube instanceof TileMultipart) {
              PartPressureTube tube = FMP.getMultiPart((TileMultipart)potentialTube, PartPressureTube.class);
              return tube != null ? tube.getTube() : null;
          } else {
              return super.getTube(potentialTube);
          }
      }

      @Override
      @Optional.Method(modid = ModIds.FMP)
      public void removeTube(TileEntity te){
          if(te instanceof TileMultipart) {
              PartPressureTube tube = FMP.getMultiPart((TileMultipart)te, PartPressureTube.class);
              if(tube != null) {
                  List<ItemStack> drops = BlockPressureTube.getModuleDrops(tube.getTube());
                  for(ItemStack drop : drops) {
                      EntityItem entity = new EntityItem(te.getWorldObj(), te.getPos().getX() + 0.5, te.getPos().getY() + 0.5, te.getPos().getZ() + 0.5);
                      entity.setEntityItemStack(drop);
                      te.getWorldObj().spawnEntityInWorld(entity);
                  }
                  ((TileMultipart)te).remPart(tube);
              }
          } else {
              super.removeTube(te);
          }
      }

      @Override
      @Optional.Method(modid = ModIds.FMP)
      public boolean occlusionTest(AxisAlignedBB aabb, TileEntity te){
          if(te instanceof TileMultipart) {
              return ((TileMultipart)te).occlusionTest(((TileMultipart)te).partList(), new NormallyOccludedPart(new Cuboid6(aabb)));
          } else {
              return super.occlusionTest(aabb, te);
          }
      }*/
}
