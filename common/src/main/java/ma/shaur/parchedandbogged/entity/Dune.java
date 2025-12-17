package ma.shaur.parchedandbogged.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import ma.shaur.parchedandbogged.worldgen.BiomeData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class Dune extends Monster
{
	private static final EntityDataAccessor<Boolean> DATA_IS_RED = SynchedEntityData.defineId(Dune.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(Dune.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(Dune.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(Dune.class, EntityDataSerializers.BOOLEAN);
	private static final boolean DEFAULT_RED = false;
	private static final boolean DEFAULT_IGNITED = false;
	private static final boolean DEFAULT_POWERED = false;
	private static final short DEFAULT_MAX_SWELL = 30;
	private static final byte DEFAULT_EXPLOSION_RADIUS = 2;
	private int swell = 0, oldSwell = 0, maxSwell = DEFAULT_MAX_SWELL, explosionRadius = DEFAULT_EXPLOSION_RADIUS;
	
	public Dune(EntityType<? extends Dune> entityType, Level level)
	{
		super(entityType, level);
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, SpawnGroupData spawnGroupData)
	{
		if (sholdSpawnRed(serverLevelAccessor.getBiome(blockPosition()))) entityData.set(DATA_IS_RED, true);
		
		return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);
	}

	private static boolean sholdSpawnRed(Holder<Biome> biome)
	{
		return biome.is(BiomeTags.IS_BADLANDS) || biome.is(BiomeData.TAG_C_IS_BADLANDS);
	}

	protected void registerGoals()
	{
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new Dune.SwellGoal(this));
		goalSelector.addGoal(3, new AvoidEntityGoal<Ocelot>(this, Ocelot.class, 6.0F, 1.0, 1.2));
		goalSelector.addGoal(3, new AvoidEntityGoal<Cat>(this, Cat.class, 6.0F, 1.0, 1.2));
		goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
		targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		super.defineSynchedData(builder);
		builder.define(DATA_SWELL_DIR, -1);
		builder.define(DATA_IS_POWERED, DEFAULT_POWERED);
		builder.define(DATA_IS_IGNITED, DEFAULT_IGNITED);
		builder.define(DATA_IS_RED, DEFAULT_RED);
	}

	public static boolean checkDuneSpawnRules(EntityType<Dune> entityType, ServerLevelAccessor serverLevelAccessor, EntitySpawnReason entitySpawnReason, BlockPos blockPos, RandomSource randomSource)
	{
		return checkMonsterSpawnRules(entityType, serverLevelAccessor, entitySpawnReason, blockPos, randomSource);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
	}

	protected void addAdditionalSaveData(ValueOutput valueOutput)
	{
		super.addAdditionalSaveData(valueOutput);
		valueOutput.putBoolean("red", isRed());
		valueOutput.putBoolean("powered", isPowered());
		valueOutput.putShort("Fuse", (short) maxSwell);
		valueOutput.putByte("ExplosionRadius", (byte) explosionRadius);
		valueOutput.putBoolean("ignited", isIgnited());
	}

	protected void readAdditionalSaveData(ValueInput valueInput)
	{
		super.readAdditionalSaveData(valueInput);
		entityData.set(DATA_IS_RED, valueInput.getBooleanOr("red", DEFAULT_RED));
		entityData.set(DATA_IS_POWERED, valueInput.getBooleanOr("powered", DEFAULT_POWERED));
		maxSwell = valueInput.getShortOr("Fuse", DEFAULT_MAX_SWELL);
		explosionRadius = valueInput.getByteOr("ExplosionRadius", DEFAULT_EXPLOSION_RADIUS);
		if (valueInput.getBooleanOr("ignited", false)) ignite();
	}

	protected InteractionResult mobInteract(Player player, InteractionHand interactionHand)
	{
		ItemStack itemStack = player.getItemInHand(interactionHand);
		if (itemStack.is(ItemTags.CREEPER_IGNITERS))
		{
			SoundEvent soundEvent = itemStack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
			level().playSound(player, getX(), getY(), getZ(), soundEvent, getSoundSource(), 1.0F, random.nextFloat() * 0.4F + 0.8F);
			if (!level().isClientSide())
			{
				ignite();
				if (!itemStack.isDamageableItem())
				{
					itemStack.shrink(1);
				}
				else
				{
					itemStack.hurtAndBreak(1, player, interactionHand.asEquipmentSlot());
				}
			}

			return InteractionResult.SUCCESS;
		} 
		else
		{
			return super.mobInteract(player, interactionHand);
		}
	}

	protected SoundEvent getHurtSound(DamageSource damageSource)
	{
		return SoundEvents.CREEPER_HURT;
	}

	protected SoundEvent getDeathSound()
	{
		return SoundEvents.CREEPER_DEATH;
	}
	
	@Override
	public void tick()
	{
		if (isAlive())
		{
			oldSwell = swell;
			if (isIgnited())
			{
				setSwellDir(1);
			}

			int i = getSwellDir();
			if (i > 0 && swell == 0)
			{
				playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
				gameEvent(GameEvent.PRIME_FUSE);
			}

			swell += i;
			if (swell < 0)
			{
				swell = 0;
			}

			if (swell >= maxSwell)
			{
				swell = maxSwell;
				explodeDune();
			}
		}

		super.tick();
	}

	public void explodeDune()
	{
		Level level = level();
		if (level instanceof ServerLevel serverLevel)
		{
			dead = true;
			triggerOnDeathMobEffects(serverLevel, RemovalReason.KILLED);
			
			boolean powered = isPowered();
			Vec3 center = position();
			BlockState sandState = isRed() ? Blocks.RED_SAND.defaultBlockState() : Blocks.SAND.defaultBlockState();
			List<BlockPos> affected = new ArrayList<>();
			playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.5F);
			gameEvent(GameEvent.EXPLODE);
			
			for (int x = 0; x < 16; ++x)
			{
				for (int y = 0; y < 16; ++y)
				{
					for (int z = 0; z < 16; ++z)
					{
						if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15)
						{
							double dX = x / 15.0F * 2.0F - 1.0F;
							double dY = y / 15.0F * 2.0F - 1.0F;
							double dZ = z / 15.0F * 2.0F - 1.0F;
							double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
							dX /= dist;
							dY /= dist;
							dZ /= dist;
							float h = explosionRadius * (0.7F + serverLevel.random.nextFloat() * 0.6F);
							double blockX = center.x;
							double blockY = center.y;
							double blockZ = center.z;

							for (; h > 0.0F; h -= 0.22500001F)
							{
								BlockPos blockPos = BlockPos.containing(blockX, blockY, blockZ);
								if(!affected.contains(blockPos))
								{
									BlockState blockState = serverLevel.getBlockState(blockPos);
									FluidState fluidState = serverLevel.getFluidState(blockPos);
									if (!serverLevel.isInWorldBounds(blockPos))
									{
										break;
									}

									if (h > 0.0F)
									{
										if(shouldReplaceWithSand(fluidState, blockState)) serverLevel.setBlock(blockPos, sandState, 3, 32);
										else tryMakeFalling(serverLevel, blockPos, fluidState, blockState, powered);
									}
									affected.add(blockPos);
								}

								blockX += dX * 0.30000001192092896;
								blockY += dY * 0.30000001192092896;
								blockZ += dZ * 0.30000001192092896;
							}
						}
					}
				}
			}
			discard();
		}
	}

	private void tryMakeFalling(ServerLevel serverLevel, BlockPos blockPos, FluidState fluidState, BlockState blockState, boolean powered)
	{
		if(!fluidState.isEmpty() || blockState.isAir()) return;
		
		PushReaction reaction = blockState.getPistonPushReaction();
		
		if(reaction == PushReaction.DESTROY) serverLevel.destroyBlock(blockPos, true, this);
		
		if(powered && reaction != PushReaction.BLOCK || blockState.getBlock() instanceof ColoredFallingBlock)
		{
			FallingBlockEntity.fall(serverLevel, blockPos, blockState);
		}
	}

	private boolean shouldReplaceWithSand(FluidState fluidState, BlockState blockState)
	{
		return !fluidState.isEmpty() || blockState.isAir();
	}

	public int getSwellDir()
	{
		return entityData.get(DATA_SWELL_DIR);
	}

	public void setSwellDir(int swellDir)
	{
		entityData.set(DATA_SWELL_DIR, swellDir);
	}

	public boolean isIgnited()
	{
		return entityData.get(DATA_IS_IGNITED);
	}
	
	public void ignite()
	{
		entityData.set(DATA_IS_IGNITED, true);
	}

	public boolean isPowered()
	{
		return entityData.get(DATA_IS_POWERED);
	}

	public boolean isRed()
	{
		return entityData.get(DATA_IS_RED);
	}

	public float getSwelling(float f)
	{
		return Mth.lerp(f, oldSwell, swell) / (float) (maxSwell - 2);
	}

	@Override
	public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damage)
	{
		if(damageSource.typeHolder().is(DamageTypes.WIND_CHARGE))
		{
			entityData.set(DATA_IS_POWERED, true);
			
			return true;
		}
		
		return super.hurtServer(serverLevel, damageSource, damage);
	}
	
	@Override
	public boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource)
	{
		return !damageSource.typeHolder().is(DamageTypes.WIND_CHARGE) && super.isInvulnerableTo(serverLevel, damageSource);
	}
	
	public class SwellGoal extends Goal 
	{
		private final Dune dune;

		public SwellGoal(Dune dune)
		{
			this.dune = dune;
			setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canUse()
		{
			LivingEntity target = dune.getTarget();
			return dune.getSwellDir() > 0 || target != null && dune.distanceToSqr(target) < 9.0;
		}

		@Override
		public void start()
		{
			dune.getNavigation().stop();
		}

		@Override
		public boolean requiresUpdateEveryTick()
		{
			return true;
		}

		@Override
		public void tick()
		{
			LivingEntity target = dune.getTarget();
			if (target == null)
			{
				dune.setSwellDir(-1);
			} 
			else if (dune.distanceToSqr(target) > 49.0)
			{
				dune.setSwellDir(-1);
			} 
			else if (!dune.getSensing().hasLineOfSight(target))
			{
				dune.setSwellDir(-1);
			} 
			else
			{
				dune.setSwellDir(1);
			}
		}
	}
}
