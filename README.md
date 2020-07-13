# Blockchain-Simulator
The Blockchain Generator is used as an example of how a simple Blockchain program might work.
The Blockchain implements the following features:
- Checks validity of blocks sent from virtual miners using a magic number. When blocks are generated too quickly, the "N" number is increased, increasing the number of 0's that the block's hash must be prefixed with. When valid blocks take too long to find, this "N" number is decreased.
- Multithreading is used to simulate virtual miners. 10 miners will be generated at runtime, each attempting to create valid blocks.
- Virtual Coins (VC) - miners are given 100 initial coins, and can win more coins by successfully adding blocks to the blockchain. While blocks are being added, miners can send each other VCs. Each block keeps track of coins sent prior to being added. These transactions are verified before being added.
- Transaction encryption: private and public keys are generated, and the Blockchain can decrypt these using RSA.
Transactions are sent as signed messages, and private keys are written as text files locally.
