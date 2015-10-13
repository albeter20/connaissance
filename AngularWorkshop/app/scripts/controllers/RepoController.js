angular.module('angularWorkshop')
  .controller('RepoController', function($scope, gitHubService, $routeParams) {

    var username = $routeParams.username;
    var reponame = $routeParams.reponame;

    gitHubService.getRepoDetails(username, reponame)
      .then(function(response) {
        $scope.repo = response.data;
      }, function(reason) {
        $scope.errorMessage = reason;
      });
  });