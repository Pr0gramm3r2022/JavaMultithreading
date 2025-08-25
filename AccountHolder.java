

abstract class AccountHolder implements Runnable {
private Account account;

public AccountHolder(Account account) {
    this.account = account;
}

class subclass extends Account {
    /*had issues when I just had override so I
    inherited from the Account class to be able to use the withdraw method*/
    @Override
    public void withdraw(int amount) {
        super.withdraw(amount);
    }

    public void run() {
        for (int i = 1; i <= 4; i++) {
            withdraw(2000);

            if (account.getBalance() < 0) {
                System.out.println("your account is overdrawn");
            }
        }
    }
}}
/*will need to reference this class if you want to implement
methods in the override*/

/*private void MakeWithdrawal(int withdrawAmount) {
   System.out.println(Thread.currentThread().getName() + " is going to withdraw $" + withdrawAmount);
    try {
        Thread.sleep(3000);
    }
}
}*/
