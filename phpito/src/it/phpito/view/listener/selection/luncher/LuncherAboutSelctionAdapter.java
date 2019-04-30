package it.phpito.view.listener.selection.luncher;

import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Label;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.view.shell.ShellDialogPHPito;
import it.phpito.view.shell.ShellPHPito;

public class LuncherAboutSelctionAdapter extends SelectionAdapter {
	private ShellPHPito shellPHPito;

	public LuncherAboutSelctionAdapter(ShellPHPito shellPHPito) {
		super();
		this.shellPHPito = shellPHPito;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		ShellDialogPHPito shellDialog = new ShellDialogPHPito(shellPHPito);
		lunchSettingPHPito(shellDialog);
	}

	/* metodo per lanciare finestra che aggiunge il progetto */
	public void lunchSettingPHPito(ShellDialogPHPito shellDialog) {
		shellDialog.setSize(400, 360);
		shellDialog.setText("About PHPito");
		UtilsViewAS.getInstance().centerWindow(shellDialog);

		String path = Paths.get("img", "logo-phpito.png").toString();
		Label lblLogo = new Label(shellDialog, SWT.WRAP);
		lblLogo.setImage(new Image(shellPHPito.getDisplay(), new ImageData(path)));
		lblLogo.setBounds(0, -10, 400, 200);
		
		String[] txtLblList = {
				PHPitoManager.NAME,
				PHPitoManager.INFO,
				"Version: " + PHPitoManager.VERSION,
				"Developed by: " + PHPitoManager.AUTHOR
		};
		
		UtilsViewAS.getInstance().printLabelVertical(txtLblList, 10, 190, 380, shellPHPito.getFontHeight(), 5, shellDialog, SWT.CENTER);
		
//		Link link = new Link(shellDialog, SWT.CENTER);
//		link.getFont().getFontData()[0].setHeight(shellPHPito.getFontHeight());
//		link.setBounds(160, 290, 80, shellPHPito.getFontHeight() + 6);
//		link.setText("<a href=\"" + PHPitoManager.LINK_GITHUB + "\">Link GitHub</a>");
//		link.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent se) {
//				try {
//					if (!UtilsAS.getInstance().openBorwser(se.text))
//							UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.OK, "FAIL!!!", "Impossibile aprire il browser!!!");
//				} catch (IOException | URISyntaxException e) {
//					UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
//				}
//			}
//		});
		
		shellDialog.open();
	}
}
