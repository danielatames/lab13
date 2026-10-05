import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../config';
import { AuthResponse, LoginRequest, Sesion } from '../models/auth.models';

const STORAGE_KEY = 'medpharm_sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private _sesion = signal<Sesion | null>(this.leerSesion());

  readonly sesion = this._sesion.asReadonly();
  readonly username = computed(() => this._sesion()?.username ?? '');
  readonly rol = computed(() => this._sesion()?.rol ?? '');

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/login`, credentials).pipe(
      tap(resp => {
        const sesion: Sesion = { token: resp.token, username: resp.username, rol: resp.rol };
        localStorage.setItem(STORAGE_KEY, JSON.stringify(sesion));
        this._sesion.set(sesion);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this._sesion.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this._sesion()?.token ?? null;
  }

  estaAutenticado(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  private leerSesion(): Sesion | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) as Sesion : null;
  }
}