import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {AppRoutingModule} from "./app-routing.module";
import {HomeModule} from "./modules/home/home.module";
import {NotFoundModule} from "./modules/not-found/not-found.module";

@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,
        NotFoundModule,
        HomeModule
    ],
    declarations: [
        AppComponent
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
