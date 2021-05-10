package blockchain;

public class Transaction {
    Miner sender;
    Miner receiver;
    int amount;

    public Transaction(Miner sender, Miner receiver, int amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public Miner getSender() {
        return sender;
    }

    public Miner getReceiver() {
        return receiver;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
    	// Transactions can either be send as Blockchain => miner, or miner => other miner
        // If sender is null, this was issued by the Blockchain for mining a block successfully (and therefore there is no sender)
        if (sender == null) {
            return receiver.getName() + " gets " + this.amount + "VC";
        }
        return sender.getName() + " sent " + this.amount + " VC to " + receiver.getName();
    }
}
