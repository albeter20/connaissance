//angular.module('oasnewui',['ngRoute','ui.router'])
//  .config(function($routeProvider, $locationProvider) {
//    $routeProvider.when("/main", {
//        templateUrl: 'views/main.html',
//        controller: 'MainController'
//      })
//      .when("/user", {
//        templateUrl: 'views/users.html',
//        controller: 'UserController'
//      })
//      .otherwise({
//        redirectTo: '/main'
//      });
//  });
angular.module('oasnewui',['ngRoute','ui.router'])
.config(function($stateProvider,$urlRouterProvider){
	
	$urlRouterProvider.otherwise('/');
	
	$stateProvider
	.state('base',{
		url:'/',
		views:{
			'header':{
				templateUrl:'views/header.html'
			},
			'content':{
				templateUrl:'views/content.html'
			}
		}
	})
	.state('base.user',{
		url:'user',
		templateUrl:'views/userpane.html',
		controller:'UserController'
	})
	
});