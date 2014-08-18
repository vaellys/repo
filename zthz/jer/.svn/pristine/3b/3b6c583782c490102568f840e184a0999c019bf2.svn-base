package qileke.jer.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qileke.jer.utils.StringUtils;

public class ActionMapLoader {
	private final Log log = LogFactory.getLog(ActionMapLoader.class);
	private Map<String, Object> actions;

	public synchronized void addActionMappers(List<File> actionMapFiles) {
		loadMapResource(actionMapFiles);
	}

	private synchronized void loadMapResource(List<File> actionMapFiles) {
		Properties properties = new Properties();

		for (File file : actionMapFiles) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				properties.load(inputStream);
			} catch (IOException e) {
				log.error("load file " + file.toString() + " error", e);
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error("close io error", e);
			}
		}
		loadActions(properties);
	}

	// private static void loadInChange(Path path) {
	// WatchService watchService = null;
	// try {
	// watchService = FileSystems.getDefault().newWatchService();
	// path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
	// StandardWatchEventKinds.ENTRY_MODIFY);
	// while (true) {
	// WatchKey watchKey = watchService.take();
	// for (WatchEvent<?> event : watchKey.pollEvents()) {
	// System.out.println("kind " + event.kind() + " context " +
	// event.context());
	// }
	// }
	// } catch (IOException | InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// watchService.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	private void loadActions(Properties properties) {
		actions = new HashMap<String, Object>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			try {
				actions.put(StringUtils.ensureSEWith((String) entry.getKey(), "/"), Class.forName((String) entry.getValue()).newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				log.error(entry.getKey() + " mapped class " + entry.getValue() + " not have no argument constructor!", e);
			} catch (ClassNotFoundException e) {
				log.error(entry.getKey() + " mapped class " + entry.getValue() + " not found!", e);
			}

		}
	}

	public Map<String, Object> getActions() {
		return actions;
	}
	
	

}
