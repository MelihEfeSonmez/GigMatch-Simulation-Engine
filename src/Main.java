import java.io.*;
import java.util.Locale;

/**
 * Main entry point for GigMatch Pro platform.
 */
public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        PlatformManager platform = new PlatformManager(); // Initialized for thia class

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                processCommand(line, writer, platform);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer, PlatformManager platform)
            throws IOException {

        String[] parts = command.split("\\s+");
        String operation = parts[0];

        try {
            String result = "";

            switch (operation) {
                case "register_customer":
                    // Format: register_customer customerID
                    if (parts.length == 2) {
                        String customerID = parts[1];
                        result = platform.registerCustomer(customerID);
                    } else {
                        result = "Some error occurred in register_customer.";
                    }
                    break;

                case "register_freelancer":
                    // Format: register_freelancer freelancerID serviceName basePrice T C R E A
                    if (parts.length == 9) {
                        String freelancerID = parts[1];
                        String serviceType = parts[2];
                        int servicePrice = Integer.parseInt(parts[3]);
                        int T = Integer.parseInt(parts[4]);
                        int C = Integer.parseInt(parts[5]);
                        int R = Integer.parseInt(parts[6]);
                        int E = Integer.parseInt(parts[7]);
                        int A = Integer.parseInt(parts[8]);
                        result = platform.registerFreelancer(freelancerID, serviceType, servicePrice, T, C, R, E, A);
                    } else {
                        result = "Some error occurred in register_freelancer.";
                    }
                    break;

                case "request_job":
                    // Format: request_job customerID serviceName topK
                    if (parts.length == 4) {
                        String customerID = parts[1];
                        String serviceName = parts[2];
                        int topK = Integer.parseInt(parts[3]);
                        result = platform.requestJob(customerID, serviceName, topK);
                    } else {
                        result = "Some error occurred in request_job.";
                    }
                    break;

                case "employ_freelancer":
                    // Format: employ_freelancer customerID freelancerID
                    if (parts.length == 3) {
                        String customerID = parts[1];
                        String freelancerID = parts[2];
                        result = platform.employ(customerID, freelancerID);
                    } else {
                        result = "Some error occurred in employ.";
                    }
                    break;

                case "complete_and_rate":
                    // Format: complete_and_rate freelancerID rating
                    if (parts.length == 3) {
                        String freelancerID = parts[1];
                        int rating = Integer.parseInt(parts[2]);
                        result = platform.completeAndRate(freelancerID, rating);
                    } else {
                        result = "Some error occurred in complete_and_rate.";
                    }
                    break;

                case "cancel_by_freelancer":
                    // Format: cancel_by_freelancer freelancerID
                    if (parts.length == 2) {
                        String freelancerID = parts[1];
                        result = platform.cancelByFreelancer(freelancerID);
                    } else {
                        result = "Some error occurred in cancel_by_freelancer.";
                    }
                    break;

                case "cancel_by_customer":
                    // Format: cancel_by_customer customerID freelancerID
                    if (parts.length == 3) {
                        String customerID = parts[1];
                        String freelancerID = parts[2];
                        result = platform.cancelByCustomer(customerID, freelancerID);
                    } else {
                        result = "Some error occurred in cancel_by_customer.";
                    }
                    break;

                case "blacklist":
                    // Format: blacklist customerID freelancerID
                    if (parts.length == 3) {
                        String customerID = parts[1];
                        String freelancerID = parts[2];
                        result = platform.blacklist(customerID, freelancerID);
                    } else {
                        result = "Some error occurred in blacklist.";
                    }
                    break;

                case "unblacklist":
                    // Format: unblacklist customerID freelancerID
                    if (parts.length == 3) {
                        String customerID = parts[1];
                        String freelancerID = parts[2];
                        result = platform.unblacklist(customerID, freelancerID);
                    } else {
                        result = "Some error occurred in unblacklist.";
                    }
                    break;

                case "change_service":
                    // Format: change_service freelancerID newService newPrice
                    if (parts.length == 4) {
                        String freelancerID = parts[1];
                        String newService = parts[2];
                        int newPrice = Integer.parseInt(parts[3]);
                        result = platform.changeService(freelancerID, newService, newPrice);
                    } else {
                        result = "Some error occurred in change_service.";
                    }
                    break;

                case "simulate_month":
                    // Format: simulate_month
                    result = platform.simulateMonth();
                    break;

                case "query_freelancer":
                    // Format: query_freelancer freelancerID
                    if (parts.length == 2) {
                        String freelancerID = parts[1];
                        result = platform.queryFreelancer(freelancerID);
                    } else {
                        result = "Some error occurred in query_freelancer.";
                    }
                    break;

                case "query_customer":
                    if (parts.length == 2) {
                        String customerID = parts[1];
                        result = platform.queryCustomer(customerID);
                    } else {
                        result = "Some error occurred in query_customer.";
                    }
                    break;

                case "update_skill":
                    // Format: update_skill freelancerID T C R E A
                    if (parts.length == 7) {
                        String freelancerID = parts[1];
                        int T = Integer.parseInt(parts[2]);
                        int C = Integer.parseInt(parts[3]);
                        int R = Integer.parseInt(parts[4]);
                        int E = Integer.parseInt(parts[5]);
                        int A = Integer.parseInt(parts[6]);
                        result = platform.updateSkill(freelancerID, T, C, R, E, A);
                    } else {
                        result = "Some error occurred in update_skill.";
                    }
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

            writer.write(result);
            writer.newLine();

        } catch (Exception e) {
            writer.write("Error processing command: " + command);
            writer.newLine();
        }
    }
}