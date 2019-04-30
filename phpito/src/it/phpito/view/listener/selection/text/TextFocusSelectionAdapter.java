package it.phpito.view.listener.selection.text;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class TextFocusSelectionAdapter extends SelectionAdapter {
	private Text text;

	public TextFocusSelectionAdapter(Text text) {
		super();
		this.text = text;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		text.forceFocus();
	}
}
