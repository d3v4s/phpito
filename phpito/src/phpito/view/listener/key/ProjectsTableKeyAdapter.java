package phpito.view.listener.key;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import phpito.view.listener.selection.launcher.LauncherAddProjectSelectionAdapter;
import phpito.view.listener.selection.launcher.LauncherDeleteProjectSelectionListener;
import phpito.view.listener.selection.launcher.LauncherSettingsProjectSelectionAdapter;
import phpito.view.listener.selection.server.StartServerSelectionAdapter;
import phpito.view.listener.selection.server.StopServerSelectionAdapter;
import phpito.view.shell.ShellPHPito;

/**
 * Class for manage key pressed on projects table
 * @author Andrea Serra
 *
 */
public class ProjectsTableKeyAdapter extends KeyAdapter {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public ProjectsTableKeyAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	/* event key pressed */
	@Override
	public void keyPressed(KeyEvent ke) {
		int kc = ke.keyCode;
		if (!(kc == SWT.ARROW_UP || kc == SWT.ARROW_DOWN)) ke.doit = false;
		switch (String.valueOf(ke.character)) {
			case "R":
			case "r":
				/* case start server */
				new StartServerSelectionAdapter(shellPHPito).startServer();
				break;
			case "S":
			case "s":
				/* case stop server */
				new StopServerSelectionAdapter(shellPHPito).stopServer();
				break;
			case "C":
			case "c":
				/* case modify project */
				new LauncherSettingsProjectSelectionAdapter(shellPHPito).launchModifyProject();
				break;
			case "A":
			case "a":
				/* case add new project */
				new LauncherAddProjectSelectionAdapter(shellPHPito).launchAddProject();
				break;
			case "D":
			case "d":
				/* case delete project */
				new LauncherDeleteProjectSelectionListener(shellPHPito).showDeleteProject();
				break;
			default:
				break;
		}
	}
	
	
}
