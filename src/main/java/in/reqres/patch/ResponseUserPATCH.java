package in.reqres.patch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserPATCH {
    private String name;
    private String job;
    private String updatedAt;
}
