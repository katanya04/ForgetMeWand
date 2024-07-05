package me.katanya04.forgetmewand.items;

import me.katanya04.forgetmewand.ForgetMeWand;
import me.katanya04.forgetmewand.utils.Utils;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;

public class ModItems {
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> itemGroup.addAfter(Items.FISHING_ROD, ModItems.FORGET_ME_WAND));
    }
    public static final Item FORGET_ME_WAND = register(
            new Item(new Item.Settings().rarity(Rarity.EPIC).maxDamage(15).component(DataComponentTypes.TOOL, MaceItem.createToolComponent())) {
                @Override
                public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("itemTooltip.forgetmewand.forget_me_wand").formatted(Formatting.AQUA));
                }

                @Override
                public boolean canRepair(ItemStack stack, ItemStack ingredient) {
                    return ingredient.isOf(Items.ECHO_SHARD);
                }

                @Override
                public ActionResult useOnBlock(ItemUsageContext context) {
                    if (context.getWorld().isClient)
                        return ActionResult.PASS;
                    PlayerEntity player = context.getPlayer();
                    BlockEntity vault = context.getWorld().getBlockEntity(context.getBlockPos());
                    if (vault == null || !vault.getType().equals(BlockEntityType.VAULT) || player == null
                            || player.isSneaking())
                        return ActionResult.PASS;
                    if (!Utils.vaultContainsPlayer(vault, player))
                        return ActionResult.PASS;
                    if (!Utils.removePlayerFromVault(vault, player))
                        return ActionResult.PASS;
                    boolean isOminous = vault.getCachedState().get(VaultBlock.OMINOUS);
                    context.getStack().damage(isOminous ? 3 : 1, player, LivingEntity.getSlotForHand(context.getHand()));
                    player.playSoundToPlayer(SoundEvents.ITEM_MACE_SMASH_GROUND, SoundCategory.BLOCKS, 1.f, 1.f);
                    Utils.summonParticlesRandSpeed(ParticleTypes.CLOUD, vault.getPos().toCenterPos(), -0.5d, 0.5d, 20);
                    return ActionResult.SUCCESS;
                }
            },
            "forget_me_wand"
    );
    private static Item register(Item item, String id) {
        Identifier itemID = Identifier.of(ForgetMeWand.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemID, item);
    }
}
