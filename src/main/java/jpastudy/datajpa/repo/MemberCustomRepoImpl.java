package jpastudy.datajpa.repo;

import jpastudy.datajpa.domain.Member;
import lombok.RequiredArgsConstructor;

import java.util.List;
import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class MemberCustomRepoImpl implements MemberCustomRepo {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
            .getResultList();
    }
}