package io.github.nahkd123.pojo.plugin.editor;

@FunctionalInterface
public interface ChatInput {
	/**
	 * <p>
	 * Called by events listener when player entered something in chat while their
	 * editor session is current active and this {@link ChatInput} is being held by
	 * that editor session.
	 * </p>
	 * <p>
	 * This method will be called right in the next server tick. Please note that
	 * this method will be called until you remove {@link ChatInput} from
	 * {@link EditorSession}.
	 * </p>
	 * 
	 * @param session The editor session that holding this {@link ChatInput}.
	 * @param input   Player's message.
	 */
	public void onInput(EditorSession session, String input);
}
