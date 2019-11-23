package phpito.view.listener.selection.rgb;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import jaswt.core.Jaswt;
import jaswt.exception.ParameterException;
import phpito.core.PHPitoManager;
import phpito.view.shell.dialog.ShellDialogSettings;

/**
 * Class SelectionAdapter for draw the color 
 * @author Andrea Serra
 *
 */
public class DrawerColorLogMonSelectionAdapter extends SelectionAdapter{
	private ShellDialogSettings shellDialogSettings;
	private int type;
	public static final int LAYER = 0;
	public static final int SCALE = 1;

	/* CONSTRUCT */
	public DrawerColorLogMonSelectionAdapter(ShellDialogSettings shellDialogSettings, int type) {
		this.shellDialogSettings = shellDialogSettings;
		this.type = type;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		try {
			switch (type) {
				case LAYER:
					/* set colors on label view color by layer */
					shellDialogSettings.setColorByLayerAndRedraw();
					break;
				case SCALE:
					/* set colors on label view color by scale */
					shellDialogSettings.setColorByScaleAndRedraw();
					break;
				default:
					break;
			}
		} catch (ParameterException e) {
			Jaswt.getInstance().launchMBError(shellDialogSettings, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
