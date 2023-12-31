package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.CourseTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json",consumes = "application/json")
    public CourseTO createCourse(@RequestBody @Validated CourseTO courseTO){

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



    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value="/{id}",consumes = "application/json")
    public void updateCourse(@PathVariable int id,
                             @RequestBody @Validated CourseTO courseTO){

        try (Connection connection = pool.getConnection()) {
            PreparedStatement pstmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            pstmExist.setInt(1,id);

            if(!pstmExist.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Courses are not Match with ID");
            }
            PreparedStatement stm = connection.prepareStatement("UPDATE course SET name=?, duration_in_months=? WHERE id=?");
            stm.setString(1,courseTO.getName());
            stm.setInt(2,courseTO.getDurationInMonths());
            stm.setInt(3,id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable int id){

        try (Connection connection = pool.getConnection()) {
            PreparedStatement pstmExist = connection.prepareStatement("SELECT * FROM course WHERE id=? ");
            pstmExist.setInt(1,id);
            if(!pstmExist.executeQuery().next()){
                 throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course name is Mismatch");
            }
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM course WHERE id=?");
            pstm.setInt(1,id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @GetMapping( value = "/{id}",produces = "application/json")
    public CourseTO getCourseDetails(@PathVariable int id){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement pstm = connection.prepareStatement("SELECT * from course where id=?");
            pstm.setInt(1,id);
            ResultSet rst = pstm.executeQuery();
            if((rst.next())){
                int cid=rst.getInt("id");
                String name=rst.getString("name");
                int duration=rst.getInt("duration_in_months");
                return new CourseTO(cid,name,duration);
            }else {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT,"Course can not Find");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @GetMapping
    public LinkedList<CourseTO> getAllDetails(){

        LinkedList<CourseTO> courseList = new LinkedList<>();

        try (Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM course WHERE id");

            while (rst.next())
            {
                courseList.add(new CourseTO(rst.getInt("id"),rst.getString("name"),rst.getInt("duration_in_months")));
            }
            return courseList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
