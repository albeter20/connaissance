angular
		.module('userModule', [ 'ngRoute' ])

		.controller('TreelistController', function($scope) {

			$scope.categories = {
				"children" : [ {
					"name" : "TABE State",
					"attr" : {
						"id" : "304506",
						"cid" : "1",
						"tcl" : "1"
					},
					"children" : [ {
						"name" : "District 1",
						"attr" : {
							"id" : "501511",
							"cid" : "2",
							"tcl" : "2"
						},
						"children" : [ {
							"name" : "School 1",
							"attr" : {
								"id" : "757457",
								"cid" : "3",
								"tcl" : "3"
							},
							"children" : [ {
								"name" : "Class 1",
								"attr" : {
									"id" : "757486",
									"cid" : "4",
									"tcl" : "4"
								}
							}, {
								"name" : "Class 2",
								"attr" : {
									"id" : "757498",
									"cid" : "4",
									"tcl" : "4"
								}
							}, {
								"name" : "Class 3",
								"attr" : {
									"id" : "757499",
									"cid" : "4",
									"tcl" : "4"
								}
							}, {
								"name" : "Class 4",
								"attr" : {
									"id" : "997206",
									"cid" : "4",
									"tcl" : "4"
								}
							} ]
						}, {
							"name" : "School 2",
							"attr" : {
								"id" : "757457",
								"cid" : "3",
								"tcl" : "3"
							},
							"children" : [ {
								"name" : "Class 21",
								"attr" : {
									"id" : "757486",
									"cid" : "4",
									"tcl" : "4"
								}
							}, {
								"name" : "Class 22",
								"attr" : {
									"id" : "757498",
									"cid" : "4",
									"tcl" : "4"
								}
							}, {
								"name" : "Class 23",
								"attr" : {
									"id" : "757499",
									"cid" : "4",
									"tcl" : "4"
								}
							}, {
								"name" : "Class 24",
								"attr" : {
									"id" : "997206",
									"cid" : "4",
									"tcl" : "4"
								}
							} ]
						} ]
					} ]
				} ]
			};
		})
		.directive(
				'tree',
				function() {
					return {
						restrict : 'E', // tells Angular to apply this to only html tag
						// that is <tree>
						replace : true, // tells Angular to replace <tree> by the whole
						// template
						scope : {
							t : '=src' // create an isolated scope variable 't' and
						// pass 'src' to it.
						},
						template : '<ul><branch ng-repeat="c in t.children" src="c"></branch></ul>'
					};
				})

		.directive('branch', function($compile) {
			return {
				restrict : 'E', // tells Angular to apply this to only html tag that is <branch>
				replace : true, // tells Angular to replace <branch> by the whole template
				scope : {
					b : '=src' // create an isolated scope variable 'b' and pass 'src' to it.
				},
				template : '<li><a>{{ b.name }}</a></li>',
				link : function(scope, element, attrs) {
					// // Check if there are any children, otherwise we'll have infinite
					// execution

					var has_children = angular.isArray(scope.b.children);

					// // Manipulate HTML in DOM
					if (has_children) {
						element.append('<tree src="b"></tree>');

						// recompile Angular because of manual appending
						$compile(element.contents())(scope);
					}

					// // Bind events
					element.on('click', function(event) {
						event.stopPropagation();

						if (has_children) {
							element.toggleClass('collapsed');
						}
					});
				}
			};
		}).controller('SimpleController', function($scope) {

			$scope.users = [ {
				firstName : 'kingshuk',
				lastName : 'chakraborty',
				email : 'kingshuk.c@tcs.com'
			}, {
				firstName : 'johnny',
				lastName : 'depp',
				email : 'jdepp@gmail.com'
			}, {
				firstName : 'victor',
				lastName : 'hugo',
				email : 'chugo@hotmail.com'
			} ];
		}).config(function($routeProvider, $locationProvider) {
			$routeProvider.when('/', {
				templateUrl : '/views/Userbasictable.html',
				controller : 'SimpleController'
			}).when('/user', {
				templateUrl : '/views/View1.html',
				controller : 'SimpleController'
			});
			// configure html5 to get links working on jsfiddle
			$locationProvider.html5Mode(true);
		});
