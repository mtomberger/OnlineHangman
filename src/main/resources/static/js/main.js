'use strict';

var gamePage = $('.game-board');
var joinPage = $('.gamestart-board');
var scorePage = $('score-board');
var loadingOverlay = $('.loading');
var roominfo = $('#roominfo');
var enemyinfo = $('#enemy-info');
var playerinfo = $('#player-info');
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
    playerinfo.text(username);
    loadingOverlay.addClass('hidden');
    isConnected = true;
    initTyping();
}
function onMessageReceived(message){
    if(message.type === 'ID'){
        clientId = message.content;
    }
    if(message.type === 'JOIN'){
        var messagetext = message.content;
        enemyinfo.text(messagetext);
    }
    if(message.type === 'INIT'){
        var messagetext = message.content;
        var myWord = messagetext.split('#;#')[0];
        var enemyWord = messagetext.split('#;#')[1];
        createWordDisplay($('.player-container .word'),myWord);
        createWordDisplay($('.enemy-container .word'),enemyWord);
    }
    if(message.type === 'ERROR') {
        stompClient.disconnect(function(){
            onError(message.content);
        });

    }
}
function onMessagesReceived(payload) {
    var receivedMessages = JSON.parse(payload.body);
    if(!receivedMessages || !receivedMessages.messages){
        return;
    }
    receivedMessages = receivedMessages.messages.filter(m => m.senderId == clientId);
    receivedMessages.forEach(m => onMessageReceived(m));
}
function onLetterChoosen(letter){
    console.log("LETTER: "+letter);
}
function createWordDisplay(container,len){
    var appendHTML = "";
    for(var i=0;i<len;i++){
        appendHTML += '<span class="letter">&nbsp;</span>';
    }
    container.html(appendHTML);
}
function drawMistake(hangman){
    hangman = $(hangman);
    if(!hangman || !hangman.is(".hangman")){
        return;
    }
    var part = hangman.find(".hangman-part:not(.draw-part)").first();
    if(!part.attr("class")){
        return;
    }
    var removedClass= part.attr("class").split(" ")[1];
    part.removeClass("circ vert hor tilt135 tilt225");
    setTimeout(function(p,rc){
        p.addClass(rc).addClass("draw-part");
    },0,part,removedClass);
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
    roominfo.text("");
    enemyinfo.text("");
    playerinfo.text("");
    resetTyping();
}
function showPage(page){
    scorePage.addClass("hidden");
    joinPage.addClass("hidden");
    gamePage.addClass("hidden");
    page.removeClass("hidden");
}

var chooseMode = false;
var typingAvailable = true;
function initTyping(){
    resetTyping();
    var typeInfoText = "Type a letter and press Enter to choose it";
    roominfo.text(typeInfoText);
    $(document).on('keydown.choose',function(e){
        var pressedKey = "";
        if((e.keyCode==27 || e.keyCode==8) && chooseMode){
            chooseMode = false;
            $('.choosen-letter').addClass("hidden");
            $('.choosen-letter').text("");
        }
        if(!typingAvailable){
            return;
        }
        if((e.keyCode>=65 && e.keyCode<=90) || (e.keyCode>=97 && e.keyCode<=122)){
            pressedKey = e.key.toUpperCase();
        }
        if(e.keyCode==13 && chooseMode){
            chooseMode = false;
            onLetterChoosen($('.choosen-letter').text());
            $('.choosen-letter').addClass("hidden");
            typingAvailable = false;
            roominfo.text("Think about your choice");
            setTimeout(function(){
                typingAvailable = true;
                roominfo.text(typeInfoText);
            },2500);
        }

        if(pressedKey){
            chooseMode=true;
            $('.choosen-letter').removeClass("hidden");
            $('.choosen-letter').text(pressedKey);
        }
    })
}
function resetTyping(){
    chooseMode = false;
    typingAvailable = true;
    $(document).off('keydown.choose');
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