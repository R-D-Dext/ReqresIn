package in.reqres.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseGet {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    //@JsonProperty("data")
    private UsersDatum data;
    private Support support;

}
