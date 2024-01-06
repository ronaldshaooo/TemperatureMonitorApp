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
ws.onmessage=handleMsgCB; 
ws.onclose=handleCloseCB; 
ws.onerror=handleErrorCB; 


function handleOpenCB(){
    wsOpen=true; 
    console.log("Websocket Connection Opened"); 
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
        let outText=document.createTextNode(user + ": " + message)
        
        rightDiv.appendChild(lineBreak); 
        rightDiv.appendChild(outText); 

    }

    if (type==="join"){
        
        let outText = document.createTextNode(user + " joined " + room + ".");
        let chatParticipants=document.createTextNode(user + " is in this chat       "+" "); 
    
        leftDiv.appendChild(lineBreak);
        leftDiv.appendChild(chatParticipants);

        rightDiv.appendChild(lineBreak);
        rightDiv.appendChild(outText);

    }

    if(type==="leave"){

        let outText = document.createTextNode(user + " left " + room + ".");

        rightDiv.appendChild(lineBreak);
        rightDiv.appendChild(outText);
    }

}

function handleCloseCB(){
    wsOpen=false;
    console.log("Websocket Connection Opened"); 
}

//Display the error? What kind of error messages will we be getting? 
function handleErrorCB(errorMessage){

    console.error("Server error: " + errorMessage);
  
    alert("Server error: " + errorMessage);

}

function handleEnterChat (event){

    if (event.key === "Enter" || event.type === "click"){ 
        let name=nameTxt.value.toLowerCase(); 
        let room=roomTxt.value.toLowerCase(); 

        console.log("name is: " + name);
        console.log("room is: " + room); 

        if(name!=="" && room!=="" && !inChatRoom){
            ws.send("join " + name + " " + room); 
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

function handleSendMessage(event){

    if (event.key === "Enter" || event.type === "click"){
        let message=messageTxt.value; 
        console.log("message is: "+ message); 

        if(message!==""){
            ws.send("message " + message);
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
    
        ws.send("leave"); 
    }
}
