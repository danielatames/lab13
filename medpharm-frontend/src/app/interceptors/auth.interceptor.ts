import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { API_URL } from '../config';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.getToken();


  const esApi = req.url.startsWith(API_URL);
  const esLogin = req.url.includes('/auth/login');

  const peticion = token && esApi && !esLogin
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 && !esLogin) {
        auth.logout();
      }
      return throwError(() => err);
    })
  );
};