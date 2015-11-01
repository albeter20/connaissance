angular.module('oasnewui').controller('UserController',
		function($scope,UserService,$log) {
	        $scope.user={};
	        
	        // set values for roles
//	        $scope.user.list = [
//	              			  {key: "1", text: "One"},
//	              			  {key: {id:2}, text: "Two"},
//	              			  {key: {id:3}, text: "Three"}
//	              			];
//	        
	        // roles list.Ideally this should come from roles master and will be coming via a service.
	        $scope.user.roles=[{
		        id:'',
		        label:'Select Role'
		        },{
		        	id:1,
		        	label:'Administrator'
		        },{
		        	id:2,
		        	label:'Administrative Co-ordinator'
		        },{
		        	id:3,
		        	label:'Proctor'
		        }];
	        $scope.user.role=$scope.user.roles[0];	
	        
	        // Timezone list. This should come from customer configuration
	        $scope.user.timezones=[{
		        timezoneId:'',
		        label:'Select Timezone'
		        },{
		        	timezoneId:1,
		        	label:'US-East'
		        },{
		        	timezoneId:2,
		        	label:'US-West'
		        },{
		        	timezoneId:3,
		        	label:'US-Central'
		        },{
		        	timezoneId:4,
		        	label:'APAC'
		        },{
		        	timezoneId:5,
		        	label:'ANZ'
		        },{
		        	timezoneId:6,
		        	label:'India'
		        }];
	        $scope.user.timezone=$scope.user.timezones[0];
	        
			$scope.cancel = function() {
				$scope.$close();
			};
			
			$scope.createUser=function(){
				$log.info('User Controller');
				$log.info($scope.user);

				//Preparing address object
				var address={};
				address.primaryPhoneNo=$scope.user.primaryPhone1+'-'+$scope.user.primaryPhone2+'-'+$scope.user.primaryPhone3;
				address.secondaryPhoneNo=$scope.user.secondaryPhone1+'-'+$scope.user.secondaryPhone2+'-'+$scope.user.secondaryPhone3;
				address.addressLine1=$scope.user.addressLine1;
				address.addressLine2=$scope.user.addressLine2;
				address.city=$scope.user.city;
				address.state=$scope.user.state;
				address.zipCode=$scope.user.zip;
				address.fax=$scope.user.fax1+'-'+$scope.user.fax2+'-'+$scope.user.fax3;
			
				var userData=$scope.user;
				userData.address=address;
				$log.info('Prepared object');
				$log.info(userData);
				UserService.create(userData)
				.then(function(response){
					
				},function(reason){
					$log.info('Issue in controller'+reason.value);
				});
			};
		});