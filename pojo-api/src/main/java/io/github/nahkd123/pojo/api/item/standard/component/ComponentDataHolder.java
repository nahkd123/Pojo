package io.github.nahkd123.pojo.api.item.standard.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Hold component data from all components.
 * </p>
 */
public interface ComponentDataHolder {
	public <C extends Component<T>, T> List<T> getList(Class<C> component);

	public <C extends Component<T>, T> T get(C component);

	public <C extends Component<T>, T> void add(C component, T data);

	public Map<Component<?>, ?> getMap();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default void addRaw(Component component, Object data) {
		add(component, data);
	}

	@SuppressWarnings("rawtypes")
	public static ComponentDataHolder newHolder() {
		Map<Class<?>, List> mapOfLists = new HashMap<>();
		Map<Component<?>, Object> mapOfData = new HashMap<>();

		return new ComponentDataHolder() {
			@SuppressWarnings("unchecked")
			@Override
			public <C extends Component<T>, T> void add(C component, T data) {
				List list = mapOfLists.get(component.getClass());
				if (list == null) mapOfLists.put(component.getClass(), list = new ArrayList());
				list.add(data);
				mapOfData.put(component, mapOfData);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <C extends Component<T>, T> List<T> getList(Class<C> type) {
				return (List<T>) mapOfLists.get(type);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <C extends Component<T>, T> T get(C component) {
				return (T) mapOfData.get(component);
			}

			@Override
			public Map<Component<?>, ?> getMap() { return mapOfData; }
		};
	}
}
