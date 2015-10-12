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
      },
     getRepoDetails:function(username,reponame){
        var repo;
        var repoUrl='https://api.github.com/repos/'+username+'/'+reponame;
        return $http.get(repoUrl)
                    .then(function(response){
                      repo=response.data;
                      $log.info('First call'+repo.login);
                      return $http.get(repoUrl+'/contributors');
                    })
                    .then(function(response){
                      repo.contributors=response.data;
                      $log.info('Second call '+repo.contributors);
                      return repo;
                    })
      }
    };

  });