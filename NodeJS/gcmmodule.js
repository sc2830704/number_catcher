var gcm = require('node-gcm');
var message = new gcm.Message();
var sender = new gcm.Sender('AIzaSyBxAVsXo6h9hiA9HNVII7yL_KW5lpwyPyQ');
var registrationIds = [];

message.addData('title','智慧排隊系統 ');
message.addData('message','Your turn!!!!');
message.addData('description','下一號即將輪到您的號碼，請準備前往櫃檯');
/*
message.collapseKey = 'demo';
message.delayWhileIdle = true;
message.timeToLive = 3;
 */
// At least one token is required - each app will register a different token
//registrationIds.push('APA91bFUf8Gb1FmVYjeLGH1zmO5UIJmetDm47N-S_8cN2P1aAg0Kd2EBO1IPXWTSzS72PiqLRXWI_HpHuFiuXVa0uhFdlXzrqG2qougX6w-HosVB5KGyMzDUcoraQP-n54CYpcM9haroxvKQASX8qtydKwZsYI9gXw');
/*
registrationIds.push('APA91bHBQgGMDE9xEIhhYugql7Hushmy4hJCFz6DtD5LqOTvecXU3LU16akLxg94tqLTggAnjtHX4lS2XFVjQqVyRP58B_XNHM51G05FqGt4ge1TDwAZEuCsvlFoq6CsbCALhDC9hThv5u1Zg2ltG8G5azIBBBA2og');
*/
	
module.exports = function (){
	
	this.Numbersend = function(regID){
		registrationIds = [];
		registrationIds.push(regID);
		message.addData('title',"號顧客您好!");		//+3
		message.addData('data','3'); //暫定 有三號 三分鐘
		message.addData('description','再三號即將輪到您的號碼，請準備前往櫃檯');
		
		sender.send(message, registrationIds, 4, function (result) {
	//		console.log(result);
	});	
	}
	this.Distancesend = function(regID,data,currentNumber){
		//console.log(data);
		registrationIds = [];
		registrationIds.push(regID);
		message.addData('title',"號顧客您好!");
		message.addData('data',data);
		message.addData('description','剩'+data+'號就輪到你了，可以前往郵局了');
		sender.send(message, registrationIds, 4, function (result) {
	//		console.log(result);
	});	
	}
	
	
}
	