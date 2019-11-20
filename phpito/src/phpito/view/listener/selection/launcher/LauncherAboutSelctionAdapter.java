package phpito.view.listener.selection.launcher;

import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Label;

import jaswt.core.Jaswt;
import phpito.core.PHPitoManager;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPito;

/**
 * Class SelectionListener to launch the PHPito info window
 * @author Andrea Serra
 *
 */
public class LauncherAboutSelctionAdapter implements SelectionListener {
	private ShellPHPito shellPHPito;

	/* CONSTRUCT */
	public LauncherAboutSelctionAdapter(ShellPHPito shellPHPito) {
		this.shellPHPito = shellPHPito;
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent e) {
		launchSettingPHPito();
	}

	/* metodo per lanciare finestra che aggiunge il progetto */
	public void launchSettingPHPito() {
		ShellDialogPHPito shellDialogPHPito = new ShellDialogPHPito(shellPHPito);
		shellDialogPHPito.setSize(400, 360);
		shellDialogPHPito.setText("About PHPito");
		Jaswt.getInstance().centerWindow(shellDialogPHPito);

		String path = Paths.get("img", "logo-phpito.png").toString();
		Label lblLogo = new Label(shellDialogPHPito, SWT.WRAP);
		lblLogo.setImage(new Image(shellPHPito.getDisplay(), new ImageData(path)));
		lblLogo.setBounds(0, -10, 400, 200);
		
		String[] txtLblList = {
			PHPitoManager.NAME,
			PHPitoManager.INFO,
			"Version: " + PHPitoManager.VERSION,
			"Developed by: " + PHPitoManager.AUTHOR
		};
		
		Jaswt.getInstance().printLabelVertical(txtLblList, 10, 190, 380, shellPHPito.getFontHeight(), 5, shellDialogPHPito, SWT.CENTER);
		
//		Link link = new Link(shellDialog, SWT.CENTER);
//		link.getFont().getFontData()[0].setHeight(shellPHPito.getFontHeight());
//		link.setBounds(160, 290, 80, shellPHPito.getFontHeight() + 6);
//		link.setText("<a href=\"" + PHPitoManager.LINK_GITHUB + "\">Link GitHub</a>");
//		link.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent se) {
//				try {
//					if (!UtilsAS.getInstance().openBorwser(se.text))
//							Jaswt.getInstance().launchMB(shellPHPito, SWT.OK, "FAIL!!!", "Unable to open browser!!!");
//				} catch (IOException | URISyntaxException e) {
//					Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.NAME);
//				}
//			}
//		});
		
		shellDialogPHPito.open();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}
}
