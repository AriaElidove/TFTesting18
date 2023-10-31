package twilightforest.advancements;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.ForgeRegistries;
import twilightforest.TwilightForestMod;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DrinkFromFlaskTrigger extends SimpleCriterionTrigger<DrinkFromFlaskTrigger.Instance> {

	public static final ResourceLocation ID = TwilightForestMod.prefix("drink_from_flask");

	@Override
	protected Instance createInstance(JsonObject json, Optional<ContextAwarePredicate> player, DeserializationContext ctx) {
		Potion potion = null;
		if (json.has("potion")) {
			ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "potion"));
			potion = ForgeRegistries.POTIONS.getValue(resourcelocation);
		}
		if(json.has("doses") && GsonHelper.getAsInt(json, "doses") > 4) throw new JsonSyntaxException("DrinkFromFlaskTrigger: can't have more than 4 doses.");
		MinMaxBounds.Ints doses = MinMaxBounds.Ints.fromJson(json.get("doses"));
		return new Instance(player, doses, potion);
	}

	public void trigger(ServerPlayer player, int doses, Potion potion) {
		this.trigger(player, (instance) -> instance.matches(doses, potion));
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		private final MinMaxBounds.Ints doses;
		@Nullable
		private final Potion potion;

		public Instance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints doses, @Nullable Potion potion) {
			super(player);
			this.doses = doses;
			this.potion = potion;
		}

		public static Instance drankPotion(int doses, Potion potion) {
			return new Instance(Optional.empty(), MinMaxBounds.Ints.exactly(doses), potion);
		}

		public boolean matches(int doses, Potion potion) {
			return this.doses.matches(doses) && this.potion != null && this.potion == potion;
		}

		@Override
		public JsonObject serializeToJson() {
			JsonObject object = super.serializeToJson();
			object.add("doses", this.doses.serializeToJson());
			if (this.potion != null) {
				object.addProperty("potion", ForgeRegistries.POTIONS.getKey(this.potion).toString());
			}
			return object;
		}
	}
}
