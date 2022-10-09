package chronoMods.utilities;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AntiConsolePrintingPatches {
    public static int logsSkipped = 0;
    @SpirePatch(clz = LogManager.class, method = "getLogger", paramtypez = {String.class})
    public static class RemoveLogging {
        @SpirePostfixPatch
        public static Logger patch(Logger __result, String s) {
            AntiLogger newLogger = new AntiLogger();
            newLogger.setBackup(__result);
            return newLogger;
        }
    }
}