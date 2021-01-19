package jpastudy.datajpa.repo;

import jpastudy.datajpa.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepo extends JpaRepository<Member, Long> {
}