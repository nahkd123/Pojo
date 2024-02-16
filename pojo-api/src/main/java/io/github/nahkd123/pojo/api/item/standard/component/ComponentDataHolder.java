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
	public <C extends Component<T>, T> List<C> get(Class<C> component);

	public <C extends Component<T>, T> void add(Class<C> type, T data);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default void addRaw(Class type, Object data) {
		add(type, data);
	}

	@SuppressWarnings("rawtypes")
	public static ComponentDataHolder newHolder() {
		Map<Class<?>, List> map = new HashMap<>();

		return new ComponentDataHolder() {
			@SuppressWarnings("unchecked")
			@Override
			public <C extends Component<T>, T> void add(Class<C> type, T data) {
				List list = map.get(type);
				if (list == null) map.put(type, list = new ArrayList());
				list.add(data);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <C extends Component<T>, T> List<C> get(Class<C> type) {
				return (List<C>) map.get(type);
			}
		};
	}
}
