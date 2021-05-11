# SocketTest (jm)

Modified version from Akshathkumar Shetty's SocketTest, which can be found here:
[GitHub](https://github.com/akshath/SocketTest)

It has the following features compared to the orignal SocketTest:
 * Print every received text from the connection in a new line and prefix it with `R: `, like it is done for every sent text with `S: `.

## Build JAR
Open a console from your SocketTest project folder and enter:

```console
./gradlew build
```

You can find the generated JAR file within the following folder:

```console
SocketTest\build\libs
```

## Original README

A java tool for socket testing. It can create both TCP and UDP client or server. It can be used to test any server or client that uses TCP or UDP protocol to communicate.

Licence: GNU Lesser General Public License

Features:
 * Create a TCP client socket and send commands.
 * Create a UDP client socket and send commands.
 * Create a TCP server socket and send responses to connected clients.
 * Create a UDP server socket that listens on a particular port.
 * Save the conversation with the client or host to a txt file.
 * TrustManager to prompt if certificate does not get validated.

Copyright © 2003-2008 Akshathkumar Shetty

@Update
 * added to program read from command line values
