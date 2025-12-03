import javax.swing.SwingUtilities;

import controller.Controller;
import view.Othello;

public class main {
	public static void main(String[] args) {
		Othello view = new Othello();
		Controller controller = new Controller(view);
		view.setController(controller);
	}
}
