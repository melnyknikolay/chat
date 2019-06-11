'use strict';

var usernamePage = document.querySelector('#username-page');
var chatCreatePage = document.querySelector('#select-chat-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var chatForm = document.querySelector('#chatForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var usersArea = document.querySelector('#usersArea');
var chatArea = document.querySelector('#chatArea');
var leaveChatForm = document.querySelector('#leaveChatForm');
var chatheader = document.querySelector('#chat-header');


var username = null;
var chatName = null;
var chatId = null;
var profileId = null;
var socket = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

window.onload = loadChats;

function loadChats() {
    username = null;
    chatName = null;
    chatId = null;
    profileId = null;
    socket = null;
    chatArea.innerHTML = "";
    var xhr = new XMLHttpRequest();
    var url = "http://localhost:8097/chat";
    xhr.open("GET", url, true);
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            Rx.Observable
                .from(JSON.parse(xhr.response))
                .subscribe(msg => onChatReceived(msg));
        }
    }
}

function selectChat(event) {
    chatName = event.currentTarget.childNodes[0].childNodes[0].data;
    createChat(event);
}

function createChat(event) {
    if (!chatName) {
        chatName = document.querySelector('#chatName').value.trim();
    }
    if (chatName) {
        var xhr = new XMLHttpRequest();
        var url = "http://localhost:8097/chat";
        var data = JSON.stringify({name: chatName});
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 201) {
                chatId = xhr.getResponseHeader("chatId");
                chatCreatePage.classList.remove('select-chat-page-class');
                chatCreatePage.classList.add('hidden');
                usernamePage.classList.remove('hidden');
            }
        };
        xhr.send(data);
    }
    event.preventDefault();
}

function connect(event) {
    username = document.querySelector('#name').value.trim();
    if (username) {
        messageArea.innerHTML = "";
        var xhr = new XMLHttpRequest();
        var url = "http://localhost:8097/profile/" + chatId;
        var data = JSON.stringify({name: username});
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 201) {
                loadMessages();
                setTimeout(function() { loadUsers(); }, 1000);
                setTimeout(function() { loadUsers(); }, 10000);
                profileId = xhr.getResponseHeader("profileId")
                usernamePage.classList.add('hidden');
                chatPage.classList.remove('hidden');
                chatPage.classList.add('select-chat-page-class');
                chatheader.textContent = 'Chat ' + chatName;
                socket = new WebSocket('ws://localhost:8097/ws/' + chatId + '/message');
                socket.addEventListener('message', function (event) {
                    let message = JSON.parse(event.data);
                    onMessageReceived(message)
                    if (message.type === 'JOIN' || message.type === 'LEAVE') {
                        loadUsers();
                    }
                });
            }
        };
        xhr.send(data);
    }
    event.preventDefault();
}

function loadUsers() {
    usersArea.innerHTML = "";
    var xhr = new XMLHttpRequest();
    var url = "http://localhost:8097/profile/chat/";
    xhr.open("GET", url + chatId, true);
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            Rx.Observable
                .from(JSON.parse(xhr.response))
                .subscribe(msg => onUserReceived(msg));
        }
    }
}

function loadMessages() {
    var xhr = new XMLHttpRequest();
    var url = "http://localhost:8097/message/all";
    var data = JSON.stringify({id: chatId});
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.send(data);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            Rx.Observable
                .from(JSON.parse(xhr.response))
                .subscribe(msg => onMessageReceived(msg));
        }
    }
}

function leaveChat(event) {
    var xhr = new XMLHttpRequest();
    var url = "http://localhost:8097/profile/" + profileId + "/" + chatId;
    xhr.open("DELETE", url, true);
    xhr.send();

    socket.close();

    chatPage.classList.remove('select-chat-page-class');
    chatPage.classList.add('hidden');
    chatCreatePage.classList.remove('hidden');
    chatCreatePage.classList.add('select-chat-page-class');
    loadChats();
    event.preventDefault();
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && socket) {
        var chatMessage = {
            user: {id: profileId, name: username},
            chat: {id: chatId},
            msg: messageContent,
            type: 'CHAT'
        };

        var xhr = new XMLHttpRequest();
        var url = "http://localhost:8097/message";
        var data = JSON.stringify(chatMessage);
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.send(data);
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(message) {
    var textElement = null;
    var messageText = null;
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.user.name + ' joined!';
        textElement = document.createElement('p');
        messageText = document.createTextNode(message.user.name + ' joined!');
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.user.name + ' left!';
        textElement = document.createElement('p');
        messageText = document.createTextNode(message.user.name + ' left!');
    } else {

        if (message.user.id === profileId) {
            messageElement.classList.add('chat-message-reverse');
        }else {
            messageElement.classList.add('chat-message');
        }


        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.user.name[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.user.name);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.user.name);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
        textElement = document.createElement('p');
        messageText = document.createTextNode(message.msg);

    }

    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function onUserReceived(user) {
    var messageElement = document.createElement('li');
    var userNameTmp = null;
    if (user.id === profileId) {
        userNameTmp = user.name + ' (me)';
    }else {
        userNameTmp = user.name;
    }

    messageElement.classList.add('chat-message');

    var avatarElement = document.createElement('i');
    var avatarText = document.createTextNode(userNameTmp[0]);
    avatarElement.appendChild(avatarText);
    avatarElement.style['background-color'] = getAvatarColor(userNameTmp);

    messageElement.appendChild(avatarElement);

    var usernameElement = document.createElement('span');
    var usernameText = document.createTextNode(userNameTmp);
    usernameElement.appendChild(usernameText);
    messageElement.appendChild(usernameElement);

    usersArea.appendChild(messageElement);
    usersArea.scrollTop = usersArea.scrollHeight;
}

function onChatReceived(chat) {
    var chatElement = document.createElement('li');

    chatElement.classList.add('chat-message');

    var chatNameElement = document.createElement('span');
    var usernameText = document.createTextNode(chat.name);
    chatNameElement.appendChild(usernameText);
    chatNameElement.style['background-color'] = getAvatarColor(chat.name);
    chatElement.appendChild(chatNameElement)

    chatArea.appendChild(chatElement);
    chatArea.scrollTop = chatArea.scrollHeight;

    chatElement.addEventListener('click', selectChat, true);
    chatElement.addEventListener('mouseover', function () {
        chatElement.style['background-color'] = "red";
    });
    chatElement.addEventListener('mouseout', function () {
        chatElement.style['background-color'] = "white";
    });
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

chatForm.addEventListener('submit', createChat, true);
usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
leaveChatForm.addEventListener('submit', leaveChat, true);