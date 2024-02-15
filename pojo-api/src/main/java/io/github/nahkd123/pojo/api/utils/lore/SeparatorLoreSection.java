package io.github.nahkd123.pojo.api.utils.lore;

import java.util.Collections;
import java.util.List;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class SeparatorLoreSection implements LoreSection {
	public static final UserDefinedId SECTION_ID = new UserDefinedId("pojo", "separator");
	private LoreSection before;

	public SeparatorLoreSection(LoreSection before) {
		this.before = before;
	}

	@Override
	public UserDefinedId getSectionId() { return SECTION_ID; }

	@Override
	public List<String> getLines() {
		return before != null && before.getLines().size() > 0
			? Collections.singletonList("")
			: Collections.emptyList();
	}

	@Override
	public String toString() {
		return "<separator>";
	}
}
