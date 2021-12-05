var app = angular.module("SmartEnvManagement", ["ngRoute"]);
 

// Controller Part




app.controller("SmartEnvController", function($scope, $http, $window) {
    $scope.sensorMeasures = [];
    $scope.sensorMeasureForm = {
        id: 1,
        empNo: "",
        unit: ""
    };
    $scope.currentTemperature = {
        id: -1,
        measureType: "temperature",
        unit: "degree_celsius"
    };
 	 $scope.sensorProgrammingForm = {
        id: -1,
        measureType: "temperature",
        unit: "degree_celsius"
    };
    $scope.reloadPage = function() {
 	 	//$window.alert("reloadPage");
    	$window.location.reload();
	}

	$scope.listOrders = [
		 { value: 'up',  label: 'up',}
		,{ value: 'down', label: 'down',}
		,{ value: 'on', label: 'on',}
		,{ value: 'off', label: 'off',}
		,{ value: 'right', label: 'right',}
		,{ value: 'left', label: 'left',}
	];


	$scope.middleValue = 20;
 
    // Now load the data from server
   _refreshAll();    
 
    // HTTP POST/PUT methods for add/edit sensorMeasure  
    // Call: http://localhost:8080/smartenv
    $scope.submitSensorMeasure = function() {
        var method = "";
        var url = "";
        if ($scope.sensorMeasureForm.id == -1) {
            method = "POST";
            url = '/smartenv';
        } else {
            method = "PUT";
            url = '/smartenv';
        }
 
        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.sensorMeasureForm),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };


	$scope.submitOrder = function() {
		console.log("submitOrder", $scope.selected);
        var method = "";
        var url = "";
        method = "POST";
        url = '/smartenv/order';
        $http({
            method: method,
            url: url,
            data: $scope.selected.value,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
        console.log("submitOrder end");
        
    };

    $scope.submitTemperatureProgramming = function() {
		console.log("submitTemperatureProgramming", $scope.sensorProgrammingForm);
        var method = "";
        var url = "";
        method = "POST";
        url = '/smartenv/programming';
        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.sensorProgrammingForm),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
 
    $scope.createSensorMeasure = function() {
        _clearFormData();
    }
 
    // HTTP DELETE- delete sensorMeasure by Id
    // Call: http://localhost:8080/smartenv/{id}
    $scope.deleteSensorMeasure = function(sensorMeasure) {
        $http({
            method: 'DELETE',
            url: '/smartenv/' + sensorMeasure.id
        }).then(_success, _error);
    };
 
    // In case of edit
    $scope.editSensorMeasure = function(sensorMeasure) {
    	console.log("editSensorMeasure", sensorMeasure);
        $scope.sensorMeasureForm.id = sensorMeasure.id;
        $scope.sensorMeasureForm.measureType = sensorMeasure.measureType;
        $scope.sensorMeasureForm.unit = sensorMeasure.unit;
        $scope.sensorMeasureForm.date = sensorMeasure.date;
        $scope.sensorMeasureForm.value = sensorMeasure.value;
    };

    function _refreshAll() {
		_refreshCurrentTemperature();
		_refreshSensorMeasureHistory();
    }

   // Private Method
    // HTTP GET- get all sensorMeasure collection
    // Call: http://localhost:8080/smartenv
    function _refreshCurrentTemperature() {
        $http({
            method: 'GET',
            url: '/smartenv/current_temperature'
        }).then(
            function(res) { // success
                $scope.currentTemperature = res.data;
                $scope.middleValue = 20;
                if( $scope.currentTemperature.targetValue > -50) {
					$scope.sensorProgrammingForm.value = $scope.currentTemperature.targetValue;
					$scope.middleValue = $scope.currentTemperature.targetValue;
                }
                console.log("_refreshCurrentTemperature", $scope.currentTemperature );                
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);                
            }
        );
    }
    
 
    // Private Method  
    // HTTP GET- get all sensorMeasure collection
    // Call: http://localhost:8080/smartenv
    function _refreshSensorMeasureHistory() {
        $http({
            method: 'GET',
            url: '/smartenv'
        }).then(
            function(res) { // success
                $scope.sensorMeasures = res.data;
                console.log("_refreshSensorMeasureHistory", $scope.sensorMeasures );                
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);                
            }
        );
    }
 
    function _success(res) {
        _refreshAll();
        _clearFormData();
    }
 
    function _error(res) {
        var data = res.data;
        var status = res.status;
        var header = res.header;
        var config = res.config;
        alert("Error: " + status + ":" + data);
    }
 
    // Clear the form
    function _clearFormData() {
        $scope.sensorMeasureForm.id = -1;
        $scope.sensorMeasureForm.measureType = "";
        $scope.sensorMeasureForm.unit = "";
        $scope.sensorMeasureForm.date = "";
        $scope.sensorMeasureForm.value = "";
    };
});

//convert Blob to File. Pass the blob and the file title as arguments
function blobToFile(theBlob, fileName){
    //A Blob() is almost a File() - it's just missing the two properties below which we will add
    theBlob.lastModifiedDate = new Date();
    theBlob.name = fileName;
    return theBlob;
}