package me.katanya04.forgetmewand.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.storage.NbtReadView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    private Utils() {}
    public static UUID NbtToUUID(NbtElement nbt) {
        Optional<int[]> uuidArray = nbt.asIntArray();
        return uuidArray.map(Uuids::toUuid).orElse(null);
    }
    public static NbtElement UUIDtoNbt(UUID uuid) {
        return new NbtIntArray(Uuids.toIntArray(uuid));
    }
    public static NbtCompound getVaultNbt(BlockEntity vault) {
        if (!vault.getType().equals(BlockEntityType.VAULT)
                || vault.getWorld() == null || vault.getWorld().getServer() == null)
            return new NbtCompound();
        return vault.createNbt(vault.getWorld().getServer().getRegistryManager());
    }
    public static void setVaultNbt(BlockEntity vault, NbtCompound nbt) {
        if (!vault.getType().equals(BlockEntityType.VAULT)
                || vault.getWorld() == null || vault.getWorld().getServer() == null)
            return;
        vault.read(NbtReadView.create(ErrorReporter.EMPTY, vault.getWorld().getRegistryManager(), nbt));
    }
    public static Set<UUID> getRewardedPlayers(BlockEntity vault) {
        NbtCompound nbt = getVaultNbt(vault);
        Optional<NbtList> playerList = nbt.getCompound("server_data").map(c -> c.getListOrEmpty("rewarded_players"));
        return playerList.map(nbtElements -> nbtElements.stream().map(Utils::NbtToUUID).filter(Objects::nonNull)
                .collect(Collectors.toSet())).orElseGet(Set::of);
    }
    public static boolean vaultContainsPlayer(BlockEntity vault, PlayerEntity player) {
        return vaultContainsPlayer(vault, player.getUuid());
    }
    public static boolean vaultContainsPlayer(BlockEntity vault, UUID playerUUID) {
        return getRewardedPlayers(vault).contains(playerUUID);
    }
    public static boolean removePlayerFromVault(BlockEntity vault, PlayerEntity player) {
        return removePlayerFromVault(vault, player.getUuid());
    }
    public static boolean removePlayerFromVault(BlockEntity vault, UUID playerUUID) {
        Set<UUID> rewardedPlayersPrevious = getRewardedPlayers(vault);
        if (!rewardedPlayersPrevious.contains(playerUUID))
            return false;
        rewardedPlayersPrevious.remove(playerUUID);
        NbtList rewardedPlayersUpdated = new NbtList();
        rewardedPlayersPrevious.stream().map(Utils::UUIDtoNbt).forEach(rewardedPlayersUpdated::add);
        NbtCompound nbt = getVaultNbt(vault);
        nbt.getCompound("server_data").ifPresent(c -> c.put("rewarded_players", rewardedPlayersUpdated));
        setVaultNbt(vault, nbt);
        return true;
    }
    public static void summonParticlesRandSpeed(ParticleEffect particle, Vec3d pos, double minSpeed, double maxSpeed, int amount) {
        ParticleManager particleManager = MinecraftClient.getInstance().particleManager;
        for (int i = 0; i < amount; i++) {
            particleManager.addParticle(particle, pos.getX(), pos.getY(), pos.getZ(), randomDoubleInRange(minSpeed, maxSpeed),
                    randomDoubleInRange(minSpeed, maxSpeed), randomDoubleInRange(minSpeed, maxSpeed));
        }
    }
    public static double randomDoubleInRange(double min, double max) {
        return min + (max - min) * new Random().nextDouble();
    }
}
