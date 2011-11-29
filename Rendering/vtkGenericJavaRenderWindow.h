/*=========================================================================

  Program:   Visualization Toolkit
  Module:    vtkRenderWindow.h

  Copyright (c) Ken Martin, Will Schroeder, Bill Lorensen
  All rights reserved.
  See Copyright.txt or http://www.kitware.com/Copyright.htm for details.

     This software is distributed WITHOUT ANY WARRANTY; without even
     the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
     PURPOSE.  See the above copyright notice for more information.

=========================================================================*/
// .NAME vtkGenericJavaRenderWindow - platform independent render window

// .SECTION Description
// vtkGenericJavaRenderWindow provides a skeleton for implementing a render window
// using one's own OpenGL context and drawable.
// To be effective, one must register an observer for WindowMakeCurrentEvent,
// WindowIsCurrentEvent and WindowFrameEvent.  When this class sends a WindowIsCurrentEvent,
// the call data is an bool* which one can use to return whether the context is current.

#ifndef vtkGenericJavaRenderWindow_hpp
#define vtkGenericJavaRenderWindow_hpp

#include "vtkOpenGLRenderWindow.h"

class VTK_RENDERING_EXPORT vtkGenericJavaRenderWindow : public vtkOpenGLRenderWindow
{
public:
  static vtkGenericJavaRenderWindow* New();
  vtkTypeMacro(vtkGenericJavaRenderWindow, vtkOpenGLRenderWindow);
  void PrintSelf(ostream& os, vtkIndent indent);
protected:
  vtkGenericJavaRenderWindow();
  ~vtkGenericJavaRenderWindow();
  int IsCurrentFlag;


public:

  //! Cleans up graphics resources allocated in the context for this VTK scene.
  void Finalize();

  //! flush the pending drawing operations
  //! Class user may to watch for WindowFrameEvent and act on it
  void Frame();

  //! Makes the context current.  It is the class user's
  //! responsibility to watch for WindowMakeCurrentEvent and set it current.
  void MakeCurrent();

  // Description:
  // Set state returned by IsCurrent.
  vtkSetMacro(IsCurrentFlag,int);
  vtkGetMacro(IsCurrentFlag,int);
  vtkBooleanMacro(IsCurrentFlag,int);

  //! Returns if the context is current.  It is the class user's
  //! responsibility to watch for WindowIsCurrentEvent and set the IsCurrentFlag.
  bool IsCurrent();
 
  //! Returns if OpenGL is supported.  It is the class user's
  //! responsibility to watch for WindowSupportsOpenGLEvent and set the int* flag
  //! passed through the call data parameter.
  int SupportsOpenGL();

  //! Returns if the context is direct.  It is the class user's
  //! responsibility to watch for WindowIsDirectEvent and set the int* flag
  //! passed through the call data parameter.
  int IsDirect();

  // {@
  //! set the drawing buffers to use
  void SetFrontBuffer(unsigned int);
  void SetFrontLeftBuffer(unsigned int);
  void SetFrontRightBuffer(unsigned int);
  void SetBackBuffer(unsigned int);
  void SetBackLeftBuffer(unsigned int);
  void SetBackRightBuffer(unsigned int);
  // }@

  //! convenience function to push the state and push/init the tranform matrices
  void PushState();
  //! convenience function to pop the state and pop the tranform matrices
  void PopState();

  // {@
  //! does nothing
  void SetWindowId(void*);
  void* GetGenericWindowId();
  void SetDisplayId(void*);
  void SetParentId(void*);
  void* GetGenericDisplayId();
  void* GetGenericParentId();
  void* GetGenericContext();
  void* GetGenericDrawable();
  void SetWindowInfo(char*);
  void SetParentInfo(char*);
  int* GetScreenSize();
  void Start();
  void HideCursor();
  void ShowCursor();
  void SetFullScreen(int);
  void WindowRemap();
  int  GetEventPending();
  void SetNextWindowId(void*);
  void SetNextWindowInfo(char*);
  void CreateAWindow();
  void DestroyWindow();
  // }@

protected:

private:
  vtkGenericJavaRenderWindow(const vtkGenericJavaRenderWindow&);  // Not implemented.
  void operator=(const vtkGenericJavaRenderWindow&);  // Not implemented.
};

#endif
