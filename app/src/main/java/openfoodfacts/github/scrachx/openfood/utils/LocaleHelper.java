package openfoodfacts.github.scrachx.openfood.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import openfoodfacts.github.scrachx.openfood.views.OFFApplication;

import java.util.Locale;

/**
 * This class is used to change your application locale and persist this change for the next time
 * that your app is going to be used.
 * <p/>
 * You can also change the locale of your application on the fly by using the setLocale method.
 * <p/>
 * Created by gunhansancar on 07/10/15.
 */
public class LocaleHelper {

    private LocaleHelper(){
        //Helper class
    }
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    public static final String USER_COUNTRY_PREFERENCE_KEY = "user_country";

    public static void onCreate(Context context) {
        String lang = getLanguageInPreferences(context, Locale.getDefault().getLanguage());
        setLocale(context, lang);
    }

    public static void onCreate(Context context, String defaultLanguage) {
        String lang = getLanguageInPreferences(context, defaultLanguage);
        setLocale(context, lang);
    }

    public static Locale getLocale() {
        return getLocale(OFFApplication.getInstance());
    }

    public static Context setLocale(Locale locale) {
        return setLocale(OFFApplication.getInstance(), locale);
    }

    public static String getLanguage(Context context) {
        String lang = getLanguageInPreferences(context, Locale.getDefault().getLanguage());
        if (lang.contains("-")) {
            lang = lang.split("-")[0];
        }
        return lang;
    }

    public static Locale getLocale(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        final Locale locale = configuration.locale;
        return locale == null ? Locale.getDefault() : locale;
    }

    public static Context setLocale(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(SELECTED_LANGUAGE, language)
            .apply();

        Locale locale = getLocale(language);
        return setLocale(context, locale);
    }

    public static Context setLocale(Context context, Locale locale) {
        if (locale == null) {
            return context;
        }
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(SELECTED_LANGUAGE, locale.getLanguage())
            .apply();

        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    /**
     * Extract language and region from the locale string
     *
     * @param locale language
     * @return Locale from locale string
     */
    public static Locale getLocale(String locale) {
        String[] localeParts = locale.split("-");
        String language = localeParts[0];
        String country = localeParts.length == 2 ? localeParts[1] : "";
        Locale localeObj = null;
        if (locale.contains("+")) {
            localeParts = locale.split("\\+");
            language = localeParts[1];
            String script = localeParts[2];
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                for (Locale checkLocale : Locale.getAvailableLocales()) {
                    if (checkLocale.getISO3Language().equals(language) && checkLocale.getCountry().equals(country) && checkLocale.getVariant().equals("")) {
                        localeObj = checkLocale;
                    }
                }
            } else {
                localeObj = new Locale.Builder().setLanguage(language).setRegion(country).setScript(script).build();
            }
        } else {
            localeObj = new Locale(language, country);
        }
        return localeObj;
    }

    private static String getLanguageInPreferences(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

}
