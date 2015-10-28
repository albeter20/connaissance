angular.module('oasnewui').controller('UserController',
		function($scope,UserService,$log) {
	        $scope.user={};	 
			$scope.cancel = function() {
				$scope.$close();
			};
			
			$scope.createUser=function(){
				$log.info('User Controller');
				$log.info($scope.user);
//				$log.info('Data received from form'+
//						'first name:'+$scope.user.firstName+' '+
//						'last name:'+$scope.user.lastName+' '+
//						'middle name:'+$scope.user.middleName+' '+
//						'email:'+$scope.user.email+' '+
//						'timezone'+$scope.user.timezone+' '+
//						'role:'+$scope.user.role+' '+
//						'External Userid:'+$scope.user.extUesrId+' '+
//						'group'+$scope.user.group);
				var userData=$scope.user;	
				UserService.create(userData)
				.then(function(response){
					
				},function(reason){
					$log.info('Issue in controller'+reason.value);
				});
			};
		});