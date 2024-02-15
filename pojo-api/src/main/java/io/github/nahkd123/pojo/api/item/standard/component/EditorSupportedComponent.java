package io.github.nahkd123.pojo.api.item.standard.component;

import java.util.List;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.NodeDescription;

/**
 * <p>
 * A special interface to support editing the component configurations right
 * inside the game (or server).
 * </p>
 * 
 * @param <T> The type of component data.
 */
public interface EditorSupportedComponent<T> extends SaveableComponent<T> {
	public NodeDescription getEditorDescription();

	/**
	 * <p>
	 * Get a list of editable nodes that can be modified by in-game editor.
	 * </p>
	 * 
	 * @return A list of editable nodes.
	 */
	public List<Editable> getEditorNodes();
}
