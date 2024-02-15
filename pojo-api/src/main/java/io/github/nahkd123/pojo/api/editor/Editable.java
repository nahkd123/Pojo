package io.github.nahkd123.pojo.api.editor;

import java.util.List;

/**
 * <p>
 * Represent an editable node.
 * </p>
 * 
 * @param <T> The value type.
 */
public sealed interface Editable permits EditableString, EditableList, EditableObject, EditableInteger, EditableBool {
	public NodeDescription getDescription();

	public List<String> getPreviewLines();
}
