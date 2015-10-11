angular.module('angularWorkshop')

.controller('MainController', function($scope, $interval, $location) {

  $scope.username = 'angular';

  var intervalValue = null;
  intervalValue = $interval(function() {
    $scope.counter -= 1;
    if ($scope.counter < 1) {
      $scope.search($scope.username);
    }
  }, 1000, $scope.counter);

  $scope.search = function(username) {
    if (intervalValue) {
      $interval.cancel(intervalValue)
      $scope.counter = null;
    }
    //
    $location.path('/user/'+username);
  }
  $scope.message = "Github Viewer";
  $scope.counter = 5;
});