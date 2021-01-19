package jpastudy.datajpa.repo;

import jpastudy.datajpa.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepo extends JpaRepository<Team, Long> {

}
