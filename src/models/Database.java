package models;

import models.datastructures.DataScore;
import models.datastructures.DataWords;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * See klass tegeleb andmebaasi ühenduse ja "igasuguste" päringutega tabelitest.
 * Alguses on ainult ühenduse jaoks funktsionaalsus
 */
public class Database {
    private static Database instance;
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
        this.connection = createConnection();
        this.selectUniqueCatgories();
    }

    public static Database getInstance(Model model) {
        if (instance == null) {
            instance = new Database(model);
        }
        return instance;
    }

    /**
     * Loob andmebaasiga ühenduse
     * @return andmebaasi ühenduse
     */
    private Connection createConnection() {
        try {
            Connection conn = DriverManager.getConnection(databaseUrl);
            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA busy_timeout = 10000");
            stmt.close();
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Andmebaasiga ühendumine ebaõnnestus");
        }
    }
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = createConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check or re-establish connection");
        }
        return connection;
    }

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
    public DataWords selectRandomWord(String category) {
        String sql = "SELECT * FROM words ";
        if (!category.equals(model.getChooseCategory())) {
            sql += "WHERE category = ? ";
        }
        sql += "ORDER BY RANDOM() LIMIT 1;";
        try {

            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            if (!category.equals(model.getChooseCategory())) {
                pstmt.setString(1, category);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("word").toUpperCase();
                String wordCategory = rs.getString("category");
                return new DataWords(id, word, wordCategory);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public synchronized void saveScore(String playerName, String guessWord, String wrongCharacters, int gameTime) {
        String sql = "INSERT INTO scores (playertime, playername, guessword, wrongcharacters, gametime) VALUES (?, ?, ?, ?, ?)";
        try {

            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setString(2, playerName);
            stmt.setString(3, guessWord);
            stmt.setString(4, formatWrongCharacters(wrongCharacters));
            stmt.setInt(5, gameTime);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data saved successfully");
            }

        } catch (SQLException e) {
            System.out.println("Error saving data: " + e.getMessage());
            throw new RuntimeException(e);  // Handle exception by throwing runtime exception
        }
    }
    private String formatWrongCharacters(String wrongCharacters) {
        return wrongCharacters.replaceAll("[\\[\\]]", "").replaceAll(", ", ", ");
    }
}
