public class Employment {

    // DATA FIELDS
    private Customer customer;
    private Freelancer freelancer;

    private boolean isActive;

    // CONSTRUCTORS
    // Default Constructor
    public Employment() {
        this.customer = null;
        this.freelancer = null;
        this.isActive = true;
    }

    public Employment(Customer customer, Freelancer freelancer) {
        this.customer = customer;
        this.freelancer = freelancer;
        this.isActive = true;
    }

    // GETTERS
    public Customer getCustomer() {return customer;}
    public Freelancer getFreelancer() {return freelancer;}
    public boolean getIsActive() {return isActive;}

    // ---METHODS---
    // Completion of the employment
    public void complete(int rating, int[] serviceSkills) {
        if (!isActive) return;
        isActive = false;

        // Finishes employment on freelancer side
        freelancer.completeJob(rating, serviceSkills);
        // Finishes employment on customer side
        customer.finishEmployment(freelancer.getFreelancerID());
    }

    // Customer cancellation
    public void cancelByCustomer() {
        if (!isActive) return;
        isActive = false;

        // Finishes employment on freelancer side
        freelancer.free();
        // Finish employment on customer side
        customer.finishEmployment(freelancer.getFreelancerID());
    }

    // Freelancer cancellation
    public void cancelByFreelancer() {
        if (!isActive) return;
        isActive = false;

        // Finishes employment on freelancer side
        freelancer.cancelJob();
        // Finish employment on customer side
        customer.finishEmployment(freelancer.getFreelancerID());
    }

}
