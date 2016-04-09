/*NumberCatcher Project
Features:
HttpServer:createServer
MySql Process:create userdata Table ,insert,delete...
SerialPort :get data from 2 serial port : NFCPort , ButtonPort

LastEditTime:2015/10/10 2pm
update and deal  method:serialListener() problem with get nfcData_Name ,nfcData_ID
new variable totalNumber  (!!!after discussion It sholud named totalNumber)

*/
﻿var fs = require('fs')
,http = require('http')
,mysql = require('./mysql')
,util = require('util');
qs = require('querystring');
var gcm = require('./gcmmodule');
var gcm = new gcm();
var express = require('express');
var app = express();
var my_sql = require('mysql')
var conn = my_sql.createConnection({
	host: 'localhost',
    user: 'root',
    password: '589321321',
    database: 'cobe'
});

socketio = require('socket.io'),
url = require("url"),
SerialPort = require("serialport").SerialPort
var i=0;
var socketServer;
var serialPort;
var portName = 'COM8'; //change this to your Arduino port
var portBtn='COM9';
var nfcData_Name,nfcData_ID;  //來自serialport的使用者資料
var btnData;		//來serialport的按鈕請求
var totalNumber = 0;	//存放總共號碼的計數
var currentNumber = 0; //存放當前號碼的計數
var isFinifshed = 0;
var db = new mysql();
var raceLocation = {lat:25.021633 ,lng:121.535217};
var clientLocation = {lat:25.021633 ,lng:121.535217};
//var rowInfo ={};
//var newInfo ={};
var post  = {id: 0 ,UUID :"0", account: 'client', number: '00',distance :'0', status:'0'}; //新資料 //test_cn => 欄位 : 資料,
var httppost ={id: 0 ,UUID :'123' , name: 'client', account: 'abc@gmail.com', birth: "2015-10-06", regID:'0'}; 
var idJson ={number: 0 }; //查特定編號
var tableName = 'numbertable_20150924'; //資料庫表格名稱
var ChangeDay; //日期改變
var regIDArray = [] ;
//
/*
var gcm = require('node-gcm');
var message = new gcm.Message();
var sender = new gcm.Sender('AIzaSyBxAVsXo6h9hiA9HNVII7yL_KW5lpwyPyQ');
var registrationIds = [];
var MyName="";
var MyregId="";
var MyBirth="";
message.addData('title',' 2 ');
message.addData('message','Your turn!!!!');
message.addData('description','已輪到您的號碼，請前往6號櫃檯');
message.collapseKey = 'demo';
message.delayWhileIdle = true;
message.timeToLive = 3;
*///
// handle contains locations to browse to (vote and poll); pathnames.
function startServer(route,handle,debug,debug2)
{
	// on request event
	function onRequest(request, response) {
	  // parse the requested url into pathname. pathname will be compared
	  // in route.js to handle (var content), if it matches the a page will
	  // come up. Otherwise a 404 will be given.
	  var pathname = url.parse(request.url).pathname;
	  console.log("Request for " + pathname + " received");
	  var content = route(handle,pathname,response,request,debug);
	}
	///////////////////////////////////資料庫表格 偵測更新
	setInterval(tableChange_day,1000);	
	console.log("DataBase start");
	setInterval(gcmDistanceService,5000);		
	console.log("GCM Distance start");
	///////////////////////////////////
	
	var httpServer = http.createServer(onRequest).listen(1337, function(){
		console.log("Listening at: http://localhost:1337");
		console.log("Server is up");
	});
	serialListener(debug);
	serialBtnListener(debug2);
	initSocketIO(httpServer,debug);
	////////////////////////////////////
	
	format = function() {
		return util.format.apply(null, arguments);
	};	

	
	server = http.createServer(function (req, res) {
	var path = url.parse(req.url, true),
		parameter = qs.parse(path.query);

	res.writeHead(200, {'Content-Type': 'text/plain'});
	formData = '';
	req.on("data", function(data) {
		return formData += data;
	});
	req.on("end", function() {
    res.writeHead(200, {"Content-Type":"text/html; charset=utf-8"});
    hpost = qs.parse(formData);
   //log(format("post=%j\n", hpost)); // 秀出收到的資料
    /////////data
	Status = hpost.Status;
    MyName = hpost.MyName;
    MyregId = hpost.MyregId;
    MyBirth = hpost.MyBirth;
    UUID = hpost.UUID;
	MyAccount = hpost.MyAccount;
	MyLongitude = hpost.MyLongitude;
	MyLatitude=hpost.MyLatitude;
	//console.log("Name : "+MyName);
	//console.log("regID : "+MyregId);
	//console.log("birth : "+MyBirth);
	//console.log("UUID : "+UUID);
	//console.log("MyAccount : "+MyAccount);
	console.log("MyLongitude : "+MyLongitude);
	console.log("MyLatitude : "+MyLatitude);
    res.write("MyName="+MyName+"<br/>");
    res.write("MyregId="+MyregId+"<br/>");
    res.write("MyBirth="+MyBirth+"<br/>");
    res.write("MyAccount="+MyAccount+"<br/>");
	res.write("UUID="+UUID+"<br/>");
	clientLocation.lat = MyLatitude;
	clientLocation.lng = MyLongitude;
	//httppost.account=Myaccount;
	httppost.name=MyName;
	httppost.regID=MyregId;	
    httppost.birth = MyBirth;
    httppost.UUID = UUID;
	httppost.account = MyAccount;
	
	 if(MyregId != undefined) //初始註冊時
	 {
		//查詢有無紀錄帳戶 UUID
		db.findOneByUUID('user',httppost.UUID,function(ret){ 		
			UUID=httppost.UUID;						
			//console.log(ret);			//查詢結果
				if(!ret) //沒有 新增帳號
				{
						//console.log(httppost.id);
						//console.log(httppost.account);
						//console.log(httppost.regID);
						db.insert('user' , httppost ,function(ret12){				  
							console.log(ret12);											
						});
						console.log("註冊成功");
				}
				else   // 有 更新帳號
				{
					
					if(httppost.regID != '')  //更新帳號的redID 不可為空白
							{
								console.log("update UUID="+UUID);										
								db.modifyregID('user' ,httppost.regID, httppost.UUID ,function(cb){ 					
								// 使原本帳戶account的regID 更新為新的regID
								//  console.log(cb);			
								});								
							}
							
				}
			});	
	}
		else if (Status == "1")
		{
			 db.findOneByUUID(tableName,httppost.UUID,function(ret){ 
								
				if(!ret) //沒有  那問題大了
						{
							console.log("有問題!!");
								
						}
					else   // 有 更新狀態(拒絕通知)
						{
							
							console.log("disable status exist UUID=" + UUID);
								db.modifyStatus(tableName ,Status, httppost.UUID ,function(cb){ 					
										console.log("update Status="+cb.Status);										
										});							
						}								
				});
		}
		else if (MyLongitude.length > 2) //抽卡後 偵測到有位置時
		{
			 db.findOneByUUID(tableName,httppost.UUID,function(ret){ 
					
					if(!ret) //沒有  那問題大了
				{
						console.log("有問題!!");
						
				}
				else   // 有 更新地址
				{
					rdistance = distHaversine(raceLocation,clientLocation);
					httppost.distance = rdistance ;
					//console.log("exist UUID=" + UUID);
						db.modifydistance(tableName ,httppost.distance, httppost.UUID ,function(cb){ 					
								//console.log("update distance="+httppost.distance);										
								// 使原本帳戶account的regID 更新為新的regID
								//  console.log(cb);			
								});							
				}

					
			 });
		}
  
	
   res.end();	
});	
});
  
log = console.log;
ip   = "192.168.1.10"; //開放外部連線IP不能鎖定
port = 7777;      //小烏龜的PORT自己設定

server.listen(port, ip);


    // Call the start function which will connect to the database.
  //  return db.start();  
	////////////////////////////////////
}

function initSocketIO(httpServer,debug)
{
	socketServer = socketio.listen(httpServer);
	if(debug == false){
		socketServer.set('log level', 1); // socket IO debug off
	}
	socketServer.on('connection', function (socket) {
		console.log("user connected");
		socket.emit('onconnection', {nfc:totalNumber, button:currentNumber});

		socket.on('saturate',function(count){
			console.log("sat");
			serialBtn.write(count);
		});


    });
}

// Listen to serial port
function serialListener(debug)
{
    var receivedData = "";
    serialPort = new SerialPort(portName, {
        baudrate: 9600,
        // defaults for Arduino serial communication
         dataBits: 8,
         parity: 'none',
         stopBits: 1,
         flowControl: false});
		 
		//-------------------processing  serialport data event------------------------
    serialPort.on("open", function () {
    			console.log('Open Serial NFC Cmmuncation');
				
    			// Listens to incoming data
    			serialPort.on('data', function(data) {
    					receivedData += new Buffer(data,'utf8').toString();
					
     					//serial port資料接收判斷
    					if (receivedData .indexOf('{') >= 0 && receivedData .indexOf('}') >= 0) {
           				nfcData_ID = receivedData .substring( receivedData .indexOf('{') + 1, receivedData .indexOf(';') );
					 				nfcData_Name = receivedData.substring( receivedData.indexOf(';') + 1  ,receivedData.indexOf('}') );
           				receivedData = '';
					 				totalNumber++;
					 				//totalNumber寫回Arduino , NFC module會將該號碼傳給Anrdoid
					 				
					 				serialPort.write(totalNumber.toString()
									+';'+currentNumber.toString());
									
									console.log("UUID:"+nfcData_ID+"    Name:"+nfcData_Name+" Number:"+totalNumber);
									
			 				//-------------------processing  serialport data event------------------------

							//socket傳送資料給client
      				socketServer.emit('updateData', totalNumber);
		 					//----------------------DB processing-------------------
	   					//取得號碼
						
	   					idJson.number=totalNumber;	   					
		 				post.UUID=nfcData_ID;
						post.account=nfcData_Name;
		 				post.number=totalNumber;
						//a();
	   					//搜尋資料庫有無此號碼
	  					db.findOneById(tableName , idJson ,function(ret){
		  				//console.log(ret);
			       if(!ret) //無相同ID資料 則插入新資料
						 {
				  			db.insert(tableName , post ,function(ret2){
				  			//console.log(ret2);
							});
	  				}
   					

						});
						
						//---------------------------------------------------------------------
       				}
					if(isFinifshed==1){
						var a=totalNumber.toString();
						console.log(a);
                        
						isFinifshed=0;
						
					}
       				
   			});
  });

}


function serialBtnListener(debug2)
{

			var receivedData = '';
			//initialize SerialPort Object for button
			serialButton = new SerialPort(portBtn,{
					buardrate:9600,
					dataBits:8,
					parity:'none',
					flowControl:false
			});
			//-------------------processing  serialport data event------------------------
			serialButton.on("open",function(){
					console.log('Open Serial Button Cmmuncation');

					//on:data 事件 每次從serialport取的一個字元
					serialButton.on("data",function(data){
							receivedData += data.toString();
							//console.log(data.toString());
							if(receivedData.indexOf('E')>=0 && receivedData.indexOf('B')>=0){
											btnData = receivedData.substring(receivedData.indexOf('B')+1,receivedData.indexOf('E'));
											receivedData = '';
											isFinished = 1;
											console.log("get in");
											
										 btnRequestProcess(btnData);
										 console.log("currentNumber : "+currentNumber);
										/////////////////////// 
								
										gcmNumberservice(currentNumber,totalNumber);
										
										//gcm.Numbersend();
										/*
										db.select("user" ,function(ret){			
											//console.log(ret); //回傳幾筆資料
											for(i=0;i<ret.length;i++)  //發送GCM通知
											{
											//console.log(i+"="+ret[i].regID); //所有regID
											registrationIds.push(ret[i].regID);
											//console.log(registrationIds);
											sender.send(message, registrationIds[i], 4, function (result) {
												//console.log(result);				
												//console.log("success!");
												});		
											}
											console.log("success!");											
										 }
										);
										*/
										 ///////////////////////
							}
					});

			});
			//-------------------processing  serialport data event------------------------
	}
	function btnRequestProcess(btnData){
					if(btnData == "plus"){
						
								if(totalNumber > currentNumber){
										currentNumber++;
										console.log("Button pushed. CurrentNumber:"+	currentNumber);
										socketServer.emit('btnClick', currentNumber);
								}
					}
	}


	function test()
	{
		console.log("test start")
		serialPort.write(i.toString());
		i++;
	}


	function gcmDistanceService(){
	//console.log(currentNumber);
	db.select(tableName ,function(ret){			
	//console.log(ret); //回傳幾筆>目前號碼資料								
	for(i=0;i<ret.length;i++)  //發送GCM通知
			{
				if(ret[i].number > currentNumber){ //排隊者號碼在目前處理號碼之後
				
						//console.log(ret[i].number+"號的距離="+ret[i].distance); //所有regID
						if( ((ret[i].distance)/100 > (ret[i].number - currentNumber)) && (ret[i].status ==0) ) //條件(線性+拒絕服務通知)
						{
							clientUUID = ret[i].UUID;
							data = (ret[i].number - currentNumber);
							clientnumber = ret[i].number;
							console.log("clientnumber:"+clientnumber);
							db.findOneByUUID('user',	clientUUID	,	function(ret3){		
								if(ret3){
								
								gcm.Distancesend( ret3.regID , data , clientnumber);
								
								}
							}); 
							
						}
			
				}
							
			}
		//console.log("gcmDistanceService success!");		
	 }
									 
	);
											
	}

	var clientUUID="";

	function gcmNumberservice(currentNumber,totalNumber){	//通知後三號
		//db=new mysql();
		if(totalNumber>=currentNumber+3)
		{
		db.findOneByNumber(tableName,currentNumber+3,function(ret1){    // Call the start function which will connect to the database.  
		clientUUID = ret1.UUID;
				if(ret1.status==0) // 欲通知的使用者仍未關閉通知
				{
					db.findOneByUUID('user',clientUUID	,	function(ret3){		
						if(ret3){
						//console.log(ret3.regID);
						gcm.Numbersend(ret3.regID);
						}
					});   
				}
			});
		}		
	}
	//////////tableChange_day 資料庫表格 自動按日更新
	function tableChange_day(){

					
	var tody = new Date();
	var nian = tody.getFullYear();
	var youe = tody.getMonth() + 1 //預設為零 加1調整;
		if (youe < 10)
		{
			youe = '0'+youe.toString(); //補零，好整理
			
		}
	var day = tody.getDate();
		if (day < 10)
			day = '0'+day.toString();
	//var hour = tody.getHours();
	//var min = tody.getMinutes();
	var miao = tody.getSeconds();
	var TimeTableName = nian.toString()+youe.toString()+day.toString();

		if (ChangeDay != day) //ChangeDay 沒有初始值
			{	
				
				ChangeDay = day;
				conn.query('CREATE TABLE IF NOT EXISTS numbertable_'+TimeTableName+'(' 
					+ ' id INT NOT NULL AUTO_INCREMENT,' 
					+ ' UUID VARCHAR(100),'
					+ ' PRIMARY KEY(id),' 				
					+ ' account VARCHAR(30),' 
					+ ' number INT(10),'
					+ '	distance float(15) default null,'
					+ ' status BOOLEAN not null default 0,'				
					+ ' take_time DATETIME DEFAULT current_timestamp,'
					+ ' handle_time DATETIME '
					+ ' )' , 				 
					function (err,rows4) {					
					if (err) throw err;
					//console.log(rows4);
					tableName = 'numbertable_'+TimeTableName;
					console.log("Use table named "+tableName);	
					console.log('Today(yyyymmdd): '+TimeTableName);
					});				
			}
				
	}

	///////////////////////////////////
	function distHaversine(p1, p2) { //距離公式
					var rad = function (x) { return x * Math.PI / 180; }
					var R = 6371.0; // earth's mean radius in km
					var dLat = rad(p2.lat - p1.lat);
					
					var dLong = rad(p2.lng - p1.lng);
	 
					var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
							Math.cos(rad(p1.lat)) * Math.cos(rad(p2.lat))
							* Math.sin(dLong / 2) * Math.sin(dLong / 2);
					var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
					var d = R * c *1000;
					console.log(parseFloat(d.toFixed(6)));
					return parseFloat(d.toFixed(6));
				}
exports.start = startServer;
