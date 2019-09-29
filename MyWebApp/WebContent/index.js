var enteringApp = angular.module('chatMeApp', []);

enteringApp
		.controller(
				'chatMeController',
				function($scope, $http) {
					$scope.thisImageURL = '';
					$scope.thisUserNickname = '';
					$scope.thisUsername = '';
					$scope.authenticationError = -1;
					$scope.currentStateOfUse = 0;
					// at login screen
					$scope.AllPublicChannelList = [];
					$scope.flag = 0;
					$scope.isSearching = false;
					$scope.chatErrorCode = -1;
					$scope.currentChannelMessageList = []; // list of messages
					// of the current channel
					$scope.publicType = "public";
					$scope.privateType = "private";
					$scope.currentChannelName = "pick a channel";		
					$scope.privateChannelType = "private";
					$scope.publicChannelType = "public";
					$scope.currentChannelId = 0;
					$scope.channelCreated = '';
					$scope.lastSearchQuery = '';
					
					$scope.channel = {
							'channelId' : '',
							'channelName' : '',
							'channelDescription' : '',
							'type' : '',
							'mentions' : '',
							'notifications' : ''
					};

					$scope.message = {
						'messageId' : '',
						'channelId' : '',
						'userName' : '',
						'nickName' : '',
						'photo' : '',
						'sentTime' : '',
						'content' : '',
						'parentMsgID' : ''
					};

					$scope.imageSource = "images/default_photo.jpg";
					// method to send log-in form to the server
					$scope.sendLogInForm = function() {
						if (typeof $scope.logInUsername == 'undefined'
								|| $scope.logInUsername.length == 0
								|| typeof $scope.logInPassword == 'undefined'
								|| $scope.logInPassword.length == 0) {
							$scope.authenticationError = -1;
							return;
						}
						if (doesContainSpecialChars($scope.logInUsername) != 1) {
							var logInData = {
								'username' : $scope.logInUsername,
								'password' : $scope.logInPassword
							}
							$http
									.post(
											"http://localhost:8080/MyWebApp/LogIn",
											JSON.stringify(logInData))
									.then(
											function(response) {
												var status = response.data.status;
												if (status == 0) {
													// authentication success
													$scope.thisImageURL = response.data.imageURL;
													$scope.thisUserNickname = response.data.nickname;
													$scope.thisUsername = $scope.logInUsername;
													$scope.gotoChannelsPage();
												} else {
													$scope.authenticationError = 101;
												}
											});
						} else { // username contains special characters
							$scope.authenticationError = 100;
						};
					};

					// method to send the sign up form to the server
					$scope.sendSignUpForm = function() {
						var imageURL = $scope.signUpImage;
						if ((!imageURL) || (imageURL == 'undefined')
								|| (imageURL == null) || (imageURL == ""))
							imageURL = "images/default_photo.jpg";
						$scope.authenticationError = validateSignUpForm(
								$scope.signUpUsername, $scope.signUpPassword,
								$scope.signUpNickname, $scope.signUpImage,
								$scope.signUpdescription);
						if ($scope.authenticationError == 0) {
							var signUpData = {
								'username' : $scope.signUpUsername,
								'password' : $scope.signUpPassword,
								'nickname' : $scope.signUpNickname,
								'description' : $scope.signUpdescription,
								'image' : imageURL
							};
							$http
									.post(
											"http://localhost:8080/MyWebApp/SignUp",
											JSON.stringify(signUpData))
									.then(
											function(response) {
												var status = response.data.status;
												if (status == 1) {
													$scope.thisImageURL = imageURL;
													$scope.thisUserNickname = $scope.signUpNickname;
													$scope.thisUsername = $scope.signUpUsername;
													$scope.authenticationError = 20; // signup
													$scope.gotoChannelsPage(); // going
												} else if (status == 2) {
													$scope.authenticationError = 120;
												} else {
													$scope.authenticationError = 103;
												};
											});
						};
					};

					$scope.gotoNewChannel = function(channelId,channelName,channelType) {
						$scope.chatErrorCode=4000;
						$scope.currentChannelMessageList = [];
						$scope.currentStateOfUse = 1;
						$scope.chatErrorCode = 0;
						$scope.updateExitTime();
						console.log($scope.currentStateOfUse);
						$scope.getUserPublicChannels();
						$scope.getUserPrivateChannels();
						
						//$scope.updateAllNotificationsAndMentions();
						
						//new code
						$scope.currentChannelId = channelId;
						$scope.currentChannelName = channelName;
						$scope.currentChannelType = channelType;
						$scope.getCurrentChannelMessages(channelIdPressed);
						// update entry time
						$scope.updateEntryTime();
						// init web socket
						$scope.initWebSocket();
						$scope.digest();
					};
					
					$scope.gotoChannelsPage = function() {
						$scope.currentStateOfUse = 1;
						$scope.chatErrorCode = 0;
						$scope.getUserPublicChannels();
						$scope.getUserPrivateChannels();
						$scope.digest();
					};

					$scope.updateProfilePic = function() {
						if ($scope.signUpImage) {
							$scope.imageSource = $scope.signUpImage;
							$scope.hasImage = 1;
						} else {
							$scope.imageSource = "images/default_photo.jpg";
							$scope.hasImage = 0;
						};
					};

					// welcome screen logic - currently fiktiv functions
					$scope.searchChannel = function(searchChannelQuery) {
						$scope.updateExitTime();
						$scope.currentStateOfUse = 2; // user searching
						$scope.chatErrorCode = 0;
						if ($scope.websocket != null) {
							$scope.terminateWebSocket();
						}
						// channel
						$scope.isSearching = true;
						$scope.searchAllPublicChannels(searchChannelQuery);
						$scope.lastSearchQuery = angular.copy(searchChannelQuery);
					};

					// goes to channel clicked by the user.
					$scope.goToChannel = function(channelIdPressed, channelName, channelType) {
						if ($scope.currentChannelId != channelIdPressed) {
							$scope.updateExitTime();
							if ($scope.websocket != null) {
								$scope.terminateWebSocket();
							}
							$scope.currentChannelId = channelIdPressed;
							$scope.currentChannelName = channelName;
							$scope.currentChannelType = channelType;
							$scope.getCurrentChannelMessages(channelIdPressed);
							// update entry time
							$scope.updateEntryTime();
							// init web socket
							$scope.initWebSocket();
							$scope.chatErrorCode = 0; // no errors present
							$scope.digest();
						}
					};

					$scope.gotoCreateChannelPage = function() {
						$scope.updateExitTime();
						$scope.currentStateOfUse = 3;
						$scope.chatErrorCode = 0;
						if ($scope.websocket != null) {
							$scope.terminateWebSocket();	
						}
						console.log('current state is: '
								+ $scope.currentStateOfUse);
						
					};

					// this function loads the current user's public channels.
					$scope.getPublicChannels = function() {
						$http
								.get(
										"http://localhost:8080/MyWebApp/publicChannels")
								.success(function(response) {
									$scope.records = response;
									$scope.AllPublicChannelList = $scope.records;
								});
					};

					// this function returns a list of all public channels
					// available on the
					// server.
					$scope.searchAllPublicChannels = function(
							searchChannelQuery) {
						if (searchChannelQuery != null
								&& searchChannelQuery != "") {
							$http
									.get(
											"http://localhost:8080/MyWebApp/SearchPublicChannels/channelName/"
													+ searchChannelQuery)
									.success(
											function(response) {
												$scope.records = response;
												$scope.searchResults = $scope.records;
												console
														.log('search results of channels are: '
																+ $scope.searchResults);
											});
						} else {
							$http.get(
									"http://localhost:8080/MyWebApp/publicChannels/userName/"
											+ $scope.thisUsername)
							.success(function(response) {
								$scope.records = response;
								$scope.searchResults = $scope.records;
							});
						};
					};

					// this function loads the current user's private channels.
					$scope.getUserPrivateChannels = function() {
						$http
								.get(
										"http://localhost:8080/MyWebApp/GetPrivateChannels/userName/"
												+ $scope.thisUsername)
								.success(
										function(response) {
											$scope.records = response;
											$scope.userPrivateChannelList = $scope.records;
											// $scope.displayNamesPrivateChannels();
										});
					};

					$scope.displayNamesPrivateChannels = function() {
						var total = $scope.userPrivateChannelList.length;
						for (var i = 0; i < total2 && index == -1; i++) {
							var name = $scope.userPrivateChannelList[i].channelName;
							var res = name.split(",");
							var user1 = res[0];
							var user2 = res[1];
							if ($scope.thisUsername === user1) {
								$scope.userPrivateChannelList[i].channelName = user2;
							} else {
								$scope.userPrivateChannelList[i].channelName = user1;
							};
						}
					};

					// same as before, only with public channels
					$scope.getUserPublicChannels = function() {
						$http
								.get(
										"http://localhost:8080/MyWebApp/GetPublicChannels/userName/"
												+ $scope.thisUsername)
								.success(
										function(response) {
											$scope.records = response;
											$scope.userPublicChannelList = $scope.records;
											console
													.log('Public channels are: '
															+ $scope.userPublicChannelList);
										});
					};

					// gets all messages shown in the channel - change this to
					// last 10 messages
					// later
					$scope.getCurrentChannelMessages = function(channelIdPressed) {
						$scope.currentChannelMessageList = [];
						$http
								.get(
										"http://localhost:8080/MyWebApp/GetMessages/channelId/"
												+ channelIdPressed
												+ "/username/"
												+ $scope.thisUsername)
								.success(
										function(response) {
											$scope.records = response;
											$scope.currentChannelMessageList = $scope.records;
											
											var total = $scope.currentChannelMessageList.length;
											for(var i=0;i<total;i++){
												var timestamp = $scope.currentChannelMessageList[i].sentTime;								
												if(timestamp.hourOfDay < 10){
													timestamp.hourOfDay = "0"+timestamp.hourOfDay;
												}
												if(timestamp.minute < 10){
													timestamp.minute = "0"+timestamp.minute;
												}
												if(timestamp.second < 10){
													timestamp.second = "0"+timestamp.second;
												}
												if(timestamp.dayOfMonth < 10){
													timestamp.dayOfMonth = "0"+timestamp.dayOfMonth;
												}
												if(timestamp.month < 10){
													timestamp.month = "0"+timestamp.month;
												}
												
												var string = timestamp.year.toString();
												timestamp.year = string.slice(string.length-2,string.length);
												
												$scope.currentChannelMessageList[i].sentTime = timestamp.hourOfDay+':'+timestamp.minute+':'+timestamp.second+" "+timestamp.dayOfMonth+"/"+timestamp.month+"/"+timestamp.year;
											}
											
											console
													.log('messages are: '
															+ $scope.currentChannelMessageList);
										});
						$scope.chatErrorCode = 0;
					};

					
					
					// this function subscribes the current user to the public
					// channel he
					// pressed.
					$scope.subscribeToChannel = function(inputChannelId,
							inputChannelName) {
						console.log('starting sub..');
						var subscriptionData = {
							'channelId' : inputChannelId,
							'username' : $scope.thisUsername
						}
						$http
								.post(
										"http://localhost:8080/MyWebApp/Subscribe",
										JSON.stringify(subscriptionData))
								.then(
										function(response) {
											var status = response.data.status;
											if (status == 0) {
												// channel sub succeeded
												$scope.channelSubscribedTo = inputChannelName;
												$scope.chatErrorCode = 2000;
												console.log('success');
												// $scope.currentStateOfUse = 4;
											} else {
												// channel creation failed -
												// channel already exists
												$scope.chatErrorCode = 14;
												console.log('failed sub');
											};
										});
					};
					

					$scope.subscribeUserToChannel = function(inputUsername,
							inputChannelId, inputChannelName) {
						console.log('starting sub..');
						var subscriptionData = {
							'channelId' : inputChannelId,
							'username' : inputUsername
						}
						$http.post("http://localhost:8080/MyWebApp/Subscribe",
								JSON.stringify(subscriptionData)).then(
								function(response) {
									var status = response.data.status;
									if (status == 0) {
										console.log('success');
									} else {
										console.log('failed sub');
									};
								});
					};

					// this function unsubscribes the current user from the
					// channel he pressed.
					$scope.unSubscribeFromChannel = function(inputChannelId,
							inputChannelType) {
						var unSubscriptionData = {
							'channelId' : inputChannelId,
							'username' : $scope.thisUsername
						}
						$http
								.post(
										"http://localhost:8080/MyWebApp/UnSubscribe",
										JSON.stringify(unSubscriptionData))
								.then(
										function(response) {
											var index;
											index = $scope
													.indexOfChannelById(inputChannelId);
											if (inputChannelType == "public") {
												$scope.userPublicChannelList
														.splice(index, 1);
											} else {
												$scope.userPrivateChannelList
														.splice(index, 1);
											}
											// unsub succeeded
											$scope.chatErrorCode = 3;
											$scope.currentChannelName = "unsub from "
													+ $scope.currentChannelName
													+ " successful";
											
											if(inputChannelType == "private"){
												$scope.userPrivateChannelList.splice($scope.indexOfChannelById(inputChannelId),1);
											}else{
												$scope.userPublicChannelList.splice($scope.indexOfChannelById(inputChannelId),1);
											};
											$scope.currentChannelMessageList = [];
										});
					};

					$scope.indexOfChannelById = function(inputChannelId) {
						var total1 = $scope.userPublicChannelList.length;
						var total2 = $scope.userPrivateChannelList.length;

						var index = -1;

						for (var i = 0; i < total1 && index == -1; i++) {
							if ($scope.userPublicChannelList[i].channelId == inputChannelId) {
								index = i;
							}
						}

						for (var i = 0; i < total2 && index == -1; i++) {
							if ($scope.userPrivateChannelList[i].channelId == inputChannelId) {
								index = i;
							}
						}

						console.log('index is: ' + index);
						return index;
					};

					// check if current user is subscribed
					$scope.isUserSubscribed = function(inputChannelId) {
						var total1 = $scope.userPublicChannelList.length;
						var total2 = $scope.userPrivateChannelList.length;
						var flag = 0;

						for (var i = 0; i < total1 && flag != 1; i++) {
							if ($scope.userPublicChannelList[i].channelId == inputChannelId) {
								flag = 1;
							}
						}

						for (var i = 0; i < total2 && flag != 1; i++) {
							if ($scope.userPrivateChannelList[i].channelId == inputChannelId) {
								flag = 1;
							}
						}

						console.log('flag is: ' + flag);
						return flag;
					};

					// sends a message to the channel
					$scope.updateMessageAtTable = function(msgContent) {
						var messageDetails = {
							'messageId' : 0, // take care of this in java code.
							'channelId' : $scope.currentChannelId,
							'userName' : $scope.thisUsername,
							'nickName' : $scope.thisUserNickname,
							'photo' : $scope.thisImageURL,
							'content' : msgContent,
							'parentMsgID' : 0
						}
						
						$scope.websocket.send(JSON.stringify(messageDetails));
						
						$http
								.post(
										"http://localhost:8080/MyWebApp/SendMessage",
										JSON.stringify(messageDetails))
								.then(
										function(response) {
											console
													.log('after new msg channel addition:');
											console
													.log($scope.currentChannelMessageList);
											$scope.digest();
										});
						
					};

					// this function creates a channel and signs the current
					// user into it
					// used for public currently.
					$scope.createNewPublicChannel = function(inputChannelName,
							inputChannelDescription) {
						var newChannelData = {
							'channelId' : 0,
							'channelName' : inputChannelName,
							'channelDescription' : inputChannelDescription,
							'type' : "public",
							'mentions' : '0',
							'notifications' : '0'
						}
						$http
								.post(
										"http://localhost:8080/MyWebApp/CreateChannel",
										JSON.stringify(newChannelData))
								.then(
										function(response) {
											// channel creation succeeded
											$scope.chatErrorCode = 1000;
											// subscribe user to new channel.
											var inputChannelId = response.data.newChannelId;
											var inputChannelName = response.data.newChannelName;
											$scope.subscribeToChannel(inputChannelId,inputChannelName);
											$scope.channelCreated = inputChannelName;
											//$scope.gotoChannelsPage();
											//$scope.goToChannel(inputChannelId,inputChannelName,publicChannelType);
											$scope.gotoNewChannel(inputChannelId,inputChannelName,$scope.publicChannelType);
											$scope.digest();
										});
					};

					// this function creates a private channel between 2 users.
					// used when someone clicks on the name of the other one
					$scope.createNewPrivateChannel = function(userA, userB) {
						var newChannelData = {
							'channelId' : 0,
							'channelName' : userA + "," + userB,
							'channelDescription' : userA + "," + userB,
							'type' : "private",
							'mentions' : '0',
							'notifications' : '0'
						}
						$http
								.post(
										"http://localhost:8080/MyWebApp/CreateChannel",
										JSON.stringify(newChannelData))
								.then(
										function(response) {
											// channel creation succeeded
											$scope.chatErrorCode = 1;
											// subscribe user to new channel.
											var inputChannelId = response.data.newChannelId;
											var inputChannelName = response.data.newChannelName;
											$scope.subscribeToChannel(
													inputChannelId,
													inputChannelName);
											$scope.subscribeUserToChannel(
													userB, inputChannelId,
													inputChannelName);
											
											$scope.gotoNewChannel(inputChannelId,inputChannelName,$scope.privateChannelType);
											$scope.digest();
										});
					};

					// WEB SOCKET FUNCTIONS FOLLOW
					$scope.initWebSocket = function() {
						var wsUri = "ws://" + window.location.host
								+ "/MyWebApp/chat/" + $scope.thisUsername
								+ "/cha" + $scope.currentChannelId;
						$scope.websocket = new WebSocket(wsUri);
						$scope.websocket.onopen = function(evt) {
							console.log("Connected to Chat Server...");
						};

						$scope.websocket.onmessage = function(evt) {
							var object = JSON.parse(evt.data);
							if ($scope.flag == 0 && object.channelId == $scope.currentChannelId) {
								object.sentTime = $scope.getDate();
								$scope.currentChannelMessageList.push(angular.copy(object));
								console.log('messages are: '
										+ $scope.currentChannelMessageList);
							} else {
								$scope.flag = 0;
							};
							//$scope.updateAllNotificationsAndMentions();
						};
						$scope.websocket.onerror = function(evt) {
							console.log('ERROR: ' + evt.data);
						};

						$scope.websocket.onclose = function(evt) {
							$scope.websocket = null;
						};
					};

					$scope.sendMessage = function() {
						if ($scope.messageToSendContent.length < 500) {

								$scope.message.nickName = $scope.thisUserNickname;
								$scope.message.photo = $scope.thisImageURL;
								$scope.message.content = $scope.messageToSendContent;
								$scope.message.sentTime = $scope.getDate(); //
								$scope.currentChannelMessageList.push(angular.copy($scope.message));
								$scope.flag = 1;
								$scope.updateMessageAtTable($scope.messageToSendContent);
							$scope.messageToSendContent = '';
							$scope.digest();
						} else {
							$scope.chatErrorCode = 500; // msg too long
						};
					};
					
					
					$scope.getDate = function() {
				        var d = new Date();
				        
				        var year = d.getFullYear();
				        
				        var month = d.getMonth();
				      
				        var day = d.getDay();	
				        
				        var second = d.getSeconds();
				        
				        var minute = d.getMinutes();
				        
				        var hour = d.getHours();
				        
				        if(month < 10){
				        	month = "0"+month;
						}
						if(day < 10){
							day = "0"+day;
						}
						if(second < 10){
							second = "0"+second;
						}
						if(minute < 10){
							minute = "0"+minute;
						}
						if(hour < 10){
							hour = "0"+hour;
						}
						
						var string = year.toString();
						year = string.slice(string.length-2,string.length);
	        
				        return hour + ":" + minute + ":" + second+" "+ day +"/"+month+"/"+year; //todo: check
				    };

					$scope.terminateWebSocket = function() {
						$scope.websocket.close();
						$scope.chatErrorCode = 404; // socket doesn't exist.
						console.log("Logged out...");
					};

					$scope.updateEntryTime = function() {
						if ($scope.currentChannelId) {
							var subscriptionData = {
								'channelId' : $scope.currentChannelId,
								'username' : $scope.thisUsername
							}
							$http
									.post(
											"http://localhost:8080/MyWebApp/UpdateEntryTime",
											JSON.stringify(subscriptionData));
						}
					};

					$scope.updateExitTime = function() {
						if ($scope.currentChannelId) {
							var subscriptionData = {
								'channelId' : $scope.currentChannelId,
								'username' : $scope.thisUsername
							}
							$http
									.post(
											"http://localhost:8080/MyWebApp/UpdateExitTime",
											JSON.stringify(subscriptionData));
							$scope.currentChannelId = 0;
						}
					};

					$scope.updateAllNotificationsAndMentions = function() {
						if($scope.userPublicChannelList){
							var total1 = $scope.userPublicChannelList.length;
							for (var i = 0; i < total1; i++) {
								$scope.getNotificationsForChannel($scope.userPublicChannelList[i]);
								$scope.getMentionsForChannel($scope.userPublicChannelList[i]);
							}
						}
						
						if($scope.userPrivateChannelList){
						var total2 = $scope.userPrivateChannelList.length;
							for (var i = 0; i < total2; i++) {
								$scope.getNotificationsForChannel($scope.userPrivateChannelList[i]);
								$scope.getMentionsForChannel($scope.userPrivateChannelList[i]);
							}
						}
						console.log("updateAllNotifications");
					};
					
					

					$scope.getNotificationsForChannel = function(inputChannel) {
						$http.get(
								"http://localhost:8080/MyWebApp/GetNotifications/channelId/"
										+ inputChannel.channelId + "/userName/"
										+ $scope.thisUsername).success(
								function(response) {
									console.log("getNotificationsForChannel");
									var notifNum = response;
									inputChannel.notifications = notifNum;
								});
					};

					$scope.getMentionsForChannel = function(inputChannel) {
						$http.get(
								"http://localhost:8080/MyWebApp/GetMentions/channelId/"
										+ inputChannel.channelId + "/userName/"
										+ $scope.thisUserNickname).success(
								function(response) {
									console.log("getMentionsForChannel");
									var mentionNum = response;
									inputChannel.mentions = mentionNum;
								});
					};
				});

function doesContainSpecialChars(str) {
	var i;
	var flag = 0;
	for (i = 0; i < str.length; i++) {
		var asciiCode = str.charAt(i);
		if ((asciiCode > 'Z' && asciiCode < 'a') || asciiCode > 'z'
				|| asciiCode < '0' || (asciiCode > '9' && asciiCode < 'A')) {
			flag = 1;
		}
	}
	return flag;
}

function validateSignUpForm(usernameToValidate, passwordToValidate,
		nicknameToValidate, urlToValidate, descriptionToValidate) {	
	if ((typeof usernameToValidate == 'undefined')
			|| (usernameToValidate.length === 0)) {
		return 102;
	}
	if (doesContainSpecialChars(usernameToValidate)) {
		return 104;
	}
	if (usernameToValidate.length > 10) {
		return 105;
	}
	if (passwordToValidate.length > 8) {
		return 106;
	}
	if ((typeof passwordToValidate == 'undefined')
			|| (passwordToValidate.length === 0)) {
		return 107;
	}
	if ((typeof nicknameToValidate == 'undefined')
			|| (nicknameToValidate.length === 0)) {
		return 108;
	}
	if (nicknameToValidate.length > 20) {
		return 109;
	}
	if (typeof urlToValidate != 'undefined') {
		if (urlToValidate.length > 3000) {
			return 111;
		}
	}
	if (typeof descriptionToValidate != 'undefined') {
		if (descriptionToValidate.length > 50) {
			return 110;
		}
	}
	return 0;
}
