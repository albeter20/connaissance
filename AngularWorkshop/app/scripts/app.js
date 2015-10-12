angular.module('angularWorkshop', ['ngRoute'])
  .config(function($routeProvider, $locationProvider) {
    $routeProvider.when("/main", {
        templateUrl: 'views/main.html',
        controller: 'MainController'
      })
      .when("/user/:username", {
        templateUrl: 'views/users.html',
        controller: 'UserController'
      })
      .when('/user/:username/:repos',{
    	templateUrl:'views/repos.html',
    	controller:'ReposController'
      })
      .otherwise({
        redirectTo: '/main'
      });
  });