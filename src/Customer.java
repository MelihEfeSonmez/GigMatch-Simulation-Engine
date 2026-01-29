public class Customer {

    // DATA FIELDS
    private String customerID; // Unique ID of the customer

    private int totalSpent; // Total money paid by the customer
    private String loyaltyTier; // Customer's loyalty tier
    private int totalEmploymentCount; // Total number of employments
    private int customerCancellationCount; // Number of customer-initiated cancellations

    private MyHashTable<String> blacklist; // Blacklist of freelancers
    private MyHashTable<String> activeFreelancers; // Active employed freelancer list

    // CONSTRUCTORS
    // Default Constructor
    public Customer() {
        this.customerID = null;
        this.totalSpent = 0;
        this.loyaltyTier = "BRONZE"; // Starts from BRONZE
        this.totalEmploymentCount = 0;
        this.customerCancellationCount = 0;
        this.blacklist = new MyHashTable<>();
        this.activeFreelancers = new MyHashTable<>();
    }

    public Customer(String customerID) {
        this.customerID = customerID;
        this.totalSpent = 0;
        this.loyaltyTier = "BRONZE"; // Starts from BRONZE
        this.totalEmploymentCount = 0;
        this.customerCancellationCount = 0;
        this.blacklist = new MyHashTable<>();
        this.activeFreelancers = new MyHashTable<>();
    }

    // GETTERS
    public String getCustomerID() {return customerID;}
    public int getTotalSpent() {return totalSpent;}
    public String getLoyaltyTier() {return loyaltyTier;}
    public int getTotalEmploymentCount() {return totalEmploymentCount;}
    public int getCustomerCancellationCount() {return customerCancellationCount;}
    public MyHashTable<String> getBlacklist() {return blacklist;}
    public MyHashTable<String> getActiveFreelancers() {return activeFreelancers;}

    // SETTERS
    public void setLoyaltyTier(String tier) {this.loyaltyTier = tier;}
    public void setCustomerCancellationCount(int count) {this.customerCancellationCount = count;}

    // ---METHODS---
    // BLACKLIST management
    public boolean isInBlacklist(String freelancerID) {
        return blacklist.containsKey(freelancerID);
    }
    public void addToBlacklist(String freelancerID) {
        blacklist.put(freelancerID, freelancerID);
    }
    public void removeFromBlacklist(String freelancerID) {
        blacklist.remove(freelancerID);
    }

    // EMPLOYMENT management
    public void startEmployment(String freelancerID) {
        activeFreelancers.put(freelancerID, freelancerID);
        totalEmploymentCount++;
    }
    public void finishEmployment(String freelancerID) {
        activeFreelancers.remove(freelancerID);
    }

    // Updates total spent by discounted amount
    public void pay(int discountedAmount) {
        this.totalSpent += discountedAmount;
    }

    // Updates the customer's loyalty tier
    public void updateLoyaltyTier() {
        int effectiveSpent = getEffectiveTotalSpent();

        if (effectiveSpent < 500) {
            loyaltyTier = "BRONZE";
        } else if (effectiveSpent < 2000){
            loyaltyTier = "SILVER";
        } else if (effectiveSpent < 5000){
            loyaltyTier = "GOLD";
        } else {
            loyaltyTier = "PLATINUM";
        }
    }

    // Excludes $250 from their total spent for each cancellation
    public int getEffectiveTotalSpent() {
        return Math.max(0, totalSpent - (customerCancellationCount * 250));
    }

}
