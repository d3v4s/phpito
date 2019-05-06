package it.phpito.view.listener.selection.rgb;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.jaswt.core.Jaswt;
import it.jaswt.exception.ParameterException;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellDialogSettings;

public class ColorsScaleSelectionAdapter extends SelectionAdapter {
	private ShellDialogSettings shellDialogSettings;

	public ColorsScaleSelectionAdapter(ShellDialogSettings shellDialog) {
		this.shellDialogSettings = shellDialog;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		HashMap<String, Integer> colorsRGBMap = shellDialogSettings.getColorsRGBMap(); 
		try {
			if (shellDialogSettings.getElementLogList().getSelectionIndex() == 0)
				shellDialogSettings.setColorBackgrndLogMonMap(colorsRGBMap);
			else if (shellDialogSettings.getElementLogList().getSelectionIndex() == 1)
				shellDialogSettings.setColorForegrndLogMonMap(colorsRGBMap);

			shellDialogSettings.getViewColorLabel().setRed(colorsRGBMap.get(PHPitoConf.K_COLOR_RED));
			shellDialogSettings.getViewColorLabel().setGreen(colorsRGBMap.get(PHPitoConf.K_COLOR_GREEN));
			shellDialogSettings.getViewColorLabel().setBlue(colorsRGBMap.get(PHPitoConf.K_COLOR_BLUE));
			shellDialogSettings.getViewColorLabel().redraw();

			shellDialogSettings.getHexColorLbl().setText(shellDialogSettings.getHexColors());
		} catch (ParameterException e) {
			Jaswt.getInstance().lunchMBError(shellDialogSettings, e, PHPitoManager.NAME);
		}
	}
}
