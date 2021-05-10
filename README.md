# Blockchain-Simulator
The Blockchain Generator is used as an example of how a simple Blockchain program might work.
The Blockchain implements the following features:
- Checks validity of blocks sent from virtual miners using a magic number. When blocks are generated too quickly, the "N" number is increased, thus increasing the number of 0's that the block's hash must be prefixed with. When valid blocks take too long to find, this "N" number is decreased.
- Multithreading is used to simulate virtual miners. 5 miners will be generated at runtime, each attempting to create valid blocks.
- Virtual Coins (VC) - miners are given 100 initial coins, and can win more coins by successfully adding blocks to the blockchain. While blocks are being added, miners can send each other VCs. Each block keeps track of coins sent prior to being added. These transactions are verified before being added.
- Transaction encryption: private and public keys are generated, and the Blockchain can decrypt these using RSA.
Transactions are sent as signed messages, and private keys are written as text files locally. Once the encrypted message reaches the blockchain, it is decrypted using the private key and added to the block.

# How to use
Download blockchain-simulator.jar and open it using the following command:

java -jar blockchain-simulator.jar

Alternatively, to run the source code simply run the Main class.

Enter how many blocks you want the program to mine. It is recommended you keep this number relatively small (between 5-15), but this varies upon your CPU - a CPU with more cores will allow the miners to work better concurrently, and therefore will find the blocks faster. 
Note it may take several seconds or even minutes for each block as the N number increases.
