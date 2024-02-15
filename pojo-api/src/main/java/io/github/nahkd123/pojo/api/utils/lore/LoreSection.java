package io.github.nahkd123.pojo.api.utils.lore;

import java.util.List;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public interface LoreSection {
	/**
	 * <p>
	 * Get the ID of this lore section. The ID is fully user-defined.
	 * </p>
	 * 
	 * @return The lore section ID.
	 */
	public UserDefinedId getSectionId();

	/**
	 * <p>
	 * Get all lines in this lore section. The returned list can be modified.
	 * </p>
	 * 
	 * @return A modifiable list of lore lines.
	 */
	public List<String> getLines();
}
