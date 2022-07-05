# Distributed Search System

## Prerequisites
* Java 11
* Maven 3

## Steps to run the system
1. Clone the repository
2. Start the bootstrap server
3. Start the nodes
4. Search and download a file in any node

### Start the bootstrap server
1. Build the bootstrap server
    ```bash
    cd <path-to-the-cloned-project>/bootstrap-server
    mvn clean install
    ```
2. Start the bootstrap server
    ```bash
    java -jar target/dfs-0.0.1-SNAPSHOT.jar
    ```
   This will start the Bootstrap server at port `55555`. If you want to pass a different port, use the command below.
   ```bash
   java -jar target/dfs-0.0.1-SNAPSHOT.jar --server.port=<bootstrap-server-port>
   ```
   
   Once the server is started it waits for the nodes to join. The following log will be seen when the server is started.
   ```
    Bootstrap Server created at 55555. Waiting for nodes to join...
   ```

### Start the nodes   
1. Build the node
    ```bash
    cd <path-to-the-cloned-project>/node
    mvn clean install
    ```
2. Start the node
    ```bash
    java -jar target/dfs-0.0.1-SNAPSHOT.jar --server.port=<node-port>
    ```
   The node by default looks for the Bootstrap server on port `55555`. If you passed a custom port when starting the Bootstrap 
   server, use the command below to start the node.
   ```bash
    java -jar target/dfs-0.0.1-SNAPSHOT.jar --server.port=<node-port> --bootstrap.server.port=<bootstrap-server-port>
   ```
   When the node starts, it accepts queries to be searched. The following log will be seen when the node is started.
   ```
   .
   .
   .
   INFO : This node : 172.20.0.1:5001
   INFO : Create file storage location for node at /tmp/ds-group-d/nodes/5001
   INFO : File list added to this node: 
   		Harry Potter
   		Mission Impossible
   		Adventures of Tintin
   		King Arthur
   		American Idol
   localhost/127.0.0.1
   INFO : SEND: Bootstrap server register message '0033 REG 172.20.0.1 5001 node5001'
   INFO : RECEIVE: Bootstrap server response '0012 REGOK 0'
   INFO : Peers : []
   INFO : Enter a query : 
   INFO : Started listening on '5001' for incoming data...
   ```
3. Check the files saved in the storage location dedicated for this node:
   ```bash
   ls /tmp/ds-group-d/nodes/5001
   ```
   You should see the same file list shown in the log.
   
4. Starting another node  
    Repeat step two with different value given for `<node-port>`
    ```
    .
    .
    .
    INFO : This node : 172.20.0.1:5002
    INFO : Create file storage location for node at /tmp/ds-group-d/nodes/5002
    INFO : File list added to this node: 
   		Harry Potter
   		Glee
   		Super Mario
   		King Arthur
   		Kung Fu Panda
    localhost/127.0.0.1
    INFO : SEND: Bootstrap server register message '0033 REG 172.20.0.1 5002 node5002'
    INFO : RECEIVE: Bootstrap server response '0028 REGOK 1 172.20.0.1 5001'
    INFO : Peers : [172.20.0.1:5001]
    INFO : SEND: Join message to '172.20.0.1:5001'
    INFO : RECEIVE: 0013 JOINOK 0 from '172.20.0.1:5001'
    INFO : UPDATE: Routing table : [172.20.0.1:5001]
    INFO : Enter a query : 
   ```
    
### Search and download a file in any node

1. Enter any file name in any node and press `Enter`
   
   E.g. Let's assume that the nodes started and the files are stored as shown in step 2 and 3.
    * Enter a file that is available in the current node. According the above logs `King Arthur` in available in the first node.
      When you enter `King Arthur` in the first node, you will see the following log on the same node.
      ```
      INFO : FOUND: Searched file found in current node '172.20.0.1:5001' as '[King Arthur]'
      ```
    
    * Enter a file that is not available in the current node but available in another node. 
      When you enter `Super Mario` in the first node, you will see the following log in the same node.
      ```
      INFO : RECEIVE: Search results received from '/192.168.1.15:59920' as '0048 SEROK 1 172.20.0.1 5002 "Super Mario" 5 0.0'
      INFO : Epoch timestamp in millis: 1657044554746 at DOWNLOAD START REQUEST
      INFO : Requested file downloaded successfully.
      INFO : Requested file downloaded successfully.
      INFO : Epoch timestamp in millis: 1657044555115 at DOWNLOAD SUCCESSFUL
      ```
2. Check the files saved in the storage location dedicated for this node:
      ```bash
      ls /tmp/ds-group-d/nodes/5001
      ```
      You should see a new file named `Super Mario.txt` additionally.
      