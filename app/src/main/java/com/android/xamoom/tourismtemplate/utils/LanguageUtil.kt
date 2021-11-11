package com.android.xamoom.tourismtemplate.utils

import android.content.Context
import java.util.*

class LanguageUtil(context: Context?) {

    fun getLanguagesByCodes(codes: ArrayList<String>): ArrayList<Language> {
        val resultLanguages = ArrayList<Language>()
        for (code in codes) {
            for (language in languages) {
                if (language.code == code.toUpperCase(Locale.ENGLISH)) {
                    resultLanguages.add(language)
                    break
                }
            }
        }
        return resultLanguages
    }

    data class Language(
            val code: String,
            val germanName: String,
            val englishName: String,
            val originName: String
    )


    companion object {
        private val languages = arrayListOf<Language>(
                Language("AF", "Afrikaans", "Afrikaans", "Afrikaans"),
                Language("SQ", "Albanisch", "Albanian", "Shqip"),
                Language("AM", "Amharisch", "Amharic", "አማርኛ"),
                Language("AR", "Arabisch", "Arabic", "اَللُّغَةُ اَلْعَرَبِيَّة"),
                Language("HY", "Armenisch", "Armenian", "Հայերեն"),
                Language("AZ", "Aserbaidschanisch", "Azerbaijani", "Azərbaycana"),
                Language("EU", "Baskisch", "Basque", "euskera"),
                Language("BN", "Bengalisch", "Bengali", "বাংলা ভাষা"),
                Language("BR", "Bertonisch", "Breton", "Brezhoneg"),
                Language("MY", "Birmanisch", "Burmese", "မြန်မာ"),
                Language("BS", "Bosnisch", "Bosnian", "Bosanski"),
                Language("BG", "Bulgarisch", "Bulgarian", "български"),
                Language("ZH", "Chinesisch", "Chinese", "普通話"),
                Language("DA", "Dänisch", "Danish", "Dansk"),
                Language("DE", "Deutsch", "German", "Deutsch"),
                Language("EN", "Englisch", "English", "English"),
                Language("EO", "Esperanto", "Esperanto", "Esperanto"),
                Language("ET", "Estnisch", "Estonian", "Eesti"),
                Language("FO", "Färöisch", "Faroese", "føroyskt"),
                Language("FIL", "Filipino", "Filipino", "Wikang Filipino"),
                Language("FI", "Finnisch", "Finnish", "Suomi"),
                Language("FR", "Französisch", "French", "Français"),
                Language("GL", "Galicisch", "Galician", "galego"),
                Language("GA", "Gälisch", "Goidelic", "teangacha Gaelacha"),
                Language("KA", "Georgisch", "Georgian", "ქართული ენა"),
                Language("EL", "Griechisch", "Greek", "ελληνική"),
                Language("KL", "Grönländisch", "Greenlandic", "Kalaallisut"),
                Language("GH", "Guaraní", "Guarani", "avañe'ẽ"),
                Language("HT", "Haitianisch", "Haitian Creole", "kreyòl ayisyen"),
                Language("HA", "Hausa", "Hausa", "هَرْشَن هَوْسَ"),
                Language("HAW", "Hawaianisch", "Hawaiian", "ʻŌlelo Hawaiʻi"),
                Language("IW", "Hebräisch", "Hebrew", "עברית"),
                Language("HI", "Hindisch", "Hindi", "हिन्दी"),
                Language("IG", "Igbo", "Igbo", "Igbo"),
                Language("IN", "Indonesisch", "Malay", "bahasa Indonesia"),
                Language("IS", "Isländisch", "Icelandic", "Íslenska"),
                Language("IT", "Italienisch", "Italian", "Italiano"),
                Language("JA", "Japanisch", "Japanese", "日本語"),
                Language("JV", "Javanisch", "Javanese", "basa Jawa"),
                Language("KH", "Kambodschanisch", "Cambodian", "ភាសាខ្មែរ"),
                Language("KZ", "Kasachisch", "Kazakh", "Қазақ тілі"),
                Language("CA", "Katalanisch", "Catalan", "Català"),
                Language("KM", "Khmer", "Khmer", "ភាសាខ្មែរ"),
                Language("KY", "Kirgisisch", "Kyrgyz", "Кыргыз тили/Kyrgyz tili"),
                Language("RN", "Kirundi", "Kirundi", "Kirundi"),
                Language("KO", "Koreanisch", "Korean", "한국말"),
                Language("HR", "Kroatisch", "Croatian", "Hrvatski"),
                Language("KU", "Kurdisch", "Kurdish", "کوردی"),
                Language("LO", "Laotisch", "Lao", "ພາສາລາວ"),
                Language("LV", "Lettisch", "Latvian", "Latviešu"),
                Language("LT", "Litauisch", "Lithuanian", "Lietuvių"),
                Language("MG", "Madagassisch", "Malagasy", "Malagasy"),
                Language("MA", "Malaiisch", "Malay", "بهاس ملايو"),
                Language("MT", "Maltesisch", "Maltese", "Malti"),
                Language("MI", "Maorisch", "Maori", "Māori"),
                Language("MR", "Marathi", "Marathi", "मराठी"),
                Language("MK", "Mazedonisch", "Mazedonian", "македонски"),
                Language("CNR", "Montenegrinisch", "Montenegrin", "Црногорски језик"),
                Language("NE", "Nepalesisch", "Nepalese", "नेपाली"),
                Language("NL", "Niederländisch", "Dutch", "Nederlands"),
                Language("NB", "Norwegisch/Bokmål", "Norwegian/Bokmål", "Bokmål"),
                Language("NN", "Norwegisch/Nynorsk", "Norwegian/Nynorsk", "Nynorsk"),
                Language("NG", "Oshiwambo", "Ovambo", "OshiVambo"),
                Language("PAN", "Panjabi", "Panjabi", "ਪੰਜਾਬੀ"),
                Language("FA", "Persisch", "Persian", "زبان فارسی"),
                Language("PL", "Polnisch", "Polish", "Polski"),
                Language("PT", "Portugiesisch", "Portugese", "Português"),
                Language("RM", "Rätoromanisch", "Rhaeto-Romance", "Rumantsch"),
                Language("RO", "Rumänisch", "Romanian", "Română"),
                Language("RU", "Russisch", "Russian", "Русский"),
                Language("SE", "Uralisch", "Northern Sami", "Davvisámegiella"),
                Language("SG", "Sango", "Sango", "Sängö"),
                Language("SV", "Schwedisch", "Swedish", "Svenska"),
                Language("SR", "Serbisch", "Serbian", "српски"),
                Language("SI", "Singalesisch", "Singalese", "සිංහල"),
                Language("SS", "Siswati", "Siswati", "siSwati"),
                Language("SK", "Slowakisch", "Slowakian", "Slovenčina"),
                Language("SL", "Slowenisch", "Slovene", "Slovenščina"),
                Language("SO", "Somali", "Somali", "Af Soomaali"),
                Language("ES", "Spanisch", "Spanish", "Español"),
                Language("SW", "Swahili", "Swahili", "Kiswahili"),
                Language("TA", "Tamil", "Tamil", "தமிழ்"),
                Language("TE", "Telugu", "Telugu", "తెలుగు"),
                Language("TH", "Thailändisch", "Thai", "ภาษาไทย"),
                Language("BO", "Tibetisch", "Tibetan", "བོད་སྐད"),
                Language("TI", "Tigrinya", "Tigrinya", "ትግርኛ"),
                Language("CZ", "Tschechisch", "Czech", "Čeština"),
                Language("TR", "Türkisch", "Turkish", "Türkçe"),
                Language("HU", "Ungarisch", "Hungarian", "Magyar"),
                Language("UR", "Urdu", "Urdu", "اردو"),
                Language("UK", "Urkainisch", "Ukrainian", "українська"),
                Language("UZ", "Usbekisch", "Uzbek", "Oʻzbek tili"),
                Language("VI", "Vietnamesisch", "Vietnamese", "Tiếng Việt"),
                Language("CY", "Walisisch", "Welsh", "Cymraeg"),
                Language("BE", "Weißrussisch", "Belarussian", "беларуская мова"),
                Language("XH", "Xhosa", "Xhosa", "isiXhosa"),
                Language("YO", "Yoruba", "Yoruba", "èdè Yorùbá"),
                Language("ZU", "Zulu", "Zulu", "isiZulu")
        )
    }
}