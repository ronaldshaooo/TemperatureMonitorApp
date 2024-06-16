"use strict";

//---adding all the html tags/items as variables---//

//store html buttons as variables
let startBtn=document.getElementById("startBtn");

//store divs as variables
let leftDiv=document.getElementById("proximityText");
let rightDiv=document.getElementById("lightText");

//---add event listeners to text boxes/buttons---//

//keyboard/mouse click events when entering chatroom
startBtn.addEventListener("click", handleEnterChat);


//---create the socket, global variables---//
let wsOpen=false;
let inChatRoom = false;
let ws = new WebSocket('ws://localhost:8080');

//---handle the websocket---//
ws.onopen=handleOpenCB;
ws.onmessage = function(e) {
    handleMsgCB(e);
};
ws.onclose=handleCloseCB;
ws.onerror=handleErrorCB;


function handleOpenCB(){
    wsOpen=true;
    console.log("Websocket Connection Opened");
}

function handleMsgCB(e){
    let msgObj = JSON.parse(e.data);
    let type = msgObj.type;
    let room = msgObj.room;
    let user = msgObj.user;

    console.log("message type: " + type);

    if(type==="message"){
        let message = msgObj.message;
        console.log("message object: " + msgObj);
        if(message >= 3)
            document.getElementById('proximityText').innerHTML = "遠";
        else if(message < 3 && message >= 1.5)
            document.getElementById('proximityText').innerHTML = "中";
        else
            document.getElementById('proximityText').innerHTML 
        = "近";        
    }

    if (type === "join"){
        let message = msgObj.message;
        console.log("message object: " + msgObj);
        if(message >= 400)
            document.getElementById('lightText').innerHTML = "亮";
        else if(message < 400 && message >= 200)
            document.getElementById('lightText').innerHTML = "中";
        else
            document.getElementById('lightText').innerHTML = "暗";        
    }
}

function handleCloseCB(){
    wsOpen=false;
    console.log("Websocket Connection Closed");
    alert("Websocket Connection Closed");
}

//display any error messages
function handleErrorCB(errorMessage){
    console.error("Server error: " + errorMessage);
}

function handleEnterChat (event){

    if (event.key === "Enter" || event.type === "click"){

        console.log("name is: " + getName());
        console.log("room is: " + getRoom());

        if(getName()!=="" && getRoom()!=="" && !inChatRoom){
            ws.send("join:" + getName() + ":" + getRoom() + ":~");
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
    return "TestPhone";
}

function getRoom(){
    return "TestChannel";
}

function getCurrentTimestamp() {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
}