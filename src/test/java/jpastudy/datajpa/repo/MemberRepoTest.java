package jpastudy.datajpa.repo;

import jpastudy.datajpa.domain.Member;
import jpastudy.datajpa.domain.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepoTest {

    @Autowired
    MemberRepo memberRepo;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Spring Data JPA 설정 후 테스트")
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepo.save(member);
        Member findMember = memberRepo.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("엔티티 설정후 테스트")
    @Transactional
    @Rollback(false)
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        //초기화
        em.flush();
        em.clear();
//확인
        List<Member> members = em.createQuery("select m from Member m",
            Member.class)
            .getResultList();
        for (Member member : members) {
            System.out.println("member=" + member);
            System.out.println("-> member.team=" + member.getTeam());
        }
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepo.save(member1);
        memberRepo.save(member2);
//단건 조회 검증
        Member findMember1 = memberRepo.findById(member1.getId()).get();
        Member findMember2 = memberRepo.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
//리스트 조회 검증
        List<Member> all = memberRepo.findAll();
        assertThat(all.size()).isEqualTo(2);
//카운트 검증
        long count = memberRepo.count();
        assertThat(count).isEqualTo(2);
//삭제 검증
        memberRepo.delete(member1);
        memberRepo.delete(member2);
        long deletedCount = memberRepo.count();
        assertThat(deletedCount).isEqualTo(0);
    }
}