package io.github.nahkd123.pojo.api.utils.lore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class LoreSorter {
	// How many sections before a mapping from section ID to section must be
	// constructed.
	private static final int SECTIONS_MAP_THRESHOLD = 16;

	private List<LoreSection> orderedSections = new ArrayList<>();
	private Map<UserDefinedId, LoreSection> sectionsMapping = null;

	public LoreSorter(List<UserDefinedId> defaultOrdering) {
		for (UserDefinedId section : defaultOrdering) {
			if (section.equals(SeparatorLoreSection.SECTION_ID))
				orderedSections.add(new SeparatorLoreSection(orderedSections.size() > 0
					? orderedSections.get(orderedSections.size() - 1)
					: null));
			else orderedSections.add(new ArrayLoreSection(section));
		}
	}

	public LoreSorter() {
		this(Collections.emptyList());
	}

	private void applyMapping() {
		if (sectionsMapping == null && orderedSections.size() > SECTIONS_MAP_THRESHOLD) {
			sectionsMapping = new HashMap<>();
			orderedSections.forEach(s -> {
				if (s instanceof SeparatorLoreSection) return;
				sectionsMapping.put(s.getSectionId(), s);
			});
		}
	}

	public LoreSection getOrNull(UserDefinedId section) {
		if (section.equals(SeparatorLoreSection.SECTION_ID))
			throw new IllegalArgumentException(section + " can't be used as regular lore section");

		applyMapping();
		if (sectionsMapping != null) return sectionsMapping.get(section);
		for (LoreSection current : orderedSections) if (current.getSectionId().equals(section)) return current;
		return null;
	}

	public LoreSection getOrCreate(UserDefinedId section) {
		if (section.equals(SeparatorLoreSection.SECTION_ID))
			throw new IllegalArgumentException(section + " can't be used as regular lore section");

		applyMapping();
		LoreSection s;

		if (sectionsMapping != null) {
			s = sectionsMapping.get(section);

			if (s == null) {
				sectionsMapping.put(section, s = new ArrayLoreSection(section));
				orderedSections.add(s);
			}
		} else {
			s = null;

			for (LoreSection current : orderedSections) {
				if (current.getSectionId().equals(section)) {
					s = current;
					break;
				}
			}

			if (s == null) orderedSections.add(s = new ArrayLoreSection(section));
		}

		return s;
	}

	public List<String> build() {
		List<String> current = null;

		for (LoreSection section : orderedSections) {
			List<String> lines = section.getLines();

			if (lines.size() > 0) {
				if (current == null) current = new ArrayList<>();
				current.addAll(lines);
			}
		}

		if (orderedSections.get(orderedSections.size() - 1) instanceof SeparatorLoreSection
			&& current != null
			&& current.size() > 0
			&& current.get(current.size() - 1).isEmpty()) {
			current.remove(current.size() - 1);
		}

		return current != null ? current : Collections.emptyList();
	}

	@Override
	public String toString() {
		return "LoreSorter" + orderedSections;
	}
}
