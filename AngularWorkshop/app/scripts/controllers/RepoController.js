angular.module('angularWorkshop')
  .controller('RepoController', function($scope, gitHubService, $routeParams) {

    var username = $routeParams.username;
    var reponame = $routeParams.reponame;

    gitHubService.getRepoDetails(username, reponame)
      .then(function(response) {
        $scope.repo = response;
        $log.info('3.Name:'+repo.name);
        $log.info('3.Open Issues'+repo.open_issues_count);
        
      }, function(reason) {
        $scope.errorMessage = reason;
      });
  });