angular.module('angularWorkshop')

.controller('UserController', function($scope, gitHubService, $routeParams) {
  $scope.username = $routeParams.username;
  $scope.displaySortOrder = '-stargazers_count';
  var intervalValue = null;
  gitHubService.getUser($scope.username)
    .then(function(data) {
      $scope.persons = data;
      gitHubService.getRepos($scope.persons)
        .then(function(data) {
          $scope.repos = data;
        }, function(reason) {
          $scope.errorMessage = 'Problem in fetching repos';
        });
    }, function(reason) {
      $scope.errorMessage = "Could not found anything";
    });
  if (intervalValue) {
    $interval.cancel(intervalValue)
    $scope.counter = null;
  }
});