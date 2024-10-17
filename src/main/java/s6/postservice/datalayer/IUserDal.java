package s6.postservice.datalayer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import s6.postservice.datalayer.entities.User;

@Repository
public interface IUserDal extends JpaRepository<User, Integer> {

}
