"use strict";

var controllers = angular.module('mensaControllers', []);


controllers.controller('GlobalController', function ($scope, $http, $rootScope) {
        $scope.loading = false;
        $rootScope.startLoading = function() {
            $scope.loading = true;
        };
        $rootScope.stopLoading = function() {
            $scope.loading = false;
        };
    });

controllers.controller('RestaurantListController', ['$scope', '$http',
    function ($scope, $http) {
        /*$http.get('/restaurants').success(function (data) {
            $scope.restaurants = data.restaurants;
        });*/
    }]);

controllers.controller('MenuListController',
    function ($scope, $http, $routeParams, $rootScope) {
        var restaurantId = parseInt($routeParams.restaurantId);
        var day = moment().hours(0).minutes(0).seconds(0).milliseconds(0);
        var cache = {};

        $scope.restaurant = "...";

        $scope.next = function () {
            day = day.clone().add(1, 'days');
            showMenu(day);
        };
        $scope.prev = function () {
            day = day.clone().subtract(1, 'days');
            showMenu(day);
        };

        function showMenu(date) {
            var key = date.format("DD.MM.YYYY");
            $scope.day = key;
            if (key in cache) {
                $scope.menu = cache[key];
                return;
            }
            $rootScope.startLoading();
            $scope.menu = [];
            $http.get('/menus/' + restaurantId, {params: {date: key}}).success(function (data) {
                $rootScope.stopLoading();
                data.menus.forEach(function (v) {
                    cache[v.date] = v;
                });
                $scope.menu = cache[key];
                $scope.restaurant = data.restaurant;
            });
        }

        setTimeout(function() {showMenu(day)}, 10);
    });

