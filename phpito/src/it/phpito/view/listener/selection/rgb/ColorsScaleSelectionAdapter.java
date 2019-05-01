package it.phpito.view.listener.selection.rgb;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.as.utils.exception.FormatException;
import it.phpito.controller.PHPitoConf;
import it.phpito.view.shell.ShellDialogSettings;

public class ColorsScaleSelectionAdapter extends SelectionAdapter {
	private ShellDialogSettings shellDialog;

	public ColorsScaleSelectionAdapter(ShellDialogSettings shellDialog) {
		this.shellDialog = shellDialog;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		HashMap<String, Integer> colorsRGBMap = shellDialog.getColorsRGBMap(); 
		try {
			if (shellDialog.getElementLogList().getSelectionIndex() == 0)
				shellDialog.setColorBackgrndLogMonMap(colorsRGBMap);
			else if (shellDialog.getElementLogList().getSelectionIndex() == 1)
				shellDialog.setColorForegrndLogMonMap(colorsRGBMap);
			
			shellDialog.getViewerColorCanvas().setRed(colorsRGBMap.get(PHPitoConf.K_COLOR_RED));
			shellDialog.getViewerColorCanvas().setGreen(colorsRGBMap.get(PHPitoConf.K_COLOR_GREEN));
			shellDialog.getViewerColorCanvas().setBlue(colorsRGBMap.get(PHPitoConf.K_COLOR_BLUE));
			shellDialog.getViewerColorCanvas().redraw();
		} catch (FormatException e) {
		}
	}
}
