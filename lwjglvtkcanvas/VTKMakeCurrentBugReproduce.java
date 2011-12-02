import vtk.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

// Minimal example for vtk MakeCurrent crashse under windows.
public class VTKMakeCurrentBugReproduce {
	public static vtkPanel canvas;
	public static void main(String argv[]) throws InterruptedException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame f = new JFrame();
				// put a vtkPanel in a JFrame
				canvas = new vtkPanel();
				f.getContentPane().add(canvas, BorderLayout.CENTER);
				f.setSize(new Dimension(200,200));
				f.setVisible(true);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						canvas.Delete();
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
						canvas.GetRenderer().AddActor(actor);
						canvas.Render();
					}
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("Finished adding actors to canvas...");
		System.out.println("Now close the frame to see error.");
		// Keep the program running, run vtk gc
		for(;;){
			System.gc();
			vtkObject.JAVA_OBJECT_MANAGER.gc(true);
		}
	}
}
