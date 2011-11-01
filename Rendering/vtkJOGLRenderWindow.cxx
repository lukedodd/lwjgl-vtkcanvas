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

#include "vtkJOGLRenderWindow.h"
#include "vtkObjectFactory.h"
#include "vtkRendererCollection.h"
#include "vtkOpenGLRenderer.h"
#include "vtkCommand.h"

vtkStandardNewMacro(vtkJOGLRenderWindow);

vtkJOGLRenderWindow::vtkJOGLRenderWindow()
{
    IsCurrentFlag = 0;
}

vtkJOGLRenderWindow::~vtkJOGLRenderWindow()
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

void vtkJOGLRenderWindow::PrintSelf(ostream& os, vtkIndent indent)
{
  this->Superclass::PrintSelf(os, indent);
}

void vtkJOGLRenderWindow::SetFrontBuffer(unsigned int b)
{
  this->FrontBuffer = b;
}

void vtkJOGLRenderWindow::SetFrontLeftBuffer(unsigned int b)
{
  this->FrontLeftBuffer = b;
}

void vtkJOGLRenderWindow::SetFrontRightBuffer(unsigned int b)
{
  this->FrontRightBuffer = b;
}

void vtkJOGLRenderWindow::SetBackBuffer(unsigned int b)
{
  this->BackBuffer = b;
}

void vtkJOGLRenderWindow::SetBackLeftBuffer(unsigned int b)
{
  this->BackLeftBuffer = b;
}

void vtkJOGLRenderWindow::SetBackRightBuffer(unsigned int b)
{
  this->BackRightBuffer = b;
}

void vtkJOGLRenderWindow::Finalize()
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

void vtkJOGLRenderWindow::Frame()
{
  this->InvokeEvent(vtkCommand::WindowFrameEvent, NULL);
}

void vtkJOGLRenderWindow::MakeCurrent()
{
  this->InvokeEvent(vtkCommand::WindowMakeCurrentEvent, NULL);
}

bool vtkJOGLRenderWindow::IsCurrent()
{
  // bool current = 0;
  // Should set is current flag.
  this->InvokeEvent(vtkCommand::WindowIsCurrentEvent, NULL);
  return IsCurrentFlag != 0;
}

int vtkJOGLRenderWindow::SupportsOpenGL()
{
  int supports_ogl = 0;
  this->InvokeEvent(vtkCommand::WindowSupportsOpenGLEvent, &supports_ogl);
  return supports_ogl;
}

int vtkJOGLRenderWindow::IsDirect()
{
  int is_direct = 0;
  this->InvokeEvent(vtkCommand::WindowIsDirectEvent, &is_direct);
  return is_direct;
}

void vtkJOGLRenderWindow::PushState()
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

void vtkJOGLRenderWindow::PopState()
{
  glMatrixMode(GL_PROJECTION);
  glPopMatrix();
  glMatrixMode(GL_MODELVIEW);
  glPopMatrix();

  glPopClientAttrib();
  glPopAttrib();
}


void vtkJOGLRenderWindow::SetWindowId(void*)
{
}

void* vtkJOGLRenderWindow::GetGenericWindowId()
{
  return NULL;
}


void vtkJOGLRenderWindow::SetDisplayId(void*)
{
}

void vtkJOGLRenderWindow::SetParentId(void*)
{
}

void* vtkJOGLRenderWindow::GetGenericDisplayId()
{
  return NULL;
}

void* vtkJOGLRenderWindow::GetGenericParentId()
{
  return NULL;
}

void* vtkJOGLRenderWindow::GetGenericContext()
{
  return NULL;
}

void* vtkJOGLRenderWindow::GetGenericDrawable()
{
  return NULL;
}

void vtkJOGLRenderWindow::SetWindowInfo(char*)
{
}

void vtkJOGLRenderWindow::SetParentInfo(char*)
{
}

int* vtkJOGLRenderWindow::GetScreenSize()
{
  return NULL;
}

void vtkJOGLRenderWindow::Start()
{
}

void vtkJOGLRenderWindow::HideCursor()
{
}

void vtkJOGLRenderWindow::ShowCursor()
{
}

void vtkJOGLRenderWindow::SetFullScreen(int)
{
}

void vtkJOGLRenderWindow::WindowRemap()
{
}

int vtkJOGLRenderWindow::GetEventPending()
{
  return 0;
}

void vtkJOGLRenderWindow::SetNextWindowId(void*)
{
}

void vtkJOGLRenderWindow::SetNextWindowInfo(char*)
{
}

void vtkJOGLRenderWindow::CreateAWindow()
{
}

void vtkJOGLRenderWindow::DestroyWindow()
{
}
