package towntalk.mapper;

import towntalk.model.Member;

public interface MemberDao {
    Member getMember(Member member);

    Member getMemberFromPassword(Member member);

    int createMember(Member member);

    int editMember(Member member);

    int removeMember(Member member);

    int countMember(Member member);
}
