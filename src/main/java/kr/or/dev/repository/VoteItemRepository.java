package kr.or.dev.repository;

import kr.or.dev.entity.timeline.vote_item.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
}
