package io.github.nahkd123.pojo.expansion.item.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableList;
import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentDataHolder;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.lore.LoreSection;
import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;
import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.stat.compute.ComputedStat;
import io.github.nahkd123.pojo.expansion.stat.compute.ComputedStats;
import io.github.nahkd123.pojo.expansion.stat.gemstone.Gemstone;
import io.github.nahkd123.pojo.expansion.stat.gemstone.GemstonesHolder;
import io.github.nahkd123.pojo.expansion.stat.localize.GemstoneLocalizer;
import io.github.nahkd123.pojo.expansion.stat.localize.GemstonesLocalizer;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public class GemstoneSlotsComponent implements EditorSupportedComponent<GemstonesHolder> {
	private static final NodeDescription DESCRIPTION = new NodeDescription(Material.EMERALD, "Pojo Expansion: Gemstone Slots", new String[] {
		"Add gemstone slots to this item.",
		"Gemstones can be applied if the gemstone item",
		"have gemstone component. It can be applied by",
		"clicking an item with suitable empty slots while",
		"having gemstone item on cursor."
	});
	private NamespacedKey typeId;
	private List<UserDefinedId> layout;
	private Supplier<GemstonesLocalizer> localizer;

	public GemstoneSlotsComponent(NamespacedKey typeId, List<UserDefinedId> layout, Supplier<GemstonesLocalizer> localizer) {
		this.typeId = typeId;
		this.layout = layout;
		this.localizer = localizer;
	}

	public List<UserDefinedId> getLayout() { return layout; }

	public static void registerFactory(NamespacedKey typeId, Supplier<GemstonesLocalizer> localizer) {
		ComponentsFactory<GemstonesHolder> factory = new EditorComponentsFactory<>() {
			@Override
			public GemstoneSlotsComponent createDefault() {
				return new GemstoneSlotsComponent(typeId, new ArrayList<>(), localizer);
			}

			@Override
			public GemstoneSlotsComponent createFromConfig(ConfigurationSection config) {
				List<UserDefinedId> layout = new ArrayList<>();
				config.getStringList("gemstoneSlots").stream()
					.map(UserDefinedId::fromString)
					.forEach(layout::add);
				return new GemstoneSlotsComponent(typeId, layout, localizer);
			}

			@Override
			public NodeDescription getEditorDescription() { return DESCRIPTION; }
		};
		factory.register(typeId);
	}

	@Override
	public void saveComponentTo(ConfigurationSection config) {
		config.set("gemstoneSlots", layout.stream().map(Object::toString).toList());
	}

	@Override
	public NamespacedKey getTypeId() { return typeId; }

	@Override
	public GemstonesHolder createNewData() {
		return new GemstonesHolder(layout);
	}

	@Override
	public void storeDataTo(PersistentDataContainer container, GemstonesHolder data) {
		List<PersistentDataContainer> slots = data.toContainers(container.getAdapterContext());
		container.set(typeId, PersistentDataType.LIST.dataContainers(), slots);
	}

	@Override
	public GemstonesHolder loadDataFrom(PersistentDataContainer container) {
		return new GemstonesHolder(layout).load(container.get(typeId, PersistentDataType.LIST.dataContainers()));
	}

	@Override
	public void applyLore(GemstonesHolder data, LoreSorter lore, boolean displayMode) {
		GemstonesLocalizer localizer = this.localizer.get();
		LoreSection section = lore.getOrCreate(new UserDefinedId(typeId));

		for (int i = 0; i < layout.size(); i++) {
			UserDefinedId slotId = layout.get(i);
			Gemstone gemstone = data.getGemstones().get(i);
			GemstoneLocalizer slotLocalizer = localizer.getLocalizer(slotId);

			String slotName = slotLocalizer.getName() != null ? slotLocalizer.getName() : slotId.toString();
			String text = gemstone != null
				? slotLocalizer.localizeFilledSlot(slotName, gemstone.getGemstoneName())
				: slotLocalizer.localizeEmptySlot(slotName);

			section.getLines().add(text);
		}
	}

	@Override
	public void applyToOtherComponent(GemstonesHolder data, ComponentDataHolder others) {
		List<ComputedStats> allComputedStats = others.getList(StatsComponent.class);
		GemstonesLocalizer localizer = this.localizer.get();

		for (int i = 0; i < layout.size(); i++) {
			Gemstone gemstone = data.getGemstones().get(i);
			if (gemstone == null) continue;

			UserDefinedId slotId = layout.get(i);
			GemstoneLocalizer slotLocalizer = localizer.getLocalizer(slotId);
			ComponentDataHolder gemstoneDataHolder = gemstone.dataHolder();
			List<ComputedStats> modifiersList = gemstoneDataHolder.getList(GemstoneComponent.class);
			ComputedStats modifiers = modifiersList.get(0);

			for (ComputedStats computedStats : allComputedStats) {
				if (modifiers.getEquipmentSlot() != PojoEquipmentSlot.ALL &&
					modifiers.getEquipmentSlot() != computedStats.getEquipmentSlot()) continue;

				for (Stat modifier : modifiers.getStats()) {
					ComputedStat computedModifier = modifiers.get(modifier);
					String text = slotLocalizer.localize(computedModifier);
					computedStats.get(modifier).modify(modifier.getOperation(), computedModifier.getValue(), text);
				}
			}
		}
	}

	@Override
	public NodeDescription getEditorDescription() { return DESCRIPTION; }

	@Override
	public List<Editable> getEditorNodes() {
		// @formatter:off
		return Arrays.asList(
			new EditableList(
				new NodeDescription(Material.EMERALD, "Slots", "Add or remove slots"),
				() -> layout.size(),
				index -> new EditableString(
					new NodeDescription(Material.EMERALD, "Slot #" + (index + 1), "Assign a gemstone slot ID"),
					() -> layout.get(index).toString(),
					s -> layout.set(index, s != null? UserDefinedId.fromString(s) : layout.get(index))),
				index -> layout.add(index, new UserDefinedId("mynamespace", "sample_slot")),
				index -> layout.remove(index),
				(from, to) -> {
					UserDefinedId id = layout.remove((int) from);
					layout.add(to, id);
				}));
		// @formatter:on
	}
}
