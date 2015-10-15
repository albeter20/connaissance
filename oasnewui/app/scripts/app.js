angular.module('oasnewui',['ngRoute'])
  .config(function($routeProvider, $locationProvider) {
    $routeProvider.when("/main", {
        templateUrl: 'views/main.html',
        controller: 'MainController'
      })
      .when("/user", {
        templateUrl: 'views/users.html',
        controller: 'UserController'
      })
      .otherwise({
        redirectTo: '/main'
      });
  });
