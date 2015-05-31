package com.example.logbacktest;

import android.os.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Created by ymka on 5/31/15.
 */
public class LogbackConfig {

    private static final String sFolderName = "test_log";
    private static final String sFileName = "log_";
    private static final String sFileExtension = ".txt";
    private static final String sCurrentFileName = "log.txt";
    private static int sMaxHistoryDays = 7;


    public static void init() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        final String logDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + sFolderName;

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(context);

        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.
        rollingFileAppender.setFile(logDir + File.separator + sCurrentFileName);

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setFileNamePattern(logDir + File.separator + sFileName + "%d" + sFileExtension);
        rollingPolicy.setMaxHistory(sMaxHistoryDays);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder filePatternEncoder = new PatternLayoutEncoder();
        filePatternEncoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n");
        filePatternEncoder.setContext(context);
        filePatternEncoder.start();

        rollingFileAppender.setEncoder(filePatternEncoder);
        rollingFileAppender.start();


        // setup LogcatAppender
        PatternLayoutEncoder logcatPatternEncoder = new PatternLayoutEncoder();
        logcatPatternEncoder.setContext(context);
        logcatPatternEncoder.setPattern("[%thread] %msg%n");
        logcatPatternEncoder.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(context);
        logcatAppender.setEncoder(logcatPatternEncoder);
        logcatAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.TRACE);
        root.addAppender(rollingFileAppender);
        root.addAppender(logcatAppender);

        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(context);
    }


}
