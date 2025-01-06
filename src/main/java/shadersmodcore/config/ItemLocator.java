package shadersmodcore.config;

import net.minecraft.Item;
import net.minecraft.ResourceLocation;

public class ItemLocator implements IObjectLocator {
   public Object getObject(ResourceLocation var1) {
      Item[] var2 = Item.itemsList;

       for (Item item : var2) {
           if (item.getUnlocalizedName().equals(var1.toString())) {
               return item;
           }
       }

      return null;
   }
}
