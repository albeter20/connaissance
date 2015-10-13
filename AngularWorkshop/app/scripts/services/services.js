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
                      $log.info('1.Name:'+repo.name);
                      $log.info('1.Open Issues'+repo.open_issues_count);
                  	  $log.info('Status text:'+response.statusText);
                      return $http.get(repoUrl+'/contributors');
                    },function(reason){
                    	$log.info('Problem in first call'+reason.status);
                    	$log.info(reason.statusText);
                    })
                    .then(function(response){
                      repo.contributors=response.data;
                      $log.info('2.Name:'+repo.name);
                      $log.info('2.Open Issues'+repo.open_issues_count);
                      $log.info('Second call '+repo.contributors);
                      return repo;
                    })
      }
    };

  });