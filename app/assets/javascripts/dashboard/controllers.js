/**
 * Dashboard controllers.
 */
define([], function() {
  'use strict';

  /**
   * user is not a service, but stems from userResolve (Check
   * ../user/services.js) object used by dashboard.routes.
   */
  var DashboardCtrl = function($scope, $http, user, notify) {
    $scope.user = user;
    $scope.retriveUserProfile = function() {
      $http.get('/contact/' + $scope.user.id).success(function(data) {
        $scope.contact = data.contact;
        $scope.account = data.account;
        
        var merchantStatus = $scope.contact.Merchant_Status__c;

        // Check if merchant on-boarding flag is set to 'On-boarded'. If not show notification with link to complete the same.
        if (!merchantStatus || merchantStatus !== 'On-boarded') {
          
          var messageTemplate = '<span>You merchant on-boarding data is incomplete. '+
          'Follow the link <a href="/onboarding">Merchant On-boarding</a> to complete your merchant on-boarding data.</span>';
          
          notify({
            messageTemplate: messageTemplate,
            classes : 'alert-warning',
            position : 'right',
            duration: 0
          });
        }
      });
    };
  };
  DashboardCtrl.$inject = [ '$scope', '$http', 'user', 'notify' ];

  var AdminDashboardCtrl = function($scope, user) {
    $scope.user = user;
  };
  AdminDashboardCtrl.$inject = [ '$scope', 'user' ];

  return {
    DashboardCtrl : DashboardCtrl,
    AdminDashboardCtrl : AdminDashboardCtrl
  };
});
