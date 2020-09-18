package fit.edu.cn;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * @author rosalindash
 * 
 *         througput: average number of successful frame transmission per time
 *         slot totalNumberofPackets generated in time slot
 * 
 */
public class MainOnePersistant {

	private static int timeSlotWasted = 0;
	private static int timeSlotUsed = 0;
	private static int timeSlotBusy = 0;

	public static void main(String[] args) {

		// fetching user input
		Utility utility = new Utility();
		System.out.println("Enter the number of Nodes");
		Scanner im = new Scanner(System.in);
		utility.setNumberOfNode(im.nextInt());
		System.out.println("Enter the buffer size for each Nodes");
		utility.setBufferSize(im.nextInt());
		System.out.println("Enter the number of TimeSlot");
		utility.setTimeSlot(im.nextInt());
		System.out.println("Enter the probability value for generating Packet");
		utility.setPacketGenerationProbability((int) (im.nextFloat() * 100));
		im.close();

		// defining the Nodes
		ArrayList<Node> nodes = new ArrayList<>(Utility.numberOfNode);
		for (int i = 0; i < Utility.numberOfNode; ++i) {
			nodes.add(new Node("10.0.0." + Integer.toString(i + 1), Utility.bufferSize));
		}

		String channelState = ChanelState.IDLE;
		for (int i = 0; i < Utility.timeSlot; i++) {

			// generating packet with probability and decrementing backOfftime for each node
			for (Node node : nodes) {
				node.generatePacket(i);
				if (node.getNodeBackOffTime() > 0) {
					node.setNodeBackOffTime(node.getNodeBackOffTime() - 1);
				}

			}

			if (channelState.equalsIgnoreCase(ChanelState.TRANSMISSION) && timeSlotBusy != 0) {
				timeSlotBusy--; // to count the number of timeSlot busy
				timeSlotUsed++; // to count the number of timeSlot Used
				channelState = ChanelState.TRANSMISSION;
			} else {

				// getting the list of node whose buffer is not empty has packet to transmit
				ArrayList<Node> nodesHavingPendingPackets = nodesHavingPendingPackets(nodes);
				if (nodesHavingPendingPackets.size() == 0) {
					channelState = ChanelState.IDLE;
					timeSlotWasted++;
				} else if (nodesHavingPendingPackets.size() == 1) {
					channelState = ChanelState.TRANSMISSION;
					timeSlotUsed++;
					// give a random node the priority to transmit
					int rando = (int) ((Math.random() * nodesHavingPendingPackets.size()));
					Node transmittingNode = nodesHavingPendingPackets.remove(rando);
					transmittingNode.successFulTransactionStatisticsUpdate(i);
					timeSlotBusy = ChanelState.KEEP_CHANNEL_BUSY;

				} else if (nodesHavingPendingPackets.size() > 1) {
					channelState = ChanelState.CONTENTION;
					timeSlotWasted += 1;
					for (Node node : nodesHavingPendingPackets) {
						node.collisionStatisticsUpdate();
					}
				}

			}
		}

		Map<String, String> getVariables = populateVariables(nodes);
		
		displayresult(getVariables);

	}

	private static void displayresult(Map<String, String> getVariables) {
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.printf("|%-46s|\n", "                  PER NODE DETAIL INFORMATIONS                    ");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		for (Map.Entry<String, String> entry : getVariables.entrySet()) {
			if (entry.getKey().contains("10.0.0")) {
				System.out.printf("|%-55s | %-7s |\n", entry.getKey(), entry.getValue());
				System.out.println("+------------------------------------------------------------------+");
			}
		}

		System.out.println("\n");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.printf("|%-46s|\n", "                  OVERALL SIMULATION RESULTS                      ");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		for (Map.Entry<String, String> entry : getVariables.entrySet()) {
			if (!entry.getKey().contains("10.0.0")) {
				System.out.printf("|%-55s | %-7s |\n", entry.getKey(), entry.getValue());
				System.out.println("+------------------------------------------------------------------+");
			}
		}

	}

	private static Map<String, String> populateVariables(ArrayList<Node> nodes) {

		Map<String, String> displayVariable = new HashMap<>();

		int totalFrames = Packet.sequenceNumber;
		int totalFramesLost = 0;
		int frameSuccessfulTransmissionCount = 0;
		int awaitingFrame = 0;
		double averageFrames = 0.0;
		int totalDelay = 0;
		for (Node node : nodes) {
			displayVariable.put("Number of frame generated by " + node.getNodeIpAddress(),
					String.valueOf(node.getNumberOfPacketsGenerated()));
			double averageofPacketPerNode = 0.0;
			totalFramesLost += node.getLostPacketCount();
			frameSuccessfulTransmissionCount += node.getSuccessPacketCount();
			awaitingFrame += node.getPacketsBuffer().size();
			totalDelay += node.getFrameTransmissionDelay();
			averageofPacketPerNode = (double) node.getNumberOfPacketsGenerated() / Utility.timeSlot;
			averageFrames = (double) totalFrames / Utility.timeSlot;
			displayVariable.put("Average Number of frame generated by " + node.getNodeIpAddress(),
					String.valueOf(averageofPacketPerNode));

		}
		displayVariable.put("Awaiting Frames", String.valueOf(awaitingFrame));
		displayVariable.put("Average of Total Frames generated", String.valueOf(averageFrames));
		displayVariable.put("Total Number of Frames", String.valueOf(totalFrames));
		displayVariable.put("Total Number of Frames Lost", String.valueOf(totalFramesLost));
		displayVariable.put("Successful Transmission Frame Count", String.valueOf(frameSuccessfulTransmissionCount));

		double throughPut = (double) frameSuccessfulTransmissionCount / Utility.timeSlot;
		double channelUtilty = (double) timeSlotUsed / Utility.timeSlot;
		double channelWaste = (double) timeSlotWasted / Utility.timeSlot;
		double averageDelay = (double) totalDelay / Utility.timeSlot;

		displayVariable.put("Throughput", String.valueOf(throughPut));
		DecimalFormat df = new DecimalFormat("###.##");
		displayVariable.put("Channel Utilization", String.valueOf(df.format(channelUtilty * 100)) + "%");
		displayVariable.put("Channel Waste", String.valueOf(channelWaste));
		displayVariable.put("Average Delay", String.valueOf(averageDelay));

		return displayVariable;

	}

	/**
	 * This method takes array of nodes as input, checks if any nodes have pending
	 * packets, returns an arraylist of nodes which have pending packets.
	 * 
	 * @param array of nodes
	 * @param max
	 * @return arraylist of nodes
	 */
	private static ArrayList<Node> nodesHavingPendingPackets(ArrayList<Node> allNodes) {
		ArrayList<Node> nodeReadyToTransmit = new ArrayList<>();
		for (Node node : allNodes) {
			if (!node.getPacketsBuffer().isEmpty() && node.getNodeBackOffTime() == 0) {
				nodeReadyToTransmit.add(node);
			}
		}
		return nodeReadyToTransmit;
	}

	public static int getRandomIntegerBetweenRange(int min, int max) {
		int x = (int) (Math.random() * ((max - min) + 1)) + min;
		return x;
	}

}
