package com.swatt.chainNode.service;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.swatt.util.environment.Environment;
import com.swatt.util.log.LoggingOutputStream;

public class LoggerController {
    public static final String LOGGER_CONSOLE_ENVIRONMENT_VARIABLE_NAME = "logger.console";
    public static final String LOGGER_CONSOLE_PROPERTY = "logger.console";
    public static final String LOGGER_CONSOLE_LEVEL_ENVIRONMENT_VARIABLE_NAME = "logger.console.level";
    public static final String LOGGER_CONSOLE_LEVEL_PROPERTY = "logger.console.level";
    public static final String LOGGER_CONSOLE_PATTERN_ENVIRONMENT_VARIABLE_NAME = "logger.console.pattern";
    public static final String LOGGER_CONSOLE_PATTERN_PROPERTY = "logger.console.pattern";
    public static final String LOGGER_FILE_ENVIRONMENT_VARIABLE_NAME = "logger.file";
    public static final String LOGGER_FILE_PROPERTY = "logger.file";
    public static final String LOGGER_FILE_PATH_ENVIRONMENT_VARIABLE_NAME = "logger.file.path";
    public static final String LOGGER_FILE_PATH_PROPERTY = "logger.file.path";
    public static final String LOGGER_FILE_LEVEL_ENVIRONMENT_VARIABLE_NAME = "logger.file.level";
    public static final String LOGGER_FILE_LEVEL_PROPERTY = "logger.file.level";
    public static final String LOGGER_FILE_PATTERN_ENVIRONMENT_VARIABLE_NAME = "logger.file.pattern";
    public static final String LOGGER_FILE_PATTERN_PROPERTY = "logger.file.pattern";

    private static final DateFormat sdf = new SimpleDateFormat("yyyyMMdd-");

    public static org.slf4j.Logger init(Properties properties) {
        InetAddress server = null;
        try {
            server = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        boolean logToConsole = Boolean.parseBoolean(Environment.getEnvironmentVariableValueOrDefault(LOGGER_CONSOLE_ENVIRONMENT_VARIABLE_NAME, LOGGER_CONSOLE_PROPERTY, properties));
        boolean logToFile = Boolean.parseBoolean(Environment.getEnvironmentVariableValueOrDefault(LOGGER_FILE_ENVIRONMENT_VARIABLE_NAME, LOGGER_FILE_PROPERTY, properties));

        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.getLoggerRepository().resetConfiguration();

        BasicConfigurator.configure();

        if (logToConsole) {
            String consoleLayoutPattern = Environment.getEnvironmentVariableValueOrDefault(LOGGER_CONSOLE_PATTERN_ENVIRONMENT_VARIABLE_NAME, LOGGER_CONSOLE_PATTERN_PROPERTY, properties);
            Level consoleLogLevel = Level.toLevel(Environment.getEnvironmentVariableValueOrDefault(LOGGER_CONSOLE_LEVEL_ENVIRONMENT_VARIABLE_NAME, LOGGER_CONSOLE_LEVEL_PROPERTY, properties));

            ConsoleAppender console = new ConsoleAppender();
            console.setLayout(new PatternLayout(consoleLayoutPattern));
            console.setThreshold(consoleLogLevel);
            console.activateOptions();
            rootLogger.addAppender(console);
        }

        if (logToFile) {
            String filePathRoot = Environment.getEnvironmentVariableValueOrDefault(LOGGER_FILE_PATH_ENVIRONMENT_VARIABLE_NAME, LOGGER_FILE_PATH_PROPERTY, properties);
            String fileLayoutPattern = Environment.getEnvironmentVariableValueOrDefault(LOGGER_FILE_PATTERN_ENVIRONMENT_VARIABLE_NAME, LOGGER_FILE_PATTERN_PROPERTY, properties);
            Level fileLogLevel = Level.toLevel(Environment.getEnvironmentVariableValueOrDefault(LOGGER_FILE_LEVEL_ENVIRONMENT_VARIABLE_NAME, LOGGER_FILE_LEVEL_PROPERTY, properties));

            String logFilePath = filePathRoot + sdf.format(new Date()) + server.getHostName() + ".log";

            FileAppender fa = new FileAppender();
            fa.setName("FileLogger");
            fa.setFile(logFilePath);
            fa.setLayout(new PatternLayout(fileLayoutPattern));
            fa.setThreshold(fileLogLevel);
            fa.setAppend(true);
            fa.activateOptions();
            rootLogger.addAppender(fa);
        }

        System.setErr(new PrintStream(new LoggingOutputStream(rootLogger, Level.ERROR)));

        return null;
    }
}