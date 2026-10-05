import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { RecetasListComponent } from './pages/recetas-list/recetas-list.component';
import { RecetaFormComponent } from './pages/receta-form/receta-form.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'recetas', component: RecetasListComponent, canActivate: [authGuard] },
  { path: 'nueva-receta', component: RecetaFormComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'recetas', pathMatch: 'full' },
  { path: '**', redirectTo: 'recetas' }
];