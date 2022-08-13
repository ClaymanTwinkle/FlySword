/**
 * Copyright (C) <2017> <coolAlias>
 * <p>
 * This file is part of coolAlias' Dynamic Sword Skills Minecraft Mod; as such,
 * you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.flysword.entity;

import com.flysword.enchantment.ModEnchantments;
import com.flysword.utils.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Sword beam shot from Link's sword when at full health. Inflicts a portion of
 * the original sword's base damage to the first entity struck, less 20% for each
 * additional target thus struck.
 * <p>
 * If using the Master Sword, the beam will shoot through enemies, hitting all
 * entities in its direct path.
 */
public class EntitySwordBeam extends EntityThrowable {
    /**
     * Damage that will be inflicted on impact
     */
    private float damage = 4.0F;

    /**
     * Skill level of user; affects range
     */
    private int level = 1;

    /**
     * Base number of ticks this entity can exist
     */
    private int lifespan = 12;

    /**
     * The amount of knockback an arrow applies when it hits a mob.
     */
    private int knockbackStrength = 1;

    public EntitySwordBeam(World world) {
        super(world);
    }

    public EntitySwordBeam(World world, EntityLivingBase entity) {
        super(world, entity);
    }

    public EntitySwordBeam(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public void entityInit() {
        setSize(0.5F, 0.5F);
    }

    /**
     * Each level increases the distance the beam will travel
     */
    public EntitySwordBeam setLevel(int level) {
        this.level = level;
        this.lifespan += level;
        return this;
    }

    /**
     * Sets amount of damage that will be caused onImpact
     */
    public EntitySwordBeam setDamage(float amount) {
        this.damage = amount;
        return this;
    }

    public float getVelocity() {
        return 1.0F + (level * 0.15F);
    }

    @Override
    public float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 0xf000f0;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (inGround || ticksExisted > lifespan) {
            if (!this.world.isRemote) {
                this.world.setEntityState(this, (byte) 3);
                setDead();
                return;
            }
        }
        for (int i = 0; i < 2; ++i) {
            EnumParticleTypes particle = (i % 2 == 1) ? EnumParticleTypes.CRIT_MAGIC : EnumParticleTypes.CRIT;
            getEntityWorld().spawnParticle(particle, posX, posY, posZ, motionX + rand.nextGaussian(), 0.01D, motionZ + rand.nextGaussian());
            getEntityWorld().spawnParticle(particle, posX, posY, posZ, -motionX + rand.nextGaussian(), 0.01D, -motionZ + rand.nextGaussian());
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            EntityPlayer player = (getThrower() instanceof EntityPlayer ? (EntityPlayer) getThrower() : null);
            if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entityHit = result.entityHit;
                if (entityHit == player || entityHit == null) {
                    return;
                }
                if (player != null) {
                    if (entityHit.attackEntityFrom(new EntityDamageSourceIndirect("indirectSword", this, player).setProjectile(), damage)) {
                        PlayerUtils.playSoundAtEntity(getEntityWorld(), entityHit, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 0.4F, 0.5F);
                        if (entityHit instanceof EntityLivingBase) {
                            if (this.knockbackStrength > 0) {
                                float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (f1 > 0.0F) {
                                    entityHit.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1, 0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1);
                                }
                            }
                        }
                    }
                    damage *= 0.8F;
                }
                if (this.level < ModEnchantments.sSwordBeam.getMaxLevel()) {
                    this.world.setEntityState(this, (byte) 3);
                    setDead();
                }
            } else {
                if (getEntityWorld().getBlockState(result.getBlockPos()).getMaterial().blocksMovement()) {
                    this.world.setEntityState(this, (byte) 3);
                    setDead();
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("damage", damage);
        compound.setInteger("level", level);
        compound.setInteger("lifespan", lifespan);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        damage = compound.getFloat("damage");
        level = compound.getInteger("level");
        lifespan = compound.getInteger("lifespan");
    }
}
