package com.my51c.see51.Logger;


import android.text.TextUtils;

import org.apache.log4j.Logger;

/**
 * Created by snt1231 on 2017/10/26.
 */

public class LoggerSave {

    /** log开关 */
    private static final boolean SWITCH_LOG = false;
    private static boolean isConfigured = false;

    public static void d(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.debug(message);
        }
    }

    private static void d(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.debug(message, exception);
        }
    }

    private static void i(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.info(message);
        }
    }

    private static void i(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.info(message, exception);
        }
    }

    private static void w(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.warn(message);
        }
    }

    private static void w(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.warn(message, exception);
        }
    }

    private static void e(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.error(message);
        }
    }

    private static void e(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.error(message, exception);
        }
    }

    private static Logger getLogger(String tag) {
        if (!isConfigured) {
           Log4jConfigure.configure();
            isConfigured = true;
        }
        Logger logger;
        if (TextUtils.isEmpty(tag)) {
            logger = Logger.getRootLogger();
        } else {
            logger = Logger.getLogger(tag);
        }
        return logger;
    }

    public static void requestLog(String tag, String request) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.info("request:"+request);
        }
    }
    public static void responseLog(String tag, String response) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.info("response:"+response);
        }
    }
}
