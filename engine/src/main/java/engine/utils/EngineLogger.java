package engine.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class EngineLogger {
	private static final Logger logger = Logger.getLogger("JTileEngine");
	private static FileHandler logFileHandler;
	private static ConsoleHandler consoleHandler;

	private EngineLogger() {
	}

	private static class LogFormatter extends Formatter {
		@Override
		public String format(LogRecord record) {
			String timestamp = new SimpleDateFormat("yyyy dd.MM. [HH:mm:ss]").format(new Date(record.getMillis()));
			String sourceClass = record.getSourceClassName();
			String sourceMethod = record.getSourceMethodName();
			String logLevel = record.getLevel().toString();
			String message = formatMessage(record);
			return String.format("%s - JTileEngine (%s.%s):\n\t[%s] %s%n", timestamp, sourceClass, sourceMethod, logLevel, message);
		}
	}

	public static void setup() {
		try {
			logger.setUseParentHandlers(false);
			logFileHandler = new FileHandler(ResourceManager.getInstance().getUserSavePath().resolve("engine.log").toString(), true);
			logFileHandler.setFormatter(new LogFormatter());
			logger.addHandler(logFileHandler);
			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new LogFormatter());
			logger.addHandler(consoleHandler);
			logger.setLevel(Level.ALL);
		} catch (IOException e) {
			System.err.println("Failed to setup logger: " + e.getMessage());
		}
	}

	public static Level getLogLevel() {
		return logger.getLevel();
	}

	public static void setLogLevel(Level level) {
		logger.setLevel(level);
		info("Log level set to: " + level);
	}

	public static void info(String message) {
		logger.info(message);
	}

	public static void warning(String message) {
		logger.warning(message);
	}

	public static void severe(String message) {
		logger.severe(message);
	}

	public static void close() {
		if (logFileHandler != null) logFileHandler.close();
		if (consoleHandler != null) consoleHandler.close();
	}
}
