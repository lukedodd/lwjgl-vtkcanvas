import vtk.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

// Minimal example for vtk xcb crashse under linux.
public class VTKXCBBugReproduce {
	public static vtkPanel canvas;
	public static void main(String argv[]) throws InterruptedException {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// put a vtkPanel in a JFrame
				canvas = new vtkPanel();
				final JFrame f = new JFrame();
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
		System.out.println("Now close the frame to see the xcb error (happens ~10-50% of the time for me)");
		System.out.println("You have to wait for a few seconds too...");
		// Keep the program running, wait for gc and auto vtk gc.
		// Omitting this makes it crash more often, I think.
		/*
		for(;;){
			Thread.sleep(1000);
		}
		 */
	}
}
