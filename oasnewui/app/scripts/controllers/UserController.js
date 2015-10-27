angular.module('oasnewui').controller('UserController',
		function($scope) {
			$scope.cancel = function() {
				$scope.$close();
			};
			
			$scope.createUser=function(){
				alert('Data received');
			};
		});