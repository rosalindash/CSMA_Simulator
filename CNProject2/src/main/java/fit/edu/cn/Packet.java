package fit.edu.cn;

public class Packet {
	
	private String src;
	private String dst;
	public static int sequenceNumber = 0;
	private int timeSlot;

	public Packet(String src, String dst, int timeSlot) {
		this.src = src;
		this.dst = dst;
		this.timeSlot = timeSlot;
		Packet.sequenceNumber++;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public static int getSequenceNumber() {
		return sequenceNumber;
	}

	public static void setSequenceNumber(int sequenceNumber) {
		Packet.sequenceNumber = sequenceNumber;
	}

	public int getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(int timeSlot) {
		this.timeSlot = timeSlot;
	}

}
