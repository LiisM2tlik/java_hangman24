package models;

import models.datastructures.DataScore;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * See klass tegeleb andmebaasi ühenduse ja "igasuguste" päringutega tabelitest.
 * Alguses on ainult ühenduse jaoks funktsionaalsus
 */
public class Database {
    /**
     * Algselt ühendust pole
     */
    private Connection connection = null;
    /**
     * Andmebaasi ühenduse string
     */
    private String databaseUrl;
    /**
     * Loodud mudel
     */
    private Model model;

    /**
     * Klassi andmebaas konstruktor
     * @param model loodud mudel
     */
    public Database(Model model) {
        this.model = model;
        this.databaseUrl = "jdbc:sqlite:" + model.getDatabaseFile();
        this.selectUniqueCatgories();
    }

    /**
     * Loob andmebaasiga ühenduse
     * @return andmebaasi ühenduse
     */
    private Connection dbConnection() throws SQLException {
        // https://stackoverflow.com/questions/13891006/
        if(connection != null) {
            connection.close();
        }
        connection = DriverManager.getConnection(databaseUrl);
        return connection;
    }

    private void selectUniqueCatgories() {
        String sql = "SELECT DISTINCT(category) as category FROM words ORDER BY category;";
        List<String> categories = new ArrayList<>(); // tühi kategooriate list
        try {
            Connection connection = this.dbConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String category = rs.getString("category");
                categories.add(category); // lisa kategooria listi kategooriad
            }
            categories.add(0, model.getChooseCategory()); // esimene kirje "kõik kategooriad"
            //System.out.println(categories);  // test, kas kategooriad saab kätte
            String[] result = categories.toArray(new String[0]);  // List<String> tehakse stringimassiiviks => String[]
            model.setCmbCategories(result); // määrab kategooriad mudelisse.
            connection.close();  // kui andmed käes siis tuleks ühendus kinni panna.
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectScores() {
        String sql = "SELECT * FROM scores ORDER BY gametime, playertime DESC, playername;";
        List<DataScore> data = new ArrayList<>();
        try {
            Connection connection = this.dbConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            model.getDataScores().clear();  // tühjenda mudelis listi sisu. ehk siis kui seda siin ei oleks siis igal kutsumisel tuleb uuesti andmed ja lõpuks topelt andmed.

            while (rs.next())  {
                String datetime = rs.getString("playertime");
                LocalDateTime playerTime = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String playerName = rs.getString("playername");
                String guessWord = rs.getString("guessword");
                String wrongChar = rs.getString("wrongcharacters");
                int timesSeconds = rs.getInt("gametime");
                // System.out.println(datetime + " |" + playertime);
                // lisa listi kirje kaupa
                data. add(new DataScore(playerTime, playerName, guessWord, wrongChar, timesSeconds));
            }
            model.setDataScores(data);
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
