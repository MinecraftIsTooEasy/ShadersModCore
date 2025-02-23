package shadersmodcore;

import net.fabricmc.api.ClientModInitializer;
import net.xiaoyu233.fml.ModResourceManager;

public class ShadersModCoreInit implements ClientModInitializer {
   public static String shadersModID = "shadersmodcore";

   @Override
   public void onInitializeClient() {
      ModResourceManager.addResourcePackDomain(shadersModID);
   }
}
