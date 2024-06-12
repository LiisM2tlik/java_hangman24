package controllers;

import listeners.ButtonCancel;
import listeners.ButtonNew;
import listeners.ComboboxChange;
import models.Model;
import views.View;

public class Controller {
    public Controller(Model model, View view) {
        // Combobox funktsionaalsus
        view.getSettings().getCmbCategory().addItemListener(new ComboboxChange(model));

        // Uus mäng funktsionaalsus
        view.getSettings().getBtnNewGame().addActionListener(new ButtonNew(model, view));

        //katkesta nupu funktsionaalsus
        view.getGameBoard().getBtnCancel().addActionListener(new ButtonCancel(model,view));

    }
}
