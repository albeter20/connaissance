angular.module('oasnewui')
.factory('UserService',function($http,$log){
	return{
		create:function(userData){
			$log.info('User Service');
			$log.info(userData);
//			$log.info('User Data in service'+'first name:'+userData.firstName+' '+
//					'last name:'+userData.lastName+' '+
//					'middle name:'+userData.middleName+' '+
//					'email:'+userData.email+' '+
//					'timezone'+userData.timezone+' '+
//					'role:'+userData.role+' '+
//					'External Userid:'+userData.extUesrId+' '+
//					'group'+userData.group
//					);
//			
			
			return $http.get('http://localhost:8089/onlinebank/api/user')
			.then(function(response){
				return response.data;
			},function(reason){
				$log.info('Reason from creation service'+reason.value);
			});
		}
	}; 
	
});

 