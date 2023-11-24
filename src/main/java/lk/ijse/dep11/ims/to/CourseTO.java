package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseTO implements Serializable {

    @Null(message = "Id Should not Enter")
    private Integer id;
    @NotBlank(message = "Name Should Not be Null")
    private String name;
    @NotNull(message = "Duration can not be Empty")
    private Integer durationInMonths;
}
