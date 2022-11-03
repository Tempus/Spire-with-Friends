package chronoMods.utilities;

import chronoMods.TogetherManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

import static chronoMods.utilities.AntiConsolePrintingPatches.logsSkipped;

public class AntiLogger implements Logger {
    private Logger backup;

    public void setBackup(Logger log) {
        backup = log;
    }

    @Override
    public void catching(Level level, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.catching(level, throwable);
        }
    }

    @Override
    public void catching(Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, Message message) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.debug(marker, message);
        }
    }

    @Override
    public void debug(Marker marker, Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, Object o) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.debug(marker, o);
        }
    }

    @Override
    public void debug(Marker marker, Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Message message) {
        logsSkipped++;
    }

    @Override
    public void debug(Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void debug(MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void debug(CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Object o) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.debug(o);
        }
    }

    @Override
    public void debug(Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(String s) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.debug(s);
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void debug(Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.debug(s, o);
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++;
    }

    @Override
    public void entry() {
        logsSkipped++;
    }

    @Override
    public void entry(Object... objects) {
        logsSkipped++;
    }

    @Override
    public void error(Marker marker, Message message) {
        backup.error(marker, message);
    }

    @Override
    public void error(Marker marker, Message message, Throwable throwable) {
        backup.error(marker, message, throwable);
    }

    @Override
    public void error(Marker marker, MessageSupplier messageSupplier) {
        backup.error(marker, messageSupplier);
    }

    @Override
    public void error(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        backup.error(marker, messageSupplier, throwable);
    }

    @Override
    public void error(Marker marker, CharSequence charSequence) {
        backup.error(marker, charSequence);
    }

    @Override
    public void error(Marker marker, CharSequence charSequence, Throwable throwable) {
        backup.error(marker, charSequence, throwable);
    }

    @Override
    public void error(Marker marker, Object o) {
        backup.error(marker, o);
    }

    @Override
    public void error(Marker marker, Object o, Throwable throwable) {
        backup.error(marker, o, throwable);
    }

    @Override
    public void error(Marker marker, String s) {
        backup.error(marker, s);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        backup.error(marker, s, objects);
    }

    @Override
    public void error(Marker marker, String s, Supplier<?>... suppliers) {
        backup.error(marker, s, suppliers);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        backup.error(marker, s, throwable);
    }

    @Override
    public void error(Marker marker, Supplier<?> supplier) {
        backup.error(marker, supplier);
    }

    @Override
    public void error(Marker marker, Supplier<?> supplier, Throwable throwable) {
        backup.error(marker, supplier, throwable);
    }

    @Override
    public void error(Message message) {
        backup.error(message);
    }

    @Override
    public void error(Message message, Throwable throwable) {
        backup.error(message, throwable);
    }

    @Override
    public void error(MessageSupplier messageSupplier) {
        backup.error(messageSupplier);
    }

    @Override
    public void error(MessageSupplier messageSupplier, Throwable throwable) {
        backup.error(messageSupplier, throwable);
    }

    @Override
    public void error(CharSequence charSequence) {
        backup.error(charSequence);
    }

    @Override
    public void error(CharSequence charSequence, Throwable throwable) {
        backup.error(charSequence, throwable);
    }

    @Override
    public void error(Object o) {
        backup.error(o);
    }

    @Override
    public void error(Object o, Throwable throwable) {
        backup.error(o, throwable);
    }

    @Override
    public void error(String s) {
        backup.error(s);
    }

    @Override
    public void error(String s, Object... objects) {
        backup.error(s, objects);
    }

    @Override
    public void error(String s, Supplier<?>... suppliers) {
        backup.error(s, suppliers);
    }

    @Override
    public void error(String s, Throwable throwable) {
        backup.error(s, throwable);
    }

    @Override
    public void error(Supplier<?> supplier) {
        backup.error(supplier);
    }

    @Override
    public void error(Supplier<?> supplier, Throwable throwable) {
        backup.error(supplier, throwable);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        backup.error(marker, s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        backup.error(marker, s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2) {
        backup.error(marker, s, o, o1, o2);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        backup.error(marker, s, o, o1, o2, o3);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        backup.error(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        backup.error(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        backup.error(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        backup.error(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        backup.error(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        backup.error(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void error(String s, Object o) {
        backup.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        backup.error(s, o, o1);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2) {
        backup.error(s, o, o1, o2);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3) {
        backup.error(s, o, o1, o2, o3);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        backup.error(s, o, o1, o2, o3, o4);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        backup.error(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        backup.error(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        backup.error(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        backup.error(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        backup.error(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void exit() {
    }

    @Override
    public <R> R exit(R r) {
        return null;
    }

    @Override
    public void fatal(Marker marker, Message message) {
        backup.fatal(marker, message);
    }

    @Override
    public void fatal(Marker marker, Message message, Throwable throwable) {
        backup.fatal(marker, message, throwable);
    }

    @Override
    public void fatal(Marker marker, MessageSupplier messageSupplier) {
        backup.fatal(marker, messageSupplier);
    }

    @Override
    public void fatal(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        backup.fatal(marker, messageSupplier, throwable);
    }

    @Override
    public void fatal(Marker marker, CharSequence charSequence) {
        backup.fatal(marker, charSequence);
    }

    @Override
    public void fatal(Marker marker, CharSequence charSequence, Throwable throwable) {
        backup.fatal(marker, charSequence, throwable);
    }

    @Override
    public void fatal(Marker marker, Object o) {
        backup.fatal(marker, o);
    }

    @Override
    public void fatal(Marker marker, Object o, Throwable throwable) {
        backup.fatal(marker, o, throwable);
    }

    @Override
    public void fatal(Marker marker, String s) {
        backup.fatal(marker, s);
    }

    @Override
    public void fatal(Marker marker, String s, Object... objects) {
        backup.fatal(marker, s, objects);
    }

    @Override
    public void fatal(Marker marker, String s, Supplier<?>... suppliers) {
        backup.fatal(marker, s, suppliers);
    }

    @Override
    public void fatal(Marker marker, String s, Throwable throwable) {
        backup.fatal(marker, s, throwable);
    }

    @Override
    public void fatal(Marker marker, Supplier<?> supplier) {
        backup.fatal(marker, supplier);
    }

    @Override
    public void fatal(Marker marker, Supplier<?> supplier, Throwable throwable) {
        backup.fatal(marker, supplier, throwable);
    }

    @Override
    public void fatal(Message message) {
        backup.fatal(message);
    }

    @Override
    public void fatal(Message message, Throwable throwable) {
        backup.fatal(message, throwable);
    }

    @Override
    public void fatal(MessageSupplier messageSupplier) {
        backup.fatal(messageSupplier);
    }

    @Override
    public void fatal(MessageSupplier messageSupplier, Throwable throwable) {
        backup.fatal(messageSupplier, throwable);
    }

    @Override
    public void fatal(CharSequence charSequence) {
        backup.fatal(charSequence);
    }

    @Override
    public void fatal(CharSequence charSequence, Throwable throwable) {
        backup.fatal(charSequence, throwable);
    }

    @Override
    public void fatal(Object o) {
        backup.fatal(o);
    }

    @Override
    public void fatal(Object o, Throwable throwable) {
        backup.fatal(o, throwable);
    }

    @Override
    public void fatal(String s) {
        backup.fatal(s);
    }

    @Override
    public void fatal(String s, Object... objects) {
        backup.fatal(s, objects);
    }

    @Override
    public void fatal(String s, Supplier<?>... suppliers) {
        backup.fatal(s, suppliers);
    }

    @Override
    public void fatal(String s, Throwable throwable) {
        backup.fatal(s, throwable);
    }

    @Override
    public void fatal(Supplier<?> supplier) {
        backup.fatal(supplier);
    }

    @Override
    public void fatal(Supplier<?> supplier, Throwable throwable) {
        backup.fatal(supplier, throwable);
    }

    @Override
    public void fatal(Marker marker, String s, Object o) {
        backup.fatal(marker, s, o);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1) {
        backup.fatal(marker, s, o, o1);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2) {
        backup.fatal(marker, s, o, o1, o2);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        backup.fatal(marker, s, o, o1, o2, o3);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        backup.fatal(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        backup.fatal(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        backup.fatal(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        backup.fatal(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        backup.fatal(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        backup.fatal(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void fatal(String s, Object o) {
        backup.fatal(s, o);
    }

    @Override
    public void fatal(String s, Object o, Object o1) {
        backup.fatal(s, o, o1);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2) {
        backup.fatal(s, o, o1, o2);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3) {
        backup.fatal(s, o, o1, o2, o3);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        backup.fatal(s, o, o1, o2, o3, o4);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        backup.fatal(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        backup.fatal(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        backup.fatal(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        backup.fatal(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        backup.fatal(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public <MF extends MessageFactory> MF getMessageFactory() {
        return null;
    }

    @Override
    public String getName() {
            return backup.getName();
    }

    @Override
    public void info(Marker marker, Message message) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, message);
        }
    }

    @Override
    public void info(Marker marker, Message message, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, message);
        }
    }

    @Override
    public void info(Marker marker, MessageSupplier messageSupplier) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, messageSupplier);
        }
    }

    @Override
    public void info(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, messageSupplier, throwable);
        }
    }

    @Override
    public void info(Marker marker, CharSequence charSequence) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, charSequence);
        }
    }

    @Override
    public void info(Marker marker, CharSequence charSequence, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, charSequence, throwable);
        }
    }

    @Override
    public void info(Marker marker, Object o) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, o);
        }
    }

    @Override
    public void info(Marker marker, Object o, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, o, throwable);
        }
    }

    @Override
    public void info(Marker marker, String s) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s);
        }
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, objects);
        }
    }

    @Override
    public void info(Marker marker, String s, Supplier<?>... suppliers) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, suppliers);
        }
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, throwable);
        }
    }

    @Override
    public void info(Marker marker, Supplier<?> supplier) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, supplier);
        }
    }

    @Override
    public void info(Marker marker, Supplier<?> supplier, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, supplier, throwable);
        }
    }

    @Override
    public void info(Message message) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(message);
        }
    }

    @Override
    public void info(Message message, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(message, throwable);
        }
    }

    @Override
    public void info(MessageSupplier messageSupplier) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(messageSupplier);
        }
    }

    @Override
    public void info(MessageSupplier messageSupplier, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(messageSupplier, throwable);
        }
    }

    @Override
    public void info(CharSequence charSequence) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(charSequence);
        }
    }

    @Override
    public void info(CharSequence charSequence, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(charSequence, throwable);
        }
    }

    @Override
    public void info(Object o) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(o);
        }
    }

    @Override
    public void info(Object o, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(o, throwable);
        }
    }

    @Override
    public void info(String s) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s);
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, objects);
        }
    }

    @Override
    public void info(String s, Supplier<?>... suppliers) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, suppliers);
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, throwable);
        }
    }

    @Override
    public void info(Supplier<?> supplier) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(supplier);
        }
    }

    @Override
    public void info(Supplier<?> supplier, Throwable throwable) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(supplier, throwable);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1, o2);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(marker, s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(String s, Object o) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o);
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2, o3);
        }
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        if(!TogetherManager.debug) {
            logsSkipped++;
        } else {
            backup.info(s, o, o1, o2, o3);
        }
    }

    @Override
    public boolean isDebugEnabled() {
            return false;
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
            return false;
    }

    @Override
    public boolean isEnabled(Level level) {
            return false;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker) {
            return false;
    }

    @Override
    public boolean isErrorEnabled() {
            return false;
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
            return false;
    }

    @Override
    public boolean isFatalEnabled() {
            return false;
    }

    @Override
    public boolean isFatalEnabled(Marker marker) {
            return false;
    }

    @Override
    public boolean isInfoEnabled() {
            return false;
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
            return false;
    }

    @Override
    public boolean isTraceEnabled() {
            return false;
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
            return false;
    }

    @Override
    public boolean isWarnEnabled() {
            return false;
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
            return false;
    }

    @Override
    public void log(Level level, Marker marker, Message message) {
            logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, Object o) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Message message) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Object o) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++;
    }

    @Override
    public void printf(Level level, Marker marker, String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void printf(Level level, String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public <T extends Throwable> T throwing(Level level, T t) {
            return null;
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
            return null;
    }

    @Override
    public void trace(Marker marker, Message message) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, Object o) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Message message) {
        logsSkipped++;
    }

    @Override
    public void trace(Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void trace(MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void trace(CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Object o) {
        logsSkipped++;
    }

    @Override
    public void trace(Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(String s) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void trace(Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++;
    }

    @Override
    public EntryMessage traceEntry() {
            return null;
    }

    @Override
    public EntryMessage traceEntry(String s, Object... objects) {
            return null;
    }

    @Override
    public EntryMessage traceEntry(Supplier<?>... suppliers) {
            return null;
    }

    @Override
    public EntryMessage traceEntry(String s, Supplier<?>... suppliers) {
            return null;
    }

    @Override
    public EntryMessage traceEntry(Message message) {
            return null;
    }

    @Override
    public void traceExit() {
        logsSkipped++;
    }

    @Override
    public <R> R traceExit(R r) {
            return null;
    }

    @Override
    public <R> R traceExit(String s, R r) {
            return null;
    }

    @Override
    public void traceExit(EntryMessage entryMessage) {
        logsSkipped++;
    }

    @Override
    public <R> R traceExit(EntryMessage entryMessage, R r) {
            return null;
    }

    @Override
    public <R> R traceExit(Message message, R r) {
            return null;
    }

    @Override
    public void warn(Marker marker, Message message) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, Object o) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Message message) {
        logsSkipped++;
    }

    @Override
    public void warn(Message message, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(MessageSupplier messageSupplier) {
        logsSkipped++;
    }

    @Override
    public void warn(MessageSupplier messageSupplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(CharSequence charSequence) {
        logsSkipped++;
    }

    @Override
    public void warn(CharSequence charSequence, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Object o) {
        logsSkipped++;
    }

    @Override
    public void warn(Object o, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(String s) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object... objects) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Supplier<?>... suppliers) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Supplier<?> supplier) {
        logsSkipped++;
    }

    @Override
    public void warn(Supplier<?> supplier, Throwable throwable) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logsSkipped++;
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logsSkipped++; 
    }
}