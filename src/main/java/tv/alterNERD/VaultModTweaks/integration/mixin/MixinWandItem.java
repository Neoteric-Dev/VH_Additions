package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModParticles;
import iskallia.vault.item.BasicItem;
import iskallia.vault.item.gear.WandItem;
import iskallia.vault.util.calc.AbilityPowerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tv.alterNERD.VaultModTweaks.integration.AdditionalGearAttributes;
import tv.alterNERD.VaultModTweaks.integration.PacketHandler;
import tv.alterNERD.VaultModTweaks.integration.WandUseMessage;

/**
 * Modifies the {@link WandItem} to be used in the mainhand instead of offhand.
 */
@Mixin(WandItem.class)
public abstract class MixinWandItem extends BasicItem
{
    //region Initialisation

    /**
     * A constructor for the {@link MixinWandItem}.
     * @param id Passing through the {@link ResourceLocation} id as an argument.
     */
    public MixinWandItem(ResourceLocation id)
    {
        super(id);
    }

    //endregion

    //region Mixins

    /**
     * Injects a new event listener onto the Forge event bus.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    void InjectEventBus(CallbackInfo callbackInformation)
    {
        MinecraftForge.EVENT_BUS.addListener(this::OnWandUseMiss);
        MinecraftForge.EVENT_BUS.addListener(this::OnWandUseHit);
    }

    /**
     * Inject new the alternate hand into the {@link WandItem#getIntendedSlot(ItemStack)} method.
     * @param stack Passing through the {@link ItemStack} stack as an argument.
     * @param callbackInformationReturnable The {@link CallbackInfoReturnable} for the injection.
     */
    @Inject(method = "getIntendedSlot", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void InjectMainhandIntendedSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callbackInformationReturnable)
    {
        callbackInformationReturnable.setReturnValue(EquipmentSlot.MAINHAND);
        callbackInformationReturnable.cancel();
    }

    /**
     * Sets the wand to damage the {@link LivingEntity} target.
     * @param stack Passing through the {@link ItemStack} stack as an argument.
     * @param target Passing through the {@link LivingEntity} target as an argument.
     * @param attacker Passing through the {@link LivingEntity} attacker as an argument.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        if (attacker instanceof Player player)
        {
            stack.hurtAndBreak(1, player, (targetEntity) -> targetEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            target.hurt(DamageSource.MAGIC, AbilityPowerHelper.getAbilityPower(player) * VaultGearData.read(stack).get(AdditionalGearAttributes.MAGIC_POWER, VaultGearAttributeTypeMerger.floatSum()));

            return true;
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    /**
     * Adds an event listener to the {@link WandItem} to allow the cast the Wand in the case of a miss.
     * @param event The {@link PlayerInteractEvent.LeftClickEmpty} event.
     */
    @SubscribeEvent
    public void OnWandUseMiss(PlayerInteractEvent.LeftClickEmpty event)
    {
        if (event.getItemStack().getItem() instanceof WandItem)
        {
            Player player = event.getPlayer();
            Vec3 position = player.position().add(player.getForward());
            Vec3 lookAngle = player.getLookAngle().scale(VaultGearData.read(player.getMainHandItem()).get(ModGearAttributes.ATTACK_RANGE, VaultGearAttributeTypeMerger.doubleSum()));
            event.getWorld().addParticle(ModParticles.FIREBALL_CLOUD.get(), position.x, position.y + player.getEyeHeight(), position.z, lookAngle.x, lookAngle.y, lookAngle.z);

            PacketHandler.sendToServer(new WandUseMessage());
        }
    }

    /**
     * Adds an event listener to the {@link WandItem} to allow the cast the Wand in the case of an entity hit.
     * @param event The {@link PlayerInteractEvent.EntityInteract} event.
     */
    @SubscribeEvent
    public void OnWandUseHit(LivingDamageEvent event)
    {
        if (event.getSource().getEntity() instanceof ServerPlayer player && player.getMainHandItem().getItem() instanceof WandItem)
        {
            Vec3 position = player.position().add(player.getForward());
            Vec3 lookAngle = player.getLookAngle().scale(VaultGearData.read(player.getMainHandItem()).get(ModGearAttributes.ATTACK_RANGE, VaultGearAttributeTypeMerger.doubleSum()));
            player.getLevel().addParticle(ModParticles.FIREBALL_CLOUD.get(), position.x, position.y + player.getEyeHeight(), position.z, lookAngle.x, lookAngle.y, lookAngle.z);

            PacketHandler.sendToServer(new WandUseMessage());
        }
    }

    //endregion
}