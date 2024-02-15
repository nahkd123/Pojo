package io.github.nahkd123.pojo.api.editor;

import java.util.List;
import java.util.stream.Stream;

public final class EditableObject implements Editable {
	private NodeDescription description;
	private List<Editable> fields;

	public EditableObject(NodeDescription description, List<Editable> fields) {
		this.description = description;
		this.fields = fields;
	}

	@Override
	public NodeDescription getDescription() { return description; }

	public List<Editable> getFields() { return fields; }

	@Override
	public List<String> getPreviewLines() {
		return fields.stream()
			.flatMap(s -> Stream.concat(
				Stream.of("&7" + s.getDescription().name() + ":"),
				s.getPreviewLines().stream().map(ss -> "  " + ss)))
			.toList();
	}
}
