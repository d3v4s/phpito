package phpito.view.listener.selection.launcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import jaswt.utils.CreateContentsStyle;
import jaswt.utils.Jaswt;
import jutilas.utils.Jutilas;
import phpito.core.PHPitoManager;
import phpito.view.shell.ShellPHPito;
import phpito.view.shell.dialog.ShellDialogPHPitoAbstract;

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

	@Override
	public void widgetDefaultSelected(SelectionEvent evnt) {
	}

	/* event click */
	@Override
	public void widgetSelected(SelectionEvent evnt) {
		ShellDialogAbout shellDialogAbout = new ShellDialogAbout(shellPHPito);
		shellDialogAbout.open();
	}

	/* ################################################################################# */
	/* START INNER CLASS */
	/* ################################################################################# */

	/**
	 * Private inner class for about shell dialog
	 * @author Andrea Serra
	 *
	 */
	private class ShellDialogAbout extends ShellDialogPHPitoAbstract {

		public ShellDialogAbout(ShellPHPito shellPHPito) {
			super(shellPHPito);
		}

		@Override
		protected void createContents() {
			this.setSize(400, 360);
			this.setText("About PHPito");
			Jaswt.getInstance().centerWindow(this);

			String path = Paths.get("img", "logo-phpito.png").toString();
			Label lblLogo = new Label(this, SWT.WRAP);
			lblLogo.setImage(new Image(shellPHPito.getDisplay(), new ImageData(path)));
			lblLogo.setBounds(0, -10, 400, 200);
			
			String[] txtLblList = {
				PHPitoManager.NAME,
				PHPitoManager.INFO,
				"Version: " + PHPitoManager.VERSION,
				"Developed by: " + PHPitoManager.AUTHOR
			};
			
			Jaswt.getInstance().createLabels(txtLblList, 10, 190, 380, 20, 5, this, SWT.CENTER, CreateContentsStyle.VERTICAL);
			
			Link link = new Link(this, SWT.CENTER);
			link.getFont().getFontData()[0].setHeight(30);
			link.setBounds(160, 290, 80, 30);
			link.setText("<a href=\"" + PHPitoManager.LINK_GITHUB + "\">Link GitHub</a>");
			link.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent evnt) {
					try {
						if (!Jutilas.getInstance().openBrowser(evnt.text))
								Jaswt.getInstance().launchMB(shellPHPito, SWT.OK, "FAIL!!!", "Unable to open browser!!!");
					} catch (IOException | URISyntaxException e) {
						Jaswt.getInstance().launchMBError(shellPHPito, e, PHPitoManager.getInstance().getJoggerError());
					}
				}
			});
		}
	}

	/* ################################################################################# */
	/* END INNER CLASS */
	/* ################################################################################# */
}
