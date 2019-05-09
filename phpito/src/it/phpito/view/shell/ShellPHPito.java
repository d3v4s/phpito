package it.phpito.view.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;
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

import it.jaswt.core.Jaswt;
import it.jaswt.core.canvas.CPUMonitorCanvas;
import it.jaswt.core.listener.selection.LuncherFileExplorerSelectionAdapter;
import it.jogger.core.Jogger;
import it.jogger.exception.FileLogException;
import it.jogger.exception.LockLogException;
import it.phpito.core.PHPitoConf;
import it.phpito.core.PHPitoManager;
import it.phpito.data.Project;
import it.phpito.data.Server;
import it.phpito.exception.ProjectException;
import it.phpito.exception.ServerException;
import it.phpito.view.listener.key.StartStopServerKeyAdapter;
import it.phpito.view.listener.selection.FlushTableSelectionAdapter;
import it.phpito.view.listener.selection.TableSelectionAdapter;
import it.phpito.view.listener.selection.launcher.LauncherAboutSelctionAdapter;
import it.phpito.view.listener.selection.launcher.LauncherAddProjectSelectionAdapter;
import it.phpito.view.listener.selection.launcher.LauncherModifyProjectSelectionAdapter;
import it.phpito.view.listener.selection.launcher.LauncherSettingSelctionAdapter;
import it.phpito.view.listener.selection.project.DeleteProjectSelectionAdapter;
import it.phpito.view.listener.selection.server.StartServerSelectionAdapter;
import it.phpito.view.listener.selection.server.StopServerSelectionAdapter;
import it.phpito.view.thread.UsageCpuThread;
import it.phpito.view.thread.WriterLogMonitorThread;
import swing2swt.layout.BorderLayout;

public class ShellPHPito extends Shell {
	private ShellPHPito shellPHPito;
	private Table table;
	private final int fontHeight = 20;
	private StyledText logOutText;
	private CPUMonitorCanvas cpuMonitorCanvas;
	private CLabel lblInfo;
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
					if (PHPitoManager.getInstance().isDebug())
						PHPitoManager.getInstance().getJoggerDebug().writeLog("Listener closing ShellPHPito");
					ArrayList<Server> serverList = PHPitoManager.getInstance().getRunningServers();
					if (!serverList.isEmpty()) {
						String msg = "Attenzione!!! I server in esecuzione verranno fermati.\n"
										+ "Confermi l'operazione???";
						int resp = Jaswt.getInstance().lunchMB(shellPHPito, SWT.YES | SWT.NO, "ATTENZIONE!!!", msg);
						event.doit = resp == SWT.YES;
						if (event.doit)
							for (Server server : serverList)
								PHPitoManager.getInstance().stopServer(server.getProject());
					}
				} catch (DOMException | IOException | ServerException | ProjectException | FileLogException | LockLogException e) {
					Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
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
	public CPUMonitorCanvas getCPUMonitorCanvas() {
		return cpuMonitorCanvas;
	}
	public CLabel getLblCPU() {
		return lblInfo;
	}

	/* metodo per creare contenuti */
	public void createContents() throws DOMException {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("Create content ShellPHPito");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		this.setMinimumSize(300, 400);
		this.setSize(850, 640);
		this.setText("PHPito");
		this.setLayout(new BorderLayout(0, 0));
		Jaswt.getInstance().centerWindow(this);

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
		
		String[] menuProjectList = {"Aggiungi", "Modifica", "Elimina", "Start", "Stop", "Aggiorna"};
		SelectionAdapter[] menuProjectSelAdptList = new SelectionAdapter[] {
				new LauncherAddProjectSelectionAdapter(this),
				new LauncherModifyProjectSelectionAdapter(this),
				new DeleteProjectSelectionAdapter(this),
				new StartServerSelectionAdapter(this),
				new StopServerSelectionAdapter(this),
				new FlushTableSelectionAdapter(this)
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
				new LauncherSettingSelctionAdapter(this),
				new LuncherFileExplorerSelectionAdapter(this, Jogger.getLogDirPath("server"), PHPitoManager.NAME),
				new LauncherAboutSelctionAdapter(this)
		};

		for (int i = 0; i < menuPHPitoList.length; i++) {
			mntm = new MenuItem(mn, SWT.NONE);
			mntm.addSelectionListener(menuPHPitoSelAdptList[i]);
			mntm.setText(menuPHPitoList[i]);
		}

		GridData gd;
		actvtLogMon = PHPitoConf.getInstance().getActvtLogMonConf();
		if (actvtLogMon) {
			if (PHPitoManager.getInstance().isDebug())
				try {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Create Content ShellPHPito - Log Monitor ON");
				} catch (FileLogException | LockLogException e) {
					e.printStackTrace();
				}
			/* contenitore per la zona alta */
			Composite topComposite = new Composite(this, SWT.NONE);
			topComposite.setLayoutData(BorderLayout.NORTH);
			topComposite.setLayout(new GridLayout(1,  false));
			
			logOutText = new StyledText(topComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 150;
			logOutText.setLayoutData(gd);
			logOutText.getFont().getFontData()[0].setHeight(fontHeight);
			logOutText.setForeground(PHPitoConf.getInstance().getColorForegrndLogMonConf());
			logOutText.setBackground(PHPitoConf.getInstance().getColorBckgrndLogMonConf());
			logOutText.setEditable(false);
			logOutText.setCursor(new Cursor(getDisplay(), SWT.CURSOR_CROSS));

		}

		/* contenitore per la zona laterale destra */
		ScrolledComposite rightScrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		rightScrolledComposite.setLayoutData(BorderLayout.WEST);
		rightScrolledComposite.setExpandHorizontal(true);
		rightScrolledComposite.setExpandVertical(true);
		
		Composite rightGridComposite = new Composite(rightScrolledComposite, SWT.NONE);
		rightGridComposite.setLayout(new GridLayout(1, false));

		/* impostazioni Grid Layout per larghezza pulsanti */
		GridData gdBttnWidth = new GridData();
		gdBttnWidth.widthHint = 150;

		/* impostazioni Grid Layout per altezza label(spazio) tra pulsanti */
		GridData gdLblHeight = new GridData();
		gdLblHeight.heightHint = 13;

		Button bttn;
		for (int i = 0; i < menuProjectList.length - 1; i++) {
			bttn = new Button(rightGridComposite, SWT.PUSH);
			bttn.addSelectionListener(menuProjectSelAdptList[i]);
			bttn.setLayoutData(gdBttnWidth);
			bttn.setText(menuProjectList[i]);
			bttn.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
			new Label(rightGridComposite, SWT.NONE).setLayoutData(gdLblHeight);
			if (menuProjectList[i].equals("Modifica"))
				bttnProjectList.add(bttn);
			else if (menuProjectList[i].equals("Elimina"))
				bttnProjectList.add(bttn);
			else if (menuProjectList[i].equals("Start"))
				bttnStartList.add(bttn);
			else if (menuProjectList[i].equals("Stop"))
				bttnStopList.add(bttn);
		}

		rightScrolledComposite.setContent(rightGridComposite);
		rightScrolledComposite.setMinSize(rightGridComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
		table.addKeyListener(new StartStopServerKeyAdapter(this));
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

		if (actvtSysInfo) {
			if (PHPitoManager.getInstance().isDebug())
				try {
					PHPitoManager.getInstance().getJoggerDebug().writeLog("Create Content ShellPHPito - Sys Info ON");
				} catch (FileLogException | LockLogException e) {
					e.printStackTrace();
				}
			Composite compositeBottom = new Composite(shellPHPito, SWT.NONE);
			compositeBottom.setLayoutData(BorderLayout.SOUTH);

			int xLabel = 25;
			if (PHPitoConf.getInstance().getActvtCPUMon()) {
				if (PHPitoManager.getInstance().isDebug())
					try {
						PHPitoManager.getInstance().getJoggerDebug().writeLog("Create Content ShellPHPito - CPU Monitor ON");
					} catch (FileLogException | LockLogException e) {
						e.printStackTrace();
					}
				cpuMonitorCanvas = new CPUMonitorCanvas(compositeBottom, SWT.NONE, new ArrayBlockingQueue<Double>(80));
				cpuMonitorCanvas.setBounds(20, 20, 80, 60);
				cpuMonitorCanvas.setStyleCPUMon(PHPitoConf.getInstance().getStyleLogMonConf());
				xLabel = 110;
			}

			if (PHPitoConf.getInstance().getOthInfo()) {
				if (PHPitoManager.getInstance().isDebug())
					try {
						PHPitoManager.getInstance().getJoggerDebug().writeLog("Create Content ShellPHPito - Other Info ON");
					} catch (FileLogException | LockLogException e) {
						e.printStackTrace();
					}
				lblInfo = new CLabel(compositeBottom, SWT.NONE);
				lblInfo.setBounds(xLabel, 15, 200, 70);
				
				try {
					lblInfo.setText(PHPitoManager.getInstance().getSystemInfo(null));
				} catch (IOException e) {
					Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
				}
			}
			new Label(compositeBottom, SWT.NONE).setBounds(0, 100, 300, 0);
		}
	}

	/* metodo che riscrive tabella da hashmap di progetti */
	private void printProjectsOnTable(HashMap<String, Project> mapProjects) {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("ShellPHPito - Print Projects on Table");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
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
			if (p.getServer().isRunning()) {
				ti.setBackground(new Color(getDisplay(), new RGB(20, 255, 20)));
				ti.setForeground(new Color(getDisplay(), new RGB(0, 0, 0)));
			}
		}
	}

	/* metodo che riscrive la tabella recuperando i dati dall'xml */
	public void flushTable() {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("ShellPHPito - Flush Table");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		int indexTable = table.getSelectionIndex();
		HashMap<String, Project> mapProjects = PHPitoManager.getInstance().getReentrantLockXMLServer().getProjectsMap();
		printProjectsOnTable(mapProjects);
		if (indexTable >= table.getItems().length || indexTable < 0)
			indexTable = 0;
		table.setSelection(indexTable);
		autoSetIdProjectSelect();
		autoEnableButtonProject();
	}

	@Override
	public void open() {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("Open ShellPHPito");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		super.open();
		try {
			PHPitoManager.getInstance().flushRunningServers();
		} catch (NumberFormatException | IOException | ProjectException e) {
			Jaswt.getInstance().lunchMBError(shellPHPito, e, PHPitoManager.NAME);
		}
		flushTable();
		if (actvtLogMon)
			new WriterLogMonitorThread(this, PHPitoManager.getInstance().getReentrantLockLogServer()).start();
		if (actvtSysInfo)
			new UsageCpuThread(this).start();
	}

	/* metodo che abilita o disabilita i pulsanti legati a progetto */
	private void autoEnableButtonProject() {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("ShellPHPito - Enable Disabled Button of Project");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		Project project = shellPHPito.getProjectSelect();
		Boolean isRunnig = (project != null) ? project.getServer().isRunning() : null;
		if (isRunnig != null) {
			for (Button bttn : shellPHPito.getBttnProjectList())
				bttn.setEnabled(true);
			for (MenuItem mntm : shellPHPito.getMntmProjectList())
				mntm.setEnabled(true);
			for (Button bttn : shellPHPito.getBttnStartList())
				bttn.setEnabled(!isRunnig);
			for (MenuItem mntm : shellPHPito.getMntmStartList())
				mntm.setEnabled(!isRunnig);
			for (Button bttn : shellPHPito.getBttnStopList())
				bttn.setEnabled(isRunnig);
			for (MenuItem mntm : shellPHPito.getMntmStopList())
				mntm.setEnabled(isRunnig);
		} else {
			for (Button bttn : shellPHPito.getBttnProjectList())
				bttn.setEnabled(false);
			for (MenuItem mntm : shellPHPito.getMntmProjectList())
				mntm.setEnabled(false);
			for (Button bttn : shellPHPito.getBttnStartList())
				bttn.setEnabled(false);
			for (MenuItem mntm : shellPHPito.getMntmStartList())
				mntm.setEnabled(false);
			for (Button bttn : shellPHPito.getBttnStopList())
				bttn.setEnabled(false);
			for (MenuItem mntm : shellPHPito.getMntmStopList())
				mntm.setEnabled(false);
		}
	}

	/* metodo che setta la variabile id con id progetto selezionato */
	public void autoSetIdProjectSelect() {
		if (PHPitoManager.getInstance().isDebug())
			try {
				PHPitoManager.getInstance().getJoggerDebug().writeLog("ShellPHPito - Auto Set ID");
			} catch (FileLogException | LockLogException e) {
				e.printStackTrace();
			}
		Long id = null;
		if (table.getSelectionIndex() >= 0)
			id = Long.parseLong(table.getItem(table.getSelectionIndex()).getText(0));
		idProjectSelect = id;
		autoEnableButtonProject();
	}
}

