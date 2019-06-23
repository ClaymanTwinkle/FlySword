package com.flysword.entity;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntitySword extends EntityLiving {

    private static final DataParameter<String> SWORD_ITEM_NAME = EntityDataManager.<String>createKey(EntitySword.class, DataSerializers.STRING);

    private ItemStack itemStack;

    public EntitySword(World worldIn) {
        super(worldIn);
        setEntityInvulnerable(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SWORD_ITEM_NAME, "");
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;

        ResourceLocation resourceLocation = itemStack.getItem().getRegistryName();
        if (resourceLocation != null) {
            this.dataManager.set(SWORD_ITEM_NAME, resourceLocation.getResourcePath());
        }
    }

    public ItemStack getItemStack() {
        if (itemStack == null) {
            Item item = Item.REGISTRY.getObject(new ResourceLocation(this.dataManager.get(SWORD_ITEM_NAME)));
            if (item == null) {
                item = Items.WOODEN_SWORD;
            }
            itemStack = new ItemStack(item);
        }
        return itemStack;
    }

    @Override
    public void notifyDataManagerChange(@Nullable DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if (SWORD_ITEM_NAME.equals(key)) {
            Item item = Item.REGISTRY.getObject(new ResourceLocation(this.dataManager.get(SWORD_ITEM_NAME)));
            if (item == null) {
                item = Items.WOODEN_SWORD;
            }
            itemStack = new ItemStack(item);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("RenderItem", 10)) {
            this.setItemStack(new ItemStack(compound.getCompoundTag("RenderItem")));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (!this.getItemStack().isEmpty()) {
            compound.setTag("RenderItem", this.getItemStack().writeToNBT(new NBTTagCompound()));
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

    @Override
    public void updatePassenger(@Nonnull Entity passenger) {
        super.updatePassenger(passenger);

        if (passenger instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) passenger;
            this.renderYawOffset = entityliving.renderYawOffset;
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if(passenger instanceof EntityPlayer) {
            setDead();
        }
    }

    @Override
    public boolean canBeSteered() {
        return this.getControllingPassenger() instanceof EntityLivingBase;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isBeingRidden() && this.getControllingPassenger() instanceof EntityPlayerSP) {
            EntityPlayerSP player = (EntityPlayerSP) getControllingPassenger();
            if (player.movementInput.sneak) {
                //player.movementInput.moveStrafe = (float) ((double) player.movementInput.moveStrafe / 0.3D);
                //player.movementInput.moveForward = (float) ((double) player.movementInput.moveForward / 0.3D);
                this.motionY -= (double) (0.03F);
            }

            if (player.movementInput.jump) {
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
            } else if (player instanceof EntityPlayer) {
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

    public void doTravel(float strafe, float vertical, float forward) {
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
}