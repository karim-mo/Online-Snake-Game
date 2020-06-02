var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

var AU = setInterval(AppleUpdate, 1000);
var SU = setInterval(ServerUpdate, 5000, {}, false);
var sessions = [];
var sess_id = 0;




server.listen(9000, function () {
    var today = new Date();
    var date = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
    var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
    var dateTime = date + ' ' + time;
    console.log("[" + dateTime + "]" + " Server has started..\n\n");
});

io.on('connection', function (socket) {
    var p = new player(socket.id, 0, 0);
    var a = new apple(socket.id, getRandomInt((1280/25)) * 25 , getRandomInt((720/25)) * 25 );
    var flag = false;

    sessions.forEach(function (k) {
        if (k.players.length < 4) {
            p.session = k.sessionid;
            a.session = k.sessionid;
            k.players.push(p);
            k.apples.push(a);
            flag = true;
            var today = new Date();
            var date = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
            var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
            var dateTime = date + ' ' + time;
            console.log("[" + dateTime + "]" + " New server connection from ID: " + socket.id + " sent to session " + k.sessionid + " with " + k.players.length + " players online.");
            socket.emit('socketID', { id: socket.id }, { appid: socket.id }, { session: k.sessionid });
            socket.emit('getPlayers', k.players);
            socket.broadcast.emit('newApple', { appid: socket.id, x: a.x, y: a.y }, { session: k.sessionid });
            socket.emit('getApples', k.apples);
            socket.broadcast.emit('newPlayer', { id: socket.id }, { session: k.sessionid });
        }
    });

    if (!flag) {
        sessions.push(new session(sess_id));
        var s = sessions[sess_id];
        p.session = sess_id;
        a.session = sess_id;
        s.players.push(p);
        s.apples.push(a);
        var today = new Date();
        var date = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
        var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
        var dateTime = date + ' ' + time;
        socket.emit('socketID', { id: socket.id }, { appid: socket.id }, { session: (sess_id) });
        socket.emit('getApples', s.apples);
        console.log("[" + dateTime + "]" + " New session with ID: " + sess_id + " was created by user ID: " + socket.id);
        sess_id++;
    }






    socket.on('disconnect', function () {
        var pl = getGameData(socket.id);
        var p, s;
        p = pl.session.players[pl.pindex];
        s = pl.session;
        var today = new Date();
        var date = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
        var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
        var dateTime = date + ' ' + time;
        console.log("[" + dateTime + "]" + " Player disconnected with ID: " + socket.id + " from session " + pl.sindex + ". Session now has " + (s.players.length-1) + " players online.");
        socket.broadcast.emit('playerDisconnected', { id: socket.id }, { appid: socket.id }, { session: p.session });
        s.players.splice(pl.pindex, 1);
        s.apples.splice(pl.pindex, 1);

        if (s.players.length < 1) {
            sessions.splice(pl.sindex, 1);
            console.log("[" + dateTime + "]" + " 0 players found in session ID " + pl.sindex + ". Session terminated.");
            sess_id--;
        }

    });
    socket.on('TailUpdate', function (data) {
        var pl = getGameData(socket.id);
        data.session = pl.sindex;
        data.id = socket.id;
        socket.broadcast.emit('TailUpdate', data);
        var s = pl.session;
        try {
            s.players[pl.pindex].x = data.x;
            s.players[pl.pindex].y = data.y;
        } catch (e) {
            console.log(e);
        }
    });

    socket.on('TailUpdateM', function (data) {
        var pl = getGameData(data.id);
        data.session = pl.sindex;
        socket.broadcast.emit('TailUpdate', data);
        var s = pl.session;
        try {
            s.players[pl.pindex].x = data.x;
            s.players[pl.pindex].y = data.y;
        } catch (e) {
            console.log(e);
        }
    });

    socket.on('playerMoved', function (data) {

        data.id = socket.id;
        var pl = getGameData(socket.id);
        data.session = pl.sindex;
        socket.broadcast.emit('playerMoved', data);
        //sendGameData('playerMoved', socket.id, false, data);
        var s = pl.session;
        try {
            //socket.emit('getApples', s.apples);
            s.players[pl.pindex].x = data.x;
            s.players[pl.pindex].y = data.y;
        } catch (e) {
            console.log(e);
        }
        //socket.emit('getPlayers', s.players);

    });
    // socket.on('appleMoved', function (data) {
    //     data.appid = socket.id;
    //     var pl = getGameData(socket.id);
    //     data.session = pl.sindex;
    //     socket.broadcast.emit('appleMoved', data);

    //     var s = pl.session;
    //     try {
    //         //socket.emit('getApples', s.apples);
    //         s.apples[pl.pindex].x = data.x;
    //         s.apples[pl.pindex].y = data.y;
    //     } catch (e) {
    //         console.log(e);
    //     }

    // });
    socket.on('appleMovedM', function (data) {
        ServerUpdate(data, true);
    });
});

function player(id, x, y) {
    this.session = -1;
    this.id = id;
    this.x = x;
    this.y = y;
}


function apple(appid, x, y) {
    this.session = -1;
    this.appid = appid;
    this.x = x;
    this.y = y;
    this.timer = 5;
}

function session(id) {
    this.sessionid = id;
    this.players = [];
    this.apples = [];
}

function getRandomInt(max) {
    return Math.floor(Math.random() * Math.floor(max));
}

function getGameData(socketid) {

    var session, sess_index, player_index, k = 0;

    sessions.forEach(function (s) {
        for (var i = 0; i < s.players.length; i++) {
            if (s.players[i].id == socketid) {
                session = s;
                sess_index = k;
                player_index = i;
                break;
            }
        }
        k++;
    });

    return { session: session, sindex: sess_index, pindex: player_index };
}

function ServerUpdate(data, immediate) {
    if (sessions.length == 0) return;
    if (!immediate) {
        var k = 0;
        sessions.forEach(function (s) {
            for (var i = 0; i < s.apples.length; i++) {
                if (s.apples[i].timer <= 0) {
                    s.apples[i].x = getRandomInt((1280 / 25)) * 25;
                    s.apples[i].y = getRandomInt((720 / 25)) * 25;
                    s.apples[i].timer = 5;
                    io.sockets.emit('appleMoved', { session: k, appid: s.apples[i].appid, x: s.apples[i].x, y: s.apples[i].y });
                }
            }
            k++;
            //socket.emit('getApples', s.apples); //could be worse in terms of performance, appleMoved is better with O(1) performance per event.
        });
    }
    else {
        var s = sessions[data.session];
        for (var i = 0; i < s.apples.length; i++) {
            if (s.apples[i].appid == data.appid) {
                s.apples[i].x = getRandomInt((1280 / 25)) * 25;
                s.apples[i].y = getRandomInt((720 / 25)) * 25;
                s.apples[i].timer = 5;
                io.sockets.emit('appleMoved', { session: data.session, appid: s.apples[i].appid, x: s.apples[i].x, y: s.apples[i].y });
            }
        }
    }
}

function AppleUpdate() {
    if (sessions.length == 0) return;
    sessions.forEach(function (s) {
        for (var i = 0; i < s.apples.length; i++) {
            s.apples[i].timer--;
        }
    });
}