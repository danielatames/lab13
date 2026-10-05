import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  cargando = signal(false);
  errorServidor = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  constructor() {
    if (this.auth.estaAutenticado()) {
      this.router.navigate(['/recetas']);
    }
  }

  invalido(campo: 'username' | 'password'): boolean {
    const c = this.form.controls[campo];
    return c.invalid && (c.touched || c.dirty);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); 
    }

    this.cargando.set(true);
    this.errorServidor.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/recetas']),
      error: (err: HttpErrorResponse) => {
        this.cargando.set(false);
        if (err.status === 401) {
          this.errorServidor.set('Usuario o contraseña incorrectos.');
        } else if (err.status === 0) {
          this.errorServidor.set('No se pudo conectar con el servidor. ¿Está corriendo el backend?');
        } else {
          this.errorServidor.set(err.error?.detail ?? 'Error inesperado al iniciar sesión.');
        }
      }
    });
  }
}