import { Component,Injectable } from '@angular/core';
import { IonicPage, NavController, NavParams, LoadingController, ToastController } from 'ionic-angular';
import { TabsPage } from '../../pages/tabs/tabs';
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpRequest, HttpEvent, HttpParams,HttpResponse } from '@angular/common/http';
import { AuthServiceProvider } from '../../providers/auth-service/auth-service';


/**
 * Generated class for the WelcomePage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-welcome',
  templateUrl: 'welcome.html',
})
@Injectable()
export class WelcomePage {

  loading: any;
  loginData = {emailId:''};
  data:any;
  public userId:any;

  constructor(public authService: AuthServiceProvider,public http: HttpClient, public navCtrl: NavController, public navParams: NavParams, public loadingCtrl: LoadingController, private toastCtrl: ToastController) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad WelcomePage');
  }

  login() {
    
    let emailId=this.loginData.emailId;
   this.authService.loginAuth(emailId).subscribe(event =>{
     if(event instanceof HttpResponse){
    console.log(event.body);
    this.userId=event.body;
    console.log(this.loginData.emailId);
    this.navCtrl.push(TabsPage,{
      'param1':"13",
      'param2':"14"
    });
     }
   });
   
      
    }

 
  }

  


