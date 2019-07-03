'use strict';

var gamePage = $('.game-board');
var joinPage = $('.gamestart-board');
var scorePage = $('score-board');
var loadingOverlay = $('.loading');
var roominfo = $('#roominfo');
var enemyinfo = $('#enemy-info');
var playerinfo = $('#player-info');
var cont = $('.score-container');
var isConnected = false;
var stompClient = null;
var username = null;
var word = null;
var clientId = generateGuid();
var MESSAGE_DELIMITER = '#;#';
var intervalId = 0;

//disable logging
console.log=function(){};

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
        var myWord = messagetext.split(MESSAGE_DELIMITER)[0];
        var enemyWord = messagetext.split(MESSAGE_DELIMITER)[1];
        createWordDisplay($('.player-container .word'),myWord);
        createWordDisplay($('.enemy-container .word'),enemyWord,word);
        initTyping();
    }
    if(message.type === 'FINISH'){
        resetTyping();
        var neededMistakes = message.content.split(MESSAGE_DELIMITER)[0];
        var availableMistakes = message.content.split(MESSAGE_DELIMITER)[1];
        var toGuess = message.content.split(MESSAGE_DELIMITER)[2];
        var state  = "' right. "+neededMistakes+" Letters were wrong.";
        $('.finish').removeClass("hidden");
        if(parseInt(neededMistakes)>parseInt(availableMistakes)){
            state  = "' wrong";
            $('.finish-failed').removeClass("hidden");

        }else{
            $('.finish-success').removeClass("hidden");

        }
        roominfo.text("You guessed the word '" + toGuess+"' from '"+enemyinfo.text()+state);
    }
    if(message.type === 'SCORE'){
        //Player,0,10,90#;#Player2,11,10,61
        var scores = message.content.split(MESSAGE_DELIMITER);
        var scoreObjs = scores.map(function(s){
            return {
                id: s.split(",")[0],
                player: s.split(",")[1],
                mistakes: parseInt(s.split(",")[2]),
                maxMistakes: parseInt(s.split(",")[3]),
                time: s.split(",")[4],
                word: s.split(",")[5],
                score: s.split(",")[6],
            };
        });
        cont.empty();
        createScoreboard(cont,scoreObjs);
        cont.append($('<button class="scores-button">').text("Submit your Score"));
        cont.removeClass("hidden");
        $('.finish').addClass("hidden");
        $('.finish-success').addClass("hidden");
        $('.finish-failed').addClass("hidden");

    }
    if(message.type === 'PLAY'){
        var messagecontents = message.content.split(MESSAGE_DELIMITER);
        var lastGuess = messagecontents[0];
        var allGuessed = messagecontents[1];
        var guessedIndex = messagecontents[2].split(",");
        var playerId = messagecontents[3];
        var containerToWrite = playerId == clientId ? $(".player-container") : $(".enemy-container");
        containerToWrite.find(".guessed").text(allGuessed);
        if(guessedIndex.length==1 && guessedIndex[0]== ""){
            drawMistake(containerToWrite.find(".hangman"));
        }else{
            containerToWrite.find(".word .letter").each(function(i){
                if(guessedIndex.filter(x => x == i).length>0){
                    $(this).text(lastGuess);
                }
            });
        }

        enemyinfo.text(messagetext);
    }
    if(message.type === 'ERROR') {
        stompClient.disconnect(function(){
            onError(message.content);
        });

    }
}
function createScoreboard(board,scoreObjs){
    scoreObjs.sort(function(a,b){
        if(a.score<b.score){
            return 1;
        }
        if(a.score==b.score){
            if(a.time>b.time){
                return 1;
            }
            return -1;
        }
        return -1;
    });
    board.append($("<h1>").text("Scores for this game"));
    var table=$("<table class='score-table'>");
    var headerRow = $("<tr>");
    headerRow.append($("<th>").text("Place"));
    headerRow.append($("<th>").text("Player name"));
    headerRow.append($("<th>").text("Mistakes made"));
    headerRow.append($("<th>").text("Time needed"));
    headerRow.append($("<th>").text("word to guess"));
    headerRow.append($("<th>").text("Score"));
    table.append(headerRow);
    for(var i=0;i<scoreObjs.length;i++){
        var scoreItem = $("<tr>");
        scoreItem.append($("<td>").text((i+1)+"."));
        var mis = scoreObjs[i].mistakes+"/"+scoreObjs[i].maxMistakes;
        if(scoreObjs[i].mistakes>scoreObjs[i].maxMistakes){
            mis = "too much";
            scoreItem.addClass("crossed");
        }
        if(scoreObjs[i].id == clientId){
            scoreItem.append($("<td class='you'>").text(scoreObjs[i].player));
        }else {
            scoreItem.append($("<td>").text(scoreObjs[i].player));
        }
        scoreItem.append($("<td>").text(mis));
        scoreItem.append($("<td>").text(scoreObjs[i].time+" Seconds"));
        scoreItem.append($("<td>").text(scoreObjs[i].word));
        scoreItem.append($("<td>").text(scoreObjs[i].score));
        table.append(scoreItem);
    }
    board.append(table);
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
    if(stompClient) {
        var chatMessage = {
            messages:[{
                senderId: clientId,
                sender: username,
                content: letter,
                type: 'PLAY'
            }]
        };
        stompClient.send("/app/hangman.guessLetter", {}, JSON.stringify(chatMessage));
    }
}
function createWordDisplay(container,len, fill){
    var appendHTML = "";
    for(var i=0;i<len;i++){
        var addAttr = fill? 'data-letter="'+fill.charAt(i)+'"':'';
        appendHTML += '<span class="letter" '+addAttr+'>&nbsp;</span>';
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
    $(hangman).parents('.player-container,.enemy-container').addClass('wrong');
    setTimeout(function(){
        $(hangman).parents('.player-container,.enemy-container').removeClass('wrong');
    },500);
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
    cont.removeClass("hidden");
    resetTyping();
}
function showPage(page){
    scorePage.addClass("hidden");
    joinPage.addClass("hidden");
    gamePage.addClass("hidden");
    page.removeClass("hidden");
    cont.addClass("hidden");
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
        if(e.keyCode>=65 && e.keyCode<=90){
            pressedKey = e.key.toUpperCase();
        }
        if(e.keyCode==13 && chooseMode){
            chooseMode = false;
            onLetterChoosen($('.choosen-letter').text());
            $('.choosen-letter').addClass("hidden");
            typingAvailable = false;
            roominfo.text("Think about your choice");
            intervalId = setTimeout(function(){
                typingAvailable = true;
                roominfo.text(typeInfoText);
            },1000);
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
    $('.choosen-letter').addClass("hidden");
    $('.choosen-letter').text("");
    $(document).off('keydown.choose');
    clearTimeout(intervalId);
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
$('body').on('click','.scores-button',function(){
    //TODO load score page

    var board = $.ajax({
        type: "GET",
        url: "/scores",
        contentType: "application/json",
        cache: false,
        timeout: 600000,
        dataType: "json",
        success: function(result) {
            alert(result);
        },
        error: function (response) {
            alert(response.responseText)
        }
    });

    roominfo.text("Top scores");
    createScoreboard(scorePage,[]);
    showPage(scorePage);
});