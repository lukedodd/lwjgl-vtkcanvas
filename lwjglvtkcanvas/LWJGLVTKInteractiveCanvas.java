import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.Timer;

import org.lwjgl.LWJGLException;

import vtk.vtkBoxWidget;
import vtk.vtkGenericRenderWindowInteractor;
import vtk.vtkInteractorStyle;
import vtk.vtkInteractorStyleTrackballCamera;
import vtk.vtkPlaneWidget;
import vtk.vtkProp3D;

/**
 * This class extends the LWJGLVTKCanvas to add the functionality that can be
 * found in the vtkCanvas class. That is, it adds a
 * vtkGenericRenderWindowInteractor, a vtkPlaneWidget and a vtkBoxWidget.
 * 
 * @see LWJGLVTKCanvas
 * @author Clemens Müthing (clemens.muething@uni-konstanz.de)
 */
public class LWJGLVTKInteractiveCanvas extends LWJGLVTKCanvas implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    protected vtkGenericRenderWindowInteractor iren = new vtkGenericRenderWindowInteractor();
    protected Timer timer = new Timer(10, new DelayAction());
    protected int ctrlPressed = 0;
    protected int shiftPressed = 0;
    protected vtkPlaneWidget pw = new vtkPlaneWidget();
    protected vtkBoxWidget bw = new vtkBoxWidget();
    protected int lastX = 0;
    protected int lastY = 0;


    public LWJGLVTKInteractiveCanvas() throws LWJGLException {
        super();

        iren.SetRenderWindow(GetRenderWindow());
        iren.TimerEventResetsTimerOff();
        iren.AddObserver("CreateTimerEvent", this, "StartTimer");
        iren.AddObserver("DestroyTimerEvent", this, "DestroyTimer");
        iren.SetSize(200, 200);
        iren.ConfigureEvent();

        pw.AddObserver("EnableEvent", this, "BeginPlaneInteraction");
        bw.AddObserver("EnableEvent", this, "BeginBoxInteraction");
        pw.SetKeyPressActivationValue('l');
        bw.SetKeyPressActivationValue('b');
        pw.SetInteractor(iren);
        bw.SetInteractor(iren);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                // The Canvas is being resized, get the new size
                int width = getWidth();
                int height = getHeight();
                setSize(width, height);
            }
        });

        ren.SetBackground(0.0, 0.0, 0.0);

        // Setup same interactor style than vtkPanel
        vtkInteractorStyleTrackballCamera style = new vtkInteractorStyleTrackballCamera();
        iren.SetInteractorStyle(style);

        // add the listeners
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(this);
    }

    public void Delete() {
        iren = null;
        pw = null;
        bw = null;
    }


    public void StartTimer() {
        if (timer.isRunning())
            timer.stop();

        timer.setRepeats(true);
        timer.start();
    }

    public void DestroyTimer() {
        if (timer.isRunning())
            timer.stop();
    }

    public vtkGenericRenderWindowInteractor getRenderWindowInteractor() {
        return this.iren;
    }

    public void setInteractorStyle(vtkInteractorStyle style) {
        iren.SetInteractorStyle(style);
    }

    public void addToPlaneWidget(vtkProp3D prop) {
        pw.SetProp3D(prop);
        pw.PlaceWidget();
    }

    public void addToBoxWidget(vtkProp3D prop) {
        bw.SetProp3D(prop);
        bw.PlaceWidget();
    }

    public void BeginPlaneInteraction() {
        System.out.println("Plane widget begin interaction");
    }

    public void BeginBoxInteraction() {
        System.out.println("Box widget begin interaction");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (ren.VisibleActorCount() == 0)
            return;
        lock();
        rw.SetDesiredUpdateRate(5.0);
        lastX = e.getX();
        lastY = e.getY();

        ctrlPressed = (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK ? 1 : 0;
        shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK ? 1 : 0;

        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed, shiftPressed, '0', 0, "0");

        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
            iren.LeftButtonPressEvent();
        } else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
            iren.MiddleButtonPressEvent();
        } else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
            iren.RightButtonPressEvent();
        }
        unlock();
        
        this.render();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        rw.SetDesiredUpdateRate(0.01);

        ctrlPressed = (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK ? 1 : 0;
        shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK ? 1 : 0;

        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed, shiftPressed, '0', 0, "0");

        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
            lock();
            iren.LeftButtonReleaseEvent();
            unlock();
        }

        if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
            lock();
            iren.MiddleButtonReleaseEvent();
            unlock();
        }

        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
            lock();
            iren.RightButtonReleaseEvent();
            unlock();
        }
        
        this.render();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.requestFocus();
        iren.SetEventInformationFlipY(e.getX(), e.getY(), 0, 0, '0', 0, "0");
        iren.EnterEvent();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        iren.SetEventInformationFlipY(e.getX(), e.getY(), 0, 0, '0', 0, "0");
        iren.LeaveEvent();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();

        ctrlPressed = (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK ? 1 : 0;
        shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK ? 1 : 0;

        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed, shiftPressed, '0', 0, "0");

        lock();
        iren.MouseMoveEvent();
        unlock();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (ren.VisibleActorCount() == 0)
            return;

        ctrlPressed = (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK ? 1 : 0;
        shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK ? 1 : 0;

        iren.SetEventInformationFlipY(e.getX(), e.getY(), ctrlPressed, shiftPressed, '0', 0, "0");

        lock();
        iren.MouseMoveEvent();
        unlock();
        
        this.render();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (ren.VisibleActorCount() == 0)
            return;
        char keyChar = e.getKeyChar();

        ctrlPressed = (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK ? 1 : 0;
        shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK ? 1 : 0;

        iren.SetEventInformationFlipY(lastX, lastY, ctrlPressed, shiftPressed, keyChar, 0, String.valueOf(keyChar));

        lock();
        iren.KeyPressEvent();
        iren.CharEvent();
        unlock();
        
        this.render();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private class DelayAction implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            lock();
            iren.TimerEvent();
            unlock();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();

        lock();

        // mouse up
        if (notches < 0) {
            iren.MouseWheelForwardEvent();
        } else { // mouse down
            iren.MouseWheelBackwardEvent();
        }
        
        unlock();

        this.render();
    }
}
