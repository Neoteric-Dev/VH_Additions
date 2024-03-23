/**
 * Copyright 2023 alterNERDtive.
 * 
 * This file is part of Vault Mod Tweaks.
 * 
 * Vault Mod Tweaks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vault Mod Tweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Vault Mod Tweaks.  If not, see <https://www.gnu.org/licenses/>.
 */
package tv.alterNERD.VaultModTweaks;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import iskallia.vault.init.ModConfigs;
import tv.alterNERD.VaultModTweaks.integration.PacketHandler;
import tv.alterNERD.VaultModTweaks.integration.TagManager;
import tv.alterNERD.VaultModTweaks.integration.mixin.MixinWandItem;
import tv.alterNERD.VaultModTweaks.util.I18n;

@Mod("the_vault_tweaks")
public class VaultModTweaks
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "the_vault_tweaks";

    public VaultModTweaks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::configLoaded);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::SetupPacketHandler);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configuration.CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENTCONFIG);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Vault Mod Tweaks by alterNERDtive");
    }

    @SubscribeEvent
    public void SetupPacketHandler(FMLCommonSetupEvent event)
    {
        event.enqueueWork(PacketHandler::Register);
    }

    private void gatherData(final GatherDataEvent event) {
        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(event.getGenerator(), MOD_ID, event.getExistingFileHelper());
        event.getGenerator().addProvider(blockTagsProvider);
        event.getGenerator().addProvider(
            (DataProvider) new TagManager(
                event.getGenerator(),
                blockTagsProvider,
                MOD_ID,
                event.getExistingFileHelper())
        );
    }

    private void configLoaded(final ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            LOGGER.info(I18n.get("the_vault_tweaks.log.config.reloadvaultconfig"));
            ModConfigs.register();
        }
    }
}
