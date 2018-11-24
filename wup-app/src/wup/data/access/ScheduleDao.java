package wup.data.access;

import java.util.Date;
import java.util.List;

import wup.data.Planner;
import wup.data.Schedule;

/**
 * 일정 정보 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface ScheduleDao {
    /**
     * 주어진 <code>id</code>에 해당하는 일정을 가져옵니다.
     *
     * @param id 일정의 일련번호
     */
    public DaoResult<Schedule> getSchedule(int id);

    /**
     * 주어진 <code>planner</code>에 등록된 모든 일정의 리스트를 가져옵니다.
     *
     * @param planner 일정을 조회할 플래너
     */
    public DaoResult<List<Schedule>> getSchedules(Planner planner);

    /**
     * 주어진 <code>planner</code>에 등록된 일정 중 지정된 기간 내에 있는 일정의 리스트를 가져옵니다.
     *
     * @param planner 일정을 조회할 플래너
     * @param from    시작 기간
     * @param to      종료 기간
     */
    public DaoResult<List<Schedule>> getSchedules(Planner planner, Date from, Date to);

    /**
     * 주어진 <code>planner</code>에 새로운 일정을 추가합니다.
     *
     * @param planner  일정을 추가할 플래너
     * @param schedule 추가할 일정의 정보
     */
    public DaoResult<Schedule> createSchedule(Planner planner, Schedule schedule);

    /**
     * 주어진 <code>id</code>에 해당하는 일정의 정보를 변경합니다.
     *
     * @param id       변경할 일정의 일련번호
     * @param schedule 변경할 정보가 들어 있는 {@link Schedule} 개체
     */
    public DaoResult<Schedule> updateSchedule(int id, Schedule schedule);

    /**
     * 주어진 <code>id</code>에 해당하는 일정을 삭제합니다.
     *
     * @param id 삭제할 일정의 일련번호
     */
    public DaoResult<Boolean> deleteSchedule(int id);
}
