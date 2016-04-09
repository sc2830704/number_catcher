var mysql = require('mysql')
   ,qs = require('querystring')
   ,dbClient;
   
/* 建立 MYSQL 連線物件 */
var conn = mysql.createConnection({
	host: 'localhost',
    user: 'root',
    password: '589321321',
    database: 'cobe'
});
/*
conn.connect(function(err){
	if(err){
			console.log("error connect!"+error.message);
			dbClient.end();
		}
		console.log("Connect mysql Successfully!");
		
		conn.query('SELECT * FROM test_cn',function(err,rows){
			
		});
});
*/
var selectSQL = 'select * from test_cn order by id desc limit 0,10'
var formData = ''; 
var post  = {id: 0 ,name: '', number: 'Hello MySQL'}; //test_cn => 欄位 : 資料,
var Maxsql    = 'SELECT max(id) as id  FROM test_cn ';
var s="";
var ps;
var cNumber='';
var firstResult = '';
var resultSet = '';
/*
conn.query('SELECT * FROM test_cn',function(err,rows){
	if(err){
			console.log("error connect!"+error.message);
				conn.end();
		}
});
*/

module.exports = function (){
	
	//資料庫連線
	conn.connect(function (err) {
   if(err){
			console.log("error connect!"+error.message);
			dbClient.end();
		}
		/*
		console.log("Connect mysql Successfully!");	
		conn.query(selectSQL,function(err,rows){
			console.log(rows);
			
		});
		*/
  });
  //所有資料  
	this.select = function(tableName,callback){
	//console.log('select');
	conn.query('SELECT * FROM '+tableName,function(err,rows){		
	if(err){
			console.log("error connect!"+error.message);
				conn.end();
		} else {
			callback(rows);
		}
	});
	};
	  
  //插入資料介面	
  this.insert =function(tableName , rowInfo ,callback){
	  console.log("insert");	
	  
	  conn.query('insert into ' + tableName +' SET ?', rowInfo , function(err,rows){
	  if(err) throw err;
	  callback(rows.insertId);
	  });
  };
  //刪除資料介面
  this.remove =function(tableName , idJson ,callback){
	  conn.query('delete from ' +tableName+' where ?',idJson,function(err,results){
		 if(err){
			 console.log('ClientReady Error :'+ err.message );
			 dbClient.end();
			 callback(false);
		 } else{
			 callback(true);
		 }
	  });
  };
  //修改資料介面 regID
  this.modifyregID =function(tableName ,rowInfo, idJson ,callback){
	  conn.query('update '+ tableName + ' SET regID = ? where UUID = ?' ,[rowInfo,idJson],function(err ,result){
		  if(err){
			  console.log("ClientReady Error: " +err.message)
			  callback(false);
		  } else{
			  callback(result);
		  }
	  });
  };
  //修改資料介面 distance
  this.modifydistance =function(tableName ,rowInfo, idJson ,callback){
	  conn.query('update '+ tableName + ' SET distance = ? where UUID = ?' ,[rowInfo,idJson],function(err ,result){
		  if(err){
			  console.log("ClientReady Error: " +err.message)
			  callback(false);
		  } else{
			  callback(result);
		  }
	  });
  };
   //修改資料介面 status
  this.modifyStatus =function(tableName ,rowInfo, idJson ,callback){
	  conn.query('update '+ tableName + ' SET status = ? where UUID = ?' ,[rowInfo,idJson],function(err ,result){
		  if(err){
			  console.log("ClientReady Error: " +err.message)
			  callback(false);
		  } else{
			  callback(result);
		  }
	  });
  };
 
  //查詢資料介面 索引 編碼ID
  this.findOneById =function(tableName ,idJson ,callback){
	 // console.log("findOneById");	
	  
	  conn.query('select * from '+tableName +' where number = ?', idJson, function(err,rows){
	  if(err) {
		  console.log('GetData Error : '+err.message);
		  conn.end();
		  callback(false);
	  }else{
		  if(rows){ //如果查詢到資料則傳回一筆資料
			callback(rows.pop());			  
			//callback(rows); // 符合條件ID全部傳回 
		  } else { // 如果資料為空
			  callback(rows);
		  }		  
	  }	  
	  });
  };
  this.findOneByNumber =function(tableName ,idJson ,callback){
	 // console.log("findOneByNumber");	
	  
	  conn.query('select * from '+tableName +' where number = ?', idJson, function(err,rows){
	  if(err) {
		  console.log('GetData Error : '+err.message);
		  conn.end();
		  callback(false);
	  }else{
		  if(rows){ //如果查詢到資料則傳回一筆資料
			callback(rows.pop());			  
			//callback(rows); // 符合條件ID全部傳回 
		  } else { // 如果資料為空
			  callback(rows);
		  }		  
	  }	  
	  });
  };
   //查詢資料 索引 帳戶
  this.findOneByaccount =function(tableName ,account ,callback){
	 // console.log("findOneByaccount");	
	  
	  conn.query('select * from '+tableName +' where account=?', account, function(err,rows){
	  if(err) {  
		  console.log('GetData Error : '+err.message);
		  conn.end();
		  callback(false);
	  }else{
		  if(rows){ //如果查詢到資料則傳回一筆資料
			console.log(rows);
			callback(rows.pop());			  
		 // callback(rows); // 符合條件ID全部傳回 
		  } else { // 如果資料為空
			  callback(rows);
		  }		  
	  }	  
	  });
  };

 //查詢資料 索引 UUID
  this.findOneByUUID =function(tableName ,UUID ,callback){
	 // console.log("findOneByUUID");	
	  
	  conn.query('select * from '+tableName +' where UUID=?', UUID, function(err,rows){
	  if(err) {  
		  console.log('GetData Error : '+err.message);
		  conn.end();
		  callback(false);
	  }else{
		  if(rows){ //如果查詢到資料則傳回一筆資料
			//console.log(rows);
			callback(rows.pop());			  
		 // callback(rows); // 符合條件ID全部傳回 
		  } else { // 如果資料為空
			  callback(rows);
		  }		  
	  }	  
	  });
  };
  
  
  //條件查詢資料介面
  this.find =function(tableName ,whereJson,orderByJson,limitArr,fieldsArr ,callback){
	  //暫無功能
  } ;
}