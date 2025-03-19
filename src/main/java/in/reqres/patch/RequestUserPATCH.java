package in.reqres.patch;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUserPATCH {
    private String name;
    private String job;
}
