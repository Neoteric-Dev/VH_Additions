package tv.alterNERD.VaultModTweaks.integration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import iskallia.vault.config.Config;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.config.VaultJewelCuttingConfig.JewelCuttingRange;
import tv.alterNERD.VaultModTweaks.Configuration;

@Mixin(VaultJewelCuttingConfig.class)
public abstract class MixinVaultJewelCuttingConfig extends Config {
    @Shadow(remap = false)
    private float jewelCuttingModifierRemovalChance;

    @Shadow(remap = false)
    private JewelCuttingRange jewelCuttingRange;

    @Override
    protected void onLoad(Config oldConfigInstance) {
        super.onLoad(oldConfigInstance);
        if (Configuration.JEWELER_ENABLED.get()) {
            this.jewelCuttingModifierRemovalChance = Configuration.JEWELER_CHANCE.get().floatValue();
            this.jewelCuttingRange = new JewelCuttingRange(Configuration.JEWELS_MIN.get(), Configuration.JEWELS_MAX.get());
        }
    }

    @Shadow(remap = false)
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Shadow(remap = false)
    @Override
    protected void reset() {
        throw new UnsupportedOperationException("Unimplemented method 'reset'");
    }
}
