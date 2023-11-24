package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.CourseTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.sql.*;

@CrossOrigin
@RestController
@RequestMapping("/courses")
public class CourseHttpController {
    private final HikariDataSource pool;
    public CourseHttpController(){

        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("mysql");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("maximumPoolSize",10);
        pool=new HikariDataSource(config);


    }
    @PreDestroy
    public void destroy(){
        pool.close();
    }


    @PostMapping(produces = "application/json",consumes = "application/json")
    public CourseTO createCourse(@RequestBody CourseTO courseTO){

        try (Connection connection = pool.getConnection()) {
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO course (name,duration_in_months) VALUES (?,?)",Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1,courseTO.getName());
            pstm.setInt(2,courseTO.getDurationInMonths());
            pstm.executeUpdate();
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            courseTO.setId(id);
            return courseTO;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @PatchMapping("/{id}")
    public void updateCourse(){
        System.out.println("updateCourse()");
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(){
        System.out.println("deleteCourse()");

    }

    @GetMapping("/{id}")
    public void getCourseDetails(){
        System.out.println("getCourseDetails()");
    }

    @GetMapping
    public void getAllDetails(){
        System.out.println("getAllDetails()");
    }


}
