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

public class LoggerController {
    public static final String LOGGER_ROOT_ENVIRONMENT_VARIABLE_NAME = "logger";
    public static final String LOGGER_ROOT_PROPERTY = "logger.root";

    private static final DateFormat sdf = new SimpleDateFormat("yyyyMMdd-");

    public static org.slf4j.Logger init(Properties properties) {
        InetAddress server = null;
        try {
            server = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String fileRoot = getEnvironmentVariableValueOrDefault(LOGGER_ROOT_ENVIRONMENT_VARIABLE_NAME,
                LOGGER_ROOT_PROPERTY, properties);

        System.out.println(properties.toString());

        String logFilePath = fileRoot + sdf.format(new Date()) + server.getHostName() + ".log";

        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.getLoggerRepository().resetConfiguration();

        BasicConfigurator.configure();

        ConsoleAppender console = new ConsoleAppender(); // create appender
        console.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        console.setThreshold(Level.FATAL);
        console.activateOptions();
        rootLogger.addAppender(console);

        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile(logFilePath);
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();
        rootLogger.addAppender(fa);

        System.setErr(new PrintStream(new LoggingOutputStream(rootLogger, Level.ERROR)));

        return null;
    }

    private static String getEnvironmentVariableValueOrDefault(String environmentVariableName, String propertyName,
            Properties properties) {
        String value = System.getenv().get(environmentVariableName);
        if (value == null) {
            value = properties.getProperty(propertyName);
        }
        return value;
    }
}