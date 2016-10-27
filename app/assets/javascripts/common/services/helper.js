/** Common helpers */
define(['angular'], function(angular) {
  'use strict';

  var mod = angular.module('common.helper', []);
  mod.service('helper', function() {
    return {
      sayHi: function() {
        return 'Welcome to the Worldline UK payment acceptance portal';
      }
    };
  });
  return mod;
});
