import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import br.edu.campusprime.interfacegrafica.TelaPrincipal;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println(
                    "Não foi possível aplicar o tema do sistema."
                );
            }

            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}