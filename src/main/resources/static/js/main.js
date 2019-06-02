'use strict';

var gamePage = $('.game-board');
var joinPage = $('.gamestart-board');
var scorePage = $('score-board');
var loadingOverlay = $('.loading');
var roominfo = $('#roominfo');
var isConnected = false;
var stompClient = null;
var username = null;
var word = null;

function connect(event) {
    username =$('#name').val().trim();
    word  = $('#word').val().trim();
    if(username && word) {
        joinPage.addClass('hidden');
        gamePage.removeClass('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        loadingOverlay.removeClass('hidden');
        stompClient.connect({}, onConnected, onSocketError);
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
    isConnected = true;
}


function onSocketError(error) {
    onError(error);
}
function onError(error) {
    resetPages();
    if(typeof error === "string"){
        $('.error-message').text(error);
        if(isConnected){
            try{
                stompClient.disconnect();
            }catch(e){}
        }
    }
    showPage(joinPage);
}
function resetPages(){
    $('#word').val("");
    $('.error-message').text("");
}
function showPage(page){
    scorePage.addClass("hidden");
    joinPage.addClass("hidden");
    gamePage.addClass("hidden");
    page.removeClass("hidden");
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
        var messagetext = message.content;
        var messageparts = messagetext.split("/");
        var newParts = messageparts.filter(f => f.trim() !== username);
        messagetext = newParts.join(" ");
        roominfo.text(messagetext);
    }
    if(message.type === 'ERROR') {
        stompClient.disconnect(function(){
            onError(message.content);
        });

    }

}
joinPage.on('submit', connect);
$("#word").on('keydown',function(e){
    return (/[A-Za-z]/.test(e.key.substr(0,1)));
});