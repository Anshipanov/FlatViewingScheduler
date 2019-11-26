package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tenant
{
    private Integer id;
    private String name;

    public Tenant(String name)
    {
        this.name = name;
    }
}
