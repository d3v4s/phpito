package it.phpito.view.listener.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;

public class DrawerCpuUsagePaintListener implements PaintListener {
	private ArrayBlockingQueue<Double> cpuUsage; // = new ArrayBlockingQueue<Double>(80);

	public DrawerCpuUsagePaintListener(ArrayBlockingQueue<Double> cpuUsage) {
		super();
		this.cpuUsage = cpuUsage;
	}

	@Override
	public void paintControl(PaintEvent se) {
		Canvas canvas = (Canvas) se.widget;
		int maxX = canvas.getSize().x;
		int maxY = canvas.getSize().y;
		ArrayList<Double> ld = new ArrayList<Double>(cpuUsage); 
		Collections.reverse(ld);
		Double d;
		int posX = maxX;
		for (int i = 0; i < ld.size(); i++) {
			d = ld.get(i);
			se.gc.setForeground(se.display.getSystemColor(SWT.COLOR_WHITE));
			se.gc.drawLine(posX, maxY - (d.intValue() * maxY/100), posX-1, maxY - (ld.get((i == ld.size() -1) ? i : i+1).intValue() * maxY/100));
			se.gc.setForeground(
					(d < 50d) ? se.display.getSystemColor(SWT.COLOR_GREEN) :
					(d < 80d) ? new Color(se.display, new RGB(255, 127, 80)) :
						se.display.getSystemColor(SWT.COLOR_RED)
			);
			se.gc.drawPoint(posX, maxY - (d.intValue() * maxY/100));
			posX--;
		}
		if (cpuUsage.remainingCapacity() == 0) {
			try {
				cpuUsage.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
