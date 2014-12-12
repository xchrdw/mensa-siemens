"use strict";

var app = angular.module('mensaApp', [
    'ngRoute',
    'mensaControllers'
]);

app.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/restaurants', {
                templateUrl: 'partials/restaurant-list.html',
                controller: 'RestaurantListController'
            }).
            when('/menus/:restaurantId', {
                templateUrl: 'partials/menu-list.html',
                controller: 'MenuListController'
            }).
            otherwise({
                redirectTo: '/restaurants'
            });
    }]);