package phpito.view.listener.selection.text;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

/**
 * Class SelectionAdpter for focus on text
 * @author Andrea Serra
 *
 */
public class TextFocusSelectionAdapter extends SelectionAdapter {
	private Text text;

	/* CONTRUCT */
	public TextFocusSelectionAdapter(Text text) {
		super();
		this.text = text;
	}

	/* click event */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		text.forceFocus();
	}
}
