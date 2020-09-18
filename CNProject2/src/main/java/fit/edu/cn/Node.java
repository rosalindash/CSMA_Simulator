package fit.edu.cn;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class Node {
	
	private String nodeIpAddress;
	private int bufferSize;
	private Queue<Packet> packetsBuffer = new LinkedList<>();
	private int collisionCount = 0;
	private int lostPacketCount = 0;
	private int numberOfPacketsGenerated = 0;
	private int successPacketCount = 0;
	private boolean packetCollisionStatus = false;
	private int frameTransmissionDelay = 0;
	private int consecutiveCollisionsCount = 0;
	private int nodeBackOffTime = 0;

	public Node(String nodeIpAddress, int bufferSize) {
		this.nodeIpAddress = nodeIpAddress;
		this.bufferSize = bufferSize;
	}

	public int getNodeBackOffTime() {
		return nodeBackOffTime;
	}

	public int getSuccessPacketCount() {
		return successPacketCount;
	}

	public int getBuffersize() {
		return bufferSize;
	}

	public int getLostPacketCount() {
		return lostPacketCount;
	}

	public boolean isPacketCollisionStatus() {
		return packetCollisionStatus;
	}


	public int getConsecutiveCollisionsCount() {
		return consecutiveCollisionsCount;
	}

	public Queue<Packet> getPacketsBuffer() {
		return packetsBuffer;
	}

	public String getNodeIpAddress() {
		return nodeIpAddress;
	}

	public int getNumberOfPacketsGenerated() {
		return numberOfPacketsGenerated;
	}

	public int getFrameTransmissionDelay() {
		return frameTransmissionDelay;
	}

	public void setNodeBackOffTime(int nodeBackOffTime) {
		this.nodeBackOffTime = nodeBackOffTime;
	}

	// code for generating packet with probability
	public void generatePacket(int packetGenerationtimeSlot) {
		if (new Random().nextDouble() <= Utility.packetGenerationProbability) {
			this.numberOfPacketsGenerated += 1;
			String destinationIP = "10.0.0.";
			destinationIP = destinationIP + String.valueOf(getRandomIntegerBetweenRange(1, Utility.numberOfNode));
			Packet newPacket = new Packet(nodeIpAddress, destinationIP, packetGenerationtimeSlot);
			// checking if the bufferSize is not full then add packet to buffer
			if (packetsBuffer.size() > Utility.bufferSize) {
				this.lostPacketCount++;
			} else {
				packetsBuffer.add(newPacket);
			}
		}
	}

	// generating random variable with upper and lowe bound range
	private int getRandomIntegerBetweenRange(int min, int max) {
		int x = (int) (Math.random() * ((max - min) + 1)) + min;
		return x;
	}

	public void successFulTransactionStatisticsUpdate(int packetGenerationtimeSlot) {

		successPacketCount++;

		// update statistics
		if (packetCollisionStatus) {
			this.packetCollisionStatus = false;
		}

		// Remove packet from buffer
		Packet packetTransmitted = this.packetsBuffer.remove();

		// Delay Calculation after successful transmission
		this.frameTransmissionDelay = packetGenerationtimeSlot - packetTransmitted.getTimeSlot();

		// update consecutive collison variable after the packet is successfully
		// transmitted
		consecutiveCollisionsCount = 0;

	}
	public void collisionStatisticsUpdate() {
		
		this.collisionCount++; // increase the collisionCount once encountered collision

		this.packetCollisionStatus = true;
		
		// setting the backOff Time for node as node doesn't participate in transmission
		this.nodeBackOffTime = calculateBackOffTime(collisionCount);

		if (consecutiveCollisionsCount > 0) {
			this.consecutiveCollisionsCount++;
		}
		if (consecutiveCollisionsCount == ChanelState.PACKET_RETRANSMISSION_COUNT_FOR_PACKET_DROP) {
			this.packetsBuffer.remove();
			this.consecutiveCollisionsCount = 0;
			this.lostPacketCount++;
		}
	}

	private int calculateBackOffTime(int collisionCount) {

		int exponentialBackOff = Math.min(ChanelState.BACKOFF_MINIMUM_VALUE, collisionCount);
		int finalBackOffValue = (int) Math.pow(2, exponentialBackOff) - 1;
		int backOffTime = getRandomIntegerBetweenRange(0, finalBackOffValue);

		return backOffTime;

	}

}
