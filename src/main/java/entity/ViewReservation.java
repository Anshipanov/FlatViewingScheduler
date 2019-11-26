package entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewReservation
{
    private Integer id;
    private int flatId;
    private int tenantId;
    private LocalDateTime startTime;
    private boolean approved = false;
    private boolean rejected = false;
    private boolean canceled = false;

    public ViewReservation(int flatId, int tenantId, LocalDateTime startTime)
    {
        this.flatId = flatId;
        this.tenantId = tenantId;
        this.startTime = startTime;
    }

}
