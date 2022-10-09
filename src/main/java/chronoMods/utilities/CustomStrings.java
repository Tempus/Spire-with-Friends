package chronoMods.utilities;

import chronoMods.TogetherManager;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CustomStrings
{

    public String[] STRINGS;


    public CustomStrings getCustomStrings(String strings) {
        if (TogetherManager.CustomStringsMap.containsKey(strings)) {
            return (CustomStrings) TogetherManager.CustomStringsMap.get(strings);
        }

        return null;
    }

    public static Map<String, CustomStrings> importCustomStrings()
    {

        Gson gson = new Gson();
        Settings.GameLanguage language = Settings.language;

        String customStrings = Gdx.files.internal("chrono/localization/together-strings.json").readString(String.valueOf(StandardCharsets.UTF_8));

        Type typeToken = new TypeToken<Map<String, CustomStrings>>() {}.getType();

        Map customStringsMap = (Map) gson.fromJson(customStrings, typeToken);

        return customStringsMap;

    }
}