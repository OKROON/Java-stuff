import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {
        // Example networks with the number of devices
        int[] devices = {99, 71, 22, 10, 4, 4}; // Assuming last two are R-R connections

        // Calculate total addresses needed for each network
        int[] totalAddresses = calculateTotalAddresses(devices);

        // Sort the networks by size, descending
        Integer[] sortedAddresses = sortAddressesDescending(totalAddresses);

        // Calculate the cumulative sums for the VLSM pie
        int[] cumulativeSums = calculateCumulativeSums(sortedAddresses);

        // Print detailed calculations and assign network requirements
        printDetailedCalculations(devices, sortedAddresses, cumulativeSums);
        assignNetworkRequirements(sortedAddresses, cumulativeSums, devices);
    }

    private static int[] calculateTotalAddresses(int[] devices) {
        int[] totalAddresses = new int[devices.length];
        System.out.println("Calculating total addresses needed for each network, including special addresses and devices:");
        for (int i = 0; i < devices.length; i++) {
            // Adjusting calculation to account for Network Address, Broadcast Address, Router, and Switch
            totalAddresses[i] = devices[i] + 4; // Assuming +4 for all networks for simplicity
            // Find the closest higher power of 2
            int powerOfTwo = 2;
            while (powerOfTwo < totalAddresses[i]) {
                powerOfTwo *= 2;
            }
            totalAddresses[i] = powerOfTwo;
            System.out.printf("Network %d: Devices = %d, Total Addresses Needed = %d\n", i + 1, devices[i], totalAddresses[i]);
        }
        return totalAddresses;
    }

    private static Integer[] sortAddressesDescending(int[] totalAddresses) {
        Integer[] sortedAddresses = Arrays.stream(totalAddresses).boxed().toArray(Integer[]::new);
        Arrays.sort(sortedAddresses, Collections.reverseOrder());
        return sortedAddresses;
    }

    private static int[] calculateCumulativeSums(Integer[] sortedAddresses) {
        int[] cumulativeSums = new int[sortedAddresses.length];
        int sum = 0;
        System.out.println("\nVLSM Pie Calculation:");
        for (int i = 0; i < sortedAddresses.length; i++) {
            sum += sortedAddresses[i];
            cumulativeSums[i] = sum;
            System.out.printf("0 + %d = %d\n", sortedAddresses[i], sum);
        }
        return cumulativeSums;
    }

    private static void printDetailedCalculations(int[] devices, Integer[] sortedAddresses, int[] cumulativeSums) {
        System.out.println("\nDetailed Calculations for Each Network:");
        for (int i = 0; i < sortedAddresses.length; i++) {
            System.out.printf("Network %d: Original Devices = %d, Adjusted for VLSM = %d, Cumulative Sum = %d\n", i + 1, devices[i], sortedAddresses[i], cumulativeSums[i]);
        }
    }

    private static void assignNetworkRequirements(Integer[] sortedAddresses, int[] cumulativeSums, int[] devices) {
        System.out.println("\nNetwork Requirements:");
        for (int i = 0; i < sortedAddresses.length; i++) {
            int AS = (i == 0) ? 0 : cumulativeSums[i-1];
            int BR = cumulativeSums[i] - 1;
            int maskBits = 32 - Integer.numberOfLeadingZeros(sortedAddresses[i] - 1);
            String mask = calculateSubnetMask(maskBits);
            int GW = AS + 1;
            System.out.printf("Network %d (Devices = %d): AS=%d, BR=%d, Mask=%s, GW=%d\n", i + 1, devices[i], AS, BR, mask, GW);
        }
    }

    private static String calculateSubnetMask(int maskBits) {
        int mask = -1 << (32 - maskBits);
        return String.format("%d.%d.%d.%d", mask >>> 24 & 0xFF, mask >>> 16 & 0xFF, mask >>> 8 & 0xFF, mask & 0xFF);
    }
}
