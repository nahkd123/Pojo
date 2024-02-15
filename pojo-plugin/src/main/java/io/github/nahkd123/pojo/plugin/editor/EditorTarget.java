package io.github.nahkd123.pojo.plugin.editor;

import org.bukkit.inventory.ItemStack;

public interface EditorTarget {
	/**
	 * <p>
	 * Get the name of this editor target. This will be displayed in menu title.
	 * </p>
	 * 
	 * @return The target name.
	 */
	public String getTargetName();

	/**
	 * <p>
	 * Get the display icon in the editor. This will be used as editor tabs.
	 * </p>
	 * 
	 * @return The display icon.
	 */
	public ItemStack getTargetDisplay();

	/**
	 * <p>
	 * Attempt to save this editor target. Sometimes it is not possible to save due
	 * to failed file IO operation, or the target have at least 1 component that
	 * can't be saved.
	 * </p>
	 * 
	 * @return Save result.
	 */
	public boolean save();

	public EditorGUI createGUI(EditorSession session);
}
