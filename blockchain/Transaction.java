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
        //if sender is null, this was issued by the chain for mining a block successfully
        if (sender == null) {
            return receiver.getName() + " gets " + this.amount + "VC";
        }
        return sender.getName() + " sent " + this.amount + " VC to " + receiver.getName();
    }
}
