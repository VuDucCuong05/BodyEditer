package net.braincake.bodytune.model;

import net.braincake.bodytune.R;

import java.util.Arrays;
import java.util.List;

public class Language {
    private int idLanguageR;
    private int nameLanguageR;
    private int flagLanguage;
    private String codeLanguage;
    private Boolean selectLanguage;

    public Language() {
    }

    public Language(int idLanguageR, int nameLanguageR, int flagLanguage, String codeLanguage, Boolean selectLanguage) {
        this.idLanguageR = idLanguageR;
        this.nameLanguageR = nameLanguageR;
        this.flagLanguage = flagLanguage;
        this.codeLanguage = codeLanguage;
        this.selectLanguage = selectLanguage;
    }

    public static final List<Language> listLanguage = Arrays.asList(
            new Language(0, R.string.text_english, R.drawable.ic_english, "en", false),
            new Language(1, R.string.text_french, R.drawable.ic_french, "fr", false),
            new Language(2, R.string.text_portuguese, R.drawable.ic_french, "pt", false),
            new Language(3, R.string.text_spanish, R.drawable.ic_spanish, "es", false),
            new Language(4, R.string.text_german, R.drawable.ic_germany, "de", false),
            new Language(5, R.string.text_hindi, R.drawable.ic_hindi, "hi", false),
            new Language(6, R.string.text_indonesia, R.drawable.ic_indonesia, "id", false),
            new Language(7, R.string.text_china, R.drawable.ic_china, "zh", false)
    );

    public int getIdLanguageR() {
        return idLanguageR;
    }

    public void setIdLanguageR(int idLanguageR) {
        this.idLanguageR = idLanguageR;
    }

    public int getNameLanguageR() {
        return nameLanguageR;
    }

    public void setNameLanguageR(int nameLanguageR) {
        this.nameLanguageR = nameLanguageR;
    }

    public int getFlagLanguage() {
        return flagLanguage;
    }

    public void setFlagLanguage(int flagLanguage) {
        this.flagLanguage = flagLanguage;
    }

    public String getCodeLanguage() {
        return codeLanguage;
    }

    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
    }

    public Boolean getSelectLanguage() {
        return selectLanguage;
    }

    public void setSelectLanguage(Boolean selectLanguage) {
        this.selectLanguage = selectLanguage;
    }
}
