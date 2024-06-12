package listeners;

import models.Model;
import views.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonNew implements ActionListener {
    private Model model;
    private View view;
    public ButtonNew(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println("Klick");  // test, kas töötab
        view.hideButtons();
        if(!view.getGameTimer().isRunning()){  // st mänguaeg ei jookse
            view.getGameTimer().setSeconds(0);
            view.getGameTimer().setMinutes(0);
            view.getGameTimer().setRunning(true); // aeg jooksma
            view.getGameTimer().startTime(); // käivita aeg
        } else {
            view.getGameTimer().stopTime();
            view.getGameTimer().setRunning(false);
        }
        // TODO siit omaarendus. label tühjaks, leida andmebaasist sõna ja tähtede arv allkriipse


    }

}
