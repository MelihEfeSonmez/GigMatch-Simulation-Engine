# GigMatch Pro Engine 🚀

**GigMatch Pro** is a high-performance, algorithmic simulation engine for a freelance marketplace. Inspired by the gig economy logic, this system handles real-time user matching, dynamic skill evolution, and monthly simulation cycles for up to **500,000 users**.

Unlike standard CRUD applications, this project implements **custom data structures** (Hash Tables and Priority Queues) from scratch to ensure $O(1)$ access times and efficient ranking algorithms without relying on built-in Java collections.

## 🌟 Key Features

* **Weighted Composite Ranking:** Matches customers with the best freelancers using a multi-factor algorithm (Skill Match, Rating, Reliability).
* **Dynamic Skill Evolution:** Freelancers' skills grow with successful jobs and degrade with cancellations or inactivity.
* **Burnout Mechanism:** Simulates real-world fatigue; freelancers engaging in excessive workload suffer performance penalties.
* **Loyalty System:** Tier-based customer status (Bronze, Silver, Gold, Platinum) affecting pricing and platform subsidies.
* **Automated Testing:** Includes a custom Python script for batched input/output verification.

## 🛠 Technical Architecture

The core of GigMatch lies in its custom-built data structures designed for scalability:

### 1. Custom Data Structures (No Built-in Libraries)
* **`MyHashTable.java`**: A highly optimized hash table implementing separate chaining. It manages **Customer** and **Freelancer** objects, ensuring constant-time lookup even under heavy load (Test cases up to 500k operations).
* **`MyPriorityQueue.java`**: A binary heap implementation used to manage the **Job Matching** process. It allows the engine to instantly retrieve the highest-ranked freelancer based on the composite score.

### 2. The Simulation Engine
* **`PlatformManager.java`**: The central controller that orchestrates user registration, job flow, monthly updates, and system-wide queries.
* **`Employment.java`**: Manages the lifecycle of a job, tracking state changes from "Hired" to "Completed" or "Cancelled".

## 🧮 The Ranking Algorithm

The engine ranks freelancers for specific jobs using a weighted composite score formula:

```math
CompositeScore = ⌊ 10000 × ( (0.55 × SkillScore) + (0.25 × RatingScore) + (0.20 × ReliabilityScore) - BurnoutPenalty ) ⌋
```

* ** SkillScore: Dot product of freelancer skills and service requirements.
* ** Reliability: Impacted by cancellation rates.
* ** Burnout Penalty: Applied if a freelancer exceeds safe workload limits.

## 📂 Project Structure
```bash
GigMatch-Engine/
├── src/
│   ├── Main.java              # Entry point
│   ├── PlatformManager.java   # Simulation controller
│   ├── Freelancer.java        # User model with skill vectors
│   ├── Customer.java          # User model with loyalty logic
│   ├── Employment.java        # Job transaction model
│   ├── MyHashTable.java       # Custom K-V store implementation
│   └── MyPriorityQueue.java   # Custom Heap implementation
├── test_cases/
│   ├── inputs/                # Large scale datasets
│   └── outputs/               # Expected simulation results
├── test_runner.py             # Automated Python testing script
└── README.md
```

## 🧪 Automated Testing (Python Runner)
A custom Python Test Runner (test_runner.py) was developed to validate the engine's accuracy against massive datasets.

* Function: Automatically executes the compiled Java bytecode against input files.
* Validation: Compares the generated output with expected logs line-by-line.
* Performance: Measures execution time to ensure the custom data structures meet the required time complexity constraints.

## 🚀 How to Run
Compile the Project:

```bash
javac src/*.java
Run the Simulation:
```
```bash
java Main input.txt output.txt
Run the Test Suite (Python):
```
```bash
python3 tests/test_runner.py
```
---
*Developed by Melih Efe Sonmez.*
