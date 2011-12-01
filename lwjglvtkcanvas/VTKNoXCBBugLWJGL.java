import vtk.*;

import javax.swing.*;

import org.lwjgl.LWJGLException;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

// This does not crash, demonstrating that LWJGLVTKCanvas is stable in
// some cases where vtkPanel is not (at least on my computer).
public class VTKNoXCBBugLWJGL {
	public static LWJGLVTKCanvas canvas;
	public static void main(String argv[]) throws InterruptedException {

		// load vtk libs
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// put a LWJGLVTKCanvas in a JFrame

				final JFrame f = new JFrame();
				vtkNativeLibrary.LoadAllNativeLibraries();
				canvas = null;
				try {
					canvas = new LWJGLVTKCanvas();
				} catch (LWJGLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}


				f.getContentPane().add(canvas, BorderLayout.CENTER);
				f.setSize(new Dimension(200,200));
				f.setVisible(true);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						// c.Delete(); // crashes with this here, or not
					}
				});
			}
		});

		// Add 1000 actors to the canvases renderer
		for(int i = 0; i < 1000; i++){
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						vtkConeSource coneSource = new vtkConeSource();
						coneSource.SetResolution(100);
						vtkPolyDataMapper mapper = new vtkPolyDataMapper();
						mapper.SetInputConnection(coneSource.GetOutputPort());
						vtkActor actor = new vtkActor();
						actor.SetMapper(mapper);
						actor.SetScale(Math.random()/10.0);
						actor.SetPosition(Math.random(), Math.random(), Math.random());
						canvas.getRenderer().AddActor(actor);
						canvas.render();
					}
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

		System.out.println("Finished adding actors to canvas...");
		System.out.println("Now close the frame to see the xcb error (happens ~50% of the time for me)");
	}
}
