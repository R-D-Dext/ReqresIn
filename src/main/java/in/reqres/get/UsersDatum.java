package in.reqres.get;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDatum {
    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;
}