package models;

import models.datastructures.DataScore;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.*;

public class Model {
    private final String chooseCategory = "Kõik kategooriad";
    /**
     * See on vaikimisi andmebaasi fail kui käsurealt uut ei leitud. Andmebaasi tabelit nimed ja struktuurid peavad
     * samad olema, kuid andmed võivad erinevad olla.
     *  hangman_words_ee.db - Eestikeelsed sõnad, edetabel on tühi
     *  hangman_words_en.db - Inglisekeelsed sõnad, edetabel on tühi
     *  hangman_words_ee_test.db - Eestikeelsed sõnad, edetabel EI ole tühi
     */
    private String databaseFile = "hangman_words_ee.db";
    /**
     * kaust kus on võllapuu pildid
     */
    private String imagesFolder = "images";
    /**
     * siin on kõik võllapuu pildid õiges järjekorras. (00-11)
     */
    private List<String> imageFiles = new ArrayList<>();


    private String selectedCategory; // Vaikimisi valitud kategooria
    private String[] cmbCategories; // rippmenüü sisu
    /**
     * tabeli mugavaks kasutamiseks
     */
    private DefaultTableModel dtm;

    /**
     * edetabeli andmed listis
     */
    private List<DataScore> dataScores = new ArrayList<>();
    private String currentWord;
    private char[] guessedChars;
    private List<Character> wrongGuesses;

    public Model(String dbName) {
        if(dbName != null) {
            this.databaseFile = dbName; // käsurealt saadud andmbaas kasutusel
        }
        // System.out.println(this.databaseFile);
        new Database(this); // Loome andmebaasi ühenduse
        readImagesFolder();
        selectedCategory = chooseCategory; // Vaikimisi "Kõik kategooriad"
    }

    /**
     * tagastab võllapuu pildid
     * @return võllapuu pildid listina List<String>
     */
    public List<String> getImageFiles() {
        return imageFiles;
    }

    private void readImagesFolder() {
        // see loeb siis pilte kaustast
        String imagesFolder = "images";
        File folder = new File(imagesFolder); // loome kausta objekti
        File[] files = folder.listFiles();
        for(File file : Objects.requireNonNull(files)) {
            imageFiles.add(file.getAbsolutePath());  // täispikk kausta tee
        }
        // Collections.sort(imageFiles); // sorteerimiseks kasvavalt kausta sisu
        // System.out.println(imageFiles); // testimiseks, kas saan õige asja kätte ja kasutab
    }
    public void startNewGame(String word) {
        this.currentWord = word;
        this.guessedChars = new char[word.length()];
        Arrays.fill(guessedChars, '_');
        this.wrongGuesses = new ArrayList<>();
    }
    /**
     * Rippmenüü esimene valik enne kategooriaid
     * @return teksti "Kõik kategooriad"
     */
    public String getChooseCategory() {
        return chooseCategory;
    }

    /**
     * Millise andmebaasiga on tegemist
     * @return andmebaasi failinimi
     */
    public String getDatabaseFile() {
        return databaseFile;
    }

    /**
     * Seadistab uue andmebaasi failinime, kui see saadi käsurealt
     * @param databaseFile uus andmebaasi failinimi
     */
    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
    }

    /**
     * Valitud kategoori
     * @return tagastab valitud kategooria
     */
    public String getSelectedCategory() {
        return selectedCategory;
    }

    /**
     * Seadistab valitud kategooria
     * @param selectedCategory uus valitud kategooria
     */
    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    /**
     * Kategooriate nimed
     * @return kategooriate nimed
     */
    public String[] getCmbCategories() {
        return cmbCategories;
    }

    /**
     * seadistab uued kategooriate nimed
     * @param cmbCategories kategooriate massiiv
     */
    public void setCmbCategories(String[] cmbCategories) {
        this.cmbCategories = cmbCategories;
    }

    public String getWrongGuessesAsString() {
        return getWrongGuesses().toString().replaceAll("[\\[\\]]", "");
    }
    /**
     *
     * @return Deafulttablemodel
     */
    public DefaultTableModel getDtm() {
        return dtm;
    }

    /**
     * seadisytame uue deafulttablemodeli
     * @param dtm uus dtm
     */
    public void setDtm(DefaultTableModel dtm) {
        this.dtm = dtm;
    }

    /**
     * loetud edetabi andmed andmebaasist
     * @return edetabeli andmed
     */
    public List<DataScore> getDataScores() {
        return dataScores;
    }

    /**
     * muudab edetabeli andmeid
     * @param dataScores uued andmed edetabeli jaoks
     */
    public void setDataScores(List<DataScore> dataScores) {
        this.dataScores = dataScores;
    }

    public char[] getGuessedChars() {
        return guessedChars;
    }
    public List<Character> getWrongGuesses() {
        return wrongGuesses;
    }
    public String getCurrentWord() {
        return currentWord;
    }
}
