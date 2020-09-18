package fit.edu.cn;

public class Utility {

	public static int numberOfNode;
	public static int bufferSize;
	public static int timeSlot;
	public static int packetGenerationProbability;
	public static int retransmissionCount;

	public Utility() {
	}

	public void setNumberOfNode(int numberOfNode) {
		this.numberOfNode = numberOfNode;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setTimeSlot(int timeSlot) {
		this.timeSlot = timeSlot;
	}

	public void setPacketGenerationProbability(int packetGenerationProbability) {
		this.packetGenerationProbability = packetGenerationProbability;
	}

	public void setRetransmissionCount(int retransmissionCount) {
		this.retransmissionCount = retransmissionCount;
	}

}
