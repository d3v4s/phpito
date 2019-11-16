package phpito.view.listener.selection.rgb;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import jaswt.core.Jaswt;
import jaswt.exception.ParameterException;
import phpito.core.PHPitoConf;
import phpito.core.PHPitoManager;
import phpito.view.shell.dialog.ShellDialogSettings;

/**
 * Class SelectionAdapter for view color when change color scale
 * @author Andrea Serra
 *
 */
public class ColorsScaleSelectionAdapter extends SelectionAdapter {
	private ShellDialogSettings shellDialogSettings;

	/* CONSTRUCT */
	public ColorsScaleSelectionAdapter(ShellDialogSettings shellDialog) {
		this.shellDialogSettings = shellDialog;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		HashMap<String, Integer> colorsRGBMap = shellDialogSettings.getColorsRGBMap(); 
		try {
			switch (shellDialogSettings.getElementLogList().getSelectionIndex()) {
			case 0:
				/* case background */
				shellDialogSettings.setColorBackgrndLogMonMap(colorsRGBMap);
				break;
			case 1:
				/* case foreground */
				shellDialogSettings.setColorForegrndLogMonMap(colorsRGBMap);
				break;
			default:
				break;
			}

			/* set RGB colors on label */
			shellDialogSettings.getViewColorLabel().setRed(colorsRGBMap.get(PHPitoConf.K_COLOR_RED));
			shellDialogSettings.getViewColorLabel().setGreen(colorsRGBMap.get(PHPitoConf.K_COLOR_GREEN));
			shellDialogSettings.getViewColorLabel().setBlue(colorsRGBMap.get(PHPitoConf.K_COLOR_BLUE));
			shellDialogSettings.getViewColorLabel().redraw();

			shellDialogSettings.getHexColorLbl().setText(shellDialogSettings.getHexColors());
		} catch (ParameterException e) {
			Jaswt.getInstance().launchMBError(shellDialogSettings, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
