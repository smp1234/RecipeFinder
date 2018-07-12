import { HttpClient, HttpRequest, HttpEvent,HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

/*
  Generated class for the AuthServiceProvider provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class AuthServiceProvider {

  constructor(public http: HttpClient) {
    console.log('Hello AuthServiceProvider Provider');
  }

  loginAuth(emailId:string): Observable<HttpEvent<{}>> {
    
   
    const params = new HttpParams().set('emailId', emailId);
    const req = new HttpRequest('GET', 'http://192.168.1.145:8080/login', {
      reportProgress: true,
      params: params,
      responseType: 'text'
    });
    
      
      return this.http.request(req);
    }

}
