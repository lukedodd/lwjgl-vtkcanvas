/*=========================================================================

  Program:   Visualization Toolkit
  Module:    vtkGenericRenderWindowInteractor.cxx

  Copyright (c) Ken Martin, Will Schroeder, Bill Lorensen
  All rights reserved.
  See Copyright.txt or http://www.kitware.com/Copyright.htm for details.

     This software is distributed WITHOUT ANY WARRANTY; without even
     the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
     PURPOSE.  See the above copyright notice for more information.

=========================================================================*/

#include "vtkGenericJavaRenderWindow.h"
#include "vtkObjectFactory.h"
#include "vtkRendererCollection.h"
#include "vtkOpenGLRenderer.h"
#include "vtkCommand.h"

vtkStandardNewMacro(vtkGenericJavaRenderWindow);

vtkGenericJavaRenderWindow::vtkGenericJavaRenderWindow()
{
    IsCurrentFlag = 0;
}

vtkGenericJavaRenderWindow::~vtkGenericJavaRenderWindow()
{
  this->Finalize();

  vtkRenderer* ren;
  this->Renderers->InitTraversal();
  for ( ren = vtkOpenGLRenderer::SafeDownCast(this->Renderers->GetNextItemAsObject());
    ren != NULL;
    ren = vtkOpenGLRenderer::SafeDownCast(this->Renderers->GetNextItemAsObject()))
    {
    ren->SetRenderWindow(NULL);
    }
}

void vtkGenericJavaRenderWindow::PrintSelf(ostream& os, vtkIndent indent)
{
  this->Superclass::PrintSelf(os, indent);
}

void vtkGenericJavaRenderWindow::SetFrontBuffer(unsigned int b)
{
  this->FrontBuffer = b;
}

void vtkGenericJavaRenderWindow::SetFrontLeftBuffer(unsigned int b)
{
  this->FrontLeftBuffer = b;
}

void vtkGenericJavaRenderWindow::SetFrontRightBuffer(unsigned int b)
{
  this->FrontRightBuffer = b;
}

void vtkGenericJavaRenderWindow::SetBackBuffer(unsigned int b)
{
  this->BackBuffer = b;
}

void vtkGenericJavaRenderWindow::SetBackLeftBuffer(unsigned int b)
{
  this->BackLeftBuffer = b;
}

void vtkGenericJavaRenderWindow::SetBackRightBuffer(unsigned int b)
{
  this->BackRightBuffer = b;
}

void vtkGenericJavaRenderWindow::Finalize()
{
  // tell each of the renderers that this render window/graphics context
  // is being removed (the RendererCollection is removed by vtkRenderWindow's
  // destructor)
  vtkRenderer* ren;
  this->Renderers->InitTraversal();
  for ( ren = vtkOpenGLRenderer::SafeDownCast(this->Renderers->GetNextItemAsObject());
    ren != NULL;
    ren = vtkOpenGLRenderer::SafeDownCast(this->Renderers->GetNextItemAsObject())  )
    {
    ren->SetRenderWindow(NULL);
    ren->SetRenderWindow(this);
    }
}

void vtkGenericJavaRenderWindow::Frame()
{
  this->InvokeEvent(vtkCommand::WindowFrameEvent, NULL);
}

void vtkGenericJavaRenderWindow::MakeCurrent()
{
  this->InvokeEvent(vtkCommand::WindowMakeCurrentEvent, NULL);
}

bool vtkGenericJavaRenderWindow::IsCurrent()
{
  // bool current = 0;
  // Should set is current flag.
  this->InvokeEvent(vtkCommand::WindowIsCurrentEvent, NULL);
  return IsCurrentFlag != 0;
}

int vtkGenericJavaRenderWindow::SupportsOpenGL()
{
  int supports_ogl = 0;
  this->InvokeEvent(vtkCommand::WindowSupportsOpenGLEvent, &supports_ogl);
  return supports_ogl;
}

int vtkGenericJavaRenderWindow::IsDirect()
{
  int is_direct = 0;
  this->InvokeEvent(vtkCommand::WindowIsDirectEvent, &is_direct);
  return is_direct;
}

void vtkGenericJavaRenderWindow::PushState()
{
  glPushClientAttrib(GL_CLIENT_ALL_ATTRIB_BITS);
  glPushAttrib(GL_ALL_ATTRIB_BITS);

  glMatrixMode(GL_PROJECTION);
  glPushMatrix();
  glLoadIdentity();
  glMatrixMode(GL_MODELVIEW);
  glPushMatrix();
  glLoadIdentity();
}

void vtkGenericJavaRenderWindow::PopState()
{
  glMatrixMode(GL_PROJECTION);
  glPopMatrix();
  glMatrixMode(GL_MODELVIEW);
  glPopMatrix();

  glPopClientAttrib();
  glPopAttrib();
}


void vtkGenericJavaRenderWindow::SetWindowId(void*)
{
}

void* vtkGenericJavaRenderWindow::GetGenericWindowId()
{
  return NULL;
}


void vtkGenericJavaRenderWindow::SetDisplayId(void*)
{
}

void vtkGenericJavaRenderWindow::SetParentId(void*)
{
}

void* vtkGenericJavaRenderWindow::GetGenericDisplayId()
{
  return NULL;
}

void* vtkGenericJavaRenderWindow::GetGenericParentId()
{
  return NULL;
}

void* vtkGenericJavaRenderWindow::GetGenericContext()
{
  return NULL;
}

void* vtkGenericJavaRenderWindow::GetGenericDrawable()
{
  return NULL;
}

void vtkGenericJavaRenderWindow::SetWindowInfo(char*)
{
}

void vtkGenericJavaRenderWindow::SetParentInfo(char*)
{
}

int* vtkGenericJavaRenderWindow::GetScreenSize()
{
  return NULL;
}

void vtkGenericJavaRenderWindow::Start()
{
}

void vtkGenericJavaRenderWindow::HideCursor()
{
}

void vtkGenericJavaRenderWindow::ShowCursor()
{
}

void vtkGenericJavaRenderWindow::SetFullScreen(int)
{
}

void vtkGenericJavaRenderWindow::WindowRemap()
{
}

int vtkGenericJavaRenderWindow::GetEventPending()
{
  return 0;
}

void vtkGenericJavaRenderWindow::SetNextWindowId(void*)
{
}

void vtkGenericJavaRenderWindow::SetNextWindowInfo(char*)
{
}

void vtkGenericJavaRenderWindow::CreateAWindow()
{
}

void vtkGenericJavaRenderWindow::DestroyWindow()
{
}
