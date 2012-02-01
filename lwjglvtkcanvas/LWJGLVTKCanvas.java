import java.awt.event.ComponentEvent;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.knime.core.node.NodeLogger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

import vtk.vtkGenericJavaRenderWindow;
import vtk.vtkRenderer;
import vtk.vtkRenderWindow;

// A awt component which draws a vtk render window onto the screen using lwjgls AWTGLCanvas.
// This is used instead of vtkCanvas/Panel because those classes seem to be unstable when the 
// panels are disposed or more than one is in use.
// The vtkCanvas/Panel classes also leak under linux, this does not.
// How to use: 
//   - Build lwjgl-vtkcanvas branch of vtk. 
//     This needs to be done because the vtkGenericRenderWindow class was not quite flexible 
//     enough to allow me to implement LWJGLVTKCanvas without any changes to vtk.
//   - Add this file to your java project. 
//   - Download lwjgl (http://lwjgl.org/download.php). I used version 2.8.2.
//      - Add lwjgl.jar to your classpath.
//      - Add lwjgl64.so (or lwjgl.so if you're on 32-bit, which I have not tested) to some
//        directory on your java system.library.path - you could probably just drop it in whatever
//        directory your application loads the VTK library files from!o
//
//  See the "main" method for example usage.
//  Things to note:
//    - This class does not pass events along to the render windows vtkInteractor, but that is
//      trivial to add (I don't use it, mail me/submit a patch if you want this functionality :).
//    - This is one big hack. I was having trouble similar to [1] and after spending a lot of time
//      trying to fix my code, or the vtkPanel code I eventually wanted to see if bypassing the
//      entire vtkPanel approach improved stability. It did, so that's one piece of the puzzle.
//      If this class fixes anyone else's issues then there's more evidence of something funky in
//      vtkPanel, if not then I know I screwed up somehow.
//
//  Good luck,
//  Luke Dodd (luke.dodd@gmail.com)
//
//    [1] http://vtk.1045678.n5.nabble.com/Crashes-in-Java-wrapped-vtk-on-linux-because-of-xcb-problems-td5023093.html
public class LWJGLVTKCanvas extends AWTGLCanvas{
	
	
	protected vtkRenderer ren;
	// A render window for which we can easily specify the MakeCurrent and IsCurrent 
	// operations from Java using vtk observesr.
	// (I don't use the standard vtkGenericRenderWindow class because it's not quite possible
	//  to implement IsCurrent using observers from Java.)
	protected vtkGenericJavaRenderWindow rw;
	
	// added this lock as locking seems to be important with the interactor
	private Lock lock;
	
	// If true will dispose after removeNotify and panel can't be used after being removed.
	private boolean autoDispose = true; 
	
	private static final NodeLogger LOGGER = NodeLogger.getLogger(LWJGLVTKCanvas.class);
	
	public interface PreRenderOperation{
		public void rendering(LWJGLVTKCanvas src);
	}

    // use this to check wheter we can make the context active or not
    private boolean initGL = false;
	
	// This functor will be run prior to any render.
	// Useful for catching renderings prompted by a resize.
	private PreRenderOperation preRender = null;
	
	public LWJGLVTKCanvas() throws LWJGLException {
		super();
		lock = new ReentrantLock();
		ren = new vtkRenderer();
		rw = new vtkGenericJavaRenderWindow();
		rw.SetSize(getWidth(), getHeight());
		// Tell the generic render window how to make the GL context current.
        rw.AddObserver("WindowMakeCurrentEvent", this, "MakeCurrent");
        rw.AddObserver("WindowIsCurrentEvent", this, "IsCurrent");
	}
	
	@Override
	public void initContext(float r, float g, float b) {
		super.initContext(r, g, b);
	}
	
	@Override
	protected void initGL() {
		super.initGL();
        rw.AddRenderer(ren);
        rw.SetMapped(1);
		rw.SetSize(getWidth(), getHeight());
		rw.OpenGLInit();

        initGL = true;
	}
	
	@Override
	protected void paintGL() {
		// Run users code prior to render.
		if(preRender != null)
			preRender.rendering(this);
		
		rw.Render(); // without this there is no leak!
		try {
			swapBuffers();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
        lock();
		rw.SetSize(getWidth(), getHeight());	
        unlock();
		super.componentResized(e);
	}
	
	// This method is called from VTK when it needs to make the GL context current.
	// This can happen outside of a renderer.Render() call!
	public void MakeCurrent(){
		try {
			// From a quick look at the source I think this does not do any AWT locking, 
			// so hopefully deadlocks won't occur.
			if(initGL && !isCurrent()){
				makeCurrent(); 
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
        }
	}
	
	// This method is called from VTK go to check if the context window for the associated render window
	// is current.
	public void IsCurrent(){
        try {
			if(isCurrent())
			    rw.SetIsCurrentFlag(1);
			else
			    rw.SetIsCurrentFlag(0);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
    }
	
	@Override
	public void removeNotify() {
		if(autoDispose){
			// Context is going to be destroyed, tell the render window first.
			if(rw != null){
			    LOGGER.debug("Destroying LWJGLVTKCanvas");
                rw.RemoveRenderer(ren);
		        rw.SetMapped(0);
			}
			super.removeNotify();
		}
        initGL = false;
	}

	public vtkRenderer GetRenderer() {
		return ren;
	}
	
	public vtkRenderWindow GetRenderWindow() {
		return rw;
	}

	// Render vtk, swap buffers.
	public void render() {
        update(getGraphics());
	}
    
	public boolean isAutoDispose() {
		return autoDispose;
	}

	public void setAutoDispose(boolean autoDispose) {
		this.autoDispose = autoDispose;
	}

	
	public PreRenderOperation getPreRender() {
		return preRender;
	}

	public void setPreRender(PreRenderOperation preRender) {
		this.preRender = preRender;
	}

	public void lock() {
	    this.lock.lock();
	}
	
	public void unlock() {
	    this.lock.unlock();
	}

    @Override
    public void setSize(final int x, final int y) {
        super.setSize(x, y);
        lock();
        rw.SetSize(x, y);
        unlock();
    }
}
