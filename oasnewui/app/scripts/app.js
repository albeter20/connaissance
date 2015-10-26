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
angular.module('oasnewui',['ngRoute','ui.router','ui.bootstrap','ngResource'])
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
	.state('base.user.create',{
		url:'/create',
		onEnter:['$state','$uibModal','$resource',function($state, $uibModal, $resource) {
			console.log('open modal');
			$uibModal.open({
				templateUrl:'views/createUser.html',
				backdrop:false,
				windowClass:'right fade',
				controller:'UserController',
				resolve: {
		            '$uibModalInstance': function () { return function () { return uibModalInstance; } }
		        }
			}).result.finally(function(){
				$state.go('base.user');
			});
		}]
		
	})

	
});