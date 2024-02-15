package io.github.nahkd123.pojo.api.utils.lore;

import java.util.ArrayList;
import java.util.List;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class ArrayLoreSection implements LoreSection {
	private UserDefinedId id;
	private List<String> lines = new ArrayList<>();

	public ArrayLoreSection(UserDefinedId id) {
		this.id = id;
	}

	@Override
	public UserDefinedId getSectionId() { return id; }

	@Override
	public List<String> getLines() { return lines; }

	@Override
	public String toString() {
		return lines.toString();
	}
}
