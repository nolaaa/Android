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

// Socket.io [socket event handling].
io.on('connection', function (socket) {
    console.log('[debug] new connection');

    // New User Joins Chat. [adds user to users, tell users of new user and respond user is ready].
    socket.on('new user', function (user) { // mine
        if (user) { // check if user is null
            console.log('[debug] new user');
            socket.emit('ready'); // tell user is ready to chat.
            socket.broadcast.emit('new user', { // broadcast new user.
                publicName: user.publicName
            });
        }
    });

    // User Sent a Message. [broadcast message to users]
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