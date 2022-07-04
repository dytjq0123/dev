package kr.or.dev.repository;

import kr.or.dev.entity.timeline.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s left join fetch s.member m where m.mem_no = :mem_no and s.schd_alarm = :schd_alarm and s.schd_del_chk = 'n'")
    List<Schedule> findByCurrentTime(@Param("mem_no") Long mem_no, @Param("schd_alarm") String schd_alarm);

    @Query("select s from Schedule s left join fetch s.member m left join fetch s.project p where m.mem_no = :mem_no and p.pro_no = :pro_no")
    List<Schedule> findByMemNoAndProNo(@Param("mem_no") Long mem_no, @Param("pro_no") Long pro_no);

}
