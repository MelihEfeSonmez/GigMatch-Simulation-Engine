import java.util.Locale;

public class Freelancer implements Comparable<Freelancer> {

    // DATA FIELDS
    private String freelancerID; // Unique ID of the freelancer

    private String serviceType; // Service type
    private int servicePrice; // Price of the service

    // Skill values
    private int T; // Technical proficiency
    private int C; // Communication
    private int R; // Creativity
    private int E; // Efficiency
    private int A; // Attention to detail

    private boolean isAvailable; // Returns availability
    private boolean isBurnout; // Returns isBurnout status
    private boolean isPlatformBanned; // Returns ban status

    private double averageRating; // Avg rating of freelancer
    private int ratingCount; // Rating number

    private int compositeScore; // For priority queue ordering

    private int completedJobs; // Number of completion
    private int cancelledJobs; // Number of cancellation
    private int monthlyCompletedJobs; // For isBurnout tracking
    private int monthlyCancelledJobs; // For platform ban tracking

    private String queuedService; // Service type requested for future
    private int queuedPrice; // New price
    private boolean hasQueuedChange; // Returns whether there is any pending

    private String employerCustomerID; // Stores who employed

    // CONSTRUCTORS
    // Default Constructor
    public Freelancer() {
        this.freelancerID = null;

        this.serviceType = null;
        this.servicePrice = 0;

        this.T = 0;
        this.C = 0;
        this.R = 0;
        this.E = 0;
        this.A = 0;

        this.isAvailable = true;
        this.isBurnout = false;
        this.isPlatformBanned = false;

        // Every freelancer starts with 1 rating of 5 stars
        this.averageRating = 5.0;
        this.ratingCount = 1;

        this.compositeScore = 0;

        this.completedJobs = 0;
        this.cancelledJobs = 0;
        this.monthlyCompletedJobs = 0;
        this.monthlyCancelledJobs = 0;

        this.queuedService = null;
        this.queuedPrice = 0;
        this.hasQueuedChange = false;

        this.employerCustomerID = null;
    }

    public Freelancer(String freelancerID, String serviceType, int servicePrice, int T, int C, int R, int E, int A) {

        this.freelancerID = freelancerID;
        this.serviceType = serviceType;
        this.servicePrice = servicePrice;

        this.T = T;
        this.C = C;
        this.R = R;
        this.E = E;
        this.A = A;

        this.isAvailable = true;
        this.isBurnout = false;
        this.isPlatformBanned = false;

        // Every freelancer starts with 1 rating of 5 stars
        this.averageRating = 5.0;
        this.ratingCount = 1;

        this.compositeScore = 0;

        this.completedJobs = 0;
        this.cancelledJobs = 0;
        this.monthlyCompletedJobs = 0;
        this.monthlyCancelledJobs = 0;

        this.queuedService = null;
        this.queuedPrice = 0;
        this.hasQueuedChange = false;

        this.employerCustomerID = null;
    }

    // GETTERS
    public String getFreelancerID() {return freelancerID;}
    public String getServiceType() {return serviceType;}
    public int getServicePrice() {return servicePrice;}

    public boolean isAvailable() {return isAvailable;}
    public boolean isBurnout() {return isBurnout;}
    public boolean isPlatformBanned() {return isPlatformBanned;}

    public int getT() {return T;}
    public int getC() {return C;}
    public int getR() {return R;}
    public int getE() {return E;}
    public int getA() {return A;}

    public double getAverageRating() {return averageRating;}
    public int getRatingCount() {return ratingCount;}

    public int getCompositeScore() {return compositeScore;}

    public int getCompletedJobs() {return completedJobs;}
    public int getCancelledJobs() {return cancelledJobs;}
    public int getMonthlyCompletedJobs() {return monthlyCompletedJobs;}
    public int getMonthlyCancelledJobs() {return monthlyCancelledJobs;}

    public String getQueuedService() {return queuedService;}
    public int getQueuedPrice() {return queuedPrice;}
    public boolean getHasQueuedChange() {return hasQueuedChange;}

    public String getEmployerCustomerID() {return employerCustomerID;}

    // SETTERS
    public void setFreelancerID(String freelancerID) {this.freelancerID = freelancerID;}
    public void setServiceType(String serviceType) {this.serviceType = serviceType;}
    public void setServicePrice(int servicePrice) {this.servicePrice = servicePrice;}

    public void setT(int T) {this.T = T;}
    public void setC(int C) {this.C = C;}
    public void setR(int R) {this.R = R;}
    public void setE(int E) {this.E = E;}
    public void setA(int A) {this.A = A;}

    public void setAvailable(boolean available) {this.isAvailable = available;}
    public void setBurnout(boolean burnout) {this.isBurnout = burnout;}
    public void setPlatformBanned(boolean platformBanned) {this.isPlatformBanned = platformBanned;}

    public void setAverageRating(double averageRating) {this.averageRating = averageRating;}
    public void setRatingCount(int ratingCount) {this.ratingCount = ratingCount;}

    public void setCompositeScore(int compositeScore) {this.compositeScore = compositeScore;}

    public void setCompletedJobs(int completedJobs) {this.completedJobs = completedJobs;}
    public void setCancelledJobs(int cancelledJobs) {this.cancelledJobs = cancelledJobs;}
    public void setMonthlyCompletedJobs(int monthlyCompletedJobs) {this.monthlyCompletedJobs = monthlyCompletedJobs;}
    public void setMonthlyCancelledJobs(int monthlyCancelledJobs) {this.monthlyCancelledJobs = monthlyCancelledJobs;}

    public void setEmployerCustomerID(String employerCustomerID) {this.employerCustomerID = employerCustomerID;}

    // ---METHODS---
    // Employment management
    public boolean employ(String customerID) {
        if (!isAvailable || isPlatformBanned) return false;
        this.isAvailable = false;
        this.employerCustomerID = customerID;
        return true;
    }
    public void free() {
        this.isAvailable = true;
        this.employerCustomerID = null;
    }

    // Completes job (rating and skills update)
    public void completeJob(int rating, int[] serviceSkills) {

        int n = ratingCount;
        averageRating = ((averageRating * n) + rating) / (n + 1.0); // Updates rating
        ratingCount++;

        completedJobs++;
        monthlyCompletedJobs++;

        if (rating >= 4 && serviceSkills != null && serviceSkills.length == 5) {
            applySkillGains(serviceSkills); // Applies skill gains
        }

        free();
    }

    // Applies skill gains after completion
    private void applySkillGains(int[] serviceSkills) {

        int[] indexes = {0, 1, 2, 3, 4};

        // Bubble sort
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 - i; j++) {

                boolean shouldSwap = false; // For tie-breaking

                if (serviceSkills[j] < serviceSkills[j + 1]) {
                    shouldSwap = true;
                } else if (serviceSkills[j] == serviceSkills[j + 1] && indexes[j] > indexes[j + 1]) { // tie-breaker
                    shouldSwap = true;
                }

                if (shouldSwap) {
                    // Swap serviceSkills
                    int tmp = serviceSkills[j];
                    serviceSkills[j] = serviceSkills[j + 1];
                    serviceSkills[j + 1] = tmp;

                    // Swap indexes
                    int tmpi = indexes[j];
                    indexes[j] = indexes[j + 1];
                    indexes[j + 1] = tmpi;
                }
            }
        }

        // Finds primary and secondaries
        gainSkill(indexes[0], 2);
        gainSkill(indexes[1], 1);
        gainSkill(indexes[2], 1);
    }

    // Applies skill gains
    private void gainSkill(int index, int amount) {
        if (index == 0) {
            T = Math.min(100, T + amount);
        } else if (index == 1) {
            C = Math.min(100, C + amount);
        } else if (index == 2) {
            R = Math.min(100, R + amount);
        } else if (index == 3) {
            E = Math.min(100, E + amount);
        } else if (index == 4) {
            A = Math.min(100, A + amount);
        }
    }

    // Cancels job (freelancer-initiated)
    public void cancelJob() {

        int n = ratingCount;
        averageRating = ((averageRating * n) + 0) / (n + 1.0); // Updates rating
        ratingCount++;

        cancelledJobs++;
        monthlyCancelledJobs++;

        // Applies -3 degradation
        T = Math.max(0, T - 3);
        C = Math.max(0, C - 3);
        R = Math.max(0, R - 3);
        E = Math.max(0, E - 3);
        A = Math.max(0, A - 3);

        free();
    }

    // Monthly simulation
    public void updateMonthlyStatus() {

        // isBurnout or recovery
        if (!isBurnout && monthlyCompletedJobs >= 5){
            isBurnout = true;
        } else if (isBurnout && monthlyCompletedJobs <= 2){
            isBurnout = false;
        }

        // Bans for 5 cancellations in one month
        if (monthlyCancelledJobs >= 5) {isPlatformBanned = true;}

        // Resets counters
        monthlyCompletedJobs = 0;
        monthlyCancelledJobs = 0;

        // Applies queued service change
        if (hasQueuedChange) {
            serviceType = queuedService;
            servicePrice = queuedPrice;
            hasQueuedChange = false;
            queuedService = null;
            queuedPrice = 0;
        }
    }

    // Changes queued service
    public void queueServiceChange(String newService, int newPrice) {
        this.queuedService = newService;
        this.queuedPrice = newPrice;
        this.hasQueuedChange = true;
    }

    // Helper for output
    public String getInfo() {
        String ratingStr = String.format(Locale.US, "%.1f", averageRating);

        String availableStr;
        if (isAvailable) {availableStr = "yes";}
        else {availableStr = "no";}

        String burnoutStr;
        if (isBurnout) {burnoutStr = "yes";}
        else {burnoutStr = "no";}

        return freelancerID + ": " + serviceType
                + ", price: " + servicePrice
                + ", rating: " + ratingStr
                + ", completed: " + completedJobs
                + ", cancelled: " + cancelledJobs
                + ", skills: (" + T + "," + C + "," + R + "," + E + "," + A + ")"
                + ", available: " + availableStr
                + ", burnout: " + burnoutStr;
    }

    // Compares freelancer with another
    public int compareTo(Freelancer other) {
        // Compare by composite score
        if (this.compositeScore != other.compositeScore) {
            return Integer.compare(other.compositeScore, this.compositeScore);
        }

        // Tie-breaker
        return this.freelancerID.compareTo(other.freelancerID);
    }

}
