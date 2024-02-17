package io.github.nahkd123.pojo.expansion.stat.gemstone;

import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.nahkd123.pojo.api.internal.PojoInternal;
import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.item.standard.component.Component;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentDataHolder;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.EnumUtils;

public record Gemstone(UserDefinedId id, ComponentDataHolder dataHolder) {

	public static final PersistentDataType<PersistentDataContainer, Gemstone> TYPE = new PersistentDataType<>() {
		@Override
		public PersistentDataContainer toPrimitive(Gemstone complex, PersistentDataAdapterContext context) {
			PersistentDataContainer primitive = context.newPersistentDataContainer();
			primitive.set(PojoInternal.keys().id, PersistentDataType.STRING, complex.id.toString());

			PojoItem item = PojoItem.getFrom(complex.id);
			if (item == null || !(item instanceof StandardPojoItem std)) return primitive;
			std.saveDataTo(primitive, complex.dataHolder);
			return primitive;
		}

		@Override
		public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }

		@Override
		public Class<Gemstone> getComplexType() { return Gemstone.class; }

		@Override
		public Gemstone fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context) {
			String stringId = primitive.get(PojoInternal.keys().id, PersistentDataType.STRING);
			if (stringId == null) return null;

			UserDefinedId id;
			try {
				id = UserDefinedId.fromString(stringId);
			} catch (IllegalArgumentException e) {
				return null;
			}

			PojoItem item = PojoItem.getFrom(id);
			if (item == null || !(item instanceof StandardPojoItem std)) return null;
			ComponentDataHolder dataHolder = std.loadDataFrom(primitive, true);
			return new Gemstone(id, dataHolder);
		}
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getGemstoneName() {
		PojoItem item = PojoItem.getFrom(id);
		if (item == null || !(item instanceof StandardPojoItem std)) return null;

		Material mat = Material.STONE;
		String name = null;

		for (Component component : std.getComponents()) {
			Object data = dataHolder.get(component);
			mat = component.applyMaterial(data, mat, false);
			name = component.applyName(data, name, false);
		}

		return name != null ? name : EnumUtils.toFriendlyName(mat);
	}
}
