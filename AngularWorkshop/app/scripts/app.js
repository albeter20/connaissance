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
      .when('/repo/:username/:reponame',{
        templateUrl:'views/repo.html',
        controller:'RepoController'
      })
      .otherwise({
        redirectTo: '/main'
      });
  });