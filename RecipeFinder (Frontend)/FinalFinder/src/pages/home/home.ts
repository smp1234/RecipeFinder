import { Component, Injectable } from '@angular/core';
import{WelcomePage} from '../../pages/welcome/welcome';
import { NavController, App, LoadingController,NavParams } from 'ionic-angular';
import { HttpClient, HttpRequest, HttpEvent, HttpResponse,HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/map'


@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
@Injectable()
export class HomePage {

  public selectedFiles:FileList;
  public currentFileUpload:File;
  
  data:Observable<any>;
  public recipe: Observable<any>;
  result:any;
  ingredients:any;
  name:any;
  steps:any;
  public userId:any;
  constructor(private navParam:NavParams, public http: HttpClient, public navCtrl: NavController, public app: App , public loadingCtrl: LoadingController) {
  
console.log("params : "+navParam.get('param1'));
    this.userId = navParam.get("userId");
  }

  logout(){
    // Remove API token
    const root = this.app.getRootNav();
    root.popToRoot();
  }
  pushFileToStorage(file:File): Observable<HttpEvent<{}>> {
    console.log('Success');
      let formdata: FormData = new FormData();
      formdata.append('file',file);
      console.log(this.userId);
      const params = new HttpParams().set('uid', this.userId);
      const req = new HttpRequest('POST', 'http://192.168.1.145:8080/upload', formdata, {
        reportProgress: true,
        params:params,
        responseType: 'text'
      });
      console.log('Successful');
      return this.http.request(req);
    }
  
    selectFile(event) {
      const file = event.target.files.item(0)
   
      if (file.type.match('image.*')) {
        this.selectedFiles = event.target.files;
      } else {
        alert('invalid format!');
      }
    }
  
    upload() {
      this.currentFileUpload = this.selectedFiles.item(0)
      this.pushFileToStorage(this.currentFileUpload).subscribe(event => {
         if (event instanceof HttpResponse) {
          this.result = event.body;
          this.result=JSON.parse(this.result);
          this.ingredients=this.result.ingredients;
          this.name=this.result.name;
          this.steps=this.result.steps;
          console.log(this.result.name);
  
        }
        
      }) 
      this.selectedFiles = undefined
    }
    
  }
