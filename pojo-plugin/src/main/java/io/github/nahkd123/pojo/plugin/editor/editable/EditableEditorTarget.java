package io.github.nahkd123.pojo.plugin.editor.editable;

import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableInteger;
import io.github.nahkd123.pojo.api.editor.EditableList;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.editor.EditorTarget;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class EditableEditorTarget implements EditorTarget {
	private EditorGUI parent;
	private Editable editable;
	private int auxPage;

	public EditableEditorTarget(EditorGUI parent, Editable editable) {
		this.parent = parent;
		this.editable = editable;
	}

	public EditorGUI getParent() { return parent; }

	public Editable getEditable() { return editable; }

	@Override
	public String getTargetName() { return editable.getDescription().name(); }

	@Override
	public ItemStack getTargetDisplay() {
		NodeDescription desc = editable.getDescription();
		return new StackBuilder(new ItemStack(desc.icon()))
			.name("&e" + desc.name())
			.appendLore(Stream.of(desc.description()).map(s -> "&7" + TextUtils.colorize(s)).toArray(String[]::new))
			.getStack();
	}

	@Override
	public boolean save() {
		return parent.getCurrent().save();
	}

	@Override
	public EditorGUI createGUI(EditorSession session) {
		if (editable instanceof EditableObject obj)
			return new ListLikeEditableEditorGUI(parent, session, this, obj.getFields());
		if (editable instanceof EditableList list) return new EditableListEditorGUI(parent, session, this, list);
		if (editable instanceof EditableString str) return new EditableStringEditorGUI(parent, session, this, str);
		if (editable instanceof EditableInteger intg) return new EditableIntegerEditorGUI(parent, session, this, intg);
		return null;
	}

	public int getAuxPage() { return auxPage; }

	public void setAuxPage(int auxPage) { this.auxPage = auxPage; }
}
