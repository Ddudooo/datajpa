package jpastudy.datajpa.repo;

import jpastudy.datajpa.domain.Member;

import java.util.List;

public interface MemberCustomRepo {

    List<Member> findMemberCustom();
}
