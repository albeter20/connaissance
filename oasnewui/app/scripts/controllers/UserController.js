angular.module('oasnewui').controller('UserController',
		function($scope) {
			$scope.cancel = function() {
				$scope.$close();
			};
		});