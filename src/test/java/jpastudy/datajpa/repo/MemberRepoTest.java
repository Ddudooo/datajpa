package jpastudy.datajpa.repo;

import jpastudy.datajpa.domain.Member;
import jpastudy.datajpa.domain.Team;
import jpastudy.datajpa.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepoTest {

    @Autowired
    MemberRepo memberRepo;

    @Autowired
    TeamRepo teamRepo;

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

    @Test
    @DisplayName("메소드 이름으로 쿼리 생성")
    public void createQueryByMethodName() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);
        memberRepo.save(member1);
        memberRepo.save(member2);

        List<Member> result = memberRepo.findByUsernameAndAgeGreaterThan("member", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("member");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Named 쿼리 테스트")
    public void namedQueryTest() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);
        memberRepo.save(member1);
        memberRepo.save(member2);

        List<Member> result = memberRepo.findByUsername("member");

        assertThat(result.get(0).getUsername()).isEqualTo("member");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Named 쿼리 테스트")
    public void namedQuery2Test() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);
        memberRepo.save(member1);
        memberRepo.save(member2);

        List<Member> result = memberRepo.findUser("member", 10);

        assertThat(result.get(0).getUsername()).isEqualTo("member");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("조회 쿼리 테스트")
    public void queryTest() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);
        memberRepo.save(member1);
        memberRepo.save(member2);

        List<String> usernameList = memberRepo.findUsernameList();

        assertThat(usernameList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("DTO 조회 쿼리 테스트")
    public void dtoQueryTest() {
        Team team = new Team("teamA");
        teamRepo.save(team);

        Member member1 = new Member("member", 10);
        member1.setTeam(team);
        memberRepo.save(member1);

        List<MemberDto> dtos = memberRepo.findMemberDto();

        assertThat(dtos.get(0).getTeamName()).isEqualTo("teamA");
    }

    @Test
    @DisplayName("파라미터 바인딩 쿼리 테스트")
    public void findMemberByNames() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member2", 20);
        memberRepo.save(member1);
        memberRepo.save(member2);

        List<String> names = new ArrayList<>();
        names.add("member");
        names.add("member2");

        List<Member> findmembers = memberRepo.findByNames(names);

        assertThat(findmembers.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("다양한 반환타입 테스트")
    public void returnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepo.save(member1);
        memberRepo.save(member2);

        List<Member> memberList = memberRepo.findListByUsername("AAA");
        Member findMember = memberRepo.findMemberByUsername("BBB");
        Optional<Member> optionalMember = memberRepo.findOptionalByUsername("AAA");

        assertThat(memberList.size()).isEqualTo(1);
        assertThat(findMember.getId()).isEqualTo(member2.getId());
        assertThat(optionalMember).isPresent();
    }

    @Test
    @DisplayName("스프링 데이터 JPA 페이징과 정렬 테스트")
    public void pageQueryTest() {
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));
        memberRepo.save(new Member("AAA", 10));

        int age = 5;
        int offset = 0;
        int limit = 3;

        Page<Member> memberPage = memberRepo
            .findPageMemberByAgeAfter(age, PageRequest.of(0, 3, Sort.by(
                Direction.DESC, "username")));

        assertThat(memberPage.getSize()).isEqualTo(3);
    }
}