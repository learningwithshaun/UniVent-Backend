package za.ac.cput.dtos;

/**
 * DTO for platform statistics
 */
public class PlatformStatsDTO {
    private long totalEventCount;
    private long totalUserCount;
    private long totalBookingCount;

    public PlatformStatsDTO(long totalEventCount, long totalUserCount, long totalBookingCount) {
        this.totalEventCount = totalEventCount;
        this.totalUserCount = totalUserCount;
        this.totalBookingCount = totalBookingCount;
    }

    public long getTotalEventCount() {
        return totalEventCount;
    }

    public void setTotalEventCount(long totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public long getTotalUserCount() {
        return totalUserCount;
    }

    public void setTotalUserCount(long totalUserCount) {
        this.totalUserCount = totalUserCount;
    }

    public long getTotalBookingCount() {
        return totalBookingCount;
    }

    public void setTotalBookingCount(long totalBookingCount) {
        this.totalBookingCount = totalBookingCount;
    }

    @Override
    public String toString() {
        return "PlatformStatsDTO{" +
                "totalEventCount=" + totalEventCount +
                ", totalUserCount=" + totalUserCount +
                ", totalBookingCount=" + totalBookingCount +
                '}';
    }
}
