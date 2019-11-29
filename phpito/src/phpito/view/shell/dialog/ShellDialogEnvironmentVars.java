package phpito.view.shell.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import jaswt.utils.CreateContentsStyle;
import jaswt.utils.Jaswt;
import phpito.view.listener.selection.text.TextFocusSelectionAdapter;

/**
 * Inner class shell for environment variables window
 * @author Andrea Serra
 *
 */
public class ShellDialogEnvironmentVars extends ShellDialogPHPitoAbstract {
	private ShellDialogProject shellDialogProject;
	private ShellDialogEnvironmentVars shellDialogEnvironmentVars;
	private HashMap<String, Text> textsMap;
	private ArrayList<Button> envVarsBtns;
	private Table table;

	/* CONSTRUCT */
	public ShellDialogEnvironmentVars(ShellDialogProject shellDialogProject) {
		super(shellDialogProject);
		this.shellDialogProject = shellDialogProject;
		this.shellDialogEnvironmentVars = this;
	}

	/* ############################################################################# */
	/* START OVERRIDE */
	/* ############################################################################# */

	@Override
	protected void createContents() {
		this.setSize(350, 500);
		this.setText("Environment Variables");
		Jaswt.getInstance().centerWindow(this);

		/* scroll for table */
		ScrolledComposite scrollTable = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		scrollTable.setBounds(10, 10, 325, 340);
		scrollTable.setExpandHorizontal(true);
		scrollTable.setExpandVertical(true);

		/* table with scroll for variables list */
		TableViewer tableViewer = new TableViewer(scrollTable, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		scrollTable.setContent(table);
		table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				autoEnableDisableButtonsEnvVars();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				ShellDialogAddModifyEnvVars shellAddModEnvVar = new ShellDialogAddModifyEnvVars(shellDialogEnvironmentVars, ShellDialogAddModifyEnvVars.MODIFY);
				shellAddModEnvVar.open();
			}
		});

		/* name for table columns */
		String[] titles = {"Key", "Value"};

		/* loop for table headers */
		TableViewerColumn tblclmn;
		for (int i = 0, length = titles.length; i < length; i++) {
			tblclmn = new TableViewerColumn(tableViewer, SWT.LEFT);
			tblclmn.getColumn().setText(titles[i]);
			tblclmn.getColumn().setWidth(160);
			tblclmn.getColumn().setResizable(true);
			tblclmn.getColumn().setMoveable(false);
		}

		envVarsBtns = new ArrayList<Button>();

		// TODO poupup menu
		/* table poupup menu */
		Menu ppmnTbl = new Menu(table);
		table.setMenu(ppmnTbl);

		/* buttons for add modify and delete the environment variables */
		String[] namesEnvVarBtn = {"Add", "Modify", "Delete"};
		SelectionListener[] listnrEnvVarBtn = {
			new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ShellDialogAddModifyEnvVars shellDialogAddAndModifyEnvVars = new ShellDialogAddModifyEnvVars(shellDialogEnvironmentVars, ShellDialogAddModifyEnvVars.ADD);
					shellDialogAddAndModifyEnvVars.open();
				}
			},
			new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ShellDialogAddModifyEnvVars shellDialogAddAndModifyEnvVars = new ShellDialogAddModifyEnvVars(shellDialogEnvironmentVars, ShellDialogAddModifyEnvVars.MODIFY);
					shellDialogAddAndModifyEnvVars.open();
				}
			},
			new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					table.remove(table.getSelectionIndex());
					table.forceFocus();
					autoEnableDisableButtonsEnvVars();
				}
			}
		};
		Button bttn;
		int ws = 90;
		for (int i = 0, length = namesEnvVarBtn.length; i < length; i++) {
			bttn = new Button(this, SWT.PUSH);
			bttn.setBounds(20 + ws * i, 360, 80, 30);
			if (listnrEnvVarBtn[i] != null) bttn.addSelectionListener(listnrEnvVarBtn[i]);
			if (namesEnvVarBtn[i] != null) bttn.setText(namesEnvVarBtn[i]);
			if (i != 0) envVarsBtns.add(bttn);
			bttn.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
		}

		/* cancel and save buttons */
		String[] namesBtn = {"Cancel", "Save"};
		SelectionListener[] slectLstnrList = {
			new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (getEnvVarsMapByTable().equals(shellDialogProject.getEnvVars())) shellDialogEnvironmentVars.dispose();
					else flushTable();
				}
			},
			new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					shellDialogProject.setEnvVars(getEnvVarsMapByTable());
					shellDialogEnvironmentVars.dispose();
				}
			}
		};
		Jaswt.getInstance().createButtons(namesBtn, 160, 430, 80, 30, 10, this, slectLstnrList, CreateContentsStyle.HORIZONTAL);

		flushTable();
	}

	/* ############################################################################# */
	/* END OVERRIDE */
	/* ############################################################################# */

	/* ############################################################################# */
	/* START PRIVATE METHODS */
	/* ############################################################################# */

	/* method that rewrite the environment vars on table */
	private void flushTable() {
		int indexTable = table.getSelectionIndex();
		table.removeAll();
		TableItem ti;
		HashMap<String, String> envVars = shellDialogProject.getEnvVars();
		Set<String> keySet = envVars.keySet();
		for (String key : keySet) {
			ti = new TableItem(table, SWT.NONE);
			ti.setText(0, key);
			ti.setText(1, envVars.get(key));
		}
		if (indexTable >= table.getItems().length || indexTable < 0) indexTable = 0;
		table.setSelection(indexTable);
		autoEnableDisableButtonsEnvVars();
	}

	/* method to enable or disable button for env var automatically */
	private void autoEnableDisableButtonsEnvVars() {
		boolean isSelectItem = table.getSelectionIndex() != -1;
		for (Button btn : envVarsBtns) btn.setEnabled(isSelectItem);
	}

	/* method that get environment variables map by table */
	private HashMap<String, String> getEnvVarsMapByTable() {
		HashMap<String, String> envVars = new HashMap<String, String>();
		TableItem[] itemList = table.getItems();
		for (TableItem item : itemList) envVars.put(item.getText(0), item.getText(1));
		return envVars;
	}

	/* ############################################################################# */
	/* END PRIVATE METHODS */
	/* ############################################################################# */

	/* ############################################################################# */
	/* START INNER CLASS */
	/* ############################################################################# */

	/**
	 * Inner class shell for add and modify a environment variable
	 * @author Andrea Serra
	 *
	 */
	private class ShellDialogAddModifyEnvVars extends ShellDialogPHPitoAbstract {
		final public static int ADD = 0;
		final public static int MODIFY = 1;
		final private String K_KEY = "key";
		final private String K_VAL = "val";
		private ShellDialogAddModifyEnvVars shellDialogAddAndModifyEnvVars;
		private TableItem tabItem;
		private int type;

		/* CONSTRUCT */
		public ShellDialogAddModifyEnvVars(ShellDialogEnvironmentVars shellDialogEnvironmentVars, int type) {
			super(shellDialogEnvironmentVars);
			this.type = type;
			this.shellDialogAddAndModifyEnvVars = this;
		}

		/* OVERRRIDE */
		@Override
		protected void createContents() {
			String title = "";
			String key = "";
			String val = "";
			switch (type) {
				case ADD:
					title = "New Environment Variable";
					tabItem = new TableItem(table, SWT.NONE);
					break;
				case MODIFY:
					title = "Modify Environment Variable";
					tabItem = table.getSelection()[0];
					key = tabItem.getText(0);
					val = tabItem.getText(1);
					break;
				default:
					break;
			}
			this.setSize(400, 200);
			this.setText(title);
			Jaswt.getInstance().centerWindow(this);

			/* print label */
			String[] namesLabel = {"Key:", "Val:"};
			Jaswt.getInstance().createLabels(namesLabel, 20, 25, 50, 30, 10, this, SWT.NONE, CreateContentsStyle.VERTICAL);

			/* print text */
			int[] widthList = {250, 250};
			String[] keysMap = {K_KEY, K_VAL};
			// = new HashMap<String, Text>();
//			Jaswt.getInstance().createTexts(80, 25, widthList, 30, 10, this, keysMap, textsMap, CreateContentsStyle.HORIZONTAL);
			textsMap = new HashMap<String, Text>(Jaswt.getInstance().createTexts(80, 25, widthList, 30, 10, this, keysMap, SWT.NONE, CreateContentsStyle.VERTICAL));
			textsMap.get(K_KEY).setText(key);
			textsMap.get(K_VAL).setText(val);

			/* class for add environment variable on table */
			SelectionAdapter saveSelctn = new SelectionAdapter() {
				/* OVERRRIDE */
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					addEnvVarOnTable();
				}
				@Override
				public void widgetSelected(SelectionEvent e) {
					addEnvVarOnTable();
				}

				/* method to add environment variable on table */
				private void addEnvVarOnTable() {
					tabItem.setText(0, textsMap.get(K_KEY).getText());
					tabItem.setText(1, textsMap.get(K_VAL).getText());
					shellDialogAddAndModifyEnvVars.dispose();
					table.forceFocus();
					autoEnableDisableButtonsEnvVars();
				}
			};

			/* listener for text */
			textsMap.get(K_KEY).addSelectionListener(new TextFocusSelectionAdapter(textsMap.get(K_VAL)));
			textsMap.get(K_VAL).addSelectionListener(saveSelctn);

			/* buttons cancel and save */
			String[] namesBtn = {"Cancel", "Save"};
			SelectionListener[] listnrList  = {
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						shellDialogAddAndModifyEnvVars.dispose();
					}
				},
				saveSelctn
			};
			Jaswt.getInstance().createButtons(namesBtn, 200, 130, 80, 30, 10, this, listnrList, CreateContentsStyle.HORIZONTAL);
		}
	}

	/* ############################################################################# */
	/* END INNER CLASS */
	/* ############################################################################# */

}
