package hu.akusius.palenque.layout;

import hu.akusius.palenque.layout.op.OperationManager;
import hu.akusius.palenque.layout.ui.DialogParent;
import hu.akusius.palenque.layout.ui.MainPanel;
import hu.akusius.palenque.layout.util.UIUtils;
import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 * A fő futtatóosztály.
 * @author Bujdosó Ákos
 */
public class Main {

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (!UIUtils.setLookAndFeel()) {
          return;
        }

        JFrame f = new JFrame("The Palenque Code – Layout");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new MainPanel(new OperationManager(), new DialogParent(f)));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
      }
    });
  }

}
