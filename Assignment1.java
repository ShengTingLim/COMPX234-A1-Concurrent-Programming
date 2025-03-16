import java.util.ArrayList;
import java.util.concurrent.Semaphore;

class Assignment1 {

    // Simulation Initialisation
    private static int NUM_MACHINES = 50; // Number of machines in the system that issue print requests
    private static int NUM_PRINTERS = 5; // Number of printers in the system that print requests
    private static int SIMULATION_TIME = 30;
    private static int MAX_PRINTER_SLEEP = 3;
    private static int MAX_MACHINE_SLEEP = 5;
    private static int QUEUE_SIZE = 5;
    private static boolean sim_active = true;

    // Create an empty list of print requests
    printList list = new printList();

    // Semaphores for synchronization

    // The queueMutex semaphore ensures mutual exclusion for accessing the printList
    // Only one thread can access the queue at a time
    private final Semaphore queueMutex = new Semaphore(1);
    
    // The emptySlots semaphore is used to block the machine threads when the queue is full
    // Represents the number of requests currently in the queue
    private final Semaphore emptySlots = new Semaphore(QUEUE_SIZE);

    // The fullSlots semaphore is used to block the printer threads when the queue is empty
    // Represents the number of requests to be printed
    // Starts at 0 to ensure that printers do not try to print from an empty queue
    private final Semaphore fullSlots = new Semaphore(0);

    public void startSimulation() {

        // ArrayList to keep for machine and printer threads
        ArrayList<Thread> mThreads = new ArrayList<Thread>();
        ArrayList<Thread> pThreads = new ArrayList<Thread>();

        // Create Machine and Printer threads
        for (int i = 0; i < NUM_MACHINES; i++) {
            machineThread m = new machineThread(i);
            mThreads.add(m);
        }

        for (int i = 0; i < NUM_PRINTERS; i++) {
            printerThread p = new printerThread(i);
            pThreads.add(p);
        }

        // start all the threads
        for (Thread printer : pThreads) {
            printer.start();
        }

        for (Thread machine : mThreads) {
            machine.start();
        }

        // let the simulation run for some time
        sleep(SIMULATION_TIME);

        // finish simulation
        sim_active = false;

        // Release all semaphores to unblock any waiting threads
        for (int i = 0; i < QUEUE_SIZE; i++) {
            emptySlots.release();
            fullSlots.release();
        }

        // Wait until all printer threads finish by using the join function
        for (Thread printer : pThreads) {
            try {
                printer.join();
            } catch (InterruptedException e) {
                System.err.println("Printer thread interrupted: " + e.getMessage());
            }
        }
    }

    // Printer class
    public class printerThread extends Thread {
        private int printerID;

        public printerThread(int id) {
            printerID = id;
        }

        public void run() {
            while (sim_active) {
                // Simulate printer taking some time to print the document
                printerSleep();
                // Grab the request at the head of the queue and print it
                printDox(printerID);
            }
        }

        public void printerSleep() {
            int sleepSeconds = 1 + (int) (Math.random() * MAX_PRINTER_SLEEP);
            // sleep(sleepSeconds*1000);
            try {
                sleep(sleepSeconds * 1000);
            } catch (InterruptedException ex) {
                System.out.println("Sleep Interrupted");
            }
        }

        public void printDox(int printerID) {
            try {
                // Wait for an available item to print
                fullSlots.acquire(); 
                if (!sim_active) {
                    fullSlots.release();
                    return;
                }
                // Wait for access to the queue
                queueMutex.acquire(); 

                System.out.println("Printer ID:" + printerID + " : now available");
                // print from the queue
                list.queuePrint(list, printerID);

                // Release access to the queue
                queueMutex.release(); 
                // Release semaphore slot to allow other another machine to send request
                emptySlots.release(); 
            } catch (InterruptedException e) {
                System.out.println("PrintDox Interrupted");
            }
        }
    }

    // Machine class
    public class machineThread extends Thread {
        private int machineID;

        public machineThread(int id) {
            machineID = id;
        }

        public void run() {
            while (sim_active) {
                // machine sleeps for a random amount of time
                machineSleep();
                // machine wakes up and sends a print request
                printRequest(machineID);
            }
        }

        // machine sleeps for a random amount of time
        public void machineSleep() {
            int sleepSeconds = 1 + (int) (Math.random() * MAX_MACHINE_SLEEP);

            try {
                sleep(sleepSeconds * 1000);
            } catch (InterruptedException ex) {
                System.out.println("Sleep Interrupted");
            }
        }

        public void printRequest(int id) {
            try {
                // Wait for an available slot
                emptySlots.acquire(); 
                if (!sim_active) {
                    emptySlots.release();
                    return;
                }
                // Wait for access to the queue
                queueMutex.acquire(); 
                
                System.out.println("Machine " + id + " Sent a print request");
                // Build a print document
                printDoc doc = new printDoc("My name is machine " + id, id);
                // Insert it in print queue
                list = list.queueInsert(list, doc);

                // Release access to the queue
                queueMutex.release();
                // Release semaphore slot to signal that a request is available
                fullSlots.release(); 
            } catch (InterruptedException e) {
                System.out.println("PrintRequest Interrupted");
            }
        }
    }

    private static void sleep(int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException ex) {
            System.out.println("Sleep Interrupted");
        }
    }
}
