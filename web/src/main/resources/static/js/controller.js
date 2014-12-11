"use strict";

var app = angular.module('SemWeb2015', ['ngSanitize']);

app.controller('SimpleController', function ($scope, $http, $document) {
    $scope.loading = false;
    $scope.toggleSentimentsValue = false;
    $scope.hover = false;

    $scope.update = function () {
        $scope.loading = true;
        $scope.hover = false;
        $scope.content = [];
        $scope.descriptionHtml = "";
        $scope.imageHtml = "";
        $scope.errormessage = "";
        $scope.stacktrace = "";
        $scope.loadingInfo = true;
        $scope.twitter = {};

        $('#tcdiv').empty();

        $http.get('/extract', {params: {uri: $scope.fetchUrl}}).
            success(function (data) {
                $scope.content = data.content;
                $scope.loading = false;
                drawTermCloud(data.content);
                updateTweets($scope.fetchUrl);
            }).error(function (error) {
                $scope.errormessage = error.message;
                $scope.stacktrace = error.stacktrace;
                $scope.loading = false;
            });
    };

    var wikiCache = new Map();
    
    $scope.entityMouseEnter = function($event) {
        var type = this.entity.type
        if (type == "O") {
            return;
        }

        if($.inArray(type,["PERSON", "ORGANIZATION","LOCATION","MISC"]) !== -1) {
            var hover = {};
            $scope.hover = hover;
            hover.show = false;
            hover.type = type;
            hover.name = this.entity.text;
            hover.show = true;
            hover.loadingInfo = true;

            var params = {name: this.entity.text, type: type};
            var key = params.name + "|" + params.type;
            if(wikiCache.has(key)) {
            	updateHover(wikiCache.get(key), hover);
            } else {
	            $http.get('/wikiInfo', {params: params}).
	                success(function (data) {
	                    if (data.name != $scope.hover.name || data.type != $scope.hover.type) {
	                        return; // another entity was selected before this query returned
	                    }
	                    wikiCache.set(key, data.wikiEntity);
	                    updateHover(data.wikiEntity, hover);
	                }).error(function (error) {
	                     $scope.errormessage = error.message;
	                     $scope.stacktrace = error.stacktrace;
	                     hover.loadingInfo = false;
	                 });
            }
        }

    };
    
    function updateHover(wikiEntity, hover) {
	    hover.loadingInfo = false;
	    if(!wikiEntity){
	        hover.descriptionHtml = "Unfortunately, no Match found!";
	    } else {
	        hover.descriptionHtml = wikiEntity.wikiAbstract;
	        if(typeof(wikiEntity.imageURL) != 'undefined') {
	            hover.imageUrl = wikiEntity.imageURL;
	        }
	    }
    }
    
    $scope.toggleSentiments = function() {
        $scope.toggleSentimentsValue = !$scope.toggleSentimentsValue;
    };

    $scope.test = function () {
        $scope.fetchUrl = "http://hpi.de/en/channel-teaser/studium/it-systems-"
            + "engineering-for-bachelor-master-and-phd.html";
        $scope.update();
    };

    function updateTweets(uri) {
        var twitter = { loading: true };
        $scope.twitter = twitter;
        $http.get('/searchTweets', {params: {q: $scope.fetchUrl}}).
            success(function (data) {
                twitter.tweets = data.tweets;
                twitter.loading = false;
                if (data.tweets.length === 0) {
                    twitter.message = "no tweets found";
                }
            }).error(function (error) {
                $scope.errormessage = error.message;
                $scope.stacktrace = error.stacktrace;
                twitter.loading = false;
            });
    }

    function drawTermCloud(content) {
        var outputDiv = document.getElementById('tcdiv');
        var tc = new TermCloud(outputDiv);
        var dataTable = new google.visualization.DataTable();
        dataTable.addColumn('string', 'Label');
        dataTable.addColumn('number', 'Value');
        dataTable.addColumn('string', 'Link');

        var termLimit = 20;
        var threshold = 0;
        var tfIdfRanking = content.tfIdfRanking;
        if (tfIdfRanking.length > termLimit) {
            threshold = tfIdfRanking[20].tfIdfValue;
        }
        var i = 0;
        var dict = {};
        content.textBlocks.forEach(function (block) {
            block.sentences.forEach(function (sent) {
                sent.fragments.forEach(function (frag) {
                    if (frag.tfIdfValue >= threshold && dict[frag.lemma] !== 1) {
                        dataTable.addRows(1);
                        dataTable.setValue(i, 0, frag.text);
                        dataTable.setValue(i, 1, Math.round(Math.log(frag.tfIdfValue + 1) * 1000));
                        dict[frag.lemma] = 1;
                        i++;
                    }
                })
            })
        });
        tc.draw(dataTable, null);
    }

});

jQuery(document).ready(function($) {
    $(window).scroll(function() {
    	var pos = Math.max($(document).scrollTop() - 90, 0);
        $('#hover-popup').stop().animate({"marginTop": pos + "px"});
    });

});