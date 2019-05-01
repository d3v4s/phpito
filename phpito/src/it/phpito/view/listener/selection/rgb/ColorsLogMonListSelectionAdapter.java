package it.phpito.view.listener.selection.rgb;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;

import it.as.utils.exception.FormatException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellDialogSettings;

public class ColorsLogMonListSelectionAdapter extends SelectionAdapter{
	private ShellDialogSettings shellDialogSettings;

	public ColorsLogMonListSelectionAdapter(ShellDialogSettings shellDialogSettings) {
		this.shellDialogSettings = shellDialogSettings;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		List list = shellDialogSettings.getElementLogList();
		HashMap<String, Integer> colorMap;
		if (list.getSelectionIndex() == 0)
			colorMap = shellDialogSettings.getColorBackgrndLogMonMap();
		else if (list.getSelectionIndex() == 1)
			colorMap = shellDialogSettings.getColorForegrndLogMonMap();
		else {
			for (String key : shellDialogSettings.getColorScaleMap().keySet())
				shellDialogSettings.getColorScaleMap().get(key).setEnabled(false);
			return;
		}
		try {
			shellDialogSettings.getViewerColorCanvas().setRed(colorMap.get(PHPitoConf.K_COLOR_RED));
			shellDialogSettings.getViewerColorCanvas().setGreen(colorMap.get(PHPitoConf.K_COLOR_GREEN));
			shellDialogSettings.getViewerColorCanvas().setBlue(colorMap.get(PHPitoConf.K_COLOR_BLUE));
			shellDialogSettings.getViewerColorCanvas().redraw();

			HashMap<String, Scale> colorsScaleMap = shellDialogSettings.getColorScaleMap();
			for (String key : colorsScaleMap.keySet()) {
				colorsScaleMap.get(key).setSelection(colorMap.get(key));
			}
			
			shellDialogSettings.getHexColorLbl().setText(shellDialogSettings.getHexColors());
		} catch (FormatException e) {
			UtilsViewAS.getInstance().lunchMBError(shellDialogSettings, e, PHPitoManager.NAME);
		}
	}
}
