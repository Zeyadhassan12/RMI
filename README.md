# RMI
a distributed system using Java's RMI (Remote Method Invocation) for managing file operations across multiple nodes, with elements of Raft consensus protocol for leader election.


This Java project is a distributed file management system built using Java RMI (Remote Method Invocation). The system enables nodes to coordinate file operations (such as download, upload, search, and delete) across a distributed network. Additionally, it implements an acknowledgment mechanism to ensure each node is updated about the operations and introduces a Raft-inspired mechanism for node role assignment, supporting leader election.

Features
File Operations: Each node can handle file upload, download, search, and delete operations, and will multicast these operations to other nodes in the network.
Delayed Acknowledgments: Custom delays simulate network latency for acknowledgment responses, with the AckDelayer and Delayer classes.
Raft-Like Leader Election: The project simulates the Raft consensus algorithm by assigning leader, follower, and candidate roles to nodes in the RaftNode class, with the leader handling file operations.
Classes
Main: Initializes the nodes and starts the distributed system.
Node: Implements core node functionalities, including handling file operations and acknowledgments. It also tracks the clock for each operation.
RaftNode: Extends Node to manage node roles (Leader, Follower, Candidate) and starts the election process when necessary.
Operations: Represents a file operation (download, upload, search, delete) and includes metadata like operation ID, sender, and clock.
Delayer & AckDelayer: Used to simulate network delay for operation and acknowledgment messages.
NodeI Interface: Defines the methods each node implements, including performOperation, ack, downloadFile, uploadFile, searchFiles, and deleteFile.
How to Use
Compile all Java files and ensure Java RMI is properly set up.
Run the Main class, which initializes nodes and starts the system.
Follow the prompts in the console to select file operations, and observe each node's response and acknowledgment handling.
Raft leader election will occur based on the node's state and network conditions, influencing which node becomes responsible for certain operations.
Folder Structure
Main Directory: Contains the core files for initiating nodes and file operations.
Node and Operation Classes: Define file operation handling and distributed functionality.
Requirements
Java 8+
Basic understanding of distributed systems and Java RMI
