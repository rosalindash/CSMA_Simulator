package fit.edu.cn;

public class ChanelState {
	public static final String IDLE = "IDLE";
	public static final String TRANSMISSION = "TRANSMISSION";
	public static final String CONTENTION = "CONTENSION";
	public static final int PACKET_RETRANSMISSION_COUNT_FOR_PACKET_DROP = 15;
	public static final int BACKOFF_MINIMUM_VALUE = 10;
	public static final int KEEP_CHANNEL_BUSY = 9;

}
