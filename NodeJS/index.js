var server = require("./server");
var router = require("./route");
var requestHandlers = require("./requestHandlers");
var mysql = require('./mysql');
var debug = false;

var handle = {}
handle["/"] = requestHandlers.sendInterface;
handle["/interface"] = requestHandlers.sendInterface;

server.start(router.route,handle,debug);