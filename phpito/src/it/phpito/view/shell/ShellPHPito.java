package it.phpito.view.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
import org.w3c.dom.DOMException;

import it.as.utils.view.UtilsViewAS;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ServerException;
import it.phpito.view.listener.key.TableKeyAdpter;
import it.phpito.view.listener.selection.DeleteProjectSelectionAdapter;
import it.phpito.view.listener.selection.LuncherAddProjectSelectionAdapter;
import it.phpito.view.listener.selection.StartServerSelectionAdapter;
import it.phpito.view.listener.selection.StopServerSelectionAdapter;
import it.phpito.view.listener.selection.TableSelectionAdapter;
import it.phpito.view.thread.WriterTerminalThread;
import swing2swt.layout.BorderLayout;

public class ShellPHPito extends Shell {
	private ShellPHPito shellPHPito;
	private Table table;
	private final int fontHeight = 20;
	private StyledText logOutText;
//	private Long idSelect = 0L;
//	private Long idProjectSelect;

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
				} catch (DOMException | IOException | ServerException e) {
					UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
				}
			}
		});
	}

	@Override
	/* override del metodo check - per evitare il controllo della subclass */
	protected void checkSubclass() {
	}

	public int getFontHeight() {
		return fontHeight;
	}
	public StyledText getLogOutText() {
		return logOutText;
	}
	public String getIdProjectSelectString() {
//		if (table.getSelection().length == 0)
//			return null;
//		return table.getSelection()[0].getText(0);
		return "1";
	}
	public Long getIdProjectSelect() {
		return Long.parseLong(getIdProjectSelectString());
	}
	
//	public void setIdSelect(Long idSelect) {
//		this.idSelect = idSelect;
//	}
	public Project getProjectSeclect() {
		return PHPitoManager.getInstance().getProjectById(getIdProjectSelect());
	}
	public Table getTable() {
		return table;
	}

	/* metodo per creare contenuti */
	public void createContents() throws DOMException {
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
		mntm.addSelectionListener(new LuncherAddProjectSelectionAdapter(this));
		mntm.setText("Aggiungi");
	
		Button btn;
		GridData gd;
		
		/* contenitore per la zona alta */
		Composite topComposite = new Composite(this, SWT.NONE);
		topComposite.setLayoutData(BorderLayout.NORTH);
		topComposite.setLayout(new GridLayout(1,  false));

		logOutText = new StyledText(topComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 210;
		logOutText.setLayoutData(gd);
		logOutText.getFont().getFontData()[0].setHeight(fontHeight);
		logOutText.setForeground(new Color(getDisplay(), 20, 207, 20));
		logOutText.setBackground(new Color(getDisplay(), 0, 0, 0));
		logOutText.setEnabled(false);
		
//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				LocalDateTime lastPrint = LocalDateTime.now().minusYears(50);
//				while (!isDisposed()) {
//					try {
//						if (lastPrint.isBefore(PHPitoManager.getInstance().getLocalDateTimeLastModifyLogServer(idSelect))) {
//							getDisplay().asyncExec(new Runnable() {
//								@Override
//								public void run() {
//									String out = PHPitoManager.getInstance().getReentrantLockLogServer().readLog(getProjectSeclect(), 10);
//									shellPHPito.getLogOutText().setText(out);
//								}
//							});
//							lastPrint = LocalDateTime.now();
//						}
//					} catch (DOMException | FileException e) {
//						e.printStackTrace();
//					}
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//					}
//				}
//			}
//		});
//		t.start();

		/* contenitore per la zona laterale destra */
		Composite rightComposite = new Composite(this, SWT.NONE);
		rightComposite.setLayoutData(BorderLayout.WEST);
		rightComposite.setLayout(new GridLayout(1, false));
		
		/* impostazioni Grid Layout per larghezza pulsanti */
		GridData gdBttnWidth = new GridData();
		gdBttnWidth.widthHint = 150;
		
		/* impostazioni Grid Layout per altezza label(spazio) tra pulsanti */
		GridData gdLblHeight = new GridData();
		gdLblHeight.heightHint = 13;
		
		/* pulsante aggiungi progetto */
		btn = new Button(rightComposite, SWT.CENTER);
		btn.addSelectionListener(new LuncherAddProjectSelectionAdapter(this));
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Aggiungi");
		new Label(rightComposite, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* pulsante per eliminare progetto */
		btn = new Button(rightComposite, SWT.CENTER);
		btn.addSelectionListener(new DeleteProjectSelectionAdapter(this));
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Elimina");
		new Label(rightComposite, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* pulsante avvia server */
		btn = new Button(rightComposite, SWT.CENTER);
		btn.addSelectionListener(new StartServerSelectionAdapter(this));
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Start");
		new Label(rightComposite, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* pulsante per fermare server */
		btn = new Button(rightComposite, SWT.CENTER);
		btn.addSelectionListener(new StopServerSelectionAdapter(this));
		btn.setLayoutData(gdBttnWidth);
		btn.setText("Stop");
		new Label(rightComposite, SWT.NONE).setLayoutData(gdLblHeight);
		
		/* composite centrale con scroll verticale */
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(BorderLayout.CENTER);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		/* tabella */
		TableViewer tableViewer = new TableViewer(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		table.addSelectionListener(new TableSelectionAdapter(this));
		table.addKeyListener(new TableKeyAdpter(this));
		table.forceFocus();
		
		/* poupup menu per tabella */
		Menu ppmnTbl = new Menu(table);
		table.setMenu(ppmnTbl);

		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new LuncherAddProjectSelectionAdapter(this));
		mntm.setText("Aggiungi");

		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		mntm.setText("Elimina");
		
		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new StartServerSelectionAdapter(this));
		mntm.setText("Start");

		mntm = new MenuItem(ppmnTbl, SWT.NONE);
		mntm.addSelectionListener(new StopServerSelectionAdapter(this));
		mntm.setText("Stop");
		
		table.setHeaderVisible(true);
		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn tblclmn = new TableViewerColumn(tableViewer, style[i]);
			tblclmn.getColumn().setText(titles[i]);
			tblclmn.getColumn().setWidth(width[i]);
			tblclmn.getColumn().setResizable(true);
			tblclmn.getColumn().setMoveable(true);
		}
//		flushTable();
	}

	/* metodo che riscrive tabella da hashmap di progetti */
	public void printProjectsOnTable(HashMap<String, Project> mapProjects) {
		TableItem ti;
		Project p;
		table.removeAll();
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

	/* metodo che riscrive la tabella recuperando i dati dall'xml */
	public void flushTable() {
		int indexTable = table.getSelectionIndex();
		HashMap<String, Project> mapProjects = PHPitoManager.getInstance().getReentrantLockXMLServer().getProjectsMap();
		printProjectsOnTable(mapProjects);
		if (indexTable >= table.getItems().length || indexTable < 0)
			indexTable = 0;
		table.setSelection(indexTable);
	}

	@Override
	public void open() {
		super.open();
		flushTable();
		new WriterTerminalThread(this).start();
	}
	
	
	
}

