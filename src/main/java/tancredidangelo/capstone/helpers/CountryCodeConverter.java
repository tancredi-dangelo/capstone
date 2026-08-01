package tancredidangelo.capstone.helpers;
import tancredidangelo.capstone.exceptions.ValidationException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryCodeConverter {

    private static final Map<String, String> NAME_TO_CODE = buildNameToCodeMap();

    private static final Map<String, String> CODE_TO_NAME = buildCodeToNameMap();

    private static Map<String, String> buildNameToCodeMap() {
        Map<String, String> map = new HashMap<>();

        for (String isoCode : Locale.getISOCountries()) {
            Locale locale = new Locale.Builder().setRegion(isoCode).build();
            String englishName = locale.getDisplayCountry(Locale.ENGLISH);
            map.put(englishName.toLowerCase(), isoCode);
        }

        return map;
    }

    private static Map<String, String> buildCodeToNameMap() {
        Map<String, String> map = new HashMap<>();

        for (String isoCode : Locale.getISOCountries()) {
            Locale locale = new Locale.Builder().setRegion(isoCode).build();
            String englishName = locale.getDisplayCountry(Locale.ENGLISH);
            map.put(isoCode.toUpperCase(), englishName);
        }

        return map;
    }



    public static String toIsoCode(String countryName) {
        if (countryName == null || countryName.isBlank()) {
            throw new ValidationException("Country is required.");
        }

        String normalized = countryName.trim().toLowerCase();

        // Se è già un codice ISO valido di 2 lettere, accettalo direttamente
        if (normalized.length() == 2 && NAME_TO_CODE.containsValue(normalized.toUpperCase())) {
            return normalized.toUpperCase();
        }

        String code = NAME_TO_CODE.get(normalized);
        if (code == null) {
            throw new ValidationException("Country '" + countryName + "' is not recognized.");
        }

        return code;
    }



    public static String toCountryName(String isoCode) {
        if (isoCode == null || isoCode.isBlank()) {
            throw new ValidationException("Country ISO code is required.");
        }

        String normalized = isoCode.trim().toUpperCase();

        String name = CODE_TO_NAME.get(normalized);
        if (name == null) {
            throw new ValidationException("Country code '" + isoCode + "' is not recognized.");
        }
        return name;
    }



}
