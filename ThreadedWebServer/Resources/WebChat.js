"use strict";

//---adding all the html tags/items as variables---//

//stores all of the html buttons as variables
let enterChatBtn=document.getElementById("enterChatBtn");
let messSendBtn=document.getElementById("sendBtn");
let leaveBtn=document.getElementById("leaveRoomBtn");

//stores all of the text boxes as variables
let nameTxt=document.getElementById("UserName");
let roomTxt=document.getElementById("ChatRoom");
let messageTxt=document.getElementById("Message");

//stores the divs as variables
let leftDiv=document.getElementById("leftDiv");
let rightDiv=document.getElementById("rightDiv");

//---adding event listeners to all text boxes and buttons---//

//handle entering keyboard/mouse click events when entering the chat room
enterChatBtn.addEventListener("click", handleEnterChat);
nameTxt.addEventListener("keypress", handleEnterChat);
roomTxt.addEventListener("keypress", handleEnterChat);

//handle keyboard/mouse click events when sending a message
messSendBtn.addEventListener("click", handleSendMessage);
messageTxt.addEventListener("keypress", handleSendMessage);

//handle keyboard/mouse click events for leaving the chatroom
leaveBtn.addEventListener("click", handleleaveChat);


//---creating the socket, global variables---//
let wsOpen=false;
let inChatRoom = false;
let ws = new WebSocket('ws://localhost:8080');

//---handling the websocket---//
ws.onopen=handleOpenCB;
ws.onmessage = function(e) {
    handleMsgCB(e);
};
ws.onclose=handleCloseCB;
ws.onerror=handleErrorCB;


function handleOpenCB(){
    wsOpen=true;
    console.log("Websocket Connection Opened");
    // alert("Websocket Connection Opened");
}

function handleMsgCB(e){
    let msgObj = JSON.parse(e.data);
    let type=msgObj.type;
    let room = msgObj.room;
    let user = msgObj.user;
    let message=msgObj.message;

    let lineBreak = document.createElement("br");

    console.log("message object: " + msgObj);
    console.log("message type: " + type);

    if(type==="message"){
        const timestamp=getCurrentTimestamp();

        let timeText = document.createElement('span');
        timeText.textContent = `[${timestamp}] `;
        timeText.classList.add('timestamp');

        let outText=document.createTextNode(user + ": " + message);


        rightDiv.appendChild(lineBreak);
        rightDiv.appendChild(timeText);
        rightDiv.appendChild(outText);

    }

    if (type === "join"){
        let chatParticipants = document.createElement("p");
        chatParticipants.textContent = user;
        chatParticipants.id = user;

        let outText = document.createTextNode(user + " joined " + room);

        leftDiv.appendChild(lineBreak);
        leftDiv.appendChild(chatParticipants);
        rightDiv.appendChild(lineBreak);
        rightDiv.appendChild(outText);
    }

    if (type === "leave"){
        let outText = document.createTextNode(user + " left " + room + ".");

        // Remove the element with the specified id
        const chatParticipant = document.getElementById(user);
        if (chatParticipant) {
            chatParticipant.parentNode.removeChild(chatParticipant);
        }

        rightDiv.appendChild(lineBreak);
        rightDiv.appendChild(outText);
    }
}
function handleCloseCB(){
    wsOpen=false;
    console.log("Websocket Connection Closed");
    alert("Websocket Connection Closed");
}

//Display the error? What kind of error messages will we be getting?
function handleErrorCB(errorMessage){

    console.error("Server error: " + errorMessage);

    //alert("Server error: " + errorMessage);

}

function handleEnterChat (event){

    if (event.key === "Enter" || event.type === "click"){

        console.log("name is: " + getName());
        console.log("room is: " + getRoom());

        if(getName()!=="" && getRoom()!=="" && !inChatRoom){
            ws.send("join:" + getName() + ":" + getRoom());
            inChatRoom = true;
        }

        else if (inChatRoom) {
            alert("You are already in a chat room. Please leave the current room before joining another one.");
            return;
        }

        else{
            alert("Incorrect entry, please try again.");
            return;
        }
    }
}

function getName(){
    return nameTxt.value.toLowerCase();
}

function getRoom(){
    return roomTxt.value.toLowerCase();
}

function handleSendMessage(event){

    if (event.key === "Enter" || event.type === "click"){
        let message=messageTxt.value;

        console.log("message is: "+ message);

        if(message!==""){
            ws.send("message:" + getName() + ":" + getRoom() + ":" + message);
            messageTxt.value = "";
        }
        else{
            alert("Entry is null, please try again");
            return;
        }
    }
}

function handleleaveChat(event){

    console.log("leave button pressed");
    if (event.type==="click"){

        document.getElementById("leftDiv").innerHTML = "";
        document.getElementById("rightDiv").innerHTML = "";

        inChatRoom=false;

        ws.send("leave:" + getName() + ":" + getRoom());
    }
}

function getCurrentTimestamp() {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
}