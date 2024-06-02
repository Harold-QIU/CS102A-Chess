# ChessProject

<img src="fig/headline.png" alt="alt text" style="zoom: 67%;" />

Honorly developed by Junjie Qiu (Haro) and Ruiyang Wu in CS102A at SUSTech in 2022. The project is selected as the excellent project to present in class (top 10\%). 

## Features

- **Local Chess Game**: Play chess with your friend on the same computer. You can undo, save and read the game anytime you time. A pleasant music is played in the background and the sound effect is triggered when you make a move.

- **Intelligent Tools**: The program provides a chessboard analysis tool that can help you understand where you can move to. Also, checkmate detection is implemented to help you detect whether you are in a checkmate situation.

- **Stupid AI**: The program provides a stupid AI that can play chess with you. The AI is not very smart and can be easily defeated. However, it can be a good practice for beginners.

- **Remote Chess Game**: Play chess with your friend remotely under the same LAN. The program provides a server to host the game and a client to connect to the server. You can play chess with your friend on different computers. You can also be a remote witness of the game.

- **Remote Chatting**: The program provides a chat room for you to chat with your friend during a remote game. You can send messages to your friend and the messages will be displayed on the screen.

- **Various Themes**: The program provides 4 themes for you to choose from. You can choose the theme you like by clicking the `Check` botton during a game and the chessboard will be displayed in the theme you choose.

## Installation

JDK 17 is required to run this project. You can download it from [here](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html). The `./ChessDemo` folder must be set as the working directory of the project.

## Game Mannual

Run the `./src/Main.java` file to start the main program. You can also run the `./out/artifacts/ChessDemo_jar/demo.exe` directly to start the program.

Run the `./src/web/Server.java` file to start the server to host the remote chess game. Note down the IPv4 address of the server. The sever will take up 8888 port and computers in the same LAN can connect to the server by typing in the IPv4 address.

<img src="fig/ip.png" alt="alt text" style="zoom: 67%;" />

### Local Game

Start a local game with full features by clicking the `Start Local Game` button.

<img src="fig/local.png" alt="alt text" style="zoom: 43%;" />

### AI Game

Start a game with AI by clicking the `Start AI Game` button. The AI game does not support store and read features.

<img src="fig/ai.png" alt="alt text" style="zoom: 43%;" />

### Web Game and Witness

Please make sure the server is running before starting a remote game. Start a remote game by clicking the `Start Web Game` button and connect to the server by typing in the IPv4 address of the server. By clicking the `Start Witness` button, you can be a remote witness of the game.

<img src="fig/net.png" alt="alt text" style="zoom: 43%;" />

You can chat with everyone in the game by typing in the chat box and clicking the `Send` button.

## Acknowledgement

Thanks Jingda Dong for what we learned about java web from him.