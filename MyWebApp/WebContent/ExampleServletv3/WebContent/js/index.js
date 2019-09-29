/* Our main application module is defined here using a single controller which will initiate its scope
and define some behavior.
This module further depends on an helper module 'txtHighlight'.
*/
angular.module('inTableSearchApp',['txtHighlight'])
	.controller('inTableSearchController', ['$scope','$http', 'highlightText', function($scope,$http,highlightText) {
	
    $scope.query = "";//this variable will hold the user's query
	
	//obtain some dataset online
	//$http is AngularJS way to do ajax-like communications
	$http.get("http://localhost:8080/ExampleServletv3/customers") ///name/Alfreds Futterkiste
			.success(function(response) {
			   $scope.records = response;
			   $scope.result = $scope.records;//this variable will hold the search results
			});
			
	//this method will be called upon change in the text typed by the user in the searchbox
	$scope.search = function(){
	    if (!$scope.query || $scope.query.length == 0){
		    //initially we show all table data
			$scope.result = $scope.records;
		}else{
		    var qstr = $scope.query.toLowerCase();
			$scope.result = [];
			for (x in $scope.records){
				//check for a match (up to a lowercasing difference)
				if ($scope.records[x].Name.toLowerCase().match(qstr) ||
					$scope.records[x].City.toLowerCase().match(qstr) ||
					$scope.records[x].Country.toLowerCase().match(qstr))
				{
					$scope.result.push($scope.records[x]); //add record to search result
				}
			}
	   }
	};
	
	//delegate the text highlighting task to an external helper service 
	$scope.hlight = function(text, qstr){
		return highlightText.highlight(text, qstr);
	};
	
}]);

