package io.github.nahkd123.pojo.api.item.standard.component;

/**
 * <p>
 * A defaulted components factory contains a {@link #createDefault()} that
 * creates a default component. This is mainly used for creating new component
 * inside in-game editor. However, the component itself must implements
 * {@link EditorSupportedComponent} to use full editor capabilities.
 * </p>
 * 
 * @param <T> The type for component data.
 */
public interface DefaultedComponentsFactory<T> extends ComponentsFactory<T> {
	public Component<T> createDefault();
}
