'use strict';

var usernamePage = $('#username-page');
var playPage = $('#username-page');
var usernameForm = $('.gamestart-board');
var loadingOverlay = $('.loading');

var stompClient = null;
var username = null;
var word = null;

function connect(event) {
    username =$('#name').val().trim();
    word  = $('#word').val().trim();
    if(username && word) {
        usernamePage.addClass('hidden');
        playPage.removeClass('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        loadingOverlay.removeClass('hidden');
        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, content: word, type: 'JOIN'})
    )
    loadingOverlay.addClass('hidden');
}


function onError(error) {

}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
        appendText(message, messageElement);
    }

}
usernameForm.on('submit', connect);