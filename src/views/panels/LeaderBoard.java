package views.panels;

import models.Database;
import models.Model;
import views.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * See on edetabeli klass. See näitab andmebaasist loetud edetabelit. Seda ei saa mängimise ajal
 * vaadata.
 */
public class LeaderBoard extends JPanel {
    /**
     * Klassisisene mudel, mille väärtus saadakse View konstruktorist ja loodud MainApp-is
     */
    private final Model model;
    /**
     * Klassisisene vaade, mille väärtus saadakse otse View-st
     */
    private final View view;
    /**
     * Tabelipäise loomine, läheb edetabelivaatelehel
     */
    private String[] heading = new String[] {"Kuupäev", "Nimi", "Sõna", "Tähed", "Mänguaeg"};
/**
 * loome tabeli teostuse päisega kuid andmeid pole
 */
    private DefaultTableModel dtm = new DefaultTableModel(heading, 0);
    /**
     * Loome tabeli dtm baasil
     */
    private JTable table = new JTable(dtm);
    /**
     * leaderboard konstruktor
     * @param model  loodud mudel mainappis
     * @param view loodud view mainappis
     */
    public LeaderBoard(Model model, View view) {
        this.model = model;
        this.view = view;

        setLayout(new BorderLayout());
        setBackground(new Color(250, 150, 215)); // Leaderboard paneeli taustavärv
        setBorder(new EmptyBorder(5, 5, 5, 5));

        model.setDtm(dtm);

        createLeaderboard();
    }

    private void createLeaderboard() {
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        // tabeli esimene eerg 100px
        table.getColumnModel().getColumn(0).setPreferredWidth(120);

        // tabeli sisu pole muudetav 1990817 <-- stackoverflow
        table.setDefaultEditor(Object.class, null);

        // lahtri keskele joondamine
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(cellRenderer);


        // kirjuta tabelist sisu mudelisse
        new Database(model).selectScores();
        //kontrolli andmeid ja uuenda tabelit
        if(!model.getDataScores().isEmpty()) { // kui list ei ole tühi
            view.updateScoreTable();
        } else {
            JOptionPane.showMessageDialog(view, "esmalt tuleb mängida.");
        }

    }

}
