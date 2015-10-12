angular.module('angularWorkshop')
  .factory('gitHubService', function($http,$log) {

    return {
      
      getUser: function(username) {
        $log.info('URL from getUser service'+'https://api.github.com/users/' +username);
        return $http.get("https://api.github.com/users/" +username)
                    .then(function(response) {
                            return response.data;
          },function(reason){
             $log.info("Reason from service"+reason.value);
          });
      },
      getRepos: function(user) {
        $log.info('URL from getRepos service '+user.repos_url);
        return $http.get(user.repos_url)
          .then(function(response) {
              return response.data;
          });
      }
    };

  });