package it.phpito.view.shell;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import it.as.utils.exception.FileException;
import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ServerException;
import it.phpito.view.listener.selection.AddProjectSelectionAdapter;
import it.phpito.view.listener.selection.StartServerSelectionAdapter;
import it.phpito.view.listener.selection.StopServerSelectionAdapter;
import swing2swt.layout.BorderLayout;
import swing2swt.layout.BoxLayout;

public class ShellPHPito extends Shell {
	private ShellPHPito shellPHPito;

	public ShellPHPito(Display display) {
		super(display);
		this.shellPHPito = this;
		this.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					ArrayList<Server> serverList = PHPitoManager.getInstance().getRunningServers();
					if (!serverList.isEmpty()) {
						String msg = "Attenzione!!! I server in esecuzione verranno fermati.\n"
										+ "Confermi l'operazione???";
						int resp = UtilsViewAS.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "ATTENZIONE!!!", msg);
						event.doit = resp == SWT.YES;
						if (event.doit)
							for (Server server : serverList)
								PHPitoManager.getInstance().stopServer(server.getProject());
					}
				} catch (FileException | IOException | ServerException e) {
					UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
				}
			}
		});
	}

	/* override del metodo check - per evitare il controllo della subclass */
	@Override
	protected void checkSubclass() {
	}

	/* metodo per creare contenuti */
	public void createContents() {
		this.setMinimumSize(200, 300);
		this.setSize(850, 600);
		UtilsViewAS.getInstance().centerWindow(this);
		this.setText("PHPito");
		this.setLayout(new BorderLayout(0, 0));

		/* nomi delle colonne per la tabella*/
		String[] titles = { "id", "Nome", "Indirizzo", "Path", "Stato"};
		int[] width = {40, 100, 130, 250, 100};
		int[] style = {SWT.CENTER, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.CENTER};
		
		/* barra menu' della testata */
		Menu menu = new Menu(this, SWT.BAR);
		this.setMenuBar(menu);
		
		MenuItem mntm;
		Menu mn;
		
		/* menu' cascata progetti */
		mntm = new MenuItem(menu, SWT.CASCADE);
		mntm.setText("Progetto");
		mn = new Menu(mntm);
		mntm.setMenu(mn);
		
		/* pulsante menu' per aggiungere un nuovo progetto */
		mntm = new MenuItem(mn, SWT.NONE);
		mntm.addSelectionListener(new AddProjectSelectionAdapter(this));
		mntm.setText("Aggiungi");
		
		/* pulsante menu' per uscire */
		mntm = new MenuItem(mn, SWT.NONE);
		mntm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shellPHPito.dispose();
			}
		});
		mntm.setText("Esci");
		
		Button btn;
		
		/* contenitore per la zona alta */
		Composite compositeTop = new Composite(this, SWT.NONE);
		compositeTop.setLayoutData(BorderLayout.NORTH);
		
		/* label per il logo */
		String pathLogo = Paths.get("img", "logo.png").toString();
		compositeTop.setLayout(new BoxLayout(BoxLayout.X_AXIS));
		Label lblLogo = new Label(compositeTop, SWT.WRAP);
		lblLogo.setImage(new Image(this.getDisplay(), new ImageData(pathLogo)));
		lblLogo.setLayoutData(BoxLayout.X_AXIS);
		
		/* contenitore per la zona laterale destra */
		Composite compositeRight = new Composite(this, SWT.NONE);
		compositeRight.setLayoutData(BorderLayout.WEST);
		compositeRight.setLayout(new GridLayout(1, false));
		
		/* impostazioni Grid Layout per larghezza pulsanti */
		GridData gdBttnWidth = new GridData();
		gdBttnWidth.widthHint = 150;
		
		/* impostazioni Grid Layout per altezza label(spazio) tra pulsanti */
		GridData gdLblHeight = new GridData();
		gdLblHeight.heightHint = 13;
		
		/* pulsante aggiungi progetto */
		btn = new Button(compositeRight, SWT.CENTER);
		btn.addSelectionListener(new AddProjectSelectionAdapter(this));
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Aggiungi");
		new Label(compositeRight, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* pulsante per eliminare progetto */
		btn = new Button(compositeRight, SWT.CENTER);
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Elimina");
		new Label(compositeRight, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* pulsante avvia server */
		btn = new Button(compositeRight, SWT.CENTER);
		btn.addSelectionListener(new StartServerSelectionAdapter(this, 1L));
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Start");
		new Label(compositeRight, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* pulsante per fermare server */
		btn = new Button(compositeRight, SWT.CENTER);
		btn.addSelectionListener(new StopServerSelectionAdapter(this, 1L));
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Stop");
		new Label(compositeRight, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* composite con scroll verticale */
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(BorderLayout.CENTER);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		/* tabella */
		TableViewer tableViewer = new TableViewer(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		/* poupup menu per tabella */
		Menu ppmnTbl = new Menu(table);
		table.setMenu(ppmnTbl);

		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new AddProjectSelectionAdapter(this));
		mntm.setText("Aggiungi");

		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		mntm.setText("Elimina");
		
		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new StartServerSelectionAdapter(this, 1L));
		mntm.setText("Start");

		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new StopServerSelectionAdapter(this, 1L));
		mntm.setText("Stop");
		
		table.setHeaderVisible(true);
		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn tblclmn = new TableViewerColumn(tableViewer, style[i]);
			tblclmn.getColumn().setText(titles[i]);
			tblclmn.getColumn().setWidth(width[i]);
			tblclmn.getColumn().setResizable(true);
			tblclmn.getColumn().setMoveable(true);
		}
		
		try {
			HashMap<String, Project> mapProjects = PHPitoManager.getInstance().getProjectsMap();
			printTable(table, mapProjects);
		} catch (FileException e) {
			UtilsViewAS.getInstance().lunchMBError(this, e, PHPitoManager.NAME);
		}
		
	}
	
	public void printTable(Table table, HashMap<String, Project> mapProjects) {
		TableItem ti;
		Project p;
		for (String id : mapProjects.keySet()) {
			p = mapProjects.get(id);
			ti = new TableItem(table, SWT.NONE);
			ti.setText(0, id);
			ti.setText(1, p.getName());
			ti.setText(2, p.getServer().getAddressAndPort());
			ti.setText(3, p.getServer().getPath());
			ti.setText(4, p.getServer().getStatePIDString());
		}
	}

}

