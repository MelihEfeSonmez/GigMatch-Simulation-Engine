import java.util.ArrayList;
import java.util.Locale;

public class PlatformManager {

    // DATA FIELDS
    private MyHashTable<Customer> customers; // Stores all customers
    private MyHashTable<Freelancer> freelancers; // Stores all registered freelancers

    private ArrayList<Employment> employments; // List of all employment records
    private MyHashTable<Employment> activeEmployments; // Stores active employments

    private MyHashTable<Customer> pendingLoyaltyUpdates; // Stores customers who need loyalty tier update
    private MyPriorityQueue<Freelancer>[] freelancerPQByService; // PQ for each service type

    // Service type constants
    private static final String[] SERVICE_TYPES = {
            "paint", "web_dev", "graphic_design", "data_entry", "tutoring",
            "cleaning", "writing", "photography", "plumbing", "electrical"
    };

    // Skill profiles [T, C, R, E, A]
    private static final int[][] SKILL_PROFILES = {
            {70, 60, 50, 85, 90},   // paint
            {95, 75, 85, 80, 90},   // web_dev
            {75, 85, 95, 70, 85},   // graphic_design
            {50, 50, 30, 95, 95},   // data_entry
            {80, 95, 70, 90, 75},   // tutoring
            {40, 60, 40, 90, 85},   // cleaning
            {70, 85, 90, 80, 95},   // writing
            {85, 80, 90, 75, 90},   // photography
            {85, 65, 60, 90, 85},   // plumbing
            {90, 65, 70, 95, 95}    // electrical
    };

    // CONSTRUCTORS
    // Default Constructor
    public PlatformManager() {
        customers = new MyHashTable<>();
        freelancers = new MyHashTable<>();

        employments = new ArrayList<>();
        activeEmployments = new MyHashTable<>();

        pendingLoyaltyUpdates = new MyHashTable<>();

        MyPriorityQueue<Freelancer>[] temp = new MyPriorityQueue[SERVICE_TYPES.length];
        freelancerPQByService = temp;
        // One priority queue for each service type
        for (int i = 0; i < SERVICE_TYPES.length; i++) {
            freelancerPQByService[i] = new MyPriorityQueue<>();
        }
    }

    // ---METHODS---
    // 1) Customer registration
    public String registerCustomer(String customerID) {
        // Validation
        if (customerID == null || customerID.trim().isEmpty()
                               || customers.containsKey(customerID)
                               || freelancers.containsKey(customerID)) {
            return "Some error occurred in register_customer.";
        }

        // Creates and stores new customer
        Customer customer = new Customer(customerID);
        customers.put(customerID, customer);

        return "registered customer " + customerID;
    }

    // 2) Freelancer registration
    public String registerFreelancer(String freelancerID, String serviceType, int servicePrice,
                                     int T, int C, int R, int E, int A) {
        // Validation
        if (freelancerID == null || freelancerID.trim().isEmpty()
                                 || !isValidService(serviceType)
                                 || servicePrice <= 0
                                 || !validSkill(T, C, R, E, A)
                                 || freelancers.containsKey(freelancerID)
                                 || customers.containsKey(freelancerID)) {
            return "Some error occurred in register_freelancer.";
        }

        // Creates and stores new freelancer
        Freelancer freelancer = new Freelancer(freelancerID, serviceType, servicePrice, T, C, R, E, A);
        freelancers.put(freelancerID, freelancer);

        // Adds freelancer to the service's priority queue
        addFreelancerToPQ(freelancer);

        return "registered freelancer " + freelancerID;
    }

    // 3) Employing a specific freelancer
    public String employ(String customerID, String freelancerID) {
        Customer customer = customers.get(customerID);
        Freelancer freelancer = freelancers.get(freelancerID);

        // Validation
        if (customer == null || freelancer == null
                || freelancer.isPlatformBanned()
                || customer.isInBlacklist(freelancerID)
                || !freelancer.isAvailable()) {
            return "Some error occurred in employ.";
        }

        // Marks freelancer as employed
        if (!freelancer.employ(customerID)) {
            return "Some error occurred in employ.";
        }
        customer.startEmployment(freelancerID);

        // Creates an employment and stores
        Employment employment = new Employment(customer, freelancer);
        employments.add(employment);

        // Adds to activeEmployments
        String key = makeEmploymentKey(customerID, freelancerID);
        activeEmployments.put(key, employment);

        return customerID + " employed " + freelancerID + " for " + freelancer.getServiceType();
    }

    // 4) Requesting a job
    public String requestJob(String customerID, String serviceType, int k) {

        Customer customer = customers.get(customerID);
        // Validation
        if (customer == null || k <= 0 || !isValidService(serviceType)) {
            return "Some error occurred in request_job.";
        }

        int serviceindex = getServiceIndex(serviceType);
        if (serviceindex == -1) {
            return "Some error occurred in request_job.";
        }

        // Gets the PQ for this service
        MyPriorityQueue<Freelancer> pq = freelancerPQByService[serviceindex];
        if (pq == null || pq.isEmpty()) {
            return "no freelancers available";
        }

        // Stores all polled freelancer
        ArrayList<Freelancer> polled = new ArrayList<>();

        // Available top-k freelancers
        ArrayList<Freelancer> chosen = new ArrayList<>();
        ArrayList<Integer> chosenScores = new ArrayList<>();

        // Extracts freelancers
        while (!pq.isEmpty() && chosen.size() < k) {
            Freelancer f = pq.poll(); // en iyi freelancer
            if (f == null) continue;
            polled.add(f);

            // If service changed after simulateMonth, skip
            if (!serviceType.equals(f.getServiceType())) {
                continue;
            }

            // Availability checks
            if (!f.isAvailable()) continue;
            if (f.isPlatformBanned()) continue;
            if (customer.isInBlacklist(f.getFreelancerID())) continue;

            // Computes composite score
            int score = calculateCompositeScore(f, serviceType);
            chosen.add(f);
            chosenScores.add(score);
        }

        // Pushes all extracted freelancers
        for (Freelancer f : polled) {
            int score = calculateCompositeScore(f, serviceType);
            f.setCompositeScore(score);
            pq.add(f);
        }

        // NO available
        if (chosen.isEmpty()) {
            return "no freelancers available";
        }

        int bestCount = chosen.size();

        // Builds output
        StringBuilder sb = new StringBuilder();
        sb.append("available freelancers for ")
                .append(serviceType)
                .append(" (top ").append(bestCount).append("):\n");

        for (int i = 0; i < bestCount; i++) {
            Freelancer f = chosen.get(i);
            int score = chosenScores.get(i);
            String ratingStr = String.format(Locale.US, "%.1f", f.getAverageRating());

            sb.append(f.getFreelancerID())
                    .append(" - composite: ").append(score)
                    .append(", price: ").append(f.getServicePrice())
                    .append(", rating: ").append(ratingStr);

            if (i < bestCount - 1) {
                sb.append("\n");
            }
        }

        // Auto-employs the best one
        Freelancer best = chosen.get(0);

        if (!best.employ(customerID)) {
            return "Some error occurred in request_job.";
        }

        // Update customer and employment
        customer.startEmployment(best.getFreelancerID());
        Employment employment = new Employment(customer, best);
        employments.add(employment);

        String key = makeEmploymentKey(customerID, best.getFreelancerID());
        activeEmployments.put(key, employment);

        sb.append("\nauto-employed best freelancer: ")
                .append(best.getFreelancerID())
                .append(" for customer ")
                .append(customerID);

        return sb.toString();
    }

    // 5.1) Customer-Initiated Cancellation
    public String cancelByCustomer(String customerID, String freelancerID) {
        // Gets customer and freelancer
        Customer customer = customers.get(customerID);
        Freelancer freelancer = freelancers.get(freelancerID);

        // Validations
        if (customer == null || freelancer == null) {
            return "Some error occurred in cancel_by_customer.";
        }
        String employerID = freelancer.getEmployerCustomerID();
        if (employerID == null || !employerID.equals(customerID)) {
            return "Some error occurred in cancel_by_customer.";
        }
        String key = makeEmploymentKey(customerID, freelancerID);
        Employment emp = activeEmployments.get(key);
        if (emp == null || !emp.getIsActive()) {
            return "Some error occurred in cancel_by_customer.";
        }

        // Applies employment cancellation
        emp.cancelByCustomer();

        // Updates freelancer position in PQ
        refreshFreelancerInPQ(freelancer);

        // Updates customer cancellation count and marks customer for simulateMonth
        customer.setCustomerCancellationCount(customer.getCustomerCancellationCount() + 1);
        pendingLoyaltyUpdates.put(customerID, customer);

        activeEmployments.remove(key); // Removes from active employments

        return "cancelled by customer: " + customerID + " cancelled " + freelancerID;
    }

    // 5.2) Freelancer-Initiated Cancellation
    public String cancelByFreelancer(String freelancerID) {
        // Gets freelancer
        Freelancer freelancer = freelancers.get(freelancerID);
        // Validations
        if (freelancer == null) {
            return "Some error occurred in cancel_by_freelancer.";
        }
        String customerID = freelancer.getEmployerCustomerID();
        if (customerID == null) {
            return "Some error occurred in cancel_by_freelancer.";
        }
        String key = makeEmploymentKey(customerID, freelancerID);
        Employment emp = activeEmployments.get(key);
        if (emp == null || !emp.getIsActive()) {
            return "Some error occurred in cancel_by_freelancer.";
        }

        // Applies employment cancellation
        emp.cancelByFreelancer();

        // Updates freelancer position in PQ
        refreshFreelancerInPQ(freelancer);

        activeEmployments.remove(key); // Removes from active employments

        StringBuilder sb = new StringBuilder();
        sb.append("cancelled by freelancer: ")
                .append(freelancerID)
                .append(" cancelled ")
                .append(customerID);

        // Checks for platform ban
        if (freelancer.getMonthlyCancelledJobs() >= 5 && !freelancer.isPlatformBanned()) {
            freelancer.setPlatformBanned(true);
            sb.append("\nplatform banned freelancer: ").append(freelancerID);
        }

        return sb.toString();
    }

    // 6) Completing and rating a job
    public String completeAndRate(String freelancerID, int rating) {
        // Gets freelancer
        Freelancer freelancer = freelancers.get(freelancerID);

        // Validations
        String customerID = freelancer.getEmployerCustomerID();
        Customer customer = customers.get(customerID);
        if (freelancer == null || customerID == null || rating < 0 || rating > 5 || customer == null) {
            return "Some error occurred in complete_and_rate.";
        }

        // Calculates payment with loyalty discount
        int customerPayment = computeCustomerPayment(customer, freelancer.getServicePrice());
        customer.pay(customerPayment);
        pendingLoyaltyUpdates.put(customerID, customer);
        // Gets service skills
        int[] serviceSkills = getSkillProfile(freelancer.getServiceType());

        // Gets active employment
        String key = makeEmploymentKey(customerID, freelancerID);
        Employment emp = activeEmployments.get(key);

        if (emp == null) { // NO employment
            return "Some error occurred in complete_and_rate.";
        }

        // Completes employment
        emp.complete(rating, serviceSkills);
        activeEmployments.remove(key); // Removes from active employments

        // Updates freelancer in PQ
        refreshFreelancerInPQ(freelancer);

        return freelancerID + " completed job for " + customerID + " with rating " + rating;
    }

    // 7) Changing service type
    public String changeService(String freelancerID, String newServiceType, int newPrice) {
        // Gets freelancer
        Freelancer freelancer = freelancers.get(freelancerID);
        // Validation
        if (freelancer == null || !isValidService(newServiceType) || newPrice <= 0) {
            return "Some error occurred in change_service.";
        }

        String oldService = freelancer.getServiceType(); // Stores old service
        freelancer.queueServiceChange(newServiceType, newPrice); // Queue service change

        return "service change for " + freelancerID
                + " queued from " + oldService
                + " to " + newServiceType;
    }

    // 8.1) Query freelancer
    public String queryFreelancer(String freelancerID) {
        // Gets freelancer
        Freelancer freelancer = freelancers.get(freelancerID);
        // Validation
        if (freelancer == null) {
            return "Some error occurred in query_freelancer.";
        }

        return freelancer.getInfo(); // Calls another method for return
    }

    // 8.2) Query customer
    public String queryCustomer(String customerID) {
        //  Gets customer
        Customer customer = customers.get(customerID);
        // Validation
        if (customer == null) {
            return "Some error occurred in query_customer.";
        }

        return customerID
                + ": total spent: $" + customer.getTotalSpent()
                + ", loyalty tier: " + customer.getLoyaltyTier()
                + ", blacklisted freelancer count: " + customer.getBlacklist().size()
                + ", total employment count: " + customer.getTotalEmploymentCount();
    }

    // 9.1) Blacklist a freelancer
    public String blacklist(String customerID, String freelancerID) {
        // Gets customer and freelancer
        Customer customer = customers.get(customerID);
        Freelancer freelancer = freelancers.get(freelancerID);
        // Validation
        if (customer == null || freelancer == null || customer.isInBlacklist(freelancerID)) {
            return "Some error occurred in blacklist.";
        }

        customer.addToBlacklist(freelancerID); // Adds customer's blacklist
        return customerID + " blacklisted " + freelancerID;
    }

    // 9.2) Unblacklist a freelancer
    public String unblacklist(String customerID, String freelancerID) {
        // Get customer and freelancer
        Customer customer = customers.get(customerID);
        Freelancer freelancer = freelancers.get(freelancerID);
        // Validation
        if (customer == null || freelancer == null || !customer.isInBlacklist(freelancerID)) {
            return "Some error occurred in unblacklist.";
        }

        customer.removeFromBlacklist(freelancerID); // Removes freelancer from blacklist
        return customerID + " unblacklisted " + freelancerID;
    }

    // 10) Manuel freelancer skill updates
    public String updateSkill(String freelancerID, int T, int C, int R, int E, int A) {
        // Gets freelancer
        Freelancer freelancer = freelancers.get(freelancerID);
        // Validations
        if (freelancer == null) {
            return "Some error occurred in update_skill.";
        }
        if (!validSkill(T, C, R, E, A)) {
            return "Some error occurred in update_skill.";
        }

        // Updates every skill
        freelancer.setT(T);
        freelancer.setC(C);
        freelancer.setR(R);
        freelancer.setE(E);
        freelancer.setA(A);

        // Updates freelancer in PQ
        refreshFreelancerInPQ(freelancer);

        return "updated skills of " + freelancerID + " for " + freelancer.getServiceType();
    }

    // 8) Monthly simulation
    public String simulateMonth() {

        // Updates all freelancers
        Object[] allFreelancers = freelancers.values();
        for (Object obj : allFreelancers) {
            if (obj == null) continue;
            Freelancer f = (Freelancer) obj;

            String oldService = f.getServiceType(); // Stores old service type

            f.updateMonthlyStatus(); // Applies monthly updates

            // Updates depending on service change
            if (!oldService.equals(f.getServiceType())) {
                moveFreelancerBetweenServices(f, oldService);
            } else {
                refreshFreelancerInPQ(f);
            }
        }

        // Updates loyalty tiers
        Object[] pending = pendingLoyaltyUpdates.values();
        for (Object obj : pending) {
            if (obj == null) continue;
            Customer c = (Customer) obj;
            c.updateLoyaltyTier();
        }
        pendingLoyaltyUpdates.clear(); // Clears list

        return "month complete";
    }

    // ---HELPER METHODS---
    // PQ helpers
    private void addFreelancerToPQ(Freelancer f) {
        int index = getServiceIndex(f.getServiceType());
        if (index == -1) return;

        // Calculates composite score depending on service type
        int score = calculateCompositeScore(f, f.getServiceType());
        f.setCompositeScore(score);

        freelancerPQByService[index].add(f);
    }
    private void refreshFreelancerInPQ(Freelancer f) {
        int index = getServiceIndex(f.getServiceType());
        if (index == -1) return;

        MyPriorityQueue<Freelancer> pq = freelancerPQByService[index];

        pq.remove(f); // Firstly, removes

        // Calculates new composite score
        int score = calculateCompositeScore(f, f.getServiceType());
        f.setCompositeScore(score);

        pq.add(f); // Finally, adds
    }
    private void moveFreelancerBetweenServices(Freelancer f, String oldServiceType) {
        // Removes from old service PQ
        int oldindex = getServiceIndex(oldServiceType);
        if (oldindex != -1) {
            freelancerPQByService[oldindex].remove(f);
        }

        // Adds new service PQ
        addFreelancerToPQ(f);
    }

    // Builds a unique key for employment
    private String makeEmploymentKey(String customerID, String freelancerID) {
        return customerID + "#" + freelancerID;
    }

    // Validates skills
    private boolean validSkill(int T, int C, int R, int E, int A) {
        return (0<=T && T<=100) && (0<=C && C<=100) && (0<=R && R<=100) &&
                (0<=E && E<=100) && (0<=A && A<=100);
    }


    // Checks service type is valid
    private boolean isValidService(String serviceType) {
        if (serviceType == null) return false;

        for (String service : SERVICE_TYPES) {
            if (service.equals(serviceType)) {
                return true;
            }
        }
        return false;
    }

    // Gets the index of service type
    private int getServiceIndex(String serviceType) {
        if (serviceType == null) return -1;

        for (int i = 0; i < SERVICE_TYPES.length; i++) {
            if (SERVICE_TYPES[i].equals(serviceType)) {
                return i;
            }
        }
        return -1;
    }

    // Gets the skill profile
    private int[] getSkillProfile(String serviceType) {
        int index = getServiceIndex(serviceType);
        if (index == -1) return null;

        // Returns copy to prevent modification
        return SKILL_PROFILES[index].clone();
    }

    // Calculates skill score for composite ranking
    private double calculateSkillScore(int[] freelancerSkills, String serviceType) {
        int[] serviceSkills = getSkillProfile(serviceType);
        if (serviceSkills == null || freelancerSkills == null || freelancerSkills.length != 5) {
            return 0.0;
        }

        // Dot product: F·S
        int dotProduct = 0;
        for (int i = 0; i < 5; i++) {
            dotProduct += freelancerSkills[i] * serviceSkills[i];
        }

        // Sum of service requirements
        int serviceSum = 0;
        for (int s : serviceSkills) {
            serviceSum += s;
        }

        // Formula: (F·S) / (100 * sum)
        if (serviceSum == 0) return 0.0;

        return (double) dotProduct / (100.0 * serviceSum);
    }

    // Calculates composite score
    private int calculateCompositeScore(Freelancer f, String serviceType) {
        // Weights
        double ws = 0.55;
        double wr = 0.25;
        double wl = 0.20;

        // Skill score
        int[] freelancerSkills = {f.getT(), f.getC(), f.getR(), f.getE(), f.getA()};
        double skillScore = calculateSkillScore(freelancerSkills, serviceType);

        // Rating score
        double ratingScore = f.getAverageRating() / 5.0;

        // Reliability score
        int total = f.getCompletedJobs() + f.getCancelledJobs();
        double reliabilityScore;
        if (total == 0) {
            reliabilityScore = 1.0;
        } else {
            reliabilityScore = 1.0 - ((double) f.getCancelledJobs() / total);
        }

        // Burnout penalty
        double burnoutPenalty;
        if (f.isBurnout()) {
            burnoutPenalty = 0.45;
        } else {
            burnoutPenalty = 0.0;
        }

        // Composite score
        double composite = ws * skillScore + wr * ratingScore + wl * reliabilityScore - burnoutPenalty;

        return (int) Math.floor(10000 * composite);
    }
    
    // Calculates customer payment with loyalty discount
    private int computeCustomerPayment(Customer customer, int price) {

        double discount;
        switch (customer.getLoyaltyTier()) {
            case "SILVER":
                discount = 0.05;
                break;
            case "GOLD":
                discount = 0.10;
                break;
            case "PLATINUM":
                discount = 0.15;
                break;
            default:
                discount = 0.0;
        }

        return (int) Math.floor(price * (1.0 - discount));
    }

}
