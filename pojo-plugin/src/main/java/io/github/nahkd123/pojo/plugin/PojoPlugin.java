package io.github.nahkd123.pojo.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.nahkd123.pojo.api.internal.PojoInternal;
import io.github.nahkd123.pojo.api.internal.PojoKeys;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.command.provided.PojoAdminCommand;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.event.InventoryEventsListener;
import io.github.nahkd123.pojo.plugin.event.PlayerChatEventsListener;
import io.github.nahkd123.pojo.plugin.item.standard.component.DisplayComponent;
import io.github.nahkd123.pojo.plugin.item.standard.component.ItemFlagsComponent;
import io.github.nahkd123.pojo.plugin.recycle.RecycleBin;
import io.github.nahkd123.pojo.plugin.registry.PersistentItemsRegistry;

/**
 * <p>
 * The main entry point for Pojo Bukkit plugin. Typically, you don't need to get
 * the Pojo plugin instance, but if you want to, you can use
 * {@link JavaPlugin#getPlugin(Class)}. Please note that {@link PojoPlugin}
 * changes more frequently than Pojo API.
 * </p>
 * <p>
 * <b>Accessing the API</b>: The API can be accessed by accessing some static
 * methods in various classes under Pojo API, such as
 * {@link ComponentsFactory#register(NamespacedKey)}.
 * </p>
 */
public class PojoPlugin extends JavaPlugin {
	// Global
	private PersistentItemsRegistry items;

	// Plugin
	private Map<UUID, EditorSession> editors = null;
	private RecycleBin recycleBin;

	@Override
	public void onLoad() {
		items = new PersistentItemsRegistry(this, new File(getDataFolder(), "items"));
		new PojoInternal(this, new PojoKeys(this), items);

		// Item components
		DisplayComponent.regisertFactory(new NamespacedKey(this, "display"));
		ItemFlagsComponent.registerFactory(new NamespacedKey(this, "item_flags"));
	}

	@Override
	public void onEnable() {
		// Pre-init
		recycleBin = new RecycleBin(getLogger(), new File(getDataFolder(), "recycle-bin"));

		// Events
		getServer().getPluginManager().registerEvents(new InventoryEventsListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerChatEventsListener(this), this);

		// Commands
		getCommand("pojo").setExecutor(new PojoAdminCommand(this));

		// Post-init
		ConsoleCommandSender console = getServer().getConsoleSender();
		PluginDescriptionFile desc = getDescription();
		console.sendMessage("");
		console.sendMessage(TextUtils.colorize("  &e" + desc.getName() + " &6v" + desc.getVersion() + " &7by &f"
			+ desc.getAuthors().stream().collect(Collectors.joining(", "))));
		console.sendMessage(TextUtils.colorize("  &eTargeting Bukkit for Minecraft " + desc.getAPIVersion()));
		console.sendMessage("");
		console.sendMessage(TextUtils.colorize("  &7Source Code: &f" + desc.getWebsite()));
		console.sendMessage(TextUtils.colorize("  &7Bug Reports: &f" + desc.getWebsite() + "/issues"));
		console.sendMessage(TextUtils.colorize("  &7Wiki:        &f" + desc.getWebsite() + "/wiki"));
		console.sendMessage("");

		items.loadRegistry();
	}

	public YamlConfiguration getOrSaveConfig(String path) {
		File file = new File(getDataFolder(), path);
		if (!file.exists()) saveResource(path, false);
		return YamlConfiguration.loadConfiguration(file);
	}

	public void copyResourceTo(File file, String resource) {
		if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

		try (FileOutputStream stream = new FileOutputStream(file)) {
			getResource(resource).transferTo(stream);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public PersistentItemsRegistry getItems() { return items; }

	public void initializeEditors() {
		if (editors != null) return;
		getLogger().info("Initializing editor features...");
		getLogger()
			.info("Editor features are not initialized by default to reduce memory usage in production environment.");
		editors = new HashMap<>();
	}

	public EditorSession getEditor(UUID uuid) {
		if (editors == null) initializeEditors();
		EditorSession session = editors.get(uuid);
		if (session == null) editors.put(uuid, session = new EditorSession(this, uuid));
		return session;
	}

	public RecycleBin getRecycleBin() { return recycleBin; }
}
