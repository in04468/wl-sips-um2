# wl-sips-portal
Worldline SIPS portal repository

## Intro

This repository is a Play 2.5 application based on the [Play+AngularJS+RequireJS](https://github.com/mariussoutier/play-angular-require-seed) seed.

## Code Organization

The JavaScript modules are organized as follows:

    |- app
    |-- assets
    |--- javascripts    <- contains all the JavaScript/CoffeeScript modules
    |---- app.js        <- app module, wires everything together
    |---- main.js       <- tells RequireJS how to load modules and bootstraps the app
    |---- common/       <- a module, in this case
    |----- main.js      <- main file of the module, loads all sub-files in this folder
    |----- filters.js   <- common's filters
    |----- directives/  <- common's directives
    |----- services/    <- common's services
    |---- ...