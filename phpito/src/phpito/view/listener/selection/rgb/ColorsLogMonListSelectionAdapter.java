package phpito.view.listener.selection.rgb;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;

import jaswt.core.Jaswt;
import jaswt.exception.ParameterException;
import phpito.core.PHPitoConf;
import phpito.core.PHPitoManager;
import phpito.view.shell.dialog.ShellDialogSettings;

/**
 * Class SelectionAdapter for view color of layer selected
 * @author Andrea Serra
 *
 */
public class ColorsLogMonListSelectionAdapter extends SelectionAdapter{
	private ShellDialogSettings shellDialogSettings;

	/* CONSTRUCT */
	public ColorsLogMonListSelectionAdapter(ShellDialogSettings shellDialogSettings) {
		this.shellDialogSettings = shellDialogSettings;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent se) {
		List list = shellDialogSettings.getElementLogList();
		HashMap<String, Integer> colorMap = null;
		/* select layer to be select color */
		switch (list.getSelectionIndex()) {
			case 0:
				/* case background */
				colorMap = shellDialogSettings.getColorBackgrndLogMonMap();
				break;
			case 1:
				/* case foreground */
				colorMap = shellDialogSettings.getColorForegrndLogMonMap();
				break;
			default:
				/* disable scale RGB */
				for (String key : shellDialogSettings.getColorScaleMap().keySet()) shellDialogSettings.getColorScaleMap().get(key).setEnabled(false);
				break;
		}
//		if (list.getSelectionIndex() == 0) colorMap = shellDialogSettings.getColorBackgrndLogMonMap();
//		else if (list.getSelectionIndex() == 1) colorMap = shellDialogSettings.getColorForegrndLogMonMap();
//		else {
//			
//			return;
//		}

		/* set colors on label view color */
		try {
			shellDialogSettings.getViewColorLabel().setRed(colorMap.get(PHPitoConf.K_COLOR_RED));
			shellDialogSettings.getViewColorLabel().setGreen(colorMap.get(PHPitoConf.K_COLOR_GREEN));
			shellDialogSettings.getViewColorLabel().setBlue(colorMap.get(PHPitoConf.K_COLOR_BLUE));
			shellDialogSettings.getViewColorLabel().redraw();

			HashMap<String, Scale> colorsScaleMap = shellDialogSettings.getColorScaleMap();
			for (String key : colorsScaleMap.keySet()) colorsScaleMap.get(key).setSelection(colorMap.get(key));
			
			shellDialogSettings.getHexColorLbl().setText(shellDialogSettings.getHexColors());
		} catch (ParameterException e) {
			Jaswt.getInstance().launchMBError(shellDialogSettings, e, PHPitoManager.getInstance().getJoggerError());
		}
	}
}
