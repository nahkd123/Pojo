package io.github.nahkd123.pojo.plugin.recycle;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class RecycleBin {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private Logger logger;
	private File folder;

	public RecycleBin(Logger logger, File folder) {
		this.logger = logger;
		this.folder = folder;
		if (!folder.exists()) folder.mkdirs();
	}

	public File getFileFor(String source, LocalDateTime time, UserDefinedId id, String ext) {
		File sourceDest = new File(folder, source);
		return new File(sourceDest, FORMATTER.format(time).replaceAll("[^A-Za-z0-9-_]", "_") + "_"
			+ id.namespace() + "_" + id.id()
			+ "." + ext);
	}

	public boolean throwToTrash(String source, LocalDateTime time, UserDefinedId id, Object object) {
		File dest;

		if (object instanceof StandardPojoItem stdItem) {
			dest = getFileFor(source, time, id, "yml");
			if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
			YamlConfiguration config = new YamlConfiguration();
			stdItem.saveToConfig(config);

			try {
				config.save(dest);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		logger.warning("Failed to move " + object + " to recycle bin: Unsupported object type");
		return false;
	}
}
