package com.flysword.entity;

import com.flysword.key.ModKeys;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class EntitySword extends EntityLiving {

    private static final String NBT_KEY_RENDER_ITEM = "RenderItem";
    private static final String NBT_KEY_OWNER_UUID = "OwnerUUID";

    private static final DataParameter<ItemStack> SWORD_ITEM_STACK = EntityDataManager.createKey(EntitySword.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntitySword.class, DataSerializers.BYTE);
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntitySword.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    private ItemStack renderItemStack;

    public EntitySword(World worldIn) {
        super(worldIn);
        setEntityInvulnerable(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SWORD_ITEM_STACK, ItemStack.EMPTY);
        this.dataManager.register(CONTROL_STATE, (byte) 0);
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
    }

    public void setItemStack(ItemStack itemStack) {
        this.dataManager.set(SWORD_ITEM_STACK, itemStack);
    }

    public ItemStack getItemStack() {
        return this.dataManager.get(SWORD_ITEM_STACK);
    }

    public ItemStack getRenderItemStack() {
        if (renderItemStack == null) {
            renderItemStack = new ItemStack(getItemStack().getItem());
        }
        return renderItemStack;
    }

    public UUID getOwnerId() {
        return (UUID) ((Optional) this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
    }

    public EntityLivingBase getOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean isOwner(Entity entityIn) {
        return entityIn instanceof EntityPlayer && entityIn == this.getOwner();
    }


    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey(NBT_KEY_RENDER_ITEM, 10)) {
            this.setItemStack(new ItemStack(compound.getCompoundTag(NBT_KEY_RENDER_ITEM)));
        }
        if (compound.hasKey(NBT_KEY_OWNER_UUID)) {
            this.setOwnerId(compound.getUniqueId(NBT_KEY_OWNER_UUID));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (!this.getItemStack().isEmpty()) {
            compound.setTag(NBT_KEY_RENDER_ITEM, this.getItemStack().writeToNBT(new NBTTagCompound()));
        }
        if (this.getOwnerId() != null) {
            compound.setUniqueId(NBT_KEY_OWNER_UUID, getOwnerId());
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    private EntityPlayer getControllingPlayer() {
        Entity entity = getControllingPassenger();
        if (entity instanceof EntityPlayer) {
            return (EntityPlayer) getControllingPassenger();
        }

        return null;
    }

    @Override
    public void updatePassenger(@Nonnull Entity passenger) {
        super.updatePassenger(passenger);

        if (passenger instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) passenger;
            this.renderYawOffset = entityliving.renderYawOffset;
        }
    }

    @Override
    public boolean canBeSteered() {
        return this.getControllingPassenger() instanceof EntityLivingBase;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        damageMultiplier = 0;
        super.fall(distance, damageMultiplier);
    }

    private boolean isRidingPlayer(EntityPlayer player) {
        return this.isBeingRidden() && this.getControllingPassenger() == player;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        EntityPlayer player = getControllingPlayer();
        if (player != null) {
            return false;
        }
        if (isOwner(source.getTrueSource())) {
            putAwaySword((EntityPlayer) source.getTrueSource());
        }
        return false;
    }

    @Override
    public void onUpdate() {
        EntityPlayer player = getControllingPlayer();
        if (player != null && player.isSneaking()) {
            putAwaySword(player);
            return;
        }

        super.onUpdate();

        if (world.isRemote) {
            this.updateClientControls();
        }
    }

    @SideOnly(Side.CLIENT)
    protected void updateClientControls() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.isRidingPlayer(mc.player)) {
            up(mc.gameSettings.keyBindJump.isKeyDown());
            down(ModKeys.sKeyFlySwordDown.isKeyDown());
        }
    }

    private void putAwaySword(EntityPlayer player) {
        setDead();
        if (!world.isRemote) {
            ItemStack sword = getItemStack();
            if (!player.addItemStackToInventory(sword)) {
                entityDropItem(sword, 0);
            }
        }
    }

    private boolean up() {
        return (dataManager.get(CONTROL_STATE) & 1) == 1;
    }

    private boolean down() {
        return (dataManager.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    private void up(boolean up) {
        setStateField(0, up);
    }

    private void down(boolean down) {
        setStateField(1, down);
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = dataManager.get(CONTROL_STATE);
        if (newState) {
            dataManager.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            dataManager.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        EntityPlayer player;
        if (this.isBeingRidden() && (player = getControllingPlayer()) != null) {
            if (down()) {
                this.motionY -= (double) (0.03F);
            }

            if (up()) {
                this.motionY += (double) (0.03F);
            }

            this.rotationYaw = player.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = player.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;
            strafe = player.moveStrafing * 0.5F;
            forward = player.moveForward;

            if (forward <= 0.0F) {
                forward *= 0.25F;
            }

            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

            if (this.canPassengerSteer()) {
                this.setAIMoveSpeed(player.getAIMoveSpeed() * 2F);
                doTravel(strafe, vertical, forward);
            } else {
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;

        } else {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.jumpMovementFactor = 0.02F;
            doTravel(strafe, vertical, forward);
        }
    }

    private void doTravel(float strafe, float vertical, float forward) {
        if (this.isInWater()) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
        } else if (this.isInLava()) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        } else {
            this.moveRelative(strafe, vertical, forward, jumpMovementFactor);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
    }
}
