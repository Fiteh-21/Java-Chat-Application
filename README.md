# Multi-Threaded Java Chat Application

A real-time chat system built using **Java Sockets** and **Swing GUI**. This project features a central server that manages multiple client connections simultaneously.

## 🚀 Features

- **Multi-Client Support:** Handles multiple users at once using `ClientHandler` threads.
- **Interactive GUI:** Built with Java Swing, featuring a scrollable chat area and message input.
- **Name Registration:** Users are prompted to enter a nickname upon joining.
- **Broadcasting:** Messages from one user are sent to all other connected clients.

## 🛠️ Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher.

### Installation & Execution

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/Fiteh-21/Java-Chat-Application.git](https://github.com/Fiteh-21/Java-Chat-Application.git)
    cd Java-Chat-Application
    ```
2.  **Compile the code:**
    ```bash
    javac Server.java Client.java
    ```
3.  **Run the Server:**
    ```bash
    java Server
    ```
4.  **Run the Client(s):**
    (Open a new terminal for each client you want to add)
    ```bash
    java Client
    ```

## 📂 Code Logic

- **Server.java**: Uses a `ServerSocket` to listen on port `12345`. It maintains a `Set` of active clients and broadcasts messages to them.
- **Client.java**: Connects to `localhost` via a `Socket`. It runs a background thread to listen for incoming messages so the GUI remains responsive.
