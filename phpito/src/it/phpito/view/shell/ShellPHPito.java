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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
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

import it.as.utils.core.LoggerAS;
import it.as.utils.core.UtilsAS;
import it.as.utils.view.UtilsViewAS;
import it.as.utils.view.listener.selection.LuncherFileExplorerSelectionAdapter;
import it.phpito.controller.PHPitoConf;
import it.phpito.controller.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;
import it.phpito.exception.ServerException;
import it.phpito.view.listener.selection.DeleteProjectSelectionAdapter;
import it.phpito.view.listener.selection.DrawerCpuUsagePaintListener;
import it.phpito.view.listener.selection.LuncherAboutSelctionAdapter;
import it.phpito.view.listener.selection.LuncherAddProjectSelectionAdapter;
import it.phpito.view.listener.selection.LuncherModifyProjectSelectionAdapter;
import it.phpito.view.listener.selection.LuncherSettingSelctionAdapter;
import it.phpito.view.listener.selection.StartServerSelectionAdapter;
import it.phpito.view.listener.selection.StopServerSelectionAdapter;
import it.phpito.view.listener.selection.TableSelectionAdapter;
import it.phpito.view.thread.EnableStartStopThread;
import it.phpito.view.thread.UsageCpuThread;
import it.phpito.view.thread.WriterTerminalThread;
import swing2swt.layout.BorderLayout;

public class ShellPHPito extends Shell {
	private ShellPHPito shellPHPito;
	private Table table;
	private final int fontHeight = 20;
	private StyledText logOutText;
	private Canvas canvas;
	private Label lblCPU;
	private Long idProjectSelect;
	private boolean actvtLogMon;
	private boolean actvtSysInfo;
	private ArrayList<MenuItem> mntmStartList = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> mntmStopList = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> mntmProjectList = new ArrayList<MenuItem>();
	private ArrayList<Button> bttnStartList = new ArrayList<Button>();
	private ArrayList<Button> bttnStopList = new ArrayList<Button>();
	private ArrayList<Button> bttnProjectList = new ArrayList<Button>();

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
				} catch (DOMException | IOException | ServerException | ProjectException e) {
					UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
				}
			}
		});
	}

	@Override
	/* override del metodo check - per evitare il controllo della subclass */
	protected void checkSubclass() {
	}

	/* get e set */
	public int getFontHeight() {
		return fontHeight;
	}
	public StyledText getLogOutText() {
		return logOutText;
	}
	public Long getIdProjectSelect() {
		return idProjectSelect;
	}
	public String getIdProjectSelectString() {
		return String.valueOf(idProjectSelect);
	}
	public Project getProjectSelect() {
		return PHPitoManager.getInstance().getProjectById(getIdProjectSelect());
	}
	public Table getTable() {
		return table;
	}
	public ArrayList<MenuItem> getMntmStartList() {
		return mntmStartList;
	}
	public ArrayList<MenuItem> getMntmStopList() {
		return mntmStopList;
	}
	public ArrayList<MenuItem> getMntmProjectList() {
		return mntmProjectList;
	}
	public ArrayList<Button> getBttnStartList() {
		return bttnStartList;
	}
	public ArrayList<Button> getBttnStopList() {
		return bttnStopList;
	}
	public ArrayList<Button> getBttnProjectList() {
		return bttnProjectList;
	}
	public Canvas getCanvas() {
		return canvas;
	}
	public Label getLblCPU() {
		return lblCPU;
	}

	/* metodo per creare contenuti */
	public void createContents() throws DOMException {
		this.setMinimumSize(300, 640);
		this.setSize(850, 640);
		this.setText("PHPito");
		this.setLayout(new BorderLayout(0, 0));
		UtilsViewAS.getInstance().centerWindow(this);

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
		
		String[] menuProjectList = {"Aggiungi", "Modifica", "Elimina", "Start", "Stop"};
		SelectionAdapter[] menuProjectSelAdptList = new SelectionAdapter[] {
				new LuncherAddProjectSelectionAdapter(this),
				new LuncherModifyProjectSelectionAdapter(this),
				new DeleteProjectSelectionAdapter(this),
				new StartServerSelectionAdapter(this),
				new StopServerSelectionAdapter(this)
		};
		
		for (int i = 0; i < menuProjectList.length; i++) {
			mntm = new MenuItem(mn, SWT.NONE);
			mntm.addSelectionListener(menuProjectSelAdptList[i]);
			mntm.setText(menuProjectList[i]);
			if (menuProjectList[i].equals("Modifica"))
				mntmProjectList.add(mntm);
			else if (menuProjectList[i].equals("Elimina"))
				mntmProjectList.add(mntm);
			else if (menuProjectList[i].equals("Start"))
				mntmStartList.add(mntm);
			else if (menuProjectList[i].equals("Stop"))
				mntmStopList.add(mntm);
		}
		
		/* menu' casca PHPito */
		mntm = new MenuItem(menu, SWT.CASCADE);
		mntm.setText("PHPito");
		mn = new Menu(mntm);
		mntm.setMenu(mn);

		String[] menuPHPitoList = {"Impostazioni", "Apri cartella Log", "About"};
		SelectionAdapter[] menuPHPitoSelAdptList = {
				new LuncherSettingSelctionAdapter(this),
				new LuncherFileExplorerSelectionAdapter(this, LoggerAS.getInstance().getPathDirLog("server"), PHPitoManager.NAME),
				new LuncherAboutSelctionAdapter(this)
		};
		
		for (int i = 0; i < menuPHPitoList.length; i++) {
			mntm = new MenuItem(mn, SWT.NONE);
			mntm.addSelectionListener(menuPHPitoSelAdptList[i]);
			mntm.setText(menuPHPitoList[i]);
		}

		GridData gd;
		actvtLogMon = PHPitoConf.getInstance().getActvtLogMonConf();
//		try {
//		} catch (FileException e) {
//			UtilsViewAS.getInstance().lunchMBError(this, e, PHPitoManager.NAME);
//		}
		if (actvtLogMon) {
			/* contenitore per la zona alta */
			Composite topComposite = new Composite(this, SWT.NONE);
			topComposite.setLayoutData(BorderLayout.NORTH);
			topComposite.setLayout(new GridLayout(1,  false));
			topComposite.setCursor(new Cursor(getDisplay(), SWT.CURSOR_CROSS));
			
			logOutText = new StyledText(topComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
			gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 210;
			logOutText.setLayoutData(gd);
			logOutText.getFont().getFontData()[0].setHeight(fontHeight);
//			logOutText.setForeground(new Color(getDisplay(), 20, 207, 20));
//			logOutText.setBackground(new Color(getDisplay(), 0, 0, 0));
			logOutText.setForeground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
			logOutText.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			logOutText.setEnabled(false);
			
//			Menu ppmnLOT = new Menu(logOutText);
//			logOutText.setMenu(ppmnLOT);
			
//			mntm = new MenuItem(ppmnLOT, SWT.NONE);
//			mntm.addSelectionListener(new LuncherFileExplorerSelectionAdapter(this, LoggerAS.getInstance().getPathDirLog("server"), PHPitoManager.NAME));
//			mntm.setText("Apri Log");
		}

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
		
		Button bttn;
		for (int i = 0; i < menuProjectList.length; i++) {
			bttn = new Button(rightComposite, SWT.PUSH);
			bttn.addSelectionListener(menuProjectSelAdptList[i]);
			bttn.setLayoutData(gdBttnWidth);
			bttn.setText(menuProjectList[i]);
			bttn.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
			new Label(rightComposite, SWT.NONE).setLayoutData(gdLblHeight);
			if (menuProjectList[i].equals("Modifica"))
				bttnProjectList.add(bttn);
			else if (menuProjectList[i].equals("Elimina"))
				bttnProjectList.add(bttn);
			else if (menuProjectList[i].equals("Start"))
				bttnStartList.add(bttn);
			else if (menuProjectList[i].equals("Stop"))
				bttnStopList.add(bttn);
		}
		
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
//		table.addKeyListener(new TableKeyAdpter(thiSs));
		table.forceFocus();
		
		/* poupup menu per tabella */
		Menu ppmnTbl = new Menu(table);
		table.setMenu(ppmnTbl);
		
		for (int i = 0; i < menuProjectList.length; i++) {
			mntm = new MenuItem(ppmnTbl, SWT.NONE);
			mntm.addSelectionListener(menuProjectSelAdptList[i]);
			mntm.setText(menuProjectList[i]);
			if (menuProjectList[i].equals("Modifica"))
				mntmProjectList.add(mntm);
			else if (menuProjectList[i].equals("Elimina"))
				mntmProjectList.add(mntm);
			else if (menuProjectList[i].equals("Start"))
				mntmStartList.add(mntm);
			else if (menuProjectList[i].equals("Stop"))
				mntmStopList.add(mntm);
		}
		
		/* nomi delle colonne per la tabella*/
		String[] titles = { "id", "Nome", "Indirizzo", "Path", "Stato"};
		int[] width = {40, 100, 130, 260, 100};
		int[] style = {SWT.CENTER, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.CENTER};
		
		table.setHeaderVisible(true);
		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn tblclmn = new TableViewerColumn(tableViewer, style[i]);
			tblclmn.getColumn().setText(titles[i]);
			tblclmn.getColumn().setWidth(width[i]);
			tblclmn.getColumn().setResizable(true);
			tblclmn.getColumn().setMoveable(true);
		}

		actvtSysInfo = PHPitoConf.getInstance().getActvtSysInfoConf();
//		try {
//		} catch (FileException e) {
//			UtilsViewAS.getInstance().lunchMBError(this, e, PHPitoManager.NAME);
//		}

		if (actvtSysInfo) {
			Composite compositeBottom = new Composite(shellPHPito, SWT.NONE);
			compositeBottom.setLayoutData(BorderLayout.SOUTH);

			canvas = new Canvas(compositeBottom, SWT.BORDER);
			canvas.setBounds(20, 20, 80, 60);
			canvas.addPaintListener(new DrawerCpuUsagePaintListener(PHPitoManager.getInstance().getCpuUsageQueue()));
			canvas.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			
			new Label(compositeBottom, SWT.NONE).setBounds(0, 100, 300, 0);
			

			String[] nameLblList = {
					"OS: " + UtilsAS.getInstance().getOsName(),
					"Arch: " + UtilsAS.getInstance().getOsArch(),
					"User: " + UtilsAS.getInstance().getOsUser()
			};
			UtilsViewAS.getInstance().printLabelVertical(nameLblList, 110, 8, 200, fontHeight, 0, compositeBottom, SWT.NONE);
			
			lblCPU = new Label(compositeBottom, SWT.NONE);
			lblCPU.setBounds(110, 68, 200, fontHeight);
			lblCPU.setText("CPU: ");
		}
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
			try {
				if (p.getServer().isRunnig()) {
					ti.setBackground(new Color(getDisplay(), new RGB(20, 207, 20)));
					ti.setForeground(new Color(getDisplay(), new RGB(0, 0, 0)));
				}
			} catch (IOException e) {
				UtilsViewAS.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
			}
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
		autoSetIdProjectSelect();
	}

	@Override
	public void open() {
		super.open();
		flushTable();
		if (actvtLogMon)
			new WriterTerminalThread(this, PHPitoManager.getInstance().getReentrantLockLogServer()).start();
		new EnableStartStopThread(this).start();
		if (actvtSysInfo)
			new UsageCpuThread(this, PHPitoManager.getInstance().getCpuUsageQueue()).start();
	}

	public void autoSetIdProjectSelect() {
		Long id = null;
		if (table.getSelectionIndex() >= 0)
			id = Long.parseLong(table.getItem(table.getSelectionIndex()).getText(0));
		idProjectSelect = id;
	}
}

