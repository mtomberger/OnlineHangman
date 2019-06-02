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
var clientId = generateGuid();

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
    stompClient.subscribe('/hangman/public', onMessagesReceived);
    // Tell your username to the server
    stompClient.send("/app/hangman.addUser",
        {},
        JSON.stringify({
            messages:[{
                senderId: clientId,
                sender: username,
                content: word,
                type: 'JOIN'
            }]
        })
    );
    loadingOverlay.addClass('hidden');
    isConnected = true;
}
function onMessageReceived(message){
    if(message.type === 'ID'){
        clientId = message.content;
    }
    if(message.type === 'JOIN'){
        var messagetext = message.content;
        roominfo.text(messagetext);
    }
    if(message.type === 'ERROR') {
        stompClient.disconnect(function(){
            onError(message.content);
        });

    }
}
function onMessagesReceived(payload) {
    var receivedMessages = JSON.parse(payload.body);
    receivedMessages = receivedMessages.messages.filter(m => m.senderId == clientId);
    receivedMessages.forEach(m => onMessageReceived(m));
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
            messages:[{
            sender: username,
            content: messageInput.value,
            type: 'PLAY'
            }]
        };
        stompClient.send("/app/hangman.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}
function generateGuid(){
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g,n=>(n^crypto.getRandomValues(new Uint8Array(1))[0]&15>>n/4).toString(16));
}
joinPage.on('submit', connect);
$("#word").on('keydown',function(e){
    return (/[A-Za-z]/.test(e.key.substr(0,1)));
});