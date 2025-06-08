package shadersmodcore.mixin.client.render;

import net.minecraft.GLAllocation;
import net.minecraft.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import shadersmodcore.api.ModelRendererAccessor;

@Mixin(ModelRenderer.class)
public class ModelRendererMixin implements ModelRendererAccessor {
    @Shadow private boolean compiled;
    @Shadow private int displayList;

    @Override
    public void resetDisplayList() {
        if (!compiled) {
            GLAllocation.deleteDisplayLists(this.displayList);
            this.displayList = 0;
            this.compiled = false;
        }
    }

}
