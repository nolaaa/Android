var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var routes = require('./routes/index');

var app = express();
var server = require('http').Server(app); // add http server.
var io = require('socket.io')(server); // add socket.io 'websockets'.
server.listen(1337); // listen for websockets on port 1337

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing a favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});



var users = new Array(10000); // users for socket.io.

// Socket.io [socket event handling].
io.on('connection', function (socket) {
    console.log('[debug] new connection');

    // (1) New User Joins Chat. [adds user to users, tell users of new user and respond user is ready].
    socket.on('new user', function (user) { // mine
        console.log('[debug] new user');
        if (!user) // if user is empty create a user
            user = { // random user_ + number between 100(min) to 1000(max).
                publicName : ('user_' + Math.floor(Math.random()*(1000-100+1)+100))
            };
        socket.user = user; // Add user to socket for disconnect and other look ups based on socket message.
        socket.emit('ready'); // tell user he is ready to chat.
        users.push(user);
        socket.broadcast.emit('new user', { // broadcast new user.
            publicName: user.publicName,
            usersOnline: users.count
        });

    });

    // (2) User Disconnects from Chat. [broadcast disconnect event; tell users who disconnected and remove user from users].
    socket.on('disconnect', function() {
        try {
            socket.broadcast.emit('disconnect', { // broadcast disconnect.
                user: socket.user
            });
            delete users[socket.user]; // remove user from users.
        } catch(e) {

        }
    });

    // (3) User Sent a Message. [broadcast message to users]
    socket.on('message', function(message) {
        console.log('[debug] message');
        if (message) { // Only if message contains something.
            socket.broadcast.emit('message', {
                publicName: message.publicName,
                message: message.message
            });
        }
    });
});


module.exports = app;
